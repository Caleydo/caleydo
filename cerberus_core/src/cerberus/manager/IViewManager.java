package cerberus.manager;


import javax.swing.JFrame;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import cerberus.manager.type.ManagerObjectType;
import cerberus.view.IViewRep;
import cerberus.view.IView;
import cerberus.view.ViewType;
import cerberus.view.opengl.IGLCanvasUser;

/**
 * Manage all canvas, view, ViewRep's nad GLCanvas objects.
 * 
 * @author Michael Kalkusch
 */
public interface IViewManager 
extends IGeneralManager {
	
//	public IView createView(final ManagerObjectType useViewType, 
//			final int iViewId, 
//			final int iParentContainerId,
//			final String sLabel);
	
	public IView createView(final ManagerObjectType useViewType, 
			final int iViewId, 
			final int iParentContainerId,
			final String sLabel,
			final int iGLcanvasId,
			final int iGLforwarderId);
	
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
	 * Get an iterator for all avaliable JFrames (== WorkspaceSwingFrame)
	 * 
	 * @return
	 */
	public Iterator<JFrame> getWorkspaceIterator();
}