package org.caleydo.core.view.opengl.canvas.glyph.gridview.gridpositionmodels;

import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;

import org.caleydo.core.view.opengl.canvas.glyph.GlyphRenderStyle;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.GlyphEntry;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.GlyphGridPosition;

public class GlyphGridPositionModelRectangle
	extends GlyphGridPositionModel {

	public GlyphGridPositionModelRectangle(GlyphRenderStyle renderStyle) {
		super(renderStyle);
	}

	@Override
	public void setGlyphPositions(Vector<Vector<GlyphGridPosition>> glyphMap, ArrayList<GlyphEntry> gg) {

		int num = gg.size();
		int x_max = (int) java.lang.Math.sqrt(num);

		if (x_max > worldLimit.x())
			x_max = worldLimit.x();

		int i = 0, j = 0;
		for (GlyphEntry g : gg) {
			if (j >= worldLimit.y()) {
				generalManager.getLogger().log(Level.WARNING,
					"The world is not enough! I've no place to put the glyphs any more.");
				break;
			}

			glyphMap.get(i).get(j).setGlyph(g);

			++i;

			if (i >= x_max) {
				i = 0;
				++j;
			}

		}

		int centerX = x_max / 2;
		int centerY = x_max / 2;

		glyphCenterGrid.setXY(centerX, centerY);
		glyphCenterWorld.set(glyphMap.get(centerX).get(centerY).getGridPosition().toVec2f());
	}

}
