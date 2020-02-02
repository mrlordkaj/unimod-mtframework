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
import com.openitvn.unicore.world.IWorldUnit;
import com.openitvn.unicore.world.IWorld;
import com.openitvn.format.mod.v15.MTModReader15;
import com.openitvn.format.mod.v21.MTModReader21;
import com.openitvn.format.mrl.MTMaterialHash;
import com.openitvn.unicore.world.ILayer;
import com.openitvn.util.StringHelper;
import com.openitvn.unicore.world.IWorldCoord;

/**
 *
 * @author Thinh Pham
 */
public class MTMod extends IWorld {
    
    private static final int MAGIC_MOD = StringHelper.makeFourCC("MOD\0");
    
    private short version;  // 156: RE5
                            // 210: RER/RER2
                            // 211: RE6
    private short revision; // ubyte
    
    public MTMod() {
        setCoordinate(IWorldCoord.Yup, IWorldUnit.Centimeters);
    }
    
    @Override
    public void fromData(DataStream ds) throws UnsupportedOperationException {
        if (ds.getInt() == MAGIC_MOD) {
            version = ds.getUByte(); 
            revision = ds.getUByte();
            MTModReader reader;
            switch (version) {
                case 156:
                    reader = new MTModReader15(version, revision);
                    break;

                case 210:
                case 211:
                    reader = new MTModReader21(version, revision);
                    break;

                default:
                    throw new UnsupportedOperationException("Unsupported mod version " + version);
            }
            reader.decode(this, ds);
            resource.register(reader.textures);
            resource.register(reader.materials);
            // cleanup cache stuff
            MTMaterialHash.cleanup();
        } else {
            throw new UnsupportedOperationException("Invalid mod format");
        }
    }
    
    public void addModel(MTModel model) {
        short lod = model.getLevelOfDetail();
        if (lod != 255) {
            ILayer layer = null;
            for (ILayer l : layers) {
                if (l.index == lod) {
                    layer = l;
                    break;
                }
            }
            if (layer == null) {
                boolean active = lod == 1;
                layer = new ILayer(lod, "Layer "+lod, active);
                layers.add(layer);
            }
            model.layerIndex = lod;
        }
        resource.register(model);
    }
}
