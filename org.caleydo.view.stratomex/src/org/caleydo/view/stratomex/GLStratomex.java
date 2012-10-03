/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 * 
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.stratomex;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.awt.GLCanvas;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.data.perspective.variable.RecordPerspective;
import org.caleydo.core.data.selection.RecordSelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.SelectionTypeEvent;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.selection.events.ClearSelectionsListener;
import org.caleydo.core.data.selection.events.ISelectionUpdateHandler;
import org.caleydo.core.data.virtualarray.EVAOperation;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.events.RecordVAUpdateEvent;
import org.caleydo.core.data.virtualarray.similarity.RelationAnalyzer;
import org.caleydo.core.event.data.ClearSelectionsEvent;
import org.caleydo.core.event.data.RelationsUpdatedEvent;
import org.caleydo.core.event.data.ReplaceTablePerspectiveEvent;
import org.caleydo.core.event.data.SelectionUpdateEvent;
import org.caleydo.core.event.view.TablePerspectivesChangedEvent;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.view.IMultiTablePerspectiveBasedView;
import org.caleydo.core.view.listener.AddTablePerspectivesEvent;
import org.caleydo.core.view.listener.RemoveTablePerspectiveEvent;
import org.caleydo.core.view.listener.RemoveTablePerspectiveListener;
import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutManager;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.ViewLayoutRenderer;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.util.GLCoordinateUtils;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.spline.ConnectionBandRenderer;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.datadomain.pathway.data.PathwayTablePerspective;
import org.caleydo.view.stratomex.brick.GLBrick;
import org.caleydo.view.stratomex.brick.configurer.CategoricalDataConfigurer;
import org.caleydo.view.stratomex.brick.configurer.IBrickConfigurer;
import org.caleydo.view.stratomex.brick.configurer.NumericalDataConfigurer;
import org.caleydo.view.stratomex.brick.contextmenu.SplitBrickItem;
import org.caleydo.view.stratomex.column.BrickColumn;
import org.caleydo.view.stratomex.column.BrickColumnManager;
import org.caleydo.view.stratomex.column.BrickColumnSpacingRenderer;
import org.caleydo.view.stratomex.event.AddGroupsToStratomexEvent;
import org.caleydo.view.stratomex.event.ConnectionsModeEvent;
import org.caleydo.view.stratomex.event.SplitBrickEvent;
import org.caleydo.view.stratomex.listener.AddGroupsToStratomexListener;
import org.caleydo.view.stratomex.listener.ConnectionsModeListener;
import org.caleydo.view.stratomex.listener.GLStratomexKeyListener;
import org.caleydo.view.stratomex.listener.ReplaceTablePerspectiveListener;
import org.caleydo.view.stratomex.listener.SplitBrickListener;
import org.caleydo.view.stratomex.vendingmachine.VendingMachine;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Composite;

/**
 * VisBricks main view
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */

public class GLStratomex extends AGLView implements IMultiTablePerspectiveBasedView,
		IGLRemoteRenderingView, IViewCommandHandler, ISelectionUpdateHandler {

	public static String VIEW_TYPE = "org.caleydo.view.stratomex";
	public static String VIEW_NAME = "StratomeX";

	private final static int ARCH_PIXEL_HEIGHT = 100;
	private final static int ARCH_PIXEL_WIDTH = 80;
	private final static float ARCH_BOTTOM_PERCENT = 1f;
	private final static float ARCH_STAND_WIDTH_PERCENT = 0.05f;

	private final static int BRICK_COLUMN_SPACING_MIN_PIXEL_WIDTH = 20;
	public final static int BRICK_COLUMN_SIDE_SPACING = 50;

	public final static float[] ARCH_COLOR = { 0f, 0f, 0f, 0.1f };

	private AddGroupsToStratomexListener addGroupsToStratomexListener;
	private ClearSelectionsListener clearSelectionsListener;
	private ConnectionsModeListener trendHighlightModeListener;
	private SplitBrickListener splitBrickListener;
	private RemoveTablePerspectiveListener removeTablePerspectiveListener;
	private ReplaceTablePerspectiveListener replaceTablePerspectiveListener;

	private BrickColumnManager brickColumnManager;

	private ConnectionBandRenderer connectionRenderer;

	private LayoutManager layoutManager;

	private Row mainRow;
	private Row centerRowLayout;
	private Column leftColumnLayout;
	private Column rightColumnLayout;

	/** thickness of the arch at the sides */
	private float archSideWidth = 0;
	private float archInnerWidth = 0;
	private float archTopY = 0;
	private float archBottomY = 0;
	private float archHeight = 0;

	/** Flag signaling if a group needs to be moved out of the center */
	boolean resizeNecessary = false;
	boolean lastResizeDirectionWasToLeft = true;

	boolean isLayoutDirty = false;

	/** Flag telling whether a detail is shown left of its dimension group */
	private boolean isLeftDetailShown = false;
	/** Same as {@link #isLeftDetailShown} for right */
	private boolean isRightDetailShown = false;

	private Queue<AGLView> uninitializedSubViews = new LinkedList<AGLView>();

	private DragAndDropController dragAndDropController;

	private RelationAnalyzer relationAnalyzer;

	private ElementLayout leftBrickColumnSpacing;
	private ElementLayout rightBrickColumnSpacing;

	/**
	 * The id category used to map between the records of the dimension groups.
	 * Only data with the same recordIDCategory can be connected
	 */
	private IDCategory recordIDCategory;

	/**
	 * The selection manager for the records, used for highlighting the visual
	 * links
	 */
	private RecordSelectionManager recordSelectionManager;

	private boolean connectionsOn = true;
	private boolean connectionsHighlightDynamic = false;

	private int selectedConnectionBandID = -1;

	/**
	 * Determines the connection focus highlight dynamically in a range between
	 * 0 and 1
	 */
	private float connectionsFocusFactor;

	private boolean isHorizontalMoveDraggingActive = false;
	private int movedBrickColumn = -1;

	/**
	 * The position of the mouse in the previous render cycle when dragging
	 * columns
	 */
	private float previousXCoordinate = Float.NaN;
	/**
	 * If the mouse is dragged further out to the left than possible, this is
	 * set to where it was
	 */
	private float leftLimitXCoordinate = Float.NaN;
	/** Same as {@link #leftLimitXCoordinate} for the right side */
	private float rightLimitXCoordinate = Float.NaN;

	/** Needed for selecting the elements when a connection band is picked **/
	private HashMap<Integer, BrickConnection> hashConnectionBandIDToRecordVA = new HashMap<Integer, BrickConnection>();

	private SelectionType volatileBandSelectionType;

	private int connectionBandIDCounter = 0;

	private boolean isConnectionLinesDirty = true;

	private List<TablePerspective> tablePerspectives;

	private VendingMachine vendingMachine;

	private ElementLayout vendingMachineLayout;

	/**
	 * Constructor.
	 * 
	 */
	public GLStratomex(GLCanvas glCanvas, Composite parentComposite,
			ViewFrustum viewFrustum) {

		super(glCanvas, parentComposite, viewFrustum, VIEW_TYPE, VIEW_NAME);

		connectionRenderer = new ConnectionBandRenderer();

		dragAndDropController = new DragAndDropController(this);

		brickColumnManager = new BrickColumnManager();

		glKeyListener = new GLStratomexKeyListener();

		relationAnalyzer = new RelationAnalyzer();

		tablePerspectives = new ArrayList<TablePerspective>();

		parentGLCanvas.removeMouseWheelListener(glMouseListener);
		parentGLCanvas.addMouseWheelListener(glMouseWheelListener);

		registerPickingListeners();
	}

	@Override
	public void init(GL2 gl) {
		displayListIndex = gl.glGenLists(1);

		textRenderer = new CaleydoTextRenderer(24);

		connectionRenderer.init(gl);

		layoutManager = new LayoutManager(viewFrustum, pixelGLConverter);
		mainRow = new Row();
		layoutManager.setBaseElementLayout(mainRow);

		initVendingMachine();
	}

	public void initLayouts() {

		brickColumnManager.getBrickColumnSpacers().clear();

		mainRow.clear();

		if (!isLeftDetailShown && !isRightDetailShown && !vendingMachine.isActive()) {
			initLeftLayout();
		}
		initCenterLayout();
		if (!isLeftDetailShown && !isRightDetailShown && !vendingMachine.isActive()) {
			initRightLayout();
		}

		if (vendingMachine.isActive())
			mainRow.append(vendingMachineLayout);

		layoutManager.updateLayout();

		updateConnectionLinesBetweenDimensionGroups();
	}

	private void initLeftLayout() {
		leftColumnLayout = new Column("leftArchColumn");
		leftColumnLayout.setPixelSizeX(ARCH_PIXEL_WIDTH);

		initSideLayout(leftColumnLayout, 0,
				brickColumnManager.getCenterColumnStartIndex());
	}

	private void initRightLayout() {
		rightColumnLayout = new Column("rightArchColumn");
		rightColumnLayout.setPixelSizeX(ARCH_PIXEL_WIDTH);

		initSideLayout(rightColumnLayout, brickColumnManager.getRightColumnStartIndex(),
				brickColumnManager.getBrickColumns().size());
	}

	public int getSideArchWidthPixels() {
		return pixelGLConverter.getPixelWidthForGLWidth(viewFrustum.getWidth()
				* ARCH_STAND_WIDTH_PERCENT);
	}

	/**
	 * Init the layout for the center region, showing the horizontal bar of the
	 * arch plus all sub-bricks above and below
	 */
	private void initCenterLayout() {

		archSideWidth = viewFrustum.getWidth() * ARCH_STAND_WIDTH_PERCENT;

		if (isRightDetailShown || isLeftDetailShown) {
			archInnerWidth = 0;
		} else {
			archInnerWidth = viewFrustum.getWidth() * (ARCH_STAND_WIDTH_PERCENT + 0.024f);
		}

		archHeight = pixelGLConverter.getGLHeightForPixelHeight(ARCH_PIXEL_HEIGHT);
		archBottomY = viewFrustum.getHeight() * ARCH_BOTTOM_PERCENT - archHeight;

		archTopY = archBottomY + archHeight;

		int dimensionGroupCountInCenter = brickColumnManager.getRightColumnStartIndex()
				- brickColumnManager.getCenterColumnStartIndex();

		centerRowLayout = new Row("centerRowLayout");

		centerRowLayout.setPriorityRendereing(true);
		centerRowLayout.setFrameColor(0, 0, 1, 1);

		leftBrickColumnSpacing = new ElementLayout("firstCenterDimGrSpacing");

		BrickColumnSpacingRenderer dimensionGroupSpacingRenderer = null;

		// Handle special case where center contains no groups
		if (dimensionGroupCountInCenter < 1) {
			dimensionGroupSpacingRenderer = new BrickColumnSpacingRenderer(null,
					connectionRenderer, null, null, this);
		} else {
			dimensionGroupSpacingRenderer = new BrickColumnSpacingRenderer(null,
					connectionRenderer, null, brickColumnManager.getBrickColumns().get(
							brickColumnManager.getCenterColumnStartIndex()), this);
		}

		leftBrickColumnSpacing.setRenderer(dimensionGroupSpacingRenderer);
		// dimensionGroupSpacingRenderer.setLineLength(archHeight);

		if (dimensionGroupCountInCenter > 1)
			leftBrickColumnSpacing.setPixelSizeX(BRICK_COLUMN_SIDE_SPACING);
		else
			leftBrickColumnSpacing.setGrabX(true);

		centerRowLayout.append(leftBrickColumnSpacing);

		for (int dimensionGroupIndex = brickColumnManager.getCenterColumnStartIndex(); dimensionGroupIndex < brickColumnManager
				.getRightColumnStartIndex(); dimensionGroupIndex++) {

			ElementLayout dynamicDimensionGroupSpacing;

			BrickColumn group = brickColumnManager.getBrickColumns().get(
					dimensionGroupIndex);
			group.setCollapsed(false);
			group.setArchHeight(ARCH_PIXEL_HEIGHT);
			centerRowLayout.append(group.getLayout());

			if (dimensionGroupIndex != brickColumnManager.getRightColumnStartIndex() - 1) {
				dynamicDimensionGroupSpacing = new ElementLayout("dynamicDimGrSpacing");
				dimensionGroupSpacingRenderer = new BrickColumnSpacingRenderer(
						relationAnalyzer, connectionRenderer, group, brickColumnManager
								.getBrickColumns().get(dimensionGroupIndex + 1), this);
				dynamicDimensionGroupSpacing.setGrabX(true);
				dynamicDimensionGroupSpacing.setRenderer(dimensionGroupSpacingRenderer);
				centerRowLayout.append(dynamicDimensionGroupSpacing);

			} else {
				rightBrickColumnSpacing = new ElementLayout("lastDimGrSpacing");
				dimensionGroupSpacingRenderer = new BrickColumnSpacingRenderer(null,
						connectionRenderer, group, null, this);

				if (dimensionGroupCountInCenter > 1)
					rightBrickColumnSpacing.setPixelSizeX(BRICK_COLUMN_SIDE_SPACING);
				else
					rightBrickColumnSpacing.setGrabX(true);

				rightBrickColumnSpacing.setRenderer(dimensionGroupSpacingRenderer);
				centerRowLayout.append(rightBrickColumnSpacing);
			}

			// dimensionGroupSpacingRenderer.setLineLength(archHeight);
		}

		mainRow.append(centerRowLayout);
	}

	/**
	 * Initialize the layout for the sides of the arch
	 * 
	 * @param columnLayout
	 * @param layoutTemplate
	 * @param layoutManager
	 * @param dimensinoGroupStartIndex
	 * @param dimensionGroupEndIndex
	 */
	private void initSideLayout(Column columnLayout, int dimensinoGroupStartIndex,
			int dimensionGroupEndIndex) {

		columnLayout.setFrameColor(1, 1, 0, 1);
		columnLayout.setBottomUp(true);

		ElementLayout dimensionGroupSpacing = new ElementLayout("firstSideDimGrSpacing");
		dimensionGroupSpacing.setGrabY(true);

		columnLayout.append(dimensionGroupSpacing);

		BrickColumnSpacingRenderer dimensionGroupSpacingRenderer = null;

		// Handle special case where arch stand contains no groups
		if (dimensinoGroupStartIndex == 0
				|| dimensinoGroupStartIndex == dimensionGroupEndIndex) {
			dimensionGroupSpacingRenderer = new BrickColumnSpacingRenderer(null,
					connectionRenderer, null, null, this);
		} else {
			dimensionGroupSpacingRenderer = new BrickColumnSpacingRenderer(null,
					connectionRenderer, null, brickColumnManager.getBrickColumns().get(
							brickColumnManager.getCenterColumnStartIndex()), this);
		}

		dimensionGroupSpacing.setRenderer(dimensionGroupSpacingRenderer);

		dimensionGroupSpacingRenderer.setVertical(false);
		// dimensionGroupSpacingRenderer.setLineLength(archSideThickness);

		for (int dimensionGroupIndex = dimensinoGroupStartIndex; dimensionGroupIndex < dimensionGroupEndIndex; dimensionGroupIndex++) {

			BrickColumn group = brickColumnManager.getBrickColumns().get(
					dimensionGroupIndex);

			group.getLayout().setAbsoluteSizeY(archSideWidth);
			group.setArchHeight(-1);
			columnLayout.append(group.getLayout());

			group.setCollapsed(true);

			dimensionGroupSpacing = new ElementLayout("sideDimGrSpacing");
			dimensionGroupSpacing.setGrabY(true);

			dimensionGroupSpacingRenderer = new BrickColumnSpacingRenderer(null, null,
					group, null, this);
			columnLayout.append(dimensionGroupSpacing);

			dimensionGroupSpacing.setRenderer(dimensionGroupSpacingRenderer);

			dimensionGroupSpacingRenderer.setVertical(false);
			// dimensionGroupSpacingRenderer.setLineLength(archSideThickness);

		}
		mainRow.append(columnLayout);

	}

	@Override
	public void initLocal(GL2 gl) {

		// Register keyboard listener to GL2 canvas
		parentComposite.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				parentComposite.addKeyListener(glKeyListener);
			}
		});

		init(gl);
	}

	@Override
	public void initRemote(final GL2 gl, final AGLView glParentView,
			final GLMouseListener glMouseListener) {

		this.glMouseListener = glMouseListener;
		init(gl);
		initLayouts();
	}

	@Override
	public void displayLocal(GL2 gl) {

		pickingManager.handlePicking(this, gl);

		display(gl);

		if (!lazyMode)
			checkForHits(gl);
	}

	@Override
	public void displayRemote(GL2 gl) {
		display(gl);
	}

	@Override
	public void display(GL2 gl) {

		if (!uninitializedSubViews.isEmpty()) {
			while (uninitializedSubViews.peek() != null) {
				uninitializedSubViews.poll().initRemote(gl, this, glMouseListener);
			}
			initLayouts();
		}
		if (isDisplayListDirty) {
			buildDisplayList(gl, displayListIndex);
			isDisplayListDirty = false;
		}

		for (BrickColumn group : brickColumnManager.getBrickColumns()) {
			group.processEvents();
		}

		handleHorizontalColumnMove(gl);
		if (isLayoutDirty) {
			isLayoutDirty = false;

			layoutManager.updateLayout();
			float minWidth = pixelGLConverter
					.getGLWidthForPixelWidth(BRICK_COLUMN_SPACING_MIN_PIXEL_WIDTH);
			for (ElementLayout layout : centerRowLayout) {
				if (!(layout.getRenderer() instanceof BrickColumnSpacingRenderer))
					continue;
				if (resizeNecessary)
					break;

				if (layout.getSizeScaledX() < minWidth - 0.01f) {
					resizeNecessary = true;
					break;
				}
			}
		}

		if (resizeNecessary) {
			// int size = centerRowLayout.size();
			// if (size >= 3) {
			// if (lastResizeDirectionWasToLeft) {
			// dimensionGroupManager.setCenterGroupStartIndex(dimensionGroupManager
			// .getCenterGroupStartIndex() + 1);
			//
			// float width =
			// centerRowLayout.getElements().get(0).getSizeScaledX()
			// + centerRowLayout.getElements().get(1).getSizeScaledX()
			// + centerRowLayout.getElements().get(2).getSizeScaledX();
			// centerRowLayout.remove(0);
			// centerRowLayout.remove(0);
			// leftDimensionGroupSpacing = centerRowLayout.getElements().get(0);
			//
			// leftDimensionGroupSpacing.setAbsoluteSizeX(width);
			// ((DimensionGroupSpacingRenderer) leftDimensionGroupSpacing
			// .getRenderer()).setLeftDimGroup(null);
			// initLeftLayout();
			//
			// // if (size == 3)
			// // leftDimensionGroupSpacing.setGrabX(true);
			//
			// } else {
			// dimensionGroupManager.setRightGroupStartIndex(dimensionGroupManager
			// .getRightGroupStartIndex() - 1);
			//
			// // float width = centerRowLayout.getElements().get(size - 1)
			// // .getSizeScaledX()
			// // + centerRowLayout.getElements().get(size - 2)
			// // .getSizeScaledX()
			// // + centerRowLayout.getElements().get(size - 3)
			// // .getSizeScaledX();
			// centerRowLayout.remove(centerRowLayout.size() - 1);
			// centerRowLayout.remove(centerRowLayout.size() - 1);
			// rightDimensionGroupSpacing = centerRowLayout.getElements().get(
			// centerRowLayout.size() - 1);
			// // rightDimensionGroupSpacing.setAbsoluteSizeX(width);
			// rightDimensionGroupSpacing.setGrabX(true);
			// ((DimensionGroupSpacingRenderer) rightDimensionGroupSpacing
			// .getRenderer()).setRightDimGroup(null);
			// initRightLayout();
			//
			// // if (size == 3)
			// // rightDimensionGroupSpacing.setGrabX(true);
			//
			// }
			// }
			// centerLayoutManager.updateLayout();
			resizeNecessary = false;
		}

		for (BrickColumn dimensionGroup : brickColumnManager.getBrickColumns()) {
			dimensionGroup.display(gl);
		}

		if (isConnectionLinesDirty)
			performConnectionLinesUpdate();

		layoutManager.render(gl);

		if (!isRightDetailShown && !isLeftDetailShown) {
			renderArch(gl);
		}

		// call after all other rendering because it calls the onDrag methods
		// which need alpha blending...
		dragAndDropController.handleDragging(gl, glMouseListener);
	}

	/**
	 * Switches to detail mode where the detail brick is on the right side of
	 * the specified column
	 * 
	 * @param focusColumn
	 *            the column that contains the focus brick
	 */
	public void switchToDetailModeRight(BrickColumn focusColumn) {

		int dimensionGroupIndex = brickColumnManager.indexOfBrickColumn(focusColumn);
		// false only if this is the rightmost column. If true we
		// move anything beyond the next column out
		if (dimensionGroupIndex != brickColumnManager.getRightColumnStartIndex() - 1) {
			brickColumnManager.setRightColumnStartIndex(dimensionGroupIndex + 2);

		}
		// false only if this is the leftmost colum. If true we
		// move anything further left out
		if (dimensionGroupIndex != brickColumnManager.getCenterColumnStartIndex()) {
			brickColumnManager.setCenterColumnStartIndex(dimensionGroupIndex);
		}

		isRightDetailShown = true;
		mainRow.remove(rightColumnLayout);
		mainRow.remove(leftColumnLayout);
		initLayouts();
		setDisplayListDirty();
	}

	/**
	 * Switches to detail mode where the detail brick is on the left side of the
	 * specified column.
	 * 
	 * @param focusColumn
	 *            the column that contains the detail brick
	 */
	public void switchToDetailModeLeft(BrickColumn focusColumn) {

		int columnIndex = brickColumnManager.indexOfBrickColumn(focusColumn);

		// false only if this is the left-most column. If true we move
		// out everything right of this column
		if (columnIndex != brickColumnManager.getCenterColumnStartIndex()) {
			brickColumnManager.setCenterColumnStartIndex(columnIndex - 1);
		}
		// false only if this is the right-most column
		if (columnIndex != brickColumnManager.getRightColumnStartIndex() - 1) {
			brickColumnManager.setRightColumnStartIndex(columnIndex + 1);
		}

		isLeftDetailShown = true;
		mainRow.remove(rightColumnLayout);
		mainRow.remove(leftColumnLayout);
		initLayouts();
		// layoutManager.updateLayout();
	}

	/**
	 * Hide the detail brick which is shown right of its parent dimension group
	 */
	public void switchToOverviewModeRight() {
		isRightDetailShown = false;
		initLayouts();
	}

	/**
	 * Hide the detail brick which is shown left of its parent dimension group
	 */
	public void switchToOverviewModeLeft() {
		isLeftDetailShown = false;
		initLayouts();
	}

	private void buildDisplayList(final GL2 gl, int iGLDisplayListIndex) {
		gl.glNewList(iGLDisplayListIndex, GL2.GL_COMPILE);

		renderArch(gl);

		gl.glEndList();
	}

	private void renderArch(GL2 gl) {

		gl.glLineWidth(1);
		gl.glColor4f(ARCH_COLOR[0], ARCH_COLOR[1], ARCH_COLOR[2], ARCH_COLOR[3] * 2);

		// Left arch
		gl.glBegin(GL2.GL_LINES);
		gl.glVertex3f(archSideWidth, archBottomY, 0f);
		gl.glVertex3f(archSideWidth, 0, 0f);
		gl.glEnd();

		// Right arch
		gl.glBegin(GL2.GL_LINES);
		gl.glVertex3f(viewFrustum.getWidth() - archSideWidth, archBottomY, 0.01f);
		gl.glVertex3f(viewFrustum.getWidth() - archSideWidth, 0, 0.1f);
		gl.glEnd();
	}

	/**
	 * Handles the left-right dragging of the whole dimension group. Does
	 * collision handling and moves dimension groups to the sides if necessary.
	 * 
	 * @param gl
	 */
	private void handleHorizontalColumnMove(GL2 gl) {
		if (!isHorizontalMoveDraggingActive)
			return;
		if (glMouseListener.wasMouseReleased()) {
			isHorizontalMoveDraggingActive = false;
			previousXCoordinate = Float.NaN;
			leftLimitXCoordinate = Float.NaN;
			rightLimitXCoordinate = Float.NaN;
			return;
		}

		Point currentPoint = glMouseListener.getPickedPoint();

		float[] pointCordinates = GLCoordinateUtils
				.convertWindowCoordinatesToWorldCoordinates(gl, currentPoint.x,
						currentPoint.y);

		if (Float.isNaN(previousXCoordinate)) {
			previousXCoordinate = pointCordinates[0];
			return;
		}

		float change = pointCordinates[0] - previousXCoordinate;

		// float change = -0.1f;
		// isHorizontalMoveDraggingActive = false;
		if (change > 0) {
			if (change < 0.01f)
				return;
			lastResizeDirectionWasToLeft = false;
		} else {
			// ignore tiny changes
			if (change > -0.01f)
				return;
			lastResizeDirectionWasToLeft = true;
		}

		if (!Float.isNaN(leftLimitXCoordinate)) {
			if (leftLimitXCoordinate >= currentPoint.x)
				return;
			else
				leftLimitXCoordinate = Float.NaN;
		}

		if (!Float.isNaN(rightLimitXCoordinate)) {
			if (rightLimitXCoordinate <= currentPoint.x)
				return;
			else
				rightLimitXCoordinate = Float.NaN;
		}
		previousXCoordinate = pointCordinates[0];

		// the spacing left of the moved element
		ElementLayout leftSpacing = null;
		// the spacing right of the moved element
		ElementLayout rightSpacing = null;
		int leftIndex = 0;
		int rightIndex = 0;

		BrickColumnSpacingRenderer spacingRenderer;
		int count = 0;
		for (ElementLayout layout : centerRowLayout) {
			if (layout.getRenderer() instanceof BrickColumnSpacingRenderer) {
				spacingRenderer = (BrickColumnSpacingRenderer) layout.getRenderer();
				if (spacingRenderer.getRightDimGroup() != null) {
					if (spacingRenderer.getRightDimGroup().getID() == movedBrickColumn) {
						leftSpacing = layout;
						leftIndex = count;

					}
				}
				if (spacingRenderer.getLeftDimGroup() != null) {
					if (spacingRenderer.getLeftDimGroup().getID() == movedBrickColumn) {
						rightSpacing = layout;
						rightIndex = count;

					}
				}
				if (count < centerRowLayout.size() - 1) {
					layout.setGrabX(false);
					layout.setAbsoluteSizeX(layout.getSizeScaledX());
				} else
					layout.setGrabX(true);
			}
			count++;
		}

		if (leftSpacing == null || rightSpacing == null)
			return;

		float leftSizeX = leftSpacing.getSizeScaledX();
		float rightSizeX = rightSpacing.getSizeScaledX();
		float minWidth = pixelGLConverter
				.getGLWidthForPixelWidth(BRICK_COLUMN_SPACING_MIN_PIXEL_WIDTH);

		if (change > 0) {
			// moved to the right, change is positive
			if (rightSizeX - change > minWidth) {
				// there is space, we adapt the spacings left and right
				rightSpacing.setAbsoluteSizeX(rightSizeX - change);
				leftSpacing.setAbsoluteSizeX(leftSizeX + change);

			} else {
				// the immediate neighbor doesn't have space, check for the
				// following
				rightSpacing.setAbsoluteSizeX(minWidth);

				float savedSize = rightSizeX - minWidth;
				float remainingChange = change - savedSize;

				while (remainingChange > 0) {
					if (centerRowLayout.size() < rightIndex + 2) {
						rightLimitXCoordinate = currentPoint.x;
						break;
					}
					rightIndex += 2;
					ElementLayout spacing = centerRowLayout.getElements().get(rightIndex);
					if (spacing.getSizeScaledX() - remainingChange > minWidth + 0.001f) {
						spacing.setAbsoluteSizeX(spacing.getSizeScaledX()
								- remainingChange);
						remainingChange = 0;
						break;
					} else {
						savedSize = spacing.getSizeScaledX() - minWidth;
						remainingChange -= savedSize;
						if (rightIndex == centerRowLayout.size() - 1) {
							rightLimitXCoordinate = currentPoint.x;
						}
						spacing.setAbsoluteSizeX(minWidth);
					}
				}
				leftSpacing.setAbsoluteSizeX(leftSizeX + change - remainingChange);
			}

		} else {
			// moved to the left, change is negative
			if (leftSizeX + change > minWidth) {
				// there is space, we adapt the spacings left and right
				leftSpacing.setAbsoluteSizeX(leftSizeX + change);
				rightSpacing.setAbsoluteSizeX(rightSizeX - change);
			} else {
				// the immediate neighbor doesn't have space, check for the
				// following
				leftSpacing.setAbsoluteSizeX(minWidth);
				float savedSize = leftSizeX - minWidth;
				float remainingChange = change + savedSize;

				while (remainingChange < 0) {
					if (leftIndex < 2) {
						// if (leftIndex == 0) {
						leftLimitXCoordinate = currentPoint.x;
						// }
						break;
					}
					leftIndex -= 2;
					ElementLayout spacing = centerRowLayout.getElements().get(leftIndex);
					if (spacing.getSizeScaledX() + remainingChange > minWidth + 0.001f) {
						// the whole change fits in the first spacing left of
						// the source
						spacing.setAbsoluteSizeX(spacing.getSizeScaledX()
								+ remainingChange);
						remainingChange = 0;
						break;
					} else {
						savedSize = spacing.getSizeScaledX() - minWidth;
						remainingChange += savedSize;
						if (leftIndex == 0) {
							leftLimitXCoordinate = currentPoint.x;
						}

						spacing.setAbsoluteSizeX(minWidth);
					}
				}
				rightSpacing.setAbsoluteSizeX(rightSizeX - change + remainingChange);
			}

		}

		setLayoutDirty();
	}

	protected void registerPickingListeners() {

		addTypePickingListener(new APickingListener() {
			@Override
			public void clicked(Pick pick) {
				selectedConnectionBandID = pick.getObjectID();
				selectElementsByConnectionBandID(selectedConnectionBandID);
			}

			@Override
			public void rightClicked(Pick pick) {

				contextMenuCreator.addContextMenuItem(new SplitBrickItem(pick
						.getObjectID(), true));
				contextMenuCreator.addContextMenuItem(new SplitBrickItem(pick
						.getObjectID(), false));
			}

		}, EPickingType.BRICK_CONNECTION_BAND.name());

		addTypePickingListener(new APickingListener() {
			@Override
			public void clicked(Pick pick) {
				dragAndDropController.clearDraggables();
				dragAndDropController.setDraggingStartPosition(pick.getPickedPoint());
				dragAndDropController.addDraggable((BrickColumn) generalManager
						.getViewManager().getGLView(pick.getObjectID()));
				dragAndDropController.setDraggingMode("DimensionGroupDrag");

			}

			// @Override
			// public void dragged(Pick pick) {
			// if (dragAndDropController.hasDraggables()) {
			// String draggingMode = dragAndDropController.getDraggingMode();
			//
			// if (glMouseListener.wasRightMouseButtonPressed())
			// dragAndDropController.clearDraggables();
			// else if (!dragAndDropController.isDragging() && draggingMode !=
			// null
			// && draggingMode.equals("DimensionGroupDrag"))
			// dragAndDropController.startDragging();
			// }
			//
			// }

		}, EPickingType.DIMENSION_GROUP.name());

		addTypePickingListener(new APickingListener() {
			@Override
			public void dragged(Pick pick) {

				if (dragAndDropController.isDragging()
						&& dragAndDropController.getDraggingMode() != null
						&& dragAndDropController.getDraggingMode().equals(
								"DimensionGroupDrag")) {
					dragAndDropController.setDropArea(brickColumnManager
							.getBrickColumnSpacers().get(pick.getObjectID()));
				} else {
					if (dragAndDropController.isDragging()) {

					}
				}
			};

		}, EPickingType.DIMENSION_GROUP_SPACER.name());

		addTypePickingListener(new APickingListener() {
			@Override
			public void clicked(Pick pick) {
				isHorizontalMoveDraggingActive = true;
				movedBrickColumn = pick.getObjectID();
			};

		}, EPickingType.MOVE_HORIZONTALLY_HANDLE.name());
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedStratomexView serializedForm = new SerializedStratomexView(this);
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

	@Override
	public String toString() {
		return "StratomeX";
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();

		addGroupsToStratomexListener = new AddGroupsToStratomexListener();
		addGroupsToStratomexListener.setHandler(this);
		eventPublisher.addListener(AddGroupsToStratomexEvent.class,
				addGroupsToStratomexListener);
		eventPublisher.addListener(AddTablePerspectivesEvent.class,
				addGroupsToStratomexListener);

		clearSelectionsListener = new ClearSelectionsListener();
		clearSelectionsListener.setHandler(this);
		eventPublisher.addListener(ClearSelectionsEvent.class, clearSelectionsListener);

		trendHighlightModeListener = new ConnectionsModeListener();
		trendHighlightModeListener.setHandler(this);
		eventPublisher
				.addListener(ConnectionsModeEvent.class, trendHighlightModeListener);

		splitBrickListener = new SplitBrickListener();
		splitBrickListener.setHandler(this);
		eventPublisher.addListener(SplitBrickEvent.class, splitBrickListener);

		removeTablePerspectiveListener = new RemoveTablePerspectiveListener();
		removeTablePerspectiveListener.setHandler(this);
		eventPublisher.addListener(RemoveTablePerspectiveEvent.class,
				removeTablePerspectiveListener);

		replaceTablePerspectiveListener = new ReplaceTablePerspectiveListener();
		replaceTablePerspectiveListener.setHandler(this);
		eventPublisher.addListener(ReplaceTablePerspectiveEvent.class,
				replaceTablePerspectiveListener);

	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();

		if (addGroupsToStratomexListener != null) {
			eventPublisher.removeListener(addGroupsToStratomexListener);
			addGroupsToStratomexListener = null;
		}

		if (clearSelectionsListener != null) {
			eventPublisher.removeListener(clearSelectionsListener);
			clearSelectionsListener = null;
		}

		if (trendHighlightModeListener != null) {
			eventPublisher.removeListener(trendHighlightModeListener);
			trendHighlightModeListener = null;
		}

		if (splitBrickListener != null) {
			eventPublisher.removeListener(splitBrickListener);
			splitBrickListener = null;
		}

		if (removeTablePerspectiveListener != null) {
			eventPublisher.removeListener(removeTablePerspectiveListener);
			removeTablePerspectiveListener = null;
		}

		if (replaceTablePerspectiveListener != null) {
			eventPublisher.removeListener(replaceTablePerspectiveListener);
			replaceTablePerspectiveListener = null;
		}
	}

	@Override
	public void handleSelectionUpdate(SelectionDelta selectionDelta) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleRedrawView() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleClearSelections() {
		clearAllSelections();

	}

	public void clearAllSelections() {
		recordSelectionManager.clearSelections();
		updateConnectionLinesBetweenDimensionGroups();
	}

	@Override
	public void broadcastElements(EVAOperation type) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<AGLView> getRemoteRenderedViews() {
		return new ArrayList<AGLView>(brickColumnManager.getBrickColumns());
	}

	@Override
	public void addTablePerspective(TablePerspective tablePerspective) {

		List<TablePerspective> tablePerspectiveWrapper = new ArrayList<TablePerspective>();
		tablePerspectiveWrapper.add(tablePerspective);
		addTablePerspectives(tablePerspectiveWrapper, null);
	}

	@Override
	public void addTablePerspectives(List<TablePerspective> newTablePerspectives) {
		addTablePerspectives(newTablePerspectives, null);
	}

	/**
	 * <p>
	 * Creates a column for each TablePerspective supplied
	 * </p>
	 * <p>
	 * As StratomeX can only map between data sets that share a mapping between
	 * records, the imprinting of the IDType and IDCategory for the records is
	 * done here if there is no data set yet.
	 * </p>
	 * 
	 * @param newTablePerspectives
	 * @param brickConfigurer
	 *            The brick configurer can be specified externally (e.g.,
	 *            pathways, kaplan meier). If null, the
	 *            {@link NumericalDataConfigurer} will be used.
	 */
	public void addTablePerspectives(List<TablePerspective> newTablePerspectives,
			IBrickConfigurer brickConfigurer) {

		if (newTablePerspectives == null || newTablePerspectives.size() == 0) {
			Logger.log(new Status(Status.WARNING, this.toString(),
					"newTablePerspectives in addDimensionGroups was null or empty"));
			return;
		}

		// if this is the first data container set, we imprint StratomeX
		if (recordIDCategory == null) {
			ATableBasedDataDomain dataDomain = newTablePerspectives.get(0)
					.getDataDomain();
			imprintVisBricks(dataDomain);
		}

		ArrayList<BrickColumn> brickColumns = brickColumnManager.getBrickColumns();

		for (TablePerspective tablePerspective : newTablePerspectives) {
			if (tablePerspective == null) {
				Logger.log(new Status(Status.ERROR, this.toString(),
						"Data container was null."));
				continue;
			}
			if (!tablePerspective.getDataDomain().getTable().isDataHomogeneous()
					&& brickConfigurer == null) {
				Logger.log(new Status(
						Status.WARNING,
						this.toString(),
						"Tried to add inhomogeneous table perspective without brick configurerer. Currently not supported."));
				continue;
			}
			if (!tablePerspective.getDataDomain().getRecordIDCategory()
					.equals(recordIDCategory)) {
				Logger.log(new Status(
						Status.ERROR,
						this.toString(),
						"Data container "
								+ tablePerspective
								+ "does not match the recordIDCategory of Visbricks - no mapping possible."));
				continue;
			}

			boolean dimensionGroupExists = false;
			for (BrickColumn brickColumn : brickColumns) {
				if (brickColumn.getTablePerspective().getID() == tablePerspective.getID()) {
					dimensionGroupExists = true;
					break;
				}
			}

			if (!dimensionGroupExists) {
				BrickColumn brickColumn = (BrickColumn) GeneralManager
						.get()
						.getViewManager()
						.createGLView(
								BrickColumn.class,
								getParentGLCanvas(),
								parentComposite,
								new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC, 0, 1,
										0, 1, -1, 1));

				/**
				 * If no brick configurer was specified in the
				 * {@link AddGroupsToVisBricksEvent}, then the numerical
				 * configurer is created by default
				 **/
				if (brickConfigurer == null) {
					// FIXME this is a hack to make tablePerspectives that have
					// only one dimension categorical data
					if (tablePerspective.getNrDimensions() == 1) {
						brickConfigurer = new CategoricalDataConfigurer(tablePerspective);
					} else {
						brickConfigurer = new NumericalDataConfigurer(tablePerspective);
					}
				}

				brickColumn.setDetailLevel(this.getDetailLevel());
				brickColumn.setBrickConfigurer(brickConfigurer);
				brickColumn.setDataDomain(tablePerspective.getDataDomain());
				brickColumn.setTablePerspective(tablePerspective);
				brickColumn.setRemoteRenderingGLView(this);
				brickColumn.setStratomex(this);
				brickColumn.initialize();

				brickColumns.add(brickColumn);
				tablePerspectives.add(tablePerspective);

				uninitializedSubViews.add(brickColumn);
				// if (tablePerspective instanceof PathwayTablePerspective) {
				// dataDomains.add(((PathwayTablePerspective) tablePerspective)
				// .getPathwayDataDomain());
				// } else {
				// dataDomains.add(tablePerspective.getDataDomain());
				// }

				brickColumnManager.setRightColumnStartIndex(brickColumnManager
						.getRightColumnStartIndex() + 1);
			}
		}

		TablePerspectivesChangedEvent event = new TablePerspectivesChangedEvent(this);
		event.setSender(this);
		GeneralManager.get().getEventPublisher().triggerEvent(event);
	}

	@Override
	public void removeTablePerspective(int tablePerspectiveID) {

		Iterator<TablePerspective> tablePerspectiveIterator = tablePerspectives
				.iterator();

		while (tablePerspectiveIterator.hasNext()) {
			TablePerspective container = tablePerspectiveIterator.next();
			if (container.getID() == tablePerspectiveID) {
				tablePerspectiveIterator.remove();
			}
		}

		brickColumnManager.removeBrickColumn(tablePerspectiveID);
		initLayouts();
		TablePerspectivesChangedEvent event = new TablePerspectivesChangedEvent(this);
		event.setSender(this);
		GeneralManager.get().getEventPublisher().triggerEvent(event);
	}

	@Override
	public List<TablePerspective> getTablePerspectives() {
		return tablePerspectives;
	}

	/**
	 * Replaces an old tablePerspective with a new one whil keeping the column
	 * at the same place.
	 * 
	 * @param newTablePerspective
	 * @param oldTablePerspective
	 */
	public void replaceTablePerspective(TablePerspective newTablePerspective,
			TablePerspective oldTablePerspective) {

		Iterator<TablePerspective> tablePerspectiveIterator = tablePerspectives
				.iterator();
		while (tablePerspectiveIterator.hasNext()) {
			TablePerspective tempPerspective = tablePerspectiveIterator.next();
			if (tempPerspective.equals(oldTablePerspective)) {
				tablePerspectiveIterator.remove();
			}
		}
		tablePerspectives.add(newTablePerspective);

		for (BrickColumn column : brickColumnManager.getBrickColumns()) {
			if (column.getTablePerspective().equals(oldTablePerspective)) {
				column.replaceTablePerspective(newTablePerspective);
			}
		}
		TablePerspectivesChangedEvent event = new TablePerspectivesChangedEvent(this);
		eventPublisher.triggerEvent(event);

	}

	/**
	 * Imprints VisBricks to a particular record ID Category by setting the
	 * {@link #recordIDCategory}, and initializes the
	 * {@link #recordSelectionManager}.
	 * 
	 * @param dataDomain
	 */
	private void imprintVisBricks(ATableBasedDataDomain dataDomain) {
		recordIDCategory = dataDomain.getRecordIDCategory();
		IDType mappingRecordIDType = recordIDCategory.getPrimaryMappingType();
		recordSelectionManager = new RecordSelectionManager(mappingRecordIDType);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

		super.reshape(drawable, x, y, width, height);

		initLayouts();
		updateLayout();
		setLayoutDirty();
	}

	@Override
	public void setDisplayListDirty() {
		super.setDisplayListDirty();
	}

	public void moveDimensionGroup(BrickColumnSpacingRenderer spacer,
			BrickColumn movedDimGroup, BrickColumn referenceDimGroup) {
		movedDimGroup.getLayout().reset();
		clearDimensionGroupSpacerHighlight();

		if (movedDimGroup == referenceDimGroup)
			return;

		boolean insertComplete = false;

		ArrayList<BrickColumn> dimensionGroups = brickColumnManager.getBrickColumns();
		for (ElementLayout leftLayout : leftColumnLayout.getElements()) {
			if (spacer == leftLayout.getRenderer()) {

				brickColumnManager.setCenterColumnStartIndex(brickColumnManager
						.getCenterColumnStartIndex() + 1);

				dimensionGroups.remove(movedDimGroup);
				if (referenceDimGroup == null) {
					dimensionGroups.add(0, movedDimGroup);
				} else {
					dimensionGroups.add(dimensionGroups.indexOf(referenceDimGroup),
							movedDimGroup);
				}

				insertComplete = true;
				break;
			}
		}

		if (!insertComplete) {
			for (ElementLayout rightLayout : rightColumnLayout.getElements()) {
				if (spacer == rightLayout.getRenderer()) {

					brickColumnManager.setRightColumnStartIndex(brickColumnManager
							.getRightColumnStartIndex() - 1);

					dimensionGroups.remove(movedDimGroup);
					if (referenceDimGroup == null) {
						dimensionGroups.add(dimensionGroups.size(), movedDimGroup);
					} else {
						dimensionGroups.add(
								dimensionGroups.indexOf(referenceDimGroup) + 1,
								movedDimGroup);
					}

					insertComplete = true;
					break;
				}
			}
		}

		if (!insertComplete) {
			for (ElementLayout centerLayout : centerRowLayout.getElements()) {
				if (spacer == centerLayout.getRenderer()) {

					if (dimensionGroups.indexOf(movedDimGroup) < brickColumnManager
							.getCenterColumnStartIndex())
						brickColumnManager.setCenterColumnStartIndex(brickColumnManager
								.getCenterColumnStartIndex() - 1);
					else if (dimensionGroups.indexOf(movedDimGroup) >= brickColumnManager
							.getRightColumnStartIndex())
						brickColumnManager.setRightColumnStartIndex(brickColumnManager
								.getRightColumnStartIndex() + 1);

					dimensionGroups.remove(movedDimGroup);
					if (referenceDimGroup == null) {
						dimensionGroups.add(
								brickColumnManager.getCenterColumnStartIndex(),
								movedDimGroup);
					} else {

						dimensionGroups.add(
								dimensionGroups.indexOf(referenceDimGroup) + 1,
								movedDimGroup);

					}

					insertComplete = true;
					break;
				}
			}
		}

		initLayouts();

		RelationsUpdatedEvent event = new RelationsUpdatedEvent();
		event.setSender(this);
		eventPublisher.triggerEvent(event);
	}

	public void clearDimensionGroupSpacerHighlight() {
		// Clear previous spacer highlights
		for (ElementLayout element : centerRowLayout.getElements()) {
			if (element.getRenderer() instanceof BrickColumnSpacingRenderer)
				((BrickColumnSpacingRenderer) element.getRenderer())
						.setRenderSpacer(false);
		}

		for (ElementLayout element : leftColumnLayout.getElements()) {
			if (element.getRenderer() instanceof BrickColumnSpacingRenderer)
				((BrickColumnSpacingRenderer) element.getRenderer())
						.setRenderSpacer(false);
		}

		for (ElementLayout element : rightColumnLayout.getElements()) {
			if (element.getRenderer() instanceof BrickColumnSpacingRenderer)
				((BrickColumnSpacingRenderer) element.getRenderer())
						.setRenderSpacer(false);
		}
	}

	public BrickColumnManager getBrickColumnManager() {
		return brickColumnManager;
	}

	public void updateConnectionLinesBetweenDimensionGroups() {

		isConnectionLinesDirty = true;
	}

	private void performConnectionLinesUpdate() {
		connectionBandIDCounter = 0;

		if (centerRowLayout != null) {
			for (ElementLayout elementLayout : centerRowLayout.getElements()) {
				if (elementLayout.getRenderer() instanceof BrickColumnSpacingRenderer) {
					((BrickColumnSpacingRenderer) elementLayout.getRenderer()).init();
				}
			}
		}

		isConnectionLinesDirty = false;
	}

	public void setLayoutDirty() {
		isLayoutDirty = true;
	}

	public void updateLayout() {

		for (BrickColumn dimGroup : brickColumnManager.getBrickColumns()) {
			dimGroup.updateLayout();
		}
	}

	/**
	 * Set whether the last resize of any sub-brick was to the left(true) or to
	 * the right. Important for determining, which dimensionGroup to kick next.
	 * 
	 * @param lastResizeDirectionWasToLeft
	 */
	public void setLastResizeDirectionWasToLeft(boolean lastResizeDirectionWasToLeft) {
		this.lastResizeDirectionWasToLeft = lastResizeDirectionWasToLeft;
	}

	public float getArchTopY() {
		return archTopY;
	}

	public float getArchBottomY() {
		return archBottomY;
	}

	public RecordSelectionManager getRecordSelectionManager() {
		return recordSelectionManager;
	}

	public GLStratomexKeyListener getKeyListener() {
		return (GLStratomexKeyListener) glKeyListener;
	}

	public void handleTrendHighlightMode(boolean connectionsOn,
			boolean connectionsHighlightDynamic, float focusFactor) {

		this.connectionsOn = connectionsOn;
		this.connectionsHighlightDynamic = connectionsHighlightDynamic;
		this.connectionsFocusFactor = focusFactor;

		updateConnectionLinesBetweenDimensionGroups();
	}

	public boolean isConnectionsOn() {
		return connectionsOn;
	}

	public boolean isConnectionsHighlightDynamic() {
		return connectionsHighlightDynamic;
	}

	public float getConnectionsFocusFactor() {
		return connectionsFocusFactor;
	}

	public int getSelectedConnectionBandID() {
		return selectedConnectionBandID;
	}

	public HashMap<Integer, BrickConnection> getHashConnectionBandIDToRecordVA() {
		return hashConnectionBandIDToRecordVA;
	}

	private void selectElementsByConnectionBandID(int connectionBandID) {
		recordSelectionManager.clearSelections();

		ClearSelectionsEvent cse = new ClearSelectionsEvent();
		cse.setSender(this);
		eventPublisher.triggerEvent(cse);

		recordSelectionManager.clearSelection(recordSelectionManager.getSelectionType());

		// Create volatile selection type
		volatileBandSelectionType = new SelectionType("Volatile band selection type",
				recordSelectionManager.getSelectionType().getColor(), 1, true, true, 1);

		volatileBandSelectionType.setManaged(false);

		SelectionTypeEvent selectionTypeEvent = new SelectionTypeEvent(
				volatileBandSelectionType);
		GeneralManager.get().getEventPublisher().triggerEvent(selectionTypeEvent);

		BrickConnection connectionBand = hashConnectionBandIDToRecordVA
				.get(connectionBandID);
		RecordVirtualArray recordVA = connectionBand.getSharedRecordVirtualArray();
		for (Integer recordID : recordVA) {
			recordSelectionManager.addToType(recordSelectionManager.getSelectionType(),
					recordVA.getIdType(), recordID);
		}

		SelectionUpdateEvent event = new SelectionUpdateEvent();
		event.setSender(this);
		SelectionDelta delta = recordSelectionManager.getDelta();
		event.setSelectionDelta(delta);
		// FIXME: actually we should not send a data domain in this case -
		// however, if we don't send it, we don't see the selection in the
		// selection info view. to fix this, we need to redesign the selection
		// info view.
		event.setDataDomainID(connectionBand.getLeftBrick().getDataDomain()
				.getDataDomainID());
		eventPublisher.triggerEvent(event);

		updateConnectionLinesBetweenDimensionGroups();
	}

	/**
	 * Splits a brick into two portions: those values that are in the band
	 * identified through the connection band id and the others.
	 */
	public void splitBrick(Integer connectionBandID, boolean isSplitLeftBrick) {
		BrickConnection brickConnection = hashConnectionBandIDToRecordVA
				.get(connectionBandID);
		RecordVirtualArray sharedRecordVA = brickConnection.getSharedRecordVirtualArray();

		RecordPerspective sourcePerspective;
		RecordVirtualArray sourceVA;
		Integer sourceGroupIndex;
		GLBrick sourceBrick;
		if (isSplitLeftBrick) {
			sourceBrick = brickConnection.getLeftBrick();
		} else {
			sourceBrick = brickConnection.getRightBrick();
		}

		sourcePerspective = sourceBrick.getBrickColumn().getTablePerspective()
				.getRecordPerspective();
		sourceVA = sourcePerspective.getVirtualArray();
		sourceGroupIndex = sourceBrick.getTablePerspective().getRecordGroup()
				.getGroupIndex();

		boolean idNeedsConverting = false;
		if (!sourceVA.getIdType().equals(sharedRecordVA.getIdType())) {
			idNeedsConverting = true;
			// sharedRecordVA =
			// sourceBrick.getDataDomain().convertForeignRecordPerspective(foreignPerspective)
		}

		List<Integer> remainingGroupIDs = new ArrayList<Integer>();

		// this is necessary because the originalGroupIDs is backed by the
		// original VA and changes in it also change the VA
		for (Integer id : sourceVA.getIDsOfGroup(sourceGroupIndex)) {
			remainingGroupIDs.add(id);
		}

		IDMappingManager idMappingManager = IDMappingManagerRegistry.get()
				.getIDMappingManager(sourceVA.getIdType().getIDCategory());

		if (idNeedsConverting) {
			RecordVirtualArray mappedSharedRecordVA = new RecordVirtualArray(
					sourceVA.getIdType());
			for (Integer recordID : sharedRecordVA) {
				recordID = idMappingManager.getID(sharedRecordVA.getIdType(),
						sourceVA.getIdType(), recordID);
				if (recordID == null || recordID == -1)
					continue;
				mappedSharedRecordVA.append(recordID);
			}
			sharedRecordVA = mappedSharedRecordVA;
		}

		// remove the ids of the shared record va from the group which is beeing
		// split
		for (Integer recordID : sharedRecordVA) {

			Iterator<Integer> remainingGroupIDIterator = remainingGroupIDs.iterator();
			while (remainingGroupIDIterator.hasNext()) {
				Integer id = remainingGroupIDIterator.next();
				if (id.equals(recordID)) {
					remainingGroupIDIterator.remove();
				}
			}
		}

		sourceVA.getGroupList().updateGroupInfo();

		List<Integer> newIDs = new ArrayList<Integer>(sourceVA.size());
		List<Integer> groupSizes = new ArrayList<Integer>(
				sourceVA.getGroupList().size() + 1);
		List<String> groupNames = new ArrayList<String>(
				sourceVA.getGroupList().size() + 1);
		List<Integer> sampleElements = new ArrayList<Integer>(sourceVA.getGroupList()
				.size() + 1);

		// build up the data for the perspective
		int sizeCounter = 0;
		for (Integer groupIndex = 0; groupIndex < sourceVA.getGroupList().size(); groupIndex++) {
			if (groupIndex == sourceGroupIndex) {
				newIDs.addAll(sharedRecordVA.getIDs());
				groupSizes.add(sharedRecordVA.size());
				sampleElements.add(sizeCounter);
				sizeCounter += sharedRecordVA.size();
				groupNames.add(sourceVA.getGroupList().get(groupIndex).getLabel()
						+ " Split 1");

				newIDs.addAll(remainingGroupIDs);
				groupSizes.add(remainingGroupIDs.size());
				sampleElements.add(sizeCounter);
				sizeCounter += remainingGroupIDs.size();
				groupNames.add(sourceVA.getGroupList().get(groupIndex).getLabel()
						+ " Split 2");
			} else {
				newIDs.addAll(sourceVA.getIDsOfGroup(groupIndex));
				groupSizes.add(sourceVA.getGroupList().get(groupIndex).getSize());
				sampleElements.add(sizeCounter);
				sizeCounter += sourceVA.getGroupList().get(groupIndex).getSize();
				groupNames.add(sourceVA.getGroupList().get(groupIndex).getLabel());
			}

		}

		PerspectiveInitializationData data = new PerspectiveInitializationData();

		data.setData(newIDs, groupSizes, sampleElements, groupNames);
		// FIXME the rest should probably not be done here but in the data
		// domain.
		sourcePerspective.init(data);

		RecordVAUpdateEvent event = new RecordVAUpdateEvent();
		event.setPerspectiveID(sourcePerspective.getPerspectiveID());

		eventPublisher.triggerEvent(event);

	}

	public int getNextConnectionBandID() {
		return connectionBandIDCounter++;
	}

	public RelationAnalyzer getRelationAnalyzer() {
		return relationAnalyzer;
	}

	public float getArchInnerWidth() {
		return archInnerWidth;
	}

	@Override
	public Set<IDataDomain> getDataDomains() {
		Set<IDataDomain> dataDomains = new HashSet<IDataDomain>();
		if (tablePerspectives == null) {
			System.out.println("Problem");
			return dataDomains;
		}
		for (TablePerspective tablePerspective : tablePerspectives) {
			if (tablePerspective instanceof PathwayTablePerspective) {
				dataDomains.add(((PathwayTablePerspective) tablePerspective)
						.getPathwayDataDomain());
			} else {
				dataDomains.add(tablePerspective.getDataDomain());
			}
		}
		return dataDomains;
	}

	@Override
	public boolean isDataView() {
		return true;
	}

	public int getArchHeight() {
		return ARCH_PIXEL_HEIGHT;
	}

	public DragAndDropController getDragAndDropController() {
		return dragAndDropController;
	}

	@Override
	protected void destroyViewSpecificContent(GL2 gl) {
		gl.glDeleteLists(displayListIndex, 1);
		// if (centerLayoutManager != null)
		// centerLayoutManager.destroy(gl);
		// if (leftLayoutManager != null)
		// leftLayoutManager.destroy(gl);
		// if (rightLayoutManager != null)
		// rightLayoutManager.destroy(gl);

		if (layoutManager != null)
			layoutManager.destroy(gl);
	}

	private void initVendingMachine() {

		vendingMachineLayout = new ElementLayout("vendingMachineLayout");
		vendingMachineLayout.setPixelSizeX(VendingMachine.VENDING_MACHINE_PIXEL_WIDTH);

		ViewFrustum frustum = new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC, 0, 1, 0,
				1, -4, 4);
		vendingMachine = (VendingMachine) GeneralManager
				.get()
				.getViewManager()
				.createGLView(VendingMachine.class, parentGLCanvas, parentComposite,
						frustum);

		vendingMachine.setStratomex(this);
		vendingMachine.setRemoteRenderingGLView(this);
		vendingMachine.initialize();

		uninitializedSubViews.add(vendingMachine);
		ViewLayoutRenderer viewLayoutRenderer = new ViewLayoutRenderer(vendingMachine);
		vendingMachineLayout.setRenderer(viewLayoutRenderer);
	}

	/**
	 * @return the vendingMachine, see {@link #vendingMachine}
	 */
	public VendingMachine getVendingMachine() {
		return vendingMachine;
	}

	public Row getLayout() {
		return mainRow;
	}

	@Override
	public boolean isTablePerspectiveValid(TablePerspective tablePerspective) {
		return true;
	}
}
