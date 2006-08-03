/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.manager.canvas;

import java.util.Vector;
import java.util.Iterator;
import java.util.Hashtable;
import java.awt.Component;
import java.awt.BorderLayout;

import javax.swing.JFrame;

import cerberus.manager.GeneralManager;
import cerberus.manager.ViewCanvasManager;
import cerberus.manager.data.CollectionManager;
import cerberus.manager.type.ManagerObjectType;

import cerberus.data.collection.view.ViewCanvas;
import cerberus.net.dwt.swing.canvas.DSwingHistogramCanvas;
import cerberus.net.dwt.swing.mdi.DDesktopPane;
import cerberus.net.dwt.swing.mdi.DInternalFrame;
import cerberus.net.dwt.swing.jogl.DSwingJoglCanvas;
import cerberus.net.dwt.swing.jogl.WorkspaceSwingFrame;
import cerberus.net.dwt.swing.jogl.listener.GLEventListenerMultiSource;
import cerberus.net.dwt.swing.jogl.listener.GLEventListenerSingleSource;
import cerberus.util.exception.CerberusRuntimeException;

/**
 * Manage several ViewCanvas items.
 * 
 * @author Michael Kalkusch
 *
 */
public class ViewCanvasManagerSimple 
extends CollectionManager
implements ViewCanvasManager,
		GeneralManager {

	private int iIdIncrement = GeneralManager.iUniqueId_Increment;
	
	private int iCurrentViewId = GeneralManager.iUniqueId_View;
	
	private int iCurrentWorkspaceId = GeneralManager.iUniqueId_Workspace;
	
	private Hashtable<Integer,Integer> hashWorkspaceId2IndexLookup;
	
	private boolean bIsFirstWorkspace = true;
	
	/**
	 * Stores references to all manged ViewCanvas items.
	 */
	protected Vector<ViewCanvas> vecViewCanvas;
	
	protected Vector<WorkspaceSwingFrame> vecWorkspaces;
	
	/**
	 * 
	 */
	public ViewCanvasManagerSimple( GeneralManager refSingelton ) {
		super(refSingelton,
				GeneralManager.iUniqueId_TypeOffset_GuiAWT );
		
		vecViewCanvas = new Vector<ViewCanvas>(10);
		
		vecWorkspaces = new Vector<WorkspaceSwingFrame> (3);
		
		hashWorkspaceId2IndexLookup = new Hashtable<Integer,Integer> ();
		
		refGeneralManager.getSingelton().setViewCanvasManager( this );
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
	 * @see cerberus.manager.canvas.ViewCanvasManagerSimple#createCanvas(ManagerObjectType, String)
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
	 * @see prometheus.data.manager.ViewCanvasManager#createCanvas(prometheus.data.manager.ManagerObjectType)
	 */
	public ViewCanvas createCanvas(final ManagerObjectType useViewCanvasType, 
			final String sAditionalParameter ) {
		
		ViewCanvas newView = null;
		
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
	public boolean deleteCanvas(ViewCanvas deleteSet) {
		
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
	public ViewCanvas getItemCanvas(int iItemId) {
		return vecViewCanvas.get( getIndexInVector_byUniqueId(iItemId) );
	}

	/* (non-Javadoc)
	 * @see prometheus.data.manager.ViewCanvasManager#getAllCanvasItems()
	 */
	public ViewCanvas[] getAllCanvasItems() {
		final int iSizeAllViewCanvas = vecViewCanvas.size();
		
		ViewCanvas[] resultBuffer = new ViewCanvas[iSizeAllViewCanvas];
		
		Iterator <ViewCanvas> iter = vecViewCanvas.iterator();
		
		for ( int iIndex=0; iter.hasNext(); iIndex++ ) {
			resultBuffer[iIndex] = iter.next();
		}
		
		return resultBuffer;
	}

	/* (non-Javadoc)
	 * @see prometheus.data.manager.ViewCanvasManager#getManagerType()
	 */
	public ManagerObjectType getManagerType() {
		return ManagerObjectType.VIEW;
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
		ViewCanvas buffer = getItemCanvas( iItemId );
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
			ViewCanvas addItem = (ViewCanvas) registerItem;
			
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
				
		Iterator<ViewCanvas> iter = this.vecViewCanvas.iterator();
		
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


}
