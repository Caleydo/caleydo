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

import com.jme.intersection.BoundingPickResults;
import com.jme.intersection.PickData;
import com.jme.math.Ray;
import com.jme.math.Triangle;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.scene.Geometry;
import com.jme.scene.Spatial;
import com.jmex.terrain.TerrainBlock;
import com.jmex.terrain.util.AbstractBresenhamTracer.Direction;

/**
 * Utility class for doing ray based picking of TerrainPages using the
 * {@link BresenhamYUpGridTracer}. It basically works by casting a pick ray
 * against the bounding volumes of the TerrainPage and its children, gathering
 * all of the TerrainBlocks hit (in distance order.) The triangles of each block
 * are then tested using the BresenhamYUpGridTracer to determine which triangles
 * to test and in what order. When a hit is found, it is guarenteed to be the
 * first such hit and can immediately be returned.
 * 
 * @author Joshua Slack
 */
public class BresenhamTerrainPicker {

    private final Triangle _gridTriA = new Triangle(new Vector3f(),
            new Vector3f(), new Vector3f());
    private final Triangle _gridTriB = new Triangle(new Vector3f(),
            new Vector3f(), new Vector3f());

    private final Vector3f _calcVec1 = new Vector3f();
    private final Ray _workRay = new Ray();

    private final Spatial _root;
    private final BoundingPickResults _pr;
    private final BresenhamYUpGridTracer _tracer;

    /**
     * Construct a new BresenhamTerrainPicker that works on the given
     * TerrainPage.
     * 
     * @param page
     *            the TerrainPage to work on.
     */
    public BresenhamTerrainPicker(final Spatial root) {
        _root = root;
        _pr = new BoundingPickResults();
        _pr.setCheckDistance(true);
        _tracer = new BresenhamYUpGridTracer();
    }

    /**
     * Ask for the point of intersection between the given ray and the terrain.
     * 
     * @param worldPick
     *            our pick ray, in world space.
     * @param store
     *            if not null, the results will be stored in this vector and the
     *            vector returned. If store is null, a new vector will be
     *            created.
     * @return null if no pick is found. Otherwise it returns a Vector3f (the
     *         store param, if that was not null) populated with the pick
     *         coordinates.
     */
    public Vector3f getTerrainIntersection(final Ray worldPick,
            final Vector3f store) {
        if (_root == null || _root.getWorldBound() == null)
            return null;

        if (!_root.getWorldBound().intersects(worldPick))
            return null;

        // Set up our working ray
        _workRay.set(worldPick);

        // Grab all terrain blocks and do a grid walk on each starting from
        // closest.
        _pr.clear();
        _root.findPick(_workRay, _pr);

        for (int i = 0, max = _pr.getNumber(); i < max; i++) {
            PickData pd = _pr.getPickData(i);
            if (pd == null)
                continue;

            Geometry g = pd.getTargetMesh();
            if (g instanceof TerrainBlock) {
                TerrainBlock block = (TerrainBlock) g;

                _tracer.getGridSpacing().set(block.getWorldScale()).multLocal(
                        block.getStepScale());
                _tracer.setGridOrigin(block.getWorldTranslation());

                _workRay.getOrigin().set(worldPick.getDirection()).multLocal(
                        pd.getDistance()-.1f).addLocal(worldPick.getOrigin());

                _tracer.startWalk(_workRay);

                if (_tracer.isRayPerpendicularToGrid()) {
                    // no intersection
                    return null;
                }

                final Vector3f intersection = store != null ? store
                        : new Vector3f();
                final Vector2f loc = _tracer.getGridLocation();

                while (loc.x >= -1 && loc.x <= block.getSize() && loc.y >= -1
                        && loc.y <= block.getSize()) {

                    // check the triangles of main square for intersection.
                    if (checkTriangles(loc.x, loc.y, _workRay, intersection,
                            block)) {
                        // we found an intersection, so return that!
                        return intersection;
                    }

                    // because of how we get our height coords, we will
                    // sometimes be off be a grid spot, so we check the next
                    // grid space up.
                    int dx = 0, dz = 0;
                    Direction d = _tracer.getLastStepDirection();
                    switch (d) {
                    case PositiveX:
                    case NegativeX:
                        dx = 0;
                        dz = 1;
                        break;
                    case PositiveZ:
                    case NegativeZ:
                        dx = 1;
                        dz = 0;
                        break;
                    }

                    if (checkTriangles(loc.x + dx, loc.y + dz, _workRay,
                            intersection, block)) {
                        // we found an intersection, so return that!
                        return intersection;
                    }

                    _tracer.next();
                }
            }
        }

        return null;
    }

    /**
     * Check the two triangles of a given grid space for intersection.
     * 
     * @param gridX
     *            grid row
     * @param gridY
     *            grid column
     * @param pick
     *            our pick ray
     * @param intersection
     *            the store variable
     * @param block
     *            the terrain block we are currently testing against.
     * @return true if a pick was found on these triangles.
     */
    protected boolean checkTriangles(float gridX, float gridY, Ray pick,
            Vector3f intersection, TerrainBlock block) {
        if (!getTriangles(gridX, gridY, block))
            return false;

        if (!pick.intersectWhere(_gridTriA, intersection)) {
            return pick.intersectWhere(_gridTriB, intersection);
        } else {
            return true;
        }
    }

    /**
     * Request the triangles (in world coord space) of a TerrainBlock that
     * correspond to the given grid location. The triangles are stored in the
     * class fields _gridTriA and _gridTriB.
     * 
     * @param gridX
     *            grid row
     * @param gridY
     *            grid column
     * @param block
     *            the TerrainBlock we are working with
     * @return true if the grid point is valid for the given block, false if it
     *         is off the block.
     */
    protected boolean getTriangles(float gridX, float gridY, TerrainBlock block) {
        _calcVec1.set(gridX, 0, gridY);
        int index = findClosestHeightIndex(_calcVec1, block);
        if (index == -1)
            return false;
        float h3 = block.getHeightMap()[index + block.getSize()];
        float h4 = block.getHeightMap()[index + block.getSize() + 1];
        float h1 = block.getHeightMap()[index];
        float h2 = block.getHeightMap()[index + 1];

        final Vector3f scaleVec = _calcVec1.set(_tracer.getGridSpacing())
                .multLocal(block.getWorldScale());

        _gridTriA.get(0).x = (gridX);
        _gridTriA.get(0).y = (h1);
        _gridTriA.get(0).z = (gridY);
        _gridTriA.get(0).multLocal(scaleVec).addLocal(_tracer.getGridOrigin());

        _gridTriA.get(1).x = (gridX);
        _gridTriA.get(1).y = (h3);
        _gridTriA.get(1).z = (gridY + 1);
        _gridTriA.get(1).multLocal(scaleVec).addLocal(_tracer.getGridOrigin());

        _gridTriA.get(2).x = (gridX + 1);
        _gridTriA.get(2).y = (h2);
        _gridTriA.get(2).z = (gridY);
        _gridTriA.get(2).multLocal(scaleVec).addLocal(_tracer.getGridOrigin());

        _gridTriB.get(0).x = (gridX + 1);
        _gridTriB.get(0).y = (h2);
        _gridTriB.get(0).z = (gridY);
        _gridTriB.get(0).multLocal(scaleVec).addLocal(_tracer.getGridOrigin());

        _gridTriB.get(1).x = (gridX);
        _gridTriB.get(1).y = (h3);
        _gridTriB.get(1).z = (gridY + 1);
        _gridTriB.get(1).multLocal(scaleVec).addLocal(_tracer.getGridOrigin());

        _gridTriB.get(2).x = (gridX + 1);
        _gridTriB.get(2).y = (h4);
        _gridTriB.get(2).z = (gridY + 1);
        _gridTriB.get(2).multLocal(scaleVec).addLocal(_tracer.getGridOrigin());

        return true;
    }

    /**
     * Finds the closest height point to a position. Will always be left/above
     * that position.
     * 
     * @param position
     *            the position to check at
     * @param block
     *            the block to get height values from
     * @return an index to the height position of the given block.
     */
    protected int findClosestHeightIndex(Vector3f position, TerrainBlock block) {

        int x = (int) position.x;
        int z = (int) position.z;

        if (x < 0 || x >= block.getSize() - 1) {
            return -1;
        }
        if (z < 0 || z >= block.getSize() - 1) {
            return -1;
        }

        return z * block.getSize() + x;
    }
}
