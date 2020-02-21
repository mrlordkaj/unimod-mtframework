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

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.openitvn.unicore.data.DataStream;
import com.openitvn.unicore.world.IGeometry;
import com.openitvn.unicore.world.ISkeleton;
import com.openitvn.format.mod.MTBone;
import com.openitvn.format.mod.MTBoundingBox;
import com.openitvn.format.mod.MTBoundingSphere;
import com.openitvn.format.mod.MTModel;
import com.openitvn.format.mod.MTModelReader;
import com.openitvn.format.mrl.MTMaterialHash;
import com.openitvn.format.mrl.MTMaterialLibrary;
import com.openitvn.maintain.Logger;
import com.openitvn.unicore.data.DataFormat;
import com.openitvn.unicore.world.resource.IModel;
import com.openitvn.helper.FileHelper;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author Thinh Pham
 */
public class MTModelReader21 extends MTModelReader {
    
    // structure
    private short numBones, numMeshes, numMaterials;
    private int numVertices, numIndices, numEdges;
    private int vertexBufferSize;
    private int unk1;
    private int numGroups;
    private int boneBufferOffset, groupBufferOffset, materialBufferOffset, meshBufferOffset, vertexBufferOffset, indexBufferOffset;
    private int fileSize; // excluded fourCC
    private MTBoundingSphere sphere;
    private MTBoundingBox box;
    private int unk2, unk3, unk4, unk5, unk6;
    private byte[] unk7;
    
    public MTModelReader21(short ver, short rev) {
        super(ver, rev);
    }
    
    @Override
    protected void decode(MTModel world, DataStream ds) {
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
        unk1 = ds.getInt();
        numGroups = ds.getInt();
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
        readGroup(world, ds, numGroups);
        
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
        ds.position(meshBufferOffset);
        HashMap<String, IModel> modelMap = new HashMap();
        MTMesh21[] meshes = new MTMesh21[numMeshes];
        for (int i = 0; i < numMeshes; i++) {
            MTMesh21 mesh = meshes[i] = new MTMesh21(ds);
            // set material name
            String matName = matNames[mesh.matIndex];
            if (matName == null) {
                Integer matHash = matHashes[mesh.matIndex];
                matName = String.format("0x%1$08x", matHash);
            }
            mesh.materialName = matName;
            short lod = mesh.levelOfDetail;
            String modName = String.format("Part_%03d_LOD_%03d", mesh.groupIndex, lod);
            IModel mod = modelMap.get(modName);
            if (mod == null) {
                // add new model when not found
                mod = new IModel(modName);
                world.registerModel(mod, lod);
                modelMap.put(modName, mod);
                // add geometry for instance
                IGeometry geo = new IGeometry(modName);
                geo.layerIndex = (lod < 255 ? lod : -1);
                geo.skeleton = skel;
                geo.attach(getGroupByIndex(mesh.groupIndex));
            }
            if (mesh.isRenderable()) {
                mod.meshes.add(mesh);
            }
        }
        
        // read meshes vertex buffer
        for (MTMesh21 mesh : meshes) {
            ds.position(vertexBufferOffset);
            mesh.readVertexBuffer(ds, translate, scale);
            ds.position(indexBufferOffset);
            mesh.readIndexBuffer(ds);
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
