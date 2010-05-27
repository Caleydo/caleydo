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
// $Id: FixedLogicrateGame.java 4131 2009-03-19 20:15:28Z blaine.dev $
package com.jme.app;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.input.InputSystem;
import com.jme.util.Timer;

/**
 * A game that attempts to run at a fixed logic rate.
 * <p>
 * The main loop makes every effort to update at the specified rate. The goal
 * is to keep a consistent game-play speed regardless of the frame rate
 * achieved by the visuals (i.e. the game will render as fast as the hardware
 * permits, while running it's logic at a fixed rate). This gives tighter
 * control on how the game state is processed, including such things as AI and
 * physics.
 * <p>
 * The concept behind this is forcing every game logic tick to represent a
 * fixed amount of real-time. For example, if the logic is updated at a rate
 * of 15 times per second, and we have a person moving at 30 pixels per second,
 * each update the person should move 2 pixels. To compensate for the
 * non-constant frame rate, we smooth the visuals using interpolation. So, if
 * the scene is rendered twice without the game logic being updated, we do not
 * render the same thing twice.
 * <p>
 * Using a fixed time-step model has a number of benefits: game logic is
 * simplified as there is no longer any need to add time deltas to achieve
 * frame rate independence. There is also a gain in efficiency as the logic can
 * be run at a lower frequency than the rendering, meaning that the logic may
 * be updated only once every second game - a net save in time. Finally,
 * because the exact same sequence of game logic code is executed every time,
 * the game becomes deterministic (that is to say, it will run the exact same
 * way every time).
 * <p>
 * Further extension of this class could be used to integrate both a fixed logic
 * rate and a fixed frame rate.
 * 
 * @author Eric Woroshow
 * @version $Revision: 4131 $, $Date: 2009-03-19 21:15:28 +0100 (Do, 19 Mär 2009) $
 */
public abstract class FixedLogicrateGame extends AbstractGame {
    private static final Logger logger = Logger
            .getLogger(FixedLogicrateGame.class.getName());

    private static final int MAX_LOOPS = 50;

    //Logic-rate managing variables
    private Timer timer;

    private int logicTPS;

    private long tickTime;

    private long time0, time1;

    private int loops;

    /**
     * <code>setLogicTicksPerSecond</code> sets the number of logic times per
     * second the game should update the logic. This should not be called prior
     * to the application being <code>start()</code> -ed.
     * 
     * @param tps
     *            the desired logic rate in ticks per second
     */
    public void setLogicTicksPerSecond(int tps) {
        if (tps < 0) {
                throw new IllegalArgumentException(
                        "Ticks per second cannot be less than zero.");
        }

        logicTPS = tps;
        tickTime = timer.getResolution() / logicTPS;
    }

    /**
     * Ticks logic at a fixed rate while rendering as fast as hardware permits.
     */
    public final void start() {
        logger.info("Application started.");
        try {
            getAttributes();

            initSystem();

            assertDisplayCreated();

            timer = Timer.getTimer();
            setLogicTicksPerSecond(60); //default to 60 tps

            initGame();

            //main loop
            while (!finished && !display.isClosing()) {
                time1 = timer.getTime();
                loops = 0;

                while ((time1 - time0) > tickTime && loops < MAX_LOOPS) {
                    //handle input events prior to updating the scene
                    // - some applications may want to put this into update of the game state
                    InputSystem.update();

                    //update game state, do not use interpolation parameter
                    update(-1.0f);
                    time0 += tickTime;
                    loops++;
                }

                //If the game logic takes far too long, discard the pending
                // time
                if ((time1 - time0) > tickTime) time0 = time1 - tickTime;

                float percentWithinTick = Math.min(1.0f,
                        (float) (time1 - time0) / tickTime);
                //render scene with interpolation value
                render(percentWithinTick);

                //swap buffers
                display.getRenderer().displayBackBuffer();

                Thread.yield();
            }
        } catch (Throwable t) {
            logger.logp(Level.SEVERE, this.getClass().toString(), "start()", "Exception in game loop", t);
        } finally {
            cleanup();
        }
        logger.info("Application ending.");

        display.reset();
        quit();
    }

    /**
     * Quits the program abruptly using <code>System.exit</code>.
     * 
     * @see AbstractGame#quit()
     */
    protected void quit() {
        if (display != null) {
            display.close();
        }
        System.exit(0);
    }

    /**
     * @param interpolation
     *            unused in this implementation
     * @see AbstractGame#update(float interpolation)
     */
    protected abstract void update(float interpolation);

    /**
     * Renders the scene. Under no circumstances should the render method alter
     * anything that could directly or indirectly modify the game logic.
     * 
     * @param percentWithinTick
     *            decimal value representing the position between update ticks
     * @see AbstractGame#render(float interpolation)
     */
    protected abstract void render(float percentWithinTick);

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
}

