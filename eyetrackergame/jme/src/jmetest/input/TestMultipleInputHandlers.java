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

import java.util.logging.Logger;

import com.jme.app.SimpleGame;
import com.jme.input.InputHandler;
import com.jme.input.KeyInput;
import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;


public class TestMultipleInputHandlers extends SimpleGame {
    private static final Logger logger = Logger
            .getLogger(TestMultipleInputHandlers.class.getName());
    
    private InputHandler input1;
    private InputHandler input2;
    private InputHandler input2_child;

    protected void simpleInitGame() {
        input1 = new InputHandler();
        input2 = new InputHandler();
        input2_child = new InputHandler();
        input2.addToAttachedHandlers( input2_child );

        input1.addAction( new InputAction() {
            public void performAction( InputActionEvent evt ) {
                logger.info( "Input 1 got SPACE event" );
            }
        }, InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_SPACE, InputHandler.AXIS_NONE, false );

        input2.addAction( new InputAction() {
            public void performAction( InputActionEvent evt ) {
                logger.info( "Input 2 got SPACE event" );
            }
        }, InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_SPACE, InputHandler.AXIS_NONE, false );

        input2_child.addAction( new InputAction() {
            public void performAction( InputActionEvent evt ) {
                logger.info( "Input 2 child got SPACE event" );
            }
        }, InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_SPACE, InputHandler.AXIS_NONE, false );
    }

    @Override
    protected void simpleUpdate() {
        input1.update( tpf );
        input2.update( tpf );
    }

    public static void main( String[] args ) {
        new TestMultipleInputHandlers().start();
    }
}

/*
 * $Log: TestMultipleInputHandlers.java,v $
 * Revision 1.2  2007/08/02 23:48:57  nca
 * logging cleanup
 *
 * Revision 1.1  2007/02/06 11:23:14  irrisor
 * Topic 4479: mapping of mouse buttons in awt events and query methods aligned; extracted static method to set up AWTMouseInput.
 *
 */

