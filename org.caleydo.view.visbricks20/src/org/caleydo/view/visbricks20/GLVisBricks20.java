package org.caleydo.view.visbricks20;

import java.util.ArrayList;
import java.util.List;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.awt.GLCanvas;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutManager;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.ViewLayoutRenderer;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.view.dvi.GLDataViewIntegrator;
import org.caleydo.view.dvi.event.OpenVendingMachineEvent;
import org.caleydo.view.stratomex.GLStratomex;
import org.caleydo.view.stratomex.brick.configurer.IBrickConfigurer;
import org.caleydo.view.stratomex.column.BrickColumn;
import org.caleydo.view.stratomex.event.AddGroupsToStratomexEvent;
import org.caleydo.view.visbricks20.listener.AddGroupsToStratomexListener;
import org.caleydo.view.visbricks20.listener.OpenVendingMachineListener;
import org.caleydo.view.visbricks20.renderstyle.VisBricks20RenderStyle;
import org.eclipse.swt.widgets.Composite;

/**
 * <p>
 * VisBricks 2.0 view.
 * </p>
 * 
 * @author Marc Streit
 */

public class GLVisBricks20 extends AGLView implements IGLRemoteRenderingView {

	public static String VIEW_TYPE = "org.caleydo.view.visbricks20";

	public static String VIEW_NAME = "VisBricks 2.0";

	private VisBricks20RenderStyle renderStyle;

	private LayoutManager layoutManager;
	private Column mainColumn;

	private GLDataViewIntegrator dvi;
	private ElementLayout dviElementLayout;

	private GLStratomex visBricks;
	private ElementLayout visBricksElementLayout;

	private GLVendingMachine vendingMachine;
	private ElementLayout vendingMachineElementLayout;

	private AddGroupsToStratomexListener addGroupsToVisBricksListener;

	private OpenVendingMachineListener openVendingMachineListener;

	private BrickColumn detailDimensionGroup;

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param viewLabel
	 * @param viewFrustum
	 */
	public GLVisBricks20(GLCanvas glCanvas, Composite parentComposite,
			ViewFrustum viewFrustum) {

		super(glCanvas, parentComposite, viewFrustum, VIEW_TYPE, VIEW_NAME);

	}

	@Override
	public void init(GL2 gl) {
		displayListIndex = gl.glGenLists(1);
		renderStyle = new VisBricks20RenderStyle(viewFrustum);

		super.renderStyle = renderStyle;
		detailLevel = EDetailLevel.HIGH;

		initLayouts();

		dvi.initRemote(gl, this, glMouseListener);
		visBricks.initRemote(gl, this, glMouseListener);
		vendingMachine.initRemote(gl, this, glMouseListener);
	}

	@Override
	public void initLocal(GL2 gl) {
		init(gl);
	}

	@Override
	public void initRemote(final GL2 gl, final AGLView glParentView,
			final GLMouseListener glMouseListener) {

		// Register keyboard listener to GL2 canvas
		glParentView.getParentComposite().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				glParentView.getParentComposite().addKeyListener(glKeyListener);
			}
		});

		this.glMouseListener = glMouseListener;

		init(gl);
	}

	@Override
	public void displayLocal(GL2 gl) {

		dvi.processEvents();
		visBricks.processEvents();
		vendingMachine.processEvents();

		pickingManager.handlePicking(this, gl);

		display(gl);
		if (busyState != EBusyState.OFF) {
			renderBusyMode(gl);
		}

		checkForHits(gl);
	}

	private void initLayouts() {

		layoutManager = new LayoutManager(viewFrustum, pixelGLConverter);

		mainColumn = new Column("baseElementLayout");
		mainColumn.setBottomUp(false);
		layoutManager.setBaseElementLayout(mainColumn);

		dviElementLayout = new Row("dviElementLayoutRow");
		createDVI(dviElementLayout);

		visBricksElementLayout = new Row("visBricksElementLayoutRow");
		// visBricksElementLayout.setDebug(true);
		createVisBricks(visBricksElementLayout);

		// Just for testing vending machine
		// vendingMachineElementLayout = new
		// Row("vendingMachineElementLayoutRow");
		// vendingMachineElementLayout.setDebug(true);
		createVendingMachine(vendingMachineElementLayout);

		mainColumn.append(dviElementLayout);
		mainColumn.append(visBricksElementLayout);
		// mainColumn.append(vendingMachineElementLayout);

		layoutManager.updateLayout();
	}

	/**
	 * Creates DVI view
	 * 
	 * @param wrappingLayout
	 * @return
	 */
	private GLDataViewIntegrator createDVI(ElementLayout wrappingLayout) {
		ViewFrustum frustum = new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC, 0, 1, 0,
				1, -4, 4);
		dvi = (GLDataViewIntegrator) GeneralManager
				.get()
				.getViewManager()
				.createGLView(GLDataViewIntegrator.class, parentGLCanvas,
						parentComposite, frustum);

		dvi.setVendingMachineMode(true);
		dvi.setRemoteRenderingGLView(this);
		dvi.initialize();

		ViewLayoutRenderer dviRenderer = new ViewLayoutRenderer(dvi);
		wrappingLayout.setRenderer(dviRenderer);
		wrappingLayout.setPixelSizeY(300);

		return dvi;
	}

	/**
	 * Creates VisBricks view
	 * 
	 * @param wrappingLayout
	 * @return
	 */
	private GLStratomex createVisBricks(ElementLayout wrappingLayout) {
		ViewFrustum frustum = new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC, 0, 1, 0,
				1, -4, 4);
		visBricks = (GLStratomex) GeneralManager
				.get()
				.getViewManager()
				.createGLView(GLStratomex.class, parentGLCanvas, parentComposite, frustum);

		visBricks.setRemoteRenderingGLView(this);
		visBricks.initialize();

		ViewLayoutRenderer visBricksRenderer = new ViewLayoutRenderer(visBricks);
		wrappingLayout.setRenderer(visBricksRenderer);

		return visBricks;
	}

	/**
	 * Creates vending machine view
	 * 
	 * @param wrappingLayout
	 * @return
	 */
	private GLVendingMachine createVendingMachine(ElementLayout wrappingLayout) {
		ViewFrustum frustum = new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC, 0, 1, 0,
				1, -4, 4);
		vendingMachine = (GLVendingMachine) GeneralManager
				.get()
				.getViewManager()
				.createGLView(GLVendingMachine.class, parentGLCanvas, parentComposite,
						frustum);

		vendingMachine.setRemoteRenderingGLView(this);
		vendingMachine.initialize();
		vendingMachine.setDimensionGroupManager(visBricks.getDimensionGroupManager());

		// ViewLayoutRenderer vendingMachineRenderer = new
		// ViewLayoutRenderer(vendingMachine);
		// wrappingLayout.setRenderer(vendingMachineRenderer);

		// Zoomer zoomer = new Zoomer(vendingMachine, wrappingLayout);
		// wrappingLayout.setZoomer(zoomer);

		return vendingMachine;
	}

	@Override
	public void displayRemote(GL2 gl) {
		display(gl);
	}

	@Override
	public void display(GL2 gl) {

		layoutManager.render(gl);
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedVisBricks20View serializedForm = new SerializedVisBricks20View();
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

		addGroupsToVisBricksListener = new AddGroupsToStratomexListener();
		addGroupsToVisBricksListener.setHandler(this);
		eventPublisher.addListener(AddGroupsToStratomexEvent.class,
				addGroupsToVisBricksListener);

		openVendingMachineListener = new OpenVendingMachineListener();
		openVendingMachineListener.setHandler(this);
		eventPublisher.addListener(OpenVendingMachineEvent.class,
				openVendingMachineListener);
	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();

		if (addGroupsToVisBricksListener != null) {
			eventPublisher.removeListener(addGroupsToVisBricksListener);
			addGroupsToVisBricksListener = null;
		}

		if (openVendingMachineListener != null) {
			eventPublisher.removeListener(openVendingMachineListener);
			openVendingMachineListener = null;
		}
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		super.reshape(drawable, x, y, width, height);

		layoutManager.updateLayout();
		visBricks.initLayouts();
		visBricks.updateLayout();
	}

	@Override
	public List<AGLView> getRemoteRenderedViews() {

		return null;
	}

	@Override
	public int getNumberOfSelections(SelectionType SelectionType) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void handleOpenVendingMachineEvent(IDataDomain dataDomain) {

		ATableBasedDataDomain tableBasedDataDomain = (ATableBasedDataDomain) dataDomain;

		// For the vending machine it does not matter which record perspective
		// we take
		TablePerspective tablePerspective = tableBasedDataDomain.getTablePerspective(
				tableBasedDataDomain.getTable().getDefaultRecordPerspective().getPerspectiveID(),
				tableBasedDataDomain.getTable().getDefaultDimensionPerspective().getPerspectiveID());

		List<TablePerspective> tablePerspectiveWrapper = new ArrayList<TablePerspective>();
		tablePerspectiveWrapper.add(tablePerspective);
		addDimensionGroups(tablePerspectiveWrapper, null);
		visBricks.addTablePerspectives(tablePerspectiveWrapper, null);

		vendingMachine.setTablePerspective(tablePerspective);
	}

	public void addDimensionGroups(List<TablePerspective> tablePerspectives,
			IBrickConfigurer dataConfigurer) {

		visBricks.addTablePerspectives(tablePerspectives, dataConfigurer);

		// Show dimension group as detail brick
		for (BrickColumn dimGroup : visBricks.getDimensionGroupManager()
				.getBrickColumns()) {
			if (tablePerspectives.get(0) == dimGroup.getTablePerspective()) {
				detailDimensionGroup = dimGroup;
				detailDimensionGroup.showDetailedBrick(vendingMachine, false);
				break;
			}
		}
		layoutManager.updateLayout();
	}

	public void vendingMachineSelectionFinished() {

		detailDimensionGroup.hideDetailedBrick();
		visBricks.updateLayout();
	}

	/**
	 * @return the visBricks, see {@link #visBricks}
	 */
	public GLStratomex getVisBricks() {
		return visBricks;
	}

	@Override
	protected void destroyViewSpecificContent(GL2 gl) {
		layoutManager.destroy(gl);
	}
}
