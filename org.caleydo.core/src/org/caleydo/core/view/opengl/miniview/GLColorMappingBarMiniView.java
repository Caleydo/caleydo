package org.caleydo.core.view.opengl.miniview;

import javax.media.opengl.GL;

import org.caleydo.core.util.mapping.GenomeColorMapper;
import org.caleydo.core.util.mapping.color.ColorMapping;

/**
 * Mini view that renders the current color bar.
 * 
 * @author Marc Streit
 */
public class GLColorMappingBarMiniView 
extends AGLMiniView {

	private GenomeColorMapper genomeMapper;
	
	/**
	 * Constructor.
	 */
	public GLColorMappingBarMiniView(final GenomeColorMapper genomeMapper) {
		
		super();
		
		this.genomeMapper = genomeMapper;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.miniview.AGLMiniView#render(javax.media.opengl.GL, float, float, float)
	 */
	public void render(GL gl, float fXOrigin, float fYOrigin, float fZOrigin) {

		ColorMapping colorMapper = genomeMapper.getColorMapper();
		
		gl.glBegin(GL.GL_QUAD_STRIP);
		gl.glColor3f(colorMapper.getColor_1().x(), colorMapper.getColor_1().y(), colorMapper.getColor_1().z());
		gl.glVertex3f(fXOrigin, fYOrigin, fZOrigin);
		gl.glVertex3f(fXOrigin + fWidth, fYOrigin, fZOrigin);
		gl.glColor3f(colorMapper.getColor_2().x(), colorMapper.getColor_2().y(), colorMapper.getColor_2().z());
		gl.glVertex3f(fXOrigin, fYOrigin + fHeight / 2, fZOrigin);
		gl.glVertex3f(fXOrigin + fWidth, fYOrigin + fHeight / 2, fZOrigin);
		gl.glColor3f(colorMapper.getColor_3().x(), colorMapper.getColor_3().y(), colorMapper.getColor_3().z());		
		gl.glVertex3f(fXOrigin, fYOrigin + fHeight, fZOrigin);
		gl.glVertex3f(fXOrigin + fWidth, fYOrigin + fHeight, fZOrigin);
		gl.glEnd();
	}
}
