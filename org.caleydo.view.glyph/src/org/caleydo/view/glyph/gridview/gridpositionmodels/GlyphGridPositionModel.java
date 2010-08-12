package org.caleydo.core.view.opengl.canvas.glyph.gridview.gridpositionmodels;

import gleem.linalg.Vec2f;
import gleem.linalg.open.Vec2i;

import java.util.ArrayList;
import java.util.Vector;

import javax.media.opengl.GL;

import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.view.glyph.GlyphRenderStyle;
import org.caleydo.view.glyph.gridview.GlyphEntry;
import org.caleydo.view.glyph.gridview.GlyphGridPosition;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Base Class for a Glyph Position Model. Delivers basic function. Override this methods to implement a new
 * position model. Register it in the GlyphGrid and implement new Event.
 * 
 * @author Stefan Sauer
 */
public class GlyphGridPositionModel {
	protected IGeneralManager generalManager = null;
	protected GlyphRenderStyle renderStyle = null;

	protected Vec2i worldLimit = null;

	protected int iDisplayListGrid = -1;

	protected Vec2f glyphCenterWorld = null;
	protected Vec2i glyphCenterGrid = null;

	/**
	 * Constructor
	 * 
	 * @param renderStyle
	 */
	public GlyphGridPositionModel(GlyphRenderStyle renderStyle) {
		generalManager = GeneralManager.get();
		this.renderStyle = renderStyle;

		worldLimit = new Vec2i();
		glyphCenterWorld = new Vec2f();
		glyphCenterGrid = new Vec2i();
	}

	/**
	 * Sets the Grid Limits.
	 * 
	 * @param x
	 * @param y
	 */
	public void setWorldLimit(int x, int y) {
		worldLimit.setXY(x, y);
	}

	/**
	 * Returns the created display list.
	 * 
	 * @return
	 */
	public int getGridLayout() {
		return iDisplayListGrid;
	}

	/**
	 * Builds a special grid, if necessary.
	 * 
	 * @param glyphMap
	 * @param gl
	 */
	public void buildGrid(Vector<Vector<GlyphGridPosition>> glyphMap, GL gl) {
	}

	/**
	 * This method positions the glyphs in the grid.
	 * 
	 * @param glyphMap_
	 *            The used Grid
	 * @param gg
	 *            List of glpyhs
	 * @param centerX
	 *            Center position
	 * @param centerY
	 *            Center position
	 */
	public void setGlyphPositions(Vector<Vector<GlyphGridPosition>> glyphMap_, ArrayList<GlyphEntry> gg) {
		GeneralManager.get().getLogger().log(
			new Status(IStatus.INFO, IGeneralManager.PLUGIN_ID, "setGlyphPositions in base class called"));
	}

	/**
	 * This method positions the glyphs in the grid, around a defined center point.
	 * 
	 * @param glyphMap_
	 *            The used Grid
	 * @param gg
	 *            List of glpyhs
	 * @param centerX
	 *            Center position
	 * @param centerY
	 *            Center position
	 */
	public void setGlyphPositions(Vector<Vector<GlyphGridPosition>> glyphMap_, ArrayList<GlyphEntry> gg,
		int centerX, int centerY) {
		GeneralManager.get().getLogger().log(
			new Status(IStatus.INFO, IGeneralManager.PLUGIN_ID, "setGlyphPositions in base class called"));
	}

	/**
	 * Is the defined x/y Position Free in the map?
	 * 
	 * @param glyphMap
	 * @param x
	 * @param y
	 * @return
	 */
	protected boolean isFree(Vector<Vector<GlyphGridPosition>> glyphMap, int x, int y) {
		if (glyphMap.contains(x))
			if (glyphMap.get(x).contains(y))
				if (glyphMap.get(x).get(y).getGlyph() != null)
					return false;

		return true;
	}

	/**
	 * Returns the center of the Positioned glyphs in world coordinates.
	 * 
	 * @return the position
	 */
	public Vec2f getGlyphCenterWorld() {
		return glyphCenterWorld;
	}

	/**
	 * Returns the center of the Positioned glyphs in grid coordinates.
	 * 
	 * @return the position
	 */
	public Vec2i getGlyphCenterGrid() {
		return glyphCenterGrid;
	}

}