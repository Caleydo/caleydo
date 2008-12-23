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
import org.caleydo.core.data.mapping.EMappingType;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.GenericSelectionManager;
import org.caleydo.core.data.selection.ISelectionDelta;
import org.caleydo.core.data.selection.SelectedElementRep;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionItem;
import org.caleydo.core.manager.event.mediator.EMediatorType;
import org.caleydo.core.manager.event.mediator.IMediatorReceiver;
import org.caleydo.core.manager.event.mediator.IMediatorSender;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.manager.specialized.glyph.GlyphManager;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.glyph.GlyphRenderStyle;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.data.GlyphAttributeType;
import org.caleydo.core.view.opengl.canvas.remote.IGLCanvasRemoteRendering;
import org.caleydo.core.view.opengl.mouse.JoglMouseListener;
import org.caleydo.core.view.opengl.mouse.PickingJoglMouseListener;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;

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

	int displayListSelectionBrush_ = -1;

	boolean bRedrawDisplayListGrid = true;
	boolean bRedrawDisplayListGlyph = true;
	boolean bDrawConnectionRepLines = true;

	boolean bIsLocal = false;

	private GlyphMouseListener mouseListener_ = null;

	private GlyphKeyListener keyListener_ = null;

	private GlyphRenderStyle renderStyle = null;

	private GlyphManager gman = null;

	private GenericSelectionManager selectionManager = null;

	private GlyphEntry oldMouseOverGlyphEntry = null;

	private int iViewRole = 0;

	private int iCornerOffset = 8;

	private float iViewScale = 0.15f;

	private String sLabel = null;
	private String sLabelPersonal = null;

	private boolean bEnableSelection = false;
	private int iSelectionBrushSize = 2;
	private ArrayList<Vec2i> alSelectionBrushCornerPoints = null;

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

		alSelectionBrushCornerPoints = new ArrayList<Vec2i>();
		mouseListener_ = new GlyphMouseListener(this);
		keyListener_ = new GlyphKeyListener();
		renderStyle = new GlyphRenderStyle(viewFrustum);

		gman = generalManager.getGlyphManager();

		selectionManager = new GenericSelectionManager.Builder(EIDType.EXPERIMENT_INDEX)
				.build();
		viewType = EManagedObjectType.GL_GLYPH;

		// TODO change this to real parameter
		// if (sLabel.equals("Glyph Single View"))
		iViewRole = 1;

		if (sLabel.equals("Glyph Selection View"))
			iViewRole = 2;

	}

	/**
	 * Sets the used positioning model
	 * 
	 * @param iconIDs
	 */
	public synchronized void setPositionModel(EIconIDs iconIDs)
	{
		grid_.setGlyphPositions(iconIDs);
		forceRebuild();

		generalManager.getViewGLCanvasManager().getConnectedElementRepresentationManager()
				.clear(EIDType.EXPERIMENT_INDEX);
	}

	/**
	 * Gets the used positioning model
	 * 
	 */
	public synchronized EIconIDs getPositionModel()
	{
		if (grid_ != null)
			return grid_.getGlyphPositions();
		return EIconIDs.DISPLAY_RECTANGLE;
	}

	/**
	 * Sets the Selection Brush
	 * 
	 * @param enable / disable selection brush
	 * @param size of the brush
	 */
	public synchronized void setSelectionBrush(int size)
	{
		if (size <= 0)
		{
			bEnableSelection = false;
			ArrayList<Integer> ids = null;
			if (size == -1)
				ids = grid_.selectAll();
			if (size == -2)
				ids = grid_.deSelectAll();

			selectionManager.clearSelections();
			alSelectionBrushCornerPoints.clear();

			if (ids != null) // no grid set
				for (int id : ids)
					selectionManager.addToType(ESelectionType.SELECTION, id);

			bDrawConnectionRepLines = false;
			triggerUpdate(EMediatorType.SELECTION_MEDIATOR, selectionManager.getDelta(), null);
			bDrawConnectionRepLines = true;

			bRedrawDisplayListGlyph = true;
		}
		else
		{
			bEnableSelection = true;
			iSelectionBrushSize = size;
		}
	}

	@Override
	public synchronized void init(GL gl)
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

		// grid_.selectAll();

		// init glyph gl
		forceRebuild();

		{ // load ids to the selection manager
			selectionManager.resetSelectionManager();

			ArrayList<Integer> tmpExtID = new ArrayList<Integer>();

			String sTmpExperiment;
			int iExperimentID;
			for (GlyphEntry g : gman.getGlyphs().values())
			{
				sTmpExperiment = g.getStringParameter("sid");

				iExperimentID = generalManager.getIDMappingManager().getID(
						EMappingType.EXPERIMENT_2_EXPERIMENT_INDEX, sTmpExperiment);

				tmpExtID.add(iExperimentID);

			}

			selectionManager.initialAdd(tmpExtID);
		}

	}

	@Override
	public synchronized void initLocal(GL gl)
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
		{
			parentGLCanvas.addMouseListener(mouseListener_);
			mouseListener_.setNavigationModes(true, false, true);
		}

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
	public synchronized void initRemote(final GL gl, final int iRemoteViewID,
			final PickingJoglMouseListener pickingTriggerMouseAdapter,
			final IGLCanvasRemoteRendering remoteRenderingGLCanvas)

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
	public synchronized void displayLocal(GL gl)
	{
		pickingManager.handlePicking(iUniqueID, gl, true);

		display(gl);
		checkForHits(gl);

		pickingTriggerMouseAdapter.resetEvents();

	}

	@Override
	public synchronized void displayRemote(GL gl)
	{

		display(gl);
		checkForHits(gl);
	}

	@Override
	public synchronized void display(GL gl)
	{
		if (grid_ == null)
		{
			renderSymbol(gl);
			return;
		}

		if (grid_.getGlyphList().keySet().size() == 0)
		{
			renderSymbol(gl);
			return;
		}

		// switch between detail level
		if (mouseListener_ != null && !isRenderedRemote())
		{
			float height = -mouseListener_.getCameraHeight();

			// System.out.println(height);

			if (height > 20)
			{
				if (grid_.getGlyphGenerator().getDetailLevel() != GLGlyphGenerator.DETAILLEVEL.LEVEL_MIN)
				{
					grid_.getGlyphGenerator().setDetailLevel(
							GLGlyphGenerator.DETAILLEVEL.LEVEL_MIN);
					bRedrawDisplayListGlyph = true;
				}
			}
			else if (height < 4)
			{
				if (grid_.getGlyphGenerator().getDetailLevel() != GLGlyphGenerator.DETAILLEVEL.LEVEL_MAX)
				{
					grid_.getGlyphGenerator().setDetailLevel(
							GLGlyphGenerator.DETAILLEVEL.LEVEL_MAX);
					bRedrawDisplayListGlyph = true;
				}
			}
			else
			{
				if (grid_.getGlyphGenerator().getDetailLevel() != GLGlyphGenerator.DETAILLEVEL.LEVEL_MID)
				{
					grid_.getGlyphGenerator().setDetailLevel(
							GLGlyphGenerator.DETAILLEVEL.LEVEL_MID);
					bRedrawDisplayListGlyph = true;
				}
			}
		}

		if (bRedrawDisplayListGrid || bRedrawDisplayListGlyph)
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

		if (displayList_ < 0 || bRedrawDisplayListGlyph)
			redrawView(gl);

		int displayListGrid = grid_.getGridLayout(bIsLocal);
		if (displayListGrid >= 0)
			gl.glCallList(displayListGrid);

		if (displayList_ >= 0)
			gl.glCallList(displayList_);

		if (alSelectionBrushCornerPoints != null)
			renderSelectionBrush(gl);

		gl.glTranslatef(-7.0f, 0.0f, 0f);
		gl.glRotatef(-45f, 0, 0, 1);

		gl.glPopMatrix();

		if (mouseListener_ != null)
			mouseListener_.render(gl);

	}

	/**
	 * inits all display lists (for glyphs and grid representations)
	 * 
	 * @param gl
	 */
	private void initDisplayLists(GL gl)
	{
		gl.glPushMatrix();
		Iterator<GlyphEntry> git = grid_.getGlyphList().values().iterator();

		GLGlyphGenerator generator = grid_.getGlyphGenerator();

		while (git.hasNext())
		{
			GlyphEntry e = git.next();
			e.generateGLLists(gl, generator);
		}
		gl.glPopMatrix();

		if (bRedrawDisplayListGrid)
		{
			gl.glPushMatrix();
			grid_.buildGrids(gl);
			grid_.setGlyphPositions();
			gl.glPopMatrix();
		}

		bRedrawDisplayListGrid = false;
	}

	/**
	 * Redraws the view
	 * 
	 * @param gl
	 */
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
			GlyphEntry ge = git.next();

			Vec2i pos = grid_.getGridPosition(ge);
			if (pos == null)
				continue;

			gl.glTranslatef(pos.x(), -(float) pos.y(), 0f);
			gl.glPushName(pickingManager.getPickingID(iUniqueID,
					EPickingType.GLYPH_FIELD_SELECTION, ge.getID()));
			gl.glCallList(ge.getGlList(gl));
			gl.glPopName();
			gl.glTranslatef(-(float) pos.x(), pos.y(), 0f);
		}
		gl.glEndList();

		bRedrawDisplayListGlyph = false;

		gl.glPopMatrix();
	}

	/**
	 * Renders the selection brush
	 * 
	 * @param gl
	 */
	private void renderSelectionBrush(GL gl)
	{
		int size = alSelectionBrushCornerPoints.size();

		Vec2i oldpoint = null;
		if (size > 1)
			oldpoint = alSelectionBrushCornerPoints.get(size - 1);

		for (Vec2i point : alSelectionBrushCornerPoints)
		{
			gl.glPushMatrix();
			gl.glTranslatef(point.x(), -point.y(), 0);

			gl.glLineWidth(3);

			if (!keyListener_.isControlDown())
				gl.glColor4f(0, 1, 0, 1);
			else
				gl.glColor4f(1, 0, 0, 1);

			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(0, 0, 0);
			gl.glVertex3f(0, 0, 1);
			gl.glEnd();

			if (oldpoint != null)
			{
				int x = point.x() - oldpoint.x();
				int y = point.y() - oldpoint.y();

				gl.glBegin(GL.GL_LINES);
				gl.glVertex3f(0, 0, 1);
				gl.glVertex3f(-x, y, 1);
				gl.glEnd();

				gl.glBegin(GL.GL_LINES);
				gl.glVertex3f(0, 0, 0);
				gl.glVertex3f(-x, y, 0);
				gl.glEnd();
			}
			gl.glPopMatrix();

			oldpoint = point;
		}
	}

	/**
	 * Render the symbol of the view instead of the view
	 * 
	 * @param gl
	 */
	private void renderSymbol(GL gl)
	{
		float fXButtonOrigin = 1.3f * renderStyle.getScaling();
		float fYButtonOrigin = 1.3f * renderStyle.getScaling();
		Texture tempTexture = iconTextureManager
				.getIconTexture(gl, EIconTextures.GLYPH_SYMBOL);
		tempTexture.enable();
		tempTexture.bind();

		TextureCoords texCoords = tempTexture.getImageTexCoords();

		// gl.glTranslatef(0.2f, 0.2f, 0);
		gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
		gl.glColor4f(1f, 1, 1, 1f);
		gl.glBegin(GL.GL_POLYGON);

		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(fXButtonOrigin, fYButtonOrigin, 0.01f);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(fXButtonOrigin, 2 * fYButtonOrigin, 0.01f);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(fXButtonOrigin * 2, 2 * fYButtonOrigin, 0.01f);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(fXButtonOrigin * 2, fYButtonOrigin, 0.01f);
		gl.glEnd();
		gl.glPopAttrib();
		tempTexture.disable();
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

	/**
	 * Handles the Rubberband Selection. This method does only the
	 * transformation/scale/rotation of the view. The real rubberband is drawn
	 * in the grid class.
	 * 
	 * @param gl
	 * @param used scale
	 */
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

		bRedrawDisplayListGlyph = true;

	}

	/**
	 * Gives the Position of a Glyph in relation to the "normal" World, without
	 * view interferences
	 * 
	 * @param glyph
	 * @return the center position ot the glyph
	 */
	private Vec3f getGlyphPosition(GlyphEntry glyph)
	{
		// Vec2i gridpos = grid_.getGridPosition( glyph.getX(), glyph.getY() );
		Vec2i gridpos = grid_.getGridPosition(glyph);
		gridpos.setY(-gridpos.y());

		// rotate
		double w = Math.PI / 4.0;
		float sxx = (float) (gridpos.x() * Math.cos(w) - gridpos.y() * Math.sin(w));
		float syy = (float) (gridpos.x() * Math.sin(w) + gridpos.y() * Math.cos(w));

		// translate grid offset
		sxx += (float) (iCornerOffset * Math.cos(w));
		syy += (float) (iCornerOffset * Math.sin(w));

		// translate to glyph center
		syy += (float) (Math.sin(w));

		Vec3f realpos = new Vec3f();
		realpos.set(sxx, syy, 1.0f);
		realpos.scale(iViewScale);

		return realpos;
	}

	/**
	 * This method handles selection per brushing. It selects glyphs around the
	 * given Glyph, acording to the Brush size (iSelectionBrushSize).
	 * 
	 * @param a Glyph Entry (the middle one of the brush)
	 * @param select (true) or deselect (false) mode?
	 */
	private void brushSelect(GlyphEntry glyph, boolean selectDeselect)
	{
		Vec2i pos = grid_.getPosition(glyph);
		alSelectionBrushCornerPoints.clear();

		int k = (iSelectionBrushSize - 1) * 2 + 1;
		int[] selectedGridHeight = new int[k];

		if (pos.y() % 2 == 0)
		{
			int u = 0, v = k - 1;
			for (int i = 0; i < k; ++i) // generates 1,3,4,2,0
			{
				if (i % 2 == 0)
				{
					selectedGridHeight[v] = i;
					v--;
				}
				else
				{
					selectedGridHeight[u] = i;
					u++;
				}
			}

		}
		else
		{
			int u = 0, v = k - 1;
			for (int i = 0; i < k; ++i) // generates 0,2,4,3,1
			{
				if (i % 2 == 0)
				{
					selectedGridHeight[u] = i;
					u++;
				}
				else
				{
					selectedGridHeight[v] = i;
					v--;
				}
			}

		}

		ArrayList<Integer> glyphIDs = new ArrayList<Integer>();
		for (int x = -iSelectionBrushSize + 1; x <= iSelectionBrushSize - 1; ++x)
		{
			int ymax = selectedGridHeight[x + iSelectionBrushSize - 1];
			for (int y = -ymax; y <= ymax; ++y)
				glyphIDs.add(grid_.getGlyphID(pos.x() + x, pos.y() - y));
		}

		if (selectDeselect)
		{
			for (int id : glyphIDs)
				if (id >= 0)
					grid_.getGlyph(id).select();
		}
		else
		{
			for (int id : glyphIDs)
				if (id >= 0)
					grid_.getGlyph(id).deSelect();
		}

		Vec2i gridPos = grid_.getGridPosition(glyph);
		Vec2i temp = null;

		{ // left
			temp = new Vec2i(gridPos);
			temp.setX(gridPos.x() - (iSelectionBrushSize - 1));
			temp.setY(gridPos.y() - (iSelectionBrushSize - 0));
			alSelectionBrushCornerPoints.add(temp);
		}

		{ // top
			temp = new Vec2i(gridPos);
			temp.setX(gridPos.x() + (iSelectionBrushSize - 0));
			temp.setY(gridPos.y() - (iSelectionBrushSize - 0));
			alSelectionBrushCornerPoints.add(temp);
		}

		{ // right
			temp = new Vec2i(gridPos);
			temp.setX(gridPos.x() + (iSelectionBrushSize - 0));
			temp.setY(gridPos.y() + (iSelectionBrushSize - 1));
			alSelectionBrushCornerPoints.add(temp);
		}

		{ // bottom
			temp = new Vec2i(gridPos);
			temp.setX(gridPos.x() - (iSelectionBrushSize - 1));
			temp.setY(gridPos.y() + (iSelectionBrushSize - 1));
			alSelectionBrushCornerPoints.add(temp);
		}
	}

	@Override
	public synchronized String getShortInfo()
	{
		if (sLabelPersonal != null)
			return "Glpyh - " + sLabelPersonal;

		return "Glpyh";
	}

	@Override
	public synchronized String getDetailedInfo()
	{
		StringBuffer sInfoText = new StringBuffer();
		sInfoText.append("Type: Glyph Map");
		sInfoText.append("GL: Showing Glyphs for clinical data");
		return sInfoText.toString();
	}

	@Override
	protected synchronized void handleEvents(EPickingType pickingType,
			EPickingMode pickingMode, int iExternalID, Pick pick)
	{

		if (pickingType == EPickingType.GLYPH_FIELD_SELECTION)
		{
			switch (pickingMode)
			{
				case MOUSE_OVER:
					GlyphEntry g = grid_.getGlyph(iExternalID);

					if (g == null)
					{
						generalManager.getLogger().log(Level.WARNING,
								"Glyph with external ID " + iExternalID + " not found!");
						pickingManager.flushHits(iUniqueID, pickingType);
						return;
					}

					// nothing changed, we don't need to do anything
					if (g == oldMouseOverGlyphEntry)
					{
						pickingManager.flushHits(iUniqueID, pickingType);
						return;
					}
					oldMouseOverGlyphEntry = g;

					selectionManager.clearSelections();
					selectionManager.addToType(ESelectionType.MOUSE_OVER, g.getID());

					if (bEnableSelection)
						brushSelect(g, !keyListener_.isControlDown());

					generalManager.getViewGLCanvasManager()
							.getConnectedElementRepresentationManager().clear(
									EIDType.EXPERIMENT_INDEX);

					triggerUpdate(EMediatorType.SELECTION_MEDIATOR, selectionManager
							.getDelta(), null);

					// <<<<<<< .working
					// only the glyphs need to be redrawn
					bRedrawDisplayListGlyph = true;
					// =======
					// if (grid_.getNumOfSelected() == 0)
					// {
					// ArrayList<Integer> select = grid_.selectAll();
					// ids.addAll(select);
					// while (selections.size() < ids.size())
					// selections.add(1);
					// }
					//
					// generalManager.getGlyphManager()
					// .getGlyphAttributeTypeWithExternalColumnNumber(2)
					// .printDistribution();
					// bRedrawDisplayList_ = true;
					//
					// // push patient id to other screens
					// // TODO rewrite this with new selection
					// // for (Selection sel : alSelection)
					// // {
					// // sel.updateSelectionSet(iUniqueID, ids, selections,
					// null);
					// // }
					//
					// selectionManager.clearSelections();
					// for (int i = 0; i < ids.size(); ++i)
					// {
					// if (selections.get(i) > 0)
					// selectionManager.addToType(ESelectionType.SELECTION,
					// ids.get(i));
					// else
					// selectionManager.addToType(ESelectionType.DESELECTED,
					// ids.get(i));
					// }
					// triggerUpdate(EMediatorType.SELECTION_MEDIATOR,
					// selectionManager.getDelta(), null);
					// >>>>>>> .merge-right.r1683

					break;
				default:
					// System.out.println("picking Mode " +
					// pickingMode.toString());

			}
		}

		pickingManager.flushHits(iUniqueID, pickingType);
	}

	@Override
	public void handleUpdate(IUniqueObject eventTrigger, ISelectionDelta selectionDelta,
			Collection<SelectionCommand> colSelectionCommand, EMediatorType eMediatorType)
	{
		if (selectionDelta.getIDType() != EIDType.EXPERIMENT_INDEX)
			return;

		generalManager.getLogger().log(Level.INFO,
				sLabel + ": Update called by " + eventTrigger.getClass().getSimpleName());

		selectionManager.clearSelections();
		selectionManager.setDelta(selectionDelta);

		if (selectionDelta.size() > 0)
			handleConnectedElementRep(selectionDelta);

		bRedrawDisplayListGlyph = true;

		if (eventTrigger instanceof GLGlyph)
			return;

		GlyphEntry actualGlyph = null;
		for (SelectionItem item : selectionDelta)
		{
			actualGlyph = grid_.getGlyph(item.getSelectionID());

			if (actualGlyph == null) // not glyph to this id found
				continue;

			if (item.getSelectionType() == ESelectionType.NORMAL)
				actualGlyph.deSelect();

			if (item.getSelectionType() == ESelectionType.SELECTION)
				actualGlyph.select();

		}

	}

	/**
	 * This method forces a rebuild of every display list in this view
	 */
	public synchronized void forceRebuild()
	{
		bRedrawDisplayListGrid = true;
		bRedrawDisplayListGlyph = true;
	}

	@Override
	public void triggerUpdate(EMediatorType eMediatorType, ISelectionDelta selectionDelta,
			Collection<SelectionCommand> colSelectionCommand)
	{
		if (selectionDelta.getSelectionData().size() > 0)
		{
			handleConnectedElementRep(selectionDelta);

			generalManager.getEventPublisher().triggerUpdate(eMediatorType, this,
					selectionDelta, null);
		}
	}

	@Override
	public synchronized void broadcastElements(ESelectionType type)
	{

	}

	/**
	 * Handles the creation of {@link SelectedElementRep} uses selection delta
	 * 
	 */
	protected void handleConnectedElementRep(ISelectionDelta selectionDelta)
	{
		if (!bDrawConnectionRepLines)
			return;

		Vec3f vecGlyphPos;
		GlyphEntry actualGlyph = null;

		Collection<SelectionItem> selection = selectionDelta.getSelectionData();
		for (SelectionItem item : selection)
		{
			actualGlyph = grid_.getGlyph(item.getSelectionID());

			if (actualGlyph == null)
				continue;

			if (item.getSelectionType() != ESelectionType.MOUSE_OVER)
				continue;

			vecGlyphPos = getGlyphPosition(actualGlyph);
			// TODO, use new ConnectionIDs
			generalManager.getViewGLCanvasManager().getConnectedElementRepresentationManager()
					.addSelection(
							actualGlyph.getID(),
							new SelectedElementRep(EIDType.EXPERIMENT_INDEX, iUniqueID,
									vecGlyphPos.x(), vecGlyphPos.y(), vecGlyphPos.z()));
		}
	}

	public synchronized void resetSelection()
	{
		for (GlyphEntry g : gman.getGlyphs().values())
			g.select();

		grid_.loadData(null);
		forceRebuild();
	}

	public synchronized void removeUnselected()
	{
		grid_.loadData(null);
		forceRebuild();
	}

	public void setPersonalName(String name)
	{
		sLabelPersonal = name;
	}

	public String getPersonalName()
	{
		return sLabelPersonal;
	}

	/**
	 * Temporary fix
	 * 
	 * @param addHeader you want a header?
	 * @param viewName
	 * @return
	 */
	@Deprecated
	public String getContainingDataAsCSV(boolean addHeader, String viewName)
	{
		Collection<GlyphEntry> list = grid_.getGlyphList().values();

		String content = "";

		// make header
		if (addHeader)
		{
			content += "GROUP; ID";

			for (int i = 1; i < gman.getGlyphAttributes().size(); ++i)
			{
				String name = gman.getGlyphAttributeTypeWithInternalColumnNumber(i).getName();

				content += "; " + name;
			}

			GlyphEntry ge = (GlyphEntry) list.toArray()[0];
			ArrayList<String> names = ge.getStringParameterColumnNames();

			for (String name : names)
			{
				content += "; " + name;
			}
			content += "\r\n";
		}

		for (GlyphEntry ge : list)
		{
			String line = viewName + "; ";

			line += GeneralManager.get().getIDMappingManager().getID(
					EMappingType.EXPERIMENT_INDEX_2_EXPERIMENT, ge.getID());

			for (int i = 0; i < ge.getNumberOfParameters(); ++i)
			{
				GlyphAttributeType type = gman
						.getGlyphAttributeTypeWithInternalColumnNumber(i);

				line += "; " + type.getParameterString(ge.getParameter(i));
			}

			for (String name : ge.getStringParameterColumnNames())
			{
				line += "; " + ge.getStringParameter(name);
			}
			content += line + "\r\n";
		}

		return content;
	}

	@Override
	public int getNumberOfSelections(ESelectionType eSelectionType)
	{
		throw new IllegalStateException("Not implemented yet. Do this now!");
	}
}
