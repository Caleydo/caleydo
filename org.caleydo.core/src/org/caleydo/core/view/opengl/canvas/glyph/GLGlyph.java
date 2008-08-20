package org.caleydo.core.view.opengl.canvas.glyph;

import gleem.linalg.Vec3f;
import gleem.linalg.open.Vec2i;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import javax.media.opengl.GL;
import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.data.collection.INominalStorage;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.collection.storage.ERawDataType;
import org.caleydo.core.data.collection.storage.NominalStorage;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.GenericSelectionManager;
import org.caleydo.core.data.selection.ISelectionDelta;
import org.caleydo.core.data.view.camera.IViewFrustum;
import org.caleydo.core.data.view.rep.renderstyle.GlyphRenderStyle;
import org.caleydo.core.manager.event.mediator.IMediatorReceiver;
import org.caleydo.core.manager.event.mediator.IMediatorSender;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.manager.specialized.glyph.EGlyphSettingIDs;
import org.caleydo.core.manager.specialized.glyph.GlyphManager;
import org.caleydo.core.manager.specialized.glyph.IGlyphManager;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.remote.IGLCanvasRemoteRendering3D;
import org.caleydo.core.view.opengl.mouse.PickingJoglMouseListener;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteHierarchyLayer;

/**
 * Rendering the Glyph View
 * 
 * @author Stefan Sauer
 */
public class GLGlyph
	extends AGLEventListener
	implements IMediatorSender, IMediatorReceiver
{

	private static final long serialVersionUID = -7899479912218913482L;

	GLGlyphGrid grid_;

	int displayList_ = -1;

	int displayListButtons_ = -1;

	boolean bRedrawDisplayList_ = true;

	boolean bIsLocal = false;

	private GlyphMouseListener mouseListener_ = null;

	private GlyphKeyListener keyListener_ = null;

	private GlyphRenderStyle renderStyle = null;

	private GlyphManager gman = null;

	private GenericSelectionManager selectionManager = null;

	/**
	 * Constructor.
	 * 
	 * @param iViewID
	 * @param iGLCanvasID
	 * @param sLabel
	 * @param viewFrustum
	 */
	public GLGlyph(final int iGLCanvasID, final String sLabel,
			final IViewFrustum viewFrustum)
	{
		super(iGLCanvasID, sLabel, viewFrustum, true);

		mouseListener_ = new GlyphMouseListener(this, generalManager);
		keyListener_ = new GlyphKeyListener();
		renderStyle = new GlyphRenderStyle(viewFrustum);

		gman = (GlyphManager) generalManager.getGlyphManager();
		gman.registerGlyphView(this);

		selectionManager = new GenericSelectionManager.Builder(EIDType.CLINICAL_ID).build();
	}

	@Override
	public void init(GL gl)
	{

		glToolboxRenderer = new GLGlyphToolboxRenderer(gl, generalManager, iUniqueID,
				new Vec3f(0, 0, 0), true, renderStyle);

		ISet glyphData = null;

		for (ISet tmpSet : alSets)
		{
			if (tmpSet != null)
			{
				if (tmpSet.getLabel().equals("Set for clinical data"))
					glyphData = tmpSet;
			}
		}

		if (glyphData == null)
		{
			generalManager.getLogger()
					.log(Level.SEVERE, "no glyph data found - shutting down");
			return;
		}

		// load ids to selectionManager
		selectionManager.resetSelectionManager();
		try
		{
			IStorage store = glyphData.get(0);
			if (store instanceof NominalStorage
					&& store.getRawDataType() == ERawDataType.STRING)
			{
				INominalStorage<String> nominalStorage = (INominalStorage<String>) store;
				for (int i = 0; i < nominalStorage.size(); ++i)
				{
					int id = Integer.parseInt(nominalStorage.getRaw(i));
					selectionManager.initialAdd(id);
				}
			}
		}
		catch (NumberFormatException e)
		{
			generalManager.getLogger().log(Level.SEVERE,
					"first glyph data row isn't the index - shutting down");
			return;
		}

		grid_ = new GLGlyphGrid(generalManager, renderStyle);
		grid_.loadData(glyphData);

		grid_.buildGrid(gl);

		grid_.buildScatterplotGrid(gl);

		grid_.selectAll();

		// grid_.setGridSize(30, 60);
		// grid_.setGlyphPositions(EIconIDs.DISPLAY_SCATTERPLOT.ordinal());

		// init glyph gl
		bRedrawDisplayList_ = true;
	}

	@Override
	public void initLocal(GL gl)
	{

		bIsLocal = true;

		init(gl);

		// parentGLCanvas.removeMouseWheelListener(pickingTriggerMouseAdapter);
		// Register specialized mouse wheel listener
		parentGLCanvas.addMouseWheelListener(mouseListener_);
		// parentGLCanvas.addMouseListener(mouseListener_);

		// parentGLCanvas.addMouseMotionListener(mouseListener_);

		parentGLCanvas.addKeyListener(keyListener_);

		// parentGLCanvas.removeMouseMotionListener(
		// parentGLCanvas.getJoglMouseListener() );
		// parentGLCanvas.addMouseMotionListener(mouseListener_);

	}

	@Override
	public void initRemote(final GL gl, final int iRemoteViewID,
			final RemoteHierarchyLayer layer,
			final PickingJoglMouseListener pickingTriggerMouseAdapter,
			final IGLCanvasRemoteRendering3D remoteRenderingGLCanvas)

	{

		bIsLocal = false;
		this.remoteRenderingGLCanvas = remoteRenderingGLCanvas;

		Collection<GLCaleydoCanvas> cc = generalManager.getViewGLCanvasManager()
				.getAllGLCanvasUsers();

		for (GLCaleydoCanvas c : cc)
		{
			// System.out.println("canvas name:" + c.getName() );
			c.addKeyListener(keyListener_);
		}

		init(gl);

		grid_.setGridSize(30, 60);
		grid_.setGlyphPositions(EIconIDs.DISPLAY_SCATTERPLOT.ordinal());
		// this.grid_.setGlyphPositions(EIconIDs.DISPLAY_RANDOM.ordinal());
	}

	@Override
	public void displayLocal(GL gl)
	{

		// GLHelperFunctions.drawAxis(gl);

		gl.glTranslatef(0f, 0f, -10f);
		gl.glRotatef(45f, 1, 0, 0);
		// gl.glRotatef( 45f, -1,0,0 );
		gl.glRotatef(80f, -1, 0, 0); // 35

		pickingManager.handlePicking(iUniqueID, gl, true);

		display(gl);
		checkForHits(gl);

		pickingTriggerMouseAdapter.resetEvents();

	}

	@Override
	public void displayRemote(GL gl)
	{

		display(gl);
		checkForHits(gl);
	}

	@Override
	public void display(GL gl)
	{

		if (grid_ == null)
			return;

		if (bRedrawDisplayList_)
		{ // redraw glyphs
			gl.glPushMatrix();
			Iterator<GlyphEntry> git = grid_.getGlyphList().values().iterator();

			while (git.hasNext())
			{
				GlyphEntry e = git.next();
				e.generateGLLists(gl);
			}
			gl.glPopMatrix();

			gl.glPushMatrix();
			grid_.buildScatterplotGrid(gl);
			grid_.setGlyphPositions();
			gl.glPopMatrix();
		}

		gl.glPushMatrix();

		// rotate grid
		gl.glRotatef(45f, 0, 0, 1);

		gl.glScalef(0.15f, 0.15f, 0.15f);

		// draw helplines
		// GLHelperFunctions.drawViewFrustum(gl, viewFrustum);
		// GLHelperFunctions.drawAxis(gl);

		gl.glTranslatef(8, 0, 0f);

		if (displayList_ < 0 || bRedrawDisplayList_)
		{
			gl.glDeleteLists(displayList_, 1);

			displayList_ = gl.glGenLists(1);
			gl.glNewList(displayList_, GL.GL_COMPILE);

			if (grid_.getGlyphList() == null)
			{
				// something is wrong (no data?)
				gl.glPopMatrix();
				return;
			}

			Iterator<GlyphEntry> git = grid_.getGlyphList().values().iterator();

			while (git.hasNext())
			{
				GlyphEntry e = git.next();

				Vec2i pos = grid_.getGridPosition(e.getX(), e.getY());
				if (pos == null)
					continue;

				gl.glTranslatef((float) pos.x(), -(float) pos.y(), 0f);
				gl.glPushName(pickingManager.getPickingID(iUniqueID,
						EPickingType.GLYPH_FIELD_SELECTION, e.getID()));
				gl.glCallList(e.getGlList(gl));
				gl.glPopName();
				gl.glTranslatef(-(float) pos.x(), (float) pos.y(), 0f);
			}
			gl.glEndList();

			bRedrawDisplayList_ = false;
		}

		int displayListGrid = grid_.getGridLayout(bIsLocal);
		if (displayListGrid >= 0)
			gl.glCallList(displayListGrid);

		if (displayList_ >= 0)
			gl.glCallList(displayList_);

		gl.glTranslatef(-7.0f, 0.0f, 0f);
		gl.glRotatef(-45f, 0, 0, 1);

		if (glToolboxRenderer != null)
			glToolboxRenderer.render(gl);

		if (mouseListener_ != null)
			mouseListener_.render(gl);

		gl.glPopMatrix();

	}

	@Override
	public ArrayList<String> getInfo()
	{

		ArrayList<String> alInfo = new ArrayList<String>();
		alInfo.add("Type: Glyph Map");
		alInfo.add("GL: Showing test clinical data");
		return alInfo;
	}

	@Override
	protected void handleEvents(EPickingType pickingType, EPickingMode pickingMode,
			int iExternalID, Pick pick)
	{

		IGlyphManager gm = generalManager.getGlyphManager();

		if (pickingType == EPickingType.GLYPH_FIELD_SELECTION)
		{
			switch (pickingMode)
			{
				case CLICKED:

					GlyphEntry g = grid_.getGlyph(iExternalID);

					if (g == null)
					{
						generalManager.getLogger().log(Level.WARNING,
								"Glyph with external ID " + iExternalID + " not found!");
						pickingManager.flushHits(iUniqueID, pickingType);
						return;
					}

					// int sendParameter =
					// this.grid_.getDataLoader().getSendParameter();
					int sendParameter = Integer.parseInt(generalManager.getGlyphManager()
							.getSetting(EGlyphSettingIDs.UPDATESENDPARAMETER));
					int sendID = g.getParameter(sendParameter);

					// create selection lists for other screens
					ArrayList<Integer> ids = new ArrayList<Integer>();
					ArrayList<Integer> selections = new ArrayList<Integer>();

					if (!keyListener_.isControlDown())
					{
						ArrayList<Integer> deselect = grid_.deSelectAll();
						ids.addAll(deselect);
						while (selections.size() < ids.size())
							selections.add(-1);
					}

					// test output only....
					int paramt = gm.getGlyphAttributeTypeWithExternalColumnNumber(2)
							.getInternalColumnNumber();
					int paramdfs = gm.getGlyphAttributeTypeWithExternalColumnNumber(9)
							.getInternalColumnNumber();

					int stagingT = g.getParameter(paramt);
					int dfs = g.getParameter(paramdfs);

					if (!g.isSelected())
					{
						System.out.println("  select object index: "
								+ Integer.toString(iExternalID) + " on Point " + g.getX()
								+ " " + g.getY() + " with Patient ID " + sendID
								+ " and T staging " + stagingT + " and dfs " + dfs);
						g.select();

						ids.add(sendID);
						selections.add(1);
					}
					else
					{
						System.out.println("DEselect object index: "
								+ Integer.toString(iExternalID) + " on Point " + g.getX()
								+ " " + g.getY() + " with Patient ID " + sendID
								+ " and T staging " + stagingT + " and dfs " + dfs);
						g.deSelect();

						ids.add(sendID);
						selections.add(-1);
					}

					if (grid_.getNumOfSelected() == 0)
					{
						ArrayList<Integer> select = grid_.selectAll();
						ids.addAll(select);
						while (selections.size() < ids.size())
							selections.add(1);
					}

					generalManager.getGlyphManager()
							.getGlyphAttributeTypeWithExternalColumnNumber(2)
							.printDistribution();
					bRedrawDisplayList_ = true;

					// push patient id to other screens
					// TODO rewrite this with new selection
					// for (Selection sel : alSelection)
					// {
					// sel.updateSelectionSet(iUniqueID, ids, selections, null);
					// }

					break;
				default:
					// System.out.println("picking Mode " +
					// pickingMode.toString());

			}
		}

		if (pickingType == EPickingType.PC_ICON_SELECTION)
		{
			switch (pickingMode)
			{
				case CLICKED:

					this.grid_.setGlyphPositions(iExternalID);
					// System.out.println("ICON id = " + iExternalID);
					bRedrawDisplayList_ = true;
					break;
				default:
			}

		}

		pickingManager.flushHits(iUniqueID, pickingType);
	}

	@Override
	public void handleUpdate(IUniqueObject eventTrigger)
	{

		generalManager.getLogger().log(Level.INFO,
				"Update called by " + eventTrigger.getClass().getSimpleName());
	}

	@Override
	public void handleUpdate(IUniqueObject eventTrigger, ISelectionDelta selectionDelta)
	{

		generalManager.getLogger().log(Level.INFO,
				"Update called by " + eventTrigger.getClass().getSimpleName());

		selectionManager.clearSelections();
		selectionManager.setDelta(selectionDelta);

		int sendParameter = Integer.parseInt(gman
				.getSetting(EGlyphSettingIDs.UPDATESENDPARAMETER));

		HashMap<Integer, GlyphEntry> glyphmap = new HashMap<Integer, GlyphEntry>();
		for (GlyphEntry g : grid_.getGlyphList().values())
			glyphmap.put(g.getParameter(sendParameter), g);

		{
			java.util.Set<Integer> selected = selectionManager
					.getElements(ESelectionType.SELECTION);
			java.util.Set<Integer> unselected = selectionManager
					.getElements(ESelectionType.DESELECTED);

			int counter = 0;

			for (int id : selected)
			{
				GlyphEntry g = glyphmap.get(id);
				if (g != null)
				{
					g.select();
					counter++;
				}
			}

			for (int id : unselected)
			{
				GlyphEntry g = glyphmap.get(id);
				if (g != null)
				{
					g.deSelect();
					counter++;
				}
			}
			System.out.println("changed selection of " + counter + " glyphs");

			bRedrawDisplayList_ = true;
		}

		// TODO rewrite this with new mechanisms
		// ArrayList<Integer> iAlSelection =
		// refSetSelection.getSelectionIdArray();
		// ArrayList<Integer> iAlSelectionMode =
		// refSetSelection.getGroupArray();
		// ArrayList<Integer> iAlSelectionOptional =
		// refSetSelection.getOptionalDataArray();
		//
		// if (eventTrigger.getClass() == GLCanvasGlyphSliderView.class)
		// {
		// if (iAlSelection.size() <= 0)
		// return;
		//
		// HashMap<Integer, HashMap<Integer, Boolean>> parameterValueSelectedMap
		// = new HashMap<Integer, HashMap<Integer, Boolean>>();
		// for (int i = 0; i < iAlSelection.size(); ++i)
		// {
		// int internalindex = iAlSelection.get(i);
		// boolean isselected = (iAlSelectionMode.get(i) == 1) ? true : false;
		// if (!parameterValueSelectedMap.containsKey(internalindex))
		// parameterValueSelectedMap.put(internalindex,
		// new HashMap<Integer, Boolean>());
		//
		//parameterValueSelectedMap.get(internalindex).put(iAlSelectionOptional.
		// get(i),
		// isselected);
		// }
		//
		// grid_.deSelectAll();
		//
		// HashMap<Integer, GlyphEntry> glyphs = grid_.getGlyphList();
		//
		// Iterator<GlyphEntry> it = glyphs.values().iterator();
		// while (it.hasNext())
		// {
		// GlyphEntry g = it.next();
		//
		// boolean isselected = true;
		// for (int internalindex : parameterValueSelectedMap.keySet())
		// {
		// int param = g.getParameter(internalindex);
		// HashMap<Integer, Boolean> p =
		// parameterValueSelectedMap.get(internalindex);
		//
		// if (p.containsKey(param))
		// {
		// if (!p.get(param))
		// // g.select();
		// // else
		// isselected = false;
		//
		// }
		// else
		// { // not in the list
		// isselected = false;
		// }
		//
		// }
		// if (isselected)
		// g.select();
		//
		// }
		//
		// bRedrawDisplayList_ = true;
		// }
		// else
		// {
		// int sendParameter =
		// Integer.parseInt(generalManager.getGlyphManager().getSetting(
		// EGlyphSettingIDs.UPDATESENDPARAMETER));
		//
		// if (iAlSelection.size() != 0)
		// {
		// for (int i = 0; i < iAlSelection.size(); ++i)
		// {
		// int sid = iAlSelection.get(i);
		// int gid = iAlSelectionMode.get(i);
		//
		// Iterator<GlyphEntry> git = grid_.getGlyphList().values().iterator();
		//
		// while (git.hasNext())
		// {
		// GlyphEntry g = git.next();
		// if (g.getParameter(sendParameter) == sid)
		// if (gid == 1)
		// g.select();
		// else
		// g.deSelect();
		// }
		// bRedrawDisplayList_ = true;
		// }
		// }
		// }

	}

	public void forceRebuild()
	{

		bRedrawDisplayList_ = true;
	}

	@Override
	public void triggerUpdate()
	{
		generalManager.getEventPublisher().handleUpdate(this);
	}

	@Override
	public void triggerUpdate(ISelectionDelta selectionDelta)
	{
		if (selectionDelta.getSelectionData().size() > 0)
			generalManager.getEventPublisher().handleUpdate(this, selectionDelta);
	}

	@Override
	public void broadcastElements(ESelectionType type)
	{
		
	}
}
