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

import org.caleydo.core.event.EventListenerManagers;
import org.caleydo.core.event.EventListenerManagers.QueuedEventListenerManager;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.core.view.opengl.layout2.basic.ScrollingDecorator.IHasMinSize;
import org.caleydo.core.view.opengl.layout2.internal.SWTLayer;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.data.loader.ResourceLocators.IResourceLocator;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

/**
 * adapter between {@link ALayoutRenderer} and {@link GLElement}, such that {@link GLElement} can be as an
 * {@link ALayoutRenderer}
 *
 * @author Samuel Gratzl
 *
 */
public final class LayoutRendererAdapter extends ALayoutRenderer implements IGLElementParent, IGLElementContext {
	/*
	 * random name for base picking type names
	 */
	private final String pickingBaseType = "BUTTON" + System.currentTimeMillis();

	private final Map<IPickingListener, PickingMetaData> pickingMetaData = new HashMap<>();
	private int pickingNameCounter = 0;

	private final QueuedEventListenerManager eventListeners;

	private final AGLView view;
	private final WindowGLElement root;

	/**
	 * do we need to perform a layout
	 */
	private boolean dirty = true;

	/**
	 * my position for {@link #getAbsoluteLocation()}
	 */
	private Vec2f location = new Vec2f(0, 0);

	private final GLContextLocal local;

	private final String eventSpace;

	public LayoutRendererAdapter(AGLView view, IResourceLocator locator, GLElement root, String eventSpace) {
		this.view = view;
		this.root = new WindowGLElement(root, view.getParentGLCanvas());
		this.eventListeners = EventListenerManagers.createQueued();
		this.eventSpace = eventSpace;

		this.local = new GLContextLocal(view.getTextureManager(), locator, view.getParentGLCanvas());

		this.root.setParent(this);
		this.root.init(this);
	}

	@Override
	public int getMinHeightPixels() {
		return (int) getMinSize().y();
	}

	/**
	 * @return
	 */
	private Vec2f getMinSize() {
		GLElement r = this.root.getRoot();
		if (r instanceof IHasMinSize)
			return ((IHasMinSize) r).getMinSize();
		return r.getLayoutDataAs(Vec2f.class, new Vec2f(0, 0));
	}

	@Override
	public int getMinWidthPixels() {
		return (int) getMinSize().x();
	}

	@Override
	public <T> T getLayoutDataAs(Class<T> clazz, T default_) {
		return getLayoutDataAs(clazz, Suppliers.ofInstance(default_));
	}

	@Override
	public <T> T getLayoutDataAs(Class<T> clazz, Supplier<? extends T> default_) {
		return GLLayouts.resolveLayoutDatas(clazz, default_, view, view.getParentGLCanvas(), this.local);
	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return false;
	}

	@Override
	public Vec2f toAbsolute(Vec2f relative) {
		relative.add(location);
		return relative;
	}

	@Override
	public Vec2f toRelative(Vec2f absolute) {
		absolute.sub(location);
		return absolute;
	}

	@Override
	protected void prepare() {
		eventListeners.processEvents();
		super.prepare();
	}

	@Override
	protected void renderContent(GL2 gl) {

		final PixelGLConverter pixelGLConverter = view.getPixelGLConverter();
		// size in pixel
		float w = pixelGLConverter.getPixelWidthForGLWidth(x);
		float h = pixelGLConverter.getPixelHeightForGLHeight(y);

		gl.glPushMatrix();
		// convert the coordinate system to
		// 0,0 w,0
		// 0,h w,h
		gl.glTranslatef(0, y, 0);
		gl.glScalef(x / w, -y / h, 1);
		float hh = view.getParentGLCanvas().getDIPHeight();

		this.location = pixelGLConverter.getCurrentPixelPos(gl);
		this.location.setY(hh - this.location.y());

		if (dirty) {
			root.setBounds(0, 0, w, h);
			root.relayout();
			dirty = false;
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

		gl.glPopMatrix();
	}

	@Override
	public final Vec2f getSize() {
		return this.root.getSize();
	}

	@Override
	public void relayout() {
		dirty = true;
	}

	@Override
	public void setLimits(float x, float y) {
		super.setLimits(x, y);
		relayout();
	}

	@Override
	public void destroy(GL2 gl) {
		this.root.takeDown();
		local.destroy(gl);
		super.destroy(gl);
	}

	@Override
	public void repaint() {
		setDisplayListDirty(true);
	}

	@Override
	public void repaintPick() {
		setDisplayListDirty(true);
	}

	@Override
	public IGLElementParent getParent() {
		return null;
	}

	@Override
	public int registerPickingListener(IPickingListener l) {
		return registerPickingListener(l, 0);
	}

	@Override
	public ISWTLayer getSWTLayer() {
		return new SWTLayer(view.getParentGLCanvas());
	}

	@Override
	public int registerPickingListener(IPickingListener l, int objectId) {
		String key = pickingBaseType + "_" + (pickingNameCounter++);
		view.addIDPickingListener(l, key, objectId);
		int id = view.getPickingManager().getPickingID(view.getID(), key, objectId);
		pickingMetaData.put(l, new PickingMetaData(id, pickingBaseType, objectId));
		return id;
	}

	private void unregisterPickingListener(IPickingListener l) {
		PickingMetaData data = pickingMetaData.remove(l);
		if (data == null)
			return;
		view.removeIDPickingListener(l, data.type, data.objectId);
	}

	@Override
	public void unregisterPickingListener(int pickingID) {
		for (Map.Entry<IPickingListener, PickingMetaData> entry : pickingMetaData.entrySet()) {
			if (entry.getValue().pickingId == pickingID) {
				unregisterPickingListener(entry.getKey());
				return;
			}
		}
	}

	@Override
	public void init(GLElement element) {
		// scan object for event listeners but only the subclasses
		eventListeners.register(element, eventSpace, AGLElementView.isNotBaseClass);
	}

	@Override
	public void takeDown(GLElement element) {
		// undo listeners
		eventListeners.unregister(element);
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
	public boolean moved(GLElement child) {
		return true;
	}
}
