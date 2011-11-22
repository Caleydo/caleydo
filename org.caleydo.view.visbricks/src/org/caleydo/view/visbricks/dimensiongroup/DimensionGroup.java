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
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.core.view.opengl.util.GLCoordinateUtils;
import org.caleydo.core.view.opengl.util.draganddrop.IDraggable;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.view.visbricks.GLVisBricks;
import org.caleydo.view.visbricks.brick.EContainedViewType;
import org.caleydo.view.visbricks.brick.GLBrick;
import org.caleydo.view.visbricks.brick.data.IBrickSortingStrategy;
import org.caleydo.view.visbricks.brick.layout.ABrickLayoutTemplate;
import org.caleydo.view.visbricks.brick.layout.CentralBrickLayoutTemplate;
import org.caleydo.view.visbricks.brick.layout.CompactCentralBrickLayoutTemplate;
import org.caleydo.view.visbricks.brick.layout.DefaultBrickLayoutTemplate;
import org.caleydo.view.visbricks.brick.layout.DetailBrickLayoutTemplate;
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

	private Column groupColumn;
	protected Row detailRow;

	protected ArrayList<GLBrick> bottomBricks;

	private boolean isGlobalViewSwitching = false;

	protected Column bottomCol;
	private GLBrick centerBrick;
	private GLBrick detailBrick;
	private Column centerLayout;
	// private ViewFrustum brickFrustum;
	// protected DataTable set;

	private EventPublisher eventPublisher = GeneralManager.get().getEventPublisher();
	private LayoutSizeCollisionListener layoutSizeCollisionListener;
	private IBrickSortingStrategy brickSortingStrategy;

	private boolean isCollapsed = false;

	private Queue<GLBrick> uninitializedBricks = new LinkedList<GLBrick>();

	protected GLVisBricks visBricks;

	// Stuff for dragging up and down
	private boolean isVerticalMoveDraggingActive = false;

	private float previousYCoordinate = Float.NaN;

	/** the minimal width of the brick */
	private int minPixelWidth;
	private float minWidth;

	private boolean showDetailBrick = false;
	private boolean hideDetailBrick = false;
	private boolean isDetailBrickShown = false;
	private boolean expandLeft = false;

	private Column detailBrickLayout;
	private ElementLayout overviewDetailGapLayout;

	public static int BOTTOM_COLUMN_ID = 0;
	public static int TOP_COLUMN_ID = 1;

	IBrickConfigurer brickConfigurer;

	public DimensionGroup(GLCanvas canvas, Composite parentComposite,
			ViewFrustum viewFrustum) {
		super(canvas, parentComposite, viewFrustum);

		viewType = VIEW_TYPE;

		detailRow = new Row("detailRow");
		detailRow.setRenderingPriority(1);
		detailRow.setXDynamic(true);
		detailRow.setFrameColor(1, 0, 1, 1);
		detailRow.sethAlign(HAlign.CENTER);
		// detailRow.setDebug(true);

		groupColumn = new Column("dimensionGroup");
		groupColumn.setXDynamic(true);
		groupColumn.setVAlign(VAlign.CENTER);
		// groupColumn.setDebug(true);

		bottomCol = new Column("dimensionGroupColumnBottom");
		bottomCol.setFrameColor(1, 0, 1, 1);
		bottomCol.setBottomUp(false);
		bottomCol.setXDynamic(true);
		bottomCol.setIDs(uniqueID, BOTTOM_COLUMN_ID);
		bottomCol.setVAlign(VAlign.CENTER);

		bottomBricks = new ArrayList<GLBrick>(20);

		centerLayout = new Column("centerLayout");
		centerLayout.setFrameColor(1, 1, 0, 1);

		initGroupColumn();
		detailRow.append(groupColumn);
	}

	/**
	 * @param brickConfigurer
	 *            setter, see {@link #brickConfigurer}
	 */
	public void setBrickConfigurer(IBrickConfigurer brickConfigurer) {
		this.brickConfigurer = brickConfigurer;
	}

	/**
	 * @param brickSortingStrategy
	 *            setter, see {@link #brickSortingStrategy}
	 */
	public void setBrickSortingStrategy(IBrickSortingStrategy brickSortingStrategy) {
		this.brickSortingStrategy = brickSortingStrategy;
	}

	private void initGroupColumn() {
		if (isCollapsed) {
			groupColumn.clear();
			groupColumn.append(centerLayout);
		} else {
			groupColumn.clear();
			groupColumn.append(bottomCol);
			groupColumn.append(centerLayout);
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

		if (centerBrick == null || uninitializedBricks.contains(centerBrick))
			return;

		if (isCollapsed) {
			centerBrick.collapse();
		} else {
			centerBrick.expand();
		}
		initGroupColumn();
		// groupColumn.updateSubLayout();
	}

	/**
	 * Creates all bricks of the dimension group
	 */
	protected void createBricks() {
		// create basic layouts

		// minPixelWidth = PIXEL_PER_DIMENSION * table.size();
		// if (minPixelWidth < MIN_BRICK_WIDTH_PIXEL)
		minPixelWidth = MIN_BRICK_WIDTH_PIXEL;
		minWidth = pixelGLConverter.getGLWidthForPixelWidth(minPixelWidth);

		groupColumn.addBackgroundRenderer(new DimensionGroupBackgroundColorRenderer(
				dataContainer.getDataDomain().getColor().getRGBA()));

		centerBrick = createBrick(centerLayout, dataContainer);
		// centerBrick.setBrickData(dimensionGroupData.getSummaryBrickData());
		// centerBrick.setBrickConfigurer(dimensionGroupData.getBrickConfigurer());
		// centerBrick.setRecordVA(new Group(), recordVA);

		ABrickLayoutTemplate layoutTemplate;

		if (isCollapsed) {
			layoutTemplate = new CompactCentralBrickLayoutTemplate(centerBrick, this,
					visBricks, centerBrick.getBrickConfigurer());
		} else {
			layoutTemplate = new CentralBrickLayoutTemplate(centerBrick, this, visBricks,
					centerBrick.getBrickConfigurer());
		}
		centerBrick.setBrickLayoutTemplate(layoutTemplate,
				layoutTemplate.getDefaultViewType());

		createSubBricks();
	}

	/**
	 * Creates all bricks except for the center brick based on the groupList in
	 * the recordVA
	 */
	protected void createSubBricks() {

		destroyOldBricks();

		List<DataContainer> brickDataContainers = dataContainer
				.getRecordSubDataContainers();

		if (brickDataContainers == null || brickDataContainers.size() <= 0)
			return;

		Set<GLBrick> segmentBricks = new HashSet<GLBrick>();

		for (DataContainer brickData : brickDataContainers) {
			GLBrick segmentBrick = createBrick(new ElementLayout("brick"), brickData);

			// segmentBrick.setBrickConfigurer(dimensionGroupData.getBrickConfigurer());

			ABrickLayoutTemplate layoutTemplate = new DefaultBrickLayoutTemplate(
					segmentBrick, visBricks, this, segmentBrick.getBrickConfigurer());

			segmentBrick.setBrickLayoutTemplate(layoutTemplate,
					layoutTemplate.getDefaultViewType());
			// FIXME temp solution
			// segmentBrick.getLayout().setPixelGLConverter(pixelGLConverter);
			// segmentBrick.getLayout().setPixelSizeY(80);

			segmentBricks.add(segmentBrick);
		}

		// if (false) {

		ArrayList<GLBrick> sortedBricks = brickSortingStrategy
				.getSortedBricks(segmentBricks);

		boolean summaryBrickPassed = false;

		for (GLBrick brick : sortedBricks) {
			bottomBricks.add(brick);
			bottomCol.append(brick.getLayout());
		}
		// }
		// else {
		// ArrayList<GLBrick> sortedBricks =
		// brickSortingStrategy.getSortedBricks(
		// segmentBricks, null);
		//
		// for (GLBrick brick : sortedBricks) {
		//
		// bottomBricks.add(brick);
		// bottomCol.append(brick.getLayout());
		//
		// }
		// }

		ElementLayout brickSpacingLayout = new ElementLayout("brickSpacingLayout");
		brickSpacingLayout.setPixelGLConverter(pixelGLConverter);
		brickSpacingLayout.setPixelSizeY(10);
		brickSpacingLayout.setRatioSizeX(0);

		for (int count = 0; count < bottomCol.size();) {

			bottomCol.add(count, brickSpacingLayout);
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
		wrappingLayout.setPixelGLConverter(pixelGLConverter);
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
		for (GLBrick brick : bottomBricks) {
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

		if (isCollapsed) {
			centerLayout.setRatioSizeY(1);
		} else {

			if (!(centerLayout.getSizeScaledY() > 0)) {
				bottomCol.setRatioSizeY(1f);
				centerLayout.setPixelGLConverter(pixelGLConverter);
				centerLayout.setPixelSizeY(archHeight);
			}
		}

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

		bottomCol.clear();
		bottomBricks.clear();
		createSubBricks();
		detailRow.updateSubLayout();
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

			detailRow.clear();
			if (expandLeft) {
				detailRow.append(detailBrickLayout);
				detailRow.append(overviewDetailGapLayout);
				detailRow.append(groupColumn);
				visBricks.switchToDetailModeRight(this);

			} else {
				detailRow.append(groupColumn);
				detailRow.append(overviewDetailGapLayout);
				detailRow.append(detailBrickLayout);
				visBricks.switchToDetailModeLeft(this);
			}

			detailRow.updateSubLayout();
			// visBricks.setLastResizeDirectionWasToLeft(false);
			visBricks.updateLayout();
			visBricks.updateConnectionLinesBetweenDimensionGroups();
			showDetailBrick = false;
			isDetailBrickShown = true;
		}

		if (hideDetailBrick || (isCollapsed && detailBrick != null)) {
			detailRow.clear();
			detailRow.append(groupColumn);
			if (detailBrick != null) {
				GeneralManager.get().getViewManager().unregisterGLView(detailBrick);
				detailBrick.unregisterEventListeners();
				detailBrick.destroy();
				detailBrick = null;
			}

			isDetailBrickShown = false;

			if (hideDetailBrick && expandLeft) {
				visBricks.switchToOverviewModeRight();
			}
			if (hideDetailBrick && !expandLeft) {
				visBricks.switchToOverviewModeLeft();
			}

			hideDetailBrick = false;

			detailRow.updateSubLayout();
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
	protected void handlePickingEvents(PickingType pickingType, PickingMode pickingMode,
			int pickingID, Pick pick) {
		switch (pickingType) {
		case MOVE_VERTICALLY_HANDLE:
			if (pickingMode == PickingMode.CLICKED) {
				isVerticalMoveDraggingActive = true;
			}
			break;
		}

	}

	@Override
	public void setDraggingStartPoint(float mouseCoordinateX, float mouseCoordinateY) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleDragging(GL2 gl, final float mouseCoordinateX,
			final float mouseCoordinateY) {

		// GLHelperFunctions.drawPointAt(gl, mouseCoordinateX, mouseCoordinateY,
		// 0);
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

		float bottomSize = bottomCol.getSizeScaledY();
		bottomCol.setAbsoluteSizeY(bottomSize + change);
		float centerSize = centerLayout.getSizeScaledY();

		centerLayout.setAbsoluteSizeY(centerSize);

		centerLayout.updateSubLayout();
		detailRow.updateSubLayout();
		// groupColumn.updateSubLayout();
		visBricks.updateConnectionLinesBetweenDimensionGroups();

	}

	/**
	 * Updates the layout of this dimensionGroup
	 */
	public void updateLayout() {
		detailRow.updateSubLayout();
		// groupColumn.updateSubLayout();
		visBricks.updateConnectionLinesBetweenDimensionGroups();
	}

	/**
	 * Switch all bricks to the specified view type
	 * 
	 * @param viewType
	 */
	public void switchBrickViews(EContainedViewType viewType) {

		for (GLBrick brick : bottomBricks) {
			brick.setContainedView(viewType);
		}
		if (detailBrick != null) {
			detailBrick.setContainedView(viewType);
		}
		// centerBrick.setRemoteView(viewType);
		detailRow.updateSubLayout();
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

		for (int i = bottomBricks.size() - 1; i >= 0; i--) {
			bricks.add(bottomBricks.get(i));

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
	public GLBrick getCenterBrick() {
		return centerBrick;
	}

	@Override
	public int getNumberOfSelections(SelectionType SelectionType) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Row getLayout() {
		return detailRow;
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
		if (managingClassID != uniqueID)
			return;

		System.out.println("handling layout collision");
		// if (layoutID == TOP_COLUMN_ID) {
		// boolean changeMade = false;
		// for (int count = topBricks.size() - 1; count >= 0; count--) {
		// GLBrick brick = topBricks.get(count);
		// if (toBigBy < 0)
		// break;
		// if (!brick.isInOverviewMode() && !brick.isSizeFixed()) {
		// // toBigBy -= brick.collapse();
		// // changeMade = true;
		// }
		// }
		// if (changeMade)
		// topCol.updateSubLayout();
		// }
		if (layoutID == BOTTOM_COLUMN_ID) {
			boolean changeMade = false;

			for (int count = bottomBricks.size() - 1; count >= 0; count--) {
				GLBrick brick = bottomBricks.get(count);
				if (toBigBy < 0)
					break;
				if (!brick.isInOverviewMode() && !brick.isSizeFixed()) {
					// toBigBy -= brick.collapse();
					// changeMade = true;
				}
			}
			if (changeMade)
				bottomCol.updateSubLayout();
		}
	}

	/**
	 * Sets whether the views of all bricks of this dimension groups shall be
	 * switched when switching the view in a single brick.
	 * 
	 * @param isGlobalViewSwitching
	 */
	public void setGlobalViewSwitching(boolean isGlobalViewSwitching) {
		this.isGlobalViewSwitching = isGlobalViewSwitching;

		for (GLBrick brick : bottomBricks) {
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

	/**
	 * Called to hide the handles of all bricks of this dimension group.
	 */
	public void hideHandles() {

		centerBrick.hideHandles();
		for (GLBrick brick : bottomBricks) {
			brick.hideHandles();
		}
	}

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

		detailBrickLayout.setPixelGLConverter(pixelGLConverter);

		detailBrick = createBrick(detailBrickLayout, brick.getDataContainer());
		// detailBrick.setBrickData(brick.getBrickData());
		// detailBrick.setBrickConfigurer(brick.getBrickConfigurer());
		// detailBrick.setRecordVA(brick.getGroup(), brick.getRecordVA());

		int detailBrickWidth = getDetailBrickWidthPixels(!expandLeft);
		detailBrickLayout.setPixelSizeX(detailBrickWidth);
		detailBrickLayout.setPixelSizeY(getDetailBrickHeightPixels());

		detailBrick.setBrickLayoutTemplate(new DetailBrickLayoutTemplate(detailBrick,
				this, visBricks, detailBrick.getBrickConfigurer()), brick
				.getCurrentViewType());

		overviewDetailGapLayout = new ElementLayout("brickSpacingLayout");
		overviewDetailGapLayout.setPixelGLConverter(pixelGLConverter);
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

		if (otherDetailDimensionGroup.isDetailBrickShown()) {
			otherDetailDimensionGroup.setDetailBrickWidth(detailBrickWidth);
		}

		showDetailBrick = true;
		this.expandLeft = expandLeft;
		brick.hideHandles();
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
		if (otherDetailDimensionGroup.isDetailBrickShown()) {
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
		boolean otherDimensionGroupShowsDetail = otherDimensionGroup.isDetailBrickShown();
		int otherDimensionGroupColumnWidth = otherDimensionGroup
				.getGroupColumnWidthPixels();

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
	 * @param isCurrentDimensionGroupLeft
	 *            Specifies, whether the dimension group is on the left side.
	 *            (When a detail brick is shown, only two dimension groups are
	 *            visible)
	 * @return The other dimension group that is currently visible in the detail
	 *         mode.
	 */
	private DimensionGroup getOtherDetailDimensionGroup(
			boolean isCurrentDimensionGroupLeft) {

		DimensionGroupManager dimensionGroupManager = visBricks
				.getDimensionGroupManager();

		ArrayList<DimensionGroup> dimensionGroups = dimensionGroupManager
				.getDimensionGroups();
		int dimensionGroupIndex = dimensionGroups.indexOf(this);

		if (isCurrentDimensionGroupLeft) {
			return dimensionGroups.get(dimensionGroupIndex + 1);
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
		return pixelGLConverter.getPixelWidthForGLWidth(groupColumn.getSizeScaledX());
	}

	/**
	 * @return Column of bricks.
	 */
	public Column getGroupColumn() {
		return groupColumn;
	}

	@Override
	protected ArrayList<ElementConnectionInformation> createElementConnectionInformation(IDType idType, int id)
			throws InvalidAttributeValueException {
		// TODO Auto-generated method stub
		return null;
	}
}
