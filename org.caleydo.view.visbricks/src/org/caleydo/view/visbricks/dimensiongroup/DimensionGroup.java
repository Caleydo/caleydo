package org.caleydo.view.visbricks.dimensiongroup;

import java.util.ArrayList;
import java.util.Iterator;
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
import org.caleydo.core.manager.event.data.StartClusteringEvent;
import org.caleydo.core.manager.event.view.storagebased.ContentVAUpdateEvent;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.clusterer.ClusterState;
import org.caleydo.core.view.IDataDomainSetBasedView;
import org.caleydo.core.view.opengl.camera.ECameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.canvas.listener.ContentVAUpdateListener;
import org.caleydo.core.view.opengl.canvas.listener.IContentVAUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.ReplaceContentVAListener;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.ILayoutedElement;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.ViewLayoutRenderer;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.draganddrop.IDraggable;
import org.caleydo.core.view.opengl.util.draganddrop.IDropArea;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.rcp.dialog.cluster.StartClusteringDialog;
import org.caleydo.view.visbricks.GLVisBricks;
import org.caleydo.view.visbricks.brick.BorderedAreaRenderer;
import org.caleydo.view.visbricks.brick.Button;
import org.caleydo.view.visbricks.brick.ButtonRenderer;
import org.caleydo.view.visbricks.brick.GLBrick;
import org.caleydo.view.visbricks.brick.layout.CentralBrickLayoutTemplate;
import org.eclipse.swt.widgets.Shell;

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

	private int visBricksViewID;

	private Column groupColumn;

	private ArrayList<GLBrick> bottomBricks;
	private ArrayList<GLBrick> topBricks;

	private Column bottomCol;
	private GLBrick centerBrick;
	private Column centerLayout;
	private ElementLayout captionLayout;
	private Column topCol;
	private ViewFrustum brickFrustum;
	private ISet set;
	private ASetBasedDataDomain dataDomain;
	private Button clusterButton;

	private EventPublisher eventPublisher = GeneralManager.get().getEventPublisher();
	private ContentVAUpdateListener contentVAUpdateListener;
	private ReplaceContentVAListener replaceContentVAListener;

	private boolean isCollapsed = false;

	private Queue<GLBrick> uninitializedBricks = new LinkedList<GLBrick>();

	private GLVisBricks visBricks;

	public DimensionGroup(GLCaleydoCanvas canvas, ViewFrustum viewFrustum) {
		super(canvas, viewFrustum, true);

		groupColumn = new Column("dimensionGroup");

		bottomCol = new Column("dimensionGroupColumnBottom");
		bottomCol.setFrameColor(1, 0, 1, 1);
//		bottomCol.setBottomUp(false);
//		bottomCol.setDebug(true);
		bottomBricks = new ArrayList<GLBrick>(20);

		centerLayout = new Column("centerLayout");
		// centerLayout.setFrameColor(1, 1, 0, 1);
		// centerLayout.setDebug(true);

		topCol = new Column("dimensionGroupColumnTop");
		topCol.setFrameColor(1, 0, 1, 1);
		topBricks = new ArrayList<GLBrick>(20);

		clusterButton = new Button(EPickingType.DIMENSION_GROUP_CLUSTER_BUTTON, 1);

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

		brickFrustum = new ViewFrustum(ECameraProjectionMode.ORTHOGRAPHIC, 0, 0, 0, 0,
				-4, 4);

		centerBrick = (GLBrick) GeneralManager.get().getViewGLCanvasManager()
				.createGLView(GLBrick.class, getParentGLCanvas(), brickFrustum);
		centerBrick.setRemoteRenderingGLView(getRemoteRenderingGLCanvas());
		centerBrick.setDataDomain(dataDomain);
		centerBrick.setSet(set);
		centerBrick.setContentVA(null, set.getContentData(Set.CONTENT).getContentVA());
		centerBrick.setBrickLayoutTemplate(new CentralBrickLayoutTemplate(centerBrick));
		centerBrick.initialize();

		ViewLayoutRenderer brickRenderer = new ViewLayoutRenderer(centerBrick);
		ElementLayout centerBrickLayout = new ElementLayout("CenterBrickLayout");
		centerBrickLayout.setRenderer(brickRenderer);
		// centerBrickLayout.setDebug(true);
		// centerBrickLayout.setFrameColor(1, 0, 0, 1);
		centerBrickLayout.setRatioSizeY(1f);

		centerBrick.setWrappingLayout(centerBrickLayout);

		centerLayout.setRenderer(new BorderedAreaRenderer());
		Row centerRow = new Row("centerRow");

		PixelGLConverter pixelGLConverter = parentGLCanvas.getPixelGLConverter();

		ElementLayout spacingLayoutY = new ElementLayout("spacingLayoutY");
		spacingLayoutY.setPixelGLConverter(pixelGLConverter);
		spacingLayoutY.setPixelSizeY(4);

		centerLayout.append(spacingLayoutY);
		centerLayout.append(centerRow);
		centerLayout.append(spacingLayoutY);

		ElementLayout spacingLayoutX = new ElementLayout("spacingLayoutX");
		spacingLayoutX.setPixelGLConverter(pixelGLConverter);
		spacingLayoutX.setPixelSizeX(4);

		Column centerColumn = new Column();

		centerRow.append(spacingLayoutX);
		centerRow.append(centerColumn);
		centerRow.append(spacingLayoutX);

		centerColumn.append(centerBrickLayout);

		Row captionRow = new Row();
		captionRow.setPixelGLConverter(pixelGLConverter);
		captionRow.setPixelSizeY(16);

		captionLayout = new ElementLayout("caption1");
		// captionLayout.setDebug(true);
		// captionLayout.setFrameColor(0, 0, 1, 1);
		captionLayout.setPixelGLConverter(pixelGLConverter);
		captionLayout.setPixelSizeY(18);
		// captionLayout.setRatioSizeY(0.2f);
		captionLayout.setFrameColor(0, 0, 1, 1);
		// captionLayout.setDebug(true);

		DimensionGroupCaptionRenderer captionRenderer = new DimensionGroupCaptionRenderer(
				this);
		captionLayout.setRenderer(captionRenderer);

		captionRow.append(captionLayout);
		captionRow.append(spacingLayoutX);

		ElementLayout clusterButtonLayout = new ElementLayout("clusterButton");
		clusterButtonLayout.setPixelGLConverter(pixelGLConverter);
		clusterButtonLayout.setPixelSizeX(16);
		clusterButtonLayout.setPixelSizeY(16);
		clusterButtonLayout.setRenderer(new ButtonRenderer(clusterButton, this,
				EIconTextures.CLUSTER_ICON, textureManager));

		captionRow.append(clusterButtonLayout);

		centerColumn.append(spacingLayoutY);

		ElementLayout lineSeparatorLayout = new ElementLayout("lineSeparator");
		lineSeparatorLayout.setPixelGLConverter(pixelGLConverter);
		lineSeparatorLayout.setPixelSizeY(3);
		lineSeparatorLayout.setRatioSizeX(1);
		lineSeparatorLayout.setRenderer(new LineSeparatorRenderer(false));
		// lineSeparatorLayout.setFrameColor(0, 0, 1, 1);
		// lineSeparatorLayout.setDebug(true);

		centerColumn.append(lineSeparatorLayout);
		centerColumn.append(spacingLayoutY);
		centerColumn.append(captionRow);
		// centerLayout.appendElement(spacingLayoutY);

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
			GLBrick subBrick = (GLBrick) GeneralManager.get().getViewGLCanvasManager()
					.createGLView(GLBrick.class, getParentGLCanvas(), new ViewFrustum());

			subBrick.setRemoteRenderingGLView(getRemoteRenderingGLCanvas());
			subBrick.setDataDomain(dataDomain);
			subBrick.setSet(set);
			subBrick.setVisBricks(visBricks);
			ElementLayout brickLayout = new ElementLayout("subbrick");
			ViewLayoutRenderer brickRenderer = new ViewLayoutRenderer(subBrick);
			brickLayout.setRenderer(brickRenderer);
			brickLayout.setFrameColor(1, 0, 0, 1);
			subBrick.setWrappingLayout(brickLayout);
			// brickLayout.setRatioSizeY(1.0f / groupList.size());

			ContentVirtualArray subVA = new ContentVirtualArray("CONTENT", contentVA
					.getVirtualArray().subList(group.getStartIndex(),
							group.getEndIndex() + 1));

			subBrick.setContentVA(group, subVA);
			subBrick.initialize();

			// float[] rep = group.getRepresentativeElement();

			uninitializedBricks.add(subBrick);

			if (centerBrick.getAverageValue() < subBrick.getAverageValue()) {
				insertBrick(subBrick, topBricks, topCol);
			} else {
				insertBrick(subBrick, bottomBricks, bottomCol);
			}

			// count++;

			 setOptimalLayoutSize();
//			setStaticLayoutSize();
		}

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
			// centerLayout.setDebug(true);
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clearAllSelections() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(GL2 gl) {

	}

	@Override
	protected void initLocal(GL2 gl) {
		// TODO Auto-generated method stub

	}

	@Override
	public void initRemote(GL2 gl, AGLView glParentView, GLMouseListener glMouseListener) {
		createBricks();

		centerBrick.initRemote(gl, glParentView, glMouseListener);

		for (GLBrick brick : bottomBricks) {
			brick.initRemote(gl, glParentView, glMouseListener);
		}

		for (GLBrick brick : topBricks) {
			brick.initRemote(gl, glParentView, glMouseListener);
		}

	}

	@Override
	public void display(GL2 gl) {
		// centerBrick.processEvents();
		// GLHelperFunctions.drawViewFrustum(gl, viewFrustum);
		while (!uninitializedBricks.isEmpty()) {
			uninitializedBricks.poll().initRemote(gl, this, glMouseListener);
		}
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
		case DIMENSION_GROUP_CLUSTER_BUTTON:
			if (pickingMode == EPickingMode.CLICKED) {
				System.out.println("cluster");

				getParentGLCanvas().getParentComposite().getDisplay()
						.asyncExec(new Runnable() {
							@Override
							public void run() {
								StartClusteringDialog dialog = new StartClusteringDialog(
										new Shell(), getDataDomain());
								dialog.open();
								ClusterState clusterState = dialog.getClusterState();
								if (clusterState == null)
									return;

								StartClusteringEvent event = null;
								// if (clusterState != null && set != null)

								event = new StartClusteringEvent(clusterState, getSet()
										.getID());
								event.setDataDomainType(getDataDomain()
										.getDataDomainType());
								GeneralManager.get().getEventPublisher()
										.triggerEvent(event);
							}
						});
			}
		}

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
	 * Note: The ID of the vis bricks view is needed for pushing the picking
	 * names, so that the GLVisBricks view can gets the events
	 * 
	 */
	public void setVisBricksViewID(int visBricksViewID) {
		this.visBricksViewID = visBricksViewID;
	}

	/**
	 * Note: The ID of the vis bricks view is needed for pushing the picking
	 * names, so that the GLVisBricks view can gets the events
	 * 
	 */
	public int getVisBricksViewID() {
		return visBricksViewID;
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
}
