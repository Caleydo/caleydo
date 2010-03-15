package org.caleydo.view.datawindows;


import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import gleem.linalg.open.Transform;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.view.opengl.CmdCreateView;
import org.caleydo.core.data.selection.EVAOperation;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.manager.usecase.EDataDomain;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.tracking.TrackDataProvider;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.AStorageBasedView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevelElement;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.caleydo.view.heatmap.heatmap.SerializedHeatMapView;
import org.caleydo.view.parcoords.SerializedParallelCoordinatesView;
import org.caleydo.view.pathway.GLPathway;
import org.caleydo.view.pathway.SerializedPathwayView;

/**
 * Rendering the Datawindow
 * 
 * @author Hannes Plank
 * @author Marc Streit
 */
@SuppressWarnings("unused")
public class GLDataWindows extends AGLView implements IGLRemoteRenderingView {

	public final static String VIEW_ID = "org.caleydo.view.datawindows";

	private double mouseCoordX = 0;
	private double mouseCoordY = 0;

	private double viewport[] = new double[16];

	private float canvasWidth;
	private float canvasHeight;

	private TrackDataProvider tracker;
	private float[] receivedEyeData;

	private ArrayList<NodeSlerp> arSlerpActions;

	

	private MouseWheelEvent mouse;

	private PoincareNode slerpedNode;

	private boolean manualPickFlag = true;

	

	private RemoteLevelElement remoteElementHyperbolic;
	private RemoteLevelElement remoteElementHeatMap;
	private RemoteLevelElement remoteElementParCoords;

	private org.eclipse.swt.graphics.Point upperLeftScreenPos = new org.eclipse.swt.graphics.Point(
			0, 0);

	private ArrayList<AGLView> containedGLViews;
	private ArrayList<ASerializedView> newViews;
	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param sLabel
	 * @param viewFrustum
	 */
	public GLDataWindows(GLCaleydoCanvas glCanvas, final String sLabel,
			final IViewFrustum viewFrustum) {

		super(glCanvas, sLabel, viewFrustum, true);
		viewType = GLDataWindows.VIEW_ID;


		containedGLViews = new ArrayList<AGLView>();
		newViews = new ArrayList<ASerializedView>();
		
		
		
		//parentGLCanvas.addMouseListener(mouseWheelListener);

		// preparing the eyetracker
		// this.tracker = new TrackDataProvider();
		// tracker.startTracking();

		arSlerpActions = new ArrayList<NodeSlerp>();

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

		remoteElementHeatMap.getGLView().processEvents();
		remoteElementParCoords.getGLView().processEvents();
		remoteElementHyperbolic.getGLView().processEvents();

		if (bIsDisplayListDirtyLocal) {

			buildDisplayList(gl, iGLDisplayListIndexLocal);
			bIsDisplayListDirtyLocal = false;

		}
		iGLDisplayListToCall = iGLDisplayListIndexLocal;

		//pickingManager.handlePicking(this, gl);

		checkForHits(gl);
		display(gl);

		if (eBusyModeState != EBusyModeState.OFF)
			renderBusyMode(gl);

	}

	@Override
	public void displayRemote(GL gl) {
		display(gl);
	}

	@Override
	public void display(GL gl) {

		// doSlerpActions();
		renderRemoteLevelElement(gl, remoteElementHyperbolic);
		//renderRemoteLevelElement(gl, remoteElementHeatMap);
		//renderRemoteLevelElement(gl, remoteElementParCoords);


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
		

		//doSlerpActions();
		//disk.zoomTree(diskZoomIntensity);
		
		
	//	disk.renderTree(gl, textureManager, pickingManager, iUniqueID,
		//		(double) canvasWidth, (double) canvasHeight);

		//		
		
		
		
	
	
		
		
	
		//mouseWheelListener.mouseWheelMoved();
		
//		if (glMouseListener.wasLeftMouseButtonPressed()) {
//			
//
//			if (glMouseListener.getPickedPoint() != null) {
//
//				System.out.println("leftmouse");
//				if (manualPickFlag == true) {
//					Point mousePoint = new Point(0, 0);
//					mousePoint = glMouseListener.getPickedPoint();
//					int[] viewport = new int[4];
//
//					gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
//					double factorX = (double) canvasWidth
//							/ (double) viewport[2];
//					double factorY = (double) canvasHeight
//							/ (double) viewport[3];
//
//					mouseCoordX = (double) (mousePoint.getX() * factorX);
//					mouseCoordY = (double) (mousePoint.getY() * factorY);
//					disk.setCenteredNode(null);
//					PoincareNode selectedNode;
//					selectedNode = disk.processEyeTrackerAction(
//							new Point2D.Double(mouseCoordX, mouseCoordY),
//							arSlerpActions);
//					if (selectedNode != null) {
//						System.out.println("nodeSelected:"
//								+ selectedNode.iComparableValue);
//
//						// arSlerpActions.add(new nodeSlerp(4,
//						// selectedNode.getPosition(),
//						// new Point2D.Double(0, 0)));
//
//						slerpedNode = selectedNode;
//						disk.setCenteredNode(selectedNode);
//
//					}
//				}
//			}
//		}

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


//		case DATAW_NODE:
//			switch (pickingMode) {
//
//			case CLICKED:
//				
//				arSlerpActions.add(new NodeSlerp(4, disk
//						.getNodeByCompareableValue(iExternalID).getPosition(),
//						new Point2D.Double(0, 0)));
//
//				slerpedNode = disk.getNodeByCompareableValue(iExternalID);
//				disk.setCenteredNode(disk
//						.getNodeByCompareableValue(iExternalID));
//
//			}

		// case DATAW_NODE :
		// switch (pickingMode) {
		//
		// case CLICKED :
		//
		// arSlerpActions.add(new nodeSlerp(4, disk
		// .getNodeByCompareableValue(iExternalID)
		// .getPosition(), new Point2D.Double(0, 0)));
		//
		// slerpedNode = disk
		// .getNodeByCompareableValue(iExternalID);
		// disk.setCenteredNode(disk
		// .getNodeByCompareableValue(iExternalID));
		//
		// }


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

		// Heat map
		ASerializedView serView = new SerializedHeatMapView();
		serView.setDataDomain(EDataDomain.GENETIC_DATA);
		AGLView view = createView(gl, serView);
		((AStorageBasedView) view).renderContext(true);

		Transform transform = new Transform();
		transform.setTranslation(new Vec3f(5, 0, 0));
		transform.setScale(new Vec3f(0.35f, 0.35f, 1));

		remoteElementHeatMap = new RemoteLevelElement(null);
		remoteElementHeatMap.setGLView(view);
		remoteElementHeatMap.setTransform(transform);

		// Parallel coordinates
		serView = new SerializedParallelCoordinatesView();
		serView.setDataDomain(EDataDomain.GENETIC_DATA);
		view = createView(gl, serView);
		((AStorageBasedView) view).renderContext(true);

		transform = new Transform();
		transform.setTranslation(new Vec3f(5, 2, 0));
		transform.setScale(new Vec3f(0.35f, 0.35f, 1));

		remoteElementParCoords = new RemoteLevelElement(null);
		remoteElementParCoords.setGLView(view);
		remoteElementParCoords.setTransform(transform);

		// Hyperbolic view
		serView = new SerializedHyperbolicView();
		serView.setDataDomain(EDataDomain.GENETIC_DATA);
		view = createView(gl, serView);

		transform = new Transform();
		transform.setTranslation(new Vec3f(0, 0, 0));
		transform.setScale(new Vec3f(1f, 1f, 1));

		remoteElementHyperbolic = new RemoteLevelElement(null);
		remoteElementHyperbolic.setGLView(view);
		remoteElementHyperbolic.setTransform(transform);
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
		glView.setRemoteRenderingGLView(this);
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

	

	@Override
	public List<AGLView> getRemoteRenderedViews() {
		// TODO Auto-generated method stub
		// FIXME
		return new ArrayList<AGLView>();
	}
}