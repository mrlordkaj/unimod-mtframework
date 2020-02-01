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

import com.badlogic.gdx.math.Vector3;
import com.openitvn.unicore.data.DataFormat;
import com.openitvn.unicore.data.DataStream;

/**
 *
 * @author Thinh Pham
 */
public class MTBoundingBox {
    
    public final Vector3 min;
    public final Vector3 max;
    
    public MTBoundingBox(DataStream ds) {
        float[] b0 = DataFormat.D3DDECLTYPE_FLOAT4.read(ds);
        float[] b1 = DataFormat.D3DDECLTYPE_FLOAT4.read(ds);
        min = new Vector3(b0[0], b0[1], b0[2]);
        max = new Vector3(b1[0], b1[1], b1[2]);
    }
}
