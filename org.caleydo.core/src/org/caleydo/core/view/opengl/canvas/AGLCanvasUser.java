package org.caleydo.core.view.opengl.canvas;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.Iterator;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.view.swt.CmdViewLoadURLInHTMLBrowser;
import org.caleydo.core.data.AUniqueManagedObject;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.SetType;
import org.caleydo.core.data.collection.set.selection.SetSelection;
import org.caleydo.core.data.view.camera.IViewCamera;
import org.caleydo.core.data.view.camera.IViewFrustum;
import org.caleydo.core.data.view.camera.ViewCameraBase;
import org.caleydo.core.data.view.camera.ViewFrustumBase.ProjectionMode;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.data.ISetManager;
import org.caleydo.core.manager.type.ManagerObjectType;
import org.caleydo.core.manager.view.EPickingMode;
import org.caleydo.core.manager.view.EPickingType;
import org.caleydo.core.manager.view.Pick;
import org.caleydo.core.manager.view.PickingManager;
import org.caleydo.core.view.opengl.canvas.remote.IGLCanvasRemoteRendering3D;
import org.caleydo.core.view.opengl.mouse.PickingJoglMouseListener;
import org.caleydo.core.view.opengl.util.GLToolboxRenderer;
import org.caleydo.core.view.opengl.util.JukeboxHierarchyLayer;


/**
 * Abstract class for OpenGL views.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * @author Alexander Lex
 * 
 */
public abstract class AGLCanvasUser 
extends AUniqueManagedObject
implements GLEventListener {
	
	// TODO: should be a list of parent canvas object to be generic
	protected GLCaleydoCanvas parentGLCanvas;
	
	/**
	 * List for all ISet objects providing data for this ViewRep.
	 */
	protected ArrayList <ISet> alSetData;
	
	/**
	 * List for all ISet objects providing data related to interactive selection for this ViewRep.	
	 */
	protected ArrayList <SetSelection> alSetSelection;
	
	protected ISetManager setManager;
	
	protected PickingManager pickingManager;
	protected PickingJoglMouseListener pickingTriggerMouseAdapter;
	
	protected IViewFrustum viewFrustum;
	
	protected IViewCamera viewCamera;
	
	protected GLToolboxRenderer glToolboxRenderer;
			
	protected IGLCanvasRemoteRendering3D remoteRenderingGLCanvas;
	
	/**
	 * Constructor.
	 * 
	 * @param generalManager
	 * @param iViewId
	 * @param iGLCanvasID
	 * @param sLabel
	 */
	protected AGLCanvasUser(final IGeneralManager generalManager,
			final int iViewID, 
			final int iGLCanvasID,
			final String sLabel,
			final IViewFrustum viewFrustum) {
		
		super(iViewID, generalManager);
		
		alSetData = new ArrayList <ISet> ();
		alSetSelection = new ArrayList <SetSelection> ();

		setManager = generalManager.getSetManager();
		
		parentGLCanvas = ((GLCaleydoCanvas)generalManager.getViewGLCanvasManager()
				.getItem(iGLCanvasID));
		
		if (parentGLCanvas != null)
		{
			// Register GL event listener view to GL canvas
			parentGLCanvas.addGLEventListener(this);
			
			generalManager.getViewGLCanvasManager()
				.registerGLEventListenerByGLCanvasID(parentGLCanvas.getID(), this);

			pickingTriggerMouseAdapter = parentGLCanvas.getJoglMouseListener();
		}
		// Frustum will only be remotely rendered by another view
		else
		{
			generalManager.getViewGLCanvasManager()
				.registerGLEventListenerByGLCanvasID(-1, this);
		}

		this.viewFrustum = viewFrustum;

		viewCamera = new ViewCameraBase(iUniqueId); // FIXME: generate own ID for camera
		
		pickingManager = generalManager.getViewGLCanvasManager().getPickingManager();
	}

	/*
	 * (non-Javadoc)
	 * @see javax.media.opengl.GLEventListener#init(javax.media.opengl.GLAutoDrawable)
	 */
	public void init(GLAutoDrawable drawable) {
		
		generalManager.getViewGLCanvasManager().getInfoAreaManager()
			.initInfoOverlay(iUniqueId, drawable);
		
		generalManager.getViewGLCanvasManager().getInfoAreaManager()
			.initInfoInPlace(viewFrustum);
		
		pickingTriggerMouseAdapter.addGLCanvas(this);
		
		((GLEventListener)parentGLCanvas).init(drawable);
		
		initLocal(drawable.getGL());
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.media.opengl.GLEventListener#display(javax.media.opengl.GLAutoDrawable)
	 */
	public void display(GLAutoDrawable drawable) {
		
		((GLEventListener)parentGLCanvas).display(drawable);

		/** Read viewing parameters... */
		final Vec3f rot_Vec3f = new Vec3f();
		final Vec3f position = viewCamera.getCameraPosition();
		final float w = viewCamera.getCameraRotationGrad(rot_Vec3f);
		
		GL gl = drawable.getGL();
		
		/** Translation */
		gl.glTranslatef(position.x(),
				position.y(),
				position.z() );
		
		/** Rotation */		
		gl.glRotatef( w, 
				rot_Vec3f.x(), 
				rot_Vec3f.y(), 
				rot_Vec3f.z());
		
		displayLocal(gl);
		
		generalManager.getViewGLCanvasManager().getInfoAreaManager()
			.renderInfoOverlay(iUniqueId, drawable);
	}

	/*
	 * (non-Javadoc)
	 * @see javax.media.opengl.GLEventListener#displayChanged(javax.media.opengl.GLAutoDrawable, boolean, boolean)
	 */
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
			boolean deviceChanged) {

		((GLEventListener)parentGLCanvas).displayChanged(drawable, modeChanged, deviceChanged);	
	}

	/*
	 * (non-Javadoc)
	 * @see javax.media.opengl.GLEventListener#reshape(javax.media.opengl.GLAutoDrawable, int, int, int, int)
	 */
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {

	    GL gl = drawable.getGL();

//		generalManager.logMsg(
//				"\n---------------------------------------------------"+
//				"\nGLEventListener with ID " +iUniqueId+ " RESHAPE GL" +
//				"\nGL_VENDOR: " + gl.glGetString(GL.GL_VENDOR)+
//				"\nGL_RENDERER: " + gl.glGetString(GL.GL_RENDERER) +
//				"\nGL_VERSION: " + gl.glGetString(GL.GL_VERSION),
//				LoggerType.STATUS);
	    
	    double fAspectRatio = (double) height / (double) width;

	    gl.glViewport(x, y, width, height);
	    gl.glMatrixMode(GL.GL_PROJECTION);
	    gl.glLoadIdentity();
	    
	    if (fAspectRatio < 1.0)
	    {
	    	fAspectRatio = 1.0 / fAspectRatio;
	    	
	    	if (viewFrustum.getProjectionMode().equals(ProjectionMode.ORTHOGRAPHIC))
	    	{
	    		gl.glOrtho(viewFrustum.getLeft() * fAspectRatio, 
	    			viewFrustum.getRight() * fAspectRatio, 
	    			viewFrustum.getBottom(), viewFrustum.getTop(), 
	    			viewFrustum.getNear(), viewFrustum.getFar());
	    	}
	    	else
	    	{
	    		gl.glFrustum(viewFrustum.getLeft() * fAspectRatio, 
		    		viewFrustum.getRight() * fAspectRatio, 
	    			viewFrustum.getBottom(), viewFrustum.getTop(),
		    		viewFrustum.getNear(), viewFrustum.getFar());	    		
	    	}
	    }
	    else 
	    {
	    	if (viewFrustum.getProjectionMode().equals(ProjectionMode.ORTHOGRAPHIC))
	    	{
	    		gl.glOrtho(viewFrustum.getLeft(), viewFrustum.getRight(), 
	    			viewFrustum.getBottom() * fAspectRatio, 
	    			viewFrustum.getTop() * fAspectRatio, 
	    			viewFrustum.getNear(), viewFrustum.getFar());
	    	}
	    	else
	    	{
	    		gl.glFrustum(viewFrustum.getLeft(), viewFrustum.getRight(), 
		    		viewFrustum.getBottom() * fAspectRatio, 
		    		viewFrustum.getTop() * fAspectRatio, 
		    		viewFrustum.getNear(), viewFrustum.getFar());		
	    	}
	    }
	    
	    gl.glMatrixMode(GL.GL_MODELVIEW);
	}
	
	public final ManagerObjectType getBaseType()
	{
		return null;
	}
	
	/**
	 * Initialization for gl, general stuff
	 * @param gl
	 */
	public abstract void init(final GL gl);
	
	/**
	 * Initialization for gl called by the local instance
	 * Has to call init internally!
	 * @param gl
	 */
	public abstract void initLocal(final GL gl);
		
	/**
	 * Initialization for gl called by a managing view
	 * Has to call init internally!
	 * @param gl
	 */
	public abstract void initRemote(final GL gl,
			final int iRemoteViewID, 
			final JukeboxHierarchyLayer layer,
			final PickingJoglMouseListener pickingTriggerMouseAdapter,
			final IGLCanvasRemoteRendering3D remoteRenderingGLCanvas);
	
	/**
	 * GL display method that has to be called in all cases
	 * @param gl
	 */
	public abstract void display(final GL gl);
	
	/**
	 * Intended for internal use when no other view is managing the scene.
	 * Has to call display internally!
	 *
	 * @param gl
	 */
	public abstract void displayLocal(final GL gl);
	
	/**
	 * Intended for external use when another instance of a view manages the scene.
	 * This is specially designed for composite views.
	 * Has to call display internally!
	 * 
	 * @param gl
	 */
	public abstract void displayRemote(final GL gl);
	
	/**
	 * @see org.caleydo.core.view.IView#addSetId(int[])
	 */
	public final void addSetId( int [] iSet) {
		
		assert iSet != null : "Can not handle null-pointer!";
		
		for ( int i=0; i < iSet.length; i++)
		{
			ISet refCurrentSet = setManager.getItemSet(iSet[i]);
			
			if ( refCurrentSet == null ) 
			{
//				generalManager.logMsg(
//						"addSetId(" + iSet[i] + ") is not registered at SetManager!",
//						LoggerType.MINOR_ERROR);
				
				continue;
			}
			
			if ( ! hasSetId_ByReference(refCurrentSet) )
			{
				switch (refCurrentSet.getSetType()) {
				case SET_PATHWAY_DATA:
				case SET_GENE_EXPRESSION_DATA:
				case SET_RAW_DATA:
					alSetData.add(refCurrentSet);
					break;
					
				case SET_SELECTION:
					alSetSelection.add((SetSelection)refCurrentSet);
					break;
					
				default:
//					generalManager.logMsg(
//							"addSetId() unsupported SetType!",
//							LoggerType.ERROR);
				} // switch (refCurrentSet.getSetType()) {
					
			} //if ( ! hasSetId_ByReference(refCurrentSet) )
			else 
			{ 
//				generalManager.logMsg(
//						"addSetId(" + iSet[i] + ") ISet is already registered!",
//						LoggerType.MINOR_ERROR);
			} //if ( ! hasSetId_ByReference(refCurrentSet) ) {...} else {...}
			
		} //for ( int i=0; i < iSet.length; i++)
	}
	
	/**
	 * @see org.caleydo.core.view.IView#removeAllSetIdByType(org.caleydo.core.data.collection.SetType)
	 */
	public final void removeAllSetIdByType( SetType setType ) {
		
		switch (setType) {
		case SET_RAW_DATA:
			alSetData.clear();
			break;
			
		case SET_SELECTION:
			alSetSelection.clear();
			break;
			
		default:
//			generalManager.logMsg(
//					"addSetId() unsupported SetType!",
//					LoggerType.ERROR);
		} // switch (setType) {
	}
	
	/**
	 * @see org.caleydo.core.view.IView#removeSetId(int[])
	 */
	public final void removeSetId( int [] iSet) {
		
		assert iSet != null : "Can not handle null-pointer!";
		
		for ( int i=0; i < iSet.length; i++)
		{
			ISet refCurrentSet = setManager.getItemSet(iSet[i]);
			
			if ( refCurrentSet == null ) 
			{
//				generalManager.logMsg(
//						"removeSetId(" + iSet[i] + ") is not registered at SetManager!",
//						LoggerType.MINOR_ERROR);
				
				continue;
			}
			
			if ( hasSetId_ByReference(refCurrentSet) )
			{
				switch (refCurrentSet.getSetType()) {
				case SET_RAW_DATA:
					alSetData.remove(refCurrentSet);
					break;
					
				case SET_SELECTION:
					alSetSelection.remove(refCurrentSet);
					break;
					
				default:
//					generalManager.logMsg(
//							"removeSetId() unsupported SetType!",
//							LoggerType.ERROR);
				} // switch (refCurrentSet.getSetType()) {
					
			} //if ( ! hasSetId_ByReference(refCurrentSet) )
			else 
			{ 
//				generalManager.logMsg(
//						"removeSetId(" + iSet[i] + ") ISet was not registered!",
//						LoggerType.MINOR_ERROR);
			} //if ( ! hasSetId_ByReference(refCurrentSet) ) {...} else {...}
			
		} //for ( int i=0; i < iSet.length; i++)
		
	}
	

	/**
	 * @see org.caleydo.core.view.IView#getAllSetId()
	 */
	public final synchronized int[] getAllSetId() {
		
		//FIXME: thread safe access to ArrayLists!
		int iTotalSizeResultArray = alSetData.size() + alSetSelection.size();
		
		/* allocate int[] and copy from Arraylist*/
		int [] resultArray = new int [iTotalSizeResultArray];
		
		/* early exit */
		if ( iTotalSizeResultArray == 0) 
		{
			return resultArray;
		}
		
		int i=0;		
		Iterator <ISet> iter = alSetData.iterator();		
		for (;iter.hasNext();i++)
		{
			resultArray[i] = iter.next().getId();
		}
		
		Iterator <SetSelection> iterSelectionSet = alSetSelection.iterator();		
		for (;iterSelectionSet.hasNext();i++)
		{
			resultArray[i] = iterSelectionSet.next().getId();
		}
		
		return resultArray;
	}
	

	/**
	 * @see org.caleydo.core.view.IView#hasSetId(int)
	 */
	public final boolean hasSetId( int iSetId) {
		ISet refCurrentSet = setManager.getItemSet(iSetId);
		
		if ( refCurrentSet == null )
		{
			return false;
		}
		
		return hasSetId_ByReference(refCurrentSet);
	}
	
	
	/**
	 * Test both ArrayList's alSetData and alSetSelection for refSet.
	 * 
	 * @param refSet test if this ISet is referred to
	 * @return TRUE if exists in any of the two ArrayList's
	 */
	public final boolean hasSetId_ByReference(final ISet refSet) {
		
		assert refSet != null : "Can not handle null-pointer";
			
		if ( alSetData.contains(refSet) ) 
		{
			return true;
		}
		if ( alSetSelection.contains(refSet) ) 
		{
			return true;
		}
		
		return false;			
	}
	
	public final GLCaleydoCanvas getParentGLCanvas() {
		
		return parentGLCanvas;
	}
	
	public final IViewFrustum getViewFrustum() {
		
		return viewFrustum;
	}
	
	public void setFrustum(IViewFrustum viewFrustum) {
		
		this.viewFrustum = viewFrustum;
	}

	/**
	 * 
	 * @param gl the gl of the context, remote gl when called remote
	 * @param leftPoint is the bottom left point if bRenderLeftToRight
	 * 			is true, else the top left point
	 * @param layer 
	 * @param bIsCalledLocally true if called locally	  
	 * @param bRenderLeftToRight true if it should be rendered left to right,
	 * 			false if top to bottom
	 */
	public void renderToolbox(final GL gl, 
			final Vec3f leftPoint,
			final JukeboxHierarchyLayer layer,
			final boolean bIsCalledLocally,
			final boolean bRenderLeftToRight)
	{
		
	}
	
	/**
	 * This class uses the pickingManager to check if any events have occured
	 * it calls the abstract handleEvents method where the events should be handled
	 * @param gl
	 */
	protected void checkForHits(final GL gl)
	{	
		// TODO: should only iterate over those relevant to the view - should it?
		for(EPickingType ePickingType : EPickingType.values())
		{		
			ArrayList<Pick> alHits = null;		
		
			alHits = pickingManager.getHits(iUniqueId, ePickingType);		
			if(alHits != null)
			{			
				if (alHits.size() != 0 )
				{				
					for (int iCount = 0; iCount < alHits.size(); iCount++)
					{
						Pick tempPick = alHits.get(iCount);
						int iPickingID = tempPick.getPickingID();
						int iExternalID = pickingManager.getExternalIDFromPickingID(iUniqueId, iPickingID);
						
						//FIXME: Is this ok?
						if(iExternalID == -1)
							continue;
						
						EPickingMode ePickingMode = tempPick.getPickingMode();
						handleEvents(ePickingType, ePickingMode, iExternalID, tempPick);					
					}
				}
			}
		}
	}
	
	/**
	 * This method is called every time a method occurs. It should take care of reacting
	 * appropriately to the events.
	 * 
	 * @param ePickingType the Picking type, held in EPickingType
	 * @param ePickingMode the Picking mode (clicked, dragged etc.)
	 * @param iExternalID  the name specified for an element with glPushName
	 * @param pick		   the pick object which can be useful to retrieve for example
	 * 							the mouse position when the pick occurred
	 */
	abstract protected void handleEvents(final EPickingType ePickingType,
			final EPickingMode ePickingMode,
			final int iExternalID,
			final Pick pick);
	
	public abstract ArrayList<String> getInfo();
	
	public GLToolboxRenderer getToolboxRenderer() 
	{
		return glToolboxRenderer;
	}
	
	public final IViewCamera getViewCamera() {
		
		return viewCamera;
	}	
	
//	public void updataContainedHierarchyLayer(final JukeboxHierarchyLayer layer) 
//	{
//		containedHierarchyLayer = layer;
//	}
	
	public void loadURLInBrowser(final String sUrl) {

		if (sUrl.isEmpty())
			return;

		CmdViewLoadURLInHTMLBrowser createdCmd = (CmdViewLoadURLInHTMLBrowser) generalManager
				.getCommandManager().createCommandByType(CommandQueueSaxType.LOAD_URL_IN_BROWSER);

		createdCmd.setAttributes(sUrl);
		createdCmd.doCommand();
	}
}
