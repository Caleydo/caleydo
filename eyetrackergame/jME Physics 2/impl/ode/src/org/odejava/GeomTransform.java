/*
 * Copyright (c) 2004, William Denniss. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above
 *   copyright notice, this list of conditions and the following
 *   disclaimer in the documentation and/or other materials provided
 *   with the distribution.
 *
 * Neither the name of William Denniss nor the names of
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) A
 * RISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE
 *
 */
package org.odejava;

import org.odejava.ode.Ode;

/**
 * <p>The GeomTransform is a geom which encapsulates a Geom, allowing the encapsulated Geom to be transformed
 * relitive to it.  The GeomTransform can then be added to a Body.</p>
 * <p/>
 * <p>Please refer to the <a href="http://opende.sourceforge.net/ode-latest-userguide.html#sec_10_7_7">ODE Documentation</a>
 * for more information.  An example in ODE code exists <a href="http://q12.org/pipermail/ode/2002-July/005462.html"> here</a></p>
 * <p/>
 * <p>It is a non-placable geometry, therefor the transform setters
 * must not be called.  However the transform getters may be called.  As it is
 * non-placable, the getters don't simply delegate the call to ODE.  Rather they
 * return the world transform of the encapsulated Geom (that is, the transform
 * of the parent body multiplied by that of the encapsulated Geom).  Subsiquently,
 * you can bind GeomTransform objects to Display objects.</p>
 *
 * @author William Denniss
 */
public class GeomTransform extends PlaceableGeom {

    private PlaceableGeom encapsulatedGeom;

    /**
     * Greats a GeomTransform with the given name
     *
     * @param name     the name of this Geom
     * @param graphics graphical representation
     * @ode dCreateGeomTransform creates the object
     */
    public GeomTransform( String name ) {
        super( name );

        spaceId = Ode.getPARENTSPACEID_ZERO();
        geomId = Ode.dCreateGeomTransform( spaceId );

        retrieveNativeAddr();

    }

    /**
     * Sets the encapsulated Geom.
     *
     * @param obj the geom this GeomTransform encapsulates.
     *            This Geom should not be added to any Space or associated
     *            with any Body.
     * @ode dGeomTransformSetGeom sets the encapsulated geom
     */
    public void setEncapsulatedGeom( PlaceableGeom obj ) {
        if ( encapsulatedGeom != null ) {
            throw new IllegalOdejavaOperation( "Attempt to assign a second Encapsulated geom.  GeomTransform can only have one encapsualted Geom." );
        }

        encapsulatedGeom = obj;
        Ode.dGeomTransformSetGeom( geomId, obj.getId() );
        encapsulatedGeom.isEncapsulated = true;
    }

    /**
     * Removes the encapsulate Geom.
     *
     * @dGeomTransformSetGeom
     */
    public void removeEncapsulatedGeom() {
        Ode.dGeomTransformSetGeom( geomId, null );
        encapsulatedGeom.isEncapsulated = false;

        encapsulatedGeom = null;
    }

    /**
     * Returns the encapsulated geometry.
     *
     * @return the encapsulated geometry.
     */
    public PlaceableGeom getEncapsulatedGeom() {
        return encapsulatedGeom;

    }
}
