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

import com.badlogic.gdx.graphics.GL20;
import com.openitvn.format.tex.v15.MTTextureHeader15;
import com.openitvn.format.tex.v11.MTTextureHeader11;
import com.openitvn.unicore.data.DataStream;
import com.openitvn.unicore.raster.IPixelFormat;
import com.openitvn.unicore.raster.ICubeMapHeader;
import com.openitvn.unicore.world.resource.ITexture;
import com.openitvn.unicore.raster.TextureHelper;
import com.openitvn.unicore.raster.IRaster;
import com.openitvn.util.FileHelper;
import com.openitvn.util.StringHelper;
import java.awt.Dimension;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * @author Thinh Pham
 */
public class MTTexture extends ITexture {
    
    public static final int MAGIC_TEX  = StringHelper.makeFourCC('T','E','X','\0');
    public static final int MAGIC_RTEX = StringHelper.makeFourCC('R','T','X','\0');
    
    private final boolean isRitual;
    private short version, revision;
    private MTTextureHeader header;
    private byte[][] cubeMapBuffer;
    private byte[][][] rasterBuffer;
    
    public MTTexture(boolean isRitual) {
        this.isRitual = isRitual;
    }
    
    public MTTexture(DataStream ds) {
        super(FileHelper.getFileName(ds.getLastPath()));
        setUWrap(GL20.GL_REPEAT);
        setVWrap(GL20.GL_REPEAT);
        
        int magic = ds.getInt();
        if (magic != MAGIC_TEX && magic != MAGIC_RTEX)
            throw new UnsupportedOperationException("Invalid MTF Texture");
        
        isRitual = (magic == MAGIC_RTEX);
        version = ds.getUByte();
        revision = ds.getUByte();
        switch (version) {
            case 112: // RE5
                header = new MTTextureHeader11(ds);
                break;
                
            case 154: // RE6
            case 155: // RE6
            case 156: // RE6
            case 157: // RER/RER2
            case 158: // DMC4
                header = new MTTextureHeader15(ds);
                break;
                
            default:
                throw new UnsupportedOperationException("Unsupported MTF Texture v" + version);
        }
        
        if (!isRitual) {
            // copy cubemap data, 18 bytes per face
            if (header.isCubeMap()) {
                cubeMapBuffer = new byte[header.faceCount][];
                for (int i = 0; i < cubeMapBuffer.length; i++)
                    cubeMapBuffer[i] = ds.get(new byte[18]);
            }
            // read image buffer offsets
            int[][] offsets = new int[header.faceCount][header.mipCount];
            for (int i = 0; i < header.faceCount; i++) {
                for (int j = 0; j < header.mipCount; j++)
                    offsets[i][j] = ds.getInt();
            }
            // copy imageBuffer from offset values above
            rasterBuffer = new byte[header.faceCount][header.mipCount][];
            for (int i = 0; i < header.faceCount; i++) {
                for(int j = 0; j < header.mipCount; j++) {
                    Dimension imageSize = TextureHelper.calcMipMapSize(header.width, header.height, j);
                    int bufferSize = header.getFormat().computeImageBufferSize(imageSize);
                    ds.position(offsets[i][j]);
                    rasterBuffer[i][j] = ds.get(new byte[bufferSize]);
                }
            }
        }
    }
    
    @Override
    public boolean replace(ITexture source) throws UnsupportedOperationException {
        //open creator form
        MTTextureVersion ver = MTTextureVersion.fromValue(version);
        MTTextureVariant var;
        if (source.isCubeMap())
            var = MTTextureVariant.CubeMap;
        else if(header != null)
            var = header.getVariant();
        else
            var = MTTextureVariant.DiffuseMap;
        short width = (short)source.getWidth();
        short height = (short)source.getHeight();
        byte faceCount = (byte)source.getFaceCount();
        byte mipCount = (byte)source.getMipCount();
        IPixelFormat fmt = source.getPixelFormat();
        FormCreator creator = new FormCreator(ver, var, width, height, faceCount, mipCount, fmt);
        creator.setVisible(true);
        
        if (!creator.isCancelled) {
            //create new header based creator form
            version = creator.version.getValue();
            switch (creator.version) {
                case RE5:
                    header = new MTTextureHeader11(creator.variant, creator.width, creator.height, creator.mipCount, creator.format);
                    break;

                default:
                    header = new MTTextureHeader15(creator.variant, creator.width, creator.height, creator.mipCount, creator.format);
                    break;
            }
            //encode or copy source data
            if (!isRitual) {
                if (source.isCubeMap() && cubeMapBuffer == null) {
                    if (source instanceof MTTexture)
                        cubeMapBuffer = ((MTTexture)source).cubeMapBuffer;
                    else
                        cubeMapBuffer = new byte[6][18];
                }
                //TODO: currently support copy same format
                rasterBuffer = new byte[header.faceCount][header.mipCount][];
                switch (header.getFormat()) {
                    case D3DFMT_A8R8G8B8:
                        for (int i = 0; i < header.faceCount; i++) {
                            for (int j = 0; j < header.mipCount; j++) {
                                Dimension mipSize = TextureHelper.calcMipMapSize(header.width, header.height, j);
                                ARGBRaster tmp = new ARGBRaster(mipSize.width, mipSize.height);
                                source.decodeImage(tmp, i, j);
                                rasterBuffer[i][j] = tmp.unwrap();
                            }
                        }
                        break;

                    default:
                        //same format, just copy buffer
                        for (int i = 0; i < header.faceCount; i++) {
                            for(int j = 0; j < header.mipCount; j++)
                                rasterBuffer[i][j] = source.getImageBuffer(i, j);
                        }
                        break;
                }
                
            }
            return true;
        } else {
            return false;
        }
    }
    
    //<editor-fold desc="Texture Properties" defaultstate="collapsed">
    
    @Override
    public int getWidth() {
        return header.width;
    }
    
    @Override
    public int getHeight() {
        return header.height;
    }
    
    @Override
    public int getFaceCount() {
        return header.faceCount;
    }
    
    @Override
    public int getMipCount() {
        return header.mipCount;
    }
    
    @Override
    public ICubeMapHeader getCubeMapHeader() {
        ICubeMapHeader cm = new ICubeMapHeader();
        if (header.isCubeMap()) {
            cm.hasPositiveX = true;
            cm.hasPositiveY = true;
            cm.hasPositiveZ = true;
            cm.hasNegativeX = true;
            cm.hasNegativeY = true;
            cm.hasNegativeZ = true;
        }
        return cm;
    }
    
    @Override
    public IPixelFormat getPixelFormat() {
        return header.getFormat();
    }
    //</editor-fold>
    
    //<editor-fold desc="File Data Management" defaultstate="collapsed">
    @Override
    public byte[] unwrap() {
        byte[] headerData = header.toBuffer();
        //pre-calculate fileSize
        int fileSize = headerData.length + 6;
        int[][] offsets = new int[header.faceCount][header.mipCount];
        if(!isRitual) {
            if(header.isCubeMap()) fileSize += header.faceCount * 18;
            fileSize += header.faceCount * header.mipCount * 4;
            for(int i = 0; i < header.faceCount; i++) {
                for(int j = 0; j < header.mipCount; j++) {
                    offsets[i][j] = fileSize;
                    fileSize += rasterBuffer[i][j].length; //end of current buffer is begining of next buffer
                }
            }
        }
        ByteBuffer data = ByteBuffer.allocate(fileSize).order(ByteOrder.LITTLE_ENDIAN);
        //write header
        data.putInt(isRitual ? MAGIC_RTEX : MAGIC_TEX);
        data.putShort(version);
        data.put(headerData);
        if (!isRitual) {
            //write CubeMap data if exists
            if (header.isCubeMap()) {
                for (byte[] bb : cubeMapBuffer)
                    data.put(bb);
            }
            //write image's buffer offsets
            for (int i = 0; i < header.faceCount; i++) {
                for (int j = 0; j < header.mipCount; j++)
                    data.putInt(offsets[i][j]);
            }
            //write image's buffers
            for(int i = 0; i < header.faceCount; i++) {
                for(int j = 0; j < header.mipCount; j++)
                    data.put(rasterBuffer[i][j]);
            }
        }
        return data.array();
    }
    
    @Override
    public byte[] getImageBuffer(int imageId, int mipMapLevel) {
        if (isRitual)
            throw new UnsupportedOperationException("Ritual Texture does not contains image data.");
        return rasterBuffer[imageId][mipMapLevel];
    }
    
    //</editor-fold>
    
    //<editor-fold desc="Encode / Decode" defaultstate="collapsed">
    @Override
    public byte[] encodeImage(IRaster src, IPixelFormat fmt) {
        throw new UnsupportedOperationException("Unsupported encoding format "+fmt);
    }
    
    @Override
    public void decodeImage(IRaster dst, int face, int mip) {
        if (!isRitual) {
            Dimension mipSize = TextureHelper.calcMipMapSize(header.width, header.height, mip);
            ByteBuffer bb = ByteBuffer.wrap(rasterBuffer[face][mip]).order(ByteOrder.LITTLE_ENDIAN);
            TextureHelper.decodeImage(dst, mipSize, getPixelFormat(), bb);
            if (header instanceof MTTextureHeader11) {
                // version 1.1 have channel multipler
                MTTextureHeader11 h = (MTTextureHeader11) header;
                byte[] rgba = new byte[4];
                for (int y = 0; y < dst.getHeight(); y++) {
                    for (int x = 0; x < dst.getWidth(); x++) {
                        dst.getRGBA(x, y, rgba);
                        rgba[0] = (byte)((rgba[0] & 0xff) * h.preR);
                        rgba[1] = (byte)((rgba[1] & 0xff) * h.preG);
                        rgba[2] = (byte)((rgba[2] & 0xff) * h.preB);
                        dst.setRGBA(x, y, rgba);
                    }
                }
            }
        }
    }
    //</editor-fold>
}
