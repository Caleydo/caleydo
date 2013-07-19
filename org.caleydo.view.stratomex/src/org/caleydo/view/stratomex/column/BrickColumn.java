/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.stratomex.column;

import gleem.linalg.Vec2f;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.media.opengl.GL2;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.events.RecordVAUpdateEvent;
import org.caleydo.core.data.virtualarray.events.RecordVAUpdateListener;
import org.caleydo.core.event.EventListenerManager;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventListenerManagers;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.ViewManager;
import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.ATableBasedView;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.ILayoutedElement;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.Row.HAlign;
import org.caleydo.core.view.opengl.layout.event.ILayoutSizeCollisionHandler;
import org.caleydo.core.view.opengl.layout.event.LayoutSizeCollisionEvent;
import org.caleydo.core.view.opengl.layout.event.LayoutSizeCollisionListener;
import org.caleydo.core.view.opengl.layout.util.ViewLayoutRenderer;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.draganddrop.IDraggable;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.view.stratomex.GLStratomex;
import org.caleydo.view.stratomex.brick.GLBrick;
import org.caleydo.view.stratomex.brick.GLBrick.EBrickHeightMode;
import org.caleydo.view.stratomex.brick.GLBrick.EBrickWidthMode;
import org.caleydo.view.stratomex.brick.configurer.IBrickConfigurer;
import org.caleydo.view.stratomex.brick.layout.ABrickLayoutConfiguration;
import org.caleydo.view.stratomex.brick.layout.CompactHeaderBrickLayoutTemplate;
import org.caleydo.view.stratomex.brick.layout.DefaultBrickLayoutTemplate;
import org.caleydo.view.stratomex.brick.layout.DetailBrickLayoutTemplate;
import org.caleydo.view.stratomex.brick.layout.HeaderBrickLayoutTemplate;
import org.caleydo.view.stratomex.brick.ui.OverviewDetailBandRenderer;
import org.caleydo.view.stratomex.event.SelectDimensionSelectionEvent;

import com.google.common.collect.Iterables;

/**
 * Container for a group of dimensions. Manages layouts as well as brick views for the whole dimension group.
 *
 * @author Alexander Lex
 *
 */
public class BrickColumn extends ATableBasedView implements ILayoutSizeCollisionHandler, ILayoutedElement, IDraggable,
		IGLRemoteRenderingView {
	public static final String VIEW_TYPE = "org.caleydo.view.dimensiongroup";

	public static final String VIEW_NAME = "Brick Column";

	public final static int PIXEL_PER_DIMENSION = 30;
	public final static int MIN_BRICK_WIDTH_PIXEL = 170;
	public final static int OVERVIEW_DETAIL_GAP_PIXEL = 30;
	public final static int MIN_DETAIL_GAP_PIXEL = 10;
	public final static float DETAIL_GAP_PORTION = 0.05f;
	public final static int BETWEEN_BRICKS_SPACING = 10;

	/**
	 * marker color for {@link #setHighlightColor(float[])} to revert to the default color
	 */
	public final static Color REVERT_COLOR = new Color(0, 0, 0, 0);

	/**
	 * The brick at the top that shows information about and provides tools for interaction with the whole dimension
	 * group
	 */
	private GLBrick headerBrick;

	/** The bricks that show the grouping data */
	private ArrayList<GLBrick> clusterBricks;

	/**
	 * The large detail replicate brick
	 */
	private GLBrick detailBrick;

	/**
	 * The main layout that contains all other layouts. In detail mode, it contains the detail brick and the group
	 * Column, else it contains only the group column
	 */
	private Row mainRow;

	/**
	 * Contains the column including the header brick and the cluster bricks. A child of {@link #mainRow}.
	 */
	private Column mainColumn;

	/** The layout for the header brick. Child of {@link #mainColumn} */
	private Column headerBrickLayout;

	/**
	 * The column containing the cluster bricks, a child of {@link #clusterBrickWrapperColumn}
	 */
	protected Column clusterBrickColumn;

	/**
	 * Wrapper for {@link #clusterBrickColumn} that makes it possible that the clusterBrickColumn goes below the
	 * {@link #headerBrickLayout}. Child of {@link #mainColumn}.
	 */
	protected Column clusterBrickWrapperColumn;

	/** The layout for the detail brick. Child of {@link #mainRow} */
	private Column detailBrickLayout;

	/**
	 * Flag telling whether all cluster bricks are synchronized when switching views (true), or if every cluster brick
	 * may show whatever view it likes.
	 */
	private boolean isGlobalViewSwitching = false;

	// private ViewFrustum brickFrustum;
	// protected Table set;

	private LayoutSizeCollisionListener layoutSizeCollisionListener;

	/**
	 * Flag telling whether the DimensionGroup is collapsed (the cluster bricks are hidden) or not. Collapsed is used
	 * for storing the DimensionGroup in the context area
	 */
	private boolean isCollapsed = false;

	private Queue<GLBrick> uninitializedBricks = new LinkedList<GLBrick>();

	protected GLStratomex stratomex;

	// Stuff for dragging up and down
	private boolean isVerticalMoveDraggingActive = false;

	private float previousYCoordinate = Float.NaN;

	/** the minimal width of the brick */
	private int minPixelWidth;

	/**
	 * ID of the last {@link BrickSpacingRenderer} added.
	 */
	private int currentBrickSpacerID = 0;

	private boolean showDetailBrick = false;
	private boolean hideDetailBrick = false;
	private boolean isDetailBrickShown = false;
	private boolean expandLeft = false;

	private ElementLayout overviewDetailGapLayout;

	public static int BOTTOM_COLUMN_ID = 0;
	public static int TOP_COLUMN_ID = 1;

	IBrickConfigurer brickConfigurer;

	private final EventListenerManager eventListeners = EventListenerManagers.wrap(this);

	public BrickColumn(IGLCanvas canvas, ViewFrustum viewFrustum) {
		super(canvas, viewFrustum, VIEW_TYPE, VIEW_NAME);

	}

	@Override
	public void initialize() {

		super.initialize();
		initLayouts();
	}

	public void initLayouts() {
		mainRow = new Row("mainRow");
		// mainRow.setDebug(true);
		mainRow.setRenderingPriority(3);
		mainRow.setXDynamic(true);
		mainRow.setFrameColor(0, 0, 1, 1);
		mainRow.sethAlign(HAlign.CENTER);

		mainRow.addBackgroundRenderer(new FrameHighlightRenderer(null, false));

		mainColumn = new Column("mainColumn");
		mainColumn.setDebug(false);
		mainColumn.setPriorityRendereing(true);
		mainColumn.setBottomUp(false);
		mainColumn.setXDynamic(true);
		mainColumn.setVAlign(VAlign.CENTER);

		clusterBrickWrapperColumn = new Column("wrapperColumn");
		clusterBrickWrapperColumn.setDebug(false);
		clusterBrickWrapperColumn.setXDynamic(true);
		clusterBrickWrapperColumn.setFrameColor(0, 1, 0, 1);
		clusterBrickWrapperColumn.setVAlign(VAlign.CENTER);

		clusterBrickColumn = new Column("clusterBrickColumn");
		clusterBrickColumn.setDebug(false);
		clusterBrickColumn.setFrameColor(1, 0, 0, 1);
		clusterBrickColumn.setBottomUp(false);
		clusterBrickColumn.setXDynamic(true);
		clusterBrickColumn.setIDs(uniqueID, BOTTOM_COLUMN_ID);
		clusterBrickColumn.setVAlign(VAlign.CENTER);

		clusterBrickWrapperColumn.append(clusterBrickColumn);

		clusterBricks = new ArrayList<GLBrick>(20);

		headerBrickLayout = new Column("headerBrickLayout");
		headerBrickLayout.setXDynamic(true);
		headerBrickLayout.setYDynamic(true);
		headerBrickLayout.setFrameColor(1, 1, 0, 1);
		headerBrickLayout.setRenderingPriority(10);
		// headerBrickLayout.setPixelSizeY(60);
		// isCollapsed = true;
		initMainColumn();
		mainRow.append(mainColumn);
	}

	/**
	 * @param brickConfigurer
	 *            setter, see {@link #brickConfigurer}
	 */
	public void setBrickConfigurer(IBrickConfigurer brickConfigurer) {
		this.brickConfigurer = brickConfigurer;
	}

	/**
	 * @return the brickConfigurer, see {@link #brickConfigurer}
	 */
	public IBrickConfigurer getBrickConfigurer() {
		return brickConfigurer;
	}

	/**
	 * Initializes the main column with either only the headerBrick, when the dimensionGroup is collapsed, or the
	 * headerBrick and the clusterBricks.
	 */
	private void initMainColumn() {
		if (isCollapsed) {
			mainColumn.clear();
			mainColumn.append(headerBrickLayout);
		} else {
			mainColumn.clear();
			mainColumn.append(headerBrickLayout);
			mainColumn.append(clusterBrickWrapperColumn);
		}
	}

	/**
	 * Set this dimension group collapsed, i.e. only it's overview and caption is rendered and no other bricks
	 */
	public void setCollapsed(boolean isCollapsed) {
		this.isCollapsed = isCollapsed;
		// isCollapsed;
		// centerBrick.setBrickLayoutTemplate(new
		// CompactBrickLayoutTemplate(centerBrick,
		// glVisBricksView, this));

		if (headerBrick == null || uninitializedBricks.contains(headerBrick))
			return;

		headerBrick.setStaticBrickHeight(stratomex.getArchHeight());
		if (isCollapsed) {
			headerBrick.setBrickHeigthMode(EBrickHeightMode.VIEW_DEPENDENT);
			headerBrick.collapse();
		} else {
			headerBrick.setBrickHeigthMode(EBrickHeightMode.STATIC);
			if (headerBrick.isInOverviewMode()) {
				headerBrick.expand();
			}
		}
		initMainColumn();
	}

	public boolean isActive() {
		return headerBrick != null && headerBrick.isActive();
	}

	/**
	 * Creates all bricks of the dimension group
	 */
	protected void createBricks() {
		// create basic layouts

		// float[] glowColor = tablePerspective.getDataDomain().getColor().getRGBA();
		// if (tablePerspective instanceof PathwayTablePerspective) {
		// glowColor = ((PathwayTablePerspective) tablePerspective)
		// .getPathwayDataDomain().getColor().getRGBA();
		// }

		// mainColumn.addBackgroundRenderer(new BrickColumnGlowRenderer(glowColor, this,
		// false));

		ElementLayout innerHeaderBrickLayout = new ElementLayout();

		// headerBrickLayout2.setRenderingPriority(1);

		// innerHeaderBrickLayout.addBackgroundRenderer(new BrickColumnGlowRenderer(
		// glowColor, this, true));

		ElementLayout brickSpacingLayout = new ElementLayout("brickSpacingLayout");
		brickSpacingLayout.setPixelSizeY(BETWEEN_BRICKS_SPACING);
		brickSpacingLayout.setRatioSizeX(0);
		// brickSpacingLayout.setRenderer(new BrickSpacingRenderer(this,
		// currentBrickSpacerID++));
		headerBrickLayout.append(innerHeaderBrickLayout);
		headerBrickLayout.append(brickSpacingLayout);

		headerBrick = createBrick(innerHeaderBrickLayout, tablePerspective, true,
				isCollapsed ? CompactHeaderBrickLayoutTemplate.class : HeaderBrickLayoutTemplate.class);

		// ABrickLayoutConfiguration layoutTemplate;

		// if (isCollapsed) {
		// layoutTemplate = new CompactHeaderBrickLayoutTemplate(headerBrick, this, stratomex,
		// headerBrick.getBrickConfigurer());
		// } else {
		// layoutTemplate = new HeaderBrickLayoutTemplate(headerBrick, this, stratomex,
		// headerBrick.getBrickConfigurer());
		// }
		// headerBrick.setBrickLayoutTemplate(layoutTemplate);

		createClusterBricks();
	}

	/**
	 * Creates all bricks except for the center brick based on the groupList in the recordVA
	 */
	private void createClusterBricks() {

		destroyOldBricks();

		List<TablePerspective> brickTablePerspectives = tablePerspective.getRecordSubTablePerspectives();

		if (brickTablePerspectives == null || brickTablePerspectives.size() <= 0)
			return;

		List<GLBrick> segmentBricks = new ArrayList<GLBrick>();

		for (TablePerspective brickData : brickTablePerspectives) {

			GLBrick segmentBrick = createBrick(new ElementLayout("brick"), brickData, false,
					DefaultBrickLayoutTemplate.class);

			// segmentBrick.setBrickConfigurer(dimensionGroupData.getBrickConfigurer());

			// ABrickLayoutConfiguration layoutTemplate = new DefaultBrickLayoutTemplate(segmentBrick, this, stratomex,
			// segmentBrick.getBrickConfigurer());
			//
			// segmentBrick.setBrickLayoutTemplate(layoutTemplate);

			segmentBricks.add(segmentBrick);
		}

		List<GLBrick> sortedBricks = brickConfigurer.getBrickSortingStrategy().getSortedBricks(segmentBricks);

		addSortedBricks(sortedBricks);

		stratomex.getRelationAnalyzer().updateRelations(tablePerspective.getRecordPerspective().getPerspectiveID(),
				tablePerspective.getRecordPerspective().getVirtualArray());

	}

	private void addSortedBricks(List<GLBrick> sortedBricks) {
		for (GLBrick brick : sortedBricks) {
			// System.out.println("Average Value: "
			// +
			// brick.getTablePerspective().getContainerStatistics().getAverageValue());
			ElementLayout brickSpacingLayout = new ElementLayout("brickSpacingLayout");
			brickSpacingLayout.setPixelSizeY(BETWEEN_BRICKS_SPACING);
			brickSpacingLayout.setRatioSizeX(0f);
			BrickSpacingRenderer brickSpacingRenderer = new BrickSpacingRenderer(this, currentBrickSpacerID++, brick);
			brickSpacingLayout.setRenderer(brickSpacingRenderer);
			clusterBrickColumn.append(brickSpacingLayout);
			clusterBricks.add(brick);
			clusterBrickColumn.append(brick.getLayout());
		}

		// for (int count = 0; count < clusterBrickColumn.size();)
		// {
		// ElementLayout brickSpacingLayout = new
		// ElementLayout("brickSpacingLayout");
		// brickSpacingLayout.setPixelSizeY(BETWEEN_BRICKS_SPACING);
		// brickSpacingLayout.setRatioSizeX(0f);
		// BrickSpacingRenderer brickSpacingRenderer = new
		// BrickSpacingRenderer(this,
		// currentBrickSpacerID++);
		// brickSpacingLayout.setRenderer(brickSpacingRenderer);
		// clusterBrickColumn.add(count, brickSpacingLayout);
		// count++;
		// count++;
		// }

		ElementLayout brickSpacingLayout = new ElementLayout("brickSpacingLayout");
		brickSpacingLayout.setRatioSizeY(1);
		brickSpacingLayout.setRatioSizeX(0f);
		BrickSpacingRenderer brickSpacingRenderer = new BrickSpacingRenderer(this, currentBrickSpacerID++, null);
		brickSpacingLayout.setRenderer(brickSpacingRenderer);
		clusterBrickColumn.append(brickSpacingLayout);
	}

	/**
	 * Moves the specified brick from its current position to the position before the second specified brick. If the
	 * second brick is null, the brick will be moved to the last position.
	 *
	 * @param brickToMove
	 * @param brickAfter
	 */
	public void moveBrick(GLBrick brickToMove, GLBrick brickAfter) {
		if (brickAfter == brickToMove)
			return;

		List<GLBrick> sortedBricks = new ArrayList<GLBrick>(clusterBricks);

		int fromIndex = sortedBricks.indexOf(brickToMove);
		if (fromIndex == -1)
			return;
		sortedBricks.set(fromIndex, null);
		if (brickAfter == null) {
			sortedBricks.add(brickToMove);
		} else {
			sortedBricks.add(sortedBricks.indexOf(brickAfter), brickToMove);
		}

		sortedBricks.remove(null);

		clusterBrickColumn.clear();
		clusterBricks.clear();

		addSortedBricks(sortedBricks);
		stratomex.updateConnectionLinesBetweenColumns();
		stratomex.setLayoutDirty();

	}

	/**
	 * Creates a single brick
	 *
	 * @param wrappingLayout
	 * @return
	 */
	private GLBrick createBrick(ElementLayout wrappingLayout, TablePerspective tablePerspective, boolean isHeaderBrick,
			Class<? extends ABrickLayoutConfiguration> layoutTemplateClass) {
		ViewFrustum brickFrustum = new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC, 0, 0, 0, 0, -4, 4);
		GLBrick brick = (GLBrick) GeneralManager.get().getViewManager()
				.createGLView(GLBrick.class, parentGLCanvas, brickFrustum);

		brick.setDataDomain(dataDomain);
		brick.setTablePerspective(tablePerspective);
		brick.setBrickConfigurer(brickConfigurer);
		brick.setRemoteRenderingGLView(this);
		brick.setStratomex(stratomex);
		brick.setLayout(wrappingLayout);
		brick.setBrickColumn(this);
		brick.setHeaderBrick(isHeaderBrick);

		ABrickLayoutConfiguration layoutTemplate = null;
		try {
			layoutTemplate = layoutTemplateClass.getConstructor(GLBrick.class, BrickColumn.class, GLStratomex.class)
					.newInstance(brick, this, stratomex);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {

			e.printStackTrace();
		}
		brick.setBrickLayoutTemplate(layoutTemplate);

		brick.initialize();

		uninitializedBricks.add(brick);

		ViewLayoutRenderer brickRenderer = new ViewLayoutRenderer(brick);
		wrappingLayout.setRenderer(brickRenderer);
		wrappingLayout.setPixelSizeX(0);
		wrappingLayout.setPixelSizeY(0);

		// if (isCollapsed)
		// {
		// wrappingLayout.setPixelSizeX(visBricks.getSideArchWidthPixels());
		// }
		// else
		// {
		// wrappingLayout.setPixelSizeX(minPixelWidth);
		// }

		return brick;
	}

	/**
	 * Destroys all sub-bricks
	 */
	private void destroyOldBricks() {
		for (GLBrick brick : clusterBricks) {
			// GeneralManager.get().getViewManager().unregisterGLView(brick);
			ViewManager.get().destroyView(getParentGLCanvas().asGLAutoDrawAble().getGL().getGL2(), brick);
			// brick.unregisterEventListeners();
		}
		clusterBricks.clear();
	}

	/**
	 * <p>
	 * Set the spacing of the arch in pixel.
	 * </p>
	 * <p>
	 * This is only used if the group is not collapsed. If it is collapsed, the values are irrelevant.
	 * </p>
	 *
	 * @param archHeight
	 *            the pixel height of the arch
	 */
	public void setArchHeight(int archHeight) {
		if (headerBrick != null) {
			headerBrick.setStaticBrickHeight(archHeight);
			headerBrick.setBrickHeigthMode(EBrickHeightMode.STATIC);
		}
	}

	@Override
	public void registerEventListeners() {

		recordVAUpdateListener = new RecordVAUpdateListener();
		recordVAUpdateListener.setHandler(this);
		eventPublisher.addListener(RecordVAUpdateEvent.class, recordVAUpdateListener);

		layoutSizeCollisionListener = new LayoutSizeCollisionListener();
		layoutSizeCollisionListener.setHandler(this);
		eventPublisher.addListener(LayoutSizeCollisionEvent.class, layoutSizeCollisionListener);

		eventListeners.register(this);

	}

	@Override
	public void unregisterEventListeners() {
		if (recordVAUpdateListener != null) {
			eventPublisher.removeListener(recordVAUpdateListener);
			recordVAUpdateListener = null;
		}

		if (layoutSizeCollisionListener != null) {
			eventPublisher.removeListener(layoutSizeCollisionListener);
			layoutSizeCollisionListener = null;
		}
		eventListeners.unregister(this);
	}

	/**
	 * This is called when a clustering was run, so we replace the sub-bricks
	 */
	@Override
	public void handleRecordVAUpdate(String recordPerspectiveID) {

		if (!tablePerspective.getRecordPerspective().getPerspectiveID().equals(recordPerspectiveID))
			return;
		reactOnGroupingChanges();

	}

	/**
	 * Replaces the local table perspective with the one provided. This should only be called through
	 * {@link GLStratomex#replaceTablePerspective(TablePerspective)}
	 *
	 * @param tablePerspective
	 */
	public void replaceTablePerspective(TablePerspective tablePerspective) {
		this.tablePerspective = tablePerspective;
		headerBrickLayout.clear();
		ViewManager.get().destroyView(getParentGLCanvas().asGLAutoDrawAble().getGL().getGL2(), headerBrick);
		headerBrick = null;

		clusterBrickColumn.clear();
		createBricks();

		stratomex.setLayoutDirty();
	}

	private void reactOnGroupingChanges() {

		clusterBrickColumn.clear();
		clusterBricks.clear();
		createClusterBricks();
		stratomex.setLayoutDirty();
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		return null;
	}

	@Override
	public void init(GL2 gl) {
		textRenderer = new CaleydoTextRenderer(24);
	}

	@Override
	protected void initLocal(GL2 gl) {
	}

	@Override
	public void initRemote(GL2 gl, AGLView glParentView, GLMouseListener glMouseListener) {
		createBricks();
		init(gl);
	}

	@Override
	public void display(GL2 gl) {

		if (showDetailBrick) {

			mainRow.clear();
			if (expandLeft) {
				mainRow.append(detailBrickLayout);
				mainRow.append(overviewDetailGapLayout);
				mainRow.append(mainColumn);
				stratomex.switchToDetailModeLeft(this);

			} else {
				mainRow.append(mainColumn);
				mainRow.append(overviewDetailGapLayout);
				mainRow.append(detailBrickLayout);
				stratomex.switchToDetailModeRight(this);
			}

			mainRow.updateSubLayout();
			// visBricks.setLastResizeDirectionWasToLeft(false);
			stratomex.setLayoutDirty();
			stratomex.updateConnectionLinesBetweenColumns();
			showDetailBrick = false;
			isDetailBrickShown = true;
		}

		if (hideDetailBrick || (isCollapsed && detailBrick != null)) {
			mainRow.clear();
			mainRow.append(mainColumn);
			if (detailBrick != null) {
				// GeneralManager.get().getViewManager().unregisterGLView(detailBrick);
				ViewManager.get().destroyView(getParentGLCanvas().asGLAutoDrawAble().getGL().getGL2(), detailBrick);
				// detailBrick.unregisterEventListeners();
				detailBrick = null;
			}

			isDetailBrickShown = false;

			if (hideDetailBrick && expandLeft) {
				stratomex.switchToOverviewModeLeft();
			}
			if (hideDetailBrick && !expandLeft) {
				stratomex.switchToOverviewModeRight();
			}

			hideDetailBrick = false;

			mainRow.updateSubLayout();
			// visBricks.setLastResizeDirectionWasToLeft(false);
			stratomex.setLayoutDirty();
			stratomex.updateConnectionLinesBetweenColumns();
		}

		while (!uninitializedBricks.isEmpty()) {
			uninitializedBricks.poll().initRemote(gl, this, glMouseListener);
			stratomex.setLayoutDirty();
			stratomex.updateConnectionLinesBetweenColumns();
		}
		handleVerticalMoveDragging(gl);
		checkForHits(gl);
	}

	@Override
	protected void displayLocal(GL2 gl) {
		// TODO Auto-generated method stub

	}

	@Override
	public void displayRemote(GL2 gl) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDraggingStartPoint(float mouseCoordinateX, float mouseCoordinateY) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleDragging(GL2 gl, final float mouseCoordinateX, final float mouseCoordinateY) {

		gl.glColor4f(0, 0, 0, 0.5f);
		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex3f(mouseCoordinateX, mouseCoordinateY, 1);
		gl.glVertex3f(mouseCoordinateX + mainColumn.getSizeScaledX(), mouseCoordinateY, 1);
		gl.glVertex3f(mouseCoordinateX + mainColumn.getSizeScaledX(), mouseCoordinateY - mainColumn.getSizeScaledY(), 1);
		gl.glVertex3f(mouseCoordinateX, mouseCoordinateY - mainColumn.getSizeScaledY(), 1);
		gl.glEnd();

	}

	@Override
	public void handleDrop(GL2 gl, float mouseCoordinateX, float mouseCoordinateY) {

		System.out.println("handle drop");
	}

	/**
	 * Handles the up-down dragging of the whole dimension group
	 *
	 * @param gl
	 */
	private void handleVerticalMoveDragging(GL2 gl) {
		if (!isVerticalMoveDraggingActive)
			return;
		if (glMouseListener.wasMouseReleased()) {
			isVerticalMoveDraggingActive = false;
			previousYCoordinate = Float.NaN;
			return;
		}

		Vec2f pointCordinates = pixelGLConverter.convertMouseCoord2GL(glMouseListener.getDIPPickedPoint());

		if (Float.isNaN(previousYCoordinate)) {
			previousYCoordinate = pointCordinates.y();
			return;
		}

		float change = pointCordinates.y() - previousYCoordinate;
		previousYCoordinate = pointCordinates.y();

		// updateBrickSizes(topCol, topBricks, newSize);

		float bottomSize = clusterBrickColumn.getSizeScaledY();
		clusterBrickColumn.setAbsoluteSizeY(bottomSize + change);
		// float centerSize = headerBrickLayout.getSizeScaledY();
		//
		// headerBrickLayout.setAbsoluteSizeY(centerSize);

		// headerBrickLayout.updateSubLayout();
		mainRow.updateSubLayout();
		// groupColumn.updateSubLayout();
		stratomex.updateConnectionLinesBetweenColumns();

	}

	/**
	 * Updates the layout of this dimensionGroup
	 */
	public void updateLayout() {

		mainRow.updateSubLayout();
		stratomex.updateConnectionLinesBetweenColumns();

		for (GLBrick clusterBrick : clusterBricks) {
			clusterBrick.updateLayout();
		}
	}

	/**
	 * Switch all bricks to the specified view type
	 *
	 * @param viewType
	 */
	public void switchBrickViews(int globalRendererID) {

		for (GLBrick brick : clusterBricks) {
			brick.setRenderer(brick.getLocalRendererID(globalRendererID));
		}
		// if (detailBrick != null) {
		// detailBrick.updateBrickSizeAccordingToRenderer(viewType);
		// }
		// centerBrick.setRemoteView(viewType);
		mainRow.updateSubLayout();
		// groupColumn.updateSubLayout();
	}

	/**
	 * @return GL minimum width of a dimension group.
	 */
	public float getMinWidth() {
		return pixelGLConverter.getGLWidthForPixelWidth(minPixelWidth);
	}

	/**
	 * Note: The vis bricks view is needed for pushing the picking names, so that the GLVisBricks view can gets the
	 * events
	 *
	 */
	public void setStratomex(GLStratomex stratomex) {
		this.stratomex = stratomex;
	}

	/**
	 * Note: The vis bricks view is needed for pushing the picking names, so that the GLVisBricks view can gets the
	 * events
	 *
	 */
	public GLStratomex getStratomexView() {
		return stratomex;
	}

	/**
	 * Get the id of the set that this dimension group is showing
	 *
	 * @return
	 */
	public int getTableID() {
		return tablePerspective.getID();
	}

	/**
	 * Returns the list of bricks ordered from bottom to top as it is rendered in this dimension group
	 *
	 * @return
	 */
	public List<GLBrick> getSegmentBricks() {
		ArrayList<GLBrick> bricks = new ArrayList<GLBrick>();

		for (int i = clusterBricks.size() - 1; i >= 0; i--) {
			bricks.add(clusterBricks.get(i));

		}

		// for (int i = topBricks.size() - 1; i >= 0; i--) {
		// bricks.add(topBricks.get(i));
		//
		// }

		// for (GLBrick brick : bottomBricks) {
		// bricks.add(brick);
		// }
		//
		//

		return bricks;
	}

	public List<GLBrick> getBricksForRelations() {
		if (isDetailBrickShown) {
			ArrayList<GLBrick> bricks = new ArrayList<GLBrick>();
			bricks.add(detailBrick);
			return bricks;
		}
		return getSegmentBricks();
	}

	public boolean isDetailBrickShown() {
		return isDetailBrickShown;
	}

	/**
	 * @return True, if the detail brick is expanded at the left or not.
	 */
	public boolean isExpandLeft() {
		return expandLeft;
	}

	/**
	 * Returns the center brick that shows the summary of the dimension group data.
	 *
	 * @return
	 */
	public GLBrick getHeaderBrick() {
		return headerBrick;
	}

	@Override
	public Row getLayout() {
		return mainRow;
	}

	@Override
	public void handleLayoutSizeCollision(int managingClassID, int layoutID, float toBigBy) {
		// if (managingClassID != uniqueID)
		// return;
		//
		// System.out.println("handling layout collision");
		// // if (externalID == TOP_COLUMN_ID) {
		// // boolean changeMade = false;
		// // for (int count = topBricks.size() - 1; count >= 0; count--) {
		// // GLBrick brick = topBricks.get(count);
		// // if (toBigBy < 0)
		// // break;
		// // if (!brick.isInOverviewMode() && !brick.isSizeFixed()) {
		// // // toBigBy -= brick.collapse();
		// // // changeMade = true;
		// // }
		// // }
		// // if (changeMade)
		// // topCol.updateSubLayout();
		// // }
		// if (externalID == BOTTOM_COLUMN_ID) {
		// boolean changeMade = false;
		//
		// for (int count = clusterBricks.size() - 1; count >= 0; count--) {
		// GLBrick brick = clusterBricks.get(count);
		// if (toBigBy < 0)
		// break;
		// if (!brick.isInOverviewMode() && !brick.isSizeFixed()) {
		// // toBigBy -= brick.collapse();
		// // changeMade = true;
		// }
		// }
		// if (changeMade)
		// clusterBrickColumn.updateSubLayout();
		// }
	}

	/**
	 * Sets whether the views of all bricks of this dimension groups shall be switched when switching the view in a
	 * single brick.
	 *
	 * @param isGlobalViewSwitching
	 */
	public void setGlobalViewSwitching(boolean isGlobalViewSwitching) {
		this.isGlobalViewSwitching = isGlobalViewSwitching;

		for (GLBrick brick : clusterBricks) {
			brick.setGlobalViewSwitching(isGlobalViewSwitching);
		}
		if (detailBrick != null) {
			detailBrick.setGlobalViewSwitching(isGlobalViewSwitching);
		}
	}

	/**
	 * @return True, if the views of all bricks of this dimension groups shall be switched when switching the view in a
	 *         single brick.
	 */
	public boolean isGlobalViewSwitching() {
		return isGlobalViewSwitching;
	}

	// /**
	// * Called to hide the handles of all bricks of this dimension group.
	// */
	// public void hideHandles() {
	//
	// centerBrick.hideHandles();
	// for (GLBrick brick : bottomBricks) {
	// brick.hideHandles();
	// }
	// }

	/**
	 * Shows a detailed brick.
	 *
	 * @param brick
	 *            The brick for which a detailed version shall be shown.
	 * @param expandLeft
	 *            Specifies, whether the detail brick shall be expanded on the left or on the right.
	 */
	public void showDetailedBrick(GLBrick brick, boolean expandLeft) {

		if (detailBrick != null) {
			// GeneralManager.get().getViewManager().unregisterGLView(detailBrick);
			ViewManager.get().destroyView(getParentGLCanvas().asGLAutoDrawAble().getGL().getGL2(), detailBrick);
			// detailBrick.unregisterEventListeners();
			detailBrick = null;
			// if we previously had an open detail brick we close it now
			if (this.expandLeft != expandLeft) {
				if (this.isDetailBrickShown && this.expandLeft)
					stratomex.switchToOverviewModeLeft();
				else if (this.isDetailBrickShown && !this.expandLeft)
					stratomex.switchToOverviewModeRight();
			}
		}

		detailBrickLayout = new Column("detailBrickWrappingLayout");

		detailBrick = createBrick(detailBrickLayout, brick.getTablePerspective(), brick.isHeaderBrick(),
				DetailBrickLayoutTemplate.class);
		// detailBrick.setBrickData(brick.getBrickData());
		// detailBrick.setBrickConfigurer(brick.getBrickConfigurer());
		// detailBrick.setRecordVA(brick.getGroup(), brick.getRecordVA());

		detailBrick.setBrickHeigthMode(EBrickHeightMode.STATIC);
		detailBrick.setBrickWidthMode(EBrickWidthMode.STATIC);
		int detailBrickWidth = getDetailBrickWidthPixels(!expandLeft);
		detailBrick.setStaticBrickWidth(detailBrickWidth);
		detailBrick.setStaticBrickHeight(getDetailBrickHeightPixels());

		// detailBrick.setBrickLayoutTemplate(
		// new DetailBrickLayoutTemplate(detailBrick, this, stratomex, detailBrick.getBrickConfigurer()));
		int globalRendererID = brick.getGlobalRendererID(brick.getActiveRendererID());
		detailBrick.setRenderer(detailBrick.getLocalRendererID(globalRendererID));

		overviewDetailGapLayout = new ElementLayout("brickSpacingLayout");
		overviewDetailGapLayout.setPixelSizeX(OVERVIEW_DETAIL_GAP_PIXEL);
		overviewDetailGapLayout.setRatioSizeY(1);

		if (expandLeft) {
			overviewDetailGapLayout.setRenderer(new OverviewDetailBandRenderer(detailBrick, brick, false));
		} else {
			overviewDetailGapLayout.setRenderer(new OverviewDetailBandRenderer(brick, detailBrick, true));
		}

		BrickColumn otherDetailDimensionGroup = getOtherDetailDimensionGroup(!expandLeft);

		if (otherDetailDimensionGroup != null && otherDetailDimensionGroup.isDetailBrickShown()) {
			otherDetailDimensionGroup.setDetailBrickWidth(detailBrickWidth);
		}

		showDetailBrick = true;
		this.expandLeft = expandLeft;
		// brick.hideHandles();
	}

	/**
	 * @param detailBrickWidth
	 *            Pixel width of the detail brick.
	 */
	public void setDetailBrickWidth(int detailBrickWidth) {
		detailBrickLayout.setPixelSizeX(detailBrickWidth);
		if (detailBrick != null)
			detailBrick.setStaticBrickWidth(detailBrickWidth);
		showDetailBrick = true;
	}

	/**
	 * Hides the detail brick.
	 */
	public void hideDetailedBrick() {
		isDetailBrickShown = false;
		hideDetailBrick = true;
		BrickColumn otherDetailDimensionGroup = getOtherDetailDimensionGroup(isLeftmost());
		if (otherDetailDimensionGroup != null && otherDetailDimensionGroup.isDetailBrickShown()) {
			otherDetailDimensionGroup.setDetailBrickWidth(otherDetailDimensionGroup
					.getDetailBrickWidthPixels(otherDetailDimensionGroup.isLeftmost()));
		}
	}

	public int getDetailBrickHeightPixels() {
		return (int) (parentGLCanvas.getDIPHeight() * 0.9f);
	}

	/**
	 * @param isCurrentDimensionGroupLeft
	 *            Specifies, whether the dimension group is on the left side. (When a detail brick is shown, only two
	 *            dimension groups are visible)
	 * @return Width of the detail brick
	 */
	public int getDetailBrickWidthPixels(boolean isCurrentDimensionGroupLeft) {

		BrickColumn otherDimensionGroup = getOtherDetailDimensionGroup(isCurrentDimensionGroupLeft);
		int otherDimensionGroupColumnWidth = 0;
		boolean otherDimensionGroupShowsDetail = false;
		if (otherDimensionGroup != null) {
			otherDimensionGroupShowsDetail = otherDimensionGroup.isDetailBrickShown();
			otherDimensionGroupColumnWidth = otherDimensionGroup.getGroupColumnWidthPixels();
		}
		float detailAreaWidth = parentGLCanvas.getDIPWidth() - 2 * OVERVIEW_DETAIL_GAP_PIXEL - 2
				* GLStratomex.BRICK_COLUMN_SIDE_SPACING - getGroupColumnWidthPixels() - otherDimensionGroupColumnWidth;
		int detailGapWidth = (int) (DETAIL_GAP_PORTION * detailAreaWidth);
		detailGapWidth = (detailGapWidth < MIN_DETAIL_GAP_PIXEL) ? MIN_DETAIL_GAP_PIXEL : detailGapWidth;

		float detailWidth = (otherDimensionGroupShowsDetail) ? (int) ((detailAreaWidth - detailGapWidth) / 2.0f)
				: detailAreaWidth;

		return (int) detailWidth;
	}

	/**
	 * Returns the neighboring {@link BrickColumn}, either the one to the right, if the parameter is true, or the one to
	 * the left, if the parameter is false.
	 * <p>
	 * Returns null there is no dimension group on the specified side
	 *
	 * @param isCurrentDimensionGroupLeft
	 *            Specifies, whether the dimension group is on the left side. (When a detail brick is shown, only two
	 *            dimension groups are visible)
	 * @return The other dimension group that is currently visible in the detail mode, or null if there is no other
	 *         group
	 */
	private BrickColumn getOtherDetailDimensionGroup(boolean isCurrentDimensionGroupLeft) {

		BrickColumnManager dimensionGroupManager = stratomex.getBrickColumnManager();

		ArrayList<BrickColumn> dimensionGroups = dimensionGroupManager.getBrickColumns();
		int dimensionGroupIndex = dimensionGroups.indexOf(this);

		if (isCurrentDimensionGroupLeft) {

			if (dimensionGroups.size() <= dimensionGroupIndex + 1) {
				// there is no dimension group further left
				return null;
			}
			return dimensionGroups.get(dimensionGroupIndex + 1);
		}

		if (dimensionGroupIndex - 1 < 0) {
			// there is no dimension group further right
			return null;
		}
		return dimensionGroups.get(dimensionGroupIndex - 1);

	}

	/**
	 * @return True if this dimension group is the leftmost dimension group.
	 */
	public boolean isLeftmost() {
		BrickColumnManager dimensionGroupManager = stratomex.getBrickColumnManager();
		int index = dimensionGroupManager.indexOfBrickColumn(this);
		return (index == dimensionGroupManager.getCenterColumnStartIndex());
	}

	/**
	 * @return True if this dimension group is the rightmost dimension group.
	 */
	public boolean isRightmost() {
		BrickColumnManager dimensionGroupManager = stratomex.getBrickColumnManager();
		int index = dimensionGroupManager.indexOfBrickColumn(this);
		return (index == dimensionGroupManager.getRightColumnStartIndex() - 1);
	}

	public int getGroupColumnWidthPixels() {
		return pixelGLConverter.getPixelWidthForGLWidth(mainColumn.getSizeScaledX());
	}

	/**
	 * @return Column of bricks.
	 */
	public Column getGroupColumn() {
		return mainColumn;
	}


	/**
	 * @param isVerticalMoveDraggingActive
	 *            setter, see {@link #isVerticalMoveDraggingActive}
	 */
	public void setVerticalMoveDraggingActive(boolean isVerticalMoveDraggingActive) {
		this.isVerticalMoveDraggingActive = isVerticalMoveDraggingActive;
	}

	/**
	 * Returns the proportional height a record should have in this dimension group as pixels. Can be in sub-pixel space
	 * and therefore returns a double.
	 *
	 * @return
	 */
	public double getProportionalHeightPerRecord() {
		int brickHeightOverhead = 0;
		for (GLBrick brick : clusterBricks) {
			brickHeightOverhead += brick.getHeightOverheadOfProportioanlBrick();
		}

		double useablePixelHeight = pixelGLConverter.getPixelHeightForGLHeight(clusterBrickColumn.getSizeScaledY())
				- (clusterBricks.size() + 1) * BETWEEN_BRICKS_SPACING - DefaultBrickLayoutTemplate.BUTTON_HEIGHT_PIXELS
				- brickHeightOverhead;
		double proportionalRecordHeight = useablePixelHeight / tablePerspective.getNrRecords();
		return proportionalRecordHeight;
	}

	public boolean isCollapsed() {
		return isCollapsed;
	}

	@Override
	public List<AGLView> getRemoteRenderedViews() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void destroyViewSpecificContent(GL2 gl) {
		if (mainRow != null)
			mainRow.destroy(gl);
		this.clusterBricks.clear();
		this.detailBrick = null;
	}

	@ListenTo(sendToMe = true)
	private void onSelectDimensionSelection(SelectDimensionSelectionEvent event) {
		Perspective current = getTablePerspective().getDimensionPerspective();
		Perspective selected = event.getDim();
		if (selected == current)
			return;
		TablePerspective new_ = getDataDomain().getTablePerspective(
				getTablePerspective().getRecordPerspective().getPerspectiveID(), selected.getPerspectiveID());
		getStratomexView().replaceTablePerspective(new_, getTablePerspective());
	}

	/**
	 * @param fs
	 */
	public void setHighlightColor(Color color) {
		if (color == REVERT_COLOR)
			color = isActive() ? SelectionType.SELECTION.getColor() : null;
		FrameHighlightRenderer renderer = Iterables.getFirst(
				Iterables.filter(getLayout().getBackgroundRenderer(), FrameHighlightRenderer.class), null);
		if (renderer != null)
			renderer.setColor(color);
		return;
	}

	/**
	 *
	 */
	public void destroyView() {
		GL2 gl = getParentGLCanvas().asGLAutoDrawAble().getGL().getGL2();
		ViewManager.get().destroyView(gl, this);
		// destroyOldBricks();
	}
}
