/*
 * Copyright (C) 2020 Thinh Pham
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
package com.openitvn.format.mod.v15;

import com.badlogic.gdx.math.Vector3;
import com.openitvn.maintain.Logger;
import com.openitvn.unicore.data.DataFormat;
import com.openitvn.unicore.data.DataStream;
import com.openitvn.unicore.world.IMesh;
import com.openitvn.unicore.world.IVertex;
import com.openitvn.unicore.world.WorldHelper;

/**
 *
 * @author Thinh Pham
 */
class MTMesh15 extends IMesh {
    
    final short groupIndex;
    final short materialIndex;
    final byte unk1; // always 1
    final short levelOfDetail; // ubyte
    final byte unk2;
    final byte vertexFormat; // from 0 to 8
    final byte vertexStride;
    final byte vertexFlag; // 0 or 8
    final byte unk3;
    final byte unk4;
    final short vertexCount;
    final short vertexIndexEnd;
    final int indexStart1;
    final int vertexOffset1;
    final int vertexOffset2;
    final int indexPosition;
    final int indexCount;
    final int indexOffset;
    final byte unk5;
    final byte unk6;
    final short indexStart2;
    final byte vertexGroupCount; // weightMapCount????
//    final byte boneMapIndex;
    final int minIndex; // ushort
    final int maxIndex; // ushort
    final short unk7;
    
    MTMesh15(DataStream ds) {
        // header
        groupIndex = ds.getShort();
        materialIndex = ds.getShort();
        unk1 = ds.get();
        levelOfDetail = ds.getUByte();
        unk2 = ds.get();
        vertexFormat = ds.get();
        vertexStride = ds.get();
        vertexFlag = ds.get(); //[0; 8]
        unk3 = ds.get();
        unk4 = ds.get();
        vertexCount = ds.getShort();
        vertexIndexEnd = ds.getShort();
        indexStart1 = ds.getInt();
        vertexOffset1 = ds.getInt();
        vertexOffset2 = ds.getInt();
        indexPosition = ds.getInt();
        indexCount = ds.getInt();
        indexOffset = ds.getInt();
        unk5 = ds.get();
        unk6 = ds.get();
        indexStart2 = ds.getShort();
        vertexGroupCount = ds.get();
        boneMapIndex = ds.get();
        minIndex = ds.getUShort();
        maxIndex = ds.getUShort();
        unk7 = ds.getShort();
        // modify properties
        materialName = String.format("material_%1$03d", materialIndex);
    }
    
    void readVertexBuffer(DataStream ds, Vector3 translate, Vector3 scale) {
        int vertDataOffset = vertexOffset1 + Math.max(indexStart1, indexStart2) * vertexStride;
        ds.position(ds.position() + vertDataOffset);
        IVertex[] vertices = new IVertex[getVertexCount()];
        switch (vertexFormat) {
            case 0: // 32
                for (int i = 0; i < vertices.length; i++) {
                    float[] b0 = DataFormat.D3DDECLTYPE_FLOAT3.read(ds);      // posX posY posZ (12)
                    float[] b1 = DataFormat.D3DDECLTYPE_UBYTE4N.read(ds);     // norX norY norZ norW (4)
                    float[] b2 = DataFormat.D3DDECLTYPE_UBYTE4N.read(ds);     // tanX tanY tanZ tanW (4)
                    float[] b3 = DataFormat.D3DDECLTYPE_FLOAT16_2.read(ds);   // texU1 texV1 (4)
                    float[] b4 = DataFormat.D3DDECLTYPE_FLOAT16_2.read(ds);   // texU2 texV2 (4)
                    float[] b5 = DataFormat.D3DDECLTYPE_FLOAT16_2.read(ds);   // texU3 texV3 (4)
                    IVertex v = vertices[i] = new IVertex(b0[0], b0[1], b0[2]);
                    v.scl(scale).add(translate);
                    v.setNormal(b1[0]-0.5f, b1[1]-0.5f, b1[2]-0.5f, b1[3]-0.5f);
                    v.setTangent(b2[0]-0.5f, b2[1]-0.5f, b2[2]-0.5f, b2[3]-0.5f);
                    v.addTexCoord(b3[0], b3[1]);
                    v.addTexCoord(b4[0], b4[1]);
                    v.addTexCoord(b5[0], b5[1]);
                }
                break;

            case 1: case 2: case 3: case 4: // 32
                for(int i = 0; i < vertices.length; i++) {
                    float[] b0 = DataFormat.D3DDECLTYPE_SHORT4N.read(ds);     // posX posY posZ posW
                    float[] b1 = DataFormat.D3DDECLTYPE_UBYTE4.read(ds);      // bone0 bone1 bone2 bone3
                    float[] b2 = DataFormat.D3DDECLTYPE_UBYTE4N.read(ds);     // weight0 weight1 weight2 weight3
                    float[] b3 = DataFormat.D3DDECLTYPE_UBYTE4N.read(ds);     // norX norY norZ norW
                    float[] b4 = DataFormat.D3DDECLTYPE_UBYTE4N.read(ds);     // tanX tanY tanZ tanW
                    float[] b5 = DataFormat.D3DDECLTYPE_FLOAT16_2.read(ds);   // texU1 texV1
                    float[] b6 = DataFormat.D3DDECLTYPE_FLOAT16_2.read(ds);   // texU2 texV2
                    IVertex v = vertices[i] = new IVertex(b0[0], b0[1], b0[2], b0[3]);
                    v.scl(scale).add(translate);
                    v.setNormal(b3[0]-0.5f, b3[1]-0.5f, b3[2]-0.5f, b3[3]-0.5f);
                    v.setTangent(b4[0]-0.5f, b4[1]-0.5f, b4[2]-0.5f, b4[3]-0.5f);
                    v.addTexCoord(b5[0], b5[1]);
                    v.addTexCoord(b6[0], b6[1]);
                    v.addBone(b1[0], b2[0]);
                    v.addBone(b1[1], b2[1]);
                    v.addBone(b1[2], b2[2]);
                    v.addBone(b1[3], b2[3]);
                }
                break;

            case 5: case 6: case 7: case 8: // 32
                for(int i = 0; i < vertices.length; i++) {
                    float[] b0 = DataFormat.D3DDECLTYPE_SHORT4N.read(ds);     // posX posY posZ posW (8)
                    float[] b1 = DataFormat.D3DDECLTYPE_UBYTE4.read(ds);      // bone0 bone1 bone2 bone3 (4)
                    float[] b2 = DataFormat.D3DDECLTYPE_UBYTE4.read(ds);      // bone4 bone5 bone6 bone7 (4)
                    float[] b3 = DataFormat.D3DDECLTYPE_UBYTE4N.read(ds);     // weight0 weight1 weight2 weight3 (4)
                    float[] b4 = DataFormat.D3DDECLTYPE_UBYTE4N.read(ds);     // weight4 weight5 weight6 weight7 (4)
                    float[] b5 = DataFormat.D3DDECLTYPE_UBYTE4N.read(ds);     // norX norY norZ norW (4)
                    float[] b6 = DataFormat.D3DDECLTYPE_FLOAT16_2.read(ds);   // texU2 texV2 (4)
                    IVertex v = vertices[i] = new IVertex(b0[0], b0[1], b0[2], b0[3]);
                    v.scl(scale).add(translate);
                    v.setNormal(b5[0]-0.5f, b5[1]-0.5f, b5[2]-0.5f, b5[3]-0.5f);
                    v.addTexCoord(b6[0], b6[1]);
                    v.addBone(b1[0], b3[0]);
                    v.addBone(b1[1], b3[1]);
                    v.addBone(b1[2], b3[2]);
                    v.addBone(b1[3], b3[3]);
                    v.addBone(b2[0], b4[0]);
                    v.addBone(b2[1], b4[1]);
                    v.addBone(b2[2], b4[2]);
                    v.addBone(b2[3], b4[3]);
                }
                break;
                
            default:
                vertices = new IVertex[0];
                Logger.printError("Unsupported vertex format 0x%02X", vertexFormat);
                break;
        }
        setVertices(vertices);
    }
    
    void readIndexBuffer(DataStream ds) {
        try {
            int indexDataOffset = indexPosition * 2 + indexOffset * 2;
            int indexStart = Math.max(indexStart1, indexStart2);
            ds.position(ds.position() + indexDataOffset);
            short[] strips = new short[indexCount];
            for (int i = 0; i < indexCount; i++)
                strips[i] = (short)(ds.getShort() - indexStart);
            // convert tri-strip to tri-list
            setIndices(WorldHelper.triangleStrip2TriangleList(strips));
        } catch (IndexOutOfBoundsException ex) {
            // TODO: avoid bug in uOm1303_Truck.arc
//            Logger.printError("Error while reading %1$s", getName());
            setIndices(new short[0]);
        }
    }
    
    private int getVertexCount() {
        if (indexStart2 > indexStart1) {
            return vertexIndexEnd - indexStart2 + 1;
            // TODO: research the content of mesh.vertex_index_start_1 and what it means in this case
            // So far it looks it contains only garbage; all vertices have the same values.
            // It's unknown why they exist for, and why they count for mesh.vertex_count
            // The imported meshes here will have a different mesh count than the original.
        } else {
            return vertexCount;
        }
    }
}
