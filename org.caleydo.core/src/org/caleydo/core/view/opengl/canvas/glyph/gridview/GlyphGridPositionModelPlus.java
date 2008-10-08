package org.caleydo.core.view.opengl.canvas.glyph.gridview;

import java.util.ArrayList;
import java.util.Vector;
import org.caleydo.core.view.opengl.renderstyle.GlyphRenderStyle;

public class GlyphGridPositionModelPlus
	extends GlyphGridPositionModel
{

	public GlyphGridPositionModelPlus(GlyphRenderStyle renderStyle)
	{
		super(renderStyle);
	}

	public void setGlyphPositions(Vector<Vector<GlyphGridPosition>> glyphMap,
			ArrayList<GlyphEntry> gg)
	{
		//TODO: implement plus model
		
		glyphCenterGrid.setXY(worldLimit.x() / 2, worldLimit.y() / 2);
		glyphCenterWorld.set(glyphMap.get(glyphCenterGrid.x()).get(glyphCenterGrid.y())
				.getGridPosition().toVec2f());

	}

}
