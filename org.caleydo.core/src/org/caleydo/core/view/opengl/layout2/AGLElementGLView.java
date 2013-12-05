/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2;

import gleem.linalg.Vec2f;

import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GL2;

import org.caleydo.core.event.EventListenerManager;
import org.caleydo.core.event.EventListenerManagers;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.layout2.internal.SWTLayer;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.PickingManager;
import org.caleydo.data.loader.ResourceLocators;
import org.caleydo.data.loader.ResourceLocators.IResourceLocator;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

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
	private GLContextLocal local;
	private final ISWTLayer swtLayer;

	/**
	 * do we need to perform a layout
	 */
	protected boolean isLayoutDirty = true;


	public AGLElementGLView(IGLCanvas glCanvas, ViewFrustum viewFrustum, String viewType,
			String viewName) {
		super(glCanvas, viewFrustum, viewType, viewName);
		this.swtLayer = new SWTLayer(glCanvas);
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
	public final <T> T getLayoutDataAs(Class<T> clazz, T default_) {
		return getLayoutDataAs(clazz, Suppliers.ofInstance(default_));
	}

	@Override
	public <T> T getLayoutDataAs(Class<T> clazz, Supplier<? extends T> default_) {
		return GLLayouts.resolveLayoutDatas(clazz, default_, this.local);
	}

	@Override
	public void init(GL2 gl) {
		IResourceLocator locator = createLocator();
		this.local = new GLContextLocal(this.getTextRenderer(), this.getTextureManager(), locator);

		this.root = new WindowGLElement(createRoot(), getParentGLCanvas());

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
		setMouseListener(glMouseListener);
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

		final boolean isPickingRun = GLGraphics.isPickingPass(gl);
		int deltaTimeMs = 0;
		if (!isPickingRun) {
			deltaTimeMs = local.getDeltaTimeMs();
		}
		GLGraphics g = new GLGraphics(gl, local, true, deltaTimeMs);
		g.checkError("pre render");

		if (isPickingRun) {
			// 1. pick passes
			root.renderPick(g);
		} else {
			// 2. pass layouting
			root.layout(deltaTimeMs);
			// 3. pass render pass
			root.render(g);
		}
		g.checkError("post render");

		checkForHits(gl);
		processEvents();

		gl.glPopMatrix();
	}

	@Override
	protected void updateViewFrustum(float width, float height) {
		viewFrustum.setLeft(0);
		viewFrustum.setRight(width);
		viewFrustum.setTop(height); // still wrong but with the right dimensions, see #display(GL2 gl)
		viewFrustum.setBottom(0);
		relayout();
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
		local.destroy(gl);
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
	public final Vec2f getSize() {
		return root.getSize();
	}

	@Override
	public final DisplayListPool getDisplayListPool() {
		return local.getPool();
	}

	@Override
	public final IMouseLayer getMouseLayer() {
		return root.getMouseLayer();
	}

	@Override
	public final IPopupLayer getPopupLayer() {
		return root.getPopupLayer();
	}

	@Override
	public final ISWTLayer getSWTLayer() {
		return swtLayer;
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
