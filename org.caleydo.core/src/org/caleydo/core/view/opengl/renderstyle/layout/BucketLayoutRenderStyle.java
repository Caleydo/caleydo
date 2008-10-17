package org.caleydo.core.view.opengl.renderstyle.layout;

import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import gleem.linalg.open.Transform;
import org.caleydo.core.view.opengl.camera.EProjectionMode;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteHierarchyLevel;

/**
 * Render style for bucket view.
 * 
 * @author Marc Streit
 */
public class BucketLayoutRenderStyle
	extends ARemoteViewLayoutRenderStyle
{
	/**
	 * Constructor.
	 */
	public BucketLayoutRenderStyle(IViewFrustum viewFrustum)
	{
		super(viewFrustum);
		initLayout();
	}

	/**
	 * Constructor.
	 */
	public BucketLayoutRenderStyle(IViewFrustum viewFrustum,
			final ARemoteViewLayoutRenderStyle previousLayoutStyle)
	{
		super(viewFrustum, previousLayoutStyle);
		initLayout();
	}

	private void initLayout()
	{
		eProjectionMode = EProjectionMode.PERSPECTIVE;

		fScalingFactorUnderInteractionLayer = 0.5f;
		fScalingFactorStackLayer = 0.5f;
		fScalingFactorPoolLayer = 0.025f;
		fScalingFactorMemoLayer = 0.08f;
		fScalingFactorTransitionLayer = 0.1f;
		fScalingFactorSpawnLayer = 0.01f;
	}

	@Override
	public RemoteHierarchyLevel initUnderInteractionLayer()
	{
		Transform transformUnderInteraction = new Transform();
		transformUnderInteraction.setTranslation(new Vec3f(-2, -2, 4 * fZoomFactor));
		transformUnderInteraction.setScale(new Vec3f(fScalingFactorUnderInteractionLayer,
				fScalingFactorUnderInteractionLayer, fScalingFactorUnderInteractionLayer));
		underInteractionLayer.setTransformByPositionIndex(0, transformUnderInteraction);

		return underInteractionLayer;
	}

	@Override
	public RemoteHierarchyLevel initStackLayer()
	{
		Transform transform;

		float fTiltAngleRad_Horizontal;
		float fTiltAngleRad_Vertical;

		fTiltAngleRad_Horizontal = (float) Math
				.acos(((4 * 1 / fAspectRatio - 4 - 2 * fPoolLayerWidth) / 2)
					/ ((float) Math.sqrt(Math.pow(4 * (1 - fZoomFactor), 2)
					+ Math.pow(((4 * 1 / fAspectRatio - 4 - 2 * fPoolLayerWidth) / 2),
							2))));
		fTiltAngleRad_Vertical = Vec3f.convertGrad2Radiant(90);
		
		float fScalingCorrection = ((float) Math.sqrt(Math.pow(4 * (1 - fZoomFactor), 2)
				+ Math.pow(((4 * 1 / fAspectRatio - 4 - 2 * fPoolLayerWidth) / 2), 2))) / 4f;


		// FIXME: handle case when height > width
		// if (fAspectRatio < 1.0)
		// {
		// // 1.0f / fAspectRatio;
		//	    	
		//	    	
		// }
		// else
		// {
		//	    	
		// }

		// TOP BUCKET WALL
		transform = new Transform();
		transform.setTranslation(new Vec3f(-2, 2 - 4f
				* (float) Math.cos(fTiltAngleRad_Vertical) * fScalingCorrection, 4 - 4
				* (float) Math.sin(fTiltAngleRad_Vertical) * (1 - fZoomFactor)));
		transform.setScale(new Vec3f(fScalingFactorStackLayer, fScalingFactorStackLayer
				* (1 - fZoomFactor), fScalingFactorStackLayer * (1 - fZoomFactor)));
		transform.setRotation(new Rotf(new Vec3f(1, 0, 0), fTiltAngleRad_Vertical));
		stackLayer.setTransformByPositionIndex(0, transform);

		// BOTTOM BUCKET WALL
		transform = new Transform();
		transform.setTranslation(new Vec3f(-2, -2, 4));
		transform.setScale(new Vec3f(fScalingFactorStackLayer, fScalingFactorStackLayer
				* (1 - fZoomFactor), fScalingFactorStackLayer * (1 - fZoomFactor)));
		transform.setRotation(new Rotf(new Vec3f(-1, 0, 0), fTiltAngleRad_Vertical));
		stackLayer.setTransformByPositionIndex(2, transform);

		// LEFT BUCKET WALL
		transform = new Transform();
		transform.setTranslation(new Vec3f(fPoolLayerWidth - 2 * 1 / fAspectRatio, -2, 4));
		transform.setScale(new Vec3f(fScalingFactorStackLayer * fScalingCorrection,
				fScalingFactorStackLayer, fScalingFactorStackLayer * fScalingCorrection));
		transform.setRotation(new Rotf(new Vec3f(0, 1, 0), fTiltAngleRad_Horizontal));
		stackLayer.setTransformByPositionIndex(1, transform);

		// RIGHT BUCKET WALL
		transform = new Transform();
		transform.setTranslation(new Vec3f(-fPoolLayerWidth + 2 * 1 / fAspectRatio - 4f
				* (float) Math.cos(fTiltAngleRad_Horizontal) * fScalingCorrection, -2, 4 - 4
				* (float) Math.sin(fTiltAngleRad_Horizontal) * fScalingCorrection));
		transform.setScale(new Vec3f(fScalingFactorStackLayer * fScalingCorrection,
				fScalingFactorStackLayer, fScalingFactorStackLayer * fScalingCorrection));
		transform.setRotation(new Rotf(new Vec3f(0, -1f, 0), fTiltAngleRad_Horizontal));
		stackLayer.setTransformByPositionIndex(3, transform);

		// // OLD static bucket implementation
		//
		// // TOP BUCKET WALL
		// transform = new Transform();
		// transform.setTranslation(new Vec3f(-2, 8 * fScalingFactorStackLayer,
		// 0));
		// transform.setScale(new Vec3f(fScalingFactorStackLayer,
		// fScalingFactorStackLayer, fScalingFactorStackLayer));
		// transform.setRotation(new Rotf(new Vec3f(1, 0, 0),
		// fTiltAngleRad_Vertical));
		// stackLayer.setTransformByPositionIndex(0, transform);
		//
		// // BOTTOM BUCKET WALL
		// transform = new Transform();
		// transform.setTranslation(new Vec3f(-2, -2-2f *
		// (float)Math.cos(fTiltAngleRad_Vertical),
		// (float)Math.sin(fTiltAngleRad_Vertical) * 4f));
		// transform.setScale(new Vec3f(fScalingFactorStackLayer,
		// fScalingFactorStackLayer, fScalingFactorStackLayer));
		// transform.setRotation(new Rotf(new Vec3f(-1, 0, 0),
		// fTiltAngleRad_Vertical));
		// stackLayer.setTransformByPositionIndex(2, transform);

		// // LEFT BUCKET WALL
		// transform = new Transform();
		// transform.setTranslation(new Vec3f(-4f *
		// (float)Math.cos(fTiltAngleRad_Horizontal), 0,
		// (float)Math.sin(fTiltAngleRad_Horizontal) * 4f));
		// transform.setScale(new Vec3f(fScalingFactorStackLayer,
		// fScalingFactorStackLayer, fScalingFactorStackLayer));
		// transform.setRotation(new Rotf(new Vec3f(0, 1, 0),
		// fTiltAngleRad_Horizontal));
		// stackLayer.setTransformByPositionIndex(1, transform);

		// // RIGHT BUCKET WALL
		// transform = new Transform();
		// transform.setTranslation(new Vec3f(8 * fScalingFactorStackLayer, 0,
		// 0));
		// transform.setScale(new Vec3f(fScalingFactorStackLayer,
		// fScalingFactorStackLayer, fScalingFactorStackLayer));
		// transform.setRotation(new Rotf(new Vec3f(0, -1f, 0),
		// fTiltAngleRad_Horizontal));
		// stackLayer.setTransformByPositionIndex(3, transform);

		return stackLayer;
	}

	@Override
	public RemoteHierarchyLevel initPoolLayer(final int iMouseOverViewID)
	{

		float fSelectedScaling = 1;
		float fYAdd = -1.8f;

		int iSelectedViewIndex = poolLayer.getPositionIndexByElementId(iMouseOverViewID);

		for (int iViewIndex = 0; iViewIndex < poolLayer.getCapacity(); iViewIndex++)
		{
			if (iViewIndex == iSelectedViewIndex)
			{
				fSelectedScaling = 3f;
			}
			else
			{
				fSelectedScaling = 1;
			}

			Transform transform = new Transform();
			transform.setTranslation(new Vec3f(-1.93f * 1 / fAspectRatio, fYAdd, 4.1f));

			fYAdd += 0.2f * fSelectedScaling;

			transform.setScale(new Vec3f(fScalingFactorPoolLayer * fSelectedScaling,
					fScalingFactorPoolLayer * fSelectedScaling, fScalingFactorPoolLayer
							* fSelectedScaling));
			poolLayer.setTransformByPositionIndex(iViewIndex, transform);
		}

		return poolLayer;
	}

	@Override
	public RemoteHierarchyLevel initMemoLayer()
	{

		// Create free memo spots
		Transform transform;
		float fMemoPos = -1.83f;
		for (int iMemoIndex = 0; iMemoIndex < memoLayer.getCapacity(); iMemoIndex++)
		{
			// Store current model-view matrix
			transform = new Transform();
			transform.setTranslation(new Vec3f(2f / fAspectRatio - fPoolLayerWidth + 0.07f,
					fMemoPos, 4.01f));
			transform.setScale(new Vec3f(fScalingFactorMemoLayer, fScalingFactorMemoLayer,
					fScalingFactorMemoLayer));
			memoLayer.setTransformByPositionIndex(iMemoIndex, transform);

			fMemoPos += 0.7f;
		}

		// Init trash can position
		fTrashCanXPos = 2f / fAspectRatio - fPoolLayerWidth + 0.2f;
		fTrashCanYPos = 1.65f;
		fTrashCanWidth = 0.35f;
		fTrashCanHeight = 0.3f;

		// Init color bar position
		fColorBarXPos = 2.01f / fAspectRatio;
		fColorBarYPos = -1;
		fColorBarWidth = 0.1f;
		fColorBarHeight = 2f;

		return memoLayer;
	}

	@Override
	public RemoteHierarchyLevel initTransitionLayer()
	{

		Transform transformTransition = new Transform();
		transformTransition.setTranslation(new Vec3f(0, -2f, 0.1f));
		transformTransition.setScale(new Vec3f(fScalingFactorTransitionLayer,
				fScalingFactorTransitionLayer, fScalingFactorTransitionLayer));
		transitionLayer.setTransformByPositionIndex(0, transformTransition);

		return transitionLayer;
	}

	@Override
	public RemoteHierarchyLevel initSpawnLayer()
	{
		Transform transformSpawn = new Transform();
		transformSpawn.setTranslation(new Vec3f(0, 0, 0));
		transformSpawn.setScale(new Vec3f(fScalingFactorSpawnLayer, fScalingFactorSpawnLayer,
				fScalingFactorSpawnLayer));
		spawnLayer.setTransformByPositionIndex(0, transformSpawn);

		return spawnLayer;
	}
}