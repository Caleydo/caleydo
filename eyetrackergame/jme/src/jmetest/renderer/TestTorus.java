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
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.light.DirectionalLight;
import com.jme.light.SpotLight;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.shape.Torus;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;

/**
 * <code>TestTorus</code>
 * 
 * @author Mark Powell
 * @version $Id: TestTorus.java 4130 2009-03-19 20:04:51Z blaine.dev $
 */
public class TestTorus extends SimpleGame {
    
    private Quaternion rotQuat = new Quaternion();
    private float angle = 0;
    private Vector3f axis = new Vector3f(1, 1, 0);
    private Torus t;

    /**
     * Entry point for the test,
     * 
     * @param args
     */
    public static void main(String[] args) {
        TestTorus app = new TestTorus();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }

    protected void simpleUpdate() {
        if (tpf < 1) {
            angle = angle + (tpf * 25);
            if (angle > 360) {
                angle = 0;
            }
        }

        rotQuat.fromAngleNormalAxis(angle * FastMath.DEG_TO_RAD, axis);
        t.setLocalRotation(rotQuat);
    }

    protected void simpleInitGame() {

        Node scene = new Node("scene");

        t = new Torus("Torus", 50, 50, 5, 10);
        t.setModelBound(new BoundingBox());
        t.updateModelBound();

        scene.attachChild(t);

        TextureState ts = display.getRenderer().createTextureState();
        ts.setEnabled(true);
        ts.setTexture(TextureManager.loadTexture(TestBoxColor.class
                .getClassLoader().getResource("jmetest/data/texture/dirt.jpg"),
                Texture.MinificationFilter.Trilinear,
                Texture.MagnificationFilter.Bilinear));

        scene.setRenderState(ts);

        SpotLight am = new SpotLight();
        am.setDiffuse(new ColorRGBA(0.0f, 0.0f, 1.0f, 1.0f));
        am.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
        am.setDirection(new Vector3f(-250, -100, 0));
        am.setLocation(new Vector3f(250, 100, 0));
        am.setAngle(1);

        SpotLight am2 = new SpotLight();
        am2.setDiffuse(new ColorRGBA(1.0f, 0.0f, 0.0f, 1.0f));
        am2.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
        am2.setDirection(new Vector3f(250, -10, 0));
        am2.setLocation(new Vector3f(-250, 10, 0));
        am2.setAngle(1);

        DirectionalLight dr = new DirectionalLight();
        dr.setDiffuse(new ColorRGBA(0.25f, 0.75f, 0.25f, 1.0f));
        dr.setAmbient(new ColorRGBA(0.25f, 0.25f, 0.25f, 1.0f));
        // dr.setSpecular(new ColorRGBA(1.0f, 0.0f, 0.0f, 1.0f));
        dr.setDirection(new Vector3f(150, 0, 150));

        lightState.detachAll();
        lightState.attach(am);
        lightState.attach(dr);
        lightState.attach(am2);
        am.setEnabled(true);
        am2.setEnabled(true);
        dr.setEnabled(true);
        scene.setRenderState(lightState);
        rootNode.attachChild(scene);
    }

}
