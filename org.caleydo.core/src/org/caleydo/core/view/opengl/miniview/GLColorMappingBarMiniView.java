package org.caleydo.core.view.opengl.miniview;

import javax.media.opengl.GL;
import org.caleydo.core.util.mapping.PathwayColorMapper;
import org.caleydo.core.util.mapping.color.ColorMapping;

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
	public GLColorMappingBarMiniView(final PathwayColorMapper genomeMapper)
	{

		super();

		this.genomeMapper = genomeMapper;
	}

	@Override
	public void render(GL gl, float fXOrigin, float fYOrigin, float fZOrigin)
	{

		ColorMapping colorMapper = genomeMapper.getColorMapper();
	
		gl.glBegin(GL.GL_QUAD_STRIP);
		gl.glColor3fv(colorMapper.getColor(0), 1);
		gl.glVertex3f(fXOrigin, fYOrigin, fZOrigin);
		gl.glVertex3f(fXOrigin + fWidth, fYOrigin, fZOrigin);
		gl.glColor3fv(colorMapper.getColor(0.5f), 1);
		gl.glVertex3f(fXOrigin, fYOrigin + fHeight / 2, fZOrigin);
		gl.glVertex3f(fXOrigin + fWidth, fYOrigin + fHeight / 2, fZOrigin);
		gl.glColor3fv(colorMapper.getColor(1), 1);
		gl.glVertex3f(fXOrigin, fYOrigin + fHeight, fZOrigin);
		gl.glVertex3f(fXOrigin + fWidth, fYOrigin + fHeight, fZOrigin);
		gl.glEnd();
	}
}
