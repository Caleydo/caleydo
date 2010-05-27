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

import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;

/**
 * Contains a list of transforms and times for each keyframe.
 */
public final class BoneTrack implements Serializable, Savable {
    
    private static final long serialVersionUID = 1L;

    private static final Quaternion IDENTITY = new Quaternion(0, 0, 0, 1);

    /**
     * Bone index in the skeleton which this track effects.
     */
    private int targetBoneIndex;

    /**
     * Transforms and times for track.
     */
    private Vector3f[] translations;
    private Quaternion[] rotations;
    private float[] times;

    // temp vectors for interpolation
    private transient final Vector3f tempV = new Vector3f();
    private transient final Quaternion tempQ = new Quaternion();

    public BoneTrack(int targetBoneIndex, float[] times, Vector3f[] translations, Quaternion[] rotations){
        this.targetBoneIndex = targetBoneIndex;

        if (times.length == 0)
            throw new RuntimeException("BoneTrack with no keyframes!");

        assert (times.length == translations.length) && (times.length == rotations.length);

        this.times = times;
        this.translations = translations;
        this.rotations = rotations;
    }

    /**
     * Serialization-only. Do not use.
     */
    public BoneTrack(){
    }

    /**
     * Modify the bone which this track modifies in the skeleton to contain
     * the correct animation transforms for a given time.
     * The transforms can be interpolated in some method from the keyframes.
     */
    public void setTime(float time, Skeleton skeleton, float weight, ArrayList<Integer> affectedBones) {
        if (affectedBones != null && !affectedBones.contains(targetBoneIndex)) {
			return;
		}
    	
    	Bone target = skeleton.getBone(targetBoneIndex);

        int lastFrame = times.length - 1;
        if (time < 0 || times.length == 1){
            tempQ.set(rotations[0]);
            tempV.set(translations[0]);
        }else if (time >= times[lastFrame]){
            tempQ.set(rotations[lastFrame]);
            tempV.set(translations[lastFrame]);
        }else{
            int startFrame = 0;
            int endFrame   = 1;
            // use len-1 so we never overflow the array
            for (int i = 0; i < times.length-1; i++){
                if (times[i] < time){
                    startFrame = i;
                    endFrame   = i + 1;
                }
            }
            float blend =       (time - times[startFrame])
                         / (times[endFrame] - times[startFrame]);

            tempQ.slerp(rotations[startFrame], rotations[endFrame], blend);
            tempV.interpolate(translations[startFrame], translations[endFrame], blend);
        }

        if (weight != 1f){
            tempQ.slerp(IDENTITY, 1f - weight);
            tempV.multLocal(weight);
        }

        target.setAnimTransforms(tempV, tempQ);
    }

    public void write(JMEExporter ex) throws IOException {
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(targetBoneIndex, "boneIndex", 0);
        oc.write(translations, "translations", null);
        oc.write(rotations, "rotations", null);
        oc.write(times, "times", null);
    }

    public void read(JMEImporter im) throws IOException {
        InputCapsule ic = im.getCapsule(this);
        targetBoneIndex = ic.readInt("boneIndex", 0);

        Savable[] sav = ic.readSavableArray("translations", null);
        if (sav != null){
            translations = new Vector3f[sav.length];
            System.arraycopy(sav, 0, translations, 0, sav.length);
        }

        sav = ic.readSavableArray("rotations", null);
        if (sav != null){
            rotations = new Quaternion[sav.length];
            System.arraycopy(sav, 0, rotations, 0, sav.length);
        }
        times = ic.readFloatArray("times", null);
    }


    public Class getClassTag() {
        return BoneTrack.class;
    }

	public int getTargetBoneIndex() {
		return targetBoneIndex;
	}

	public Vector3f[] getTranslations() {
		return translations;
	}

	public Quaternion[] getRotations() {
		return rotations;
	}

	public float[] getTimes() {
		return times;
	}
}
