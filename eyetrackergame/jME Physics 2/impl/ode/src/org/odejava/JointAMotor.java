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
public class JointAMotor extends Joint {

    /**
     * Create a new AMotor joint that belongs to the given world and does not
     * belong to any group. The name is set to the null string.
     *
     * @param world The world that this belongs to
     */
    public JointAMotor( World world ) {
        this( null, world, null );
    }

    /**
     * Create a new AMotor joint that belongs to the given world and has a
     * name. The name parameter is optional. The joint is attached to the
     * global environment.
     *
     * @param name  A label string to associate with this joint
     * @param world The world that this belongs to
     */
    public JointAMotor( String name, World world ) {
        this( name, world, null );
    }

    /**
     * Create a new AMotor joint that belongs to the given world. The
     * JointGroup is optional and the name is set to the null string. If no
     * group is provided, the joint is attached to the global environment.
     *
     * @param world      The world that this belongs to
     * @param jointGroup An optional group to associate this joint with
     */
    public JointAMotor( World world, JointGroup jointGroup ) {
        this( null, world, jointGroup );
    }

    /**
     * Create a new AMotor joint that belongs to the given world. The
     * JointGroup and name parameters are optional. If no group is provided,
     * the joint is attached to the global environment.
     *
     * @param name       A label string to associate with this joint
     * @param world      The world that this belongs to
     * @param jointGroup An optional group to associate this joint with
     */
    public JointAMotor( String name, World world, JointGroup jointGroup ) {
        super( name, jointGroup );
        jointId = Ode.dJointCreateAMotor( world.getId(), jointGroupId );
    }

    public void setMode( int mode ) {
        Ode.dJointSetAMotorMode( jointId, mode );
    }

    public int getMode() {
        return Ode.dJointGetAMotorMode( jointId );
    }

    /**
     * Set the number of valid axes to use for this motor. The valid range is
     * between 0 and 3. A value of zero effectively disables the motor.
     *
     * @param num The number of valid axes to use. Must be in the range [0,3].
     */
    public void setNumAxes( int num ) {
        Ode.dJointSetAMotorNumAxes( jointId, num );
    }

    /**
     * Get the number of valid axes currently in use by the motor.
     *
     * @return A value in the range [0,3]
     */
    public int getNumAxes() {
        return Ode.dJointGetAMotorNumAxes( jointId );
    }

    public void setAxis( int anum, int rel, float x, float y, float z ) {
        Ode.dJointSetAMotorAxis( jointId, anum, rel, x, y, z );
    }

    /**
     * Get the axis vector for the requested axis identifier. A new vector
     * instance will be created for each request. This is identical to calling
     * <code>getAxis(anum, null)</code>.
     *
     * @param anum The axis number in the range [1,3]
     * @return A new vector object containing the axis values
     */
    public Vector3f getAxis( int anum ) {
        return getAxis( anum, null );
    }

    /**
     * Get the axis vector for the requested axis identifier and place it in
     * the user-provided data structure. If the user-provided data structure is
     * null, then a new instance is created and returned, otherwise the user
     * provided structure is used as the return value.
     *
     * @param anum The axis number in the range [1,3]
     * @param val  An object to place the values into or null
     * @return Either the val parameter or a new object
     */
    public Vector3f getAxis( int anum, Vector3f val ) {
        Vector3f ret = val;

        if ( ret == null ) {
            ret = new Vector3f();
        }

        SWIGTYPE_p_float arr = Ode.new_floatArray( 3 );
        Ode.dJointGetAMotorAxis( jointId, anum, arr );
        ret.x = Ode.floatArray_getitem( arr, 0 );
        ret.y = Ode.floatArray_getitem( arr, 1 );
        ret.z = Ode.floatArray_getitem( arr, 2 );
        Ode.delete_floatArray( arr );
        return ret;
    }

    public int getAxisRel( int anum ) {
        return Ode.dJointGetAMotorAxisRel( jointId, anum );
    }

    public void setAngle( int anum, float angle ) {
        Ode.dJointSetAMotorAngle( jointId, anum, angle );
    }

    public float getAngle( int anum ) {
        return Ode.dJointGetAMotorAngle( jointId, anum );
    }

    public float getAngleRate( int anum ) {
        return Ode.dJointGetAMotorAngleRate( jointId, anum );
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
        Ode.dJointSetAMotorParam( jointId, parameter, value );
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
        return Ode.dJointGetAMotorParam( jointId, parameter );
    }

    public void setAnchor( float x, float y, float z ) {
        throw new UnsupportedOperationException();
    }

    /**
     * Convenience method, just calls addTorque(x,y,z)
     *
     * @param torque added torque
     */
    public void addTorque( Vector3f torque ) {
        addTorque( torque.getX(), torque.getY(), torque.getZ() );
    }

    public void addTorque( float x, float y, float z ) {
        Ode.dJointAddAMotorTorques( jointId, x, y, z );
    }
}
