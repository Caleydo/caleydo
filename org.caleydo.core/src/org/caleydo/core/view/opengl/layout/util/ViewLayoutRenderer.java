package org.caleydo.core.view.opengl.layout.util;

import javax.media.opengl.GL2;
import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;

/**
 * A sub-class for {@link LayoutRenderer} intended to render whole
 * {@link AGLView}s. The main contract here, is that the view renders within its
 * view frustum, which is updated according to the size of the layout in the
 * {@link #setLimits(float, float)} method.
 * 
 * @author Alexander Lex
 */
public class ViewLayoutRenderer
	extends LayoutRenderer {

	protected AGLView view;

	/**
	 * Constructor taking an {@link AGLView} to be rendered by this renderer.
	 * 
	 * @param view
	 */
	public ViewLayoutRenderer(AGLView view) {
		this.view = view;
	}

	/**
	 * Calls the displayRemote of the view to be rendered.
	 */
	@Override
	public void render(GL2 gl) {
		super.render(gl);
		view.displayRemote(gl);
	}

	@Override
	public void setLimits(float x, float y) {
		super.setLimits(x, y);
		ViewFrustum viewFrustum = view.getViewFrustum();

		if (viewFrustum == null) {
			viewFrustum = new ViewFrustum();
			viewFrustum.setProjectionMode(CameraProjectionMode.ORTHOGRAPHIC);
		}

		viewFrustum.setLeft(0);
		viewFrustum.setBottom(0);
		viewFrustum.setRight(x);
		viewFrustum.setTop(y);
		view.setFrustum(viewFrustum);
		view.setDisplayListDirty();
	}

	/**
	 * @param view setter, see {@link #view}
	 */
	public void setView(AGLView view) {
		this.view = view;
	}

	/**
	 * @return the view, see {@link #view}
	 */
	public AGLView getView() {
		return view;
	}

	@Override
	public int getMinHeightPixels() {
		return view.getMinPixelHeight();
	}

	@Override
	public int getMinWidthPixels() {
		return view.getMinPixelWidth();
	}

}
