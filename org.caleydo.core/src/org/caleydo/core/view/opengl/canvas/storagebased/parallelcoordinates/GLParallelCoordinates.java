package org.caleydo.core.view.opengl.canvas.storagebased.parallelcoordinates;

import static org.caleydo.core.view.opengl.canvas.storagebased.parallelcoordinates.ParCoordsRenderStyle.ANGLUAR_LINE_WIDTH;
import static org.caleydo.core.view.opengl.canvas.storagebased.parallelcoordinates.ParCoordsRenderStyle.ANGULAR_COLOR;
import static org.caleydo.core.view.opengl.canvas.storagebased.parallelcoordinates.ParCoordsRenderStyle.ANGULAR_POLYGON_COLOR;
import static org.caleydo.core.view.opengl.canvas.storagebased.parallelcoordinates.ParCoordsRenderStyle.AXIS_MARKER_WIDTH;
import static org.caleydo.core.view.opengl.canvas.storagebased.parallelcoordinates.ParCoordsRenderStyle.AXIS_Z;
import static org.caleydo.core.view.opengl.canvas.storagebased.parallelcoordinates.ParCoordsRenderStyle.DESELECTED_POLYLINE_LINE_WIDTH;
import static org.caleydo.core.view.opengl.canvas.storagebased.parallelcoordinates.ParCoordsRenderStyle.LABEL_Z;
import static org.caleydo.core.view.opengl.canvas.storagebased.parallelcoordinates.ParCoordsRenderStyle.MOUSE_OVER_POLYLINE_LINE_WIDTH;
import static org.caleydo.core.view.opengl.canvas.storagebased.parallelcoordinates.ParCoordsRenderStyle.NAN_Y_OFFSET;
import static org.caleydo.core.view.opengl.canvas.storagebased.parallelcoordinates.ParCoordsRenderStyle.NUMBER_AXIS_MARKERS;
import static org.caleydo.core.view.opengl.canvas.storagebased.parallelcoordinates.ParCoordsRenderStyle.POLYLINE_MOUSE_OVER_COLOR;
import static org.caleydo.core.view.opengl.canvas.storagebased.parallelcoordinates.ParCoordsRenderStyle.POLYLINE_NO_OCCLUSION_PREV_COLOR;
import static org.caleydo.core.view.opengl.canvas.storagebased.parallelcoordinates.ParCoordsRenderStyle.POLYLINE_SELECTED_COLOR;
import static org.caleydo.core.view.opengl.canvas.storagebased.parallelcoordinates.ParCoordsRenderStyle.SELECTED_POLYLINE_LINE_WIDTH;
import static org.caleydo.core.view.opengl.canvas.storagebased.parallelcoordinates.ParCoordsRenderStyle.X_AXIS_COLOR;
import static org.caleydo.core.view.opengl.canvas.storagebased.parallelcoordinates.ParCoordsRenderStyle.X_AXIS_LINE_WIDTH;
import static org.caleydo.core.view.opengl.canvas.storagebased.parallelcoordinates.ParCoordsRenderStyle.Y_AXIS_COLOR;
import static org.caleydo.core.view.opengl.canvas.storagebased.parallelcoordinates.ParCoordsRenderStyle.Y_AXIS_LINE_WIDTH;
import static org.caleydo.core.view.opengl.canvas.storagebased.parallelcoordinates.ParCoordsRenderStyle.Y_AXIS_LOW;
import static org.caleydo.core.view.opengl.canvas.storagebased.parallelcoordinates.ParCoordsRenderStyle.Y_AXIS_MOUSE_OVER_COLOR;
import static org.caleydo.core.view.opengl.canvas.storagebased.parallelcoordinates.ParCoordsRenderStyle.Y_AXIS_MOUSE_OVER_LINE_WIDTH;
import static org.caleydo.core.view.opengl.canvas.storagebased.parallelcoordinates.ParCoordsRenderStyle.Y_AXIS_SELECTED_COLOR;
import static org.caleydo.core.view.opengl.canvas.storagebased.parallelcoordinates.ParCoordsRenderStyle.Y_AXIS_SELECTED_LINE_WIDTH;
import static org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle.getDecimalFormat;
import gleem.linalg.Rotf;
import gleem.linalg.Vec2f;
import gleem.linalg.Vec3f;

import java.awt.Point;
import java.awt.Rectangle;
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
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.mapping.EIDCategory;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.ESelectionCommandType;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.EVAOperation;
import org.caleydo.core.data.selection.EVAType;
import org.caleydo.core.data.selection.IVirtualArray;
import org.caleydo.core.data.selection.SelectedElementRep;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.VirtualArray;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.selection.delta.IVirtualArrayDelta;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.selection.delta.VADeltaItem;
import org.caleydo.core.data.selection.delta.VirtualArrayDelta;
import org.caleydo.core.manager.event.data.BookmarkEvent;
import org.caleydo.core.manager.event.data.ReplaceVirtualArrayInUseCaseEvent;
import org.caleydo.core.manager.event.view.ResetAllViewsEvent;
import org.caleydo.core.manager.event.view.infoarea.InfoAreaUpdateEvent;
import org.caleydo.core.manager.event.view.storagebased.AngularBrushingEvent;
import org.caleydo.core.manager.event.view.storagebased.ApplyCurrentSelectionToVirtualArrayEvent;
import org.caleydo.core.manager.event.view.storagebased.BookmarkButtonEvent;
import org.caleydo.core.manager.event.view.storagebased.ChangeOrientationParallelCoordinatesEvent;
import org.caleydo.core.manager.event.view.storagebased.PreventOcclusionEvent;
import org.caleydo.core.manager.event.view.storagebased.ResetAxisSpacingEvent;
import org.caleydo.core.manager.event.view.storagebased.ResetParallelCoordinatesEvent;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.event.view.storagebased.UpdateViewEvent;
import org.caleydo.core.manager.event.view.storagebased.UseRandomSamplingEvent;
import org.caleydo.core.manager.event.view.storagebased.VirtualArrayUpdateEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.manager.usecase.EDataDomain;
import org.caleydo.core.manager.usecase.EDataFilterLevel;
import org.caleydo.core.manager.view.ConnectedElementRepresentationManager;
import org.caleydo.core.manager.view.StandardTransformer;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.preferences.PreferenceConstants;
import org.caleydo.core.view.opengl.camera.EProjectionMode;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.bookmarking.GLBookmarkManager;
import org.caleydo.core.view.opengl.canvas.listener.ResetViewListener;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.canvas.storagebased.AStorageBasedView;
import org.caleydo.core.view.opengl.canvas.storagebased.parallelcoordinates.listener.AngularBrushingListener;
import org.caleydo.core.view.opengl.canvas.storagebased.parallelcoordinates.listener.ApplyCurrentSelectionToVirtualArrayListener;
import org.caleydo.core.view.opengl.canvas.storagebased.parallelcoordinates.listener.BookmarkButtonListener;
import org.caleydo.core.view.opengl.canvas.storagebased.parallelcoordinates.listener.ChangeOrientationListener;
import org.caleydo.core.view.opengl.canvas.storagebased.parallelcoordinates.listener.PreventOcclusionListener;
import org.caleydo.core.view.opengl.canvas.storagebased.parallelcoordinates.listener.ResetAxisSpacingListener;
import org.caleydo.core.view.opengl.canvas.storagebased.parallelcoordinates.listener.UseRandomSamplingListener;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;
import org.caleydo.core.view.opengl.util.GLCoordinateUtils;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.container.ExperimentContextMenuItemContainer;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.container.GeneContextMenuItemContainer;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.eclipse.jface.dialogs.MessageDialog;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;

/**
 * This class is responsible for rendering the parallel coordinates
 * 
 * @author Alexander Lex (responsible for PC)
 * @author Marc Streit
 */
public class GLParallelCoordinates
	extends AStorageBasedView
	implements IGLRemoteRenderingView {

	/**
	 * Flag whether to take measures against occlusion or not
	 */
	private boolean bPreventOcclusion = true;

	// flag whether one array should be a polyline or an axis
	// protected boolean bRenderHorizontally = false;

	// Specify the current input data type for the axis and polylines
	// Is used for meta information, such as captions
	private EIDType eAxisDataType;

	private EIDType ePolylineDataType;

	private boolean bIsDraggingActive = false;

	private EPickingType draggedObject;

	/**
	 * Hashes a gate id, which is made up of an axis id + the last three digits a gate counter (per axis) to a
	 * pair of values which make up the upper and lower gate tip
	 */
	// private HashMap<Integer, Pair<Float, Float>> hashGates;
	private HashMap<Integer, AGate> hashGates;
	private HashMap<Integer, ArrayList<Integer>> hashIsGateBlocking;
	/**
	 * Hashes how many gates are used on a axis
	 */
	private HashMap<Integer, Integer> hashNumberOfGatesPerAxisID;

	/**
	 * HashMap for the gates that are used to remove selections across all axes, when the set is homogeneous
	 */
	private HashMap<Integer, Gate> hashMasterGates;
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

	private Vec3f vecAngularBrushingPoint;

	private float fDefaultAngle = (float) Math.PI / 6;

	private float fCurrentAngle = 0;

	// private boolean bIsLineSelected = false;
	private int iSelectedLineID = -1;

	private Pick linePick;

	private SelectedElementRep elementRep;

	// private int iPolylineVAID = 0;
	/**
	 * The virtual array that is currently associated with polylines. By default this is equivalent to the
	 * contentVA, but after the dimensions have swapped this can also be the storageVA
	 */
	private IVirtualArray polylineVA;
	/**
	 * The va type of the polylines
	 */
	private EVAType polylineVAType = EVAType.CONTENT;
	// private int iAxisVAID = 0;
	/**
	 * Same as polylineVA, just inverse
	 */
	private IVirtualArray axisVA;
	/**
	 * The type of the VA associated with the axis, by default Storages
	 */
	private EVAType axisVAType = EVAType.STORAGE;

	private SelectionManager polylineSelectionManager;
	private SelectionManager axisSelectionManager;

	protected ParCoordsRenderStyle renderStyle;

	private int iDisplayEveryNthPolyline = 1;

	EIconTextures dropTexture = EIconTextures.DROP_NORMAL;
	int iChangeDropOnAxisNumber = -1;

	GLBookmarkManager glBookmarks;
	boolean bShowSelectionHeatMap = false;

	private GLInfoAreaManager infoAreaManager;

	/** Utility object for coordinate transformation and projection */
	protected StandardTransformer selectionTransformer;

	// listeners
	private ApplyCurrentSelectionToVirtualArrayListener applyCurrentSelectionToVirtualArrayListener;
	private ResetAxisSpacingListener resetAxisSpacingListener;
	private BookmarkButtonListener bookmarkListener;
	private ResetViewListener resetViewListener;
	private UseRandomSamplingListener useRandomSamplingListener;
	private ChangeOrientationListener changeOrientationListener;
	private PreventOcclusionListener preventOcclusionListener;
	private AngularBrushingListener angularBrushingListener;

	private org.eclipse.swt.graphics.Point upperLeftScreenPos = new org.eclipse.swt.graphics.Point(0, 0);

	/**
	 * FIXME: remove after data flipper video
	 */
	private boolean renderConnectionsLeft = true;

	/**
	 * Constructor.
	 */
	public GLParallelCoordinates(GLCaleydoCanvas glCanvas, final String sLabel, final IViewFrustum viewFrustum) {

		super(glCanvas, sLabel, viewFrustum);
		viewType = EManagedObjectType.GL_PARALLEL_COORDINATES;

		renderStyle = new ParCoordsRenderStyle(this, viewFrustum);
		super.renderStyle = this.renderStyle;

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

		selectionTransformer = new StandardTransformer(iUniqueID);
		init(gl);
	}

	@Override
	public void initRemote(final GL gl, final AGLView glParentView,
		final GLMouseListener glMouseListener, GLInfoAreaManager infoAreaManager) {

		bShowSelectionHeatMap = false;
		this.glMouseListener = glMouseListener;
		this.infoAreaManager = infoAreaManager;

		iGLDisplayListIndexRemote = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexRemote;

		selectionTransformer = new StandardTransformer(iUniqueID);
		init(gl);
		// toggleRenderContext();
	}

	@Override
	public void init(final GL gl) {

		fXDefaultTranslation = renderStyle.getXSpacing();
		fYTranslation = renderStyle.getBottomSpacing();
	}

	@Override
	public void initData() {
		super.initData();

		if (glBookmarks != null)
			glBookmarks.setSet(set);
		initGates();
		resetAxisSpacing();
	}

	@Override
	public void displayLocal(final GL gl) {

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
		ConnectedElementRepresentationManager cerm =
			GeneralManager.get().getViewGLCanvasManager().getConnectedElementRepresentationManager();
		cerm.doViewRelatedTransformation(gl, selectionTransformer);

		// infoAreaManager.renderInPlaceInfo(gl);

		if (eBusyModeState != EBusyModeState.OFF) {
			renderBusyMode(gl);
		}
	}

	@Override
	public void displayRemote(final GL gl) {

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
	public void display(final GL gl) {

		// if(storageVA.size() > 20)
		// return;
		//		
		processEvents();

		if (bShowSelectionHeatMap) {

			gl.glTranslatef(viewFrustum.getRight() - glBookmarks.getViewFrustum().getWidth(), 0, 0.002f);

			// Render memo pad background
			IViewFrustum sHMFrustum = glBookmarks.getViewFrustum();
			sHMFrustum.setTop(viewFrustum.getTop());
			sHMFrustum.setBottom(viewFrustum.getBottom());

			gl.glColor4fv(GeneralRenderStyle.PANEL_BACKGROUN_COLOR, 0);
			gl.glLineWidth(1);
			gl.glBegin(GL.GL_POLYGON);
			gl.glVertex3f(0, 0, 0);
			gl.glVertex3f(glBookmarks.getViewFrustum().getWidth(), 0, 0);
			gl.glVertex3f(glBookmarks.getViewFrustum().getWidth(), glBookmarks.getViewFrustum().getHeight(),
				0);
			gl.glVertex3f(0, glBookmarks.getViewFrustum().getHeight(), 0);
			gl.glEnd();

			int iPickingID =
				pickingManager.getPickingID(iUniqueID, EPickingType.PCS_VIEW_SELECTION, glBookmarks.getID());
			gl.glPushName(iPickingID);
			glBookmarks.displayRemote(gl);

			gl.glPopName();
			gl.glTranslatef(-viewFrustum.getRight() + glBookmarks.getViewFrustum().getWidth(), 0, -0.002f);
		}

		if (generalManager.getTrackDataProvider().isTrackModeActive())
			handleTrackInput(gl);

		// TODO another display list
		clipToFrustum(gl);

		gl.glTranslatef(fXDefaultTranslation + fXTranslation, fYTranslation, 0.0f);

		if (bIsDraggingActive) {
			handleGateDragging(gl);
		}

		if (bWasAxisMoved) {
			adjustAxisSpacing(gl);
			if (glMouseListener.wasMouseReleased()) {
				bWasAxisMoved = false;
			}
		}

		gl.glCallList(iGLDisplayListToCall);

		if (bIsAngularBrushingActive && iSelectedLineID != -1) {
			handleAngularBrushing(gl);
		}

		gl.glTranslatef(-fXDefaultTranslation - fXTranslation, -fYTranslation, 0.0f);

		if (!isRenderedRemote())
			contextMenu.render(gl, this);

	}

	private void createSelectionHeatMap(GL gl) {
		// Create selection panel
		CmdCreateGLEventListener cmdCreateGLView =
			(CmdCreateGLEventListener) generalManager.getCommandManager().createCommandByType(
				ECommandType.CREATE_GL_PROPAGATION_HEAT_MAP_3D);
		cmdCreateGLView.setAttributes(dataDomain, EProjectionMode.ORTHOGRAPHIC, 0, 0.8f, viewFrustum
			.getBottom(), viewFrustum.getTop(), -20, 20, -1);
		cmdCreateGLView.doCommand();
		glBookmarks = (GLBookmarkManager) cmdCreateGLView.getCreatedObject();
		glBookmarks.setRemoteRenderingGLView(this);
		glBookmarks.setUseCase(useCase);
		glBookmarks.setSet(set);
		glBookmarks.initData();

		// FIXME: remoteRenderingGLCanvas is null, conceptual error
		glBookmarks.initRemote(gl, this, glMouseListener, null);
	}

	public void triggerAngularBrushing() {
		bAngularBrushingSelectPolyline = true;
		setDisplayListDirty();
	}

	@Override
	public void renderContext(boolean bRenderOnlyContext) {
		this.bRenderOnlyContext = bRenderOnlyContext;

		if (bRenderOnlyContext) {
			contentVA = useCase.getVA(EVAType.CONTENT_CONTEXT);
		}
		else {

			contentVA = useCase.getVA(EVAType.CONTENT);
		}

		contentSelectionManager.setVA(contentVA);
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
	public void preventOcclusion(boolean bPreventOcclusion) {
		this.bPreventOcclusion = bPreventOcclusion;
		setDisplayListDirty();
	}

	/**
	 * Reset all selections and deselections
	 */
	@Override
	public void clearAllSelections() {

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

		if (glBookmarks != null) {
			glBookmarks.clearAllSelections();
		}

	}

	/**
	 * Sends a bookmark event containing all elements which are currently visible in the pcs, if the number of
	 * elements is less than 20. If it's more than 20 an error message is displayed.
	 */
	public void bookmarkElements() {

		IVirtualArrayDelta delta = contentSelectionManager.getBroadcastVADelta();
		if (delta.size() > 20) {
			getParentGLCanvas().getParentComposite().getDisplay().asyncExec(new Runnable() {

				@Override
				public void run() {
					MessageDialog.openError(getParentGLCanvas().getParentComposite().getShell(),
						"Bookmark Limit",
						"Can not bookmark more than 20 elements - reduce polylines to less than 20 first");

					return;
				}
			});
			return;
		}

		if (!isRenderedRemote()) {
			bShowSelectionHeatMap = true;
			BookmarkEvent<Integer> bookmarkEvent = new BookmarkEvent<Integer>(EIDType.EXPRESSION_INDEX);
			for (VADeltaItem item : delta.getAllItems()) {
				bookmarkEvent.addBookmark(item.getPrimaryID());
			}
			eventPublisher.triggerEvent(bookmarkEvent);
			resetAxisSpacing();
			setDisplayListDirty();
		}
	}

	public void saveSelection() {

		// polylineSelectionManager.moveType(ESelectionType.DESELECTED,
		// ESelectionType.REMOVE);

		polylineSelectionManager.removeElements(ESelectionType.DESELECTED);
		clearAllSelections();
		setDisplayListDirty();

		polylineVA.setGroupList(null);

		// todo this doesn't work for turned stuff
		ReplaceVirtualArrayInUseCaseEvent event =
			new ReplaceVirtualArrayInUseCaseEvent(ePolylineDataType.getCategory(), polylineVAType,
				(VirtualArray) polylineVA);

		event.setSender(this);
		eventPublisher.triggerEvent(event);

	}

	/**
	 * Initializes the array lists that contain the data. Must be run at program start, every time you
	 * exchange axis and polylines and every time you change storages or selections *
	 */
	@Override
	protected void initLists() {

		if (bRenderOnlyContext)
			contentVAType = EVAType.CONTENT_CONTEXT;
		else
			contentVAType = EVAType.CONTENT;

		contentVA = useCase.getVA(contentVAType);

		storageVA = useCase.getVA(storageVAType);

		initContentVariables();

		contentSelectionManager.setVA(contentVA);
		storageSelectionManager.setVA(storageVA);
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
		EIDType contentDataType;
		EIDType storageDataType;
		if (dataDomain == EDataDomain.GENETIC_DATA) {
			contentDataType = EIDType.EXPRESSION_INDEX;
			storageDataType = EIDType.EXPERIMENT_INDEX;
		}
		else if (dataDomain == EDataDomain.CLINICAL_DATA || dataDomain == EDataDomain.UNSPECIFIED) {
			contentDataType = EIDType.EXPERIMENT_INDEX;
			storageDataType = EIDType.EXPERIMENT_RECORD;
		}
		else {
			throw new IllegalStateException("Unsupported data domain (" + dataDomain
				+ ") for parallel coordinates");
		}

		if (bRenderStorageHorizontally) {

			eAxisDataType = contentDataType;
			ePolylineDataType = storageDataType;

			axisVA = contentVA;
			axisVAType = contentVAType;
			polylineVA = storageVA;
			polylineVAType = storageVAType;

			polylineSelectionManager = storageSelectionManager;
			axisSelectionManager = contentSelectionManager;
		}
		else {

			eAxisDataType = storageDataType;
			ePolylineDataType = contentDataType;

			polylineVA = contentVA;
			polylineVAType = contentVAType;
			axisVA = storageVA;
			axisVAType = storageVAType;

			polylineSelectionManager = contentSelectionManager;
			axisSelectionManager = storageSelectionManager;
		}
	}

	/**
	 * Initialize the gates. The gate heights are saved in two lists, which contain the rendering height of
	 * the gate
	 */
	private void initGates() {
		hashGates = new HashMap<Integer, AGate>();
		hashNumberOfGatesPerAxisID = new HashMap<Integer, Integer>();
		hashIsGateBlocking = new HashMap<Integer, ArrayList<Integer>>();
		if (set.isSetHomogeneous()) {
			hashMasterGates = new HashMap<Integer, Gate>();
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

			if (set.isSetHomogeneous()) {
				renderGlobalBrush(gl);
			}

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

			// if (bShowSelectionHeatMap) {
			//
			// gl.glTranslatef(viewFrustum.getRight() - glSelectionHeatMap.getViewFrustum().getWidth(), 0,
			// 0.002f);
			// // gl.glTranslatef(1, 0, 0);
			// int iPickingID =
			// pickingManager.getPickingID(iUniqueID, EPickingType.PCS_VIEW_SELECTION,
			// glSelectionHeatMap.getID());
			// gl.glPushName(iPickingID);
			// glSelectionHeatMap.displayRemote(gl);
			//
			// gl.glPopName();
			// // gl.glTranslatef(-1, 0, 0);
			// gl.glTranslatef(-viewFrustum.getRight() + glSelectionHeatMap.getViewFrustum().getWidth(), 0,
			// -0.002f);
			// }

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
			// iDisplayEveryNthPolyline = contentVA.size()
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
			for (int iVertexCount = 0; iVertexCount < axisVA.size(); iVertexCount++) {
				int iStorageIndex = 0;

				// get the index if array as polyline
				if (bRenderStorageHorizontally) {
					iStorageIndex = contentVA.get(iVertexCount);
				}
				// get the storage and the storage index for the different cases
				else {
					currentStorage = set.get(storageVA.get(iVertexCount));
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

		textRenderer.setColor(0, 0, 0, 1);

		int iNumberAxis = axisVA.size();
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

			if (detailLevel == EDetailLevel.HIGH) {

				// NaN Button
				float fXButtonOrigin = alAxisSpacing.get(iCount);

				Vec3f lowerLeftCorner =
					new Vec3f(fXButtonOrigin - 0.03f, ParCoordsRenderStyle.NAN_Y_OFFSET - 0.03f,
						ParCoordsRenderStyle.NAN_Z);
				Vec3f lowerRightCorner =
					new Vec3f(fXButtonOrigin + 0.03f, ParCoordsRenderStyle.NAN_Y_OFFSET - 0.03f,
						ParCoordsRenderStyle.NAN_Z);
				Vec3f upperRightCorner =
					new Vec3f(fXButtonOrigin + 0.03f, ParCoordsRenderStyle.NAN_Y_OFFSET + 0.03f,
						ParCoordsRenderStyle.NAN_Z);
				Vec3f upperLeftCorner =
					new Vec3f(fXButtonOrigin - 0.03f, ParCoordsRenderStyle.NAN_Y_OFFSET + 0.03f,
						ParCoordsRenderStyle.NAN_Z);
				Vec3f scalingPivot =
					new Vec3f(fXButtonOrigin, ParCoordsRenderStyle.NAN_Y_OFFSET, ParCoordsRenderStyle.NAN_Z);

				int iPickingID =
					pickingManager.getPickingID(iUniqueID, EPickingType.REMOVE_NAN, axisVA.get(iCount));
				gl.glPushName(iPickingID);

				textureManager.renderGUITexture(gl, EIconTextures.NAN, lowerLeftCorner, lowerRightCorner,
					upperRightCorner, upperLeftCorner, scalingPivot, 1, 1, 1, 1, 100);

				gl.glPopName();

				// markers on axis
				float fMarkerSpacing = renderStyle.getAxisHeight() / (NUMBER_AXIS_MARKERS + 1);
				for (int iInnerCount = 1; iInnerCount <= NUMBER_AXIS_MARKERS; iInnerCount++) {
					float fCurrentHeight = fMarkerSpacing * iInnerCount;
					if (iCount == 0) {
						if (set.isSetHomogeneous()) {
							float fNumber =
								(float) set.getRawForNormalized(fCurrentHeight / renderStyle.getAxisHeight());

							Rectangle2D bounds =
								textRenderer.getScaledBounds(gl, getDecimalFormat().format(fNumber),
									renderStyle.getSmallFontScalingFactor(),
									ParCoordsRenderStyle.MIN_NUMBER_TEXT_SIZE);
							float fWidth = (float) bounds.getWidth();
							float fHeightHalf = (float) bounds.getHeight() / 3.0f;

							renderNumber(gl, getDecimalFormat().format(fNumber), fXPosition - fWidth
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

					case EXPRESSION_INDEX:
						// FIXME: Due to new mapping system, a mapping involving expression index can return a
						// Set of
						// values, depending on the IDType that has been specified when loading expression
						// data.
						// Possibly a different handling of the Set is required.
						Set<String> setGeneSymbols =
							idMappingManager.getIDAsSet(EIDType.EXPRESSION_INDEX, EIDType.GENE_SYMBOL, axisVA
								.get(iCount));

						if ((setGeneSymbols != null && !setGeneSymbols.isEmpty())) {
							sAxisLabel = (String) setGeneSymbols.toArray()[0];
						}
						if (sAxisLabel == null)
							sAxisLabel = "Unknown Gene";
						break;

					case EXPERIMENT:
					default:
						if (bRenderStorageHorizontally) {
							sAxisLabel = "TODO: gene labels for axis";
						}
						else
							sAxisLabel = set.get(storageVA.get(iCount)).getLabel();
						break;

				}
				gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
				gl.glTranslatef(fXPosition,
					renderStyle.getAxisHeight() + renderStyle.getAxisCaptionSpacing(), 0);
				gl.glRotatef(25, 0, 0, 1);
				textRenderer.begin3DRendering();
				float fScaling = renderStyle.getSmallFontScalingFactor();
				if (isRenderedRemote())
					fScaling *= 1.5f;
				textRenderer.draw3D(gl, sAxisLabel, 0, 0, 0, fScaling,
					ParCoordsRenderStyle.MIN_AXIS_LABEL_TEXT_SIZE);
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

				lowerLeftCorner.set(fXButtonOrigin - 0.03f, fYGateAddOrigin, AXIS_Z);
				lowerRightCorner.set(fXButtonOrigin + 0.03f, fYGateAddOrigin, AXIS_Z);
				upperRightCorner.set(fXButtonOrigin + 0.03f, fYGateAddOrigin + 0.12f, AXIS_Z);
				upperLeftCorner.set(fXButtonOrigin - 0.03f, fYGateAddOrigin + 0.12f, AXIS_Z);
				scalingPivot.set(fXButtonOrigin, fYGateAddOrigin, AXIS_Z);

				gl.glPushName(iPickingID);

				textureManager.renderGUITexture(gl, EIconTextures.ADD_GATE, lowerLeftCorner,
					lowerRightCorner, upperRightCorner, upperLeftCorner, scalingPivot, 1, 1, 1, 1, 100);

				gl.glPopName();

				if (selectedSet.contains(axisVA.get(iCount)) || mouseOverSet.contains(axisVA.get(iCount))) {

					lowerLeftCorner.set(fXButtonOrigin - 0.15f, fYDropOrigin - 0.3f, AXIS_Z + 0.005f);
					lowerRightCorner.set(fXButtonOrigin + 0.15f, fYDropOrigin - 0.3f, AXIS_Z + 0.005f);
					upperRightCorner.set(fXButtonOrigin + 0.15f, fYDropOrigin, AXIS_Z + 0.005f);
					upperLeftCorner.set(fXButtonOrigin - 0.15f, fYDropOrigin, AXIS_Z + 0.005f);
					scalingPivot.set(fXButtonOrigin, fYDropOrigin, AXIS_Z + 0.005f);

					// the mouse over drop
					if (iChangeDropOnAxisNumber == iCount) {
						// tempTexture = textureManager.getIconTexture(gl, dropTexture);
						textureManager.renderGUITexture(gl, dropTexture, lowerLeftCorner, lowerRightCorner,
							upperRightCorner, upperLeftCorner, scalingPivot, 1, 1, 1, 1, 80);

						if (!bWasAxisMoved) {
							dropTexture = EIconTextures.DROP_NORMAL;
						}
					}
					else {
						textureManager
							.renderGUITexture(gl, EIconTextures.DROP_NORMAL, lowerLeftCorner,
								lowerRightCorner, upperRightCorner, upperLeftCorner, scalingPivot, 1, 1, 1,
								1, 80);
					}

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

					gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
					gl.glPushName(iPickingID);

					lowerLeftCorner.set(fXButtonOrigin - 0.05f, fYDropOrigin - 0.2f, AXIS_Z);
					lowerRightCorner.set(fXButtonOrigin + 0.05f, fYDropOrigin - 0.2f, AXIS_Z);
					upperRightCorner.set(fXButtonOrigin + 0.05f, fYDropOrigin, AXIS_Z);
					upperLeftCorner.set(fXButtonOrigin - 0.05f, fYDropOrigin, AXIS_Z);
					scalingPivot.set(fXButtonOrigin, fYDropOrigin, AXIS_Z);

					textureManager.renderGUITexture(gl, EIconTextures.SMALL_DROP, lowerLeftCorner,
						lowerRightCorner, upperRightCorner, upperLeftCorner, scalingPivot, 1, 1, 1, 1, 80);

					gl.glPopName();
					gl.glPopAttrib();

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
		Texture tempTexture = textureManager.getIconTexture(gl, EIconTextures.PAR_COORDS_SYMBOL);
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

		for (Integer iGateID : hashGates.keySet()) {
			// Gate ID / 1000 is axis ID
			int iAxisID = iGateID / 1000;
			AGate gate = hashGates.get(iGateID);
			// Pair<Float, Float> gate = hashGates.get(iGateID);
			// TODO for all indices

			ArrayList<Integer> iAlAxisIndex = axisVA.indicesOf(iAxisID);
			for (int iAxisIndex : iAlAxisIndex) {
				float fCurrentPosition = alAxisSpacing.get(iAxisIndex);
				gate.setCurrentPosition(fCurrentPosition);
				//String label = set.get(iAxisID).getLabel();

				gate.draw(gl, pickingManager, textureManager, textRenderer, iUniqueID);
				// renderSingleGate(gl, gate, iAxisID, iGateID, fCurrentPosition);
			}
		}

	}

	// private void renderSingleGate(GL gl, Pair<Float, Float> gate, int iAxisID, int iGateID,
	// float fCurrentPosition) {
	//
	// Float fBottom = gate.getFirst();
	// Float fTop = gate.getSecond();
	//
	// gl.glColor4f(1, 1, 1, 0f);
	// int iPickingID = pickingManager.getPickingID(iUniqueID, EPickingType.REMOVE_GATE, iGateID);
	// gl.glPushName(iPickingID);
	// gl.glBegin(GL.GL_POLYGON);
	// gl.glVertex3f(fCurrentPosition + GATE_WIDTH, fTop - GATE_TIP_HEIGHT, GATE_Z);
	// gl.glVertex3f(fCurrentPosition + 0.1828f - GATE_WIDTH, fTop - GATE_TIP_HEIGHT, GATE_Z);
	// gl.glVertex3f(fCurrentPosition + 0.1828f - GATE_WIDTH, fTop, GATE_Z);
	// gl.glVertex3f(fCurrentPosition + GATE_WIDTH, fTop, GATE_Z);
	// gl.glEnd();
	// gl.glPopName();
	//
	// Texture tempTexture = textureManager.getIconTexture(gl, EIconTextures.GATE_TOP);
	// tempTexture.enable();
	// tempTexture.bind();
	// TextureCoords texCoords = tempTexture.getImageTexCoords();
	// gl.glColor4f(1, 1, 1, 1);
	// // The tip of the gate
	// gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.GATE_TIP_SELECTION, iGateID));
	// gl.glBegin(GL.GL_POLYGON);
	// gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
	// gl.glVertex3f(fCurrentPosition - GATE_WIDTH, fTop - GATE_TIP_HEIGHT, GATE_Z);
	// gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
	// gl.glVertex3f(fCurrentPosition + 0.1828f - GATE_WIDTH, fTop - GATE_TIP_HEIGHT, GATE_Z);
	// gl.glTexCoord2f(texCoords.right(), texCoords.top());
	// gl.glVertex3f(fCurrentPosition + 0.1828f - GATE_WIDTH, fTop, GATE_Z);
	// gl.glTexCoord2f(texCoords.left(), texCoords.top());
	// gl.glVertex3f(fCurrentPosition - GATE_WIDTH, fTop, GATE_Z);
	// gl.glEnd();
	// tempTexture.disable();
	//
	// tempTexture = textureManager.getIconTexture(gl, EIconTextures.GATE_MENUE);
	// tempTexture.enable();
	// tempTexture.bind();
	// texCoords = tempTexture.getImageTexCoords();
	// float fMenuHeight = 8 * GATE_WIDTH / 3.5f;
	// gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
	// gl.glBegin(GL.GL_POLYGON);
	// gl.glTexCoord2f(texCoords.left(), texCoords.top());
	// gl.glVertex3f(fCurrentPosition - 7 * GATE_WIDTH, fTop, GATE_Z);
	// gl.glTexCoord2f(texCoords.right(), texCoords.top());
	// gl.glVertex3f(fCurrentPosition + GATE_WIDTH, fTop, GATE_Z);
	// gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
	// gl.glVertex3f(fCurrentPosition + GATE_WIDTH, fTop + fMenuHeight, GATE_Z);
	// gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
	// gl.glVertex3f(fCurrentPosition - 7 * GATE_WIDTH, fTop + fMenuHeight, GATE_Z);
	// gl.glEnd();
	//
	// textRenderer.setColor(1, 1, 1, 1);
	// float fValue = (float) set.getRawForNormalized(fTop / renderStyle.getAxisHeight());
	// renderNumber(gl, getDecimalFormat().format(fValue), fCurrentPosition - 5 * GATE_WIDTH, fTop + 0.02f);
	//
	// tempTexture.disable();
	// gl.glPopAttrib();
	// gl.glPopName();
	//
	// // if (set.isSetHomogeneous())
	// // {
	// // // renderBoxedYValues(gl, fCurrentPosition, fTop,
	// // // getDecimalFormat().format(
	// // // set.getRawForNormalized(fTop / renderStyle.getAxisHeight())),
	// // // ESelectionType.NORMAL);
	// // }
	// // else
	// // {
	// // // TODO storage based acces
	// // }
	//
	// tempTexture = textureManager.getIconTexture(gl, EIconTextures.GATE_BODY);
	// tempTexture.enable();
	// tempTexture.bind();
	// texCoords = tempTexture.getImageTexCoords();
	// gl.glColor4f(1, 1, 1, 1);
	// gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.GATE_BODY_SELECTION, iGateID));
	// gl.glBegin(GL.GL_POLYGON);
	// gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
	// gl.glVertex3f(fCurrentPosition - GATE_WIDTH, fBottom + ParCoordsRenderStyle.GATE_BOTTOM_HEIGHT,
	// GATE_Z);
	// gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
	// gl.glVertex3f(fCurrentPosition + GATE_WIDTH, fBottom + ParCoordsRenderStyle.GATE_BOTTOM_HEIGHT,
	// GATE_Z);
	// gl.glTexCoord2f(texCoords.right(), texCoords.top());
	// gl.glVertex3f(fCurrentPosition + GATE_WIDTH, fTop - GATE_TIP_HEIGHT, GATE_Z);
	// gl.glTexCoord2f(texCoords.left(), texCoords.top());
	// gl.glVertex3f(fCurrentPosition - GATE_WIDTH, fTop - GATE_TIP_HEIGHT, GATE_Z);
	// gl.glEnd();
	// gl.glPopName();
	// tempTexture.disable();
	//
	// gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.GATE_BOTTOM_SELECTION, iGateID));
	// tempTexture = textureManager.getIconTexture(gl, EIconTextures.GATE_BOTTOM);
	// tempTexture.enable();
	// tempTexture.bind();
	// texCoords = tempTexture.getImageTexCoords();
	// gl.glColor4f(1, 1, 1, 1);
	// gl.glBegin(GL.GL_POLYGON);
	// gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
	// gl.glVertex3f(fCurrentPosition - GATE_WIDTH, fBottom, GATE_Z);
	// gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
	// gl.glVertex3f(fCurrentPosition + GATE_WIDTH, fBottom, GATE_Z);
	// gl.glTexCoord2f(texCoords.right(), texCoords.top());
	// gl.glVertex3f(fCurrentPosition + GATE_WIDTH, fBottom + ParCoordsRenderStyle.GATE_BOTTOM_HEIGHT,
	// GATE_Z);
	// gl.glTexCoord2f(texCoords.left(), texCoords.top());
	// gl.glVertex3f(fCurrentPosition - GATE_WIDTH, fBottom + ParCoordsRenderStyle.GATE_BOTTOM_HEIGHT,
	// GATE_Z);
	// gl.glEnd();
	//
	// tempTexture = textureManager.getIconTexture(gl, EIconTextures.GATE_MENUE);
	// tempTexture.enable();
	// tempTexture.bind();
	// texCoords = tempTexture.getImageTexCoords();
	// gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
	// gl.glBegin(GL.GL_POLYGON);
	// gl.glTexCoord2f(texCoords.left(), texCoords.top());
	// gl.glVertex3f(fCurrentPosition - 7 * GATE_WIDTH, fBottom, GATE_Z);
	// gl.glTexCoord2f(texCoords.right(), texCoords.top());
	// gl.glVertex3f(fCurrentPosition + GATE_WIDTH, fBottom, GATE_Z);
	// gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
	// gl.glVertex3f(fCurrentPosition + GATE_WIDTH, fBottom - fMenuHeight, GATE_Z);
	// gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
	// gl.glVertex3f(fCurrentPosition - 7 * GATE_WIDTH, fBottom - fMenuHeight, GATE_Z);
	// gl.glEnd();
	//
	// textRenderer.setColor(1, 1, 1, 1);
	// fValue = (float) set.getRawForNormalized(fBottom / renderStyle.getAxisHeight());
	// renderNumber(gl, getDecimalFormat().format(fValue), fCurrentPosition - 5 * GATE_WIDTH, fBottom
	// - fMenuHeight + 0.02f);
	//
	// tempTexture.disable();
	//
	// gl.glPopName();
	//
	// // if (set.isSetHomogeneous())
	// // {
	// // // float fValue = (float) set.getRawForNormalized(fBottom
	// // // / renderStyle.getAxisHeight());
	// // // if (fValue > set.getMin())
	// // // renderBoxedYValues(gl, fCurrentPosition, fBottom,
	// // // getDecimalFormat()
	// // // .format(fValue), ESelectionType.NORMAL);
	// // }
	// // else
	// // {
	// // // TODO storage based access
	// // }
	// }

	private void renderGlobalBrush(GL gl) {
		if (detailLevel != EDetailLevel.HIGH)
			return;

		gl.glColor4f(0, 0, 0, 1f);
		gl.glLineWidth(ParCoordsRenderStyle.Y_AXIS_LINE_WIDTH);
		// gl.glPushName(iPickingID);

		float fXOrigin = -0.25f;

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
		gl.glPushName(iPickingID);

		Vec3f lowerLeftCorner = new Vec3f(fXOrigin - 0.05f, fYGateAddOrigin, AXIS_Z);
		Vec3f lowerRightCorner = new Vec3f(fXOrigin + 0.05f, fYGateAddOrigin, AXIS_Z);
		Vec3f upperRightCorner = new Vec3f(fXOrigin + 0.05f, fYGateAddOrigin + 0.2f, AXIS_Z);
		Vec3f upperLeftCorner = new Vec3f(fXOrigin - 0.05f, fYGateAddOrigin + 0.2f, AXIS_Z);
		Vec3f scalingPivot = new Vec3f(fXOrigin, fYGateAddOrigin, AXIS_Z);

		textureManager.renderGUITexture(gl, EIconTextures.ADD_GATE, lowerLeftCorner, lowerRightCorner,
			upperRightCorner, upperLeftCorner, scalingPivot, 1, 1, 1, 1, 100);

		gl.glPopName();

		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

		for (Integer iGateID : hashMasterGates.keySet()) {
			Gate gate = hashMasterGates.get(iGateID);
			Float fBottom = gate.getBottom();
			Float fTop = gate.getTop();

			gl.glColor4fv(ParCoordsRenderStyle.GATE_BODY_COLOR, 0);
			gl.glBegin(GL.GL_POLYGON);
			gl.glVertex3f(fXOrigin, fBottom, 0);
			gl.glVertex3f(viewFrustum.getWidth() - 1, fBottom, 0);
			gl.glVertex3f(viewFrustum.getWidth() - 1, fTop, 0);
			// todo eurovis hacke
			// gl.glVertex3f(viewFrustum.getWidth(), fBottom, 0);
			// gl.glVertex3f(viewFrustum.getWidth(), fTop, 0);
			//			
			gl.glVertex3f(fXOrigin - 0.05f, fTop, 0);
			gl.glEnd();

			gate.setCurrentPosition(fXOrigin);
			gate.draw(gl, pickingManager, textureManager, textRenderer, iUniqueID);
			// renderSingleGate(gl, gate, -1, iGateID, fXOrigin);
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

		float fScaling = renderStyle.getSmallFontScalingFactor();
		if (isRenderedRemote())
			fScaling *= 1.5f;
		// don't render values that are below the y axis
		if (fYOrigin < 0)
			return;

		gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
		gl.glLineWidth(Y_AXIS_LINE_WIDTH);
		gl.glColor4fv(Y_AXIS_COLOR, 0);

		Rectangle2D tempRectangle =
			textRenderer.getScaledBounds(gl, sRawValue, fScaling, ParCoordsRenderStyle.MIN_NUMBER_TEXT_SIZE);
		float fSmallSpacing = renderStyle.getVerySmallSpacing();
		float fBackPlaneWidth = (float) tempRectangle.getWidth();
		float fBackPlaneHeight = (float) tempRectangle.getHeight();
		float fXTextOrigin = fXOrigin + 2 * AXIS_MARKER_WIDTH;
		float fYTextOrigin = fYOrigin;

		gl.glColor4f(1f, 1f, 1f, 0.8f);
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(fXTextOrigin - fSmallSpacing, fYTextOrigin - fSmallSpacing, LABEL_Z);
		gl.glVertex3f(fXTextOrigin + fBackPlaneWidth, fYTextOrigin - fSmallSpacing, LABEL_Z);
		gl.glVertex3f(fXTextOrigin + fBackPlaneWidth, fYTextOrigin + fBackPlaneHeight, LABEL_Z);
		gl.glVertex3f(fXTextOrigin - fSmallSpacing, fYTextOrigin + fBackPlaneHeight, LABEL_Z);
		gl.glEnd();

		renderNumber(gl, sRawValue, fXTextOrigin, fYTextOrigin);
		gl.glPopAttrib();
	}

	private void renderNumber(GL gl, String sRawValue, float fXOrigin, float fYOrigin) {
		textRenderer.begin3DRendering();

		// String text = "";
		// if (Float.isNaN(fRawValue))
		// text = "NaN";
		// else
		// text = getDecimalFormat().format(fRawValue);

		float fScaling = renderStyle.getSmallFontScalingFactor();
		if (isRenderedRemote())
			fScaling *= 1.5f;

		textRenderer.draw3D(gl, sRawValue, fXOrigin, fYOrigin, ParCoordsRenderStyle.TEXT_ON_LABEL_Z,
			fScaling, ParCoordsRenderStyle.MIN_NUMBER_TEXT_SIZE);
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

		// todo only valid for one gate
		AGate gate = null;
		if (iDraggedGateNumber > 999) {
			gate = hashGates.get(iDraggedGateNumber);
		}
		else {
			gate = hashMasterGates.get(iDraggedGateNumber);
		}
		if (gate == null)
			return;

		gate.handleDragging(gl, fArTargetWorldCoordinates[0], fArTargetWorldCoordinates[1], draggedObject,
			bIsGateDraggingFirstTime);
		bIsGateDraggingFirstTime = false;

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
			AGate gate = hashGates.get(iGateID);

			for (int iPolylineIndex : polylineVA) {
				EDataRepresentation usedDataRepresentation = EDataRepresentation.RAW;
				if (!set.isSetHomogeneous())
					usedDataRepresentation = EDataRepresentation.NORMALIZED;
				if (bRenderStorageHorizontally) {
					fCurrentValue = set.get(iPolylineIndex).getFloat(usedDataRepresentation, iAxisID);
				}
				else {
					fCurrentValue = set.get(iAxisID).getFloat(usedDataRepresentation, iPolylineIndex);
				}

				if (Float.isNaN(fCurrentValue)) {
					continue;
				}

				if (fCurrentValue <= gate.getUpperValue() && fCurrentValue >= gate.getLowerValue()) {
					alCurrentGateBlocks.add(iPolylineIndex);
				}

				// if (fCurrentValue <= (gate.getSecond() - 0.0000000001f) / renderStyle.getAxisHeight()
				// && fCurrentValue >= gate.getFirst() / renderStyle.getAxisHeight()) {
				// alCurrentGateBlocks.add(iPolylineIndex);
				// }
			}
		}
	}

	private void handleNANUnselection() {
		float fCurrentValue = 0;
		hashIsNANBlocking.clear();
		for (Integer iAxisID : hashExcludeNAN.keySet()) {
			ArrayList<Integer> alDeselectedLines = new ArrayList<Integer>();
			for (int iPolylineIndex : polylineVA) {
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
			Gate gate = hashMasterGates.get(iGateID);
			for (int iPolylineIndex : polylineVA) {
				boolean bIsBlocking = true;
				for (int iAxisIndex : axisVA) {
					if (bRenderStorageHorizontally) {
						fCurrentValue = set.get(iPolylineIndex).getFloat(EDataRepresentation.RAW, iAxisIndex);
					}
					else {
						fCurrentValue = set.get(iAxisIndex).getFloat(EDataRepresentation.RAW, iPolylineIndex);
					}

					if (Float.isNaN(fCurrentValue)) {
						continue;
					}
					// if (fCurrentValue <= (gate.getSecond() - 0.0000000001f) / renderStyle.getAxisHeight()
					// && fCurrentValue >= gate.getFirst() / renderStyle.getAxisHeight()) {
					// bIsBlocking = true;
					// }

					if (fCurrentValue <= gate.getUpperValue() && fCurrentValue >= gate.getLowerValue()) {
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

			for (VADeltaItem item : delta) {
				if (item.getType() == EVAOperation.REMOVE) {
					int iElement = axisVA.get(item.getIndex());

					// resetAxisSpacing();
					if (axisVA.containsElement(iElement) == 1) {
						hashGates.remove(iElement);
					}
				}
				else if (item.getType() == EVAOperation.REMOVE_ELEMENT) {

					hashGates.remove(item.getPrimaryID());
				}
			}

			axisSelectionManager.setVADelta(delta);
			resetAxisSpacing();
		}
		if (delta.getIDType() == ePolylineDataType) {

			contentSelectionManager.setVADelta(delta);
			// for (VADeltaItem item : delta) {
			// int iElement = axisVA.get(item.getIndex());
			// if (item.getType() == EVAOperation.REMOVE) {
			// // resetAxisSpacing();
			// if (axisVA.containsElement(iElement) == 1) {
			// hashGates.remove(iElement);
			// }
			// }
			// else if (item.getType() == EVAOperation.REMOVE_ELEMENT) {
			// // resetAxisSpacing();
			// hashGates.remove(item.getPrimaryID());
			// }
			// }
		}

	}

	// TODO: revise this, not very performance friendly, especially the clearing
	// of the DESELECTED
	private void handleUnselection() {

		handleGateUnselection();
		handleNANUnselection();
		if (set.isSetHomogeneous())
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
		if (bIsDraggingActive || bIsAngularBrushingActive) {
			triggerSelectionUpdate();
		}

		// for (int iCurrent : hashDeselectedPolylines.keySet())
		// {
		// polylineSelectionManager.addToType(ESelectionType.DESELECTED,
		// alCurrent);
		// polylineSelectionManager.addToType(ESelectionType.DESELECTED,
		// iCurrent);
		// }
	}

	private void triggerSelectionUpdate() {
		SelectionUpdateEvent selectionUpdateEvent = new SelectionUpdateEvent();
		selectionUpdateEvent.setSelectionDelta(polylineSelectionManager.getDelta());
		selectionUpdateEvent.setSender(this);
		eventPublisher.triggerEvent(selectionUpdateEvent);
		// send out a major update which tells the hhm to update its textures
		UpdateViewEvent updateView = new UpdateViewEvent();
		updateView.setSender(this);
		eventPublisher.triggerEvent(updateView);
	}

	@Override
	protected void handlePickingEvents(final EPickingType ePickingType, final EPickingMode ePickingMode,
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

						// Prevent handling of non genetic data in context menu
						if (dataDomain != EDataDomain.GENETIC_DATA)
							break;

						GeneContextMenuItemContainer geneContextMenuItemContainer =
							new GeneContextMenuItemContainer();
						geneContextMenuItemContainer.setID(EIDType.EXPRESSION_INDEX, iExternalID);
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

				// if (ePolylineDataType == EIDType.EXPRESSION_INDEX && !bAngularBrushingSelectPolyline) {
				if (!bAngularBrushingSelectPolyline) {
					//
					// SelectionCommand command =
					// new SelectionCommand(ESelectionCommandType.CLEAR, eSelectionType);
					// // sendSelectionCommandEvent(EIDType.EXPRESSION_INDEX, command);

					ISelectionDelta selectionDelta = polylineSelectionManager.getDelta();
					handleConnectedElementRep(selectionDelta);
					SelectionUpdateEvent event = new SelectionUpdateEvent();
					event.setSender(this);
					event.setSelectionDelta((SelectionDelta) selectionDelta);
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
					case RIGHT_CLICKED:
						eSelectionType = ESelectionType.SELECTION;
						if (!isRenderedRemote()) {
							contextMenu.setLocation(pick.getPickedPoint(), getParentGLCanvas().getWidth(),
								getParentGLCanvas().getHeight());
							contextMenu.setMasterGLView(this);
						}
						ExperimentContextMenuItemContainer experimentContextMenuItemContainer =
							new ExperimentContextMenuItemContainer();
						experimentContextMenuItemContainer.setID(iExternalID);
						contextMenu.addItemContanier(experimentContextMenuItemContainer);

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
				if (eAxisDataType == EIDType.EXPRESSION_INDEX || eAxisDataType == EIDType.EXPERIMENT_INDEX) {
					handleConnectedElementRep(selectionDelta);
				}
				SelectionUpdateEvent event = new SelectionUpdateEvent();
				event.setSender(this);
				event.setSelectionDelta((SelectionDelta) selectionDelta);
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
						if (axisVA.containsElement(axisVA.get(iExternalID)) == 1) {
							hashGates.remove(axisVA.get(iExternalID));
						}
						axisVA.remove(iExternalID);
						axisSelectionManager.remove(iExternalID, false);
						IVirtualArrayDelta vaDelta =
							new VirtualArrayDelta(axisVAType, EIDType.EXPERIMENT_INDEX);
						vaDelta.add(VADeltaItem.remove(iExternalID));
						sendVirtualArrayUpdateEvent(vaDelta);
						setDisplayListDirty();
						resetAxisSpacing();

						// NewGroupInfoEvent newGroupInfoEvent = new NewGroupInfoEvent();
						// newGroupInfoEvent.setSender(this);
						// newGroupInfoEvent.setEVAType(axisVAType);
						// newGroupInfoEvent.setGroupList(null);
						// newGroupInfoEvent.setDeleteTree(true);
						// eventPublisher.triggerEvent(newGroupInfoEvent);

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
							axisVA.copy(iExternalID);
							IVirtualArrayDelta vaDelta =
								new VirtualArrayDelta(axisVAType, EIDType.EXPERIMENT_INDEX);
							vaDelta.add(VADeltaItem.copy(iExternalID));
							sendVirtualArrayUpdateEvent(vaDelta);

							// NewGroupInfoEvent newGroupInfoEvent = new NewGroupInfoEvent();
							// newGroupInfoEvent.setSender(this);
							// newGroupInfoEvent.setEVAType(axisVAType);
							// newGroupInfoEvent.setGroupList(null);
							// newGroupInfoEvent.setDeleteTree(true);
							// eventPublisher.triggerEvent(newGroupInfoEvent);

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
							iGateCount = 0;
						}
						else {
							iGateCount++;
						}
						hashNumberOfGatesPerAxisID.put(iExternalID, iGateCount);
						int iGateID = iExternalID * 1000 + iGateCount;
						AGate gate;
						if (set.isSetHomogeneous()) {
							gate =
								new Gate(iGateID, (float) set.getRawForNormalized(0), (float) set
									.getRawForNormalized(0.5f), set, renderStyle);
						}
						else {
							gate = new NominalGate(iGateID, 0, 0.5f, set, renderStyle);
						}
						hashGates.put(iGateID, gate);
						// hashGates.put(iGateID, new Pair<Float, Float>(0f, renderStyle.getAxisHeight() /
						// 2f));

						hashIsGateBlocking.put(iGateID, new ArrayList<Integer>());
						handleUnselection();
						triggerSelectionUpdate();
						setDisplayListDirty();

						break;
				}
				break;
			case ADD_MASTER_GATE:
				switch (ePickingMode) {
					case CLICKED:
						Gate gate =
							new Gate(++iNumberOfMasterGates, (float) set.getRawForNormalized(0), (float) set
								.getRawForNormalized(0.5f), set, renderStyle);
						hashMasterGates.put(iNumberOfMasterGates, gate);
						hashIsGateBlocking.put(iNumberOfMasterGates, new ArrayList<Integer>());
						handleUnselection();
						triggerSelectionUpdate();
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
						}
						else {
							hashMasterGates.remove(iExternalID);
							hashIsGateBlocking.remove(iExternalID);
						}
						handleUnselection();
						triggerSelectionUpdate();
						setDisplayListDirty();
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
		virtualArrayUpdateEvent.setVirtualArrayDelta((VirtualArrayDelta) delta);
		virtualArrayUpdateEvent.setInfo(getShortInfo());
		eventPublisher.triggerEvent(virtualArrayUpdateEvent);
	}

	@Override
	protected ArrayList<SelectedElementRep> createElementRep(EIDType idType, int iStorageIndex)
		throws InvalidAttributeValueException {

		ArrayList<SelectedElementRep> alElementReps = new ArrayList<SelectedElementRep>();

		float fXValue = 0;
		float fYValue = 0;

		if ((bRenderStorageHorizontally && idType == EIDType.EXPRESSION_INDEX || !bRenderStorageHorizontally
			&& idType == EIDType.EXPERIMENT_INDEX)
			&& ePolylineDataType != EIDType.EXPERIMENT_INDEX) {
			for (int iAxisNumber : axisVA.indicesOf(iStorageIndex)) {
				fXValue = iAxisNumber * renderStyle.getAxisSpacing(axisVA.size());
				fXValue = fXValue + renderStyle.getXSpacing();
				fYValue = renderStyle.getBottomSpacing();
				alElementReps.add(new SelectedElementRep(idType, iUniqueID, fXValue, fYValue, 0.0f));
			}
		}
		else {
			//added to create multiple connection points
			for (int count = 0; count < 3; count++) {
				// if (eAxisDataType == EIDType.EXPERIMENT_RECORD)
				// fXValue = viewFrustum.getRight() - 0.2f;
				// else
				// fXValue = viewFrustum.getRight() - 0.4f;
				if (renderConnectionsLeft) {
					
					//replaced to get multiple connection points
					fXValue = renderStyle.getXSpacing() + renderStyle.getAxisSpacing(axisVA.size())* axisVA.get(axisVA.size()-1)/2*count;
					fYValue =	set.get(storageVA.get(storageVA.size() - 1)/2*count).getFloat(EDataRepresentation.NORMALIZED, iStorageIndex);
					//fXValue = fXValue + renderStyle.getXSpacing();
					//fYValue = set.get(storageVA.get(0)).getFloat(EDataRepresentation.NORMALIZED, iStorageIndex);
	
				}
				else {
					if (eAxisDataType == EIDType.EXPERIMENT_RECORD)
						fXValue = viewFrustum.getRight() - 0.2f;
					else
						fXValue = viewFrustum.getRight() - 0.4f;
					fYValue =
						set.get(storageVA.get(storageVA.size() - 1)).getFloat(EDataRepresentation.NORMALIZED,
							iStorageIndex);
				}
				
				// // get the value on the leftmost axis
				// fYValue = set.get(storageVA.get(0)).getFloat(EDataRepresentation.NORMALIZED, iStorageIndex);
	
				if (Float.isNaN(fYValue)) {
					fYValue = NAN_Y_OFFSET * renderStyle.getAxisHeight() + renderStyle.getBottomSpacing();
				}
				else {
					fYValue = fYValue * renderStyle.getAxisHeight() + renderStyle.getBottomSpacing();
				}
				alElementReps.add(new SelectedElementRep(idType, iUniqueID,fXValue, fYValue, 0.0f));
			}
			
		//	
		}
		return alElementReps;
	}

	@Override
	public String getShortInfo() {
		String message;
		int iNumLines =
			contentSelectionManager.getNumberOfElements(ESelectionType.NORMAL)
				+ contentSelectionManager.getNumberOfElements(ESelectionType.MOUSE_OVER)
				+ contentSelectionManager.getNumberOfElements(ESelectionType.SELECTION);
		if (iDisplayEveryNthPolyline == 1) {
			message =
				"Parallel Coordinates - " + iNumLines + " " + useCase.getContentLabel(false, true) + " / "
					+ storageVA.size() + " experiments";
		}
		else {
			message =
				"Parallel Coordinates - a sample of " + iNumLines / iDisplayEveryNthPolyline + " out of "
					+ iNumLines + " " + useCase.getContentLabel(false, true) + " / \n " + storageVA.size()
					+ " experiments";
		}
		return message;

	}

	@Override
	public String getDetailedInfo() {
		StringBuffer sInfoText = new StringBuffer();
		sInfoText.append("<b>Type:</b> Parallel Coordinates\n");
		sInfoText.append(polylineVA.size() + useCase.getContentLabel(false, true) + " as polylines and "
			+ axisVA.size() + " experiments as axis.\n");

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
				sInfoText.append("Showing all " + useCase.getContentLabel(false, true) + " in the dataset\n");
			}
			else if (dataFilterLevel == EDataFilterLevel.ONLY_MAPPING) {
				sInfoText.append("Showing all " + useCase.getContentLabel(false, true)
					+ " that have a known DAVID ID mapping\n");
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
		// virtualArray = contentVA;
		// else
		// virtualArray = storageVA;
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

		iAxisLeftIndex = axisVA.get(iPosition);
		iAxisRightIndex = axisVA.get(iPosition + 1);

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

		for (Integer iCurrent : polylineVA) {
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

	/**
	 * Changes the role of axes and polylines.
	 * 
	 * @param defaultOrientation
	 *            the default orientation is for content to be polylines and for storages to be the axes
	 */
	@Override
	public void changeOrientation(boolean defaultOrientation) {
		if (defaultOrientation != this.bRenderStorageHorizontally) {
			if (defaultOrientation && contentVA.size() > 100) {

				getParentGLCanvas().getParentComposite().getDisplay().asyncExec(new Runnable() {

					@Override
					public void run() {
						MessageDialog.openError(getParentGLCanvas().getParentComposite().getShell(),
							"Axis Limit",
							"Can not show more than 100 axis - reduce polylines to less than 100 first");
						return;
					}
				});

				return;

			}
		}

		this.bRenderStorageHorizontally = defaultOrientation;

		connectedElementRepresentationManager.clear(EIDType.EXPRESSION_INDEX);
		initContentVariables();

		resetAxisSpacing();
		initGates();

		setDisplayListDirty();
	}

	@Override
	public boolean isInDefaultOrientation() {
		return bRenderStorageHorizontally;
	}

	private void adjustAxisSpacing(GL gl) {

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

			IVirtualArrayDelta vaDelta = new VirtualArrayDelta(axisVAType, EIDType.EXPERIMENT_INDEX);
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

	private void handleTrackInput(final GL gl) {

		// TODO: very performance intensive - better solution needed (only in reshape)!
		getParentGLCanvas().getParentComposite().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				upperLeftScreenPos = getParentGLCanvas().getParentComposite().toDisplay(1, 1);
			}
		});

		Rectangle screenRect = getParentGLCanvas().getBounds();
		float[] fArTrackPos = generalManager.getTrackDataProvider().getEyeTrackData();

		fArTrackPos[0] -= upperLeftScreenPos.x;
		fArTrackPos[1] -= upperLeftScreenPos.y;

		// GLHelperFunctions.drawPointAt(gl, new Vec3f(fArTrackPos[0] / screenRect.width * 8f,
		// (1f - fArTrackPos[1] / screenRect.height) * 8f * fAspectRatio, 0.01f));

		float fTrackX = (generalManager.getTrackDataProvider().getEyeTrackData()[0]) / screenRect.width;

		fTrackX *= renderStyle.getWidthOfCoordinateSystem();

		int iAxisNumber = 0;
		for (int iCount = 0; iCount < alAxisSpacing.size() - 1; iCount++) {
			if (alAxisSpacing.get(iCount) < fTrackX && alAxisSpacing.get(iCount + 1) > fTrackX) {
				if (fTrackX - alAxisSpacing.get(iCount) < alAxisSpacing.get(iCount) - fTrackX) {
					iAxisNumber = iCount;
				}
				else {
					iAxisNumber = iCount + 1;
				}

				break;
			}
		}

		int iNumberOfAxis = axisVA.size();

		float fOriginalAxisSpacing = renderStyle.getAxisSpacing(iNumberOfAxis);

		float fFocusAxisSpacing = fOriginalAxisSpacing * 2;

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

//	private void focusOnAreaWii() {
//		if (!generalManager.isWiiModeActive())
//			return;
//
//		WiiRemote wii = generalManager.getWiiRemote();
//
//		float fXWiiPosition = wii.getCurrentSmoothHeadPosition()[0] + 1f;
//
//		// we assume that this is far right, and -fMax is far left
//		float fMaxX = 2;
//
//		if (fXWiiPosition > fMaxX) {
//			fXWiiPosition = fMaxX;
//		}
//		else if (fXWiiPosition < -fMaxX) {
//			fXWiiPosition = -fMaxX;
//		}
//
//		// now we normalize to 0 to 1
//		fXWiiPosition = (fXWiiPosition + fMaxX) / (2 * fMaxX);
//
//		fXWiiPosition *= renderStyle.getWidthOfCoordinateSystem();
//		int iAxisNumber = 0;
//		for (int iCount = 0; iCount < alAxisSpacing.size() - 1; iCount++) {
//			if (alAxisSpacing.get(iCount) < fXWiiPosition && alAxisSpacing.get(iCount + 1) > fXWiiPosition) {
//				if (fXWiiPosition - alAxisSpacing.get(iCount) < alAxisSpacing.get(iCount) - fXWiiPosition) {
//					iAxisNumber = iCount;
//				}
//				else {
//					iAxisNumber = iCount + 1;
//				}
//
//				break;
//			}
//		}
//
//		int iNumberOfAxis = axisVA.size();
//
//		float fOriginalAxisSpacing = renderStyle.getAxisSpacing(iNumberOfAxis);
//
//		float fFocusAxisSpacing = 2 * fOriginalAxisSpacing;
//
//		float fReducedSpacing =
//			(renderStyle.getWidthOfCoordinateSystem() - 2 * fFocusAxisSpacing) / (iNumberOfAxis - 3);
//
//		float fCurrentX = 0;
//		alAxisSpacing.clear();
//		for (int iCount = 0; iCount < iNumberOfAxis; iCount++) {
//			alAxisSpacing.add(fCurrentX);
//			if (iCount + 1 == iAxisNumber || iCount == iAxisNumber) {
//				fCurrentX += fFocusAxisSpacing;
//			}
//			else {
//				fCurrentX += fReducedSpacing;
//			}
//		}
//
//		setDisplayListDirty();
//	}

	public void resetAxisSpacing() {
		alAxisSpacing.clear();
		int iNumAxis = axisVA.size();
		float fInitAxisSpacing = renderStyle.getAxisSpacing(iNumAxis);
		for (int iCount = 0; iCount < iNumAxis; iCount++) {
			alAxisSpacing.add(fInitAxisSpacing * iCount);
		}
		setDisplayListDirty();
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedParallelCoordinatesView serializedForm = new SerializedParallelCoordinatesView(dataDomain);
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

	@Override
	public void handleUpdateView() {
		setDisplayListDirty();
	}

	@Override
	public void destroy() {
		selectionTransformer.destroy();
		super.destroy();
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();
		applyCurrentSelectionToVirtualArrayListener = new ApplyCurrentSelectionToVirtualArrayListener();
		applyCurrentSelectionToVirtualArrayListener.setHandler(this);
		eventPublisher.addListener(ApplyCurrentSelectionToVirtualArrayEvent.class,
			applyCurrentSelectionToVirtualArrayListener);

		resetAxisSpacingListener = new ResetAxisSpacingListener();
		resetAxisSpacingListener.setHandler(this);
		eventPublisher.addListener(ResetAxisSpacingEvent.class, resetAxisSpacingListener);

		bookmarkListener = new BookmarkButtonListener();
		bookmarkListener.setHandler(this);
		eventPublisher.addListener(BookmarkButtonEvent.class, bookmarkListener);

		resetViewListener = new ResetViewListener();
		resetViewListener.setHandler(this);
		eventPublisher.addListener(ResetAllViewsEvent.class, resetViewListener);
		// second event for same listener
		eventPublisher.addListener(ResetParallelCoordinatesEvent.class, resetViewListener);

		useRandomSamplingListener = new UseRandomSamplingListener();
		useRandomSamplingListener.setHandler(this);
		eventPublisher.addListener(UseRandomSamplingEvent.class, useRandomSamplingListener);

		changeOrientationListener = new ChangeOrientationListener();
		changeOrientationListener.setHandler(this);
		eventPublisher
			.addListener(ChangeOrientationParallelCoordinatesEvent.class, changeOrientationListener);

		preventOcclusionListener = new PreventOcclusionListener();
		preventOcclusionListener.setHandler(this);
		eventPublisher.addListener(PreventOcclusionEvent.class, preventOcclusionListener);

		angularBrushingListener = new AngularBrushingListener();
		angularBrushingListener.setHandler(this);
		eventPublisher.addListener(AngularBrushingEvent.class, angularBrushingListener);

	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();
		if (applyCurrentSelectionToVirtualArrayListener != null) {
			eventPublisher.removeListener(applyCurrentSelectionToVirtualArrayListener);
			applyCurrentSelectionToVirtualArrayListener = null;
		}

		if (resetAxisSpacingListener != null) {
			eventPublisher.removeListener(resetAxisSpacingListener);
			resetAxisSpacingListener = null;
		}

		if (bookmarkListener != null) {
			eventPublisher.removeListener(bookmarkListener);
			bookmarkListener = null;
		}
		if (resetViewListener != null) {
			eventPublisher.removeListener(resetViewListener);
			resetViewListener = null;
		}

		if (changeOrientationListener != null) {
			eventPublisher.removeListener(changeOrientationListener);
			changeOrientationListener = null;
		}

		if (preventOcclusionListener != null) {
			eventPublisher.removeListener(preventOcclusionListener);
			preventOcclusionListener = null;
		}

		if (angularBrushingListener != null) {
			eventPublisher.removeListener(angularBrushingListener);
			angularBrushingListener = null;
		}
	}

	@Override
	public void handleSelectionCommand(EIDCategory category, SelectionCommand selectionCommand) {
		if (category == ePolylineDataType.getCategory())
			polylineSelectionManager.executeSelectionCommand(selectionCommand);
		else if (category == eAxisDataType.getCategory())
			axisSelectionManager.executeSelectionCommand(selectionCommand);
		else
			return;

		setDisplayListDirty();
	}

	@Override
	public String toString() {
		int iNumElements =
			(contentSelectionManager.getNumberOfElements() - contentSelectionManager
				.getNumberOfElements(ESelectionType.DESELECTED));
		String renderMode = "standalone";
		if (isRenderedRemote())
			renderMode = "remote";
		return ("PCs, " + renderMode + ", " + iNumElements + " elements" + " Axis DT: " + eAxisDataType
			+ " Polyline DT:" + ePolylineDataType);
	}

	@Override
	public void setSet(ISet set) {
		super.setSet(set);
	}

	@Override
	public List<AGLView> getRemoteRenderedViews() {
		return new ArrayList<AGLView>();
	}

	public void setRenderConnectionState(boolean renderConnectionssLeft) {
		this.renderConnectionsLeft = renderConnectionssLeft;

	}
}
