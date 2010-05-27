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
package com.jme.scene.state.jogl.records;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import com.jme.renderer.ColorRGBA;
import com.jme.scene.state.StateRecord;
import com.jme.util.geom.BufferUtils;

public class LightStateRecord extends StateRecord {
    private ArrayList<LightRecord> lightList = new ArrayList<LightRecord>();
    private int lightMask;
    private int backLightMask;
    private boolean twoSidedOn;
    public ColorRGBA globalAmbient = new ColorRGBA(-1, -1, -1, -1);
    private boolean enabled;
    private boolean localViewer;
    private boolean separateSpecular;

    // buffer for light colors.
    public FloatBuffer lightBuffer = BufferUtils.createColorBuffer(1);

    public int getBackLightMask() {
        return backLightMask;
    }
    public void setBackLightMask(int backLightMask) {
        this.backLightMask = backLightMask;
    }
    public LightRecord getLightRecord(int index) {
        if (lightList.size() <= index) {
            return null;
        }

        return lightList.get(index);
    }

    public void setLightRecord(LightRecord lr, int index) {
        while (lightList.size() <= index) {
            lightList.add(null);
        }

        lightList.set(index, lr);
    }
    public int getLightMask() {
        return lightMask;
    }
    public void setLightMask(int lightMask) {
        this.lightMask = lightMask;
    }
    public boolean isTwoSidedOn() {
        return twoSidedOn;
    }
    public void setTwoSidedOn(boolean twoSidedOn) {
        this.twoSidedOn = twoSidedOn;
    }
    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    public boolean isLocalViewer() {
        return localViewer;
    }
    public void setLocalViewer(boolean localViewer) {
        this.localViewer = localViewer;
    }
    public boolean isSeparateSpecular() {
        return separateSpecular;
    }
    public void setSeparateSpecular(boolean seperateSpecular) {
        this.separateSpecular = seperateSpecular;
    }

    @Override
    public void invalidate() {
        super.invalidate();
        for (LightRecord record : lightList) {
            record.invalidate();
        }

        lightMask = -1;
        backLightMask = -1;
        twoSidedOn = false;
        enabled = false;
        localViewer = false;
        separateSpecular = false;
        globalAmbient.set(-1, -1, -1, -1);
    }

    @Override
    public void validate() {
        super.validate();
        for (LightRecord record : lightList) {
            record.validate();
        }
    }
}
