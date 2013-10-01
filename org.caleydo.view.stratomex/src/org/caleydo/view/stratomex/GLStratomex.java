/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.stratomex;

import gleem.linalg.Vec2f;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

import org.caleydo.core.data.collection.column.container.CategoricalClassDescription;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainOracle;
import org.caleydo.core.data.datadomain.DataSupportDefinitions;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.datadomain.IDataSupportDefinition;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.data.selection.EventBasedSelectionManager;
import org.caleydo.core.data.selection.IEventBasedSelectionManagerUser;
import org.caleydo.core.data.selection.SelectionCommands;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.selection.events.SelectionCommandListener;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.events.RecordVAUpdateEvent;
import org.caleydo.core.data.virtualarray.similarity.RelationAnalyzer;
import org.caleydo.core.event.EventListenerManager;
import org.caleydo.core.event.EventListenerManager.DeepScan;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventListenerManagers;
import org.caleydo.core.event.data.RelationsUpdatedEvent;
import org.caleydo.core.event.data.RemoveDataDomainEvent;
import org.caleydo.core.event.data.ReplaceTablePerspectiveEvent;
import org.caleydo.core.event.data.SelectionUpdateEvent;
import org.caleydo.core.event.view.TablePerspectivesChangedEvent;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.base.IUniqueObject;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.view.IMultiTablePerspectiveBasedView;
import org.caleydo.core.view.ViewManager;
import org.caleydo.core.view.listener.AddTablePerspectivesEvent;
import org.caleydo.core.view.listener.RemoveTablePerspectiveEvent;
import org.caleydo.core.view.listener.RemoveTablePerspectiveListener;
import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutManager;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.ColorRenderer;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.IPickingLabelProvider;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.spline.ConnectionBandRenderer;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.datadomain.pathway.data.PathwayTablePerspective;
import org.caleydo.view.stratomex.addin.IStratomeXAddIn;
import org.caleydo.view.stratomex.addin.StratomeXAddIns;
import org.caleydo.view.stratomex.brick.GLBrick;
import org.caleydo.view.stratomex.brick.configurer.CategoricalDataConfigurer;
import org.caleydo.view.stratomex.brick.configurer.ClinicalDataConfigurer;
import org.caleydo.view.stratomex.brick.configurer.IBrickConfigurer;
import org.caleydo.view.stratomex.brick.configurer.NumericalDataConfigurer;
import org.caleydo.view.stratomex.brick.configurer.PathwayDataConfigurer;
import org.caleydo.view.stratomex.brick.contextmenu.SplitBrickItem;
import org.caleydo.view.stratomex.column.BlockAdapter;
import org.caleydo.view.stratomex.column.BrickColumn;
import org.caleydo.view.stratomex.column.BrickColumnManager;
import org.caleydo.view.stratomex.column.BrickColumnSpacingRenderer;
import org.caleydo.view.stratomex.event.ConnectionsModeEvent;
import org.caleydo.view.stratomex.event.HighlightBandEvent;
import org.caleydo.view.stratomex.event.MergeBricksEvent;
import org.caleydo.view.stratomex.event.SelectElementsEvent;
import org.caleydo.view.stratomex.event.SplitBrickEvent;
import org.caleydo.view.stratomex.listener.AddGroupsToStratomexListener;
import org.caleydo.view.stratomex.listener.GLStratomexKeyListener;
import org.caleydo.view.stratomex.listener.ReplaceTablePerspectiveListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Composite;

/**
 * VisBricks main view
 *
 * @author Marc Streit
 * @author Alexander Lex
 */

public class GLStratomex extends AGLView implements IMultiTablePerspectiveBasedView, IGLRemoteRenderingView,
		IViewCommandHandler, IEventBasedSelectionManagerUser {

	public static final String VIEW_TYPE = "org.caleydo.view.stratomex";
	public static final String VIEW_NAME = "StratomeX";

	public final static int ARCH_PIXEL_HEIGHT = 100;
	private final static int ARCH_PIXEL_WIDTH = 80;
	private final static float ARCH_BOTTOM_PERCENT = 1f;
	private final static float ARCH_STAND_WIDTH_PERCENT = 0.05f;

	private final static int BRICK_COLUMN_SPACING_MIN_PIXEL_WIDTH = 20;
	public final static int BRICK_COLUMN_SIDE_SPACING = 50;

	public final static float[] ARCH_COLOR = { 0f, 0f, 0f, 0.1f };

	private AddGroupsToStratomexListener addGroupsToStratomexListener;
	private SelectionCommandListener selectionCommandListener;
	private RemoveTablePerspectiveListener<GLStratomex> removeTablePerspectiveListener;
	private ReplaceTablePerspectiveListener replaceTablePerspectiveListener;
	private final EventListenerManager listeners = EventListenerManagers.wrap(this);

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

	private Queue<BrickColumn> uninitializedSubViews = new LinkedList<>();

	private DragAndDropController dragAndDropController;

	private RelationAnalyzer relationAnalyzer;

	private ElementLayout leftBrickColumnSpacing;
	private ElementLayout rightBrickColumnSpacing;

	/**
	 * set of extra highlights to bands
	 */
	private Map<String, Color> bandHighlights = new HashMap<>(2);

	/**
	 * The id category used to map between the records of the dimension groups. Only data with the same recordIDCategory
	 * can be connected
	 */
	private IDCategory recordIDCategory;

	/**
	 * The selection manager for the records, used for highlighting the visual links
	 */
	private EventBasedSelectionManager recordSelectionManager;

	private boolean connectionsShowOnlySelected = false;
	private boolean connectionsHighlightDynamic = false;

	private int selectedConnectionBandID = -1;

	/**
	 * Determines the connection focus highlight dynamically in a range between 0 and 1
	 */
	private float connectionsFocusFactor;

	private boolean isHorizontalMoveDraggingActive = false;
	private int movedBrickColumn = -1;

	/**
	 * The position of the mouse in the previous render cycle when dragging columns
	 */
	private float previousXCoordinate = Float.NaN;
	/**
	 * If the mouse is dragged further out to the left than possible, this is set to where it was
	 */
	private float leftLimitXCoordinate = Float.NaN;
	/** Same as {@link #leftLimitXCoordinate} for the right side */
	private float rightLimitXCoordinate = Float.NaN;

	/** Needed for selecting the elements when a connection band is picked **/
	private HashMap<Integer, BrickConnection> hashConnectionBandIDToRecordVA = new HashMap<Integer, BrickConnection>();

	private HashMap<Perspective, HashMap<Perspective, BrickConnection>> hashRowPerspectivesToConnectionBandID = new HashMap<Perspective, HashMap<Perspective, BrickConnection>>();

	private int connectionBandIDCounter = 0;

	private boolean isConnectionLinesDirty = true;

	private List<TablePerspective> tablePerspectives;

	private int preDetailModeCenterColumnStartIndex = -1;
	private int preDetailModeRightColumnStartIndex = -1;

	private AddTablePerspectiveParameters addTablePerspectiveParameters;

	@DeepScan
	private final Collection<IStratomeXAddIn> addins = StratomeXAddIns.createFor(this);

	private class AddTablePerspectiveParameters {
		private final List<TablePerspective> newTablePerspectives;
		private final IBrickConfigurer brickConfigurer;
		private final BrickColumn sourceColumn;
		private final boolean addRight;

		public AddTablePerspectiveParameters(List<TablePerspective> tablePerspectives,
				IBrickConfigurer brickConfigurer, BrickColumn sourceColumn, boolean addRight) {
			this.newTablePerspectives = tablePerspectives;
			this.brickConfigurer = brickConfigurer;
			this.sourceColumn = sourceColumn;
			this.addRight = addRight;
		}
	}

	/**
	 * Constructor.
	 *
	 */
	public GLStratomex(IGLCanvas glCanvas, ViewFrustum viewFrustum) {

		super(glCanvas, viewFrustum, VIEW_TYPE, VIEW_NAME);

		connectionRenderer = new ConnectionBandRenderer();

		dragAndDropController = new DragAndDropController(this);

		brickColumnManager = new BrickColumnManager();

		glKeyListener = new GLStratomexKeyListener();

		relationAnalyzer = new RelationAnalyzer();

		tablePerspectives = new ArrayList<TablePerspective>();

		textureManager = new TextureManager(new ResourceLoader(Activator.getResourceLocator()));

		registerPickingListeners();

	}

	/**
	 * @return the addins, see {@link #addins}
	 */
	public Iterable<IStratomeXAddIn> getAddins() {
		return addins;
	}

	@Override
	public void init(GL2 gl) {
		displayListIndex = gl.glGenLists(1);

		textRenderer = new CaleydoTextRenderer(24);

		connectionRenderer.init(gl);

		layoutManager = new LayoutManager(viewFrustum, pixelGLConverter);
		mainRow = new Row();
		layoutManager.setBaseElementLayout(mainRow);

		leftColumnLayout = new Column("leftArchColumn");
		leftColumnLayout.setPixelSizeX(ARCH_PIXEL_WIDTH);

		rightColumnLayout = new Column("rightArchColumn");
		rightColumnLayout.setPixelSizeX(ARCH_PIXEL_WIDTH);
	}

	public void initLayouts() {

		brickColumnManager.getBrickColumnSpacers().clear();

		mainRow.clear();

		if (!isLeftDetailShown && !isRightDetailShown) {
			initLeftLayout();
		}
		initCenterLayout();
		if (!isLeftDetailShown && !isRightDetailShown) {
			initRightLayout();
		}

		layoutManager.updateLayout();

		updateConnectionLinesBetweenColumns();
	}

	private void initLeftLayout() {
		initSideLayout(leftColumnLayout, 0, brickColumnManager.getCenterColumnStartIndex());
	}

	private void initRightLayout() {
		initSideLayout(rightColumnLayout, brickColumnManager.getRightColumnStartIndex(), brickColumnManager
				.getBrickColumns().size());
	}

	public int getSideArchWidthPixels() {
		return pixelGLConverter.getPixelWidthForGLWidth(viewFrustum.getWidth() * ARCH_STAND_WIDTH_PERCENT);
	}

	/**
	 * Init the layout for the center region, showing the horizontal bar of the arch plus all sub-bricks above and below
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

		centerRowLayout = new Row("centerRowLayout");

		centerRowLayout.setPriorityRendereing(true);
		centerRowLayout.setFrameColor(0, 0, 1, 1);

		List<BlockAdapter> columns = new ArrayList<>();
		for (int columnIndex = brickColumnManager.getCenterColumnStartIndex(); columnIndex < brickColumnManager
				.getRightColumnStartIndex(); columnIndex++) {
			BrickColumn column = brickColumnManager.getBrickColumns().get(columnIndex);
			column.setCollapsed(false);
			column.setArchHeight(ARCH_PIXEL_HEIGHT);
			columns.add(new BlockAdapter(column));
		}
		for (IStratomeXAddIn addin : addins)
			addin.addColumns(columns);

		mainRow.append(centerRowLayout);

		// Handle special case where center contains no groups
		if (columns.isEmpty()) {
			leftBrickColumnSpacing = new ElementLayout("firstCenterDimGrSpacing");
			leftBrickColumnSpacing.setRenderer(new BrickColumnSpacingRenderer(null, connectionRenderer, null, null,
					this));
			leftBrickColumnSpacing.setGrabX(true);
			centerRowLayout.append(leftBrickColumnSpacing);
			rightBrickColumnSpacing = null;
			return;
		}

		leftBrickColumnSpacing = new ElementLayout("firstCenterDimGrSpacing");
		leftBrickColumnSpacing.setRenderer(new BrickColumnSpacingRenderer(null, connectionRenderer, null,
 columns
				.get(0), this));
		if (columns.size() > 1)
			leftBrickColumnSpacing.setPixelSizeX(BRICK_COLUMN_SIDE_SPACING);
		else
			leftBrickColumnSpacing.setGrabX(true);
		centerRowLayout.append(leftBrickColumnSpacing);

		BlockAdapter last = null;
		for (int i = 0; i < columns.size(); ++i) {
			BlockAdapter column = columns.get(i);
			if (i > 0) { // not the last one
				ElementLayout dynamicColumnSpacing = new ElementLayout("dynamicDimGrSpacing");
				dynamicColumnSpacing.setGrabX(true);
				dynamicColumnSpacing.setRenderer(new BrickColumnSpacingRenderer(relationAnalyzer, connectionRenderer,
						last, column, this));
				centerRowLayout.append(dynamicColumnSpacing);
			}

			centerRowLayout.add(column.asElementLayout());
			last = column;
		}

		rightBrickColumnSpacing = new ElementLayout("lastDimGrSpacing");
		rightBrickColumnSpacing.setRenderer(new BrickColumnSpacingRenderer(null, connectionRenderer, last, null, this));
		if (columns.size() > 1)
			rightBrickColumnSpacing.setPixelSizeX(BRICK_COLUMN_SIDE_SPACING);
		else
			rightBrickColumnSpacing.setGrabX(true);
		centerRowLayout.append(rightBrickColumnSpacing);
	}

	/**
	 * Initialize the layout for the sides of the arch
	 *
	 * @param columnLayout
	 * @param layoutTemplate
	 * @param layoutManager
	 * @param columnStartIndex
	 * @param columnEndIndex
	 */
	private void initSideLayout(Column columnLayout, int columnStartIndex, int columnEndIndex) {

		columnLayout.setFrameColor(1, 1, 0, 1);
		columnLayout.setBottomUp(true);
		columnLayout.clear();
		columnLayout.setRenderer(new ColorRenderer(new float[] { 0.7f, 0.7f, 0.7f, 1f }));

		ElementLayout columnSpacing = new ElementLayout("firstSideDimGrSpacing");
		columnSpacing.setGrabY(true);

		columnLayout.append(columnSpacing);

		BrickColumnSpacingRenderer brickColumnSpacingRenderer = null;

		// Handle special case where arch stand contains no groups
		if (columnStartIndex == 0 || columnStartIndex == columnEndIndex) {
			brickColumnSpacingRenderer = new BrickColumnSpacingRenderer(null, connectionRenderer, null, null, this);
		} else {
			brickColumnSpacingRenderer = new BrickColumnSpacingRenderer(null, connectionRenderer, null,
					new BlockAdapter(brickColumnManager.getBrickColumns().get(
							brickColumnManager.getCenterColumnStartIndex())), this);
		}

		columnSpacing.setRenderer(brickColumnSpacingRenderer);

		brickColumnSpacingRenderer.setVertical(false);

		for (int columnIndex = columnStartIndex; columnIndex < columnEndIndex; columnIndex++) {

			BrickColumn column = brickColumnManager.getBrickColumns().get(columnIndex);

			column.getLayout().setAbsoluteSizeY(archSideWidth);
			column.setArchHeight(-1);
			columnLayout.append(column.getLayout());

			column.setCollapsed(true);

			columnSpacing = new ElementLayout("sideDimGrSpacing");
			columnSpacing.setGrabY(true);

			brickColumnSpacingRenderer = new BrickColumnSpacingRenderer(null, null, new BlockAdapter(column), null,
					this);
			columnLayout.append(columnSpacing);

			columnSpacing.setRenderer(brickColumnSpacingRenderer);

			brickColumnSpacingRenderer.setVertical(false);

		}
		mainRow.append(columnLayout);

	}

	@Override
	public void initLocal(GL2 gl) {

		// Register keyboard listener to GL2 canva
		final Composite parentComposite = parentGLCanvas.asComposite();
		parentComposite.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				parentComposite.addKeyListener(glKeyListener);
			}
		});

		init(gl);
	}

	@Override
	public void initRemote(final GL2 gl, final AGLView glParentView, final GLMouseListener glMouseListener) {

		setMouseListener(glMouseListener);
		init(gl);
		initLayouts();
	}

	@Override
	public void displayLocal(GL2 gl) {
		pickingManager.handlePicking(this, gl);

		display(gl);

		if (!lazyMode) {
			checkForHits(gl);
		}
		for (IStratomeXAddIn addin : addins)
			addin.postDisplay();
	}

	@Override
	public void displayRemote(GL2 gl) {
		display(gl);
	}

	@Override
	public void display(GL2 gl) {
		boolean hasAnyThing = false;
		for (IStratomeXAddIn addin : addins)
			hasAnyThing = hasAnyThing || !addin.isEmpty();

		if ((tablePerspectives == null || tablePerspectives.isEmpty()) && !hasAnyThing) {
			if (isDisplayListDirty) {
				gl.glNewList(displayListIndex, GL2.GL_COMPILE);

				boolean viaAddin = false;
				for (IStratomeXAddIn addin : addins)
					if (!addin.isEmpty()) {
						addin.renderEmpty(gl, 0, getArchTopY(), getViewFrustum().getWidth(), getArchBottomY()
								- getArchTopY(), 0);
						viaAddin = true;
					}
				if (!viaAddin) {
					renderEmptyViewText(gl, new String[] { "Please use the the Data-View Integrator view to assign ",
							"one or multiple dataset(s) to StratomeX.",
							"Refer to http://help.caleydo.org for more information." });
				}
				gl.glEndList();
				isDisplayListDirty = false;
			}
			gl.glCallList(displayListIndex);
		} else {

			if (!uninitializedSubViews.isEmpty()) {
				while (uninitializedSubViews.peek() != null) {
					uninitializedSubViews.poll().initRemote(gl, this, glMouseListener);
				}
				initLayouts();
			}
			// if (isDisplayListDirty) {
			// buildDisplayList(gl, displayListIndex);
			// isDisplayListDirty = false;
			// }

			for (BrickColumn group : brickColumnManager.getBrickColumns()) {
				group.processEvents();
			}

			handleHorizontalColumnMove(gl);
			if (isLayoutDirty) {
				isLayoutDirty = false;
				layoutManager.updateLayout();
				float minWidth = pixelGLConverter.getGLWidthForPixelWidth(BRICK_COLUMN_SPACING_MIN_PIXEL_WIDTH);
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
				updateConnectionLinesBetweenColumns();
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
				// leftDimensionGroupSpacing =
				// centerRowLayout.getElements().get(0);
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
				// rightDimensionGroupSpacing =
				// centerRowLayout.getElements().get(
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

			for (BrickColumn column : brickColumnManager.getBrickColumns()) {
				column.display(gl);
			}

			if (isConnectionLinesDirty) {
				performConnectionLinesUpdate();
			}

			layoutManager.render(gl);

			// if (!isRightDetailShown && !isLeftDetailShown) {
			// gl.glCallList(displayListIndex);
			// }

			// call after all other rendering because it calls the onDrag
			// methods
			// which need alpha blending...
			dragAndDropController.handleDragging(gl, glMouseListener);
		}

		if (addTablePerspectiveParameters != null) {
			addTablePerspectives(addTablePerspectiveParameters.newTablePerspectives,
					addTablePerspectiveParameters.brickConfigurer, addTablePerspectiveParameters.sourceColumn,
					addTablePerspectiveParameters.addRight);
			addTablePerspectiveParameters = null;
		}
	}

	public boolean isDetailMode() {
		return isLeftDetailShown || isRightDetailShown;
	}

	/**
	 * @return
	 */
	public boolean canShowDetailBrick() {
		for (IStratomeXAddIn addin : addins)
			if (!addin.canShowDetailBrick())
				return false;
		return true;
	}

	/**
	 * Switches to detail mode where the detail brick is on the right side of the specified column
	 *
	 * @param focusColumn
	 *            the column that contains the focus brick
	 */
	public void switchToDetailModeRight(BrickColumn focusColumn) {

		if (!isRightDetailShown && !isLeftDetailShown) {
			preDetailModeCenterColumnStartIndex = brickColumnManager.getCenterColumnStartIndex();
			preDetailModeRightColumnStartIndex = brickColumnManager.getRightColumnStartIndex();
		}

		int columnIndex = brickColumnManager.indexOfBrickColumn(focusColumn);
		// false only if this is the rightmost column. If true we
		// move anything beyond the next column out
		if (columnIndex != brickColumnManager.getRightColumnStartIndex() - 1) {
			brickColumnManager.setRightColumnStartIndex(columnIndex + 2);

		}
		// false only if this is the leftmost colum. If true we
		// move anything further left out
		if (columnIndex != brickColumnManager.getCenterColumnStartIndex()) {
			brickColumnManager.setCenterColumnStartIndex(columnIndex);
		}

		isRightDetailShown = true;
		mainRow.remove(rightColumnLayout);
		mainRow.remove(leftColumnLayout);
		initLayouts();
		setDisplayListDirty();
	}

	/**
	 * Switches to detail mode where the detail brick is on the left side of the specified column.
	 *
	 * @param focusColumn
	 *            the column that contains the detail brick
	 */
	public void switchToDetailModeLeft(BrickColumn focusColumn) {

		if (!isRightDetailShown && !isLeftDetailShown) {
			preDetailModeCenterColumnStartIndex = brickColumnManager.getCenterColumnStartIndex();
			preDetailModeRightColumnStartIndex = brickColumnManager.getRightColumnStartIndex();
		}

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
		if (!isRightDetailShown && !isLeftDetailShown) {
			restoreColumnSetup();
		}
		initLayouts();
	}

	private void restoreColumnSetup() {
		brickColumnManager.setCenterColumnStartIndex(preDetailModeCenterColumnStartIndex);
		brickColumnManager.setRightColumnStartIndex(preDetailModeRightColumnStartIndex);
	}

	/**
	 * Hide the detail brick which is shown left of its parent dimension group
	 */
	public void switchToOverviewModeLeft() {
		isLeftDetailShown = false;
		if (!isRightDetailShown && !isLeftDetailShown) {
			restoreColumnSetup();
		}
		initLayouts();
	}

	/**
	 * Handles the left-right dragging of the whole dimension group. Does collision handling and moves dimension groups
	 * to the sides if necessary.
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

		final Vec2f currentPoint = glMouseListener.getDIPPickedPoint();
		Vec2f pointCordinates = pixelGLConverter.convertMouseCoord2GL(currentPoint);

		if (Float.isNaN(previousXCoordinate)) {
			previousXCoordinate = pointCordinates.x();
			return;
		}
		float change = pointCordinates.x() - previousXCoordinate;

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
			if (leftLimitXCoordinate >= currentPoint.x())
				return;
			else
				leftLimitXCoordinate = Float.NaN;
		}

		if (!Float.isNaN(rightLimitXCoordinate)) {
			if (rightLimitXCoordinate <= currentPoint.x())
				return;
			else
				rightLimitXCoordinate = Float.NaN;
		}

		previousXCoordinate = pointCordinates.x();

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
		float minWidth = pixelGLConverter.getGLWidthForPixelWidth(BRICK_COLUMN_SPACING_MIN_PIXEL_WIDTH);

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
						rightLimitXCoordinate = currentPoint.x();
						break;
					}
					rightIndex += 2;
					ElementLayout spacing = centerRowLayout.get(rightIndex);
					if (spacing.getSizeScaledX() - remainingChange > minWidth + 0.001f) {
						spacing.setAbsoluteSizeX(spacing.getSizeScaledX() - remainingChange);
						remainingChange = 0;
						break;
					} else {
						savedSize = spacing.getSizeScaledX() - minWidth;
						remainingChange -= savedSize;
						if (rightIndex == centerRowLayout.size() - 1) {
							rightLimitXCoordinate = currentPoint.x();
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
						leftLimitXCoordinate = currentPoint.x();
						// }
						break;
					}
					leftIndex -= 2;
					ElementLayout spacing = centerRowLayout.get(leftIndex);
					if (spacing.getSizeScaledX() + remainingChange > minWidth + 0.001f) {
						// the whole change fits in the first spacing left of
						// the source
						spacing.setAbsoluteSizeX(spacing.getSizeScaledX() + remainingChange);
						remainingChange = 0;
						break;
					} else {
						savedSize = spacing.getSizeScaledX() - minWidth;
						remainingChange += savedSize;
						if (leftIndex == 0) {
							leftLimitXCoordinate = currentPoint.x();
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

				contextMenuCreator.addContextMenuItem(new SplitBrickItem(pick.getObjectID(), true));
				contextMenuCreator.addContextMenuItem(new SplitBrickItem(pick.getObjectID(), false));
			}

		}, EPickingType.BRICK_CONNECTION_BAND.name());

		addTypePickingListener(new APickingListener() {
			@Override
			public void clicked(Pick pick) {
				dragAndDropController.clearDraggables();
				dragAndDropController.setDraggingStartPosition(pick.getPickedPoint());
				dragAndDropController.addDraggable((BrickColumn) ViewManager.get().getGLView(pick.getObjectID()));
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

				if (dragAndDropController.isDragging() && dragAndDropController.getDraggingMode() != null
						&& dragAndDropController.getDraggingMode().equals("DimensionGroupDrag")) {
					dragAndDropController.setDropArea(brickColumnManager.getBrickColumnSpacers()
							.get(pick.getObjectID()));
				} else {
					if (dragAndDropController.isDragging()) {

					}
				}
			}

			@Override
			protected void clicked(Pick pick) {
				SelectionCommands.clearSelections();
				hideAllBrickWidgets();
			}
		}, EPickingType.DIMENSION_GROUP_SPACER.name());

		addTypePickingListener(new APickingListener() {
			@Override
			public void dragged(Pick pick) {

				if (dragAndDropController.isDragging() && dragAndDropController.getDraggingMode() != null
						&& dragAndDropController.getDraggingMode().equals("DimensionGroupDrag")) {
					dragAndDropController.setDropArea(brickColumnManager.getBrickColumnSpacers()
							.get(pick.getObjectID()));
				} else {
					if (dragAndDropController.isDragging()) {

					}
				}
			}

			@Override
			protected void mouseOver(Pick pick) {
				if (!isDetailMode()) {
					for (BrickColumnSpacingRenderer manager : brickColumnManager.getBrickColumnSpacers().values())
						manager.setHeaderHovered(true);
				}
				super.mouseOver(pick);
			}

			@Override
			protected void mouseOut(Pick pick) {
				for (BrickColumnSpacingRenderer manager : brickColumnManager.getBrickColumnSpacers().values())
					manager.setHeaderHovered(false);
				super.mouseOut(pick);
			}

			@Override
			protected void clicked(Pick pick) {
				SelectionCommands.clearSelections();
				hideAllBrickWidgets();
			}
		}, EPickingType.DIMENSION_GROUP_SPACER_HEADER.name());

		addTypePickingListener(new APickingListener() {
			@Override
			public void clicked(Pick pick) {
				isHorizontalMoveDraggingActive = true;
				movedBrickColumn = pick.getObjectID();
			}
		}, EPickingType.MOVE_HORIZONTALLY_HANDLE.name());

		addTypePickingTooltipListener(new IPickingLabelProvider() {

			@Override
			public String getLabel(Pick pick) {
				BrickConnection connection = hashConnectionBandIDToRecordVA.get(pick.getObjectID());
				if (connection == null)
					return "";

				int numSharedElements = connection.getSharedRecordVirtualArray().size();
				VirtualArray leftColumnVA = connection.getLeftBrick().getBrickColumn().getTablePerspective()
						.getRecordPerspective().getVirtualArray();
				int leftGroupIndex = connection.getLeftBrick().getTablePerspective().getRecordGroup().getGroupIndex();
				int numLeftElements = leftColumnVA.getIDsOfGroup(leftGroupIndex).size();
				float percentageLeft = (float) numSharedElements / numLeftElements * 100;

				VirtualArray rightColumnVA = connection.getRightBrick().getBrickColumn().getTablePerspective()
						.getRecordPerspective().getVirtualArray();
				int rightGroupIndex = connection.getRightBrick().getTablePerspective().getRecordGroup().getGroupIndex();
				int numRightElements = rightColumnVA.getIDsOfGroup(rightGroupIndex).size();
				float percentageRight = (float) numSharedElements / numRightElements * 100;
				DecimalFormat df = new DecimalFormat("#.##");

				return "Shared elements: " + numSharedElements + "\n" + df.format(percentageLeft) + "% of left group ("
						+ numLeftElements + " in total)\n" + df.format(percentageRight) + "% of right group ("
						+ numRightElements + " in total)";
			}
		}, EPickingType.BRICK_CONNECTION_BAND.name());

		for (IStratomeXAddIn addin : addins)
			addin.registerPickingListeners();
	}

	public void hideAllBrickWidgets() {
		for (BrickColumn brickColumn : brickColumnManager.getBrickColumns()) {
			brickColumn.getHeaderBrick().showWidgets(false);
			for (GLBrick brick : brickColumn.getSegmentBricks()) {
				brick.showWidgets(false);
			}
		}
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedStratomexView serializedForm = new SerializedStratomexView(this);
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
		eventPublisher.addListener(AddTablePerspectivesEvent.class, addGroupsToStratomexListener);

		removeTablePerspectiveListener = new RemoveTablePerspectiveListener<>();
		removeTablePerspectiveListener.setHandler(this);
		eventPublisher.addListener(RemoveTablePerspectiveEvent.class, removeTablePerspectiveListener);

		replaceTablePerspectiveListener = new ReplaceTablePerspectiveListener();
		replaceTablePerspectiveListener.setHandler(this);
		eventPublisher.addListener(ReplaceTablePerspectiveEvent.class, replaceTablePerspectiveListener);

		listeners.register(this);
	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();

		if (addGroupsToStratomexListener != null) {
			eventPublisher.removeListener(addGroupsToStratomexListener);
			addGroupsToStratomexListener = null;
		}

		if (selectionCommandListener != null) {
			eventPublisher.removeListener(selectionCommandListener);
			selectionCommandListener = null;
		}

		if (removeTablePerspectiveListener != null) {
			eventPublisher.removeListener(removeTablePerspectiveListener);
			removeTablePerspectiveListener = null;
		}

		if (replaceTablePerspectiveListener != null) {
			eventPublisher.removeListener(replaceTablePerspectiveListener);
			replaceTablePerspectiveListener = null;
		}
		listeners.unregisterAll();

		if (recordSelectionManager != null)
			recordSelectionManager.unregisterEventListeners();
	}

	@Override
	public void handleRedrawView() {
		// TODO Auto-generated method stub

	}

	public void clearAllSelections() {
		if (recordSelectionManager != null)
			recordSelectionManager.clearSelections();
		updateConnectionLinesBetweenColumns();
	}

	@Override
	public List<AGLView> getRemoteRenderedViews() {
		return new ArrayList<AGLView>(brickColumnManager.getBrickColumns());
	}

	@Override
	public void addTablePerspective(TablePerspective tablePerspective) {

		List<TablePerspective> tablePerspectiveWrapper = new ArrayList<TablePerspective>();
		tablePerspectiveWrapper.add(tablePerspective);
		addTablePerspectives(tablePerspectiveWrapper, null, null);
	}

	@Override
	public void addTablePerspectives(List<TablePerspective> newTablePerspectives) {
		addTablePerspectives(newTablePerspectives, null, null);
	}

	/**
	 * <p>
	 * Creates a column for each TablePerspective supplied
	 * </p>
	 * <p>
	 * As StratomeX can only map between data sets that share a mapping between records, the imprinting of the IDType
	 * and IDCategory for the records is done here if there is no data set yet.
	 * </p>
	 *
	 * @param newTablePerspectives
	 * @param brickConfigurer
	 *            The brick configurer can be specified externally (e.g., pathways, kaplan meier). If null, the
	 *            {@link NumericalDataConfigurer} will be used.
	 */
	public void addTablePerspectives(List<TablePerspective> newTablePerspectives, IBrickConfigurer brickConfigurer,
			BrickColumn sourceColumn) {
		addTablePerspectives(newTablePerspectives, brickConfigurer, sourceColumn, true);
	}

	public List<Pair<Integer, BrickColumn>> addTablePerspectives(List<TablePerspective> newTablePerspectives,
			IBrickConfigurer brickConfigurer, BrickColumn sourceColumn, boolean addRight) {

		List<Pair<Integer, BrickColumn>> added = new ArrayList<>();

		if (newTablePerspectives == null || newTablePerspectives.size() == 0) {
			Logger.log(new Status(IStatus.WARNING, this.toString(),
					"newTablePerspectives in addTablePerspectives was null or empty"));
			return added;
		}
		ArrayList<BrickColumn> brickColumns = brickColumnManager.getBrickColumns();

		if (isLeftDetailShown || isRightDetailShown) {
			// Hide detail bricks and add later
			for (BrickColumn brickColumn : brickColumns) {
				if (brickColumn.isDetailBrickShown()) {
					brickColumn.hideDetailedBrick();
				}
			}
			this.addTablePerspectiveParameters = new AddTablePerspectiveParameters(newTablePerspectives,
					brickConfigurer, sourceColumn, addRight);
			return added;
		}

		// if this is the first data container set, we imprint StratomeX
		if (recordIDCategory == null) {
			ATableBasedDataDomain dataDomain = newTablePerspectives.get(0).getDataDomain();
			imprintStratomex(dataDomain);
		}

		for (TablePerspective tablePerspective : newTablePerspectives) {
			if (tablePerspective == null) {
				Logger.log(new Status(IStatus.ERROR, this.toString(), "Data container was null."));
				continue;
			}
			// if (!tablePerspective.getDataDomain().getTable().isDataHomogeneous() && brickConfigurer == null) {
			// Logger.log(new Status(IStatus.WARNING, this.toString(),
			// "Tried to add inhomogeneous table perspective without brick configurerer. Currently not supported."));
			// continue;
			// }
			if (!tablePerspective.getDataDomain().getRecordIDCategory().equals(recordIDCategory)) {
				Logger.log(new Status(IStatus.ERROR, this.toString(), "Data container " + tablePerspective
						+ "does not match the recordIDCategory of Visbricks - no mapping possible."));
				continue;
			}

			boolean columnExists = false;
			for (BrickColumn brickColumn : brickColumns) {
				if (brickColumn.getTablePerspective().getID() == tablePerspective.getID()) {
					columnExists = true;
					break;
				}
			}

			if (!columnExists) {
				BrickColumn brickColumn = createBrickColumn(brickConfigurer, tablePerspective);
				int columnIndex;
				if (sourceColumn == null)
					columnIndex = addRight ? brickColumnManager.getRightColumnStartIndex() : brickColumnManager
							.getCenterColumnStartIndex();
				else
					columnIndex = brickColumns.indexOf(sourceColumn) + 1;

				added.add(Pair.make(columnIndex, brickColumn));

				brickColumns.add(columnIndex, brickColumn);
				tablePerspectives.add(tablePerspective);

				// if (tablePerspective instanceof PathwayTablePerspective) {
				// dataDomains.add(((PathwayTablePerspective) tablePerspective)
				// .getPathwayDataDomain());
				// } else {
				// dataDomains.add(tablePerspective.getDataDomain());
				// }

				brickColumnManager.setRightColumnStartIndex(brickColumnManager.getRightColumnStartIndex() + 1);
			}
		}

		TablePerspectivesChangedEvent event = new TablePerspectivesChangedEvent(this);
		event.setSender(this);
		GeneralManager.get().getEventPublisher().triggerEvent(event);

		return added;
	}

	/**
	 * @param tablePerspective
	 * @param brickConfigurer
	 * @return
	 */
	private BrickColumn createBrickColumn(IBrickConfigurer brickConfigurer, TablePerspective tablePerspective) {
		BrickColumn brickColumn = (BrickColumn) GeneralManager
				.get()
				.getViewManager()
				.createGLView(BrickColumn.class, getParentGLCanvas(),
						new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC, 0, 1, 0, 1, -1, 1));

		/**
		 * If no brick configurer was specified in the {@link AddGroupsToVisBricksEvent}, then the numerical configurer
		 * is created by default
		 **/
		if (brickConfigurer == null) {
			brickConfigurer = createDefaultBrickConfigurer(tablePerspective);
		}

		brickColumn.setDetailLevel(this.getDetailLevel());
		brickColumn.setBrickConfigurer(brickConfigurer);
		brickColumn.setDataDomain(tablePerspective != null ? tablePerspective.getDataDomain() : null);
		brickColumn.setTablePerspective(tablePerspective);
		brickColumn.setRemoteRenderingGLView(this);
		brickColumn.setStratomex(this);
		brickColumn.initialize();
		uninitializedSubViews.add(brickColumn);

		for (IStratomeXAddIn addin : addins)
			addin.addedBrickColumn(brickColumn);

		return brickColumn;
	}

	public IBrickConfigurer createDefaultBrickConfigurer(TablePerspective tablePerspective) {
		IBrickConfigurer brickConfigurer;
		// FIXME this is a hack to make tablePerspectives that have
		// only one dimension categorical data
		if (tablePerspective instanceof PathwayTablePerspective) {
			brickConfigurer = new PathwayDataConfigurer();
		} else if (tablePerspective.getNrDimensions() == 1) {

			Object description = tablePerspective
					.getDataDomain()
					.getTable()
					.getDataClassSpecificDescription(
							tablePerspective.getDimensionPerspective().getVirtualArray().get(0),
							tablePerspective.getRecordPerspective().getVirtualArray().get(0));

			if (DataDomainOracle.isClinical(tablePerspective.getDataDomain())
					&& !(description instanceof CategoricalClassDescription<?>)) {
				brickConfigurer = new ClinicalDataConfigurer();
			} else {
				brickConfigurer = new CategoricalDataConfigurer(tablePerspective);
			}
		} else {
			brickConfigurer = new NumericalDataConfigurer(tablePerspective);
		}
		return brickConfigurer;
	}

	@Override
	public void removeTablePerspective(TablePerspective tablePerspective) {

		Iterator<TablePerspective> tablePerspectiveIterator = tablePerspectives.iterator();

		while (tablePerspectiveIterator.hasNext()) {
			TablePerspective container = tablePerspectiveIterator.next();
			if (container == tablePerspective) {
				tablePerspectiveIterator.remove();
			}
		}

		brickColumnManager.removeBrickColumn(tablePerspective.getID());
		// remove uninitalized referenced
		for (Iterator<BrickColumn> it = uninitializedSubViews.iterator(); it.hasNext();) {
			if (it.next().getTablePerspective() == tablePerspective)
				it.remove();
		}
		cleanUp(tablePerspective);

		initLayouts();
		TablePerspectivesChangedEvent event = new TablePerspectivesChangedEvent(this);
		event.setSender(this);
		GeneralManager.get().getEventPublisher().triggerEvent(event);
	}

	private void cleanUp(TablePerspective tablePerspective) {
		// cleanup band connections
		for (Iterator<BrickConnection> it = hashConnectionBandIDToRecordVA.values().iterator(); it.hasNext();) {
			BrickConnection next = it.next();
			if (next.refersTo(tablePerspective))
				it.remove();
		}
		for (Iterator<Map.Entry<Perspective, HashMap<Perspective, BrickConnection>>> it = hashRowPerspectivesToConnectionBandID
				.entrySet().iterator(); it.hasNext();) {
			Entry<Perspective, HashMap<Perspective, BrickConnection>> next = it.next();
			for (Iterator<BrickConnection> it2 = next.getValue().values().iterator(); it2.hasNext();) {
				BrickConnection next2 = it2.next();
				if (next2.refersTo(tablePerspective))
					it2.remove();
			}
			if (next.getValue().isEmpty())
				it.remove();
		}
		// remove unused stuff
		boolean columnWithPerspectiveRemaining = false;
		for (BrickColumn column : brickColumnManager.getBrickColumns()) {
			if (column.getTablePerspective().getRecordPerspective() == tablePerspective.getRecordPerspective()) {
				columnWithPerspectiveRemaining = true;
				break;
			}
		}
		if (!columnWithPerspectiveRemaining) {
			relationAnalyzer.removeAll(tablePerspective.getRecordPerspective());
			eventPublisher.triggerEvent(new RelationsUpdatedEvent().from(this));
		}
	}

	@ListenTo
	private void removeDataDomain(RemoveDataDomainEvent event) {
		String dataDomainID = event.getEventSpace();
		List<TablePerspective> tmp = new ArrayList<>(getTablePerspectives());
		for (TablePerspective p : tmp) {
			if (dataDomainID.equals(p.getDataDomain().getDataDomainID()))
				removeTablePerspective(p);
		}
		System.out.println();
	}

	@Override
	public List<TablePerspective> getTablePerspectives() {
		return tablePerspectives;
	}

	/**
	 * Replaces an old tablePerspective with a new one whil keeping the column at the same place.
	 *
	 * @param newTablePerspective
	 * @param oldTablePerspective
	 */
	public void replaceTablePerspective(TablePerspective newTablePerspective, TablePerspective oldTablePerspective) {

		Iterator<TablePerspective> tablePerspectiveIterator = tablePerspectives.iterator();
		while (tablePerspectiveIterator.hasNext()) {
			TablePerspective tempPerspective = tablePerspectiveIterator.next();
			if (tempPerspective.equals(oldTablePerspective)) {
				cleanUp(tempPerspective);
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
	 * Imprints VisBricks to a particular record ID Category by setting the {@link #recordIDCategory}, and initializes
	 * the {@link #recordSelectionManager}.
	 *
	 * @param dataDomain
	 */
	private void imprintStratomex(ATableBasedDataDomain dataDomain) {
		recordIDCategory = dataDomain.getRecordIDCategory();
		IDType mappingRecordIDType;
		if (recordIDCategory.getCategoryName().equals("GENE")) {
			// FIXME: this hack is necessary because otherwise we get pretty unintuitive results for the gene case as we
			// have multimappings (bands highlight in portions that aren't even selected).
			mappingRecordIDType = dataDomain.getRecordIDType();
		} else {
			mappingRecordIDType = recordIDCategory.getPrimaryMappingType();
		}
		recordSelectionManager = new EventBasedSelectionManager(this, mappingRecordIDType);
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

	public void moveColumn(BrickColumnSpacingRenderer spacer, BrickColumn movedColumn, BrickColumn referenceColumn) {
		movedColumn.getLayout().reset();
		clearColumnSpacerHighlight();

		if (movedColumn == referenceColumn)
			return;

		boolean insertComplete = false;

		ArrayList<BrickColumn> columns = brickColumnManager.getBrickColumns();
		for (ElementLayout leftLayout : leftColumnLayout) {
			if (spacer == leftLayout.getRenderer()) {

				brickColumnManager.setCenterColumnStartIndex(brickColumnManager.getCenterColumnStartIndex() + 1);

				columns.remove(movedColumn);
				if (referenceColumn == null) {
					columns.add(0, movedColumn);
				} else {
					columns.add(columns.indexOf(referenceColumn), movedColumn);
				}

				insertComplete = true;
				break;
			}
		}

		if (!insertComplete) {
			for (ElementLayout rightLayout : rightColumnLayout) {
				if (spacer == rightLayout.getRenderer()) {

					brickColumnManager.setRightColumnStartIndex(brickColumnManager.getRightColumnStartIndex() - 1);

					columns.remove(movedColumn);
					if (referenceColumn == null) {
						columns.add(columns.size(), movedColumn);
					} else {
						columns.add(columns.indexOf(referenceColumn) + 1, movedColumn);
					}

					insertComplete = true;
					break;
				}
			}
		}

		if (!insertComplete) {
			for (ElementLayout centerLayout : centerRowLayout) {
				if (spacer == centerLayout.getRenderer()) {

					if (columns.indexOf(movedColumn) < brickColumnManager.getCenterColumnStartIndex())
						brickColumnManager
								.setCenterColumnStartIndex(brickColumnManager.getCenterColumnStartIndex() - 1);
					else if (columns.indexOf(movedColumn) >= brickColumnManager.getRightColumnStartIndex())
						brickColumnManager.setRightColumnStartIndex(brickColumnManager.getRightColumnStartIndex() + 1);

					columns.remove(movedColumn);
					if (referenceColumn == null) {
						columns.add(brickColumnManager.getCenterColumnStartIndex(), movedColumn);
					} else {

						columns.add(columns.indexOf(referenceColumn) + 1, movedColumn);

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

	/** FIXME: documentation */
	public void clearColumnSpacerHighlight() {
		// Clear previous spacer highlights
		for (ElementLayout element : centerRowLayout) {
			if (element.getRenderer() instanceof BrickColumnSpacingRenderer)
				((BrickColumnSpacingRenderer) element.getRenderer()).setRenderSpacer(false);
		}

		for (ElementLayout element : leftColumnLayout) {
			if (element.getRenderer() instanceof BrickColumnSpacingRenderer)
				((BrickColumnSpacingRenderer) element.getRenderer()).setRenderSpacer(false);
		}

		for (ElementLayout element : rightColumnLayout) {
			if (element.getRenderer() instanceof BrickColumnSpacingRenderer)
				((BrickColumnSpacingRenderer) element.getRenderer()).setRenderSpacer(false);
		}
	}

	public BrickColumnManager getBrickColumnManager() {
		return brickColumnManager;
	}

	public void updateConnectionLinesBetweenColumns() {

		isConnectionLinesDirty = true;
	}

	private void performConnectionLinesUpdate() {
		connectionBandIDCounter = 0;

		if (centerRowLayout != null) {
			for (ElementLayout elementLayout : centerRowLayout) {
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

	public void relayout() {
		initLayouts();
		setDisplayListDirty();
	}

	/**
	 * Set whether the last resize of any sub-brick was to the left(true) or to the right. Important for determining,
	 * which brick to kick next.
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

	public SelectionManager getRecordSelectionManager() {
		return recordSelectionManager;
	}

	public GLStratomexKeyListener getKeyListener() {
		return (GLStratomexKeyListener) glKeyListener;
	}

	@ListenTo
	private void onHandleTrendHighlightMode(ConnectionsModeEvent event) {
		this.connectionsShowOnlySelected = event.isConnectionsShowOnlySelected();
		this.connectionsHighlightDynamic = event.isConnectionsHighlightDynamic();
		this.connectionsFocusFactor = event.getFocusFactor();

		updateConnectionLinesBetweenColumns();
	}

	public boolean isConnectionsShowOnlySelected() {
		return connectionsShowOnlySelected;
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

	/**
	 * @return the hashConnectionBandIDToRecordVA, see {@link #hashConnectionBandIDToRecordVA}
	 */
	public HashMap<Integer, BrickConnection> getHashConnectionBandIDToRecordVA() {
		return hashConnectionBandIDToRecordVA;
	}

	/**
	 * @return the hashTablePerspectivesToConnectionBandID, see {@link #hashRowPerspectivesToConnectionBandID}
	 */
	public HashMap<Perspective, HashMap<Perspective, BrickConnection>> getHashTablePerspectivesToConnectionBandID() {
		return hashRowPerspectivesToConnectionBandID;
	}

	public void selectElementsByConnectionBandID(Perspective recordPerspective1, Perspective recordPerspective2) {

		BrickConnection connectionBand = null;
		HashMap<Perspective, BrickConnection> tmp = hashRowPerspectivesToConnectionBandID.get(recordPerspective1);
		if (tmp != null) {
			connectionBand = tmp.get(recordPerspective2);
		} else {
			tmp = hashRowPerspectivesToConnectionBandID.get(recordPerspective2);
			if (tmp != null)
				connectionBand = tmp.get(recordPerspective1);
		}

		if (connectionBand != null)
			selectElementsByConnectionBandID(connectionBand.getConnectionBandID());
	}

	public void selectElementsByConnectionBandID(int connectionBandID) {
		BrickConnection connectionBand = hashConnectionBandIDToRecordVA.get(connectionBandID);
		VirtualArray recordVA = connectionBand.getSharedRecordVirtualArray();

		GLBrick leftBrick = connectionBand.getLeftBrick();
		EventBasedSelectionManager tablePerspectiveSelectionManager = leftBrick.getTablePerspectiveSelectionManager();
		tablePerspectiveSelectionManager.clearSelection(tablePerspectiveSelectionManager.getSelectionType());
		tablePerspectiveSelectionManager.triggerSelectionUpdateEvent();
		// Required because the selection manager of this brick does not report the clear selection to this brick itself
		leftBrick.setSelected(false);

		selectElements(recordVA, recordVA.getIdType(), leftBrick.getDataDomain().getDataDomainID(),
				recordSelectionManager.getSelectionType());
	}

	@ListenTo(sendToMe = true)
	private void onSelectElements(SelectElementsEvent event) {
		selectElements(event.getIds(), event.getIdType(), event.getEventSpace(), event.getSelectionType());
	}

	@ListenTo(sendToMe = true)
	private void onHighlightBand(HighlightBandEvent event) {
		if (event.isClearAll()) {
			bandHighlights.clear();
		} else {
			String key = toHighlightBandKey(event.getGroupA(), event.getGroupB());
			String key2 = toHighlightBandKey(event.getGroupB(), event.getGroupA());
			if (event.isHighlight()) {
				bandHighlights.put(key, event.getColor());
				bandHighlights.put(key2, event.getColor());
			} else {
				bandHighlights.remove(key);
				bandHighlights.remove(key2);
			}
		}

		setDisplayListDirty();
	}

	private static String toHighlightBandKey(IUniqueObject ag, IUniqueObject bg) {
		if (ag == null || bg == null)
			return ""; // dummy
		return ag.getID() + "/" + bg.getID();
	}

	public Color isHighlightingBand(GLBrick a, GLBrick b) {
		final TablePerspective at = a.getTablePerspective();
		final TablePerspective bt = b.getTablePerspective();
		IUniqueObject i1 = at.getRecordGroup();
		IUniqueObject i2 = bt.getRecordGroup();
		Color c;
		if ((c = bandHighlights.get(toHighlightBandKey(i1, i2))) != null)
			return c;
		if (at instanceof PathwayTablePerspective
				&& (c = bandHighlights.get(toHighlightBandKey(((PathwayTablePerspective) at).getPathway(), i2))) != null)
			return c;
		if (bt instanceof PathwayTablePerspective
				&& (c = bandHighlights.get(toHighlightBandKey(i1, ((PathwayTablePerspective) bt).getPathway()))) != null)
			return c;
		return null;
	}

	public void selectElements(Iterable<Integer> ids, IDType idType, String dataDomainID, SelectionType selectionType) {
		if (recordSelectionManager == null)
			return;

		if (!getKeyListener().isCtrlDown())
			recordSelectionManager.clearSelection(selectionType);

		for (Integer recordID : ids) {
			recordSelectionManager.addToType(selectionType, idType, recordID);
		}

		SelectionUpdateEvent event = new SelectionUpdateEvent();
		event.setSender(this);
		SelectionDelta delta = recordSelectionManager.getDelta();
		event.setSelectionDelta(delta);
		eventPublisher.triggerEvent(event);

		updateConnectionLinesBetweenColumns();
	}

	@ListenTo
	private void onSplitBrick(SplitBrickEvent event) {
		splitBrick(event.getConnectionBandID(), event.isSplitLeftBrick());
	}

	/**
	 * Splits a brick into two portions: those values that are in the band identified through the connection band id and
	 * the others.
	 */
	public void splitBrick(Integer connectionBandID, boolean isSplitLeftBrick) {
		BrickConnection brickConnection = hashConnectionBandIDToRecordVA.get(connectionBandID);
		VirtualArray sharedRecordVA = brickConnection.getSharedRecordVirtualArray();

		Perspective sourcePerspective;
		VirtualArray sourceVA;
		Integer sourceGroupIndex;
		GLBrick sourceBrick;
		if (isSplitLeftBrick) {
			sourceBrick = brickConnection.getLeftBrick();
		} else {
			sourceBrick = brickConnection.getRightBrick();
		}

		sourcePerspective = sourceBrick.getBrickColumn().getTablePerspective().getRecordPerspective();
		sourceVA = sourcePerspective.getVirtualArray();
		sourceGroupIndex = sourceBrick.getTablePerspective().getRecordGroup().getGroupIndex();

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

		IDMappingManager idMappingManager = IDMappingManagerRegistry.get().getIDMappingManager(
				sourceVA.getIdType().getIDCategory());

		if (idNeedsConverting) {
			VirtualArray mappedSharedRecordVA = new VirtualArray(sourceVA.getIdType());
			for (Integer recordID : sharedRecordVA) {
				recordID = idMappingManager.getID(sharedRecordVA.getIdType(), sourceVA.getIdType(), recordID);
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
		List<Integer> groupSizes = new ArrayList<Integer>(sourceVA.getGroupList().size() + 1);
		List<String> groupNames = new ArrayList<String>(sourceVA.getGroupList().size() + 1);
		List<Integer> sampleElements = new ArrayList<Integer>(sourceVA.getGroupList().size() + 1);

		// build up the data for the perspective
		int sizeCounter = 0;
		for (Integer groupIndex = 0; groupIndex < sourceVA.getGroupList().size(); groupIndex++) {
			if (groupIndex == sourceGroupIndex) {
				newIDs.addAll(sharedRecordVA.getIDs());
				groupSizes.add(sharedRecordVA.size());
				sampleElements.add(sizeCounter);
				sizeCounter += sharedRecordVA.size();
				groupNames.add(sourceVA.getGroupList().get(groupIndex).getLabel() + " Split 1");

				newIDs.addAll(remainingGroupIDs);
				groupSizes.add(remainingGroupIDs.size());
				sampleElements.add(sizeCounter);
				sizeCounter += remainingGroupIDs.size();
				groupNames.add(sourceVA.getGroupList().get(groupIndex).getLabel() + " Split 2");
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

	@ListenTo
	private void onMergeBricks(MergeBricksEvent event) {

		List<GLBrick> bricksToMerge = event.getBricks();
		if (bricksToMerge.size() <= 1)
			return;
		Collections.reverse(bricksToMerge);

		BrickColumn brickColumn = bricksToMerge.get(0).getBrickColumn();
		Perspective sourcePerspective = brickColumn.getTablePerspective().getRecordPerspective();
		VirtualArray sourceVA = sourcePerspective.getVirtualArray();
		List<GLBrick> bricks = brickColumn.getSegmentBricks();
		Collections.reverse(bricks);

		List<Integer> mergedBrickIDs = new ArrayList<>();

		// add ids of all bricks
		for (GLBrick brick : bricksToMerge) {
			mergedBrickIDs.addAll(brick.getTablePerspective().getRecordPerspective().getVirtualArray().getIDs());
		}

		List<Integer> newIDs = new ArrayList<Integer>(sourceVA.size());
		List<Integer> groupSizes = new ArrayList<Integer>(bricks.size() - bricksToMerge.size() + 1);
		List<String> groupNames = new ArrayList<String>(bricks.size() - bricksToMerge.size() + 1);
		List<Integer> sampleElements = new ArrayList<Integer>(bricks.size() - bricksToMerge.size() + 1);
		boolean mergedBrickAdded = false;

		int sizeCounter = 0;
		for (GLBrick brick : bricks) {
			if (bricksToMerge.contains(brick)) {
				if (mergedBrickAdded)
					continue;
				newIDs.addAll(mergedBrickIDs);
				groupSizes.add(mergedBrickIDs.size());
				sampleElements.add(sizeCounter);
				sizeCounter += mergedBrickIDs.size();
				StringBuilder label = new StringBuilder("Merge of ");
				for (int i = 0; i < bricksToMerge.size(); i++) {
					GLBrick b = bricksToMerge.get(i);
					label.append(sourceVA.getGroupList().get(b.getTablePerspective().getRecordGroup().getGroupIndex())
							.getLabel());
					if (i < bricksToMerge.size() - 1)
						label.append(", ");
				}
				groupNames.add(label.toString());
				mergedBrickAdded = true;
			} else {
				List<Integer> brickIDs = brick.getTablePerspective().getRecordPerspective().getVirtualArray().getIDs();
				newIDs.addAll(brickIDs);
				groupSizes.add(brickIDs.size());
				sampleElements.add(sizeCounter);
				sizeCounter += brickIDs.size();
				groupNames.add(sourceVA.getGroupList()
						.get(brick.getTablePerspective().getRecordGroup().getGroupIndex()).getLabel());
			}
		}

		PerspectiveInitializationData data = new PerspectiveInitializationData();

		data.setData(newIDs, groupSizes, sampleElements, groupNames);
		// FIXME the rest should probably not be done here but in the data
		// domain.
		sourcePerspective.init(data);

		RecordVAUpdateEvent e = new RecordVAUpdateEvent();
		e.setPerspectiveID(sourcePerspective.getPerspectiveID());

		eventPublisher.triggerEvent(e);
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
			return dataDomains;
		}
		for (TablePerspective tablePerspective : tablePerspectives) {
			if (tablePerspective instanceof PathwayTablePerspective) {
				dataDomains.add(((PathwayTablePerspective) tablePerspective).getPathwayDataDomain());
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

		if (layoutManager != null)
			layoutManager.destroy(gl);
	}

	public Row getLayout() {
		return mainRow;
	}

	@Override
	public IDataSupportDefinition getDataSupportDefinition() {
		return DataSupportDefinitions.all;
	}

	@Override
	public void notifyOfSelectionChange(EventBasedSelectionManager selectionManager) {
		updateConnectionLinesBetweenColumns();
	}

	/**
	 * @param wizard
	 */
	public void registerEventListener(Object elem) {
		listeners.register(elem);
	}

	public void unregisterEventListener(Object elem) {
		listeners.unregister(elem);
	}
}
