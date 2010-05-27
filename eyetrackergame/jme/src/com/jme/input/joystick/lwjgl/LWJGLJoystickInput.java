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

package com.jme.input.joystick.lwjgl;

import java.util.ArrayList;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controllers;

import com.jme.input.joystick.DummyJoystickInput;
import com.jme.input.joystick.Joystick;
import com.jme.input.joystick.JoystickInput;
import com.jme.input.joystick.JoystickInputListener;

/**
 * LWJGL Implementation of {@link JoystickInput}.
 */
public class LWJGLJoystickInput extends JoystickInput {
    private ArrayList<LWJGLJoystick> joysticks;
    private DummyJoystickInput.DummyJoystick dummyJoystick;


    /**
     *
     * @throws RuntimeException if initialization failed
     */
    protected LWJGLJoystickInput() throws RuntimeException {
        try {
            Controllers.create();
            updateJoystickList();
        } catch ( LWJGLException e ) {
            throw new RuntimeException( "Initalizing joystick support failed", e );
        }
    }

    private void updateJoystickList() {
        joysticks = new ArrayList<LWJGLJoystick>();
        for ( int i = 0; i < Controllers.getControllerCount(); i++ ) {
            joysticks.add( new LWJGLJoystick( Controllers.getController( i ) ) );
        }
    }


    public void update() {
        Controllers.poll();
        while ( Controllers.next() ) {
            if ( listeners != null && listeners.size() > 0 ) {
                Joystick joystick = getJoystick( Controllers.getEventSource().getIndex() );
                int controlIndex = Controllers.getEventControlIndex();
                if ( Controllers.isEventButton() ) {
                    boolean buttonPressed = joystick.isButtonPressed( controlIndex );
                    for ( int i = 0; i < listeners.size(); i++ ) {
                        JoystickInputListener listener = listeners.get( i );
                        listener.onButton( joystick, controlIndex, buttonPressed );
                    }
                }
                else if ( Controllers.isEventAxis() ) {
                    float axisValue = joystick.getAxisValue( controlIndex );
                    for ( int i = 0; i < listeners.size(); i++ ) {
                        JoystickInputListener listener = listeners.get( i );
                        listener.onAxis( joystick, controlIndex, axisValue );
                    }
                }
            }
        }
    }

    public int getJoystickCount() {
        int numJoysticks = joysticks.size();
        if ( numJoysticks != Controllers.getControllerCount() )
        {
            updateJoystickList();
        }
        return numJoysticks;
    }

    public Joystick getJoystick( int index ) {
        return joysticks.get( index );
    }

    public Joystick getDefaultJoystick() {
        if ( getJoystickCount() > 0 )
        {
            return getJoystick( getJoystickCount()-1 );
        }
        
        if ( dummyJoystick == null )
        {
            dummyJoystick = new DummyJoystickInput.DummyJoystick();
        }
        return dummyJoystick;        
    }

    protected void destroy() {
        Controllers.destroy();
    }

    @Override
    public ArrayList<Joystick> findJoysticksByAxis(String... axis) {
        ArrayList<Joystick> rVal = new ArrayList<Joystick>();
        for (int i = 0; i < getJoystickCount(); i++) {
            Joystick test = getJoystick(i);
            boolean add = true;
            for (String aName : axis) {
                int aIndex = test.findAxis(aName); 
                if (aIndex == -1) {
                    add = false;
                    break;
                }
                
                try {
                    test.getAxisValue(aIndex);
                } catch (Exception e) {
                    add = false;
                    break;
                }
            }
            if (add)
                rVal.add(test);
        }
            
        return rVal;
    }

}
