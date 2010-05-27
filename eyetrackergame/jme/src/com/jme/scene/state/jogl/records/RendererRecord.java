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

import java.nio.IntBuffer;
import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import com.jme.renderer.ColorRGBA;
import com.jme.scene.state.StateRecord;
import com.jme.util.geom.BufferUtils;

public class RendererRecord extends StateRecord {
    private int matrixMode = -1;
    private int currentElementVboId = -1, currentVboId = -1;
    private boolean matrixValid;
    private boolean vboValid;
    private boolean elementVboValid;
    private transient ColorRGBA tempColor = new ColorRGBA();
    private ArrayList<Integer> vboCleanupCache = new ArrayList<Integer>();
    private IntBuffer idBuff = BufferUtils.createIntBuffer(16);

    public void switchMode(int mode) {
        final GL gl = GLU.getCurrentGL();

        if (!matrixValid || this.matrixMode != mode) {
            gl.glMatrixMode(mode);
            this.matrixMode = mode;
            matrixValid = true;
        }
    }

    public void setCurrentColor(ColorRGBA setTo) {
final GL gl = GLU.getCurrentGL();

//        if (!colorValid || !currentColor.equals(setTo)) {
            gl.glColor4f(setTo.r, setTo.g, setTo.b, setTo.a);
//            currentColor.set(setTo);
//            colorValid = true;
//        }
    }

    public void setBoundVBO(int id) {
        final GL gl = GLU.getCurrentGL();

        if (!vboValid || currentVboId != id) {
            gl.glBindBufferARB(
                    GL.GL_ARRAY_BUFFER_ARB, id);
            currentVboId = id;
            vboValid = true;
        }
    }

    public void setBoundElementVBO(int id) {
        final GL gl = GLU.getCurrentGL();

        if (!elementVboValid || currentElementVboId != id) {
            gl.glBindBufferARB(GL.GL_ELEMENT_ARRAY_BUFFER_ARB, id);
            currentElementVboId = id;
            elementVboValid = true;
        }
    }

    public void setCurrentColor(float red, float green, float blue, float alpha) {
        tempColor.set(red, green, blue, alpha);
        setCurrentColor(tempColor);
    }

    @Override
    public void invalidate() {
        invalidateMatrix();
        invalidateVBO();
    }

    @Override
    public void validate() {
        // ignore  - validate per item or locally
    }

    public void invalidateMatrix() {
        matrixValid = false;
        matrixMode = -1;
    }

    public void invalidateVBO() {
        vboValid = false;
        elementVboValid = false;
        currentElementVboId = currentVboId = -1;
    }

    public int makeVBOId() {
        final GL gl = GLU.getCurrentGL();

        idBuff.rewind();
        gl.glGenBuffersARB(idBuff.limit(),idBuff); // TODO Check <size>
        int vboID = idBuff.get(0);
        vboCleanupCache.add(vboID);
        return vboID;
    }

    public void deleteVBOId(int id) {
        final GL gl = GLU.getCurrentGL();

        idBuff.rewind();
        idBuff.put(id).flip();
        gl.glDeleteBuffersARB(idBuff.limit(),idBuff); // TODO Check <size>
        vboCleanupCache.remove(Integer.valueOf(id));
    }

    public void cleanupVBOs() {
        for (int x = vboCleanupCache.size(); --x >= 0;) {
            deleteVBOId(vboCleanupCache.get(x));
        }
        vboCleanupCache.clear();
    }
}
