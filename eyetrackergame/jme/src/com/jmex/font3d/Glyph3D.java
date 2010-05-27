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
package com.jmex.font3d;

import java.awt.geom.Rectangle2D;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Vector;

import com.jme.math.Vector3f;
import com.jmex.font3d.math.ClosedPolygon;
import com.jmex.font3d.math.DoublyConnectedEdgeList;
import com.jmex.font3d.math.PlanarEdge;
import com.jmex.font3d.math.PlanarVertex;
import com.jmex.font3d.math.TriangulationVertex;
import com.jmex.font3d.math.Triangulator;

/**
 * This class represents a glyph of text.
 * 
 * @author emanuel
 */
public class Glyph3D {
    private static final long serialVersionUID = -8126290675830115033L;

    /**
     * This represents the planar subdivision of the glyph, before
     * triangulation.
     */
    Triangulator subdivision = new Triangulator();
    Vector<PlanarEdge> outline;
    Vector3f outline_normals[];

    // These are the indices for the triangulation of the surface of the glyph.
    IntBuffer surface;

    private Rectangle2D bounds;

    private Glyph3DMesh glyphmesh;

    private char glyph_char;

    private int childIndex = -1;

    public Glyph3D(char glyph_char) {
        this.glyph_char = glyph_char;
    }

    /**
     * This method adds one closed polygon to the subdivision (it can both be
     * the outer-polygon or the inner) In normal glyphs, the outer should be
     * clockwise and the inner counter-clockwise. That is why we add the edges
     * in the opposite order, due to the convention of
     * {@link DoublyConnectedEdgeList}.
     */
    public void addPolygon(ClosedPolygon poly) {
        /*
         * if(poly.isHole()) { logger.info("The Polygon is a hole, this might be
         * alright."); }
         */
        int first_vert_id = subdivision.getVertices().size();
        for (Vector3f p : poly.getPoints()) {
            PlanarVertex vert = subdivision.addVertex(p);
            if (vert.getIndex() > first_vert_id) {
                subdivision.addEdge(vert.getIndex(), vert.getIndex() - 1);
            }
        }
        // Add the last one
        subdivision.addEdge(first_vert_id, first_vert_id
                + poly.getPoints().size() - 1);
    }

    /**
     * Triangulate the glyph, but first save the original outline.
     */
    public void triangulate() {
        // Save the outline and calculate normals
        outline = new Vector<PlanarEdge>();
        for (PlanarEdge e : subdivision.getEdges()) {
            if (e.isRealEdge()) {
                outline.add(e);
            }
        }
        // Calculate outline normals
        outline_normals = new Vector3f[outline.size()];
        for (PlanarEdge e : outline) {
            TriangulationVertex vert = (TriangulationVertex) e.getDestination();
            // Normal 1
            Vector3f normal1 = new Vector3f(vert.getOutGoingEdge()
                    .getDestination().getPoint())
                    .subtractLocal(vert.getPoint()).normalizeLocal();
            // Vector3f normal1 = new
            // Vector3f(vert.getOutGoingEdge().getDestination().getPoint()).subtractLocal(vert.getPoint());
            normal1.z = -normal1.x;
            normal1.x = normal1.y;
            normal1.y = normal1.z;
            normal1.z = 0;
            // Normal 2
            Vector3f normal2 = new Vector3f(vert.getPoint()).subtractLocal(
                    vert.getInGoingEdge().getOrigin().getPoint())
                    .normalizeLocal();
            // Vector3f normal2 = new
            // Vector3f(vert.getPoint()).subtractLocal(vert.getInGoingEdge().getOrigin().getPoint());
            normal2.z = -normal2.x;
            normal2.x = normal2.y;
            normal2.y = normal2.z;
            normal2.z = 0;
            normal1.addLocal(normal2).normalizeLocal();

            outline_normals[vert.getIndex()] = normal1;
        }

        // Calculate the triangulation of the surface.
        surface = subdivision.triangulate();
    }

    public boolean isEmpty() {
        return subdivision.getVertices().size() == 0;
    }

    public Vector<PlanarEdge> getOutline() {
        return outline;
    }

    public Vector3f[] getOutlineNormals() {
        return outline_normals;
    }

    public IntBuffer getSurface() {
        return surface;
    }

    public void setBounds(Rectangle2D bounds2D) {
        bounds = bounds2D;
    }

    public Rectangle2D getBounds() {
        return bounds;
    }

    public ArrayList<TriangulationVertex> getVertices() {
        return subdivision.getVertices();
    }

    /**
     * Here we create the glyph-mesh from the triangulation (using
     * sides/front/back according to arguments).
     * 
     * @param drawBack
     * @param drawFront
     * @param drawSides
     */
    public void generateMesh(boolean drawSides, boolean drawFront,
            boolean drawBack) {
        this.glyphmesh = new Glyph3DMesh(this, drawSides, drawFront, drawBack);
        this.glyphmesh.setName("char: "+glyph_char);
    }

    public Glyph3DMesh getMesh() {
        return glyphmesh;
    }

    public char getChar() {
        return glyph_char;
    }

    public void setChildIndex(int index) {
        this.childIndex = index;
    }

    public int getChildIndex() {
        return childIndex;
    }
}
