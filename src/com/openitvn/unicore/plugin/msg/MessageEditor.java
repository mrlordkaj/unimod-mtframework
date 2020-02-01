/*
 * Copyright (C) 2017 Thinh Pham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.openitvn.unicore.plugin.msg;

import com.openitvn.control.table.BooleanCellRenderer;
import com.openitvn.unicore.Unicore;
import com.openitvn.unicore.data.DataStream;
import com.openitvn.unicore.data.FileStream;
import com.openitvn.util.FileHelper;
import com.openitvn.unicore.data.EntryStream;
import com.openitvn.unicore.plugin.FileViewer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.RowFilter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author Thinh Pham
 */
public class MessageEditor extends FileViewer {
    
    private static final String WORK_DIR = Unicore.workDir + "/MTFramework/MessageEditor";
    
    private MessageTableModel messageTableModel;
    private MessageTableEntry selectedMessage = null;
    private MTMessageSnapshot snapshot;
    
    public MessageEditor() {
        initComponents();
        initMessageTable();
        super.setName("Message Editor");
    }
    
    @Override
    public void openStream(DataStream ds) {
        //load backup if exist, igrone all errors
        boolean useSnap = false;
        String snapName = getSnapshotName();
        if (snapName != null) {
            try (FileStream savStream = new FileStream(new File(snapName))) {
                snapshot.readData(savStream);
                useSnap = true;
                btnSave.setEnabled(true);
            } catch (IOException | UnsupportedOperationException ex) { }
        }
        //else load file content
        if (!useSnap)
            snapshot.setOriginalData(ds);
        //update form
        messageTableModel.fireTableDataChanged();
        refineMessageTable();
        messageTable.scrollRectToVisible(new Rectangle());
        btnClose.setEnabled(true);
        txtFile.setText(ds.getFullPath());
    }
    
    @Override
    public boolean closeStream() {
        if (btnSnapshot.isEnabled()) {
            int rs = JOptionPane.showConfirmDialog(this,
                    "You made some changes on this file.\nWant take a snapshot before close?",
                    "Take Snapshot", JOptionPane.YES_NO_CANCEL_OPTION);
            if (rs == JOptionPane.CANCEL_OPTION)
                return false;
            if (rs == JOptionPane.YES_OPTION)
                takeSnapshot();
        }
        // reset form
        btnClose.setEnabled(false);
        btnSave.setEnabled(false);
        btnSnapshot.setEnabled(false);
        docOriginal.setText("");
        docTranslation.setText("");
        txtFile.setText("");
        messageTableModel.unbind();
        selectedMessage = null;
        return true;
    }
    
    private void initMessageTable() {
        //setup the table
        messageTableModel = (MessageTableModel)messageTable.getModel();
        snapshot = new MTMessageSnapshot(messageTableModel);
        
        //fixed column sizes
        TableColumnModel tcm = messageTable.getColumnModel();
        tcm.getColumn(MessageTableModel.COL_LINE).setMinWidth(40);
        tcm.getColumn(MessageTableModel.COL_LINE).setMaxWidth(40);
        tcm.getColumn(MessageTableModel.COL_HIDDEN).setMinWidth(22);
        tcm.getColumn(MessageTableModel.COL_HIDDEN).setMaxWidth(22);
        tcm.getColumn(MessageTableModel.COL_SKIP).setMinWidth(22);
        tcm.getColumn(MessageTableModel.COL_SKIP).setMaxWidth(22);
        tcm.getColumn(MessageTableModel.COL_APPROVED).setMinWidth(22);
        tcm.getColumn(MessageTableModel.COL_APPROVED).setMaxWidth(22);
        
        //custom background color for rows
        DefaultTableCellRenderer rowRenderer = new DefaultTableCellRenderer(){
            private final Color skipColor = new Color(240,180,160);
            private final Color approvedColor = new Color(150,230,200);
            
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                int msgId = table.convertRowIndexToModel(row);
                MessageTableEntry msg = messageTableModel.getEntry(msgId);
                if (!isSelected) {
                    if (msg.isSkip()) {
                        this.setBackground(skipColor);
                    } else if(msg.isApproved()) {
                        this.setBackground(approvedColor);
                    } else {
                        this.setBackground(table.getBackground());
                    }
                }
                return this;
            }   
        };
        messageTable.setDefaultRenderer(Integer.class, rowRenderer);
        messageTable.setDefaultRenderer(String.class, rowRenderer);
        messageTable.setDefaultRenderer(Boolean.class, new BooleanCellRenderer());
        
        //event when message table change selection
        messageTable.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            int sel = messageTable.getSelectedRow();
            if (sel >= 0) {
                int messageId = messageTable.convertRowIndexToModel(sel);
                selectedMessage = messageTableModel.getEntry(messageId);
                String original = selectedMessage.getOriginal();
                String translation = selectedMessage.getTranslation();
                if(translation.equals("")) translation = original;
                makeUpText(original, docOriginal);
                makeUpText(translation, docTranslation);
            }
        });
    }
    
    private void refineMessageTable() {
        ArrayList<RowFilter<MessageTableModel,Integer>> masterFilter = new ArrayList<>();
        
        ArrayList<RowFilter<MessageTableModel,Integer>> strFilter = new ArrayList<>();
        String regex = txtSearch.getSearchRegex();
        strFilter.add(RowFilter.regexFilter(regex, MessageTableModel.COL_ORIGINAL));
        strFilter.add(RowFilter.regexFilter(regex, MessageTableModel.COL_TRANSLATION));
        masterFilter.add(RowFilter.orFilter(strFilter));
        
        if (!btnHidden.isSelected()) {
            RowFilter<MessageTableModel, Integer> hiddenFilter = new RowFilter<MessageTableModel, Integer>() {
                @Override
                public boolean include(RowFilter.Entry<? extends MessageTableModel, ? extends Integer> entry) {
                    int modelRow = entry.getIdentifier();
                    boolean isHidden = (Boolean)entry.getModel().getValueAt(modelRow, MessageTableModel.COL_HIDDEN);
                    return !isHidden;
                }
            };
            masterFilter.add(hiddenFilter);
        }
        
        TableRowSorter<MessageTableModel> sorter = (TableRowSorter) messageTable.getRowSorter();
        sorter.setRowFilter(RowFilter.andFilter(masterFilter));
    }
    
    private MessageTableEntry[] getSelectedMessages() {
        MessageTableEntry[] rs;
        int[] selectedRows = messageTable.getSelectedRows();
        rs = new MessageTableEntry[selectedRows.length];
        for (int i = 0; i < rs.length; i++) {
            int curMessageId = messageTable.convertRowIndexToModel(selectedRows[i]);
            rs[i] = messageTableModel.getEntry(curMessageId);
        }
        return rs;
    }
    
    private void makeUpText(String source, JTextArea target) {
        source = source.replaceAll("<break>", "\n");
        target.setText(source);
        target.setCaretPosition(0);
    }
    
    private String makeDownText(JTextArea target) {
        String source = target.getText();
        source = source.replaceAll("\n", "<break>");
        return source;
    }
    
    private String getSnapshotName() {
        String name = FileHelper.getFileName(stream.getFullPath());
        if (stream instanceof EntryStream) {
            String arcName = ((EntryStream)stream).getArchive().getFile().getName();
            return String.format("%1$s/[%2$s][%3$s].sav", WORK_DIR, arcName, name);
        } else if (stream instanceof FileStream) {
            return String.format("%1$s/[%2$s].sav", WORK_DIR, name);
        }
        return null;
    }
    
    private void takeSnapshot() {
        String snapName = getSnapshotName();
        if (snapName != null) {
            try (FileOutputStream fos = new FileOutputStream(new File(snapName))) {
                fos.write(snapshot.toData());
                btnSnapshot.setEnabled(false);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Writing Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mnuMessageTable = new javax.swing.JPopupMenu();
        mnuApproved = new javax.swing.JMenuItem();
        mnuDisapproved = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JPopupMenu.Separator();
        mnuExclude = new javax.swing.JMenuItem();
        mnuInclude = new javax.swing.JMenuItem();
        jSeparator9 = new javax.swing.JPopupMenu.Separator();
        mnuHideMessage = new javax.swing.JMenuItem();
        mnuUnhideMessage = new javax.swing.JMenuItem();
        jSeparator10 = new javax.swing.JPopupMenu.Separator();
        mnuClearTranslated = new javax.swing.JMenuItem();
        mainToolbar = new javax.swing.JToolBar();
        btnOpen = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnExport = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnSnapshot = new javax.swing.JButton();
        btnCharmap = new javax.swing.JButton();
        btnHidden = new javax.swing.JToggleButton();
        jSeparator7 = new javax.swing.JToolBar.Separator();
        txtSearch = new com.openitvn.control.KTextField();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        btnAbout = new javax.swing.JButton();
        mainSplitter = new javax.swing.JSplitPane();
        translateSplitter = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        docOriginal = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        docTranslation = new javax.swing.JTextArea();
        messageTableScroller = new javax.swing.JScrollPane();
        messageTable = new javax.swing.JTable();
        txtFile = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();

        mnuApproved.setText("Approve Translations");
        mnuApproved.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuApprovedActionPerformed(evt);
            }
        });
        mnuMessageTable.add(mnuApproved);

        mnuDisapproved.setText("Disapprove Translations");
        mnuDisapproved.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDisapprovedActionPerformed(evt);
            }
        });
        mnuMessageTable.add(mnuDisapproved);
        mnuMessageTable.add(jSeparator8);

        mnuExclude.setText("Exclude Messages");
        mnuExclude.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuExcludeActionPerformed(evt);
            }
        });
        mnuMessageTable.add(mnuExclude);

        mnuInclude.setText("Include Messages");
        mnuInclude.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuIncludeActionPerformed(evt);
            }
        });
        mnuMessageTable.add(mnuInclude);
        mnuMessageTable.add(jSeparator9);

        mnuHideMessage.setText("Hide Messages");
        mnuHideMessage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuHideMessageActionPerformed(evt);
            }
        });
        mnuMessageTable.add(mnuHideMessage);

        mnuUnhideMessage.setText("Unhide Messages");
        mnuUnhideMessage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuUnhideMessageActionPerformed(evt);
            }
        });
        mnuMessageTable.add(mnuUnhideMessage);
        mnuMessageTable.add(jSeparator10);

        mnuClearTranslated.setText("Clear Translations");
        mnuClearTranslated.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuClearTranslatedActionPerformed(evt);
            }
        });
        mnuMessageTable.add(mnuClearTranslated);

        setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 6, 6, 6));

        mainToolbar.setBorder(null);
        mainToolbar.setFloatable(false);
        mainToolbar.setRollover(true);

        btnOpen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icon24/toolbar_open.png"))); // NOI18N
        btnOpen.setToolTipText("Open");
        btnOpen.setFocusable(false);
        btnOpen.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        btnOpen.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnOpen.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnOpen.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mainToolbar.add(btnOpen);

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icon24/toolbar_close.png"))); // NOI18N
        btnClose.setToolTipText("Close");
        btnClose.setEnabled(false);
        btnClose.setFocusable(false);
        btnClose.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        btnClose.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnClose.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnClose.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        mainToolbar.add(btnClose);

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icon24/toolbar_save.png"))); // NOI18N
        btnSave.setToolTipText("Save");
        btnSave.setEnabled(false);
        btnSave.setFocusable(false);
        btnSave.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        btnSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSave.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        mainToolbar.add(btnSave);

        btnExport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icon24/toolbar_export.png"))); // NOI18N
        btnExport.setToolTipText("Export");
        btnExport.setEnabled(false);
        btnExport.setFocusable(false);
        btnExport.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        btnExport.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnExport.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnExport.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mainToolbar.add(btnExport);

        jSeparator1.setSeparatorSize(new java.awt.Dimension(10, 30));
        mainToolbar.add(jSeparator1);

        btnSnapshot.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icon24/toolbar_snapshot.png"))); // NOI18N
        btnSnapshot.setToolTipText("Take Snapshot");
        btnSnapshot.setEnabled(false);
        btnSnapshot.setFocusable(false);
        btnSnapshot.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        btnSnapshot.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSnapshot.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnSnapshot.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSnapshot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSnapshotActionPerformed(evt);
            }
        });
        mainToolbar.add(btnSnapshot);

        btnCharmap.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icon24/toolbar_charmap.png"))); // NOI18N
        btnCharmap.setToolTipText("Charmap Editor");
        btnCharmap.setFocusable(false);
        btnCharmap.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        btnCharmap.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCharmap.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnCharmap.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCharmap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCharmapActionPerformed(evt);
            }
        });
        mainToolbar.add(btnCharmap);

        btnHidden.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icon24/toolbar_hidden.png"))); // NOI18N
        btnHidden.setToolTipText("Show/Hide Hidden Lines");
        btnHidden.setFocusable(false);
        btnHidden.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        btnHidden.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnHidden.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnHidden.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnHidden.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHiddenActionPerformed(evt);
            }
        });
        mainToolbar.add(btnHidden);

        jSeparator7.setSeparatorSize(new java.awt.Dimension(10, 30));
        mainToolbar.add(jSeparator7);

        txtSearch.setMaximumSize(new java.awt.Dimension(200, 26));
        txtSearch.setPrompt("search here");
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSearchKeyReleased(evt);
            }
        });
        mainToolbar.add(txtSearch);

        jSeparator3.setSeparatorSize(new java.awt.Dimension(10, 30));
        mainToolbar.add(jSeparator3);

        btnAbout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icon24/toolbar_about.png"))); // NOI18N
        btnAbout.setToolTipText("About Message Viewer");
        btnAbout.setFocusable(false);
        btnAbout.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        btnAbout.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAbout.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnAbout.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAboutActionPerformed(evt);
            }
        });
        mainToolbar.add(btnAbout);

        mainSplitter.setBorder(null);
        mainSplitter.setDividerSize(6);
        mainSplitter.setResizeWeight(0.5);

        translateSplitter.setBorder(null);
        translateSplitter.setDividerSize(6);
        translateSplitter.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        translateSplitter.setResizeWeight(0.5);
        translateSplitter.setMinimumSize(new java.awt.Dimension(240, 52));
        translateSplitter.setPreferredSize(new java.awt.Dimension(240, 198));

        docOriginal.setEditable(false);
        docOriginal.setColumns(20);
        docOriginal.setRows(5);
        docOriginal.setToolTipText("Original Text");
        docOriginal.setOpaque(false);
        jScrollPane1.setViewportView(docOriginal);

        translateSplitter.setLeftComponent(jScrollPane1);

        docTranslation.setColumns(20);
        docTranslation.setRows(5);
        docTranslation.setToolTipText("Translation Text");
        docTranslation.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                docTranslationKeyReleased(evt);
            }
        });
        jScrollPane2.setViewportView(docTranslation);

        translateSplitter.setRightComponent(jScrollPane2);

        mainSplitter.setRightComponent(translateSplitter);

        messageTableScroller.setMinimumSize(new java.awt.Dimension(240, 23));
        messageTableScroller.setPreferredSize(new java.awt.Dimension(240, 402));

        messageTable.setAutoCreateRowSorter(true);
        messageTable.setModel(new MessageTableModel());
        messageTable.setComponentPopupMenu(mnuMessageTable);
        messageTable.setGridColor(new java.awt.Color(204, 204, 204));
        messageTable.setRowHeight(20);
        messageTable.getTableHeader().setReorderingAllowed(false);
        messageTableScroller.setViewportView(messageTable);

        mainSplitter.setLeftComponent(messageTableScroller);

        txtFile.setEditable(false);
        txtFile.setBackground(new java.awt.Color(255, 255, 255));
        txtFile.setMaximumSize(new java.awt.Dimension(32767, 20));

        jLabel1.setLabelFor(txtFile);
        jLabel1.setText("File:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainToolbar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(mainSplitter, javax.swing.GroupLayout.DEFAULT_SIZE, 488, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtFile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(mainToolbar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mainSplitter, javax.swing.GroupLayout.DEFAULT_SIZE, 396, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnHiddenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHiddenActionPerformed
        refineMessageTable();
    }//GEN-LAST:event_btnHiddenActionPerformed

    private void mnuApprovedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuApprovedActionPerformed
        for(MessageTableEntry curMsg : getSelectedMessages())
            curMsg.setApproved(true);
        messageTable.repaint();
        btnSave.setEnabled(true);
        btnSnapshot.setEnabled(true);
    }//GEN-LAST:event_mnuApprovedActionPerformed

    private void mnuDisapprovedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDisapprovedActionPerformed
        for(MessageTableEntry curMsg : getSelectedMessages())
            curMsg.setApproved(false);
        messageTable.repaint();
        btnSave.setEnabled(true);
        btnSnapshot.setEnabled(true);
    }//GEN-LAST:event_mnuDisapprovedActionPerformed

    private void mnuExcludeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExcludeActionPerformed
        for (MessageTableEntry curMsg : getSelectedMessages())
            curMsg.setSkip(true);
        messageTable.repaint();
        btnSave.setEnabled(true);
        btnSnapshot.setEnabled(true);
    }//GEN-LAST:event_mnuExcludeActionPerformed

    private void mnuIncludeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuIncludeActionPerformed
        for (MessageTableEntry curMsg : getSelectedMessages())
            curMsg.setSkip(false);
        messageTable.repaint();
        btnSave.setEnabled(true);
        btnSnapshot.setEnabled(true);
    }//GEN-LAST:event_mnuIncludeActionPerformed

    private void mnuHideMessageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuHideMessageActionPerformed
        for (MessageTableEntry curMsg : getSelectedMessages())
            curMsg.setHidden(true);
        refineMessageTable();
        btnSnapshot.setEnabled(true);
    }//GEN-LAST:event_mnuHideMessageActionPerformed

    private void mnuUnhideMessageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuUnhideMessageActionPerformed
        for (MessageTableEntry curMsg : getSelectedMessages())
            curMsg.setHidden(false);
        refineMessageTable();
        btnSnapshot.setEnabled(true);
    }//GEN-LAST:event_mnuUnhideMessageActionPerformed

    private void mnuClearTranslatedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuClearTranslatedActionPerformed
        for (MessageTableEntry curMsg : getSelectedMessages())
            curMsg.setTranslation("");
        messageTable.repaint();
        btnSave.setEnabled(true);
        btnSnapshot.setEnabled(true);
    }//GEN-LAST:event_mnuClearTranslatedActionPerformed

    private void docTranslationKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_docTranslationKeyReleased
        if (selectedMessage != null) {
            selectedMessage.setTranslation(makeDownText(docTranslation));
            btnSave.setEnabled(true);
            btnSnapshot.setEnabled(true);
            messageTable.repaint();
        }
    }//GEN-LAST:event_docTranslationKeyReleased

    private void btnCharmapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCharmapActionPerformed
        new CharmapEditor().setVisible(true);
    }//GEN-LAST:event_btnCharmapActionPerformed

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        requestClose();
    }//GEN-LAST:event_btnCloseActionPerformed

    private void btnSnapshotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSnapshotActionPerformed
        takeSnapshot();
    }//GEN-LAST:event_btnSnapshotActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        if (btnSnapshot.isEnabled())
            takeSnapshot();
        try {
            stream.replace(snapshot.compile());
            stream.save(requestBackup());
            btnSave.setEnabled(false);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Writing Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAboutActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnAboutActionPerformed

    private void txtSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyReleased
        refineMessageTable();
    }//GEN-LAST:event_txtSearchKeyReleased
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAbout;
    private javax.swing.JButton btnCharmap;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnExport;
    private javax.swing.JToggleButton btnHidden;
    private javax.swing.JButton btnOpen;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSnapshot;
    private javax.swing.JTextArea docOriginal;
    private javax.swing.JTextArea docTranslation;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator10;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator7;
    private javax.swing.JPopupMenu.Separator jSeparator8;
    private javax.swing.JPopupMenu.Separator jSeparator9;
    private javax.swing.JSplitPane mainSplitter;
    private javax.swing.JToolBar mainToolbar;
    private javax.swing.JTable messageTable;
    private javax.swing.JScrollPane messageTableScroller;
    private javax.swing.JMenuItem mnuApproved;
    private javax.swing.JMenuItem mnuClearTranslated;
    private javax.swing.JMenuItem mnuDisapproved;
    private javax.swing.JMenuItem mnuExclude;
    private javax.swing.JMenuItem mnuHideMessage;
    private javax.swing.JMenuItem mnuInclude;
    private javax.swing.JPopupMenu mnuMessageTable;
    private javax.swing.JMenuItem mnuUnhideMessage;
    private javax.swing.JSplitPane translateSplitter;
    private javax.swing.JTextField txtFile;
    private com.openitvn.control.KTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
