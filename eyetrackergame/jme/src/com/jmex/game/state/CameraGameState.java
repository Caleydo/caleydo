/*
 * Copyright (c) 2003-2009 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
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

package com.jmex.game.state;

import com.jme.renderer.Camera;
import com.jme.system.DisplaySystem;

/**
 * <p>
 * A typical game state that initializes a rootNode, camera and a ZBufferState.
 * </p>
 * 
 * <p>
 * In update(float) we call updateGeometricState(0, true) on the rootNode,
 * and in render(float) we draw it.
 * </p>
 * 
 * <p>
 * stateUpdate and stateRender can be filled with custom logic. Much like in 
 * SimpleGame.
 * </p>
 * 
 * <p>
 * The setActive method will trigger the onActivate/onDeactivate methods, giving
 * derived classes an opportunity to perform special actions. E.g. start/stop 
 * playing menu music and such. Beware though; the onActivate method points
 * the renderer to the camera contained by this state, so if you override it
 * you must remember to call super.onActivate().
 * </p>
 * 
 * @author Per Thulin
 */
public class CameraGameState extends CameraGameStateDefaultCamera {
	
	/** The camera of this game state. */
	protected Camera cam;
	
	/**
	 * Inits rootNode, camera and ZBufferState. Also invokes initInput().
	 * 
	 * @param name The name of this GameState.
	 */
	public CameraGameState(String name) {
		super(name);
		
		initCamera();
	}

    /**
	 * Points the renderers camera to the one contained by this state. Derived 
	 * classes can put special actions they want to perform when activated here.
	 */
	protected void onActivate() {
		DisplaySystem.getDisplaySystem().getRenderer().setCamera(cam);
	}

    /**
	 * Gets the camera of this state.
	 * 
	 * @return The camera of this state.
	 */
	public Camera getCamera() {
		return cam;
	}
	
	/**
	 * Sets the camera of this state.
	 */
	public void setCamera(Camera cam) {
		this.cam = cam;
	}
	
	/**
	 * Initializes a standard camera.
	 */
	protected void initCamera() {
		DisplaySystem display = DisplaySystem.getDisplaySystem();
		
		cam = display.getRenderer().createCamera(
				display.getWidth(),
				display.getHeight());
		
		cam.setFrustumPerspective(45.0f,
				(float) display.getWidth() /
				(float) display.getHeight(), 1, 1000);
		
		cam.update();
		
		//display.getRenderer().setCamera(cam);
	}
	
}
