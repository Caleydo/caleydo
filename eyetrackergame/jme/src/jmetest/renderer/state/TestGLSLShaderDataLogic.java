/*
 * Copyright (c) 2003-2009 jMonkeyEngine All rights reserved. Redistribution and
 * use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met: * Redistributions of source
 * code must retain the above copyright notice, this list of conditions and the
 * following disclaimer. * Redistributions in binary form must reproduce the
 * above copyright notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the distribution. *
 * Neither the name of 'jMonkeyEngine' nor the names of its contributors may be
 * used to endorse or promote products derived from this software without
 * specific prior written permission. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT
 * HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package jmetest.renderer.state;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.SharedMesh;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.CullState;
import com.jme.scene.state.GLSLShaderDataLogic;
import com.jme.scene.state.GLSLShaderObjectsState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.TextureManager;

/**
 * <code>TestGLSLShaderDataLogic</code>
 * 
 * @author rherlitz
 * @version $Id: TestGLSLShaderDataLogic.java,v 1.1 2007/08/14 10:32:15 rherlitz
 *          Exp $
 */
public class TestGLSLShaderDataLogic extends SimpleGame {
    private static final Logger logger = Logger
            .getLogger(TestGLSLShaderDataLogic.class.getName());

    /**
     * Entry point for the test,
     * 
     * @param args
     */
    public static void main(String[] args) {
        TestGLSLShaderDataLogic app = new TestGLSLShaderDataLogic();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }

    /**
     * builds the trimesh.
     * 
     * @see com.jme.app.SimpleGame#initGame()
     */
    protected void simpleInitGame() {
        display.setTitle("jME - TestGLSLShaderDataLogic");

        Sphere s = new Sphere("Sphere", 8, 8, 10);
        s.setModelBound(new BoundingBox());
        s.updateModelBound();

        CullState cs = display.getRenderer().createCullState();
        cs.setCullFace(CullState.Face.Back);
        cs.setEnabled(true);
        rootNode.setRenderState(cs);

        TextureState ts = display.getRenderer().createTextureState();
        ts.setEnabled(true);
        ts.setTexture(TextureManager.loadTexture(
                TestGLSLShaderDataLogic.class.getClassLoader().getResource(
                        "jmetest/data/images/Monkey.jpg"),
                Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear));

        Node n1 = new Node("n1");
        n1.setLocalTranslation(new Vector3f(0, 0, 0));
        n1.setRenderState(createShader());

        rootNode.attachChild(n1);

        for (int i = 0; i < 100; i++) {
            SharedMesh sm = new SharedMesh("Share" + i, s);

            sm.setLocalTranslation(new Vector3f(
                    (float) Math.random() * 500 - 250,
                    (float) Math.random() * 500 - 250,
                    (float) Math.random() * 500 - 250));
            sm.setRenderState(ts);
            n1.attachChild(sm);
        }

        rootNode.updateRenderState();
    }

    private GLSLShaderObjectsState createShader() {

        // Check is GLSL is supported on current hardware.
        if (!GLSLShaderObjectsState.isSupported()) {
            logger
                    .severe("Your graphics card does not support GLSL programs, and thus cannot run this test.");
            quit();
        }

        GLSLShaderObjectsState so = display.getRenderer()
                .createGLSLShaderObjectsState();

        try {
            so
                    .load(
                            TestGLSLShaderObjectsState.class
                                    .getClassLoader()
                                    .getResource(
                                            "jmetest/data/images/datalogicshader.vert"),
                            TestGLSLShaderObjectsState.class
                                    .getClassLoader()
                                    .getResource(
                                            "jmetest/data/images/datalogicshader.frag"));
            so.apply();
            DisplaySystem.getDisplaySystem().getRenderer().checkCardError();
        } catch (JmeException e) {
            logger.log(Level.WARNING, "Error loading shader", e);
            quit();
        }

        so.setUniform("baseTexture", 0);
        so.setUniform("positionOffset", 1.0f, 1.0f, 1.0f);

        so.setShaderDataLogic(new GLSLShaderDataLogic() {
            public void applyData(GLSLShaderObjectsState shader, Geometry geom) {
                shader.setUniform("positionOffset", geom
                        .getWorldTranslation());
            }
        });

        so.setEnabled(true);

        return so;
    }
}
