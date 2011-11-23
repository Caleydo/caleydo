package org.caleydo.core.view.opengl.canvas;

import gleem.linalg.Vec3f;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;

import org.caleydo.core.data.datadomain.IDataDomainBasedView;
import org.caleydo.core.data.id.ManagedObjectType;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.EVAOperation;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.IListenerOwner;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.exception.ExceptionHandler;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.view.AView;
import org.caleydo.core.view.ViewManager;
import org.caleydo.core.view.contextmenu.ContextMenuCreator;
import org.caleydo.core.view.opengl.camera.IViewCamera;
import org.caleydo.core.view.opengl.camera.ViewCameraBase;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.listener.GLMouseWheelListener;
import org.caleydo.core.view.opengl.canvas.listener.IMouseWheelHandler;
import org.caleydo.core.view.opengl.canvas.listener.IResettableView;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.keyboard.GLKeyListener;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingManager;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevelElement;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Composite;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;

/**
 * Abstract base class for all OpenGL2 views.
 * <p>
 * Every view has to specify its {@link #VIEW_TYPE}.
 * </p>
 * <h2>Creating a View</h2>
 * <p>
 * Views may only be instantiated using the {@link ViewManager#createGLView(Class, GLCanvas, ViewFrustum)}. As
 * a consequence, the constructor of the view may have only those two arguments (GLCanvas, ViewFrustum).
 * Otherwise, the creation will fail.
 * </p>
 * <p>
 * After the object is created, set the dataDomain (if your view is a {@link IDataDomainBasedView}). If your
 * view is rendered remotely (i.e. embedded in anothre view) you MUST call
 * {@link #setRemoteRenderingGLView(IGLRemoteRenderingView)} now! Then you MUST call {@link #initialize()}.
 * This method initializes the event listeners and other things.
 * </p>
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public abstract class AGLView
	extends AView
	implements GLEventListener, IResettableView, IMouseWheelHandler {

	public final static String VIEW_TYPE = "unspecified";
	/** The human readable view name used to identifying the type of the view */
	protected String viewLabel = "Unspecified view name";
	/** The caption of the view */
	protected String label = "Not set";

	public enum EBusyState {
		SWITCH_OFF,
		ON,
		OFF
	}

	/**
	 * The canvas rendering the view. The canvas also holds the {@link PixelGLConverter}
	 */
	protected GLCanvas parentGLCanvas;

	protected PickingManager pickingManager;

	/**
	 * Key listener which is created and registered in specific view.
	 */
	protected GLKeyListener<?> glKeyListener;

	protected GLMouseListener glMouseListener;

	protected ViewFrustum viewFrustum;

	protected IViewCamera viewCamera;

	// private FPSCounter fpsCounter;

	protected PixelGLConverter pixelGLConverter = null;

	/**
	 * The views current aspect ratio. Value gets updated when reshape is called by the JOGL2 animator.
	 */
	protected float fAspectRatio = 1f;

	protected DetailLevel detailLevel = DetailLevel.HIGH;

	/**
	 * The remote level element in which the view is placed. This variable is only set when the view is
	 * rendered remote.
	 */
	protected RemoteLevelElement remoteLevelElement;

	protected IGLRemoteRenderingView glRemoteRenderingView;

	/** Flag determining whether the display list is invalid and has to be rebuild */
	protected boolean isDisplayListDirty = true;

	/** The index of the main display list as required by opengl */
	protected int displayListIndex = 0;

	protected boolean hasFrustumChanged = false;

	protected GeneralRenderStyle renderStyle;

	protected TextureManager textureManager;

	private int frameCounter = 0;
	private int rotationFrameCounter = 0;
	private static final int NUMBER_OF_FRAMES = 15;

	protected EBusyState busyState = EBusyState.OFF;

	protected ContextMenuCreator contextMenuCreator = new ContextMenuCreator();

	/**
	 * The queue which holds the events
	 */
	private BlockingQueue<Pair<AEventListener<? extends IListenerOwner>, AEvent>> queue =
		new LinkedBlockingQueue<Pair<AEventListener<? extends IListenerOwner>, AEvent>>();

	private boolean isVisible = true;

	protected CaleydoTextRenderer textRenderer;

	/**
	 * True if the mouse is currently over this view. If lazyMode is true then the picking does not need to be
	 * rendered
	 */
	protected boolean lazyMode;

	/**
	 * picking listeners that are notified only for picks with a specific id / type combination. The key of
	 * the map is the type, the key of the internal map is the pickedObjectID
	 */
	private HashMap<String, HashMap<Integer, Set<IPickingListener>>> idPickingListeners;
	/**
	 * Picking listeners that are notified for all picks of a type. The key of the map is the type.
	 */
	private HashMap<String, Set<IPickingListener>> typePickingListeners;

	private int currentScrollBarID = 0;

	private HashSet<IMouseWheelHandler> mouseWheelListeners;

	protected GLMouseWheelListener glMouseWheelListener;

	/**
	 * Constructor. If the glCanvas object is null - then the view is rendered remote.
	 */
	protected AGLView(GLCanvas glCanvas, Composite parentComposite, final ViewFrustum viewFrustum) {

		super(GeneralManager.get().getIDCreator().createID(ManagedObjectType.GL_VIEW), parentComposite);

		GeneralManager.get().getViewManager().registerGLView(this);
		parentGLCanvas = glCanvas;

		glMouseListener = new GLMouseListener();
		glMouseListener.setNavigationModes(false, false, false);

		// Register mouse listener to GL2 canvas
		glCanvas.addMouseListener(glMouseListener);
		glCanvas.addMouseMotionListener(glMouseListener);
		glCanvas.addMouseWheelListener(glMouseListener);

		idPickingListeners = new HashMap<String, HashMap<Integer, Set<IPickingListener>>>();
		typePickingListeners = new HashMap<String, Set<IPickingListener>>();

		this.viewFrustum = viewFrustum;
		viewCamera = new ViewCameraBase(uniqueID);

		pickingManager = generalManager.getViewManager().getPickingManager();
		textureManager = new TextureManager();

		glMouseWheelListener = new GLMouseWheelListener(this);

		pixelGLConverter = new PixelGLConverter(viewFrustum, parentGLCanvas);

		mouseWheelListeners = new HashSet<IMouseWheelHandler>();

	}

	@Override
	public void initialize() {
		registerEventListeners();

		if (glRemoteRenderingView == null)
			GeneralManager.get().getViewManager().registerGLEventListenerByGLCanvas(parentGLCanvas, this);
	}

	@Override
	public void init(GLAutoDrawable drawable) {

		GL2 gl = drawable.getGL().getGL2();

		// This is specially important for Windows. Otherwise JOGL2 internally
		// slows down dramatically (factor of 10).
		gl.setSwapInterval(0);

		// fpsCounter = new FPSCounter(drawable, 16);
		// fpsCounter.setColor(0.5f, 0.5f, 0.5f, 1);

		gl.glShadeModel(GL2.GL_SMOOTH); // Enables Smooth Shading
		gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f); // white Background
		gl.glClearDepth(1.0f); // Depth Buffer Setup
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT | GL2.GL_STENCIL_BUFFER_BIT);

		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glDepthFunc(GL2.GL_LEQUAL);

		gl.glEnable(GL2.GL_BLEND);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

		// gl.glEnable(GL2.GL_POINT_SMOOTH);
		// gl.glHint(GL2.GL_POINT_SMOOTH_HINT, GL2.GL_NICEST);
		gl.glEnable(GL2.GL_LINE_SMOOTH);
		gl.glHint(GL2.GL_LINE_SMOOTH_HINT, GL2.GL_NICEST);
		// gl.glEnable(GL2.GL_POLYGON_SMOOTH);
		// gl.glHint(GL2.GL_POLYGON_SMOOTH_HINT, GL2.GL_NICEST);

		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);

		gl.glEnable(GL2.GL_COLOR_MATERIAL);
		gl.glColorMaterial(GL2.GL_FRONT, GL2.GL_DIFFUSE);

		glMouseListener.addGLCanvas(this);
		pixelGLConverter = new PixelGLConverter(viewFrustum, parentGLCanvas);
		initLocal(gl);
	}

	/**
	 * @param label
	 *            setter, see {@link #viewLabel}
	 */
	public void setViewLabel(String viewLabel) {
		this.viewLabel = viewLabel;
	}

	/**
	 * @return the label, see {@link #viewLabel}
	 */
	public String getViewLabel() {
		return viewLabel;
	}

	@Override
	public final void display(GLAutoDrawable drawable) {
		try {
			processEvents();
			if (!isVisible())
				return;

			final Vec3f rot_Vec3f = new Vec3f();
			final Vec3f position = viewCamera.getCameraPosition();

			GL2 gl = drawable.getGL().getGL2();

			// load identity matrix
			gl.glMatrixMode(GL2.GL_MODELVIEW);
			gl.glLoadIdentity();

			// clear screen
			gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

			gl.glTranslatef(position.x(), position.y(), position.z());
			gl.glRotatef(viewCamera.getCameraRotationGrad(rot_Vec3f), rot_Vec3f.x(), rot_Vec3f.y(),
				rot_Vec3f.z());

			displayLocal(gl);

			// fpsCounter.draw();
		}
		catch (RuntimeException exception) {
			ExceptionHandler.get().handleViewException(exception, this);
		}
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

		if (glRemoteRenderingView != null || this.getViewType().equals("org.caleydo.view.bucket")
			|| this.getViewType().equals("org.caleydo.view.dataflipper")) {
			viewFrustum.considerAspectRatio(true);
		}
		else {
			// normalize between 0 and 8
			Rectangle frame = parentGLCanvas.getBounds();
			viewFrustum.setLeft(0);
			// viewFrustum.setRight(8);// frame.width / 100);
			viewFrustum.setBottom(0);
			float value = (float) frame.height / (float) frame.width * 8.0f;

			// Special case for embedded heatmap in hierarchical heatmap
			// if (this.getViewType().equals("org.caleydo.view.heatmap.hierarchical"))
			// viewFrustum.setTop(5.51f);
			// else
			viewFrustum.setTop(value);

			viewFrustum.setRight(8);

			setDisplayListDirty();
			hasFrustumChanged = true;
		}

		GL2 gl = drawable.getGL().getGL2();

		fAspectRatio = (float) height / (float) width;

		gl.glViewport(x, y, width, height);
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();

		viewFrustum.setProjectionMatrix(gl, fAspectRatio);
		updateDetailMode();
	}

	/**
	 * <p>
	 * Method responsible for initialization of the data. It is intended to be overridden, all subclasses must
	 * use this method to initialize their members related to {@link AView#table}.
	 * </p>
	 */
	public void initData() {
	}

	/**
	 * Clears all selections, meaning that no element is selected or deselected after this method was called.
	 * Everything returns to "normal". Note that virtual array manipulations are not considered selections and
	 * are therefore not retable.
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
		gl.glClear(GL2.GL_STENCIL_BUFFER_BIT);
		gl.glColorMask(false, false, false, false);
		gl.glClearStencil(0); // Clear The Stencil Buffer To 0
		gl.glEnable(GL2.GL_DEPTH_TEST); // Enables Depth Testing
		gl.glDepthFunc(GL2.GL_LEQUAL); // The Type Of Depth Testing To Do
		gl.glEnable(GL2.GL_STENCIL_TEST);
		gl.glStencilFunc(GL2.GL_ALWAYS, 1, 1);
		gl.glStencilOp(GL2.GL_KEEP, GL2.GL_KEEP, GL2.GL_REPLACE);
		gl.glDisable(GL2.GL_DEPTH_TEST);

		// Clip region that renders in stencil buffer (in this case the
		// frustum)
		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex3f(viewFrustum.getLeft(), viewFrustum.getBottom(), -0.01f);
		gl.glVertex3f(viewFrustum.getRight(), viewFrustum.getBottom(), -0.01f);
		gl.glVertex3f(viewFrustum.getRight(), viewFrustum.getTop(), -0.01f);
		gl.glVertex3f(viewFrustum.getLeft(), viewFrustum.getTop(), -0.01f);
		gl.glEnd();

		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glColorMask(true, true, true, true);
		gl.glStencilFunc(GL2.GL_EQUAL, 1, 1);
		gl.glStencilOp(GL2.GL_KEEP, GL2.GL_KEEP, GL2.GL_KEEP);

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
	public abstract void initRemote(final GL2 gl, final AGLView glParentView,
		final GLMouseListener glMouseListener);

	/**
	 * GL2 display method that has to be called in all cases manually, either by {@link #displayLocal(GL)} or
	 * {@link #displayRemote(GL)}. It must be responsible for rendering the scene. It is also called by the
	 * picking manager.
	 * 
	 * @param gl
	 */
	public abstract void display(final GL2 gl);

	@Override
	public final synchronized void queueEvent(AEventListener<? extends IListenerOwner> listener, AEvent event) {
		queue.add(new Pair<AEventListener<? extends IListenerOwner>, AEvent>(listener, event));
	}

	/**
	 * This method should be called every display cycle when it is save to change the state of the object. It
	 * processes all the previously submitted events.
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
	 * This method is called by the animator of a registered class. It should not be called by anyone else,
	 * but has to call the local {@link #display(GL)}, where the actual rendering must happen. If a view is
	 * rendered remote, this method may not be called - instead use {@link #displayRemote(GL)}.
	 * </p>
	 * <p>
	 * Typically a displayLocal should contain:
	 * <ul>
	 * <li>a call to {@link #processEvents()}, where the event queue is processed</li>
	 * <li>a call to the {@link #processEvents()} method of all views it renders locally</li>
	 * <li>this has to be followed by a check whether the view is active, using {@link #isVisible}. If the
	 * view is inactive it should return at this point.</li>
	 * <li>a call to the {@link PickingManager#handlePicking(AGLView, GL)} method, which renders the scene in
	 * picking mode.</li>
	 * <li>and finally a call to the local display</li>
	 * </ul>
	 * 
	 * @param gl
	 */
	protected abstract void displayLocal(final GL2 gl);

	/**
	 * Intended for external use when another instance of a view manages the scene. This is specially designed
	 * for composite views. Has to call display internally! The steps necessary in {@link #displayLocal(GL)},
	 * such as handling of events and picking have to be taken care of the instance calling this method.
	 * 
	 * @param gl
	 */
	public abstract void displayRemote(final GL2 gl);

	public final GLCanvas getParentGLCanvas() {

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
	 * Set the level of detail to be displayed, choose from the options in {@link DetailLevel}. If the
	 * specified detail level differs from the current {@link #setDisplayListDirty()} is called.
	 * 
	 * @param detailLevel
	 */
	public void setDetailLevel(DetailLevel detailLevel) {
		if (this.detailLevel != detailLevel) {
			this.detailLevel = detailLevel;
			setDisplayListDirty();
		}
	}

	private void updateDetailMode() {

		DetailLevel newDetailLevel;
		int pixelWidth = pixelGLConverter.getPixelWidthForGLWidth(viewFrustum.getWidth());
		int pixelHeight = pixelGLConverter.getPixelHeightForGLHeight(viewFrustum.getHeight());
		if (pixelHeight > getMinPixelHeight(DetailLevel.HIGH)
			&& pixelWidth > getMinPixelWidth(DetailLevel.HIGH)) {
			newDetailLevel = DetailLevel.HIGH;
		}
		else if (pixelHeight > getMinPixelHeight(DetailLevel.MEDIUM)
			&& pixelWidth > getMinPixelWidth(DetailLevel.MEDIUM)) {
			newDetailLevel = DetailLevel.MEDIUM;
		}
		else {
			newDetailLevel = DetailLevel.LOW;
		}
		setDetailLevel(newDetailLevel);

	}

	/**
	 * Check whether we had a picking hit somewhere during the previous run
	 * 
	 * @param gl
	 */
	protected final void checkForHits(final GL2 gl) {

		Set<String> hitTypes = pickingManager.getHitTypes(uniqueID);
		if (hitTypes == null)
			return;

		contextMenuCreator.clear();

		for (String pickingType : hitTypes) {

			ArrayList<Pick> alHits = null;

			alHits = pickingManager.getHits(uniqueID, pickingType);
			if (alHits != null && alHits.size() != 0) {

				for (int iCount = 0; iCount < alHits.size(); iCount++) {
					Pick tempPick = alHits.get(iCount);
					int pickedObjectID = tempPick.getID();
					if (pickedObjectID == -1) {
						continue;
					}

					PickingMode ePickingMode = tempPick.getPickingMode();

					handlePicking(pickingType, ePickingMode, pickedObjectID, tempPick);
					// FIXME: This is for legacy support -> picking listeners should be used

					try {
						PickingType type = PickingType.valueOf(pickingType);
						try {
							handlePickingEvents(type, ePickingMode, pickedObjectID, tempPick);
						}
						catch (Exception e) {
							Logger.log(new Status(Status.ERROR, this.toString(),
								"Caught exception when picking", e));
						}
					}
					catch (IllegalArgumentException e) {
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
			for (IPickingListener pickingListener : pickingListeners) {
				notifyPickingListener(pickingListener, pickingMode, pick);
			}
		}

		HashMap<Integer, Set<IPickingListener>> map = idPickingListeners.get(pickingType);
		if (map == null)
			return;

		pickingListeners = map.get(pickedObjectID);

		if (pickingListeners != null) {
			for (IPickingListener pickingListener : pickingListeners) {
				notifyPickingListener(pickingListener, pickingMode, pick);
			}
		}
	}

	private void notifyPickingListener(IPickingListener pickingListener, PickingMode pickingMode, Pick pick) {
		if (pickingListener == null)
			return;

		switch (pickingMode) {
			case CLICKED:
				pickingListener.clicked(pick);
				break;
			case DOUBLE_CLICKED:
				pickingListener.doubleClicked(pick);
				break;
			case RIGHT_CLICKED:
				pickingListener.rightClicked(pick);
				break;
			case MOUSE_OVER:
				pickingListener.mouseOver(pick);
				break;
			case DRAGGED:
				pickingListener.dragged(pick);
				break;
			case MOUSE_OUT:
				pickingListener.mouseOut(pick);
				break;
		}
	}

	/**
	 * Registers a {@link IPickingListener} for this view that is called when objects with the specified
	 * pickingType <b>and</b> ID are picked.
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
			if (listener.getClass() == pickingListener.getClass()) {
				return;
			}
		}
		pickingListeners.add(pickingListener);
		map.put(pickedObjectID, pickingListeners);

	}

	/**
	 * Registers a {@link IPickingListener} for this view that is call whenever an object of the specified
	 * type was picked, independent of the object's picking id.
	 * 
	 * @see AGLView#addIDPickingListener(IPickingListener, String, int) for picking id dependend
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
	 * Removes the specified {@link IPickingListener} for single ids that has been added with the specified
	 * picking type and id.
	 * 
	 * @param pickingListener
	 * @param pickingType
	 * @param pickedObjectID
	 */
	public void removeIDPickingListener(IPickingListener pickingListener, String pickingType,
		int pickedObjectID) {
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
	 * {@link #removeAllTypePickingListeners(String)} is preferred to using this method for performance
	 * reasons.
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
	 * This method is called every time a method occurs. It should take care of reacting appropriately to the
	 * events.
	 * 
	 * @param pickingType
	 *            the Picking type, held in EPickingType
	 * @param pickingMode
	 *            the Picking mode (clicked, dragged etc.)
	 * @param pickingID
	 *            the name specified for an element with glPushName
	 * @param pick
	 *            the pick object which can be useful to retrieve for example the mouse position when the pick
	 *            occurred
	 * @deprecated replaced by picking listeners. No longer abstract since it's not neccessary for views to
	 *             implement
	 */
	@Deprecated
	protected void handlePickingEvents(final PickingType pickingType, final PickingMode pickingMode,
		final int pickingID, final Pick pick) {
	}

	public final IViewCamera getViewCamera() {
		return viewCamera;
	}

	/**
	 * Broadcast elements only with a given type. This is used only for pathways so that the genes in a
	 * pathway are removed when it is closed
	 */
	@Deprecated
	public void broadcastElements(EVAOperation type) {
	}

	public void setRemoteLevelElement(RemoteLevelElement element) {
		this.remoteLevelElement = element;
	}

	public RemoteLevelElement getRemoteLevelElement() {
		return remoteLevelElement;
	}

	public final boolean isRenderedRemote() {
		return glRemoteRenderingView != null;
	}

	public final void setRemoteRenderingGLView(IGLRemoteRenderingView glRemoteRenderingView) {
		this.glRemoteRenderingView = glRemoteRenderingView;
		pixelGLConverter = glRemoteRenderingView.getPixelGLConverter();
		// pixelGLConverter = new PixelGLConverter(glRemoteRenderingView.getViewFrustum(), parentGLCanvas);
	}

	public final IGLRemoteRenderingView getRemoteRenderingGLView() {
		return glRemoteRenderingView;
	}

	protected void renderBusyMode(final GL2 gl) {
		float fTransparency = 0.3f * frameCounter / NUMBER_OF_FRAMES;
		float fLoadingTransparency = 0.8f * frameCounter / NUMBER_OF_FRAMES;

		if (busyState == EBusyState.ON && frameCounter < NUMBER_OF_FRAMES) {
			frameCounter++;
		}
		else if (busyState == EBusyState.SWITCH_OFF) {
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
		}
		else {
			fXCenter = (viewFrustum.getRight() - viewFrustum.getLeft()) / 2;
			fYCenter = (viewFrustum.getTop() - viewFrustum.getBottom()) / 2;
		}

		// TODO bad hack here, frustum wrong or renderStyle null

		Texture tempTexture = textureManager.getIconTexture(gl, EIconTextures.LOADING);
		tempTexture.enable();
		tempTexture.bind();

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

		tempTexture.disable();

		// gl.glColor4f(1.0f, 0.0f, 0.0f, 0.5f);
		Texture circleTexture = textureManager.getIconTexture(gl, EIconTextures.LOADING_CIRCLE);
		circleTexture.enable();
		circleTexture.bind();
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

		circleTexture.disable();

		if (busyState == EBusyState.SWITCH_OFF && frameCounter <= 0) {
			pickingManager.enablePicking(true);
			busyState = EBusyState.OFF;
		}

		// System.out.println("Busy mode status: " +eBusyModeState);
	}

	/**
	 * Enables the busy mode, which renders the loading dialog and disables the picking. This method may be
	 * overridden if different behaviour is desired.
	 * 
	 * @param bBusyMode
	 *            true if the busy mode should be enabled, false if it should be disabled
	 */
	public void enableBusyMode(final boolean bBusyMode) {
		if (!bBusyMode && busyState == EBusyState.ON) {
			busyState = EBusyState.SWITCH_OFF;
			pickingManager.enablePicking(true);
		}
		else if (bBusyMode) {
			pickingManager.enablePicking(false);
			busyState = EBusyState.ON;
		}
	}

	@Deprecated
	public abstract int getNumberOfSelections(SelectionType SelectionType);

	public final float getAspectRatio() {
		return fAspectRatio;
	}

	public final DetailLevel getDetailLevel() {
		return detailLevel;
	}

	public void destroy() {
		// Propagate remove action of elements to other views
		this.broadcastElements(EVAOperation.REMOVE_ELEMENT);

		pickingManager.removeViewSpecificData(uniqueID);

		generalManager.getViewManager().getConnectedElementRepresentationManager().clearAll();
		generalManager.getViewManager().unregisterGLView(this);
		unregisterEventListeners();
	}

	// @Override
	// public synchronized Pair<AEventListener<? extends IListenerOwner>, AEvent> getEvent() {
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
	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	/**
	 * Check whether the view is visible. If not, it should not be rendered. Note that events should be
	 * processed anyway.
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
	protected void renderSymbol(GL2 gl, EIconTextures texture, float buttonSize) {

		float xButtonOrigin = viewFrustum.getLeft() + viewFrustum.getWidth() / 2 - buttonSize / 2;
		float yButtonOrigin = viewFrustum.getBottom() + viewFrustum.getHeight() / 2 - buttonSize / 2;
		Texture tempTexture = textureManager.getIconTexture(gl, texture);
		tempTexture.enable();
		tempTexture.bind();

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
		tempTexture.disable();
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub

	}

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
	 * @return The minimum height in pixels the view currently requires to show its content properly. The
	 *         default implementation in the base class calls {@link #getMinPixelheight()} with
	 *         {@link DetailLevel#LOW}
	 */
	public int getMinPixelHeight() {
		return getMinPixelHeight(DetailLevel.LOW);
	}

	/**
	 * @return The minimum width in pixels the view currently requires to show its content properly. The
	 *         default implementation in the base class calls {@link #getMinPixelWidth()} with
	 *         {@link DetailLevel#LOW}
	 */
	public int getMinPixelWidth() {
		return getMinPixelWidth(DetailLevel.LOW);
	}

	/**
	 * @return The minimum height in pixels the view requires to show its content properly with the specified
	 *         detail level.
	 */
	public int getMinPixelHeight(DetailLevel detailLevel) {
		return 0;
	}

	/**
	 * @return The minimum width in pixels the view requires to show its content properly with the specified
	 *         detail level.
	 */
	public int getMinPixelWidth(DetailLevel detailLevel) {
		return 0;
	}

	/**
	 * Gets the highest possible detail level the view is able to display its content with, using the
	 * specified width and height.
	 * 
	 * @param pixelHeight
	 * @param pixelWidth
	 * @return
	 */
	public DetailLevel getHightestPossibleDetailLevel(int pixelHeight, int pixelWidth) {
		return DetailLevel.LOW;
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

	/** Returns the heading of the view, should be overriden in the subviews*/
	public String getLabel() {
		return viewLabel;
	}
	
	/**
	 * Set the heading of the view
	 * @param label
	 */
	public void setLabel(String label)
	{
		this.label = label;
	}
}
