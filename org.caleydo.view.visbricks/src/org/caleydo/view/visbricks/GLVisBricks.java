package org.caleydo.view.visbricks;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.graph.tree.ClusterTree;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.virtualarray.EVAOperation;
import org.caleydo.core.data.virtualarray.similarity.RelationAnalyzer;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
import org.caleydo.core.manager.event.data.NewMetaSetsEvent;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.IDataDomainSetBasedView;
import org.caleydo.core.view.opengl.camera.ECameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.DetailLevel;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutManager;
import org.caleydo.core.view.opengl.layout.LayoutTemplate;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.spline.ConnectionBandRenderer;
import org.caleydo.core.view.opengl.util.spline.IConnectionRenderer;
import org.caleydo.core.view.opengl.util.vislink.NURBSCurve;
import org.caleydo.view.visbricks.dimensiongroup.DimensionGroup;
import org.caleydo.view.visbricks.dimensiongroup.DimensionGroupManager;
import org.caleydo.view.visbricks.dimensiongroup.DimensionGroupSpacingRenderer;
import org.caleydo.view.visbricks.listener.NewMetaSetsListener;
import org.caleydo.view.visbricks.renderstyle.VisBricksRenderStyle;

/**
 * VisBricks main view
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */

public class GLVisBricks extends AGLView implements IGLRemoteRenderingView,
		IViewCommandHandler, ISelectionUpdateHandler, IDataDomainSetBasedView {

	public final static String VIEW_ID = "org.caleydo.view.visbricks";

	private final static float ARCH_TOP_PERCENT = 0.6f;
	private final static float ARCH_BOTTOM_PERCENT = 0.4f;
	private final static float ARCH_STAND_WIDTH_PERCENT = 0.05f;

	private final static int DIMENSION_GROUP_SPACING = 30;

	private NewMetaSetsListener metaSetsListener;

	private ASetBasedDataDomain dataDomain;

	private VisBricksRenderStyle renderStyle;

	private DimensionGroupManager dimensionGroupManager;

	private IConnectionRenderer connectionRenderer;

	private LayoutManager centerLayoutManager;
	private LayoutManager leftLayoutManager;
	private LayoutManager rightLayoutManager;

	private LayoutTemplate centerLayout;
	private LayoutTemplate leftLayout;
	private LayoutTemplate rightLayout;

	private Row centerRowLayout;
	private Column leftColumnLayout;
	private Column rightColumnLayout;

	/** thickness of the arch at the sided */
	private float archSideThickness = 0;
	private float archInnerWidth = 0;
	private float archTopY = 0;
	private float archBottomY = 0;
	private float archHeight = 0;

	private Queue<DimensionGroup> uninitializedDimensionGroups = new LinkedList<DimensionGroup>();

	private DragAndDropController dragAndDropController;

	private boolean dropDimensionGroupAfter = true;

	private RelationAnalyzer relationAnalyzer;

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param sLabel
	 * @param viewFrustum
	 */
	public GLVisBricks(GLCaleydoCanvas glCanvas, final ViewFrustum viewFrustum) {
		super(glCanvas, viewFrustum, true);

		viewType = GLVisBricks.VIEW_ID;

		connectionRenderer = new ConnectionBandRenderer();

		dragAndDropController = new DragAndDropController(this);

		dimensionGroupManager = new DimensionGroupManager();
	}

	@Override
	public void init(GL2 gl) {
		dataDomain.createContentRelationAnalyzer();
		relationAnalyzer = dataDomain.getContentRelationAnalyzer();

		// renderStyle = new GeneralRenderStyle(viewFrustum);
		renderStyle = new VisBricksRenderStyle(viewFrustum);

		super.renderStyle = renderStyle;
		detailLevel = DetailLevel.HIGH;
		metaSetsUpdated();
		connectionRenderer.init(gl);

		// initLayout();
	}

	private void initLayoutCenter() {

		archSideThickness = viewFrustum.getWidth() * ARCH_STAND_WIDTH_PERCENT;
		archInnerWidth = viewFrustum.getWidth() * (ARCH_STAND_WIDTH_PERCENT + 0.05f);
		archTopY = viewFrustum.getHeight() * ARCH_TOP_PERCENT;
		archBottomY = viewFrustum.getHeight() * ARCH_BOTTOM_PERCENT;
		archHeight = (ARCH_TOP_PERCENT - ARCH_BOTTOM_PERCENT) * viewFrustum.getHeight();

		float centerLayoutWidth = viewFrustum.getWidth() - 2 * archInnerWidth;

		float spacerWidth = (centerLayoutWidth - (dimensionGroupManager
				.getRightGroupStartIndex() - dimensionGroupManager
				.getCenterGroupStartIndex())
				* archHeight)
				/ (dimensionGroupManager.getRightGroupStartIndex()
						- dimensionGroupManager.getCenterGroupStartIndex() - 1);

		centerRowLayout = new Row("centerArchRow");
		centerRowLayout.setFrameColor(1, 1, 0, 1);
		// centerRowLayout.setDebug(false);

		ElementLayout dimensionGroupSpacing = new ElementLayout("dimensionGroupSpacing");
		DimensionGroupSpacingRenderer dimensionGroupSpacingRenderer = new DimensionGroupSpacingRenderer();
		dimensionGroupSpacing.setRenderer(dimensionGroupSpacingRenderer);
		dimensionGroupSpacingRenderer.setLineLength(archHeight);

		dimensionGroupSpacing.setPixelGLConverter(parentGLCanvas.getPixelGLConverter());
		dimensionGroupSpacing.setPixelSizeX(2);
		// dimensionGroupSpacing.setDebug(false);
		centerRowLayout.appendElement(dimensionGroupSpacing);

		for (int dimensionGroupIndex = dimensionGroupManager.getCenterGroupStartIndex(); dimensionGroupIndex < dimensionGroupManager
				.getRightGroupStartIndex(); dimensionGroupIndex++) {

			DimensionGroup group = dimensionGroupManager.getDimensionGroups().get(
					dimensionGroupIndex);
			group.setCollapsed(false);
			group.getLayout().setAbsoluteSizeX(archHeight);
			group.getLayout().setRatioSizeY(1);
			group.setArchBounds(ARCH_BOTTOM_PERCENT, ARCH_TOP_PERCENT
					- ARCH_BOTTOM_PERCENT, ARCH_BOTTOM_PERCENT);
			centerRowLayout.appendElement(group.getLayout());

			dimensionGroupSpacing = new ElementLayout("dimensionGroupSpacing");

			if (dimensionGroupIndex != dimensionGroupManager.getRightGroupStartIndex() - 1)
			{	dimensionGroupSpacingRenderer = new DimensionGroupSpacingRenderer(
						relationAnalyzer, group, dimensionGroupManager
								.getDimensionGroups().get(dimensionGroupIndex + 1));
			}
			else 
				dimensionGroupSpacingRenderer = new DimensionGroupSpacingRenderer();
			
			dimensionGroupSpacingRenderer.setLineLength(archHeight);
			dimensionGroupSpacing.setRenderer(dimensionGroupSpacingRenderer);
			dimensionGroupSpacing.setPixelGLConverter(parentGLCanvas
					.getPixelGLConverter());
			// dimensionGroupSpacing.setDebug(false);
			if (dimensionGroupIndex == dimensionGroupManager.getRightGroupStartIndex() - 1)
				dimensionGroupSpacing.setPixelSizeX(2);
			else
				dimensionGroupSpacing.setAbsoluteSizeX(spacerWidth);
			centerRowLayout.appendElement(dimensionGroupSpacing);
		}

		centerLayout = new LayoutTemplate();
		centerLayout.setPixelGLConverter(parentGLCanvas.getPixelGLConverter());
		centerLayout.setBaseElementLayout(centerRowLayout);

		ViewFrustum centerArchFrustum = new ViewFrustum(viewFrustum.getProjectionMode(),
				0, centerLayoutWidth, 0, viewFrustum.getHeight(), 0, 1);
		centerLayoutManager = new LayoutManager(centerArchFrustum);
		centerLayoutManager.setTemplate(centerLayout);

		centerLayoutManager.updateLayout();
	}

	private void initLayoutLeft() {

		leftColumnLayout = new Column("leftArchColumn");
		leftColumnLayout.setFrameColor(1, 1, 0, 1);
		// leftColumnLayout.setDebug(false);
		leftColumnLayout.setBottomUp(false);

		ElementLayout dimensionGroupSpacing = new ElementLayout("dimensionGroupSpacing");
		DimensionGroupSpacingRenderer dimensionGroupSpacingRenderer = new DimensionGroupSpacingRenderer();
		dimensionGroupSpacingRenderer.setVertical(false);
		dimensionGroupSpacingRenderer.setLineLength(archSideThickness);
		dimensionGroupSpacing.setRenderer(dimensionGroupSpacingRenderer);
		dimensionGroupSpacing.setPixelGLConverter(parentGLCanvas.getPixelGLConverter());
		dimensionGroupSpacing.setPixelSizeY(DIMENSION_GROUP_SPACING);
		leftColumnLayout.appendElement(dimensionGroupSpacing);

		for (int dimensionGroupIndex = 0; dimensionGroupIndex < dimensionGroupManager
				.getCenterGroupStartIndex(); dimensionGroupIndex++) {

			DimensionGroup group = dimensionGroupManager.getDimensionGroups().get(
					dimensionGroupIndex);

			group.getLayout().setRatioSizeX(1);
			group.getLayout().setAbsoluteSizeY(archSideThickness);
			// group.getLayout().setDebug(false);
			group.setArchBounds(0, 0, 0);
			leftColumnLayout.appendElement(group.getLayout());

			group.setCollapsed(true);

			dimensionGroupSpacing = new ElementLayout("dimensionGroupSpacing");
			dimensionGroupSpacingRenderer = new DimensionGroupSpacingRenderer();
			dimensionGroupSpacingRenderer.setVertical(false);
			dimensionGroupSpacingRenderer.setLineLength(archSideThickness);
			dimensionGroupSpacing.setRenderer(dimensionGroupSpacingRenderer);
			dimensionGroupSpacing.setPixelGLConverter(parentGLCanvas
					.getPixelGLConverter());
			dimensionGroupSpacing.setPixelSizeY(DIMENSION_GROUP_SPACING);
			leftColumnLayout.appendElement(dimensionGroupSpacing);
		}

		leftLayout = new LayoutTemplate();
		leftLayout.setPixelGLConverter(parentGLCanvas.getPixelGLConverter());
		leftLayout.setBaseElementLayout(leftColumnLayout);

		ViewFrustum leftArchFrustum = new ViewFrustum(viewFrustum.getProjectionMode(), 0,
				archSideThickness, 0, archBottomY, 0, 1);
		leftLayoutManager = new LayoutManager(leftArchFrustum);
		leftLayoutManager.setTemplate(leftLayout);

		leftLayoutManager.updateLayout();
	}

	private void initLayoutRight() {

		rightColumnLayout = new Column("rightArchColumn");
		rightColumnLayout.setFrameColor(1, 1, 0, 1);
		rightColumnLayout.setBottomUp(false);
		// rightColumnLayout.setDebug(false);

		ElementLayout dimensionGroupSpacing = new ElementLayout("dimensionGroupSpacing");
		DimensionGroupSpacingRenderer dimensionGroupSpacingRenderer = new DimensionGroupSpacingRenderer();
		dimensionGroupSpacingRenderer.setVertical(false);
		dimensionGroupSpacingRenderer.setLineLength(archSideThickness);
		dimensionGroupSpacing.setRenderer(dimensionGroupSpacingRenderer);
		dimensionGroupSpacing.setPixelGLConverter(parentGLCanvas.getPixelGLConverter());
		dimensionGroupSpacing.setPixelSizeY(5);
		rightColumnLayout.appendElement(dimensionGroupSpacing);

		for (int dimensionGroupIndex = dimensionGroupManager.getRightGroupStartIndex(); dimensionGroupIndex < dimensionGroupManager
				.getDimensionGroups().size(); dimensionGroupIndex++) {

			DimensionGroup group = dimensionGroupManager.getDimensionGroups().get(
					dimensionGroupIndex);
			group.setCollapsed(true);

			group.getLayout().setRatioSizeX(1);
			// since this should be a square, we set the height as width here
			group.getLayout().setAbsoluteSizeY(archSideThickness);
			// group.getLayout().setDebug(true);
			group.setArchBounds(0, 0, 0);
			rightColumnLayout.appendElement(group.getLayout());

			dimensionGroupSpacing = new ElementLayout("dimensionGroupSpacing");
			dimensionGroupSpacingRenderer = new DimensionGroupSpacingRenderer();
			dimensionGroupSpacingRenderer.setVertical(false);
			dimensionGroupSpacingRenderer.setLineLength(archSideThickness);
			dimensionGroupSpacing.setRenderer(dimensionGroupSpacingRenderer);
			dimensionGroupSpacing.setPixelGLConverter(parentGLCanvas
					.getPixelGLConverter());
			dimensionGroupSpacing.setPixelSizeY(DIMENSION_GROUP_SPACING);
			rightColumnLayout.appendElement(dimensionGroupSpacing);
		}

		rightLayout = new LayoutTemplate();
		rightLayout.setPixelGLConverter(parentGLCanvas.getPixelGLConverter());
		rightLayout.setBaseElementLayout(rightColumnLayout);

		ViewFrustum leftArchFrustum = new ViewFrustum(viewFrustum.getProjectionMode(), 0,
				archSideThickness, 0, archBottomY, 0, 1);
		rightLayoutManager = new LayoutManager(leftArchFrustum);
		rightLayoutManager.setTemplate(rightLayout);

		rightLayoutManager.updateLayout();

	}

	@Override
	public void initLocal(GL2 gl) {
		init(gl);
	}

	@Override
	public void initRemote(final GL2 gl, final AGLView glParentView,
			final GLMouseListener glMouseListener) {

		// Register keyboard listener to GL2 canvas
		glParentView.getParentGLCanvas().getParentComposite().getDisplay()
				.asyncExec(new Runnable() {
					@Override
					public void run() {
						glParentView.getParentGLCanvas().getParentComposite()
								.addKeyListener(glKeyListener);
					}
				});

		this.glMouseListener = glMouseListener;

		iGLDisplayListIndexRemote = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexRemote;
		init(gl);
	}

	@Override
	public void displayLocal(GL2 gl) {
		if (!uninitializedDimensionGroups.isEmpty()) {
			while (uninitializedDimensionGroups.peek() != null) {
				uninitializedDimensionGroups.poll().initRemote(gl, this, glMouseListener);

			}
			initLayoutCenter();
			initLayoutLeft();
			initLayoutRight();

		}

		for (DimensionGroup group : dimensionGroupManager.getDimensionGroups()) {
			group.processEvents();
		}
		// brick.display(gl);
		pickingManager.handlePicking(this, gl);
		display(gl);
		checkForHits(gl);
	}

	@Override
	public void displayRemote(GL2 gl) {

	}

	@Override
	public void display(GL2 gl) {

		renderArch(gl);

		for (DimensionGroup dimensionGroup : dimensionGroupManager.getDimensionGroups()) {
			dimensionGroup.display(gl);
		}

		leftLayoutManager.render(gl);

		gl.glTranslatef(archInnerWidth, 0, 0);
		centerLayoutManager.render(gl);
		gl.glTranslatef(-archInnerWidth, 0, 0);

		float rightArchStand = (1 - ARCH_STAND_WIDTH_PERCENT) * viewFrustum.getWidth();
		gl.glTranslatef(rightArchStand, 0, 0);
		rightLayoutManager.render(gl);
		gl.glTranslatef(-rightArchStand, 0, 0);

		// call after all other rendering because it calls the onDrag methods
		// which need alpha blending...
		dragAndDropController.handleDragging(gl, glMouseListener);
	}

	private void renderArch(GL2 gl) {
		gl.glColor3f(1, 0, 0);
		gl.glColor4f(0.5f, 0.5f, 0.5f, 1f);

		// Left arch

		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex3f(0, 0, 0f);
		gl.glVertex3f(0, archBottomY, 0f);
		gl.glVertex3f(archSideThickness, archBottomY, 0f);
		gl.glVertex3f(archSideThickness, 0, 0f);
		gl.glEnd();

		ArrayList<Vec3f> inputPoints = new ArrayList<Vec3f>();
		inputPoints.add(new Vec3f(0, archBottomY, 0));
		inputPoints.add(new Vec3f(0, archTopY, 0));
		inputPoints.add(new Vec3f(archInnerWidth * 0.9f, archTopY, 0));

		NURBSCurve curve = new NURBSCurve(inputPoints, 10);
		ArrayList<Vec3f> outputPoints = curve.getCurvePoints();

		outputPoints.add(new Vec3f(archInnerWidth, archTopY, 0));
		outputPoints.add(new Vec3f(archInnerWidth, archBottomY, 0));

		inputPoints.clear();
		inputPoints.add(new Vec3f(archInnerWidth, archBottomY, 0));
		inputPoints.add(new Vec3f(archSideThickness, archBottomY, 0));
		inputPoints.add(new Vec3f(archSideThickness, archBottomY * 0.8f, 0));

		curve = new NURBSCurve(inputPoints, 10);
		outputPoints.addAll(curve.getCurvePoints());

		connectionRenderer.render(gl, outputPoints);

		// Right arch

		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex3f(viewFrustum.getWidth(), 0, 0f);
		gl.glVertex3f(viewFrustum.getWidth(), archBottomY, 0f);
		gl.glVertex3f(viewFrustum.getWidth() - archSideThickness, archBottomY, 0f);
		gl.glVertex3f(viewFrustum.getWidth() - archSideThickness, 0, 0f);
		gl.glEnd();

		inputPoints.clear();
		inputPoints.add(new Vec3f(viewFrustum.getWidth(), archBottomY, 0));
		inputPoints.add(new Vec3f(viewFrustum.getWidth(), archTopY, 0));
		inputPoints.add(new Vec3f(viewFrustum.getWidth() - archInnerWidth * 0.9f,
				archTopY, 0));

		curve = new NURBSCurve(inputPoints, 10);
		outputPoints.clear();
		outputPoints.addAll(curve.getCurvePoints());

		outputPoints.add(new Vec3f(viewFrustum.getWidth() - archInnerWidth, archTopY, 0));
		outputPoints.add(new Vec3f(viewFrustum.getWidth() - archInnerWidth, archBottomY,
				0));

		inputPoints.clear();
		inputPoints
				.add(new Vec3f(viewFrustum.getWidth() - archInnerWidth, archBottomY, 0));
		inputPoints.add(new Vec3f(viewFrustum.getWidth() - archSideThickness,
				archBottomY, 0));
		inputPoints.add(new Vec3f(viewFrustum.getWidth() - archSideThickness,
				archBottomY * 0.8f, 0));

		curve = new NURBSCurve(inputPoints, 10);
		outputPoints.addAll(curve.getCurvePoints());

		connectionRenderer.render(gl, outputPoints);

		// Arch top bar
		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex3f(archInnerWidth, archTopY, 0f);
		gl.glVertex3f(archInnerWidth, archBottomY, 0f);
		gl.glVertex3f(viewFrustum.getWidth() - archInnerWidth, archBottomY, 0f);
		gl.glVertex3f(viewFrustum.getWidth() - archInnerWidth, archTopY, 0f);
		gl.glEnd();
	}

	@Override
	protected void handlePickingEvents(EPickingType pickingType,
			EPickingMode pickingMode, int externalID, Pick pick) {

		if (detailLevel == DetailLevel.VERY_LOW) {
			return;
		}

		switch (pickingType) {

		case DIMENSION_GROUP:
			switch (pickingMode) {
			case MOUSE_OVER:

				System.out.println("Mouse over");
				break;
			case CLICKED:

				dragAndDropController.setDraggingStartPosition(pick.getPickedPoint());
				dragAndDropController.addDraggable((DimensionGroup) generalManager
						.getViewGLCanvasManager().getGLView(externalID));
				break;
			case RIGHT_CLICKED:
				break;
			case DRAGGED:
				if (dragAndDropController.hasDraggables()) {
					if (glMouseListener.wasRightMouseButtonPressed())
						dragAndDropController.clearDraggables();
					else if (!dragAndDropController.isDragging())
						dragAndDropController.startDragging();
				}

				DimensionGroup currentDimGroup = (DimensionGroup) generalManager
						.getViewGLCanvasManager().getGLView(externalID);
				dragAndDropController.setDropArea(currentDimGroup);
				break;

			}
		}

	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedVisBricksView serializedForm = new SerializedVisBricksView(
				dataDomain.getDataDomainType());
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

	@Override
	public String toString() {
		return "TODO: ADD INFO THAT APPEARS IN THE LOG";
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();

		metaSetsListener = new NewMetaSetsListener();
		metaSetsListener.setHandler(this);
		eventPublisher.addListener(NewMetaSetsEvent.class, metaSetsListener);

	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();

		if (metaSetsListener != null) {
			eventPublisher.removeListener(metaSetsListener);
			metaSetsListener = null;
		}
	}

	@Override
	public void handleSelectionUpdate(ISelectionDelta selectionDelta,
			boolean scrollToSelection, String info) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleRedrawView() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleUpdateView() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleClearSelections() {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearAllSelections() {
		// TODO Auto-generated method stub

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

	@Override
	public List<AGLView> getRemoteRenderedViews() {
		return null;
	}

	public void metaSetsUpdated() {

		ClusterTree storageTree = dataDomain.getSet().getStorageData(storageVAType)
				.getStorageTree();
		if (storageTree == null)
			return;

		ArrayList<ISet> allMetaSets = storageTree.getRoot().getAllMetaSetsFromSubTree();

		ArrayList<ISet> filteredMetaSets = new ArrayList<ISet>(allMetaSets.size() / 2);

		for (ISet metaSet : allMetaSets) {
			if (metaSet.size() > 1 && metaSet.size() != dataDomain.getSet().size())
				filteredMetaSets.add(metaSet);
		}
		initializeBricks(filteredMetaSets);

	}

	private void initializeBricks(ArrayList<ISet> metaSets) {

		ArrayList<DimensionGroup> dimensionGroups = dimensionGroupManager
				.getDimensionGroups();
		dimensionGroups.clear();

		for (ISet set : metaSets) {

			// TODO here we need to check which metaSets have already been
			// assigned to a dimensiongroup and not re-create them
			DimensionGroup dimensionGroup = (DimensionGroup) GeneralManager
					.get()
					.getViewGLCanvasManager()
					.createGLView(
							DimensionGroup.class,
							getParentGLCanvas(),
							new ViewFrustum(ECameraProjectionMode.ORTHOGRAPHIC, 0, 1, 0,
									1, -1, 1));

			dimensionGroup.setDataDomain(dataDomain);
			dimensionGroup.setSet(set);
			dimensionGroup.setRemoteRenderingGLView(this);
			dimensionGroup.setVisBricks(this);
			dimensionGroup.setVisBricksViewID(uniqueID);
			dimensionGroup.initialize();

			dimensionGroups.add(dimensionGroup);

			uninitializedDimensionGroups.add(dimensionGroup);

		}

		dimensionGroupManager.calculateGroupDivision();
	}

	@Override
	public void setDataDomain(ASetBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

	@Override
	public ASetBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	@Override
	public String getShortInfo() {

		return "LayoutTemplate Caleydo View";
	}

	@Override
	public String getDetailedInfo() {
		return "LayoutTemplate Caleydo View";

	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

		super.reshape(drawable, x, y, width, height);

		initLayoutCenter();
		initLayoutLeft();
		initLayoutRight();
	}

	@Override
	public void setDisplayListDirty() {
		super.setDisplayListDirty();
	}

	public void moveGroupDimension(DimensionGroup referenceDimGroup,
			DimensionGroup movedDimGroup) {

		dimensionGroupManager.moveGroupDimension(referenceDimGroup, movedDimGroup,
				dropDimensionGroupAfter);

		initLayoutCenter();
		initLayoutLeft();
		initLayoutRight();
	}

	public void highlightDimensionGroupSpacer(DimensionGroup dragOverDimensionGroup,
			float mouseX, float mouseY) {

		// Clear previous spacer highlights
		for (ElementLayout element : centerRowLayout.getElements()) {
			if (element.getRenderer() instanceof DimensionGroupSpacingRenderer)
				((DimensionGroupSpacingRenderer) element.getRenderer())
						.setRenderSpacer(false);
		}

		for (ElementLayout element : leftColumnLayout.getElements()) {
			if (element.getRenderer() instanceof DimensionGroupSpacingRenderer)
				((DimensionGroupSpacingRenderer) element.getRenderer())
						.setRenderSpacer(false);
		}

		for (ElementLayout element : rightColumnLayout.getElements()) {
			if (element.getRenderer() instanceof DimensionGroupSpacingRenderer)
				((DimensionGroupSpacingRenderer) element.getRenderer())
						.setRenderSpacer(false);
		}

		dropDimensionGroupAfter = false;

		int hightlightOffset = -1;

		if (centerRowLayout.getElements().contains(dragOverDimensionGroup.getLayout())) {

			if ((dragOverDimensionGroup.getLayout().getTranslateX() + dragOverDimensionGroup
					.getLayout().getSizeScaledX()) < mouseX)
				dropDimensionGroupAfter = true;
			else
				dropDimensionGroupAfter = false;
		}

		if (dropDimensionGroupAfter)
			hightlightOffset = +1;

		ElementLayout spacingElement;

		if (centerRowLayout.getElements().contains(dragOverDimensionGroup.getLayout())) {

			spacingElement = centerRowLayout.getElements().get(
					centerRowLayout.getElements().indexOf(
							dragOverDimensionGroup.getLayout())
							+ hightlightOffset);

			((DimensionGroupSpacingRenderer) spacingElement.getRenderer())
					.setRenderSpacer(true);
		}

		if ((leftColumnLayout.getElements().contains(dragOverDimensionGroup.getLayout()) || rightColumnLayout
				.getElements().contains(dragOverDimensionGroup.getLayout()))) {

			if ((dragOverDimensionGroup.getLayout().getTranslateY() + dragOverDimensionGroup
					.getLayout().getSizeScaledY()) < mouseY)
				dropDimensionGroupAfter = true;
			else
				dropDimensionGroupAfter = false;
		}

		if (dropDimensionGroupAfter)
			hightlightOffset = +1;

		if (leftColumnLayout.getElements().contains(dragOverDimensionGroup.getLayout())) {

			spacingElement = leftColumnLayout.getElements().get(
					leftColumnLayout.getElements().indexOf(
							dragOverDimensionGroup.getLayout())
							- hightlightOffset);

			((DimensionGroupSpacingRenderer) spacingElement.getRenderer())
					.setRenderSpacer(true);
		}

		if (rightColumnLayout.getElements().contains(dragOverDimensionGroup.getLayout())) {

			spacingElement = rightColumnLayout.getElements().get(
					rightColumnLayout.getElements().indexOf(
							dragOverDimensionGroup.getLayout())
							- hightlightOffset);

			((DimensionGroupSpacingRenderer) spacingElement.getRenderer())
					.setRenderSpacer(true);
		}
	}

	public DimensionGroupManager getDimensionGroupManager() {
		return dimensionGroupManager;
	}
}
