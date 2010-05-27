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

package com.jme.scene.state;

import java.io.IOException;

import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;

/**
 * <code>ShadeState</code> maintains the interpolation of color between
 * vertices. Smooth shades the colors with proper linear interpolation, while
 * flat provides no smoothing.  If this state is not enabled, Smooth is used.
 * 
 * @author Mark Powell
 * @version $Id: ShadeState.java 4336 2009-05-03 20:57:01Z christoph.luder $
 */
public abstract class ShadeState extends RenderState {

    public enum ShadeMode {
        /**
         * Pick the color of just one vertex of a triangle and rasterize all
         * pixels of the triangle with this color.
         */
        Flat,
        /**
         * Smoothly interpolate the color values between the three colors of the
         * three vertices.  (Default)
         */
        Smooth;
    }

    // shade mode.
    protected ShadeMode shadeMode = ShadeMode.Smooth;

    /**
     * Constructor instantiates a new <code>ShadeState</code> object with the
     * default mode being smooth.
     */
    public ShadeState() {
    }

    /**
     * <code>getShade</code> returns the current shading mode.
     * 
     * @return the current shading mode.
     */
    public ShadeMode getShadeMode() {
        return shadeMode;
    }

    /**
     * <code>setShadeMode</code> sets the current shading mode.
     * 
     * @param shadeMode
     *            the new shading mode.
     * @throws IllegalArgumentException
     *             if shadeMode is null
     */
    public void setShadeMode(ShadeMode shadeMode) {
        if (shadeMode == null) {
            throw new IllegalArgumentException("shadeMode can not be null.");
        }
        this.shadeMode = shadeMode;
        setNeedsRefresh(true);
    }

    /**
     * <code>getType</code> returns this type of this render state.
     * (RS_SHADE).
     * 
     * @see com.jme.scene.state.RenderState#getType()
     * @deprecated As of 2.0, use {@link RenderState#getStateType()} instead.
     */
    public int getType() {
        return RS_SHADE;
    }

    /**
     * <code>getStateType</code> returns the type {@link RenderState.StateType#Shade}
     * 
     * @return {@link RenderState.StateType#Shade}
     * @see com.jme.scene.state.RenderState#getStateType()
     */
    public StateType getStateType() {
    	
        return StateType.Shade;
    }

    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(shadeMode, "shadeMode", ShadeMode.Smooth);
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        shadeMode = capsule.readEnum("shadeMode", ShadeMode.class,
                ShadeMode.Smooth);
    }

    public Class<?> getClassTag() {
        return ShadeState.class;
    }
}
