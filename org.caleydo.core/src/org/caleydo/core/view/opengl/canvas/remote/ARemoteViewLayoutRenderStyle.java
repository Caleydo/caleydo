package org.caleydo.core.view.opengl.canvas.remote;

import org.caleydo.core.view.opengl.camera.EProjectionMode;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteElementManager;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevel;

/**
 * Abstract render style for remote rendered views as Jukebox and Bucket.
 * 
 * @author Marc Streit
 */
public abstract class ARemoteViewLayoutRenderStyle
	extends GeneralRenderStyle {
	public final static float NAVIGATION_OVERLAY_TRANSPARENCY = 1f;

	protected RemoteElementManager remoteElementManager;

	protected float fAspectRatio = 1.0f;
	protected float fZoomFactor = 0.0f;
	protected float fPoolLayerWidth = 0.8f;

	public enum LayoutMode {
		BUCKET,
		JUKEBOX,
		LIST
	}

	protected RemoteLevel focusLevel;
	protected RemoteLevel stackLevel;
	protected RemoteLevel poolLevel;
	protected RemoteLevel transitionLevel;
	protected RemoteLevel spawnLevel;
	protected RemoteLevel selectionLevel;

	protected EProjectionMode eProjectionMode;

	protected float fScalingFactorFocusLevel;
	protected float fScalingFactorStackLevel;
	protected float fScalingFactorPoolLevel;
	protected float fScalingFactorSelectionLevel;
	protected float fScalingFactorTransitionLevel;
	protected float fScalingFactorSpawnLevel;

	protected float fTrashCanXPos;
	protected float fTrashCanYPos;
	protected float fTrashCanWidth;
	protected float fTrashCanHeight;

	// protected float fColorBarXPos;
	// protected float fColorBarYPos;
	// protected float fColorBarWidth;
	// protected float fColorBarHeight;

	/**
	 * Constructor.
	 * 
	 * @param generalManager
	 */
	public ARemoteViewLayoutRenderStyle(IViewFrustum viewFrustum) {
		super(viewFrustum);

		focusLevel = new RemoteLevel(1, "Focus Level", null, stackLevel);
		stackLevel = new RemoteLevel(4, "Stack Level", focusLevel, poolLevel);
		poolLevel = new RemoteLevel(14, "Pool Level", stackLevel, null);
		transitionLevel = new RemoteLevel(1, "Transition Level", null, null);
		spawnLevel = new RemoteLevel(1, "Spawn Level", null, stackLevel);
		selectionLevel = new RemoteLevel(1, "Selection Level", null, stackLevel);

		remoteElementManager = RemoteElementManager.get();
	}

	/**
	 * Constructor. Copies layers from previous layer. Used for toggle between layouts.
	 */
	public ARemoteViewLayoutRenderStyle(IViewFrustum viewFrustum,
		final ARemoteViewLayoutRenderStyle previousLayoutStyle) {
		super(viewFrustum);
		focusLevel = previousLayoutStyle.getUnderInteractionLayer();
		stackLevel = previousLayoutStyle.getStackLayer();
		poolLevel = previousLayoutStyle.getPoolLayer();
		selectionLevel = previousLayoutStyle.getMemoLayer();
		transitionLevel = previousLayoutStyle.getTransitionLayer();
		spawnLevel = previousLayoutStyle.getSpawnLayer();
	}

	public abstract RemoteLevel initFocusLevel();

	public abstract RemoteLevel initStackLevel(boolean bIsZoomedIn);

	public abstract RemoteLevel initPoolLevel(boolean bIsZoomedIn, int iMouseOverViewID);

	public abstract RemoteLevel initMemoLevel();

	public abstract RemoteLevel initTransitionLevel();

	public abstract RemoteLevel initSpawnLevel();

	public RemoteLevel getUnderInteractionLayer() {
		return focusLevel;
	}

	public RemoteLevel getStackLayer() {
		return stackLevel;
	}

	public RemoteLevel getPoolLayer() {
		return poolLevel;
	}

	public RemoteLevel getTransitionLayer() {
		return transitionLevel;
	}

	public RemoteLevel getSpawnLayer() {
		return spawnLevel;
	}

	public RemoteLevel getMemoLayer() {
		return selectionLevel;
	}

	public EProjectionMode getProjectionMode() {
		return eProjectionMode;
	}

	public float getTrashCanXPos() {
		return fTrashCanXPos;
	}

	public float getTrashCanYPos() {
		return fTrashCanYPos;
	}

	public float getTrashCanWidth() {
		return fTrashCanWidth;
	}

	public float getTrashCanHeight() {
		return fTrashCanHeight;
	}

	// public float getColorBarXPos()
	// {
	// return fColorBarXPos;
	// }
	//
	// public float getColorBarYPos()
	// {
	// return fColorBarYPos;
	// }
	//
	// public float getColorBarWidth()
	// {
	// return fColorBarWidth;
	// }
	//
	// public float getColorBarHeight()
	// {
	// return fColorBarHeight;
	// }

	public void setAspectRatio(final float fAspectRatio) {
		this.fAspectRatio = fAspectRatio;
	}

	public float getAspectRatio() {
		return fAspectRatio;
	}

	public void setZoomFactor(final float fZoomFactor) {
		this.fZoomFactor = fZoomFactor;
	}

	public float getZoomFactor() {
		return fZoomFactor;
	}
}
