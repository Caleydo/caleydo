package org.caleydo.core.view.opengl.miniview;

import java.util.ArrayList;
import javax.media.opengl.GL;
import org.caleydo.core.util.mapping.PathwayColorMapper;
import org.caleydo.core.util.mapping.color.ColorMapping;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.mapping.color.ColorMarkerPoint;
import org.caleydo.core.util.mapping.color.EColorMappingType;

/**
 * Mini view that renders the current color bar.
 * 
 * TODO do this for the marker points
 * 
 * @author Marc Streit
 */
public class GLColorMappingBarMiniView
	extends AGLMiniView
{

	private PathwayColorMapper genomeMapper;

	/**
	 * Constructor.
	 */
	public GLColorMappingBarMiniView()
	{

		super();

	}

	@Override
	public void render(GL gl, float fXOrigin, float fYOrigin, float fZOrigin)
	{

		ColorMapping colorMapper = ColorMappingManager.get().getColorMapping(
				EColorMappingType.GENE_EXPRESSION);

		ArrayList<ColorMarkerPoint> alColorMarkerPoints = colorMapper.getMarkerPoints();

		gl.glBegin(GL.GL_QUAD_STRIP);
		int iCount = 0;
		for (ColorMarkerPoint markerPoint : alColorMarkerPoints)
		{
			gl.glColor3fv(markerPoint.getColor(), 0);
			float fYCurrent = fYOrigin + fHeight * markerPoint.getValue();
			gl.glVertex3f(fXOrigin, fYCurrent, fZOrigin);
			gl.glVertex3f(fXOrigin + fWidth, fYCurrent, fZOrigin);
			iCount++;
		}
		gl.glEnd();
	}
}
