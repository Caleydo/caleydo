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

package com.jme.input;

import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ArrayList;

import com.jme.image.Image;
import com.jme.input.lwjgl.LWJGLMouseInput;


/**
 * <code>MouseInput</code> defines an interface to communicate with the mouse
 * input device.
 * The status of specific buttons can be queried via the {@link #isButtonDown}
 * method. Position data can be queried by various get methods.
 * For each button that is pressed or released as well as for movement of
 * mouse or wheel an event is generated which
 * can be received by a {@link MouseInputListener}, these are subscribed via
 * {@link #addListener(MouseInputListener)}. Handling of events is done inside the
 * {@link #update} method.
 * @author Mark Powell
 * @version $Id: MouseInput.java 4666 2009-09-04 17:22:52Z skye.book $
 */
public abstract class MouseInput extends Input {

    private static MouseInput instance;
    /**
     * list of event listeners.
     */
    protected ArrayList<MouseInputListener> listeners;
    public static final String INPUT_LWJGL = LWJGLMouseInput.class.getName();
    public static final String INPUT_AWT = "com.jmex.awt.input.AWTMouseInput";

    /**
     * @return the input instance, implementation is determined by querying {@link #getProvider()}
     */
    public static MouseInput get() {
        if ( instance == null ) {
            try {
                final Constructor constructor = getProvider().getDeclaredConstructor( (Class[])null );
                constructor.setAccessible( true );
                instance = (MouseInput) constructor.newInstance( (Object[])null );
            } catch ( Exception e ) {
                throw new RuntimeException( "Error creating input provider", e );
            }
        }
        return instance;
    }


    /**
     * Query current provider for input.
     *
     * @return currently selected provider
     */
    public static Class<?> getProvider() {
        return provider;
    }

    /**
     * store the value for field provider
     */
    private static Class provider = LWJGLMouseInput.class;

    /**
     * Change the provider used for mouse input. Default is {@link MouseInput#INPUT_LWJGL}.
     *
     * @param value new provider class name
     * @throws IllegalStateException    if called after first call of {@link #get()}. Note that get is called when
     *                                  creating the DisplaySystem.
     * @throws IllegalArgumentException if the specified class cannot be found using {@link Class#forName(String)}
     */
    public static void setProvider( String value ) {
        if ( instance != null ) {
            throw new IllegalStateException( "Provider may only be changed before input is created!" );
        }
        if ( InputSystem.INPUT_SYSTEM_LWJGL.equals( value ) ) {
            value = INPUT_LWJGL;
        }
        else if ( InputSystem.INPUT_SYSTEM_AWT.equals( value ) ) {
            value = INPUT_AWT;
        }
        try {
            setProvider( Class.forName( value ) );
        } catch ( ClassNotFoundException e ) {
            throw new IllegalArgumentException( "Unsupported provider: " + e.getMessage() );
        }
    }

    /**
     * Change the provider used for mouse input. Default is {@link InputSystem#INPUT_SYSTEM_LWJGL}.
     *
     * @param value new provider
     * @throws IllegalStateException if called after first call of {@link #get()}. Note that get is called when
     *                               creating the DisplaySystem.
     */
    public static void setProvider( final Class value ) {
        if ( instance != null ) {
            throw new IllegalStateException( "Provider may only be changed before input is created!" );
        }
        if ( MouseInput.class.isAssignableFrom( value ) ) {
            provider = value;
        }
        else {
            throw new IllegalArgumentException( "Specified class does not extend MouseInput" );
        }
    }

    /**
     * <code>destroy</code> cleans up the native mouse interface.
     * Destroy is protected now - please is {@link #destroyIfInitalized()}.
     */
    protected abstract void destroy();

    /**
     *
     * <code>getButtonIndex</code> gets the button code for a given button
     * name.
     * @param buttonName the name to get the code for.
     * @return the code for the given button name.
     */
    public abstract int getButtonIndex(String buttonName);

    /**
     *
     * <code>isButtonDown</code> returns true if a given button is pressed,
     * false if it is not pressed.
     * @param buttonCode the button code to check.
     * @return true if the button is pressed, false otherwise.
     */
    public abstract boolean isButtonDown(int buttonCode);

    /**
     *
     * <code>getButtonName</code> gets the button name for a given button
     * code.
     * @param buttonIndex the code to get the name for.
     * @return the name for the given button code.
     */
    public abstract String getButtonName(int buttonIndex);

    /**
     * <code>isInited</code> returns true if the key class is not setup
     * already (ie. .get() was not yet called).
     * 
     * @return true if it is initialized and ready for use, false otherwise.
     */
    public static boolean isInited() {
        return instance != null;
    }

    /**
     *
     * <code>getWheelDelta</code> gets the change in the mouse wheel.
     * @return the change in the mouse wheel.
     */
    public abstract int getWheelDelta();

    /**
     *
     * <code>getXDelta</code> gets the change along the x axis.
     * @return the change along the x axis.
     */
    public abstract int getXDelta();

    /**
     *
     * <code>getYDelta</code> gets the change along the y axis.
     * @return the change along the y axis.
     */
    public abstract int getYDelta();

    /**
     *
     * <code>getXAbsolute</code> gets the absolute x axis value.
     * @return the absolute x axis value.
     */
    public abstract int getXAbsolute();

    /**
     *
     * <code>getYAbsolute</code> gets the absolute y axis value.
     * @return the absolute y axis value.
     */
    public abstract int getYAbsolute();

    /**
     * Updates the state of the mouse (position and button states). Invokes event listeners synchronously.
     */
    public abstract void update();

    /**
     * <code>setCursorVisible</code> sets the visibility of the hardware cursor.
     * @param v true turns the cursor on false turns it off
     */
    public abstract void setCursorVisible(boolean v);

    /**
     * <code>isCursorVisible</code>
     * @return the visibility of the hardware cursor
     */
    public abstract boolean isCursorVisible();

	/**
	 * <code>setHardwareCursor</code> sets the image to use for the hardware cursor.
	 * @param file URL to cursor image
	 */
	public abstract void setHardwareCursor(URL file);

	/**
	 * <code>setHardwareCursor</code> sets the image and hotspot position to use for the hardware cursor.
	 * @param file URL to cursor image
	 * @param xHotspot Cursor X hotspot position
	 * @param yHotspot Cursor Y hotspot position
	 */
	public abstract void setHardwareCursor(URL file, int xHotspot, int yHotspot);

    /**
     * This method will set an animated hardware cursor.
     * 
     * @param file
     *            in this method file is only used as a key for cursor cashing
     * @param images
     *            the animation frames
     * @param delays
     *            delays between changing each frame
     * @param xHotspot
     *            from image left
     * @param yHotspot
     *            from image bottom
     */
    public abstract void setHardwareCursor(URL file, Image[] images,
            int[] delays, int xHotspot, int yHotspot);

	/**
     * Subscribe a listener to receive mouse events. Enable event generation.
     * 
     * @param listener
     *            to be subscribed
     */
    public void addListener( MouseInputListener listener ) {
        if ( listeners == null ) {
            listeners = new ArrayList<MouseInputListener>();
        }

        listeners.add( listener );
    }

    /**
     * Unsubscribe a listener. Disable event generation if no more listeners.
     * @see #addListener(com.jme.input.MouseInputListener)
     * @param listener to be unsubscribed
     */
    public void removeListener( MouseInputListener listener ) {
        if ( listeners != null ) {
            listeners.remove( listener );
        }
    }

    /**
     * Remove all listeners and disable event generation.
     */
    public void removeListeners() {
        if ( listeners != null ) {
            listeners.clear();
        }
    }

	/**
	 * Check if a listener is already added to this MouseInput
	 * @param listener listener to check for
	 * @return true if listener is contained in the listenerlist
	 */
	public boolean containsListener( MouseInputListener listener ) {
        return listeners != null && listeners.contains( listener );
    }

	/**
	 * Get all added mouse listeners
	 * @return ArrayList of listeners added to this MouseInput
	 */
	public ArrayList<MouseInputListener> getListeners() {
		return listeners;
	}

	/**
     * Destroy the input if it was initialized.
     */
    public static void destroyIfInitalized() {
        if ( instance != null )
        {
            instance.destroy();
            instance = null;
        }
    }

    /**
     * @return absolute wheel rotation
     */
    public abstract int getWheelRotation();

    /**
     * @return number of mouse buttons
     */
    public abstract int getButtonCount();
    
    public abstract void setCursorPosition( int x, int y);
    
    public abstract void clear();
    
    public abstract void clearButton(int buttonCode);
    
}
