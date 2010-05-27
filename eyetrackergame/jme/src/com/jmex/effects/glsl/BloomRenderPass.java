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
 * GLSL bloom effect pass. - Render supplied source to a texture - Extract
 * intensity - Blur intensity - Blend with first pass
 * 
 * @author Rikard Herlitz (MrCoder) - initial implementation
 * @author Joshua Slack - Enhancements and reworking to use a single
 *         texrenderer, ability to reuse existing back buffer, faster blur,
 *         throttling speed-up, etc.
 */
public class BloomRenderPass extends Pass {
    private static final long serialVersionUID = 1L;

    private float throttle = 0;//1/50f; 
    private float sinceLast = 1; 
    
    private TextureRenderer tRenderer;
	private Texture2D mainTexture;
    private Texture2D secondTexture;
    private Texture2D screenTexture;

    private Quad fullScreenQuad;

	private GLSLShaderObjectsState extractionShader;
    private GLSLShaderObjectsState blurShader;
    private GLSLShaderObjectsState blurShaderHorizontal;
    private GLSLShaderObjectsState blurShaderVertical;
	private GLSLShaderObjectsState finalShader;

	private int nrBlurPasses;
	private float blurSize;
	private float blurIntensityMultiplier;
	private float exposurePow;
	private float exposureCutoff;
	private boolean supported = true;
    private boolean useCurrentScene = false;
    
    private boolean useSeparateConvolution = false;

	public static String shaderDirectory = "com/jmex/effects/glsl/data/";

	/**
	 * Reset bloom parameters to default
	 */
	public void resetParameters() {
		nrBlurPasses = 2;
		blurSize = 0.02f;
		blurIntensityMultiplier = 1.3f;
		exposurePow = 3.0f;
		exposureCutoff = 0.0f;
	}

	/**
	 * Release pbuffers in TextureRenderer's. Preferably called from user cleanup method.
	 */
	public void cleanup() {
        super.cleanUp();
        if (tRenderer != null)
            tRenderer.cleanup();
	}

	public boolean isSupported() {
		return supported;
	}
	
	/**
	 * Creates a new bloom renderpass
	 *
	 * @param cam		 Camera used for rendering the bloomsource
	 * @param renderScale Scale of bloom texture
	 */
	public BloomRenderPass(Camera cam, int renderScale) {

        //Test for glsl support
        if(!GLSLShaderObjectsState.isSupported()) {
            supported = false;
            return;
        }

        DisplaySystem display = DisplaySystem.getDisplaySystem();

		resetParameters();

		//Create texture renderers and rendertextures(alternating between two not to overwrite pbuffers)
        tRenderer = display.createTextureRenderer(
                display.getWidth() / renderScale, 
                display.getHeight() / renderScale,
                TextureRenderer.Target.Texture2D);

		if (!tRenderer.isSupported()) {
			supported = false;
			return;
		}
		tRenderer.setMultipleTargets(true);
        tRenderer.setBackgroundColor(new ColorRGBA(0.0f, 0.0f, 0.0f, 1.0f));
        tRenderer.setCamera(cam);

		mainTexture = new Texture2D();
        mainTexture.setWrap(Texture.WrapMode.Clamp);
		mainTexture.setMagnificationFilter(Texture.MagnificationFilter.Bilinear);
        tRenderer.setupTexture(mainTexture);

        secondTexture = new Texture2D();
        secondTexture.setWrap(Texture.WrapMode.Clamp);
        secondTexture.setMagnificationFilter(Texture.MagnificationFilter.Bilinear);
        tRenderer.setupTexture(secondTexture);

        screenTexture = new Texture2D();
        screenTexture.setWrap(Texture.WrapMode.Clamp);
        screenTexture.setMagnificationFilter(Texture.MagnificationFilter.Bilinear);
        tRenderer.setupTexture(screenTexture);
        
        extractionShader = display.getRenderer().createGLSLShaderObjectsState();
		extractionShader.load(BloomRenderPass.class.getClassLoader().getResource(shaderDirectory + "bloom_extract.vert"),
				BloomRenderPass.class.getClassLoader().getResource(shaderDirectory + "bloom_extract.frag"));
		extractionShader.setEnabled(true);
        extractionShader.setUniform("RT", 0);

		//Create blur shader
        blurShader = display.getRenderer().createGLSLShaderObjectsState();
		blurShader.load(BloomRenderPass.class.getClassLoader().getResource(shaderDirectory + "bloom_blur.vert"),
                BloomRenderPass.class.getClassLoader().getResource(shaderDirectory + "bloom_blur.frag"));
        blurShader.setEnabled(true);
        blurShader.setUniform("RT", 0);

        //Create blur shader horizontal
        blurShaderHorizontal = display.getRenderer().createGLSLShaderObjectsState();
        blurShaderHorizontal.load(BloomRenderPass.class.getClassLoader().getResource(shaderDirectory + "bloom_blur.vert"),
                BloomRenderPass.class.getClassLoader().getResource(shaderDirectory + "bloom_blur_horizontal7.frag"));
        blurShaderHorizontal.setEnabled(true);
        blurShaderHorizontal.setUniform("RT", 0);

        //Create blur shader vertical
        blurShaderVertical = display.getRenderer().createGLSLShaderObjectsState();
        blurShaderVertical.load(BloomRenderPass.class.getClassLoader().getResource(shaderDirectory + "bloom_blur.vert"),
                BloomRenderPass.class.getClassLoader().getResource(shaderDirectory + "bloom_blur_vertical7.frag"));
        blurShaderVertical.setEnabled(true);
        blurShaderVertical.setUniform("RT", 0);

		//Create final shader(basic texturing)
		finalShader = display.getRenderer().createGLSLShaderObjectsState();
		finalShader.load(BloomRenderPass.class.getClassLoader().getResource(shaderDirectory + "bloom_final.vert"),
				BloomRenderPass.class.getClassLoader().getResource(shaderDirectory + "bloom_final.frag"));
		finalShader.setEnabled(true);

		//Create fullscreen quad
		fullScreenQuad = new Quad("FullScreenQuad", display.getWidth()/4, display.getHeight()/4);
		fullScreenQuad.getLocalRotation().set(0, 0, 0, 1);
		fullScreenQuad.getLocalTranslation().set(display.getWidth() / 2, display.getHeight() / 2, 0);
		fullScreenQuad.getLocalScale().set(1, 1, 1);
		fullScreenQuad.setRenderQueueMode(Renderer.QUEUE_ORTHO);

		fullScreenQuad.setCullHint(Spatial.CullHint.Never);
		fullScreenQuad.setTextureCombineMode(Spatial.TextureCombineMode.Replace);
		fullScreenQuad.setLightCombineMode(Spatial.LightCombineMode.Off);
        
		TextureState ts = display.getRenderer().createTextureState();
		ts.setEnabled(true);
        fullScreenQuad.setRenderState(ts);

		BlendState as = display.getRenderer().createBlendState();
		as.setBlendEnabled(true);
		as.setSourceFunction(BlendState.SourceFunction.One);
		as.setDestinationFunction(BlendState.DestinationFunction.One);
		as.setEnabled(true);
        fullScreenQuad.setRenderState(as);

        fullScreenQuad.updateRenderState();
        fullScreenQuad.updateGeometricState(0.0f, true);
	}

    /**
     * Helper class to get all spatials rendered in one TextureRenderer.render() call.
     */
    private class SpatialsRenderNode extends Node {
        private static final long serialVersionUID = 7367501683137581101L;
        public void draw( Renderer r ) {
            Spatial child;
            for (int i = 0, cSize = spatials.size(); i < cSize; i++) {
                child = spatials.get(i);
                if (child != null)
                    child.onDraw(r);
            }
        }

        public void onDraw( Renderer r ) {
            draw( r );
        }
    }

    private final SpatialsRenderNode spatialsRenderNode = new SpatialsRenderNode();

    @Override
    protected void doUpdate(float tpf) {
        super.doUpdate(tpf);
        sinceLast += tpf;
    }
    
    public void doRender(Renderer r) {
        if (!isSupported() || !useCurrentScene && spatials.size() == 0 ) {
            return;
        }

        BlendState as = (BlendState) fullScreenQuad.states[RenderState.StateType.Blend.ordinal()];

        if (sinceLast > throttle) {
            sinceLast = 0;

            as.setEnabled(false);
            TextureState ts = (TextureState) fullScreenQuad.states[RenderState.StateType.Texture.ordinal()];
            
            // see if we should use the current scene to bloom, or only things added to the pass.
            if (useCurrentScene) {
                // grab backbuffer to texture
                tRenderer.copyToTexture(screenTexture, 
                        DisplaySystem.getDisplaySystem().getWidth(), 
                        DisplaySystem.getDisplaySystem().getHeight());
                ts.setTexture(screenTexture, 0);
            } else {
        		//Render scene to texture
                tRenderer.render( spatialsRenderNode , mainTexture);
                ts.setTexture(mainTexture, 0);
            }

    		//Extract intensity
    		extractionShader.setUniform("exposurePow", getExposurePow());
    		extractionShader.setUniform("exposureCutoff", getExposureCutoff());
    
            fullScreenQuad.states[RenderState.StateType.GLSLShaderObjects.ordinal()] = extractionShader;
            tRenderer.render(fullScreenQuad, secondTexture);
        
            if (!useSeparateConvolution) {
                blurShader.setUniform("sampleDist", getBlurSize());
                blurShader.setUniform("blurIntensityMultiplier",  getBlurIntensityMultiplier());

                ts.setTexture(secondTexture, 0);
                fullScreenQuad.states[RenderState.StateType.GLSLShaderObjects.ordinal()] = blurShader;
                tRenderer.render(fullScreenQuad, mainTexture);

                //Extra blur passes
                for(int i = 1; i < getNrBlurPasses(); i++) {
                    blurShader.setUniform("sampleDist", getBlurSize() - (float)i*getBlurSize()/getNrBlurPasses());
                    if (i%2 == 1) {
                        ts.setTexture(mainTexture, 0);
                        tRenderer.render(fullScreenQuad, secondTexture);
                    } else {
                        ts.setTexture(secondTexture, 0);
                        tRenderer.render(fullScreenQuad, mainTexture);
                    }
                }
                if (getNrBlurPasses()%2 == 1) {
                    ts.setTexture(mainTexture, 0);
                } else {
                    ts.setTexture(secondTexture, 0);
                    tRenderer.render(fullScreenQuad, mainTexture, false);
                }
            } else {
                blurShaderVertical.setUniform("blurIntensityMultiplier",  getBlurIntensityMultiplier());

                for(int i = 0; i < getNrBlurPasses(); i++) {
                    blurShaderHorizontal.setUniform("sampleDist", getBlurSize() - (float)i*getBlurSize()/getNrBlurPasses());
                    blurShaderVertical.setUniform("sampleDist", getBlurSize() - (float)i*getBlurSize()/getNrBlurPasses());

                    ts.setTexture(secondTexture, 0);
                    fullScreenQuad.states[RenderState.StateType.GLSLShaderObjects.ordinal()] = blurShaderHorizontal;
                    tRenderer.render(fullScreenQuad, mainTexture);
                    ts.setTexture(mainTexture, 0);
                    fullScreenQuad.states[RenderState.StateType.GLSLShaderObjects.ordinal()] = blurShaderVertical;
                    tRenderer.render(fullScreenQuad, secondTexture);
                }
                ts.setTexture(secondTexture, 0);
            }
        }

		//Final blend
		as.setEnabled(true);
        
        fullScreenQuad.states[RenderState.StateType.GLSLShaderObjects.ordinal()] = finalShader;
        r.draw(fullScreenQuad);
	}

	/**
     * @return The throttle amount - or in other words, how much time in
     *         seconds must pass before the bloom effect is updated.
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

    public float getBlurSize() {
		return blurSize;
	}

	public void setBlurSize(float blurSize) {
		this.blurSize = blurSize;
	}

	public float getExposurePow() {
		return exposurePow;
	}

	public void setExposurePow(float exposurePow) {
		this.exposurePow = exposurePow;
	}

	public float getExposureCutoff() {
		return exposureCutoff;
	}

	public void setExposureCutoff(float exposureCutoff) {
		this.exposureCutoff = exposureCutoff;
	}

	public float getBlurIntensityMultiplier() {
		return blurIntensityMultiplier;
	}

	public void setBlurIntensityMultiplier(float blurIntensityMultiplier) {
		this.blurIntensityMultiplier = blurIntensityMultiplier;
	}

	public int getNrBlurPasses() {
		return nrBlurPasses;
	}

	public void setNrBlurPasses(int nrBlurPasses) {
		this.nrBlurPasses = nrBlurPasses;
	}

    public boolean useCurrentScene() {
        return useCurrentScene;
    }

    public void setUseCurrentScene(boolean useCurrentScene) {
        this.useCurrentScene = useCurrentScene;
    }

    public void setUseSeparateConvolution(boolean useSeparateConvolution) {
        this.useSeparateConvolution = useSeparateConvolution;
    }

    public boolean isUseSeparateConvolution() {
        return useSeparateConvolution;
    }
}
