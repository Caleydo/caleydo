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

package jmetest.base;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.input.InputHandler;
import com.jme.input.KeyInput;
import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Box;

/**
 * @author Irrisor
 */
public class TestFrustumCulling extends SimpleGame {
    protected void simpleInitGame() {
        for ( int x = 0; x < 10; x++ ) {
            for ( int y = 0; y < 10; y++ ) {
                Box box = new Box( x + ", " + y, new Vector3f(), 0.5f, 0.5f, 0.5f );
                box.setModelBound( new BoundingBox() );
                box.updateModelBound();
                box.getLocalTranslation().set( x, 0, y );
                rootNode.attachChild( box );
            }
        }

        input.addAction( new InputAction() {
            public void performAction( InputActionEvent evt ) {
                // zoom in
                cam.setFrustum( cam.getFrustumNear(), cam.getFrustumFar(),
                        cam.getFrustumLeft()*0.99f, cam.getFrustumRight()*0.99f,
                        cam.getFrustumTop()*0.99f, cam.getFrustumBottom()*0.99f );
            }
        }, InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_Q, InputHandler.AXIS_NONE, true );

        input.addAction( new InputAction() {
            public void performAction( InputActionEvent evt ) {
                // zoom in
                cam.setFrustum( cam.getFrustumNear(), cam.getFrustumFar(),
                        cam.getFrustumLeft()*1.01f, cam.getFrustumRight()*1.01f,
                        cam.getFrustumTop()*1.01f, cam.getFrustumBottom()*1.01f );
            }
        }, InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_E, InputHandler.AXIS_NONE, true );
    }

    public static void main( String[] args ) {
        new TestFrustumCulling().start();
    }
}
