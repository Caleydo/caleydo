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

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.EVAOperation;
import org.caleydo.core.data.virtualarray.StorageVirtualArray;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.IListenerOwner;
import org.caleydo.core.manager.event.view.ToggleMagnifyingGlassEvent;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.mapping.IDMappingManager;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
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
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutManager;
import org.caleydo.core.view.opengl.layout.LayoutTemplate;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;
import org.caleydo.core.view.opengl.util.GLMagnifyingGlass;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevelElement;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.ContextMenu;
import org.caleydo.core.view.opengl.util.scrollbar.IScrollBarUpdateHandler;
import org.caleydo.core.view.opengl.util.scrollbar.ScrollBar;
import org.caleydo.core.view.opengl.util.scrollbar.ScrollBarRenderer;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.core.view.opengl.util.texture.TextureManager;

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
 * {@link ViewManager#createGLView(Class, GLCaleydoCanvas, ViewFrustum)}. As a consequence, the constructor of
 * the view may have only those two arguments (GLCaleydoCanvas, ViewFrustum). Otherwise, the creation will
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
	implements GLEventListener, IResettableView, IScrollBarUpdateHandler, IMouseWheelHandler {

	public final static String VIEW_TYPE = "unspecified";

	public enum EBusyModeState {
		SWITCH_OFF,
		ON,
		OFF
	}

	/**
	 * The canvas rendering the view. The canvas also holds the {@link PixelGLConverter}
	 */
	protected GLCaleydoCanvas parentGLCanvas;

	protected PickingManager pickingManager;

	/**
	 * Key listener which is created and registered in specific view.
	 */
	protected GLKeyListener<?> glKeyListener;

	protected GLMouseListener glMouseListener;

	protected GLMouseWheelListener glMouseWheelListener;

	protected ViewFrustum viewFrustum;

	protected IViewCamera viewCamera;

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

	private float previousZoomScale = 1.0f;
	private float currentZoomScale = 1.0f;

	private ScrollBar hScrollBar;
	private ScrollBar vScrollBar;
	private LayoutManager hScrollBarLayoutManager;
	private LayoutTemplate hScrollBarTemplate;
	private LayoutManager vScrollBarLayoutManager;
	private LayoutTemplate vScrollBarTemplate;

	private boolean wasMouseWheeled = false;

	private Point mouseWheelPosition;

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
	protected String contentVAType = ISet.CONTENT;

	/**
	 * The id of the virtual array that manages the storage references in the set
	 */
	protected StorageVirtualArray storageVA;
	/**
	 * The type of the storage VA
	 */
	protected String storageVAType = ISet.STORAGE;

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

	protected boolean useZooming = false;

	protected CaleydoTextRenderer textRenderer;

	/**
	 * True if the mouse is currently over this view. If lazyMode is true then the picking does not need to be
	 * rendered
	 */
	protected boolean lazyMode;

	private float relativeViewTranlateX;

	private float relativeViewTranlateY;

	private HashMap<String, HashMap<Integer, Set<IPickingListener>>> singleIDPickingListeners;
	private HashMap<String, Set<IPickingListener>> multiIDPickingListeners;

	// FIXME: Maybe this can be generalized so a view only needs only one DragAndDropController
	private DragAndDropController scrollBarDragAndDropController;

	private int currentScrollBarID = 0;

	private HashSet<IMouseWheelHandler> mouseWheelListeners;

	/**
	 * Constructor. If the glCanvas object is null - then the view is rendered remote.
	 */
	protected AGLView(GLCaleydoCanvas glCanvas, final ViewFrustum viewFrustum,
		final boolean bRegisterToParentCanvasNow) {

		super(GeneralManager.get().getIDCreator().createID(EManagedObjectType.GL_VIEW));

		GeneralManager.get().getViewGLCanvasManager().registerGLView(this);
		parentGLCanvas = glCanvas;

		if (bRegisterToParentCanvasNow && parentGLCanvas != null) {
			glMouseListener = parentGLCanvas.getGLMouseListener();
		}

		glMouseWheelListener = new GLMouseWheelListener(this);
		singleIDPickingListeners = new HashMap<String, HashMap<Integer, Set<IPickingListener>>>();
		multiIDPickingListeners = new HashMap<String, Set<IPickingListener>>();
		scrollBarDragAndDropController = new DragAndDropController(this);

		this.viewFrustum = viewFrustum;

		viewCamera = new ViewCameraBase(uniqueID);

		pickingManager = generalManager.getViewGLCanvasManager().getPickingManager();
		idMappingManager = generalManager.getIDMappingManager();
		textureManager = new TextureManager();
		contextMenu = ContextMenu.get();

		bShowMagnifyingGlass = false;

		glCanvas.initPixelGLConverter(viewFrustum);

		mouseWheelListeners = new HashSet<IMouseWheelHandler>();

		initScrollBars();

	}

	private void initScrollBars() {

		hScrollBarLayoutManager = new LayoutManager(viewFrustum);
		hScrollBarTemplate = new LayoutTemplate();
		hScrollBar = new ScrollBar(0, 10, 5, 5, EPickingType.ZOOM_SCROLLBAR, 0, this);

		Column baseColumn = new Column();

		ElementLayout hScrollBarLayout = new ElementLayout("horizontalScrollBar");
		hScrollBarLayout.setPixelGLConverter(parentGLCanvas.getPixelGLConverter());
		hScrollBarLayout.setPixelSizeY(10);
		hScrollBarLayout.setRatioSizeX(1.0f);
		hScrollBarLayout.setRenderer(new ScrollBarRenderer(hScrollBar, this, true,
			scrollBarDragAndDropController));

		ElementLayout hSpacingLayout = new ElementLayout("horizontalSpacing");
		hSpacingLayout.setRatioSizeX(1.0f);

		baseColumn.append(hScrollBarLayout);
		baseColumn.append(hSpacingLayout);

		hScrollBarTemplate.setBaseElementLayout(baseColumn);

		hScrollBarLayoutManager.setTemplate(hScrollBarTemplate);

		vScrollBarLayoutManager = new LayoutManager(viewFrustum);
		vScrollBarTemplate = new LayoutTemplate();
		vScrollBar = new ScrollBar(0, 10, 5, 5, EPickingType.ZOOM_SCROLLBAR, 1, this);

		Row baseRow = new Row();

		ElementLayout vScrollBarLayout = new ElementLayout("horizontalScrollBar");
		vScrollBarLayout.setPixelGLConverter(parentGLCanvas.getPixelGLConverter());
		vScrollBarLayout.setPixelSizeX(10);
		vScrollBarLayout.setRatioSizeY(1.0f);
		vScrollBarLayout.setRenderer(new ScrollBarRenderer(vScrollBar, this, false,
			scrollBarDragAndDropController));

		ElementLayout vSpacingLayout = new ElementLayout("verticalSpacing");
		vSpacingLayout.setRatioSizeX(1.0f);

		baseRow.append(vSpacingLayout);
		baseRow.append(vScrollBarLayout);

		vScrollBarTemplate.setBaseElementLayout(baseRow);

		vScrollBarLayoutManager.setTemplate(vScrollBarTemplate);
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

		glMouseListener.addGLCanvas(this);

		((GLEventListener) parentGLCanvas).init(drawable);

		initLocal(drawable.getGL().getGL2());

		hScrollBarLayoutManager.updateLayout();
		vScrollBarLayoutManager.updateLayout();
	}

	@Override
	public final void display(GLAutoDrawable drawable) {
		try {
			processEvents();
			if (!isVisible())
				return;

			((GLEventListener) parentGLCanvas).display(drawable);

			final Vec3f rot_Vec3f = new Vec3f();
			final Vec3f position = viewCamera.getCameraPosition();

			GL2 gl = drawable.getGL().getGL2();

			gl.glTranslatef(position.x(), position.y(), position.z());
			gl.glRotatef(viewCamera.getCameraRotationGrad(rot_Vec3f), rot_Vec3f.x(), rot_Vec3f.y(),
				rot_Vec3f.z());

			if (useZooming) {
				beginZoom(gl);
			}

			displayLocal(gl);

			if (useZooming) {
				endZoom(gl);
			}

			// if (bShowMagnifyingGlass) {
			// if (magnifyingGlass == null) {
			// magnifyingGlass = new GLMagnifyingGlass();
			// }
			// magnifyingGlass.draw(gl, glMouseListener);
			// }
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
	 * use this method to initialize their members related to {@link AView#set}.
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

	public final GLCaleydoCanvas getParentGLCanvas() {
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
		hScrollBarLayoutManager.updateLayout();
		vScrollBarLayoutManager.updateLayout();

		// parentGLCanvas.initPixelGLConverter(viewFrustum);
	}

	private void updateDetailMode() {
		PixelGLConverter pixelGLConverter = parentGLCanvas.getPixelGLConverter();
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

					EPickingMode ePickingMode = tempPick.getPickingMode();
					if (pickingType == EPickingType.CONTEXT_MENU_SELECTION.name()
						|| pickingType == EPickingType.CONTEXT_MENU_SCROLL_DOWN.name()
						|| pickingType == EPickingType.CONTEXT_MENU_SCROLL_UP.name()) {
						contextMenu.handlePickingEvents(EPickingType.valueOf(pickingType), ePickingMode,
							externalID);
					}
					else {
						if (tempPick.getPickingMode() != EPickingMode.RIGHT_CLICKED)
							contextMenu.flush();
						handlePicking(pickingType, ePickingMode, externalID, tempPick);
						// FIXME: This is for legacy support -> picking listeners should be used
						try {
							handlePickingEvents(EPickingType.valueOf(pickingType), ePickingMode, externalID,
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

	protected void handlePicking(String pickingType, EPickingMode pickingMode, int pickingID, Pick pick) {

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

	private void handlePicking(IPickingListener pickingListener, EPickingMode pickingMode, Pick pick) {
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
	 * pickingType and ID the listener's methods are called.
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
			pickingListeners.add(pickingListener);
		}
		map.put(externalID, pickingListeners);

	}

	/**
	 * Registers a {@link IPickingListener} for this view. When objects are picked with the specified
	 * pickingType the listener's methods are called.
	 * 
	 * @param pickingListener
	 * @param pickingType
	 */
	public void addMultiIDPickingListener(IPickingListener pickingListener, String pickingType) {
		Set<IPickingListener> pickingListeners = multiIDPickingListeners.get(pickingType);
		if (pickingListeners == null) {
			pickingListeners = new HashSet<IPickingListener>();
			pickingListeners.add(pickingListener);
		}
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
	abstract protected void handlePickingEvents(final EPickingType pickingType,
		final EPickingMode pickingMode, final int pickingID, final Pick pick);

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
		if (contentVAType.equals(ISet.CONTENT))
			return false;
		return true;
	}

	public final void setRemoteRenderingGLView(IGLRemoteRenderingView glRemoteRenderingView) {
		this.glRemoteRenderingView = glRemoteRenderingView;;
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

	public final StorageVirtualArray getStorageVA() {
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

	/**
	 * Specifies whether the view should zoom when the mouse was wheeled. This method should not be called
	 * when the view is rendered remote.
	 * 
	 * @param useZooming
	 */
	public void useZooming(boolean useZooming) {

		if ((this.useZooming && useZooming) || (!this.useZooming && !useZooming))
			return;

		if (useZooming) {
			parentGLCanvas.removeMouseWheelListener(glMouseListener);
			parentGLCanvas.addMouseWheelListener(glMouseWheelListener);
		}
		else {
			parentGLCanvas.removeMouseWheelListener(glMouseWheelListener);
			parentGLCanvas.addMouseWheelListener(glMouseListener);
		}

		this.useZooming = useZooming;
	}

	/**
	 * This method shall be called before the view is rendered in order to be zoomed.
	 * 
	 * @param gl
	 */
	public void beginZoom(GL2 gl) {

		if (currentZoomScale == 1.0f) {
			relativeViewTranlateX = 0;
			relativeViewTranlateY = 0;
			return;
		}

		float viewTranslateX = relativeViewTranlateX * viewFrustum.getWidth();
		float viewTranslateY = relativeViewTranlateY * viewFrustum.getHeight();

		PixelGLConverter pixelGLConverter = parentGLCanvas.getPixelGLConverter();

		// float zoomCenterX = relativeZoomCenterX * viewFrustum.getWidth();
		// float zoomCenterY = relativeZoomCenterY * viewFrustum.getHeight();
		//
		// float viewTranslateX;
		// float viewTranslateY;

		if (wasMouseWheeled) {

			float viewPositionX = pixelGLConverter.getGLWidthForCurrentGLTransform(gl);
			float viewPositionY = pixelGLConverter.getGLHeightForCurrentGLTransform(gl);
			float wheelPositionX = pixelGLConverter.getGLWidthForPixelWidth(mouseWheelPosition.x);
			float wheelPositionY =
				pixelGLConverter.getGLHeightForPixelHeight(parentGLCanvas.getHeight() - mouseWheelPosition.y);

			// viewTranslateX =
			// (viewFrustum.getWidth() / 2.0f) - zoomCenterX - (previousZoomScale - 1) * zoomCenterX;
			// viewTranslateY =
			// (viewFrustum.getHeight() / 2.0f) - zoomCenterY - (previousZoomScale - 1) * zoomCenterY;

			float zoomCenterMouseX = wheelPositionX - viewPositionX;
			float zoomCenterMouseY = wheelPositionY - viewPositionY;

			float relativeImageCenterX =
				(-viewTranslateX + zoomCenterMouseX) / (viewFrustum.getWidth() * previousZoomScale);
			float relativeImageCenterY =
				(-viewTranslateY + zoomCenterMouseY) / (viewFrustum.getHeight() * previousZoomScale);

			float zoomCenterX = relativeImageCenterX * viewFrustum.getWidth();
			float zoomCenterY = relativeImageCenterY * viewFrustum.getHeight();

			// zoomCenterX = viewPositionX + viewFrustum.getWidth() - wheelPositionX;
			// zoomCenterY = viewPositionY + viewFrustum.getHeight() - wheelPositionY;
			viewTranslateX =
				(viewFrustum.getWidth() / 2.0f) - zoomCenterX - (currentZoomScale - 1) * zoomCenterX;
			viewTranslateY =
				(viewFrustum.getHeight() / 2.0f) - zoomCenterY - (currentZoomScale - 1) * zoomCenterY;

			if (viewTranslateX > 0)
				viewTranslateX = 0;
			if (viewTranslateY > 0)
				viewTranslateY = 0;

			if (viewTranslateX < -(viewFrustum.getWidth() * (currentZoomScale - 1)))
				viewTranslateX = -(viewFrustum.getWidth() * (currentZoomScale - 1));
			if (viewTranslateY < -(viewFrustum.getHeight() * (currentZoomScale - 1)))
				viewTranslateY = -(viewFrustum.getHeight() * (currentZoomScale - 1));

			relativeViewTranlateX = viewTranslateX / viewFrustum.getWidth();
			relativeViewTranlateY = viewTranslateY / viewFrustum.getHeight();

			// System.out.println("=========================================");
			// System.out.println("viewPos: " + viewPositionX + "," + viewPositionY + "\n zoomCenter: "
			// + zoomCenterX + "," + zoomCenterY + "\n Frustum: " + viewFrustum.getWidth() + ","
			// + viewFrustum.getHeight() + "\n Translate: " + viewTranlateX + "," + viewTranlateY
			// + "\n currentZoom: " + currentZoomScale + "; prevZoom: " + previousZoomScale);
		}

		float relativeImageCenterX =
			(-viewTranslateX + viewFrustum.getWidth() / 2.0f) / (viewFrustum.getWidth() * currentZoomScale);
		float relativeImageCenterY =
			(-viewTranslateY + viewFrustum.getHeight() / 2.0f) / (viewFrustum.getHeight() * currentZoomScale);

		float zoomCenterX = relativeImageCenterX * viewFrustum.getWidth();
		float zoomCenterY = relativeImageCenterY * viewFrustum.getHeight();

		hScrollBar.setPageSize(pixelGLConverter.getPixelWidthForGLWidth((viewFrustum.getWidth() - viewFrustum
			.getWidth() / currentZoomScale)
			/ currentZoomScale));
		hScrollBar.setMaxValue(pixelGLConverter.getPixelWidthForGLWidth(viewFrustum.getWidth()
			- viewFrustum.getWidth() / (currentZoomScale * 2.0f)));
		hScrollBar.setMinValue(pixelGLConverter.getPixelWidthForGLWidth(viewFrustum.getWidth()
			/ (currentZoomScale * 2.0f)));
		hScrollBar.setSelection(pixelGLConverter.getPixelWidthForGLWidth(zoomCenterX));

		vScrollBar
			.setPageSize(pixelGLConverter.getPixelWidthForGLWidth((viewFrustum.getHeight() - viewFrustum
				.getHeight() / currentZoomScale)
				/ currentZoomScale));
		vScrollBar.setMaxValue(pixelGLConverter.getPixelWidthForGLWidth(viewFrustum.getHeight()
			- viewFrustum.getHeight() / (currentZoomScale * 2.0f)));
		vScrollBar.setMinValue(pixelGLConverter.getPixelWidthForGLWidth(viewFrustum.getHeight()
			/ (currentZoomScale * 2.0f)));
		vScrollBar.setSelection(pixelGLConverter.getPixelWidthForGLWidth(zoomCenterY));

		// viewTranslateX = (viewFrustum.getWidth() / 2.0f) - zoomCenterX - (currentZoomScale - 1) *
		// zoomCenterX;
		// viewTranslateY =
		// (viewFrustum.getHeight() / 2.0f) - zoomCenterY - (currentZoomScale - 1) * zoomCenterY;
		//
		// relativeZoomCenterX = zoomCenterX / viewFrustum.getWidth();
		// relativeZoomCenterY = zoomCenterY / viewFrustum.getHeight();

		gl.glPushMatrix();
		gl.glTranslatef(viewTranslateX, viewTranslateY, 0);
		gl.glScalef(currentZoomScale, currentZoomScale, 1);

		// JUST FOR TESTING OF 1D ZOOM IN Z-DIRECTION
		// TODO: ADD MODE FOR X-ZOOM, Y-ZOOM OR BOTH
		// gl.glTranslatef(0, viewTranslateY, 0);
		// gl.glScalef(1, currentZoomScale, 1);

	}

	/**
	 * This method shall be called after the view has been rendered, if beginZoom(GL) has been called
	 * beforehand.
	 * 
	 * @param gl
	 */
	public void endZoom(GL2 gl) {

		previousZoomScale = currentZoomScale;

		if (currentZoomScale == 1.0f)
			return;
		gl.glPopMatrix();
		wasMouseWheeled = false;

		hScrollBarLayoutManager.render(gl);
		vScrollBarLayoutManager.render(gl);

		scrollBarDragAndDropController.handleDragging(gl, glMouseListener);
	}

	@Override
	public void handleScrollBarUpdate(ScrollBar scrollBar) {
		if (scrollBar == hScrollBar) {
			float zoomCenterX =
				parentGLCanvas.getPixelGLConverter().getGLWidthForPixelWidth(scrollBar.getSelection());
			float viewTranslateX =
				(viewFrustum.getWidth() / 2.0f) - zoomCenterX - (currentZoomScale - 1) * zoomCenterX;

			if (viewTranslateX > 0)
				viewTranslateX = 0;
			if (viewTranslateX < -(viewFrustum.getWidth() * (currentZoomScale - 1)))
				viewTranslateX = -(viewFrustum.getWidth() * (currentZoomScale - 1));

			relativeViewTranlateX = viewTranslateX / viewFrustum.getWidth();
		}
		if (scrollBar == vScrollBar) {
			float zoomCenterY =
				parentGLCanvas.getPixelGLConverter().getGLHeightForPixelHeight(scrollBar.getSelection());
			float viewTranslateY =
				(viewFrustum.getHeight() / 2.0f) - zoomCenterY - (currentZoomScale - 1) * zoomCenterY;

			if (viewTranslateY > 0)
				viewTranslateY = 0;
			if (viewTranslateY < -(viewFrustum.getHeight() * (currentZoomScale - 1)))
				viewTranslateY = -(viewFrustum.getHeight() * (currentZoomScale - 1));

			relativeViewTranlateY = viewTranslateY / viewFrustum.getHeight();
		}
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
}
