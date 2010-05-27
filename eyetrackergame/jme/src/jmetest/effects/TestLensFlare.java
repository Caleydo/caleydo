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

package jmetest.effects;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.light.LightNode;
import com.jme.light.PointLight;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Spatial.LightCombineMode;
import com.jme.scene.shape.Box;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.jmex.effects.LensFlare;
import com.jmex.effects.LensFlareFactory;

/**
 * <code>TestLensFlare</code> Test of the lens flare effect in jME. Notice
 * that currently it doesn't do occlusion culling.
 * 
 * @author Joshua Slack
 * @version $Id: TestLensFlare.java 4130 2009-03-19 20:04:51Z blaine.dev $
 */
public class TestLensFlare extends SimpleGame {

    private LightNode lightNode;
    LensFlare flare;

    public static void main(String[] args) {
        TestLensFlare app = new TestLensFlare();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }

    protected void simpleInitGame() {

        display.setTitle("Lens Flare!");
        cam.setLocation(new Vector3f(0.0f, 0.0f, 200.0f));
        cam.update();
        lightState.detachAll();

        PointLight dr = new PointLight();
        dr.setEnabled(true);
        dr.setDiffuse(ColorRGBA.white.clone());
        dr.setAmbient(ColorRGBA.gray.clone());
        dr.setLocation(new Vector3f(0f, 0f, 0f));

        lightState.attach(dr);
        lightState.setTwoSidedLighting(true);

        lightNode = new LightNode("light");
        lightNode.setLight(dr);

        Vector3f min2 = new Vector3f(-0.5f, -0.5f, -0.5f);
        Vector3f max2 = new Vector3f(0.5f, 0.5f, 0.5f);
        Box lightBox = new Box("box", min2, max2);
        lightBox.setModelBound(new BoundingBox());
        lightBox.updateModelBound();
        lightNode.attachChild(lightBox);
        lightNode.setLocalTranslation(new Vector3f(-14f, 14f, -14f));

        Box box2 = new Box("blocker", new Vector3f(-5, -5, -5), new Vector3f(5,
                5, 5));
        box2.setModelBound(new BoundingBox());
        box2.updateModelBound();
        box2.setLocalTranslation(new Vector3f(100, 0, 0));
        rootNode.attachChild(box2);

        // clear the lights from this lightbox so the lightbox itself doesn't
        // get affected by light:
        lightBox.setLightCombineMode(LightCombineMode.Off);

        // Setup the lensflare textures.
        TextureState[] tex = new TextureState[4];
        tex[0] = display.getRenderer().createTextureState();
        tex[0].setTexture(TextureManager.loadTexture(LensFlare.class
                .getClassLoader()
                .getResource("jmetest/data/texture/flare1.png"),
                Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear, Image.Format.RGBA8,
                0.0f, true));
        tex[0].setEnabled(true);

        tex[1] = display.getRenderer().createTextureState();
        tex[1].setTexture(TextureManager.loadTexture(LensFlare.class
                .getClassLoader()
                .getResource("jmetest/data/texture/flare2.png"),
                Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear));
        tex[1].setEnabled(true);

        tex[2] = display.getRenderer().createTextureState();
        tex[2].setTexture(TextureManager.loadTexture(LensFlare.class
                .getClassLoader()
                .getResource("jmetest/data/texture/flare3.png"),
                Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear));
        tex[2].setEnabled(true);

        tex[3] = display.getRenderer().createTextureState();
        tex[3].setTexture(TextureManager.loadTexture(LensFlare.class
                .getClassLoader()
                .getResource("jmetest/data/texture/flare4.png"),
                Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear));
        tex[3].setEnabled(true);

        flare = LensFlareFactory.createBasicLensFlare("flare", tex);
        flare.setRootNode(rootNode);
        //lightNode.attachChild(flare);
        Box box = new Box("my box", new Vector3f(0, 0, 0), 10, 10, 10);
        box.setModelBound(new BoundingBox());
        box.updateModelBound();
        rootNode.attachChild(box);
        rootNode.attachChild(lightNode);

        // notice that it comes at the end
        lightNode.attachChild(flare);

    }

}
