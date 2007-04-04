/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.manager.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;
import java.util.Iterator;
import java.util.Hashtable;
//import java.awt.Component;
//import java.awt.BorderLayout;

import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.swing.JFrame;

import cerberus.manager.IGeneralManager;
import cerberus.manager.IViewGLCanvasManager;
import cerberus.manager.data.ACollectionManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.manager.type.ManagerType;
import cerberus.command.CommandQueueSaxType;
import cerberus.data.collection.view.IViewCanvas;
import cerberus.net.dwt.swing.WorkspaceSwingFrame;
import cerberus.net.dwt.swing.canvas.DSwingHistogramCanvas;
//import cerberus.net.dwt.swing.mdi.DDesktopPane;
import cerberus.net.dwt.swing.mdi.DInternalFrame;
import cerberus.net.dwt.swing.jogl.DSwingJoglCanvas;
import cerberus.net.dwt.swing.jogl.listener.GLEventListenerMultiSource;
import cerberus.net.dwt.swing.jogl.listener.GLEventListenerSingleSource;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.view.gui.IView;
import cerberus.view.gui.IViewRep;
import cerberus.view.gui.ViewType;
import cerberus.view.gui.opengl.IGLCanvasDirector;
import cerberus.view.gui.opengl.IGLCanvasUser;

/**
 * Manage several IViewCanvas items.
 * 
 * @author Michael Kalkusch
 *
 */
public class ViewCanvasManager 
extends ACollectionManager
implements IViewGLCanvasManager,
		IGeneralManager {

	private int iIdIncrement = IGeneralManager.iUniqueId_Increment;
	
	private int iCurrentViewId = IGeneralManager.iUniqueId_View;
	
	private int iCurrentWorkspaceId = IGeneralManager.iUniqueId_Workspace;
	
	private Hashtable<Integer,Integer> hashWorkspaceId2IndexLookup;
	
	private boolean bIsFirstWorkspace = true;
	
	/**
	 * Stores references to all manged IViewCanvas items.
	 */
	protected Vector<IViewCanvas> vecViewCanvas;
	
	protected Vector<WorkspaceSwingFrame> vecWorkspaces;
	
	/**
	 * 
	 */
	public ViewCanvasManager( IGeneralManager refSingelton ) {
		super(refSingelton,
				IGeneralManager.iUniqueId_TypeOffset_GUI_AWT,
				ManagerType.VIEW_GUI_SWT );
		
		vecViewCanvas = new Vector<IViewCanvas>(10);
		
		vecWorkspaces = new Vector<WorkspaceSwingFrame> (3);
		
		hashWorkspaceId2IndexLookup = new Hashtable<Integer,Integer> ();
		
		refGeneralManager.getSingelton().setViewGLCanvasManager( this );
	}

	private boolean hasLookupValueWorkspaceById( final int iItemId ) {
		return hashWorkspaceId2IndexLookup.containsKey( new Integer( iItemId ));
	}
	
	/**
	 * Creates a new Internal Frame as child Frame of the Frame addressed via iTargetFrameId.
	 * Note: Same result as createCanvas( ManagerObjectType.VIEW_NEW_IFRAME , * )
	 * 
	 * @param iTargetFrameId unique Id ot address Frame
	 * @param sAditionalParameter additional parameters
	 * 
	 * @see cerberus.manager.view.ViewCanvasManager#createCanvas(ManagerObjectType, String)
	 * 
	 * @return new DInternalFrame as child of frame addressed via iTargetFrameId
	 */
	public DInternalFrame createNewInternalFrame(final int iTargetFrameId,
			final String sAditionalParameter) {

		WorkspaceSwingFrame refFrame = 
			getItemWorkspace( iTargetFrameId );
		
		DInternalFrame newDInternalFrame = 
			refFrame.createDInternalFrame( sAditionalParameter );
		
		/* set Id & register .. */
		final int iNewId = createNewViewId();
		newDInternalFrame.setId( iNewId );
		registerItem( newDInternalFrame, iNewId, ManagerObjectType.VIEW_NEW_IFRAME );
		
		return newDInternalFrame;		
	}
	
	/**
	 * @see prometheus.data.manager.IViewCanvasManager#createCanvas(prometheus.data.manager.ManagerObjectType)
	 */
	public IViewCanvas createCanvas(final ManagerObjectType useViewCanvasType, 
			final String sAditionalParameter ) {
		
		IViewCanvas newView = null;
		
		GLEventListenerMultiSource listenerGL = null;
		
		switch ( useViewCanvasType ) {
			case VIEW_HISTOGRAM2D:
				return new DSwingHistogramCanvas( this.refGeneralManager, null );
				//return new DHistogramCanvas( this.refGeneralManager );
				
			case VIEW_JOGL_CANVAS_SINGLE:
				listenerGL = 
					new GLEventListenerMultiSource();
				
			case VIEW_JOGL_CANVAS_MULTIPLE:
				listenerGL = 
					new GLEventListenerMultiSource();
				
				try {
					int iTargetFrameId = Integer.valueOf( sAditionalParameter );
					
					GLEventListenerSingleSource listenerSingle = 
						new GLEventListenerSingleSource(null);
					newView = new DSwingJoglCanvas(refGeneralManager,
							listenerSingle,
							iTargetFrameId );
				} catch (NumberFormatException nfe) {
					assert false: "Error converting [" + sAditionalParameter + "] to int!";
				}
				break;
										
			case VIEW_HEATMAP2D:
//				return new SetPlanarSimple(4,getGeneralManager());
			
			case VIEW_NEW_IFRAME:
				try {
					int iTargetFrameId = Integer.valueOf( sAditionalParameter );
					
					return createNewInternalFrame(iTargetFrameId,"");
					
				} catch (NumberFormatException nfe) {
					assert false: "Error in VIEW_NEW_IFRAME converting [" + sAditionalParameter + "] to int!";
				} 
				break;
				
//			case ManagerObjectType.:
//				break;
				
			default:
				throw new CerberusRuntimeException("SetManagerSimple.createSet() failed due to unhandled type [" +
						useViewCanvasType.name() + "]");
			// return null;
		}
		
		
		
		final int iNewId = createNewViewId();
		newView.setId( iNewId );
		registerItem( newView, iNewId, useViewCanvasType);
		
		return newView;	
	}

	protected int createNewViewId() {
		return (iCurrentViewId += iIdIncrement);
	}
	
	protected int createNewWorkspaceId() {
		return (iCurrentWorkspaceId +=iIdIncrement);
	}
	
	/* (non-Javadoc)
	 * @see prometheus.data.manager.ViewCanvasManager#deleteCanvas(prometheus.data.collection.ViewCanvas)
	 */
	public boolean deleteCanvas(IViewCanvas deleteSet) {
		
		return deleteCanvas( 
				vecViewCanvas.indexOf(deleteSet) );
	}

	/* (non-Javadoc)
	 * @see prometheus.data.manager.ViewCanvasManager#deleteCanvas(int)
	 */
	public boolean deleteCanvas( final int iItemId) {
		
		if ( ! hasItem_withUniqueId( iItemId ) ) {
			return false;
		}
		final int iIndexInVector = getIndexInVector_byUniqueId(iItemId);
		
		unregisterItem( iItemId, 
				vecViewCanvas.get( iIndexInVector ).getBaseType() );
		
		//FIXME re-organize hole lookuptable! Otherwise vector would grwo containing null-eleemnts!
		vecViewCanvas.setElementAt(null, iIndexInVector );

		return true;
	}

	/* (non-Javadoc)
	 * @see prometheus.data.manager.ViewCanvasManager#getItemCanvas(int)
	 */
	public IViewCanvas getItemCanvas(int iItemId) {
		return vecViewCanvas.get( getIndexInVector_byUniqueId(iItemId) );
	}

	/* (non-Javadoc)
	 * @see prometheus.data.manager.ViewCanvasManager#getAllCanvasItems()
	 */
	public IViewCanvas[] getAllCanvasItems() {
		final int iSizeAllViewCanvas = vecViewCanvas.size();
		
		IViewCanvas[] resultBuffer = new IViewCanvas[iSizeAllViewCanvas];
		
		Iterator <IViewCanvas> iter = vecViewCanvas.iterator();
		
		for ( int iIndex=0; iter.hasNext(); iIndex++ ) {
			resultBuffer[iIndex] = iter.next();
		}
		
		return resultBuffer;
	}

	/* (non-Javadoc)
	 * @see prometheus.data.manager.GeneralManager#hasItem(int)
	 */
	public boolean hasItem(int iItemId) {
		return hasItem_withUniqueId( iItemId );
	}

	/* (non-Javadoc)
	 * @see prometheus.data.manager.GeneralManager#getItem(int)
	 */
	public Object getItem(int iItemId) {
		IViewCanvas buffer = getItemCanvas( iItemId );
		if ( buffer != null ) {
			return buffer;
		}
		
		return vecWorkspaces.get( this.hashWorkspaceId2IndexLookup.get( iItemId ) );
	}

	/*
	 *  (non-Javadoc)
	 * @see prometheus.manager.ViewCanvasManager#createWorkspace(prometheus.manager.BaseManagerType, java.lang.String)
	 */
	public WorkspaceSwingFrame createWorkspace( 
			final ManagerObjectType useViewCanvasType,
			final String sAditionalParameter ) {
		
		WorkspaceSwingFrame newWorkspace = null;
		
		if ( useViewCanvasType == ManagerObjectType.VIEW_NEW_FRAME) {
			boolean bIsRootFrame = false;
			if ( sAditionalParameter.startsWith("root_frame") ) {
				bIsRootFrame = true;
			}
			newWorkspace = 
				new WorkspaceSwingFrame(refGeneralManager,bIsRootFrame);
		}
		else {
			assert false : "Can not create Workpsace due to invalid ManagerObjectType [" + 
				useViewCanvasType + "]";
			return null;
		}
		
		final int iNewWorkspaceId = createNewWorkspaceId();
		
		// since item is not added to vector yet the size at this moment equals the index.
		final int iPositionInVector = vecWorkspaces.size(); 
		
		newWorkspace.setId(iNewWorkspaceId);
		vecWorkspaces.add( newWorkspace );
				
		// add to this list too, in order to use only one list for checking hasItem()...
		registerItem_byUniqueId_insideCollection( iNewWorkspaceId, iPositionInVector );
		
		// add to hashtable for workspace only.
		hashWorkspaceId2IndexLookup.put( iNewWorkspaceId, iPositionInVector );
		
		newWorkspace.initCanvas();
		
		return newWorkspace;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see prometheus.manager.ViewCanvasManager#deleteWorkspace(int)
	 */
	public boolean deleteWorkspace( final int iItemId ) {
		if ( hashWorkspaceId2IndexLookup.containsKey( iItemId )) {
			
			//FIXME: do cleanup befor removing workspace!
			vecWorkspaces.remove( hashWorkspaceId2IndexLookup.get( iItemId ));
			return true;
		}
		return false;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see prometheus.manager.ViewCanvasManager#getItemWorkspace(int)
	 */
	public WorkspaceSwingFrame getItemWorkspace( final int iItemId ) {
		
		return vecWorkspaces.get( hashWorkspaceId2IndexLookup.get( iItemId ) );
	}
	
	/*
	 *  (non-Javadoc)
	 * @see prometheus.manager.ViewCanvasManager#getWorkspaceIterator()
	 */
	public Iterator<WorkspaceSwingFrame> getWorkspaceIterator() {
		return vecWorkspaces.iterator();
	}
	
	
	/* (non-Javadoc)
	 * @see prometheus.data.manager.GeneralManager#size()
	 */
	public int size() {
		return this.vecViewCanvas.size() + vecWorkspaces.size();
	}


	/* (non-Javadoc)
	 * @see prometheus.data.manager.GeneralManager#registerItem(java.lang.Object, int, prometheus.data.manager.BaseManagerType)
	 */
	public boolean registerItem(Object registerItem, int iItemId,
			ManagerObjectType type) 
	{	
		try {
			IViewCanvas addItem = (IViewCanvas) registerItem;
			
			//addItem.setId( iItemId );
			
			if ( hasItem_withUniqueId( iItemId ) ) {
				vecViewCanvas.set( getIndexInVector_byUniqueId( iItemId ), addItem );
				return true;
			}
			
			registerItem_byUniqueId_insideCollection( iItemId, vecViewCanvas.size() );
			vecViewCanvas.addElement( addItem );
				
			return true;
		}
		catch ( NullPointerException npe) {
			assert false:"cast of object ot storage falied";
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see prometheus.data.manager.GeneralManager#unregisterItem(int, prometheus.data.manager.BaseManagerType)
	 */
	public boolean unregisterItem(int iItemId, ManagerObjectType type) {
		
		return unregisterItem_byUniqueId_insideCollection( iItemId );
	}

	/*
	 *  (non-Javadoc)
	 * @see prometheus.data.manager.ViewCanvasManager#addAllViewCanvas(javax.swing.JFrame)
	 */
	public void addAllViewCanvas( JFrame refJFrame ) {
				
		Iterator<IViewCanvas> iter = this.vecViewCanvas.iterator();
		
		assert false : "missing code";
		
//		while ( iter.hasNext() ) {
//			try {
//				//refJFrame.add( (Component) iter.next(), BorderLayout.CENTER );
////				refJFrame.add( (Component) iter.next() );
////				refJFrame.paint( refJFrame.getGraphics() );
//				
//				DInternalFrame refNewIFrame = refDDesktopPane.createInternalFrame("InternalFrame");
//				
//				refNewIFrame.add( (Component) iter.next() );
//				refNewIFrame.paint( refJFrame.getGraphics() );
//			}
//			catch (NullPointerException npe) {
//				// skip item...
//			}
//		}
	}


	/* ------------------------ */
	
	public IGLCanvasUser createGLCanvasUser(CommandQueueSaxType useViewType, int iViewId, int iParentContainerId, String sLabel) {

		// TODO Auto-generated method stub
		return null;
	}

	public GLCanvas getGLCanvas(int iId) {

		// TODO Auto-generated method stub
		return null;
	}

	public boolean registerGLCanvas(GLCanvas canvas, int iCanvasId) {

		// TODO Auto-generated method stub
		return false;
	}

	public boolean unregisterGLCanvas(GLCanvas canvas) {

		// TODO Auto-generated method stub
		return false;
	}

	public boolean registerGLCanvasUser(IGLCanvasUser canvas, int iCanvasId) {

		// TODO Auto-generated method stub
		return false;
	}

	public boolean unregisterGLCanvasUser(IGLCanvasUser canvas) {

		// TODO Auto-generated method stub
		return false;
	}

	public GLEventListener getGLEventListener(int iId) {

		// TODO Auto-generated method stub
		return null;
	}

	public boolean registerGLEventListener(GLEventListener canvasListener, int iId) {

		// TODO Auto-generated method stub
		return false;
	}

	public boolean unregisterGLEventListener(GLEventListener canvasListener) {

		// TODO Auto-generated method stub
		return false;
	}

	public boolean addGLEventListener2GLCanvasById(int iCanvasListenerId, int iCanvasId) {

		// TODO Auto-generated method stub
		return false;
	}

	public boolean removeGLEventListener2GLCanvasById(int iCanvasListenerId, int iCanvasId) {

		// TODO Auto-generated method stub
		return false;
	}

	public IGLCanvasDirector getGLCanvasDirector(int iId) {

		// TODO Auto-generated method stub
		return null;
	}

	public boolean registerGLCanvasDirector(IGLCanvasDirector director, int iId) {

		// TODO Auto-generated method stub
		return false;
	}

	public boolean unregisterGLCanvasDirector(IGLCanvasDirector director) {

		// TODO Auto-generated method stub
		return false;
	}

	public IView createView(ManagerObjectType useViewType, int iViewId, int iParentContainerId, String sLabel) {

		// TODO Auto-generated method stub
		return null;
	}

	public void addViewRep(IView refView) {

		// TODO Auto-generated method stub
		
	}

	public void removeViewRep(IView refView) {

		// TODO Auto-generated method stub
		
	}

	public Collection<IView> getAllViews() {

		// TODO Auto-generated method stub
		return null;
	}

	public Collection<IGLCanvasUser> getAllGLCanvasUsers() {

		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList<IViewRep> getViewRepByType(ViewType viewType) {

		// TODO Auto-generated method stub
		return null;
	}
	


}
