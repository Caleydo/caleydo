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
// $Id: BaseGame.java 4131 2009-03-19 20:15:28Z blaine.dev $
package com.jme.app;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.input.InputSystem;
import com.jme.util.ThrowableHandler;
import com.jme.system.GameSettings;
import com.jme.system.PropertiesGameSettings;

/**
 * The simplest possible implementation of a game loop.
 * <p>
 * This class defines a pure high speed game loop that runs as fast as CPU/GPU
 * will allow. No handling of variable frame rates is included and, as a
 * result, this class is unsuitable for most production applications; it is
 * useful as a base for applications which require more specialised behaviour
 * as it includes basic game configuration code.
 * 
 * @author Mark Powell, Eric Woroshow
 * @version $Revision: 4131 $, $Date: 2009-03-19 21:15:28 +0100 (Do, 19 Mär 2009) $
 */
public abstract class BaseGame extends AbstractGame {
    private static final Logger logger = Logger.getLogger(BaseGame.class
            .getName());
	protected ThrowableHandler throwableHandler;

    /**
     * The simplest main game loop possible: render and update as fast as
     * possible.
     */
    public final void start() {
        logger.info("Application started.");
        try {
            getAttributes();

            if (!finished) {
                initSystem();

                assertDisplayCreated();

                initGame();

                // main loop
                while (!finished && !display.isClosing()) {
                    // handle input events prior to updating the scene
                    // - some applications may want to put this into update of
                    // the game state
                    InputSystem.update();

                    // update game state, do not use interpolation parameter
                    update(-1.0f);

                    // render, do not use interpolation parameter
                    render(-1.0f);

                    // swap buffers
                    display.getRenderer().displayBackBuffer();

                    Thread.yield();
                }
            }
        } catch (Throwable t) {
            logger.logp(Level.SEVERE, this.getClass().toString(), "start()", "Exception in game loop", t);
            if (throwableHandler != null) {
				throwableHandler.handle(t);
			}
        }

        cleanup();
        logger.info( "Application ending.");

        if (display != null)
            display.reset();
        quit();
    }

    /**
     * Closes the display
     * 
     * @see AbstractGame#quit()
     */
    protected void quit() {
        if (display != null)
            display.close();
    }

    /**
     * Get the exception handler if one hs been set.
     * 
     * @return the exception handler, or {@code null} if not set.
     */
	protected ThrowableHandler getThrowableHandler() {
		return throwableHandler;
	}

	/**
	 *
	 * @param throwableHandler
	 */
	protected void setThrowableHandler(ThrowableHandler throwableHandler) {
		this.throwableHandler = throwableHandler;
	}

    /**
     * @param interpolation
     *            unused in this implementation
     * @see AbstractGame#update(float interpolation)
     */
    protected abstract void update(float interpolation);

    /**
     * @param interpolation
     *            unused in this implementation
     * @see AbstractGame#render(float interpolation)
     */
    protected abstract void render(float interpolation);

    /**
     * @see AbstractGame#initSystem()
     */
    protected abstract void initSystem();

    /**
     * @see AbstractGame#initGame()
     */
    protected abstract void initGame();

    /**
     * @see AbstractGame#reinit()
     */
    protected abstract void reinit();

    /**
     * @see AbstractGame#cleanup()
     */
    protected abstract void cleanup();

    /**
     * @see AbstractGame#getNewSettings()
     */
    protected GameSettings getNewSettings() {
        return new BaseGameSettings();
    }

    /**
     * A PropertiesGameSettings which defaults Fullscreen to TRUE.
     */
    static class BaseGameSettings extends PropertiesGameSettings {
        static {
            // This is how you programmatically override the DEFAULT_*
            // settings of GameSettings.
            // You can also make declarative overrides by using
            // "game-defaults.properties" in a CLASSPATH root directory (or
            // use the 2-param PropertiesGameSettings constructor for any name).
            // (This is all very different from the user-specific
            // "properties.cfg"... or whatever file is specified below...,
            // which is read from the current directory and is session-specific).
            defaultFullscreen = Boolean.TRUE;
            defaultSettingsWidgetImage = "/jmetest/data/images/Monkey.png";
        }
        /**
         * Populates the GameSettings from the (session-specific) .properties
         * file.
         */
        BaseGameSettings() {
            super("properties.cfg");
            load();
        }
    }
}
