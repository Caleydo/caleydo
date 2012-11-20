package org.caleydo.core.view.opengl.layout;

import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GL2;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.opengl.canvas.AGLView;

/**
 * Renderer that allows to switch between different {@link AGLView}s or
 * {@link LayoutRenderer}s.
 * 
 * @author Christian Partl
 * 
 */
public class MultiFormRenderer extends LayoutRenderer {

	/**
	 * The renderer that is currently active.
	 */
	private LayoutRenderer currentRenderer;

	/**
	 * Map containing all views that can be rendered remote.
	 */
	private Map<String, AGLView> views = new HashMap<>();

	/**
	 * The view that displays this renderer in its own canvas.
	 */
	private AGLView remoteRenderingView;

	public MultiFormRenderer(AGLView remoteRenderingView) {
		this.remoteRenderingView = remoteRenderingView;
	}

	public void addView(String viewID, TablePerspective tablePerspective) {

	}

	@Override
	protected void renderContent(GL2 gl) {
		currentRenderer.renderContent(gl);
	}

	@Override
	protected boolean permitsDisplayLists() {
		return currentRenderer.permitsDisplayLists();
	}

	@Override
	protected void prepare() {
		currentRenderer.prepare();
		if (currentRenderer.isDisplayListDirty)
			setDisplayListDirty();
	}

	@Override
	public void setDisplayListDirty() {
		currentRenderer.setDisplayListDirty();
	}

	@Override
	public void setLimits(float x, float y) {
		currentRenderer.setLimits(x, y);
	}

	@Override
	public int getMinHeightPixels() {
		return currentRenderer.getMinHeightPixels();
	}

	@Override
	public int getMinWidthPixels() {
		return currentRenderer.getMinWidthPixels();
	}

	@Override
	void setElementLayout(ElementLayout elementLayout) {
		super.setElementLayout(elementLayout);
		currentRenderer.setElementLayout(elementLayout);
	}
	

}
