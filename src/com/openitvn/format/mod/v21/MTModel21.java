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
package com.openitvn.format.mod.v21;

import com.badlogic.gdx.math.Vector3;
import com.openitvn.unicore.data.DataStream;
import com.openitvn.unicore.world.IVertex;
import com.openitvn.format.mod.MTModel;
import com.openitvn.unicore.data.DataFormat;

/**
 *
 * @author Thinh Pham
 */
public class MTModel21 extends MTModel {
    
    private static final int STRUCT_SIZE = 48;
    
    private static final int FLAG_RENDERABLE     = 0b1;
    private static final int FLAG_UNKNOW_01      = 0b1 << 1;
    private static final int FLAG_UNKNOW_02      = 0b1 << 2;
    private static final int FLAG_UNKNOW_03      = 0b1 << 3;
    private static final int FLAG_UNKNOW_04      = 0b1 << 4;
    private static final int FLAG_UNKNOW_05      = 0b1 << 5;
    private static final int FLAG_UNKNOW_06      = 0b1 << 6;
    private static final int FLAG_UNKNOW_07      = 0b1 << 7;
    private static final int FLAG_UNKNOW_08      = 0b1 << 8;
    private static final int FLAG_UNKNOW_09      = 0b1 << 9;
    private static final int FLAG_UNKNOW_10      = 0b1 << 10;
    private static final int FLAG_UNKNOW_11      = 0b1 << 11;
    private static final int FLAG_UNKNOW_12      = 0b1 << 12;
    private static final int FLAG_UNKNOW_13      = 0b1 << 13;
    private static final int FLAG_UNKNOW_14      = 0b1 << 14;
    private static final int FLAG_UNKNOW_15      = 0b1 << 15;
    
    private final short flags;
    private final short vertexCount;
    protected final byte groupIndex;
    protected final short matIndex;
//    protected final short levelOfDetail; // ubyte
    private final byte clazz; // what class
    private final byte vertexFlag; // meshClass
    private final byte vertexStride;
    private final byte renderMode;
    private final int indexStart;
    private final int vertexOffset;
    protected final int vertexFormat;
    private final int indexPosition;
    private final int indexCount;
    private final int indexOffset;
//    private final byte boneMapIndex;
    private final byte vertexGroupCount;
    private final byte unk2, unk3;
    private final short minIndex;
    private final short maxIndex;
    private final int hash;
    
    MTModel21(DataStream ds) {
        // header
        flags = ds.getShort();
        vertexCount = ds.getShort();
        groupIndex = ds.get();
        matIndex = (short)(ds.getShort() >> 4);
        levelOfDetail = ds.getUByte();
        clazz = ds.get();
        vertexFlag = ds.get();
        vertexStride = ds.get();
        renderMode = ds.get();
        indexStart = ds.getInt();
        vertexOffset = ds.getInt();
        vertexFormat = ds.getInt();
        indexPosition = ds.getInt();
        indexCount = ds.getInt();
        indexOffset = ds.getInt();
        boneMapIndex = ds.get(); //37
        vertexGroupCount = ds.get(); //weight map?
        unk2 = ds.get();
        unk3 = ds.get();
        minIndex = ds.getShort();
        maxIndex = ds.getShort();
        hash = ds.getInt();
    }
    
    boolean isRenderable() {
        return (flags & FLAG_RENDERABLE) != 0;
    }
    
    void setMaterialName(String matName) {
        meshAsModel.materialName = matName;
    }
    
    void readIndexBuffer(DataStream ib) {
        int indexBufferOffset = indexPosition * 2 + indexOffset * 2;
        ib.position(ib.position() + indexBufferOffset);
        short[] indices = new short[indexCount];
        for (int i = 0; i < indexCount; i++)
            indices[i] = (short)(ib.getShort() - indexStart);
        meshAsModel.setIndices(indices);
    }
    
    void readVertexBuffer(DataStream ds, Vector3 translate, Vector3 scale) throws UnsupportedOperationException {
//        System.out.printf("0x%1$08X\n", vertexFormat);
        int vertexBufferOffset = vertexOffset + indexStart * vertexStride;
        ds.position(ds.position() + vertexBufferOffset);
        IVertex[] vertices = new IVertex[vertexCount];
        float f = 1 / 32767f;
        switch (vertexFormat) {
            case 0xB0983013: // RER om1303
            case 0xB0983014: // DMC4
                // 12, degenerate
                for (int i = 0; i < vertices.length; i++) {
                    float[] b0 = DataFormat.D3DDECLTYPE_SHORT4.read(ds);        // px py pz b1 (8)
                    float[] b1 = DataFormat.D3DDECLTYPE_FLOAT16_2.read(ds);     // u1 v1 (4)
                    IVertex v = vertices[i] = new IVertex(b0[0]*f, b0[1]*f, b0[2]*f);
                    v.scl(scale).add(translate);
                    v.addBone(b0[3], 1);
                    v.addTexCoord(b1[0], b1[1]);
                }
                break;
                
            case 0xDB7DA014:
            case 0xDB7DA015: // DMC4
                // 16
                for (int i = 0; i < vertices.length; i++) {
                    float[] b0 = DataFormat.D3DDECLTYPE_SHORT4N.read(ds);     // px py pz pw (8)
                    float[] b1 = DataFormat.D3DDECLTYPE_UBYTE4N.read(ds);     // nx ny nz nw (4)
                    float[] b2 = DataFormat.D3DDECLTYPE_FLOAT16_2.read(ds);   // u1 v1 (4)
                    IVertex v = vertices[i] = new IVertex(b0[0], b0[1], b0[2]);
                    v.scl(scale).add(translate);
                    v.setNormal(b1[0]-0.5f, b1[1]-0.5f, b1[2]-0.5f, b1[3]-0.5f);
                    v.addTexCoord(b2[0], b2[1]);
                }
                break;

            case 0xA8FAB018: // RER om1303
            case 0xA8FAB019: // DMC4
                // 20
                for (int i = 0; i < vertices.length; i++) {
                    float[] b0 = DataFormat.D3DDECLTYPE_SHORT4.read(ds);      // px py pz b1 (8)
                    float[] b1 = DataFormat.D3DDECLTYPE_UBYTE4N.read(ds);     // nx ny nz nw (4)
                    float[] b2 = DataFormat.D3DDECLTYPE_UBYTE4N.read(ds);     // tx ty tz tw (4)
                    float[] b3 = DataFormat.D3DDECLTYPE_FLOAT16_2.read(ds);   // u1 v1 (4)
                    IVertex v = vertices[i] = new IVertex(b0[0]*f, b0[1]*f, b0[2]*f);
                    v.scl(scale).add(translate);
                    v.setNormal(b1[0]-0.5f, b1[1]-0.5f, b1[2]-0.5f, b1[3]-0.5f);
                    v.setTangent(b2[0]-0.5f, b2[1]-0.5f, b2[2]-0.5f, b2[3]-0.5f);
                    v.addTexCoord(b3[0], b3[1]);
                    v.addBone(b0[3], 1);
                }
                break;
                
            case 0xA8FA0018: // RER2 fig09 mod
                // 20
                for (int i = 0; i < vertices.length; i++) {
                    vertices[i] = new IVertex();
                    ds.skip(20);
                }
                break;
            
            case 0xA7D7D036:
            case 0xA7D7D037: // DMC4 st000-m05-denkyu
            case 0x0CB68015: // RER om1a00
            case 0x0CB68016: // DMC em000
                // 20
                for (int i = 0; i < vertices.length; i++) {
                    float[] b0 = DataFormat.D3DDECLTYPE_FLOAT3.read(ds);      // px py pz (12)
                    float[] b1 = DataFormat.D3DDECLTYPE_UBYTE4N.read(ds);     // nx ny nz nw (4)
                    float[] b2 = DataFormat.D3DDECLTYPE_FLOAT16_2.read(ds);   // u1 v1 (4)
                    IVertex v = vertices[i] = new IVertex(b0[0], b0[1], b0[2]);
                    v.scl(scale).add(translate);
                    v.setNormal(b1[0]-0.5f, b1[1]-0.5f, b1[2]-0.5f, b1[3]-0.5f);
                    v.addTexCoord(b2[0], b2[1]);
                }
                break;
                
            case 0xC31F201C:
            case 0xC31F201D: // DMC4
                // 24
                for (int i = 0; i < vertices.length; i++) {
                    float[] b0 = DataFormat.D3DDECLTYPE_SHORT4N.read(ds);     // px py pz w1 (8)
                    float[] b1 = DataFormat.D3DDECLTYPE_UBYTE4N.read(ds);     // nx ny nz nw (4)
                    float[] b2 = DataFormat.D3DDECLTYPE_UBYTE4N.read(ds);     // tx ty tz tw (4)
                    float[] b3 = DataFormat.D3DDECLTYPE_FLOAT16_2.read(ds);   // u1 v1 (4)
                    float[] b4 = DataFormat.D3DDECLTYPE_FLOAT16_2.read(ds);   // b1 b2 (4)
                    IVertex v = vertices[i] = new IVertex(b0[0], b0[1], b0[2]);
                    v.scl(scale).add(translate);
                    v.setNormal(b1[0]-0.5f, b1[1]-0.5f, b1[2]-0.5f, b1[3]-0.5f);
                    v.setTangent(b2[0]-0.5f, b2[1]-0.5f, b2[2]-0.5f, b2[3]-0.5f);
                    v.addTexCoord(b3[0], b3[1]);
                    v.addBone(b4[0], b0[3]);
                    v.addBone(b4[1], 1-b0[3]);
                }
                break;
                
            case 0xD8297028: // RE6 Core/dogtag
            case 0xD8297029: // DMC4 st000/ko012-m00-nagaisu
                // 24
                for (int i = 0; i < vertices.length; i++) {
                    float[] b0 = DataFormat.D3DDECLTYPE_FLOAT3.read(ds);      // px py pz (12)
                    float[] b1 = DataFormat.D3DDECLTYPE_UBYTE4N.read(ds);     // nx ny nz nw (4)
                    float[] b2 = DataFormat.D3DDECLTYPE_UBYTE4N.read(ds);     // tx ty tz tw (4) (unchecked)
                    float[] b3 = DataFormat.D3DDECLTYPE_FLOAT16_2.read(ds);   // u1 v1 (4)
                    IVertex v = vertices[i] = new IVertex(b0[0], b0[1], b0[2]);
                    v.scl(scale).add(translate);
                    v.setNormal(b1[0]-0.5f, b1[1]-0.5f, b1[2]-0.5f, b1[3]-0.5f);
                    v.setTangent(b2[0]-0.5f, b2[1]-0.5f, b2[2]-0.5f, b2[3]-0.5f);
                    v.addTexCoord(b3[0], b3[1]);
                }
                break;
                
            case 0xCBF6C01A:
            case 0xCBF6C01B: // DMC4 st001-m98e-demogate
                // 24
                for (int i = 0; i < vertices.length; i++) {
                    float[] b0 = DataFormat.D3DDECLTYPE_SHORT4.read(ds);      // px py pz b1 (8)
                    float[] b1 = DataFormat.D3DDECLTYPE_UBYTE4N.read(ds);     // nx ny nz nw (4)
                    float[] b2 = DataFormat.D3DDECLTYPE_UBYTE4N.read(ds);     // tx ty tz tw (4)
                    float[] b3 = DataFormat.D3DDECLTYPE_FLOAT16_2.read(ds);   // u1 v1 (4)
                    ds.skip(4); // TODO
                    IVertex v = vertices[i] = new IVertex(b0[0]*f, b0[1]*f, b0[2]*f);
                    v.scl(scale).add(translate);
                    v.setNormal(b1[0]-0.5f, b1[1]-0.5f, b1[2]-0.5f, b1[3]-0.5f);
                    v.setTangent(b2[0]-0.5f, b2[1]-0.5f, b2[2]-0.5f, b2[3]-0.5f);
                    v.addTexCoord(b3[0], b3[1]);
                    v.addBone(b0[3], 1);
                }
                break;
                
            case 0x207D6037: // RER om1106, om1425
            case 0x207D6038: // DCM4 st000-m00-base
                // 24
                for (int i = 0; i < vertices.length; i++) {
                    float[] b0 = DataFormat.D3DDECLTYPE_FLOAT3.read(ds);      // px py pz (12)
                    float[] b1 = DataFormat.D3DDECLTYPE_UBYTE4N.read(ds);     // nx ny nz nw (4)
                    float[] b2 = DataFormat.D3DDECLTYPE_FLOAT16_2.read(ds);   // u1 v1 (4)
                    float[] b3 = DataFormat.D3DDECLTYPE_UBYTE4N.read(ds);     // tx ty tz tw (4) (unchecked)
                    IVertex v = vertices[i] = new IVertex(b0[0], b0[1], b0[2]);
                    v.scl(scale).add(translate);
                    v.setNormal(b1[0]-0.5f, b1[1]-0.5f, b1[2]-0.5f, b1[3]-0.5f);
                    v.setTangent(b3[0]-0.5f, b3[1]-0.5f, b3[2]-0.5f, b3[3]-0.5f);
                    v.addTexCoord(b2[0], b2[1]);
                }
                break;
                
            case 0xD1A47038: // RER om150f, om180e
            case 0xD1A47039: // DMC4 st001-m00-base
                // 24
                for (int i = 0; i < vertices.length; i++) {
                    float[] b0 = DataFormat.D3DDECLTYPE_FLOAT3.read(ds);      // px py pz (12)
                    float[] b1 = DataFormat.D3DDECLTYPE_UBYTE4N.read(ds);     // nx ny nz nw (4)
                    float[] b2 = DataFormat.D3DDECLTYPE_FLOAT16_2.read(ds);   // u1 v1 (4)
                    float[] b3 = DataFormat.D3DDECLTYPE_UBYTE4N.read(ds);     // tx ty tz tw (4) (unchecked)
                    IVertex v = vertices[i] = new IVertex(b0[0], b0[1], b0[2]);
                    v.scl(scale).add(translate);
                    v.setNormal(b1[0]-0.5f, b1[1]-0.5f, b1[2]-0.5f, b1[3]-0.5f);
                    v.setTangent(b3[0]-0.5f, b3[1]-0.5f, b3[2]-0.5f, b3[3]-0.5f);
                    v.addTexCoord(b2[0], b2[1]);
                }
                break;
                
            case 0xA14E003C: // uWp0980/md001e_00
                // 28
                for (int i = 0; i < vertices.length; i++) {
                    float[] b0 = DataFormat.D3DDECLTYPE_FLOAT3.read(ds);      // px py pz (12)
                    float[] b1 = DataFormat.D3DDECLTYPE_UBYTE4N.read(ds);     // nx ny nz nw (4)
                    float[] b2 = DataFormat.D3DDECLTYPE_FLOAT16_2.read(ds);   // u1 v1 (4)
                    ds.skip(8);
                    IVertex v = vertices[i] = new IVertex(b0[0], b0[1], b0[2]);
                    v.scl(scale).add(translate);
                    v.setNormal(b1[0]-0.5f, b1[1]-0.5f, b1[2]-0.5f, b1[3]-0.5f);
                    v.addTexCoord(b2[0], b2[1]);
                }
                break;
                
            case 0xAFA6302D: // uWp0990/md0303_04
                // 28
                for (int i = 0; i < vertices.length; i++) {
                    float[] b0 = DataFormat.D3DDECLTYPE_FLOAT3.read(ds);      // px py pz (12)
                    float[] b1 = DataFormat.D3DDECLTYPE_UBYTE4N.read(ds);     // nx ny nz nw (4)
                    float[] b2 = DataFormat.D3DDECLTYPE_FLOAT16_2.read(ds);   // u1 v1 (4) (unchecked)
                    ds.skip(8);
                    IVertex v = vertices[i] = new IVertex(b0[0], b0[1], b0[2]);
                    v.scl(scale).add(translate);
                    v.setNormal(b1[0]-0.5f, b1[1]-0.5f, b1[2]-0.5f, b1[3]-0.5f);
                    v.addTexCoord(b2[0], b2[1]);
                }
                break;
                
            case 0x5E7F202C: // uWp1500/md0020
                // 28
                for (int i = 0; i < vertices.length; i++) {
                    float[] b0 = DataFormat.D3DDECLTYPE_FLOAT3.read(ds);      // px py pz (12) (unchecked)
                    float[] b1 = DataFormat.D3DDECLTYPE_UBYTE4N.read(ds);     // nx ny nz nw (4)
                    float[] b2 = DataFormat.D3DDECLTYPE_FLOAT16_2.read(ds);   // u1 v1 (4) (unchecked)
                    ds.skip(8);
                    IVertex v = vertices[i] = new IVertex(b0[0], b0[1], b0[2]);
                    v.scl(scale).add(translate);
                    v.setNormal(b1[0]-0.5f, b1[1]-0.5f, b1[2]-0.5f, b1[3]-0.5f);
                    v.addTexCoord(b2[0], b2[1]);
                }
                break;
                
            case 0x5E7F202D: // st001-m95-monbase
                // 28
                for (int i = 0; i < vertices.length; i++) {
                    float[] b0 = DataFormat.D3DDECLTYPE_FLOAT3.read(ds);      // px py pz (12)
                    ds.skip(16);
                    IVertex v = vertices[i] = new IVertex(b0[0], b0[1], b0[2]);
                    v.scl(scale).add(translate);
                }
                break;
               
            case 0x49B4F029: // RER om151a, om190e
            case 0x49B4F02A: // DCM4 st000-m00-base
                // 28
                for (int i = 0; i < vertices.length; i++) {
                    float[] b0 = DataFormat.D3DDECLTYPE_FLOAT3.read(ds);      // pz py pz (12)
                    float[] b1 = DataFormat.D3DDECLTYPE_UBYTE4N.read(ds);     // nx ny nz nw (4)
                    float[] b2 = DataFormat.D3DDECLTYPE_UBYTE4N.read(ds);     // tx ty tz tw (4) (unchecked)
                    float[] b3 = DataFormat.D3DDECLTYPE_FLOAT16_2.read(ds);   // u1 v1 (4)
                    ds.skip(4); // TODO
                    IVertex v = vertices[i] = new IVertex(b0[0], b0[1], b0[2]);
                    v.scl(scale).add(translate);
                    v.setNormal(b1[0]-0.5f, b1[1]-0.5f, b1[2]-0.5f, b1[3]-0.5f);
                    v.setTangent(b2[0]-0.5f, b2[1]-0.5f, b2[2]-0.5f, b2[3]-0.5f);
                    v.addTexCoord(b3[0], b3[1]);
                }
                break;

            case 0x14D40020:
            case 0x14D40021: // DMC4
                // 28
                for (int i = 0; i < vertices.length; i++) {
                    float[] b0 = DataFormat.D3DDECLTYPE_SHORT4N.read(ds);     // px py pz w1 (8)
                    float[] b1 = DataFormat.D3DDECLTYPE_UBYTE4N.read(ds);     // nx ny nz nw (4)
                    float[] b2 = DataFormat.D3DDECLTYPE_UBYTE4N.read(ds);     // tx ty tz tw (4)
                    float[] b3 = DataFormat.D3DDECLTYPE_UBYTE4.read(ds);      // b1 b2 b3 b4 (4)
                    float[] b4 = DataFormat.D3DDECLTYPE_FLOAT16_2.read(ds);   // u1 v1 (4)
                    float[] b5 = DataFormat.D3DDECLTYPE_FLOAT16_2.read(ds);   // w2 w3 (4)
                    IVertex v = vertices[i] = new IVertex(b0[0], b0[1], b0[2]);
                    v.scl(scale).add(translate);
                    v.setNormal(b1[0]-0.5f, b1[1]-0.5f, b1[2]-0.5f, b1[3]-0.5f);
                    v.setTangent(b2[0]-0.5f, b2[1]-0.5f, b2[2]-0.5f, b2[3]-0.5f);
                    v.addTexCoord(b4[0], b4[1]);
                    v.addBone(b3[0], b0[3]);
                    v.addBone(b3[1], b5[0]);
                    v.addBone(b3[2], b5[1]);
                    v.addBone(b3[3], 1-b0[3]-b5[0]-b5[1]);
                }
                break;
                
            case 0x00D40020: // RER2 fig09 mod
                // 28
                for (int i = 0; i < vertices.length; i++) {
                    vertices[i] = new IVertex();
                    ds.skip(28);
                }
                break;
                
            case 0xA320C016:
            case 0xA320C017: // DMC4 em000
                // 28
                for (int i = 0; i < vertices.length; i++) {
                    float[] b0 = DataFormat.D3DDECLTYPE_SHORT4N.read(ds);     // px py pz pw (8)
                    float[] b1 = DataFormat.D3DDECLTYPE_UBYTE4N.read(ds);     // nx ny nz nw (4)
                    float[] b2 = DataFormat.D3DDECLTYPE_UBYTE4N.read(ds);     // tx ty tz tw (4)
                    ds.skip(4); // TODO: binormals or color?
                    float[] b4 = DataFormat.D3DDECLTYPE_FLOAT16_2.read(ds);   // u1 v1 (4)
                    float[] b5 = DataFormat.D3DDECLTYPE_FLOAT16_2.read(ds);   // u2 v2 (4)
                    IVertex v = vertices[i] = new IVertex(b0[0], b0[1], b0[2]);
                    v.scl(scale).add(translate);
                    v.setNormal(b1[0]-0.5f, b1[1]-0.5f, b1[2]-0.5f, b1[3]-0.5f);
                    v.setTangent(b2[0]-0.5f, b2[1]-0.5f, b2[2]-0.5f, b2[3]-0.5f);
                    v.addTexCoord(b4[0], b4[1]);
                    v.addTexCoord(b5[0], b5[1]);
                }
                break;
                
            case 0xD877801B:
                // 32
                for (int i = 0; i < vertices.length; i++) {
                    float[] b0 = DataFormat.D3DDECLTYPE_SHORT4.read(ds);      // px py pz b1 (8)
                    float[] b1 = DataFormat.D3DDECLTYPE_UBYTE4N.read(ds);     // nx ny nz nw (4)
                    float[] b2 = DataFormat.D3DDECLTYPE_UBYTE4N.read(ds);     // tx ty tz tw (4)
                    float[] b3 = DataFormat.D3DDECLTYPE_FLOAT16_2.read(ds);   // u1 v1 (4)
                    float[] b4 = DataFormat.D3DDECLTYPE_FLOAT16_2.read(ds);   // u2 v2 (4)
                    float[] b5 = DataFormat.D3DDECLTYPE_FLOAT16_2.read(ds);   // u3 v3 (4)
                    ds.skip(4); // TODO
                    IVertex v = vertices[i] = new IVertex(b0[0]*f, b0[1]*f, b0[2]*f);
                    v.scl(scale).add(translate);
                    v.setNormal(b1[0]-0.5f, b1[1]-0.5f, b1[2]-0.5f, b1[3]-0.5f);
                    v.setTangent(b2[0]-0.5f, b2[1]-0.5f, b2[2]-0.5f, b2[3]-0.5f);
                    v.addTexCoord(b3[0], b3[1]);
                    v.addTexCoord(b4[0], b4[1]);
                    v.addTexCoord(b5[0], b5[1]);
                    v.addBone(b0[3], 1);
                }
                break;
                
            case 0xB86DE02A: // RER om180d
            case 0xB86DE02B: // DCM4 st000-m00-base
                // 32
                for (int i = 0; i < vertices.length; i++) {
                    float[] b0 = DataFormat.D3DDECLTYPE_FLOAT3.read(ds);      // px py pz (12)
                    float[] b1 = DataFormat.D3DDECLTYPE_UBYTE4N.read(ds);     // nx ny nz nw (4)
                    float[] b2 = DataFormat.D3DDECLTYPE_UBYTE4N.read(ds);     // tx ty tz tw (4)
                    float[] b3 = DataFormat.D3DDECLTYPE_FLOAT16_2.read(ds);   // u1 v1 (4)
                    ds.skip(8); // TODO
                    IVertex v = vertices[i] = new IVertex(b0[0], b0[1], b0[2]);
                    v.scl(scale).add(translate);
                    v.setNormal(b1[0]-0.5f, b1[1]-0.5f, b1[2]-0.5f, b1[3]-0.5f);
                    v.setTangent(b2[0]-0.5f, b2[1]-0.5f, b2[2]-0.5f, b2[3]-0.5f);
                    v.addTexCoord(b3[0], b3[1]);
                }
                break;
                
            case 0xBB424024:
            case 0xBB424025: // DMC4 em000
                // 36
                for (int i = 0; i < vertices.length; i++) {
                    float[] b0 = DataFormat.D3DDECLTYPE_SHORT4N.read(ds);     // px py pz w1 (8)
                    float[] b1 = DataFormat.D3DDECLTYPE_UBYTE4N.read(ds);     // nz ny nz nw (4)
                    float[] b2 = DataFormat.D3DDECLTYPE_UBYTE4N.read(ds);     // w2 w3 w4 w5 (4)
                    float[] b3 = DataFormat.D3DDECLTYPE_UBYTE4.read(ds);      // b1 b2 b3 b4 (4)
                    float[] b4 = DataFormat.D3DDECLTYPE_UBYTE4.read(ds);      // b5 b6 b7 b8 (4)
                    float[] b5 = DataFormat.D3DDECLTYPE_FLOAT16_2.read(ds);   // u1 v1 (4)
                    float[] b6 = DataFormat.D3DDECLTYPE_FLOAT16_2.read(ds);   // w6 w7 (4)
                    ds.skip(4); // TODO: vertex color?
                    IVertex v = vertices[i] = new IVertex(b0[0], b0[1], b0[2]);
                    v.scl(scale).add(translate);
                    v.setNormal(b1[0]-0.5f, b1[1]-0.5f, b1[2]-0.5f, b1[3]-0.5f);
                    v.addTexCoord(b5[0], b5[1]);
                    v.addBone(b3[0], b0[3]);
                    v.addBone(b3[1], b2[0]);
                    v.addBone(b3[2], b2[1]);
                    v.addBone(b3[3], b2[2]);
                    v.addBone(b4[0], b2[3]);
                    v.addBone(b4[1], b6[0]);
                    v.addBone(b4[2], b6[1]);
                    v.addBone(b4[3], 1-b0[3]-b2[0]-b2[1]-b2[2]-b2[3]-b6[0]-b6[1]);
                }
                break;
                
            case 0xB392101F: // RER2 wp1800
                // 36 (SkinnedCharacter04)
                for (int i = 0; i < vertices.length; i++) {
                    float[] b0 = DataFormat.D3DDECLTYPE_SHORT4N.read(ds);   // px py pz w1 (8)
                    float[] b1 = DataFormat.D3DDECLTYPE_UBYTE4N.read(ds);   // nz ny nz nw (4)
                    float[] b2 = DataFormat.D3DDECLTYPE_UBYTE4N.read(ds);   // tx ty tz tw (4)
                    float[] b3 = DataFormat.D3DDECLTYPE_FLOAT16_2.read(ds); // b1 b2 (4)
                    float[] b4 = DataFormat.D3DDECLTYPE_FLOAT16_2.read(ds); // u1 v1 (4)
                    float[] b5 = DataFormat.D3DDECLTYPE_FLOAT16_2.read(ds); // u2 v2 (4)
                    ds.skip(8);
                    IVertex v = vertices[i] = new IVertex(b0[0], b0[1], b0[2]);
                    v.scl(scale).add(translate);
                    v.setNormal(b1[0]-0.5f, b1[1]-0.5f, b1[2]-0.5f, b1[3]-0.5f);
                    v.setTangent(b2[0]-0.5f, b2[1]-0.5f, b2[2]-0.5f, b2[3]-0.5f);
                    v.addBone(b3[0], b0[3]);
                    v.addBone(b3[1], 1-b0[3]);
                    v.addTexCoord(b4[0], b4[1]);
                    v.addTexCoord(b5[0], b5[1]);
                }
                break;
                
            case 0x64593023:
                // 40
                for (int i = 0; i < vertices.length; i++) {
                    float[] b0 = DataFormat.D3DDECLTYPE_SHORT4N.read(ds);     // px py pz w1 (8)
                    float[] b1 = DataFormat.D3DDECLTYPE_UBYTE4N.read(ds);     // nx ny nz nw (4)
                    float[] b2 = DataFormat.D3DDECLTYPE_UBYTE4N.read(ds);     // tx ty tz tw (4)
                    float[] b3 = DataFormat.D3DDECLTYPE_UBYTE4.read(ds);      // b1 b2 b3 b4 (4)
                    float[] b4 = DataFormat.D3DDECLTYPE_FLOAT16_2.read(ds);   // u1 v1 (4)
                    float[] b5 = DataFormat.D3DDECLTYPE_FLOAT16_2.read(ds);   // w2 w3 (4)
                    ds.skip(4); // TODO
                    float[] b7 = DataFormat.D3DDECLTYPE_FLOAT16_2.read(ds);   // u2 v2 (4)
                    ds.skip(4); // TODO
                    IVertex v = vertices[i] = new IVertex(b0[0], b0[1], b0[2]);
                    v.scl(scale).add(translate);
                    v.setNormal(b1[0]-0.5f, b1[1]-0.5f, b1[2]-0.5f, b1[3]-0.5f);
                    v.setTangent(b2[0]-0.5f, b2[1]-0.5f, b2[2]-0.5f, b2[3]-0.5f);
                    v.addTexCoord(b4[0], b4[1]);
                    v.addTexCoord(b7[0], b7[1]);
                    v.addBone(b3[0], b0[3]);
                    v.addBone(b3[1], b5[0]);
                    v.addBone(b3[2], b5[1]);
                    v.addBone(b3[3], 1-b0[3]-b5[0]-b5[1]);
                }
                break;
                
            case 0x2F55C03D: // RE1 pl0b mod
                // 64
                for (int i = 0; i < vertices.length; i++) {
                    float[] b0 = DataFormat.D3DDECLTYPE_SHORT4N.read(ds);     // px py pz w1 (8)
                    float[] b1 = DataFormat.D3DDECLTYPE_UBYTE4N.read(ds);     // nx ny nz nw (4)
                    float[] b2 = DataFormat.D3DDECLTYPE_UBYTE4N.read(ds);     // tx ty tz tw (4)
                    float[] b3 = DataFormat.D3DDECLTYPE_UBYTE4.read(ds);      // b1 b2 b3 b4 (4)
                    float[] b4 = DataFormat.D3DDECLTYPE_FLOAT16_2.read(ds);   // u1 v1 (4)
                    float[] b5 = DataFormat.D3DDECLTYPE_FLOAT16_2.read(ds);   // w2 w3 (4)
                    ds.skip(36); // TODO
                    IVertex v = vertices[i] = new IVertex(b0[0], b0[1], b0[2]);
                    v.scl(scale).add(translate);
                    v.setNormal(b1[0]-0.5f, b1[1]-0.5f, b1[2]-0.5f, b1[3]-0.5f);
                    v.setTangent(b2[0]-0.5f, b2[1]-0.5f, b2[2]-0.5f, b2[3]-0.5f);
                    v.addTexCoord(b4[0], b4[1]);
                    v.addBone(b3[0], b0[3]);
                    v.addBone(b3[1], b5[0]);
                    v.addBone(b3[2], b5[1]);
                    v.addBone(b3[3], 1-b0[3]-b5[0]-b5[1]);
                }
                break;
                
            default:
                throw new UnsupportedOperationException(String.format("Unsupported vertex format 0x%08X (%2$d)", vertexFormat, vertexStride));
        }
        meshAsModel.setVertices(vertices);
    }
}
