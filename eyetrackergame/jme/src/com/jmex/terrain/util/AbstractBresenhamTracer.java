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
package com.jmex.terrain.util;

import com.jme.math.Ray;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;

/**
 * AbstractBresenhamTracer is a simple base class for using Bresenham's line
 * equation in jME. Bresenham's line equation is useful for doing various 3d
 * tasks that involve regularly spaced grids (such as picking against a
 * terrain.)
 * 
 * @author Joshua Slack
 */
public abstract class AbstractBresenhamTracer {

    protected final Vector3f _gridOrigin = new Vector3f();
    protected final Vector3f _gridSpacing = new Vector3f();
    protected final Vector2f _gridLocation = new Vector2f();
    protected final Vector3f _rayLocation = new Vector3f();
    protected final Ray _walkRay = new Ray();

    protected Direction stepDirection = Direction.None;
    protected float rayLength;

    public static enum Direction {
        None, PositiveX, NegativeX, PositiveY, NegativeY, PositiveZ, NegativeZ;
    };

    /**
     * 
     * @return the direction of our last step on the grid.
     */
    public Direction getLastStepDirection() {
        return stepDirection;
    }

    /**
     * 
     * @return the row and column we are currently in on the grid.
     */
    public Vector2f getGridLocation() {
        return _gridLocation;
    }

    /**
     * 
     * @return how far we traveled down the ray on our last call to next().
     */
    public float getRayTraveled() {
        return rayLength;
    }

    /**
     * Set the world origin of our grid. This is useful to the tracer when doing
     * conversion between world coordinates and grid locations.
     * 
     * @param origin
     *            our new origin (copied into the tracer)
     */
    public void setGridOrigin(Vector3f origin) {
        _gridOrigin.set(origin);
    }

    /**
     * 
     * @return the current grid origin
     * @see #setGridOrigin(Vector3f)
     */
    public Vector3f getGridOrigin() {
        return _gridOrigin;
    }

    /**
     * Set the world spacing (scale) of our grid. Also useful for converting
     * between world coordinates and grid location.
     * 
     * @param spacing
     *            our new spacing (copied into the tracer)
     */
    public void setGridSpacing(Vector3f spacing) {
        this._gridSpacing.set(spacing);
    }

    /**
     * 
     * @return the current grid spacing
     * @see #setGridSpacing(Vector3f)
     */
    public Vector3f getGridSpacing() {
        return _gridSpacing;
    }

    /**
     * Set up our position on the grid and initialize the tracer using the
     * provided ray.
     * 
     * @param walkRay
     *            the world ray along which we we walk the grid.
     */
    public abstract void startWalk(final Ray walkRay);

    /**
     * Move us along our walkRay to the next grid location.
     */
    public abstract void next();

    /**
     * 
     * @return true if our walkRay, specified in startWalk, ended up being
     *         perpendicular to the grid (and therefore can not move to a new
     *         grid location on calls to next(). You should test this after
     *         calling startWalk and before calling next().
     */
    public abstract boolean isRayPerpendicularToGrid();
}
