/**
 * 
 */
package org.caleydo.view.filterpipeline.representation;

import javax.media.opengl.GL2;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;

/**
 * @author Thomas Geymayer
 *
 */
public interface IRenderable
{
	/**
	 * Render this object to the given OpenGL2 context
	 * 
	 * @param gl
	 * @param textRenderer
	 */
	public void render(GL2 gl, CaleydoTextRenderer textRenderer);
}
