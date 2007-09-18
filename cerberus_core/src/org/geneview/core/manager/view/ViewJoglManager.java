package org.geneview.core.manager.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Enumeration;

import javax.swing.JFrame;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;

import org.geneview.core.command.CommandQueueSaxType;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.ILoggerManager.LoggerType;
import org.geneview.core.manager.IViewGLCanvasManager;
import org.geneview.core.manager.base.AAbstractManager;
import org.geneview.core.manager.type.ManagerObjectType;
import org.geneview.core.manager.type.ManagerType;
import org.geneview.core.util.exception.GeneViewRuntimeException;
import org.geneview.core.view.IViewRep;
import org.geneview.core.view.IView;
import org.geneview.core.view.ViewType;
import org.geneview.core.view.jogl.JoglCanvasForwarderType;
import org.geneview.core.view.opengl.IGLCanvasDirector;
import org.geneview.core.view.opengl.IGLCanvasUser;
import org.geneview.core.view.opengl.canvas.GLCanvasTestTriangle;
import org.geneview.core.view.opengl.canvas.heatmap.GLCanvasHeatmap;
//import org.geneview.core.view.opengl.canvas.heatmap.GLCanvasHeatmap2D;
import org.geneview.core.view.opengl.canvas.heatmap.GLCanvasHeatmap2DColumn;
import org.geneview.core.view.opengl.canvas.histogram.GLCanvasHistogram2D;
import org.geneview.core.view.opengl.canvas.isosurface.GLCanvasIsoSurface3D;
import org.geneview.core.view.opengl.canvas.parcoords.GLCanvasParCoords3D;
import org.geneview.core.view.opengl.canvas.pathway.GLCanvasJukeboxPathway3D;
import org.geneview.core.view.opengl.canvas.pathway.GLCanvasLayeredPathway3D;
import org.geneview.core.view.opengl.canvas.pathway.GLCanvasPanelPathway3D;
//import org.geneview.core.view.opengl.canvas.scatterplot.GLCanvasMinMaxScatterPlot2D;
import org.geneview.core.view.opengl.canvas.scatterplot.GLCanvasMinMaxScatterPlot3D;
import org.geneview.core.view.opengl.canvas.scatterplot.GLMinMaxScatterplot2Dinteractive;
import org.geneview.core.view.opengl.canvas.scatterplot.GLCanvasScatterPlot2D;
import org.geneview.core.view.opengl.canvas.texture.GLCanvasTexture2D;
import org.geneview.core.view.opengl.canvas.widgets.GLCanvasWidget;
import org.geneview.core.view.swt.browser.HTMLBrowserViewRep;
import org.geneview.core.view.swt.data.exchanger.DataExchangerViewRep;
import org.geneview.core.view.swt.data.exchanger.NewSetEditorViewRep;
//import org.geneview.core.view.swt.data.exchanger.SetEditorViewRep;
import org.geneview.core.view.swt.data.explorer.DataExplorerViewRep;
//import org.geneview.core.view.swt.data.DataTableViewRep;
import org.geneview.core.view.swt.mixer.MixerViewRep;
import org.geneview.core.view.swt.pathway.Pathway2DViewRep;
import org.geneview.core.view.swt.progressbar.ProgressBarViewRep;
import org.geneview.core.view.swt.jogl.gears.GearsViewRep;
import org.geneview.core.view.swt.jogl.SwtJoglGLCanvasViewRep;
import org.geneview.core.view.swt.slider.SelectionSliderViewRep;
import org.geneview.core.view.swt.slider.StorageSliderViewRep;
import org.geneview.core.view.swt.image.ImageViewRep;
import org.geneview.core.view.swt.test.TestTableViewRep;
import org.geneview.core.view.swt.undoredo.UndoRedoViewRep;

/**
 * Manage all canvas, view, ViewRep's and GLCanvas objects.
 * 
 * @author Michael Kalkusch
 */
public class ViewJoglManager 
extends AAbstractManager
implements IViewGLCanvasManager {

	protected HashMap<Integer, IView> hashViewId2View;

	protected HashMap<Integer, GLCanvas> hashGLCanvas;

	protected HashMap<Integer, IGLCanvasUser> hashGLCanvasUser;

	protected HashMap<IGLCanvasUser, Integer> hashGLCanvasUser_revert;

	protected HashMap<Integer, IGLCanvasDirector> hashGLCanvasDirector;

	protected Hashtable<IGLCanvasDirector, Integer> hashGLCanvasDirector_revert;

	/** speed up removal of GLCanvastbon2!WS objects. */
	protected HashMap<GLCanvas, Integer> hashGLCanvas_revert;

	protected HashMap<Integer, GLEventListener> hashGLEventListener;
	
	/** speed up removal of GLEventListener objects. */
	protected HashMap<GLEventListener, Integer> hashGLEventListener_revert;

	/**
	 * List of data explorer view reps (needed for mediator registration 
	 * when data is created).
	 */
	protected ArrayList<IViewRep> arDataExplorerViewRep;
	
	/**
	 * List of HTML browser view reps
	 */
	protected ArrayList<IViewRep> arHTMLBrowserViewRep;
		
	//protected ArrayList<WorkspaceSwingFrame> arWorkspaceSwingFrame;
	protected ArrayList<JFrame> arWorkspaceJFrame;
	
	private JoglCanvasForwarderType joglCanvasForwarderType;

	public ViewJoglManager(IGeneralManager setGeneralManager) {

		super(setGeneralManager, 
				IGeneralManager.iUniqueId_TypeOffset_GUI_AWT,
				ManagerType.VIEW_GUI_SWT );

		assert setGeneralManager != null : "Constructor with null-pointer to singelton";

		hashViewId2View = new HashMap<Integer, IView>();

		hashGLCanvas = new HashMap<Integer, GLCanvas>();
		hashGLCanvas_revert = new HashMap<GLCanvas, Integer>();

		hashGLEventListener = new HashMap<Integer, GLEventListener>();
		hashGLEventListener_revert = new HashMap<GLEventListener, Integer>();

		hashGLCanvasUser = new HashMap<Integer, IGLCanvasUser>();
		hashGLCanvasUser_revert = new HashMap<IGLCanvasUser, Integer>();

		hashGLCanvasDirector = new HashMap<Integer, IGLCanvasDirector>();
		hashGLCanvasDirector_revert = new Hashtable<IGLCanvasDirector, Integer>();

		arDataExplorerViewRep = new ArrayList<IViewRep>();
		arHTMLBrowserViewRep = new ArrayList<IViewRep>();
		arWorkspaceJFrame = new ArrayList<JFrame>();

		refGeneralManager.getSingelton().setViewGLCanvasManager(this);
	}


	public boolean hasItem(int iItemId) {

		if (hashViewId2View.containsKey(iItemId))
			return true;

		if (hashGLCanvas.containsKey(iItemId))
			return true;
		
		if (hashGLCanvasUser.containsKey(iItemId))
			return true;
		
		if (hashGLEventListener.containsKey(iItemId))
			return true;

		return false;
	}

	public Object getItem(int iItemId) {

		IView bufferIView = hashViewId2View.get(iItemId);

		if (bufferIView != null)
			return bufferIView;

		GLCanvas bufferCanvas = hashGLCanvas.get(iItemId);

		if (bufferCanvas != null)
			return bufferCanvas;
		
		IGLCanvasUser bufferCanvasUser = hashGLCanvasUser.get(iItemId);

		if (bufferCanvasUser != null)
			return bufferCanvasUser;

		return hashGLEventListener.get(iItemId);
	}

	public int size() {

		// TODO Auto-generated method stub
		return 0;
	}

	public synchronized boolean registerItem(Object registerItem, int iItemId,
			ManagerObjectType type) {

		assert iItemId != 0 : "registerItem(Object,int) must not use iItemId==0";
		
		IView registerView = (IView) registerItem;

		hashViewId2View.put(iItemId, registerView);

		switch ( registerView.getViewType() ) {
			case SWT_DATA_EXPLORER:
			case SWT_HTML_BROWSER:
				this.addViewRep(registerView);
				break;
				
			//default: // do nothing			
		} // switch ( registerView.getViewType() ) {
		
		refGeneralManager.getSingelton().logMsg(
				"registerItem( " + iItemId + " ) as View", 
				LoggerType.VERBOSE);

		return true;
	}

	public boolean unregisterItem(int iItemId, ManagerObjectType type) {

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
	public IView createView(final ManagerObjectType useViewType, 
			final int iUniqueId,
			final int iParentContainerId,
			final int iGlForwarderId, 
			final String sLabel ) {

		if (useViewType.getGroupType() != ManagerType.VIEW)
		{
			throw new GeneViewRuntimeException(
					"try to create object with wrong type "
							+ useViewType.name());
		}

		//final int iNewId = this.createNewId(useViewType);

		switch (useViewType)
		{
		case VIEW:

		case VIEW_SWT_PATHWAY:
			return new Pathway2DViewRep(this.refGeneralManager, iUniqueId,
					iParentContainerId, sLabel);
		case VIEW_SWT_DATA_EXPLORER:
			return new DataExplorerViewRep(this.refGeneralManager, iUniqueId,
					iParentContainerId, sLabel);
		case VIEW_SWT_DATA_EXCHANGER:
			return new DataExchangerViewRep(this.refGeneralManager, iUniqueId,
					iParentContainerId, sLabel);	
		case VIEW_SWT_DATA_SET_EDITOR:
			return new NewSetEditorViewRep(this.refGeneralManager, iUniqueId,
					iParentContainerId, sLabel);
		case VIEW_SWT_PROGRESS_BAR:
			return new ProgressBarViewRep(this.refGeneralManager, iUniqueId,
					iParentContainerId, sLabel);
		case VIEW_SWT_TEST_TABLE:
			return new TestTableViewRep(this.refGeneralManager, iUniqueId,
					iParentContainerId, sLabel);
		case VIEW_SWT_GEARS:
			return new GearsViewRep(this.refGeneralManager, 
					iUniqueId,
					iParentContainerId, 
					sLabel,
					iGlForwarderId);

		//return new Heatmap2DViewRep(iNewId, this.refGeneralManager);
			
		case VIEW_SWT_SELECTION_SLIDER:
			return new SelectionSliderViewRep(this.refGeneralManager, iUniqueId,
					iParentContainerId, sLabel);
		case VIEW_SWT_STORAGE_SLIDER:
			return new StorageSliderViewRep(this.refGeneralManager, iUniqueId,
					iParentContainerId, sLabel);
		case VIEW_SWT_MIXER:
			return new MixerViewRep(this.refGeneralManager, iUniqueId,
					iParentContainerId, sLabel);			
		case VIEW_SWT_BROWSER:
			return new HTMLBrowserViewRep(this.refGeneralManager, iUniqueId,
					iParentContainerId, sLabel);
		case VIEW_SWT_IMAGE:
			return new ImageViewRep(this.refGeneralManager, iUniqueId,
					iParentContainerId, sLabel);
		case VIEW_SWT_UNDO_REDO:
			return new UndoRedoViewRep(this.refGeneralManager, iUniqueId,
					iParentContainerId, sLabel);	
		case VIEW_SWT_JOGL_MULTI_GLCANVAS:
			return new SwtJoglGLCanvasViewRep(this.refGeneralManager, iUniqueId,
					iParentContainerId,					
					iGlForwarderId,
					sLabel,
					JoglCanvasForwarderType.DEFAULT_FORWARDER);

		default:
			throw new GeneViewRuntimeException(
					"StorageManagerSimple.createView() failed due to unhandled type ["
							+ useViewType.toString() + "]");
		}
	}

	public ArrayList<IViewRep> getViewRepByType(ViewType viewType) {

		switch (viewType)
		{
		case SWT_DATA_EXPLORER:
			return arDataExplorerViewRep;
			
		case SWT_HTML_BROWSER:
			return arHTMLBrowserViewRep;
			
		default:
			assert false : "unsupportet view Type";
			return null;
		} //switch (viewType)
		
	}

	public IGLCanvasUser createGLCanvasUser(CommandQueueSaxType useViewType,
			final int iUniqueId, 
			final int iGlForwarderId,
			String sLabel ) {

		switch (useViewType)
		{
		case CREATE_GL_TRIANGLE_TEST:
			return new GLCanvasTestTriangle(refGeneralManager, iUniqueId,
					iGlForwarderId, sLabel);

		case CREATE_GL_HEATMAP:
			return new GLCanvasHeatmap(refGeneralManager, iUniqueId,
					iGlForwarderId, sLabel);

		case CREATE_GL_HEATMAP2D:
			System.err.println("  overwrite: create CREATE_GL_HEATMAP2DCOLUMN instead of requested CREATE_GL_HEATMAP2D");
			return new GLCanvasHeatmap2DColumn(refGeneralManager, iUniqueId,
					iGlForwarderId, sLabel);
			/*
			return new GLCanvasHeatmap2D(refGeneralManager, iUniqueId,
					iGlForwarderId, sLabel);
			*/
			
		case CREATE_GL_HEATMAP2DCOLUMN:
			return new GLCanvasHeatmap2DColumn(refGeneralManager, iUniqueId,
					iGlForwarderId, sLabel);
			
		case CREATE_GL_SCATTERPLOT2D:
			return new GLCanvasScatterPlot2D(refGeneralManager, iUniqueId,
					iGlForwarderId, sLabel);

		case CREATE_GL_MINMAX_SCATTERPLOT2D:
			refSingelton.logMsg("CREATE_GL_MINMAX_SCATTERPLOT2D ==> create GLMinMaxScatterplot2Dinteractive instead",LoggerType.MINOR_ERROR_XML);
			
			return new GLMinMaxScatterplot2Dinteractive(refGeneralManager, iUniqueId,
					iGlForwarderId, sLabel);
//			return new GLCanvasMinMaxScatterPlot2D(refGeneralManager, iUniqueId,
//					iGlForwarderId, sLabel);

		case CREATE_GL_MINMAX_SCATTERPLOT3D:
			return new GLCanvasMinMaxScatterPlot3D(refGeneralManager, iUniqueId,
					iGlForwarderId, sLabel);
			
		case CREATE_GL_WIDGET:
			return new GLCanvasWidget(refGeneralManager, iUniqueId,
					iGlForwarderId, sLabel);

		case CREATE_GL_HISTOGRAM2D:
			return new GLCanvasHistogram2D(refGeneralManager, iUniqueId,
					iGlForwarderId, sLabel);

		case CREATE_GL_ISOSURFACE3D:
			return new GLCanvasIsoSurface3D(
					refGeneralManager, 
					iUniqueId, 
					iGlForwarderId, 
					sLabel);
			
		case CREATE_GL_TEXTURE2D:
			return new GLCanvasTexture2D(
					refGeneralManager, 
					iUniqueId, 
					iGlForwarderId, 
					sLabel);
			
		case CREATE_GL_LAYERED_PATHWAY_3D:
			return new GLCanvasLayeredPathway3D(
					refGeneralManager, 
					iUniqueId,
					iGlForwarderId, 
					sLabel);
			
		case CREATE_GL_PANEL_PATHWAY_3D:
			return new GLCanvasPanelPathway3D(
					refGeneralManager, 
					iUniqueId,
					iGlForwarderId, 
					sLabel);	

		case CREATE_GL_JUKEBOX_PATHWAY_3D:
			return new GLCanvasJukeboxPathway3D(
					refGeneralManager, 
					iUniqueId,
					iGlForwarderId, 
					sLabel);
			
		case CREATE_GL_PARALLEL_COORDINATES_3D:
			return new GLCanvasParCoords3D(
					refGeneralManager, 
					iUniqueId,
					iGlForwarderId, 
					sLabel);
			
		default:
			throw new GeneViewRuntimeException(
					"ViewJoglManager.createGLCanvasUser() failed due to unhandled type ["
							+ useViewType.toString() + "]");
		}
	}

//	public GLCanvas getGLCanvas(final int iId) {
//
//		return hashGLCanvas.get(iId);
//	}

	public boolean registerGLCanvas(final GLCanvas canvas, final int iCanvasId) {

		assert iCanvasId != 0 : "registerItem(Object,int) must not use iItemId==0";
		
		
		if (hashGLCanvas.containsKey(iCanvasId))
		{
			refGeneralManager.getSingelton().logMsg(
					"registerGLCanvas() id " + iCanvasId
					+ " is already registered!",
					LoggerType.FULL );
			return false;
		}
		if (hashGLCanvas.containsValue(canvas))
		{
			refGeneralManager.getSingelton().logMsg(
					"registerGLCanvas() canvas bound to id " + iCanvasId
					+ " is already registered!",
					LoggerType.FULL );
			return false;
		}

		hashGLCanvas.put(iCanvasId, canvas);
		hashGLCanvas_revert.put(canvas, iCanvasId);

		return true;
	}

	public boolean unregisterGLCanvas(final GLCanvas canvas) {

		if (hashGLCanvas_revert.containsKey(canvas))
		{

			int iCanvasId = hashGLCanvas_revert.get(canvas);

			hashGLCanvas.remove(iCanvasId);
			hashGLCanvas_revert.remove(canvas);

			return true;
		}

		refGeneralManager
				.getSingelton().logMsg(
						"unregisterGLCanvas() canvas object was not found inside ViewJogleManager!",
						LoggerType.FULL );

		return false;
	}

	public boolean registerGLCanvasUser(final IGLCanvasUser canvas,
			final int iCanvasId) {

		assert iCanvasId != 0 : "registerItem(Object,int) must not use iItemId==0";
				
		if (hashGLCanvasUser.containsKey(iCanvasId))
		{
			refGeneralManager.getSingelton().logMsg(
					"registerGLCanvasUser() id " + iCanvasId
							+ " is already registered!",
							LoggerType.MINOR_ERROR );
			return false;
		}
		if (hashGLCanvasUser.containsValue(canvas))
		{
			refGeneralManager.getSingelton().logMsg(
					"registerGLCanvasUser() canvas bound to id " + iCanvasId
							+ " is already registered!",
							LoggerType.MINOR_ERROR );
			return false;
		}

		hashGLCanvasUser.put(iCanvasId, canvas);
		hashGLCanvasUser_revert.put(canvas, iCanvasId);
		
		refGeneralManager.getSingelton().logMsg(
				"registerGLCanvasUser() id " + iCanvasId
						+ " registered successfully to " + this.iUniqueId_current + "  " + this.getClass().toString(),
						LoggerType.FULL );

		return true;
	}

	public boolean unregisterGLCanvasUser(final IGLCanvasUser canvas) {

		if (hashGLCanvas_revert.containsKey(canvas))
		{

			int iCanvasId = hashGLCanvas_revert.get(canvas);

			hashGLCanvas.remove(iCanvasId);
			hashGLCanvas_revert.remove(canvas);

			return true;
		}

		refGeneralManager
				.getSingelton().logMsg(
						"unregisterGLCanvasUser() canvas object was not found inside ViewJogleManager!",
						LoggerType.FULL );

		return false;
	}

	public GLEventListener getGLEventListener(final int iId) {

		return hashGLEventListener.get(iId);
	}

	/**
	 * Register a new GLEventListener with a new Id.
	 */
	public boolean registerGLEventListener(GLEventListener canvasListener,
			int iGLEventListenerId) {

		assert iGLEventListenerId != 0 : "registerItem(Object,int) must not use iItemId==0";		
		
		if (hashGLEventListener.containsKey(iGLEventListenerId))
		{
			refGeneralManager.getSingelton().logMsg(
					"registerGLEventListener() id " + iGLEventListenerId
					+ " is already registered!",
					LoggerType.FULL );
			return false;
		}
		if (hashGLEventListener.containsValue(canvasListener))
		{
			refGeneralManager.getSingelton().logMsg(
					"registerGLEventListener() canvas bound to id " + iGLEventListenerId
					+ " is already registered!",
					LoggerType.FULL );
			return false;
		}

		hashGLEventListener.put(iGLEventListenerId, canvasListener);
		hashGLEventListener_revert.put(canvasListener, iGLEventListenerId);

		return true;
	}

	/**
	 * Attention: Unregister GLEventListener at GLCanvas before callint this method.
	 * 
	 */
	public boolean unregisterGLEventListener(GLEventListener canvasListener) {

		if (!hashGLEventListener_revert.containsKey(canvasListener))
		{
			refGeneralManager
					.getSingelton().logMsg(
							"unregisterGLEventListener() because GLEventListern is unkown!",
							LoggerType.FULL );
			return false;
		}

		int icanvasListenerId = hashGLEventListener_revert.get(canvasListener);

		hashGLEventListener.remove(icanvasListenerId);
		hashGLEventListener_revert.remove(canvasListener);

		//TODO: Unregister GLEventListener at GLCanvas before!

		return true;
	}

	public boolean addGLEventListener2GLCanvasById(int iCanvasListenerId,
			int iCanvasId) {

		GLEventListener listener = hashGLEventListener.get(iCanvasListenerId);

		if (listener == null)
		{
			refGeneralManager
					.getSingelton().logMsg(
							"addGLEventListener2GLCanvasById() because GLEventListern is not registered!",
							LoggerType.FULL );
			return false;
		}

		GLCanvas canvas = hashGLCanvas.get(iCanvasId);

		if (canvas == null)
		{
			refGeneralManager.getSingelton().logMsg(
					"addGLEventListener2GLCanvasById() because GLCanvas is not registered!",
					LoggerType.FULL );
			return false;
		}

		canvas.addGLEventListener(listener);

		return true;
	}

	public boolean removeGLEventListener2GLCanvasById(int iCanvasListenerId,
			int iCanvasId) {

		GLEventListener listener = hashGLEventListener.get(iCanvasListenerId);

		if (listener == null)
		{
			refGeneralManager.getSingelton().logMsg(
					"removeGLEventListener2GLCanvasById() because GLEventListern is not registered!",
					LoggerType.FULL );
			return false;
		}

		GLCanvas canvas = hashGLCanvas.get(iCanvasId);

		if (canvas == null)
		{
			refGeneralManager.getSingelton().logMsg(
					"removeGLEventListener2GLCanvasById() because GLCanvas is not registered!",
					LoggerType.FULL );
			return false;
		}

		canvas.removeGLEventListener(listener);

		return true;
	}

	public IGLCanvasDirector getGLCanvasDirector(int iId) {

		return this.hashGLCanvasDirector.get(iId);
	}

	public synchronized boolean registerGLCanvasDirector(final IGLCanvasDirector director,
			final int iId) {

		if (hashGLCanvasDirector.containsKey(iId))
		{
			refGeneralManager.getSingelton().logMsg(
					"registerGLCanvasDirector() id " + iId
					+ " is already registered!",
					LoggerType.FULL );
			return false;
		}
		if (hashGLCanvasDirector.containsValue(director))
		{
			refGeneralManager.getSingelton().logMsg(
					"registerGLCanvasDirector() director bound to id " + iId
					+ " is already registered!",
					LoggerType.FULL );
			return false;
		}

		hashGLCanvasDirector.put(iId, director);
		hashGLCanvasDirector_revert.put(director, iId);

		return true;

	}

	public boolean unregisterGLCanvasDirector(IGLCanvasDirector director) {

		if (!hashGLCanvasDirector_revert.containsKey(director))
		{
			refGeneralManager.getSingelton().logMsg(
					"unregisterGLEventListener() because GLEventListern is unkown!",
					LoggerType.FULL );
			return false;
		}

		int icanvasDirectorId = hashGLCanvasDirector_revert.get(director);

		hashGLCanvasDirector.remove(icanvasDirectorId);
		hashGLCanvasDirector_revert.remove(director);

		//TODO: check details..

		return true;
	}

	
	@Override
	public void destroyOnExit() {

		refGeneralManager.getSingelton().logMsg(
				"ViewJoglManager.destroyOnExit()",
				LoggerType.FULL );

		Enumeration<IGLCanvasDirector> enumDirector = this.hashGLCanvasDirector_revert
				.keys();

		if (enumDirector != null)
		{

			while (enumDirector.hasMoreElements())
			{
				IGLCanvasDirector refGLCanvasDirector = enumDirector
						.nextElement();

				if (refGLCanvasDirector != null)
				{
					refGLCanvasDirector.destroyDirector();
					refGLCanvasDirector = null;
				}
			} // while
		} // if

		Iterator <JFrame> iterFrame = arWorkspaceJFrame.iterator();		
		while ( iterFrame.hasNext())
		{
			iterFrame.next().dispose();
		}
		
		refGeneralManager.getSingelton().logMsg(
				"ViewJoglManager.destroyOnExit()  ...[DONE]",
				LoggerType.FULL );
	}
	
	public void addViewRep(final IView refView) {

		try 
		{	
			switch ( refView.getViewType() )
			{
			
			case SWT_SET_EDITOR:
			case SWT_DATA_EXCHANGER:
				System.err.println("Ignore: addViewRep("+ 
						refView.getViewType()+
						") type SWT_DATA_EXCHANGER!");
				return;
				
			case SWT_DATA_EXPLORER:
				arDataExplorerViewRep.add( (DataExplorerViewRep) refView);
				return;
			case SWT_HTML_BROWSER:
				arHTMLBrowserViewRep.add( (HTMLBrowserViewRep) refView);
				return;
				
			default:
				assert false : "unsupported ViewType " + refView.getViewType();
			} //switch ( refView.getViewType() )
		}
		catch ( NullPointerException npe)
		{
			System.err.println("addViewRep(IView) getViewType() returned unexpected (class)!");
			
			assert false : "Error,  getViewType() returned unexpected (class)";
		} //try .. catch ( NullPointerException npe)
	}
	
	
	public void removeViewRep(final IView refView) {

		try 
		{	
			switch ( refView.getViewType() )
			{
			
			case SWT_DATA_EXPLORER:
				arDataExplorerViewRep.remove( (DataExplorerViewRep) refView);
				return;
			case SWT_HTML_BROWSER:
				arDataExplorerViewRep.remove( (DataExplorerViewRep) refView);
				return;
				
			default:
				assert false : "unsupported ViewType " + refView.getViewType();
			} //switch ( refView.getViewType() )
			
		}
		catch ( NullPointerException npe)
		{
			System.err.println("removedViewRep(IView) getViewType() returned unexpected (class)!");
			
			assert false : "Error,  getViewType() returned unexpected (class)";
		} //try .. catch ( NullPointerException npe)
	}

	/*
	 * (non-Javadoc)
	 * @see cerberus.manager.IViewManager#getAllViews()
	 */
	public Collection<IView> getAllViews() {

		return hashViewId2View.values();
	}
	
	/*
	 * (non-Javadoc)
	 * @see cerberus.manager.IViewManager#getAllGLCanvasUsers()
	 */
	public Collection<IGLCanvasUser> getAllGLCanvasUsers() {
		
		return hashGLCanvasUser.values();
	}


	public JFrame createWorkspace(ManagerObjectType useViewCanvasType, String sAditionalParameter) {

		switch (useViewCanvasType)
		{
		case VIEW_NEW_FRAME:
//			WorkspaceSwingFrame newJFrame = 
//				new WorkspaceSwingFrame( refGeneralManager, true);
			JFrame newJFrame = 
				new JFrame();
			arWorkspaceJFrame.add(newJFrame);
			
			return newJFrame;
			
		default:
			assert false : "unsupported type!";
			return null;
			
		} //switch (useViewCanvasType)
	
	}


	public Iterator<JFrame> getWorkspaceIterator() {

		return arWorkspaceJFrame.iterator();
	}

	
	/**
	 * @return the joglCanvasForwarderType
	 */
	protected final JoglCanvasForwarderType getJoglCanvasForwarderType() {
	
		return joglCanvasForwarderType;
	}


	
	/**
	 * @param joglCanvasForwarderType the joglCanvasForwarderType to set
	 */
	public final void setJoglCanvasForwarderType(
			JoglCanvasForwarderType type) {
	
		this.joglCanvasForwarderType = type;
	}
}
