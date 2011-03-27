package org.caleydo.view.visbricks;

import gleem.linalg.Vec3f;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.graph.tree.ClusterTree;
import org.caleydo.core.data.selection.ContentSelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.SelectionTypeEvent;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.EVAOperation;
import org.caleydo.core.data.virtualarray.similarity.RelationAnalyzer;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
import org.caleydo.core.manager.event.data.NewMetaSetsEvent;
import org.caleydo.core.manager.event.data.RelationsUpdatedEvent;
import org.caleydo.core.manager.event.view.ClearSelectionsEvent;
import org.caleydo.core.manager.event.view.storagebased.ConnectionsModeEvent;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
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
import org.caleydo.core.view.opengl.canvas.listener.ClearSelectionsListener;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutManager;
import org.caleydo.core.view.opengl.layout.LayoutTemplate;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.GLCoordinateUtils;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.spline.ConnectionBandRenderer;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.core.view.opengl.util.vislink.NURBSCurve;
import org.caleydo.view.visbricks.dimensiongroup.DimensionGroup;
import org.caleydo.view.visbricks.dimensiongroup.DimensionGroupManager;
import org.caleydo.view.visbricks.dimensiongroup.DimensionGroupSpacingRenderer;
import org.caleydo.view.visbricks.listener.ConnectionsModeListener;
import org.caleydo.view.visbricks.listener.GLVisBricksKeyListener;
import org.caleydo.view.visbricks.listener.NewMetaSetsListener;
import org.caleydo.view.visbricks.renderstyle.VisBricksRenderStyle;

import com.jogamp.common.util.IntIntHashMap;

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

	private final static int DIMENSION_GROUP_SPACING_MIN_PIXEL_WIDTH = 30;

	private NewMetaSetsListener metaSetsListener;
	private ClearSelectionsListener clearSelectionsListener;
	private ConnectionsModeListener trendHighlightModeListener;

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

	private boolean isLeftDetailShown = false;
	private boolean isRightDetailShown = false;

	private Queue<DimensionGroup> uninitializedDimensionGroups = new LinkedList<DimensionGroup>();

	private DragAndDropController dragAndDropController;

	private RelationAnalyzer relationAnalyzer;

	private ElementLayout leftDimensionGroupSpacing;
	private ElementLayout rightDimensionGroupSpacing;

	private ContentSelectionManager contentSelectionManager;

	private boolean connectionsOn = true;
	private boolean connectionsHighlightDynamic = false;

	private int selectedConnectionBandID = -1;

	/**
	 * Determines the connection focus highlight dynamically in a range between
	 * 0 and 1
	 */
	private float connectionsFocusFactor;

	private boolean isHorizontalMoveDraggingActive = false;
	private int movedDimensionGroup = -1;

	private float previousXCoordinate = Float.NaN;

	/** Needed for selecting the elments when a connection band is picked **/
	private HashMap<Integer, ContentVirtualArray> hashConnectionBandIDToContentVA = new HashMap<Integer, ContentVirtualArray>();

	private SelectionType volatieBandSelectionType;

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

		glKeyListener = new GLVisBricksKeyListener();
	}

	@Override
	public void init(GL2 gl) {
		textRenderer = new CaleydoTextRenderer(24);
		dataDomain.createContentRelationAnalyzer();
		relationAnalyzer = dataDomain.getContentRelationAnalyzer();

		contentSelectionManager = dataDomain.getContentSelectionManager();

		// renderStyle = new GeneralRenderStyle(viewFrustum);
		renderStyle = new VisBricksRenderStyle(viewFrustum);

		super.renderStyle = renderStyle;
		detailLevel = DetailLevel.HIGH;
		metaSetsUpdated();
		connectionRenderer.init(gl);
		// viewFrustum.setProjectionMode(ECameraProjectionMode.PERSPECTIVE);
		// viewCamera.setCameraRotation(new Rotf());

	}

	private void initLayouts() {

		dimensionGroupManager.getDimensionGroupSpacers().clear();

		initCenterLayout();

		initLeftLayout();
		initRightLayout();

		updateConnectionLinesBetweenDimensionGroups();
	}

	private void initLeftLayout() {
		ViewFrustum leftArchFrustum = new ViewFrustum(
				viewFrustum.getProjectionMode(), 0, archSideThickness, 0,
				archBottomY, 0, 1);
		leftLayoutManager = new LayoutManager(leftArchFrustum);
		leftColumnLayout = new Column("leftArchColumn");
		leftLayoutTemplate = new LayoutTemplate();

		initSideLayout(leftColumnLayout, leftLayoutTemplate, leftLayoutManager,
				0, dimensionGroupManager.getCenterGroupStartIndex());
	}

	private void initRightLayout() {
		ViewFrustum rightArchFrustum = new ViewFrustum(
				viewFrustum.getProjectionMode(), 0, archSideThickness, 0,
				archBottomY, 0, 1);
		rightColumnLayout = new Column("rightArchColumn");
		rightLayout = new LayoutTemplate();
		rightLayoutManager = new LayoutManager(rightArchFrustum);
		initSideLayout(rightColumnLayout, rightLayout, rightLayoutManager,
				dimensionGroupManager.getRightGroupStartIndex(),
				dimensionGroupManager.getDimensionGroups().size());
	}

	public int getSideArchWidthPixels() {
		return parentGLCanvas.getPixelGLConverter().getPixelWidthForGLWidth(
				viewFrustum.getWidth() * ARCH_STAND_WIDTH_PERCENT);
	}

	/**
	 * Init the layout for the center region, showing the horizontal bar of the
	 * arch plus all sub-bricks above and below
	 */
	private void initCenterLayout() {

		archSideThickness = viewFrustum.getWidth() * ARCH_STAND_WIDTH_PERCENT;
		if (isLeftDetailShown || isRightDetailShown) {
			archInnerWidth = 0;
		} else {
			archInnerWidth = viewFrustum.getWidth()
					* (ARCH_STAND_WIDTH_PERCENT + 0.024f);
		}

		archHeight = parentGLCanvas.getPixelGLConverter()
				.getGLHeightForPixelHeight(ARCH_PIXEL_HEIGHT);
		archBottomY = viewFrustum.getHeight() * ARCH_BOTTOM_PERCENT
				- archHeight / 2f;

		archTopY = archBottomY + archHeight;

		int dimensionGroupCountInCenter = dimensionGroupManager
				.getRightGroupStartIndex()
				- dimensionGroupManager.getCenterGroupStartIndex();

		float centerLayoutWidth = viewFrustum.getWidth() - 2 * (archInnerWidth);
		// float centerLayoutWidth = viewFrustum.getWidth();

		centerRowLayout = new Row("centerArchRow");
		centerRowLayout.setFrameColor(1, 1, 0, 1);
		// centerRowLayout.setDebug(false);

		leftDimensionGroupSpacing = new ElementLayout("firstCenterDimGrSpacing");
		// leftDimensionGroupSpacing.setDebug(true);

		DimensionGroupSpacingRenderer dimensionGroupSpacingRenderer = null;

		// Handle special case where center contains no groups
		if (dimensionGroupCountInCenter < 1) {
			dimensionGroupSpacingRenderer = new DimensionGroupSpacingRenderer(
					null, connectionRenderer, null, null, this);
		} else {
			dimensionGroupSpacingRenderer = new DimensionGroupSpacingRenderer(
					null, connectionRenderer, null, dimensionGroupManager
							.getDimensionGroups().get(
									dimensionGroupManager
											.getCenterGroupStartIndex()), this);
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

		for (int dimensionGroupIndex = dimensionGroupManager
				.getCenterGroupStartIndex(); dimensionGroupIndex < dimensionGroupManager
				.getRightGroupStartIndex(); dimensionGroupIndex++) {

			ElementLayout dynamicDimensionGroupSpacing;

			DimensionGroup group = dimensionGroupManager.getDimensionGroups()
					.get(dimensionGroupIndex);
			group.setCollapsed(false);
			group.setArchHeight(ARCH_PIXEL_HEIGHT);
			centerRowLayout.append(group.getLayout());
			// centerRowLayout.setDebug(true);

			if (dimensionGroupIndex != dimensionGroupManager
					.getRightGroupStartIndex() - 1) {
				dynamicDimensionGroupSpacing = new ElementLayout(
						"dynamicDimGrSpacing");
				dimensionGroupSpacingRenderer = new DimensionGroupSpacingRenderer(
						relationAnalyzer, connectionRenderer, group,
						dimensionGroupManager.getDimensionGroups().get(
								dimensionGroupIndex + 1), this);
				dynamicDimensionGroupSpacing.setGrabX(true);
				dynamicDimensionGroupSpacing
						.setRenderer(dimensionGroupSpacingRenderer);
				centerRowLayout.append(dynamicDimensionGroupSpacing);

			} else {
				rightDimensionGroupSpacing = new ElementLayout(
						"lastDimGrSpacing");
				dimensionGroupSpacingRenderer = new DimensionGroupSpacingRenderer(
						null, connectionRenderer, group, null, this);
				rightDimensionGroupSpacing.setPixelGLConverter(parentGLCanvas
						.getPixelGLConverter());

				if (dimensionGroupCountInCenter > 1)
					rightDimensionGroupSpacing.setPixelSizeX(50);
				else
					rightDimensionGroupSpacing.setGrabX(true);

				rightDimensionGroupSpacing
						.setRenderer(dimensionGroupSpacingRenderer);
				centerRowLayout.append(rightDimensionGroupSpacing);
			}
			// dimensionGroupSpacing.setDebug(true);

			dimensionGroupSpacingRenderer.setLineLength(archHeight);
		}

		centerLayout = new LayoutTemplate();
		centerLayout.setPixelGLConverter(parentGLCanvas.getPixelGLConverter());
		centerLayout.setBaseElementLayout(centerRowLayout);

		ViewFrustum centerArchFrustum = new ViewFrustum(
				viewFrustum.getProjectionMode(), 0, centerLayoutWidth, 0,
				viewFrustum.getHeight(), 0, 1);
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
	private void initSideLayout(Column columnLayout,
			LayoutTemplate layoutTemplate, LayoutManager layoutManager,
			int dimensinoGroupStartIndex, int dimensinoGroupEndIndex) {

		layoutTemplate
				.setPixelGLConverter(parentGLCanvas.getPixelGLConverter());
		layoutTemplate.setBaseElementLayout(columnLayout);

		layoutManager.setTemplate(layoutTemplate);

		columnLayout.setFrameColor(1, 1, 0, 1);
		// columnLayout.setDebug(true);
		columnLayout.setBottomUp(true);

		ElementLayout dimensionGroupSpacing = new ElementLayout(
				"firstSideDimGrSpacing");

		// dimensionGroupSpacing.setDebug(true);
		dimensionGroupSpacing.setGrabY(true);

		columnLayout.append(dimensionGroupSpacing);

		DimensionGroupSpacingRenderer dimensionGroupSpacingRenderer = null;

		// Handle special case where arch stand contains no groups
		if (dimensinoGroupStartIndex == 0) {
			dimensionGroupSpacingRenderer = new DimensionGroupSpacingRenderer(
					null, connectionRenderer, null, null, this);
		} else {
			dimensionGroupSpacingRenderer = new DimensionGroupSpacingRenderer(
					null, connectionRenderer, null, dimensionGroupManager
							.getDimensionGroups().get(
									dimensionGroupManager
											.getCenterGroupStartIndex()), this);
		}

		dimensionGroupSpacing.setRenderer(dimensionGroupSpacingRenderer);

		dimensionGroupSpacingRenderer.setVertical(false);
		dimensionGroupSpacingRenderer.setLineLength(archSideThickness);

		for (int dimensionGroupIndex = dimensinoGroupStartIndex; dimensionGroupIndex < dimensinoGroupEndIndex; dimensionGroupIndex++) {

			DimensionGroup group = dimensionGroupManager.getDimensionGroups()
					.get(dimensionGroupIndex);

			group.getLayout().setAbsoluteSizeY(archSideThickness);
			// group.getLayout().setDebug(true);
			group.setArchHeight(-1);
			columnLayout.append(group.getLayout());

			group.setCollapsed(true);

			dimensionGroupSpacing = new ElementLayout("sideDimGrSpacing");
			// dimensionGroupSpacing.setDebug(true);
			dimensionGroupSpacing.setGrabY(true);

			dimensionGroupSpacingRenderer = new DimensionGroupSpacingRenderer(
					null, null, group, null, this);
			columnLayout.append(dimensionGroupSpacing);

			dimensionGroupSpacing.setRenderer(dimensionGroupSpacingRenderer);

			dimensionGroupSpacingRenderer.setVertical(false);
			dimensionGroupSpacingRenderer.setLineLength(archSideThickness);

		}

		layoutManager.updateLayout();

	}

	@Override
	public void initLocal(GL2 gl) {

		// Register keyboard listener to GL2 canvas
		parentGLCanvas.getParentComposite().getDisplay()
				.asyncExec(new Runnable() {
					@Override
					public void run() {
						parentGLCanvas.getParentComposite().addKeyListener(
								glKeyListener);
					}
				});

		iGLDisplayListIndexLocal = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexLocal;
		init(gl);
	}

	@Override
	public void initRemote(final GL2 gl, final AGLView glParentView,
			final GLMouseListener glMouseListener) {

	}

	@Override
	public void displayLocal(GL2 gl) {

		iGLDisplayListToCall = iGLDisplayListIndexLocal;

		if (!uninitializedDimensionGroups.isEmpty()) {
			while (uninitializedDimensionGroups.peek() != null) {
				uninitializedDimensionGroups.poll().initRemote(gl, this,
						glMouseListener);

			}

			initLayouts();
		}

		if (bIsDisplayListDirtyLocal) {
			buildDisplayList(gl, iGLDisplayListIndexLocal);
			bIsDisplayListDirtyLocal = false;
		}

		for (DimensionGroup group : dimensionGroupManager.getDimensionGroups()) {
			group.processEvents();
		}
		// brick.display(gl);

		if (!lazyMode)
			pickingManager.handlePicking(this, gl);

		display(gl);

		if (!lazyMode)
			checkForHits(gl);
	}

	@Override
	public void displayRemote(GL2 gl) {
	}

	@Override
	public void display(GL2 gl) {
		handleHorizontalMoveDragging(gl);
		// viewFrustum.getFar();
		// viewCamera.setCameraPosition(new Vec3f(-0.5f,-0.5f,-1.f));
		// GLHelperFunctions.drawPointAt(gl, 0,0,0);
		// gl.glCallList(iGLDisplayListToCall);

		if (true) {
			isLayoutDirty = false;
			centerLayoutManager.updateLayout();
			float minWidth = parentGLCanvas.getPixelGLConverter()
					.getGLWidthForPixelWidth(
							DIMENSION_GROUP_SPACING_MIN_PIXEL_WIDTH);
			for (ElementLayout layout : centerRowLayout) {
				if (!(layout.getRenderer() instanceof DimensionGroupSpacingRenderer))
					continue;
				if (resizeNecessary)
					break;

				if (layout.getSizeScaledX() < minWidth - 0.01f) {
					resizeNecessary = true;
					break;
				}
			}
		}

		if (resizeNecessary) {

			int size = centerRowLayout.size();
			if (size >= 3) {
				if (lastResizeDirectionWasToLeft) {
					dimensionGroupManager
							.setCenterGroupStartIndex(dimensionGroupManager
									.getCenterGroupStartIndex() + 1);

					float width = centerRowLayout.getElements().get(0)
							.getSizeScaledX()
							+ centerRowLayout.getElements().get(1)
									.getSizeScaledX()
							+ centerRowLayout.getElements().get(2)
									.getSizeScaledX();
					centerRowLayout.remove(0);
					centerRowLayout.remove(0);
					leftDimensionGroupSpacing = centerRowLayout.getElements()
							.get(0);

					leftDimensionGroupSpacing.setAbsoluteSizeX(width);
					((DimensionGroupSpacingRenderer) leftDimensionGroupSpacing
							.getRenderer()).setLeftDimGroup(null);
					initLeftLayout();

					// if (size == 3)
					// leftDimensionGroupSpacing.setGrabX(true);

				} else {
					dimensionGroupManager
							.setRightGroupStartIndex(dimensionGroupManager
									.getRightGroupStartIndex() - 1);

					float width = centerRowLayout.getElements().get(size - 1)
							.getSizeScaledX()
							+ centerRowLayout.getElements().get(size - 2)
									.getSizeScaledX()
							+ centerRowLayout.getElements().get(size - 3)
									.getSizeScaledX();
					centerRowLayout.remove(centerRowLayout.size() - 1);
					centerRowLayout.remove(centerRowLayout.size() - 1);
					rightDimensionGroupSpacing = centerRowLayout.getElements()
							.get(centerRowLayout.size() - 1);
					// rightDimensionGroupSpacing.setAbsoluteSizeX(width);
					rightDimensionGroupSpacing.setGrabX(true);
					((DimensionGroupSpacingRenderer) rightDimensionGroupSpacing
							.getRenderer()).setRightDimGroup(null);
					initRightLayout();

					// if (size == 3)
					// rightDimensionGroupSpacing.setGrabX(true);

				}
			}
			centerLayoutManager.updateLayout();
			resizeNecessary = false;
		}
		// float angle = 70f;
		// viewCamera.setCameraRotation(new Rotf());

		// gl.glRotatef(angle, 1, 0, 0);
		if (!isLeftDetailShown && !isRightDetailShown) {
			renderArch(gl);
		}

		for (DimensionGroup dimensionGroup : dimensionGroupManager
				.getDimensionGroups()) {
			dimensionGroup.display(gl);
		}

		if (!isLeftDetailShown && !isRightDetailShown) {
			leftLayoutManager.render(gl);
		}

		gl.glTranslatef(archInnerWidth, 0, 0);
		centerLayoutManager.render(gl);
		gl.glTranslatef(-archInnerWidth, 0, 0);

		if (!isLeftDetailShown && !isRightDetailShown) {
			float rightArchStand = (1 - ARCH_STAND_WIDTH_PERCENT)
					* viewFrustum.getWidth();
			gl.glTranslatef(rightArchStand, 0, 0);
			rightLayoutManager.render(gl);
			gl.glTranslatef(-rightArchStand, 0, 0);
		}

		// gl.glRotatef(-angle, 1, 0, 0);

		// call after all other rendering because it calls the onDrag methods
		// which need alpha blending...
		dragAndDropController.handleDragging(gl, glMouseListener);
	}

	public void switchToDetailModeLeft(DimensionGroup leftDimGr) {

		int leftIndex = dimensionGroupManager.indexOfDimensionGroup(leftDimGr);
		// int rightIndex =
		// dimensionGroupManager.indexOfDimensionGroup(rightDimGr);

		// int centerGroupStartIndex =
		// dimensionGroupManager.getCenterGroupStartIndex();
		// int rightGroupStartIndex =
		// dimensionGroupManager.getRightGroupStartIndex();

		// for(int index = leftIndex-centerGroupStartIndex; index <= leftIndex;
		// index++)
		// {
		// centerRowLayout.remove(0);
		// centerRowLayout.remove(0);
		// }
		dimensionGroupManager.setCenterGroupStartIndex(leftIndex);
		dimensionGroupManager.setRightGroupStartIndex(leftIndex + 2);

		isLeftDetailShown = true;
		// leftDimensionGroupSpacing = centerRowLayout.getElements().get(0);
		// centerRowLayout.getElements().get(2).setGrabX(true);
		//
		// ((DimensionGroupSpacingRenderer)
		// leftDimensionGroupSpacing.getRenderer())
		// .setLeftDimGroup(null);

		initLeftLayout();
		initCenterLayout();
		initRightLayout();
	}

	public void switchToDetailModeRight(DimensionGroup rightDimGr) {

		// int leftIndex =
		// dimensionGroupManager.indexOfDimensionGroup(leftDimGr);
		int rightIndex = dimensionGroupManager
				.indexOfDimensionGroup(rightDimGr);

//		int centerGroupStartIndex = dimensionGroupManager
//				.getCenterGroupStartIndex();
//		int rightGroupStartIndex = dimensionGroupManager
//				.getRightGroupStartIndex();

		// for(int index = leftIndex-centerGroupStartIndex; index <= leftIndex;
		// index++)
		// {
		// centerRowLayout.remove(0);
		// centerRowLayout.remove(0);
		// }
		dimensionGroupManager.setCenterGroupStartIndex(rightIndex - 1);
		dimensionGroupManager.setRightGroupStartIndex(rightIndex + 1);
		// leftDimensionGroupSpacing = centerRowLayout.getElements().get(0);
		// centerRowLayout.getElements().get(2).setGrabX(true);
		//
		// ((DimensionGroupSpacingRenderer)
		// leftDimensionGroupSpacing.getRenderer())
		// .setLeftDimGroup(null);
		isRightDetailShown = true;

		initLeftLayout();
		initCenterLayout();
		initRightLayout();
	}
	
	public void switchToOverviewModeLeft() {
		isLeftDetailShown = false;
//		initLeftLayout();
//		initCenterLayout();
//		initRightLayout();
	}
	
	public void switchToOverviewModeRight() {
		isRightDetailShown = false;
//		initLeftLayout();
//		initCenterLayout();
//		initRightLayout();
	}

	private void buildDisplayList(final GL2 gl, int iGLDisplayListIndex) {
		gl.glNewList(iGLDisplayListIndex, GL2.GL_COMPILE);

		renderArch(gl);

		gl.glEndList();
	}

	private void renderArch(GL2 gl) {

		// Left arch

		gl.glLineWidth(1);

		gl.glColor4f(0.5f, 0.5f, 0.5f, 1f);

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

		ArrayList<Vec3f> outputPointsTop = curve.getCurvePoints();
		outputPointsTop.add(new Vec3f(archInnerWidth, archTopY, 0));

		inputPoints.clear();
		inputPoints.add(new Vec3f(archInnerWidth, archBottomY, 0));
		inputPoints.add(new Vec3f(archSideThickness, archBottomY, 0));
		inputPoints.add(new Vec3f(archSideThickness, archBottomY * 0.8f, 0));

		curve = new NURBSCurve(inputPoints, 10);

		ArrayList<Vec3f> outputPointsBottom = new ArrayList<Vec3f>();
		outputPointsBottom.addAll(curve.getCurvePoints());

		ArrayList<Vec3f> outputPoints = new ArrayList<Vec3f>();

		outputPoints.addAll(outputPointsTop);
		outputPoints.add(new Vec3f(archInnerWidth, archBottomY, 0));
		outputPoints.addAll(outputPointsBottom);

		connectionRenderer.render(gl, outputPoints);

		gl.glColor4f(0, 0, 0, 0.8f);
		gl.glBegin(GL2.GL_LINE_STRIP);
		for (Vec3f point : outputPointsTop)
			gl.glVertex3f(point.x(), point.y(), point.z());
		gl.glEnd();

		gl.glBegin(GL2.GL_LINE_STRIP);
		for (Vec3f point : outputPointsBottom)
			gl.glVertex3f(point.x(), point.y(), point.z());
		gl.glEnd();

		gl.glColor4f(0, 0, 0, 0.8f);

		gl.glBegin(GL2.GL_LINES);
		gl.glVertex3f(0, archBottomY, 0f);
		gl.glVertex3f(0, 0, 0f);
		gl.glEnd();

		gl.glBegin(GL2.GL_LINES);
		gl.glVertex3f(archSideThickness, archBottomY * 0.8f, 0f);
		gl.glVertex3f(archSideThickness, 0, 0f);
		gl.glEnd();

		// Right arch

		gl.glColor4f(0.5f, 0.5f, 0.5f, 1f);
		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex3f(viewFrustum.getWidth(), 0, 0f);
		gl.glVertex3f(viewFrustum.getWidth(), archBottomY, 0f);
		gl.glVertex3f(viewFrustum.getWidth() - archSideThickness, archBottomY,
				0f);
		gl.glVertex3f(viewFrustum.getWidth() - archSideThickness, 0, 0f);
		gl.glEnd();

		inputPoints.clear();
		inputPoints.add(new Vec3f(viewFrustum.getWidth(), archBottomY, 0));
		inputPoints.add(new Vec3f(viewFrustum.getWidth(), archTopY, 0));
		inputPoints.add(new Vec3f(viewFrustum.getWidth() - archInnerWidth
				* 0.9f, archTopY, 0));

		curve = new NURBSCurve(inputPoints, 10);
		outputPointsTop.clear();
		outputPointsTop = curve.getCurvePoints();
		outputPointsTop.add(new Vec3f(viewFrustum.getWidth() - archInnerWidth,
				archTopY, 0));

		inputPoints.clear();
		inputPoints.add(new Vec3f(viewFrustum.getWidth() - archInnerWidth,
				archBottomY, 0));
		inputPoints.add(new Vec3f(viewFrustum.getWidth() - archSideThickness,
				archBottomY, 0));
		inputPoints.add(new Vec3f(viewFrustum.getWidth() - archSideThickness,
				archBottomY * 0.8f, 0));

		curve = new NURBSCurve(inputPoints, 10);

		outputPointsBottom.clear();
		outputPointsBottom = new ArrayList<Vec3f>();
		outputPointsBottom.addAll(curve.getCurvePoints());

		outputPoints.clear();

		outputPoints.addAll(outputPointsTop);
		outputPoints.add(new Vec3f(viewFrustum.getWidth() - archInnerWidth,
				archBottomY, 0));
		outputPoints.addAll(outputPointsBottom);

		connectionRenderer.render(gl, outputPoints);

		gl.glColor4f(0, 0, 0, 0.8f);
		gl.glBegin(GL2.GL_LINE_STRIP);
		for (Vec3f point : outputPointsTop)
			gl.glVertex3f(point.x(), point.y(), point.z());
		gl.glEnd();

		gl.glBegin(GL2.GL_LINE_STRIP);
		for (Vec3f point : outputPointsBottom)
			gl.glVertex3f(point.x(), point.y(), point.z());
		gl.glEnd();

		gl.glBegin(GL2.GL_LINES);
		gl.glVertex3f(viewFrustum.getWidth(), 0, 0f);
		gl.glVertex3f(viewFrustum.getWidth(), archBottomY, 0f);
		gl.glEnd();

		gl.glBegin(GL2.GL_LINES);
		gl.glVertex3f(viewFrustum.getWidth() - archSideThickness,
				archBottomY * 0.8f, 0.01f);
		gl.glVertex3f(viewFrustum.getWidth() - archSideThickness, 0, 0.1f);
		gl.glEnd();
	}

	/**
	 * Handles the up-down dragging of the whole dimension group
	 * 
	 * @param gl
	 */
	private void handleHorizontalMoveDragging(GL2 gl) {
		if (!isHorizontalMoveDraggingActive)
			return;
		if (glMouseListener.wasMouseReleased()) {
			isHorizontalMoveDraggingActive = false;
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

		float change = pointCordinates[0] - previousXCoordinate;

		// float change = -0.1f;
		// isHorizontalMoveDraggingActive = false;
		if (change > 0)
			lastResizeDirectionWasToLeft = false;
		else
			lastResizeDirectionWasToLeft = true;
		previousXCoordinate = pointCordinates[0];

		ElementLayout leftSpacing = null;
		int leftIndex = 0;
		int rightIndex = 0;
		ElementLayout rightSpacing = null;

		DimensionGroupSpacingRenderer spacingRenderer;
		int count = 0;
		for (ElementLayout layout : centerRowLayout) {
			if (layout.getRenderer() instanceof DimensionGroupSpacingRenderer) {
				spacingRenderer = (DimensionGroupSpacingRenderer) layout
						.getRenderer();
				if (spacingRenderer.getRightDimGroup() != null) {
					if (spacingRenderer.getRightDimGroup().getID() == movedDimensionGroup) {
						leftSpacing = layout;
						leftIndex = count;

					}
				}
				if (spacingRenderer.getLeftDimGroup() != null) {
					if (spacingRenderer.getLeftDimGroup().getID() == movedDimensionGroup) {
						rightSpacing = layout;
						rightIndex = count;

					}
				}
				if (count < centerRowLayout.size() - 1) {
					layout.setGrabX(false);
					layout.setAbsoluteSizeX(layout.getSizeScaledX());
				} else
					layout.setGrabX(true);
			}
			count++;
		}

		if (leftSpacing == null || rightSpacing == null)
			return;

		float leftSizeX = leftSpacing.getSizeScaledX();
		float rightSizeX = rightSpacing.getSizeScaledX();
		float minWidth = parentGLCanvas.getPixelGLConverter()
				.getGLWidthForPixelWidth(
						DIMENSION_GROUP_SPACING_MIN_PIXEL_WIDTH);

		if (change > 0) {
			if (rightSizeX - change > minWidth) {
				rightSpacing.setAbsoluteSizeX(rightSizeX - change);
			} else {
				rightSpacing.setAbsoluteSizeX(minWidth);
				float savedSize = rightSizeX - minWidth;
				float remainingChange = change - savedSize;

				while (remainingChange > 0) {
					if (centerRowLayout.size() < rightIndex + 2)
						break;

					rightIndex += 2;
					ElementLayout spacing = centerRowLayout.getElements().get(
							rightIndex);
					if (spacing.getSizeScaledX() - remainingChange > minWidth + 0.001f) {
						spacing.setAbsoluteSizeX(spacing.getSizeScaledX()
								- remainingChange);
						remainingChange = -1;
					} else {
						savedSize = spacing.getSizeScaledX() - minWidth;
						remainingChange -= savedSize;
						if (rightIndex == centerRowLayout.size() - 1)
							spacing.setAbsoluteSizeX(0f);
						else
							spacing.setAbsoluteSizeX(minWidth);
					}
				}
			}
			leftSpacing.setAbsoluteSizeX(leftSizeX + change);
		} else {

			if (leftSizeX + change > minWidth) {
				leftSpacing.setAbsoluteSizeX(leftSizeX + change);
			} else {
				leftSpacing.setAbsoluteSizeX(minWidth);
				float savedSize = leftSizeX - minWidth;
				float remainingChange = change + savedSize;

				while (remainingChange < 0) {
					if (leftIndex < 2)
						break;

					leftIndex -= 2;
					ElementLayout spacing = centerRowLayout.getElements().get(
							leftIndex);
					if (spacing.getSizeScaledX() + remainingChange > minWidth + 0.001f) {
						spacing.setAbsoluteSizeX(spacing.getSizeScaledX()
								+ remainingChange);
						remainingChange = 1;
					} else {
						savedSize = spacing.getSizeScaledX() + minWidth;
						remainingChange += savedSize;
						if (leftIndex == 0)
							spacing.setAbsoluteSizeX(0);
						else
							spacing.setAbsoluteSizeX(minWidth);
					}
				}
			}
			rightSpacing.setAbsoluteSizeX(rightSizeX - change);
		}

		updateLayout();
	}

	@Override
	protected void handlePickingEvents(EPickingType pickingType,
			EPickingMode pickingMode, int externalID, Pick pick) {

		if (detailLevel == DetailLevel.VERY_LOW) {
			return;
		}

		selectedConnectionBandID = -1;

		switch (pickingType) {

		case BRICK_CONNECTION_BAND:
			switch (pickingMode) {

			case DOUBLE_CLICKED:
				System.out.println("Switch to detail mode.");
				break;

			case CLICKED:
				selectedConnectionBandID = externalID;
				selectElementsByConnectionBandID(selectedConnectionBandID);
				break;

			}
		case DIMENSION_GROUP:
			switch (pickingMode) {
			case MOUSE_OVER:

				System.out.println("Mouse over");
				break;
			case CLICKED:

				dragAndDropController.setDraggingStartPosition(pick
						.getPickedPoint());
				dragAndDropController
						.addDraggable((DimensionGroup) generalManager
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
			break;

		case MOVE_HORIZONTALLY_HANDLE:
			if (pickingMode == EPickingMode.CLICKED) {
				isHorizontalMoveDraggingActive = true;
				movedDimensionGroup = externalID;
			}
			break;
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

		clearSelectionsListener = new ClearSelectionsListener();
		clearSelectionsListener.setHandler(this);
		eventPublisher.addListener(ClearSelectionsEvent.class,
				clearSelectionsListener);

		trendHighlightModeListener = new ConnectionsModeListener();
		trendHighlightModeListener.setHandler(this);
		eventPublisher.addListener(ConnectionsModeEvent.class,
				trendHighlightModeListener);

	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();

		if (metaSetsListener != null) {
			eventPublisher.removeListener(metaSetsListener);
			metaSetsListener = null;
		}

		if (clearSelectionsListener != null) {
			eventPublisher.removeListener(clearSelectionsListener);
			clearSelectionsListener = null;
		}

		if (trendHighlightModeListener != null) {
			eventPublisher.removeListener(trendHighlightModeListener);
			trendHighlightModeListener = null;
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
		clearAllSelections();

	}

	@Override
	public void clearAllSelections() {
		contentSelectionManager.clearSelections();
		updateConnectionLinesBetweenDimensionGroups();
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

		ClusterTree storageTree = dataDomain.getSet()
				.getStorageData(storageVAType).getStorageTree();
		if (storageTree == null)
			return;

		ArrayList<ISet> allMetaSets = storageTree.getRoot()
				.getAllMetaSetsFromSubTree();

		ArrayList<ISet> filteredMetaSets = new ArrayList<ISet>(
				allMetaSets.size() / 2);

		for (ISet metaSet : allMetaSets) {
			if (metaSet.size() > 1
					&& metaSet.size() != dataDomain.getSet().size())
				filteredMetaSets.add(metaSet);
		}
		initializeBricks(filteredMetaSets);

	}

	private void initializeBricks(ArrayList<ISet> metaSets) {

		ArrayList<DimensionGroup> dimensionGroups = dimensionGroupManager
				.getDimensionGroups();
		Iterator<DimensionGroup> dimensionGroupIterator = dimensionGroups
				.iterator();
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
							new ViewFrustum(ECameraProjectionMode.ORTHOGRAPHIC,
									0, 1, 0, 1, -1, 1));

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
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {

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

				dimensionGroupManager
						.setCenterGroupStartIndex(dimensionGroupManager
								.getCenterGroupStartIndex() + 1);

				dimensionGroups.remove(movedDimGroup);
				if (referenceDimGroup == null) {
					dimensionGroups.add(0, movedDimGroup);
				} else {
					dimensionGroups.add(
							dimensionGroups.indexOf(referenceDimGroup),
							movedDimGroup);
				}

				insertComplete = true;
				break;
			}
		}

		if (!insertComplete) {
			for (ElementLayout rightLayout : rightColumnLayout.getElements()) {
				if (spacer == rightLayout.getRenderer()) {

					dimensionGroupManager
							.setRightGroupStartIndex(dimensionGroupManager
									.getRightGroupStartIndex() - 1);

					dimensionGroups.remove(movedDimGroup);
					if (referenceDimGroup == null) {
						dimensionGroups.add(dimensionGroups.size(),
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
						dimensionGroups.add(dimensionGroupManager
								.getCenterGroupStartIndex(), movedDimGroup);
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
		// initLayouts();

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
					((DimensionGroupSpacingRenderer) elementLayout
							.getRenderer()).init();
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
	public void setLastResizeDirectionWasToLeft(
			boolean lastResizeDirectionWasToLeft) {
		this.lastResizeDirectionWasToLeft = lastResizeDirectionWasToLeft;
	}

	public float getArchTopY() {
		return archTopY;
	}

	public float getArchBottomY() {
		return archBottomY;
	}

	public ContentSelectionManager getContentSelectionManager() {
		return contentSelectionManager;
	}

	public GLVisBricksKeyListener getKeyListener() {
		return (GLVisBricksKeyListener) glKeyListener;
	}

	public void handleTrendHighlightMode(boolean connectionsOn,
			boolean connectionsHighlightDynamic, float focusFactor) {

		this.connectionsOn = connectionsOn;
		this.connectionsHighlightDynamic = connectionsHighlightDynamic;
		this.connectionsFocusFactor = focusFactor;

		updateConnectionLinesBetweenDimensionGroups();
	}

	public boolean isConnectionsOn() {
		return connectionsOn;
	}

	public boolean isConnectionsHighlightDynamic() {
		return connectionsHighlightDynamic;
	}

	public float getConnectionsFocusFactor() {
		return connectionsFocusFactor;
	}

	public int getSelectedConnectionBandID() {
		return selectedConnectionBandID;
	}

	public HashMap<Integer, ContentVirtualArray> getHashConnectionBandIDToContentVA() {
		return hashConnectionBandIDToContentVA;
	}

	private void selectElementsByConnectionBandID(int connectionBandID) {

		contentSelectionManager.clearSelections();

		ClearSelectionsEvent cse = new ClearSelectionsEvent();
		cse.setSender(this);
		eventPublisher.triggerEvent(cse);

		// Create volatile selection type
		volatieBandSelectionType = new SelectionType(
				"Volatile band selection type", contentSelectionManager
						.getSelectionType().getColor(), 1, true, true, 1);

		volatieBandSelectionType.setManaged(false);

		SelectionTypeEvent selectionTypeEvent = new SelectionTypeEvent(
				volatieBandSelectionType);
		GeneralManager.get().getEventPublisher()
				.triggerEvent(selectionTypeEvent);

		for (Integer contentID : hashConnectionBandIDToContentVA
				.get(connectionBandID)) {
			contentSelectionManager.addToType(volatieBandSelectionType,
					contentID);
		}

		SelectionUpdateEvent event = new SelectionUpdateEvent();
		event.setDataDomainType(getDataDomain().getDataDomainType());
		event.setSender(this);
		SelectionDelta delta = contentSelectionManager.getDelta();
		event.setSelectionDelta(delta);
		GeneralManager.get().getEventPublisher().triggerEvent(event);

		updateConnectionLinesBetweenDimensionGroups();
	}
}
