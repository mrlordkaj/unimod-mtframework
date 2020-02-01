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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author Thinh Pham
 */
public class CharmapEditor extends javax.swing.JDialog {

    public CharmapEditor() {
        super.setModal(true);
        initComponents();
        openText();
        initTextArea();
    }
    
    private void initTextArea() {
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                btnSave.setEnabled(true);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                btnSave.setEnabled(true);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                btnSave.setEnabled(true);
            }
        });
    }
    
    private void openText() {
        //open external charmap.tbl from default directory if exist
        //or else open from internal
        try {
            File file = new File(MTCharmap.EXTERNAL_CHARMAP);
            InputStream is;
            if (file.exists()) {
                is = new FileInputStream(file);
                txtFile.setText(file.getAbsolutePath());
            } else {
                is = getClass().getResourceAsStream(MTCharmap.INTERNAL_CHARMAP);
            }
            InputStreamReader isr = new InputStreamReader(is);
            textArea.read(isr, null);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Reading Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean saveText() {
        try {
            File file = new File(MTCharmap.EXTERNAL_CHARMAP);
            if(!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            FileOutputStream os = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(os);
            textArea.write(osw);
            os.close();
            txtFile.setText(file.getAbsolutePath());
            btnSave.setEnabled(false);
            return true;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Writing Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtFile = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        textArea = new javax.swing.JTextArea();
        btnSave = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Charmap Editor");
        setLocationByPlatform(true);
        setMinimumSize(new java.awt.Dimension(240, 240));

        txtFile.setEditable(false);
        txtFile.setForeground(new java.awt.Color(102, 102, 102));
        txtFile.setText("<Integrated Character Map>");
        txtFile.setBorder(null);

        textArea.setColumns(20);
        textArea.setRows(5);
        jScrollPane1.setViewportView(textArea);

        btnSave.setText("OK");
        btnSave.setEnabled(false);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtFile, javax.swing.GroupLayout.DEFAULT_SIZE, 567, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSave)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSave)
                    .addComponent(txtFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        if(saveText()) {
            JOptionPane.showMessageDialog(this, "You may need reload opening file to apply changes.", "Charmap Saved", JOptionPane.INFORMATION_MESSAGE);
            MTCharmap.getInstance().loadCharmapFile();
            dispose();
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSave;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea textArea;
    private javax.swing.JTextField txtFile;
    // End of variables declaration//GEN-END:variables
}
