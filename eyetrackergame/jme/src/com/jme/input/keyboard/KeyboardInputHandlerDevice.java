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

package com.jme.input.keyboard;

import com.jme.input.ActionTrigger;
import com.jme.input.InputHandler;
import com.jme.input.InputHandlerDevice;
import com.jme.input.action.InputActionEvent;
import com.jme.input.action.InputActionInterface;

/**
 * Creates InputHandler triggers for keyboard support.
 */
public class KeyboardInputHandlerDevice extends InputHandlerDevice {
    public KeyboardInputHandlerDevice() {
        super( InputHandler.DEVICE_KEYBOARD );
    }

    protected void createTriggers( InputActionInterface action, int axis, int button, boolean allowRepeats, InputHandler inputHandler ) {
//        if ( button == InputHandler.BUTTON_ALL ) {
        new KeyTrigger( inputHandler, "key", action, button, allowRepeats );
//        }
//        else if ( button != InputHandler.BUTTON_NONE ) {
//            inputHandler.addAction( action, "key code " + button, button, allowRepeats );
//        }
        if ( axis != InputHandler.AXIS_NONE ) {
//            logger.warning( "addAction was called with an axis specified for keyboard!" );
        }

    }

    private TriggersKeyboardInputListener keyboardListener;

    public synchronized TriggersKeyboardInputListener getKeyboardListener() {
        if ( keyboardListener == null ) {
            keyboardListener = new TriggersKeyboardInputListener();
            keyboardListener.activate();
        }
        return keyboardListener;
    }

    protected class KeyTrigger extends ActionTrigger {
        private final int keyCode;

        private int activations = 0;
        private char[] chars = new char[1];
        private int[] keyCodes = new int[1];
        private boolean[] pressed = new boolean[1];

        public KeyTrigger( InputHandler handler, String triggerName, InputActionInterface action, int keyCode, boolean allowRepeats ) {
            super( handler, triggerName, action, allowRepeats );
            this.keyCode = keyCode;
            getKeyboardListener().add( this );
        }

        protected void remove() {
            super.remove();
            getKeyboardListener().remove( this );
        }

        protected void putTriggerInfo( InputActionEvent event, int invocationIndex ) {
            super.putTriggerInfo( event, invocationIndex );
            event.setTriggerIndex( keyCodes[invocationIndex] );
            event.setTriggerPressed( pressed[invocationIndex] );
            event.setTriggerCharacter( chars[invocationIndex] );
        }

        public synchronized void performAction( InputActionEvent event ) {
            super.performAction( event );
            if ( !allowRepeats ) {
                activations = 0;
            }
        }

        protected int getActionInvocationCount() {
            return activations;
        }

        protected String getDeviceName() {
            return InputHandler.DEVICE_KEYBOARD;
        }

        public synchronized void checkActivation( char character, int buttonIndex, float position, float delta, boolean pressed, Object data ) {
            if ( buttonIndex == this.keyCode || this.keyCode == InputHandler.BUTTON_ALL ) {
                if ( !inputHandler.isEnabled() ) return;
                int activations = this.activations;
                char[] chars = this.chars;
                if ( pressed || !allowRepeats ) {
                    if ( activations == chars.length ) {
                        char[] newChars = new char[activations + 3]; // allocate 3 at a time
                        System.arraycopy( chars, 0, newChars, 0, activations );
                        this.chars = chars = newChars;
                        int[] newKeyCodes = new int[activations + 3];
                        System.arraycopy( this.keyCodes, 0, newKeyCodes, 0, activations );
                        this.keyCodes = newKeyCodes;
                        boolean[] newPressed = new boolean[activations + 3];
                        System.arraycopy( this.pressed, 0, newPressed, 0, activations );
                        this.pressed = newPressed;
                    }
                    chars[activations] = character;
                    keyCodes[activations] = buttonIndex;
                }
                if ( allowRepeats ) {
                    if ( pressed ) {
                        this.pressed[activations] = true;
                        activate();
                        this.activations = activations + 1;
                    }
                    else {
                        if ( activations <= 1 ) {
                            this.pressed[0] = false;
                            this.activations = 0;
                            deactivate();
                        }
                        else {
                            for ( int i = 0, j = 0; i < keyCodes.length; i++ ) {
                                if ( keyCodes[i] != buttonIndex ) {
                                    if ( j!=i ) {
                                        keyCodes[j] = keyCodes[i];
                                        chars[j] = chars[i];
                                    }
                                    j++;
                                } else {
                                    this.activations = activations - 1;
                                }
                            }
                        }
                    }
                }
                else {
                    this.pressed[activations] = pressed;
                    activate();
                    this.activations = activations + 1;
                }
            }
        }
    }
}
