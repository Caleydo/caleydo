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
// $Id: Quad.java 4131 2009-03-19 20:15:28Z blaine.dev $
package com.jme.scene.shape;

import java.nio.FloatBuffer;

import com.jme.math.Vector3f;
import com.jme.scene.TexCoords;
import com.jme.scene.TriMesh;
import com.jme.util.geom.BufferUtils;

/**
 * A four sided, two dimensional shape (a quadrilateral).
 * <p>
 * The local height of the {@code Quad} defines it's size about the y-axis,
 * while the width defines the x-axis. The z-axis will always be 0.
 * 
 * @author Mark Powell
 * @version $Revision: 4131 $, $Date: 2009-03-19 21:15:28 +0100 (Do, 19 Mär 2009) $
 */
public class Quad extends TriMesh {

    private static final long serialVersionUID = 1L;

    protected float width = 0;
    
    protected float height = 0;

    public Quad() {
    }

    /**
     * Constructor creates a new <code>Quad</code> object. That data for the
     * <code>Quad</code> is not set until a call to <code>initialize</code>
     * is made.
     * 
     * @param name
     *            the name of this <code>Quad</code>.
     */
    public Quad(String name) {
        this(name, 1, 1);
    }

    /**
     * Constructor creates a new <code>Quade</code> object with the provided
     * width and height.
     * 
     * @param name
     *            the name of the <code>Quad</code>.
     * @param width
     *            the width of the <code>Quad</code>.
     * @param height
     *            the height of the <code>Quad</code>.
     */
    public Quad(String name, float width, float height) {
        super(name);
        updateGeometry(width, height);
    }

    /**
     * <code>getCenter</code> returns the center of the <code>Quad</code>.
     * 
     * @return Vector3f the center of the <code>Quad</code>.
     */
    public Vector3f getCenter() {
        return worldTranslation;
    }

    public float getHeight() {
        return height;
    }

    public float getWidth() {
        return width;
    }

    /**
     * <code>initialize</code> builds the data for the <code>Quad</code>
     * object.
     * 
     * @param width
     *            the width of the <code>Quad</code>.
     * @param height
     *            the height of the <code>Quad</code>.
     * @deprecated Use {@link #updateGeometry(float,float)} instead
     */
    public void initialize(float width, float height) {
        updateGeometry(width, height);
    }

    /**
     * <code>resize</code> changes the width and height of the given quad by
     * altering its vertices.
     * 
     * @param width
     *            the new width of the <code>Quad</code>.
     * @param height
     *            the new height of the <code>Quad</code>.
     */
    public void resize(float width, float height) {
        this.width = width;
        this.height = height;
        getVertexBuffer().clear();
        getVertexBuffer().put(-width / 2f).put(height / 2f).put(0);
        getVertexBuffer().put(-width / 2f).put(-height / 2f).put(0);
        getVertexBuffer().put(width / 2f).put(-height / 2f).put(0);
        getVertexBuffer().put(width / 2f).put(height / 2f).put(0);
    }

    /**
     * Rebuild this quad based on a new set of parameters.
     * 
     * @param width the width of the quad.
     * @param height the height of the quad.
     */
    public void updateGeometry(float width, float height) {
        this.width = width;
        this.height = height;
        setVertexCount(4);
        setVertexBuffer(BufferUtils.createVector3Buffer(getVertexCount()));
        setNormalBuffer(BufferUtils.createVector3Buffer(getVertexCount()));
        FloatBuffer tbuf = BufferUtils.createVector2Buffer(getVertexCount());
        setTextureCoords(new TexCoords(tbuf));
        setTriangleQuantity(2);
        setIndexBuffer(BufferUtils.createIntBuffer(getTriangleCount() * 3));
        resize(width, height);
        getNormalBuffer().put(new float[] { 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1 });
        tbuf.put(new float[] { 0, 1, 0, 0, 1, 0, 1, 1 });
        getIndexBuffer().put(new int[] { 0, 1, 2, 0, 2, 3 });
    }

}