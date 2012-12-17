package org.caleydo.core.view.opengl.layout.util.multiform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.media.opengl.GL2;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.IRemoteViewCreator;
import org.caleydo.core.view.ViewManager;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.AForwardingRenderer;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.layout.util.ViewLayoutRenderer;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;

/**
 * Renderer that allows to switch between different remotely rendered {@link AGLView}s or {@link LayoutRenderer}s.
 * Remotely rendered views are integrated into this renderer using plugin mechanism of extension point
 * <code>org.caleydo.view.EmbeddedView</code>.
 *
 * @author Christian Partl
 *
 */
public class MultiFormRenderer extends AForwardingRenderer implements IEmbeddedVisualizationInfo {

	/**
	 * Renderer that is used to render active remote views.
	 */
	private ViewLayoutRenderer viewRenderer = new ViewLayoutRenderer();

	/**
	 * The view that displays this renderer. If plugin views are to be displayed in this multiform renderer, this view
	 * must be the view that offers the plugins.
	 */
	private final AGLView remoteRenderingView;

	/**
	 * Determines whether the views added to this renderer are immediately created or not until set used the first time.
	 */
	private final boolean isLazyViewCreation;

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
	 * The default {@link ARendererInfo}. The associated renderer is set active by default. If multiple renderers claim
	 * to be the default renderer, the last of them that was added renderer is default. If no renderer claims to be
	 * default, the first added renderer is default.
	 */
	private ARendererInfo defaultRendererInfo;

	/**
	 * Set of {@link IMultiFormChangeListener}s that are informed, when this multiform renderer changes.
	 */
	private Set<IMultiFormChangeListener> changeListeners = new HashSet<>();

	/**
	 * Abstract base class for renderer information that is used by {@link MultiFormRenderer}.
	 *
	 * @author Christian Partl
	 *
	 */
	private abstract class ARendererInfo {

		/**
		 * ID used by {@link MultiFormRenderer} to identify the different renderers.
		 */
		protected final int rendererID;

		/**
		 * Path to the icon file that is used to represent the renderer.
		 */
		protected final String iconPath;

		/**
		 * Determines, whether the associated renderer is active.
		 */
		protected boolean isActive = false;

		/**
		 * Info about different aspects of the embedded visualization.
		 */
		protected final IEmbeddedVisualizationInfo visInfo;

		protected ARendererInfo(int rendererID, String iconPath, IEmbeddedVisualizationInfo visInfo) {
			this.rendererID = rendererID;
			this.iconPath = iconPath;
			this.visInfo = visInfo;
		}

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
		private final String viewID;
		/**
		 * ID that is used to identify the appropriate {@link IRemoteViewCreator} for this view.
		 */
		private final String embeddingID;
		/**
		 * Table perspectives that shall be displayed by the view.
		 */
		private final List<TablePerspective> tablePerspectives;
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

		/**
		 * @param rendererID
		 * @param iconPath
		 * @param visInfo
		 */
		protected ViewInfo(int rendererID, String iconPath, IEmbeddedVisualizationInfo visInfo, String viewID,
				String embeddingID, List<TablePerspective> tablePerspectives) {
			super(rendererID, iconPath, visInfo);
			this.viewID = viewID;
			this.embeddingID = embeddingID;
			this.tablePerspectives = tablePerspectives;

			// TODO Auto-generated constructor stub
		}

		@Override
		void setActive() {
			if (view == null) {
				createView();
			}
			viewRenderer.setView(view);
			currentRenderer = viewRenderer;
			if (!isInitialized) {
				view.initRemote(remoteRenderingView.getParentGLCanvas().asGLAutoDrawAble().getGL().getGL2(),
						remoteRenderingView, remoteRenderingView.getGLMouseListener());
				isInitialized = true;
			}
			currentRenderer.setLimits(x, y);
			MultiFormRenderer.this.isDisplayListDirty = true;
			currentRenderer.setDisplayListDirty();
			isActive = true;
		}

		/**
		 * Creates a view using the given view info.
		 *
		 * @param viewInfo
		 * @return
		 */
		void createView() {
			view = ViewManager.get()
					.createRemotePlugInView(viewID, embeddingID, remoteRenderingView, tablePerspectives);
		}

		@Override
		void prepareRenderer(GL2 gl) {
			// if (!isInitialized) {
			// view.initRemote(gl, remoteRenderingView, remoteRenderingView.getGLMouseListener());
			// isInitialized = true;
			// }
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
		private final LayoutRenderer renderer;

		/**
		 * @param rendererID
		 * @param iconPath
		 * @param visInfo
		 */
		protected LayoutRendererInfo(int rendererID, String iconPath, IEmbeddedVisualizationInfo visInfo,
				LayoutRenderer renderer) {
			super(rendererID, iconPath, visInfo);
			this.renderer = renderer;
		}

		@Override
		void setActive() {
			currentRenderer = renderer;
			currentRenderer.setLimits(x, y);
			MultiFormRenderer.this.isDisplayListDirty = true;
			currentRenderer.setDisplayListDirty();
			isActive = true;
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
	 *            List of tablePerspectives that shall be displayed in the view. If the view only supports a single
	 *            table perspective, the first of the list is used.
	 * @return Identifier for the currently added view that can be used to set it active ({@link #setActive(int)}) or
	 *         remove.
	 */
	public int addView(String viewID, String embeddingID, List<TablePerspective> tablePerspectives) {

		String iconPath = ViewManager.get().getRemotePlugInViewIcon(viewID, remoteRenderingView.getViewType(),
				embeddingID);
		if (iconPath == null) {
			iconPath = EIconTextures.NO_ICON_AVAILABLE.getFileName();
		}
		int rendererID = currentMaxRendererID++;

		IEmbeddedVisualizationInfo visInfo = ViewManager.get().getEmbeddedVisualizationInfoOfPluginView(viewID,
				embeddingID, remoteRenderingView.getViewType());
		ViewInfo info = new ViewInfo(rendererID, iconPath, visInfo, viewID, embeddingID, tablePerspectives);
		if (!isLazyViewCreation) {
			info.createView();
		}

		if (ViewManager.get().isPlugInViewDefault(viewID, remoteRenderingView.getViewType(), embeddingID)) {
			defaultRendererInfo = info;
		}

		rendererInfos.put(rendererID, info);

		notifyAdded(rendererID);

		return rendererID;
	}

	/**
	 * Adds a {@link LayoutRenderer} to this {@link MultiFormRenderer}.
	 *
	 * @param renderer
	 *            The renderer to be added.
	 * @param iconPath
	 *            Path to the image file that shall be used for an iconic representation of the renderer. If null is
	 *            specified, a default icon will be used.
	 * @param visInfo
	 *            Provides embedding information about the renderer to be added.
	 * @param isDefaultRenderer
	 *            Determines whether the renderer should be default. The default renderer is set active automatically,
	 *            if no renderer was set active explicitly.
	 * @return Identifier for the currently added renderer that can be used to set it active ({@link #setActive(int)})
	 *         or remove.
	 */
	public int addLayoutRenderer(LayoutRenderer renderer, String iconPath, IEmbeddedVisualizationInfo visInfo,
			boolean isDefaultRenderer) {

		int rendererID = currentMaxRendererID++;
		LayoutRendererInfo info = new LayoutRendererInfo(rendererID,
				iconPath == null ? EIconTextures.NO_ICON_AVAILABLE.getFileName() : iconPath, visInfo, renderer);
		rendererInfos.put(rendererID, info);

		if (isDefaultRenderer) {
			defaultRendererInfo = info;
		}

		notifyAdded(rendererID);

		return rendererID;
	}

	/**
	 * Removes a renderer specified by its ID.
	 *
	 * @param rendererID
	 *            ID of the renderer.
	 * @param destroy
	 *            If true, the renderer will be destroyed.
	 */
	public void removeRenderer(int rendererID, boolean destroy) {

		ARendererInfo info = rendererInfos.get(rendererID);
		if (info == null)
			return;

		rendererInfos.remove(rendererID);
		GL2 gl = remoteRenderingView.getParentGLCanvas().asGLAutoDrawAble().getGL().getGL2();
		if (info instanceof ViewInfo) {
			AGLView view = ((ViewInfo) info).view;
			if (view != null) {
				// GeneralManager.get().getViewManager().unregisterGLView(view);
				GeneralManager.get().getViewManager().destroyView(gl, view);
			}
		} else {

			((LayoutRendererInfo) info).renderer.destroy(gl);
		}
		if (info == currentRendererInfo) {
			currentRendererInfo = null;
		}
		if (info == defaultRendererInfo) {
			defaultRendererInfo = null;
		}

		notifyRemoved(rendererID);
	}

	/**
	 * Gets the file path of the icon that is associated with the {@link AGLView} or {@link LayoutRenderer} specified by
	 * the provided renderer ID.
	 *
	 * @param rendererID
	 *            ID that specifies the view or renderer.
	 * @return File path of the associated Icon. Null, if no renderer or view is associated with the specified ID.
	 */
	public String getIconPath(int rendererID) {
		ARendererInfo info = rendererInfos.get(rendererID);
		if (info != null) {
			return info.iconPath;
		}
		return null;
	}

	/**
	 * @return The renderer ID of the currently active renderer. -1, if no renderer has been set active.
	 */
	public int getActiveRendererID() {
		return (currentRendererInfo != null && currentRendererInfo.isActive) ? currentRendererInfo.rendererID : -1;
	}

	/**
	 * Ensures,if possible, that there is a valid renderer ready to display content.
	 *
	 * @return True, if a valid renderer could be set, false otherwise.
	 */
	private boolean ensureValidRenderer() {

		if (currentRendererInfo == null) {
			if (determineDefaultRendererInfo()) {
				currentRendererInfo = defaultRendererInfo;
			} else {
				return false;
			}
		}

		if (!currentRendererInfo.isActive) {
			currentRendererInfo.setActive();
			notifyActive(currentRendererInfo.rendererID, -1);
		}

		return true;
	}

	/**
	 * Determines {@link #defaultRendererInfo}.
	 *
	 * @return True, if a default could be determined, false otherwise.
	 */
	private boolean determineDefaultRendererInfo() {
		if (defaultRendererInfo != null)
			return true;

		List<Integer> idList = new ArrayList<>(rendererInfos.keySet());
		if (idList.size() > 0) {
			Collections.sort(idList);
			defaultRendererInfo = rendererInfos.get(idList.get(idList.size() - 1));
		} else {
			return false;
		}

		return true;
	}

	/**
	 * Obtains the ID of the current default renderer. The default renderer is set active automatically, if no renderer
	 * was set active explicitly. The last renderer that was added as default (either via extension point
	 * <code>org.caleydo.view.EmbeddedView</code> for plugin views, or via
	 * {@link #addLayoutRenderer(LayoutRenderer, String, IEmbeddedVisualizationInfo, boolean)}) is default. If no
	 * renderer was added as default, the first added renderer is default. It is possible to change the default renderer
	 * using {@link #setDefaultRenderer(int)}.
	 *
	 * @return The id of the default renderer. -1 if no default renderer could be determined.
	 */
	public int getDefaultRendererID() {
		if (determineDefaultRendererInfo())
			return defaultRendererInfo.rendererID;
		return -1;
	}

	/**
	 * Sets the default renderer for this multiform renderer.The default renderer is set active automatically, if no
	 * renderer was set active explicitly.
	 *
	 * @param rendererID
	 *            ID of the renderer to set default.
	 */
	public void setDefaultRenderer(int rendererID) {
		ARendererInfo info = rendererInfos.get(rendererID);
		if (info != null) {
			defaultRendererInfo = info;
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
			int previousRendererID = currentRendererInfo != null ? currentRendererInfo.rendererID : -1;
			if (currentRendererInfo != null) {
				previousRendererID = currentRendererInfo.rendererID;
				currentRendererInfo.isActive = false;
			}
			currentRendererInfo = info;
			info.setActive();
			notifyActive(rendererID, previousRendererID);
		}
	}

	/**
	 * Gets the {@link AGLView} associated with the provided rendererID.
	 *
	 * @param rendererID
	 *            Identifier that specifies the view.
	 * @return The view that is associated with the specified ID. Null, if no view corresponds to this ID.
	 */
	public AGLView getView(int rendererID) {
		ARendererInfo info = rendererInfos.get(rendererID);
		if (info == null)
			return null;

		if (info instanceof ViewInfo) {
			ViewInfo viewInfo = (ViewInfo) info;
			if (viewInfo.view == null) {
				viewInfo.createView();
			}
			return viewInfo.view;
		}
		return null;
	}

	/**
	 * Gets the {@link LayoutRenderer} associated with the provided rendererID.
	 *
	 * @param rendererID
	 *            Identifier that specifies the layout.
	 * @return The renderer that is associated with the specified ID. Null, if no renderer corresponds to this ID.
	 */
	public LayoutRenderer getLayoutRenderer(int rendererID) {
		ARendererInfo info = rendererInfos.get(rendererID);
		if (info == null)
			return null;

		if (info instanceof LayoutRendererInfo) {
			LayoutRendererInfo layoutRendererInfo = (LayoutRendererInfo) info;
			return layoutRendererInfo.renderer;
		}
		return null;
	}

	/**
	 * @return The ids of all {@link AGLView}s and {@link LayoutRenderer}s added.
	 */
	public Set<Integer> getRendererIDs() {
		return new HashSet<>(rendererInfos.keySet());
	}

	/**
	 * @param rendererID
	 * @return True, if an {@link AGLView} is associated with the provided ID.
	 */
	public boolean isView(int rendererID) {
		ARendererInfo info = rendererInfos.get(rendererID);
		if (info == null)
			return false;
		return (info instanceof ViewInfo);
	}

	/**
	 * @param rendererID
	 * @return True, if a {@link LayoutRenderer} is associated with the provided ID.
	 */
	public boolean isLayoutRenderer(int rendererID) {
		ARendererInfo info = rendererInfos.get(rendererID);
		if (info == null)
			return false;
		return (info instanceof LayoutRendererInfo);
	}

	@Override
	protected void renderContent(GL2 gl) {
		if (!ensureValidRenderer())
			return;
		currentRendererInfo.prepareRenderer(gl);
		super.renderContent(gl);
	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		if (!ensureValidRenderer())
			return false;
		return super.permitsWrappingDisplayLists();
	}

	@Override
	protected void prepare() {
		if (!ensureValidRenderer())
			return;
		super.setDisplayListDirty();
	}

	@Override
	public void setDisplayListDirty() {
		if (!ensureValidRenderer())
			return;
		super.setDisplayListDirty();
	}

	@Override
	public boolean isDisplayListDirty() {
		if (!ensureValidRenderer())
			return false;
		return super.isDisplayListDirty();
	}

	@Override
	public void setLimits(float x, float y) {
		if (!ensureValidRenderer())
			return;
		super.setLimits(x, y);
	}

	@Override
	public int getMinHeightPixels() {
		if (!ensureValidRenderer())
			return 0;
		return currentRenderer.getMinHeightPixels();
	}

	@Override
	public int getMinWidthPixels() {
		if (!ensureValidRenderer())
			return 0;
		return currentRenderer.getMinWidthPixels();
	}

	@Override
	protected void setElementLayout(ElementLayout elementLayout) {
		if (!ensureValidRenderer())
			return;
		super.setElementLayout(elementLayout);
	}

	/**
	 * @return the isLazyViewCreation, see {@link #isLazyViewCreation}
	 */
	public boolean isLazyViewCreation() {
		return isLazyViewCreation;
	}

	/**
	 * Adds a {@link IMultiFormChangeListener} to this {@link MultiFormRenderer}, if it is not already added.
	 *
	 * @param listener
	 */
	public void addChangeListener(IMultiFormChangeListener listener) {
		changeListeners.add(listener);
	}

	/**
	 * Removes a {@link IMultiFormChangeListener} from this {@link MultiFormRenderer}.
	 *
	 * @param listener
	 */
	public void removeChangeListener(IMultiFormChangeListener listener) {
		changeListeners.remove(listener);
	}

	/**
	 * Notifies all registered {@link IMultiFormChangeListener}s of the change of the currently active renderer.
	 *
	 * @param currentRendererID
	 *            ID of the renderer that is now set active.
	 * @param previousRendererID
	 *            ID of the renderer that was set active before. -1 if no renderer was active before.
	 */
	protected void notifyActive(int currentRendererID, int previousRendererID) {
		for (IMultiFormChangeListener listener : changeListeners) {
			listener.activeRendererChanged(this, currentRendererID, previousRendererID);
		}
	}

	/**
	 * Notifies all registered {@link IMultiFormChangeListener}s of the added renderer.
	 *
	 * @param rendererID
	 *            ID of the renderer that was added.
	 */
	protected void notifyAdded(int rendererID) {
		for (IMultiFormChangeListener listener : changeListeners) {
			listener.rendererAdded(this, rendererID);
		}
	}

	/**
	 * Notifies all registered {@link IMultiFormChangeListener}s of the removed renderer.
	 *
	 * @param rendererID
	 *            ID of the renderer that was removed.
	 */
	protected void notifyRemoved(int rendererID) {
		for (IMultiFormChangeListener listener : changeListeners) {
			listener.rendererRemoved(this, rendererID);
		}
	}

	@Override
	public void destroy(GL2 gl) {
		super.destroy(gl);
		for (ARendererInfo info : rendererInfos.values()) {
			if (info instanceof ViewInfo) {
				AGLView view = ((ViewInfo) info).view;
				if (view != null) {
					GeneralManager.get().getViewManager().destroyView(gl, view);

				}
			} else {
				LayoutRenderer renderer = ((LayoutRendererInfo) info).renderer;
				// The current renderer has already been destroyed by super
				if (currentRenderer != renderer) {
					((LayoutRendererInfo) info).renderer.destroy(gl);
				}
			}
		}

		// The view renderer has already been destroyed by super
		if (currentRenderer != viewRenderer) {
			viewRenderer.destroy(gl);
		}

		for (IMultiFormChangeListener listener : changeListeners) {
			listener.destroyed(this);
		}
		changeListeners.clear();
	}

	@Override
	public EScalingEntity getPrimaryWidthScalingEntity() {
		if (!ensureValidRenderer())
			return null;
		return currentRendererInfo.visInfo.getPrimaryWidthScalingEntity();
	}

	@Override
	public EScalingEntity getPrimaryHeightScalingEntity() {
		if (!ensureValidRenderer())
			return null;
		return currentRendererInfo.visInfo.getPrimaryHeightScalingEntity();
	}
}
