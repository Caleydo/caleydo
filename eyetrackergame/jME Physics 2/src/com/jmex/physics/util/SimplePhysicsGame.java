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

import java.util.logging.Logger;

import com.jme.app.BaseSimpleGame;
import com.jme.input.FirstPersonHandler;
import com.jme.input.InputHandler;
import com.jme.input.KeyInput;
import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;
import com.jme.renderer.Renderer;
import com.jmex.physics.PhysicsDebugger;
import com.jmex.physics.PhysicsSpace;


/**
 * <code>SimplePhysicsGame</code> provides the simplest possible implementation of a
 * main game loop including physics. It's the equivalent to {@link com.jme.app.BaseSimpleGame}, only with a PhysicsSpace
 * added.
 *
 * @author Irrisor
 * @version $Id: SimplePhysicsGame.java,v 1.18 2007/09/22 14:28:39 irrisor Exp $
 * @see com.jme.app.BaseSimpleGame
 */
public abstract class SimplePhysicsGame extends BaseSimpleGame {

    private PhysicsSpace physicsSpace;
    
    protected InputHandler cameraInputHandler;

    protected boolean showPhysics;

    private float physicsSpeed = 1;

    /**
     * @return speed set by {@link #setPhysicsSpeed(float)}
     */
    public float getPhysicsSpeed() {
        return physicsSpeed;
    }

    /**
     * The multiplier for the physics time. Default is 1, which means normal speed. 0 means no physics processing.
     * @param physicsSpeed new speed
     */
    public void setPhysicsSpeed( float physicsSpeed ) {
        this.physicsSpeed = physicsSpeed;
    }

    @Override
    protected void initSystem() {
        super.initSystem();

        /** Create a basic input controller. */
        cameraInputHandler = new FirstPersonHandler( cam, 50, 1 );
        input = new InputHandler();
        input.addToAttachedHandlers( cameraInputHandler );

        setPhysicsSpace( PhysicsSpace.create() );

        input.addAction( new InputAction() {
            public void performAction( InputActionEvent evt ) {
                if ( evt.getTriggerPressed() ) {
                    showPhysics = !showPhysics;
                }
            }
        }, InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_V, InputHandler.AXIS_NONE, false );
    }

    /**
     * @return the physics space for this simple game
     */
    public PhysicsSpace getPhysicsSpace() {
        return physicsSpace;
    }

    /**
     * @param physicsSpace The physics space for this simple game
     */
	protected void setPhysicsSpace(PhysicsSpace physicsSpace) {
		if ( physicsSpace != this.physicsSpace ) {
			if ( this.physicsSpace != null )
	       		this.physicsSpace.delete();
			this.physicsSpace = physicsSpace;
		}
	}

    private boolean firstFrame = true;

    /**
     * Called every frame to update scene information.
     *
     * @param interpolation unused in this implementation
     * @see BaseSimpleGame#update(float interpolation)
     */
    @Override
    protected final void update( float interpolation ) {
        // disable input as we want it to be updated _after_ physics
        // in your application derived from BaseGame you can simply make the call to InputHandler.update later
        // in your game loop instead of this disabling and re-enabling

        super.update( interpolation );

        if ( !pause ) {
            float tpf = this.tpf;
            if ( tpf > 0.2 || Float.isNaN( tpf ) ) {
                Logger.getLogger( PhysicsSpace.LOGGER_NAME ).warning( "Maximum physics update interval is 0.2 seconds - capped." );
                tpf = 0.2f;
            }
            getPhysicsSpace().update( tpf * physicsSpeed );
        }

        input.update( tpf );

        if ( !pause ) {
            /** Call simpleUpdate in any derived classes of SimpleGame. */
            simpleUpdate();

            /** Update controllers/render states/transforms/bounds for rootNode. */
            rootNode.updateGeometricState( tpf, true );
            statNode.updateGeometricState( tpf, true );
        }

        if ( firstFrame )
        {
            // drawing and calculating the first frame usually takes longer than the rest
            // to avoid a rushing simulation we reset the timer
            timer.reset();
            firstFrame = false;
        }
    }

    @Override
    protected void updateInput() {
        // don't input here but after physics update
    }

    /**
     * This is called every frame in BaseGame.start(), after update()
     *
     * @param interpolation unused in this implementation
     * @see com.jme.app.AbstractGame#render(float interpolation)
     */
    @Override
    protected final void render( float interpolation ) {
        super.render( interpolation );

        preRender();

        Renderer r = display.getRenderer();

        /** Draw the rootNode and all its children. */
        r.draw( rootNode );

        /** Call simpleRender() in any derived classes. */
        simpleRender();
        
        /** Draw the stats node to show our stat charts. */
        r.draw( statNode );

        doDebug(r);
    }

    /**
     * 
     */
    protected void preRender() {

    }

    @Override
    protected void doDebug(Renderer r) {
        super.doDebug(r);

        if ( showPhysics ) {
            PhysicsDebugger.drawPhysics( getPhysicsSpace(), r );
        }
    }
}

/*
 * $log$
 */