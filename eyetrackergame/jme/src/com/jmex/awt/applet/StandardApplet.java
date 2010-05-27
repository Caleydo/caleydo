package com.jmex.awt.applet;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.PixelFormat;

import com.jme.image.Image;
import com.jme.input.InputSystem;
import com.jme.input.MouseInput;
import com.jme.input.joystick.JoystickInput;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.system.lwjgl.LWJGLDisplaySystem;
import com.jme.util.GameTaskQueue;
import com.jme.util.GameTaskQueueManager;
import com.jme.util.NanoTimer;
import com.jme.util.TextureManager;
import com.jme.util.ThrowableHandler;
import com.jme.util.Timer;
import com.jmex.audio.AudioSystem;
import com.jmex.game.StandardGame;
import com.jmex.game.state.GameStateManager;

/**
 * LWJGL2 Applet imlpementation similar to {@link StandardGame}
 */
public abstract class StandardApplet extends Applet {
    private static final long serialVersionUID = 6894421316159346138L;
    private static final Logger logger = Logger.getLogger(StandardApplet.class
	    .getName());
    
    public static enum GameType {
        GRAPHICAL, HEADLESS
    }
    
    private final static String JME_VERSION_TAG = "jME2 Main Trunk @ Google Code";

    /** The awt canvas to draw to */
    protected Canvas displayParent;
    /** The thread with the game logic: initialization, updating, rendering */
    protected Thread gameThread;
    /** Flag for running the system. */
    private boolean started = false;
    protected boolean finished = false;
    /** The DisplaySystem for this applet*/
    protected DisplaySystem display;
    
    /** Settings that should be set by their accesors before
     *  init() is called in the subclassed applet*/
    /** The name of this game */
    private String gameName = "Standard Applet";
    /** The type of this default Graphical*/
    private GameType type = GameType.GRAPHICAL;
    /** The preferred framerate of this game deafault 30*/
    private int preferredFPS = 30;
    /** The boolean that determines if the game is VSyncEnabled*/
    private boolean vSyncEnabled = false;
    /** The icon for this game only really seen for an applet
     *  when in fullscreen and is exited slowly. So not really
     *  worth it.
     */
    private Image[] icons;
    /** The background color*/
    private ColorRGBA backgroundColor = ColorRGBA.black.clone();
    /** Alpha bits to use for the renderer. */
    private int alphaBits = 0;
    /** Depth bits to use for the renderer.   */
    private int depthBits = 8;
    /*** Stencil bits to use for the renderer.   */
    private int stencilBits = 0;
    /** Number of samples to use for the multisample buffer. */
    private int samples = 0;
    
    /** The timer that controls the games framerate*/
    private Timer timer;

    /** The camera used in this game*/
    private Camera camera;
    /** If there is an exception during the game 
     * loop this is used to handle it*/
    protected ThrowableHandler throwableHandler;
    /** The lock used for concurrency. */
    private Lock updateLock;

    public String getVersion() {
    	return JME_VERSION_TAG;
    }

    public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	public String getGameName() {
		return gameName;
	}
	
	public void setType(GameType type) {
		this.type = type;
	}

	public GameType getType() {
		return type;
	}
	
	public void setFramerate(int preferredFPS) {
		this.preferredFPS = preferredFPS;
	}

	public int getFramerate() {
		return preferredFPS;
	}
	
	public void setVSyncEnabled(boolean vSyncEnabled) {
		this.vSyncEnabled = vSyncEnabled;
	}
	
	public boolean getVSyncEnabled() {
		return vSyncEnabled;
	}
	
	public void setIcons( Image[] icons) {
        this.icons = icons;
    }
	
    public void setBackgroundColor(ColorRGBA backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
    
    public int getAlphaBits() {
		return alphaBits;
	}

	public void setAlphaBits(int alphaBits) {
		this.alphaBits = alphaBits;
	}

	public int getDepthBits() {
		return depthBits;
	}

	public void setDepthBits(int depthBits) {
		this.depthBits = depthBits;
	}

	public int getStencilBits() {
		return stencilBits;
	}

	public void setStencilBits(int stencilBits) {
		this.stencilBits = stencilBits;
	}

	public int getSamples() {
		return samples;
	}

	public void setSamples(int samples) {
		this.samples = samples;
	}
	
	public void toggleFullscreen() {
		try {
			int width, height;
			if (Display.isFullscreen()) {
				Display.setFullscreen(false);
				width = this.getWidth();
				height = this.getHeight();
			        logger.info("Regular Width: " + width + " Height: " + height);
			}
			else {
				Display.setFullscreen(true);
				Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
				width = dimension.width;
				height = dimension.height;
				logger.info("Fullscreen Width: " + width + " Height: " + height);
			}
			display.getRenderer().reinit(
					width,
					height);
				camera.setFrustumPerspective(45.0f, (float) width
					/ (float) height, 1, 1000);
			DisplaySystem.getDisplaySystem().setWidth(width);
			DisplaySystem.getDisplaySystem().setHeight(height);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}
    
    /**
     * The internally used <code>DisplaySystem</code> for this instance
     * of <code>StandardApplet</code>
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
     * <code>StandardApplet</code>.
     * 
     * @return
     *      Camera
     *      
     * @see Camera
     */
    public Camera getCamera() {
        return camera;
    }
    
    public Canvas getCanvas() {
        return displayParent;
    }
       
    /**
     * Initializes the awt canvas to later render the jme scene to via
     * Display.setParent()
     */
    public void init() {
    	logger.info("Applet initialized.");
    	setLayout(new BorderLayout());
    	try {
    		displayParent = new Canvas();
    		displayParent.setSize(getWidth(), getHeight());
    		add(displayParent);
    		displayParent.setFocusable(true);
    		displayParent.requestFocus();
    		displayParent.setIgnoreRepaint(true);
    		setVisible(true);
    	} catch (Exception e) {
    		System.err.println(e);
    		throw new RuntimeException("Unable to create display");
    	}
    	
        // Create Lock
        updateLock = new ReentrantLock(true); // Make our lock be fair (first come, first serve)
    }

    /**
     * Creates the game thread, which first initializes the display, then runs
     * the game updates and renders.
     */
    public void start() {
    	logger.info("Applet started.");
    	gameThread = new Thread() {
    		public void run() {
    			try {
    				logger.info("display_parent.isDisplayable() = "
    						+ displayParent.isDisplayable());
    				Display.setParent(displayParent);

    				Display.create(new PixelFormat(alphaBits, depthBits, stencilBits, samples));
    				// initGL();
    			} catch (LWJGLException e) {
    				e.printStackTrace();
    			}

    			gameLoop();
    			cleanup();    			
    			logger.info("Applet ending.");
    			quit();
    		}
    	};
    	
        // Assign a name to the thread
        gameThread.setName("OpenGL");
        
        // Start the thread
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

    public void destroy() {
    	super.destroy();
		logger.info("Clear up");
    }

    public void gameLoop() {
	try {
	    if (!finished) {
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
	        long preferredTicksPerFrame = -1;
	        long frameStartTick = -1;
	        long frames = 0;
	        long frameDurationTicks = -1;
	        if (preferredFPS >= 0) {
	            preferredTicksPerFrame = Math.round((float)timer.getResolution() / (float)preferredFPS);
	        }
	        
	    	// main loop
	        float tpf;
	        started = true;
	    	while (!finished && !display.isClosing()) {
	            // Fixed framerate Start
	            if (preferredTicksPerFrame >= 0) {
	                frameStartTick = timer.getTime();
	            }
	            
	            timer.update();
	            tpf = timer.getTimePerFrame();
	            
	    		// handle input events prior to updating the scene
	    		// - some applications may want to put this into update of
	    		// the game state
	            if (type == GameType.GRAPHICAL) {
	                InputSystem.update();
	            }
	            
	    		// update game state
	    		update(tpf);
	    		// render game state
	    		render(tpf);
	    		// Swap buffers, process messages, handle input
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
	                // TODO: Is this really necessary will anybody be playing for 58494241 years at 5000 frames/sec?
	                if (frames == Long.MAX_VALUE) frames = 0;
	            }
	    		
	    		Thread.yield();
	    	}
	        started = false;
	    }
	} catch (Throwable t) {
	    logger.logp(Level.SEVERE, this.getClass().toString(), "start()",
		    "Exception in game loop", t);
	    if (throwableHandler != null) {
		throwableHandler.handle(t);
	    }
	}

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
     * <code>assertDisplayCreated</code> determines if the display system was
     * successfully created before use.
     * 
     * @throws JmeException
     *             if the display system was not successfully created
     */
    protected void assertDisplayCreated() throws JmeException {
    	if (display == null) {
    		logger.severe("Display system is null.");
    		throw new JmeException("Window must be created during"
    				+ " initialization.");
    	}
	}
    
    protected void initSystem() {
        if (type == GameType.GRAPHICAL) {

            // Configure Joystick
            if (JoystickInput.getProvider() == null) {
                JoystickInput.setProvider(InputSystem.INPUT_SYSTEM_LWJGL);
            }

	    	display = DisplaySystem.getDisplaySystem();

            
            displayMins();
            
            display.setTitle(gameName);
            if( icons != null) {
                display.setIcon( icons);
            }
            
	    	((LWJGLDisplaySystem) display).initForApplet(getWidth(), getHeight());

            camera = display.getRenderer().createCamera(getWidth(), getHeight());
            
            display.getRenderer().setBackgroundColor(backgroundColor);

            // Setup Vertical Sync if enabled
            display.setVSyncEnabled(vSyncEnabled);

            // Configure Camera
            cameraPerspective();
            cameraFrame();
            camera.update();
            display.getRenderer().setCamera(camera);
            
            initSound();
        } else {
        	display = DisplaySystem.getDisplaySystem();
        }
        
    	this.addComponentListener(new AppletResizeListener(this));
    }
    
    protected void initSound() {
        AudioSystem.getSystem().getEar().trackOrientation(camera);
        AudioSystem.getSystem().getEar().trackPosition(camera);
    }

    private void displayMins() {
        display.setMinDepthBits(depthBits);
        display.setMinStencilBits(stencilBits);
        display.setMinAlphaBits(alphaBits);
        display.setMinSamples(samples);
    }

    private void cameraPerspective() {
        camera.setFrustumPerspective(45.0f, (float)getWidth() / (float)getHeight(), 1.0f, 1000.0f);
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

    /**
     * @param interpolationc 
     *            is the number of seconds since the last frame
     */
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
            AudioSystem.getSystem().update();
        }
    }

    /**
     * @param interpolation
     *            is the number of seconds since the last frame
     */
    protected void render(float interpolation) {
        display.getRenderer().clearBuffers();

        // Execute renderQueue item
        GameTaskQueueManager.getManager().getQueue(GameTaskQueue.RENDER).execute();

        // Render the GameStates
        GameStateManager.getInstance().render(interpolation);
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
		remove(displayParent);
    }
       
	/** Halts execution (cleanup methods are called afterwards) */
    public void finish() {
    	finished = true;
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
}