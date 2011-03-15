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
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.ILayoutedElement;
import org.caleydo.core.view.opengl.layout.ViewLayoutRenderer;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.GLCoordinateUtils;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.draganddrop.IDraggable;
import org.caleydo.core.view.opengl.util.draganddrop.IDropArea;
import org.caleydo.view.visbricks.GLVisBricks;
import org.caleydo.view.visbricks.brick.GLBrick;
import org.caleydo.view.visbricks.brick.layout.CentralBrickLayoutTemplate;

/**
 * Container for a group of dimensions. Manages layouts as well as brick views
 * for the whole dimension group.
 * 
 * @author Alexander Lex
 * 
 */
public class DimensionGroup extends AGLView implements IDataDomainSetBasedView,
		IContentVAUpdateHandler, ILayoutedElement, IDraggable, IDropArea {

	public final static String VIEW_ID = "org.caleydo.view.dimensiongroup";

	public final static int PIXEL_PER_DIMENSION = 30;
	public final static int MIN_BRICK_WIDTH = 200;

	private GLVisBricks glVisBricksView;

	private Column groupColumn;

	private ArrayList<GLBrick> bottomBricks;
	private ArrayList<GLBrick> topBricks;

	private Column bottomCol;
	private GLBrick centerBrick;
	private Column centerLayout;
	private Column topCol;
	// private ViewFrustum brickFrustum;
	private ISet set;
	private ASetBasedDataDomain dataDomain;

	private EventPublisher eventPublisher = GeneralManager.get().getEventPublisher();
	private ContentVAUpdateListener contentVAUpdateListener;
	private ReplaceContentVAListener replaceContentVAListener;

	private boolean isCollapsed = false;

	private Queue<GLBrick> uninitializedBricks = new LinkedList<GLBrick>();

	private GLVisBricks visBricks;

	// Stuff for dragging up and down
	private boolean isDraggingActive = false;
	private float previousXCoordinate = Float.NaN;
	private float previousYCoordinate = Float.NaN;

	/** the minimal width of the brick */
	private int minPixelWidth;
	private float minWidth;

	private boolean isBrickResizeActive = false;

	public DimensionGroup(GLCaleydoCanvas canvas, ViewFrustum viewFrustum) {
		super(canvas, viewFrustum, true);

		groupColumn = new Column("dimensionGroup");
		// groupColumn.setDebug(true);

		bottomCol = new Column("dimensionGroupColumnBottom");
		bottomCol.setFrameColor(1, 0, 1, 1);
		bottomCol.setBottomUp(false);
		bottomCol.setXDynamic(true);
		// bottomCol.setDebug(true);

		bottomBricks = new ArrayList<GLBrick>(20);

		centerLayout = new Column("centerLayout");
		centerLayout.setFrameColor(1, 1, 0, 1);
		// centerLayout.setDebug(true);

		topCol = new Column("dimensionGroupColumnTop");
		topCol.setFrameColor(1, 0, 1, 1);
		topBricks = new ArrayList<GLBrick>(20);
		topCol.setXDynamic(true);
		// topCol.setDebug(true);

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
		initGroupColumn();
		// groupColumn.updateSubLayout();
	}

	private void createBricks() {
		// create basic layouts

		minPixelWidth = PIXEL_PER_DIMENSION * set.size();
		if (minPixelWidth < MIN_BRICK_WIDTH)
			minPixelWidth = MIN_BRICK_WIDTH;
		minWidth = parentGLCanvas.getPixelGLConverter().getGLWidthForPixelWidth(
				minPixelWidth);

		centerBrick = createBrick(centerLayout);
		centerBrick.setContentVA(new Group(), set.getContentData(Set.CONTENT)
				.getContentVA());
		centerBrick.setBrickLayoutTemplate(new CentralBrickLayoutTemplate(centerBrick,
				this));

		createSubBricks();
	}

	private void createSubBricks() {

		destroyOldBricks();

		ContentVirtualArray contentVA = set.getContentData(Set.CONTENT).getContentVA();

		if (contentVA.getGroupList() == null)
			return;

		ContentGroupList groupList = contentVA.getGroupList();
		// int count = 0;
		groupList.updateGroupInfo();
		for (Group group : groupList) {
			GLBrick subBrick = createBrick(new ElementLayout("subbrick"));

			ContentVirtualArray subVA = new ContentVirtualArray("CONTENT", contentVA
					.getVirtualArray().subList(group.getStartIndex(),
							group.getEndIndex() + 1));

			subBrick.setContentVA(group, subVA);

			if (centerBrick.getAverageValue() < subBrick.getAverageValue()) {
				insertBrick(subBrick, topBricks, topCol);
			} else {
				insertBrick(subBrick, bottomBricks, bottomCol);
			}

			// setOptimalLayoutSize();
			setStaticLayoutSize();

		}

	}

	GLBrick createBrick(ElementLayout wrappingLayout) {
		ViewFrustum brickFrustum = new ViewFrustum(ECameraProjectionMode.ORTHOGRAPHIC, 0,
				0, 0, 0, -4, 4);
		GLBrick brick = (GLBrick) GeneralManager.get().getViewGLCanvasManager()
				.createGLView(GLBrick.class, getParentGLCanvas(), brickFrustum);

		brick.setRemoteRenderingGLView(getRemoteRenderingGLCanvas());
		brick.setDataDomain(dataDomain);
		brick.setSet(set);
		brick.setVisBricks(visBricks);
		brick.setWrappingLayout(wrappingLayout);
		brick.setDimensionGroup(this);
		brick.initialize();
		uninitializedBricks.add(brick);

		ViewLayoutRenderer brickRenderer = new ViewLayoutRenderer(brick);
		wrappingLayout.setRenderer(brickRenderer);
		wrappingLayout.setPixelGLConverter(parentGLCanvas.getPixelGLConverter());
		wrappingLayout.setPixelSizeX(minPixelWidth);
		// wrappingLayout.setPixelMinSizeY(30);
		// wrappingLayout.setDebug(true);

		return brick;
	}

	private void setOptimalLayoutSize() {
		for (ElementLayout layout : topCol) {

			layout.setRatioSizeY(1.0f / topCol.size());
		}
		for (ElementLayout layout : bottomCol) {
			layout.setRatioSizeY(1.0f / bottomCol.size());
		}
	}

	private void setStaticLayoutSize() {

		int elementCount = topCol.size() > bottomCol.size() ? topCol.size() : bottomCol
				.size();

		for (ElementLayout layout : topCol) {

			layout.setAbsoluteSizeY(0.5f);
		}
		for (ElementLayout layout : bottomCol) {
			layout.setAbsoluteSizeY(0.5f);
		}
	}

	private void setDynamicLayoutSize() {

	}

	private void insertBrick(GLBrick subBrick, ArrayList<GLBrick> bricks, Column layout) {

		int count;
		for (count = 0; count < bricks.size(); count++) {
			if (bricks.get(count).getAverageValue() > subBrick.getAverageValue())
				break;
		}
		bricks.add(count, subBrick);
		layout.add(count, subBrick.getWrappingLayout());
	}

	private void destroyOldBricks() {
		for (GLBrick brick : topBricks) {
			GeneralManager.get().getViewGLCanvasManager().unregisterGLView(brick);
			brick.unregisterEventListeners();
			brick.destroy();
		}
		for (GLBrick brick : bottomBricks) {
			GeneralManager.get().getViewGLCanvasManager().unregisterGLView(brick);
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
	 * Set the spacing of the arch in ratio (i.e. the sum of the values has to
	 * be 1
	 * </p>
	 * <p>
	 * This is only used if the group is not collapsed. If it is collapsed, the
	 * values are irrelevant.
	 * </p>
	 * 
	 * @param below
	 *            the ratio size of the space below the arch
	 * @param archThickness
	 *            the ratio thickness in y of the arch
	 * @param above
	 *            the ratio size of the space above the arch
	 */
	public void setArchBounds(float below, float archThickness, float above) {

		if (isCollapsed) {
			centerLayout.setRatioSizeY(1);
		} else {
			bottomCol.setRatioSizeY(below);
			topCol.setRatioSizeY(above);
			centerLayout.setRatioSizeY(archThickness);
		}

	}

	@Override
	public void registerEventListeners() {

		contentVAUpdateListener = new ContentVAUpdateListener();
		contentVAUpdateListener.setHandler(this);
		contentVAUpdateListener
				.setExclusiveDataDomainType(dataDomain.getDataDomainType());
		eventPublisher.addListener(ContentVAUpdateEvent.class, contentVAUpdateListener);

		replaceContentVAListener = new ReplaceContentVAListener();
		replaceContentVAListener.setHandler(this);
		replaceContentVAListener.setExclusiveDataDomainType(dataDomain
				.getDataDomainType());
		eventPublisher.addListener(ReplaceContentVAEvent.class, replaceContentVAListener);

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
	}

	@Override
	public void handleVAUpdate(ContentVADelta vaDelta, String info) {
	}

	@Override
	public void replaceContentVA(int setID, String dataDomainType, String vaType) {

		if (set.getID() == setID) {
			topCol.clear();
			topBricks.clear();
			bottomCol.clear();
			bottomBricks.clear();
			createSubBricks();
			topCol.updateSubLayout();
			bottomCol.updateSubLayout();

			visBricks.initiConnectionLinesBetweenDimensionGroups();
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
	public void initRemote(GL2 gl, AGLView glParentView, GLMouseListener glMouseListener) {
		createBricks();

	}

	@Override
	public void display(GL2 gl) {
		while (!uninitializedBricks.isEmpty()) {
			uninitializedBricks.poll().initRemote(gl, this, glMouseListener);
		}
		handleSizeDragging(gl);
		handleBrickResize(gl);
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
				isDraggingActive = true;
			}
			break;
		case RESIZE_HANDLE_LOWER_RIGHT:
			if (pickingMode == EPickingMode.CLICKED) {
				isBrickResizeActive = true;
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
		gl.glVertex2f(mouseCoordinateX, mouseCoordinateY);
		gl.glVertex2f(mouseCoordinateX + 1, mouseCoordinateY);
		gl.glVertex2f(mouseCoordinateX + 1, mouseCoordinateY + 1);
		gl.glVertex2f(mouseCoordinateX, mouseCoordinateY + 1);
		gl.glEnd();

	}

	@Override
	public void handleDrop(GL2 gl, float mouseCoordinateX, float mouseCoordinateY) {

		System.out.println("handle drop");
	}

	private void handleSizeDragging(GL2 gl) {
		if (!isDraggingActive)
			return;
		if (glMouseListener.wasMouseReleased()) {
			isDraggingActive = false;
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

		float topSize = topCol.getSizeScaledY();
		topCol.setAbsoluteSizeY(topSize - change);

		float bottomSize = bottomCol.getSizeScaledY();
		bottomCol.setAbsoluteSizeY(bottomSize + change);
		float centerSize = centerLayout.getSizeScaledY();

		centerLayout.setAbsoluteSizeY(centerSize);

		centerLayout.updateSubLayout();
		groupColumn.updateSubLayout();
		visBricks.initiConnectionLinesBetweenDimensionGroups();

	}

	private void handleBrickResize(GL2 gl) {
		if (!isBrickResizeActive)
			return;
		if (glMouseListener.wasMouseReleased()) {
			isBrickResizeActive = false;
			previousXCoordinate = Float.NaN;
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

		float changeX = pointCordinates[0] - previousXCoordinate;

		float width = centerLayout.getSizeScaledX();
		float changePercentage = changeX / width;

		float newWidth = width + changeX;
		if (newWidth < minWidth)
			return;

		previousXCoordinate = pointCordinates[0];

		centerLayout.setAbsoluteSizeX(newWidth);
		groupColumn.setAbsoluteSizeX(width + changeX);

		float height = centerLayout.getSizeScaledY();
		centerLayout.setAbsoluteSizeY(height * (1 + changePercentage));

		centerBrick.getWrappingLayout().updateSubLayout();

		visBricks.updateLayout();
		visBricks.initiConnectionLinesBetweenDimensionGroups();

	}

	@Override
	public void handleDragOver(GL2 gl, java.util.Set<IDraggable> draggables,
			float mouseCoordinateX, float mouseCoordinateY) {

		visBricks.highlightDimensionGroupSpacer(this, mouseCoordinateX, mouseCoordinateY);
	}

	@Override
	public void handleDrop(GL2 gl, java.util.Set<IDraggable> draggables,
			float mouseCoordinateX, float mouseCoordinateY,
			DragAndDropController dragAndDropController) {

		for (IDraggable draggable : draggables) {

			if (draggable == this)
				break;

			visBricks.moveGroupDimension(this, (DimensionGroup) draggable);
		}

		draggables.clear();
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

	ISet getSet() {
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
		for (GLBrick brick : bottomBricks) {
			bricks.add(brick);
		}
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

}
