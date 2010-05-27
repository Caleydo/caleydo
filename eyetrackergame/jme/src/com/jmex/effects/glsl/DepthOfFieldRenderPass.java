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
package com.jmex.effects.glsl;

import java.net.URL;

import com.jme.image.Texture;
import com.jme.image.Texture2D;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.renderer.TextureRenderer;
import com.jme.renderer.pass.Pass;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.GLSLShaderObjectsState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;

/**
 * GLSL Depth of Field effect pass. - Creating a depth texture with a root
 * Spatial and its subspatials - Use it on full screen texture downsampled to
 * blur it with stronger opacity and blurring on far away parts - render result
 * (unblended) on the screen overwriting with the blurred parts.
 * 
 * @author Paul Illes - initial implementation of DepthOfFieldRenderPass for jME
 *         1.0 based on partially MrCorder's shaders plus : about original Ogre
 *         DoF demo: "Depth of Field" demo for Ogre Copyright (C) 2006 Christian
 *         Lindequist Larsen This code is in the public domain. You may do
 *         whatever you want with it. - Used from that part the depth shader
 *         with some modifications.
 * 
 * @author (MrCoder) - initial implementation of BloomRenderPass (original pass)
 * @author Joshua Slack - Enhancements and reworking to use a single
 *         texrenderer, ability to reuse existing back buffer, faster blur,
 *         throttling speed-up, etc.
 */
public class DepthOfFieldRenderPass extends Pass {
    private static final long serialVersionUID = 1L;

    /** The time between texture updates */
    private float throttle = 0.001f;
    /** The amount of time since the last update */
    private float sinceLast = 1;

    /** The renderer used to produce the DoF texture */
    private TextureRenderer tRenderer;
    /** The final resulting texture to be splatted across the screen */
    private Texture2D resultTexture;
    /** The texture storing the depth values to work out how much to blur by */
    private Texture2D depthTexture;
    /** The copy of the screen */
    private Texture2D screenTexture;

    /**
     * The full scren quad used to display the generated texture across the
     * screen
     */
    private Quad fullScreenQuad;

    /** Shader to render the final texture */
    private GLSLShaderObjectsState finalShader;
    /** Shader to record depth in to a texture */
    private GLSLShaderObjectsState depthShader;
    /** Shader to provide bluring based on depth */
    private GLSLShaderObjectsState dofShader;

    /** The size of the blur kernel */
    private float blurSize;

    /** The depth at which blur starts */
    public float nearBlurDepth = 10f;
    /** The depth at which the viewer is focussing */
    public float focalPlaneDepth = 25f;
    /** The depth at which we're at full blur */
    public float farBlurDepth = 50f;
    /** The cut off point where we stop blurring */
    public float blurrinessCutoff = 1f;

    /** True if this pass is supported */
    private boolean supported = true;

    /** The location where we'll find our shaders - relative to this class */
    private static String shaderDirectory = "data/";

    /**
     * A place to internally save previous enforced states setup before
     * rendering this pass
     */
    private RenderState[] preStates = new RenderState[RenderState.StateType.values().length];
    /** A utility to render the spatials added in one texture render */
    private final SpatialsRenderNode spatialsRenderNode = new SpatialsRenderNode();

    /**
     * Creates a new DOG renderpass
     * 
     * @param cam
     *            Camera used for rendering the bloomsource
     * @param renderScale
     *            Scale of bloom texture
     */
    public DepthOfFieldRenderPass(Camera cam, int renderScale) {
        DisplaySystem display = DisplaySystem.getDisplaySystem();
        resetParameters();

        // Create texture renderers and rendertextures(alternating between two
        // not to overwrite pbuffers)
        tRenderer = display.createTextureRenderer(display.getWidth()
                / renderScale, display.getHeight() / renderScale,
                TextureRenderer.Target.Texture2D);
        if (!tRenderer.isSupported()) {
            supported = false;
            return;
        }

        tRenderer.setBackgroundColor(new ColorRGBA(0.0f, 0.0f, 0.0f, 1.0f));
        tRenderer.setCamera(cam);

        screenTexture = new Texture2D();
        screenTexture.setWrap(Texture.WrapMode.Clamp);
        tRenderer.setupTexture(screenTexture);

        resultTexture = new Texture2D();
        resultTexture.setWrap(Texture.WrapMode.Clamp);
        resultTexture
                .setMagnificationFilter(Texture.MagnificationFilter.Bilinear);
        tRenderer.setupTexture(resultTexture);

        depthTexture = new Texture2D();
        depthTexture.setWrap(Texture.WrapMode.Clamp);
        tRenderer.setupTexture(depthTexture);

        if (!GLSLShaderObjectsState.isSupported()) {
            supported = false;
            return;
        }

        // Create final shader(basic texturing)
        finalShader = display.getRenderer().createGLSLShaderObjectsState();
        finalShader.load(getResource(shaderDirectory + "dof_fullscreen.vert"),
                getResource(shaderDirectory + "dof_fullscreen.frag"));
        finalShader.setEnabled(true);

        // DOF

        depthShader = display.getRenderer().createGLSLShaderObjectsState();
        depthShader.load(getResource(shaderDirectory + "dof_1_depth.vert"),
                getResource(shaderDirectory + "dof_1_depth.frag"));
        depthShader.setEnabled(true);

        // Create dof shader
        dofShader = display.getRenderer().createGLSLShaderObjectsState();
        dofShader.load(getResource(shaderDirectory + "dof_simple.vert"),
                getResource(shaderDirectory + "dof_3_dof_2.frag"));
        dofShader.setEnabled(true);

        // Create fullscreen quad
        fullScreenQuad = new Quad("FullScreenQuad", display.getWidth() / 4,
                display.getHeight() / 4);
        fullScreenQuad.getLocalRotation().set(0, 0, 0, 1);
        fullScreenQuad.getLocalTranslation().set(display.getWidth() / 2,
                display.getHeight() / 2, 0);
        fullScreenQuad.getLocalScale().set(1, 1, 1);
        fullScreenQuad.setRenderQueueMode(Renderer.QUEUE_ORTHO);

        fullScreenQuad.setCullHint(Spatial.CullHint.Never);
        fullScreenQuad
                .setTextureCombineMode(Spatial.TextureCombineMode.Replace);
        fullScreenQuad.setLightCombineMode(Spatial.LightCombineMode.Off);

        TextureState ts = display.getRenderer().createTextureState();
        ts.setEnabled(true);
        fullScreenQuad.setRenderState(ts);

        BlendState as = display.getRenderer().createBlendState();
        // no blending, result texture has to overwrite screen - not blend!
        as.setTestEnabled(true);
        as.setTestFunction(BlendState.TestFunction.GreaterThan);
        as.setEnabled(true);
        fullScreenQuad.setRenderState(as);

        fullScreenQuad.updateRenderState();
        fullScreenQuad.updateGeometricState(0.0f, true);
    }

    /**
     * Reset bloom parameters to default
     */
    public void resetParameters() {
        nearBlurDepth = 10f;
        focalPlaneDepth = 25f;
        farBlurDepth = 50f;
        blurrinessCutoff = 50f;
        blurSize = 0.005f;
    }

    /**
     * Release pbuffers in TextureRenderer's. Preferably called from user
     * cleanup method.
     */
    public void cleanup() {
        super.cleanUp();

        if (tRenderer != null) {
            tRenderer.cleanup();
        }
    }

    /**
     * Check if this pass is supported
     * 
     * @return True if this render pass is supported
     */
    public boolean isSupported() {
        return supported;
    }

    /**
     * Get a resource relative to this class
     * 
     * @param ref
     *            The reference to the resource
     * @return The URL to the resoure or null if there is none
     */
    private URL getResource(String ref) {
        return Thread.currentThread().getContextClassLoader().getResource(
                "com/jmex/effects/glsl/" + ref);
    }

    @Override
    protected void doUpdate(float tpf) {
        super.doUpdate(tpf);
        sinceLast += tpf;
    }

    /**
     * saves any states enforced by the user for replacement at the end of the
     * pass.
     */
    protected void saveEnforcedStates() {
        for (int x = RenderState.StateType.values().length; --x >= 0;) {
            preStates[x] = context.enforcedStateList[x];
        }
    }

    /**
     * replaces any states enforced by the user at the end of the pass.
     */
    protected void replaceEnforcedStates() {
        for (int x = RenderState.StateType.values().length; --x >= 0;) {
            context.enforcedStateList[x] = preStates[x];
        }
    }

    /**
     * @see com.jme.renderer.pass.Pass#doRender(com.jme.renderer.Renderer)
     */
    public void doRender(Renderer r) {
        if (spatials.size() == 0) {
            return;
        }

        BlendState as = (BlendState) fullScreenQuad.states[RenderState.StateType.Blend.ordinal()];
        TextureState ts = (TextureState) fullScreenQuad.states[RenderState.StateType.Texture.ordinal()];

        if (throttle < sinceLast) {
            sinceLast = 0;
            as.setEnabled(false);

            // rendering the screen
            tRenderer.copyToTexture(screenTexture, DisplaySystem
                    .getDisplaySystem().getWidth(), DisplaySystem
                    .getDisplaySystem().getHeight());

            // depth
            context.enforceState(depthShader);
            depthShader.setUniform("dofParams", nearBlurDepth, focalPlaneDepth,
                    farBlurDepth, blurrinessCutoff);
            depthShader.setUniform("mainTexture", 0);
            tRenderer.render(spatialsRenderNode, depthTexture); // depth texture
            replaceEnforcedStates();

            // dof
            dofShader.clearUniforms();
            dofShader.setUniform("scene", 0);
            dofShader.setUniform("depth", 1);
            dofShader.setUniform("sampleDist0", getBlurSize());
            fullScreenQuad.states[RenderState.StateType.GLSLShaderObjects.ordinal()] = dofShader;
            ts.setTexture(screenTexture, 0);
            ts.setTexture(depthTexture, 1);
            fullScreenQuad.setRenderState(ts);
            tRenderer.render(fullScreenQuad, resultTexture);

            // ts.setTexture(resultTexture,0);
            ts.setTexture(resultTexture, 0);
            ts.setTexture(null, 1);
        }

        // Final blend
        as.setEnabled(true);

        fullScreenQuad.states[RenderState.StateType.GLSLShaderObjects.ordinal()] = finalShader;
        r.draw(fullScreenQuad);
    }

    /**
     * @return The throttle amount - or in other words, how much time in seconds
     *         must pass before the bloom effect is updated.
     */
    public float getThrottle() {
        return throttle;
    }

    /**
     * @param throttle
     *            The throttle amount - or in other words, how much time in
     *            seconds must pass before the bloom effect is updated.
     */
    public void setThrottle(float throttle) {
        this.throttle = throttle;
    }

    /**
     * Get the size of the blur kernal
     * 
     * @return The size of the blur kernal
     */
    public float getBlurSize() {
        return blurSize;
    }

    /**
     * Set the size of the blur kernal
     * 
     * @param blurSize
     *            The size of the blur kernal
     */
    public void setBlurSize(float blurSize) {
        this.blurSize = blurSize;
    }

    /**
     * Get the depth at which blur starts
     * 
     * @return The depth at which blur starts
     */
    public float getNearBlurDepth() {
        return nearBlurDepth;
    }

    /**
     * Set the depth at which blur starts
     * 
     * @param nearBlurDepth
     *            The depth at which blur starts
     */
    public void setNearBlurDepth(float nearBlurDepth) {
        this.nearBlurDepth = nearBlurDepth;
    }

    /**
     * Get the focus depth of the viewer
     * 
     * @return The focus depth of the viewer
     */
    public float getFocalPlaneDepth() {
        return focalPlaneDepth;
    }

    /**
     * Set the focus depth of the viewer
     * 
     * @return The focus depth of the viewer
     */
    public void setFocalPlaneDepth(float focalPlaneDepth) {
        this.focalPlaneDepth = focalPlaneDepth;
    }

    /**
     * Get the depth at which blur is at it's maximum
     * 
     * @return The depth at which blur is at it's maximum
     */
    public float getFarBlurDepth() {
        return farBlurDepth;
    }

    /**
     * Set the depth at which blur is at it's maximum
     * 
     * @return The depth at which blur is at it's maximum
     */
    public void setFarBlurDepth(float farBlurDepth) {
        this.farBlurDepth = farBlurDepth;
    }

    /**
     * Get the blur cut off value
     * 
     * @return The blur cut off value
     */
    public float getBlurrinessCutoff() {
        return blurrinessCutoff;
    }

    /**
     * Set the cutoff depth at which blurring stops
     * 
     * @param blurrinessCutoff
     *            The depth at which blurring stops
     */
    public void setBlurrinessCutoff(float blurrinessCutoff) {
        this.blurrinessCutoff = blurrinessCutoff;
    }

    /**
     * Helper class to get all spatials rendered in one TextureRenderer.render()
     * call.
     */
    private class SpatialsRenderNode extends Node {
        private static final long serialVersionUID = 7367501683137581101L;

        /**
         * @see com.jme.scene.Node#draw(com.jme.renderer.Renderer)
         */
        public void draw(Renderer r) {
            Spatial child;
            for (int i = 0, cSize = spatials.size(); i < cSize; i++) {
                child = spatials.get(i);
                if (child != null)
                    child.onDraw(r);
            }
        }

        /**
         * @see com.jme.scene.Spatial#onDraw(com.jme.renderer.Renderer)
         */
        public void onDraw(Renderer r) {
            draw(r);
        }
    }
}
