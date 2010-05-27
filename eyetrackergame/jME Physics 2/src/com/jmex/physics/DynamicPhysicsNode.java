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
import java.util.ArrayList;

import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;

/**
 * This {@link PhysicsNode} represents a physical entity that can be subject to forces to move and/or rotate it.
 * <p/>
 * PhysicsNodes are created solely by the PhysicsSpace (methods {@link PhysicsSpace#createDynamicNode()} and
 * {@link PhysicsSpace#createStaticNode()}).
 *
 * @author Irrisor
 */
public abstract class DynamicPhysicsNode extends PhysicsNode {
    private static final Vector3f ZERO = new Vector3f();

    protected DynamicPhysicsNode( String name ) {
        super( name );
    }

    protected DynamicPhysicsNode() {
    }

    @Override
    public final boolean isStatic() {
        return false;
    }

    /**
     * Query mass of this physical entity. Defaults to 1.
     *
     * @return mass
     */
    public abstract float getMass();

    /**
     * Change mass of this object.
     *
     * @param value new mass
     */
    public abstract void setMass( final float value );

    /**
     * Obtain the center of mass.
     *
     * @param store where to put the position (null to create a new vector)
     * @return center as vector in object coordinate space
     */
    public abstract Vector3f getCenterOfMass( Vector3f store );

    /**
     * Change the center of mass for this node. This moves the attached spatials to a suited position. So this
     * method should be called _after_ configuring the geometries (e.g. after {@link #generatePhysicsGeometry()}).
     *
     * @param value new center as vector in object coordinate space
     */
    public abstract void setCenterOfMass( final Vector3f value );

    /**
     * Reset force on this node to 0.
     */
    public abstract void clearForce();

    /**
     * Obtain the current force on this node.
     *
     * @param store where to put the force (null to create a new vector)
     * @return store
     */
    public abstract Vector3f getForce( Vector3f store );

    /**
     * Add a force to be applied to this node at a given relative location. The force vector is given
     * in world coordinate space. The force is applied in the next computation step and is cleared afterwards.
     * Thus this method has to be called frequently (before each physics step) if a constant force should be applied.
     *
     * @param force force to be added (world coordinate space)
     * @param at    position of the object where the force should be applied (relative to the center of mass)
     * @see PhysicsUpdateCallback
     */
    public abstract void addForce( Vector3f force, Vector3f at );

    private static final Vector3f centerOfMassStore = new Vector3f();

    /**
     * Add a force to be applied to this node (at the center of mass). The vector is given in world coordinate space.
     *
     * @param force force to be added
     * @see #addForce(com.jme.math.Vector3f, com.jme.math.Vector3f)
     */
    public void addForce( Vector3f force ) {
        addForce( force, ZERO );
    }

    /**
     * Reset torque on this node to 0.
     */
    public abstract void clearTorque();

    /**
     * Obtain the current torque on this node.
     *
     * @param store where to put the torque (null to create a new vector)
     * @return store
     */
    public abstract Vector3f getTorque( Vector3f store );

    /**
     * Add a torque to be applied to this node. The vector is given in world coordinate space.
     *
     * @param torque torque to be added
     */
    public abstract void addTorque( Vector3f torque );

    /**
     * Sets the linear velocity of this node.
     *
     * @param velocity new velocity in world coordinate space
     */
    public abstract void setLinearVelocity( Vector3f velocity );

    /**
     * Query the linear velocity of this node. The
     * passed in Vector3f will be populated with the values, and then returned.
     *
     * @param store where to store the velocity (null to create a new vector)
     * @return store
     */
    public abstract Vector3f getLinearVelocity( Vector3f store );

    /**
     * Sets the angular velocity of this node.
     *
     * @param velocity new velocity in world coordinate space
     */
    public abstract void setAngularVelocity( Vector3f velocity );

    /**
     * Query the angular velocity of this node. The
     * passed in Vector3f will be populated with the values, and then returned.
     *
     * @param store where to store the velocity (null to create a new vector)
     * @return store
     */
    public abstract Vector3f getAngularVelocity( Vector3f store );


    /**
     * @return true if node is affected by gravity
     * @see #setAffectedByGravity(boolean)
     */
    public abstract boolean isAffectedByGravity();

    /**
     * Switch gravity on/off for this single node.
     *
     * @param value new value
     */
    public abstract void setAffectedByGravity( final boolean value );

    /**
     * Resets all force, torque and velocities.
     */
    public void clearDynamics() {
        clearForce();
        clearTorque();
        setLinearVelocity( ZERO );
        setAngularVelocity( ZERO );
    }

    private static final Vector3f tmpTo = new Vector3f();

    @Override
    protected void drawDebugInfo( Renderer renderer ) {
        super.drawDebugInfo( renderer );

        PhysicsDebugger.drawArrow( getWorldTranslation(),
                getLinearVelocity( tmpTo ).multLocal( 0.1f )
                        .addLocal( getWorldTranslation() ),
                ColorRGBA.gray, renderer );

        PhysicsDebugger.drawArrow( getWorldTranslation(),
                getForce( tmpTo ).multLocal( 0.1f )
                        .addLocal( getWorldTranslation() ),
                ColorRGBA.red, renderer );

        if ( isAffectedByGravity() ) {
            PhysicsDebugger.drawArrow( getWorldTranslation(),
                    getSpace().getDirectionalGravity( tmpTo ).multLocal( 0.1f )
                            .addLocal( getWorldTranslation() ),
                    ColorRGBA.red, renderer );
        }
    }

    /**
     * Computes the mass for this node that would result from the attached geometries and their densities (materials).
     */
    public abstract void computeMass();

    @Override
    public Class getClassTag() {
    		return DynamicPhysicsNode.class;
    }
    
    public static final String MASS_PROPERTY = "mass";
    public static final String CENTER_OF_MASS_PROPERTY = "centerOfMass";
    public static final String LINEAR_VELOCITY_PROPERTY = "linearVelocity";
    public static final String ANGULAR_VELOCITY_PROPERTY = "angularVelocity";
    public static final String AFFECTED_BY_GRAVITY_PROPERTY = "affectedByGravity";
    public static final String JOINTS_PROPERTY = "joints";

	@Override
	public void read(JMEImporter im) throws IOException {
		super.read(im);

        InputCapsule capsule = im.getCapsule( this );
        
        float mass = capsule.readFloat( MASS_PROPERTY, 1.0f );
        if ( mass != 1.0f )
        	setMass( mass );

        Vector3f centerOfMass = (Vector3f) capsule.readSavable( CENTER_OF_MASS_PROPERTY, Vector3f.ZERO );
        if ( !Vector3f.ZERO.equals( centerOfMass ) ) 
        	setCenterOfMass( centerOfMass );

        Vector3f linearVelocity = (Vector3f) capsule.readSavable( LINEAR_VELOCITY_PROPERTY, Vector3f.ZERO );
        if ( !Vector3f.ZERO.equals( linearVelocity ) ) 
        	setLinearVelocity( linearVelocity );

        Vector3f angularVelocity = (Vector3f) capsule.readSavable( ANGULAR_VELOCITY_PROPERTY, Vector3f.ZERO );
        if ( !Vector3f.ZERO.equals( angularVelocity ) ) 
        	setAngularVelocity( angularVelocity );

        setAffectedByGravity( capsule.readBoolean( AFFECTED_BY_GRAVITY_PROPERTY, true ) );
        
		// read joints
		// joints do not have to be added to the space here, as they're automatically
		// added by the binary modules which call space.createJoint()
        capsule.readSavableArrayList(JOINTS_PROPERTY, null);
	}

	@Override
	public void write(JMEExporter ex) throws IOException {
		super.write(ex);

        OutputCapsule capsule = ex.getCapsule( this );
        capsule.write( getMass(), MASS_PROPERTY, 1.0f );
        getCenterOfMass( centerOfMassStore );
        capsule.write( centerOfMassStore, CENTER_OF_MASS_PROPERTY, Vector3f.ZERO );
        capsule.write( getLinearVelocity( null ), LINEAR_VELOCITY_PROPERTY, Vector3f.ZERO );
        capsule.write( getAngularVelocity( null ), ANGULAR_VELOCITY_PROPERTY, Vector3f.ZERO );
        capsule.write( isAffectedByGravity(), AFFECTED_BY_GRAVITY_PROPERTY, true );
        
        // search the space for joints connected to this node
        PhysicsSpace space = getSpace();
        ArrayList<Joint> connectedJoints = new ArrayList<Joint>();
        for (Joint joint : space.getJoints()) {
			for (DynamicPhysicsNode node : joint.getNodes()) {
				if (this == node) {
					// found a joint for this node
					connectedJoints.add(joint);
				}
			}
		}
        capsule.writeSavableArrayList(connectedJoints, JOINTS_PROPERTY, null);
	}

    /**
     * This method may be called to tell the physics implementation that this node does not move by forces like gravity
     * and other internal constantly applied forces currently.
     * This can optimize the physics calculations quite a lot. Note this will cause this node to e.g. hang around
     * in the air if it's not really resting on the ground.
     * @see #unrest()
     */
    public abstract void rest();

    /**
     * This method tells the physics implementation to check the node for movement after it has been rested (manually
     * or automatically).
     * @see #rest()
     */
    public abstract void unrest();
}

/*
* $log$
*/
