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

import com.jme.input.ActionTrigger;
import com.jme.input.InputHandler;
import com.jme.input.InputHandlerDevice;
import com.jme.input.action.InputActionEvent;
import com.jme.input.action.InputActionInterface;

/**
 * Creates InputHandler triggers for joystick support.
 */
public class JoystickInputHandlerDevice extends InputHandlerDevice {
    protected final Joystick joystick;

    public JoystickInputHandlerDevice( Joystick joystick, String name ) {
        super( name );
        this.joystick = joystick;
    }

    private TriggersJoystickInputListener joystickListener;

    public synchronized TriggersJoystickInputListener getJoystickListener() {
        if ( joystickListener == null ) {
            joystickListener = new TriggersJoystickInputListener();
            joystickListener.activate();
        }
        return joystickListener;
    }

    protected void createTriggers( InputActionInterface action, int axis, int button, boolean allowRepeats, InputHandler inputHandler ) {

        if ( axis != InputHandler.AXIS_NONE && axis < joystick.getAxisCount() ) {
            String[] axisNames = joystick.getAxisNames();
            int minAxis = axis == InputHandler.AXIS_ALL ? 0 : axis;
            int maxAxis = axis == InputHandler.AXIS_ALL ? axisNames.length - 1 : axis;
            for ( int j = minAxis; j <= maxAxis; j++ ) {
                new JoystickAxisTrigger( inputHandler, axisNames[j], action, joystick, j, allowRepeats );
            }
        }
        if ( button != InputHandler.BUTTON_NONE && axis < joystick.getButtonCount() ) {
            int minButton = button == InputHandler.BUTTON_ALL ? 0 : button;
            int maxButton = button == InputHandler.BUTTON_ALL ? joystick.getButtonCount() - 1 : button;
            for ( int j = minButton; j <= maxButton; j++ ) {
                new JoystickButtonTrigger( inputHandler, "BUTTON" + j, action, joystick, j, allowRepeats );
            }
        }
    }
    
    /**
     * 
     * this method returns the joystick connected to this handler
     * 
     * @return
     *    the connected joystick
     */
    public Joystick getJoystick() {
    	return joystick;
	}

	protected class JoystickButtonTrigger extends ActionTrigger {
        private int button;
        private Joystick joystick;
        private boolean pressed;

        public JoystickButtonTrigger( InputHandler handler, String triggerName, InputActionInterface action,
                                      Joystick joystick, int button, boolean allowRepeats ) {
            super( handler, triggerName, action, allowRepeats );
            this.button = button;
            this.joystick = joystick;
            getJoystickListener().add( this );
        }

        protected void remove() {
            super.remove();
            getJoystickListener().remove( this );
        }

        protected void putTriggerInfo( InputActionEvent event, int invocationIndex ) {
            super.putTriggerInfo( event, invocationIndex );
            event.setTriggerIndex( button );
            event.setTriggerCharacter( (char) ( 'A' + button ) );
            event.setTriggerPressed( pressed );
        }

        protected String getDeviceName() {
            return joystick.getName();
        }

        public void checkActivation( char character, int buttonIndex, float position, float delta, boolean pressed, Object data ) {
            if ( data == joystick && buttonIndex == this.button ) {
                if ( allowRepeats ) {
                    if ( pressed ) {
                        this.pressed = true;
                        activate();
                    }
                    else {
                        deactivate();
                    }
                } else {
                    this.pressed = pressed; 
                    activate();
                }
            }
        }
    }

    protected class JoystickAxisTrigger extends ActionTrigger {
        private Joystick joystick;
        private int axis;

        public JoystickAxisTrigger( InputHandler handler, String triggerName, InputActionInterface action, Joystick joystick,
                                    int axis, boolean allowRepeats ) {
            super( handler, triggerName, action, allowRepeats );
            this.joystick = joystick;
            this.axis = axis;
            getJoystickListener().add( this );
            if ( allowRepeats ) {
                activate();
            }
        }

        protected void remove() {
            super.remove();
            getJoystickListener().remove( this );
        }

        private float delta;
        private float position;

        protected void putTriggerInfo( InputActionEvent event, int invocationIndex ) {
            super.putTriggerInfo( event, invocationIndex );
            event.setTriggerIndex( axis );
            event.setTriggerDelta( delta );
            event.setTriggerPosition( position );
        }

        protected String getDeviceName() {
            return joystick.getName();
        }

        public void checkActivation( char character, int axisIndex, float position, float delta, boolean pressed, Object data ) {
            if ( data == joystick && axisIndex == this.axis ) {
                this.delta = position - this.position; //delta is the position, too
                this.position = position;
                if ( !allowRepeats ) {
                    activate();
                }
            }
        }
    }
    
}
