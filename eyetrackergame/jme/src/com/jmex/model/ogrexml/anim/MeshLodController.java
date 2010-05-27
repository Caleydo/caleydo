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

import com.jme.renderer.Camera;
import com.jme.scene.Controller;
import com.jme.system.DisplaySystem;

public class MeshLodController extends Controller {
 static final long serialVersionUID = 1L;
    private MeshAnimationController animControl;

    public MeshLodController(MeshAnimationController animControl){
        this.animControl = animControl;
    }

    @Override
    public void update(float time) {
        OgreMesh[] targets = animControl.getMeshList();
        int maxLod = targets[0].getLodLevelCount() - 1;

        Camera cam = DisplaySystem.getDisplaySystem().getRenderer().getCamera();
        targets[0].updateWorldVectors();
        float dist = cam.getLocation().distance(targets[0].getWorldTranslation());
        int level = Math.round(dist / 500f);
        if (level < 0)
            level = 0;
        else if (level > maxLod)
            level = maxLod;

        final float desiredTPF = 1f / 60f;
        // 1 > if went above budget, <= 1 if all OK
        final float TPFratio = time / desiredTPF;

        // adaptive frameskip -> increase frameskipping when framerate is low
        animControl.setFrameSkip( Math.round( (dist / 200f) * TPFratio ) );
        for (OgreMesh target: targets){
            target.setLodLevel(level);
        }
    }


}
