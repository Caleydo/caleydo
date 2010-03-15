package org.caleydo.view.datawindows;


import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import gleem.linalg.open.Transform;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

import javax.media.opengl.GL;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.view.opengl.CmdCreateView;
import org.caleydo.core.data.graph.pathway.core.PathwayGraph;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.selection.EVAOperation;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.manager.ICommandManager;
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
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevelElement;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.caleydo.view.pathway.GLPathway;
import org.caleydo.view.pathway.SerializedPathwayView;

/**
 * Rendering the Datawindow
 * 
 * @author Hannes Plank
 * @author Marc Streit
 */
@SuppressWarnings("unused")
public class GLHyperbolic extends AGLView {

	public final static String VIEW_ID = "org.caleydo.view.hyperbolic";

	private double mouseCoordX = 0;
	private double mouseCoordY = 0;

	private double viewport[] = new double[16];

	private float canvasWidth;
	private float canvasHeight;

	private TrackDataProvider tracker;
	private float[] receivedEyeData;

	private RemoteLevelElement testRemoteElement;

	private ArrayList<NodeSlerp> arSlerpActions;

	public DataWindowsDisk disk;

	// properties of the circle
	// private double circleRadius = 0.5;

	private PoincareNode slerpedNode;

	private boolean manualPickFlag = true;

	

	private org.eclipse.swt.graphics.Point upperLeftScreenPos = new org.eclipse.swt.graphics.Point(
			0, 0);

	private Random randomGenerator = new Random(19580427);

	private Tree<PoincareNode> tree;
	public double diskZoomIntensity = 0;
	
	private DataWindowsMouseWheelListener mouseWheelListener ;
	
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

		// preparing the eyetracker
		// this.tracker = new TrackDataProvider();
		// tracker.startTracking();

		// remote test
		Transform transform = new Transform();
		transform.setTranslation(new Vec3f(0, 0, 0));
		transform.setScale(new Vec3f(0.5f, 0.5f, 1));
		testRemoteElement = new RemoteLevelElement(null);
		testRemoteElement.setTransform(transform);

		// debug

		disk = new DataWindowsDisk(this);

		
		
//mouseWheelListener = new DataWindowsMouseWheelListener(this);
		
//parentGLCanvas.removeMouseWheelListener(glMouseListener);
		//parentGLCanvas.removeMouseListener(glMouseListener);
		// Register specialized bucket mouse wheel listener
//parentGLCanvas.addMouseWheelListener(mouseWheelListener);
	
		
		
		//glMouseListener.addGLCanvas(this);
		
		
		
		// nullpointer:
		// Tree<ClusterNode> tree = set.getStorageTree();

		arSlerpActions = new ArrayList<NodeSlerp>();

		// ASerializedView serView = getSerializableRepresentation();
		// newViews.add(serView);

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
		gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, viewport, 0);

		canvasWidth = 2 / (float) viewport[0];
		canvasHeight = 2 / (float) viewport[5];// if (set == null)
		// return;

		if (bIsDisplayListDirtyLocal) {

			buildDisplayList(gl, iGLDisplayListIndexLocal);
			bIsDisplayListDirtyLocal = false;

		}
		iGLDisplayListToCall = iGLDisplayListIndexLocal;

		// pickingManager.handlePicking(this, gl);

		checkForHits(gl);
		display(gl);

		if (eBusyModeState != EBusyModeState.OFF)
			renderBusyMode(gl);

	}

	@Override
	public void displayRemote(GL gl) {

		gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, viewport, 0);

		canvasWidth = 2 / (float) viewport[0];
		canvasHeight = 2 / (float) viewport[5];// if (set == null)

		checkForHits(gl);
		display(gl);
	}

	@Override
	public void display(GL gl) {

		// GLHelperFunctions.drawAxis(gl);
		// GLHelperFunctions.drawViewFrustum(gl, viewFrustum);
		// gl.glEnable(GL.GL_DEPTH_TEST);
		// clipToFrustum(gl);

		// gl.glMatrixMode(GL.GL_PROJECTION);
		// gl.glLoadIdentity();
		// //
		// gl.glOrtho(0.0f, canvasWidth,canvasHeight, 0.0f, -1.0f, 1.0f);
		// //
		// gl.glMatrixMode(GL.GL_MODELVIEW);
		// gl.glLoadIdentity();
		//

		// System.out.println("mouseZeiger:"+mousePoint.getX()+"|"+mousePoint.getY());

		//

		// gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
		//		 

		// System.out.println("viewport: " +canvasWidth + "|"+canvasHeight);
		// canvasWidth=7;
		// canvasHeight=5;
		//		

		//
		// }
		//
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
		disk.zoomTree(diskZoomIntensity);
		disk.renderTree(gl, textureManager, pickingManager, iUniqueID,
				(double) canvasWidth, (double) canvasHeight);

		//		

		

		if (glMouseListener.wasLeftMouseButtonPressed()) {
			
		
			

			if (glMouseListener.getPickedPoint() != null) {

				System.out.println("leftmouse");
				if (manualPickFlag == true) {
					Point mousePoint = new Point(0, 0);
					mousePoint = glMouseListener.getPickedPoint();
					int[] viewport = new int[4];

					gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
					double factorX = (double) canvasWidth
							/ (double) viewport[2];
					double factorY = (double) canvasHeight
							/ (double) viewport[3];

					mouseCoordX = (double) (mousePoint.getX() * factorX);
					mouseCoordY = (double) (mousePoint.getY() * factorY);

					PoincareNode selectedNode;
					selectedNode = disk.processEyeTrackerAction(
							new Point2D.Double(mouseCoordX, mouseCoordY),
							arSlerpActions);
					if (selectedNode != null) {
						System.out.println("nodeSelected:"
								+ selectedNode.iComparableValue);

						// arSlerpActions.add(new nodeSlerp(4,
						// selectedNode.getPosition(),
						// new Point2D.Double(0, 0)));

						slerpedNode = selectedNode;
						disk.setCenteredNode(selectedNode);

					}
				}
			}
		}

		// if (!containedGLViews.isEmpty()) {
		//
		// containedGLViews.get(0).displayRemote(gl);
		// // renderRemoteLevelElement(gl,
		// // testLevel.getElementByPositionIndex(0));
		//

		// buildDisplayList(gl, iGLDisplayListIndexRemote);
		// if (!isRenderedRemote())
		// contextMenu.render(gl, this);
	}

	private void buildDisplayList(final GL gl, int iGLDisplayListIndex) {

		if (bHasFrustumChanged) {
			bHasFrustumChanged = false;
		}

		gl.glNewList(iGLDisplayListIndex, GL.GL_COMPILE);
		gl.glEndList();
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

		gl.glTranslatef(translation.x(), translation.y(), translation.z());
		gl.glRotatef(Vec3f.convertRadiant2Grad(fAngle), axis.x(), axis.y(),
				axis.z());
		gl.glScalef(scale.x(), scale.y(), scale.z());

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
		return "TODO: ScatterploT Deatil Info";
	}

	@Override
	protected void handlePickingEvents(EPickingType ePickingType,
			EPickingMode pickingMode, int iExternalID, Pick pick) {
		if (detailLevel == EDetailLevel.VERY_LOW) {
			return;
		}

		SelectionType selectionType;
		switch (ePickingType) {

			case DATAW_NODE :
				switch (pickingMode) {

					case CLICKED :

						arSlerpActions.add(new NodeSlerp(4, disk
								.getNodeByCompareableValue(iExternalID)
								.getPosition(), new Point2D.Double(0, 0)));

						slerpedNode = disk
								.getNodeByCompareableValue(iExternalID);
						disk.setCenteredNode(disk
								.getNodeByCompareableValue(iExternalID));

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

	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();
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

//	private void initNewView(GL gl) {
//		// if (!newViews.isEmpty()) {
//		// ASerializedView serView = newViews.remove(0);
//		// AGLView view = createView(gl, serView);
//		// containedGLViews.add(view);
//		// testRemoteElement.setGLView(view);
//		// }
//
//		SerializedPathwayView serTestPathway = new SerializedPathwayView(
//				dataDomain);
//		// serTestPathway.setPathwayID(generalManager.getPathwayManager().searchPathwayByName("TGF-beta signaling pathway",
//		// EPathwayDatabaseType.KEGG).getID());
//				.getPathwayManager().getAllItems().toArray()[randomGenerator
//				.nextInt(generalManager.getPathwayManager().getAllItems()
//						.size())])).getID());
//
//		AGLView view = createView(gl, serTestPathway);
//		view.broadcastElements(EVAOperation.APPEND_UNIQUE);
//		testRemoteElement.setGLView(view);
//	}

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
		}

		glView.initRemote(gl, this, glMouseListener, null);

		return glView;
	}

	private void doSlerpActions() {
		if (arSlerpActions.isEmpty()) {
			return;
		}

		NodeSlerp singleSlerp = arSlerpActions.get(0);
		if (singleSlerp.doASlerp(slerpedNode.getZoomedPosition()) == true) {

			disk.translateTreeMoebius(singleSlerp.returnPoint);
		}
		else {

			disk.translateTreeMoebius(singleSlerp.returnPoint);
			disk.setCenteredNode(slerpedNode);
			disk.centeredNodeSize=disk.findOptimalCenterNodeSize(disk.getCenteredNode(), 10);
			arSlerpActions.remove(0);
		}		
	}
		

	public void drawRemoteView(GL gl, PoincareNode node, Point2D.Double position, double size) {

		Transform transform = new Transform();
		transform.setTranslation(new Vec3f((float) position.getX(),
				(float) position.getY(), 0));
		transform.setScale(new Vec3f((float) size, (float) size, 1));
		testRemoteElement.setTransform(transform);

//		initNewView(gl);
		testRemoteElement.setGLView(((ViewHyperbolicNode)node).getGlView());
		
		renderRemoteLevelElement(gl, testRemoteElement);
	}

	private void createPathwayTree(GL gl) {

		tree = new Tree<PoincareNode>();

		GLPathway glPathwayView = createPathwayView(gl);
		glPathwayView.broadcastElements(EVAOperation.APPEND_UNIQUE);
		
		ViewHyperbolicNode node = new ViewHyperbolicNode(tree, "pathway",
				1, glPathwayView);

		tree.setRootNode(node);
		
		for (int i=0; i<5; i++) {
		
			node =  new ViewHyperbolicNode(tree, "child_pathway",
					3, createPathwayView(gl));
			tree.addChild(tree.getRoot(), node);
			
			for (int j=0; j<5; j++) {
				
				ViewHyperbolicNode childNode =  new ViewHyperbolicNode(tree, "child_pathway",
						3, createPathwayView(gl));
				tree.addChild(node, childNode);
			}
		}
		
//		tree.addChild(node, new PoincareNode(tree, "Child1 l1", 3));
//		tree.addChild(node, new PoincareNode(tree, "Child2 l1", 3));
//		tree.addChild(node, new PoincareNode(tree, "Child1 l1", 3));
//		tree.addChild(node, new PoincareNode(tree, "Child2 l1", 3));
//		tree.addChild(node, new PoincareNode(tree, "Child1 l1", 3));

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

	private GLPathway createPathwayView(GL gl) {
		
		SerializedPathwayView serPathway = new SerializedPathwayView(
				dataDomain);
		// serTestPathway.setPathwayID(generalManager.getPathwayManager().searchPathwayByName("TGF-beta signaling pathway",
		// EPathwayDatabaseType.KEGG).getID());
		serPathway.setPathwayID(((PathwayGraph) (generalManager
				.getPathwayManager().getAllItems().toArray()[randomGenerator
				.nextInt(generalManager.getPathwayManager().getAllItems()
						.size())])).getID());

		return (GLPathway)createView(gl, serPathway);
	}
	

}