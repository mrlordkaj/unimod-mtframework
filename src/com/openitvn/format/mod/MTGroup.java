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

import com.openitvn.unicore.data.DataStream;
import com.openitvn.unicore.world.INode;

/**
 *
 * @author Thinh Pham
 */
public class MTGroup extends INode {
    public static final int STRUCT_SIZE = 32;
    
    public final int index;
    public final float unk2;
    public final float unk3;
    public final float unk4;
    public final float unk5;
    public final float unk6;
    public final float unk7;
    public final float unk8;
    
    public MTGroup(DataStream ds) {
        // read data
        index = ds.getInt();
        unk2 = ds.getFloat();
        unk3 = ds.getFloat();
        unk4 = ds.getFloat();
        unk5 = ds.getFloat();
        unk6 = ds.getFloat();
        unk7 = ds.getFloat();
        unk8 = ds.getFloat();
        // update properties
        name = String.format("Group_%1$03d", index);
    }
}
