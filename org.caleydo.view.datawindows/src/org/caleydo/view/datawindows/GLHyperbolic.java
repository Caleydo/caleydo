package org.caleydo.view.datawindows;

import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import gleem.linalg.open.Transform;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import javax.media.opengl.GL;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.view.opengl.CmdCreateView;
import org.caleydo.core.data.graph.pathway.core.PathwayGraph;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.selection.EVAOperation;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.event.view.remote.LoadPathwayEvent;
import org.caleydo.core.manager.event.view.remote.LoadPathwaysByGeneEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.tracking.TrackDataProvider;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.GLHelperFunctions;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevelElement;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.caleydo.rcp.view.listener.AddPathwayListener;
import org.caleydo.rcp.view.listener.IRemoteRenderingHandler;
import org.caleydo.rcp.view.listener.LoadPathwaysByGeneListener;
import org.caleydo.view.pathway.GLPathway;
import org.caleydo.view.pathway.SerializedPathwayView;

/**
 * Rendering the Datawindow
 * 
 * @author Hannes Plank
 * @author Marc Streit
 */
@SuppressWarnings("unused")
public class GLHyperbolic extends AGLView implements IRemoteRenderingHandler {

	public final static String VIEW_ID = "org.caleydo.view.hyperbolic";

	// private double viewport[] = new double[16];
	//
	// public float canvasWidth;
	// public float canvasHeight;

	private TrackDataProvider tracker;
	private float[] receivedEyeData;

	private RemoteLevelElement remoteNodeElement;

	private ArrayList<NodeSlerp> arSlerpActions;

	public DataWindowsDisk disk;

	private PoincareNode slerpedNode;

	private boolean manualPickFlag = true;

	private org.eclipse.swt.graphics.Point upperLeftScreenPos = new org.eclipse.swt.graphics.Point(
			0, 0);

	private Random randomGenerator = new Random(19580427);

	public boolean displayFullView;

	public Tree<PoincareNode> tree;
	public float diskZoomIntensity = 0;

	private ArrayList<simpleSlerp> simpleSlerpActions;

	private float currentAngleSlerpFactor;

	private float previousSimpleSlerp;

	private AddPathwayListener addPathwayListener;
	private LoadPathwaysByGeneListener loadPathwaysByGeneListener;

	private ArrayList<ASerializedView> newViews;

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param sLabel
	 * @param viewFrustum
	 */
	public GLHyperbolic(GLCaleydoCanvas glCanvas, final String sLabel,
			final IViewFrustum viewFrustum) {

		super(glCanvas, sLabel, viewFrustum, true);
		viewType = GLHyperbolic.VIEW_ID;

		// preparing the eyetrackerall
		// this.tracker = new TrackDataProvider();
		// tracker.startTracking();

		// remote test
		// Transform transform = new Transform();
		// transform.setTranslation(new Vec3f(0, 0, 0));
		// transform.setScale(new Vec3f(0.5f, 0.5f, 1));
		remoteNodeElement = new RemoteLevelElement(null);
		// testRemoteElement.setTransform(transform);

		disk = new DataWindowsDisk(this);

		arSlerpActions = new ArrayList<NodeSlerp>();
		simpleSlerpActions = new ArrayList<simpleSlerp>();

		newViews = new ArrayList<ASerializedView>();

		displayFullView = false;

	}

	@Override
	public void initLocal(GL gl) {

		iGLDisplayListIndexLocal = gl.glGenLists(5);

		iGLDisplayListToCall = iGLDisplayListIndexLocal;
		init(gl);

	}

	@Override
	public void initRemote(final GL gl, final AGLView glParentView,
			final GLMouseListener glMouseListener,
			GLInfoAreaManager infoAreaManager) {

		this.glMouseListener = glMouseListener;

		iGLDisplayListIndexRemote = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexRemote;

		init(gl);
	}

	@Override
	public void setDetailLevel(EDetailLevel detailLevel) {
		super.setDetailLevel(detailLevel);
	}

	@Override
	public void displayLocal(GL gl) {
		processEvents();

		// if (!isVisible())
		// return;
		// gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, viewport, 0);
		//
		// canvasWidth = 2 / (float) viewport[0];
		// canvasHeight = 2 / (float) viewport[5];// if (set == null)
		// return;

		if (bIsDisplayListDirtyLocal) {
			buildDisplayList(gl, iGLDisplayListIndexLocal);
			bIsDisplayListDirtyLocal = false;
		}
		iGLDisplayListToCall = iGLDisplayListIndexLocal;

		// pickingManager.handlePicking(this, gl);

		display(gl);
		checkForHits(gl);

		if (eBusyModeState != EBusyModeState.OFF)
			renderBusyMode(gl);

	}

	@Override
	public void displayRemote(GL gl) {

		// gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, viewport, 0);
		// canvasWidth = 2 / (float) viewport[0];
		// canvasHeight = 2 / (float) viewport[5];// if (set == null)

		display(gl);
		checkForHits(gl);
	}

	@Override
	public void display(GL gl) {

		// GLHelperFunctions.drawViewFrustum(gl, viewFrustum);

		// GLHelperFunctions.drawPointAt(gl,new Vec3f(0,0,0));
		// GLHelperFunctions.drawPointAt(gl,new Vec3f(4,4,0));
		// GLHelperFunctions.drawPointAt(gl,new Vec3f(-2,-2,0));

		// receivedEyeData = tracker.getEyeTrackData();
		//
		// int offsetX = upperLeftScreenPos.x;
		// int offsetY = upperLeftScreenPos.y;
		//
		// receivedEyeData[0] = receivedEyeData[0] - (float) offsetX;
		// receivedEyeData[1] = receivedEyeData[1] - (float) offsetY;
		//
		// // System.out.println("Eye position korrigiert: " +
		// receivedEyeData[0]
		// // + " / " + receivedEyeData[1]);
		// float factorX = canvasWidth / (float) viewport[2];
		// float factorY = canvasHeight / (float) viewport[3];
		//
		// // visualisation of the eyecursor
		// gl.glBegin(GL.GL_LINE);
		// gl.glVertex3f(receivedEyeData[0] * factorX, receivedEyeData[1]
		// * factorY, 0);
		// gl.glVertex3f(2, 2, 0);
		// gl.glEnd();

		// remote test

		doSlerpActions();
		initNewView(gl);
		disk.zoomTree(diskZoomIntensity);

		disk
				.renderTree(gl, textureManager, pickingManager, iUniqueID,
						(float) viewFrustum.getWidth(), (float) viewFrustum
								.getHeight());

		// if (!containedGLViews.isEmpty()) {
		//
		// containedGLViews.get(0).displayRemote(gl);
		// // renderRemoteLevelElement(gl,
		// // testLevel.getElementByPositionIndex(0));
		//

		// buildDisplayList(gl, iGLDisplayListIndexRemote);
		// if (!isRenderedRemote())
		// contextMenu.render(gl, this);

		// Render invisible background for detecting clicks using GL selection
		// mechanism
		// gl.glPushName(pickingManager.getPickingID(iUniqueID,
		// EPickingType.BACKGROUND_HYPERBOLIC, 0));
		// gl.glColor4f(1, 0, 0, 0);
		// gl.glBegin(GL.GL_POLYGON);
		// gl.glVertex3f(viewFrustum.getLeft(), viewFrustum.getBottom(), 10);
		// gl.glVertex3f(viewFrustum.getRight() - viewFrustum.getLeft(),
		// viewFrustum
		// .getBottom(), 10);
		// gl.glVertex3f(viewFrustum.getRight() - viewFrustum.getLeft(),
		// viewFrustum
		// .getTop()
		// - viewFrustum.getBottom(), 10);
		// gl.glVertex3f(viewFrustum.getLeft(), viewFrustum.getTop()
		// - viewFrustum.getBottom(), 10);
		// gl.glEnd();
		// gl.glPopName();

	}

	public void correctDiskAngle() {

		this.previousSimpleSlerp = 0;
		simpleSlerp actualSlerp = new simpleSlerp();
		actualSlerp.endingCondition = disk.calculateCorrectDiskRotation(disk
				.getCenteredNode());

		System.out.println("hello from there" + actualSlerp.endingCondition);
		actualSlerp.speed = 10;

		simpleSlerpActions.add(actualSlerp);

	}

	private void buildDisplayList(final GL gl, int iGLDisplayListIndex) {

		if (bHasFrustumChanged) {
			bHasFrustumChanged = false;
		}

		gl.glNewList(iGLDisplayListIndex, GL.GL_COMPILE);
		gl.glEndList();
	}

	/**
	 * Adds new remote-rendered-views that have been queued for displaying to
	 * this view. Only one view is taken from the list and added for remote
	 * rendering per call to this method.
	 * 
	 * @param GL
	 */
	private void initNewView(GL gl) {
		if (!newViews.isEmpty()
				&& GeneralManager.get().getPathwayManager()
						.isPathwayLoadingFinished() && arSlerpActions.isEmpty()) {

			ASerializedView serView = newViews.remove(0);
			AGLView view = createView(gl, serView);

			ViewHyperbolicNode node = new ViewHyperbolicNode(tree, view
					.getLabel(), 1, view);
			disk.insertNode(node, disk.getCenteredNode());

		}
	}

	private void renderRemoteLevelElement(final GL gl,
			RemoteLevelElement element) {

		AGLView glView = element.getGLView();

		if (glView == null) {
			return;
		}

		gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.REMOTE_LEVEL_ELEMENT, element.getID()));
		gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.VIEW_SELECTION, glView.getID()));

		gl.glPushMatrix();

		Transform transform = element.getTransform();
		Vec3f translation = transform.getTranslation();
		Rotf rot = transform.getRotation();
		Vec3f scale = transform.getScale();
		Vec3f axis = new Vec3f();
		float fAngle = rot.get(axis);

		// Corrections in translation and scaling for magnifying embedded views
		gl.glTranslatef(translation.x() - glView.getViewFrustum().getWidth()
				/ 2f * scale.x(), translation.y()
				- glView.getViewFrustum().getHeight() / 2f * scale.y(),
				translation.z());
		gl.glRotatef(Vec3f.convertRadiant2Grad(fAngle), axis.x(), axis.y(),
				axis.z());
		gl.glScalef(2 * scale.x(), 2 * scale.y(), scale.z());

		glView.displayRemote(gl);
		gl.glPopMatrix();
		gl.glPopName();
		gl.glPopName();
	}

	@Override
	public String getShortInfo() {
		if (contentVA == null)
			return "Scatterplot - 0 " + useCase.getContentLabel(false, true)
					+ " / 0 experiments";

		return "Scatterplot - " + contentVA.size() + " "
				+ useCase.getContentLabel(false, true) + " / "
				+ storageVA.size() + " experiments";
	}

	@Override
	public String getDetailedInfo() {
		StringBuffer sInfoText = new StringBuffer();
		sInfoText.append("<b>Type:</b> Scatter Plot\n");
		// TODO Everything

		// return sInfoText.toString();
		return "TODO: Hyperbolic Detail Info";
	}

	@Override
	protected void handlePickingEvents(EPickingType ePickingType,
			EPickingMode pickingMode, int iExternalID, Pick pick) {
		if (detailLevel == EDetailLevel.VERY_LOW) {
			return;
		}

		SelectionType selectionType;
		switch (ePickingType) {

		case BACKGROUND_HYPERBOLIC:
			switch (pickingMode) {
			case RIGHT_CLICKED:

				// // if (testZoomViewEventSwitch == false) {
				// directHyperbolicView.disk.rotateDisk(Math.PI/180);

				// this.previousSimpleSlerp = 0;
				// simpleSlerp actualSlerp = new simpleSlerp();
				// actualSlerp.endingCondition = disk
				// .calculateCorrectDiskRotation(disk.getCenteredNode());
				// actualSlerp.speed = 10;
				//
				// simpleSlerpActions.add(actualSlerp);

				// this.focusViewEvent(2, 0.75, true);
				// testZoomViewEventSwitch = true;
				// // }
				break;
			}

		case DATAW_NODE:
			switch (pickingMode) {

			case CLICKED:

				// arSlerpActions.add(new NodeSlerp(4, disk
				// .getNodeByCompareableValue(iExternalID).getPosition(),
				// new Point2D.Double(0, 0)));
				//
				// slerpedNode = disk.getNodeByCompareableValue(iExternalID);

			}

		}

	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedDataWindowsView serializedForm = new SerializedDataWindowsView(
				dataDomain);
		serializedForm.setViewID(this.getID());

		return serializedForm;
	}

	@Override
	public String toString() {
		return "Standalone Scatterplot, rendered remote: " + isRenderedRemote()
				+ ", contentSize: " + contentVA.size() + ", storageSize: "
				+ storageVA.size() + ", contentVAType: " + contentVAType
				+ ", remoteRenderer:" + getRemoteRenderingGLCanvas();
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
	public void broadcastElements(EVAOperation type) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearAllSelections() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(GL gl) {

		// initNewView(gl);

		createPathwayTree(gl);
		disk.loadTree(tree);
		disk.zoomTree(0);
	}

	@Override
	public int getNumberOfSelections(SelectionType SelectionType) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void initFromSerializableRepresentation(ASerializedView ser) {

		SerializedDataWindowsView serializedView = (SerializedDataWindowsView) ser;

		setDisplayListDirty();
	}

	private AGLView createView(GL gl, ASerializedView serView) {

		ICommandManager cm = generalManager.getCommandManager();
		CmdCreateView cmdView = (CmdCreateView) cm
				.createCommandByType(ECommandType.CREATE_GL_VIEW);
		cmdView.setViewID(serView.getViewType());
		cmdView.setAttributesFromSerializedForm(serView);
		cmdView.doCommand();

		AGLView glView = cmdView.getCreatedObject();
		glView.setUseCase(useCase);
		// glView.setRemoteRenderingGLView(this);
		glView.setSet(set);

		if (glView instanceof GLPathway) {
			GLPathway glPathway = (GLPathway) glView;

			glPathway.setPathway(((SerializedPathwayView) serView)
					.getPathwayID());
			glPathway.enablePathwayTextures(true);
			glPathway.enableNeighborhood(false);
			glPathway.enableGeneMapping(false);

			// glPathway.setDetailLevel(EDetailLevel.VERY_LOW);
			// glPathway.broadcastbriElements(EVAOperation.APPEND_UNIQUE);
		}

		glView.initRemote(gl, this, glMouseListener, null);

		return glView;
	}

	public void doSlerpActions() {
		// slerping the translation
		if (arSlerpActions.isEmpty() == false) {

			NodeSlerp singleSlerp = arSlerpActions.get(0);
			if (singleSlerp.doASlerp(slerpedNode.getZoomedPosition()) == true) {

				disk.translateTreeMoebius(singleSlerp.returnPoint);
			} else {

				disk.translateTreeMoebius(singleSlerp.returnPoint);
				disk.setCenteredNode(slerpedNode);
				disk.centeredNodeSize = disk.findOptimalCenterNodeSize(disk
						.getCenteredNode(), 10);

				arSlerpActions.remove(0);
			}
		}
		// slerping the rotation

		if (simpleSlerpActions.isEmpty() == false) {

			simpleSlerp simpleSlerp = simpleSlerpActions.get(0);
			if (simpleSlerp.doASlerp() == true) {
				double relativeSimpleSlerpState = simpleSlerp.state
						- previousSimpleSlerp;
				disk.rotateDisk(simpleSlerp.state - this.previousSimpleSlerp);
				this.previousSimpleSlerp = simpleSlerp.state;
			} else {
				disk.rotateDisk(simpleSlerp.state - this.previousSimpleSlerp);
				simpleSlerpActions.clear();

			}
		}

	}

	public void drawRemoteView(GL gl, PoincareNode node, float[] position,
			float size) {

		Transform transform = new Transform();
		transform.setScale(new Vec3f(size, size * fAspectRatio, 1));
		transform.setTranslation(new Vec3f(position[0], position[1], 0));

		if (this.displayFullView == true
				&& (this.disk.getCenteredNode() == node)) {
			transform.setScale(new Vec3f(8 * remoteNodeElement.getTransform()
					.getScale().x(), 8
					* remoteNodeElement.getTransform().getScale().y()
					* fAspectRatio, 1));
			transform.setTranslation(new Vec3f(viewFrustum.getWidth() / 2,
					viewFrustum.getHeight() / 2, 0));
		}

		remoteNodeElement.setTransform(transform);
		remoteNodeElement.setGLView(((ViewHyperbolicNode) node).getGlView());

		if (this.displayFullView == true
				&& (this.disk.getCenteredNode() == node)) {
			renderRemoteLevelElement(gl, remoteNodeElement);
		}
		if (this.displayFullView == false) {
			renderRemoteLevelElement(gl, remoteNodeElement);
		}

	}

	private void createPathwayTree(GL gl) {

		tree = new Tree<PoincareNode>();

		GLPathway glPathwayView = createPathwayView(gl);
		glPathwayView.broadcastElements(EVAOperation.APPEND_UNIQUE);

		ViewHyperbolicNode node = new ViewHyperbolicNode(tree, "pathway", 1,
				glPathwayView);

		tree.setRootNode(node);

		node = new ViewHyperbolicNode(tree, "child_pathway", 1,
				createPathwayView(gl));
		tree.addChild(tree.getRoot(), node);

		for (int i = 0; i < 5; i++) {

			node = new ViewHyperbolicNode(tree, "child_pathway", i + 1,
					createPathwayView(gl));
			tree.addChild(tree.getRoot(), node);

			for (int j = 0; j < 5; j++) {

				ViewHyperbolicNode childNode = new ViewHyperbolicNode(tree,
						"child_pathway", i + j + 2, createPathwayView(gl));
				tree.addChild(node, childNode);
			}
		}

		// tree.addChild(node, new PoincareNode(tree, "Child1 l1", 3));
		// tree.addChild(node, new PoincareNode(tree, "Child2 l1", 3));
		// tree.addChild(node, new PoincareNode(tree, "Child1 l1", 3));
		// tree.addChild(node, new PoincareNode(tree, "Child2 l1", 3));
		// tree.addChild(node, new PoincareNode(tree, "Child1 l1", 3));

		// int iCount = 3344;
		// for (PoincareNode tempNode : tree.getChildren(node)) {
		//
		// PoincareNode tempNode22 = new PoincareNode(tree, "Child6 l1",
		// iCount--);
		// tree.addChild(tempNode, tempNode22);
		//
		// tree.addChild(tempNode22, new PoincareNode(tree, "Child7 l1",
		// iCount--));
		// tree.addChild(tempNode22, new PoincareNode(tree, "Child7 l1",
		// iCount--));
		//
		// PoincareNode tempNode433 = new PoincareNode(tree, "Child6 l1",
		// iCount--);
		// tree.addChild(tempNode22, tempNode433);
		// tree.addChild(tempNode433, new PoincareNode(tree, "Child7 l1",
		// iCount--));
		// tree.addChild(tempNode433, new PoincareNode(tree, "Child7 l1",
		// iCount--));
		//
		// PoincareNode tempNode33 = new PoincareNode(tree, "Child6 l1",
		// iCount--);
		// tree.addChild(tempNode22, tempNode33);
		// tree.addChild(tempNode33, new PoincareNode(tree, "Child7 l1",
		// iCount--));
		// tree.addChild(tempNode33, new PoincareNode(tree, "Child7 l1",
		// iCount--));
		//
		// tree.addChild(tempNode, new PoincareNode(tree, "Child3 l1",
		// iCount--));
		// tree.addChild(tempNode, new PoincareNode(tree, "Child4 l1",
		// iCount--));
		// tree.addChild(tempNode, new PoincareNode(tree, "Child3 l1",
		// iCount--));
		// tree.addChild(tempNode, new PoincareNode(tree, "Child4 l1",
		// iCount--));
		// tree.addChild(tempNode, new PoincareNode(tree, "Child3 l1",
		// iCount--));
		// tree.addChild(tempNode, new PoincareNode(tree, "Child4 l1",
		// iCount--));
		// tree.addChild(tempNode, new PoincareNode(tree, "Child3 l1",
		// iCount--));
		//
		// PoincareNode tempNode2 = new PoincareNode(tree, "Child6 l1",
		// iCount--);
		// tree.addChild(tempNode, tempNode2);
		//
		// tree.addChild(tempNode2, new PoincareNode(tree, "Child7 l1",
		// iCount--));
		// tree.addChild(tempNode2, new PoincareNode(tree, "Child7 l1",
		// iCount--));
		// tree.addChild(tempNode2, new PoincareNode(tree, "Child7 l1",
		// iCount--));
		// PoincareNode tempNode3 = new PoincareNode(tree, "Child6 l1",
		// iCount--);
		// tree.addChild(tempNode2, tempNode3);
		// tree.addChild(tempNode3, new PoincareNode(tree, "Child7 l1",
		// iCount--));
		// tree.addChild(tempNode3, new PoincareNode(tree, "Child7 l1",
		// iCount--));
		// tree.addChild(tempNode3, new PoincareNode(tree, "Child7 l1",
		// iCount--));
		// tree.addChild(tempNode3, new PoincareNode(tree, "Child7 l1",
		// iCount--));
		// tree.addChild(tempNode3, new PoincareNode(tree, "Child7 l1",
		// iCount--));
		// tree.addChild(tempNode3, new PoincareNode(tree, "Child7 l1",
		// iCount--));
		// PoincareNode tempNode34 = new PoincareNode(tree, "Child6 l1",
		// iCount--);
		// tree.addChild(tempNode3, tempNode34);
		// tree.addChild(tempNode34, new PoincareNode(tree, "Child7 l1",
		// iCount--));
		// tree.addChild(tempNode34, new PoincareNode(tree, "Child7 l1",
		// iCount--));
		// PoincareNode tempNode344 = new PoincareNode(tree, "Child6 l1",
		// iCount--);
		// tree.addChild(tempNode34, tempNode344);
		// tree.addChild(tempNode344, new PoincareNode(tree, "Child7 l1",
		// iCount--));
		// tree.addChild(tempNode344, new PoincareNode(tree, "Child7 l1",
		// iCount--));
		// PoincareNode tempNode3444 = new PoincareNode(tree, "Child6 l1",
		// iCount--);
		// tree.addChild(tempNode344, tempNode3444);
		// tree.addChild(tempNode3444, new PoincareNode(tree, "Child7 l1",
		// iCount--));
		// tree.addChild(tempNode3444, new PoincareNode(tree, "Child7 l1",
		// iCount--));
		// PoincareNode tempNode34444 = new PoincareNode(tree, "Child6 l1",
		// iCount--);
		// tree.addChild(tempNode3444, tempNode34444);
		// tree.addChild(tempNode34444, new PoincareNode(tree, "Child7 l1",
		// iCount--));
		// tree.addChild(tempNode34444, new PoincareNode(tree, "Child7 l1",
		// iCount--));
		// PoincareNode tempNode344444 = new PoincareNode(tree, "Child6 l1",
		// iCount--);
		// tree.addChild(tempNode34444, tempNode344444);
		// tree.addChild(tempNode344444, new PoincareNode(tree, "Child7 l1",
		// iCount--));
		// tree.addChild(tempNode344444, new PoincareNode(tree, "Child7 l1",
		// iCount--));
		// }

	}

	@Deprecated
	public GLPathway createPathwayView(GL gl) {

		SerializedPathwayView serPathway = new SerializedPathwayView(dataDomain);
		// serTestPathway.setPathwayID(generalManager.getPathwayManager().searchPathwayByName("TGF-beta signaling pathway",
		// EPathwayDatabaseType.KEGG).getID());
		serPathway.setPathwayID(((PathwayGraph) (generalManager
				.getPathwayManager().getAllItems().toArray()[randomGenerator
				.nextInt(generalManager.getPathwayManager().getAllItems()
						.size())])).getID());

		return (GLPathway) createView(gl, serPathway);
	}

	// called, if the user focuses a point on the display

	public void setEyeTrackerAction(float[] mousePoint, float[] offset,
			float[] scalation) {
		// if (this.get != null) {
		float[] mouseCoord = new float[2];

		// normation of the mouseposition
		float factorX = 1 / (float) (this.getParentGLCanvas().getWidth() * scalation[0]);
		float factorY = 1 / (float) (this.getParentGLCanvas().getHeight());

		mouseCoord[0] = (mousePoint[0] * factorX - offset[0]) * 2 - 1;
		mouseCoord[1] = ((this.getParentGLCanvas().getHeight() - mousePoint[1])
				* factorY - offset[1]) * 2 - 1;

		PoincareNode selectedNode = disk.processEyeTrackerAction(mouseCoord
				.clone(), arSlerpActions);

		if (selectedNode == disk.getCenteredNode()) {
			return;
		}

		if (selectedNode != null) {
			disk.setCenteredNode(selectedNode);

			slerpedNode = selectedNode;
			// disk.setCenteredNode(selectedNode);

		} else {
			disk.setCenteredNode(null);
		}

		correctDiskAngle();
	}

	@Override
	public void addPathwayView(int iPathwayID) {

		if (!generalManager.getPathwayManager().isPathwayVisible(
				generalManager.getPathwayManager().getItem(iPathwayID))) {
			SerializedPathwayView serPathway = new SerializedPathwayView(
					dataDomain);
			serPathway.setPathwayID(iPathwayID);
			newViews.add(serPathway);
		}
	}

	@Override
	public void loadDependentPathways(Set<PathwayGraph> newPathwayGraphs) {

		for (PathwayGraph pathway : newPathwayGraphs) {
			addPathwayView(pathway.getID());
		}
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