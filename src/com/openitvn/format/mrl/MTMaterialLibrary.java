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
package com.openitvn.format.mrl;

import com.openitvn.unicore.data.DataStream;
import com.openitvn.format.mod.DependencyHelper;
import com.openitvn.format.tex.MTTexture;
import com.openitvn.maintain.DumpEntry;
import com.openitvn.unicore.world.resource.IMaterialPack;
import com.openitvn.util.StringHelper;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 *
 * @author Thinh Pham
 */
public class MTMaterialLibrary extends IMaterialPack<MTMaterial, MTTexture> {
    
    public static final int FOURCC = StringHelper.makeFourCC("MRL\0");
    
    @Override
    public void decode(DataStream ds) {
        if (ds.getInt() == FOURCC) {
            // header
            int version = ds.getInt();
            int numMaterials = ds.getInt();
            int numTextures = ds.getInt();
            int unk1 = ds.getInt();
            int textureOffset = ds.getInt();
            int materialOffset = ds.getInt();
            
            // textures
            for (int i = 0; i < numTextures; i++) {
                ds.position(textureOffset + i * 76 + 12);
                String path = ds.readFixedString(64);
                textures.add(DependencyHelper.requestTEX(ds, path));
            }
            
            // materials
            HashMap<String, Integer> countMap = new HashMap<>();
            HashMap<String, MTMaterial> matMap = new HashMap<>();
            for (int i = 0; i < numMaterials; i++) {
                ds.position(materialOffset + i * 60);
                // material header
                MTMaterial mat = new MTMaterial(ds);
                materials.add(mat);
                // material content
                switch (version) {
                    case 32: // RER
                        mat.readBuffer32(ds, textures);
                        break;
                        
                    case 33: // RE6
                        mat.readBuffer33(ds, textures);
                        break;
                        
                    case 34: // RER2
                        mat.readBuffer34(ds, textures);
                        break;
                }
                // create readable name
                String name = mat.normalizeName();
                if (name == null) {
                    String tex = mat.diffuseTexture;
                    Integer count = countMap.get(tex);
                    count = (count == null) ? 1 : count+1;
                    countMap.put(tex, count);
                    matMap.put(tex, mat);
                }
            }
            for (Entry<String, Integer> e : countMap.entrySet()) {
                if (e.getValue() == 1) {
                    System.err.println(matMap.get(e.getKey()));
                }
            }
        } else {
            throw new UnsupportedOperationException("Invalid MRL format");
        }
    }
    
    @Override
    public Collection<DumpEntry> dump(DataStream ds) {
        addDump(DumpEntry.FOURCC, "magic", 0);
        addDump(DumpEntry.INT, "version", 4);
        addDump(DumpEntry.INT, "numMaterials", 8);
        addDump(DumpEntry.INT, "numTextures", 12);
        addDump(DumpEntry.INT, "textureOffset", 20);
        addDump(DumpEntry.INT, "materialOffset", 24);
//        int numTexs = ds.getInt(12);
//        int texOffset = ds.getInt(20);
//        for (int i = 0; i < numTexs; i++) {
//            int k = texOffset + i * 76 + 12; // skip 4 bytes call, 8 bytes padding
//            addDump(DumpEntry.STRING, String.format("texName[%1$d]", i), k, 64);
//        }
        int numMats = ds.getInt(8);
        int matOffset = ds.getInt(24);
        for (int i = 0; i < numMats; i++) {
            int k = matOffset + i * 60;
            addDump(DumpEntry.BINARY, String.format("matHash[%1$d]", i), k += 4, 4);
//            addDump(DumpEntry.INT, String.format("matSize[%1$d]", i), k += 4);
//            addDump(DumpEntry.INT, String.format("matOffset[%1$d]", i), k += 44);
        }
        return super.dump(ds);
    }
}
