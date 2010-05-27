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

package com.jme.system;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import sun.misc.Service;
import sun.misc.ServiceConfigurationError;

import com.jme.image.Image;
import com.jme.input.joystick.JoystickInput;
import com.jme.math.Ray;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.RenderContext;
import com.jme.renderer.Renderer;
import com.jme.renderer.TextureRenderer;
import com.jme.scene.state.RenderState;
import com.jme.system.canvas.CanvasConstructor;
import com.jme.system.canvas.JMECanvas;
import com.jme.system.dummy.DummySystemProvider;
import com.jme.system.jogl.JOGLSystemProvider;
import com.jme.system.lwjgl.LWJGLSystemProvider;

/**
 * <code>DisplaySystem</code> defines an interface for system creation.
 * Specifically, any implementing class will create a window for rendering. It
 * also should create the appropriate <code>Renderer</code> object that allows
 * the client to render to this window. <p/> Implementing classes should check
 * for the appropriate libraries to insure these libraries are indeed installed
 * on the system. This will allow users to cleanly exit if an improper library
 * was chosen for rendering. <p/> Example usage: <p/> <code>
 * DisplaySystem ds = DisplaySystem.getDisplaySystem();<br>
 * ds.createWindow(640,480,32,60,true);<br>
 * Renderer r = ds.getRenderer();<br>
 * </code>
 * 
 * @author Mark Powell
 * @author Gregg Patton
 * @author Joshua Slack - Optimizations, Headless rendering, RenderContexts, AWT integration
 * @version $Id: DisplaySystem.java 4729 2009-10-22 07:07:29Z andreas.grabner@gmail.com $
 * @see com.jme.renderer.Renderer
 */
public abstract class DisplaySystem {

    private static final Logger LOGGER = Logger.getLogger(DisplaySystem.class.getName());

    /** The display system that has been created. */
    private static volatile SystemProvider system;

    /**
     * Width selected for the renderer.
     */
    protected int width;

    /**
     * height selected for the renderer.
     */
    protected int height;

    /**
     * Bit depth selected for renderer.
     */
    protected int bpp;

    /**
     * Frequency selected for renderer.
     */
    protected int frq;

    /**
     * Is the display full screen?
     */
    protected boolean fs;

    /**
     * Is the display created already?
     */
    protected boolean created;

    /**
     * Alpha bits to use for the renderer.
     */
    protected int alphaBits = 0;

    /**
     * Depth bits to use for the renderer.
     */
    protected int depthBits = 8;

    /**
     * Stencil bits to use for the renderer.
     */
    protected int stencilBits = 0;

    /**
     * Number of samples to use for the multisample buffer.
     */
    protected int samples = 0;

    /**
     * Gamma value of display - default is 1.0f. 0->infinity
     */
    protected float gamma = 1.0f;

    /**
     * Brightness value of display - default is 0f. -1.0 -> 1.0
     */
    protected float brightness = 0;

    /**
     * Contrast value of display - default is 1.0f. 0->infinity
     */
    protected float contrast = 1;

    private static final Map<String, SystemProvider> systemProviderMap = new HashMap<String, SystemProvider>();
            
    private Map<String, Class<? extends CanvasConstructor>> canvasConstructRegistry = new HashMap<String, Class<? extends CanvasConstructor>>();

    /**
     * A new display system has been created. The default static display system
     * is set to the newly created display system.
     */
    protected DisplaySystem() {
    }

    /**
     * <code>getDisplaySystem</code> is a factory method that creates the
     * appropriate display system specified by the key parameter. If the key
     * given is not a valid identifier for a specific display system, the
     * fallback default is returned.
     * 
     * @param key
     *            the display system to use.
     * @return the appropriate display system specified by the key.
     */
    public static DisplaySystem getDisplaySystem(String key) {
        // force to initialize joystick input before display system as there are
        // LWJGL issues with creating it afterwards.
        // FIXME What about the impact on other display systems?
        JoystickInput.get();

        try {
            setSystemProvider(getCachedSystemProvider(key));
        }
        catch (IllegalStateException alreadySet) {
            LOGGER.warning(alreadySet.getMessage());
        }

        return getDisplaySystem();
    }

    private static SystemProvider getCachedSystemProvider(String providerId) {
        return getSystemProviderMap().get(providerId);
    }

    private static Map<String, SystemProvider> getSystemProviderMap()
            throws ServiceConfigurationError {
        if (systemProviderMap.isEmpty()) {
            @SuppressWarnings("unchecked")
            Iterator<SystemProvider> displayProviders = Service.providers(SystemProvider.class);
            while (displayProviders.hasNext()) {
                SystemProvider provider = (SystemProvider) displayProviders
                        .next();
                systemProviderMap.put(provider.getProviderIdentifier(),
                        provider);
            }

            // if the provider map is still empty (no providers found),
            if (systemProviderMap.isEmpty()) {
                // insert the default
                SystemProvider sp = new LWJGLSystemProvider();
                systemProviderMap.put(sp.getProviderIdentifier(), sp);
                sp = new JOGLSystemProvider();
                systemProviderMap.put(sp.getProviderIdentifier(), sp);
                sp = new DummySystemProvider();
                systemProviderMap.put(sp.getProviderIdentifier(), sp);
            }
        }

        return systemProviderMap;
    }

    /**
     * Returns all available system providers
     *
     * @return String array containing all available system providers
     */
    public static String[] getSystemProviderIdentifiers() {
        Collection<String> ids = getSystemProviderMap().keySet();

        String[] names = new String[ids.size()];

        ids.toArray(names);

        return names;
    }

    /**
     * Returns the currently system provider.  If no system provider has been
     * set, then a default LWJGL system provider is used.
     * 
     * @return The current system provider.
     */
    public static SystemProvider getSystemProvider() {
        SystemProvider currentProvider = system;
        if (currentProvider != null) {
            return currentProvider;
        }
        
        // if none defined by Service.providers, use fallback default
        synchronized (DisplaySystem.class) {
            if (system == null) {
                system = new LWJGLSystemProvider();
            }
            
            return system;
        }
    }

    
    /**
     * Sets the SystemProvider to be provider.
     * <p>
     * Once installed, the provider cannot be replaced.
     * 
     * @param provider
     *          the SystemProvider to install.  if <code>null</code>, no
     *          provider is set.
     * @throws IllegalStateException
     *          if a provider was previous installed.
     * 
     * @since 2.0
     */
    public static synchronized void setSystemProvider(SystemProvider provider) throws IllegalStateException {
       if (system != null) {
           throw new IllegalStateException("SystemProvider already set");
       }
       
       system = provider;
    }
    
    /**
     * Returns the currently created display system.
     * 
     * @return The current display system.
     */
    public static DisplaySystem getDisplaySystem() {
        return getSystemProvider().getDisplaySystem();
    }

    /**
     * Sets a new width for the display system
     * @param width
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Returns the set width for the display system.
     * 
     * @return The set width.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Sets a new height for the display system
     * @param height
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Returns the set height for the display system.
     * 
     * @return The set height.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns the set bit depth for the display system.
     * 
     * @return the set bit depth
     */
    public int getBitDepth() {
        return bpp;
    }

    /**
     * Returns the set frequency for the display system.
     * 
     * @return the set frequency
     */
    public int getFrequency() {
        return frq;
    }

    /**
     * Returns whether or not the display system is set to be full screen.
     * 
     * @return true if full screen
     */
    public boolean isFullScreen() {
        return fs;
    }

    /**
     * <code>getAdapter</code> returns the name of the underlying system's
     * graphics adapter for debugging / display purposes.
     * 
     * @return the adapter's name as a String
     */
    public abstract String getAdapter();

    /**
     * <code>getDriverVersion</code> returns a string representing the version
     * of driver installed on the underlying system.
     * 
     * @return the version as a String
     */
    public abstract String getDriverVersion();
    
    /**
     * <code>getDisplayVendor</code> returns the vendor of the graphics
     * adapter
     * 
     * @return The adapter vendor
     */
    public abstract String getDisplayVendor();

    /**
     * <code>getDisplayRenderer</code> returns details of the adapter
     * 
     * @return The adapter details
     */
    public abstract String getDisplayRenderer();

    /**
     * <code>getDisplayAPIVersion</code> returns the API version supported
     * 
     * @return The api version supported
     */
    public abstract String getDisplayAPIVersion();

    /**
     * <code>isValidDisplayMode</code> determines if the given parameters
     * constitute a valid display mode on this system. Returning true does not
     * necessarily guarantee that the system is capable of running in the
     * specified display mode, merely that it <i>believes </i> it is possible.
     * 
     * @param width
     *            the width/horizontal resolution of the display.
     * @param height
     *            the height/vertical resolution of the display.
     * @param bpp
     *            the bit depth of the display.
     * @param freq
     *            the frequency of refresh of the display (in Hz).
     */
    public abstract boolean isValidDisplayMode(int width, int height, int bpp,
            int freq);

    /**
     * <code>setVSyncEnabled</code> attempts to enable or disable monitor
     * vertical synchronization. The method is a "best attempt" to change the
     * monitor vertical refresh synchronization, and is <b>not </b> guaranteed
     * to be successful. This is dependent on OS.
     * 
     * @param enabled
     *            <code>true</code> to synchronize, <code>false</code> to
     *            ignore synchronization
     */
    public abstract void setVSyncEnabled(boolean enabled);

    /**
     * Sets the title of the display system. This is usually reflected by the
     * renderer as text in the menu bar.
     * 
     * @param title
     *            The new display title.
     */
    public abstract void setTitle(String title);

    /**
     * <code>createWindow</code> creates a window with the desired settings.
     * The width and height defined by w and h define the size of the window if
     * fullscreen is false, otherwise it defines the resolution of the
     * fullscreen display. The color depth is defined by bpp. The implementing
     * class should only allow 16, 24, and 32. The monitor frequency is defined
     * by the frq parameter and should not exceed the capabilities of the
     * connected hardware, the implementing class should attempt to assure this
     * does not happen. Lastly, the boolean flag fs determines if the display
     * should be windowed or fullscreen. If false, windowed is chosen. This
     * window will be placed in the center of the screen initially. If true
     * fullscreen mode will be entered with the appropriate settings.
     * 
     * @param w
     *            the width/horizontal resolution of the display.
     * @param h
     *            the height/vertical resolution of the display.
     * @param bpp
     *            the color depth of the display.
     * @param frq
     *            the frequency of refresh of the display.
     * @param fs
     *            flag determining if fullscreen is to be used or not. True will
     *            use fullscreen, false will use windowed mode.
     */
    public abstract void createWindow(int w, int h, int bpp, int frq, boolean fs);

    /**
     * <code>createHeadlessWindow</code> creates a headless window with the
     * desired settings. A headless window is a rendering target that is not
     * shown on screen. It is useful for doing offline rendering, integration
     * and so forth. You can not have a regular and headless window at the same
     * time. The width and height defined by w and h define the size of the
     * window. The color depth is defined by bpp.
     * 
     * @param w
     *            the width/horizontal resolution of the display.
     * @param h
     *            the height/vertical resolution of the display.
     * @param bpp
     *            the color depth of the display.
     */
    public abstract void createHeadlessWindow(int w, int h, int bpp);

    /**
     * <code>createCanvas</code> should create a canvas object with the
     * desired settings. The width and height defined by w and h define the size
     * of the canvas.  Makes an AWT canvas by default.
     * 
     * @param w
     *            the width/horizontal resolution of the display.
     * @param h
     *            the height/vertical resolution of the display.
     */
    public JMECanvas createCanvas(int w, int h) {
        return createCanvas(w, h, "AWT", new HashMap<String, Object>());
    }

    /**
     * <code>createCanvas</code> should create a canvas object with the desired
     * settings. The width and height defined by w and h define the size of the
     * canvas.
     * 
     * @param w
     *            the width/horizontal resolution of the display.
     * @param h
     *            the height/vertical resolution of the display.
     * @param type
     *            the type of canvas to make.  e.g. "AWT", "SWT".
     * @param props
     *            the properties we want to use (if any) for constructing our
     *            canvas.
     */
    public abstract JMECanvas createCanvas(int w, int h, String type, HashMap<String, Object> props);

    public void registerCanvasConstructor(String type, Class<? extends CanvasConstructor> constructorClass) {
        canvasConstructRegistry.put(type, constructorClass);
    }

    public CanvasConstructor makeCanvasConstructor(String type) {
        Class<? extends CanvasConstructor> constructorClass = canvasConstructRegistry.get(type);
        if (constructorClass == null) {
            throw new JmeException("Unregistered canvas type: "+type);
        }
        CanvasConstructor constructor;
        try {
            constructor = constructorClass.newInstance();
        } catch (Exception e) {
            throw new JmeException("Unable to instantiate canvas constructor: "+constructorClass);
        }
        return constructor;
    }

    /**
     * <code>recreateWindow</code> recreates a window with the desired
     * settings.
     * 
     * @param w
     *            the width/horizontal resolution of the display.
     * @param h
     *            the height/vertical resolution of the display.
     * @param bpp
     *            the color depth of the display.
     * @param frq
     *            the frequency of refresh of the display.
     * @param fs
     *            flag determining if fullscreen is to be used or not. True will
     *            use fullscreen, false will use windowed mode.
     */
    public abstract void recreateWindow(int w, int h, int bpp, int frq,
            boolean fs);

    /**
     * <code>getRenderer</code> returns the <code>Renderer</code>
     * implementation that is compatible with the chosen
     * <code>DisplaySystem</code>. For example, if
     * <code>LWJGLDisplaySystem</code> is used, the returned
     * <code>Renderer</code> will be</code> LWJGLRenderer</code>.
     * 
     * @return the appropriate <code>Renderer</code> implementation that is
     *         compatible with the used <code>DisplaySystem</code>.
     * @see com.jme.renderer.Renderer
     */
    public abstract Renderer getRenderer();

    /**
     * <code>setRenderer</code> sets the <code>Renderer</code> object that
     * is to be used by this display. The implementing class should take
     * measures to insure that the given Renderer is compatible with the
     * Display.
     * 
     * @param r
     *            the Renderer to set for this display.
     */
    public abstract void setRenderer(Renderer r);

    /**
     * <code>isCreated</code> returns the current status of the display
     * system. If the window and renderer are created, true is returned,
     * otherwise false.
     * 
     * @return whether the display system is created.
     */
    public boolean isCreated() {
        return created;
    }
    
    /**
     * <code>isActive</code> returns true if the display is active.
     * 
     * @return whether the display system is active.
     */
    public abstract boolean isActive();


    /**
     * <code>isClosing</code> notifies if the window is currently closing.
     * This could be caused via the application itself or external interrupts
     * such as alt-f4 etc.
     * 
     * @return true if the window is closing, false otherwise.
     */
    public abstract boolean isClosing();

    /**
     * <code>reset</code> cleans up the display system for closing or
     * restarting.
     */
    public abstract void reset();

    /**
     * <code>close</code> shutdowns and destroys any window contexts.
     */
    public abstract void close();

    /**
     * Returns the minimum bits per pixel in the alpha buffer.
     * 
     * @return the int value of alphaBits.
     */
    public int getMinAlphaBits() {
        return alphaBits;
    }

    /**
     * Sets the minimum bits per pixel in the alpha buffer.
     * 
     * @param alphaBits -
     *            the new value for alphaBits
     */
    public void setMinAlphaBits(int alphaBits) {
        this.alphaBits = alphaBits;
    }

    /**
     * Returns the minimum bits per pixel in the depth buffer.
     * 
     * @return the int value of depthBits.
     */
    public int getMinDepthBits() {
        return depthBits;
    }

    /**
     * Sets the minimum bits per pixel in the depth buffer.
     * 
     * @param depthBits -
     *            the new value for depthBits
     */
    public void setMinDepthBits(int depthBits) {
        this.depthBits = depthBits;
    }

    /**
     * Returns the minimum bits per pixel in the stencil buffer.
     * 
     * @return the int value of stencilBits.
     */
    public int getMinStencilBits() {
        return stencilBits;
    }

    /**
     * Sets the minimum bits per pixel in the stencil buffer.
     * 
     * @param stencilBits -
     *            the new value for stencilBits
     */
    public void setMinStencilBits(int stencilBits) {
        this.stencilBits = stencilBits;
    }

    /**
     * Returns the minimum samples in multisample buffer.
     * 
     * @return the int value of samples.
     */
    public int getMinSamples() {
        return samples;
    }

    /**
     * Sets the minimum samples in the multisample buffer.
     * 
     * @param samples -
     *            the new value for samples
     */
    public void setMinSamples(int samples) {
        this.samples = samples;
    }

    /**
     * Returns the brightness last requested by this display.
     * 
     * @return brightness - should be between -1 and 1.
     */
    public float getBrightness() {
        return brightness;
    }

    /**
     * Note: This affects the whole screen, not just the game window.
     * 
     * @param brightness
     *            The brightness to set (set -1 to 1) default is 0
     */
    public void setBrightness(float brightness) {
        this.brightness = brightness;
        updateDisplayBGC();
    }

    /**
     * @return Returns the contrast.
     */
    public float getContrast() {
        return contrast;
    }

    /**
     * Note: This affects the whole screen, not just the game window.
     * 
     * @param contrast
     *            The contrast to set (set greater than 0) default is 1
     */
    public void setContrast(float contrast) {
        this.contrast = contrast;
        updateDisplayBGC();
    }

    /**
     * @return Returns the gamma.
     */
    public float getGamma() {
        return gamma;
    }

    /**
     * Note: This affects the whole screen, not just the game window.
     * 
     * @param gamma
     *            The gamma to set (default is 1)
     */
    public void setGamma(float gamma) {
        this.gamma = gamma;
        updateDisplayBGC();
    }

    /**
     * Sets all three in one call. <p/> Note: This affects the whole screen, not
     * just the game window.
     * 
     * @param brightness
     * @param gamma
     * @param contrast
     */
    public void setBrightnessGammaContrast(float brightness, float gamma,
            float contrast) {
        this.brightness = brightness;
        this.gamma = gamma;
        this.contrast = contrast;
        updateDisplayBGC();
    }

    /**
     * Called when the display system is created, this function sets the default
     * render states for the renderer. It should not be called directly by the
     * user.
     * 
     * @param r
     *            The renderer to get the default states from.
     */
    public static void updateStates(Renderer r) {
    	
    	for (RenderState.StateType type : RenderState.StateType.values()) {
    		
    		Renderer.defaultStateList[type.ordinal()] = r.createState(type);
    		Renderer.defaultStateList[type.ordinal()].setEnabled(false);
    	}
    }

    /**
     * Create a TextureRenderer using the underlying system.
     * 
     * @param width
     *            width of texture
     * @param height
     *            height of texture
     * @param target
     * @return A TextureRenderer for the display system.
     */
    public abstract TextureRenderer createTextureRenderer(int width,
            int height, TextureRenderer.Target target);
    
    /**
     * Create a TextureRenderer using the underlying system.
     * 
     * @param width
     *            width of texture
     * @param height
     *            height of texture
     * @param samples
     *            AA samples used when rendering to texture
     * @param target
     * @return A TextureRenderer for the display system.
     */
    public abstract TextureRenderer createTextureRenderer(int width,
            int height, int samples, TextureRenderer.Target target);

    /**
     * Translate world to screen coordinates
     * 
     * @param worldPosition
     *            Vector3f representing the world position to retrieve.
     * @return the screen position.
     */
    public Vector3f getScreenCoordinates(Vector3f worldPosition) {
        return getScreenCoordinates(worldPosition, null);
    }

    /**
     * Translate world to screen coordinates
     * 
     * @param worldPosition
     *            Vector3f representing the world position to retrieve.
     * @param store
     *            Vector3f to store the world position in.
     * @return Vector3f The store vector3f, after storing.
     */
    public Vector3f getScreenCoordinates(Vector3f worldPosition, Vector3f store) {
        return getRenderer().getCamera().getScreenCoordinates(worldPosition,
                store);
    }

    /**
     * Translate screen to world coordinates.
     * 
     * @param screenPosition
     *            Vector2f representing the screen position with 0,0 at the
     *            bottom left.
     * @param zPos
     *            The z position away from the viewing plane, between 0 and 1.
     * @return A Vector3f representing the vector's world position.
     */
    public Vector3f getWorldCoordinates(Vector2f screenPosition, float zPos) {
        return getWorldCoordinates(screenPosition, zPos, null);
    }

    /**
     * Translate screen to world coordinates.
     * 
     * @param screenPosition
     *            Vector2f representing the screen position with 0,0 at the
     *            bottom left
     * @param zPos
     *            float The z position away from the viewing plane.
     * @param store
     *            Vector3f The vector to store the result in.
     * @return Vector3f The store vector, after storing it's result.
     */
    public Vector3f getWorldCoordinates(Vector2f screenPosition, float zPos,
            Vector3f store) {
        return getRenderer().getCamera().getWorldCoordinates(screenPosition,
                zPos, store);
    }

    /**
     * Generate a pick ray from a 2d screen point. The screen point is assumed
     * to have origin at the lower left, but when using awt mouse clicks, you'll
     * want to set flipVertical to true since that system has an origin at the
     * upper right. The Ray will be in world coordinates and the direction will
     * be normalized.
     * 
     * @param screenPosition
     *            Vector2f representing the screen position with 0,0 at the
     *            bottom left
     * @param flipVertical
     *            Whether or not to flip the y coordinate of the screen position
     *            across the middle of the screen.
     * @param store
     *            The ray to store the result in. If null, a new Ray is created.
     * @return the ray
     */
    public Ray getPickRay(Vector2f screenPosition, boolean flipVertical,
            Ray store) {
        if (flipVertical) {
            screenPosition.y = getRenderer().getHeight() - screenPosition.y;
        }
        if (store == null) store = new Ray();
        getWorldCoordinates(screenPosition, 0,
                store.origin);
        getWorldCoordinates(screenPosition, 0.3f,
                store.direction).subtractLocal(store.origin)
                .normalizeLocal();
        return store;
    }

    /**
     * Update the display's gamma, brightness and contrast based on the set
     * values.
     */
    protected abstract void updateDisplayBGC();
    
    /**
     * Sets one or more icons for the DisplaySystem.
     * <p>
     * As a reference for usual platforms on number of icons and their sizes:
     * <ul>
     * <li>On Windows you should supply at least one 16x16 image and one 32x32.</li>
     * <li>Linux (and similar platforms) expect one 32x32 image.</li>
     * <li>Mac OS X should be supplied one 128x128 image.</li>
     * </ul>
     * </p>
     * <p>
     * Images should be in format RGBA8888. If they are not jME will try to convert them
     * using ImageUtils. If that fails a <code>JmeException</code> could be thrown.
     * </p>
     * 
     * @param iconImages
     *            Array of Images to be used as icons.
     * @author Tony Vera
     * @author Tijl Houtbeckers - some changes to handling non-RGBA8888 Images.
     * 
     */
    public abstract void setIcon(Image[] iconImages);

    /**
     * @return a RenderContext object representing the current OpenGL context.
     */
    public abstract RenderContext<?> getCurrentContext();

    public static synchronized void resetSystemProvider() {
        if (system != null) { 
            system.disposeDisplaySystem();
            system = null;
        }
    }

    /**
     * If running in windowed mode, move the window's position to the given
     * display coordinates.
     * 
     * @param locX
     * @param locY
     */
    public abstract void moveWindowTo(int locX, int locY);
}

