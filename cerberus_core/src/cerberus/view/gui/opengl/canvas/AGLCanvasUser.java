/**
 * 
 */
package cerberus.view.gui.opengl.canvas;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.Iterator;

import javax.media.opengl.GL;

import cerberus.data.collection.ISet;
import cerberus.data.collection.SetType;
import cerberus.data.collection.set.selection.SetSelection;
import cerberus.data.view.camera.IViewCamera;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.manager.data.ISetManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.view.gui.jogl.mouse.AViewCameraListenerObject;
import cerberus.view.gui.opengl.IGLCanvasDirector;
import cerberus.view.gui.opengl.IGLCanvasUser;

/**
 * @author Michael Kalkusch
 *
 */
public abstract class AGLCanvasUser 
extends AViewCameraListenerObject
implements IGLCanvasUser {
	
	protected GL canvas;
	
	protected IGLCanvasDirector openGLCanvasDirector;
	
	private boolean bInitGLcanvawsWasCalled = false;
	
	/**
	 * List for all ISet objects providing data for this ViewRep.
	 */
	protected ArrayList <ISet> alSetData;
	
	/**
	 * List for all ISet objects providing data related to interactive selection for this ViewRep.	
	 */
	protected ArrayList <SetSelection> alSetSelection;
	
	protected ISetManager refSetManager;
	
	/**
	 * @param setGeneralManager
	 */
	protected AGLCanvasUser( final IGeneralManager setGeneralManager,
			final IViewCamera setViewCamera,
			int iViewId, 
			int iParentContainerId, 
			String sLabel )
	{
		super( iViewId, setGeneralManager, setViewCamera);
		
		openGLCanvasDirector =
			setGeneralManager.getSingelton().getViewGLCanvasManager().getGLCanvasDirector( iParentContainerId );
		
		assert openGLCanvasDirector != null : 
			"parent GLCanvas Director is null! Maybe parentID=" + 
			iParentContainerId + " in XML file is invalid.";
		
		//this.canvas = openGLCanvasDirector.getGLCanvas();
		
		//assert canvas != null : "canvas from parten ist null!";
		
		alSetData = new ArrayList <ISet> ();
		alSetSelection = new ArrayList <SetSelection> ();
		
		refSetManager = refGeneralManager.getSingelton().getSetManager();
	}

	public final boolean isInitGLDone() 
	{
		return this.bInitGLcanvawsWasCalled;
	}
	
	public final void setInitGLDone() 
	{
		if ( bInitGLcanvawsWasCalled ) {
			System.err.println(" called setInitGLDone() for more than once! " + 
					this.getClass().getSimpleName()  +
					" " + this.getId());
		}
		else 
		{
			System.err.println(" called setInitGLDone() " + 
					this.getClass().getSimpleName() + 
					" " + this.getId() );
		}
		bInitGLcanvawsWasCalled = true;
	}
	
//	/* (non-Javadoc)
//	 * @see cerberus.view.gui.opengl.IGLCanvasUser#link2GLCanvasDirector(cerberus.view.gui.opengl.IGLCanvasDirector)
//	 */
//	public final void link2GLCanvasDirector(IGLCanvasDirector parentView)
//	{
//		if ( openGLCanvasDirector == null ) {
//			openGLCanvasDirector = parentView;
//		}
//		
//		parentView.addGLCanvasUser( this );
//	}


	/**
	 * Canvas must not be read from outside.
	 * 
	 */
	public final GL getGLCanvas()
	{
		return canvas;
	}

	/**
	 * Canvas must not be set from outside!
	 * 
	 * @param canvas
	 */
	protected final void setGLCanvas(GL canvas)
	{
		assert false : "Canvas must not be set!";
	}
	
	/* (non-Javadoc)
	 * @see cerberus.view.gui.opengl.IGLCanvasUser#getGLCanvasDirector()
	 */
	public final IGLCanvasDirector getGLCanvasDirector()
	{
		return openGLCanvasDirector;
	}

	public final ManagerObjectType getBaseType()
	{
		return null;
	}

	
	/**
	 * @see cerberus.view.gui.IView#addSetId(int[])
	 */
	public final void addSetId( int [] iSet) {
		
		assert iSet != null : "Can not handle null-pointer!";
		
		for ( int i=0; i < iSet.length; i++)
		{
			ISet refCurrentSet = refSetManager.getItemSet(iSet[i]);
			
			if ( refCurrentSet == null ) 
			{
				refGeneralManager.getSingelton().logMsg(
						"addSetId(" + iSet[i] + ") is not registered at SetManager!",
						LoggerType.MINOR_ERROR);
				
				continue;
			}
			
			if ( ! hasSetId_ByReference(refCurrentSet) )
			{
				switch (refCurrentSet.getSetType()) {
				case SET_RAW_DATA:
					alSetData.add(refCurrentSet);
					break;
					
				case SET_SELECTION:
					alSetSelection.add((SetSelection)refCurrentSet);
					break;
					
				default:
					refGeneralManager.getSingelton().logMsg(
							"addSetId() unsupported SetType!",
							LoggerType.ERROR_ONLY);
				} // switch (refCurrentSet.getSetType()) {
					
			} //if ( ! hasSetId_ByReference(refCurrentSet) )
			else 
			{ 
				refGeneralManager.getSingelton().logMsg(
						"addSetId(" + iSet[i] + ") ISet is already registered!",
						LoggerType.MINOR_ERROR);
			} //if ( ! hasSetId_ByReference(refCurrentSet) ) {...} else {...}
			
		} //for ( int i=0; i < iSet.length; i++)
	}
	
	/**
	 * @see cerberus.view.gui.IView#removeAllSetIdByType(cerberus.data.collection.SetType)
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
			refGeneralManager.getSingelton().logMsg(
					"addSetId() unsupported SetType!",
					LoggerType.ERROR_ONLY);
		} // switch (setType) {
	}
	
	/**
	 * @see cerberus.view.gui.IView#removeSetId(int[])
	 */
	public final void removeSetId( int [] iSet) {
		
		assert iSet != null : "Can not handle null-pointer!";
		
		for ( int i=0; i < iSet.length; i++)
		{
			ISet refCurrentSet = refSetManager.getItemSet(iSet[i]);
			
			if ( refCurrentSet == null ) 
			{
				refGeneralManager.getSingelton().logMsg(
						"removeSetId(" + iSet[i] + ") is not registered at SetManager!",
						LoggerType.MINOR_ERROR);
				
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
					refGeneralManager.getSingelton().logMsg(
							"removeSetId() unsupported SetType!",
							LoggerType.ERROR_ONLY);
				} // switch (refCurrentSet.getSetType()) {
					
			} //if ( ! hasSetId_ByReference(refCurrentSet) )
			else 
			{ 
				refGeneralManager.getSingelton().logMsg(
						"removeSetId(" + iSet[i] + ") ISet was not registered!",
						LoggerType.MINOR_ERROR);
			} //if ( ! hasSetId_ByReference(refCurrentSet) ) {...} else {...}
			
		} //for ( int i=0; i < iSet.length; i++)
		
	}
	

	/**
	 * @see cerberus.view.gui.IView#getAllSetId()
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
	 * @see cerberus.view.gui.IView#hasSetId(int)
	 */
	public final boolean hasSetId( int iSetId) {
		ISet refCurrentSet = refSetManager.getItemSet(iSetId);
		
		if ( refCurrentSet == null )
		{
			return false;
		}
		
		return hasSetId_ByReference(refCurrentSet);
	}
	
	
	/**
	 * Test both ArrayList's alSetData and alSetSelection for refSet.
	 * 
	 * @param refSet test if this ISet is refered to
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
	


	
	
	/**
	 * @see cerberus.view.gui.opengl.IGLCanvasUser#initGLCanvas(javax.media.opengl.GLCanvas)
	 */
	public void initGLCanvas(GL gl)
	{
		setInitGLDone();		
	}

	
	/**
	 * @see cerberus.view.gui.opengl.IGLCanvasUser#render(javax.media.opengl.GLAutoDrawable)
	 */
	public final void render( GL gl)
	{		
		/** Read viewing parameters... */
		final Vec3f rot_Vec3f = new Vec3f();
		final Vec3f position = refViewCamera.getCameraPosition();
		final float w = refViewCamera.getCameraRotationGrad(rot_Vec3f);
		
	
		/** Translation */
		gl.glTranslatef(position.x(),
				position.y(),
				position.z() );
		
		
		/** Rotation */		
		gl.glRotatef( w, 
				rot_Vec3f.x(), 
				rot_Vec3f.y(), 
				rot_Vec3f.z());
		
		// isInitGLDone() == bInitGLcanvawsWasCalled 
		if  ( ! bInitGLcanvawsWasCalled ) {
			initGLCanvas(gl);
			System.err.println("INIT CALLED IN RENDER METHOD of " +this.getClass().getSimpleName());
		}
		
		this.renderPart( gl );
	}
	
	/**
	 * @see cerberus.view.gui.opengl.IGLCanvasUser#reshape(javax.media.opengl.GLAutoDrawable, int, int, int, int)
	 */
	public void reshape(GL gl, 
			final int x,
			final int y,
			final int width,
			final int height) {
		
		System.out.println(" AGLCanvasUser_OriginRotation.reshape(GLCanvas canvas)");
		
		this.renderPart( gl );
	}
	

	/**
	 * @see cerberus.view.gui.opengl.IGLCanvasUser#destroyGLCanvas()
	 */
	public void destroyGLCanvas() {
		
	}

	/**
	 * @see cerberus.view.gui.opengl.IGLCanvasUser#displayChanged(javax.media.opengl.GL, boolean, boolean)
	 */
	public final void displayChanged(GL gl, 
			boolean modeChanged, 
			boolean deviceChanged) {
		
		this.render( gl );		
	}

	/**
	 * @see cerberus.view.gui.opengl.IGLCanvasUser#update(javax.media.opengl.GL)
	 */
	public void update(GL gl) {
		this.render( gl );	
	}
	
	/*
	 * Forwards render(GL) to derived class.
	 * 
	 * @see cerberus.view.gui.opengl.canvas.AGLCanvasUser#render(GL)
	 * 
	 * @param gl canvas created from GLAutoDrawable
	 */
	public abstract void renderPart(GL gl);
}
