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
package com.openitvn.format.mod.v21x;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.openitvn.unicore.data.DataStream;
import com.openitvn.unicore.world.IGeometry;
import com.openitvn.unicore.world.ISkeleton;
import com.openitvn.format.mod.MTBone;
import com.openitvn.format.mod.MTBoundingBox;
import com.openitvn.format.mod.MTBoundingSphere;
import com.openitvn.format.mod.MTGroup;
import com.openitvn.format.mod.MTMod;
import com.openitvn.format.mod.MTModReader;
import com.openitvn.format.mrl.MTMaterialHash;
import com.openitvn.format.mrl.MTMaterialLibrary;
import com.openitvn.maintain.Logger;
import com.openitvn.unicore.data.DataFormat;
import com.openitvn.util.FileHelper;
import java.io.IOException;

/**
 *
 * @author Thinh Pham
 */
public class MTModReader21x extends MTModReader {
    
    // structure
    private short numBones, numMeshes, numMaterials;
    private int numVertices, numIndices, numEdges;
    private int vertexBufferSize;
    private int pad1;
    private int groupCount;
    private int boneBufferOffset, groupBufferOffset, materialBufferOffset, meshBufferOffset, vertexBufferOffset, indexBufferOffset;
    private int fileSize; // excluded fourCC
    private MTBoundingSphere sphere;
    private MTBoundingBox box;
    private int unk2, unk3, unk4, unk5, unk6;
    private byte[] unk7;
    
    public MTModReader21x(short ver, short rev) {
        super(ver, rev);
    }
    
    @Override
    protected void decode(MTMod world, DataStream ds) {
        // read materials from mrl file
        requestMRL(ds);
        
        // begin header
        world.maxBones = numBones = ds.getShort(); // no boneMap
        numMeshes = ds.getShort();
        numMaterials = ds.getShort();
        numVertices = ds.getInt();
        numIndices = ds.getInt();
        numEdges = ds.getInt();
        vertexBufferSize = ds.getInt();
        pad1 = ds.getInt();
        groupCount = ds.getInt();
        boneBufferOffset = ds.getInt();
        groupBufferOffset = ds.getInt();
        materialBufferOffset = ds.getInt();
        meshBufferOffset = ds.getInt();
        vertexBufferOffset = ds.getInt();
        indexBufferOffset = ds.getInt();
        fileSize = ds.getInt();
        // end header
        
        // begin bounding objects
        float sphereRadius = ds.getFloat();
        float[] spherePosition = DataFormat.D3DDECLTYPE_FLOAT3.read(ds);
        sphere = new MTBoundingSphere(spherePosition, sphereRadius);
        box = new MTBoundingBox(ds);
        Vector3 translate = new Vector3();
        Vector3 scale = new Vector3(1, 1, 1);
        // end bounding objects
        
        unk2 = ds.getInt();
        unk3 = ds.getInt();
        unk4 = ds.getInt();
        unk5 = ds.getInt();
        unk6 = ds.getInt();
        
        // read bones
        ISkeleton skel = null;
        if (numBones > 0) {
            skel = new ISkeleton("skeleton");
            skel.attach(world);
            MTBone[] bones = new MTBone[numBones];
            ds.position(boneBufferOffset);
            for (int i = 0; i < numBones; i++) {
                MTBone b = bones[i] = new MTBone(i, ds);
                if (b.parentId == 255)
                    b.attach(skel);
                else
                    b.attach(bones[b.parentId]);
            }
            for (MTBone b : bones) {
                b.readLocalTransform(ds);
            }
            for (MTBone b : bones) {
                if (b.parentId == 255) {
                    float[] m = new float[16];
                    for (int i = 0; i < 16; i++)
                        m[i] = ds.getFloat();
                    new Matrix4(m).getScale(scale);
                    translate.set(box.min);
                } else {
                    ds.skip(16 * 4);
                }
            }
        }
        
        // read groups
        ds.position(groupBufferOffset);
        MTGroup[] groups = new MTGroup[groupCount];
        for (int i = 0; i < groupCount; i++) {
            MTGroup gr = groups[i] = new MTGroup(ds);
            gr.attach(world);
        }
        // end groups
        
        // materials
        String[] matNames = new String[numMaterials];
        Integer[] matHashes = new Integer[numMaterials];
        ds.position(materialBufferOffset);
        if (version <= 210) {
            // version 210 read names directly, take hashes from hashMap
            for (int i = 0; i < numMaterials; i++) {
                matNames[i] = ds.readFixedString(128);
                matHashes[i] = MTMaterialHash.getHash(matNames[i]);
                if (matHashes[i] == null)
                    System.err.println(matNames[i]);
            }
        } else {
            // version 211 read hashes directly, take names from hashMap
            for (int i = 0; i < numMaterials; i++) {
                matHashes[i] = ds.getInt();
                matNames[i] = MTMaterialHash.getName(matHashes[i]);
            }
        }
        
        // read meshes
        MTModel21x[] models = new MTModel21x[numMeshes];
        ds.position(meshBufferOffset);
        for (int i = 0; i < numMeshes; i++) {
            MTModel21x mod = models[i] = new MTModel21x(ds);
            // set material name
            String matName = matNames[mod.matIndex];
            Integer matHash = matHashes[mod.matIndex];
            String modName;
            if (matName == null) {
                matName = String.format("0x%1$08x", matHash);
                modName = String.format("part_%1$03d (%2$s)", i, matName);
            } else if (matHash == null) {
                modName = String.format("part_%1$03d (%2$s)", i, matName);
            } else {
                modName = String.format("part_%1$03d", i);
            }
            mod.setName(modName);
            mod.setMaterialName(matName);
            // add mesh to world
            if (mod.isRenderable()) {
                IGeometry geo = new IGeometry();
                geo.setName(mod.getName());
                if (mod.getLevelOfDetail() < 255)
                    geo.setLayerIndex(mod.getLevelOfDetail());
                geo.skeleton = skel;
                for (MTGroup gr : groups) {
                    if (mod.groupIndex == gr.index) {
                        geo.attach(gr);
                        break;
                    }
                }
            }
            world.addModel(mod);
        }
        
        // read meshes vertex buffer
        for (MTModel21x mod : models) {
            ds.position(vertexBufferOffset);
            mod.readVertexBuffer(ds, translate, scale);
            ds.position(indexBufferOffset);
            mod.readIndexBuffer(ds);
        }
    }
    
    private void requestMRL(DataStream ds) {
        String mrlPath = FileHelper.cropFileExt(ds.getLastPath()) + ".mrl";
        String mrlName = FileHelper.getFileName(mrlPath);
        try (DataStream ms = ds.getExternal(mrlPath)) {
            MTMaterialLibrary mrl = new MTMaterialLibrary();
            mrl.decode(ms);
            materials.addAll(mrl.materials);
            textures.addAll(mrl.textures);
            Logger.printNormal("Loaded dependency " + mrlName);
        } catch (IOException ex) {
            Logger.printError("Missed dependency " + mrlName);
        } catch (UnsupportedOperationException ex) {
            Logger.printError("Missed dependency %1$s (%2$s)", mrlName, ex.getMessage());
        }
    }
}
