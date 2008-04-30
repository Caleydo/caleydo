package org.caleydo.core.manager.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.swing.JFrame;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.data.view.camera.IViewFrustum;
import org.caleydo.core.data.view.rep.renderstyle.layout.ARemoteViewLayoutRenderStyle;
import org.caleydo.core.manager.AManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IViewGLCanvasManager;
import org.caleydo.core.manager.type.ManagerObjectType;
import org.caleydo.core.manager.type.ManagerType;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.view.IView;
import org.caleydo.core.view.IViewRep;
import org.caleydo.core.view.ViewType;
import org.caleydo.core.view.opengl.canvas.AGLCanvasUser;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.glyph.GLCanvasGlyph;
import org.caleydo.core.view.opengl.canvas.parcoords.GLCanvasParCoords3D;
import org.caleydo.core.view.opengl.canvas.pathway.GLCanvasJukeboxPathway3D;
import org.caleydo.core.view.opengl.canvas.pathway.GLCanvasPathway3D;
import org.caleydo.core.view.opengl.canvas.remote.GLCanvasRemoteRendering3D;
import org.caleydo.core.view.opengl.canvas.remote.glyph.GLCanvasRemoteGlyph;
import org.caleydo.core.view.opengl.canvas.wii.GLCanvasWiiTest;
import org.caleydo.core.view.opengl.util.infoarea.GLInfoAreaManager;
import org.caleydo.core.view.swt.browser.HTMLBrowserViewRep;
import org.caleydo.core.view.swt.data.exchanger.DataExchangerViewRep;
import org.caleydo.core.view.swt.data.exchanger.NewSetEditorViewRep;
import org.caleydo.core.view.swt.data.explorer.DataExplorerViewRep;
import org.caleydo.core.view.swt.data.search.DataEntitySearcherViewRep;
import org.caleydo.core.view.swt.image.ImageViewRep;
import org.caleydo.core.view.swt.jogl.SwtJoglGLCanvasViewRep;
import org.caleydo.core.view.swt.jogl.gears.GearsViewRep;
import org.caleydo.core.view.swt.mixer.MixerViewRep;
import org.caleydo.core.view.swt.pathway.Pathway2DViewRep;
import org.caleydo.core.view.swt.progressbar.ProgressBarViewRep;
import org.caleydo.core.view.swt.slider.SelectionSliderViewRep;
import org.caleydo.core.view.swt.slider.StorageSliderViewRep;
import org.caleydo.core.view.swt.test.TestTableViewRep;
import org.caleydo.core.view.swt.undoredo.UndoRedoViewRep;

import com.sun.opengl.util.Animator;
import com.sun.opengl.util.FPSAnimator;

/**
 * Manage all canvas, view, ViewRep's and GLCanvas objects.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class ViewGLCanvasManager 
extends AManager
implements IViewGLCanvasManager {

	protected HashMap<Integer, IView> hashViewId2View;

	protected HashMap<Integer, GLCanvas> hashGLCanvasID2GLCanvas;
	
	protected HashMap<Integer, ArrayList<GLEventListener>> hashGLCanvasID2GLEventListeners;

	protected HashMap<Integer, GLEventListener> hashGLEventListenerID2GLEventListener;
	
	/**
	 * List of data explorer view reps (needed for mediator registration 
	 * when data is created).
	 */
	protected ArrayList<IViewRep> arDataExplorerViewRep;
	
	/**
	 * List of HTML browser view reps
	 */
	protected ArrayList<IViewRep> arHTMLBrowserViewRep;
		
	protected ArrayList<JFrame> arWorkspaceJFrame;
	
	private Animator fpsAnimator;
	
	private DataEntitySearcherViewRep dataEntitySearcher;
	
	private PickingManager pickingManager;
	
	private SelectionManager selectionManager;

	private GLInfoAreaManager infoAreaManager; 
	
	/**
	 * Constructor.
	 * 
	 * @param setGeneralManager
	 */
	public ViewGLCanvasManager(final IGeneralManager generalManager) {

		super(generalManager, 
				IGeneralManager.iUniqueId_TypeOffset_GUI_AWT,
				ManagerType.VIEW);

		assert generalManager != null : "Constructor with null-pointer to singelton";

		pickingManager = new PickingManager(generalManager);
		selectionManager = new SelectionManager(generalManager);
		infoAreaManager = new GLInfoAreaManager(generalManager);
		
		hashViewId2View = new HashMap<Integer, IView>();
		hashGLCanvasID2GLCanvas = new HashMap<Integer, GLCanvas>();
		hashGLCanvasID2GLEventListeners = new HashMap<Integer, ArrayList<GLEventListener>>();
		hashGLEventListenerID2GLEventListener = new HashMap<Integer, GLEventListener>();
		
		arDataExplorerViewRep = new ArrayList<IViewRep>();
		arHTMLBrowserViewRep = new ArrayList<IViewRep>();
		arWorkspaceJFrame = new ArrayList<JFrame>();
	}


	public boolean hasItem(int iItemId) {

		if (hashViewId2View.containsKey(iItemId))
			return true;

		if (hashGLCanvasID2GLCanvas.containsKey(iItemId))
			return true;
		
		if (hashGLEventListenerID2GLEventListener.containsKey(iItemId))
			return true;
		
		return false;
	}

	public Object getItem(int iItemId) {

		IView bufferIView = hashViewId2View.get(iItemId);

		if (bufferIView != null)
			return bufferIView;

		GLCanvas bufferCanvas = hashGLCanvasID2GLCanvas.get(iItemId);

		if (bufferCanvas != null)
			return bufferCanvas;
		
		GLEventListener bufferEventListener = 
			hashGLEventListenerID2GLEventListener.get(iItemId);
		
		if (bufferEventListener != null)
			return bufferEventListener;
		
		return null;
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
		
//		generalManager.logMsg(
//				"registerItem( " + iItemId + " ) as View", 
//				LoggerType.VERBOSE);

		return true;
	}

	public boolean unregisterItem(int iItemId, ManagerObjectType type) {

		assert false : "not done yet";
		return false;
	}
	

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IViewManager#createView(org.caleydo.core.manager.type.ManagerObjectType, int, int, java.lang.String)
	 */
	public IView createView(final ManagerObjectType useViewType, 
			final int iViewID,
			final int iParentContainerID,
			final String sLabel ) {

		if (useViewType.getGroupType() != ManagerType.VIEW)
		{
			throw new CaleydoRuntimeException(
					"try to create object with wrong type "
							+ useViewType.name());
		}

		//final int iNewId = this.createNewId(useViewType);

		try
		{
			switch (useViewType)
			{
			case VIEW:

			case VIEW_SWT_PATHWAY:
				return new Pathway2DViewRep(generalManager, iViewID,
						iParentContainerID, sLabel);
			case VIEW_SWT_DATA_EXPLORER:
				return new DataExplorerViewRep(generalManager, iViewID,
						iParentContainerID, sLabel);
			case VIEW_SWT_DATA_EXCHANGER:
				return new DataExchangerViewRep(generalManager, iViewID,
						iParentContainerID, sLabel);	
			case VIEW_SWT_DATA_SET_EDITOR:
				return new NewSetEditorViewRep(generalManager, iViewID,
						iParentContainerID, sLabel);
			case VIEW_SWT_PROGRESS_BAR:
				return new ProgressBarViewRep(generalManager, iViewID,
						iParentContainerID, sLabel);
			case VIEW_SWT_TEST_TABLE:
				return new TestTableViewRep(generalManager, iViewID,
						iParentContainerID, sLabel);
			case VIEW_SWT_SELECTION_SLIDER:
				return new SelectionSliderViewRep(generalManager, iViewID,
						iParentContainerID, sLabel);
			case VIEW_SWT_STORAGE_SLIDER:
				return new StorageSliderViewRep(generalManager, iViewID,
						iParentContainerID, sLabel);
			case VIEW_SWT_MIXER:
				return new MixerViewRep(generalManager, iViewID,
						iParentContainerID, sLabel);			
			case VIEW_SWT_BROWSER:
				return new HTMLBrowserViewRep(generalManager, iViewID,
						iParentContainerID, sLabel);
			case VIEW_SWT_IMAGE:
				return new ImageViewRep(generalManager, iViewID,
						iParentContainerID, sLabel);
			case VIEW_SWT_UNDO_REDO:
				return new UndoRedoViewRep(generalManager, iViewID,
						iParentContainerID, sLabel);	
			case VIEW_SWT_DATA_ENTITY_SEARCHER:
				return new DataEntitySearcherViewRep(generalManager, iViewID,
						iParentContainerID, sLabel);	

			default:
				throw new CaleydoRuntimeException(
						"StorageManagerSimple.createView() failed due to unhandled type ["
								+ useViewType.toString() + "]");
			}
			
		} 
		catch (NullPointerException e)
		{
//			generalManager.logMsg("Error while creating view; createView(" +
//					useViewType.toString() +
//					", " + iViewID + 
//					", " + iParentContainerID + 
//					", " + sLabel + " )", LoggerType.ERROR);
			
			return null;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IViewManager#createGLView(org.caleydo.core.manager.type.ManagerObjectType, int, int, int, java.lang.String)
	 */
	public IView createGLView(final ManagerObjectType useViewType, 
			final int iViewID,
			final int iParentContainerID,
			final int iGLCanvasID,
			final String sLabel ) {

		if (useViewType.getGroupType() != ManagerType.VIEW)
		{
			throw new CaleydoRuntimeException(
					"try to create object with wrong type "
							+ useViewType.name());
		}

		try
		{
			switch (useViewType)
			{
			case VIEW_SWT_GEARS:
				return new GearsViewRep(generalManager, iViewID,
						iParentContainerID, iGLCanvasID, sLabel);
			case VIEW_SWT_JOGL_MULTI_GLCANVAS:
				return new SwtJoglGLCanvasViewRep(generalManager, iViewID,
						iParentContainerID,	iGLCanvasID, sLabel);

			default:
				throw new CaleydoRuntimeException(
						"StorageManagerSimple.createView() failed due to unhandled type ["
								+ useViewType.toString() + "]");
			}
		} 
		catch (NullPointerException e)
		{
//			generalManager.logMsg("Error while creating view; createView(" +
//					useViewType.toString() +
//					", " + iViewID + 
//					", " + iParentContainerID + 
//					", " + sLabel + " )", LoggerType.ERROR);
			
			return null;
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

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IViewGLCanvasManager#createGLCanvas(org.caleydo.core.command.CommandQueueSaxType, int, int, java.lang.String, org.caleydo.core.data.view.camera.IViewFrustum)
	 */
	public GLEventListener createGLCanvas(CommandQueueSaxType useViewType,
			final int iUniqueId, 
			final int iGLCanvasID,
			final String sLabel,
			final IViewFrustum viewFrustum) {

		try
		{
			switch (useViewType)
			{	
//			case CREATE_GL_SCATTERPLOT2D:
//				return new GLCanvasScatterPlot2D(refGeneralManager, iUniqueId,
//						iGlForwarderId, sLabel);
//	
//			case CREATE_GL_MINMAX_SCATTERPLOT2D:
//				refSingelton.logMsg("CREATE_GL_MINMAX_SCATTERPLOT2D ==> create GLMinMaxScatterplot2Dinteractive instead",LoggerType.MINOR_ERROR_XML);
//				
//				return new GLMinMaxScatterplot2Dinteractive(refGeneralManager, iUniqueId,
//						iGlForwarderId, sLabel);
//	
//			case CREATE_GL_MINMAX_SCATTERPLOT3D:
//				return new GLCanvasMinMaxScatterPlot3D(refGeneralManager, iUniqueId,
//						iGlForwarderId, sLabel);
//				
//			case CREATE_GL_WIDGET:
//				return new GLCanvasWidget(refGeneralManager, iUniqueId,
//						iGlForwarderId, sLabel);
//	
//			case CREATE_GL_HISTOGRAM2D:
//				return new GLCanvasHistogram2D(refGeneralManager, iUniqueId,
//						iGlForwarderId, sLabel);
//	
//			case CREATE_GL_ISOSURFACE3D:
//				return new GLCanvasIsoSurface3D(
//						refGeneralManager, 
//						iUniqueId, 
//						iGlForwarderId, 
//						sLabel);
//				
//			case CREATE_GL_TEXTURE2D:
//				return new GLCanvasTexture2D(
//						refGeneralManager, 
//						iUniqueId, 
//						iGlForwarderId, 
//						sLabel);

//	
			case CREATE_GL_JUKEBOX_PATHWAY_3D:
				return new GLCanvasJukeboxPathway3D(
						generalManager, 
						iUniqueId,
						iGLCanvasID, 
						sLabel,
						viewFrustum);
				
			case CREATE_GL_PATHWAY_3D:
				return new GLCanvasPathway3D(
						generalManager, 
						iUniqueId,
						iGLCanvasID,
						sLabel,
						viewFrustum);	
				
			case CREATE_GL_PARALLEL_COORDINATES_3D:
				return new GLCanvasParCoords3D(
						generalManager, 
						iUniqueId,
						iGLCanvasID, 
						sLabel,
						viewFrustum);
				
			case CREATE_GL_GLYPH:
				return new GLCanvasGlyph(
						generalManager, 
						iUniqueId,
						iGLCanvasID, 
						sLabel,
						viewFrustum);				
				
			case CREATE_GL_BUCKET_3D:
				return new GLCanvasRemoteRendering3D(
						generalManager, 
						iUniqueId,
						iGLCanvasID, 
						sLabel,
						viewFrustum,
						ARemoteViewLayoutRenderStyle.LayoutMode.BUCKET);	
				
			case CREATE_GL_JUKEBOX_3D:
				return new GLCanvasRemoteRendering3D(
						generalManager, 
						iUniqueId,
						iGLCanvasID, 
						sLabel,
						viewFrustum,
						ARemoteViewLayoutRenderStyle.LayoutMode.JUKEBOX);	
				
			case CREATE_GL_WII_TEST:
				return new GLCanvasWiiTest(
						generalManager, 
						iUniqueId,
						iGLCanvasID, 
						sLabel,
						viewFrustum);	
				
			case CREATE_GL_REMOTE_GLYPH:
				return new GLCanvasRemoteGlyph(
						generalManager, 
						iUniqueId,
						iGLCanvasID, 
						sLabel,
						viewFrustum);					
				
			default:
				throw new CaleydoRuntimeException(
						"ViewJoglManager.createGLCanvasUser() failed due to unhandled type ["
								+ useViewType.toString() + "]");
			}
		
		} 
		catch (NullPointerException e)
		{
//			generalManager.logMsg("Error while creating view; createView(" +
//					useViewType.toString() +
//					", " + iUniqueId + 
//					", " + iGLCanvasID + 
//					", " + sLabel + " )", LoggerType.ERROR);
			
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IViewGLCanvasManager#registerGLCanvas(javax.media.opengl.GLCanvas, int)
	 */
	public boolean registerGLCanvas(final GLCanvas glCanvas, final int iCanvasId) {

		assert iCanvasId != 0 : "registerItem(Object,int) must not use iItemId==0";
		
		if (hashGLCanvasID2GLCanvas.containsKey(iCanvasId))
		{
//			generalManager.logMsg(
//					"registerGLCanvas() id " + iCanvasId
//					+ " is already registered!",
//					LoggerType.FULL );
			return false;
		}
		if (hashGLCanvasID2GLCanvas.containsValue(glCanvas))
		{
//			generalManager.logMsg(
//					"registerGLCanvas() canvas bound to id " + iCanvasId
//					+ " is already registered!",
//					LoggerType.FULL );
			return false;
		}

		hashGLCanvasID2GLCanvas.put(iCanvasId, glCanvas);

		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IViewGLCanvasManager#unregisterGLCanvas(javax.media.opengl.GLCanvas)
	 */
	public boolean unregisterGLCanvas(final GLCanvas canvas) {

		// TODO: IMPLEMENT!
		
//		generalManager.logMsg(
//						"unregisterGLCanvas() canvas object was not found inside ViewJogleManager!",
//						LoggerType.FULL );

		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IViewGLCanvasManager#registerGLEventListenerByGLCanvasID(int, javax.media.opengl.GLEventListener)
	 */
	public void registerGLEventListenerByGLCanvasID(final int iGLCanvasID,
			final GLEventListener gLEventListener) {
		
		hashGLEventListenerID2GLEventListener.put((
				(AGLCanvasUser)gLEventListener).getId(), gLEventListener);
		
		if (iGLCanvasID == -1)
			return;
		
		if (!hashGLCanvasID2GLEventListeners.containsKey(iGLCanvasID))
			hashGLCanvasID2GLEventListeners.put(iGLCanvasID, new ArrayList<GLEventListener>());
			
		hashGLCanvasID2GLEventListeners.get(iGLCanvasID).add(gLEventListener);
	}


	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IViewGLCanvasManager#unregisterGLEventListener()
	 */
	public void unregisterGLEventListener(final int iGLEventListenerID) {

		GLEventListener gLEventListenerToRemove = 
			hashGLEventListenerID2GLEventListener.get(iGLEventListenerID);
		
		GLCaleydoCanvas parentGLCanvas = ((AGLCanvasUser)gLEventListenerToRemove).getParentGLCanvas();
		
		if (parentGLCanvas != null)
		{
			parentGLCanvas.removeGLEventListener(gLEventListenerToRemove);
			hashGLCanvasID2GLEventListeners.get(parentGLCanvas.getID()).remove(gLEventListenerToRemove);	
		}

		hashGLEventListenerID2GLEventListener.remove(iGLEventListenerID);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.base.AAbstractManager#destroyOnExit()
	 */
	public void destroyOnExit() {

//		generalManager.logMsg(
//				"ViewJoglManager.destroyOnExit()  ...[DONE]",
//				LoggerType.FULL );
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
			case SWT_DATA_ENTITY_SEARCHER:
				dataEntitySearcher = (DataEntitySearcherViewRep)refView;
				
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
	
	public void removeAllGLCanvasByType() 
	{
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IViewManager#getAllViews()
	 */
	public Collection<IView> getAllViews() {

		return hashViewId2View.values();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IViewManager#getAllGLCanvasUsers()
	 */
	public Collection<GLCanvas> getAllGLCanvasUsers() {
		
		return hashGLCanvasID2GLCanvas.values();
	}
	
	public Collection<GLEventListener> getAllGLEventListeners() {
		
		return hashGLEventListenerID2GLEventListener.values();
	}


	public JFrame createWorkspace(
			ManagerObjectType useViewCanvasType, String sAditionalParameter) {

		switch (useViewCanvasType)
		{
		case VIEW_NEW_FRAME:
			JFrame newJFrame = 
				new JFrame();
			arWorkspaceJFrame.add(newJFrame);
			
			return newJFrame;
			
		default:
			assert false : "unsupported type!";
			return null;
			
		} //switch (useViewCanvasType)
	
	}
	
	public DataEntitySearcherViewRep getDataEntitySearcher() {
		
		return dataEntitySearcher;
	}
	
	public PickingManager getPickingManager() {

		return pickingManager;
	}
	
	public SelectionManager getSelectionManager() {
		
		return selectionManager;
	}
	
	public GLInfoAreaManager getInfoAreaManager() {
		
		return infoAreaManager;
	}
	
	public void createAnimator() {
		
		fpsAnimator = new FPSAnimator(null, 60);
		
		Iterator<GLCanvas> iterGLCanvas = hashGLCanvasID2GLCanvas.values().iterator();
		while(iterGLCanvas.hasNext())
		{
			fpsAnimator.add(iterGLCanvas.next());
		}
		
		fpsAnimator.start();
	}
	
	public Animator getAnimator() {
		
		return fpsAnimator;
	}
}
