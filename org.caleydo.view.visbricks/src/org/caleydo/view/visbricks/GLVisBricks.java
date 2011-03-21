package org.caleydo.view.visbricks;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.graph.tree.ClusterTree;
import org.caleydo.core.data.mapping.IDType;
import org.caleydo.core.data.selection.ContentSelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.SelectionTypeEvent;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.virtualarray.EVAOperation;
import org.caleydo.core.data.virtualarray.similarity.RelationAnalyzer;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
import org.caleydo.core.manager.event.data.NewMetaSetsEvent;
import org.caleydo.core.manager.event.data.RelationsUpdatedEvent;
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

	private final static int ARCH_PIXEL_HEIGHT = 150;
	private final static float ARCH_BOTTOM_PERCENT = 0.5f;
	private final static float ARCH_STAND_WIDTH_PERCENT = 0.05f;

	private final static int DIMENSION_GROUP_SPACING = 30;

	private NewMetaSetsListener metaSetsListener;

	private ASetBasedDataDomain dataDomain;

	private VisBricksRenderStyle renderStyle;

	private DimensionGroupManager dimensionGroupManager;

	private ConnectionBandRenderer connectionRenderer;

	private LayoutManager centerLayoutManager;
	private LayoutManager leftLayoutManager;
	private LayoutManager rightLayoutManager;

	private LayoutTemplate centerLayout;
	private LayoutTemplate leftLayoutTemplate;
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

	/** Flag signalling if a group needs to be moved out of the center */
	boolean resizeNecessary = false;
	boolean lastResizeDirectionWasToLeft = true;

	boolean isLayoutDirty = false;

	private Queue<DimensionGroup> uninitializedDimensionGroups = new LinkedList<DimensionGroup>();

	private DragAndDropController dragAndDropController;

	private boolean dropDimensionGroupAfter = true;

	private RelationAnalyzer relationAnalyzer;

	private ElementLayout leftDimensionGroupSpacing;
	private ElementLayout rightDimensionGroupSpacing;

	private ContentSelectionManager contentSelectionManager;
	
	private SelectionType selectedByGroupSelectionType = new SelectionType("Selected by group",
			new float[] { 0, 0, 1, 1}, 1, false, true, 1);

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

		contentSelectionManager = dataDomain.getContentSelectionManager();
		
		SelectionTypeEvent selectionTypeEvent = new SelectionTypeEvent(
				selectedByGroupSelectionType);
		eventPublisher.triggerEvent(selectionTypeEvent);
		
		// renderStyle = new GeneralRenderStyle(viewFrustum);
		renderStyle = new VisBricksRenderStyle(viewFrustum);

		super.renderStyle = renderStyle;
		detailLevel = DetailLevel.HIGH;
		metaSetsUpdated();
		connectionRenderer.init(gl);
	}

	private void initLayouts() {

		dimensionGroupManager.getDimensionGroupSpacers().clear();

		initCenterLayout();

		ViewFrustum leftArchFrustum = new ViewFrustum(viewFrustum.getProjectionMode(), 0,
				archSideThickness, 0, archBottomY, 0, 1);
		leftLayoutManager = new LayoutManager(leftArchFrustum);
		leftColumnLayout = new Column("leftArchColumn");
		leftLayoutTemplate = new LayoutTemplate();

		initSideLayout(leftColumnLayout, leftLayoutTemplate, leftLayoutManager, 0,
				dimensionGroupManager.getCenterGroupStartIndex());

		ViewFrustum rightArchFrustum = new ViewFrustum(viewFrustum.getProjectionMode(),
				0, archSideThickness, 0, archBottomY, 0, 1);
		rightColumnLayout = new Column("rightArchColumn");
		rightLayout = new LayoutTemplate();
		rightLayoutManager = new LayoutManager(rightArchFrustum);
		initSideLayout(rightColumnLayout, rightLayout, rightLayoutManager,
				dimensionGroupManager.getRightGroupStartIndex(), dimensionGroupManager
						.getDimensionGroups().size());

		updateConnectionLinesBetweenDimensionGroups();
	}

	/**
	 * Init the layout for the center region, showing the horizontal bar of the
	 * arch plus all sub-bricks above and below
	 */
	private void initCenterLayout() {

		archSideThickness = viewFrustum.getWidth() * ARCH_STAND_WIDTH_PERCENT;
		archInnerWidth = viewFrustum.getWidth() * (ARCH_STAND_WIDTH_PERCENT + 0.024f);

		archHeight = parentGLCanvas.getPixelGLConverter().getGLHeightForPixelHeight(
				ARCH_PIXEL_HEIGHT);
		archBottomY = viewFrustum.getHeight() * ARCH_BOTTOM_PERCENT - archHeight / 2f;

		archTopY = archBottomY + archHeight;

		int dimensionGroupCountInCenter = dimensionGroupManager.getRightGroupStartIndex()
				- dimensionGroupManager.getCenterGroupStartIndex();

		float centerLayoutWidth = viewFrustum.getWidth() - 2 * (archInnerWidth);

		centerRowLayout = new Row("centerArchRow");
		centerRowLayout.setFrameColor(1, 1, 0, 1);
		// centerRowLayout.setDebug(false);

		leftDimensionGroupSpacing = new ElementLayout("firstCenterDimGrSpacing");
		// leftDimensionGroupSpacing.setDebug(true);

		DimensionGroupSpacingRenderer dimensionGroupSpacingRenderer = null;

		// Handle special case where center contains no groups
		if (dimensionGroupCountInCenter < 1) {
			dimensionGroupSpacingRenderer = new DimensionGroupSpacingRenderer(null,
					connectionRenderer, null, null, this);
		} else {
			dimensionGroupSpacingRenderer = new DimensionGroupSpacingRenderer(null,
					connectionRenderer, null, dimensionGroupManager.getDimensionGroups()
							.get(dimensionGroupManager.getCenterGroupStartIndex()), this);
		}

		leftDimensionGroupSpacing.setRenderer(dimensionGroupSpacingRenderer);
		dimensionGroupSpacingRenderer.setLineLength(archHeight);

		leftDimensionGroupSpacing.setPixelGLConverter(parentGLCanvas
				.getPixelGLConverter());

		if (dimensionGroupCountInCenter > 1)
			leftDimensionGroupSpacing.setPixelSizeX(50);
		else
			leftDimensionGroupSpacing.setGrabX(true);

		centerRowLayout.append(leftDimensionGroupSpacing);

		for (int dimensionGroupIndex = dimensionGroupManager.getCenterGroupStartIndex(); dimensionGroupIndex < dimensionGroupManager
				.getRightGroupStartIndex(); dimensionGroupIndex++) {

			ElementLayout dynamicDimensionGroupSpacing;

			DimensionGroup group = dimensionGroupManager.getDimensionGroups().get(
					dimensionGroupIndex);
			group.setCollapsed(false);
			group.setArchHeight(ARCH_PIXEL_HEIGHT);
			centerRowLayout.append(group.getLayout());
			// centerRowLayout.setDebug(true);

			if (dimensionGroupIndex != dimensionGroupManager.getRightGroupStartIndex() - 1) {
				dynamicDimensionGroupSpacing = new ElementLayout("dynamicDimGrSpacing");
				dimensionGroupSpacingRenderer = new DimensionGroupSpacingRenderer(
						relationAnalyzer, connectionRenderer, group,
						dimensionGroupManager.getDimensionGroups().get(
								dimensionGroupIndex + 1), this);
				dynamicDimensionGroupSpacing.setGrabX(true);
				dynamicDimensionGroupSpacing.setRenderer(dimensionGroupSpacingRenderer);
				centerRowLayout.append(dynamicDimensionGroupSpacing);

			} else {
				rightDimensionGroupSpacing = new ElementLayout("lastDimGrSpacing");
				dimensionGroupSpacingRenderer = new DimensionGroupSpacingRenderer(null,
						connectionRenderer, group, null, this);
				rightDimensionGroupSpacing.setPixelGLConverter(parentGLCanvas
						.getPixelGLConverter());

				if (dimensionGroupCountInCenter > 1)
					rightDimensionGroupSpacing.setPixelSizeX(50);
				else
					rightDimensionGroupSpacing.setGrabX(true);

				rightDimensionGroupSpacing.setRenderer(dimensionGroupSpacingRenderer);
				centerRowLayout.append(rightDimensionGroupSpacing);
			}
			// dimensionGroupSpacing.setDebug(true);

			dimensionGroupSpacingRenderer.setLineLength(archHeight);
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

	/**
	 * Initialize the layout for the sides of the arch
	 * 
	 * @param columnLayout
	 * @param layoutTemplate
	 * @param layoutManager
	 * @param dimensinoGroupStartIndex
	 * @param dimensinoGroupEndIndex
	 */
	private void initSideLayout(Column columnLayout, LayoutTemplate layoutTemplate,
			LayoutManager layoutManager, int dimensinoGroupStartIndex,
			int dimensinoGroupEndIndex) {

		layoutTemplate.setPixelGLConverter(parentGLCanvas.getPixelGLConverter());
		layoutTemplate.setBaseElementLayout(columnLayout);

		layoutManager.setTemplate(layoutTemplate);

		columnLayout.setFrameColor(1, 1, 0, 1);
		// columnLayout.setDebug(true);
		columnLayout.setBottomUp(true);

		ElementLayout dimensionGroupSpacing = new ElementLayout("firstSideDimGrSpacing");

		// dimensionGroupSpacing.setDebug(true);
		dimensionGroupSpacing.setGrabY(true);

		columnLayout.append(dimensionGroupSpacing);

		DimensionGroupSpacingRenderer dimensionGroupSpacingRenderer = null;

		// Handle special case where arch stand contains no groups
		if (dimensinoGroupStartIndex == 0) {
			dimensionGroupSpacingRenderer = new DimensionGroupSpacingRenderer(null,
					connectionRenderer, null, null, this);
		} else {
			dimensionGroupSpacingRenderer = new DimensionGroupSpacingRenderer(null,
					connectionRenderer, null, dimensionGroupManager.getDimensionGroups()
							.get(dimensionGroupManager.getCenterGroupStartIndex()), this);
		}

		dimensionGroupSpacing.setRenderer(dimensionGroupSpacingRenderer);

		dimensionGroupSpacingRenderer.setVertical(false);
		dimensionGroupSpacingRenderer.setLineLength(archSideThickness);

		for (int dimensionGroupIndex = dimensinoGroupStartIndex; dimensionGroupIndex < dimensinoGroupEndIndex; dimensionGroupIndex++) {

			DimensionGroup group = dimensionGroupManager.getDimensionGroups().get(
					dimensionGroupIndex);

			group.getLayout().setAbsoluteSizeY(archSideThickness);
			// group.getLayout().setDebug(true);
			group.setArchHeight(-1);
			columnLayout.append(group.getLayout());

			group.setCollapsed(true);

			dimensionGroupSpacing = new ElementLayout("sideDimGrSpacing");
			// dimensionGroupSpacing.setDebug(true);
			dimensionGroupSpacing.setGrabY(true);

			dimensionGroupSpacingRenderer = new DimensionGroupSpacingRenderer(null, null,
					group, null, this);
			columnLayout.append(dimensionGroupSpacing);

			dimensionGroupSpacing.setRenderer(dimensionGroupSpacingRenderer);

			dimensionGroupSpacingRenderer.setVertical(false);
			dimensionGroupSpacingRenderer.setLineLength(archSideThickness);

		}

		layoutManager.updateLayout();

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

			initLayouts();
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

		if (isLayoutDirty) {
			isLayoutDirty = false;
			centerLayoutManager.updateLayout();

			for (ElementLayout layout : centerRowLayout) {
				if (resizeNecessary)
					break;
				if (layout.getSizeScaledX() < parentGLCanvas.getPixelGLConverter()
						.getGLWidthForPixelWidth(DIMENSION_GROUP_SPACING) + 0.001f) {
					resizeNecessary = true;
					break;
				}
			}
		}

		renderArch(gl);

		for (DimensionGroup dimensionGroup : dimensionGroupManager.getDimensionGroups()) {
			dimensionGroup.display(gl);
		}

		if (resizeNecessary) {
			if (lastResizeDirectionWasToLeft) {
				dimensionGroupManager.setCenterGroupStartIndex(dimensionGroupManager
						.getCenterGroupStartIndex() + 1);
				if (centerRowLayout.size() == 3)
					leftDimensionGroupSpacing.setGrabX(true);

			} else {
				dimensionGroupManager.setRightGroupStartIndex(dimensionGroupManager
						.getRightGroupStartIndex() - 1);
				if (centerRowLayout.size() == 3)
					rightDimensionGroupSpacing.setGrabX(true);
			}
			initLayouts();

			updateLayout();
			resizeNecessary = false;
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
				break;

			}

		case DIMENSION_GROUP_SPACER:
			switch (pickingMode) {
			case DRAGGED:

				dragAndDropController.setDropArea(dimensionGroupManager
						.getDimensionGroupSpacers().get(externalID));

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
		Iterator<DimensionGroup> dimensionGroupIterator = dimensionGroups.iterator();
		while (dimensionGroupIterator.hasNext()) {
			DimensionGroup dimensionGroup = dimensionGroupIterator.next();
			ISet metaSet = dimensionGroup.getSet();
			if (!metaSets.contains(metaSet)) {
				dimensionGroupIterator.remove();
			} else {
				metaSets.remove(metaSet);
			}

		}
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
			dimensionGroup.setVisBricksView(this);
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

		initLayouts();

	}

	@Override
	public void setDisplayListDirty() {
		super.setDisplayListDirty();
	}

	public void moveDimensionGroup(DimensionGroupSpacingRenderer spacer,
			DimensionGroup movedDimGroup, DimensionGroup referenceDimGroup) {
		movedDimGroup.getLayout().reset();
		clearDimensionGroupSpacerHighlight();

		boolean insertComplete = false;

		ArrayList<DimensionGroup> dimensionGroups = dimensionGroupManager
				.getDimensionGroups();
		for (ElementLayout leftLayout : leftColumnLayout.getElements()) {
			if (spacer == leftLayout.getRenderer()) {

				dimensionGroupManager.setCenterGroupStartIndex(dimensionGroupManager
						.getCenterGroupStartIndex() + 1);

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

					dimensionGroupManager.setRightGroupStartIndex(dimensionGroupManager
							.getRightGroupStartIndex() - 1);

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

					if (dimensionGroups.indexOf(movedDimGroup) < dimensionGroupManager
							.getCenterGroupStartIndex())
						dimensionGroupManager
								.setCenterGroupStartIndex(dimensionGroupManager
										.getCenterGroupStartIndex() - 1);
					else if (dimensionGroups.indexOf(movedDimGroup) >= dimensionGroupManager
							.getRightGroupStartIndex())
						dimensionGroupManager
								.setRightGroupStartIndex(dimensionGroupManager
										.getRightGroupStartIndex() + 1);

					dimensionGroups.remove(movedDimGroup);
					if (referenceDimGroup == null) {
						dimensionGroups.add(
								dimensionGroupManager.getCenterGroupStartIndex(),
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
		// FIXME: check why the second layout is needed here. otherwise the
		// moved views appear upside down
		initLayouts();

		RelationsUpdatedEvent event = new RelationsUpdatedEvent();
		event.setDataDomainType(dataDomain.getDataDomainType());
		event.setSender(this);
		eventPublisher.triggerEvent(event);
	}

	public void clearDimensionGroupSpacerHighlight() {
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
	}

	public DimensionGroupManager getDimensionGroupManager() {
		return dimensionGroupManager;
	}

	public void updateConnectionLinesBetweenDimensionGroups() {
		if (centerRowLayout != null) {
			for (ElementLayout elementLayout : centerRowLayout.getElements()) {
				if (elementLayout.getRenderer() instanceof DimensionGroupSpacingRenderer) {
					((DimensionGroupSpacingRenderer) elementLayout.getRenderer()).init();
				}
			}
		}
	}

	public void updateLayout() {
		isLayoutDirty = true;
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
	
	public SelectionType getSelectedByGroupSelectionType() {
		return selectedByGroupSelectionType;
	}
	
	public ContentSelectionManager getContentSelectionManager() {
		return contentSelectionManager;
	}
}
