package org.caleydo.core.view.opengl.canvas.remote;

import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import gleem.linalg.Vec4f;
import gleem.linalg.open.Transform;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.view.opengl.CmdCreateGLEventListener;
import org.caleydo.core.command.view.opengl.CmdCreateGLPathway;
import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.data.collection.ESetType;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.graph.ICaleydoGraphItem;
import org.caleydo.core.data.graph.pathway.core.PathwayGraph;
import org.caleydo.core.data.graph.pathway.item.vertex.PathwayVertexGraphItem;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.mapping.EMappingType;
import org.caleydo.core.data.selection.DeltaEventContainer;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.EVAOperation;
import org.caleydo.core.data.selection.ISelectionDelta;
import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.event.EMediatorType;
import org.caleydo.core.manager.event.IDListEventContainer;
import org.caleydo.core.manager.event.IEventContainer;
import org.caleydo.core.manager.event.IMediatorReceiver;
import org.caleydo.core.manager.event.IMediatorSender;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.util.system.SystemTime;
import org.caleydo.core.util.system.Time;
import org.caleydo.core.view.opengl.camera.EProjectionMode;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.cell.GLCell;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.GLGlyph;
import org.caleydo.core.view.opengl.canvas.panel.GLSelectionPanel;
import org.caleydo.core.view.opengl.canvas.pathway.GLPathway;
import org.caleydo.core.view.opengl.canvas.remote.bucket.BucketMouseWheelListener;
import org.caleydo.core.view.opengl.canvas.remote.bucket.GLConnectionLineRendererBucket;
import org.caleydo.core.view.opengl.canvas.remote.jukebox.GLConnectionLineRendererJukebox;
import org.caleydo.core.view.opengl.canvas.storagebased.AStorageBasedView;
import org.caleydo.core.view.opengl.canvas.storagebased.heatmap.GLHeatMap;
import org.caleydo.core.view.opengl.canvas.storagebased.parcoords.GLParallelCoordinates;
import org.caleydo.core.view.opengl.miniview.GLColorMappingBarMiniView;
import org.caleydo.core.view.opengl.mouse.PickingJoglMouseListener;
import org.caleydo.core.view.opengl.renderstyle.layout.ARemoteViewLayoutRenderStyle;
import org.caleydo.core.view.opengl.renderstyle.layout.BucketLayoutRenderStyle;
import org.caleydo.core.view.opengl.renderstyle.layout.JukeboxLayoutRenderStyle;
import org.caleydo.core.view.opengl.renderstyle.layout.ListLayoutRenderStyle;
import org.caleydo.core.view.opengl.renderstyle.layout.ARemoteViewLayoutRenderStyle.LayoutMode;
import org.caleydo.core.view.opengl.util.drag.GLDragAndDrop;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteElementManager;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevel;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevelElement;
import org.caleydo.core.view.opengl.util.slerp.SlerpAction;
import org.caleydo.core.view.opengl.util.slerp.SlerpMod;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.core.view.opengl.util.texture.GLOffScreenTextureRenderer;
import org.caleydo.util.graph.EGraphItemHierarchy;
import org.caleydo.util.graph.EGraphItemProperty;
import org.caleydo.util.graph.IGraphItem;
import com.sun.opengl.util.j2d.TextRenderer;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;

/**
 * Abstract class that is able to remotely rendering views. Subclasses implement
 * the positioning of the views (bucket, jukebox, etc.).
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public class GLRemoteRendering
	extends AGLEventListener
	implements IMediatorReceiver, IMediatorSender, IGLCanvasRemoteRendering
{
	private ARemoteViewLayoutRenderStyle.LayoutMode layoutMode;

	private static final int SLERP_RANGE = 1000;
	private static final int SLERP_SPEED = 1400;

	// private GenericSelectionManager selectionManager;

	private int iMouseOverObjectID = -1;

	private RemoteLevel focusLevel;
	private RemoteLevel stackLevel;
	private RemoteLevel poolLevel;
	private RemoteLevel transitionLevel;
	private RemoteLevel spawnLevel;
	private RemoteLevel selectionLevel;

	private ArrayList<SlerpAction> arSlerpActions;

	private Time time;

	/**
	 * Slerp factor: 0 = source; 1 = destination
	 */
	private int iSlerpFactor = 0;

	protected AGLConnectionLineRenderer glConnectionLineRenderer;

	private int iNavigationMouseOverViewID_left = -1;
	private int iNavigationMouseOverViewID_right = -1;
	private int iNavigationMouseOverViewID_out = -1;
	private int iNavigationMouseOverViewID_in = -1;
	private int iNavigationMouseOverViewID_lock = -1;

	private boolean bEnableNavigationOverlay = false;

	private ArrayList<Integer> iAlUninitializedPathwayIDs;

	private TextRenderer textRenderer;

	private GLDragAndDrop dragAndDrop;

	private ARemoteViewLayoutRenderStyle layoutRenderStyle;

	private BucketMouseWheelListener bucketMouseWheelListener;

	private GLColorMappingBarMiniView colorMappingBarMiniView;

	private ArrayList<Integer> iAlContainedViewIDs;

	/**
	 * The current view in which the user is performing actions.
	 */
	private int iActiveViewID = -1;

	// private int iGLDisplayList;

	private GLSelectionPanel glSelectionPanel;

	private ISelectionDelta lastSelectionDelta;

	/**
	 * Used for dragging views to the pool area.
	 */
	private int iPoolLevelCommonID = -1;

	private GLOffScreenTextureRenderer glOffScreenRenderer;

	private boolean bUpdateOffScreenTextures = true;

	private boolean bEnableConnectinLines = true;

	/**
	 * Constructor.
	 */
	public GLRemoteRendering(final int iGLCanvasID, final String sLabel,
			final IViewFrustum viewFrustum,
			final ARemoteViewLayoutRenderStyle.LayoutMode layoutMode)
	{
		super(iGLCanvasID, sLabel, viewFrustum, true);
		viewType = EManagedObjectType.GL_REMOTE_RENDERING;
		this.layoutMode = layoutMode;

		// if (generalManager.isWiiModeActive())
		// {
		glOffScreenRenderer = new GLOffScreenTextureRenderer();
		// }

		if (layoutMode.equals(ARemoteViewLayoutRenderStyle.LayoutMode.BUCKET))
		{
			layoutRenderStyle = new BucketLayoutRenderStyle(viewFrustum);
			super.renderStyle = layoutRenderStyle;

			bucketMouseWheelListener = new BucketMouseWheelListener(this,
					(BucketLayoutRenderStyle) layoutRenderStyle);

			// Unregister standard mouse wheel listener
			parentGLCanvas.removeMouseWheelListener(pickingTriggerMouseAdapter);
			// Register specialized bucket mouse wheel listener
			parentGLCanvas.addMouseWheelListener(bucketMouseWheelListener);
			// parentGLCanvas.addMouseListener(bucketMouseWheelListener);

		}
		else if (layoutMode.equals(ARemoteViewLayoutRenderStyle.LayoutMode.JUKEBOX))
		{
			layoutRenderStyle = new JukeboxLayoutRenderStyle(viewFrustum);
		}
		else if (layoutMode.equals(ARemoteViewLayoutRenderStyle.LayoutMode.LIST))
		{
			layoutRenderStyle = new ListLayoutRenderStyle(viewFrustum);
		}

		focusLevel = layoutRenderStyle.initFocusLevel();

		if (GeneralManager.get().isWiiModeActive()
				&& layoutMode.equals(ARemoteViewLayoutRenderStyle.LayoutMode.BUCKET))
		{
			stackLevel = ((BucketLayoutRenderStyle) layoutRenderStyle).initStackLevelWii();
		}
		else
		{
			stackLevel = layoutRenderStyle.initStackLevel(bucketMouseWheelListener
					.isZoomedIn());
		}

		poolLevel = layoutRenderStyle.initPoolLevel(bucketMouseWheelListener.isZoomedIn(), -1);
		selectionLevel = layoutRenderStyle.initMemoLevel();
		transitionLevel = layoutRenderStyle.initTransitionLevel();
		spawnLevel = layoutRenderStyle.initSpawnLevel();

		if (layoutMode.equals(ARemoteViewLayoutRenderStyle.LayoutMode.BUCKET))
		{
			glConnectionLineRenderer = new GLConnectionLineRendererBucket(focusLevel,
					stackLevel, poolLevel);
		}
		else if (layoutMode.equals(ARemoteViewLayoutRenderStyle.LayoutMode.JUKEBOX))
		{
			glConnectionLineRenderer = new GLConnectionLineRendererJukebox(focusLevel,
					stackLevel, poolLevel);
		}
		else if (layoutMode.equals(ARemoteViewLayoutRenderStyle.LayoutMode.LIST))
		{
			glConnectionLineRenderer = null;
		}

		pickingTriggerMouseAdapter.addGLCanvas(this);

		arSlerpActions = new ArrayList<SlerpAction>();

		iAlUninitializedPathwayIDs = new ArrayList<Integer>();

		createEventMediator();

		dragAndDrop = new GLDragAndDrop();

		textRenderer = new TextRenderer(new Font("Arial", Font.PLAIN, 24), false);

		// trashCan = new TrashCan();

		// TODO: the genome mapper should be stored centralized instead of newly
		// created
		colorMappingBarMiniView = new GLColorMappingBarMiniView(viewFrustum);

		// Create selection panel
		CmdCreateGLEventListener cmdCreateGLView = (CmdCreateGLEventListener) generalManager
				.getCommandManager().createCommandByType(
						ECommandType.CREATE_GL_PANEL_SELECTION);
		cmdCreateGLView.setAttributes(EProjectionMode.ORTHOGRAPHIC, 0, 0.8f, 0, 4, -20, 20,
				null, -1);
		cmdCreateGLView.doCommand();
		glSelectionPanel = (GLSelectionPanel) cmdCreateGLView.getCreatedObject();

		// Registration to event system
		generalManager.getEventPublisher().addSender(EMediatorType.SELECTION_MEDIATOR,
				(IMediatorSender) this);
		generalManager.getEventPublisher().addReceiver(EMediatorType.SELECTION_MEDIATOR,
				(IMediatorReceiver) this);
		generalManager.getEventPublisher().addSender(EMediatorType.VIEW_SELECTION, this);

		iPoolLevelCommonID = generalManager.getIDManager().createID(
				EManagedObjectType.REMOTE_LEVEL_ELEMENT);
	}

	@Override
	public void initLocal(final GL gl)
	{
		// iGLDisplayList = gl.glGenLists(1);

		init(gl);
	}

	@Override
	public void initRemote(final GL gl, final int iRemoteViewID,
			final PickingJoglMouseListener pickingTriggerMouseAdapter,
			final IGLCanvasRemoteRendering remoteRenderingGLCanvas)
	{

		throw new IllegalStateException("Not implemented to be rendered remote");
	}

	@Override
	public void init(final GL gl)
	{
		gl.glClearColor(0.5f, 0.5f, 0.5f, 1f);

		if (glConnectionLineRenderer != null)
			glConnectionLineRenderer.init(gl);

		// iconTextureManager = new GLIconTextureManager(gl);

		time = new SystemTime();
		((SystemTime) time).rebase();

		initializeContainedViews(gl);

		selectionLevel.getElementByPositionIndex(0).setContainedElementID(
				glSelectionPanel.getID());
		// selectionLevel.setElementVisibilityById(true,
		// glSelectionPanel.getID());

		glSelectionPanel.initRemote(gl, getID(), pickingTriggerMouseAdapter,
				remoteRenderingGLCanvas);

		colorMappingBarMiniView.setWidth(layoutRenderStyle.getColorBarWidth());
		colorMappingBarMiniView.setHeight(layoutRenderStyle.getColorBarHeight());

		glOffScreenRenderer.init(gl);
	}

	@Override
	public synchronized void displayLocal(final GL gl)
	{
		if ((pickingTriggerMouseAdapter.wasRightMouseButtonPressed() && !bucketMouseWheelListener
				.isZoomedIn())
				&& !(layoutRenderStyle instanceof ListLayoutRenderStyle))
		{
			bEnableNavigationOverlay = !bEnableNavigationOverlay;

			if (glConnectionLineRenderer != null)
				glConnectionLineRenderer.enableRendering(!bEnableNavigationOverlay);
		}

		pickingManager.handlePicking(iUniqueID, gl, true);

		// if (bIsDisplayListDirtyLocal)
		// {
		// buildDisplayList(gl);
		// bIsDisplayListDirtyLocal = false;
		// }

		display(gl);

		if (eBusyModeState != EBusyModeState.OFF)
			renderBusyMode(gl);

		if (pickingTriggerMouseAdapter.getPickedPoint() != null)
			dragAndDrop.setCurrentMousePos(gl, pickingTriggerMouseAdapter.getPickedPoint());

		if (dragAndDrop.isDragActionRunning())
		{
			dragAndDrop.renderDragThumbnailTexture(gl);
		}

		if (pickingTriggerMouseAdapter.wasMouseReleased() && dragAndDrop.isDragActionRunning())
		{
			int iDraggedObjectId = dragAndDrop.getDraggedObjectedId();

			// System.out.println("over: " +iExternalID);
			// System.out.println("dragged: " +iDraggedObjectId);

			// Prevent user from dragging element onto selection level
			if (!RemoteElementManager.get().hasItem(iMouseOverObjectID)
					|| !selectionLevel.containsElement(RemoteElementManager.get().getItem(
							iMouseOverObjectID)))
			{
				RemoteLevelElement mouseOverElement = null;

				// Check if a drag and drop action is performed onto the pool
				// level
				if (iMouseOverObjectID == iPoolLevelCommonID)
				{
					mouseOverElement = poolLevel.getNextFree();
				}
				else if (mouseOverElement == null && iMouseOverObjectID != iDraggedObjectId)
				{
					mouseOverElement = RemoteElementManager.get().getItem(iMouseOverObjectID);
				}

				if (mouseOverElement != null)
				{
					RemoteLevelElement originElement = RemoteElementManager.get().getItem(
							iDraggedObjectId);

					int iMouseOverElementID = mouseOverElement.getContainedElementID();
					int iOriginElementID = originElement.getContainedElementID();

					mouseOverElement.setContainedElementID(iOriginElementID);
					originElement.setContainedElementID(iMouseOverElementID);

					IViewManager viewGLCanvasManager = generalManager.getViewGLCanvasManager();

					AGLEventListener originView = viewGLCanvasManager
							.getGLEventListener(iOriginElementID);
					if (originView != null)
						originView.setRemoteLevelElement(mouseOverElement);

					AGLEventListener mouseOverView = viewGLCanvasManager
							.getGLEventListener(iMouseOverElementID);
					if (mouseOverView != null)
						mouseOverView.setRemoteLevelElement(originElement);

					updateViewDetailLevels(originElement);
					updateViewDetailLevels(mouseOverElement);

					if (mouseOverElement.getContainedElementID() != -1)
					{
						if (poolLevel.containsElement(originElement)
								&& (stackLevel.containsElement(mouseOverElement) || focusLevel
										.containsElement(mouseOverElement)))
						{
							generalManager.getViewGLCanvasManager().getGLEventListener(
									mouseOverElement.getContainedElementID())
									.broadcastElements(EVAOperation.APPEND_UNIQUE);
						}

						if (poolLevel.containsElement(mouseOverElement)
								&& (stackLevel.containsElement(originElement) || focusLevel
										.containsElement(originElement)))
						{
							generalManager.getViewGLCanvasManager().getGLEventListener(
									mouseOverElement.getContainedElementID())
									.broadcastElements(EVAOperation.REMOVE_ELEMENT);
						}
					}
				}
			}

			dragAndDrop.stopDragAction();
			bUpdateOffScreenTextures = true;
		}

		checkForHits(gl);

		pickingTriggerMouseAdapter.resetEvents();
		// gl.glCallList(iGLDisplayListIndexLocal);
	}

	@Override
	public synchronized void displayRemote(final GL gl)
	{
		display(gl);
	}

	@Override
	public synchronized void display(final GL gl)
	{
		time.update();

		layoutRenderStyle.initPoolLevel(false, iMouseOverObjectID);
		// layoutRenderStyle.initStackLevel(false);
		// layoutRenderStyle.initMemoLevel();

		if (GeneralManager.get().isWiiModeActive()
				&& layoutMode.equals(ARemoteViewLayoutRenderStyle.LayoutMode.BUCKET))
		{
			((BucketLayoutRenderStyle) layoutRenderStyle).initFocusLevelWii();

			((BucketLayoutRenderStyle) layoutRenderStyle).initStackLevelWii();
		}

		doSlerpActions(gl);
		initializeNewPathways(gl);

		if (!generalManager.isWiiModeActive())
		{
			renderRemoteLevel(gl, focusLevel);
			renderRemoteLevel(gl, stackLevel);
		}
		else
		{
			if (bUpdateOffScreenTextures)
				updateOffScreenTextures(gl);

			renderRemoteLevel(gl, focusLevel);

			glOffScreenRenderer.renderRubberBucket(gl, stackLevel,
					(BucketLayoutRenderStyle) layoutRenderStyle, this);
		}

		// If user zooms to the bucket bottom all but the under
		// focus layer is _not_ rendered.
		if (bucketMouseWheelListener == null || !bucketMouseWheelListener.isZoomedIn())
		{
			// comment here for connection lines
			if (glConnectionLineRenderer != null && bEnableConnectinLines)
				glConnectionLineRenderer.render(gl);

			renderPoolAndMemoLayerBackground(gl);

			renderRemoteLevel(gl, transitionLevel);
			renderRemoteLevel(gl, spawnLevel);
			renderRemoteLevel(gl, poolLevel);
			renderRemoteLevel(gl, selectionLevel);
		}

		if (layoutMode.equals(ARemoteViewLayoutRenderStyle.LayoutMode.BUCKET))
		{
			bucketMouseWheelListener.render();
		}

		// colorMappingBarMiniView.render(gl,
		// layoutRenderStyle.getColorBarXPos(),
		// layoutRenderStyle.getColorBarYPos(), 4);

		renderHandles(gl);

		// gl.glCallList(iGLDisplayList);
	}

	public synchronized void setInitialContainedViews(
			ArrayList<Integer> iAlInitialContainedViewIDs)
	{
		iAlContainedViewIDs = iAlInitialContainedViewIDs;
	}

	private void initializeContainedViews(final GL gl)
	{
		if (iAlContainedViewIDs == null)
			return;

		for (int iContainedViewID : iAlContainedViewIDs)
		{
			AGLEventListener tmpGLEventListener = generalManager.getViewGLCanvasManager()
					.getGLEventListener(iContainedViewID);

			// Ignore pathway views upon startup
			// because they will be activated when pathway loader thread has
			// finished
			if (tmpGLEventListener == this || tmpGLEventListener instanceof GLPathway)
			{
				continue;
			}

			int iViewID = (tmpGLEventListener).getID();

			if (focusLevel.hasFreePosition())
			{
				RemoteLevelElement element = focusLevel.getNextFree();
				element.setContainedElementID(iViewID);

				tmpGLEventListener.initRemote(gl, iUniqueID, pickingTriggerMouseAdapter, this);

				tmpGLEventListener.broadcastElements(EVAOperation.APPEND_UNIQUE);
				tmpGLEventListener.setDetailLevel(EDetailLevel.MEDIUM);
				tmpGLEventListener.setRemoteLevelElement(element);

				// generalManager.getGUIBridge().setActiveGLSubView(this,
				// tmpGLEventListener);

			}
			else if (stackLevel.hasFreePosition()
					&& !(layoutRenderStyle instanceof ListLayoutRenderStyle))
			{
				RemoteLevelElement element = stackLevel.getNextFree();
				element.setContainedElementID(iViewID);

				tmpGLEventListener.initRemote(gl, iUniqueID, pickingTriggerMouseAdapter, this);

				tmpGLEventListener.broadcastElements(EVAOperation.APPEND_UNIQUE);
				tmpGLEventListener.setDetailLevel(EDetailLevel.LOW);
				tmpGLEventListener.setRemoteLevelElement(element);
			}
			else if (poolLevel.hasFreePosition())
			{
				RemoteLevelElement element = poolLevel.getNextFree();
				element.setContainedElementID(iViewID);

				tmpGLEventListener.initRemote(gl, iUniqueID, pickingTriggerMouseAdapter, this);
				tmpGLEventListener.setDetailLevel(EDetailLevel.VERY_LOW);
				tmpGLEventListener.setRemoteLevelElement(element);
			}

			// pickingTriggerMouseAdapter.addGLCanvas(tmpGLEventListener);
			pickingManager.getPickingID(iUniqueID, EPickingType.VIEW_SELECTION, iViewID);

			generalManager.getEventPublisher().addSender(EMediatorType.SELECTION_MEDIATOR,
					(IMediatorSender) tmpGLEventListener);
			generalManager.getEventPublisher().addReceiver(EMediatorType.SELECTION_MEDIATOR,
					(IMediatorReceiver) tmpGLEventListener);
		}
	}

	public void renderBucketWall(final GL gl, boolean bRenderBorder, RemoteLevelElement element)
	{
		// Highlight potential view drop destination
		if (dragAndDrop.isDragActionRunning() && element.getID() == iMouseOverObjectID)
		{
			gl.glLineWidth(5);
			gl.glColor4f(0.2f, 0.2f, 0.2f, 1);
			gl.glBegin(GL.GL_LINE_LOOP);
			gl.glVertex3f(0, 0, 0.01f);
			gl.glVertex3f(0, 8, 0.01f);
			gl.glVertex3f(8, 8, 0.01f);
			gl.glVertex3f(8, 0, 0.01f);
			gl.glEnd();
		}

		if (arSlerpActions.isEmpty())
			gl.glColor4f(1f, 1f, 1f, 1.0f); // normal mode
		else
			gl.glColor4f(1f, 1f, 1f, 0.3f);

		if (!iAlUninitializedPathwayIDs.isEmpty())
			gl.glColor4f(1f, 1f, 1f, 0.3f);

		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(0, 0, -0.03f);
		gl.glVertex3f(0, 8, -0.03f);
		gl.glVertex3f(8, 8, -0.03f);
		gl.glVertex3f(8, 0, -0.03f);
		gl.glEnd();

		if (!bRenderBorder)
			return;

		gl.glColor4f(0.4f, 0.4f, 0.4f, 1f);
		gl.glLineWidth(1f);

		// gl.glBegin(GL.GL_LINES);
		// gl.glVertex3f(0, 0, -0.02f);
		// gl.glVertex3f(0, 8, -0.02f);
		// gl.glVertex3f(8, 8, -0.02f);
		// gl.glVertex3f(8, 0, -0.02f);
		// gl.glEnd();
	}

	private void renderRemoteLevel(final GL gl, final RemoteLevel level)
	{
		for (RemoteLevelElement element : level.getAllElements())
		{
			renderRemoteLevelElement(gl, element, level);

			if (!(layoutRenderStyle instanceof ListLayoutRenderStyle))
				renderEmptyBucketWall(gl, element, level);
		}
	}

	private void renderRemoteLevelElement(final GL gl, RemoteLevelElement element,
			RemoteLevel level)
	{
		// // Check if view is visible
		// if (!level.getElementVisibilityById(iViewID))
		// return;

		if (element.getContainedElementID() == -1)
			return;

		int iViewID = element.getContainedElementID();

		gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.REMOTE_LEVEL_ELEMENT, element.getID()));
		gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.VIEW_SELECTION,
				iViewID));

		AGLEventListener glEventListener = (generalManager.getViewGLCanvasManager()
				.getGLEventListener(iViewID));

		if (glEventListener == null)
			throw new IllegalStateException("Cannot render canvas object which is null!");

		gl.glPushMatrix();

		Transform transform = element.getTransform();
		Vec3f translation = transform.getTranslation();
		Rotf rot = transform.getRotation();
		Vec3f scale = transform.getScale();
		Vec3f axis = new Vec3f();
		float fAngle = rot.get(axis);

		gl.glTranslatef(translation.x(), translation.y(), translation.z());
		gl.glRotatef(Vec3f.convertRadiant2Grad(fAngle), axis.x(), axis.y(), axis.z());
		gl.glScalef(scale.x(), scale.y(), scale.z());

		if (level == poolLevel)
		{
			String sRenderText = glEventListener.getShortInfo();

			// Limit pathway name in length
			int iMaxChars;
			if (layoutRenderStyle instanceof ListLayoutRenderStyle)
				iMaxChars = 80;
			else
				iMaxChars = 20;

			if (sRenderText.length() > iMaxChars && scale.x() < 0.03f)
				sRenderText = sRenderText.subSequence(0, iMaxChars - 3) + "...";

			if (element.getID() == iMouseOverObjectID)
				textRenderer.setColor(1, 1, 1, 1);
			else
				textRenderer.setColor(0, 0, 0, 1);

			if (glEventListener.getNumberOfSelections(ESelectionType.MOUSE_OVER) > 0)
			{
				textRenderer.setColor(1, 0, 0, 1);
				// sRenderText =
				// glEventListener.getNumberOfSelections(ESelectionType.MOUSE_OVER)
				// + " - " + sRenderText;
			}
			else if (glEventListener.getNumberOfSelections(ESelectionType.SELECTION) > 0)
			{
				textRenderer.setColor(0, 1, 0, 1);
				// sRenderText =
				// glEventListener.getNumberOfSelections(ESelectionType.SELECTION)
				// + " - " + sRenderText;
			}

			float fTextScalingFactor = 0.09f;
			float fTextXPosition = 0f;

			if (element.getID() == iMouseOverObjectID)
			{
				renderPoolSelection(gl, translation.x() - 0.4f / fAspectRatio, translation.y()
						* scale.y() + 5.2f,

				(float) textRenderer.getBounds(sRenderText).getWidth() * 0.06f + 23, 6f,
						element); // 1.8f -> pool focus scaling

				gl.glTranslatef(0.8f, 1.3f, 0);

				fTextScalingFactor = 0.075f;
				fTextXPosition = 12f;
			}
			else
			{
				// Render view background frame
				Texture tempTexture = iconTextureManager.getIconTexture(gl,
						EIconTextures.POOL_VIEW_BACKGROUND);
				tempTexture.enable();
				tempTexture.bind();

				float fFrameWidth = 9.5f;
				TextureCoords texCoords = tempTexture.getImageTexCoords();

				gl.glColor4f(1, 1, 1, 0.75f);

				gl.glBegin(GL.GL_POLYGON);
				gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
				gl.glVertex3f(-0.7f, -0.6f + fFrameWidth, -0.01f);
				gl.glTexCoord2f(texCoords.left(), texCoords.top());
				gl.glVertex3f(-0.7f + fFrameWidth, -0.6f + fFrameWidth, -0.01f);
				gl.glTexCoord2f(texCoords.right(), texCoords.top());
				gl.glVertex3f(-0.7f + fFrameWidth, -0.6f, -0.01f);
				gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
				gl.glVertex3f(-0.7f, -0.6f, -0.01f);
				gl.glEnd();

				tempTexture.disable();

				fTextXPosition = 9.5f;
			}

			textRenderer.begin3DRendering();
			textRenderer.draw3D(sRenderText, fTextXPosition, 3, 0, fTextScalingFactor);
			textRenderer.end3DRendering();
		}

		// Prevent rendering of view textures when simple list view
		// if ((layoutRenderStyle instanceof ListLayoutRenderStyle
		// && (layer == poolLayer || layer == stackLayer)))
		// {
		// gl.glPopMatrix();
		// return;
		// }

		if (level != selectionLevel && level != poolLevel)
		{
			if (level.equals(focusLevel))
				renderBucketWall(gl, false, element);
			else
				renderBucketWall(gl, true, element);
		}

		if (!bEnableNavigationOverlay || !level.equals(stackLevel))
		{
			glEventListener.displayRemote(gl);
		}
		else
		{
			renderNavigationOverlay(gl, element.getID());
		}

		gl.glPopMatrix();

		gl.glPopName();
		gl.glPopName();
	}

	private void renderEmptyBucketWall(final GL gl, RemoteLevelElement element,
			RemoteLevel level)
	{
		gl.glPushMatrix();

		gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.REMOTE_LEVEL_ELEMENT, element.getID()));

		Transform transform = element.getTransform();
		Vec3f translation = transform.getTranslation();
		Rotf rot = transform.getRotation();
		Vec3f scale = transform.getScale();
		Vec3f axis = new Vec3f();
		float fAngle = rot.get(axis);

		gl.glTranslatef(translation.x(), translation.y(), translation.z());
		gl.glScalef(scale.x(), scale.y(), scale.z());
		gl.glRotatef(Vec3f.convertRadiant2Grad(fAngle), axis.x(), axis.y(), axis.z());

		if (!level.equals(transitionLevel) && !level.equals(spawnLevel)
				&& !level.equals(poolLevel) && !level.equals(selectionLevel))
		{
			renderBucketWall(gl, true, element);
		}

		gl.glPopName();

		gl.glPopMatrix();
	}

	private void renderHandles(final GL gl)
	{
		float fZoomedInScalingFactor = 0.4f;

		// Bucket stack top
		RemoteLevelElement element = stackLevel.getElementByPositionIndex(0);
		if (element.getContainedElementID() != -1)
		{
			if (!bucketMouseWheelListener.isZoomedIn())
			{
				gl.glTranslatef(-2, 0, 4.02f);
				renderNavigationHandleBar(gl, element, 4, 0.075f, false, 1);
				gl.glTranslatef(2, 0, -4.02f);
			}
			else
			{
				gl.glTranslatef(-2 - 4 * fZoomedInScalingFactor, 0, 0.02f);
				renderNavigationHandleBar(gl, element, 4 * fZoomedInScalingFactor, 0.075f,
						false, 1 / fZoomedInScalingFactor);
				gl.glTranslatef(2 + 4 * fZoomedInScalingFactor, 0, -0.02f);
			}
		}

		// Bucket stack bottom
		element = stackLevel.getElementByPositionIndex(2);
		if (element.getContainedElementID() != -1)
		{
			if (!bucketMouseWheelListener.isZoomedIn())
			{
				gl.glTranslatef(-2, 0, 4.02f);
				gl.glRotatef(180, 1, 0, 0);
				renderNavigationHandleBar(gl, element, 4, 0.075f, true, 1);
				gl.glRotatef(-180, 1, 0, 0);
				gl.glTranslatef(2, 0, -4.02f);
			}
			else
			{
				gl.glTranslatef(-2 - 4 * fZoomedInScalingFactor, -4 + 4
						* fZoomedInScalingFactor, 0.02f);
				renderNavigationHandleBar(gl, element, 4 * fZoomedInScalingFactor, 0.075f,
						false, 1 / fZoomedInScalingFactor);
				gl.glTranslatef(2 + 4 * fZoomedInScalingFactor, +4 - 4
						* fZoomedInScalingFactor, -0.02f);
			}
		}

		// Bucket stack left
		element = stackLevel.getElementByPositionIndex(1);
		if (element.getContainedElementID() != -1)
		{
			if (!bucketMouseWheelListener.isZoomedIn())
			{
				gl.glTranslatef(-2f / fAspectRatio + 2 + 0.8f, -2, 4.02f);
				gl.glRotatef(90, 0, 0, 1);
				renderNavigationHandleBar(gl, element, 4, 0.075f, false, 1);
				gl.glRotatef(-90, 0, 0, 1);
				gl.glTranslatef(2f / fAspectRatio - 2 - 0.8f, 2, -4.02f);
			}
			else
			{
				gl.glTranslatef(2, 0, 0.02f);
				renderNavigationHandleBar(gl, element, 4 * fZoomedInScalingFactor, 0.075f,
						false, 1 / fZoomedInScalingFactor);
				gl.glTranslatef(-2, 0, -0.02f);
			}
		}

		// Bucket stack right
		element = stackLevel.getElementByPositionIndex(3);
		if (element.getContainedElementID() != -1)
		{
			if (!bucketMouseWheelListener.isZoomedIn())
			{
				gl.glTranslatef(2f / fAspectRatio - 0.8f - 2, 2, 4.02f);
				gl.glRotatef(-90, 0, 0, 1);
				renderNavigationHandleBar(gl, element, 4, 0.075f, false, 1);
				gl.glRotatef(90, 0, 0, 1);
				gl.glTranslatef(-2f / fAspectRatio + 0.8f + 2, -2, -4.02f);
			}
			else
			{
				gl.glTranslatef(2, -4 + 4 * fZoomedInScalingFactor, 0.02f);
				renderNavigationHandleBar(gl, element, 4 * fZoomedInScalingFactor, 0.075f,
						false, 1 / fZoomedInScalingFactor);
				gl.glTranslatef(-2, +4 - 4 * fZoomedInScalingFactor, -0.02f);
			}
		}

		// Bucket center
		element = focusLevel.getElementByPositionIndex(0);
		if (element.getContainedElementID() != -1)
		{
			float fYCorrection = 0f;

			if (!bucketMouseWheelListener.isZoomedIn())
				fYCorrection = 0f;
			else
				fYCorrection = 0.1f;

			Transform transform = element.getTransform();
			Vec3f translation = transform.getTranslation();

			gl.glTranslatef(translation.x(), translation.y() - 2 * 0.075f + fYCorrection,
					translation.z() + 0.001f);

			gl.glScalef(2, 2, 2);
			renderNavigationHandleBar(gl, element, 2, 0.075f, false, 2);
			gl.glScalef(1 / 2f, 1 / 2f, 1 / 2f);

			gl.glTranslatef(-translation.x(), -translation.y() + 2 * 0.075f - fYCorrection,
					-translation.z() - 0.001f);
		}
	}

	private void renderNavigationHandleBar(final GL gl, RemoteLevelElement element,
			float fHandleWidth, float fHandleHeight, boolean bUpsideDown, float fScalingFactor)
	{
		// Render icons
		gl.glTranslatef(0, 2 + fHandleHeight, 0);
		renderSingleHandle(gl, element.getID(), EPickingType.BUCKET_DRAG_ICON_SELECTION,
				EIconTextures.NAVIGATION_DRAG_VIEW, fHandleHeight, fHandleHeight);
		gl.glTranslatef(fHandleWidth - 2 * fHandleHeight, 0, 0);
		if (bUpsideDown)
		{
			gl.glRotatef(180, 1, 0, 0);
			gl.glTranslatef(0, fHandleHeight, 0);
		}
		renderSingleHandle(gl, element.getID(), EPickingType.BUCKET_LOCK_ICON_SELECTION,
				EIconTextures.NAVIGATION_LOCK_VIEW, fHandleHeight, fHandleHeight);
		if (bUpsideDown)
		{
			gl.glTranslatef(0, -fHandleHeight, 0);
			gl.glRotatef(-180, 1, 0, 0);
		}
		gl.glTranslatef(fHandleHeight, 0, 0);
		renderSingleHandle(gl, element.getID(), EPickingType.BUCKET_REMOVE_ICON_SELECTION,
				EIconTextures.NAVIGATION_REMOVE_VIEW, fHandleHeight, fHandleHeight);
		gl.glTranslatef(-fHandleWidth + fHandleHeight, -2 - fHandleHeight, 0);

		// Render background (also draggable)
		gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.BUCKET_DRAG_ICON_SELECTION, element.getID()));
		gl.glColor3f(0.25f, 0.25f, 0.25f);
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(0 + fHandleHeight, 2 + fHandleHeight, 0);
		gl.glVertex3f(fHandleWidth - 2 * fHandleHeight, 2 + fHandleHeight, 0);
		gl.glVertex3f(fHandleWidth - 2 * fHandleHeight, 2, 0);
		gl.glVertex3f(0 + fHandleHeight, 2, 0);
		gl.glEnd();

		gl.glPopName();

		// Render view information
		String sText = generalManager.getViewGLCanvasManager().getGLEventListener(
				element.getContainedElementID()).getShortInfo();

		int iMaxChars = 50;
		if (sText.length() > iMaxChars)
			sText = sText.subSequence(0, iMaxChars - 3) + "...";

		float fTextScalingFactor = 0.0027f;

		if (bUpsideDown)
		{
			gl.glRotatef(180, 1, 0, 0);
			gl.glTranslatef(0, -4 - fHandleHeight, 0);
		}

		textRenderer.setColor(0.7f, 0.7f, 0.7f, 1);
		textRenderer.begin3DRendering();
		textRenderer.draw3D(sText, 2 / fScalingFactor
				- (float) textRenderer.getBounds(sText).getWidth() / 2f * fTextScalingFactor,
				2.02f, 0, fTextScalingFactor);
		textRenderer.end3DRendering();

		if (bUpsideDown)
		{
			gl.glTranslatef(0, 4 + fHandleHeight, 0);
			gl.glRotatef(-180, 1, 0, 0);
		}
	}

	private void renderSingleHandle(final GL gl, int iRemoteLevelElementID,
			EPickingType ePickingType, EIconTextures eIconTexture, float fWidth, float fHeight)
	{
		gl.glPushName(pickingManager.getPickingID(iUniqueID, ePickingType,
				iRemoteLevelElementID));

		Texture tempTexture = iconTextureManager.getIconTexture(gl, eIconTexture);
		tempTexture.enable();
		tempTexture.bind();

		TextureCoords texCoords = tempTexture.getImageTexCoords();
		gl.glColor3f(1, 1, 1);
		gl.glBegin(GL.GL_POLYGON);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(0, -fHeight, 0f);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(0, 0, 0f);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(fWidth, 0, 0f);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(fWidth, -fHeight, 0f);
		gl.glEnd();

		tempTexture.disable();

		gl.glPopName();
	}

	private void renderNavigationOverlay(final GL gl, final int iRemoteLevelElementID)
	{
		if (glConnectionLineRenderer != null)
			glConnectionLineRenderer.enableRendering(false);

		RemoteLevelElement remoteLevelElement = RemoteElementManager.get().getItem(
				iRemoteLevelElementID);

		EPickingType leftWallPickingType = null;
		EPickingType rightWallPickingType = null;
		EPickingType topWallPickingType = null;
		EPickingType bottomWallPickingType = null;

		Vec4f tmpColor_out = new Vec4f(0.9f, 0.9f, 0.9f,
				ARemoteViewLayoutRenderStyle.NAVIGATION_OVERLAY_TRANSPARENCY);
		Vec4f tmpColor_in = new Vec4f(0.9f, 0.9f, 0.9f,
				ARemoteViewLayoutRenderStyle.NAVIGATION_OVERLAY_TRANSPARENCY);
		Vec4f tmpColor_left = new Vec4f(0.9f, 0.9f, 0.9f,
				ARemoteViewLayoutRenderStyle.NAVIGATION_OVERLAY_TRANSPARENCY);
		Vec4f tmpColor_right = new Vec4f(0.9f, 0.9f, 0.9f,
				ARemoteViewLayoutRenderStyle.NAVIGATION_OVERLAY_TRANSPARENCY);
		Vec4f tmpColor_lock = new Vec4f(0.9f, 0.9f, 0.9f,
				ARemoteViewLayoutRenderStyle.NAVIGATION_OVERLAY_TRANSPARENCY);

		// Assign view symbol
		Texture textureViewSymbol;
		AGLEventListener view = generalManager.getViewGLCanvasManager().getGLEventListener(
				remoteLevelElement.getContainedElementID());
		if (view instanceof GLHeatMap)
		{
			textureViewSymbol = iconTextureManager.getIconTexture(gl,
					EIconTextures.HEAT_MAP_SYMBOL);
		}
		else if (view instanceof GLParallelCoordinates)
		{
			textureViewSymbol = iconTextureManager.getIconTexture(gl,
					EIconTextures.PAR_COORDS_SYMBOL);
		}
		else if (view instanceof GLPathway)
		{
			textureViewSymbol = iconTextureManager.getIconTexture(gl,
					EIconTextures.PATHWAY_SYMBOL);
		}
		else if (view instanceof GLGlyph)
		{
			textureViewSymbol = iconTextureManager.getIconTexture(gl,
					EIconTextures.GLYPH_SYMBOL);
		}
		else if (view instanceof GLCell)
		{
			textureViewSymbol = iconTextureManager.getIconTexture(gl,
					EIconTextures.GLYPH_SYMBOL);
		}
		else
		{
			throw new IllegalStateException("Unknown view that has no symbol assigned.");
		}

		Texture textureMoveLeft = null;
		Texture textureMoveRight = null;
		Texture textureMoveOut = null;
		Texture textureMoveIn = null;

		TextureCoords texCoords = textureViewSymbol.getImageTexCoords();

		if (iNavigationMouseOverViewID_lock == iRemoteLevelElementID)
			tmpColor_lock.set(1, 0.3f, 0.3f,
					ARemoteViewLayoutRenderStyle.NAVIGATION_OVERLAY_TRANSPARENCY);

		if (layoutMode.equals(LayoutMode.JUKEBOX))
		{
			topWallPickingType = EPickingType.BUCKET_MOVE_RIGHT_ICON_SELECTION;
			bottomWallPickingType = EPickingType.BUCKET_MOVE_LEFT_ICON_SELECTION;
			leftWallPickingType = EPickingType.BUCKET_MOVE_OUT_ICON_SELECTION;
			rightWallPickingType = EPickingType.BUCKET_MOVE_IN_ICON_SELECTION;

			if (iNavigationMouseOverViewID_out == iRemoteLevelElementID)
				tmpColor_left.set(1, 0.3f, 0.3f,
						ARemoteViewLayoutRenderStyle.NAVIGATION_OVERLAY_TRANSPARENCY);
			else if (iNavigationMouseOverViewID_in == iRemoteLevelElementID)
				tmpColor_right.set(1, 0.3f, 0.3f,
						ARemoteViewLayoutRenderStyle.NAVIGATION_OVERLAY_TRANSPARENCY);
			else if (iNavigationMouseOverViewID_left == iRemoteLevelElementID)
				tmpColor_in.set(1, 0.3f, 0.3f,
						ARemoteViewLayoutRenderStyle.NAVIGATION_OVERLAY_TRANSPARENCY);
			else if (iNavigationMouseOverViewID_right == iRemoteLevelElementID)
				tmpColor_out.set(1, 0.3f, 0.3f,
						ARemoteViewLayoutRenderStyle.NAVIGATION_OVERLAY_TRANSPARENCY);

			textureMoveIn = iconTextureManager.getIconTexture(gl, EIconTextures.ARROW_LEFT);
			textureMoveOut = iconTextureManager.getIconTexture(gl, EIconTextures.ARROW_DOWN);
			textureMoveLeft = iconTextureManager.getIconTexture(gl, EIconTextures.ARROW_DOWN);
			textureMoveRight = iconTextureManager.getIconTexture(gl, EIconTextures.ARROW_LEFT);
		}
		else
		{
			if (stackLevel.getPositionIndexByElementID(remoteLevelElement) == 0) // top
			{
				topWallPickingType = EPickingType.BUCKET_MOVE_OUT_ICON_SELECTION;
				bottomWallPickingType = EPickingType.BUCKET_MOVE_IN_ICON_SELECTION;
				leftWallPickingType = EPickingType.BUCKET_MOVE_LEFT_ICON_SELECTION;
				rightWallPickingType = EPickingType.BUCKET_MOVE_RIGHT_ICON_SELECTION;

				if (iNavigationMouseOverViewID_out == iRemoteLevelElementID)
					tmpColor_out.set(1, 0.3f, 0.3f,
							ARemoteViewLayoutRenderStyle.NAVIGATION_OVERLAY_TRANSPARENCY);
				else if (iNavigationMouseOverViewID_in == iRemoteLevelElementID)
					tmpColor_in.set(1, 0.3f, 0.3f,
							ARemoteViewLayoutRenderStyle.NAVIGATION_OVERLAY_TRANSPARENCY);
				else if (iNavigationMouseOverViewID_left == iRemoteLevelElementID)
					tmpColor_left.set(1, 0.3f, 0.3f,
							ARemoteViewLayoutRenderStyle.NAVIGATION_OVERLAY_TRANSPARENCY);
				else if (iNavigationMouseOverViewID_right == iRemoteLevelElementID)
					tmpColor_right.set(1, 0.3f, 0.3f,
							ARemoteViewLayoutRenderStyle.NAVIGATION_OVERLAY_TRANSPARENCY);

				textureMoveIn = iconTextureManager
						.getIconTexture(gl, EIconTextures.ARROW_LEFT);
				textureMoveOut = iconTextureManager.getIconTexture(gl,
						EIconTextures.ARROW_DOWN);
				textureMoveLeft = iconTextureManager.getIconTexture(gl,
						EIconTextures.ARROW_DOWN);
				textureMoveRight = iconTextureManager.getIconTexture(gl,
						EIconTextures.ARROW_LEFT);
			}
			else if (stackLevel.getPositionIndexByElementID(remoteLevelElement) == 2) // bottom
			{
				topWallPickingType = EPickingType.BUCKET_MOVE_IN_ICON_SELECTION;
				bottomWallPickingType = EPickingType.BUCKET_MOVE_OUT_ICON_SELECTION;
				leftWallPickingType = EPickingType.BUCKET_MOVE_RIGHT_ICON_SELECTION;
				rightWallPickingType = EPickingType.BUCKET_MOVE_LEFT_ICON_SELECTION;

				if (iNavigationMouseOverViewID_out == iRemoteLevelElementID)
					tmpColor_in.set(1, 0.3f, 0.3f,
							ARemoteViewLayoutRenderStyle.NAVIGATION_OVERLAY_TRANSPARENCY);
				else if (iNavigationMouseOverViewID_in == iRemoteLevelElementID)
					tmpColor_out.set(1, 0.3f, 0.3f,
							ARemoteViewLayoutRenderStyle.NAVIGATION_OVERLAY_TRANSPARENCY);
				else if (iNavigationMouseOverViewID_left == iRemoteLevelElementID)
					tmpColor_right.set(1, 0.3f, 0.3f,
							ARemoteViewLayoutRenderStyle.NAVIGATION_OVERLAY_TRANSPARENCY);
				else if (iNavigationMouseOverViewID_right == iRemoteLevelElementID)
					tmpColor_left.set(1, 0.3f, 0.3f,
							ARemoteViewLayoutRenderStyle.NAVIGATION_OVERLAY_TRANSPARENCY);

				textureMoveIn = iconTextureManager
						.getIconTexture(gl, EIconTextures.ARROW_LEFT);
				textureMoveOut = iconTextureManager.getIconTexture(gl,
						EIconTextures.ARROW_DOWN);
				textureMoveLeft = iconTextureManager.getIconTexture(gl,
						EIconTextures.ARROW_DOWN);
				textureMoveRight = iconTextureManager.getIconTexture(gl,
						EIconTextures.ARROW_LEFT);
			}
			else if (stackLevel.getPositionIndexByElementID(remoteLevelElement) == 1) // left
			{
				topWallPickingType = EPickingType.BUCKET_MOVE_RIGHT_ICON_SELECTION;
				bottomWallPickingType = EPickingType.BUCKET_MOVE_LEFT_ICON_SELECTION;
				leftWallPickingType = EPickingType.BUCKET_MOVE_OUT_ICON_SELECTION;
				rightWallPickingType = EPickingType.BUCKET_MOVE_IN_ICON_SELECTION;

				if (iNavigationMouseOverViewID_out == iRemoteLevelElementID)
					tmpColor_left.set(1, 0.3f, 0.3f,
							ARemoteViewLayoutRenderStyle.NAVIGATION_OVERLAY_TRANSPARENCY);
				else if (iNavigationMouseOverViewID_in == iRemoteLevelElementID)
					tmpColor_right.set(1, 0.3f, 0.3f,
							ARemoteViewLayoutRenderStyle.NAVIGATION_OVERLAY_TRANSPARENCY);
				else if (iNavigationMouseOverViewID_left == iRemoteLevelElementID)
					tmpColor_in.set(1, 0.3f, 0.3f,
							ARemoteViewLayoutRenderStyle.NAVIGATION_OVERLAY_TRANSPARENCY);
				else if (iNavigationMouseOverViewID_right == iRemoteLevelElementID)
					tmpColor_out.set(1, 0.3f, 0.3f,
							ARemoteViewLayoutRenderStyle.NAVIGATION_OVERLAY_TRANSPARENCY);

				textureMoveIn = iconTextureManager
						.getIconTexture(gl, EIconTextures.ARROW_LEFT);
				textureMoveOut = iconTextureManager.getIconTexture(gl,
						EIconTextures.ARROW_DOWN);
				textureMoveLeft = iconTextureManager.getIconTexture(gl,
						EIconTextures.ARROW_DOWN);
				textureMoveRight = iconTextureManager.getIconTexture(gl,
						EIconTextures.ARROW_LEFT);
			}
			else if (stackLevel.getPositionIndexByElementID(remoteLevelElement) == 3) // right
			{
				topWallPickingType = EPickingType.BUCKET_MOVE_LEFT_ICON_SELECTION;
				bottomWallPickingType = EPickingType.BUCKET_MOVE_RIGHT_ICON_SELECTION;
				leftWallPickingType = EPickingType.BUCKET_MOVE_IN_ICON_SELECTION;
				rightWallPickingType = EPickingType.BUCKET_MOVE_OUT_ICON_SELECTION;

				if (iNavigationMouseOverViewID_out == iRemoteLevelElementID)
					tmpColor_right.set(1, 0.3f, 0.3f,
							ARemoteViewLayoutRenderStyle.NAVIGATION_OVERLAY_TRANSPARENCY);
				else if (iNavigationMouseOverViewID_in == iRemoteLevelElementID)
					tmpColor_left.set(1, 0.3f, 0.3f,
							ARemoteViewLayoutRenderStyle.NAVIGATION_OVERLAY_TRANSPARENCY);
				else if (iNavigationMouseOverViewID_left == iRemoteLevelElementID)
					tmpColor_out.set(1, 0.3f, 0.3f,
							ARemoteViewLayoutRenderStyle.NAVIGATION_OVERLAY_TRANSPARENCY);
				else if (iNavigationMouseOverViewID_right == iRemoteLevelElementID)
					tmpColor_in.set(1, 0.3f, 0.3f,
							ARemoteViewLayoutRenderStyle.NAVIGATION_OVERLAY_TRANSPARENCY);

				textureMoveIn = iconTextureManager
						.getIconTexture(gl, EIconTextures.ARROW_LEFT);
				textureMoveOut = iconTextureManager.getIconTexture(gl,
						EIconTextures.ARROW_DOWN);
				textureMoveLeft = iconTextureManager.getIconTexture(gl,
						EIconTextures.ARROW_DOWN);
				textureMoveRight = iconTextureManager.getIconTexture(gl,
						EIconTextures.ARROW_LEFT);
			}
			// else if
			// (underInteractionLayer.getPositionIndexByElementID(iViewID) == 0)
			// // center
			// {
			// topWallPickingType = EPickingType.BUCKET_MOVE_OUT_ICON_SELECTION;
			// bottomWallPickingType =
			// EPickingType.BUCKET_MOVE_IN_ICON_SELECTION;
			// leftWallPickingType =
			// EPickingType.BUCKET_MOVE_LEFT_ICON_SELECTION;
			// rightWallPickingType =
			// EPickingType.BUCKET_MOVE_RIGHT_ICON_SELECTION;
			//
			// if (iNavigationMouseOverViewID_out == iViewID)
			// tmpColor_out.set(1, 0.3f, 0.3f, 0.9f);
			// else if (iNavigationMouseOverViewID_in == iViewID)
			// tmpColor_in.set(1, 0.3f, 0.3f, 0.9f);
			// else if (iNavigationMouseOverViewID_left == iViewID)
			// tmpColor_left.set(1, 0.3f, 0.3f, 0.9f);
			// else if (iNavigationMouseOverViewID_right == iViewID)
			// tmpColor_right.set(1, 0.3f, 0.3f, 0.9f);
			//
			// textureMoveIn =
			// iconTextureManager.getIconTexture(EIconTextures.ARROW_LEFT);
			// textureMoveOut =
			// iconTextureManager.getIconTexture(EIconTextures.ARROW_DOWN);
			// textureMoveLeft = iconTextureManager
			// .getIconTexture(EIconTextures.ARROW_DOWN);
			// textureMoveRight = iconTextureManager
			// .getIconTexture(EIconTextures.ARROW_LEFT);
			// }
		}
		// else if (underInteractionLayer.containsElement(iViewID))
		// {
		// topWallPickingType = EPickingType.BUCKET_MOVE_OUT_ICON_SELECTION;
		// bottomWallPickingType =
		// EPickingType.BUCKET_MOVE_RIGHT_ICON_SELECTION;
		// leftWallPickingType = EPickingType.BUCKET_MOVE_IN_ICON_SELECTION;
		// rightWallPickingType = EPickingType.BUCKET_MOVE_OUT_ICON_SELECTION;
		// }

		gl.glLineWidth(1);

		float fNavigationZValue = 0f;

		// CENTER - NAVIGATION: VIEW IDENTIFICATION ICON
		// gl.glPushName(pickingManager.getPickingID(iUniqueID,
		// EPickingType.BUCKET_LOCK_ICON_SELECTION, iViewID));

		gl.glColor4f(0.5f, 0.5f, 0.5f, 1);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(2.66f, 2.66f, fNavigationZValue);
		gl.glVertex3f(2.66f, 5.33f, fNavigationZValue);
		gl.glVertex3f(5.33f, 5.33f, fNavigationZValue);
		gl.glVertex3f(5.33f, 2.66f, fNavigationZValue);
		gl.glEnd();

		gl.glColor4f(tmpColor_lock.x(), tmpColor_lock.y(), tmpColor_lock.z(),
				ARemoteViewLayoutRenderStyle.NAVIGATION_OVERLAY_TRANSPARENCY);

		// gl.glBegin(GL.GL_POLYGON);
		// gl.glVertex3f(2.66f, 2.66f, 0.02f);
		// gl.glVertex3f(2.66f, 5.33f, 0.02f);
		// gl.glVertex3f(5.33f, 5.33f, 0.02f);
		// gl.glVertex3f(5.33f, 2.66f, 0.02f);
		// gl.glEnd();

		textureViewSymbol.enable();
		textureViewSymbol.bind();

		gl.glBegin(GL.GL_POLYGON);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(2.66f, 2.66f, fNavigationZValue);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(2.66f, 5.33f, fNavigationZValue);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(5.33f, 5.33f, fNavigationZValue);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(5.33f, 2.66f, fNavigationZValue);
		gl.glEnd();

		textureViewSymbol.disable();

		// gl.glPopName();

		// BOTTOM - NAVIGATION: MOVE IN
		gl.glPushName(pickingManager.getPickingID(iUniqueID, bottomWallPickingType,
				iRemoteLevelElementID));

		gl.glColor4f(0.5f, 0.5f, 0.5f, 1);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(0, 0, fNavigationZValue);
		gl.glVertex3f(2.66f, 2.66f, fNavigationZValue);
		gl.glVertex3f(5.33f, 2.66f, fNavigationZValue);
		gl.glVertex3f(8, 0, fNavigationZValue);
		gl.glEnd();

		gl.glColor4f(tmpColor_in.x(), tmpColor_in.y(), tmpColor_in.z(),
				ARemoteViewLayoutRenderStyle.NAVIGATION_OVERLAY_TRANSPARENCY);

		// gl.glBegin(GL.GL_POLYGON);
		// gl.glVertex3f(0.05f, 0.05f, 0.02f);
		// gl.glVertex3f(2.66f, 2.66f, 0.02f);
		// gl.glVertex3f(5.33f, 2.66f, 0.02f);
		// gl.glVertex3f(7.95f, 0.02f, 0.02f);
		// gl.glEnd();

		textureMoveIn.enable();
		textureMoveIn.bind();
		// texCoords = textureMoveIn.getImageTexCoords();
		// gl.glColor4f(1,0.3f,0.3f,0.9f);
		gl.glBegin(GL.GL_POLYGON);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(2.66f, 0.05f, fNavigationZValue);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(2.66f, 2.66f, fNavigationZValue);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(5.33f, 2.66f, fNavigationZValue);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(5.33f, 0.05f, fNavigationZValue);
		gl.glEnd();

		textureMoveIn.disable();

		gl.glPopName();

		// RIGHT - NAVIGATION: MOVE RIGHT
		gl.glPushName(pickingManager.getPickingID(iUniqueID, rightWallPickingType,
				iRemoteLevelElementID));

		gl.glColor4f(0.5f, 0.5f, 0.5f, 1);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(8, 0, fNavigationZValue);
		gl.glVertex3f(5.33f, 2.66f, fNavigationZValue);
		gl.glVertex3f(5.33f, 5.33f, fNavigationZValue);
		gl.glVertex3f(8, 8, fNavigationZValue);
		gl.glEnd();

		gl.glColor4f(tmpColor_right.x(), tmpColor_right.y(), tmpColor_right.z(),
				ARemoteViewLayoutRenderStyle.NAVIGATION_OVERLAY_TRANSPARENCY);

		// gl.glBegin(GL.GL_POLYGON);
		// gl.glVertex3f(7.95f, 0.05f, 0.02f);
		// gl.glVertex3f(5.33f, 2.66f, 0.02f);
		// gl.glVertex3f(5.33f, 5.33f, 0.02f);
		// gl.glVertex3f(7.95f, 7.95f, 0.02f);
		// gl.glEnd();

		textureMoveRight.enable();
		textureMoveRight.bind();

		// gl.glColor4f(0,1,0,1);
		gl.glBegin(GL.GL_POLYGON);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(7.95f, 2.66f, fNavigationZValue);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(5.33f, 2.66f, fNavigationZValue);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(5.33f, 5.33f, fNavigationZValue);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(7.95f, 5.33f, fNavigationZValue);
		gl.glEnd();

		textureMoveRight.disable();

		gl.glPopName();

		// LEFT - NAVIGATION: MOVE LEFT
		gl.glPushName(pickingManager.getPickingID(iUniqueID, leftWallPickingType,
				iRemoteLevelElementID));

		gl.glColor4f(0.5f, 0.5f, 0.5f, 1);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(0, 0, fNavigationZValue);
		gl.glVertex3f(0, 8, fNavigationZValue);
		gl.glVertex3f(2.66f, 5.33f, fNavigationZValue);
		gl.glVertex3f(2.66f, 2.66f, fNavigationZValue);
		gl.glEnd();

		gl.glColor4f(tmpColor_left.x(), tmpColor_left.y(), tmpColor_left.z(),
				ARemoteViewLayoutRenderStyle.NAVIGATION_OVERLAY_TRANSPARENCY);

		// gl.glBegin(GL.GL_POLYGON);
		// gl.glVertex3f(0.05f, 0.05f, fNavigationZValue);
		// gl.glVertex3f(0.05f, 7.95f, fNavigationZValue);
		// gl.glVertex3f(2.66f, 5.33f, fNavigationZValue);
		// gl.glVertex3f(2.66f, 2.66f, fNavigationZValue);
		// gl.glEnd();

		textureMoveLeft.enable();
		textureMoveLeft.bind();

		// gl.glColor4f(0,1,0,1);
		gl.glBegin(GL.GL_POLYGON);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(0.05f, 2.66f, fNavigationZValue);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(0.05f, 5.33f, fNavigationZValue);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(2.66f, 5.33f, fNavigationZValue);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(2.66f, 2.66f, fNavigationZValue);
		gl.glEnd();

		textureMoveLeft.disable();

		gl.glPopName();

		// TOP - NAVIGATION: MOVE OUT
		gl.glPushName(pickingManager.getPickingID(iUniqueID, topWallPickingType,
				iRemoteLevelElementID));

		gl.glColor4f(0.5f, 0.5f, 0.5f, 1);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(0, 8, fNavigationZValue);
		gl.glVertex3f(8, 8, fNavigationZValue);
		gl.glVertex3f(5.33f, 5.33f, fNavigationZValue);
		gl.glVertex3f(2.66f, 5.33f, fNavigationZValue);
		gl.glEnd();

		gl.glColor4f(tmpColor_out.x(), tmpColor_out.y(), tmpColor_out.z(),
				ARemoteViewLayoutRenderStyle.NAVIGATION_OVERLAY_TRANSPARENCY);

		// gl.glBegin(GL.GL_POLYGON);
		// gl.glVertex3f(0.05f, 7.95f, 0.02f);
		// gl.glVertex3f(7.95f, 7.95f, 0.02f);
		// gl.glVertex3f(5.33f, 5.33f, 0.02f);
		// gl.glVertex3f(2.66f, 5.33f, 0.02f);
		// gl.glEnd();

		textureMoveOut.enable();
		textureMoveOut.bind();

		// gl.glColor4f(0,1,0,1);
		gl.glBegin(GL.GL_POLYGON);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(2.66f, 7.95f, fNavigationZValue);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(5.33f, 7.95f, fNavigationZValue);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(5.33f, 5.33f, fNavigationZValue);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(2.66f, 5.33f, fNavigationZValue);
		gl.glEnd();

		textureMoveOut.disable();

		gl.glPopName();
	}

	private void renderPoolSelection(final GL gl, float fXOrigin, float fYOrigin,
			float fWidth, float fHeight, RemoteLevelElement element)
	{
		float fPanelSideWidth = 11f;

		gl.glColor3f(0.25f, 0.25f, 0.25f);
		gl.glBegin(GL.GL_POLYGON);

		gl.glVertex3f(fXOrigin + 1.65f / fAspectRatio + fPanelSideWidth, fYOrigin - fHeight
				/ 2f + fHeight, 0f);
		gl.glVertex3f(fXOrigin + 1.65f / fAspectRatio + fPanelSideWidth + fWidth, fYOrigin
				- fHeight / 2f + fHeight, 0f);
		gl.glVertex3f(fXOrigin + 1.65f / fAspectRatio + fPanelSideWidth + fWidth, fYOrigin
				- fHeight / 2f, 0f);
		gl.glVertex3f(fXOrigin + 1.65f / fAspectRatio + fPanelSideWidth, fYOrigin - fHeight
				/ 2f, 0f);

		gl.glEnd();

		Texture tempTexture = iconTextureManager.getIconTexture(gl,
				EIconTextures.POOL_VIEW_BACKGROUND_SELECTION);
		tempTexture.enable();
		tempTexture.bind();

		TextureCoords texCoords = tempTexture.getImageTexCoords();

		gl.glColor4f(1, 1, 1, 0.75f);

		gl.glBegin(GL.GL_POLYGON);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(fXOrigin + (2) / fAspectRatio + fPanelSideWidth, fYOrigin - fHeight,
				-0.01f);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(fXOrigin + (2) / fAspectRatio + fPanelSideWidth, fYOrigin + fHeight,
				-0.01f);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(fXOrigin + 2f / fAspectRatio, fYOrigin + fHeight, -0.01f);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(fXOrigin + 2f / fAspectRatio, fYOrigin - fHeight, -0.01f);
		gl.glEnd();

		tempTexture.disable();

		gl.glPopName();
		gl.glPopName();

		int fHandleScaleFactor = 18;
		gl.glTranslatef(fXOrigin + 2.5f / fAspectRatio,
				fYOrigin - fHeight / 2f + fHeight - 1f, 1.8f);
		gl.glScalef(fHandleScaleFactor, fHandleScaleFactor, fHandleScaleFactor);
		renderSingleHandle(gl, element.getID(), EPickingType.BUCKET_DRAG_ICON_SELECTION,
				EIconTextures.POOL_DRAG_VIEW, 0.1f, 0.1f);
		gl.glTranslatef(0, -0.2f, 0);
		renderSingleHandle(gl, element.getID(), EPickingType.BUCKET_REMOVE_ICON_SELECTION,
				EIconTextures.POOL_REMOVE_VIEW, 0.1f, 0.1f);
		gl.glTranslatef(0, 0.2f, 0);
		gl.glScalef(1f / fHandleScaleFactor, 1f / fHandleScaleFactor, 1f / fHandleScaleFactor);
		gl.glTranslatef(-fXOrigin - 2.5f / fAspectRatio, -fYOrigin + fHeight / 2f - fHeight
				+ 1f, -1.8f);

		// gl.glColor3f(0.25f, 0.25f, 0.25f);
		// gl.glBegin(GL.GL_POLYGON);
		// gl.glVertex3f(fXOrigin + 3f, fYOrigin - fHeight / 2f + fHeight -
		// 2.5f, 0f);
		// gl.glVertex3f(fXOrigin + 5.1f, fYOrigin - fHeight / 2f + fHeight -
		// 2.5f, 0f);
		// gl.glVertex3f(fXOrigin + 5.1f, fYOrigin- fHeight / 2f + 1.5f, 0f);
		// gl.glVertex3f(fXOrigin + 3f, fYOrigin- fHeight / 2f + 1.5f , 0f);
		// gl.glEnd();

		gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.REMOTE_LEVEL_ELEMENT, element.getID()));
		gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.VIEW_SELECTION,
				element.getID()));
	}

	private void doSlerpActions(final GL gl)
	{
		if (arSlerpActions.isEmpty())
			return;

		SlerpAction tmpSlerpAction = arSlerpActions.get(0);

		if (iSlerpFactor == 0)
		{
			tmpSlerpAction.start();

			// System.out.println("Start slerp action " +tmpSlerpAction);
		}

		if (iSlerpFactor < SLERP_RANGE)
		{
			// Makes animation rendering speed independent
			iSlerpFactor += SLERP_SPEED * time.deltaT();

			if (iSlerpFactor > SLERP_RANGE)
				iSlerpFactor = SLERP_RANGE;
		}

		slerpView(gl, tmpSlerpAction);
	}

	private void slerpView(final GL gl, SlerpAction slerpAction)
	{
		int iViewID = slerpAction.getElementId();

		SlerpMod slerpMod = new SlerpMod();

		if ((iSlerpFactor == 0))
		{
			slerpMod.playSlerpSound();
		}

		Transform transform = slerpMod.interpolate(slerpAction.getOriginRemoteLevelElement()
				.getTransform(),
				slerpAction.getDestinationRemoteLevelElement().getTransform(),
				(float) iSlerpFactor / SLERP_RANGE);

		gl.glPushMatrix();

		slerpMod.applySlerp(gl, transform);

		(generalManager.getViewGLCanvasManager().getGLEventListener(iViewID))
				.displayRemote(gl);

		gl.glPopMatrix();

		// Check if slerp action is finished
		if (iSlerpFactor >= SLERP_RANGE)
		{
			arSlerpActions.remove(slerpAction);

			iSlerpFactor = 0;

			slerpAction.finished();

			RemoteLevelElement destinationElement = slerpAction
					.getDestinationRemoteLevelElement();

			updateViewDetailLevels(destinationElement);
		}

		// After last slerp action is done the line connections are turned on
		// again
		if (arSlerpActions.isEmpty())
		{
			if (glConnectionLineRenderer != null)
				glConnectionLineRenderer.enableRendering(true);

			generalManager.getViewGLCanvasManager().getInfoAreaManager().enable(
					!bEnableNavigationOverlay);
		}
	}

	private void updateViewDetailLevels(RemoteLevelElement element)
	{
		RemoteLevel destinationLevel = element.getRemoteLevel();

		if (element.getContainedElementID() == -1)
			return;

		AGLEventListener glActiveSubView = GeneralManager.get().getViewGLCanvasManager()
				.getGLEventListener(element.getContainedElementID());

		glActiveSubView.setRemoteLevelElement(element);

		// Update detail level of moved view when slerp action is finished;
		if (destinationLevel == focusLevel)
		{
			if (bucketMouseWheelListener.isZoomedIn()
					|| layoutRenderStyle instanceof ListLayoutRenderStyle)
				glActiveSubView.setDetailLevel(EDetailLevel.HIGH);
			else
				glActiveSubView.setDetailLevel(EDetailLevel.MEDIUM);

			// if (glActiveSubView instanceof GLPathway)
			// {
			// ((GLPathway) glActiveSubView).enableTitleRendering(true);
			// ((GLPathway) glActiveSubView).setAlignment(SWT.CENTER,
			// SWT.BOTTOM);
			// }

			// generalManager.getGUIBridge().setActiveGLSubView(this,
			// glActiveSubView);
		}
		else if (destinationLevel == stackLevel)
		{
			glActiveSubView.setDetailLevel(EDetailLevel.LOW);

			// if (glActiveSubView instanceof GLPathway)
			// {
			// ((GLPathway) glActiveSubView).enableTitleRendering(true);
			//
			// int iStackPos = stackLevel.getPositionIndexByElementID(element);
			// switch (iStackPos)
			// {
			// case 0:
			// ((GLPathway) glActiveSubView).setAlignment(SWT.CENTER, SWT.TOP);
			// break;
			// case 1:
			// ((GLPathway) glActiveSubView).setAlignment(SWT.LEFT, SWT.BOTTOM);
			// break;
			// case 2:
			// ((GLPathway) glActiveSubView).setAlignment(SWT.CENTER,
			// SWT.BOTTOM);
			// break;
			// case 3:
			// ((GLPathway) glActiveSubView).setAlignment(SWT.RIGHT,
			// SWT.BOTTOM);
			// break;
			// default:
			// break;
			// }
			// }
		}
		else if (destinationLevel == poolLevel || destinationLevel == selectionLevel)
		{
			glActiveSubView.setDetailLevel(EDetailLevel.VERY_LOW);

			// if (glActiveSubView instanceof GLPathway)
			// {
			// ((GLPathway) glActiveSubView).enableTitleRendering(false);
			// }
		}

		compactPoolLevel();
	}

	private void loadViewToFocusLevel(final int iRemoteLevelElementID)
	{
		RemoteLevelElement element = RemoteElementManager.get().getItem(iRemoteLevelElementID);

		// Check if other slerp action is currently running
		if (iSlerpFactor > 0 && iSlerpFactor < SLERP_RANGE)
			return;

		arSlerpActions.clear();

		int iViewID = element.getContainedElementID();

		if (iViewID == -1)
			return;

		// Only broadcast elements if view is moved from pool to bucket
		if (poolLevel.containsElement(element))
		{
			generalManager.getViewGLCanvasManager().getGLEventListener(iViewID)
					.broadcastElements(EVAOperation.APPEND_UNIQUE);
		}

		// if (layoutRenderStyle instanceof ListLayoutRenderStyle)
		// {
		// // Slerp selected view to under interaction transition position
		// SlerpAction slerpActionTransition = new
		// SlerpAction(iRemoteLevelElementID, poolLevel,
		// transitionLevel);
		// arSlerpActions.add(slerpActionTransition);
		//
		// // Check if focus has a free spot
		// if (focusLevel.getElementByPositionIndex(0).getContainedElementID()
		// != -1)
		// {
		// // Slerp under interaction view to free spot in pool
		// SlerpAction reverseSlerpAction = new SlerpAction(focusLevel
		// .getElementIDByPositionIndex(0), focusLevel, poolLevel);
		// arSlerpActions.add(reverseSlerpAction);
		// }
		//
		// // Slerp selected view from transition position to under interaction
		// // position
		// SlerpAction slerpAction = new SlerpAction(iViewID, transitionLevel,
		// focusLevel);
		// arSlerpActions.add(slerpAction);
		// }
		// else
		{
			// Check if view is already loaded in the stack layer
			if (stackLevel.containsElement(element))
			{
				// Slerp selected view to transition position
				SlerpAction slerpActionTransition = new SlerpAction(element, transitionLevel
						.getElementByPositionIndex(0));
				arSlerpActions.add(slerpActionTransition);

				// Check if focus level is free
				if (!focusLevel.hasFreePosition())
				{
					// Slerp focus view to free spot in stack
					SlerpAction reverseSlerpAction = new SlerpAction(focusLevel
							.getElementByPositionIndex(0).getContainedElementID(), focusLevel
							.getElementByPositionIndex(0), element);
					arSlerpActions.add(reverseSlerpAction);
				}

				// Slerp selected view from transition position to focus
				// position
				SlerpAction slerpAction = new SlerpAction(element.getContainedElementID(),
						transitionLevel.getElementByPositionIndex(0), focusLevel
								.getElementByPositionIndex(0));
				arSlerpActions.add(slerpAction);
			}
			else
			{
				// Slerp selected view to transition position
				SlerpAction slerpActionTransition = new SlerpAction(element, transitionLevel
						.getElementByPositionIndex(0));
				arSlerpActions.add(slerpActionTransition);

				RemoteLevelElement freeStackElement = null;
				if (!stackLevel.hasFreePosition())
				{
					int iReplacePosition = 1;

					// // Determine non locked stack position for view movement
					// to pool
					// for (int iTmpReplacePosition = 0; iTmpReplacePosition <
					// stackLevel.getCapacity(); iTmpReplacePosition++)
					// {
					// if
					// (stackLevel.getElementByPositionIndex(iTmpReplacePosition).isLocked())
					// continue;
					//						
					// iReplacePosition = iTmpReplacePosition + 1; // +1 to
					// start with left view for outsourcing
					//						
					// if (iReplacePosition == 4)
					// iReplacePosition = 0;
					//						
					// break;
					// }
					//					
					// if (iReplacePosition == -1)
					// throw new
					// IllegalStateException("All views in stack are locked!");

					freeStackElement = stackLevel.getElementByPositionIndex(iReplacePosition);

					// Slerp view from stack to pool
					SlerpAction reverseSlerpAction = new SlerpAction(freeStackElement,
							poolLevel.getNextFree());
					arSlerpActions.add(reverseSlerpAction);

					// Unregister all elements of the view that is moved out
					generalManager.getViewGLCanvasManager().getGLEventListener(
							freeStackElement.getContainedElementID()).broadcastElements(
							EVAOperation.REMOVE_ELEMENT);
				}
				else
				{
					freeStackElement = stackLevel.getNextFree();
				}

				if (!focusLevel.hasFreePosition())
				{
					// Slerp focus view to free spot in stack
					SlerpAction reverseSlerpAction2 = new SlerpAction(focusLevel
							.getElementByPositionIndex(0), freeStackElement);
					arSlerpActions.add(reverseSlerpAction2);
				}

				// Slerp selected view from transition position to focus
				// position
				SlerpAction slerpAction = new SlerpAction(iViewID, transitionLevel
						.getElementByPositionIndex(0), focusLevel.getElementByPositionIndex(0));
				arSlerpActions.add(slerpAction);
			}
		}

		iSlerpFactor = 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handleExternalEvent(IUniqueObject eventTrigger, IEventContainer eventContainer)
	{

		switch (eventContainer.getEventType())
		{
			// pathway loading based on gene id
			case LOAD_PATHWAY_BY_GENE:

				// take care here, if we ever use non integer ids this has to be
				// cast to raw type first to determine the actual id data types
				IDListEventContainer<Integer> idContainer = (IDListEventContainer<Integer>) eventContainer;
				if (idContainer.getIDType() == EIDType.REFSEQ_MRNA_INT)
				{
					int iGraphItemID = 0;
					Integer iDavidID = -1;
					ArrayList<ICaleydoGraphItem> alPathwayVertexGraphItem = new ArrayList<ICaleydoGraphItem>();

					for (Integer iRefSeqID : idContainer.getIDs())
					{
						iDavidID = idMappingManager.getID(EMappingType.REFSEQ_MRNA_INT_2_DAVID, iRefSeqID);
						
						if (iDavidID == null || iDavidID == -1)
							throw new IllegalStateException("Cannot resolve RefSeq ID to David ID.");
						
						iGraphItemID = generalManager.getPathwayItemManager()
								.getPathwayVertexGraphItemIdByDavidId(iDavidID);

						if (iGraphItemID == -1)
							continue;

						PathwayVertexGraphItem tmpPathwayVertexGraphItem = ((PathwayVertexGraphItem) generalManager
								.getPathwayItemManager().getItem(iGraphItemID));

						if (tmpPathwayVertexGraphItem == null)
							continue;

						alPathwayVertexGraphItem.add(tmpPathwayVertexGraphItem);
					}

					if (!alPathwayVertexGraphItem.isEmpty())
					{
						loadDependentPathways(alPathwayVertexGraphItem);
					}
				}
				else
				{
					throw new IllegalStateException("Not Implemented");
				}
				break;
			// Handle incoming pathways
			case LOAD_PATHWAY_BY_PATHWAY_ID:
				IDListEventContainer<Integer> pathwayIDContainer = (IDListEventContainer<Integer>) eventContainer;

				for (Integer iPathwayID : pathwayIDContainer.getIDs())
				{
					addPathwayView(iPathwayID);
				}

				break;
			case SELECTION_UPDATE:
				lastSelectionDelta = ((DeltaEventContainer<ISelectionDelta>) eventContainer)
						.getSelectionDelta();
		}

		bUpdateOffScreenTextures = true;
	}

	/**
	 * Add pathway view. Also used when serialized pathways are loaded.
	 * 
	 * @param iPathwayIDToLoad
	 */
	public synchronized void addPathwayView(final int iPathwayIDToLoad)
	{
		iAlUninitializedPathwayIDs.add(iPathwayIDToLoad);
	}

	public synchronized void loadDependentPathways(final List<ICaleydoGraphItem> alVertex)
	{
		// Remove pathways from stacked layer view
		// poolLayer.removeAllElements();

		Iterator<ICaleydoGraphItem> iterPathwayGraphItem = alVertex.iterator();
		Iterator<IGraphItem> iterIdenticalPathwayGraphItemRep = null;

		IGraphItem pathwayGraphItem;
		int iPathwayID = 0;

		while (iterPathwayGraphItem.hasNext())
		{
			pathwayGraphItem = iterPathwayGraphItem.next();

			if (pathwayGraphItem == null)
			{
				// generalManager.logMsg(
				// this.getClass().getSimpleName() + " (" + iUniqueID
				// + "): pathway graph item is null.  ",
				// LoggerType.VERBOSE);
				continue;
			}

			iterIdenticalPathwayGraphItemRep = pathwayGraphItem.getAllItemsByProp(
					EGraphItemProperty.ALIAS_CHILD).iterator();

			while (iterIdenticalPathwayGraphItemRep.hasNext())
			{
				iPathwayID = ((PathwayGraph) iterIdenticalPathwayGraphItemRep.next()
						.getAllGraphByType(EGraphItemHierarchy.GRAPH_PARENT).toArray()[0])
						.getId();

				// Only add pathway if it is not loaded yet
				if (!generalManager.getPathwayManager().isPathwayVisible(iPathwayID))
					iAlUninitializedPathwayIDs.add(iPathwayID);
			}
		}

		if (iAlUninitializedPathwayIDs.isEmpty())
			return;

		// Disable picking until pathways are loaded
		pickingManager.enablePicking(false);

		// Zoom out of the bucket when loading pathways
		if (bucketMouseWheelListener.isZoomedIn())
			bucketMouseWheelListener.triggerZoom(false);

		// Turn on busy mode
		for (AGLEventListener tmpGLEventListener : GeneralManager.get()
				.getViewGLCanvasManager().getAllGLEventListeners())
		{
			if (!tmpGLEventListener.isRenderedRemote())
				tmpGLEventListener.enableBusyMode(true);
		}

		// iSlerpFactor = 0;
	}

	@Override
	protected void handleEvents(EPickingType pickingType, EPickingMode pickingMode,
			int iExternalID, Pick pick)
	{
		switch (pickingType)
		{
			case BUCKET_DRAG_ICON_SELECTION:

				switch (pickingMode)
				{
					case CLICKED:

						if (!dragAndDrop.isDragActionRunning())
						{
							// System.out.println("Start drag!");
							dragAndDrop.startDragAction(iExternalID);
						}

						iMouseOverObjectID = iExternalID;

						compactPoolLevel();

						break;
				}

				pickingManager.flushHits(iUniqueID, EPickingType.BUCKET_DRAG_ICON_SELECTION);
				pickingManager.flushHits(iUniqueID, EPickingType.REMOTE_LEVEL_ELEMENT);

				break;

			case BUCKET_REMOVE_ICON_SELECTION:

				switch (pickingMode)
				{
					case CLICKED:

						RemoteLevelElement element = RemoteElementManager.get().getItem(
								iExternalID);

						AGLEventListener glEventListener = ((AGLEventListener) generalManager
								.getViewGLCanvasManager().getGLEventListener(
										element.getContainedElementID()));

						// Unregister all elements of the view that is removed
						glEventListener.broadcastElements(EVAOperation.REMOVE_ELEMENT);

						removeView(glEventListener);

						element.setContainedElementID(-1);

						if (element.getRemoteLevel() == poolLevel)
							compactPoolLevel();

						break;
				}

				pickingManager.flushHits(iUniqueID, EPickingType.BUCKET_REMOVE_ICON_SELECTION);

				break;

			case BUCKET_LOCK_ICON_SELECTION:

				switch (pickingMode)
				{
					case CLICKED:

						RemoteLevelElement element = RemoteElementManager.get().getItem(
								iExternalID);

						// Toggle lock flag
						element.lock(!element.isLocked());

						break;
				}

				pickingManager.flushHits(iUniqueID, EPickingType.BUCKET_LOCK_ICON_SELECTION);

				break;

			case REMOTE_LEVEL_ELEMENT:
				switch (pickingMode)
				{
					case MOUSE_OVER:
					case DRAGGED:
						iMouseOverObjectID = iExternalID;
						break;
					case CLICKED:

						// Do not handle click if element is dragged
						if (dragAndDrop.isDragActionRunning())
							break;

						// Check if view is contained in pool level
						for (RemoteLevelElement element : poolLevel.getAllElements())
						{
							if (element.getID() == iExternalID)
							{
								loadViewToFocusLevel(iExternalID);
								break;
							}
						}
						break;
				}

				pickingManager.flushHits(iUniqueID, EPickingType.REMOTE_LEVEL_ELEMENT);

				break;

			case VIEW_SELECTION:
				switch (pickingMode)
				{
					case MOUSE_OVER:

						// generalManager.getViewGLCanvasManager().getInfoAreaManager()
						// .setDataAboutView(iExternalID);

						// Prevent update flood when moving mouse over view
						if (iActiveViewID == iExternalID)
							break;

						iActiveViewID = iExternalID;

						setDisplayListDirty();

						// TODO
						// generalManager.getEventPublisher().triggerEvent(
						// EMediatorType.VIEW_SELECTION,
						// generalManager.getViewGLCanvasManager().getGLEventListener(
						// iExternalID), );

						break;

					case CLICKED:

						// generalManager.getViewGLCanvasManager().getInfoAreaManager()
						// .setDataAboutView(iExternalID);

						break;
				}

				pickingManager.flushHits(iUniqueID, EPickingType.VIEW_SELECTION);

				break;

			// case BUCKET_LOCK_ICON_SELECTION:
			// switch (pickingMode)
			// {
			// case CLICKED:
			//
			// break;
			//
			// case MOUSE_OVER:
			//
			// iNavigationMouseOverViewID_lock = iExternalID;
			// iNavigationMouseOverViewID_left = -1;
			// iNavigationMouseOverViewID_right = -1;
			// iNavigationMouseOverViewID_out = -1;
			// iNavigationMouseOverViewID_in = -1;
			//
			// break;
			// }
			//
			// pickingManager.flushHits(iUniqueID,
			// EPickingType.BUCKET_LOCK_ICON_SELECTION);
			//
			// break;

			case BUCKET_MOVE_IN_ICON_SELECTION:
				switch (pickingMode)
				{
					case CLICKED:
						loadViewToFocusLevel(iExternalID);
						bEnableNavigationOverlay = false;
						// glConnectionLineRenderer.enableRendering(true);
						break;

					case MOUSE_OVER:

						iNavigationMouseOverViewID_left = -1;
						iNavigationMouseOverViewID_right = -1;
						iNavigationMouseOverViewID_out = -1;
						iNavigationMouseOverViewID_in = iExternalID;
						iNavigationMouseOverViewID_lock = -1;

						break;
				}

				pickingManager
						.flushHits(iUniqueID, EPickingType.BUCKET_MOVE_IN_ICON_SELECTION);

				break;

			case BUCKET_MOVE_OUT_ICON_SELECTION:
				switch (pickingMode)
				{
					case CLICKED:

						// Check if other slerp action is currently running
						if (iSlerpFactor > 0 && iSlerpFactor < SLERP_RANGE)
							break;

						// glConnectionLineRenderer.enableRendering(true);

						arSlerpActions.clear();

						RemoteLevelElement element = RemoteElementManager.get().getItem(
								iExternalID);
						SlerpAction slerpActionTransition = new SlerpAction(element, poolLevel
								.getNextFree());
						arSlerpActions.add(slerpActionTransition);

						bEnableNavigationOverlay = false;

						// Unregister all elements of the view that is moved out
						generalManager.getViewGLCanvasManager().getGLEventListener(
								element.getContainedElementID()).broadcastElements(
								EVAOperation.REMOVE_ELEMENT);

						break;

					case MOUSE_OVER:

						iNavigationMouseOverViewID_left = -1;
						iNavigationMouseOverViewID_right = -1;
						iNavigationMouseOverViewID_out = iExternalID;
						iNavigationMouseOverViewID_in = -1;
						iNavigationMouseOverViewID_lock = -1;

						break;
				}

				pickingManager.flushHits(iUniqueID,
						EPickingType.BUCKET_MOVE_OUT_ICON_SELECTION);

				break;

			case BUCKET_MOVE_LEFT_ICON_SELECTION:
				switch (pickingMode)
				{
					case CLICKED:
						// Check if other slerp action is currently running
						if (iSlerpFactor > 0 && iSlerpFactor < SLERP_RANGE)
							break;

						// glConnectionLineRenderer.enableRendering(true);

						arSlerpActions.clear();

						RemoteLevelElement selectedElement = RemoteElementManager.get()
								.getItem(iExternalID);

						int iDestinationPosIndex = stackLevel
								.getPositionIndexByElementID(selectedElement);

						if (iDestinationPosIndex == 3)
							iDestinationPosIndex = 0;
						else
							iDestinationPosIndex++;

						// Check if destination position in stack is free
						if (stackLevel.getElementByPositionIndex(iDestinationPosIndex)
								.getContainedElementID() == -1)
						{
							SlerpAction slerpAction = new SlerpAction(selectedElement,
									stackLevel.getElementByPositionIndex(iDestinationPosIndex));
							arSlerpActions.add(slerpAction);
						}
						else
						{
							SlerpAction slerpActionTransition = new SlerpAction(
									selectedElement, transitionLevel
											.getElementByPositionIndex(0));
							arSlerpActions.add(slerpActionTransition);

							SlerpAction slerpAction = new SlerpAction(stackLevel
									.getElementByPositionIndex(iDestinationPosIndex),
									selectedElement);
							arSlerpActions.add(slerpAction);

							SlerpAction slerpActionTransitionReverse = new SlerpAction(
									selectedElement.getContainedElementID(), transitionLevel
											.getElementByPositionIndex(0), stackLevel
											.getElementByPositionIndex(iDestinationPosIndex));
							arSlerpActions.add(slerpActionTransitionReverse);
						}

						bEnableNavigationOverlay = false;

						break;

					case MOUSE_OVER:

						iNavigationMouseOverViewID_left = iExternalID;
						iNavigationMouseOverViewID_right = -1;
						iNavigationMouseOverViewID_out = -1;
						iNavigationMouseOverViewID_in = -1;
						iNavigationMouseOverViewID_lock = -1;

						break;
				}

				pickingManager.flushHits(iUniqueID,
						EPickingType.BUCKET_MOVE_LEFT_ICON_SELECTION);

				break;

			case BUCKET_MOVE_RIGHT_ICON_SELECTION:
				switch (pickingMode)
				{
					case CLICKED:
						// Check if other slerp action is currently running
						if (iSlerpFactor > 0 && iSlerpFactor < SLERP_RANGE)
							break;

						// glConnectionLineRenderer.enableRendering(true);

						arSlerpActions.clear();

						RemoteLevelElement selectedElement = RemoteElementManager.get()
								.getItem(iExternalID);

						int iDestinationPosIndex = stackLevel
								.getPositionIndexByElementID(selectedElement);

						if (iDestinationPosIndex == 0)
							iDestinationPosIndex = 3;
						else
							iDestinationPosIndex--;

						// Check if destination position in stack is free
						if (stackLevel.getElementByPositionIndex(iDestinationPosIndex)
								.getContainedElementID() == -1)
						{
							SlerpAction slerpAction = new SlerpAction(selectedElement,
									stackLevel.getElementByPositionIndex(iDestinationPosIndex));
							arSlerpActions.add(slerpAction);
						}
						else
						{
							SlerpAction slerpActionTransition = new SlerpAction(
									selectedElement, transitionLevel
											.getElementByPositionIndex(0));
							arSlerpActions.add(slerpActionTransition);

							SlerpAction slerpAction = new SlerpAction(stackLevel
									.getElementByPositionIndex(iDestinationPosIndex),
									selectedElement);
							arSlerpActions.add(slerpAction);

							SlerpAction slerpActionTransitionReverse = new SlerpAction(
									selectedElement.getContainedElementID(), transitionLevel
											.getElementByPositionIndex(0), stackLevel
											.getElementByPositionIndex(iDestinationPosIndex));
							arSlerpActions.add(slerpActionTransitionReverse);
						}

						bEnableNavigationOverlay = false;

						break;

					case MOUSE_OVER:

						iNavigationMouseOverViewID_left = -1;
						iNavigationMouseOverViewID_right = iExternalID;
						iNavigationMouseOverViewID_out = -1;
						iNavigationMouseOverViewID_in = -1;
						iNavigationMouseOverViewID_lock = -1;

						break;
				}

				pickingManager.flushHits(iUniqueID,
						EPickingType.BUCKET_MOVE_RIGHT_ICON_SELECTION);

				break;

			// case MEMO_PAD_SELECTION:
			// switch (pickingMode)
			// {
			// case CLICKED:
			//
			// break;
			//
			// case DRAGGED:
			//
			// int iDraggedObjectId = dragAndDrop.getDraggedObjectedId();
			//
			// if (iExternalID == TRASH_CAN_PICKING_ID)
			// {
			// if (iDraggedObjectId != -1)
			// {
			// // if
			// // (memoLayer.containsElement(iDraggedObjectId))
			// // {
			// memoLayer.removeElement(iDraggedObjectId);
			// // dragAndDrop.stopDragAction();
			// // break;
			// // }
			//
			// underInteractionLayer.removeElement(iDraggedObjectId);
			// stackLayer.removeElement(iDraggedObjectId);
			// poolLayer.removeElement(iDraggedObjectId);
			// }
			// }
			// else if (iExternalID == MEMO_PAD_PICKING_ID)
			// {
			// if (iDraggedObjectId != -1)
			// {
			// if (!memoLayer.containsElement(iDraggedObjectId))
			// {
			// memoLayer.addElement(iDraggedObjectId);
			// memoLayer.setElementVisibilityById(true, iDraggedObjectId);
			// }
			// }
			// }
			//
			// dragAndDrop.stopDragAction();
			//
			// break;
			// }
			//
			// pickingManager.flushHits(iUniqueID,
			// EPickingType.MEMO_PAD_SELECTION);

			// break;

		}
	}

	@Override
	public String getShortInfo()
	{
		return "Bucket / Jukebox";
	}

	@Override
	public String getDetailedInfo()
	{
		StringBuffer sInfoText = new StringBuffer();
		sInfoText.append("Bucket / Jukebox");
		return sInfoText.toString();
	}

	private void createEventMediator()
	{
		generalManager.getEventPublisher().addSender(EMediatorType.SELECTION_MEDIATOR,
				(IMediatorSender) this);

		generalManager.getEventPublisher().addReceiver(EMediatorType.SELECTION_MEDIATOR,
				(IMediatorReceiver) this);
	}

	public synchronized void toggleLayoutMode()
	{
		if (layoutMode.equals(ARemoteViewLayoutRenderStyle.LayoutMode.BUCKET))
			layoutMode = ARemoteViewLayoutRenderStyle.LayoutMode.LIST;
		// layoutMode = ARemoteViewLayoutRenderStyle.LayoutMode.JUKEBOX;
		else
			layoutMode = ARemoteViewLayoutRenderStyle.LayoutMode.BUCKET;

		if (layoutMode.equals(ARemoteViewLayoutRenderStyle.LayoutMode.BUCKET))
		{
			layoutRenderStyle = new BucketLayoutRenderStyle(viewFrustum, layoutRenderStyle);

			bucketMouseWheelListener = new BucketMouseWheelListener(this,
					(BucketLayoutRenderStyle) layoutRenderStyle);

			// Unregister standard mouse wheel listener
			parentGLCanvas.removeMouseWheelListener(pickingTriggerMouseAdapter);
			// Register specialized bucket mouse wheel listener
			parentGLCanvas.addMouseWheelListener(bucketMouseWheelListener);

			glConnectionLineRenderer = new GLConnectionLineRendererBucket(focusLevel,
					stackLevel, poolLevel);
		}
		else if (layoutMode.equals(ARemoteViewLayoutRenderStyle.LayoutMode.JUKEBOX))
		{
			layoutRenderStyle = new JukeboxLayoutRenderStyle(viewFrustum, layoutRenderStyle);

			// Unregister bucket wheel listener
			parentGLCanvas.removeMouseWheelListener(bucketMouseWheelListener);
			// Register standard mouse wheel listener
			parentGLCanvas.addMouseWheelListener(pickingTriggerMouseAdapter);

			glConnectionLineRenderer = new GLConnectionLineRendererJukebox(focusLevel,
					stackLevel, poolLevel);
		}
		else if (layoutMode.equals(ARemoteViewLayoutRenderStyle.LayoutMode.LIST))
		{
			layoutRenderStyle = new ListLayoutRenderStyle(viewFrustum, layoutRenderStyle);
			glConnectionLineRenderer = null;

			// // Copy views from stack to pool
			// for (Integer iElementID : stackLevel.getElementList())
			// {
			// if (iElementID == -1)
			// continue;
			//				
			// poolLevel.addElement(iElementID);
			// // poolLevel.setElementVisibilityById(true, iElementID);
			// }
			// stackLevel.clear();
		}

		focusLevel = layoutRenderStyle.initFocusLevel();
		stackLevel = layoutRenderStyle.initStackLevel(bucketMouseWheelListener.isZoomedIn());
		poolLevel = layoutRenderStyle.initPoolLevel(bucketMouseWheelListener.isZoomedIn(), -1);
		selectionLevel = layoutRenderStyle.initMemoLevel();
		transitionLevel = layoutRenderStyle.initTransitionLevel();
		spawnLevel = layoutRenderStyle.initSpawnLevel();

		viewFrustum.setProjectionMode(layoutRenderStyle.getProjectionMode());

		// Trigger reshape to apply new projection mode
		// Is there a better way to achieve this? :)
		parentGLCanvas.setSize(parentGLCanvas.getWidth(), parentGLCanvas.getHeight());
	}

	public synchronized void toggleConnectionLines()
	{
		bEnableConnectinLines = !bEnableConnectinLines;
	}

	/**
	 * Unregister view from event system. Remove view from GL render loop.
	 */
	public void removeView(AGLEventListener glEventListener)
	{
		glEventListener.destroy();
	}

	public synchronized void clearAll()
	{
		enableBusyMode(false);
		pickingManager.enablePicking(true);
		
		iAlUninitializedPathwayIDs.clear();
		arSlerpActions.clear();

		generalManager.getPathwayManager().resetPathwayVisiblityState();

		clearRemoteLevel(focusLevel);
		clearRemoteLevel(stackLevel);
		clearRemoteLevel(poolLevel);
		
		generalManager.getViewGLCanvasManager().getConnectedElementRepresentationManager().clearAll();
	}

	private void clearRemoteLevel(RemoteLevel remoteLevel)
	{
		int iViewID;
		IViewManager viewManager = generalManager.getViewGLCanvasManager();
		AGLEventListener glEventListener = null;

		for (RemoteLevelElement element : remoteLevel.getAllElements())
		{
			iViewID = element.getContainedElementID();

			if (iViewID == -1)
				continue;

			glEventListener = viewManager.getGLEventListener(iViewID);

			if (glEventListener instanceof GLHeatMap
					|| glEventListener instanceof GLParallelCoordinates)
			{
				// Remove all elements from heatmap and parallel coordinates
				((AStorageBasedView) glEventListener).resetView();

				if (!glEventListener.isRenderedRemote())
					glEventListener.enableBusyMode(false);
			}
			else
			{
				removeView(glEventListener);
				element.setContainedElementID(-1);
			}
		}
	}

	// @Override
	// public synchronized RemoteLevel getHierarchyLayerByGLEventListenerId(
	// final int iGLEventListenerId)
	// {
	// if (focusLevel.containsElement(iGLEventListenerId))
	// return focusLevel;
	// else if (stackLevel.containsElement(iGLEventListenerId))
	// return stackLevel;
	// else if (poolLevel.containsElement(iGLEventListenerId))
	// return poolLevel;
	// else if (transitionLevel.containsElement(iGLEventListenerId))
	// return transitionLevel;
	// else if (spawnLevel.containsElement(iGLEventListenerId))
	// return spawnLevel;
	// else if (selectionLevel.containsElement(iGLEventListenerId))
	// return selectionLevel;
	//
	// generalManager.getLogger().log(Level.WARNING,
	// "GL Event Listener " + iGLEventListenerId +
	// " is not contained in any layer!");
	//
	// return null;
	// }

	@Override
	public RemoteLevel getFocusLevel()
	{
		return focusLevel;
	}

	@Override
	public BucketMouseWheelListener getBucketMouseWheelListener()
	{
		return bucketMouseWheelListener;
	}

	@Override
	public synchronized void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height)
	{
		super.reshape(drawable, x, y, width, height);

		// Update aspect ratio and reinitialize stack and focus layer
		layoutRenderStyle.setAspectRatio(fAspectRatio);

		layoutRenderStyle.initFocusLevel();
		layoutRenderStyle.initStackLevel(bucketMouseWheelListener.isZoomedIn());
		layoutRenderStyle.initPoolLevel(bucketMouseWheelListener.isZoomedIn(),
				iMouseOverObjectID);
		layoutRenderStyle.initMemoLevel();
	}

	protected void renderPoolAndMemoLayerBackground(final GL gl)
	{
		// Pool layer background

		float fWidth = 0.8f;
		float fXCorrection = 0.07f; // Detach pool level from stack

		if (layoutMode.equals(LayoutMode.BUCKET))
		{
			gl.glPushName(pickingManager.getPickingID(iUniqueID,
					EPickingType.REMOTE_LEVEL_ELEMENT, iPoolLevelCommonID));

			gl.glColor4f(0.85f, 0.85f, 0.85f, 1f);
			gl.glLineWidth(1);
			gl.glBegin(GL.GL_POLYGON);
			gl.glVertex3f((-2 - fXCorrection) / fAspectRatio, -2, 4);
			gl.glVertex3f((-2 - fXCorrection) / fAspectRatio, 2, 4);
			gl.glVertex3f((-2 - fXCorrection) / fAspectRatio + fWidth, 2, 4);
			gl.glVertex3f((-2 - fXCorrection) / fAspectRatio + fWidth, -2, 4);
			gl.glEnd();

			if (dragAndDrop.isDragActionRunning() && iMouseOverObjectID == iPoolLevelCommonID)
			{
				gl.glLineWidth(5);
				gl.glColor4f(0.2f, 0.2f, 0.2f, 1);
			}
			else
			{
				gl.glLineWidth(1);
				gl.glColor4f(0.4f, 0.4f, 0.4f, 1);
			}

			gl.glBegin(GL.GL_LINE_LOOP);
			gl.glVertex3f((-2 - fXCorrection) / fAspectRatio, -2, 4);
			gl.glVertex3f((-2 - fXCorrection) / fAspectRatio, 2, 4);
			gl.glVertex3f((-2 - fXCorrection) / fAspectRatio + fWidth, 2, 4);
			gl.glVertex3f((-2 - fXCorrection) / fAspectRatio + fWidth, -2, 4);
			gl.glEnd();

			gl.glPopName();

			// // Render memo pad background
			// gl.glColor4f(0.85f, 0.85f, 0.85f, 1f);
			// gl.glLineWidth(1);
			// gl.glBegin(GL.GL_POLYGON);
			// gl.glVertex3f(2 / fAspectRatio, -2, 4);
			// gl.glVertex3f(2 / fAspectRatio, 2, 4);
			// gl.glVertex3f(2 / fAspectRatio - fWidth, 2, 4);
			// gl.glVertex3f(2 / fAspectRatio - fWidth, -2, 4);
			// gl.glEnd();
			//
			// gl.glColor4f(0.4f, 0.4f, 0.4f, 1);
			// gl.glLineWidth(1);
			// gl.glBegin(GL.GL_LINE_LOOP);
			// gl.glVertex3f(2 / fAspectRatio, -2, 4);
			// gl.glVertex3f(2 / fAspectRatio, 2, 4);
			// gl.glVertex3f(2 / fAspectRatio - fWidth, 2, 4);
			// gl.glVertex3f(2 / fAspectRatio - fWidth, -2, 4);
			// gl.glEnd();
		}

		// Render caption
		if (textRenderer == null)
			return;

		String sTmp = "POOL AREA";
		textRenderer.begin3DRendering();
		textRenderer.setColor(0.6f, 0.6f, 0.6f, 1.0f);
		textRenderer.draw3D(sTmp, (-1.9f - fXCorrection) / fAspectRatio, -1.97f, 4.001f,
				0.003f);
		textRenderer.end3DRendering();
	}

	public synchronized void enableGeneMapping(final boolean bEnableMapping)
	{
		for (GLEventListener tmpGLEventListener : generalManager.getViewGLCanvasManager()
				.getAllGLEventListeners())
		{
			if (tmpGLEventListener instanceof GLPathway)
			{
				((GLPathway) tmpGLEventListener).enableGeneMapping(bEnableMapping);
			}
		}
	}

	public synchronized void enablePathwayTextures(final boolean bEnablePathwayTexture)
	{
		for (GLEventListener tmpGLEventListener : generalManager.getViewGLCanvasManager()
				.getAllGLEventListeners())
		{
			if (tmpGLEventListener instanceof GLPathway)
			{
				((GLPathway) tmpGLEventListener).enablePathwayTextures(bEnablePathwayTexture);
			}
		}
	}

	public synchronized void enableNeighborhood(final boolean bEnableNeighborhood)
	{
		for (GLEventListener tmpGLEventListener : generalManager.getViewGLCanvasManager()
				.getAllGLEventListeners())
		{
			if (tmpGLEventListener instanceof GLPathway)
			{
				((GLPathway) tmpGLEventListener).enableNeighborhood(bEnableNeighborhood);
			}
		}
	}

	@Override
	public void triggerEvent(EMediatorType eMediatorType, IEventContainer eventContainer)
	{
		generalManager.getEventPublisher().triggerEvent(eMediatorType, this, eventContainer);

	}

	@Override
	public synchronized void broadcastElements(EVAOperation type)
	{
		throw new IllegalStateException("Not Implemented");
	}

	private synchronized void initializeNewPathways(final GL gl)
	{
		// Init newly created pathways
		// FIXME: this specialization to pathways in the bucket is not good!
		if (!iAlUninitializedPathwayIDs.isEmpty() && arSlerpActions.isEmpty())
		{
			int iTmpPathwayID = iAlUninitializedPathwayIDs.get(0);

			// Check if pathway is already loaded in bucket
			if (!generalManager.getPathwayManager().isPathwayVisible(iTmpPathwayID))
			{
				ArrayList<Integer> iAlSetIDs = new ArrayList<Integer>();

				for (ISet tmpSet : alSets)
				{
					if (tmpSet.getSetType() != ESetType.GENE_EXPRESSION_DATA)
						continue;

					iAlSetIDs.add(tmpSet.getID());
				}

				// Create Pathway3D view
				CmdCreateGLPathway cmdPathway = (CmdCreateGLPathway) generalManager
						.getCommandManager().createCommandByType(
								ECommandType.CREATE_GL_PATHWAY_3D);

				cmdPathway.setAttributes(iTmpPathwayID, iAlSetIDs,
						EProjectionMode.ORTHOGRAPHIC, -4, 4, 4, -4, -20, 20);
				cmdPathway.doCommand();

				GLPathway glPathway = (GLPathway) cmdPathway.getCreatedObject();
				int iGeneratedViewID = glPathway.getID();

				GeneralManager.get().getEventPublisher().addSender(
						EMediatorType.SELECTION_MEDIATOR, (IMediatorSender) glPathway);
				GeneralManager.get().getEventPublisher().addReceiver(
						EMediatorType.SELECTION_MEDIATOR, (IMediatorReceiver) glPathway);

				iAlContainedViewIDs.add(iGeneratedViewID);

				// Trigger last delta to new pathways
				if (lastSelectionDelta != null)
					triggerEvent(EMediatorType.SELECTION_MEDIATOR,
							new DeltaEventContainer<ISelectionDelta>(lastSelectionDelta));

				if (focusLevel.hasFreePosition())
				{
					spawnLevel.getElementByPositionIndex(0).setContainedElementID(
							iGeneratedViewID);
					SlerpAction slerpActionTransition = new SlerpAction(spawnLevel
							.getElementByPositionIndex(0), focusLevel.getNextFree());
					arSlerpActions.add(slerpActionTransition);

					glPathway.initRemote(gl, iUniqueID, pickingTriggerMouseAdapter, this);
					glPathway.setDetailLevel(EDetailLevel.MEDIUM);

					// Trigger initial gene propagation
					glPathway.broadcastElements(EVAOperation.APPEND_UNIQUE);
				}
				else if (stackLevel.hasFreePosition()
						&& !(layoutRenderStyle instanceof ListLayoutRenderStyle))
				{
					spawnLevel.getElementByPositionIndex(0).setContainedElementID(
							iGeneratedViewID);
					SlerpAction slerpActionTransition = new SlerpAction(spawnLevel
							.getElementByPositionIndex(0), stackLevel.getNextFree());
					arSlerpActions.add(slerpActionTransition);

					glPathway.initRemote(gl, iUniqueID, pickingTriggerMouseAdapter, this);
					glPathway.setDetailLevel(EDetailLevel.LOW);

					// Trigger initial gene propagation
					glPathway.broadcastElements(EVAOperation.APPEND_UNIQUE);
				}
				else if (poolLevel.hasFreePosition())
				{
					spawnLevel.getElementByPositionIndex(0).setContainedElementID(
							iGeneratedViewID);
					SlerpAction slerpActionTransition = new SlerpAction(spawnLevel
							.getElementByPositionIndex(0), poolLevel.getNextFree());
					arSlerpActions.add(slerpActionTransition);

					glPathway.initRemote(gl, iUniqueID, pickingTriggerMouseAdapter, this);
					glPathway.setDetailLevel(EDetailLevel.VERY_LOW);
				}
				else
				{
					generalManager.getLogger().log(Level.SEVERE,
							"No empty space left to add new pathway!");
					iAlUninitializedPathwayIDs.clear();

					for (AGLEventListener eventListener : generalManager
							.getViewGLCanvasManager().getAllGLEventListeners())
					{
						if (!eventListener.isRenderedRemote())
							eventListener.enableBusyMode(false);
					}

					// Enable picking after all pathways are loaded
					generalManager.getViewGLCanvasManager().getPickingManager().enablePicking(
							true);

					return;
				}
			}
			else
			{
				generalManager.getLogger().log(Level.WARNING,
						"Pathway with ID: " + iTmpPathwayID + " is already loaded in Bucket.");
			}

			iAlUninitializedPathwayIDs.remove(0);

			if (iAlUninitializedPathwayIDs.isEmpty())
			{
				// Enable picking after all pathways are loaded
				generalManager.getViewGLCanvasManager().getPickingManager()
						.enablePicking(true);

				for (AGLEventListener eventListener : generalManager.getViewGLCanvasManager()
						.getAllGLEventListeners())
				{
					if (!eventListener.isRenderedRemote())
						eventListener.enableBusyMode(false);
				}
			}
		}
	}

	@Override
	public void enableBusyMode(boolean busyMode)
	{
		super.enableBusyMode(busyMode);

		if (eBusyModeState == EBusyModeState.ON)
		{
			// parentGLCanvas.removeMouseListener(pickingTriggerMouseAdapter);
			parentGLCanvas.removeMouseWheelListener(bucketMouseWheelListener);
		}
		else
		{
			// parentGLCanvas.addMouseListener(pickingTriggerMouseAdapter);
			parentGLCanvas.addMouseWheelListener(bucketMouseWheelListener);
		}
	}

	@Override
	public int getNumberOfSelections(ESelectionType eSelectionType)
	{
		return 0;
	}

	private void compactPoolLevel()
	{
		RemoteLevelElement element;
		RemoteLevelElement elementInner;
		for (int iIndex = 0; iIndex < poolLevel.getCapacity(); iIndex++)
		{
			element = poolLevel.getElementByPositionIndex(iIndex);
			if (element.isFree())
			{
				// Search for next element to put it in the free position
				for (int iInnerIndex = iIndex + 1; iInnerIndex < poolLevel.getCapacity(); iInnerIndex++)
				{
					elementInner = poolLevel.getElementByPositionIndex(iInnerIndex);

					if (elementInner.isFree())
						continue;

					element.setContainedElementID(elementInner.getContainedElementID());
					elementInner.setContainedElementID(-1);

					break;
				}
			}
		}
	}

	public ArrayList<Integer> getRemoteRenderedViews()
	{
		return iAlContainedViewIDs;
	}

	private void updateOffScreenTextures(final GL gl)
	{
		bUpdateOffScreenTextures = false;

		gl.glPushMatrix();

		int iViewWidth = parentGLCanvas.getWidth();
		int iViewHeight = parentGLCanvas.getHeight();

		if (stackLevel.getElementByPositionIndex(0).getContainedElementID() != -1)
		{
			glOffScreenRenderer.renderToTexture(gl, stackLevel.getElementByPositionIndex(0)
					.getContainedElementID(), 0, iViewWidth, iViewHeight);
		}

		if (stackLevel.getElementByPositionIndex(1).getContainedElementID() != -1)
		{
			glOffScreenRenderer.renderToTexture(gl, stackLevel.getElementByPositionIndex(1)
					.getContainedElementID(), 1, iViewWidth, iViewHeight);
		}

		if (stackLevel.getElementByPositionIndex(2).getContainedElementID() != -1)
		{
			glOffScreenRenderer.renderToTexture(gl, stackLevel.getElementByPositionIndex(2)
					.getContainedElementID(), 2, iViewWidth, iViewHeight);
		}

		if (stackLevel.getElementByPositionIndex(3).getContainedElementID() != -1)
		{
			glOffScreenRenderer.renderToTexture(gl, stackLevel.getElementByPositionIndex(3)
					.getContainedElementID(), 3, iViewWidth, iViewHeight);
		}

		gl.glPopMatrix();
	}
}
