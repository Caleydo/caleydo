package org.geneview.core.manager;


import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JFrame;

import org.geneview.core.manager.type.ManagerObjectType;
import org.geneview.core.manager.view.PickingManager;
import org.geneview.core.view.IView;
import org.geneview.core.view.IViewRep;
import org.geneview.core.view.ViewType;
import org.geneview.core.view.opengl.IGLCanvasUser;

/**
 * Manage all canvas, view, ViewRep's and GLCanvas objects.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public interface IViewManager 
extends IGeneralManager {
	
	public IView createView(final ManagerObjectType useViewType, 
			final int iUniqueId,
			final int iParentContainerId,
			final int iGlForwarderId, 
			final String sLabel );
	
	public void destroyOnExit();

	public void addViewRep(IView refView);
	
	public void removeViewRep(IView refView) ;
	
	public Collection<IView> getAllViews();
	
	public Collection<IGLCanvasUser> getAllGLCanvasUsers();
	
	public ArrayList<IViewRep> getViewRepByType(ViewType viewType);
	
	/**
	 * Create a new JFrame.
	 * 
	 * @param useViewCanvasType
	 * @param sAditionalParameter
	 * @return
	 */
	public JFrame createWorkspace( 
			final ManagerObjectType useViewCanvasType,
			final String sAditionalParameter );
	
	/**
	 * Get the PickingManager which is responsible for system wide picking
	 * @return the PickingManager
	 */
	public PickingManager getPickingManager();
}