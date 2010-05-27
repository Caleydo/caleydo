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

package jmetest.game.state;

import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.input.FirstPersonHandler;
import com.jme.input.InputHandler;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jmex.game.state.CameraGameState;
import com.jmex.game.state.GameStateManager;

/**
 * @author Per Thulin
 */
public class IngameState extends CameraGameState {

	private InputHandler input;
	
	public IngameState(String name) {
		super(name);
		
		// Move the camera a bit.
	    cam.setLocation(new Vector3f(0,10,0));
	    cam.update();
	    
	    initInput();
		
	    // Create a Quad.
	    Quad q = new Quad("Quad", 200, 200);
	    q.setModelBound(new BoundingBox());
	    q.updateModelBound();
	    q.setLocalRotation(new Quaternion(new float[] {90*FastMath.DEG_TO_RAD,0,0}));
	    
	    // Apply a texture to it.
	    TextureState ts = 
	    	DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
	    Texture texture =
	    	TextureManager.loadTexture(
                IngameState.class.getClassLoader().getResource(
                "jmetest/data/texture/dirt.jpg"),
                Texture.MinificationFilter.Trilinear,
                Texture.MagnificationFilter.Bilinear);
	    texture.setWrap(Texture.WrapMode.Repeat);
	    ts.setTexture(texture);
	    ts.setEnabled(true);	    
	    q.setRenderState(ts);
	    
	    // Add it to the scene.
	    rootNode.attachChild(q);
	    
	    // Remember to update the rootNode before you get going.
	    rootNode.updateGeometricState(0, true);
	    rootNode.updateRenderState();
	}
	
	/**
	 * Gets called every time the game state manager switches to this game state.
	 * Sets the window title.
	 */
	public void onActivate() {
		DisplaySystem.getDisplaySystem().
			setTitle("Test Game State System - Ingame State");
		super.onActivate();
	}
	
	/**
	 * Gets called from super constructor. Sets up the input handler that let
	 * us walk around using the w,s,a,d keys and mouse.
	 */
	private void initInput() {
	    input = new FirstPersonHandler(cam, 10, 1);
	    
	    // Bind the exit action to the escape key.
	    KeyBindingManager.getKeyBindingManager().set(
	        "exit",
	        KeyInput.KEY_ESCAPE);
	}
	
	protected void stateUpdate(float tpf) {
		input.update(tpf);
		if (KeyBindingManager.getKeyBindingManager().
				isValidCommand("exit", false)) {
			// Here we switch to the menu state which is already loaded
			GameStateManager.getInstance().activateChildNamed("menu");
			// And remove this state, because we don't want to keep it in memory.
			GameStateManager.getInstance().detachChild("ingame");
		}
	}
	
}