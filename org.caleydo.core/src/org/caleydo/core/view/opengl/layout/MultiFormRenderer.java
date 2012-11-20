package org.caleydo.core.view.opengl.layout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.media.opengl.GL2;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.util.ViewLayoutRenderer;

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
	 * Renderer that is used to render active remote views.
	 */
	private ViewLayoutRenderer viewRenderer = new ViewLayoutRenderer();

	/**
	 * The view that displays this renderer in its own canvas.
	 */
	private AGLView remoteRenderingView;

	/**
	 * Determines whether the views added to this renderer are immediately
	 * created or not until set used the first time.
	 */
	private boolean isLazyViewCreation;

	/**
	 * Abstract base class for renderer information that is used by
	 * {@link MultiFormRenderer}.
	 * 
	 * @author Christian Partl
	 * 
	 */
	private abstract class ARendererInfo {

		/**
		 * Performs all necessary operations to set the associated rendering
		 * entity active in {@link MultiFormRenderer}.
		 */
		abstract void setActive();

		/**
		 * Called in every render cycle to prepare the renderer.
		 * 
		 * @param gl
		 */
		abstract void prepareRenderer(GL2 gl);
	}

	/**
	 * Info that holds necessary information for remote rendered views.
	 * 
	 * @author Christian Partl
	 */
	private class ViewInfo extends ARendererInfo {
		/**
		 * ID of the view type.
		 */
		private String viewID;
		/**
		 * Table perspectives that shall be displayed by the view.
		 */
		private List<TablePerspective> tablePerspectives;
		/**
		 * The view.
		 */
		private AGLView view;

		/**
		 * Determines whether
		 * {@link AGLView#initRemote(GL2, AGLView, org.caleydo.core.view.opengl.mouse.GLMouseListener)}
		 * has already been called.
		 */
		private boolean isInitialized = false;

		@Override
		void setActive() {
			if (view == null) {
				view = createView(this);
			}
			viewRenderer.setView(view);
			currentRenderer = viewRenderer;
			currentRenderer.setLimits(x, y);
			setDisplayListDirty();
		}

		@Override
		void prepareRenderer(GL2 gl) {
			if (!isInitialized) {
				view.initRemote(gl, remoteRenderingView,
						remoteRenderingView.getGLMouseListener());
				isInitialized = true;
			}
		}
	}

	/**
	 * Info that holds necessary information for {@link LayoutRenderer}s.
	 * 
	 * @author Christian Partl
	 */
	private class LayoutRendererInfo extends ARendererInfo {
		/**
		 * The renderer.
		 */
		private LayoutRenderer renderer;

		@Override
		void setActive() {
			currentRenderer = renderer;
			currentRenderer.setLimits(x, y);
			setDisplayListDirty();
		}

		@Override
		void prepareRenderer(GL2 gl) {
			// nothing to do
		}
	}

	/**
	 * Map that stores all {@link ARendererInfo}s and associates it with an
	 * identifier.
	 */
	private Map<Integer, ARendererInfo> rendererInfos = new HashMap<>();

	/**
	 * The highest id of a {@link ARendererInfo} that is currently stored in
	 * {@link #rendererInfos}.
	 */
	private int currentMaxRendererID = 0;

	public MultiFormRenderer(AGLView remoteRenderingView,
			boolean isLazyViewCreation) {
		this.remoteRenderingView = remoteRenderingView;
		this.isLazyViewCreation = isLazyViewCreation;
	}

	/**
	 * Adds a view to this {@link MultiFormRenderer}. Depending on whether lazy
	 * view creation is being used, the view is created immediately or the first
	 * time it is used.
	 * 
	 * @param viewID
	 *            ID specifying the view type.
	 * @param tablePerspectives
	 *            List of tablePerspectives that shall be displayed in the view.
	 * @return Identifier for the currently added view that can be used to set
	 *         it active ({@link #setActive(int)}).
	 */
	public int addView(String viewID, List<TablePerspective> tablePerspectives) {

		ViewInfo info = new ViewInfo();
		info.viewID = viewID;
		info.tablePerspectives = tablePerspectives;

		if (!isLazyViewCreation) {
			AGLView view = createView(info);
			info.view = view;
		}
		int rendererID = currentMaxRendererID++;
		rendererInfos.put(rendererID, info);

		return rendererID;
	}

	/**
	 * Adds a {@link LayoutRenderer} to this {@link MultiFormRenderer}.
	 * 
	 * @param renderer
	 *            The renderer to be added.
	 * @return Identifier for the currently added renderer that can be used to
	 *         set it active ({@link #setActive(int)}).
	 */
	public int addLayoutRenderer(LayoutRenderer renderer) {
		LayoutRendererInfo info = new LayoutRendererInfo();
		info.renderer = renderer;

		int rendererID = currentMaxRendererID++;
		rendererInfos.put(rendererID, info);

		return rendererID;
	}

	/**
	 * Sets a {@link AGLView} or {@link LayoutRenderer} previously added to this
	 * {@link MultiFormRenderer} active, so that it will be rendered. If the
	 * specified identifier is invalid, no operation is performed.
	 * 
	 * @param rendererID
	 *            Identifier that specifies a view or layout renderer.
	 */
	public void setActive(int rendererID) {
		ARendererInfo info = rendererInfos.get(rendererID);
		if (info != null)
			info.setActive();
	}

	private AGLView createView(ViewInfo viewInfo) {
		// TODO: implement
		return null;
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

	public boolean isLazyViewCreation() {
		return isLazyViewCreation;
	}

	public void setLazyViewCreation(boolean isLazyViewCreation) {
		this.isLazyViewCreation = isLazyViewCreation;
	}

}
