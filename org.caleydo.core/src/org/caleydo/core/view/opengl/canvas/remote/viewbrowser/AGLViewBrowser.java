package org.caleydo.core.view.opengl.canvas.remote.viewbrowser;

import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import gleem.linalg.open.Transform;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.view.opengl.CmdCreateGLEventListener;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.EVAOperation;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.event.view.ViewActivationEvent;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.manager.view.ConnectedElementRepresentationManager;
import org.caleydo.core.manager.view.RemoteRenderingTransformer;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.system.SystemTime;
import org.caleydo.core.util.system.Time;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionUpdateHandler;
import org.caleydo.core.view.opengl.canvas.remote.AGLConnectionLineRenderer;
import org.caleydo.core.view.opengl.canvas.remote.ARemoteViewLayoutRenderStyle;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.canvas.remote.SerializedRemoteRenderingView;
import org.caleydo.core.view.opengl.canvas.remote.bucket.GLConnectionLineRendererBucket;
import org.caleydo.core.view.opengl.canvas.remote.list.ListLayoutRenderStyle;
import org.caleydo.core.view.opengl.canvas.storagebased.AStorageBasedView;
import org.caleydo.core.view.opengl.canvas.storagebased.heatmap.GLHeatMap;
import org.caleydo.core.view.opengl.canvas.storagebased.heatmap.SerializedHeatMapView;
import org.caleydo.core.view.opengl.canvas.storagebased.parallelcoordinates.GLParallelCoordinates;
import org.caleydo.core.view.opengl.canvas.storagebased.parallelcoordinates.SerializedParallelCoordinatesView;
import org.caleydo.core.view.opengl.canvas.tissue.GLTissue;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;
import org.caleydo.core.view.opengl.util.drag.GLDragAndDrop;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteElementManager;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevel;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevelElement;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.caleydo.core.view.opengl.util.slerp.SlerpAction;
import org.caleydo.core.view.opengl.util.slerp.SlerpMod;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.sun.opengl.util.j2d.TextRenderer;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;

/**
 * Class that is able to remotely rendering views. Subclasses implement the positioning of the views (bucket,
 * jukebox, etc.).
 * 
 * @author Marc Streit
 * @author Alexander Lex
 * @author Werner Puff
 */
public abstract class AGLViewBrowser
	extends AGLEventListener
	implements ISelectionUpdateHandler, IGLRemoteRenderingView {

	private static final int SLERP_RANGE = 1000;
	private static final int SLERP_SPEED = 1400;

	public final static float SIDE_PANEL_WIDTH = 0.8f;

	private int iMouseOverObjectID = -1;

	protected RemoteLevel focusLevel;
	protected RemoteLevel stackLevel;
	protected RemoteLevel poolLevel;
	protected RemoteLevel transitionLevel;
	protected RemoteLevel spawnLevel;
	protected RemoteLevel externalSelectionLevel;

	private ArrayList<SlerpAction> arSlerpActions;

	private Time time;

	/**
	 * Slerp factor: 0 = source; 1 = destination
	 */
	private int iSlerpFactor = 0;

	protected AGLConnectionLineRenderer glConnectionLineRenderer;

	private TextRenderer textRenderer;

	private GLDragAndDrop dragAndDrop;

	private ARemoteViewLayoutRenderStyle layoutRenderStyle;

	protected ArrayList<ASerializedView> newViews;

	private ArrayList<AGLEventListener> containedGLViews;

	/**
	 * The current view in which the user is performing actions.
	 */
	private int iActiveViewID = -1;

	private ISelectionDelta lastSelectionDelta;

	/**
	 * Used for dragging views to the pool area.
	 */
	private int iPoolLevelCommonID = -1;

	private boolean connectionLinesEnabled = true;

	private GLInfoAreaManager infoAreaManager;

	/**
	 * Transformation utility object to transform and project view related coordinates
	 */
	protected RemoteRenderingTransformer selectionTransformer;

	private boolean isSlerpActive = false;

	// protected AddPathwayListener addPathwayListener = null;
	// protected LoadPathwaysByGeneListener loadPathwaysByGeneListener = null;
	// protected EnableGeneMappingListener enableGeneMappingListener = null;
	// protected DisableGeneMappingListener disableGeneMappingListener = null;
	// protected EnableTexturesListener enableTexturesListener = null;
	// protected DisableTexturesListener disableTexturesListener = null;
	// protected EnableNeighborhoodListener enableNeighborhoodListener = null;
	// protected DisableNeighborhoodListener disableNeighborhoodListener = null;
	// protected ToggleNavigationModeListener toggleNavigationModeListener = null;
	// protected ToggleZoomListener toggleZoomListener = null;
	// protected EnableConnectionLinesListener enableConnectionLinesListener = null;
	// protected DisableConnectionLinesListener disableConnectionLinesListener = null;
	// protected ResetViewListener resetViewListener = null;
	// protected SelectionUpdateListener selectionUpdateListener = null;

	/**
	 * Constructor.
	 */
	public AGLViewBrowser(GLCaleydoCanvas glCanvas, final String sLabel, final IViewFrustum viewFrustum) {

		super(glCanvas, sLabel, viewFrustum, true);

		focusLevel = new RemoteLevel(1, "Focus Level", null, stackLevel);
		stackLevel = new RemoteLevel(4, "Stack Level", focusLevel, poolLevel);
		poolLevel = new RemoteLevel(14, "Pool Level", stackLevel, null);
		transitionLevel = new RemoteLevel(1, "Transition Level", null, null);
		spawnLevel = new RemoteLevel(1, "Spawn Level", null, stackLevel);
		externalSelectionLevel = new RemoteLevel(1, "Selection Level", null, stackLevel);

		initFocusLevel();
		initStackLevel();
		initPoolLevel(-1);
		initTransitionLevel();
		initSpawnLevel();
		initExternalSelectionLevel();

		glConnectionLineRenderer = new GLConnectionLineRendererBucket(focusLevel, stackLevel);

		if (glMouseListener != null)
			glMouseListener.addGLCanvas(this);

		arSlerpActions = new ArrayList<SlerpAction>();

		containedGLViews = new ArrayList<AGLEventListener>();
		newViews = new ArrayList<ASerializedView>();

		dragAndDrop = new GLDragAndDrop();

		textRenderer = new TextRenderer(new Font("Arial", Font.PLAIN, 24), false);

		iPoolLevelCommonID = generalManager.getIDManager().createID(EManagedObjectType.REMOTE_LEVEL_ELEMENT);
	}

	protected abstract void initFocusLevel();

	protected abstract void initPoolLevel(int selectedElementId);

	protected abstract void initSpawnLevel();

	protected abstract void initExternalSelectionLevel();

	protected abstract void initTransitionLevel();

	private void initStackLevel() {
		Transform transform;

		float fScalingFactorZoomedIn = 0.16f;
		float yPos = 0.02f;

		// TOP BUCKET WALL
		transform = new Transform();
		transform.setTranslation(new Vec3f(0, yPos, 0f));
		transform.setScale(new Vec3f(fScalingFactorZoomedIn, fScalingFactorZoomedIn, fScalingFactorZoomedIn));
		transform.setRotation(new Rotf(new Vec3f(0, 0, 0), 0));

		stackLevel.getElementByPositionIndex(0).setTransform(transform);

		// LEFT BUCKET WALL
		transform = new Transform();
		transform.setTranslation(new Vec3f(1.6f, yPos, 0));
		transform.setScale(new Vec3f(fScalingFactorZoomedIn, fScalingFactorZoomedIn, fScalingFactorZoomedIn));
		transform.setRotation(new Rotf(new Vec3f(0, 0, 0), 0));
		stackLevel.getElementByPositionIndex(1).setTransform(transform);

		// BOTTOM BUCKET WALL
		transform = new Transform();
		transform.setTranslation(new Vec3f(3.2f, yPos, 0));
		transform.setScale(new Vec3f(fScalingFactorZoomedIn, fScalingFactorZoomedIn, fScalingFactorZoomedIn));
		transform.setRotation(new Rotf(new Vec3f(0, 0, 0), 0));
		stackLevel.getElementByPositionIndex(2).setTransform(transform);

		// RIGHT BUCKET WALL
		transform = new Transform();
		transform.setTranslation(new Vec3f(4.8f, yPos, 0));
		transform.setScale(new Vec3f(fScalingFactorZoomedIn, fScalingFactorZoomedIn, fScalingFactorZoomedIn));
		transform.setRotation(new Rotf(new Vec3f(0, 0, 0), 0));
		stackLevel.getElementByPositionIndex(3).setTransform(transform);
	}

	@Override
	public void initLocal(final GL gl) {
		// iGLDisplayList = gl.glGenLists(1);

		init(gl);
	}

	@Override
	public void initRemote(final GL gl, final AGLEventListener glParentView,
		final GLMouseListener glMouseListener, GLInfoAreaManager infoAreaManager) {

		// Register keyboard listener to GL canvas
		// glParentView.getParentGLCanvas().getParentComposite().getDisplay().asyncExec(new Runnable() {
		// public void run() {
		// glParentView.getParentGLCanvas().getParentComposite().addKeyListener(glKeyListener);
		// }
		// });

		this.glMouseListener = glMouseListener;

		iGLDisplayListIndexRemote = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexRemote;
		init(gl);
	}

	@Override
	public void init(final GL gl) {

		ArrayList<RemoteLevelElement> remoteLevelElementWhiteList = new ArrayList<RemoteLevelElement>();
		remoteLevelElementWhiteList.addAll(focusLevel.getAllElements());
		remoteLevelElementWhiteList.addAll(stackLevel.getAllElements());
		remoteLevelElementWhiteList.addAll(poolLevel.getAllElements());
		selectionTransformer = new RemoteRenderingTransformer(iUniqueID, remoteLevelElementWhiteList);

		addInitialViews();

		gl.glClearColor(0.5f, 0.5f, 0.5f, 1f);

		if (glConnectionLineRenderer != null) {
			glConnectionLineRenderer.init(gl);
		}

		// iconTextureManager = new TextureManager(gl);

		time = new SystemTime();
		((SystemTime) time).rebase();
	}

	protected abstract void addInitialViews();

	@Override
	public void displayLocal(final GL gl) {

		pickingManager.handlePicking(this, gl);

		// if (bIsDisplayListDirtyLocal)
		// {
		// buildDisplayList(gl);
		// bIsDisplayListDirtyLocal = false;
		// }

		display(gl);

		if (eBusyModeState != EBusyModeState.OFF) {
			renderBusyMode(gl);
		}

		checkForHits(gl);

		// gl.glCallList(iGLDisplayListIndexLocal);
	}

	@Override
	public void displayRemote(final GL gl) {

		display(gl);
		checkForHits(gl);

		ConnectedElementRepresentationManager cerm =
			GeneralManager.get().getViewGLCanvasManager().getConnectedElementRepresentationManager();
		cerm.doViewRelatedTransformation(gl, selectionTransformer);
	}

	@Override
	public void display(final GL gl) {
		time.update();
		processEvents();

		// Update the pool transformations according to the current mouse over
		// object
		initPoolLevel(iMouseOverObjectID);

		// initStackLevel();
		initFocusLevel();

		// Just for layout testing during runtime
		// layoutRenderStyle.initStackLevel();
		// layoutRenderStyle.initMemoLevel();

		doSlerpActions(gl);
		initNewView(gl);

		renderRemoteLevel(gl, focusLevel);
		// renderRemoteLevel(gl, stackLevel);
		renderRemoteLevel(gl, poolLevel);
		renderRemoteLevel(gl, externalSelectionLevel);
		renderRemoteLevel(gl, spawnLevel);
		renderRemoteLevel(gl, transitionLevel);
		// renderRemoteLevel(gl, poolLevel);

		renderHandles(gl);

		// gl.glCallList(iGLDisplayList);

		// comment here for connection lines
		// transform-selections here
		if (glConnectionLineRenderer != null && connectionLinesEnabled) {
			glConnectionLineRenderer.setActiveViewID(iActiveViewID); // FIXME: added
			glConnectionLineRenderer.render(gl);
		}

		// float fZTranslation = 0;
		// gl.glTranslatef(0, 0, fZTranslation);
		// contextMenu.render(gl, this);
		// gl.glTranslatef(0, 0, -fZTranslation);

		if (glMouseListener.getPickedPoint() != null) {
			dragAndDrop.setCurrentMousePos(gl, glMouseListener.getPickedPoint());
		}

		if (dragAndDrop.isDragActionRunning()) {
			dragAndDrop.renderDragThumbnailTexture(gl, true);
		}

		if (glMouseListener.wasMouseReleased() && dragAndDrop.isDragActionRunning()) {
			int iDraggedObjectId = dragAndDrop.getDraggedObjectedId();

			// System.out.println("over: " +iExternalID);
			// System.out.println("dragged: " +iDraggedObjectId);

			// Prevent user from dragging element onto selection level
			if (!RemoteElementManager.get().hasItem(iMouseOverObjectID)
				|| !externalSelectionLevel.containsElement(RemoteElementManager.get().getItem(
					iMouseOverObjectID))) {
				RemoteLevelElement mouseOverElement = null;

				// Check if a drag and drop action is performed onto the pool
				// level
				if (iMouseOverObjectID == iPoolLevelCommonID) {
					mouseOverElement = poolLevel.getNextFree();
				}
				else if (mouseOverElement == null && iMouseOverObjectID != iDraggedObjectId) {
					mouseOverElement = RemoteElementManager.get().getItem(iMouseOverObjectID);
				}

				if (mouseOverElement != null) {
					RemoteLevelElement originElement = RemoteElementManager.get().getItem(iDraggedObjectId);

					int iMouseOverElementID = mouseOverElement.getContainedElementID();
					int iOriginElementID = originElement.getContainedElementID();

					mouseOverElement.setContainedElementID(iOriginElementID);
					originElement.setContainedElementID(iMouseOverElementID);

					IViewManager viewGLCanvasManager = generalManager.getViewGLCanvasManager();

					AGLEventListener originView = viewGLCanvasManager.getGLEventListener(iOriginElementID);
					if (originView != null) {
						originView.setRemoteLevelElement(mouseOverElement);
					}

					AGLEventListener mouseOverView =
						viewGLCanvasManager.getGLEventListener(iMouseOverElementID);
					if (mouseOverView != null) {
						mouseOverView.setRemoteLevelElement(originElement);
					}

					updateViewDetailLevels(originElement);
					updateViewDetailLevels(mouseOverElement);

					if (mouseOverElement.getContainedElementID() != -1) {
						if (poolLevel.containsElement(originElement)
							&& (stackLevel.containsElement(mouseOverElement) || focusLevel
								.containsElement(mouseOverElement))) {
							generalManager.getViewGLCanvasManager().getGLEventListener(
								mouseOverElement.getContainedElementID()).broadcastElements(
								EVAOperation.APPEND_UNIQUE);
						}

						if (poolLevel.containsElement(mouseOverElement)
							&& (stackLevel.containsElement(originElement) || focusLevel
								.containsElement(originElement))) {
							generalManager.getViewGLCanvasManager().getGLEventListener(
								mouseOverElement.getContainedElementID()).broadcastElements(
								EVAOperation.REMOVE_ELEMENT);
						}
					}
				}
			}

			generalManager.getViewGLCanvasManager().getConnectedElementRepresentationManager()
				.clearTransformedConnections();
			dragAndDrop.stopDragAction();
		}
	}

	public void renderBucketWall(final GL gl, boolean bRenderBorder, RemoteLevelElement element) {
		// Highlight potential view drop destination
		if (dragAndDrop.isDragActionRunning() && element.getID() == iMouseOverObjectID) {
			gl.glLineWidth(5);
			gl.glColor4f(0.2f, 0.2f, 0.2f, 1);
			gl.glBegin(GL.GL_LINE_LOOP);
			gl.glVertex3f(0, 0, 0.01f);
			gl.glVertex3f(0, 8, 0.01f);
			gl.glVertex3f(8, 8, 0.01f);
			gl.glVertex3f(8, 0, 0.01f);
			gl.glEnd();
		}

		if (arSlerpActions.isEmpty()) {
			gl.glColor4f(1f, 1f, 1f, 1.0f); // normal mode
		}
		else {
			gl.glColor4f(1f, 1f, 1f, 0.3f);
		}

		if (!newViews.isEmpty()) {
			gl.glColor4f(1f, 1f, 1f, 0.3f);
		}

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
	}

	private void renderRemoteLevel(final GL gl, final RemoteLevel level) {
		for (RemoteLevelElement element : level.getAllElements()) {
			renderRemoteLevelElement(gl, element, level);

			if (!(layoutRenderStyle instanceof ListLayoutRenderStyle)) {
				renderEmptyBucketWall(gl, element, level);
			}
		}
	}

	private void renderRemoteLevelElement(final GL gl, RemoteLevelElement element, RemoteLevel level) {
		// // Check if view is visible
		// if (!level.getElementVisibilityById(iViewID))
		// return;

		if (element.getContainedElementID() == -1)
			return;

		int iViewID = element.getContainedElementID();

		gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.REMOTE_LEVEL_ELEMENT, element
			.getID()));
		gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.VIEW_SELECTION, iViewID));

		AGLEventListener glEventListener =
			generalManager.getViewGLCanvasManager().getGLEventListener(iViewID);

		if (glEventListener == null) {
			generalManager.getLogger().log(
				new Status(IStatus.WARNING, IGeneralManager.PLUGIN_ID,
					"Bucket level element is null and cannot be rendered!"));
			return;
		}

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

		if (level == poolLevel) {
			String sRenderText = glEventListener.getShortInfo();

			if (glEventListener instanceof GLTissue)
				sRenderText = ((GLTissue) glEventListener).getLabel();

			// Limit sub view name in length
			int iMaxChars;
			iMaxChars = 20;

			if (sRenderText.length() > iMaxChars && scale.x() < 0.06f) {
				sRenderText = sRenderText.subSequence(0, iMaxChars - 3) + "...";
			}

			float fTextScalingFactor = 0.09f;
			float fTextXPosition = 0f;

			float fXShift = -7.1f;
			if (this instanceof GLTissueViewBrowser)
				fXShift = -0.8f;

			if (element.getID() == iMouseOverObjectID) {
				renderPoolSelection(gl, translation.x() + fXShift, translation.y() * scale.y() + 5.2f,

				(float) textRenderer.getBounds(sRenderText).getWidth() * 0.06f + 23, 6f, element);
				gl.glTranslatef(0.8f, 1.3f, 0);

				fTextScalingFactor = 0.075f;
				fTextXPosition = 12f;
			}
			else {
				// Render view background frame
				Texture tempTexture = textureManager.getIconTexture(gl, EIconTextures.POOL_VIEW_BACKGROUND);
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

			int iNumberOfGenesSelected = glEventListener.getNumberOfSelections(ESelectionType.SELECTION);
			int iNumberOfGenesMouseOver = glEventListener.getNumberOfSelections(ESelectionType.MOUSE_OVER);

			textRenderer.begin3DRendering();

			if (element.getID() == iMouseOverObjectID) {
				textRenderer.setColor(1, 1, 1, 1);
			}
			else {
				textRenderer.setColor(0, 0, 0, 1);
			}

			if (iNumberOfGenesMouseOver == 0 && iNumberOfGenesSelected == 0) {
				textRenderer.draw3D(sRenderText, fTextXPosition, 3f, 0.1f, fTextScalingFactor);
			}
			else {
				textRenderer.draw3D(sRenderText, fTextXPosition, 4.5f, 0.1f, fTextScalingFactor);
			}

			textRenderer.end3DRendering();

			gl.glLineWidth(4);

			if (element.getID() == iMouseOverObjectID) {
				gl.glTranslatef(2.2f, 0.5f, 0);
			}

			if (iNumberOfGenesMouseOver > 0) {
				if (element.getID() == iMouseOverObjectID) {
					gl.glTranslatef(-2.5f, 0, 0);
				}

				textRenderer.begin3DRendering();
				textRenderer.draw3D(Integer.toString(iNumberOfGenesMouseOver), fTextXPosition + 9, 2.4f, 0,
					fTextScalingFactor);
				textRenderer.end3DRendering();

				if (element.getID() == iMouseOverObjectID) {
					gl.glTranslatef(2.5f, 0, 0);
				}

				gl.glColor4fv(GeneralRenderStyle.MOUSE_OVER_COLOR, 0);
				gl.glBegin(GL.GL_LINES);
				gl.glVertex3f(10, 2.7f, 0f);
				gl.glVertex3f(18, 2.7f, 0f);
				gl.glVertex3f(20, 2.7f, 0f);
				gl.glVertex3f(29, 2.7f, 0f);
				gl.glEnd();
			}

			if (iNumberOfGenesSelected > 0) {
				if (iNumberOfGenesMouseOver > 0) {
					gl.glTranslatef(0, -1.8f, 0);
				}

				if (element.getID() == iMouseOverObjectID) {
					gl.glTranslatef(-2.5f, 0, 0);
				}

				textRenderer.begin3DRendering();
				textRenderer.draw3D(Integer.toString(iNumberOfGenesSelected), fTextXPosition + 9, 2.5f, 0,
					fTextScalingFactor);
				textRenderer.end3DRendering();

				if (element.getID() == iMouseOverObjectID) {
					gl.glTranslatef(2.5f, 0, 0);
				}

				gl.glColor4fv(GeneralRenderStyle.SELECTED_COLOR, 0);
				gl.glBegin(GL.GL_LINES);
				gl.glVertex3f(10, 2.9f, 0f);
				gl.glVertex3f(18, 2.9f, 0f);
				gl.glVertex3f(20, 2.9f, 0f);
				gl.glVertex3f(29, 2.9f, 0f);
				gl.glEnd();

				if (iNumberOfGenesMouseOver > 0) {
					gl.glTranslatef(0, 1.8f, 0);
				}
			}

			if (element.getID() == iMouseOverObjectID) {
				gl.glTranslatef(-2.2f, -0.5f, 0);
			}
		}

		// Prevent rendering of view textures when simple list view
		// if ((layoutRenderStyle instanceof ListLayoutRenderStyle
		// && (layer == poolLayer || layer == stackLayer)))
		// {
		// gl.glPopMatrix();
		// return;
		// }

		if (level != externalSelectionLevel && level != poolLevel) {
			if (level.equals(focusLevel)) {
				renderBucketWall(gl, false, element);
			}
			else {
				renderBucketWall(gl, true, element);
			}
		}

		glEventListener.displayRemote(gl);

		gl.glPopMatrix();

		gl.glPopName();
		gl.glPopName();
	}

	private void renderEmptyBucketWall(final GL gl, RemoteLevelElement element, RemoteLevel level) {
		gl.glPushMatrix();

		gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.REMOTE_LEVEL_ELEMENT, element
			.getID()));

		Transform transform = element.getTransform();
		Vec3f translation = transform.getTranslation();
		Rotf rot = transform.getRotation();
		Vec3f scale = transform.getScale();
		Vec3f axis = new Vec3f();
		float fAngle = rot.get(axis);

		gl.glTranslatef(translation.x(), translation.y(), translation.z());
		gl.glScalef(scale.x(), scale.y(), scale.z());
		gl.glRotatef(Vec3f.convertRadiant2Grad(fAngle), axis.x(), axis.y(), axis.z());

		if (!level.equals(transitionLevel) && !level.equals(spawnLevel) && !level.equals(poolLevel)
			&& !level.equals(externalSelectionLevel)) {
			renderBucketWall(gl, true, element);
		}

		gl.glPopName();

		gl.glPopMatrix();
	}

	private void renderHandles(final GL gl) {

		// // Bucket stack top
		// RemoteLevelElement element = stackLevel.getElementByPositionIndex(0);
		// if (element.getContainedElementID() != -1) {
		//
		// // if (!bucketMouseWheelListener.isZoomedIn()) {
		// // gl.glTranslatef(-2, 0, 4.02f);
		// // renderNavigationHandleBar(gl, element, 4, 0.075f, false, 2);
		// // gl.glTranslatef(2, 0, -4.02f);
		// // }
		// // else {
		// renderStackViewHandleBarZoomedIn(gl, element);
		// // }
		// }
		//
		// // Bucket stack bottom
		// element = stackLevel.getElementByPositionIndex(2);
		// if (element.getContainedElementID() != -1) {
		// // if (!bucketMouseWheelListener.isZoomedIn()) {
		// // gl.glTranslatef(-2, 0, 4.02f);
		// // gl.glRotatef(180, 1, 0, 0);
		// // renderNavigationHandleBar(gl, element, 4, 0.075f, true, 2);
		// // gl.glRotatef(-180, 1, 0, 0);
		// // gl.glTranslatef(2, 0, -4.02f);
		// // }
		// // else {
		// renderStackViewHandleBarZoomedIn(gl, element);
		// // }
		// }
		//
		// // Bucket stack left
		// element = stackLevel.getElementByPositionIndex(1);
		// if (element.getContainedElementID() != -1) {
		// // if (!bucketMouseWheelListener.isZoomedIn()) {
		// // gl.glTranslatef(-2f / fAspectRatio + 2 + 0.8f, -2, 4.02f);
		// // gl.glRotatef(90, 0, 0, 1);
		// // renderNavigationHandleBar(gl, element, 4, 0.075f, false, 2);
		// // gl.glRotatef(-90, 0, 0, 1);
		// // gl.glTranslatef(2f / fAspectRatio - 2 - 0.8f, 2, -4.02f);
		// // }
		// // else {
		// renderStackViewHandleBarZoomedIn(gl, element);
		// // }
		// }
		//
		// // Bucket stack right
		// element = stackLevel.getElementByPositionIndex(3);
		// if (element.getContainedElementID() != -1) {
		// // if (!bucketMouseWheelListener.isZoomedIn()) {
		// // gl.glTranslatef(2f / fAspectRatio - 0.8f - 2, 2, 4.02f);
		// // gl.glRotatef(-90, 0, 0, 1);
		// // renderNavigationHandleBar(gl, element, 4, 0.075f, false, 2);
		// // gl.glRotatef(90, 0, 0, 1);
		// // gl.glTranslatef(-2f / fAspectRatio + 0.8f + 2, -2, -4.02f);
		// // }
		// // else {
		// renderStackViewHandleBarZoomedIn(gl, element);
		// // }
		// }

		// Bucket center (focus)
		RemoteLevelElement element = focusLevel.getElementByPositionIndex(0);
		if (element.getContainedElementID() != -1) {

			Transform transform;
			Vec3f translation;
			Vec3f scale;

			float fYCorrection = 0f;
			// if (!bucketMouseWheelListener.isZoomedIn()) {
			// fYCorrection = 0f;
			// }
			// else {
			fYCorrection = 0.145f;
			// }

			transform = element.getTransform();
			translation = transform.getTranslation();
			scale = transform.getScale();

			gl.glTranslatef(translation.x(), translation.y() - 2 * 0.075f + fYCorrection,
				translation.z() + 0.001f);

			gl.glScalef(scale.x() * 4, scale.y() * 4, scale.z());
			renderNavigationHandleBar(gl, element, 2, 0.075f, false, 2);
			gl.glScalef(1 / (scale.x() * 4), 1 / (scale.y() * 4), 1 / scale.z());

			gl.glTranslatef(-translation.x(), -translation.y() + 2 * 0.075f - fYCorrection,
				-translation.z() - 0.001f);
		}
	}

	// private void renderStackViewHandleBarZoomedIn(final GL gl, RemoteLevelElement element) {
	// Transform transform = element.getTransform();
	// Vec3f translation = transform.getTranslation();
	// Vec3f scale = transform.getScale();
	// // float fZoomedInScalingFactor = 0.1f;
	// float fYCorrection = 0f;
	// // if (!bucketMouseWheelListener.isZoomedIn()) {
	// // fYCorrection = 0f;
	// // }
	// // else {
	// fYCorrection = 0.145f;
	// // }
	//
	// gl.glTranslatef(translation.x(), translation.y() - 2 * 0.075f + fYCorrection,
	// translation.z() + 0.001f);
	// gl.glScalef(scale.x() * 4, scale.y() * 4, scale.z());
	// renderNavigationHandleBar(gl, element, 2, 0.075f, false, 2);
	// gl.glScalef(1 / (scale.x() * 4), 1 / (scale.y() * 4), 1 / scale.z());
	// gl.glTranslatef(-translation.x(), -translation.y() + 2 * 0.075f - fYCorrection,
	// -translation.z() - 0.001f);
	// }

	private void renderNavigationHandleBar(final GL gl, RemoteLevelElement element, float fHandleWidth,
		float fHandleHeight, boolean bUpsideDown, float fScalingFactor) {

		// Render icons
		gl.glTranslatef(0, 2 + fHandleHeight, 0);
		renderSingleHandle(gl, element.getID(), EPickingType.BUCKET_DRAG_ICON_SELECTION,
			EIconTextures.NAVIGATION_DRAG_VIEW, fHandleHeight, fHandleHeight);
		gl.glTranslatef(fHandleWidth - 2 * fHandleHeight, 0, 0);
		if (bUpsideDown) {
			gl.glRotatef(180, 1, 0, 0);
			gl.glTranslatef(0, fHandleHeight, 0);
		}
		renderSingleHandle(gl, element.getID(), EPickingType.BUCKET_LOCK_ICON_SELECTION,
			EIconTextures.NAVIGATION_LOCK_VIEW, fHandleHeight, fHandleHeight);
		if (bUpsideDown) {
			gl.glTranslatef(0, -fHandleHeight, 0);
			gl.glRotatef(-180, 1, 0, 0);
		}
		gl.glTranslatef(fHandleHeight, 0, 0);
		renderSingleHandle(gl, element.getID(), EPickingType.BUCKET_REMOVE_ICON_SELECTION,
			EIconTextures.NAVIGATION_REMOVE_VIEW, fHandleHeight, fHandleHeight);
		gl.glTranslatef(-fHandleWidth + fHandleHeight, -2 - fHandleHeight, 0);

		// Render background (also draggable)
		gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.BUCKET_DRAG_ICON_SELECTION, element
			.getID()));
		gl.glColor3f(0.25f, 0.25f, 0.25f);
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(0 + fHandleHeight, 2 + fHandleHeight, 0);
		gl.glVertex3f(fHandleWidth - 2 * fHandleHeight, 2 + fHandleHeight, 0);
		gl.glVertex3f(fHandleWidth - 2 * fHandleHeight, 2, 0);
		gl.glVertex3f(0 + fHandleHeight, 2, 0);
		gl.glEnd();

		gl.glPopName();

		// Render view information
		String sText =
			generalManager.getViewGLCanvasManager().getGLEventListener(element.getContainedElementID())
				.getShortInfo();

		int iMaxChars = 50;
		if (sText.length() > iMaxChars) {
			sText = sText.subSequence(0, iMaxChars - 3) + "...";
		}

		float fTextScalingFactor = 0.0027f;

		if (bUpsideDown) {
			gl.glRotatef(180, 1, 0, 0);
			gl.glTranslatef(0, -4 - fHandleHeight, 0);
		}

		textRenderer.setColor(0.7f, 0.7f, 0.7f, 1);
		textRenderer.begin3DRendering();
		textRenderer.draw3D(sText, fHandleWidth / fScalingFactor
			- (float) textRenderer.getBounds(sText).getWidth() / 2f * fTextScalingFactor, 2.02f, 0f,
			fTextScalingFactor);
		textRenderer.end3DRendering();

		if (bUpsideDown) {
			gl.glTranslatef(0, 4 + fHandleHeight, 0);
			gl.glRotatef(-180, 1, 0, 0);
		}
	}

	private void renderSingleHandle(final GL gl, int iRemoteLevelElementID, EPickingType ePickingType,
		EIconTextures eIconTexture, float fWidth, float fHeight) {
		gl.glPushName(pickingManager.getPickingID(iUniqueID, ePickingType, iRemoteLevelElementID));

		Texture tempTexture = textureManager.getIconTexture(gl, eIconTexture);
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

	private void renderPoolSelection(final GL gl, float fXOrigin, float fYOrigin, float fWidth,
		float fHeight, RemoteLevelElement element) {
		float fPanelSideWidth = 11f;

		float z = 0.06f;

		gl.glColor3f(0.25f, 0.25f, 0.25f);
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(fXOrigin + 10.2f, fYOrigin - fHeight / 2f + fHeight, z);
		gl.glVertex3f(fXOrigin + 10.2f + fWidth, fYOrigin - fHeight / 2f + fHeight, z);
		gl.glVertex3f(fXOrigin + 10.2f + fWidth, fYOrigin - fHeight / 2f, z);
		gl.glVertex3f(fXOrigin + 10.2f, fYOrigin - fHeight / 2f, z);
		gl.glEnd();

		Texture tempTexture = textureManager.getIconTexture(gl, EIconTextures.POOL_VIEW_BACKGROUND_SELECTION);
		tempTexture.enable();
		tempTexture.bind();

		TextureCoords texCoords = tempTexture.getImageTexCoords();

		gl.glColor4f(1, 1, 1, 0.75f);

		gl.glBegin(GL.GL_POLYGON);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(fXOrigin + fPanelSideWidth, fYOrigin - fHeight, -0.01f);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(fXOrigin + fPanelSideWidth, fYOrigin + fHeight, -0.01f);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(fXOrigin, fYOrigin + fHeight, -0.01f);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(fXOrigin, fYOrigin - fHeight, -0.01f);
		gl.glEnd();

		tempTexture.disable();

		gl.glPopName();
		gl.glPopName();

		int fHandleScaleFactor = 18;
		gl.glTranslatef(fXOrigin - 1.2f, fYOrigin - fHeight / 2f + fHeight - 1f, 1.8f);
		gl.glScalef(fHandleScaleFactor, fHandleScaleFactor, fHandleScaleFactor);
		renderSingleHandle(gl, element.getID(), EPickingType.BUCKET_DRAG_ICON_SELECTION,
			EIconTextures.POOL_DRAG_VIEW, 0.1f, 0.1f);
		gl.glTranslatef(0, -0.2f, 0);
		renderSingleHandle(gl, element.getID(), EPickingType.BUCKET_REMOVE_ICON_SELECTION,
			EIconTextures.POOL_REMOVE_VIEW, 0.1f, 0.1f);
		gl.glTranslatef(0, 0.2f, 0);
		gl.glScalef(1f / fHandleScaleFactor, 1f / fHandleScaleFactor, 1f / fHandleScaleFactor);
		gl.glTranslatef(-fXOrigin + 1.2f, -fYOrigin + fHeight / 2f - fHeight + 1f, -1.8f);

		// gl.glColor3f(0.25f, 0.25f, 0.25f);
		// gl.glBegin(GL.GL_POLYGON);
		// gl.glVertex3f(fXOrigin + 3f, fYOrigin - fHeight / 2f + fHeight -
		// 2.5f, 0f);
		// gl.glVertex3f(fXOrigin + 5.1f, fYOrigin - fHeight / 2f + fHeight -
		// 2.5f, 0f);
		// gl.glVertex3f(fXOrigin + 5.1f, fYOrigin- fHeight / 2f + 1.5f, 0f);
		// gl.glVertex3f(fXOrigin + 3f, fYOrigin- fHeight / 2f + 1.5f , 0f);
		// gl.glEnd();

		gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.REMOTE_LEVEL_ELEMENT, element
			.getID()));
		gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.VIEW_SELECTION, element.getID()));
	}

	private void doSlerpActions(final GL gl) {
		if (arSlerpActions.isEmpty())
			return;

		SlerpAction tmpSlerpAction = arSlerpActions.get(0);

		if (iSlerpFactor == 0) {
			tmpSlerpAction.start();

			// System.out.println("Start slerp action " +tmpSlerpAction);
		}

		if (iSlerpFactor < SLERP_RANGE) {
			// Makes animation rendering speed independent
			iSlerpFactor += SLERP_SPEED * time.deltaT();

			if (iSlerpFactor > SLERP_RANGE) {
				iSlerpFactor = SLERP_RANGE;
			}
		}

		slerpView(gl, tmpSlerpAction);
	}

	private void slerpView(final GL gl, SlerpAction slerpAction) {
		int iViewID = slerpAction.getElementId();

		SlerpMod slerpMod = new SlerpMod();

		if (iSlerpFactor == 0) {
			slerpMod.playSlerpSound();
		}

		Transform transform =
			slerpMod.interpolate(slerpAction.getOriginRemoteLevelElement().getTransform(), slerpAction
				.getDestinationRemoteLevelElement().getTransform(), (float) iSlerpFactor / SLERP_RANGE);

		gl.glPushMatrix();

		slerpMod.applySlerp(gl, transform, true, false);

		generalManager.getViewGLCanvasManager().getGLEventListener(iViewID).displayRemote(gl);

		gl.glPopMatrix();

		// Check if slerp action is finished
		if (iSlerpFactor >= SLERP_RANGE) {
			arSlerpActions.remove(slerpAction);
			iSlerpFactor = 0;
			slerpAction.finished();
			RemoteLevelElement destinationElement = slerpAction.getDestinationRemoteLevelElement();
			updateViewDetailLevels(destinationElement);
		}

		// After last slerp action is done the line connections are turned on
		// again
		if (arSlerpActions.isEmpty()) {
			if (glConnectionLineRenderer != null) {
				glConnectionLineRenderer.enableRendering(true);
			}

			// generalManager.getViewGLCanvasManager().getInfoAreaManager().enable(!bEnableNavigationOverlay);
			generalManager.getViewGLCanvasManager().getConnectedElementRepresentationManager()
				.clearTransformedConnections();
		}
	}

	private void updateViewDetailLevels(RemoteLevelElement element) {
		RemoteLevel destinationLevel = element.getRemoteLevel();

		if (element.getContainedElementID() == -1)
			return;

		AGLEventListener glActiveSubView =
			GeneralManager.get().getViewGLCanvasManager().getGLEventListener(element.getContainedElementID());

		glActiveSubView.setRemoteLevelElement(element);

		// Update detail level of moved view when slerp action is finished;
		if (destinationLevel == focusLevel) {
			glActiveSubView.setDetailLevel(EDetailLevel.MEDIUM);
		}
		else if (destinationLevel == stackLevel) {
			glActiveSubView.setDetailLevel(EDetailLevel.LOW);
		}
		else if (destinationLevel == poolLevel || destinationLevel == externalSelectionLevel) {
			glActiveSubView.setDetailLevel(EDetailLevel.VERY_LOW);
		}

		compactPoolLevel();
	}

	private void loadViewToFocusLevel(final int iRemoteLevelElementID) {
		RemoteLevelElement element = RemoteElementManager.get().getItem(iRemoteLevelElementID);

		// Check if other slerp action is currently running
		// if (iSlerpFactor > 0 && iSlerpFactor < SLERP_RANGE)
		// return;

		arSlerpActions.clear();

		int iViewID = element.getContainedElementID();

		if (iViewID == -1)
			return;

		// Slerp focus view to pool
		SlerpAction makePlaceSlerpActionTransition =
			new SlerpAction(focusLevel.getElementByPositionIndex(0), poolLevel.getNextFree());
		arSlerpActions.add(makePlaceSlerpActionTransition);

		// Slerp selected view to focus position
		SlerpAction slerpActionTransition = new SlerpAction(element, focusLevel.getElementByPositionIndex(0));
		arSlerpActions.add(slerpActionTransition);

		iSlerpFactor = 0;
	}

	@Override
	public void handleSelectionUpdate(ISelectionDelta selectionDelta, boolean scrollToSelection, String info) {
		lastSelectionDelta = selectionDelta;
	}

	@Override
	protected void handlePickingEvents(EPickingType pickingType, EPickingMode pickingMode, int iExternalID,
		Pick pick) {

		switch (pickingType) {
			case BUCKET_DRAG_ICON_SELECTION:

				switch (pickingMode) {
					case CLICKED:

						if (!dragAndDrop.isDragActionRunning()) {
							// System.out.println("Start drag!");
							dragAndDrop.startDragAction(iExternalID);
						}

						iMouseOverObjectID = iExternalID;

						compactPoolLevel();

						break;
				}
				break;

			case BUCKET_REMOVE_ICON_SELECTION:

				switch (pickingMode) {
					case CLICKED:

						RemoteLevelElement element = RemoteElementManager.get().getItem(iExternalID);

						AGLEventListener glEventListener =
							generalManager.getViewGLCanvasManager().getGLEventListener(
								element.getContainedElementID());

						// // Unregister all elements of the view that is
						// removed
						// glEventListener.broadcastElements(EVAOperation.REMOVE_ELEMENT);

						removeView(glEventListener);
						element.setContainedElementID(-1);
						containedGLViews.remove(glEventListener);

						if (element.getRemoteLevel() == poolLevel) {
							compactPoolLevel();
						}

						if (glEventListener instanceof GLTissue)
							removeSelection(((GLTissue) glEventListener).getExperimentIndex());

						setDisplayListDirty();

						break;
				}
				break;

			case BUCKET_LOCK_ICON_SELECTION:

				switch (pickingMode) {
					case CLICKED:

						RemoteLevelElement element = RemoteElementManager.get().getItem(iExternalID);

						// Toggle lock flag
						element.lock(!element.isLocked());

						break;
				}
				break;

			case REMOTE_LEVEL_ELEMENT:
				switch (pickingMode) {
					case MOUSE_OVER:
					case DRAGGED:
						iMouseOverObjectID = iExternalID;
						break;
					case CLICKED:

						// Do not handle click if element is dragged
						if (dragAndDrop.isDragActionRunning()) {
							break;
						}

						// Check if view is contained in pool level
						for (RemoteLevelElement element : poolLevel.getAllElements()) {
							if (element.getID() == iExternalID) {
								loadViewToFocusLevel(iExternalID);
								break;
							}
						}
						break;
				}
				break;

			case VIEW_SELECTION:
				switch (pickingMode) {
					case MOUSE_OVER:

						// generalManager.getViewGLCanvasManager().getInfoAreaManager()
						// .setDataAboutView(iExternalID);

						// Prevent update flood when moving mouse over view
						if (iActiveViewID == iExternalID) {
							break;
						}

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
					case RIGHT_CLICKED:
						contextMenu.setLocation(pick.getPickedPoint(), getParentGLCanvas().getWidth(),
							getParentGLCanvas().getHeight());
						contextMenu.setMasterGLView(this);
						break;

				}
				break;

			case CONTEXT_MENU_SELECTION:
				System.out.println("Waa");
				break;
		}
	}

	/**
	 * Unregister view from event system. Remove view from GL render loop.
	 */
	public void removeView(AGLEventListener glEventListener) {
		if (glEventListener != null) {
			glEventListener.destroy();
		}
	}

	public void resetView(boolean reinitialize) {

		useCase.resetContextVA();
		if (containedGLViews == null)
			return;

		enableBusyMode(false);
		pickingManager.enablePicking(true);

		if (reinitialize) {
			ArrayList<ASerializedView> removeNewViews = new ArrayList<ASerializedView>();
			for (ASerializedView view : newViews) {
				if (!(view instanceof SerializedParallelCoordinatesView || view instanceof SerializedHeatMapView)) {
					removeNewViews.add(view);
				}
			}
			newViews.removeAll(removeNewViews);
		}
		else {
			newViews.clear();
		}

		IViewManager viewManager = generalManager.getViewGLCanvasManager();

		if (reinitialize) {
			ArrayList<AGLEventListener> removeView = new ArrayList<AGLEventListener>();
			for (AGLEventListener glView : containedGLViews) {
				if (!(glView instanceof GLParallelCoordinates || glView instanceof GLHeatMap)) {
					removeView.add(glView);
				}
			}
			containedGLViews.removeAll(removeView);
		}
		else {
			containedGLViews.clear();
		}

		if (reinitialize) {
			generalManager.getPathwayManager().resetPathwayVisiblityState();
		}

		// Send out remove broadcast for views that are currently slerped
		for (SlerpAction slerpAction : arSlerpActions) {
			viewManager.getGLEventListener(slerpAction.getElementId()).broadcastElements(
				EVAOperation.REMOVE_ELEMENT);
		}
		arSlerpActions.clear();

		clearRemoteLevel(focusLevel);
		clearRemoteLevel(stackLevel);
		clearRemoteLevel(poolLevel);
		clearRemoteLevel(transitionLevel);

		if (reinitialize) {
			// Move heat map and par coords view to its initial position in the
			// bucket
			for (AGLEventListener view : containedGLViews) {
				if (view instanceof GLParallelCoordinates) {
					stackLevel.getElementByPositionIndex(0).setContainedElementID(view.getID());
					view.setRemoteLevelElement(stackLevel.getElementByPositionIndex(0));
				}
				else if (view instanceof GLHeatMap) {
					focusLevel.getElementByPositionIndex(0).setContainedElementID(view.getID());
					view.setRemoteLevelElement(focusLevel.getElementByPositionIndex(0));
				}
			}
		}

		generalManager.getViewGLCanvasManager().getConnectedElementRepresentationManager().clearAll();
	}

	@Override
	public void resetView() {
		resetView(true);
	}

	protected void clearRemoteLevel(RemoteLevel remoteLevel) {
		int iViewID;
		IViewManager viewManager = generalManager.getViewGLCanvasManager();
		AGLEventListener glEventListener = null;

		for (RemoteLevelElement element : remoteLevel.getAllElements()) {
			iViewID = element.getContainedElementID();

			if (iViewID == -1) {
				continue;
			}

			glEventListener = viewManager.getGLEventListener(iViewID);

			if (glEventListener instanceof GLHeatMap || glEventListener instanceof GLParallelCoordinates) {
				// Remove all elements from heatmap and parallel coordinates
				((AStorageBasedView) glEventListener).resetView();

				if (!glEventListener.isRenderedRemote()) {
					glEventListener.enableBusyMode(false);
				}
			}
			else {
				removeView(glEventListener);
				glEventListener.broadcastElements(EVAOperation.REMOVE_ELEMENT);
			}

			element.setContainedElementID(-1);
		}
	}

	// @Override
	// public RemoteLevel getFocusLevel() {
	// return focusLevel;
	// }
	//
	// @Override
	// public BucketMouseWheelListener getBucketMouseWheelListener() {
	// return bucketMouseWheelListener;
	// }

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		super.reshape(drawable, x, y, width, height);

		// Update aspect ratio and reinitialize stack and focus layer
		layoutRenderStyle.setAspectRatio(fAspectRatio);

		layoutRenderStyle.initFocusLevel();
		layoutRenderStyle.initStackLevel();
		layoutRenderStyle.initPoolLevel(iMouseOverObjectID);
		layoutRenderStyle.initMemoLevel();
	}

	// protected void renderPoolAndMemoLayerBackground(final GL gl) {
	//
	// float fXCorrection = 0.07f; // Detach pool level from stack
	//
	// float fZ;
	// if (bucketMouseWheelListener.isZoomedIn())
	// fZ = -0.005f;
	// else
	// fZ = 4f;
	//
	// float fXScaling = 1;
	// float fYScaling = 1;
	//
	// if (fAspectRatio < 1) {
	// fXScaling = 1 / fAspectRatio;
	// fYScaling = 1;
	// }
	// else {
	// fXScaling = 1;
	// fYScaling = fAspectRatio;
	// }
	//
	// float fLeftSceneBorder = (-2 - fXCorrection) * fXScaling;
	// float fBottomSceneBorder = -2 * fYScaling;
	//
	// if (layoutMode.equals(LayoutMode.BUCKET)) {
	// gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.REMOTE_LEVEL_ELEMENT,
	// iPoolLevelCommonID));
	//
	// gl.glColor4fv(GeneralRenderStyle.PANEL_BACKGROUN_COLOR, 0);
	// gl.glLineWidth(1);
	//
	// gl.glBegin(GL.GL_POLYGON);
	// gl.glVertex3f(fLeftSceneBorder, fBottomSceneBorder, fZ);
	// gl.glVertex3f(fLeftSceneBorder, -fBottomSceneBorder, fZ);
	// gl.glVertex3f(fLeftSceneBorder + BucketLayoutRenderStyle.SIDE_PANEL_WIDTH, -fBottomSceneBorder,
	// fZ);
	// gl
	// .glVertex3f(fLeftSceneBorder + BucketLayoutRenderStyle.SIDE_PANEL_WIDTH, fBottomSceneBorder,
	// fZ);
	// gl.glEnd();
	//
	// if (dragAndDrop.isDragActionRunning() && iMouseOverObjectID == iPoolLevelCommonID) {
	// gl.glLineWidth(5);
	// gl.glColor4f(0.2f, 0.2f, 0.2f, 1);
	// }
	// else {
	// gl.glLineWidth(1);
	// gl.glColor4f(0.4f, 0.4f, 0.4f, 1);
	// }
	//
	// gl.glBegin(GL.GL_LINE_LOOP);
	// gl.glVertex3f(fLeftSceneBorder, fBottomSceneBorder, fZ);
	// gl.glVertex3f(fLeftSceneBorder, -fBottomSceneBorder, fZ);
	// gl.glVertex3f(fLeftSceneBorder + BucketLayoutRenderStyle.SIDE_PANEL_WIDTH, -fBottomSceneBorder,
	// fZ);
	// gl
	// .glVertex3f(fLeftSceneBorder + BucketLayoutRenderStyle.SIDE_PANEL_WIDTH, fBottomSceneBorder,
	// fZ);
	// gl.glEnd();
	//
	// gl.glPopName();
	//
	// // Render selection heat map list background
	// gl.glColor4fv(GeneralRenderStyle.PANEL_BACKGROUN_COLOR, 0);
	// gl.glLineWidth(1);
	// gl.glBegin(GL.GL_POLYGON);
	// gl.glVertex3f(-fLeftSceneBorder, fBottomSceneBorder, fZ);
	// gl.glVertex3f(-fLeftSceneBorder, -fBottomSceneBorder, fZ);
	// gl.glVertex3f(-fLeftSceneBorder - BucketLayoutRenderStyle.SIDE_PANEL_WIDTH, -fBottomSceneBorder,
	// fZ);
	// gl.glVertex3f(-fLeftSceneBorder - BucketLayoutRenderStyle.SIDE_PANEL_WIDTH, fBottomSceneBorder,
	// fZ);
	// gl.glEnd();
	//
	// gl.glColor4f(0.4f, 0.4f, 0.4f, 1);
	// gl.glLineWidth(1);
	// gl.glBegin(GL.GL_LINE_LOOP);
	// gl.glVertex3f(-fLeftSceneBorder, fBottomSceneBorder, fZ);
	// gl.glVertex3f(-fLeftSceneBorder, -fBottomSceneBorder, fZ);
	// gl.glVertex3f(-fLeftSceneBorder - BucketLayoutRenderStyle.SIDE_PANEL_WIDTH, -fBottomSceneBorder,
	// fZ);
	// gl.glVertex3f(-fLeftSceneBorder - BucketLayoutRenderStyle.SIDE_PANEL_WIDTH, fBottomSceneBorder,
	// fZ);
	// gl.glEnd();
	// }
	//
	// // Render caption
	// if (textRenderer == null)
	// return;
	//
	// String sTmp = "POOL AREA";
	// textRenderer.begin3DRendering();
	// textRenderer.setColor(0.6f, 0.6f, 0.6f, 1.0f);
	// textRenderer.draw3D(sTmp, (-1.9f - fXCorrection) / fAspectRatio, -1.97f, fZ + 0.01f, 0.003f);
	// textRenderer.end3DRendering();
	// }

	@Override
	public void broadcastElements(EVAOperation type) {
		// do nothing
	}

	/**
	 * Adds new remote-rendered-views that have been queued for displaying to this view. Only one view is
	 * taken from the list and added for remote rendering per call to this method.
	 * 
	 * @param GL
	 */
	private void initNewView(GL gl) {

		// Views should not be loaded until the browser is finished to be slerped
		if (isSlerpActive)
			return;

		if (!newViews.isEmpty() && GeneralManager.get().getPathwayManager().isPathwayLoadingFinished()
			&& arSlerpActions.isEmpty()) {

			ASerializedView serView = newViews.remove(0);
			AGLEventListener view = createView(gl, serView);
			if (hasFreeViewPosition()) {

				// TODO use this when views should be slerped in
				// if (this instanceof GLPathwayViewBrowser)
				addSlerpActionForView(gl, view);
				// else {
				//
				// if (focusLevel.hasFreePosition()) {
				// poolLevel.getNextFree().setContainedElementID(view.getID());
				// view.broadcastElements(EVAOperation.APPEND_UNIQUE);
				// }
				// else if (poolLevel.hasFreePosition()) {
				// poolLevel.getNextFree().setContainedElementID(view.getID());
				// }
				// else {
				// GeneralManager.get().getLogger().log(
				// new Status(Status.WARNING, GeneralManager.PLUGIN_ID,
				// "No empty space left to add new view!"));
				// }
				// }

				containedGLViews.add(view);
			}
			else {
				newViews.clear();
			}
			if (newViews.isEmpty()) {
				triggerToolBarUpdate();
				enableUserInteraction();
			}
		}
	}

	/**
	 * Triggers a toolbar update by sending an event similar to the view activation
	 * 
	 * @TODO: Move to remote rendering base class
	 */
	private void triggerToolBarUpdate() {

		ViewActivationEvent viewActivationEvent = new ViewActivationEvent();
		viewActivationEvent.setSender(this);
		List<AGLEventListener> views = getRemoteRenderedViews();

		List<Integer> viewIDs = new ArrayList<Integer>();
		viewIDs.add(getID());
		for (AGLEventListener view : views) {
			viewIDs.add(view.getID());
		}

		viewActivationEvent.setViewIDs(viewIDs);

		IEventPublisher eventPublisher = GeneralManager.get().getEventPublisher();
		eventPublisher.triggerEvent(viewActivationEvent);
	}

	/**
	 * Checks if this view has some space left to add at least 1 view
	 * 
	 * @return <code>true</code> if some space is left, <code>false</code> otherwise
	 */
	public boolean hasFreeViewPosition() {
		return focusLevel.hasFreePosition()
			|| (stackLevel.hasFreePosition() && !(layoutRenderStyle instanceof ListLayoutRenderStyle))
			|| poolLevel.hasFreePosition();
	}

	/**
	 * Adds a Slerp-Transition for a view. Usually this is used when a new view is added to the bucket or 2
	 * views change its position in the bucket. The operation does not always succeed. A reason for this is
	 * when no more space is left to slerp the given view to.
	 * 
	 * @param gl
	 * @param view
	 *            the view for which the slerp transition should be added
	 * @return <code>true</code> if adding the slerp action was successfull, <code>false</code> otherwise
	 */
	private boolean addSlerpActionForView(GL gl, AGLEventListener view) {

		RemoteLevelElement origin = spawnLevel.getElementByPositionIndex(0);
		RemoteLevelElement destination = null;

		if (focusLevel.hasFreePosition()) {
			destination = focusLevel.getNextFree();
			view.broadcastElements(EVAOperation.APPEND_UNIQUE);
		}
		// else if (stackLevel.hasFreePosition() && !(layoutRenderStyle instanceof ListLayoutRenderStyle)) {
		// destination = stackLevel.getNextFree();
		// view.broadcastElements(EVAOperation.APPEND_UNIQUE);
		// }
		else if (poolLevel.hasFreePosition()) {
			destination = poolLevel.getNextFree();
		}
		else {
			GeneralManager.get().getLogger()
				.log(
					new Status(IStatus.WARNING, IGeneralManager.PLUGIN_ID,
						"No empty space left to add new view!"));
			newViews.clear();
			return false;
		}

		origin.setContainedElementID(view.getID());
		SlerpAction slerpActionTransition = new SlerpAction(origin, destination);
		arSlerpActions.add(slerpActionTransition);

		view.initRemote(gl, this, glMouseListener, infoAreaManager);
		view.setDetailLevel(EDetailLevel.MEDIUM);

		return true;
	}

	/**
	 * Creates and initializes a new view based on its serialized form. The view is already added to the list
	 * of event receivers and senders.
	 * 
	 * @param gl
	 * @param serView
	 *            serialized form of the view to create
	 * @return the created view ready to be used within the application
	 */
	protected AGLEventListener createView(GL gl, ASerializedView serView) {

		ICommandManager cm = generalManager.getCommandManager();
		ECommandType cmdType = serView.getCreationCommandType();
		CmdCreateGLEventListener cmdView = (CmdCreateGLEventListener) cm.createCommandByType(cmdType);
		cmdView.setAttributesFromSerializedForm(serView);
		// cmdView.setSet(set);
		cmdView.doCommand();

		AGLEventListener glView = cmdView.getCreatedObject();
		glView.setUseCase(useCase);
		glView.setRemoteRenderingGLView(this);
		glView.setSet(set);

		triggerMostRecentDelta();

		return glView;
	}

	// /**
	// * initializes the configuration of a pathway to the configuration currently stored in this
	// * remote-rendering-view.
	// *
	// * @param pathway
	// * pathway to set the configuration
	// */
	// private void initializePathwayView(GLPathway pathway) {
	// pathway.enablePathwayTextures(pathwayTexturesEnabled);
	// pathway.enableNeighborhood(neighborhoodEnabled);
	// pathway.enableGeneMapping(geneMappingEnabled);
	// }

	/**
	 * Triggers the most recent user selection to the views. This is especially needed to initialize new added
	 * views with the current selection information.
	 */
	private void triggerMostRecentDelta() {
		// Trigger last delta to new views
		if (lastSelectionDelta != null) {
			SelectionUpdateEvent event = new SelectionUpdateEvent();
			event.setSender(this);
			event.setSelectionDelta((SelectionDelta) lastSelectionDelta);
			event.setInfo(getShortInfo());
			eventPublisher.triggerEvent(event);
		}
	}

	/**
	 * Disables picking and enables busy mode
	 */
	public void disableUserInteraction() {
		IViewManager canvasManager = generalManager.getViewGLCanvasManager();
		canvasManager.getPickingManager().enablePicking(false);
		canvasManager.requestBusyMode(this);
	}

	/**
	 * Enables picking and disables busy mode
	 */
	public void enableUserInteraction() {
		IViewManager canvasManager = generalManager.getViewGLCanvasManager();
		canvasManager.getPickingManager().enablePicking(true);
		canvasManager.releaseBusyMode(this);
	}

	// public void loadDependentPathways(Set<PathwayGraph> newPathwayGraphs) {
	//
	// // add new pathways to bucket
	// for (PathwayGraph pathway : newPathwayGraphs) {
	// addPathwayView(pathway.getID());
	// }
	//
	// if (!newViews.isEmpty()) {
	// // Zoom out of the bucket when loading pathways
	// if (bucketMouseWheelListener.isZoomedIn()) {
	// bucketMouseWheelListener.triggerZoom(false);
	// }
	// disableUserInteraction();
	// }
	// }

	@Override
	public int getNumberOfSelections(ESelectionType eSelectionType) {
		return 0;
	}

	private void compactPoolLevel() {
		RemoteLevelElement element;
		RemoteLevelElement elementInner;
		for (int iIndex = 0; iIndex < poolLevel.getCapacity(); iIndex++) {
			element = poolLevel.getElementByPositionIndex(iIndex);
			if (element.isFree()) {
				// Search for next element to put it in the free position
				for (int iInnerIndex = iIndex + 1; iInnerIndex < poolLevel.getCapacity(); iInnerIndex++) {
					elementInner = poolLevel.getElementByPositionIndex(iInnerIndex);

					if (elementInner.isFree()) {
						continue;
					}

					element.setContainedElementID(elementInner.getContainedElementID());
					elementInner.setContainedElementID(-1);

					break;
				}
			}
		}
	}

	@Override
	public List<AGLEventListener> getRemoteRenderedViews() {
		return containedGLViews;
	}

	@Override
	public void clearAllSelections() {
		for (AGLEventListener view : containedGLViews) {
			view.clearAllSelections();
		}
	}

	/**
	 * FIXME: should be moved to a bucket-mediator registers the event-listeners to the event framework
	 */
	@Override
	public void registerEventListeners() {
		super.registerEventListeners();
	}

	/**
	 * FIXME: should be moved to a bucket-mediator registers the event-listeners to the event framework
	 */
	@Override
	public void unregisterEventListeners() {

		super.unregisterEventListeners();
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedRemoteRenderingView serializedForm = new SerializedRemoteRenderingView(dataDomain);
		serializedForm.setViewID(this.getID());
		// serializedForm.setPathwayTexturesEnabled(pathwayTexturesEnabled);
		// serializedForm.setNeighborhoodEnabled(neighborhoodEnabled);
		// serializedForm.setGeneMappingEnabled(geneMappingEnabled);
		serializedForm.setConnectionLinesEnabled(connectionLinesEnabled);

		IViewManager viewManager = generalManager.getViewGLCanvasManager();

		ArrayList<ASerializedView> remoteViews =
			new ArrayList<ASerializedView>(focusLevel.getAllElements().size());
		for (RemoteLevelElement rle : focusLevel.getAllElements()) {
			if (rle.getContainedElementID() != -1) {
				AGLEventListener remoteView = viewManager.getGLEventListener(rle.getContainedElementID());
				remoteViews.add(remoteView.getSerializableRepresentation());
			}
		}
		serializedForm.setFocusViews(remoteViews);

		remoteViews = new ArrayList<ASerializedView>(stackLevel.getAllElements().size());
		for (RemoteLevelElement rle : stackLevel.getAllElements()) {
			if (rle.getContainedElementID() != -1) {
				AGLEventListener remoteView = viewManager.getGLEventListener(rle.getContainedElementID());
				remoteViews.add(remoteView.getSerializableRepresentation());
			}
		}
		serializedForm.setStackViews(remoteViews);

		return serializedForm;
	}

	@Override
	public void initFromSerializableRepresentation(ASerializedView ser) {
		resetView(false);

		SerializedTissueViewBrowserView serializedView = (SerializedTissueViewBrowserView) ser;

		// pathwayTexturesEnabled = serializedView.isPathwayTexturesEnabled();
		// neighborhoodEnabled = serializedView.isNeighborhoodEnabled();
		// geneMappingEnabled = serializedView.isGeneMappingEnabled();
		// connectionLinesEnabled = serializedView.isConnectionLinesEnabled();

		// for (ASerializedView remoteSerializedView : serializedView.getFocusViews()) {
		// newViews.add(remoteSerializedView);
		// }
		// for (ASerializedView remoteSerializedView : serializedView.getStackViews()) {
		// newViews.add(remoteSerializedView);
		// }

		newViews.addAll(serializedView.getInitialContainedViews());

		setDisplayListDirty();
	}

	@Override
	public void destroy() {
		selectionTransformer.destroy();
		selectionTransformer = null;
		super.destroy();
	}

	// public boolean isGeneMappingEnabled() {
	// return geneMappingEnabled;
	// }
	//
	// public void setGeneMappingEnabled(boolean geneMappingEnabled) {
	// this.geneMappingEnabled = geneMappingEnabled;
	// }
	//
	// public boolean isPathwayTexturesEnabled() {
	// return pathwayTexturesEnabled;
	// }
	//
	// public void setPathwayTexturesEnabled(boolean pathwayTexturesEnabled) {
	// this.pathwayTexturesEnabled = pathwayTexturesEnabled;
	// }
	//
	// public boolean isNeighborhoodEnabled() {
	// return neighborhoodEnabled;
	// }
	//
	// public void setNeighborhoodEnabled(boolean neighborhoodEnabled) {
	// this.neighborhoodEnabled = neighborhoodEnabled;
	// }

	public boolean isConnectionLinesEnabled() {
		return connectionLinesEnabled;
	}

	public void setConnectionLinesEnabled(boolean connectionLinesEnabled) {
		this.connectionLinesEnabled = connectionLinesEnabled;
	}

	public RemoteLevel getStackLevel() {
		return stackLevel;
	}

	public AGLConnectionLineRenderer getGlConnectionLineRenderer() {
		return glConnectionLineRenderer;
	}

	protected void removeSelection(int iElementID) {

	}

	public void setSlerpActive(boolean isSlerpActive) {
		this.isSlerpActive = isSlerpActive;
	}
}
