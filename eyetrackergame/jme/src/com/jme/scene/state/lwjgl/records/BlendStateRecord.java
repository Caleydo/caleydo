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
package com.jme.scene.state.lwjgl.records;

import com.jme.renderer.ColorRGBA;
import com.jme.scene.state.StateRecord;

public class BlendStateRecord extends StateRecord {
    public boolean blendEnabled = false;
    public boolean testEnabled = false;

    // RGB or primary
    public int srcFactorRGB = -1;
    public int dstFactorRGB = -1;
    public int blendEqRGB = -1;

    // Alpha (if supported)
    public int srcFactorAlpha = -1;
    public int dstFactorAlpha = -1;
    public int blendEqAlpha = -1;

    public int alphaFunc = -1;
    public float alphaRef = -1;

    public ColorRGBA blendColor = new ColorRGBA(-1, -1, -1, -1);
    
    @Override
    public void invalidate() {
        super.invalidate();
        
        blendEnabled = false;
        testEnabled = false;

        srcFactorRGB = -1;
        dstFactorRGB = -1;
        blendEqRGB = -1;

        srcFactorAlpha = -1;
        dstFactorAlpha = -1;
        blendEqAlpha = -1;

        alphaFunc = -1;
        alphaRef = -1;

        blendColor.set(-1, -1, -1, -1);
    }
}
