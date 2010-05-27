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

import java.nio.FloatBuffer;

import com.jme.app.SimpleGame;
import com.jme.input.NodeHandler;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.GLSLShaderObjectsState;
import com.jme.util.geom.BufferUtils;

/**
 * Tests GLSL shader attributes functionality
 * 
 * @author Rikard Herlitz (MrCoder)
 */
public class TestGLSLShaderAttributes extends SimpleGame {
    private ColorQuad quad0, quad1, quad2, quad3;

    public static void main(String[] args) {
        TestGLSLShaderAttributes app = new TestGLSLShaderAttributes();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }

    protected void simpleInitGame() {
        display.setTitle("Test GLSL attributes");

        cam.setLocation(new Vector3f(0, 0, 2));
        cam.update();
        input = new NodeHandler(rootNode, 10, 2);

        quad0 = new ColorQuad(ColorRGBA.red.clone(), 1.0f);
        rootNode.attachChild(quad0);
        quad0.getLocalTranslation().x -= 0.5f;
        quad0.getLocalTranslation().y -= 0.5f;

        quad1 = new ColorQuad(ColorRGBA.green.clone(), 0.7f);
        rootNode.attachChild(quad1);
        quad1.getLocalTranslation().x += 0.5f;
        quad1.getLocalTranslation().y -= 0.5f;

        quad2 = new ColorQuad(ColorRGBA.blue.clone(), 0.5f);
        rootNode.attachChild(quad2);
        quad2.getLocalTranslation().x -= 0.5f;
        quad2.getLocalTranslation().y += 0.5f;

        quad3 = new ColorQuad(ColorRGBA.orange.clone(), 1.1f);
        rootNode.attachChild(quad3);
        quad3.getLocalTranslation().x += 0.5f;
        quad3.getLocalTranslation().y += 0.5f;

        rootNode.updateRenderState();
    }

    protected void simpleUpdate() {
        quad0.update(0.5f);
        quad1.update(1.0f);
        quad2.update(3.0f);
        quad3.update(5.0f);
    }

    private class ColorQuad extends Quad {
        private static final long serialVersionUID = 1L;
        /** Shader attribute buffer for vertex colors */
        private FloatBuffer vertexColors;
        /** Shader attribute buffer for amount of offset to normal */
        private FloatBuffer vertexOffset;

        public ColorQuad(ColorRGBA color, float size) {
            super("glslQuad", 1f, 1f);

            // Check is GLSL is supported on current hardware.
            if (!GLSLShaderObjectsState.isSupported()) {
                quit();
            }

            GLSLShaderObjectsState so = display.getRenderer()
                    .createGLSLShaderObjectsState();

            setDefaultColor(color);

            so.load(TestGLSLShaderAttributes.class
                            .getClassLoader()
                            .getResource(
                                    "jmetest/data/images/attributeshader.vert"),
                    TestGLSLShaderAttributes.class
                            .getClassLoader()
                            .getResource(
                                    "jmetest/data/images/attributeshader.frag"));

            vertexColors = BufferUtils.createFloatBuffer(16);
            for (int i = 0; i < 4; i++) {
                vertexColors.put(color.r);
                vertexColors.put(color.g);
                vertexColors.put(color.b);
                vertexColors.put(color.a);
            }
            so.setAttributePointer("vertexColors", 4, true, 0, vertexColors);

            vertexOffset = BufferUtils.createFloatBuffer(4);
            so.setAttributePointer("vertexOffset", 1, true, 0, vertexOffset);

            so.setUniform("size", size);

            so.setEnabled(true);

            setRenderState(so);
        }

        public void update(float speed) {
            vertexOffset.rewind();
            vertexOffset.put(
                            FastMath.sin(timer.getTimeInSeconds() * speed) * 0.5f + 0.5f)
                        .put(
                            FastMath.sin(timer.getTimeInSeconds() * speed
                                    + 1.0f) * 0.5f + 0.5f).put(
                            FastMath.sin(timer.getTimeInSeconds() * speed
                                    + 2.0f) * 0.5f + 0.5f).put(
                            FastMath.sin(timer.getTimeInSeconds() * speed
                                    + 3.0f) * 0.5f + 0.5f);
        }

    }
}