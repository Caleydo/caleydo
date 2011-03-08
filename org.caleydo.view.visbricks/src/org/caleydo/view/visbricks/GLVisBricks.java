package org.caleydo.view.visbricks;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.virtualarray.EVAOperation;
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
	private final static float ARCH_STAND_WIDTH_PERCENT = 0.1f;

	private final static int DIMENSION_GROUP_SPACING = 10;
	private final static int MAX_CENTER_DIMENSION_GROUPS = 6;

	private NewMetaSetsListener metaSetsListener;

	private ASetBasedDataDomain dataDomain;

	private VisBricksRenderStyle renderStyle;

	private IConnectionRenderer connectionRenderer;

	private ArrayList<DimensionGroup> dimensionGroups;
	private int centerGroupStartIndex = 0;
	private int centerGroupEndIndex = 0;

	private LayoutManager centerLayoutManager;
	private LayoutManager leftLayoutManager;
	private LayoutManager rightLayoutManager;

	private LayoutTemplate centerLayout;
	private LayoutTemplate leftLayout;
	private LayoutTemplate rightLayout;

	private float archWidth = 0;
	private float archInnerWidth = 0;
	private float archTopY = 0;
	private float archBottomY = 0;
	private float archHeight = 0;

	private Queue<DimensionGroup> uninitializedDimensionGroups = new LinkedList<DimensionGroup>();

	private DragAndDropController dragAndDropController;

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param sLabel
	 * @param viewFrustum
	 */
	public GLVisBricks(GLCaleydoCanvas glCanvas, final ViewFrustum viewFrustum) {
		super(glCanvas, viewFrustum, true);

		dimensionGroups = new ArrayList<DimensionGroup>(20);

		viewType = GLVisBricks.VIEW_ID;

		connectionRenderer = new ConnectionBandRenderer();

		dragAndDropController = new DragAndDropController(this);
	}

	@Override
	public void init(GL2 gl) {
		// renderStyle = new GeneralRenderStyle(viewFrustum);
		renderStyle = new VisBricksRenderStyle(viewFrustum);

		super.renderStyle = renderStyle;
		detailLevel = DetailLevel.HIGH;
		metaSetsUpdated();
		connectionRenderer.init(gl);

		// initLayout();
	}

	private void initLayoutCenter() {

		archWidth = viewFrustum.getWidth() * ARCH_STAND_WIDTH_PERCENT;
		archInnerWidth = viewFrustum.getWidth() * (ARCH_STAND_WIDTH_PERCENT + 0.05f);
		archTopY = viewFrustum.getHeight() * ARCH_TOP_PERCENT;
		archBottomY = viewFrustum.getHeight() * ARCH_BOTTOM_PERCENT;
		archHeight = (ARCH_TOP_PERCENT - ARCH_BOTTOM_PERCENT) * viewFrustum.getHeight();

		float centerLayoutWidth = viewFrustum.getWidth() - 2 * archInnerWidth;

		float dimensionGroupLayoutRatio = 1f / (centerGroupEndIndex - centerGroupStartIndex);

		Row rowLayout = new Row("centerArchRow");
		rowLayout.setFrameColor(1, 1, 0, 1);
		rowLayout.setDebug(true);

		ElementLayout dimensionGroupSpacing = new ElementLayout("dimensionGroupSpacing");
		DimensionGroupSpacingRenderer dimensionGroupSpacingRenderer = new DimensionGroupSpacingRenderer();
		dimensionGroupSpacing.setRenderer(dimensionGroupSpacingRenderer);
		dimensionGroupSpacing.setPixelGLConverter(parentGLCanvas.getPixelGLConverter());
		dimensionGroupSpacing.setPixelSizeX(5);
		rowLayout.appendElement(dimensionGroupSpacing);

		for (int dimensionGroupIndex = centerGroupStartIndex; dimensionGroupIndex < centerGroupEndIndex; dimensionGroupIndex++) {

			DimensionGroup group = dimensionGroups.get(dimensionGroupIndex);
			group.getLayout().setRatioSizeX(dimensionGroupLayoutRatio);
			group.getLayout().setRatioSizeY(1);
			group.setArchBounds(archHeight, ARCH_BOTTOM_PERCENT, ARCH_TOP_PERCENT
					- ARCH_BOTTOM_PERCENT, ARCH_BOTTOM_PERCENT);
			group.setCollapsed(false);
			rowLayout.appendElement(group.getLayout());

			dimensionGroupSpacing = new ElementLayout("dimensionGroupSpacing");
			dimensionGroupSpacingRenderer = new DimensionGroupSpacingRenderer();
			dimensionGroupSpacing.setRenderer(dimensionGroupSpacingRenderer);
			dimensionGroupSpacing.setPixelGLConverter(parentGLCanvas
					.getPixelGLConverter());
			dimensionGroupSpacing.setPixelSizeX(DIMENSION_GROUP_SPACING);
			rowLayout.appendElement(dimensionGroupSpacing);
		}

		centerLayout = new LayoutTemplate();
		centerLayout.setPixelGLConverter(parentGLCanvas.getPixelGLConverter());
		centerLayout.setBaseElementLayout(rowLayout);

		ViewFrustum centerArchFrustum = new ViewFrustum(viewFrustum.getProjectionMode(),
				0, centerLayoutWidth, 0, viewFrustum.getHeight(), 0, 1);
		centerLayoutManager = new LayoutManager(centerArchFrustum);
		centerLayoutManager.setTemplate(centerLayout);
		if (uninitializedDimensionGroups.size() == 0)
			centerLayoutManager.updateLayout();
	}

	private void initLayoutLeft() {

		float dimensionGroupLayoutRatio = 1f / centerGroupStartIndex;

		Column columnLayout = new Column("leftArchColumn");
		columnLayout.setFrameColor(1, 1, 0, 1);
		columnLayout.setDebug(true);
			
		ElementLayout dimensionGroupSpacing = new ElementLayout("dimensionGroupSpacing");
		DimensionGroupSpacingRenderer dimensionGroupSpacingRenderer = new DimensionGroupSpacingRenderer();
		dimensionGroupSpacing.setRenderer(dimensionGroupSpacingRenderer);
		dimensionGroupSpacing.setPixelGLConverter(parentGLCanvas.getPixelGLConverter());
		dimensionGroupSpacing.setPixelSizeY(5);
		columnLayout.appendElement(dimensionGroupSpacing);

		for (int dimensionGroupIndex = 0; dimensionGroupIndex < centerGroupStartIndex; dimensionGroupIndex++) {

			DimensionGroup group = dimensionGroups.get(dimensionGroupIndex);

			group.getLayout().setRatioSizeX(1);
			group.getLayout().setRatioSizeY(dimensionGroupLayoutRatio);
			group.getLayout().setDebug(true);
			group.setArchBounds(viewFrustum.getHeight(), ARCH_BOTTOM_PERCENT,
					ARCH_TOP_PERCENT - ARCH_BOTTOM_PERCENT, ARCH_BOTTOM_PERCENT);
			columnLayout.appendElement(group.getLayout());

			group.setCollapsed(true);

			dimensionGroupSpacing = new ElementLayout("dimensionGroupSpacing");
			dimensionGroupSpacingRenderer = new DimensionGroupSpacingRenderer();
			dimensionGroupSpacing.setRenderer(dimensionGroupSpacingRenderer);
			dimensionGroupSpacing.setPixelGLConverter(parentGLCanvas
					.getPixelGLConverter());
			dimensionGroupSpacing.setPixelSizeY(DIMENSION_GROUP_SPACING);
			columnLayout.appendElement(dimensionGroupSpacing);
		}

		leftLayout = new LayoutTemplate();
		leftLayout.setPixelGLConverter(parentGLCanvas.getPixelGLConverter());
		leftLayout.setBaseElementLayout(columnLayout);
		
		ViewFrustum leftArchFrustum = new ViewFrustum(viewFrustum.getProjectionMode(), 0,
				archWidth, 0, archBottomY, 0, 1);
		leftLayoutManager = new LayoutManager(leftArchFrustum);
		leftLayoutManager.setTemplate(leftLayout);
		if (uninitializedDimensionGroups.size() == 0)
			leftLayoutManager.updateLayout();
	}

	private void initLayoutRight() {

		float dimensionGroupLayoutRatio = 1f / (dimensionGroups.size() - centerGroupEndIndex);

		Column columnLayout = new Column("rightArchColumn");
		columnLayout.setFrameColor(1, 1, 0, 1);

		ElementLayout dimensionGroupSpacing = new ElementLayout("dimensionGroupSpacing");
		DimensionGroupSpacingRenderer dimensionGroupSpacingRenderer = new DimensionGroupSpacingRenderer();
		dimensionGroupSpacing.setRenderer(dimensionGroupSpacingRenderer);
		dimensionGroupSpacing.setPixelGLConverter(parentGLCanvas.getPixelGLConverter());
		dimensionGroupSpacing.setPixelSizeY(5);
		columnLayout.appendElement(dimensionGroupSpacing);

		for (int dimensionGroupIndex = centerGroupEndIndex; dimensionGroupIndex < dimensionGroups.size(); dimensionGroupIndex++) {

			DimensionGroup group = dimensionGroups.get(dimensionGroupIndex);
			group.getLayout().setRatioSizeX(1);
			group.getLayout().setRatioSizeY(dimensionGroupLayoutRatio);
			group.setArchBounds(viewFrustum.getHeight(), ARCH_BOTTOM_PERCENT,
					ARCH_TOP_PERCENT - ARCH_BOTTOM_PERCENT, ARCH_BOTTOM_PERCENT);
			columnLayout.appendElement(group.getLayout());

			group.setCollapsed(true);

			dimensionGroupSpacing = new ElementLayout("dimensionGroupSpacing");
			dimensionGroupSpacingRenderer = new DimensionGroupSpacingRenderer();
			dimensionGroupSpacing.setRenderer(dimensionGroupSpacingRenderer);
			dimensionGroupSpacing.setPixelGLConverter(parentGLCanvas
					.getPixelGLConverter());
			dimensionGroupSpacing.setPixelSizeY(DIMENSION_GROUP_SPACING);
			columnLayout.appendElement(dimensionGroupSpacing);
		}

		rightLayout = new LayoutTemplate();
		rightLayout.setPixelGLConverter(parentGLCanvas.getPixelGLConverter());
		rightLayout.setBaseElementLayout(columnLayout);

		ViewFrustum leftArchFrustum = new ViewFrustum(viewFrustum.getProjectionMode(), 0,
				archWidth, 0, archBottomY, 0, 1);
		rightLayoutManager = new LayoutManager(leftArchFrustum);
		rightLayoutManager.setTemplate(rightLayout);
		if (uninitializedDimensionGroups.size() == 0)
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

		for (DimensionGroup group : dimensionGroups) {
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

		for (DimensionGroup dimensionGroup : dimensionGroups) {
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

		// // Band border
		// // gl.glLineWidth(1);
		// gl.glColor4f(0.5f, 0.5f, 0.5f, 1f);
		// // gl.glBegin(GL2.GL_LINE_STRIP);
		// // for (int i = 0; i < outputPoints.size(); i++) {
		// // gl.glVertex3f(outputPoints.get(i).x(), outputPoints.get(i).y(),
		// 0f);
		// // }
		// // gl.glEnd();
		//
		// // inputPoints = new ArrayList<Vec3f>();
		// // inputPoints.add(new Vec3f(leftBottomPos[0], leftBottomPos[1], 0));
		// // inputPoints.add(new Vec3f(rightBottomPos[0], rightBottomPos[1],
		// 0));
		// //
		// // curve = new NURBSCurve(inputPoints, 10);
		// // ArrayList<Vec3f> points = curve.getCurvePoints();
		//
		// // Reverse point order
		// // for (int i = points.size() - 1; i >= 0; i--) {
		// // outputPoints.add(points.get(i));
		// // }
		//
		// // Band border
		// // gl.glLineWidth(1);
		// // gl.glBegin(GL2.GL_LINE_STRIP);
		// // for (int i = 0; i < points.size(); i++) {
		// // gl.glVertex3f(points.get(i).x(), points.get(i).y(), 0f);
		// // }
		// // gl.glEnd();

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
		gl.glVertex3f(archWidth, archBottomY, 0f);
		gl.glVertex3f(archWidth, 0, 0f);
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
		inputPoints.add(new Vec3f(archWidth, archBottomY, 0));
		inputPoints.add(new Vec3f(archWidth, archBottomY * 0.8f, 0));

		curve = new NURBSCurve(inputPoints, 10);
		outputPoints.addAll(curve.getCurvePoints());

		connectionRenderer.render(gl, outputPoints);

		// Right arch

		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex3f(viewFrustum.getWidth(), 0, 0f);
		gl.glVertex3f(viewFrustum.getWidth(), archBottomY, 0f);
		gl.glVertex3f(viewFrustum.getWidth() - archWidth, archBottomY, 0f);
		gl.glVertex3f(viewFrustum.getWidth() - archWidth, 0, 0f);
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
		inputPoints.add(new Vec3f(viewFrustum.getWidth() - archWidth, archBottomY, 0));
		inputPoints.add(new Vec3f(viewFrustum.getWidth() - archWidth, archBottomY * 0.8f,
				0));

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

		ArrayList<ISet> allMetaSets = dataDomain.getSet().getStorageData(storageVAType)
				.getStorageTree().getRoot().getAllMetaSetsFromSubTree();

		ArrayList<ISet> filteredMetaSets = new ArrayList<ISet>(allMetaSets.size() / 2);

		for (ISet metaSet : allMetaSets) {
			if (metaSet.size() > 1 && metaSet.size() != dataDomain.getSet().size())
				filteredMetaSets.add(metaSet);
		}
		initializeBricks(filteredMetaSets);

	}

	private void initializeBricks(ArrayList<ISet> metaSets) {
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
			dimensionGroup.setVisBricksViewID(iUniqueID);
			dimensionGroup.initialize();

			dimensionGroups.add(dimensionGroup);

			uninitializedDimensionGroups.add(dimensionGroup);

		}

		if (dimensionGroups.size() > MAX_CENTER_DIMENSION_GROUPS) {
			centerGroupStartIndex = (dimensionGroups.size() - MAX_CENTER_DIMENSION_GROUPS) / 2;
			centerGroupEndIndex = centerGroupStartIndex + MAX_CENTER_DIMENSION_GROUPS;
		} else {
			centerGroupStartIndex = 0;
			centerGroupEndIndex = dimensionGroups.size();
		}
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
			DimensionGroup movedDimGroup, boolean beforeOrAfter) {

		int movedDimGroupIndex = dimensionGroups.indexOf(movedDimGroup);
		int refDimGroupIndex = dimensionGroups.indexOf(referenceDimGroup);
		
		if (refDimGroupIndex < centerGroupStartIndex)
			centerGroupStartIndex++;
		else if (refDimGroupIndex > centerGroupStartIndex && refDimGroupIndex < centerGroupEndIndex)
		{
			if (movedDimGroupIndex > centerGroupEndIndex)
				centerGroupEndIndex++;
			else if (movedDimGroupIndex < centerGroupStartIndex)
				centerGroupStartIndex--;
		}
		else if (refDimGroupIndex > centerGroupEndIndex)
			centerGroupEndIndex--;
			
		dimensionGroups.remove(movedDimGroup);
		dimensionGroups
				.add(dimensionGroups.indexOf(referenceDimGroup) + 1, movedDimGroup);

		initLayoutCenter();
		initLayoutLeft();
		initLayoutRight();
	}
}
