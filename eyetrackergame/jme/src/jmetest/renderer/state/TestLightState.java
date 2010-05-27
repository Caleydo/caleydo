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

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingSphere;
import com.jme.image.Texture;
import com.jme.light.DirectionalLight;
import com.jme.light.SpotLight;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Pyramid;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;

/**
 * <code>TestLightState</code>
 * @author Mark Powell
 * @version $Id: TestLightState.java 4130 2009-03-19 20:04:51Z blaine.dev $
 */
public class TestLightState extends SimpleGame {
    private TriMesh t;

    /**
     * Entry point for the test,
     * @param args
     */
    public static void main(String[] args) {
        TestLightState app = new TestLightState();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }

    /**
     * builds the trimesh.
     * @see com.jme.app.SimpleGame#initGame()
     */
    protected void simpleInitGame() {
        display.setTitle("Light State Test");
        cam.setLocation(new Vector3f(-10,0,40));
        cam.update();

        Vector3f max = new Vector3f(10,10,10);
        Vector3f min = new Vector3f(0,0,0);

        t = new Box("Box", min,max);
        t.setModelBound(new BoundingSphere());
        t.updateModelBound();

        Pyramid t2 = new Pyramid("Pyramid", 10,20);
        t2.setModelBound(new BoundingSphere());
        t2.updateModelBound();

        t.setLocalTranslation(new Vector3f(0,0,-10));

        t2.setLocalTranslation(new Vector3f(-20,0,0));

        rootNode.attachChild(t);
        rootNode.attachChild(t2);

        SpotLight sp1 = new SpotLight();
        sp1.setDiffuse(new ColorRGBA(0.0f, 1.0f, 0.0f, 1.0f));
        sp1.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
        sp1.setDirection(new Vector3f(-1, -0.5f, 0));
        sp1.setLocation(new Vector3f(25, 10, 0));
        sp1.setAngle(15);
        sp1.setEnabled(true);

        SpotLight sp2 = new SpotLight();
        sp2.setDiffuse(new ColorRGBA(1.0f, 0.0f, 0.0f, 1.0f));
        sp2.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
        sp2.setDirection(new Vector3f(1, -0.5f, 0));
        sp2.setLocation(new Vector3f(-25, 10, 0));
        sp2.setAngle(15);
        sp2.setEnabled(true);

        DirectionalLight dr = new DirectionalLight();
        dr.setDiffuse(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
        dr.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
        dr.setSpecular(new ColorRGBA(1.0f, 0.0f, 0.0f, 1.0f));
        dr.setDirection(new Vector3f(150, 0 , 150));
        dr.setEnabled(true);

        lightState.detachAll();
        lightState.attach(sp1);
        lightState.attach(dr);
        lightState.attach(sp2);

        TextureState ts = display.getRenderer().createTextureState();
                ts.setEnabled(true);
                ts.setTexture(
                    TextureManager.loadTexture(
                        TestLightState.class.getClassLoader().getResource("jmetest/data/images/Monkey.jpg"),
                        Texture.MinificationFilter.Trilinear,
                        Texture.MagnificationFilter.Bilinear));

        rootNode.setRenderState(ts);
    }
}
