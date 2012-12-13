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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.event.EventListeners;
import org.caleydo.core.event.data.ReplaceTablePerspectiveEvent;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.IMultiTablePerspectiveBasedView;
import org.caleydo.core.view.ITablePerspectiveBasedView;
import org.caleydo.core.view.listener.AddTablePerspectivesEvent;
import org.caleydo.core.view.listener.RemoveTablePerspectiveEvent;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.ElementLayouts;
import org.caleydo.core.view.opengl.layout.ILayoutedElement;
import org.caleydo.core.view.opengl.layout.LayoutManager;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.view.stratomex.GLStratomex;
import org.caleydo.view.tourguide.SerializedTourGuideView;
import org.caleydo.view.tourguide.data.DataDomainQuery;
import org.caleydo.view.tourguide.data.ESorting;
import org.caleydo.view.tourguide.data.ScoreQuery;
import org.caleydo.view.tourguide.data.Scores;
import org.caleydo.view.tourguide.data.ScoringElement;
import org.caleydo.view.tourguide.data.load.ImportExternalScoreCommand;
import org.caleydo.view.tourguide.data.score.AdjustedRandScore;
import org.caleydo.view.tourguide.data.score.CollapseScore;
import org.caleydo.view.tourguide.data.score.IScore;
import org.caleydo.view.tourguide.data.score.JaccardIndexScore;
import org.caleydo.view.tourguide.data.score.MutualExclusiveScore;
import org.caleydo.view.tourguide.data.serialize.ISerializeableScore;
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
import org.caleydo.view.tourguide.listener.StratomexTablePerspectiveListener;
import org.caleydo.view.tourguide.vendingmachine.ui.CreateAdjustedRandScoreDialog;
import org.caleydo.view.tourguide.vendingmachine.ui.CreateCompositeScoreDialog;
import org.caleydo.view.tourguide.vendingmachine.ui.CreateJaccardIndexScoreDialog;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * <p>
 * The vending machine for stratification and cluster comparisons using scoring approach.
 * </p>
 *
 * @author Marc Streit
 */

public class VendingMachine extends AGLView implements IGLRemoteRenderingView, ILayoutedElement {
	public static final String VIEW_TYPE = "org.caleydo.view.tool.tourguide";
	public static final String VIEW_NAME = "Tour Guide";


	private LayoutManager layoutManager;
	private Column mainColumn;
	private ScoreQueryUI scoreQueryUI;
	private DataDomainQueryUI dataDomainSelector;

	private final EventListeners listeners = new EventListeners();

	private StratomexAdapter stratomex = new StratomexAdapter();
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
		final PropertyChangeListener recomputeListener = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				recomputeScores();
			}
		};
		dataDomainQuery.addPropertyChangeListener(DataDomainQuery.PROP_FILTER, recomputeListener);
		scoreQuery.addPropertyChangeListener(ScoreQuery.PROP_ORDER_BY, recomputeListener);
		scoreQuery.addPropertyChangeListener(ScoreQuery.PROP_SELECTION, recomputeListener);
		scoreQuery.addPropertyChangeListener(ScoreQuery.PROP_TOP, recomputeListener);
		scoreQuery.addPropertyChangeListener(ScoreQuery.PROP_FILTER, recomputeListener);
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
		layoutManager.setUseDisplayLists(true);

		dataDomainSelector = new DataDomainQueryUI(this);
		dataDomainSelector.setQuery(dataDomainQuery);
		mainColumn.append(dataDomainSelector);

		mainColumn.append(ElementLayouts.createYSpacer(20));

		scoreQueryUI = new ScoreQueryUI(this, this.stratomex);
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
		if (!stratomex.hasOne()) {
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
		listeners.register(RemoveTablePerspectiveEvent.class, new StratomexTablePerspectiveListener(this));
		listeners.register(AddTablePerspectivesEvent.class, new StratomexTablePerspectiveListener(this));
		listeners.register(ReplaceTablePerspectiveEvent.class, new StratomexTablePerspectiveListener(this));
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
		IScore score = Scores.get().addIfAbsent(new AdjustedRandScore(stratification));
		onAddColumn(score);
	}

	public void createStratificationGroupScore(TablePerspective stratification, Group group) {
		IScore score = Scores.get().addIfAbsent(new JaccardIndexScore(stratification, group));
		onAddColumn(score);
	}

	public void createMutualExclusiveGroupScore(TablePerspective stratification, Group act) {
		Scores manager = Scores.get();
		Collection<JaccardIndexScore> scores = new ArrayList<>();
		JaccardIndexScore actScore = null;
		for (Group group : stratification.getRecordPerspective().getVirtualArray().getGroupList()) {
			JaccardIndexScore s = manager.addIfAbsent(new JaccardIndexScore(stratification, group));
			scores.add(s);
			if (group.equals(act))
				actScore = s;
		}
		onAddColumn(manager.addIfAbsent(new MutualExclusiveScore(actScore, scores)));
	}

	public void createStratificationGroupScore(TablePerspective stratification, Iterable<Group> groups) {
		Scores manager = Scores.get();
		CollapseScore composite = new CollapseScore(stratification.getLabel());
		for (Group group : groups) {
			composite.add(manager.addIfAbsent(new JaccardIndexScore(stratification, group)));
		}
		onAddColumn(composite);
	}

	@Override
	protected void destroyViewSpecificContent(GL2 gl) {
		this.stratomex.cleanUp();
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
		if (this.stratomex.setStratomex(stratomex))
			setDisplayListDirty();
	}

	private void recomputeScores() {
		if (scoreQueryUI.isRunning())
			return;
		if (scoreQuery.isBusy()) {
			scoreQueryUI.setRunning(true);
			Job job = new Job("Update Tour Guide") {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					scoreQuery.waitTillComplete();
					System.out.println("sending ready");
					GeneralManager.get().getEventPublisher()
							.triggerEvent(new ScoreQueryReadyEvent(VendingMachine.this));
					return Status.OK_STATUS;
				}
			};
			job.schedule();
		} else {
			System.out.println("not busy");
			GeneralManager.get().getEventPublisher().triggerEvent(new ScoreQueryReadyEvent(VendingMachine.this));
		}

	}

	public void onScoreQueryReady() {
		System.out.println("ready");
		scoreQueryUI.setRunning(false);
		scoreQueryUI.setData(scoreQuery.call());
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

	public void onCreateNewScore(final CreateScoreColumnEvent.Type type) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				switch (type) {
				case COMBINED:
					new CreateCompositeScoreDialog(new Shell(), Scores.get().getScoreIDs(), scoreQueryUI, false).open();
					break;
				case COLLAPSED:
					new CreateCompositeScoreDialog(new Shell(), Scores.get().getScoreIDs(), scoreQueryUI, true).open();
					break;
				case JACCARD:
					new CreateJaccardIndexScoreDialog(new Shell(), scoreQueryUI).open();
					break;
				case ADJUSTED_RAND:
					new CreateAdjustedRandScoreDialog(new Shell(), scoreQueryUI).open();
					break;
				}
			}
		});
	}

	public void onAddColumn(IScore score) {
		this.scoreQuery.addSelection(score);
	}

	public void onRemoveColumn(IScore score, boolean removeFromSystem) {
		scoreQuery.sortBy(score, ESorting.NONE);
		scoreQuery.removeSelection(score);
		if (removeFromSystem) {
			Scores.get().remove(score);
		}
	}

	public void onImportExternalScore(final ATableBasedDataDomain dataDomain, boolean dimensionDirection,
			Class<? extends ISerializeableScore> type) {
		Display.getDefault().asyncExec(
				new ImportExternalScoreCommand(dataDomain, dimensionDirection, type, scoreQueryUI));
	}

	/**
	 * @return
	 */
	public ScoreQueryUI getScoreQueryUI() {
		return scoreQueryUI;
	}

	/**
	 * @param receiver
	 * @param tablePerspectiveID
	 */
	public void onStratomexRemoveBrick(IMultiTablePerspectiveBasedView receiver, int tablePerspectiveID) {
		if (!stratomex.is(receiver))
			return;
		stratomex.removeBrick(tablePerspectiveID);
		final ScoringElement selected = this.scoreQueryUI.getSelected();
		if (selected != null && selected.getStratification().getID() == tablePerspectiveID) {
			this.scoreQueryUI.setSelected(-1);
		}
	}

	public void onStratomexAddBricks(ITablePerspectiveBasedView receiver, Collection<TablePerspective> tablePerspectives) {
		if (!stratomex.is(receiver))
			return;
		stratomex.addBricks(tablePerspectives);
	}

	public void onStratomexReplaceBricks(Integer receiver, TablePerspective oldPerspective,
			TablePerspective newPerspective) {
		if (!stratomex.is(receiver))
			return;
		stratomex.replaceBricks(oldPerspective, newPerspective);
	}

}
