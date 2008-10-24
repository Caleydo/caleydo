package org.caleydo.core.view.opengl.canvas.glyph.gridview;

import gleem.linalg.Vec2f;
import gleem.linalg.Vec3f;
import gleem.linalg.Vec4f;
import gleem.linalg.open.Vec2i;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;
import javax.media.opengl.GL;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.specialized.glyph.EGlyphSettingIDs;
import org.caleydo.core.manager.specialized.glyph.IGlyphManager;
import org.caleydo.core.view.opengl.renderstyle.GlyphRenderStyle;
import org.caleydo.core.view.opengl.util.GLHelperFunctions;

/**
 * Glyph View Grid saves & organizes the positions in the grid
 * 
 * @author Stefan Sauer
 */
public class GLGlyphGrid
{

	private IGeneralManager generalManager = null;

	private IGlyphManager gman = null;

	private Vector<Vector<GlyphGridPosition>> glyphMap_;

	private HashMap<Integer, GlyphEntry> glyphs_ = null;

	private GlyphDataLoader glyphDataLoader = null;

	private int GLGridList_ = -1;

	private EIconIDs iPositionType = EIconIDs.DISPLAY_RECTANGLE;

	private Vec2i worldLimit_ = new Vec2i();

	private Vec2f glyphCenter = new Vec2f();

	private Vec2f glyphLowerLeft = null;
	private Vec2f glyphUpperRight = null;

	private GlyphRenderStyle renderStyle = null;

	private HashMap<EIconIDs, GlyphGridPositionModel> positionModels = null;

	public GLGlyphGrid(GlyphRenderStyle renderStyle)
	{
		this.generalManager = GeneralManager.get();
		this.renderStyle = renderStyle;
		gman = generalManager.getGlyphManager();

		glyphLowerLeft = new Vec2f();
		glyphUpperRight = new Vec2f();

		glyphMap_ = new Vector<Vector<GlyphGridPosition>>();

		positionModels = new HashMap<EIconIDs, GlyphGridPositionModel>();
		positionModels.put(EIconIDs.DISPLAY_PLUS, new GlyphGridPositionModelPlus(renderStyle));
		positionModels.put(EIconIDs.DISPLAY_RANDOM, new GlyphGridPositionModelRandom(
				renderStyle));
		positionModels.put(EIconIDs.DISPLAY_CIRCLE, new GlyphGridPositionModelCircle(
				renderStyle));
		positionModels.put(EIconIDs.DISPLAY_RECTANGLE, new GlyphGridPositionModelRectangle(
				renderStyle));
		positionModels.put(EIconIDs.DISPLAY_SCATTERPLOT,
				new GlyphGridPositionModelScatterplot(renderStyle));

		setGridSize(50, 100);
	}

	/*
	 * World boundary. Only for random positioning (yet) DONT CHANGE THIS @
	 * RUNNING TIME (only directly after "new ....") otherwise you need to
	 * reload everything
	 */
	public void setGridSize(int x, int y)
	{

		worldLimit_.setXY(x, y);

		for (GlyphGridPositionModel model : positionModels.values())
			model.setWorldLimit(x, y);

		glyphMap_.clear();

		for (int i = 0; i < worldLimit_.x(); ++i)
		{
			Vector<GlyphGridPosition> t = new Vector<GlyphGridPosition>();
			for (int j = 0; j < worldLimit_.y(); ++j)
				t.add(j, new GlyphGridPosition(i, j));
			glyphMap_.add(i, t);
		}
	}

	public int getGridLayout(boolean isLocal)
	{
		int dl = positionModels.get(iPositionType).getGridLayout();
		if (dl >= 0)
			return dl;

		if (!isLocal)
			return -1;
		return GLGridList_;
	}

	public int getXMax()
	{
		return worldLimit_.x();
	}

	public int getYMax()
	{
		return worldLimit_.y();
	}

	public Vec2f getGlyphCenter()
	{
		return new Vec2f(glyphCenter);
	}

	public Vec2f getGlyphLowerLeft()
	{
		return new Vec2f(glyphLowerLeft);
	}

	public Vec2f getGlyphUpperRight()
	{
		return new Vec2f(glyphUpperRight);
	}

	public float getGlyphLowerLeftUpperRightDiagonale()
	{
		Vec2f ll = getGlyphLowerLeft();
		Vec2f ur = getGlyphUpperRight();
		Vec2f diagV = new Vec2f();
		diagV.setX(ur.x() - ll.x());
		diagV.setY(ur.y() - ll.y());

		float f = (float) Math.sqrt(diagV.x() * diagV.x() + diagV.y() * diagV.y() + 1.0);

		// System.out.println(diagV.x() + " " + diagV.y() + " " + f);

		return f;
	}

	public ArrayList<Integer> deSelectAll()
	{

		int ssi = Integer.parseInt(generalManager.getGlyphManager().getSetting(
				EGlyphSettingIDs.UPDATESENDPARAMETER));
		// int ssi = glyphDataLoader.getSendParameter();
		ArrayList<Integer> temp = new ArrayList<Integer>();
		for (GlyphEntry g : glyphs_.values())
		{
			if (g.isSelected())
			{
				temp.add(g.getParameter(ssi));
			}
			g.deSelect();
		}
		return temp;
	}

	public ArrayList<Integer> selectAll()
	{

		int ssi = Integer.parseInt(generalManager.getGlyphManager().getSetting(
				EGlyphSettingIDs.UPDATESENDPARAMETER));
		ArrayList<Integer> temp = new ArrayList<Integer>();
		for (GlyphEntry g : glyphs_.values())
		{
			if (!g.isSelected())
			{
				temp.add(g.getParameter(ssi));
			}
			g.select();
		}
		return temp;
	}

	public ArrayList<Integer> selectRubberBand(GL gl, Vec3f point1, Vec3f point2)
	{

		int ssi = Integer.parseInt(generalManager.getGlyphManager().getSetting(
				EGlyphSettingIDs.UPDATESENDPARAMETER));
		ArrayList<Integer> temp = new ArrayList<Integer>();

		// find points in glyph map
		if (glyphMap_.size() < 1)
			return temp;

		Vec2f fPoint1 = new Vec2f(Math.round(point1.x()), Math.round(point1.y()));
		Vec2f fPoint2 = new Vec2f(Math.round(point2.x()), Math.round(point2.y()));

		GlyphGridPosition pos1 = null;
		GlyphGridPosition pos2 = null;

		Vec2f diff1 = new Vec2f();
		diff1.set(1000f, 1000f);

		float length1 = 1000000;
		float length2 = 1000000;

		for (int i = 0; i < glyphMap_.size(); ++i)
		{
			for (int j = 0; j < glyphMap_.get(i).size(); ++j)
			{

				Vec2f pos1ij = glyphMap_.get(i).get(j).getGridPosition().toVec2f();
				pos1ij.sub(fPoint1);

				// System.out.println(i + " " + j + " " + length1);

				if (pos1ij.length() < length1)
				{
					pos1 = glyphMap_.get(i).get(j);
					length1 = pos1ij.length();
				}

				Vec2f pos2i = glyphMap_.get(i).get(j).getGridPosition().toVec2f();
				pos2i.sub(fPoint2);

				if (pos2i.length() < length2)
				{
					pos2 = glyphMap_.get(i).get(j);
					length2 = pos2i.length();
				}

			}
		}
		if (pos1 == null || pos2 == null)
			return temp;

		/*
		 * Vec2i grpos = glyphMap_.get(2).get(5).getPosition(); int glx =
		 * glyphMap_.get(2).get(5).getGlyph().getX(); int gly =
		 * glyphMap_.get(2).get(5).getGlyph().getY();
		 */
		// System.out.println(grpos.x() + ", " + grpos.y() + " | " + glx + " " +
		// gly);
//		System.out.println("p1(" + pos1.getPosition().x() + "," + pos1.getPosition().y()
//				+ "), p2 (" + pos2.getPosition().x() + "," + pos2.getPosition().y() + ")");

		int smallX = pos1.getPosition().y() / 2;
		int bigX = pos2.getPosition().y() / 2;
		if (smallX > bigX)
		{
			smallX = pos2.getPosition().y() / 2;
			bigX = pos1.getPosition().y() / 2;
		}

		int smallY = pos1.getPosition().x();
		int bigY = pos2.getPosition().x();
		if (smallY > bigY)
		{
			smallY = pos2.getPosition().x();
			bigY = pos1.getPosition().x();
		}

		gl.glPushMatrix();
		gl.glTranslatef(pos1.getGridPosition().x(), pos1.getGridPosition().y(), 0);
		GLHelperFunctions.drawAxis(gl);
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glTranslatef(pos2.getGridPosition().x(), pos2.getGridPosition().y(), 0);
		GLHelperFunctions.drawAxis(gl);
		gl.glPopMatrix();

		deSelectAll();

		// System.out.println(smallX + " to " + bigX + " | " + smallY + " to " +
		// bigY);

		for (int i = smallX; i < bigX; ++i)
		{
			for (int j = smallY; j < bigY; ++j)
			{
				GlyphGridPosition ggp = glyphMap_.get(i).get(j);
				if (!ggp.isPositionFree())
					ggp.getGlyph().select();
			}
		}

		return temp;
	}

	public int getNumOfSelected()
	{

		int c = 0;
		for (GlyphEntry g : glyphs_.values())
			if (g.isSelected())
				++c;
		return c;
	}

	public int getNumOfDeSelected()
	{

		int c = 0;
		for (GlyphEntry g : glyphs_.values())
			if (!g.isSelected())
				++c;
		return c;
	}

	public int getGlyphGLList(GL gl, int x, int y)
	{

		if (glyphMap_.contains(x))
			if (glyphMap_.get(x).contains(y))
				if (glyphMap_.get(x).get(y).getGlyph() != null)
					return glyphMap_.get(x).get(y).getGlyph().getGlList(gl);

		generalManager.getLogger().log(
				Level.WARNING,
				"Someone wanted a Glyph GL List on the grid position " + x + ", " + y
						+ ", but there is nothing");
		return -1;
	}

	public int getGlyphID(int x, int y)
	{

		if (glyphMap_.contains(x))
			if (glyphMap_.get(x).contains(y))
				if (glyphMap_.get(x).get(y).getGlyph() != null)
					return glyphMap_.get(x).get(y).getGlyph().getID();

		generalManager.getLogger().log(
				Level.WARNING,
				"Someone wanted a Glyph on the grid position " + x + ", " + y
						+ ", but there is nothing");
		return -1;
	}

	public Vec2i getGridPosition(int x, int y)
	{

		if (x >= worldLimit_.x() || y >= worldLimit_.y())
		{
			generalManager.getLogger()
					.log(
							Level.WARNING,
							"Someone wanted a Glyph outside the grid! (" + x + ", " + y
									+ ") WorldLimit (" + worldLimit_.x() + ","
									+ worldLimit_.y() + ")");
			return null;
		}

		return glyphMap_.get(x).get(y).getGridPosition();
	}

	public Vec2i getGridPosition(GlyphEntry glyph)
	{
		for (Vector<GlyphGridPosition> v1 : glyphMap_)
			for (GlyphGridPosition v2 : v1)
				if(glyph == v2.getGlyph())
					return v2.getGridPosition();

		return new Vec2i();
	}

	public GlyphEntry getGlyph(int id)
	{

		if (!glyphs_.containsKey(id))
		{
			generalManager.getLogger().log(Level.WARNING,
					"Someone wanted a Glyph with the id " + id + " but it doesn't exist.");
			return null;
		}
		return glyphs_.get(id);
	}

	public HashMap<Integer, GlyphEntry> getGlyphList()
	{

		return glyphs_;
	}

	public void setGlyphList(HashMap<Integer, GlyphEntry> glyphs)
	{
		clearGlyphMap();
		glyphs_ = glyphs;
		setGlyphPositions();
	}

	private void clearGlyphMap()
	{

		for (Vector<GlyphGridPosition> v1 : glyphMap_)
			for (GlyphGridPosition v2 : v1)
				v2.setGlyph(null);
	}

	public void loadData(ISet glyphData)
	{

		glyphDataLoader = new GlyphDataLoader();
		if (glyphData != null)
		{
			glyphDataLoader.loadGlyphs(glyphData);
			gman.initGlyphGenerator();
		}
		glyphs_ = gman.getGlyphs();

		setGlyphPositions(iPositionType);
		// setGlyphPositionsRectangle();
		// setGlyphPositionsCenter();
		// setGlyphPositionsRandom();

	}

	public void buildGrids(GL gl)
	{
		// build grids in the position models
		for (GlyphGridPositionModel model : positionModels.values())
			model.buildGrid(glyphMap_, gl);

//		// std grid
//		Vec4f gridColor_ = renderStyle.getGridColor();
//
//		// delete list if present (rebuild grid)
//		if (GLGridList_ >= 0)
//			gl.glDeleteLists(GLGridList_, 1);
//
//		// draw grid
//		GLGridList_ = gl.glGenLists(1);
//		gl.glNewList(GLGridList_, GL.GL_COMPILE);
//
//		gl.glLineWidth(1);
//
//		gl.glTranslatef(0f, -100f, 0f);
//
//		for (int i = 0; i < 200; ++i)
//		{
//			gl.glTranslatef(0f, 1f, 0f);
//			gl.glBegin(GL.GL_LINES);
//			gl.glColor4f(gridColor_.get(0), gridColor_.get(1), gridColor_.get(2), gridColor_
//					.get(3));
//			gl.glVertex3f(-100, 0, 0);
//			gl.glVertex3f(100, 0, 0);
//			gl.glEnd();
//		}
//
//		gl.glTranslatef(-100f, -100f, 0f);
//
//		for (int i = 0; i < 200; ++i)
//		{
//			gl.glTranslatef(1f, 0f, 0f);
//			gl.glBegin(GL.GL_LINES);
//			gl.glColor4f(gridColor_.get(0), gridColor_.get(1), gridColor_.get(2), gridColor_
//					.get(3));
//			gl.glVertex3f(0, -100, 0);
//			gl.glVertex3f(0, 100, 0);
//			gl.glEnd();
//		}
//
//		gl.glTranslatef(-100f, 0f, 0f);
//
//		gl.glEndList();
	}

	public void setGlyphPositions()
	{
		setGlyphPositions(iPositionType);
	}

	public void setGlyphPositions(EIconIDs iTyp)
	{
		iPositionType = iTyp;

		if (this.positionModels.containsKey(iTyp))
		{
			clearGlyphMap();
			ArrayList<GlyphEntry> gg = sortGlyphs(glyphs_.values());
			this.positionModels.get(iTyp).setGlyphPositions(glyphMap_, gg);

			calculateGridSize(this.positionModels.get(iTyp).getGlyphCenterGrid());
		}
	}

	private void calculateGridSize(Vec2i center)
	{
		calculateGridSize(center.x(), center.y());
	}

	private void calculateGridSize(int centerX, int centerY)
	{
		glyphLowerLeft.set(0f, 0f);
		glyphUpperRight.set(0f, 0f);

		glyphCenter.set(glyphMap_.get(centerX).get(centerY).getGridPosition().toVec2f());

		// find lower / upper x
		for (int x = 0; x < worldLimit_.x(); ++x)
		{
			if (!glyphMap_.get(x).get(centerY).isPositionFree() && glyphLowerLeft.x() == 0)
				glyphLowerLeft.setX(glyphMap_.get(x).get(centerY).getPosition().x());
			if (!glyphMap_.get(x).get(centerY).isPositionFree() && glyphLowerLeft.x() != 0)
				glyphUpperRight.setX(glyphMap_.get(x).get(centerY).getPosition().x());
		}
		// find lower / upper y
		for (int y = 0; y < worldLimit_.y(); ++y)
		{
			if (!glyphMap_.get(centerX).get(y).isPositionFree() && glyphLowerLeft.y() == 0)
				glyphLowerLeft.setY(glyphMap_.get(centerX).get(y).getPosition().y());
			if (!glyphMap_.get(centerX).get(y).isPositionFree() && glyphLowerLeft.y() != 0)
				glyphUpperRight.setY(glyphMap_.get(centerX).get(y).getPosition().y());
		}
	}

	private ArrayList<GlyphEntry> sortGlyphs(Collection<GlyphEntry> unsorted)
	{
		return sortGlyphsRecursive(unsorted, 0);
	}

	private ArrayList<GlyphEntry> sortGlyphsRecursive(Collection<GlyphEntry> unsorted,
			int depth)
	{

		HashMap<Integer, ArrayList<GlyphEntry>> temp = new HashMap<Integer, ArrayList<GlyphEntry>>();
		int maxp = 0;

		int sortIndex = gman.getSortOrder(depth);

		if (sortIndex < 0)
		{ // max of sort depth reached
			ArrayList<GlyphEntry> t = new ArrayList<GlyphEntry>();
			t.addAll(unsorted);
			return t;
		}

		for (GlyphEntry g : unsorted)
		{
			int p = g.getParameter(sortIndex);

			if (!temp.containsKey(p))
				temp.put(p, new ArrayList<GlyphEntry>());

			temp.get(p).add(g);
			if (p > maxp)
				maxp = p;
		}

		ArrayList<GlyphEntry> temp2 = new ArrayList<GlyphEntry>();

		for (int i : temp.keySet())
		{
			if (!temp.containsKey(i))
				continue;

			ArrayList<GlyphEntry> gs = sortGlyphsRecursive(temp.get(i), depth + 1);
			temp2.addAll(gs);
		}

		return temp2;
	}

	public boolean isFree(int x, int y)
	{

		if (glyphMap_.contains(x))
			if (glyphMap_.get(x).contains(y))
				if (glyphMap_.get(x).get(y).getGlyph() != null)
					return false;

		return true;
	}

}
