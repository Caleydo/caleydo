package org.caleydo.core.view.opengl.canvas.glyph.gridview;

import gleem.linalg.Vec2f;
import gleem.linalg.Vec4f;
import gleem.linalg.open.Vec2i;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import javax.media.opengl.GL;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.view.glyph.GlyphRenderStyle;
import org.caleydo.view.glyph.gridview.data.GlyphDataLoader;
import org.caleydo.view.glyph.gridview.gridpositionmodels.GlyphGridPositionModel;
import org.caleydo.view.glyph.gridview.gridpositionmodels.GlyphGridPositionModelCircle;
import org.caleydo.view.glyph.gridview.gridpositionmodels.GlyphGridPositionModelPlus;
import org.caleydo.view.glyph.gridview.gridpositionmodels.GlyphGridPositionModelRandom;
import org.caleydo.view.glyph.gridview.gridpositionmodels.GlyphGridPositionModelRectangle;
import org.caleydo.view.glyph.gridview.gridpositionmodels.GlyphGridPositionModelScatterplot;
import org.caleydo.view.glyph.manager.GlyphManager;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Glyph View Grid saves & organizes the positions in the grid
 * 
 * @author Stefan Sauer
 */
public class GLGlyphGrid {
	private boolean bEnableWorldGrid = true;

	private GeneralManager generalManager = null;

	private GlyphManager gman = null;

	private Vector<Vector<GlyphGridPosition>> glyphMap_;

	private HashMap<Integer, GlyphEntry> glyphs_ = null;

	private GlyphDataLoader glyphDataLoader = null;

	private int GLGridList_ = -1;

	private EPositionModel iPositionType = EPositionModel.DISPLAY_SCATTERPLOT;

	private Vec2i worldLimit_ = null;

	private Vec2f glyphCenter = null;

	private Vec2f glyphLowerLeft = null;
	private Vec2f glyphUpperRight = null;

	private GlyphRenderStyle renderStyle = null;

	private HashMap<EPositionModel, GlyphGridPositionModel> positionModels = null;

	private GLGlyphGenerator glyphGenerator;

	/**
	 * Constructor
	 * 
	 * @param renderStyle
	 *            The used Render Style
	 * @param isLocal
	 *            Is the View a local (true) or remote (false) one?
	 */
	public GLGlyphGrid(GlyphRenderStyle renderStyle, boolean isLocal) {
		this.generalManager = GeneralManager.get();
		this.renderStyle = renderStyle;
		gman = generalManager.getGlyphManager();

		glyphLowerLeft = new Vec2f();
		glyphUpperRight = new Vec2f();

		worldLimit_ = new Vec2i();
		glyphCenter = new Vec2f();

		glyphs_ = new HashMap<Integer, GlyphEntry>();
		glyphMap_ = new Vector<Vector<GlyphGridPosition>>();

		positionModels = new HashMap<EPositionModel, GlyphGridPositionModel>();
		positionModels.put(EPositionModel.DISPLAY_PLUS, new GlyphGridPositionModelPlus(renderStyle));
		positionModels.put(EPositionModel.DISPLAY_RANDOM, new GlyphGridPositionModelRandom(renderStyle));
		positionModels.put(EPositionModel.DISPLAY_CIRCLE, new GlyphGridPositionModelCircle(renderStyle));
		positionModels
			.put(EPositionModel.DISPLAY_RECTANGLE, new GlyphGridPositionModelRectangle(renderStyle));
		positionModels.put(EPositionModel.DISPLAY_SCATTERPLOT, new GlyphGridPositionModelScatterplot(
			renderStyle));

		setGridSize(50, 100);

		glyphGenerator = new GLGlyphGenerator(isLocal);
	}

	/**
	 * Sets the World boundary. Only for random positioning (yet). DONT CHANGE THIS @ RUNNING TIME (only
	 * directly after "new ....") otherwise you need to reload everything
	 * 
	 * @param x
	 *            width
	 * @param y
	 *            height
	 */
	public void setGridSize(int x, int y) {

		worldLimit_.setXY(x, y);

		for (GlyphGridPositionModel model : positionModels.values()) {
			model.setWorldLimit(x, y);
		}

		glyphMap_.clear();

		for (int i = 0; i < worldLimit_.x(); ++i) {
			Vector<GlyphGridPosition> t = new Vector<GlyphGridPosition>();
			for (int j = 0; j < worldLimit_.y(); ++j) {
				t.add(j, new GlyphGridPosition(i, j));
			}
			glyphMap_.add(i, t);
		}
	}

	/**
	 * This returns the display list for the grid, if present.
	 * 
	 * @param isLocal
	 *            Is the View a local (true) or remote (false) one?
	 * @return The used display List, or -1 if not present.
	 */
	public int getGridLayout(boolean isLocal) {
		int dl = positionModels.get(iPositionType).getGridLayout();
		if (dl >= 0)
			return dl;

		if (!isLocal)
			return -1;
		return GLGridList_;
	}

	/**
	 * Returns the World Width.
	 * 
	 * @return returns the world width.
	 */
	public int getXMax() {
		return worldLimit_.x();
	}

	/**
	 * Returns the World Height
	 * 
	 * @return
	 */
	public int getYMax() {
		return worldLimit_.y();
	}

	/**
	 * Returns the current Center of the Glyphs inside the Worlds.
	 * 
	 * @return The Position as Grid Positions
	 */
	public Vec2f getGlyphCenter() {
		return new Vec2f(glyphCenter);
	}

	/**
	 * Returns the Lowest, most left Glyph of the Grid.
	 * 
	 * @return The Position as Grid Positions
	 */
	public Vec2f getGlyphLowerLeft() {
		return new Vec2f(glyphLowerLeft);
	}

	/**
	 * Returns the Uppest, most right Glyph of the Grid.
	 * 
	 * @return The Position as Grid Positions
	 */
	public Vec2f getGlyphUpperRight() {
		return new Vec2f(glyphUpperRight);
	}

	/**
	 * Returns the current Glyph Generator
	 * 
	 * @return
	 */
	public GLGlyphGenerator getGlyphGenerator() {
		return glyphGenerator;
	}

	/**
	 * Returns the distance between the lowest left and uppest right Glyph.
	 * 
	 * @return The diagonale length
	 */
	public float getGlyphLowerLeftUpperRightDiagonale() {
		Vec2f ll = getGlyphLowerLeft();
		Vec2f ur = getGlyphUpperRight();
		Vec2f diagV = new Vec2f();
		diagV.setX(ur.x() - ll.x());
		diagV.setY(ur.y() - ll.y());

		float f = (float) Math.sqrt(diagV.x() * diagV.x() + diagV.y() * diagV.y() + 1.0);

		return f;
	}

	/**
	 * This deselects all glyphs in this view.
	 * 
	 * @return a list of ids of the actual deselected glyphs (for trigger update, etc.)
	 */
	public ArrayList<Integer> deSelectAll() {

		// int ssi = glyphDataLoader.getSendParameter();
		ArrayList<Integer> temp = new ArrayList<Integer>();
		for (GlyphEntry g : glyphs_.values()) {
			if (g.isSelected()) {
				temp.add(g.getID());
			}
			g.deSelect();
		}
		return temp;
	}

	/**
	 * This selects all glyphs in this view.
	 * 
	 * @return a list of ids of the actual selected glyphs (for trigger update, etc.)
	 */
	public ArrayList<Integer> selectAll() {
		ArrayList<Integer> temp = new ArrayList<Integer>();
		for (GlyphEntry g : glyphs_.values()) {
			if (!g.isSelected()) {
				temp.add(g.getID());
			}
			g.select();
		}
		return temp;
	}

	/**
	 * Returns the Number of selected Glyphs.
	 * 
	 * @return num of selected Glyphs
	 */
	public int getNumOfSelected() {

		int c = 0;
		for (GlyphEntry g : glyphs_.values())
			if (g.isSelected())
				++c;
		return c;
	}

	/**
	 * Returns the Number of delselected Glyphs.
	 * 
	 * @return num of deselected Glyphs
	 */
	public int getNumOfDeSelected() {

		int c = 0;
		for (GlyphEntry g : glyphs_.values())
			if (!g.isSelected())
				++c;
		return c;
	}

	/**
	 * Returns the diaplay List of the Glyph on the given Position.
	 * 
	 * @param gl
	 *            GL Context
	 * @param x
	 *            X-Grid-Position
	 * @param y
	 *            Y-Grid-Position
	 * @return the display list (or -1 if no glyph is on this position)
	 */
	public int getGlyphGLList(GL gl, int x, int y) {
		if (x >= 0 && glyphMap_.size() > x) {
			Vector<GlyphGridPosition> vGGP = glyphMap_.get(x);

			if (y >= 0 && vGGP.size() > y) {
				GlyphGridPosition ggpos = vGGP.get(y);
				if (ggpos.getGlyph() != null)
					return ggpos.getGlyph().getGlList(gl);
			}
		}

		generalManager.getLogger().log(
			new Status(IStatus.WARNING, GeneralManager.PLUGIN_ID,
				"Someone wanted a Glyph GL List on the grid position " + x + ", " + y
					+ ", but there is nothing"));
		return -1;
	}

	/**
	 * Returns the ID of the Glyph on this Position
	 * 
	 * @param x
	 *            X-Grid-Position
	 * @param y
	 *            Y-Grid-Position
	 * @return The Glyph ID
	 */
	public int getGlyphID(int x, int y) {
		if (x >= 0 && glyphMap_.size() > x) {
			Vector<GlyphGridPosition> vGGP = glyphMap_.get(x);

			if (y >= 0 && vGGP.size() > y) {
				GlyphGridPosition ggpos = vGGP.get(y);
				if (ggpos.getGlyph() != null)
					return ggpos.getGlyph().getID();
			}
		}

		return -1;
	}

	/**
	 * Returns the Real World Position of the given Glyph
	 * 
	 * @param glyph
	 * @return The Real World Position
	 */
	public Vec2i getGridPosition(GlyphEntry glyph) {
		for (Vector<GlyphGridPosition> v1 : glyphMap_) {
			for (GlyphGridPosition v2 : v1)
				if (glyph == v2.getGlyph())
					return v2.getGridPosition();
		}

		return new Vec2i();
	}

	/**
	 * Returns the Grid-Position of the given Glyph
	 * 
	 * @param glyph
	 * @return The Grid-Position
	 */
	public Vec2i getPosition(GlyphEntry glyph) {
		for (Vector<GlyphGridPosition> v1 : glyphMap_) {
			for (GlyphGridPosition v2 : v1)
				if (glyph == v2.getGlyph())
					return v2.getPosition();
		}

		return new Vec2i();
	}

	/**
	 * Returns the Glyph with the given ID.
	 * 
	 * @param id
	 *            ID of the Glyph.
	 * @return the Glyph
	 */
	public GlyphEntry getGlyph(int id) {

		if (!glyphs_.containsKey(id)) {
			generalManager.getLogger().log(
				new Status(IStatus.WARNING, GeneralManager.PLUGIN_ID, "Someone wanted a Glyph with the id "
					+ id + " but it doesn't exist."));
			return null;
		}
		return glyphs_.get(id);
	}

	/**
	 * Returns all of the Glyphs.
	 * 
	 * @return HashMap<GlyphID, Glyph>
	 */
	public HashMap<Integer, GlyphEntry> getGlyphList() {
		return glyphs_;
	}

	/**
	 * Sets the Glyph List
	 * 
	 * @param glyphs
	 *            The Glyphs (HashMap<GlyphID, Glyph>)
	 */
	public void setGlyphList(HashMap<Integer, GlyphEntry> glyphs) {
		clearGlyphMap();
		glyphs_ = glyphs;
		setGlyphPositionModel();
	}

	/**
	 * Removes all the Glyphs from the Grid.
	 */
	private void clearGlyphMap() {
		for (Vector<GlyphGridPosition> v1 : glyphMap_)
			for (GlyphGridPosition v2 : v1)
				v2.setGlyph(null);
	}

	/**
	 * This actually loads the Glyph into the Grid.
	 * 
	 * @param glyphData
	 *            The clinical data set
	 */
	public void loadData(ISet glyphData) {
		glyphDataLoader = new GlyphDataLoader();
		if (glyphData != null)
			glyphDataLoader.loadGlyphs(glyphData);

		glyphs_ = gman.getSelectedGlyphs();

		// setGridSize(glyphs_.size(), glyphs_.size() + 10);

		setGlyphPositionModel(iPositionType);
		// setGlyphPositionsRectangle();
		// setGlyphPositionsCenter();
		// setGlyphPositionsRandom();

	}

	/**
	 * Build the Grid Structure and GL display list for the Grid.
	 * 
	 * @param gl
	 *            GL Context
	 */
	public void buildGrids(GL gl) {
		// build grids in the position models
		for (GlyphGridPositionModel model : positionModels.values())
			model.buildGrid(glyphMap_, gl);

		if (!bEnableWorldGrid)
			return;

		// std grid
		Vec4f gridColor_ = renderStyle.getGridColor();

		// delete list if present (rebuild grid)
		if (GLGridList_ >= 0)
			gl.glDeleteLists(GLGridList_, 1);

		// draw grid
		GLGridList_ = gl.glGenLists(1);
		gl.glNewList(GLGridList_, GL.GL_COMPILE);

		gl.glLineWidth(1);

		gl.glTranslatef(0f, -100f, 0f);

		for (int i = 0; i < 200; ++i) {
			gl.glTranslatef(0f, 1f, 0f);
			gl.glBegin(GL.GL_LINES);
			gl.glColor4f(gridColor_.get(0), gridColor_.get(1), gridColor_.get(2), gridColor_.get(3));
			gl.glVertex3f(-100, 0, 0);
			gl.glVertex3f(100, 0, 0);
			gl.glEnd();
		}

		gl.glTranslatef(-100f, -100f, 0f);

		for (int i = 0; i < 200; ++i) {
			gl.glTranslatef(1f, 0f, 0f);
			gl.glBegin(GL.GL_LINES);
			gl.glColor4f(gridColor_.get(0), gridColor_.get(1), gridColor_.get(2), gridColor_.get(3));
			gl.glVertex3f(0, -100, 0);
			gl.glVertex3f(0, 100, 0);
			gl.glEnd();
		}

		gl.glTranslatef(-100f, 0f, 0f);

		gl.glEndList();
	}

	/**
	 * Returns the Glyph Position Model ENUM
	 * 
	 * @return
	 */
	public EPositionModel getGlyphPositionModel() {
		return iPositionType;
	}

	/**
	 * Returns the Glyph Position Model
	 * 
	 * @param model
	 *            UNUSED (just for methode override)
	 * @return The current Position Model
	 */
	public GlyphGridPositionModel getGlyphPositionModel(EPositionModel model) {
		return positionModels.get(iPositionType);
	}

	/**
	 * Sets the current Glyph Position Model
	 */
	public void setGlyphPositionModel() {
		setGlyphPositionModel(iPositionType);
	}

	/**
	 * Sets a specific Glyph Position Model
	 * 
	 * @param iTyp
	 *            The Position Model Type
	 */
	public void setGlyphPositionModel(EPositionModel iTyp) {
		iPositionType = iTyp;

		if (this.positionModels.containsKey(iTyp)) {
			clearGlyphMap();
			ArrayList<GlyphEntry> gg = sortGlyphs(glyphs_.values());
			this.positionModels.get(iTyp).setGlyphPositions(glyphMap_, gg);

			calculateGridSize(this.positionModels.get(iTyp).getGlyphCenterGrid());
		}
	}

	/**
	 * This calculates the actual used space inside the Grid
	 * 
	 * @param center
	 *            The center of the Glyphs
	 */
	private void calculateGridSize(Vec2i center) {
		calculateGridSize(center.x(), center.y());
	}

	/**
	 * This calculates the actual used space inside the Grid
	 * 
	 * @param centerX
	 *            The center of the Glyphs
	 * @param centerY
	 *            The center of the Glyphs
	 */
	private void calculateGridSize(int centerX, int centerY) {
		glyphLowerLeft.set(0f, 0f);
		glyphUpperRight.set(0f, 0f);

		glyphCenter.set(glyphMap_.get(centerX).get(centerY).getGridPosition().toVec2f());

		// find lower / upper x
		for (int x = 0; x < worldLimit_.x(); ++x) {
			if (!glyphMap_.get(x).get(centerY).isPositionFree() && glyphLowerLeft.x() == 0)
				glyphLowerLeft.setX(glyphMap_.get(x).get(centerY).getPosition().x());
			if (!glyphMap_.get(x).get(centerY).isPositionFree() && glyphLowerLeft.x() != 0)
				glyphUpperRight.setX(glyphMap_.get(x).get(centerY).getPosition().x());
		}
		// find lower / upper y
		for (int y = 0; y < worldLimit_.y(); ++y) {
			if (!glyphMap_.get(centerX).get(y).isPositionFree() && glyphLowerLeft.y() == 0)
				glyphLowerLeft.setY(glyphMap_.get(centerX).get(y).getPosition().y());
			if (!glyphMap_.get(centerX).get(y).isPositionFree() && glyphLowerLeft.y() != 0)
				glyphUpperRight.setY(glyphMap_.get(centerX).get(y).getPosition().y());
		}
	}

	/**
	 * Sorts the given Glyphs acording the defined rules in the xml file.
	 * 
	 * @param unsorted
	 *            list of Glyph entrys to be sorted
	 * @return a sorted list of Glyphs
	 */
	private ArrayList<GlyphEntry> sortGlyphs(Collection<GlyphEntry> unsorted) {
		return sortGlyphsRecursive(unsorted, 0);
	}

	/**
	 * Recursive methode to sort the glyphs.
	 * 
	 * @param unsorted
	 *            list of Glyph entrys to be sorted
	 * @param depth
	 *            recursive depth
	 * @return a sorted list of Glyphs
	 */
	private ArrayList<GlyphEntry> sortGlyphsRecursive(Collection<GlyphEntry> unsorted, int depth) {

		HashMap<Integer, ArrayList<GlyphEntry>> temp = new HashMap<Integer, ArrayList<GlyphEntry>>();
		int maxp = 0;

		int sortIndex = gman.getSortOrder(depth);

		if (sortIndex < 0) { // max of sort depth reached
			ArrayList<GlyphEntry> t = new ArrayList<GlyphEntry>();
			t.addAll(unsorted);
			return t;
		}

		for (GlyphEntry g : unsorted) {
			int p = g.getParameter(sortIndex);

			if (!temp.containsKey(p))
				temp.put(p, new ArrayList<GlyphEntry>());

			temp.get(p).add(g);
			if (p > maxp)
				maxp = p;
		}

		ArrayList<GlyphEntry> temp2 = new ArrayList<GlyphEntry>();

		for (int i : temp.keySet()) {
			if (!temp.containsKey(i))
				continue;

			ArrayList<GlyphEntry> gs = sortGlyphsRecursive(temp.get(i), depth + 1);
			temp2.addAll(gs);
		}

		return temp2;
	}

	/**
	 * Is the Position free? Or is already a Glyph here?
	 * 
	 * @param x
	 *            X-Grid-Position
	 * @param y
	 *            Y-Grid-Position
	 * @return true if the position is free. false if it's not.
	 */
	public boolean isFree(int x, int y) {
		if (glyphMap_.contains(x))
			if (glyphMap_.get(x).contains(y))
				if (glyphMap_.get(x).get(y).getGlyph() != null)
					return false;

		return true;
	}

}
