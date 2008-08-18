package org.caleydo.core.view.opengl.canvas;

import gleem.linalg.Vec3f;
import java.util.ArrayList;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import org.caleydo.core.command.CommandType;
import org.caleydo.core.command.view.swt.CmdViewLoadURLInHTMLBrowser;
import org.caleydo.core.data.AUniqueObject;
import org.caleydo.core.data.collection.ESetType;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.view.camera.IViewCamera;
import org.caleydo.core.data.view.camera.IViewFrustum;
import org.caleydo.core.data.view.camera.ViewCameraBase;
import org.caleydo.core.data.view.camera.ViewFrustumBase.ProjectionMode;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.data.ISetManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.view.opengl.canvas.remote.IGLCanvasRemoteRendering3D;
import org.caleydo.core.view.opengl.mouse.PickingJoglMouseListener;
import org.caleydo.core.view.opengl.util.GLToolboxRenderer;
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
	
	// TODO: should be a list of parent canvas object to be generic
	protected GLCaleydoCanvas parentGLCanvas;

	/**
	 * List for all ISet objects providing data for this ViewRep.
	 */
	protected ArrayList<ISet> alSetData;

	protected transient ISetManager setManager;

	protected PickingManager pickingManager;

	protected PickingJoglMouseListener pickingTriggerMouseAdapter;

	protected IViewFrustum viewFrustum;

	protected IViewCamera viewCamera;

	protected transient GLToolboxRenderer glToolboxRenderer;

	protected IGLCanvasRemoteRendering3D remoteRenderingGLCanvas;
	
	/**
	 * The views current aspect ratio. Value gets updated when reshape is called
	 * by the JOGL animator.
	 */
	protected float fAspectRatio = 1f;

	/**
	 * Constructor.
	 */
	protected AGLEventListener(final int iGLCanvasID, 
			final String sLabel, final IViewFrustum viewFrustum,
			final boolean bRegisterToParentCanvasNow)
	{
		super(GeneralManager.get().getIDManager().createID(EManagedObjectType.GL_EVENT_LISTENER));

		generalManager = GeneralManager.get();
		
		alSetData = new ArrayList<ISet>();
//		alSelection = new ArrayList<Selection>();

		setManager = generalManager.getSetManager();

		parentGLCanvas = ((GLCaleydoCanvas) generalManager.getViewGLCanvasManager().getCanvas(iGLCanvasID));

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

	/*
	 * (non-Javadoc)
	 * @see
	 * javax.media.opengl.GLEventListener#init(javax.media.opengl.GLAutoDrawable
	 * )
	 */
	public void init(GLAutoDrawable drawable)
	{

		generalManager.getViewGLCanvasManager().getInfoAreaManager().initInfoOverlay(
				iUniqueID, drawable);

		generalManager.getViewGLCanvasManager().getInfoAreaManager().initInfoInPlace(
				viewFrustum);

		generalManager.getViewGLCanvasManager().getInfoAreaManager().enable(false);

		pickingTriggerMouseAdapter.addGLCanvas(this);

		((GLEventListener) parentGLCanvas).init(drawable);

		initLocal(drawable.getGL());
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * javax.media.opengl.GLEventListener#display(javax.media.opengl.GLAutoDrawable
	 * )
	 */
	public void display(GLAutoDrawable drawable)
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

		generalManager.getViewGLCanvasManager().getInfoAreaManager().renderInfoOverlay(
				iUniqueID, drawable);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * javax.media.opengl.GLEventListener#displayChanged(javax.media.opengl.
	 * GLAutoDrawable, boolean, boolean)
	 */
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
			boolean deviceChanged)
	{

		((GLEventListener) parentGLCanvas)
				.displayChanged(drawable, modeChanged, deviceChanged);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * javax.media.opengl.GLEventListener#reshape(javax.media.opengl.GLAutoDrawable
	 * , int, int, int, int)
	 */
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
	{

		GL gl = drawable.getGL();

		// generalManager.logMsg(
		// "\n---------------------------------------------------"+
		// "\nGLEventListener with ID " +iUniqueID+ " RESHAPE GL" +
		// "\nGL_VENDOR: " + gl.glGetString(GL.GL_VENDOR)+
		// "\nGL_RENDERER: " + gl.glGetString(GL.GL_RENDERER) +
		// "\nGL_VERSION: " + gl.glGetString(GL.GL_VERSION),
		// LoggerType.STATUS);

		fAspectRatio = (float) height / (float) width;

		gl.glViewport(x, y, width, height);
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();

		if (fAspectRatio < 1.0)
		{
			if (viewFrustum.getProjectionMode().equals(ProjectionMode.ORTHOGRAPHIC))
			{
				gl.glOrtho(viewFrustum.getLeft() * 1.0f / fAspectRatio, viewFrustum.getRight()
						* 1.0f / fAspectRatio, viewFrustum.getBottom(), viewFrustum.getTop(),
						viewFrustum.getNear(), viewFrustum.getFar());
			}
			else
			{
				gl.glFrustum(viewFrustum.getLeft() * 1.0f / fAspectRatio, viewFrustum
						.getRight()
						* 1.0f / fAspectRatio, viewFrustum.getBottom(), viewFrustum.getTop(),
						viewFrustum.getNear(), viewFrustum.getFar());
			}
		}
		else
		{
			if (viewFrustum.getProjectionMode().equals(ProjectionMode.ORTHOGRAPHIC))
			{
				gl.glOrtho(viewFrustum.getLeft(), viewFrustum.getRight(), viewFrustum
						.getBottom()
						* fAspectRatio, viewFrustum.getTop() * fAspectRatio, viewFrustum
						.getNear(), viewFrustum.getFar());
			}
			else
			{
				gl.glFrustum(viewFrustum.getLeft(), viewFrustum.getRight(), viewFrustum
						.getBottom()
						* fAspectRatio, viewFrustum.getTop() * fAspectRatio, viewFrustum
						.getNear(), viewFrustum.getFar());
			}
		}

		gl.glMatrixMode(GL.GL_MODELVIEW);

		// // Just for testing
		// float[] test =
		// GLCoordinateUtils.convertWindowCoordinatesToWorldCoordinates(
		// gl, width, height);
		// System.out.println("Object space coordinates: " +test[0] + "," +
		// test[1] + "," + test[2]);
		// viewFrustum.setLeft(-test[0] / 2);
		// viewFrustum.setRight(test[0] / 2);
		// viewFrustum.setBottom(-test[1] / 2);
		// viewFrustum.setTop(test[1] / 2);
	}

	public final EManagedObjectType getBaseType()
	{

		return null;
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
	public abstract void initLocal(final GL gl);

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
	public abstract void displayLocal(final GL gl);

	/**
	 * Intended for external use when another instance of a view manages the
	 * scene. This is specially designed for composite views. Has to call
	 * display internally!
	 * 
	 * @param gl
	 */
	public abstract void displayRemote(final GL gl);

	public final void addSet(ISet set)
	{

		alSetData.add(set);
	}

	public final void addSet(int iSetID)
	{

		alSetData.add((ISet) generalManager.getSetManager().getItem(iSetID));
	}


	public final void removeAllSetIdByType(ESetType setType)
	{

	}

	public final void removeSetId(int[] iSet)
	{

	}

	public final boolean hasSetId(int iSetId)
	{

		return false;
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

		// TODO: should only iterate over those relevant to the view - should
		// it?
		for (EPickingType ePickingType : EPickingType.values())
		{
			ArrayList<Pick> alHits = null;

			alHits = pickingManager.getHits(iUniqueID, ePickingType);
			if (alHits != null)
			{
				if (alHits.size() != 0)
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

	public abstract ArrayList<String> getInfo();

	public GLToolboxRenderer getToolboxRenderer()
	{

		return glToolboxRenderer;
	}

	public final IViewCamera getViewCamera()
	{

		return viewCamera;
	}
	
	public void loadURLInBrowser(final String sUrl)
	{
		if (sUrl.length() == 0)
			return;

		CmdViewLoadURLInHTMLBrowser createdCmd = (CmdViewLoadURLInHTMLBrowser) generalManager
				.getCommandManager().createCommandByType(
						CommandType.LOAD_URL_IN_BROWSER);

		createdCmd.setAttributes(sUrl);
		createdCmd.doCommand();
	}
	
	public abstract void broadcastElements(ESelectionType type);
	
}
