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
package com.jmex.physics.util;

import com.jme.input.InputHandler;
import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;
import com.jme.intersection.PickData;
import com.jme.intersection.TrianglePickResults;
import com.jme.math.Ray;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.system.DisplaySystem;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.Joint;
import com.jmex.physics.PhysicsCollisionGeometry;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.StaticPhysicsNode;
import com.jmex.physics.contact.ContactInfo;
import com.jmex.physics.geometry.PhysicsRay;

/**
 * A small tool to be able to pick the visual of physics nodes and move them around with the mouse.
 *
 * @author Irrisor
 */
public class PhysicsPicker {
    /**
     * root node of the scene - used for picking.
     */
    private final Node rootNode;
    /**
     * helper no the picked node is joined to.
     */
    private final DynamicPhysicsNode myNode;
    /**
     * joint to link myNode and picked node.
     */
    private final Joint joint;
    /**
     * joint to fix myNode in the world.
     */
    private final Joint worldJoint;
    private PhysicsPicker.PickAction pickAction;
    private PhysicsPicker.MoveAction moveAction;
    private InputHandler pickHandler;

    /**
     * Constructor of the class.
     *
     * @param input        where {@link #getInputHandler()} is attached to
     * @param rootNode     root node of the scene - used for picking
     * @param physicsSpace physics space to create joints in (picked nodes must reside in there)
     */
    public PhysicsPicker( InputHandler input, Node rootNode, PhysicsSpace physicsSpace ) {
        this( input,  rootNode, physicsSpace, false );
    }
    /**
     * Constructor of the class.
     *
     * @param input        where {@link #getInputHandler()} is attached to
     * @param rootNode     root node of the scene - used for picking
     * @param physicsSpace physics space to create joints in (picked nodes must reside in there)
     * @param allowRotation true to allow rotation of the picked object
     */
    public PhysicsPicker( InputHandler input, Node rootNode, PhysicsSpace physicsSpace, boolean allowRotation ) {
        this.inputHandler = new InputHandler();
        input.addToAttachedHandlers( this.inputHandler );
        this.rootNode = rootNode;
        joint = physicsSpace.createJoint();
        if ( allowRotation )
        {
            joint.createRotationalAxis().setDirection( Vector3f.UNIT_X );
            joint.createRotationalAxis().setDirection( Vector3f.UNIT_Y );
            joint.createRotationalAxis().setDirection( Vector3f.UNIT_Z );
        }
        joint.setSpring( 2000, 200 );
        myNode = physicsSpace.createDynamicNode();
        myNode.setName( "Physics Picker Helper Node");
        myNode.setAffectedByGravity( false );
        worldJoint = physicsSpace.createJoint();
        activatePhysicsPicker();
    }

    /**
     * @return the input handler for this picker
     */
    public InputHandler getInputHandler() {
        return inputHandler;
    }

    private InputHandler inputHandler;

    private DynamicPhysicsNode picked;

    private final Vector2f mousePosition = new Vector2f();

    private void activatePhysicsPicker() {
        pickAction = new PickAction();
        inputHandler.addAction( pickAction, InputHandler.DEVICE_MOUSE, 0, InputHandler.AXIS_NONE, false );

        moveAction = new MoveAction();
        inputHandler.addAction( moveAction, InputHandler.DEVICE_MOUSE, InputHandler.BUTTON_NONE,
                InputHandler.AXIS_ALL, false );
    }

    private void release() {
        picked = null;
        joint.detach();
        worldJoint.detach();
        myNode.setActive( false );
    }

    private final Vector3f pickedScreenPos = new Vector3f();
    private final Vector3f pickedWorldOffset = new Vector3f();

    private void attach( DynamicPhysicsNode node ) {
        DisplaySystem.getDisplaySystem().getScreenCoordinates( node.getWorldTranslation(), pickedScreenPos );
        DisplaySystem.getDisplaySystem().getWorldCoordinates( mousePosition, pickedScreenPos.z, pickedWorldOffset );

        picked = node;
        node.localToWorld( node.getCenterOfMass( myNode.getLocalTranslation() ) , myNode.getLocalTranslation() );
        pickedWorldOffset.subtractLocal( myNode.getLocalTranslation() );
        myNode.setActive( true );
        worldJoint.setAnchor( myNode.getLocalTranslation() );
        worldJoint.attach( myNode );
        joint.attach( myNode, node );
        joint.setAnchor( new Vector3f() );
    }

    public void delete() {
        inputHandler.removeAction( pickAction );
        inputHandler.removeAction( moveAction );
        myNode.setActive( false );
        myNode.removeFromParent();
        joint.detach();
        joint.setActive( false );
        worldJoint.detach();
        worldJoint.setActive( false );
        picked = null;
    }

    public DynamicPhysicsNode getPickedNode() {
        return picked;
    }

    private final Ray pickRay = new Ray();

    /**
     * @return true if picking is done with the visuals, false if done with the physics representation
     * @see #setPickModeVisual(boolean) 
     */
    public boolean isPickModeVisual() {
        return pickModeVisual;
    }

    /**
     * The mode used for picking: visual picking uses the bounding volumes and visual meshes of scene elements to
     * determine what was clicked by the user, the physical mode uses the actual physical representations.
     * @param pickModeVisual true to switch to visual mode, false to switch to physical mode
     */
    public void setPickModeVisual( boolean pickModeVisual ) {
        this.pickModeVisual = pickModeVisual;
    }

    private boolean pickModeVisual = true;

    private class PickAction extends InputAction {

        public void performAction( InputActionEvent evt ) {
            if ( evt.getTriggerPressed() ) {
                DisplaySystem.getDisplaySystem().getWorldCoordinates( mousePosition, 0, pickRay.origin );
                DisplaySystem.getDisplaySystem().getWorldCoordinates( mousePosition, 0.3f, pickRay.direction );
                pickRay.direction.subtractLocal( pickRay.origin ).normalizeLocal();

                if ( pickModeVisual )
                {
                    pickVisual();
                }
                else
                {
                    pickPhysical();
                }
            }
            else {
                release();
            }
        }
    }

    private final TrianglePickResults pickResults = new TrianglePickResults();
    private void pickVisual() {
        pickResults.clear();
        pickResults.setCheckDistance( true );
        rootNode.findPick( pickRay, pickResults );
        loopResults:
        for ( int i = 0; i < pickResults.getNumber(); i++ ) {
            PickData data = pickResults.getPickData( i );
            if ( data.getTargetTris() != null && data.getTargetTris().size() > 0 ) {
                Spatial target = data.getTargetMesh();
                while ( target != null ) {
                    if ( target instanceof DynamicPhysicsNode ) {
                        DynamicPhysicsNode picked = (DynamicPhysicsNode) target;
                        attach( picked );
                        break loopResults;
                    }
                    target = target.getParent();
                }
            }
        }
    }

    private PhysicsRay physicsRay;
    private PhysicsCollisionGeometry nearestPickedGeom;
    private float minPickDistance;
    private void pickPhysical()
    {
        if ( physicsRay == null )
        {
            final StaticPhysicsNode pickNode = myNode.getSpace().createStaticNode();
            pickNode.setActive( false );
            physicsRay = pickNode.createRay( "pickRay" );
            pickHandler = new InputHandler();
            pickHandler.addAction( new PhysicsPickAction(), physicsRay.getCollisionEventHandler(), false );
        }
        physicsRay.getLocalTranslation().set( pickRay.getOrigin() );
        physicsRay.getLocalScale().set( pickRay.getDirection() ).multLocal( 1000 );
        physicsRay.updateWorldVectors();

        nearestPickedGeom = null;
        minPickDistance = Float.POSITIVE_INFINITY;
        pickHandler.setEnabled( true );
        myNode.getSpace().pick( physicsRay );
        pickHandler.update( 0 );
        pickHandler.setEnabled( false );
        physicsRay.getLocalScale().set( 1, 1, 1 );

        if ( nearestPickedGeom != null )
        {
            attach( (DynamicPhysicsNode) nearestPickedGeom.getPhysicsNode() );
        }
    }

    private class MoveAction extends InputAction {
        private final Vector3f anchor = new Vector3f();

        public void performAction( InputActionEvent evt ) {

            switch ( evt.getTriggerIndex() ) {
                case 0:
                    mousePosition.x = evt.getTriggerPosition() * DisplaySystem.getDisplaySystem().getWidth();
                    break;
                case 1:
                    mousePosition.y = evt.getTriggerPosition() * DisplaySystem.getDisplaySystem().getHeight();
                    break;
                case 2:
                    // move into z direction with the wheel
                    if ( evt.getTriggerDelta() > 0 ) {
                        pickedScreenPos.z += ( 1 - pickedScreenPos.z ) / 10;
                    }
                    else {
                        pickedScreenPos.z = ( 10 * pickedScreenPos.z - 1 ) / 9;
                    }
                    break;
            }

            if ( picked != null ) {
                DisplaySystem.getDisplaySystem().getWorldCoordinates( mousePosition, pickedScreenPos.z, anchor );
                myNode.getLocalTranslation().set( anchor.subtractLocal( pickedWorldOffset ) );
                worldJoint.setAnchor( myNode.getLocalTranslation() );
                worldJoint.attach( myNode );
            }
        }
    }

    private class PhysicsPickAction extends InputAction {
        public void performAction( InputActionEvent evt ) {
            final ContactInfo info = (ContactInfo) evt.getTriggerData();
            PhysicsCollisionGeometry other = info.getGeometry1() == physicsRay ? info.getGeometry2() : info.getGeometry1();
            if ( !other.getPhysicsNode().isStatic() && info.getPenetrationDepth() < minPickDistance )
            {
                minPickDistance = info.getPenetrationDepth();
                nearestPickedGeom = other;
            }
        }
    }
}

/*
 * $log$
 */

