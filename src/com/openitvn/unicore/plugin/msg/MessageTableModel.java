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

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Thinh Pham
 */
public class MessageTableModel extends AbstractTableModel {
    private final String[] COLUMNS = {"Line", "Original", "Translation", "H", "S", "A"};
    public static final int COL_LINE = 0;
    public static final int COL_ORIGINAL = 1;
    public static final int COL_TRANSLATION = 2;
    public static final int COL_HIDDEN = 3;
    public static final int COL_SKIP = 4;
    public static final int COL_APPROVED = 5;
    
    private final ArrayList<MessageTableEntry> entries = new ArrayList<>();

    @Override
    public int getRowCount() {
        return entries.size();
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
        switch(col) {
            case COL_LINE:
                return Integer.class;
                
            case COL_ORIGINAL:
            case COL_TRANSLATION:
                return String.class;
                
            case COL_HIDDEN:
            case COL_SKIP:
            case COL_APPROVED:
                return Boolean.class;
        }
        return Object.class;
    }

    @Override
    public Object getValueAt(int row, int col) {
        MessageTableEntry e = entries.get(row);
        switch (col) {
            case COL_LINE:
                return e.getLine();
                
            case COL_ORIGINAL:
                return e.getOriginal();
                
            case COL_TRANSLATION:
                return e.getTranslation();
                
            case COL_HIDDEN:
                return e.isHidden();
                
            case COL_SKIP:
                return e.isSkip();
                
            case COL_APPROVED:
                return e.isApproved();
        }
        return null;
    }
    
    public void addEntry(MessageTableEntry entry) {
        entries.add(entry);
    }
    
    public MessageTableEntry[] entries() {
        return entries.toArray(new MessageTableEntry[entries.size()]);
    }
    
    public MessageTableEntry getEntry(int lineId) {
        return entries.get(lineId);
    }
    
    public void unbind() {
        entries.clear();
        fireTableDataChanged();
    }
}
