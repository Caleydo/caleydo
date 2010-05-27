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
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.shape.Teapot;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;

/**
 * <code>TestTeapot</code>
 * 
 * @author Joshua Slack
 * @version $Id: TestTeapot.java 4130 2009-03-19 20:04:51Z blaine.dev $
 */
public class TestTeapot extends SimpleGame {

    private Quaternion rotQuat = new Quaternion();
    private float angle = 0;
    private Vector3f axis = new Vector3f(0, 1, 0);
    private Teapot t;

    public static void main(String[] args) {
        TestTeapot app = new TestTeapot();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }

    protected void simpleUpdate() {
        if (tpf < 1) {
            angle = angle + tpf * 25;
            if (angle > 360)
                angle = 0;
        }

        rotQuat.fromAngleAxis(angle * FastMath.DEG_TO_RAD, axis);

        t.setLocalRotation(rotQuat);
    }

    protected void simpleInitGame() {
        display.setTitle("Teapot Test");
        cam.setLocation(new Vector3f(0, 2, 10));
        cam.update();

        t = new Teapot("Teapot");
        t.setModelBound(new BoundingBox());
        t.updateModelBound();

        rootNode.attachChild(t);

        TextureState ts = display.getRenderer().createTextureState();
        // Base texture, not environmental map.
        Texture t0 = TextureManager.loadTexture(
                TestTeapot.class.getClassLoader().getResource(
                        "jmetest/data/images/Monkey.jpg"),
                Texture.MinificationFilter.Trilinear,
                Texture.MagnificationFilter.Bilinear);
        // Environmental Map (reflection of clouds)
        Texture t = TextureManager.loadTexture(TestTeapot.class
                .getClassLoader()
                .getResource("jmetest/data/texture/clouds.png"),
                Texture.MinificationFilter.Trilinear,
                Texture.MagnificationFilter.Bilinear);
        t.setEnvironmentalMapMode(Texture.EnvironmentalMapMode.SphereMap);
        ts.setTexture(t0, 0);
        ts.setTexture(t, 1);
        ts.setEnabled(true);

        // rootNode.setRenderState(ts);

        lightState.setTwoSidedLighting(true);
        lightState.get(0).setDiffuse(ColorRGBA.white.clone());
    }
}
