/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES2;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.util.logging.Logger;

import com.google.common.io.CharStreams;
import com.jogamp.opengl.util.glsl.ShaderUtil;

/**
 * @author Samuel Gratzl
 *
 */
public class GLGraphicsUtils {
	private static final Logger log = Logger.create(GLGraphicsUtils.class);

	public static int compileShader(GL2 gl, String vertexShader, String fragmentShader) {
		if (!ShaderUtil.isShaderCompilerAvailable(gl)) {
			log.error("no shaders available!", new Throwable());
			return -1;
		}

		int vs = gl.glCreateShader(GL2ES2.GL_VERTEX_SHADER);
		gl.glShaderSource(vs, 1, new String[] { vertexShader }, (int[]) null, 0);
		gl.glCompileShader(vs);

		ByteArrayOutputStream slog = new ByteArrayOutputStream();
		PrintStream slog_print = new PrintStream(slog);

		if (!ShaderUtil.isShaderStatusValid(gl, vs, GL2ES2.GL_COMPILE_STATUS, slog_print)) {
			gl.glDeleteShader(vs);
			log.error("can't compile vertex shader: " + slog.toString(), new Throwable());
			return -1;
		} else {
			log.debug("compiling vertex shader warnings: " + ShaderUtil.getShaderInfoLog(gl, vs));
		}

		int fs = gl.glCreateShader(GL2ES2.GL_FRAGMENT_SHADER);
		gl.glShaderSource(fs, 1, new String[] { fragmentShader }, (int[]) null, 0);
		gl.glCompileShader(fs);
		if (!ShaderUtil.isShaderStatusValid(gl, vs, GL2ES2.GL_COMPILE_STATUS, slog_print)) {
			gl.glDeleteShader(vs);
			gl.glDeleteShader(fs);
			log.error("can't compile fragment shader: " + slog.toString(), new Throwable());
			return -1;
		} else {
			log.debug("compiling fragment shader warnings: " + ShaderUtil.getShaderInfoLog(gl, fs));
		}

		int programId = gl.glCreateProgram();
		gl.glAttachShader(programId, vs);
		gl.glAttachShader(programId, fs);
		gl.glLinkProgram(programId);
		gl.glValidateProgram(programId);

		// Mark shaders for deletion (they will not be freed as long as they
		// are attached to a program)
		gl.glDeleteShader(vs);
		gl.glDeleteShader(fs);

		if (!ShaderUtil.isProgramLinkStatusValid(gl, programId, slog_print)) {
			gl.glDeleteProgram(programId);
			log.error("can't link program: " + slog.toString(), new Throwable());
			return -1;
		} else {
			log.debug("linking program warnings: " + ShaderUtil.getProgramInfoLog(gl, programId));
		}

		return programId;
	}

	/**
	 * Load and compile vertex and fragment shader
	 *
	 * @param vertexShader
	 * @param fragmentShader
	 * @return
	 * @throws IOException
	 */
	public static int loadShader(GL2 gl, InputStream vertexShader, InputStream fragmentShader) throws IOException {
		String vsrc = CharStreams.toString(new InputStreamReader(vertexShader));
		String fsrc = CharStreams.toString(new InputStreamReader(fragmentShader));
		return compileShader(gl, vsrc, fsrc);
	}

	/**
	 * similar to {@link #checkError()} but just return the state without printing a message
	 * 
	 * @return whether an error was found
	 */
	public static boolean clearError(GL2 gl) {
		int error = gl.glGetError();
		return error > 0;
	}

	/**
	 * checks for errors and prints a {@link System#err} message
	 * 
	 * @param text
	 *            description message
	 * @return whether an error was found
	 */
	public static boolean checkError(GL2 gl) {
		int error = gl.glGetError();
		if (error > 0) {
			StackTraceElement[] stackTrace = new Throwable().fillInStackTrace().getStackTrace();
			StackTraceElement caller = stackTrace[1];
			GLU glu = new GLU();
			System.err.println(caller.toString() + " " + error + " " + glu.gluErrorString(error) + " ");
			return true;
		}
		return false;
	}

	public static boolean isPickingPass(GL2 gl) {
		int[] r = new int[1];
		gl.glGetIntegerv(GL2.GL_RENDER_MODE, r, 0);
		return r[0] == GL2.GL_SELECT;
	}

}
