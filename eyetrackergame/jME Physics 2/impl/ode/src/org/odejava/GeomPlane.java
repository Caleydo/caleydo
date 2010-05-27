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
 * from this software withou t specific prior written permission.
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
 * The plane equation is x+b*y+c*z = d. The plane's normal vector is (a,b,c),
 * and it must have length 1. Planes are non-placeable geoms. This means that,
 * unlike placeable geoms, planes do not have an assigned position and
 * rotation. This means that the parameters (a,b,c,d) are always in global
 * coordinates. In other words it is assumed that the plane is always part of
 * the static environment and not tied to any movable object.
 * <p/>
 * Created 16.12.2003 (dd.mm.yyyy)
 *
 * @author Jani Laakso E-mail: jani.laakso@itmill.com
 *         see http://odejava.dev.java.net
 */
public class GeomPlane extends Geom {

    /**
     * Create plane geometry to specific space.
     *
     * @param name
     * @param space
     * @param sizeX
     * @param sizeY
     * @param sizeZ
     * @param graphics graphical representation
     */
    public GeomPlane( String name, float a, float b, float c, float d ) {
        super( name );

        spaceId = Ode.getPARENTSPACEID_ZERO();
        geomId = Ode.dCreatePlane( spaceId, a, b, c, d );

        //updateReferences();
        retrieveNativeAddr();
    }

    public float[] getLengths() {
        SWIGTYPE_p_float tmpArray = Ode.new_floatArray( 4 );
        Ode.dGeomPlaneGetParams( geomId, tmpArray );
        float[] result =
                {
                        Ode.floatArray_getitem( tmpArray, 0 ),
                        Ode.floatArray_getitem( tmpArray, 1 ),
                        Ode.floatArray_getitem( tmpArray, 2 ),
                        Ode.floatArray_getitem( tmpArray, 3 )};
        return result;
    }


}
