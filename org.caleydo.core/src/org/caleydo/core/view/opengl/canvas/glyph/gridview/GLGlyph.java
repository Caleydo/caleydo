package org.caleydo.core.view.opengl.canvas.glyph.gridview;

import gleem.linalg.Rotf;
import gleem.linalg.Vec2f;
import gleem.linalg.Vec3f;
import gleem.linalg.open.Vec2i;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import javax.media.opengl.GL;
import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.GenericSelectionManager;
import org.caleydo.core.data.selection.ISelectionDelta;
import org.caleydo.core.manager.event.mediator.IMediatorReceiver;
import org.caleydo.core.manager.event.mediator.IMediatorSender;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.manager.specialized.glyph.EGlyphSettingIDs;
import org.caleydo.core.manager.specialized.glyph.GlyphManager;
import org.caleydo.core.manager.specialized.glyph.IGlyphManager;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.remote.IGLCanvasRemoteRendering3D;
import org.caleydo.core.view.opengl.mouse.JoglMouseListener;
import org.caleydo.core.view.opengl.mouse.PickingJoglMouseListener;
import org.caleydo.core.view.opengl.renderstyle.GlyphRenderStyle;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteHierarchyLevel;

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

	private int iViewRole = 0;

	private int iCornerOffset = 8;

	private float iViewScale = 0.15f;

	private String sLabel = null;

	/**
	 * Constructor.
	 * 
	 * @param iViewID
	 * @param iGLCanvasID
	 * @param sLabel
	 * @param viewFrustum
	 */
	public GLGlyph(final int iGLCanvasID, final String sLabel, final IViewFrustum viewFrustum)
	{
		super(iGLCanvasID, sLabel, viewFrustum, true);

		this.sLabel = sLabel;

		mouseListener_ = new GlyphMouseListener(this);
		keyListener_ = new GlyphKeyListener();
		renderStyle = new GlyphRenderStyle(viewFrustum);

		gman = (GlyphManager) generalManager.getGlyphManager();
		gman.registerGlyphView(this);

		selectionManager = new GenericSelectionManager.Builder(EIDType.CLINICAL_ID).build();
		viewType = EManagedObjectType.GL_GLYPH;

		if (sLabel.equals("Glyph Single View"))
			iViewRole = 1;

		if (sLabel.equals("Glyph Selection View"))
			iViewRole = 2;
	}

	public void setViewMode(EIconIDs iconIDs)
	{
		grid_.setGlyphPositions(iconIDs);
		bRedrawDisplayList_ = true;
	}

	@Override
	public void init(GL gl)
	{

		ISet glyphData = null;

		for (ISet tmpSet : alSets)
		{
			if (tmpSet != null)
			{
				if (tmpSet.getLabel().equals("Set for clinical data"))
					glyphData = tmpSet;
			}
		}

		grid_ = new GLGlyphGrid(renderStyle);
		grid_.loadData(glyphData);

		grid_.buildGrids(gl);

		grid_.selectAll();

		// init glyph gl
		bRedrawDisplayList_ = true;

		{ // load ids to the selection manager
			selectionManager.resetSelectionManager();

			int sendParameter = Integer.parseInt(gman
					.getSetting(EGlyphSettingIDs.UPDATESENDPARAMETER));

			ArrayList<Integer> tmpExtID = new ArrayList<Integer>();

			for (GlyphEntry g : gman.getGlyphs().values())
				tmpExtID.add(g.getParameter(sendParameter));

			selectionManager.initialAdd(tmpExtID);
		}

	}

	@Override
	public void initLocal(GL gl)
	{

		bIsLocal = true;

		float fInitZoom = -10f;

		// position /scale camera
		Rotf t = new Rotf();
		t.set(new Vec3f(-1, 0, 0), (float) (Math.PI / 4.0 - 2.0 * Math.PI * (-(fInitZoom + 3))
				/ 360.0));
		this.getViewCamera().setCameraRotation(t);
		this.getViewCamera().addCameraPosition(new Vec3f(0, 0, fInitZoom));

		init(gl);

		// disable standard mouse movement (DON't remove the listeners, it will
		// affect the picking!
		{
			MouseListener[] ml = parentGLCanvas.getMouseListeners();
			for (MouseListener l : ml)
			{
				if (l instanceof JoglMouseListener)
					((JoglMouseListener) l).setNavigationModes(false, false, false);
			}
		}

		// Register specialized mouse wheel listener
		if (mouseListener_ != null)
			parentGLCanvas.addMouseListener(mouseListener_);

		if (iViewRole != 2 && mouseListener_ != null)
		{
			parentGLCanvas.addMouseMotionListener(mouseListener_);
			parentGLCanvas.addMouseWheelListener(mouseListener_);

			parentGLCanvas.addKeyListener(keyListener_);
		}

		grid_.setGlyphPositions(EIconIDs.DISPLAY_RECTANGLE);
		// grid_.setGlyphPositions(EIconIDs.DISPLAY_PLUS);

		if (this.iViewRole == 2)
			grid_.setGlyphPositions(EIconIDs.DISPLAY_CIRCLE);

	}

	@Override
	public void initRemote(final GL gl, final int iRemoteViewID,
			final RemoteHierarchyLevel layer,
			final PickingJoglMouseListener pickingTriggerMouseAdapter,
			final IGLCanvasRemoteRendering3D remoteRenderingGLCanvas)

	{

		bIsLocal = false;
		this.remoteRenderingGLCanvas = remoteRenderingGLCanvas;

		Collection<GLCaleydoCanvas> cc = generalManager.getViewGLCanvasManager()
				.getAllGLCanvasUsers();

		for (GLCaleydoCanvas c : cc)
		{
			c.addKeyListener(keyListener_);
		}

		init(gl);

		grid_.setGridSize(30, 60);
		grid_.setGlyphPositions(EIconIDs.DISPLAY_RECTANGLE);
		// grid_.setGlyphPositions(EIconIDs.DISPLAY_SCATTERPLOT);
	}

	@Override
	public void displayLocal(GL gl)
	{
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
			initDisplayLists(gl);

		organizeGlyphsForViewRole();

		gl.glPushMatrix();

		// rotate grid
		gl.glRotatef(45f, 0, 0, 1);

		float scale = iViewScale;

		if (iViewRole == 2) // don't scale down for the selectionview
			scale = 1;

		gl.glScalef(scale, scale, scale);
		gl.glTranslatef(iCornerOffset, 0, 0f);

		if (mouseListener_ != null)
			handleMouseListenerRubberband(gl, scale);

		if (displayList_ < 0 || bRedrawDisplayList_)
			redrawView(gl);

		int displayListGrid = grid_.getGridLayout(bIsLocal);
		if (displayListGrid >= 0)
			gl.glCallList(displayListGrid);

		if (displayList_ >= 0)
			gl.glCallList(displayList_);

		gl.glTranslatef(-7.0f, 0.0f, 0f);
		gl.glRotatef(-45f, 0, 0, 1);

		gl.glPopMatrix();

		if (mouseListener_ != null)
			mouseListener_.render(gl);

	}

	private void initDisplayLists(GL gl)
	{
		gl.glPushMatrix();
		Iterator<GlyphEntry> git = grid_.getGlyphList().values().iterator();

		while (git.hasNext())
		{
			GlyphEntry e = git.next();
			e.generateGLLists(gl);
		}
		gl.glPopMatrix();

		gl.glPushMatrix();
		grid_.buildGrids(gl);
		grid_.setGlyphPositions();
		gl.glPopMatrix();

	}

	private void redrawView(GL gl)
	{
		gl.glPushMatrix();

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

			gl.glTranslatef(pos.x(), -(float) pos.y(), 0f);
			gl.glPushName(pickingManager.getPickingID(iUniqueID,
					EPickingType.GLYPH_FIELD_SELECTION, e.getID()));
			gl.glCallList(e.getGlList(gl));
			gl.glPopName();
			gl.glTranslatef(-(float) pos.x(), pos.y(), 0f);
		}
		gl.glEndList();

		bRedrawDisplayList_ = false;

		gl.glPopMatrix();
	}

	private void organizeGlyphsForViewRole()
	{
		if (iViewRole != 2)
			return;

		HashMap<Integer, GlyphEntry> tmp = new HashMap<Integer, GlyphEntry>();
		for (GlyphEntry g : gman.getGlyphs().values())
		{
			if (g.isSelected())
				tmp.put(g.getID(), g);
		}
		grid_.setGlyphList(tmp);

		// position camera
		Vec3f campos = new Vec3f();
		Vec2f camcenter = grid_.getGlyphCenter();

		float diag = grid_.getGlyphLowerLeftUpperRightDiagonale();

		float x = (float) (Math.sin(Math.PI / 4.0) * (camcenter.x() + 8.0f));
		float y = (float) (Math.cos(Math.PI / 4.0) * (camcenter.x() + 8.0f));

		float f = 1.0f - diag * 0.05f;

		campos.set(-x, -y + 8, 7 + f * 10);

		this.getViewCamera().setCameraPosition(campos);
	}

	private void handleMouseListenerRubberband(GL gl, float scale)
	{
		ArrayList<Vec3f> points = mouseListener_.getRubberBandPoints();
		if (points.size() < 2)
			return;

		Vec3f sPos = points.get(0);
		Vec3f cPos = points.get(1);

		// scale
		sPos.scale(1 / scale);
		cPos.scale(1 / scale);

		// rotate
		double w = -Math.PI / 4.0;
		float sxx = (float) (sPos.x() * Math.cos(w) - sPos.y() * Math.sin(w));
		float syy = (float) (sPos.x() * Math.sin(w) + sPos.y() * Math.cos(w));

		float cxx = (float) (cPos.x() * Math.cos(w) - cPos.y() * Math.sin(w));
		float cyy = (float) (cPos.x() * Math.sin(w) + cPos.y() * Math.cos(w));

		// translate
		cxx -= iCornerOffset;
		sxx -= iCornerOffset;

		Vec3f sPosNew = new Vec3f(sxx, syy, sPos.z());
		Vec3f cPosNew = new Vec3f(cxx, cyy, cPos.z());

		// handle selections
		grid_.selectRubberBand(gl, sPosNew, cPosNew);

		// set to grid
		Vec2i iPoint1 = new Vec2i();
		iPoint1.setXY(Math.round(sPosNew.x()), Math.round(sPosNew.y()));
		Vec2i iPoint2 = new Vec2i();
		iPoint2.setXY(Math.round(cPosNew.x()), Math.round(cPosNew.y()));

		gl.glPushMatrix();
		gl.glTranslatef(iPoint1.x(), iPoint1.y(), 0);
		// GLHelperFunctions.drawAxis(gl);
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glTranslatef(iPoint2.x(), iPoint2.y(), 0);
		// GLHelperFunctions.drawAxis(gl);
		gl.glPopMatrix();

		bRedrawDisplayList_ = true;

	}

	@Override
	public String getShortInfo()
	{
		return "Glpyh";
	}

	@Override
	public String getDetailedInfo()
	{
		StringBuffer sInfoText = new StringBuffer();
		sInfoText.append("Type: Glyph Map");
		sInfoText.append("GL: Showing test clinical data");
		return sInfoText.toString();
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
						ArrayList<String> colnames = g.getStringParameterColumnNames();
						String sampleid = g.getStringParameter(colnames.get(0));

						System.out.println("  select object index: "
								+ Integer.toString(iExternalID) + " on Point " + g.getX()
								+ " " + g.getY() + " with Patient ID " + sendID
								+ " and T staging " + stagingT + " and dfs " + dfs + " + "
								+ sampleid);
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
					selectionManager.clearSelections();
					for (int i = 0; i < ids.size(); ++i)
					{
						if (selections.get(i) > 0)
							selectionManager.addToType(ESelectionType.SELECTION, ids.get(i));
						else
							selectionManager.addToType(ESelectionType.DESELECTED, ids.get(i));
					}
					triggerUpdate(selectionManager.getDelta());

					break;
				default:
					// System.out.println("picking Mode " +
					// pickingMode.toString());

			}
		}

		pickingManager.flushHits(iUniqueID, pickingType);
	}

	@Override
	public void handleUpdate(IUniqueObject eventTrigger, ISelectionDelta selectionDelta)
	{

		generalManager.getLogger().log(Level.INFO,
				sLabel + ": Update called by " + eventTrigger.getClass().getSimpleName());

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

	}

	public void forceRebuild()
	{

		bRedrawDisplayList_ = true;
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
