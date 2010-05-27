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
import org.odejava.ode.OdeConstants;
import org.odejava.ode.SWIGTYPE_p_float;

/**
 * Created 20.12.2003 (dd.mm.yyyy)
 *
 * @author Jani Laakso E-mail: jani.laakso@itmill.com
 *         see http://odejava.dev.java.net
 */
public class JointUniversal extends Joint {

    /**
     * Create a new Universal joint that belongs to the given world and does not
     * belong to any group. The name is set to the null string.
     *
     * @param world The world that this belongs to
     */
    public JointUniversal( World world ) {
        this( null, world, null );
    }

    /**
     * Create a new Universal joint that belongs to the given world and has a
     * name. The name parameter is optional. The joint is attached to the
     * global environment.
     *
     * @param name  A label string to associate with this joint
     * @param world The world that this belongs to
     */
    public JointUniversal( String name, World world ) {
        this( name, world, null );
    }

    /**
     * Create a new universal joint that belongs to the given world. The
     * JointGroup is optional and the name is set to the null string. If no
     * group is provided, the joint is attached to the global environment.
     *
     * @param world      The world that this belongs to
     * @param jointGroup An optional group to associate this joint with
     */
    public JointUniversal( World world, JointGroup jointGroup ) {
        this( null, world, jointGroup );
    }

    /**
     * Create a new Universal joint that belongs to the given world. The
     * JointGroup and name parameters are optional. If no group is provided,
     * the joint is attached to the global environment.
     *
     * @param name       A label string to associate with this joint
     * @param world      The world that this belongs to
     * @param jointGroup An optional group to associate this joint with
     */
    public JointUniversal( String name, World world, JointGroup jointGroup ) {
        super( name, jointGroup );
        jointId = Ode.dJointCreateUniversal( world.getId(), jointGroupId );
    }

    public void setAnchor( float x, float y, float z ) {
        Ode.dJointSetUniversalAnchor( jointId, x, y, z );
    }

    public Vector3f getAnchor() {
        SWIGTYPE_p_float arr = Ode.new_floatArray( 3 );
        Ode.dJointGetUniversalAnchor( jointId, arr );
        Vector3f ret = new Vector3f( Ode.floatArray_getitem( arr, 0 ),
                Ode.floatArray_getitem( arr, 1 ),
                Ode.floatArray_getitem( arr, 2 ) );
        Ode.delete_floatArray( arr );
        return ret;
    }

    public Vector3f getAnchor2() {
        SWIGTYPE_p_float arr = Ode.new_floatArray( 3 );
        Ode.dJointGetUniversalAnchor2( jointId, arr );
        Vector3f ret = new Vector3f( Ode.floatArray_getitem( arr, 0 ),
                Ode.floatArray_getitem( arr, 1 ),
                Ode.floatArray_getitem( arr, 2 ) );
        Ode.delete_floatArray( arr );
        return ret;
    }

    @Override
    public void setAxis1( float x, float y, float z ) {
        Ode.dJointSetUniversalAxis1( jointId, x, y, z );
    }

    public Vector3f getAxis1() {
        SWIGTYPE_p_float arr = Ode.new_floatArray( 3 );
        Ode.dJointGetUniversalAxis1( jointId, arr );
        Vector3f ret = new Vector3f( Ode.floatArray_getitem( arr, 0 ),
                Ode.floatArray_getitem( arr, 1 ),
                Ode.floatArray_getitem( arr, 2 ) );
        Ode.delete_floatArray( arr );
        return ret;
    }

    @Override
    public void setAxis2( float x, float y, float z ) {
        Ode.dJointSetUniversalAxis2( jointId, x, y, z );
    }

    public Vector3f getAxis2() {
        SWIGTYPE_p_float arr = Ode.new_floatArray( 3 );
        Ode.dJointGetUniversalAxis2( jointId, arr );
        Vector3f ret = new Vector3f( Ode.floatArray_getitem( arr, 0 ),
                Ode.floatArray_getitem( arr, 1 ),
                Ode.floatArray_getitem( arr, 2 ) );
        Ode.delete_floatArray( arr );
        return ret;
    }

    public float getAngle() {
        return Ode.dJointGetUniversalAngle1( jointId );
    }

    public float getAngleRate() {
        return Ode.dJointGetUniversalAngle1Rate( jointId );
    }

    public float getAngle2() {
        return Ode.dJointGetUniversalAngle2( jointId );
    }

    public float getAngle2Rate() {
        return Ode.dJointGetUniversalAngle2Rate( jointId );
    }

    /**
     * Set the bounciness of the stops for axis 1. This is a value in the range
     * [0,1] defining how hitting the stop will effect the return travel of the
     * two bodies. A value of 0 means there is no bounce and the bodies will
     * not bounce back. A value of 1 means the full contact velocity at the
     * stop will be reflected back in the opposite direction.
     *
     * @param bounce The bounciness factor in the range [0,1]
     * @throws IllegalArgumentException The bounce factor is out of range
     */
    public void setStopBounce( float bounce )
            throws IllegalArgumentException {
        if ( bounce < 0 || bounce > 1 ) {
            throw new IllegalArgumentException( BOUNCE_RANGE_MSG + bounce );
        }

        Ode.dJointSetHingeParam( jointId, OdeConstants.dParamBounce, bounce );
    }

    /**
     * Fetch the current bounce factor for the stop on axis 1.
     *
     * @return The bounce factor as a value in the range [0,1]
     */
    public float getStopBounce() {
        return Ode.dJointGetHingeParam( jointId, OdeConstants.dParamBounce );
    }

    /**
     * Set the bounciness of the stops for axis 2. This is a value in the range
     * [0,1] defining how hitting the stop will effect the return travel of the
     * two bodies. A value of 0 means there is no bounce and the bodies will
     * not bounce back. A value of 1 means the full contact velocity at the
     * stop will be reflected back in the opposite direction.
     *
     * @param bounce The bounciness factor in the range [0,1]
     * @throws IllegalArgumentException The bounce factor is out of range
     */
    public void setStopBounce2( float bounce )
            throws IllegalArgumentException {
        if ( bounce < 0 || bounce > 1 ) {
            throw new IllegalArgumentException( BOUNCE_RANGE_MSG + bounce );
        }

        Ode.dJointSetHingeParam( jointId, OdeConstants.dParamBounce2, bounce );
    }

    /**
     * Fetch the current bounce factor for the stop on axis 2.
     *
     * @return The bounce factor as a value in the range [0,1]
     */
    public float getStopBounce2() {
        return Ode.dJointGetHingeParam( jointId, OdeConstants.dParamBounce2 );
    }

    /**
     * Set a generic, unchecked, parameter directly for this joint. The
     * parameter types that are permissable for the joint are defined by the
     * individual joint classes and the definitions can be found in
     * {@link org.odejava.ode.OdeConstants}.
     *
     * @param parameter A parameter constant from OdeConstants
     * @param value     The new value to associate with the parameter
     */
    @Override
    public void setParam( int parameter, float value ) {
        Ode.dJointSetUniversalParam( jointId, parameter, value );
    }

    /**
     * Get an unchecked parameter value directly from this joint. The
     * parameter types that are permissable for the joint are defined by the
     * individual joint classes and the definitions can be found in
     * {@link org.odejava.ode.OdeConstants}.
     *
     * @param parameter A constant describing the parameter to fetch
     * @return The value of that parameter
     */
    @Override
    public float getParam( int parameter ) {
        return Ode.dJointGetUniversalParam( jointId, parameter );
    }
}
