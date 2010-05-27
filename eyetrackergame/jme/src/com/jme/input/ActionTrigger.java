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

import java.util.ArrayList;

import com.jme.input.action.InputActionEvent;
import com.jme.input.action.InputActionInterface;

/**
 * Stores data about an action trigger. Subclasses provide the actual trigger functionality.
 * Triggers are used by {@link InputHandler} to decouple event
 * occurrence and action invocation.
 * <br>
 * The most important methods of the trigger are {@link #activate()} and {@link #deactivate()}: they add and remove this
 * trigger from the list of active trigger in the InputHandler. For all active triggers the {@link #performAction}
 * method is called in the {@link InputHandler#update(float)} method.
 * <br>
 * A trigger also registers itself with an input handler and can be removed from the list of triggers via the
 * {@link #remove()} method.
 */
public abstract class ActionTrigger {
    protected final InputHandler inputHandler;

    /**
     * Create a new action trigger for a fixed input handler.
     *
     * @param inputHandler handler this trigger belongs to (cannot be changed)
     * @param triggerName  name of this trigger (usually a button or axis name)
     * @param action       action that is performed by this trigger
     * @param allowRepeats true to allow multiple action invocations per event
     */
    protected ActionTrigger( final InputHandler inputHandler, String triggerName, InputActionInterface action, boolean allowRepeats ) {
        this.inputHandler = inputHandler;
        this.action = action;
        this.allowRepeats = allowRepeats;
        this.name = triggerName;
        synchronized ( inputHandler ) {
            if ( inputHandler.allTriggers == null ) {
                inputHandler.allTriggers = new ArrayList<ActionTrigger>();
            }
            inputHandler.allTriggers.add( this );
        }
    }

    /**
     * Remove this trigger.
     */
    protected void remove() {
        synchronized ( inputHandler ) {
            deactivate();
            inputHandler.allTriggers.remove( this );
        }
    }

    protected final String name;
    protected final boolean allowRepeats;
    protected final InputActionInterface action;

    /**
     * Invoked to activate or deactivate a trigger on specific event. The data in the
     * parameters depend on the kind of trigger. Defaults for each parameter (set if value for parameter
     * is unknown or not applicable) are given below. The trigger should activate or deactivate itself
     * if appropriate.
     *
     * @param character some character data associated with the event, default '\0'.
     *                  <br>example: keyboard character
     * @param index     index of the device part that caused the event, default -1, >= 0 if valid
     *                  <br>example: mouse button index, joystick axis index
     * @param position  new position of the device part that caused the event, default NaN, common range [-1;1]
     *                  <br>example: joystick axis position
     * @param delta     position delta of the device part that caused the event, default NaN, common range [-1;1]
     *                  <br>example: joystick axis delta
     * @param pressed   indicates if a button was pressed or released, default: false
     *                  <br>example: true if joystick button is pressed, false if joystick button is released
     * @param data      any trigger specific data
     *                  <br>example: joystick triggers get the Joystick instance for fast comparison
     * @see #activate()
     * @see #deactivate()
     */
    public abstract void checkActivation( char character, int index,
                                          float position, float delta, boolean pressed, Object data );

    /**
     * Called by InputHandler to fill info about the trigger into an event. Commonly overwritten by trigger
     * implementations to provide additional info.
     *
     * @param event where to put the information
     * @param invocationIndex index to distinct multiple action invocations per trigger activation
     * @see #getActionInvocationCount()
     */
    protected void putTriggerInfo( InputActionEvent event, int invocationIndex ) {
        event.setTriggerName( name );
        event.setTriggerAllowsRepeats( allowRepeats );
        event.setTriggerDevice( getDeviceName() );
        event.setTriggerCharacter( '\0' );
        event.setTriggerDelta( 0 );
        event.setTriggerIndex( 0 );
        event.setTriggerPosition( 0 );
        event.setTriggerPressed( false );
    }

    /**
     * @return name of the device this trigger belongs to
     */
    protected abstract String getDeviceName();

    /**
     * true while in the active triggers list of the InputHandler.
     */
    private boolean active;

    /**
     * add this trigger to the list of active trigger in the InputHandler.
     */
    protected final void activate() {
        synchronized ( inputHandler ) {
            if ( !active && inputHandler.isEnabled() ) {
                active = true;
                ActionTrigger firstActiveTrigger = inputHandler.activeTriggers;
                inputHandler.activeTriggers = this;
                this.setNext( firstActiveTrigger );
            }
        }
    }

    /**
     * remove this trigger from the list of active trigger in the InputHandler.
     */
    protected final void deactivate() {
        synchronized ( inputHandler ) {
            if ( active ) {
                active = false;
                ActionTrigger firstActiveTrigger = inputHandler.activeTriggers;
                if ( firstActiveTrigger == this ) {
                    inputHandler.activeTriggers = getNext();
                    setNext( null );
                }
                else {
                    this.getPrevious().setNext( this.getNext() );
                }
            }
        }
    }

    /**
     * Used to maintain a linked list of active triggers.
     */
    private ActionTrigger next;

    /**
     * Used to maintain a linked list of active triggers.
     *
     * @return current value of the field next
     */
    ActionTrigger getNext() {
        return this.next;
    }

    /**
     * Used to maintain a linked list of active triggers.
     *
     * @param value new value for field next
     * @return true if next was changed
     */
    boolean setNext( final ActionTrigger value ) {
        final ActionTrigger oldValue = this.next;
        boolean changed = false;
        if ( oldValue != value ) {
            if ( oldValue != null ) {
                this.next = null;
                oldValue.setPrevious( null );
            }
            this.next = value;
            if ( value != null ) {
                value.setPrevious( this );
            }
            changed = true;
        }
        return changed;
    }

    /**
     * Used to maintain a linked list of active triggers.
     */
    private ActionTrigger previous;

    /**
     * @return current value of the field previous
     */
    ActionTrigger getPrevious() {
        return this.previous;
    }

    /**
     * Used to maintain a linked list of active triggers.
     *
     * @param value new value for field previous
     * @return true if previous was changed
     */
    boolean setPrevious( final ActionTrigger value ) {
        final ActionTrigger oldValue = this.previous;
        boolean changed = false;
        if ( oldValue != value ) {
            if ( oldValue != null ) {
                this.previous = null;
                oldValue.setNext( null );
            }
            this.previous = value;
            if ( value != null ) {
                value.setNext( this );
            }
            changed = true;
        }
        return changed;
    }

    /**
     * @return the number of times the action should be invoked if triggered
     */
    protected int getActionInvocationCount() {
        return 1;
    }

    /**
     * Perform the action and deactivate the trigger if it does not allow repeats.
     *
     * @param event info about the event that caused the action
     */
    public void performAction( InputActionEvent event ) {
        final int count = getActionInvocationCount();
        for ( int i=0; i < count; i++ ) {
            putTriggerInfo( event, i );
            action.performAction( event );
        }
        if ( !allowRepeats ) {
            deactivate();
        }
    }

    /**
     * @return true if the trigger was activated
     * @see ActionTrigger
     */
    public final boolean isActive() {
        return active;
    }

    /**
     * Trigger implementation for using {@link KeyBindingManager} as trigger.
     */
    static class CommandTrigger extends ActionTrigger {
        protected CommandTrigger( InputHandler handler, String triggerName, InputActionInterface action, boolean allowRepeats ) {
            super( handler, triggerName, action, allowRepeats );
            activate();
        }

        public void performAction( InputActionEvent event ) {
            if ( name == null ||
                    KeyBindingManager.getKeyBindingManager().isValidCommand( name, allowRepeats ) ) {
                super.performAction( event );
                activate();
            }
        }

        public void checkActivation( char character, int index, float position, float delta, boolean pressed, Object data ) {
            //is a trigger that is checked each frame
        }

        protected String getDeviceName() {
            return "command";
        }
    }
}