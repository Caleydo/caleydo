package org.caleydo.view.dataflipper;

import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import gleem.linalg.open.Transform;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.data.CmdDataCreateDataDomain;
import org.caleydo.core.command.view.opengl.CmdCreateView;
import org.caleydo.core.data.collection.ESetType;
import org.caleydo.core.data.graph.pathway.core.PathwayGraph;
import org.caleydo.core.data.selection.EVAOperation;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.StorageVAType;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IDataDomain;
import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.datadomain.ADataDomain;
import org.caleydo.core.manager.datadomain.EDataDomain;
import org.caleydo.core.manager.event.view.ViewActivationEvent;
import org.caleydo.core.manager.event.view.remote.LoadPathwayEvent;
import org.caleydo.core.manager.event.view.remote.LoadPathwaysByGeneEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.manager.specialized.EOrganism;
import org.caleydo.core.manager.view.ConnectedElementRepresentationManager;
import org.caleydo.core.manager.view.RemoteRenderingTransformer;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.system.SystemTime;
import org.caleydo.core.util.system.Time;
import org.caleydo.core.view.IView;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.GLGlyph;
import org.caleydo.core.view.opengl.canvas.remote.AGLConnectionLineRenderer;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteElementManager;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevelElement;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.caleydo.core.view.opengl.util.slerp.SlerpAction;
import org.caleydo.core.view.opengl.util.slerp.SlerpMod;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.rcp.view.listener.AddPathwayListener;
import org.caleydo.rcp.view.listener.IRemoteRenderingHandler;
import org.caleydo.rcp.view.listener.LoadPathwaysByGeneListener;
import org.caleydo.view.bucket.GLBucket;
import org.caleydo.view.heatmap.hierarchical.GLHierarchicalHeatMap;
import org.caleydo.view.parcoords.GLParallelCoordinates;
import org.caleydo.view.pathway.GLPathway;
import org.caleydo.view.pathwaybrowser.GLPathwayViewBrowser;
import org.caleydo.view.tissue.GLTissue;
import org.caleydo.view.tissuebrowser.GLTissueViewBrowser;

import com.sun.opengl.util.j2d.TextRenderer;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;

public class GLDataFlipper extends AGLView implements IGLRemoteRenderingView,
		IRemoteRenderingHandler {

	public final static String VIEW_ID = "org.caleydo.view.dataflipper";

	private static final int SLERP_RANGE = 1000;
	private static final int SLERP_SPEED = 1400;

	private static final int MAX_SIDE_VIEWS = 10;

	private ArrayList<ASerializedView> newViews;

	private ArrayList<AGLView> containedGLViews;

	private RemoteLevelElement focusElement;
	private ArrayList<RemoteLevelElement> stackElementsLeft;
	private ArrayList<RemoteLevelElement> stackElementsRight;

	protected AGLConnectionLineRenderer glConnectionLineRenderer;

	/**
	 * Transformation utility object to transform and project view related
	 * coordinates
	 */
	protected RemoteRenderingTransformer selectionTransformer;

	private GLInfoAreaManager infoAreaManager;

	private TextRenderer textRenderer;

	private TextureManager textureManager;

	private ArrayList<SlerpAction> arSlerpActions;

	private Time time;

	private RemoteLevelElement lastPickedRemoteLevelElement;
	private int iLastPickedViewID;

	/**
	 * Slerp factor: 0 = source; 1 = destination
	 */
	private int iSlerpFactor = 0;

	// private boolean focusZoom = false;

	private boolean isExperimentCountOK = false;

	private boolean isPathwayContentAvailable = false;

	private AddPathwayListener addPathwayListener = null;
	private LoadPathwaysByGeneListener loadPathwaysByGeneListener = null;

	boolean isPatientGuideActive = true;
	boolean isPatientAlternativeGuideActive = false;
	boolean isTissueGuideActive = false;
	boolean isGeneticGuideActive = false;
	boolean renderGeneticViews = false;
	boolean renderPathwayViews = false;

	/**
	 * Constructor.
	 */
	public GLDataFlipper(GLCaleydoCanvas glCanvas, final String sLabel,
			final IViewFrustum viewFrustum) {

		super(glCanvas, sLabel, viewFrustum, true);

		viewType = GLDataFlipper.VIEW_ID;

		// // Unregister standard mouse wheel listener
		// parentGLCanvas.removeMouseWheelListener(glMouseListener);
		// // Register specialized bucket mouse wheel listener
		// parentGLCanvas.addMouseWheelListener(bucketMouseWheelListener);
		// // parentGLCanvas.addMouseListener(bucketMouseWheelListener);

		textRenderer = new TextRenderer(new Font("Arial", Font.PLAIN, 24), false);
		textureManager = new TextureManager();
		arSlerpActions = new ArrayList<SlerpAction>();

		glMouseListener.addGLCanvas(this);

		newViews = new ArrayList<ASerializedView>();
		containedGLViews = new ArrayList<AGLView>();
		stackElementsRight = new ArrayList<RemoteLevelElement>();
		stackElementsLeft = new ArrayList<RemoteLevelElement>();

		// TODO: Move to render style
		Transform transform = new Transform();
		transform.setTranslation(new Vec3f(-0.1f, 0.28f, 4));// -1.7f, -1.5f,
		// 4));
		transform.setScale(new Vec3f(1 / 2.5f, 1 / 2.5f, 1 / 2.5f));
		// transform.setTranslation(new Vec3f(-1.95f, -1.4f, 0));
		// transform.setScale(new Vec3f(1 / 1.15f, 1 / 1.15f, 1 / 1.1f));

		focusElement = new RemoteLevelElement(null);
		focusElement.setTransform(transform);
		RemoteElementManager.get().registerItem(focusElement);

		for (int iSideViewsIndex = 1; iSideViewsIndex <= MAX_SIDE_VIEWS; iSideViewsIndex++) {
			RemoteLevelElement newElement = new RemoteLevelElement(null);
			transform = new Transform();
			transform.setTranslation(new Vec3f(-2.1f - iSideViewsIndex / 1.8f + 1.5f,
					-1.25f + 1.5f, 4f));
			transform.setScale(new Vec3f(1 / 2.4f, 1 / 2.4f, 1 / 2.4f));
			transform.setRotation(new Rotf(new Vec3f(0, 1, 0), Vec3f
					.convertGrad2Radiant(96)));
			newElement.setTransform(transform);
			stackElementsLeft.add(newElement);
			RemoteElementManager.get().registerItem(newElement);

			newElement = new RemoteLevelElement(null);
			transform = new Transform();
			transform.setTranslation(new Vec3f(3.15f + iSideViewsIndex / 1.8f + 1.5f,
					-1.55f + 1.5f, -1f));
			transform.setScale(new Vec3f(1 / 1.95f, 1 / 1.95f, 1 / 2f));
			transform.setRotation(new Rotf(new Vec3f(0, -1, 0), Vec3f
					.convertGrad2Radiant(96)));
			newElement.setTransform(transform);
			stackElementsRight.add(newElement);
			RemoteElementManager.get().registerItem(newElement);
		}

		glConnectionLineRenderer = new GLConnectionLineRendererDataFlipper(focusElement,
				stackElementsLeft, stackElementsRight);

		CmdDataCreateDataDomain cmd = new CmdDataCreateDataDomain(
				ECommandType.CREATE_DATA_DOMAIN);
		cmd.setAttributes(EDataDomain.PATHWAY_DATA);
		cmd.doCommand();
		ADataDomain pathwayDataDomain = (ADataDomain) cmd.getCreatedObject();
		pathwayDataDomain.setOrganism(EOrganism.HOMO_SAPIENS);
		//pathwayDataDomain.setSet(generalManager.getUseCase(EDataDomain.PATHWAY_DATA).getSet());
		generalManager.addUseCase(pathwayDataDomain);
		
		cmd = new CmdDataCreateDataDomain(
				ECommandType.CREATE_DATA_DOMAIN);
		cmd.setAttributes(EDataDomain.TISSUE_DATA);
		cmd.doCommand();
		ADataDomain tissueDataDomain = (ADataDomain) cmd.getCreatedObject();
		pathwayDataDomain.setOrganism(EOrganism.HOMO_SAPIENS);
		//pathwayDataDomain.setSet(generalManager.getUseCase(EDataDomain.TISSUE_DATA).getSet());
		generalManager.addUseCase(pathwayDataDomain);

	}

	@Override
	public void initLocal(final GL gl) {
		// iGLDisplayList = gl.glGenLists(1);

		ArrayList<RemoteLevelElement> remoteLevelElementWhiteList = new ArrayList<RemoteLevelElement>();
		remoteLevelElementWhiteList.add(focusElement);
		remoteLevelElementWhiteList.add(stackElementsLeft.get(0));
		remoteLevelElementWhiteList.add(stackElementsRight.get(0));
		// remoteLevelElementWhiteList.add(stackElementsLeft.get(1));
		// remoteLevelElementWhiteList.add(stackElementsRight.get(1));
		selectionTransformer = new RemoteRenderingTransformer(iUniqueID,
				remoteLevelElementWhiteList);

		init(gl);
	}

	@Override
	public void initRemote(final GL gl, final AGLView glParentView,
			final GLMouseListener glMouseListener, GLInfoAreaManager infoAreaManager) {

		throw new IllegalStateException("Not implemented to be rendered remote");
	}

	@Override
	public void init(final GL gl) {
		gl.glClearColor(0.5f, 0.5f, 0.5f, 1f);

		time = new SystemTime();
		((SystemTime) time).rebase();

		infoAreaManager = new GLInfoAreaManager();
		infoAreaManager.initInfoInPlace(viewFrustum);

		if (glConnectionLineRenderer != null) {
			glConnectionLineRenderer.init(gl);
		}

	}

	@Override
	public void displayLocal(final GL gl) {
		processEvents();
		for (AGLView view : containedGLViews)
			view.processEvents();
		if (!isVisible())
			return;
		pickingManager.handlePicking(this, gl);

		display(gl);

		if (eBusyModeState != EBusyModeState.OFF) {
			renderBusyMode(gl);
		}

		checkForHits(gl);

		ConnectedElementRepresentationManager cerm = GeneralManager.get()
				.getViewGLCanvasManager().getConnectedElementRepresentationManager();
		cerm.doViewRelatedTransformation(gl, selectionTransformer);

		// gl.glCallList(iGLDisplayListIndexLocal);
	}

	@Override
	public void displayRemote(final GL gl) {
		display(gl);
	}

	@Override
	public void display(final GL gl) {

		time.update();
		// processEvents();

		// gl.glCallList(iGLDisplayList);

		doSlerpActions(gl);
		initNewView(gl);

		for (RemoteLevelElement element : stackElementsLeft) {
			renderRemoteLevelElement(gl, element);
		}

		for (RemoteLevelElement element : stackElementsRight) {
			renderRemoteLevelElement(gl, element);
		}

		renderRemoteLevelElement(gl, focusElement);

		// if (focusZoom) {
		renderHandles(gl);
		renderDataViewIcons(gl, EDataDomain.CLINICAL_DATA);
		renderDataViewIcons(gl, EDataDomain.TISSUE_DATA);
		renderDataViewIcons(gl, EDataDomain.GENETIC_DATA);
		renderDataViewIcons(gl, EDataDomain.PATHWAY_DATA);
		renderGuidanceConnections(gl);

		if (glConnectionLineRenderer != null && arSlerpActions.isEmpty()) {
			glConnectionLineRenderer.render(gl);
		}

		float fZTranslation = 0;
		fZTranslation = 4f;

		gl.glTranslatef(0, 0, fZTranslation);
		contextMenu.render(gl, this);
		gl.glTranslatef(0, 0, -fZTranslation);
	}

	private void renderRemoteLevelElement(final GL gl, RemoteLevelElement element) {

		AGLView glView = element.getGLView();
		if (glView == null) {
			return;
		}

		int vaSize = generalManager.getUseCase(EDataDomain.GENETIC_DATA).getStorageVA(
				StorageVAType.STORAGE).size();
		if (vaSize > 20)
			isExperimentCountOK = false;
		else
			isExperimentCountOK = true;

		// if (((glEventListener instanceof GLParallelCoordinates &&
		// glEventListener.getSet().getSetType() ==
		// ESetType.GENE_EXPRESSION_DATA)
		// || glEventListener instanceof GLHierarchicalHeatMap
		// || glEventListener instanceof GLTissueViewBrowser || (glEventListener
		// instanceof
		// GLPathwayViewBrowser && renderPathwayViews))
		// && !isExperimentCountOK) {
		// return;
		// }
		//
		// if (((glEventListener instanceof GLParallelCoordinates &&
		// glEventListener.getSet().getSetType() ==
		// ESetType.GENE_EXPRESSION_DATA)
		// || glEventListener instanceof GLHierarchicalHeatMap
		// || glEventListener instanceof GLTissueViewBrowser || glEventListener
		// instanceof
		// GLPathwayViewBrowser)
		// && !renderGeneticViews) {
		// return;
		// }
		//
		// if ((glEventListener instanceof GLPathwayViewBrowser &&
		// !isPathwayContentAvailable)
		// || (glEventListener instanceof GLPathwayViewBrowser &&
		// !renderPathwayViews))
		// return;

		// if (glView instanceof GLGlyph
		// || (glView instanceof GLParallelCoordinates && glView.getSet()
		// .getSetType() != ESetType.GENE_EXPRESSION_DATA)
		// || (((glView instanceof GLHierarchicalHeatMap || glView instanceof
		// GLParallelCoordinates) && glView
		// .getSet().getSetType() == ESetType.GENE_EXPRESSION_DATA)
		// && isExperimentCountOK && renderGeneticViews)
		// || (glView instanceof GLTissueViewBrowser && isTissueGuideActive)
		// || (glView instanceof GLPathwayViewBrowser
		// && isPathwayContentAvailable && renderPathwayViews)) {

		gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.REMOTE_LEVEL_ELEMENT, element.getID()));
		gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.VIEW_SELECTION,
				glView.getID()));

		gl.glPushMatrix();

		Transform transform = element.getTransform();
		Vec3f translation = transform.getTranslation();
		Rotf rot = transform.getRotation();
		Vec3f scale = transform.getScale();
		Vec3f axis = new Vec3f();
		float fAngle = rot.get(axis);

		if (glView instanceof GLBucket) {

			gl.glTranslatef(translation.x() - 1.5f, translation.y() - 1.5f, translation
					.z());
			gl.glScalef(scale.x(), scale.y(), scale.z());
			renderBucketWall(gl, true);
			gl.glScalef(1 / scale.x(), 1 / scale.y(), 1 / scale.z());
			gl.glTranslatef(-translation.x() + 1.5f, -translation.y() + 1.5f,
					-translation.z());

			gl.glTranslatef(translation.x() + 0.14f, translation.y() - 0.09f, translation
					.z() + 2);
			gl.glRotatef(Vec3f.convertRadiant2Grad(fAngle), axis.x(), axis.y(), axis.z());
			gl.glScalef(scale.x(), scale.y(), scale.z());
		} else {
			gl.glTranslatef(translation.x() - 1.5f, translation.y() - 1.5f, translation
					.z());
			gl.glRotatef(Vec3f.convertRadiant2Grad(fAngle), axis.x(), axis.y(), axis.z());
			gl.glScalef(scale.x(), scale.y(), scale.z());

			renderBucketWall(gl, true);
		}

		if ((stackElementsLeft.contains(element) || focusElement == element)
				&& glView instanceof GLTissueViewBrowser)
			((GLTissueViewBrowser) glView).setPoolSide(true);
		else if (stackElementsRight.contains(element)
				&& glView instanceof GLTissueViewBrowser)
			((GLTissueViewBrowser) glView).setPoolSide(false);

		glView.displayRemote(gl);

		gl.glPopMatrix();

		gl.glPopName();
		gl.glPopName();

		// }
	}

	/**
	 * Adds new remote-rendered-views that have been queued for displaying to
	 * this view. Only one view is taken from the list and added for remote
	 * rendering per call to this method.
	 * 
	 * @param GL
	 */
	private void initNewView(GL gl) {

		// if(arSlerpActions.isEmpty())
		// {
		if (!newViews.isEmpty()) {
			ASerializedView serView = newViews.remove(0);
			AGLView view = createView(gl, serView);

			// addSlerpActionForView(gl, view);

			// TODO: remove when activating slerp
			view.initRemote(gl, this, glMouseListener, infoAreaManager);
			// view.getViewFrustum().considerAspectRatio(true);

			containedGLViews.add(view);

			if (focusElement.isFree()) {
				focusElement.setGLView(view);
				view.setRemoteLevelElement(focusElement);
				view.setDetailLevel(EDetailLevel.HIGH);
			} else {

				if (newViews.size() % 2 == 0) {

					Iterator<RemoteLevelElement> iter = stackElementsLeft.iterator();
					while (iter.hasNext()) {
						RemoteLevelElement element = iter.next();
						if (element.isFree()) {
							element.setGLView(view);
							view.setRemoteLevelElement(element);
							view.setDetailLevel(EDetailLevel.LOW);
							break;
						}
					}
				} else {
					Iterator<RemoteLevelElement> iter = stackElementsRight.iterator();
					while (iter.hasNext()) {
						RemoteLevelElement element = iter.next();
						if (element.isFree()) {
							element.setGLView(view);
							view.setRemoteLevelElement(element);
							view.setDetailLevel(EDetailLevel.LOW);
							break;
						}
					}
				}
			}

			if (newViews.isEmpty()) {
				triggerToolBarUpdate();
				enableUserInteraction();
			}
		}
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

		IEventPublisher eventPublisher = GeneralManager.get().getEventPublisher();
		eventPublisher.triggerEvent(viewActivationEvent);
	}

	@Override
	public List<AGLView> getRemoteRenderedViews() {
		return containedGLViews;
	}

	@Override
	public void initFromSerializableRepresentation(ASerializedView ser) {
		// resetView(false);

		SerializedDataFlipperView serializedView = (SerializedDataFlipperView) ser;
		newViews.addAll(serializedView.getInitialContainedViews());

		setDisplayListDirty();
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
	private AGLView createView(GL gl, ASerializedView serView) {

		ICommandManager commandManager = generalManager.getCommandManager();
		CmdCreateView cmdView = (CmdCreateView) commandManager
				.createCommandByType(ECommandType.CREATE_GL_VIEW);
		cmdView.setViewID(serView.getViewType());
		cmdView.setAttributesFromSerializedForm(serView);
		cmdView.doCommand();

		AGLView glView = cmdView.getCreatedObject();
		glView.setRemoteRenderingGLView(this);

		return glView;
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

	private void doSlerpActions(final GL gl) {
		if (arSlerpActions.isEmpty())
			return;

		// SlerpAction tmpSlerpAction = arSlerpActions.get(0);

		if (iSlerpFactor == 0) {

			for (SlerpAction tmpSlerpAction : arSlerpActions) {
				tmpSlerpAction.start();
			}
		}

		if (iSlerpFactor < SLERP_RANGE) {
			// Makes animation rendering CPU speed independent
			iSlerpFactor += SLERP_SPEED * time.deltaT();

			if (iSlerpFactor > SLERP_RANGE) {
				iSlerpFactor = SLERP_RANGE;
			}
		}

		for (SlerpAction tmpSlerpAction : arSlerpActions) {
			slerpView(gl, tmpSlerpAction);
		}

		// Check if slerp action is finished
		if (iSlerpFactor >= SLERP_RANGE) {

			// // Finish in reverse order - otherwise the target ID would
			// overwrite the next
			// for (int iSlerpIndex = arSlerpActions.size() - 1; iSlerpIndex >=
			// 0; iSlerpIndex--) {
			// arSlerpActions.get(iSlerpIndex).finished();
			// }

			for (SlerpAction tmpSlerpAction : arSlerpActions) {
				tmpSlerpAction.finished();

				updateViewDetailLevels(tmpSlerpAction.getDestinationRemoteLevelElement());

				AGLView glView = generalManager.getViewGLCanvasManager().getGLView(
						tmpSlerpAction.getElementId());

				if (glView instanceof GLTissueViewBrowser)
					((GLTissueViewBrowser) glView).setSlerpActive(false);

				if (glView instanceof GLParallelCoordinates
						&& glView.getSet().getSetType() == ESetType.GENE_EXPRESSION_DATA) {

					boolean renderConnectionsLeft = true;
					if (glView == focusElement.getGLView())
						renderConnectionsLeft = false;

					((GLParallelCoordinates) glView)
							.setRenderConnectionState(renderConnectionsLeft);

				}
			}

			arSlerpActions.clear();
			iSlerpFactor = 0;

			// Trigger chain move when selected view has not reached the focus
			// position
			if (iLastPickedViewID != focusElement.getGLView().getID())
				chainMove(lastPickedRemoteLevelElement);
		}
	}

	private void slerpView(final GL gl, SlerpAction slerpAction) {
		int iViewID = slerpAction.getElementId();

		if (iViewID == -1)
			return;

		SlerpMod slerpMod = new SlerpMod();

		if (iSlerpFactor == 0) {
			slerpMod.playSlerpSound();
		}

		Transform transform = slerpMod.interpolate(slerpAction
				.getOriginRemoteLevelElement().getTransform(), slerpAction
				.getDestinationRemoteLevelElement().getTransform(), (float) iSlerpFactor
				/ SLERP_RANGE);

		gl.glPushMatrix();

		slerpMod.applySlerp(gl, transform, true, true);

		AGLView glEventListener = generalManager.getViewGLCanvasManager().getGLView(
				slerpAction.getElementId());

		// if (glEventListener instanceof GLGlyph
		// || (glEventListener instanceof GLParallelCoordinates &&
		// glEventListener
		// .getSet().getSetType() != ESetType.GENE_EXPRESSION_DATA)
		// || (((glEventListener instanceof GLHierarchicalHeatMap ||
		// glEventListener instanceof GLParallelCoordinates) && glEventListener
		// .getSet().getSetType() == ESetType.GENE_EXPRESSION_DATA)
		// && isExperimentCountOK && isTissueGuideActive && renderGeneticViews)
		// || (glEventListener instanceof GLTissueViewBrowser &&
		// isTissueGuideActive)
		// || (glEventListener instanceof GLPathwayViewBrowser
		// && isPathwayContentAvailable && renderPathwayViews)) {

		renderBucketWall(gl, true);
		generalManager.getViewGLCanvasManager().getGLView(iViewID).displayRemote(gl);
		// }

		gl.glPopMatrix();

		// // Check if slerp action is finished
		// if (iSlerpFactor >= SLERP_RANGE) {
		// // arSlerpActions.remove(slerpAction);
		// arSlerpActions.removeAll();
		//
		// iSlerpFactor = 0;
		//			
		// slerpAction.finished();
		//
		// // RemoteLevelElement destinationElement =
		// slerpAction.getDestinationRemoteLevelElement();
		//
		// // updateViewDetailLevels(destinationElement);
		// // bUpdateOffScreenTextures = true;
		// }

		// // After last slerp action is done the line connections are turned on
		// // again
		// if (arSlerpActions.isEmpty()) {
		// if (glConnectionLineRenderer != null) {
		// glConnectionLineRenderer.enableRendering(true);
		// }
		//
		// generalManager.getViewGLCanvasManager().getInfoAreaManager().enable(!bEnableNavigationOverlay);
		// generalManager.getViewGLCanvasManager().getConnectedElementRepresentationManager().clearTransformedConnections();
		// }
	}

	@Override
	public void broadcastElements(EVAOperation type) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearAllSelections() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getDetailedInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNumberOfSelections(SelectionType SelectionType) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getShortInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void handlePickingEvents(EPickingType pickingType,
			EPickingMode pickingMode, int iExternalID, Pick pick) {

		isPatientAlternativeGuideActive = false;
		switch (pickingType) {

		case VIEW_SELECTION:
			switch (pickingMode) {
			case MOUSE_OVER:

				setDisplayListDirty();
				break;

			case CLICKED:
				break;
			case RIGHT_CLICKED:
				contextMenu.setLocation(pick.getPickedPoint(), getParentGLCanvas()
						.getWidth(), getParentGLCanvas().getHeight());
				contextMenu.setMasterGLView(this);
				break;

			}
			// infoAreaManager.setData(iExternalID,
			// EIDType.EXPRESSION_INDEX, pick.getPickedPoint(),
			// 0.3f);// pick.getDepth());
			break;

		case REMOTE_LEVEL_ELEMENT:
			switch (pickingMode) {
			case CLICKED:
				// Check if other slerp action is currently running
				if (iSlerpFactor > 0 && iSlerpFactor < SLERP_RANGE) {
					break;
				}

				// glConnectionLineRenderer.enableRendering(true);

				arSlerpActions.clear();
				lastPickedRemoteLevelElement = RemoteElementManager.get().getItem(
						iExternalID);
				iLastPickedViewID = lastPickedRemoteLevelElement.getGLView().getID();
				chainMove(lastPickedRemoteLevelElement);

				AGLView pickedView = GeneralManager.get().getViewGLCanvasManager()
						.getGLView(iLastPickedViewID);

				if (pickedView instanceof GLTissueViewBrowser) {

					isTissueGuideActive = true;
					// isGeneticGuideActive = false;

					((GLTissueViewBrowser) pickedView).setSlerpActive(true);
				}

				else if ((pickedView instanceof GLParallelCoordinates && pickedView
						.getSet().getSetType() == ESetType.GENE_EXPRESSION_DATA)
						|| pickedView instanceof GLHierarchicalHeatMap) {
					renderGeneticViews = true;
				} else if (pickedView instanceof GLPathwayViewBrowser)
					renderPathwayViews = true;

				break;
			case MOUSE_OVER:

				RemoteLevelElement element = RemoteElementManager.get().getItem(
						iExternalID);
				int pickID = element.getGLView().getID();

				AGLView pickedView2 = GeneralManager.get().getViewGLCanvasManager()
						.getGLView(pickID);

				if (pickedView2 instanceof GLGlyph) {
					isPatientAlternativeGuideActive = true;
				}

				break;
			}
			break;

		case BUCKET_DRAG_ICON_SELECTION:

			switch (pickingMode) {
			case CLICKED:

				break;
			}
		}
	}

	private void chainMove(RemoteLevelElement selectedElement) {

		// Clear connection lines
		generalManager.getViewGLCanvasManager()
				.getConnectedElementRepresentationManager().clearAll();

		// Chain slerping to the right
		if (stackElementsLeft.contains(selectedElement)) {

			for (int iElementIndex = stackElementsLeft.size(); iElementIndex >= 0; iElementIndex--) {

				if (iElementIndex < (MAX_SIDE_VIEWS - 1)) {
					arSlerpActions
							.add(new SlerpAction(
									stackElementsLeft.get(iElementIndex + 1),
									stackElementsLeft.get(iElementIndex)));
				}

				if (iElementIndex == 0) {
					arSlerpActions.add(new SlerpAction(stackElementsLeft
							.get(iElementIndex), focusElement));
				}
			}

			arSlerpActions.add(new SlerpAction(focusElement, stackElementsRight.get(0)));

			for (int iElementIndex = 0; iElementIndex < stackElementsRight.size(); iElementIndex++) {

				if (iElementIndex < (MAX_SIDE_VIEWS - 1)) {
					// if (!remoteLevelElementsRight.get(iElementIndex +
					// 1).isFree()) {
					arSlerpActions.add(new SlerpAction(stackElementsRight
							.get(iElementIndex), stackElementsRight
							.get(iElementIndex + 1)));
					// }
				}
			}
		}
		// Chain slerping to the left
		else if (stackElementsRight.contains(selectedElement)) {

			for (int iElementIndex = 0; iElementIndex < stackElementsRight.size(); iElementIndex++) {

				if (iElementIndex < (MAX_SIDE_VIEWS - 1)) {
					arSlerpActions.add(new SlerpAction(stackElementsRight
							.get(iElementIndex + 1), stackElementsRight
							.get(iElementIndex)));
				}

				if (iElementIndex == 0) {
					arSlerpActions.add(new SlerpAction(stackElementsRight
							.get(iElementIndex), focusElement));
				}
			}

			arSlerpActions.add(new SlerpAction(focusElement, stackElementsLeft.get(0)));

			for (int iElementIndex = 0; iElementIndex < stackElementsLeft.size(); iElementIndex++) {

				if (iElementIndex < (MAX_SIDE_VIEWS - 1)) {
					// if (!remoteLevelElementsLeft.get(iElementIndex +
					// 1).isFree()) {
					arSlerpActions
							.add(new SlerpAction(stackElementsLeft.get(iElementIndex),
									stackElementsLeft.get(iElementIndex + 1)));
					// }
				}
			}
		}
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedDataFlipperView serializedForm = new SerializedDataFlipperView(
				dataDomain);
		serializedForm.setViewID(this.getID());

		// IViewManager viewManager = generalManager.getViewGLCanvasManager();

		// ArrayList<ASerializedView> remoteViews =
		// new ArrayList<ASerializedView>(focusLevel.getAllElements().size());
		// for (RemoteLevelElement rle : focusLevel.getAllElements()) {
		// if (rle.getContainedElementID() != -1) {
		// AGLEventListener remoteView =
		// viewManager.getGLEventListener(rle.getContainedElementID());
		// remoteViews.add(remoteView.getSerializableRepresentation());
		// }
		// }
		// serializedForm.setFocusViews(remoteViews);
		//
		// remoteViews = new
		// ArrayList<ASerializedView>(stackLevel.getAllElements().size());
		// for (RemoteLevelElement rle : stackLevel.getAllElements()) {
		// if (rle.getContainedElementID() != -1) {
		// AGLEventListener remoteView =
		// viewManager.getGLEventListener(rle.getContainedElementID());
		// remoteViews.add(remoteView.getSerializableRepresentation());
		// }
		// }
		// serializedForm.setStackViews(remoteViews);

		return serializedForm;
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		super.reshape(drawable, x, y, width, height);

		// AGLView glView = focusElement.getGLView();
		// if (glView == null)
		// return;

		// IViewFrustum frustum = glView.getViewFrustum();
		// frustum.setTop(8*fAspectRatio);
		// glView.reshape(drawable, x, y, width, height);
	}

	private void renderDataViewIcons(final GL gl, EDataDomain dataDomain) {

		IDataDomain useCase = GeneralManager.get().getUseCase(dataDomain);
		ArrayList<String> possibleViews = useCase.getPossibleViews();

		EIconTextures dataIcon = null;
		float fXPos = 0.5f;

		if (dataDomain == EDataDomain.CLINICAL_DATA) {
			dataIcon = EIconTextures.DATA_FLIPPER_DATA_ICON_PATIENT;
			fXPos += -3f;
		} else if (dataDomain == EDataDomain.TISSUE_DATA) {
			dataIcon = EIconTextures.DATA_FLIPPER_DATA_ICON_TISSUE;
			fXPos += -1.5f;
		} else if (dataDomain == EDataDomain.GENETIC_DATA) {
			dataIcon = EIconTextures.DATA_FLIPPER_DATA_ICON_GENE_EXPRESSION;
			fXPos += -0f;
		} else if (dataDomain == EDataDomain.PATHWAY_DATA) {
			dataIcon = EIconTextures.DATA_FLIPPER_DATA_ICON_PATHWAY;
			fXPos += 1.5f;
		}

		float fViewIconWidth = 0.12f;
		gl.glTranslatef(fXPos, -2.07f, 4);

		// Data background
		textureManager.renderTexture(gl, EIconTextures.DATA_FLIPPER_DATA_ICON_BACKGROUND,
				new Vec3f(0, 0, 0), new Vec3f(0.51f, 0, 0), new Vec3f(0.51f, 0.3f, 0),
				new Vec3f(0, 0.3f, 0), 1, 1, 1, 1);

		gl.glTranslatef(0, 0.31f, 0);

		// First view background
		textureManager.renderTexture(gl,
				EIconTextures.DATA_FLIPPER_VIEW_ICON_BACKGROUND_ROUNDED, new Vec3f(
						fViewIconWidth, 0.0f, 0), new Vec3f(0.0f, 0.0f, 0), new Vec3f(
						0.0f, fViewIconWidth, 0), new Vec3f(fViewIconWidth,
						fViewIconWidth, 0), 1, 1, 1, 1);

		gl.glTranslatef(fViewIconWidth + 0.01f, 0, 0);

		// Second view background
		textureManager.renderTexture(gl,
				EIconTextures.DATA_FLIPPER_VIEW_ICON_BACKGROUND_SQUARE, new Vec3f(
						fViewIconWidth, 0.0f, 0), new Vec3f(0.0f, 0.0f, 0), new Vec3f(
						0.0f, fViewIconWidth, 0), new Vec3f(fViewIconWidth,
						fViewIconWidth, 0), 1, 1, 1, 1);

		gl.glTranslatef(fViewIconWidth + 0.01f, 0, 0);

		// Third view background
		textureManager.renderTexture(gl,
				EIconTextures.DATA_FLIPPER_VIEW_ICON_BACKGROUND_SQUARE, new Vec3f(
						fViewIconWidth, 0.0f, 0), new Vec3f(0.0f, 0.0f, 0), new Vec3f(
						0.0f, fViewIconWidth, 0), new Vec3f(fViewIconWidth,
						fViewIconWidth, 0), 1, 1, 1, 1);

		gl.glTranslatef(fViewIconWidth + 0.01f, 0, 0);

		// Forth view background
		textureManager.renderTexture(gl,
				EIconTextures.DATA_FLIPPER_VIEW_ICON_BACKGROUND_ROUNDED, new Vec3f(0,
						0.0f, 0), new Vec3f(fViewIconWidth, 0.0f, 0), new Vec3f(
						fViewIconWidth, fViewIconWidth, 0), new Vec3f(0, fViewIconWidth,
						0), 1, 1, 1, 1);
		gl.glTranslatef(-3 * fViewIconWidth - 0.03f, -0.31f, 0);

		float fGuidancePipeWidth = 0.02f;
		float alpha = 0.3f;
		EIconTextures connTexture = EIconTextures.DATA_FLIPPER_GUIDANCE_CONNECTION_STRAIGHT;
		if ((dataDomain == EDataDomain.CLINICAL_DATA && isExperimentCountOK && isPatientGuideActive)
				|| (dataDomain == EDataDomain.TISSUE_DATA && isTissueGuideActive)
				|| (dataDomain == EDataDomain.GENETIC_DATA && isPathwayContentAvailable)) {
			alpha = 1f;
			connTexture = EIconTextures.DATA_FLIPPER_GUIDANCE_CONNECTION_STRAIGHT_HIGHLIGHT;
		}

		if (dataDomain != EDataDomain.PATHWAY_DATA) {
			textureManager.renderTexture(gl, connTexture, new Vec3f(0.51f, 0.15f, 0.0f),
					new Vec3f(1.5f, 0.15f, 0.0f), new Vec3f(1.5f,
							0.15f + fGuidancePipeWidth + 0.05f, 0.0f), new Vec3f(0.51f,
							0.15f + fGuidancePipeWidth + 0.05f, 0.0f), 1, 1, 1, alpha);
		}

		// if (isPatientAlternativeGuideActive && dataDomain ==
		// EDataDomain.CLINICAL_DATA) {
		// textureManager.renderTexture(gl, connTexture, new Vec3f(0.52f, 0.05f,
		// 0.02f), new Vec3f(1.5f,
		// 0.05f, 0.02f), new Vec3f(1.5f, 0.05f + fGuidancePipeWidth + 0.05f,
		// 0.02f), new Vec3f(0.52f,
		// 0.05f + fGuidancePipeWidth + 0.05f, 0.02f), 1, 1, 1, alpha);
		//
		// gl.glTranslatef(1f, 0, 0);
		// textureManager.renderTexture(gl, connTexture, new Vec3f(0.51f, 0.05f,
		// 0.02f), new Vec3f(1.5f,
		// 0.05f, 0.02f), new Vec3f(1.5f, 0.05f + fGuidancePipeWidth + 0.05f,
		// 0.02f), new Vec3f(0.51f,
		// 0.05f + fGuidancePipeWidth + 0.05f, 0.02f), 1, 1, 1, alpha);;
		//
		// gl.glTranslatef(0.5f, 0, 0);
		// textureManager.renderTexture(gl, connTexture, new Vec3f(0.51f, 0.05f,
		// 0.02f), new Vec3f(1.5f,
		// 0.05f, 0.02f), new Vec3f(1.5f, 0.05f + fGuidancePipeWidth + 0.05f,
		// 0.02f), new Vec3f(0.51f,
		// 0.05f + fGuidancePipeWidth + 0.05f, 0.02f), 1, 1, 1, alpha);
		// gl.glTranslatef(-1.5f, 0, 0);
		// }

		for (int viewIndex = 0; viewIndex < possibleViews.size(); viewIndex++) {

			EIconTextures iconTextureType;
			if (viewType.equals(GLHierarchicalHeatMap.VIEW_ID))
				iconTextureType = EIconTextures.HEAT_MAP_ICON;
			else if (viewType.equals(GLParallelCoordinates.VIEW_ID))
				iconTextureType = EIconTextures.PAR_COORDS_ICON;
			else if (viewType.equals(GLGlyph.VIEW_ID))
				iconTextureType = EIconTextures.GLYPH_ICON;
			else if (viewType.equals(GLPathway.VIEW_ID)
					|| viewType.equals(GLPathwayViewBrowser.VIEW_ID))
				iconTextureType = EIconTextures.PATHWAY_ICON;
			else if (viewType.equals(GLTissue.VIEW_ID)
					|| viewType.equals(GLTissueViewBrowser.VIEW_ID))
				iconTextureType = EIconTextures.TISSUE_SAMPLE;
			else
				iconTextureType = EIconTextures.LOCK;

			RemoteLevelElement element = findElementContainingView(dataDomain, viewType);
			AGLView glView = null;
			if (element != null) {
				glView = element.getGLView();
			}

			float fIconBackgroundGray = 1;
			if (element == null)
				fIconBackgroundGray = 0.6f;
			else {
				if ((glView instanceof GLGlyph
						|| (glView instanceof GLParallelCoordinates && glView.getSet()
								.getSetType() != ESetType.GENE_EXPRESSION_DATA) || isExperimentCountOK)) {
					fIconBackgroundGray = 1f;
				} else
					fIconBackgroundGray = 0.6f;

				gl.glPushName(pickingManager.getPickingID(iUniqueID,
						EPickingType.REMOTE_LEVEL_ELEMENT, element.getID()));
			}

			float fIconPadding = 0.015f;
			gl.glTranslatef(0, 0, 0.001f);
			switch (viewIndex) {
			case 0:
				// Data icon
				textureManager.renderTexture(gl, dataIcon, new Vec3f(0f, 0.02f, 0.01f),
						new Vec3f(0.5f, 0.02f, 0.01f), new Vec3f(0.5f, 0.28f, 0.01f),
						new Vec3f(0.0f, 0.28f, 0.01f), 1, 1, 1, 1);

				// First view icon
				gl.glTranslatef(0, 0.31f, 0);
				textureManager.renderTexture(gl, iconTextureType, new Vec3f(
						fViewIconWidth - fIconPadding, fIconPadding, 0), new Vec3f(
						fIconPadding, fIconPadding, 0), new Vec3f(fIconPadding,
						fViewIconWidth - fIconPadding, 0), new Vec3f(fViewIconWidth
						- fIconPadding, fViewIconWidth - fIconPadding, 0),
						fIconBackgroundGray, fIconBackgroundGray, fIconBackgroundGray, 1);
				gl.glTranslatef(0, -0.31f, 0);

				break;
			case 1:
				// Second view icon
				gl.glTranslatef(0.13f, 0.31f, 0);
				textureManager.renderTexture(gl, iconTextureType, new Vec3f(
						fViewIconWidth - fIconPadding, fIconPadding, 0), new Vec3f(
						fIconPadding, fIconPadding, 0), new Vec3f(fIconPadding,
						fViewIconWidth - fIconPadding, 0), new Vec3f(fViewIconWidth
						- fIconPadding, fViewIconWidth - fIconPadding, 0),
						fIconBackgroundGray, fIconBackgroundGray, fIconBackgroundGray, 1);
				gl.glTranslatef(-0.13f, -0.31f, 0);
				break;
			case 2:
				// Third view icon
				gl.glTranslatef(0.26f, 0.31f, 0);
				textureManager.renderTexture(gl, iconTextureType, new Vec3f(
						fViewIconWidth - fIconPadding, fIconPadding, 0), new Vec3f(
						fIconPadding, fIconPadding, 0), new Vec3f(fIconPadding,
						fViewIconWidth - fIconPadding, 0), new Vec3f(fViewIconWidth
						- fIconPadding, fViewIconWidth - fIconPadding, 0),
						fIconBackgroundGray, fIconBackgroundGray, fIconBackgroundGray, 1);
				gl.glTranslatef(-0.26f, -0.31f, 0);
				break;
			case 3:
				// Forth view icon
				gl.glTranslatef(0.39f, 0.31f, 0);
				textureManager.renderTexture(gl, iconTextureType, new Vec3f(
						fViewIconWidth - fIconPadding, fIconPadding, 0), new Vec3f(
						fIconPadding, fIconPadding, 0), new Vec3f(fIconPadding,
						fViewIconWidth - fIconPadding, 0), new Vec3f(fViewIconWidth
						- fIconPadding, fViewIconWidth - fIconPadding, 0),
						fIconBackgroundGray, fIconBackgroundGray, fIconBackgroundGray, 1);
				gl.glTranslatef(-0.39f, -0.31f, 0);
				break;
			}

			if (element != null)
				gl.glPopName();

			if (glView instanceof GLGlyph
					|| (glView instanceof GLParallelCoordinates && glView.getSet()
							.getSetType() != ESetType.GENE_EXPRESSION_DATA)
					|| (((glView instanceof GLHierarchicalHeatMap || glView instanceof GLParallelCoordinates) && glView
							.getSet().getSetType() == ESetType.GENE_EXPRESSION_DATA)
							&& isExperimentCountOK && isTissueGuideActive && renderGeneticViews)
					|| (glView instanceof GLTissueViewBrowser && isTissueGuideActive)
					|| (glView instanceof GLPathwayViewBrowser
							&& isPathwayContentAvailable && renderPathwayViews)) {

				// if ((glEventListener instanceof GLGlyph
				// || (glEventListener instanceof GLParallelCoordinates &&
				// glEventListener.getSet().getSetType() !=
				// ESetType.GENE_EXPRESSION_DATA) ||
				// isExperimentCountOK)) {
				//				
				if (element != null && arSlerpActions.isEmpty()) {

					float fHorizontalConnStart = 0;
					float fHorizontalConnStop = 0;
					float fHorizontalConnHeight = 0;
					float fPipeWidth = 0.05f;

					// gl.glTranslatef(fXPos, -2.6f, 3);
					Transform transform = element.getTransform();
					Vec3f translation = transform.getTranslation();

					// if (element == focusElement) {
					//				
					// textureManager.renderTexture(gl,
					// EIconTextures.DATA_FLIPPER_CONNECTION_STRAIGHT,
					// new Vec3f(0.05f, 0.43f, 0.0f), new Vec3f(fPipeWidth,
					// 0.43f,
					// 0.0f), new Vec3f(fPipeWidth,
					// 0.85f, 0.0f), new Vec3f(0.05f, 0.85f, 0.0f), 1, 1, 1, 1);
					// }
					// else
					if (element == stackElementsLeft.get(0)) {
						// // LEFT first
						gl.glTranslatef(-fXPos - 1.56f + translation.x(),
								0.47f + translation.y(), translation.z() * 0);
						textureManager.renderTexture(gl,
								EIconTextures.DATA_FLIPPER_CONNECTION_STRAIGHT,
								new Vec3f(0.0f, 0.0f + fPipeWidth, 0.0f), new Vec3f(
										fPipeWidth, 0.0f + fPipeWidth, 0.0f), new Vec3f(
										fPipeWidth, 0.1f, 0.0f), new Vec3f(0.0f, 0.1f,
										0.0f), 1, 1, 1, 1);
						gl.glTranslatef(0, -0.2f, 0);

						gl.glTranslatef(fXPos + 1.56f - translation.x(), -0.47f
								- translation.y() + 0.2f, -translation.z() * 0);
						//					
						fHorizontalConnStart = -fXPos + translation.x() - 1.56f
								+ fPipeWidth;
						fHorizontalConnHeight = 0.67f;
					} else if (element == stackElementsLeft.get(1)) {
						// // LEFT second
						gl.glTranslatef(-fXPos - 1.53f + translation.x(),
								0.34f + translation.y(), translation.z() * 0);
						textureManager.renderTexture(gl,
								EIconTextures.DATA_FLIPPER_CONNECTION_STRAIGHT,
								new Vec3f(0.0f, 0.0f + fPipeWidth, 0.0f), new Vec3f(
										fPipeWidth, 0.0f + fPipeWidth, 0.0f), new Vec3f(
										fPipeWidth, 0.23f, 0.0f), new Vec3f(0.0f, 0.23f,
										0.0f), 1, 1, 1, 1);
						gl.glTranslatef(0, -0.2f, 0);

						gl.glTranslatef(fXPos + 1.53f - translation.x(), -0.34f
								- translation.y() + 0.2f, -translation.z() * 0);
						//				
						fHorizontalConnStart = -fXPos + translation.x() - 1.53f
								+ fPipeWidth;
						fHorizontalConnHeight = 0.54f;
					} else if (element == stackElementsRight.get(0)) {
						// RIGHT first
						gl.glTranslatef(-fXPos - 2.53f + translation.x(),
								0.76f + translation.y(), translation.z() * 0);
						textureManager.renderTexture(gl,
								EIconTextures.DATA_FLIPPER_CONNECTION_STRAIGHT,
								new Vec3f(0.0f, 0.0f + fPipeWidth, 0.0f), new Vec3f(
										fPipeWidth, 0.0f + fPipeWidth, 0.0f), new Vec3f(
										fPipeWidth, 0.1f, 0.0f), new Vec3f(0.0f, 0.1f,
										0.0f), 1, 1, 1, 1);
						gl.glTranslatef(0, -0.2f, 0);

						gl.glTranslatef(fXPos + 2.53f - translation.x(), -0.76f
								- translation.y() + 0.2f, -translation.z() * 0);
						//				
						fHorizontalConnStart = -fXPos + translation.x() - 2.53f;
						fHorizontalConnHeight = 0.67f;
					} else if (element == stackElementsRight.get(1)) {
						// RIGHT second
						gl.glTranslatef(-fXPos - 2.63f + translation.x(),
								0.64f + translation.y(), translation.z() * 0);
						textureManager.renderTexture(gl,
								EIconTextures.DATA_FLIPPER_CONNECTION_STRAIGHT,
								new Vec3f(0.0f, 0.0f + fPipeWidth, 0.0f), new Vec3f(
										fPipeWidth, 0.0f + fPipeWidth, 0.0f), new Vec3f(
										fPipeWidth, 0.23f, 0.0f), new Vec3f(0.0f, 0.23f,
										0.0f), 1, 1, 1, 1);
						gl.glTranslatef(0, -0.2f, 0);

						gl.glTranslatef(fXPos + 2.63f - translation.x(), -0.64f
								- translation.y() + 0.2f, -translation.z() * 0);
						//				
						fHorizontalConnStart = -fXPos + translation.x() - 2.63f;
						fHorizontalConnHeight = 0.54f;
					}

					if (element == focusElement
							|| (stackElementsLeft.contains(element) && stackElementsLeft
									.indexOf(element) < 2)
							|| (stackElementsRight.contains(element) && stackElementsRight
									.indexOf(element) < 2)) {
						float fPipeHeight = 0.11f;

						if (fHorizontalConnHeight > 0.6)
							fPipeHeight = 0.24f;

						switch (viewIndex) {
						case 0:

							if (element == focusElement) {

								textureManager.renderTexture(gl,
										EIconTextures.DATA_FLIPPER_CONNECTION_STRAIGHT,
										new Vec3f(0.04f, 0.43f, 0.0f), new Vec3f(
												0.04f + fPipeWidth, 0.43f, 0.0f),
										new Vec3f(0.04f + fPipeWidth, 0.85f, 0.0f),
										new Vec3f(0.04f, 0.85f, 0.0f), 1, 1, 1, 1);

								// Special case when focus element is last
								// view on the right stack
								if (stackElementsRight.get(0).isFree()) {
									textureManager.renderTexture(gl,
											EIconTextures.DATA_FLIPPER_CONNECTION_CORNER,
											new Vec3f(0.04f - fPipeWidth, 0.85f, 0.0f),
											new Vec3f(0.04f + fPipeWidth, 0.85f, 0.0f),
											new Vec3f(0.04f + fPipeWidth,
													0.85f + fPipeWidth + 0.05f, 0.0f),
											new Vec3f(0.04f - fPipeWidth,
													0.85f + fPipeWidth + 0.05f, 0.0f), 1,
											1, 1, 1);

									textureManager
											.renderTexture(
													gl,
													EIconTextures.DATA_FLIPPER_CONNECTION_STRAIGHT,
													new Vec3f(
															fHorizontalConnStart - 0.4f,
															0.85f + fPipeWidth, 0.0f),
													new Vec3f(0.04f - fPipeWidth,
															0.85f + fPipeWidth, 0.0f),
													new Vec3f(0.04f - fPipeWidth,
															0.85f + fPipeWidth + 0.05f,
															0.0f), new Vec3f(
															fHorizontalConnStart - 0.4f,
															0.85f + fPipeWidth + 0.05f,
															0.0f), 1, 1, 1, 1);
								}
								// Special case when focus element is last
								// view on the left stack
								else if (stackElementsLeft.get(0).isFree()) {
									textureManager
											.renderTexture(
													gl,
													EIconTextures.DATA_FLIPPER_CONNECTION_CORNER,
													new Vec3f(0.04f + 2 * fPipeWidth,
															0.85f, 0.0f), new Vec3f(
															0.04f, 0.85f, 0.0f),
													new Vec3f(0.04f,
															0.85f + fPipeWidth + 0.05f,
															0.0f), new Vec3f(
															0.04f + 2 * fPipeWidth,
															0.85f + fPipeWidth + 0.05f,
															0.0f), 1, 1, 1, 1);

									textureManager
											.renderTexture(
													gl,
													EIconTextures.DATA_FLIPPER_CONNECTION_STRAIGHT,
													new Vec3f(
															fHorizontalConnStart + 0.9f,
															0.85f + fPipeWidth, 0.0f),
													new Vec3f(0.04f + 2 * fPipeWidth,
															0.85f + fPipeWidth, 0.0f),
													new Vec3f(0.04f + 2 * fPipeWidth,
															0.85f + fPipeWidth + 0.05f,
															0.0f), new Vec3f(
															fHorizontalConnStart + 0.9f,
															0.85f + fPipeWidth + 0.05f,
															0.0f), 1, 1, 1, 1);
								}
							} else {
								gl.glTranslatef(0.032f, 0.43f, 0);
								textureManager.renderTexture(gl,
										EIconTextures.DATA_FLIPPER_CONNECTION_STRAIGHT,
										new Vec3f(0.0f, 0.0f, 0.0f), new Vec3f(
												fPipeWidth, 0.0f, 0.0f), new Vec3f(
												fPipeWidth, fPipeHeight - fPipeWidth,
												0.0f), new Vec3f(0.0f, fPipeHeight
												- fPipeWidth, 0.0f), 1, 1, 1, 1);
								gl.glTranslatef(-0.032f, -0.43f, 0);

								if (stackElementsLeft.contains(element))
									fHorizontalConnStop = 0.03f;
								else
									fHorizontalConnStop = 0.08f;
							}
							break;
						case 1:
							if (element == focusElement) {

								textureManager.renderTexture(gl,
										EIconTextures.DATA_FLIPPER_CONNECTION_STRAIGHT,
										new Vec3f(0.16f, 0.43f, 0.0f), new Vec3f(
												0.16f + fPipeWidth, 0.43f, 0.0f),
										new Vec3f(0.16f + fPipeWidth, 0.85f, 0.0f),
										new Vec3f(0.16f, 0.85f, 0.0f), 1, 1, 1, 1);

								if (!stackElementsRight.get(1).isFree()) {
									textureManager
											.renderTexture(
													gl,
													EIconTextures.DATA_FLIPPER_CONNECTION_CORNER,
													new Vec3f(0.16f + 2 * fPipeWidth,
															0.85f, 0.0f), new Vec3f(
															0.16f, 0.85f, 0.0f),
													new Vec3f(0.16f,
															0.85f + fPipeWidth + 0.05f,
															0.0f), new Vec3f(
															0.16f + 2 * fPipeWidth,
															0.85f + fPipeWidth + 0.05f,
															0.0f), 1, 1, 1, 1);

									textureManager
											.renderTexture(
													gl,
													EIconTextures.DATA_FLIPPER_CONNECTION_STRAIGHT,
													new Vec3f(
															fHorizontalConnStart + 0.9f,
															0.85f + fPipeWidth, 0.0f),
													new Vec3f(
															0.04f + 2 * fPipeWidth + 0.1f,
															0.85f + fPipeWidth, 0.0f),
													new Vec3f(
															0.04f + 2 * fPipeWidth + 0.1f,
															0.85f + fPipeWidth + 0.05f,
															0.0f), new Vec3f(
															fHorizontalConnStart + 0.9f,
															0.85f + fPipeWidth + 0.05f,
															0.0f), 1, 1, 1, 1);
								}
							} else {
								gl.glTranslatef(0.17f, 0.43f, 0);
								textureManager.renderTexture(gl,
										EIconTextures.DATA_FLIPPER_CONNECTION_STRAIGHT,
										new Vec3f(0.0f, 0.0f, 0.0f), new Vec3f(
												fPipeWidth, 0.0f, 0.0f), new Vec3f(
												fPipeWidth, fPipeHeight - fPipeWidth,
												0.0f), new Vec3f(0.0f, fPipeHeight
												- fPipeWidth, 0.0f), 1, 1, 1, 1);

								gl.glTranslatef(-0.17f, -0.43f, 0);

								if (stackElementsLeft.contains(element))
									fHorizontalConnStop = 0.17f;
								else
									fHorizontalConnStop = 0.22f;
							}
							break;
						case 2:
							// TODO
							break;
						case 3:
							// TODO
							break;
						}

						if (element != focusElement) {
							if (fHorizontalConnStart < fHorizontalConnStop) {
								textureManager.renderTexture(gl,
										EIconTextures.DATA_FLIPPER_CONNECTION_STRAIGHT,
										new Vec3f(fHorizontalConnStart + fPipeWidth,
												fHorizontalConnHeight, 0.0f), new Vec3f(
												fHorizontalConnStop - fPipeWidth,
												fHorizontalConnHeight, 0.0f), new Vec3f(
												fHorizontalConnStop - fPipeWidth,
												fHorizontalConnHeight + 0.05f, 0.0f),
										new Vec3f(fHorizontalConnStart + fPipeWidth,
												fHorizontalConnHeight + 0.05f, 0.0f), 1,
										1, 1, 1);
							} else {
								textureManager.renderTexture(gl,
										EIconTextures.DATA_FLIPPER_CONNECTION_STRAIGHT,
										new Vec3f(fHorizontalConnStart - fPipeWidth,
												fHorizontalConnHeight, 0.0f), new Vec3f(
												fHorizontalConnStop + fPipeWidth,
												fHorizontalConnHeight, 0.0f), new Vec3f(
												fHorizontalConnStop + fPipeWidth,
												fHorizontalConnHeight + 0.05f, 0.0f),
										new Vec3f(fHorizontalConnStart - fPipeWidth,
												fHorizontalConnHeight + 0.05f, 0.0f), 1,
										1, 1, 1);
							}

							// ROUND CORNERS near views
							if (fHorizontalConnStart > fHorizontalConnStop) {
								textureManager.renderTexture(gl,
										EIconTextures.DATA_FLIPPER_CONNECTION_CORNER,
										new Vec3f(fHorizontalConnStart - fPipeWidth,
												fHorizontalConnHeight + fPipeWidth
														+ 0.05f, 0.0f), new Vec3f(
												fHorizontalConnStart + fPipeWidth,
												fHorizontalConnHeight + fPipeWidth
														+ 0.05f, 0.0f), new Vec3f(
												fHorizontalConnStart + fPipeWidth,
												fHorizontalConnHeight, 0.0f), new Vec3f(
												fHorizontalConnStart - fPipeWidth,
												fHorizontalConnHeight, 0.0f), 1, 1, 1, 1);
							} else {
								textureManager.renderTexture(gl,
										EIconTextures.DATA_FLIPPER_CONNECTION_CORNER,
										new Vec3f(fHorizontalConnStart + fPipeWidth,
												fHorizontalConnHeight + fPipeWidth
														+ 0.05f, 0.0f), new Vec3f(
												fHorizontalConnStart - fPipeWidth,
												fHorizontalConnHeight + fPipeWidth
														+ 0.05f, 0.0f), new Vec3f(
												fHorizontalConnStart - fPipeWidth,
												fHorizontalConnHeight, 0.0f), new Vec3f(
												fHorizontalConnStart + fPipeWidth,
												fHorizontalConnHeight, 0.0f), 1, 1, 1, 1);
							}

							// ROUND CORNERS near data sets
							if (fHorizontalConnStart > fHorizontalConnStop) {
								textureManager
										.renderTexture(
												gl,
												EIconTextures.DATA_FLIPPER_CONNECTION_CORNER,
												new Vec3f(fHorizontalConnStop
														+ fPipeWidth,
														fHorizontalConnHeight
																- fPipeWidth, 0.0f),
												new Vec3f(fHorizontalConnStop
														- fPipeWidth,
														fHorizontalConnHeight
																- fPipeWidth, 0.0f),
												new Vec3f(fHorizontalConnStop
														- fPipeWidth,
														fHorizontalConnHeight + 0.05f,
														0.0f), new Vec3f(
														fHorizontalConnStop + fPipeWidth,
														fHorizontalConnHeight + 0.05f,
														0.0f), 1, 1, 1, 1);
							} else {
								textureManager
										.renderTexture(
												gl,
												EIconTextures.DATA_FLIPPER_CONNECTION_CORNER,
												new Vec3f(fHorizontalConnStop
														- fPipeWidth,
														fHorizontalConnHeight
																- fPipeWidth, 0.0f),
												new Vec3f(fHorizontalConnStop
														+ fPipeWidth,
														fHorizontalConnHeight
																- fPipeWidth, 0.0f),
												new Vec3f(fHorizontalConnStop
														+ fPipeWidth,
														fHorizontalConnHeight + 0.05f,
														0.0f), new Vec3f(
														fHorizontalConnStop - fPipeWidth,
														fHorizontalConnHeight + 0.05f,
														0.0f), 1, 1, 1, 1);
							}
						}
					}
				}

				// if (element != null)
				// gl.glPopName();

			}
			gl.glTranslatef(0, 0, -0.001f);

		}
		gl.glTranslatef(-fXPos, 2.07f, -4);
	}

	private void renderGuidanceConnections(final GL gl) {

		// float fPipeWidth = 0.1f;
		//
		// textureManager.renderTexture(gl,
		// EIconTextures.DATA_FLIPPER_GUIDANCE_CONNECTION_STRAIGHT, new
		// Vec3f(
		// 0, 0.85f, 0.0f), new Vec3f(0.16f, 0.85f, 0.0f),
		// new Vec3f(0.16f, 0.85f + fPipeWidth + 0.05f, 0.0f),
		// new Vec3f(0, 0.85f + fPipeWidth + 0.05f, 0.0f), 1, 1, 1, 1);
	}

	private RemoteLevelElement findElementContainingView(EDataDomain dataDomain,
			String viewID) {

		for (AGLView glView : containedGLViews) {
			if (glView.getViewType().equals(viewID)
					&& glView.getDataDomain() == dataDomain) {
				if (focusElement.getGLView() == glView)
					return focusElement;

				for (RemoteLevelElement element : stackElementsLeft) {
					if (element.getGLView() == glView)
						return element;
				}

				for (RemoteLevelElement element : stackElementsRight) {
					if (element.getGLView() == glView)
						return element;
				}
			}
		}

		return null;
	}

	// FIXME: method copied from bucket
	private void renderHandles(final GL gl) {

		// Bucket center (focus)
		RemoteLevelElement element = focusElement;
		AGLView glView = element.getGLView();
		if (glView != null) {

			Transform transform;
			Vec3f translation;
			// Vec3f scale;

			float fYCorrection = 0f;

			transform = element.getTransform();
			translation = transform.getTranslation();
			// scale = transform.getScale();

			gl.glTranslatef(translation.x() - 1.5f, translation.y() - 0.225f - 0.075f
					+ fYCorrection, translation.z() + 0.001f);

			// gl.glScalef(scale.x() * 4, scale.y() * 4, 1);
			renderNavigationHandleBar(gl, element, 3.2f, 0.075f, false, 2);
			// gl.glScalef(1 / (scale.x() * 4), 1 / (scale.y() * 4), 1);

			gl.glTranslatef(-translation.x() + 1.5f, -translation.y() + 0.225f + 0.075f
					- fYCorrection, -translation.z() - 0.001f);
		}

		// Left first
		element = stackElementsLeft.get(0);
		glView = element.getGLView();
		if (glView != null) {

			gl.glTranslatef(-0.64f, -1.25f, 4.02f);
			gl.glRotatef(90, 0, 0, 1);
			renderNavigationHandleBar(gl, element, 3.33f, 0.075f, false, 2);
			gl.glRotatef(-90, 0, 0, 1);
			gl.glTranslatef(0.64f, 1.25f, -4.02f);
		}

		// Left second
		element = stackElementsLeft.get(1);
		glView = element.getGLView();
		if (glView != null) {

			gl.glTranslatef(-1.17f, -1.25f, 4.02f);
			gl.glRotatef(90, 0, 0, 1);
			renderNavigationHandleBar(gl, element, 3.32f, 0.075f, false, 2);
			gl.glRotatef(-90, 0, 0, 1);
			gl.glTranslatef(1.17f, 1.25f, -4.02f);
		}

		// Right first
		element = stackElementsRight.get(0);
		glView = element.getGLView();
		if (element.getGLView() != null) {

			gl.glTranslatef(0.65f, 2.08f, 4.02f);
			gl.glRotatef(-90, 0, 0, 1);
			renderNavigationHandleBar(gl, element, 3.34f, 0.075f, false, 2);
			gl.glRotatef(90, 0, 0, 1);
			gl.glTranslatef(-0.65f, -2.08f, -4.02f);
		}

		// Right second
		element = stackElementsRight.get(1);
		glView = element.getGLView();

		if (glView != null) {

			gl.glTranslatef(1.1f, 2.08f, 4.02f);
			gl.glRotatef(-90, 0, 0, 1);
			renderNavigationHandleBar(gl, element, 3.34f, 0.075f, false, 2);
			gl.glRotatef(90, 0, 0, 1);
			gl.glTranslatef(-1.1f, -2.08f, -4.02f);
		}
	}

	// private void renderViewConnectionPipes(final GL gl, RemoteLevelElement
	// element) {
	//
	// textureManager.renderTexture(gl,
	// EIconTextures.DATA_FLIPPER_CONNECTION_STRAIGHT, new Vec3f(0, 0, 0),
	// new Vec3f(0.63f, 0, 0), new Vec3f(0.63f, 0.46f, 0), new Vec3f(0, 0.46f,
	// 0), 1, 1, 1, 1);
	//
	// textureManager.renderTexture(gl,
	// EIconTextures.DATA_FLIPPER_CONNECTION_CORNER, new Vec3f(0, 0, 0),
	// new Vec3f(0.63f, 0, 0), new Vec3f(0.63f, 0.46f, 0), new Vec3f(0, 0.46f,
	// 0), 1, 1, 1, 1);
	//
	// }

	// FIXME: method copied from bucket
	// private void renderViewTitleBar(final GL gl, RemoteLevelElement element,
	// float fHandleWidth,
	// float fHandleHeight, boolean bUpsideDown, float fScalingFactor,
	// EOrientation eOrientation) {
	//
	// // if (eOrientation == EOrientation.LEFT) {
	// // gl.glBegin(GL.GL_POLYGON);
	// // gl.glVertex3f(-fHandleHeight, fHandleHeight, 0);
	// // gl.glVertex3f(0, fHandleHeight, 0);
	// // gl.glVertex3f(0, fHandleWidth - fHandleHeight, 0);
	// // gl.glVertex3f(-fHandleHeight, fHandleWidth - fHandleHeight, 0);
	// // gl.glEnd();
	// //
	// // // Render icons
	// // gl.glTranslatef(-fHandleHeight, 0, 0);
	// // renderSingleHandle(gl, element.getID(),
	// EPickingType.BUCKET_DRAG_ICON_SELECTION,
	// // EIconTextures.NAVIGATION_DRAG_VIEW, fHandleHeight, fHandleHeight,
	// eOrientation);
	// // gl.glTranslatef(0, fHandleWidth - fHandleWidth, 0);
	// //// if (bUpsideDown) {
	// //// gl.glRotatef(180, 1, 0, 0);
	// //// gl.glTranslatef(0, fHandleHeight, 0);
	// //// }
	// //// renderSingleHandle(gl, element.getID(),
	// EPickingType.BUCKET_LOCK_ICON_SELECTION,
	// //// EIconTextures.NAVIGATION_LOCK_VIEW, fHandleHeight, fHandleHeight);
	// //// if (bUpsideDown) {
	// //// gl.glTranslatef(0, -fHandleHeight, 0);
	// //// gl.glRotatef(-180, 1, 0, 0);
	// //// }
	// //// gl.glTranslatef(0, -fHandleWidth, 0);
	// //// renderSingleHandle(gl, element.getID(),
	// EPickingType.BUCKET_REMOVE_ICON_SELECTION,
	// //// EIconTextures.NAVIGATION_REMOVE_VIEW, fHandleHeight, fHandleHeight);
	// // gl.glTranslatef(fHandleHeight, -fHandleWidth + fHandleWidth, 0);
	// //
	// // }
	//
	// if (eOrientation == EOrientation.LEFT)
	// gl.glRotatef(90, 0, 0, 1);
	//		
	// // Render icons
	// // gl.glTranslatef(0, fHandleWidth + fHandleHeight, 0);
	// // renderSingleHandle(gl, element.getID(),
	// EPickingType.BUCKET_DRAG_ICON_SELECTION,
	// // EIconTextures.NAVIGATION_DRAG_VIEW, fHandleHeight, fHandleHeight,
	// eOrientation);
	// // gl.glTranslatef(0, -fHandleWidth - fHandleHeight, 0);
	// // gl.glTranslatef(fHandleWidth - 2 * fHandleHeight, 0, 0);
	//		
	// // if (bUpsideDown) {
	// // gl.glRotatef(180, 1, 0, 0);
	// // gl.glTranslatef(0, fHandleHeight, 0);
	// // }
	// // renderSingleHandle(gl, element.getID(),
	// EPickingType.BUCKET_LOCK_ICON_SELECTION,
	// // EIconTextures.NAVIGATION_LOCK_VIEW, fHandleHeight, fHandleHeight,
	// eOrientation);
	// // if (bUpsideDown) {
	// // gl.glTranslatef(0, -fHandleHeight, 0);
	// // gl.glRotatef(-180, 1, 0, 0);
	// // }
	// // gl.glTranslatef(fHandleHeight, 0, 0);
	// // renderSingleHandle(gl, element.getID(),
	// EPickingType.BUCKET_REMOVE_ICON_SELECTION,
	// // EIconTextures.NAVIGATION_REMOVE_VIEW, fHandleHeight, fHandleHeight,
	// eOrientation);
	// // gl.glTranslatef(-fHandleWidth + fHandleHeight, -fHandleWidth -
	// fHandleHeight, 0);
	// //
	// // // Render background (also draggable)
	// //
	// // gl.glPushName(pickingManager.getPickingID(iUniqueID,
	// EPickingType.BUCKET_DRAG_ICON_SELECTION,
	// element
	// // .getID()));
	// gl.glColor3f(0.25f, 0.25f, 0.25f);
	// //
	// //// if (eOrientation == EOrientation.TOP) {
	// gl.glBegin(GL.GL_POLYGON);
	// gl.glVertex3f(0 + fHandleHeight, fHandleWidth + fHandleHeight, 0);
	// gl.glVertex3f(fHandleWidth - 2 * fHandleHeight, fHandleWidth +
	// fHandleHeight, 0);
	// gl.glVertex3f(fHandleWidth - 2 * fHandleHeight, fHandleWidth, 0);
	// gl.glVertex3f(0 + fHandleHeight, fHandleWidth, 0);
	// gl.glEnd();
	// //// }
	// // gl.glPopName();
	// //
	// if (eOrientation == EOrientation.LEFT)
	// gl.glRotatef(-90, 0, 0, 1);
	//
	// //
	// // // Render view information
	// // String sText =
	// //
	// generalManager.getViewGLCanvasManager().getGLEventListener(element.getContainedElementID())
	// // .getShortInfo();
	// //
	// // int iMaxChars = 50;
	// // if (sText.length() > iMaxChars) {
	// // sText = sText.subSequence(0, iMaxChars - 3) + "...";
	// // }
	// //
	// // float fTextScalingFactor = 0.0027f;
	// //
	// // if (bUpsideDown) {
	// // gl.glRotatef(180, 1, 0, 0);
	// // gl.glTranslatef(0, -4 - fHandleHeight, 0);
	// // }
	// //
	// // textRenderer.setColor(0.7f, 0.7f, 0.7f, 1);
	// // textRenderer.begin3DRendering();
	// // textRenderer.draw3D(sText, fHandleWidth / fScalingFactor
	// // - (float) textRenderer.getBounds(sText).getWidth() / 2f *
	// fTextScalingFactor, fHandleWidth + .02f,
	// // 0f, fTextScalingFactor);
	// // textRenderer.end3DRendering();
	// //
	// // if (bUpsideDown) {
	// // gl.glTranslatef(0, 4 + fHandleHeight, 0);
	// // gl.glRotatef(-180, 1, 0, 0);
	// // }
	// }

	private void renderNavigationHandleBar(final GL gl, RemoteLevelElement element,
			float fHandleWidth, float fHandleHeight, boolean bUpsideDown,
			float fScalingFactor) {

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
		renderSingleHandle(gl, element.getID(),
				EPickingType.BUCKET_REMOVE_ICON_SELECTION,
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
		String sText = element.getGLView().getShortInfo();

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

	// FIXME: method copied from bucket
	private void renderSingleHandle(final GL gl, int iRemoteLevelElementID,
			EPickingType ePickingType, EIconTextures eIconTexture, float fWidth,
			float fHeight) {

		gl.glPushName(pickingManager.getPickingID(iUniqueID, ePickingType,
				iRemoteLevelElementID));

		Texture tempTexture = textureManager.getIconTexture(gl, eIconTexture);
		tempTexture.enable();
		tempTexture.bind();

		TextureCoords texCoords = tempTexture.getImageTexCoords();
		gl.glColor3f(1, 1, 1);
		gl.glBegin(GL.GL_POLYGON);

		// if (eOrientation == EOrientation.TOP) {
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(0, -fHeight, 0f);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(0, 0, 0f);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(fWidth, 0, 0f);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(fWidth, -fHeight, 0f);
		gl.glEnd();
		// }
		// else if (eOrientation == EOrientation.LEFT) {
		// gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		// gl.glVertex3f(0, -fHeight, 0f);
		// gl.glTexCoord2f(texCoords.left(), texCoords.top());
		// gl.glVertex3f(0, 0, 0f);
		// gl.glTexCoord2f(texCoords.right(), texCoords.top());
		// gl.glVertex3f(fWidth, 0, 0f);
		// gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		// gl.glVertex3f(fWidth, -fHeight, 0f);
		// gl.glEnd();
		// }

		tempTexture.disable();

		gl.glPopName();
	}

	// FIXME: method copied from bucket
	public void renderBucketWall(final GL gl, boolean bRenderBorder) {

		gl.glLineWidth(2);

		// Highlight potential view drop destination
		// if (dragAndDrop.isDragActionRunning() && element.getID() ==
		// iMouseOverObjectID) {
		// gl.glLineWidth(5);
		// }
		// gl.glColor4f(0.2f, 0.2f, 0.2f, 1);
		// gl.glBegin(GL.GL_LINE_LOOP);
		// gl.glVertex3f(0, 0, 0.01f);
		// gl.glVertex3f(0, 8, 0.01f);
		// gl.glVertex3f(8, 8, 0.01f);
		// gl.glVertex3f(8, 0, 0.01f);
		// gl.glEnd();
		// }

		// if (arSlerpActions.isEmpty()) {
		gl.glColor4f(1f, 1f, 1f, 1.0f); // normal mode
		// }
		// else {
		// gl.glColor4f(1f, 1f, 1f, 0.3f);
		// }

		if (!newViews.isEmpty()) {
			gl.glColor4f(1f, 1f, 1f, 0.3f);
		}

		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(0, 0, -0.01f);
		gl.glVertex3f(0, 8, -0.01f);
		gl.glVertex3f(8, 8, -0.01f);
		gl.glVertex3f(8, 0, -0.01f);
		gl.glEnd();

		if (!bRenderBorder)
			return;

		gl.glColor4f(0.4f, 0.4f, 0.4f, 1f);
		gl.glLineWidth(1f);
	}

	private void updateViewDetailLevels(RemoteLevelElement element) {

		AGLView glActiveSubView = element.getGLView();

		if (glActiveSubView == null)
			return;

		glActiveSubView.setRemoteLevelElement(element);

		// Update detail level of moved view when slerp action is finished;
		if (element == focusElement) {
			glActiveSubView.setDetailLevel(EDetailLevel.HIGH);
		} else {
			glActiveSubView.setDetailLevel(EDetailLevel.LOW);
		}
	}

	@Override
	public void registerEventListeners() {

		super.registerEventListeners();

		addPathwayListener = new AddPathwayListener();
		addPathwayListener.setHandler(this);
		eventPublisher.addListener(LoadPathwayEvent.class, addPathwayListener);

		loadPathwaysByGeneListener = new LoadPathwaysByGeneListener();
		loadPathwaysByGeneListener.setHandler(this);
		eventPublisher.addListener(LoadPathwaysByGeneEvent.class,
				loadPathwaysByGeneListener);
	}

	@Override
	public void unregisterEventListeners() {

		super.unregisterEventListeners();

		if (addPathwayListener != null) {
			eventPublisher.removeListener(addPathwayListener);
			addPathwayListener = null;
		}

		if (loadPathwaysByGeneListener != null) {
			eventPublisher.removeListener(loadPathwaysByGeneListener);
			loadPathwaysByGeneListener = null;
		}
	}

	@Override
	public void addPathwayView(int iPathwayID) {
		isPathwayContentAvailable = true;

	}

	@Override
	public void loadDependentPathways(Set<PathwayGraph> newPathwayGraphs) {
		isPathwayContentAvailable = true;
	}

	@Override
	public void setConnectionLinesEnabled(boolean enabled) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setGeneMappingEnabled(boolean geneMappingEnabled) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setNeighborhoodEnabled(boolean neighborhoodEnabled) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPathwayTexturesEnabled(boolean pathwayTexturesEnabled) {
		// TODO Auto-generated method stub

	}

	@Override
	public void toggleNavigationMode() {
		// TODO Auto-generated method stub

	}

	@Override
	public void toggleZoom() {
		// TODO Auto-generated method stub

	}
}
