package org.caleydo.core.view.opengl.renderstyle.layout;

import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import gleem.linalg.open.Transform;
import org.caleydo.core.view.opengl.camera.EProjectionMode;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevel;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevelElement;

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

		fScalingFactorFocusLevel = 0.5f;
		fScalingFactorStackLevel = 0.5f;
		fScalingFactorPoolLevel = 0.025f;
		fScalingFactorSelectionLevel = 1f;
		fScalingFactorTransitionLevel = 0.1f;
		fScalingFactorSpawnLevel = 0.01f;
	}

	@Override
	public RemoteLevel initFocusLevel()
	{
		Transform transform = new Transform();
		transform.setTranslation(new Vec3f(-2, -2, 4 * fZoomFactor));
		transform.setScale(new Vec3f(fScalingFactorFocusLevel, fScalingFactorFocusLevel,
				fScalingFactorFocusLevel));

		focusLevel.getElementByPositionIndex(0).setTransform(transform);

		return focusLevel;
	}

	@Override
	public RemoteLevel initStackLevel(boolean bIsZoomedIn)
	{
		Transform transform;

		if (!bIsZoomedIn)
		{
			float fTiltAngleRad_Horizontal;
			float fTiltAngleRad_Vertical;

			fTiltAngleRad_Horizontal = (float) Math
					.acos(((4 * 1 / fAspectRatio - 4 - 2 * fPoolLayerWidth) / 2)
							/ ((float) Math.sqrt(Math.pow(4 * (1 - fZoomFactor), 2)
											+ Math.pow(((4 * 1 / fAspectRatio - 4 - 2 * fPoolLayerWidth) / 2), 2))));
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
			transform.setScale(new Vec3f(fScalingFactorStackLevel, fScalingFactorStackLevel
					* (1 - fZoomFactor), fScalingFactorStackLevel * (1 - fZoomFactor)));
			transform.setRotation(new Rotf(new Vec3f(1, 0, 0), fTiltAngleRad_Vertical));

			stackLevel.getElementByPositionIndex(0).setTransform(transform);

			// LEFT BUCKET WALL
			transform = new Transform();
			transform.setTranslation(new Vec3f(fPoolLayerWidth - 2 * 1 / fAspectRatio, -2, 4));
			transform.setScale(new Vec3f(fScalingFactorStackLevel * fScalingCorrection,
					fScalingFactorStackLevel, fScalingFactorStackLevel * fScalingCorrection));
			transform.setRotation(new Rotf(new Vec3f(0, 1, 0), fTiltAngleRad_Horizontal));

			stackLevel.getElementByPositionIndex(1).setTransform(transform);

			// BOTTOM BUCKET WALL
			transform = new Transform();
			transform.setTranslation(new Vec3f(-2, -2, 4));
			transform.setScale(new Vec3f(fScalingFactorStackLevel, fScalingFactorStackLevel
					* (1 - fZoomFactor), fScalingFactorStackLevel * (1 - fZoomFactor)));
			transform.setRotation(new Rotf(new Vec3f(-1, 0, 0), fTiltAngleRad_Vertical));

			stackLevel.getElementByPositionIndex(2).setTransform(transform);

			// RIGHT BUCKET WALL
			transform = new Transform();
			transform.setTranslation(new Vec3f(-fPoolLayerWidth + 2 * 1 / fAspectRatio - 4f
					* (float) Math.cos(fTiltAngleRad_Horizontal) * fScalingCorrection, -2, 4
					- 4 * (float) Math.sin(fTiltAngleRad_Horizontal) * fScalingCorrection));
			transform.setScale(new Vec3f(fScalingFactorStackLevel * fScalingCorrection,
					fScalingFactorStackLevel, fScalingFactorStackLevel * fScalingCorrection));
			transform.setRotation(new Rotf(new Vec3f(0, -1f, 0), fTiltAngleRad_Horizontal));

			stackLevel.getElementByPositionIndex(3).setTransform(transform);
		}
		else
		{
			float fScalingFactorZoomedIn = 0.4f;

			// TOP BUCKET WALL
			transform = new Transform();
			transform.setTranslation(new Vec3f(-7.25f, 0.8f, -4f));
			transform.setScale(new Vec3f(fScalingFactorZoomedIn, fScalingFactorZoomedIn,
					fScalingFactorZoomedIn));
			transform.setRotation(new Rotf(new Vec3f(0, 0, 0), 0));

			stackLevel.getElementByPositionIndex(0).setTransform(transform);

			// LEFT BUCKET WALL
			transform = new Transform();
			transform.setTranslation(new Vec3f(4.05f, 0.8f, -4f));
			transform.setScale(new Vec3f(fScalingFactorZoomedIn, fScalingFactorZoomedIn,
					fScalingFactorZoomedIn));
			transform.setRotation(new Rotf(new Vec3f(0, 0, 0), 0));

			stackLevel.getElementByPositionIndex(1).setTransform(transform);

			// BOTTOM BUCKET WALL
			transform = new Transform();
			transform.setTranslation(new Vec3f(-7.25f, -4, -4f));
			transform.setScale(new Vec3f(fScalingFactorZoomedIn, fScalingFactorZoomedIn,
					fScalingFactorZoomedIn));
			transform.setRotation(new Rotf(new Vec3f(0, 0, 0), 0));

			stackLevel.getElementByPositionIndex(2).setTransform(transform);

			// RIGHT BUCKET WALL
			transform = new Transform();
			transform.setTranslation(new Vec3f(4.05f, -4, -4f));
			;
			transform.setScale(new Vec3f(fScalingFactorZoomedIn, fScalingFactorZoomedIn,
					fScalingFactorZoomedIn));
			transform.setRotation(new Rotf(new Vec3f(0, 0, 0), 0));

			stackLevel.getElementByPositionIndex(3).setTransform(transform);
		}

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

		return stackLevel;
	}

	@Override
	public RemoteLevel initPoolLevel(boolean bIsZoomedIn,
			int iSelectedRemoteLevelElementID)
	{
		Transform transform;

		float fSelectedScaling = 1;
		float fYAdd = 1.9f;
		float fZ = 0;
		
//		if (bIsZoomedIn)
//		{
//			fZ = 0f;
//		}
//		else
//		{
			fZ = 4.1f;
//		}

		int iRemoteLevelElementIndex = 0;
		for (RemoteLevelElement element : poolLevel.getAllElements())
		{
			if (element.getID() == iSelectedRemoteLevelElementID)
			{
				fSelectedScaling = 1.8f;
				fYAdd -= 0.3f * fSelectedScaling;
			}
			else
			{
				fSelectedScaling = 1;
				fYAdd -= 0.22f * fSelectedScaling;
			}

			transform = new Transform();
			transform.setTranslation(new Vec3f(-1.97f * 1 / fAspectRatio, fYAdd, fZ));

			transform.setScale(new Vec3f(fScalingFactorPoolLevel * fSelectedScaling,
					fScalingFactorPoolLevel * fSelectedScaling, fScalingFactorPoolLevel
							* fSelectedScaling));

			poolLevel.getElementByPositionIndex(iRemoteLevelElementIndex).setTransform(
					transform);
			iRemoteLevelElementIndex++;
		}

		return poolLevel;
	}

	@Override
	public RemoteLevel initMemoLevel()
	{
		Transform transform = new Transform();
		transform.setTranslation(new Vec3f(2f / fAspectRatio - fPoolLayerWidth, -2, 4.01f));
		transform.setScale(new Vec3f(fScalingFactorSelectionLevel,
				fScalingFactorSelectionLevel, fScalingFactorSelectionLevel));

		selectionLevel.getElementByPositionIndex(0).setTransform(transform);

		// Init color bar position
		fColorBarXPos = 2.01f / fAspectRatio;
		fColorBarYPos = -1;
		fColorBarWidth = 0.1f;
		fColorBarHeight = 2f;

		return selectionLevel;
	}

	@Override
	public RemoteLevel initTransitionLevel()
	{
		Transform transform = new Transform();
		transform.setTranslation(new Vec3f(0, -2f, 0.1f));
		transform.setScale(new Vec3f(fScalingFactorTransitionLevel,
				fScalingFactorTransitionLevel, fScalingFactorTransitionLevel));

		transitionLevel.getElementByPositionIndex(0).setTransform(transform);

		return transitionLevel;
	}

	@Override
	public RemoteLevel initSpawnLevel()
	{
		Transform transform = new Transform();
		transform.setTranslation(new Vec3f(0, 0, 0));
		transform.setScale(new Vec3f(fScalingFactorSpawnLevel, fScalingFactorSpawnLevel,
				fScalingFactorSpawnLevel));

		spawnLevel.getElementByPositionIndex(0).setTransform(transform);

		return spawnLevel;
	}
}