/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 * 
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.stratomex.vendingmachine;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
import org.caleydo.view.stratomex.column.BrickColumnManager;
import org.caleydo.view.stratomex.event.ScoreColumnEvent;
import org.caleydo.view.stratomex.event.ScoreGroupEvent;
import org.caleydo.view.stratomex.listener.ScoreColumnListener;
import org.caleydo.view.stratomex.listener.ScoreGroupListener;
import org.caleydo.view.stratomex.listener.VendingMachineKeyListener;
import org.eclipse.swt.widgets.Composite;

/**
 * <p>
 * The vending machine for stratification and cluster comparisons using scoring
 * approach.
 * </p>
 * 
 * @author Marc Streit
 */

public class VendingMachine
	extends AGLView
	implements IGLRemoteRenderingView, ILayoutedElement {

	public static String VIEW_TYPE = "org.caleydo.view.vendingmachine";

	public static String VIEW_NAME = "Vending Machine";

	public static int VENDING_MACHINE_PIXEL_WIDTH = 320;

	private Column mainRankColumn;

	private TablePerspective referenceTablePerspective;

	private List<TablePerspective> tablePerspectives = new ArrayList<TablePerspective>();

	private HashMap<RankedElement, ElementLayout> rankedElementToElementLayout = new HashMap<RankedElement, ElementLayout>();

	private int selectedTablePerspectiveIndex = 0;

	private ColorRenderer highlightRankBackgroundRenderer;

	private List<RankedElement> rankedElements;

	private GLStratomex stratomex;

	private boolean isActive;

	private BrickColumnManager brickColumnManager;

	private BrickColumn referenceBrickColumn;

	private ScoreColumnListener scoreColumnListener;

	private ScoreGroupListener scoreGroupListener;

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param viewLabel
	 * @param viewFrustum
	 */
	public VendingMachine(GLCanvas glCanvas, Composite parentComposite, ViewFrustum viewFrustum) {

		super(glCanvas, parentComposite, viewFrustum, VIEW_TYPE, VIEW_NAME);

		glKeyListener = new VendingMachineKeyListener(this);

		parentGLCanvas.removeMouseWheelListener(glMouseListener);
		parentGLCanvas.addMouseWheelListener(glMouseWheelListener);

		initLayouts();
	}

	@Override
	public void init(GL2 gl) {
		displayListIndex = gl.glGenLists(1);

		super.renderStyle = renderStyle;
		detailLevel = EDetailLevel.HIGH;

		textRenderer = new CaleydoTextRenderer(10);
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
		mainRankColumn.setBottomUp(false);
		mainRankColumn.setPixelSizeX(VENDING_MACHINE_PIXEL_WIDTH);
		mainRankColumn.setFrameColor(1, 0, 0, 1);
	}

	/**
	 * Updates the layout of this dimensionGroup
	 */
	public void updateLayout() {

		mainRankColumn.updateSubLayout();
	}

	private void createRankedStratomexViews() {

		mainRankColumn.clear();
		rankedElementToElementLayout.clear();

		// Trigger ranking of data containers
		rankedElements = new ArrayList<RankedElement>();
		for (TablePerspective scoredTablePerspective : tablePerspectives) {

			// Check if we compare whole stratifications or only single groups
			if (referenceTablePerspective.getRecordSubTablePerspectives().size() > 1) {
				float score = scoredTablePerspective.getContainerStatistics()
						.getAdjustedRandIndex().getScore(referenceTablePerspective, true);

				RankedElement rankedElement = new RankedElement(score, scoredTablePerspective,
						null);
				rankedElements.add(rankedElement);
			}
			else {
				HashMap<TablePerspective, Float> subTablePerspectiveToScore = referenceTablePerspective
						.getContainerStatistics().getJaccardIndex()
						.getScore(scoredTablePerspective, true);

				for (TablePerspective subTablePerspective : subTablePerspectiveToScore
						.keySet()) {

					RankedElement rankedElement = new RankedElement(
							subTablePerspectiveToScore.get(subTablePerspective),
							scoredTablePerspective, subTablePerspective);
					rankedElements.add(rankedElement);
				}
			}
		}

		Collections.sort(rankedElements);
		// Collections.reverse(rankedElements);

		int rank = 0;

		for (RankedElement rankedElement : rankedElements) {

			BigDecimal bd = new BigDecimal(rankedElement.getScore()).setScale(2,
					RoundingMode.HALF_EVEN);
			float score = bd.floatValue();

			Row rankedElementLayout = new Row("rankElementLayout");
			rankedElementLayout.setGrabX(true);
			rankedElementLayout.setPixelSizeY(30);
			RankNumberRenderer rankNumberRenderer = new RankNumberRenderer("[" + (++rank)
					+ ".] " + score + " "
					+ rankedElement.getColumnTablePerspective().getLabel() + " - "
					+ rankedElement.getGroupTablePerspective().getLabel(), getTextRenderer());
			rankedElementLayout.setRenderer(rankNumberRenderer);

			rankedElementToElementLayout.put(rankedElement, rankedElementLayout);

			mainRankColumn.append(rankedElementLayout);
		}

		// Add first ranked table perspective as the currently selected one to
		// stratomex
		TablePerspective tablePerspective = rankedElements.get(selectedTablePerspectiveIndex)
				.getColumnTablePerspective();
		addTablePerspectiveToStratomex(tablePerspective);

		// Move newly added table perspective to be right of the reference table
		// perspective
		brickColumnManager.moveBrickColumn(
				brickColumnManager.getBrickColumn(tablePerspective), brickColumnManager
						.indexOfBrickColumn(brickColumnManager
								.getBrickColumn(referenceTablePerspective)) + 1);
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

		scoreColumnListener = new ScoreColumnListener();
		scoreColumnListener.setHandler(this);
		eventPublisher.addListener(ScoreColumnEvent.class, scoreColumnListener);

		scoreGroupListener = new ScoreGroupListener();
		scoreGroupListener.setHandler(this);
		eventPublisher.addListener(ScoreGroupEvent.class, scoreGroupListener);
	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();

		if (scoreColumnListener != null) {
			eventPublisher.removeListener(scoreColumnListener);
			scoreColumnListener = null;
		}

		if (scoreGroupListener != null) {
			eventPublisher.removeListener(scoreGroupListener);
			scoreGroupListener = null;
		}
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		super.reshape(drawable, x, y, width, height);
	}

	@Override
	public List<AGLView> getRemoteRenderedViews() {

		return null;
	}

	public void setColumnTablePerspective(TablePerspective referenceTablePerspective) {

		referenceBrickColumn = brickColumnManager.getBrickColumn(referenceTablePerspective);
		setTablePerspective(referenceTablePerspective);
	}

	public void setGroupTablePerspective(TablePerspective referenceColumnTablePerspective,
			TablePerspective referenceGroupTablePerspective) {

		referenceBrickColumn = brickColumnManager
				.getBrickColumn(referenceColumnTablePerspective);
		setTablePerspective(referenceGroupTablePerspective);
	}

	public void setTablePerspective(TablePerspective referenceTablePerspective) {

		tablePerspectives.clear();
		selectedTablePerspectiveIndex = 0;
		isActive = true;
		stratomex.getLayout().getLayoutManager().updateLayout();

		this.referenceTablePerspective = referenceTablePerspective;

		// Highlight reference table
		referenceBrickColumn.getLayout().addBackgroundRenderer(
				new BrickColumnGlowRenderer(new float[] { 1, 0, 0 }, referenceBrickColumn,
						false));

		ATableBasedDataDomain dataDomain = referenceTablePerspective.getDataDomain();

		Set<String> rowIDs = dataDomain.getRecordPerspectiveIDs();

		for (String id : rowIDs) {
			RecordPerspective perspective = dataDomain.getTable().getRecordPerspective(id);
			if (perspective.isPrivate()) {
				continue;
			}

			TablePerspective newTablePerspective = dataDomain.getTablePerspective(id,
					referenceTablePerspective.getDimensionPerspective().getPerspectiveID());
			newTablePerspective.setPrivate(true);

			// Do not add the current reference table perspectives to scoring
			if (referenceTablePerspective == newTablePerspective
					|| referenceBrickColumn.getTablePerspective() == newTablePerspective)
				continue;

			tablePerspectives.add(newTablePerspective);
		}

		if (tablePerspectives != null || tablePerspectives.size() == 0)
			createRankedStratomexViews();

		rankedElementToElementLayout.get(rankedElements.get(0)).addBackgroundRenderer(
				highlightRankBackgroundRenderer);

		stratomex.updateLayout();
		stratomex.setLayoutDirty();
	}

	public List<TablePerspective> getTablePerspectives() {
		return tablePerspectives;
	}

	public void highlightNextPreviousVisBrick(boolean next) {

		RankedElement prevSelectedRankedElement = rankedElements
				.get(selectedTablePerspectiveIndex);

		if (next && selectedTablePerspectiveIndex < (rankedElements.size() - 1))
			selectedTablePerspectiveIndex++;
		else if (!next && selectedTablePerspectiveIndex > 0)
			selectedTablePerspectiveIndex--;

		RankedElement newlySelectedRankedElement = rankedElements
				.get(selectedTablePerspectiveIndex);

		if (prevSelectedRankedElement != newlySelectedRankedElement) {

			int replaceIndex = brickColumnManager.indexOfBrickColumn(brickColumnManager
					.getBrickColumn(prevSelectedRankedElement.getColumnTablePerspective()));

			stratomex.removeTablePerspective(prevSelectedRankedElement
					.getColumnTablePerspective().getID());

			rankedElementToElementLayout.get(prevSelectedRankedElement)
					.clearBackgroundRenderers();

			rankedElementToElementLayout.get(newlySelectedRankedElement)
					.addBackgroundRenderer(highlightRankBackgroundRenderer);

			addTablePerspectiveToStratomex(newlySelectedRankedElement
					.getColumnTablePerspective());
			brickColumnManager.moveBrickColumn(brickColumnManager
					.getBrickColumn(newlySelectedRankedElement.getColumnTablePerspective()),
					replaceIndex);

			mainRankColumn.getLayoutManager().updateLayout();
		}
	}

	private void addTablePerspectiveToStratomex(TablePerspective newlySelectedTablePerspective) {
		stratomex.addTablePerspective(newlySelectedTablePerspective);

		BrickColumn brickColumn = brickColumnManager
				.getBrickColumn(newlySelectedTablePerspective);
		brickColumn.getLayout().addBackgroundRenderer(
				new BrickColumnGlowRenderer(new float[] { 0, 1, 0 }, brickColumn, false));
		brickColumn.getLayout().updateSubLayout();
	}

	public void selectChoice() {

		rankedElements.get(selectedTablePerspectiveIndex).getColumnTablePerspective()
				.setPrivate(false);
		isActive = false;

		brickColumnManager
				.getBrickColumn(
						rankedElements.get(selectedTablePerspectiveIndex)
								.getColumnTablePerspective()).getLayout()
				.clearBackgroundRenderers();
		referenceBrickColumn.getLayout().clearBackgroundRenderers();

		stratomex.updateLayout();
		stratomex.setLayoutDirty();
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
	public void setStratomex(GLStratomex stratomex) {
		this.stratomex = stratomex;
		brickColumnManager = stratomex.getBrickColumnManager();
	}

	/**
	 * @return the isActive, see {@link #isActive}
	 */
	public boolean isActive() {
		return isActive;
	}

	public void updatLayout() {
		if (isActive)
			mainRankColumn.setPixelSizeX(300);
		else
			mainRankColumn.setAbsoluteSizeX(0);
	}
}
