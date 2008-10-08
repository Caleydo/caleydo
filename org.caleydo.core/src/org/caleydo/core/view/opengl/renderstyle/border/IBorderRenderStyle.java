package org.caleydo.core.view.opengl.renderstyle.border;

import gleem.linalg.Vec4f;
import javax.media.opengl.GL;
import org.caleydo.core.view.opengl.renderstyle.border.BorderRenderStyle.BORDER;

public interface IBorderRenderStyle
{

	/* (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.miniview.slider.iBorderRenderStyle#setBorderWidth(int)
	 */
	public abstract void setBorderWidth(final int width);

	/* (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.miniview.slider.iBorderRenderStyle#setBorder(org.caleydo.core.view.opengl.miniview.slider.BorderRenderStyle.BORDER, boolean)
	 */
	public abstract void setBorder(int borderpart, boolean onoff);

	/* (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.miniview.slider.iBorderRenderStyle#setBorderColor(gleem.linalg.Vec4f)
	 */
	public abstract void setBorderColor(Vec4f color);

	/* (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.miniview.slider.iBorderRenderStyle#init(javax.media.opengl.GL)
	 */
	public abstract void init(GL gl);

	/* (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.miniview.slider.iBorderRenderStyle#display(javax.media.opengl.GL)
	 */
	public abstract void display(GL gl);

}