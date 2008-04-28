package org.caleydo.core.data.view.rep.renderstyle.layout;

import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import gleem.linalg.open.Transform;

import org.caleydo.core.data.view.camera.ViewFrustumBase.ProjectionMode;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.view.opengl.util.JukeboxHierarchyLayer;

/**
 * 
 * Render style for bucket view.
 * 
 * @author Marc Streit
 *
 */
public class BucketLayoutRenderStyle 
extends ARemoteViewLayoutRenderStyle {
	
	/**
	 * Constructor.
	 */
	public BucketLayoutRenderStyle(final IGeneralManager generalManager) 
	{
		super(generalManager);

		initLayout();
	}
	
	/**
	 * Constructor.
	 */
	public BucketLayoutRenderStyle(final IGeneralManager generalManager, 
			final ARemoteViewLayoutRenderStyle previousLayoutStyle) 
	{
		super(generalManager, previousLayoutStyle);
		
		initLayout();
	}
	
	private void initLayout() 
	{		
		projectionMode = ProjectionMode.PERSPECTIVE;
		
		fScalingFactorUnderInteractionLayer = 0.5f;
		fScalingFactorStackLayer = 0.5f;
		fScalingFactorPoolLayer = 0.025f;
		fScalingFactorMemoLayer = 0.08f;
		fScalingFactorTransitionLayer = 0.05f;
		fScalingFactorSpawnLayer = 0.01f;
		
		fTrashCanXPos = 4.07f;
		fTrashCanYPos = 0.05f;
		fTrashCanWidth = 0.35f;
		fTrashCanHeight = 0.35f;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.view.rep.renderstyle.layout.ARemoteViewLayoutRenderStyle#initUnderInteractionLayer()
	 */
	public JukeboxHierarchyLayer initUnderInteractionLayer() {

		Transform transformUnderInteraction = new Transform();
		transformUnderInteraction.setTranslation(new Vec3f(0, 0, 0f));
		transformUnderInteraction.setScale(new Vec3f(fScalingFactorUnderInteractionLayer,
				fScalingFactorUnderInteractionLayer, fScalingFactorUnderInteractionLayer));
		underInteractionLayer.setTransformByPositionIndex(0,
				transformUnderInteraction);
		
		return underInteractionLayer;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.view.rep.renderstyle.layout.ARemoteViewLayoutRenderStyle#initStackLayer()
	 */
	public JukeboxHierarchyLayer initStackLayer() {
		
		float fTiltAngleDegree = 90; // degree
		float fTiltAngleRad = Vec3f.convertGrad2Radiant(fTiltAngleDegree);

		// TOP BUCKET WALL
		Transform transform = new Transform();
		transform.setTranslation(new Vec3f(0, 8 * fScalingFactorStackLayer, 0));
		transform.setScale(new Vec3f(fScalingFactorStackLayer, fScalingFactorStackLayer, fScalingFactorStackLayer));
		transform.setRotation(new Rotf(new Vec3f(1, 0, 0), fTiltAngleRad));
		stackLayer.setTransformByPositionIndex(0, transform);

		// BOTTOM BUCKET WALL
		transform = new Transform();
		transform.setTranslation(new Vec3f(0, 0, 4));
		transform.setScale(new Vec3f(fScalingFactorStackLayer, fScalingFactorStackLayer, fScalingFactorStackLayer));
		transform.setRotation(new Rotf(new Vec3f(-1, 0, 0), fTiltAngleRad));
		stackLayer.setTransformByPositionIndex(2, transform);

		// LEFT BUCKET WALL
		transform = new Transform();
		transform.setTranslation(new Vec3f(0, 0, 4));
		transform.setScale(new Vec3f(fScalingFactorStackLayer, fScalingFactorStackLayer, fScalingFactorStackLayer));
		transform.setRotation(new Rotf(new Vec3f(0, 1, 0), fTiltAngleRad));
		stackLayer.setTransformByPositionIndex(1, transform);

		// RIGHT BUCKET WALL
		transform = new Transform();
		transform.setTranslation(new Vec3f(8 * fScalingFactorStackLayer, 0, 0));
		transform.setScale(new Vec3f(fScalingFactorStackLayer, fScalingFactorStackLayer, fScalingFactorStackLayer));
		transform.setRotation(new Rotf(new Vec3f(0, -1f, 0), fTiltAngleRad));
		stackLayer.setTransformByPositionIndex(3, transform);
		
		return stackLayer;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.view.rep.renderstyle.layout.ARemoteViewLayoutRenderStyle#initPoolLayer()
	 */
	public JukeboxHierarchyLayer initPoolLayer(final int iMouseOverViewID) {
		
		float fSelectedScaling = 1;
		float fYAdd = 0.1f;

		int iSelectedViewIndex = poolLayer
				.getPositionIndexByElementId(iMouseOverViewID);

		for (int iViewIndex = 0; iViewIndex < poolLayer.getCapacity(); iViewIndex++)
		{
			if (iViewIndex == iSelectedViewIndex)
			{
				fSelectedScaling = 3f;
			} else
			{
				fSelectedScaling = 1;
			}
			Transform transform = new Transform();

			transform.setTranslation(new Vec3f(-1.25f, fYAdd, 4.1f));

			fYAdd += 0.2f * fSelectedScaling;

			transform.setScale(new Vec3f(fScalingFactorPoolLayer * fSelectedScaling, 
					fScalingFactorPoolLayer	* fSelectedScaling, 
					fScalingFactorPoolLayer * fSelectedScaling));
			poolLayer.setTransformByPositionIndex(iViewIndex, transform);
		}
		
		return poolLayer;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.view.rep.renderstyle.layout.ARemoteViewLayoutRenderStyle#initMemoLayer()
	 */
	public JukeboxHierarchyLayer initMemoLayer() {

		// Create free memo spots
		Transform transform;
		float fMemoPos = 0.46f;
		for (int iMemoIndex = 0; iMemoIndex < memoLayer.getCapacity(); iMemoIndex++)
		{
			// Store current model-view matrix
			transform = new Transform();
			transform.setTranslation(new Vec3f(4.0f, fMemoPos, 4.1f));
			transform.setScale(new Vec3f(fScalingFactorMemoLayer, fScalingFactorMemoLayer, fScalingFactorMemoLayer));
			memoLayer.setTransformByPositionIndex(iMemoIndex, transform);

			fMemoPos += 0.7f;
		}
		
		return memoLayer;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.view.rep.renderstyle.layout.ARemoteViewLayoutRenderStyle#initTransitionLayer()
	 */
	public JukeboxHierarchyLayer initTransitionLayer() {

		Transform transformTransition = new Transform();
		transformTransition.setTranslation(new Vec3f(1.9f, 0, 0.1f));
		transformTransition.setScale(new Vec3f(fScalingFactorTransitionLayer,
				fScalingFactorTransitionLayer, fScalingFactorTransitionLayer));
		transitionLayer.setTransformByPositionIndex(0, transformTransition);

		return transitionLayer;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.view.rep.renderstyle.layout.ARemoteViewLayoutRenderStyle#initSpawnLayer()
	 */
	public JukeboxHierarchyLayer initSpawnLayer() {

		Transform transformSpawn = new Transform();
		transformSpawn.setTranslation(new Vec3f(4.4f, 3.9f, 4.1f));
		transformSpawn.setScale(new Vec3f(fScalingFactorSpawnLayer,
				fScalingFactorSpawnLayer, fScalingFactorSpawnLayer));
		spawnLayer.setTransformByPositionIndex(0, transformSpawn);
		
		return spawnLayer;
	}
}
