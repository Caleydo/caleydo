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
import org.odejava.ode.SWIGTYPE_p_float;

/**
 * A ray is different from all the other geom classes in that it does not
 * represent a solid object. It is an infinitely thin line that starts from the
 * geom's position and extends in the direction of the geom's local Z-axis.
 * <p/>
 * Calling dCollide() between a ray and another geom will result in at most one
 * contact point. Rays have their own conventions for the contact information
 * in the dContactGeom structure (thus it is not useful to create contact
 * joints from this information):
 * <p/>
 * pos - This is the point at which the ray intersects the surface of the other
 * geom, regardless of whether the ray starts from inside or outside the geom.
 * <p/>
 * normal - This is the surface normal of the other geom at the contact point.
 * if dCollide() is passed the ray as its first geom then the normal will be
 * oriented correctly for ray reflection from that surface (otherwise it will
 * have the opposite sign).
 * <p/>
 * depth - This is the distance from the start of the ray to the contact point.
 * <p/>
 * Rays are useful for things like visibility testing, detemining the path of
 * projectiles or light rays, and for object placement.
 * <p/>
 * Created 16.12.2003 (dd.mm.yyyy)
 *
 * @author Jani Laakso E-mail: jani.laakso@itmill.com
 *         see http://odejava.dev.java.net
 */
public class GeomRay extends Geom {

    /**
     * Create ray geometry to specific space.
     *
     * @param space
     * @param length
     * @param graphics graphical representation
     */
    public GeomRay( String name, float length ) {
        super( name );
        spaceId = Ode.getPARENTSPACEID_ZERO();
        geomId = Ode.dCreateRay( spaceId, length );


        retrieveNativeAddr();
    }

    /**
     * Set the starting position (px,py,pz) and direction (dx,dy,dz) of the
     * given ray. The ray's rotation matrix will be adjusted so that the local
     * Z-axis is aligned with the direction. Note that this does not adjust the
     * ray's length.
     *
     * @param px
     * @param py
     * @param pz
     * @param dx
     * @param dy
     * @param dz
     */
    public void setStartPosAndDirection(
            float px,
            float py,
            float pz,
            float dx,
            float dy,
            float dz ) {
        Ode.dGeomRaySet( geomId, px, py, pz, dx, dy, dz );
    }

    /**
     * Get the starting position (start) of the ray.
     *
     * @return starting position coordinates
     */
    public float[] getStartingPos() {
        SWIGTYPE_p_float tmpArray = Ode.new_floatArray( 4 );
        Ode.dGeomRayGet( geomId, tmpArray, Ode.new_floatArray( 4 ) );
        float[] result =
                {
                        Ode.floatArray_getitem( tmpArray, 0 ),
                        Ode.floatArray_getitem( tmpArray, 1 ),
                        Ode.floatArray_getitem( tmpArray, 2 )};
        return result;
    }

    /**
     * Get the direction (dir) of the ray. The returned direction will be a
     * unit length vector.
     *
     * @return direction vector
     */
    public float[] getDirection() {
        SWIGTYPE_p_float tmpArray = Ode.new_floatArray( 4 );
        Ode.dGeomRayGet( geomId, Ode.new_floatArray( 4 ), tmpArray );
        float[] result =
                {
                        Ode.floatArray_getitem( tmpArray, 0 ),
                        Ode.floatArray_getitem( tmpArray, 1 ),
                        Ode.floatArray_getitem( tmpArray, 2 )};
        return result;
    }


    public void setLength( float length ) {
        Ode.dGeomRaySetLength( geomId, length );
    }
}
