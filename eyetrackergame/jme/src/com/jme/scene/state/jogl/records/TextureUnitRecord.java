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

import java.util.Arrays;

import com.jme.image.Texture;
import com.jme.image.Texture.ApplyMode;
import com.jme.image.Texture.CombinerFunctionAlpha;
import com.jme.image.Texture.CombinerFunctionRGB;
import com.jme.image.Texture.CombinerOperandAlpha;
import com.jme.image.Texture.CombinerOperandRGB;
import com.jme.image.Texture.CombinerScale;
import com.jme.image.Texture.CombinerSource;
import com.jme.math.Matrix4f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.state.StateRecord;

/**
 * Represents a texture unit in opengl
 */
public class TextureUnitRecord extends StateRecord {
    public boolean enabled[] = new boolean[Texture.Type.values().length];
    public Matrix4f texMatrix = new Matrix4f();
    public Vector3f texScale = new Vector3f();
    public int boundTexture = -1;
    public ApplyMode envMode = null;
    public CombinerScale envRGBScale = null;
    public CombinerScale envAlphaScale = null;
    public ColorRGBA blendColor = new ColorRGBA(-1, -1, -1, -1);
    public CombinerFunctionRGB rgbCombineFunc = null;
    public CombinerFunctionAlpha alphaCombineFunc = null;
    public CombinerSource combSrcRGB0 = null, combSrcRGB1 = null, combSrcRGB2 = null;
    public CombinerOperandRGB combOpRGB0 = null, combOpRGB1 = null, combOpRGB2 = null;
    public CombinerSource combSrcAlpha0 = null, combSrcAlpha1 = null, combSrcAlpha2 = null;
    public CombinerOperandAlpha combOpAlpha0 = null, combOpAlpha1 = null, combOpAlpha2 = null;
    public boolean identityMatrix = true;

    public boolean textureGenQ = false, textureGenR = false, textureGenS = false, textureGenT = false;
    public int textureGenQMode = -1, textureGenRMode = -1, textureGenSMode = -1, textureGenTMode = -1;

    public TextureUnitRecord() {
    }

    @Override
    public void invalidate() {
        super.invalidate();

        Arrays.fill(enabled, false);
        texMatrix.loadIdentity();
        texScale.zero();
        boundTexture = -1;
        envMode = null;
        envRGBScale = null;
        envAlphaScale = null;
        blendColor.set(-1, -1, -1, -1);
        rgbCombineFunc = null;
        alphaCombineFunc = null;
        combSrcRGB0 = null;
        combSrcRGB1 = null;
        combSrcRGB2 = null;
        combOpRGB0 = null;
        combOpRGB1 = null;
        combOpRGB2 = null;
        combSrcAlpha0 = null;
        combSrcAlpha1 = null;
        combSrcAlpha2 = null;
        combOpAlpha0 = null;
        combOpAlpha1 = null;
        combOpAlpha2 = null;
        identityMatrix = false;

        textureGenQ = false;
        textureGenR = false;
        textureGenS = false;
        textureGenT = false;
        textureGenQMode = -1;
        textureGenRMode = -1;
        textureGenSMode = -1;
        textureGenTMode = -1;
    }
}
