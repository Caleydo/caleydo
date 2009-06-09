package org.caleydo.core.view.opengl.canvas.remote.bucket;

import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import gleem.linalg.open.Transform;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.camera.EProjectionMode;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.remote.ARemoteViewLayoutRenderStyle;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevel;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevelElement;

/**
 * Render style for bucket view.
 * 
 * @author Marc Streit
 */
public class BucketLayoutRenderStyle
	extends ARemoteViewLayoutRenderStyle {
	public final static float BUCKET_WIDTH = 2f;
	public final static float BUCKET_HEIGHT = 2f;
	public final static float BUCKET_DEPTH = 4f;

	public final static float SIDE_PANEL_WIDTH = 0.8f;

	private float fBucketBottomLeft = 0;
	private float fBucketBottomRight = 0;
	private float fBucketBottomTop = 0;
	private float fBucketBottomBottom = 0;
	private float fHeadDist = 0;
	private float[] fArHeadPosition;
	
	private boolean bIsZoomedIn = false;

	/**
	 * Constructor.
	 */
	public BucketLayoutRenderStyle(IViewFrustum viewFrustum) {
		super(viewFrustum);
		initLayout();
	}

	/**
	 * Constructor.
	 */
	public BucketLayoutRenderStyle(IViewFrustum viewFrustum,
		final ARemoteViewLayoutRenderStyle previousLayoutStyle) {
		super(viewFrustum, previousLayoutStyle);
		initLayout();
	}

	private void initLayout() {
		eProjectionMode = EProjectionMode.PERSPECTIVE;

		fScalingFactorFocusLevel = 0.5f;
		fScalingFactorStackLevel = 0.5f;
		fScalingFactorPoolLevel = 0.025f;
		fScalingFactorSelectionLevel = 1f;
		fScalingFactorTransitionLevel = 0.1f;
		fScalingFactorSpawnLevel = 0.01f;
	}

	@Override
	public RemoteLevel initFocusLevel() {
		Transform transform = new Transform();

		float fXScaling = 1;
		float fYScaling = 1;

		if (fAspectRatio < 1f) {
			fXScaling = 1 / fAspectRatio;
			fYScaling = 1;
		}
		else {
			fXScaling = 1;
			fYScaling = fAspectRatio;
		}

		float fLeftSceneBorder = -2 * fXScaling + SIDE_PANEL_WIDTH;
		float fBottomSceneBorder = -2 * fYScaling;

		if (bIsZoomedIn) {
			transform.setTranslation(new Vec3f(fLeftSceneBorder, fBottomSceneBorder, 0));

			if (fAspectRatio < 0.65f) {

				if (fAspectRatio < 0.61f) {
					transform.setScale(new Vec3f(fScalingFactorFocusLevel * fYScaling* 0.97f,
						fScalingFactorFocusLevel * fYScaling* 0.97f, fScalingFactorFocusLevel));
				}
				else {
					transform.setScale(new Vec3f(fScalingFactorFocusLevel * fYScaling * 0.9f,
						fScalingFactorFocusLevel * fYScaling * 0.9f, fScalingFactorFocusLevel));
				}
			}
			else {
				if (fAspectRatio > 0.75f) {
					transform.setScale(new Vec3f(fScalingFactorFocusLevel * fXScaling * 0.6f,
						fScalingFactorFocusLevel * fXScaling * 0.6f, fScalingFactorFocusLevel));
				}
				else {
					transform.setScale(new Vec3f(fScalingFactorFocusLevel * fXScaling * 0.52f,
						fScalingFactorFocusLevel * fXScaling * 0.52f, fScalingFactorFocusLevel));
				}
			}
		}
		else {
			transform.setTranslation(new Vec3f(-2, -2, 0));
			transform.setScale(new Vec3f(fScalingFactorFocusLevel, fScalingFactorFocusLevel,
				fScalingFactorFocusLevel));
		}

		focusLevel.getElementByPositionIndex(0).setTransform(transform);

		return focusLevel;
	}

	@Override
	public RemoteLevel initStackLevel() {
		Transform transform;

		if (!bIsZoomedIn) {
			float fTiltAngleRad_Horizontal;
			float fTiltAngleRad_Vertical;

			fTiltAngleRad_Horizontal =
				(float) Math.acos((4 * 1 / fAspectRatio - 4 - 2 * fPoolLayerWidth)
					/ 2
					/ (float) Math.sqrt(Math.pow(4 * (1 - fZoomFactor), 2)
						+ Math.pow(((4 * 1 / fAspectRatio - 4 - 2 * fPoolLayerWidth) / 2), 2)));

			fTiltAngleRad_Vertical = Vec3f.convertGrad2Radiant(90);

			float fScalingCorrection =
				(float) Math.sqrt(Math.pow(4 * (1 - fZoomFactor), 2)
					+ Math.pow(((4 * 1 / fAspectRatio - 4 - 2 * fPoolLayerWidth) / 2), 2)) / 4f;

			// handle case when height > width
			if (fAspectRatio > 0.71f) {
				if (fAspectRatio < 1) {
					fAspectRatio = 1 / fAspectRatio;
				}

				fTiltAngleRad_Vertical =
					(float) Math.acos((4 * fAspectRatio - 4)
						/ 2
						/ (float) Math.sqrt(Math.pow(4 * (1 - fZoomFactor), 2)
							+ Math.pow(((4 * fAspectRatio - 4) / 2), 2)));

				fTiltAngleRad_Horizontal = Vec3f.convertGrad2Radiant(90);

				fScalingCorrection =
					(float) Math.sqrt(Math.pow(4 * (1 - fZoomFactor), 2)
						+ Math.pow(((4 * fAspectRatio - 4) / 2), 2)) / 4f;

				// TOP BUCKET WALL
				transform = new Transform();
				transform.setTranslation(new Vec3f(-2, 2 * fAspectRatio - 4f
					* (float) Math.cos(fTiltAngleRad_Vertical) * fScalingCorrection, 4 - 4
					* (float) Math.sin(fTiltAngleRad_Vertical) * (1 - fZoomFactor)));
				transform.setScale(new Vec3f(fScalingFactorStackLevel, fScalingFactorStackLevel
					* fScalingCorrection, fScalingFactorStackLevel * fScalingCorrection));
				transform.setRotation(new Rotf(new Vec3f(1, 0, 0), fTiltAngleRad_Vertical));
				stackLevel.getElementByPositionIndex(0).setTransform(transform);

				// BOTTOM BUCKET WALL
				transform = new Transform();
				transform.setTranslation(new Vec3f(-2, -2 * fAspectRatio, 4));
				transform.setScale(new Vec3f(fScalingFactorStackLevel, fScalingFactorStackLevel
					* fScalingCorrection, fScalingFactorStackLevel * fScalingCorrection));
				transform.setRotation(new Rotf(new Vec3f(-1, 0, 0), fTiltAngleRad_Vertical));
				stackLevel.getElementByPositionIndex(2).setTransform(transform);

				// LEFT BUCKET WALL
				transform = new Transform();
				transform.setTranslation(new Vec3f(-2, -2, 4));
				transform.setScale(new Vec3f(fScalingFactorStackLevel, fScalingFactorStackLevel
					* (1 - fZoomFactor), fScalingFactorStackLevel * (1 - fZoomFactor)));
				transform.setRotation(new Rotf(new Vec3f(0, 1, 0), fTiltAngleRad_Horizontal));
				stackLevel.getElementByPositionIndex(1).setTransform(transform);

				// RIGHT BUCKET WALL
				transform = new Transform();
				transform.setTranslation(new Vec3f(2 - 4f * (float) Math.cos(fTiltAngleRad_Horizontal)
					* fScalingCorrection, -2, 4 - 4 * (float) Math.sin(fTiltAngleRad_Horizontal)
					* fScalingCorrection));
				transform.setScale(new Vec3f(fScalingFactorStackLevel, fScalingFactorStackLevel
					* (1 - fZoomFactor), fScalingFactorStackLevel * (1 - fZoomFactor)));
				transform.setRotation(new Rotf(new Vec3f(0, -1f, 0), fTiltAngleRad_Horizontal));
				stackLevel.getElementByPositionIndex(3).setTransform(transform);

			}
			else {
				// TOP BUCKET WALL
				transform = new Transform();
				transform.setTranslation(new Vec3f(-2, 2 - 4f * (float) Math.cos(fTiltAngleRad_Vertical)
					* fScalingCorrection, 4 - 4 * (float) Math.sin(fTiltAngleRad_Vertical)
					* (1 - fZoomFactor)));
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
					* (float) Math.cos(fTiltAngleRad_Horizontal) * fScalingCorrection, -2, 4 - 4
					* (float) Math.sin(fTiltAngleRad_Horizontal) * fScalingCorrection));
				transform.setScale(new Vec3f(fScalingFactorStackLevel * fScalingCorrection,
					fScalingFactorStackLevel, fScalingFactorStackLevel * fScalingCorrection));
				transform.setRotation(new Rotf(new Vec3f(0, -1f, 0), fTiltAngleRad_Horizontal));
				stackLevel.getElementByPositionIndex(3).setTransform(transform);

			}

		}
		else {
			float fScalingFactorZoomedIn;

			float fXScaling = 1;
			float fYScaling = 1;

			if (fAspectRatio < 1f) {
				fXScaling = 1 / fAspectRatio;
				fYScaling = 1;
			}
			else {
				fXScaling = 1;
				fYScaling = fAspectRatio;
			}

			float fLeftSceneBorder = 0;
			float fBottomSceneBorder = 0;

			boolean bVerticalStack = true;
			if (fAspectRatio < 0.75f) {
				bVerticalStack = true;
				fLeftSceneBorder = (2 * fXScaling - 2.05f * SIDE_PANEL_WIDTH);
				fBottomSceneBorder = -2 * fYScaling;
				fScalingFactorZoomedIn = 0.113f;
			}
			else {
				bVerticalStack = false;
				fLeftSceneBorder = (-2 * fXScaling + SIDE_PANEL_WIDTH);
				fBottomSceneBorder = (2 - 0.65f) * fYScaling;
				fScalingFactorZoomedIn = 0.08f;
			}

			// TOP BUCKET WALL
			transform = new Transform();
			transform.setTranslation(new Vec3f(fLeftSceneBorder, fBottomSceneBorder, 0f));
			transform.setScale(new Vec3f(fScalingFactorZoomedIn, fScalingFactorZoomedIn,
				fScalingFactorZoomedIn));
			transform.setRotation(new Rotf(new Vec3f(0, 0, 0), 0));

			stackLevel.getElementByPositionIndex(0).setTransform(transform);

			// LEFT BUCKET WALL
			transform = new Transform();
			if (bVerticalStack)
				transform.setTranslation(new Vec3f(fLeftSceneBorder, fBottomSceneBorder + 9
					* fScalingFactorZoomedIn, 0));
			else
				transform.setTranslation(new Vec3f(fLeftSceneBorder + 9 * fScalingFactorZoomedIn,
					fBottomSceneBorder, 0));
			transform.setScale(new Vec3f(fScalingFactorZoomedIn, fScalingFactorZoomedIn,
				fScalingFactorZoomedIn));
			transform.setRotation(new Rotf(new Vec3f(0, 0, 0), 0));

			stackLevel.getElementByPositionIndex(1).setTransform(transform);

			// BOTTOM BUCKET WALL
			transform = new Transform();
			if (bVerticalStack)
				transform.setTranslation(new Vec3f(fLeftSceneBorder, fBottomSceneBorder + 18
					* fScalingFactorZoomedIn, 0));
			else
				transform.setTranslation(new Vec3f(fLeftSceneBorder + 18 * fScalingFactorZoomedIn,
					fBottomSceneBorder, 0));
			transform.setScale(new Vec3f(fScalingFactorZoomedIn, fScalingFactorZoomedIn,
				fScalingFactorZoomedIn));
			transform.setRotation(new Rotf(new Vec3f(0, 0, 0), 0));

			stackLevel.getElementByPositionIndex(2).setTransform(transform);

			// RIGHT BUCKET WALL
			transform = new Transform();
			if (bVerticalStack)
				transform.setTranslation(new Vec3f(fLeftSceneBorder, fBottomSceneBorder + 27
					* fScalingFactorZoomedIn, 0));
			else
				transform.setTranslation(new Vec3f(fLeftSceneBorder + 27 * fScalingFactorZoomedIn,
					fBottomSceneBorder, 0));
			transform.setScale(new Vec3f(fScalingFactorZoomedIn, fScalingFactorZoomedIn,
				fScalingFactorZoomedIn));
			transform.setRotation(new Rotf(new Vec3f(0, 0, 0), 0));

			stackLevel.getElementByPositionIndex(3).setTransform(transform);

		}

		return stackLevel;
	}

	@Override
	public RemoteLevel initPoolLevel(int iSelectedRemoteLevelElementID) {
		Transform transform;

		float fSelectedScaling = 1;
		float fYAdd = 1.9f;
		float fZ = 0;

		if (bIsZoomedIn)
			fZ = 0.1f;
		else
			fZ = 4.1f;

		int iRemoteLevelElementIndex = 0;
		for (RemoteLevelElement element : poolLevel.getAllElements()) {
			if (element.getID() == iSelectedRemoteLevelElementID) {
				fSelectedScaling = 1.8f;
				fYAdd -= 0.3f * fSelectedScaling;
			}
			else {
				fSelectedScaling = 1;
				fYAdd -= 0.25f * fSelectedScaling;
			}

			transform = new Transform();
			transform.setTranslation(new Vec3f(-2f / fAspectRatio, fYAdd, fZ));

			transform.setScale(new Vec3f(fScalingFactorPoolLevel * fSelectedScaling, fScalingFactorPoolLevel
				* fSelectedScaling, fScalingFactorPoolLevel * fSelectedScaling));

			poolLevel.getElementByPositionIndex(iRemoteLevelElementIndex).setTransform(transform);
			iRemoteLevelElementIndex++;
		}

		return poolLevel;
	}

	@Override
	public RemoteLevel initMemoLevel() {

		float fZ = 0;
		if (bIsZoomedIn)
			fZ = 0.02f;
		else
			fZ = 4.02f;

		Transform transform = new Transform();
		transform.setTranslation(new Vec3f(2f / fAspectRatio - fPoolLayerWidth + 0.10f, -2.01f, fZ));
		transform.setScale(new Vec3f(fScalingFactorSelectionLevel, fScalingFactorSelectionLevel,
			fScalingFactorSelectionLevel));

		selectionLevel.getElementByPositionIndex(0).setTransform(transform);

		return selectionLevel;
	}

	@Override
	public RemoteLevel initTransitionLevel() {
		Transform transform = new Transform();
		transform.setTranslation(new Vec3f(0, -2f, 0.1f));
		transform.setScale(new Vec3f(fScalingFactorTransitionLevel, fScalingFactorTransitionLevel,
			fScalingFactorTransitionLevel));

		transitionLevel.getElementByPositionIndex(0).setTransform(transform);

		return transitionLevel;
	}

	@Override
	public RemoteLevel initSpawnLevel() {
		
		Transform transform = new Transform();
		transform.setTranslation(new Vec3f(-2f, 0, 0f));
		transform.setScale(new Vec3f(fScalingFactorSpawnLevel, fScalingFactorSpawnLevel,
			fScalingFactorSpawnLevel));

		spawnLevel.getElementByPositionIndex(0).setTransform(transform);

		return spawnLevel;
	}

	public RemoteLevel initFocusLevelTrack() {
		fArHeadPosition = GeneralManager.get().getTrackDataProvider().get2DTrackData();
		fArHeadPosition[0] = fArHeadPosition[0] / 1280f * 4 - 4;
		fArHeadPosition[1] = fArHeadPosition[1] / 1024f * 4 - 4;

		fBucketBottomLeft = -1 * fArHeadPosition[0] - BUCKET_WIDTH - 1.5f;
		fBucketBottomRight = -1 * fArHeadPosition[0] + BUCKET_WIDTH - 1.5f;
		fBucketBottomTop = fArHeadPosition[1] + BUCKET_HEIGHT;
		fBucketBottomBottom = fArHeadPosition[1] - BUCKET_HEIGHT;

		fHeadDist =
			-1 * GeneralManager.get().getWiiRemote().getCurrentHeadDistance() + 7f
				+ Math.abs(fBucketBottomRight - 2) / 2 + Math.abs(fBucketBottomTop - 2) / 2;

		float fXScaling = (4 * 2 - Math.abs(fBucketBottomLeft - fBucketBottomRight)) / (4 * 2);
		float fYScaling = (4 * 2 - Math.abs(fBucketBottomBottom - fBucketBottomTop)) / (4 * 2);

		// float fXScaling = (4*2 - Math.abs(fBucketBottomLeft) - Math.abs(fBucketBottomRight)) / (4*2);
		// float fYScaling = (4*2 - Math.abs(fBucketBottomBottom) - Math.abs(fBucketBottomTop)) / (4*2);

		Transform transform = new Transform();
		transform.setTranslation(new Vec3f(fBucketBottomLeft, fBucketBottomBottom, fHeadDist));
		transform.setScale(new Vec3f(fXScaling, fYScaling, 1));
		focusLevel.getElementByPositionIndex(0).setTransform(transform);

		return focusLevel;
	}

	public RemoteLevel initStackLevelTrack() {
		Transform transform;

		float fAK = BUCKET_DEPTH - 1 * fHeadDist;
		float fGK = BUCKET_HEIGHT - fBucketBottomTop;
		float fAngle = (float) Math.atan((fGK / fAK));

		// Top plane
		transform = new Transform();
		transform.setTranslation(new Vec3f(-BUCKET_WIDTH, BUCKET_HEIGHT, BUCKET_DEPTH));
		transform
			.setRotation(new Rotf(new Vec3f(1, 0, 0), (float) (Vec3f.convertGrad2Radiant(270) - fAngle)));
		transform.setScale(new Vec3f(1, 1, 1));
		stackLevel.getElementByPositionIndex(0).setTransform(transform);

		fGK = BUCKET_WIDTH + fBucketBottomLeft;
		fAngle = (float) Math.atan((fGK / fAK));

		// Left plane
		transform = new Transform();
		transform.setTranslation(new Vec3f(-BUCKET_WIDTH, 0, BUCKET_DEPTH));
		transform.setRotation(new Rotf(new Vec3f(0, 1, 0), (float) (Vec3f.convertGrad2Radiant(90) - fAngle)));
		transform.setScale(new Vec3f(1, 1, 1));
		stackLevel.getElementByPositionIndex(1).setTransform(transform);

		fGK = BUCKET_WIDTH + fBucketBottomBottom;
		fAngle = (float) Math.atan((fGK / fAK));

		// Bottom plane
		transform = new Transform();
		transform.setTranslation(new Vec3f(-BUCKET_WIDTH, -BUCKET_HEIGHT, BUCKET_DEPTH));
		transform
			.setRotation(new Rotf(new Vec3f(-1, 0, 0), (float) (Vec3f.convertGrad2Radiant(90) - fAngle)));
		transform.setScale(new Vec3f(1, 1, 1));
		stackLevel.getElementByPositionIndex(2).setTransform(transform);

		fGK = BUCKET_WIDTH - fBucketBottomRight;
		fAngle = (float) Math.atan((fGK / fAK));

		// Right plane
		transform = new Transform();
		transform.setTranslation(new Vec3f(BUCKET_WIDTH, 0, BUCKET_DEPTH));
		transform
			.setRotation(new Rotf(new Vec3f(0, -1, 0), (float) (Vec3f.convertGrad2Radiant(270) - fAngle)));
		transform.setScale(new Vec3f(1, 1, 1));
		stackLevel.getElementByPositionIndex(3).setTransform(transform);

		return stackLevel;
	}
	
	public RemoteLevel initFocusLevelWii() {
		fArHeadPosition = GeneralManager.get().getWiiRemote().getCurrentSmoothHeadPosition();
		fArHeadPosition[0] = fArHeadPosition[0] * 4 + 4;
		fArHeadPosition[1] *= 4;

		fBucketBottomLeft = -1 * fArHeadPosition[0] - BUCKET_WIDTH - 1.5f;
		fBucketBottomRight = -1 * fArHeadPosition[0] + BUCKET_WIDTH - 1.5f;
		fBucketBottomTop = fArHeadPosition[1] + BUCKET_HEIGHT;
		fBucketBottomBottom = fArHeadPosition[1] - BUCKET_HEIGHT;

		fHeadDist =
			-1 * GeneralManager.get().getWiiRemote().getCurrentHeadDistance() + 7f
				+ Math.abs(fBucketBottomRight - 2) / 2 + Math.abs(fBucketBottomTop - 2) / 2;

		float fXScaling = (4 * 2 - Math.abs(fBucketBottomLeft - fBucketBottomRight)) / (4 * 2);
		float fYScaling = (4 * 2 - Math.abs(fBucketBottomBottom - fBucketBottomTop)) / (4 * 2);

		// float fXScaling = (4*2 - Math.abs(fBucketBottomLeft) - Math.abs(fBucketBottomRight)) / (4*2);
		// float fYScaling = (4*2 - Math.abs(fBucketBottomBottom) - Math.abs(fBucketBottomTop)) / (4*2);

		Transform transform = new Transform();
		transform.setTranslation(new Vec3f(fBucketBottomLeft, fBucketBottomBottom, fHeadDist));
		transform.setScale(new Vec3f(fXScaling, fYScaling, 1));
		focusLevel.getElementByPositionIndex(0).setTransform(transform);

		return focusLevel;
	}

	public RemoteLevel initStackLevelWii() {
		Transform transform;

		float fAK = BUCKET_DEPTH - 1 * fHeadDist;
		float fGK = BUCKET_HEIGHT - fBucketBottomTop;
		float fAngle = (float) Math.atan((fGK / fAK));

		// Top plane
		transform = new Transform();
		transform.setTranslation(new Vec3f(-BUCKET_WIDTH, BUCKET_HEIGHT, BUCKET_DEPTH));
		transform
			.setRotation(new Rotf(new Vec3f(1, 0, 0), (float) (Vec3f.convertGrad2Radiant(270) - fAngle)));
		transform.setScale(new Vec3f(1, 1, 1));
		stackLevel.getElementByPositionIndex(0).setTransform(transform);

		fGK = BUCKET_WIDTH + fBucketBottomLeft;
		fAngle = (float) Math.atan((fGK / fAK));

		// Left plane
		transform = new Transform();
		transform.setTranslation(new Vec3f(-BUCKET_WIDTH, 0, BUCKET_DEPTH));
		transform.setRotation(new Rotf(new Vec3f(0, 1, 0), (float) (Vec3f.convertGrad2Radiant(90) - fAngle)));
		transform.setScale(new Vec3f(1, 1, 1));
		stackLevel.getElementByPositionIndex(1).setTransform(transform);

		fGK = BUCKET_WIDTH + fBucketBottomBottom;
		fAngle = (float) Math.atan((fGK / fAK));

		// Bottom plane
		transform = new Transform();
		transform.setTranslation(new Vec3f(-BUCKET_WIDTH, -BUCKET_HEIGHT, BUCKET_DEPTH));
		transform
			.setRotation(new Rotf(new Vec3f(-1, 0, 0), (float) (Vec3f.convertGrad2Radiant(90) - fAngle)));
		transform.setScale(new Vec3f(1, 1, 1));
		stackLevel.getElementByPositionIndex(2).setTransform(transform);

		fGK = BUCKET_WIDTH - fBucketBottomRight;
		fAngle = (float) Math.atan((fGK / fAK));

		// Right plane
		transform = new Transform();
		transform.setTranslation(new Vec3f(BUCKET_WIDTH, 0, BUCKET_DEPTH));
		transform
			.setRotation(new Rotf(new Vec3f(0, -1, 0), (float) (Vec3f.convertGrad2Radiant(270) - fAngle)));
		transform.setScale(new Vec3f(1, 1, 1));
		stackLevel.getElementByPositionIndex(3).setTransform(transform);

		return stackLevel;
	}

	public float getBucketBottomLeft() {
		return fBucketBottomLeft;
	}

	public float getBucketBottomRight() {
		return fBucketBottomRight;
	}

	public float getBucketBottomBottom() {
		return fBucketBottomBottom;
	}

	public float getBucketBottomTop() {
		return fBucketBottomTop;
	}

	public float[] getHeadPosition() {
		return fArHeadPosition;
	}

	public float getHeadDistance() {
		return fHeadDist;
	}
	
	public void setZoomedIn(boolean bIsZoomedIn) {
		this.bIsZoomedIn = bIsZoomedIn;
	}
}