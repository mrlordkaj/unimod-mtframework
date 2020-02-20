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
package com.openitvn.format.tex.v15;

import com.openitvn.unicore.data.DataStream;
import com.openitvn.unicore.world.resource.IPixelFormat;
import com.openitvn.format.tex.MTTextureHeader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * @author Thinh Pham
 */
public class MTTextureHeader15 extends MTTextureHeader {
    
    private static final int STRUCT_SIZE = 10;
    private static final int VARIANT_COMMON  = 0x2000;
    private static final int VARIANT_CUBEMAP = 0x6000;
    
    public byte formatRE6;
    public final byte unk1; //uwrap?
    public final byte unk2; //vwrap?
    
    public MTTextureHeader15(DataStream ds) {
        variant = ds.getShort();
        mipCount = ds.get();
        int whf = ds.getInt(); //12 bit width, 12 bit height, 8 bit faceCount
        width     = (short)((whf       & 0xfff) * 4);
        height    = (short)((whf >> 12 & 0xfff) * 2);
        faceCount =  (byte)((whf >> 24 & 0xff ));
        formatRE6 = ds.get();
        unk1 = ds.get();
        unk2 = ds.get();
    }
    
    @Override
    public byte[] toData() {
        ByteBuffer header = ByteBuffer.allocate(STRUCT_SIZE).order(ByteOrder.LITTLE_ENDIAN);
        header.putShort(variant);
        header.put(mipCount);
        int whf = (width/4) | (height/2 << 12) | (faceCount << 24);
        header.putInt(whf);
        header.put(formatRE6);
        header.put(unk1);
        header.put(unk2);
        header.rewind();
        return header.array();
    }
    
    @Override
    public final IPixelFormat getPixelFormat() {
        switch (formatRE6) {
            case 0x07:
            case 0x08:
            case 0x09:
            case 0x0E:
            case 0x27:
            case 0x28:
            case 0x2D:
                return IPixelFormat.D3DFMT_A8R8G8B8;
                
            case 0x13:
            case 0x14:
            case 0x19:
            case 0x1E:
                return IPixelFormat.D3DFMT_DXT1;
                
            case 0x15:
            case 0x16:
                return IPixelFormat.D3DFMT_DXT3;
                
            case 0x17:
            case 0x18:
            case 0x1F:
            case 0x20:
            case 0x21:
            case 0x23:
            case 0x24:
            case 0x25:
            case 0x29:
            case 0x2A:
            case 0x2B:
            case 0x2F:
                return IPixelFormat.D3DFMT_DXT5;
                
            //case 0x1B:
            //    return 3Dc/ATI2;
        }
        throw new UnsupportedOperationException(String.format("Unsupported Format %02X.", formatRE6));
    }
    
    @Override
    public final void setPixelFormat(IPixelFormat format) {
        switch (format) {
            case D3DFMT_A8R8G8B8:
                formatRE6 = 0x07;
                return;
                
            case D3DFMT_DXT1:
                formatRE6 = 0x13;
                return;
                
            case D3DFMT_DXT3:
                formatRE6 = 0x15;
                return;
                
            case D3DFMT_DXT5:
                formatRE6 = 0x17;
                return;
        }
        throw new UnsupportedOperationException("Unsupported "+ format);
    }
    
    @Override
    public boolean isCubeMap() {
        return (variant == VARIANT_CUBEMAP);
    }
}
