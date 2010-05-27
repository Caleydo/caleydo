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

import com.jme.app.SimpleGame;
import com.jme.image.Texture;
import com.jme.input.NodeHandler;
import com.jme.light.PointLight;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.shape.Quad;
import com.jme.scene.shape.Torus;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;

/**
 * <code>TestEnvMap</code>
 * 
 * @author Mark Powell
 * @version $Id: TestEnvMap.java 4130 2009-03-19 20:04:51Z blaine.dev $
 */
public class TestEnvMap extends SimpleGame {

    public static void main(String[] args) {
        TestEnvMap app = new TestEnvMap();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }

    protected void simpleInitGame() {
        display.setTitle("Environmental Maps");

        cam.setLocation(new Vector3f(0, 0, 100));
        cam.update();

        Torus torus = new Torus("Torus", 50, 50, 10, 20);

        Quad background = new Quad("Background");
        background.updateGeometry(150, 120);
        background.setLocalTranslation(new Vector3f(0, 0, -30));

        Texture bg = TextureManager.loadTexture(TestEnvMap.class
                .getClassLoader()
                .getResource("jmetest/data/texture/clouds.png"),
                Texture.MinificationFilter.BilinearNearestMipMap,
                Texture.MagnificationFilter.Bilinear);
        TextureState bgts = display.getRenderer().createTextureState();
        bgts.setTexture(bg);
        bgts.setEnabled(true);
        background.setRenderState(bgts);

        TextureState ts = display.getRenderer().createTextureState();
        // Base texture, not environmental map.
        Texture t0 = TextureManager.loadTexture(
                TestEnvMap.class.getClassLoader().getResource(
                        "jmetest/data/images/Monkey.jpg"),
                Texture.MinificationFilter.Trilinear,
                Texture.MagnificationFilter.Bilinear);
        // Environmental Map (reflection of clouds)
        Texture t = TextureManager.loadTexture(TestEnvMap.class
                .getClassLoader()
                .getResource("jmetest/data/texture/clouds.png"),
                Texture.MinificationFilter.Trilinear,
                Texture.MagnificationFilter.Bilinear);
        t.setEnvironmentalMapMode(Texture.EnvironmentalMapMode.SphereMap);
        ts.setTexture(t0, 0);
        ts.setTexture(t, 1);
        ts.setEnabled(true);

        PointLight pl = new PointLight();
        pl.setAmbient(new ColorRGBA(0.75f, 0.75f, 0.75f, 1));
        pl.setDiffuse(new ColorRGBA(1, 0, 0, 1));
        pl.setLocation(new Vector3f(50, 0, 0));
        pl.setEnabled(true);

        lightState.attach(pl);

        torus.setRenderState(ts);
        rootNode.attachChild(torus);
        rootNode.attachChild(background);

        input = new NodeHandler(torus, 10, 2);
    }
}
