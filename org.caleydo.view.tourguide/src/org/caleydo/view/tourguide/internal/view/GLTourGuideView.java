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
package org.caleydo.view.tourguide.internal.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.media.opengl.GLAutoDrawable;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataSupportDefinitions;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.data.NewDataDomainEvent;
import org.caleydo.core.event.data.RemoveDataDomainEvent;
import org.caleydo.core.event.data.ReplaceTablePerspectiveEvent;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.listener.AddTablePerspectivesEvent;
import org.caleydo.core.view.listener.RemoveTablePerspectiveEvent;
import org.caleydo.core.view.opengl.canvas.GLMouseAdapter;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.canvas.IGLKeyListener;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.AGLElementView;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IPopupLayer;
import org.caleydo.core.view.opengl.layout2.basic.ScrollBar;
import org.caleydo.core.view.opengl.layout2.basic.ScrollingDecorator;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.datadomain.pathway.PathwayDataDomain;
import org.caleydo.view.stratomex.GLStratomex;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.api.score.MultiScore;
import org.caleydo.view.tourguide.internal.SerializedTourGuideView;
import org.caleydo.view.tourguide.internal.compute.ComputeAllOfJob;
import org.caleydo.view.tourguide.internal.compute.ComputeForScoreJob;
import org.caleydo.view.tourguide.internal.event.AddScoreColumnEvent;
import org.caleydo.view.tourguide.internal.event.CreateScoreEvent;
import org.caleydo.view.tourguide.internal.event.ImportExternalScoreEvent;
import org.caleydo.view.tourguide.internal.event.ScoreQueryReadyEvent;
import org.caleydo.view.tourguide.internal.external.ImportExternalScoreCommand;
import org.caleydo.view.tourguide.internal.score.ScoreFactories;
import org.caleydo.view.tourguide.internal.score.Scores;
import org.caleydo.view.tourguide.internal.view.col.IAddToStratomex;
import org.caleydo.view.tourguide.internal.view.col.PerspectiveRankColumnModel;
import org.caleydo.view.tourguide.internal.view.col.ScoreRankColumnModel;
import org.caleydo.view.tourguide.internal.view.col.SizeRankColumnModel;
import org.caleydo.view.tourguide.internal.view.model.ADataDomainQuery;
import org.caleydo.view.tourguide.internal.view.model.CategoricalDataDomainQuery;
import org.caleydo.view.tourguide.internal.view.model.CustomSubList;
import org.caleydo.view.tourguide.internal.view.model.PathwayDataDomainQuery;
import org.caleydo.view.tourguide.internal.view.model.TableDataDomainQuery;
import org.caleydo.view.tourguide.internal.view.ui.DataDomainQueryUI;
import org.caleydo.view.tourguide.internal.view.ui.pool.ScorePoolUI;
import org.caleydo.view.tourguide.spi.IScoreFactory;
import org.caleydo.view.tourguide.spi.score.IGroupScore;
import org.caleydo.view.tourguide.spi.score.IRegisteredScore;
import org.caleydo.view.tourguide.spi.score.IScore;
import org.caleydo.vis.rank.config.RankTableConfigBase;
import org.caleydo.vis.rank.config.RankTableUIConfigs;
import org.caleydo.vis.rank.layout.RowHeightLayouts;
import org.caleydo.vis.rank.model.ACompositeRankColumnModel;
import org.caleydo.vis.rank.model.ARankColumnModel;
import org.caleydo.vis.rank.model.MaxCompositeRankColumnModel;
import org.caleydo.vis.rank.model.RankRankColumnModel;
import org.caleydo.vis.rank.model.RankTableModel;
import org.caleydo.vis.rank.model.StackedRankColumnModel;
import org.caleydo.vis.rank.model.mixin.IRankableColumnMixin;
import org.caleydo.vis.rank.ui.RenderStyle;
import org.caleydo.vis.rank.ui.TableBodyUI;
import org.caleydo.vis.rank.ui.TableUI;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.google.common.collect.Iterables;
/**
 * @author Samuel Gratzl
 *
 */
public class GLTourGuideView extends AGLElementView implements IGLKeyListener, IAddToStratomex {
	private static final char TOGGLE_ALIGN_ALL = 't';

	public static final String VIEW_TYPE = "org.caleydo.view.tool.tourguide";
	public static final String VIEW_NAME = "Tour Guide";

	private static final int DATADOMAIN_QUERY = 0;
	private static final int TABLE = 1;
	private static final int POOL = 2;

	private final StratomexAdapter stratomex = new StratomexAdapter();
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
				updateMask(false);
			}
		}
	};

	private EDataDomainQueryMode mode = EDataDomainQueryMode.TABLE_BASED;

	private final GLElement waiting = new GLElement(new IGLRenderer() {
		@Override
		public void render(GLGraphics g, float w, float h, GLElement parent) {
			// g.color(1, 1, 1, 0.3f).fillRect(0, 0, w, h);
			g.fillImage("resources/loading/loading_circle.png", (w - 250) * 0.5f, (h - 250) * 0.5f, 250, 250);
		}
	});
	private boolean noStratomexVisible = false;
	private final GLElement noStratomex = new GLElement(new IGLRenderer() {
		@Override
		public void render(GLGraphics g, float w, float h, GLElement parent) {
			g.color(1, 1, 1, 0.5f).fillRect(0, 0, w, h);
			g.drawText("No active StratomeX", 10, h * 0.5f - 12, w - 20, 24, VAlign.CENTER);
		}
	});

	/**
	 * marker for the data mode
	 */
	private boolean hasAnyGroupScore;

	public GLTourGuideView(IGLCanvas glCanvas) {
		super(glCanvas, VIEW_TYPE, VIEW_NAME);

		this.table = new RankTableModel(new RankTableConfigBase() {
			@Override
			public boolean isMoveAble(ARankColumnModel model, boolean clone) {
				if (model instanceof ScoreRankColumnModel) {
					IScore s = ((ScoreRankColumnModel) model).getScore();
					if (!s.supports(mode))
						return false;
				}
				return super.isMoveAble(model, clone);
			}

			@Override
			public boolean isDestroyOnHide(ARankColumnModel model) {
				return false;
		}
		});
		this.table.addPropertyChangeListener(RankTableModel.PROP_SELECTED_ROW, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				onSelectRow((PerspectiveRow) evt.getOldValue(), (PerspectiveRow) evt.getNewValue());
			}
		});
		this.table.add(new RankRankColumnModel().setWidth(30));
		this.table.add(new PerspectiveRankColumnModel(this).setWidth(200));
		this.table.add(new SizeRankColumnModel().setWidth(75));

		addAllExternalScore(this.table);

		canvas.addKeyListener(new IGLKeyListener() {
			@Override
			public void keyPressed(IKeyEvent e) {
				if (e.isKey(ESpecialKey.DOWN))
					table.selectNextRow();
				else if (e.isKey(ESpecialKey.UP))
					table.selectPreviousRow();
				else if (e.isControlDown() && (e.isKey(TOGGLE_ALIGN_ALL))) {
					// short cut for align all
					for (StackedRankColumnModel stacked : Iterables.filter(table.getColumns(),
							StackedRankColumnModel.class)) {
						stacked.setAlignAll(!stacked.isAlignAll());
					}
				}
			}

			@Override
			public void keyReleased(IKeyEvent e) {

			}
		});

		canvas.addMouseListener(new GLMouseAdapter() {
			@Override
			public void mouseWheelMoved(IMouseEvent e) {
				onWheelMoved(e.getWheelRotation());
			}
		});

		for (EDataDomainQueryMode mode : EDataDomainQueryMode.values()) {
			for (IDataDomain dd : mode.getAllDataDomains()) {
				final ADataDomainQuery q = createFor(mode, dd);
				q.addPropertyChangeListener(ADataDomainQuery.PROP_ACTIVE, listener);
				q.addPropertyChangeListener(ADataDomainQuery.PROP_MASK, listener);
				queries.add(q);
			}
		}
	}

	/**
	 * @param table2
	 */
	private static void addAllExternalScore(RankTableModel table) {
		for (IScore score : Scores.get().getPersistentScores()) {
			ScoreRankColumnModel model = new ScoreRankColumnModel(score);
			table.add(model);
			model.hide();
		}
	}

	public void cloneFrom(GLTourGuideView view) {
		// clone table and pool
		this.table.reset();
		for (ARankColumnModel model : view.table.getColumns()) {
			ARankColumnModel clone = model.clone();
			this.table.add(clone);
		}
		for (ARankColumnModel model : view.table.getPool()) {
			model = model.clone();
			this.table.add(model);
			model.hide();
		}

		this.table.addData(view.table.getData()); // add all data
		this.table.setSelectedRow(view.table.getSelectedRow());
		this.table.setDataMask((BitSet)view.table.getDataMask().clone());
		List<?> tmp = this.table.getData();
		@SuppressWarnings("unchecked")
		List<PerspectiveRow> data = (List<PerspectiveRow>) tmp;

		for (int i = 0; i < queries.size(); ++i) {
			ADataDomainQuery q = queries.get(i);
			ADataDomainQuery clone = view.queries.get(i);
			q.cloneFrom(clone, data);
		}
		this.hasAnyGroupScore = view.hasAnyGroupScore;
		this.mode = view.mode;
		this.mask.clear();
		this.mask.or(view.mask);
	}

	private static ADataDomainQuery createFor(EDataDomainQueryMode mode, IDataDomain dd) {
		if (DataSupportDefinitions.categoricalTables.apply(dd))
			return new CategoricalDataDomainQuery(mode, (ATableBasedDataDomain) dd);
		if (dd instanceof PathwayDataDomain)
			return new PathwayDataDomainQuery(mode, (PathwayDataDomain) dd);
		return new TableDataDomainQuery(mode, (ATableBasedDataDomain) dd);
	}

	@ListenTo
	private void onAddDataDomain(final NewDataDomainEvent event) {
		IDataDomain dd = event.getDataDomain();

		for (EDataDomainQueryMode mode : EDataDomainQueryMode.values()) {
			if (mode.isCompatible(dd)) {
				ADataDomainQuery query = createFor(mode, dd);
				queries.add(query);
				getDataDomainQueryUI().add(query);
				break;
			}
		}
	}

	@ListenTo
	private void onRemoveDataDomain(final RemoveDataDomainEvent event) {
		final String id = event.getEventSpace();
		for (ADataDomainQuery query : queries) {
			if (Objects.equals(query.getDataDomain().getDataDomainID(), id)) {
				queries.remove(query);
				getDataDomainQueryUI().remove(query);
				if (query.isActive())
					updateMask(false);
				break;
			}
		}
	}

	protected void onActiveChanged(ADataDomainQuery q, boolean active) {
		if (q.getMode() != mode) {
			changeMode(q.getMode());
		}
		if (q.isInitialized()) {
			if (active) {
				scheduleAllOf(q);
			} else
				updateMask(false);
			return;
		} else
			scheduleAllOf(q);
	}

	/**
	 * @param q
	 */
	private void scheduleAllOf(final ADataDomainQuery q) {
		Collection<IScore> scores = new ArrayList<>(getVisibleScores(null));
		ComputeAllOfJob job = new ComputeAllOfJob(q, scores, this, hasAnyGroupScore);
		if (job.hasThingsToDo()) {
			getPopupLayer().show(waiting, null, 0);
			job.schedule();
		} else {
			updateMask(false);
		}
	}

	private void scheduleAllOf(Collection<IScore> toCompute) {
		if (!hasAnyGroupScore && hasAnyGroupScoreScore(toCompute))
			updateMask(true);
		ComputeForScoreJob job = new ComputeForScoreJob(toCompute, table.getData(), table.getDefaultFilter()
				.getFilter(), this);
		if (job.hasThingsToDo()) {
			getPopupLayer().show(waiting, null, 0);
			job.schedule();
		} else {
			addColumns(toCompute);
		}
	}


	@SuppressWarnings("unchecked")
	@ListenTo(sendToMe = true)
	private void onScoreQueryReady(ScoreQueryReadyEvent event) {
		getPopupLayer().hide(waiting);
		if (event.getScores() != null) {
			addColumns(event.getScores());
		} else if (event.getNewQuery() != null) {
			int offset = table.getDataSize();
			ADataDomainQuery q = event.getNewQuery();
			System.out.println("add data of " + q.getDataDomain().getLabel());
			table.addData(q.getData());
			List<?> m = table.getData();
			// use sublists to save memory
			q.init(offset, new CustomSubList<PerspectiveRow>((List<PerspectiveRow>) m, offset, m.size() - offset));
			updateMask(false);
		} else {
			updateMask(false);
		}
	}

	private void addColumns(Collection<IScore> scores) {
		for (IScore s : scores) {
			if (s instanceof MultiScore) {
				ACompositeRankColumnModel combined = table.getConfig().createNewCombined(0);
				table.add(2, combined);
				for (IScore s2 : ((MultiScore) s)) {
					combined.add(new ScoreRankColumnModel(s2));
				}
				if (combined instanceof IRankableColumnMixin)
					((IRankableColumnMixin) combined).orderByMe();
			} else {
				ScoreRankColumnModel ss = new ScoreRankColumnModel(s);
				table.add(2, ss);
				ss.orderByMe();
			}
		}
		updateMask(false);
	}

	private void updateMask(boolean forceGroupScore) {
		this.mask.clear();
		this.hasAnyGroupScore = forceGroupScore || hasAnyGroupScore();
		for (ADataDomainQuery q : this.queries) {
			if (!q.isInitialized())
				continue;
			int offset = q.getOffset();
			int size = q.getSize();
			if (!q.isActive())
				this.mask.set(offset, offset + size, false);
			else {
				this.mask.or(q.getMask(!hasAnyGroupScore));
			}
		}
		table.setDataMask(this.mask);
	}

	/**
	 * @return
	 */
	private boolean hasAnyGroupScore() {
		return hasAnyGroupScore(table.getColumns());
	}

	private static boolean hasAnyGroupScore(Iterable<ARankColumnModel> columns) {
		for (ARankColumnModel col : columns) {
			if (col instanceof ACompositeRankColumnModel) {
				if (hasAnyGroupScore((ACompositeRankColumnModel) col))
					return true;
			} else if (col instanceof ScoreRankColumnModel) {
				if (((ScoreRankColumnModel) col).getScore() instanceof IGroupScore)
					return true;
			}
		}
		return false;
	}

	private static boolean hasAnyGroupScoreScore(Iterable<IScore> scores) {
		for (IScore s : Scores.flatten(scores)) {
			if (s instanceof IGroupScore)
				return true;
		}
		return false;
	}

	private TourGuideVis getVis() {
		return (TourGuideVis) getRoot();
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		super.init(drawable);
		eventListeners.register(stratomex);
		this.canvas.addKeyListener(this);

		final PropertyChangeListener columnListener = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				switch (evt.getPropertyName()) {
				case RankTableModel.PROP_COLUMNS:
					if (evt.getNewValue() == null || evt.getOldValue() == null) { // removed or added
						updateMask(false);
					}
					break;
				case RankTableModel.PROP_POOL:
					updateMask(false);
					break;
				}

			}
		};
		this.table.addPropertyChangeListener(RankTableModel.PROP_COLUMNS, columnListener);
		this.table.addPropertyChangeListener(RankTableModel.PROP_POOL, columnListener);

		this.noStratomexVisible = stratomex.hasOne();
		updateStratomexState();
	}

	private void updateStratomexState() {
		boolean act = stratomex.hasOne();
		boolean prev = !this.noStratomexVisible;
		if (act == prev)
			return;
		if (prev)
			getPopupLayer().show(noStratomex, null, 0);
		else
			getPopupLayer().hide(noStratomex);
		this.noStratomexVisible = !this.noStratomexVisible;
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		this.stratomex.cleanUp();
		canvas.removeKeyListener(this);
		super.dispose(drawable);
	}

	@Override
	protected GLElement createRoot() {
		return new TourGuideVis();
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		stratomex.sendDelayedEvents();
		super.display(drawable);
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		return new SerializedTourGuideView();
	}

	protected void onSelectRow(PerspectiveRow old, PerspectiveRow new_) {
		stratomex.updatePreview(old, new_, getVisibleScores(new_), mode);
	}

	/**
	 * @param mode2
	 */
	private void changeMode(EDataDomainQueryMode mode) {
		// check visible columns if they support the new mode
		for (ARankColumnModel model : new ArrayList<>(table.getColumns())) {
			if (needToHide(model,mode))
				table.hide(model);
			else if (model instanceof StackedRankColumnModel) {
				StackedRankColumnModel s = (StackedRankColumnModel)model;
				for (ARankColumnModel model2 : new ArrayList<>(s.getChildren())) {
					if (needToHide(model2, mode))
						s.hide(model2);
				}
			}
		}
		for (ADataDomainQuery q : this.queries) {
			if (q.isActive() && q.getMode() != mode) {
				q.setJustActive(false);
			}
		}
		getDataDomainQueryUI().updateSelections();

		getPoolUI().updateMode(mode);

		this.mode = mode;

	}

	private DataDomainQueryUI getDataDomainQueryUI() {
		return (DataDomainQueryUI) getVis().get(DATADOMAIN_QUERY);
	}

	private ScorePoolUI getPoolUI() {
		return (ScorePoolUI) getVis().get(POOL);
	}

	/**
	 * @param model
	 * @param mode2
	 * @return
	 */
	private static boolean needToHide(ARankColumnModel model, EDataDomainQueryMode mode) {
		if ((model instanceof ScoreRankColumnModel) && !((ScoreRankColumnModel) model).getScore().supports(mode)) {
			return true;
		} else if (model instanceof MaxCompositeRankColumnModel) {
			MaxCompositeRankColumnModel max = (MaxCompositeRankColumnModel) model;
			for (ARankColumnModel model2 : max) {
				if ((model2 instanceof ScoreRankColumnModel)
						&& !((ScoreRankColumnModel) model2).getScore().supports(mode)) {
					return true;
				}
			}
		}
		return false;
	}
	/**
	 * @return
	 */
	private Collection<IScore> getVisibleScores(PerspectiveRow row) {
		Collection<IScore> r = new ArrayList<>();
		Deque<ARankColumnModel> cols = new LinkedList<>(table.getColumns());
		while (!cols.isEmpty()) {
			ARankColumnModel model = cols.pollFirst();
			if (model instanceof ScoreRankColumnModel) {
				r.add(((ScoreRankColumnModel) model).getScore());
			} else if (model instanceof StackedRankColumnModel) {
				cols.addAll(((StackedRankColumnModel) model).getChildren());
			} else if (model instanceof MaxCompositeRankColumnModel) {
				MaxCompositeRankColumnModel max = (MaxCompositeRankColumnModel) model;
				if (row != null) {
					int repr = max.getSplittedValue(row).getRepr();
					cols.add(max.get(repr));
				} else {
					cols.addAll(max.getChildren());
				}
			}
		}
		for (Iterator<IScore> it = r.iterator(); it.hasNext();) {
			if (!it.next().supports(mode))
				it.remove();
		}
		return r;
	}

	@Override
	public void keyPressed(IKeyEvent e) {
		if (e.isKey(ESpecialKey.DOWN))
			table.selectNextRow();
		else if (e.isKey(ESpecialKey.UP))
			table.selectPreviousRow();
	}

	@Override
	public void keyReleased(IKeyEvent e) {

	}

	public void attachToStratomex() {
		this.stratomex.attach();
	}

	public void detachFromStratomex() {
		this.stratomex.detach();
	}

	public void switchToStratomex(GLStratomex stratomex) {
		if (this.stratomex.setStratomex(stratomex))
			repaint();
		IPopupLayer popupLayer = getPopupLayer();
		if (popupLayer == null)
			return;
		updateStratomexState();
	}

	@Override
	public void add2Stratomex(PerspectiveRow r) {
		stratomex.addToStratomex(r, getVisibleScores(r), mode);
	}

	@Override
	public boolean canAdd2Stratomex(PerspectiveRow r) {
		return r.getPerspective() != null
				&& (!stratomex.contains(r.getPerspective()) || stratomex.isTemporaryPreviewed(r.getPerspective()));
	}

	@ListenTo
	private void onStratomexRemoveBrick(RemoveTablePerspectiveEvent event) {
		if (!stratomex.is(event.getReceiver()))
			return;
		stratomex.removeBrick(event.getTablePerspective().getID());

		PerspectiveRow selected = (PerspectiveRow) table.getSelectedRow();
		if (selected != null && selected.getPerspective() != null
				&& selected.getPerspective() == event.getTablePerspective()) {
			this.table.setSelectedRow(null);
		}

		// this.scoreQueryUI.updateAddToStratomexState();
	}

	// protected void onAddColumn() {
	// List<IScore> scores = new ArrayList<>(Scores.get().getScores());
	// Collections.sort(scores, new LabelComparator());
	// final Set<IScore> visible = getVisibleColumns();
	//
	// EDataDomainQueryMode mode = this.query.getQuery().getMode();
	//
	// ContextMenuCreator creator = view.getContextMenuCreator();
	// ScoreFactories.addCreateItems(creator, this, mode);
	// creator.addSeparator();
	// MetricFactories.addCreateItems(creator, visible, this, mode);
	// creator.addSeparator();
	//
	// for (IScore s : scores) {
	// if (visible.contains(s) || !s.supports(mode))
	// continue;
	// creator.addContextMenuItem(new GenericContextMenuItem("Add " + s.getAbbreviation() + " " + s.getLabel(),
	// new AddScoreColumnEvent(s).to(this)));
	// }
	// }

	/**
	 * @param oldValue
	 */
	protected void destroy(ARankColumnModel model) {
		if (model instanceof ScoreRankColumnModel) {
			Scores.get().remove(((ScoreRankColumnModel) model).getScore());
		}
	}

	@ListenTo(sendToMe = true)
	private void onAddColumn(AddScoreColumnEvent event) {
		Collection<IScore> toCompute = new ArrayList<>();
		Scores scores = Scores.get();
		for (IScore s : event.getScores()) {
			if (!s.supports(this.mode))
				continue;
			if (s instanceof IRegisteredScore)
				s = scores.addIfAbsent((IRegisteredScore) s);
			if (s instanceof MultiScore) {
				MultiScore sm = (MultiScore) s;
				MultiScore tmp = new MultiScore(sm.getLabel(), sm.getColor(), sm.getBGColor());
				for (IScore s2 : ((MultiScore) s)) {
					if (s2 instanceof IRegisteredScore)
						s2 = scores.addIfAbsent((IRegisteredScore) s2);
					tmp.add(s2);
				}
				toCompute.add(tmp);
			} else
				toCompute.add(s);
		}
		scheduleAllOf(toCompute);
	}


	@ListenTo(sendToMe = true)
	private void onCreateScore(final CreateScoreEvent event) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				IScoreFactory f = ScoreFactories.get(event.getScore());
				f.createCreateDialog(new Shell(), GLTourGuideView.this).open();
			}
		});
	}

	// protected void onModeChanged(EDataDomainQueryMode mode) {
	// //remove all not valid orders
	// for (IScore s : Lists.newArrayList(orderBy.keySet()))
	// if (!s.supports(mode))
	// sortBy(s, ESorting.NONE);
	//
	// //remove all not valid scores
	// for(IScore s : Lists.newArrayList(selection))
	// if (!s.supports(mode))
	// removeSelection(s);
	//

	@ListenTo
	private void onImportExternalScore(ImportExternalScoreEvent event) {
		if (event.getSender() != this)
			return;
		Display.getDefault().asyncExec(
				new ImportExternalScoreCommand(event.getDataDomain(), event.isInDimensionDirection(), event.getType(),
						this));
	}

	@ListenTo
	private void onStratomexAddBricks(AddTablePerspectivesEvent event) {
		if (!stratomex.is(event.getReceiver()))
			return;
		stratomex.addBricks(event.getTablePerspectives());
		// TODO correctly repaint
		// this.scoreQueryUI.updateAddToStratomexState();
	}

	@ListenTo
	private void onStratomexReplaceBricks(ReplaceTablePerspectiveEvent event) {
		if (!stratomex.is(event.getViewID()))
			return;
		stratomex.replaceBricks(event.getOldPerspective(), event.getNewPerspective());
	}

	private class TourGuideVis extends GLElementContainer {
		public TourGuideVis() {
			setLayout(GLLayouts.flowVertical(10));
			this.add(new DataDomainQueryUI(queries));
			TableUI tableui = new TableUI(table, RankTableUIConfigs.DEFAULT, RowHeightLayouts.UNIFORM);
			ScrollingDecorator sc = new ScrollingDecorator(tableui, new ScrollBar(true), null,
					RenderStyle.SCROLLBAR_WIDTH);
			this.add(sc);
			this.add(new ScorePoolUI(table, RankTableUIConfigs.DEFAULT, GLTourGuideView.this));
		}
	}

	/**
	 * @param wheelRotation
	 */
	protected void onWheelMoved(int wheelRotation) {
		if (wheelRotation == 0)
			return;
		TableBodyUI body = findBody();
		if (body != null)
			body.scroll(-wheelRotation);
	}

	/**
	 * @return
	 */
	private TableBodyUI findBody() {
		GLElement root = getRoot();
		if (root == null)
			return null;
		TourGuideVis r = (TourGuideVis) root;
		return ((TableUI) ((ScrollingDecorator) r.get(TABLE)).getContent()).getBody();
	}
}

