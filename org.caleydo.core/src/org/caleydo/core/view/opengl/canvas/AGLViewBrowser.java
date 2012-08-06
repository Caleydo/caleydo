/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 * 
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.view.opengl.canvas;

import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import gleem.linalg.open.Transform;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.awt.GLCanvas;

import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.selection.events.ISelectionUpdateHandler;
import org.caleydo.core.data.virtualarray.EVAOperation;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.view.ViewActivationEvent;
import org.caleydo.core.event.view.tablebased.SelectionUpdateEvent;
import org.caleydo.core.id.object.ManagedObjectType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedSingleTablePerspectiveBasedView;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.util.system.Time;
import org.caleydo.core.view.IDataDomainBasedView;
import org.caleydo.core.view.IView;
import org.caleydo.core.view.ViewManager;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.remote.AGLConnectionLineRenderer;
import org.caleydo.core.view.opengl.canvas.remote.ARemoteViewLayoutRenderStyle;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.canvas.remote.list.ListLayoutRenderStyle;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.core.view.opengl.util.drag.GLDragAndDrop;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteElementManager;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevel;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevelElement;
import org.caleydo.core.view.opengl.util.slerp.SlerpAction;
import org.caleydo.core.view.opengl.util.slerp.SlerpMod;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.core.view.vislink.ConnectedElementRepresentationManager;
import org.caleydo.core.view.vislink.RemoteRenderingTransformer;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Composite;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;

/**
 * Base class for view browsers such as tissue and pathway browser view
 * 
 * @author Marc Streit
 * @author Alexander Lex
 * @author Werner Puff
 */
public abstract class AGLViewBrowser extends AGLView implements ISelectionUpdateHandler,
		IGLRemoteRenderingView, IDataDomainBasedView<IDataDomain> {

	protected IDataDomain dataDomain;

	private static final int SLERP_RANGE = 1000;
	private static final int SLERP_SPEED = 1400;

	public final static float SIDE_PANEL_WIDTH = 0.8f;

	protected static final int MAX_VIEWS = 14;

	private int mouseOverObjectID = -1;

	protected RemoteLevel focusLevel;
	protected RemoteLevel stackLevel;
	protected RemoteLevel poolLevel;
	protected RemoteLevel transitionLevel;
	protected RemoteLevel spawnLevel;
	protected RemoteLevel externalSelectionLevel;

	private ArrayList<SlerpAction> arSlerpActions;

	private Time time;

	protected EIconTextures viewSymbol;

	/**
	 * Slerp factor: 0 = source; 1 = destination
	 */
	private int iSlerpFactor = 0;

	/**
	 * LayoutRenderer for connection lines. A concrete instance has to be
	 * specified in sub-classes
	 */
	protected AGLConnectionLineRenderer glConnectionLineRenderer;

	private GLDragAndDrop dragAndDrop;

	private ARemoteViewLayoutRenderStyle layoutRenderStyle;

	protected ArrayList<ASerializedView> newViews;

	protected ArrayList<AGLView> containedGLViews;

	/**
	 * The current view in which the user is performing actions.
	 */
	private int iActiveViewID = -1;

	private SelectionDelta lastSelectionDelta;

	/**
	 * Used for dragging views to the pool area.
	 */
	private int iPoolLevelCommonID = -1;

	private boolean connectionLinesEnabled = true;

	// private GLInfoAreaManager infoAreaManager;

	/**
	 * Transformation utility object to transform and project view related
	 * coordinates
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
	// protected ToggleNavigationModeListener toggleNavigationModeListener =
	// null;
	// protected ToggleZoomListener toggleZoomListener = null;
	// protected EnableConnectionLinesListener enableConnectionLinesListener =
	// null;
	// protected DisableConnectionLinesListener disableConnectionLinesListener =
	// null;
	// protected ResetViewListener resetViewListener = null;
	// protected SelectionUpdateListener selectionUpdateListener = null;

	/**
	 * Constructor.
	 * 
	 * @param viewType
	 *            TODO
	 * @param viewName
	 *            TODO
	 */
	public AGLViewBrowser(GLCanvas glCanvas, Composite parentComposite,
			final ViewFrustum viewFrustum, String viewType, String viewName) {

		super(glCanvas, parentComposite, viewFrustum, viewType, viewName);

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

		// this has to be done in implementing views
		// glConnectionLineRenderer = new
		// GLConnectionLineRendererBucket(focusLevel, stackLevel);

		if (glMouseListener != null)
			glMouseListener.addGLCanvas(this);

		arSlerpActions = new ArrayList<SlerpAction>();

		containedGLViews = new ArrayList<AGLView>();
		newViews = new ArrayList<ASerializedView>();

		dragAndDrop = new GLDragAndDrop();

		iPoolLevelCommonID = generalManager.getIDCreator().createID(
				ManagedObjectType.REMOTE_LEVEL_ELEMENT);
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
		transform.setScale(new Vec3f(fScalingFactorZoomedIn, fScalingFactorZoomedIn,
				fScalingFactorZoomedIn));
		transform.setRotation(new Rotf(new Vec3f(0, 0, 0), 0));

		stackLevel.getElementByPositionIndex(0).setTransform(transform);

		// LEFT BUCKET WALL
		transform = new Transform();
		transform.setTranslation(new Vec3f(1.6f, yPos, 0));
		transform.setScale(new Vec3f(fScalingFactorZoomedIn, fScalingFactorZoomedIn,
				fScalingFactorZoomedIn));
		transform.setRotation(new Rotf(new Vec3f(0, 0, 0), 0));
		stackLevel.getElementByPositionIndex(1).setTransform(transform);

		// BOTTOM BUCKET WALL
		transform = new Transform();
		transform.setTranslation(new Vec3f(3.2f, yPos, 0));
		transform.setScale(new Vec3f(fScalingFactorZoomedIn, fScalingFactorZoomedIn,
				fScalingFactorZoomedIn));
		transform.setRotation(new Rotf(new Vec3f(0, 0, 0), 0));
		stackLevel.getElementByPositionIndex(2).setTransform(transform);

		// RIGHT BUCKET WALL
		transform = new Transform();
		transform.setTranslation(new Vec3f(4.8f, yPos, 0));
		transform.setScale(new Vec3f(fScalingFactorZoomedIn, fScalingFactorZoomedIn,
				fScalingFactorZoomedIn));
		transform.setRotation(new Rotf(new Vec3f(0, 0, 0), 0));
		stackLevel.getElementByPositionIndex(3).setTransform(transform);
	}

	@Override
	public void initLocal(final GL2 gl) {
		init(gl);
	}

	@Override
	public void initRemote(final GL2 gl, final AGLView glParentView,
			final GLMouseListener glMouseListener) {

		// Register keyboard listener to GL2 canvas
		// glParentView.getParentComposite().getDisplay().asyncExec(new
		// Runnable() {
		// public void run() {
		// glParentView.getParentComposite().addKeyListener(glKeyListener);
		// }
		// });

		this.glMouseListener = glMouseListener;
		displayListIndex = gl.glGenLists(1);
		init(gl);
	}

	@Override
	public void init(final GL2 gl) {

		ArrayList<RemoteLevelElement> remoteLevelElementWhiteList = new ArrayList<RemoteLevelElement>();
		remoteLevelElementWhiteList.addAll(focusLevel.getAllElements());
		remoteLevelElementWhiteList.addAll(stackLevel.getAllElements());
		remoteLevelElementWhiteList.addAll(poolLevel.getAllElements());
		selectionTransformer = new RemoteRenderingTransformer(uniqueID,
				remoteLevelElementWhiteList);

		addInitialViews();

		gl.glClearColor(0.5f, 0.5f, 0.5f, 1f);

		if (glConnectionLineRenderer != null) {
			glConnectionLineRenderer.init(gl);
		}

		time = new Time();
		((Time) time).rebase();
	}

	protected abstract void addInitialViews();

	@Override
	public void displayLocal(final GL2 gl) {

		for (AGLView view : containedGLViews)
			view.processEvents();

		// if (bIsDisplayListDirtyLocal)
		// {
		// buildDisplayList(gl);
		// bIsDisplayListDirtyLocal = false;
		// }

		display(gl);

		if (busyState != EBusyState.OFF) {
			renderBusyMode(gl);
		}

		checkForHits(gl);

		// gl.glCallList(iGLDisplayListIndexLocal);
	}

	@Override
	public void displayRemote(final GL2 gl) {
		for (AGLView view : containedGLViews)
			view.processEvents();

		display(gl);
		checkForHits(gl);

		ConnectedElementRepresentationManager cerm = GeneralManager.get()
				.getViewManager().getConnectedElementRepresentationManager();
		cerm.doViewRelatedTransformation(gl, selectionTransformer);
	}

	@Override
	public void display(final GL2 gl) {
		time.update();

		if (focusLevel.getElementByPositionIndex(0).isFree() && arSlerpActions.isEmpty()) {
			renderSymbol(gl, viewSymbol, 3);
			String text = "";
			if (getViewType().equals("org.caleydo.view.pathwabrowser"))
				text = "Trigger pathway loading first!";
			else
				text = "Filter below 20 patients";

			renderText(gl, text, 0.01f, 1, 0.5f, 0);
		}

		// Update the pool transformations according to the current mouse over
		// object
		initPoolLevel(mouseOverObjectID);
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
			glConnectionLineRenderer.setActiveViewID(iActiveViewID); // FIXME:
			// added
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

			// System.out.println("over: " +externalID);
			// System.out.println("dragged: " +iDraggedObjectId);

			// Prevent user from dragging element onto selection level
			if (!RemoteElementManager.get().hasItem(mouseOverObjectID)
					|| !externalSelectionLevel.containsElement(RemoteElementManager.get()
							.getItem(mouseOverObjectID))) {
				RemoteLevelElement mouseOverElement = null;

				// Check if a drag and drop action is performed onto the pool
				// level
				if (mouseOverObjectID == iPoolLevelCommonID) {
					mouseOverElement = poolLevel.getNextFree();
				} else if (mouseOverElement == null
						&& mouseOverObjectID != iDraggedObjectId) {
					mouseOverElement = RemoteElementManager.get().getItem(
							mouseOverObjectID);
				}

				if (mouseOverElement != null) {
					RemoteLevelElement originElement = RemoteElementManager.get()
							.getItem(iDraggedObjectId);

					AGLView mouseOverView = mouseOverElement.getGLView();
					AGLView originView = originElement.getGLView();

					mouseOverElement.setGLView(originView);
					originElement.setGLView(mouseOverView);

					if (originView != null) {
						originView.setRemoteLevelElement(mouseOverElement);
					}

					if (mouseOverView != null) {
						mouseOverView.setRemoteLevelElement(originElement);
					}

					updateViewDetailLevels(originElement);
					updateViewDetailLevels(mouseOverElement);

					if (mouseOverElement.getGLView() != null) {
						if (poolLevel.containsElement(originElement)
								&& (stackLevel.containsElement(mouseOverElement) || focusLevel
										.containsElement(mouseOverElement))) {
							mouseOverElement.getGLView().broadcastElements(
									EVAOperation.APPEND_UNIQUE);
						}

						if (poolLevel.containsElement(mouseOverElement)
								&& (stackLevel.containsElement(originElement) || focusLevel
										.containsElement(originElement))) {
							mouseOverElement.getGLView().broadcastElements(
									EVAOperation.REMOVE_ELEMENT);
						}
					}
				}
			}

			generalManager.getViewManager().getConnectedElementRepresentationManager()
					.clearTransformedConnections();
			dragAndDrop.stopDragAction();
		}
	}

	public void renderBucketWall(final GL2 gl, boolean bRenderBorder,
			RemoteLevelElement element) {
		// Highlight potential view drop destination
		if (dragAndDrop.isDragActionRunning() && element.getID() == mouseOverObjectID) {
			gl.glLineWidth(5);
			gl.glColor4f(0.2f, 0.2f, 0.2f, 1);
			gl.glBegin(GL2.GL_LINE_LOOP);
			gl.glVertex3f(0, 0, 0.01f);
			gl.glVertex3f(0, 8, 0.01f);
			gl.glVertex3f(8, 8, 0.01f);
			gl.glVertex3f(8, 0, 0.01f);
			gl.glEnd();
		}

		if (arSlerpActions.isEmpty()) {
			gl.glColor4f(1f, 1f, 1f, 1.0f); // normal mode
		} else {
			gl.glColor4f(1f, 1f, 1f, 0.3f);
		}

		if (!newViews.isEmpty()) {
			gl.glColor4f(1f, 1f, 1f, 0.3f);
		}

		gl.glBegin(GL2.GL_POLYGON);
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

	private void renderRemoteLevel(final GL2 gl, final RemoteLevel level) {
		for (RemoteLevelElement element : level.getAllElements()) {
			renderRemoteLevelElement(gl, element, level);

			if (!(layoutRenderStyle instanceof ListLayoutRenderStyle)) {
				renderEmptyBucketWall(gl, element, level);
			}
		}
	}

	private void renderRemoteLevelElement(final GL2 gl, RemoteLevelElement element,
			RemoteLevel level) {
		// // Check if view is visible
		// if (!level.getElementVisibilityById(viewID))
		// return;

		AGLView glView = element.getGLView();
		if (glView == null) {
			return;
		}

		gl.glPushName(pickingManager.getPickingID(uniqueID,
				PickingType.REMOTE_LEVEL_ELEMENT, element.getID()));
		gl.glPushName(pickingManager.getPickingID(uniqueID,
				PickingType.REMOTE_VIEW_SELECTION, glView.getID()));

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

		if (level == focusLevel) {

			gl.glLineWidth(2);
			gl.glColor4f(0.2f, 0.2f, 0.2f, 1);
			gl.glBegin(GL2.GL_LINE_LOOP);
			gl.glVertex3f(0, 0, 0.f);
			gl.glVertex3f(0, 8, 0.f);
			gl.glVertex3f(8, 8, 0.f);
			gl.glVertex3f(8, 0, 0.f);
			gl.glEnd();

			gl.glColor4f(0.9f, 0.9f, 0.9f, 1);

			gl.glBegin(GL2.GL_POLYGON);
			gl.glVertex3f(0, 0, -0.001f);
			gl.glVertex3f(0, 8, -0.001f);
			gl.glVertex3f(8, 8, -0.001f);
			gl.glVertex3f(8, 0, -0.001f);
			gl.glEnd();
		}

		if (level == poolLevel) {
			String sRenderText = glView.getViewName();

			// FIXME: after view plugin restructuring
			// if (glView instanceof GLTissue)
			// sRenderText = ((GLTissue) glView).getLabel();

			// Limit sub view name in length
			int iMaxChars;
			iMaxChars = 20;

			if (sRenderText.length() > iMaxChars && scale.x() < 0.06f) {
				sRenderText = sRenderText.subSequence(0, iMaxChars - 3) + "...";
			}

			float fTextScalingFactor = 0.09f;
			float fTextXPosition = 0f;

			float fXShift = -7.1f;
			// FIXME: after view plugin restructuring
			// if (this instanceof GLTissueViewBrowser)
			// fXShift = -0.8f;

			if (element.getID() == mouseOverObjectID) {
				renderPoolSelection(gl, translation.x() + fXShift, translation.y()
						* scale.y() + 5.2f,

				(float) textRenderer.getBounds(sRenderText).getWidth() * 0.06f + 23, 6f,
						element);
				gl.glTranslatef(0.8f, 1.3f, 0);

				fTextScalingFactor = 0.075f;
				fTextXPosition = 12f;
			} else {
				// Render view background frame
				Texture tempTexture = textureManager.getIconTexture(gl,
						EIconTextures.POOL_VIEW_BACKGROUND);
				tempTexture.enable(gl);
				tempTexture.bind(gl);

				float fFrameWidth = 9.5f;
				TextureCoords texCoords = tempTexture.getImageTexCoords();

				gl.glColor4f(1, 1, 1, 0.75f);

				gl.glBegin(GL2.GL_POLYGON);
				gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
				gl.glVertex3f(-0.7f, -0.6f + fFrameWidth, -0.01f);
				gl.glTexCoord2f(texCoords.left(), texCoords.top());
				gl.glVertex3f(-0.7f + fFrameWidth, -0.6f + fFrameWidth, -0.01f);
				gl.glTexCoord2f(texCoords.right(), texCoords.top());
				gl.glVertex3f(-0.7f + fFrameWidth, -0.6f, -0.01f);
				gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
				gl.glVertex3f(-0.7f, -0.6f, -0.01f);
				gl.glEnd();

				tempTexture.disable(gl);

				fTextXPosition = 9.5f;
			}

			int iNumberOfGenesSelected = glView
					.getNumberOfSelections(SelectionType.SELECTION);
			int iNumberOfGenesMouseOver = glView
					.getNumberOfSelections(SelectionType.MOUSE_OVER);

			textRenderer.begin3DRendering();

			if (element.getID() == mouseOverObjectID) {
				textRenderer.setColor(1, 1, 1, 1);
			} else {
				textRenderer.setColor(0, 0, 0, 1);
			}

			if (iNumberOfGenesMouseOver == 0 && iNumberOfGenesSelected == 0) {
				textRenderer.draw3D(sRenderText, fTextXPosition, 3f, 0.1f,
						fTextScalingFactor);
			} else {
				textRenderer.draw3D(sRenderText, fTextXPosition, 4.5f, 0.1f,
						fTextScalingFactor);
			}

			textRenderer.end3DRendering();

			gl.glLineWidth(4);

			if (element.getID() == mouseOverObjectID) {
				gl.glTranslatef(2.2f, 0.5f, 0);
			}

			if (iNumberOfGenesMouseOver > 0) {
				if (element.getID() == mouseOverObjectID) {
					gl.glTranslatef(-2.5f, 0, 0);
				}

				textRenderer.begin3DRendering();
				textRenderer.draw3D(Integer.toString(iNumberOfGenesMouseOver),
						fTextXPosition + 9, 2.4f, 0, fTextScalingFactor);
				textRenderer.end3DRendering();

				if (element.getID() == mouseOverObjectID) {
					gl.glTranslatef(2.5f, 0, 0);
				}

				gl.glColor4fv(SelectionType.MOUSE_OVER.getColor(), 0);
				gl.glBegin(GL2.GL_LINES);
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

				if (element.getID() == mouseOverObjectID) {
					gl.glTranslatef(-2.5f, 0, 0);
				}

				textRenderer.begin3DRendering();
				textRenderer.draw3D(Integer.toString(iNumberOfGenesSelected),
						fTextXPosition + 9, 2.5f, 0, fTextScalingFactor);
				textRenderer.end3DRendering();

				if (element.getID() == mouseOverObjectID) {
					gl.glTranslatef(2.5f, 0, 0);
				}

				gl.glColor4fv(SelectionType.SELECTION.getColor(), 0);
				gl.glBegin(GL2.GL_LINES);
				gl.glVertex3f(10, 2.9f, 0f);
				gl.glVertex3f(18, 2.9f, 0f);
				gl.glVertex3f(20, 2.9f, 0f);
				gl.glVertex3f(29, 2.9f, 0f);
				gl.glEnd();

				if (iNumberOfGenesMouseOver > 0) {
					gl.glTranslatef(0, 1.8f, 0);
				}
			}

			if (element.getID() == mouseOverObjectID) {
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
			} else {
				renderBucketWall(gl, true, element);
			}
		}

		glView.displayRemote(gl);

		gl.glPopMatrix();

		gl.glPopName();
		gl.glPopName();
	}

	private void renderEmptyBucketWall(final GL2 gl, RemoteLevelElement element,
			RemoteLevel level) {
		gl.glPushMatrix();

		gl.glPushName(pickingManager.getPickingID(uniqueID,
				PickingType.REMOTE_LEVEL_ELEMENT, element.getID()));

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
				&& !level.equals(poolLevel) && !level.equals(externalSelectionLevel)) {
			renderBucketWall(gl, true, element);
		}

		gl.glPopName();

		gl.glPopMatrix();
	}

	private void renderHandles(final GL2 gl) {

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
		if (element.getGLView() != null) {

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

			gl.glTranslatef(-translation.x(), -translation.y() + 2 * 0.075f
					- fYCorrection, -translation.z() - 0.001f);
		}
	}

	// private void renderStackViewHandleBarZoomedIn(final GL2 gl,
	// RemoteLevelElement element) {
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
	// gl.glTranslatef(translation.x(), translation.y() - 2 * 0.075f +
	// fYCorrection,
	// translation.z() + 0.001f);
	// gl.glScalef(scale.x() * 4, scale.y() * 4, scale.z());
	// renderNavigationHandleBar(gl, element, 2, 0.075f, false, 2);
	// gl.glScalef(1 / (scale.x() * 4), 1 / (scale.y() * 4), 1 / scale.z());
	// gl.glTranslatef(-translation.x(), -translation.y() + 2 * 0.075f -
	// fYCorrection,
	// -translation.z() - 0.001f);
	// }

	private void renderNavigationHandleBar(final GL2 gl, RemoteLevelElement element,
			float fHandleWidth, float fHandleHeight, boolean bUpsideDown,
			float fScalingFactor) {

		// Render icons
		gl.glTranslatef(0, 2 + fHandleHeight, 0);
		renderSingleHandle(gl, element.getID(), PickingType.REMOTE_VIEW_DRAG,
				EIconTextures.NAVIGATION_DRAG_VIEW, fHandleHeight, fHandleHeight);
		gl.glTranslatef(fHandleWidth - 2 * fHandleHeight, 0, 0);
		if (bUpsideDown) {
			gl.glRotatef(180, 1, 0, 0);
			gl.glTranslatef(0, fHandleHeight, 0);
		}
		renderSingleHandle(gl, element.getID(), PickingType.REMOTE_VIEW_LOCK,
				EIconTextures.NAVIGATION_LOCK_VIEW, fHandleHeight, fHandleHeight);
		if (bUpsideDown) {
			gl.glTranslatef(0, -fHandleHeight, 0);
			gl.glRotatef(-180, 1, 0, 0);
		}
		gl.glTranslatef(fHandleHeight, 0, 0);
		renderSingleHandle(gl, element.getID(), PickingType.REMOTE_VIEW_REMOVE,
				EIconTextures.NAVIGATION_REMOVE_VIEW, fHandleHeight, fHandleHeight);
		gl.glTranslatef(-fHandleWidth + fHandleHeight, -2 - fHandleHeight, 0);

		// Render background (also draggable)
		gl.glPushName(pickingManager.getPickingID(uniqueID, PickingType.REMOTE_VIEW_DRAG,
				element.getID()));
		gl.glColor3f(0.25f, 0.25f, 0.25f);
		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex3f(0 + fHandleHeight, 2 + fHandleHeight, 0);
		gl.glVertex3f(fHandleWidth - 2 * fHandleHeight, 2 + fHandleHeight, 0);
		gl.glVertex3f(fHandleWidth - 2 * fHandleHeight, 2, 0);
		gl.glVertex3f(0 + fHandleHeight, 2, 0);
		gl.glEnd();

		gl.glPopName();

		// Render view information
		String sText = element.getGLView().getViewName();

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
				- (float) textRenderer.getBounds(sText).getWidth() / 2f
				* fTextScalingFactor, 2.02f, 0f, fTextScalingFactor);
		textRenderer.end3DRendering();

		if (bUpsideDown) {
			gl.glTranslatef(0, 4 + fHandleHeight, 0);
			gl.glRotatef(-180, 1, 0, 0);
		}
	}

	private void renderSingleHandle(final GL2 gl, int iRemoteLevelElementID,
			PickingType ePickingType, EIconTextures eIconTexture, float fWidth,
			float fHeight) {
		gl.glPushName(pickingManager.getPickingID(uniqueID, ePickingType,
				iRemoteLevelElementID));

		Texture tempTexture = textureManager.getIconTexture(gl, eIconTexture);
		tempTexture.enable(gl);
		tempTexture.bind(gl);

		TextureCoords texCoords = tempTexture.getImageTexCoords();
		gl.glColor3f(1, 1, 1);
		gl.glBegin(GL2.GL_POLYGON);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(0, -fHeight, 0f);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(0, 0, 0f);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(fWidth, 0, 0f);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(fWidth, -fHeight, 0f);
		gl.glEnd();

		tempTexture.disable(gl);

		gl.glPopName();
	}

	private void renderPoolSelection(final GL2 gl, float fXOrigin, float fYOrigin,
			float fWidth, float fHeight, RemoteLevelElement element) {
		float fPanelSideWidth = 11f;

		float z = 0.06f;

		gl.glColor3f(0.25f, 0.25f, 0.25f);
		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex3f(fXOrigin + 10.2f, fYOrigin - fHeight / 2f + fHeight, z);
		gl.glVertex3f(fXOrigin + 10.2f + fWidth, fYOrigin - fHeight / 2f + fHeight, z);
		gl.glVertex3f(fXOrigin + 10.2f + fWidth, fYOrigin - fHeight / 2f, z);
		gl.glVertex3f(fXOrigin + 10.2f, fYOrigin - fHeight / 2f, z);
		gl.glEnd();

		Texture tempTexture = textureManager.getIconTexture(gl,
				EIconTextures.POOL_VIEW_BACKGROUND_SELECTION);
		tempTexture.enable(gl);
		tempTexture.bind(gl);

		TextureCoords texCoords = tempTexture.getImageTexCoords();

		gl.glColor4f(1, 1, 1, 0.75f);

		gl.glBegin(GL2.GL_POLYGON);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(fXOrigin + fPanelSideWidth, fYOrigin - fHeight, -0.01f);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(fXOrigin + fPanelSideWidth, fYOrigin + fHeight, -0.01f);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(fXOrigin, fYOrigin + fHeight, -0.01f);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(fXOrigin, fYOrigin - fHeight, -0.01f);
		gl.glEnd();

		tempTexture.disable(gl);

		gl.glPopName();
		gl.glPopName();

		int fHandleScaleFactor = 18;
		gl.glTranslatef(fXOrigin - 1.2f, fYOrigin - fHeight / 2f + fHeight - 1f, 1.8f);
		gl.glScalef(fHandleScaleFactor, fHandleScaleFactor, fHandleScaleFactor);
		renderSingleHandle(gl, element.getID(), PickingType.REMOTE_VIEW_DRAG,
				EIconTextures.POOL_DRAG_VIEW, 0.1f, 0.1f);
		gl.glTranslatef(0, -0.2f, 0);
		renderSingleHandle(gl, element.getID(), PickingType.REMOTE_VIEW_REMOVE,
				EIconTextures.POOL_REMOVE_VIEW, 0.1f, 0.1f);
		gl.glTranslatef(0, 0.2f, 0);
		gl.glScalef(1f / fHandleScaleFactor, 1f / fHandleScaleFactor,
				1f / fHandleScaleFactor);
		gl.glTranslatef(-fXOrigin + 1.2f, -fYOrigin + fHeight / 2f - fHeight + 1f, -1.8f);

		// gl.glColor3f(0.25f, 0.25f, 0.25f);
		// gl.glBegin(GL2.GL_POLYGON);
		// gl.glVertex3f(fXOrigin + 3f, fYOrigin - fHeight / 2f + fHeight -
		// 2.5f, 0f);
		// gl.glVertex3f(fXOrigin + 5.1f, fYOrigin - fHeight / 2f + fHeight -
		// 2.5f, 0f);
		// gl.glVertex3f(fXOrigin + 5.1f, fYOrigin- fHeight / 2f + 1.5f, 0f);
		// gl.glVertex3f(fXOrigin + 3f, fYOrigin- fHeight / 2f + 1.5f , 0f);
		// gl.glEnd();

		gl.glPushName(pickingManager.getPickingID(uniqueID,
				PickingType.REMOTE_LEVEL_ELEMENT, element.getID()));
		gl.glPushName(pickingManager.getPickingID(uniqueID,
				PickingType.REMOTE_VIEW_SELECTION, element.getID()));
	}

	private void doSlerpActions(final GL2 gl) {
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

	private void slerpView(final GL2 gl, SlerpAction slerpAction) {
		int viewID = slerpAction.getElementId();

		SlerpMod slerpMod = new SlerpMod();

		if (iSlerpFactor == 0) {
			slerpMod.playSlerpSound();
		}

		Transform transform = slerpMod.interpolate(slerpAction
				.getOriginRemoteLevelElement().getTransform(), slerpAction
				.getDestinationRemoteLevelElement().getTransform(), (float) iSlerpFactor
				/ SLERP_RANGE);

		gl.glPushMatrix();

		slerpMod.applySlerp(gl, transform, true, false);

		generalManager.getViewManager().getGLView(viewID).displayRemote(gl);

		gl.glPopMatrix();

		// Check if slerp action is finished
		if (iSlerpFactor >= SLERP_RANGE) {
			arSlerpActions.remove(slerpAction);
			iSlerpFactor = 0;
			slerpAction.finished();
			RemoteLevelElement destinationElement = slerpAction
					.getDestinationRemoteLevelElement();
			updateViewDetailLevels(destinationElement);
		}

		// After last slerp action is done the line connections are turned on
		// again
		if (arSlerpActions.isEmpty()) {
			if (glConnectionLineRenderer != null) {
				glConnectionLineRenderer.enableRendering(true);
			}

			// generalManager.getViewGLCanvasManager().getInfoAreaManager().enable(!bEnableNavigationOverlay);
			generalManager.getViewManager().getConnectedElementRepresentationManager()
					.clearTransformedConnections();
		}
	}

	private void updateViewDetailLevels(RemoteLevelElement element) {
		RemoteLevel destinationLevel = element.getRemoteLevel();

		if (element.getGLView() == null)
			return;

		AGLView glActiveSubView = element.getGLView();
		glActiveSubView.setRemoteLevelElement(element);

		// Update detail level of moved view when slerp action is finished;
		if (destinationLevel == focusLevel) {
			glActiveSubView.setDetailLevel(EDetailLevel.MEDIUM);
		} else if (destinationLevel == stackLevel) {
			glActiveSubView.setDetailLevel(EDetailLevel.LOW);
		} else if (destinationLevel == poolLevel
				|| destinationLevel == externalSelectionLevel) {
			glActiveSubView.setDetailLevel(EDetailLevel.VERY_LOW);
		}

		compactPoolLevel();
	}

	private void loadViewToFocusLevel(final int iRemoteLevelElementID) {
		RemoteLevelElement element = RemoteElementManager.get().getItem(
				iRemoteLevelElementID);

		// Check if other slerp action is currently running
		// if (iSlerpFactor > 0 && iSlerpFactor < SLERP_RANGE)
		// return;

		arSlerpActions.clear();

		if (element.getGLView() == null)
			return;

		// Slerp focus view to pool
		SlerpAction makePlaceSlerpActionTransition = new SlerpAction(
				focusLevel.getElementByPositionIndex(0), poolLevel.getNextFree());
		arSlerpActions.add(makePlaceSlerpActionTransition);

		// Slerp selected view to focus position
		SlerpAction slerpActionTransition = new SlerpAction(element,
				focusLevel.getElementByPositionIndex(0));
		arSlerpActions.add(slerpActionTransition);

		iSlerpFactor = 0;
	}

	@Override
	public void handleSelectionUpdate(SelectionDelta selectionDelta) {
		lastSelectionDelta = selectionDelta;
	}

	@Override
	protected void handlePickingEvents(PickingType pickingType, PickingMode pickingMode,
			int externalID, Pick pick) {

		switch (pickingType) {
		case REMOTE_VIEW_DRAG:

			switch (pickingMode) {
			case CLICKED:

				if (!dragAndDrop.isDragActionRunning()) {
					// System.out.println("Start drag!");
					dragAndDrop.startDragAction(externalID);
				}

				mouseOverObjectID = externalID;

				compactPoolLevel();

				break;
			}
			break;

		case REMOTE_VIEW_REMOVE:

			switch (pickingMode) {
			case CLICKED:

				RemoteLevelElement element = RemoteElementManager.get().getItem(
						externalID);

				AGLView glView = element.getGLView();

				// // Unregister all elements of the view that is
				// removed
				// glEventListener.broadcastElements(EVAOperation.REMOVE_ELEMENT);

				removeView(glView);
				element.setGLView(null);
				containedGLViews.remove(glView);

				if (element.getRemoteLevel() == poolLevel) {
					compactPoolLevel();
				}

				// FIXME: after view plugin restructuring
				// if (glView instanceof GLTissue)
				// removeSelection(((GLTissue) glView)
				// .getExperimentIndex());

				setDisplayListDirty();

				break;
			}
			break;

		case REMOTE_VIEW_LOCK:

			switch (pickingMode) {
			case CLICKED:

				RemoteLevelElement element = RemoteElementManager.get().getItem(
						externalID);

				// Toggle lock flag
				element.lock(!element.isLocked());

				break;
			}
			break;

		case REMOTE_LEVEL_ELEMENT:

			switch (pickingMode) {
			case MOUSE_OVER:
			case DRAGGED:
				mouseOverObjectID = externalID;
				break;
			case CLICKED:

				// Do not handle click if element is dragged
				if (dragAndDrop.isDragActionRunning()) {
					break;
				}

				// Check if view is contained in pool level
				for (RemoteLevelElement element : poolLevel.getAllElements()) {
					if (element.getID() == externalID) {
						loadViewToFocusLevel(externalID);
						break;
					}
				}
				break;
			}
			break;

		case REMOTE_VIEW_SELECTION:

			switch (pickingMode) {
			case MOUSE_OVER:

				// generalManager.getViewGLCanvasManager().getInfoAreaManager()
				// .setDataAboutView(externalID);

				// Prevent update flood when moving mouse over view
				if (iActiveViewID == externalID) {
					break;
				}

				iActiveViewID = externalID;

				setDisplayListDirty();

				// TODO
				// generalManager.getEventPublisher().triggerEvent(
				// EMediatorType.VIEW_SELECTION,
				// generalManager.getViewGLCanvasManager().getGLEventListener(
				// externalID), );

				break;

			case CLICKED:

				// generalManager.getViewGLCanvasManager().getInfoAreaManager()
				// .setDataAboutView(externalID);

				break;
			case RIGHT_CLICKED:

				break;

			}
			break;
		}
	}

	/**
	 * Unregister view from event system. Remove view from GL2 render loop.
	 */
	public void removeView(AGLView glEventListener) {
		// FIXME: check why this caused a null pointer ex
		// if (glEventListener != null) {
		// glEventListener.destroy();
		// }
	}

	public void resetView(boolean reinitialize) {

		// useCase.resetContextVA();
		if (containedGLViews == null)
			return;

		enableBusyMode(false);
		pickingManager.enablePicking(true);

		if (reinitialize) {
			ArrayList<ASerializedView> removeNewViews = new ArrayList<ASerializedView>();
			for (ASerializedView view : newViews) {
				if (!(view.getViewType().equals("org.caleydo.view.parcoords") || view
						.getViewType().equals("org.caleydo.view.heatmap"))) {
					removeNewViews.add(view);
				}
			}
			newViews.removeAll(removeNewViews);
		} else {
			newViews.clear();
		}

		ViewManager viewManager = generalManager.getViewManager();

		if (reinitialize) {
			ArrayList<AGLView> removeView = new ArrayList<AGLView>();
			for (AGLView glView : containedGLViews) {
				if (!(glView.getViewType().equals("org.caleydo.view.parcoords") || glView
						.getViewType().equals("org.caleydo.view.heatmap"))) {
					removeView.add(glView);
				}
			}
			containedGLViews.removeAll(removeView);
		} else {
			containedGLViews.clear();
		}

		// Send out remove broadcast for views that are currently slerped
		for (SlerpAction slerpAction : arSlerpActions) {
			viewManager.getGLView(slerpAction.getElementId()).broadcastElements(
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
			for (AGLView view : containedGLViews) {
				if (view.getViewType().equals("org.caleydo.view.parcoords")) {
					stackLevel.getElementByPositionIndex(0).setGLView(view);
					view.setRemoteLevelElement(stackLevel.getElementByPositionIndex(0));
				} else if (view.getViewType().equals("org.caleydo.view.heatmap")) {
					focusLevel.getElementByPositionIndex(0).setGLView(view);
					view.setRemoteLevelElement(focusLevel.getElementByPositionIndex(0));
				}
			}
		}

		generalManager.getViewManager().getConnectedElementRepresentationManager()
				.clearAll();
	}

	@Override
	public void resetView() {
		resetView(true);
	}

	protected void clearRemoteLevel(RemoteLevel remoteLevel) {
		AGLView glView = null;

		for (RemoteLevelElement element : remoteLevel.getAllElements()) {
			if (element.getGLView() == null) {
				continue;
			}

			glView = element.getGLView();

			if (glView.getViewType().equals("org.caleydo.view.heatmap")
					|| glView.getViewType().equals("org.caleydo.view.parcoords")) {
				// Remove all elements from heatmap and parallel coordinates
				((ATableBasedView) glView).resetView();

				if (!glView.isRenderedRemote()) {
					glView.enableBusyMode(false);
				}
			} else {
				removeView(glView);
				glView.broadcastElements(EVAOperation.REMOVE_ELEMENT);
			}

			element.setGLView(null);
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
		layoutRenderStyle.initPoolLevel(mouseOverObjectID);
		layoutRenderStyle.initMemoLevel();
	}

	// protected void renderPoolAndMemoLayerBackground(final GL2 gl) {
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
	// gl.glPushName(pickingManager.getPickingID(uniqueID,
	// EPickingType.REMOTE_LEVEL_ELEMENT,
	// iPoolLevelCommonID));
	//
	// gl.glColor4fv(GeneralRenderStyle.PANEL_BACKGROUN_COLOR, 0);
	// gl.glLineWidth(1);
	//
	// gl.glBegin(GL2.GL_POLYGON);
	// gl.glVertex3f(fLeftSceneBorder, fBottomSceneBorder, fZ);
	// gl.glVertex3f(fLeftSceneBorder, -fBottomSceneBorder, fZ);
	// gl.glVertex3f(fLeftSceneBorder +
	// BucketLayoutRenderStyle.SIDE_PANEL_WIDTH, -fBottomSceneBorder,
	// fZ);
	// gl
	// .glVertex3f(fLeftSceneBorder + BucketLayoutRenderStyle.SIDE_PANEL_WIDTH,
	// fBottomSceneBorder,
	// fZ);
	// gl.glEnd();
	//
	// if (dragAndDrop.isDragActionRunning() && iMouseOverObjectID ==
	// iPoolLevelCommonID) {
	// gl.glLineWidth(5);
	// gl.glColor4f(0.2f, 0.2f, 0.2f, 1);
	// }
	// else {
	// gl.glLineWidth(1);
	// gl.glColor4f(0.4f, 0.4f, 0.4f, 1);
	// }
	//
	// gl.glBegin(GL2.GL_LINE_LOOP);
	// gl.glVertex3f(fLeftSceneBorder, fBottomSceneBorder, fZ);
	// gl.glVertex3f(fLeftSceneBorder, -fBottomSceneBorder, fZ);
	// gl.glVertex3f(fLeftSceneBorder +
	// BucketLayoutRenderStyle.SIDE_PANEL_WIDTH, -fBottomSceneBorder,
	// fZ);
	// gl
	// .glVertex3f(fLeftSceneBorder + BucketLayoutRenderStyle.SIDE_PANEL_WIDTH,
	// fBottomSceneBorder,
	// fZ);
	// gl.glEnd();
	//
	// gl.glPopName();
	//
	// // Render selection heat map list background
	// gl.glColor4fv(GeneralRenderStyle.PANEL_BACKGROUN_COLOR, 0);
	// gl.glLineWidth(1);
	// gl.glBegin(GL2.GL_POLYGON);
	// gl.glVertex3f(-fLeftSceneBorder, fBottomSceneBorder, fZ);
	// gl.glVertex3f(-fLeftSceneBorder, -fBottomSceneBorder, fZ);
	// gl.glVertex3f(-fLeftSceneBorder -
	// BucketLayoutRenderStyle.SIDE_PANEL_WIDTH, -fBottomSceneBorder,
	// fZ);
	// gl.glVertex3f(-fLeftSceneBorder -
	// BucketLayoutRenderStyle.SIDE_PANEL_WIDTH, fBottomSceneBorder,
	// fZ);
	// gl.glEnd();
	//
	// gl.glColor4f(0.4f, 0.4f, 0.4f, 1);
	// gl.glLineWidth(1);
	// gl.glBegin(GL2.GL_LINE_LOOP);
	// gl.glVertex3f(-fLeftSceneBorder, fBottomSceneBorder, fZ);
	// gl.glVertex3f(-fLeftSceneBorder, -fBottomSceneBorder, fZ);
	// gl.glVertex3f(-fLeftSceneBorder -
	// BucketLayoutRenderStyle.SIDE_PANEL_WIDTH, -fBottomSceneBorder,
	// fZ);
	// gl.glVertex3f(-fLeftSceneBorder -
	// BucketLayoutRenderStyle.SIDE_PANEL_WIDTH, fBottomSceneBorder,
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
	// textRenderer.draw3D(sTmp, (-1.9f - fXCorrection) / fAspectRatio, -1.97f,
	// fZ + 0.01f, 0.003f);
	// textRenderer.end3DRendering();
	// }

	@Override
	public void broadcastElements(EVAOperation type) {
		// do nothing
	}

	/**
	 * Adds new remote-rendered-views that have been queued for displaying to
	 * this view. Only one view is taken from the list and added for remote
	 * rendering per call to this method.
	 * 
	 * @param GL
	 */
	private void initNewView(GL2 gl) {

		// Views should not be loaded until the browser is finished to be
		// slerped
		if (isSlerpActive)
			return;

		if (!newViews.isEmpty() && readyForLoadingNewViews() && arSlerpActions.isEmpty()) {

			ASerializedView serView = newViews.remove(0);
			AGLView view = createView(gl, serView);
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
			} else {
				newViews.clear();
			}
			if (newViews.isEmpty()) {
				triggerToolBarUpdate();
				enableUserInteraction();
			}
		}
	}

	public boolean readyForLoadingNewViews() {
		return true;
	}

	/**
	 * Triggers a toolbar update by sending an event similar to the view
	 * activation
	 * 
	 * @TODO: Move to remote rendering base class
	 */
	private void triggerToolBarUpdate() {

		ViewActivationEvent viewActivationEvent = new ViewActivationEvent();
		viewActivationEvent.setSender(this);
		List<AGLView> glViews = getRemoteRenderedViews();
		List<IView> views = new ArrayList<IView>();
		views.add(this);
		for (AGLView view : glViews) {
			views.add(view);
		}

		viewActivationEvent.setViews(views);

		EventPublisher eventPublisher = GeneralManager.get().getEventPublisher();
		eventPublisher.triggerEvent(viewActivationEvent);
	}

	/**
	 * Checks if this view has some space left to add at least 1 view
	 * 
	 * @return <code>true</code> if some space is left, <code>false</code>
	 *         otherwise
	 */
	public boolean hasFreeViewPosition() {
		return focusLevel.hasFreePosition()
				|| (stackLevel.hasFreePosition() && !(layoutRenderStyle instanceof ListLayoutRenderStyle))
				|| poolLevel.hasFreePosition();
	}

	/**
	 * Adds a Slerp-Transition for a view. Usually this is used when a new view
	 * is added to the bucket or 2 views change its position in the bucket. The
	 * operation does not always succeed. A reason for this is when no more
	 * space is left to slerp the given view to.
	 * 
	 * @param gl
	 * @param view
	 *            the view for which the slerp transition should be added
	 * @return <code>true</code> if adding the slerp action was successfull,
	 *         <code>false</code> otherwise
	 */
	private boolean addSlerpActionForView(GL2 gl, AGLView view) {

		RemoteLevelElement origin = spawnLevel.getElementByPositionIndex(0);
		RemoteLevelElement destination = null;

		if (focusLevel.hasFreePosition()) {
			destination = focusLevel.getNextFree();
			// view.broadcastElements(EVAOperation.APPEND_UNIQUE);
		}
		// else if (stackLevel.hasFreePosition() && !(layoutRenderStyle
		// instanceof ListLayoutRenderStyle)) {
		// destination = stackLevel.getNextFree();
		// view.broadcastElements(EVAOperation.APPEND_UNIQUE);
		// }
		else if (poolLevel.hasFreePosition()) {
			destination = poolLevel.getNextFree();
		} else {
			Logger.log(new Status(IStatus.WARNING, this.toString(),
					"No empty space left to add new view!"));
			newViews.clear();
			return false;
		}

		origin.setGLView(view);
		SlerpAction slerpActionTransition = new SlerpAction(origin, destination);
		arSlerpActions.add(slerpActionTransition);

		view.initRemote(gl, this, glMouseListener);
		view.setDetailLevel(EDetailLevel.MEDIUM);

		return true;
	}

	/**
	 * Creates and initializes a new view based on its serialized form. The view
	 * is already added to the list of event receivers and senders.
	 * 
	 * @param gl
	 * @param serView
	 *            serialized form of the view to create
	 * @return the created view ready to be used within the application
	 */
	@SuppressWarnings("unchecked")
	protected AGLView createView(GL2 gl, ASerializedView serView) {

		@SuppressWarnings("rawtypes")
		Class viewClass;
		try {
			viewClass = Class.forName(serView.getViewClassType());
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("Cannot find class for view "
					+ serView.getViewType());
		}

		AGLView glView = GeneralManager.get().getViewManager()
				.createGLView(viewClass, parentGLCanvas, parentComposite, viewFrustum);
		glView.setRemoteRenderingGLView(this);

		if (glView instanceof IDataDomainBasedView<?>) {
			((IDataDomainBasedView<IDataDomain>) glView).setDataDomain(DataDomainManager
					.get().getDataDomainByID(
							((ASerializedSingleTablePerspectiveBasedView) serView).getDataDomainID()));
		}

		glView.initialize();
		triggerMostRecentDelta();

		return glView;
	}

	// /**
	// * initializes the configuration of a pathway to the configuration
	// currently stored in this
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
	 * Triggers the most recent user selection to the views. This is especially
	 * needed to initialize new added views with the current selection
	 * information.
	 */
	private void triggerMostRecentDelta() {
		// Trigger last delta to new views
		if (lastSelectionDelta != null) {
			SelectionUpdateEvent event = new SelectionUpdateEvent();
			event.setSender(this);
			event.setSelectionDelta((SelectionDelta) lastSelectionDelta);
			eventPublisher.triggerEvent(event);
		}
	}

	/**
	 * Disables picking and enables busy mode
	 */
	public void disableUserInteraction() {
		ViewManager canvasManager = generalManager.getViewManager();
		canvasManager.getPickingManager().enablePicking(false);
		canvasManager.requestBusyMode(this);
	}

	/**
	 * Enables picking and disables busy mode
	 */
	public void enableUserInteraction() {
		ViewManager canvasManager = generalManager.getViewManager();
		canvasManager.getPickingManager().enablePicking(true);
		canvasManager.releaseBusyMode(this);
	}

	@Override
	public int getNumberOfSelections(SelectionType SelectionType) {
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

					element.setGLView(elementInner.getGLView());
					elementInner.setGLView(null);

					break;
				}
			}
		}
	}

	@Override
	public List<AGLView> getRemoteRenderedViews() {
		return containedGLViews;
	}

	/**
	 * FIXME: should be moved to a bucket-mediator registers the event-listeners
	 * to the event framework
	 */
	@Override
	public void registerEventListeners() {
		super.registerEventListeners();
	}

	/**
	 * FIXME: should be moved to a bucket-mediator registers the event-listeners
	 * to the event framework
	 */
	@Override
	public void unregisterEventListeners() {

		super.unregisterEventListeners();
	}

	@Override
	public ASerializedView getSerializableRepresentation() {

		// SerializedRemoteRenderingView serializedForm = new
		// SerializedRemoteRenderingView(
		// dataDomain);
		// serializedForm.setViewID(this.getID());
		// // serializedForm.setPathwayTexturesEnabled(pathwayTexturesEnabled);
		// // serializedForm.setNeighborhoodEnabled(neighborhoodEnabled);
		// // serializedForm.setGeneMappingEnabled(geneMappingEnabled);
		// serializedForm.setConnectionLinesEnabled(connectionLinesEnabled);
		//
		// ViewManager viewManager = generalManager.getViewGLCanvasManager();
		//
		// ArrayList<ASerializedView> remoteViews = new
		// ArrayList<ASerializedView>(
		// focusLevel.getAllElements().size());
		// for (RemoteLevelElement rle : focusLevel.getAllElements()) {
		// if (rle.getGLView() != null) {
		// AGLView remoteView = rle.getGLView();
		// remoteViews.add(remoteView.getSerializableRepresentation());
		// }
		// }
		// serializedForm.setFocusViews(remoteViews);
		//
		// remoteViews = new ArrayList<ASerializedView>(stackLevel
		// .getAllElements().size());
		// for (RemoteLevelElement rle : stackLevel.getAllElements()) {
		// if (rle.getGLView() != null) {
		// AGLView remoteView = rle.getGLView();
		// remoteViews.add(remoteView.getSerializableRepresentation());
		// }
		// }
		// serializedForm.setStackViews(remoteViews);
		//
		// return serializedForm;

		throw new IllegalStateException("TODO implement view browser serialization");
	}

	@Override
	public void initFromSerializableRepresentation(ASerializedView ser) {
		resetView(false);

		// FIXME: after view plugin restructuring
		// SerializedTissueViewBrowserView serializedView =
		// (SerializedTissueViewBrowserView) ser;

		// pathwayTexturesEnabled = serializedView.isPathwayTexturesEnabled();
		// neighborhoodEnabled = serializedView.isNeighborhoodEnabled();
		// geneMappingEnabled = serializedView.isGeneMappingEnabled();
		// connectionLinesEnabled = serializedView.isConnectionLinesEnabled();

		// for (ASerializedView remoteSerializedView :
		// serializedView.getFocusViews()) {
		// newViews.add(remoteSerializedView);
		// }
		// for (ASerializedView remoteSerializedView :
		// serializedView.getStackViews()) {
		// newViews.add(remoteSerializedView);
		// }

		// FIXME: after view plugin restructuring
		// newViews.addAll(serializedView.getInitialContainedViews());

		setDisplayListDirty();
	}

	@Override
	public void destroyViewSpecificContent(GL2 gl) {
		selectionTransformer.destroy();
		selectionTransformer = null;
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

	@Override
	public IDataDomain getDataDomain() {
		return dataDomain;
	}

	@Override
	public void setDataDomain(IDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

	public void renderText(GL2 gl, String text, float size, float x, float y, float z) {
		textRenderer.setColor(0.3f, 0.3f, 0.3f, 1);
		textRenderer.begin3DRendering();
		textRenderer.draw3D(text, x, y, z, size);
		textRenderer.end3DRendering();
	}
}
