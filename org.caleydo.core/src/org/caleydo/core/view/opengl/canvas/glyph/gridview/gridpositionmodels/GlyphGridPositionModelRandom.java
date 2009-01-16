package org.caleydo.core.view.opengl.canvas.glyph.gridview.gridpositionmodels;

import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;
import org.caleydo.core.view.opengl.canvas.glyph.GlyphRenderStyle;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.GlyphEntry;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.GlyphGridPosition;

public class GlyphGridPositionModelRandom
	extends GlyphGridPositionModel
{

	public GlyphGridPositionModelRandom(GlyphRenderStyle renderStyle)
	{
		super(renderStyle);
	}

	@Override
	public void setGlyphPositions(Vector<Vector<GlyphGridPosition>> glyphMap,
			ArrayList<GlyphEntry> gg)
	{
		Random rand = new Random();

		for (GlyphEntry g : gg)
		{
			boolean bFoundplace = false;
			int x;
			int y;
			int counter = 0;
			do
			{
				x = rand.nextInt(worldLimit.x());
				y = rand.nextInt(worldLimit.y());

				bFoundplace = isFree(glyphMap, x, y);
				++counter;

			} while (!bFoundplace && counter < 10000000);

			if (counter >= 10000000)
				System.err.println("no place for glyph " + g.getID() + " found");

			if (bFoundplace)
			{
				glyphMap.get(x).get(y).setGlyph(g);
			}
		}
		glyphCenterGrid.setXY(worldLimit.x() / 2, worldLimit.y() / 2);
		glyphCenterWorld.set(glyphMap.get(glyphCenterGrid.x()).get(glyphCenterGrid.y())
				.getGridPosition().toVec2f());
	}

}
