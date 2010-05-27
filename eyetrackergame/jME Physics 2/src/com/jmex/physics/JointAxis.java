/*
 * Copyright (c) 2005-2007 jME Physics 2
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of 'jME Physics 2' nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jmex.physics;

import java.io.IOException;

import com.jme.math.Vector3f;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;

/**
 * An axis for a {@link Joint}. An axis can belong to only one Joint. Each axis adds one degree of freedom to a joint.
 *
 * @author Irrisor
 * @see Joint
 * @see Joint#createRotationalAxis()
 * @see Joint#createTranslationalAxis()
 */
public abstract class JointAxis implements Savable {

    JointAxis() {
    }

    private Joint joint;

    public Joint getJoint() {
        return joint;
    }

    void setJoint( Joint joint ) {
        this.joint = joint;
    }

    /**
     * @return true if this axis imposes translational freedom in the axis direction.
     */
    public abstract boolean isTranslationalAxis();

    /**
     * @return true if this axis imposes rotational freedom around the axis direction.
     */
    public abstract boolean isRotationalAxis();

    /**
     * store direction of this axis.
     */
    private final Vector3f direction = new Vector3f();

    /**
     * Sets the direction of this axis.
     *
     * @param direction new direction in world coordinate space if the joint is attached to the world, object coordinate
     *                  space if attached to two objects
     * @see #isRelativeToSecondObject()
     */
    public void setDirection( Vector3f direction ) {
        this.direction.set( direction ).normalizeLocal();
    }

    /**
     * Query the direction of this axis. The
     * passed in Vector3f will be populated with the values, and then returned.
     *
     * @param store where to store the direction (null to create a new vector)
     * @return store
     */
    public Vector3f getDirection( Vector3f store ) {
        if ( store == null ) {
            store = new Vector3f();
        }
        store.set( direction );
        return store;
    }

    /**
     * Query position of the objects attached to this joint. For a translational axis this is in direction of this axis,
     * for a rotational axis this is around the axis direction in radians.
     *
     * @return the relative position of the objects attached to the joint
     */
    public abstract float getPosition();

    /**
     * @return the relative velocity (translation/angular speed) of the objects attached to the joint in/around
     *         direction of this axis
     */
    public abstract float getVelocity();


    /**
     * Change the force the joint motor can use to reach the desired velocity.
     *
     * @param value new acceleration than can be used
     * @see #setDesiredVelocity(float)
     */
    public abstract void setAvailableAcceleration( float value );

    /**
     * @return the maximum force available to reach the desired velocity
     * @see #setAvailableAcceleration
     */
    public abstract float getAvailableAcceleration();

    /**
     * Change the desired velocity. The joint motor uses the available linear acceleration to reach the specified
     * velocity as fast as possible.
     *
     * @param value new desired linear velocity
     * @see #setAvailableAcceleration(float)
     */
    public abstract void setDesiredVelocity( float value );

    /**
     * @return the last set desired velocity
     * @see #setDesiredVelocity(float)
     */
    public abstract float getDesiredVelocity();

    /**
     * Change the smallest allowed position.
     *
     * @param value smallest allowed position, {@link Float#NEGATIVE_INFINITY} to disable minimum
     */
    public abstract void setPositionMinimum( float value );

    /**
     * @return smallest allowed position, {@link Float#NEGATIVE_INFINITY} if minimum is disabled
     */
    public abstract float getPositionMinimum();

    /**
     * Change the largest allowed position.
     *
     * @param value largest allowed position, {@link Float#POSITIVE_INFINITY} to disable maximum
     */
    public abstract void setPositionMaximum( float value );

    /**
     * @return largest allowed position, {@link Float#POSITIVE_INFINITY} if maximum is disabled
     */
    public abstract float getPositionMaximum();

    /**
     * Copy axis data from another axis.
     *
     * @param toCopy where to read values from
     */
    public void copy( JointAxis toCopy ) {
        if ( isRotationalAxis() != toCopy.isRotationalAxis() ) {
            throw new IllegalArgumentException( "cannot copy from linear to rotational axis or vice versa" );
        }
        setDirection( toCopy.getDirection( null ) );
        setDesiredVelocity( toCopy.getDesiredVelocity() );
        setAvailableAcceleration( toCopy.getAvailableAcceleration() );
        setRelativeToSecondObject( toCopy.isRelativeToSecondObject() );
        try {
            setPositionMaximum( toCopy.getPositionMaximum() );
        } catch ( UnsupportedOperationException e ) {
            // ok, skip it
        }
        try {
            setPositionMinimum( toCopy.getPositionMinimum() );
        } catch ( UnsupportedOperationException e ) {
            // ok, skip it
        }
    }


    /**
     * getter for field relativeToSecondObject
     *
     * @return true if this axis' direction is given in coordinates relative to the second object. The default is false
     *         which means it is relative to the first object (resp. the world)
     */
    public boolean isRelativeToSecondObject() {
        return this.relativeToSecondObject;
    }

    /**
     * store the value for field relativeToSecondObject
     */
    private boolean relativeToSecondObject;

    /**
     * setter for field relativeToSecondObject
     *
     * @param value new value
     */
    public void setRelativeToSecondObject( final boolean value ) {
        this.relativeToSecondObject = value;
    }

    public static final String DIRECTION_PROPERTY = "direction";
    public static final String RELATIVE_TO_SECOND_OBJECT_PROPERTY = "relativeToSecondObject";
    public static final String JOINT_PROPERTY = "joint";
    public static final String AVAILABLE_ACCELERATION_PROPERTY = "availableAcceleration";
    public static final String DESIRED_VELOCITY_PROPERTY = "desiredVelocity";
    public static final String POSITION_MAXIMUM_PROPERTY = "positionMaximum";
    public static final String POSITION_MINIMUM_PROPERTY = "positionMinimum";

    public void read( JMEImporter im ) throws IOException {
        InputCapsule capsule = im.getCapsule( this );
        
        setAvailableAcceleration( capsule.readFloat(AVAILABLE_ACCELERATION_PROPERTY, 0.0f));
        setDesiredVelocity( capsule.readFloat(DESIRED_VELOCITY_PROPERTY, 0.0f));
        setPositionMaximum( capsule.readFloat(POSITION_MAXIMUM_PROPERTY, Float.POSITIVE_INFINITY));
        setPositionMinimum( capsule.readFloat(POSITION_MINIMUM_PROPERTY, Float.NEGATIVE_INFINITY));
        setDirection( (Vector3f) capsule.readSavable( DIRECTION_PROPERTY, Vector3f.ZERO ) );
        setRelativeToSecondObject( capsule.readBoolean( RELATIVE_TO_SECOND_OBJECT_PROPERTY, false ) );

        // joint property is not read because it is set during joint creation
    }

    public void write( JMEExporter ex ) throws IOException {
        OutputCapsule capsule = ex.getCapsule( this );
        
        capsule.write( getAvailableAcceleration(), AVAILABLE_ACCELERATION_PROPERTY, 0.0f);
        capsule.write( getDesiredVelocity(), DESIRED_VELOCITY_PROPERTY, 0.0f);
        capsule.write( getPositionMaximum(), POSITION_MAXIMUM_PROPERTY, Float.POSITIVE_INFINITY);
        capsule.write( getPositionMinimum(), POSITION_MINIMUM_PROPERTY, Float.NEGATIVE_INFINITY);
        capsule.write( direction, DIRECTION_PROPERTY, Vector3f.ZERO );
        capsule.write( isRelativeToSecondObject(), RELATIVE_TO_SECOND_OBJECT_PROPERTY, false );
        capsule.write( getJoint(), JOINT_PROPERTY, null );
    }

    public static Joint readJointFromInputCapsule( InputCapsule inputCapsule ) throws IOException {
        return (Joint) inputCapsule.readSavable( JOINT_PROPERTY, null );
    }
}

/*
 * $log$
 */

