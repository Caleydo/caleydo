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

package jmetest.shape;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Random;

import com.jme.app.SimpleGame;
import com.jme.image.Texture;
import com.jme.input.KeyInput;
import com.jme.input.controls.GameControl;
import com.jme.input.controls.GameControlManager;
import com.jme.input.controls.binding.KeyboardBinding;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.shape.MultiFaceBox;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;

/**
 * Shows the usage of TestMultiFaceBox.
 * The used Texture is 1 Unit wide and 8 Units high and is created dynamically.
 * The first 6 squares are mapped to the sides of the Box.
 * Everything else is exactly as in Box.
 */
public class TestMultiFaceBox extends SimpleGame {
    private static final Random RANDOM = new Random();
    private static final Font FONT = new Font("Arial Unicode MS", Font.PLAIN,
            30);
    private Color[] colors = new Color[] { Color.red, Color.green, Color.blue,
            Color.yellow, Color.white, Color.orange };
    private MultiFaceBox box;
    private int[] keys = new int[] { KeyInput.KEY_0, KeyInput.KEY_1,
            KeyInput.KEY_2, KeyInput.KEY_3, KeyInput.KEY_4, KeyInput.KEY_5 };

    private GameControl[] control = new GameControl[6];
    private Quad quad;
    private TextureState ts;

    protected void simpleInitGame() {
        // create a Quad to show the Texture itself
        quad = new Quad("info", 64, 64*8);
        quad.setLocalTranslation(new Vector3f(display.getWidth()-quad.getWidth(), 
                    quad.getHeight()/2 +10, 0));
        graphNode.attachChild(quad);
        
        // create the MultiFaceBox
        box = new MultiFaceBox("box", new Vector3f(), 10, 10, 10);
        // create controls to change the color of a face
        GameControlManager manager = new GameControlManager();
        for (int i = 0; i < 6; i++) {
            control[i] = manager.addControl("control" + 1);
            control[i].addBinding(new KeyboardBinding(keys[i]));
        }
        MaterialState ms = DisplaySystem.getDisplaySystem().getRenderer()
                .createMaterialState();
        ms.setEmissive(ColorRGBA.white.clone());
        box.setRenderState(ms);
        setTexture();
        rootNode.attachChild(box);
        
        // set the initial Camera location and look at the Box
        cam.setLocation(new Vector3f(-40, 40, 40));
        cam.lookAt(box.getLocalTranslation(), Vector3f.UNIT_Y);
    }

    /**
     * create a Texture which is 1 Unit wide and 8 Units high dynamically
     */
    private void setTexture() {
        final BufferedImage bi = new BufferedImage(64, 512,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D bg = (Graphics2D) bi.getGraphics();
        bg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        bg.setFont(FONT);
        for (int i = 0; i < 6; i++) {
            bg.setColor(colors[i]);
            bg.fillRect(0, i * 64, 64, (i + 1) * 64);
            bg.setColor(Color.black);
            bg.drawString("" + i, 28, 64 * i + 38);
        }
        bg.dispose();
        
        if (ts == null) {
            // only create the TextureState the first time
            ts = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
        } else {
            // destroy the old texture
            ts.deleteAll(true);
        }
        // create the new one
        Texture t = TextureManager.loadTexture(bi,
                Texture.MinificationFilter.BilinearNearestMipMap, 
                Texture.MagnificationFilter.Bilinear, 1, false);
        ts.setTexture(t);
        
        box.setRenderState(ts);
        box.updateRenderState();
        
        // show the same texture also on a simple Quad
        quad.setRenderState(ts);
        quad.updateRenderState();
    }

    /**
     * check for pressed keys and recreate the texture if needed.
     */
    @Override
    public void simpleUpdate() {
        for (int i = 0; i < 6; i++) {
            if (control[i].getValue() != 0) {
                System.out.println(i);
                colors[i] = new Color(RANDOM.nextInt(256), RANDOM.nextInt(256),
                        RANDOM.nextInt(256));
                setTexture();
            }
        }
    }
    
    public static void main(String... args) {
        TestMultiFaceBox app = new TestMultiFaceBox();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }
}