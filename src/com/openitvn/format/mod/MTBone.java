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
package com.openitvn.format.mod;

import com.badlogic.gdx.math.Matrix4;
import com.openitvn.unicore.data.DataStream;
import com.openitvn.unicore.world.IBone;

/**
 *
 * @author Thinh Pham
 */
public class MTBone extends IBone {
    
//    public static final int STRUCT_SIZE = 24;
    
    public final short animMapIndex;    // ubyte
    public final short parentId;        // ubyte, 255 = root
    public final short mirrorIndex;     // ubyte
    public final short paletteIndex;
    public final float unk1;
    public final float parentDistance;
    public final float x, y, z;         // relative to the parent bone
    
    public MTBone(int index, DataStream ds) {
        // read data
        animMapIndex = ds.getUByte();
        parentId = ds.getUByte();
        mirrorIndex = ds.getUByte();
        paletteIndex = ds.getUByte();
        unk1 = ds.getFloat();
        parentDistance = ds.getFloat();
        x = ds.getFloat();
        y = ds.getFloat();
        z = ds.getFloat();
        // update properties
        name = String.format("bone_%1$03d", index);
    }
    
    public void readLocalTransform(DataStream ds) {
        float[] m = transform.localMatrix.val;
        for (int i = 0; i < m.length; i++)
            m[i] = ds.getFloat();
    }
    
    public void readInverseTransform(DataStream ds) {
        float[] m = new float[16];
        for (int i = 0; i < 16; i++)
            m[i] = ds.getFloat();
        inverseTransform = new Matrix4(m);
    }
}
