/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.util.spline;

import gleem.linalg.Vec2f;
import gleem.linalg.Vec3f;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUtessellator;
import javax.media.opengl.glu.GLUtessellatorCallback;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.util.gleem.ColoredVec2f;
import org.caleydo.core.view.opengl.util.gleem.ColoredVec3f;
import org.caleydo.core.view.opengl.util.gleem.IColored;
import org.caleydo.core.view.opengl.util.gleem.TexturedVec2f;

/**
 * a tesselation renderer supporting: {@link Vec3f}, {@link ColoredVec3f}, double[] and {@link Vec2f}
 *
 * @author Samuel Gratzl
 *
 */
public class TesselationRenderer {
	private GLUtessellator tesselator;
	private final TesselationCallback tessCallback;


	public TesselationRenderer() {
		tesselator = GLU.gluNewTess();
		tessCallback = new TesselationCallback();

		GLU.gluTessCallback(tesselator, GLU.GLU_TESS_VERTEX, tessCallback);// vertexCallback);
		GLU.gluTessCallback(tesselator, GLU.GLU_TESS_BEGIN, tessCallback);// beginCallback);
		GLU.gluTessCallback(tesselator, GLU.GLU_TESS_END, tessCallback);// endCallback);
		GLU.gluTessCallback(tesselator, GLU.GLU_TESS_ERROR, tessCallback);// errorCallback);
		GLU.gluTessCallback(tesselator, GLU.GLU_TESS_COMBINE, tessCallback);// combineCallback);
	}

	public void destroy() {
		GLU.gluDeleteTess(tesselator);
		tesselator = null;
	}

	@Override
	protected void finalize() throws Throwable {
		if (tesselator != null)
			GLU.gluDeleteTess(tesselator);
		super.finalize();
	}

	public GLUtessellator begin(GLGraphics g) {
		tessCallback.gl = g.gl;
		tessCallback.g = g;
		return tesselator;
	}

	public void end() {
		tessCallback.gl = null;
		tessCallback.g = null;
	}

	private class TesselationCallback implements GLUtessellatorCallback {
		GLGraphics g;
		GL2 gl;

		@Override
		public void begin(int type) {
			// gl.glPolygonMode(gl.GL_FRONT_AND_BACK, gl.GL_LINE);
			gl.glBegin(type);
		}

		@Override
		public void end() {
			gl.glEnd();
		}

		@Override
		public void vertex(Object vertexData) {
			checkColor(vertexData);
			checkTexture(vertexData);
			if (vertexData instanceof double[]) {
				double[] pointer = (double[]) vertexData;
				assert pointer.length >= 3;
				switch (pointer.length) { // also colors?
				case 3 + 3:
					gl.glColor3dv(pointer, 3);
					break;
				case 3 + 4:
					gl.glColor4dv(pointer, 3);
					break;
				}
				gl.glVertex3dv(pointer, 0);
			} else if (vertexData instanceof Vec3f) {
				Vec3f v = (Vec3f) vertexData;
				gl.glVertex3f(v.x(), v.y(), v.z());
			} else if (vertexData instanceof Vec2f) {
				Vec2f v = (Vec2f) vertexData;
				gl.glVertex3f(v.x(), v.y(), g.z());
			}
		}

		private void checkColor(Object v) {
			if (v instanceof IColored) {
				IColored cv = (IColored) v;
				gl.glColor4fv(cv.getColor().getRGBA(), 0);
			}
		}

		private void checkTexture(Object v) {
			if (v instanceof TexturedVec2f) {
				TexturedVec2f cv = (TexturedVec2f) v;
				Vec2f c = cv.getTexCoords();
				if (c != null)
					gl.glTexCoord2f(c.x(), c.y());
			}
		}

		@Override
		public void vertexData(Object vertexData, Object polygonData) {
		}

		/**
		 * combineCallback is used to create a new vertex when edges intersect. coordinate location is trivial to
		 * calculate, but weight[4] may be used to average color, normal, or texture coordinate data. In this program,
		 * color is weighted.
		 */
		@Override
		public void combine(double[] coords, Object[] data, //
				float[] weight, Object[] outData) {
			Object type = data[0];

			if (type instanceof double[]) {
				final int length = ((double[]) data[0]).length;
				assert length >= 3;

				double[] vertex = new double[length];
				// copy the new coordinates
				vertex[0] = coords[0];
				vertex[1] = coords[1];
				vertex[2] = coords[2];

				if (length > 3) { // copy the weighted rest
					for (int i = 3; i < length; ++i) {
						float v = 0;
						for (int j = 0; j < weight.length; ++j) {
							double[] d = (double[]) data[j];
							if (d == null)
								continue;
							v += weight[j] * d[i];
						}
						vertex[i] = v;
					}
				}

				outData[0] = vertex;
			} else if (type instanceof ColoredVec3f) {
				ColoredVec3f cv = new ColoredVec3f();
				cv.set((float) coords[0], (float) coords[1], (float) coords[2]);
				cv.setColor(interpolateColor(data, weight));
				outData[0] = cv;
			} else if (type instanceof Vec3f) {
				Vec3f v = new Vec3f();
				v.set((float) coords[0], (float) coords[1], (float) coords[2]);
				outData[0] = v;
			} else if (type instanceof ColoredVec2f) {
				ColoredVec2f v = new ColoredVec2f();
				v.set((float) coords[0], (float) coords[1]);
				v.setColor(interpolateColor(data, weight));
				outData[0] = v;
			} else if (type instanceof TexturedVec2f) {
				TexturedVec2f v = new TexturedVec2f();
				v.set((float) coords[0], (float) coords[1]);
				v.setTexCoords(interpolateTexel(data, weight));
				outData[0] = v;
			} else if (type instanceof Vec2f) {
				Vec2f v = new Vec2f();
				v.set((float) coords[0], (float) coords[1]);
				outData[0] = v;
			}
		}

		private Color interpolateColor(Object[] data, float[] weight) {
			float r = 0, g = 0, b = 0, a = 0;
			for (int j = 0; j < weight.length; ++j) {
				IColored d = (IColored) data[j];
				if (d == null)
					continue;
				float[] rgba = d.getColor().getRGBA();
				r += weight[j] * rgba[0];
				g += weight[j] * rgba[1];
				b += weight[j] * rgba[2];
				a += weight[j] * rgba[3];
			}
			Color c = new Color(r, g, b, a);
			return c;
		}

		private Vec2f interpolateTexel(Object[] data, float[] weight) {
			float s = 0, t = 0;
			for (int j = 0; j < weight.length; ++j) {
				TexturedVec2f d = (TexturedVec2f) data[j];
				if (d == null)
					continue;
				Vec2f st = d.getTexCoords();
				s += st == null ? 0 : weight[j] * st.x();
				t += st == null ? 0 : weight[j] * st.y();
			}
			return new Vec2f(s, t);
		}

		@Override
		public void combineData(double[] coords, Object[] data, //
				float[] weight, Object[] outData, Object polygonData) {
		}

		@Override
		public void error(int errnum) {
			// GLU glu = GLU.createGLU();
			// String err = glu.gluErrorString(errnum);
			System.err.println("Tessellation Error: " + errnum);
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

	/**
	 * renders the given 3d vertices
	 *
	 * @param g
	 * @param vertices
	 */
	public void render3(GLGraphics g, Iterable<Vec3f> vertices) {
		GLUtessellator tesselator = begin(g);
		GLU.gluTessBeginPolygon(tesselator, null);
		{
			GLU.gluTessBeginContour(tesselator);

			for (Vec3f v : vertices) {
				GLU.gluTessVertex(tesselator, asDoubleArray(v), 0, v);
			}

			GLU.gluTessEndContour(tesselator);
		}
		GLU.gluTessEndPolygon(tesselator);
		end();
	}

	/**
	 * renderes the given 2d vertices
	 *
	 * @param g
	 * @param vertices
	 */
	public void render2(GLGraphics g, Iterable<Vec2f> vertices) {
		GLUtessellator tesselator = begin(g);
		GLU.gluTessBeginPolygon(tesselator, null);
		{
			GLU.gluTessBeginContour(tesselator);

			for (Vec2f v : vertices) {
				GLU.gluTessVertex(tesselator, asDoubleArray(v, g.z()), 0, v);
			}

			GLU.gluTessEndContour(tesselator);
		}
		GLU.gluTessEndPolygon(tesselator);
		end();
	}

	/**
	 * set the winding rule property
	 *
	 * @param windingRule
	 */
	public void setWindingRule(int windingRule) {
		GLU.gluTessProperty(tesselator, GLU.GLU_TESS_WINDING_RULE, windingRule);
	}


	public static double[] asDoubleArray(Vec3f v) {
		return new double[] { v.x(), v.y(), v.z() };
	}

	public static double[] asDoubleArray(Vec2f v, float z) {
		return new double[] { v.x(), v.y(), z };
	}
}
