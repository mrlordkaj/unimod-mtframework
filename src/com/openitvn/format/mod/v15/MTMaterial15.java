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
package com.openitvn.format.mod.v15;

import com.openitvn.unicore.data.DataStream;
import com.openitvn.unicore.world.resource.ITexture;
import com.openitvn.unicore.world.resource.IMaterial;
import java.util.ArrayList;

/**
 *
 * @author Thinh Pham
 */
public class MTMaterial15 extends IMaterial {
    
//    private static final int STRUCT_SIZE = 160;
    
    private static final int FLAG_UNKNOW_00      = 0b0000000000000001;
    private static final int FLAG_UNKNOW_01      = 0b0000000000000010;
    private static final int FLAG_IS_ABSTRACT    = 0b0000000000000100; // turn on if the mesh is abstract
    private static final int FLAG_UNKNOW_03      = 0b0000000000001000;
    private static final int FLAG_UNKNOW_04      = 0b0000000000010000;
    private static final int FLAG_UNKNOW_05      = 0b0000000000100000;
    private static final int FLAG_IS_OPAQUE      = 0b0000000001000000; // or FLAG_BACKFACE_CULL? or FLAG_RENDERABLE? it always equals not FLAG_IS_ABSTRACT?
    private static final int FLAG_UNKNOW_07      = 0b0000000010000000;
    private static final int FLAG_HAS_ALPHA      = 0b0000000100000000;
    private static final int FLAG_UNKNOW_09      = 0b0000001000000000;
    private static final int FLAG_UNKNOW_10      = 0b0000010000000000;
    private static final int FLAG_8BONE_VERTEX   = 0b0000100000000000; // v156 always set to zero since 8 bones is not yet supported
    private static final int FLAG_UNKNOW_12      = 0b0001000000000000;
    private static final int FLAG_UNKNOW_13      = 0b0010000000000000;
    private static final int FLAG_UNKNOW_14      = 0b0100000000000000;
    private static final int FLAG_UNKNOW_15      = 0b1000000000000000;
    
    //struct data
    private final short flags1;
    private final short unk1;
    private final short unk2;
    private final short unk3;
    private final short unk4;
    private final short unk5;
    private final short unk6;
    private final short unk7;
    private final short unk8;
    private final short unk9;
    private final short unk10;
    private final short unk11;
    private final short unk12;
    private final short unk13;
    private final short unk14;
    private final short unk15;
    private final short unk16;
    private final short unk17;
    private final short unk18;
    private final short unk19;
    private final short unk20;
    private final short unk21;
    private final short unk22;
    private final int diffuseIndex;
    private final int normalIndex;
    private final int ambientIndex;
    private final int tex4;
    private final int tex5;
    private final int tex6;
    private final int tex7;
    private final int tex8;
    private final float unk23;
    private final float unk24;
    private final float unk25;
    private final float unk26;
    private final float unk27;
    private final float unk28;
    private final float unk29;
    private final float unk30;
    private final float unk31;
    private final float unk32;
    private final float unk33;
    private final float unk34;
    private final float unk35;
    private final float unk36;
    private final float unk37;
    private final float unk38;
    private final float unk39;
    private final float unk40;
    private final float unk41;
    private final float unk42;
    private final float unk43;
    private final float unk44;
    private final float unk45;
    private final float unk46;
    private final float unk47;
    private final float unk48;
    
    public MTMaterial15(DataStream ds, ArrayList<ITexture> textures) {
        flags1 = ds.getShort();
        unk1 = ds.getUByte();
        unk2 = ds.getUByte();
        unk3 = ds.getUByte(); //always 131
        unk4 = ds.getUByte(); //skin 68, hair 68, matuge 0, armor 72
        unk5 = ds.getUByte(); //always 0
        unk6 = ds.getUByte(); //always 0
        unk7 = ds.getUByte(); //always 6
        unk8 = ds.getUByte(); //always 230
        unk9 = ds.getUByte(); //always 39
        unk10 = ds.getUByte(); //always 135
        unk11 = ds.getUByte(); //skin 123, hair 123, matuge 19, armor 195
        unk12 = ds.getUByte(); //skin 1, hair 1, armor 1, matuge 0, hand 0
        unk13 = ds.getUByte(); //always 0
        unk14 = ds.getUByte(); //always 0
        unk15 = ds.getUByte(); //always 112
        unk16 = ds.getUByte(); //always 8
        unk17 = ds.getUByte(); //always 177
        unk18 = ds.getUByte(); //always 10
        unk19 = ds.getUByte(); //always 0
        unk20 = ds.getUByte(); //always 0
        unk21 = ds.getUByte(); //always 0
        unk22 = ds.getUByte(); //always 0
        diffuseIndex = ds.getInt();
        normalIndex = ds.getInt();
        ambientIndex = ds.getInt();
        tex4 = ds.getInt();
        tex5 = ds.getInt();
        tex6 = ds.getInt();
        tex7 = ds.getInt();
        tex8 = ds.getInt();
        unk23 = ds.getFloat();
        unk24 = ds.getFloat();
        unk25 = ds.getFloat();
        unk26 = ds.getFloat();
        unk27 = ds.getFloat();
        unk28 = ds.getFloat();
        unk29 = ds.getFloat();
        unk30 = ds.getFloat();
        unk31 = ds.getFloat();
        unk32 = ds.getFloat();
        unk33 = ds.getFloat();
        unk34 = ds.getFloat();
        unk35 = ds.getFloat();
        unk36 = ds.getFloat();
        unk37 = ds.getFloat();
        unk38 = ds.getFloat();
        unk39 = ds.getFloat();
        unk40 = ds.getFloat();
        unk41 = ds.getFloat();
        unk42 = ds.getFloat();
        unk43 = ds.getFloat();
        unk44 = ds.getFloat();
        unk45 = ds.getFloat();
        unk46 = ds.getFloat();
        unk47 = ds.getFloat();
        unk48 = ds.getFloat();
        // set generic attribute
        if (ambientIndex > 0 && textures.get(ambientIndex - 1) != null)
            ambientTexture = textures.get(ambientIndex - 1).getName();
        if (diffuseIndex > 0 && textures.get(diffuseIndex - 1) != null)
            diffuseTexture = textures.get(diffuseIndex - 1).getName();
        if (normalIndex  > 0 && textures.get(normalIndex  - 1) != null)
            normalTexture = textures.get(normalIndex  - 1).getName();
        alphaBlend = (flags1 & FLAG_HAS_ALPHA) != 0;
    }
    
    public boolean isRenderable() {
        return (flags1 & FLAG_IS_ABSTRACT) == 0;
    }
}
