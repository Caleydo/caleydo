package org.caleydo.core.view.opengl.renderstyle.layout;

import org.caleydo.core.view.opengl.camera.EProjectionMode;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;
import org.caleydo.core.view.opengl.util.hierarchy.EHierarchyLevel;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteHierarchyLevel;

/**
 * Abstract render style for remote rendered views as Jukebox and Bucket.
 * 
 * @author Marc Streit
 */
public abstract class ARemoteViewLayoutRenderStyle
	extends GeneralRenderStyle
{
	public final static float NAVIGATION_OVERLAY_TRANSPARENCY = 1f;
	
	protected float fAspectRatio = 1.0f;
	protected float fZoomFactor = 0.0f;
	protected float fPoolLayerWidth = 0.8f;

	public enum LayoutMode
	{
		BUCKET,
		JUKEBOX
	}

	protected RemoteHierarchyLevel underInteractionLayer;
	protected RemoteHierarchyLevel stackLayer;
	protected RemoteHierarchyLevel poolLayer;
	protected RemoteHierarchyLevel transitionLayer;
	protected RemoteHierarchyLevel spawnLayer;
	protected RemoteHierarchyLevel memoLayer;

	protected EProjectionMode eProjectionMode;

	protected float fScalingFactorUnderInteractionLayer;
	protected float fScalingFactorStackLayer;
	protected float fScalingFactorPoolLayer;
	protected float fScalingFactorMemoLayer;
	protected float fScalingFactorTransitionLayer;
	protected float fScalingFactorSpawnLayer;

	protected float fTrashCanXPos;
	protected float fTrashCanYPos;
	protected float fTrashCanWidth;
	protected float fTrashCanHeight;

	protected float fColorBarXPos;
	protected float fColorBarYPos;
	protected float fColorBarWidth;
	protected float fColorBarHeight;

	/**
	 * Constructor.
	 * 
	 * @param generalManager
	 */
	public ARemoteViewLayoutRenderStyle(IViewFrustum viewFrustum)
	{
		super(viewFrustum);
		underInteractionLayer = new RemoteHierarchyLevel(EHierarchyLevel.UNDER_INTERACTION);
		stackLayer = new RemoteHierarchyLevel(EHierarchyLevel.STACK);
		poolLayer = new RemoteHierarchyLevel(EHierarchyLevel.POOL);
		transitionLayer = new RemoteHierarchyLevel(EHierarchyLevel.TRANSITION);
		spawnLayer = new RemoteHierarchyLevel(EHierarchyLevel.SPAWN);
		memoLayer = new RemoteHierarchyLevel(EHierarchyLevel.MEMO);

		underInteractionLayer.setParentLayer(stackLayer);
		stackLayer.setChildLayer(underInteractionLayer);
		stackLayer.setParentLayer(poolLayer);
		poolLayer.setChildLayer(stackLayer);
	}

	/**
	 * Constructor. Copies layers from previous layer. Used for toggle between
	 * layouts.
	 */
	public ARemoteViewLayoutRenderStyle(IViewFrustum viewFrustum, final ARemoteViewLayoutRenderStyle previousLayoutStyle)
	{
		super(viewFrustum);
		underInteractionLayer = previousLayoutStyle.getUnderInteractionLayer();
		stackLayer = previousLayoutStyle.getStackLayer();
		poolLayer = previousLayoutStyle.getPoolLayer();
		memoLayer = previousLayoutStyle.getMemoLayer();
		transitionLayer = previousLayoutStyle.getTransitionLayer();
		spawnLayer = previousLayoutStyle.getSpawnLayer();
	}

	public abstract RemoteHierarchyLevel initUnderInteractionLayer();
	public abstract RemoteHierarchyLevel initStackLayer(boolean bIsZoomedIn);
	public abstract RemoteHierarchyLevel initPoolLayer(final int iMouseOverViewID);
	public abstract RemoteHierarchyLevel initMemoLayer();
	public abstract RemoteHierarchyLevel initTransitionLayer();
	public abstract RemoteHierarchyLevel initSpawnLayer();

	public RemoteHierarchyLevel getUnderInteractionLayer()
	{

		return underInteractionLayer;
	}

	public RemoteHierarchyLevel getStackLayer()
	{

		return stackLayer;
	}

	public RemoteHierarchyLevel getPoolLayer()
	{

		return poolLayer;
	}

	public RemoteHierarchyLevel getTransitionLayer()
	{

		return transitionLayer;
	}

	public RemoteHierarchyLevel getSpawnLayer()
	{

		return spawnLayer;
	}

	public RemoteHierarchyLevel getMemoLayer()
	{

		return memoLayer;
	}

	public EProjectionMode getProjectionMode()
	{

		return eProjectionMode;
	}

	public float getTrashCanXPos()
	{

		return fTrashCanXPos;
	}

	public float getTrashCanYPos()
	{

		return fTrashCanYPos;
	}

	public float getTrashCanWidth()
	{

		return fTrashCanWidth;
	}

	public float getTrashCanHeight()
	{

		return fTrashCanHeight;
	}

	public float getColorBarXPos()
	{

		return fColorBarXPos;
	}

	public float getColorBarYPos()
	{

		return fColorBarYPos;
	}

	public float getColorBarWidth()
	{

		return fColorBarWidth;
	}

	public float getColorBarHeight()
	{

		return fColorBarHeight;
	}

	public void setAspectRatio(final float fAspectRatio)
	{

		this.fAspectRatio = fAspectRatio;
	}

	public void setZoomFactor(final float fZoomFactor)
	{

		this.fZoomFactor = fZoomFactor;
	}

	public float getZoomFactor()
	{

		return fZoomFactor;
	}
}
