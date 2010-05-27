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
// $Id: StripBox.java 4131 2009-03-19 20:15:28Z blaine.dev $
package com.jme.scene.shape;

import java.nio.FloatBuffer;

import com.jme.math.Vector3f;
import com.jme.scene.TexCoords;
import com.jme.scene.TriMesh;
import com.jme.util.geom.BufferUtils;

/**
 * A box made from a strip mode tri-mesh.
 * 
 * @author Mark Powell
 * @author Joshua Slack (conversion from Box to StripBox)
 * @version $Revision: 4131 $, $Date: 2009-03-19 21:15:28 +0100 (Do, 19 Mär 2009) $
 */
public class StripBox extends AbstractBox {

    private static final long serialVersionUID = 1L;

    /** <strong>NOT API:</strong> for internal use, do not call from user code. */
	public StripBox() {
		super("temp");
	}

	/**
	 * Constructor instantiates a new <code>StripBox</code> object. Center and
	 * vertice information must be supplied later.
	 * 
	 * @param name
	 *            the name of the scene element. This is required for
	 *            identification and comparision purposes.
	 */
	public StripBox(String name) {
		super(name);
	}

	/**
	 * Constructor instantiates a new <code>StripBox</code> object. The minimum and
	 * maximum point are provided. These two points define the shape and size of
	 * the box, but not it's orientation or position. You should use the
	 * <code>setLocalTranslation</code> and <code>setLocalRotation</code>
	 * for those attributes.
	 * 
	 * @param name
	 *            the name of the scene element. This is required for
	 *            identification and comparision purposes.
	 * @param min
	 *            the minimum point that defines the box.
	 * @param max
	 *            the maximum point that defines the box.
	 */
	public StripBox(String name, Vector3f min, Vector3f max) {
		super(name);
		updateGeometry(min, max);
	}

	/**
	 * Constructs a new box. The box has the given center and extends in the x,
	 * y, and z out from the center (+ and -) by the given amounts. So, for
	 * example, a box with extent of .5 would be the unit cube.
	 * 
	 * @param name
	 *            Name of the box.
	 * @param center
	 *            Center of the box.
	 * @param xExtent
	 *            x extent of the box, in both directions.
	 * @param yExtent
	 *            y extent of the box, in both directions.
	 * @param zExtent
	 *            z extent of the box, in both directions.
	 */
	public StripBox(String name, Vector3f center, float xExtent, float yExtent,
			float zExtent) {
		super(name);
		updateGeometry(center, xExtent, yExtent, zExtent);
	}

	protected void duUpdateGeometryVertices() {
        setVertexBuffer(BufferUtils.createVector3Buffer(getVertexBuffer(), 8));
		Vector3f[] vert = computeVertices(); // returns 8
        getVertexBuffer().clear();
		getVertexBuffer().put(vert[0].x).put(vert[0].y).put(vert[0].z);
		getVertexBuffer().put(vert[1].x).put(vert[1].y).put(vert[1].z);
		getVertexBuffer().put(vert[2].x).put(vert[2].y).put(vert[2].z);
		getVertexBuffer().put(vert[3].x).put(vert[3].y).put(vert[3].z);
        getVertexBuffer().put(vert[4].x).put(vert[4].y).put(vert[4].z);
        getVertexBuffer().put(vert[5].x).put(vert[5].y).put(vert[5].z);
        getVertexBuffer().put(vert[6].x).put(vert[6].y).put(vert[6].z);
        getVertexBuffer().put(vert[7].x).put(vert[7].y).put(vert[7].z);
	}

	protected void duUpdateGeometryNormals() {
        Vector3f[] vert = computeVertices(); // returns 8
        setNormalBuffer(BufferUtils.createVector3Buffer(getNormalBuffer(), 8));
        Vector3f norm = new Vector3f();

        getNormalBuffer().clear();
        for (int i = 0; i < 8; i++) {
            norm.set(vert[i]).normalizeLocal();
            getNormalBuffer().put(norm.x).put(norm.y).put(norm.z);
        }
	}

	protected void duUpdateGeometryTextures() {
	    if (getTextureCoords().get(0) == null) {
		    getTextureCoords().set(0,new TexCoords(BufferUtils.createVector2Buffer(24)));
		    FloatBuffer tex = getTextureCoords().get(0).coords;
		    tex.put(1).put(0); //0
			tex.put(0).put(0); //1
			tex.put(0).put(1); //2
			tex.put(1).put(1); //3
            tex.put(1).put(0); //4
            tex.put(0).put(0); //5
            tex.put(1).put(1); //6
            tex.put(0).put(1); //7
	    }
	}

	protected void duUpdateGeometryIndices() {
        setMode(TriMesh.Mode.Strip);
	    if (getIndexBuffer() == null) {
			int[] indices = { 1, 0, 4, 5, 7, 0, 3, 1, 2, 4, 6, 7, 2, 3 };
			setIndexBuffer(BufferUtils.createIntBuffer(indices));
	    }
	}

	/**
	 * Creates a new StripBox object containing the same data as this one.
	 * 
	 * @return the new StripBox
	 */
	public StripBox clone() {
		return new StripBox(getName() + "_clone",
		        (Vector3f) center.clone(),
		        xExtent, yExtent, zExtent);
	}

}