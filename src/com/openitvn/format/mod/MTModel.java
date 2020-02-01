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

import com.openitvn.unicore.world.IMesh;
import com.openitvn.unicore.world.resource.IModel;

/**
 *
 * @author Thinh Pham
 */
public abstract class MTModel extends IModel {
    
    protected short levelOfDetail;
    protected int layerIndex = -1;
    protected final IMesh meshAsModel;
    
    public MTModel() {
        meshAsModel = new IMesh();
        meshes.add(meshAsModel);
    }
    
    public short getLevelOfDetail() {
        return levelOfDetail;
    }
}
