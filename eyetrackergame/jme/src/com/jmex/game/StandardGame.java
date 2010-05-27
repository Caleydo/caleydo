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
// $Id: StandardGame.java 4501 2009-07-13 10:42:01Z julien.gouesse $
package com.jmex.game;

import java.awt.Canvas;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import com.jme.app.AbstractGame;
import com.jme.image.Image;
import com.jme.input.InputSystem;
import com.jme.input.MouseInput;
import com.jme.input.joystick.JoystickInput;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.system.DisplaySystem;
import com.jme.system.GameSettings;
import com.jme.system.PreferencesGameSettings;
import com.jme.system.dummy.DummySystemProvider;
import com.jme.system.jogl.JOGLSystemProvider;
import com.jme.util.GameTaskQueue;
import com.jme.util.GameTaskQueueManager;
import com.jme.util.NanoTimer;
import com.jme.util.TextureManager;
import com.jme.util.Timer;
import com.jmex.audio.AudioSystem;
import com.jmex.awt.jogl.JOGLAWTCanvasConstructor;
import com.jmex.awt.lwjgl.LWJGLAWTCanvasConstructor;
import com.jmex.game.state.GameStateManager;

/**
 * A game that implements all of the basic functionality that you will need.
 * <p>
 * This is intended to be the next logical step up from {@link com.jme.app.SimpleGame
 * SimpleGame} and can be utilised in production games.
 * <p>
 * {@code StandardGame} provides the following features to ease game development:
 * 
 * <ul>
 * <li>client/server division without needing any code changes;</li>
 * <li>an alternative settings to replace the PropertiesIO system;</li>
 * <li>built-in (forced) multithreading as the OpenGL thread is managed for you;</li>
 * <li>the ability to inject additional work into the OpenGL thread using a task queue;</li>
 * <li>shadow support;</li>
 * </ul>
 * 
 * as well as re-initialisation of the graphical context (if settings change
 * for example) and everything else a typical game requires.
 * <p>
 * However, even with all of the extras that {@code StandardGame} provides it
 * does not force anything extra on you as the non-necessary items should be
 * put into your {@link com.jmex.game.state.GameState GameState}s and managed
 * there. This process helps to organise the different aspects of your game and
 * get the game process started ASAP to kill the long-standing problem of
 * start-up lag.
 * 
 * @author Matthew D. Hicks
 * @version $Revision: 4501 $, $Date: 2009-07-13 12:42:01 +0200 (Mo, 13 Jul 2009) $
 */
public final class StandardGame extends AbstractGame implements Runnable {
    private static final Logger logger = Logger.getLogger(StandardGame.class
            .getName());
    
    public static final int DISPLAY_WINDOW = 1;
    public static final int DISPLAY_CANVAS = 2;
    
    public static boolean THREAD_FRIENDLY = true;
    public static int DISPLAY_MODE = DISPLAY_WINDOW;
    
    public static enum GameType {
        GRAPHICAL, HEADLESS
    }

    private Thread gameThread;
    private String gameName;
    private GameType type;
    private boolean started;
    private Image[] icons;
    
    private Timer timer;
    private Camera camera;
    private ColorRGBA backgroundColor;
    private UncaughtExceptionHandler exceptionHandler;
    
    private Canvas canvas;

    private Lock updateLock;
    
    public StandardGame(String gameName) {
        this(gameName, GameType.GRAPHICAL, null);
    }

    public StandardGame(String gameName, GameType type) {
        this(gameName, type, null);
    }

    public StandardGame(String gameName, GameType type, GameSettings settings) {
        this(gameName, type, settings, null);
    }

    /**
     * @see AbstractGame#getNewSettings()
     */
    protected GameSettings getNewSettings() {
        boolean newNode = true;
        Preferences userPrefsRoot = Preferences.userRoot();
        try {
            newNode = !userPrefsRoot.nodeExists(gameName);
        } catch (BackingStoreException bse) { }

        return new PreferencesGameSettings(
                userPrefsRoot.node(gameName), newNode,
                "game-defaults.properties");

        /* To persist to a .properties file instead of java.util.prefs,
         * subclass StandardGame with a getNewSettings method like this:
        com.jme.system.PropertiesGameSettings pgs =
                new com.jme.system.PropertiesGameSettings("pgs.properties");
        pgs.load();
        return pgs;
        */
    }

    public StandardGame(String gameName, GameType type, GameSettings settings, UncaughtExceptionHandler exceptionHandler) {
        this.gameName = gameName;
        this.type = type;
        this.settings = settings;
        this.exceptionHandler = exceptionHandler;
        backgroundColor = ColorRGBA.black.clone();

        // if (this.settings == null) this.settings = getNewSettings();
        // To load settings without displaying a Settings widget, enable
        // the preceding if statement, and comment out the following if block.
        if (this.settings == null) {
            //setConfigShowMode(ConfigShowMode.AlwaysShow); // To override dflt.
            getAttributes();
        }

        // Create Lock
        updateLock = new ReentrantLock(true); // Make our lock be fair (first come, first serve)
    }

    public GameType getGameType() {
        return type;
    }

    public void start() {
        gameThread = new Thread(this);
        if (exceptionHandler == null) {
            exceptionHandler = new DefaultUncaughtExceptionHandler(this);
        }
        gameThread.setUncaughtExceptionHandler(exceptionHandler);
        
        // Assign a name to the thread
        gameThread.setName("OpenGL");
        
        gameThread.start();

        // Wait for main game loop before returning
        try {
            while (!isStarted()) {
                Thread.sleep(1);
            }
        } catch (InterruptedException exc) {
            logger.logp(Level.SEVERE, this.getClass().toString(), "start()", "Exception", exc);
        }
    }

    public void run() {
        lock();
        initSystem();
        if (type != GameType.HEADLESS) {
            assertDisplayCreated();
            
            // Default the mouse cursor to off
            MouseInput.get().setCursorVisible(false);
        }
        
        initGame();
        if (type == GameType.GRAPHICAL) {
            timer = Timer.getTimer();
        } else if (type == GameType.HEADLESS) {
            timer = new NanoTimer();
        }

        // Configure frame rate
        int preferredFPS = settings.getFramerate();
        long preferredTicksPerFrame = -1;
        long frameStartTick = -1;
        long frames = 0;
        long frameDurationTicks = -1;
        if (preferredFPS >= 0) {
            preferredTicksPerFrame = Math.round((float)timer.getResolution() / (float)preferredFPS);
        }

        // Main game loop
        float tpf;
        started = true;
        while ((!finished) && (!display.isClosing())) {
            // Fixed framerate Start
            if (preferredTicksPerFrame >= 0) {
                frameStartTick = timer.getTime();
            }

            timer.update();
            tpf = timer.getTimePerFrame();

            if (type == GameType.GRAPHICAL) {
                InputSystem.update();
            }
            update(tpf);
            render(tpf);
            display.getRenderer().displayBackBuffer();

            // Fixed framerate End
            if (preferredTicksPerFrame >= 0) {
                frames++;
                frameDurationTicks = timer.getTime() - frameStartTick;
                while (frameDurationTicks < preferredTicksPerFrame) {
                    long sleepTime = ((preferredTicksPerFrame - frameDurationTicks) * 1000) / timer.getResolution();
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException exc) {
                        logger.log(Level.SEVERE,
                                   "Interrupted while sleeping in fixed-framerate",
                                   exc);
                    }
                    frameDurationTicks = timer.getTime() - frameStartTick;
                }
                if (frames == Long.MAX_VALUE) frames = 0;
            }

            if (THREAD_FRIENDLY) Thread.yield();
        }
        started = false;
        cleanup();
        quit();
    }

    protected void initSystem() {
        if (type == GameType.GRAPHICAL) {

            // Configure Joystick
            if (JoystickInput.getProvider() == null) {
                JoystickInput.setProvider(InputSystem.INPUT_SYSTEM_LWJGL);
            }

            display = DisplaySystem.getDisplaySystem(settings.getRenderer());
            displayMins();
            
            display.setTitle(gameName);
            if( icons != null) {
                display.setIcon( icons);
            }

            if (DISPLAY_MODE == DISPLAY_WINDOW) {
                display.createWindow(settings.getWidth(), settings.getHeight(), settings.getDepth(), settings
                                .getFrequency(), settings.isFullscreen());
            } else if (DISPLAY_MODE == DISPLAY_CANVAS) {
            	// XXX: included to preserve current functionality. Probably
				// want to move this to prefs or the user of StandardGame.
                if(JOGLSystemProvider.SYSTEM_IDENTIFIER.equals(settings.getRenderer()))
                    display.registerCanvasConstructor("AWT", JOGLAWTCanvasConstructor.class);
                else
                    display.registerCanvasConstructor("AWT", LWJGLAWTCanvasConstructor.class);
                canvas = (Canvas)display.createCanvas(settings.getWidth(), settings.getHeight());
            }
            camera = display.getRenderer().createCamera(display.getWidth(), display.getHeight());
            display.getRenderer().setBackgroundColor(backgroundColor);

            // Setup Vertical Sync if enabled
            display.setVSyncEnabled(settings.isVerticalSync());

            // Configure Camera
            cameraPerspective();
            cameraFrame();
            camera.update();
            display.getRenderer().setCamera(camera);

            if ((settings.isMusic()) || (settings.isSFX())) {
                initSound();
            }
        } else {
        	display = DisplaySystem.getDisplaySystem(DummySystemProvider.DUMMY_SYSTEM_IDENTIFIER);
        }
    }

    /**
     * The java.awt.Canvas if DISPLAY_CANVAS is the DISPLAY_MODE
     * 
     * @return
     *      Canvas
     */
    public Canvas getCanvas() {
        return canvas;
    }
    
    protected void initSound() {
        AudioSystem.getSystem().getEar().trackOrientation(camera);
        AudioSystem.getSystem().getEar().trackPosition(camera);
    }

    private void displayMins() {
        display.setMinDepthBits(settings.getDepthBits());
        display.setMinStencilBits(settings.getStencilBits());
        display.setMinAlphaBits(settings.getAlphaBits());
        display.setMinSamples(settings.getSamples());
    }

    private void cameraPerspective() {
        camera.setFrustumPerspective(45.0f, (float)display.getWidth() / (float)display.getHeight(), 1.0f, 1000.0f);
        camera.setParallelProjection(false);
        camera.update();
    }

    private void cameraFrame() {
        Vector3f loc = new Vector3f(0.0f, 0.0f, 25.0f);
        Vector3f left = new Vector3f(-1.0f, 0.0f, 0.0f);
        Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
        Vector3f dir = new Vector3f(0.0f, 0.0f, -1.0f);
        camera.setFrame(loc, left, up, dir);
    }
    
    public void resetCamera() {
        cameraFrame();
    }

    protected void initGame() {
        // Create the GameStateManager
        GameStateManager.create();
    }

    protected void update(float interpolation) {
        // Open the lock up for just a brief second
        unlock();
        lock();

        // Execute updateQueue item
        GameTaskQueueManager.getManager().getQueue(GameTaskQueue.UPDATE).execute();

        // Update the GameStates
        GameStateManager.getInstance().update(interpolation);

        if (type == GameType.GRAPHICAL) {

            // Update music/sound
            if ((settings.isMusic()) || (settings.isSFX())) {
                AudioSystem.getSystem().update();
            }
        }
    }

    protected void render(float interpolation) {
        display.getRenderer().clearBuffers();

        // Execute renderQueue item
        GameTaskQueueManager.getManager().getQueue(GameTaskQueue.RENDER).execute();

        // Render the GameStates
        GameStateManager.getInstance().render(interpolation);
    }
    
    public void reinit() {
        reinitAudio();
        reinitVideo();
    }
    
    public void reinitAudio() {
        if (AudioSystem.isCreated()) {
            AudioSystem.getSystem().cleanup();
        }
    }

	public void reinitVideo() {
		GameTaskQueueManager.getManager().update(new Callable<Object>() {
			public Object call() throws Exception {
				displayMins();

				display.recreateWindow(settings.getWidth(), settings
						.getHeight(), settings.getDepth(), settings
						.getFrequency(), settings.isFullscreen());
				camera = display.getRenderer().createCamera(display.getWidth(),
						display.getHeight());
				display.getRenderer().setBackgroundColor(backgroundColor);
				if ((settings.isMusic()) || (settings.isSFX())) {
					initSound();
				}
				return null;
			}
		});
	}

	public void recreateGraphicalContext() {
		reinit();
	}

    protected void cleanup() {
        GameStateManager.getInstance().cleanup();
        
        DisplaySystem.getDisplaySystem().getRenderer().cleanup();
        TextureManager.doTextureCleanup();
        TextureManager.clearCache();
        
        JoystickInput.destroyIfInitalized();
        if (AudioSystem.isCreated()) {
            AudioSystem.getSystem().cleanup();
        }
    }

    protected void quit() {
        if (display != null) {
            display.reset();
            display.close();
        }
    }

    /**
     * The internally used <code>DisplaySystem</code> for this instance
     * of <code>StandardGame</code>
     * 
     * @return
     *      DisplaySystem
     * 
     * @see DisplaySystem
     */
    public DisplaySystem getDisplay() {
        return display;
    }

    /**
     * The internally used <code>Camera</code> for this instance of
     * <code>StandardGame</code>.
     * 
     * @return
     *      Camera
     *      
     * @see Camera
     */
    public Camera getCamera() {
        return camera;
    }

    /**
     * The <code>GameSettings</code> implementation being utilized in
     * this instance of <code>StandardGame</code>.
     * 
     * @return
     *      GameSettings
     *      
     * @see GameSettings
     */
    public GameSettings getSettings() {
        return settings;
    }

    /**
     * Override the background color defined for this game. The reinit() method
     * must be invoked if the game is currently running before this will take effect.
     * 
     * @param backgroundColor
     */
    public void setBackgroundColor(ColorRGBA backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    /**
     * Gracefully shutdown the main game loop thread. This is a synonym
     * for the finish() method but just sounds better.
     * 
     * @see #finish()
     */
    public void shutdown() {
        finish();
    }

    /**
     * Will return true if within the main game loop. This is particularly
     * useful to determine if the game has finished the initialization but
     * will also return false if the game has been terminated.
     * 
     * @return
     *      boolean
     */
    public boolean isStarted() {
        return started;
    }

    /**
     * Specify the UncaughtExceptionHandler for circumstances where an exception in the
     * OpenGL thread is not captured properly.
     * 
     * @param exceptionHandler
     */
    public void setUncaughtExceptionHandler(UncaughtExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        if (gameThread != null) {
            gameThread.setUncaughtExceptionHandler(this.exceptionHandler);
        }
    }

    /**
     * Causes the current thread to wait for an update to occur in the OpenGL thread.
     * This can be beneficial if there is work that has to be done in the OpenGL thread
     * that needs to be completed before continuing in another thread.
     * 
     * You can chain invocations of this together in order to wait for multiple updates.
     * 
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public void delayForUpdate() throws InterruptedException, ExecutionException {
        Future<Object> f = GameTaskQueueManager.getManager().update(new Callable<Object>() {
            public Object call() throws Exception {
                return null;
            }
        });
        f.get();
    }

    /**
     * Convenience method to let you know if the thread you're in is the OpenGL thread
     * 
     * @return
     *      true if, and only if, the current thread is the OpenGL thread
     */
    public boolean inGLThread() {
        if (Thread.currentThread() == gameThread) {
            return true;
        }
        return false;
    }

    /**
     * Convenience method that will make sure <code>callable</code> is executed in the
     * OpenGL thread. If it is already in the OpenGL thread when this method is invoked
     * it will be executed and returned immediately. Otherwise, it will be put into the
     * GameTaskQueue and executed in the next update. This is a blocking method and will
     * wait for the successful return of <code>callable</code> before returning.
     * 
     * @param <T>
     * @param callable
     * @return result of callable.get()
     * @throws Exception
     */
    public <T> T executeInGL(Callable<T> callable) throws Exception {
        if (inGLThread()) {
            return callable.call();
        }
        Future<T> future = GameTaskQueueManager.getManager().update(callable);
        return future.get();
    }

    /**
     * Will wait for a lock at the beginning of the OpenGL update method. Once this method returns the
     * OpenGL thread is blocked until the lock is released (via unlock()). If another thread currently
     * has a lock or it is currently in the process of an update the calling thread will be blocked until
     * the lock is successfully established.
     */
    public void lock() {
        updateLock.lock();
    }

    /**
     * Used in conjunction with lock() in order to release a previously assigned lock on the OpenGL thread.
     * This <b>MUST</b> be executed within the same thread that called lock() in the first place or the lock
     * will not be released.
     */
    public void unlock() {
        updateLock.unlock();
    }

    public void setIcons( Image[] icons) {
        this.icons = icons;
    }
}

class DefaultUncaughtExceptionHandler implements UncaughtExceptionHandler {
    private static final Logger logger = Logger
            .getLogger(DefaultUncaughtExceptionHandler.class.getName());
    
    private StandardGame game;

    public DefaultUncaughtExceptionHandler(StandardGame game) {
        this.game = game;
    }

    public void uncaughtException(Thread t, Throwable e) {
        logger.log(Level.SEVERE, "Main game loop broken by uncaught exception", e);
        game.shutdown();
        game.cleanup();
        game.quit();
    }
}
