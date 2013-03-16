package org.caleydo.core.view.opengl.layout2;

import gleem.linalg.Vec2f;

import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GL2;

import org.caleydo.core.event.EventListenerManager;
import org.caleydo.core.event.EventListenerManagers;
import org.caleydo.core.util.base.ILabelProvider;
import org.caleydo.core.view.ViewManager;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.PickingManager;
import org.caleydo.data.loader.ResourceLocators;
import org.caleydo.data.loader.ResourceLocators.IResourceLocator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * a {@link GLElement} based view using a {@link AGLView} for compatibility
 *
 * attention that means, that just the basic features of {@link PickingManager} are available
 *
 * @author Samuel Gratzl
 *
 */
public abstract class AGLElementGLView extends AGLView implements IGLElementContext, IGLElementParent {
	/*
	 * random name for base picking type names
	 */
	private final String pickingBaseType = "BUTTON" + System.currentTimeMillis();

	private final Map<IPickingListener, PickingMetaData> pickingMetaData = new HashMap<>();
	private int pickingNameCounter = 0;

	protected final EventListenerManager eventListeners = EventListenerManagers.wrap(this);

	private WindowGLElement root;
	private DisplayListPool pool;
	private IResourceLocator locator;

	/**
	 * do we need to perform a layout
	 */
	protected boolean isLayoutDirty = true;

	private final TimeDelta timeDelta = new TimeDelta();

	public AGLElementGLView(IGLCanvas glCanvas, Composite parentComposite, ViewFrustum viewFrustum, String viewType,
			String viewName) {
		super(glCanvas, parentComposite, viewFrustum, viewType, viewName);
	}

	/**
	 * hook for creating a custom {@link IResourceLocator}
	 *
	 * @return
	 */
	protected IResourceLocator createLocator() {
		return ResourceLocators.chain(ResourceLocators.classLoader(getClass().getClassLoader()),
				ResourceLocators.DATA_CLASSLOADER, ResourceLocators.FILE);
	}

	protected GLElement getRoot() {
		return root == null ? null : root.getRoot();
	}

	@Override
	public void init(GL2 gl) {
		this.root = new WindowGLElement(createRoot());
		this.locator = createLocator();
		this.pool = new DisplayListPool();

		this.root.setParent(this);
		this.root.init(this);
	}

	/**
	 * central hook point for creating the {@link GLElement} hierarchy
	 *
	 * @return
	 */
	protected abstract GLElement createRoot();

	@Override
	protected void initLocal(GL2 gl) {
		init(gl);
	}

	@Override
	public void initRemote(GL2 gl, AGLView glParentView, GLMouseListener glMouseListener) {
		this.glMouseListener = glMouseListener;
		init(gl);
	}

	@Override
	public void display(GL2 gl) {
		// the problem
		// if we directly set to the origin top-left coordinate system via the projection matrix, we have the problem
		// that the pixel gl converter may return negative values
		// but the gl element doesn't care as it doesn't uses the gl matrix stack for quering
		// so convert it manually here
		// we have
		// 0,h w,h
		// 0,0 w,0
		// we want
		// 0,0 w,0
		// 0,h w,h
		gl.glPushMatrix();
		gl.glTranslatef(0, viewFrustum.getTop(), 0);
		gl.glScalef(1, -1, 1);

		if (isLayoutDirty) {
			root.setBounds(0, 0, viewFrustum.getRight(), viewFrustum.getTop());
			root.relayout();
			isLayoutDirty = false;
		}

		final boolean isPickingRun = isPickingRun(gl);
		int deltaTimeMs = 0;
		if (!isPickingRun) {
			deltaTimeMs = timeDelta.getDeltaTimeMs();
		}
		GLGraphics g = new GLGraphics(gl, this.getTextRenderer(), this.getTextureManager(), locator, true, deltaTimeMs);
		g.checkError("pre render");

		if (isPickingRun(gl)) {
			// 1. pick passes
			root.renderPick(g);
		} else {
			// 2. pass layouting
			root.layout(deltaTimeMs);
			// 3. pass render pass
			root.render(g);
		}
		g.checkError("post render");
		g.destroy();

		checkForHits(gl);
		processEvents();

		gl.glPopMatrix();
	}

	@Override
	protected void updateViewFrustum(int width, int height) {
		viewFrustum.setLeft(0);
		viewFrustum.setRight(width);
		viewFrustum.setTop(height); // still wrong but with the right dimensions, see #display(GL2 gl)
		viewFrustum.setBottom(0);
		relayout();
	}

	/**
	 * are we rendering or do picking
	 *
	 * @param gl
	 * @return
	 */
	private static boolean isPickingRun(GL2 gl) {
		int[] r = new int[1];
		gl.glGetIntegerv(GL2.GL_RENDER_MODE, r, 0);
		return r[0] == GL2.GL_SELECT;
	}

	@Override
	protected void displayLocal(GL2 gl) {
		pickingManager.handlePicking(this, gl);

		if (busyState == EBusyState.ON) {
			renderBusyMode(gl);
		} else {
			display(gl);
		}
	}

	@Override
	public void displayRemote(GL2 gl) {
		display(gl);
	}

	@Override
	protected void destroyViewSpecificContent(GL2 gl) {
		this.root.takeDown();
		this.pool.deleteAll(gl);
	}

	@Override
	public final int registerPickingListener(IPickingListener l) {
		return registerPickingListener(l, 0);
	}

	@Override
	public final int registerPickingListener(IPickingListener l, int objectId) {
		String key = pickingBaseType + "_" + (pickingNameCounter++);
		this.addIDPickingListener(l, key, objectId);
		int id = this.getPickingManager().getPickingID(this.getID(), key, objectId);
		pickingMetaData.put(l, new PickingMetaData(id, pickingBaseType, objectId));
		return id;
	}


	private final void unregisterPickingListener(IPickingListener l) {
		PickingMetaData data = pickingMetaData.remove(l);
		if (data == null)
			return;
		this.removeIDPickingListener(l, data.type, data.objectId);
	}

	@Override
	public final IPickingListener createTooltip(ILabelProvider label) {
		return getParentGLCanvas().createTooltip(label);
	}

	@Override
	public final void showContextMenu(Iterable<? extends AContextMenuItem> items) {
		ViewManager.get().getCanvasFactory().showPopupMenu(getParentGLCanvas(), items);
	}

	@Override
	public final void unregisterPickingListener(int pickingID) {
		for (Map.Entry<IPickingListener, PickingMetaData> entry : pickingMetaData.entrySet()) {
			if (entry.getValue().pickingId == pickingID) {
				unregisterPickingListener(entry.getKey());
				return;
			}
		}
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();
		eventListeners.register(this);
	}

	/**
	 * final as just the {@link #eventListeners} object should be used
	 */
	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();
		eventListeners.unregisterAll();
	}

	@Override
	public final void setCursor(final int swtCursorConst) {
		final Composite c = this.getParentGLCanvas().asComposite();
		final Display d = c.getDisplay();
		d.asyncExec(new Runnable() {
			@Override
			public void run() {
				c.setCursor(swtCursorConst < 0 ? null : d.getSystemCursor(swtCursorConst));
			}
		});
	}

	@Override
	public void init(GLElement element) {
		// scan object for event listeners but only the subclasses
		eventListeners.register(element, null, AGLElementView.isNotBaseClass);
	}

	@Override
	public void takeDown(GLElement element) {
		// undo listeners
		eventListeners.unregister(element);
	}

	@Override
	public final IGLElementParent getParent() {
		return null;
	}

	@Override
	public final DisplayListPool getDisplayListPool() {
		return pool;
	}

	@Override
	public final IMouseLayer getMouseLayer() {
		return root.getMouseLayer();
	}

	@Override
	public final IPopupLayer getPopupLayer() {
		return root.getPopupLayer();
	}

	private static class PickingMetaData {
		private final int pickingId;
		private final String type;
		private final int objectId;

		public PickingMetaData(int pickingId, String type, int objectId) {
			this.pickingId = pickingId;
			this.type = type;
			this.objectId = objectId;
		}

	}

	@Override
	public final boolean moved(GLElement child) {
		return true;
	}

	@Override
	public void relayout() {
		isLayoutDirty = true;
	}

	@Override
	public void repaint() {
		setDisplayListDirty();
	}

	@Override
	public void repaintPick() {
		setDisplayListDirty();
	}

	@Override
	public Vec2f toAbsolute(Vec2f relative) {
		relative.add(new Vec2f(viewFrustum.getLeft(), viewFrustum.getBottom()));
		return relative;
	}

	@Override
	public Vec2f toRelative(Vec2f absolute) {
		absolute.sub(new Vec2f(viewFrustum.getLeft(), viewFrustum.getBottom()));
		return absolute;
	}
}
