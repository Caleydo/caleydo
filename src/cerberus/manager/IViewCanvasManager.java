/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.manager;

import java.util.Iterator;

import javax.swing.JFrame;

import cerberus.manager.type.ManagerObjectType;

import cerberus.data.collection.view.IViewCanvas;
import cerberus.net.dwt.swing.jogl.WorkspaceSwingFrame;
import cerberus.net.dwt.swing.mdi.DInternalFrame;

/**
 * Manges all ISet's.
 * 
 * Note: the ISetManager must register itself to the singelton prometheus.app.SingeltonManager
 * 
 * @author Michael Kalkusch
 *
 */
public interface IViewCanvasManager extends IGeneralManager
{

	public IViewCanvas createCanvas(final ManagerObjectType useViewCanvasType,
			final String sParameter);

	public boolean deleteCanvas(IViewCanvas deleteSet);

	public boolean deleteCanvas(final int iItemId);

	/**
	 * Get IViewCanvas linked to the unique Id iItemId.
	 * Note: Does not return any WorkspaceSwingFrame
	 * 
	 * @param iItemId iItemId unique Id to addesse workspace
	 * 
	 * @return IViewCanvas linke to the unique Id
	 */
	public IViewCanvas getItemCanvas(final int iItemId);

	/**
	 * Get an array containig all IViewCanvas objects, but not the WorkspaceSwingFrame / Frame obejcts.
	 * 
	 * @return array with all IViewCanvas objects, excluding all WorkspaceSwingFrame
	 */
	public IViewCanvas[] getAllCanvasItems();

	/**
	 * Creates a new Internal Frame as child Frame of the Frame addressed via iTargetFrameId.
	 * Note: Same result as createCanvas( ManagerObjectType.VIEW_NEW_IFRAME , * )
	 * 
	 * @param iTargetFrameId unique Id ot address Frame
	 * @param sAditionalParameter additional parameters
	 * 
	 * @see cerberus.manager.canvas.ViewCanvasManager#createCanvas(ManagerObjectType, String)
	 * @see cerberus.manager.IViewCanvasManager#createCanvas(ManagerObjectType, String)
	 * 
	 * @return new DInternalFrame as child of frame addressed via iTargetFrameId
	 */
	public DInternalFrame createNewInternalFrame(final int iTargetFrameId,
			final String sAditionalParameter);

	/**
	 * Creates a new WorkspaceSwingFrame / JFrame 
	 * 
	 * @param useViewCanvasType detailed type of new JFrame
	 * @param sAditionalParameter additional parameters
	 * 
	 * @return new frame
	 */
	public WorkspaceSwingFrame createWorkspace(
			final ManagerObjectType useViewCanvasType,
			final String sAditionalParameter);

	public boolean deleteWorkspace(final int iItemId);

	/**
	 * Get a workspace addressed via its unique Id
	 * 
	 * @param iItemId unique Id to addesse workspace
	 * 
	 * @return frame linked to iItemId
	 */
	public WorkspaceSwingFrame getItemWorkspace(final int iItemId);

	/**
	 * Get iterator for all workspace fames.
	 * 
	 * @return iterator for all workspace fames
	 */
	public Iterator<WorkspaceSwingFrame> getWorkspaceIterator();

	/*
	 *  (non-Javadoc)
	 * @see prometheus.manager.GeneralManager#getManagerType()
	 */
	public ManagerObjectType getManagerType();

	/**
	 * Adds all IViewCanvas components to the parent JFrame.
	 * 
	 * @param refJFrame parent JFrame
	 */
	public void addAllViewCanvas(JFrame refJFrame);

}
