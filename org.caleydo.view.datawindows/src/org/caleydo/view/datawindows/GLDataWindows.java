package org.caleydo.view.datawindows;

import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import gleem.linalg.open.Transform;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.data.CmdDataCreateDataDomain;
import org.caleydo.core.command.view.opengl.CmdCreateView;
import org.caleydo.core.data.selection.EVAOperation;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IDataDomain;
import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.datadomain.IDataDomainBasedView;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
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
public class GLDataWindows extends AGLView implements IGLRemoteRenderingView,
		MouseMotionListener {

	public final static String VIEW_ID = "org.caleydo.view.datawindows";

	private ArrayList<NodeSlerp> arSlerpActions;

	private MouseWheelEvent mouse;

	private PoincareNode slerpedNode;

	private boolean manualPickFlag = true;

	private RemoteLevelElement remoteElementHyperbolic;
	private RemoteLevelElement remoteElementHeatMap;
	private RemoteLevelElement remoteElementParCoords;

	private GLHyperbolic directHyperbolicView;

	private float viewSizeHyperbolic = 1;

	private ArrayList<SimpleSlerp> simpleSlerpActions;

	private org.eclipse.swt.graphics.Point upperLeftScreenPos = new org.eclipse.swt.graphics.Point(
			0, 0);

	private ArrayList<AGLView> containedGLViews;
	private ArrayList<ASerializedView> newViews;

	private DataWindowsMouseWheelListener mouseWheelListener;

	private GLCaleydoCanvas canvas;

	private float[] layoutHotSpot;
	private float[] defaultLayoutHotSpot;
	private boolean hyperbolicViewSquared = false;

	private Point mousePoint = new Point(0, 0);

	private float[] viewSlerpTargetPoint;
	private float[] viewSlerpStartPoint;

	private eyeTracking eyeTracker;

	private int pixelWidth;
	private int pixelHeight;

	private enum viewType {
		HYPERBOLIC, PARCOORDS, HEATMAP;
	}

	private boolean layoutHotSpotInitSwitch = false;
	private boolean testZoomViewEventSwitch = false;

	private double[] viewPort;

	private enum inputType {
		MOUSE_ONLY, EYETRACKER_ONLY, EYETRACKER_SIMULATED;
	}

	private inputType selectedInput;

	/**
	 * /** Constructor.
	 * 
	 * @param glCanvas
	 * @param sLabel
	 * @param viewFrustum
	 */
	public GLDataWindows(GLCaleydoCanvas glCanvas, final String sLabel,
			final IViewFrustum viewFrustum) {

		super(glCanvas, sLabel, viewFrustum, true);
		canvas = glCanvas;
		viewType = GLDataWindows.VIEW_ID;

		containedGLViews = new ArrayList<AGLView>();
		newViews = new ArrayList<ASerializedView>();

		defaultLayoutHotSpot = new float[2];
		layoutHotSpot = new float[2];

		selectedInput = inputType.MOUSE_ONLY;

		// remoteHyperbolicPosition.setLocation(0, canvasHeight / 2 -
		// canvasHeight
		// * remoteHyperbolicScalation[1] / 2);

		if (selectedInput == inputType.EYETRACKER_ONLY
				|| selectedInput == inputType.EYETRACKER_SIMULATED) {
			eyeTracker = new eyeTracking(true, "insertIpAdressHere");
			if (selectedInput == inputType.EYETRACKER_ONLY) {
				eyeTracker.startTracking();
			}
		}

		viewSlerpStartPoint = new float[2];
		viewSlerpTargetPoint = new float[2];
		simpleSlerpActions = new ArrayList<SimpleSlerp>();

		// FIXME: maybe we have to find a better place for trigger pathway
		// loading
		CmdDataCreateDataDomain cmd = new CmdDataCreateDataDomain(
				ECommandType.CREATE_DATA_DOMAIN);
		cmd.setAttributes("org.caleydo.datadomain.pathway");
		cmd.doCommand();

		// simulatedPosition = new eyeTrackerSimulation();
		getParentGLCanvas().getParentComposite().getDisplay().asyncExec(
				new Runnable() {
					@Override
					public void run() {
						upperLeftScreenPos = getParentGLCanvas()
								.getParentComposite().toDisplay(1, 1);
					}
				});
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

		display(gl);

		pickingManager.handlePicking(this, gl);
		checkForHits(gl);

		if (eBusyModeState != EBusyModeState.OFF)
			renderBusyMode(gl);

	}

	@Override
	public void displayRemote(GL gl) {
		display(gl);
		checkForHits(gl);
	}

	@SuppressWarnings("static-access")
	@Override
	public void display(GL gl) {
		doSlerpActions();

		// Render invisible background for detecting clicks using GL selection
		// mechanism

		// gl.glPushName(pickingManager.getPickingID(iUniqueID,
		// EPickingType.BACKGROUND, 0));
		// gl.glColor4f(0, 0, 0, 0);
		// gl.glBegin(GL.GL_POLYGON);
		// gl.glVertex3f(viewFrustum.getLeft(), viewFrustum.getBottom(), 0);
		// gl.glVertex3f(viewFrustum.getRight() - viewFrustum.getLeft(),
		// viewFrustum
		// .getBottom(), 0);
		// gl.glVertex3f(viewFrustum.getRight() - viewFrustum.getLeft(),
		// viewFrustum
		// .getTop()
		// - viewFrustum.getBottom(), 0);
		// gl.glVertex3f(viewFrustum.getLeft(), viewFrustum.getTop()
		// - viewFrustum.getBottom(), 0);
		// gl.glEnd();
		// gl.glPopName();
		

		 
		float canvasWidth = viewFrustum.getWidth();
		float canvasHeight = viewFrustum.getHeight();

		if (layoutHotSpotInitSwitch == false) {
			layoutHotSpot[0] = canvasWidth / 2 + 1;
			layoutHotSpot[1] = canvasHeight / 2;
			layoutHotSpotInitSwitch = true;
		}

		defaultLayoutHotSpot[0] = canvasWidth / 2 + 1;
		defaultLayoutHotSpot[1] = canvasHeight / 2;
		// GLHelperFunctions.drawPointAt(gl, (float) layoutHotSpot[0],
		// (float) layoutHotSpot[1], 1);

		// transforming the hyperbolic view:

		// GLHelperFunctions.drawViewFrustum(gl, viewFrustum);

		Transform transform = new Transform();
		transform.setTranslation(new Vec3f((float) layoutHotSpot[0],
				(float) layoutHotSpot[1], 0));

		transform.setScale(new Vec3f(
				(float) (canvasWidth - layoutHotSpot[0]) / 8,
				(float) (canvasHeight - layoutHotSpot[1]) / 8, 1));
		remoteElementHeatMap.setTransform(transform);
		Transform transform2 = new Transform();

		// if (hyperbolicViewSquared = true) {
		// if (canvasHeight > layoutHotSpot[0]) {
		transform2.setScale(new Vec3f(1 * 0.6f,// (float) (layoutHotSpot[0])
				// / canvasWidth,
				1 * fAspectRatio,// (float) (layoutHotSpot[1]) /
				// canvasHeight,
				1));
		// transform2.setTranslation(new Vec3f(0, 0, 0));

		// } else {
		//
		// transform2
		// .setScale(new Vec3f(1*0.6f, 1*fAspectRatio, 1));
		// // transform2.setTranslation(new Vec3f((float) (layoutHotSpot
		// // [0] - canvasHeight) / 2, 0, 0));
		//
		// eyeTrackerOffset.setLocation(
		// (layoutHotSpot[0] - canvasHeight) / 2, 0);
		// }

		// } else {
		// transform2.setScale(new Vec3f((float) (layoutHotSpot[0]) / 8,
		// 1, 1));
		// }

		remoteElementHyperbolic.setTransform(transform2);
		Transform transform3 = new Transform();
		transform3.setTranslation(new Vec3f((float) layoutHotSpot[0],
				(float) 0, 0));
		transform3.setScale(new Vec3f(
				(float) (canvasWidth - layoutHotSpot[0]) / 8,
				(float) layoutHotSpot[1] / 8, 1));

		remoteElementParCoords.setTransform(transform3);

		renderRemoteLevelElement(gl, remoteElementHyperbolic);
		renderRemoteLevelElement(gl, remoteElementHeatMap);
		renderRemoteLevelElement(gl, remoteElementParCoords);

		// simulation of the eye tracker

		if (selectedInput == inputType.EYETRACKER_SIMULATED) {
			int[] mousePositionInt = new int[2];
			
			
			mousePositionInt[0] = this.glMouseListener.mousePositionForEyeTracker[0];
			mousePositionInt[1] = this.glMouseListener.mousePositionForEyeTracker[1];
			eyeTracker.cutWindowOffset(upperLeftScreenPos.x,
					upperLeftScreenPos.y);
			// position should be set on real mouseposition on the
			// screen
			 this.eyeTracker
			 .setRawEyeTrackerPosition(mousePositionInt);
			 eyeTracker.cutWindowOffset(upperLeftScreenPos.x,
						upperLeftScreenPos.y);
		}

		if (selectedInput == inputType.EYETRACKER_ONLY) {
			eyeTracker.receiveData();

			eyeTracker.cutWindowOffset(upperLeftScreenPos.x,
					upperLeftScreenPos.y);

			// eyeTracker.calculateGLCoordinates(canvasWidth, canvasHeight,
			// pixelWidth, pixelHeight);
		}

		if (selectedInput == inputType.EYETRACKER_ONLY
				|| selectedInput == inputType.EYETRACKER_SIMULATED) {
			eyeTracker.checkForFixedCoordinate();

			if (eyeTracker.getFixedCoordinate()[0] != 0
					&& eyeTracker.getFixedCoordinate()[1] != 0) {

				System.out.println("Position: "
						+ eyeTracker.getFixedCoordinate()[0] + " "
						+ eyeTracker.getFixedCoordinate()[1]);
			}

		}

		if (selectedInput == inputType.EYETRACKER_ONLY
				|| selectedInput == inputType.EYETRACKER_SIMULATED) {

			if (eyeTracker.getFixedCoordinate() != null) {
				this.evaluateUserSelection();

			}

		}

		contextMenu.render(gl, this);

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
				EPickingType.REMOTE_VIEW_SELECTION, glView.getID()));

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

		return "TODO: Data Windows Info";
	}

	@Override
	public String getDetailedInfo() {

		return "TODO: Data Windows Detail Info";
	}

	@Override
	protected void handlePickingEvents(EPickingType ePickingType,
			EPickingMode pickingMode, int iExternalID, Pick pick) {
		if (detailLevel == EDetailLevel.VERY_LOW) {
			return;
		}

		SelectionType selectionType;
		switch (ePickingType) {
		case BACKGROUND:

			switch (pickingMode) {

			case CLICKED:
				evaluateUserSelection();
				break;
			case RIGHT_CLICKED:
				break;
			}
			break;

		case REMOTE_VIEW_SELECTION:
			switch (pickingMode) {
			case MOUSE_OVER:
				// this.focusViewEvent(iExternalID, 0.75, true);
				break;
			case CLICKED:
				evaluateUserSelection();
				break;
			case RIGHT_CLICKED:

				contextMenu.setLocation(pick.getPickedPoint(),
						getParentGLCanvas().getWidth(), getParentGLCanvas()
								.getHeight());
				contextMenu.setMasterGLView(this);
				break;

			}
			break;

		}

	}

	private void evaluateUserSelection() {

		testZoomViewEventSwitch = false;
		selectedInput = inputType.EYETRACKER_SIMULATED;
		if (selectedInput == inputType.EYETRACKER_ONLY
				|| selectedInput == inputType.EYETRACKER_SIMULATED) {

			float[] translation = new float[2];
			float[] scalation = new float[2];
			translation[0] = remoteElementHyperbolic.getTransform()
					.getTranslation().x();
			translation[1] = remoteElementHyperbolic.getTransform()
					.getTranslation().y();
			scalation[0] = remoteElementHyperbolic.getTransform().getScale()
					.x();
			scalation[1] = remoteElementHyperbolic.getTransform().getScale()
					.y();
			if (translation != null && scalation != null) {

				float[] fixedCoordinate = new float[2];
				fixedCoordinate[0] = (float) this.eyeTracker
						.getFixedCoordinate()[0];
				fixedCoordinate[1] = (float) this.eyeTracker
						.getFixedCoordinate()[1];
				this.directHyperbolicView.setEyeTrackerAction(fixedCoordinate,
						translation, scalation);
			}
			// reset the fixed eyetracker coordinate:
			eyeTracker.resetFixedCoordinate();
		}

		if (selectedInput == inputType.MOUSE_ONLY) {

			if (glMouseListener.getPickedPoint() != null) {

				if (manualPickFlag == true) {
					mousePoint = glMouseListener.getPickedPoint();

					float[] mousePosition = new float[2];
					System.out.println("Mouse Position:");
					mousePosition[0] = (float) mousePoint.getX();
					mousePosition[1] = (float) mousePoint.getY();
					System.out.println("mouse position:" + mousePosition[0]
							+ " " + mousePosition[1]);

					float[] translation = new float[2];
					float[] scalation = new float[2];
					translation[0] = remoteElementHyperbolic.getTransform()
							.getTranslation().x();
					translation[1] = remoteElementHyperbolic.getTransform()
							.getTranslation().y();
					scalation[0] = remoteElementHyperbolic.getTransform()
							.getScale().x();
					scalation[1] = remoteElementHyperbolic.getTransform()
							.getScale().y();

					this.directHyperbolicView.setEyeTrackerAction(
							mousePosition, translation, scalation);

				}
			}
		}

	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedDataWindowsView serializedForm = new SerializedDataWindowsView();
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
		ASerializedView serView = new SerializedHeatMapView();// SerializedHierarchicalHeatMapView();//
		serView.setDataDomainType("org.caleydo.datadomain.genetic");
		AGLView view = createView(gl, serView);
		((AStorageBasedView) view).renderContext(true);

		remoteElementHeatMap = new RemoteLevelElement(null);
		remoteElementHeatMap.setGLView(view);

		// Parallel coordinates
		serView = new SerializedParallelCoordinatesView();
		serView.setDataDomainType("org.caleydo.datadomain.genetic");
		view = createView(gl, serView);
		((AStorageBasedView) view).renderContext(true);

		remoteElementParCoords = new RemoteLevelElement(null);
		remoteElementParCoords.setGLView(view);

		// Hyperbolic view
		serView = new SerializedHyperbolicView();
		view = createView(gl, serView);
		directHyperbolicView = (GLHyperbolic) view;

		remoteElementHyperbolic = new RemoteLevelElement(null);
		remoteElementHyperbolic.setGLView(view);

		mouseWheelListener = new DataWindowsMouseWheelListener(
				this.directHyperbolicView);

		canvas.removeMouseWheelListener(glMouseListener);

		canvas.addMouseWheelListener(mouseWheelListener);

		glMouseListener.addGLCanvas(this);

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

	@SuppressWarnings("unchecked")
	private AGLView createView(GL gl, ASerializedView serView) {

		ICommandManager cm = generalManager.getCommandManager();
		CmdCreateView cmdView = (CmdCreateView) cm
				.createCommandByType(ECommandType.CREATE_GL_VIEW);
		cmdView.setViewID(serView.getViewType());
		cmdView.setAttributesFromSerializedForm(serView);
		cmdView.doCommand();

		AGLView glView = cmdView.getCreatedObject();
		if (glView instanceof IDataDomainBasedView<?>)
			((IDataDomainBasedView<IDataDomain>) glView)
					.setDataDomain(DataDomainManager.getInstance()
							.getDataDomain(serView.getDataDomainType()));
		glView.setRemoteRenderingGLView(this);

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

	// Zooms the selected view
	// @param zoomintentsity: 0 is no zooming, 1 is max
	public void focusViewEvent(int view, double zoomIntensity, boolean inFocus) {

		String viewType = generalManager.getViewGLCanvasManager().getGLView(
				view).getViewType();

		if (viewType.equals("org.caleydo.view.hyperbolic")) {
			if (inFocus == true) {

				viewSlerpStartPoint = defaultLayoutHotSpot.clone();
				viewSlerpTargetPoint[0] = (defaultLayoutHotSpot[0] + (viewFrustum
						.getWidth() - defaultLayoutHotSpot[0])
						* (float) zoomIntensity);
				viewSlerpTargetPoint[0] = defaultLayoutHotSpot[1];
				simpleSlerpActions.add(new SimpleSlerp());

			} else {
				viewSlerpStartPoint = layoutHotSpot.clone();
				viewSlerpTargetPoint = defaultLayoutHotSpot.clone();
				simpleSlerpActions.add(new SimpleSlerp());
			}
		} else if (viewType.equals("org.caleydo.view.heatmap")) {

			if (inFocus == true) {
				viewSlerpStartPoint = defaultLayoutHotSpot.clone();

				viewSlerpTargetPoint[0] = defaultLayoutHotSpot[0]
						* (1 - (float) zoomIntensity);
				viewSlerpTargetPoint[0] = defaultLayoutHotSpot[1]
						* (1 - (float) zoomIntensity);

				simpleSlerpActions.add(new SimpleSlerp());
			} else {
				viewSlerpStartPoint = layoutHotSpot.clone();
				viewSlerpTargetPoint = defaultLayoutHotSpot.clone();
				simpleSlerpActions.add(new SimpleSlerp());
			}
		} else if (viewType.equals("org.caleydo.view.parcoords")) {
			if (inFocus == true) {
				viewSlerpStartPoint = defaultLayoutHotSpot.clone();
				viewSlerpTargetPoint[0] = defaultLayoutHotSpot[0]
						* (1 - (float) zoomIntensity);
				viewSlerpTargetPoint[0] = viewFrustum.getHeight()
						- (defaultLayoutHotSpot[1] * (1 - (float) zoomIntensity));
				simpleSlerpActions.add(new SimpleSlerp());
			} else {
				viewSlerpStartPoint = layoutHotSpot.clone();
				viewSlerpTargetPoint = defaultLayoutHotSpot.clone();
				simpleSlerpActions.add(new SimpleSlerp());
			}
		}
	}

	public void doSlerpActions() {
		if (simpleSlerpActions.isEmpty() == false) {
			SimpleSlerp singleSlerp = simpleSlerpActions.get(0);
			if (singleSlerp.doASlerp() == true) {
				moveLayoutPoint(viewSlerpTargetPoint, viewSlerpStartPoint,
						singleSlerp.state);
			} else {
				moveLayoutPoint(viewSlerpTargetPoint, viewSlerpStartPoint,
						singleSlerp.state);
				simpleSlerpActions.clear();
			}
		}
	}

	public void moveLayoutPoint(float[] targetPoint, float[] startPoint,
			float state) {

		float[] relativePosition = new float[2];
		relativePosition[0] = startPoint[0] - targetPoint[0];
		relativePosition[1] = startPoint[1] - targetPoint[1];
		float distance = relativePosition[0] * relativePosition[0]
				+ relativePosition[1] * relativePosition[1];
		distance = (float) Math.sqrt(distance);
		distance = distance * state;

		float[] vector = new float[2];

		vector[0] = targetPoint[0] - startPoint[0];
		vector[1] = targetPoint[1] - startPoint[1];
		if ((vector[0] == 0) && (vector[1] == 0)) {
			return;
		}

		float[] eVector = new float[2];

		eVector = vector.clone();
		float length = vector[0] * vector[0] + vector[1] * vector[1];
		length = (float) Math.sqrt(length);
		eVector[0] = vector[0] / length;
		eVector[1] = vector[1] / length;

		layoutHotSpot[0] = startPoint[0] + eVector[0] * distance;
		layoutHotSpot[1] = startPoint[1] + eVector[1] * distance;

	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}