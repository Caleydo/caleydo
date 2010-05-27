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

import java.util.logging.Logger;

import com.jme.app.SimpleGame;
import com.jme.input.NodeHandler;
import com.jme.math.FastMath;
import com.jme.math.Matrix3f;
import com.jme.math.Matrix4f;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.GLSLShaderObjectsState;

/**
 * Tests GLSL uniform functionality. Nothing visual, just making sure that all
 * inputs work.
 *
 * @author Rikard Herlitz (MrCoder)
 */
public class TestGLSLShaderVariables extends SimpleGame {
    private static final Logger logger = Logger
            .getLogger(TestGLSLShaderVariables.class.getName());
    
    /** GLSLShader handle for updating uniform in simpleUpdate loop */
    private GLSLShaderObjectsState so;

    public static void main(String[] args) {
        TestGLSLShaderVariables app = new TestGLSLShaderVariables();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }

    protected void simpleInitGame() {
        display.setTitle("Test GLSL variables");

        cam.setLocation(new Vector3f(0, 0, 2));
        cam.update();
        input = new NodeHandler(rootNode, 10, 2);

        Quad brick = createBrickQuad();
        rootNode.attachChild(brick);

        rootNode.updateRenderState();
    }


    protected void simpleUpdate() {
        so.setUniform("floatVal",
                FastMath.sin(timer.getTimeInSeconds()) * 0.5f + 0.5f);
    }

    private Quad createBrickQuad() {

        // Check is GLSL is supported on current hardware.
        if (!GLSLShaderObjectsState.isSupported()) {
            logger.severe("Your graphics card does not support GLSL programs, and thus cannot run this test.");
            quit();
        }

        so = display.getRenderer().createGLSLShaderObjectsState();

        so.load(TestGLSLShaderVariables.class.getClassLoader().getResource(
                "jmetest/data/images/fullshader.vert"),
                TestGLSLShaderVariables.class.getClassLoader().getResource(
                        "jmetest/data/images/fullshader.frag"));

        so.setUniform("floatVal", 0.0f);
        so.setUniform("vec2Val", 0.0f, 0.0f);
        so.setUniform("vec3Val", 0.0f, 0.0f, 0.0f);
        so.setUniform("vec4Val", 0.0f, 0.0f, 0.0f, 0.0f);
        so.setUniform("intVal", 0);
        so.setUniform("ivec2Val", 0, 0);
        so.setUniform("ivec3Val", 0, 0, 0);
        so.setUniform("ivec4Val", 0, 0, 0, 0);
        so.setUniform("boolVal", false);
        so.setUniform("bvec2Val", false, false);
        so.setUniform("bvec3Val", false, false, false);
        so.setUniform("bvec4Val", false, false, false, false);
        so.setUniform("mat2Val", new float[]{0.0f, 0.0f, 0.0f, 0.0f}, false);
        so.setUniform("mat3Val", new Matrix3f(0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 0.0f), false);
        so.setUniform("mat4Val", new Matrix4f(0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
                0.0f), false);
        so.setEnabled(true);

        //Generate the torus
        Quad box = new Quad("glslQuad", 1f, 1f);
        box.setRenderState(so);

        return box;
    }
}
