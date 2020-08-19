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
package net.unimod.mtframework;

import com.openitvn.format.arc.MTArchiveEntry;
import com.openitvn.format.mod.MTModel;
import com.openitvn.unicore.Unicore;
import com.openitvn.unicore.Workspace;
import com.openitvn.unicore.data.EntryStream;
import com.openitvn.unicore.plugin.PanelViewer;
import com.openitvn.unicore.world.IWorld;
import java.io.IOException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author Thinh Pham
 */
public final class REStage extends PanelViewer {
    
    final REStageModel sceneModel = new REStageModel();
    IWorld world;
    
    public REStage() {
        initComponents();
        
        world = new IWorld("RE Stage");
        Unicore.registerWorld(world);
        
        TableColumnModel cm = sceneTable.getColumnModel();
        cm.getColumn(REStageModel.COL_COUNT_INT).setMinWidth(40);
        cm.getColumn(REStageModel.COL_COUNT_INT).setMaxWidth(40);
        cm.getColumn(REStageModel.COL_COUNT_EXT).setMinWidth(40);
        cm.getColumn(REStageModel.COL_COUNT_EXT).setMaxWidth(40);
        
        sceneTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent evt) {
                if (!evt.getValueIsAdjusting()) {
                    int row = sceneTable.getSelectedRow();
                    if (row >= 0) {
                        int id = sceneTable.convertRowIndexToModel(row);
                        REStageEntity entity = sceneModel.entries.get(id);
                        System.out.println(entity.arc);
                        for (MTArchiveEntry entry : entity.ints) {
                            try (EntryStream ds = new EntryStream(entry)) {
                                MTModel model = new MTModel();
                                model.fromData(ds);
                                world.resource.register(model.resource.getTextures());
                                world.resource.register(model.resource.getMaterials());
                                world.resource.register(model.resource.getModels());
                                Unicore.focusToWorld(world);
                            } catch (IOException ex) {
                                ex.printStackTrace(System.err);
                            }
                        }
                    }
                }
            }
        });
    }
    
    @Override
    public void workspaceChanged(Workspace space) {
        sceneModel.bind(space);
    }
    
    @Override
    public boolean requestClose() {
        return true;
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
        sceneTable = new javax.swing.JTable();

        setName("RE Stage"); // NOI18N

        sceneTable.setAutoCreateRowSorter(true);
        sceneTable.setModel(sceneModel);
        sceneTable.setRowHeight(20);
        sceneTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        sceneTable.setShowHorizontalLines(false);
        sceneTable.setShowVerticalLines(false);
        sceneTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(sceneTable);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable sceneTable;
    // End of variables declaration//GEN-END:variables
}
