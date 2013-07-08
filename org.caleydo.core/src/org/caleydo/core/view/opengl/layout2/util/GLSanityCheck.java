/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2.util;

import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.fixedfunc.GLMatrixFunc;

/**
 * utility class for holding a bunch of metrics to ensure that the opengl state doesn't change
 * 
 * @author Samuel Gratzl
 * 
 */
public class GLSanityCheck {
	private final int stackDepthAttrib;
	private final int stackDepthClientAttrib;
	private final int stackDepthModelView;
	private final int stackDepthName;

	private final int matrixMode;

	private GLSanityCheck(GL2 gl) {
		int[] vs = new int[1];
		gl.glGetIntegerv(GL2.GL_ATTRIB_STACK_DEPTH, vs, 0);
		stackDepthAttrib = vs[0];
		gl.glGetIntegerv(GL2.GL_CLIENT_ATTRIB_STACK_DEPTH, vs, 0);
		stackDepthClientAttrib = vs[0];
		gl.glGetIntegerv(GL2ES1.GL_MODELVIEW_STACK_DEPTH, vs, 0);
		stackDepthModelView = vs[0];
		gl.glGetIntegerv(GL2.GL_NAME_STACK_DEPTH, vs, 0);
		stackDepthName = vs[0];

		gl.glGetIntegerv(GLMatrixFunc.GL_MATRIX_MODE, vs, 0);
		matrixMode = vs[0];

	}

	public static GLSanityCheck create(GL2 gl) {
		return new GLSanityCheck(gl);
	}

	public boolean verify(GL2 gl) {
		return verify(gl, "");
	}
	public boolean verify(GL2 gl, String text) {
		GLSanityCheck after = create(gl);

		boolean noError = this.equals(after);

		if (noError)
			return true;

		StackTraceElement[] stackTrace = new Throwable().fillInStackTrace().getStackTrace();
		StackTraceElement caller = stackTrace[2];
		System.err.println(caller.toString() + " before: " + this + " after :" + after + ": " + text);
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + matrixMode;
		result = prime * result + stackDepthAttrib;
		result = prime * result + stackDepthClientAttrib;
		result = prime * result + stackDepthModelView;
		result = prime * result + stackDepthName;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GLSanityCheck other = (GLSanityCheck) obj;
		if (matrixMode != other.matrixMode)
			return false;
		if (stackDepthAttrib != other.stackDepthAttrib)
			return false;
		if (stackDepthClientAttrib != other.stackDepthClientAttrib)
			return false;
		if (stackDepthModelView != other.stackDepthModelView)
			return false;
		if (stackDepthName != other.stackDepthName)
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GLSanityCheck [stackDepthAttrib=");
		builder.append(stackDepthAttrib);
		builder.append(", stackDepthClientAttrib=");
		builder.append(stackDepthClientAttrib);
		builder.append(", stackDepthModelView=");
		builder.append(stackDepthModelView);
		builder.append(", stackDepthName=");
		builder.append(stackDepthName);
		builder.append(", matrixMode=");
		builder.append(matrixMode);
		builder.append("]");
		return builder.toString();
	}

}
