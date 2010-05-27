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

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Dummy JoystickInput to disable joystick support.
 */
public class DummyJoystickInput extends JoystickInput {
    private static final Logger logger = Logger
            .getLogger(DummyJoystickInput.class.getName());
    
    private DummyJoystick dummyJoystick = new DummyJoystick();

    public DummyJoystickInput() {
        logger.info("Joystick support is disabled");
    }

    /**
     * @return number of attached game controllers
     */
    public int getJoystickCount() {
        return 0;
    }

    /**
     * Game controller at specified index.
     *
     * @param index index of the controller (0 <= index <= {@link #getJoystickCount()})
     * @return game controller
     */
    public Joystick getJoystick( int index ) {
        return null;
    }

    @Override
    public ArrayList<Joystick> findJoysticksByAxis(String... axis) {
        return null;
    }
    
    /**
     * This is a method to obtain a single joystick. It's simple to used but not
     * recommended (user may have multiple joysticks!).
     *
     * @return what the implementation thinks is the main joystick, not null!
     */
    public Joystick getDefaultJoystick() {
        return dummyJoystick;
    }

    protected void destroy() {

    }

    /**
     * Poll data for this input system part (update the values) and send events to all listeners
     * (events will not be generated if no listeners were added via addListener).
     */
    public void update() {

    }

    public static class DummyJoystick implements Joystick {
        public void rumble( int axis, float intensity ) {
        }

        public String[] getAxisNames() {
            return new String[0];
        }

        public int getAxisCount() {
            return 0;
        }

        public float getAxisValue( int axis ) {
            return 0;
        }

        public int getButtonCount() {
            return 0;
        }

        public boolean isButtonPressed( int button ) {
            return false;
        }

        public String getName() {
            return "Dummy";
        }

        public void setDeadZone( int axis, float value ) {

        }

        public int findAxis(String name) {
            return -1;
        }
    }

}
