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
public class MTBoundingSphere {
    
    public final Vector3 position;
    public final float radius;
    
    public MTBoundingSphere(DataStream ds) {
        this(DataFormat.D3DDECLTYPE_FLOAT3.read(ds), ds.getFloat());
    }
    
    public MTBoundingSphere(float[] postion, float radius) {
        this.position = new Vector3(postion[0], postion[1], postion[2]);
        this.radius = radius;
    }
}
