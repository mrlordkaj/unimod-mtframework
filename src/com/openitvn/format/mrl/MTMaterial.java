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

import com.openitvn.format.tex.MTTexture;
import com.openitvn.unicore.data.DataStream;
import com.openitvn.unicore.world.resource.IMaterial;
import java.util.ArrayList;

/**
 *
 * @author Thinh Pham
 */
public class MTMaterial extends IMaterial {
    
//    private static final int STRUCT_SIZE = 60;
    
    // header     
    public final int nameHash;      
    public final int bufferSize;
    public final int bufferOffset;
    
    // content
    private int texDifId;
    private int texNorId;
    private int texLgtId;
    private int tex4;
    private int tex5;
    private int tex6;
    private int tex7;
    private int tex8;
    
    public MTMaterial(DataStream ds) {
        // header
        int magic = ds.getInt();    // const e4 eb b0 5f
        nameHash = ds.getInt();     // hash for name, the algorithm is unknown, use hashmap instead
        bufferSize = ds.getInt();
        int unk3 = ds.getInt();
        int unk4 = ds.getInt();     // const 9f 91 13 b8
        int unk5 = ds.getInt();     // const a8 f1 8c 10
        int unk6 = ds.getInt();     // const 20 c0 4d 80
        int unk7 = ds.getInt();
        int unk8 = ds.getInt();     // const 0
        int unk9 = ds.getInt();     // const 0
        int unk10 = ds.getInt();    // const 0
        int unk11 = ds.getInt();    // const 0
        int unk12 = ds.getInt();    // const 0
        bufferOffset = ds.getInt();
        int unk14 = ds.getInt();    // const 0
    }
    
    public String normalizeName() {
        String hashName = MTMaterialHash.getName(nameHash);
        name = (hashName != null) ? hashName :
                String.format("0x%1$08x (%2$s)", nameHash, diffuseTexture);
        return hashName;
    }
    
    public void readBuffer32(DataStream ds, ArrayList<MTTexture> textures) {
        ds.position(bufferOffset);
        long bufferEnd = bufferOffset + bufferSize;
        int cmd, val;
        mainLoop:
        while (ds.position() < bufferEnd) {
            try {
                switch (cmd = ds.getInt()) { // command = ds.getInt();
                    case 0x5a0dcdc0: // term???
                        break mainLoop;

                    case 0x348dcdc3: // NM
                        texNorId = ds.getInt();
                        normalTexture = textures.get(texNorId - 1).getName();
                        break;

                    case 0x34adcdc3: // DM
                        tex4 = ds.getInt();
                        break;

                    case 0x345dcdc3: // BM
                        texDifId = ds.getInt();
                        diffuseTexture = textures.get(texDifId - 1).getName();
                        break;

                    case 0x359dcdc3: // MM
                        texLgtId = ds.getInt();
                        ambientTexture = textures.get(texLgtId - 1).getName();
                        break;

                    case 0x531dcdc0:
                        alphaBlend = (ds.getInt() == 0x8e54a532);
                        break;

                    case 0x527dcdc0:
                        alphaTest = (ds.getInt() == 0x7587aa1f) ? 0.5f : 0;
                        break;

//                    default:
//                        System.out.printf("0x%1$08x\n", cmd);
//                        break;
                }
            } catch (ArrayIndexOutOfBoundsException | NullPointerException ex) {
                // negative textureId
                // texture is missing
            }
        }
    }
    
    public void readBuffer33(DataStream ds, ArrayList<MTTexture> textures) {
        ds.position(bufferOffset);
        long bufferEnd = bufferOffset + bufferSize;
        int cmd, val;
        mainLoop:
        while (ds.position() < bufferEnd) {
            switch (cmd = ds.getInt()) {
                case 0x59adcdc0: // term???
                    break mainLoop;
                    
                case 0x347dcdc3: // BM
                    texDifId = ds.getInt();
                    diffuseTexture = textures.get(texDifId - 1).getName();
                    break;
                    
                case 0x34adcdc3: // NM
                    texNorId = ds.getInt();
                    normalTexture = textures.get(texNorId - 1).getName();
                    break;
                    
                case 0x35adcdc3: // MM
                    texLgtId = ds.getInt();
                    ambientTexture = textures.get(texLgtId - 1).getName();
                    break;
                    
                case 0x58bdcdc0:
                    alphaBlend = (ds.getInt() == 0x8e54a58c);
                    break;
                    
//                default:
//                    System.out.printf("0x%1$08x\n", cmd);
//                    break;
            }
        }
    }
    
    public void readBuffer34(DataStream ds, ArrayList<MTTexture> textures) {
        ds.position(bufferOffset);
        long bufferEnd = bufferOffset + bufferSize;
        int cmd, val;
        mainLoop:
        while (ds.position() < bufferEnd) {
            try {
                switch (cmd = ds.getInt()) {
//                    case 0x59adcdc0: // term???
//                        break mainLoop;
//                        
                    case 0x360dcdc3: // NM RER2
                    case 0x368dcdc3: // NM DMC4
                        texNorId = ds.getInt();
                        normalTexture = textures.get(texNorId - 1).getName();
                        break;
                        
                    case 0x35ddcdc3: // BM RER2
                    case 0x365dcdc3: // BM DMC4
                        texDifId = ds.getInt();
                        diffuseTexture = textures.get(texDifId - 1).getName();
                        break;

                    case 0x371dcdc3: // MM RER2
                    case 0x379dcdc3: // MM DMC4
                        texLgtId = ds.getInt();
                        ambientTexture = textures.get(texLgtId - 1).getName();
                        break;
                        
                    case 0x55adcdc0:
                        alphaBlend = (ds.getInt() == 0x8e54a55b);
                        break;
                        
//                    case 0x35edcdc3: // BM
//                    case 0x37ddcdc3: // 1
//                    case 0x37edcdc3: // 2
//                    case 0x374dcdc3: // 8
//                    case 0x366dcdc3: // 9
//                        val = ds.getInt();
//                        System.out.printf("0x%1$08x - %2$d\n", cmd, val);
//                        break;
                        
//                    case 0x632dcdc0:
//                    case 0x633dcdc0:
//                    case 0x62bdcdc0:
//                    case 0x5c7dcdc0:
//                    case 0x5c8dcdc0:
//                    case 0x5c9dcdc0:
//                    case 0x5cadcdc0:
//                    case 0x56fdcdc0:
//                    case 0x577dcdc0:
//                    case 0x573dcdc0:
//                    case 0x565dcdc0:
//                    case 0x563dcdc0:
//                    case 0x564dcdc0:
//                    case 0x5bcdcdc0:
//                    case 0x551dcdc0:
//                    case 0x54fdcdc0:
//                    case 0x58edcdc0:
//                    case 0x589dcdc0:
//                    case 0x5b6dcdc0:
//                    case 0x5afdcdc0:
//                    case 0x5b3dcdc0:
//                    case 0x5b5dcdc0:
//                    case 0x5abdcdc0:
//                    case 0x5c0dcdc0:
//                    case 0x5bedcdc0:
//                    case 0x5bfdcdc0:
//                    case 0x5cbdcdc0:
//                        val = ds.getInt();
//                        System.out.printf("0x%1$08x - 0x%2$08x (%2$d)\n", cmd, val);
//                        break;
                        
//                    default:
//                        System.out.printf("0x%1$08x\n", cmd);
//                        break;
                }
            } catch (ArrayIndexOutOfBoundsException | NullPointerException ex) {
                // negative textureId
                // texture is missing
            }
        }
//        System.out.println("========");
    }
}
