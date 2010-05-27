/*
 * Open Dynamics Engine for Java (odejava) Copyright (c) 2004, Jani Laakso, All
 * rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution. Neither the name of the odejava nor the
 * names of its contributors may be used to endorse or promote products derived
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
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.odejava;

import org.odejava.ode.Ode;

/**
 * Capsule is like a cylinder except it has half-sphere caps at
 * its ends. This feature makes the internal collision detection code
 * particularly fast and accurate. The cylinder's length, not counting the
 * caps, is given by length. The capsule is aligned along the geom's local Z
 * axis. The radius of the caps, and of the cylinder itself, is given by
 * radius.
 * <p/>
 * Created 16.12.2003 (dd.mm.yyyy)
 *
 * @author Jani Laakso E-mail: jani.laakso@itmill.com
 *         see http://odejava.dev.java.net
 */
public class GeomCapsule extends PlaceableGeom {

    private float radius;
    private float length;

    /**
     * Create capsule geometry to specific space.
     *
     * @param name
     * @param radius
     * @param length
     */
    public GeomCapsule( String name, float radius, float length ) {
        super( name );

        this.radius = radius;
        this.length = length;
        spaceId = Ode.getPARENTSPACEID_ZERO();
        geomId = Ode.dCreateCapsule( spaceId, radius, length );
        retrieveNativeAddr();
    }

    /**
     * Get capsule radius.
     *
     * @return radius
     */
    public float getRadius() {
        return radius;
    }

    /**
     * Get cylinder length.
     *
     * @return length
     */
    public float getLength() {
        return length;
    }

    public void setRadius( float radius ) {
        this.radius = radius;
        Ode.dGeomCapsuleSetParams( geomId, radius, length );
    }

    public void setLength( float length ) {
        this.length = length;
        Ode.dGeomCapsuleSetParams( geomId, radius, length );
    }

}
