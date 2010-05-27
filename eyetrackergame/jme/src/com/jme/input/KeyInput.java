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
import java.util.ArrayList;
import java.util.List;

import com.jme.input.lwjgl.LWJGLKeyInput;

/**
 * <code>KeyInput</code> provides an interface for dealing with keyboard input.
 * There are public constants for each key of the keyboard, which correspond
 * to the LWJGL key bindings. This may require conversion by other subclasses
 * for specific APIs. <br>
 * The status of specific keys can be queried via the {@link #isKeyDown}
 * method. For each key that is pressed or released an event is generated which
 * can be received by a {@link KeyInputListener}, these are subscribed via
 * {@link #addListener(KeyInputListener)}. Handling of events is done inside the
 * {@link #update} method.
 *
 * @author Mark Powell
 * @version $Id: KeyInput.java 4666 2009-09-04 17:22:52Z skye.book $
 */
public abstract class KeyInput extends Input {

    /**
     * escape key.
     */
    public static final int KEY_ESCAPE = 0x01;
    /**
     * 1 key.
     */
    public static final int KEY_1 = 0x02;
    /**
     * 2 key.
     */
    public static final int KEY_2 = 0x03;
    /**
     * 3 key.
     */
    public static final int KEY_3 = 0x04;
    /**
     * 4 key.
     */
    public static final int KEY_4 = 0x05;
    /**
     * 5 key.
     */
    public static final int KEY_5 = 0x06;
    /**
     * 6 key.
     */
    public static final int KEY_6 = 0x07;
    /**
     * 7 key.
     */
    public static final int KEY_7 = 0x08;
    /**
     * 8 key.
     */
    public static final int KEY_8 = 0x09;
    /**
     * 9 key.
     */
    public static final int KEY_9 = 0x0A;
    /**
     * 0 key.
     */
    public static final int KEY_0 = 0x0B;
    /**
     * - key.
     */
    public static final int KEY_MINUS = 0x0C;
    /**
     * = key.
     */
    public static final int KEY_EQUALS = 0x0D;
    /**
     * back key.
     */
    public static final int KEY_BACK = 0x0E;
    /**
     * tab key.
     */
    public static final int KEY_TAB = 0x0F;
    /**
     * q key.
     */
    public static final int KEY_Q = 0x10;
    /**
     * w key.
     */
    public static final int KEY_W = 0x11;
    /**
     * e key.
     */
    public static final int KEY_E = 0x12;
    /**
     * r key.
     */
    public static final int KEY_R = 0x13;
    /**
     * t key.
     */
    public static final int KEY_T = 0x14;
    /**
     * y key.
     */
    public static final int KEY_Y = 0x15;
    /**
     * u key.
     */
    public static final int KEY_U = 0x16;
    /**
     * i key.
     */
    public static final int KEY_I = 0x17;
    /**
     * o key.
     */
    public static final int KEY_O = 0x18;
    /**
     * p key.
     */
    public static final int KEY_P = 0x19;
    /**
     * [ key.
     */
    public static final int KEY_LBRACKET = 0x1A;
    /**
     * ] key.
     */
    public static final int KEY_RBRACKET = 0x1B;
    /**
     * enter (main keyboard) key.
     */
    public static final int KEY_RETURN = 0x1C;
    /**
     * left control key.
     */
    public static final int KEY_LCONTROL = 0x1D;
    /**
     * a key.
     */
    public static final int KEY_A = 0x1E;
    /**
     * s key.
     */
    public static final int KEY_S = 0x1F;
    /**
     * d key.
     */
    public static final int KEY_D = 0x20;
    /**
     * f key.
     */
    public static final int KEY_F = 0x21;
    /**
     * g key.
     */
    public static final int KEY_G = 0x22;
    /**
     * h key.
     */
    public static final int KEY_H = 0x23;
    /**
     * j key.
     */
    public static final int KEY_J = 0x24;
    /**
     * k key.
     */
    public static final int KEY_K = 0x25;
    /**
     * l key.
     */
    public static final int KEY_L = 0x26;
    /**
     * ; key.
     */
    public static final int KEY_SEMICOLON = 0x27;
    /**
     * ' key.
     */
    public static final int KEY_APOSTROPHE = 0x28;
    /**
     * ` key.
     */
    public static final int KEY_GRAVE = 0x29;
    /**
     * left shift key.
     */
    public static final int KEY_LSHIFT = 0x2A;
    /**
     * \ key.
     */
    public static final int KEY_BACKSLASH = 0x2B;
    /**
     * z key.
     */
    public static final int KEY_Z = 0x2C;
    /**
     * x key.
     */
    public static final int KEY_X = 0x2D;
    /**
     * c key.
     */
    public static final int KEY_C = 0x2E;
    /**
     * v key.
     */
    public static final int KEY_V = 0x2F;
    /**
     * b key.
     */
    public static final int KEY_B = 0x30;
    /**
     * n key.
     */
    public static final int KEY_N = 0x31;
    /**
     * m key.
     */
    public static final int KEY_M = 0x32;
    /**
     * , key.
     */
    public static final int KEY_COMMA = 0x33;
    /**
     * . key (main keyboard).
     */
    public static final int KEY_PERIOD = 0x34;
    /**
     * / key (main keyboard).
     */
    public static final int KEY_SLASH = 0x35;
    /**
     * right shift key.
     */
    public static final int KEY_RSHIFT = 0x36;
    /**
     * * key (on keypad).
     */
    public static final int KEY_MULTIPLY = 0x37;
    /**
     * left alt key.
     */
    public static final int KEY_LMENU = 0x38;
    /**
     * space key.
     */
    public static final int KEY_SPACE = 0x39;
    /**
     * caps lock key.
     */
    public static final int KEY_CAPITAL = 0x3A;
    /**
     * F1 key.
     */
    public static final int KEY_F1 = 0x3B;
    /**
     * F2 key.
     */
    public static final int KEY_F2 = 0x3C;
    /**
     * F3 key.
     */
    public static final int KEY_F3 = 0x3D;
    /**
     * F4 key.
     */
    public static final int KEY_F4 = 0x3E;
    /**
     * F5 key.
     */
    public static final int KEY_F5 = 0x3F;
    /**
     * F6 key.
     */
    public static final int KEY_F6 = 0x40;
    /**
     * F7 key.
     */
    public static final int KEY_F7 = 0x41;
    /**
     * F8 key.
     */
    public static final int KEY_F8 = 0x42;
    /**
     * F9 key.
     */
    public static final int KEY_F9 = 0x43;
    /**
     * F10 key.
     */
    public static final int KEY_F10 = 0x44;
    /**
     * NumLK key.
     */
    public static final int KEY_NUMLOCK = 0x45;
    /**
     * Scroll lock key.
     */
    public static final int KEY_SCROLL = 0x46;
    /**
     * 7 key (num pad).
     */
    public static final int KEY_NUMPAD7 = 0x47;
    /**
     * 8 key (num pad).
     */
    public static final int KEY_NUMPAD8 = 0x48;
    /**
     * 9 key (num pad).
     */
    public static final int KEY_NUMPAD9 = 0x49;
    /**
     * - key (num pad).
     */
    public static final int KEY_SUBTRACT = 0x4A;
    /**
     * 4 key (num pad).
     */
    public static final int KEY_NUMPAD4 = 0x4B;
    /**
     * 5 key (num pad).
     */
    public static final int KEY_NUMPAD5 = 0x4C;
    /**
     * 6 key (num pad).
     */
    public static final int KEY_NUMPAD6 = 0x4D;
    /**
     * + key (num pad).
     */
    public static final int KEY_ADD = 0x4E;
    /**
     * 1 key (num pad).
     */
    public static final int KEY_NUMPAD1 = 0x4F;
    /**
     * 2 key (num pad).
     */
    public static final int KEY_NUMPAD2 = 0x50;
    /**
     * 3 key (num pad).
     */
    public static final int KEY_NUMPAD3 = 0x51;
    /**
     * 0 key (num pad).
     */
    public static final int KEY_NUMPAD0 = 0x52;
    /**
     * . key (num pad).
     */
    public static final int KEY_DECIMAL = 0x53;
    /**
     * F11 key.
     */
    public static final int KEY_F11 = 0x57;
    /**
     * F12 key.
     */
    public static final int KEY_F12 = 0x58;
    /**
     * F13 key.
     */
    public static final int KEY_F13 = 0x64;
    /**
     * F14 key.
     */
    public static final int KEY_F14 = 0x65;
    /**
     * F15 key.
     */
    public static final int KEY_F15 = 0x66;
    /**
     * kana key (Japanese).
     */
    public static final int KEY_KANA = 0x70;
    /**
     * convert key (Japanese).
     */
    public static final int KEY_CONVERT = 0x79;
    /**
     * noconvert key (Japanese).
     */
    public static final int KEY_NOCONVERT = 0x7B;
    /**
     * yen key (Japanese).
     */
    public static final int KEY_YEN = 0x7D;
    /**
     * = on num pad (NEC PC98).
     */
    public static final int KEY_NUMPADEQUALS = 0x8D;
    /**
     * circum flex key (Japanese).
     */
    public static final int KEY_CIRCUMFLEX = 0x90;
    /**
     * &#064; key (NEC PC98).
     */
    public static final int KEY_AT = 0x91;
    /**
     * : key (NEC PC98)
     */
    public static final int KEY_COLON = 0x92;
    /**
     * _ key (NEC PC98).
     */
    public static final int KEY_UNDERLINE = 0x93;
    /**
     * kanji key (Japanese).
     */
    public static final int KEY_KANJI = 0x94;
    /**
     * stop key (NEC PC98).
     */
    public static final int KEY_STOP = 0x95;
    /**
     * ax key (Japanese).
     */
    public static final int KEY_AX = 0x96;
    /**
     * (J3100).
     */
    public static final int KEY_UNLABELED = 0x97;
    /**
     * Enter key (num pad).
     */
    public static final int KEY_NUMPADENTER = 0x9C;
    /**
     * right control key.
     */
    public static final int KEY_RCONTROL = 0x9D;
    /**
     * , key on num pad (NEC PC98).
     */
    public static final int KEY_NUMPADCOMMA = 0xB3;
    /**
     * / key (num pad).
     */
    public static final int KEY_DIVIDE = 0xB5;
    /**
     * SysRq key.
     */
    public static final int KEY_SYSRQ = 0xB7;
    /**
     * right alt key.
     */
    public static final int KEY_RMENU = 0xB8;
    /**
     * pause key.
     */
    public static final int KEY_PAUSE = 0xC5;
    /**
     * home key.
     */
    public static final int KEY_HOME = 0xC7;
    /**
     * up arrow key.
     */
    public static final int KEY_UP = 0xC8;
    /**
     * PgUp key.
     */
    public static final int KEY_PRIOR = 0xC9;
    /**
     * PgUp key.
     */
    public static final int KEY_PGUP = KEY_PRIOR;

    /**
     * left arrow key.
     */
    public static final int KEY_LEFT = 0xCB;
    /**
     * right arrow key.
     */
    public static final int KEY_RIGHT = 0xCD;
    /**
     * end key.
     */
    public static final int KEY_END = 0xCF;
    /**
     * down arrow key.
     */
    public static final int KEY_DOWN = 0xD0;
    /**
     * PgDn key.
     */
    public static final int KEY_NEXT = 0xD1;
    /**
     * PgDn key.
     */
    public static final int KEY_PGDN = KEY_NEXT;

    /**
     * insert key.
     */
    public static final int KEY_INSERT = 0xD2;
    /**
     * delete key.
     */
    public static final int KEY_DELETE = 0xD3;
    public static final int KEY_LMETA            = 0xDB; /* Left Windows/Option key */
    /**
     * The left windows key, mapped to KEY_LMETA
     *
     * @Deprecated Use KEY_LMETA instead
     */
    public static final int KEY_LWIN            = KEY_LMETA; /* Left Windows key */
    public static final int KEY_RMETA            = 0xDC; /* Right Windows/Option key */
    /**
     * The right windows key, mapped to KEY_RMETA
     *
     * @Deprecated Use KEY_RMETA instead
     */
    public static final int KEY_RWIN            = KEY_RMETA; /* Right Windows key */
    public static final int KEY_APPS = 0xDD;
    /**
     * power key.
     */
    public static final int KEY_POWER = 0xDE;
    /**
     * sleep key.
     */
    public static final int KEY_SLEEP = 0xDF;


    private static KeyInput instance;
    /**
     * list of event listeners.
     */
    protected List<KeyInputListener> listeners;
    public static final String INPUT_LWJGL = LWJGLKeyInput.class.getName();
    public static final String INPUT_AWT = "com.jmex.awt.input.AWTKeyInput";

    /**
     * @return the input instance, implementation is determined by querying {@link #getProvider()}
     */
    public static KeyInput get() {
        if ( instance == null ) {
            try {
                final Constructor constructor = getProvider().getDeclaredConstructor( (Class[])null );
                constructor.setAccessible( true );
                instance = (KeyInput) constructor.newInstance( (Object[])null );
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
    private static Class provider = LWJGLKeyInput.class;

    /**
     * Change the provider used for keyboard input. Default is {@link KeyInput#INPUT_LWJGL}.
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
     * Change the provider used for keyboard input. Default is {@link InputSystem#INPUT_SYSTEM_LWJGL}.
     *
     * @param value new provider
     * @throws IllegalStateException if called after first call of {@link #get()}. Note that get is called when
     *                               creating the DisplaySystem.
     */
    public static void setProvider( final Class value ) {
        if ( instance != null ) {
            throw new IllegalStateException( "Provider may only be changed before input is created!" );
        }
        if ( KeyInput.class.isAssignableFrom( value ) ) {
            provider = value;
        }
        else {
            throw new IllegalArgumentException( "Specified class does not extend KeyInput" );
        }
    }

    /**
     * <code>isKeyDown</code> returns true if the given key is pressed. False
     * otherwise.
     *
     * @param key the keycode to check for.
     * @return true if the key is pressed, false otherwise.
     */
    public abstract boolean isKeyDown( int key );

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
     * <code>getKeyName</code> returns the string prepresentation of a
     * key code.
     *
     * @param key the key code to check.
     * @return the string representation of a key code.
     */
    public abstract String getKeyName( int key );

    /**
     * The reverse of getKeyName, returns the value of the key given the name
     *
     * @param name
     * @return the value of the key
     */
    public abstract int getKeyIndex( String name );

    /**
     * Updates the current state of the keyboard, holding
     * information about what keys are pressed.
     * Invokes event listeners synchronously.     
     */
    public abstract void update();

    /**
     * <code>destroy</code> frees the keyboard for use by other applications.
     * Destroy is protected now - please is {@link #destroyIfInitalized()}.
     */
    protected abstract void destroy();

    /**
     * Subscribe a listener to receive mouse events. Enable event generation.
     *
     * @param listener to be subscribed
     */
    public void addListener( KeyInputListener listener ) {
        if ( listeners == null ) {
            listeners = new ArrayList<KeyInputListener>();
        }

        listeners.add( listener );
    }

    /**
     * Unsubscribe a listener. Disable event generation if no more listeners.
     *
     * @param listener to be unsubcribed
     * @see #addListener(KeyInputListener)
     */
    public void removeListener( KeyInputListener listener ) {
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
	 * Check if a listener is already added to this KeyInput
	 * @param listener listener to check for
	 * @return true if listener is contained in the listener list
	 */
	public boolean containsListener( KeyInputListener listener ) {
		if ( listeners != null ) {
			return listeners.contains( listener );
		}
		return false;
	}

	/**
	 * Get all added key listeners
	 * @return ArrayList of listeners added to this KeyInput
	 */
	public List<KeyInputListener> getListeners() {
		return listeners;
	}


	/**
     * Destroy the input if it was initialized.
     */
    public static void destroyIfInitalized() {
        if ( instance != null ) {
            instance.destroy();
            instance = null;
        }
    }

    public boolean isShiftDown() {
        return isKeyDown(KeyInput.KEY_LSHIFT) || isKeyDown(KeyInput.KEY_RSHIFT);
    }

    public boolean isControlDown() {
        return isKeyDown(KeyInput.KEY_LCONTROL) || isKeyDown(KeyInput.KEY_RCONTROL);
    }
    
    public abstract void clear();
    
    public abstract void clearKey(int keycode);
}
