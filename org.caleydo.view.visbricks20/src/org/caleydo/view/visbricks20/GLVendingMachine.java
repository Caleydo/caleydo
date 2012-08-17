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
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.RecordPerspective;
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
import org.caleydo.view.stratomex.GLStratomex;
import org.caleydo.view.stratomex.column.BrickColumn;
import org.caleydo.view.stratomex.column.BrickColumnManager;
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

public class GLVendingMachine
	extends AGLView
	implements IGLRemoteRenderingView {

	public static String VIEW_TYPE = "org.caleydo.view.vendingmachine";

	public static String VIEW_NAME = "Vending Machine";

	private VisBricks20RenderStyle renderStyle;

	private LayoutManager layoutManager;

	private Column mainColumn;

	private ArrayList<GLStratomex> visBricksStack = new ArrayList<GLStratomex>();

	private List<TablePerspective> tablePerspectives = new ArrayList<TablePerspective>();

	private BrickColumnManager dimGroupManager;

	private Queue<GLStratomex> uninitializedVisBrickViews = new LinkedList<GLStratomex>();

	private GLStratomex selectedVisBricksChoice;

	/**
	 * Hash that maps a GLVisBrick to the data container that is shown in
	 * addition to the "fixed" data containers.
	 */
	private HashMap<GLStratomex, TablePerspective> hashVisBricks2TablePerspectiveChoice = new HashMap<GLStratomex, TablePerspective>();

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param viewLabel
	 * @param viewFrustum
	 */
	public GLVendingMachine(GLCanvas glCanvas, Composite parentComposite,
			ViewFrustum viewFrustum) {

		super(glCanvas, parentComposite, viewFrustum, VIEW_TYPE, VIEW_NAME);

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

		if (tablePerspectives != null || tablePerspectives.size() == 0)
			createRankedVisBricksViews();

		layoutManager.updateLayout();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void createRankedVisBricksViews() {

		List<TablePerspective> fixedTablePerspectives = new ArrayList<TablePerspective>();

		for (BrickColumn dimGroup : dimGroupManager.getBrickColumns()) {

			// Only add fixed data container if it is not contained in the
			// options itself
			if (!tablePerspectives.contains(dimGroup.getTablePerspective()))
				fixedTablePerspectives.add(dimGroup.getTablePerspective());
		}

		hashVisBricks2TablePerspectiveChoice.clear();

		// Trigger ranking of data containers
		List<Pair<Float, TablePerspective>> score2TablePerspectiveList = new ArrayList<Pair<Float, TablePerspective>>();
		for (TablePerspective referenceTablePerspective : tablePerspectives) {
			float scoreSum = 0;
			int scoreCount = 0;
			for (TablePerspective fixedTablePerspective : fixedTablePerspectives) {

				scoreSum += referenceTablePerspective.getContainerStatistics()
						.adjustedRandIndex().getScore(fixedTablePerspective, false);
				scoreCount++;
			}
			score2TablePerspectiveList
					.add(new Pair(scoreSum / scoreCount, referenceTablePerspective));
		}

		Collections.sort(score2TablePerspectiveList);

		int rank = score2TablePerspectiveList.size();
		for (Pair<Float, TablePerspective> score2TablePerspective : score2TablePerspectiveList) {

			TablePerspective tablePerspective = score2TablePerspective.getSecond();

			Row vendingMachineElementLayout = new Row("vendingMachineElementLayout");
			vendingMachineElementLayout.setGrabY(true);

			ElementLayout rankElementLayout = new ElementLayout("rankElementLayout");
			rankElementLayout.setPixelSizeX(70);
			RankNumberRenderer rankNumberRenderer = new RankNumberRenderer("" + rank--, // score2TablePerspective.getFirst(),
					getTextRenderer());
			rankElementLayout.setRenderer(rankNumberRenderer);

			ElementLayout visBricksElementLayout = new ElementLayout(
					"visBricksElementLayoutRow");

			GLStratomex visBricks = createVisBricks(visBricksElementLayout);
			visBricks.addTablePerspective(tablePerspective);

			visBricksStack.add(0, visBricks);
			hashVisBricks2TablePerspectiveChoice.put(visBricks, tablePerspective);

			if (visBricksStack.size() == score2TablePerspectiveList.size()) {
				// by default the last vending machine is selected
				selectedVisBricksChoice = visBricksStack.get(0);

				vendingMachineElementLayout.addBackgroundRenderer(new ColorRenderer(
						new float[] { 1, 1, 0, 1 }));
			}

			if (fixedTablePerspectives != null && fixedTablePerspectives.size() > 0)
				visBricks.addTablePerspectives(fixedTablePerspectives, null);

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
	private GLStratomex createVisBricks(ElementLayout wrappingLayout) {
		ViewFrustum frustum = new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC, 0, 1, 0, 1,
				-4, 4);
		GLStratomex visBricks = (GLStratomex) GeneralManager.get().getViewManager()
				.createGLView(GLStratomex.class, parentGLCanvas, parentComposite, frustum);

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
	public void setDimensionGroupManager(BrickColumnManager dimGroupManager) {
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

	public void setTablePerspective(TablePerspective tablePerspective) {
		// tablePerspectives.add(tablePerspective);

		tablePerspectives.clear();

		ATableBasedDataDomain dataDomain = tablePerspective.getDataDomain();
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

			TablePerspective newTablePerspective = dataDomain.getTablePerspective(id, tablePerspective
					.getDimensionPerspective().getPerspectiveID());
			newTablePerspective.setPrivate(true);

			tablePerspectives.add(newTablePerspective);
		}

		initLayouts();
	}

	public List<TablePerspective> getTablePerspectives() {
		return tablePerspectives;
	}

	public void highlightNextPreviousVisBrick(boolean next) {

		GLStratomex previouslySelectedVisBricksChoice = selectedVisBricksChoice;
		TablePerspective previouslySelectedTablePerspective = hashVisBricks2TablePerspectiveChoice
				.get(selectedVisBricksChoice);
		previouslySelectedTablePerspective.setPrivate(true);

		int selectedIndex = visBricksStack.indexOf(selectedVisBricksChoice);
		if (next && selectedIndex < (visBricksStack.size() - 1))
			selectedIndex++;
		else if (!next && selectedIndex > 0)
			selectedIndex--;

		selectedVisBricksChoice = visBricksStack.get(selectedIndex);
		TablePerspective selectedTablePerspective = hashVisBricks2TablePerspectiveChoice
				.get(selectedVisBricksChoice);
		selectedTablePerspective.setPrivate(false);

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
		for (BrickColumn dimGroup : dimGroupManager.getBrickColumns()) {

			if (dimGroup.isDetailBrickShown()) {
				dimGroup.setTablePerspective(selectedTablePerspective);
				// FIXME: update in dim group does not wor
				// dimGroup.initLayouts();
				// dimGroup.updateLayout();
				break;
			}
		}
		dimGroupManager.setCenterGroupStartIndex(0);
		dimGroupManager.setRightGroupStartIndex(dimGroupManager.getBrickColumns().size());

		getVisBricks20View().getVisBricks().updateLayout();

		layoutManager.updateLayout();
	}

	public void selectVisBricksChoice() {

		TablePerspective selectedTablePerspective = hashVisBricks2TablePerspectiveChoice
				.get(selectedVisBricksChoice);
		selectedTablePerspective.setPrivate(false);

		tablePerspectives.clear();
		selectedVisBricksChoice = null;
		visBricksStack.clear();
		initLayouts();
	}

	public GLVisBricks20 getVisBricks20View() {
		return ((GLVisBricks20) getRemoteRenderingGLView());
	}

	@Override
	protected void destroyViewSpecificContent(GL2 gl) {
		layoutManager.destroy(gl);
	}
}
