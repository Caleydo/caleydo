/**
 * 
 */
package org.caleydo.view.filterpipeline.representation;

import gleem.linalg.Vec3f;
import javax.media.opengl.GL2;
import org.caleydo.core.view.opengl.util.AGLGUIElement;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.view.filterpipeline.FilterItem;

/**
 * @author Thomas Geymayer
 *
 */
public class FilterMenu
	extends AGLGUIElement
	implements IRenderable
{
	private FilterItem<?> filter = null;
	
	/**
	 * 
	 */
	public FilterMenu()
	{
		super();
		
		minSize = 10;
	}
	
	/**
	 * 
	 * @param filter
	 */
	public void setFilter(FilterItem<?> filter)
	{
		this.filter = filter;
	}

	@Override
	public void render(GL2 gl, CaleydoTextRenderer textRenderer)
	{
		if( filter == null )
			return;

		float x = filter.getRepresentation().getPosition().x(),
			  y = filter.getRepresentation().getPosition().y();
		
		beginGUIElement(gl, new Vec3f());
		gl.glPushName(filter.getPickingID());
		gl.glBegin(GL2.GL_QUADS);
		{
			gl.glColor3f(1, 0, 0);
			gl.glVertex3d(x - 0.5, y, 0.9);
			gl.glVertex3d(x - 0.5, y + 1, 0.9);
			gl.glVertex3d(x, y + 1, 0.9);
			gl.glVertex3d(x, y, 0.9);
		}
		gl.glEnd();
		gl.glPopName();
		endGUIElement(gl);
	}

}
