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

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.data.CmdDataCreateSelectionSetMakro;
import org.caleydo.core.command.event.CmdEventCreateMediator;
import org.caleydo.core.command.view.opengl.CmdGlObjectPathway3D;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.set.selection.ISetSelection;
import org.caleydo.core.data.graph.pathway.core.PathwayGraph;
import org.caleydo.core.data.graph.pathway.item.vertex.PathwayVertexGraphItem;
import org.caleydo.core.data.view.camera.IViewFrustum;
import org.caleydo.core.data.view.camera.ViewFrustumBase.ProjectionMode;
import org.caleydo.core.data.view.rep.renderstyle.layout.ARemoteViewLayoutRenderStyle;
import org.caleydo.core.data.view.rep.renderstyle.layout.BucketLayoutRenderStyle;
import org.caleydo.core.data.view.rep.renderstyle.layout.JukeboxLayoutRenderStyle;
import org.caleydo.core.data.view.rep.renderstyle.layout.ARemoteViewLayoutRenderStyle.LayoutMode;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IEventPublisher.MediatorType;
import org.caleydo.core.manager.event.mediator.IMediatorReceiver;
import org.caleydo.core.manager.event.mediator.IMediatorSender;
import org.caleydo.core.manager.event.mediator.MediatorUpdateType;
import org.caleydo.core.manager.type.ManagerObjectType;
import org.caleydo.core.manager.view.EPickingMode;
import org.caleydo.core.manager.view.EPickingType;
import org.caleydo.core.manager.view.Pick;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.slerp.SlerpAction;
import org.caleydo.core.util.slerp.SlerpMod;
import org.caleydo.core.util.system.SystemTime;
import org.caleydo.core.util.system.Time;
import org.caleydo.core.view.opengl.canvas.AGLCanvasStorageBasedView;
import org.caleydo.core.view.opengl.canvas.AGLCanvasUser;
import org.caleydo.core.view.opengl.canvas.heatmap.GLCanvasHeatMap;
import org.caleydo.core.view.opengl.canvas.parcoords.GLCanvasParCoords3D;
import org.caleydo.core.view.opengl.canvas.pathway.GLCanvasPathway3D;
import org.caleydo.core.view.opengl.canvas.remote.bucket.BucketMouseWheelListener;
import org.caleydo.core.view.opengl.canvas.remote.bucket.GLConnectionLineRendererBucket;
import org.caleydo.core.view.opengl.canvas.remote.jukebox.GLConnectionLineRendererJukebox;
import org.caleydo.core.view.opengl.mouse.PickingJoglMouseListener;
import org.caleydo.core.view.opengl.util.EIconTextures;
import org.caleydo.core.view.opengl.util.GLIconTextureManager;
import org.caleydo.core.view.opengl.util.GLSharedObjects;
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
 * Abstract class that is able to remotely rendering views.
 * Subclasses implement the positioning of the views (bucket, jukebox, etc.).
 * 
 * @author Marc Streit
 * 
 */
public class GLCanvasRemoteRendering3D 
extends AGLCanvasUser
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

	private int iBucketEventMediatorID = -1;

	// Memo pad variables
	private static final int MEMO_PAD_PICKING_ID = 1;
	private static final int TRASH_CAN_PICKING_ID = 2;

	protected TextRenderer textRenderer;

	private GLDragAndDrop dragAndDrop;
	
	private ARemoteViewLayoutRenderStyle layoutRenderStyle;
	
	private BucketMouseWheelListener bucketMouseWheelListener;
	
	private TrashCan trashCan;

	/**
	 * Constructor.
	 * 
	 */
	public GLCanvasRemoteRendering3D(final IGeneralManager generalManager,
			final int iViewId,
			final int iGLCanvasID,
			final String sLabel,
			final IViewFrustum viewFrustum,
			final ARemoteViewLayoutRenderStyle.LayoutMode layoutMode) {

		super(generalManager, iViewId, iGLCanvasID, sLabel, viewFrustum);

		this.layoutMode = layoutMode;
		
		if (layoutMode.equals(ARemoteViewLayoutRenderStyle.LayoutMode.BUCKET))
		{
			layoutRenderStyle = new BucketLayoutRenderStyle();			

			bucketMouseWheelListener = new BucketMouseWheelListener(this, generalManager, 
					(BucketLayoutRenderStyle)layoutRenderStyle);
			
			// Unregister standard mouse wheel listener
			parentGLCanvas.removeMouseWheelListener(pickingTriggerMouseAdapter);
			// Register specialized bucket mouse wheel listener
			parentGLCanvas.addMouseWheelListener(bucketMouseWheelListener);

		}
		else if (layoutMode.equals(ARemoteViewLayoutRenderStyle.LayoutMode.JUKEBOX))
		{
			layoutRenderStyle = new JukeboxLayoutRenderStyle(generalManager);
		}
		
		underInteractionLayer = layoutRenderStyle.initUnderInteractionLayer();
		stackLayer = layoutRenderStyle.initStackLayer();
		poolLayer =layoutRenderStyle.initPoolLayer(-1);
		memoLayer = layoutRenderStyle.initMemoLayer();
		transitionLayer = layoutRenderStyle.initTransitionLayer();
		spawnLayer = layoutRenderStyle.initSpawnLayer();
		
		if (layoutMode.equals(ARemoteViewLayoutRenderStyle.LayoutMode.BUCKET))
		{
			glConnectionLineRenderer = new GLConnectionLineRendererBucket(generalManager,
					underInteractionLayer, stackLayer, poolLayer);
		}
		else if (layoutMode.equals(ARemoteViewLayoutRenderStyle.LayoutMode.JUKEBOX))
		{
			glConnectionLineRenderer = new GLConnectionLineRendererJukebox(generalManager,
					underInteractionLayer, stackLayer, poolLayer);
		}
		
		pickingTriggerMouseAdapter.addGLCanvas(this);

		arSlerpActions = new ArrayList<SlerpAction>();

		iAlUninitializedPathwayIDs = new ArrayList<Integer>();

		createEventMediator();

		dragAndDrop = new GLDragAndDrop();
		
		textRenderer = new TextRenderer(new Font("Arial",
				Font.BOLD, 24), false);
		
		trashCan = new TrashCan();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#initLocal(javax.media.opengl.GL)
	 */
	public void initLocal(final GL gl) {

		init(gl);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#initRemote(javax.media.opengl.GL, int, org.caleydo.core.view.opengl.util.JukeboxHierarchyLayer, org.caleydo.core.view.opengl.mouse.PickingJoglMouseListener, org.caleydo.core.view.opengl.canvas.remote.IGLCanvasRemoteRendering3D)
	 */
	public void initRemote(final GL gl, 
			final int iRemoteViewID,
			final RemoteHierarchyLayer layer,
			final PickingJoglMouseListener pickingTriggerMouseAdapter,
			final IGLCanvasRemoteRendering3D remoteRenderingGLCanvas) 
	{
		// not implemented for a remote view
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#init(javax.media.opengl.GL)
	 */
	public void init(final GL gl) {

		glIconTextureManager = new GLIconTextureManager(gl);

		time = new SystemTime();
		((SystemTime) time).rebase();
		
		retrieveContainedViews(gl);
		
		trashCan.init(gl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#displayLocal(javax.media.opengl.GL)
	 */
	public void displayLocal(final GL gl) {

		if (pickingTriggerMouseAdapter.wasRightMouseButtonPressed())
		{
			bEnableNavigationOverlay = !bEnableNavigationOverlay;

			glConnectionLineRenderer.enableRendering(!bEnableNavigationOverlay);
		}

		pickingManager.handlePicking(iUniqueId, gl, true);

		display(gl);

		if (pickingTriggerMouseAdapter.getPickedPoint() != null)
			dragAndDrop.setCurrentMousePos(gl, pickingTriggerMouseAdapter
					.getPickedPoint());

		if (pickingTriggerMouseAdapter.wasMouseReleased())
			dragAndDrop.stopDragAction();

		checkForHits(gl);

		pickingTriggerMouseAdapter.resetEvents();
		// gl.glCallList(iGLDisplayListIndexLocal);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#displayRemote(javax.media.opengl.GL)
	 */
	public void displayRemote(final GL gl) {

		display(gl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#display(javax.media.opengl.GL)
	 */
	public void display(final GL gl) {

		time.update();

		layoutRenderStyle.initPoolLayer(iMouseOverViewID);
//		layoutRenderStyle.initMemoLayer();
//		layoutRenderStyle.initStackLayer();
//		layoutRenderStyle.initTransitionLayer();
//		layoutRenderStyle.initUnderInteractionLayer();
		
		doSlerpActions(gl);
		
		renderLayer(gl, underInteractionLayer);
		
		// If user zooms to the bucket bottom all but the under
		// interaction layer is _not_ rendered.
		if (bucketMouseWheelListener == null ||
				!bucketMouseWheelListener.isBucketBottomReached())
		{
			glConnectionLineRenderer.render(gl);
		
			if (layoutMode.equals(ARemoteViewLayoutRenderStyle.LayoutMode.BUCKET))
			{
				renderPoolAndMemoLayerBackground(gl);
				
				// MARC: temporarily turn off memo pad for jukebox layout
				renderLayer(gl, memoLayer);
				
				gl.glPushName(generalManager
						.getViewGLCanvasManager().getPickingManager().getPickingID(
								iUniqueId, EPickingType.MEMO_PAD_SELECTION,
								MEMO_PAD_PICKING_ID));
				gl.glPopName();
			}
			
			renderLayer(gl, poolLayer);
			renderLayer(gl, transitionLayer);
			renderLayer(gl, stackLayer);
			renderLayer(gl, spawnLayer);
		}
		
		if (layoutMode.equals(ARemoteViewLayoutRenderStyle.LayoutMode.BUCKET))
		{
			bucketMouseWheelListener.render();	
		}
	}

	private void retrieveContainedViews(final GL gl) {

		Iterator<GLEventListener> iterGLEventListener = generalManager
				.getViewGLCanvasManager().getAllGLEventListeners().iterator();

		while (iterGLEventListener.hasNext())
		{
			AGLCanvasUser tmpGLEventListener = (AGLCanvasUser) iterGLEventListener
					.next();

			if (tmpGLEventListener == this)
				continue;

			int iViewID = ((AGLCanvasUser) tmpGLEventListener).getId();

			if (underInteractionLayer.containsElement(-1))
			{
				underInteractionLayer.addElement(iViewID);
				underInteractionLayer.setElementVisibilityById(true, iViewID);

				tmpGLEventListener.initRemote(gl, iUniqueId,
						underInteractionLayer, pickingTriggerMouseAdapter, this);

			} else if (stackLayer.containsElement(-1))
			{
				stackLayer.addElement(iViewID);
				stackLayer.setElementVisibilityById(true, iViewID);

				tmpGLEventListener.initRemote(gl, iUniqueId, stackLayer,
						pickingTriggerMouseAdapter, this);
			} else if (poolLayer.containsElement(-1))
			{
				poolLayer.addElement(iViewID);
				poolLayer.setElementVisibilityById(true, iViewID);

				tmpGLEventListener.initRemote(gl, iUniqueId, poolLayer,
						pickingTriggerMouseAdapter, this);
			}

			// pickingTriggerMouseAdapter.addGLCanvas(tmpGLEventListener);
			pickingManager.getPickingID(iUniqueId, EPickingType.VIEW_SELECTION, iViewID);

			// Register new view to mediator
			// generalManager.getEventPublisher()
			// .registerSenderToMediator(iBucketEventMediatorID, iViewID);
			// generalManager.getEventPublisher()
			// .registerSenderToMediator(iBucketEventMediatorID, iViewID);

			ArrayList<Integer> arMediatorIDs = new ArrayList<Integer>();
			arMediatorIDs.add(iViewID);
			generalManager.getEventPublisher()
					.addSendersAndReceiversToMediator(
							generalManager.getEventPublisher()
									.getItemMediator(iBucketEventMediatorID),
							arMediatorIDs, arMediatorIDs,
							MediatorType.SELECTION_MEDIATOR,
							MediatorUpdateType.MEDIATOR_DEFAULT);
		}
	}

	private void renderBucketWall(final GL gl) {

		gl.glColor4f(0.4f, 0.4f, 0.4f, 0.8f);
		gl.glLineWidth(4);

		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(0, 0, -0.01f);
		gl.glVertex3f(0, 8, -0.01f);
		gl.glVertex3f(8, 8, -0.01f);
		gl.glVertex3f(8, 0, -0.01f);
		gl.glEnd();

		gl.glColor4f(0.9f, 0.9f, 0.9f, 0.4f);

		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(0, 0, -0.01f);
		gl.glVertex3f(0, 8, -0.01f);
		gl.glVertex3f(8, 8, -0.01f);
		gl.glVertex3f(8, 0, -0.01f);
		gl.glEnd();
	}

	private void renderLayer(final GL gl, final RemoteHierarchyLayer layer) {

		Iterator<Integer> iterElementList = layer.getElementList().iterator();
		int iViewId = 0;
		int iLayerPositionIndex = 0;

		while (iterElementList.hasNext())
		{
			iViewId = iterElementList.next();

			renderEmptyBucketWall(gl, layer, iLayerPositionIndex);

			// Check if spot in layer is currently empty
			if (iViewId != -1)
			{
				gl.glPushName(pickingManager.getPickingID(iUniqueId,
						EPickingType.VIEW_SELECTION, iViewId));
				renderViewByID(gl, iViewId, layer);
				gl.glPopName();
			}

			iLayerPositionIndex++;
		}
	}

	private void renderViewByID(final GL gl, final int iViewID,
			final RemoteHierarchyLayer layer) {

		// Init newly created pathways
		// FIXME: this specialization to pathways in the bucket is not good!
		if (!iAlUninitializedPathwayIDs.isEmpty() && arSlerpActions.isEmpty())
		{
			// Iterator<Integer> iterUninitializedViewIDs =
			// iAlUninitializedPathwayIDs.iterator();
			// while (iterUninitializedViewIDs.hasNext())
			// {
			// int iTmpPathwayID = iterUninitializedViewIDs.next();

			int iTmpPathwayID = iAlUninitializedPathwayIDs.get(0);

			// Check if pathway is already loaded in bucket
			if (!generalManager.getPathwayManager().isPathwayVisible(iTmpPathwayID))
			{
				ArrayList<Integer> iArSetIDs = new ArrayList<Integer>();

//				// FIXME: think of other way instead of hard coded set IDs
//				iArSetIDs.add(85101);
//				iArSetIDs.add(87101);
//				iArSetIDs.add(86101);
//				iArSetIDs.add(88101);
				
				for(ISet tmpSet : alSetData)
				{
					iArSetIDs.add(tmpSet.getId());
				}

				// Create new selection set
				int iSelectionSetID = generalManager
						.getSetManager().createId(ManagerObjectType.SET);
				CmdDataCreateSelectionSetMakro selectedSetCmd = (CmdDataCreateSelectionSetMakro) generalManager
						.getCommandManager().createCommandByType(
								CommandQueueSaxType.CREATE_SET_SELECTION_MAKRO);
				selectedSetCmd.setAttributes(iSelectionSetID);
				selectedSetCmd.doCommand();

				iArSetIDs.add(iSelectionSetID);

				int iGeneratedViewID = generalManager
						.getViewGLCanvasManager().createId(
								ManagerObjectType.VIEW);

				// Create Pathway3D view
				CmdGlObjectPathway3D cmdPathway = (CmdGlObjectPathway3D) generalManager
						.getCommandManager().createCommandByType(
								CommandQueueSaxType.CREATE_GL_PATHWAY_3D);

				cmdPathway.setAttributes(iGeneratedViewID, iTmpPathwayID,
						iArSetIDs, ProjectionMode.ORTHOGRAPHIC, -4, 4, 4, -4,
						-20, 20);

				cmdPathway.doCommand();

				// FIXME: Do this in initRemote of the view
				// Register new view to mediator
				// generalManager.getEventPublisher()
				// .registerSenderToMediator(iBucketEventMediatorID,
				// iGeneratedViewID);
				// generalManager.getEventPublisher()
				// .registerSenderToMediator(iBucketEventMediatorID,
				// iGeneratedViewID);
				ArrayList<Integer> arMediatorIDs = new ArrayList<Integer>();
				arMediatorIDs.add(iGeneratedViewID);
				generalManager.getEventPublisher()
						.addSendersAndReceiversToMediator(
								generalManager
										.getEventPublisher().getItemMediator(
												iBucketEventMediatorID),
								arMediatorIDs, arMediatorIDs,
								MediatorType.SELECTION_MEDIATOR,
								MediatorUpdateType.MEDIATOR_DEFAULT);

				if (underInteractionLayer.containsElement(-1))
				{
					SlerpAction slerpActionTransition = new SlerpAction(
							iGeneratedViewID, spawnLayer, underInteractionLayer);
					arSlerpActions.add(slerpActionTransition);

					((AGLCanvasUser) generalManager
							.getViewGLCanvasManager().getItem(iGeneratedViewID))
							.initRemote(gl, iUniqueId, underInteractionLayer,
									pickingTriggerMouseAdapter, this);
				} else if (stackLayer.containsElement(-1))
				{
					SlerpAction slerpActionTransition = new SlerpAction(
							iGeneratedViewID, spawnLayer, stackLayer);
					arSlerpActions.add(slerpActionTransition);

					((AGLCanvasUser) generalManager
							.getViewGLCanvasManager().getItem(iGeneratedViewID))
							.initRemote(gl, iUniqueId, stackLayer,
									pickingTriggerMouseAdapter, this);
				} else if (poolLayer.containsElement(-1))
				{
					SlerpAction slerpActionTransition = new SlerpAction(
							iGeneratedViewID, spawnLayer, poolLayer);
					arSlerpActions.add(slerpActionTransition);

					((AGLCanvasUser) generalManager
							.getViewGLCanvasManager().getItem(iGeneratedViewID))
							.initRemote(gl, iUniqueId, poolLayer,
									pickingTriggerMouseAdapter, this);
				} else
				{
					generalManager.getLogger().log(Level.SEVERE, "No empty space left to add new pathway!");	
					iAlUninitializedPathwayIDs.remove(0);
					return;
				}

				spawnLayer.addElement(iGeneratedViewID);
			}

			iAlUninitializedPathwayIDs.remove(0);

			// Enable picking after last pathway was loaded
			if (iAlUninitializedPathwayIDs.isEmpty())
			{
				generalManager.getViewGLCanvasManager().getPickingManager().enablePicking(true);
				gl.glClearColor(1, 1, 1, 1); // white background
			}
			else	
			{
				gl.glClearColor(1, 1, 0.6f, 1f); // yellowish background (busy mode)
			}
			
			generalManager.getViewGLCanvasManager().getSelectionManager().clear();
			
			// Trigger mouse over update if an entity is currently selected
			alSetSelection.get(0).updateSelectionSet(iUniqueId);
		}

		// Check if view is visible
		if (!layer.getElementVisibilityById(iViewID))
			return;

		AGLCanvasUser tmpCanvasUser = ((AGLCanvasUser) generalManager
				.getViewGLCanvasManager().getItem(iViewID));

		if (tmpCanvasUser == null)
			throw new CaleydoRuntimeException(
					"Cannot render canvas object which is null!");

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

//		if (layer.equals(underInteractionLayer) || layer.equals(stackLayer))
//		{
//			renderBucketWall(gl);
//		}

		// Render transparent plane for picking views without texture (e.g. PC)
		gl.glColor4f(1, 1, 1, 0);

		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(0, 0, -0.01f);
		gl.glVertex3f(0, 8, -0.01f);
		gl.glVertex3f(8, 8, -0.01f);
		gl.glVertex3f(8, 0, -0.01f);
		gl.glEnd();
		
		if (layer.equals(poolLayer))
		{	
			String sRenderText = tmpCanvasUser.getInfo().get(1);
			
			// Limit pathway name in length
			if(sRenderText.length()> 16)
				sRenderText = sRenderText.subSequence(0, 13) + "...";

			textRenderer.begin3DRendering();	
			textRenderer.setColor(0,0,0,1);
			textRenderer.draw3D(sRenderText,
					8.5f, 3, 0,
					0.1f);  // scale factor
			textRenderer.end3DRendering();
		}
		
//		GLSharedObjects.drawAxis(gl);

		tmpCanvasUser.displayRemote(gl);			

		if (layer.equals(stackLayer))
		{
			renderNavigationOverlay(gl, iViewID);
		}

		gl.glPopMatrix();
	}

	public void renderEmptyBucketWall(final GL gl,
			final RemoteHierarchyLayer layer, final int iLayerPositionIndex) {

		gl.glPushMatrix();

		Transform transform = layer
				.getTransformByPositionIndex(iLayerPositionIndex);
		Vec3f translation = transform.getTranslation();
		Rotf rot = transform.getRotation();
		Vec3f scale = transform.getScale();
		Vec3f axis = new Vec3f();
		float fAngle = rot.get(axis);

		gl.glTranslatef(translation.x(), translation.y(), translation.z());
		gl.glScalef(scale.x(), scale.y(), scale.z());
		gl.glRotatef(Vec3f.convertRadiant2Grad(fAngle), axis.x(), axis.y(),
				axis.z());

		if (!layer.equals(transitionLayer) && !layer.equals(spawnLayer) && !layer.equals(poolLayer))
		{
			renderBucketWall(gl);
		}

		gl.glPopMatrix();
	}

	private void renderNavigationOverlay(final GL gl, final int iViewID) {

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

		Texture textureLock = glIconTextureManager
				.getIconTexture(EIconTextures.LOCK);
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
	
				textureMoveIn = glIconTextureManager
						.getIconTexture(EIconTextures.ARROW_LEFT);
				textureMoveOut = glIconTextureManager
						.getIconTexture(EIconTextures.ARROW_DOWN);
				textureMoveLeft = glIconTextureManager
						.getIconTexture(EIconTextures.ARROW_DOWN);
				textureMoveRight = glIconTextureManager
						.getIconTexture(EIconTextures.ARROW_LEFT);
			} else if (stackLayer.getPositionIndexByElementId(iViewID) == 2) // bottom
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
	
				textureMoveIn = glIconTextureManager
						.getIconTexture(EIconTextures.ARROW_LEFT);
				textureMoveOut = glIconTextureManager
						.getIconTexture(EIconTextures.ARROW_DOWN);
				textureMoveLeft = glIconTextureManager
						.getIconTexture(EIconTextures.ARROW_DOWN);
				textureMoveRight = glIconTextureManager
						.getIconTexture(EIconTextures.ARROW_LEFT);
			} else if (stackLayer.getPositionIndexByElementId(iViewID) == 1) // left
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
	
				textureMoveIn = glIconTextureManager
						.getIconTexture(EIconTextures.ARROW_LEFT);
				textureMoveOut = glIconTextureManager
						.getIconTexture(EIconTextures.ARROW_DOWN);
				textureMoveLeft = glIconTextureManager
						.getIconTexture(EIconTextures.ARROW_DOWN);
				textureMoveRight = glIconTextureManager
						.getIconTexture(EIconTextures.ARROW_LEFT);
			} else if (stackLayer.getPositionIndexByElementId(iViewID) == 3) // right
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
	
				textureMoveIn = glIconTextureManager
						.getIconTexture(EIconTextures.ARROW_LEFT);
				textureMoveOut = glIconTextureManager
						.getIconTexture(EIconTextures.ARROW_DOWN);
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
		gl.glPushName(pickingManager.getPickingID(iUniqueId,
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

		gl.glColor4f(tmpColor_lock.x(), tmpColor_lock.y(), tmpColor_lock.z(),
				tmpColor_lock.w());
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
		gl.glPushName(pickingManager.getPickingID(iUniqueId,
				bottomWallPickingType, iViewID));

		gl.glColor4f(0.5f, 0.5f, 0.5f, 1);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(0, 0, 0.02f);
		gl.glVertex3f(2.66f, 2.66f, 0.02f);
		gl.glVertex3f(5.33f, 2.66f, 0.02f);
		gl.glVertex3f(8, 0, 0.02f);
		gl.glEnd();

		gl.glColor4f(tmpColor_in.x(), tmpColor_in.y(), tmpColor_in.z(),
				tmpColor_in.w());

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
		gl.glPushName(pickingManager.getPickingID(iUniqueId,
				rightWallPickingType, iViewID));

		gl.glColor4f(0.5f, 0.5f, 0.5f, 1);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(8, 0, 0.02f);
		gl.glVertex3f(5.33f, 2.66f, 0.02f);
		gl.glVertex3f(5.33f, 5.33f, 0.02f);
		gl.glVertex3f(8, 8, 0.02f);
		gl.glEnd();

		gl.glColor4f(tmpColor_right.x(), tmpColor_right.y(),
				tmpColor_right.z(), tmpColor_right.w());

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
		gl.glPushName(pickingManager.getPickingID(iUniqueId,
				leftWallPickingType, iViewID));

		gl.glColor4f(0.5f, 0.5f, 0.5f, 1);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(0, 0, 0.02f);
		gl.glVertex3f(0, 8, 0.02f);
		gl.glVertex3f(2.66f, 5.33f, 0.02f);
		gl.glVertex3f(2.66f, 2.66f, 0.02f);
		gl.glEnd();

		gl.glColor4f(tmpColor_left.x(), tmpColor_left.y(), tmpColor_left.z(),
				tmpColor_left.w());

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
		gl.glPushName(pickingManager.getPickingID(iUniqueId,
				topWallPickingType, iViewID));

		gl.glColor4f(0.5f, 0.5f, 0.5f, 1);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(0, 8, 0.02f);
		gl.glVertex3f(8, 8, 0.02f);
		gl.glVertex3f(5.33f, 5.33f, 0.02f);
		gl.glVertex3f(2.66f, 5.33f, 0.02f);
		gl.glEnd();

		gl.glColor4f(tmpColor_out.x(), tmpColor_out.y(), tmpColor_out.z(),
				tmpColor_out.w());
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

	private void doSlerpActions(final GL gl) {

		if (arSlerpActions.isEmpty())
			return;

		SlerpAction tmpSlerpAction = arSlerpActions.get(0);

		if (iSlerpFactor == 0)
		{
			tmpSlerpAction.start();

//			if (tmpSlerpAction.getDestinationHierarchyLayer().equals(stackLayer)
//					|| tmpSlerpAction.getDestinationHierarchyLayer().equals(underInteractionLayer))
//			{
//				glConnectionLineRenderer.enableRendering(false);
//			}

			// tmpSlerpAction.getOriginHierarchyLayer().setElementVisibilityById(false,
			// tmpSlerpAction.getOriginHierarchyLayer().getElementIdByPositionIndex(
			// tmpSlerpAction.getOriginPosIndex()));

//			// Update layer in toolbox renderer
//			((AGLCanvasUser) generalManager
//					.getViewGLCanvasManager().getItem(
//							tmpSlerpAction.getElementId()))
//					.getToolboxRenderer().updateLayer(
//							tmpSlerpAction.getDestinationHierarchyLayer());
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

	private void slerpView(final GL gl, SlerpAction slerpAction) {

		int iViewId = slerpAction.getElementId();
		SlerpMod slerpMod = new SlerpMod();

		if ((iSlerpFactor == 0))
		{
			slerpMod.playSlerpSound();
		}

		Transform transform = slerpMod.interpolate(slerpAction
				.getOriginHierarchyLayer().getTransformByPositionIndex(
						slerpAction.getOriginPosIndex()), slerpAction
				.getDestinationHierarchyLayer().getTransformByPositionIndex(
						slerpAction.getDestinationPosIndex()),
				(float) iSlerpFactor / SLERP_RANGE);

		gl.glPushMatrix();

		slerpMod.applySlerp(gl, transform);

		((AGLCanvasUser) generalManager.getViewGLCanvasManager()
				.getItem(iViewId)).displayRemote(gl);

		gl.glPopMatrix();

		if (iSlerpFactor >= SLERP_RANGE)
		{
			arSlerpActions.remove(slerpAction);

			// if
			// (!slerpAction.getOriginHierarchyLayer().equals(slerpAction.getDestinationHierarchyLayer()))
			// {
			// Remove view from origin layer after slerping
			// slerpAction.getOriginHierarchyLayer().removeElement(iViewId);
			// }

			slerpAction.getDestinationHierarchyLayer()
					.setElementVisibilityById(true, iViewId);

			iSlerpFactor = 0;
		}

		// After last slerp action is done the line connections are turned on
		// again
		if (arSlerpActions.isEmpty())
		{
			glConnectionLineRenderer.enableRendering(true);

			generalManager.getViewGLCanvasManager()
					.getInfoAreaManager().enable(!bEnableNavigationOverlay);
		}
	}

	private void loadViewToUnderInteractionLayer(final int iViewID) {

//		generalManager.logMsg(
//						this.getClass().getSimpleName()
//								+ ": loadPathwayToUnderInteractionPosition(): View with ID "
//								+ iViewID + " is under interaction.",
//						LoggerType.VERBOSE);

		// // Check if pathway is already under interaction
		// if (underInteractionLayer.containsElement(iViewID))
		// return;

		// Check if other slerp action is currently running
		if (iSlerpFactor > 0 && iSlerpFactor < SLERP_RANGE)
			return;

		arSlerpActions.clear();

		// // Check if under interaction layer is free
		// if (underInteractionLayer.getElementList().isEmpty())
		// {
		// // Slerp directly from pool to under interaction layer
		// SlerpAction slerpAction = new SlerpAction(iViewId, stackLayer,
		// false);
		// arSlerpActions.add(slerpAction);
		// }
		// else
		// {

		// // Check if stack layer has a free spot to switch out view under
		// interaction
		// if (stackLayer.containsElement(-1))
		// {
		// // Slerp current view back to layered view
		// if (!underInteractionLayer.getElementList().isEmpty())
		// {
		// // Slerp selected view to under interaction transition position
		// SlerpAction slerpActionTransition = new SlerpAction(
		// iViewID, poolLayer, transitionLayer);
		// arSlerpActions.add(slerpActionTransition);
		//				
		// // Slerp under interaction view to free spot in stack
		// SlerpAction reverseSlerpAction = new SlerpAction(
		// underInteractionLayer.getElementIdByPositionIndex(0),
		// underInteractionLayer, stackLayer);
		// arSlerpActions.add(reverseSlerpAction);
		//				
		// // Slerp selected view from transition position to under interaction
		// position
		// SlerpAction slerpAction = new SlerpAction(
		// iViewID, transitionLayer, underInteractionLayer);
		// arSlerpActions.add(slerpAction);
		// }
		// }
		// else
		// {
		// Check if view is already loaded in the stack layer
		if (stackLayer.containsElement(iViewID))
		{
			// Slerp selected view to under interaction transition position
			SlerpAction slerpActionTransition = new SlerpAction(iViewID,
					stackLayer, transitionLayer);
			arSlerpActions.add(slerpActionTransition);

			// Slerp under interaction view to free spot in stack
			SlerpAction reverseSlerpAction = new SlerpAction(
					underInteractionLayer.getElementIdByPositionIndex(0),
					underInteractionLayer, stackLayer);
			arSlerpActions.add(reverseSlerpAction);

			// Slerp selected view from transition position to under interaction
			// position
			SlerpAction slerpAction = new SlerpAction(iViewID, transitionLayer,
					underInteractionLayer);
			arSlerpActions.add(slerpAction);
		} else
		{
			// Slerp selected view to under interaction transition position
			SlerpAction slerpActionTransition = new SlerpAction(iViewID,
					poolLayer, transitionLayer);
			arSlerpActions.add(slerpActionTransition);

			if (!stackLayer.containsElement(-1))
			{
				// Slerp view from stack to pool
				SlerpAction reverseSlerpAction = new SlerpAction(stackLayer
						.getElementIdByPositionIndex(stackLayer
								.getNextPositionIndex()), stackLayer, true);
				arSlerpActions.add(reverseSlerpAction);
			}

			// Slerp under interaction view to free spot in stack
			SlerpAction reverseSlerpAction2 = new SlerpAction(
					underInteractionLayer.getElementIdByPositionIndex(0),
					underInteractionLayer, true);
			arSlerpActions.add(reverseSlerpAction2);

			// Slerp selected view from transition position to under interaction
			// position
			SlerpAction slerpAction = new SlerpAction(iViewID, transitionLayer,
					underInteractionLayer);
			arSlerpActions.add(slerpAction);
		}
		// }
		//		
		iSlerpFactor = 0;

		// // Slerp current pathway back to layered view
		// if (!underInteractionLayer.getElementList().isEmpty())
		// {
		// SlerpAction reverseSlerpAction = new SlerpAction(
		// underInteractionLayer.getElementIdByPositionIndex(0),
		// underInteractionLayer, true);
		//
		// arSlerpActions.add(reverseSlerpAction);
		// }
		//
		// SlerpAction slerpAction;
		//
		// // Prevent slerp action if pathway is already in layered view
		// if (!stackLayer.containsElement(iViewId))
		// {
		// // Slerp to layered pathway view
		// slerpAction = new SlerpAction(iViewId, poolLayer, false);
		//
		// arSlerpActions.add(slerpAction);
		// }
		//
		// // Slerp from layered to under interaction position
		// slerpAction = new SlerpAction(iViewId, stackLayer, false);
		//
		// arSlerpActions.add(slerpAction);
		// iSlerpFactor = 0;
		//
		// // bRebuildVisiblePathwayDisplayLists = true;
		// //selectedVertex = null;
	}

	@Override
	public void updateReceiver(Object eventTrigger) {

		// TODO Auto-generated method stub

	}

	@Override
	public void updateReceiver(Object eventTrigger, ISet updatedSet) 
	{
		generalManager.getLogger().log(Level.INFO, "Update called by "
				+eventTrigger.getClass().getSimpleName());
		
		ISetSelection setSelection = (ISetSelection) updatedSet;

		setSelection.getReadToken();	
								
		ArrayList<Integer> iAlSelection = setSelection.getSelectionIdArray();
		ArrayList<Integer> iAlSelectionGroup = setSelection.getGroupArray();	
		
		ArrayList<Integer> iAlTmpSelectionId = new ArrayList<Integer>(2);
		ArrayList<Integer> iAlTmpGroupId = new ArrayList<Integer>(2);
		
		if (iAlSelection != null && iAlSelectionGroup != null)
		{		
//			// MARC: Donno if ok here and needed
//			if (iAlSelection.size() != 0)
//				alSetSelection.get(0).clearAllSelectionArrays();
			
			ArrayList<IGraphItem> alPathwayVertexGraphItem = new ArrayList<IGraphItem>();
			
			for (int iSelectionIndex = 0; iSelectionIndex < iAlSelection.size(); iSelectionIndex++)
			{			
				int iDavidId = iAlSelection.get(iSelectionIndex);
				
				if (iAlSelectionGroup.get(iSelectionIndex) == -1)
				{
					generalManager.getViewGLCanvasManager().getSelectionManager().clear();
					continue;
				}
				else if (iAlSelectionGroup.get(iSelectionIndex) != 2)
					continue;
				
				alSetSelection.get(0).clearAllSelectionArrays();
				
				PathwayVertexGraphItem tmpPathwayVertexGraphItem = 
					((PathwayVertexGraphItem)generalManager.getPathwayItemManager().getItem(
						generalManager.getPathwayItemManager().getPathwayVertexGraphItemIdByDavidId(iDavidId)));
			
				alPathwayVertexGraphItem.add(tmpPathwayVertexGraphItem);
				
				iAlTmpSelectionId.add(iDavidId);
				iAlTmpGroupId.add(1); // mouse over
			}
			
			if (!alPathwayVertexGraphItem.isEmpty())
			{
				loadDependentPathways(alPathwayVertexGraphItem);
			}
			
			alSetSelection.get(0).getWriteToken();
			alSetSelection.get(0).mergeSelection(iAlTmpSelectionId, iAlTmpGroupId, null);
			alSetSelection.get(0).returnWriteToken();
		}
		// Check if update set contains a pathway that was searched by the user
		else if (setSelection.getOptionalDataArray() != null)
		{	
			int iPathwayIDToLoad = setSelection.getOptionalDataArray().get(0);
			iAlUninitializedPathwayIDs.add(iPathwayIDToLoad);
			
			// Disable picking until pathways are loaded
			generalManager.getViewGLCanvasManager().getPickingManager().enablePicking(false);
		}

		setSelection.returnReadToken();
	}

	public void loadDependentPathways(final List<IGraphItem> alVertex) {

		// Remove pathways from stacked layer view
		// poolLayer.removeAllElements();

		Iterator<IGraphItem> iterPathwayGraphItem = alVertex.iterator();
		Iterator<IGraphItem> iterIdenticalPathwayGraphItemRep = null;

		IGraphItem pathwayGraphItem;
		int iPathwayID = 0;

		while (iterPathwayGraphItem.hasNext())
		{
			pathwayGraphItem = iterPathwayGraphItem.next();

			if (pathwayGraphItem == null)
			{
//				generalManager.logMsg(
//						this.getClass().getSimpleName() + " (" + iUniqueId
//								+ "): pathway graph item is null.  ",
//						LoggerType.VERBOSE);
				continue;
			}

			iterIdenticalPathwayGraphItemRep = pathwayGraphItem
					.getAllItemsByProp(EGraphItemProperty.ALIAS_CHILD)
					.iterator();

			while (iterIdenticalPathwayGraphItemRep.hasNext())
			{
				iPathwayID = ((PathwayGraph) iterIdenticalPathwayGraphItemRep
						.next().getAllGraphByType(EGraphItemHierarchy.GRAPH_PARENT).toArray()[0]).getId();

				iAlUninitializedPathwayIDs.add(iPathwayID);

				// Disable picking until pathways are loaded
				generalManager.getViewGLCanvasManager().getPickingManager().enablePicking(false);				
			}

			iSlerpFactor = 0;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#handleEvents(org.caleydo.core.manager.view.EPickingType,
	 *      org.caleydo.core.manager.view.EPickingMode, int,
	 *      org.caleydo.core.manager.view.Pick)
	 */
	protected void handleEvents(EPickingType pickingType,
			EPickingMode pickingMode, int iExternalID, Pick pick) {

		switch (pickingType)
		{
		case VIEW_SELECTION:
			switch (pickingMode)
			{
			case MOUSE_OVER:
				
				iMouseOverViewID = iExternalID;
				generalManager.getViewGLCanvasManager()
						.getInfoAreaManager().setDataAboutView(iExternalID);

				break;

			case CLICKED:

				generalManager.getViewGLCanvasManager()
						.getInfoAreaManager().setDataAboutView(iExternalID);

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

			pickingManager.flushHits(iUniqueId, EPickingType.VIEW_SELECTION);

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

			pickingManager.flushHits(iUniqueId,
					EPickingType.BUCKET_LOCK_ICON_SELECTION);

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

			pickingManager.flushHits(iUniqueId,
					EPickingType.BUCKET_MOVE_IN_ICON_SELECTION);

			break;

		case BUCKET_MOVE_OUT_ICON_SELECTION:
			switch (pickingMode)
			{
			case CLICKED:

				// Check if other slerp action is currently running
				if (iSlerpFactor > 0 && iSlerpFactor < SLERP_RANGE)
					break;

				arSlerpActions.clear();

				SlerpAction slerpActionTransition = new SlerpAction(
						iExternalID, stackLayer, poolLayer);
				arSlerpActions.add(slerpActionTransition);

				bEnableNavigationOverlay = false;

				break;

			case MOUSE_OVER:

				iNavigationMouseOverViewID_left = -1;
				iNavigationMouseOverViewID_right = -1;
				iNavigationMouseOverViewID_out = iExternalID;
				iNavigationMouseOverViewID_in = -1;
				iNavigationMouseOverViewID_lock = -1;

				break;
			}

			pickingManager.flushHits(iUniqueId,
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

				if (stackLayer
						.getElementIdByPositionIndex(iDestinationPosIndex) == -1)
				{
					SlerpAction slerpAction = new SlerpAction(iExternalID,
							stackLayer, stackLayer, iDestinationPosIndex);
					arSlerpActions.add(slerpAction);
				} else
				{
					SlerpAction slerpActionTransition = new SlerpAction(
							iExternalID, stackLayer, transitionLayer);
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

			pickingManager.flushHits(iUniqueId,
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
				if (stackLayer
						.getElementIdByPositionIndex(iDestinationPosIndex) == -1)
				{
					SlerpAction slerpAction = new SlerpAction(iExternalID,
							stackLayer, stackLayer, iDestinationPosIndex);
					arSlerpActions.add(slerpAction);
				} else
				{
					SlerpAction slerpActionTransition = new SlerpAction(
							iExternalID, stackLayer, transitionLayer);
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

			pickingManager.flushHits(iUniqueId,
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
						// if (memoLayer.containsElement(iDraggedObjectId))
						// {
						memoLayer.removeElement(iDraggedObjectId);
						// dragAndDrop.stopDragAction();
						// break;
						// }

						underInteractionLayer.removeElement(iDraggedObjectId);
						stackLayer.removeElement(iDraggedObjectId);
						poolLayer.removeElement(iDraggedObjectId);
					}
				} else if (iExternalID == MEMO_PAD_PICKING_ID)
				{
					if (iDraggedObjectId != -1)
					{
						if (!memoLayer.containsElement(iDraggedObjectId))
						{
							memoLayer.addElement(iDraggedObjectId);
							memoLayer.setElementVisibilityById(true,
									iDraggedObjectId);
						}
					}
				}

				dragAndDrop.stopDragAction();

				break;
			}

			pickingManager.flushHits(iUniqueId, EPickingType.MEMO_PAD_SELECTION);

			break;

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#getInfo()
	 */
	public ArrayList<String> getInfo() {

		ArrayList<String> sAlInfo = new ArrayList<String>();
		sAlInfo.add("No info available!");
		return sAlInfo;
	}

	private void createEventMediator() {

		// Create event mediator that connects all views in the bucket
		iBucketEventMediatorID = generalManager.getEventPublisher()
				.createId(ManagerObjectType.EVENT_MEDIATOR_CREATE);

		CmdEventCreateMediator tmpMediatorCmd = (CmdEventCreateMediator) generalManager
				.getCommandManager().createCommandByType(
						CommandQueueSaxType.CREATE_EVENT_MEDIATOR);

		ArrayList<Integer> iAlSenderIDs = new ArrayList<Integer>();
		ArrayList<Integer> iAlReceiverIDs = new ArrayList<Integer>();
		iAlSenderIDs.add(iUniqueId);
		iAlReceiverIDs.add(iUniqueId);
		tmpMediatorCmd.setAttributes(iBucketEventMediatorID, iAlSenderIDs,
				iAlReceiverIDs, MediatorType.SELECTION_MEDIATOR);
		tmpMediatorCmd.doCommand();
	}
	
	public void toggleLayoutMode()
	{
		if (layoutMode.equals(ARemoteViewLayoutRenderStyle.LayoutMode.BUCKET))
			layoutMode = ARemoteViewLayoutRenderStyle.LayoutMode.JUKEBOX;
		else
			layoutMode = ARemoteViewLayoutRenderStyle.LayoutMode.BUCKET;
		
		if (layoutMode.equals(ARemoteViewLayoutRenderStyle.LayoutMode.BUCKET))
		{
			layoutRenderStyle = new BucketLayoutRenderStyle(generalManager, layoutRenderStyle);			

			bucketMouseWheelListener = new BucketMouseWheelListener(this, generalManager, 
					(BucketLayoutRenderStyle)layoutRenderStyle);
			
			// Unregister standard mouse wheel listener
			parentGLCanvas.removeMouseWheelListener(pickingTriggerMouseAdapter);
			// Register specialized bucket mouse wheel listener
			parentGLCanvas.addMouseWheelListener(bucketMouseWheelListener);
		}
		else if (layoutMode.equals(ARemoteViewLayoutRenderStyle.LayoutMode.JUKEBOX))
		{
			layoutRenderStyle = new JukeboxLayoutRenderStyle(generalManager, layoutRenderStyle);
			
			// Unregister bucket wheel listener
			parentGLCanvas.removeMouseWheelListener(bucketMouseWheelListener);
			// Register standard mouse wheel listener
			parentGLCanvas.addMouseWheelListener(pickingTriggerMouseAdapter);
		}
		
		underInteractionLayer = layoutRenderStyle.initUnderInteractionLayer();
		stackLayer = layoutRenderStyle.initStackLayer();
		poolLayer =layoutRenderStyle.initPoolLayer(-1);
		memoLayer = layoutRenderStyle.initMemoLayer();
		transitionLayer = layoutRenderStyle.initTransitionLayer();
		spawnLayer = layoutRenderStyle.initSpawnLayer();
		
		viewFrustum.setProjectionMode(layoutRenderStyle.getProjectionMode());
		
		// Trigger reshape to apply new projection mode
		// Is there a better way to achieve this? :)
		parentGLCanvas.setSize(parentGLCanvas.getWidth(), parentGLCanvas.getHeight());
		
		if (layoutMode.equals(ARemoteViewLayoutRenderStyle.LayoutMode.BUCKET))
		{
			glConnectionLineRenderer = new GLConnectionLineRendererBucket(generalManager,
					underInteractionLayer, stackLayer, poolLayer);
		}
		else if (layoutMode.equals(ARemoteViewLayoutRenderStyle.LayoutMode.JUKEBOX))
		{
			glConnectionLineRenderer = new GLConnectionLineRendererJukebox(generalManager,
					underInteractionLayer, stackLayer, poolLayer);
		}
	}
	
	public void clearAll() 
	{
		// Remove all pathway views
		int iGLEventListenerId = -1;

		ArrayList<GLEventListener> alGLEventListenerToRemove = new ArrayList<GLEventListener>();
		
		for (GLEventListener tmpGLEventListenerToRemove : generalManager.getViewGLCanvasManager().getAllGLEventListeners())
		{
			iGLEventListenerId = ((AGLCanvasUser)tmpGLEventListenerToRemove).getId();
		
			if (tmpGLEventListenerToRemove.getClass().equals(GLCanvasPathway3D.class))
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
			else if (tmpGLEventListenerToRemove.getClass().equals(GLCanvasHeatMap.class) ||
					tmpGLEventListenerToRemove.getClass().equals(GLCanvasParCoords3D.class))
			{
				// Remove all elements from heatmap and parallel coordinates
				((AGLCanvasStorageBasedView)tmpGLEventListenerToRemove).clearAllSelections();
			}
		}
		
		int iGLEventListenerIdToRemove = -1;
		for (int iGLEventListenerIndex = 0; iGLEventListenerIndex < alGLEventListenerToRemove.size(); 
			iGLEventListenerIndex++)
		{
			iGLEventListenerIdToRemove = ((AGLCanvasUser)alGLEventListenerToRemove.get(iGLEventListenerIndex)).getId();
			
			// Unregister removed pathways from event mediator
			ArrayList<Integer> arMediatorIDs = new ArrayList<Integer>();
			arMediatorIDs.add(iGLEventListenerIdToRemove);
			generalManager.getEventPublisher().addSendersAndReceiversToMediator(
					generalManager.getEventPublisher()
							.getItemMediator(iBucketEventMediatorID),
					arMediatorIDs, arMediatorIDs,
					MediatorType.SELECTION_MEDIATOR,
					MediatorUpdateType.MEDIATOR_DEFAULT);
			
			generalManager.getViewGLCanvasManager().unregisterGLEventListener(iGLEventListenerIdToRemove);
		}
		
		generalManager.getViewGLCanvasManager().getSelectionManager().clear();
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.remote.IGLCanvasRemoteRendering3D#getHierarchyLayerByGLCanvasListenerId(int)
	 */
	public RemoteHierarchyLayer getHierarchyLayerByGLCanvasListenerId(
			final int iGLEvnetListenerId) {

		if (underInteractionLayer.containsElement(iGLEvnetListenerId))
			return underInteractionLayer;
		else if (stackLayer.containsElement(iGLEvnetListenerId))
			return stackLayer;
		else if (poolLayer.containsElement(iGLEvnetListenerId))
			return poolLayer;
		else if (transitionLayer.containsElement(iGLEvnetListenerId))
			return transitionLayer;
		else if (spawnLayer.containsElement(iGLEvnetListenerId))
			return spawnLayer;
		else if (memoLayer.containsElement(iGLEvnetListenerId))
			return memoLayer;

		generalManager.getLogger().log(Level.WARNING, 
				"GL Event Listener " +iGLEvnetListenerId +" is not contained in any layer!");

		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.canvas.AGLCanvasUser#reshape(javax.media.opengl.GLAutoDrawable, int, int, int, int)
	 */
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		
		super.reshape(drawable, x, y, width, height);
		
		// Update aspect ratio and reinitialize stack and focus layer
		layoutRenderStyle.setAspectRatio(fAspectRatio);
		
		if (layoutMode.equals(ARemoteViewLayoutRenderStyle.LayoutMode.BUCKET))
		{	
			layoutRenderStyle.initUnderInteractionLayer();
			layoutRenderStyle.initStackLayer();
			layoutRenderStyle.initPoolLayer(iMouseOverViewID);
			layoutRenderStyle.initMemoLayer();
		}
	}
	
	protected void renderPoolAndMemoLayerBackground(final GL gl) {
		
		//Pool layer background

		float fWidth = 0.8f;
		
		gl.glColor4f(0.9f, 0.9f, 0.3f, 0.5f);
		gl.glLineWidth(4);
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(-2/fAspectRatio, -2, 4);
		gl.glVertex3f(-2/fAspectRatio, 2, 4);
		gl.glVertex3f(-2/fAspectRatio + fWidth, 2, 4);
		gl.glVertex3f(-2/fAspectRatio + fWidth, -2, 4);
		gl.glEnd();
	
		gl.glColor4f(0.4f, 0.4f, 0.4f, 0.8f);
		gl.glLineWidth(4);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(-2/fAspectRatio, -2, 4);
		gl.glVertex3f(-2/fAspectRatio, 2, 4);
		gl.glVertex3f(-2/fAspectRatio + fWidth, 2, 4);
		gl.glVertex3f(-2/fAspectRatio + fWidth, -2, 4);
		gl.glEnd();
		
		// Render memo pad background
		gl.glColor4f(0.9f, 0.9f, 0.3f, 0.5f);
		gl.glLineWidth(4);
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(2/fAspectRatio, -2, 4);
		gl.glVertex3f(2/fAspectRatio, 2, 4);
		gl.glVertex3f(2/fAspectRatio - fWidth, 2, 4);
		gl.glVertex3f(2/fAspectRatio - fWidth, -2, 4);
		gl.glEnd();

		gl.glColor4f(0.4f, 0.4f, 0.4f, 0.8f);
		gl.glLineWidth(4);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(2/fAspectRatio, -2, 4);
		gl.glVertex3f(2/fAspectRatio, 2, 4);
		gl.glVertex3f(2/fAspectRatio - fWidth, 2, 4);
		gl.glVertex3f(2/fAspectRatio - fWidth, -2, 4);
		gl.glEnd();

		// Render trash can
		gl.glPushName(generalManager.getViewGLCanvasManager()
				.getPickingManager().getPickingID(iUniqueId,
						EPickingType.MEMO_PAD_SELECTION,
						TRASH_CAN_PICKING_ID));
		trashCan.render(gl, layoutRenderStyle);
		gl.glPopName();
		
		// Render caption
		if (textRenderer == null)
			return;

		String sTmp = "POOL AREA";
		textRenderer.begin3DRendering();
		textRenderer.setColor(0.7f, 0.7f, 0.7f, 1.0f);
		textRenderer.draw3D(sTmp, -1.95f/fAspectRatio, -1.95f, 4.001f, 0.004f); // scale factor
		sTmp = "MEMO AREA";
		textRenderer.draw3D(sTmp, 2.05f/fAspectRatio - fWidth, -1.95f, 4.001f, 0.004f); // scale factor
		textRenderer.end3DRendering();
	}
}
