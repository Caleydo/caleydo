package org.caleydo.core.view.opengl.canvas.remote.jukebox;

import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import gleem.linalg.open.Transform;

import java.awt.Font;
import java.io.File;

import javax.media.opengl.GL;

import org.caleydo.core.data.view.camera.IViewFrustum;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.view.EPickingType;
import org.caleydo.core.view.opengl.canvas.remote.AGLRemoteRendering3D;

import com.sun.opengl.util.j2d.TextRenderer;
import com.sun.opengl.util.texture.TextureCoords;
import com.sun.opengl.util.texture.TextureIO;



/**
 * Implementation of the jukebox setup. It supports the user with the ability to
 * navigate and interact with arbitrary views.
 * 
 * @author Marc Streit
 * 
 */
public class GLCanvasJukebox3D 
extends AGLRemoteRendering3D
{
	private static final float SCALING_FACTOR_UNDER_INTERACTION_LAYER = 0.8f;
	private static final float SCALING_FACTOR_STACK_LAYER = 0.4f;
	private static final float SCALING_FACTOR_POOL_LAYER = 0.04f;
	private static final float SCALING_FACTOR_MEMO_LAYER = 0.11f;
	private static final float SCALING_FACTOR_TRANSITION_LAYER = 0.05f;
	private static final float SCALING_FACTOR_SPAWN_LAYER = 0.01f;
	
	/**
	 * Constructor.
	 * 
	 */
	public GLCanvasJukebox3D(final IGeneralManager generalManager,
			final int iViewId,
			final int iGLCanvasID,
			final String sLabel,
			final IViewFrustum viewFrustum) {

		super(generalManager, iViewId, iGLCanvasID, sLabel, viewFrustum);
		
		Transform transformTransition = new Transform();
		transformTransition.setTranslation(new Vec3f(2f, -0.1f, 0.1f));
		transformTransition.setScale(new Vec3f(SCALING_FACTOR_TRANSITION_LAYER,
				SCALING_FACTOR_TRANSITION_LAYER,
				SCALING_FACTOR_TRANSITION_LAYER));
		transitionLayer.setTransformByPositionIndex(0, transformTransition);
		
		Transform transformUnderInteraction = new Transform();
		transformUnderInteraction.setTranslation(new Vec3f(2f, -0.6f, 0f));
		transformUnderInteraction.setScale(new Vec3f(
				SCALING_FACTOR_UNDER_INTERACTION_LAYER,
				SCALING_FACTOR_UNDER_INTERACTION_LAYER,
				SCALING_FACTOR_UNDER_INTERACTION_LAYER));
		underInteractionLayer.setTransformByPositionIndex(0, transformUnderInteraction);

		Transform transformSpawn = new Transform();
		transformSpawn.setTranslation(new Vec3f(-4.4f, 3.9f, 4.1f));
		transformSpawn.setScale(new Vec3f(SCALING_FACTOR_SPAWN_LAYER,
				SCALING_FACTOR_SPAWN_LAYER, SCALING_FACTOR_SPAWN_LAYER));
		spawnLayer.setTransformByPositionIndex(0, transformSpawn);
		
		glConnectionLineRenderer = new GLConnectionLineRendererJukebox(generalManager,
				underInteractionLayer, stackLayer, poolLayer);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#init(javax.media.opengl.GL)
	 */
	public void init(final GL gl) {
		
		super.init(gl);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#display(javax.media.opengl.GL)
	 */
	public void display(final GL gl) {

		super.display(gl);
	}
	
	protected void buildStackLayer(final GL gl) {
		float fTiltAngleDegree = 57; // degree
		float fTiltAngleRad = Vec3f.convertGrad2Radiant(fTiltAngleDegree);
		float fLayerYPos = 2.9f;
		int iMaxLayers = 4;
	
		// Create free pathway layer spots
		Transform transform;
		for (int iLayerIndex = 0; iLayerIndex < iMaxLayers; iLayerIndex++)
		{
			// Store current model-view matrix
			transform = new Transform();
			transform.setTranslation(new Vec3f(-2.7f, fLayerYPos, 0f));
			
			// DKT horizontal stack
			// transform.setTranslation(new Vec3f(-2.7f + fLayerYPos, 1.1f, 0));
			// transform.setRotation(new Rotf(new Vec3f(-0.7f, -1f, 0), fTiltAngleRad));
			transform.setScale(new Vec3f(SCALING_FACTOR_STACK_LAYER,
					SCALING_FACTOR_STACK_LAYER,
					SCALING_FACTOR_STACK_LAYER));
			transform.setRotation(new Rotf(new Vec3f(-1f, -0.7f, 0), fTiltAngleRad));
			
			stackLayer.setTransformByPositionIndex(iLayerIndex, transform);
	
			fLayerYPos -= 1.5f;
		}
	}
	
	protected void buildMemoLayer(final GL gl) {

		// Create free memo spots
		Transform transform;
		float fMemoPos = 2.1f;
		for (int iMemoIndex = 0; iMemoIndex < memoLayer.getCapacity(); iMemoIndex++)
		{
			// Store current model-view matrix
			transform = new Transform();
			transform.setTranslation(new Vec3f(fMemoPos, -1.7f, 0.1f));
			transform.setScale(new Vec3f(SCALING_FACTOR_MEMO_LAYER,
					SCALING_FACTOR_MEMO_LAYER, SCALING_FACTOR_MEMO_LAYER));
			memoLayer.setTransformByPositionIndex(iMemoIndex, transform);

			fMemoPos += 1f;
		}

		try
		{
			if (this.getClass().getClassLoader().getResource(TRASH_BIN_PATH) != null)
			{
				trashCanTexture = TextureIO.newTexture(TextureIO
						.newTextureData(this.getClass().getClassLoader()
								.getResourceAsStream(TRASH_BIN_PATH), false,
								"PNG"));
			} else
			{
				trashCanTexture = TextureIO
						.newTexture(TextureIO.newTextureData(new File(
								TRASH_BIN_PATH), false, "PNG"));
			}

		} catch (Exception e)
		{
			System.out
					.println("GLPathwayMemoPad.init() Error loading texture from "
							+ TRASH_BIN_PATH);
			e.printStackTrace();
		}
	}
	
	protected void updatePoolLayer() {

		float fSelectedScaling = 1;
		float fYAdd = 0.1f;

		int iSelectedViewIndex = poolLayer.getPositionIndexByElementId(iMouseOverViewID);

		for (int iViewIndex = 0; iViewIndex < poolLayer.getCapacity(); iViewIndex++)
		{
			if (iViewIndex == iSelectedViewIndex)
			{
				fSelectedScaling = 2;
			} else
			{
				fSelectedScaling = 1;
			}
			
			Transform transform = new Transform();
			transform.setTranslation(new Vec3f(-4.2f, fYAdd, 4.1f));

			fYAdd += 0.35f * fSelectedScaling;

			transform.setScale(new Vec3f(SCALING_FACTOR_POOL_LAYER
					* fSelectedScaling, SCALING_FACTOR_POOL_LAYER
					* fSelectedScaling, SCALING_FACTOR_POOL_LAYER
					* fSelectedScaling));
			poolLayer.setTransformByPositionIndex(iViewIndex, transform);
		}
	}
	
	protected void renderMemoPad(final GL gl) {

		if (trashCanTexture == null)
			return;

		gl.glColor4f(0.9f, 0.9f, 0.3f, 0.5f);
		gl.glLineWidth(4);
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(2f, -0.7f, 0);
		gl.glVertex3f(2f, -1.8f, 0);
		gl.glVertex3f(8.4f, -1.8f, 0);
		gl.glVertex3f(8.4f, -0.7f, 0);
		gl.glEnd();

		gl.glColor4f(0.4f, 0.4f, 0.4f, 0.8f);
		gl.glLineWidth(4);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(2f, -0.7f, 0);
		gl.glVertex3f(2f, -1.8f, 0);
		gl.glVertex3f(8.4f, -1.8f, 0);
		gl.glVertex3f(8.4f, -0.7f, 0);
		gl.glEnd();
		
		TextureCoords texCoords = trashCanTexture.getImageTexCoords();

		gl.glPushName(generalManager.getSingelton().getViewGLCanvasManager()
				.getPickingManager().getPickingID(iUniqueId,
						EPickingType.MEMO_PAD_SELECTION,
						MEMO_PAD_TRASH_CAN_PICKING_ID));

		trashCanTexture.enable();
		trashCanTexture.bind();

		gl.glColor3f(1, 1, 1);

		gl.glBegin(GL.GL_QUADS);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(7.3f, -1.7f, 0.1f);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(8f, -1.7f, 0.1f);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(8f, -0.9f, 0.1f);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(7.3f, -0.9f, 0.1f);
		gl.glEnd();

		trashCanTexture.disable();

		gl.glPopName();

//		if (textRenderer == null)
//			return;
//
//		String sTmp = "MEMO AREA";
//
//		textRenderer.begin3DRendering();
//		textRenderer.setColor(0.7f, 0.7f, 0.7f, 1.0f);
//
//		float fPosition = 3f;
//		for (int iCharacterIndex = 0; iCharacterIndex < sTmp.length(); iCharacterIndex++)
//		{
//			textRenderer.draw3D((String)(sTmp.subSequence(iCharacterIndex,
//					iCharacterIndex + 1)), -1.03f, fPosition, 4.001f, 0.003f); // scale
//																				// factor
//
//			fPosition -= 0.3f;
//		}
//
//		// TODO: move this to a display list and out of this method
//		sTmp = "POOL AREA";
//
//		fPosition = 3f;
//		for (int iCharacterIndex = 0; iCharacterIndex < sTmp.length(); iCharacterIndex++)
//		{
//			textRenderer.draw3D((String)(sTmp.subSequence(iCharacterIndex,
//					iCharacterIndex + 1)), 4.79f, fPosition, 4.001f, 0.003f); // scale
//																				// factor
//
//			fPosition -= 0.3f;
//		}
//
//		textRenderer.end3DRendering();
	}
	
	protected void renderPoolLayerBackground(final GL gl) {
		
		// not used in jukebox
	}
}