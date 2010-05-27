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
package com.jme.input.util;

import com.jme.input.ActionTrigger;
import com.jme.input.InputHandler;
import com.jme.input.action.InputActionEvent;
import com.jme.input.action.InputActionInterface;

/**
 * trigger for simulating button
 */
class SyntheticTrigger extends ActionTrigger {
    private SyntheticTriggerContainer container;
    private boolean permanentlyActiveIfRepeats;

    public SyntheticTrigger( SyntheticTriggerContainer container, InputHandler handler, InputActionInterface action,
                             boolean allowRepeats, boolean permanentlyActiveIfRepeats ) {
        super( handler, container.getName(), action, allowRepeats );
        this.container = container;
        container.add( this );
        this.permanentlyActiveIfRepeats = permanentlyActiveIfRepeats;
        if ( permanentlyActiveIfRepeats ) {
            if ( allowRepeats ) {
                activate();
            }
        }
        infos[0] = new TriggerInfo();
    }

    private int count;
    private TriggerInfo[] infos = new TriggerInfo[1];

    protected int getActionInvocationCount() {
        return count;
    }

    protected void remove() {
        super.remove();
        container.remove( this );
    }

    public void performAction( InputActionEvent event ) {
        super.performAction( event );
        if ( !allowRepeats ) {
            count = 0;
        }
    }

    protected void putTriggerInfo( InputActionEvent event, int invocationIndex ) {
        super.putTriggerInfo( event, invocationIndex );
        event.setTriggerIndex( container.getIndex() );
        TriggerInfo info = infos[invocationIndex];
        event.setTriggerCharacter( info.character );
        event.setTriggerPressed( info.pressed );
        event.setTriggerDelta( info.delta );
        event.setTriggerPosition( info.position );
        event.setTriggerData( info.data );
    }

    protected final String getDeviceName() {
        return UtilInputHandlerDevice.DEVICE_UTIL;
    }

    public void checkActivation( char character, int buttonIndex, float position, float delta, boolean pressed, Object data ) {
        if ( buttonIndex == container.getIndex() ) {
            if ( !inputHandler.isEnabled() ) return;
            TriggerInfo info;
            if ( allowRepeats ) {
                info = infos[0];
                count = 1;
            }
            else {
                if ( count >= infos.length ) {
                    TriggerInfo[] oldInfos = infos;
                    infos = new TriggerInfo[ count + 3 ];
                    System.arraycopy( oldInfos, 0, infos, 0, oldInfos.length );
                    for ( int i = oldInfos.length; i < infos.length; i++ ) {
                        infos[i] = new TriggerInfo();
                    }
                }
                info = infos[count];
                count++;
            }
            info.character = character;
            info.position = position;
            info.delta = delta;
            info.pressed = pressed;
            info.data = data;
            if ( !allowRepeats ) {
                activate();
            } else if ( !permanentlyActiveIfRepeats ) {
                if ( pressed ) {
                    activate();
                }
                else {
                    deactivate();
                }
            }
        }
    }
}

/*
 * $log$
 */

