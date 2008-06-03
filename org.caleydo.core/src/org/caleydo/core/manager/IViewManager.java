package org.caleydo.core.manager;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JFrame;

import org.caleydo.core.manager.type.ManagerObjectType;
import org.caleydo.core.view.IView;
import org.caleydo.core.view.IView;
import org.caleydo.core.view.ViewType;

/**
 * Manage all canvas, view, ViewRep's and GLCanvas objects.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public interface IViewManager 
extends IManager
{		
	public IView createView(final ManagerObjectType useViewType, 
			final int iUniqueId,
			final int iParentContainerId,
			final String sLabel);
	
	public IView createGLView(final ManagerObjectType useViewType, 
			final int iViewID,
			final int iParentContainerID,
			final int iCanvasID,
			final String sLabel);
	
	public void destroyOnExit();

	public void addViewRep(IView view);
	
	public void removeViewRep(IView view) ;
	
	public Collection<IView> getAllViews();
	
	public ArrayList<IView> getViewRepByType(ViewType viewType);
	
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
}