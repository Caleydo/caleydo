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
 * Created 20.12.2003 (dd.mm.yyyy)
 *
 * @author Jani Laakso E-mail: jani.laakso@itmill.com
 *         see http://odejava.dev.java.net
 */
public class JointBall extends Joint {

    /**
     * Create a new Ball joint that belongs to the given world and does not
     * belong to any group. The name is set to the null string.
     *
     * @param world The world that this belongs to
     */
    public JointBall( World world ) {
        this( null, world, null );
    }

    /**
     * Create a new Ball joint that belongs to the given world and has a
     * name. The name parameter is optional. The joint is attached to the
     * global environment.
     *
     * @param name  A label string to associate with this joint
     * @param world The world that this belongs to
     */
    public JointBall( String name, World world ) {
        this( name, world, null );
    }

    /**
     * Create a new Ball joint that belongs to the given world. The
     * JointGroup is optional and the name is set to the null string. If no
     * group is provided, the joint is attached to the global environment.
     *
     * @param world      The world that this belongs to
     * @param jointGroup An optional group to associate this joint with
     */
    public JointBall( World world, JointGroup jointGroup ) {
        this( null, world, jointGroup );
    }

    /**
     * Create a new Ball joint that belongs to the given world. The
     * JointGroup and name parameters are optional. If no group is provided,
     * the joint is attached to the global environment.
     *
     * @param name       A label string to associate with this joint
     * @param world      The world that this belongs to
     * @param jointGroup An optional group to associate this joint with
     */
    public JointBall( String name, World world, JointGroup jointGroup ) {
        super( name, jointGroup );
        jointId = Ode.dJointCreateBall( world.getId(), jointGroupId );
    }

    public void setParam( int parameter, float value ) {
        Ode.dJointSetBallParam( jointId, parameter, value );
    }

    /**
     * Set the anchor location to a new value, using individual values.
     *
     * @param x The x component of the the anchor location
     * @param y The y component of the the anchor location
     * @param z The z component of the the anchor location
     */
    public void setAnchor( float x, float y, float z ) {
        Ode.dJointSetBallAnchor( jointId, x, y, z );
    }

    /**
     * Set the anchor location, relative to body 1 to a new value, using a vector.
     *
     * @param position A vector holding the anchor location
     */
    public void setAnchor( Vector3f position ) {
        setAnchor( position.x, position.y, position.z );
    }

    /**
     * Get the anchor location of the joint, relative to body 1. A new Vector3f
     * instance will be created for each request. This is identical to calling
     * <code>getAnchor(null)</code>.
     *
     * @return A new vector object containing the location
     */
    public Vector3f getAnchor() {
        return getAnchor( (Vector3f) null );
    }

    /**
     * Get the anchor location of the joint, relative to body 1, and place it
     * in the user-provided data structure. If the user-provided data structure
     * is null, then a new instance is created and returned, otherwise the user
     * provided structure is used as the return value.
     *
     * @param result An object to place the values into or null
     * @return Either the result parameter or a new object
     */
    public Vector3f getAnchor( Vector3f result ) {
        if ( result == null ) {
            result = new Vector3f();
        }

        SWIGTYPE_p_float arr = Ode.new_floatArray( 3 );
        Ode.dJointGetBallAnchor( jointId, arr );
        result.x = Ode.floatArray_getitem( arr, 0 );
        result.y = Ode.floatArray_getitem( arr, 1 );
        result.z = Ode.floatArray_getitem( arr, 2 );
        Ode.delete_floatArray( arr );

        return result;
    }

    /**
     * Get the anchor location the joint, relative to body 1 and place it in the
     * user-provided array.
     *
     * @param result An object to place the values into
     */
    public void getAnchor( float[] result ) {
        SWIGTYPE_p_float arr = Ode.new_floatArray( 3 );
        Ode.dJointGetBallAnchor( jointId, arr );
        result[0] = Ode.floatArray_getitem( arr, 0 );
        result[1] = Ode.floatArray_getitem( arr, 1 );
        result[2] = Ode.floatArray_getitem( arr, 2 );
        Ode.delete_floatArray( arr );
    }


    /**
     * Get the anchor location of the joint, relative to body 2. A new Vector3f
     * instance will be created for each request. This is identical to calling
     * <code>getAnchor2(null)</code>.
     *
     * @return A new vector object containing the location
     */
    public Vector3f getAnchor2() {
        return getAnchor2( (Vector3f) null );
    }

    /**
     * Get the anchor location of the joint, relative to body 2, and place it
     * in the user-provided data structure. If the user-provided data structure
     * is null, then a new instance is created and returned, otherwise the user
     * provided structure is used as the return value.
     *
     * @param result An object to place the values into or null
     * @return Either the result parameter or a new object
     */
    public Vector3f getAnchor2( Vector3f result ) {
        if ( result == null ) {
            result = new Vector3f();
        }

        SWIGTYPE_p_float arr = Ode.new_floatArray( 3 );
        Ode.dJointGetBallAnchor2( jointId, arr );
        result.x = Ode.floatArray_getitem( arr, 0 );
        result.y = Ode.floatArray_getitem( arr, 1 );
        result.z = Ode.floatArray_getitem( arr, 2 );
        Ode.delete_floatArray( arr );

        return result;
    }

    /**
     * Get the anchor location the joint, relative to body 2 and place it in
     * the user-provided array.
     *
     * @param result An object to place the values into
     */
    public void getAnchor2( float[] result ) {
        SWIGTYPE_p_float arr = Ode.new_floatArray( 3 );
        Ode.dJointGetBallAnchor2( jointId, arr );
        result[0] = Ode.floatArray_getitem( arr, 0 );
        result[1] = Ode.floatArray_getitem( arr, 1 );
        result[2] = Ode.floatArray_getitem( arr, 2 );
        Ode.delete_floatArray( arr );
    }
}
