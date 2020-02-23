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
package com.openitvn.format.mod.v15;

import com.badlogic.gdx.math.Vector3;
import com.openitvn.unicore.data.DataStream;
import com.openitvn.unicore.world.IGeometry;
import com.openitvn.unicore.world.ISkeleton;
import com.openitvn.format.mod.DependencyHelper;
import com.openitvn.format.mod.MTBoundingBox;
import com.openitvn.format.mod.MTBoundingSphere;
import com.openitvn.format.mod.MTBone;
import com.openitvn.format.mod.MTModel;
import com.openitvn.format.mod.MTModelReader;
import com.openitvn.format.tex.MTTexture;
import com.openitvn.unicore.world.resource.IModel;
import java.util.HashMap;

/**
 *
 * @author Thinh Pham
 */
public class MTModelReader15 extends MTModelReader {
    
    // structure
    private short numBones, numMeshes, numMaterials;
    private int numVertices, numIndices, numEdges;
    private int vertexBufferSize, vertexBuffer2Size;
    private int numTextures, numGroups, numBoneMaps;
    private int boneBufferOffset, groupBufferOffset, textureBufferOffset, meshBufferOffset, vertexBufferOffset, vertexBufferEnd, indexBufferOffset;
    private int reserved1;
    private int reserved2;
    private MTBoundingSphere sphere;
    private MTBoundingBox box;
    private int unk1, unk2, unk3, unk4, unk5, unk6, unk7, unk8, unk9, unk10, unk11;
    private int reserved3;
    private byte[] unk12;
    
    public MTModelReader15(short ver, short rev) {
        super(ver, rev);
    }
    
    @Override
    protected void decode(MTModel world, DataStream ds) {
        world.maxBones = 32; // each map have 32 bones
        numBones = ds.getShort();
        numMeshes = ds.getShort();
        numMaterials = ds.getShort();
        numVertices = ds.getInt();
        numIndices = ds.getInt();
        numEdges = ds.getInt();
        vertexBufferSize = ds.getInt();
        vertexBuffer2Size = ds.getInt();
        numTextures = ds.getInt();
        numGroups = ds.getInt();
        numBoneMaps = ds.getInt();
        boneBufferOffset = ds.getInt();
        groupBufferOffset = ds.getInt();
        textureBufferOffset = ds.getInt();
        meshBufferOffset = ds.getInt();
        vertexBufferOffset = ds.getInt();
        vertexBufferEnd = ds.getInt();
        indexBufferOffset = ds.getInt();
        reserved1 = ds.getInt();
        reserved2 = ds.getInt();
        sphere = new MTBoundingSphere(ds);
        box = new MTBoundingBox(ds);
        unk1 = ds.getInt();
        unk2 = ds.getInt();
        unk3 = ds.getInt();
        unk4 = ds.getInt();
        unk5 = ds.getInt();
        unk6 = ds.getInt();
        unk7 = ds.getInt();
        unk8 = ds.getInt();
        unk9 = ds.getInt();
        unk10 = ds.getInt();
        unk11 = ds.getInt();
        reserved3 = ds.getInt();
        unk12 = (unk8 != 0) ? new byte[boneBufferOffset - 176] : new byte[0];
        ds.get(unk12);
        
        Vector3 scale = new Vector3(box.max).sub(box.min);
        
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
                b.readInverseTransform(ds);
            }
        }
        
        // read boneMap
        if (skel != null && numBoneMaps > 0) {
            // skip boneMap header
            ds.skip(256);
            // read boneMap data
            long boneMapOffset = ds.position();
            skel.boneMap = new short[numBoneMaps][];
            for (int i = 0; i < numBoneMaps; i++) {
                ds.position(boneMapOffset + i * 36);
                int numLocalBones = ds.getInt();
                skel.boneMap[i] = new short[numLocalBones];
                for (int j = 0; j < numLocalBones; j++)
                    skel.boneMap[i][j] = ds.getUByte();
            }
        }
        
        // read groups
        ds.position(groupBufferOffset);
        readGroup(world, ds, numGroups);
        
        // build textures by request external tex files
        ds.position(textureBufferOffset);
        for (int i = 0; i < numTextures; i++) {
            String texName = ds.readFixedString(64);
            MTTexture tex = DependencyHelper.requestTEX(ds, texName);
            textures.add(tex);
        }
        // read material buffers
        for (int i = 0; i < numMaterials; i++) {
            MTMaterial15 mat = new MTMaterial15(ds, textures);
            materials.add(mat);
            mat.setName(String.format("material_%1$03d", i));
        }
        
        // read mesh headers
        ds.position(meshBufferOffset);
        HashMap<String, IModel> modelMap = new HashMap();
        MTMesh15[] meshes = new MTMesh15[numMeshes];
        for (int i = 0; i < numMeshes; i++) {
            MTMesh15 mesh = meshes[i] = new MTMesh15(ds);
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
                geo.setLayerIndex(lod < 255 ? lod : -1);
                geo.skeleton = skel;
                geo.attach(getGroupByIndex(mesh.groupIndex));
            }
            MTMaterial15 mat = (MTMaterial15) materials.get(mesh.materialIndex);
            if (mat.isRenderable()) {
                mod.meshes.add(mesh);
            }
        }
        
        // read meshes vertex buffer
        for (MTMesh15 mesh : meshes) {
            ds.position(vertexBufferOffset);
            mesh.readVertexBuffer(ds, box.min, scale);
            ds.position(indexBufferOffset);
            mesh.readIndexBuffer(ds);
        }
    }
}
