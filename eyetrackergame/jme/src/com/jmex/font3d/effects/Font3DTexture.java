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
package com.jmex.font3d.effects;

import java.net.URL;

import com.jme.image.Texture;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jmex.font3d.Font3D;

/**
 * This class will apply a texture to a font. The reason this has to be done
 * this way is because of some locking/unlocking and other internal things of
 * the Font3D/Text3D.
 * 
 * @author emanuel
 */
public class Font3DTexture implements Font3DEffect {
    private TextureState ts = null;

    public Font3DTexture() {
        ts = DisplaySystem.getDisplaySystem().getRenderer()
                .createTextureState();
    }

    public Font3DTexture(TextureState ts) {
        this.ts = ts;
    }

    public Font3DTexture(Texture tex) {
        this();
        ts.setTexture(tex);
    }

    public Font3DTexture(URL texurl) {
        this();
        Texture tex = TextureManager.loadTexture(texurl,
                Texture.MinificationFilter.BilinearNearestMipMap,
                Texture.MagnificationFilter.Bilinear);
        tex.setWrap(Texture.WrapMode.Repeat);
        ts.setTexture(tex);
    }

    public void applyEffect(Font3D font) {
        boolean mesh_locked = font.isMeshLocked();
        if (mesh_locked) {
            font.unlockMesh();
        }

        // Apply the texture state
        font.getRenderNode().setRenderState(ts);

        if (mesh_locked) {
            font.lockMesh();
        }
    }
}
