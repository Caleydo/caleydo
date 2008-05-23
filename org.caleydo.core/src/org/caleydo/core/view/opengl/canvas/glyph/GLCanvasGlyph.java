package org.caleydo.core.view.opengl.canvas.glyph;

import gleem.linalg.Vec3f;
import gleem.linalg.open.Vec2i;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;

import javax.media.opengl.GL;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.view.camera.IViewFrustum;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.event.mediator.IMediatorReceiver;
import org.caleydo.core.manager.event.mediator.IMediatorSender;
import org.caleydo.core.manager.view.EPickingMode;
import org.caleydo.core.manager.view.EPickingType;
import org.caleydo.core.manager.view.Pick;
import org.caleydo.core.view.opengl.canvas.AGLCanvasUser;
import org.caleydo.core.view.opengl.canvas.parcoords.GLParCoordsToolboxRenderer;
import org.caleydo.core.view.opengl.canvas.remote.IGLCanvasRemoteRendering3D;
import org.caleydo.core.view.opengl.canvas.remote.glyph.GlyphMouseListener;
import org.caleydo.core.view.opengl.mouse.PickingJoglMouseListener;
import org.caleydo.core.view.opengl.util.GLSharedObjects;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteHierarchyLayer;

/**
 * Rendering the Glyph View
 * 
 * @author Stefan Sauer
 *
 */

public class GLCanvasGlyph 
extends AGLCanvasUser
implements IMediatorSender, IMediatorReceiver
{	
	//private Vec4f vecRotation = new Vec4f(1, 1, 1, 0);
	//private Vec3f vecTranslation = new Vec3f(0,0,0);
	
	GLCanvasGlyphGrid grid_;
	int displayList_ = -1;
	int displayListGrid_ = -1;
	boolean bRedrawDisplayList_ = true;

	private GlyphMouseListener mouseListener_ = null;
	
	/**
	 * 
	 * Constructor.
	 * 
	 * @param generalManager
	 * @param iViewId
	 * @param iGLCanvasID
	 * @param sLabel
	 * @param viewFrustum
	 */
	public GLCanvasGlyph(final IGeneralManager generalManager,
			final int iViewId,
			final int iGLCanvasID,
			final String sLabel,
			final IViewFrustum viewFrustum)
	{
		super(generalManager, iViewId, iGLCanvasID, sLabel, viewFrustum);
		
		mouseListener_ = new GlyphMouseListener(this, generalManager);
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#init(javax.media.opengl.GL)
	 */
	public void init(GL gl) 
	{
//	      gl.glEnable    ( GL.GL_DEPTH_TEST);
	    
		ISet glyphMapping = null;
		ISet glyphData = null;
		
		// Read test data
		// Extract data
		for (ISet tmpSet : alSetData)
		{
			System.out.println(tmpSet.getLabel());
			
			if(tmpSet.getLabel().equals("Set for glyph mapping"))
				glyphMapping = tmpSet;
			
			if(tmpSet.getLabel().equals("Set for clinical data"))
				glyphData = tmpSet;

			
			/*
			IStorage storagePatientID = tmpSet.getStorageByDimAndIndex(0, 0);
			IStorage storageTestInt = tmpSet.getStorageByDimAndIndex(0, 1);
			IStorage storageTestFloat = tmpSet.getStorageByDimAndIndex(0, 2);
			IStorage storageTestText = tmpSet.getStorageByDimAndIndex(0, 3);
			*/
			
			//IStorage storageDiseaseID = tmpSet.getStorageByDimAndIndex(0, 0);
			//IStorage storagePatientID = tmpSet.getStorageByDimAndIndex(0, 1);
			
		}
	
		// Trigger selection update
//		alSetSelection.get(0).getWriteToken();
//		alSetSelection.get(0).updateSelectionSet(iUniqueId, iAlTmpSelectionId, iAlTmpGroupId, null);
//		alSetSelection.get(0).returnWriteToken();

		grid_ = new GLCanvasGlyphGrid();
		grid_.loadGlyphs(gl, glyphMapping, glyphData);
		grid_.buildGrid(gl);
		

		
		
		//init glyph gl lists
		Iterator<GlyphEntry> git = grid_.getGlyphList().values().iterator();
		
		while(git.hasNext()) {
			GlyphEntry e = git.next();
			e.generateGLLists(gl);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#initLocal(javax.media.opengl.GL)
	 */
	public void initLocal(GL gl) 
	{
		init(gl);

		displayListGrid_ = grid_.getGridLayout();
		
		parentGLCanvas.removeMouseWheelListener(pickingTriggerMouseAdapter);
		// Register specialized mouse wheel listener
		parentGLCanvas.addMouseWheelListener(mouseListener_);	
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#initRemote(javax.media.opengl.GL, int, org.caleydo.core.view.opengl.util.JukeboxHierarchyLayer, org.caleydo.core.view.opengl.mouse.PickingJoglMouseListener, org.caleydo.core.view.opengl.canvas.remote.IGLCanvasRemoteRendering3D)
	 */


	public void initRemote(final GL gl, 
			final int iRemoteViewID,
			final RemoteHierarchyLayer layer,
			final PickingJoglMouseListener pickingTriggerMouseAdapter,
			final IGLCanvasRemoteRendering3D remoteRenderingGLCanvas) 

	{
		this.remoteRenderingGLCanvas = remoteRenderingGLCanvas;
	
		glToolboxRenderer = new GLParCoordsToolboxRenderer(gl, generalManager,
				iUniqueId, iRemoteViewID, new Vec3f (0, 0, 0), layer, true, null);
		
		//displayListGrid_ = grid_.getGridLayout();
		init(gl);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#displayLocal(javax.media.opengl.GL)
	 */
	public void displayLocal(GL gl) 
	{
		gl.glTranslatef(0f, 0f, -10f);
		gl.glRotatef( 45f, 1,0,0 );
		gl.glRotatef( 80f, -1,0,0 );
		
		pickingManager.handlePicking(iUniqueId, gl, true);

		display(gl);
		checkForHits(gl);
		
		pickingTriggerMouseAdapter.resetEvents();
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#displayRemote(javax.media.opengl.GL)
	 */
	public void displayRemote(GL gl) 
	{		
		display(gl);
		checkForHits(gl);
//		pickingTriggerMouseAdapter.resetEvents();		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#display(javax.media.opengl.GL)
	 */
	public void display(GL gl) 
	{
		if(grid_ == null)
			return;
		
		
	    gl.glPushMatrix();

	    //rotate grid
		gl.glRotatef( 45f, 0,0,1 );

	    
		
	    gl.glScalef(0.15f, 0.15f, 0.15f);
		
		//draw helplines
		//GLSharedObjects.drawViewFrustum(gl, viewFrustum);
		GLSharedObjects.drawAxis(gl);

		
		if(displayList_ < 0 || bRedrawDisplayList_) {
			gl.glDeleteLists(displayList_,1);

			displayList_ = gl.glGenLists(1);
			gl.glNewList(displayList_, GL.GL_COMPILE);

			if(grid_.getGlyphList() == null) {
				//something is wrong (no data?)
			    gl.glPopMatrix();
				return;
			}
			
			Iterator<GlyphEntry> git = grid_.getGlyphList().values().iterator();
			
			while(git.hasNext()) {
				GlyphEntry e = git.next();
				Vec2i pos = grid_.getGridPosition(e.getX(), e.getY());
				
				gl.glTranslatef( (float)pos.x(), -(float)pos.y(), 0f);
				gl.glPushName(pickingManager.getPickingID(iUniqueId,
						EPickingType.GLYPH_FIELD_SELECTION, e.getID() ) );
				gl.glCallList(e.getGlList(gl));
				gl.glPopName();
				gl.glTranslatef(-(float)pos.x(),  (float)pos.y(), 0f);
			}
			gl.glEndList();
			
			bRedrawDisplayList_ = false;
		}
		

		if(displayListGrid_ >= 0)
			gl.glCallList(displayListGrid_);
		
		if(displayList_ >= 0)
			gl.glCallList(displayList_);
		
	    gl.glPopMatrix();
		
		if(mouseListener_ != null)
			mouseListener_.render();
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#getInfo()
	 */
	public ArrayList<String> getInfo() {

		ArrayList<String> alInfo = new ArrayList<String>();
		alInfo.add("Type: Glyph Map");
		alInfo.add("GL: Showing test clinical data");
		return alInfo;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#handleEvents(org.caleydo.core.manager.view.EPickingType, org.caleydo.core.manager.view.EPickingMode, int, org.caleydo.core.manager.view.Pick)
	 */
	protected void handleEvents(EPickingType pickingType,
			EPickingMode pickingMode, int iExternalID, Pick pick) 
	{
		if(pickingType != EPickingType.GLYPH_FIELD_SELECTION)
			return;
		
		switch(pickingMode) {
			case CLICKED:
				GlyphEntry g = grid_.getGlyph(iExternalID);
				
				int patientid = g.getParameter(1);
				int stagingT = g.getParameter(2);
				
				System.out.println("picked object index: " + Integer.toString(iExternalID) + " on Point " +  g.getX() + " " + g.getY() + " with Patient ID " + patientid + " and T staging " + stagingT );
				
				grid_.deSelectAll();
				g.select();
				
				bRedrawDisplayList_ = true;
				
				
				
				//push patient id to other screens
				
				//alSetSelection.get(0).getWriteToken();
				//alSetSelection.get(0).updateSelectionSet(iUniqueId, iAlTmpSelectionId, iAlTmpGroupId, null);
				//alSetSelection.get(0).returnWriteToken();
				break;
			default:
				//System.out.println("picking Mode " + pickingMode.toString());
					
		}
		
		pickingManager.flushHits(iUniqueId, pickingType);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.event.mediator.IMediatorReceiver#updateReceiver(java.lang.Object)
	 */
	public void updateReceiver(Object eventTrigger) {

		generalManager.getLogger().log(Level.INFO, "Update called by "
				+eventTrigger.getClass().getSimpleName());	
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.event.mediator.IMediatorReceiver#updateReceiver(java.lang.Object, org.caleydo.core.data.collection.ISet)
	 */
	public void updateReceiver(Object eventTrigger, ISet updatedSet) {

		generalManager.getLogger().log(Level.INFO, "Update called by "
				+eventTrigger.getClass().getSimpleName());	
	}
}
