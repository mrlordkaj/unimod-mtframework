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
import com.openitvn.unicore.raster.ICubeMap;
import com.openitvn.unicore.world.resource.ITexture;
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
    
    private final boolean isAbstract;
    private short version, revision;
    private MTTextureHeader header;
    private byte[][] cubeMapBuffer;
    private byte[][][] imageBuffers;
    
    public MTTexture(DataStream ds) {
        super(FileHelper.getFileName(ds.getLastPath()));
        
        int magic = ds.getInt();
        if (magic != MAGIC_TEX && magic != MAGIC_RTEX)
            throw new UnsupportedOperationException("Invalid MTF Texture");
        
        isAbstract = (magic == MAGIC_RTEX);
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
        
        if (!isAbstract) {
            // copy cubemap data, 18 bytes per face
            if (header.isCubeMap()) {
                cubeMapBuffer = new byte[header.faceCount][];
                for (int i = 0; i < cubeMapBuffer.length; i++) {
                    cubeMapBuffer[i] = ds.get(new byte[18]);
                }
            }
            // read image buffer offsets
            int[][] offsets = new int[header.faceCount][header.mipCount];
            for (int i = 0; i < header.faceCount; i++) {
                for (int j = 0; j < header.mipCount; j++) {
                    offsets[i][j] = ds.getInt();
                }
            }
            // copy imageBuffer from offset values above
            imageBuffers = new byte[header.faceCount][header.mipCount][];
            for (int i = 0; i < header.faceCount; i++) {
                for (int j = 0; j < header.mipCount; j++) {
                    Dimension imageSize = computeMipMapSize(header.width, header.height, j);
                    int bufferSize = header.getPixelFormat().computeImageBufferSize(imageSize);
                    ds.position(offsets[i][j]);
                    imageBuffers[i][j] = ds.get(new byte[bufferSize]);
                }
            }
        }
    }
    
    @Override
    public byte[] compileTexture(ITexture src) {
        // change header to match source
        header.width = (short)src.getWidth();
        header.height = (short)src.getHeight();
        header.faceCount = (byte)src.getFaceCount();
        header.mipCount = (byte)src.getMipCount();
        header.setPixelFormat(src.getPixelFormat());
        
        // copy buffer data from source
        imageBuffers = new byte[header.faceCount][header.mipCount][];
        for (int i = 0; i < header.faceCount; i++) {
            for (int j = 0; j < header.mipCount; j++) {
                imageBuffers[i][j] = src.getImageBuffer(i, j);
            }
        }
        
        // precomp fileSize
        byte[] headerData = header.toData();
        int fileSize = headerData.length + 6;
        int[][] offsets = new int[header.faceCount][header.mipCount];
        if (!isAbstract) {
            if (header.isCubeMap())
                fileSize += header.faceCount * 18;
            fileSize += header.faceCount * header.mipCount * 4;
            for (int i = 0; i < header.faceCount; i++) {
                for (int j = 0; j < header.mipCount; j++) {
                    offsets[i][j] = fileSize;
                    // end of current buffer is begining of next buffer
                    fileSize += imageBuffers[i][j].length;
                }
            }
        }
        ByteBuffer data = ByteBuffer.allocate(fileSize).order(ByteOrder.LITTLE_ENDIAN);
        // write header
        data.putInt(isAbstract ? MAGIC_RTEX : MAGIC_TEX);
        data.putShort(version);
        data.put(headerData);
        if (!isAbstract) {
            // write CubeMap data if exists
            if (header.isCubeMap()) {
                for (byte[] bb : cubeMapBuffer)
                    data.put(bb);
            }
            // write image's buffer offsets
            for (int i = 0; i < header.faceCount; i++) {
                for (int j = 0; j < header.mipCount; j++)
                    data.putInt(offsets[i][j]);
            }
            // write image's buffers
            for (int i = 0; i < header.faceCount; i++) {
                for (int j = 0; j < header.mipCount; j++)
                    data.put(imageBuffers[i][j]);
            }
        }
        return data.array();
    }
    
    @Override
    public byte[][] getPalette() {
        return null;
    }
    
    @Override
    public byte[] getImageBuffer(int faceId, int mipLevel) {
        return isAbstract ? new byte[0] : imageBuffers[faceId][mipLevel];
    }
    
    @Override
    public void decodeImage(IRaster dstImg, int faceId, int mipLevel) {
        if (!isAbstract) {
            super.decodeImage(dstImg, faceId, mipLevel);
            if (header instanceof MTTextureHeader11) {
                // ver 1.1 have channel multipler
                MTTextureHeader11 h = (MTTextureHeader11) header;
                byte[] rgba = new byte[4];
                for (int y = 0; y < dstImg.getHeight(); y++) {
                    for (int x = 0; x < dstImg.getWidth(); x++) {
                        dstImg.getRGBA(x, y, rgba);
                        rgba[0] = (byte)((rgba[0] & 0xff) * h.preR);
                        rgba[1] = (byte)((rgba[1] & 0xff) * h.preG);
                        rgba[2] = (byte)((rgba[2] & 0xff) * h.preB);
                        dstImg.setRGBA(x, y, rgba);
                    }
                }
            }
        }
    }
    
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
    public int getUWrap() {
        return GL20.GL_REPEAT;
    }

    @Override
    public int getVWrap() {
        return GL20.GL_REPEAT;
    }
    
    @Override
    public ICubeMap getCubeMapHeader() {
        ICubeMap cm = new ICubeMap();
        if (header.isCubeMap()) {
            cm.positiveX = true;
            cm.positiveY = true;
            cm.positiveZ = true;
            cm.negativeX = true;
            cm.negativeY = true;
            cm.negativeZ = true;
        }
        return cm;
    }
    
    @Override
    public IPixelFormat getPixelFormat() {
        return header.getPixelFormat();
    }
}
