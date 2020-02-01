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

/**
 *
 * @author Thinh Pham
 */
public class MessageTableEntry {
    private final int line;
    private final String original;
    private String translation;
    private boolean hidden = false;
    private boolean skip = false;
    private boolean approved = false;
    
    public MessageTableEntry(int line, String english, String vietnamese) {
        this.line = line;
        this.original = english;
        this.translation = vietnamese;
    }
    
    public int getLine() {
        return line;
    }
    
    public String getOriginal() {
        return original;
    }
    
    public String getTranslation() {
        return translation;
    }
    
    public void setTranslation(String value) {
        translation = value;
    }
    
    public String getMessageForCompile() {
        return (skip || translation.equals("")) ? original : translation;
    }
    
    public boolean isHidden() {
        return hidden;
    }
    
    public void setHidden(boolean value) {
        hidden = value;
    }
    
    public boolean isSkip() {
        return skip;
    }
    
    public void setSkip(boolean value) {
        skip = value;
        if(skip) approved = false;
    }
    
    public boolean isApproved() {
        return approved;
    }
    
    public void setApproved(boolean value) {
        approved = value;
        if(approved) skip = false;
    }
}
