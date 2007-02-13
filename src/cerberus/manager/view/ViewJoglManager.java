package cerberus.manager.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.Enumeration;

import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;

import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.manager.IViewManager;
import cerberus.manager.IViewGLCanvasManager;
import cerberus.manager.base.AAbstractManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.manager.type.ManagerType;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.view.gui.AViewRep;
import cerberus.view.gui.IView;
import cerberus.view.gui.ViewType;
import cerberus.view.gui.opengl.IGLCanvasDirector;
import cerberus.view.gui.opengl.IGLCanvasUser;
import cerberus.view.gui.opengl.canvas.GLCanvasHeatmap;
import cerberus.view.gui.opengl.canvas.GLCanvasTestTriangle;
import cerberus.view.gui.opengl.canvas.heatmap.GLCanvasHeatmap2D;
import cerberus.view.gui.opengl.canvas.histogram.GLCanvasHistogram2D;
import cerberus.view.gui.opengl.canvas.isosurface.GLCanvasIsoSurface3D;
import cerberus.view.gui.opengl.canvas.pathway.GLCanvasPathway3D;
import cerberus.view.gui.opengl.canvas.scatterplot.GLCanvasMinMaxScatterPlot2D;
import cerberus.view.gui.opengl.canvas.scatterplot.GLCanvasMinMaxScatterPlot3D;
import cerberus.view.gui.opengl.canvas.scatterplot.GLCanvasScatterPlot2D;
import cerberus.view.gui.opengl.canvas.texture.GLCanvasTexture2D;
import cerberus.view.gui.swt.browser.HTMLBrowserViewRep;
import cerberus.view.gui.swt.data.exchanger.DataExchangerViewRep;
import cerberus.view.gui.swt.data.exchanger.SetEditorViewRep;
import cerberus.view.gui.swt.data.explorer.DataExplorerViewRep;
//import cerberus.view.gui.swt.data.DataTableViewRep;
import cerberus.view.gui.swt.mixer.MixerViewRep;
import cerberus.view.gui.swt.pathway.Pathway2DViewRep;
import cerberus.view.gui.swt.progressbar.ProgressBarViewRep;
import cerberus.view.gui.swt.gears.jogl.GearsViewRep;
import cerberus.view.gui.swt.heatmap.jogl.SwtJogHistogram2DViewRep;
import cerberus.view.gui.swt.jogl.SwtJoglGLCanvasViewRep;
import cerberus.view.gui.swt.jogl.sample.TestTriangleViewRep;
import cerberus.view.gui.swt.scatterplot.jogl.Scatterplot2DViewRep;
import cerberus.view.gui.swt.slider.SelectionSliderViewRep;
import cerberus.view.gui.swt.slider.StorageSliderViewRep;
import cerberus.view.gui.swt.heatmap.jogl.Heatmap2DViewRep;
import cerberus.view.gui.swt.image.ImageViewRep;
import cerberus.view.gui.swt.test.TestTableViewRep;
import cerberus.xml.parser.command.CommandQueueSaxType;

/**
 * Manage all canvas, view, ViewRep's nad GLCanvas objects.
 * 
 * @author Michael Kalkusch
 */
public class ViewJoglManager 
extends AAbstractManager
implements IViewManager, IViewGLCanvasManager {

	/**
	 * Stores, which GLEventListener are registered to one GLCanvas.
	 * hashGLCanvasId2Vector provices mapping of unique-GLCanvas-Id to position inside this vector.
	 */
	private HashMap<Integer, Vector<GLEventListener>> hashGLCanvasId_2_vecGLEventListener;

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
	 * when data is created.
	 */
	protected ArrayList<AViewRep> arDataExplorerViewRep;

	public ViewJoglManager(IGeneralManager setGeneralManager) {

		super(setGeneralManager, 
				IGeneralManager.iUniqueId_TypeOffset_GUI_AWT,
				ManagerType.GUI_SWT );

		assert setGeneralManager != null : "Constructor with null-pointer to singelton";

		hashViewId2View = new HashMap<Integer, IView>();

		hashGLCanvas = new HashMap<Integer, GLCanvas>();
		hashGLCanvas_revert = new HashMap<GLCanvas, Integer>();

		hashGLEventListener = new HashMap<Integer, GLEventListener>();
		hashGLEventListener_revert = new HashMap<GLEventListener, Integer>();

		/** internal datastructure to map GLCanvas to GLEventListeners .. */
		hashGLCanvasId_2_vecGLEventListener = new HashMap<Integer, Vector<GLEventListener>>();

		hashGLCanvasUser = new HashMap<Integer, IGLCanvasUser>();
		hashGLCanvasUser_revert = new HashMap<IGLCanvasUser, Integer>();

		hashGLCanvasDirector = new HashMap<Integer, IGLCanvasDirector>();
		hashGLCanvasDirector_revert = new Hashtable<IGLCanvasDirector, Integer>();

		arDataExplorerViewRep = new ArrayList<AViewRep>();

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

	public boolean registerItem(Object registerItem, int iItemId,
			ManagerObjectType type) {

		assert iItemId != 0 : "registerItem(Object,int) must not use iItemId==0";
		
		IView registerView = (IView) registerItem;

		hashViewId2View.put(iItemId, registerView);

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
	public IView createView(ManagerObjectType useViewType, int iViewId,
			int iParentContainerId, String sLabel) {

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
			return new Pathway2DViewRep(this.refGeneralManager, iViewId,
					iParentContainerId, sLabel);
		case VIEW_SWT_DATA_EXPLORER:
			return new DataExplorerViewRep(this.refGeneralManager, iViewId,
					iParentContainerId, sLabel);
		case VIEW_SWT_DATA_EXCHANGER:
			return new DataExchangerViewRep(this.refGeneralManager, iViewId,
					iParentContainerId, sLabel);	
		case VIEW_SWT_DATA_SET_EDITOR:
			return new SetEditorViewRep(this.refGeneralManager, iViewId,
					iParentContainerId, sLabel);
		case VIEW_SWT_PROGRESS_BAR:
			return new ProgressBarViewRep(this.refGeneralManager, iViewId,
					iParentContainerId, sLabel);
		case VIEW_SWT_TEST_TABLE:
			return new TestTableViewRep(this.refGeneralManager, iViewId,
					iParentContainerId, sLabel);
		case VIEW_SWT_GEARS:
			return new GearsViewRep(this.refGeneralManager, iViewId,
					iParentContainerId, sLabel);
		case VIEW_SWT_HEATMAP2D:
			return new Heatmap2DViewRep(this.refGeneralManager, iViewId,
					iParentContainerId, sLabel);
		case VIEW_SWT_HISTOGRAM2D:
			return new SwtJogHistogram2DViewRep(this.refGeneralManager,
					iViewId, iParentContainerId, sLabel);
		//return new Heatmap2DViewRep(iNewId, this.refGeneralManager);
		case VIEW_SWT_SCATTERPLOT2D:
			return new Scatterplot2DViewRep(this.refGeneralManager, iViewId,
					iParentContainerId, sLabel);
		case VIEW_SWT_SCATTERPLOT3D:
			return new Scatterplot2DViewRep(this.refGeneralManager, iViewId,
					iParentContainerId, sLabel);
		case VIEW_SWT_SELECTION_SLIDER:
			return new SelectionSliderViewRep(this.refGeneralManager, iViewId,
					iParentContainerId, sLabel);
		case VIEW_SWT_STORAGE_SLIDER:
			return new StorageSliderViewRep(this.refGeneralManager, iViewId,
					iParentContainerId, sLabel);
		case VIEW_SWT_MIXER:
			return new MixerViewRep(this.refGeneralManager, iViewId,
					iParentContainerId, sLabel);			
		case VIEW_SWT_BROWSER:
			return new HTMLBrowserViewRep(this.refGeneralManager, iViewId,
					iParentContainerId, sLabel);
		case VIEW_SWT_IMAGE:
			return new ImageViewRep(this.refGeneralManager, iViewId,
					iParentContainerId, sLabel);
		case VIEW_SWT_JOGL_TEST_TRIANGLE:
			return new TestTriangleViewRep(this.refGeneralManager, iViewId,
					iParentContainerId, sLabel);
		case VIEW_SWT_JOGL_MULTI_GLCANVAS:
			return new SwtJoglGLCanvasViewRep(this.refGeneralManager, iViewId,
					iParentContainerId, sLabel);

		default:
			throw new CerberusRuntimeException(
					"StorageManagerSimple.createView() failed due to unhandled type ["
							+ useViewType.toString() + "]");
		}
	}

	public ArrayList<AViewRep> getViewByType(ViewType viewType) {

		if (viewType.equals(ViewType.SWT_DATA_EXPLORER))
			return arDataExplorerViewRep;

		return null;
	}

	public IGLCanvasUser createGLCanvasUser(CommandQueueSaxType useViewType,
			int iViewId, int iParentContainerId, String sLabel) {

		switch (useViewType)
		{
		case CREATE_GL_TRIANGLE_TEST:
			return new GLCanvasTestTriangle(refGeneralManager, iViewId,
					iParentContainerId, sLabel);

		case CREATE_GL_HEATMAP:
			return new GLCanvasHeatmap(refGeneralManager, iViewId,
					iParentContainerId, sLabel);

		case CREATE_GL_HEATMAP2D:
			return new GLCanvasHeatmap2D(refGeneralManager, iViewId,
					iParentContainerId, sLabel);
			
		case CREATE_GL_SCATTERPLOT2D:
			return new GLCanvasScatterPlot2D(refGeneralManager, iViewId,
					iParentContainerId, sLabel);

		case CREATE_GL_MINMAX_SCATTERPLOT2D:
			return new GLCanvasMinMaxScatterPlot2D(refGeneralManager, iViewId,
					iParentContainerId, sLabel);

		case CREATE_GL_MINMAX_SCATTERPLOT3D:
			return new GLCanvasMinMaxScatterPlot3D(refGeneralManager, iViewId,
					iParentContainerId, sLabel);

		case CREATE_GL_HISTOGRAM2D:
			return new GLCanvasHistogram2D(refGeneralManager, iViewId,
					iParentContainerId, sLabel);

		case CREATE_GL_ISOSURFACE3D:
			return new GLCanvasIsoSurface3D(
					refGeneralManager, 
					iViewId, 
					iParentContainerId, 
					sLabel);
			
		case CREATE_GL_TEXTURE2D:
			return new GLCanvasTexture2D(
					refGeneralManager, 
					iViewId, 
					iParentContainerId, 
					sLabel);
			
		case CREATE_GL_PATHWAY2D:
			return new GLCanvasPathway3D(refGeneralManager, iViewId,
					iParentContainerId, sLabel);

		default:
			throw new CerberusRuntimeException(
					"ViewJoglManager.createGLCanvasUser() failed due to unhandled type ["
							+ useViewType.toString() + "]");
		}
	}

	public GLCanvas getGLCanvas(final int iId) {

		return hashGLCanvas.get(iId);
	}

	public boolean registerGLCanvas(final GLCanvas canvas, final int iCanvasId) {

		assert iCanvasId != 0 : "registerItem(Object,int) must not use iItemId==0";
		
		
		if (hashGLCanvas.containsKey(iCanvasId))
		{
			refGeneralManager.getSingelton().logMsg(
					"registerGLCanvas() id " + iCanvasId
							+ " is already registered!");
			return false;
		}
		if (hashGLCanvas.containsValue(canvas))
		{
			refGeneralManager.getSingelton().logMsg(
					"registerGLCanvas() canvas bound to id " + iCanvasId
							+ " is already registered!");
			return false;
		}

		hashGLCanvas.put(iCanvasId, canvas);
		hashGLCanvas_revert.put(canvas, iCanvasId);

		synchronized (getClass())
		{
			hashGLCanvasId_2_vecGLEventListener.put(iCanvasId,
					new Vector<GLEventListener>());
		}

		return true;
	}

	public boolean unregisterGLCanvas(final GLCanvas canvas) {

		if (hashGLCanvas_revert.containsKey(canvas))
		{

			int iCanvasId = hashGLCanvas_revert.get(canvas);

			hashGLCanvas.remove(iCanvasId);
			hashGLCanvas_revert.remove(canvas);

			synchronized (getClass())
			{
				/* get all GLEventListeners registered to this GLCanvas... */
				Vector<GLEventListener> vecListOfRemoveableGLEventListeners = hashGLCanvasId_2_vecGLEventListener
						.get(iCanvasId);

				Iterator<GLEventListener> iterListeners = vecListOfRemoveableGLEventListeners
						.iterator();

				while (iterListeners.hasNext())
				{
					canvas.removeGLEventListener(iterListeners.next());
				}

				/* Clean up HashMap with id -> Vector<GLEventListener> .. */
				hashGLCanvasId_2_vecGLEventListener.remove(iCanvasId);
			}
			/** Unregister all GLListeners to! */

			return true;
		}

		refGeneralManager
				.getSingelton()
				.logMsg(
						"unregisterGLCanvas() canvas object was not found inside ViewJogleManager!");

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

		synchronized (getClass())
		{
			hashGLCanvasId_2_vecGLEventListener.put(iCanvasId,
					new Vector<GLEventListener>());
		}
		
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
				.getSingelton()
				.logMsg(
						"unregisterGLCanvasUser() canvas object was not found inside ViewJogleManager!");

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
							+ " is already registered!");
			return false;
		}
		if (hashGLEventListener.containsValue(canvasListener))
		{
			refGeneralManager.getSingelton().logMsg(
					"registerGLEventListener() canvas bound to id " + iGLEventListenerId
							+ " is already registered!");
			return false;
		}

		hashGLEventListener.put(iGLEventListenerId, canvasListener);

		return true;
	}

	/**
	 * Attention: Unregister GLEventListener at GLCanvas before callint this methode.
	 * 
	 */
	public boolean unregisterGLEventListener(GLEventListener canvasListener) {

		if (!hashGLEventListener_revert.containsKey(canvasListener))
		{
			refGeneralManager
					.getSingelton()
					.logMsg(
							"unregisterGLEventListener() because GLEventListern is unkown!");
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
					.getSingelton()
					.logMsg(
							"addGLEventListener2GLCanvasById() because GLEventListern is not registered!");
			return false;
		}

		GLCanvas canvas = hashGLCanvas.get(iCanvasId);

		if (canvas == null)
		{
			refGeneralManager
					.getSingelton()
					.logMsg(
							"addGLEventListener2GLCanvasById() because GLCanvas is not registered!");
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
			refGeneralManager
					.getSingelton()
					.logMsg(
							"removeGLEventListener2GLCanvasById() because GLEventListern is not registered!");
			return false;
		}

		GLCanvas canvas = hashGLCanvas.get(iCanvasId);

		if (canvas == null)
		{
			refGeneralManager
					.getSingelton()
					.logMsg(
							"removeGLEventListener2GLCanvasById() because GLCanvas is not registered!");
			return false;
		}

		canvas.removeGLEventListener(listener);

		return true;
	}

	public IGLCanvasDirector getGLCanvasDirector(int iId) {

		return this.hashGLCanvasDirector.get(iId);
	}

	public boolean registerGLCanvasDirector(final IGLCanvasDirector director,
			final int iId) {

		if (hashGLCanvasDirector.containsKey(iId))
		{
			refGeneralManager.getSingelton().logMsg(
					"registerGLCanvasDirector() id " + iId
							+ " is already registered!");
			return false;
		}
		if (hashGLCanvasDirector.containsValue(director))
		{
			refGeneralManager.getSingelton().logMsg(
					"registerGLCanvasDirector() director bound to id " + iId
							+ " is already registered!");
			return false;
		}

		hashGLCanvasDirector.put(iId, director);
		hashGLCanvasDirector_revert.put(director, iId);

		return true;

	}

	public boolean unregisterGLCanvasDirector(IGLCanvasDirector director) {

		if (!hashGLCanvasDirector_revert.containsKey(director))
		{
			refGeneralManager
					.getSingelton()
					.logMsg(
							"unregisterGLEventListener() because GLEventListern is unkown!");
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
				"ViewJoglManager.destroyOnExit()");

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

		refGeneralManager.getSingelton().logMsg(
				"ViewJoglManager.destroyOnExit()  ...[DONE]");
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
}
