package org.caleydo.view.parcoords;

import static org.caleydo.view.parcoords.PCRenderStyle.ANGLUAR_LINE_WIDTH;
import static org.caleydo.view.parcoords.PCRenderStyle.ANGULAR_COLOR;
import static org.caleydo.view.parcoords.PCRenderStyle.ANGULAR_POLYGON_COLOR;
import static org.caleydo.view.parcoords.PCRenderStyle.AXIS_MARKER_WIDTH;
import static org.caleydo.view.parcoords.PCRenderStyle.AXIS_Z;
import static org.caleydo.view.parcoords.PCRenderStyle.LABEL_Z;
import static org.caleydo.view.parcoords.PCRenderStyle.NAN_Y_OFFSET;
import static org.caleydo.view.parcoords.PCRenderStyle.NUMBER_AXIS_MARKERS;
import static org.caleydo.view.parcoords.PCRenderStyle.X_AXIS_COLOR;
import static org.caleydo.view.parcoords.PCRenderStyle.X_AXIS_LINE_WIDTH;
import static org.caleydo.view.parcoords.PCRenderStyle.Y_AXIS_COLOR;
import static org.caleydo.view.parcoords.PCRenderStyle.Y_AXIS_LINE_WIDTH;
import static org.caleydo.view.parcoords.PCRenderStyle.Y_AXIS_MOUSE_OVER_LINE_WIDTH;
import static org.caleydo.view.parcoords.PCRenderStyle.Y_AXIS_SELECTED_LINE_WIDTH;
import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.management.InvalidAttributeValueException;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.awt.GLCanvas;

import org.caleydo.core.data.collection.storage.AStorage;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.collection.storage.NominalStorage;
import org.caleydo.core.data.collection.storage.NumericalStorage;
import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.filter.ContentFilter;
import org.caleydo.core.data.filter.StorageFilter;
import org.caleydo.core.data.filter.event.NewContentFilterEvent;
import org.caleydo.core.data.filter.event.NewStorageFilterEvent;
import org.caleydo.core.data.id.IDCategory;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.id.ManagedObjectType;
import org.caleydo.core.data.selection.SelectedElementRep;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.EVAOperation;
import org.caleydo.core.data.virtualarray.delta.ContentVADelta;
import org.caleydo.core.data.virtualarray.delta.StorageVADelta;
import org.caleydo.core.data.virtualarray.delta.VADeltaItem;
import org.caleydo.core.gui.preferences.PreferenceConstants;
import org.caleydo.core.manager.datadomain.EDataFilterLevel;
import org.caleydo.core.manager.datadomain.IDataDomain;
import org.caleydo.core.manager.event.data.BookmarkEvent;
import org.caleydo.core.manager.event.view.ResetAllViewsEvent;
import org.caleydo.core.manager.event.view.infoarea.InfoAreaUpdateEvent;
import org.caleydo.core.manager.event.view.storagebased.AngularBrushingEvent;
import org.caleydo.core.manager.event.view.storagebased.ApplyCurrentSelectionToVirtualArrayEvent;
import org.caleydo.core.manager.event.view.storagebased.BookmarkButtonEvent;
import org.caleydo.core.manager.event.view.storagebased.ResetAxisSpacingEvent;
import org.caleydo.core.manager.event.view.storagebased.ResetParallelCoordinatesEvent;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.event.view.storagebased.UpdateViewEvent;
import org.caleydo.core.manager.event.view.storagebased.UseRandomSamplingEvent;
import org.caleydo.core.manager.picking.PickingMode;
import org.caleydo.core.manager.picking.PickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.manager.view.StandardTransformer;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.format.Formatter;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.ATableBasedView;
import org.caleydo.core.view.opengl.canvas.DetailLevel;
import org.caleydo.core.view.opengl.canvas.listener.ResetViewListener;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.GLCoordinateUtils;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.container.ContentContextMenuItemContainer;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.container.StorageContextMenuItemContainer;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.view.parcoords.PCRenderStyle.PolyLineState;
import org.caleydo.view.parcoords.listener.AngularBrushingListener;
import org.caleydo.view.parcoords.listener.ApplyCurrentSelectionToVirtualArrayListener;
import org.caleydo.view.parcoords.listener.BookmarkButtonListener;
import org.caleydo.view.parcoords.listener.ResetAxisSpacingListener;
import org.caleydo.view.parcoords.listener.UseRandomSamplingListener;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;

import com.jogamp.common.nio.Buffers;

/**
 * This class is responsible for rendering the parallel coordinates
 * 
 * @author Alexander Lex (responsible for PC)
 * @author Marc Streit
 */
public class GLParallelCoordinates extends ATableBasedView implements
		IGLRemoteRenderingView {

	public final static String VIEW_TYPE = "org.caleydo.view.parcoords";

	private PickingType draggedObject;

	/**
	 * Hashes a gate id, which is made up of an axis id + the last three digits
	 * a gate counter (per axis) to a pair of values which make up the upper and
	 * lower gate tip
	 */
	private HashMap<Integer, AGate> hashGates;
	/**
	 * Hash of blocking gates
	 */
	private HashMap<Integer, ArrayList<Integer>> hashIsGateBlocking;

	/**
	 * HashMap for the gates that are used to remove selections across all axes,
	 * when the set is homogeneous
	 */
	private HashMap<Integer, Gate> hashMasterGates;

	/**
	 * Gate counter used for unique ID retrieval for gates. It is shared between
	 * regular and master gates
	 */
	private int iGateCounter = 0;

	/**
	 * HashMap that has flags for all the axes that have NAN
	 */
	private HashMap<Integer, Boolean> hashExcludeNAN;
	private HashMap<Integer, ArrayList<Integer>> hashIsNANBlocking;

	private ArrayList<ArrayList<Integer>> alIsAngleBlocking;

	private ArrayList<Float> axisSpacings;

	private int iDraggedGateNumber = 0;

	/** the spacing on the sides of the coordinates system */
	private float xSideSpacing = 0;

	private float fYTranslation = 0;

	private boolean bAngularBrushingSelectPolyline = false;
	private boolean bIsAngularBrushingActive = false;
	private boolean bIsAngularBrushingFirstTime = false;
	private boolean bIsAngularDraggingActive = false;

	private boolean bIsGateDraggingFirstTime = false;
	private boolean bIsDraggingActive = false;
	private boolean hasFilterChanged = false;

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

	// private SelectedElementRep elementRep;

	// private int iPolylineVAID = 0;

	// private int iAxisVAID = 0;

	protected PCRenderStyle renderStyle;

	private int displayEveryNthPolyline = 1;

	EIconTextures dropTexture = EIconTextures.DROP_NORMAL;
	int iChangeDropOnAxisNumber = -1;

	/** Utility object for coordinate transformation and projection */
	protected StandardTransformer selectionTransformer;

	// listeners
	private ApplyCurrentSelectionToVirtualArrayListener applyCurrentSelectionToVirtualArrayListener;
	private ResetAxisSpacingListener resetAxisSpacingListener;
	private BookmarkButtonListener bookmarkListener;
	private ResetViewListener resetViewListener;
	private UseRandomSamplingListener useRandomSamplingListener;
	private AngularBrushingListener angularBrushingListener;

	protected int[] vertexBufferIndices = new int[] { -1 };

	private org.eclipse.swt.graphics.Point upperLeftScreenPos = new org.eclipse.swt.graphics.Point(
			0, 0);

	/**
	 * FIXME: remove after data flipper video
	 */
	private boolean renderConnectionsLeft = true;

	/**
	 * Constructor.
	 */
	public GLParallelCoordinates(GLCanvas glCanvas, Composite parentComposite, ViewFrustum viewFrustum) {

		super(glCanvas, parentComposite, viewFrustum);
		viewType = GLParallelCoordinates.VIEW_TYPE;
		renderStyle = new PCRenderStyle(this, viewFrustum);
		super.renderStyle = this.renderStyle;

		alIsAngleBlocking = new ArrayList<ArrayList<Integer>>();
		alIsAngleBlocking.add(new ArrayList<Integer>());

		axisSpacings = new ArrayList<Float>();
		iNumberOfRandomElements = generalManager.getPreferenceStore().getInt(
				PreferenceConstants.PC_NUM_RANDOM_SAMPLING_POINT);

		// glSelectionHeatMap =
		// ((ViewManager)generalManager.getViewGLCanvasManager()).getSelectionHeatMap();

		icon = EIconTextures.PAR_COORDS_ICON;
	}

	@Override
	public void initLocal(final GL2 gl) {
		textRenderer = new CaleydoTextRenderer(24);
		iGLDisplayListIndexLocal = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexLocal;

		// glSelectionHeatMap.addSets(alSets);
		// glSelectionHeatMap.initRemote(gl, getID(),
		// glMouseListener,
		// remoteRenderingGLCanvas);

		selectionTransformer = new StandardTransformer(uniqueID);
		init(gl);
	}

	@Override
	public void initRemote(final GL2 gl, final AGLView glParentView,
			final GLMouseListener glMouseListener) {

		this.glMouseListener = glMouseListener;

		iGLDisplayListIndexRemote = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexRemote;

		selectionTransformer = new StandardTransformer(uniqueID);
		init(gl);
		// toggleRenderContext();
	}

	@Override
	public void init(final GL2 gl) {
		textRenderer = new CaleydoTextRenderer(24);

		initData();

		updateSpacings();
	}

	private void updateSpacings() {
		xSideSpacing = renderStyle.getXSpacing();
		fYTranslation = renderStyle.getBottomSpacing();
	}

	@Override
	public void initData() {
		super.initData();

		initGates();
		resetAxisSpacing();
	}

	@Override
	public void displayLocal(final GL2 gl) {

		if (table == null)
			return;

		if (!lazyMode)
			pickingManager.handlePicking(this, gl);

		if (bIsDisplayListDirtyLocal) {
			handleUnselection();
			buildDisplayList(gl, iGLDisplayListIndexLocal);
			bIsDisplayListDirtyLocal = false;
		}
		iGLDisplayListToCall = iGLDisplayListIndexLocal;

		display(gl);

		if (!lazyMode)
			checkForHits(gl);

		// ConnectedElementRepresentationManager cerm =
		// GeneralManager.get().getViewGLCanvasManager().getConnectedElementRepresentationManager();
		// cerm.doViewRelatedTransformation(gl, selectionTransformer);

		if (eBusyModeState != EBusyModeState.OFF) {
			renderBusyMode(gl);
		}
	}

	@Override
	public void displayRemote(final GL2 gl) {

		if (table == null)
			return;

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
	public void display(final GL2 gl) {

		processEvents();
		// displayVBO(gl);

		// setDetailLevel(DetailLevel.HIGH);

		// GLHelperFunctions.drawViewFrustum(gl, viewFrustum);

		gl.glEnable(GL2.GL_BLEND);

		if (generalManager.getTrackDataProvider().isTrackModeActive())
			handleTrackInput(gl);

		// TODO another display list
		// clipToFrustum(gl);

		gl.glTranslatef(xSideSpacing, fYTranslation, 0.0f);

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

		gl.glTranslatef(-xSideSpacing, -fYTranslation, 0.0f);

		if (!isRenderedRemote())
			contextMenu.render(gl, this);

	}

	public void triggerAngularBrushing() {
		bAngularBrushingSelectPolyline = true;
		setDisplayListDirty();
	}

	@Override
	public void renderContext(boolean bRenderOnlyContext) {
		this.bRenderOnlyContext = bRenderOnlyContext;

		if (bRenderOnlyContext) {
			contentVAType = DataTable.CONTENT_CONTEXT;
			contentVA = dataDomain.getContentVA(contentVAType);
		} else {
			contentVAType = DataTable.CONTENT;
			contentVA = dataDomain.getContentVA(contentVAType);
		}

		contentSelectionManager.setVA(contentVA);
		// initGates();
		clearAllSelections();

		setDisplayListDirty();
	}

	/**
	 * Reset all selections and deselections
	 */
	@Override
	public void clearAllSelections() {

		contentSelectionManager.clearSelections();
		storageSelectionManager.clearSelections();

		clearFilters();
		setDisplayListDirty();
		connectedElementRepresentationManager.clear(contentIDType);
	}

	/**
	 * Clears gates and angluar filter
	 */
	private void clearFilters() {
		initGates();
		// isEnabled = false;
		bIsAngularBrushingActive = false;

		for (ArrayList<Integer> alCurrent : alIsAngleBlocking) {
			alCurrent.clear();
		}
		for (ArrayList<Integer> alCurrent : hashIsGateBlocking.values()) {
			alCurrent.clear();
		}
		setDisplayListDirty();
	}

	/**
	 * Sends a bookmark event containing all elements which are currently
	 * visible in the pcs, if the number of elements is less than 20. If it's
	 * more than 20 an error message is displayed.
	 */
	public void bookmarkElements() {

		ContentVADelta delta = contentSelectionManager.getBroadcastVADelta();
		if (delta.size() > 20) {
			parentComposite.getDisplay()
					.asyncExec(new Runnable() {

						@Override
						public void run() {
							MessageDialog
									.openError(parentComposite.getShell(), "Bookmark Limit",
											"Can not bookmark more than 20 elements - reduce polylines to less than 20 first");

							return;
						}
					});
			return;
		}

		if (!isRenderedRemote()) {
			BookmarkEvent<Integer> bookmarkEvent = new BookmarkEvent<Integer>(
					contentIDType);
			for (VADeltaItem item : delta.getAllItems()) {
				bookmarkEvent.addBookmark(item.getPrimaryID());
			}
			eventPublisher.triggerEvent(bookmarkEvent);
			resetAxisSpacing();
			setDisplayListDirty();
		}
	}

	public void saveSelection() {

		Set<Integer> removedElements = contentSelectionManager
				.getElements(SelectionType.DESELECTED);

		ContentVADelta delta = new ContentVADelta(contentVAType, contentIDType);
		for (Integer contentID : removedElements) {
			delta.add(VADeltaItem.removeElement(contentID));
		}

		contentSelectionManager.clearSelection(SelectionType.DESELECTED);
		clearFilters();
		triggerContentFilterEvent(delta, "Removed via gates");

	}

	/**
	 * Initializes the array lists that contain the data. Must be run at program
	 * start, every time you exchange axis and polylines and every time you
	 * change storages or selections *
	 */
	@Override
	protected void initLists() {

		if (bRenderOnlyContext)
			contentVAType = DataTable.CONTENT_CONTEXT;
		else
			contentVAType = DataTable.CONTENT;

		// contentVA = dataDomain.getContentVA(contentVAType);
		if (contentVA == null)
			contentVA = table.getContentData(contentVAType).getContentVA();
		if (storageVA == null)
			storageVA = table.getStorageData(storageVAType).getStorageVA();
		// storageVA = dataDomain.getStorageVA(storageVAType);

		contentSelectionManager.setVA(contentVA);
		storageSelectionManager.setVA(storageVA);

		initGates();
	}

	/**
	 * Initialize the gates. The gate heights are saved in two lists, which
	 * contain the rendering height of the gate
	 */
	private void initGates() {
		hashGates = new HashMap<Integer, AGate>();
		hashIsGateBlocking = new HashMap<Integer, ArrayList<Integer>>();
		if (table.isSetHomogeneous()) {
			hashMasterGates = new HashMap<Integer, Gate>();
		}
		hashExcludeNAN = new HashMap<Integer, Boolean>();
		hashIsNANBlocking = new HashMap<Integer, ArrayList<Integer>>();
	}

	/**
	 * Build polyline display list. Renders coordinate system, polylines and
	 * gates, by calling the render methods
	 * 
	 * @param gl
	 *            GL2 context
	 * @param iGLDisplayListIndex
	 *            the index of the display list
	 */
	private void buildDisplayList(final GL2 gl, int iGLDisplayListIndex) {
		gl.glNewList(iGLDisplayListIndex, GL2.GL_COMPILE);

		if (contentVA.size() == 0) {
			gl.glTranslatef(-xSideSpacing, -fYTranslation, 0.0f);
			renderSymbol(gl, EIconTextures.PAR_COORDS_SYMBOL, 2);
			gl.glTranslatef(+xSideSpacing, fYTranslation, 0.0f);
		} else {

			if (table.isSetHomogeneous() && !isRenderedRemote()) {
				renderMasterGate(gl);
			}

			renderCoordinateSystem(gl);

			for (SelectionType selectionType : contentSelectionManager
					.getSelectionTypes()) {
				if (selectionType.isVisible()) {
					if (selectionType == SelectionType.NORMAL)
						renderNormalPolylines(gl, selectionType);
					else
						renderSelectedPolylines(gl, selectionType);
				}
			}
			renderGates(gl);
		}
		gl.glEndList();
	}

	/**
	 * Polyline rendering method. All polylines that are contained in the
	 * polylineSelectionManager and are of the selection type specified in
	 * renderMode
	 * 
	 * FIXME this needs to be changed to iterate over the virtual array,
	 * considering the deselected elements
	 * 
	 * @param gl
	 *            the GL2 context
	 * @param renderMode
	 *            the type of selection in the selection manager to render
	 */
	private void renderNormalPolylines(GL2 gl, SelectionType selectionType) {

		int nrVisibleLines = contentVA.size()
				- contentSelectionManager.getNumberOfElements(SelectionType.DESELECTED);

		displayEveryNthPolyline = (contentVA.size() - contentSelectionManager
				.getNumberOfElements(SelectionType.DESELECTED)) / iNumberOfRandomElements;

		if (displayEveryNthPolyline == 0) {
			displayEveryNthPolyline = 1;
		}

		PolyLineState renderState = renderStyle.getPolyLineState(selectionType,
				nrVisibleLines / displayEveryNthPolyline);

		// this loop executes once per polyline
		for (int contentIndex = 0; contentIndex < contentVA.size(); contentIndex += displayEveryNthPolyline) {
			int contentID = contentVA.get(contentIndex);
			if (!contentSelectionManager.checkStatus(SelectionType.DESELECTED, contentID))
				renderSingleLine(gl, contentID, selectionType, renderState, false);
		}
	}

	private void renderSelectedPolylines(GL2 gl, SelectionType selectionType) {
		if (!selectionType.isVisible())
			return;
		int nrVisibleLines = contentSelectionManager.getNumberOfElements(selectionType);
		Set<Integer> lines = contentSelectionManager.getElements(selectionType);
		boolean renderAsSelection = true;
		if (lines.size() > 1)
			renderAsSelection = false;
		PolyLineState renderState = renderStyle.getPolyLineState(selectionType,
				nrVisibleLines / displayEveryNthPolyline);
		renderState.updateOcclusionPrev(nrVisibleLines);
		for (Integer contentID : lines) {

			if (contentVA.contains(contentID))
				renderSingleLine(gl, contentID, selectionType, renderState,
						renderAsSelection);
		}
	}

	private void renderSingleLine(GL2 gl, Integer contentID, SelectionType selectionType,
			PolyLineState renderState, boolean renderCaption) {

		gl.glColor4fv(renderState.color, 0);
		gl.glLineWidth(renderState.lineWidth);
		if (!(detailLevel == DetailLevel.HIGH || detailLevel == DetailLevel.MEDIUM))
			renderCaption = false;

		AStorage currentStorage = null;

		float previousX = 0;
		float previousY = 0;
		float currentX = 0;
		float currentY = 0;

		if (selectionType != SelectionType.DESELECTED) {
			gl.glPushName(pickingManager.getPickingID(uniqueID,
					PickingType.POLYLINE_SELECTION, contentID));
		}

		if (!renderCaption) {
			gl.glBegin(GL2.GL_LINE_STRIP);
		}

		// this loop executes once per axis
		for (int storageCount = 0; storageCount < storageVA.size(); storageCount++) {

			currentStorage = table.get(storageVA.get(storageCount));

			currentX = axisSpacings.get(storageCount);
			currentY = currentStorage.getFloat(EDataRepresentation.NORMALIZED, contentID);
			if (Float.isNaN(currentY)) {
				currentY = NAN_Y_OFFSET / renderStyle.getAxisHeight();
			}
			if (storageCount != 0) {
				if (renderCaption) {
					gl.glBegin(GL2.GL_LINES);
				}

				gl.glVertex3f(previousX, previousY * renderStyle.getAxisHeight(),
						renderState.zDepth);
				gl.glVertex3f(currentX, currentY * renderStyle.getAxisHeight(),
						renderState.zDepth);

				if (renderCaption) {
					gl.glEnd();
				}

			}

			if (renderCaption) {
				String sRawValue;
				if (currentStorage instanceof NumericalStorage) {
					sRawValue = Formatter.formatNumber(currentStorage.getFloat(
							EDataRepresentation.RAW, contentID));

				} else if (currentStorage instanceof NominalStorage) {
					sRawValue = ((NominalStorage<String>) currentStorage)
							.getRaw(contentID);
				} else
					throw new IllegalStateException("Unknown Storage Type");

				renderBoxedYValues(gl, currentX, currentY * renderStyle.getAxisHeight(),
						sRawValue, selectionType);
			}

			previousX = currentX;
			previousY = currentY;
		}

		if (!renderCaption) {
			gl.glEnd();
		}

		if (selectionType != SelectionType.DESELECTED) {
			gl.glPopName();
		}

	}

	/**
	 * Render the coordinate system of the parallel coordinates, including the
	 * axis captions and axis-specific buttons
	 * 
	 * @param gl
	 *            the gl context
	 * @param iNumberAxis
	 */
	private void renderCoordinateSystem(GL2 gl) {

		textRenderer.setColor(0, 0, 0, 1);

		int numberOfAxis = storageVA.size();
		// draw X-Axis
		gl.glColor4fv(X_AXIS_COLOR, 0);
		gl.glLineWidth(X_AXIS_LINE_WIDTH);

		gl.glPushName(pickingManager.getPickingID(uniqueID,
				PickingType.X_AXIS_SELECTION, 1));
		gl.glBegin(GL2.GL_LINES);

		gl.glVertex3f(renderStyle.getXAxisStart(), 0.0f, 0.0f);
		gl.glVertex3f(renderStyle.getXAxisEnd(), 0.0f, 0.0f);

		gl.glEnd();
		gl.glPopName();

		// draw all Y-Axis
		Set<Integer> selectedSet = storageSelectionManager
				.getElements(SelectionType.SELECTION);
		Set<Integer> mouseOverSet = storageSelectionManager
				.getElements(SelectionType.MOUSE_OVER);

		int iCount = 0;
		while (iCount < numberOfAxis) {
			float fXPosition = axisSpacings.get(iCount);
			if (selectedSet.contains(storageVA.get(iCount))) {
				gl.glColor4fv(SelectionType.SELECTION.getColor(), 0);
				gl.glLineWidth(Y_AXIS_SELECTED_LINE_WIDTH);
				gl.glEnable(GL2.GL_LINE_STIPPLE);
				gl.glLineStipple(2, (short) 0xAAAA);
			} else if (mouseOverSet.contains(storageVA.get(iCount))) {
				gl.glColor4fv(SelectionType.MOUSE_OVER.getColor(), 0);
				gl.glLineWidth(Y_AXIS_MOUSE_OVER_LINE_WIDTH);
				gl.glEnable(GL2.GL_LINE_STIPPLE);
				gl.glLineStipple(2, (short) 0xAAAA);
			} else {
				gl.glColor4fv(Y_AXIS_COLOR, 0);
				gl.glLineWidth(Y_AXIS_LINE_WIDTH);
			}

			int axisPickingID = pickingManager.getPickingID(uniqueID,
					PickingType.Y_AXIS_SELECTION, storageVA.get(iCount));
			gl.glPushName(axisPickingID);
			gl.glBegin(GL2.GL_LINES);
			gl.glVertex3f(fXPosition, 0, AXIS_Z);
			gl.glVertex3f(fXPosition, renderStyle.getAxisHeight(), AXIS_Z);

			// Top marker
			gl.glVertex3f(fXPosition - AXIS_MARKER_WIDTH, renderStyle.getAxisHeight(),
					AXIS_Z);
			gl.glVertex3f(fXPosition + AXIS_MARKER_WIDTH, renderStyle.getAxisHeight(),
					AXIS_Z);

			gl.glEnd();
			gl.glDisable(GL2.GL_LINE_STIPPLE);
			gl.glPopName();

			// if (detailLevel == DetailLevel.LO) {
			if (!isRenderedRemote()) {
				// markers on axis
				float fMarkerSpacing = renderStyle.getAxisHeight()
						/ (NUMBER_AXIS_MARKERS + 1);
				for (int iInnerCount = 1; iInnerCount <= NUMBER_AXIS_MARKERS; iInnerCount++) {
					float fCurrentHeight = fMarkerSpacing * iInnerCount;
					if (iCount == 0) {
						if (table.isSetHomogeneous()) {
							float fNumber = (float) table
									.getRawForNormalized(fCurrentHeight
											/ renderStyle.getAxisHeight());

							Rectangle2D bounds = textRenderer.getScaledBounds(gl,
									Formatter.formatNumber(fNumber),
									renderStyle.getSmallFontScalingFactor(),
									PCRenderStyle.MIN_NUMBER_TEXT_SIZE);
							float fWidth = (float) bounds.getWidth();
							float fHeightHalf = (float) bounds.getHeight() / 3.0f;

							renderNumber(gl, Formatter.formatNumber(fNumber), fXPosition
									- fWidth - AXIS_MARKER_WIDTH, fCurrentHeight
									- fHeightHalf);
						} else {
							// TODO: storage based access
						}
					}
					gl.glColor3fv(Y_AXIS_COLOR, 0);
					gl.glBegin(GL2.GL_LINES);
					gl.glVertex3f(fXPosition - AXIS_MARKER_WIDTH, fCurrentHeight, AXIS_Z);
					gl.glVertex3f(fXPosition + AXIS_MARKER_WIDTH, fCurrentHeight, AXIS_Z);
					gl.glEnd();

				}
			}

			String sAxisLabel = null;

			sAxisLabel = table.get(storageVA.get(iCount)).getLabel();

			gl.glTranslatef(fXPosition,
					renderStyle.getAxisHeight() + renderStyle.getAxisCaptionSpacing(), 0);

			float width = renderStyle.getAxisSpacing(storageVA.size());
			if (iCount == numberOfAxis - 1)
				width = fYTranslation;
			textRenderer.renderTextInBounds(gl, sAxisLabel, 0, 0, 0.02f, width,
					pixelGLConverter.getGLHeightForPixelHeight(10));

			gl.glTranslatef(-fXPosition,
					-(renderStyle.getAxisHeight() + renderStyle.getAxisCaptionSpacing()),
					0);

			if (table.isSetHomogeneous()) {
				// textRenderer.begin3DRendering();
				//
				// // render values on top and bottom of axis
				//
				// // top
				// String text =
				// getDecimalFormat().format(set.getMax());
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
			} else {
				// TODO
			}

			if (!isRenderedRemote()) {

				float fXButtonOrigin = axisSpacings.get(iCount);

				Vec3f lowerLeftCorner = new Vec3f(fXButtonOrigin - 0.03f,
						PCRenderStyle.NAN_Y_OFFSET - 0.03f, PCRenderStyle.NAN_Z);
				Vec3f lowerRightCorner = new Vec3f(fXButtonOrigin + 0.03f,
						PCRenderStyle.NAN_Y_OFFSET - 0.03f, PCRenderStyle.NAN_Z);
				Vec3f upperRightCorner = new Vec3f(fXButtonOrigin + 0.03f,
						PCRenderStyle.NAN_Y_OFFSET + 0.03f, PCRenderStyle.NAN_Z);
				Vec3f upperLeftCorner = new Vec3f(fXButtonOrigin - 0.03f,
						PCRenderStyle.NAN_Y_OFFSET + 0.03f, PCRenderStyle.NAN_Z);
				Vec3f scalingPivot = new Vec3f(fXButtonOrigin,
						PCRenderStyle.NAN_Y_OFFSET, PCRenderStyle.NAN_Z);

				int iPickingID = pickingManager.getPickingID(uniqueID,
						PickingType.REMOVE_NAN, storageVA.get(iCount));
				gl.glPushName(iPickingID);

				textureManager.renderGUITexture(gl, EIconTextures.NAN, lowerLeftCorner,
						lowerRightCorner, upperRightCorner, upperLeftCorner,
						scalingPivot, 1, 1, 1, 1, 100);

				gl.glPopName();

				// render Buttons

				iPickingID = -1;
				float fYDropOrigin = -PCRenderStyle.AXIS_BUTTONS_Y_OFFSET;

				gl.glBlendFunc(GL2.GL_ONE, GL2.GL_ONE_MINUS_SRC_ALPHA);

				// the gate add button
				float fYGateAddOrigin = renderStyle.getAxisHeight();
				iPickingID = pickingManager.getPickingID(uniqueID, PickingType.ADD_GATE,
						storageVA.get(iCount));

				lowerLeftCorner.set(fXButtonOrigin - 0.03f, fYGateAddOrigin, AXIS_Z);
				lowerRightCorner.set(fXButtonOrigin + 0.03f, fYGateAddOrigin, AXIS_Z);
				upperRightCorner.set(fXButtonOrigin + 0.03f, fYGateAddOrigin + 0.12f,
						AXIS_Z);
				upperLeftCorner.set(fXButtonOrigin - 0.03f, fYGateAddOrigin + 0.12f,
						AXIS_Z);
				scalingPivot.set(fXButtonOrigin, fYGateAddOrigin, AXIS_Z);

				gl.glPushName(iPickingID);

				textureManager.renderGUITexture(gl, EIconTextures.ADD_GATE,
						lowerLeftCorner, lowerRightCorner, upperRightCorner,
						upperLeftCorner, scalingPivot, 1, 1, 1, 1, 100);

				gl.glPopName();

				if (selectedSet.contains(storageVA.get(iCount))
						|| mouseOverSet.contains(storageVA.get(iCount))) {

					lowerLeftCorner.set(fXButtonOrigin - 0.15f, fYDropOrigin - 0.3f,
							AXIS_Z + 0.005f);
					lowerRightCorner.set(fXButtonOrigin + 0.15f, fYDropOrigin - 0.3f,
							AXIS_Z + 0.005f);
					upperRightCorner.set(fXButtonOrigin + 0.15f, fYDropOrigin,
							AXIS_Z + 0.005f);
					upperLeftCorner.set(fXButtonOrigin - 0.15f, fYDropOrigin,
							AXIS_Z + 0.005f);
					scalingPivot.set(fXButtonOrigin, fYDropOrigin, AXIS_Z + 0.005f);

					// the mouse over drop
					if (iChangeDropOnAxisNumber == iCount) {
						// tempTexture = textureManager.getIconTexture(gl,
						// dropTexture);
						textureManager.renderGUITexture(gl, dropTexture, lowerLeftCorner,
								lowerRightCorner, upperRightCorner, upperLeftCorner,
								scalingPivot, 1, 1, 1, 1, 80);

						if (!bWasAxisMoved) {
							dropTexture = EIconTextures.DROP_NORMAL;
						}
					} else {
						textureManager.renderGUITexture(gl, EIconTextures.DROP_NORMAL,
								lowerLeftCorner, lowerRightCorner, upperRightCorner,
								upperLeftCorner, scalingPivot, 1, 1, 1, 1, 80);
					}

					iPickingID = pickingManager.getPickingID(uniqueID,
							PickingType.MOVE_AXIS, iCount);
					gl.glColor4f(0, 0, 0, 0f);
					gl.glPushName(iPickingID);
					gl.glBegin(GL2.GL_TRIANGLES);
					gl.glVertex3f(fXButtonOrigin, fYDropOrigin, AXIS_Z + 0.01f);
					gl.glVertex3f(fXButtonOrigin + 0.08f, fYDropOrigin - 0.3f,
							AXIS_Z + 0.01f);
					gl.glVertex3f(fXButtonOrigin - 0.08f, fYDropOrigin - 0.3f,
							AXIS_Z + 0.01f);
					gl.glEnd();
					gl.glPopName();

					iPickingID = pickingManager.getPickingID(uniqueID,
							PickingType.DUPLICATE_AXIS, iCount);
					// gl.glColor4f(0, 1, 0, 0.5f);
					gl.glPushName(iPickingID);
					gl.glBegin(GL2.GL_TRIANGLES);
					gl.glVertex3f(fXButtonOrigin, fYDropOrigin, AXIS_Z + 0.01f);
					gl.glVertex3f(fXButtonOrigin - 0.08f, fYDropOrigin - 0.21f,
							AXIS_Z + 0.01f);
					gl.glVertex3f(fXButtonOrigin - 0.23f, fYDropOrigin - 0.21f,
							AXIS_Z + 0.01f);
					gl.glEnd();
					gl.glPopName();

					iPickingID = pickingManager.getPickingID(uniqueID,
							PickingType.REMOVE_AXIS, iCount);
					// gl.glColor4f(0, 0, 1, 0.5f);
					gl.glPushName(iPickingID);
					gl.glBegin(GL2.GL_TRIANGLES);
					gl.glVertex3f(fXButtonOrigin, fYDropOrigin, AXIS_Z + 0.01f);
					gl.glVertex3f(fXButtonOrigin + 0.08f, fYDropOrigin - 0.21f,
							AXIS_Z + 0.01f);
					gl.glVertex3f(fXButtonOrigin + 0.23f, fYDropOrigin - 0.21f,
							AXIS_Z + 0.01f);
					gl.glEnd();
					gl.glPopName();

				} else {
					iPickingID = pickingManager.getPickingID(uniqueID,
							PickingType.MOVE_AXIS, iCount);

					gl.glPushAttrib(GL2.GL_CURRENT_BIT | GL2.GL_LINE_BIT);
					gl.glPushName(iPickingID);

					lowerLeftCorner.set(fXButtonOrigin - 0.05f, fYDropOrigin - 0.2f,
							AXIS_Z);
					lowerRightCorner.set(fXButtonOrigin + 0.05f, fYDropOrigin - 0.2f,
							AXIS_Z);
					upperRightCorner.set(fXButtonOrigin + 0.05f, fYDropOrigin, AXIS_Z);
					upperLeftCorner.set(fXButtonOrigin - 0.05f, fYDropOrigin, AXIS_Z);
					scalingPivot.set(fXButtonOrigin, fYDropOrigin, AXIS_Z);

					textureManager.renderGUITexture(gl, EIconTextures.SMALL_DROP,
							lowerLeftCorner, lowerRightCorner, upperRightCorner,
							upperLeftCorner, scalingPivot, 1, 1, 1, 1, 80);

					gl.glPopName();
					gl.glPopAttrib();

				}
				gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
			}

			iCount++;
		}
	}

	/**
	 * Render the gates and update the fArGateHeights for the
	 * selection/unselection
	 * 
	 * @param gl
	 * @param iNumberAxis
	 */
	private void renderGates(GL2 gl) {

		if (detailLevel != DetailLevel.HIGH)
			return;

		for (Integer iGateID : hashGates.keySet()) {
			// Gate ID / 1000 is axis ID

			AGate gate = hashGates.get(iGateID);
			int iAxisID = gate.getAxisID();
			// Pair<Float, Float> gate = hashGates.get(iGateID);
			// TODO for all indices

			ArrayList<Integer> iAlAxisIndex = storageVA.indicesOf(iAxisID);
			for (int iAxisIndex : iAlAxisIndex) {
				float fCurrentPosition = axisSpacings.get(iAxisIndex);
				gate.setCurrentPosition(fCurrentPosition);
				// String label = set.get(iAxisID).getLabel();

				gate.draw(gl, pickingManager, textureManager, textRenderer, uniqueID);
				// renderSingleGate(gl, gate, iAxisID, iGateID,
				// fCurrentPosition);
			}
		}

	}

	private void renderMasterGate(GL2 gl) {
		if (detailLevel != DetailLevel.HIGH)
			return;

		gl.glColor4f(0, 0, 0, 1f);
		gl.glLineWidth(PCRenderStyle.Y_AXIS_LINE_WIDTH);
		// gl.glPushName(iPickingID);

		float fXOrigin = pixelGLConverter.getGLWidthForPixelWidth(0);

		gl.glBegin(GL2.GL_LINES);
		gl.glVertex3f(fXOrigin, 0, AXIS_Z);
		gl.glVertex3f(fXOrigin, renderStyle.getAxisHeight(), AXIS_Z);
		gl.glVertex3f(fXOrigin - AXIS_MARKER_WIDTH, 0, AXIS_Z);
		gl.glVertex3f(fXOrigin + AXIS_MARKER_WIDTH, 0, AXIS_Z);
		gl.glVertex3f(fXOrigin - AXIS_MARKER_WIDTH, renderStyle.getAxisHeight(), AXIS_Z);
		gl.glVertex3f(fXOrigin + AXIS_MARKER_WIDTH, renderStyle.getAxisHeight(), AXIS_Z);
		gl.glEnd();

		gl.glBlendFunc(GL2.GL_ONE, GL2.GL_ONE_MINUS_SRC_ALPHA);

		// the gate add button
		float fYGateAddOrigin = renderStyle.getAxisHeight();
		int iPickingID = pickingManager.getPickingID(uniqueID,
				PickingType.ADD_MASTER_GATE, 1);
		gl.glPushName(iPickingID);

		Vec3f lowerLeftCorner = new Vec3f(fXOrigin - 0.05f, fYGateAddOrigin, AXIS_Z);
		Vec3f lowerRightCorner = new Vec3f(fXOrigin + 0.05f, fYGateAddOrigin, AXIS_Z);
		Vec3f upperRightCorner = new Vec3f(fXOrigin + 0.05f, fYGateAddOrigin + 0.2f,
				AXIS_Z);
		Vec3f upperLeftCorner = new Vec3f(fXOrigin - 0.05f, fYGateAddOrigin + 0.2f,
				AXIS_Z);
		Vec3f scalingPivot = new Vec3f(fXOrigin, fYGateAddOrigin, AXIS_Z);

		textureManager.renderGUITexture(gl, EIconTextures.ADD_GATE, lowerLeftCorner,
				lowerRightCorner, upperRightCorner, upperLeftCorner, scalingPivot, 1, 1,
				1, 1, 100);

		gl.glPopName();

		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

		for (Integer iGateID : hashMasterGates.keySet()) {
			Gate gate = hashMasterGates.get(iGateID);

			Float fBottom = gate.getBottom();
			Float fTop = gate.getTop();

			gl.glColor4fv(PCRenderStyle.GATE_BODY_COLOR, 0);
			gl.glBegin(GL2.GL_POLYGON);
			gl.glVertex3f(fXOrigin, fBottom, 0);
			gl.glVertex3f(viewFrustum.getWidth() - 1, fBottom, 0);
			gl.glVertex3f(viewFrustum.getWidth() - 1, fTop, 0);
			// TODO eurovis hacke
			// gl.glVertex3f(viewFrustum.getWidth(), fBottom, 0);
			// gl.glVertex3f(viewFrustum.getWidth(), fTop, 0);
			//
			gl.glVertex3f(fXOrigin - 0.05f, fTop, 0);
			gl.glEnd();

			gate.setCurrentPosition(fXOrigin);
			gate.draw(gl, pickingManager, textureManager, textRenderer, uniqueID);
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
	private void renderBoxedYValues(GL2 gl, float fXOrigin, float fYOrigin,
			String sRawValue, SelectionType renderMode) {

		float fScaling = renderStyle.getSmallFontScalingFactor();

		// don't render values that are below the y axis
		if (fYOrigin < 0)
			return;

		gl.glPushAttrib(GL2.GL_CURRENT_BIT | GL2.GL_LINE_BIT);
		gl.glLineWidth(Y_AXIS_LINE_WIDTH);
		gl.glColor4fv(Y_AXIS_COLOR, 0);

		Rectangle2D tempRectangle = textRenderer.getScaledBounds(gl, sRawValue, fScaling,
				PCRenderStyle.MIN_NUMBER_TEXT_SIZE);
		float fSmallSpacing = renderStyle.getVerySmallSpacing();
		float fBackPlaneWidth = (float) tempRectangle.getWidth();
		float maxWidth = renderStyle.getAxisSpacing(storageVA.size());
		if (fBackPlaneWidth > maxWidth)
			fBackPlaneWidth = maxWidth;

		float fBackPlaneHeight = (float) tempRectangle.getHeight();
		float fXTextOrigin = fXOrigin + 2 * AXIS_MARKER_WIDTH;
		float fYTextOrigin = fYOrigin;

		gl.glColor4f(1f, 1f, 1f, 0.8f);
		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex3f(fXTextOrigin - fSmallSpacing, fYTextOrigin - fSmallSpacing, LABEL_Z);
		gl.glVertex3f(fXTextOrigin + fBackPlaneWidth, fYTextOrigin - fSmallSpacing,
				LABEL_Z);
		gl.glVertex3f(fXTextOrigin + fBackPlaneWidth, fYTextOrigin + fBackPlaneHeight,
				LABEL_Z);
		gl.glVertex3f(fXTextOrigin - fSmallSpacing, fYTextOrigin + fBackPlaneHeight,
				LABEL_Z);
		gl.glEnd();

		textRenderer.renderTextInBounds(gl, sRawValue, fXTextOrigin, fYTextOrigin,
				PCRenderStyle.TEXT_ON_LABEL_Z, fBackPlaneWidth, fBackPlaneHeight);
		// renderNumber(gl, sRawValue, fXTextOrigin, fYTextOrigin);
		gl.glPopAttrib();
	}

	private void renderNumber(GL2 gl, String sRawValue, float fXOrigin, float fYOrigin) {

		float fScaling = renderStyle.getSmallFontScalingFactor();

		textRenderer.renderText(gl, sRawValue, fXOrigin, fYOrigin,
				PCRenderStyle.TEXT_ON_LABEL_Z, fScaling,
				PCRenderStyle.MIN_NUMBER_TEXT_SIZE);
	}

	/**
	 * Renders the gates and updates their values
	 * 
	 * @param gl
	 */
	private void handleGateDragging(GL2 gl) {
		hasFilterChanged = true;
		// bIsDisplayListDirtyLocal = true;
		// bIsDisplayListDirtyRemote = true;
		Point currentPoint = glMouseListener.getPickedPoint();

		float[] fArTargetWorldCoordinates = GLCoordinateUtils
				.convertWindowCoordinatesToWorldCoordinates(gl, currentPoint.x,
						currentPoint.y);

		// todo only valid for one gate
		AGate gate = null;

		gate = hashGates.get(iDraggedGateNumber);

		if (gate == null) {
			gate = hashMasterGates.get(iDraggedGateNumber);
			if (gate == null)
				return;
		}
		gate.handleDragging(gl, fArTargetWorldCoordinates[0],
				fArTargetWorldCoordinates[1], draggedObject, bIsGateDraggingFirstTime);
		bIsGateDraggingFirstTime = false;

		bIsDisplayListDirtyLocal = true;
		bIsDisplayListDirtyRemote = true;
		InfoAreaUpdateEvent event = new InfoAreaUpdateEvent();
		event.setDataDomainID(dataDomain.getDataDomainID());
		event.setSender(this);
		event.setInfo(getShortInfoLocal());
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

			ArrayList<Integer> alCurrentGateBlocks = hashIsGateBlocking.get(iGateID);
			if (alCurrentGateBlocks == null)
				return;
			alCurrentGateBlocks.clear();
			AGate gate = hashGates.get(iGateID);
			int iAxisID = gate.getAxisID();
			if (iAxisID == -1)
				continue;
			for (int contentID : contentVA) {
				EDataRepresentation usedDataRepresentation = EDataRepresentation.RAW;
				if (!table.isSetHomogeneous())
					usedDataRepresentation = EDataRepresentation.NORMALIZED;

				fCurrentValue = table.get(iAxisID).getFloat(usedDataRepresentation,
						contentID);

				if (Float.isNaN(fCurrentValue)) {
					continue;
				}

				if (fCurrentValue <= gate.getUpperValue()
						&& fCurrentValue >= gate.getLowerValue()) {
					alCurrentGateBlocks.add(contentID);
				}
			}
		}
	}

	private void handleNANUnselection() {

		float fCurrentValue = 0;
		hashIsNANBlocking.clear();
		for (Integer iAxisID : hashExcludeNAN.keySet()) {
			ArrayList<Integer> alDeselectedLines = new ArrayList<Integer>();
			for (int iPolylineIndex : contentVA) {

				fCurrentValue = table.get(iAxisID).getFloat(EDataRepresentation.NORMALIZED,
						iPolylineIndex);

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
			for (int iPolylineIndex : contentVA) {
				boolean bIsBlocking = true;
				for (int iAxisIndex : storageVA) {

					fCurrentValue = table.get(iAxisIndex).getFloat(EDataRepresentation.RAW,
							iPolylineIndex);

					if (Float.isNaN(fCurrentValue)) {
						continue;
					}

					if (fCurrentValue <= gate.getUpperValue()
							&& fCurrentValue >= gate.getLowerValue()) {
						bIsBlocking = true;
					} else {
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
	protected void reactOnExternalSelection(ISelectionDelta delta,
			boolean scrollToSelection) {
		handleUnselection();
		resetAxisSpacing();
	}

	@Override
	protected void reactOnContentVAChanges(ContentVADelta delta) {

		contentSelectionManager.setVADelta(delta);

	}

	@Override
	public void handleVAUpdate(StorageVADelta delta, String info) {
		for (VADeltaItem item : delta) {
			if (item.getType() == EVAOperation.REMOVE) {
				Integer id = storageVA.get(item.getIndex());

				// resetAxisSpacing();
				if (storageVA.occurencesOf(id) == 1) {
					removeGate(id);
				}
			} else if (item.getType() == EVAOperation.REMOVE_ELEMENT) {

				removeGate(item.getPrimaryID());
			}
		}
		super.handleVAUpdate(delta, info);
		resetAxisSpacing();
	}

	/** removes a gate based on an axis id **/
	private void removeGate(int axisID) {
		Iterator<Integer> gateIterator = hashGates.keySet().iterator();
		while (gateIterator.hasNext()) {
			int gateID = gateIterator.next();
			if (hashGates.get(gateID).getAxisID() == axisID) {
				gateIterator.remove();
				hashIsGateBlocking.remove(gateID);
			}

		}
	}

	/**
	 * TODO: revise this, not very performance friendly, especially the clearing
	 * of the DESELECTED
	 */

	private void handleUnselection() {
		if (!hasFilterChanged)
			return;

		hasFilterChanged = false;
		handleGateUnselection();
		handleNANUnselection();
		if (table.isSetHomogeneous())
			handleMasterGateUnselection();

		contentSelectionManager.clearSelection(SelectionType.DESELECTED);

		for (ArrayList<Integer> alCurrent : hashIsGateBlocking.values()) {
			contentSelectionManager.addToType(SelectionType.DESELECTED, alCurrent);
		}

		for (ArrayList<Integer> alCurrent : alIsAngleBlocking) {
			contentSelectionManager.addToType(SelectionType.DESELECTED, alCurrent);
		}

		for (ArrayList<Integer> alCurrent : hashIsNANBlocking.values()) {
			contentSelectionManager.addToType(SelectionType.DESELECTED, alCurrent);
		}

		if (bIsDraggingActive || bIsAngularBrushingActive) {
			triggerSelectionUpdate();
		}
	}

	private void triggerSelectionUpdate() {
		SelectionUpdateEvent selectionUpdateEvent = new SelectionUpdateEvent();
		selectionUpdateEvent.setDataDomainID(dataDomain.getDataDomainID());
		selectionUpdateEvent.setSelectionDelta(contentSelectionManager.getDelta());
		selectionUpdateEvent.setSender(this);
		eventPublisher.triggerEvent(selectionUpdateEvent);
		// send out a major update which tells the hhm to update its textures
		UpdateViewEvent updateView = new UpdateViewEvent();
		updateView.setSender(this);
		eventPublisher.triggerEvent(updateView);
	}

	@Override
	protected void handlePickingEvents(final PickingType pickingType,
			final PickingMode pickingMode, final int pickingID, final Pick pick) {
		if (detailLevel == DetailLevel.VERY_LOW || bIsDraggingActive || bWasAxisMoved) {
			return;
		}

		SelectionType selectionType;
		switch (pickingType) {
		case PCS_VIEW_SELECTION:
			break;
		case POLYLINE_SELECTION:
			switch (pickingMode) {

			case CLICKED:
				selectionType = SelectionType.SELECTION;
				if (bAngularBrushingSelectPolyline) {
					bAngularBrushingSelectPolyline = false;
					bIsAngularBrushingActive = true;
					iSelectedLineID = pickingID;
					linePick = pick;
					bIsAngularBrushingFirstTime = true;
				}

				break;
			case MOUSE_OVER:
				selectionType = SelectionType.MOUSE_OVER;
				break;

			case RIGHT_CLICKED:
				selectionType = SelectionType.SELECTION;

				// Prevent handling of non genetic data in context menu
				if (!dataDomain.getDataDomainType().equals(
						"org.caleydo.datadomain.genetic"))
					break;

				ContentContextMenuItemContainer contentContextMenuItemContainer = new ContentContextMenuItemContainer();
				contentContextMenuItemContainer.setDataDomain(dataDomain);
				contentContextMenuItemContainer.setID(contentIDType, pickingID);
				contextMenu.addItemContanier(contentContextMenuItemContainer);

				if (!isRenderedRemote()) {
					contextMenu.setLocation(pick.getPickedPoint(), getParentGLCanvas()
							.getWidth(), getParentGLCanvas().getHeight());
					contextMenu.setMasterGLView(this);
				}
				break;

			default:
				return;

			}

			if (contentSelectionManager.checkStatus(selectionType, pickingID)) {
				break;
			}

			connectedElementRepresentationManager.clear(
					contentSelectionManager.getIDType(), selectionType);

			contentSelectionManager.clearSelection(selectionType);

			// TODO: Integrate multi spotting support again
			// if (ePolylineDataType == EIDType.EXPRESSION_INDEX) {
			// // Resolve multiple spotting on chip and add all to the
			// // selection manager.
			// Integer iRefSeqID =
			// idMappingManager.getID(EMappingType.EXPRESSION_INDEX_2_REFSEQ_MRNA_INT,
			// externalID);
			// if (iRefSeqID == null) {
			// pickingManager.flushHits(uniqueID, ePickingType);
			// return;
			// }
			// int iConnectionID =
			// generalManager.getIDManager().createID(EManagedObjectType.CONNECTION);
			// for (Object iExpressionIndex : idMappingManager.getMultiID(
			// EMappingType.REFSEQ_MRNA_INT_2_EXPRESSION_INDEX, iRefSeqID))
			// {
			// polylineSelectionManager.addToType(SelectionType, (Integer)
			// iExpressionIndex);
			// polylineSelectionManager.addConnectionID(iConnectionID,
			// (Integer) iExpressionIndex);
			// }
			// }
			// else {
			contentSelectionManager.addToType(selectionType, pickingID);
			contentSelectionManager.addConnectionID(generalManager.getIDCreator()
					.createID(ManagedObjectType.CONNECTION), pickingID);

			// }

			// if (ePolylineDataType == EIDType.EXPRESSION_INDEX &&
			// !bAngularBrushingSelectPolyline) {
			if (!bAngularBrushingSelectPolyline) {
				// //
				// SelectionCommand command = new SelectionCommand(
				// ESelectionCommandType.CLEAR, selectionType);
				// sendSelectionCommandEvent(EIDType.EXPRESSION_INDEX, command);

				ISelectionDelta selectionDelta = contentSelectionManager.getDelta();
				handleConnectedElementReps(selectionDelta);
				SelectionUpdateEvent event = new SelectionUpdateEvent();
				event.setSender(this);
				event.setDataDomainID(dataDomain.getDataDomainID());
				event.setSelectionDelta((SelectionDelta) selectionDelta);
				event.setInfo(getShortInfoLocal());
				eventPublisher.triggerEvent(event);
			}

			setDisplayListDirty();
			break;

		case X_AXIS_SELECTION:
			break;
		case Y_AXIS_SELECTION:

			switch (pickingMode) {
			case CLICKED:
				selectionType = SelectionType.SELECTION;
				break;

			case MOUSE_OVER:
				selectionType = SelectionType.MOUSE_OVER;
				break;
			case RIGHT_CLICKED:
				selectionType = SelectionType.SELECTION;
				if (!isRenderedRemote()) {
					contextMenu.setLocation(pick.getPickedPoint(), getParentGLCanvas()
							.getWidth(), getParentGLCanvas().getHeight());
					contextMenu.setMasterGLView(this);
				}
				StorageContextMenuItemContainer experimentContextMenuItemContainer = new StorageContextMenuItemContainer();
				experimentContextMenuItemContainer.setDataDomain(dataDomain);
				experimentContextMenuItemContainer.setID(storageIDType, pickingID);
				contextMenu.addItemContanier(experimentContextMenuItemContainer);

			default:
				return;

			}

			storageSelectionManager.clearSelection(selectionType);
			storageSelectionManager.addToType(selectionType, pickingID);

			storageSelectionManager.addConnectionID(generalManager.getIDCreator()
					.createID(ManagedObjectType.CONNECTION), pickingID);

			connectedElementRepresentationManager.clear(
					storageSelectionManager.getIDType(), selectionType);

			// triggerSelectionUpdate(EMediatorType.SELECTION_MEDIATOR,
			// axisSelectionManager
			// .getDelta(), null);

			// SelectionCommand command = new SelectionCommand(
			// ESelectionCommandType.CLEAR, selectionType);
			// sendSelectionCommandEvent(eAxisDataType, command);

			ISelectionDelta selectionDelta = storageSelectionManager.getDelta();
			// if (storageSelectionManager.getIDType() ==
			// EIDType.EXPERIMENT_INDEX) {
			handleConnectedElementReps(selectionDelta);
			// }
			SelectionUpdateEvent event = new SelectionUpdateEvent();
			event.setSender(this);
			event.setDataDomainID(dataDomain.getDataDomainID());
			event.setSelectionDelta((SelectionDelta) selectionDelta);
			eventPublisher.triggerEvent(event);

			setDisplayListDirty();
			break;
		case GATE_TIP_SELECTION:
			switch (pickingMode) {
			case MOUSE_OVER:
				iDraggedGateNumber = pickingID;
				draggedObject = PickingType.GATE_TIP_SELECTION;
				setDisplayListDirty();
				break;
			case CLICKED:
				bIsDraggingActive = true;
				draggedObject = PickingType.GATE_TIP_SELECTION;
				iDraggedGateNumber = pickingID;
				break;
			// case DRAGGED:
			// bIsDraggingActive = true;
			// draggedObject = EPickingType.GATE_TIP_SELECTION;
			// iDraggedGateNumber = externalID;
			// break;

			}
			break;
		case GATE_BOTTOM_SELECTION:
			switch (pickingMode) {
			case MOUSE_OVER:
				iDraggedGateNumber = pickingID;
				draggedObject = PickingType.GATE_BOTTOM_SELECTION;
				setDisplayListDirty();
				break;
			case CLICKED:
				bIsDraggingActive = true;
				draggedObject = PickingType.GATE_BOTTOM_SELECTION;
				iDraggedGateNumber = pickingID;
				break;
			}
			break;

		case GATE_BODY_SELECTION:
			switch (pickingMode) {
			case MOUSE_OVER:
				iDraggedGateNumber = pickingID;
				draggedObject = PickingType.GATE_BODY_SELECTION;
				setDisplayListDirty();
				break;
			case CLICKED:
				bIsDraggingActive = true;
				bIsGateDraggingFirstTime = true;
				draggedObject = PickingType.GATE_BODY_SELECTION;
				iDraggedGateNumber = pickingID;
				break;
			}
			break;
		case PC_ICON_SELECTION:
			switch (pickingMode) {
			case CLICKED:

				break;
			}
			break;
		case REMOVE_AXIS:
			switch (pickingMode) {
			case MOUSE_OVER:
				dropTexture = EIconTextures.DROP_DELETE;
				iChangeDropOnAxisNumber = pickingID;
				break;
			case CLICKED:
				if (storageVA.occurencesOf(storageVA.get(pickingID)) == 1) {
					removeGate(storageVA.get(pickingID));
				}
				// Integer storageID = storageVA.remove(pickingID);
				Integer storageID = storageVA.get(pickingID);
				storageSelectionManager.remove(pickingID);
				StorageVADelta vaDelta = new StorageVADelta(DataTable.STORAGE, storageIDType);
				vaDelta.add(VADeltaItem.remove(pickingID));

				triggerStorageFilterEvent(vaDelta,
						"Removed " + dataDomain.getStorageLabel(storageID));
				// sendStorageVAUpdateEvent(vaDelta);
				setDisplayListDirty();
				resetAxisSpacing();
				break;
			}
			break;

		case MOVE_AXIS:
			switch (pickingMode) {
			case CLICKED:
				bWasAxisMoved = true;
				bWasAxisDraggedFirstTime = true;
				iMovedAxisPosition = pickingID;
				setDisplayListDirty();
			case MOUSE_OVER:
				dropTexture = EIconTextures.DROP_MOVE;
				iChangeDropOnAxisNumber = pickingID;
				setDisplayListDirty();
				break;
			}
			break;

		case DUPLICATE_AXIS:
			switch (pickingMode) {
			case MOUSE_OVER:
				dropTexture = EIconTextures.DROP_DUPLICATE;
				iChangeDropOnAxisNumber = pickingID;
				break;
			case CLICKED:
				if (pickingID >= 0) {
					// storageVA.copy(pickingID);
					StorageVADelta vaDelta = new StorageVADelta(DataTable.STORAGE,
							storageIDType);
					vaDelta.add(VADeltaItem.copy(pickingID));
					triggerStorageFilterEvent(
							vaDelta,
							"Copied "
									+ dataDomain.getStorageLabel(storageVA.get(pickingID)));

					setDisplayListDirty();
					// resetSelections();
					// initGates();
					resetAxisSpacing();
					break;
				}
			}
			break;
		case ADD_GATE:
			switch (pickingMode) {
			case CLICKED:
				hasFilterChanged = true;
				AGate gate;
				if (table.isSetHomogeneous()) {
					gate = new Gate(++iGateCounter, pickingID,
							(float) table.getRawForNormalized(0),
							(float) table.getRawForNormalized(0.5f), table, renderStyle);
				} else {
					gate = new NominalGate(++iGateCounter, pickingID, 0, 0.5f, table,
							renderStyle);
				}
				hashGates.put(this.iGateCounter, gate);
				hashIsGateBlocking.put(this.iGateCounter, new ArrayList<Integer>());
				handleUnselection();
				triggerSelectionUpdate();
				setDisplayListDirty();

				break;
			}
			break;
		case ADD_MASTER_GATE:
			switch (pickingMode) {
			case CLICKED:
				hasFilterChanged = true;
				Gate gate = new Gate(++iGateCounter, -1,
						(float) table.getRawForNormalized(0),
						(float) table.getRawForNormalized(0.5f), table, renderStyle);
				gate.setMasterGate(true);
				hashMasterGates.put(iGateCounter, gate);
				hashIsGateBlocking.put(iGateCounter, new ArrayList<Integer>());
				handleUnselection();
				triggerSelectionUpdate();
				setDisplayListDirty();
				break;
			}
			break;

		case REMOVE_GATE:
			switch (pickingMode) {
			case CLICKED:
				hasFilterChanged = true;
				// either the gate belongs to the normal or to the master gates
				if (hashGates.remove(pickingID) == null)
					hashMasterGates.remove(pickingID);

				hashIsGateBlocking.remove(pickingID);

				handleUnselection();
				triggerSelectionUpdate();
				setDisplayListDirty();
				break;
			}
			break;
		case ANGULAR_UPPER:
			switch (pickingMode) {
			case CLICKED:
				bIsAngularDraggingActive = true;
			case DRAGGED:
				bIsAngularDraggingActive = true;
			}
			break;

		case ANGULAR_LOWER:
			switch (pickingMode) {
			case CLICKED:
				bIsAngularDraggingActive = true;
			case DRAGGED:
				bIsAngularDraggingActive = true;
			}
			break;
		case REMOVE_NAN:
			switch (pickingMode) {
			case CLICKED:
				hasFilterChanged = true;
				if (hashExcludeNAN.containsKey(pickingID)) {
					hashExcludeNAN.remove(pickingID);
				} else {
					hashExcludeNAN.put(pickingID, null);
				}
				setDisplayListDirty();
				break;

			}
			break;
		}
	}

	private void triggerStorageFilterEvent(StorageVADelta delta, String label) {

		StorageFilter filter = new StorageFilter();
		filter.setVADelta(delta);
		filter.setLabel(label);
		filter.setDataDomain(dataDomain);

		NewStorageFilterEvent filterEvent = new NewStorageFilterEvent();
		filterEvent.setFilter(filter);
		filterEvent.setSender(this);
		filterEvent.setDataDomainID(dataDomain.getDataDomainID());

		eventPublisher.triggerEvent(filterEvent);
	}

	private void triggerContentFilterEvent(ContentVADelta delta, String label) {

		ContentFilter filter = new ContentFilter();
		filter.setVADelta(delta);
		filter.setLabel(label);
		filter.setDataDomain(dataDomain);

		NewContentFilterEvent filterEvent = new NewContentFilterEvent();
		filterEvent.setFilter(filter);
		filterEvent.setSender(this);
		filterEvent.setDataDomainID(dataDomain.getDataDomainID());

		eventPublisher.triggerEvent(filterEvent);
	}

	@Override
	protected ArrayList<SelectedElementRep> createElementRep(IDType idType, int id)
			throws InvalidAttributeValueException {

		ArrayList<SelectedElementRep> alElementReps = new ArrayList<SelectedElementRep>();

		float x = 0;
		float y = 0;

		if (idType == storageIDType
				&& dataDomain.getDataDomainID()
						.equals("org.caleydo.datadomain.genetic")) {

			int axisCount = storageVA.indexOf(id);
			// for (int iAxisID : storageVA) {
			x = axisCount * renderStyle.getAxisSpacing(storageVA.size());
			axisCount++;
			x = x + renderStyle.getXSpacing();
			y = renderStyle.getBottomSpacing();
			// y =set.get(storageVA.get(storageVA.size() - 1)).getFloat(
			// EDataRepresentation.NORMALIZED, iAxisID);
			alElementReps.add(new SelectedElementRep(idType, uniqueID, x, y, 0.0f));
			// }
			// }
			// else if (idType == EIDType.EXPERIMENT_INDEX
			// && dataDomain.getDataDomainType().equals(
			// "org.caleydo.datadomain.clinical")) {
			// System.out.println("wu");
			// alElementReps.add(new SelectedElementRep(idType, uniqueID, 0, 0,
			// 0.0f));

		} else {
			// if (eAxisDataType == EIDType.EXPERIMENT_RECORD)
			// fXValue = viewFrustum.getRight() - 0.2f;
			// else
			// fXValue = viewFrustum.getRight() - 0.4f;

			// if (renderConnectionsLeft) {
			// x = x + renderStyle.getXSpacing();
			// y =
			// set.get(storageVA.get(0)).getFloat(EDataRepresentation.NORMALIZED,
			// iStorageIndex);
			// } else {
			// if (eAxisDataType == EIDType.EXPERIMENT_RECORD)
			// fXValue = viewFrustum.getRight() - 0.2f;
			// else
			x = viewFrustum.getLeft() + renderStyle.getXSpacing();
			y = table.get(storageVA.get(0)).getFloat(EDataRepresentation.NORMALIZED, id);
			// }

			// // get the value on the leftmost axis
			// fYValue =
			// set.get(storageVA.get(0)).getFloat(EDataRepresentation.NORMALIZED,
			// iStorageIndex);

			if (Float.isNaN(y)) {
				y = NAN_Y_OFFSET * renderStyle.getAxisHeight()
						+ renderStyle.getBottomSpacing();
			} else {
				y = y * renderStyle.getAxisHeight() + renderStyle.getBottomSpacing();
			}
			alElementReps.add(new SelectedElementRep(idType, uniqueID, x, y, 0.0f));
		}

		return alElementReps;
	}

	@Override
	public String getShortInfo() {
		String message;
		int iNumLines = contentVA.size();
		if (displayEveryNthPolyline == 1) {
			message = "Parallel Coordinates - " + iNumLines + " "
					+ dataDomain.getContentName(false, true) + " / " + storageVA.size()
					+ " experiments";
		} else {
			message = "Parallel Coordinates showing a sample of " + iNumLines
					/ displayEveryNthPolyline + " out of " + iNumLines + " "
					+ dataDomain.getContentName(false, true) + " / " + storageVA.size()
					+ " experiments";
		}
		return message;

	}

	@Override
	public String getDetailedInfo() {
		StringBuffer sInfoText = new StringBuffer();
		sInfoText.append("<b>Type:</b> Parallel Coordinates\n");
		sInfoText.append(contentVA.size() + dataDomain.getContentName(false, true)
				+ " as polylines and " + storageVA.size() + " experiments as axis.\n");

		if (bRenderOnlyContext) {
			sInfoText
					.append("Showing only genes which occur in one of the other views in focus\n");
		} else {
			if (bUseRandomSampling) {
				sInfoText.append("Random sampling active, sample size: "
						+ iNumberOfRandomElements + "\n");
			} else {
				sInfoText.append("Random sampling inactive\n");
			}

			if (dataFilterLevel == EDataFilterLevel.COMPLETE) {
				sInfoText.append("Showing all " + dataDomain.getContentName(false, true)
						+ " in the dataset\n");
			} else if (dataFilterLevel == EDataFilterLevel.ONLY_MAPPING) {
				sInfoText.append("Showing all " + dataDomain.getContentName(false, true)
						+ " that have a known DAVID ID mapping\n");
			} else if (dataFilterLevel == EDataFilterLevel.ONLY_CONTEXT) {
				sInfoText
						.append("Showing all genes that are contained in any of the KEGG or Biocarta Pathways\n");
			}
		}

		return sInfoText.toString();
	}

	// TODO
	private void handleAngularBrushing(final GL2 gl) {
		hasFilterChanged = true;
		if (bIsAngularBrushingFirstTime) {
			fCurrentAngle = fDefaultAngle;
			Point currentPoint = linePick.getPickedPoint();
			float[] fArPoint = GLCoordinateUtils
					.convertWindowCoordinatesToWorldCoordinates(gl, currentPoint.x,
							currentPoint.y);
			vecAngularBrushingPoint = new Vec3f(fArPoint[0], fArPoint[1], 0.01f);
			bIsAngularBrushingFirstTime = false;

		}
		alIsAngleBlocking.get(0).clear();

		int iPosition = 0;

		for (int iCount = 0; iCount < axisSpacings.size() - 1; iCount++) {
			if (vecAngularBrushingPoint.x() > axisSpacings.get(iCount)
					&& vecAngularBrushingPoint.x() < axisSpacings.get(iCount + 1)) {
				iPosition = iCount;
			}
		}

		int iAxisLeftIndex;
		int iAxisRightIndex;

		iAxisLeftIndex = storageVA.get(iPosition);
		iAxisRightIndex = storageVA.get(iPosition + 1);

		Vec3f vecLeftPoint = new Vec3f(0, 0, 0);
		Vec3f vecRightPoint = new Vec3f(0, 0, 0);

		vecLeftPoint.setY(table.get(iAxisLeftIndex).getFloat(
				EDataRepresentation.NORMALIZED, iSelectedLineID)
				* renderStyle.getAxisHeight());
		vecRightPoint.setY(table.get(iAxisRightIndex).getFloat(
				EDataRepresentation.NORMALIZED, iSelectedLineID)
				* renderStyle.getAxisHeight());

		vecLeftPoint.setX(axisSpacings.get(iPosition));
		vecRightPoint.setX(axisSpacings.get(iPosition + 1));

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
			float fArPoint[] = GLCoordinateUtils
					.convertWindowCoordinatesToWorldCoordinates(gl, pickedPoint.x,
							pickedPoint.y);
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

		gl.glPushName(pickingManager.getPickingID(uniqueID, PickingType.ANGULAR_UPPER,
				iPosition));
		gl.glBegin(GL2.GL_LINES);
		gl.glVertex3f(vecTriangleOrigin.x(), vecTriangleOrigin.y(),
				vecTriangleOrigin.z() + 0.02f);
		gl.glVertex3f(vecUpperPoint.x(), vecUpperPoint.y(), vecUpperPoint.z() + 0.02f);
		gl.glEnd();
		gl.glPopName();

		gl.glPushName(pickingManager.getPickingID(uniqueID, PickingType.ANGULAR_UPPER,
				iPosition));
		gl.glBegin(GL2.GL_LINES);
		gl.glVertex3f(vecTriangleOrigin.x(), vecTriangleOrigin.y(),
				vecTriangleOrigin.z() + 0.02f);
		gl.glVertex3f(vecLowerPoint.x(), vecLowerPoint.y(), vecLowerPoint.z() + 0.02f);
		gl.glEnd();
		gl.glPopName();

		// draw angle polygon

		gl.glColor4fv(ANGULAR_POLYGON_COLOR, 0);
		// gl.glColor4f(1, 0, 0, 0.5f);
		gl.glBegin(GL2.GL_POLYGON);
		rotf.set(new Vec3f(0, 0, 1), -fCurrentAngle / 10);
		Vec3f tempVector = vecCenterLine.copy();
		gl.glVertex3f(vecTriangleOrigin.x(), vecTriangleOrigin.y(),
				vecTriangleOrigin.z() + 0.02f);

		for (int iCount = 0; iCount <= 10; iCount++) {
			Vec3f vecPoint = tempVector.copy();
			vecPoint.normalize();
			vecPoint.scale(fLegLength);
			gl.glVertex3f(vecTriangleOrigin.x() + vecPoint.x(), vecTriangleOrigin.y()
					+ vecPoint.y(), vecTriangleOrigin.z() + vecPoint.z() + 0.02f);
			tempVector = rotf.rotateVector(tempVector);
		}
		gl.glEnd();

		gl.glBegin(GL2.GL_POLYGON);
		rotf.set(new Vec3f(0, 0, 1), fCurrentAngle / 10);
		tempVector = vecCenterLine.copy();

		gl.glVertex3f(vecTriangleOrigin.x(), vecTriangleOrigin.y(),
				vecTriangleOrigin.z() + 0.02f);
		for (int iCount = 0; iCount <= 10; iCount++) {
			Vec3f vecPoint = tempVector.copy();
			vecPoint.normalize();
			vecPoint.scale(fLegLength);
			gl.glVertex3f(vecTriangleOrigin.x() + vecPoint.x(), vecTriangleOrigin.y()
					+ vecPoint.y(), vecTriangleOrigin.z() + vecPoint.z() + 0.02f);

			tempVector = rotf.rotateVector(tempVector);

		}

		// gl.glVertex3f(vecUpperPoint.x(), vecUpperPoint.y(), vecUpperPoint.z()
		// + 0.02f);
		gl.glEnd();

		// check selection

		for (Integer iCurrent : contentVA) {

			vecLeftPoint.setY(table.get(iAxisLeftIndex).getFloat(
					EDataRepresentation.NORMALIZED, iCurrent)
					* renderStyle.getAxisHeight());
			vecRightPoint.setY(table.get(iAxisRightIndex).getFloat(
					EDataRepresentation.NORMALIZED, iCurrent)
					* renderStyle.getAxisHeight());

			vecLeftPoint.setX(axisSpacings.get(iPosition));
			vecRightPoint.setX(axisSpacings.get(iPosition + 1));

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

	private void adjustAxisSpacing(GL2 gl) {

		Point currentPoint = glMouseListener.getPickedPoint();

		float[] fArTargetWorldCoordinates = GLCoordinateUtils
				.convertWindowCoordinatesToWorldCoordinates(gl, currentPoint.x,
						currentPoint.y);

		float fWidth = fArTargetWorldCoordinates[0] - xSideSpacing;

		if (bWasAxisDraggedFirstTime) {
			// adjust from the actually clicked point to the center of the axis
			fAxisDraggingOffset = fWidth - axisSpacings.get(iMovedAxisPosition);
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
		for (int iCount = 0; iCount < axisSpacings.size(); iCount++) {
			if (iMovedAxisPosition > iCount && fWidth < axisSpacings.get(iCount)) {
				iSwitchAxisWithThis = iCount;
				break;
			}
			if (iMovedAxisPosition < iCount && fWidth > axisSpacings.get(iCount)) {
				iSwitchAxisWithThis = iCount;
			}
		}

		if (iSwitchAxisWithThis != -1) {
			storageVA.move(iMovedAxisPosition, iSwitchAxisWithThis);
			axisSpacings.remove(iMovedAxisPosition);
			axisSpacings.add(iSwitchAxisWithThis, fWidth);

			StorageVADelta vaDelta = new StorageVADelta(storageVAType, storageIDType);
			vaDelta.add(VADeltaItem.move(iMovedAxisPosition, iSwitchAxisWithThis));
			triggerStorageFilterEvent(
					vaDelta,
					"Moved "
							+ dataDomain.getStorageLabel(storageVA
									.get(iMovedAxisPosition)));
			iMovedAxisPosition = iSwitchAxisWithThis;
		}

		else {
			axisSpacings.set(iMovedAxisPosition, fWidth);
		}
		setDisplayListDirty();

	}

	private void handleTrackInput(final GL2 gl) {

		// TODO: very performance intensive - better solution needed (only in
		// reshape)!
		parentComposite.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				upperLeftScreenPos = parentComposite.toDisplay(
						1, 1);
			}
		});

		Rectangle screenRect = getParentGLCanvas().getBounds();
		float[] fArTrackPos = generalManager.getTrackDataProvider().getEyeTrackData();

		fArTrackPos[0] -= upperLeftScreenPos.x;
		fArTrackPos[1] -= upperLeftScreenPos.y;

		// GLHelperFunctions.drawPointAt(gl, new Vec3f(fArTrackPos[0] /
		// screenRect.width * 8f,
		// (1f - fArTrackPos[1] / screenRect.height) * 8f * fAspectRatio,
		// 0.01f));

		float fTrackX = (generalManager.getTrackDataProvider().getEyeTrackData()[0])
				/ screenRect.width;

		fTrackX *= renderStyle.getWidthOfCoordinateSystem();

		int iAxisNumber = 0;
		for (int iCount = 0; iCount < axisSpacings.size() - 1; iCount++) {
			if (axisSpacings.get(iCount) < fTrackX
					&& axisSpacings.get(iCount + 1) > fTrackX) {
				if (fTrackX - axisSpacings.get(iCount) < axisSpacings.get(iCount)
						- fTrackX) {
					iAxisNumber = iCount;
				} else {
					iAxisNumber = iCount + 1;
				}

				break;
			}
		}

		int iNumberOfAxis = storageVA.size();

		float fOriginalAxisSpacing = renderStyle.getAxisSpacing(iNumberOfAxis);

		float fFocusAxisSpacing = fOriginalAxisSpacing * 2;

		float fReducedSpacing = (renderStyle.getWidthOfCoordinateSystem() - 2 * fFocusAxisSpacing)
				/ (iNumberOfAxis - 3);

		float fCurrentX = 0;
		axisSpacings.clear();
		for (int iCount = 0; iCount < iNumberOfAxis; iCount++) {
			axisSpacings.add(fCurrentX);
			if (iCount + 1 == iAxisNumber || iCount == iAxisNumber) {
				fCurrentX += fFocusAxisSpacing;
			} else {
				fCurrentX += fReducedSpacing;
			}
		}

		setDisplayListDirty();
	}

	public void resetAxisSpacing() {
		axisSpacings.clear();
		int numAxis = storageVA.size();
		float initialAxisSpacing = renderStyle.getAxisSpacing(numAxis);
		for (int count = 0; count < numAxis; count++) {
			axisSpacings.add(initialAxisSpacing * count);
		}
		setDisplayListDirty();
	}

	@Override
	public void setFrustum(ViewFrustum viewFrustum) {
		super.setFrustum(viewFrustum);
		updateSpacings();
		resetAxisSpacing();
		renderStyle = new PCRenderStyle(this, viewFrustum);
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedParallelCoordinatesView serializedForm = new SerializedParallelCoordinatesView(
				dataDomain.getDataDomainID());
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
		eventPublisher
				.addListener(ResetParallelCoordinatesEvent.class, resetViewListener);

		useRandomSamplingListener = new UseRandomSamplingListener();
		useRandomSamplingListener.setHandler(this);
		eventPublisher.addListener(UseRandomSamplingEvent.class,
				useRandomSamplingListener);

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

		if (angularBrushingListener != null) {
			eventPublisher.removeListener(angularBrushingListener);
			angularBrushingListener = null;
		}
	}

	@Override
	public void handleSelectionCommand(IDCategory category,
			SelectionCommand selectionCommand) {
		if (category == contentSelectionManager.getIDType().getIDCategory())
			contentSelectionManager.executeSelectionCommand(selectionCommand);
		else if (category == storageSelectionManager.getIDType().getIDCategory())
			storageSelectionManager.executeSelectionCommand(selectionCommand);
		else
			return;

		setDisplayListDirty();
	}

	@Override
	public String toString() {
		int iNumElements = (contentSelectionManager.getNumberOfElements() - contentSelectionManager
				.getNumberOfElements(SelectionType.DESELECTED));
		String renderMode = "standalone";
		if (isRenderedRemote())
			renderMode = "remote";
		return ("PCs, " + renderMode + ", " + iNumElements + " elements" + " Axis DT: "
				+ storageSelectionManager.getIDType() + " Polyline DT:" + contentSelectionManager
				.getIDType());
	}

	@Override
	public List<AGLView> getRemoteRenderedViews() {
		return new ArrayList<AGLView>();
	}

	public void setRenderConnectionState(boolean renderConnectionssLeft) {
		this.renderConnectionsLeft = renderConnectionssLeft;

	}

	public DataTable getSet() {
		return table;
	}

	public void setSet(DataTable set) {
		this.table = set;
	}

	public void setContentVA(ContentVirtualArray contentVA) {
		this.contentVA = contentVA;
		contentSelectionManager.setVA(contentVA);
	}

	@Override
	public int getMinPixelHeight() {
		// TODO: Calculate depending on content
		return 100;
	}

	private float[] generateVertexBuffer() {
		int numberOfVertices = table.getMetaData().depth() * table.getMetaData().size() * 2;

		float vertices[] = new float[numberOfVertices];
		int vertexCounter = 0;

		for (int index = 0; index < table.getMetaData().depth(); index++) {
			int storageCounter = 0;
			for (Integer storageID : storageVA) {
				float xValue = 0.2f * storageCounter++;
				NumericalStorage storage = (NumericalStorage) table.get(storageID);

				float yValue = storage.getFloat(EDataRepresentation.NORMALIZED, index);
				vertices[vertexCounter++] = xValue;
				vertices[vertexCounter++] = yValue;
			}
		}

		return vertices;
	}

	private void displayVBO(GL2 gl) {
		// GLHelperFunctions.drawPointAt(gl, 0.5f, 0.5f, 0f);

		gl.glColor3f(0, 0, 1);
		// int[] indices = { 0, 1, 1, 2, 2, 3, 3};
		int size = 100000;
		int[] indices = new int[size];
		for (int count = 0; count < size - 1;) {
			if (count - 1 >= 0) {
				indices[count] = indices[count - 1];
				indices[count + 1] = count;
			} else {
				indices[count] = 0;
				indices[count + 1] = 1;
			}
			count += 2;

		}
		IntBuffer indexBuffer = Buffers.newDirectIntBuffer(indices);
		indexBuffer.rewind();

		if (vertexBufferIndices[0] == -1) {
			// float vertices[] = new float[] { 0.0f, 0.0f, 0.5f, 0.5f, 2, 1, 4,
			// 2, 5, 6};

			float vertices[] = generateVertexBuffer();

			FloatBuffer vertexBuffer = Buffers.newDirectFloatBuffer(vertices);

			vertexBuffer.rewind();

			if (!gl.isFunctionAvailable("glGenBuffers")
					|| !gl.isFunctionAvailable("glBindBuffer")
					|| !gl.isFunctionAvailable("glBufferData")
					|| !gl.isFunctionAvailable("glDeleteBuffers")) {
				throw new IllegalStateException("Vertex Buffer Objects not supported");
			}
			gl.glGenBuffers(1, vertexBufferIndices, 0);
			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vertexBufferIndices[0]);
			gl.glBufferData(GL.GL_ARRAY_BUFFER, vertices.length * Buffers.SIZEOF_FLOAT,
					vertexBuffer, GL2.GL_DYNAMIC_DRAW);
			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vertexBufferIndices[0]);
			// ByteBuffer bytebuffer = gl.glMapBuffer(GL.GL_ARRAY_BUFFER,
			// GL2.GL_WRITE_ONLY);
			// FloatBuffer floatBuffer =
			// bytebuffer.order(ByteOrder.nativeOrder())
			// .asFloatBuffer();

			// for (float vertex : vertices) {
			// floatBuffer.put(vertex);
			// }
			gl.glUnmapBuffer(GL.GL_ARRAY_BUFFER);

		}

		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vertexBufferIndices[0]);
		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		// gl.glEnableClientState( GL2.GL_COLOR_ARRAY )
		gl.glVertexPointer(2, GL2.GL_FLOAT, 0, 0);
		gl.glDrawArrays(GL.GL_LINE_STRIP, 0, vertexBufferIndices[0]);
		gl.glDrawElements(GL2.GL_LINES, indices.length, GL2.GL_UNSIGNED_INT, indexBuffer);
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
		gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
		// gl.glDisableClientState(GL2.GL_COLOR_ARRAY);
		// gl.glDisable(GL2.GL_COLOR_MATERIAL);

		// glcanvas.swapBuffers();

		// FloatBuffer colorBuffer = BufferUtil.newFloatBuffer(colors.length);
		// colorBuffer.put(colors);
		// colorBuffer.rewind();

		// gl.glLineWidth(4);
		// gl.glColor3f(0, 1, 1);
		//
		// // gl.glGenBuffersARB(vertices.length, )
		// gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		//
		// // gl.glColorPointer(3, GL2.GL_FLOAT, 0, colorBuffer);
		// gl.glDrawElements(GL2.GL_LINE_STRIP, indices.length,
		// GL2.GL_UNSIGNED_INT,
		// indexBuffer);
		// gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
		// gl.glFlush();

		// int vertices[] = new int[] { 1, 1, 3, 2, 5, 1, 3, 5, 5, 1, 1,
		// 5 };
		// float colors[] = new float[] { 1.0f, 0.2f, 0.2f, 0.2f, 0.2f, 1.0f,
		// 0.8f, 1.0f,
		// 0.2f, 0.75f, 0.75f, 0.75f, 0.35f, 0.35f, 0.35f, 0.5f, 0.5f, 0.5f };
		// IntBuffer tmpVerticesBuf = BufferUtil.newIntBuffer(vertices.length);
		// FloatBuffer tmpColorsBuf = BufferUtil.newFloatBuffer(colors.length);
		// for (int i = 0; i < vertices.length; i++)
		// tmpVerticesBuf.put(vertices[i]);
		// for (int j = 0; j < colors.length; j++)
		// tmpColorsBuf.put(colors[j]);
		// tmpVerticesBuf.rewind();
		// tmpColorsBuf.rewind();
		// //
		// gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		// gl.glEnableClientState(GL2.GL_COLOR_ARRAY);
		// //
		// gl.glVertexPointer(2, GL2.GL_INT, 0, tmpVerticesBuf);
		// gl.glColorPointer(3, GL2.GL_FLOAT, 0, tmpColorsBuf);
		// // this.verticesBuf = tmpVerticesBuf;
		// // this.colorsBuf = tmpColorsBuf;
		//
		// int indices[] = new int[] { 0, 1, 3, 4 };
		// IntBuffer indicesBuf = BufferUtil.newIntBuffer(indices.length);
		// for (int i = 0; i < indices.length; i++)
		// indicesBuf.put(indices[i]);
		// indicesBuf.rewind();
		// gl.glDrawElements(GL2.GL_LINE_STRIP, 4, GL2.GL_UNSIGNED_INT,
		// indicesBuf);
		//
		// gl.glFlush();
	}

	@Override
	public int getMinPixelHeight(DetailLevel detailLevel) {
		switch (detailLevel) {
		case HIGH:
			return 100;
		case MEDIUM:
			return 80;
		case LOW:
			return 50;
		default:
			return 50;
		}
	}

	@Override
	public int getMinPixelWidth(DetailLevel detailLevel) {
		switch (detailLevel) {
		case HIGH:
			return 100;
		case MEDIUM:
			return 80;
		case LOW:
			return Math.max(150, 30 * table.getMetaData().size());
		default:
			return 80;
		}
	}

	@Override
	public java.util.Set<IDataDomain> getDataDomains() {
		java.util.Set<IDataDomain> dataDomains = new HashSet<IDataDomain>();
		dataDomains.add(dataDomain);
		return dataDomains;
	}

}
