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
import java.util.logging.Logger;

import javax.media.opengl.GL;

import com.jme.renderer.ColorRGBA;
import com.jme.scene.state.StateRecord;
import com.jme.util.geom.BufferUtils;

public class MaterialStateRecord extends StateRecord {
    private static final Logger logger = Logger.getLogger(MaterialStateRecord.class.getName());

    public ColorRGBA frontAmbient = new ColorRGBA(-1,-1,-1,-1);
    public ColorRGBA frontDiffuse = new ColorRGBA(-1,-1,-1,-1);
    public ColorRGBA frontSpecular = new ColorRGBA(-1,-1,-1,-1);
    public ColorRGBA frontEmissive = new ColorRGBA(-1,-1,-1,-1);
    public ColorRGBA backAmbient = new ColorRGBA(-1,-1,-1,-1);
    public ColorRGBA backDiffuse = new ColorRGBA(-1,-1,-1,-1);
    public ColorRGBA backSpecular = new ColorRGBA(-1,-1,-1,-1);
    public ColorRGBA backEmissive = new ColorRGBA(-1,-1,-1,-1);
    public float shininess = Float.NEGATIVE_INFINITY;
    public int colorMaterial = Integer.MIN_VALUE;
    public int materialFace = Integer.MIN_VALUE;
    public int face = Integer.MIN_VALUE;
    public FloatBuffer tempColorBuff = BufferUtils.createColorBuffer(1);


    public boolean isSetColor(int face, int glMatColor, ColorRGBA color, MaterialStateRecord record) {
        if (face == GL.GL_FRONT) {
            switch (glMatColor) {
                case GL.GL_AMBIENT:
                    return color.r == frontAmbient.r && color.g == frontAmbient.g && color.b == frontAmbient.b && color.a == frontAmbient.a;
                case GL.GL_DIFFUSE:
                    return color.r == frontDiffuse.r && color.g == frontDiffuse.g && color.b == frontDiffuse.b && color.a == frontDiffuse.a;
                case GL.GL_SPECULAR:
                    return color.r == frontSpecular.r && color.g == frontSpecular.g && color.b == frontSpecular.b && color.a == frontSpecular.a;
                case GL.GL_EMISSION:
                    return color.r == frontEmissive.r && color.g == frontEmissive.g && color.b == frontEmissive.b && color.a == frontEmissive.a;
                default:
                    logger.warning("bad isSetColor");
            }
        } else if (face == GL.GL_FRONT_AND_BACK) {
            switch (glMatColor) {
                case GL.GL_AMBIENT:
                    return color.r == frontAmbient.r && color.g == frontAmbient.g && color.b == frontAmbient.b && color.a == frontAmbient.a &&
                           color.r == backAmbient.r && color.g == backAmbient.g && color.b == backAmbient.b && color.a == backAmbient.a;
                case GL.GL_DIFFUSE:
                    return color.r == frontDiffuse.r && color.g == frontDiffuse.g && color.b == frontDiffuse.b && color.a == frontDiffuse.a &&
                           color.r == backDiffuse.r && color.g == backDiffuse.g && color.b == backDiffuse.b && color.a == backDiffuse.a;
                case GL.GL_SPECULAR:
                    return color.r == frontSpecular.r && color.g == frontSpecular.g && color.b == frontSpecular.b && color.a == frontSpecular.a &&
                           color.r == backSpecular.r && color.g == backSpecular.g && color.b == backSpecular.b && color.a == backSpecular.a;
                case GL.GL_EMISSION:
                    return color.r == frontEmissive.r && color.g == frontEmissive.g && color.b == frontEmissive.b && color.a == frontEmissive.a &&
                           color.r == backEmissive.r && color.g == backEmissive.g && color.b == backEmissive.b && color.a == backEmissive.a;
                default:
                    logger.warning("bad isSetColor");
            }
        } else if (face == GL.GL_BACK) {
            switch (glMatColor) {
                case GL.GL_AMBIENT:
                    return color.r == backAmbient.r && color.g == backAmbient.g && color.b == backAmbient.b && color.a == backAmbient.a;
                case GL.GL_DIFFUSE:
                    return color.r == backDiffuse.r && color.g == backDiffuse.g && color.b == backDiffuse.b && color.a == backDiffuse.a;
                case GL.GL_SPECULAR:
                    return color.r == backSpecular.r && color.g == backSpecular.g && color.b == backSpecular.b && color.a == backSpecular.a;
                case GL.GL_EMISSION:
                    return color.r == backEmissive.r && color.g == backEmissive.g && color.b == backEmissive.b && color.a == backEmissive.a;
                default:
                    logger.warning("bad isSetColor");
            }
        }
        return false;
    }


    public void setColor(int face, int glMatColor, ColorRGBA color) {
        if (face == GL.GL_FRONT || face == GL.GL_FRONT_AND_BACK) {
            switch (glMatColor) {
                case GL.GL_AMBIENT:
                    frontAmbient.set(color);
                    break;
                case GL.GL_DIFFUSE:
                    frontDiffuse.set(color);
                    break;
                case GL.GL_SPECULAR:
                    frontSpecular.set(color);
                    break;
                case GL.GL_EMISSION:
                    frontEmissive.set(color);
                    break;
                default:
                    logger.warning("bad setColor");
            }
        }
        if (face == GL.GL_BACK || face == GL.GL_FRONT_AND_BACK) {
            switch (glMatColor) {
                case GL.GL_AMBIENT:
                    backAmbient.set(color);
                    break;
                case GL.GL_DIFFUSE:
                    backDiffuse.set(color);
                    break;
                case GL.GL_SPECULAR:
                    backSpecular.set(color);
                    break;
                case GL.GL_EMISSION:
                    backEmissive.set(color);
                    break;
                default:
                    logger.warning("bad setColor");
            }
        }
    }


    public void resetColorsForCM(int face, int glMat) {
        if (face == GL.GL_FRONT || face == GL.GL_FRONT_AND_BACK) {
            switch (glMat) {
                case GL.GL_AMBIENT:
                    frontAmbient.set(-1, -1, -1, -1);
                    break;
                case GL.GL_DIFFUSE:
                    frontDiffuse.set(-1, -1, -1, -1);
                    break;
                case GL.GL_AMBIENT_AND_DIFFUSE:
                    frontAmbient.set(-1, -1, -1, -1);
                    frontDiffuse.set(-1, -1, -1, -1);
                    break;
                case GL.GL_EMISSION:
                    frontEmissive.set(-1, -1, -1, -1);
                    break;
                case GL.GL_SPECULAR:
                    frontSpecular.set(-1, -1, -1, -1);
                    break;
            }
        }
        if (face == GL.GL_BACK || face == GL.GL_FRONT_AND_BACK) {
            switch (glMat) {
                case GL.GL_AMBIENT:
                    backAmbient.set(-1, -1, -1, -1);
                    break;
                case GL.GL_DIFFUSE:
                    backDiffuse.set(-1, -1, -1, -1);
                    break;
                case GL.GL_AMBIENT_AND_DIFFUSE:
                    backAmbient.set(-1, -1, -1, -1);
                    backDiffuse.set(-1, -1, -1, -1);
                    break;
                case GL.GL_EMISSION:
                    backEmissive.set(-1, -1, -1, -1);
                    break;
                case GL.GL_SPECULAR:
                    backSpecular.set(-1, -1, -1, -1);
                    break;
            }
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();

        frontAmbient.set(-1,-1,-1,-1);
        frontDiffuse.set(-1,-1,-1,-1);
        frontSpecular.set(-1,-1,-1,-1);
        frontEmissive.set(-1,-1,-1,-1);
        backAmbient.set(-1,-1,-1,-1);
        backDiffuse.set(-1,-1,-1,-1);
        backSpecular.set(-1,-1,-1,-1);
        backEmissive.set(-1,-1,-1,-1);
        shininess = Float.NEGATIVE_INFINITY;
        colorMaterial = Integer.MIN_VALUE;
        materialFace = Integer.MIN_VALUE;
        face = Integer.MIN_VALUE;
    }
}
