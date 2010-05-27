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

package com.jme.scene.state.jogl;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import com.jme.renderer.RenderContext;
import com.jme.scene.state.ZBufferState;
import com.jme.scene.state.jogl.records.ZBufferStateRecord;
import com.jme.system.DisplaySystem;

/**
 * <code>JOGLZBufferState</code> subclasses ZBufferState to use the JOGL API
 * to access OpenGL.
 *
 * @author Mark Powell
 * @author Joshua Slack - reworked for StateRecords.
 * @author Steve Vaughan - JOGL port
 * @version $Id: JOGLZBufferState.java 4131 2009-03-19 20:15:28Z blaine.dev $
 */
public class JOGLZBufferState extends ZBufferState {
    private static final long serialVersionUID = 1L;

    /**
     * <code>set</code> turns on the specified depth test specified by the
     * state.
     *
     * @see com.jme.scene.state.RenderState#apply()
     */
    public void apply() {
        // ask for the current state record
        RenderContext<?> context = DisplaySystem.getDisplaySystem()
                .getCurrentContext();
        ZBufferStateRecord record = (ZBufferStateRecord) context.getStateRecord(StateType.ZBuffer);
        context.currentStates[StateType.ZBuffer.ordinal()] = this;

        enableDepthTest(isEnabled(), record);
        if (isEnabled()) {
            int depthFunc = 0;
            switch (getFunction()) {
                case Never:
                    depthFunc = GL.GL_NEVER;
                    break;
                case LessThan:
                    depthFunc = GL.GL_LESS;
                    break;
                case EqualTo:
                    depthFunc = GL.GL_EQUAL;
                    break;
                case LessThanOrEqualTo:
                    depthFunc = GL.GL_LEQUAL;
                    break;
                case GreaterThan:
                    depthFunc = GL.GL_GREATER;
                    break;
                case NotEqualTo:
                    depthFunc = GL.GL_NOTEQUAL;
                    break;
                case GreaterThanOrEqualTo:
                    depthFunc = GL.GL_GEQUAL;
                    break;
                case Always:
                    depthFunc = GL.GL_ALWAYS;
            }
            applyFunction(depthFunc, record);
        }

        enableWrite(isWritable(), record);

        if (!record.isValid())
            record.validate();
    }

    private void enableDepthTest(boolean enable, ZBufferStateRecord record) {
        final GL gl = GLU.getCurrentGL();

        if (enable && (!record.depthTest || !record.isValid())) {
            gl.glEnable(GL.GL_DEPTH_TEST);
            record.depthTest = true;
        } else if (!enable && (record.depthTest || !record.isValid())) {
            gl.glDisable(GL.GL_DEPTH_TEST);
            record.depthTest = false;
        }
    }

    private void applyFunction(int depthFunc, ZBufferStateRecord record) {
        final GL gl = GLU.getCurrentGL();

        if (depthFunc != record.depthFunc || !record.isValid()) {
            gl.glDepthFunc(depthFunc);
            record.depthFunc = depthFunc;
        }
    }

    private void enableWrite(boolean enable, ZBufferStateRecord record) {
        final GL gl = GLU.getCurrentGL();

        if (enable != record.writable || !record.isValid()) {
            gl.glDepthMask(enable);
            record.writable = enable;
        }
    }

    @Override
    public ZBufferStateRecord createStateRecord() {
        return new ZBufferStateRecord();
    }
}
