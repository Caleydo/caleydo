package org.caleydo.core.data.view.rep.renderstyle.layout;

import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import gleem.linalg.open.Transform;

import org.caleydo.core.data.view.camera.ViewFrustumBase.ProjectionMode;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteHierarchyLayer;

/**
 * 
 * Render style for jukebox view.
 * 
 * @author Marc Streit
 *
 */
public class JukeboxLayoutRenderStyle 
extends ARemoteViewLayoutRenderStyle {

	/**
	 * Constructor.
	 */
	public JukeboxLayoutRenderStyle(final IGeneralManager generalManager) 
	{
		super();	
		initLayout();
	}
	
	/**
	 * Constructor.
	 */
	public JukeboxLayoutRenderStyle(final IGeneralManager generalManager, 
			final ARemoteViewLayoutRenderStyle previousLayoutStyle) 
	{
		super(previousLayoutStyle);
		initLayout();
	}

	private void initLayout() 
	{
		projectionMode = ProjectionMode.ORTHOGRAPHIC;
		
		fScalingFactorUnderInteractionLayer = 0.3f;
		fScalingFactorStackLayer = 0.13f;
		fScalingFactorPoolLayer = 0.02f;
		fScalingFactorMemoLayer = 0.05f;
		fScalingFactorTransitionLayer = 0.025f;
		fScalingFactorSpawnLayer = 0.005f;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.view.rep.renderstyle.layout.ARemoteViewLayoutRenderStyle#initUnderInteractionLayer()
	 */
	public RemoteHierarchyLayer initUnderInteractionLayer() {

		Transform transformUnderInteraction = new Transform();
		transformUnderInteraction.setTranslation(new Vec3f(0.32f/fAspectRatio, -1.05f, 0f));
		transformUnderInteraction.setScale(new Vec3f(fScalingFactorUnderInteractionLayer, 
				fScalingFactorUnderInteractionLayer, fScalingFactorUnderInteractionLayer));
		underInteractionLayer.setTransformByPositionIndex(0, transformUnderInteraction);
		
		return underInteractionLayer;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.view.rep.renderstyle.layout.ARemoteViewLayoutRenderStyle#initStackLayer()
	 */
	public RemoteHierarchyLayer initStackLayer() {

		float fTiltAngleDegree = 57; // degree
		float fTiltAngleRad = Vec3f.convertGrad2Radiant(fTiltAngleDegree);
		float fLayerYPos = 0.95f;
		int iMaxLayers = 4;
	
		// Create free pathway layer spots
		Transform transform;
		for (int iLayerIndex = 0; iLayerIndex < iMaxLayers; iLayerIndex++)
		{
			// Store current model-view matrix
			transform = new Transform();
			transform.setTranslation(new Vec3f(-0.5f/fAspectRatio, fLayerYPos, 0f));
			
			// DKT horizontal stack
			// transform.setTranslation(new Vec3f(-2.7f + fLayerYPos, 1.1f, 0));
			// transform.setRotation(new Rotf(new Vec3f(-0.7f, -1f, 0), fTiltAngleRad));
			transform.setScale(new Vec3f(fScalingFactorStackLayer, fScalingFactorStackLayer, fScalingFactorStackLayer));
			transform.setRotation(new Rotf(new Vec3f(-1f, -0.7f, 0), fTiltAngleRad));
			
			stackLayer.setTransformByPositionIndex(iLayerIndex, transform);
	
			fLayerYPos -= 0.7f;
		}
		
		return stackLayer;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.view.rep.renderstyle.layout.ARemoteViewLayoutRenderStyle#initPoolLayer()
	 */
	public RemoteHierarchyLayer initPoolLayer(final int iMouseOverViewID) {

		float fSelectedScaling = 1;
		float fYAdd = -1f;

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
			transform.setTranslation(new Vec3f(-1.1f/fAspectRatio, fYAdd, 4.1f));

			fYAdd += 0.15f * fSelectedScaling;

			transform.setScale(new Vec3f(fSelectedScaling * fScalingFactorPoolLayer,
					fSelectedScaling * fScalingFactorPoolLayer,
					fSelectedScaling * fScalingFactorPoolLayer));
			poolLayer.setTransformByPositionIndex(iViewIndex, transform);
		}
		
		return poolLayer;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.view.rep.renderstyle.layout.ARemoteViewLayoutRenderStyle#initMemoLayer()
	 */
	public RemoteHierarchyLayer initMemoLayer() {

		// Create free memo spots
		Transform transform;
		float fMemoPos = 1.7f;
		for (int iMemoIndex = 0; iMemoIndex < memoLayer.getCapacity(); iMemoIndex++)
		{
			// Store current model-view matrix
			transform = new Transform();
			transform.setTranslation(new Vec3f(fMemoPos, 0.6f, 0.1f));
			transform.setScale(new Vec3f(fScalingFactorMemoLayer, fScalingFactorMemoLayer, fScalingFactorMemoLayer));
			memoLayer.setTransformByPositionIndex(iMemoIndex, transform);

			fMemoPos += 0.42f;
		}

		fTrashCanXPos = 3.8f;
		fTrashCanYPos = 0.62f;
		fTrashCanWidth = 0.35f;
		fTrashCanHeight = 0.35f;
		
		return memoLayer;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.view.rep.renderstyle.layout.ARemoteViewLayoutRenderStyle#initTransitionLayer()
	 */
	public RemoteHierarchyLayer initTransitionLayer() {

		Transform transformTransition = new Transform();
		transformTransition.setTranslation(new Vec3f(0.6f, 0f, 4.1f));
		transformTransition.setScale(new Vec3f(fScalingFactorTransitionLayer,
				fScalingFactorTransitionLayer, fScalingFactorTransitionLayer));
		transitionLayer.setTransformByPositionIndex(0, transformTransition);
		
		return transitionLayer;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.view.rep.renderstyle.layout.ARemoteViewLayoutRenderStyle#initSpawnLayer()
	 */
	public RemoteHierarchyLayer initSpawnLayer() {

		Transform transformSpawn = new Transform();
		transformSpawn.setTranslation(new Vec3f(-4.4f, 3.9f, 4.1f));
		transformSpawn.setScale(new Vec3f(fScalingFactorSpawnLayer,
				fScalingFactorSpawnLayer, fScalingFactorSpawnLayer));
		spawnLayer.setTransformByPositionIndex(0, transformSpawn);
	
		return spawnLayer;
	}
}
