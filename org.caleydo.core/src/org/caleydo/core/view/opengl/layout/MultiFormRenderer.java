package org.caleydo.core.view.opengl.layout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.media.opengl.GL2;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.IRemoteViewCreator;
import org.caleydo.core.view.ViewManager;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.util.ViewLayoutRenderer;

/**
 * Renderer that allows to switch between different remotely rendered {@link AGLView}s or {@link LayoutRenderer}s and
 * handles remote view creation.
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
	 * Determines whether the views added to this renderer are immediately created or not until set used the first time.
	 */
	private boolean isLazyViewCreation;

	/**
	 * Map that stores all {@link ARendererInfo}s and associates it with an identifier.
	 */
	private Map<Integer, ARendererInfo> rendererInfos = new HashMap<>();

	/**
	 * The highest id of a {@link ARendererInfo} that is currently stored in {@link #rendererInfos}.
	 */
	private int currentMaxRendererID = 0;

	/**
	 * {@link ARendererInfo} that is currently active and whose renderer is therefore displayed.
	 */
	private ARendererInfo currentRendererInfo;

	/**
	 * Determines whether a default renderer is currently used without setting a renderer active.
	 */
	private boolean isDefaultRenderer = true;

	/**
	 * Abstract base class for renderer information that is used by {@link MultiFormRenderer}.
	 *
	 * @author Christian Partl
	 *
	 */
	private abstract class ARendererInfo {

		/**
		 * Performs all necessary operations to set the associated rendering entity active in {@link MultiFormRenderer}.
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
		 * ID that is used to identify the appropriate {@link IRemoteViewCreator} for this view.
		 */
		private String embeddingID;
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
		 * {@link AGLView#initRemote(GL2, AGLView, org.caleydo.core.view.opengl.mouse.GLMouseListener)} has already been
		 * called.
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
			MultiFormRenderer.this.isDisplayListDirty = true;
			currentRenderer.setDisplayListDirty();
		}

		@Override
		void prepareRenderer(GL2 gl) {
			if (!isInitialized) {
				view.initRemote(gl, remoteRenderingView, remoteRenderingView.getGLMouseListener());
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
			MultiFormRenderer.this.isDisplayListDirty = true;
			currentRenderer.setDisplayListDirty();
		}

		@Override
		void prepareRenderer(GL2 gl) {
			// nothing to do
		}
	}

	public MultiFormRenderer(AGLView remoteRenderingView, boolean isLazyViewCreation) {
		this.remoteRenderingView = remoteRenderingView;
		this.isLazyViewCreation = isLazyViewCreation;
	}

	/**
	 * Adds a view to this {@link MultiFormRenderer}. Depending on whether lazy view creation is being used, the view is
	 * created immediately or the first time it is used.
	 *
	 * @param viewID
	 *            ID specifying the view type.
	 * @param embeddingID
	 *            ID that specifies the embedding in the parent view. This ID is used to determine the appropriate
	 *            {@link IRemoteViewCreator} for the embedded view.
	 * @param tablePerspectives
	 *            List of tablePerspectives that shall be displayed in the view.
	 * @return Identifier for the currently added view that can be used to set it active ({@link #setActive(int)}).
	 */
	public int addView(String viewID, String embeddingID, List<TablePerspective> tablePerspectives) {

		ViewInfo info = new ViewInfo();
		info.viewID = viewID;
		info.embeddingID = embeddingID;
		info.tablePerspectives = tablePerspectives;

		if (!isLazyViewCreation) {
			info.view = createView(info);
		}
		int rendererID = currentMaxRendererID++;
		rendererInfos.put(rendererID, info);

		// Set default renderer and info in case no activate is performed
		if (currentRendererInfo == null) {
			setDefaultRenderer(info, viewRenderer);
		}

		return rendererID;
	}

	/**
	 * Adds a {@link LayoutRenderer} to this {@link MultiFormRenderer}.
	 *
	 * @param renderer
	 *            The renderer to be added.
	 * @return Identifier for the currently added renderer that can be used to set it active ({@link #setActive(int)}).
	 */
	public int addLayoutRenderer(LayoutRenderer renderer) {
		LayoutRendererInfo info = new LayoutRendererInfo();
		info.renderer = renderer;

		int rendererID = currentMaxRendererID++;
		rendererInfos.put(rendererID, info);

		// Set default renderer and info in case no activate is performed
		if (currentRendererInfo == null) {
			setDefaultRenderer(info, renderer);
		}

		return rendererID;
	}

	private void setDefaultRenderer(ARendererInfo info, LayoutRenderer renderer) {
		isDefaultRenderer = true;
		currentRendererInfo = info;
		currentRenderer = renderer;
	}

	private void setDefaultRendererActive() {
		if (isDefaultRenderer) {
			currentRendererInfo.setActive();
			isDefaultRenderer = false;
		}
	}

	/**
	 * Sets a {@link AGLView} or {@link LayoutRenderer} previously added to this {@link MultiFormRenderer} active, so
	 * that it will be rendered. If the specified identifier is invalid, no operation is performed.
	 *
	 * @param rendererID
	 *            Identifier that specifies a view or layout renderer.
	 */
	public void setActive(int rendererID) {
		ARendererInfo info = rendererInfos.get(rendererID);
		if (info != null) {
			currentRendererInfo = info;
			info.setActive();
			isDefaultRenderer = false;
		}
	}

	/**
	 * Creates a view using the given view info.
	 *
	 * @param viewInfo
	 * @return
	 */
	private AGLView createView(ViewInfo viewInfo) {
		return ViewManager.get().createRemotePlugInView(viewInfo.viewID, viewInfo.embeddingID, remoteRenderingView,
				viewInfo.tablePerspectives);
	}

	@Override
	protected void renderContent(GL2 gl) {
		setDefaultRendererActive();
		currentRendererInfo.prepareRenderer(gl);
		currentRenderer.renderContent(gl);
	}

	@Override
	protected boolean permitsDisplayLists() {
		setDefaultRendererActive();
		return currentRenderer.permitsDisplayLists();
	}

	@Override
	protected void prepare() {
		setDefaultRendererActive();
		currentRenderer.prepare();
		if (currentRenderer.isDisplayListDirty)
			setDisplayListDirty();
	}

	@Override
	public void setDisplayListDirty() {
		super.setDisplayListDirty();
		setDefaultRendererActive();
		currentRenderer.setDisplayListDirty();
	}

	@Override
	public void setLimits(float x, float y) {
		setDefaultRendererActive();
		currentRenderer.setLimits(x, y);
	}

	@Override
	public int getMinHeightPixels() {
		setDefaultRendererActive();
		return currentRenderer.getMinHeightPixels();
	}

	@Override
	public int getMinWidthPixels() {
		setDefaultRendererActive();
		return currentRenderer.getMinWidthPixels();
	}

	@Override
	void setElementLayout(ElementLayout elementLayout) {
		super.setElementLayout(elementLayout);
		setDefaultRendererActive();
		currentRenderer.setElementLayout(elementLayout);
	}

	/**
	 * @return the isLazyViewCreation, see {@link #isLazyViewCreation}
	 */
	public boolean isLazyViewCreation() {
		return isLazyViewCreation;
	}

	/**
	 * @param isLazyViewCreation
	 *            setter, see {@link isLazyViewCreation}
	 */
	public void setLazyViewCreation(boolean isLazyViewCreation) {
		this.isLazyViewCreation = isLazyViewCreation;
	}
}
