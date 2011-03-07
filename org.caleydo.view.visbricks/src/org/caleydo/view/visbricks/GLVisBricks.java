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
import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.DetailLevel;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.Template;
import org.caleydo.core.view.opengl.layout.TemplateRenderer;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.spline.ConnectionBandRenderer;
import org.caleydo.core.view.opengl.util.spline.IConnectionRenderer;
import org.caleydo.core.view.opengl.util.vislink.NURBSCurve;
import org.caleydo.view.visbricks.brick.GLBrick;
import org.caleydo.view.visbricks.dimensiongroup.DimensionGroup;
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

	private NewMetaSetsListener metaSetsListener;

	private ASetBasedDataDomain dataDomain;

	private VisBricksRenderStyle renderStyle;

	private IConnectionRenderer connectionRenderer;

	private ArrayList<DimensionGroup> centerGroups;
	private ArrayList<DimensionGroup> leftBrickList;
	private ArrayList<DimensionGroup> rightBrickList;

	private TemplateRenderer centerLayoutRenderer;
	private TemplateRenderer leftLayoutRenderer;
	private TemplateRenderer rightLayoutRenderer;

	private Template centerLayout;
	private Template leftLayout;
	private Template rightLayout;

	private float archWidth = 0;
	private float archInnerWidth = 0;
	private float archTopY = 0;
	private float archBottomY = 0;
	private float archHeight = 0;

	private Queue<DimensionGroup> uninitializedDimensionGroups = new LinkedList<DimensionGroup>();

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param sLabel
	 * @param viewFrustum
	 */
	public GLVisBricks(GLCaleydoCanvas glCanvas, final ViewFrustum viewFrustum) {
		super(glCanvas, viewFrustum, true);

		centerGroups = new ArrayList<DimensionGroup>(20);
		leftBrickList = new ArrayList<DimensionGroup>(10);
		rightBrickList = new ArrayList<DimensionGroup>(10);

		viewType = GLVisBricks.VIEW_ID;

		connectionRenderer = new ConnectionBandRenderer();
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
		archInnerWidth = viewFrustum.getWidth() * (ARCH_STAND_WIDTH_PERCENT + 0.1f);
		archTopY = viewFrustum.getHeight() * ARCH_TOP_PERCENT;
		archBottomY = viewFrustum.getHeight() * ARCH_BOTTOM_PERCENT;
		archHeight = (ARCH_TOP_PERCENT - ARCH_BOTTOM_PERCENT) * viewFrustum.getHeight();

		float centerLayoutWidth = viewFrustum.getWidth() - 2 * archInnerWidth;

		float dimensionGroupLayoutRatio = 1f / centerGroups.size();

		Row rowLayout = new Row("centerArchRow");
		rowLayout.setFrameColor(1, 1, 0, 1);

		for (DimensionGroup group : centerGroups) {
			group.getLayout().setRatioSizeX(dimensionGroupLayoutRatio);
			group.setArchBounds(archHeight, ARCH_BOTTOM_PERCENT, ARCH_TOP_PERCENT
					- ARCH_BOTTOM_PERCENT, ARCH_BOTTOM_PERCENT);
			rowLayout.appendElement(group.getLayout());
		}

		centerLayout = new Template();
		centerLayout.setPixelGLConverter(parentGLCanvas.getPixelGLConverter());
		centerLayout.setBaseElementLayout(rowLayout);

		ViewFrustum centerArchFrustum = new ViewFrustum(viewFrustum.getProjectionMode(),
				0, centerLayoutWidth, 0, viewFrustum.getHeight(), 0, 1);
		centerLayoutRenderer = new TemplateRenderer(centerArchFrustum);
		centerLayoutRenderer.setTemplate(centerLayout);
		if (uninitializedDimensionGroups.size() == 0)
			centerLayoutRenderer.updateLayout();
	}

	private void initLayoutLeft() {

		// int dimensionGroupCount = 2;
		// float dimensionGroupLayoutRatio = 1f / dimensionGroupCount;
		//
		// Column columnLayout = new Column("leftArchColumn");
		// columnLayout.setFrameColor(1, 1, 0, 1);
		//
		// for (int i = 0; i < dimensionGroupCount; i++) {
		//
		// ViewFrustum brickFrustum = new
		// ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC,
		// 0, 0, 0, 0, -4, 4);
		//
		// GLBrick leftBrick = (GLBrick)
		// GeneralManager.get().getViewGLCanvasManager()
		// .createGLView(GLBrick.class, this.getParentGLCanvas(), brickFrustum);
		// leftBrick.setRemoteRenderingGLView(this);
		// ElementLayout brickLayout = new ElementLayout("brick");
		// BrickRenderer brickRenderer = new BrickRenderer(leftBrick);
		// brickLayout.setRenderer(brickRenderer);
		// brickLayout.setFrameColor(1, 0, 0, 1);
		// brickLayout.setRatioSizeY(dimensionGroupLayoutRatio);
		//
		// columnLayout.appendElement(brickLayout);
		// }
		//
		// leftLayout = new Template();
		// leftLayout.setPixelGLConverter(pixelGLConverter);
		// leftLayout.setBaseElementLayout(columnLayout);
		//
		// ViewFrustum leftArchFrustum = new
		// ViewFrustum(viewFrustum.getProjectionMode(), 0,
		// ARCH_STAND_WIDTH_PERCENT * viewFrustum.getWidth(), 0,
		// viewFrustum.getHeight() * ARCH_BOTTOM_PERCENT, 0, 1);
		// leftLayoutRenderer = new TemplateRenderer(leftArchFrustum);
		// leftLayoutRenderer.setTemplate(leftLayout);
		// leftLayoutRenderer.updateLayout();
	}

	private void initLayoutRight() {
		// int dimensionGroupCount = 4;
		// float dimensionGroupLayoutRatio = 1f / dimensionGroupCount;
		//
		// Column columnLayout = new Column("leftArchColumn");
		// columnLayout.setFrameColor(1, 1, 0, 1);
		//
		// for (int i = 0; i < dimensionGroupCount; i++) {
		//
		// ViewFrustum brickFrustum = new
		// ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC,
		// 0, 0, 0, 0, -4, 4);
		//
		// GLBrick rightBrick = (GLBrick)
		// GeneralManager.get().getViewGLCanvasManager()
		// .createGLView(GLBrick.class, this.getParentGLCanvas(), brickFrustum);
		// rightBrick.setRemoteRenderingGLView(this);
		// ElementLayout brickLayout = new ElementLayout("brick");
		// BrickRenderer brickRenderer = new BrickRenderer(rightBrick);
		// brickLayout.setRenderer(brickRenderer);
		// brickLayout.setFrameColor(1, 0, 0, 1);
		// brickLayout.setRatioSizeY(dimensionGroupLayoutRatio);
		//
		// columnLayout.appendElement(brickLayout);
		// }
		//
		// rightLayout = new Template();
		// rightLayout.setPixelGLConverter(pixelGLConverter);
		// rightLayout.setBaseElementLayout(columnLayout);
		//
		// ViewFrustum leftArchFrustum = new
		// ViewFrustum(viewFrustum.getProjectionMode(), 0,
		// ARCH_STAND_WIDTH_PERCENT * viewFrustum.getWidth(), 0,
		// viewFrustum.getHeight() * ARCH_BOTTOM_PERCENT, 0, 1);
		// rightLayoutRenderer = new TemplateRenderer(leftArchFrustum);
		// rightLayoutRenderer.setTemplate(rightLayout);
		// rightLayoutRenderer.updateLayout();
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

		}
		if (bIsDisplayListDirtyRemote == true) {
			initLayoutCenter();
			bIsDisplayListDirtyRemote = false;
		}
		for (DimensionGroup group : centerGroups) {
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

		for (DimensionGroup group : centerGroups) {
			group.display(gl);
		}

		// leftLayoutRenderer.render(gl);

		gl.glTranslatef(archInnerWidth, 0, 0);
		centerLayoutRenderer.render(gl);
		gl.glTranslatef(-archInnerWidth, 0, 0);

		// float rightArchStand = (1 - ARCH_STAND_WIDTH_PERCENT) *
		// viewFrustum.getWidth();
		// gl.glTranslatef(rightArchStand, 0, 0);
		// rightLayoutRenderer.render(gl);
		// gl.glTranslatef(-rightArchStand, 0, 0);

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
			EPickingMode pickingMode, int iExternalID, Pick pick) {

		// TODO: Implement picking processing here!
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

		System.out.println("MetaSets update");

	}

	private void initializeBricks(ArrayList<ISet> metaSets) {
		centerGroups.clear();
		for (ISet set : metaSets) {

			// TODO here we need to check which metaSets have already been
			// assigned to a dimensiongroup and not re-create them
			DimensionGroup dimensionGroup = (DimensionGroup) GeneralManager
					.get()
					.getViewGLCanvasManager()
					.createGLView(
							DimensionGroup.class,
							getParentGLCanvas(),
							new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC, 0, 1, 0,
									1, -1, 1));

			dimensionGroup.setDataDomain(dataDomain);
			dimensionGroup.setSet(set);
			dimensionGroup.setRemoteRenderingGLView(this);
			dimensionGroup.initialize();
			//
			centerGroups.add(dimensionGroup);

			uninitializedDimensionGroups.add(dimensionGroup);

		}

		// for(GLBrick brick : centerBrickList)
		// {
		//
		// }

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

		return "Template Caleydo View";
	}

	@Override
	public String getDetailedInfo() {
		return "Template Caleydo View";

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
}
