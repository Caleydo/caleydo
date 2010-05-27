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
package jmetest.renderer.state;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.input.KeyBindingManager;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.Text;
import com.jme.scene.Spatial.CullHint;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Quad;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.StippleState;

/**
 * StippleState Test.<br>
 * Demonstrates the use of StippleState.<br>
 * Different stipple masks are applied to a Quad, Sphere and Box.
 * 
 * @author Christoph Luder
 */
public class TestStippleState extends SimpleGame {
    /** List of RenderStates to enable/disable when Space is pressed */
    private ArrayList<RenderState> states = new ArrayList<RenderState>();
    
    /**
     * creates different stipple mask, and applies them to a Box, Sphere.
     */
    @Override
    protected void simpleInitGame() {
        // create a StippleState using the fly stipple pattern
        StippleState stippleState = display.getRenderer().createStippleState();
        states.add(stippleState);
        stippleState.setStippleMask(createFlyPattern());
        // create a Box and apply the StippleState
        Box box = new Box("box", new Vector3f(), 1, 1, 1);
        box.setModelBound(new BoundingBox());
        box.updateModelBound();
        box.setLocalTranslation(2, 0, 0);
        box.setRenderState(stippleState);
        rootNode.attachChild(box);

        // create a StippleState using the halftone stipple pattern
        stippleState = display.getRenderer().createStippleState();
        states.add(stippleState);
        stippleState.setStippleMask(createHalftonePattern());
        // create a Sphere and apply the StippleState
        Sphere sphere = new Sphere("sphere", 15, 15, 1);
        sphere.setModelBound(new BoundingBox());
        sphere.updateModelBound();
        sphere.setLocalTranslation(-2, 0, 0);
        sphere.setRenderState(stippleState);
        rootNode.attachChild(sphere);

        // create a StippleState using the interlaced pattern
        stippleState = display.getRenderer().createStippleState();
        stippleState.setStippleMask(createInterlacedPattern());
        states.add(stippleState);
        // create a Quad 256 x 256 pixel big and apply our generated StippleState
        Quad quad = new Quad("quad", 256, 256);
        quad.setRenderQueueMode(Renderer.QUEUE_ORTHO);
        quad.setModelBound(new BoundingBox());
        quad.updateModelBound();
        quad.setLocalTranslation(display.getWidth()-128, 128, 0);
        quad.setCullHint(CullHint.Never);
        quad.setRenderState(stippleState);
        statNode.attachChild(quad);
        statNode.updateGeometricState(0, true);
        
        // set up a key to enable / disable the StippleStates
        KeyBindingManager.getKeyBindingManager().add("toggleStipple", Keyboard.KEY_SPACE);
        Text info = Text.createDefaultTextLabel("info", "Press Space to toggle StippleStates");
        info.setLocalTranslation(0, 20, 0);
        statNode.attachChild(info);
    }

    /**
     * check if Space is pressed and toggle the RenderStates.
     */
    @Override
    protected void simpleUpdate() {
        if (KeyBindingManager.getKeyBindingManager().isValidCommand("toggleStipple", false)) {
            for (RenderState r : states) {
                r.setEnabled(!r.isEnabled());
            }
        }
    }
    
    public static void main(String[] args) {
        TestStippleState game = new TestStippleState();
        game.setConfigShowMode(ConfigShowMode.AlwaysShow);
        game.start();
    }

    private ByteBuffer createInterlacedPattern() {
        ByteBuffer interlaced = ByteBuffer.allocate(1024);
        interlaced.put(new byte[] {
                0x00, 0x00, 0x00, 0x00, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,
                0x00, 0x00, 0x00, 0x00, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,
                0x00, 0x00, 0x00, 0x00, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,
                0x00, 0x00, 0x00, 0x00, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,
                0x00, 0x00, 0x00, 0x00, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,
                0x00, 0x00, 0x00, 0x00, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,
                0x00, 0x00, 0x00, 0x00, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,
                0x00, 0x00, 0x00, 0x00, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,
                0x00, 0x00, 0x00, 0x00, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,
                0x00, 0x00, 0x00, 0x00, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,
                0x00, 0x00, 0x00, 0x00, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,
                0x00, 0x00, 0x00, 0x00, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,
                0x00, 0x00, 0x00, 0x00, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,
                0x00, 0x00, 0x00, 0x00, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,
                0x00, 0x00, 0x00, 0x00, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,
                0x00, 0x00, 0x00, 0x00, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF
                });
        interlaced.rewind();
        return interlaced;
    }
    
    private ByteBuffer createFlyPattern() {
        ByteBuffer fly = ByteBuffer.allocateDirect(1024);
        fly.put(new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x03, (byte) 0x80, (byte) 0x01,
                (byte) 0xC0, (byte) 0x06, (byte) 0xC0, (byte) 0x03,
                (byte) 0x60, (byte) 0x04, (byte) 0x60, (byte) 0x06,
                (byte) 0x20, (byte) 0x04, (byte) 0x30, (byte) 0x0C,
                (byte) 0x20, (byte) 0x04, (byte) 0x18, (byte) 0x18,
                (byte) 0x20, (byte) 0x04, (byte) 0x0C, (byte) 0x30,
                (byte) 0x20, (byte) 0x04, (byte) 0x06, (byte) 0x60,
                (byte) 0x20, (byte) 0x44, (byte) 0x03, (byte) 0xC0,
                (byte) 0x22, (byte) 0x44, (byte) 0x01, (byte) 0x80,
                (byte) 0x22, (byte) 0x44, (byte) 0x01, (byte) 0x80,
                (byte) 0x22, (byte) 0x44, (byte) 0x01, (byte) 0x80,
                (byte) 0x22, (byte) 0x44, (byte) 0x01, (byte) 0x80,
                (byte) 0x22, (byte) 0x44, (byte) 0x01, (byte) 0x80,
                (byte) 0x22, (byte) 0x44, (byte) 0x01, (byte) 0x80,
                (byte) 0x22, (byte) 0x66, (byte) 0x01, (byte) 0x80,
                (byte) 0x66, (byte) 0x33, (byte) 0x01, (byte) 0x80,
                (byte) 0xCC, (byte) 0x19, (byte) 0x81, (byte) 0x81,
                (byte) 0x98, (byte) 0x0C, (byte) 0xC1, (byte) 0x83,
                (byte) 0x30, (byte) 0x07, (byte) 0xe1, (byte) 0x87,
                (byte) 0xe0, (byte) 0x03, (byte) 0x3f, (byte) 0xfc,
                (byte) 0xc0, (byte) 0x03, (byte) 0x31, (byte) 0x8c,
                (byte) 0xc0, (byte) 0x03, (byte) 0x33, (byte) 0xcc,
                (byte) 0xc0, (byte) 0x06, (byte) 0x64, (byte) 0x26,
                (byte) 0x60, (byte) 0x0c, (byte) 0xcc, (byte) 0x33,
                (byte) 0x30, (byte) 0x18, (byte) 0xcc, (byte) 0x33,
                (byte) 0x18, (byte) 0x10, (byte) 0xc4, (byte) 0x23,
                (byte) 0x08, (byte) 0x10, (byte) 0x63, (byte) 0xC6,
                (byte) 0x08, (byte) 0x10, (byte) 0x30, (byte) 0x0c,
                (byte) 0x08, (byte) 0x10, (byte) 0x18, (byte) 0x18,
                (byte) 0x08, (byte) 0x10, (byte) 0x00, (byte) 0x00,
                (byte) 0x08 });
        fly.rewind();

        return fly;
    }
    
    private ByteBuffer createHalftonePattern() {
        ByteBuffer halftone = ByteBuffer.allocate(1024);
        halftone.put(new byte[] {                   
                (byte)0xAA, (byte)0xAA, (byte)0xAA, (byte)0xAA, 0x55, 0x55, 0x55, 0x55, 
                (byte)0xAA, (byte)0xAA, (byte)0xAA, (byte)0xAA, 0x55, 0x55, 0x55, 0x55, 
                (byte)0xAA, (byte)0xAA, (byte)0xAA, (byte)0xAA, 0x55, 0x55, 0x55, 0x55,
                (byte)0xAA, (byte)0xAA, (byte)0xAA, (byte)0xAA, 0x55, 0x55, 0x55, 0x55, 
                (byte)0xAA, (byte)0xAA, (byte)0xAA, (byte)0xAA, 0x55, 0x55, 0x55, 0x55, 
                (byte)0xAA, (byte)0xAA, (byte)0xAA, (byte)0xAA, 0x55, 0x55, 0x55, 0x55,
                (byte)0xAA, (byte)0xAA, (byte)0xAA, (byte)0xAA, 0x55, 0x55, 0x55, 0x55, 
                (byte)0xAA, (byte)0xAA, (byte)0xAA, (byte)0xAA, 0x55, 0x55, 0x55, 0x55, 
                (byte)0xAA, (byte)0xAA, (byte)0xAA, (byte)0xAA, 0x55, 0x55, 0x55, 0x55,
                (byte)0xAA, (byte)0xAA, (byte)0xAA, (byte)0xAA, 0x55, 0x55, 0x55, 0x55, 
                (byte)0xAA, (byte)0xAA, (byte)0xAA, (byte)0xAA, 0x55, 0x55, 0x55, 0x55, 
                (byte)0xAA, (byte)0xAA, (byte)0xAA, (byte)0xAA, 0x55, 0x55, 0x55, 0x55,
                (byte)0xAA, (byte)0xAA, (byte)0xAA, (byte)0xAA, 0x55, 0x55, 0x55, 0x55, 
                (byte)0xAA, (byte)0xAA, (byte)0xAA, (byte)0xAA, 0x55, 0x55, 0x55, 0x55, 
                (byte)0xAA, (byte)0xAA, (byte)0xAA, (byte)0xAA, 0x55, 0x55, 0x55, 0x55,
                (byte)0xAA, (byte)0xAA, (byte)0xAA, (byte)0xAA, 0x55, 0x55, 0x55, 0x55});
        halftone.rewind();
        return halftone;
    }
}
