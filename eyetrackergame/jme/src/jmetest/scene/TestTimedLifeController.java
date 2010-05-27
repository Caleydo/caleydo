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
package jmetest.scene;

import java.util.logging.Logger;

import com.jmex.game.StandardGame;
import com.jmex.game.state.BasicGameState;
import com.jmex.game.state.GameState;
import com.jmex.game.state.GameStateManager;
import com.jmex.scene.TimedLifeController;

/**
 * @author Matthew D. Hicks
 */
public class TestTimedLifeController extends TimedLifeController {
    private static final Logger logger = Logger
            .getLogger(TestTimedLifeController.class.getName());
    
	private static final long serialVersionUID = 1L;
	
	private GameState state;

	public TestTimedLifeController(GameState state, float lifeInSeconds) {
		super(lifeInSeconds);
		this.state = state;
	}

	public void updatePercentage(float percentComplete) {
		logger.info("I'm this much complete: " + percentComplete);
		if (percentComplete == 1.0f) {
			logger.info("Guess I'm done!");
			GameStateManager.getInstance().detachChild(state);
			state.setActive(false);
		}
	}
	
	public static void main(String[] args) throws Exception {
		StandardGame game = new StandardGame("TestTimedLifeGameState");
		game.start();
		
		BasicGameState timedState = new BasicGameState("TimedLife");
		TestTimedLifeController controller = new TestTimedLifeController(timedState, 10.0f);
		timedState.getRootNode().addController(controller);
		GameStateManager.getInstance().attachChild(timedState);
		timedState.setActive(true);
	}
}
