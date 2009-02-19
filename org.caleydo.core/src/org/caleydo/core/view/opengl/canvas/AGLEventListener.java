package org.caleydo.core.view.opengl.canvas;

import gleem.linalg.Vec3f;
import java.awt.Rectangle;
import java.util.ArrayList;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import org.caleydo.core.data.collection.ESetType;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.EVAOperation;
import org.caleydo.core.manager.IIDMappingManager;
import org.caleydo.core.manager.event.IMediatorReceiver;
import org.caleydo.core.manager.event.IMediatorSender;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.util.exception.ExceptionHandler;
import org.caleydo.core.view.AView;
import org.caleydo.core.view.opengl.camera.IViewCamera;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.camera.ViewCameraBase;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.GLGlyph;
import org.caleydo.core.view.opengl.canvas.remote.GLRemoteRendering;
import org.caleydo.core.view.opengl.canvas.remote.IGLCanvasRemoteRendering;
import org.caleydo.core.view.opengl.mouse.PickingJoglMouseListener;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevelElement;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.core.view.opengl.util.texture.GLIconTextureManager;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;

/**
 * Abstract class for OpenGL views.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * @author Alexander Lex
 */
public abstract class AGLEventListener
	extends AView
	implements GLEventListener
{
	public enum EBusyModeState
	{
		SWITCH_OFF,
		ON,
		OFF
	}

	protected EManagedObjectType viewType = EManagedObjectType.GL_EVENT_LISTENER;

	// TODO: should be a list of parent canvas object to be generic
	protected GLCaleydoCanvas parentGLCanvas;

	protected PickingManager pickingManager;

	protected PickingJoglMouseListener pickingTriggerMouseAdapter;

	protected IViewFrustum viewFrustum;

	protected IViewCamera viewCamera;

	protected IGLCanvasRemoteRendering remoteRenderingGLCanvas;

	/**
	 * The views current aspect ratio. Value gets updated when reshape is called
	 * by the JOGL animator.
	 */
	protected float fAspectRatio = 1f;

	protected EDetailLevel detailLevel = EDetailLevel.HIGH;

	/**
	 * The remote level element in which the view is placed. This variable is
	 * only set when the view is rendered remote.
	 */
	protected RemoteLevelElement remoteLevelElement;

	protected boolean bIsDisplayListDirtyLocal = true;
	protected boolean bIsDisplayListDirtyRemote = true;

	protected int iGLDisplayListIndexLocal;
	protected int iGLDisplayListIndexRemote;

	protected int iGLDisplayListToCall = 0;

	protected boolean bHasFrustumChanged = false;

	protected GeneralRenderStyle renderStyle;

	protected GLIconTextureManager iconTextureManager;

	private int iFrameCounter = 0;
	private int iRotationFrameCounter = 0;
	private static final int NUMBER_OF_FRAMES = 15;

	protected EBusyModeState eBusyModeState = EBusyModeState.OFF;
	
	protected IIDMappingManager idMappingManager;

	/**
	 * The id of the virtual array that manages the contents (the indices) in
	 * the storages
	 */
	protected int iContentVAID = -1;

	/**
	 * The id of the virtual array that manages the storage references in the
	 * set
	 */
	protected int iStorageVAID = -1;
	
	/**
	 * Constructor.
	 */
	protected AGLEventListener(final int iGLCanvasID, final String sLabel,
			final IViewFrustum viewFrustum, final boolean bRegisterToParentCanvasNow)
	{
		super(iGLCanvasID, sLabel, GeneralManager.get().getIDManager().createID(
				EManagedObjectType.GL_EVENT_LISTENER));

		parentGLCanvas = (generalManager.getViewGLCanvasManager().getCanvas(iGLCanvasID));

		if (bRegisterToParentCanvasNow && parentGLCanvas != null)
		{
			// Register GL event listener view to GL canvas
//			parentGLCanvas.addGLEventListener(this);

			// generalManager.getViewGLCanvasManager().registerGLEventListenerByGLCanvasID(
			// parentGLCanvas.getID(), this);

			pickingTriggerMouseAdapter = parentGLCanvas.getJoglMouseListener();
		}
		// // Frustum will only be remotely rendered by another view
		// else
		// {
		// generalManager.getViewGLCanvasManager().registerGLEventListenerByGLCanvasID(-1,
		// this);
		// }

		this.viewFrustum = viewFrustum;

		viewCamera = new ViewCameraBase(iUniqueID); // FIXME: generate own ID
		// for camera

		pickingManager = generalManager.getViewGLCanvasManager().getPickingManager();
		idMappingManager = generalManager.getIDMappingManager();
		iconTextureManager = new GLIconTextureManager();
	}

	@Override
	public void init(GLAutoDrawable drawable)
	{

		// generalManager.getViewGLCanvasManager().getInfoAreaManager().
		// initInfoOverlay(
		// iUniqueID, drawable);
		//
		// generalManager.getViewGLCanvasManager().getInfoAreaManager().
		// initInfoInPlace(
		// viewFrustum);
		//
		// generalManager.getViewGLCanvasManager().getInfoAreaManager().enable(
		// false);

		pickingTriggerMouseAdapter.addGLCanvas(this);

		((GLEventListener) parentGLCanvas).init(drawable);

		initLocal(drawable.getGL());
	}

	@Override
	public synchronized void display(GLAutoDrawable drawable)
	{
		try
		{
			((GLEventListener) parentGLCanvas).display(drawable);

			/** Read viewing parameters... */
			final Vec3f rot_Vec3f = new Vec3f();
			final Vec3f position = viewCamera.getCameraPosition();
			final float w = viewCamera.getCameraRotationGrad(rot_Vec3f);

			GL gl = drawable.getGL();

			/** Translation */
			gl.glTranslatef(position.x(), position.y(), position.z());

			/** Rotation */
			gl.glRotatef(w, rot_Vec3f.x(), rot_Vec3f.y(), rot_Vec3f.z());

			displayLocal(gl);

		}
		catch (RuntimeException exception)
		{
			ExceptionHandler.get().handleException(exception);
		}
	}

	@Override
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
			boolean deviceChanged)
	{

		((GLEventListener) parentGLCanvas)
				.displayChanged(drawable, modeChanged, deviceChanged);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
	{
		if (remoteRenderingGLCanvas != null || this instanceof GLRemoteRendering
				|| this instanceof GLGlyph)
			viewFrustum.considerAspectRatio(true);
		else
		{
			// normalize between 0 and 8
			Rectangle frame = parentGLCanvas.getBounds();
			viewFrustum.setLeft(0);
			viewFrustum.setRight(8);// frame.width / 100);
			viewFrustum.setBottom(0);
			float value = (float) frame.height / (float) frame.width * 8.0f;
			viewFrustum.setTop(value);

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
	 * Set the display list to dirty
	 */
	public void setDisplayListDirty()
	{
		bIsDisplayListDirtyLocal = true;
		bIsDisplayListDirtyRemote = true;
	}

	/**
	 * This method clips everything outside the frustum
	 */
	protected void clipToFrustum(GL gl)
	{
//		gl.glClear(GL.GL_STENCIL_BUFFER_BIT);
//		gl.glColorMask(false, false, false, false);
//		gl.glClearStencil(0); // Clear The Stencil Buffer To 0
//		gl.glEnable(GL.GL_DEPTH_TEST); // Enables Depth Testing
//		gl.glDepthFunc(GL.GL_LEQUAL); // The Type Of Depth Testing To Do
//		gl.glEnable(GL.GL_STENCIL_TEST);
//		gl.glStencilFunc(GL.GL_ALWAYS, 1, 1);
//		gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_REPLACE);
//		gl.glDisable(GL.GL_DEPTH_TEST);
//
//		// Clip region that renders in stencil buffer (in this case the
//		// frustum)
//		gl.glBegin(GL.GL_POLYGON);
//		gl.glVertex3f(viewFrustum.getLeft(), viewFrustum.getBottom(), -0.01f);
//		gl.glVertex3f(viewFrustum.getRight(), viewFrustum.getBottom(), -0.01f);
//		gl.glVertex3f(viewFrustum.getRight(), viewFrustum.getTop(), -0.01f);
//		gl.glVertex3f(viewFrustum.getLeft(), viewFrustum.getTop(), -0.01f);
//		gl.glEnd();
//
//		gl.glEnable(GL.GL_DEPTH_TEST);
//		gl.glColorMask(true, true, true, true);
//		gl.glStencilFunc(GL.GL_EQUAL, 1, 1);
//		gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_KEEP);
	}

	/**
	 * Initialization for gl, general stuff
	 * 
	 * @param gl
	 */
	public abstract void init(final GL gl);

	/**
	 * Initialization for gl called by the local instance Has to call init
	 * internally!
	 * 
	 * @param gl
	 */
	protected abstract void initLocal(final GL gl);

	/**
	 * Initialization for gl called by a managing view Has to call init
	 * internally!
	 * 
	 * @param gl
	 */
	public abstract void initRemote(final GL gl, final int iRemoteViewID,
			final PickingJoglMouseListener pickingTriggerMouseAdapter,
			final IGLCanvasRemoteRendering remoteRenderingGLCanvas);

	/**
	 * GL display method that has to be called in all cases
	 * 
	 * @param gl
	 */
	public abstract void display(final GL gl);

	/**
	 * Intended for internal use when no other view is managing the scene. Has
	 * to call display internally!
	 * 
	 * @param gl
	 */
	protected abstract void displayLocal(final GL gl);

	/**
	 * Intended for external use when another instance of a view manages the
	 * scene. This is specially designed for composite views. Has to call
	 * display internally!
	 * 
	 * @param gl
	 */
	public abstract void displayRemote(final GL gl);

	public final GLCaleydoCanvas getParentGLCanvas()
	{
		return parentGLCanvas;
	}

	public final IViewFrustum getViewFrustum()
	{
		return viewFrustum;
	}

	public void setFrustum(IViewFrustum viewFrustum)
	{
		this.viewFrustum = viewFrustum;
	}

	/**
	 * This class uses the pickingManager to check if any events have occurred it
	 * calls the abstract handleEvents method where the events should be handled
	 * 
	 * @param gl
	 */
	protected void checkForHits(final GL gl)
	{

		for (EPickingType ePickingType : EPickingType.values())
		{
			if (ePickingType.getViewType() != viewType)
			{
				if (viewType == EManagedObjectType.GL_EVENT_LISTENER)
					throw new IllegalStateException(
							"Views must define their view type in the constructor");
				continue;
			}

			ArrayList<Pick> alHits = null;

			alHits = pickingManager.getHits(iUniqueID, ePickingType);
			if (alHits != null && alHits.size() != 0)
			{

				for (int iCount = 0; iCount < alHits.size(); iCount++)
				{
					Pick tempPick = alHits.get(iCount);
					int iPickingID = tempPick.getPickingID();
					int iExternalID = pickingManager.getExternalIDFromPickingID(iUniqueID,
							iPickingID);
					
					if (iExternalID == -1)
						continue;

					EPickingMode ePickingMode = tempPick.getPickingMode();
					handleEvents(ePickingType, ePickingMode, iExternalID, tempPick);
				}
			}
		}
	}

	/**
	 * This method is called every time a method occurs. It should take care of
	 * reacting appropriately to the events.
	 * 
	 * @param ePickingType the Picking type, held in EPickingType
	 * @param ePickingMode the Picking mode (clicked, dragged etc.)
	 * @param iExternalID the name specified for an element with glPushName
	 * @param pick the pick object which can be useful to retrieve for example
	 *            the mouse position when the pick occurred
	 */
	abstract protected void handleEvents(final EPickingType ePickingType,
			final EPickingMode ePickingMode, final int iExternalID, final Pick pick);

	public abstract String getShortInfo();

	public abstract String getDetailedInfo();

	public final IViewCamera getViewCamera()
	{
		return viewCamera;
	}

	/**
	 * Broadcast elements only with a given type.
	 */
	public abstract void broadcastElements(EVAOperation type);

	public synchronized void setDetailLevel(EDetailLevel detailLevel)
	{
		this.detailLevel = detailLevel;
		setDisplayListDirty();
	}

	public synchronized void setRemoteLevelElement(RemoteLevelElement element)
	{
		this.remoteLevelElement = element;
	}

	public RemoteLevelElement getRemoteLevelElement()
	{
		return remoteLevelElement;
	}

	public boolean isRenderedRemote()
	{
		if (remoteRenderingGLCanvas == null)
			return false;

		return true;
	}

	public IGLCanvasRemoteRendering getRemoteRenderingGLCanvas()
	{
		return remoteRenderingGLCanvas;
	}

	protected synchronized void renderBusyMode(final GL gl)
	{
		float fTransparency = 0.3f * iFrameCounter / NUMBER_OF_FRAMES;
		float fLoadingTransparency = 0.8f * iFrameCounter / NUMBER_OF_FRAMES;

		if (eBusyModeState == EBusyModeState.ON && iFrameCounter < NUMBER_OF_FRAMES)
		{
			iFrameCounter++;
		}
		else if (eBusyModeState == EBusyModeState.SWITCH_OFF)
		{
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
		if (renderStyle == null || this instanceof GLRemoteRendering)
		{
			fXCenter = 0;
			fYCenter = 0;
		}
		else
		{
			fXCenter = renderStyle.getXCenter();
			fYCenter = renderStyle.getYCenter();
		}

		// TODO bad hack here, frustum wrong or renderStyle null

		Texture tempTexture = iconTextureManager.getIconTexture(gl, EIconTextures.LOADING);
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
		Texture circleTexture = iconTextureManager.getIconTexture(gl,
				EIconTextures.LOADING_CIRCLE);
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

		if (eBusyModeState == EBusyModeState.SWITCH_OFF && iFrameCounter <= 0)
		{
			pickingManager.enablePicking(true);
			eBusyModeState = EBusyModeState.OFF;
		}

		// System.out.println("Busy mode status: " +eBusyModeState);
	}

	public void enableBusyMode(final boolean bBusyMode)
	{
		if (!bBusyMode && eBusyModeState == EBusyModeState.ON)
		{
			eBusyModeState = EBusyModeState.SWITCH_OFF;
			pickingManager.enablePicking(true);
		}
		else if (bBusyMode)
		{
			pickingManager.enablePicking(false);
			eBusyModeState = EBusyModeState.ON;
		}

		// System.out.println("Busy mode change: " +eBusyModeState.toString());
	}

	/**
	 * Method return true if an element is currently selected for a given
	 * selection type.
	 * 
	 */
	public abstract int getNumberOfSelections(ESelectionType eSelectionType);
	
	public float getAspectRatio()
	{
		return fAspectRatio;
	}
	
	public int getContentVAID()
	{
		return iContentVAID;
	}
	
	public int getStorageVAID()
	{
		return iStorageVAID;
	}
	
	public EDetailLevel getDetailLevel()
	{
		return detailLevel;
	}
	
	public void destroy()
	{
		// Propagate remove action of elements to other views
		this.broadcastElements(EVAOperation.REMOVE_ELEMENT);
		
		if (this instanceof IMediatorSender)
		{
			generalManager.getEventPublisher().removeSenderFromAllGroups(
					(IMediatorSender) this);
		}

		if (this instanceof IMediatorReceiver)
		{
			generalManager.getEventPublisher().removeReceiverFromAllGroups(
					(IMediatorReceiver) this);
		}

//		generalManager.getViewGLCanvasManager().getConnectedElementRepresentationManager()
//				.clearByView(EIDType.REFSEQ_MRNA_INT, iUniqueID);

		generalManager.getViewGLCanvasManager().getConnectedElementRepresentationManager().clearAll();
		
		generalManager.getViewGLCanvasManager().unregisterGLEventListener(
				iUniqueID);
	}
	
	@Override
	public synchronized void addSet(ISet set)
	{
		super.addSet(set);
		setDisplayListDirty();
	}

	@Override
	public synchronized void addSet(int iSetID)
	{
		super.addSet(iSetID);
		setDisplayListDirty();
	}

	@Override
	public synchronized void removeSets(ESetType setType)
	{
		super.removeSets(setType);
		setDisplayListDirty();
	}

	@Override
	public synchronized void clearSets()
	{
		super.clearSets();
		setDisplayListDirty();
	}
}
