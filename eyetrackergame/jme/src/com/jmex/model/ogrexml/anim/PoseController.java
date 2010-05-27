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

package com.jmex.model.ogrexml.anim;

import java.nio.FloatBuffer;
import java.util.Map;

import com.jme.scene.Controller;
import com.jme.scene.TriMesh;

/**
 * Non-functional class, do not use.
 */
@Deprecated
public class PoseController extends Controller {

        private static final long serialVersionUID = 1301423379092925973L;
        private Map<String, Pose> poseMap;
    private FloatBuffer originalVertBuf;
    private TriMesh target;

    private Pose curPose = null;
    private float blendTime = 0f;
    private float blendTimeMax = 0f;


//    public PoseController(Map<TriMesh, List<Pose>> trimeshPoses){
//        poseMap = poses;
//
//        this.target = target;
//
//        // create a copy of the original vertex buffer
//        FloatBuffer meshVertBuf = target.getVertexBuffer();
//        originalVertBuf = BufferUtils.createFloatBuffer(meshVertBuf.capacity());
//        meshVertBuf.rewind();
//        originalVertBuf.rewind();
//        originalVertBuf.put(meshVertBuf);
//        originalVertBuf.flip();
//    }
//
    @Override
    public void update(float time) {
        if (!isActive())
            return;

        if (curPose != null && blendTime > 0f){
            float blend = blendTime / blendTimeMax;

            resetToBind();
            curPose.apply(blend, target.getVertexBuffer());

            blendTime -= time;
            if (blendTime < 0f)
                blendTime = 0f;
        }
    }

    private void resetToBind(){
        FloatBuffer meshVertBuf = target.getVertexBuffer();
        meshVertBuf.rewind();
        meshVertBuf.put(originalVertBuf);
    }

    public void reset(){
        resetToBind();
        curPose = null;
        blendTime = 0f;
        blendTimeMax = 0f;
    }

    public void blendToPose(String name, float time){
        curPose = poseMap.get(name);

        if (curPose == null)
            throw new NullPointerException("Expected post name '" + name
                    + "' is missing");

        blendTime = time;
        blendTimeMax = time;
    }

    public void setPose(String name){
        curPose = poseMap.get(name);

        if (curPose == null)
            throw new NullPointerException("Expected post name '" + name
                    + "' is missing");

        blendTime = 0f;
        blendTimeMax = 0f;

        resetToBind();
        curPose.apply(1f, target.getVertexBuffer());
    }


}
