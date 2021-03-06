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
package com.openitvn.format.tex.v11;

import com.openitvn.unicore.world.resource.IPixelFormat;
import com.openitvn.unicore.data.DataStream;
import com.openitvn.format.tex.MTTextureHeader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * @author Thinh Pham
 */
public class MTTextureHeader11 extends MTTextureHeader {
    
    private static final int STRUCT_SIZE = 34;
    private static final int VARIANT_BM = 0x0022;
    private static final int VARIANT_MM = 0x0002; //MM, CMM
    private static final int VARIANT_NM = 0x0032; //NM, DM
    private static final int VARIANT_CM = 0x0013; //CubeMap
    
    public final byte unk1; //uwrap?
    public final byte unk2; //vwrap?
    public final int unk3;
    public int formatRE5;
    public final float preR;
    public final float preG;
    public final float preB;
    public final float preA;
    
    public MTTextureHeader11(DataStream ds) {
        variant = ds.getShort();
        mipCount = ds.get();
        faceCount = ds.get();
        unk1 = ds.get(); //always 0
        unk2 = ds.get(); //always 0
        width = ds.getShort();
        height = ds.getShort();
        unk3 = ds.getInt(); //always 0?
        formatRE5 = ds.getInt();
        preR = ds.getFloat();
        preG = ds.getFloat();
        preB = ds.getFloat();
        preA = ds.getFloat();
    }
    
    @Override
    public byte[] toData() {
        ByteBuffer bb = ByteBuffer.allocate(STRUCT_SIZE).order(ByteOrder.LITTLE_ENDIAN);
        bb.putShort(variant);
        bb.put(mipCount);
        bb.put(faceCount);
        bb.put(unk1);
        bb.put(unk2);
        bb.putShort(width);
        bb.putShort(height);
        bb.putInt(unk3);
        bb.putInt(formatRE5);
        bb.putFloat(preR);
        bb.putFloat(preG);
        bb.putFloat(preB);
        bb.putFloat(preA);
        bb.rewind();
        return bb.array();
    }
    
    @Override
    public boolean isCubeMap() {
        return (variant == VARIANT_CM);
    }
    
    @Override
    public final IPixelFormat getPixelFormat() {
        switch (formatRE5) {
            case 0x00000015:
                return IPixelFormat.D3DFMT_A8R8G8B8;

            case 0x31545844:
                return IPixelFormat.D3DFMT_DXT1;

            case 0x33545844:
                return IPixelFormat.D3DFMT_DXT3;

            case 0x35545844:
                return IPixelFormat.D3DFMT_DXT5;
        }
        throw new UnsupportedOperationException(String.format("Pixel Format %1$02X is not supported yet.", formatRE5));
    }
    
    @Override
    public final void setPixelFormat(IPixelFormat format) {
        switch (format) {
            case D3DFMT_A8R8G8B8:
                formatRE5 = 0x00000015;
                return;
                
            case D3DFMT_DXT1:
                formatRE5 = 0x31545844;
                return;
                
            case D3DFMT_DXT3:
                formatRE5 = 0x33545844;
                return;
                
            case D3DFMT_DXT5:
                formatRE5 = 0x35545844;
                return;
        }
        throw new UnsupportedOperationException("Unsupported "+ format);
    }
}
