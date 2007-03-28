/**
 * 
 */
package cerberus.view.gui.opengl.canvas;

import java.util.ArrayList;
import java.util.Iterator;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;

import cerberus.data.AUniqueManagedObject;
import cerberus.data.collection.ISet;
import cerberus.data.collection.SetType;
import cerberus.data.collection.set.selection.SetSelection;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.manager.data.ISetManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.view.gui.opengl.IGLCanvasDirector;
import cerberus.view.gui.opengl.IGLCanvasUser;

/**
 * @author Michael Kalkusch
 *
 */
public abstract class AGLCanvasUser 
extends AUniqueManagedObject 
implements IGLCanvasUser {
	
	protected GLAutoDrawable canvas;
	
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
			int iViewId, 
			int iParentContainerId, 
			String sLabel )
	{
		super( iViewId, setGeneralManager );
		
		openGLCanvasDirector =
			setGeneralManager.getSingelton().getViewGLCanvasManager().getGLCanvasDirector( iParentContainerId );
		
		assert openGLCanvasDirector != null : 
			"parent GLCanvas Director is null! Maybe parentID=" + 
			iParentContainerId + " in XML file is invalid.";
		
		this.canvas = openGLCanvasDirector.getGLCanvas();
		
		assert canvas != null : "canvas from parten ist null!";
		
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


	/* (non-Javadoc)
	 * @see cerberus.view.gui.opengl.IGLCanvasUser#getGLCanvas()
	 */
	public final GLAutoDrawable getGLCanvas()
	{
		return canvas;
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

	public void initGLCanvas(GLCanvas canvas)
	{
		setInitGLDone();
	}

	
	public abstract void renderPart(GL gl);
	
	
	public final void render(GLAutoDrawable canvas)
	{
		GL gl = canvas.getGL();
		
//		/* Clear The Screen And The Depth Buffer */
//		gl.glPushMatrix();

//		gl.glTranslatef( origin.x(), origin.y(), origin.z() );
//		gl.glRotatef( rotation.x(), 
//				rotation.y(),
//				rotation.z(),
//				rotation.w() );
		
		// isInitGLDone() == bInitGLcanvawsWasCalled 
		if  ( ! bInitGLcanvawsWasCalled ) {
			initGLCanvas( (GLCanvas) canvas);
			System.err.println("INIT CALLED IN RENDER METHOD of " +this.getClass().getSimpleName());
		}
		
		this.renderPart( gl );

//		gl.glPopMatrix();		
//		
		//System.err.println(" TestTriangle.render(GLCanvas canvas)");
	}
	
	public void reshape(GLAutoDrawable drawable, 
			final int x,
			final int y,
			final int width,
			final int height) {
		
		GL gl = drawable.getGL();
		
		System.out.println(" AGLCanvasUser_OriginRotation.reshape(GLCanvas canvas)");
		
		this.renderPart( gl );
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
}
