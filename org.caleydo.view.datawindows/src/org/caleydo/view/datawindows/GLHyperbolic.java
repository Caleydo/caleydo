package org.caleydo.view.datawindows;

import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import gleem.linalg.open.Transform;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import javax.media.opengl.GL2;

import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.EVAOperation;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.datadomain.IDataDomain;
import org.caleydo.core.manager.datadomain.IDataDomainBasedView;
import org.caleydo.core.manager.event.view.remote.LoadPathwayEvent;
import org.caleydo.core.manager.event.view.remote.LoadPathwaysByGeneEvent;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.tracking.TrackDataProvider;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.DetailLevel;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevelElement;
import org.caleydo.datadomain.pathway.IPathwayLoader;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.listener.LoadPathwaysByGeneListener;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.caleydo.rcp.view.listener.AddPathwayListener;
import org.caleydo.rcp.view.listener.IRemoteRenderingHandler;
import org.caleydo.view.pathway.GLPathway;
import org.caleydo.view.pathway.SerializedPathwayView;

/**
 * Rendering the Datawindow
 * 
 * @author Hannes Plank
 * @author Marc Streit
 */
@SuppressWarnings("unused")
public class GLHyperbolic extends AGLView implements IRemoteRenderingHandler,
		IPathwayLoader {

	public final static String VIEW_ID = "org.caleydo.view.hyperbolic";

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

	private ArrayList<SimpleSlerp> simpleSlerpActions;

	private float currentAngleSlerpFactor;

	private float previousSimpleSlerp;

	private AddPathwayListener addPathwayListener;
	private LoadPathwaysByGeneListener loadPathwaysByGeneListener;

	private ArrayList<ASerializedView> newViews;

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param viewFrustum
	 */
	public GLHyperbolic(GLCaleydoCanvas glCanvas, final ViewFrustum viewFrustum) {

		super(glCanvas, viewFrustum, true);
		viewType = GLHyperbolic.VIEW_ID;

		// preparing the eyetracker
		// this.tracker = new TrackDataProvider();
		// tracker.startTracking();

		remoteNodeElement = new RemoteLevelElement(null);

		disk = new DataWindowsDisk(this);

		arSlerpActions = new ArrayList<NodeSlerp>();
		simpleSlerpActions = new ArrayList<SimpleSlerp>();
		newViews = new ArrayList<ASerializedView>();

		displayFullView = false;
		slerpedNode = new PoincareNode(null, "", 1);

	}

	@Override
	public void initLocal(GL2 gl) {

		iGLDisplayListIndexLocal = gl.glGenLists(5);

		iGLDisplayListToCall = iGLDisplayListIndexLocal;
		init(gl);

	}

	@Override
	public void initRemote(final GL2 gl, final AGLView glParentView,
			final GLMouseListener glMouseListener) {

		this.glMouseListener = glMouseListener;

		iGLDisplayListIndexRemote = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexRemote;

		init(gl);
	}

	@Override
	public void setDetailLevel(DetailLevel detailLevel) {
		super.setDetailLevel(detailLevel);
	}

	@Override
	public void displayLocal(GL2 gl) {

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
	public void displayRemote(GL2 gl) {

		display(gl);
		checkForHits(gl);
	}

	@Override
	public void display(GL2 gl) {

		doSlerpActions();
		initNewView(gl);

		// zoomTree has to be called every loop iteration, because every
		// operation on the tree has to be transformed into zoomed coordinates
		disk.zoomTree(diskZoomIntensity);

		// renders the tree and all of it's nodes using the zoomed coordinates
		disk.renderTree(gl, textureManager, pickingManager, iUniqueID,
				(float) viewFrustum.getWidth(), (float) viewFrustum.getHeight());

		// if (!containedGLViews.isEmpty()) {
		//
		// containedGLViews.get(0).displayRemote(gl);
		// // renderRemoteLevelElement(gl,
		// // testLevel.getElementByPositionIndex(0));
		//

		// buildDisplayList(gl, iGLDisplayListIndexRemote);
		// if (!isRenderedRemote())
		// contextMenu.render(gl, this);

		// Render invisible background for detecting clicks using GL2 selection
		// mechanism
		// gl.glPushName(pickingManager.getPickingID(iUniqueID,
		// EPickingType.BACKGROUND_HYPERBOLIC, 0));
		// gl.glColor4f(1, 0, 0, 0);
		// gl.glBegin(GL2.GL_POLYGON);
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

	// creates a slerp rotating the tree to the right position
	public void correctDiskAngle() {
		if (simpleSlerpActions.isEmpty()) {
			previousSimpleSlerp = 0;
			SimpleSlerp actualSlerp = new SimpleSlerp();

			// the ending condition of the slerp is the correct angle
			actualSlerp.endingCondition = disk.calculateCorrectDiskRotation(disk
					.getCenteredNode());

			actualSlerp.speed = 10;

			simpleSlerpActions.add(actualSlerp);
		}
	}

	private void buildDisplayList(final GL2 gl, int iGLDisplayListIndex) {

		if (bHasFrustumChanged) {
			bHasFrustumChanged = false;
		}

		gl.glNewList(iGLDisplayListIndex, GL2.GL_COMPILE);
		gl.glEndList();
	}

	/**
	 * Adds new remote-rendered-views that have been queued for displaying to
	 * this view. Only one view is taken from the list and added for remote
	 * rendering per call to this method.
	 * 
	 * @param GL
	 */
	private void initNewView(GL2 gl) {
		if (!newViews.isEmpty() && PathwayManager.get().isPathwayLoadingFinished()
				&& arSlerpActions.isEmpty()) {

			ASerializedView serView = newViews.remove(0);
			AGLView view = createView(gl, serView);

			ViewHyperbolicNode node = new ViewHyperbolicNode(tree, "TODO: set me!", 1,
					view);
			disk.insertNode(node, disk.getCenteredNode());

		}
	}

	private void renderRemoteLevelElement(final GL2 gl, RemoteLevelElement element) {

		AGLView glView = element.getGLView();

		if (glView == null) {
			return;
		}

		gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.REMOTE_LEVEL_ELEMENT, element.getID()));
		gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.REMOTE_VIEW_SELECTION, glView.getID()));
		gl.glPushMatrix();

		Transform transform = element.getTransform();
		Vec3f translation = transform.getTranslation();
		Rotf rot = transform.getRotation();
		Vec3f scale = transform.getScale();
		Vec3f axis = new Vec3f();
		float fAngle = rot.get(axis);

		// Corrections in translation and scaling for magnifying embedded views
		// gl.glTranslatef(translation.x() - glView.getViewFrustum().getWidth()
		// / 2f * scale.x(), translation.y()
		// - glView.getViewFrustum().getHeight() / 2f * scale.y(),
		// translation.z());
		gl.glTranslatef(translation.x(), translation.y(), translation.z());
		gl.glRotatef(Vec3f.convertRadiant2Grad(fAngle), axis.x(), axis.y(), axis.z());

		gl.glScalef(scale.x(), scale.y(), scale.z());

		glView.displayRemote(gl);
		gl.glPopMatrix();
		gl.glPopName();
		gl.glPopName();
	}

	@Override
	public String getShortInfo() {

		return "TODO: Hyperbolic info";
	}

	@Override
	public String getDetailedInfo() {

		return "TODO: Hyperbolic Detail Info";
	}

	@Override
	protected void handlePickingEvents(EPickingType pickingType,
			EPickingMode pickingMode, int iExternalID, Pick pick) {
		if (detailLevel == DetailLevel.VERY_LOW) {
			return;
		}

		SelectionType selectionType;
		switch (pickingType) {

		case BACKGROUND_HYPERBOLIC:
			switch (pickingMode) {
			case RIGHT_CLICKED:

				break;
			}

		case DATAW_NODE:
			switch (pickingMode) {

			case CLICKED:
			}

		}

	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedHyperbolicView serializedForm = new SerializedHyperbolicView();
		serializedForm.setViewID(this.getID());

		return serializedForm;
	}

	@Override
	public String toString() {
		return "TODO: Hyperbolic Info";
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
	public void init(GL2 gl) {

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
	private AGLView createView(GL2 gl, ASerializedView serView) {

		@SuppressWarnings("rawtypes")
		Class viewClass;
		try {
			viewClass = Class.forName(serView.getViewClassType());
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("Cannot find class for view "+serView.getViewType());
		}
		
		AGLView glView = GeneralManager.get().getViewGLCanvasManager()
				.createGLView(viewClass, parentGLCanvas, viewFrustum);
		//glView.setRemoteRenderingGLView(this);

		if (glView instanceof IDataDomainBasedView<?>) {
			((IDataDomainBasedView<IDataDomain>) glView).setDataDomain(DataDomainManager
					.get().getDataDomain(serView.getDataDomainType()));
		}
		
		if (glView instanceof GLPathway) {
			GLPathway glPathway = (GLPathway) glView;

			glPathway.setPathway(((SerializedPathwayView) serView).getPathwayID());
			glPathway.enablePathwayTextures(true);
			glPathway.enableNeighborhood(false);
			glPathway.enableGeneMapping(false);
		}
		glView.initialize();
		glView.initRemote(gl, this, glMouseListener);
		
		return glView;
	}

	public void doSlerpActions() {

		// slerping the translation
		if (arSlerpActions.isEmpty() == false) {

			NodeSlerp singleSlerp = arSlerpActions.get(0);
			if (singleSlerp.doASlerp(slerpedNode.getZoomedPosition()) == true) {
				// translating the tree by the differental step, calculated by
				// the node slerp
				disk.translateTreeMoebius(singleSlerp.returnPoint);
			} else {
				// the last translation, after the slerp has finished
				disk.setCenteredNode(slerpedNode);

				// move the node exactly in the middle of the disk
				float[] translationVector = new float[2];
				translationVector[0] = disk.getCenteredNode().getPosition()[0] * -1;
				translationVector[1] = disk.getCenteredNode().getPosition()[1] * -1;
				disk.translateTreeMoebius(translationVector);

				arSlerpActions.remove(0);
			}
		}

		// slerping the rotation
		if (simpleSlerpActions.isEmpty() == false) {
			SimpleSlerp simpleSlerp = simpleSlerpActions.get(0);
			if (simpleSlerp.doASlerp() == true) {

				// calculating the differential slerp angle
				double relativeSimpleSlerpState = simpleSlerp.state - previousSimpleSlerp;

				// rotating the disk with the differential slerp angle
				disk.rotateDisk(simpleSlerp.state - this.previousSimpleSlerp);
				this.previousSimpleSlerp = simpleSlerp.state;
			} else {
				disk.rotateDisk(simpleSlerp.state - this.previousSimpleSlerp);
				simpleSlerpActions.clear();
			}
		}

	}

	// displays a node using a remote view
	public void drawRemoteView(GL2 gl, PoincareNode node, float[] position, float size) {

		Transform transform = new Transform();
		remoteNodeElement.setGLView(((ViewHyperbolicNode) node).getGlView());
		// remoteNodeElement.getGLView().getViewFrustum().setBottom(fBottom)
		// if a node is totally zoomed in, the remote view of the node is
		// displayed on the full hyperbolic view
		if (this.displayFullView == true && (this.disk.getCenteredNode() == node)) {
			transform.setScale(new Vec3f(1, 1, 1));
			transform.setTranslation(new Vec3f(0, 0, 0.1f));
		} else {
			// in this case, the size of the displayed remote view depends on
			// the position of the node
			transform.setScale(new Vec3f(size, size * fAspectRatio, 1));

			transform.setTranslation(new Vec3f(position[0], position[1], 0));
		}

		remoteNodeElement.setTransform(transform);
		renderRemoteLevelElement(gl, remoteNodeElement);

	}

	private void createPathwayTree(GL2 gl) {

		// creating a sample tree for testing
		tree = new Tree<PoincareNode>();

		GLPathway glPathwayView = createPathwayView(gl);
		glPathwayView.broadcastElements(EVAOperation.APPEND_UNIQUE);

		ViewHyperbolicNode node = new ViewHyperbolicNode(tree, "pathway", 1,
				glPathwayView);

		tree.setRootNode(node);

		node = new ViewHyperbolicNode(tree, "child_pathway", 1, createPathwayView(gl));
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
	}

	@Deprecated
	public GLPathway createPathwayView(GL2 gl) {

		SerializedPathwayView serPathway = new SerializedPathwayView(
				"org.caleydo.datadomain.pathway");
		// serTestPathway.setPathwayID(generalManager.getPathwayManager().searchPathwayByName("TGF-beta signaling pathway",
		// EPathwayDatabaseType.KEGG).getID());
		serPathway.setPathwayID(((PathwayGraph) (PathwayManager.get().getAllItems()
				.toArray()[randomGenerator.nextInt(PathwayManager.get().getAllItems()
				.size())])).getID());

		return (GLPathway) createView(gl, serPathway);
	}

	// called, if the user focuses a point on the display with his eyes
	public boolean setEyeTrackerAction(int[] mouseCoord, float[] offset,
			float[] scalation, boolean eyeControlled) {
		// if (this.get != null) {
		if (this.displayFullView) {
			return false;
		}

		float[] mousePoint = new float[2];
		mousePoint[0] = (float) mouseCoord[0];
		mousePoint[1] = (float) mouseCoord[1];

		boolean returnValue;

		float factorX = 1 / (float) (this.getParentGLCanvas().getWidth() * scalation[0]);
		float factorY = 1 / (float) (this.getParentGLCanvas().getHeight());

		if (mousePoint != null && offset != null) {
			mousePoint[0] = (mousePoint[0] * factorX - offset[0]) * 2 - 1;
			mousePoint[1] = ((this.getParentGLCanvas().getHeight() - mousePoint[1])
					* factorY - offset[1]) * 2 - 1;
		}

		PoincareNode selectedNode = disk.processEyeTrackerAction(mousePoint.clone(),
				arSlerpActions, eyeControlled);

		if (selectedNode == disk.getCenteredNode()) {
			return true;
		}

		if (selectedNode != null) {
			disk.setCenteredNode(selectedNode);
			slerpedNode = selectedNode;
			returnValue = true;
		} else {
			returnValue = false;
		}
		correctDiskAngle();
		return returnValue;
	}

	@Override
	public void addPathwayView(int iPathwayID) {

		if (!PathwayManager.get().isPathwayVisible(
				PathwayManager.get().getItem(iPathwayID))) {
			SerializedPathwayView serPathway = new SerializedPathwayView(
					"org.caleydo.datadomain.pathway");
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