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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.jme.math.Vector3f;
import org.odejava.ode.Ode;
import org.odejava.ode.SWIGTYPE_p_dWorldID;
import org.odejava.ode.SWIGTYPE_p_float;

/**
 * The world object is a container for rigid bodies and joints. Objects in
 * different worlds can not interact, for example rigid bodies from two
 * different worlds can not collide.
 * <p/>
 * All the objects in a world exist at the same point in time, thus one reason
 * to use separate worlds is to simulate systems at different rates.
 * <p/>
 * Most applications will only need one world.
 * <p/>
 * <p/>
 * <b>Usage Notes</b>
 * <p/>
 * Odejava supports only single world for now. Support for multiple
 * worlds is added later. Only latest created World class will be used on
 * Odejava. Create this class only once!
 * <p/>
 * <p/>
 * The default value for contact surface thickness is zero. This can
 * cause floating point accuracy problems and result in visible jitter of
 * the objects. It is recommended that a small value of around 0.001 be set
 * to avoid this problem.
 * <p/>
 * <p/>
 * <b>Unimplemented</b>
 * <p/>
 * <ul>
 * <li>Auto disable of all forms. Getter methods return default values</li>
 * <li>Contact surface thickness</li>
 * <li>Correction velocity</li>
 * </ul>
 * <p/>
 * Created 16.12.2003 (dd.mm.yyyy)
 *
 * @author Jani Laakso E-mail: jani.laakso@itmill.com
 *         see http://odejava.dev.java.net
 */
public class World {

    /**
     * Default step size to use if nothing else set.
     */
    private static final float DEFAULT_STEPSIZE = 0.05f;

    /**
     * Default iterations per step to use if nothing else set.
     */
    private static final int DEFAULT_ITERATIONS = 10;

    private SWIGTYPE_p_dWorldID worldId;

    /**
     * The current time increment per step in seconds
     */
    private float stepSize;

    /**
     * The number of iterations ODE should to make per step
     */
    private int stepInteractions;

    /**
     * Flag indicating that this world has been requested to be deleted. After
     * this is set to true, none of the methods should allow further calls to
     * ODE as the values are invalid, and may well cause a crash of the library
     * or other strange error.
     */
    protected boolean deleted;
    public static final String LOGGER_NAME = "ODEJava";

    /**
     * Create a new, empty world. StepSize is initialised to 0.05 seconds and
     * 10 iterations per step.
     */
    public World() {
        worldId = Ode.dWorldCreate();

        stepSize = DEFAULT_STEPSIZE;
        stepInteractions = DEFAULT_ITERATIONS;

        deleted = false;

        //reduce accuracy
        //Ode.dWorldSetQuickStepNumIterations( worldId, 10 );
    }

    /**
     * Set the gravity vector to be applied to this world
     *
     * @param x The x component of the gravity vector
     * @param y The y component of the gravity vector
     * @param z The z component of the gravity vector
     */
    public void setGravity( float x, float y, float z ) {
        Ode.dWorldSetGravity( worldId, x, y, z );
    }

    /**
     * Get the gravity vector for the world. A new vector instance will be
     * created for each request. This is identical to calling
     * <code>getGravity(null)</code>.
     */
    public Vector3f getGravity() {
        return getGravity( (Vector3f) null );
    }

    /**
     * Get the vector for the current gravity and place it in the user-provided
     * data structure. If the user-provided data structure is null, then a
     * new instance is created and returned, otherwise the user provided
     * structure is used as the return value.
     *
     * @param val An object to place the values into or null
     * @return Either the val parameter or a new object
     */
    public Vector3f getGravity( Vector3f val ) {
        Vector3f ret = val;

        if ( ret == null ) {
            ret = new Vector3f();
        }

        SWIGTYPE_p_float arr = Ode.new_floatArray( 3 );
        Ode.dWorldGetGravity( worldId, arr );
        ret.x = Ode.floatArray_getitem( arr, 0 );
        ret.y = Ode.floatArray_getitem( arr, 1 );
        ret.z = Ode.floatArray_getitem( arr, 2 );
        Ode.delete_floatArray( arr );
        return ret;
    }

    /**
     * Get the vector for the current gravity and place it in the user-provided
     * array.  The array provided must be at least length 3.
     *
     * @param val The array to copy the value into
     */
    public void getGravity( float[] val ) {
        SWIGTYPE_p_float arr = Ode.new_floatArray( 3 );
        Ode.dWorldGetGravity( worldId, arr );
        val[0] = Ode.floatArray_getitem( arr, 0 );
        val[1] = Ode.floatArray_getitem( arr, 1 );
        val[2] = Ode.floatArray_getitem( arr, 2 );
        Ode.delete_floatArray( arr );
    }

    public SWIGTYPE_p_dWorldID getId() {
        return worldId;
    }

    /**
     * StepFast uses ODE's dWorldStepFast1.
     */
    public void stepFast() {
        // Step world based on contact jointgroup information
        Ode.dWorldStepFast1( worldId, stepSize, stepInteractions );
    }

    /**
     * Step uses ODE's dWorldStep with the predefined step size
     * and number of iterations.
     */
    public void step() {
        // Step world based on contact jointgroup information
        Ode.dWorldStep( worldId, stepSize );
    }

    /**
     * Step uses ODE's dWorldStep with the passed in step size.
     * This is would be better to be used in variable-framerate systems
     * where the step size is not known ahead of time.
     *
     * @param stepTime The time to use for this increment, in seconds
     */
    public void step( float stepTime ) {
        Ode.dWorldStep( worldId, stepTime );
    }

    /**
     * Step uses ODE's dWorldQuickStep with the predefined step size
     * and number of iterations.
     */
    public void quickStep() {
        // Step world based on contact jointgroup information
        Ode.dWorldQuickStep( worldId, stepSize );
    }

    /**
     * Step uses ODE's dWorldQuickStep with the passed in step size.
     * This is would be better to be used in variable-framerate systems
     * where the step size is not known ahead of time.
     *
     * @param stepTime The time to use for this increment, in seconds
     */
    public void quickStep( float stepTime ) {
        Ode.dWorldQuickStep( worldId, stepTime );
    }

    /**
     * Request deletion of this world. Any further calls to this world
     * instance after this has been called will be met with an error.
     */
    public void delete() {
        if ( deleted ) {
            return;
        }

        // Delete world
        Ode.dWorldDestroy( worldId );
        deleted = true;
    }

    /**
     * @return Returns the stepInteractions.
     */
    public int getStepInteractions() {
        return stepInteractions;
    }

    /**
     * Number of interactions, higher gives better accuracy but lower speed in
     * case of many simultaneous collisions.
     *
     * @param stepInteractions The stepInteractions to set.
     */
    public void setStepInteractions( int stepInteractions ) {
        this.stepInteractions = stepInteractions;
    }

    /**
     * @return Returns the stepSize.
     */
    public float getStepSize() {
        return stepSize;
    }

    /**
     * Step size, higher decreases accuracy but gives more speed.
     *
     * @param stepSize The stepSize to set, in seconds
     */
    public void setStepSize( float stepSize ) {
        this.stepSize = stepSize;
    }

//    /**
//     * Get body by name. If the name is not known then this will return null.
//     *
//     * @param name The name to look up the body by
//     * @return The body object corresponding to the name, or null if none
//     */
//    public Body getBody(String name) {
//        return (Body) bodyMap.get(name);
//    }

    public void setAutoEnableDepthSF1( int autoEnableDepth ) {
        Ode.dWorldSetAutoEnableDepthSF1( worldId, autoEnableDepth );
    }

    public int getAutoEnableDepthSF1( int autoEnableDepth ) {
        return Ode.dWorldGetAutoEnableDepthSF1( worldId );
    }

    /**
     * Set the contact surface layer thickness around all geometry objects.
     * Contacts are allowed to sink into the surface layer up to the given
     * depth before coming to rest.
     * <p/>
     * The value provided should be non-negative.
     *
     * @param depth The depth value to allow
     */
    public void setContactSurfaceThickness( float depth ) {
//        Ode.dWorldSetContactSurfaceLayer(worldId, depth);
    }

    /**
     * Get the currently set value of the world's contact surface layer
     * thickness.
     *
     * @return A non-negative value representing the thickness
     */
    public float getContactSurfaceThickness() {
        return 0;
//        return Ode.dWorldGetContactSurfaceLayer(worldId);
    }

    /**
     * Control whether the world should allow auto-disable of bodies. A value
     * of true will enable the auto disable ability. Whether individual bodies
     * are disabled or not depends on the settings of the individual
     * thresholds (which can be set by other methods).
     *
     * @param state True to enable auto disabling of bodies, false to disable
     */
    public void setAutoDisableBodies( boolean state ) {
        Ode.dWorldSetAutoDisableFlag( worldId, state ? 1 : 0 );
    }

    /**
     * Check to see the current state of the auto disable functionality.
     *
     * @return true if the auto-disable mode is on, false otherwise
     */
    public boolean isAutoDisablingBodies() {
        boolean ret;

        int flag = Ode.dWorldGetAutoDisableFlag( worldId );
        ret = flag == 1 ? true : false;

        return ret;
    }

    /**
     * Set the threshold for the linear velocity that will cause a body to be
     * disabled.  Once the velocity falls below this value, the body will be
     * subject to being disabled. The threshold is only used if the auto
     * disable capability is enabled.
     *
     * @param vel The speed below which the body is disabled
     */
    public void setLinearVelocityDisableThreshold( float vel ) {
//        Ode.dWorldSetAutoDisableLinearThreshold(worldId, vel);
    }

    /**
     * Get the threshold for linear velocity at which a body will be
     * automatically disabled.
     *
     * @return The current threshold value
     */
    public float getLinearVelocityDisableThreshold() {
        return 0.01f;
//        return Ode.dWorldGetAutoDisableLinearThreshold(worldId);
    }

    /**
     * Set the threshold for the angular velocity that will cause a body to be
     * disabled.  Once the velocity falls below this value, the body will be
     * subject to being disabled. The threshold is only used if the auto
     * disable capability is enabled.
     *
     * @param vel The speed below which the body is disabled
     */
    public void setAngularVelocityDisableThreshold( float vel ) {
//        Ode.dWorldSetAutoDisableAngularThreshold(worldId, vel);
    }

    /**
     * Get the threshold for angular velocity at which a body will be
     * automatically disabled.
     *
     * @return The current threshold value
     */
    public float getAngularVelocityDisableThreshold() {
        return 0.01f;
//        return Ode.dWorldGetAutoDisableAngularThreshold(worldId);
    }

    /**
     * Set the number of evaluation steps before an umoving body is disabled.
     * If the body has not moved in this number of steps, it is automatically
     * disabled. This setting is only used if the auto disable capabilities is
     * enabled. If the number of steps is negative or zero, bodies cannot be
     * disabled using this way.
     *
     * @param steps The number of evaluation steps to use or negative to disable
     */
    public void setStepDisableThreshold( int steps ) {
//        Ode.dWorldSetAutoDisableSteps(worldId, steps);
    }

    /**
     * Get the threshold for the number of steps at which a body will be
     * automatically disabled.
     *
     * @return The current threshold value
     */
    public int getStepDisableThreshold() {
        return 10;
//        return Ode.dWorldGetAutoDisableSteps(worldId);
    }

    /**
     * Set the total amount of evaluation time an umoving body is disabled.
     * If the body has not moved in this time, it is automatically
     * disabled. This setting is only used if the auto disable capabilities is
     * enabled. If the time is negative or zero, bodies cannot be
     * disabled using this way.
     *
     * @param time The amount of time in seconds or negative to disable
     */
    public void setTimeDisableThreshold( float time ) {
//        Ode.dWorldSetAutoDisableSteps(worldId, time);
    }

    /**
     * Get the threshold for the evaluation time at which a body will be
     * automatically disabled.
     *
     * @return The current threshold value
     */
    public float getTimeDisableThreshold() {
        return 0;
//        return Ode.dWorldGetAutoDisableTime(worldId);
    }

    /**
     * Set the amount of constant force to mix into the system when the
     * bodies are not at a stop. This value has no effect when the bodies are
     * at one of the two stops.
     *
     * @param force The amount of force to use
     */
    public void setConstraintForceMix( float force ) {
        Ode.dWorldSetCFM( worldId, force );
    }

    /**
     * Get the amount of the constant force mix parameter currently set for
     * positions between the two stops.
     *
     * @return The current constant force mix
     */
    public float getConstraintForceMix() {
        return Ode.dWorldGetCFM( worldId );
    }

    /**
     * Set the amount of error reduction. This value should be
     * between 0 and 1. 0 is no reduction at all, 1 is full correction in a
     * single step.
     *
     * @param erp The amount of error reduction to use
     */
    public void setErrorReductionParameter( float erp ) {
        Ode.dWorldSetERP( worldId, erp );
    }

    /**
     * Get the amount of the error reduction parameter currently set. This
     * value will be between 0 and 1. 0 is no bounce at all, 1 is full bounce.
     *
     * @return A value between 0 and 1
     */
    public float getErrorReductionParameter() {
        return Ode.dWorldGetERP( worldId );
    }

    /**
     * Set the maximum correction velocity that is allowed during a contact.
     * The default value is infinite, meaning deeply embedded objects will pop
     * instantly back to the surface. Although negative values are allowed,
     * this will force the objects to further embed themselves, which is not
     * a good thing.
     *
     * @param vel The speed at which objects should correct contact problems
     */
    public void setMaxCorrectionVelocity( float vel ) {
//        Ode.dWorldSetContactMaxCorrectingVel(worldId, vel);
    }

    /**
     * Get the current maximum correction velocity used during cpntacts. The
     * default value is Float.POSITIVE_INFINITY.
     *
     * @return The correction velocity set. No range limits
     */
    public float getMaxCorrectionVelocity() {
        return 0;
//        return Ode.dWorldGetContactMaxCorrectingVel(worldId);
    }
}
