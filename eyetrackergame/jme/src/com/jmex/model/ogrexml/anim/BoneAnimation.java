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

import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Bone animation updates each of it's tracks with the skeleton and time
 * to apply the animation.
 */
public final class BoneAnimation implements Serializable, Savable {

    private static final long serialVersionUID = 1L;

    private String name;
    private float length;

    private BoneTrack[] tracks;

    BoneAnimation(String name, float length, BoneTrack[] tracks){
        this.name = name;
        this.length = length;
        this.tracks = tracks;
    }

    /**
     * Serialization-only. Do not use.
     */
    public BoneAnimation(){
    }

    public String getName(){
        return name;
    }

    public float getLength(){
        return length;
    }

    public BoneTrack[] getTracks(){
        return tracks;
    }

    void setTime(float time, Skeleton skeleton, float weight, ArrayList<Integer> affectedBones){
        for (int i = 0; i < tracks.length; i++){
            tracks[i].setTime(time, skeleton, weight, affectedBones);
        }
    }


    public void write(JMEExporter e) throws IOException {
        OutputCapsule out = e.getCapsule(this);
        out.write(name, "name", null);
        out.write(length, "length", 0f);
        out.write(tracks, "tracks", null);
    }

    public void read(JMEImporter i) throws IOException {
        InputCapsule in = i.getCapsule(this);
        name = in.readString("name", null);
        length = in.readFloat("length", 0f);

        Savable[] sav = in.readSavableArray("tracks", null);
        if (sav != null){
            tracks = new BoneTrack[sav.length];
            System.arraycopy(sav, 0, tracks, 0, sav.length);
        }
    }

    public Class getClassTag() {
        return BoneAnimation.class;
    }

}
