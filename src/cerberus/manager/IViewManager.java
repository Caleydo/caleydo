package cerberus.manager;


import java.util.Collection;

import cerberus.manager.type.ManagerObjectType;
import cerberus.view.gui.IView;
import cerberus.view.gui.opengl.IGLCanvasUser;

/**
 * Manage all canvas, view, ViewRep's nad GLCanvas objects.
 * 
 * @author Michael Kalkusch
 */
public interface IViewManager 
extends IGeneralManager {
	
	public IView createView(final ManagerObjectType useViewType, 
			int iViewId, 
			int iParentContainerId, 
			String sLabel);
	
	public void destroyOnExit();

	public void addViewRep(IView refView);
	
	public void removeViewRep(IView refView) ;
	
	public Collection<IView> getAllViews();
	
	public Collection<IGLCanvasUser> getAllGLCanvasUsers();
}