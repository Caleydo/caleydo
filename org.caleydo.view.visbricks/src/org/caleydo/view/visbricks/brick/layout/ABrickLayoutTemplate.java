package org.caleydo.view.visbricks.brick.layout;

import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.layout.LayoutTemplate;
import org.caleydo.view.visbricks.brick.GLBrick;

/**
 * Subclasses of this class are intended to specify the elements and their
 * layout for different appearences of a brick.
 * 
 * @author Christian Partl
 * 
 */
public abstract class ABrickLayoutTemplate extends LayoutTemplate {

	protected GLBrick brick;
	protected LayoutRenderer viewRenderer;
	protected boolean showHandles;

	public ABrickLayoutTemplate(GLBrick brick) {
		this.brick = brick;
		showHandles = false;
		setPixelGLConverter(brick.getParentGLCanvas().getPixelGLConverter());
		registerPickingListeners();
	}

	/**
	 * Sets the renderer for the view element of a brick.
	 * 
	 * @param viewRenderer
	 */
	public void setViewRenderer(LayoutRenderer viewRenderer) {
		this.viewRenderer = viewRenderer;
	}

	/**
	 * @return True, if handles are shown.
	 */
	public boolean isShowHandles() {
		return showHandles;
	}

	/**
	 * Sets whether handles shall be shown.
	 * 
	 * @param showHandles
	 */
	public void setShowHandles(boolean showHandles) {
		this.showHandles = showHandles;
	}

	/**
	 * Registers PickingListeners. Automatically called upon Object creation.
	 */
	protected abstract void registerPickingListeners();

	/**
	 * @return Minimum height in pixels required by the brick with the current layout and view
	 */
	public abstract int getMinHeightPixels();

	/**
	 * @return Minimum width in pixels required by the brick with the current layout and view
	 */
	public abstract int getMinWidthPixels();

}
