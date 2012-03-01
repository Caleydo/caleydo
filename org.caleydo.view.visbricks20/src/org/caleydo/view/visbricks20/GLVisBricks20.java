package org.caleydo.view.visbricks20;

import java.util.List;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.awt.GLCanvas;
import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.DetailLevel;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutManager;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.ViewLayoutRenderer;
import org.caleydo.core.view.opengl.layout.util.Zoomer;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.view.datagraph.GLDataGraph;
import org.caleydo.view.visbricks.GLVisBricks;
import org.caleydo.view.visbricks.brick.configurer.IBrickConfigurer;
import org.caleydo.view.visbricks.event.AddGroupsToVisBricksEvent;
import org.caleydo.view.visbricks20.listener.AddGroupsToVisBricksListener;
import org.caleydo.view.visbricks20.renderstyle.VisBricks20RenderStyle;
import org.eclipse.swt.widgets.Composite;

/**
 * <p>
 * VisBricks 2.0 view.
 * </p>
 * 
 * @author Marc Streit
 */

public class GLVisBricks20
	extends AGLView
	implements IGLRemoteRenderingView {

	public final static String VIEW_TYPE = "org.caleydo.view.visbricks20";

	private VisBricks20RenderStyle renderStyle;

	private LayoutManager layoutManager;

	private GLDataGraph dvi;

	private GLVisBricks visBricks;
	
	private GLVendingMachine vendingMachine;

	private AddGroupsToVisBricksListener addGroupsToVisBricksListener;

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param viewLabel
	 * @param viewFrustum
	 */
	public GLVisBricks20(GLCanvas glCanvas, Composite parentComposite, ViewFrustum viewFrustum) {

		super(glCanvas, parentComposite, viewFrustum);

		viewType = GLVisBricks20.VIEW_TYPE;
		viewLabel = "VisBricks 2.0";
	}

	@Override
	public void init(GL2 gl) {
		displayListIndex = gl.glGenLists(1);
		renderStyle = new VisBricks20RenderStyle(viewFrustum);

		super.renderStyle = renderStyle;
		detailLevel = DetailLevel.HIGH;

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

		Column mainColumn = new Column("baseElementLayout");
		mainColumn.setDebug(false);
		mainColumn.setBottomUp(false);
		layoutManager.setBaseElementLayout(mainColumn);

		Row dviElementLayout = new Row("dviElementLayoutRow");
		dviElementLayout.setDebug(false);
		createDVI(dviElementLayout);

		Row visBricksElementLayout = new Row("visBricksElementLayoutRow");
		visBricksElementLayout.setDebug(false);
		createVisBricks(visBricksElementLayout);

		// Just for testing vending machine
		Row vendingMachineElementLayout = new Row("wendingMachineElementLayoutRow");
		vendingMachineElementLayout.setDebug(false);
		createWendingMachine(vendingMachineElementLayout);
		
		mainColumn.append(dviElementLayout);
		mainColumn.append(visBricksElementLayout);
		mainColumn.append(vendingMachineElementLayout);

		layoutManager.updateLayout();
	}

	/**
	 * Creates DVI view
	 * 
	 * @param wrappingLayout
	 * @return
	 */
	private GLDataGraph createDVI(ElementLayout wrappingLayout) {
		ViewFrustum frustum = new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC, 0, 1, 0, 1,
				-4, 4);
		dvi = (GLDataGraph) GeneralManager.get().getViewManager()
				.createGLView(GLDataGraph.class, parentGLCanvas, parentComposite, frustum);

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
	private GLVisBricks createVisBricks(ElementLayout wrappingLayout) {
		ViewFrustum frustum = new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC, 0, 1, 0, 1,
				-4, 4);
		visBricks = (GLVisBricks) GeneralManager.get().getViewManager()
				.createGLView(GLVisBricks.class, parentGLCanvas, parentComposite, frustum);

		visBricks.setRemoteRenderingGLView(this);
		visBricks.initialize();

		ViewLayoutRenderer visBricksRenderer = new ViewLayoutRenderer(visBricks);
		wrappingLayout.setRenderer(visBricksRenderer);

		return visBricks;
	}

	/**
	 * Creates wending machine view
	 * 
	 * @param wrappingLayout
	 * @return
	 */
	private GLVendingMachine createWendingMachine(ElementLayout wrappingLayout) {
		ViewFrustum frustum = new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC, 0, 1, 0, 1,
				-4, 4);
		vendingMachine = (GLVendingMachine) GeneralManager.get().getViewManager()
				.createGLView(GLVendingMachine.class, parentGLCanvas, parentComposite, frustum);

		vendingMachine.setRemoteRenderingGLView(this);
		vendingMachine.initialize();
		vendingMachine.setDimensionGroupManager(visBricks.getDimensionGroupManager());

		ViewLayoutRenderer wendingMachineRenderer = new ViewLayoutRenderer(vendingMachine);
		wrappingLayout.setRenderer(wendingMachineRenderer);
		wrappingLayout.setPixelSizeY(500);
	
		Zoomer zoomer = new Zoomer(vendingMachine, wrappingLayout);
		wrappingLayout.setZoomer(zoomer);

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

		addGroupsToVisBricksListener = new AddGroupsToVisBricksListener();
		addGroupsToVisBricksListener.setHandler(this);
		eventPublisher.addListener(AddGroupsToVisBricksEvent.class,
				addGroupsToVisBricksListener);
	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();

		if (addGroupsToVisBricksListener != null) {
			eventPublisher.removeListener(addGroupsToVisBricksListener);
			addGroupsToVisBricksListener = null;
		}
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		super.reshape(drawable, x, y, width, height);
		// initLayouts();

		layoutManager.updateLayout();
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

	public void addDimensionGroups(List<DataContainer> dataContainers,
			IBrickConfigurer dataConfigurer) {
		visBricks.addDimensionGroups(dataContainers, dataConfigurer);
	
		// TODO choose first ranked
		vendingMachine.setDataContainer(dataContainers.get(0));
	}
}
