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

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.java.games.input.Rumbler;

import org.lwjgl.input.Controller;

import com.jme.input.joystick.Joystick;

/**
 * LWJGL Implementation of {@link Joystick}.
 */
class LWJGLJoystick implements Joystick {
    private static final Logger logger = Logger.getLogger(LWJGLJoystick.class
            .getName());

    private Controller controller;
    private Rumbler[] rumblers;

    LWJGLJoystick( Controller controller ) {
        this.controller = controller;

        //fix me: dirty hack to obtain the rumblers:
        try {
            Field targetField = controller.getClass().getDeclaredField( "target" );
            targetField.setAccessible( true );
            net.java.games.input.Controller jinputController = (net.java.games.input.Controller) targetField.get( controller );
            Rumbler[] rumblers = jinputController.getRumblers();
            this.rumblers = new Rumbler[getAxisCount()];
            String[] axisNames = getAxisNames();
            for ( int i = 0; i < rumblers.length; i++ ) {
                Rumbler rumbler = rumblers[i];
                for ( int j = 0; j < axisNames.length; j++ ) {
                    String axisName = axisNames[j];
                    if ( axisName.equals( rumbler.getAxisName() ) ) {
                        this.rumblers[j] = rumbler;
                    }
                }
            }
        } catch ( Exception e ) {
            logger.logp(Level.SEVERE, this.getClass().toString(),
                    "LWJGLJoystick(Controller)", "Exception", e);
        }
    }

    public void rumble( int axis, float intensity ) {
        if ( rumblers != null && axis < rumblers.length ) {
            Rumbler rumbler = rumblers[axis];
            if ( rumbler != null ) {
                rumbler.rumble( intensity );
            }
        }
    }

    public String[] getAxisNames() {
        Controller c = controller;
        String[] axises = new String[c.getAxisCount()];
        for ( int i = 0; i < axises.length; i++ ) {
            axises[i] = c.getAxisName( i );
        }
        return axises;
    }

    public int getAxisCount() {
        return controller.getAxisCount();
    }

    public float getAxisValue( int axis ) {
        Controller c = controller;
        if ( axis < c.getAxisCount() ) {
            return c.getAxisValue( axis );
        }
        
        return 0;        
    }

    public int getButtonCount() {
        return controller.getButtonCount();
    }

    public boolean isButtonPressed( int button ) {
        Controller c = controller;
        if ( button < c.getButtonCount() ) {
            return c.isButtonPressed( button );
        }
        
        return false;        
    }

    public String getName() {
        return controller.getName();
    }

    public int findAxis(String name) {
        int i = 0;
        for (String axName : getAxisNames()){
            if (name.equalsIgnoreCase(axName)) return i;
            i++;
        }
        return -1;
    }
}
