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
public class JointSlider extends Joint {

    /**
     * Create a new Slider joint that belongs to the given world and does not
     * belong to any group. The name is set to the null string.
     *
     * @param world The world that this belongs to
     */
    public JointSlider( World world ) {
        this( null, world, null );
    }

    /**
     * Create a new Slider joint that belongs to the given world and has a
     * name. The name parameter is optional. The joint is attached to the
     * global environment.
     *
     * @param name  A label string to associate with this joint
     * @param world The world that this belongs to
     */
    public JointSlider( String name, World world ) {
        this( name, world, null );
    }

    /**
     * Create a new slider joint that belongs to the given world. The
     * JointGroup is optional and the name is set to the null string. If no
     * group is provided, the joint is attached to the global environment.
     *
     * @param world      The world that this belongs to
     * @param jointGroup An optional group to associate this joint with
     */
    public JointSlider( World world, JointGroup jointGroup ) {
        this( null, world, jointGroup );
    }

    /**
     * Create a new slider joint that belongs to the given world. The
     * JointGroup and name parameters are optional. If no group is provided,
     * the joint is attached to the global environment.
     *
     * @param name       A label string to associate with this joint
     * @param world      The world that this belongs to
     * @param jointGroup An optional group to associate this joint with
     */
    public JointSlider( String name, World world, JointGroup jointGroup ) {
        super( name, jointGroup );
        jointId = Ode.dJointCreateSlider( world.getId(), jointGroupId );
    }

    @Override
    public void setAxis1( float x, float y, float z ) {
        Ode.dJointSetSliderAxis( jointId, x, y, z );
    }

    public void setAnchor( float x, float y, float z ) {
        // nothing to do
    }

    /**
     * @param x
     * @param y
     * @param z
     * @deprecated use setAxis1 instead
     */
    public void setAxis( float x, float y, float z ) {
        Ode.dJointSetSliderAxis( jointId, x, y, z );
    }

    /**
     * Get the axis vector for the slide. A new vector instance will be created
     * for each request. This is identical to calling
     * <code>getAxis(null)</code>.
     *
     * @return A new vector object containing the axis values
     */
    public Vector3f getAxis() {
        return getAxis( null );
    }

    /**
     * Get the axis vector for the slider and place it in the user-provided
     * data structure. If the user-provided data structure is null, then a
     * new instance is created and returned, otherwise the user provided
     * structure is used as the return value.
     *
     * @param val An object to place the values into or null
     * @return Either the val parameter or a new object
     */
    public Vector3f getAxis( Vector3f val ) {
        Vector3f ret = val;

        if ( ret == null ) {
            ret = new Vector3f();
        }

        SWIGTYPE_p_float arr = Ode.new_floatArray( 3 );
        Ode.dJointGetSliderAxis( jointId, arr );
        ret.x = Ode.floatArray_getitem( arr, 0 );
        ret.y = Ode.floatArray_getitem( arr, 1 );
        ret.z = Ode.floatArray_getitem( arr, 2 );
        Ode.delete_floatArray( arr );
        return ret;
    }

    /**
     * Fetch the current separation distance between the two bodies.
     *
     * @return The current separation value
     */
    public float getPosition() {
        return Ode.dJointGetSliderPosition( jointId );
    }

    /**
     * Fetch the current rate of separation between the two bodies. A positive
     * value means the separation is closing over time, a positive value is
     * increasing separation.
     *
     * @return The current separation rate value
     */
    public float getPositionRate() {
        return Ode.dJointGetSliderPositionRate( jointId );
    }

    /**
     * Set the maximum separation allowable between the two bodies. This must
     * be a non-negative number. A negative value effectively disables the
     * joint. A value of Float.POSITIVE_INFINITY will disable the maximum
     * extent stop.
     *
     * @param distance The maximum separation permitted between bodies
     */
    public void setMaximumPosition( float distance ) {
        Ode.dJointSetSliderParam( jointId, OdeConstants.dParamHiStop, distance );
    }

    /**
     * Get the current maxiumum separation permitted between the two bodies.
     * A value of Float.POSITIVE_INFINITY indicates that the maximum stop
     * distance is currently disabled.
     *
     * @return The current maximum separation distance
     */
    public float getMaximumPosition() {
        return Ode.dJointGetSliderParam( jointId, OdeConstants.dParamHiStop );
    }

    /**
     * Set the minimum separation allowable between the two bodies. This must
     * be a non-negative number. A negative value effectively disables the
     * joint. A value of Float.NEGATIVE_INFINITY will disable the minimum
     * extent stop.
     *
     * @param distance The minimum separation permitted between bodies
     */
    public void setMinimumPosition( float distance ) {
        Ode.dJointSetSliderParam( jointId, OdeConstants.dParamLoStop, distance );
    }

    /**
     * Get the current miniumum separation permitted between the two bodies.
     * A value of Float.NEGATIVE_INFINITY indicates that the minimum stop
     * distance is currently disabled.
     *
     * @return The current minimum separation distance
     */
    public float getMinimumPosition() {
        return Ode.dJointGetSliderParam( jointId, OdeConstants.dParamLoStop );
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
     * Set the amount of stop bounce. This value must be between 0 and 1.
     * 0 is no bounce at all, 1 is full bounce.
     *
     * @param bounce The amount of bounce to use in the range [0,1]
     * @throws IllegalArgumentException The bounce factor is out of range
     */
    public void setStopBounce( float bounce )
            throws IllegalArgumentException {
        if ( bounce < 0 || bounce > 1 ) {
            throw new IllegalArgumentException( BOUNCE_RANGE_MSG + bounce );
        }

        Ode.dJointSetSliderParam( jointId, OdeConstants.dParamBounce, bounce );
    }

    /**
     * Get the amount of stop bounce currently set. This value will be
     * between 0 and 1. 0 is no bounce at all, 1 is full bounce.
     *
     * @return A value between 0 and 1
     */
    public float getStopBounce() {
        return Ode.dJointGetSliderParam( jointId, OdeConstants.dParamBounce );
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
     * value will be between 0 and 1. 0 is no reduction at all, 1 is full
     * reduction in a single time step.
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
        Ode.dJointSetSliderParam( jointId, parameter, value );
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
        return Ode.dJointGetSliderParam( jointId, parameter );
    }
}
