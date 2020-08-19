/*
 * Copyright (C) 2019 Thinh Pham
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

import com.openitvn.unicore.Workspace;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Thinh Pham
 */
class REStageModel extends AbstractTableModel {
    
    static final String[] COLUMNS = { "Name", "Int.", "Ext." };
    static final int COL_NAME = 0;
    static final int COL_COUNT_INT = 1;
    static final int COL_COUNT_EXT = 2;
    
    ArrayList<REStageEntity> entries = new ArrayList<>();
    
    void bind(Workspace space) {
        entries.clear();
        if ("re5".equals(space.name)) {
            String arcPath = "/nativePC_MT/Image/Archive";
            File arcDir = new File(space.location+arcPath);
            String regex = "^s([0-9]+).arc$";
            for (File arc : arcDir.listFiles()) {
                String name = arc.getName();
                if (name.matches(regex)) {
                    try {
                        entries.add(new REStageEntity(space.location, arc));
                    } catch (IOException ex) { }
                }
            }
        }
        fireTableDataChanged();
    }
    
    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }
    
    @Override
    public String getColumnName(int col) {
        return COLUMNS[col];
    }
    
    @Override
    public Class getColumnClass(int col) {
        switch (col) {
            case COL_NAME:
                return String.class;
                
            case COL_COUNT_INT:
            case COL_COUNT_EXT:
                return Integer.class;
        }
        return Object.class;
    }

    @Override
    public int getRowCount() {
        return entries.size();
    }
    
    @Override
    public Object getValueAt(int row, int col) {
        switch (col) {
            case COL_NAME:
                return entries.get(row);
                
            case COL_COUNT_INT:
                return entries.get(row).getInternalCount();
                
            case COL_COUNT_EXT:
                return entries.get(row).getExternalCount();
        }
        return null;
    }
}
