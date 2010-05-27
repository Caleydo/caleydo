// Copyright © 2008 JMonkeyEngine, all rights reserved.
// See the accompanying LICENSE file for terms and conditions of use.
// $Id: GeoSphere.java 4131 2009-03-19 20:15:28Z blaine.dev $
package com.jme.scene.shape;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.scene.TexCoords;
import com.jme.scene.TriMesh;
import com.jme.util.geom.BufferUtils;

/**
 * A polygon mesh approximating a sphere by recursive subdivision.
 * <p>
 * First approximation is an octahedron; each level of refinement increases the
 * number of polygons by a factor of 4.
 * <p>
 * Shared vertices are not retained, so numerical errors may produce cracks
 * between polygons at high subdivision levels.
 * <p>
 * TODO: texture co-ordinates could be nicer
 * 
 * @author John Leech - initial idea and original C implementation
 * @author Irrisor - Java port and JME optimisation
 * @version $Revision: 4131 $, $Date: 2009-03-19 21:15:28 +0100 (Do, 19 Mär 2009) $
 */
public class GeoSphere extends TriMesh {

    private static final long serialVersionUID = 1L;

    private int numLevels;

    private boolean usingIcosahedron = true;

    /** <strong>NOT API:</strong> for internal use, do not call from user code. */
    public GeoSphere() {}

    /**
     * @param name the name of the spatial
     * @param ikosa
     *            true to start with an 20 triangles, false to start with 8
     *            triangles
     * @param maxlevels
     *            an integer >= 1 setting the recursion level
     * @see jmetest.shape.TestGeoSphere
     */
    public GeoSphere(String name, boolean ikosa, int maxlevels) {
        super(name);
        updateGeometry(maxlevels, ikosa);
    }

    /**
     * Compute the average of two vectors.
     * 
     * @param a
     *            first vector
     * @param b
     *            second vector
     * @return the average of two points
     */
    private Vector3f createMidpoint(Vector3f a, Vector3f b) {
        return new Vector3f((a.x + b.x) * 0.5f, (a.y + b.y) * 0.5f,
                (a.z + b.z) * 0.5f);
    }

    public int getNumLevels() {
        return numLevels;
    }

    /**
     * TODO: radius is always 1
     * 
     * @return 1
     */
    public float getRadius() {
        return 1;
    }

    public boolean isUsingIcosahedron() {
        return usingIcosahedron;
    }

    private void put(Vector3f vec) {
        FloatBuffer vertBuf = getVertexBuffer();
        vertBuf.put(vec.x);
        vertBuf.put(vec.y);
        vertBuf.put(vec.z);

        float length = vec.length();
        FloatBuffer normBuf = getNormalBuffer();
        float xNorm = vec.x / length;
        normBuf.put(xNorm);
        float yNorm = vec.y / length;
        normBuf.put(yNorm);
        float zNorm = vec.z / length;
        normBuf.put(zNorm);

        FloatBuffer texBuf = getTextureCoords(0).coords;
        texBuf.put((FastMath.atan2(yNorm, xNorm) / (2 * FastMath.PI) + 1) % 1);
        texBuf.put(zNorm / 2 + 0.5f);
    }

    private void updateGeometry(int maxLevels, boolean icosahedron) {
        this.numLevels = maxLevels;
        this.usingIcosahedron = icosahedron;
        int initialTriangleCount = icosahedron ? 20 : 8;
        int initialVertexCount = icosahedron ? 12 : 6;
        // number of triangles = initialTriangleCount * 4^(maxlevels-1)
        int triangleQuantity = initialTriangleCount << ((numLevels - 1) * 2);
        setTriangleQuantity(triangleQuantity);
        // number of vertBuf = (initialVertexCount + initialTriangleCount*4 +
        // initialTriangleCount*4*4 + ...)
        // = initialTriangleCount*(((4^maxlevels)-1)/(4-1)-1) +
        // initialVertexCount
        int vertQuantity = initialTriangleCount
                * (((1 << (numLevels * 2)) - 1) / (4 - 1) - 1)
                + initialVertexCount;
        setVertexCount(vertQuantity);

        FloatBuffer vertBuf = getVertexBuffer();
        setVertexBuffer(vertBuf = BufferUtils.createVector3Buffer(vertBuf,
                vertQuantity));
        setNormalBuffer(BufferUtils.createVector3Buffer(getNormalBuffer(),
                vertQuantity));
        TexCoords textureCoords = getTextureCoords(0);
		setTextureCoords(new TexCoords(BufferUtils.createVector3Buffer(textureCoords != null ? textureCoords.coords : null,
                vertQuantity)), 0);

        int pos = 0;

        Triangle[] old;
        if (icosahedron) {
            int[] indices = new int[] {
                    0, 1, 2, 0, 2, 3, 0, 3, 4, 0, 4, 5, 0, 5, 1, 1, 10, 6, 2, 6,
                    7, 3, 7, 8, 4, 8, 9, 5, 9, 10, 6, 2, 1, 7, 3, 2, 8, 4, 3, 9,
                    5, 4, 10, 1, 5, 11, 7, 6, 11, 8, 7, 11, 9, 8, 11, 10, 9, 11,
                    6, 10
            };
            float y = 0.4472f;
            float a = 0.8944f;
            float b = 0.2764f;
            float c = 0.7236f;
            float d = 0.8507f;
            float e = 0.5257f;
            put(new Vector3f(0, 1, 0));
            put(new Vector3f(a, y, 0));
            put(new Vector3f(b, y, -d));
            put(new Vector3f(-c, y, -e));
            put(new Vector3f(-c, y, e));
            put(new Vector3f(b, y, d));
            put(new Vector3f(c, -y, -e));
            put(new Vector3f(-b, -y, -d));
            put(new Vector3f(-a, -y, 0));
            put(new Vector3f(-b, -y, d));
            put(new Vector3f(c, -y, e));
            put(new Vector3f(0, -1, 0));
            pos += 12;
            
            Triangle[] ikosaedron = new Triangle[indices.length / 3];
            for (int i = 0; i < ikosaedron.length; i++) {
                Triangle triangle = ikosaedron[i] = new Triangle();
                triangle.pt[0] = indices[i * 3];
                triangle.pt[1] = indices[i * 3 + 1];
                triangle.pt[2] = indices[i * 3 + 2];
            }

            old = ikosaedron;
        } else {
            /* Six equidistant points lying on the unit sphere */
            final Vector3f XPLUS = new Vector3f(1, 0, 0); /* X */
            final Vector3f XMIN = new Vector3f(-1, 0, 0); /* -X */
            final Vector3f YPLUS = new Vector3f(0, 1, 0); /* Y */
            final Vector3f YMIN = new Vector3f(0, -1, 0); /* -Y */
            final Vector3f ZPLUS = new Vector3f(0, 0, 1); /* Z */
            final Vector3f ZMIN = new Vector3f(0, 0, -1); /* -Z */

            int xplus = pos++;
            put(XPLUS);
            int xmin = pos++;
            put(XMIN);
            int yplus = pos++;
            put(YPLUS);
            int ymin = pos++;
            put(YMIN);
            int zplus = pos++;
            put(ZPLUS);
            int zmin = pos++;
            put(ZMIN);

            Triangle[] octahedron = new Triangle[] {
                    new Triangle(yplus, zplus, xplus),
                    new Triangle(xmin, zplus, yplus),
                    new Triangle(ymin, zplus, xmin),
                    new Triangle(xplus, zplus, ymin),
                    new Triangle(zmin, yplus, xplus),
                    new Triangle(zmin, xmin, yplus),
                    new Triangle(zmin, ymin, xmin),
                    new Triangle(zmin, xplus, ymin) };

            old = octahedron;
        }

        Vector3f pt0 = new Vector3f();
        Vector3f pt1 = new Vector3f();
        Vector3f pt2 = new Vector3f();

        /* Subdivide each starting triangle (maxlevels - 1) times */
        for (int level = 1; level < numLevels; level++) {
            /* Allocate a next triangle[] */
            Triangle[] next = new Triangle[old.length * 4];
            for (int i = 0; i < next.length; i++) {
                next[i] = new Triangle();
            }

            /*
             * Subdivide each polygon in the old approximation and normalize the
             * next points thus generated to lie on the surface of the unit
             * sphere. Each input triangle with vertBuf labelled [0,1,2] as
             * shown below will be turned into four next triangles:
             * 
             * Make next points
             *   a = (0+2)/2
             *   b = (0+1)/2
             *   c = (1+2)/2
             *   
             * 1   /\   Normalize a, b, c
             *    /  \
             * b /____\ c
             * 
             * Construct next triangles
             * 
             *    /\    /\   [0,b,a] 
             *   /  \  /  \  [b,1,c]
             *  /____\/____\ [a,b,c]
             *  0 a 2 [a,c,2]
             */
            for (int i = 0; i < old.length; i++) {
                int newi = i * 4;
                Triangle oldt = old[i], newt = next[newi];

                BufferUtils.populateFromBuffer(pt0, vertBuf, oldt.pt[0]);
                BufferUtils.populateFromBuffer(pt1, vertBuf, oldt.pt[1]);
                BufferUtils.populateFromBuffer(pt2, vertBuf, oldt.pt[2]);
                Vector3f av = createMidpoint(pt0, pt2).normalizeLocal();
                Vector3f bv = createMidpoint(pt0, pt1).normalizeLocal();
                Vector3f cv = createMidpoint(pt1, pt2).normalizeLocal();
                int a = pos++;
                put(av);
                int b = pos++;
                put(bv);
                int c = pos++;
                put(cv);

                newt.pt[0] = oldt.pt[0];
                newt.pt[1] = b;
                newt.pt[2] = a;
                newt = next[++newi];

                newt.pt[0] = b;
                newt.pt[1] = oldt.pt[1];
                newt.pt[2] = c;
                newt = next[++newi];

                newt.pt[0] = a;
                newt.pt[1] = b;
                newt.pt[2] = c;
                newt = next[++newi];

                newt.pt[0] = a;
                newt.pt[1] = c;
                newt.pt[2] = oldt.pt[2];
            }

            /* Continue subdividing next triangles */
            old = next;
        }

        IntBuffer indexBuffer = BufferUtils
                .createIntBuffer(triangleQuantity * 3);
        setIndexBuffer(indexBuffer);

        for (Triangle triangle : old) {
            for (int aPt : triangle.pt) {
                indexBuffer.put(aPt);
            }
        }
    }

    static class Triangle {
        int[] pt = new int[3]; /* Vertices of triangle */

        public Triangle() {}

        public Triangle(int pt0, int pt1, int pt2) {
            pt[0] = pt0;
            pt[1] = pt1;
            pt[2] = pt2;
        }
    }

}
