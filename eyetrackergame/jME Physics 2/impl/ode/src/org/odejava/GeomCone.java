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
 * Terrain is an contrib module. Contributed by Benoit Chaperot,
 * www.jstarlab.com
 * This module is currently disabled.
 * <p/>
 * Created 16.12.2003 (dd.mm.yyyy)
 *
 * @author Jani Laakso E-mail: jani.laakso@itmill.com
 *         see http://odejava.dev.java.net
 */
public class GeomCone extends PlaceableGeom {

    /**
     * Create cone geometry to specific space.
     *
     * @param name
     * @param radius
     * @param length
     */
    public GeomCone( String name, float radius, float length ) {
        super( name );
        spaceId = Ode.getPARENTSPACEID_ZERO();
        //  geomId = Ode.dCreateCone(spaceId, radius, length);

//        retrieveNativeAddr();
        throw new UnsupportedOperationException( "cone not implemented" );
    }

    /**
     * Get cone radius.
     *
     * @return radius
     */
/*  public float getRadius() {
        Ode.dGeomConeGetParams(geomId, tmpFloatArray, tmpFloatArray2);
        return Ode.floatArray_getitem(tmpFloatArray, 0);
    }
*/
    /**
     * Get cone length.
     *
     * @return length
     */
    /*public float getLength() {
        Ode.dGeomConeGetParams(geomId, tmpFloatArray, tmpFloatArray2);
        return Ode.floatArray_getitem(tmpFloatArray2, 0);
    }
    */

}
