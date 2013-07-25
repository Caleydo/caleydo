/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2;

import gleem.linalg.Vec2f;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.fixedfunc.GLMatrixFunc;

import org.caleydo.core.event.EventListenerManagers;
import org.caleydo.core.event.EventListenerManagers.QueuedEventListenerManager;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.AView;
import org.caleydo.core.view.ViewManager;
import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.canvas.IGLView;
import org.caleydo.core.view.opengl.layout2.animation.AnimatedGLElementContainer;
import org.caleydo.core.view.opengl.layout2.internal.SWTLayer;
import org.caleydo.core.view.opengl.layout2.util.GLSanityCheck;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.SimplePickingManager;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.data.loader.ResourceLocators;
import org.caleydo.data.loader.ResourceLocators.IResourceLocator;

import com.google.common.base.Predicate;

/**
 * a {@link IGLView} based on {@link GLElement}s its NOT a {@link AGLView}
 *
 * @author Samuel Gratzl
 *
 */
public abstract class AGLElementView extends AView implements IGLView, GLEventListener, IGLElementContext,
		IGLElementParent {
	protected final IGLCanvas canvas;

	protected final QueuedEventListenerManager eventListeners = EventListenerManagers.createQueued();

	private final SimplePickingManager pickingManager = new SimplePickingManager();

	private final ViewFrustum viewFrustum = new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC, 0, 100, 100, 0, -20, 20);

	private WindowGLElement root;
	private boolean dirtyLayout = true;

	private boolean visible = true;
	private GLContextLocal local;
	private final ISWTLayer swtLayer;

	public AGLElementView(IGLCanvas glCanvas, String viewType, String viewName) {
		super(viewType,
				viewName);
		this.canvas = glCanvas;
		this.swtLayer = new SWTLayer(glCanvas);
		this.canvas.addGLEventListener(this);
		this.canvas.addMouseListener(pickingManager.getListener());
	}

	protected final GLElement getRoot() {
		if (root == null)
			return null;
		return root.getRoot();
	}

	@Override
	public final IGLElementParent getParent() {
		return null;
	}

	@Override
	public final IGLCanvas getParentGLCanvas() {
		return canvas;
	}

	@Override
	public final void setVisible(boolean visible) {
		if (this.visible == visible)
			return;
		this.visible = visible;
		if (local != null)
			local.getTimeDelta().stop();
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		return null;
	}

	protected IResourceLocator createResourceLocator() {
		return ResourceLocators.chain(ResourceLocators.classLoader(getClass().getClassLoader()),
				ResourceLocators.DATA_CLASSLOADER, ResourceLocators.FILE);
	}

	protected TextureManager createTextureManager(IResourceLocator locator) {
		return new TextureManager(new ResourceLoader(locator));
	}

	protected abstract GLElement createRoot();


	@Override
	public void initialize() {
		super.initialize();
		GeneralManager.get().getViewManager().registerView(this, true);
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		IResourceLocator locator = createResourceLocator();
		TextureManager textures = createTextureManager(locator);

		local = new GLContextLocal(textures, locator, canvas);

		AGLView.initGLContext(gl);

		gl.glLoadIdentity();

		eventListeners.register(this);

		initScene();

		local.getTimeDelta().reset();
	}

	protected void initScene() {
		this.root = new WindowGLElement(createRoot());
		this.root.setParent(this);
		this.root.init(this);
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		this.canvas.removeGLEventListener(this);
		this.canvas.removeMouseListener(pickingManager.getListener());

		this.root.takeDown();
		GL2 gl = drawable.getGL().getGL2();
		local.destroy(gl);

		this.eventListeners.unregisterAll();

		ViewManager.get().destroyView(this);
	}

	private final float getWidth() {
		return viewFrustum.getRight();
	}

	private final float getHeight() {
		return viewFrustum.getBottom();
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		eventListeners.processEvents();

		if (!isVisible())
			return;

		final int deltaTimeMs = local.getDeltaTimeMs();
		GL2 gl = drawable.getGL().getGL2();
		// clear screen
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		// gl.glActiveTexture(GL.GL_TEXTURE0);
		gl.glBindTexture(GL.GL_TEXTURE_2D, GL.GL_NONE);

		gl.glLoadIdentity();
		gl.glTranslatef(0.375f, 0.375f, 0);

		final GLGraphics g = new GLGraphics(gl, local, true, deltaTimeMs);
		g.clearError();

		float paddedWidth = getWidth();
		float paddedHeight = getHeight();

		if (dirtyLayout) {
			root.setBounds(0, 0, paddedWidth, paddedHeight);
			root.relayout();
			dirtyLayout = false;
		}

		// 1. pass: picking
		Vec2f mousePos = pickingManager.getCurrentMousePos();
		if (mousePos != null) {
			root.getMouseLayer().setBounds(mousePos.x(), mousePos.y(), getWidth() - mousePos.x(),
					getHeight() - mousePos.y());
			root.getMouseLayer().relayout();
		}

		GLSanityCheck s = null;
		assert (s = GLSanityCheck.create(gl)) != null;
		pickingManager.doPicking(g.gl, new Runnable() {
			@Override
			public void run() {
				root.renderPick(g);
			}
		});
		g.checkError();
		assert s != null && s.verify(gl);

		// 2. pass: layouting
		root.layout(deltaTimeMs);

		assert (s = GLSanityCheck.create(gl)) != null;
		// 3. pass: rendering
		root.render(g);

		g.checkError();
		assert s != null && s.verify(gl);
	}



	/**
	 * @return
	 */
	private boolean isVisible() {
		return visible && canvas.isVisible();
	}

	@Override
	public final void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL2 gl = drawable.getGL().getGL2();

		viewFrustum.setRight(canvas.getDIPWidth());
		viewFrustum.setBottom(canvas.getDIPHeight());

		gl.glViewport(x, y, width, height);
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glLoadIdentity();
		viewFrustum.setProjectionMatrix(gl);

		relayout();
	}

	@Override
	public Vec2f toAbsolute(Vec2f relative) {
		return relative;
	}

	@Override
	public Vec2f toRelative(Vec2f absolute) {
		return absolute;
	}

	@Override
	public final IMouseLayer getMouseLayer() {
		return root == null ? null : root.getMouseLayer();
	}

	@Override
	public final IPopupLayer getPopupLayer() {
		return root == null ? null : root.getPopupLayer();
	}

	@Override
	public final ISWTLayer getSWTLayer() {
		return swtLayer;
	}

	@Override
	public final boolean moved(GLElement child) {
		return true;
	}

	@Override
	public void init(GLElement element) {
		// scan object for event listeners but only the subclasses
		eventListeners.register(element, null, isNotBaseClass);
	}

	static final Predicate<Class<?>> isNotBaseClass = new Predicate<Class<?>>() {
		@Override
		public boolean apply(Class<?> c) {
			if (c.isAssignableFrom(GLElement.class) || c.isAssignableFrom(GLElementContainer.class)
					|| c.isAssignableFrom(AnimatedGLElementContainer.class))
				return false;
			return true;
		}
	};

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
	public final void relayout() {
		dirtyLayout = true;
	}

	@Override
	public final void repaint() {

	}

	@Override
	public final void repaintPick() {

	}


	@Override
	public final int registerPickingListener(IPickingListener l) {
		return registerPickingListener(l, 0);
	}

	@Override
	public final int registerPickingListener(IPickingListener l, int objectId) {
		return pickingManager.register(l, objectId);
	}

	@Override
	public final void unregisterPickingListener(int pickingID) {
		pickingManager.unregister(pickingID);
	}
}
