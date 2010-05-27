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
import com.jme.scene.state.WireframeState;
import com.jme.scene.state.jogl.records.LineRecord;
import com.jme.scene.state.jogl.records.WireframeStateRecord;
import com.jme.system.DisplaySystem;

/**
 * <code>JOGLWireframeState</code> subclasses WireframeState to use the JOGL
 * API to access OpenGL. If the state is enabled, wireframe mode is used,
 * otherwise solid fill is used.
 *
 * @author Mark Powell
 * @author Joshua Slack - reworked for StateRecords.
 * @author Steve Vaughan - JOGL port
 * @version $Id: JOGLWireframeState.java 4131 2009-03-19 20:15:28Z blaine.dev $
 */
public class JOGLWireframeState extends WireframeState {

    private static final long serialVersionUID = 1L;

    /**
     * <code>set</code> sets the polygon mode to line or fill depending on if
     * the state is enabled or not.
     *
     * @see com.jme.scene.state.WireframeState#apply()
     */
    public void apply() {
        // ask for the current state record
        RenderContext<?> context = DisplaySystem.getDisplaySystem()
                .getCurrentContext();
        WireframeStateRecord record = (WireframeStateRecord) context.getStateRecord(StateType.Wireframe);
        LineRecord lineRecord = (LineRecord) context.getLineRecord();
        context.currentStates[StateType.Wireframe.ordinal()] = this;

        if (isEnabled()) {
            lineRecord.applyLineWidth(lineWidth);
            lineRecord.applyLineSmooth(isAntialiased());
            lineRecord.applyLineStipple(1, (short)0xFFFF);
            if (!lineRecord.isValid())
                lineRecord.validate();

            switch (face) {
                case Front:
                    applyPolyMode(GL.GL_LINE, GL.GL_FILL, record);
                    break;
                case Back:
                    applyPolyMode(GL.GL_FILL, GL.GL_LINE, record);
                    break;
                case FrontAndBack:
                default:
                    applyPolyMode(GL.GL_LINE, GL.GL_LINE, record);
                    break;
            }
        } else {
            applyPolyMode(GL.GL_FILL, GL.GL_FILL, record);
        }

        if (!record.isValid())
            record.validate();
    }

    private void applyPolyMode(int frontMode, int backMode, WireframeStateRecord record) {
        final GL gl = GLU.getCurrentGL();

        if (record.isValid()) {
            if (frontMode == backMode && (record.frontMode != frontMode || record.backMode != backMode)) {
                gl.glPolygonMode(GL.GL_FRONT_AND_BACK, frontMode);
                record.frontMode = frontMode;
                record.backMode = backMode;
            } else if (frontMode != backMode) {
                if (record.frontMode != frontMode) {
                    gl.glPolygonMode(GL.GL_FRONT, frontMode);
                    record.frontMode = frontMode;
                }
                if (record.backMode != backMode) {
                    gl.glPolygonMode(GL.GL_BACK, backMode);
                    record.backMode = backMode;
                }
            }

        } else {
            if (frontMode == backMode) {
                gl.glPolygonMode(GL.GL_FRONT_AND_BACK, frontMode);
            } else if (frontMode != backMode) {
                gl.glPolygonMode(GL.GL_FRONT, frontMode);
                gl.glPolygonMode(GL.GL_BACK, backMode);
            }
            record.frontMode = frontMode;
            record.backMode = backMode;
        }
    }

    @Override
    public WireframeStateRecord createStateRecord() {
        return new WireframeStateRecord();
    }
}
