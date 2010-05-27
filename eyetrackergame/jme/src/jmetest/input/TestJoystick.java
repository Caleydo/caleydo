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

package jmetest.input;

import java.io.IOException;
import java.util.logging.Logger;

import com.jme.input.InputSystem;
import com.jme.input.joystick.Joystick;
import com.jme.input.joystick.JoystickInput;

/**
 * TestJoystick
 */
public class TestJoystick {
    private static final Logger logger = Logger.getLogger(TestJoystick.class
            .getName());
    
    public static void main(String[] args) throws IOException,
            InterruptedException {
        JoystickInput.setProvider(InputSystem.INPUT_SYSTEM_LWJGL);
        JoystickInput input = JoystickInput.get();
        logger.info("Number of joysticks: " + input.getJoystickCount());
        logger.info("Testing all joysticks.");

        boolean go = true;
        while (go) {
            input.update();
            for (int x = 0; x < input.getJoystickCount(); x++) {
                Joystick test = input.getJoystick(x);
                for (int i = 0; i < test.getAxisCount(); i++) {
                    float val = test.getAxisValue(i);
                    if (val != 0)
                        logger.info("joystick #"+x+" ('"+test.getName()+"') - axis '"+test.getAxisNames()[i]+"': "
                                + val);
                }
                for (int i = 0; i < test.getButtonCount(); i++) {
                    boolean pushed = test.isButtonPressed(i);
                    if (pushed)
                        logger.info("joystick #"+x+" ('"+test.getName()+"') - button #"+i+" pressed.");
                }
            }
            Thread.sleep(10);
        }
    }
}
