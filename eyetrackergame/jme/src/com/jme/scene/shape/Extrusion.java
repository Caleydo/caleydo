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
package com.jme.scene.shape;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Line;
import com.jme.scene.TriMesh;
import com.jme.util.geom.BufferUtils;

/**
 * An extrusion of a 2D object ({@link Line}) along a path (List of Vector3f).
 * Either a convenience constructor can be used or the {@link #updateGeometry}
 * method. It is also capable of doing a cubic spline interpolation for a list
 * of supporting points
 * 
 * @author Irrisor
 */
public class Extrusion extends TriMesh {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Default Constructor. Creates an empty Extrusion.
     * 
     * @see #updateGeometry(Line, List, Vector3f)
     * @see #updateGeometry(Line, List, boolean, Vector3f)
     * @see #updateGeometry(Line, List, int, Vector3f)
     * @see #updateGeometry(Line, List, int, boolean, Vector3f)
     */
    public Extrusion() {
    }

    /**
     * Creates an empty named Extrusion.
     * 
     * @param name
     *            name
     * @see #updateGeometry(Line, List, Vector3f)
     * @see #updateGeometry(Line, List, boolean, Vector3f)
     * @see #updateGeometry(Line, List, int, Vector3f)
     * @see #updateGeometry(Line, List, int, boolean, Vector3f)
     */
    public Extrusion(String name) {
        super(name);
    }

    /**
     * Convenience constructor. Calls
     * {@link #updateGeometry(Line, List, Vector3f)}.
     * 
     * @param shape
     *            see {@link #updateGeometry(Line, List, Vector3f)}
     * @param path
     *            see {@link #updateGeometry(Line, List, Vector3f)}
     * @param up
     *            up vector
     */
    public Extrusion(Line shape, List<Vector3f> path, Vector3f up) {
        updateGeometry(shape, path, up);
    }

    /**
     * Convenience constructor. Sets the name and calls
     * {@link #updateGeometry(Line, List, Vector3f)}.
     * 
     * @param name
     *            name
     * @param shape
     *            see {@link #updateGeometry(Line, List, Vector3f)}
     * @param path
     *            see {@link #updateGeometry(Line, List, Vector3f)}
     * @param up
     *            up vector
     */
    public Extrusion(String name, Line shape, List<Vector3f> path, Vector3f up) {
        super(name);
        updateGeometry(shape, path, up);
    }

    /**
     * Update vertex, color, index and texture buffers (0) to contain an
     * extrusion of shape along path.
     * 
     * @param shape
     *            an instance of Line that describes the 2D shape
     * @param path
     *            a list of vectors that describe the path the shape should be
     *            extruded
     * @param up
     *            up vector
     */
    public void updateGeometry(Line shape, List<Vector3f> path, Vector3f up) {
        updateGeometry(shape, path, false, up);
    }

    /**
     * Update vertex, color, index and texture buffers (0) to contain an
     * extrusion of shape along path.
     * 
     * @param shape
     *            an instance of Line that describes the 2D shape
     * @param path
     *            a list of vectors that describe the path the shape should be
     *            extruded
     * @param closed
     *            true to connect first and last point
     * @param up
     *            up vector
     */
    public void updateGeometry(Line shape, List<Vector3f> path, boolean closed,
            Vector3f up) {
        FloatBuffer shapeBuffer = shape.getVertexBuffer();
        FloatBuffer shapeNormalBuffer = shape.getNormalBuffer();

        FloatBuffer vertices;
        FloatBuffer normals;
        int numVertices = path.size() * shapeBuffer.limit();
        if (getVertexBuffer() != null
                && getVertexBuffer().limit() == numVertices) {
            vertices = getVertexBuffer();
            normals = getNormalBuffer();
            vertices.rewind();
            normals.rewind();
        } else {
            vertices = BufferUtils.createFloatBuffer(numVertices);
            normals = BufferUtils.createFloatBuffer(numVertices);
        }
        int numIndices = (path.size() - 1) * 2 * shapeBuffer.limit();
        IntBuffer indices;
        if (getIndexBuffer() != null && getIndexBuffer().limit() == numIndices) {
            indices = getIndexBuffer();
            indices.rewind();
        } else {
            indices = BufferUtils.createIntBuffer(numIndices);
        }

        int shapeVertices = shapeBuffer.limit() / 3;
        Vector3f vector = new Vector3f();
        Vector3f direction = new Vector3f();
        Quaternion rotation = new Quaternion();
        for (int i = 0; i < path.size(); i++) {
            Vector3f point = path.get(i);
            shapeBuffer.rewind();
            shapeNormalBuffer.rewind();
            int shapeVertice = 0;
            do {

                Vector3f nextPoint = i < path.size() - 1 ? path.get(i + 1)
                        : closed ? path.get(0) : null;
                Vector3f lastPoint = i > 0 ? path.get(i - 1) : null;
                if (nextPoint != null) {
                    direction.set(nextPoint).subtractLocal(point);
                } else {
                    direction.set(point).subtractLocal(lastPoint);
                }
                rotation.lookAt(direction, up);

                vector.set(shapeNormalBuffer.get(), shapeNormalBuffer.get(),
                        shapeNormalBuffer.get());
                rotation.multLocal(vector);
                normals.put(vector.x);
                normals.put(vector.y);
                normals.put(vector.z);

                vector.set(shapeBuffer.get(), shapeBuffer.get(), shapeBuffer
                        .get());
                rotation.multLocal(vector);
                vector.addLocal(point);
                vertices.put(vector.x);
                vertices.put(vector.y);
                vertices.put(vector.z);

                if ((shapeVertice & 1) == 0) {
                    if (i < path.size() - 1) {
                        indices.put(i * shapeVertices + shapeVertice);
                        indices.put(i * shapeVertices + shapeVertice + 1);
                        indices.put((i + 1) * shapeVertices + shapeVertice);

                        indices.put((i + 1) * shapeVertices + shapeVertice + 1);
                        indices.put((i + 1) * shapeVertices + shapeVertice);
                        indices.put(i * shapeVertices + shapeVertice + 1);
                    } else if (closed) {
                        indices.put(i * shapeVertices + shapeVertice);
                        indices.put(i * shapeVertices + shapeVertice + 1);
                        indices.put(0 + shapeVertice);

                        indices.put(0 + shapeVertice + 1);
                        indices.put(0 + shapeVertice);
                        indices.put(i * shapeVertices + shapeVertice + 1);
                    }
                }
                shapeVertice++;
            } while (shapeBuffer.hasRemaining());
        }

        setVertexBuffer(vertices);
        setNormalBuffer(normals);
        setIndexBuffer(indices);
    }

    /**
     * Performs cubic spline interpolation to find a path through the supporting
     * points where the second derivative is zero. Then calls
     * {@link #updateGeometry(Line, List, Vector3f)} with this path.
     * 
     * @param shape
     *            an instance of Line that describes the 2D shape
     * @param points
     *            a list of supporting points for the spline interpolation
     * @param segments
     *            number of resulting path segments per supporting point
     * @param up
     *            up vector
     */
    public void updateGeometry(Line shape, List<Vector3f> points, int segments,
            Vector3f up) {
        updateGeometry(shape, points, segments, false, up);
    }

    /**
     * Performs cubic spline interpolation to find a path through the supporting
     * points where the second derivative is zero. Then calls
     * {@link #updateGeometry(Line, List, boolean, Vector3f)} with this path.
     * 
     * @param shape
     *            an instance of Line that describes the 2D shape
     * @param points
     *            a list of supporting points for the spline interpolation
     * @param segments
     *            number of resulting path segments per supporting point
     * @param closed
     *            true to close the shape (connect last and first point)
     * @param up
     *            up vector
     */
    public void updateGeometry(Line shape, List<Vector3f> points, int segments,
            boolean closed, Vector3f up) {
        int np = points.size(); // number of points
        if (closed) {
            np = np + 3;
        }
        float d[][] = new float[3][np]; // Newton form coefficients
        float x[] = new float[np]; // x-coordinates of nodes

        List<Vector3f> path = new ArrayList<Vector3f>();

        for (int i = 0; i < np; i++) {
            Vector3f p;
            if (!closed) {
                p = points.get(i);
            } else {
                if (i == 0) {
                    p = points.get(points.size() - 1);
                } else if (i >= np - 2) {
                    p = points.get(i - np + 2);
                } else {
                    p = points.get(i - 1);
                }
            }
            x[i] = i;
            d[0][i] = p.x;
            d[1][i] = p.y;
            d[2][i] = p.z;
        }

        if (np > 1) {
            float[][] a = new float[3][np];
            float h[] = new float[np];
            for (int i = 1; i <= np - 1; i++) {
                h[i] = x[i] - x[i - 1];
            }
            if (np > 2) {
                float sub[] = new float[np - 1];
                float diag[] = new float[np - 1];
                float sup[] = new float[np - 1];

                for (int i = 1; i <= np - 2; i++) {
                    diag[i] = (h[i] + h[i + 1]) / 3;
                    sup[i] = h[i + 1] / 6;
                    sub[i] = h[i] / 6;
                    for (int dim = 0; dim < 3; dim++) {
                        a[dim][i] = (d[dim][i + 1] - d[dim][i]) / h[i + 1]
                                - (d[dim][i] - d[dim][i - 1]) / h[i];
                    }
                }
                for (int dim = 0; dim < 3; dim++) {
                    solveTridiag(sub.clone(), diag.clone(), sup.clone(),
                            a[dim], np - 2);
                }
            }
            // note that a[0]=a[np-1]=0
            // draw
            if (!closed) {
                path.add(new Vector3f(d[0][0], d[1][0], d[2][0]));
            }
            float[] point = new float[3];
            for (int i = closed ? 2 : 1; i <= np - 2; i++) { // loop over
                                                                // intervals
                                                                // between nodes
                for (int j = 1; j <= segments; j++) {
                    for (int dim = 0; dim < 3; dim++) {
                        float t1 = (h[i] * j) / segments;
                        float t2 = h[i] - t1;
                        float v = ((-a[dim][i - 1] / 6 * (t2 + h[i]) * t1 + d[dim][i - 1])
                                * t2 + (-a[dim][i] / 6 * (t1 + h[i]) * t2 + d[dim][i])
                                * t1)
                                / h[i];
                        // float t = x[i - 1] + t1;
                        point[dim] = v;
                    }
                    path.add(new Vector3f(point[0], point[1], point[2]));
                }
            }
        }

        this.updateGeometry(shape, path, closed, up);
    }

    /*
     * solve linear system with tridiagonal n by n matrix a using Gaussian
     * elimination without pivoting where a(i,i-1) = sub[i] for 2<=i<=n a(i,i) =
     * diag[i] for 1<=i<=n a(i,i+1) = sup[i] for 1<=i<=n-1 (the values
     * sub[1], sup[n] are ignored) right hand side vector b[1:n] is overwritten
     * with solution NOTE: 1...n is used in all arrays, 0 is unused
     */
    private static void solveTridiag(float sub[], float diag[], float sup[],
            float b[], int n) {
        // factorization and forward substitution
        for (int i = 2; i <= n; i++) {
            sub[i] = sub[i] / diag[i - 1];
            diag[i] = diag[i] - sub[i] * sup[i - 1];
            b[i] = b[i] - sub[i] * b[i - 1];
        }
        b[n] = b[n] / diag[n];
        for (int i = n - 1; i >= 1; i--) {
            b[i] = (b[i] - sup[i] * b[i + 1]) / diag[i];
        }
    }
}

/*
 * $Log: Extrusion.java,v $ Revision 1.6 2007/04/03 14:30:17 nca ISSUE MINOR:
 * Lots of code clean-up (imports, qualifiers, brackets, etc) BoundingSphere
 * fix. ParticleSystem fix. Revision 1.5 2007/03/06 15:16:06 nca ISSUE MINOR:
 * Doc update Revision 1.4 2007/02/06 11:23:15 irrisor Topic 4479: mapping of
 * mouse buttons in awt events and query methods aligned; extracted static
 * method to set up AWTMouseInput. Revision 1.3 2006/12/16 13:51:44 irrisor
 * MINOR: tidied some comments Revision 1.2 2006/12/15 16:38:18 irrisor MINOR:
 * JavaDoc corrected Revision 1.1 2006/12/15 15:57:30 irrisor MINOR: Added
 * extrusion shape plus some helper methods MINOR: some javadoc corrected
 */
