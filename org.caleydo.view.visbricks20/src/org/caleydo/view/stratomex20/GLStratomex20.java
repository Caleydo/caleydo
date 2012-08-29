package org.caleydo.view.stratomex20;

import java.util.ArrayList;
import java.util.List;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.awt.GLCanvas;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.IMultiTablePerspectiveBasedView;
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
import org.caleydo.view.stratomex20.listener.AddGroupsToStratomexListener;
import org.caleydo.view.stratomex20.listener.OpenVendingMachineListener;
import org.caleydo.view.stratomex20.renderstyle.Stratomex20RenderStyle;
import org.eclipse.swt.widgets.Composite;

/**
 * <p>
 * StreomeX 2.0 experimental view.
 * </p>
 * 
 * @author Marc Streit
 */

public class GLStratomex20
	extends AGLView
	implements IGLRemoteRenderingView, IMultiTablePerspectiveBasedView {

	public static String VIEW_TYPE = "org.caleydo.view.stratomex20";

	public static String VIEW_NAME = "StratomeX 2.0";

	private Stratomex20RenderStyle renderStyle;

	private LayoutManager layoutManager;
	private Column mainColumn;
	private Row mainRow;

	private GLDataViewIntegrator dvi;
	private ElementLayout dviElementLayout;

	private GLStratomex stratomex;
	private ElementLayout stratomexElementLayout;

	private GLVendingMachine vendingMachine;

	private AddGroupsToStratomexListener addGroupsToStratomexListener;

	private OpenVendingMachineListener openVendingMachineListener;

	private BrickColumn detailDimensionGroup;

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param viewLabel
	 * @param viewFrustum
	 */
	public GLStratomex20(GLCanvas glCanvas, Composite parentComposite, ViewFrustum viewFrustum) {

		super(glCanvas, parentComposite, viewFrustum, VIEW_TYPE, VIEW_NAME);

	}

	@Override
	public void init(GL2 gl) {
		displayListIndex = gl.glGenLists(1);
		renderStyle = new Stratomex20RenderStyle(viewFrustum);

		super.renderStyle = renderStyle;
		detailLevel = EDetailLevel.HIGH;

		initLayouts();

		dvi.initRemote(gl, this, glMouseListener);
		stratomex.initRemote(gl, this, glMouseListener);
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
		stratomex.processEvents();
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

		mainColumn.append(dviElementLayout);

		mainRow = new Row("rowLayout");
		//mainRow.setDebug(true);
		mainColumn.append(mainRow);

		stratomexElementLayout = new Row("stratomexElementLayoutRow");
		//stratomexElementLayout.setDebug(true);
		stratomexElementLayout.setRatioSizeX(0.7f);
		createStratomex(stratomexElementLayout);
		createVendingMachine();
		vendingMachine.initLayouts();
		mainRow.append(stratomexElementLayout);
		mainRow.append(vendingMachine.getLayout());
	}

	/**
	 * Creates DVI view
	 * 
	 * @param wrappingLayout
	 * @return
	 */
	private GLDataViewIntegrator createDVI(ElementLayout wrappingLayout) {
		ViewFrustum frustum = new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC, 0, 1, 0, 1,
				-4, 4);
		dvi = (GLDataViewIntegrator) GeneralManager
				.get()
				.getViewManager()
				.createGLView(GLDataViewIntegrator.class, parentGLCanvas, parentComposite,
						frustum);

		dvi.setVendingMachineMode(true);
		dvi.setRemoteRenderingGLView(this);
		dvi.initialize();

		ViewLayoutRenderer dviRenderer = new ViewLayoutRenderer(dvi);
		wrappingLayout.setRenderer(dviRenderer);
		wrappingLayout.setPixelSizeY(300);

		return dvi;
	}

	/**
	 * Creates Stratomex view
	 * 
	 * @param wrappingLayout
	 * @return
	 */
	private GLStratomex createStratomex(ElementLayout wrappingLayout) {
		ViewFrustum frustum = new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC, 0, 1, 0, 1,
				-4, 4);
		stratomex = (GLStratomex) GeneralManager.get().getViewManager()
				.createGLView(GLStratomex.class, parentGLCanvas, parentComposite, frustum);

		stratomex.setRemoteRenderingGLView(this);
		stratomex.initialize();

		ViewLayoutRenderer stratomexRenderer = new ViewLayoutRenderer(stratomex);
		wrappingLayout.setRenderer(stratomexRenderer);

		return stratomex;
	}

	/**
	 * Creates vending machine view
	 * 
	 * @param wrappingLayout
	 * @return
	 */
	private GLVendingMachine createVendingMachine() {
		ViewFrustum frustum = new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC, 0, 1, 0, 1,
				-4, 4);
		vendingMachine = (GLVendingMachine) GeneralManager
				.get()
				.getViewManager()
				.createGLView(GLVendingMachine.class, parentGLCanvas, parentComposite, frustum);

		vendingMachine.setRemoteRenderingGLView(this);
		vendingMachine.initialize();
		vendingMachine.setStratomex(stratomex);
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
		SerializedStratomex20View serializedForm = new SerializedStratomex20View();
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

		addGroupsToStratomexListener = new AddGroupsToStratomexListener();
		addGroupsToStratomexListener.setHandler(this);
		eventPublisher.addListener(AddGroupsToStratomexEvent.class,
				addGroupsToStratomexListener);

		openVendingMachineListener = new OpenVendingMachineListener();
		openVendingMachineListener.setHandler(this);
		eventPublisher.addListener(OpenVendingMachineEvent.class, openVendingMachineListener);
	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();

		if (addGroupsToStratomexListener != null) {
			eventPublisher.removeListener(addGroupsToStratomexListener);
			addGroupsToStratomexListener = null;
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
		stratomex.initLayouts();
		stratomex.updateLayout();
		vendingMachine.updateLayout();
	}

	@Override
	public List<AGLView> getRemoteRenderedViews() {

		ArrayList<AGLView> remoteRenderedViews = new ArrayList<AGLView>();
		remoteRenderedViews.add(dvi);
		remoteRenderedViews.add(vendingMachine);
		remoteRenderedViews.add(stratomex);
		return remoteRenderedViews;
	}

	public void handleOpenVendingMachineEvent(IDataDomain dataDomain) {

		ATableBasedDataDomain tableBasedDataDomain = (ATableBasedDataDomain) dataDomain;

		// For the vending machine it does not matter which record perspective
		// we take
		TablePerspective tablePerspective = tableBasedDataDomain.getTablePerspective(
				tableBasedDataDomain.getTable().getDefaultRecordPerspective()
						.getPerspectiveID(), tableBasedDataDomain.getTable()
						.getDefaultDimensionPerspective().getPerspectiveID());

		List<TablePerspective> tablePerspectiveWrapper = new ArrayList<TablePerspective>();
		tablePerspectiveWrapper.add(tablePerspective);

		vendingMachine.setTablePerspective(tablePerspective);
	}

	public void addDimensionGroups(List<TablePerspective> tablePerspectives,
			IBrickConfigurer dataConfigurer) {

		stratomex.addTablePerspectives(tablePerspectives, dataConfigurer);
		layoutManager.updateLayout();
	}

	/**
	 * @return the visBricks, see {@link #stratomex}
	 */
	public GLStratomex getVisBricks() {
		return stratomex;
	}

	@Override
	protected void destroyViewSpecificContent(GL2 gl) {
		layoutManager.destroy(gl);
	}
	
	@Override
	public boolean isDataView() {
		return true;
	}


	@Override
	public void addTablePerspective(TablePerspective newTablePerspective) {
		stratomex.addTablePerspective(newTablePerspective);
		layoutManager.updateLayout();
	}

	@Override
	public void addTablePerspectives(List<TablePerspective> newTablePerspectives) {
		stratomex.addTablePerspectives(newTablePerspectives);
		layoutManager.updateLayout();
	}

	@Override
	public List<TablePerspective> getTablePerspectives() {

		return new ArrayList<TablePerspective>();
	}

	@Override
	public void removeTablePerspective(int tablePerspectiveID) {
		stratomex.removeTablePerspective(tablePerspectiveID);
	}


	public void addTablePerspectives(List<TablePerspective> tablePerspectives, IBrickConfigurer dataConfigurer) {
		stratomex.addTablePerspectives(tablePerspectives);
		layoutManager.updateLayout();
	}
}
