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

package com.jmetest.physics.vehicle;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.image.Texture.MagnificationFilter;
import com.jme.image.Texture.MinificationFilter;
import com.jme.image.Texture.WrapMode;
import com.jme.input.ChaseCamera;
import com.jme.input.InputHandler;
import com.jme.input.KeyInput;
import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;
import com.jme.input.action.InputActionInterface;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jme.scene.shape.Box;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.jmex.physics.StaticPhysicsNode;
import com.jmex.physics.material.Material;
import com.jmex.physics.util.SimplePhysicsGame;

/**
 * This class tests the advanced vehicle, tuned by Erick B Passos.
 * @author Erick B Passos 
 */
public class TestAdvancedVehicle extends SimplePhysicsGame {

    // Our car
    protected Car car;
    private InputAction resetAction;

    protected void simpleInitGame() {
        // Lets tune our ODEJava first
        tunePhysics();

        // Lets put a simple physics floor so our car can race over it
        createFloor();

        // Lets create our car
        createCar();

        // We need to DRIVE!
        initInput();

        // Just putting some instructions on screen.
        createText();

        // Lets be nice and update our scene graph properties.
        rootNode.updateRenderState();

        resetAction.performAction( null );
    }

    /**
     * Just instantiates our Car and attaches it to the scene graph.
     */
    private void createCar() {
        car = new Car( getPhysicsSpace() );
        rootNode.attachChild( car );
    }

    /**
     * Creates a jME ChaseCamera for the car chassis and adds custom input actions to control it.
     */
    private void initInput() {
        // Simple chase camera
        input.removeFromAttachedHandlers( cameraInputHandler );
        cameraInputHandler = new ChaseCamera( cam, car.getChassis().getChild( 0 ) );
        input.addToAttachedHandlers( cameraInputHandler );

        // Attaching the custom input actions (and its respective keys) to the carInput handler.
        input.addAction( new AccelAction( car, 1 ),
                InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_UP, InputHandler.AXIS_NONE, false );
        input.addAction( new AccelAction( car, -1 ),
                InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_DOWN, InputHandler.AXIS_NONE, false );
        input.addAction( new SteerAction( car, -1 ),
                InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_LEFT, InputHandler.AXIS_NONE, false );
        input.addAction( new SteerAction( car, 1 ),
                InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_RIGHT, InputHandler.AXIS_NONE, false );

        resetAction = new ResetAction();
        input.addAction( resetAction, InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_S, InputHandler.AXIS_NONE, false );
    }

    /**
     * Simple instructions on screen.
     */
    private void createText() {
        Text label = Text.createDefaultTextLabel( "instructions",
                "Use arrows to drive. Use the mouse wheel to control the chase camera. S to reset the car." );
        label.setLocalTranslation( 0, 20, 0 );
        statNode.attachChild( label );
        statNode.updateRenderState();
    }

    private void tunePhysics() {
        getPhysicsSpace().setAutoRestThreshold( 0.2f );
        // Otherwise it would be VERY slow.
        setPhysicsSpeed( 4 );
    }

    /**
     * Creates the physics floor and glues a Texture on it.
     * The floor is a simple, horizontally very large, box, from which we create a StaticPhysicsNode since it
     * doesn't need to move.
     */
    private void createFloor() {
        Spatial floorVisual = new Box( "floor", new Vector3f(), 10000, 0.1f, 10000 );
        floorVisual.setModelBound( new BoundingBox() );
        floorVisual.updateModelBound();
        StaticPhysicsNode floor = getPhysicsSpace().createStaticNode();
        floor.attachChild( floorVisual );
        floor.generatePhysicsGeometry();
        floor.setMaterial( Material.CONCRETE );
        floor.setLocalTranslation( new Vector3f( 0, -0.1f, 0 ) );
        rootNode.attachChild( floor );

        // Glueing the texture on the floor.
        final TextureState wallTextureState = display.getRenderer().createTextureState();
        wallTextureState.setTexture( TextureManager.loadTexture( jmetest.TestChooser.class
                .getResource( "data/texture/dirt.jpg" ), MinificationFilter.NearestNeighborLinearMipMap, MagnificationFilter.NearestNeighbor ) );
        wallTextureState.getTexture().setScale( new Vector3f( 256, 256, 1 ) );
        wallTextureState.getTexture().setWrap( WrapMode.Repeat );
        floorVisual.setRenderState( wallTextureState );
    }

    public static void main( String[] args ) {
        Logger.getLogger( "" ).setLevel( Level.WARNING );
        new TestAdvancedVehicle().start();
    }

    /**
     * Simple input action for steering the wheel.
     */
    private class SteerAction implements InputActionInterface {
        Car car;

        int direction;

        public SteerAction( Car car, int direction ) {
            this.car = car;
            this.direction = direction;
        }

        public void performAction( final InputActionEvent e ) {
            // If the key is down (left or right) lets steer
            if ( e.getTriggerPressed() ) {
                car.steer( direction );
            }
            // If it's up, lets unsteer
            else {
                car.unsteer();
            }
        }

    }

    /**
     * Simple input action for accelerating and braking the car.
     */
    private class AccelAction implements InputActionInterface {
        Car car;

        int direction;

        public AccelAction( final Car car, final int direction ) {
            this.car = car;
            this.direction = direction;
        }

        public void performAction( final InputActionEvent e ) {
            // If the key has just been pressed, lets accelerate in the desired direction
            if ( e.getTriggerPressed() ) {
                car.accelerate( direction );
            }
            // Otherwise, lets release the wheels.
            else {
                car.releaseAccel();
            }
        }
    }

    private class ResetAction extends InputAction {
        public void performAction( InputActionEvent evt ) {
            car.setPosition( 0, 50, 0 );
        }
    }
}

/*
 * $Log: TestAdvancedVehicle.java,v $
 * Revision 1.3  2007/10/18 14:41:03  irrisor
 * allow collision detection for joint nodes
 *
 * Revision 1.2  2007/10/06 18:41:16  irrisor
 * reset action for advanced vehicle test
 *
 * Revision 1.1  2007/10/05 20:01:43  irrisor
 * Contribution by Erick B Passos: advanced vehicle
 * Author: Erick Passos
 * 
 */

