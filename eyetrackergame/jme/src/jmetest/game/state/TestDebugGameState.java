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

import java.util.concurrent.Callable;

import com.jme.math.Vector3f;
import com.jme.scene.shape.Box;
import com.jme.util.GameTaskQueueManager;
import com.jmex.game.StandardGame;
import com.jmex.game.state.DebugGameState;
import com.jmex.game.state.GameStateManager;

/**
 * Though the name seems redundant, the purpose is to test the TestGameState feature.
 * 
 * @author Matthew D. Hicks
 */
public class TestDebugGameState {
    public static void main(String[] args) throws Exception {
        final StandardGame game = new StandardGame("TestGame");	// Create our game
        game.start();	// Start the game thread
        
        GameTaskQueueManager.getManager().update(new Callable<Void>(){

			public Void call() throws Exception {
				DebugGameState gameState = new DebugGameState(game);	// Create our game state
				GameStateManager.getInstance().attachChild(gameState);	// Attach it to the GameStateManager
				gameState.setActive(true);	// Activate it

				Box box = new Box("TestBox", new Vector3f(), 1.0f, 1.0f, 1.0f);		// Create a Box
				gameState.getRootNode().attachChild(box);	// Attach the box to rootNode in DebugGameState
				box.setRandomColors();		// Set random colors on it - it will only be visible if the lights are off though
				box.updateRenderState();	// Update the render state so the colors appear (the game is already running, so this must always be done)

				return null;
			}
	    });
    }
}
