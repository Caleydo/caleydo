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
 * A joint that allows movement along two separate axis, with the calculations
 * implemented as the joints connected in series. There is also a suspension
 * setting allowing the use of this joint in places like a wheel of a car.
 * <p/>
 * <p/>
 * A Hinge-2 joint has two axes located around a common anchor point. Axis 1 is
 * specified relative to the first body and axis 2 is specified relative to the
 * second body. Axis 1 can have limits and a motor, axis 2 can only have a
 * motor.
 * <p/>
 * Created 20.12.2003 (dd.mm.yyyy)
 *
 * @author Jani Laakso E-mail: jani.laakso@itmill.com
 *         see http://odejava.dev.java.net
 */
public class JointHinge2 extends Joint {
    /**
     * Error message when the minimum stop angle is out of range
     */
    private static final String MIN_ANGLE_RANGE_MSG =
            "The minimum range angle stop must lie between +/- PI radians: ";

    /**
     * Error message when the maximum stop angle is out of range
     */
    private static final String MAX_ANGLE_RANGE_MSG =
            "The maximum range angle stop must lie between +/- PI radians: ";

    /**
     * Error message when the torque has a negative value
     */
    private static final String NEG_TORQUE_MSG =
            "The torque value supplied is negative: ";

    /**
     * Floating point delta below which we ignore differences
     */
    private static final float ZERO_EPS = 0.000001f;

    /**
     * Create a new Hinge2 joint that belongs to the given world and does not
     * belong to any group. The name is set to the null string.
     *
     * @param world The world that this belongs to
     */
    public JointHinge2( World world ) {
        this( null, world, null );
    }

    /**
     * Create a new Hinge2 joint that belongs to the given world and has a
     * name. The name parameter is optional. The joint is attached to the
     * global environment.
     *
     * @param name  A label string to associate with this joint
     * @param world The world that this belongs to
     */
    public JointHinge2( String name, World world ) {
        this( name, world, null );
    }

    /**
     * Create a new Hinge-2 joint that belongs to the given world. The
     * JointGroup is optional and the name is set to the null string. If no
     * group is provided, the joint is attached to the global environment.
     *
     * @param world      The world that this belongs to
     * @param jointGroup An optional group to associate this joint with
     */
    public JointHinge2( World world, JointGroup jointGroup ) {
        this( null, world, jointGroup );
    }

    /**
     * Create a new Hinge-2 joint that belongs to the given world. The
     * JointGroup and name parameters are optional. If no group is provided,
     * the joint is attached to the global environment.
     *
     * @param name       A label string to associate with this joint
     * @param world      The world that this belongs to
     * @param jointGroup An optional group to associate this joint with
     */
    public JointHinge2( String name, World world, JointGroup jointGroup ) {
        super( name, jointGroup );
        jointId = Ode.dJointCreateHinge2( world.getId(), jointGroupId );
    }

    /**
     * Set the anchor location to a new value, using individual values.
     *
     * @param x The x component of the the anchor location
     * @param y The y component of the the anchor location
     * @param z The z component of the the anchor location
     */
    public void setAnchor( float x, float y, float z ) {
        Ode.dJointSetHinge2Anchor( jointId, x, y, z );
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
        Ode.dJointGetHinge2Anchor( jointId, arr );
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
        Ode.dJointGetHinge2Anchor( jointId, arr );
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
        Ode.dJointGetHinge2Anchor2( jointId, arr );
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
        Ode.dJointGetHinge2Anchor2( jointId, arr );
        result[0] = Ode.floatArray_getitem( arr, 0 );
        result[1] = Ode.floatArray_getitem( arr, 1 );
        result[2] = Ode.floatArray_getitem( arr, 2 );
        Ode.delete_floatArray( arr );
    }

    public void setAxis1( float x, float y, float z ) {
        Ode.dJointSetHinge2Axis1( jointId, x, y, z );
    }

    /**
     * Set the axis 1 vector to a new value, using a vector.
     *
     * @param vector A vector holding the anchor location
     *
    public void setAxis1(Vector3f vector) {
    setAxis1(vector.x, vector.y, vector.z);
    }*/

    /**
     * Get the axis 1 direction vector. A new Vector3f
     * instance will be created for each request. This is identical to calling
     * <code>getAxis1(null)</code>.
     *
     * @return A new vector object containing the location
     */
    public Vector3f getAxis1() {
        return getAxis1( (Vector3f) null );
    }

    /**
     * Get the axis 1 direction vector, and place it
     * in the user-provided data structure. If the user-provided data structure
     * is null, then a new instance is created and returned, otherwise the user
     * provided structure is used as the return value.
     *
     * @param result An object to place the values into or null
     * @return Either the result parameter or a new object
     */
    public Vector3f getAxis1( Vector3f result ) {
        if ( result == null ) {
            result = new Vector3f();
        }

        SWIGTYPE_p_float arr = Ode.new_floatArray( 3 );
        Ode.dJointGetHinge2Axis1( jointId, arr );
        result.x = Ode.floatArray_getitem( arr, 0 );
        result.y = Ode.floatArray_getitem( arr, 1 );
        result.z = Ode.floatArray_getitem( arr, 2 );
        Ode.delete_floatArray( arr );

        return result;
    }

    /**
     * Get the axis 1 direction vector and place it in the user-provided array.
     *
     * @param result An object to place the values into
     */
    public void getAxis1( float[] result ) {
        SWIGTYPE_p_float arr = Ode.new_floatArray( 3 );
        Ode.dJointGetHinge2Axis1( jointId, arr );
        result[0] = Ode.floatArray_getitem( arr, 0 );
        result[1] = Ode.floatArray_getitem( arr, 1 );
        result[2] = Ode.floatArray_getitem( arr, 2 );
        Ode.delete_floatArray( arr );
    }


    public void setAxis2( float x, float y, float z ) {
        Ode.dJointSetHinge2Axis2( jointId, x, y, z );
    }

    /**
     * Set the axis 2 vector to a new value, using a vector.
     *
     * @param vector A vector holding the anchor location
     *
    public void setAxis2(Vector3f vector) {
    setAxis2(vector.x, vector.y, vector.z);
    }*/

    /**
     * Get the axis 2 direction vector. A new Vector3f
     * instance will be created for each request. This is identical to calling
     * <code>getAxis2(null)</code>.
     *
     * @return A new vector object containing the location
     */
    public Vector3f getAxis2() {
        return getAxis2( (Vector3f) null );
    }

    /**
     * Get the axis 2 direction vector, and place it
     * in the user-provided data structure. If the user-provided data structure
     * is null, then a new instance is created and returned, otherwise the user
     * provided structure is used as the return value.
     *
     * @param result An object to place the values into or null
     * @return Either the result parameter or a new object
     */
    public Vector3f getAxis2( Vector3f result ) {
        if ( result == null ) {
            result = new Vector3f();
        }

        SWIGTYPE_p_float arr = Ode.new_floatArray( 3 );
        Ode.dJointGetHinge2Axis2( jointId, arr );
        result.x = Ode.floatArray_getitem( arr, 0 );
        result.y = Ode.floatArray_getitem( arr, 1 );
        result.z = Ode.floatArray_getitem( arr, 2 );
        Ode.delete_floatArray( arr );

        return result;
    }

    /**
     * Get the axis 2 direction vector and place it in the user-provided array.
     *
     * @param result An object to place the values into
     */
    public void getAxis2( float[] result ) {
        SWIGTYPE_p_float arr = Ode.new_floatArray( 3 );
        Ode.dJointGetHinge2Axis2( jointId, arr );
        result[0] = Ode.floatArray_getitem( arr, 0 );
        result[1] = Ode.floatArray_getitem( arr, 1 );
        result[2] = Ode.floatArray_getitem( arr, 2 );
        Ode.delete_floatArray( arr );
    }


    public float getAngle() {
        return Ode.dJointGetHinge2Angle1( jointId );
    }

    public float getAngleRate() {
        return Ode.dJointGetHinge2Angle1Rate( jointId );
    }

    public float getAngle2() {
        throw new UnsupportedOperationException( "position of the second angle cannot be read by this implementation" );
    }

    public float getAngle2Rate() {
        return Ode.dJointGetHinge2Angle2Rate( jointId );
    }

    /**
     * Set the angular speed that you would like axis 1 to achieve. This is
     * not an instantaneous change, it is just a request that the system attempt
     * to achieve this. Typically the torque of the motor on this axis will be
     * used, but other factors, such as external forces will come into play.
     *
     * @param speed The speed that should be achieved
     */
    public void setDesiredAngularVelocity1( float speed ) {
        Ode.dJointSetHinge2Param( jointId, OdeConstants.dParamVel, speed );
    }

    /**
     * Get the requested angular speed currently set axis 1. This requests the
     * desired speed, not the actual speed currently calculated by the physics
     * model.
     *
     * @return The current speed requested
     */
    public float getDesiredAngularVelocity() {
        return Ode.dJointGetHinge2Param( jointId, OdeConstants.dParamVel );
    }

    /**
     * Set the angular speed that you would like axis 2 to achieve. This is
     * not an instantaneous change, it is just a request that the system attempt
     * to achieve this. Typically the torque of the motor on this axis will be
     * used, but other factors, such as external forces will come into play.
     *
     * @param speed The speed that should be achieved
     */
    public void setDesiredAngularVelocity2( float speed ) {
        Ode.dJointSetHinge2Param( jointId, OdeConstants.dParamVel2, speed );
    }

    /**
     * Get the requested angular speed currently set axis 2. This requests the
     * desired speed, not the actual speed currently calculated by the physics
     * model.
     *
     * @return The current speed requested
     */
    public float getDesiredAngularVelocity2() {
        return Ode.dJointGetHinge2Param( jointId, OdeConstants.dParamVel2 );
    }

    /**
     * Set the amount of torque on axis 1 that can be applied by the motor, in
     * order to reach the desired angular velocity. Value must always be
     * greater than zero for it to have an effect. A value of zero disables this
     * motor. Torque is defined in Newton-metres.
     *
     * @param torque The amount of torque to use in Nm
     * @throws IllegalArgumentException The torque value is negative
     */
    public void setMaxTorque1( float torque )
            throws IllegalArgumentException {

        if ( torque < 0 ) {
            throw new IllegalArgumentException( NEG_TORQUE_MSG + torque );
        }

        Ode.dJointSetHinge2Param( jointId, OdeConstants.dParamFMax, torque );
    }

    /**
     * Get the amount of the torque currently set for the motor on axis 1.
     *
     * @return The current torque in Nm
     */
    public float getMaxTorque1() {
        return Ode.dJointGetHinge2Param( jointId, OdeConstants.dParamFMax );
    }

    /**
     * Set the amount of torque on axis 2 that can be applied by the motor, in
     * order to reach the desired angular velocity. Value must always be
     * greater than zero for it to have an effect. A value of zero disables this
     * motor. Torque is defined in Newton-metres.
     *
     * @param torque The amount of torque to use in Nm
     * @throws IllegalArgumentException The torque value is negative
     */
    public void setMaxTorque2( float torque )
            throws IllegalArgumentException {

        if ( torque < 0 ) {
            throw new IllegalArgumentException( NEG_TORQUE_MSG + torque );
        }

        Ode.dJointSetHinge2Param( jointId, OdeConstants.dParamFMax2, torque );
    }

    /**
     * Get the amount of the torque currently set for the motor on axis 2.
     *
     * @return The current torque in Nm
     */
    public float getMaxTorque2() {
        return Ode.dJointGetHinge2Param( jointId, OdeConstants.dParamFMax2 );
    }

    /**
     * Set the amount of constant force to mix into the system on axis 1 when
     * the bodies are not at a stop. This value has no effect when the bodies
     * are at one of the two stops for this axis.
     *
     * @param force The amount of force to use
     */
    public void setConstantForceMix( float force ) {
        Ode.dJointSetHinge2Param( jointId, OdeConstants.dParamCFM, force );
    }

    /**
     * Get the amount of the constant force mix parameter currently set for
     * axis 1 positions between the two stops.
     *
     * @return The current constant force mix
     */
    public float getConstantForceMix() {
        return Ode.dJointGetHinge2Param( jointId, OdeConstants.dParamCFM );
    }

    /**
     * Set the amount of constant force to mix into the system on axis 2 when
     * the bodies are not at a stop. This value has no effect when the bodies
     * are at one of the two stops for this axis.
     *
     * @param force The amount of force to use
     */
    public void setConstantForceMix2( float force ) {
        Ode.dJointSetHinge2Param( jointId, OdeConstants.dParamCFM2, force );
    }

    /**
     * Get the amount of the constant force mix parameter currently set for
     * axis 2 positions between the two stops.
     *
     * @return The current constant force mix
     */
    public float getConstantForceMix2() {
        return Ode.dJointGetHinge2Param( jointId, OdeConstants.dParamCFM2 );
    }

    /**
     * Set the amount of suspension error reduction. This value should be
     * between 0 and 1. 0 is no reduction at all, 1 is full correction in a
     * single step. Suspension values only apply to axis 1.
     *
     * @param erp The amount of error reduction to use
     */
    public void setSuspensionERP( float erp ) {
        Ode.dJointSetHinge2Param( jointId,
                OdeConstants.dParamSuspensionERP,
                erp );
    }

    /**
     * Get the amount of the suspension error reduction parameter currently
     * set. This value will be between 0 and 1. 0 is no correction at all, 1 is
     * full correction in a single step.Suspension values only apply to axis 1.
     *
     * @return A value between 0 and 1
     */
    public float getSuspensionERP() {
        return Ode.dJointGetHinge2Param( jointId,
                OdeConstants.dParamSuspensionERP );
    }

    /**
     * Set the amount of suspension constant force to mix into the system when the
     * bodies are travelling between the stops on axis 1. This value has no effect
     * when the bodies are at stops. Suspension values only apply to axis 1.
     *
     * @param force The amount of force to use
     */
    public void setSuspensionCFM( float force ) {
        Ode.dJointSetHinge2Param( jointId,
                OdeConstants.dParamSuspensionCFM,
                force );
    }

    /**
     * Get the amount of the suspension constant force mix parameter currently set.
     * Suspension values only apply to axis 1.
     *
     * @return The current constant force mix at the stops
     */
    public float getSuspensionCFM() {
        return Ode.dJointGetHinge2Param( jointId,
                OdeConstants.dParamSuspensionCFM );
    }

    /**
     * Set the minimum angle that this joint is permitted to rotate to around
     * axis 1. Angles are specified relative to the initial position that the
     * joint was created in. The angle value is limited to the range +/- &pi;.
     * If the the provided angle is out of this range, an exception is thrown.
     * <p/>
     * Note that if the maximum angle provided is less than the minimum angle
     * at the point of evaluation, ODE ignores all limits.
     * <p/>
     * A value of Float.NEGATIVE_INFINITY can be used to disable the minimum
     * stop.
     *
     * @param angle The minimum stop angle in radians [-&pi;,+&pi;] or
     *              Float.NEGATIVE_INFINITY
     * @throws IllegalArgumentException The provided angle is out of the valid
     *                                  range
     */
    public void setMinAngleStop( float angle )
            throws IllegalArgumentException {

        if ( !Float.isInfinite( angle ) && ( ( Math.PI - Math.abs( angle ) ) < -ZERO_EPS ) ) {
            throw new IllegalArgumentException( MIN_ANGLE_RANGE_MSG + angle );
        }

        Ode.dJointSetHinge2Param( jointId, OdeConstants.dParamLoStop, angle );
    }

    /**
     * Fetch the currently set maximum angle stop for axis 1from this joint.
     *
     * @return A angle in radians in the range [-&pi;,+&pi;] or
     *         Float.NEGATIVE_INFINITY
     */
    public float getMinAngleStop() {
        return Ode.dJointGetHinge2Param( jointId, OdeConstants.dParamLoStop );
    }

    /**
     * Set the maximum angle that this joint is permitted to rotate to around
     * axis 1. Angles are specified relative to the initial position that the
     * joint was created in. The angle value is limited to the range +/- &pi;.
     * If the the provided angle is out of this range, an exception is thrown.
     * <p/>
     * Note that if the maximum angle provided is less than the minimum angle
     * at the point of evaluation, ODE ignores all limits.
     * <p/>
     * A value of Float.POSITIVE_INFINITY can be used to disable the maximum stop.
     *
     * @param angle The maximum stop angle in radians [-&pi;,+&pi;] or
     *              Float.POSITIVE_INFINITY
     * @throws IllegalArgumentException The provided angle is out of the valid
     *                                  range
     */
    public void setMaxAngleStop( float angle )
            throws IllegalArgumentException {

        if ( !Float.isInfinite( angle ) && ( ( Math.PI - Math.abs( angle ) ) < -ZERO_EPS ) ) {
            throw new IllegalArgumentException( MAX_ANGLE_RANGE_MSG + angle );
        }

        Ode.dJointSetHinge2Param( jointId, OdeConstants.dParamHiStop, angle );
    }

    /**
     * Fetch the currently set maximum angle stop for axis 1 from this joint.
     *
     * @return A angle in radians in the range [-&pi;,+&pi;] or
     *         Float.POSITIVE_INFINITY
     */
    public float getMaxAngleStop() {
        return Ode.dJointGetHinge2Param( jointId, OdeConstants.dParamHiStop );
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

        Ode.dJointSetHinge2Param( jointId, OdeConstants.dParamBounce, bounce );
    }

    /**
     * Fetch the current bounce factor for the stop on axis 1.
     *
     * @return The bounce factor as a value in the range [0,1]
     */
    public float getStopBounce() {
        return Ode.dJointGetHinge2Param( jointId, OdeConstants.dParamBounce );
    }

    /**
     * Set the amount of stop bounce error reduction. This value should be
     * between 0 and 1. 0 is no reduction at all, 1 is full correction in a
     * single step.
     *
     * @param erp The amount of error reduction to use
     */
    public void setStopERP( float erp ) {
        Ode.dJointSetHinge2Param( jointId, OdeConstants.dParamStopERP, erp );
    }

    /**
     * Get the amount of the stop error reduction parameter currently set. This
     * value will be between 0 and 1. 0 is no bounce at all, 1 is full bounce.
     *
     * @return A value between 0 and 1
     */
    public float getStopERP() {
        return Ode.dJointGetHinge2Param( jointId, OdeConstants.dParamStopERP );
    }

    /**
     * Set the amount of stop constant force to mix into the system when the
     * bodies reach a stop. This value has no effect when the bodies are not at
     * the stops. Together with the ERP value, this can be used to get spongy
     * or soft stops. Note that this is inteded for unpowered joints, it does
     * not work as expected on powered joints.
     *
     * @param force The amount of force to use
     */
    public void setStopCFM( float force ) {
        Ode.dJointSetHinge2Param( jointId, OdeConstants.dParamStopCFM, force );
    }

    /**
     * Get the amount of the stop constant force mix parameter currently set.
     *
     * @return The current constant force mix at the stops
     */
    public float getStopCFM() {
        return Ode.dJointGetHinge2Param( jointId, OdeConstants.dParamStopCFM );
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
        Ode.dJointSetHinge2Param( jointId, parameter, value );
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
        return Ode.dJointGetHinge2Param( jointId, parameter );
    }
}
