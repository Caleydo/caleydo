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

package com.jme.input.joystick;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.input.Input;
import com.jme.input.InputSystem;
import com.jme.input.joystick.lwjgl.LWJGLJoystickInput;

/**
 * Manager for attached Joysticks. Singleton - use the {@link #get()} method.
 * Joysticks can be polled by calling {@link #update()}.
 *
 * @author Matthew D. Hicks, Irrisor
 */
public abstract class JoystickInput extends Input {
    private static final Logger logger = Logger.getLogger(JoystickInput.class.getName());

    /**
     * Only instance.
     */
    private static JoystickInput instance;
    public static final String INPUT_LWJGL = LWJGLJoystickInput.class.getName();
    public static final String INPUT_DUMMY = DummyJoystickInput.class.getName();

    /**
     * Initialize (if needed) and return the JoystickInput.
     * Implementation is determined by querying {@link #getProvider()}.<br>
     * Joystick support is disabled by default - call {@link #setProvider(String)} before creating the display system
     * to enable it (and choose implementation).
     * @return the only instance of the joystick manager
     */
    public static JoystickInput get() {
        if ( instance == null ) {
            try {
                if ( instance == null ) {
                    try {
                        final Constructor constructor = getProvider().getDeclaredConstructor( (Class[])null );
                        constructor.setAccessible( true );
                        instance = (JoystickInput) constructor.newInstance( (Object[])null );
                    } catch ( Exception e ) {
                        throw new RuntimeException( "Error creating input provider", e );
                    }
                }
                return instance;
            } catch ( RuntimeException e ) {
                logger.log(Level.WARNING,
                        "Joystick support disabled due to error:", e);
                instance = new DummyJoystickInput() {
                };
            }
        }
        return instance;
    }

    /**
     * Protect contructor to avoid direct invocation.
     */
    protected JoystickInput() {
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
    private static Class provider = DummyJoystickInput.class;

    /**
     * Change the provider used for joystick input. Default is {@link JoystickInput#INPUT_LWJGL}.
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
        else if ( InputSystem.INPUT_SYSTEM_DUMMY.equals( value ) ) {
            value = INPUT_DUMMY;
        }
        try {
            setProvider( Class.forName( value ) );
        } catch ( ClassNotFoundException e ) {
            throw new IllegalArgumentException( "Unsupported provider: " + e.getMessage() );
        }
    }

    /**
     * Change the provider used for joystick input. Default is {@link InputSystem#INPUT_SYSTEM_LWJGL}.
     *
     * @param value new provider
     * @throws IllegalStateException if called after first call of {@link #get()}. Note that get is called when
     *                               creating the DisplaySystem.
     */
    public static void setProvider( final Class value ) {
        if ( instance != null ) {
            throw new IllegalStateException( "Provider may only be changed before input is created!" );
        }
        if ( JoystickInput.class.isAssignableFrom( value ) ) {
            provider = value;
        }
        else {
            throw new IllegalArgumentException( "Specified class does not extend JoystickInput" );
        }
    }

    /**
     * list of event listeners.
     */
    protected ArrayList<JoystickInputListener> listeners;

    /**
     * Subscribe a listener to receive joystick events. Enable event generation.
     * @param listener to be subscribed
     */
    public void addListener( JoystickInputListener listener ) {
        if ( listeners == null ) {
            listeners = new ArrayList<JoystickInputListener>();
        }

        listeners.add( listener );
    }

    /**
     * Unsubscribe a listener. Disable event generation if no more listeners.
     * @see #addListener(JoystickInputListener)
     * @param listener to be unsuscribed
     */
    public void removeListener( JoystickInputListener listener ) {
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
     * Check if a listener is already added to this JoystickInput
     * @param listener listener to check for
     * @return true if listener is contained in the listenerlist
     */
    public boolean containsListener( JoystickInputListener listener ) {
    	return listeners != null && listeners.contains( listener );
    }

    /**
     * Get all added joystick listeners
     * @return ArrayList of listeners added to this JoystickInput
     */
    public ArrayList<JoystickInputListener> getListeners() {
    	return listeners;
    }

    /**
     * @return number of attached game controllers
     */
    public abstract int getJoystickCount();

    /**
     * Game controller at specified index.
     * @param index index of the controller (0 <= index <= {@link #getJoystickCount()})
     * @return game controller
     */
    public abstract Joystick getJoystick( int index );

    /**
     * This is a method to obtain a single joystick. It's simple to used but not
     * recommended (user may have multiple joysticks!).
     * @return what the implementation thinks is the main joystick, not null!
     */
    public abstract Joystick getDefaultJoystick();

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

    protected abstract void destroy();

    /**
     * Locate and return a joystick with the given axis names.
     * 
     * @param axis
     *            1 or more names to look by
     * @return array of joysticks, each having axis to match every name (case insensitive)
     */
    public abstract ArrayList<Joystick> findJoysticksByAxis(String... axis);
}