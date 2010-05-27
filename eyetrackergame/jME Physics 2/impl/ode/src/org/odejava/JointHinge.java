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
 * Represents a joint that is restricted to a single degree of freedom around
 * a hinge axis between two bodies.
 * <p/>
 * <p/>
 * Created 20.12.2003 (dd.mm.yyyy)
 *
 * @author Jani Laakso E-mail: jani.laakso@itmill.com
 *         see http://odejava.dev.java.net
 */
public class JointHinge extends Joint {
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
     * Floating point delta below which we ignore differences
     */
    private static final float ZERO_EPS = 0.000001f;

    /**
     * Create a new Hinge joint that belongs to the given world and does not
     * belong to any group. The name is set to the null string.
     *
     * @param world The world that this belongs to
     */
    public JointHinge( World world ) {
        this( null, world, null );
    }

    /**
     * Create a new Hinge joint that belongs to the given world and has a
     * name. The name parameter is optional. The joint is attached to the
     * global environment.
     *
     * @param name  A label string to associate with this joint
     * @param world The world that this belongs to
     */
    public JointHinge( String name, World world ) {
        this( name, world, null );
    }

    /**
     * Create a new Hinge joint that belongs to the given world. The
     * JointGroup is optional and the name is set to the null string. If no
     * group is provided, the joint is attached to the global environment.
     *
     * @param world      The world that this belongs to
     * @param jointGroup An optional group to associate this joint with
     */
    public JointHinge( World world, JointGroup jointGroup ) {
        this( null, world, jointGroup );
    }

    /**
     * Create a new Hinge joint that belongs to the given world. The
     * JointGroup and name parameters are optional. If no group is provided,
     * the joint is attached to the global environment.
     *
     * @param name       A label string to associate with this joint
     * @param world      The world that this belongs to
     * @param jointGroup An optional group to associate this joint with
     */
    public JointHinge( String name, World world, JointGroup jointGroup ) {
        super( name, jointGroup );
        jointId = Ode.dJointCreateHinge( world.getId(), jointGroupId );
    }

    public void setAnchor( float x, float y, float z ) {
        Ode.dJointSetHingeAnchor( jointId, x, y, z );
    }

    public Vector3f getAnchor() {
        SWIGTYPE_p_float arr = Ode.new_floatArray( 3 );
        Ode.dJointGetHingeAnchor( jointId, arr );
        Vector3f ret = new Vector3f( Ode.floatArray_getitem( arr, 0 ),
                Ode.floatArray_getitem( arr, 1 ),
                Ode.floatArray_getitem( arr, 2 ) );
        Ode.delete_floatArray( arr );
        return ret;
    }

    public Vector3f getAnchor2() {
        SWIGTYPE_p_float arr = Ode.new_floatArray( 3 );
        Ode.dJointGetHingeAnchor2( jointId, arr );
        Vector3f ret = new Vector3f( Ode.floatArray_getitem( arr, 0 ),
                Ode.floatArray_getitem( arr, 1 ),
                Ode.floatArray_getitem( arr, 2 ) );
        Ode.delete_floatArray( arr );
        return ret;
    }

    @Override
    public void setAxis1( float x, float y, float z ) {
        Ode.dJointSetHingeAxis( jointId, x, y, z );
    }

    /**
     * @param x
     * @param y
     * @param z
     * @deprecated use setAxis1 instead
     */
    public void setAxis( float x, float y, float z ) {
        Ode.dJointSetHingeAxis( jointId, x, y, z );
    }

    public Vector3f getAxis() {
        SWIGTYPE_p_float arr = Ode.new_floatArray( 3 );
        Ode.dJointGetHingeAxis( jointId, arr );
        Vector3f ret = new Vector3f( Ode.floatArray_getitem( arr, 0 ),
                Ode.floatArray_getitem( arr, 1 ),
                Ode.floatArray_getitem( arr, 2 ) );
        Ode.delete_floatArray( arr );
        return ret;
    }

    @Override
    public float getAngle() {
        return Ode.dJointGetHingeAngle( jointId );
    }

    @Override
    public float getAngleRate() {
        return Ode.dJointGetHingeAngleRate( jointId );
    }

    /**
     * Set the amount of constant force to mix into the system when the
     * bodies are not at a stop. This value has no effect when the bodies are
     * at one of the two stops.
     *
     * @param force The amount of force to use
     */
    public void setConstantForceMix( float force ) {
        Ode.dJointSetSliderParam( jointId, OdeConstants.dParamCFM, force );
    }

    /**
     * Get the amount of the constant force mix parameter currently set for
     * positions between the two stops.
     *
     * @return The current constant force mix
     */
    public float getConstantForceMix() {
        return Ode.dJointGetSliderParam( jointId, OdeConstants.dParamCFM );
    }

    /**
     * Set the minimum angle that this joint is permitted to rotate to. Angles
     * are specified relative to the initial position that the joint was
     * created in. The angle value is limited to the range +/- &pi;. If the
     * the provided angle is out of this range, an exception is thrown.
     * <p/>
     * Note that if the maximum angle provided is less than the minimum angle
     * at the point of evaluation, ODE ignores all limits.
     * <p/>
     * A value of Float.NEGATIVE_INFINITY can be used to disable the maximum stop.
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

        Ode.dJointSetHingeParam( jointId, OdeConstants.dParamLoStop, angle );
    }

    /**
     * Fetch the currently set maximum angle stop from this joint.
     *
     * @return A angle in radians in the range [-&pi;,+&pi;] or
     *         Float.NEGATIVE_INFINITY
     */
    public float getMinAngleStop() {
        return Ode.dJointGetHingeParam( jointId, OdeConstants.dParamLoStop );
    }

    /**
     * Set the maximum angle that this joint is permitted to rotate to. Angles
     * are specified relative to the initial position that the joint was
     * created in. The angle value is limited to the range +/- &pi;. If the
     * the provided angle is out of this range, an exception is thrown.
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

        Ode.dJointSetHingeParam( jointId, OdeConstants.dParamHiStop, angle );
    }

    /**
     * Fetch the currently set maximum angle stop from this joint.
     *
     * @return A angle in radians in the range [-&pi;,+&pi;] or
     *         Float.POSITIVE_INFINITY
     */
    public float getMaxAngleStop() {
        return Ode.dJointGetHingeParam( jointId, OdeConstants.dParamHiStop );
    }

    /**
     * Set the bounciness of the stops. This is a value in the range [0,1]
     * defining how hitting the stop will effect the return travel of the two
     * bodies. A value of 0 means there is no bounce and the bodies will not
     * bounce back. A value of 1 means the full contact velocity at the stop
     * will be reflected back in the opposite direction.
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
     * Fetch the current bounce factor for the hinge stop.
     *
     * @return The bounce factor as a value in the range [0,1]
     */
    public float getStopBounce() {
        return Ode.dJointGetHingeParam( jointId, OdeConstants.dParamBounce );
    }

    /**
     * Set the amount of stop bounce error reduction. This value should be
     * between 0 and 1. 0 is no reduction at all, 1 is full correction in a
     * single step.
     *
     * @param erp The amount of error reduction to use
     */
    public void setStopERP( float erp ) {
        Ode.dJointSetSliderParam( jointId, OdeConstants.dParamStopERP, erp );
    }

    /**
     * Get the amount of the stop error reduction parameter currently set. This
     * value will be between 0 and 1. 0 is no bounce at all, 1 is full bounce.
     *
     * @return A value between 0 and 1
     */
    public float getStopERP() {
        return Ode.dJointGetSliderParam( jointId, OdeConstants.dParamStopERP );
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
        Ode.dJointSetSliderParam( jointId, OdeConstants.dParamStopCFM, force );
    }

    /**
     * Get the amount of the stop constant force mix parameter currently set.
     *
     * @return The current constant force mix at the stops
     */
    public float getStopCFM() {
        return Ode.dJointGetSliderParam( jointId, OdeConstants.dParamStopCFM );
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
        Ode.dJointSetHingeParam( jointId, parameter, value );
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
        return Ode.dJointGetHingeParam( jointId, parameter );
    }
}
