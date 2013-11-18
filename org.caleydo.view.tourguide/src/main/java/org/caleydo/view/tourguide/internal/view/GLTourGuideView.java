/*******************************************************************************
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.media.opengl.GLAutoDrawable;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.event.EventListenerManager.DeepScan;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.data.DataDomainUpdateEvent;
import org.caleydo.core.event.data.NewDataDomainLoadedEvent;
import org.caleydo.core.event.data.RemoveDataDomainEvent;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.canvas.GLThreadListenerWrapper;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.canvas.IGLKeyListener;
import org.caleydo.core.view.opengl.canvas.IGLMouseListener;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.AGLElementView;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.basic.ScrollBar;
import org.caleydo.core.view.opengl.layout2.basic.ScrollingDecorator;
import org.caleydo.core.view.opengl.layout2.basic.WaitingElement;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.view.tourguide.api.event.AddScoreColumnEvent;
import org.caleydo.view.tourguide.api.external.ImportExternalScoreCommand;
import org.caleydo.view.tourguide.api.model.ADataDomainQuery;
import org.caleydo.view.tourguide.api.model.AScoreRow;
import org.caleydo.view.tourguide.api.model.CategoricalDataDomainQuery;
import org.caleydo.view.tourguide.api.score.ISerializeableScore;
import org.caleydo.view.tourguide.api.score.MultiScore;
import org.caleydo.view.tourguide.api.score.Scores;
import org.caleydo.view.tourguide.api.vis.ITourGuideView;
import org.caleydo.view.tourguide.internal.SerializedTourGuideView;
import org.caleydo.view.tourguide.internal.TourGuideRenderStyle;
import org.caleydo.view.tourguide.internal.compute.ComputeAllOfJob;
import org.caleydo.view.tourguide.internal.compute.ComputeExtrasJob;
import org.caleydo.view.tourguide.internal.compute.ComputeForScoreJob;
import org.caleydo.view.tourguide.internal.event.ExtraInitialScoreQueryReadyEvent;
import org.caleydo.view.tourguide.internal.event.ImportExternalScoreEvent;
import org.caleydo.view.tourguide.internal.event.InitialScoreQueryReadyEvent;
import org.caleydo.view.tourguide.internal.event.JobDiedEvent;
import org.caleydo.view.tourguide.internal.event.JobStateProgressEvent;
import org.caleydo.view.tourguide.internal.event.RemoveLeadingScoreColumnsEvent;
import org.caleydo.view.tourguide.internal.event.ScoreQueryReadyEvent;
import org.caleydo.view.tourguide.internal.model.CustomSubList;
import org.caleydo.view.tourguide.internal.view.col.IScoreMixin;
import org.caleydo.view.tourguide.internal.view.col.ScoreIntegerRankColumnModel;
import org.caleydo.view.tourguide.internal.view.col.ScoreRankColumnModel;
import org.caleydo.view.tourguide.internal.view.col.SizeRankColumnModel;
import org.caleydo.view.tourguide.internal.view.ui.ADataDomainElement;
import org.caleydo.view.tourguide.internal.view.ui.DataDomainQueryUI;
import org.caleydo.view.tourguide.internal.view.ui.pool.ScorePoolUI;
import org.caleydo.view.tourguide.spi.adapter.ITourGuideAdapter;
import org.caleydo.view.tourguide.spi.adapter.ITourGuideDataMode;
import org.caleydo.view.tourguide.spi.score.IRegisteredScore;
import org.caleydo.view.tourguide.spi.score.IScore;
import org.caleydo.view.tourguide.spi.score.IStratificationScore;
import org.caleydo.vis.lineup.config.RankTableConfigBase;
import org.caleydo.vis.lineup.config.RankTableUIConfigBase;
import org.caleydo.vis.lineup.layout.RowHeightLayouts;
import org.caleydo.vis.lineup.model.ACompositeRankColumnModel;
import org.caleydo.vis.lineup.model.ARankColumnModel;
import org.caleydo.vis.lineup.model.GroupRankColumnModel;
import org.caleydo.vis.lineup.model.IRow;
import org.caleydo.vis.lineup.model.MaxRankColumnModel;
import org.caleydo.vis.lineup.model.RankColumnModels;
import org.caleydo.vis.lineup.model.RankRankColumnModel;
import org.caleydo.vis.lineup.model.RankTableModel;
import org.caleydo.vis.lineup.model.StringRankColumnModel;
import org.caleydo.vis.lineup.model.mapping.PiecewiseMapping;
import org.caleydo.vis.lineup.model.mixin.IAnnotatedColumnMixin;
import org.caleydo.vis.lineup.model.mixin.IRankColumnModel;
import org.caleydo.vis.lineup.model.mixin.IRankableColumnMixin;
import org.caleydo.vis.lineup.ui.RankTableKeyListener;
import org.caleydo.vis.lineup.ui.RankTableUIMouseKeyListener;
import org.caleydo.vis.lineup.ui.RenderStyle;
import org.caleydo.vis.lineup.ui.TableBodyUI;
import org.caleydo.vis.lineup.ui.TableUI;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.swt.widgets.Display;

import com.google.common.collect.Lists;

/**
 * @author Samuel Gratzl
 *
 */
public class GLTourGuideView extends AGLElementView implements ITourGuideView {
	public static final String VIEW_TYPE = "org.caleydo.view.tool.tourguide";
	public static final String VIEW_NAME = "LineUp";


	private final RankTableModel table;

	private final BitSet mask = new BitSet();
	private final List<ADataDomainQuery> queries = new ArrayList<>();

	private final PropertyChangeListener listener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			switch (evt.getPropertyName()) {
			case ADataDomainQuery.PROP_ACTIVE:
				onActiveChanged((ADataDomainQuery) evt.getSource(), (boolean) evt.getNewValue());
				break;
			case ADataDomainQuery.PROP_MASK:
			case ADataDomainQuery.PROP_MIN_CLUSTER_SIZE_FILTER:
			case CategoricalDataDomainQuery.PROP_GROUP_SELECTION:
				ADataDomainQuery s = (ADataDomainQuery) evt.getSource();
				if (s.isActive()) {
					scheduleAllOf(s);
					updateSpecificColumns(s);
				}
			}
		}
	};

	/**
	 * mode of this tour guide
	 */
	private final ITourGuideAdapter adapter;
	private final ITourGuideDataMode mode;

	private final WaitingElement waiting = new WaitingElement();

	private boolean noAttachedView = false;
	private final GLElement noAttachedViewLabel = new GLElement(new IGLRenderer() {
		@Override
		public void render(GLGraphics g, float w, float h, GLElement parent) {
			g.color(1, 1, 1, 0.5f).fillRect(0, 0, w, h);
			g.drawText("No compatible view visible", 10, h * 0.5f - 12, w - 20, 24, VAlign.CENTER);
		}
	});

	private final IJobChangeListener jobListener = new JobChangeAdapter() {
		@Override
		public void done(IJobChangeEvent event) {
			onJobDone(event.getResult());
		}

		@Override
		public void running(IJobChangeEvent event) {
			onJobStarted();
		}
	};
	@DeepScan
	private final IGLKeyListener tableKeyListener;
	private IGLKeyListener tableKeyListener2; // lazy and manually scanned
	private IGLMouseListener tableMouseListener; // lazy

	private DataDomainQueryUI dataDomainQueryUI;
	private TableUI tableUI;

	/**
	 * history of added score to select what was the last added score, see #1493, use a weak list to avoid creating
	 * references
	 */
	private final Deque<WeakReference<IScore>> scoreHistory = new LinkedList<>();

	public GLTourGuideView(IGLCanvas glCanvas, ITourGuideAdapter adapter) {
		super(glCanvas, VIEW_TYPE, VIEW_NAME);

		this.adapter = adapter;
		this.mode = adapter.asMode();
		this.table = new RankTableModel(new RankTableConfigBase());
		this.table.addPropertyChangeListener(RankTableModel.PROP_SELECTED_ROW, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				onSelectRow((AScoreRow) evt.getOldValue(), (AScoreRow) evt.getNewValue());
			}
		});
		this.table.addPropertyChangeListener(RankTableModel.PROP_POOL, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getNewValue() == null) { // removed aka destroyed
					// destroy persistent scores
					if (evt.getOldValue() instanceof IScoreMixin) {
						IScoreMixin r = (IScoreMixin) evt.getOldValue();
						IScore score = r.getScore();
						if (score instanceof ISerializeableScore)
							Scores.get().removePersistentScore((ISerializeableScore) score);
					}
				}
			}
		});
		this.table.add(new RankRankColumnModel().setWidth(30));
		this.adapter.addDefaultColumns(this.table);

		addAllExternalScore(this.table);

		for (ADataDomainQuery q : mode.createDataDomainQueries()) {
			q.addPropertyChangeListener(ADataDomainQuery.PROP_ACTIVE, listener);
			q.addPropertyChangeListener(ADataDomainQuery.PROP_MASK, listener);
			q.addPropertyChangeListener(CategoricalDataDomainQuery.PROP_GROUP_SELECTION, listener);
			q.addPropertyChangeListener(ADataDomainQuery.PROP_MIN_CLUSTER_SIZE_FILTER, listener);
			queries.add(q);
		}

		// wrap for having the right thread
		this.tableKeyListener = GLThreadListenerWrapper.wrap(new RankTableKeyListener(table));
	}

	/**
	 * @return the queries, see {@link #queries}
	 */
	@Override
	public List<ADataDomainQuery> getQueries() {
		return Collections.unmodifiableList(queries);
	}

	private void updateQueryUIStates() {
		if (this.dataDomainQueryUI != null)
			this.dataDomainQueryUI.updateSelections();
	}

	/**
	 * @param table2
	 */
	private void addAllExternalScore(RankTableModel table) {
		for (IScore score : Scores.get().getPersistentScores()) {
			if (!score.supports(mode))
				continue;
			ARankColumnModel model = createColumnModel(score);
			table.add(model);
			model.hide();
		}
	}

	/**
	 * @param score
	 * @return
	 */
	private ARankColumnModel createColumnModel(IScore score) {
		this.scoreHistory.add(new WeakReference<>(score));
		PiecewiseMapping mapping = score.createMapping();
		if (mapping == null) // by conventions
			return new ScoreIntegerRankColumnModel(score);
		return new ScoreRankColumnModel(score);
	}

	@ListenTo
	private void onAddDataDomain(final NewDataDomainLoadedEvent event) {
		IDataDomain dd = event.getDataDomain();

		if (!mode.apply(dd))
			return;

		for (ADataDomainQuery query : mode.createDataDomainQuery(dd)) {
			queries.add(query);
			query.addPropertyChangeListener(ADataDomainQuery.PROP_ACTIVE, listener);
			query.addPropertyChangeListener(ADataDomainQuery.PROP_MASK, listener);
			query.addPropertyChangeListener(CategoricalDataDomainQuery.PROP_GROUP_SELECTION, listener);
			query.addPropertyChangeListener(ADataDomainQuery.PROP_MIN_CLUSTER_SIZE_FILTER, listener);
			getDataDomainQueryUI().add(query);
		}
	}

	@ListenTo
	private void onRemoveDataDomain(final RemoveDataDomainEvent event) {
		final String id = event.getEventSpace();
		for (ADataDomainQuery query : queries) {
			if (Objects.equals(query.getDataDomain().getDataDomainID(), id)) {
				query.cleanup();
				query.removePropertyChangeListener(ADataDomainQuery.PROP_ACTIVE, listener);
				query.removePropertyChangeListener(ADataDomainQuery.PROP_MASK, listener);
				query.removePropertyChangeListener(CategoricalDataDomainQuery.PROP_GROUP_SELECTION, listener);
				query.removePropertyChangeListener(ADataDomainQuery.PROP_MIN_CLUSTER_SIZE_FILTER, listener);
				queries.remove(query);
				getDataDomainQueryUI().remove(query);
				if (query.isActive())
					updateMask();
				break;
			}
		}
	}

	@ListenTo
	private void onDataDomainUpdate(DataDomainUpdateEvent event) {
		boolean update = false;
		List<Pair<ADataDomainQuery, List<AScoreRow>>> extras = new ArrayList<>();
		for (ADataDomainQuery query : queries) {
			if (event.getDataDomain() != query.getDataDomain())
				continue;
			List<AScoreRow> added = query.onDataDomainUpdated();
			if (added == null)
				continue;

			if (added.isEmpty()) {
				update = true;
				continue;
			}
			extras.add(Pair.make(query, added));
		}
		if (!extras.isEmpty())
			scheduleExtras(extras);
		else if (update)
			updateMask();
	}

	protected void onActiveChanged(ADataDomainQuery q, boolean active) {
		if (q.isInitialized()) {
			if (active) {
				q.createSpecificColumns(table);
				removeAllSimpleFilter();
				scheduleAllOf(q);
			} else {
				q.removeSpecificColumns(table);
				updateMask();
			}
			return;
		} else {
			if (active)
				removeAllSimpleFilter();
			scheduleAllOf(q);
		}
	}

	protected void updateSpecificColumns(ADataDomainQuery q) {
		q.updateSpecificColumns(table);
	}

	@Override
	public void removeAllSimpleFilter() {
		// reset all string and integer filters
		for (ARankColumnModel model : table.getFlatColumns()) {
			if (model instanceof StringRankColumnModel)
				((StringRankColumnModel) model).setFilter(null, true, false);
			else if (model instanceof SizeRankColumnModel)
				((SizeRankColumnModel) model).setFilter(null, null);
		}
	}

	/**
	 * @param q
	 */
	private void scheduleAllOf(final ADataDomainQuery q) {
		Collection<IScore> scores = new ArrayList<>(getVisibleScores(null, false));
		ComputeAllOfJob job = new ComputeAllOfJob(q, scores, this);
		if (job.hasThingsToDo()) {
			waiting.reset(job);
			job.addJobChangeListener(jobListener);
			getPopupLayer().show(waiting, null, 0);
			job.schedule();
		} else {
			updateMask();
		}
	}

	private void scheduleAllOf(Collection<IScore> toCompute) {
		ComputeForScoreJob job = new ComputeForScoreJob(toCompute, table.getData(),
				table.getMyRanker(null).getFilter(), this);
		if (job.hasThingsToDo()) {
			waiting.reset(job);
			job.addJobChangeListener(jobListener);
			getPopupLayer().show(waiting, null, 0);
			job.schedule();
		} else {
			addColumnsImpl(toCompute);
		}
	}

	private void scheduleExtras(List<Pair<ADataDomainQuery, List<AScoreRow>>> extras) {
		Collection<IScore> scores = new ArrayList<>(getVisibleScores(null, false));
		ComputeExtrasJob job = new ComputeExtrasJob(extras, scores, this);
		if (job.hasThingsToDo()) {
			waiting.reset(job);
			job.addJobChangeListener(jobListener);
			getPopupLayer().show(waiting, null, 0);
			job.schedule();
		} else {
			onExtraInitialScoreQueryReady(new ExtraInitialScoreQueryReadyEvent(extras));
		}
	}

	@ListenTo(sendToMe = true)
	private void onProgressEvent(JobStateProgressEvent event) {
		if (event.isErroneous())
			waiting.error(event.getText());
		else
			waiting.progress(event.getCompleted(), event.getText());
	}

	/**
	 * @param status
	 */
	protected void onJobDone(IStatus status) {
		EventPublisher.trigger(new JobStateProgressEvent(status.getMessage(), 1.0f, !status.isOK()).to(this));
		if (status.getSeverity() == IStatus.ERROR) {
			EventPublisher.trigger(new JobDiedEvent().to(this));
		}
	}

	/**
	 *
	 */
	protected void onJobStarted() {
	}

	@ListenTo(sendToMe = true)
	private void onJobDied(JobDiedEvent event) {
		// job died continue as was nothing
		getPopupLayer().hide(waiting);
	}

	@ListenTo(sendToMe = true)
	private void onScoreQueryReady(ScoreQueryReadyEvent event) {
		getPopupLayer().hide(waiting);
		if (event.getScores() != null) {
			addColumnsImpl(event.getScores());
		} else {
			invalidVisibleScores();
			updateMask();
		}
	}

	@SuppressWarnings("unchecked")
	@ListenTo(sendToMe = true)
	private void onInitialScoreQueryReady(InitialScoreQueryReadyEvent event) {
		getPopupLayer().hide(waiting);
		int offset = table.getDataSize();
		ADataDomainQuery q = event.getNewQuery();
		System.out.println("add data of " + q.getDataDomain().getLabel());
		table.addData(q.getOrCreate());
		List<?> m = table.getDataModifiable();
		// use sublists to save memory
		q.init(offset, new CustomSubList<AScoreRow>((List<AScoreRow>) m, offset, m.size() - offset));
		q.createSpecificColumns(table);

		invalidVisibleScores();
		updateMask();
	}

	@SuppressWarnings("unchecked")
	@ListenTo(sendToMe = true)
	private void onExtraInitialScoreQueryReady(ExtraInitialScoreQueryReadyEvent event) {
		getPopupLayer().hide(waiting);

		for (Pair<ADataDomainQuery, List<AScoreRow>> pair : event.getExtras()) {
			ADataDomainQuery q = pair.getFirst();
			System.out.println("add data of " + q.getDataDomain().getLabel());
			int offset = table.getDataSize();
			table.addData(pair.getSecond());
			List<?> m = table.getDataModifiable();
			// use sublists to save memory
			q.addData(offset, new CustomSubList<AScoreRow>((List<AScoreRow>) m, offset, m.size() - offset));
		}

		invalidVisibleScores();
		updateMask();
	}

	private void addColumnsImpl(Collection<IScore> scores) {
		for (IScore s : scores) {
			int lastLabel = findLastLabelColumn();
			if (s instanceof MultiScore) {
				ACompositeRankColumnModel combined = table.getConfig().createNewCombined(
						((MultiScore) s).getCombinedType());
				if (combined instanceof IAnnotatedColumnMixin) {
					((IAnnotatedColumnMixin) combined).setTitle(s.getLabel());
				}
				table.add(lastLabel + 1, combined);
				for (IScore s2 : ((MultiScore) s)) {
					combined.add(createColumnModel(s2));
				}
				if (combined instanceof IRankableColumnMixin)
					((IRankableColumnMixin) combined).orderByMe();
				else
					for (ARankColumnModel m : combined) {
						if (m instanceof IRankableColumnMixin) {
							((IRankableColumnMixin) m).orderByMe();
							break;
						}
					}
			} else {
				ARankColumnModel ss = createColumnModel(s);
				table.add(lastLabel + 1, ss);
				if (ss instanceof IScoreMixin)
					((IScoreMixin) ss).orderByMe();
			}
		}

		TableBodyUI bodyUI = getTableBodyUI();
		if (bodyUI != null)
			bodyUI.scrollFirst(); // scroll to the top
	}

	/**
	 * @return
	 */
	private int findLastLabelColumn() {
		List<ARankColumnModel> cols = table.getColumns();
		int l = 0;
		for (int i = 0; i < cols.size(); ++i) {
			if (cols.get(i) instanceof StringRankColumnModel)
				l = i;
		}
		return l;
	}

	private void updateMask() {
		this.mask.clear();
		for (ADataDomainQuery q : this.queries) {
			if (!q.isInitialized() || !q.isActive())
				continue;
			this.mask.or(q.getMask());
		}
		table.setDataMask(this.mask);
	}

	private GLElementContainer getVis() {
		return (GLElementContainer) getRoot();
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		super.init(drawable);
		this.canvas.addKeyListener(tableKeyListener);
		RankTableUIMouseKeyListener tableUIListener = new RankTableUIMouseKeyListener(getTableBodyUI());
		this.tableKeyListener2 = GLThreadListenerWrapper.wrap((IGLKeyListener) tableUIListener);
		this.tableMouseListener = GLThreadListenerWrapper.wrap((IGLMouseListener) tableUIListener);
		this.canvas.addKeyListener(eventListeners.register(this.tableKeyListener2));
		this.canvas.addMouseListener(eventListeners.register(this.tableMouseListener));

		this.noAttachedView = !this.adapter.isBound2View(); // artifically set to the wrong value to ensure an update
		updateBound2ViewState();

		// select first data domain element by default
		if (!queries.isEmpty()) {
			((ADataDomainElement) getDataDomainQueryUI().get(0)).setSelected(true);
		}

		eventListeners.register(adapter);
		this.adapter.setup(this, getVis());
	}

	@Override
	public void updateBound2ViewState() {
		updateQueryUIStates();
		boolean act = this.adapter.isBound2View();
		boolean prev = !this.noAttachedView;
		if (act == prev)
			return;
		if (prev)
			getPopupLayer().show(noAttachedViewLabel, null, 0);
		else
			getPopupLayer().hide(noAttachedViewLabel);
		this.noAttachedView = !this.noAttachedView;

	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		this.adapter.cleanup();
		canvas.removeKeyListener(tableKeyListener);
		canvas.removeKeyListener(tableKeyListener2);
		canvas.removeMouseListener(tableMouseListener);
		super.dispose(drawable);
	}

	@Override
	protected GLElement createRoot() {
		GLElementContainer vis = new GLElementContainer(new ReactiveFlowLayout(10));
		this.dataDomainQueryUI = new DataDomainQueryUI(queries, mode);
		vis.add(dataDomainQueryUI);
		this.tableUI = new TableUI(table, new RankTableUIConfig(), RowHeightLayouts.UNIFORM);
		ScrollingDecorator sc = new ScrollingDecorator(tableUI, new ScrollBar(true), null, RenderStyle.SCROLLBAR_WIDTH);
		vis.add(sc);

		vis.add(new ScorePoolUI(table, new RankTableUIConfig(), GLTourGuideView.this));
		return vis;
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		adapter.preDisplay();
		super.display(drawable);
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		return new SerializedTourGuideView();
	}

	protected void onSelectRow(AScoreRow old, AScoreRow new_) {
		if (adapter.canShowPreviews())
			updatePreview(old, new_);
	}

	private void updatePreview(AScoreRow old, AScoreRow new_) {
		if (adapter == null)
			return;
		adapter.update(old, new_, getVisibleScores(new_, true), getSortedByScore());
	}

	private IScore getSortedByScore() {
		IRankableColumnMixin orderBy = table.getMyRanker(null).getOrderBy();
		return toRankedScore(orderBy);
	}

	private IScore toRankedScore(IRankColumnModel model) {
		if (model instanceof IScoreMixin)
			return ((IScoreMixin) model).getScore();
		if (model instanceof ACompositeRankColumnModel)
			return toRankedScore(((ACompositeRankColumnModel) model).get(0));
		return null;
	}

	private DataDomainQueryUI getDataDomainQueryUI() {
		return dataDomainQueryUI;
	}

	/**
	 * @return
	 */
	private TableBodyUI getTableBodyUI() {
		if (this.tableUI == null)
			return null;
		return this.tableUI.getBody();
	}

	/**
	 * @return
	 */
	private Collection<IScore> getVisibleScores(AScoreRow row, boolean justLastStratificationOne) {
		Collection<IScore> r = new ArrayList<>();
		Deque<ARankColumnModel> cols = new LinkedList<>();
		if (justLastStratificationOne) {
			cols.addAll(Lists.newArrayList(table.getColumnsOf(table.getDefaultRanker())));
		} else
			cols.addAll(table.getColumns());

		while (!cols.isEmpty()) {
			ARankColumnModel model = cols.pollFirst();
			if (model instanceof IScoreMixin) {
				r.add(((IScoreMixin) model).getScore());
			} else if (model instanceof MaxRankColumnModel) {
				MaxRankColumnModel max = (MaxRankColumnModel) model;
				if (row != null) {
					int repr = max.getSplittedValue(row).getRepr();
					cols.add(max.get(repr));
				} else {
					cols.addAll(max.getChildren());
				}
			} else if (model instanceof ACompositeRankColumnModel) {
				cols.addAll(((ACompositeRankColumnModel) model).getChildren());
			}
		}
		for (Iterator<IScore> it = r.iterator(); it.hasNext();) {
			if (!it.next().supports(mode))
				it.remove();
		}
		if (justLastStratificationOne && !r.isEmpty()) {
			// #1493 select just the last score
			for (Iterator<WeakReference<IScore>> it = this.scoreHistory.descendingIterator(); it.hasNext();) {
				IScore s = it.next().get();
				if (s == null)
					it.remove();
				else if (r.contains(s) && s instanceof IStratificationScore)
					return Collections.singleton(s);
			}
		}
		return r;
	}

	private void invalidVisibleScores() {
		for (ARankColumnModel model : RankColumnModels.flatten(table.getColumns())) {
			if (model instanceof IScoreMixin) {
				((IScoreMixin) model).dirty();
			}
		}
	}

	@Override
	public void addColumns(IScore... scores) {
		if (scores.length <= 0)
			return;
		Collection<IScore> toCompute = new ArrayList<>();

		for (IScore s : scores) {
			if (!s.supports(mode))
				continue;
			if (s instanceof IRegisteredScore)
				((IRegisteredScore) s).onRegistered();
			if (s instanceof MultiScore) {
				MultiScore sm = (MultiScore) s;
				MultiScore tmp = new MultiScore(sm.getLabel(), sm.getColor(), sm.getBGColor(), sm.getCombinedType());
				for (IScore s2 : ((MultiScore) s)) {
					if (s2 instanceof IRegisteredScore)
						((IRegisteredScore) s2).onRegistered();
					tmp.add(s2);
				}
				toCompute.add(tmp);
			} else
				toCompute.add(s);
		}
		if (toCompute.isEmpty())
			return;
		scheduleAllOf(toCompute);
	}

	@ListenTo(sendToMe = true)
	private void onAddColumn(AddScoreColumnEvent event) {
		if (event.getScores().isEmpty())
			return;
		addColumns(event.getScores().toArray(new IScore[0]));
	}

	@Override
	public void removeLeadingScoreColumns() {
		List<ARankColumnModel> columns = this.table.getColumns();
		boolean hasOne = false;
		Collection<ARankColumnModel> toremove = new ArrayList<>();
		for (ARankColumnModel col : columns) {
			if ((col instanceof IScoreMixin && !(((IScoreMixin) col).getScore() instanceof ISerializeableScore))
					|| (col instanceof ACompositeRankColumnModel && hasScore((ACompositeRankColumnModel) col))) {
				hasOne = true;
				toremove.add(col);
			} else if (hasOne || col instanceof GroupRankColumnModel)
				break;
		}
		for (ARankColumnModel col : toremove) {
			Set<ARankColumnModel> children = RankColumnModels.flatten(col);
			// look in the children for things to persist i.e move to the memo pad, e.g. external scores
			for (ARankColumnModel r : children) {
				if (r instanceof IScoreMixin && ((IScoreMixin) r).getScore() instanceof ISerializeableScore)
					r.hide();
				else if (r instanceof IScoreMixin)
					scoreHistory.remove(((IScoreMixin) r).getScore());
			}

			table.remove(col);
		}
	}

	@ListenTo
	private void onRemoveLeadingScoreColumns(RemoveLeadingScoreColumnsEvent event) {
		removeLeadingScoreColumns();
	}

	/**
	 * @param col
	 * @return
	 */
	private boolean hasScore(ACompositeRankColumnModel col) {
		for (ARankColumnModel c : col) {
			if (c instanceof IScoreMixin
					|| (c instanceof ACompositeRankColumnModel && hasScore((ACompositeRankColumnModel) c)))
				return true;
		}
		return false;
	}

	@ListenTo
	private void onImportExternalScore(ImportExternalScoreEvent event) {
		if (event.getSender() != this)
			return;
		Display.getDefault().asyncExec(
				new ImportExternalScoreCommand(event.getDataDomain(), event.isInDimensionDirection(), event.getType(),
						this));
	}

	private void resetAdapter() {
		this.table.setSelectedRow(null); // reset selection
		removeAllSimpleFilter();
	}

	/**
	 * @return the adapter, see {@link #adapter}
	 */
	public ITourGuideAdapter getAdapter() {
		return adapter;
	}

	@Override
	public void clearSelection() {
		table.setSelectedRow(null);
		getTableBodyUI().repaint();
	}

	@Override
	public AScoreRow getSelection() {
		return (AScoreRow) table.getSelectedRow();
	}

	@Override
	public void setSelection(AScoreRow row) {
		table.setSelectedRow(row);
	}

	private class RankTableUIConfig extends RankTableUIConfigBase {
		public RankTableUIConfig() {
			super(true, true, true);
		}

		@Override
		public boolean isSmallHeaderByDefault() {
			return true;
		}

		@Override
		public boolean canEditValues() {
			return false;
		}

		@Override
		public EButtonBarPositionMode getButtonBarPosition() {
			return EButtonBarPositionMode.UNDER_LABEL;
		}

		@Override
		public void renderIsOrderByGlyph(GLGraphics g, float w, float h, boolean orderByIt) {
			if (orderByIt) {
				g.fillImage(RenderStyle.ICON_SMALL_HEADER_OFF, w * .5f - 7, -4, 14, 14);
			}
		}

		@Override
		public void renderHeaderBackground(GLGraphics g, float w, float h, float labelHeight, ARankColumnModel model) {
			g.color(0.96f).fillRect(0, 0, w, h);
			if (labelHeight > 0)
				g.color(model.getBgColor()).fillRect(0, labelHeight - 2, w, 2);
		}

		@Override
		public void renderRowBackground(GLGraphics g, float x, float y, float w, float h, boolean even, IRow row,
				IRow selected) {
			if (row == selected) {
				g.color(TourGuideRenderStyle.colorSelectedRow());
				g.incZ();
				g.fillRect(x, y, w, h);
				if (adapter != null && adapter.isPreviewing((AScoreRow) row)) {
					TourGuideRenderStyle.COLOR_PREVIEW_BORDER_ROW.set(g.gl);
					g.drawLine(x, y, x + w, y);
					g.drawLine(x, y + h, x + w, y + h);
					TourGuideRenderStyle.COLOR_PREVIEW_BORDER_ROW.clear(g.gl);
				} else {
					g.color(RenderStyle.COLOR_SELECTED_BORDER);
					g.drawLine(x, y, x + w, y);
					g.drawLine(x, y + h, x + w, y + h);
				}
				g.decZ();
			} else if (adapter != null && adapter.isVisible((AScoreRow) row)) {
				g.color(TourGuideRenderStyle.COLOR_STRATOMEX_ROW);
				g.fillRect(x, y, w, h);
			} else if (!even) {
				g.color(RenderStyle.COLOR_BACKGROUND_EVEN);
				g.fillRect(x, y, w, h);
			}
		}

		@Override
		public Color getBarOutlineColor() {
			return Color.DARK_GRAY; // outline color
		}

		@Override
		public void onRowClick(RankTableModel table, PickingMode pickingMode, IRow row, boolean isSelected,
				IGLElementContext context) {
			if (!isSelected && pickingMode == PickingMode.CLICKED) {
				table.setSelectedRow(row);
			} else if ((isSelected && pickingMode == PickingMode.CLICKED && adapter != null && adapter
					.canShowPreviews())
					|| (pickingMode == PickingMode.DOUBLE_CLICKED && (adapter == null || !adapter.canShowPreviews()))) {
				updatePreview(null, (AScoreRow) row);
			} else if (adapter != null)
				adapter.onRowClick(table, pickingMode, (AScoreRow) row, isSelected, context);

		}

		@Override
		public boolean isFastFiltering() {
			return true;
		}
	}
}
