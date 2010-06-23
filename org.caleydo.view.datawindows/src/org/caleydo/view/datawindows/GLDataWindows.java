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
import org.caleydo.core.view.opengl.util.GLHelperFunctions;
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

	private ArrayList<SimpleSlerp> simpleSlerpActions;

	private org.eclipse.swt.graphics.Point upperLeftScreenPos = new org.eclipse.swt.graphics.Point(
			0, 0);

	private int[] pixelDimensions;
	private int viewport[];
	private ArrayList<AGLView> containedGLViews;
	private ArrayList<ASerializedView> newViews;

	private DataWindowsMouseWheelListener mouseWheelListener;

	private GLCaleydoCanvas canvas;

	private float[] layoutHotSpot;
	private float[] defaultLayoutHotSpot;

	private Point mousePoint = new Point(0, 0);

	private float[] viewSlerpTargetPoint;
	private float[] viewSlerpStartPoint;

	private eyeTracking eyeTracker;

	private enum remoteViewType {
		HYPERBOLIC, PARCOORDS, HEATMAP;
	}

	private boolean layoutHotSpotInitSwitch = false;
	private double[] viewPort;

	private enum inputType {
		MOUSE_ONLY, EYETRACKER_ONLY, EYETRACKER_SIMULATED;
	}

	private inputType selectedInput;
	private boolean trackerZoom = true;

	private float canvasWidth;
	private float canvasHeight;

	private float[] zoomPointHyperbolic;
	private float[] zoomPointHeatmap;
	private float[] zoomPointParCoord;

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

		// change this attribute for different inputs
		selectedInput = inputType.EYETRACKER_SIMULATED;

		viewport = new int[4];
		pixelDimensions = new int[2];

		eyeTracker = new eyeTracking(true, "192.168.1.100");
		if (selectedInput == inputType.EYETRACKER_ONLY) {
			eyeTracker.startTracking();
		}

		viewSlerpStartPoint = new float[2];
		viewSlerpTargetPoint = new float[2];
		simpleSlerpActions = new ArrayList<SimpleSlerp>();

		zoomPointHyperbolic = new float[2];
		zoomPointHyperbolic[0] = 1.5f;
		zoomPointHyperbolic[1] = 1;
		zoomPointHeatmap = new float[2];
		zoomPointHeatmap[0] = 0.5f;
		zoomPointHeatmap[1] = 0.5f;
		zoomPointParCoord = new float[2];
		zoomPointParCoord[0] = 0.5f;
		zoomPointParCoord[1] = 1.5f;

		// FIXME: maybe we have to find a better place for trigger pathway
		// loading
		CmdDataCreateDataDomain cmd = new CmdDataCreateDataDomain(
				ECommandType.CREATE_DATA_DOMAIN);
		cmd.setAttributes("org.caleydo.datadomain.pathway");
		cmd.doCommand();

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

	@Override
	public void display(GL gl) {
		doSlerpActions();

		canvasWidth = viewFrustum.getWidth();
		canvasHeight = viewFrustum.getHeight();

		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
		pixelDimensions[0] = viewport[2];
		pixelDimensions[1] = viewport[3];

		if (layoutHotSpotInitSwitch == false) {
			layoutHotSpot[0] = canvasWidth / 2;
			layoutHotSpot[1] = canvasHeight / 2;
			layoutHotSpotInitSwitch = true;
		}

		defaultLayoutHotSpot[0] = canvasWidth / 2;
		defaultLayoutHotSpot[1] = canvasHeight / 2;

		// transforming the hyperbolic view:
		Transform heatmapTransform = new Transform();
		heatmapTransform.setTranslation(new Vec3f((float) layoutHotSpot[0]+0.1f,
				(float) layoutHotSpot[1], 0));

		heatmapTransform.setScale(new Vec3f(
				(float) (canvasWidth - layoutHotSpot[0]) / canvasWidth,
				(float) (canvasHeight - layoutHotSpot[1]) / canvasHeight, 1));

		remoteElementHeatMap.setTransform(heatmapTransform);

		// remoteElementHeatMap.getGLView().getViewFrustum().setBottom(0);
		// remoteElementHeatMap.getGLView().getViewFrustum().setLeft(0);
		// remoteElementHeatMap.getGLView().getViewFrustum().setRight(canvasWidth-layoutHotSpot[1]);
		// remoteElementHeatMap.getGLView().getViewFrustum().setTop(canvasHeight-layoutHotSpot[0]);
		//		
		Transform hyperbolicTransform = new Transform();
		hyperbolicTransform.setScale(new Vec3f(layoutHotSpot[0] / canvasWidth,
				1 * fAspectRatio, 1));
		hyperbolicTransform.setTranslation(new Vec3f(0, 0, 0));
		remoteElementHyperbolic.setTransform(hyperbolicTransform);

		Transform parCoordsTransform = new Transform();
		parCoordsTransform.setTranslation(new Vec3f((float) layoutHotSpot[0],
				(float) 0, 0));
		parCoordsTransform.setScale(new Vec3f(
				 (float)(canvasWidth - layoutHotSpot[0]) / 8,
				(float) layoutHotSpot[1] / 8, 1));

		remoteElementParCoords.setTransform(parCoordsTransform);
		//        
		// remoteElementParCoords.getGLView().getViewFrustum().setBottom(0);
		// remoteElementParCoords.getGLView().getViewFrustum().setLeft(0);
		// remoteElementParCoords.getGLView().getViewFrustum().setRight(canvasWidth-layoutHotSpot[0]);
		// remoteElementParCoords.getGLView().getViewFrustum().setTop(layoutHotSpot[1]);
		//		
		renderRemoteLevelElement(gl, remoteElementHyperbolic);
		renderRemoteLevelElement(gl, remoteElementHeatMap);
		renderRemoteLevelElement(gl, remoteElementParCoords);

		// simulation of the eye tracker
		if (selectedInput == inputType.EYETRACKER_SIMULATED
				|| selectedInput == inputType.MOUSE_ONLY) {
			int[] mousePositionInt = new int[2];

			mousePositionInt[0] = this.glMouseListener.mousePosition[0];
			mousePositionInt[1] = this.glMouseListener.mousePosition[1];
			
			// position should be set on real mouse position on the
			// screen
			this.eyeTracker.setRawEyeTrackerPosition(mousePositionInt);
			eyeTracker.cutWindowOffset(upperLeftScreenPos.x,
					upperLeftScreenPos.y);
			eyeTracker.checkForFixedCoordinate();
		}
		if (selectedInput == inputType.EYETRACKER_ONLY) {
			eyeTracker.receiveData();
			eyeTracker.cutWindowOffset(upperLeftScreenPos.x,
					upperLeftScreenPos.y);
			eyeTracker.checkForFixedCoordinate();
			
		}

		// if (selectedInput == inputType.EYETRACKER_ONLY
		// || selectedInput == inputType.EYETRACKER_SIMULATED) {
		if (eyeTracker.getFixedCoordinate() != null) {
			if (eyeTracker.getFixedCoordinate()[0] != 0) {
				if (eyeTracker.getFixedCoordinate()[0] < (pixelDimensions[0] * (layoutHotSpot[0] / canvasWidth))) {
					this.focusViewEvent(remoteViewType.HYPERBOLIC);
				} else {
					if ((pixelDimensions[1] - eyeTracker.getFixedCoordinate()[1]) < (pixelDimensions[1] * (layoutHotSpot[1] / canvasHeight))) {
						this.focusViewEvent(remoteViewType.PARCOORDS);
					} else {
						this.focusViewEvent(remoteViewType.HEATMAP);
					}
				}
			}
		}
		if (selectedInput == inputType.EYETRACKER_ONLY
				|| selectedInput == inputType.EYETRACKER_SIMULATED) {
			if (eyeTracker.getFixedCoordinate()[0] != 0 && eyeTracker.eyeTrackerPauseStatus==0) {	
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
		boolean foundNode = false;

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
				
				
				
				foundNode = this.directHyperbolicView
						.setEyeTrackerAction(eyeTracker.getFixedCoordinate(),
								translation, scalation,false);
			}
			// reset the fixed eyetracker coordinate:
			eyeTracker.resetFixedCoordinate();

			if (foundNode) {
				eyeTracker.pauseEyeTracker();
			}
		}

		if (selectedInput == inputType.MOUSE_ONLY) {
			if (glMouseListener.getPickedPoint() != null) {
				if (manualPickFlag == true) {
					mousePoint = glMouseListener.getPickedPoint();
					int[] mousePosition = new int[2];
					mousePosition[0] = (int) mousePoint.getX();
					mousePosition[1] = (int) mousePoint.getY();
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
							mousePosition, translation, scalation,true);
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
	public void focusViewEvent(remoteViewType view) {

		if (view == remoteViewType.HYPERBOLIC) {
			viewSlerpStartPoint = layoutHotSpot.clone();
			viewSlerpTargetPoint[0] = defaultLayoutHotSpot[0]
					* zoomPointHyperbolic[0];
			viewSlerpTargetPoint[1] = defaultLayoutHotSpot[1]
					* zoomPointHyperbolic[1];
			simpleSlerpActions.add(new SimpleSlerp());
		} else if (view == remoteViewType.HEATMAP) {

			viewSlerpStartPoint = layoutHotSpot.clone();
			viewSlerpTargetPoint[0] = defaultLayoutHotSpot[0]
					* zoomPointHeatmap[0];
			viewSlerpTargetPoint[1] = defaultLayoutHotSpot[1]
					* zoomPointHeatmap[1];
			simpleSlerpActions.add(new SimpleSlerp());
		} else if (view == remoteViewType.PARCOORDS) {
			viewSlerpStartPoint = layoutHotSpot.clone();
			viewSlerpTargetPoint[0] = defaultLayoutHotSpot[0]
					* zoomPointParCoord[0];
			viewSlerpTargetPoint[1] = defaultLayoutHotSpot[1]
					* zoomPointParCoord[1];
			simpleSlerpActions.add(new SimpleSlerp());
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