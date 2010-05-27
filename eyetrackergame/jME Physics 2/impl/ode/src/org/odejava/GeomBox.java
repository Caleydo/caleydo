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

import com.jme.math.Vector3f;
import org.odejava.ode.Ode;
import org.odejava.ode.SWIGTYPE_p_float;

/**
 * Box geometry based on x, y and z sizes.
 * <p/>
 * Created 16.12.2003 (dd.mm.yyyy)
 *
 * @author Jani Laakso E-mail: jani.laakso@itmill.com
 *         see http://odejava.dev.java.net
 */
public class GeomBox extends PlaceableGeom {


    /**
     * Create box geometry to specific space.
     *
     * @param name
     * @param sizeX
     * @param sizeY
     * @param sizeZ
     * @param graphics graphical representation
     */
    public GeomBox( String name, float sizeX, float sizeY, float sizeZ ) {
        super( name );

        spaceId = Ode.getPARENTSPACEID_ZERO();
        geomId = Ode.dCreateBox( spaceId, sizeX, sizeY, sizeZ );

        retrieveNativeAddr();


    }

    public float[] getLengths() {
        SWIGTYPE_p_float tmpArray = Ode.new_floatArray( 4 );
        Ode.dGeomBoxGetLengths( geomId, tmpArray );
        float[] result =
                {
                        Ode.floatArray_getitem( tmpArray, 0 ),
                        Ode.floatArray_getitem( tmpArray, 1 ),
                        Ode.floatArray_getitem( tmpArray, 2 )};
        return result;
    }

    public void setSize( Vector3f size ) {
        Ode.dGeomBoxSetLengths( geomId, size.x, size.y, size.z );
    }
}
