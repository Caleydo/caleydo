/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.canvas;

import gleem.linalg.Vec3f;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
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
import org.caleydo.core.event.view.ViewScrollEvent;
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
import org.caleydo.core.view.listener.ViewScrollEventListener;
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
import org.caleydo.core.view.opengl.picking.IPickingLabelProvider;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingManager;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;
import org.caleydo.core.view.opengl.util.FPSCounter;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
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

	protected PickingManager pickingManager;

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

	private int frameCounter = 0;
	private int rotationFrameCounter = 0;
	private static final int NUMBER_OF_FRAMES = 15;

	protected EBusyState busyState = EBusyState.OFF;

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

	/**
	 * picking listeners that are notified only for picks with a specific id / type combination. The key of the map is
	 * the type, the key of the internal map is the pickedObjectID
	 */
	private HashMap<String, HashMap<Integer, Set<IPickingListener>>> idPickingListeners;
	/**
	 * Picking listeners that are notified for all picks of a type. The key of the map is the type.
	 */
	private HashMap<String, Set<IPickingListener>> typePickingListeners;

	private int currentScrollBarID = 0;

	private HashSet<IMouseWheelHandler> mouseWheelListeners;

	protected GLMouseWheelListener glMouseWheelListener;

	private boolean focusGained = false;

	private int scrollX = 0;

	private int scrollY = 0;

	private ViewScrollEventListener viewScrollEventListener;

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

		idPickingListeners = new HashMap<String, HashMap<Integer, Set<IPickingListener>>>();
		typePickingListeners = new HashMap<String, Set<IPickingListener>>();

		this.viewFrustum = viewFrustum;
		viewCamera = new ViewCameraBase(uniqueID);

		pickingManager = generalManager.getViewManager().getPickingManager();
		textureManager = new TextureManager();

		glMouseWheelListener = new GLMouseWheelListener(this);

		pixelGLConverter = new PixelGLConverter(viewFrustum, parentGLCanvas);

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

			gl.glPushMatrix();
			gl.glTranslatef(pixelGLConverter.getGLWidthForPixelWidth(scrollX),
					pixelGLConverter.getGLHeightForPixelHeight(scrollY), 0);

			// clear screen
			gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

			gl.glTranslatef(position.x(), position.y(), position.z());
			gl.glRotatef(viewCamera.getCameraRotationGrad(rot_Vec3f), rot_Vec3f.x(), rot_Vec3f.y(), rot_Vec3f.z());

			// gl.glActiveTexture(GL.GL_TEXTURE0);
			gl.glBindTexture(GL.GL_TEXTURE_2D, GL.GL_NONE);

			displayLocal(gl);

			if (showFPSCounter)
				fpsCounter.draw();
			gl.glPopMatrix();
		} catch (RuntimeException exception) {
			ExceptionHandler.get().handleViewException(exception, this);
		}
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

		updateViewFrustum(width, height);
		// scrollX = x;
		// scrollY = y;

		// Display.getDefault().asyncExec(new Runnable() {
		//
		// @Override
		// public void run() {
		// Composite parentsparent = parentComposite.getParent();
		// System.out.println("asd");
		//
		// }
		// });

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
	 * This method clips everything outside the frustum
	 */
	public void clipToFrustum(GL2 gl) {
		// if (this instanceof GLHeatMap && ((GLHeatMap) this).isInListMode())
		// return;
		//
		gl.glClear(GL.GL_STENCIL_BUFFER_BIT);
		gl.glColorMask(false, false, false, false);
		gl.glClearStencil(0); // Clear The Stencil Buffer To 0
		gl.glEnable(GL.GL_DEPTH_TEST); // Enables Depth Testing
		gl.glDepthFunc(GL.GL_LEQUAL); // The Type Of Depth Testing To Do
		gl.glEnable(GL.GL_STENCIL_TEST);
		gl.glStencilFunc(GL.GL_ALWAYS, 1, 1);
		gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_REPLACE);
		gl.glDisable(GL.GL_DEPTH_TEST);

		// Clip region that renders in stencil buffer (in this case the
		// frustum)
		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex3f(viewFrustum.getLeft(), viewFrustum.getBottom(), -0.01f);
		gl.glVertex3f(viewFrustum.getRight(), viewFrustum.getBottom(), -0.01f);
		gl.glVertex3f(viewFrustum.getRight(), viewFrustum.getTop(), -0.01f);
		gl.glVertex3f(viewFrustum.getLeft(), viewFrustum.getTop(), -0.01f);
		gl.glEnd();

		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glColorMask(true, true, true, true);
		gl.glStencilFunc(GL.GL_EQUAL, 1, 1);
		gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_KEEP);

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

	/**
	 * Check whether we had a picking hit somewhere during the previous run
	 *
	 * @param gl
	 */
	protected final void checkForHits(final GL2 gl) {
		contextMenuCreator.clear();
		Set<String> hitTypes = pickingManager.getHitTypes(uniqueID);
		if (hitTypes == null)
			return;

		for (String pickingType : hitTypes) {

			ArrayList<Pick> alHits = null;

			alHits = pickingManager.getHits(uniqueID, pickingType);

			// This is a try to fix MOUSE_OUT in remote rendered views, not
			// successful yet
			// if(isRenderedRemote() && (alHits == null || alHits.size() == 0))
			// {
			// AGLView remoteRenderingView = this;
			// while(remoteRenderingView.isRenderedRemote()) {
			// remoteRenderingView =
			// (AGLView)(remoteRenderingView.getRemoteRenderingGLView());
			// }
			// alHits = pickingManager.getHits(remoteRenderingView.getID(),
			// pickingType);
			// }

			if (alHits != null && alHits.size() != 0) {

				for (int iCount = 0; iCount < alHits.size(); iCount++) {
					Pick tempPick = alHits.get(iCount);
					int pickedObjectID = tempPick.getObjectID();
					if (pickedObjectID == -1) {
						continue;
					}

					PickingMode ePickingMode = tempPick.getPickingMode();

					handlePicking(pickingType, ePickingMode, pickedObjectID, tempPick);
					// FIXME: This is for legacy support -> picking listeners
					// should be used

					try {
						PickingType type = PickingType.valueOf(pickingType);
						try {
							handlePickingEvents(type, ePickingMode, pickedObjectID, tempPick);
						} catch (Exception e) {
							Logger.log(new Status(IStatus.ERROR, this.toString(), "Caught exception when picking", e));
						}
					} catch (IllegalArgumentException e) {
					}

				}
				pickingManager.flushHits(uniqueID, pickingType);
			}
		}

		if (contextMenuCreator.hasMenuItems())
			contextMenuCreator.open(this);
	}

	protected void handlePicking(String pickingType, PickingMode pickingMode, int pickedObjectID, Pick pick) {

		Set<IPickingListener> pickingListeners = typePickingListeners.get(pickingType);

		if (pickingListeners != null) {
			// Create copy of picking listeners to avoid concurrent modification issues when a picking listener tries to
			// register or unregister a picking listener to this set
			for (IPickingListener pickingListener : new HashSet<>(pickingListeners)) {
				notifyPickingListener(pickingListener, pickingMode, pick);
			}
		}

		HashMap<Integer, Set<IPickingListener>> map = idPickingListeners.get(pickingType);
		if (map == null)
			return;

		pickingListeners = map.get(pickedObjectID);

		if (pickingListeners != null) {
			// Create copy of picking listeners to avoid concurrent modification issues when a picking listener tries to
			// register or unregister a picking listener to this set
			for (IPickingListener pickingListener : new HashSet<>(pickingListeners)) {
				notifyPickingListener(pickingListener, pickingMode, pick);
			}
		}
	}

	private void notifyPickingListener(IPickingListener pickingListener, PickingMode pickingMode, Pick pick) {
		if (pickingListener == null)
			return;
		pickingListener.pick(pick);
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
		HashMap<Integer, Set<IPickingListener>> map = idPickingListeners.get(pickingType);
		if (map == null) {
			map = new HashMap<Integer, Set<IPickingListener>>();
			idPickingListeners.put(pickingType, map);
		}
		Set<IPickingListener> pickingListeners = map.get(pickedObjectID);
		if (pickingListeners == null) {
			pickingListeners = new HashSet<IPickingListener>();

		}
		for (IPickingListener listener : pickingListeners) {
			if (listener == pickingListener) {
				return;
			}
		}
		pickingListeners.add(pickingListener);
		map.put(pickedObjectID, pickingListeners);

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

	public final void addTypePickingTooltipListener(IPickingLabelProvider labelProvider, String pickingType) {
		addTypePickingListener(this.getParentGLCanvas().createTooltip(labelProvider), pickingType);
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
		Set<IPickingListener> pickingListeners = typePickingListeners.get(pickingType);
		if (pickingListeners == null) {
			pickingListeners = new HashSet<IPickingListener>();

		}
		for (IPickingListener listener : pickingListeners) {
			if (listener == pickingListener) {
				return;
			}
		}
		pickingListeners.add(pickingListener);
		typePickingListeners.put(pickingType, pickingListeners);
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
		HashMap<Integer, Set<IPickingListener>> map = idPickingListeners.get(pickingType);
		if (map == null) {
			return;
		}

		Set<IPickingListener> pickingListeners = map.get(pickedObjectID);
		if (pickingListeners == null) {
			return;
		}
		pickingListeners.remove(pickingListener);
	}

	/**
	 * Removes the specified {@link IPickingListener} for the specified picking type
	 *
	 * @param pickingListener
	 * @param pickingType
	 */
	public void removeTypePickingListener(IPickingListener pickingListener, String pickingType) {
		Set<IPickingListener> pickingListeners = typePickingListeners.get(pickingType);
		if (pickingListeners == null) {
			return;
		}
		pickingListeners.remove(pickingListener);
	}

	/**
	 * <p>
	 * Remove the specified picking listener from wherever it is registered with this view.
	 * </p>
	 * <p>
	 * Using {@link #removeIDPickingListener(IPickingListener, String, int)} or
	 * {@link #removeAllTypePickingListeners(String)} is preferred to using this method for performance reasons.
	 *
	 * @param pickingListener
	 */
	public void removePickingListener(IPickingListener pickingListener) {

		for (HashMap<Integer, Set<IPickingListener>> map : idPickingListeners.values()) {
			if (map != null) {
				for (Set<IPickingListener> pickingListeners : map.values()) {
					if (pickingListeners != null) {
						pickingListeners.remove(pickingListener);
					}
				}
			}
		}
		for (Set<IPickingListener> pickingListeners : typePickingListeners.values()) {
			if (pickingListeners != null) {
				pickingListeners.remove(pickingListener);
			}
		}
	}

	/**
	 * Removes all ID picking listeners for a specific picking type and ID.
	 *
	 * @param pickingType
	 * @param pickedObjectID
	 */
	public void removeAllIDPickingListeners(String pickingType, int pickedObjectID) {

		HashMap<Integer, Set<IPickingListener>> map = idPickingListeners.get(pickingType);
		if (map == null) {
			return;
		}

		Set<IPickingListener> pickingListeners = map.get(pickedObjectID);
		if (pickingListeners == null) {
			return;
		}
		pickingListeners.clear();
	}

	/**
	 * Removes all picking listeners.
	 */
	public void removeAllPickingListeners() {
		idPickingListeners.clear();
		typePickingListeners.clear();
	}

	/**
	 * Removes all type picking listeners for a specific picking type.
	 *
	 * @param pickingType
	 */
	public void removeAllTypePickingListeners(String pickingType) {

		Set<IPickingListener> pickingListeners = typePickingListeners.get(pickingType);
		if (pickingListeners == null) {
			return;
		}
		pickingListeners.clear();
	}

	/**
	 * This method is called every time a method occurs. It should take care of reacting appropriately to the events.
	 *
	 * @param pickingType
	 *            the Picking type, held in EPickingType
	 * @param pickingMode
	 *            the Picking mode (clicked, dragged etc.)
	 * @param pickingID
	 *            the name specified for an element with glPushName
	 * @param pick
	 *            the pick object which can be useful to retrieve for example the mouse position when the pick occurred
	 * @deprecated replaced by picking listeners. No longer abstract since it's not necessary for views to implement
	 */
	@Deprecated
	protected void handlePickingEvents(final PickingType pickingType, final PickingMode pickingMode,
			final int pickingID, final Pick pick) {
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
		ViewManager.get().registerRemoteRenderedView(this, (AGLView) glRemoteRenderingView);
		// pixelGLConverter = new
		// PixelGLConverter(glRemoteRenderingView.getViewFrustum(),
		// parentGLCanvas);
	}

	public final IGLRemoteRenderingView getRemoteRenderingGLView() {
		return glRemoteRenderingView;
	}

	protected void renderBusyMode(final GL2 gl) {
		float fTransparency = 0.3f * frameCounter / NUMBER_OF_FRAMES;
		float fLoadingTransparency = 0.8f * frameCounter / NUMBER_OF_FRAMES;

		if (busyState == EBusyState.ON && frameCounter < NUMBER_OF_FRAMES) {
			frameCounter++;
		} else if (busyState == EBusyState.SWITCH_OFF) {
			frameCounter--;
		}

		gl.glColor4f(1, 1, 1, fTransparency);
		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex3f(-9, -9, 4.2f);
		gl.glVertex3f(-9, 9, 4.2f);
		gl.glVertex3f(9, 9, 4.2f);
		gl.glVertex3f(9, -9, 4.2f);
		gl.glEnd();

		float fXCenter, fYCenter;
		if (this instanceof IGLRemoteRenderingView) {
			fXCenter = 0;
			fYCenter = 0;
		} else {
			fXCenter = (viewFrustum.getRight() - viewFrustum.getLeft()) / 2;
			fYCenter = (viewFrustum.getTop() - viewFrustum.getBottom()) / 2;
		}

		// TODO bad hack here, frustum wrong or renderStyle null

		Texture tempTexture = textureManager.getIconTexture(EIconTextures.LOADING);
		tempTexture.enable(gl);
		tempTexture.bind(gl);

		TextureCoords texCoords = tempTexture.getImageTexCoords();

		gl.glPushAttrib(GL2.GL_CURRENT_BIT | GL2.GL_LINE_BIT);
		gl.glColor4f(1.0f, 1.0f, 1.0f, fLoadingTransparency);

		gl.glBegin(GL2.GL_POLYGON);

		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(fXCenter - GeneralRenderStyle.LOADING_BOX_HALF_WIDTH, fYCenter
				- GeneralRenderStyle.LOADING_BOX_HALF_HEIGHT, 4.21f);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(fXCenter - GeneralRenderStyle.LOADING_BOX_HALF_WIDTH, fYCenter
				+ GeneralRenderStyle.LOADING_BOX_HALF_HEIGHT, 4.21f);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(fXCenter + GeneralRenderStyle.LOADING_BOX_HALF_WIDTH, fYCenter
				+ GeneralRenderStyle.LOADING_BOX_HALF_HEIGHT, 4.21f);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());

		gl.glVertex3f(fXCenter + GeneralRenderStyle.LOADING_BOX_HALF_WIDTH, fYCenter
				- GeneralRenderStyle.LOADING_BOX_HALF_HEIGHT, 4.21f);
		gl.glEnd();

		tempTexture.disable(gl);

		// gl.glColor4f(1.0f, 0.0f, 0.0f, 0.5f);
		Texture circleTexture = textureManager.getIconTexture(EIconTextures.LOADING_CIRCLE);
		circleTexture.enable(gl);
		circleTexture.bind(gl);
		texCoords = circleTexture.getImageTexCoords();

		gl.glTranslatef(fXCenter - 0.6f, fYCenter, 0);
		gl.glRotatef(-rotationFrameCounter, 0, 0, 1);

		gl.glBegin(GL2.GL_POLYGON);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(-0.1f, -0.1f, 4.22f);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(-0.1f, 0.1f, 4.22f);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(0.1f, 0.1f, 4.22f);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(0.1f, -0.1f, 4.22f);
		gl.glEnd();
		gl.glRotatef(+rotationFrameCounter, 0, 0, 1);
		gl.glTranslatef(fXCenter + 0.6f, fYCenter, 0);

		rotationFrameCounter += 3;
		gl.glPopAttrib();

		circleTexture.disable(gl);

		if (busyState == EBusyState.SWITCH_OFF && frameCounter <= 0) {
			pickingManager.enablePicking(true);
			busyState = EBusyState.OFF;
		}

		// System.out.println("Busy mode status: " +eBusyModeState);
	}

	/**
	 * Enables the busy mode, which renders the loading dialog and disables the picking. This method may be overridden
	 * if different behaviour is desired.
	 *
	 * @param bBusyMode
	 *            true if the busy mode should be enabled, false if it should be disabled
	 */
	public void enableBusyMode(final boolean bBusyMode) {
		if (!bBusyMode && busyState == EBusyState.ON) {
			busyState = EBusyState.SWITCH_OFF;
			pickingManager.enablePicking(true);
		} else if (bBusyMode) {
			pickingManager.enablePicking(false);
			busyState = EBusyState.ON;
		}
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
		viewScrollEventListener = new ViewScrollEventListener();
		viewScrollEventListener.setHandler(this);
		eventPublisher.addListener(ViewScrollEvent.class, viewScrollEventListener);
	}

	@Override
	public void unregisterEventListeners() {
		if (viewScrollEventListener != null) {
			eventPublisher.removeListener(viewScrollEventListener);
			viewScrollEventListener = null;
		}

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

		pickingManager.removeViewSpecificData(uniqueID);
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

	public PickingManager getPickingManager() {
		return pickingManager;
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

	public void onScrolled(ViewScrollEvent event) {
		// if (System.getProperty("os.name").contains("Mac")) {
		// scrollX = -event.getOriginX();
		// scrollY = event.getOriginY();
		// }
	}
}
