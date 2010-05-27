/*
 * Open Dynamics Engine for Java (odejava) Copyright (c) 2004, Jani Laakso, All
 * rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution. Neither the name of the odejava nor the
 * names of its contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.odejava;

import org.odejava.ode.Ode;

/**
 * A quad-tree based space for collision detection.
 * QuadTrees are located at a fixed point in space and cannot change after
 * being created.
 * <p/>
 * <p/>
 * Created 16.12.2003 (dd.mm.yyyy)
 *
 * @author Jani Laakso E-mail: jani.laakso@itmill.com
 *         see http://odejava.dev.java.net
 */
public class QuadTreeSpace extends Space {

    /**
     * The center of the space
     */
    protected float[] center;

    /**
     * The extents of the space
     */
    protected float[] extents;

    /**
     * The depth of the quad tree to generate for this space.
     */
    protected int depth;

    /**
     * Create a QuadTree space that does not have a parent. Depth should be a
     * non-negative number.
     *
     * @param center  The location of the space's center in world coords
     * @param extents The size of the space's extents in world coords
     * @param depth   The number of subdivisions of the quad tree to make
     */
    public QuadTreeSpace( float[] center, float[] extents, int depth ) {
        this( null, center, extents, depth );
    }

    /**
     * Create a QuadTree space that may optionally belong to a parent space.
     * If the parent space reference is null, then this space is placed as a
     * top-level space. Depth should be a non-negative number.
     *
     * @param center  The location of the space's center in world coords
     * @param extents The size of the space's extents in world coords
     * @param depth   The number of subdivisions of the quad tree to make
     * @param parent  A reference to the parent space, or null
     */
    public QuadTreeSpace( Space parent, float[] center, float[] extents, int depth ) {
        super( parent );

        spaceId = Ode.dQuadTreeSpaceCreate( parentId,
                Odejava.createSwigArray( center ),
                Odejava.createSwigArray( extents ),
                depth );

        this.center = new float[3];
        this.center[0] = center[0];
        this.center[1] = center[1];
        this.center[2] = center[2];

        this.extents = new float[3];
        this.extents[0] = extents[0];
        this.extents[1] = extents[1];
        this.extents[2] = extents[2];

        this.depth = depth;
    }

    /**
     * Get the center of the space and copy it into the user-provided array.
     *
     * @param result An array to copy the center into
     */
    public void getCenter( float[] result ) {
        result[0] = center[0];
        result[1] = center[1];
        result[2] = center[2];
    }

    /**
     * Get the extents of the space and copy it into the user-provided array.
     *
     * @param result An array to copy the center into
     */
    public void getExtents( float[] result ) {
        result[0] = extents[0];
        result[1] = extents[1];
        result[2] = extents[2];
    }

    /**
     * Get the depth of the quadtree.
     *
     * @return The depth - a non-negative number
     */
    public float getDepth() {
        return depth;
    }
}
