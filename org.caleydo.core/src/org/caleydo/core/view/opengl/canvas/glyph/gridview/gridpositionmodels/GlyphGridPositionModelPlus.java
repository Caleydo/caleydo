package org.caleydo.core.view.opengl.canvas.glyph.gridview.gridpositionmodels;

import gleem.linalg.open.Vec2i;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import javax.media.opengl.GL;
import org.caleydo.core.manager.specialized.glyph.GlyphManager;
import org.caleydo.core.view.opengl.canvas.glyph.GlyphRenderStyle;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.GlyphEntry;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.GlyphGridPosition;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.data.GlyphAttributeType;

public class GlyphGridPositionModelPlus
	extends GlyphGridPositionModel
{
	private GlyphManager gman = null;

	public GlyphGridPositionModelPlus(GlyphRenderStyle renderStyle)
	{
		super(renderStyle);
		gman = (GlyphManager) generalManager.getGlyphManager();
	}

	@Override
	public void setGlyphPositions(Vector<Vector<GlyphGridPosition>> glyphMap,
			ArrayList<GlyphEntry> gg)
	{
		setGlyphPositions(glyphMap, gg, (worldLimit.x() - 2) / 2, (worldLimit.y() - 2) / 2);
	}

	@Override
	public void buildGrid(Vector<Vector<GlyphGridPosition>> glyphMap, GL gl)
	{
		if (!gman.isActive())
			return;

	}

	public void setGlyphPositions(Vector<Vector<GlyphGridPosition>> glyphMap_,
			ArrayList<GlyphEntry> gg, int centerX, int centerY)
	{
		if (centerX == 0 && centerY == 0)
		{
			int num = gg.size();
			int x_max = (int) java.lang.Math.sqrt(num);
			centerX = x_max / 2 + 1;
			centerY = x_max / 2 + 1;
		}
		glyphCenterGrid.setXY(centerX, centerY);
		glyphCenterWorld.set(glyphMap_.get(centerX).get(centerY).getGridPosition().toVec2f());

		//TODO read this from configuration
		int yIndex = gman.getGlyphAttributeTypeWithExternalColumnNumber(9).getInternalColumnNumber();
		int xIndex = gman.getGlyphAttributeTypeWithExternalColumnNumber(6).getInternalColumnNumber();

		for (GlyphEntry ge : gg)
		{
			int y = ge.getParameter(yIndex);
			int x = ge.getParameter(xIndex);
			boolean isRightSide = (x == 0 ? false : true);

			if (y >= glyphMap_.get(0).size())
				continue;

			// this are the glyphs with invalid (or not given) data
			if (y < 0 || x < 0)
			{
				boolean done = false;
				for (int i = 0; i < glyphMap_.size() && !done; ++i)
				{
					for (int j = 0; j < glyphMap_.get(i).size() && !done; ++j)
					{
						if (glyphMap_.get(i).get(j).isPositionFree())
						{
							glyphMap_.get(i).get(j).setGlyph(ge);
							done = true;
						}
					}
				}
				continue;
			}

			boolean done = false;

			if (isRightSide)
			{
				for (int i = centerX; i < glyphMap_.size() && !done; i++)
				{
					if (glyphMap_.get(i).get(y).isPositionFree())
					{
						glyphMap_.get(i).get(y).setGlyph(ge);
						done = true;
					}
				}
			}
			else
			{
				for (int i = centerX; i > 0 && !done; i--)
				{
					if (glyphMap_.get(i).get(y).isPositionFree())
					{
						glyphMap_.get(i).get(y).setGlyph(ge);
						done = true;
					}
				}
			}
		}

	}

}
