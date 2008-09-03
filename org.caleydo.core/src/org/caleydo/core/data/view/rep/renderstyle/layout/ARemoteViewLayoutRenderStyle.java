package org.caleydo.core.data.view.rep.renderstyle.layout;

import org.caleydo.core.data.view.camera.EProjectionMode;
import org.caleydo.core.data.view.camera.IViewFrustum;
import org.caleydo.core.data.view.rep.renderstyle.GeneralRenderStyle;
import org.caleydo.core.view.opengl.util.hierarchy.EHierarchyLevel;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteHierarchyLayer;

/**
 * Abstract render style for remote rendered views as Jukebox and Bucket.
 * 
 * @author Marc Streit
 */
public abstract class ARemoteViewLayoutRenderStyle
	extends GeneralRenderStyle
{

	protected float fAspectRatio = 1.0f;

	protected float fZoomFactor = 0.0f;

	protected float fPoolLayerWidth = 0.8f;

	public enum LayoutMode
	{
		BUCKET,
		JUKEBOX
	}

	protected RemoteHierarchyLayer underInteractionLayer;

	protected RemoteHierarchyLayer stackLayer;

	protected RemoteHierarchyLayer poolLayer;

	protected RemoteHierarchyLayer transitionLayer;

	protected RemoteHierarchyLayer spawnLayer;

	protected RemoteHierarchyLayer memoLayer;

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
		underInteractionLayer = new RemoteHierarchyLayer(EHierarchyLevel.UNDER_INTERACTION);
		stackLayer = new RemoteHierarchyLayer(EHierarchyLevel.STACK);
		poolLayer = new RemoteHierarchyLayer(EHierarchyLevel.POOL);
		transitionLayer = new RemoteHierarchyLayer(EHierarchyLevel.TRANSITION);
		spawnLayer = new RemoteHierarchyLayer(EHierarchyLevel.SPAWN);
		memoLayer = new RemoteHierarchyLayer(EHierarchyLevel.MEMO);

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

	public abstract RemoteHierarchyLayer initUnderInteractionLayer();

	public abstract RemoteHierarchyLayer initStackLayer();

	public abstract RemoteHierarchyLayer initPoolLayer(final int iMouseOverViewID);

	public abstract RemoteHierarchyLayer initMemoLayer();

	public abstract RemoteHierarchyLayer initTransitionLayer();

	public abstract RemoteHierarchyLayer initSpawnLayer();

	public RemoteHierarchyLayer getUnderInteractionLayer()
	{

		return underInteractionLayer;
	}

	public RemoteHierarchyLayer getStackLayer()
	{

		return stackLayer;
	}

	public RemoteHierarchyLayer getPoolLayer()
	{

		return poolLayer;
	}

	public RemoteHierarchyLayer getTransitionLayer()
	{

		return transitionLayer;
	}

	public RemoteHierarchyLayer getSpawnLayer()
	{

		return spawnLayer;
	}

	public RemoteHierarchyLayer getMemoLayer()
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
