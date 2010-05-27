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

import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.app.AbstractGame;
import com.jme.app.BaseGame;
import com.jme.input.KeyInput;
import com.jme.input.MouseInput;
import com.jme.input.joystick.JoystickInput;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.Timer;
import com.jmex.game.state.GameState;
import com.jmex.game.state.GameStateManager;

/**
 * <p>
 * This test shows how to use the game state system. It can not extend
 * SimpleGame because a lot of SimpleGames functions (e.g. camera, rootNode and input)
 * has been delegated down to the individual game states. So this class is
 * basically a stripped down version of SimpleGame, which inits the
 * GameStateManager and launches a MenuState.
 * </p>
 * 
 * <p>
 * It also has a special way to reach the finish method, using a singleton instance
 * and a static exit method.
 * </p>
 * 
 * @author Per Thulin
 */
public class TestGameStateSystem extends BaseGame {
    private static final Logger logger = Logger
            .getLogger(TestGameStateSystem.class.getName());
	
	/** Only used in the static exit method. */
	private static AbstractGame instance;
	
	/** High resolution timer for jME. */
	private Timer timer;
	
	/** Simply an easy way to get at timer.getTimePerFrame(). */
	private float tpf;
	
	/**
	 * This is called every frame in BaseGame.start()
	 * 
	 * @param interpolation unused in this implementation
	 * @see AbstractGame#update(float interpolation)
	 */
	protected final void update(float interpolation) {
		// Recalculate the framerate.
		timer.update();
		tpf = timer.getTimePerFrame();
		
		// Update the current game state.
		GameStateManager.getInstance().update(tpf);
	}
	
	/**
	 * This is called every frame in BaseGame.start(), after update()
	 * 
	 * @param interpolation unused in this implementation
	 * @see AbstractGame#render(float interpolation)
	 */
	protected final void render(float interpolation) {	
		// Clears the previously rendered information.
		display.getRenderer().clearBuffers();
		// Render the current game state.
		GameStateManager.getInstance().render(tpf);
	}
	
	/**
	 * Creates display, sets  up camera, and binds keys.  Called in BaseGame.start() directly after
	 * the dialog box.
	 * 
	 * @see AbstractGame#initSystem()
	 */
	protected final void initSystem() {
		try {
			/** Get a DisplaySystem acording to the renderer selected in the startup box. */
			display = DisplaySystem.getDisplaySystem(settings.getRenderer());
			/** Create a window with the startup box's information. */
			display.createWindow(
					settings.getWidth(),
					settings.getHeight(),
					settings.getDepth(),
					settings.getFrequency(),
					settings.isFullscreen());
			/** Create a camera specific to the DisplaySystem that works with
			 * the display's width and height*/			
		}
		catch (JmeException e) {
			/** If the displaysystem can't be initialized correctly, exit instantly. */
            logger.log(Level.SEVERE, "Could not create displaySystem", e);
			System.exit(1);
		}
		
		/** Get a high resolution timer for FPS updates. */
		timer = Timer.getTimer();
		
	}
	
	/**
	 * Called in BaseGame.start() after initSystem().
	 * 
	 * @see AbstractGame#initGame()
	 */
	protected final void initGame() {		
		instance = this;
		display.setTitle("Test Game State System");
		
		// Creates the GameStateManager. Only needs to be called once.
		GameStateManager.create();
		// Adds a new GameState to the GameStateManager. In order for it to get
		// processed (rendered and updated) it needs to get activated.
		GameState menu = new MenuState("menu");
		menu.setActive(true);
		GameStateManager.getInstance().attachChild(menu);
	}
	
	/**
	 * Empty.
	 * 
	 * @see AbstractGame#reinit()
	 */
	protected void reinit() {
	}
	
	/**
	 * Cleans up the keyboard and game state system.
	 * 
	 * @see AbstractGame#cleanup()
	 */
	protected void cleanup() {
		logger.info("Cleaning up resources.");
		
		// Performs cleanup on all loaded game states.
		GameStateManager.getInstance().cleanup();
		
        KeyInput.destroyIfInitalized();
        MouseInput.destroyIfInitalized();
        JoystickInput.destroyIfInitalized();
	}
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		TestGameStateSystem app = new TestGameStateSystem();
		app.setConfigShowMode(ConfigShowMode.AlwaysShow);
		app.start();
	}
	
	/**
	 * Static method to finish this application.
	 */
	public static void exit() {
		instance.finish();
	}
	
}
