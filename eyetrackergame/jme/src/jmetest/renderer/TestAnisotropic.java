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

package jmetest.renderer;

import java.nio.FloatBuffer;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingSphere;
import com.jme.image.Texture;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial.LightCombineMode;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;

/**
 * <code>TestAnisotropic</code>
 * 
 * @author Joshua Slack
 * @version $Id: TestAnisotropic.java 4130 2009-03-19 20:04:51Z blaine.dev $
 */
public class TestAnisotropic extends SimpleGame {

    private Quad q;
    private Texture texture;
    private TextureState ts;
    private float anisoLevel = 0.0f;

    /**
     * Entry point for the test,
     * 
     * @param args
     */
    public static void main(String[] args) {
        TestAnisotropic app = new TestAnisotropic();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }

    protected void simpleUpdate() {
        if (KeyBindingManager.getKeyBindingManager().isValidCommand("aniso",
                false)) {
            anisoLevel += .25f;
            if (anisoLevel > 1.0) {
                anisoLevel = 0;
            }
            display.setTitle("Anisotropic Demo - Aniso "+(anisoLevel*100)+"% - press 'f' to switch");
            texture.setAnisotropicFilterPercent(anisoLevel);
        }
    }

    /**
     * builds the trimesh.
     * 
     * @see com.jme.app.SimpleGame#initGame()
     */
    protected void simpleInitGame() {
        display.setTitle("Anisotropic Demo - Aniso 0% - press 'f' to switch");
        KeyBindingManager.getKeyBindingManager().set("aniso", KeyInput.KEY_F);
        cam.setLocation(new Vector3f(0, 10, 100));
        cam.update();

        q = new Quad("Quad", 200, 200);
        q.setModelBound(new BoundingSphere());
        q.updateModelBound();
        q.setLocalRotation(new Quaternion(new float[] {
                90 * FastMath.DEG_TO_RAD, 0, 0 }));
        q.setLightCombineMode(LightCombineMode.Off);

        FloatBuffer tBuf = q.getTextureCoords(0).coords;
        tBuf.clear();
        tBuf.put(0).put(5);
        tBuf.put(0).put(0);
        tBuf.put(5).put(0);
        tBuf.put(5).put(5);

        rootNode.attachChild(q);

        ts = display.getRenderer().createTextureState();
        ts.setEnabled(true);
        texture = TextureManager.loadTexture(TestAnisotropic.class
                .getClassLoader().getResource("jmetest/data/texture/dirt.jpg"),
                Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear, 0, true);
        texture.setWrap(Texture.WrapMode.Repeat);

        ts.setTexture(texture);
        rootNode.setRenderState(ts);

        lightState.setTwoSidedLighting(true);
    }
}
