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
import org.odejava.ode.SWIGTYPE_p_dBodyID;
import org.odejava.ode.SWIGTYPE_p_dJointGroupID;
import org.odejava.ode.SWIGTYPE_p_dJointID;
import org.odejava.ode.dJointFeedback;

/**
 * In real life a joint is something like a hinge, that is used to connect two
 * objects. In ODE a joint is very similar: It is a relationship that is
 * enforced between two bodies so that they can only have certain positions and
 * orientations relative to each other.
 * <p/>
 * <p/>
 * Created 20.12.2003 (dd.mm.yyyy)
 *
 * @author Jani Laakso E-mail: jani.laakso@itmill.com
 *         see http://odejava.dev.java.net
 */
public abstract class Joint {

    /**
     * Error message when the bounce range is negative
     */
    protected static final String BOUNCE_RANGE_MSG =
            "The bounce range value must be a non-negative value: ";

    /**
     * The ID of the group that this joint belongs to. Assigned during the
     * constructor and will either point to the group's ID or to the default
     * zero value if no group is provided.
     */
    protected SWIGTYPE_p_dJointGroupID jointGroupId;

    /**
     * The ODE ID of this joint object. Set during the constructor of the
     * concrete classes.
     */
    protected SWIGTYPE_p_dJointID jointId;

    /**
     * An arbitrary, user-defined name string for the joint. Not used by ODE.
     */
    protected String name;

    /**
     * If the user provided a joint group to create the joint with, this
     * will reference the group it is a part of. If the joint is part of the
     * global environment, and not part of a group, this will be null.
     */
    protected JointGroup group;

    /**
     * The first body that this joint is attached to. Null if not attached.
     */
    protected Body body1;

    /**
     * The second body that this joint is attached to. Null if not attached.
     */
    protected Body body2;

    /**
     * Dataholder for acquiring feedback information when the user has enabled
     * feedback tracking. If no tracking is currently taking place, this will
     * be null.
     */
    protected JointFeedback feedback;

    /**
     * Flag indicating that this joint has been requested to be deleted. After
     * this is set to true, none of the methods should allow further calls to
     * ODE as the values are invalid, and may well cause a crash of the library
     * or other strange error.
     */
    protected boolean deleted;

    /**
     * Create a new joint with a given name label and an optional group that
     * it is a member of. Both parameters are optional. If no joint group is
     * supplied then the default joint ID of 0 is used.
     *
     * @param name       A label string to associate with this joint
     * @param jointGroup An optional group to associate this joint with
     */
    public Joint( String name, JointGroup jointGroup ) {
        this.name = name;
        this.group = jointGroup;

        if ( jointGroup != null ) {
            jointGroup.addJoint( this );
            jointGroupId = jointGroup.getId();
        }
        else {
            jointGroupId = Ode.getJOINTGROUPID_ZERO();
        }

        deleted = false;
    }

    /**
     * Attach this joint to the two bodies. If both body references are null,
     * the joint is detached from both and put into limbo until re-attached to
     * one or more bodies. If this joint is already attached to bodies, it is
     * first detached from the old bodies and then attached to new bodies.
     * Either body reference may be null, in which case the joint is attached
     * to the global environment.
     *
     * @param body1 The first body reference to attach to
     * @param body2 The second body reference to attach to
     */
    public void attach( Body body1, Body body2 ) {

        SWIGTYPE_p_dBodyID id1 = body1 == null ? Ode.getBODYID_ZERO() : body1.getId();
        SWIGTYPE_p_dBodyID id2 = body2 == null ? Ode.getBODYID_ZERO() : body2.getId();

        Ode.dJointAttach( jointId, id1, id2 );
        this.body1 = body1;
        this.body2 = body2;
    }

    /**
     * Request deletion of this joint. If the joint is attached to a group, it
     * is not immediately deleted. Instead, it is marked as deleted and must wait
     * until the owning group is deleted or emptied. Any further calls to this joint
     * instance after this has been called will be met with an error.
     */
    public void delete() {
        Ode.dJointDestroy( jointId );
        deleted = true;
    }

    /**
     * Get the currently set name of this joint. If no name is set, this will
     * return null.
     *
     * @return The name string or null if none set
     */
    public String getName() {
        return name;
    }

    /**
     * Set a new custom name for this joint. A value of null will clear the
     * currently set name. This is a local convenience function as ODE does not
     * track names for joints.
     *
     * @param name The new name string to set.
     */
    public void setName( String name ) {
        this.name = name;
    }

    /**
     * Create a JointFeedback object and associate it with this joint. This
     * means that ODE will now store specific information about this joint in
     * this structure after every world step, so you can find out more
     * information on what forces and torques are acting upon it.
     */
    public void enableFeedbackTracking() {
        enableFeedbackTracking( true );
    }

    /**
     * Modify the current joint feedback tracking state. This can be used to
     * enable or disable feedback tracking as needed.
     *
     * @param state true to enable feedback tracking, false to disable
     */
    public void enableFeedbackTracking( boolean state ) {
        if ( state ) {
            // Don't renable if it is already running.
            if ( feedback == null ) {
                feedback = new JointFeedback( new dJointFeedback() );
                Ode.dJointSetFeedback( jointId, feedback.getFeedback() );
            }
        }
        else {
            feedback = null;
            Ode.dJointSetFeedback( jointId, null );
        }
    }

    /**
     * Check to see if feedback tracking is currently enabled.
     *
     * @return true if feeback tracking is running
     */
    public boolean isFeedbackTrackingEnabled() {
        return feedback != null;
    }

    /**
     * Return detailed information about this joint from the last simulation step.
     * Must first enableFeedbackTracking() to start getting information.
     *
     * @return The joint feedback instance, or null if tracking is not enabled.
     */
    public JointFeedback getFeedback() {
        return feedback;
    }

    /**
     * Returns the JointGroup that this joint was attached to when created. If
     * no group was assigned, this returns null.
     *
     * @return The group reference or null
     */
    public JointGroup getGroup() {
        return group;
    }

    /**
     * Returns the first body this joint is connected to. If no body is
     * assigned, this returns null.
     *
     * @return The current first body instance, or null
     */
    public Body getBody1() {
        return body1;
    }

    /**
     * Returns the second body this joint is connected to. If no body is
     * assigned, this returns null.
     *
     * @return The current second body instance, or null
     */
    public Body getBody2() {
        return body2;
    }

    /**
     * Set a generic, unchecked, parameter directly for this joint. The
     * parameter types that are permissable for the joint are defined by the
     * individual joint classes and the definitions can be found in
     * {@link org.odejava.ode.OdeConstants}. The default implementation in this
     * class does not support any parameters, and is implemented as an empty
     * method that silently ignores the request.
     *
     * @param parameter A parameter constant from OdeConstants
     * @param value     The new value to associate with the parameter
     */
    public abstract void setParam( int parameter, float value );

    /**
     * Get an unchecked parameter value directly from this joint. The
     * parameter types that are permissable for the joint are defined by the
     * individual joint classes and the definitions can be found in
     * {@link org.odejava.ode.OdeConstants}. The default implementation in this
     * class does not support any parameters, and shall always return zero.
     *
     * @param parameter A constant describing the parameter to fetch
     * @return The value of that parameter
     */
    public float getParam( int parameter ) {
        return 0;
    }

    public void setAxis1( float x, float y, float z ) {
        throw new UnsupportedOperationException();
    }

    public void setAxis2( float x, float y, float z ) {
        throw new UnsupportedOperationException();
    }


    public abstract void setAnchor( float x, float y, float z );

    public float getAngleRate() {
        throw new UnsupportedOperationException();
    }

    public float getAngle() {
        throw new UnsupportedOperationException();
    }

    public float getMaxAngleStop() {
        throw new UnsupportedOperationException();
    }

    public float getMinAngleStop() {
        throw new UnsupportedOperationException();
    }

    public void setMaxAngleStop( float value ) {
        throw new UnsupportedOperationException();
    }

    public void setMinAngleStop( float value ) {
        throw new UnsupportedOperationException();
    }

    public float getAngle2Rate() {
        throw new UnsupportedOperationException();
    }

    public float getAngle2() {
        throw new UnsupportedOperationException();
    }

    public void setBodiesCollide(boolean enabled)
    {
        Ode.dJointSetBodiesCollide( jointId, enabled ? 1 : 0 );
    }
}
