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
package jmetest.input.controls;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingSphere;
import com.jme.input.MouseInput;
import com.jme.input.controls.GameControlManager;
import com.jme.input.controls.binding.MouseAxisBinding;
import com.jme.input.controls.binding.MouseOffsetBinding;
import com.jme.input.controls.controller.Axis;
import com.jme.input.controls.controller.RotationController;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.CameraNode;
import com.jme.scene.Node;
import com.jme.scene.shape.Sphere;
import com.jme.system.DisplaySystem;

/**
 * This test shows how to use the MouseOffsetBinding game control.
 * A Camera node is rotated by a RotationController, 'powered' by MouseOffsetBindings.
 *  
 * @author Christoph Luder
 */
public class TestMouseOffsetBinding extends SimpleGame {
    @Override
    protected void simpleInitGame() {
        DisplaySystem.getDisplaySystem().getRenderer().setBackgroundColor(ColorRGBA.darkGray);
        
        // create a scene to have something to orient yourself 
        createScene();
        
        // create a GameControlManager and Controls to rotate the camera node
        GameControlManager m = new GameControlManager();
        m.addControl("left").addBinding(new MouseOffsetBinding(MouseAxisBinding.AXIS_X, true));
        m.addControl("right").addBinding(new MouseOffsetBinding(MouseAxisBinding.AXIS_X, false));
        
        m.addControl("up").addBinding(new MouseOffsetBinding(MouseAxisBinding.AXIS_Y, true));
        m.addControl("down").addBinding(new MouseOffsetBinding(MouseAxisBinding.AXIS_Y, false));
        
        // create a camera node
        CameraNode camNode = new CameraNode("camNode", display.getRenderer().getCamera());
        rootNode.attachChild(camNode);
        
        // Moving the Mouse to the left or right should rotate the camera node around the Y Axis.
        RotationController yawControl = new RotationController(camNode, 
                                                m.getControl("left"), m.getControl("right"), 1.0f, Axis.Y);
        
        // Moving the Mouse up or down should rotate the camera node around the X Axis.
        RotationController pitchControl = new RotationController(camNode, 
                                                m.getControl("up"), m.getControl("down"), 1.0f, Axis.X);
        
        // add the controllers to the camera node
        camNode.addController(yawControl);
        camNode.addController(pitchControl);
        
        MouseInput.get().setCursorVisible(true);
    }

    @Override
    protected void simpleUpdate() {
        super.simpleUpdate();
    }

    public static void main(String[] args) {
        TestMouseOffsetBinding app = new TestMouseOffsetBinding();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }
    
    /** create a simple scene to have something to orient on */
    private void createScene() {
        int count = 5;
        int offset = 30;
        
        Node scene = new Node("scene");
        for (int i = 0; i < count; i++) {
            for (int j = 0; j < count; j++) {
                for (int k = 0; k < count; k++) {
                    Sphere s = new Sphere("s", 10, 10, 0.5f);
                    s.setModelBound(new BoundingSphere());
                    s.updateModelBound();
                    s.setLocalTranslation(i*offset, j*offset, k*offset);
                    scene.attachChild(s);
                }
            }
        }
        scene.setLocalTranslation(-count/2*offset, -count/2*offset, -count/2*offset);
        scene.lock();
        rootNode.attachChild(scene);
    }
}