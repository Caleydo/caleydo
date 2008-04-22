package org.caleydo.core.data.view.rep.renderstyle.layout;

import org.caleydo.core.data.GeneralRenderStyle;
import org.caleydo.core.data.view.camera.ViewFrustumBase.ProjectionMode;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.view.opengl.util.JukeboxHierarchyLayer;

/**
 * 
 * Abstract render style for remote rendered views as Jukebox and Bucket.
 * 
 * @author Marc Streit
 *
 */
public abstract class ARemoteViewLayoutRenderStyle
extends GeneralRenderStyle {
	
	public enum LayoutMode
	{
		BUCKET,
		JUKEBOX
	}
	
	private static final int MAX_LOADED_VIEWS = 30;
	
	protected JukeboxHierarchyLayer underInteractionLayer;
	protected JukeboxHierarchyLayer stackLayer;
	protected JukeboxHierarchyLayer poolLayer;
	protected JukeboxHierarchyLayer transitionLayer;
	protected JukeboxHierarchyLayer spawnLayer;
	protected JukeboxHierarchyLayer memoLayer;
	
	protected ProjectionMode projectionMode;
	
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
	
	/**
	 * Constructor.
	 * 
	 * @param generalManager
	 */
	public ARemoteViewLayoutRenderStyle(final IGeneralManager generalManager) 
	{
		underInteractionLayer = new JukeboxHierarchyLayer(generalManager, 1);
		stackLayer = new JukeboxHierarchyLayer(generalManager, 4);
		poolLayer = new JukeboxHierarchyLayer(generalManager, MAX_LOADED_VIEWS);
		transitionLayer = new JukeboxHierarchyLayer(generalManager, 1);
		spawnLayer = new JukeboxHierarchyLayer(generalManager, 1);
		memoLayer = new JukeboxHierarchyLayer(generalManager, 5);
		
		underInteractionLayer.setParentLayer(stackLayer);
		stackLayer.setChildLayer(underInteractionLayer);
		stackLayer.setParentLayer(poolLayer);
		poolLayer.setChildLayer(stackLayer);
	}
	
	/**
	 * Constructor.
	 * 
	 * Copies layers from previous layer. Used for toggle between layouts.
	 */
	public ARemoteViewLayoutRenderStyle(final IGeneralManager generalManager,
			final ARemoteViewLayoutRenderStyle previousLayoutStyle)
	{
		underInteractionLayer = previousLayoutStyle.getUnderInteractionLayer();
		stackLayer = previousLayoutStyle.getStackLayer();
		poolLayer = previousLayoutStyle.getPoolLayer();
		memoLayer = previousLayoutStyle.getMemoLayer();
		transitionLayer = previousLayoutStyle.getTransitionLayer();
		spawnLayer = previousLayoutStyle.getSpawnLayer();
	}
	
	public abstract JukeboxHierarchyLayer initUnderInteractionLayer();
	public abstract JukeboxHierarchyLayer initStackLayer();
	public abstract JukeboxHierarchyLayer initPoolLayer(final int iMouseOverViewID);
	public abstract JukeboxHierarchyLayer initMemoLayer();
	public abstract JukeboxHierarchyLayer initTransitionLayer();
	public abstract JukeboxHierarchyLayer initSpawnLayer();

	
	public JukeboxHierarchyLayer getUnderInteractionLayer() {
	
		return underInteractionLayer;
	}

	public JukeboxHierarchyLayer getStackLayer() {
	
		return stackLayer;
	}
	
	public JukeboxHierarchyLayer getPoolLayer() {
	
		return poolLayer;
	}

	public JukeboxHierarchyLayer getTransitionLayer() {
	
		return transitionLayer;
	}
	
	public JukeboxHierarchyLayer getSpawnLayer() {
	
		return spawnLayer;
	}

	public JukeboxHierarchyLayer getMemoLayer() {
	
		return memoLayer;
	}
	
	public ProjectionMode getProjectionMode() {
		
		return projectionMode;
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
	
	public abstract void initLayout();
}
