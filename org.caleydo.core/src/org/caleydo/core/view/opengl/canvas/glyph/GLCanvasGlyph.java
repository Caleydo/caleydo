package org.caleydo.core.view.opengl.canvas.glyph;

import gleem.linalg.Vec3f;
import gleem.linalg.open.Vec2i;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;

import javax.media.opengl.GL;
import javax.media.opengl.GLCanvas;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.set.selection.ISetSelection;
import org.caleydo.core.data.collection.set.selection.SetSelection;
import org.caleydo.core.data.view.camera.IViewFrustum;
import org.caleydo.core.data.view.rep.renderstyle.GlyphRenderStyle;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.event.mediator.IMediatorReceiver;
import org.caleydo.core.manager.event.mediator.IMediatorSender;
import org.caleydo.core.manager.specialized.glyph.EGlyphSettingIDs;
import org.caleydo.core.manager.specialized.glyph.IGlyphManager;
import org.caleydo.core.manager.view.EPickingMode;
import org.caleydo.core.manager.view.EPickingType;
import org.caleydo.core.manager.view.Pick;
import org.caleydo.core.view.opengl.canvas.AGLCanvasUser;
import org.caleydo.core.view.opengl.canvas.remote.IGLCanvasRemoteRendering3D;
import org.caleydo.core.view.opengl.mouse.PickingJoglMouseListener;
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
	
	GLCanvasGlyphGrid grid_;
	int displayList_ = -1;
	int displayListButtons_ = -1;
	boolean bRedrawDisplayList_ = true;
	boolean bIsLocal = false;
	
	

	private GlyphMouseListener mouseListener_ = null;
	private GlyphKeyListener keyListener_ = null;
	private GlyphRenderStyle renderStyle = null;
	
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
		super(generalManager, iViewId, iGLCanvasID, sLabel, viewFrustum, true);
		
		mouseListener_ = new GlyphMouseListener(this, generalManager);
		keyListener_ = new GlyphKeyListener();
		renderStyle = new GlyphRenderStyle(viewFrustum);
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#init(javax.media.opengl.GL)
	 */
	public void init(GL gl) 
	{
		
		glToolboxRenderer = new GLGlyphToolboxRenderer(gl, generalManager, iUniqueId, new Vec3f (0, 0, 0), true, renderStyle );
		
		ISet glyphData = null;
		
		for (ISet tmpSet : alSetData)
		{
			if(tmpSet.getLabel().equals("Set for clinical data"))
				glyphData = tmpSet;
		}
		
		if(glyphData == null)
		{
			generalManager.getLogger().log(Level.SEVERE, "no glyph data found - shutting down" );
			return;
		}

		grid_ = new GLCanvasGlyphGrid(generalManager);
		grid_.loadData(glyphData);
		
		grid_.buildGrid(gl);
		
		grid_.buildScatterplotGrid(gl);
		
		
		//grid_.setGridSize(30, 60);
		//grid_.setGlyphPositions(EIconIDs.DISPLAY_SCATTERPLOT.ordinal());

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
		bIsLocal = true;
		
		init(gl);
		
		//parentGLCanvas.removeMouseWheelListener(pickingTriggerMouseAdapter);
		// Register specialized mouse wheel listener
		parentGLCanvas.addMouseWheelListener(mouseListener_);
		//parentGLCanvas.addMouseListener(mouseListener_);
		
		//parentGLCanvas.addMouseMotionListener(mouseListener_);
		
		parentGLCanvas.addKeyListener(keyListener_);
		
		//parentGLCanvas.removeMouseMotionListener( parentGLCanvas.getJoglMouseListener() );
		//parentGLCanvas.addMouseMotionListener(mouseListener_);
		
		
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
		bIsLocal = false;
		this.remoteRenderingGLCanvas = remoteRenderingGLCanvas;

		Collection<GLCanvas> cc = generalManager.getViewGLCanvasManager().getAllGLCanvasUsers();
		
		for(GLCanvas c : cc) {
			//System.out.println("canvas name:" + c.getName() );
			c.addKeyListener(keyListener_);
		}

		init(gl);
		
		grid_.setGridSize(30, 60);
		grid_.setGlyphPositions(EIconIDs.DISPLAY_SCATTERPLOT.ordinal());
		//this.grid_.setGlyphPositions(EIconIDs.DISPLAY_RANDOM.ordinal());
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#displayLocal(javax.media.opengl.GL)
	 */
	public void displayLocal(GL gl) 
	{
		//GLSharedObjects.drawAxis(gl);
		
		gl.glTranslatef(0f, 0f, -10f);
		gl.glRotatef( 45f, 1,0,0 );
		//gl.glRotatef( 45f, -1,0,0 ); 
		gl.glRotatef( 80f, -1,0,0 ); //35�
		
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
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#display(javax.media.opengl.GL)
	 */
	public void display(GL gl) 
	{
		if(grid_ == null)
			return;

		if(bRedrawDisplayList_) { //redraw glyphs
			gl.glPushMatrix();
			Iterator<GlyphEntry> git = grid_.getGlyphList().values().iterator();
		
			while(git.hasNext()) {
				GlyphEntry e = git.next();
				e.generateGLLists(gl);
			}
			gl.glPopMatrix();
		}
		
	    gl.glPushMatrix();


		//rotate grid
		gl.glRotatef( 45f, 0,0,1 );
    
		
	    gl.glScalef(0.15f, 0.15f, 0.15f);
		

	    //draw helplines
		//GLSharedObjects.drawViewFrustum(gl, viewFrustum);
		//GLSharedObjects.drawAxis(gl);

		gl.glTranslatef(8, 0, 0f);

		
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
				if(pos == null) continue;
				
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
		

		int displayListGrid = grid_.getGridLayout(bIsLocal);
		if(displayListGrid >= 0)
			gl.glCallList(displayListGrid);
		
		if(displayList_ >= 0)
			gl.glCallList(displayList_);
		
		gl.glTranslatef( -7.0f, 0.0f, 0f);
		gl.glRotatef( -45f, 0,0,1 );
		
		if(glToolboxRenderer != null)
			glToolboxRenderer.render(gl);


		if(mouseListener_ != null)
			mouseListener_.render(gl);

		gl.glPopMatrix();
		
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
		IGlyphManager gm = generalManager.getGlyphManager();

		if(pickingType == EPickingType.GLYPH_FIELD_SELECTION) {
			switch(pickingMode) {
				case CLICKED:
					
					GlyphEntry g = grid_.getGlyph(iExternalID);
				
					if( g == null ) {
						generalManager.getLogger().log(Level.WARNING, "Glyph with external ID " +iExternalID + " not found!" );
						pickingManager.flushHits(iUniqueId, pickingType);
						return;
					}
					
					//int sendParameter = this.grid_.getDataLoader().getSendParameter();
					int sendParameter = Integer.parseInt(generalManager.getGlyphManager().getSetting(EGlyphSettingIDs.UPDATESENDPARAMETER));
					int sendID = g.getParameter(sendParameter);
					
					//create selection lists for other screens
					ArrayList<Integer> ids = new ArrayList<Integer>();
					ArrayList<Integer> selections = new ArrayList<Integer>();

					
					if(!keyListener_.isControlDown()) {
						ArrayList<Integer> deselect = grid_.deSelectAll();
						ids.addAll(deselect);
						while(selections.size() < ids.size())
							selections.add(-1);
					}
					
					
					
					
					//test output only....
					int paramt   = gm.getGlyphAttributeTypeWithExternalColumnNumber(2).getInternalColumnNumber();
					int paramdfs = gm.getGlyphAttributeTypeWithExternalColumnNumber(9).getInternalColumnNumber();
					
					int stagingT = g.getParameter(paramt);
					int dfs = g.getParameter(paramdfs);

					if(!g.isSelected()) {
						System.out.println("  select object index: " + Integer.toString(iExternalID) + " on Point " +  g.getX() + " " + g.getY() + " with Patient ID " + sendID + " and T staging " + stagingT + " and dfs " + dfs );
						g.select();
						
						ids.add(sendID);
						selections.add(1);
					} else {
						System.out.println("DEselect object index: " + Integer.toString(iExternalID) + " on Point " +  g.getX() + " " + g.getY() + " with Patient ID " + sendID + " and T staging " + stagingT + " and dfs " + dfs );
						g.deSelect();
						
						
						
						ids.add(sendID);
						selections.add(-1);
					}
				
					if(grid_.getNumOfSelected() == 0) {
						ArrayList<Integer> select = grid_.selectAll();
						ids.addAll(select);
						while(selections.size() < ids.size())
							selections.add(1);
					}
					
					generalManager.getGlyphManager().getGlyphAttributeTypeWithExternalColumnNumber(2).printDistribution();
					bRedrawDisplayList_ = true;
					
					//push patient id to other screens
					for(SetSelection sel : alSetSelection) {
						sel.getWriteToken();
						sel.updateSelectionSet(iUniqueId, ids, selections, null);
						sel.returnWriteToken();
					}
					
					break;
				default:
					//System.out.println("picking Mode " + pickingMode.toString());
					
			}
		}
		
		if(pickingType == EPickingType.PC_ICON_SELECTION) {
			switch(pickingMode) {
				case CLICKED:
					
					this.grid_.setGlyphPositions(iExternalID);
					//System.out.println("ICON id = " + iExternalID);
					bRedrawDisplayList_ = true;
					break;
				default:
			}
			
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
		
		ISetSelection refSetSelection = (ISetSelection) updatedSet;

		refSetSelection.getReadToken();
		ArrayList<Integer> iAlSelection = refSetSelection.getSelectionIdArray();
		ArrayList<Integer> iAlSelectionMode = refSetSelection.getGroupArray();
		
		//int sendParameter = this.grid_.getDataLoader().getSendParameter();
		int sendParameter = Integer.parseInt(generalManager.getGlyphManager().getSetting(EGlyphSettingIDs.UPDATESENDPARAMETER));
		
		if (iAlSelection.size() != 0) {
			for(int i=0;i<iAlSelection.size();++i) {
				int sid = iAlSelection.get(i);
				int gid = iAlSelectionMode.get(i);
			
				Iterator<GlyphEntry> git = grid_.getGlyphList().values().iterator();
				
				while (git.hasNext()) {
					GlyphEntry g = git.next();
					if(g.getParameter(sendParameter) == sid)
						if(gid == 1)
							g.select();
						else
							g.deSelect();
				}
				bRedrawDisplayList_ = true;
			}
		}
		updatedSet.returnReadToken();
		
	}
}
