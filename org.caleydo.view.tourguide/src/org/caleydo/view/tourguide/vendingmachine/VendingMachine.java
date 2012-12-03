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
package org.caleydo.view.tourguide.vendingmachine;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.EventListeners;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.color.Colors;
import org.caleydo.core.view.listener.AddTablePerspectivesEvent;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.ILayoutedElement;
import org.caleydo.core.view.opengl.layout.LayoutManager;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.view.stratomex.GLStratomex;
import org.caleydo.view.stratomex.column.BrickColumn;
import org.caleydo.view.stratomex.column.BrickColumnGlowRenderer;
import org.caleydo.view.stratomex.column.BrickColumnManager;
import org.caleydo.view.tourguide.SerializedTourGuideView;
import org.caleydo.view.tourguide.data.DataDomainQuery;
import org.caleydo.view.tourguide.data.ESorting;
import org.caleydo.view.tourguide.data.ScoreQuery;
import org.caleydo.view.tourguide.data.Scores;
import org.caleydo.view.tourguide.data.ScoringElement;
import org.caleydo.view.tourguide.data.load.ExternalIDTypeScoreParser;
import org.caleydo.view.tourguide.data.load.ScoreParseSpecification;
import org.caleydo.view.tourguide.data.load.ui.ImportExternalScoreDialog;
import org.caleydo.view.tourguide.data.score.AGroupScore;
import org.caleydo.view.tourguide.data.score.AdjustedRankScore;
import org.caleydo.view.tourguide.data.score.CollapseScore;
import org.caleydo.view.tourguide.data.score.EScoreType;
import org.caleydo.view.tourguide.data.score.ExternalIDTypeScore;
import org.caleydo.view.tourguide.data.score.IScore;
import org.caleydo.view.tourguide.data.score.JaccardIndexScore;
import org.caleydo.view.tourguide.event.AddScoreColumnEvent;
import org.caleydo.view.tourguide.event.CreateScoreColumnEvent;
import org.caleydo.view.tourguide.event.ImportExternalScoreEvent;
import org.caleydo.view.tourguide.event.RemoveScoreColumnEvent;
import org.caleydo.view.tourguide.event.ScoreQueryReadyEvent;
import org.caleydo.view.tourguide.event.ScoreTablePerspectiveEvent;
import org.caleydo.view.tourguide.listener.ImportExternalScoreListener;
import org.caleydo.view.tourguide.listener.RemoveScoreColumnListener;
import org.caleydo.view.tourguide.listener.ScoreColumnListener;
import org.caleydo.view.tourguide.listener.ScoreQueryReadyListener;
import org.caleydo.view.tourguide.listener.ScoreTablePerspectiveListener;
import org.caleydo.view.tourguide.vendingmachine.ui.CreateCompositeScoreDialog;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.google.common.base.Function;

/**
 * <p>
 * The vending machine for stratification and cluster comparisons using scoring approach.
 * </p>
 *
 * @author Marc Streit
 */

public class VendingMachine extends AGLView implements IGLRemoteRenderingView, ILayoutedElement, ISelectionListener {

	public static final String VIEW_TYPE = "org.caleydo.view.tool.tourguide";
	public static final String VIEW_NAME = "Tour Guide";

	private LayoutManager layoutManager;
	private Column mainColumn;
	private ScoreQueryUI scoreQueryUI;
	private DataDomainQueryUI dataDomainSelector;

	private final EventListeners listeners = new EventListeners();

	private GLStratomex stratomex;
	private BrickColumnManager brickColumnManager;

	private DataDomainQuery dataDomainQuery = new DataDomainQuery();
	private ScoreQuery scoreQuery = new ScoreQuery(dataDomainQuery);

	private CaleydoTextRenderer textLargeRenderer;

	public VendingMachine(IGLCanvas glCanvas, Composite parentComposite, ViewFrustum viewFrustum) {
		super(glCanvas, parentComposite, viewFrustum, VIEW_TYPE, VIEW_NAME);

		dataDomainQuery.addPropertyChangeListener(DataDomainQuery.PROP_SELECTION, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getNewValue() == null)
					onHideDataDomain((ATableBasedDataDomain) evt.getOldValue());
				else
					onShowDataDomain((ATableBasedDataDomain) evt.getNewValue());
			}
		});
		scoreQuery.addPropertyChangeListener(ScoreQuery.PROP_ORDER_BY, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				recomputeScores();
			}
		});
		scoreQuery.addPropertyChangeListener(ScoreQuery.PROP_SELECTION, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				recomputeScores();
			}
		});
	}

	@Override
	public void init(GL2 gl) {
		displayListIndex = gl.glGenLists(1);

		super.renderStyle = renderStyle;
		detailLevel = EDetailLevel.HIGH;

		textRenderer = new CaleydoTextRenderer(12);
		textLargeRenderer = new CaleydoTextRenderer(24);
		layoutManager = new LayoutManager(viewFrustum, pixelGLConverter);

		initLayouts();
	}

	@Override
	public void initLocal(GL2 gl) {
		init(gl);
	}

	@Override
	public void initRemote(final GL2 gl, final AGLView glParentView, final GLMouseListener glMouseListener) {
		this.glMouseListener = glMouseListener;
		init(gl);
	}

	@Override
	public void displayLocal(GL2 gl) {
		pickingManager.handlePicking(this, gl);

		if (busyState == EBusyState.ON) {
			renderBusyMode(gl);
		} else {
			display(gl);
		}
	}

	public void initLayouts() {
		mainColumn = new Column("mainColumn");
		mainColumn.setGrabX(true);
		mainColumn.setGrabY(true);
		mainColumn.setBottomUp(false);

		layoutManager.setBaseElementLayout(mainColumn);
		layoutManager.setUseDisplayLists(false);

		dataDomainSelector = new DataDomainQueryUI(this);
		dataDomainSelector.setQuery(dataDomainQuery);
		mainColumn.append(dataDomainSelector);

		scoreQueryUI = new ScoreQueryUI(this, this, new Function<ScoringElement, Void>() {
			@Override
			public Void apply(ScoringElement elem) {
				addToStratomex(elem);
				return null;
			}
		});
		scoreQueryUI.setQuery(scoreQuery);
		mainColumn.append(scoreQueryUI);

		layoutManager.updateLayout();
	}

	@Override
	public void displayRemote(GL2 gl) {
		display(gl);
	}

	@Override
	public void display(GL2 gl) {
		if (!hasActiveStratomex()) {
			CaleydoTextRenderer tmp = textRenderer;
			textRenderer = textLargeRenderer; // overwrite for large font
			renderEmptyViewText(gl, "No Active Stratomex");
			textRenderer = tmp;
			return;
		}

		checkForHits(gl);
		processEvents();

		layoutManager.render(gl);
	}

	private boolean hasActiveStratomex() {
		return stratomex != null;
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		return new SerializedTourGuideView();
	}

	@Override
	public String toString() {
		return "TourGuide";
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();
		listeners.register(ScoreTablePerspectiveEvent.class, new ScoreTablePerspectiveListener().setHandler(this));
		listeners.register(AddScoreColumnEvent.class, new ScoreColumnListener(this));
		listeners.register(CreateScoreColumnEvent.class, new ScoreColumnListener(this));
		listeners.register(RemoveScoreColumnEvent.class, new RemoveScoreColumnListener(this));
		listeners.register(ScoreQueryReadyEvent.class, new ScoreQueryReadyListener(this));
		listeners.register(ImportExternalScoreEvent.class, new ImportExternalScoreListener(this));
	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();
		listeners.unregisterAll();
	}

	@Override
	public List<AGLView> getRemoteRenderedViews() {
		return null;
	}

	public void createStratificationScore(TablePerspective stratification) {
		IScore score = Scores.get().addIfAbsent(new AdjustedRankScore(stratification));
		onAddColumn(score);
	}

	public void createStratificationGroupScore(TablePerspective stratification, Group group) {
		IScore score = Scores.get().addIfAbsent(new JaccardIndexScore(stratification, group));
		onAddColumn(score);
	}

	public void createStratificationGroupScore(TablePerspective stratification, Iterable<Group> groups) {
		Scores manager = Scores.get();
		CollapseScore composite = new CollapseScore(stratification.getLabel());
		for (Group group : groups) {
			composite.add(manager.addIfAbsent(new JaccardIndexScore(stratification, group)));
		}
		onAddColumn(composite);
	}

	{
		// Highlight reference table
		// TODO
		// referenceBrickColumn.getLayout().addBackgroundRenderer(
		// new BrickColumnGlowRenderer(REFERENCE_SELECTION_COLOR, referenceBrickColumn,
		// false));
	}

	private static void triggerEvent(AEvent event) {
		GeneralManager.get().getEventPublisher().triggerEvent(event);
	}

	private void addTablePerspectiveToStratomex(TablePerspective newlySelectedTablePerspective) {

		if (!hasActiveStratomex())
			return;
		// FIXME as event
		AddTablePerspectivesEvent event = new AddTablePerspectivesEvent(newlySelectedTablePerspective);
		event.setReceiver(stratomex);
		event.setSender(this);
		triggerEvent(event);

		stratomex.addTablePerspective(newlySelectedTablePerspective);

		BrickColumn brickColumn = brickColumnManager.getBrickColumn(newlySelectedTablePerspective);
		brickColumn.getLayout().addBackgroundRenderer(
				new BrickColumnGlowRenderer(Colors.YELLOW.getRGBA(), brickColumn, false));
		brickColumn.getLayout().updateSubLayout();
	}

	private void addToStratomex(ScoringElement elem) {
		if (!hasActiveStratomex())
			return;
		AddTablePerspectivesEvent event = new AddTablePerspectivesEvent(elem.getStratification());
		event.setReceiver(stratomex);
		event.setSender(this);
		triggerEvent(event);
	}

	@Override
	protected void destroyViewSpecificContent(GL2 gl) {

		// TODO: remove button picking listeners
		// this.removeAllIDPickingListeners(DATASET_BUTTON_PICKING_TYPE,
		// DATASET_BUTTON_PICKING_ID);
	}

	@Override
	public ElementLayout getLayout() {
		return mainColumn;
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		super.reshape(drawable, x, y, width, height);
		layoutManager.updateLayout();
	}

	/**
	 * @param stratomex
	 */
	public void switchToStratomex(GLStratomex stratomex) {
		this.stratomex = stratomex;
		this.brickColumnManager = stratomex == null ? null : stratomex.getBrickColumnManager();
		setDisplayListDirty();
	}

	private void recomputeScores() {
		if (scoreQuery.isBusy()) {
			scoreQueryUI.setRunning(true);
			Job job = new Job("Update Tour Guide") {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					scoreQuery.waitTillComplete();
					GeneralManager.get().getEventPublisher()
							.triggerEvent(new ScoreQueryReadyEvent(VendingMachine.this));
					return Status.OK_STATUS;
				}
			};
			job.schedule();
		} else {
			onScoreQueryReady();
		}
	}

	public void onScoreQueryReady() {
		scoreQueryUI.setRunning(false);
		scoreQueryUI.setData(scoreQuery.call());
	}

	@Override
	public void onSelectionChanged(ScoringElement old, ScoringElement new_, IScore new_column) {
		if (!hasActiveStratomex())
			return;
		int replaceIndex = -1;
		if (old != null) { // remove old
			TablePerspective stratification = old.getStratification();
			replaceIndex = brickColumnManager.indexOfBrickColumn(brickColumnManager.getBrickColumn(stratification));
			stratomex.removeTablePerspective(stratification.getID());
		}
		if (new_ != null) {
			TablePerspective stratification = new_.getStratification();
			addTablePerspectiveToStratomex(stratification);
			if (replaceIndex >= 0)
				brickColumnManager.moveBrickColumn(brickColumnManager.getBrickColumn(stratification), replaceIndex);

			if (new_column instanceof AGroupScore) {
				// FIXME highlight connection between groups
				AGroupScore a = (AGroupScore) new_column;
				// a.getStratification();
				// triggerEvent(new SelectElementsEvent(this, new_.get
				// // new_..getReferenceTablePerspective().getRecordPerspective()));
			}
		}
	}

	private void onShowDataDomain(ATableBasedDataDomain dataDomain) {
		recomputeScores();
	}

	private void onHideDataDomain(ATableBasedDataDomain dataDomain) {
		boolean isCurrentlyVisible = false;
		for (ScoringElement sp : scoreQueryUI.getData()) {
			if (sp.getStratification().getDataDomain().equals(dataDomain)) {
				isCurrentlyVisible = true;
				break;
			}
		}
		if (!isCurrentlyVisible) // if no element is currently visible with this data domain just ignore that change
			return;
		// we have to update the list
		recomputeScores();
	}

	public void onCreateNewScore(final boolean createCollapsedScore) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				new CreateCompositeScoreDialog(new Shell(), Scores.get().getScoreIDs(), scoreQueryUI,
						createCollapsedScore).open();
			}
		});
	}

	public void onAddColumn(IScore score) {
		this.scoreQuery
				.sortBy(score, score.getScoreType() == EScoreType.STANDALONE_RANK ? ESorting.ASC : ESorting.DESC);
		this.scoreQuery.addSelection(score);
		recomputeScores();
	}

	public void onRemoveColumn(IScore score, boolean removeFromSystem) {
		ESorting bak = scoreQuery.getSorting(score);
		scoreQuery.sortBy(score, ESorting.NONE);
		scoreQuery.removeSelection(score);
		if (removeFromSystem) {
			Scores.get().remove(score);
		}
		if (bak != ESorting.NONE) // if it was relevant for sorting recompute
			recomputeScores();
	}

	public void onImportExternalScore(final ATableBasedDataDomain dataDomain, final IDCategory category) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				ScoreParseSpecification spec = new ImportExternalScoreDialog(new Shell(), category).call();
				if (spec == null)
					return;
				IDType target;
				if (dataDomain.getRecordIDCategory().equals(category))
					target = dataDomain.getRecordIDType();
				else
					target = dataDomain.getDimensionIDType();

				Collection<ExternalIDTypeScore> scores = new ExternalIDTypeScoreParser(spec, target).call();

				final Scores scoreManager = Scores.get();

				IScore last = null;
				for (ExternalIDTypeScore score : scores) {
					last = scoreManager.addPersistentScoreIfAbsent(score);
				}
				if (last != null) // add the last newly created one to the list
					triggerEvent(new AddScoreColumnEvent(last, scoreQueryUI));
			}
		});
	}

	/**
	 * @return
	 */
	public ScoreQueryUI getScoreQueryUI() {
		return scoreQueryUI;
	}


}
