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

package com.jme.scene.state.lwjgl;

import org.lwjgl.opengl.EXTFogCoord;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

import com.jme.renderer.ColorRGBA;
import com.jme.renderer.RenderContext;
import com.jme.scene.state.FogState;
import com.jme.scene.state.lwjgl.records.FogStateRecord;
import com.jme.system.DisplaySystem;

/**
 * <code>LWJGLFogState</code> subclasses the fog state using the LWJGL API to
 * set the OpenGL fog state.
 * 
 * @author Mark Powell
 * @author Joshua Slack - reworked for StateRecords.
 * @version $Id: LWJGLFogState.java 4131 2009-03-19 20:15:28Z blaine.dev $
 */
public class LWJGLFogState extends FogState {
    private static final long serialVersionUID = 2L;

    private static boolean inited = false;

    /**
     * Constructor instantiates a new <code>LWJGLFogState</code> object with
     * default values.
     *  
     */
    public LWJGLFogState() {
        super();
        if (!inited) {
            // Check for support of fog coords.
            supportsFogCoords = supportsFogCoordsDetected = GLContext
                    .getCapabilities().GL_EXT_fog_coord;
            inited = true;
        }
    }

    /**
     * <code>set</code> sets the OpenGL fog values if the state is enabled.
     * 
     * @see com.jme.scene.state.RenderState#apply()
     */
    public void apply() {
        // ask for the current state record
        RenderContext<?> context = DisplaySystem.getDisplaySystem().getCurrentContext();
        FogStateRecord record = (FogStateRecord) context.getStateRecord(StateType.Fog);
        context.currentStates[StateType.Fog.ordinal()] = this;

        if (isEnabled()) {
            enableFog(true, record);
            
            if (record.isValid()) {
                if (record.fogStart != start) {
                    GL11.glFogf(GL11.GL_FOG_START, start);
                    record.fogStart = start;
                }
                if (record.fogEnd != end) {
                    GL11.glFogf(GL11.GL_FOG_END, end);
                    record.fogEnd = end;
                }            
                if (record.density != density) {
                    GL11.glFogf(GL11.GL_FOG_DENSITY, density);
                    record.density = density;
                }
            } else {
                GL11.glFogf(GL11.GL_FOG_START, start);
                record.fogStart = start;
                GL11.glFogf(GL11.GL_FOG_END, end);
                record.fogEnd = end;
                GL11.glFogf(GL11.GL_FOG_DENSITY, density);
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
        if (record.isValid()) {
            if (enable && !record.enabled) {
                GL11.glEnable(GL11.GL_FOG);
                record.enabled = true;
            } else if (!enable && record.enabled) {
                GL11.glDisable(GL11.GL_FOG);
                record.enabled = false;
            }
        } else {
            if (enable) {
                GL11.glEnable(GL11.GL_FOG);
            } else {
                GL11.glDisable(GL11.GL_FOG);
            }            
            record.enabled = enable;
        }
    }

    private void applyFogColor(ColorRGBA color, FogStateRecord record) {
        if (!record.isValid() || !color.equals(record.fogColor)) {
            record.fogColor.set(color);
            record.colorBuff.clear();
            record.colorBuff.put(record.fogColor.r).put(record.fogColor.g).put(
                    record.fogColor.b).put(record.fogColor.a);
            record.colorBuff.flip();
            GL11.glFog(GL11.GL_FOG_COLOR, record.colorBuff);
        }
    }

    private void applyFogSource(CoordinateSource source, FogStateRecord record) {
        if (supportsFogCoords) {
            if (!record.isValid() || !source.equals(record.source)) {
                if (source == CoordinateSource.Depth) {
                    GL11.glFogi(EXTFogCoord.GL_FOG_COORDINATE_SOURCE_EXT, EXTFogCoord.GL_FRAGMENT_DEPTH_EXT);
                } else {
                    GL11.glFogi(EXTFogCoord.GL_FOG_COORDINATE_SOURCE_EXT, EXTFogCoord.GL_FOG_COORDINATE_EXT);
                }
            }
        }
    }
    
    private void applyFogMode(DensityFunction densityFunction, FogStateRecord record) {
        int glMode = 0;
        switch (densityFunction) {
            case Exponential:
                glMode = GL11.GL_EXP;
                break;
            case Linear:
                glMode = GL11.GL_LINEAR;
                break;
            case ExponentialSquared:
                glMode = GL11.GL_EXP2;
                break;
        }
        
        if (!record.isValid() || record.fogMode != glMode) {
            GL11.glFogi(GL11.GL_FOG_MODE, glMode);
            record.fogMode = glMode;
        }
    }

    private void applyFogHint(Quality quality, FogStateRecord record) {
        int glHint = 0;
        switch (quality) {
            case PerVertex:
                glHint = GL11.GL_FASTEST;
                break;
            case PerPixel:
                glHint = GL11.GL_NICEST;
                break;
        }
        
        if (!record.isValid() || record.fogHint != glHint) {
            GL11.glHint(GL11.GL_FOG_HINT, glHint);
            record.fogHint = glHint;
        }
    }

    @Override
    public FogStateRecord createStateRecord() {
        return new FogStateRecord();
    }
}
