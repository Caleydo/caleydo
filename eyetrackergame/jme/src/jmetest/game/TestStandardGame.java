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
package jmetest.game;


import java.util.concurrent.Callable;

import com.jme.bounding.BoundingSphere;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Box;
import com.jme.util.GameTaskQueueManager;
import com.jmex.editors.swing.settings.GameSettingsPanel;
import com.jmex.game.StandardGame;
import com.jmex.game.state.DebugGameState;
import com.jmex.game.state.GameStateManager;

/**
 * <code>TestStandardGame</code> is meant to be an example replacement of
 * <code>jmetest.base.TestSimpleGame</code> using the StandardGame implementation
 * instead of SimpleGame.
 * 
 * @author Matthew D. Hicks
 */
public class TestStandardGame {
	public static void main(String[] args) throws Exception {
	    // Enable statistics gathering
	    System.setProperty("jme.stats", "set");
	    
		// Instantiate StandardGame
		final StandardGame game = new StandardGame("A Simple Test");
		// Show settings screen
		if (GameSettingsPanel.prompt(game.getSettings())) {
			// Start StandardGame, it will block until it has initialized successfully, then return
			game.start();
			
			GameTaskQueueManager.getManager().update(new Callable<Void>() {

				public Void call() throws Exception {
					// Create a DebugGameState - has all the built-in features that SimpleGame provides
					// NOTE: for a distributable game implementation you'll want to use something like
					// BasicGameState instead and provide control features yourself.
					DebugGameState state = new DebugGameState(game);
					// Put our box in it
					Box box = new Box("my box", new Vector3f(0, 0, 0), 2, 2, 2);
				    box.setModelBound(new BoundingSphere());
				    box.updateModelBound();
				    // We had to add the following line because the render thread is already running
				    // Anytime we add content we need to updateRenderState or we get funky effects
				    state.getRootNode().attachChild(box);
				    box.updateRenderState();
					// Add it to the manager
					GameStateManager.getInstance().attachChild(state);
					// Activate the game state
					state.setActive(true);
					
					return null;
				}
			});
		}
	}
}
