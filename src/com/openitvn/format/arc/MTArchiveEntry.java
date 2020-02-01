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
package com.openitvn.format.arc;

import com.openitvn.unicore.archive.IArchiveEntry;

/**
 *
 * @author Thinh Pham
 */
public class MTArchiveEntry extends IArchiveEntry {
    
    private final String realPath;
    private final int hash;
    private final byte flags;
    
    public MTArchiveEntry(MTArchive arc, String path, int hash, int packed, int size, byte flags, int offset) {
        super(arc, path+"."+MTArchiveExtension.getString(hash), size, offset, packed);
        this.realPath = path;
        this.hash = hash;
        this.flags = flags;
    }
    
    protected String getPathWithoutExt() {
        return realPath;
    }
    
    protected int getHash() {
        return hash;
    }
    
    protected byte getFlags() {
        return flags;
    }
}
