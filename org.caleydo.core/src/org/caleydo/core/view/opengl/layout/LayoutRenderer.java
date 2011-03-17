package org.caleydo.core.view.opengl.layout;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.canvas.AGLView;

/**
 * <p>
 * Layouts (i.e {@link ElementLayout}s, {@link Column}s or {@link Row}s can have renderers associated with
 * them. These Renderers display the content in a form that has to be specified in a sub-class of this one.
 * </p>
 * <p>
 * Each Layout may have up to three renderers. One for the background, the main renderer and one for the
 * foreground, which are also rendered in this sequence.
 * </p>
 * <p>
 * There are two main ways to use renderers: either by letting a sub-part of an {@link AGLView} be rendered by
 * them. Then typically, an instance of this view is passed to a sub-class to collaborate closely (possibly
 * using package private access) with the renderer.
 * </p>
 * <p>
 * Alternatively, whole views can be rendered in a renderer. For this, use the specialized
 * {@link ViewLayoutRenderer} class.
 * </p>
 * <p>
 * Every LayoutRenderer renders from (0, 0) to (x, y). An LayoutRenderer does not take care of any spacings on
 * the sides.
 * </p>
 * 
 * @author Alexander Lex
 */
public class LayoutRenderer {
	protected float x;
	protected float y;
	protected boolean debugMode = true;

	protected ElementLayout elementLayout;

	/**
	 * To be overridden in a sub-class.
	 * 
	 * @param gl
	 */
	public void render(GL2 gl) {
	}

	/**
	 * Set the limits of this renderer. The view must render within only these limits.
	 * 
	 * @param x
	 * @param y
	 */
	protected void setLimits(float x, float y) {
		this.x = x;
		this.y = y;
	}

	/** Calculate spacing if required */
	protected void updateSpacing(ElementLayout elementLayout) {
		this.elementLayout = elementLayout;
	}

}
