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
import com.openitvn.unicore.world.resource.ITexture;
import com.openitvn.unicore.world.resource.IMaterial;
import java.util.ArrayList;

/**
 *
 * @author Thinh Pham
 */
public abstract class MTModReader {
    
    // order is matter (used as id), so we must use ArrayList collection here
    protected final ArrayList<IMaterial> materials = new ArrayList();
    protected final ArrayList<ITexture> textures = new ArrayList();
    
    protected final short version, revision;
    
    public MTModReader(short ver, short rev) {
        this.version = ver;
        this.revision = rev;
    }
    
    protected abstract void decode(MTMod world, DataStream ds) throws UnsupportedOperationException;
}
