/*
 * Copyright (c) 2003-2009 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jmex.model.ogrexml;

import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.VBOInfo;
import com.jme.scene.state.RenderState;
import com.jmex.model.ogrexml.anim.*;

public final class MeshCloner {

    public static final void setVBO(Node sourceMesh){
        for (Spatial child : sourceMesh.getChildren()){
            OgreMesh sourceSubmesh = (OgreMesh) child;
            VBOInfo info = new VBOInfo(true);

            // disable vertex and normal VBOs because those
            // will be updated every frame by the animation controller
            info.setVBOVertexEnabled(false);
            info.setVBONormalEnabled(false);
            info.setVBOIndexEnabled(false);

            sourceSubmesh.setVBOInfo(info);
        }
    }

    public static final void setDL(Node sourceMesh){
        sourceMesh.lockTransforms();
        sourceMesh.lockMeshes();
    }

    public static final void setLODLevel(Node sourceMesh, int level){
        for (Spatial child : sourceMesh.getChildren()){
            OgreMesh sourceSubmesh = (OgreMesh) child;
            sourceSubmesh.setLodLevel(level);
        }
    }

    public static void cloneSpatial(Spatial source, Spatial target){
        target.setName(source.getName());

        for (int i = 0; i < RenderState.RS_MAX_STATE; i++){
            target.setRenderState(source.getRenderState(i));
        }

        target.setCollisionMask(target.getCollisionMask());

        target.setLocalTranslation(source.getLocalTranslation().clone());
        target.setLocalRotation(source.getLocalRotation().clone());
        target.setLocalScale(source.getLocalScale().clone());

        target.setLocks(source.getLocks());
        target.setZOrder(source.getZOrder());

        target.setCullHint(source.getCullHint());
        target.setTextureCombineMode(source.getTextureCombineMode());
        target.setLightCombineMode(source.getLightCombineMode());
        target.setRenderQueueMode(source.getRenderQueueMode());
        target.setNormalsMode(source.getNormalsMode());
        target.setModelBound(source.getWorldBound().clone(null));
    }

    public static void cloneMesh(TriMesh source, TriMesh target){
        cloneSpatial(source, target);

        // buffers
        target.setVertexBuffer(source.getVertexBuffer());
        target.setColorBuffer(source.getColorBuffer());
        target.setBinormalBuffer(source.getBinormalBuffer());
        target.setTangentBuffer(source.getTangentBuffer());
        target.setNormalBuffer(source.getNormalBuffer());
        target.setTextureCoords(source.getTextureCoords());

        // misc data
        target.setVertexCount(source.getVertexCount());
        target.setCastsShadows(source.isCastsShadows());
        target.setHasDirtyVertices(source.hasDirtyVertices());
        target.setDefaultColor(source.getDefaultColor());
        target.setDisplayListID(source.getDisplayListID());
        target.setVBOInfo(source.getVBOInfo());

        // trimesh specific
        target.setMode(source.getMode());
        target.setIndexBuffer(source.getIndexBuffer());

        // update from model data
        target.updateModelBound();
    }

    public static final Node cloneMesh(Node sourceMesh){
        Node targetMesh = new Node(sourceMesh.getName());
        cloneSpatial(sourceMesh, targetMesh);

        OgreMesh[] submeshes = new OgreMesh[sourceMesh.getQuantity()];
        int index = 0;
        for (Spatial child : sourceMesh.getChildren()){
            OgreMesh sourceSubmesh = (OgreMesh) child;
            OgreMesh targetSubmesh = new OgreMesh(sourceSubmesh.getName());
            cloneMesh(sourceSubmesh, targetSubmesh);
            targetSubmesh.cloneFromMesh(sourceSubmesh);
            targetMesh.attachChild(targetSubmesh);
            submeshes[index] = targetSubmesh;
            index++;
        }

        if (sourceMesh.getControllerCount() > 0){
            MeshAnimationController sourceControl = (MeshAnimationController) sourceMesh.getController(0);
            MeshAnimationController targetControl = new MeshAnimationController(submeshes, sourceControl);
            targetMesh.addController(targetControl);
        }

        return targetMesh;
    }

}
