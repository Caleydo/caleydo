package org.caleydo.view.stratomex.vendingmachine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.awt.GLCanvas;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.RecordPerspective;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.ILayoutedElement;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.ColorRenderer;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.view.stratomex.GLStratomex;
import org.caleydo.view.stratomex.column.BrickColumn;
import org.caleydo.view.stratomex.column.BrickColumnGlowRenderer;
import org.eclipse.swt.widgets.Composite;

/**
 * <p>
 * A view that renders a stacked list of VisBricks views.
 * </p>
 * 
 * @author Marc Streit
 */

public class VendingMachine
	extends AGLView
	implements IGLRemoteRenderingView, ILayoutedElement {

	public static String VIEW_TYPE = "org.caleydo.view.vendingmachine";

	public static String VIEW_NAME = "Vending Machine";

	private Column mainRankColumn;

	private TablePerspective referenceTablePerspective;

	private List<TablePerspective> tablePerspectives = new ArrayList<TablePerspective>();

	private HashMap<TablePerspective, ElementLayout> tablePerspectiveToRankElementLayout = new HashMap<TablePerspective, ElementLayout>();

	private int selectedTablePerspectiveIndex;

	private ColorRenderer highlightRankBackgroundRenderer;

	private List<Pair<Float, TablePerspective>> scoreToTablePerspective;
	
	private GLStratomex stratomex;

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param viewLabel
	 * @param viewFrustum
	 */
	public VendingMachine(GLCanvas glCanvas, Composite parentComposite,
			ViewFrustum viewFrustum) {

		super(glCanvas, parentComposite, viewFrustum, VIEW_TYPE, VIEW_NAME);

		glKeyListener = new VendingMachineKeyListener(this);

		parentGLCanvas.removeMouseWheelListener(glMouseListener);
		parentGLCanvas.addMouseWheelListener(glMouseWheelListener);
	}

	@Override
	public void init(GL2 gl) {
		displayListIndex = gl.glGenLists(1);

		super.renderStyle = renderStyle;
		detailLevel = EDetailLevel.HIGH;

		textRenderer = new CaleydoTextRenderer(12);
		highlightRankBackgroundRenderer = new ColorRenderer(new float[] { 1, 1, 0, 1 });
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

		mainRankColumn = new Column("mainRankColum");
		mainRankColumn.setBottomUp(true);
		mainRankColumn.setGrabY(true);
		// mainRankColumn.setDebug(true);
		mainRankColumn.setFrameColor(1, 0, 0, 1);
		mainRankColumn.updateSubLayout();
	}

	/**
	 * Updates the layout of this dimensionGroup
	 */
	public void updateLayout() {

		mainRankColumn.updateSubLayout();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void createRankedStratomexViews() {

		mainRankColumn.clear();
		tablePerspectiveToRankElementLayout.clear();

		// Trigger ranking of data containers
		scoreToTablePerspective = new ArrayList<Pair<Float, TablePerspective>>();
		for (TablePerspective scoredTablePerspective : tablePerspectives) {

			float score = scoredTablePerspective.getContainerStatistics()
					.getAdjustedRandIndex().getScore(referenceTablePerspective, true);

			scoreToTablePerspective.add(new Pair(score, scoredTablePerspective));
		}

		Collections.sort(scoreToTablePerspective);
		Collections.reverse(scoreToTablePerspective);

		int rank = 0;

		for (Pair<Float, TablePerspective> score2TablePerspective : scoreToTablePerspective) {

			TablePerspective tablePerspective = score2TablePerspective.getSecond();

			Row rankElementLayout = new Row("rankElementLayout");
			rankElementLayout.setGrabX(true);
			rankElementLayout.setGrabY(true);
			RankNumberRenderer rankNumberRenderer = new RankNumberRenderer("[" + (++rank)
					+ ".] " + score2TablePerspective.getFirst() + " "
					+ tablePerspective.getLabel(), getTextRenderer());
			rankElementLayout.setRenderer(rankNumberRenderer);

			tablePerspectiveToRankElementLayout.put(tablePerspective, rankElementLayout);

			mainRankColumn.add(0, rankElementLayout);
		}

		selectedTablePerspectiveIndex = 0;

		mainRankColumn.getLayoutManager().updateLayout();
	}

	@Override
	public void displayRemote(GL2 gl) {
		display(gl);
	}

	@Override
	public void display(GL2 gl) {

	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		return null;
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
	}

	@Override
	public List<AGLView> getRemoteRenderedViews() {

		return null;
	}

	public void setTablePerspective(TablePerspective referenceTablePerspective) {

		ATableBasedDataDomain dataDomain = referenceTablePerspective.getDataDomain();
		//
		// // For the vending machine it does not matter which record
		// perspective
		// // we take
		// TablePerspective tablePerspective = dataDomain.getTablePerspective(
		// dataDomain.getTable().getDefaultRecordPerspective()
		// .getPerspectiveID(), dataDomain.getTable()
		// .getDefaultDimensionPerspective().getPerspectiveID());
		//
		// List<TablePerspective> tablePerspectiveWrapper = new
		// ArrayList<TablePerspective>();
		// tablePerspectiveWrapper.add(tablePerspective);

		tablePerspectives.clear();

		this.referenceTablePerspective = referenceTablePerspective;

		Set<String> rowIDs = dataDomain.getRecordPerspectiveIDs();

		int count = 0;
		for (String id : rowIDs) {
			count++;
			RecordPerspective perspective = dataDomain.getTable().getRecordPerspective(id);
			if (perspective.isPrivate()) {
				continue;
			}

			TablePerspective newTablePerspective = dataDomain.getTablePerspective(id,
					referenceTablePerspective.getDimensionPerspective().getPerspectiveID());
			newTablePerspective.setPrivate(true);

			// Do not add the current reference table perspectives to scoring
			if (referenceTablePerspective == newTablePerspective)
				continue;

			// Add first table perspective as the currently selected one to
			// stratomex
			if (count == 1) {
				stratomex.addTablePerspective(newTablePerspective);
				stratomex.updateLayout();
			}

			tablePerspectives.add(newTablePerspective);
		}

		if (tablePerspectives != null || tablePerspectives.size() == 0)
			createRankedStratomexViews();

		tablePerspectiveToRankElementLayout.get(scoreToTablePerspective.get(0).getSecond())
				.addBackgroundRenderer(highlightRankBackgroundRenderer);
		mainRankColumn.getLayoutManager().updateLayout();
	}

	public List<TablePerspective> getTablePerspectives() {
		return tablePerspectives;
	}

	public void highlightNextPreviousVisBrick(boolean next) {

		TablePerspective prevSelectedTablePerspective = scoreToTablePerspective.get(
				selectedTablePerspectiveIndex).getSecond();

		if (next && selectedTablePerspectiveIndex < (tablePerspectives.size() - 1))
			selectedTablePerspectiveIndex++;
		else if (!next && selectedTablePerspectiveIndex > 0)
			selectedTablePerspectiveIndex--;

		System.out.println(selectedTablePerspectiveIndex);

		TablePerspective newlySelectedTablePerspective = scoreToTablePerspective.get(
				selectedTablePerspectiveIndex).getSecond();

		if (prevSelectedTablePerspective != newlySelectedTablePerspective) {
			stratomex.getDimensionGroupManager().removeBrickColumn(
					prevSelectedTablePerspective.getID());

			tablePerspectiveToRankElementLayout.get(prevSelectedTablePerspective)
					.clearBackgroundRenderers();

			tablePerspectiveToRankElementLayout.get(newlySelectedTablePerspective)
					.addBackgroundRenderer(highlightRankBackgroundRenderer);

			stratomex.addTablePerspective(newlySelectedTablePerspective);

			BrickColumn brickColumn = stratomex.getDimensionGroupManager().getBrickColumns()
					.get(1);
			brickColumn.getLayout().addBackgroundRenderer(
					new BrickColumnGlowRenderer(new float[] { 1, 0, 9 }, brickColumn, false));
			brickColumn.getLayout().updateSubLayout();

			mainRankColumn.getLayoutManager().updateLayout();
		}
	}

	public void selectChoice() {

		tablePerspectives.get(selectedTablePerspectiveIndex).setPrivate(false);
		tablePerspectives.clear();
		mainRankColumn.setPixelSizeX(0);
	}

	@Override
	protected void destroyViewSpecificContent(GL2 gl) {
	}

	@Override
	public ElementLayout getLayout() {
		return mainRankColumn;
	}

	/**
	 * @param glStratomex
	 */
	public void setStratomex(GLStratomex glStratomex) {
		this.stratomex = stratomex;
	}
}
