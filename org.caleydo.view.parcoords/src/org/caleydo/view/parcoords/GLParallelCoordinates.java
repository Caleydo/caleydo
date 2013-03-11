/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander Lex, Christian Partl, Johannes Kepler
 * University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.parcoords;

import static org.caleydo.view.parcoords.PCRenderStyle.ANGLUAR_LINE_WIDTH;
import static org.caleydo.view.parcoords.PCRenderStyle.ANGULAR_COLOR;
import static org.caleydo.view.parcoords.PCRenderStyle.ANGULAR_POLYGON_COLOR;
import static org.caleydo.view.parcoords.PCRenderStyle.AXIS_MARKER_WIDTH;
import static org.caleydo.view.parcoords.PCRenderStyle.AXIS_Z;
import static org.caleydo.view.parcoords.PCRenderStyle.LABEL_Z;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.data.collection.table.CategoricalTable;
import org.caleydo.core.data.collection.table.NumericalTable;
import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.filter.Filter;
import org.caleydo.core.data.filter.event.NewFilterEvent;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.delta.VADeltaItem;
import org.caleydo.core.data.virtualarray.delta.VirtualArrayDelta;
import org.caleydo.core.data.virtualarray.events.SortByDataEvent;
import org.caleydo.core.event.EventListenerManager;
import org.caleydo.core.event.EventListenerManagers;
import org.caleydo.core.event.data.DataDomainUpdateEvent;
import org.caleydo.core.event.data.SelectionUpdateEvent;
import org.caleydo.core.event.view.ResetAllViewsEvent;
import org.caleydo.core.event.view.TablePerspectivesChangedEvent;
import org.caleydo.core.event.view.UseRandomSamplingEvent;
import org.caleydo.core.gui.preferences.PreferenceConstants;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.format.Formatter;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.core.view.contextmenu.GenericContextMenuItem;
import org.caleydo.core.view.contextmenu.item.BookmarkMenuItem;
import org.caleydo.core.view.listener.AddTablePerspectivesEvent;
import org.caleydo.core.view.listener.AddTablePerspectivesListener;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.ATableBasedView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.canvas.listener.ResetViewListener;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.util.GLCoordinateUtils;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.datadomain.genetic.GeneticDataDomain;
import org.caleydo.datadomain.pathway.contextmenu.container.GeneMenuItemContainer;
import org.caleydo.view.parcoords.PCRenderStyle.PolyLineState;
import org.caleydo.view.parcoords.listener.AngularBrushingEvent;
import org.caleydo.view.parcoords.listener.AngularBrushingListener;
import org.caleydo.view.parcoords.listener.ApplyCurrentSelectionToVirtualArrayEvent;
import org.caleydo.view.parcoords.listener.ApplyCurrentSelectionToVirtualArrayListener;
import org.caleydo.view.parcoords.listener.ResetAxisSpacingEvent;
import org.caleydo.view.parcoords.listener.ResetAxisSpacingListener;
import org.caleydo.view.parcoords.listener.ResetParallelCoordinatesEvent;
import org.caleydo.view.parcoords.listener.UseRandomSamplingListener;
import org.eclipse.swt.widgets.Composite;

/**
 * This class is responsible for rendering the parallel coordinates
 *
 * @author Alexander Lex (responsible for PC)
 * @author Marc Streit
 */
public class GLParallelCoordinates extends ATableBasedView implements IGLRemoteRenderingView {

	public static String VIEW_TYPE = "org.caleydo.view.parcoords";
	public static String VIEW_NAME = "Parallel Coordinates";

	private EPickingType draggedObject;

	private final EventListenerManager listeners = EventListenerManagers.wrap(this);

	/**
	 * Hashes a gate id, which is made up of an axis id + the last three digits a gate counter (per axis) to a pair of
	 * values which make up the upper and lower gate tip
	 */
	private HashMap<Integer, Gate> hashGates;
	/**
	 * Hash of blocking gates
	 */
	private HashMap<Integer, ArrayList<Integer>> hashIsGateBlocking;

	/**
	 * HashMap for the gates that are used to remove selections across all axes, when the set is homogeneous
	 */
	private HashMap<Integer, Gate> hashMasterGates;

	/**
	 * Gate counter used for unique ID retrieval for gates. It is shared between regular and master gates
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

	protected float fYTranslation = 0;

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

	protected PCRenderStyle renderStyle;

	private int displayEveryNthPolyline = 1;

	/** The currently used drop-texture */
	private String dropTexture = PCRenderStyle.DROP_NORMAL;

	int changeDropOnAxisNumber = -1;

	/**
	 * Constructor.
	 */
	public GLParallelCoordinates(IGLCanvas glCanvas, Composite parentComposite, ViewFrustum viewFrustum) {
		super(glCanvas, parentComposite, viewFrustum, VIEW_TYPE, VIEW_NAME);

		// custom one
		this.textureManager = new TextureManager(Activator.getResourceLoader());

		renderStyle = new PCRenderStyle(this, viewFrustum);

		alIsAngleBlocking = new ArrayList<ArrayList<Integer>>();
		alIsAngleBlocking.add(new ArrayList<Integer>());

		axisSpacings = new ArrayList<Float>();
		numberOfRandomElements = generalManager.getPreferenceStore().getInt(
				PreferenceConstants.PC_NUM_RANDOM_SAMPLING_POINT);

		// glSelectionHeatMap =
		// ((ViewManager)generalManager.getViewGLCanvasManager()).getSelectionHeatMap();

		icon = PCRenderStyle.PC_LARGE_TEXTURE;
	}

	@Override
	public void initLocal(final GL2 gl) {
		init(gl);
	}

	@Override
	public void initRemote(final GL2 gl, final AGLView glParentView, final GLMouseListener glMouseListener) {

		this.glMouseListener = glMouseListener;
		init(gl);
	}

	@Override
	public void init(final GL2 gl) {
		textRenderer = new CaleydoTextRenderer(24);
		displayListIndex = gl.glGenLists(1);
		initData();
		registerPickingListeners();
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

		if (!lazyMode)
			pickingManager.handlePicking(this, gl);

		display(gl);

		if (busyState != EBusyState.OFF) {
			renderBusyMode(gl);
		}
	}

	@Override
	public void displayRemote(final GL2 gl) {
		display(gl);

	}

	@Override
	public void display(final GL2 gl) {
		if (isDisplayListDirty) {
			handleUnselection();
			buildDisplayList(gl, displayListIndex);
			isDisplayListDirty = false;
		}

		processEvents();
		// displayVBO(gl);

		gl.glEnable(GL.GL_BLEND);

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

		gl.glCallList(displayListIndex);

		if (bIsAngularBrushingActive && iSelectedLineID != -1) {
			handleAngularBrushing(gl);
		}

		gl.glTranslatef(-xSideSpacing, -fYTranslation, 0.0f);

		if (!lazyMode)
			checkForHits(gl);
	}

	public void triggerAngularBrushing() {
		bAngularBrushingSelectPolyline = true;
		setDisplayListDirty();
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

	public void saveSelection() {

		Set<Integer> removedElements = recordSelectionManager.getElements(SelectionType.DESELECTED);

		VirtualArrayDelta delta = new VirtualArrayDelta(tablePerspective.getRecordPerspective().getPerspectiveID(),
				recordIDType);
		for (Integer recordID : removedElements) {
			delta.add(VADeltaItem.removeElement(recordID));
		}

		recordSelectionManager.clearSelection(SelectionType.DESELECTED);
		clearFilters();
		triggerRecordFilterEvent(delta, "Removed via gates");

	}

	/**
	 * Initialize the gates. The gate heights are saved in two lists, which contain the rendering height of the gate
	 */
	private void initGates() {
		hashGates = new HashMap<Integer, Gate>();
		hashIsGateBlocking = new HashMap<Integer, ArrayList<Integer>>();
		if (dataDomain != null
				&& (dataDomain.getTable() instanceof NumericalTable || dataDomain.getTable() instanceof CategoricalTable)) {
			hashMasterGates = new HashMap<Integer, Gate>();
		}
		hashExcludeNAN = new HashMap<Integer, Boolean>();
		hashIsNANBlocking = new HashMap<Integer, ArrayList<Integer>>();
	}

	/**
	 * Build polyline display list. Renders coordinate system, polylines and gates, by calling the render methods
	 *
	 * @param gl
	 *            GL2 context
	 * @param iGLDisplayListIndex
	 *            the index of the display list
	 */
	private void buildDisplayList(final GL2 gl, int iGLDisplayListIndex) {
		gl.glNewList(iGLDisplayListIndex, GL2.GL_COMPILE);

		if (tablePerspective.getNrRecords() == 0 || tablePerspective.getNrDimensions() == 0) {
			gl.glTranslatef(-xSideSpacing, -fYTranslation, 0.0f);
			renderSymbol(gl, PCRenderStyle.PC_LARGE_TEXTURE, 2);
			gl.glTranslatef(+xSideSpacing, fYTranslation, 0.0f);
		} else {

			if (dataDomain.getTable().isDataHomogeneous() && !isRenderedRemote()) {
				renderMasterGate(gl);
			}

			renderCoordinateSystem(gl);

			for (SelectionType selectionType : recordSelectionManager.getSelectionTypes()) {
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
	 * Polyline rendering method. All polylines that are contained in the polylineSelectionManager and are of the
	 * selection type specified in renderMode
	 *
	 * FIXME this needs to be changed to iterate over the virtual array, considering the deselected elements
	 *
	 * @param gl
	 *            the GL2 context
	 * @param renderMode
	 *            the type of selection in the selection manager to render
	 */
	private void renderNormalPolylines(GL2 gl, SelectionType selectionType) {

		int nrVisibleLines = tablePerspective.getNrRecords()
				- recordSelectionManager.getNumberOfElements(SelectionType.DESELECTED);

		displayEveryNthPolyline = (tablePerspective.getNrRecords() - recordSelectionManager
				.getNumberOfElements(SelectionType.DESELECTED)) / numberOfRandomElements;

		if (displayEveryNthPolyline == 0) {
			displayEveryNthPolyline = 1;
		}

		PolyLineState renderState = renderStyle.getPolyLineState(selectionType, nrVisibleLines
				/ displayEveryNthPolyline);

		// this loop executes once per polyline
		for (int recordIndex = 0; recordIndex < tablePerspective.getNrRecords(); recordIndex += displayEveryNthPolyline) {
			int recordID = tablePerspective.getRecordPerspective().getVirtualArray().get(recordIndex);
			if (!recordSelectionManager.checkStatus(SelectionType.DESELECTED, recordID))
				renderSingleLine(gl, recordID, selectionType, renderState, false);
		}
	}

	private void renderSelectedPolylines(GL2 gl, SelectionType selectionType) {
		if (!selectionType.isVisible())
			return;
		int nrVisibleLines = recordSelectionManager.getNumberOfElements(selectionType);
		Set<Integer> lines = recordSelectionManager.getElements(selectionType);
		boolean renderAsSelection = true;
		if (lines.size() > 1)
			renderAsSelection = false;
		PolyLineState renderState = renderStyle.getPolyLineState(selectionType, nrVisibleLines
				/ displayEveryNthPolyline);
		renderState.updateOcclusionPrev(nrVisibleLines);
		for (Integer recordID : lines) {

			if (tablePerspective.getRecordPerspective().getVirtualArray().contains(recordID))
				renderSingleLine(gl, recordID, selectionType, renderState, renderAsSelection);
		}
	}

	private void renderSingleLine(GL2 gl, Integer recordID, SelectionType selectionType, PolyLineState renderState,
			boolean renderCaption) {

		gl.glColor4fv(renderState.color, 0);
		gl.glLineWidth(renderState.lineWidth);
		if (detailLevel == EDetailLevel.LOW)
			renderCaption = false;

		float previousX = 0;
		float previousY = 0;
		float currentX = 0;
		float currentY = 0;

		if (selectionType != SelectionType.DESELECTED) {
			gl.glPushName(pickingManager.getPickingID(uniqueID, EPickingType.POLYLINE_SELECTION.name(), recordID));
		}

		if (!renderCaption) {
			gl.glBegin(GL.GL_LINE_STRIP);
		}

		VirtualArray dimensionVA = tablePerspective.getDimensionPerspective().getVirtualArray();
		Table table = dataDomain.getTable();

		// this loop executes once per axis
		for (int dimensionCount = 0; dimensionCount < tablePerspective.getNrDimensions(); dimensionCount++) {

			Integer dimensionID = dimensionVA.get(dimensionCount);

			currentX = axisSpacings.get(dimensionCount);
			currentY = table.getNormalizedValue(dimensionID, recordID);
			if (Float.isNaN(currentY)) {
				currentY = -pixelGLConverter.getGLHeightForPixelHeight(PCRenderStyle.NAN_Y_OFFSET);
			}
			if (dimensionCount != 0) {
				if (renderCaption) {
					gl.glBegin(GL.GL_LINES);
				}

				gl.glVertex3f(previousX, previousY * renderStyle.getAxisHeight(), renderState.zDepth);
				gl.glVertex3f(currentX, currentY * renderStyle.getAxisHeight(), renderState.zDepth);

				if (renderCaption) {
					gl.glEnd();
				}

			}

			if (renderCaption) {
				String rawValueString;
				EDataType rawDataType = table.getRawDataType(dimensionID, recordID);
				if (rawDataType == EDataType.FLOAT) {

					rawValueString = Formatter.formatNumber((float) table.getRaw(dimensionID, recordID));

				} else if (rawDataType == EDataType.STRING) {
					rawValueString = table.getRaw(dimensionID, recordID);
				} else
					throw new IllegalStateException("Unknown Raw Data Type Type");

				renderBoxedYValues(gl, currentX, currentY * renderStyle.getAxisHeight(), rawValueString, selectionType);
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
	 * Render the coordinate system of the parallel coordinates, including the axis captions and axis-specific buttons
	 *
	 * @param gl
	 *            the gl context
	 * @param iNumberAxis
	 */
	private void renderCoordinateSystem(GL2 gl) {
		if (detailLevel.equals(EDetailLevel.LOW))
			return;
		textRenderer.setColor(0, 0, 0, 1);

		int numberOfAxis = tablePerspective.getNrDimensions();
		// draw X-Axis
		gl.glColor4fv(X_AXIS_COLOR, 0);
		gl.glLineWidth(X_AXIS_LINE_WIDTH);

		gl.glPushName(pickingManager.getPickingID(uniqueID, EPickingType.X_AXIS_SELECTION.name(), 1));
		gl.glBegin(GL.GL_LINES);

		gl.glVertex3f(renderStyle.getXAxisStart(), 0.0f, 0.0f);
		gl.glVertex3f(renderStyle.getXAxisEnd(), 0.0f, 0.0f);

		gl.glEnd();
		gl.glPopName();

		// draw all Y-Axis
		Set<Integer> selectedSet = dimensionSelectionManager.getElements(SelectionType.SELECTION);
		Set<Integer> mouseOverSet = dimensionSelectionManager.getElements(SelectionType.MOUSE_OVER);

		int count = 0;
		VirtualArray dimensionVA = tablePerspective.getDimensionPerspective().getVirtualArray();

		while (count < numberOfAxis) {
			float xPosition = axisSpacings.get(count);
			if (selectedSet.contains(dimensionVA.get(count))) {
				gl.glColor4fv(SelectionType.SELECTION.getColor(), 0);
				gl.glLineWidth(Y_AXIS_SELECTED_LINE_WIDTH);
				gl.glEnable(GL2.GL_LINE_STIPPLE);
				gl.glLineStipple(2, (short) 0xAAAA);
			} else if (mouseOverSet.contains(dimensionVA.get(count))) {
				gl.glColor4fv(SelectionType.MOUSE_OVER.getColor(), 0);
				gl.glLineWidth(Y_AXIS_MOUSE_OVER_LINE_WIDTH);
				gl.glEnable(GL2.GL_LINE_STIPPLE);
				gl.glLineStipple(2, (short) 0xAAAA);
			} else {
				gl.glColor4fv(Y_AXIS_COLOR, 0);
				gl.glLineWidth(Y_AXIS_LINE_WIDTH);
			}

			int axisPickingID = pickingManager.getPickingID(uniqueID, EPickingType.Y_AXIS_SELECTION.name(),
					dimensionVA.get(count));
			gl.glPushName(axisPickingID);
			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(xPosition, 0, AXIS_Z);
			gl.glVertex3f(xPosition, renderStyle.getAxisHeight(), AXIS_Z);

			float axisMarkerWidth = pixelGLConverter.getGLWidthForPixelWidth(AXIS_MARKER_WIDTH);
			// Top marker
			gl.glVertex3f(xPosition - axisMarkerWidth, renderStyle.getAxisHeight(), AXIS_Z);
			gl.glVertex3f(xPosition + axisMarkerWidth, renderStyle.getAxisHeight(), AXIS_Z);

			gl.glEnd();
			gl.glDisable(GL2.GL_LINE_STIPPLE);
			gl.glPopName();

			// if (detailLevel == DetailLevel.LO) {
			if (!isRenderedRemote()) {
				// markers on axis
				float fMarkerSpacing = renderStyle.getAxisHeight() / (NUMBER_AXIS_MARKERS + 1);
				for (int iInnerCount = 1; iInnerCount <= NUMBER_AXIS_MARKERS; iInnerCount++) {
					float currentHeight = fMarkerSpacing * iInnerCount;
					if (count == 0) {
						if (dataDomain.getTable() instanceof NumericalTable) {
							float fNumber = (float) ((NumericalTable) dataDomain.getTable()).getRawForNormalized(
									dataTransformation, currentHeight / renderStyle.getAxisHeight());

							float width = pixelGLConverter.getGLWidthForPixelWidth(40);
							float height = pixelGLConverter.getGLHeightForPixelHeight(12);

							float xOrigin = xPosition - width - axisMarkerWidth;

							float yOrigin = currentHeight - height / 2;

							textRenderer.renderTextInBounds(gl, Formatter.formatNumber(fNumber), xOrigin, yOrigin,
									PCRenderStyle.TEXT_ON_LABEL_Z, width, height);

						} else {
							// TODO: dimension based access
						}
					}
					gl.glColor3fv(Y_AXIS_COLOR, 0);
					gl.glBegin(GL.GL_LINES);
					gl.glVertex3f(xPosition - axisMarkerWidth, currentHeight, AXIS_Z);
					gl.glVertex3f(xPosition + axisMarkerWidth, currentHeight, AXIS_Z);
					gl.glEnd();

				}
			}

			String axisLabel = null;

			axisLabel = dataDomain.getDimensionLabel(dimensionVA.get(count));

			gl.glTranslatef(xPosition, renderStyle.getAxisHeight() + renderStyle.getAxisCaptionSpacing(), 0);

			float width = renderStyle.getAxisSpacing(dimensionVA.size());
			if (count == numberOfAxis - 1)
				width = fYTranslation;

			textRenderer.renderTextInBounds(gl, axisLabel, 0, 0, 0.02f, width,
					pixelGLConverter.getGLHeightForPixelHeight(10));

			gl.glTranslatef(-xPosition, -(renderStyle.getAxisHeight() + renderStyle.getAxisCaptionSpacing()), 0);

			if (!isRenderedRemote()) {

				float xOrigin = axisSpacings.get(count);
				float nanYOrigin = -pixelGLConverter.getGLHeightForPixelHeight(PCRenderStyle.NAN_Y_OFFSET);

				// nan texture is 16x16 - set half of that
				float buttonWidht = pixelGLConverter.getGLWidthForPixelWidth(6);
				float buttonHeight = pixelGLConverter.getGLHeightForPixelHeight(6);

				// float nan

				Vec3f lowerLeftCorner = new Vec3f(xOrigin - buttonWidht, nanYOrigin - buttonHeight, PCRenderStyle.NAN_Z);
				Vec3f lowerRightCorner = new Vec3f(xOrigin + buttonWidht, nanYOrigin - buttonHeight,
						PCRenderStyle.NAN_Z);
				Vec3f upperRightCorner = new Vec3f(xOrigin + buttonWidht, nanYOrigin + buttonHeight,
						PCRenderStyle.NAN_Z);
				Vec3f upperLeftCorner = new Vec3f(xOrigin - buttonWidht, nanYOrigin + buttonHeight, PCRenderStyle.NAN_Z);

				int pickingID = pickingManager.getPickingID(uniqueID, EPickingType.REMOVE_NAN.name(),
						dimensionVA.get(count));
				gl.glPushName(pickingID);

				textureManager.renderTexture(gl, PCRenderStyle.NAN, lowerLeftCorner, lowerRightCorner,
						upperRightCorner, upperLeftCorner, 1, 1, 1, 1);

				gl.glPopName();

				// render Buttons

				pickingID = -1;

				gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA);

				// the gate add button on upperBound

				float yGateAddOrigin = renderStyle.getAxisHeight();
				pickingID = pickingManager.getPickingID(uniqueID, EPickingType.ADD_GATE.name(), dimensionVA.get(count));

				// gate add texture - 16 x 32
				// half width, full height
				buttonWidht = pixelGLConverter.getGLWidthForPixelWidth(8);
				buttonHeight = pixelGLConverter.getGLHeightForPixelHeight(32);

				lowerLeftCorner.set(xOrigin - buttonWidht, yGateAddOrigin, AXIS_Z);
				lowerRightCorner.set(xOrigin + buttonWidht, yGateAddOrigin, AXIS_Z);
				upperRightCorner.set(xOrigin + buttonWidht, yGateAddOrigin + buttonHeight, AXIS_Z);
				upperLeftCorner.set(xOrigin - buttonWidht, yGateAddOrigin + buttonHeight, AXIS_Z);

				gl.glPushName(pickingID);

				textureManager.renderTexture(gl, PCRenderStyle.ADD_GATE, lowerLeftCorner, lowerRightCorner,
						upperRightCorner, upperLeftCorner, 1, 1, 1, 1);

				gl.glPopName();

				float fYDropOrigin = -pixelGLConverter.getGLHeightForPixelHeight(20);

				if (selectedSet.contains(dimensionVA.get(count)) || mouseOverSet.contains(dimensionVA.get(count))) {

					// the mouse over drops
					// texture is 63x62

					buttonWidht = pixelGLConverter.getGLWidthForPixelWidth(31);
					buttonHeight = pixelGLConverter.getGLHeightForPixelHeight(63);

					lowerLeftCorner.set(xOrigin - buttonWidht, fYDropOrigin - buttonHeight, AXIS_Z + 0.005f);
					lowerRightCorner.set(xOrigin + buttonWidht, fYDropOrigin - buttonHeight, AXIS_Z + 0.005f);
					upperRightCorner.set(xOrigin + buttonWidht, fYDropOrigin, AXIS_Z + 0.005f);
					upperLeftCorner.set(xOrigin - buttonWidht, fYDropOrigin, AXIS_Z + 0.005f);

					if (changeDropOnAxisNumber == count) {
						// tempTexture = textureManager.getIconTexture(gl,
						// dropTexture);
						textureManager.renderTexture(gl, dropTexture, lowerLeftCorner, lowerRightCorner,
								upperRightCorner, upperLeftCorner, 1, 1, 1, 1);

						if (!bWasAxisMoved) {
							dropTexture = PCRenderStyle.DROP_NORMAL;
						}
					} else {
						textureManager.renderTexture(gl, PCRenderStyle.DROP_NORMAL, lowerLeftCorner, lowerRightCorner,
								upperRightCorner, upperLeftCorner, 1, 1, 1, 1);
					}

					// picking for the sub-parts of the drop texture

					// center drop has width of 30 starts at position 16
					buttonWidht = pixelGLConverter.getGLWidthForPixelWidth(15);
					buttonHeight = pixelGLConverter.getGLHeightForPixelHeight(63);

					pickingID = pickingManager.getPickingID(uniqueID, EPickingType.MOVE_AXIS.name(), count);
					gl.glColor4f(0, 0, 0, 0f);
					gl.glPushName(pickingID);
					gl.glBegin(GL.GL_TRIANGLES);
					gl.glVertex3f(xOrigin, fYDropOrigin, AXIS_Z + 0.01f);
					gl.glVertex3f(xOrigin + buttonWidht, fYDropOrigin - buttonHeight, AXIS_Z + 0.01f);
					gl.glVertex3f(xOrigin - buttonWidht, fYDropOrigin - buttonHeight, AXIS_Z + 0.01f);
					gl.glEnd();
					gl.glPopName();

					// left drop
					buttonHeight = pixelGLConverter.getGLHeightForPixelHeight(63 - 14);
					float buttonOuterBorder = pixelGLConverter.getGLWidthForPixelWidth(31);
					buttonWidht = pixelGLConverter.getGLWidthForPixelWidth(16);

					float buttonOuterHight = pixelGLConverter.getGLHeightForPixelHeight(16);

					pickingID = pickingManager.getPickingID(uniqueID, EPickingType.DUPLICATE_AXIS.name(), count);
					// gl.glColor4f(0, 1, 0, 0.5f);
					gl.glPushName(pickingID);
					gl.glBegin(GL2.GL_POLYGON);
					gl.glVertex3f(xOrigin, fYDropOrigin, AXIS_Z + 0.01f);
					gl.glVertex3f(xOrigin - buttonOuterBorder, fYDropOrigin - buttonHeight + buttonOuterHight,
							AXIS_Z + 0.01f);
					gl.glVertex3f(xOrigin - buttonOuterBorder, fYDropOrigin - buttonHeight, AXIS_Z + 0.01f);
					gl.glVertex3f(xOrigin - buttonOuterBorder + buttonWidht, fYDropOrigin - buttonHeight,
							AXIS_Z + 0.01f);
					gl.glEnd();
					gl.glPopName();

					pickingID = pickingManager.getPickingID(uniqueID, EPickingType.REMOVE_AXIS.name(), count);
					// gl.glColor4f(0, 0, 1, 0.5f);
					gl.glPushName(pickingID);
					gl.glBegin(GL2.GL_POLYGON);
					gl.glVertex3f(xOrigin, fYDropOrigin, AXIS_Z + 0.01f);
					gl.glVertex3f(xOrigin + buttonOuterBorder, fYDropOrigin - buttonHeight + buttonOuterHight,
							AXIS_Z + 0.01f);
					gl.glVertex3f(xOrigin + buttonOuterBorder, fYDropOrigin - buttonHeight, AXIS_Z + 0.01f);
					gl.glVertex3f(xOrigin + buttonOuterBorder - buttonWidht, fYDropOrigin - buttonHeight,
							AXIS_Z + 0.01f);
					gl.glEnd();
					gl.glPopName();

				} else {

					// standard lowerBound drop texture - 16 x 32
					// half width, full height
					buttonWidht = pixelGLConverter.getGLWidthForPixelWidth(8);
					buttonHeight = pixelGLConverter.getGLHeightForPixelHeight(32);

					pickingID = pickingManager.getPickingID(uniqueID, EPickingType.MOVE_AXIS.name(), count);

					gl.glPushAttrib(GL2.GL_CURRENT_BIT | GL2.GL_LINE_BIT);
					gl.glPushName(pickingID);

					lowerLeftCorner.set(xOrigin - buttonWidht, fYDropOrigin - buttonHeight, AXIS_Z);
					lowerRightCorner.set(xOrigin + buttonWidht, fYDropOrigin - buttonHeight, AXIS_Z);
					upperRightCorner.set(xOrigin + buttonWidht, fYDropOrigin, AXIS_Z);
					upperLeftCorner.set(xOrigin - buttonWidht, fYDropOrigin, AXIS_Z);

					textureManager.renderTexture(gl, PCRenderStyle.SMALL_DROP, lowerLeftCorner, lowerRightCorner,
							upperRightCorner, upperLeftCorner, 1, 1, 1, 1);

					gl.glPopName();
					gl.glPopAttrib();

				}
				gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
			}

			count++;
		}
	}

	/**
	 * Render the gates and update the fArGateHeights for the selection/unselection
	 *
	 * @param gl
	 * @param iNumberAxis
	 */
	private void renderGates(GL2 gl) {

		if (detailLevel != EDetailLevel.HIGH)
			return;

		for (Integer iGateID : hashGates.keySet()) {
			// Gate ID / 1000 is axis ID

			Gate gate = hashGates.get(iGateID);
			int axisID = gate.getAxisID();
			// Pair<Float, Float> gate = hashGates.get(iGateID);
			// TODO for all indices

			ArrayList<Integer> axesIndices = tablePerspective.getDimensionPerspective().getVirtualArray()
					.indicesOf(axisID);
			for (int axisIndex : axesIndices) {
				float currentPosition = axisSpacings.get(axisIndex);
				gate.setxPosition(currentPosition);
				// String label = table.get(iAxisID).getLabel();

				gate.draw(gl);
				// renderSingleGate(gl, gate, iAxisID, iGateID,
				// fCurrentPosition);
			}
		}

	}

	private void renderMasterGate(GL2 gl) {
		if (detailLevel != EDetailLevel.HIGH)
			return;

		gl.glColor4f(0, 0, 0, 1f);

		gl.glLineWidth(PCRenderStyle.Y_AXIS_LINE_WIDTH);

		float xOrigin = -pixelGLConverter.getGLWidthForPixelWidth(40);
		float axisMarkerWidth = pixelGLConverter.getGLWidthForPixelWidth(AXIS_MARKER_WIDTH);

		gl.glBegin(GL.GL_LINES);
		gl.glVertex3f(xOrigin, 0, AXIS_Z);
		gl.glVertex3f(xOrigin, renderStyle.getAxisHeight(), AXIS_Z);
		gl.glVertex3f(xOrigin - axisMarkerWidth, 0, AXIS_Z);
		gl.glVertex3f(xOrigin + axisMarkerWidth, 0, AXIS_Z);
		gl.glVertex3f(xOrigin - axisMarkerWidth, renderStyle.getAxisHeight(), AXIS_Z);
		gl.glVertex3f(xOrigin + axisMarkerWidth, renderStyle.getAxisHeight(), AXIS_Z);
		gl.glEnd();

		gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA);

		// the gate add button
		float yGateAddOrigin = renderStyle.getAxisHeight();
		int pickingID = pickingManager.getPickingID(uniqueID, EPickingType.ADD_MASTER_GATE.name(), 1);

		gl.glPushName(pickingID);
		// the gate add button on upperBound

		// gate add texture - 16 x 32
		// half width, full height
		float buttonWidht = pixelGLConverter.getGLWidthForPixelWidth(8);
		float buttonHeight = pixelGLConverter.getGLHeightForPixelHeight(32);

		Vec3f lowerLeftCorner = new Vec3f(xOrigin - buttonWidht, yGateAddOrigin, AXIS_Z);
		Vec3f lowerRightCorner = new Vec3f(xOrigin + buttonWidht, yGateAddOrigin, AXIS_Z);
		Vec3f upperRightCorner = new Vec3f(xOrigin + buttonWidht, yGateAddOrigin + buttonHeight, AXIS_Z);
		Vec3f upperLeftCorner = new Vec3f(xOrigin - buttonWidht, yGateAddOrigin + buttonHeight, AXIS_Z);

		textureManager.renderTexture(gl, PCRenderStyle.ADD_GATE, lowerLeftCorner, lowerRightCorner, upperRightCorner,
				upperLeftCorner, 1, 1, 1, 1);

		gl.glPopName();

		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

		for (Integer iGateID : hashMasterGates.keySet()) {
			Gate gate = hashMasterGates.get(iGateID);

			float bottom = gate.getLowerBound() * renderStyle.getAxisHeight();
			float top = gate.getUpperBound() * renderStyle.getAxisHeight();

			gl.glColor4fv(PCRenderStyle.GATE_BODY_COLOR, 0);
			gl.glBegin(GL2.GL_POLYGON);
			gl.glVertex3f(xOrigin, bottom, 0);
			gl.glVertex3f(viewFrustum.getWidth(), bottom, 0);
			gl.glVertex3f(viewFrustum.getWidth(), top, 0);

			gl.glVertex3f(xOrigin, top, 0);
			gl.glEnd();

			gate.setxPosition(xOrigin);
			gate.draw(gl);

		}

	}

	/**
	 * Render the captions on the axis
	 *
	 * @param gl
	 * @param xOrigin
	 * @param yOrigin
	 * @param renderMode
	 */
	private void renderBoxedYValues(GL2 gl, float xOrigin, float yOrigin, String string, SelectionType renderMode) {

		// don't render values that are below the y axis
		if (yOrigin < 0)
			return;

		gl.glPushAttrib(GL2.GL_CURRENT_BIT | GL2.GL_LINE_BIT);
		gl.glLineWidth(Y_AXIS_LINE_WIDTH);
		gl.glColor4fv(Y_AXIS_COLOR, 0);

		float widthSpacing = pixelGLConverter.getGLWidthForPixelWidth(2);
		float heightSpacing = pixelGLConverter.getGLWidthForPixelWidth(2);

		float backPlaneWidth = pixelGLConverter.getGLWidthForPixelWidth(40);
		float maxWidth = renderStyle.getAxisSpacing(tablePerspective.getNrDimensions());
		if (backPlaneWidth > maxWidth)
			backPlaneWidth = maxWidth;

		float fBackPlaneHeight = pixelGLConverter.getGLHeightForPixelHeight(12);
		float xTextOrigin = xOrigin + 2 * pixelGLConverter.getGLWidthForPixelWidth(AXIS_MARKER_WIDTH);
		float yTextOrigin = yOrigin;

		gl.glColor4f(1f, 1f, 1f, 0.8f);
		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex3f(xTextOrigin - widthSpacing, yTextOrigin - heightSpacing, LABEL_Z);
		gl.glVertex3f(xTextOrigin + backPlaneWidth, yTextOrigin - heightSpacing, LABEL_Z);
		gl.glVertex3f(xTextOrigin + backPlaneWidth, yTextOrigin + fBackPlaneHeight, LABEL_Z);
		gl.glVertex3f(xTextOrigin - widthSpacing, yTextOrigin + fBackPlaneHeight, LABEL_Z);
		gl.glEnd();

		textRenderer.renderTextInBounds(gl, string, xTextOrigin, yTextOrigin, PCRenderStyle.TEXT_ON_LABEL_Z,
				backPlaneWidth, fBackPlaneHeight);
		gl.glPopAttrib();
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

		int pixelHeight = parentGLCanvas.getHeight() - currentPoint.y;

		float x = pixelGLConverter.getGLWidthForPixelWidth(currentPoint.x);
		float y = pixelGLConverter.getGLHeightForPixelHeight(pixelHeight);

		// float[] fArTargetWorldCoordinates = GLCoordinateUtils.convertWindowCoordinatesToWorldCoordinates(gl,
		// currentPoint.x, currentPoint.y);

		// todo only valid for one gate
		Gate gate = hashGates.get(iDraggedGateNumber);

		if (gate == null) {
			gate = hashMasterGates.get(iDraggedGateNumber);
			if (gate == null)
				return;
		}
		gate.handleDragging(gl, x, y, draggedObject, bIsGateDraggingFirstTime);
		bIsGateDraggingFirstTime = false;

		isDisplayListDirty = true;

		if (glMouseListener.wasMouseReleased()) {

			bIsDraggingActive = false;
		}

	}

	/**
	 * Unselect all lines that are deselected with the gates
	 *
	 * @param changeDropOnAxisNumber
	 */
	// TODO revise
	private void handleGateUnselection() {

		float currentValue = -1;
		for (Integer iGateID : hashGates.keySet()) {

			ArrayList<Integer> alCurrentGateBlocks = hashIsGateBlocking.get(iGateID);
			if (alCurrentGateBlocks == null)
				return;
			alCurrentGateBlocks.clear();
			Gate gate = hashGates.get(iGateID);
			int axisID = gate.getAxisID();
			if (axisID == -1)
				continue;
			int dimensionID = tablePerspective.getDimensionPerspective().getVirtualArray().get(axisID);

			for (int recordID : tablePerspective.getRecordPerspective().getVirtualArray()) {

				currentValue = dataDomain.getTable().getNormalizedValue(dimensionID, recordID);

				if (Float.isNaN(currentValue)) {
					continue;
				}

				if (currentValue <= gate.getUpperBound() && currentValue >= gate.getLowerBound()) {
					alCurrentGateBlocks.add(recordID);
				}
			}
		}
	}

	private void handleNANUnselection() {

		float currentValue = 0;
		hashIsNANBlocking.clear();
		for (Integer axisID : hashExcludeNAN.keySet()) {
			ArrayList<Integer> deselectedLines = new ArrayList<Integer>();
			for (int polylineIndex : tablePerspective.getRecordPerspective().getVirtualArray()) {

				currentValue = dataDomain.getTable().getNormalizedValue(axisID, polylineIndex);

				if (Float.isNaN(currentValue)) {
					deselectedLines.add(polylineIndex);
				}
			}
			hashIsNANBlocking.put(axisID, deselectedLines);
		}
	}

	private void handleMasterGateUnselection() {

		float currentValue = -1;
		for (Integer iGateID : hashMasterGates.keySet()) {

			ArrayList<Integer> alCurrentGateBlocks = hashIsGateBlocking.get(iGateID);
			if (alCurrentGateBlocks == null)
				return;
			alCurrentGateBlocks.clear();
			Gate gate = hashMasterGates.get(iGateID);
			for (int recordID : tablePerspective.getRecordPerspective().getVirtualArray()) {
				boolean bIsBlocking = true;
				for (int dimensionID : tablePerspective.getDimensionPerspective().getVirtualArray()) {

					currentValue = dataDomain.getTable().getNormalizedValue(dataTransformation, dimensionID, recordID);

					if (Float.isNaN(currentValue)) {
						continue;
					}

					if (currentValue <= gate.getUpperBound() && currentValue >= gate.getLowerBound()) {
						bIsBlocking = true;
					} else {
						bIsBlocking = false;
						break;
					}
				}
				if (bIsBlocking) {
					alCurrentGateBlocks.add(recordID);
				}
			}
		}
	}

	@Override
	protected void reactOnExternalSelection(SelectionDelta delta) {
		handleUnselection();
		resetAxisSpacing();
	}

	@Override
	public void handleDimensionVAUpdate(String dimensionPerspectiveID) {
		if (!tablePerspective.getDimensionPerspective().getPerspectiveID().equals(dimensionPerspectiveID))
			return;
		super.handleDimensionVAUpdate(dimensionPerspectiveID);
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
	 * TODO: revise this, not very performance friendly, especially the clearing of the DESELECTED
	 */

	private void handleUnselection() {
		if (!hasFilterChanged)
			return;

		hasFilterChanged = false;
		handleGateUnselection();
		handleNANUnselection();
		if (dataDomain.getTable().isDataHomogeneous())
			handleMasterGateUnselection();

		recordSelectionManager.clearSelection(SelectionType.DESELECTED);

		for (ArrayList<Integer> alCurrent : hashIsGateBlocking.values()) {
			recordSelectionManager.addToType(SelectionType.DESELECTED, alCurrent);
		}

		for (ArrayList<Integer> alCurrent : alIsAngleBlocking) {
			recordSelectionManager.addToType(SelectionType.DESELECTED, alCurrent);
		}

		for (ArrayList<Integer> alCurrent : hashIsNANBlocking.values()) {
			recordSelectionManager.addToType(SelectionType.DESELECTED, alCurrent);
		}

		if (bIsDraggingActive || bIsAngularBrushingActive) {
			triggerSelectionUpdate();
		}
	}

	private void triggerSelectionUpdate() {
		SelectionUpdateEvent selectionUpdateEvent = new SelectionUpdateEvent();
		selectionUpdateEvent.setEventSpace(dataDomain.getDataDomainID());
		selectionUpdateEvent.setSelectionDelta(recordSelectionManager.getDelta());
		selectionUpdateEvent.setSender(this);
		eventPublisher.triggerEvent(selectionUpdateEvent);
	}

	protected void registerPickingListeners() {

		addTypePickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {

				if (bAngularBrushingSelectPolyline) {
					bAngularBrushingSelectPolyline = false;
					bIsAngularBrushingActive = true;
					iSelectedLineID = pick.getObjectID();
					linePick = pick;
					bIsAngularBrushingFirstTime = true;
				}

				handleRecordSelection(SelectionType.SELECTION, pick.getObjectID());
			}

			@Override
			public void mouseOver(Pick pick) {
				handleRecordSelection(SelectionType.MOUSE_OVER, pick.getObjectID());
			}

			@Override
			public void rightClicked(Pick pick) {
				handleRecordSelection(SelectionType.SELECTION, pick.getObjectID());
				if (dataDomain instanceof GeneticDataDomain) {

					GeneMenuItemContainer contexMenuItemContainer = new GeneMenuItemContainer();
					contexMenuItemContainer.setDataDomain(dataDomain);
					contexMenuItemContainer.setData(recordIDType, pick.getObjectID());
					contextMenuCreator.addContextMenuItemContainer(contexMenuItemContainer);
				}

			}

		}, EPickingType.POLYLINE_SELECTION.name());

		addTypePickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				handleDimensionSelection(SelectionType.SELECTION, pick.getObjectID());
			}

			@Override
			public void mouseOver(Pick pick) {
				handleDimensionSelection(SelectionType.MOUSE_OVER, pick.getObjectID());
			}

			@Override
			public void rightClicked(Pick pick) {
				handleDimensionRightClick(pick.getObjectID());
				handleDimensionSelection(SelectionType.SELECTION, pick.getObjectID());

			}

		}, EPickingType.Y_AXIS_SELECTION.name());

		addTypePickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				bIsDraggingActive = true;
				draggedObject = EPickingType.GATE_TIP_SELECTION;
				iDraggedGateNumber = pick.getObjectID();

			}

			@Override
			public void mouseOver(Pick pick) {
				iDraggedGateNumber = pick.getObjectID();
				draggedObject = EPickingType.GATE_TIP_SELECTION;
				setDisplayListDirty();
			}

		}, EPickingType.GATE_TIP_SELECTION.name());

		addTypePickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				bIsDraggingActive = true;
				draggedObject = EPickingType.GATE_BOTTOM_SELECTION;
				iDraggedGateNumber = pick.getObjectID();
			}

			@Override
			public void mouseOver(Pick pick) {
				iDraggedGateNumber = pick.getObjectID();
				draggedObject = EPickingType.GATE_BOTTOM_SELECTION;
				setDisplayListDirty();
			}
		}, EPickingType.GATE_BOTTOM_SELECTION.name());

		addTypePickingListener(new APickingListener(

		) {

			@Override
			public void clicked(Pick pick) {
				bIsDraggingActive = true;
				bIsGateDraggingFirstTime = true;
				draggedObject = EPickingType.GATE_BODY_SELECTION;
				iDraggedGateNumber = pick.getObjectID();
			}

			@Override
			public void mouseOver(Pick pick) {
				iDraggedGateNumber = pick.getObjectID();
				draggedObject = EPickingType.GATE_BODY_SELECTION;
				setDisplayListDirty();

			}
		}, EPickingType.GATE_BODY_SELECTION.name());

		addTypePickingListener(new APickingListener() {
			@Override
			public void clicked(Pick pick) {
				int pickingID = pick.getObjectID();
				VirtualArray dimensionVA = tablePerspective.getDimensionPerspective().getVirtualArray();
				if (dimensionVA.occurencesOf(dimensionVA.get(pickingID)) == 1) {
					removeGate(dimensionVA.get(pickingID));
				}

				VirtualArrayDelta vaDelta = new VirtualArrayDelta(tablePerspective.getDimensionPerspective()
						.getPerspectiveID(), dimensionIDType);
				vaDelta.add(VADeltaItem.remove(pickingID));

				Integer dimensionID = dimensionVA.get(pickingID);
				triggerDimensionFilterEvent(vaDelta, "Removed " + dataDomain.getDimensionLabel(dimensionID));
				setDisplayListDirty();
				resetAxisSpacing();

			}

			@Override
			public void mouseOver(Pick pick) {
				dropTexture = PCRenderStyle.DROP_DELETE;
				changeDropOnAxisNumber = pick.getObjectID();
				setDisplayListDirty();

			}
		}, EPickingType.REMOVE_AXIS.name());

		addTypePickingListener(new APickingListener() {
			@Override
			public void clicked(Pick pick) {
				bWasAxisMoved = true;
				bWasAxisDraggedFirstTime = true;
				iMovedAxisPosition = pick.getObjectID();
				setDisplayListDirty();
			}

			@Override
			public void mouseOver(Pick pick) {
				dropTexture = PCRenderStyle.DROP_MOVE;
				changeDropOnAxisNumber = pick.getObjectID();
				setDisplayListDirty();

			}

			@Override
			protected void rightClicked(Pick pick) {

				handleDimensionRightClick(pick.getObjectID());
			}

		}, EPickingType.MOVE_AXIS.name());

		addTypePickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				int pickingID = pick.getObjectID();
				if (pickingID >= 0) {
					// dimensionVA.copy(pickingID);
					VirtualArrayDelta vaDelta = new VirtualArrayDelta(tablePerspective.getDimensionPerspective()
							.getPerspectiveID(), dimensionIDType);
					vaDelta.add(VADeltaItem.copy(pickingID));
					triggerDimensionFilterEvent(
							vaDelta,
							"Copied "
									+ dataDomain.getDimensionLabel(tablePerspective.getDimensionPerspective()
											.getVirtualArray().get(pickingID)));

					setDisplayListDirty();
					// resetSelections();
					// initGates();
					resetAxisSpacing();
				}

			}

			@Override
			public void mouseOver(Pick pick) {
				dropTexture = PCRenderStyle.DROP_DUPLICATE;
				changeDropOnAxisNumber = pick.getObjectID();
				setDisplayListDirty();
			}
		}, EPickingType.DUPLICATE_AXIS.name());

		addTypePickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				hasFilterChanged = true;
				Gate gate = new Gate(getSelf(), ++iGateCounter, pick.getObjectID(), 0, 0.5f);
				hashGates.put(iGateCounter, gate);
				hashIsGateBlocking.put(iGateCounter, new ArrayList<Integer>());
				handleUnselection();
				triggerSelectionUpdate();
				setDisplayListDirty();

			}

		}, EPickingType.ADD_GATE.name());

		addTypePickingListener(new APickingListener() {
			@Override
			public void clicked(Pick pick) {
				hasFilterChanged = true;
				Gate gate = new Gate(getSelf(), ++iGateCounter, -1, 0, 0.5f);
				// gate.setMasterGate(true);
				hashMasterGates.put(iGateCounter, gate);
				hashIsGateBlocking.put(iGateCounter, new ArrayList<Integer>());
				handleUnselection();
				triggerSelectionUpdate();
				setDisplayListDirty();
			}
		}, EPickingType.ADD_MASTER_GATE.name());

		addTypePickingListener(new APickingListener() {
			@Override
			public void clicked(Pick pick) {
				int pickingID = pick.getObjectID();
				hasFilterChanged = true;
				// either the gate belongs to the normal or to the
				// master gates
				if (hashGates.remove(pickingID) == null)
					hashMasterGates.remove(pickingID);

				hashIsGateBlocking.remove(pickingID);

				handleUnselection();
				triggerSelectionUpdate();
				setDisplayListDirty();
			}

		}, EPickingType.REMOVE_GATE.name());

		APickingListener angularPickingListener = new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				bIsAngularDraggingActive = true;
			}

			@Override
			public void mouseOver(Pick pick) {
				bIsAngularDraggingActive = true;
			}
		};

		addTypePickingListener(angularPickingListener, EPickingType.ANGULAR_UPPER.name());

		addTypePickingListener(angularPickingListener, EPickingType.ANGULAR_LOWER.name());

		addTypePickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				int pickingID = pick.getObjectID();
				hasFilterChanged = true;
				if (hashExcludeNAN.containsKey(pickingID)) {
					hashExcludeNAN.remove(pickingID);
				} else {
					hashExcludeNAN.put(pickingID, null);
				}
				setDisplayListDirty();
			}
		}, EPickingType.REMOVE_NAN.name());
	}

	private GLParallelCoordinates getSelf() {
		return this;
	}

	private void handleRecordSelection(SelectionType selectionType, Integer id) {
		if (recordSelectionManager.checkStatus(selectionType, id)) {
			return;
		}

		recordSelectionManager.clearSelection(selectionType);

		recordSelectionManager.addToType(selectionType, id);
		// recordSelectionManager
		// .addConnectionID(generalManager.getIDCreator().createID(ManagedObjectType.CONNECTION), id);

		if (!bAngularBrushingSelectPolyline) {
			SelectionDelta selectionDelta = recordSelectionManager.getDelta();
			SelectionUpdateEvent event = new SelectionUpdateEvent();
			event.setSender(this);
			event.setEventSpace(dataDomain.getDataDomainID());
			event.setSelectionDelta(selectionDelta);
			eventPublisher.triggerEvent(event);
		}

		setDisplayListDirty();
	}

	private void handleDimensionSelection(SelectionType selectionType, Integer id) {
		dimensionSelectionManager.clearSelection(selectionType);
		dimensionSelectionManager.addToType(selectionType, id);

		SelectionDelta selectionDelta = dimensionSelectionManager.getDelta();
		SelectionUpdateEvent event = new SelectionUpdateEvent();
		event.setSender(this);
		event.setEventSpace(dataDomain.getDataDomainID());
		event.setSelectionDelta(selectionDelta);
		eventPublisher.triggerEvent(event);
		setDisplayListDirty();
	}

	private void handleDimensionRightClick(Integer dimensionID) {

		AContextMenuItem menuItem = new BookmarkMenuItem("Bookmark "
				+ dataDomain.getDimensionLabel(dimensionIDType, dimensionID), dimensionIDType, dimensionID,
				dataDomain.getDataDomainID());
		contextMenuCreator.addContextMenuItem(menuItem);

		SortByDataEvent sortEvent = new SortByDataEvent(dataDomain.getDataDomainID(),
				tablePerspective.getTablePerspectiveKey(), tablePerspective.getRecordPerspective().getIdType(),
				dimensionID);
		sortEvent.setSender(this);

		AContextMenuItem sortByDimensionItem = new GenericContextMenuItem("Sort by this axis ", sortEvent);

		contextMenuCreator.addContextMenuItem(sortByDimensionItem);

	}

	private void triggerDimensionFilterEvent(VirtualArrayDelta delta, String label) {

		Filter filter = new Filter(tablePerspective.getDimensionPerspective().getPerspectiveID());

		filter.setVADelta(delta);
		filter.setLabel(label);
		filter.setDataDomain(dataDomain);

		NewFilterEvent filterEvent = new NewFilterEvent();
		filterEvent.setFilter(filter);
		filterEvent.setSender(this);
		filterEvent.setEventSpace(dataDomain.getDataDomainID());

		eventPublisher.triggerEvent(filterEvent);
	}

	private void triggerRecordFilterEvent(VirtualArrayDelta delta, String label) {

		Filter filter = new Filter(tablePerspective.getRecordPerspective().getPerspectiveID());
		filter.setVADelta(delta);
		filter.setLabel(label);
		filter.setDataDomain(dataDomain);

		NewFilterEvent filterEvent = new NewFilterEvent();
		filterEvent.setFilter(filter);
		filterEvent.setSender(this);
		filterEvent.setEventSpace(dataDomain.getDataDomainID());

		eventPublisher.triggerEvent(filterEvent);
	}

	private void handleAngularBrushing(final GL2 gl) {
		hasFilterChanged = true;
		if (bIsAngularBrushingFirstTime) {
			fCurrentAngle = fDefaultAngle;
			Point currentPoint = linePick.getPickedPoint();
			float[] fArPoint = GLCoordinateUtils.convertWindowCoordinatesToWorldCoordinates(gl, currentPoint.x,
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

		int leftAxisIndex;
		int rightAxisIndex;

		VirtualArray dimensionVA = tablePerspective.getDimensionPerspective().getVirtualArray();
		VirtualArray recordVA = tablePerspective.getRecordPerspective().getVirtualArray();
		Table table = dataDomain.getTable();

		leftAxisIndex = dimensionVA.get(iPosition);
		rightAxisIndex = dimensionVA.get(iPosition + 1);

		Vec3f vecLeftPoint = new Vec3f(0, 0, 0);
		Vec3f vecRightPoint = new Vec3f(0, 0, 0);

		vecLeftPoint.setY(table.getNormalizedValue(leftAxisIndex, iSelectedLineID) * renderStyle.getAxisHeight());
		vecRightPoint.setY(table.getNormalizedValue(rightAxisIndex, iSelectedLineID) * renderStyle.getAxisHeight());

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
			float fArPoint[] = GLCoordinateUtils.convertWindowCoordinatesToWorldCoordinates(gl, pickedPoint.x,
					pickedPoint.y);
			Vec3f vecPickedPoint = new Vec3f(fArPoint[0], fArPoint[1], 0.01f);
			Vec3f vecTempLine = vecPickedPoint.minus(vecTriangleOrigin);

			fCurrentAngle = getAngle(vecTempLine, vecCenterLine);

			setDisplayListDirty();
		}

		rotf.set(new Vec3f(0, 0, 1), fCurrentAngle);

		Vec3f vecUpperPoint = rotf.rotateVector(vecCenterLine);
		rotf.set(new Vec3f(0, 0, 1), -fCurrentAngle);
		Vec3f vecLowerPoint = rotf.rotateVector(vecCenterLine);

		vecUpperPoint.add(vecTriangleOrigin);
		vecLowerPoint.add(vecTriangleOrigin);

		gl.glColor4fv(ANGULAR_COLOR, 0);
		gl.glLineWidth(ANGLUAR_LINE_WIDTH);

		gl.glPushName(pickingManager.getPickingID(uniqueID, EPickingType.ANGULAR_UPPER.name(), iPosition));
		gl.glBegin(GL.GL_LINES);
		gl.glVertex3f(vecTriangleOrigin.x(), vecTriangleOrigin.y(), vecTriangleOrigin.z() + 0.02f);
		gl.glVertex3f(vecUpperPoint.x(), vecUpperPoint.y(), vecUpperPoint.z() + 0.02f);
		gl.glEnd();
		gl.glPopName();

		gl.glPushName(pickingManager.getPickingID(uniqueID, EPickingType.ANGULAR_UPPER.name(), iPosition));
		gl.glBegin(GL.GL_LINES);
		gl.glVertex3f(vecTriangleOrigin.x(), vecTriangleOrigin.y(), vecTriangleOrigin.z() + 0.02f);
		gl.glVertex3f(vecLowerPoint.x(), vecLowerPoint.y(), vecLowerPoint.z() + 0.02f);
		gl.glEnd();
		gl.glPopName();

		// draw angle polygon

		gl.glColor4fv(ANGULAR_POLYGON_COLOR, 0);
		// gl.glColor4f(1, 0, 0, 0.5f);
		gl.glBegin(GL2.GL_POLYGON);
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

		gl.glBegin(GL2.GL_POLYGON);
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

		for (Integer iCurrent : recordVA) {

			vecLeftPoint.setY(table.getNormalizedValue(leftAxisIndex, iCurrent) * renderStyle.getAxisHeight());
			vecRightPoint.setY(table.getNormalizedValue(rightAxisIndex, iCurrent) * renderStyle.getAxisHeight());

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

		float[] fArTargetWorldCoordinates = GLCoordinateUtils.convertWindowCoordinatesToWorldCoordinates(gl,
				currentPoint.x, currentPoint.y);

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

		VirtualArray dimensionVA = tablePerspective.getDimensionPerspective().getVirtualArray();

		if (iSwitchAxisWithThis != -1) {
			dimensionVA.move(iMovedAxisPosition, iSwitchAxisWithThis);
			axisSpacings.remove(iMovedAxisPosition);
			axisSpacings.add(iSwitchAxisWithThis, fWidth);

			VirtualArrayDelta vaDelta = new VirtualArrayDelta(tablePerspective.getDimensionPerspective()
					.getPerspectiveID(), dimensionIDType);
			vaDelta.add(VADeltaItem.move(iMovedAxisPosition, iSwitchAxisWithThis));
			triggerDimensionFilterEvent(vaDelta,
					"Moved " + dataDomain.getDimensionLabel(dimensionVA.get(iMovedAxisPosition)));
			iMovedAxisPosition = iSwitchAxisWithThis;
		}

		else {
			axisSpacings.set(iMovedAxisPosition, fWidth);
		}
		setDisplayListDirty();

	}

	public void resetAxisSpacing() {
		axisSpacings.clear();
		int numAxis = tablePerspective.getNrDimensions();
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
		SerializedParallelCoordinatesView serializedForm = new SerializedParallelCoordinatesView(this);
		return serializedForm;
	}

	@Override
	public void destroyViewSpecificContent(GL2 gl) {
		// selectionTransformer.destroy();
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();

		ApplyCurrentSelectionToVirtualArrayListener applyCurrentSelectionToVirtualArrayListener = new ApplyCurrentSelectionToVirtualArrayListener();
		applyCurrentSelectionToVirtualArrayListener.setHandler(this);
		listeners.register(ApplyCurrentSelectionToVirtualArrayEvent.class, applyCurrentSelectionToVirtualArrayListener);

		ResetAxisSpacingListener resetAxisSpacingListener = new ResetAxisSpacingListener();
		resetAxisSpacingListener.setHandler(this);
		listeners.register(ResetAxisSpacingEvent.class, resetAxisSpacingListener);

		ResetViewListener resetViewListener = new ResetViewListener();
		resetViewListener.setHandler(this);
		listeners.register(ResetAllViewsEvent.class, resetViewListener);
		// second event for same listener
		listeners.register(ResetParallelCoordinatesEvent.class, resetViewListener);

		UseRandomSamplingListener useRandomSamplingListener = new UseRandomSamplingListener();
		useRandomSamplingListener.setHandler(this);
		listeners.register(UseRandomSamplingEvent.class, useRandomSamplingListener);

		AngularBrushingListener angularBrushingListener = new AngularBrushingListener();
		angularBrushingListener.setHandler(this);
		listeners.register(AngularBrushingEvent.class, angularBrushingListener);

		AddTablePerspectivesListener addTablePerspectivesListener = new AddTablePerspectivesListener();
		addTablePerspectivesListener.setHandler(this);
		listeners.register(AddTablePerspectivesEvent.class, addTablePerspectivesListener);

	}

	@Override
	public void unregisterEventListeners() {
		listeners.unregisterAll();
	}

	@Override
	public void handleSelectionCommand(IDCategory category, SelectionCommand selectionCommand) {
		if (category == recordSelectionManager.getIDType().getIDCategory())
			recordSelectionManager.executeSelectionCommand(selectionCommand);
		else if (category == dimensionSelectionManager.getIDType().getIDCategory())
			dimensionSelectionManager.executeSelectionCommand(selectionCommand);
		else
			return;

		setDisplayListDirty();
	}

	@Override
	public String toString() {
		if (recordSelectionManager != null && dimensionSelectionManager != null) {
			int iNumElements = (recordSelectionManager.getNumberOfElements() - recordSelectionManager
					.getNumberOfElements(SelectionType.DESELECTED));
			String renderMode = "standalone";
			if (isRenderedRemote())
				renderMode = "remote";
			return ("PCs, " + renderMode + ", " + iNumElements + " elements" + " Axis DT: "
					+ dimensionSelectionManager.getIDType() + " Polyline DT:" + recordSelectionManager.getIDType());
		} else
			return super.toString();
	}

	@Override
	public List<AGLView> getRemoteRenderedViews() {
		return new ArrayList<AGLView>();
	}

	@Override
	public int getMinPixelHeight() {
		// TODO: Calculate depending on content
		return 100;
	}

	@Override
	public int getMinPixelHeight(EDetailLevel detailLevel) {
		switch (detailLevel) {
		case HIGH:
			return 400;
		case MEDIUM:
			return 200;
		case LOW:
			return 50;
		default:
			return 50;
		}
	}

	@Override
	public int getMinPixelWidth(EDetailLevel detailLevel) {
		return getPixelPerElement(false, detailLevel, 5, 10);
	}

	@Override
	public java.util.Set<IDataDomain> getDataDomains() {
		java.util.Set<IDataDomain> dataDomains = new HashSet<IDataDomain>();
		dataDomains.add(dataDomain);
		return dataDomains;
	}

	@Override
	public void setDetailLevel(EDetailLevel detailLevel) {
		super.setDetailLevel(detailLevel);
		switch (detailLevel) {
		case LOW:
			numberOfRandomElements = 50;
			break;
		case MEDIUM:
			numberOfRandomElements = 100;
			break;
		case HIGH:
			numberOfRandomElements = generalManager.getPreferenceStore().getInt(
					PreferenceConstants.PC_NUM_RANDOM_SAMPLING_POINT);
			break;

		default:
			break;
		}
	}

	@Override
	public void setTablePerspective(TablePerspective tablePerspective) {
		this.tablePerspective = tablePerspective;

		initData();
		updateSpacings();

		setDisplayListDirty();
		DataDomainUpdateEvent event = new DataDomainUpdateEvent(tablePerspective.getDataDomain());
		eventPublisher.triggerEvent(event);

		TablePerspectivesChangedEvent tbEvent = new TablePerspectivesChangedEvent(this);
		eventPublisher.triggerEvent(tbEvent);

	}

}
