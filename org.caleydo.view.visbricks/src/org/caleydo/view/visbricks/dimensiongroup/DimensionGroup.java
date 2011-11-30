package org.caleydo.view.visbricks.dimensiongroup;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import javax.management.InvalidAttributeValueException;
import javax.media.opengl.GL2;
import javax.media.opengl.awt.GLCanvas;
import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.selection.ElementConnectionInformation;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.events.RecordVAUpdateEvent;
import org.caleydo.core.data.virtualarray.events.RecordVAUpdateListener;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.ATableBasedView;
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
import org.caleydo.core.view.opengl.util.GLCoordinateUtils;
import org.caleydo.core.view.opengl.util.draganddrop.IDraggable;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.view.visbricks.GLVisBricks;
import org.caleydo.view.visbricks.brick.EContainedViewType;
import org.caleydo.view.visbricks.brick.GLBrick;
import org.caleydo.view.visbricks.brick.GLBrick.EBrickHeightMode;
import org.caleydo.view.visbricks.brick.GLBrick.EBrickWidthMode;
import org.caleydo.view.visbricks.brick.layout.ABrickLayoutConfiguration;
import org.caleydo.view.visbricks.brick.layout.CompactHeaderBrickLayoutTemplate;
import org.caleydo.view.visbricks.brick.layout.DefaultBrickLayoutTemplate;
import org.caleydo.view.visbricks.brick.layout.DetailBrickLayoutTemplate;
import org.caleydo.view.visbricks.brick.layout.HeaderBrickLayoutTemplate;
import org.caleydo.view.visbricks.brick.layout.IBrickConfigurer;
import org.caleydo.view.visbricks.brick.ui.OverviewDetailBandRenderer;
import org.eclipse.swt.widgets.Composite;

/**
 * Container for a group of dimensions. Manages layouts as well as brick views
 * for the whole dimension group.
 * 
 * @author Alexander Lex
 * 
 */
public class DimensionGroup extends ATableBasedView implements
		ILayoutSizeCollisionHandler, ILayoutedElement, IDraggable {

	public final static String VIEW_TYPE = "org.caleydo.view.dimensiongroup";

	public final static int PIXEL_PER_DIMENSION = 30;
	public final static int MIN_BRICK_WIDTH_PIXEL = 170;
	public final static int OVERVIEW_DETAIL_GAP_PIXEL = 30;
	public final static int MIN_DETAIL_GAP_PIXEL = 10;
	public final static float DETAIL_GAP_PORTION = 0.05f;
	public final static int BETWEEN_BRICKS_SPACING = 10;

	/**
	 * The brick at the top that shows information about and provides tools for
	 * interaction with the whole dimension group
	 */
	private GLBrick headerBrick;

	/** The bricks that show the grouping data */
	private ArrayList<GLBrick> clusterBricks;

	/**
	 * The large detail replicate brick
	 */
	private GLBrick detailBrick;

	/**
	 * The main layout that contains all other layouts. In detail mode, it
	 * contains the detail brick and the group Column, else it contains only the
	 * group column
	 */
	private Row mainRow;

	/**
	 * Contains the dimension group including the header brick and the cluster
	 * bricks. A child of {@link #mainRow}.
	 */
	private Column mainColumn;

	/** The layout for the header brick. Child of {@link #mainColumn} */
	private Column headerBrickLayout;

	/**
	 * The column containing the cluster bricks, a child of
	 * {@link #clusterBrickWrapperColumn}
	 */
	protected Column clusterBrickColumn;

	/**
	 * Wrapper for {@link #clusterBrickColumn} that makes it possible that the
	 * clusterBrickColumn goes below the {@link #headerBrickLayout}. Child of
	 * {@link #mainColumn}.
	 */
	protected Column clusterBrickWrapperColumn;

	/** The layout for the detail brick. Child of {@link #mainRow} */
	private Column detailBrickLayout;

	/**
	 * Flag telling whether all cluster bricks are synchronized when switching
	 * views (true), or if every cluster brick may show whatever view it likes.
	 */
	private boolean isGlobalViewSwitching = false;

	// private ViewFrustum brickFrustum;
	// protected DataTable set;

	private EventPublisher eventPublisher = GeneralManager.get().getEventPublisher();
	private LayoutSizeCollisionListener layoutSizeCollisionListener;

	/**
	 * Flag telling whether the DimensionGroup is collapsed (the cluster bricks
	 * are hidden) or not. Collapsed is used for storing the DimensionGroup in
	 * the context area
	 */
	private boolean isCollapsed = false;

	private Queue<GLBrick> uninitializedBricks = new LinkedList<GLBrick>();

	protected GLVisBricks visBricks;

	// Stuff for dragging up and down
	private boolean isVerticalMoveDraggingActive = false;

	private float previousYCoordinate = Float.NaN;

	/** The height of the header brick in pixel */
	private int headerBrickPixelHeight = 80;

	/** the minimal width of the brick */
	private int minPixelWidth;

	private boolean showDetailBrick = false;
	private boolean hideDetailBrick = false;
	private boolean isDetailBrickShown = false;
	private boolean expandLeft = false;

	private ElementLayout overviewDetailGapLayout;

	public static int BOTTOM_COLUMN_ID = 0;
	public static int TOP_COLUMN_ID = 1;

	IBrickConfigurer brickConfigurer;

	public DimensionGroup(GLCanvas canvas, Composite parentComposite,
			ViewFrustum viewFrustum) {
		super(canvas, parentComposite, viewFrustum);

		viewType = VIEW_TYPE;

		mainRow = new Row("mainRow");
		mainRow.setRenderingPriority(1);
		mainRow.setXDynamic(true);
		mainRow.setFrameColor(1, 0, 1, 1);
		mainRow.sethAlign(HAlign.CENTER);

		mainColumn = new Column("mainColumn");
		mainColumn.setPriorityRendereing(true);
		mainColumn.setBottomUp(false);
		mainColumn.setXDynamic(true);
		mainColumn.setVAlign(VAlign.CENTER);

		clusterBrickWrapperColumn = new Column("wrapperColumn");
		clusterBrickWrapperColumn.setXDynamic(true);

		clusterBrickColumn = new Column("clusterBrickColumn");
		clusterBrickColumn.setFrameColor(1, 0, 1, 1);
		clusterBrickColumn.setBottomUp(false);
		clusterBrickColumn.setXDynamic(true);
		clusterBrickColumn.setIDs(uniqueID, BOTTOM_COLUMN_ID);
		clusterBrickColumn.setVAlign(VAlign.CENTER);

		clusterBrickWrapperColumn.append(clusterBrickColumn);

		clusterBricks = new ArrayList<GLBrick>(20);

		headerBrickLayout = new Column("headerBrickLayout");
		headerBrickLayout.setXDynamic(true);
		headerBrickLayout.setYDynamic(true);
		// headerBrickLayout.setDebug(true);
		headerBrickLayout.setFrameColor(1, 1, 0, 1);
		headerBrickLayout.setRenderingPriority(10);
		// headerBrickLayout.setPixelSizeY(60);

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
	 * Initializes the main column with either only the headerBrick, when the
	 * dimensionGroup is collapsed, or the headerBrick and the clusterBricks.
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
	 * Set this dimension group collapsed, i.e. only it's overview and caption
	 * is rendered and no other bricks
	 */
	public void setCollapsed(boolean isCollapsed) {
		this.isCollapsed = isCollapsed;
		// centerBrick.setBrickLayoutTemplate(new
		// CompactBrickLayoutTemplate(centerBrick,
		// glVisBricksView, this));
		headerBrick.setStaticBrickHeight(visBricks.getArchHeight());
		if (headerBrick == null || uninitializedBricks.contains(headerBrick))
			return;

		if (isCollapsed) {
			headerBrick.setBrickHeigthMode(EBrickHeightMode.VIEW_DEPENDENT);
			headerBrick.collapse();
		} else {
			headerBrick.setBrickHeigthMode(EBrickHeightMode.STATIC);
			headerBrick.expand();
		}
		initMainColumn();
		// groupColumn.updateSubLayout();
	}

	/**
	 * Creates all bricks of the dimension group
	 */
	protected void createBricks() {
		// create basic layouts

		// minPixelWidth = PIXEL_PER_DIMENSION * table.size();
		// if (minPixelWidth < MIN_BRICK_WIDTH_PIXEL)
		// minPixelWidth = MIN_BRICK_WIDTH_PIXEL;
		// minWidth = pixelGLConverter.getGLWidthForPixelWidth(minPixelWidth);

		mainColumn.addBackgroundRenderer(new DimensionGroupBackgroundColorRenderer(
				dataContainer.getDataDomain().getColor().getRGBA()));

		ElementLayout headerBrickLayout2 = new ElementLayout();

		ElementLayout brickSpacingLayout = new ElementLayout("brickSpacingLayout");
		brickSpacingLayout.setPixelSizeY(BETWEEN_BRICKS_SPACING);
		brickSpacingLayout.setRatioSizeX(0);
		headerBrickLayout.append(headerBrickLayout2);
		headerBrickLayout.append(brickSpacingLayout);

		headerBrick = createBrick(headerBrickLayout2, dataContainer);
		headerBrick.setHeaderBrick(true);

		ABrickLayoutConfiguration layoutTemplate;

		if (isCollapsed) {
			layoutTemplate = new CompactHeaderBrickLayoutTemplate(headerBrick, this,
					visBricks, headerBrick.getBrickConfigurer());
		} else {
			layoutTemplate = new HeaderBrickLayoutTemplate(headerBrick, this, visBricks,
					headerBrick.getBrickConfigurer());
		}
		headerBrick.setBrickLayoutTemplate(layoutTemplate,
				layoutTemplate.getDefaultViewType());

		creatClusterBricks();
	}

	/**
	 * Creates all bricks except for the center brick based on the groupList in
	 * the recordVA
	 */
	private void creatClusterBricks() {

		destroyOldBricks();

		List<DataContainer> brickDataContainers = dataContainer
				.getRecordSubDataContainers();

		if (brickDataContainers == null || brickDataContainers.size() <= 0)
			return;

		Set<GLBrick> segmentBricks = new HashSet<GLBrick>();

		for (DataContainer brickData : brickDataContainers) {
			GLBrick segmentBrick = createBrick(new ElementLayout("brick"), brickData);

			// segmentBrick.setBrickConfigurer(dimensionGroupData.getBrickConfigurer());

			ABrickLayoutConfiguration layoutTemplate = new DefaultBrickLayoutTemplate(
					segmentBrick, visBricks, this, segmentBrick.getBrickConfigurer());

			segmentBrick.setBrickLayoutTemplate(layoutTemplate,
					layoutTemplate.getDefaultViewType());

			segmentBricks.add(segmentBrick);
		}

		ArrayList<GLBrick> sortedBricks = brickConfigurer.getBrickSortingStrategy()
				.getSortedBricks(segmentBricks);

		for (GLBrick brick : sortedBricks) {
			// System.out.println("Average Value: "
			// +
			// brick.getDataContainer().getContainerStatistics().getAverageValue());
			clusterBricks.add(brick);
			clusterBrickColumn.append(brick.getLayout());
		}

		ElementLayout brickSpacingLayout = new ElementLayout("brickSpacingLayout");
		brickSpacingLayout.setPixelSizeY(BETWEEN_BRICKS_SPACING);
		brickSpacingLayout.setRatioSizeX(0);

		for (int count = 0; count < clusterBrickColumn.size();) {

			clusterBrickColumn.add(count, brickSpacingLayout);
			count++;
			count++;
		}

		visBricks.getRelationAnalyzer().updateRelations(
				dataContainer.getRecordPerspective().getID(),
				dataContainer.getRecordPerspective().getVirtualArray());

	}

	/**
	 * Creates a single brick
	 * 
	 * @param wrappingLayout
	 * @return
	 */
	private GLBrick createBrick(ElementLayout wrappingLayout, DataContainer dataContainer) {
		ViewFrustum brickFrustum = new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC, 0,
				0, 0, 0, -4, 4);
		GLBrick brick = (GLBrick) GeneralManager
				.get()
				.getViewManager()
				.createGLView(GLBrick.class, parentGLCanvas, parentComposite,
						brickFrustum);

		brick.setDataDomain(dataDomain);
		brick.setDataContainer(dataContainer);
		brick.setBrickConfigurer(brickConfigurer);
		brick.setRemoteRenderingGLView(getRemoteRenderingGLView());

		// brick.setTable(set);
		brick.setVisBricks(visBricks);
		brick.setLayout(wrappingLayout);
		brick.setDimensionGroup(this);
		brick.initialize();
		uninitializedBricks.add(brick);

		ViewLayoutRenderer brickRenderer = new ViewLayoutRenderer(brick);
		wrappingLayout.setRenderer(brickRenderer);
		if (isCollapsed) {
			wrappingLayout.setPixelSizeX(visBricks.getSideArchWidthPixels());
		} else {
			wrappingLayout.setPixelSizeX(minPixelWidth);
		}

		return brick;
	}

	/**
	 * Destroys all sub-bricks
	 */
	private void destroyOldBricks() {
		for (GLBrick brick : clusterBricks) {
			GeneralManager.get().getViewManager().unregisterGLView(brick);
			brick.unregisterEventListeners();
			brick.destroy();
		}
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		unregisterEventListeners();
	}

	/**
	 * <p>
	 * Set the spacing of the arch in pixel.
	 * </p>
	 * <p>
	 * This is only used if the group is not collapsed. If it is collapsed, the
	 * values are irrelevant.
	 * </p>
	 * 
	 * @param archHeight
	 *            the pixel height of the arch
	 */
	public void setArchHeight(int archHeight) {
		headerBrick.setStaticBrickHeight(archHeight);
	}

	@Override
	public void registerEventListeners() {

		recordVAUpdateListener = new RecordVAUpdateListener();
		recordVAUpdateListener.setHandler(this);
		recordVAUpdateListener.setExclusiveDataDomainID(dataDomain.getDataDomainID());
		eventPublisher.addListener(RecordVAUpdateEvent.class, recordVAUpdateListener);

		layoutSizeCollisionListener = new LayoutSizeCollisionListener();
		layoutSizeCollisionListener.setHandler(this);
		eventPublisher.addListener(LayoutSizeCollisionEvent.class,
				layoutSizeCollisionListener);
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
	}

	/**
	 * This is called when a clustering was run, so we replace the sub-bricks
	 */
	@Override
	public void handleRecordVAUpdate(String recordPerspectiveID) {

		if (!dataContainer.getRecordPerspective().getID().equals(recordPerspectiveID))
			return;

		clusterBrickColumn.clear();
		clusterBricks.clear();
		creatClusterBricks();
		mainRow.updateSubLayout();
		// groupColumn.updateSubLayout();
		visBricks.updateConnectionLinesBetweenDimensionGroups();

		// visBricks.getRelationAnalyzer().updateRelations(
		// dimensionGroupData.getID(),
		// dimensionGroupData.getSummaryBrickVA());

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
		// createBricks(table.getContentData(Set.CONTENT).getRecordVA());
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
				visBricks.switchToDetailModeLeft(this);

			} else {
				mainRow.append(mainColumn);
				mainRow.append(overviewDetailGapLayout);
				mainRow.append(detailBrickLayout);
				visBricks.switchToDetailModeRight(this);
			}

			mainRow.updateSubLayout();
			// visBricks.setLastResizeDirectionWasToLeft(false);
			visBricks.updateLayout();
			visBricks.updateConnectionLinesBetweenDimensionGroups();
			showDetailBrick = false;
			isDetailBrickShown = true;
		}

		if (hideDetailBrick || (isCollapsed && detailBrick != null)) {
			mainRow.clear();
			mainRow.append(mainColumn);
			if (detailBrick != null) {
				GeneralManager.get().getViewManager().unregisterGLView(detailBrick);
				detailBrick.unregisterEventListeners();
				detailBrick.destroy();
				detailBrick = null;
			}

			isDetailBrickShown = false;

			if (hideDetailBrick && expandLeft) {
				visBricks.switchToOverviewModeLeft();
			}
			if (hideDetailBrick && !expandLeft) {
				visBricks.switchToOverviewModeRight();
			}

			hideDetailBrick = false;

			mainRow.updateSubLayout();
			// visBricks.setLastResizeDirectionWasToLeft(false);
			visBricks.updateLayout();
			visBricks.updateConnectionLinesBetweenDimensionGroups();
		}

		while (!uninitializedBricks.isEmpty()) {
			uninitializedBricks.poll().initRemote(gl, this, glMouseListener);
			visBricks.updateLayout();
			visBricks.updateConnectionLinesBetweenDimensionGroups();
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
	public void handleDragging(GL2 gl, final float mouseCoordinateX,
			final float mouseCoordinateY) {

		gl.glColor4f(0, 0, 0, 0.5f);
		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex3f(mouseCoordinateX, mouseCoordinateY, 1);
		gl.glVertex3f(mouseCoordinateX + 1, mouseCoordinateY, 1);
		gl.glVertex3f(mouseCoordinateX + 1, mouseCoordinateY + 1, 1);
		gl.glVertex3f(mouseCoordinateX, mouseCoordinateY + 1, 1);
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

		Point currentPoint = glMouseListener.getPickedPoint();

		float[] pointCordinates = GLCoordinateUtils
				.convertWindowCoordinatesToWorldCoordinates(gl, currentPoint.x,
						currentPoint.y);

		if (Float.isNaN(previousYCoordinate)) {
			previousYCoordinate = pointCordinates[1];
			return;
		}

		float change = pointCordinates[1] - previousYCoordinate;
		previousYCoordinate = pointCordinates[1];

		// updateBrickSizes(topCol, topBricks, newSize);

		float bottomSize = clusterBrickColumn.getSizeScaledY();
		clusterBrickColumn.setAbsoluteSizeY(bottomSize + change);
		// float centerSize = headerBrickLayout.getSizeScaledY();
		//
		// headerBrickLayout.setAbsoluteSizeY(centerSize);

		// headerBrickLayout.updateSubLayout();
		mainRow.updateSubLayout();
		// groupColumn.updateSubLayout();
		visBricks.updateConnectionLinesBetweenDimensionGroups();

	}

	/**
	 * Updates the layout of this dimensionGroup
	 */
	public void updateLayout() {
		mainRow.updateSubLayout();
		// groupColumn.updateSubLayout();
		visBricks.updateConnectionLinesBetweenDimensionGroups();
	}

	/**
	 * Switch all bricks to the specified view type
	 * 
	 * @param viewType
	 */
	public void switchBrickViews(EContainedViewType viewType) {

		for (GLBrick brick : clusterBricks) {
			brick.setContainedView(viewType);
		}
		if (detailBrick != null) {
			detailBrick.setContainedView(viewType);
		}
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
	 * Note: The vis bricks view is needed for pushing the picking names, so
	 * that the GLVisBricks view can gets the events
	 * 
	 */
	public void setVisBricksView(GLVisBricks glVisBricksView) {
		this.visBricks = glVisBricksView;
	}

	/**
	 * Note: The vis bricks view is needed for pushing the picking names, so
	 * that the GLVisBricks view can gets the events
	 * 
	 */
	public GLVisBricks getVisBricksView() {
		return visBricks;
	}

	// public DataTable getTable() {
	// return set;
	// }

	/**
	 * Get the id of the set that this dimension group is showing
	 * 
	 * @return
	 */
	public int getTableID() {
		return dataContainer.getID();
	}

	/**
	 * Returns the list of bricks ordered from bottom to top as it is rendered
	 * in this dimension group
	 * 
	 * @return
	 */
	public List<GLBrick> getBricks() {
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
		return getBricks();
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
	 * Returns the center brick that shows the summary of the dimension group
	 * data.
	 * 
	 * @return
	 */
	public GLBrick getHeaderBrick() {
		return headerBrick;
	}

	@Override
	public int getNumberOfSelections(SelectionType SelectionType) {
		return 0;
	}

	@Override
	public Row getLayout() {
		return mainRow;
	}

	// @Override
	// public void setDataDomain(ASetBasedDataDomain dataDomain) {
	// this.dataDomain = dataDomain;
	// }
	//
	// @Override
	// public ASetBasedDataDomain getDataDomain() {
	// return dataDomain;
	// }

	// public void setTable(DataTable set) {
	// this.set = set;
	// }

	@Override
	public void handleLayoutSizeCollision(int managingClassID, int layoutID, float toBigBy) {
		// if (managingClassID != uniqueID)
		// return;
		//
		// System.out.println("handling layout collision");
		// // if (layoutID == TOP_COLUMN_ID) {
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
		// if (layoutID == BOTTOM_COLUMN_ID) {
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
	 * Sets whether the views of all bricks of this dimension groups shall be
	 * switched when switching the view in a single brick.
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
	 * @return True, if the views of all bricks of this dimension groups shall
	 *         be switched when switching the view in a single brick.
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
	 *            Specifies, whether the detail brick shall be expanded on the
	 *            left or on the right.
	 */
	public void showDetailedBrick(GLBrick brick, boolean expandLeft) {

		if (detailBrick != null) {
			GeneralManager.get().getViewManager().unregisterGLView(detailBrick);
			detailBrick.unregisterEventListeners();
			detailBrick.destroy();
			detailBrick = null;
		}

		detailBrickLayout = new Column("detailBrickWrappingLayout");

		detailBrick = createBrick(detailBrickLayout, brick.getDataContainer());
		detailBrick.setHeaderBrick(brick.isHeaderBrick());
		// detailBrick.setBrickData(brick.getBrickData());
		// detailBrick.setBrickConfigurer(brick.getBrickConfigurer());
		// detailBrick.setRecordVA(brick.getGroup(), brick.getRecordVA());

		detailBrick.setBrickHeigthMode(EBrickHeightMode.STATIC);
		detailBrick.setBrickWidthMode(EBrickWidthMode.STATIC);
		int detailBrickWidth = getDetailBrickWidthPixels(!expandLeft);
		detailBrick.setStaticBrickWidth(detailBrickWidth);
		detailBrick.setStaticBrickHeight(getDetailBrickHeightPixels());

		detailBrick.setBrickLayoutTemplate(new DetailBrickLayoutTemplate(detailBrick,
				this, visBricks, detailBrick.getBrickConfigurer()), brick
				.getCurrentViewType());

		overviewDetailGapLayout = new ElementLayout("brickSpacingLayout");
		overviewDetailGapLayout.setPixelSizeX(OVERVIEW_DETAIL_GAP_PIXEL);
		overviewDetailGapLayout.setRatioSizeY(1);

		if (expandLeft) {
			overviewDetailGapLayout.setRenderer(new OverviewDetailBandRenderer(
					detailBrick, brick, false));
		} else {
			overviewDetailGapLayout.setRenderer(new OverviewDetailBandRenderer(brick,
					detailBrick, true));
		}

		DimensionGroup otherDetailDimensionGroup = getOtherDetailDimensionGroup(!expandLeft);

		if (otherDetailDimensionGroup != null
				&& otherDetailDimensionGroup.isDetailBrickShown()) {
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
		showDetailBrick = true;
	}

	/**
	 * Hides the detail brick.
	 */
	public void hideDetailedBrick() {
		isDetailBrickShown = false;
		hideDetailBrick = true;
		DimensionGroup otherDetailDimensionGroup = getOtherDetailDimensionGroup(isLeftmost());
		if (otherDetailDimensionGroup != null
				&& otherDetailDimensionGroup.isDetailBrickShown()) {
			otherDetailDimensionGroup.setDetailBrickWidth(otherDetailDimensionGroup
					.getDetailBrickWidthPixels(otherDetailDimensionGroup.isLeftmost()));
		}
	}

	public int getDetailBrickHeightPixels() {
		return (int) (parentGLCanvas.getHeight() * 0.9f);
	}

	/**
	 * @param isCurrentDimensionGroupLeft
	 *            Specifies, whether the dimension group is on the left side.
	 *            (When a detail brick is shown, only two dimension groups are
	 *            visible)
	 * @return Width of the detail brick
	 */
	public int getDetailBrickWidthPixels(boolean isCurrentDimensionGroupLeft) {

		DimensionGroup otherDimensionGroup = getOtherDetailDimensionGroup(isCurrentDimensionGroupLeft);
		int otherDimensionGroupColumnWidth = 0;
		boolean otherDimensionGroupShowsDetail = false;
		if (otherDimensionGroup != null) {
			otherDimensionGroupShowsDetail = otherDimensionGroup.isDetailBrickShown();
			otherDimensionGroupColumnWidth = otherDimensionGroup
					.getGroupColumnWidthPixels();
		}
		int detailAreaWidth = parentGLCanvas.getWidth() - 2 * OVERVIEW_DETAIL_GAP_PIXEL
				- 2 * GLVisBricks.DIMENSION_GROUP_SIDE_SPACING
				- getGroupColumnWidthPixels() - otherDimensionGroupColumnWidth;
		int detailGapWidth = (int) (DETAIL_GAP_PORTION * detailAreaWidth);
		detailGapWidth = (detailGapWidth < MIN_DETAIL_GAP_PIXEL) ? MIN_DETAIL_GAP_PIXEL
				: detailGapWidth;

		int detailWidth = (otherDimensionGroupShowsDetail) ? (int) ((detailAreaWidth - detailGapWidth) / 2.0f)
				: detailAreaWidth;

		return detailWidth;
	}

	/**
	 * Returns the neighboring {@link DimensionGroup}, either the one to the
	 * right, if the parameter is true, or the one to the left, if the parameter
	 * is false.
	 * <p>
	 * Returns null there is no dimension group on the specified side
	 * 
	 * @param isCurrentDimensionGroupLeft
	 *            Specifies, whether the dimension group is on the left side.
	 *            (When a detail brick is shown, only two dimension groups are
	 *            visible)
	 * @return The other dimension group that is currently visible in the detail
	 *         mode, or null if there is no other group
	 */
	private DimensionGroup getOtherDetailDimensionGroup(
			boolean isCurrentDimensionGroupLeft) {

		DimensionGroupManager dimensionGroupManager = visBricks
				.getDimensionGroupManager();

		ArrayList<DimensionGroup> dimensionGroups = dimensionGroupManager
				.getDimensionGroups();
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
		DimensionGroupManager dimensionGroupManager = visBricks
				.getDimensionGroupManager();
		int index = dimensionGroupManager.indexOfDimensionGroup(this);
		return (index == dimensionGroupManager.getCenterGroupStartIndex());
	}

	/**
	 * @return True if this dimension group is the rightmost dimension group.
	 */
	public boolean isRightmost() {
		DimensionGroupManager dimensionGroupManager = visBricks
				.getDimensionGroupManager();
		int index = dimensionGroupManager.indexOfDimensionGroup(this);
		return (index == dimensionGroupManager.getRightGroupStartIndex() - 1);
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

	@Override
	protected ArrayList<ElementConnectionInformation> createElementConnectionInformation(
			IDType idType, int id) throws InvalidAttributeValueException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param isVerticalMoveDraggingActive
	 *            setter, see {@link #isVerticalMoveDraggingActive}
	 */
	public void setVerticalMoveDraggingActive(boolean isVerticalMoveDraggingActive) {
		this.isVerticalMoveDraggingActive = isVerticalMoveDraggingActive;
	}

	/**
	 * Returns the proportional height a record should have in this dimension
	 * group as pixels. Can be in sub-pixel space and therefore returns a
	 * double.
	 * 
	 * @return
	 */
	public double getProportionalHeightPerRecord() {
		double useablePixelHeight = getParentGLCanvas().getHeight()
				- visBricks.getArchHeight()
				- (dataContainer.getRecordPerspective().getVirtualArray().getGroupList()
						.size() + 1) * BETWEEN_BRICKS_SPACING;
		double proportionalRecordHeight = useablePixelHeight
				/ dataContainer.getNrRecords();
		return proportionalRecordHeight;
	}
}
