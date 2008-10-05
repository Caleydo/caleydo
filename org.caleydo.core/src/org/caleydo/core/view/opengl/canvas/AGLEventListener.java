package org.caleydo.core.view.opengl.canvas;

import gleem.linalg.Vec3f;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.view.swt.CmdViewLoadURLInHTMLBrowser;
import org.caleydo.core.data.AUniqueObject;
import org.caleydo.core.data.collection.ESetType;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.view.camera.IViewCamera;
import org.caleydo.core.data.view.camera.IViewFrustum;
import org.caleydo.core.data.view.camera.ViewCameraBase;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.data.ISetManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.util.exception.ExceptionHandler;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.GLGlyph;
import org.caleydo.core.view.opengl.canvas.glyph.sliderview.GLGlyphSliderView;
import org.caleydo.core.view.opengl.canvas.remote.GLRemoteRendering;
import org.caleydo.core.view.opengl.canvas.remote.IGLCanvasRemoteRendering3D;
import org.caleydo.core.view.opengl.mouse.PickingJoglMouseListener;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteHierarchyLayer;

/**
 * Abstract class for OpenGL views.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * @author Alexander Lex
 */
public abstract class AGLEventListener
	extends AUniqueObject
	implements GLEventListener
{
	protected IGeneralManager generalManager;

	protected EManagedObjectType viewType = EManagedObjectType.GL_EVENT_LISTENER;
	// TODO: should be a list of parent canvas object to be generic
	protected GLCaleydoCanvas parentGLCanvas;

	/**
	 * List for all ISet objects providing data for this ViewRep.
	 */
	protected ArrayList<ISet> alSets;

	protected transient ISetManager setManager;

	protected PickingManager pickingManager;

	protected PickingJoglMouseListener pickingTriggerMouseAdapter;

	protected IViewFrustum viewFrustum;

	protected IViewCamera viewCamera;

	protected IGLCanvasRemoteRendering3D remoteRenderingGLCanvas;

	/**
	 * The views current aspect ratio. Value gets updated when reshape is called
	 * by the JOGL animator.
	 */
	protected float fAspectRatio = 1f;

	protected EDetailLevel detailLevel = EDetailLevel.HIGH;

	protected boolean bIsDisplayListDirtyLocal = true;
	protected boolean bIsDisplayListDirtyRemote = true;
	protected boolean bHasFrustumChanged = false;

	/**
	 * Constructor.
	 */
	protected AGLEventListener(final int iGLCanvasID, final String sLabel,
			final IViewFrustum viewFrustum, final boolean bRegisterToParentCanvasNow)
	{
		super(GeneralManager.get().getIDManager().createID(
				EManagedObjectType.GL_EVENT_LISTENER));

		generalManager = GeneralManager.get();

		alSets = new ArrayList<ISet>();
		// alSelection = new ArrayList<Selection>();

		setManager = generalManager.getSetManager();

		parentGLCanvas = (generalManager.getViewGLCanvasManager().getCanvas(iGLCanvasID));

		if (bRegisterToParentCanvasNow && parentGLCanvas != null)
		{
			// Register GL event listener view to GL canvas
			parentGLCanvas.addGLEventListener(this);

			generalManager.getViewGLCanvasManager().registerGLEventListenerByGLCanvasID(
					parentGLCanvas.getID(), this);

			pickingTriggerMouseAdapter = parentGLCanvas.getJoglMouseListener();
		}
		// Frustum will only be remotely rendered by another view
		else
		{
			generalManager.getViewGLCanvasManager().registerGLEventListenerByGLCanvasID(-1,
					this);
		}

		this.viewFrustum = viewFrustum;

		viewCamera = new ViewCameraBase(iUniqueID); // FIXME: generate own ID
		// for camera

		pickingManager = generalManager.getViewGLCanvasManager().getPickingManager();
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
	public void display(GLAutoDrawable drawable)
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
	
			// generalManager.getViewGLCanvasManager().getInfoAreaManager().
			// renderInfoOverlay(
			// iUniqueID, drawable);
		
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
		// gl.glVertex3f(viewFrustum.getLeft(), viewFrustum.getBottom(),
		// -0.01f);
		// gl.glVertex3f(viewFrustum.getRight(), viewFrustum.getBottom(),
		// -0.01f);
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
			final RemoteHierarchyLayer layer,
			final PickingJoglMouseListener pickingTriggerMouseAdapter,
			final IGLCanvasRemoteRendering3D remoteRenderingGLCanvas);

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

	public void addSet(ISet set)
	{
		alSets.add(set);
		setDisplayListDirty();
	}

	public void addSet(int iSetID)
	{
		alSets.add(generalManager.getSetManager().getItem(iSetID));
		setDisplayListDirty();
	}

	public void removeSets(ESetType setType)
	{
		Iterator<ISet> iter = alSets.iterator();
		while (iter.hasNext())
		{
			if (iter.next().getSetType() == setType)
				iter.remove();
		}
		setDisplayListDirty();
	}

	public void clearSets()
	{
		alSets.clear();
		setDisplayListDirty();
	}

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
	 * @param gl the gl of the context, remote gl when called remote
	 * @param leftPoint is the bottom left point if bRenderLeftToRight is true,
	 *            else the top left point
	 * @param layer
	 * @param bIsCalledLocally true if called locally
	 * @param bRenderLeftToRight true if it should be rendered left to right,
	 *            false if top to bottom
	 */
	public void renderToolbox(final GL gl, final Vec3f leftPoint,
			final RemoteHierarchyLayer layer, final boolean bIsCalledLocally,
			final boolean bRenderLeftToRight)
	{

	}

	/**
	 * This class uses the pickingManager to check if any events have occured it
	 * calls the abstract handleEvents method where the events should be handled
	 * 
	 * @param gl
	 */
	protected void checkForHits(final GL gl)
	{

		for (EPickingType ePickingType : EPickingType.values())
		{
			// if (ePickingType.getViewType() != viewType)
			// {
			// if(viewType == EManagedObjectType.GL_EVENT_LISTENER)
			// throw new IllegalStateException(
			// "Views must define their view type in the constructor");
			// continue;
			// }

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

					// FIXME: Is this ok?
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

	public void loadURLInBrowser(final String sUrl)
	{
		if (sUrl.length() == 0)
			return;

		CmdViewLoadURLInHTMLBrowser createdCmd = (CmdViewLoadURLInHTMLBrowser) generalManager
				.getCommandManager().createCommandByType(ECommandType.LOAD_URL_IN_BROWSER);

		createdCmd.setAttributes(sUrl);
		createdCmd.doCommand();
	}

	/**
	 * Broadcast elements only with a given type.
	 */
	public abstract void broadcastElements(ESelectionType type);

	public void setDetailLevel(EDetailLevel detailLevel)
	{
		this.detailLevel = detailLevel;
		setDisplayListDirty();
	}

	public boolean isRenderedRemote()
	{
		if (remoteRenderingGLCanvas == null)
			return false;

		return true;
	}
}
