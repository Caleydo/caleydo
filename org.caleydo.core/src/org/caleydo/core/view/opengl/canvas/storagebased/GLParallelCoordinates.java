package org.caleydo.core.view.opengl.canvas.storagebased;

import static org.caleydo.core.view.opengl.canvas.storagebased.ParCoordsRenderStyle.ANGLUAR_LINE_WIDTH;
import static org.caleydo.core.view.opengl.canvas.storagebased.ParCoordsRenderStyle.ANGULAR_COLOR;
import static org.caleydo.core.view.opengl.canvas.storagebased.ParCoordsRenderStyle.ANGULAR_POLYGON_COLOR;
import static org.caleydo.core.view.opengl.canvas.storagebased.ParCoordsRenderStyle.AXIS_MARKER_WIDTH;
import static org.caleydo.core.view.opengl.canvas.storagebased.ParCoordsRenderStyle.AXIS_Z;
import static org.caleydo.core.view.opengl.canvas.storagebased.ParCoordsRenderStyle.DESELECTED_POLYLINE_LINE_WIDTH;
import static org.caleydo.core.view.opengl.canvas.storagebased.ParCoordsRenderStyle.GATE_TIP_HEIGHT;
import static org.caleydo.core.view.opengl.canvas.storagebased.ParCoordsRenderStyle.GATE_WIDTH;
import static org.caleydo.core.view.opengl.canvas.storagebased.ParCoordsRenderStyle.GATE_Z;
import static org.caleydo.core.view.opengl.canvas.storagebased.ParCoordsRenderStyle.LABEL_Z;
import static org.caleydo.core.view.opengl.canvas.storagebased.ParCoordsRenderStyle.MOUSE_OVER_POLYLINE_LINE_WIDTH;
import static org.caleydo.core.view.opengl.canvas.storagebased.ParCoordsRenderStyle.NAN_Y_OFFSET;
import static org.caleydo.core.view.opengl.canvas.storagebased.ParCoordsRenderStyle.NUMBER_AXIS_MARKERS;
import static org.caleydo.core.view.opengl.canvas.storagebased.ParCoordsRenderStyle.POLYLINE_MOUSE_OVER_COLOR;
import static org.caleydo.core.view.opengl.canvas.storagebased.ParCoordsRenderStyle.POLYLINE_NO_OCCLUSION_PREV_COLOR;
import static org.caleydo.core.view.opengl.canvas.storagebased.ParCoordsRenderStyle.POLYLINE_SELECTED_COLOR;
import static org.caleydo.core.view.opengl.canvas.storagebased.ParCoordsRenderStyle.SELECTED_POLYLINE_LINE_WIDTH;
import static org.caleydo.core.view.opengl.canvas.storagebased.ParCoordsRenderStyle.X_AXIS_COLOR;
import static org.caleydo.core.view.opengl.canvas.storagebased.ParCoordsRenderStyle.X_AXIS_LINE_WIDTH;
import static org.caleydo.core.view.opengl.canvas.storagebased.ParCoordsRenderStyle.Y_AXIS_COLOR;
import static org.caleydo.core.view.opengl.canvas.storagebased.ParCoordsRenderStyle.Y_AXIS_LINE_WIDTH;
import static org.caleydo.core.view.opengl.canvas.storagebased.ParCoordsRenderStyle.Y_AXIS_LOW;
import static org.caleydo.core.view.opengl.canvas.storagebased.ParCoordsRenderStyle.Y_AXIS_MOUSE_OVER_COLOR;
import static org.caleydo.core.view.opengl.canvas.storagebased.ParCoordsRenderStyle.Y_AXIS_MOUSE_OVER_LINE_WIDTH;
import static org.caleydo.core.view.opengl.canvas.storagebased.ParCoordsRenderStyle.Y_AXIS_SELECTED_COLOR;
import static org.caleydo.core.view.opengl.canvas.storagebased.ParCoordsRenderStyle.Y_AXIS_SELECTED_LINE_WIDTH;
import static org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle.getDecimalFormat;
import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;

import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.management.InvalidAttributeValueException;
import javax.media.opengl.GL;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.view.opengl.CmdCreateGLEventListener;
import org.caleydo.core.data.collection.INominalStorage;
import org.caleydo.core.data.collection.INumericalStorage;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.ESelectionCommandType;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.EVAOperation;
import org.caleydo.core.data.selection.GenericSelectionManager;
import org.caleydo.core.data.selection.IVirtualArray;
import org.caleydo.core.data.selection.SelectedElementRep;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.selection.delta.IVirtualArrayDelta;
import org.caleydo.core.data.selection.delta.VADeltaItem;
import org.caleydo.core.data.selection.delta.VirtualArrayDelta;
import org.caleydo.core.manager.event.view.TriggerPropagationCommandEvent;
import org.caleydo.core.manager.event.view.infoarea.InfoAreaUpdateEvent;
import org.caleydo.core.manager.event.view.remote.LoadPathwaysByGeneEvent;
import org.caleydo.core.manager.event.view.storagebased.PropagationEvent;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.event.view.storagebased.VirtualArrayUpdateEvent;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.preferences.PreferenceConstants;
import org.caleydo.core.util.wii.WiiRemote;
import org.caleydo.core.view.opengl.camera.EProjectionMode;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.remote.IGLCanvasRemoteRendering;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;
import org.caleydo.core.view.opengl.util.GLCoordinateUtils;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.container.GeneContextMenuItemContainer;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.core.view.serialize.ASerializedView;
import org.caleydo.core.view.serialize.SerializedDummyView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;

/**
 * This class is responsible for rendering the parallel coordinates
 * 
 * @author Alexander Lex (responsible for PC)
 * @author Marc Streit
 */
public class GLParallelCoordinates
	extends AStorageBasedView {

	/**
	 * Flag whether to take measures against occlusion or not
	 */
	private boolean bPreventOcclusion = true;

	// flag whether one array should be a polyline or an axis
	// protected boolean bRenderHorizontally = false;

	// Specify the current input data type for the axis and polylines
	// Is used for meta information, such as captions
	private EIDType eAxisDataType = EIDType.EXPERIMENT_INDEX;

	private EIDType ePolylineDataType = EIDType.EXPRESSION_INDEX;

	private boolean bIsDraggingActive = false;

	private EPickingType draggedObject;

	/**
	 * Hashes a gate id, which is made up of an axis id + the last three digits a gate counter (per axis) to a
	 * pair of values which make up the upper and lower gate tip
	 */
	private HashMap<Integer, Pair<Float, Float>> hashGates;
	private HashMap<Integer, ArrayList<Integer>> hashIsGateBlocking;
	/**
	 * Hashes how many gates are used on a axis
	 */
	private HashMap<Integer, Integer> hashNumberOfGatesPerAxisID;

	/**
	 * HashMap for the gates that are used to remove selections across all axes, when the set is homogeneous
	 */
	private HashMap<Integer, Pair<Float, Float>> hashMasterGates;
	private int iNumberOfMasterGates;

	/**
	 * HashMap that has flags for all the axes that have NAN
	 */
	private HashMap<Integer, Boolean> hashExcludeNAN;
	private HashMap<Integer, ArrayList<Integer>> hashIsNANBlocking;

	private ArrayList<ArrayList<Integer>> alIsAngleBlocking;

	private ArrayList<Float> alAxisSpacing;

	private int iDraggedGateNumber = 0;

	private float fXDefaultTranslation = 0;

	private float fXTranslation = 0;

	private float fYTranslation = 0;

	private float fXTargetTranslation = 0;

	private boolean bIsTranslationActive = false;

	private boolean bAngularBrushingSelectPolyline = false;
	private boolean bIsAngularBrushingActive = false;
	private boolean bIsAngularBrushingFirstTime = false;

	private boolean bIsGateDraggingFirstTime = false;
	private boolean bIsAngularDraggingActive = false;

	private boolean bWasAxisMoved = false;
	private boolean bWasAxisDraggedFirstTime = true;
	private float fAxisDraggingOffset;

	private int iMovedAxisPosition = -1;

	private float fGateTopSpacing;
	private float fGateBottomSpacing;

	private Vec3f vecAngularBrushingPoint;

	private float fDefaultAngle = (float) Math.PI / 6;

	private float fCurrentAngle = 0;

	// private boolean bIsLineSelected = false;
	private int iSelectedLineID = -1;

	private Pick linePick;

	private SelectedElementRep elementRep;

	private int iPolylineVAID = 0;
	private int iAxisVAID = 0;

	private GenericSelectionManager polylineSelectionManager;
	private GenericSelectionManager axisSelectionManager;

	protected ParCoordsRenderStyle renderStyle;

	private int iDisplayEveryNthPolyline = 1;

	EIconTextures dropTexture = EIconTextures.DROP_NORMAL;
	int iChangeDropOnAxisNumber = -1;

	GLPropagationHeatMap glSelectionHeatMap;
	boolean bShowSelectionHeatMap = false;

	private GLInfoAreaManager infoAreaManager;

	/**
	 * Constructor.
	 */
	public GLParallelCoordinates(GLCaleydoCanvas glCanvas, final String sLabel, final IViewFrustum viewFrustum) {

		super(glCanvas, sLabel, viewFrustum);
		viewType = EManagedObjectType.GL_PARALLEL_COORDINATES;

		renderStyle = new ParCoordsRenderStyle(this, viewFrustum);
		super.renderStyle = this.renderStyle;

		contentSelectionManager = new GenericSelectionManager.Builder(EIDType.EXPRESSION_INDEX).build();
		storageSelectionManager = new GenericSelectionManager.Builder(EIDType.EXPERIMENT_INDEX).build();

		alIsAngleBlocking = new ArrayList<ArrayList<Integer>>();
		alIsAngleBlocking.add(new ArrayList<Integer>());

		alAxisSpacing = new ArrayList<Float>();
		iNumberOfRandomElements =
			generalManager.getPreferenceStore().getInt(PreferenceConstants.PC_NUM_RANDOM_SAMPLING_POINT);

		// glSelectionHeatMap =
		// ((ViewManager)generalManager.getViewGLCanvasManager()).getSelectionHeatMap();
	}

	@Override
	public void initLocal(final GL gl) {
		iGLDisplayListIndexLocal = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexLocal;

		// glSelectionHeatMap.addSets(alSets);
		// glSelectionHeatMap.initRemote(gl, getID(),
		// glMouseListener,
		// remoteRenderingGLCanvas);

		createSelectionHeatMap(gl);

		infoAreaManager = new GLInfoAreaManager();
		infoAreaManager.initInfoInPlace(viewFrustum);

		init(gl);
	}

	@Override
	public void initRemote(final GL gl, final AGLEventListener glParentView,
		final GLMouseListener glMouseListener, final IGLCanvasRemoteRendering remoteRenderingGLCanvas,
		GLInfoAreaManager infoAreaManager) {

		bShowSelectionHeatMap = false;
		this.remoteRenderingGLView = remoteRenderingGLCanvas;
		this.glMouseListener = glMouseListener;
		this.infoAreaManager = infoAreaManager;

		iGLDisplayListIndexRemote = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexRemote;
		init(gl);
		// toggleRenderContext();
	}

	@Override
	public void init(final GL gl) {

		fXDefaultTranslation = renderStyle.getXSpacing();
		fYTranslation = renderStyle.getBottomSpacing();
	}

	@Override
	public synchronized void initData() {
		super.initData();

		if (glSelectionHeatMap != null)
			glSelectionHeatMap.setSet(set);
		initGates();
		resetAxisSpacing();
	}

	@Override
	public synchronized void resetView() {

	}

	@Override
	public synchronized void displayLocal(final GL gl) {

		if (set == null)
			return;

		if (bIsTranslationActive) {
			doTranslation();
		}

		pickingManager.handlePicking(this, gl);
		handleUnselection();
		if (bIsDisplayListDirtyLocal) {
			buildDisplayList(gl, iGLDisplayListIndexLocal);
			bIsDisplayListDirtyLocal = false;
		}
		iGLDisplayListToCall = iGLDisplayListIndexLocal;

		checkForHits(gl);

		display(gl);

		// infoAreaManager.renderInPlaceInfo(gl);

		if (eBusyModeState != EBusyModeState.OFF) {
			renderBusyMode(gl);
		}
	}

	@Override
	public synchronized void displayRemote(final GL gl) {

		if (set == null)
			return;

		if (bIsTranslationActive) {
			doTranslation();
		}
		handleUnselection();
		if (bIsDisplayListDirtyRemote) {
			buildDisplayList(gl, iGLDisplayListIndexRemote);
			bIsDisplayListDirtyRemote = false;
		}

		iGLDisplayListToCall = iGLDisplayListIndexRemote;

		display(gl);

		checkForHits(gl);
	}

	@Override
	public synchronized void display(final GL gl) {

		if (bShowSelectionHeatMap) {

			gl.glTranslatef(viewFrustum.getRight() - glSelectionHeatMap.getViewFrustum().getWidth(), 0,
				0.002f);

			// Render memo pad background
			IViewFrustum sHMFrustum = glSelectionHeatMap.getViewFrustum();
			sHMFrustum.setTop(viewFrustum.getTop());
			sHMFrustum.setBottom(viewFrustum.getBottom());

			gl.glColor4fv(GeneralRenderStyle.PANEL_BACKGROUN_COLOR, 0);
			gl.glLineWidth(1);
			gl.glBegin(GL.GL_POLYGON);
			gl.glVertex3f(0, 0, 0);
			gl.glVertex3f(glSelectionHeatMap.getViewFrustum().getWidth(), 0, 0);
			gl.glVertex3f(glSelectionHeatMap.getViewFrustum().getWidth(), glSelectionHeatMap.getViewFrustum()
				.getHeight(), 0);
			gl.glVertex3f(0, glSelectionHeatMap.getViewFrustum().getHeight(), 0);
			gl.glEnd();

			// gl.glColor4f(0.4f, 0.4f, 0.4f, 1);
			// gl.glLineWidth(1);
			// gl.glBegin(GL.GL_LINE_LOOP);
			// gl.glVertex3f((2 + fXCorrection) / fAspectRatio, -2, 4);
			// gl.glVertex3f((2 + fXCorrection) / fAspectRatio, 2, 4);
			// gl.glVertex3f((2 + fXCorrection) / fAspectRatio - fWidth, 2, 4);
			// gl.glVertex3f((2 + fXCorrection) / fAspectRatio - fWidth, -2, 4);
			// gl.glEnd();
			int iPickingID =
				pickingManager.getPickingID(iUniqueID, EPickingType.PCS_VIEW_SELECTION, glSelectionHeatMap
					.getID());
			gl.glPushName(iPickingID);
			glSelectionHeatMap.displayRemote(gl);

			gl.glPopName();
			gl.glTranslatef(-viewFrustum.getRight() + glSelectionHeatMap.getViewFrustum().getWidth(), 0,
				-0.002f);
		}

		// focusOnArea();
		// TODO another display list
		clipToFrustum(gl);

		gl.glTranslatef(fXDefaultTranslation + fXTranslation, fYTranslation, 0.0f);

		if (bIsDraggingActive) {
			handleGateDragging(gl);
			// if (glMouseListener.wasMouseReleased())
			// {
			// bIsDraggingActive = false;
			// }
		}

		if (bWasAxisMoved) {
			adjustAxisSpacing(gl);
			if (glMouseListener.wasMouseReleased()) {
				bWasAxisMoved = false;
			}
		}

		// checkUnselection();
		// GLHelperFunctions.drawAxis(gl);
		gl.glCallList(iGLDisplayListToCall);

		if (bIsAngularBrushingActive && iSelectedLineID != -1) {
			handleAngularBrushing(gl);
			// if(glMouseListener.wasMouseReleased())
			// bIsAngularBrushingActive = false;

		}

		gl.glTranslatef(-fXDefaultTranslation - fXTranslation, -fYTranslation, 0.0f);

		// gl.glDisable(GL.GL_STENCIL_TEST);

		if (!isRenderedRemote())
			contextMenu.render(gl, this);

	}

	private void createSelectionHeatMap(GL gl) {
		// Create selection panel
		CmdCreateGLEventListener cmdCreateGLView =
			(CmdCreateGLEventListener) generalManager.getCommandManager().createCommandByType(
				ECommandType.CREATE_GL_PROPAGATION_HEAT_MAP_3D);
		cmdCreateGLView.setAttributes(EProjectionMode.ORTHOGRAPHIC, 0, 0.8f, viewFrustum.getBottom(),
			viewFrustum.getTop(), -20, 20, null, -1);
		cmdCreateGLView.doCommand();
		glSelectionHeatMap = (GLPropagationHeatMap) cmdCreateGLView.getCreatedObject();
		glSelectionHeatMap.setRenderedRemote(true);
		glSelectionHeatMap.setSet(set);
		glSelectionHeatMap.initData();

		// FIXME: remoteRenderingGLCanvas is null, conceptual error
		glSelectionHeatMap.initRemote(gl, this, glMouseListener, remoteRenderingGLView, null);
	}

	/**
	 * Choose whether to render one array as a polyline and every entry across arrays is an axis or whether
	 * the array corresponds to an axis and every entry across arrays is a polyline
	 */
	private synchronized void renderStorageAsPolyline(boolean bRenderStorageHorizontally) {

		if (bRenderStorageHorizontally != this.bRenderStorageHorizontally) {
			if (bRenderStorageHorizontally && set.getVA(iContentVAID).size() > 100) {
				MessageBox messageBox = new MessageBox(new Shell(), SWT.OK);
				messageBox
					.setMessage("Can not show more than 100 axis - reduce polylines to less than 100 first");
				messageBox.open();
				return;
			}

			EIDType eTempType = eAxisDataType;
			eAxisDataType = ePolylineDataType;
			ePolylineDataType = eTempType;
		}

		this.bRenderStorageHorizontally = bRenderStorageHorizontally;
		// isEnabled = false;

		fXTranslation = 0;
		connectedElementRepresentationManager.clear(EIDType.EXPRESSION_INDEX);
		initContentVariables();

		resetAxisSpacing();
		initGates();

		setDisplayListDirty();
	}

	public synchronized void triggerAngularBrushing() {
		bAngularBrushingSelectPolyline = true;
		setDisplayListDirty();
	}

	@Override
	public synchronized void renderContext(boolean bRenderOnlyContext) {
		this.bRenderOnlyContext = bRenderOnlyContext;

		if (bRenderOnlyContext) {
			iContentVAID = mapVAIDs.get(EStorageBasedVAType.EXTERNAL_SELECTION);
		}
		else {
			if (!mapVAIDs.containsKey(EStorageBasedVAType.COMPLETE_SELECTION)) {
				initCompleteList();
			}

			iContentVAID = mapVAIDs.get(EStorageBasedVAType.COMPLETE_SELECTION);
		}

		contentSelectionManager.setVA(set.getVA(iContentVAID));
		initContentVariables();
		// initGates();
		clearAllSelections();

		setDisplayListDirty();
	}

	/**
	 * Choose whether to take measures against occlusion or not
	 * 
	 * @param bPreventOcclusion
	 */
	public synchronized void preventOcclusion(boolean bPreventOcclusion) {
		this.bPreventOcclusion = bPreventOcclusion;
		setDisplayListDirty();
	}

	/**
	 * Reset all selections and deselections
	 */
	@Override
	public synchronized void clearAllSelections() {

		initGates();
		contentSelectionManager.clearSelections();
		storageSelectionManager.clearSelections();

		// isEnabled = false;
		bIsAngularBrushingActive = false;

		for (ArrayList<Integer> alCurrent : alIsAngleBlocking) {
			alCurrent.clear();
		}
		for (ArrayList<Integer> alCurrent : hashIsGateBlocking.values()) {
			alCurrent.clear();
		}
		setDisplayListDirty();
		connectedElementRepresentationManager.clear(EIDType.EXPRESSION_INDEX);

		if (glSelectionHeatMap != null) {
			glSelectionHeatMap.clearAllSelections();
		}

		// triggerEvent(EMediatorType.PROPAGATION_MEDIATOR,
		// new SelectionCommandEventContainer(EIDType.REFSEQ_MRNA_INT,
		// new SelectionCommand(ESelectionCommandType.CLEAR_ALL)));
		//		
		// triggerEvent(EMediatorType.SELECTION_MEDIATOR,
		// new SelectionCommandEventContainer(EIDType.REFSEQ_MRNA_INT,
		// new SelectionCommand(ESelectionCommandType.CLEAR_ALL)));
		// triggerEvent(EMediatorType.SELECTION_MEDIATOR, new
		// SelectionCommandEventContainer(
		// EIDType.EXPERIMENT_INDEX,
		// new SelectionCommand(ESelectionCommandType.CLEAR_ALL)));

	}

	@Override
	public synchronized void broadcastElements() {

		// saveSelection();

		IVirtualArrayDelta delta = contentSelectionManager.getBroadcastVADelta();
		if (delta.size() > 20) {
			MessageBox messageBox = new MessageBox(new Shell(), SWT.OK);
			messageBox
				.setMessage("Can not show more than 20 selected elements - reduce polylines to less than 20 first");
			messageBox.open();
			return;
		}

		if (!isRenderedRemote()) {
			bShowSelectionHeatMap = true;

			SelectionCommand command = new SelectionCommand(ESelectionCommandType.RESET);
			TriggerPropagationCommandEvent event = new TriggerPropagationCommandEvent();
			event.setType(EIDType.EXPRESSION_INDEX);
			List<SelectionCommand> commands = new ArrayList<SelectionCommand>();
			commands.add(command);
			event.setSelectionCommands(commands);
			eventPublisher.triggerEvent(event);

			PropagationEvent propagationEvent = new PropagationEvent();
			propagationEvent.setVirtualArrayDelta(delta);
			eventPublisher.triggerEvent(propagationEvent);

			resetAxisSpacing();
			setDisplayListDirty();
		}
	}

	public synchronized void saveSelection() {

		// polylineSelectionManager.moveType(ESelectionType.DESELECTED,
		// ESelectionType.REMOVE);
		polylineSelectionManager.removeElements(ESelectionType.DESELECTED);
		clearAllSelections();
		setDisplayListDirty();
	}

	/**
	 * Initializes the array lists that contain the data. Must be run at program start, every time you
	 * exchange axis and polylines and every time you change storages or selections *
	 */
	@Override
	protected void initLists() {

		// TODO this needs only to be done if initLists has to be called during
		// runtime, not while initing
		// contentSelectionManager.resetSelectionManager();
		// storageSelectionManager.resetSelectionManager();

		if (bRenderOnlyContext) {
			iContentVAID = mapVAIDs.get(EStorageBasedVAType.EXTERNAL_SELECTION);
		}
		else {
			if (!mapVAIDs.containsKey(EStorageBasedVAType.COMPLETE_SELECTION)) {
				initCompleteList();
			}
			iContentVAID = mapVAIDs.get(EStorageBasedVAType.COMPLETE_SELECTION);

		}
		iStorageVAID = mapVAIDs.get(EStorageBasedVAType.STORAGE_SELECTION);

		initContentVariables();

		contentSelectionManager.setVA(set.getVA(iContentVAID));
		storageSelectionManager.setVA(set.getVA(iStorageVAID));
		// iNumberOfEntriesToRender = alContentSelection.size();

		// int iNumberOfAxis = ;

		// // this for loop executes once per polyline
		// for (int iPolyLineCount = 0; iPolyLineCount <
		// iNumberOfPolyLinesToRender; iPolyLineCount++)
		// {
		// polylineSelectionManager.initialAdd(set.getVA(iPolylineVAID).get(
		// iPolyLineCount));
		// }
		//
		// // this for loop executes one per axis
		// for (int iAxisCount = 0; iAxisCount < iNumberOfAxis; iAxisCount++)
		// {
		// axisSelectionManager.initialAdd(set.getVA(iAxisVAID).get(iAxisCount));
		// }

		initGates();
	}

	/**
	 * Build mapping between polyline/axis and storage/content for virtual arrays and selection managers
	 */
	private void initContentVariables() {
		if (bRenderStorageHorizontally) {

			iPolylineVAID = iStorageVAID;
			iAxisVAID = iContentVAID;
			polylineSelectionManager = storageSelectionManager;
			axisSelectionManager = contentSelectionManager;
		}
		else {
			iPolylineVAID = iContentVAID;
			iAxisVAID = iStorageVAID;
			polylineSelectionManager = contentSelectionManager;
			axisSelectionManager = storageSelectionManager;
		}
	}

	/**
	 * Initialize the gates. The gate heights are saved in two lists, which contain the rendering height of
	 * the gate
	 */
	private void initGates() {
		hashGates = new HashMap<Integer, Pair<Float, Float>>();
		hashNumberOfGatesPerAxisID = new HashMap<Integer, Integer>();
		hashIsGateBlocking = new HashMap<Integer, ArrayList<Integer>>();
		if (set.isSetHomogeneous()) {
			hashMasterGates = new HashMap<Integer, Pair<Float, Float>>();
			iNumberOfMasterGates = 0;
		}
		hashExcludeNAN = new HashMap<Integer, Boolean>();
		hashIsNANBlocking = new HashMap<Integer, ArrayList<Integer>>();
	}

	/**
	 * Build polyline display list. Renders coordinate system, polylines and gates, by calling the render
	 * methods
	 * 
	 * @param gl
	 *            GL context
	 * @param iGLDisplayListIndex
	 *            the index of the display list
	 */
	private void buildDisplayList(final GL gl, int iGLDisplayListIndex) {
		gl.glNewList(iGLDisplayListIndex, GL.GL_COMPILE);

		if (contentSelectionManager.getNumberOfElements() == 0) {
			gl.glTranslatef(-fXDefaultTranslation - fXTranslation, -fYTranslation, 0.0f);
			renderSymbol(gl);
			gl.glTranslatef(+fXDefaultTranslation + fXTranslation, fYTranslation, 0.0f);
		}
		else {

			renderCoordinateSystem(gl);

			// FIXME if uses z buffer fighting to avoid artfacts when tiltet
			if (detailLevel.compareTo(EDetailLevel.LOW) < 1) {
				renderPolylines(gl, ESelectionType.MOUSE_OVER);
				renderPolylines(gl, ESelectionType.SELECTION);
				// renderPolylines(gl, ESelectionType.DESELECTED);
				renderPolylines(gl, ESelectionType.NORMAL);
			}
			else {
				// renderPolylines(gl, ESelectionType.DESELECTED);
				renderPolylines(gl, ESelectionType.NORMAL);
				renderPolylines(gl, ESelectionType.MOUSE_OVER);
				renderPolylines(gl, ESelectionType.SELECTION);
			}

			renderGates(gl);

			if (set.isSetHomogeneous()) {
				renderGlobalBrush(gl);
			}

//			if (bShowSelectionHeatMap) {
//
//				gl.glTranslatef(viewFrustum.getRight() - glSelectionHeatMap.getViewFrustum().getWidth(), 0,
//					0.002f);
//				// gl.glTranslatef(1, 0, 0);
//				int iPickingID =
//					pickingManager.getPickingID(iUniqueID, EPickingType.PCS_VIEW_SELECTION,
//						glSelectionHeatMap.getID());
//				gl.glPushName(iPickingID);
//				glSelectionHeatMap.displayRemote(gl);
//
//				gl.glPopName();
//				// gl.glTranslatef(-1, 0, 0);
//				gl.glTranslatef(-viewFrustum.getRight() + glSelectionHeatMap.getViewFrustum().getWidth(), 0,
//					-0.002f);
//			}

		}

		gl.glEndList();
	}

	/**
	 * Polyline rendering method. All polylines that are contained in the polylineSelectionManager and are of
	 * the selection type specified in renderMode
	 * 
	 * @param gl
	 *            the GL context
	 * @param renderMode
	 *            the type of selection in the selection manager to render
	 */
	@SuppressWarnings("unchecked")
	private void renderPolylines(GL gl, ESelectionType renderMode) {

		Set<Integer> setDataToRender = null;
		float fZDepth = 0f;

		if (renderMode == ESelectionType.DESELECTED || renderMode == ESelectionType.NORMAL) {
			// iDisplayEveryNthPolyline = set.getVA(iContentVAID).size()
			// / iNumberOfRandomElements;
			iDisplayEveryNthPolyline =
				(polylineSelectionManager.getNumberOfElements() - polylineSelectionManager
					.getNumberOfElements(ESelectionType.DESELECTED))
					/ iNumberOfRandomElements;
			if (iDisplayEveryNthPolyline == 0) {
				iDisplayEveryNthPolyline = 1;
			}
		}

		switch (renderMode) {
			case NORMAL:
				setDataToRender = polylineSelectionManager.getElements(renderMode);

				fZDepth = ParCoordsRenderStyle.POLYLINE_NORMAL_Z;

				if (detailLevel.compareTo(EDetailLevel.LOW) < 1) {
					gl.glColor4fv(renderStyle.getPolylineDeselectedOcclusionPrevColor(setDataToRender.size()
						/ iDisplayEveryNthPolyline), 0);
					gl.glLineWidth(ParCoordsRenderStyle.DESELECTED_POLYLINE_LINE_WIDTH);

				}
				else {
					if (bPreventOcclusion) {
						gl.glColor4fv(renderStyle.getPolylineOcclusionPrevColor(setDataToRender.size()
							/ iDisplayEveryNthPolyline), 0);
					}
					else {
						gl.glColor4fv(POLYLINE_NO_OCCLUSION_PREV_COLOR, 0);
					}

					gl.glLineWidth(ParCoordsRenderStyle.POLYLINE_LINE_WIDTH);
				}
				break;
			case SELECTION:
				setDataToRender = polylineSelectionManager.getElements(renderMode);
				gl.glColor4fv(POLYLINE_SELECTED_COLOR, 0);
				gl.glLineWidth(SELECTED_POLYLINE_LINE_WIDTH);
				fZDepth = ParCoordsRenderStyle.POLYLINE_SELECTED_Z;
				break;
			case MOUSE_OVER:
				setDataToRender = polylineSelectionManager.getElements(renderMode);
				gl.glColor4fv(POLYLINE_MOUSE_OVER_COLOR, 0);
				gl.glLineWidth(MOUSE_OVER_POLYLINE_LINE_WIDTH);
				fZDepth = ParCoordsRenderStyle.POLYLINE_SELECTED_Z;
				break;
			case DESELECTED:
				fZDepth = ParCoordsRenderStyle.POLYLINE_DESELECTED_Z;
				setDataToRender = polylineSelectionManager.getElements(renderMode);
				gl.glColor4fv(renderStyle.getPolylineDeselectedOcclusionPrevColor(setDataToRender.size()
					/ iDisplayEveryNthPolyline), 0);
				gl.glLineWidth(DESELECTED_POLYLINE_LINE_WIDTH);
				break;
			default:
				setDataToRender = polylineSelectionManager.getElements(ESelectionType.NORMAL);
		}

		boolean bRenderingSelection = false;

		if ((renderMode == ESelectionType.SELECTION || renderMode == ESelectionType.MOUSE_OVER)
			&& detailLevel == EDetailLevel.HIGH) {
			bRenderingSelection = true;
		}

		Iterator<Integer> dataIterator = setDataToRender.iterator();
		// this loop executes once per polyline
		while (dataIterator.hasNext()) {
			int iPolyLineID = dataIterator.next();
			if (bUseRandomSampling
				&& (renderMode == ESelectionType.DESELECTED || renderMode == ESelectionType.NORMAL)) {
				if (iPolyLineID % iDisplayEveryNthPolyline != 0) {
					continue;
					// if(!alUseInRandomSampling.get(set.getVA(iPolylineVAID).indexOf(iPolyLineID)))
					// continue;
				}
			}
			if (renderMode != ESelectionType.DESELECTED) {
				gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.POLYLINE_SELECTION,
					iPolyLineID));
			}

			if (!bRenderingSelection) {
				gl.glBegin(GL.GL_LINE_STRIP);
			}

			IStorage currentStorage = null;

			// decide on which storage to use when array is polyline
			if (bRenderStorageHorizontally) {
				int iWhichStorage = iPolyLineID;
				// currentStorage = set.getStorageFromVA(iStorageVAID,
				// iWhichStorage);
				currentStorage = set.get(iWhichStorage);// ,
				// iIndex)iStorageVAID,
				// iWhichStorage);
			}

			float fPreviousXValue = 0;
			float fPreviousYValue = 0;
			float fCurrentXValue = 0;
			float fCurrentYValue = 0;

			// this loop executes once per axis
			for (int iVertexCount = 0; iVertexCount < set.getVA(iAxisVAID).size(); iVertexCount++) {
				int iStorageIndex = 0;

				// get the index if array as polyline
				if (bRenderStorageHorizontally) {
					iStorageIndex = set.getVA(iContentVAID).get(iVertexCount);
				}
				// get the storage and the storage index for the different cases
				else {
					currentStorage = set.getStorageFromVA(iStorageVAID, iVertexCount);
					iStorageIndex = iPolyLineID;
				}

				fCurrentXValue = alAxisSpacing.get(iVertexCount);
				fCurrentYValue = currentStorage.getFloat(EDataRepresentation.NORMALIZED, iStorageIndex);
				if (Float.isNaN(fCurrentYValue)) {
					fCurrentYValue = NAN_Y_OFFSET / renderStyle.getAxisHeight();
				}
				if (iVertexCount != 0) {
					if (bRenderingSelection) {
						gl.glBegin(GL.GL_LINES);
					}

					gl.glVertex3f(fPreviousXValue, fPreviousYValue * renderStyle.getAxisHeight(), fZDepth);
					gl.glVertex3f(fCurrentXValue, fCurrentYValue * renderStyle.getAxisHeight(), fZDepth);

					if (bRenderingSelection) {
						gl.glEnd();
					}

				}

				if (bRenderingSelection) {
					String sRawValue;
					if (currentStorage instanceof INumericalStorage) {
						sRawValue =
							getDecimalFormat().format(
								currentStorage.getFloat(EDataRepresentation.RAW, iStorageIndex));

					}
					else if (currentStorage instanceof INominalStorage) {
						sRawValue = ((INominalStorage<String>) currentStorage).getRaw(iStorageIndex);
					}
					else
						throw new IllegalStateException("Unknown Storage Type");

					renderBoxedYValues(gl, fCurrentXValue, fCurrentYValue * renderStyle.getAxisHeight(),
						sRawValue, renderMode);
				}

				fPreviousXValue = fCurrentXValue;
				fPreviousYValue = fCurrentYValue;
			}

			if (!bRenderingSelection) {
				gl.glEnd();
			}

			if (renderMode != ESelectionType.DESELECTED) {
				gl.glPopName();
			}
		}
	}

	/**
	 * Render the coordinate system of the parallel coordinates, including the axis captions and axis-specific
	 * buttons
	 * 
	 * @param gl
	 *            the gl context
	 * @param iNumberAxis
	 */
	private void renderCoordinateSystem(GL gl) {
		IVirtualArray axisVA = set.getVA(iAxisVAID);
		textRenderer.setColor(0, 0, 0, 1);

		int iNumberAxis = set.getVA(iAxisVAID).size();
		// draw X-Axis
		gl.glColor4fv(X_AXIS_COLOR, 0);
		gl.glLineWidth(X_AXIS_LINE_WIDTH);

		gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.X_AXIS_SELECTION, 1));
		gl.glBegin(GL.GL_LINES);

		gl.glVertex3f(renderStyle.getXAxisStart(), 0.0f, 0.0f);
		gl.glVertex3f(renderStyle.getXAxisEnd(), 0.0f, 0.0f);

		gl.glEnd();
		gl.glPopName();

		// draw all Y-Axis
		Set<Integer> selectedSet = axisSelectionManager.getElements(ESelectionType.SELECTION);
		Set<Integer> mouseOverSet = axisSelectionManager.getElements(ESelectionType.MOUSE_OVER);

		int iCount = 0;
		while (iCount < iNumberAxis) {
			float fXPosition = alAxisSpacing.get(iCount);
			if (selectedSet.contains(axisVA.get(iCount))) {
				gl.glColor4fv(Y_AXIS_SELECTED_COLOR, 0);
				gl.glLineWidth(Y_AXIS_SELECTED_LINE_WIDTH);
				gl.glEnable(GL.GL_LINE_STIPPLE);
				gl.glLineStipple(2, (short) 0xAAAA);
			}
			else if (mouseOverSet.contains(axisVA.get(iCount))) {
				gl.glColor4fv(Y_AXIS_MOUSE_OVER_COLOR, 0);
				gl.glLineWidth(Y_AXIS_MOUSE_OVER_LINE_WIDTH);
				gl.glEnable(GL.GL_LINE_STIPPLE);
				gl.glLineStipple(2, (short) 0xAAAA);
			}
			else {
				gl.glColor4fv(Y_AXIS_COLOR, 0);
				gl.glLineWidth(Y_AXIS_LINE_WIDTH);
			}
			gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.Y_AXIS_SELECTION, axisVA
				.get(iCount)));
			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(fXPosition, Y_AXIS_LOW, AXIS_Z);
			gl.glVertex3f(fXPosition, renderStyle.getAxisHeight(), AXIS_Z);

			// Top marker
			gl.glVertex3f(fXPosition - AXIS_MARKER_WIDTH, renderStyle.getAxisHeight(), AXIS_Z);
			gl.glVertex3f(fXPosition + AXIS_MARKER_WIDTH, renderStyle.getAxisHeight(), AXIS_Z);

			gl.glEnd();
			gl.glDisable(GL.GL_LINE_STIPPLE);

			if (detailLevel != EDetailLevel.HIGH || !renderStyle.isEnoughSpaceForText(iNumberAxis)) {
				// pop the picking id here when we don't want to include the
				// axis label
				gl.glPopName();
			}

			// NaN Button
			float fXButtonOrigin = alAxisSpacing.get(iCount);

			Texture tempTexture = iconTextureManager.getIconTexture(gl, EIconTextures.NAN);

			tempTexture.enable();
			tempTexture.bind();

			TextureCoords texCoords = tempTexture.getImageTexCoords();
			int iPickingID =
				pickingManager.getPickingID(iUniqueID, EPickingType.REMOVE_NAN, axisVA.get(iCount));
			// gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
			gl.glColor4f(1, 1, 1, 1f);
			gl.glPushName(iPickingID);

			gl.glBegin(GL.GL_POLYGON);
			gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
			gl.glVertex3f(fXButtonOrigin - 0.03f, ParCoordsRenderStyle.NAN_Y_OFFSET - 0.03f,
				ParCoordsRenderStyle.NAN_Z);
			gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
			gl.glVertex3f(fXButtonOrigin + 0.03f, ParCoordsRenderStyle.NAN_Y_OFFSET - 0.03f,
				ParCoordsRenderStyle.NAN_Z);
			gl.glTexCoord2f(texCoords.right(), texCoords.top());
			gl.glVertex3f(fXButtonOrigin + 0.03f, ParCoordsRenderStyle.NAN_Y_OFFSET + 0.03f,
				ParCoordsRenderStyle.NAN_Z);
			gl.glTexCoord2f(texCoords.left(), texCoords.top());
			gl.glVertex3f(fXButtonOrigin - 0.03f, ParCoordsRenderStyle.NAN_Y_OFFSET + 0.03f,
				ParCoordsRenderStyle.NAN_Z);
			gl.glEnd();

			gl.glPopName();

			// gl.glBlendFunc(GL.GL_ONE, GL.GL_D);
			// gl.glPopAttrib();
			tempTexture.disable();

			if (detailLevel == EDetailLevel.HIGH) {
				// markers on axis
				float fMarkerSpacing = renderStyle.getAxisHeight() / (NUMBER_AXIS_MARKERS + 1);
				for (int iInnerCount = 1; iInnerCount <= NUMBER_AXIS_MARKERS; iInnerCount++) {
					float fCurrentHeight = fMarkerSpacing * iInnerCount;
					if (iCount == 0) {
						if (set.isSetHomogeneous()) {
							float fNumber =
								(float) set.getRawForNormalized(fCurrentHeight / renderStyle.getAxisHeight());

							Rectangle2D bounds = textRenderer.getBounds(getDecimalFormat().format(fNumber));
							float fWidth =
								(float) bounds.getWidth() * renderStyle.getSmallFontScalingFactor();
							float fHeightHalf =
								(float) bounds.getHeight() * renderStyle.getSmallFontScalingFactor() / 3;

							renderNumber(getDecimalFormat().format(fNumber), fXPosition - fWidth
								- AXIS_MARKER_WIDTH, fCurrentHeight - fHeightHalf);
						}
						else {
							// TODO: storage based access
						}
					}
					gl.glColor3fv(Y_AXIS_COLOR, 0);
					gl.glBegin(GL.GL_LINES);
					gl.glVertex3f(fXPosition - AXIS_MARKER_WIDTH, fCurrentHeight, AXIS_Z);
					gl.glVertex3f(fXPosition + AXIS_MARKER_WIDTH, fCurrentHeight, AXIS_Z);
					gl.glEnd();

				}

				String sAxisLabel = null;
				switch (eAxisDataType) {
					// TODO not very generic here
					// case EXPERIMENT:
					// // Labels
					// // sAxisLabel = alDataStorages.get(iCount).getLabel();

					// Please check ALEX
					// case EXPRESSION_INDEX:
					// sAxisLabel =
					// Integer.toString(IDMappingHelper.get().getRefSeqFromStorageIndex(
					// set.getVA(iContentVAID).get(iCount)));
					// break;
					default:
						sAxisLabel = set.getStorageFromVA(iStorageVAID, iCount).getLabel();
						break;

				}
				gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
				gl.glTranslatef(fXPosition,
					renderStyle.getAxisHeight() + renderStyle.getAxisCaptionSpacing(), 0);
				gl.glRotatef(25, 0, 0, 1);
				textRenderer.begin3DRendering();
				textRenderer.draw3D(sAxisLabel, 0, 0, 0, renderStyle.getSmallFontScalingFactor());
				textRenderer.end3DRendering();
				gl.glRotatef(-25, 0, 0, 1);
				gl.glTranslatef(-fXPosition, -(renderStyle.getAxisHeight() + renderStyle
					.getAxisCaptionSpacing()), 0);

				if (set.isSetHomogeneous()) {
					// textRenderer.begin3DRendering();
					//
					// // render values on top and bottom of axis
					//
					// // top
					// String text = getDecimalFormat().format(set.getMax());
					// textRenderer.draw3D(text, fXPosition + 2 *
					// AXIS_MARKER_WIDTH, renderStyle
					// .getAxisHeight(), 0,
					// renderStyle.getSmallFontScalingFactor());
					//
					// // bottom
					// text = getDecimalFormat().format(set.getMin());
					// textRenderer.draw3D(text, fXPosition + 2 *
					// AXIS_MARKER_WIDTH, 0, 0,
					// renderStyle.getSmallFontScalingFactor());
					// textRenderer.end3DRendering();
				}
				else {
					// TODO
				}

				gl.glPopAttrib();

				// render Buttons

				iPickingID = -1;
				float fYDropOrigin = -ParCoordsRenderStyle.AXIS_BUTTONS_Y_OFFSET;

				gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA);

				// the gate add button
				float fYGateAddOrigin = renderStyle.getAxisHeight();
				iPickingID =
					pickingManager.getPickingID(iUniqueID, EPickingType.ADD_GATE, axisVA.get(iCount));
				tempTexture = iconTextureManager.getIconTexture(gl, EIconTextures.ADD_GATE);

				tempTexture.enable();
				tempTexture.bind();
				texCoords = tempTexture.getImageTexCoords();

				// gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
				gl.glColor4f(1, 1, 1, 1f);
				gl.glPushName(iPickingID);

				gl.glBegin(GL.GL_POLYGON);
				gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
				gl.glVertex3f(fXButtonOrigin - 0.03f, fYGateAddOrigin, AXIS_Z);
				gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
				gl.glVertex3f(fXButtonOrigin + 0.03f, fYGateAddOrigin, AXIS_Z);
				gl.glTexCoord2f(texCoords.right(), texCoords.top());
				gl.glVertex3f(fXButtonOrigin + 0.03f, fYGateAddOrigin + 0.12f, AXIS_Z);
				gl.glTexCoord2f(texCoords.left(), texCoords.top());
				gl.glVertex3f(fXButtonOrigin - 0.03f, fYGateAddOrigin + 0.12f, AXIS_Z);
				gl.glEnd();

				gl.glPopName();

				// gl.glBlendFunc(GL.GL_ONE, GL.GL_D);
				// gl.glPopAttrib();
				tempTexture.disable();

				if (selectedSet.contains(axisVA.get(iCount)) || mouseOverSet.contains(axisVA.get(iCount))) {
					// the mouse over drop
					if (iChangeDropOnAxisNumber == iCount) {
						tempTexture = iconTextureManager.getIconTexture(gl, dropTexture);
						if (!bWasAxisMoved) {
							dropTexture = EIconTextures.DROP_NORMAL;
						}
					}
					else {
						tempTexture = iconTextureManager.getIconTexture(gl, EIconTextures.DROP_NORMAL);
					}
					tempTexture.enable();
					tempTexture.bind();

					texCoords = tempTexture.getImageTexCoords();

					// gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
					gl.glColor4f(1, 1, 1, 1);

					gl.glBegin(GL.GL_POLYGON);
					gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
					gl.glVertex3f(fXButtonOrigin - 0.15f, fYDropOrigin - 0.3f, AXIS_Z + 0.005f);
					gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
					gl.glVertex3f(fXButtonOrigin + 0.15f, fYDropOrigin - 0.3f, AXIS_Z + 0.005f);
					gl.glTexCoord2f(texCoords.right(), texCoords.top());
					gl.glVertex3f(fXButtonOrigin + 0.15f, fYDropOrigin, AXIS_Z + 0.005f);
					gl.glTexCoord2f(texCoords.left(), texCoords.top());
					gl.glVertex3f(fXButtonOrigin - 0.15f, fYDropOrigin, AXIS_Z + 0.005f);
					gl.glEnd();

					// gl.glPopAttrib();
					tempTexture.disable();

					iPickingID = pickingManager.getPickingID(iUniqueID, EPickingType.MOVE_AXIS, iCount);
					gl.glColor4f(0, 0, 0, 0f);
					gl.glPushName(iPickingID);
					gl.glBegin(GL.GL_TRIANGLES);
					gl.glVertex3f(fXButtonOrigin, fYDropOrigin, AXIS_Z + 0.01f);
					gl.glVertex3f(fXButtonOrigin + 0.08f, fYDropOrigin - 0.3f, AXIS_Z + 0.01f);
					gl.glVertex3f(fXButtonOrigin - 0.08f, fYDropOrigin - 0.3f, AXIS_Z + 0.01f);
					gl.glEnd();
					gl.glPopName();

					iPickingID = pickingManager.getPickingID(iUniqueID, EPickingType.DUPLICATE_AXIS, iCount);
					// gl.glColor4f(0, 1, 0, 0.5f);
					gl.glPushName(iPickingID);
					gl.glBegin(GL.GL_TRIANGLES);
					gl.glVertex3f(fXButtonOrigin, fYDropOrigin, AXIS_Z + 0.01f);
					gl.glVertex3f(fXButtonOrigin - 0.08f, fYDropOrigin - 0.21f, AXIS_Z + 0.01f);
					gl.glVertex3f(fXButtonOrigin - 0.23f, fYDropOrigin - 0.21f, AXIS_Z + 0.01f);
					gl.glEnd();
					gl.glPopName();

					iPickingID = pickingManager.getPickingID(iUniqueID, EPickingType.REMOVE_AXIS, iCount);
					// gl.glColor4f(0, 0, 1, 0.5f);
					gl.glPushName(iPickingID);
					gl.glBegin(GL.GL_TRIANGLES);
					gl.glVertex3f(fXButtonOrigin, fYDropOrigin, AXIS_Z + 0.01f);
					gl.glVertex3f(fXButtonOrigin + 0.08f, fYDropOrigin - 0.21f, AXIS_Z + 0.01f);
					gl.glVertex3f(fXButtonOrigin + 0.23f, fYDropOrigin - 0.21f, AXIS_Z + 0.01f);
					gl.glEnd();
					gl.glPopName();

				}
				else {
					iPickingID = pickingManager.getPickingID(iUniqueID, EPickingType.MOVE_AXIS, iCount);
					tempTexture = iconTextureManager.getIconTexture(gl, EIconTextures.SMALL_DROP);
					tempTexture.enable();
					tempTexture.bind();

					texCoords = tempTexture.getImageTexCoords();

					gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
					gl.glColor4f(1, 1, 1, 1);
					gl.glPushName(iPickingID);

					gl.glBegin(GL.GL_POLYGON);
					gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
					gl.glVertex3f(fXButtonOrigin - 0.05f, fYDropOrigin - 0.2f, AXIS_Z);
					gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
					gl.glVertex3f(fXButtonOrigin + 0.05f, fYDropOrigin - 0.2f, AXIS_Z);
					gl.glTexCoord2f(texCoords.right(), texCoords.top());
					gl.glVertex3f(fXButtonOrigin + 0.05f, fYDropOrigin, AXIS_Z);
					gl.glTexCoord2f(texCoords.left(), texCoords.top());
					gl.glVertex3f(fXButtonOrigin - 0.05f, fYDropOrigin, AXIS_Z);
					gl.glEnd();

					gl.glPopName();
					gl.glPopAttrib();
					tempTexture.disable();

				}
				gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

				gl.glPopName();
			}
			iCount++;
		}
	}

	/**
	 * Render the symbol of the view instead of the view
	 * 
	 * @param gl
	 */
	private void renderSymbol(GL gl) {
		float fXButtonOrigin = 0.33f * renderStyle.getScaling();
		float fYButtonOrigin = 0.33f * renderStyle.getScaling();
		Texture tempTexture = iconTextureManager.getIconTexture(gl, EIconTextures.PAR_COORDS_SYMBOL);
		tempTexture.enable();
		tempTexture.bind();

		TextureCoords texCoords = tempTexture.getImageTexCoords();

		gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
		gl.glColor4f(1f, 1, 1, 1f);
		gl.glBegin(GL.GL_POLYGON);

		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(fXButtonOrigin, fYButtonOrigin, 0.02f);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(fXButtonOrigin, 2 * fYButtonOrigin, 0.02f);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(fXButtonOrigin * 2, 2 * fYButtonOrigin, 0.02f);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(fXButtonOrigin * 2, fYButtonOrigin, 0.02f);
		gl.glEnd();
		gl.glPopAttrib();
		tempTexture.disable();
	}

	/**
	 * Render the gates and update the fArGateHeights for the selection/unselection
	 * 
	 * @param gl
	 * @param iNumberAxis
	 */
	private void renderGates(GL gl) {

		if (detailLevel != EDetailLevel.HIGH)
			return;
		IVirtualArray axisVA = set.getVA(iAxisVAID);

		for (Integer iGateID : hashGates.keySet()) {
			// Gate ID / 1000 is axis ID
			int iAxisID = iGateID / 1000;
			Pair<Float, Float> gate = hashGates.get(iGateID);
			// TODO for all indices

			ArrayList<Integer> iAlAxisIndex = axisVA.indicesOf(iAxisID);
			for (int iAxisIndex : iAlAxisIndex) {
				float fCurrentPosition = alAxisSpacing.get(iAxisIndex);
				renderSingleGate(gl, gate, iAxisID, iGateID, fCurrentPosition);
			}
		}

	}

	private void renderSingleGate(GL gl, Pair<Float, Float> gate, int iAxisID, int iGateID,
		float fCurrentPosition) {
		// final float fGateWidth = renderStyle.;
		// final float fGateTipHeight = renderStyle.GATE_TIP_HEIGHT;

		Float fBottom = gate.getFirst();
		Float fTop = gate.getSecond();

		// if ((bIsGateMouseOver || bIsDraggingActive) && iGateID ==
		// iDraggedGateNumber
		// && draggedObject == EPickingType.GATE_TIP_SELECTION)
		// {
		//		
		// bIsGateMouseOver = false;
		// }
		// else
		// {
		// fArGateColor = GATE_TIP_COLOR;
		// }
		// invisible part for picking the remove button
		gl.glColor4f(1, 1, 1, 0f);
		int iPickingID = pickingManager.getPickingID(iUniqueID, EPickingType.REMOVE_GATE, iGateID);
		gl.glPushName(iPickingID);
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(fCurrentPosition + GATE_WIDTH, fTop - GATE_TIP_HEIGHT, GATE_Z);
		gl.glVertex3f(fCurrentPosition + 0.1828f - GATE_WIDTH, fTop - GATE_TIP_HEIGHT, GATE_Z);
		gl.glVertex3f(fCurrentPosition + 0.1828f - GATE_WIDTH, fTop, GATE_Z);
		gl.glVertex3f(fCurrentPosition + GATE_WIDTH, fTop, GATE_Z);
		gl.glEnd();
		gl.glPopName();

		Texture tempTexture = iconTextureManager.getIconTexture(gl, EIconTextures.GATE_TOP);
		tempTexture.enable();
		tempTexture.bind();
		TextureCoords texCoords = tempTexture.getImageTexCoords();
		gl.glColor4f(1, 1, 1, 1);
		// The tip of the gate
		gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.GATE_TIP_SELECTION, iGateID));
		gl.glBegin(GL.GL_POLYGON);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(fCurrentPosition - GATE_WIDTH, fTop - GATE_TIP_HEIGHT, GATE_Z);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(fCurrentPosition + 0.1828f - GATE_WIDTH, fTop - GATE_TIP_HEIGHT, GATE_Z);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(fCurrentPosition + 0.1828f - GATE_WIDTH, fTop, GATE_Z);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(fCurrentPosition - GATE_WIDTH, fTop, GATE_Z);
		gl.glEnd();
		tempTexture.disable();

		tempTexture = iconTextureManager.getIconTexture(gl, EIconTextures.GATE_MENUE);
		tempTexture.enable();
		tempTexture.bind();
		texCoords = tempTexture.getImageTexCoords();
		float fMenuHeight = 8 * GATE_WIDTH / 3.5f;
		gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
		gl.glBegin(GL.GL_POLYGON);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(fCurrentPosition - 7 * GATE_WIDTH, fTop, GATE_Z);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(fCurrentPosition + GATE_WIDTH, fTop, GATE_Z);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(fCurrentPosition + GATE_WIDTH, fTop + fMenuHeight, GATE_Z);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(fCurrentPosition - 7 * GATE_WIDTH, fTop + fMenuHeight, GATE_Z);
		gl.glEnd();

		textRenderer.setColor(1, 1, 1, 1);
		float fValue = (float) set.getRawForNormalized(fTop / renderStyle.getAxisHeight());
		renderNumber(getDecimalFormat().format(fValue), fCurrentPosition - 5 * GATE_WIDTH, fTop + 0.02f);

		tempTexture.disable();
		gl.glPopAttrib();
		gl.glPopName();

		// if (detailLevel == EDetailLevel.HIGH)
		// {
		// if (set.isSetHomogeneous())
		// {
		// // renderBoxedYValues(gl, fCurrentPosition, fTop,
		// // getDecimalFormat().format(
		// // set.getRawForNormalized(fTop / renderStyle.getAxisHeight())),
		// // ESelectionType.NORMAL);
		// }
		// else
		// {
		// // TODO storage based acces
		// }
		//
		// }

		tempTexture = iconTextureManager.getIconTexture(gl, EIconTextures.GATE_BODY);
		tempTexture.enable();
		tempTexture.bind();
		texCoords = tempTexture.getImageTexCoords();
		gl.glColor4f(1, 1, 1, 1);
		gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.GATE_BODY_SELECTION, iGateID));
		gl.glBegin(GL.GL_POLYGON);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(fCurrentPosition - GATE_WIDTH, fBottom + ParCoordsRenderStyle.GATE_BOTTOM_HEIGHT,
			GATE_Z);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(fCurrentPosition + GATE_WIDTH, fBottom + ParCoordsRenderStyle.GATE_BOTTOM_HEIGHT,
			GATE_Z);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(fCurrentPosition + GATE_WIDTH, fTop - GATE_TIP_HEIGHT, GATE_Z);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(fCurrentPosition - GATE_WIDTH, fTop - GATE_TIP_HEIGHT, GATE_Z);
		gl.glEnd();
		gl.glPopName();
		tempTexture.disable();

		// if ((bIsGateMouseOver || bIsDraggingActive) && iGateID ==
		// iDraggedGateNumber
		// && draggedObject == EPickingType.GATE_BOTTOM_SELECTION)
		// {
		// fArGateColor = POLYLINE_SELECTED_COLOR;
		// bIsGateMouseOver = false;
		// }
		// else
		// {
		// fArGateColor = GATE_TIP_COLOR;
		// }

		gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.GATE_BOTTOM_SELECTION, iGateID));
		tempTexture = iconTextureManager.getIconTexture(gl, EIconTextures.GATE_BOTTOM);
		tempTexture.enable();
		tempTexture.bind();
		texCoords = tempTexture.getImageTexCoords();
		gl.glColor4f(1, 1, 1, 1);
		gl.glBegin(GL.GL_POLYGON);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(fCurrentPosition - GATE_WIDTH, fBottom, GATE_Z);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(fCurrentPosition + GATE_WIDTH, fBottom, GATE_Z);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(fCurrentPosition + GATE_WIDTH, fBottom + ParCoordsRenderStyle.GATE_BOTTOM_HEIGHT,
			GATE_Z);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(fCurrentPosition - GATE_WIDTH, fBottom + ParCoordsRenderStyle.GATE_BOTTOM_HEIGHT,
			GATE_Z);
		gl.glEnd();

		tempTexture = iconTextureManager.getIconTexture(gl, EIconTextures.GATE_MENUE);
		tempTexture.enable();
		tempTexture.bind();
		texCoords = tempTexture.getImageTexCoords();
		gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
		gl.glBegin(GL.GL_POLYGON);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(fCurrentPosition - 7 * GATE_WIDTH, fBottom, GATE_Z);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(fCurrentPosition + GATE_WIDTH, fBottom, GATE_Z);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(fCurrentPosition + GATE_WIDTH, fBottom - fMenuHeight, GATE_Z);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(fCurrentPosition - 7 * GATE_WIDTH, fBottom - fMenuHeight, GATE_Z);
		gl.glEnd();

		textRenderer.setColor(1, 1, 1, 1);
		fValue = (float) set.getRawForNormalized(fBottom / renderStyle.getAxisHeight());
		renderNumber(getDecimalFormat().format(fValue), fCurrentPosition - 5 * GATE_WIDTH, fBottom
			- fMenuHeight + 0.02f);

		tempTexture.disable();

		gl.glPopName();

		// if (detailLevel == EDetailLevel.HIGH)
		// {
		// if (set.isSetHomogeneous())
		// {
		// // float fValue = (float) set.getRawForNormalized(fBottom
		// // / renderStyle.getAxisHeight());
		// // if (fValue > set.getMin())
		// // renderBoxedYValues(gl, fCurrentPosition, fBottom,
		// // getDecimalFormat()
		// // .format(fValue), ESelectionType.NORMAL);
		// }
		// else
		// {
		// // TODO storage based access
		// }
		// }
	}

	private void renderGlobalBrush(GL gl) {
		if (detailLevel != EDetailLevel.HIGH)
			return;

		gl.glColor4f(0, 0, 0, 1f);
		gl.glLineWidth(ParCoordsRenderStyle.Y_AXIS_LINE_WIDTH);
		// gl.glPushName(iPickingID);

		float fXOrigin = -0.2f;

		gl.glBegin(GL.GL_LINES);
		gl.glVertex3f(fXOrigin, 0, AXIS_Z);
		gl.glVertex3f(fXOrigin, renderStyle.getAxisHeight(), AXIS_Z);
		gl.glVertex3f(fXOrigin - AXIS_MARKER_WIDTH, 0, AXIS_Z);
		gl.glVertex3f(fXOrigin + AXIS_MARKER_WIDTH, 0, AXIS_Z);
		gl.glVertex3f(fXOrigin - AXIS_MARKER_WIDTH, renderStyle.getAxisHeight(), AXIS_Z);
		gl.glVertex3f(fXOrigin + AXIS_MARKER_WIDTH, renderStyle.getAxisHeight(), AXIS_Z);
		gl.glEnd();

		gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA);

		// the gate add button
		float fYGateAddOrigin = renderStyle.getAxisHeight();
		int iPickingID = pickingManager.getPickingID(iUniqueID, EPickingType.ADD_MASTER_GATE, 1);
		Texture tempTexture = iconTextureManager.getIconTexture(gl, EIconTextures.ADD_GATE);
		tempTexture.enable();
		tempTexture.bind();

		TextureCoords texCoords = tempTexture.getImageTexCoords();

		gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
		gl.glColor4f(1, 1, 1, 1);
		gl.glPushName(iPickingID);

		gl.glBegin(GL.GL_POLYGON);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(fXOrigin - 0.05f, fYGateAddOrigin, AXIS_Z);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(fXOrigin + 0.05f, fYGateAddOrigin, AXIS_Z);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(fXOrigin + 0.05f, fYGateAddOrigin + 0.2f, AXIS_Z);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(fXOrigin - 0.05f, fYGateAddOrigin + 0.2f, AXIS_Z);
		gl.glEnd();

		gl.glPopName();
		gl.glPopAttrib();
		tempTexture.disable();

		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

		for (Integer iGateID : hashMasterGates.keySet()) {
			Pair<Float, Float> gate = hashMasterGates.get(iGateID);
			renderSingleGate(gl, gate, -1, iGateID, fXOrigin);
		}

		// gl.glPopName();
	}

	/**
	 * Render the captions on the axis
	 * 
	 * @param gl
	 * @param fXOrigin
	 * @param fYOrigin
	 * @param renderMode
	 */
	private void renderBoxedYValues(GL gl, float fXOrigin, float fYOrigin, String sRawValue,
		ESelectionType renderMode) {

		// don't render values that are below the y axis
		if (fYOrigin < 0)
			return;

		gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
		gl.glLineWidth(Y_AXIS_LINE_WIDTH);
		gl.glColor4fv(Y_AXIS_COLOR, 0);

		Rectangle2D tempRectangle = textRenderer.getBounds(sRawValue);
		float fSmallSpacing = renderStyle.getVerySmallSpacing();
		float fBackPlaneWidth = (float) tempRectangle.getWidth() * renderStyle.getSmallFontScalingFactor();
		float fBackPlaneHeight = (float) tempRectangle.getHeight() * renderStyle.getSmallFontScalingFactor();
		float fXTextOrigin = fXOrigin + 2 * AXIS_MARKER_WIDTH;
		float fYTextOrigin = fYOrigin;

		gl.glColor4f(1f, 1f, 1f, 0.8f);
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(fXTextOrigin - fSmallSpacing, fYTextOrigin - fSmallSpacing, LABEL_Z);
		gl.glVertex3f(fXTextOrigin + fBackPlaneWidth, fYTextOrigin - fSmallSpacing, LABEL_Z);
		gl.glVertex3f(fXTextOrigin + fBackPlaneWidth, fYTextOrigin + fBackPlaneHeight, LABEL_Z);
		gl.glVertex3f(fXTextOrigin - fSmallSpacing, fYTextOrigin + fBackPlaneHeight, LABEL_Z);
		gl.glEnd();

		renderNumber(sRawValue, fXTextOrigin, fYTextOrigin);
		gl.glPopAttrib();
	}

	private void renderNumber(String sRawValue, float fXOrigin, float fYOrigin) {
		textRenderer.begin3DRendering();

		// String text = "";
		// if (Float.isNaN(fRawValue))
		// text = "NaN";
		// else
		// text = getDecimalFormat().format(fRawValue);

		textRenderer.draw3D(sRawValue, fXOrigin, fYOrigin, ParCoordsRenderStyle.TEXT_ON_LABEL_Z, renderStyle
			.getSmallFontScalingFactor());
		textRenderer.end3DRendering();
	}

	/**
	 * Renders the gates and updates their values
	 * 
	 * @param gl
	 */
	private void handleGateDragging(GL gl) {

		// bIsDisplayListDirtyLocal = true;
		// bIsDisplayListDirtyRemote = true;
		Point currentPoint = glMouseListener.getPickedPoint();

		float[] fArTargetWorldCoordinates =
			GLCoordinateUtils.convertWindowCoordinatesToWorldCoordinates(gl, currentPoint.x, currentPoint.y);

		float height = fArTargetWorldCoordinates[1];

		// todo only valid for one gate
		Pair<Float, Float> gate;
		if (iDraggedGateNumber > 999) {
			gate = hashGates.get(iDraggedGateNumber);
		}
		else {
			gate = hashMasterGates.get(iDraggedGateNumber);
		}
		if (gate == null)
			return;

		float fTop = gate.getSecond();
		float fBottom = gate.getFirst();

		if (bIsGateDraggingFirstTime) {
			fGateTopSpacing = fTop - height;
			fGateBottomSpacing = height - fBottom;
			bIsGateDraggingFirstTime = false;
		}
		float fTipUpperLimit = renderStyle.getAxisHeight();
		float fTipLowerLimit = fBottom + GATE_TIP_HEIGHT;
		float fBottomLowerLimit = 0;
		// - renderStyle.getGateTipHeight();
		float fBottomUpperLimit = fTop - GATE_TIP_HEIGHT;

		if (draggedObject == EPickingType.GATE_TIP_SELECTION) {
			gate.setSecond(height);
		}
		else if (draggedObject == EPickingType.GATE_BOTTOM_SELECTION) {
			gate.setFirst(height);
		}
		else if (draggedObject == EPickingType.GATE_BODY_SELECTION) {
			gate.setSecond(height + fGateTopSpacing);
			gate.setFirst(height - fGateBottomSpacing);

		}

		if (gate.getSecond() > fTipUpperLimit) {
			gate.setSecond(fTipUpperLimit);
		}
		if (gate.getSecond() < fTipLowerLimit) {
			gate.setSecond(fTipLowerLimit);
		}
		if (gate.getFirst() > fBottomUpperLimit) {
			gate.setFirst(fBottomUpperLimit);
		}
		if (gate.getFirst() < fBottomLowerLimit) {
			gate.setFirst(fBottomLowerLimit);
		}

		bIsDisplayListDirtyLocal = true;
		bIsDisplayListDirtyRemote = true;
		InfoAreaUpdateEvent event = new InfoAreaUpdateEvent();
		event.setSender(this);
		event.setInfo(getShortInfo());
		eventPublisher.triggerEvent(event);

		if (glMouseListener.wasMouseReleased()) {
			bIsDraggingActive = false;
		}

	}

	/**
	 * Unselect all lines that are deselected with the gates
	 * 
	 * @param iChangeDropOnAxisNumber
	 */
	// TODO revise
	private void handleGateUnselection() {

		float fCurrentValue = -1;
		for (Integer iGateID : hashGates.keySet()) {
			int iAxisID = iGateID / 1000;
			ArrayList<Integer> alCurrentGateBlocks = hashIsGateBlocking.get(iGateID);
			if (alCurrentGateBlocks == null)
				return;
			alCurrentGateBlocks.clear();
			Pair<Float, Float> gate = hashGates.get(iGateID);

			for (int iPolylineIndex : set.getVA(iPolylineVAID)) {
				if (bRenderStorageHorizontally) {
					fCurrentValue = set.get(iPolylineIndex).getFloat(EDataRepresentation.NORMALIZED, iAxisID);
				}
				else {
					fCurrentValue = set.get(iAxisID).getFloat(EDataRepresentation.NORMALIZED, iPolylineIndex);
				}

				if (Float.isNaN(fCurrentValue)) {
					continue;
				}

				if (fCurrentValue <= (gate.getSecond() - 0.0000000001f) / renderStyle.getAxisHeight()
					&& fCurrentValue >= gate.getFirst() / renderStyle.getAxisHeight()) {
					alCurrentGateBlocks.add(iPolylineIndex);
				}
			}
		}
	}

	private void handleNANUnselection() {
		float fCurrentValue = 0;
		hashIsNANBlocking.clear();
		for (Integer iAxisID : hashExcludeNAN.keySet()) {
			ArrayList<Integer> alDeselectedLines = new ArrayList<Integer>();
			for (int iPolylineIndex : set.getVA(iPolylineVAID)) {
				if (bRenderStorageHorizontally) {
					fCurrentValue = set.get(iPolylineIndex).getFloat(EDataRepresentation.NORMALIZED, iAxisID);
				}
				else {
					fCurrentValue = set.get(iAxisID).getFloat(EDataRepresentation.NORMALIZED, iPolylineIndex);
				}

				if (Float.isNaN(fCurrentValue)) {
					alDeselectedLines.add(iPolylineIndex);
				}
			}
			hashIsNANBlocking.put(iAxisID, alDeselectedLines);
		}
	}

	private void handleMasterGateUnselection() {

		float fCurrentValue = -1;
		for (Integer iGateID : hashMasterGates.keySet()) {
			ArrayList<Integer> alCurrentGateBlocks = hashIsGateBlocking.get(iGateID);
			if (alCurrentGateBlocks == null)
				return;
			alCurrentGateBlocks.clear();
			Pair<Float, Float> gate = hashMasterGates.get(iGateID);

			for (int iPolylineIndex : set.getVA(iPolylineVAID)) {
				boolean bIsBlocking = true;
				for (int iAxisIndex : set.getVA(iAxisVAID)) {
					if (bRenderStorageHorizontally) {
						fCurrentValue =
							set.get(iPolylineIndex).getFloat(EDataRepresentation.NORMALIZED, iAxisIndex);
					}
					else {
						fCurrentValue =
							set.get(iAxisIndex).getFloat(EDataRepresentation.NORMALIZED, iPolylineIndex);
					}

					if (Float.isNaN(fCurrentValue)) {
						continue;
					}

					if (fCurrentValue <= (gate.getSecond() - 0.0000000001f) / renderStyle.getAxisHeight()
						&& fCurrentValue >= gate.getFirst() / renderStyle.getAxisHeight()) {
						bIsBlocking = true;
					}
					else {
						bIsBlocking = false;
						break;
					}
				}
				if (bIsBlocking) {
					alCurrentGateBlocks.add(iPolylineIndex);
				}
			}
		}
	}

	@Override
	protected void reactOnExternalSelection(boolean scrollToSelection) {
		handleUnselection();
		resetAxisSpacing();
	}

	@Override
	protected void reactOnVAChanges(IVirtualArrayDelta delta) {
		if (delta.getIDType() == eAxisDataType) {

			IVirtualArray axisVA = set.getVA(iAxisVAID);
			for (VADeltaItem item : delta) {
				int iElement = axisVA.get(item.getIndex());
				if (item.getType() == EVAOperation.REMOVE) {
					// resetAxisSpacing();
					if (axisVA.containsElement(iElement) == 1) {
						hashGates.remove(iElement);
					}
				}
				else if (item.getType() == EVAOperation.REMOVE_ELEMENT) {
					// resetAxisSpacing();
					hashGates.remove(item.getPrimaryID());
				}
			}
		}

	}

	// TODO: revise this, not very performance friendly, especially the clearing
	// of the DESELECTED
	private void handleUnselection() {

		handleGateUnselection();
		handleNANUnselection();
		handleMasterGateUnselection();

		// HashMap<Integer, Boolean> hashDeselectedPolylines = new
		// HashMap<Integer, Boolean>();

		polylineSelectionManager.clearSelection(ESelectionType.DESELECTED);

		for (ArrayList<Integer> alCurrent : hashIsGateBlocking.values()) {
			polylineSelectionManager.addToType(ESelectionType.DESELECTED, alCurrent);
			// for (Integer iCurrent : alCurrent)
			// {
			// hashDeselectedPolylines.put(iCurrent, null);
			// }
		}

		for (ArrayList<Integer> alCurrent : alIsAngleBlocking) {
			polylineSelectionManager.addToType(ESelectionType.DESELECTED, alCurrent);
			// for (Integer iCurrent : alCurrent)
			// {
			// hashDeselectedPolylines.put(iCurrent, null);
			// }
		}

		for (ArrayList<Integer> alCurrent : hashIsNANBlocking.values()) {
			polylineSelectionManager.addToType(ESelectionType.DESELECTED, alCurrent);
			// for (Integer iCurrent : alCurrent)
			// {
			// hashDeselectedPolylines.put(iCurrent, null);
			// }
		}

		// for (int iCurrent : hashDeselectedPolylines.keySet())
		// {
		// polylineSelectionManager.addToType(ESelectionType.DESELECTED,
		// alCurrent);
		// polylineSelectionManager.addToType(ESelectionType.DESELECTED,
		// iCurrent);
		// }
	}

	@Override
	protected void handleEvents(final EPickingType ePickingType, final EPickingMode ePickingMode,
		final int iExternalID, final Pick pick) {
		if (detailLevel == EDetailLevel.VERY_LOW || bIsDraggingActive || bWasAxisMoved) {
			return;
		}

		ESelectionType eSelectionType;
		switch (ePickingType) {
			case PCS_VIEW_SELECTION:
				break;
			case POLYLINE_SELECTION:
				switch (ePickingMode) {
					case DOUBLE_CLICKED:
						LoadPathwaysByGeneEvent loadPathwaysByGeneEvent = new LoadPathwaysByGeneEvent();
						loadPathwaysByGeneEvent.setSender(this);
						loadPathwaysByGeneEvent.setGeneID(iExternalID);
						loadPathwaysByGeneEvent.setIdType(EIDType.EXPRESSION_INDEX);
						generalManager.getEventPublisher().triggerEvent(loadPathwaysByGeneEvent);
						// intentionally no break

					case CLICKED:
						eSelectionType = ESelectionType.SELECTION;
						if (bAngularBrushingSelectPolyline) {
							bAngularBrushingSelectPolyline = false;
							bIsAngularBrushingActive = true;
							iSelectedLineID = iExternalID;
							linePick = pick;
							bIsAngularBrushingFirstTime = true;
						}

						break;
					case MOUSE_OVER:
						eSelectionType = ESelectionType.MOUSE_OVER;
						break;

					case RIGHT_CLICKED:
						eSelectionType = ESelectionType.SELECTION;

						GeneContextMenuItemContainer geneContextMenuItemContainer =
							new GeneContextMenuItemContainer();
						geneContextMenuItemContainer.setStorageIndex(iExternalID);
						contextMenu.addItemContanier(geneContextMenuItemContainer);

						if (!isRenderedRemote()) {
							contextMenu.setLocation(pick.getPickedPoint(), getParentGLCanvas().getWidth(),
								getParentGLCanvas().getHeight());
							contextMenu.setMasterGLView(this);
						}
						break;

					default:
						return;

				}

				// infoAreaManager.setData(iExternalID, EIDType.EXPRESSION_INDEX, pick.getPickedPoint(),
				// pick.getDepth());

				if (polylineSelectionManager.checkStatus(eSelectionType, iExternalID)) {
					break;
				}

				connectedElementRepresentationManager.clear(ePolylineDataType);

				polylineSelectionManager.clearSelection(eSelectionType);

				// TODO: Integrate multi spotting support again
				// if (ePolylineDataType == EIDType.EXPRESSION_INDEX) {
				// // Resolve multiple spotting on chip and add all to the
				// // selection manager.
				// Integer iRefSeqID =
				// idMappingManager.getID(EMappingType.EXPRESSION_INDEX_2_REFSEQ_MRNA_INT, iExternalID);
				// if (iRefSeqID == null) {
				// pickingManager.flushHits(iUniqueID, ePickingType);
				// return;
				// }
				// int iConnectionID = generalManager.getIDManager().createID(EManagedObjectType.CONNECTION);
				// for (Object iExpressionIndex : idMappingManager.getMultiID(
				// EMappingType.REFSEQ_MRNA_INT_2_EXPRESSION_INDEX, iRefSeqID)) {
				// polylineSelectionManager.addToType(eSelectionType, (Integer) iExpressionIndex);
				// polylineSelectionManager.addConnectionID(iConnectionID, (Integer) iExpressionIndex);
				// }
				// }
				// else {
				polylineSelectionManager.addToType(eSelectionType, iExternalID);
				polylineSelectionManager.addConnectionID(generalManager.getIDManager().createID(
					EManagedObjectType.CONNECTION), iExternalID);
				// }

				if (ePolylineDataType == EIDType.EXPRESSION_INDEX && !bAngularBrushingSelectPolyline) {

					SelectionCommand command =
						new SelectionCommand(ESelectionCommandType.CLEAR, eSelectionType);
					// sendSelectionCommandEvent(EIDType.EXPRESSION_INDEX, command);

					ISelectionDelta selectionDelta = contentSelectionManager.getDelta();
					handleConnectedElementRep(selectionDelta);
					SelectionUpdateEvent event = new SelectionUpdateEvent();
					event.setSender(this);
					event.setSelectionDelta(selectionDelta);
					event.setInfo(getShortInfo());
					eventPublisher.triggerEvent(event);
				}

				setDisplayListDirty();
				break;

			case X_AXIS_SELECTION:
				break;
			case Y_AXIS_SELECTION:

				switch (ePickingMode) {
					case CLICKED:
						eSelectionType = ESelectionType.SELECTION;
						break;

					case MOUSE_OVER:
						eSelectionType = ESelectionType.MOUSE_OVER;
						break;

					default:
						return;

				}

				axisSelectionManager.clearSelection(eSelectionType);
				axisSelectionManager.addToType(eSelectionType, iExternalID);

				axisSelectionManager.addConnectionID(generalManager.getIDManager().createID(
					EManagedObjectType.CONNECTION), iExternalID);

				connectedElementRepresentationManager.clear(eAxisDataType);

				// triggerSelectionUpdate(EMediatorType.SELECTION_MEDIATOR,
				// axisSelectionManager
				// .getDelta(), null);

				SelectionCommand command = new SelectionCommand(ESelectionCommandType.CLEAR, eSelectionType);
				sendSelectionCommandEvent(eAxisDataType, command);

				ISelectionDelta selectionDelta = axisSelectionManager.getDelta();
				if (eAxisDataType == EIDType.EXPRESSION_INDEX) {
					handleConnectedElementRep(selectionDelta);
				}
				SelectionUpdateEvent event = new SelectionUpdateEvent();
				event.setSelectionDelta(selectionDelta);
				eventPublisher.triggerEvent(event);

				rePosition(iExternalID);
				setDisplayListDirty();
				break;
			case GATE_TIP_SELECTION:
				switch (ePickingMode) {
					case MOUSE_OVER:
						iDraggedGateNumber = iExternalID;
						draggedObject = EPickingType.GATE_TIP_SELECTION;
						setDisplayListDirty();
						break;
					case CLICKED:
						bIsDraggingActive = true;
						draggedObject = EPickingType.GATE_TIP_SELECTION;
						iDraggedGateNumber = iExternalID;
						break;
					// case DRAGGED:
					// bIsDraggingActive = true;
					// draggedObject = EPickingType.GATE_TIP_SELECTION;
					// iDraggedGateNumber = iExternalID;
					// break;

				}
				break;
			case GATE_BOTTOM_SELECTION:
				switch (ePickingMode) {
					case MOUSE_OVER:
						iDraggedGateNumber = iExternalID;
						draggedObject = EPickingType.GATE_BOTTOM_SELECTION;
						setDisplayListDirty();
						break;
					case CLICKED:
						bIsDraggingActive = true;
						draggedObject = EPickingType.GATE_BOTTOM_SELECTION;
						iDraggedGateNumber = iExternalID;
						break;
				}
				break;

			case GATE_BODY_SELECTION:
				switch (ePickingMode) {
					case MOUSE_OVER:
						iDraggedGateNumber = iExternalID;
						draggedObject = EPickingType.GATE_BODY_SELECTION;
						setDisplayListDirty();
						break;
					case CLICKED:
						bIsDraggingActive = true;
						bIsGateDraggingFirstTime = true;
						draggedObject = EPickingType.GATE_BODY_SELECTION;
						iDraggedGateNumber = iExternalID;
						break;
				}
				break;
			case PC_ICON_SELECTION:
				switch (ePickingMode) {
					case CLICKED:

						break;
				}
				break;
			case REMOVE_AXIS:
				switch (ePickingMode) {
					case MOUSE_OVER:
						dropTexture = EIconTextures.DROP_DELETE;
						iChangeDropOnAxisNumber = iExternalID;
						break;
					case CLICKED:
						IVirtualArray axisVA = set.getVA(iAxisVAID);
						if (axisVA.containsElement(axisVA.get(iExternalID)) == 1) {
							hashGates.remove(axisVA.get(iExternalID));
						}
						axisVA.remove(iExternalID);

						IVirtualArrayDelta vaDelta = new VirtualArrayDelta(EIDType.EXPERIMENT_INDEX);
						vaDelta.add(VADeltaItem.remove(iExternalID));
						sendVirtualArrayUpdateEvent(vaDelta);
						setDisplayListDirty();
						resetAxisSpacing();
						break;
				}
				break;
			case MOVE_AXIS:
				switch (ePickingMode) {

					case CLICKED:
						bWasAxisMoved = true;
						bWasAxisDraggedFirstTime = true;
						iMovedAxisPosition = iExternalID;
						setDisplayListDirty();
					case MOUSE_OVER:
						dropTexture = EIconTextures.DROP_MOVE;
						iChangeDropOnAxisNumber = iExternalID;
						break;
				}
				break;

			case DUPLICATE_AXIS:
				switch (ePickingMode) {
					case MOUSE_OVER:
						dropTexture = EIconTextures.DROP_DUPLICATE;
						iChangeDropOnAxisNumber = iExternalID;
						break;
					case CLICKED:
						if (iExternalID >= 0) {
							set.getVA(iAxisVAID).copy(iExternalID);
							IVirtualArrayDelta vaDelta = new VirtualArrayDelta(EIDType.EXPERIMENT_INDEX);
							vaDelta.add(VADeltaItem.copy(iExternalID));
							sendVirtualArrayUpdateEvent(vaDelta);

							setDisplayListDirty();
							// resetSelections();
							// initGates();
							resetAxisSpacing();
							break;
						}
				}
				break;
			case ADD_GATE:
				switch (ePickingMode) {
					case CLICKED:

						Integer iGateCount = hashNumberOfGatesPerAxisID.get(iExternalID);
						if (iGateCount == null) {
							iGateCount = 1;
						}
						else {
							iGateCount++;
						}
						hashNumberOfGatesPerAxisID.put(iExternalID, iGateCount);
						int iGateID = iExternalID * 1000 + iGateCount;
						hashGates.put(iGateID, new Pair<Float, Float>(0f, renderStyle.getAxisHeight() / 2f));

						hashIsGateBlocking.put(iGateID, new ArrayList<Integer>());
						setDisplayListDirty();

						break;
				}
				break;
			case ADD_MASTER_GATE:
				switch (ePickingMode) {
					case CLICKED:
						hashMasterGates.put(++iNumberOfMasterGates, new Pair<Float, Float>(0f, renderStyle
							.getAxisHeight() / 2f));
						hashIsGateBlocking.put(iNumberOfMasterGates, new ArrayList<Integer>());
						setDisplayListDirty();
						break;
				}
				break;

			case REMOVE_GATE:
				switch (ePickingMode) {
					case CLICKED:
						if (iExternalID > 999) {
							hashGates.remove(iExternalID);
							hashIsGateBlocking.remove(iExternalID);
							setDisplayListDirty();
						}
						else {
							hashMasterGates.remove(iExternalID);
							hashIsGateBlocking.remove(iExternalID);
							setDisplayListDirty();
						}
						break;
				}
				break;
			case ANGULAR_UPPER:
				switch (ePickingMode) {
					case CLICKED:
						bIsAngularDraggingActive = true;
					case DRAGGED:
						bIsAngularDraggingActive = true;
				}
				break;

			case ANGULAR_LOWER:
				switch (ePickingMode) {
					case CLICKED:
						bIsAngularDraggingActive = true;
					case DRAGGED:
						bIsAngularDraggingActive = true;
				}
				break;
			case REMOVE_NAN:
				switch (ePickingMode) {
					case CLICKED:

						if (hashExcludeNAN.containsKey(iExternalID)) {
							hashExcludeNAN.remove(iExternalID);
						}
						else {
							hashExcludeNAN.put(iExternalID, null);
						}
						setDisplayListDirty();
						break;

				}
				break;
		}
	}

	private void sendVirtualArrayUpdateEvent(IVirtualArrayDelta delta) {
		VirtualArrayUpdateEvent virtualArrayUpdateEvent = new VirtualArrayUpdateEvent();
		virtualArrayUpdateEvent.setSender(this);
		virtualArrayUpdateEvent.setVirtualArrayDelta(delta);
		virtualArrayUpdateEvent.setInfo(getShortInfo());
		eventPublisher.triggerEvent(virtualArrayUpdateEvent);
	}

	@Override
	protected ArrayList<SelectedElementRep> createElementRep(EIDType idType, int iStorageIndex)
		throws InvalidAttributeValueException {

		ArrayList<SelectedElementRep> alElementReps = new ArrayList<SelectedElementRep>();

		float fXValue = 0;
		float fYValue = 0;

		if (bRenderStorageHorizontally && idType == EIDType.EXPRESSION_INDEX || !bRenderStorageHorizontally
			&& idType == EIDType.EXPERIMENT_INDEX) {
			for (int iAxisNumber : set.getVA(iAxisVAID).indicesOf(iStorageIndex)) {

				fXValue = iAxisNumber * renderStyle.getAxisSpacing(set.getVA(iAxisVAID).size());
				fXValue = fXValue + renderStyle.getXSpacing();
				fYValue = renderStyle.getBottomSpacing();
				alElementReps.add(new SelectedElementRep(idType, iUniqueID, fXValue, fYValue, 0.0f));
			}
		}
		else {

			fXValue = renderStyle.getXSpacing() + fXTranslation;
			// get the value on the leftmost axis
			fYValue =
				set.getStorageFromVA(iStorageVAID, 0).getFloat(EDataRepresentation.NORMALIZED, iStorageIndex);

			if (Float.isNaN(fYValue)) {
				fYValue = NAN_Y_OFFSET * renderStyle.getAxisHeight() + renderStyle.getBottomSpacing();
			}
			else {
				fYValue = fYValue * renderStyle.getAxisHeight() + renderStyle.getBottomSpacing();
			}
			alElementReps.add(new SelectedElementRep(idType, iUniqueID, fXValue, fYValue, 0.0f));
		}

		return alElementReps;
	}

	@Override
	public synchronized String getShortInfo() {
		String message;
		int iNumLines =
			contentSelectionManager.getNumberOfElements(ESelectionType.NORMAL)
				+ contentSelectionManager.getNumberOfElements(ESelectionType.MOUSE_OVER)
				+ contentSelectionManager.getNumberOfElements(ESelectionType.SELECTION);
		if (iDisplayEveryNthPolyline == 1) {
			message =
				"Parallel Coordinates - " + iNumLines + " genes / " + set.getVA(iStorageVAID).size()
					+ " experiments";
		}
		else {
			message =
				"Parallel Coordinates - a sample of " + iNumLines / iDisplayEveryNthPolyline + " out of "
					+ iNumLines + " genes / \n " + set.getVA(iStorageVAID).size() + " experiments";
		}
		return message;

	}

	@Override
	public synchronized String getDetailedInfo() {
		StringBuffer sInfoText = new StringBuffer();
		sInfoText.append("<b>Type:</b> Parallel Coordinates\n");
		sInfoText.append(set.getVA(iPolylineVAID).size() + " Genes as polylines and "
			+ set.getVA(iAxisVAID).size() + " experiments as axis.\n");

		if (bRenderOnlyContext) {
			sInfoText.append("Showing only genes which occur in one of the other views in focus\n");
		}
		else {
			if (bUseRandomSampling) {
				sInfoText.append("Random sampling active, sample size: " + iNumberOfRandomElements + "\n");
			}
			else {
				sInfoText.append("Random sampling inactive\n");
			}

			if (dataFilterLevel == EDataFilterLevel.COMPLETE) {
				sInfoText.append("Showing all Genes in the dataset\n");
			}
			else if (dataFilterLevel == EDataFilterLevel.ONLY_MAPPING) {
				sInfoText.append("Showing all Genes that have a known DAVID ID mapping\n");
			}
			else if (dataFilterLevel == EDataFilterLevel.ONLY_CONTEXT) {
				sInfoText
					.append("Showing all genes that are contained in any of the KEGG or Biocarta Pathways\n");
			}
		}

		return sInfoText.toString();
	}

	/**
	 * Re-position a view centered on a element, specified by the element ID
	 * 
	 * @param iElementID
	 *            the ID of the element that should be in the center
	 */
	protected void rePosition(int iElementID) {

		// IVirtualArray virtualArray;
		// if (bRenderStorageHorizontally)
		// virtualArray = set.getVA(iContentVAID);
		// else
		// virtualArray = set.getVA(iStorageVAID);
		//
		// float fCurrentPosition =
		// virtualArray.indexOf(iElementID) * fAxisSpacing +
		// renderStyle.getXSpacing();
		//
		// float fFrustumLength = viewFrustum.getRight() -
		// viewFrustum.getLeft();
		// float fLength = (virtualArray.size() - 1) * fAxisSpacing;
		//
		// fXTargetTranslation = -(fCurrentPosition - fFrustumLength / 2);
		//
		// if (-fXTargetTranslation > fLength - fFrustumLength)
		// fXTargetTranslation = -(fLength - fFrustumLength + 2 *
		// renderStyle.getXSpacing());
		// else if (fXTargetTranslation > 0)
		// fXTargetTranslation = 0;
		// else if (-fXTargetTranslation < -fXTranslation + fFrustumLength / 2
		// - renderStyle.getXSpacing()
		// && -fXTargetTranslation > -fXTranslation - fFrustumLength / 2
		// + renderStyle.getXSpacing())
		// {
		// fXTargetTranslation = fXTranslation;
		// return;
		// }
		//
		// bIsTranslationActive = true;
	}

	// TODO
	private void doTranslation() {

		float fDelta = 0;
		if (fXTargetTranslation < fXTranslation - 0.3) {

			fDelta = -0.3f;

		}
		else if (fXTargetTranslation > fXTranslation + 0.3) {
			fDelta = 0.3f;
		}
		else {
			fDelta = fXTargetTranslation - fXTranslation;
			bIsTranslationActive = false;
		}

		if (elementRep != null) {
			ArrayList<Vec3f> alPoints = elementRep.getPoints();
			for (Vec3f currentPoint : alPoints) {
				currentPoint.setX(currentPoint.x() + fDelta);
			}
		}

		fXTranslation += fDelta;
	}

	// TODO
	private void handleAngularBrushing(final GL gl) {

		if (bIsAngularBrushingFirstTime) {
			fCurrentAngle = fDefaultAngle;
			Point currentPoint = linePick.getPickedPoint();
			float[] fArPoint =
				GLCoordinateUtils.convertWindowCoordinatesToWorldCoordinates(gl, currentPoint.x,
					currentPoint.y);
			vecAngularBrushingPoint = new Vec3f(fArPoint[0], fArPoint[1], 0.01f);
			bIsAngularBrushingFirstTime = false;

		}
		alIsAngleBlocking.get(0).clear();

		int iPosition = 0;

		for (int iCount = 0; iCount < alAxisSpacing.size() - 1; iCount++) {
			if (vecAngularBrushingPoint.x() > alAxisSpacing.get(iCount)
				&& vecAngularBrushingPoint.x() < alAxisSpacing.get(iCount + 1)) {
				iPosition = iCount;
			}
		}

		int iAxisLeftIndex;
		int iAxisRightIndex;

		iAxisLeftIndex = set.getVA(iAxisVAID).get(iPosition);
		iAxisRightIndex = set.getVA(iAxisVAID).get(iPosition + 1);

		Vec3f vecLeftPoint = new Vec3f(0, 0, 0);
		Vec3f vecRightPoint = new Vec3f(0, 0, 0);

		if (bRenderStorageHorizontally) {
			vecLeftPoint.setY(set.get(iSelectedLineID).getFloat(EDataRepresentation.NORMALIZED,
				iAxisLeftIndex)
				* renderStyle.getAxisHeight());
			vecRightPoint.setY(set.get(iSelectedLineID).getFloat(EDataRepresentation.NORMALIZED,
				iAxisRightIndex)
				* renderStyle.getAxisHeight());
		}
		else {
			vecLeftPoint.setY(set.get(iAxisLeftIndex).getFloat(EDataRepresentation.NORMALIZED,
				iSelectedLineID)
				* renderStyle.getAxisHeight());
			vecRightPoint.setY(set.get(iAxisRightIndex).getFloat(EDataRepresentation.NORMALIZED,
				iSelectedLineID)
				* renderStyle.getAxisHeight());
		}

		vecLeftPoint.setX(alAxisSpacing.get(iPosition));
		vecRightPoint.setX(alAxisSpacing.get(iPosition + 1));

		Vec3f vecDirectional = vecRightPoint.minus(vecLeftPoint);
		float fLength = vecDirectional.length();
		vecDirectional.normalize();

		Vec3f vecTriangleOrigin = vecLeftPoint.addScaled(fLength / 4, vecDirectional);

		Vec3f vecTriangleLimit = vecLeftPoint.addScaled(fLength / 4 * 3, vecDirectional);

		Rotf rotf = new Rotf();

		Vec3f vecCenterLine = vecTriangleLimit.minus(vecTriangleOrigin);
		float fLegLength = vecCenterLine.length();

		if (bIsAngularDraggingActive) {
			Point pickedPoint = glMouseListener.getPickedPoint();
			float fArPoint[] =
				GLCoordinateUtils
					.convertWindowCoordinatesToWorldCoordinates(gl, pickedPoint.x, pickedPoint.y);
			Vec3f vecPickedPoint = new Vec3f(fArPoint[0], fArPoint[1], 0.01f);
			Vec3f vecTempLine = vecPickedPoint.minus(vecTriangleOrigin);

			fCurrentAngle = getAngle(vecTempLine, vecCenterLine);

			bIsDisplayListDirtyLocal = true;
			bIsDisplayListDirtyRemote = true;
		}

		rotf.set(new Vec3f(0, 0, 1), fCurrentAngle);

		Vec3f vecUpperPoint = rotf.rotateVector(vecCenterLine);
		rotf.set(new Vec3f(0, 0, 1), -fCurrentAngle);
		Vec3f vecLowerPoint = rotf.rotateVector(vecCenterLine);

		vecUpperPoint.add(vecTriangleOrigin);
		vecLowerPoint.add(vecTriangleOrigin);

		gl.glColor4fv(ANGULAR_COLOR, 0);
		gl.glLineWidth(ANGLUAR_LINE_WIDTH);

		gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.ANGULAR_UPPER, iPosition));
		gl.glBegin(GL.GL_LINES);
		gl.glVertex3f(vecTriangleOrigin.x(), vecTriangleOrigin.y(), vecTriangleOrigin.z() + 0.02f);
		gl.glVertex3f(vecUpperPoint.x(), vecUpperPoint.y(), vecUpperPoint.z() + 0.02f);
		gl.glEnd();
		gl.glPopName();

		gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.ANGULAR_UPPER, iPosition));
		gl.glBegin(GL.GL_LINES);
		gl.glVertex3f(vecTriangleOrigin.x(), vecTriangleOrigin.y(), vecTriangleOrigin.z() + 0.02f);
		gl.glVertex3f(vecLowerPoint.x(), vecLowerPoint.y(), vecLowerPoint.z() + 0.02f);
		gl.glEnd();
		gl.glPopName();

		// draw angle polygon

		gl.glColor4fv(ANGULAR_POLYGON_COLOR, 0);
		// gl.glColor4f(1, 0, 0, 0.5f);
		gl.glBegin(GL.GL_POLYGON);
		rotf.set(new Vec3f(0, 0, 1), -fCurrentAngle / 10);
		Vec3f tempVector = vecCenterLine.copy();
		gl.glVertex3f(vecTriangleOrigin.x(), vecTriangleOrigin.y(), vecTriangleOrigin.z() + 0.02f);

		for (int iCount = 0; iCount <= 10; iCount++) {
			Vec3f vecPoint = tempVector.copy();
			vecPoint.normalize();
			vecPoint.scale(fLegLength);
			gl.glVertex3f(vecTriangleOrigin.x() + vecPoint.x(), vecTriangleOrigin.y() + vecPoint.y(),
				vecTriangleOrigin.z() + vecPoint.z() + 0.02f);
			tempVector = rotf.rotateVector(tempVector);
		}
		gl.glEnd();

		gl.glBegin(GL.GL_POLYGON);
		rotf.set(new Vec3f(0, 0, 1), fCurrentAngle / 10);
		tempVector = vecCenterLine.copy();

		gl.glVertex3f(vecTriangleOrigin.x(), vecTriangleOrigin.y(), vecTriangleOrigin.z() + 0.02f);
		for (int iCount = 0; iCount <= 10; iCount++) {
			Vec3f vecPoint = tempVector.copy();
			vecPoint.normalize();
			vecPoint.scale(fLegLength);
			gl.glVertex3f(vecTriangleOrigin.x() + vecPoint.x(), vecTriangleOrigin.y() + vecPoint.y(),
				vecTriangleOrigin.z() + vecPoint.z() + 0.02f);

			tempVector = rotf.rotateVector(tempVector);

		}

		// gl.glVertex3f(vecUpperPoint.x(), vecUpperPoint.y(), vecUpperPoint.z()
		// + 0.02f);
		gl.glEnd();

		// check selection

		for (Integer iCurrent : set.getVA(iPolylineVAID)) {
			if (bRenderStorageHorizontally) {
				vecLeftPoint.setY(set.get(iCurrent).getFloat(EDataRepresentation.NORMALIZED, iAxisLeftIndex)
					* renderStyle.getAxisHeight());
				vecRightPoint.setY(set.get(iCurrent)
					.getFloat(EDataRepresentation.NORMALIZED, iAxisRightIndex)
					* renderStyle.getAxisHeight());
			}
			else {
				vecLeftPoint.setY(set.get(iAxisLeftIndex).getFloat(EDataRepresentation.NORMALIZED, iCurrent)
					* renderStyle.getAxisHeight());
				vecRightPoint.setY(set.get(iAxisRightIndex)
					.getFloat(EDataRepresentation.NORMALIZED, iCurrent)
					* renderStyle.getAxisHeight());
			}

			vecLeftPoint.setX(alAxisSpacing.get(iPosition));
			vecRightPoint.setX(alAxisSpacing.get(iPosition + 1));

			// Vec3f vecCompareLine = vecLeftPoint.minus(vecRightPoint);
			Vec3f vecCompareLine = vecRightPoint.minus(vecLeftPoint);
			float fCompareAngle = getAngle(vecCompareLine, vecCenterLine);

			if (fCompareAngle > fCurrentAngle || fCompareAngle < -fCurrentAngle)
			// !(fCompareAngle < fAngle && fCompareAngle < -fAngle))
			{
				// contentSelectionManager.addToType(EViewInternalSelectionType
				// .DESELECTED, iCurrent);
				alIsAngleBlocking.get(0).add(iCurrent);
			}
			// else
			// {
			// // TODO combinations
			// //contentSelectionManager.addToType(EViewInternalSelectionType.
			// NORMAL, iCurrent);
			// }

		}

		if (glMouseListener.wasMouseReleased()) {
			bIsAngularDraggingActive = false;
			// bIsAngularBrushingActive = false;
		}

	}

	private float getAngle(final Vec3f vecOne, final Vec3f vecTwo) {
		Vec3f vecNewOne = vecOne.copy();
		Vec3f vecNewTwo = vecTwo.copy();

		vecNewOne.normalize();
		vecNewTwo.normalize();
		float fTmp = vecNewOne.dot(vecNewTwo);
		return (float) Math.acos(fTmp);
	}

	@Override
	public void changeOrientation(boolean defaultOrientation) {
		renderStorageAsPolyline(defaultOrientation);
	}

	@Override
	public boolean isInDefaultOrientation() {
		return bRenderStorageHorizontally;
	}

	private void adjustAxisSpacing(GL gl) {
		IVirtualArray axisVA = set.getVA(iAxisVAID);

		Point currentPoint = glMouseListener.getPickedPoint();

		float[] fArTargetWorldCoordinates =
			GLCoordinateUtils.convertWindowCoordinatesToWorldCoordinates(gl, currentPoint.x, currentPoint.y);

		float fWidth = fArTargetWorldCoordinates[0] - fXTranslation - fXDefaultTranslation;

		if (bWasAxisDraggedFirstTime) {
			// adjust from the actually clicked point to the center of the axis
			fAxisDraggingOffset = fWidth - alAxisSpacing.get(iMovedAxisPosition);
			bWasAxisDraggedFirstTime = false;
		}

		fWidth -= fAxisDraggingOffset;

		if (fWidth < renderStyle.getXAxisStart()) {
			fWidth = renderStyle.getXAxisStart();
		}
		if (fWidth > renderStyle.getXAxisEnd()) {
			fWidth = renderStyle.getXAxisEnd();
		}

		int iSwitchAxisWithThis = -1;
		for (int iCount = 0; iCount < alAxisSpacing.size(); iCount++) {
			if (iMovedAxisPosition > iCount && fWidth < alAxisSpacing.get(iCount)) {
				iSwitchAxisWithThis = iCount;
				break;
			}
			if (iMovedAxisPosition < iCount && fWidth > alAxisSpacing.get(iCount)) {
				iSwitchAxisWithThis = iCount;
			}
		}

		if (iSwitchAxisWithThis != -1) {
			axisVA.move(iMovedAxisPosition, iSwitchAxisWithThis);
			alAxisSpacing.remove(iMovedAxisPosition);
			alAxisSpacing.add(iSwitchAxisWithThis, fWidth);

			IVirtualArrayDelta vaDelta = new VirtualArrayDelta(EIDType.EXPERIMENT_INDEX);
			vaDelta.add(VADeltaItem.move(iMovedAxisPosition, iSwitchAxisWithThis));
			sendVirtualArrayUpdateEvent(vaDelta);
			iMovedAxisPosition = iSwitchAxisWithThis;
		}
		// if (iMovedAxisPosition > 0 && fWidth <
		// alAxisSpacing.get(iMovedAxisPosition - 1))
		// {
		// // switch axis to the left
		// axisVA.moveLeft(iMovedAxisPosition);
		// alAxisSpacing.remove(iMovedAxisPosition);
		// alAxisSpacing.add(iMovedAxisPosition - 1, fWidth);
		//
		// IVirtualArrayDelta vaDelta = new
		// VirtualArrayDelta(EIDType.EXPERIMENT_INDEX);
		// vaDelta.add(VADeltaItem.moveLeft(iMovedAxisPosition));
		// generalManager.getEventPublisher().triggerEvent(EMediatorType.SELECTION_MEDIATOR,
		// this, new DeltaEventContainer<IVirtualArrayDelta>(vaDelta));
		// iMovedAxisPosition--;
		// }
		// else if (iMovedAxisPosition < axisVA.size() - 1
		// && fWidth > alAxisSpacing.get(iMovedAxisPosition + 1))
		// {
		// // switch axis to the right
		// axisVA.moveRight(iMovedAxisPosition);
		// alAxisSpacing.remove(iMovedAxisPosition);
		// alAxisSpacing.add(iMovedAxisPosition + 1, fWidth);
		//
		// IVirtualArrayDelta vaDelta = new
		// VirtualArrayDelta(EIDType.EXPERIMENT_INDEX);
		// vaDelta.add(VADeltaItem.moveRight(iMovedAxisPosition));
		// generalManager.getEventPublisher().triggerEvent(EMediatorType.SELECTION_MEDIATOR,
		// this, new DeltaEventContainer<IVirtualArrayDelta>(vaDelta));
		// iMovedAxisPosition++;
		//
		// }
		else {
			alAxisSpacing.set(iMovedAxisPosition, fWidth);
		}
		setDisplayListDirty();

	}

	private void focusOnArea() {
		if (!generalManager.isWiiModeActive())
			return;

		WiiRemote wii = generalManager.getWiiRemote();

		float fXWiiPosition = wii.getCurrentSmoothHeadPosition()[0] + 1f;

		// we assume that this is far right, and -fMax is far left
		float fMaxX = 2;

		if (fXWiiPosition > fMaxX) {
			fXWiiPosition = fMaxX;
		}
		else if (fXWiiPosition < -fMaxX) {
			fXWiiPosition = -fMaxX;
		}

		// now we normalize to 0 to 1
		fXWiiPosition = (fXWiiPosition + fMaxX) / (2 * fMaxX);

		fXWiiPosition *= renderStyle.getWidthOfCoordinateSystem();
		int iAxisNumber = 0;
		for (int iCount = 0; iCount < alAxisSpacing.size() - 1; iCount++) {
			if (alAxisSpacing.get(iCount) < fXWiiPosition && alAxisSpacing.get(iCount + 1) > fXWiiPosition) {
				if (fXWiiPosition - alAxisSpacing.get(iCount) < alAxisSpacing.get(iCount) - fXWiiPosition) {
					iAxisNumber = iCount;
				}
				else {
					iAxisNumber = iCount + 1;
				}

				break;
			}
		}

		int iNumberOfAxis = set.getVA(iAxisVAID).size();

		float fOriginalAxisSpacing = renderStyle.getAxisSpacing(iNumberOfAxis);

		float fFocusAxisSpacing = 2 * fOriginalAxisSpacing;

		float fReducedSpacing =
			(renderStyle.getWidthOfCoordinateSystem() - 2 * fFocusAxisSpacing) / (iNumberOfAxis - 3);

		float fCurrentX = 0;
		alAxisSpacing.clear();
		for (int iCount = 0; iCount < iNumberOfAxis; iCount++) {
			alAxisSpacing.add(fCurrentX);
			if (iCount + 1 == iAxisNumber || iCount == iAxisNumber) {
				fCurrentX += fFocusAxisSpacing;
			}
			else {
				fCurrentX += fReducedSpacing;
			}
		}

		setDisplayListDirty();
	}

	public void resetAxisSpacing() {
		alAxisSpacing.clear();
		int iNumAxis = set.sizeVA(iAxisVAID);
		float fInitAxisSpacing = renderStyle.getAxisSpacing(iNumAxis);
		for (int iCount = 0; iCount < iNumAxis; iCount++) {
			alAxisSpacing.add(fInitAxisSpacing * iCount);
		}
		setDisplayListDirty();
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedDummyView serializedForm = new SerializedDummyView();
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

}
