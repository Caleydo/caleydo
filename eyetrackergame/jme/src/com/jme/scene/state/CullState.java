/*
 * Copyright (c) 2003-2010 jMonkeyEngine
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
 * <code>CullState</code> determines which side of a model will be visible when
 * it is rendered. By default, both sides are visible. Define front as the side
 * that traces its vertexes counter clockwise and back as the side that traces
 * its vertexes clockwise, a side (front or back) can be culled, or not shown
 * when the model is rendered. Instead, the side will be transparent.
 * <br>
 * Implementations of this class should take note of the flipped culling mode.
 * <br>
 * <b>NOTE:</b> Any object that is placed in the transparent queue with two
 * sided transparency will not use the cullstate that is attached to it. Instead,
 * using the cullstates necessary for rendering two sided transparency.
 * 
 * @see CullState#setFlippedCulling(boolean)
 * 
 * @author Mark Powell
 * @author Joshua Slack
 * @author Jack Lindamood (javadoc only)
 * @author Tijl Houtbeckers (added flipped culling mode)
 * @version $Id: CullState.java 4798 2010-01-20 20:00:37Z skye.book $
 */
public abstract class CullState extends RenderState {

    public enum Face {
        /** Neither front or back face is culled. This is default. */
        None,
        /** Cull the front faces. */
        Front,
        /** Cull the back faces. */
        Back,
        /** Cull both the front and back faces. */
        FrontAndBack;
    }

    public enum PolygonWind {
        /** Polygons whose vertices are specified in CCW order are front facing. This is default. */
        CounterClockWise,
        /** Polygons whose vertices are specified in CW order are front facing. */
        ClockWise;
    }

    /** The cull face set for this CullState. */
    private Face cullFace = Face.None;

    /** The polygonWind order set for this CullState. */
    private PolygonWind polygonWind = PolygonWind.CounterClockWise;

    /**
     * <code>getType</code> returns RenderState.RS_CULL
     * 
     * @return RenderState.RS_CULL
     * @see com.jme.scene.state.RenderState#getType()
     * @deprecated As of 2.0, use {@link RenderState#getStateType()} instead.
     */
    public int getType() {
        return RS_CULL;
    }

    /**
     * <code>getStateType</code> returns the type {@link RenderState.StateType#Cull}
     * 
     * @return {@link RenderState.StateType#Cull}
     * @see com.jme.scene.state.RenderState#getStateType()
     */
    public StateType getStateType() {
    	
        return StateType.Cull;
    }

    /**
     * @param face
     *            The new face to cull.
     */
    public void setCullFace(Face face) {
        cullFace = face;
        setNeedsRefresh(true);
    }

    /**
     * @return the currently set face to cull.
     */
    public Face getCullFace() {
        return cullFace;
    }

    /**
     * @param windOrder
     *            The new polygonWind order.
     */
    public void setPolygonWind(PolygonWind windOrder) {
        polygonWind = windOrder;
        setNeedsRefresh(true);
    }

    /**
     * @return the currently set polygonWind order.
     */
    public PolygonWind getPolygonWind() {
        return polygonWind;
    }
    
    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(cullFace, "cullFace", Face.None);
        capsule.write(polygonWind, "polygonWind", PolygonWind.CounterClockWise);
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        cullFace = capsule.readEnum("cullFace", Face.class, Face.None);
        polygonWind = capsule.readEnum("polygonWind", PolygonWind.class, PolygonWind.CounterClockWise);
    }

    public Class<?> getClassTag() {
        return CullState.class;
    }
}
