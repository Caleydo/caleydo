package cerberus.manager.view;

import java.util.HashMap;

import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;

import cerberus.manager.IGeneralManager;
import cerberus.manager.IViewGLCanvasManager;
import cerberus.manager.IViewManager;
import cerberus.manager.base.AAbstractManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.manager.type.ManagerType;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.view.gui.IView;
import cerberus.view.gui.opengl.IGLCanvasDirector;
import cerberus.view.gui.opengl.IGLCanvasUser;
import cerberus.view.gui.swt.data.explorer.DataExplorerViewRep;
import cerberus.view.gui.swt.data.DataTableViewRep;
import cerberus.view.gui.swt.pathway.jgraph.PathwayViewRep;
import cerberus.view.gui.swt.progressbar.ProgressBarViewRep;
import cerberus.view.gui.swt.gears.jogl.GearsViewRep;
import cerberus.view.gui.swt.heatmap.jogl.SwtJogHistogram2DViewRep;
import cerberus.view.gui.swt.jogl.SwtJoglGLCanvasViewRep;
import cerberus.view.gui.swt.jogl.sample.TestTriangleViewRep;
import cerberus.view.gui.swt.scatterplot.jogl.Scatterplot2DViewRep;
import cerberus.view.gui.swt.slider.SelectionSliderViewRep;
import cerberus.view.gui.swt.slider.StorageSliderViewRep;
import cerberus.view.gui.swt.heatmap.jogl.Heatmap2DViewRep;
import cerberus.view.gui.swt.test.TestTableViewRep;
import cerberus.xml.parser.command.CommandQueueSaxType;

/**
 * 
 * @deprecated use  ViewJoglManager
 * 
 * @author kalkusch
 *
 */
public class ViewManager 
extends AAbstractManager
implements IViewManager, IViewGLCanvasManager {
	
	protected HashMap<Integer, IView> hashViewId2View;
	
	public ViewManager(IGeneralManager setGeneralManager)
	{
		super(setGeneralManager,
				IGeneralManager.iUniqueId_TypeOffset_GUI_AWT );

		assert setGeneralManager != null : "Constructor with null-pointer to singelton";

		hashViewId2View = new HashMap<Integer, IView>();
		
		refGeneralManager.getSingelton().setViewGLCanvasManager(this);
	}

	public boolean hasItem(int iItemId)
	{
		if (hashViewId2View.containsKey(iItemId) == true)
			return true;
		
		return false;
	}

	public Object getItem(int iItemId)
	{
		return hashViewId2View.get(iItemId);
	}

	public int size()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public ManagerObjectType getManagerType()
	{
		assert false:"not done yet";
		return null;
	}

	public boolean registerItem(Object registerItem, int iItemId,
			ManagerObjectType type)
	{
		IView registerView = (IView) registerItem;
		
		hashViewId2View.put(iItemId, registerView);
		
		return true;
	}

	public boolean unregisterItem(int iItemId, ManagerObjectType type)
	{
		assert false : "not done yet";
		return false;
	}

//	/**
//	 * Method creates a new ID and 
//	 * calls createView(ManagerObjectType useViewType, int iUniqueId).
//	 */
//	public IView createView(final ManagerObjectType useViewType)
//	{
//		final int iUniqueId = this.createNewId(useViewType);
//		
//		return createView(useViewType, iUniqueId);
//	}

	/**
	 * Method creates a new view representation according to the 
	 * type parameter.
	 */
	public IView createView(ManagerObjectType useViewType, int iViewId,
			int iParentContainerId, String sLabel)
	{
		if (useViewType.getGroupType() != ManagerType.VIEW)
		{
			throw new CerberusRuntimeException(
					"try to create object with wrong type "
							+ useViewType.name());
		}

		//final int iNewId = this.createNewId(useViewType);

		switch (useViewType)
		{
		case VIEW:

		case VIEW_SWT_PATHWAY:
			return new PathwayViewRep(
					this.refGeneralManager, iViewId, iParentContainerId, sLabel);
		case VIEW_SWT_DATA_EXPLORER:
			return new DataExplorerViewRep(
					this.refGeneralManager, iViewId, iParentContainerId, sLabel);
		case VIEW_SWT_PROGRESS_BAR:
			return new ProgressBarViewRep(
					this.refGeneralManager, iViewId, iParentContainerId, sLabel);
		case VIEW_SWT_TEST_TABLE:
			return new TestTableViewRep(
					this.refGeneralManager, iViewId, iParentContainerId, sLabel);	
		case VIEW_SWT_GEARS:
			return new GearsViewRep(
					this.refGeneralManager, iViewId, iParentContainerId, sLabel);
		case VIEW_SWT_HEATMAP2D:
			return new Heatmap2DViewRep(
					this.refGeneralManager, iViewId, iParentContainerId, sLabel);
		case VIEW_SWT_HISTOGRAM2D:
			return new SwtJogHistogram2DViewRep(
					this.refGeneralManager, iViewId, iParentContainerId, sLabel);
			//return new Heatmap2DViewRep(iNewId, this.refGeneralManager);
		case VIEW_SWT_SCATTERPLOT2D:
			return new Scatterplot2DViewRep(
					this.refGeneralManager, iViewId, iParentContainerId, sLabel);
		case VIEW_SWT_SCATTERPLOT3D:
			return new Scatterplot2DViewRep(
					this.refGeneralManager, iViewId, iParentContainerId, sLabel);
		case VIEW_SWT_SELECTION_SLIDER:
			return new SelectionSliderViewRep(
					this.refGeneralManager, iViewId, iParentContainerId, sLabel);
		case VIEW_SWT_STORAGE_SLIDER:
			return new StorageSliderViewRep(
					this.refGeneralManager, iViewId, iParentContainerId, sLabel);
		case VIEW_SWT_JOGL_TEST_TRIANGLE:
			return new TestTriangleViewRep(
					this.refGeneralManager, iViewId, iParentContainerId, sLabel);
		case VIEW_SWT_JOGL_MULTI_GLCANVAS:
			assert false : "not implemented! No Jogl available!";
			return null;
		

		default:
			throw new CerberusRuntimeException(
					"StorageManagerSimple.createView() failed due to unhandled type ["
							+ useViewType.toString() + "]");
		}
	}

	public GLCanvas getGLCanvas(int iId)
	{
		assert false : "not implemented!";
		return null;
	}

	public boolean registerGLCanvas(GLCanvas canvas, int iCanvasId)
	{
		assert false : "not implemented!";
		return false;
	}

	public boolean unregisterGLCanvas(GLCanvas canvas)
	{
		assert false : "not implemented!";
		return false;
	}

	public GLEventListener getGLEventListener(int iId)
	{
		assert false : "not implemented!";
		return null;
	}

	public boolean registerGLEventListener(GLEventListener canvasListener, int iId)
	{
		assert false : "not implemented!";
		return false;
	}

	public boolean unregisterGLEventListener(GLEventListener canvasListener)
	{
		assert false : "not implemented!";
		return false;
	}

	public boolean addGLEventListener2GLCanvasById(int iCanvasListenerId, int iCanvasId)
	{
		assert false : "not implemented!";
		return false;
	}

	public boolean removeGLEventListener2GLCanvasById(int iCanvasListenerId, int iCanvasId)
	{
		assert false : "not implemented!";
		return false;
	}

	public boolean registerGLCanvasUser(IGLCanvasUser canvas, int iCanvasId)
	{
		assert false : "not implemented!";
		return false;
	}

	public boolean unregisterGLCanvasUser(IGLCanvasUser canvas)
	{
		assert false : "not implemented!";
		return false;
	}

	public IGLCanvasDirector getGLCanvasDirector(int iId)
	{
		assert false : "not implemented!";
		return null;
	}

	public boolean registerGLCanvasDirector(IGLCanvasDirector director)
	{
		assert false : "not implemented!";
		return false;
	}

	public boolean unregisterGLCanvasDirector(IGLCanvasDirector director)
	{
		assert false : "not implemented!";
		return false;
	}

	public boolean registerGLCanvasDirector(IGLCanvasDirector director, int iId)
	{
		assert false : "not implemented!";
		return false;
	}

	public IGLCanvasUser createGLCanvasUser(CommandQueueSaxType useViewType, int iViewId, int iParentContainerId, String sLabel)
	{
		assert false : "not implemented!";
		return null;
	}
}
