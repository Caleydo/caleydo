/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2;

import gleem.linalg.Vec2f;

import java.awt.Point;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.fixedfunc.GLMatrixFunc;

import org.caleydo.core.event.EventListenerManagers;
import org.caleydo.core.event.EventListenerManagers.QueuedEventListenerManager;
import org.caleydo.core.id.object.ManagedObjectType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.AView;
import org.caleydo.core.view.contextmenu.ContextMenuCreator;
import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.canvas.IGLView;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.PickingMouseListener;
import org.caleydo.core.view.opengl.picking.SimplePickingManager;
import org.caleydo.core.view.opengl.util.text.CompositeTextRenderer;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.data.loader.ResourceLocators;
import org.caleydo.data.loader.ResourceLocators.IResourceLocator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * a {@link IGLView} based on {@link GLElement}s its NOT a {@link AGLView}
 *
 * @author Samuel Gratzl
 *
 */
public abstract class AGLElementView extends AView implements IGLView, GLEventListener, IGLElementContext,
		IGLElementParent {
	private final IGLCanvas canvas;

	protected final QueuedEventListenerManager eventListeners = EventListenerManagers.createQueued();

	private final SimplePickingManager pickingManager = new SimplePickingManager();
	private final PickingMouseListener mouseListener = new PickingMouseListener();

	private final ViewFrustum viewFrustum = new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC, 0, 100, 100, 0, -20, 20);

	private WindowGLElement root;
	private DisplayListPool pool;
	private boolean dirtyLayout = true;

	private boolean visible = true;
	private CompositeTextRenderer text;
	private TextureManager textures;
	private IResourceLocator locator;

	protected ContextMenuCreator contextMenuCreator = new ContextMenuCreator();

	public AGLElementView(IGLCanvas glCanvas, Composite parentComposite, String viewType, String viewName) {
		super(GeneralManager.get().getIDCreator().createID(ManagedObjectType.GL_VIEW), parentComposite, viewType,
				viewName);
		this.canvas = glCanvas;
		this.canvas.addGLEventListener(this);
		this.canvas.addMouseListener(mouseListener);
	}

	protected GLElement getRoot() {
		return root.getRoot();
	}

	@Override
	public IGLCanvas getParentGLCanvas() {
		return canvas;
	}

	@Override
	public void setVisible(boolean visible) {
		if (this.visible == visible)
			return;
		this.visible = visible;
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		return null;
	}

	@Override
	public void initFromSerializableRepresentation(ASerializedView serializedView) {

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
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		text = new CompositeTextRenderer(8, 16, 24, 40);
		locator = createResourceLocator();
		textures = createTextureManager(locator);

		AGLView.initGLContext(gl);

		gl.glLoadIdentity();

		eventListeners.register(this);

		this.root = new WindowGLElement(createRoot());
		this.root.setParent(this);
		this.pool = new DisplayListPool();
		this.root.init(this);
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		this.canvas.removeGLEventListener(this);
		this.canvas.removeMouseListener(mouseListener);

		this.root.takeDown();
		GL2 gl = drawable.getGL().getGL2();
		this.pool.deleteAll(gl);

		this.eventListeners.unregisterAll();
	}

	private float getWidth() {
		return viewFrustum.getRight();
	}

	private float getHeight() {
		return viewFrustum.getBottom();
	}

	@Override
	public final void display(GLAutoDrawable drawable) {
		eventListeners.processEvents();

		if (!visible)
			return;

		GL2 gl = drawable.getGL().getGL2();
		// clear screen
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		gl.glTranslatef(0.375f, 0.375f, 0);

		final GLGraphics g = new GLGraphics(gl, text, getTextureManager(), locator, true);

		float paddedWidth = getWidth();
		float paddedHeight = getHeight();

		if (dirtyLayout) {
			root.setBounds(0, 0, paddedWidth, paddedHeight);
			root.layout();
			dirtyLayout = false;
		}

		Runnable toRender = new Runnable() {
			@Override
			public void run() {
				root.renderPick(g);
			}
		};

		Point mousePos = mouseListener.getCurrentMousePos();
		if (mousePos != null) {
			root.getMouseLayer().setBounds(mousePos.x, mousePos.y, getWidth() - mousePos.x, getHeight() - mousePos.y);
			root.getMouseLayer().relayout();
		}
		contextMenuCreator.clear();
		pickingManager.doPicking(mouseListener, g.gl, toRender);
		if (contextMenuCreator.hasMenuItems())
			contextMenuCreator.open(this);

		root.render(g);

		g.destroy();
	}

	@Override
	public final void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL2 gl = drawable.getGL().getGL2();

		viewFrustum.setRight(width);
		viewFrustum.setBottom(height);

		gl.glViewport(x, y, width, height);
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glLoadIdentity();
		viewFrustum.setProjectionMatrix(gl);

		relayout();
	}

	@Override
	public final Vec2f getAbsoluteLocation() {
		return new Vec2f(0, 0);
	}

	@Override
	public final TextureManager getTextureManager() {
		return this.textures;
	}

	@Override
	public final IMouseLayer getMouseLayer() {
		return root.getMouseLayer();
	}

	@Override
	public final boolean moved(GLElement child) {
		return true;
	}

	@Override
	public void init(GLElement element) {
		// scan object for event listeners but only the subclasses
		eventListeners.register(element, null, GLElement.class);
	}

	@Override
	public void takeDown(GLElement element) {
		// undo listeners
		eventListeners.unregister(element);
	}

	@Override
	public final DisplayListPool getDisplayListPool() {
		return pool;
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
	public final void setCursor(final int swtCursorConst) {
		final Composite c = canvas.asComposite();
		final Display d = c.getDisplay();
		d.asyncExec(new Runnable() {
			@Override
			public void run() {
				c.setCursor(swtCursorConst < 0 ? null : d.getSystemCursor(swtCursorConst));
			}
		});
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
	public final void unregisterPickingListener(IPickingListener l) {
		pickingManager.unregister(l);
	}

	@Override
	public final void unregisterPickingListener(int pickingID) {
		pickingManager.unregister(pickingID);
	}

}
