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
import com.jme.math.Vector3f;

/**
 * An implementation of AbstractBresenhamTracer that works on the XZ plane, with
 * positive Y as up.
 * 
 * @author Joshua Slack
 */
public class BresenhamYUpGridTracer extends AbstractBresenhamTracer {

    // a "near zero" value we will use to determine if the walkRay is
    // perpendicular to the grid.
    protected static float TOLERANCE = 0.0000001f;

    private int _stepXDirection;
    private int _stepZDirection;

    // from current position along ray
    private float _distToNextXIntersection, _distToNextZIntersection;
    private float _distBetweenXIntersections, _distBetweenZIntersections;

    @Override
    public void startWalk(final Ray walkRay) {
        // store ray
        _walkRay.set(walkRay);

        // simplify access to direction
        Vector3f direction = _walkRay.getDirection();

        // Move start point to grid space
        Vector3f start = this._walkRay.getOrigin().subtract(_gridOrigin);

        _gridLocation.x = (int) (start.x / _gridSpacing.x);
        _gridLocation.y = (int) (start.z / _gridSpacing.z);

        Vector3f ooDirection = new Vector3f(1.0f / direction.x, 1,
                1.0f / direction.z);

        // Check which direction on the X world axis we are moving.
        if (direction.x > TOLERANCE) {
            _distToNextXIntersection = ((_gridLocation.x + 1) * _gridSpacing.x - start.x)
                    * ooDirection.x;
            _distBetweenXIntersections = _gridSpacing.x * ooDirection.x;
            _stepXDirection = 1;
        } else if (direction.x < -TOLERANCE) {
            _distToNextXIntersection = (start.x - (_gridLocation.x * _gridSpacing.x))
                    * -direction.x;
            _distBetweenXIntersections = -_gridSpacing.x * ooDirection.x;
            _stepXDirection = -1;
        } else {
            _distToNextXIntersection = Float.MAX_VALUE;
            _distBetweenXIntersections = Float.MAX_VALUE;
            _stepXDirection = 0;
        }

        // Check which direction on the Z world axis we are moving.
        if (direction.z > TOLERANCE) {
            _distToNextZIntersection = ((_gridLocation.y + 1) * _gridSpacing.z - start.z)
                    * ooDirection.z;
            _distBetweenZIntersections = _gridSpacing.z * ooDirection.z;
            _stepZDirection = 1;
        } else if (direction.z < -TOLERANCE) {
            _distToNextZIntersection = (start.z - (_gridLocation.y * _gridSpacing.z))
                    * -direction.z;
            _distBetweenZIntersections = -_gridSpacing.z * ooDirection.z;
            _stepZDirection = -1;
        } else {
            _distToNextZIntersection = Float.MAX_VALUE;
            _distBetweenZIntersections = Float.MAX_VALUE;
            _stepZDirection = 0;
        }

        // Reset some variables
        _rayLocation.set(start);
        rayLength = 0.0f;
        stepDirection = Direction.None;
    }

    @Override
    public void next() {
        // Walk us to our next location based on distances to next X or Z grid
        // line.
        if (_distToNextXIntersection < _distToNextZIntersection) {
            rayLength = _distToNextXIntersection;
            _gridLocation.x += _stepXDirection;
            _distToNextXIntersection += _distBetweenXIntersections;
            switch (_stepXDirection) {
            case -1:
                stepDirection = Direction.NegativeX;
                break;
            case 0:
                stepDirection = Direction.None;
                break;
            case 1:
                stepDirection = Direction.PositiveX;
                break;
            }
        } else {
            rayLength = _distToNextZIntersection;
            _gridLocation.y += _stepZDirection;
            _distToNextZIntersection += _distBetweenZIntersections;
            switch (_stepZDirection) {
            case -1:
                stepDirection = Direction.NegativeZ;
                break;
            case 0:
                stepDirection = Direction.None;
                break;
            case 1:
                stepDirection = Direction.PositiveZ;
                break;
            }
        }

        _rayLocation.set(_walkRay.direction).multLocal(rayLength).addLocal(
                _walkRay.origin);
    }

    @Override
    public boolean isRayPerpendicularToGrid() {
        return _stepXDirection == 0 && _stepZDirection == 0;
    }
}
