package org.caleydo.view.visbricks20;

import java.util.ArrayList;
import java.util.Collections;
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
import org.caleydo.core.data.perspective.RecordPerspective;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutManager;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.ColorRenderer;
import org.caleydo.core.view.opengl.layout.util.ViewLayoutRenderer;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.view.visbricks.GLVisBricks;
import org.caleydo.view.visbricks.dimensiongroup.DimensionGroup;
import org.caleydo.view.visbricks.dimensiongroup.DimensionGroupManager;
import org.caleydo.view.visbricks20.listener.GLVendingMachineKeyListener;
import org.caleydo.view.visbricks20.renderer.RankNumberRenderer;
import org.caleydo.view.visbricks20.renderstyle.VisBricks20RenderStyle;
import org.eclipse.swt.widgets.Composite;

/**
 * <p>
 * A view that renders a stacked list of VisBricks views.
 * </p>
 * 
 * @author Marc Streit
 */

public class GLVendingMachine extends AGLView implements IGLRemoteRenderingView {

	public final static String VIEW_TYPE = "org.caleydo.view.vendingmachine";

	private VisBricks20RenderStyle renderStyle;

	private LayoutManager layoutManager;

	private Column mainColumn;

	private ArrayList<GLVisBricks> visBricksStack = new ArrayList<GLVisBricks>();

	private List<DataContainer> dataContainers = new ArrayList<DataContainer>();

	private DimensionGroupManager dimGroupManager;

	private Queue<GLVisBricks> uninitializedVisBrickViews = new LinkedList<GLVisBricks>();

	private GLVisBricks selectedVisBricksChoice;

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
		detailLevel = EDetailLevel.HIGH;

		textRenderer = new CaleydoTextRenderer(24);

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
		mainColumn.setBottomUp(false);
		layoutManager.setBaseElementLayout(mainColumn);

		if (dataContainers != null || dataContainers.size() == 0)
			createRankedVisBricksViews();

		layoutManager.updateLayout();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void createRankedVisBricksViews() {

		List<DataContainer> fixedDataContainers = new ArrayList<DataContainer>();

		for (DimensionGroup dimGroup : dimGroupManager.getDimensionGroups()) {

			// Only add fixed data container if it is not contained in the
			// options itself
			if (!dataContainers.contains(dimGroup.getDataContainer()))
				fixedDataContainers.add(dimGroup.getDataContainer());
		}

		hashVisBricks2DataContainerChoice.clear();

		// Trigger ranking of data containers
		List<Pair<Float, DataContainer>> score2DataContainerList = new ArrayList<Pair<Float, DataContainer>>();
		for (DataContainer referenceDataContainer : dataContainers) {
			float scoreSum = 0;
			int scoreCount = 0;
			for (DataContainer fixedDataContainer : fixedDataContainers) {

				scoreSum += referenceDataContainer.getContainerStatistics()
						.adjustedRandIndex().getScore(fixedDataContainer, false);
				scoreCount++;
			}
			score2DataContainerList.add(new Pair(scoreSum / scoreCount,
					referenceDataContainer));
		}

		Collections.sort(score2DataContainerList);

		int rank = score2DataContainerList.size();
		for (Pair<Float, DataContainer> score2DataContainer : score2DataContainerList) {

			DataContainer dataContainer = score2DataContainer.getSecond();

			Row vendingMachineElementLayout = new Row("vendingMachineElementLayout");
			vendingMachineElementLayout.setGrabY(true);

			ElementLayout rankElementLayout = new ElementLayout("rankElementLayout");
			rankElementLayout.setPixelSizeX(70);
			RankNumberRenderer rankNumberRenderer = new RankNumberRenderer("" + rank--, // score2DataContainer.getFirst(),
					getTextRenderer());
			rankElementLayout.setRenderer(rankNumberRenderer);

			ElementLayout visBricksElementLayout = new ElementLayout(
					"visBricksElementLayoutRow");

			GLVisBricks visBricks = createVisBricks(visBricksElementLayout);
			visBricks.addDataContainer(dataContainer);

			visBricksStack.add(0, visBricks);
			hashVisBricks2DataContainerChoice.put(visBricks, dataContainer);

			if (visBricksStack.size() == score2DataContainerList.size()) {
				// by default the last vending machine is selected
				selectedVisBricksChoice = visBricksStack.get(0);

				vendingMachineElementLayout.addBackgroundRenderer(new ColorRenderer(
						new float[] { 1, 1, 0, 1 }));
			}

			if (fixedDataContainers != null && fixedDataContainers.size() > 0)
				visBricks.addDataContainers(fixedDataContainers, null);

			uninitializedVisBrickViews.add(visBricks);
			vendingMachineElementLayout.append(rankElementLayout);
			vendingMachineElementLayout.append(visBricksElementLayout);
			mainColumn.add(0, vendingMachineElementLayout);
		}
	}

	/**
	 * Creates VisBricks view
	 * 
	 * @param wrappingLayout
	 * @return
	 */
	private GLVisBricks createVisBricks(ElementLayout wrappingLayout) {
		ViewFrustum frustum = new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC, 0, 1, 0,
				1, -4, 4);
		GLVisBricks visBricks = (GLVisBricks) GeneralManager
				.get()
				.getViewManager()
				.createGLView(GLVisBricks.class, parentGLCanvas, parentComposite, frustum);

		visBricks.setVendingMachineMode(true);
		visBricks.setRemoteRenderingGLView(this);
		visBricks.initialize();
		visBricks.setDetailLevel(EDetailLevel.LOW);

		ViewLayoutRenderer visBricksRenderer = new ViewLayoutRenderer(visBricks);
		wrappingLayout.setRenderer(visBricksRenderer);
		// wrappingLayout.setDebug(true);

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
	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();
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

			RecordPerspective perspective = dataDomain.getTable()
					.getRecordPerspective(id);
			if (perspective.isPrivate()) {
				continue;
			}

			DataContainer newDataContainer = dataDomain.getDataContainer(id,
					dataContainer.getDimensionPerspective().getID());
			newDataContainer.setPrivate(true);

			dataContainers.add(newDataContainer);
		}

		initLayouts();
	}

	public List<DataContainer> getDataContainers() {
		return dataContainers;
	}

	public void highlightNextPreviousVisBrick(boolean next) {

		GLVisBricks previouslySelectedVisBricksChoice = selectedVisBricksChoice;
		DataContainer previouslySelectedDatacontainer = hashVisBricks2DataContainerChoice
				.get(selectedVisBricksChoice);
		previouslySelectedDatacontainer.setPrivate(true);

		int selectedIndex = visBricksStack.indexOf(selectedVisBricksChoice);
		if (next && selectedIndex < (visBricksStack.size() - 1))
			selectedIndex++;
		else if (!next && selectedIndex > 0)
			selectedIndex--;

		selectedVisBricksChoice = visBricksStack.get(selectedIndex);
		DataContainer selectedDataContainer = hashVisBricks2DataContainerChoice
				.get(selectedVisBricksChoice);
		selectedDataContainer.setPrivate(false);

		for (ElementLayout vendingMachineElementLayout : mainColumn.getElements()) {

			ElementLayout viewLayout = ((Row) vendingMachineElementLayout).getElements()
					.get(1);
			if (((ViewLayoutRenderer) viewLayout.getRenderer()).getView() == selectedVisBricksChoice)
				vendingMachineElementLayout.addBackgroundRenderer(new ColorRenderer(
						new float[] { 1, 1, 0, 1 }));
			else if (((ViewLayoutRenderer) viewLayout.getRenderer()).getView() == previouslySelectedVisBricksChoice)
				vendingMachineElementLayout.clearBackgroundRenderers();
		}

		// Switch currently shown dim group data container in main VisBricks
		// view
		for (DimensionGroup dimGroup : dimGroupManager.getDimensionGroups()) {

			if (dimGroup.isDetailBrickShown()) {
				dimGroup.setDataContainer(selectedDataContainer);
				// FIXME: update in dim group does not wor
				// dimGroup.initLayouts();
				// dimGroup.updateLayout();
				break;
			}
		}
		dimGroupManager.setCenterGroupStartIndex(0);
		dimGroupManager.setRightGroupStartIndex(dimGroupManager.getDimensionGroups()
				.size());

		getVisBricks20View().getVisBricks().updateLayout();

		layoutManager.updateLayout();
	}

	public void selectVisBricksChoice() {

		DataContainer selectedDataContainer = hashVisBricks2DataContainerChoice
				.get(selectedVisBricksChoice);
		selectedDataContainer.setPrivate(false);

		dataContainers.clear();
		selectedVisBricksChoice = null;
		visBricksStack.clear();
		initLayouts();
	}

	public GLVisBricks20 getVisBricks20View() {
		return ((GLVisBricks20) getRemoteRenderingGLView());
	}
}
