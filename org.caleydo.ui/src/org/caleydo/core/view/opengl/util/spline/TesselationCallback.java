/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.util.spline;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUtessellatorCallback;

/*
 * Tessellator callback implemenation with all the callback routines. YOu could use
 * GLUtesselatorCallBackAdapter instead. But
 */
public class TesselationCallback
	implements GLUtessellatorCallback {
	protected  GL2 gl;
	protected GLU glu;

	public TesselationCallback(GL2 gl, GLU glu) {
		this.gl = gl;
		this.glu = glu;
	}

	@Override
	public void begin(int type) {
		gl.glBegin(type);
	}

	@Override
	public void end() {
		gl.glEnd();
	}

	@Override
	public void vertex(Object vertexData) {
		double[] pointer;
		if (vertexData instanceof double[]) {
			pointer = (double[]) vertexData;
			// gl.glEnable(GL.GL_BLEND);
			// gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
			// gl.glColor4f(0f,0f,0f, 0.5f);
			// gl.glColor3dv(pointer, 3);
			gl.glVertex3dv(pointer, 0);
		}
	}

	@Override
	public void vertexData(Object vertexData, Object polygonData) {
	}

	/*
	 * combineCallback is used to create a new vertex when edges intersect. coordinate location is trivial to
	 * calculate, but weight[4] may be used to average color, normal, or texture coordinate data. In this
	 * program, color is weighted.
	 */
	@Override
	public void combine(double[] coords, Object[] data, //
		float[] weight, Object[] outData) {
		double[] vertex = new double[6];
		// int i;

		vertex[0] = coords[0];
		vertex[1] = coords[1];
		vertex[2] = coords[2];
		// for (i = 3; i < 6/* 7OutOfBounds from C! */; i++)
		// vertex[i] = weight[0] //
		// * ((double[]) data[0])[i]
		// + weight[1]
		// * ((double[]) data[1])[i]
		// + weight[2]
		// * ((double[]) data[2])[i]
		// + weight[3]
		// * ((double[]) data[3])[i];
		outData[0] = vertex;
	}

	@Override
	public void combineData(double[] coords, Object[] data, //
		float[] weight, Object[] outData, Object polygonData) {
	}

	@Override
	public void error(int errnum) {
		String estring;

		estring = glu.gluErrorString(errnum);
		System.err.println("Tessellation Error: " + estring);
		System.exit(0);
	}

	@Override
	public void beginData(int type, Object polygonData) {
	}

	@Override
	public void endData(Object polygonData) {
	}

	@Override
	public void edgeFlag(boolean boundaryEdge) {
	}

	@Override
	public void edgeFlagData(boolean boundaryEdge, Object polygonData) {
	}

	@Override
	public void errorData(int errnum, Object polygonData) {
	}
}
