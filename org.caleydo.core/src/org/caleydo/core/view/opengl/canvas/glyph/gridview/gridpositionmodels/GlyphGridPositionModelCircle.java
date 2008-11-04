package org.caleydo.core.view.opengl.canvas.glyph.gridview.gridpositionmodels;

import java.util.ArrayList;
import java.util.Vector;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.GlyphEntry;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.GlyphGridPosition;
import org.caleydo.core.view.opengl.renderstyle.GlyphRenderStyle;

public class GlyphGridPositionModelCircle
	extends GlyphGridPositionModel
{

	public GlyphGridPositionModelCircle(GlyphRenderStyle renderStyle)
	{
		super(renderStyle);
	}

	public void setGlyphPositions(Vector<Vector<GlyphGridPosition>> glyphMap,
			ArrayList<GlyphEntry> gg)
	{
		setGlyphPositions(glyphMap, gg, (worldLimit.x() - 2) / 2, (worldLimit.y() - 2) / 2);
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
		
		if(centerX > glyphMap_.size())
			centerX = glyphMap_.size()-1;

		if(centerY > glyphMap_.get(centerX).size())
			centerY = glyphMap_.get(centerX).size()-1;
		
		glyphCenterGrid.setXY(centerX, centerY);
		glyphCenterWorld.set(glyphMap_.get(centerX).get(centerY).getGridPosition().toVec2f());

		int k = 0;
		int d = 0;
		int cm = 1;
		int c = 0;
		int x = centerX;
		int y = centerY;

		for (GlyphEntry g : gg)
		{
			boolean isfree = false;

			if (x >= 0 && y >= 0 && x < this.worldLimit.x() && y < this.worldLimit.y())
				isfree = glyphMap_.get(x).get(y).isPositionFree();

			while (!isfree)
			{
				switch (d % 4)
				{
					case 0:
						++x;
						break;
					case 1:
						--y;
						break;
					case 2:
						--x;
						break;
					case 3:
						++y;
						break;
				}
				++c;
				if (c == cm)
				{
					++d;
					c = 0;
					++k;
					if (k == 2)
					{
						++cm;
						k = 0;
					}
				}
				if (x < 0)
					x = 0;
				if (y < 0)
					y = 0;

				if (x < 0)
					x = 0;
				if (x >= worldLimit.x())
					x = worldLimit.x() - 1;
				if (y < 0)
					y = 0;
				if (y >= worldLimit.y())
					y = worldLimit.y() - 1;
				isfree = glyphMap_.get(x).get(y).isPositionFree();
			}
			glyphMap_.get(x).get(y).setGlyph(g);

		}
	}
}
