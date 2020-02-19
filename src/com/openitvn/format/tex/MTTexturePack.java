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
package com.openitvn.format.tex;

import com.openitvn.unicore.data.DataStream;
import com.openitvn.maintain.DumpEntry;
import com.openitvn.unicore.world.resource.ITexture;
import com.openitvn.unicore.world.resource.ITexturePack;
import java.util.Collection;

/**
 *
 * @author Thinh Pham
 */
public class MTTexturePack extends ITexturePack {
        
    @Override
    public void fromSource(ITexturePack src) {
        try {
            MTTexture tex = new MTTexture(false);
            tex.replace(src.textures.get(0));
            textures.add(tex);
        } catch (IndexOutOfBoundsException ex) {
            throw new UnsupportedOperationException("Can't copy from empty source file.");
        }
    }
    
    @Override
    public void decode(DataStream ds) {
        textures.add(new MTTexture(ds));
    }
    
    @Override
    public byte[] encode() {
        ITexture origin = textures.get(0);
        ITexture patch = patchMap.get(0);
        return origin.compilePatch(patch);
    }
    
    @Override
    public Collection<DumpEntry> dump(DataStream ds) {
        addDump(DumpEntry.FOURCC, "magic", 0);
        addDump(DumpEntry.UBYTE, "version", 4);
        addDump(DumpEntry.UBYTE, "variant", 5);
        addDump(DumpEntry.USHORT, "revision", 6);
        short version = ds.getShort(4);
        switch (version) {
            case 112: // RE5
                addDump(DumpEntry.UBYTE, "numMips", 8);
                addDump(DumpEntry.UBYTE, "numFaces", 9);
                addDump(DumpEntry.USHORT, "width", 12);
                addDump(DumpEntry.USHORT, "height", 14);
                addDump(DumpEntry.UINT, "format", 20);
                addDump(DumpEntry.FLOAT, "preR", 24);
                addDump(DumpEntry.FLOAT, "preG", 28);
                addDump(DumpEntry.FLOAT, "preB", 32);
                addDump(DumpEntry.FLOAT, "preA", 36);
                short numMips = ds.getUByte(8);
                short numFaces = ds.getUByte(9);
                int k = 40;
                for (int i = 0; i < numFaces; i++) {
                    for (int j = 0; j < numMips; j++) {
                        String name = String.format("offset[%1$d][%2$d]", i, j);
                        addDump(DumpEntry.UINT, name, k);
                        k += 4;
                    }
                }
                break;
        }
        return super.dump(ds);
    }
}
