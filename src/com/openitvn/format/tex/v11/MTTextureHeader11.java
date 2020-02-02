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

import com.openitvn.unicore.raster.IPixelFormat;
import com.openitvn.unicore.data.DataStream;
import com.openitvn.format.tex.MTTextureHeader;
import com.openitvn.format.tex.MTTextureVariant;
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
    
    private final byte unk1; //uwrap?
    private final byte unk2; //vwrap?
    private final int unk3;
    private int formatRE5;
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
    
    public MTTextureHeader11(MTTextureVariant variant, short width, short height, byte mipCount, IPixelFormat format) throws UnsupportedOperationException {
        switch (variant) {
            case NormalMap:
                this.variant = VARIANT_NM;
                this.faceCount = 1;
                break;
                
            case LightMap:
                this.variant = VARIANT_MM;
                this.faceCount = 1;
                break;
                
            case CubeMap:
                this.variant = VARIANT_CM;
                this.faceCount = 6;
                break;
                
            default:
                this.variant = VARIANT_BM;
                this.faceCount = 1;
                break;
        }
        switch (format) {
            case D3DFMT_A8R8G8B8:
            case DXGI_FORMAT_B8G8R8A8_UNORM:
                formatRE5 = 0x00000015;
                break;
                
            case D3DFMT_DXT1:
            case DXGI_FORMAT_BC1_UNORM:
                formatRE5 = 0x31545844;
                break;
                
            case D3DFMT_DXT3:
            case DXGI_FORMAT_BC2_UNORM:
                formatRE5 = 0x33545844;
                break;
                
            case D3DFMT_DXT5:
            case DXGI_FORMAT_BC3_UNORM:
                formatRE5 = 0x35545844;
                break;
                
            default:
                throw new UnsupportedOperationException(String.format("Pixel Format %1$s is not supported yet.", format));
        }
        this.mipCount = mipCount;
        this.width = width;
        this.height = height;
        preA = preR = preG = preB = 1;
        unk3 = unk2 = unk1 = 0;
    }
    
    @Override
    protected byte[] toBuffer() {
        ByteBuffer header = ByteBuffer.allocate(STRUCT_SIZE).order(ByteOrder.LITTLE_ENDIAN);
        header.putShort(variant);
        header.put(mipCount);
        header.put(faceCount);
        header.put(unk1);
        header.put(unk2);
        header.putShort(width);
        header.putShort(height);
        header.putInt(unk3);
        header.putInt(formatRE5);
        header.putFloat(preR);
        header.putFloat(preG);
        header.putFloat(preB);
        header.putFloat(preA);
        header.rewind();
        return header.array();
    }
    
    @Override
    protected boolean isCubeMap() {
        return (variant == VARIANT_CM);
    }
    
    @Override
    protected final IPixelFormat getFormat() throws UnsupportedOperationException {
        switch (formatRE5) {
            case 0x00000015:
                return IPixelFormat.D3DFMT_A8R8G8B8;

            case 0x31545844:
                return IPixelFormat.D3DFMT_DXT1;

            case 0x33545844:
                return IPixelFormat.D3DFMT_DXT3;

            case 0x35545844:
                return IPixelFormat.D3DFMT_DXT5;

            default:
                throw new UnsupportedOperationException(String.format("Pixel Format %1$02X is not supported yet.", formatRE5));
        }
    }
    
    @Override
    protected MTTextureVariant getVariant() {
        switch (variant) {
            case VARIANT_MM:
                return MTTextureVariant.LightMap;
                
            case VARIANT_NM:
                return MTTextureVariant.NormalMap;
                
            case VARIANT_CM:
                return MTTextureVariant.CubeMap;
                
            default:
                return MTTextureVariant.DiffuseMap;
        }
    }
}
