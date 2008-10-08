package org.caleydo.core.view.opengl.canvas.glyph.gridview;

import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;
import gleem.linalg.Vec2f;
import gleem.linalg.open.Vec2i;
import javax.media.opengl.GL;
import org.caleydo.core.view.opengl.renderstyle.GlyphRenderStyle;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.general.GeneralManager;

public class GlyphGridPositionModel
{
	protected IGeneralManager generalManager = null;
	protected GlyphRenderStyle renderStyle = null;

	protected Vec2i worldLimit = null;

	protected int iDisplayListGrid = -1;

	protected Vec2f glyphCenterWorld = null;
	protected Vec2i glyphCenterGrid = null;


	public GlyphGridPositionModel(GlyphRenderStyle renderStyle)
	{
		generalManager = GeneralManager.get();
		this.renderStyle = renderStyle;
		
		worldLimit = new Vec2i();
		glyphCenterWorld = new Vec2f();
		glyphCenterGrid = new Vec2i();
	}

	public void setWorldLimit(int x, int y)
	{
		worldLimit.setX(x);
		worldLimit.setY(y);
	}

	public int getGridLayout()
	{
		return iDisplayListGrid;
	}

	public void buildGrid(Vector<Vector<GlyphGridPosition>> glyphMap, GL gl)
	{
	}

	public void setGlyphPositions(Vector<Vector<GlyphGridPosition>> glyphMap_,
			ArrayList<GlyphEntry> gg)
	{
		GeneralManager.get().getLogger().log(Level.INFO,
				"setGlyphPositions in base class called");
	}

	public void setGlyphPositions(Vector<Vector<GlyphGridPosition>> glyphMap_,
			ArrayList<GlyphEntry> gg, int centerX, int centerY)
	{
		GeneralManager.get().getLogger().log(Level.INFO,
				"setGlyphPositions in base class called");
	}

	protected boolean isFree(Vector<Vector<GlyphGridPosition>> glyphMap, int x, int y)
	{
		if (glyphMap.contains(x))
			if (glyphMap.get(x).contains(y))
				if (glyphMap.get(x).get(y).getGlyph() != null)
					return false;

		return true;
	}

	public Vec2f getGlyphCenterWorld()
	{
		return glyphCenterWorld;
	}

	public Vec2i getGlyphCenterGrid()
	{
		return glyphCenterGrid;
	}

}