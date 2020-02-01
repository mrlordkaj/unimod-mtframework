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
import com.openitvn.format.tex.MTTexture;
import com.openitvn.util.FileHelper;
import com.openitvn.maintain.Logger;
import java.io.IOException;

/**
 *
 * @author Thinh Pham
 */
public abstract class DependencyHelper {
    
    public static MTTexture requestTEX(DataStream rs, String path) {
        String texName = FileHelper.getFileName(path);
        try (DataStream ds = rs.getExternal(path + ".tex")) {
//            if (ds == null)
//                ds = rs.getExternal(path + ".rtex");
            MTTexture tex = new MTTexture(ds);
            tex.setName(texName);
//            ds.close();
            Logger.printNormal("Loaded dependency %1$s.tex", texName);
            return tex;
        } catch (IOException | UnsupportedOperationException | NullPointerException ex) {
            Logger.printError("Missed dependency %1$s.tex", texName);
            return null;
        }
    }
}
