/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander Lex, Christian Partl, Johannes Kepler
 * University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.view.opengl.canvas;

import gleem.linalg.Vec3f;

import java.awt.Point;
import java.util.HashSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.fixedfunc.GLMatrixFunc;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.IListenerOwner;
import org.caleydo.core.id.object.ManagedObjectType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.base.ILabelProvider;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.exception.ExceptionHandler;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.view.AView;
import org.caleydo.core.view.IDataDomainBasedView;
import org.caleydo.core.view.ViewManager;
import org.caleydo.core.view.contextmenu.ContextMenuCreator;
import org.caleydo.core.view.opengl.camera.IViewCamera;
import org.caleydo.core.view.opengl.camera.ViewCameraBase;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.listener.GLMouseWheelListener;
import org.caleydo.core.view.opengl.canvas.listener.IMouseWheelHandler;
import org.caleydo.core.view.opengl.canvas.listener.IResettableView;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.keyboard.GLFPSKeyListener;
import org.caleydo.core.view.opengl.keyboard.GLKeyListener;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.PickingManager2;
import org.caleydo.core.view.opengl.picking.SpacePickingManager;
import org.caleydo.core.view.opengl.util.FPSCounter;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;

/**
 * Abstract base class for all OpenGL2 views.
 * <p>
 * Every view has to specify its {@link #VIEW_TYPE}.
 * </p>
 * <h2>Creating a View</h2>
 * <p>
 * Views may only be instantiated using the {@link ViewManager#createGLView(Class, GLCanvas, ViewFrustum)}. As a
 * consequence, the constructor of the view may have only those two arguments (GLCanvas, ViewFrustum). Otherwise, the
 * creation will fail.
 * </p>
 * <p>
 * After the object is created, set the dataDomain (if your view is a {@link IDataDomainBasedView}). If your view is
 * rendered remotely (i.e. embedded in anothre view) you MUST call
 * {@link #setRemoteRenderingGLView(IGLRemoteRenderingView)} now! Then you MUST call {@link #initialize()}. This method
 * initializes the event listeners and other things.
 * </p>
 *
 * @author Marc Streit
 * @author Alexander Lex
 */
public abstract class AGLView extends AView implements IGLView, GLEventListener, IResettableView, IMouseWheelHandler {

	protected final static int EMPTY_VIEW_TEXT_HEIGHT_PIXELS = 26;

	public enum EBusyState {
		SWITCH_OFF, ON, OFF
	}

	/**
	 * The canvas rendering the view. The canvas also holds the {@link PixelGLConverter}
	 */
	protected IGLCanvas parentGLCanvas;

	private PickingManager2 pickingManager;

	/**
	 * Key listener which is created and registered in specific view.
	 */
	protected GLKeyListener<?> glKeyListener;

	protected GLMouseListener glMouseListener;

	protected ViewFrustum viewFrustum;

	protected IViewCamera viewCamera;

	private FPSCounter fpsCounter;

	private boolean showFPSCounter;

	protected PixelGLConverter pixelGLConverter = null;

	protected EDetailLevel detailLevel = EDetailLevel.HIGH;

	protected IGLRemoteRenderingView glRemoteRenderingView;

	/**
	 * Flag determining whether the display list is invalid and has to be rebuild
	 */
	protected boolean isDisplayListDirty = true;

	/** The index of the main display list as required by opengl */
	protected int displayListIndex = 0;

	protected boolean hasFrustumChanged = false;

	protected TextureManager textureManager;

	protected ContextMenuCreator contextMenuCreator = new ContextMenuCreator();

	/**
	 * The queue which holds the events
	 */
	private BlockingQueue<Pair<AEventListener<? extends IListenerOwner>, AEvent>> queue = new LinkedBlockingQueue<Pair<AEventListener<? extends IListenerOwner>, AEvent>>();

	private boolean isVisible = true;

	protected CaleydoTextRenderer textRenderer;

	/**
	 * True if the mouse is currently over this view. If lazyMode is true then the picking does not need to be rendered
	 */
	protected boolean lazyMode;

	private int currentScrollBarID = 0;

	private HashSet<IMouseWheelHandler> mouseWheelListeners;

	protected GLMouseWheelListener glMouseWheelListener;

	private boolean focusGained = false;

	private IGLFocusListener focusListener = new IGLFocusListener() {
		@Override
		public void focusLost() {
			// focusGained=false;
		}

		@Override
		public void focusGained() {
			focusGained = true;
		}
	};

	/**
	 * Constructor. If the glCanvas object is null - then the view is rendered remote.
	 *
	 * @param viewType
	 *            TODO
	 * @param viewName
	 *            TODO
	 */
	protected AGLView(IGLCanvas glCanvas, final Composite parentComposite, final ViewFrustum viewFrustum,
			String viewType, String viewName) {

		super(GeneralManager.get().getIDCreator().createID(ManagedObjectType.GL_VIEW), parentComposite, viewType,
				viewName);

		parentGLCanvas = glCanvas;

		glMouseListener = new GLMouseListener();
		glMouseListener.setNavigationModes(false, false, false);

		// Register mouse listener to GL2 canvas
		glCanvas.addMouseListener(glMouseListener);

		glCanvas.addFocusListener(focusListener);

		this.viewFrustum = viewFrustum;
		viewCamera = new ViewCameraBase(uniqueID);

		textureManager = new TextureManager();

		glMouseWheelListener = new GLMouseWheelListener(this);

		pixelGLConverter = new PixelGLConverter(viewFrustum, parentGLCanvas);

		pickingManager = new PickingManager2();

		mouseWheelListeners = new HashSet<IMouseWheelHandler>();

		parentGLCanvas.addMouseListener(glMouseWheelListener);

	}

	@Override
	public void initialize() {
		ViewManager viewManager = GeneralManager.get().getViewManager();
		viewManager.registerView(this, !isRenderedRemote());
		setLabel(this.getDefaultLabel(), true);
		registerEventListeners();

		if (glRemoteRenderingView == null)
			viewManager.registerGLEventListenerByGLCanvas(parentGLCanvas, this);

		viewManager.initializeUnserializedViews();
	}

	@Override
	public void init(GLAutoDrawable drawable) {

		final GLFPSKeyListener fpsKeyListener = new GLFPSKeyListener(this);
		parentComposite.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				parentComposite.addKeyListener(fpsKeyListener);
			}
		});

		GL2 gl = drawable.getGL().getGL2();

		// This is specially important for Windows. Otherwise JOGL2 internally
		// slows down dramatically (factor of 10).
		// gl.setSwapInterval(0);

		fpsCounter = new FPSCounter(drawable, 16);
		fpsCounter.setColor(0.5f, 0.5f, 0.5f, 1);

		initGLContext(gl);

		glMouseListener.addGLCanvas(this);
		pixelGLConverter = new PixelGLConverter(viewFrustum, parentGLCanvas);
		textRenderer = new CaleydoTextRenderer(24);
		parentGLCanvas.addMouseListener(pickingManager.getListener());

		initLocal(gl);
	}

	public static void initGLContext(GL2 gl) {
		gl.glShadeModel(GLLightingFunc.GL_SMOOTH); // Enables Smooth Shading
		gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f); // white Background
		gl.glClearDepth(1.0f); // Depth Buffer Setup
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT | GL.GL_STENCIL_BUFFER_BIT);

		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LEQUAL);

		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

		// gl.glEnable(GL2.GL_POINT_SMOOTH);
		// gl.glHint(GL2.GL_POINT_SMOOTH_HINT, GL.GL_NICEST);
		gl.glEnable(GL.GL_LINE_SMOOTH);
		gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);
		// gl.glEnable(GL2.GL_POLYGON_SMOOTH);
		// gl.glHint(GL2.GL_POLYGON_SMOOTH_HINT, GL.GL_NICEST);

		gl.glHint(GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);

		gl.glEnable(GLLightingFunc.GL_COLOR_MATERIAL);
		gl.glColorMaterial(GL.GL_FRONT, GLLightingFunc.GL_DIFFUSE);
	}

	@Override
	public final void display(GLAutoDrawable drawable) {
		try {
			processEvents();
			if (!isVisible())
				return;

			if (!focusGained) {
				parentGLCanvas.requestFocus();

				// Display.getDefault().asyncExec(new Runnable() {
				// @Override
				// public void run() {
				// parentGLCanvas.requestFocus();
				// }
				// });

			}

			// parentComposite.getDisplay().asyncExec(new Runnable() {
			// @Override
			// public void run() {
			//
			// parentComposite.setFocus();
			// parentComposite.set
			// // viewPart.setFocus();
			// // viewPart.getSWTComposite().setFocus();
			//
			// }
			// });
			final Vec3f rot_Vec3f = new Vec3f();
			final Vec3f position = viewCamera.getCameraPosition();

			// System.out.println("focus owner " +
			// parentGLCanvas.isFocusOwner());
			// System.out.println("focusable " + parentGLCanvas.isFocusable());

			GL2 gl = drawable.getGL().getGL2();

			// ViewManager.get().executePendingRemoteViewDestruction(gl, this);

			// load identity matrix
			gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
			gl.glLoadIdentity();

			// clear screen
			gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

			gl.glTranslatef(position.x(), position.y(), position.z());
			gl.glRotatef(viewCamera.getCameraRotationGrad(rot_Vec3f), rot_Vec3f.x(), rot_Vec3f.y(), rot_Vec3f.z());

			displayLocal(gl);

			if (showFPSCounter)
				fpsCounter.draw();
		} catch (RuntimeException exception) {
			ExceptionHandler.get().handleViewException(exception, this);
		}
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

		updateViewFrustum(width, height);

		setDisplayListDirty();
		hasFrustumChanged = true;

		GL2 gl = drawable.getGL().getGL2();

		gl.glViewport(x, y, width, height);
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glLoadIdentity();

		viewFrustum.setProjectionMatrix(gl);
		updateDetailMode();
	}

	/**
	 * hook for custom view frustum layouts
	 *
	 * @param width
	 * @param height
	 */
	protected void updateViewFrustum(int width, int height) {
		viewFrustum.setLeft(0);
		viewFrustum.setBottom(0);
		float aspectRatio = (float) height / (float) width;
		viewFrustum.setTop(aspectRatio);
		viewFrustum.setRight(1);
	}

	/**
	 * <p>
	 * Method responsible for initialization of the data. It is intended to be overridden, all subclasses must use this
	 * method to initialize their members related to {@link AView#table}.
	 * </p>
	 */
	public void initData() {
	}

	/**
	 * Clears all selections, meaning that no element is selected or deselected after this method was called. Everything
	 * returns to "normal". Note that virtual array manipulations are not considered selections and are therefore not
	 * retable.
	 */
	// public abstract void clearAllSelections();

	/**
	 * Reset the view to its initial state by calling {@link #initData()}
	 */
	@Override
	public void resetView() {
		initData();
	}

	/**
	 * Set the display list to dirty. May be overridden by subclasses.
	 */
	public void setDisplayListDirty() {
		isDisplayListDirty = true;
	}

	/**
	 * Initialization for gl, general stuff
	 *
	 * @param gl
	 */
	public abstract void init(final GL2 gl);

	/**
	 * Initialization for gl called by the local instance Has to call init internally!
	 *
	 * @param gl
	 */
	protected abstract void initLocal(final GL2 gl);

	/**
	 * Initialization for gl called by a managing view has to call init internally!
	 *
	 * @param gl
	 */
	public abstract void initRemote(final GL2 gl, final AGLView glParentView, final GLMouseListener glMouseListener);

	/**
	 * GL2 display method that has to be called in all cases manually, either by {@link #displayLocal(GL)} or
	 * {@link #displayRemote(GL)}. It must be responsible for rendering the scene. It is also called by the picking
	 * manager.
	 *
	 * @param gl
	 */
	public abstract void display(final GL2 gl);

	@Override
	public final synchronized void queueEvent(AEventListener<? extends IListenerOwner> listener, AEvent event) {
		queue.add(new Pair<AEventListener<? extends IListenerOwner>, AEvent>(listener, event));
	}

	/**
	 * This method should be called every display cycle when it is save to change the state of the object. It processes
	 * all the previously submitted events.
	 */
	public final void processEvents() {
		Pair<AEventListener<? extends IListenerOwner>, AEvent> pair;
		while (queue.peek() != null) {
			pair = queue.poll();
			pair.getFirst().handleEvent(pair.getSecond());
		}
	}

	/**
	 * <p>
	 * This method is called by the animator of a registered class. It should not be called by anyone else, but has to
	 * call the local {@link #display(GL)}, where the actual rendering must happen. If a view is rendered remote, this
	 * method may not be called - instead use {@link #displayRemote(GL)}.
	 * </p>
	 * <p>
	 * Typically a displayLocal should contain:
	 * <ul>
	 * <li>a call to {@link #processEvents()}, where the event queue is processed</li>
	 * <li>a call to the {@link #processEvents()} method of all views it renders locally</li>
	 * <li>this has to be followed by a check whether the view is active, using {@link #isVisible}. If the view is
	 * inactive it should return at this point.</li>
	 * <li>a call to the {@link PickingManager#handlePicking(AGLView, GL)} method, which renders the scene in picking
	 * mode.</li>
	 * <li>and finally a call to the local display</li>
	 * </ul>
	 *
	 * @param gl
	 */
	protected abstract void displayLocal(final GL2 gl);

	/**
	 * Intended for external use when another instance of a view manages the scene. This is specially designed for
	 * composite views. Has to call display internally! The steps necessary in {@link #displayLocal(GL)}, such as
	 * handling of events and picking have to be taken care of the instance calling this method.
	 *
	 * @param gl
	 */
	public abstract void displayRemote(final GL2 gl);

	@Override
	public final IGLCanvas getParentGLCanvas() {

		if (parentGLCanvas == null && this.isRenderedRemote())
			return getRemoteRenderingGLView().getParentGLCanvas();

		return parentGLCanvas;
	}

	public final ViewFrustum getViewFrustum() {
		return viewFrustum;
	}

	public void setFrustum(ViewFrustum viewFrustum) {
		this.viewFrustum = viewFrustum;

		updateDetailMode();

		// parentGLCanvas.initPixelGLConverter(viewFrustum);
	}

	/**
	 * Set the level of detail to be displayed, choose from the options in {@link EDetailLevel}. If the specified detail
	 * level differs from the current {@link #setDisplayListDirty()} is called.
	 *
	 * @param detailLevel
	 */
	public void setDetailLevel(EDetailLevel detailLevel) {
		if (this.detailLevel != detailLevel) {
			this.detailLevel = detailLevel;
			setDisplayListDirty();
		}
	}

	private void updateDetailMode() {
		EDetailLevel newDetailLevel;
		int pixelWidth = pixelGLConverter.getPixelWidthForGLWidth(viewFrustum.getWidth());
		int pixelHeight = pixelGLConverter.getPixelHeightForGLHeight(viewFrustum.getHeight());
		if (pixelHeight > getMinPixelHeight(EDetailLevel.HIGH) && pixelWidth > getMinPixelWidth(EDetailLevel.HIGH)) {
			newDetailLevel = EDetailLevel.HIGH;
		} else if (pixelHeight > getMinPixelHeight(EDetailLevel.MEDIUM)
				&& pixelWidth > getMinPixelWidth(EDetailLevel.MEDIUM)) {
			newDetailLevel = EDetailLevel.MEDIUM;
		} else {
			newDetailLevel = EDetailLevel.LOW;
		}
		setDetailLevel(newDetailLevel);
	}

	protected final void handlePicking(GL2 gl) {
		contextMenuCreator.clear();
		pickingManager.doPicking(gl, this);

		if (contextMenuCreator.hasMenuItems())
			contextMenuCreator.open(this);
	}

	/**
	 * Registers a {@link IPickingListener} for this view that is called when objects with the specified pickingType
	 * <b>and</b> ID are picked.
	 *
	 * @param pickingListener
	 *            the picking listener that should be called on picking event
	 * @param pickingType
	 *            the picking type. Take care that the type is unique for a view.
	 * @param pickedObjectID
	 *            the id identifying the picked object
	 */
	public void addIDPickingListener(IPickingListener pickingListener, String pickingType, int pickedObjectID) {
		getPickingManager().addPickingListener(pickingType, pickedObjectID, pickingListener);
	}

	/**
	 * @return
	 */
	protected final SpacePickingManager getPickingManager() {
		return pickingManager.getSpace(getID());
	}

	public final PickingManager2 getPickingManager2() {
		return pickingManager;
	}

	public final int getPickingID(String pickingType, int pickedObjectID) {
		return getPickingManager().getPickingID(pickingType, pickedObjectID);
	}

	public final void addIDPickingTooltipListener(String tooltip, String pickingType, int pickedObjectID) {
		addIDPickingListener(this.getParentGLCanvas().createTooltip(tooltip), pickingType, pickedObjectID);
	}

	public final void addIDPickingTooltipListener(ILabelProvider tooltip, String pickingType, int pickedObjectID) {
		addIDPickingListener(this.getParentGLCanvas().createTooltip(tooltip), pickingType, pickedObjectID);
	}

	public final void addTypePickingTooltipListener(String tooltip, String pickingType) {
		addTypePickingListener(this.getParentGLCanvas().createTooltip(tooltip), pickingType);
	}

	/**
	 * Registers a {@link IPickingListener} for this view that is call whenever an object of the specified type was
	 * picked, independent of the object's picking id.
	 *
	 * @see AGLView#addIDPickingListener(IPickingListener, String, int) for picking id dependent
	 * @param pickingListener
	 *            the picking listener that should be called on picking event
	 * @param pickingType
	 *            the picking type. Take care that the type is unique for a view.
	 */
	public void addTypePickingListener(IPickingListener pickingListener, String pickingType) {
		getPickingManager().addTypePickingListener(pickingType, pickingListener);
	}

	/**
	 * Removes the specified {@link IPickingListener} for single ids that has been added with the specified picking type
	 * and id.
	 *
	 * @param pickingListener
	 * @param pickingType
	 * @param pickedObjectID
	 */
	public void removeIDPickingListener(IPickingListener pickingListener, String pickingType, int pickedObjectID) {
		getPickingManager().removePickingListener(pickingType, pickedObjectID, pickingListener);
	}

	/**
	 * Removes the specified {@link IPickingListener} for the specified picking type
	 *
	 * @param pickingListener
	 * @param pickingType
	 */
	public void removeTypePickingListener(IPickingListener pickingListener, String pickingType) {
		getPickingManager().removeTypePickingListener(pickingType, pickingListener);
	}

	/**
	 * Removes all ID picking listeners for a specific picking type and ID.
	 *
	 * @param pickingType
	 * @param pickedObjectID
	 */
	public void removeAllIDPickingListeners(String pickingType, int pickedObjectID) {
		getPickingManager().removePickingListeners(pickingType, pickedObjectID);
	}

	/**
	 * Removes all picking listeners.
	 */
	public void removeAllPickingListeners() {
		getPickingManager().removeAllPickingListeners();
	}

	/**
	 * Removes all type picking listeners for a specific picking type.
	 *
	 * @param pickingType
	 */
	public void removeAllTypePickingListeners(String pickingType) {
		getPickingManager().removeTypePickingListeners(pickingType);
	}

	public final IViewCamera getViewCamera() {
		return viewCamera;
	}

	public final boolean isRenderedRemote() {
		return glRemoteRenderingView != null;
	}

	public final void setRemoteRenderingGLView(IGLRemoteRenderingView glRemoteRenderingView) {
		this.glRemoteRenderingView = glRemoteRenderingView;
		pixelGLConverter = glRemoteRenderingView.getPixelGLConverter();
		pickingManager = glRemoteRenderingView.getPickingManager2(); // FIXME
		ViewManager.get().registerRemoteRenderedView(this, (AGLView) glRemoteRenderingView);
		// pixelGLConverter = new
		// PixelGLConverter(glRemoteRenderingView.getViewFrustum(),
		// parentGLCanvas);
	}

	public final IGLRemoteRenderingView getRemoteRenderingGLView() {
		return glRemoteRenderingView;
	}

	public final EDetailLevel getDetailLevel() {
		return detailLevel;
	}

	// @Override
	// public synchronized Pair<AEventListener<? extends IListenerOwner>,
	// AEvent> getEvent() {
	// return queue.poll();
	// }

	@Override
	public void initFromSerializableRepresentation(ASerializedView serialzedView) {
	}

	@Override
	public void registerEventListeners() {
	}

	@Override
	public void unregisterEventListeners() {
	}

	/**
	 * Set whether this view is visible.
	 *
	 * @param isVisible
	 *            true if the view is visible
	 */
	@Override
	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
		if (!isVisible) {
			focusGained = false;
		}
	}

	/**
	 * Check whether the view is visible. If not, it should not be rendered. Note that events should be processed
	 * anyway.
	 *
	 * @return true if it is visible
	 */
	protected boolean isVisible() {
		return isVisible;
	}

	/**
	 * Renders the symbol of a view. (Called when there's nothing to display.)
	 *
	 * @param gl
	 *            GL2 Object that shall be used for rendering.
	 */
	protected void renderSymbol(GL2 gl, String texture, float buttonSize) {

		float xButtonOrigin = viewFrustum.getLeft() + viewFrustum.getWidth() / 2 - buttonSize / 2;
		float yButtonOrigin = viewFrustum.getBottom() + viewFrustum.getHeight() / 2 - buttonSize / 2;
		Texture tempTexture = textureManager.getIconTexture(texture);
		tempTexture.enable(gl);
		tempTexture.bind(gl);

		TextureCoords texCoords = tempTexture.getImageTexCoords();

		gl.glPushAttrib(GL2.GL_CURRENT_BIT | GL2.GL_LINE_BIT);
		gl.glColor4f(1f, 1, 1, 1f);
		gl.glBegin(GL2.GL_POLYGON);

		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(xButtonOrigin, yButtonOrigin, 0.01f);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(xButtonOrigin, yButtonOrigin + buttonSize, 0.01f);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(xButtonOrigin + buttonSize, yButtonOrigin + buttonSize, 0.01f);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(xButtonOrigin + buttonSize, yButtonOrigin, 0.01f);
		gl.glEnd();
		gl.glPopAttrib();
		tempTexture.disable(gl);
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		if (PlatformUI.getWorkbench().isClosing())
			return;
		Logger.log(new Status(IStatus.INFO, toString(), "Disposing view"));

		GL2 gl = drawable.getGL().getGL2();
		// First, destroy the remote views, then unregister, otherwise the
		// remote view destruction would not work
		// ViewManager.get().destroyRemoteViews(gl, this);
		// ViewManager.get().unregisterGLView(this);
		ViewManager.get().destroyView(gl, this);
		// destroy(gl);
	}

	/**
	 * Destroys this view by removing data common in all views and calling {@link #destroyViewSpecificContent(GL2)}.
	 *
	 * @param gl
	 */
	public final void destroy(GL2 gl) {
		System.out.println("destroy " + label);
		unregisterEventListeners();
		destroyViewSpecificContent(gl);
		parentGLCanvas.removeFocusListener(focusListener);
	}

	/**
	 * In this method subclasses should remove all view specific content, especially GL resources such as display lists.
	 * Note that there is no need to take care about remote rendered views.
	 *
	 * @param gl
	 */
	protected abstract void destroyViewSpecificContent(GL2 gl);

	/**
	 * Returns the text renderer valid for the gl context of this view.
	 */
	public CaleydoTextRenderer getTextRenderer() {
		return textRenderer;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		System.out.println("Finalizing " + this);
	}

	/**
	 * @return The minimum height in pixels the view currently requires to show its content properly. The default
	 *         implementation in the base class calls {@link #getMinPixelheight()} with {@link EDetailLevel#LOW}
	 */
	public int getMinPixelHeight() {
		return getMinPixelHeight(EDetailLevel.LOW);
	}

	/**
	 * @return The minimum width in pixels the view currently requires to show its content properly. The default
	 *         implementation in the base class calls {@link #getMinPixelWidth()} with {@link EDetailLevel#LOW}
	 */
	public int getMinPixelWidth() {
		return getMinPixelWidth(EDetailLevel.LOW);
	}

	/**
	 * @return The minimum height in pixels the view requires to show its content properly with the specified detail
	 *         level.
	 */
	public int getMinPixelHeight(EDetailLevel detailLevel) {
		return 0;
	}

	/**
	 * @return The minimum width in pixels the view requires to show its content properly with the specified detail
	 *         level.
	 */
	public int getMinPixelWidth(EDetailLevel detailLevel) {
		return 0;
	}

	/**
	 * Gets the highest possible detail level the view is able to display its content with, using the specified width
	 * and height.
	 *
	 * @param pixelHeight
	 * @param pixelWidth
	 * @return
	 */
	public EDetailLevel getHightestPossibleDetailLevel(int pixelHeight, int pixelWidth) {
		return EDetailLevel.LOW;
	}

	/**
	 * Sets the boolean lazy mode which determines if the mouse is over the canvas.
	 *
	 * @param lazyMode
	 */
	public void setLazyMode(boolean lazyMode) {
		this.lazyMode = lazyMode;

	}

	public synchronized int createNewScrollBarID() {
		return currentScrollBarID++;
	}

	public GLMouseListener getGLMouseListener() {
		return glMouseListener;
	}

	/**
	 * Register any mouseWheelListener that may be interested in being notified when the mouse wheel is moved
	 *
	 * @param listener
	 */
	public void registerMouseWheelListener(IMouseWheelHandler listener) {
		mouseWheelListeners.add(listener);
	}

	public void unregisterRemoteViewMouseWheelListener(IMouseWheelHandler listener) {
		mouseWheelListeners.remove(listener);
	}

	/**
	 * This method shall be called when the mouse was wheeled for zooming.
	 *
	 * @param wheelAmount
	 * @param wheelPosition
	 */
	@Override
	public void handleMouseWheel(int wheelAmount, Point wheelPosition) {
		for (IMouseWheelHandler listener : mouseWheelListeners) {
			listener.handleMouseWheel(wheelAmount, wheelPosition);
		}
	}

	/**
	 * Returns the pixelGLConverter associated with this canvas.
	 *
	 * @return
	 */
	public PixelGLConverter getPixelGLConverter() {
		return pixelGLConverter;
	}

	/**
	 * Returns the instance that is responsible for creating the context menu.
	 */
	public ContextMenuCreator getContextMenuCreator() {
		return contextMenuCreator;
	}

	/**
	 * Method recursively determines the top level GL view. Picking listeners for instance need to be registered to the
	 * top level GL view.
	 *
	 * @return the top level GL view
	 */
	public AGLView getTopLevelGLView() {
		if (isRenderedRemote())
			return ((AGLView) getRemoteRenderingGLView()).getTopLevelGLView();

		return this;
	}

	/**
	 * @return the textureManager, see {@link #textureManager}
	 */
	public TextureManager getTextureManager() {
		return textureManager;
	}

	/**
	 * Turn on/off the rendering of the FPS counter
	 */
	public void toggleFPSCounter() {
		this.showFPSCounter = !showFPSCounter;
	}

	@Override
	public void setLabel(String label, boolean isLabelDefault) {
		if (isRenderedRemote()) {
			this.label = label;
			this.isLabelDefault = isLabelDefault;
		} else {
			super.setLabel(label, isLabelDefault);
		}
	}

	/**
	 * Renders a message in the center of the view. This method is intended to be used for displaying messages that
	 * indicate what a user should do when the view is empty.
	 *
	 * @param gl
	 * @param lines
	 *            The lines of text.
	 */
	protected void renderEmptyViewText(GL2 gl, String... lines) {

		float textHeight = pixelGLConverter.getGLHeightForPixelHeight(EMPTY_VIEW_TEXT_HEIGHT_PIXELS);
		float safetySpacing = pixelGLConverter.getGLHeightForPixelHeight(3);
		textRenderer.setColor(0, 0, 0, 1);
		float linePositionY = viewFrustum.getHeight() / 2.0f + textHeight * lines.length / 2.0f;
		for (String line : lines) {
			float requiredWidth = textRenderer.getRequiredTextWidth(line, textHeight);
			float linePositionX = viewFrustum.getWidth() / 2.0f - requiredWidth / 2.0f;

			textRenderer.renderTextInBounds(gl, line, linePositionX, linePositionY, 0, requiredWidth + safetySpacing,
					textHeight);
			linePositionY -= textHeight;
		}
	}
}
