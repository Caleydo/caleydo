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

package com.jme.scene;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.logging.Logger;

import com.jme.intersection.CollisionResults;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.geom.BufferUtils;

/**
 * <code>Point</code> defines a collection of vertices that are rendered as
 * single points.
 * 
 * @author Mark Powell
 * @version $Id: Point.java 4751 2009-11-09 17:13:20Z blaine.dev $
 */
public class Point extends Geometry {
    private static final Logger logger = Logger
            .getLogger(Point.class.getName());

	private static final long serialVersionUID = 1L;

    private float pointSize = 1.0f;
    private boolean antialiased = false;

    protected transient IntBuffer indexBuffer;

    public Point() {
        
    }

	/**
	 * Constructor instantiates a new <code>Point</code> object with a given
	 * set of data. Any data may be null, except the vertex array. If this is
	 * null an exception is thrown.
	 * 
	 * @param name
	 *            the name of the scene element. This is required for
	 *            identification and comparison purposes.
	 * @param vertex
	 *            the vertices or points.
	 * @param normal
	 *            the normals of the points.
	 * @param color
	 *            the color of the points.
	 * @param texture
	 *            the texture coordinates of the points.
	 */
	public Point(String name, Vector3f[] vertex, Vector3f[] normal,
			ColorRGBA[] color, Vector2f[] texture) {

		super(name, 
		        BufferUtils.createFloatBuffer(vertex), 
		        BufferUtils.createFloatBuffer(normal), 
		        BufferUtils.createFloatBuffer(color), 
                TexCoords.makeNew(texture));
        generateIndices();
		logger.fine("Point created.");
	}

	/**
	 * Constructor instantiates a new <code>Point</code> object with a given
	 * set of data. Any data may be null, except the vertex array. If this is
	 * null an exception is thrown.
	 * 
	 * @param name
	 *            the name of the scene element. This is required for
	 *            identification and comparison purposes.
	 * @param vertex
	 *            the vertices or points.
	 * @param normal
	 *            the normals of the points.
	 * @param color
	 *            the color of the points.
	 * @param coords
	 *            the texture coordinates of the points.
	 */
	public Point(String name, FloatBuffer vertex, FloatBuffer normal,
			FloatBuffer color, TexCoords coords) {
		super(name, vertex, normal, color, coords);
        generateIndices();
		logger.fine("Point created.");
	}

    @Override
    public void reconstruct(FloatBuffer vertices, FloatBuffer normals, FloatBuffer colors, TexCoords coords) {
        super.reconstruct(vertices, normals, colors, coords);
        generateIndices();
    }

    public void generateIndices() {
        if (getIndexBuffer() == null || getIndexBuffer().limit() != getVertexCount()) {
            setIndexBuffer(BufferUtils.createIntBuffer(getVertexCount()));
        } else
            getIndexBuffer().rewind();

        for (int x = 0; x < getVertexCount(); x++)
            getIndexBuffer().put(x);
    }
    
	/*
	 * unsupported
	 * 
	 * @see com.jme.scene.Spatial#hasCollision(com.jme.scene.Spatial,
	 *      com.jme.intersection.CollisionResults)
	 */
	public void findCollisions(
            Spatial scene, CollisionResults results, int requiredOnBits) {
		; // unsupported
	}
	
	public boolean hasCollision(
            Spatial scene, boolean checkTriangles, int requiredOnBits) {
		return false;
	}

    /**
     * @return true if points are to be drawn antialiased
     */
    public boolean isAntialiased() {
        return antialiased;
    }
    
    /**
     * Sets whether the point should be antialiased. May decrease performance. If
     * you want to enabled antialiasing, you should also use an alphastate with
     * a source of SourceFunction.SourceAlpha and a destination of DB_ONE_MINUS_SRC_ALPHA or
     * DB_ONE.
     * 
     * @param antialiased
     *            true if the line should be antialiased.
     */
    public void setAntialiased(boolean antialiased) {
        this.antialiased = antialiased;
    }

    /**
     * @return the pixel size of each point.
     */
    public float getPointSize() {
        return pointSize;
    }

    /**
     * Sets the pixel width of the point when drawn. Non anti-aliased point
     * sizes are rounded to the nearest whole number by OpenGL.
     * 
     * @param size
     *            The size to set.
     */
    public void setPointSize(float size) {
        this.pointSize = size;
    }

    public IntBuffer getIndexBuffer() {
        return indexBuffer;
    }

    public void setIndexBuffer(IntBuffer indices) {
        this.indexBuffer = indices;
    }

    
    /**
     * Used with Serialization. Do not call this directly.
     * 
     * @param s
     * @throws IOException
     * @see java.io.Serializable
     */
    private void writeObject(java.io.ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        if (getIndexBuffer() == null)
            s.writeInt(0);
        else {
            s.writeInt(getIndexBuffer().limit());
            getIndexBuffer().rewind();
            for (int x = 0, len = getIndexBuffer().limit(); x < len; x++)
                s.writeInt(getIndexBuffer().get());
        }
    }

    /**
     * Used with Serialization. Do not call this directly.
     * 
     * @param s
     * @throws IOException
     * @throws ClassNotFoundException
     * @see java.io.Serializable
     */
    private void readObject(java.io.ObjectInputStream s) throws IOException,
            ClassNotFoundException {
        s.defaultReadObject();
        int len = s.readInt();
        if (len == 0) {
            setIndexBuffer(null);
        } else {
            IntBuffer buf = BufferUtils.createIntBuffer(len);
            for (int x = 0; x < len; x++)
                buf.put(s.readInt());
            setIndexBuffer(buf);            
        }
    }

    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(pointSize, "pointSize", 1);
        capsule.write(antialiased, "antialiased", false);
        capsule.write(indexBuffer, "indexBuffer", null);
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        pointSize = capsule.readFloat("pointSize", 1);
        antialiased = capsule.readBoolean("antialiased", false);
        indexBuffer = capsule.readIntBuffer("indexBuffer", null);
    }
    
    public void draw(Renderer r) {
        if (!r.isProcessingQueue()) {
            if (r.checkAndAdd(this))
                return;
        }

        r.draw(this);
    }
}

