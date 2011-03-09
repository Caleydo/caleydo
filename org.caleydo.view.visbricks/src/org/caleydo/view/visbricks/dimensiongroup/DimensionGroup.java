package org.caleydo.view.visbricks.dimensiongroup;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.set.Set;
import org.caleydo.core.data.group.ContentGroupList;
import org.caleydo.core.data.group.Group;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.EVAOperation;
import org.caleydo.core.data.virtualarray.delta.ContentVADelta;
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
import org.caleydo.core.view.opengl.util.GLHelperFunctions;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.draganddrop.IDraggable;
import org.caleydo.core.view.opengl.util.draganddrop.IDropArea;
import org.caleydo.view.visbricks.GLVisBricks;
import org.caleydo.view.visbricks.brick.GLBrick;

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

	private EventPublisher eventPublisher = GeneralManager.get().getEventPublisher();
	private ContentVAUpdateListener contentVAUpdateListener;
	private ReplaceContentVAListener replaceContentVAListener;

	private boolean isCollapsed = false;

	private Queue<GLBrick> uninitializedBricks = new LinkedList<GLBrick>();

	private boolean dropAfterDimensionGroup;

	public DimensionGroup(GLCaleydoCanvas canvas, ViewFrustum viewFrustum) {
		super(canvas, viewFrustum, true);

		groupColumn = new Column("dimensionGroup");

		bottomCol = new Column("dimensionGroupColumnBottom");
		bottomCol.setFrameColor(1, 0, 1, 1);
		bottomBricks = new ArrayList<GLBrick>(20);

		centerLayout = new Column("centralBrick");

		topCol = new Column("dimensionGroupColumnTop");
		topCol.setFrameColor(1, 0, 1, 1);
		topBricks = new ArrayList<GLBrick>(20);

		initGroupColumn();
	}

	private void initGroupColumn() {
		if (isCollapsed) {
			groupColumn.clear();
			groupColumn.appendElement(centerLayout);
		} else {
			groupColumn.clear();
			groupColumn.appendElement(bottomCol);
			groupColumn.appendElement(centerLayout);
			groupColumn.appendElement(topCol);
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

		ViewLayoutRenderer brickRenderer = new ViewLayoutRenderer(centerBrick);
		ElementLayout centerBrickLayout = new ElementLayout();
		centerBrickLayout.setRenderer(brickRenderer);
		centerBrickLayout.setFrameColor(1, 0, 0, 1);
		centerLayout.appendElement(centerBrickLayout);

		captionLayout = new ElementLayout("caption");
		captionLayout.setPixelGLConverter(parentGLCanvas.getPixelGLConverter());
		captionLayout.setPixelSizeY(20);
		captionLayout.setFrameColor(0, 0, 1, 1);

		DimensionGroupCaptionRenderer captionRenderer = new DimensionGroupCaptionRenderer(
				this);
		captionLayout.setRenderer(captionRenderer);

		centerLayout.appendElement(captionLayout);

		createSubBricks();
	}

	private void createSubBricks() {
		ContentVirtualArray contentVA = set.getContentData(Set.CONTENT).getContentVA();

		if (contentVA.getGroupList() == null)
			return;

		ContentGroupList groupList = contentVA.getGroupList();
		int count = 0;
		groupList.updateGroupInfo();
		for (Group group : groupList) {
			GLBrick subBrick = (GLBrick) GeneralManager.get().getViewGLCanvasManager()
					.createGLView(GLBrick.class, getParentGLCanvas(), new ViewFrustum());

			subBrick.setRemoteRenderingGLView(getRemoteRenderingGLCanvas());
			subBrick.setDataDomain(dataDomain);
			subBrick.setSet(set);
			ElementLayout brickLayout = new ElementLayout("subbrick");
			ViewLayoutRenderer brickRenderer = new ViewLayoutRenderer(subBrick);
			brickLayout.setRenderer(brickRenderer);
			brickLayout.setFrameColor(1, 0, 0, 1);
			// brickLayout.setRatioSizeY(1.0f / groupList.size());

			uninitializedBricks.add(subBrick);

			ContentVirtualArray subVA = new ContentVirtualArray("CONTENT", contentVA
					.getVirtualArray()
					.subList(group.getStartIndex(), group.getEndIndex()));

			subBrick.setContentVA(subVA);

			// float[] rep = group.getRepresentativeElement();

			if (count < groupList.size() / 2) {
				bottomBricks.add(subBrick);
				bottomCol.appendElement(brickLayout);
			} else {
				topBricks.add(subBrick);
				topCol.appendElement(brickLayout);

			}
			count++;

		}

		for (ElementLayout layout : topCol) {

			layout.setRatioSizeY(1.0f / topCol.size());
		}
		for (ElementLayout layout : bottomCol) {
			layout.setRatioSizeY(1.0f / bottomCol.size());
		}

	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		unregisterEventListeners();
	}

	/**
	 * Set the spacing of the arch: the total height in absolute gl values, the
	 * rest in ratio (i.e. the sum of the values has to be 1
	 * 
	 * @param totalArchHeight
	 *            the height of the arch from top to bottom, in abolute gl
	 *            coordinates
	 * @param below
	 *            the ratio size of the space below the arch
	 * @param archThickness
	 *            the ratio thickness in y of the arch
	 * @param above
	 *            the ratio size of the space above the arch
	 */
	public void setArchBounds(float totalArchHeight, float below, float archThickness,
			float above) {

		if (isCollapsed) {
			centerLayout.setRatioSizeY(1);
			centerLayout.setDebug(true);
		} else {
			bottomCol.setRatioSizeY(below);
			topCol.setRatioSizeY(above);
			centerLayout.setRatioSizeY(archThickness);
		}
		// brickFrustum = new ViewFrustum(ECameraProjectionMode.ORTHOGRAPHIC, 0,
		// totalArchHeight * archThickness, 0, totalArchHeight * archThickness,
		// -4,
		// 4);
		// centerBrick.setFrustum(brickFrustum);
	}

	// public void init(final GL2 gl, final AGLView glParentView,
	// final GLMouseListener glMouseListener, GLInfoAreaManager infoAreaManager)
	// {
	// centerBrick.initRemote(gl, glParentView, glMouseListener);
	//
	// for (GLBrick brick : bottomBricks) {
	// brick.initRemote(gl, glParentView, glMouseListener);
	// }
	// }

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
			bottomCol.clear();
			createSubBricks();
			topCol.updateSubLayout();
			bottomCol.updateSubLayout();
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
	}

	@Override
	public void display(GL2 gl) {
		centerBrick.processEvents();
		while (!uninitializedBricks.isEmpty()) {
			uninitializedBricks.poll().initRemote(gl, this, glMouseListener);
		}

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
		// TODO Auto-generated method stub

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

		GLHelperFunctions.drawPointAt(gl, mouseCoordinateX, mouseCoordinateY, 0);
	}

	@Override
	public void handleDrop(GL2 gl, float mouseCoordinateX, float mouseCoordinateY) {

		System.out.println("handle drop");
	}

	@Override
	public void handleDragOver(GL2 gl, java.util.Set<IDraggable> draggables,
			float mouseCoordinateX, float mouseCoordinateY) {

		((GLVisBricks) glRemoteRenderingView).highlightDimensionGroupSpacer(this,
				mouseCoordinateX, mouseCoordinateY);
	}

	@Override
	public void handleDrop(GL2 gl, java.util.Set<IDraggable> draggables,
			float mouseCoordinateX, float mouseCoordinateY,
			DragAndDropController dragAndDropController) {

		for (IDraggable draggable : draggables) {

			((GLVisBricks) glRemoteRenderingView).moveGroupDimension(this,
					(DimensionGroup) draggable, dropAfterDimensionGroup);
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
}
