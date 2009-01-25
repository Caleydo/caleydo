package org.caleydo.core.view.opengl.renderstyle.layout;

import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import gleem.linalg.open.Transform;

import org.caleydo.core.util.wii.WiiRemote;
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
	private float fInputDeviceXCorrection = 0;

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
		transform.setTranslation(new Vec3f(-2 + fInputDeviceXCorrection, -2, 4 * fZoomFactor));
		transform.setScale(new Vec3f(fScalingFactorFocusLevel, fScalingFactorFocusLevel,
			fScalingFactorFocusLevel));

		focusLevel.getElementByPositionIndex(0).setTransform(transform);

		return focusLevel;
	}
	
	public RemoteLevel initFocusLevelWii(WiiRemote wiiRemote)
	{
		float[] fArHeadPosition = wiiRemote.getCurrentSmoothHeadPosition();
		
		fArHeadPosition[0] = fArHeadPosition[0] * 4 + 4;
		fArHeadPosition[1] *= 4;
		
		float fBucketWidth = 2f;
		float fBucketHeight = 2f;
		float fBucketDepth = 4.0f;
		float fBucketBottomLeft = -1*fArHeadPosition[0] - fBucketWidth - 1.5f;
		float fBucketBottomRight = -1*fArHeadPosition[0] + fBucketWidth - 1.5f;
		float fBucketBottomTop = fArHeadPosition[1] + fBucketHeight;
		float fBucketBottomBottom = fArHeadPosition[1] - fBucketHeight;		
		
		//FIXME: x must take 2.44 as width
		float fNormalizedHeadDist = -1*wiiRemote.getCurrentHeadDistance() + 7f
			+ Math.abs(fBucketBottomRight -2)/2 
			+ Math.abs(fBucketBottomTop - 2) /2;
		
		float fXScaling = (4*2 - Math.abs(fBucketBottomLeft) - Math.abs(fBucketBottomRight)) / (4*2);
		float fYScaling = (4*2 - Math.abs(fBucketBottomBottom) - Math.abs(fBucketBottomTop)) / (4*2);
		
		Transform transform = new Transform();
		transform.setTranslation(new Vec3f(fBucketBottomLeft, fBucketBottomBottom, fNormalizedHeadDist));
		transform.setScale(new Vec3f(fXScaling,fYScaling,1));
		focusLevel.getElementByPositionIndex(0).setTransform(transform);
		
		return focusLevel;
	}
	
	public RemoteLevel initStackLevelWii(WiiRemote wiiRemote)
	{
		float[] fArHeadPosition = wiiRemote.getCurrentSmoothHeadPosition();
		
		fArHeadPosition[0] = fArHeadPosition[0] * 4 + 4;
		fArHeadPosition[1] *= 4;
		
		float fBucketWidth = 2;//2.44f;
		float fBucketHeight = 2f;
		float fBucketDepth = 4.0f;
		float fBucketBottomLeft = -1*fArHeadPosition[0] - fBucketWidth - 1.5f;
		float fBucketBottomRight = -1*fArHeadPosition[0] + fBucketWidth - 1.5f;
		float fBucketBottomTop = fArHeadPosition[1] + fBucketHeight;
		float fBucketBottomBottom = fArHeadPosition[1] - fBucketHeight;			

		//FIXME: x must take 2.44 as width
		float fNormalizedHeadDist = -1*wiiRemote.getCurrentHeadDistance() + 7f 
			+ Math.abs(fBucketBottomRight -2)/2 
			+ Math.abs(fBucketBottomTop - 2) /2;
		
//		System.out.println("Head dist:" +fNormalizedHeadDist);
//		System.out.println("bottom left:" +fBucketBottomLeft);
		
		Transform transform;
		
		float fAK = 4 -1*fNormalizedHeadDist;
		float fGK = fBucketHeight - fBucketBottomTop;
		float fAngle = (float) Math.atan((fAK/fGK));
		
		// Top plane
		transform = new Transform();
		transform.setTranslation(new Vec3f(-fBucketWidth, fBucketHeight, fBucketDepth));
		transform.setRotation(new Rotf(new Vec3f(1,0,0), (float) (Vec3f.convertGrad2Radiant(0)+fAngle)));
		transform.setScale(new Vec3f(1,1,1));
		stackLevel.getElementByPositionIndex(0).setTransform(transform);
		
		fAK = 4 -1*fNormalizedHeadDist;
		fGK = fBucketWidth + fBucketBottomLeft;
		fAngle = (float) Math.atan((fGK/fAK));
		
		// Left plane
		transform = new Transform();
		transform.setTranslation(new Vec3f(-fBucketWidth, 0, fBucketDepth));
		transform.setRotation(new Rotf(new Vec3f(0,1,0), (float) (Vec3f.convertGrad2Radiant(90)-fAngle)));
		transform.setScale(new Vec3f(1,1,1));
		stackLevel.getElementByPositionIndex(1).setTransform(transform);
		
		fAK = 4 -1*fNormalizedHeadDist;
		fGK = fBucketWidth + fBucketBottomBottom;
		fAngle = (float) Math.atan((fGK/fAK));
		
		// Bottom plane
		transform = new Transform();
		transform.setTranslation(new Vec3f(-fBucketWidth, -fBucketWidth, fBucketDepth));
		transform.setRotation(new Rotf(new Vec3f(-1,0,0), (float) (Vec3f.convertGrad2Radiant(90)-fAngle)));
		transform.setScale(new Vec3f(1,1,1));
		stackLevel.getElementByPositionIndex(2).setTransform(transform);
	
		fAK = 4 -1*fNormalizedHeadDist;
		fGK = fBucketWidth - fBucketBottomRight;
		fAngle = (float) Math.atan((fGK/fAK));
		
		// Right plane
		transform = new Transform();
		transform.setTranslation(new Vec3f(fBucketWidth, 0, fBucketDepth));
		transform.setRotation(new Rotf(new Vec3f(0,-1,0), (float) (Vec3f.convertGrad2Radiant(270)-fAngle)));
		transform.setScale(new Vec3f(1,1,1));
		stackLevel.getElementByPositionIndex(3).setTransform(transform);
		
		return stackLevel;
	}
	
	@Override
	public RemoteLevel initStackLevel(boolean bIsZoomedIn)
	{
		Transform transform;

		if (!bIsZoomedIn)
		{
			float fTiltAngleRad_Horizontal;
			float fTiltAngleRad_Vertical;

			fTiltAngleRad_Horizontal =
				(float) Math
					.acos(((4 * 1 / fAspectRatio - 4 - 2 * fPoolLayerWidth) / 2)
						/ ((float) Math.sqrt(Math.pow(4 * (1 - fZoomFactor), 2)
							+ Math.pow(((4 * 1 / fAspectRatio - 4 - 2 * fPoolLayerWidth) / 2),
								2))));
			
			fTiltAngleRad_Vertical = Vec3f.convertGrad2Radiant(90);

			float fScalingCorrection =
				((float) Math.sqrt(Math.pow(4 * (1 - fZoomFactor), 2)
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

			fInputDeviceXCorrection = 0;// 2 * 1 / fAspectRatio - 4f
			// * (float) Math.cos(fTiltAngleRad_Horizontal - 0.4f) *
			// fScalingCorrection;

			// TOP BUCKET WALL
			transform = new Transform();
			transform.setTranslation(new Vec3f(-2 + fInputDeviceXCorrection, 2 - 4f
				* (float) Math.cos(fTiltAngleRad_Vertical) * fScalingCorrection, 4 - 4
				* (float) Math.sin(fTiltAngleRad_Vertical) * (1 - fZoomFactor)));
			transform.setScale(new Vec3f(fScalingFactorStackLevel, fScalingFactorStackLevel
				* (1 - fZoomFactor), fScalingFactorStackLevel * (1 - fZoomFactor)));
			transform.setRotation(new Rotf(new Vec3f(1, 0, 0), fTiltAngleRad_Vertical));

			stackLevel.getElementByPositionIndex(0).setTransform(transform);

			// fTiltAngleRad_Horizontal -= 0.4f;

			// LEFT BUCKET WALL
			transform = new Transform();
			transform.setTranslation(new Vec3f(fPoolLayerWidth - 2 * 1 / fAspectRatio, -2, 4));
			transform.setScale(new Vec3f(fScalingFactorStackLevel * fScalingCorrection,
				fScalingFactorStackLevel, fScalingFactorStackLevel * fScalingCorrection));
			transform.setRotation(new Rotf(new Vec3f(0, 1, 0), fTiltAngleRad_Horizontal));

			stackLevel.getElementByPositionIndex(1).setTransform(transform);

			// BOTTOM BUCKET WALL
			transform = new Transform();
			transform.setTranslation(new Vec3f(-2 + fInputDeviceXCorrection, -2, 4));
			transform.setScale(new Vec3f(fScalingFactorStackLevel, fScalingFactorStackLevel
				* (1 - fZoomFactor), fScalingFactorStackLevel * (1 - fZoomFactor)));
			transform.setRotation(new Rotf(new Vec3f(-1, 0, 0), fTiltAngleRad_Vertical));

			stackLevel.getElementByPositionIndex(2).setTransform(transform);

			// fTiltAngleRad_Horizontal += 0.8f;

			// RIGHT BUCKET WALL
			transform = new Transform();
			transform.setTranslation(new Vec3f(-fPoolLayerWidth + 2 * 1 / fAspectRatio - 4f
				* (float) Math.cos(fTiltAngleRad_Horizontal) * fScalingCorrection, -2, 4 - 4
				* (float) Math.sin(fTiltAngleRad_Horizontal) * fScalingCorrection));
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
			transform.setTranslation(new Vec3f(4.05f, -4, -4f));;
			transform.setScale(new Vec3f(fScalingFactorZoomedIn, fScalingFactorZoomedIn,
				fScalingFactorZoomedIn));
			transform.setRotation(new Rotf(new Vec3f(0, 0, 0), 0));

			stackLevel.getElementByPositionIndex(3).setTransform(transform);
		}

		return stackLevel;
	}

	@Override
	public RemoteLevel initPoolLevel(boolean bIsZoomedIn, int iSelectedRemoteLevelElementID)
	{
		Transform transform;

		float fSelectedScaling = 1;
		float fYAdd = 1.9f;
		float fZ = 0;

		// if (bIsZoomedIn)
		// {
		// fZ = 0f;
		// }
		// else
		// {
		fZ = 4.1f;
		// }

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
				fYAdd -= 0.25f * fSelectedScaling;
			}

			transform = new Transform();
			transform.setTranslation(new Vec3f(-2f / fAspectRatio, fYAdd, fZ));

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
		transform.setTranslation(new Vec3f(2f / fAspectRatio - fPoolLayerWidth + 0.12f, -2,
			4.01f));
		transform.setScale(new Vec3f(fScalingFactorSelectionLevel,
			fScalingFactorSelectionLevel, fScalingFactorSelectionLevel));

		selectionLevel.getElementByPositionIndex(0).setTransform(transform);

		// Init color bar position
		fColorBarXPos = 2.05f / fAspectRatio;
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