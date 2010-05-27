/*
 * Copyright © 2003-2009 jMonkeyEngine
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

package com.jme.math;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Logger;

import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;

/**
 * A plane in 3D space.
 * <p>
 * The plane is defined by a vector, <i>N</i> which is normal to the plane; and
 * a constant, <i>C</i>, representing the distance of the plane from the
 * origin. The plane can be represented by the equation <i>C = Nâp</i> where
 * <i>p</p> is a point on the plane.
 *
 * @author Mark Powell
 * @author Joshua Slack
 * @version $Revision: 4720 $, $Date: 2009-10-09 05:52:07 +0200 (Fr, 09 Okt 2009) $
 */
public class Plane implements Serializable, Savable, Cloneable {

    private static final Logger logger = Logger.getLogger(Plane.class.getName());

    private static final long serialVersionUID = 1L;

    /** Vector normal to the plane. */
    public Vector3f normal;

    /** Constant of the plane. See formula in class definition. */
    public float constant;

    /**
     * Constructor instantiates a new <code>Plane</code> object. This is the
     * default object and contains a normal of (0,0,0) and a constant of 0.
     */
    public Plane() {
        normal = new Vector3f();
    }

    /**
     * Constructor instantiates a new <code>Plane</code> object. The normal
     * and constant values are set at creation.
     * 
     * @param normal
     *            the normal of the plane.
     * @param constant
     *            the constant of the plane.
     */
    public Plane(Vector3f normal, float constant) {
        if (normal == null) {
            logger.warning("Normal was null, created default normal.");
            normal = new Vector3f();
        }
        this.normal = normal;
        this.constant = constant;
    }

    /**
     * <code>setNormal</code> sets the normal of the plane.
     * 
     * @param normal
     *            the new normal of the plane.
     */
    public void setNormal(Vector3f normal) {
        if (normal == null) {
            logger.warning("Normal was null, created default normal.");
            normal = new Vector3f();
        }
        this.normal = normal;
    }

    /**
     * <code>getNormal</code> retrieves the normal of the plane.
     * 
     * @return the normal of the plane.
     */
    public Vector3f getNormal() {
        return normal;
    }

    /**
     * <code>setConstant</code> sets the constant value that helps define the
     * plane.
     * 
     * @param constant
     *            the new constant value.
     */
    public void setConstant(float constant) {
        this.constant = constant;
    }

    /**
     * <code>getConstant</code> returns the constant of the plane.
     * 
     * @return the constant of the plane.
     */
    public float getConstant() {
        return constant;
    }

    /**
     * <code>pseudoDistance</code> calculates the distance from this plane to
     * a provided point. If the point is on the negative side of the plane the
     * distance returned is negative, otherwise it is positive. If the point is
     * on the plane, it is zero.
     * 
     * @param point
     *            the point to check.
     * @return the signed distance from the plane to a point.
     */
    public float pseudoDistance(Vector3f point) {
        return normal.dot(point) - constant;
    }

    /**
     * Determine on which side of this plane the point {@code p} lies.
     * 
     * @param p the point to check.
     * @return the side at which the point lies.
     */
    public Side whichSide(Vector3f p) {
        float dis = pseudoDistance(p);
        if (dis < 0) { return Side.NEGATIVE; }
        if (dis > 0) { return Side.POSITIVE; }
        return Side.NONE;
    }

    /**
     * Initialize this plane using the three points of the given triangle.
     * 
     * @param t
     *            the triangle
     */
    public void setPlanePoints(Triangle t) {
        setPlanePoints(t.get(0), t.get(1), t.get(2));
    }

    /**
     * Initialize the Plane using the given 3 points as coplanar.
     * 
     * @param v1
     *            the first point
     * @param v2
     *            the second point
     * @param v3
     *            the third point
     */
    public void setPlanePoints(Vector3f v1, Vector3f v2, Vector3f v3) {
        normal.set(v2).subtractLocal(v1);
        normal.crossLocal(v3.x - v1.x, v3.y - v1.y, v3.z - v1.z)
                .normalizeLocal();
        constant = normal.dot(v1);
    }

    /**
     * <code>toString</code> returns a string that represents the string
     * representation of this plane. It represents the normal as a
     * <code>Vector3f</code> object, so the format is the following:
     * com.jme.math.Plane [Normal: org.jme.math.Vector3f [X=XX.XXXX, Y=YY.YYYY,
     * Z=ZZ.ZZZZ] - Constant: CC.CCCCC]
     * 
     * @return the string representation of this plane.
     */
    public String toString() {
        return "com.jme.math.Plane [Normal: " + normal + " - Constant: "
                + constant + "]";
    }

    public void write(JMEExporter e) throws IOException {
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(normal, "normal", Vector3f.ZERO);
        capsule.write(constant, "constant", 0);
    }

    public void read(JMEImporter e) throws IOException {
        InputCapsule capsule = e.getCapsule(this);
        normal = (Vector3f) capsule.readSavable("normal", Vector3f.ZERO.clone());
        constant = capsule.readFloat("constant", 0);
    }

    public Class<? extends Plane> getClassTag() {
        return this.getClass();
    }
    
    @Override
    public Plane clone() {
        try {
            Plane p = (Plane) super.clone();
            p.normal = normal.clone();
            return p;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public static enum Side {
        /** A point on the side opposite the normal to the plane. */
        NEGATIVE,
        /** A point on the plane itself. */
        NONE,
        /** A point on the side of the normal to the plane. */
        POSITIVE
    }

}