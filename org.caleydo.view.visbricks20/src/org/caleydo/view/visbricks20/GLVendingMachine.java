package org.caleydo.view.visbricks20;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.awt.GLCanvas;
import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.RecordPerspective;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.IDataContainerBasedView;
import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.DetailLevel;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutManager;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.ColorRenderer;
import org.caleydo.core.view.opengl.layout.util.ViewLayoutRenderer;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.view.datagraph.event.OpenVendingMachineEvent;
import org.caleydo.view.visbricks.GLVisBricks;
import org.caleydo.view.visbricks.dimensiongroup.DimensionGroup;
import org.caleydo.view.visbricks.dimensiongroup.DimensionGroupManager;
import org.caleydo.view.visbricks.event.AddGroupsToVisBricksEvent;
import org.caleydo.view.visbricks20.listener.GLVendingMachineKeyListener;
import org.caleydo.view.visbricks20.listener.OpenVendingMachineListener;
import org.caleydo.view.visbricks20.renderstyle.VisBricks20RenderStyle;
import org.eclipse.swt.widgets.Composite;

/**
 * <p>
 * A view that renders a stacked list of VisBricks views.
 * </p>
 * 
 * @author Marc Streit
 */

public class GLVendingMachine
	extends AGLView
	implements IGLRemoteRenderingView, IDataContainerBasedView {

	public final static String VIEW_TYPE = "org.caleydo.view.vendingmachine";

	private VisBricks20RenderStyle renderStyle;

	private LayoutManager layoutManager;

	private Column mainColumn;

	private ArrayList<GLVisBricks> visBricksStack = new ArrayList<GLVisBricks>();

	private List<DataContainer> dataContainers = new ArrayList<DataContainer>();

	private DimensionGroupManager dimGroupManager;

	private Queue<GLVisBricks> uninitializedVisBrickViews = new LinkedList<GLVisBricks>();

	private GLVisBricks selectedVisBricksChoice;

	private OpenVendingMachineListener openVendingMachineListener;

	/**
	 * Hash that maps a GLVisBrick to the data container that is shown in
	 * addition to the "fixed" data containers.
	 */
	private HashMap<GLVisBricks, DataContainer> hashVisBricks2DataContainerChoice = new HashMap<GLVisBricks, DataContainer>();

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param viewLabel
	 * @param viewFrustum
	 */
	public GLVendingMachine(GLCanvas glCanvas, Composite parentComposite,
			ViewFrustum viewFrustum) {

		super(glCanvas, parentComposite, viewFrustum);

		viewType = GLVendingMachine.VIEW_TYPE;
		viewLabel = "Vending machine";

		glKeyListener = new GLVendingMachineKeyListener(this);

		parentGLCanvas.removeMouseWheelListener(glMouseListener);
		parentGLCanvas.addMouseWheelListener(glMouseWheelListener);
	}

	@Override
	public void init(GL2 gl) {
		displayListIndex = gl.glGenLists(1);
		renderStyle = new VisBricks20RenderStyle(viewFrustum);

		super.renderStyle = renderStyle;
		detailLevel = DetailLevel.HIGH;

		initLayouts();
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

		// TODO: iterate over visbricks views
		// visBricks.processEvents();

		pickingManager.handlePicking(this, gl);

		display(gl);
		if (busyState != EBusyState.OFF) {
			renderBusyMode(gl);
		}

		checkForHits(gl);
	}

	public void initLayouts() {

		layoutManager = new LayoutManager(viewFrustum, pixelGLConverter);

		mainColumn = new Column("baseElementLayout");
		mainColumn.setDebug(false);
		mainColumn.setBottomUp(false);
		layoutManager.setBaseElementLayout(mainColumn);

		if (dataContainers != null || dataContainers.size() == 0)
			createRankedVisBricksViews();

		layoutManager.updateLayout();
	}

	private void createRankedVisBricksViews() {

		// TODO: trigger ranking

		List<DataContainer> fixedDataContainers = new ArrayList<DataContainer>();

		for (DimensionGroup dimGroup : dimGroupManager.getDimensionGroups()) {

			// Only add fixed data container if it is not contained in the
			// options itself
			if (!dataContainers.contains(dimGroup.getDataContainer()))
				fixedDataContainers.add(dimGroup.getDataContainer());
		}

		hashVisBricks2DataContainerChoice.clear();

		for (DataContainer dataContainer : dataContainers) {

			Row visBricksElementLayout = new Row("visBricksElementLayoutRow");
			visBricksElementLayout.setDebug(false);
			visBricksElementLayout.setGrabY(true);

			GLVisBricks visBricks = createVisBricks(visBricksElementLayout);
			visBricks.setDataContainer(dataContainer);

			visBricksStack.add(visBricks);
			hashVisBricks2DataContainerChoice.put(visBricks, dataContainer);

			if (visBricksStack.size() == 1) {
				// by default the first choice is selected
				selectedVisBricksChoice = visBricksStack.get(0);

				visBricksElementLayout.addBackgroundRenderer(new ColorRenderer(new float[] {
						1, 1, 0, 1 }));
			}
			
			if (fixedDataContainers != null && fixedDataContainers.size() > 0)
				visBricks.addDimensionGroups(fixedDataContainers, null);

			uninitializedVisBrickViews.add(visBricks);
			mainColumn.append(visBricksElementLayout);
		}
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
		GLVisBricks visBricks = (GLVisBricks) GeneralManager.get().getViewManager()
				.createGLView(GLVisBricks.class, parentGLCanvas, parentComposite, frustum);

		visBricks.setVendingMachineMode(true);
		visBricks.setRemoteRenderingGLView(this);
		visBricks.initialize();
		visBricks.setDetailLevel(DetailLevel.LOW);

		ViewLayoutRenderer visBricksRenderer = new ViewLayoutRenderer(visBricks);
		wrappingLayout.setRenderer(visBricksRenderer);

		return visBricks;
	}

	/**
	 * Set an external dimension group manager which is then the basis for
	 * creating the ranked VisBricks options.
	 * 
	 * @param dimGroupManager
	 */
	public void setDimensionGroupManager(DimensionGroupManager dimGroupManager) {
		this.dimGroupManager = dimGroupManager;
	}

	@Override
	public void displayRemote(GL2 gl) {
		display(gl);
	}

	@Override
	public void display(GL2 gl) {

		if (!uninitializedVisBrickViews.isEmpty()) {
			while (uninitializedVisBrickViews.peek() != null) {
				uninitializedVisBrickViews.poll().initRemote(gl, this, glMouseListener);
			}
			// initLayouts();
		}

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

		openVendingMachineListener = new OpenVendingMachineListener();
		openVendingMachineListener.setHandler(this);
		eventPublisher.addListener(OpenVendingMachineEvent.class, openVendingMachineListener);
	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();

		if (openVendingMachineListener != null) {
			eventPublisher.removeListener(openVendingMachineListener);
			openVendingMachineListener = null;
		}
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		super.reshape(drawable, x, y, width, height);
		// initLayouts();

		// visBricks.updateLayout();
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

	@Override
	public void setDataContainer(DataContainer dataContainer) {
		// dataContainers.add(dataContainer);

		dataContainers.clear();

		ATableBasedDataDomain dataDomain = dataContainer.getDataDomain();
		Set<String> rowIDs = dataDomain.getRecordPerspectiveIDs();

		int count = 0;
		for (String id : rowIDs) {
			count++;

			if (count > 2)
				break;

			RecordPerspective perspective = dataDomain.getTable().getRecordPerspective(id);
			if (perspective.isPrivate()) {
				continue;
			}

			DataContainer newDataContainer = dataDomain.getDataContainer(id, dataContainer
					.getDimensionPerspective().getID());
			newDataContainer.setPrivate(true);

			dataContainers.add(newDataContainer);
		}

		initLayouts();
	}

	@Override
	public List<DataContainer> getDataContainers() {
		return dataContainers;
	}

	public void highlightNextPreviousVisBrick(boolean next) {

		// viewLayout.clearBackgroundRenderers();
		GLVisBricks previouslySelectedVisBricksChoice = selectedVisBricksChoice;
		hashVisBricks2DataContainerChoice.get(selectedVisBricksChoice).setPrivate(true);

		int selectedIndex = visBricksStack.indexOf(selectedVisBricksChoice);
		if (next && selectedIndex < visBricksStack.size() + 1)
			selectedIndex++;
		else if (!next && selectedIndex > 0)
			selectedIndex--;

		selectedVisBricksChoice = visBricksStack.get(selectedIndex);
		hashVisBricks2DataContainerChoice.get(selectedVisBricksChoice).setPrivate(false);

		for (ElementLayout viewLayout : mainColumn.getElements()) {

			if (((ViewLayoutRenderer) viewLayout.getRenderer()).getView() == selectedVisBricksChoice)
				viewLayout
						.addBackgroundRenderer(new ColorRenderer(new float[] { 1, 1, 0, 1 }));
			else if (((ViewLayoutRenderer) viewLayout.getRenderer()).getView() == previouslySelectedVisBricksChoice)
				viewLayout.clearBackgroundRenderers();
		}

		layoutManager.updateLayout();
	}

	public void selectVisBricksChoice() {

		DataContainer selectedDataContainer = hashVisBricks2DataContainerChoice
				.get(selectedVisBricksChoice);
		selectedDataContainer.setPrivate(false);

		AddGroupsToVisBricksEvent event = new AddGroupsToVisBricksEvent(selectedDataContainer);
		event.setReceiver((AGLView) getRemoteRenderingGLView());
		event.setSender(this);
		GeneralManager.get().getEventPublisher().triggerEvent(event);

		dataContainers.clear();
		selectedVisBricksChoice = null;
		visBricksStack.clear();
		initLayouts();
	}

	public void handleOpenVendingMachineEvent(IDataDomain dataDomain) {
		// TODO choose first ranked

		ATableBasedDataDomain tableBasedDataDomain = (ATableBasedDataDomain) dataDomain;

		// tableBasedDataDomain.getDataContainer(tableBasedDataDomain.get.gettableBasedDataDomain.getRecordPerspectiveIDs()
		// .toArray()[0],
		// tableBasedDataDomain.getDimensionPerspectiveIDs().toArray()[0]);

		// For the vending machine it does not matter which record perspective
		// we take

		DataContainer dataContainer = tableBasedDataDomain.getDataContainer(
				tableBasedDataDomain.getTable().getDefaultRecordPerspective().getID(),
				tableBasedDataDomain.getTable().getDefaultDimensionPerspective().getID());

		setDataContainer(dataContainer);
	}
	
	public GLVisBricks20 getVisBricks20View() {
		return ((GLVisBricks20)getRemoteRenderingGLView());
	}
}
