package org.caleydo.core.view.opengl.canvas;

import gleem.linalg.Vec3f;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import org.caleydo.core.data.selection.ContentVAType;
import org.caleydo.core.data.selection.ContentVirtualArray;
import org.caleydo.core.data.selection.EVAOperation;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.StorageVAType;
import org.caleydo.core.data.selection.StorageVirtualArray;
import org.caleydo.core.manager.IIDMappingManager;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.IListenerOwner;
import org.caleydo.core.manager.event.IPollingListenerOwner;
import org.caleydo.core.manager.event.view.ToggleMagnifyingGlassEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.exception.ExceptionHandler;
import org.caleydo.core.view.AView;
import org.caleydo.core.view.opengl.camera.IViewCamera;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.camera.ViewCameraBase;
import org.caleydo.core.view.opengl.canvas.listener.IResettableView;
import org.caleydo.core.view.opengl.canvas.listener.ToggleMagnifyingGlassListener;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.keyboard.GLKeyListener;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;
import org.caleydo.core.view.opengl.util.GLMagnifyingGlass;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevelElement;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.ContextMenu;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.core.view.opengl.util.texture.TextureManager;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;

/**
 * Abstract class for OpenGL views.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 * @author Michael Kalkusch
 */
public abstract class AGLView
	extends AView
	implements GLEventListener, IPollingListenerOwner, IResettableView {

	public enum EBusyModeState {
		SWITCH_OFF,
		ON,
		OFF
	}

	// TODO: should be a list of parent canvas object to be generic
	protected GLCaleydoCanvas parentGLCanvas;

	protected PickingManager pickingManager;

	/**
	 * Key listener which is created and registered in specific view.
	 */
	protected GLKeyListener<?> glKeyListener;

	protected GLMouseListener glMouseListener;

	protected IViewFrustum viewFrustum;

	protected IViewCamera viewCamera;

	/**
	 * The views current aspect ratio. Value gets updated when reshape is called by the JOGL animator.
	 */
	protected float fAspectRatio = 1f;

	protected EDetailLevel detailLevel = EDetailLevel.HIGH;

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

	protected IIDMappingManager idMappingManager;

	/**
	 * The virtual array that manages the contents (the indices) in the storages
	 */
	protected ContentVirtualArray contentVA;
	/**
	 * The type of the content VA
	 */
	protected ContentVAType contentVAType = ContentVAType.CONTENT;

	/**
	 * The id of the virtual array that manages the storage references in the set
	 */
	protected StorageVirtualArray storageVA;
	/**
	 * The type of the storage VA
	 */
	protected StorageVAType storageVAType = StorageVAType.STORAGE;

	/**
	 * The context menu each view should implement. It has to be created in initLocal or is set via initRemote
	 */
	protected ContextMenu contextMenu;

	/**
	 * The queue which holds the events
	 */
	private BlockingQueue<Pair<AEventListener<? extends IListenerOwner>, AEvent>> queue;

	// /** id of the related view in the gui (e.g. RCP) */
	// private String viewGUIID;

	private boolean isVisible = true;

	/**
	 * Constructor.
	 */
	protected AGLView(GLCaleydoCanvas glCanvas, final String sLabel, final IViewFrustum viewFrustum,
		final boolean bRegisterToParentCanvasNow) {

		// If the glCanvas object is null - then the view is rendered remote.
		super(glCanvas != null ? glCanvas.getID() : -1, sLabel, GeneralManager.get().getIDManager().createID(
			EManagedObjectType.GL_VIEW));

		parentGLCanvas = glCanvas;

		if (bRegisterToParentCanvasNow && parentGLCanvas != null) {
			glMouseListener = parentGLCanvas.getGLMouseListener();
		}

		this.viewFrustum = viewFrustum;

		viewCamera = new ViewCameraBase(iUniqueID);

		pickingManager = generalManager.getViewGLCanvasManager().getPickingManager();
		idMappingManager = generalManager.getIDMappingManager();
		textureManager = new TextureManager();
		contextMenu = ContextMenu.get();

		queue = new LinkedBlockingQueue<Pair<AEventListener<? extends IListenerOwner>, AEvent>>();

		bShowMagnifyingGlass = false;
	}

	@Override
	public void init(GLAutoDrawable drawable) {

		glMouseListener.addGLCanvas(this);

		((GLEventListener) parentGLCanvas).init(drawable);

		initLocal(drawable.getGL());
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

			GL gl = drawable.getGL();

			gl.glTranslatef(position.x(), position.y(), position.z());
			gl.glRotatef(viewCamera.getCameraRotationGrad(rot_Vec3f), rot_Vec3f.x(), rot_Vec3f.y(), rot_Vec3f.z());

			displayLocal(gl);

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

	@Override
	public final void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {

		((GLEventListener) parentGLCanvas).displayChanged(drawable, modeChanged, deviceChanged);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

		if (glRemoteRenderingView != null || this.getViewType().equals("org.caleydo.view.bucket")
			|| this.getViewType().equals("org.caleydo.view.glyph")
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

			bIsDisplayListDirtyLocal = true;
			bIsDisplayListDirtyRemote = true;
			bHasFrustumChanged = true;
		}

		GL gl = drawable.getGL();

		fAspectRatio = (float) height / (float) width;

		gl.glViewport(x, y, width, height);
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();

		viewFrustum.setProjectionMatrix(gl, fAspectRatio);
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
	protected void clipToFrustum(GL gl) {
		// if (this instanceof GLHeatMap && ((GLHeatMap) this).isInListMode())
		// return;
		//
		// gl.glClear(GL.GL_STENCIL_BUFFER_BIT);
		// gl.glColorMask(false, false, false, false);
		// gl.glClearStencil(0); // Clear The Stencil Buffer To 0
		// gl.glEnable(GL.GL_DEPTH_TEST); // Enables Depth Testing
		// gl.glDepthFunc(GL.GL_LEQUAL); // The Type Of Depth Testing To Do
		// gl.glEnable(GL.GL_STENCIL_TEST);
		// gl.glStencilFunc(GL.GL_ALWAYS, 1, 1);
		// gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_REPLACE);
		// gl.glDisable(GL.GL_DEPTH_TEST);
		//
		// // Clip region that renders in stencil buffer (in this case the
		// // frustum)
		// gl.glBegin(GL.GL_POLYGON);
		// gl.glVertex3f(viewFrustum.getLeft(), viewFrustum.getBottom(), -0.01f);
		// gl.glVertex3f(viewFrustum.getRight(), viewFrustum.getBottom(), -0.01f);
		// gl.glVertex3f(viewFrustum.getRight(), viewFrustum.getTop(), -0.01f);
		// gl.glVertex3f(viewFrustum.getLeft(), viewFrustum.getTop(), -0.01f);
		// gl.glEnd();
		//
		// gl.glEnable(GL.GL_DEPTH_TEST);
		// gl.glColorMask(true, true, true, true);
		// gl.glStencilFunc(GL.GL_EQUAL, 1, 1);
		// gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_KEEP);
	}

	/**
	 * Initialization for gl, general stuff
	 * 
	 * @param gl
	 */
	public abstract void init(final GL gl);

	/**
	 * Initialization for gl called by the local instance Has to call init internally!
	 * 
	 * @param gl
	 */
	protected abstract void initLocal(final GL gl);

	/**
	 * Initialization for gl called by a managing view has to call init internally!
	 * 
	 * @param gl
	 * @param infoAreaManager
	 *            TODO
	 */
	public abstract void initRemote(final GL gl, final AGLView glParentView,
		final GLMouseListener glMouseListener, GLInfoAreaManager infoAreaManager);

	/**
	 * GL display method that has to be called in all cases manually, either by {@link #displayLocal(GL)} or
	 * {@link #displayRemote(GL)}. It must be responsible for rendering the scene. It is also called by the
	 * picking manager.
	 * 
	 * @param gl
	 */
	public abstract void display(final GL gl);

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
	protected abstract void displayLocal(final GL gl);

	/**
	 * Intended for external use when another instance of a view manages the scene. This is specially designed
	 * for composite views. Has to call display internally! The steps necessary in {@link #displayLocal(GL)},
	 * such as handling of events and picking have to be taken care of the instance calling this method.
	 * 
	 * @param gl
	 */
	public abstract void displayRemote(final GL gl);

	public final GLCaleydoCanvas getParentGLCanvas() {
		if (this.isRenderedRemote())
			return getRemoteRenderingGLCanvas().getParentGLCanvas();

		return parentGLCanvas;
	}

	public final IViewFrustum getViewFrustum() {
		return viewFrustum;
	}

	public final void setFrustum(IViewFrustum viewFrustum) {
		this.viewFrustum = viewFrustum;
	}

	/**
	 * This class uses the pickingManager to check if any events have occurred it calls the abstract
	 * handleEvents method where the events should be handled
	 * 
	 * @param gl
	 */
	protected final void checkForHits(final GL gl) {

		Set<EPickingType> hitTypes = pickingManager.getHitTypes(iUniqueID);
		if (hitTypes == null)
			return;
		for (EPickingType pickingType : hitTypes) {

			ArrayList<Pick> alHits = null;

			alHits = pickingManager.getHits(iUniqueID, pickingType);
			if (alHits != null && alHits.size() != 0) {

				for (int iCount = 0; iCount < alHits.size(); iCount++) {
					Pick tempPick = alHits.get(iCount);
					int iExternalID = tempPick.getID();
					if (iExternalID == -1) {
						continue;
					}

					EPickingMode ePickingMode = tempPick.getPickingMode();
					if (pickingType == EPickingType.CONTEXT_MENU_SELECTION
						|| pickingType == EPickingType.CONTEXT_MENU_SCROLL_DOWN
						|| pickingType == EPickingType.CONTEXT_MENU_SCROLL_UP) {
						contextMenu.handlePickingEvents(pickingType, ePickingMode, iExternalID);
					}
					else {
						if (tempPick.getPickingMode() != EPickingMode.RIGHT_CLICKED)
							contextMenu.flush();
						handlePickingEvents(pickingType, ePickingMode, iExternalID, tempPick);
					}
					pickingManager.flushHits(iUniqueID, pickingType);
				}
			}
		}
	}

	/**
	 * This method is called every time a method occurs. It should take care of reacting appropriately to the
	 * events.
	 * 
	 * @param ePickingType
	 *            the Picking type, held in EPickingType
	 * @param ePickingMode
	 *            the Picking mode (clicked, dragged etc.)
	 * @param iExternalID
	 *            the name specified for an element with glPushName
	 * @param pick
	 *            the pick object which can be useful to retrieve for example the mouse position when the pick
	 *            occurred
	 */
	abstract protected void handlePickingEvents(final EPickingType ePickingType,
		final EPickingMode ePickingMode, final int iExternalID, final Pick pick);

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
	public void setDetailLevel(EDetailLevel detailLevel) {
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
		if (contentVAType == ContentVAType.CONTENT)
			return false;
		return true;
	}

	public final void setRemoteRenderingGLView(IGLRemoteRenderingView glRemoteRenderingView) {
		this.glRemoteRenderingView = glRemoteRenderingView;;
	}

	public final IGLRemoteRenderingView getRemoteRenderingGLCanvas() {
		return glRemoteRenderingView;
	}

	protected void renderBusyMode(final GL gl) {
		float fTransparency = 0.3f * iFrameCounter / NUMBER_OF_FRAMES;
		float fLoadingTransparency = 0.8f * iFrameCounter / NUMBER_OF_FRAMES;

		if (eBusyModeState == EBusyModeState.ON && iFrameCounter < NUMBER_OF_FRAMES) {
			iFrameCounter++;
		}
		else if (eBusyModeState == EBusyModeState.SWITCH_OFF) {
			iFrameCounter--;
		}

		gl.glColor4f(1, 1, 1, fTransparency);
		gl.glBegin(GL.GL_POLYGON);
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

		gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
		gl.glColor4f(1.0f, 1.0f, 1.0f, fLoadingTransparency);

		gl.glBegin(GL.GL_POLYGON);

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

		gl.glBegin(GL.GL_POLYGON);
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

	public final EDetailLevel getDetailLevel() {
		return detailLevel;
	}

	public void destroy() {
		// Propagate remove action of elements to other views
		this.broadcastElements(EVAOperation.REMOVE_ELEMENT);

		pickingManager.removeViewSpecificData(iUniqueID);

		// generalManager.getViewGLCanvasManager().getConnectedElementRepresentationManager()
		// .clearByView(EIDType.REFSEQ_MRNA_INT, iUniqueID);

		generalManager.getViewGLCanvasManager().getConnectedElementRepresentationManager().clearAll();
		generalManager.getViewGLCanvasManager().unregisterGLView(this);
		unregisterEventListeners();
	}



	@Override
	public synchronized void queueEvent(AEventListener<? extends IListenerOwner> listener, AEvent event) {
		queue.add(new Pair<AEventListener<? extends IListenerOwner>, AEvent>(listener, event));
	}

	@Override
	public synchronized Pair<AEventListener<? extends IListenerOwner>, AEvent> getEvent() {
		return queue.poll();
	}

	@Override
	public void initFromSerializableRepresentation(ASerializedView ser) {
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
	 *            GL Object that shall be used for rendering.
	 */
	protected void renderSymbol(GL gl, EIconTextures texture, float buttonSize) {

		float fXButtonOrigin = viewFrustum.getLeft() + viewFrustum.getWidth() / 2 - buttonSize / 2;
		float fYButtonOrigin = viewFrustum.getBottom() + viewFrustum.getHeight() / 2 - buttonSize / 2;
		Texture tempTexture = textureManager.getIconTexture(gl, texture);
		tempTexture.enable();
		tempTexture.bind();

		TextureCoords texCoords = tempTexture.getImageTexCoords();

		gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
		gl.glColor4f(1f, 1, 1, 1f);
		gl.glBegin(GL.GL_POLYGON);

		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(fXButtonOrigin, fYButtonOrigin, 0.01f);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(fXButtonOrigin, fYButtonOrigin + buttonSize, 0.01f);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(fXButtonOrigin + buttonSize, fYButtonOrigin + buttonSize, 0.01f);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(fXButtonOrigin + buttonSize, fYButtonOrigin, 0.01f);
		gl.glEnd();
		gl.glPopAttrib();
		tempTexture.disable();

	}
}
