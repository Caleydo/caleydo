package org.caleydo.core.view.opengl.canvas.remote;

import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import gleem.linalg.Vec4f;
import gleem.linalg.open.Transform;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Collection;
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
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.ISelectionDelta;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionDelta;
import org.caleydo.core.data.selection.SelectionItem;
import org.caleydo.core.manager.event.mediator.EMediatorType;
import org.caleydo.core.manager.event.mediator.IMediatorReceiver;
import org.caleydo.core.manager.event.mediator.IMediatorSender;
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

	// FIXME: should be a singleton

	// private GLIconTextureManager iconTextureManager;

	private ArrayList<Integer> iAlUninitializedPathwayIDs;

	private int iMediatorID = -1;

	private TextRenderer textRenderer;

	private GLDragAndDrop dragAndDrop;

	private ARemoteViewLayoutRenderStyle layoutRenderStyle;

	private BucketMouseWheelListener bucketMouseWheelListener;

	private GLColorMappingBarMiniView colorMappingBarMiniView;

	private ArrayList<Integer> iAlContainedViewIDs;

	// private int iGLDisplayList;

	private GLSelectionPanel glSelectionPanel;

	private ISelectionDelta lastSelectionDelta;

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

		focusLevel = layoutRenderStyle.initUnderInteractionLevel();
		stackLevel = layoutRenderStyle.initStackLevel(bucketMouseWheelListener.isZoomedIn());
		poolLevel = layoutRenderStyle.initPoolLevel(-1);
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

		// selectionManager = new
		// GenericSelectionManager.Builder(EIDType.DAVID).build();

		// Registration to event system
		generalManager.getEventPublisher().addSender(EMediatorType.SELECTION_MEDIATOR,
				(IMediatorSender) this);
		generalManager.getEventPublisher().addReceiver(EMediatorType.SELECTION_MEDIATOR,
				(IMediatorReceiver) this);
		generalManager.getEventPublisher().addSender(EMediatorType.VIEW_SELECTION, this);
		// generalManager.getEventPublisher().addSender(
		// EMediatorType.SELECTION_MEDIATOR, (IMediatorSender)this);
		// generalManager.getEventPublisher().addReceiver(
		// EMediatorType.BUCKET_INTERNAL_INCOMING_MEDIATOR,
		// (IMediatorReceiver)this);
	}

	@Override
	public void initLocal(final GL gl)
	{
		// iGLDisplayList = gl.glGenLists(1);

		init(gl);
	}

	@Override
	public void initRemote(final GL gl, final int iRemoteViewID, final RemoteLevel layer,
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

		glSelectionPanel.initRemote(gl, getID(), null, pickingTriggerMouseAdapter,
				remoteRenderingGLCanvas);

		// trashCan.init(gl);

		colorMappingBarMiniView.setWidth(layoutRenderStyle.getColorBarWidth());
		colorMappingBarMiniView.setHeight(layoutRenderStyle.getColorBarHeight());
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

			if (iMouseOverObjectID != iDraggedObjectId)
			{
				RemoteLevelElement mouseOverElement = RemoteElementManager.get().getItem(
						iMouseOverObjectID);

				RemoteLevelElement originElement = RemoteElementManager.get().getItem(
						iDraggedObjectId);

				int iMouseOverElementID = mouseOverElement.getContainedElementID();
				int iOriginElementID = originElement.getContainedElementID();

				mouseOverElement.setContainedElementID(iOriginElementID);
				originElement.setContainedElementID(iMouseOverElementID);
			}

			// if (iDraggedObjectId != -1 && iMouseOverObjectID !=
			// iDraggedObjectId)
			// {

			// if (focusLevel.containsElement(mouseOverElement))
			// {
			// focusLevel.replaceElement(originElement, mouseOverElement);
			// // focusLevel.setElementVisibilityById(true, iDraggedObjectId);
			// }
			// else if (focusLevel.containsElement(originElement))
			// {
			// focusLevel.replaceElement(mouseOverElement, originElement);
			// // focusLevel.setElementVisibilityById(true, iMouseOverObjectID);
			// }
			//
			// if (stackLevel.containsElement(iMouseOverObjectID)
			// && stackLevel.containsElement(iDraggedObjectId))
			// {
			// stackLevel.swapElements(iMouseOverObjectID, iDraggedObjectId);
			// }
			// else
			// {
			// if (stackLevel.containsElement(iMouseOverObjectID))
			// {
			// stackLevel.replaceElement(iDraggedObjectId,
			// stackLevel.getPositionIndexByElementID(iMouseOverObjectID));
			// // stackLevel.setElementVisibilityById(true, iDraggedObjectId);
			// }
			// else if (stackLevel.containsElement(iDraggedObjectId))
			// {
			// stackLevel.replaceElement(iMouseOverObjectID,
			// stackLevel.getPositionIndexByElementID(iDraggedObjectId));
			// // stackLevel.setElementVisibilityById(true, iMouseOverObjectID);
			// }
			// }
			// }

			// System.out.println("Stop drag!");

			dragAndDrop.stopDragAction();
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

		layoutRenderStyle.initPoolLevel(iMouseOverObjectID);
		// layoutRenderStyle.initMemoLayer();
		// layoutRenderStyle.initStackLayer();
		// layoutRenderStyle.initTransitionLayer();
		// layoutRenderStyle.initUnderInteractionLayer();
		// layoutRenderStyle.initSpawnLayer();

		doSlerpActions(gl);
		initializeNewPathways(gl);

		renderRemoteLevel(gl, focusLevel);
		renderRemoteLevel(gl, stackLevel);

		// If user zooms to the bucket bottom all but the under
		// interaction layer is _not_ rendered.
		if (bucketMouseWheelListener == null || !bucketMouseWheelListener.isZoomedIn())
		{
			// comment here for connection lines
			if (glConnectionLineRenderer != null)
				glConnectionLineRenderer.render(gl);

			renderPoolAndMemoLayerBackground(gl);

			renderRemoteLevel(gl, transitionLevel);
			renderRemoteLevel(gl, poolLevel);
			renderRemoteLevel(gl, spawnLevel);

			renderRemoteLevel(gl, selectionLevel);
		}

		if (layoutMode.equals(ARemoteViewLayoutRenderStyle.LayoutMode.BUCKET))
		{
			bucketMouseWheelListener.render();
		}

		colorMappingBarMiniView.render(gl, layoutRenderStyle.getColorBarXPos(),
				layoutRenderStyle.getColorBarYPos(), 4);

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
				focusLevel.getNextFree().setContainedElementID(iViewID);

				tmpGLEventListener.initRemote(gl, iUniqueID, focusLevel,
						pickingTriggerMouseAdapter, this);

				tmpGLEventListener.broadcastElements(ESelectionType.ADD);
				tmpGLEventListener.setDetailLevel(EDetailLevel.MEDIUM);

				generalManager.getGUIBridge().setActiveGLSubView(this, tmpGLEventListener);

			}
			else if (stackLevel.hasFreePosition()
					&& !(layoutRenderStyle instanceof ListLayoutRenderStyle))
			{
				stackLevel.getNextFree().setContainedElementID(iViewID);

				tmpGLEventListener.initRemote(gl, iUniqueID, stackLevel,
						pickingTriggerMouseAdapter, this);

				tmpGLEventListener.broadcastElements(ESelectionType.ADD);
				tmpGLEventListener.setDetailLevel(EDetailLevel.LOW);
			}
			else if (poolLevel.hasFreePosition())
			{
				poolLevel.getNextFree().setContainedElementID(iViewID);

				tmpGLEventListener.initRemote(gl, iUniqueID, poolLevel,
						pickingTriggerMouseAdapter, this);
				tmpGLEventListener.setDetailLevel(EDetailLevel.VERY_LOW);
			}

			// pickingTriggerMouseAdapter.addGLCanvas(tmpGLEventListener);
			pickingManager.getPickingID(iUniqueID, EPickingType.VIEW_SELECTION, iViewID);

			generalManager.getEventPublisher().addSender(EMediatorType.SELECTION_MEDIATOR,
					(IMediatorSender) tmpGLEventListener);
			generalManager.getEventPublisher().addReceiver(EMediatorType.SELECTION_MEDIATOR,
					(IMediatorReceiver) tmpGLEventListener);
		}
	}

	private void renderBucketWall(final GL gl, boolean bRenderBorder)
	{
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

		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(0, 0, -0.02f);
		gl.glVertex3f(0, 8, -0.02f);
		gl.glVertex3f(8, 8, -0.02f);
		gl.glVertex3f(8, 0, -0.02f);
		gl.glEnd();
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

		// if (tmpCanvasUser == null)

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

		if (level.equals(poolLevel))
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
				sRenderText = glEventListener.getNumberOfSelections(ESelectionType.MOUSE_OVER)
						+ " - " + sRenderText;
			}

			float fTextScalingFactor = 0.09f;
			if (element.getID() == iMouseOverObjectID)
			{
				renderPoolSelection(gl, translation.x() + 3f, translation.y() * scale.y()
						+ 5.3f,
						(float) textRenderer.getBounds(sRenderText).getWidth() * 0.06f + 23,
						6f); // 1.8f -> pool focus scaling

				gl.glTranslatef(4f, 1.3f, 0);

				fTextScalingFactor = 0.075f;
			}

			textRenderer.begin3DRendering();
			textRenderer.draw3D(sRenderText, 8.5f, 3, 0, fTextScalingFactor); // scale
																				// factor
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
				renderBucketWall(gl, false);
			else
				renderBucketWall(gl, true);
		}

		if (!bEnableNavigationOverlay || !level.equals(stackLevel))
		{
			glEventListener.displayRemote(gl);
		}
		else
		{
			renderNavigationOverlay(gl, element.getID());
		}

		// // Render transparent plane for picking views without texture (e.g.
		// PC)
		// gl.glColor4f(1, 1, 1, 0);
		//
		// gl.glBegin(GL.GL_POLYGON);
		// gl.glVertex3f(0, 0, -0.01f);
		// gl.glVertex3f(0, 8, -0.01f);
		// gl.glVertex3f(8, 8, -0.01f);
		// gl.glVertex3f(8, 0, -0.01f);
		// gl.glEnd();

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
			renderBucketWall(gl, true);
		}

		gl.glPopName();

		gl.glPopMatrix();
	}

	private void renderHandles(final GL gl)
	{
		// glConnectionLineRenderer.enableRendering(false);

		// IViewFrustum frustum = glEventListener.getViewFrustum();
		float fBilboardWidth = 0.1f;

		// Bucket stack top
		gl.glTranslatef(2 - fBilboardWidth, 2 - fBilboardWidth, 4f);
		renderSingleHandle(gl, stackLevel.getElementByPositionIndex(0).getID(),
				EPickingType.BUCKET_REMOVE_ICON_SELECTION,
				EIconTextures.NAVIGATION_REMOVE_VIEW);
		gl.glTranslatef(-0.17f, 0, 0);
		renderSingleHandle(gl, stackLevel.getElementByPositionIndex(0).getID(),
				EPickingType.BUCKET_DRAG_ICON_SELECTION, EIconTextures.NAVIGATION_DRAG_VIEW);
		gl.glTranslatef(-2 + 0.17f + fBilboardWidth, -2 + fBilboardWidth, -4f);

		// Bucket stack left
		gl.glTranslatef(-2f / fAspectRatio + 0.8f + 0.1f, 1.9f, 4);
		gl.glRotatef(90, 0, 0, 1);
		renderSingleHandle(gl, stackLevel.getElementByPositionIndex(1).getID(),
				EPickingType.BUCKET_REMOVE_ICON_SELECTION,
				EIconTextures.NAVIGATION_REMOVE_VIEW);
		gl.glTranslatef(-0.17f, 0, 0);
		renderSingleHandle(gl, stackLevel.getElementByPositionIndex(1).getID(),
				EPickingType.BUCKET_DRAG_ICON_SELECTION, EIconTextures.NAVIGATION_DRAG_VIEW);
		gl.glRotatef(-90, 0, 0, 1);
		gl.glTranslatef(2f / fAspectRatio - 0.8f - 0.1f, -1.9f + 0.17f, -4f);

		// Bucket stack bottom
		gl.glTranslatef(2 - fBilboardWidth, -2 + 0.1f, 4f);
		gl.glRotatef(180, 1, 0, 0);
		renderSingleHandle(gl, stackLevel.getElementByPositionIndex(2).getID(),
				EPickingType.BUCKET_REMOVE_ICON_SELECTION,
				EIconTextures.NAVIGATION_REMOVE_VIEW);
		gl.glTranslatef(-0.17f, 0, 0);
		renderSingleHandle(gl, stackLevel.getElementByPositionIndex(2).getID(),
				EPickingType.BUCKET_DRAG_ICON_SELECTION, EIconTextures.NAVIGATION_DRAG_VIEW);
		gl.glRotatef(-180, 1, 0, 0);
		gl.glTranslatef(-2 + 0.17f + fBilboardWidth, 2 - 0.1f, -4f);

		// Bucket stack right
		gl.glTranslatef(2f / fAspectRatio - fBilboardWidth - 0.8f, 1.55f, 4f);
		gl.glRotatef(-90, 0, 0, 1);
		renderSingleHandle(gl, stackLevel.getElementByPositionIndex(3).getID(),
				EPickingType.BUCKET_DRAG_ICON_SELECTION, EIconTextures.NAVIGATION_DRAG_VIEW);
		gl.glTranslatef(-0.17f, 0, 0);
		renderSingleHandle(gl, stackLevel.getElementByPositionIndex(3).getID(),
				EPickingType.BUCKET_REMOVE_ICON_SELECTION,
				EIconTextures.NAVIGATION_REMOVE_VIEW);
		gl.glPopName();
		gl.glRotatef(90, 0, 0, 1);
		gl.glTranslatef(-2f / fAspectRatio + fBilboardWidth + 0.8f, -1.55f - 0.17f, -4f);

		// Bucket center
		gl.glTranslatef(2 - 2 * fBilboardWidth, 2 - 2 * fBilboardWidth, 0.05f);
		gl.glScalef(2, 2, 2);
		renderSingleHandle(gl, focusLevel.getElementByPositionIndex(0).getID(),
				EPickingType.BUCKET_REMOVE_ICON_SELECTION,
				EIconTextures.NAVIGATION_REMOVE_VIEW);
		gl.glTranslatef(-0.17f, 0, 0);
		renderSingleHandle(gl, focusLevel.getElementByPositionIndex(0).getID(),
				EPickingType.BUCKET_DRAG_ICON_SELECTION, EIconTextures.NAVIGATION_DRAG_VIEW);
		gl.glScalef(1 / 2f, 1 / 2f, 1 / 2f);
		gl.glTranslatef(-2 + 2 * 0.17f + 2 * fBilboardWidth, -2 + 2 * fBilboardWidth, -0.05f);

	}

	private void renderSingleHandle(final GL gl, int iRemoteLevelElementID,
			EPickingType ePickingType, EIconTextures eIconTexture)
	{
		gl.glPushName(pickingManager.getPickingID(iUniqueID, ePickingType,
				iRemoteLevelElementID));

		// renderRemoveButton(gl, 0, 0, 0.1f);

		Texture tempTexture = iconTextureManager.getIconTexture(gl, eIconTexture);
		tempTexture.enable();
		tempTexture.bind();

		TextureCoords texCoords = tempTexture.getImageTexCoords();
		gl.glColor3f(1, 1, 1);
		gl.glBegin(GL.GL_POLYGON);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(0, 0, -0.01f);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(0, 0.1f, -0.01f);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(-0.2f, 0.1f, -0.01f);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(-0.2f, 0, -0.01f);
		gl.glEnd();

		tempTexture.disable();

		gl.glPopName();
	}

	private void renderRemoveButton(final GL gl, float fXOrigin, float fYOrigin,
			float fButtonLength)
	{
		gl.glColor4f(1, 1, 1, 1);
		gl.glLineWidth(2);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(fXOrigin, fYOrigin, 0f);
		gl.glVertex3f(fXOrigin, fYOrigin + fButtonLength, 0f);
		gl.glVertex3f(fXOrigin + fButtonLength, fYOrigin + fButtonLength, 0f);
		gl.glVertex3f(fXOrigin + fButtonLength, fYOrigin, 0f);
		gl.glEnd();
		gl.glBegin(GL.GL_LINES);
		gl.glVertex3f(fXOrigin, fYOrigin, 0f);
		gl.glVertex3f(fXOrigin + fButtonLength, fYOrigin + fButtonLength, 0f);
		gl.glVertex3f(fXOrigin, fYOrigin + fButtonLength, 0f);
		gl.glVertex3f(fXOrigin + fButtonLength, fYOrigin, 0f);
		gl.glEnd();
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
			float fWidth, float fHeight)
	{
		float fPanelSideWidth = 2.5f;

		Texture tempTexture = iconTextureManager.getIconTexture(gl,
				EIconTextures.PANEL_SELECTION_SIDE);
		tempTexture.enable();
		tempTexture.bind();

		TextureCoords texCoords = tempTexture.getImageTexCoords();

		gl.glColor4f(1, 1, 1, 0.75f);

		// Left background piece
		gl.glBegin(GL.GL_POLYGON);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(fXOrigin + 0.1f, fYOrigin - fHeight, -0.01f);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(fXOrigin + 0.1f, fYOrigin + fHeight, -0.01f);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(fXOrigin + 0.1f + fPanelSideWidth, fYOrigin + fHeight, -0.01f);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(fXOrigin + 0.1f + fPanelSideWidth, fYOrigin - fHeight, -0.01f);
		gl.glEnd();

		// Right background piece
		gl.glBegin(GL.GL_POLYGON);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(fXOrigin + fWidth, fYOrigin - fHeight, -0.01f);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(fXOrigin + fWidth, fYOrigin + fHeight, -0.01f);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(fXOrigin + fWidth + fPanelSideWidth, fYOrigin + fHeight, -0.01f);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(fXOrigin + fWidth + fPanelSideWidth, fYOrigin - fHeight, -0.01f);
		gl.glEnd();

		tempTexture.disable();

		// Center background piece
		tempTexture = iconTextureManager.getIconTexture(gl,
				EIconTextures.PANEL_SELECTION_CENTER);
		tempTexture.enable();
		tempTexture.bind();

		texCoords = tempTexture.getImageTexCoords();

		gl.glBegin(GL.GL_POLYGON);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(fXOrigin + fPanelSideWidth + 0.1f, fYOrigin - fHeight, -0.01f);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(fXOrigin + fPanelSideWidth + 0.1f, fYOrigin + fHeight, -0.01f);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(fXOrigin + fWidth, fYOrigin + fHeight, -0.01f);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(fXOrigin + fWidth, fYOrigin - fHeight, -0.01f);
		gl.glEnd();

		tempTexture.disable();

		gl.glPopName();
		gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.BUCKET_REMOVE_ICON_SELECTION, iMouseOverObjectID));

		renderRemoveButton(gl, fXOrigin + 2.5f, fYOrigin + 1f, 1.8f);

		gl.glPopName();
		gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.VIEW_SELECTION,
				iMouseOverObjectID));
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

		// generalManager.getLogger().log(Level.INFO,
		// "Slerp action running from "
		// + slerpAction.getOriginHierarchyLevel() + ": " +
		// slerpAction.getOriginPosIndex()
		// + " to " + slerpAction.getDestinationHierarchyLevel() + ": " +
		// slerpAction.getDestinationPosIndex());

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

			// // RemoteElementManager destinationLayer =
			// slerpAction.getDestinationHierarchyLevel();
			// // destinationLayer.setElementVisibilityById(true, iViewID);
			//
			// AGLEventListener glActiveSubView =
			// GeneralManager.get().getViewGLCanvasManager()
			// .getGLEventListener(iViewID);
			//
			// // Update detail level of moved view when slerp action is
			// finished;
			// if (destinationLayer.equals(focusLevel))
			// {
			// if (bucketMouseWheelListener.isZoomedIn() || layoutRenderStyle
			// instanceof ListLayoutRenderStyle)
			// glActiveSubView.setDetailLevel(EDetailLevel.HIGH);
			// else
			// glActiveSubView.setDetailLevel(EDetailLevel.MEDIUM);
			//				
			// if (glActiveSubView instanceof GLPathway)
			// {
			// ((GLPathway)glActiveSubView).enableTitleRendering(true);
			// ((GLPathway)glActiveSubView).setAlignment(SWT.CENTER,
			// SWT.BOTTOM);
			// }
			//
			// generalManager.getGUIBridge().setActiveGLSubView(this,
			// glActiveSubView);
			// }
			// else if (destinationLayer.equals(stackLevel))
			// {
			// glActiveSubView.setDetailLevel(EDetailLevel.LOW);
			//				
			// if (glActiveSubView instanceof GLPathway)
			// {
			// ((GLPathway)glActiveSubView).enableTitleRendering(true);
			//					
			// int iStackPos = stackLevel.getPositionIndexByElementID(iViewID);
			// switch (iStackPos)
			// {
			// case 0:
			// ((GLPathway)glActiveSubView).setAlignment(SWT.CENTER, SWT.TOP);
			// break;
			// case 1:
			// ((GLPathway)glActiveSubView).setAlignment(SWT.LEFT, SWT.BOTTOM);
			// break;
			// case 2:
			// ((GLPathway)glActiveSubView).setAlignment(SWT.CENTER,
			// SWT.BOTTOM);
			// break;
			// case 3:
			// ((GLPathway)glActiveSubView).setAlignment(SWT.RIGHT, SWT.BOTTOM);
			// break;
			// default:
			// break;
			// }
			// }
			// }
			// else if (destinationLayer.equals(poolLevel) ||
			// destinationLayer.equals(selectionLevel))
			// {
			// glActiveSubView.setDetailLevel(EDetailLevel.VERY_LOW);
			//				
			// if (glActiveSubView instanceof GLPathway)
			// {
			// ((GLPathway)glActiveSubView).enableTitleRendering(false);
			// }
			// }
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

	private void loadViewToFocusLevel(final int iRemoteLevelElementID)
	{
		RemoteLevelElement element = RemoteElementManager.get().getItem(iRemoteLevelElementID);

		// Check if other slerp action is currently running
		if (iSlerpFactor > 0 && iSlerpFactor < SLERP_RANGE)
			return;

		arSlerpActions.clear();

		int iViewID = element.getContainedElementID();

		// Only broadcast elements if view is moved from pool to bucket
		if (poolLevel.containsElement(element))
		{
			generalManager.getViewGLCanvasManager().getGLEventListener(iViewID)
					.broadcastElements(ESelectionType.ADD);
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
					freeStackElement = stackLevel.getElementByPositionIndex(1);

					// Slerp view from stack to pool
					SlerpAction reverseSlerpAction = new SlerpAction(freeStackElement,
							poolLevel.getNextFree());
					arSlerpActions.add(reverseSlerpAction);

					// Unregister all elements of the view that is moved out
					generalManager.getViewGLCanvasManager().getGLEventListener(
							freeStackElement.getContainedElementID()).broadcastElements(
							ESelectionType.REMOVE);
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

	@Override
	public synchronized void handleUpdate(IUniqueObject eventTrigger,
			ISelectionDelta selectionDelta, Collection<SelectionCommand> colSelectionCommand,
			EMediatorType eMediatorType)
	{

		generalManager.getLogger().log(
				Level.INFO,
				"Update called by " + eventTrigger.getClass().getSimpleName()
						+ ", received in: " + this.getClass().getSimpleName());

		// Special case for empty update that pathway sends for updating info
		// area
		if (!selectionDelta.iterator().hasNext())
			return;

		// Handle incoming genes
		if (selectionDelta.getIDType() == EIDType.DAVID)
		{
			lastSelectionDelta = selectionDelta;

			// PATHWAY LOADING OF DEPENDENT PATHWAYS
			int iDavidID = 0;
			int iGraphItemID = 0;
			ArrayList<ICaleydoGraphItem> alPathwayVertexGraphItem = new ArrayList<ICaleydoGraphItem>();

			for (SelectionItem item : selectionDelta)
			{
				// Only consider items that are selected
				if (item.getSelectionType() != ESelectionType.SELECTION)
					continue;

				iDavidID = item.getSelectionID();

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
		// Handle incoming pathways
		else if (selectionDelta.getIDType() == EIDType.PATHWAY)
		{
			Iterator<SelectionItem> iterator = selectionDelta.getSelectionData().iterator();
			if (iterator.hasNext())
				addPathwayView(iterator.next().getSelectionID());
			else
				throw new IllegalStateException("Illegals selection delta state: no pathways");
		}
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
		generalManager.getViewGLCanvasManager().getPickingManager().enablePicking(false);

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
			case REMOTE_LEVEL_ELEMENT:
				switch (pickingMode)
				{
					case MOUSE_OVER:
					case DRAGGED:
						iMouseOverObjectID = iExternalID;
						break;
					case CLICKED:

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

						generalManager.getViewGLCanvasManager().getInfoAreaManager()
								.setDataAboutView(iExternalID);

						setDisplayListDirty();

						int iGLEventListenerID = iExternalID;

						if (iGLEventListenerID != -1)
						{
							generalManager.getEventPublisher().triggerUpdate(
									EMediatorType.VIEW_SELECTION,
									generalManager.getViewGLCanvasManager()
											.getGLEventListener(iGLEventListenerID),
									new SelectionDelta(EIDType.DAVID), null);
						}

						break;

					case CLICKED:

						generalManager.getViewGLCanvasManager().getInfoAreaManager()
								.setDataAboutView(iExternalID);

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

						break;
				}

				pickingManager.flushHits(iUniqueID, EPickingType.BUCKET_DRAG_ICON_SELECTION);

				break;

			case BUCKET_REMOVE_ICON_SELECTION:

				switch (pickingMode)
				{
					case CLICKED:

						RemoteLevelElement element = RemoteElementManager.get().getItem(
								iExternalID);

						clearView(element.getContainedElementID());
						element.setContainedElementID(-1);
						break;
				}

				pickingManager.flushHits(iUniqueID, EPickingType.BUCKET_REMOVE_ICON_SELECTION);

				break;

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
								ESelectionType.REMOVE);

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

		focusLevel = layoutRenderStyle.initUnderInteractionLevel();
		stackLevel = layoutRenderStyle.initStackLevel(bucketMouseWheelListener.isZoomedIn());
		poolLevel = layoutRenderStyle.initPoolLevel(-1);
		selectionLevel = layoutRenderStyle.initMemoLevel();
		transitionLevel = layoutRenderStyle.initTransitionLevel();
		spawnLevel = layoutRenderStyle.initSpawnLevel();

		viewFrustum.setProjectionMode(layoutRenderStyle.getProjectionMode());

		// Trigger reshape to apply new projection mode
		// Is there a better way to achieve this? :)
		parentGLCanvas.setSize(parentGLCanvas.getWidth(), parentGLCanvas.getHeight());
	}

	/**
	 * Unregister view from event system. Remove view from GL render loop.
	 */
	private void clearView(int iViewID)
	{
		GLEventListener glEventListener = ((AGLEventListener) generalManager
				.getViewGLCanvasManager().getGLEventListener(iViewID));

		if (glEventListener instanceof IMediatorSender)
		{
			generalManager.getEventPublisher().removeSenderFromAllGroups(
					(IMediatorSender) glEventListener);
		}

		if (glEventListener instanceof IMediatorReceiver)
		{
			generalManager.getEventPublisher().removeReceiverFromAllGroups(
					(IMediatorReceiver) glEventListener);
		}

		generalManager.getViewGLCanvasManager().getConnectedElementRepresentationManager()
				.clearAll();

		for (AGLEventListener eventListener : generalManager.getViewGLCanvasManager()
				.getAllGLEventListeners())
		{
			if (!eventListener.isRenderedRemote())
				eventListener.enableBusyMode(false);
		}
		generalManager.getViewGLCanvasManager().unregisterGLEventListener(iViewID);

		glEventListener = null;
	}

	public synchronized void clearAll()
	{
		// iAlUninitializedPathwayIDs.clear();
		// arSlerpActions.clear();
		//		
		// generalManager.getPathwayManager().resetPathwayVisiblityState();
		//		
		// // Remove all pathway views
		// int iGLEventListenerId = -1;
		//
		// ArrayList<GLEventListener> alGLEventListenerToRemove = new
		// ArrayList<GLEventListener>();
		//
		// for (GLEventListener tmpGLEventListenerToRemove : generalManager
		// .getViewGLCanvasManager().getAllGLEventListeners())
		// {
		// iGLEventListenerId = ((AGLEventListener)
		// tmpGLEventListenerToRemove).getID();
		//
		// if (tmpGLEventListenerToRemove instanceof GLPathway)
		// {
		// if (poolLevel.containsElement(iGLEventListenerId))
		// poolLevel.removeElement(iGLEventListenerId);
		// else if (stackLevel.containsElement(iGLEventListenerId))
		// stackLevel.removeElement(iGLEventListenerId);
		// else if (focusLevel.containsElement(iGLEventListenerId))
		// focusLevel.removeElement(iGLEventListenerId);
		// else if (selectionLevel.containsElement(iGLEventListenerId))
		// selectionLevel.removeElement(iGLEventListenerId);
		// else if (transitionLevel.containsElement(iGLEventListenerId))
		// transitionLevel.removeElement(iGLEventListenerId);
		// else if (spawnLevel.containsElement(iGLEventListenerId))
		// spawnLevel.removeElement(iGLEventListenerId);
		//
		// alGLEventListenerToRemove.add(tmpGLEventListenerToRemove);
		// }
		// else if (tmpGLEventListenerToRemove instanceof GLHeatMap
		// || tmpGLEventListenerToRemove instanceof GLParallelCoordinates)
		// {
		// // Remove all elements from heatmap and parallel coordinates
		// ((AStorageBasedView) tmpGLEventListenerToRemove).resetView();
		// }
		// }
		//
		// for (int iGLEventListenerIndex = 0; iGLEventListenerIndex <
		// alGLEventListenerToRemove
		// .size(); iGLEventListenerIndex++)
		// {
		// clearView(iGLEventListenerIndex);
		// }
		//
		// generalManager.getViewGLCanvasManager().getConnectedElementRepresentationManager()
		// .clear();
		//		
		// for (AGLEventListener eventListener : generalManager
		// .getViewGLCanvasManager().getAllGLEventListeners())
		// {
		// if (!eventListener.isRenderedRemote())
		// eventListener.enableBusyMode(false);
		// }
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

		layoutRenderStyle.initUnderInteractionLevel();
		layoutRenderStyle.initStackLevel(bucketMouseWheelListener.isZoomedIn());
		layoutRenderStyle.initPoolLevel(iMouseOverObjectID);
		layoutRenderStyle.initMemoLevel();
	}

	protected void renderPoolAndMemoLayerBackground(final GL gl)
	{
		// Pool layer background

		float fWidth = 0.8f;

		if (layoutMode.equals(LayoutMode.BUCKET))
		{
			gl.glColor4f(0.85f, 0.85f, 0.85f, 1f);
			gl.glLineWidth(1);
			gl.glBegin(GL.GL_POLYGON);
			gl.glVertex3f(-2 / fAspectRatio, -2, 4);
			gl.glVertex3f(-2 / fAspectRatio, 2, 4);
			gl.glVertex3f(-2 / fAspectRatio + fWidth, 2, 4);
			gl.glVertex3f(-2 / fAspectRatio + fWidth, -2, 4);
			gl.glEnd();

			gl.glColor4f(0.4f, 0.4f, 0.4f, 1);
			gl.glLineWidth(1);
			gl.glBegin(GL.GL_LINE_LOOP);
			gl.glVertex3f(-2 / fAspectRatio, -2, 4);
			gl.glVertex3f(-2 / fAspectRatio, 2, 4);
			gl.glVertex3f(-2 / fAspectRatio + fWidth, 2, 4);
			gl.glVertex3f(-2 / fAspectRatio + fWidth, -2, 4);
			gl.glEnd();

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
		textRenderer.setColor(0.7f, 0.7f, 0.7f, 1.0f);
		textRenderer.draw3D(sTmp, -1.95f / fAspectRatio, -1.95f, 4.001f, 0.004f);
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
	public synchronized void triggerUpdate(EMediatorType eMediatorType,
			ISelectionDelta selectionDelta, Collection<SelectionCommand> colSelectionCommand)
	{
		generalManager.getEventPublisher().triggerUpdate(eMediatorType, this, selectionDelta,
				colSelectionCommand);
	}

	@Override
	public synchronized void broadcastElements(ESelectionType type)
	{
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

				// Trigger last delta to new pathways
				if (lastSelectionDelta != null)
					triggerUpdate(EMediatorType.SELECTION_MEDIATOR, lastSelectionDelta, null);

				if (focusLevel.hasFreePosition())
				{
					spawnLevel.getElementByPositionIndex(0).setContainedElementID(
							iGeneratedViewID);
					SlerpAction slerpActionTransition = new SlerpAction(spawnLevel
							.getElementByPositionIndex(0), focusLevel.getNextFree());
					arSlerpActions.add(slerpActionTransition);

					glPathway.initRemote(gl, iUniqueID, focusLevel,
							pickingTriggerMouseAdapter, this);
					glPathway.setDetailLevel(EDetailLevel.MEDIUM);

					// Trigger initial gene propagation
					glPathway.broadcastElements(ESelectionType.ADD);
				}
				else if (stackLevel.hasFreePosition()
						&& !(layoutRenderStyle instanceof ListLayoutRenderStyle))
				{
					spawnLevel.getElementByPositionIndex(0).setContainedElementID(
							iGeneratedViewID);
					SlerpAction slerpActionTransition = new SlerpAction(spawnLevel
							.getElementByPositionIndex(0), stackLevel.getNextFree());
					arSlerpActions.add(slerpActionTransition);

					glPathway.initRemote(gl, iUniqueID, stackLevel,
							pickingTriggerMouseAdapter, this);
					glPathway.setDetailLevel(EDetailLevel.LOW);

					// Trigger initial gene propagation
					glPathway.broadcastElements(ESelectionType.ADD);
				}
				else if (poolLevel.hasFreePosition())
				{
					spawnLevel.getElementByPositionIndex(0).setContainedElementID(
							iGeneratedViewID);
					SlerpAction slerpActionTransition = new SlerpAction(spawnLevel
							.getElementByPositionIndex(0), poolLevel.getNextFree());
					arSlerpActions.add(slerpActionTransition);

					glPathway.initRemote(gl, iUniqueID, poolLevel, pickingTriggerMouseAdapter,
							this);
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

	public int getMediatorID()
	{
		return iMediatorID;
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
}
