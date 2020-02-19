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
package com.openitvn.format.tex;

import com.openitvn.unicore.raster.IPixelFormat;

/**
 *
 * @author Thinh Pham
 */
public abstract class MTTextureHeader {
    
    protected short variant;
    public short width;
    public short height;
    public byte faceCount;
    public byte mipCount;
    
    public abstract IPixelFormat getPixelFormat();
    public abstract void setPixelFormat(IPixelFormat format);
    public abstract boolean isCubeMap();
    public abstract byte[] toData();
}
