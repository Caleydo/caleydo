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

package com.jme.util.lwjgl;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.OpenGLException;
import org.lwjgl.opengl.Util;

import com.jme.image.Texture;
import com.jme.image.Image.Format;
import com.jme.scene.state.lwjgl.records.TextureStateRecord;
import com.jme.util.geom.BufferUtils;

public class LWJGLTextureUpdater {
    private static IntBuffer idBuff = BufferUtils.createIntBuffer(16);
    private static boolean glTexSubImage2DSupported = true;

    public static void updateTexture(Texture texture, ByteBuffer data, int w, int h, Format format) {
        int dataFormat = TextureStateRecord.getGLDataFormat(format);
        int pixelFormat = TextureStateRecord.getGLPixelFormat(format);
        
        idBuff.clear();
        GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D, idBuff);
        int oldTex = idBuff.get();

        GL11.glBindTexture( GL11.GL_TEXTURE_2D, texture.getTextureId() );
        GL11.glPixelStorei( GL11.GL_UNPACK_ALIGNMENT, 1 );

        if (glTexSubImage2DSupported) {
            GL11.glTexSubImage2D( GL11.GL_TEXTURE_2D, 0,
                    0, 0, w, h, pixelFormat,
                    GL11.GL_UNSIGNED_BYTE, data );

            try {
                Util.checkGLError();
            } catch ( OpenGLException e ) {
                glTexSubImage2DSupported = false;
                updateTexture(texture, data, w, h, format);
            }
        } else {
            GL11.glTexImage2D( GL11.GL_TEXTURE_2D, 0,
                    dataFormat, w,
                    h, 0, pixelFormat,
                    GL11.GL_UNSIGNED_BYTE, data );
        }

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, oldTex);
    }
}
