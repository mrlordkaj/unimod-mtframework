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

import com.openitvn.unicore.raster.IRaster;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * @author Thinh Pham
 */
public class ARGBRaster implements IRaster {
    
    private final int width;
    private final int height;
    private final ByteBuffer data;
    
    public ARGBRaster(int width, int height) {
        this.width = width;
        this.height = height;
        data = ByteBuffer.allocate(width*height*4).order(ByteOrder.LITTLE_ENDIAN);
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }
    
    @Override
    public void getRGBA(int x, int y, byte[] rgba) {
        ByteBuffer bb = ByteBuffer.wrap(rgba).order(ByteOrder.BIG_ENDIAN);
        bb.putInt(data.getInt((y*getWidth()+x)*4));
    }

    @Override
    public void setRGBA(int x, int y, byte[] rgba) {
        ByteBuffer bb = ByteBuffer.wrap(rgba).order(ByteOrder.BIG_ENDIAN);
        data.putInt((y*getWidth()+x)*4, bb.getInt());
    }
    
    @Override
    public void getRGBA(int x, int y, int width, int height, byte[] rgba) {
        ByteBuffer bb = ByteBuffer.wrap(rgba).order(ByteOrder.BIG_ENDIAN);
        int w = getWidth();
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                int pos = (y+j) * w + (x+i);
                bb.putInt(data.getInt(pos*4));
            }
        }
    }
    
    @Override
    public void setRGBA(int x, int y, int width, int height, byte[] rgba) {
        ByteBuffer bb = ByteBuffer.wrap(rgba).order(ByteOrder.BIG_ENDIAN);
        int w = getWidth();
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                int pos = (y+j) * w + (x+i);
                data.putInt(pos*4, bb.getInt());
            }
        }
    }
    
    public byte[] unwrap() {
        return data.array();
    }
}
