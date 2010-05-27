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

package com.jme.input.mouse;

import com.jme.input.ActionTrigger;
import com.jme.input.InputHandler;
import com.jme.input.InputHandlerDevice;
import com.jme.input.MouseInput;
import com.jme.input.action.InputActionEvent;
import com.jme.input.action.InputActionInterface;
import com.jme.system.DisplaySystem;

/**
 * Creates InputHandler triggers for mouse support.
 */
public class MouseInputHandlerDevice extends InputHandlerDevice {
    public MouseInputHandlerDevice() {
        super( InputHandler.DEVICE_MOUSE );
    }

    private TriggersMouseInputListener mouseListener;

    public synchronized TriggersMouseInputListener getMouseListener() {
        if ( mouseListener == null ) {
            mouseListener = new TriggersMouseInputListener();
            mouseListener.activate();
        } else if (!mouseListener.isActivated()) {
            mouseListener.activate();
        }
        return mouseListener;
    }

    protected void createTriggers( InputActionInterface action, int axis, int button, boolean allowRepeats, InputHandler inputHandler ) {
        if ( button != InputHandler.BUTTON_NONE ) {
            int minButton = button == InputHandler.BUTTON_ALL ? 0 : button;
            int maxButton = button == InputHandler.BUTTON_ALL ? MouseInput.get().getButtonCount() - 1 : button;
            for ( int i = minButton; i <= maxButton; i++ ) {
                new MouseButtonTrigger( inputHandler, MouseInput.get().getButtonName( i ),
                        action, i, allowRepeats );
            }
        }
        if ( axis != InputHandler.AXIS_NONE ) {
            int minAxis = axis == InputHandler.AXIS_ALL ? 0 : axis;
            int maxAxis = axis == InputHandler.AXIS_ALL ? 2 : axis;
            for ( int i = minAxis; i <= maxAxis; i++ ) {
                String axisName;
                switch ( i ) {
                    case 0:
                        axisName = "X Axis";
                        break;
                    case 1:
                        axisName = "Y Axis";
                        break;
                    case 2:
                        axisName = "Wheel";
                        break;
                    default:
                        axisName = null;
                }
                new MouseAxisTrigger( inputHandler, axisName, action, i, allowRepeats );
            }
        }
    }

    protected class MouseButtonTrigger extends ActionTrigger {
        private int button;
        private boolean pressed;

        public MouseButtonTrigger( InputHandler handler, String triggerName, InputActionInterface action, int button, boolean allowRepeats ) {
            super( handler, triggerName, action, allowRepeats );
            this.button = button;
            getMouseListener().add( this );
        }

        protected void remove() {
            super.remove();
            getMouseListener().remove( this );
        }

        protected void putTriggerInfo( InputActionEvent event, int invocationIndex ) {
            super.putTriggerInfo( event, invocationIndex );
            event.setTriggerIndex( button );
            event.setTriggerPressed( pressed );
            final char buttonChar;
            switch ( button ) {
                case 0:
                    buttonChar = 'L';
                    break;
                case 1:
                    buttonChar = 'R';
                    break;
                case 2:
                    buttonChar = 'M';
                    break;
                default:
                    buttonChar = (char) ( 'a' + button - 3 );
            }
            event.setTriggerCharacter( buttonChar );
        }

        protected String getDeviceName() {
            return InputHandler.DEVICE_MOUSE;
        }

        public void checkActivation( char character, int buttonIndex, float position, float delta, boolean pressed, Object data ) {
            if ( buttonIndex == this.button ) {
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

    protected class MouseAxisTrigger extends ActionTrigger {
        private int axis;

        public MouseAxisTrigger( InputHandler handler, String triggerName, InputActionInterface action, int axis, boolean allowRepeats ) {
            super( handler, triggerName, action, allowRepeats );
            this.axis = axis;
            getMouseListener().add( this );
            if ( allowRepeats ) {
                if ( axis == 0 ) {
                    position = MouseInput.get().getXAbsolute() / (float) DisplaySystem.getDisplaySystem().getWidth();
                } else if ( axis == 1 ) {
                    position = MouseInput.get().getYAbsolute() / (float) DisplaySystem.getDisplaySystem().getHeight();
                }
                activate();
            }
        }

        protected void remove() {
            super.remove();
            getMouseListener().remove( this );
        }

        private float delta;
        private float position;

        protected void putTriggerInfo( InputActionEvent event, int invocationIndex ) {
            super.putTriggerInfo( event, invocationIndex );
            event.setTriggerIndex( axis );
            event.setTriggerDelta( delta );
            delta = 0;
            event.setTriggerPosition( position );
        }

        protected String getDeviceName() {
            return InputHandler.DEVICE_MOUSE;
        }

        public void checkActivation( char character, int axisIndex, float position, float delta, boolean pressed, Object data ) {
            if ( axisIndex == this.axis ) {
                this.delta += delta;
                this.position = position;
                if ( !allowRepeats ) {
                    activate();
                }
            }
        }
    }
}
