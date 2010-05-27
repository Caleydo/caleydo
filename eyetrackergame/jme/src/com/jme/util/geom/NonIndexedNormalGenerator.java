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

package com.jme.util.geom;

import java.util.Arrays;

import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.scene.TriMesh;

/**
 * A utility class to generate normals for a set of vertices. The triangles 
 * must be defined by just the vertices, so that every 3 consecutive vertices 
 * define one triangle. However, an index array must be specified to identify 
 * identical vertices properly (see method 
 * {@link #generateNormals(float[], int[], float)}. If the index aray is not 
 * specified, the vertex normals are currently simply taken from the faces they 
 * belong to (this might be changed in the future, so that vertices are 
 * compared by their values). 
 * 
 * @version 2008-03-11
 * @author Michael Sattler
 */
public class NonIndexedNormalGenerator {
	
	private Vector3f temp1 = new Vector3f();
	
	private Vector3f temp2 = new Vector3f();
	
	private Vector3f temp3 = new Vector3f();
	
	private int[] indices;
	
	private float creaseAngle;
	
	private float[] faceNormals;
	
	private int[] normalsToSet;
	
	/**
     * Calculates the normals for a set of faces determined by the specified
     * vertices. Every 3 consecutive vertices define one triangle.<br />
     * <strong>Please note:</strong> This method uses class fields and is not
     * synchronized! Therefore it should only be called from a single thread,
     * unless synchronization is taken care of externally.
     * 
     * @param vertices
     *            The vertex coordinates. Every three values define one vertex
     * @param indices
     *            An array containing int values. Each value belongs to one
     *            vertex in the <code>vertices</code> array, the values are
     *            stored in the same order as the vertices. For equal vertices
     *            in the <code>vertices</code> array, the indices are also
     *            equal.
     * @param creaseAngle
     *            The maximum angle in radians between faces to which normals
     *            between the faces are interpolated to create a smooth
     *            transition
     * @return An array containing the generated normals for the geometry
     */
	public float[] generateNormals(
			float[] vertices, int[] indices, float creaseAngle) {
		
		this.indices = indices;
		this.creaseAngle = creaseAngle;
		this.normalsToSet = new int[10];
		Arrays.fill(normalsToSet, -1);
		
		initFaceNormals(vertices);
		
		if (creaseAngle < 0.0f + FastMath.ZERO_TOLERANCE
				|| indices == null) {
			return getFacetedVertexNormals();
		}
		return getVertexNormals();
	}
	
	/**
	 * Initializes the array <code>faceNormals</code> with the normals of all 
	 * faces (triangles) of the mesh.
	 * @param vertices The array containing all vertex coordinates
	 */
	private void initFaceNormals(float[] vertices) {
		faceNormals = new float[vertices.length / 3];
		
		for (int i = 0; i * 9 < vertices.length; i++) {
			temp1.set(vertices[i * 9 + 0], 
					  vertices[i * 9 + 1], 
					  vertices[i * 9 + 2]);
			temp2.set(vertices[i * 9 + 3], 
					  vertices[i * 9 + 4], 
					  vertices[i * 9 + 5]);
			temp3.set(vertices[i * 9 + 6], 
					  vertices[i * 9 + 7], 
					  vertices[i * 9 + 8]);
			
			temp2.subtractLocal(temp1); // A -> B
			temp3.subtractLocal(temp1); // A -> C
			
			temp2.cross(temp3, temp1);
			temp1.normalizeLocal(); // Normal
			
			faceNormals[i * 3 + 0] = temp1.x;
			faceNormals[i * 3 + 1] = temp1.y;
			faceNormals[i * 3 + 2] = temp1.z;
		}
	}
	
	/**
	 * Creates an array containing the interpolated normals for all vertices
	 * @return The array with the vertex normals
	 */
	private float[] getVertexNormals() {
		
		float[] normals = new float[faceNormals.length * 3];
		boolean[] setNormals = new boolean[faceNormals.length];
		
		for (int i = 0; i * 3 < faceNormals.length; i++) {
			for (int j = 0; j < 3; j++) {
				if (!setNormals[i * 3 + j]) {
					setInterpolatedNormal(normals, setNormals, i, j);
				}
			}
		}
		
		return normals;
	}
	
	/**
	 * Computes the interpolated normal for the specified vertex of the 
	 * specified face and applies it to all identical vertices for which the 
	 * normal is interpolated.
	 * @param normals The array to store the vertex normals
	 * @param setNormals An array indicating which vertex normals have already 
	 * been set
	 * @param face The index of the face containing the current vertex
	 * @param vertex The index of the vertex inside the face (0 - 2)
	 */
	private void setInterpolatedNormal(
			float[] normals, boolean[] setNormals, int face, int vertex) {
		
		// temp1: Normal of the face the specified vertex belongs to
		temp1.set(faceNormals[face * 3 + 0], 
				  faceNormals[face * 3 + 1], 
				  faceNormals[face * 3 + 2]);
		
		// temp2: Sum of all face normals to be interpolated
		temp2.set(temp1);
		
		int vertIndex = indices[face * 3 + vertex];
		normalsToSet[0] = face * 3 + vertex;
		int count = 1;
		
		
		/*
		 * Get the normals of all faces containing the specified vertex whose 
		 * angle to the specified one is less than the crease angle
		 */
		for (int i = face * 3 + vertex + 1; i < indices.length; i++) {
			if (indices[i] == vertIndex && !setNormals[face * 3 + vertex]) {
				// temp3: Normal of the face the current vertex belongs to
				temp3.set(faceNormals[(i / 3) * 3 + 0], 
						  faceNormals[(i / 3) * 3 + 1], 
						  faceNormals[(i / 3) * 3 + 2]);
				if (temp1.angleBetween(temp3) < creaseAngle) {
					normalsToSet = setValue(normalsToSet, count, i);
					count++;
					temp2.add(temp3);
				}
			}
		}
		
		temp2.normalizeLocal();
		
		// Set the normals for all vertices marked for interpolation
		for (int i = 0; i < normalsToSet.length && normalsToSet[i] != -1; i++) {
			normals[normalsToSet[i] * 3 + 0] = temp2.x;
			normals[normalsToSet[i] * 3 + 1] = temp2.y;
			normals[normalsToSet[i] * 3 + 2] = temp2.z;
			setNormals[normalsToSet[i]] = true;
			normalsToSet[i] = -1;
		}
	}
	
	/**
	 * Puts the value into the array at the specified index. If the index is 
	 * out of bounds, an new array with a length of 3 fields more than the 
	 * specified one is created first and the values copied to it. 
	 * @param array The array
	 * @param index The index to insert the value
	 * @param value The value to insert
	 * @return The array with the values, either the specified one or the new 
	 * one
	 */
	private int[] setValue(int[] array, int index, int value) {
		if (index >= array.length) {
			int[] temp = new int[array.length + 3];
			Arrays.fill(temp, -1);
			System.arraycopy(array, 0, temp, 0, array.length);
			array = temp;
		}
		
		array[index] = value;
		return array;
	}
	
	/**
	 * Simply copies the face normals to the vertices contained in each face, 
	 * creating a faceted appearance.
	 * @return The vertex normals
	 */
	private float[] getFacetedVertexNormals() {
		float[] normals = new float[faceNormals.length * 3];
		for (int i = 0; i * 3 < faceNormals.length; i++) {
			for (int j = 0; j < 3; j++) {
				normals[i * 9 + j + 0] = faceNormals[i * 3 + j];
				normals[i * 9 + j + 3] = faceNormals[i * 3 + j];
				normals[i * 9 + j + 6] = faceNormals[i * 3 + j];
			}
		}
		return normals;
	}
	
	
	public void generateNormals(TriMesh mesh) {
		
	}
}
