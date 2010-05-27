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

package com.jme.animation;

import java.io.IOException;
import java.io.Serializable;

import com.jme.math.Vector3f;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;


/**
 * The Influence class defines a pairing between a vertex and a bone. This
 * pairing is given a weight to define how much the bone affects the vertex.
 */
public class BoneInfluence implements Serializable, Savable {

    private static final long serialVersionUID = 5904348001742899839L;
    
    public float weight;
    public Bone bone;
    public String boneId;
    public Vector3f vOffset;
    public Vector3f nOffset;

    public BoneInfluence() {
    }

    public BoneInfluence(Bone boneIndex, float weight) {
        this.bone = boneIndex;
        this.weight = weight;
    }
    
    public void assignBone(Bone b) {
        if(boneId == null || b == null) {
            return;
        }
        
        if(boneId.equals(b.getName())) {
            bone = b;
        } else {
            for(int i = 0; i < b.getQuantity(); i++) {
                if(b.getChild(i) instanceof Bone) {
                    this.assignBone((Bone)b.getChild(i));
                }
            }
        }
    }
    
    @Override
    public boolean equals(Object arg0) {
    		if (!(arg0 instanceof BoneInfluence)) return false;
    		
    		BoneInfluence other = (BoneInfluence)arg0;
    		
    		if (boneId != null) {
    			if (!boneId.equals(other.boneId)) return false;
    		} else if (other.boneId != null) return false;
    		
    		return true;
    }

    public void write(JMEExporter e) throws IOException {
        OutputCapsule cap = e.getCapsule(this);
        cap.write(weight, "weight", 0);
        cap.write(bone, "bone", null);
        cap.write(boneId, "boneId", null);
        cap.write(vOffset, "vOffset", null);
        cap.write(nOffset, "nOffset", null);
    }

    public void read(JMEImporter e) throws IOException {
        InputCapsule cap = e.getCapsule(this);
        weight = cap.readFloat("weight", 0);
        bone = (Bone)cap.readSavable("bone", null);
        boneId = cap.readString("boneId", null);
        nOffset = (Vector3f)cap.readSavable("nOffset", null);
        vOffset = (Vector3f)cap.readSavable("vOffset", null);
    }
    
    public Class getClassTag() {
        return this.getClass();
    }
}
