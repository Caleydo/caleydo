package org.caleydo.view.visbricks.dimensiongroup;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.set.Set;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.EVAOperation;
import org.caleydo.core.data.virtualarray.delta.ContentVADelta;
import org.caleydo.core.data.virtualarray.group.ContentGroupList;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
import org.caleydo.core.manager.event.EventPublisher;
import org.caleydo.core.manager.event.data.ReplaceContentVAEvent;
import org.caleydo.core.manager.event.view.storagebased.ContentVAUpdateEvent;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.IDataDomainSetBasedView;
import org.caleydo.core.view.opengl.camera.ECameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.listener.ContentVAUpdateListener;
import org.caleydo.core.view.opengl.canvas.listener.IContentVAUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.ReplaceContentVAListener;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.ILayoutedElement;
import org.caleydo.core.view.opengl.layout.ViewLayoutRenderer;
import org.caleydo.core.view.opengl.layout.event.ILayoutSizeCollisionHandler;
import org.caleydo.core.view.opengl.layout.event.LayoutSizeCollisionEvent;
import org.caleydo.core.view.opengl.layout.event.LayoutSizeCollisionListener;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.GLCoordinateUtils;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.draganddrop.IDraggable;
import org.caleydo.core.view.opengl.util.draganddrop.IDropArea;
import org.caleydo.view.visbricks.GLVisBricks;
import org.caleydo.view.visbricks.brick.EContainedViewType;
import org.caleydo.view.visbricks.brick.GLBrick;
import org.caleydo.view.visbricks.brick.layout.CentralBrickLayoutTemplate;
import org.caleydo.view.visbricks.brick.layout.CompactBrickLayoutTemplate;

/**
 * Container for a group of dimensions. Manages layouts as well as brick views
 * for the whole dimension group.
 * 
 * @author Alexander Lex
 * 
 */
public class DimensionGroup extends AGLView implements IDataDomainSetBasedView,
		IContentVAUpdateHandler, ILayoutSizeCollisionHandler, ILayoutedElement,
		IDraggable {

	public final static String VIEW_ID = "org.caleydo.view.dimensiongroup";

	public final static int PIXEL_PER_DIMENSION = 30;
	public final static int MIN_BRICK_WIDTH_PIXEL = 150;

	private GLVisBricks glVisBricksView;

	private Column groupColumn;

	private ArrayList<GLBrick> bottomBricks;
	private ArrayList<GLBrick> topBricks;

	private boolean isGlobalViewSwitching = false;

	private Column bottomCol;
	private GLBrick centerBrick;
	private Column centerLayout;
	private Column topCol;
	// private ViewFrustum brickFrustum;
	private ISet set;
	private ASetBasedDataDomain dataDomain;

	private EventPublisher eventPublisher = GeneralManager.get()
			.getEventPublisher();
	private ContentVAUpdateListener contentVAUpdateListener;
	private ReplaceContentVAListener replaceContentVAListener;
	private LayoutSizeCollisionListener layoutSizeCollisionListener;

	private boolean isCollapsed = false;

	private Queue<GLBrick> uninitializedBricks = new LinkedList<GLBrick>();

	private GLVisBricks visBricks;

	// Stuff for dragging up and down
	private boolean isVerticalMoveDraggingActive = false;
	// private float previousXCoordinate = Float.NaN;
	private float previousYCoordinate = Float.NaN;

	/** the minimal width of the brick */
	private int minPixelWidth;
	private float minWidth;

	public static int BOTTOM_COLUMN_ID = 0;
	public static int TOP_COLUMN_ID = 1;

	public DimensionGroup(GLCaleydoCanvas canvas, ViewFrustum viewFrustum) {
		super(canvas, viewFrustum, true);

		groupColumn = new Column("dimensionGroup");
		// groupColumn.setDebug(true);
		groupColumn.setXDynamic(true);
		groupColumn.setVAlign(VAlign.CENTER);

		bottomCol = new Column("dimensionGroupColumnBottom");
		bottomCol.setFrameColor(1, 0, 1, 1);
		bottomCol.setBottomUp(false);
		bottomCol.setXDynamic(true);
		bottomCol.setIDs(uniqueID, BOTTOM_COLUMN_ID);
		bottomCol.setVAlign(VAlign.CENTER);

		// bottomCol.setDebug(true);

		bottomBricks = new ArrayList<GLBrick>(20);

		centerLayout = new Column("centerLayout");
		centerLayout.setFrameColor(1, 1, 0, 1);
		// centerLayout.setDebug(true);

		topCol = new Column("dimensionGroupColumnTop");
		topCol.setFrameColor(1, 0, 1, 1);
		topBricks = new ArrayList<GLBrick>(20);
		topCol.setXDynamic(true);
		topCol.setIDs(uniqueID, TOP_COLUMN_ID);
		// topCol.setDebug(true);
		topCol.setVAlign(VAlign.CENTER);

		initGroupColumn();
	}

	public void setVisBricks(GLVisBricks visBricks) {
		this.visBricks = visBricks;
	}

	private void initGroupColumn() {
		if (isCollapsed) {
			groupColumn.clear();
			groupColumn.append(centerLayout);
		} else {
			groupColumn.clear();
			groupColumn.append(bottomCol);
			groupColumn.append(centerLayout);
			groupColumn.append(topCol);
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

		// FIXME: Christian, here you can change the layout of the brick to the
		// respective state

		initGroupColumn();
		// groupColumn.updateSubLayout();
	}

	/**
	 * Creates all bricks of the dimension group
	 */
	private void createBricks() {
		// create basic layouts

		minPixelWidth = PIXEL_PER_DIMENSION * set.size();
		if (minPixelWidth < MIN_BRICK_WIDTH_PIXEL)
			minPixelWidth = MIN_BRICK_WIDTH_PIXEL;
		minWidth = parentGLCanvas.getPixelGLConverter()
				.getGLWidthForPixelWidth(minPixelWidth);

		centerBrick = createBrick(centerLayout);
		centerBrick.setContentVA(new Group(), set.getContentData(Set.CONTENT)
				.getContentVA());
		centerBrick.setBrickLayoutTemplate(new CentralBrickLayoutTemplate(
				centerBrick, this, centerBrick.getLayoutConfigurer()));

		createSubBricks();
	}

	/**
	 * Creates all bricks except for the center brick based on the groupList in
	 * the contentVA
	 */
	private void createSubBricks() {

		destroyOldBricks();

		ContentVirtualArray contentVA = set.getContentData(Set.CONTENT)
				.getContentVA();

		if (contentVA.getGroupList() == null)
			return;

		ContentGroupList groupList = contentVA.getGroupList();
		// int count = 0;
		groupList.updateGroupInfo();

		for (Group group : groupList) {
			GLBrick subBrick = createBrick(new ElementLayout("subbrick"));

			ContentVirtualArray subVA = new ContentVirtualArray("CONTENT",
					contentVA.getVirtualArray().subList(group.getStartIndex(),
							group.getEndIndex() + 1));

			subBrick.setContentVA(group, subVA);
			// FIXME temp solution
			subBrick.getLayout().setPixelGLConverter(
					parentGLCanvas.getPixelGLConverter());
			subBrick.getLayout().setPixelSizeY(80);

			if (centerBrick.getAverageValue() < subBrick.getAverageValue()) {
				insertBrick(subBrick, topBricks, topCol);

			} else {
				insertBrick(subBrick, bottomBricks, bottomCol);
			}
		}
		ElementLayout brickSpacingLayout = new ElementLayout(
				"brickSpacingLayout");
		brickSpacingLayout.setPixelGLConverter(parentGLCanvas
				.getPixelGLConverter());
		brickSpacingLayout.setPixelSizeY(10);
		brickSpacingLayout.setRatioSizeX(0);
		// brickSpacingLayout.setDebug(true);

		for (int count = 0; count < topCol.size();) {

			topCol.add(count, brickSpacingLayout);
			count++;
			count++;
		}

		for (int count = 0; count < bottomCol.size();) {

			bottomCol.add(count, brickSpacingLayout);
			count++;
			count++;
		}

	}

	/**
	 * Creates a single brick
	 * 
	 * @param wrappingLayout
	 * @return
	 */
	private GLBrick createBrick(ElementLayout wrappingLayout) {
		ViewFrustum brickFrustum = new ViewFrustum(
				ECameraProjectionMode.ORTHOGRAPHIC, 0, 0, 0, 0, -4, 4);
		GLBrick brick = (GLBrick) GeneralManager.get().getViewGLCanvasManager()
				.createGLView(GLBrick.class, getParentGLCanvas(), brickFrustum);

		brick.setRemoteRenderingGLView(getRemoteRenderingGLCanvas());
		brick.setDataDomain(dataDomain);
		brick.setSet(set);
		brick.setVisBricks(visBricks);
		brick.setLayout(wrappingLayout);
		brick.setDimensionGroup(this);
		brick.initialize();
		uninitializedBricks.add(brick);

		ViewLayoutRenderer brickRenderer = new ViewLayoutRenderer(brick);
		wrappingLayout.setRenderer(brickRenderer);
		wrappingLayout
				.setPixelGLConverter(parentGLCanvas.getPixelGLConverter());
		wrappingLayout.setPixelSizeX(minPixelWidth);

		return brick;
	}

	/**
	 * Inserts the specified brick into the specified list of bricks and column
	 * so that it's ordered based on average values.
	 * 
	 * @param subBrick
	 * @param bricks
	 * @param layout
	 */
	private void insertBrick(GLBrick subBrick, ArrayList<GLBrick> bricks,
			Column layout) {

		int count;
		for (count = 0; count < bricks.size(); count++) {
			if (bricks.get(count).getAverageValue() > subBrick
					.getAverageValue())
				break;
		}
		bricks.add(count, subBrick);
		layout.add(count, subBrick.getLayout());
	}

	/**
	 * Destroys all sub-bricks
	 */
	private void destroyOldBricks() {
		for (GLBrick brick : topBricks) {
			GeneralManager.get().getViewGLCanvasManager()
					.unregisterGLView(brick);
			brick.unregisterEventListeners();
			brick.destroy();
		}
		for (GLBrick brick : bottomBricks) {
			GeneralManager.get().getViewGLCanvasManager()
					.unregisterGLView(brick);
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
			bottomCol.setRatioSizeY(0.5f);
			topCol.setRatioSizeY(0.5f);
			centerLayout.setPixelGLConverter(parentGLCanvas
					.getPixelGLConverter());
			centerLayout.setPixelSizeY(archHeight);
		}

	}

	@Override
	public void registerEventListeners() {

		contentVAUpdateListener = new ContentVAUpdateListener();
		contentVAUpdateListener.setHandler(this);
		contentVAUpdateListener.setExclusiveDataDomainType(dataDomain
				.getDataDomainType());
		eventPublisher.addListener(ContentVAUpdateEvent.class,
				contentVAUpdateListener);

		replaceContentVAListener = new ReplaceContentVAListener();
		replaceContentVAListener.setHandler(this);
		replaceContentVAListener.setExclusiveDataDomainType(dataDomain
				.getDataDomainType());
		eventPublisher.addListener(ReplaceContentVAEvent.class,
				replaceContentVAListener);

		layoutSizeCollisionListener = new LayoutSizeCollisionListener();
		layoutSizeCollisionListener.setHandler(this);
		eventPublisher.addListener(LayoutSizeCollisionEvent.class,
				layoutSizeCollisionListener);
	}

	@Override
	public void unregisterEventListeners() {
		if (contentVAUpdateListener != null) {
			eventPublisher.removeListener(contentVAUpdateListener);
			contentVAUpdateListener = null;
		}

		if (replaceContentVAListener != null) {
			eventPublisher.removeListener(replaceContentVAListener);
			replaceContentVAListener = null;
		}

		if (layoutSizeCollisionListener != null) {
			eventPublisher.removeListener(layoutSizeCollisionListener);
			layoutSizeCollisionListener = null;
		}
	}

	@Override
	public void handleVAUpdate(ContentVADelta vaDelta, String info) {
	}

	/**
	 * This is called when a clustering was run, so we replace the sub-bricks
	 */
	@Override
	public void replaceContentVA(int setID, String dataDomainType, String vaType) {

		if (set.getID() == setID) {
			topCol.clear();
			topBricks.clear();
			bottomCol.clear();
			bottomBricks.clear();
			createSubBricks();
			groupColumn.updateSubLayout();
			visBricks.updateConnectionLinesBetweenDimensionGroups();
		}
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		return null;
	}

	@Override
	public void clearAllSelections() {
	}

	@Override
	public void init(GL2 gl) {
	}

	@Override
	protected void initLocal(GL2 gl) {
	}

	@Override
	public void initRemote(GL2 gl, AGLView glParentView,
			GLMouseListener glMouseListener) {
		createBricks();
	}

	@Override
	public void display(GL2 gl) {
		while (!uninitializedBricks.isEmpty()) {
			uninitializedBricks.poll().initRemote(gl, this, glMouseListener);
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
	protected void handlePickingEvents(EPickingType pickingType,
			EPickingMode pickingMode, int pickingID, Pick pick) {
		switch (pickingType) {
		case DRAGGING_HANDLE:
			if (pickingMode == EPickingMode.CLICKED) {
				isVerticalMoveDraggingActive = true;
			}
			break;
		}

	}

	@Override
	public void setDraggingStartPoint(float mouseCoordinateX,
			float mouseCoordinateY) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleDragging(GL2 gl, final float mouseCoordinateX,
			final float mouseCoordinateY) {

		// GLHelperFunctions.drawPointAt(gl, mouseCoordinateX, mouseCoordinateY,
		// 0);
		gl.glColor4f(0, 0, 0, 0.5f);
		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex2f(mouseCoordinateX, mouseCoordinateY);
		gl.glVertex2f(mouseCoordinateX + 1, mouseCoordinateY);
		gl.glVertex2f(mouseCoordinateX + 1, mouseCoordinateY + 1);
		gl.glVertex2f(mouseCoordinateX, mouseCoordinateY + 1);
		gl.glEnd();

	}

	@Override
	public void handleDrop(GL2 gl, float mouseCoordinateX,
			float mouseCoordinateY) {

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

		float newSize = topCol.getSizeScaledY() - change;
		topCol.setAbsoluteSizeY(newSize);
		// updateBrickSizes(topCol, topBricks, newSize);

		float bottomSize = bottomCol.getSizeScaledY();
		bottomCol.setAbsoluteSizeY(bottomSize + change);
		float centerSize = centerLayout.getSizeScaledY();

		centerLayout.setAbsoluteSizeY(centerSize);

		centerLayout.updateSubLayout();
		groupColumn.updateSubLayout();
		visBricks.updateConnectionLinesBetweenDimensionGroups();

	}

	/**
	 * Updates the layout of this dimensionGroup
	 */
	public void updateLayout() {
		groupColumn.updateSubLayout();
		visBricks.updateConnectionLinesBetweenDimensionGroups();
	}

	/**
	 * Switch all bricks to the specified view type
	 * 
	 * @param viewType
	 */
	public void switchBrickViews(EContainedViewType viewType) {
		for (GLBrick brick : topBricks) {
			brick.setRemoteView(viewType);
		}
		for (GLBrick brick : bottomBricks) {
			brick.setRemoteView(viewType);
		}
		// centerBrick.setRemoteView(viewType);
		groupColumn.updateSubLayout();
	}

	public float getMinWidth() {
		return minWidth;
	}

	/**
	 * Note: The vis bricks view is needed for pushing the picking names, so
	 * that the GLVisBricks view can gets the events
	 * 
	 */
	public void setVisBricksView(GLVisBricks glVisBricksView) {
		this.glVisBricksView = glVisBricksView;
	}

	/**
	 * Note: The vis bricks view is needed for pushing the picking names, so
	 * that the GLVisBricks view can gets the events
	 * 
	 */
	public GLVisBricks getVisBricksView() {
		return glVisBricksView;
	}

	public ISet getSet() {
		return set;
	}

	/**
	 * Get the id of the set that this dimension group is showing
	 * 
	 * @return
	 */
	public int getSetID() {
		return set.getID();
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

		// for (GLBrick brick : bottomBricks) {
		// bricks.add(brick);
		// }

		for (GLBrick brick : topBricks) {
			bricks.add(brick);
		}
		return bricks;
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
	public String getShortInfo() {

		return "Dimension Group";
	}

	@Override
	public String getDetailedInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void broadcastElements(EVAOperation type) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getNumberOfSelections(SelectionType SelectionType) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Column getLayout() {
		return groupColumn;
	}

	@Override
	public void setDataDomain(ASetBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

	@Override
	public ASetBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	public void setSet(ISet set) {
		this.set = set;
	}

	@Override
	public void handleLayoutSizeCollision(int managingClassID, int layoutID,
			float toBigBy) {
		if (managingClassID != uniqueID)
			return;

		System.out.println("handling layout collision");
		if (layoutID == TOP_COLUMN_ID) {
			boolean changeMade = false;
			for (int count = topBricks.size() - 1; count >= 0; count--) {
				GLBrick brick = topBricks.get(count);
				if (toBigBy < 0)
					break;
				if (!brick.isInOverviewMode()) {
					toBigBy -= brick.setToOverviewMode();
					changeMade = true;
				}
			}
			if (changeMade)
				topCol.updateSubLayout();
		}
		if (layoutID == BOTTOM_COLUMN_ID) {
			boolean changeMade = false;

			for (int count = bottomBricks.size() - 1; count >= 0; count--) {
				GLBrick brick = bottomBricks.get(count);
				if (toBigBy < 0)
					break;
				if (!brick.isInOverviewMode()) {
					toBigBy -= brick.setToOverviewMode();
					changeMade = true;
				}
			}
			if (changeMade)
				bottomCol.updateSubLayout();
		}
	}

	public void setGlobalViewSwitching(boolean isGlobalViewSwitching) {
		this.isGlobalViewSwitching = isGlobalViewSwitching;
		for (GLBrick brick : topBricks) {
			brick.setGlobalViewSwitching(isGlobalViewSwitching);
		}
		for (GLBrick brick : bottomBricks) {
			brick.setGlobalViewSwitching(isGlobalViewSwitching);
		}
	}

	public boolean isGlobalViewSwitching() {
		return isGlobalViewSwitching;
	}
}
