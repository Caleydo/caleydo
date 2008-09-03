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
import org.caleydo.core.command.event.CmdEventCreateMediator;
import org.caleydo.core.command.view.opengl.CmdCreateGLPathway;
import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.graph.ICaleydoGraphItem;
import org.caleydo.core.data.graph.pathway.core.PathwayGraph;
import org.caleydo.core.data.graph.pathway.item.vertex.PathwayVertexGraphItem;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.ISelectionDelta;
import org.caleydo.core.data.selection.SelectionItem;
import org.caleydo.core.data.view.camera.EProjectionMode;
import org.caleydo.core.data.view.camera.IViewFrustum;
import org.caleydo.core.data.view.rep.renderstyle.layout.ARemoteViewLayoutRenderStyle;
import org.caleydo.core.data.view.rep.renderstyle.layout.BucketLayoutRenderStyle;
import org.caleydo.core.data.view.rep.renderstyle.layout.JukeboxLayoutRenderStyle;
import org.caleydo.core.data.view.rep.renderstyle.layout.ARemoteViewLayoutRenderStyle.LayoutMode;
import org.caleydo.core.manager.IEventPublisher.MediatorType;
import org.caleydo.core.manager.event.mediator.IMediatorReceiver;
import org.caleydo.core.manager.event.mediator.IMediatorSender;
import org.caleydo.core.manager.event.mediator.MediatorUpdateType;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.slerp.SlerpAction;
import org.caleydo.core.util.slerp.SlerpMod;
import org.caleydo.core.util.system.SystemTime;
import org.caleydo.core.util.system.Time;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.pathway.GLPathway;
import org.caleydo.core.view.opengl.canvas.remote.bucket.BucketMouseWheelListener;
import org.caleydo.core.view.opengl.canvas.remote.bucket.GLConnectionLineRendererBucket;
import org.caleydo.core.view.opengl.canvas.remote.jukebox.GLConnectionLineRendererJukebox;
import org.caleydo.core.view.opengl.canvas.storagebased.AStorageBasedView;
import org.caleydo.core.view.opengl.canvas.storagebased.heatmap.GLHeatMap;
import org.caleydo.core.view.opengl.canvas.storagebased.parcoords.GLParallelCoordinates;
import org.caleydo.core.view.opengl.miniview.GLColorMappingBarMiniView;
import org.caleydo.core.view.opengl.mouse.PickingJoglMouseListener;
import org.caleydo.core.view.opengl.util.EIconTextures;
import org.caleydo.core.view.opengl.util.GLIconTextureManager;
import org.caleydo.core.view.opengl.util.drag.GLDragAndDrop;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteHierarchyLayer;
import org.caleydo.core.view.opengl.util.trashcan.TrashCan;
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
	implements IMediatorReceiver, IMediatorSender, IGLCanvasRemoteRendering3D
{

	private ARemoteViewLayoutRenderStyle.LayoutMode layoutMode;

	private static final int SLERP_RANGE = 1000;

	private static final int SLERP_SPEED = 1200;

	protected int iMouseOverViewID = -1;

	protected RemoteHierarchyLayer underInteractionLayer;

	protected RemoteHierarchyLayer stackLayer;

	protected RemoteHierarchyLayer poolLayer;

	protected RemoteHierarchyLayer transitionLayer;

	protected RemoteHierarchyLayer spawnLayer;

	protected RemoteHierarchyLayer memoLayer;

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
	private GLIconTextureManager glIconTextureManager;

	private ArrayList<Integer> iAlUninitializedPathwayIDs;

	private int iMediatorID = -1;

	// Memo pad variables
	private static final int MEMO_PAD_PICKING_ID = 1;

	private static final int TRASH_CAN_PICKING_ID = 2;

	protected TextRenderer textRenderer;

	private GLDragAndDrop dragAndDrop;

	private ARemoteViewLayoutRenderStyle layoutRenderStyle;

	private BucketMouseWheelListener bucketMouseWheelListener;

	private TrashCan trashCan;

	private GLColorMappingBarMiniView colorMappingBarMiniView;

	/**
	 * When the system is in the busy mode the background color will turn yellow
	 * and the system interaction will be turned off.
	 */
	private boolean bBusyMode = false;

	private boolean bBusyModeChanged = false;
	
	private ArrayList<Integer> iAlContainedViewIDs;
	

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

			bucketMouseWheelListener = new BucketMouseWheelListener(this,
					(BucketLayoutRenderStyle) layoutRenderStyle);

			// Unregister standard mouse wheel listener
			parentGLCanvas.removeMouseWheelListener(pickingTriggerMouseAdapter);
			// Register specialized bucket mouse wheel listener
			parentGLCanvas.addMouseWheelListener(bucketMouseWheelListener);

		}
		else if (layoutMode.equals(ARemoteViewLayoutRenderStyle.LayoutMode.JUKEBOX))
		{
			layoutRenderStyle = new JukeboxLayoutRenderStyle(viewFrustum);
		}

		underInteractionLayer = layoutRenderStyle.initUnderInteractionLayer();
		stackLayer = layoutRenderStyle.initStackLayer();
		poolLayer = layoutRenderStyle.initPoolLayer(-1);
		memoLayer = layoutRenderStyle.initMemoLayer();
		transitionLayer = layoutRenderStyle.initTransitionLayer();
		spawnLayer = layoutRenderStyle.initSpawnLayer();

		if (layoutMode.equals(ARemoteViewLayoutRenderStyle.LayoutMode.BUCKET))
		{
			glConnectionLineRenderer = new GLConnectionLineRendererBucket(
					underInteractionLayer, stackLayer, poolLayer);
		}
		else if (layoutMode.equals(ARemoteViewLayoutRenderStyle.LayoutMode.JUKEBOX))
		{
			glConnectionLineRenderer = new GLConnectionLineRendererJukebox(
					underInteractionLayer, stackLayer, poolLayer);
		}

		pickingTriggerMouseAdapter.addGLCanvas(this);

		arSlerpActions = new ArrayList<SlerpAction>();

		iAlUninitializedPathwayIDs = new ArrayList<Integer>();

		createEventMediator();

		dragAndDrop = new GLDragAndDrop();

		textRenderer = new TextRenderer(new Font("Arial", Font.BOLD, 24), false);

		trashCan = new TrashCan();

		// TODO: the genome mapper should be stored centralized instead of newly
		// created
		colorMappingBarMiniView = new GLColorMappingBarMiniView(viewFrustum);
	}

	@Override
	public void initLocal(final GL gl)
	{

		init(gl);
	}

	@Override
	public void initRemote(final GL gl, final int iRemoteViewID,
			final RemoteHierarchyLayer layer,
			final PickingJoglMouseListener pickingTriggerMouseAdapter,
			final IGLCanvasRemoteRendering3D remoteRenderingGLCanvas)
	{

		throw new IllegalStateException("Not implemented to be rendered remote");
	}

	@Override
	public void init(final GL gl)
	{

		glIconTextureManager = new GLIconTextureManager(gl);

		time = new SystemTime();
		((SystemTime) time).rebase();

		initializeContainedViews(gl);

		trashCan.init(gl);

		colorMappingBarMiniView.setWidth(layoutRenderStyle.getColorBarWidth());
		colorMappingBarMiniView.setHeight(layoutRenderStyle.getColorBarHeight());
	}

	@Override
	public void displayLocal(final GL gl)
	{

		if (pickingTriggerMouseAdapter.wasRightMouseButtonPressed())
		{
			bEnableNavigationOverlay = !bEnableNavigationOverlay;

			glConnectionLineRenderer.enableRendering(!bEnableNavigationOverlay);
		}

		pickingManager.handlePicking(iUniqueID, gl, true);

		display(gl);

		if (pickingTriggerMouseAdapter.getPickedPoint() != null)
			dragAndDrop.setCurrentMousePos(gl, pickingTriggerMouseAdapter.getPickedPoint());

		if (pickingTriggerMouseAdapter.wasMouseReleased())
			dragAndDrop.stopDragAction();

		checkForHits(gl);

		pickingTriggerMouseAdapter.resetEvents();
		// gl.glCallList(iGLDisplayListIndexLocal);
	}

	@Override
	public void displayRemote(final GL gl)
	{

		display(gl);
	}

	@Override
	public void display(final GL gl)
	{

		time.update();

		layoutRenderStyle.initPoolLayer(iMouseOverViewID);
		// layoutRenderStyle.initMemoLayer();
		// layoutRenderStyle.initStackLayer();
		// layoutRenderStyle.initTransitionLayer();
		// layoutRenderStyle.initUnderInteractionLayer();

		if (bBusyModeChanged)
			updateBusyMode(gl);

		doSlerpActions(gl);
		initializeNewPathways(gl);

		renderLayer(gl, underInteractionLayer);

		// If user zooms to the bucket bottom all but the under
		// interaction layer is _not_ rendered.
		if (bucketMouseWheelListener == null || !bucketMouseWheelListener.isZoomedIn())
		{	
			// if
			// (layoutMode.equals(ARemoteViewLayoutRenderStyle.LayoutMode.BUCKET
			// ))
			// {
			renderPoolAndMemoLayerBackground(gl);

			// gl.glPushName(generalManager.getViewGLCanvasManager().
			// getPickingManager()
			// .getPickingID(iUniqueID, EPickingType.MEMO_PAD_SELECTION,
			// MEMO_PAD_PICKING_ID));
			// gl.glPopName();
			// }

			renderLayer(gl, poolLayer);
			renderLayer(gl, transitionLayer);
			renderLayer(gl, stackLayer);
			renderLayer(gl, spawnLayer);
			renderLayer(gl, memoLayer);

			glConnectionLineRenderer.render(gl);
		}

		if (layoutMode.equals(ARemoteViewLayoutRenderStyle.LayoutMode.BUCKET))
		{
			bucketMouseWheelListener.render();
		}

		colorMappingBarMiniView.render(gl, layoutRenderStyle.getColorBarXPos(),
				layoutRenderStyle.getColorBarYPos(), 4);
	}

	public void setInitialContainedViews(ArrayList<Integer> iAlInitialContainedViewIDs)
	{
		iAlContainedViewIDs = iAlInitialContainedViewIDs;
	}
	
	private void initializeContainedViews(final GL gl)
	{
		for (int iContainedViewID : iAlContainedViewIDs)
		{
			AGLEventListener tmpGLEventListener = 
				generalManager.getViewGLCanvasManager().getGLEventListener(iContainedViewID);
			
			// Ignore pathway views upon startup
			// because they will be activated when pathway loader thread has
			// finished
			if (tmpGLEventListener == this || tmpGLEventListener instanceof GLPathway)
			{
				continue;
			}

			int iViewID = (tmpGLEventListener).getID();

			if (underInteractionLayer.containsElement(-1))
			{
				underInteractionLayer.addElement(iViewID);
				underInteractionLayer.setElementVisibilityById(true, iViewID);

				tmpGLEventListener.initRemote(gl, iUniqueID, underInteractionLayer,
						pickingTriggerMouseAdapter, this);

				tmpGLEventListener.broadcastElements(ESelectionType.NORMAL);
				tmpGLEventListener.setDetailLevel(EDetailLevel.MEDIUM);

				generalManager.getGUIBridge().setActiveGLSubView(this, tmpGLEventListener);

			}
			else if (stackLayer.containsElement(-1))
			{
				stackLayer.addElement(iViewID);
				stackLayer.setElementVisibilityById(true, iViewID);

				tmpGLEventListener.initRemote(gl, iUniqueID, stackLayer,
						pickingTriggerMouseAdapter, this);

				tmpGLEventListener.broadcastElements(ESelectionType.NORMAL);
				tmpGLEventListener.setDetailLevel(EDetailLevel.LOW);
			}
			else if (poolLayer.containsElement(-1))
			{
				poolLayer.addElement(iViewID);
				poolLayer.setElementVisibilityById(true, iViewID);

				tmpGLEventListener.initRemote(gl, iUniqueID, poolLayer,
						pickingTriggerMouseAdapter, this);
				tmpGLEventListener.setDetailLevel(EDetailLevel.VERY_LOW);
			}

			// pickingTriggerMouseAdapter.addGLCanvas(tmpGLEventListener);
			pickingManager.getPickingID(iUniqueID, EPickingType.VIEW_SELECTION, iViewID);

			ArrayList<Integer> arMediatorIDs = new ArrayList<Integer>();
			arMediatorIDs.add(iViewID);
			generalManager.getEventPublisher().addSendersAndReceiversToMediator(
					generalManager.getEventPublisher().getItem(iMediatorID),
					arMediatorIDs, arMediatorIDs, MediatorType.SELECTION_MEDIATOR,
					MediatorUpdateType.MEDIATOR_DEFAULT);
		}
	}

	private void renderBucketWall(final GL gl)
	{
		gl.glColor4f(0.4f, 0.4f, 0.4f, 1f);
		gl.glLineWidth(4);

		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(0, 0, -0.02f);
		gl.glVertex3f(0, 8, -0.02f);
		gl.glVertex3f(8, 8, -0.02f);
		gl.glVertex3f(8, 0, -0.02f);
		gl.glEnd();

		gl.glColor4f(0.96f, 0.96f, 0.96f, 1f);

		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(0, 0, -0.02f);
		gl.glVertex3f(0, 8, -0.02f);
		gl.glVertex3f(8, 8, -0.02f);
		gl.glVertex3f(8, 0, -0.02f);
		gl.glEnd();
	}

	private void renderLayer(final GL gl, final RemoteHierarchyLayer layer)
	{
		Iterator<Integer> iterElementList = layer.getElementList().iterator();
		int iViewId = 0;
		int iLayerPositionIndex = 0;

		while (iterElementList.hasNext())
		{
			iViewId = iterElementList.next();

			// Check if spot in layer is currently empty
			if (iViewId != -1)
			{
				gl.glPushName(pickingManager.getPickingID(iUniqueID,
						EPickingType.VIEW_SELECTION, iViewId));
				renderViewByID(gl, iViewId, layer);
				gl.glPopName();
			}
			else
				renderEmptyBucketWall(gl, layer, iLayerPositionIndex);

			iLayerPositionIndex++;
		}
	}

	private void renderViewByID(final GL gl, final int iViewID,
			final RemoteHierarchyLayer layer)
	{
		// Check if view is visible
		if (!layer.getElementVisibilityById(iViewID))
			return;

		AGLEventListener tmpCanvasUser = (generalManager.getViewGLCanvasManager()
				.getGLEventListener(iViewID));

		if (tmpCanvasUser == null)
			throw new CaleydoRuntimeException("Cannot render canvas object which is null!");

		gl.glPushMatrix();

		Transform transform = layer.getTransformByElementId(iViewID);
		Vec3f translation = transform.getTranslation();
		Rotf rot = transform.getRotation();
		Vec3f scale = transform.getScale();
		Vec3f axis = new Vec3f();
		float fAngle = rot.get(axis);

		gl.glTranslatef(translation.x(), translation.y(), translation.z());
		gl.glScalef(scale.x(), scale.y(), scale.z());
		gl.glRotatef(Vec3f.convertRadiant2Grad(fAngle), axis.x(), axis.y(), axis.z());

		// if (layer.equals(underInteractionLayer) || layer.equals(stackLayer))
		// {
		// renderBucketWall(gl);
		// }

		if (layer.equals(poolLayer))
		{
			String sRenderText = tmpCanvasUser.getInfo().get(1);

			// Limit pathway name in length
			if (sRenderText.length() > 16)
				sRenderText = sRenderText.subSequence(0, 13) + "...";

			textRenderer.begin3DRendering();
			textRenderer.setColor(0, 0, 0, 1);
			textRenderer.draw3D(sRenderText, 8.5f, 3, 0, 0.1f); // scale factor
			textRenderer.end3DRendering();
		}

		// GLHelperFunctions.drawAxis(gl);

		renderBucketWall(gl);

		tmpCanvasUser.displayRemote(gl);

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

		if (layer.equals(stackLayer))
		{
			renderNavigationOverlay(gl, iViewID);
		}

		gl.glPopMatrix();
	}

	public void renderEmptyBucketWall(final GL gl, final RemoteHierarchyLayer layer,
			final int iLayerPositionIndex)
	{

		gl.glPushMatrix();

		Transform transform = layer.getTransformByPositionIndex(iLayerPositionIndex);
		Vec3f translation = transform.getTranslation();
		Rotf rot = transform.getRotation();
		Vec3f scale = transform.getScale();
		Vec3f axis = new Vec3f();
		float fAngle = rot.get(axis);

		gl.glTranslatef(translation.x(), translation.y(), translation.z());
		gl.glScalef(scale.x(), scale.y(), scale.z());
		gl.glRotatef(Vec3f.convertRadiant2Grad(fAngle), axis.x(), axis.y(), axis.z());

		if (!layer.equals(transitionLayer) && !layer.equals(spawnLayer)
				&& !layer.equals(poolLayer))
		{
			renderBucketWall(gl);
		}

		gl.glPopMatrix();
	}

	private void renderNavigationOverlay(final GL gl, final int iViewID)
	{

		if (!bEnableNavigationOverlay)
			return;

		glConnectionLineRenderer.enableRendering(false);

		EPickingType leftWallPickingType = null;
		EPickingType rightWallPickingType = null;
		EPickingType topWallPickingType = null;
		EPickingType bottomWallPickingType = null;

		Vec4f tmpColor_out = new Vec4f(0.9f, 0.9f, 0.9f, 0.9f);
		Vec4f tmpColor_in = new Vec4f(0.9f, 0.9f, 0.9f, 0.9f);
		Vec4f tmpColor_left = new Vec4f(0.9f, 0.9f, 0.9f, 0.9f);
		Vec4f tmpColor_right = new Vec4f(0.9f, 0.9f, 0.9f, 0.9f);
		Vec4f tmpColor_lock = new Vec4f(0.9f, 0.9f, 0.9f, 0.9f);

		Texture textureLock = glIconTextureManager.getIconTexture(EIconTextures.LOCK);
		Texture textureMoveLeft = null;
		Texture textureMoveRight = null;
		Texture textureMoveOut = null;
		Texture textureMoveIn = null;

		TextureCoords texCoords = textureLock.getImageTexCoords();

		if (iNavigationMouseOverViewID_lock == iViewID)
			tmpColor_lock.set(1, 0.3f, 0.3f, 0.9f);

		if (layoutMode.equals(LayoutMode.JUKEBOX))
		{
			topWallPickingType = EPickingType.BUCKET_MOVE_RIGHT_ICON_SELECTION;
			bottomWallPickingType = EPickingType.BUCKET_MOVE_LEFT_ICON_SELECTION;
			leftWallPickingType = EPickingType.BUCKET_MOVE_OUT_ICON_SELECTION;
			rightWallPickingType = EPickingType.BUCKET_MOVE_IN_ICON_SELECTION;

			if (iNavigationMouseOverViewID_out == iViewID)
				tmpColor_left.set(1, 0.3f, 0.3f, 0.9f);
			else if (iNavigationMouseOverViewID_in == iViewID)
				tmpColor_right.set(1, 0.3f, 0.3f, 0.9f);
			else if (iNavigationMouseOverViewID_left == iViewID)
				tmpColor_in.set(1, 0.3f, 0.3f, 0.9f);
			else if (iNavigationMouseOverViewID_right == iViewID)
				tmpColor_out.set(1, 0.3f, 0.3f, 0.9f);

			textureMoveIn = glIconTextureManager.getIconTexture(EIconTextures.ARROW_LEFT);
			textureMoveOut = glIconTextureManager.getIconTexture(EIconTextures.ARROW_DOWN);
			textureMoveLeft = glIconTextureManager.getIconTexture(EIconTextures.ARROW_DOWN);
			textureMoveRight = glIconTextureManager.getIconTexture(EIconTextures.ARROW_LEFT);
		}
		else
		{
			if (stackLayer.getPositionIndexByElementId(iViewID) == 0) // top
			{
				topWallPickingType = EPickingType.BUCKET_MOVE_OUT_ICON_SELECTION;
				bottomWallPickingType = EPickingType.BUCKET_MOVE_IN_ICON_SELECTION;
				leftWallPickingType = EPickingType.BUCKET_MOVE_LEFT_ICON_SELECTION;
				rightWallPickingType = EPickingType.BUCKET_MOVE_RIGHT_ICON_SELECTION;

				if (iNavigationMouseOverViewID_out == iViewID)
					tmpColor_out.set(1, 0.3f, 0.3f, 0.9f);
				else if (iNavigationMouseOverViewID_in == iViewID)
					tmpColor_in.set(1, 0.3f, 0.3f, 0.9f);
				else if (iNavigationMouseOverViewID_left == iViewID)
					tmpColor_left.set(1, 0.3f, 0.3f, 0.9f);
				else if (iNavigationMouseOverViewID_right == iViewID)
					tmpColor_right.set(1, 0.3f, 0.3f, 0.9f);

				textureMoveIn = glIconTextureManager.getIconTexture(EIconTextures.ARROW_LEFT);
				textureMoveOut = glIconTextureManager.getIconTexture(EIconTextures.ARROW_DOWN);
				textureMoveLeft = glIconTextureManager
						.getIconTexture(EIconTextures.ARROW_DOWN);
				textureMoveRight = glIconTextureManager
						.getIconTexture(EIconTextures.ARROW_LEFT);
			}
			else if (stackLayer.getPositionIndexByElementId(iViewID) == 2) // bottom
			{
				topWallPickingType = EPickingType.BUCKET_MOVE_IN_ICON_SELECTION;
				bottomWallPickingType = EPickingType.BUCKET_MOVE_OUT_ICON_SELECTION;
				leftWallPickingType = EPickingType.BUCKET_MOVE_RIGHT_ICON_SELECTION;
				rightWallPickingType = EPickingType.BUCKET_MOVE_LEFT_ICON_SELECTION;

				if (iNavigationMouseOverViewID_out == iViewID)
					tmpColor_in.set(1, 0.3f, 0.3f, 0.9f);
				else if (iNavigationMouseOverViewID_in == iViewID)
					tmpColor_out.set(1, 0.3f, 0.3f, 0.9f);
				else if (iNavigationMouseOverViewID_left == iViewID)
					tmpColor_right.set(1, 0.3f, 0.3f, 0.9f);
				else if (iNavigationMouseOverViewID_right == iViewID)
					tmpColor_left.set(1, 0.3f, 0.3f, 0.9f);

				textureMoveIn = glIconTextureManager.getIconTexture(EIconTextures.ARROW_LEFT);
				textureMoveOut = glIconTextureManager.getIconTexture(EIconTextures.ARROW_DOWN);
				textureMoveLeft = glIconTextureManager
						.getIconTexture(EIconTextures.ARROW_DOWN);
				textureMoveRight = glIconTextureManager
						.getIconTexture(EIconTextures.ARROW_LEFT);
			}
			else if (stackLayer.getPositionIndexByElementId(iViewID) == 1) // left
			{
				topWallPickingType = EPickingType.BUCKET_MOVE_RIGHT_ICON_SELECTION;
				bottomWallPickingType = EPickingType.BUCKET_MOVE_LEFT_ICON_SELECTION;
				leftWallPickingType = EPickingType.BUCKET_MOVE_OUT_ICON_SELECTION;
				rightWallPickingType = EPickingType.BUCKET_MOVE_IN_ICON_SELECTION;

				if (iNavigationMouseOverViewID_out == iViewID)
					tmpColor_left.set(1, 0.3f, 0.3f, 0.9f);
				else if (iNavigationMouseOverViewID_in == iViewID)
					tmpColor_right.set(1, 0.3f, 0.3f, 0.9f);
				else if (iNavigationMouseOverViewID_left == iViewID)
					tmpColor_in.set(1, 0.3f, 0.3f, 0.9f);
				else if (iNavigationMouseOverViewID_right == iViewID)
					tmpColor_out.set(1, 0.3f, 0.3f, 0.9f);

				textureMoveIn = glIconTextureManager.getIconTexture(EIconTextures.ARROW_LEFT);
				textureMoveOut = glIconTextureManager.getIconTexture(EIconTextures.ARROW_DOWN);
				textureMoveLeft = glIconTextureManager
						.getIconTexture(EIconTextures.ARROW_DOWN);
				textureMoveRight = glIconTextureManager
						.getIconTexture(EIconTextures.ARROW_LEFT);
			}
			else if (stackLayer.getPositionIndexByElementId(iViewID) == 3) // right
			{
				topWallPickingType = EPickingType.BUCKET_MOVE_LEFT_ICON_SELECTION;
				bottomWallPickingType = EPickingType.BUCKET_MOVE_RIGHT_ICON_SELECTION;
				leftWallPickingType = EPickingType.BUCKET_MOVE_IN_ICON_SELECTION;
				rightWallPickingType = EPickingType.BUCKET_MOVE_OUT_ICON_SELECTION;

				if (iNavigationMouseOverViewID_out == iViewID)
					tmpColor_right.set(1, 0.3f, 0.3f, 0.9f);
				else if (iNavigationMouseOverViewID_in == iViewID)
					tmpColor_left.set(1, 0.3f, 0.3f, 0.9f);
				else if (iNavigationMouseOverViewID_left == iViewID)
					tmpColor_out.set(1, 0.3f, 0.3f, 0.9f);
				else if (iNavigationMouseOverViewID_right == iViewID)
					tmpColor_in.set(1, 0.3f, 0.3f, 0.9f);

				textureMoveIn = glIconTextureManager.getIconTexture(EIconTextures.ARROW_LEFT);
				textureMoveOut = glIconTextureManager.getIconTexture(EIconTextures.ARROW_DOWN);
				textureMoveLeft = glIconTextureManager
						.getIconTexture(EIconTextures.ARROW_DOWN);
				textureMoveRight = glIconTextureManager
						.getIconTexture(EIconTextures.ARROW_LEFT);
			}
		}
		// else if (underInteractionLayer.containsElement(iViewID))
		// {
		// topWallPickingType = EPickingType.BUCKET_MOVE_OUT_ICON_SELECTION;
		// bottomWallPickingType =
		// EPickingType.BUCKET_MOVE_RIGHT_ICON_SELECTION;
		// leftWallPickingType = EPickingType.BUCKET_MOVE_IN_ICON_SELECTION;
		// rightWallPickingType = EPickingType.BUCKET_MOVE_OUT_ICON_SELECTION;
		// }

		gl.glLineWidth(4);

		// CENTER - NAVIGATION: LOCK
		gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.BUCKET_LOCK_ICON_SELECTION, iViewID));

		gl.glColor4f(0.5f, 0.5f, 0.5f, 1);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(2.66f, 2.66f, 0.02f);
		gl.glVertex3f(2.66f, 5.33f, 0.02f);
		gl.glVertex3f(5.33f, 5.33f, 0.02f);
		gl.glVertex3f(5.33f, 2.66f, 0.02f);
		gl.glEnd();

		textureLock.enable();
		textureLock.bind();

		gl.glColor4f(tmpColor_lock.x(), tmpColor_lock.y(), tmpColor_lock.z(), tmpColor_lock
				.w());
		gl.glBegin(GL.GL_POLYGON);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(2.66f, 2.66f, 0.03f);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(2.66f, 5.33f, 0.03f);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(5.33f, 5.33f, 0.03f);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(5.33f, 2.66f, 0.03f);
		gl.glEnd();

		textureLock.disable();

		gl.glPopName();

		// BOTTOM - NAVIGATION: MOVE IN
		gl.glPushName(pickingManager.getPickingID(iUniqueID, bottomWallPickingType, iViewID));

		gl.glColor4f(0.5f, 0.5f, 0.5f, 1);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(0, 0, 0.02f);
		gl.glVertex3f(2.66f, 2.66f, 0.02f);
		gl.glVertex3f(5.33f, 2.66f, 0.02f);
		gl.glVertex3f(8, 0, 0.02f);
		gl.glEnd();

		gl.glColor4f(tmpColor_in.x(), tmpColor_in.y(), tmpColor_in.z(), tmpColor_in.w());

		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(0.05f, 0.05f, 0.02f);
		gl.glVertex3f(2.66f, 2.66f, 0.02f);
		gl.glVertex3f(5.33f, 2.66f, 0.02f);
		gl.glVertex3f(7.95f, 0.02f, 0.02f);
		gl.glEnd();

		textureMoveIn.enable();
		textureMoveIn.bind();
		// texCoords = textureMoveIn.getImageTexCoords();
		// gl.glColor4f(1,0.3f,0.3f,0.9f);
		gl.glBegin(GL.GL_POLYGON);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(2.66f, 0.05f, 0.03f);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(2.66f, 2.66f, 0.03f);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(5.33f, 2.66f, 0.03f);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(5.33f, 0.05f, 0.03f);
		gl.glEnd();

		textureMoveIn.disable();

		gl.glPopName();

		// RIGHT - NAVIGATION: MOVE RIGHT
		gl.glPushName(pickingManager.getPickingID(iUniqueID, rightWallPickingType, iViewID));

		gl.glColor4f(0.5f, 0.5f, 0.5f, 1);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(8, 0, 0.02f);
		gl.glVertex3f(5.33f, 2.66f, 0.02f);
		gl.glVertex3f(5.33f, 5.33f, 0.02f);
		gl.glVertex3f(8, 8, 0.02f);
		gl.glEnd();

		gl.glColor4f(tmpColor_right.x(), tmpColor_right.y(), tmpColor_right.z(),
				tmpColor_right.w());

		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(7.95f, 0.05f, 0.02f);
		gl.glVertex3f(5.33f, 2.66f, 0.02f);
		gl.glVertex3f(5.33f, 5.33f, 0.02f);
		gl.glVertex3f(7.95f, 7.95f, 0.02f);
		gl.glEnd();

		textureMoveRight.enable();
		textureMoveRight.bind();

		// gl.glColor4f(0,1,0,1);
		gl.glBegin(GL.GL_POLYGON);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(7.95f, 2.66f, 0.03f);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(5.33f, 2.66f, 0.03f);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(5.33f, 5.33f, 0.03f);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(7.95f, 5.33f, 0.03f);
		gl.glEnd();

		textureMoveRight.disable();

		gl.glPopName();

		// LEFT - NAVIGATION: MOVE LEFT
		gl.glPushName(pickingManager.getPickingID(iUniqueID, leftWallPickingType, iViewID));

		gl.glColor4f(0.5f, 0.5f, 0.5f, 1);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(0, 0, 0.02f);
		gl.glVertex3f(0, 8, 0.02f);
		gl.glVertex3f(2.66f, 5.33f, 0.02f);
		gl.glVertex3f(2.66f, 2.66f, 0.02f);
		gl.glEnd();

		gl.glColor4f(tmpColor_left.x(), tmpColor_left.y(), tmpColor_left.z(), tmpColor_left
				.w());

		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(0.05f, 0.05f, 0.02f);
		gl.glVertex3f(0.05f, 7.95f, 0.02f);
		gl.glVertex3f(2.66f, 5.33f, 0.02f);
		gl.glVertex3f(2.66f, 2.66f, 0.02f);
		gl.glEnd();

		textureMoveLeft.enable();
		textureMoveLeft.bind();

		// gl.glColor4f(0,1,0,1);
		gl.glBegin(GL.GL_POLYGON);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(0.05f, 2.66f, 0.03f);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(0.05f, 5.33f, 0.03f);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(2.66f, 5.33f, 0.03f);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(2.66f, 2.66f, 0.03f);
		gl.glEnd();

		textureMoveLeft.disable();

		gl.glPopName();

		// TOP - NAVIGATION: MOVE OUT
		gl.glPushName(pickingManager.getPickingID(iUniqueID, topWallPickingType, iViewID));

		gl.glColor4f(0.5f, 0.5f, 0.5f, 1);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(0, 8, 0.02f);
		gl.glVertex3f(8, 8, 0.02f);
		gl.glVertex3f(5.33f, 5.33f, 0.02f);
		gl.glVertex3f(2.66f, 5.33f, 0.02f);
		gl.glEnd();

		gl.glColor4f(tmpColor_out.x(), tmpColor_out.y(), tmpColor_out.z(), tmpColor_out.w());
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(0.05f, 7.95f, 0.02f);
		gl.glVertex3f(7.95f, 7.95f, 0.02f);
		gl.glVertex3f(5.33f, 5.33f, 0.02f);
		gl.glVertex3f(2.66f, 5.33f, 0.02f);
		gl.glEnd();

		textureMoveOut.enable();
		textureMoveOut.bind();

		// gl.glColor4f(0,1,0,1);
		gl.glBegin(GL.GL_POLYGON);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(2.66f, 7.95f, 0.03f);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(5.33f, 7.95f, 0.03f);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(5.33f, 5.33f, 0.03f);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(2.66f, 5.33f, 0.03f);
		gl.glEnd();

		textureMoveOut.disable();

		gl.glPopName();
	}

	private void doSlerpActions(final GL gl)
	{

		if (arSlerpActions.isEmpty())
			return;

		SlerpAction tmpSlerpAction = arSlerpActions.get(0);

		if (iSlerpFactor == 0)
		{
			tmpSlerpAction.start();
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

		Transform transform = slerpMod.interpolate(slerpAction.getOriginHierarchyLayer()
				.getTransformByPositionIndex(slerpAction.getOriginPosIndex()), slerpAction
				.getDestinationHierarchyLayer().getTransformByPositionIndex(
						slerpAction.getDestinationPosIndex()), (float) iSlerpFactor
				/ SLERP_RANGE);

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

			RemoteHierarchyLayer destinationLayer = slerpAction.getDestinationHierarchyLayer();
			destinationLayer.setElementVisibilityById(true, iViewID);

			AGLEventListener glActiveSubView = GeneralManager.get().getViewGLCanvasManager()
					.getGLEventListener(iViewID);

			// Update detail level of moved view when slerp action is finished;
			if (destinationLayer.equals(underInteractionLayer))
			{
				glActiveSubView.setDetailLevel(EDetailLevel.MEDIUM);
				generalManager.getGUIBridge().setActiveGLSubView(this, glActiveSubView);
			}
			else if (destinationLayer.equals(stackLayer))
			{
				glActiveSubView.setDetailLevel(EDetailLevel.LOW);
			}
			else if (destinationLayer.equals(poolLayer) || destinationLayer.equals(memoLayer))
			{
				glActiveSubView.setDetailLevel(EDetailLevel.VERY_LOW);
			}
		}

		// After last slerp action is done the line connections are turned on
		// again
		if (arSlerpActions.isEmpty())
		{
			glConnectionLineRenderer.enableRendering(true);

			generalManager.getViewGLCanvasManager().getInfoAreaManager().enable(
					!bEnableNavigationOverlay);
		}
	}

	private void loadViewToUnderInteractionLayer(final int iViewID)
	{
		// Check if other slerp action is currently running
		if (iSlerpFactor > 0 && iSlerpFactor < SLERP_RANGE)
			return;

		arSlerpActions.clear();

		generalManager.getViewGLCanvasManager().getGLEventListener(iViewID).broadcastElements(
				ESelectionType.NORMAL);

		// Check if view is already loaded in the stack layer
		if (stackLayer.containsElement(iViewID))
		{
			// Slerp selected view to under interaction transition position
			SlerpAction slerpActionTransition = new SlerpAction(iViewID, stackLayer,
					transitionLayer);
			arSlerpActions.add(slerpActionTransition);

			if (underInteractionLayer.getElementIdByPositionIndex(0) != -1)
			{
				// Slerp under interaction view to free spot in stack
				SlerpAction reverseSlerpAction = new SlerpAction(underInteractionLayer
						.getElementIdByPositionIndex(0), underInteractionLayer, stackLayer);
				arSlerpActions.add(reverseSlerpAction);
			}

			// Slerp selected view from transition position to under interaction
			// position
			SlerpAction slerpAction = new SlerpAction(iViewID, transitionLayer,
					underInteractionLayer);
			arSlerpActions.add(slerpAction);
		}
		else
		{
			// Slerp selected view to under interaction transition position
			SlerpAction slerpActionTransition = new SlerpAction(iViewID, poolLayer,
					transitionLayer);
			arSlerpActions.add(slerpActionTransition);

			if (!stackLayer.containsElement(-1))
			{
				// Slerp view from stack to pool
				SlerpAction reverseSlerpAction = new SlerpAction(stackLayer
						.getElementIdByPositionIndex(stackLayer.getNextPositionIndex()),
						stackLayer, true);
				arSlerpActions.add(reverseSlerpAction);

				// Unregister all elements of the view that is moved out
				generalManager.getViewGLCanvasManager().getGLEventListener(
						stackLayer.getElementIdByPositionIndex(stackLayer
								.getNextPositionIndex())).broadcastElements(
						ESelectionType.REMOVE);
			}

			// Slerp under interaction view to free spot in stack
			SlerpAction reverseSlerpAction2 = new SlerpAction(underInteractionLayer
					.getElementIdByPositionIndex(0), underInteractionLayer, true);
			arSlerpActions.add(reverseSlerpAction2);

			// Slerp selected view from transition position to under interaction
			// position
			SlerpAction slerpAction = new SlerpAction(iViewID, transitionLayer,
					underInteractionLayer);
			arSlerpActions.add(slerpAction);
		}

		iSlerpFactor = 0;
	}

	@Override
	public void handleUpdate(IUniqueObject eventTrigger)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void handleUpdate(IUniqueObject eventTrigger, ISelectionDelta selectionDelta)
	{

		generalManager.getLogger().log(Level.INFO,
				"Update called by " + eventTrigger.getClass().getSimpleName());

		// Handle incoming genes
		if (selectionDelta.getIDType() == EIDType.DAVID)
		{
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
			addPathwayView(selectionDelta.getSelectionData().get(0).getSelectionID());
		}
	}

	// // TODO re-implement
	//
	// ArrayList<IGraphItem> alPathwayVertexGraphItem = new
	// ArrayList<IGraphItem>();
	//
	// for (int iSelectionIndex = 0; iSelectionIndex < iAlSelection.size();
	// iSelectionIndex++)
	// {
	// int iDavidId = iAlSelection.get(iSelectionIndex);
	//
	// if (iAlSelectionGroup.get(iSelectionIndex) == -1)
	// {
	// generalManager.getViewGLCanvasManager().getSelectionManager().clear();
	// continue;
	// }
	// else if (iAlSelectionGroup.get(iSelectionIndex) != 2)
	// continue;
	//
	// alSelection.get(0).clearAllSelectionArrays();
	//
	// PathwayVertexGraphItem tmpPathwayVertexGraphItem =
	// ((PathwayVertexGraphItem) generalManager
	// .getPathwayItemManager().getItem(
	// generalManager.getPathwayItemManager()
	// .getPathwayVertexGraphItemIdByDavidId(iDavidId)));
	//
	// alPathwayVertexGraphItem.add(tmpPathwayVertexGraphItem);
	//
	// iAlTmpSelectionId.add(iDavidId);
	// iAlTmpGroupId.add(1); // mouse over
	// }
	//
	// if (!alPathwayVertexGraphItem.isEmpty())
	// {
	// loadDependentPathways(alPathwayVertexGraphItem);
	// }
	//
	// alSelection.get(0).mergeSelection(iAlTmpSelectionId, iAlTmpGroupId,
	// null);
	// }
	// // Check if update set contains a pathway that was searched by the user
	// else if (setSelection.getOptionalDataArray() != null)
	// {
	// addPathwayView(setSelection.getOptionalDataArray().get(0));
	//
	// enableBusyMode(true);
	// }

	/**
	 * Add pathway view. Also used when serialized pathways are loaded.
	 * 
	 * @param iPathwayIDToLoad
	 */
	public void addPathwayView(final int iPathwayIDToLoad)
	{

		iAlUninitializedPathwayIDs.add(iPathwayIDToLoad);
	}

	public void loadDependentPathways(final List<ICaleydoGraphItem> alVertex)
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

				iAlUninitializedPathwayIDs.add(iPathwayID);

				// Disable picking until pathways are loaded
				generalManager.getViewGLCanvasManager().getPickingManager().enablePicking(
						false);
			}

			iSlerpFactor = 0;
		}
	}

	@Override
	protected void handleEvents(EPickingType pickingType, EPickingMode pickingMode,
			int iExternalID, Pick pick)
	{

		switch (pickingType)
		{
			case VIEW_SELECTION:
				switch (pickingMode)
				{
					case MOUSE_OVER:

						iMouseOverViewID = iExternalID;
						generalManager.getViewGLCanvasManager().getInfoAreaManager()
								.setDataAboutView(iExternalID);

						break;

					case CLICKED:

						generalManager.getViewGLCanvasManager().getInfoAreaManager()
								.setDataAboutView(iExternalID);

						if (poolLayer.containsElement(iExternalID))// ||
						// stackLayer.containsElement(iViewID))
						{
							loadViewToUnderInteractionLayer(iExternalID);
						}

						if (!dragAndDrop.isDragActionRunning())
							dragAndDrop.startDragAction(iExternalID);

						break;

					case DRAGGED:
						break;
				}

				pickingManager.flushHits(iUniqueID, EPickingType.VIEW_SELECTION);

				break;

			case BUCKET_LOCK_ICON_SELECTION:
				switch (pickingMode)
				{
					case CLICKED:

						break;

					case MOUSE_OVER:

						iNavigationMouseOverViewID_lock = iExternalID;
						iNavigationMouseOverViewID_left = -1;
						iNavigationMouseOverViewID_right = -1;
						iNavigationMouseOverViewID_out = -1;
						iNavigationMouseOverViewID_in = -1;

						break;
				}

				pickingManager.flushHits(iUniqueID, EPickingType.BUCKET_LOCK_ICON_SELECTION);

				break;

			case BUCKET_MOVE_IN_ICON_SELECTION:
				switch (pickingMode)
				{
					case CLICKED:
						loadViewToUnderInteractionLayer(iExternalID);
						bEnableNavigationOverlay = false;
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

						arSlerpActions.clear();

						SlerpAction slerpActionTransition = new SlerpAction(iExternalID,
								stackLayer, poolLayer);
						arSlerpActions.add(slerpActionTransition);

						bEnableNavigationOverlay = false;

						// Unregister all elements of the view that is moved out
						generalManager.getViewGLCanvasManager()
								.getGLEventListener(iExternalID).broadcastElements(
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

						arSlerpActions.clear();

						int iDestinationPosIndex = stackLayer
								.getPositionIndexByElementId(iExternalID);

						if (iDestinationPosIndex == 3)
							iDestinationPosIndex = 0;
						else
							iDestinationPosIndex++;

						if (stackLayer.getElementIdByPositionIndex(iDestinationPosIndex) == -1)
						{
							SlerpAction slerpAction = new SlerpAction(iExternalID, stackLayer,
									stackLayer, iDestinationPosIndex);
							arSlerpActions.add(slerpAction);
						}
						else
						{
							SlerpAction slerpActionTransition = new SlerpAction(iExternalID,
									stackLayer, transitionLayer);
							arSlerpActions.add(slerpActionTransition);

							SlerpAction slerpAction = new SlerpAction(stackLayer
									.getElementIdByPositionIndex(iDestinationPosIndex),
									stackLayer, stackLayer, stackLayer
											.getPositionIndexByElementId(iExternalID));
							arSlerpActions.add(slerpAction);

							SlerpAction slerpActionTransitionReverse = new SlerpAction(
									iExternalID, transitionLayer, stackLayer,
									iDestinationPosIndex);
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

						arSlerpActions.clear();

						int iDestinationPosIndex = stackLayer
								.getPositionIndexByElementId(iExternalID);

						if (iDestinationPosIndex == 0)
							iDestinationPosIndex = 3;
						else
							iDestinationPosIndex--;

						// Check if spot is free
						if (stackLayer.getElementIdByPositionIndex(iDestinationPosIndex) == -1)
						{
							SlerpAction slerpAction = new SlerpAction(iExternalID, stackLayer,
									stackLayer, iDestinationPosIndex);
							arSlerpActions.add(slerpAction);
						}
						else
						{
							SlerpAction slerpActionTransition = new SlerpAction(iExternalID,
									stackLayer, transitionLayer);
							arSlerpActions.add(slerpActionTransition);

							SlerpAction slerpAction = new SlerpAction(stackLayer
									.getElementIdByPositionIndex(iDestinationPosIndex),
									stackLayer, stackLayer, stackLayer
											.getPositionIndexByElementId(iExternalID));
							arSlerpActions.add(slerpAction);

							SlerpAction slerpActionTransitionReverse = new SlerpAction(
									iExternalID, transitionLayer, stackLayer,
									iDestinationPosIndex);
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

			case MEMO_PAD_SELECTION:
				switch (pickingMode)
				{
					case CLICKED:

						break;

					case DRAGGED:

						int iDraggedObjectId = dragAndDrop.getDraggedObjectedId();

						if (iExternalID == TRASH_CAN_PICKING_ID)
						{
							if (iDraggedObjectId != -1)
							{
								// if
								// (memoLayer.containsElement(iDraggedObjectId))
								// {
								memoLayer.removeElement(iDraggedObjectId);
								// dragAndDrop.stopDragAction();
								// break;
								// }

								underInteractionLayer.removeElement(iDraggedObjectId);
								stackLayer.removeElement(iDraggedObjectId);
								poolLayer.removeElement(iDraggedObjectId);
							}
						}
						else if (iExternalID == MEMO_PAD_PICKING_ID)
						{
							if (iDraggedObjectId != -1)
							{
								if (!memoLayer.containsElement(iDraggedObjectId))
								{
									memoLayer.addElement(iDraggedObjectId);
									memoLayer.setElementVisibilityById(true, iDraggedObjectId);
								}
							}
						}

						dragAndDrop.stopDragAction();

						break;
				}

				pickingManager.flushHits(iUniqueID, EPickingType.MEMO_PAD_SELECTION);

				break;

		}
	}

	@Override
	public ArrayList<String> getInfo()
	{

		ArrayList<String> sAlInfo = new ArrayList<String>();
		sAlInfo.add("No info available!");
		return sAlInfo;
	}

	private void createEventMediator()
	{
		CmdEventCreateMediator tmpMediatorCmd = (CmdEventCreateMediator) generalManager
				.getCommandManager().createCommandByType(ECommandType.CREATE_EVENT_MEDIATOR);

		ArrayList<Integer> iAlSenderIDs = new ArrayList<Integer>();
		ArrayList<Integer> iAlReceiverIDs = new ArrayList<Integer>();
		iAlSenderIDs.add(iUniqueID);
		iAlReceiverIDs.add(iUniqueID);
		tmpMediatorCmd.setAttributes(iAlSenderIDs, iAlReceiverIDs,
				MediatorType.SELECTION_MEDIATOR);
		tmpMediatorCmd.doCommand();

		iMediatorID = tmpMediatorCmd.getMediatorID();
	}

	public void toggleLayoutMode()
	{

		if (layoutMode.equals(ARemoteViewLayoutRenderStyle.LayoutMode.BUCKET))
			layoutMode = ARemoteViewLayoutRenderStyle.LayoutMode.JUKEBOX;
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
		}
		else if (layoutMode.equals(ARemoteViewLayoutRenderStyle.LayoutMode.JUKEBOX))
		{
			layoutRenderStyle = new JukeboxLayoutRenderStyle(viewFrustum, layoutRenderStyle);

			// Unregister bucket wheel listener
			parentGLCanvas.removeMouseWheelListener(bucketMouseWheelListener);
			// Register standard mouse wheel listener
			parentGLCanvas.addMouseWheelListener(pickingTriggerMouseAdapter);
		}

		underInteractionLayer = layoutRenderStyle.initUnderInteractionLayer();
		stackLayer = layoutRenderStyle.initStackLayer();
		poolLayer = layoutRenderStyle.initPoolLayer(-1);
		memoLayer = layoutRenderStyle.initMemoLayer();
		transitionLayer = layoutRenderStyle.initTransitionLayer();
		spawnLayer = layoutRenderStyle.initSpawnLayer();

		viewFrustum.setProjectionMode(layoutRenderStyle.getProjectionMode());

		// Trigger reshape to apply new projection mode
		// Is there a better way to achieve this? :)
		parentGLCanvas.setSize(parentGLCanvas.getWidth(), parentGLCanvas.getHeight());

		if (layoutMode.equals(ARemoteViewLayoutRenderStyle.LayoutMode.BUCKET))
		{
			glConnectionLineRenderer = new GLConnectionLineRendererBucket(
					underInteractionLayer, stackLayer, poolLayer);
		}
		else if (layoutMode.equals(ARemoteViewLayoutRenderStyle.LayoutMode.JUKEBOX))
		{
			glConnectionLineRenderer = new GLConnectionLineRendererJukebox(
					underInteractionLayer, stackLayer, poolLayer);
		}
	}

	public void clearAll()
	{

		// Remove all pathway views
		int iGLEventListenerId = -1;

		ArrayList<GLEventListener> alGLEventListenerToRemove = new ArrayList<GLEventListener>();

		for (GLEventListener tmpGLEventListenerToRemove : generalManager
				.getViewGLCanvasManager().getAllGLEventListeners())
		{
			iGLEventListenerId = ((AGLEventListener) tmpGLEventListenerToRemove).getID();

			if (tmpGLEventListenerToRemove instanceof GLPathway)
			{
				if (poolLayer.containsElement(iGLEventListenerId))
					poolLayer.removeElement(iGLEventListenerId);
				else if (stackLayer.containsElement(iGLEventListenerId))
					stackLayer.removeElement(iGLEventListenerId);
				else if (underInteractionLayer.containsElement(iGLEventListenerId))
					underInteractionLayer.removeElement(iGLEventListenerId);
				else if (memoLayer.containsElement(iGLEventListenerId))
					memoLayer.removeElement(iGLEventListenerId);
				else if (transitionLayer.containsElement(iGLEventListenerId))
					transitionLayer.removeElement(iGLEventListenerId);
				else if (spawnLayer.containsElement(iGLEventListenerId))
					spawnLayer.removeElement(iGLEventListenerId);

				alGLEventListenerToRemove.add(tmpGLEventListenerToRemove);
			}
			else if (tmpGLEventListenerToRemove instanceof GLHeatMap
					|| tmpGLEventListenerToRemove instanceof GLParallelCoordinates)
			{
				// Remove all elements from heatmap and parallel coordinates
				((AStorageBasedView) tmpGLEventListenerToRemove).clearAllSelections();
			}
		}

		int iGLEventListenerIdToRemove = -1;
		for (int iGLEventListenerIndex = 0; iGLEventListenerIndex < alGLEventListenerToRemove
				.size(); iGLEventListenerIndex++)
		{
			iGLEventListenerIdToRemove = ((AGLEventListener) alGLEventListenerToRemove
					.get(iGLEventListenerIndex)).getID();

			// Unregister removed pathways from event mediator
			ArrayList<Integer> arMediatorIDs = new ArrayList<Integer>();
			arMediatorIDs.add(iGLEventListenerIdToRemove);
			generalManager.getEventPublisher().addSendersAndReceiversToMediator(
					generalManager.getEventPublisher().getItem(iMediatorID),
					arMediatorIDs, arMediatorIDs, MediatorType.SELECTION_MEDIATOR,
					MediatorUpdateType.MEDIATOR_DEFAULT);

			generalManager.getViewGLCanvasManager().unregisterGLEventListener(
					iGLEventListenerIdToRemove);
		}

		generalManager.getViewGLCanvasManager().getConnectedElementRepresentationManager()
				.clear();
	}

	@Override
	public RemoteHierarchyLayer getHierarchyLayerByGLEventListenerId(
			final int iGLEventListenerId)
	{
		if (underInteractionLayer.containsElement(iGLEventListenerId))
			return underInteractionLayer;
		else if (stackLayer.containsElement(iGLEventListenerId))
			return stackLayer;
		else if (poolLayer.containsElement(iGLEventListenerId))
			return poolLayer;
		else if (transitionLayer.containsElement(iGLEventListenerId))
			return transitionLayer;
		else if (spawnLayer.containsElement(iGLEventListenerId))
			return spawnLayer;
		else if (memoLayer.containsElement(iGLEventListenerId))
			return memoLayer;

		generalManager.getLogger().log(Level.WARNING,
				"GL Event Listener " + iGLEventListenerId + " is not contained in any layer!");

		return null;
	}

	@Override
	public RemoteHierarchyLayer getUnderInteractionHierarchyLayer()
	{
		return underInteractionLayer;
	}

	@Override
	public BucketMouseWheelListener getBucketMouseWheelListener()
	{
		return bucketMouseWheelListener;
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
	{
		super.reshape(drawable, x, y, width, height);

		// Update aspect ratio and reinitialize stack and focus layer
		layoutRenderStyle.setAspectRatio(fAspectRatio);

		layoutRenderStyle.initUnderInteractionLayer();
		layoutRenderStyle.initStackLayer();
		layoutRenderStyle.initPoolLayer(iMouseOverViewID);
		layoutRenderStyle.initMemoLayer();
	}

	protected void renderPoolAndMemoLayerBackground(final GL gl)
	{

		// Pool layer background

		float fWidth = 0.8f;

		if (layoutMode.equals(LayoutMode.BUCKET))
		{
			gl.glColor4f(0.9f, 0.9f, 0.3f, 0.5f);
			gl.glLineWidth(4);
			gl.glBegin(GL.GL_POLYGON);
			gl.glVertex3f(-2 / fAspectRatio, -2, 4);
			gl.glVertex3f(-2 / fAspectRatio, 2, 4);
			gl.glVertex3f(-2 / fAspectRatio + fWidth, 2, 4);
			gl.glVertex3f(-2 / fAspectRatio + fWidth, -2, 4);
			gl.glEnd();

			gl.glColor4f(0.4f, 0.4f, 0.4f, 0.8f);
			gl.glLineWidth(4);
			gl.glBegin(GL.GL_LINE_LOOP);
			gl.glVertex3f(-2 / fAspectRatio, -2, 4);
			gl.glVertex3f(-2 / fAspectRatio, 2, 4);
			gl.glVertex3f(-2 / fAspectRatio + fWidth, 2, 4);
			gl.glVertex3f(-2 / fAspectRatio + fWidth, -2, 4);
			gl.glEnd();

			// Render memo pad background
			gl.glColor4f(0.9f, 0.9f, 0.3f, 0.5f);
			gl.glLineWidth(4);
			gl.glBegin(GL.GL_POLYGON);
			gl.glVertex3f(2 / fAspectRatio, -2, 4);
			gl.glVertex3f(2 / fAspectRatio, 2, 4);
			gl.glVertex3f(2 / fAspectRatio - fWidth, 2, 4);
			gl.glVertex3f(2 / fAspectRatio - fWidth, -2, 4);
			gl.glEnd();

			gl.glColor4f(0.4f, 0.4f, 0.4f, 0.8f);
			gl.glLineWidth(4);
			gl.glBegin(GL.GL_LINE_LOOP);
			gl.glVertex3f(2 / fAspectRatio, -2, 4);
			gl.glVertex3f(2 / fAspectRatio, 2, 4);
			gl.glVertex3f(2 / fAspectRatio - fWidth, 2, 4);
			gl.glVertex3f(2 / fAspectRatio - fWidth, -2, 4);
			gl.glEnd();
		}

		// Render trash can
		gl
				.glPushName(generalManager.getViewGLCanvasManager().getPickingManager()
						.getPickingID(iUniqueID, EPickingType.MEMO_PAD_SELECTION,
								TRASH_CAN_PICKING_ID));
		trashCan.render(gl, layoutRenderStyle);
		gl.glPopName();

		// Render caption
		if (textRenderer == null)
			return;

		String sTmp = "POOL AREA";
		textRenderer.begin3DRendering();
		textRenderer.setColor(0.7f, 0.7f, 0.7f, 1.0f);
		textRenderer.draw3D(sTmp, -1.95f / fAspectRatio, -1.95f, 4.001f, 0.004f); // scale
		// factor
		sTmp = "MEMO AREA";
		textRenderer.draw3D(sTmp, 2.05f / fAspectRatio - fWidth, -1.95f, 4.001f, 0.004f); // scale
		// factor
		textRenderer.end3DRendering();
	}

	public void enableGeneMapping(final boolean bEnableMapping)
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

	public void enablePathwayTextures(final boolean bEnablePathwayTexture)
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

	public void enableNeighborhood(final boolean bEnableNeighborhood)
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

	public void enableBusyMode(final boolean bBusyMode)
	{

		this.bBusyMode = bBusyMode;
		bBusyModeChanged = true;
	}

	private void updateBusyMode(final GL gl)
	{

		if (bBusyMode)
		{
			generalManager.getViewGLCanvasManager().getPickingManager().enablePicking(false);
			gl.glClearColor(1, 1, 0.6f, 1f); // yellowish background (busy mode)
		}
		else
		{
			generalManager.getViewGLCanvasManager().getPickingManager().enablePicking(true);
			gl.glClearColor(1, 1, 1, 1); // white background
		}

		bBusyModeChanged = false;
	}

	@Override
	public void triggerUpdate()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void triggerUpdate(ISelectionDelta selectionDelta)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void broadcastElements(ESelectionType type)
	{

	}

	private void initializeNewPathways(final GL gl)
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
					iAlSetIDs.add(tmpSet.getID());
				}

				// Create Pathway3D view
				CmdCreateGLPathway cmdPathway = (CmdCreateGLPathway) generalManager
						.getCommandManager().createCommandByType(
								ECommandType.CREATE_GL_PATHWAY_3D);

				cmdPathway.setAttributes(iTmpPathwayID, iAlSetIDs,
						EProjectionMode.ORTHOGRAPHIC, -4, 4, 4, -4, -20, 20);
				cmdPathway.doCommand();

				GLPathway pathway = (GLPathway) cmdPathway.getCreatedObject();
				int iGeneratedViewID = pathway.getID();

				ArrayList<Integer> arMediatorIDs = new ArrayList<Integer>();
				arMediatorIDs.add(iGeneratedViewID);

				generalManager.getEventPublisher().addSendersAndReceiversToMediator(
						generalManager.getEventPublisher().getItem(iMediatorID),
						arMediatorIDs, arMediatorIDs, MediatorType.SELECTION_MEDIATOR,
						MediatorUpdateType.MEDIATOR_DEFAULT);

				if (underInteractionLayer.containsElement(-1))
				{
					SlerpAction slerpActionTransition = new SlerpAction(iGeneratedViewID,
							spawnLayer, underInteractionLayer);
					arSlerpActions.add(slerpActionTransition);

					pathway.initRemote(gl, iUniqueID, underInteractionLayer,
							pickingTriggerMouseAdapter, this);
					pathway.setDetailLevel(EDetailLevel.MEDIUM);

					// Trigger initial gene propagation
					pathway.broadcastElements(ESelectionType.NORMAL);
				}
				else if (stackLayer.containsElement(-1))
				{
					SlerpAction slerpActionTransition = new SlerpAction(iGeneratedViewID,
							spawnLayer, stackLayer);
					arSlerpActions.add(slerpActionTransition);

					pathway.initRemote(gl, iUniqueID, stackLayer, pickingTriggerMouseAdapter,
							this);
					pathway.setDetailLevel(EDetailLevel.LOW);

					// Trigger initial gene propagation
					pathway.broadcastElements(ESelectionType.NORMAL);
				}
				else if (poolLayer.containsElement(-1))
				{
					SlerpAction slerpActionTransition = new SlerpAction(iGeneratedViewID,
							spawnLayer, poolLayer);
					arSlerpActions.add(slerpActionTransition);

					pathway.initRemote(gl, iUniqueID, poolLayer, pickingTriggerMouseAdapter,
							this);
					pathway.setDetailLevel(EDetailLevel.VERY_LOW);
				}
				else
				{
					generalManager.getLogger().log(Level.SEVERE,
							"No empty space left to add new pathway!");
					iAlUninitializedPathwayIDs.remove(0);
					return;
				}

				spawnLayer.addElement(iGeneratedViewID);
			}

			iAlUninitializedPathwayIDs.remove(0);

			if (iAlUninitializedPathwayIDs.isEmpty())
				enableBusyMode(false);
			else
				enableBusyMode(true);

			generalManager.getViewGLCanvasManager().getConnectedElementRepresentationManager()
					.clear();

			// Trigger mouse over update if an entity is currently selected
			// TODO: investigate
			// alSelection.get(0).updateSelectionSet(iUniqueID);
		}
	}
	
	public int getMediatorID()
	{
		return iMediatorID;
	}
}
