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

import com.jme.renderer.ColorRGBA;
import com.jme.renderer.RenderContext;
import com.jme.renderer.jogl.JOGLContextCapabilities;
import com.jme.renderer.jogl.JOGLRenderer;
import com.jme.scene.state.FogState;
import com.jme.scene.state.jogl.records.FogStateRecord;
import com.jme.system.DisplaySystem;

/**
 * <code>JOGLFogState</code> subclasses the fog state using the JOGL API to
 * set the OpenGL fog state.
 *
 * @author Mark Powell
 * @author Joshua Slack - reworked for StateRecords.
 * @author Steve Vaughan - JOGL port
 * @version $Id: JOGLFogState.java 4458 2009-07-02 08:39:10Z julien.gouesse $
 */
public class JOGLFogState extends FogState {
    private static final long serialVersionUID = 2L;

    private static boolean inited = false;
    
    
    public JOGLFogState() {
        this( ( ( JOGLRenderer ) DisplaySystem.getDisplaySystem().
        getRenderer()).getContextCapabilities() );
    }
    
    /**
     * Constructor instantiates a new <code>JOGLFogState</code> object with
     * default values.
     *
     */
    public JOGLFogState(JOGLContextCapabilities caps) {
        super();
        
        if (!inited) {
            // Check for support of fog coords.
            supportsFogCoords = supportsFogCoordsDetected = caps.GL_EXT_fog_coord;
            inited = true;
        }
    }

    /**
     * <code>set</code> sets the OpenGL fog values if the state is enabled.
     *
     * @see com.jme.scene.state.RenderState#apply()
     */
    public void apply() {
        final GL gl = GLU.getCurrentGL();

        // ask for the current state record
        RenderContext<?> context = DisplaySystem.getDisplaySystem()
                .getCurrentContext();
        FogStateRecord record = (FogStateRecord) context.getStateRecord(StateType.Fog);
        context.currentStates[StateType.Fog.ordinal()] = this;

        if (isEnabled()) {
            enableFog(true, record);

            if (record.isValid()) {
                if (record.fogStart != start) {
                    gl.glFogf(GL.GL_FOG_START, start);
                    record.fogStart = start;
                }
                if (record.fogEnd != end) {
                    gl.glFogf(GL.GL_FOG_END, end);
                    record.fogEnd = end;
                }
                if (record.density != density) {
                    gl.glFogf(GL.GL_FOG_DENSITY, density);
                    record.density = density;
                }
            } else {
                gl.glFogf(GL.GL_FOG_START, start);
                record.fogStart = start;
                gl.glFogf(GL.GL_FOG_END, end);
                record.fogEnd = end;
                gl.glFogf(GL.GL_FOG_DENSITY, density);
                record.density = density;
            }

            applyFogColor(getColor(), record);
            applyFogMode(densityFunction, record);
            applyFogHint(quality, record);
            applyFogSource(source, record);
        } else {
            enableFog(false, record);
        }

        if (!record.isValid())
            record.validate();
    }

    private void enableFog(boolean enable, FogStateRecord record) {
        final GL gl = GLU.getCurrentGL();

        if (record.isValid()) {
            if (enable && !record.enabled) {
                gl.glEnable(GL.GL_FOG);
                record.enabled = true;
            } else if (!enable && record.enabled) {
                gl.glDisable(GL.GL_FOG);
                record.enabled = false;
            }
        } else {
            if (enable) {
                gl.glEnable(GL.GL_FOG);
            } else {
                gl.glDisable(GL.GL_FOG);
            }
            record.enabled = enable;
        }
    }

    private void applyFogColor(ColorRGBA color, FogStateRecord record) {
        final GL gl = GLU.getCurrentGL();

        if (!record.isValid() || !color.equals(record.fogColor)) {
            record.fogColor.set(color);
            record.colorBuff.clear();
            record.colorBuff.put(record.fogColor.r).put(record.fogColor.g).put(
                    record.fogColor.b).put(record.fogColor.a);
            record.colorBuff.flip();
            gl.glFogfv(GL.GL_FOG_COLOR, record.colorBuff); // TODO Check for float
        }
    }

    private void applyFogSource(CoordinateSource source, FogStateRecord record) {
        final GL gl = GLU.getCurrentGL();

        if (supportsFogCoords) {
            if (!record.isValid() || !source.equals(record.source)) {
                if (source == CoordinateSource.Depth) {
                    gl.glFogi(GL.GL_FOG_COORDINATE_SOURCE_EXT, GL.GL_FRAGMENT_DEPTH_EXT);
                } else {
                    gl.glFogi(GL.GL_FOG_COORDINATE_SOURCE_EXT, GL.GL_FOG_COORDINATE_EXT);
                }
            }
        }
    }

    private void applyFogMode(DensityFunction densityFunction, FogStateRecord record) {
        final GL gl = GLU.getCurrentGL();

        int glMode = 0;
        switch (densityFunction) {
            case Exponential:
                glMode = GL.GL_EXP;
                break;
            case Linear:
                glMode = GL.GL_LINEAR;
                break;
            case ExponentialSquared:
                glMode = GL.GL_EXP2;
                break;
        }

        if (!record.isValid() || record.fogMode != glMode) {
            gl.glFogi(GL.GL_FOG_MODE, glMode);
            record.fogMode = glMode;
        }
    }

    private void applyFogHint(Quality quality, FogStateRecord record) {
        final GL gl = GLU.getCurrentGL();

        int glHint = 0;
        switch (quality) {
            case PerVertex:
                glHint = GL.GL_FASTEST;
                break;
            case PerPixel:
                glHint = GL.GL_NICEST;
                break;
        }

        if (!record.isValid() || record.fogHint != glHint) {
            gl.glHint(GL.GL_FOG_HINT, glHint);
            record.fogHint = glHint;
        }
    }

    @Override
    public FogStateRecord createStateRecord() {
        return new FogStateRecord();
    }
}
