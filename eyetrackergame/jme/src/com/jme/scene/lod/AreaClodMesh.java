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

package com.jme.scene.lod;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.logging.Logger;

import com.jme.renderer.Renderer;
import com.jme.scene.TexCoords;
import com.jme.scene.TriMesh;
import com.jme.util.AreaUtils;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;

/**
 * <code>AreaClodMesh</code> originally ported from David Eberly's c++,
 * modifications and enhancements made from there.<br>
 * <br>
 * This class is an automatically updating ClodMesh that updates records
 * according to how much area the bounding volume takes up on the screen. Use it
 * just like a normal ClodMesh, but allow it to update itself.
 * 
 * @author Joshua Slack
 * @author Jack Lindamood (javadoc only)
 * @version $Id: AreaClodMesh.java 4639 2009-08-29 00:34:05Z skye.book $
 */
public class AreaClodMesh extends ClodMesh {
    private static final Logger logger = Logger.getLogger(AreaClodMesh.class
            .getName());
    
	private static final long serialVersionUID = 1L;

	private float trisPerPixel = 1f;

	private float distTolerance = 1f;

	private float lastDistance = 0f;

	/**
	 * Empty Constructor to be used internally only.
	 */
	public AreaClodMesh() {
	}

	/**
	 * Creates a new AreaClodMesh with the given name.  This should only be used if
	 * the user is going to call reconstruct and create on the clod mesh.
	 * @param name The mesh's name.
	 * @see #reconstruct(com.jme.math.Vector3f[], com.jme.math.Vector3f[], com.jme.renderer.ColorRGBA[], com.jme.math.Vector2f[], int[])
	 * @see #create(com.jme.scene.lod.CollapseRecord[])
	 */
	public AreaClodMesh(String name) {
		super(name);
	}

	/**
	 * Creates a clod mesh that mimics the given TriMesh's geometry information.  More specifically,
	 * RenderState and Controller information is <b>not</b> absorbed by this AreaClodMesh.  A null
	 * for records causes the AreaClodMesh to generate its own records information.
	 * @param name The name of this new mesh.
	 * @param data The TriMesh to copy information into for this mesh.
	 * @param records The collapse record(s) this ClodMesh should use.  These modify how the ClodMesh
	 * collapses vertexes.
	 */
	public AreaClodMesh(String name, TriMesh data, CollapseRecord[] records) {

		super(name, data, records);
	}

	/**
	 * Creates a clod mesh with the given information.  A null for records causes the AreaClodMesh to
	 * generate its own records information.
	 * @param name The name of the ClodMesh.
	 * @param vertices The vertex information of this clod mesh.
	 * @param normal The per vertex normal information of this clod mesh.
	 * @param color The per vertex color information of this clod mesh.
	 * @param coords The per vertex texture information of this clod mesh.
	 * @param indices The index array of this TriMesh's triangles.
	 * @param records The collapse record(s) this ClodMesh should use.  These modify how the ClodMesh
	 * collapses vertexes.
	 */
	public AreaClodMesh(String name, FloatBuffer vertices, FloatBuffer normal,
			FloatBuffer color, TexCoords coords, IntBuffer indices,
			CollapseRecord[] records) {

		super(name, vertices, normal, color, coords, indices, records);

	}

	/**
	 * This function is used during rendering to choose the correct target record for the
	 * AreaClodMesh according to the information in the renderer.  This should not be called
	 * manually.  Instead, allow it to be called automatically during rendering.
	 * @param r The Renderer to use.
	 * @return the target record this AreaClodMesh will use to collapse vertexes.
	 */
	public int chooseTargetRecord(Renderer r) {
		if (getWorldBound() == null) {
			logger.warning("AreaClodMesh found with no Bounds.");
			return 0;
		}

		if (records == null || records.length == 0) {
			logger.warning("Records was null.");
			return 0;
		}

		float newDistance = getWorldBound().distanceTo(
				r.getCamera().getLocation());
		if (Math.abs(newDistance - lastDistance) <= distTolerance)
			return targetRecord; // we haven't moved relative to the model, send the old measurement back.
		if (lastDistance > newDistance && targetRecord == 0)
			return targetRecord; // we're already at the lowest setting and we just got closer to the model, no need to keep trying.
		if (lastDistance < newDistance && targetRecord == records.length - 1)
			return targetRecord; // we're already at the highest setting and we just got further from the model, no need to keep trying.

		lastDistance = newDistance;

		// estimate area of polygon via bounding volume
		float area = AreaUtils.calcScreenArea(getWorldBound(), lastDistance, r
				.getWidth());
		float trisToDraw = area * trisPerPixel;
		targetRecord = records.length - 1;
		for (int i = records.length; --i >= 0;) {
			if (trisToDraw - records[i].numbTriangles < 0)
				break;
			targetRecord = i;
		}
		return targetRecord;
	}

	/**
	 * This function is ignored by AreaClodMesh because target records are updated automatically
	 *  during draw.
	 * @param target Ignored.
	 */
	public void setTargetRecord(int target) {
		// ignore;
	}

	/**
	 * Returns the currently set number of triangles per pixel this AreaClodMesh should fit on
	 * the screen.  The default value is 1.
	 * @return The current Triangles per pixel.
	 */
	public float getTrisPerPixel() {
		return trisPerPixel;
	}

	/**
	 * Sets the number of triangles per pixel this AreaClodMesh should try to fit on the screen.
	 * The default value is 1.
	 * @param trisPerPixel The new value for Triangles per pixel.
	 */
	public void setTrisPerPixel(float trisPerPixel) {
		this.trisPerPixel = trisPerPixel;
	}

	/**
	 * Returns the amount of distance the camera must move from the center of this AreaClodMesh's
	 * bounding volume before a collapse is initiated.  The default is 1.
	 * @return The current distance tolerance of collapsing.
	 */
	public float getDistanceTolerance() {
		return distTolerance;
	}

	/**
	 * Sets the amount of distance the camera must move from the center of this AreaClodMesh's
	 * bounding volume before a collapse is initiated.  The default is 1.
	 * @param tolerance The new distance tolerance.
	 */
	public void setDistanceTolerance(float tolerance) {
		this.distTolerance = tolerance;
	}

	public void write(JMEExporter e) throws IOException {
		super.write(e);
		OutputCapsule capsule = e.getCapsule(this);
		capsule.write(trisPerPixel, "trisPerPixel", 1);
		capsule.write(distTolerance, "distTolerance", 1);
		capsule.write(lastDistance, "lastDistance", 0);
	}

	public void read(JMEImporter e) throws IOException {
		super.read(e);
		InputCapsule capsule = e.getCapsule(this);
		trisPerPixel = capsule.readFloat("trisPerPixel", 1);
		distTolerance = capsule.readFloat("distTolerance", 1);
		lastDistance = capsule.readFloat("lastDistance", 0);
	}
}
