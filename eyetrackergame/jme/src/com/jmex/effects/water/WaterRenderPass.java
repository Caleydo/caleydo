/*
 * Copyright (c) 2003-2010 jMonkeyEngine
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

package com.jmex.effects.water;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.image.Texture;
import com.jme.image.Texture2D;
import com.jme.math.Plane;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.renderer.TextureRenderer;
import com.jme.renderer.pass.Pass;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.Spatial.CullHint;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.ClipState;
import com.jme.scene.state.CullState;
import com.jme.scene.state.FogState;
import com.jme.scene.state.GLSLShaderObjectsState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.TextureManager;

/**
 * <code>WaterRenderPass</code>
 * Water effect pass.
 *
 * @author Rikard Herlitz (MrCoder)
 * @version $Id: WaterRenderPass.java 4782 2010-01-03 12:54:04Z skye.book $
 */
public class WaterRenderPass extends Pass {
    private static final Logger logger = Logger.getLogger(WaterRenderPass.class
            .getName());
    
    private static final long serialVersionUID = 1L;

    protected Camera cam;
    protected float tpf;
    protected float reflectionThrottle = 1/50f, refractionThrottle = 1/50f;
    protected float reflectionTime = 0, refractionTime = 0;
    protected boolean useFadeToFogColor = false;

	protected TextureRenderer tRenderer;
	protected Texture2D textureReflect;
	protected Texture2D textureRefract;
	protected Texture2D textureDepth;

	protected ArrayList<Spatial> renderList;
	protected ArrayList<Texture> texArray = new ArrayList<Texture>();
	protected Node skyBox;

	protected GLSLShaderObjectsState waterShader;
	protected CullState cullBackFace;
	protected TextureState textureState;
	protected TextureState fallbackTextureState;
	
    private Texture normalmapTexture;
    private Texture dudvTexture;
    private Texture foamTexture;
    private Texture fallbackTexture;
	
	protected BlendState as1;
	protected ClipState clipState;
	protected FogState noFog;

	protected Plane waterPlane;
	protected Vector3f tangent;
	protected Vector3f binormal;
	protected Vector3f calcVect = new Vector3f();
	protected float clipBias;
	protected ColorRGBA waterColorStart;
	protected ColorRGBA waterColorEnd;
	protected float heightFalloffStart;
	protected float heightFalloffSpeed;
	protected float waterMaxAmplitude;
	protected float speedReflection;
	protected float speedRefraction;

	protected boolean aboveWater;
	protected float normalTranslation = 0.0f;
	protected float refractionTranslation = 0.0f;
	protected boolean supported = true;
	protected boolean useProjectedShader = false;
	protected boolean useRefraction = false;
	protected boolean useReflection = true;
	protected int renderScale;

	public static String simpleShaderStr = "com/jmex/effects/water/data/flatwatershader";
	public static String simpleShaderRefractionStr = "com/jmex/effects/water/data/flatwatershader_refraction";
	public static String projectedShaderStr = "com/jmex/effects/water/data/projectedwatershader";
	public static String projectedShaderRefractionStr = "com/jmex/effects/water/data/projectedwatershader_refraction";
	protected String currentShaderStr;

    public static String normalMapTextureString = "com/jmex/effects/water/data/normalmap3.dds";
    public static String dudvMapTextureString = "com/jmex/effects/water/data/dudvmap.png";
    public static String foamMapTextureString = "com/jmex/effects/water/data/oceanfoam.png";
    public static String fallbackMapTextureString = "com/jmex/effects/water/data/water2.png";

    /**
     * Resets water parameters to default values
     *
     */
    public void resetParameters() {
		waterPlane = new Plane( new Vector3f( 0.0f, 1.0f, 0.0f ), 0.0f );
		tangent = new Vector3f( 1.0f, 0.0f, 0.0f );
		binormal = new Vector3f( 0.0f, 0.0f, 1.0f );

		waterMaxAmplitude = 1.0f;
		clipBias = 0.0f;
		waterColorStart = new ColorRGBA( 0.0f, 0.0f, 0.1f, 1.0f );
		waterColorEnd = new ColorRGBA( 0.0f, 0.3f, 0.1f, 1.0f );
		heightFalloffStart = 300.0f;
		heightFalloffSpeed = 500.0f;
		speedReflection = 0.1f;
		speedRefraction = -0.05f;
	}

	/**
	 * Release pbuffers in TextureRenderer's. Preferably called from user cleanup method.
	 */
	public void cleanup() {
		if( isSupported() )
			tRenderer.cleanup();
	}

	public boolean isSupported() {
		return supported;
	}

	/**
	 * Creates a new WaterRenderPass
	 *
	 * @param cam				main rendercam to use for reflection settings etc
	 * @param renderScale		how many times smaller the reflection/refraction textures should be compared to the main display
	 * @param useProjectedShader true - use the projected setup for variable height water meshes, false - use the flat shader setup
	 * @param useRefraction	  enable/disable rendering of refraction textures
	 */
	public WaterRenderPass( Camera cam, int renderScale, boolean useProjectedShader, boolean useRefraction ) {
		this.cam = cam;
		this.useProjectedShader = useProjectedShader;
        this.useRefraction = useRefraction;
		this.renderScale = renderScale;
		resetParameters();
		initialize();
	}

	private void initialize() {
		if( useRefraction && useProjectedShader && TextureState.getNumberOfFragmentUnits() < 6 ||
			useRefraction && TextureState.getNumberOfFragmentUnits() < 5 ) {
			useRefraction = false;
			logger.info("Not enough textureunits, falling back to non refraction water");
		}

		DisplaySystem display = DisplaySystem.getDisplaySystem();

		if( !GLSLShaderObjectsState.isSupported() ) {
			supported = false;
		}

        waterShader = display.getRenderer().createGLSLShaderObjectsState();

		cullBackFace = display.getRenderer().createCullState();
		cullBackFace.setEnabled( true );
		cullBackFace.setCullFace( CullState.Face.None );
		clipState = display.getRenderer().createClipState();
		if( isSupported() ) {
			tRenderer = display.createTextureRenderer(
					    display.getWidth() / renderScale,
                        display.getHeight() / renderScale,
                        TextureRenderer.Target.Texture2D);

			if( tRenderer.isSupported() ) {
                tRenderer.setMultipleTargets(true);
				tRenderer.setBackgroundColor( new ColorRGBA( 0.0f, 0.0f, 0.0f, 1.0f ) );
				tRenderer.getCamera().setFrustum( cam.getFrustumNear(), cam.getFrustumFar(), cam.getFrustumLeft(), cam.getFrustumRight(), cam.getFrustumTop(), cam.getFrustumBottom() );

				textureState = display.getRenderer().createTextureState();
				textureState.setEnabled( true );

				setupTextures();
			}
			else {
				supported = false;
			}
		}

		if( !isSupported() ) {
		    createFallbackData();
		} else {
            noFog = display.getRenderer().createFogState();
            noFog.setEnabled(false);      
        }
	}

    protected void setupTextures() {
        textureReflect = new Texture2D();
        textureReflect.setWrap(Texture.WrapMode.EdgeClamp);
        textureReflect.setMagnificationFilter( Texture.MagnificationFilter.Bilinear );
        textureReflect.setScale( new Vector3f( -1.0f, 1.0f, 1.0f ) );
        textureReflect.setTranslation( new Vector3f( 1.0f, 0.0f, 0.0f ) );
        tRenderer.setupTexture( textureReflect );

        normalmapTexture = TextureManager.loadTexture(
        		WaterRenderPass.class.getClassLoader().getResource( normalMapTextureString ),
        		Texture.MinificationFilter.Trilinear,
        		Texture.MagnificationFilter.Bilinear
        );
        textureState.setTexture( normalmapTexture, 0 );
        normalmapTexture.setWrap(Texture.WrapMode.Repeat);

        textureState.setTexture( textureReflect, 1 );

        dudvTexture = TextureManager.loadTexture(
        		WaterRenderPass.class.getClassLoader().getResource( dudvMapTextureString ),
        		Texture.MinificationFilter.Trilinear,
        		Texture.MagnificationFilter.Bilinear, com.jme.image.Image.Format.GuessNoCompression, 1.0f, false
        );
        dudvTexture.setScale(new Vector3f(0.8f, 0.8f, 1.0f));
        textureState.setTexture( dudvTexture, 2 );
        dudvTexture.setWrap(Texture.WrapMode.Repeat);

        if( useRefraction ) {
            textureRefract = new Texture2D();
            textureRefract.setWrap(Texture.WrapMode.EdgeClamp);
            textureRefract.setMagnificationFilter( Texture.MagnificationFilter.Bilinear );
            tRenderer.setupTexture( textureRefract );

            textureDepth = new Texture2D();
            textureDepth.setWrap(Texture.WrapMode.EdgeClamp);
            textureDepth.setMagnificationFilter( Texture.MagnificationFilter.NearestNeighbor );
            textureDepth.setRenderToTextureType( Texture.RenderToTextureType.Depth );
            tRenderer.setupTexture( textureDepth );

            textureState.setTexture( textureRefract, 3 );
        	textureState.setTexture( textureDepth, 4 );
        }

        if( useProjectedShader ) {
        	foamTexture = TextureManager.loadTexture(
        			WaterRenderPass.class.getClassLoader().getResource( foamMapTextureString ),
        			Texture.MinificationFilter.Trilinear,
        			Texture.MagnificationFilter.Bilinear );
        	if( useRefraction ) {
        		textureState.setTexture( foamTexture, 5 );
        	}
        	else {
        		textureState.setTexture( foamTexture, 3 );
        	}
        	foamTexture.setWrap(Texture.WrapMode.Repeat);
        }

        clipState.setEnabled( true );
        clipState.setEnableClipPlane( ClipState.CLIP_PLANE0, true );

        reloadShader();
    }

    private void createFallbackData() {
        DisplaySystem display = DisplaySystem.getDisplaySystem();

        fallbackTextureState = display.getRenderer().createTextureState();
        fallbackTextureState.setEnabled( true );

        fallbackTexture = TextureManager.loadTexture(
        		WaterRenderPass.class.getClassLoader().getResource( fallbackMapTextureString ),
        		Texture.MinificationFilter.Trilinear,
        		Texture.MagnificationFilter.Bilinear );
        fallbackTextureState.setTexture( fallbackTexture, 0 );
        fallbackTexture.setWrap(Texture.WrapMode.Repeat);

        as1 = display.getRenderer().createBlendState();
        as1.setBlendEnabled( true );
        as1.setTestEnabled( true );
        as1.setSourceFunction( BlendState.SourceFunction.SourceAlpha );
        as1.setDestinationFunction( BlendState.DestinationFunction.OneMinusSourceAlpha );
        as1.setEnabled( true );
    }

	@Override
	protected void doUpdate( float tpf ) {
		super.doUpdate( tpf );
		this.tpf = tpf;
	}


	public void doRender( Renderer r ) {
		updateTranslations();

		float camWaterDist = waterPlane.pseudoDistance( cam.getLocation() );
		aboveWater = camWaterDist >= 0;

		if( isSupported() ) {
			waterShader.setUniform( "tangent", tangent );
			waterShader.setUniform( "binormal", binormal );
			waterShader.setUniform( "useFadeToFogColor", useFadeToFogColor );
			waterShader.setUniform( "waterColor", waterColorStart );
			waterShader.setUniform( "waterColorEnd", waterColorEnd );
			waterShader.setUniform( "normalTranslation", normalTranslation );
			waterShader.setUniform( "refractionTranslation", refractionTranslation );
			waterShader.setUniform( "abovewater", aboveWater );
			if( useProjectedShader ) {
				waterShader.setUniform( "cameraPos", cam.getLocation() );
				waterShader.setUniform( "waterHeight", waterPlane.getConstant() );
				waterShader.setUniform( "amplitude", waterMaxAmplitude );
				waterShader.setUniform( "heightFalloffStart", heightFalloffStart );
				waterShader.setUniform( "heightFalloffSpeed", heightFalloffSpeed );
			}

			float heightTotal = clipBias + waterMaxAmplitude - waterPlane.getConstant();
			Vector3f normal = waterPlane.getNormal();
			clipState.setEnabled( true );

			if( useReflection ) {
	            clipState.setClipPlaneEquation( ClipState.CLIP_PLANE0, normal.x, normal.y, normal.z, heightTotal );

	            renderReflection();
			}

			if( useRefraction && aboveWater ) {
	            clipState.setClipPlaneEquation( ClipState.CLIP_PLANE0, -normal.x, -normal.y, -normal.z, -heightTotal );

	            renderRefraction();
			}

			clipState.setEnabled( false );
		}

		if (fallbackTextureState != null) {
			fallbackTextureState.getTexture().setTranslation( new Vector3f( 0, normalTranslation, 0 ) );
		}
	}

    protected void updateTranslations() {
        normalTranslation += speedReflection * tpf;
		refractionTranslation += speedRefraction * tpf;
    }

	public void reloadShader() {
		if( useProjectedShader ) {
			if( useRefraction ) {
				currentShaderStr = projectedShaderRefractionStr;
			}
			else {
				currentShaderStr = projectedShaderStr;
			}
		}
		else {
			if( useRefraction ) {
				currentShaderStr = simpleShaderRefractionStr;
			}
			else {
				currentShaderStr = simpleShaderStr;
			}
		}
		GLSLShaderObjectsState testShader = DisplaySystem.getDisplaySystem().getRenderer().createGLSLShaderObjectsState();
		try {
			testShader.load( WaterRenderPass.class.getClassLoader().getResource( currentShaderStr + ".vert" ),
							 WaterRenderPass.class.getClassLoader().getResource( currentShaderStr + ".frag" ) );
			testShader.apply();
            DisplaySystem.getDisplaySystem().getRenderer().checkCardError();
		} catch( JmeException e ) {
            logger.log(Level.WARNING, "Error loading shader", e);
			return;
		}

		waterShader.load( WaterRenderPass.class.getClassLoader().getResource( currentShaderStr + ".vert" ),
						  WaterRenderPass.class.getClassLoader().getResource( currentShaderStr + ".frag" ) );

        waterShader.setUniform( "normalMap", 0 );
        waterShader.setUniform( "reflection", 1 );
        waterShader.setUniform( "dudvMap", 2 );
        if( useRefraction ) {
            waterShader.setUniform( "refraction", 3 );
            waterShader.setUniform( "depthMap", 4 );
        }
        if( useProjectedShader ) {
            if( useRefraction ) {
                waterShader.setUniform( "foamMap", 5 );
            }
            else {
                waterShader.setUniform( "foamMap", 3 );
            }
        }

        logger.info("Shader reloaded...");
	}

	public void setWaterEffectOnSpatial( Spatial spatial, boolean useTransparency) {
	    setWaterEffectOnSpatial( spatial );

	    if (useTransparency) {
	        if (fallbackTextureState == null) {
	            createFallbackData();
	        }
	        spatial.setRenderState( as1 );
	        spatial.updateRenderState();
	    }
	}
	
    /**
     * Sets a spatial up for being rendered with the watereffect
     * @param spatial Spatial to use as base for the watereffect
     */
	public void setWaterEffectOnSpatial( Spatial spatial ) {
		spatial.setRenderState( cullBackFace );
		if( isSupported() ) {
			spatial.setRenderQueueMode( Renderer.QUEUE_SKIP );
			spatial.setRenderState( waterShader );
			spatial.setRenderState(textureState);
		}
		else {
			spatial.setRenderQueueMode( Renderer.QUEUE_TRANSPARENT );
			spatial.setLightCombineMode( Spatial.LightCombineMode.Off );
			spatial.setRenderState(fallbackTextureState);
			spatial.setRenderState( as1 );
		}
		spatial.updateRenderState();
	}
	
	public void setFallbackEffectOnSpatial( Spatial spatial )  {
	    if (fallbackTextureState == null) {
	        createFallbackData();
	    }
	    
        spatial.setRenderState( cullBackFace );
        spatial.setRenderQueueMode( Renderer.QUEUE_TRANSPARENT );
        spatial.setLightCombineMode( Spatial.LightCombineMode.Off );
        spatial.setRenderState(fallbackTextureState);
        spatial.setRenderState(as1);
        spatial.updateRenderState();	    
	}

	//temporary vectors for mem opt.
	private Vector3f tmpLocation = new Vector3f();
	private Vector3f camReflectPos = new Vector3f();
	private Vector3f camReflectDir = new Vector3f();
	private Vector3f camReflectUp = new Vector3f();
	private Vector3f camReflectLeft = new Vector3f();
	private Vector3f camLocation = new Vector3f();

	private void renderReflection() {
	    if (renderList == null || renderList.isEmpty()) {
	        return;
	    }
	    
	    reflectionTime += tpf;
        if (reflectionTime < reflectionThrottle) return;
        reflectionTime = 0;

		if( aboveWater ) {
			camLocation.set( cam.getLocation() );

			float planeDistance = waterPlane.pseudoDistance( camLocation );
            calcVect.set(waterPlane.getNormal()).multLocal( planeDistance * 2.0f );
			camReflectPos.set( camLocation.subtractLocal( calcVect ) );

			camLocation.set( cam.getLocation() ).addLocal( cam.getDirection() );
			planeDistance = waterPlane.pseudoDistance( camLocation );
            calcVect.set(waterPlane.getNormal()).multLocal( planeDistance * 2.0f );
			camReflectDir.set( camLocation.subtractLocal( calcVect ) ).subtractLocal( camReflectPos ).normalizeLocal();

			camLocation.set( cam.getLocation() ).addLocal( cam.getUp() );
			planeDistance = waterPlane.pseudoDistance( camLocation );
            calcVect.set(waterPlane.getNormal()).multLocal( planeDistance * 2.0f );
			camReflectUp.set( camLocation.subtractLocal( calcVect ) ).subtractLocal( camReflectPos ).normalizeLocal();

			camReflectLeft.set( camReflectUp ).crossLocal( camReflectDir ).normalizeLocal();

			tRenderer.getCamera().getLocation().set( camReflectPos );
			tRenderer.getCamera().getDirection().set( camReflectDir );
			tRenderer.getCamera().getUp().set( camReflectUp );
			tRenderer.getCamera().getLeft().set( camReflectLeft );
		}
		else {
			tRenderer.getCamera().getLocation().set( cam.getLocation() );
			tRenderer.getCamera().getDirection().set( cam.getDirection() );
			tRenderer.getCamera().getUp().set( cam.getUp() );
			tRenderer.getCamera().getLeft().set( cam.getLeft() );
		}

		if ( skyBox != null ) {
			tmpLocation.set( skyBox.getLocalTranslation() );
			skyBox.getLocalTranslation().set( tRenderer.getCamera().getLocation() );
			skyBox.updateWorldData( 0.0f );
		}

        texArray.clear();
        texArray.add(textureReflect);
        
        if (isUseFadeToFogColor()) {
            context.enforceState(noFog);
            tRenderer.render( renderList, texArray );
            context.clearEnforcedState(RenderState.StateType.Fog);
        } else {
            tRenderer.render( renderList, texArray );
        }

		if ( skyBox != null ) {
			skyBox.getLocalTranslation().set( tmpLocation );
			skyBox.updateWorldData( 0.0f );
		}
	}

	private void renderRefraction() {
        if (renderList.isEmpty()) {
            return;
        }
        
        refractionTime += tpf;
        if (refractionTime < refractionThrottle) return;
        refractionTime = 0;

        tRenderer.getCamera().getLocation().set( cam.getLocation() );
		tRenderer.getCamera().getDirection().set( cam.getDirection() );
		tRenderer.getCamera().getUp().set( cam.getUp() );
		tRenderer.getCamera().getLeft().set( cam.getLeft() );

        CullHint cullMode = CullHint.Dynamic;
		if ( skyBox != null ) {
			cullMode = skyBox.getCullHint();
			skyBox.setCullHint( CullHint.Always );
		}

        texArray.clear();
        texArray.add(textureRefract);
        texArray.add(textureDepth);
        
        if (isUseFadeToFogColor()) {
            context.enforceState(noFog);
            tRenderer.render( renderList, texArray );
            context.clearEnforcedState(RenderState.StateType.Fog);
        } else {
            tRenderer.render( renderList, texArray );
        }

		if ( skyBox != null ) {
			skyBox.setCullHint( cullMode );
		}
	}

	public void removeReflectedScene( Spatial renderNode ) {
		if(renderList != null) {
			logger.info("Removed reflected scene: " + renderList.remove(renderNode));
		}
	}
	
	public void clearReflectedScene() {
		if(renderList != null) {
			renderList.clear();
		}
	}
	
    /**
     * Sets spatial to be used as reflection in the water(clears previously set)
     * @param renderNode Spatial to use as reflection in the water
     */
	public void setReflectedScene( Spatial renderNode ) {
		if(renderList == null) {
			renderList = new ArrayList<Spatial>();
		}
		renderList.clear();
		renderList.add(renderNode);
		renderNode.setRenderState( clipState );
		renderNode.updateRenderState();
	}
    
	/**
     * Adds a spatial to the list of spatials used as reflection in the water
     * @param renderNode Spatial to add to the list of objects used as reflection in the water
	 */
	public void addReflectedScene( Spatial renderNode ) {
        if (renderNode == null) return;
        
		if(renderList == null) {
			renderList = new ArrayList<Spatial>();
		}
		if(!renderList.contains(renderNode)) {
			renderList.add(renderNode);
			renderNode.setRenderState( clipState );
			renderNode.updateRenderState();
		}
	}

    /**
     * Sets up a node to be transformed and clipped for skybox usage
     * @param skyBox Handle to a node to use as skybox
     */
	public void setSkybox( Node skyBox ) {
        if (skyBox != null) {
    		ClipState skyboxClipState = DisplaySystem.getDisplaySystem().getRenderer().createClipState();
    		skyboxClipState.setEnabled( false );
    		skyBox.setRenderState( skyboxClipState );
    		skyBox.updateRenderState();
        }

		this.skyBox = skyBox;
	}

	public Camera getCam() {
		return cam;
	}

	public void setCam( Camera cam ) {
		this.cam = cam;
	}

	public ColorRGBA getWaterColorStart() {
		return waterColorStart;
	}

    /** 
     * Color to use when the incident angle to the surface is low 
     */ 
	public void setWaterColorStart( ColorRGBA waterColorStart ) {
		this.waterColorStart = waterColorStart;
	}

	public ColorRGBA getWaterColorEnd() {
		return waterColorEnd;
	}

    /**
     * Color to use when the incident angle to the surface is high
     */
	public void setWaterColorEnd( ColorRGBA waterColorEnd ) {
		this.waterColorEnd = waterColorEnd;
	}

	public float getHeightFalloffStart() {
		return heightFalloffStart;
	}

    /**
     * Set at what distance the waveheights should start to fade out(for projected water only)
     * @param heightFalloffStart
     */
	public void setHeightFalloffStart( float heightFalloffStart ) {
		this.heightFalloffStart = heightFalloffStart;
	}

	public float getHeightFalloffSpeed() {
		return heightFalloffSpeed;
	}

    /**
     * Set the fadeout length of the waveheights, when over falloff start(for projected water only)
     * @param heightFalloffStart
     */
	public void setHeightFalloffSpeed( float heightFalloffSpeed ) {
		this.heightFalloffSpeed = heightFalloffSpeed;
	}

	public float getWaterHeight() {
		return waterPlane.getConstant();
	}

    /**
     * Set base height of the waterplane(Used for reflecting the camera for rendering reflection)
     * @param waterHeight Waterplane height
     */
	public void setWaterHeight( float waterHeight ) {
		this.waterPlane.setConstant( waterHeight );
	}

	public Vector3f getNormal() {
		return waterPlane.getNormal();
	}

    /**
     * Set the normal of the waterplane(Used for reflecting the camera for rendering reflection)
     * @param normal Waterplane normal
     */
	public void setNormal( Vector3f normal ) {
		waterPlane.setNormal( normal );
	}

	public float getSpeedReflection() {
		return speedReflection;
	}

    /**
     * Set the movement speed of the reflectiontexture
     * @param speedReflection Speed of reflectiontexture
     */
	public void setSpeedReflection( float speedReflection ) {
		this.speedReflection = speedReflection;
	}

	public float getSpeedRefraction() {
		return speedRefraction;
	}

    /**
     * Set the movement speed of the refractiontexture
     * @param speedRefraction Speed of refractiontexture
     */
	public void setSpeedRefraction( float speedRefraction ) {
		this.speedRefraction = speedRefraction;
	}

	public float getWaterMaxAmplitude() {
		return waterMaxAmplitude;
	}

    /**
     * Maximum amplitude of the water, used for clipping correctly(projected water only)
     * @param waterMaxAmplitude Maximum amplitude
     */
	public void setWaterMaxAmplitude( float waterMaxAmplitude ) {
		this.waterMaxAmplitude = waterMaxAmplitude;
	}

	public float getClipBias() {
		return clipBias;
	}

	public void setClipBias( float clipBias ) {
		this.clipBias = clipBias;
	}

	public Plane getWaterPlane() {
		return waterPlane;
	}

	public void setWaterPlane( Plane waterPlane ) {
		this.waterPlane = waterPlane;
	}

	public Vector3f getTangent() {
		return tangent;
	}

	public void setTangent( Vector3f tangent ) {
		this.tangent = tangent;
	}

	public Vector3f getBinormal() {
		return binormal;
	}

	public void setBinormal( Vector3f binormal ) {
		this.binormal = binormal;
	}

	public Texture getTextureReflect() {
		return textureReflect;
	}

	public Texture getTextureRefract() {
		return textureRefract;
	}

	public Texture getTextureDepth() {
		return textureDepth;
	}

    /**
     * If true, fade to fogcolor. If false, fade to 100% reflective surface
     * @param value
     */
    public void useFadeToFogColor(boolean value) {
        useFadeToFogColor = value;
    }

    public boolean isUseFadeToFogColor() {
        return useFadeToFogColor;
    }

	public boolean isUseReflection() {
		return useReflection;
	}

    /**
     * Turn reflection on and off
     * @param useReflection
     */
	public void setUseReflection(boolean useReflection) {
        if (useReflection == this.useReflection) return;
		this.useReflection = useReflection;
		reloadShader();
	}

	public boolean isUseRefraction() {
		return useRefraction;
	}

    /**
     * Turn refraction on and off
     * @param useRefraction
     */
	public void setUseRefraction(boolean useRefraction) {
        if (useRefraction == this.useRefraction) return;
		this.useRefraction = useRefraction;
		reloadShader();
	}

	public int getRenderScale() {
		return renderScale;
	}

	public void setRenderScale(int renderScale) {
		this.renderScale = renderScale;
	}

    public boolean isUseProjectedShader() {
        return useProjectedShader;
    }

    public void setUseProjectedShader(boolean useProjectedShader) {
        if (useProjectedShader == this.useProjectedShader) return;
        this.useProjectedShader = useProjectedShader;
        reloadShader();
    }

    public float getReflectionThrottle() {
        return reflectionThrottle;
    }

    public void setReflectionThrottle(float reflectionThrottle) {
        this.reflectionThrottle = reflectionThrottle;
    }

    public float getRefractionThrottle() {
        return refractionThrottle;
    }

    public void setRefractionThrottle(float refractionThrottle) {
        this.refractionThrottle = refractionThrottle;
    }

    public TextureState getTextureState() {
        return textureState;
    }

    public void setTextureState(TextureState textureState) {
        this.textureState = textureState;
    }

    public void updateCamera() {
        if (isSupported()) {
            tRenderer.getCamera().setFrustum( cam.getFrustumNear(), cam.getFrustumFar(), cam.getFrustumLeft(), cam.getFrustumRight(), cam.getFrustumTop(), cam.getFrustumBottom() );            
        }
    }

    public void setFallbackTexture(Texture fallbackTexture) {
        this.fallbackTexture = fallbackTexture;
    }

    public Texture getFallbackTexture() {
        return fallbackTexture;
    }

    public void setNormalmapTexture(Texture normalmapTexture) {
        this.normalmapTexture = normalmapTexture;
    }

    public Texture getNormalmapTexture() {
        return normalmapTexture;
    }

    public void setDudvTexture(Texture dudvTexture) {
        this.dudvTexture = dudvTexture;
    }

    public Texture getDudvTexture() {
        return dudvTexture;
    }

    public void setFoamTexture(Texture foamTexture) {
        this.foamTexture = foamTexture;
    }

    public Texture getFoamTexture() {
        return foamTexture;
    }
}
