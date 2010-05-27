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
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.GLSLShaderObjectsState;

/**
 * @author Thomas Hourdel
 */
public class TestGLSLShaderObjectsState extends SimpleGame {
    private static final Logger logger = Logger
            .getLogger(TestGLSLShaderObjectsState.class.getName());

    public static void main(String[] args) {
        TestGLSLShaderObjectsState app = new TestGLSLShaderObjectsState();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }

    protected void simpleInitGame() {
        display.setTitle( "GLSL" );
        display.getRenderer().setBackgroundColor(
                new ColorRGBA( 0.0f, 0.0f, 0.0f, 0.0f ) );

        cam.setLocation( new Vector3f( 0, 0, 2 ) );
        cam.update();
        input = new NodeHandler( rootNode, 10, 2 );

        Quad brick = createBrickQuad();
        rootNode.attachChild( brick );

        rootNode.updateRenderState();
    }

    private Quad createBrickQuad() {

        // Check is GLSL is supported on current hardware.
        if (!GLSLShaderObjectsState.isSupported()) {
            logger.severe("Your graphics card does not support GLSL programs, and thus cannot run this test.");
            quit();
        }

        GLSLShaderObjectsState so = display.getRenderer()
                .createGLSLShaderObjectsState();

        so.load(TestGLSLShaderObjectsState.class.getClassLoader().getResource(
                "jmetest/data/images/shader.vert"),
                TestGLSLShaderObjectsState.class.getClassLoader().getResource(
                        "jmetest/data/images/shader.frag"));
        so.setUniform("BrickColor", 1.0f, 0.3f, 0.2f);
        so.setUniform("MortarColor", 0.85f, 0.86f, 0.84f);
        so.setUniform("BrickSize", 0.30f, 0.15f);
        so.setUniform("BrickPct", 0.90f, 0.85f);
        so.setUniform("LightPosition", 0.0f, 0.0f, 4.0f);
        so.setEnabled(true);

        //Generate the torus
        Quad box = new Quad("glslQuad", 1f, 1f);
        box.setRenderState(so);

        return box;
    }
}