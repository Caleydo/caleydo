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

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.id.ManagedObjectType;
import org.caleydo.core.data.mapping.IDMappingManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.EVAOperation;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.IListenerOwner;
import org.caleydo.core.manager.event.view.ToggleMagnifyingGlassEvent;
import org.caleydo.core.manager.picking.PickingMode;
import org.caleydo.core.manager.picking.PickingType;
import org.caleydo.core.manager.picking.IPickingListener;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.exception.ExceptionHandler;
import org.caleydo.core.view.AView;
import org.caleydo.core.view.opengl.camera.IViewCamera;
import org.caleydo.core.view.opengl.camera.ViewCameraBase;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.listener.GLMouseWheelListener;
import org.caleydo.core.view.opengl.canvas.listener.IMouseWheelHandler;
import org.caleydo.core.view.opengl.canvas.listener.IResettableView;
import org.caleydo.core.view.opengl.canvas.listener.ToggleMagnifyingGlassListener;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.keyboard.GLKeyListener;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;
import org.caleydo.core.view.opengl.util.GLMagnifyingGlass;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevelElement;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.ContextMenu;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
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
 * Views may only be instantiated using the
 * {@link ViewManager#createGLView(Class, GLCanvas, ViewFrustum)}. As a consequence, the constructor of
 * the view may have only those two arguments (GLCanvas, ViewFrustum). Otherwise, the creation will
 * fail.
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
/**
 * @author test
 */
public abstract class AGLView
	extends AView
	implements GLEventListener, IResettableView, IMouseWheelHandler {

	public final static String VIEW_TYPE = "unspecified";

	public enum EBusyModeState {
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
	
//	private FPSCounter fpsCounter;

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

	protected boolean bIsDisplayListDirtyLocal = true;
	protected boolean bIsDisplayListDirtyRemote = true;

	protected int iGLDisplayListIndexLocal;
	protected int iGLDisplayListIndexRemote;

	protected int iGLDisplayListToCall = 0;

	protected boolean bHasFrustumChanged = false;

	protected GeneralRenderStyle renderStyle;

	protected TextureManager textureManager;

	private int iFrameCounter = 0;
	private int iRotationFrameCounter = 0;
	private static final int NUMBER_OF_FRAMES = 15;
	
	protected GLMagnifyingGlass magnifyingGlass;

	private ToggleMagnifyingGlassListener magnifyingGlassListener;

	private boolean bShowMagnifyingGlass;

	protected EBusyModeState eBusyModeState = EBusyModeState.OFF;

	protected IDMappingManager idMappingManager;

	/**
	 * The virtual array that manages the contents (the indices) in the storages
	 */
	protected ContentVirtualArray contentVA;
	/**
	 * The type of the content VA
	 */
	protected String contentVAType = DataTable.RECORD;

	/**
	 * The id of the virtual array that manages the storage references in the set
	 */
	protected DimensionVirtualArray storageVA;
	/**
	 * The type of the storage VA
	 */
	protected String storageVAType = DataTable.DIMENSION;

	/**
	 * The context menu each view should implement. It has to be created in initLocal or is set via initRemote
	 */
	protected ContextMenu contextMenu;

	/**
	 * The queue which holds the events
	 */
	private BlockingQueue<Pair<AEventListener<? extends IListenerOwner>, AEvent>> queue =
		new LinkedBlockingQueue<Pair<AEventListener<? extends IListenerOwner>, AEvent>>();

	// /** id of the related view in the gui (e.g. RCP) */
	// private String viewGUIID;

	private boolean isVisible = true;

	protected CaleydoTextRenderer textRenderer;

	/**
	 * True if the mouse is currently over this view. If lazyMode is true then the picking does not need to be
	 * rendered
	 */
	protected boolean lazyMode;

	private HashMap<String, HashMap<Integer, Set<IPickingListener>>> singleIDPickingListeners;
	private HashMap<String, Set<IPickingListener>> multiIDPickingListeners;

	private int currentScrollBarID = 0;

	private HashSet<IMouseWheelHandler> mouseWheelListeners;
	
	protected GLMouseWheelListener glMouseWheelListener;

	/**
	 * Constructor. If the glCanvas object is null - then the view is rendered remote.
	 */
	protected AGLView(GLCanvas glCanvas, Composite parentComposite, final ViewFrustum viewFrustum) {

		super(GeneralManager.get().getIDCreator().createID(ManagedObjectType.GL_VIEW), parentComposite);

		GeneralManager.get().getViewGLCanvasManager().registerGLView(this);
		parentGLCanvas = glCanvas;

		glMouseListener = new GLMouseListener();
		glMouseListener.setNavigationModes(false, false, false);
		
		// Register mouse listener to GL2 canvas
		glCanvas.addMouseListener(glMouseListener);
		glCanvas.addMouseMotionListener(glMouseListener);
		glCanvas.addMouseWheelListener(glMouseListener);
		
		singleIDPickingListeners = new HashMap<String, HashMap<Integer, Set<IPickingListener>>>();
		multiIDPickingListeners = new HashMap<String, Set<IPickingListener>>();

		this.viewFrustum = viewFrustum;
		viewCamera = new ViewCameraBase(uniqueID);

		pickingManager = generalManager.getViewGLCanvasManager().getPickingManager();
		idMappingManager = generalManager.getIDMappingManager();
		textureManager = new TextureManager();
		contextMenu = ContextMenu.get();
		
		glMouseWheelListener = new GLMouseWheelListener(this);

		bShowMagnifyingGlass = false;

		pixelGLConverter = new PixelGLConverter(viewFrustum, parentGLCanvas);

		mouseWheelListeners = new HashSet<IMouseWheelHandler>();

	}

	@Override
	public void initialize() {
		registerEventListeners();
		
		 if (glRemoteRenderingView == null)
             GeneralManager.get().getViewGLCanvasManager()
                     .registerGLEventListenerByGLCanvas(parentGLCanvas, this);
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

		initLocal(gl);
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

			// if (bShowMagnifyingGlass) {
			// if (magnifyingGlass == null) {
			// magnifyingGlass = new GLMagnifyingGlass();
			// }
			// magnifyingGlass.draw(gl, glMouseListener);
			// }
			
			// fpsCounter.draw();
		}
		catch (RuntimeException exception) {
			ExceptionHandler.get().handleViewException(exception, this);
		}
	}

	// @Override
	// public final void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
	//
	// ((GLEventListener) parentGLCanvas).displayChanged(drawable, modeChanged, deviceChanged);
	// }

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
			bHasFrustumChanged = true;
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
	 * <p>
	 * Note: This is completely independent of {@link #init(GL)}
	 * </p>
	 */
	public void initData() {
	}

	/**
	 * Clears all selections, meaning that no element is selected or deselected after this method was called.
	 * Everything returns to "normal". Note that virtual array manipulations are not considered selections and
	 * are therefore not reset.
	 */
	public abstract void clearAllSelections();

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
		bIsDisplayListDirtyLocal = true;
		bIsDisplayListDirtyRemote = true;
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
		if (this.isRenderedRemote())
			return getRemoteRenderingGLCanvas().getParentGLCanvas();

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

	private void updateDetailMode() {
		int pixelWidth = pixelGLConverter.getPixelWidthForGLWidth(viewFrustum.getWidth());
		int pixelHeight = pixelGLConverter.getPixelHeightForGLHeight(viewFrustum.getHeight());
		if (pixelHeight > getMinPixelHeight(DetailLevel.HIGH)
			&& pixelWidth > getMinPixelWidth(DetailLevel.HIGH)) {
			setDetailLevel(DetailLevel.HIGH);
		}
		else if (pixelHeight > getMinPixelHeight(DetailLevel.MEDIUM)
			&& pixelWidth > getMinPixelWidth(DetailLevel.MEDIUM)) {
			setDetailLevel(DetailLevel.MEDIUM);
		}
		else
			setDetailLevel(DetailLevel.LOW);
		setDisplayListDirty();
	}

	/**
	 * This class uses the pickingManager to check if any events have occurred it calls the abstract
	 * handleEvents method where the events should be handled
	 * 
	 * @param gl
	 */
	protected final void checkForHits(final GL2 gl) {

		Set<String> hitTypes = pickingManager.getHitTypes(uniqueID);
		if (hitTypes == null)
			return;
		for (String pickingType : hitTypes) {

			ArrayList<Pick> alHits = null;

			alHits = pickingManager.getHits(uniqueID, pickingType);
			if (alHits != null && alHits.size() != 0) {

				for (int iCount = 0; iCount < alHits.size(); iCount++) {
					Pick tempPick = alHits.get(iCount);
					int externalID = tempPick.getID();
					if (externalID == -1) {
						continue;
					}

					PickingMode ePickingMode = tempPick.getPickingMode();
					if (pickingType == PickingType.CONTEXT_MENU_SELECTION.name()
						|| pickingType == PickingType.CONTEXT_MENU_SCROLL_DOWN.name()
						|| pickingType == PickingType.CONTEXT_MENU_SCROLL_UP.name()) {
						contextMenu.handlePickingEvents(PickingType.valueOf(pickingType), ePickingMode,
							externalID);
					}
					else {
						if (tempPick.getPickingMode() != PickingMode.RIGHT_CLICKED)
							contextMenu.flush();
						handlePicking(pickingType, ePickingMode, externalID, tempPick);
						// FIXME: This is for legacy support -> picking listeners should be used
						try {
							handlePickingEvents(PickingType.valueOf(pickingType), ePickingMode, externalID,
								tempPick);
						}
						catch (Exception e) {

						}
					}
					pickingManager.flushHits(uniqueID, pickingType);
				}
			}
		}
	}

	protected void handlePicking(String pickingType, PickingMode pickingMode, int pickingID, Pick pick) {

		Set<IPickingListener> pickingListeners = multiIDPickingListeners.get(pickingType);

		if (pickingListeners != null) {
			for (IPickingListener pickingListener : pickingListeners) {
				handlePicking(pickingListener, pickingMode, pick);
			}
		}

		HashMap<Integer, Set<IPickingListener>> map = singleIDPickingListeners.get(pickingType);
		if (map == null)
			return;

		pickingListeners = map.get(pickingID);

		if (pickingListeners != null) {
			for (IPickingListener pickingListener : pickingListeners) {
				handlePicking(pickingListener, pickingMode, pick);
			}
		}
	}

	private void handlePicking(IPickingListener pickingListener, PickingMode pickingMode, Pick pick) {
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
		}
	}

	/**
	 * Registers a {@link IPickingListener} for this view. When objects are picked with the specified
	 * pickingType and ID the listener's methods are called. Note that only one instance of the same
	 * PickingListener class can be added to a picking type plus ID.
	 * 
	 * @param pickingListener
	 * @param pickingType
	 * @param externalID
	 */
	public void addSingleIDPickingListener(IPickingListener pickingListener, String pickingType,
		int externalID) {
		HashMap<Integer, Set<IPickingListener>> map = singleIDPickingListeners.get(pickingType);
		if (map == null) {
			map = new HashMap<Integer, Set<IPickingListener>>();
			singleIDPickingListeners.put(pickingType, map);
		}
		Set<IPickingListener> pickingListeners = map.get(externalID);
		if (pickingListeners == null) {
			pickingListeners = new HashSet<IPickingListener>();

		}
		for (IPickingListener listener : pickingListeners) {
			if (listener.getClass() == pickingListener.getClass()) {
				return;
			}
		}
		pickingListeners.add(pickingListener);
		map.put(externalID, pickingListeners);

	}

	/**
	 * Registers a {@link IPickingListener} for this view. When objects are picked with the specified
	 * pickingType the listener's methods are called. Note that only one instance of the same PickingListener
	 * class can be added to a picking type.
	 * 
	 * @param pickingListener
	 * @param pickingType
	 */
	public void addMultiIDPickingListener(IPickingListener pickingListener, String pickingType) {
		Set<IPickingListener> pickingListeners = multiIDPickingListeners.get(pickingType);
		if (pickingListeners == null) {
			pickingListeners = new HashSet<IPickingListener>();

		}
		for (IPickingListener listener : pickingListeners) {
			if (listener.getClass() == pickingListener.getClass()) {
				return;
			}
		}
		pickingListeners.add(pickingListener);
		multiIDPickingListeners.put(pickingType, pickingListeners);
	}

	/**
	 * Removes the specified {@link IPickingListener} for single ids that has been added with the specified
	 * picking type and id.
	 * 
	 * @param pickingListener
	 * @param pickingType
	 * @param externalID
	 */
	public void removeSingleIDPickingListener(IPickingListener pickingListener, String pickingType,
		int externalID) {
		HashMap<Integer, Set<IPickingListener>> map = singleIDPickingListeners.get(pickingType);
		if (map == null) {
			return;
		}

		Set<IPickingListener> pickingListeners = map.get(externalID);
		if (pickingListeners == null) {
			return;
		}
		pickingListeners.remove(pickingListener);
	}

	/**
	 * Removes the specified {@link IPickingListener} for multiple ids that has been added with the specified
	 * picking type.
	 * 
	 * @param pickingListener
	 * @param pickingType
	 */
	public void removeMultiIDPickingListener(IPickingListener pickingListener, String pickingType) {
		Set<IPickingListener> pickingListeners = multiIDPickingListeners.get(pickingType);
		if (pickingListeners == null) {
			return;
		}
		pickingListeners.remove(pickingListener);
	}

	/**
	 * Removes the specified {@link IPickingListener} for single ids that has been added with any picking type
	 * or id.
	 * 
	 * @param pickingListener
	 */
	public void removeSingleIDPickingListener(IPickingListener pickingListener) {

		for (HashMap<Integer, Set<IPickingListener>> map : singleIDPickingListeners.values()) {
			if (map != null) {
				for (Set<IPickingListener> pickingListeners : map.values()) {
					if (pickingListeners != null) {
						pickingListeners.remove(pickingListener);
					}
				}
			}
		}

	}

	/**
	 * Removes the specified {@link IPickingListener} for multiple ids that has been added with any picking
	 * type.
	 * 
	 * @param pickingListener
	 */
	public void removeMultiIDPickingListener(IPickingListener pickingListener) {
		for (Set<IPickingListener> pickingListeners : multiIDPickingListeners.values()) {
			if (pickingListeners != null) {
				pickingListeners.remove(pickingListener);
			}
		}
	}

	/**
	 * Equal to calling both of the methods {@link #removeMultiIDPickingListener(IPickingListener)} and
	 * {@link #removeSingleIDPickingListener(IPickingListener)}.
	 * 
	 * @param pickingListener
	 */
	public void removePickingListener(IPickingListener pickingListener) {
		removeSingleIDPickingListener(pickingListener);
		removeMultiIDPickingListener(pickingListener);
	}

	/**
	 * Removes all single ID picking listeners for a specific picking type and ID.
	 * 
	 * @param pickingType
	 * @param externalID
	 */
	public void removeSingleIDPickingListeners(String pickingType, int externalID) {

		HashMap<Integer, Set<IPickingListener>> map = singleIDPickingListeners.get(pickingType);
		if (map == null) {
			return;
		}

		Set<IPickingListener> pickingListeners = map.get(externalID);
		if (pickingListeners == null) {
			return;
		}
		pickingListeners.clear();
	}
	
	/**
	 * Removes all Multiple ID picking listeners for a specific picking type.
	 * 
	 * @param pickingType
	 */
	public void removeMultiIDPickingListeners(String pickingType) {

		Set<IPickingListener> pickingListeners = multiIDPickingListeners.get(pickingType);
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
	 */
	abstract protected void handlePickingEvents(final PickingType pickingType,
		final PickingMode pickingMode, final int pickingID, final Pick pick);

	/**
	 * Returns a short info string about the view. Typically this should mention the name of the view plus the
	 * number of elements displayed. This method is intended to be called only by remote views, for calls
	 * within the local class implementing this view use {@link #getShortInfoLocal()}.
	 * 
	 * @return the info string
	 */
	public abstract String getShortInfo();

	/**
	 * The variaton of {@link #getShortInfo()} which must be called locally. This takes care of integrating
	 * possible information from a remote rendering view.
	 * 
	 * @return the info string
	 */
	protected String getShortInfoLocal() {
		if (isRenderedRemote())
			return (((AGLView) getRemoteRenderingGLCanvas()).getShortInfo());
		else
			return getShortInfo();
	}

	/**
	 * Returns a extensive info string about the view. Typically this should mention the name of the view plus
	 * the number of elements displayed, plus possible states of the view. This method is intended to be
	 * called only by remote views, for calls within the local class implementing this view use
	 * {@link #getShortInfoLocal()}.
	 * 
	 * @return the info string
	 */
	public abstract String getDetailedInfo();

	/**
	 * The variaton of {@link #getDetailedInfo()} which must be called locally. This takes care of integrating
	 * possible information from a remote rendering view.
	 * 
	 * @return the info string
	 */
	protected String getDetailInfoLocal() {
		if (isRenderedRemote())
			return (((AGLView) getRemoteRenderingGLCanvas()).getDetailedInfo());
		else
			return getDetailedInfo();
	}

	public final IViewCamera getViewCamera() {
		return viewCamera;
	}

	/**
	 * Broadcast elements only with a given type.
	 */
	public abstract void broadcastElements(EVAOperation type);

	/**
	 * Set the level of detail to be displayed
	 * 
	 * @param detailLevel
	 */
	public void setDetailLevel(DetailLevel detailLevel) {
		this.detailLevel = detailLevel;
		setDisplayListDirty();
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

	public final boolean rendersContextOnly() {
		if (contentVAType.equals(DataTable.RECORD))
			return false;
		return true;
	}

	public final void setRemoteRenderingGLView(IGLRemoteRenderingView glRemoteRenderingView) {
		this.glRemoteRenderingView = glRemoteRenderingView;
		pixelGLConverter = new PixelGLConverter(glRemoteRenderingView.getViewFrustum(), parentGLCanvas);
	}

	public final IGLRemoteRenderingView getRemoteRenderingGLCanvas() {
		return glRemoteRenderingView;
	}

	protected void renderBusyMode(final GL2 gl) {
		float fTransparency = 0.3f * iFrameCounter / NUMBER_OF_FRAMES;
		float fLoadingTransparency = 0.8f * iFrameCounter / NUMBER_OF_FRAMES;

		if (eBusyModeState == EBusyModeState.ON && iFrameCounter < NUMBER_OF_FRAMES) {
			iFrameCounter++;
		}
		else if (eBusyModeState == EBusyModeState.SWITCH_OFF) {
			iFrameCounter--;
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
		gl.glRotatef(-iRotationFrameCounter, 0, 0, 1);

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
		gl.glRotatef(+iRotationFrameCounter, 0, 0, 1);
		gl.glTranslatef(fXCenter + 0.6f, fYCenter, 0);

		iRotationFrameCounter += 3;
		gl.glPopAttrib();

		circleTexture.disable();

		if (eBusyModeState == EBusyModeState.SWITCH_OFF && iFrameCounter <= 0) {
			pickingManager.enablePicking(true);
			eBusyModeState = EBusyModeState.OFF;
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
		if (!bBusyMode && eBusyModeState == EBusyModeState.ON) {
			eBusyModeState = EBusyModeState.SWITCH_OFF;
			pickingManager.enablePicking(true);
		}
		else if (bBusyMode) {
			pickingManager.enablePicking(false);
			eBusyModeState = EBusyModeState.ON;
		}
	}

	public abstract int getNumberOfSelections(SelectionType SelectionType);

	public final float getAspectRatio() {
		return fAspectRatio;
	}

	public ContentVirtualArray getContentVA() {
		return contentVA;
	}

	public final DimensionVirtualArray getStorageVA() {
		return storageVA;
	}

	public final DetailLevel getDetailLevel() {
		return detailLevel;
	}

	public void destroy() {
		// Propagate remove action of elements to other views
		this.broadcastElements(EVAOperation.REMOVE_ELEMENT);

		pickingManager.removeViewSpecificData(uniqueID);

		// generalManager.getViewGLCanvasManager().getConnectedElementRepresentationManager()
		// .clearByView(EIDType.REFSEQ_MRNA_INT, uniqueID);

		generalManager.getViewGLCanvasManager().getConnectedElementRepresentationManager().clearAll();
		generalManager.getViewGLCanvasManager().unregisterGLView(this);
		unregisterEventListeners();
	}

	@Override
	public final synchronized void queueEvent(AEventListener<? extends IListenerOwner> listener, AEvent event) {
		queue.add(new Pair<AEventListener<? extends IListenerOwner>, AEvent>(listener, event));
	}

	// @Override
	// public synchronized Pair<AEventListener<? extends IListenerOwner>, AEvent> getEvent() {
	// return queue.poll();
	// }

	@Override
	public void initFromSerializableRepresentation(ASerializedView serialzedView) {
		// the default implementation does not initialize anything
	}

	@Override
	public void registerEventListeners() {
		magnifyingGlassListener = new ToggleMagnifyingGlassListener();
		magnifyingGlassListener.setHandler(this);
		eventPublisher.addListener(ToggleMagnifyingGlassEvent.class, magnifyingGlassListener);

	}

	@Override
	public void unregisterEventListeners() {
		if (magnifyingGlassListener != null) {
			eventPublisher.removeListener(magnifyingGlassListener);
			magnifyingGlassListener = null;
		}
	}

	public void handleToggleMagnifyingGlassEvent() {
		bShowMagnifyingGlass = !bShowMagnifyingGlass;
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
		System.out.println("Finalizing " + VIEW_TYPE);
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
}
