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

package jmetest.awt.applet;

import java.nio.FloatBuffer;
import java.util.logging.Logger;

import com.jme.image.Texture;
import com.jme.light.PointLight;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Spatial;
import com.jme.scene.TexCoords;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.CullState;
import com.jme.scene.state.FragmentProgramState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.VertexProgramState;
import com.jme.util.TextureManager;
import com.jme.util.geom.BufferUtils;
import com.jmex.awt.applet.SimpleJMEApplet;

public class AppletTestShaderBumpMapping extends SimpleJMEApplet {
    private static final Logger logger = Logger
            .getLogger(AppletTestShaderBumpMapping.class.getName());
    
    private static final long serialVersionUID = 1L;
    private final static String BRICK_TEX = "jmetest/data/images/rockwall2.jpg";
    private final static String BRICK_HEIGHT = "jmetest/data/images/rockwall_height2.jpg";
    private final static String BRICK_NRML = "jmetest/data/images/rockwall_normal2.jpg";
    private final static String BRICK_VP = "jmetest/data/images/bump_parallax.vp";
    private final static String BRICK_FP = "jmetest/data/images/bump_parallax.fp";
    
    private float angle0 = 0.0f, angle1 = 0.0f;
    
    public void simpleAppletSetup() {
        // Set up cull state
        CullState cs = getRenderer().createCullState();
        cs.setCullFace(CullState.Face.Back);
        cs.setEnabled(true);

        // Basic brick texture
        TextureState brick = getRenderer().createTextureState();

        Texture tex = TextureManager.loadTexture(AppletTestShaderBumpMapping.class
                .getClassLoader().getResource(BRICK_TEX),
                Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear);
        tex.setWrap(Texture.WrapMode.Repeat);

        // Height map of the brick wall
        Texture height = TextureManager.loadTexture(
                AppletTestShaderBumpMapping.class.getClassLoader().getResource(
                        BRICK_HEIGHT), Texture.MinificationFilter.Trilinear,
                Texture.MagnificationFilter.Bilinear);
        height.setWrap(Texture.WrapMode.Repeat);

        // Normal map of the brick wall
        Texture normal = TextureManager.loadTexture(
                AppletTestShaderBumpMapping.class.getClassLoader().getResource(
                        BRICK_NRML), Texture.MinificationFilter.Trilinear,
                Texture.MagnificationFilter.Bilinear);
        normal.setWrap(Texture.WrapMode.Repeat);

        brick.setTexture(tex, 0);
        brick.setTexture(normal, 1);
        brick.setTexture(height, 2);

        brick.setEnabled(true);

        VertexProgramState vert = getRenderer()
                .createVertexProgramState();
        FragmentProgramState frag = getRenderer()
                .createFragmentProgramState();
        // Ensure the extensions are supported, else exit immediately
        if (!vert.isSupported() || !frag.isSupported()) {
            logger.severe("Your graphics card does not support vertex or fragment programs, and thus cannot run this test.");
            destroy();
        }

        // Load vertex program
        vert.load(AppletTestShaderBumpMapping.class.getClassLoader().getResource(
                BRICK_VP));
        vert.setEnabled(true);

        // Load fragment program
        frag.load(AppletTestShaderBumpMapping.class.getClassLoader().getResource(
                BRICK_FP));
        frag.setEnabled(true);

        Quad q = new Quad("wall", 10f, 10f);

        // Set up textures
        q.setRenderState(brick);

        FloatBuffer tex1 = BufferUtils.createVector2Buffer(4);
        for (int x = 0; x < 4; x++)
            tex1.put(1.0f).put(0.0f);
        q.setTextureCoords(new TexCoords(tex1), 1);

        FloatBuffer tex2 = BufferUtils.createVector2Buffer(4);
        for (int x = 0; x < 4; x++)
            tex2.put(0.0f).put(1.0f);
        q.setTextureCoords(new TexCoords(tex2), 2);

        // Set up ARB programs
        q.setRenderState(vert);
        q.setRenderState(frag);

        q.setRenderState(cs);

        initLights();

        getRootNode().attachChild(q);
        getRootNode().setCullHint(Spatial.CullHint.Never);
    }
    
    private void initLights( ) {
        //Set up two lights in the scene
        PointLight light0 = new PointLight();
        light0.setDiffuse(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
        light0.setLocation(new Vector3f(2f, 4f, 1f));
        light0.setEnabled(true);
        
        PointLight light1 = new PointLight();
        light1.setDiffuse(new ColorRGBA(1.0f, 0.5f, 0.0f, 1.0f));
        light1.setLocation(new Vector3f(2f, 2f, 1f));
        light1.setEnabled(true);
        LightState lightState = getLightState();
        lightState.detachAll();
        lightState.setEnabled(true);
        lightState.attach(light0);
        lightState.attach(light1);
        lightState.setGlobalAmbient(new ColorRGBA(0,0,0,1));
        getRootNode().setRenderState(lightState);        
    }
    
    @Override
    public void simpleAppletUpdate() {
        angle0 += 2 * getTimePerFrame();
        angle1 += 4 * getTimePerFrame();
        
        ((PointLight)getLightState().get(0)).setLocation(new Vector3f(2.0f * FastMath.cos(angle0), 2.0f * FastMath.sin(angle0), 1.5f));
        ((PointLight)getLightState().get(1)).setLocation(new Vector3f(2.0f * FastMath.cos(angle1), 2.0f * FastMath.sin(angle1), 1.5f));
    }
}
