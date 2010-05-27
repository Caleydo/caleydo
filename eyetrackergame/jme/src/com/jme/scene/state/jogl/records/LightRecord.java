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

import com.jme.math.Matrix4f;
import com.jme.math.Quaternion;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.state.StateRecord;

public class LightRecord extends StateRecord {
    public ColorRGBA ambient = new ColorRGBA(-1, -1, -1, -1);
    public ColorRGBA diffuse = new ColorRGBA(-1, -1, -1, -1);
    public ColorRGBA specular = new ColorRGBA(-1, -1, -1, -1);
    private float constant = -1;
    private float linear = -1;
    private float quadratic = -1;
    private float spotExponent = -1;
    private float spotCutoff = -1;
    private boolean enabled = false;

    // NB: Vector4F would be more appropriate than Quaternion...
    public Quaternion position = new Quaternion();
    public Matrix4f modelViewMatrix = new Matrix4f();

    private boolean attenuate;

    public boolean isAttenuate() {
        return attenuate;
    }

    public void setAttenuate(boolean attenuate) {
        this.attenuate = attenuate;
    }

    public float getConstant() {
        return constant;
    }

    public void setConstant(float constant) {
        this.constant = constant;
    }

    public float getLinear() {
        return linear;
    }

    public void setLinear(float linear) {
        this.linear = linear;
    }

    public float getQuadratic() {
        return quadratic;
    }

    public void setQuadratic(float quadratic) {
        this.quadratic = quadratic;
    }

    public float getSpotExponent() {
        return spotExponent;
    }

    public void setSpotExponent(float exponent) {
        this.spotExponent = exponent;
    }

    public float getSpotCutoff() {
        return spotCutoff;
    }

    public void setSpotCutoff(float spotCutoff) {
        this.spotCutoff = spotCutoff;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void invalidate() {
        super.invalidate();

        ambient.set(-1, -1, -1, -1);
        diffuse.set(-1, -1, -1, -1);
        specular.set(-1, -1, -1, -1);
        constant = -1;
        linear = -1;
        quadratic = -1;
        spotExponent = -1;
        spotCutoff = -1;
        enabled = false;

        position.set(-1, -1, -1, -1);
        modelViewMatrix.loadIdentity();
    }
}
