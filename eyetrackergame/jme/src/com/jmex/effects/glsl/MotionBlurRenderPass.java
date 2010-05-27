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

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.image.Texture;
import com.jme.image.Texture2D;
import com.jme.math.Matrix4f;
import com.jme.renderer.AbstractCamera;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.renderer.TextureRenderer;
import com.jme.renderer.pass.Pass;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.CullState;
import com.jme.scene.state.GLSLShaderObjectsState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;

/**
 * GLSL motion blur pass.
 *
 * @author Rikard Herlitz (MrCoder) - initial implementation
 */
public class MotionBlurRenderPass extends Pass {
    private static final Logger logger = Logger
            .getLogger(MotionBlurRenderPass.class.getName());
    
	private static final long serialVersionUID = 1L;

	private TextureRenderer tRenderer;
	private Texture2D mainTexture;

	private BlendState alphaObj;
	private CullState cullObj;
	private TextureState tsObj;
	private float blurStrength = -0.000035f;

	private GLSLShaderObjectsState motionBlurShader;

	private boolean freeze = false;
	private boolean supported = true;
	private boolean useCurrentScene = false;
    
    /**
     * Container with matrix-data for tracked spatials
     */
    private class DynamicObject {
        public int index;
		public Spatial spatial;
		public Matrix4f modelMatrix = new Matrix4f();
		public Matrix4f modelViewMatrix = new Matrix4f();
		public Matrix4f modelViewProjectionMatrix = new Matrix4f();
	}

	private ArrayList<DynamicObject> dynamicObjects = new ArrayList<DynamicObject>();

	private Matrix4f tmpMatrix = new Matrix4f();
	private Matrix4f projectionMatrix = new Matrix4f();

	private float tpf = 0.0f;
	private Camera cam;

    /**
     * Adds a spatial to be tracked and rendered with motionblur
     * @param spatial The spatial to track
     */
    public void addMotionBlurSpatial( Spatial spatial ) {
		DynamicObject dynamicObject = new DynamicObject();
		dynamicObject.spatial = spatial;
		dynamicObjects.add( dynamicObject );
	}

	/**
	 * Reset motionblur parameters to default
	 */
	public void resetParameters() {
	}

	/**
	 * Release pbuffers in TextureRenderer's. Preferably called from user cleanup method.
	 */
	public void cleanup() {
		super.cleanUp();
		if( tRenderer != null )
			tRenderer.cleanup();
	}

	public boolean isSupported() {
		return supported;
	}

	/**
	 * Creates a new motionblur renderpass
	 *
	 * @param cam		 Camera used for rendering the motionblur source
	 */
	public MotionBlurRenderPass( Camera cam ) {

        //Test for glsl support
        if(!GLSLShaderObjectsState.isSupported()) {
            supported = false;
            return;
        }

        this.cam = cam;
		DisplaySystem display = DisplaySystem.getDisplaySystem();

		resetParameters();

		tRenderer = display.createTextureRenderer(
				    display.getWidth(),
                    display.getHeight(),
                    TextureRenderer.Target.Texture2D);
		tRenderer.setBackgroundColor( new ColorRGBA( 0.0f, 0.0f, 0.0f, 1.0f ) );
		tRenderer.setCamera( cam );

		mainTexture = new Texture2D();
		mainTexture.setMagnificationFilter( Texture.MagnificationFilter.Bilinear );
		tRenderer.setupTexture( mainTexture );

		//Create extract intensity shader
		motionBlurShader = display.getRenderer().createGLSLShaderObjectsState();
		reloadShader();

		tsObj = display.getRenderer().createTextureState();
		tsObj.setEnabled( true );
		tsObj.setTexture( mainTexture, 0 );

		cullObj = display.getRenderer().createCullState();
		cullObj.setEnabled( true );
		cullObj.setCullFace( CullState.Face.Back );

		alphaObj = display.getRenderer().createBlendState();
		alphaObj.setEnabled( true );
		alphaObj.setBlendEnabled( true );
		alphaObj.setSourceFunction( BlendState.SourceFunction.SourceAlpha );
		alphaObj.setDestinationFunction( BlendState.DestinationFunction.OneMinusSourceAlpha );
	}

    /**
     * Verifies that the shaders are compiling and reloads them
     */
    public void reloadShader() {
		GLSLShaderObjectsState testShader = DisplaySystem.getDisplaySystem().getRenderer().createGLSLShaderObjectsState();
		try {
			testShader.load( MotionBlurRenderPass.class.getClassLoader().getResource( "com/jmex/effects/glsl/data/motionblur.vert" ),
							 MotionBlurRenderPass.class.getClassLoader().getResource( "com/jmex/effects/glsl/data/motionblur.frag" ) );
			testShader.apply();
            DisplaySystem.getDisplaySystem().getRenderer().checkCardError();
		} catch( JmeException e ) {
			logger.log(Level.WARNING, "Error loading shader", e);
			return;
		}

		motionBlurShader.load( MotionBlurRenderPass.class.getClassLoader().getResource( "com/jmex/effects/glsl/data/motionblur.vert" ),
							   MotionBlurRenderPass.class.getClassLoader().getResource( "com/jmex/effects/glsl/data/motionblur.frag" ) );

		motionBlurShader.clearUniforms();
		motionBlurShader.setUniform( "screenTexture", 0 );
		motionBlurShader.setUniform( "prevModelViewMatrix", new Matrix4f(), false );
		motionBlurShader.setUniform( "prevModelViewProjectionMatrix", new Matrix4f(), false );
		motionBlurShader.setUniform( "halfWinSize", DisplaySystem.getDisplaySystem().getWidth() * 0.5f, DisplaySystem.getDisplaySystem().getHeight() * 0.5f );
		motionBlurShader.setUniform( "blurStrength", blurStrength );
		motionBlurShader.apply();

		logger.info("Shader reloaded...");
	}

	/**
	 * Helper class to get all spatials rendered in one TextureRenderer.render() call.
	 */
	private class SpatialsRenderNode extends Node {
		private static final long serialVersionUID = 7367501683137581101L;

		public void draw( Renderer r ) {
			Spatial child;
			for( int i = 0, cSize = spatials.size(); i < cSize; i++ ) {
				child = spatials.get( i );
				if( child != null )
					child.onDraw( r );
			}
		}

		public void onDraw( Renderer r ) {
			draw( r );
		}
	}

	private final SpatialsRenderNode spatialsRenderNode = new SpatialsRenderNode();

	@Override
	protected void doUpdate( float tpf ) {
		super.doUpdate( tpf );
		if ( !freeze ) {
			this.tpf = tpf;
		}
	}

    /**
     * <code>doRender</code> renders this pass to the framebuffer
     *
     * @param r
     *            Renderer to use for drawing.
     * @see com.jme.renderer.pass.Pass#doRender(com.jme.renderer.Renderer)
     */
	public void doRender( Renderer r ) {
		if( !isSupported() || !useCurrentScene && spatials.size() == 0 ) {
			return;
		}

		// see if we should use the current scene to motionblur, or only things added to the pass.
		if( useCurrentScene ) {
			// grab backbuffer to texture
			tRenderer.copyToTexture( mainTexture,
										   DisplaySystem.getDisplaySystem().getWidth(),
										   DisplaySystem.getDisplaySystem().getHeight());
		}
		else {
			//Render scene to texture
			tRenderer.render( spatialsRenderNode, mainTexture );
		}

		projectionMatrix.set( ((AbstractCamera) cam).getProjectionMatrix() );
		for( int i = 0; i < dynamicObjects.size(); i++ ) {
			DynamicObject dynamicObject = dynamicObjects.get( i );
			Matrix4f modelMatrix = dynamicObject.modelMatrix;
			Matrix4f modelViewMatrix = dynamicObject.modelViewMatrix;
			Matrix4f modelViewProjectionMatrix = dynamicObject.modelViewProjectionMatrix;

			modelViewMatrix.set( modelMatrix );
			modelViewMatrix.multLocal( ((AbstractCamera) cam).getModelViewMatrix() );
			modelViewProjectionMatrix.set( modelViewMatrix ).multLocal( projectionMatrix );
		}

		context.enforceState( motionBlurShader );
        context.enforceState( tsObj );
        context.enforceState( cullObj );

		for( int i = 0; i < dynamicObjects.size(); i++ ) {
			DynamicObject dynamicObject = dynamicObjects.get( i );

			motionBlurShader.setUniform( "prevModelViewMatrix", dynamicObject.modelViewMatrix, false );
			motionBlurShader.setUniform( "prevModelViewProjectionMatrix", dynamicObject.modelViewProjectionMatrix, false );
			motionBlurShader.setUniform( "blurStrength", blurStrength / tpf );
			motionBlurShader.apply();

			r.draw( dynamicObject.spatial );
			r.renderQueue();
		}

        context.clearEnforcedState( RenderState.StateType.GLSLShaderObjects );
        context.clearEnforcedState( RenderState.StateType.Texture );
        context.clearEnforcedState( RenderState.StateType.Cull );

		if( !freeze ) {
			for( int i = 0; i < dynamicObjects.size(); i++ ) {
				DynamicObject dynamicObject = dynamicObjects.get( i );
				Matrix4f modelMatrix = dynamicObject.modelMatrix;
				Spatial spatial = dynamicObject.spatial;

				modelMatrix.loadIdentity();
				spatial.getWorldRotation().toRotationMatrix( tmpMatrix );
				modelMatrix.multLocal( tmpMatrix );
				modelMatrix.m00 *= spatial.getWorldScale().x;
				modelMatrix.m11 *= spatial.getWorldScale().y;
				modelMatrix.m22 *= spatial.getWorldScale().z;
				modelMatrix.setTranslation( spatial.getWorldTranslation() );
				modelMatrix.transposeLocal();
			}
		}
	}

    public Texture getMainTexture() {
        return mainTexture;
    }

    public boolean useCurrentScene() {
		return useCurrentScene;
	}

	public void setUseCurrentScene( boolean useCurrentScene ) {
		this.useCurrentScene = useCurrentScene;
	}

	public boolean isFreeze() {
		return freeze;
	}

	public void setFreeze( boolean freeze ) {
		this.freeze = freeze;
	}

	public float getBlurStrength() {
		return blurStrength;
	}

	public void setBlurStrength( float blurStrength ) {
		this.blurStrength = blurStrength;
	}
}
