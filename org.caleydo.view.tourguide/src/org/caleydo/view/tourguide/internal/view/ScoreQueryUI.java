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

import static org.caleydo.core.view.opengl.layout.ElementLayouts.createXSpacer;
import static org.caleydo.view.tourguide.internal.TourGuideRenderStyle.SELECTED_COLOR;

import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.gui.util.RenameNameDialog;
import org.caleydo.core.util.base.DefaultLabelProvider;
import org.caleydo.core.util.base.ILabelHolder;
import org.caleydo.core.util.color.Colors;
import org.caleydo.core.view.contextmenu.AContextMenuItem.EContextMenuType;
import org.caleydo.core.view.contextmenu.ContextMenuCreator;
import org.caleydo.core.view.contextmenu.GenericContextMenuItem;
import org.caleydo.core.view.contextmenu.GroupContextMenuItem;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.PickingRenderer;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.core.view.opengl.picking.SpacePickingManager.OnPick;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.api.query.ESorting;
import org.caleydo.view.tourguide.api.query.ScoreQuery;
import org.caleydo.view.tourguide.api.query.ScoringElement;
import org.caleydo.view.tourguide.api.query.filter.CompareScoreFilter;
import org.caleydo.view.tourguide.api.query.filter.ECompareOperator;
import org.caleydo.view.tourguide.api.score.CollapseScore;
import org.caleydo.view.tourguide.api.score.CombinedScore;
import org.caleydo.view.tourguide.api.score.ECombinedOperator;
import org.caleydo.view.tourguide.api.util.LabelComparator;
import org.caleydo.view.tourguide.internal.event.AddScoreColumnEvent;
import org.caleydo.view.tourguide.internal.event.ChangeCombinedScoreOperatorEvent;
import org.caleydo.view.tourguide.internal.event.CreateScoreEvent;
import org.caleydo.view.tourguide.internal.event.RemoveScoreColumnEvent;
import org.caleydo.view.tourguide.internal.event.RenameScoreColumnEvent;
import org.caleydo.view.tourguide.internal.event.ToggleNaNFilterScoreColumnEvent;
import org.caleydo.view.tourguide.internal.score.MetricFactories;
import org.caleydo.view.tourguide.internal.score.ScoreFactories;
import org.caleydo.view.tourguide.internal.score.Scores;
import org.caleydo.view.tourguide.internal.view.col.ATableColumn;
import org.caleydo.view.tourguide.internal.view.col.AddQueryColumn;
import org.caleydo.view.tourguide.internal.view.col.MatchColumn;
import org.caleydo.view.tourguide.internal.view.col.QueryColumn;
import org.caleydo.view.tourguide.internal.view.col.RankColumn;
import org.caleydo.view.tourguide.internal.view.col.Separator;
import org.caleydo.view.tourguide.internal.view.ui.ScoreFilterDialog;
import org.caleydo.view.tourguide.spi.IScoreFactory;
import org.caleydo.view.tourguide.spi.query.filter.IScoreFilter;
import org.caleydo.view.tourguide.spi.score.IScore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * @author Samuel Gratzl
 *
 */
public class ScoreQueryUI extends Row {
	public static final String SELECT_ROW = "SELECT_ROW";
	public static final String ADD_TO_STRATOMEX = "ADD_TO_STATOMEX";
	public static final String ADD_COLUMN = "ADD_COLUMN";
	public static final String EDIT_FILTER = "EDIT_FILTER";
	public static final String DROP_SEPARATOR = "DROP_SEPARATOR";

	private final List<QueryColumn> queryColumns = new ArrayList<>();
	private final List<ATableColumn> columns = new ArrayList<>();

	private int selectedRow = -1;
	private List<ScoringElement> data = Collections.emptyList();

	private ScoreQuery query;
	private final PropertyChangeListener selectionChanged = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			onSelectionChanged(evt);
		}
	};
	private final PropertyChangeListener orderByChanged = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			onOrderByChanged(evt);
		}
	};
	private final PropertyChangeListener filterChanged = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			onFilterChanged(evt);
		}
	};

	private final AGLView view;
	private final StratomexAdapter stratomex;
	private final DragAndDropController dndController;

	public ScoreQueryUI(AGLView view, StratomexAdapter stratomex, DragAndDropController dndController) {
		this.view = view;
		this.stratomex = stratomex;
		this.dndController = dndController;
		init();
		initListeners(view);

	}

	private void init() {
		this.setLeftToRight(true);
		setXDynamic(true);
		setYDynamic(false);
	}

	public void setQuery(ScoreQuery query) {
		if (this.query != null) {
			this.query.removePropertyChangeListener(ScoreQuery.PROP_SELECTION, selectionChanged);
			this.query.removePropertyChangeListener(ScoreQuery.PROP_ORDER_BY, orderByChanged);
			this.query.removePropertyChangeListener(ScoreQuery.PROP_FILTER, filterChanged);
		}
		this.query = query;
		this.query.addPropertyChangeListener(ScoreQuery.PROP_SELECTION, selectionChanged);
		this.query.addPropertyChangeListener(ScoreQuery.PROP_ORDER_BY, orderByChanged);
		this.query.addPropertyChangeListener(ScoreQuery.PROP_FILTER, filterChanged);
		// initial
		createColumns(query);
	}

	private void createColumns(ScoreQuery query) {
		this.clear();
		this.columns.clear();
		this.columns.add(new RankColumn(view));
		this.columns.add(new MatchColumn(view, stratomex));
		int i = 0;
		this.queryColumns.clear();
		for (IScore column : query.getSelection()) {
			QueryColumn col = QueryColumn.create(column, i++, query.getSorting(column), view);
			this.columns.add(col);
			this.queryColumns.add(col);
		}
		this.columns.add(new AddQueryColumn(view));

		final ElementLayout colSpace = createXSpacer(1);
		this.add(colSpace);
		this.add(columns.get(0));
		this.add(colSpace);
		this.add(columns.get(1));
		for (QueryColumn col : this.queryColumns) {
			this.add(new Separator(size(), view, this)).add(col);
		}
		this.add(columns.get(columns.size() - 1));

		invalidate();
		this.setPixelSizeY(columns.get(1).getPixelSizeY());
	}

	public void setData(List<ScoringElement> data) {
		setSelected(-1);
		this.data = data;
		for (ATableColumn col : this.columns)
			col.setData(data, query);
		this.setPixelSizeY(columns.get(1).getPixelSizeY());
		invalidate();
	}

	private void invalidate() {
		if (layoutManager != null) {
			layoutManager.updateLayout();
			updateSubLayout();
		}
	}

	public void setSelected(int row) {
		if (selectedRow == row)
			return;
		if (row < -1 || row >= data.size())
			return;
		ScoringElement old = null;
		if (selectedRow != -1) {
			old = data.get(selectedRow);
			for (ATableColumn tcol : this.columns) {
				ElementLayout l = tcol.getTd(selectedRow);
				if (l == null)
					continue;
				List<ALayoutRenderer> renderers = l.getBackgroundRenderer();
				if (renderers.isEmpty() || !(renderers.get(0) instanceof PickingRenderer))
					continue;
				((PickingRenderer) renderers.get(0)).setColor(Colors.TRANSPARENT);
			}
			layoutManager.setRenderingDirty();
		}
		selectedRow = row;
		ScoringElement new_ = null;
		if (selectedRow != -1) {
			new_ = data.get(selectedRow);
			for (ATableColumn tcol : this.columns) {
				ElementLayout l = tcol.getTd(selectedRow);
				if (l == null)
					continue;
				List<ALayoutRenderer> renderers = l.getBackgroundRenderer();
				if (renderers.isEmpty() || !(renderers.get(0) instanceof PickingRenderer))
					continue;
				((PickingRenderer) renderers.get(0)).setColor(SELECTED_COLOR);
			}
			layoutManager.setRenderingDirty();
		}
		invalidate();
		stratomex.updatePreview(old, new_, getVisibleColumns(new_), query.getMode());
	}

	public void updateAddToStratomexState() {
		MatchColumn col = (MatchColumn) columns.get(1);
		col.updateState(this.data);
	}

	public ScoreQuery getQuery() {
		return query;
	}

	public ScoringElement getSelected() {
		if (selectedRow < 0)
			return null;
		return data.get(selectedRow);
	}

	private Collection<IScore> getVisibleColumns(ScoringElement row) {
		if (row == null)
			return null;
		Collection<IScore> r = new ArrayList<>();
		for (QueryColumn column : this.queryColumns) {
			IScore s = column.getScore();
			if (s instanceof CollapseScore)
				s = row.getSelected((CollapseScore) s);
			r.add(s);
		}
		return r;
	}

	// ############## EVENT HANDLING

	private void initListeners(AGLView view) {
		view.addTypePickingListener(new APickingListener() {
			@Override
			public void clicked(Pick pick) {
				onSortBy(queryColumns.get(pick.getObjectID()));
			}

			@Override
			public void dragged(Pick pick) {
				if (!dndController.hasDraggables())
					onDragStart(queryColumns.get(pick.getObjectID()), pick.getDragStartPoint());
			}

			@Override
			public void rightClicked(Pick pick) {
				onShowColumnMenu(queryColumns.get(pick.getObjectID()));
			}

			@Override
			public void doubleClicked(Pick pick) {
				onToggleCollapse(queryColumns.get(pick.getObjectID()));
			}
		}, QueryColumn.CLICK_COLUMN_HEADER);
		view.addTypePickingListener(new APickingListener() {
			@Override
			public void clicked(Pick pick) {
				onAddColumn();
			}
		}, ADD_COLUMN);
		view.addTypePickingTooltipListener("Add another column", ADD_COLUMN);

		view.addTypePickingListener(new APickingListener() {
			@Override
			public void clicked(Pick pick) {
				setSelected(pick.getObjectID());
			}
		}, SELECT_ROW);
		view.addTypePickingListener(new APickingListener() {
			@Override
			public void clicked(Pick pick) {
				onAddToStratomex(pick.getObjectID());
			}
		}, ADD_TO_STRATOMEX);
		view.addTypePickingTooltipListener("Add this row to StratomeX", ADD_TO_STRATOMEX);
		view.addTypePickingListener(new APickingListener() {
			@Override
			public void clicked(Pick pick) {
				onEditFilter();
			}

			@Override
			public void pick(Pick pick) {
				super.pick(pick);
			}
		}, EDIT_FILTER);
		view.addTypePickingTooltipListener("Edit Score Filters", EDIT_FILTER);

		view.addTypePickingListener(new APickingListener() {

			@Override
			public void dragged(Pick pick) {
				onSeparatorDragged(pick);
			}

			@Override
			public void mouseOut(Pick pick) {
				dndController.setDropArea(null);
			}
		}, DROP_SEPARATOR);
	}

	@OnPick(value = EDIT_FILTER, mode = PickingMode.CLICKED)
	private void onEditFilter2() {

	}

	/**
	 * @param pick
	 */
	protected void onSeparatorDragged(Pick pick) {
		if (dndController.isDragging())
			dndController.setDropArea((Separator) get(pick.getObjectID()));
		else {
			// ATableColumn column = (ATableColumn) get(pick.getObjectID() - 1);
			// dndController.setDraggables(Sets.newHashSet(column.asResize()));
			// dndController.setDraggingStartPosition(pick.getDragStartPoint());
		}
	}
	protected void onToggleCollapse(QueryColumn column) {
		column.toggleCollapse();
	}

	/**
	 * @param queryColumn
	 */
	protected void onDragStart(QueryColumn queryColumn, Point startPosition) {
		dndController.clearDraggables();
		dndController.addDraggable(queryColumn);
		dndController.setDraggingStartPosition(startPosition);
	}

	protected void onSortBy(QueryColumn columnHeader) {
		if (query == null)
			return;
		query.sortBy(columnHeader.getScore(), columnHeader.nextSorting());
	}

	protected void onSelectionChanged(PropertyChangeEvent evt) {
		createColumns(this.query);
		if (this.data != null)
			setData(data);
	}

	protected void onOrderByChanged(PropertyChangeEvent evt) {
		for (QueryColumn col : queryColumns) {
			ESorting s = query.getSorting(col.getScore());
			if (s != null)
				col.setSort(s);
		}
	}

	protected void onFilterChanged(PropertyChangeEvent evt) {
		Set<IScore> enabled = Sets.newHashSet();
		for (IScoreFilter f : query.getFilter())
			enabled.add(f.getReference());
		for (QueryColumn col : queryColumns) {
			col.setHasFilter(enabled.contains(col.getScore()));
		}
	}

	protected void onShowColumnMenu(QueryColumn column) {
		ContextMenuCreator creator = view.getContextMenuCreator();
		if (column.getScore() instanceof DefaultLabelProvider) {
			creator.add("Rename", new RenameScoreColumnEvent((DefaultLabelProvider) column.getScore()).to(this));
			creator.addSeparator();
		}
		creator.add("Hide", new RemoveScoreColumnEvent(column.getScore(), false).to(this));
		creator.add("Hide And Remove", new RemoveScoreColumnEvent(column.getScore(), true).to(this));
		creator.addSeparator();

		{
			boolean hasNanFilter = hasNaNFilter(column.getScore());
			creator.addContextMenuItem(new GenericContextMenuItem("Enable NaN Filter", EContextMenuType.CHECK,
					new ToggleNaNFilterScoreColumnEvent(column.getScore()).to(this)).setState(hasNanFilter));
		}
		if (column.getScore() instanceof CombinedScore) {
			CombinedScore s = (CombinedScore) column.getScore();
			GroupContextMenuItem group = new GroupContextMenuItem("Operator");
			creator.addContextMenuItem(group);
			ECombinedOperator act = (s).getOperator();
			for (ECombinedOperator op : ECombinedOperator.values()) {
				group.add(new GenericContextMenuItem(op.getLabel(), EContextMenuType.CHECK,
						new ChangeCombinedScoreOperatorEvent(s, op).to(this)).setState(op == act));
			}
		}
		// creator.addContextMenuItem(new GenericContextMenuItem("Edit Filter", new ))
	}

	private boolean hasNaNFilter(IScore score) {
		for (IScoreFilter s : query.getFilter()) {
			if (!(s instanceof CompareScoreFilter))
				continue;
			CompareScoreFilter cs = ((CompareScoreFilter) s);
			if (cs.getReference() == score && cs.getOp() == ECompareOperator.IS_NOT_NA)
				return true;
		}
		return false;
	}

	protected void onAddColumn() {
		List<IScore> scores = new ArrayList<>(Scores.get().getScores());
		Collections.sort(scores, new LabelComparator());
		final Set<IScore> visible = getVisibleColumns();

		EDataDomainQueryMode mode = this.query.getQuery().getMode();

		ContextMenuCreator creator = view.getContextMenuCreator();
		ScoreFactories.addCreateItems(creator, this, mode);
		creator.addSeparator();
		MetricFactories.addCreateItems(creator, visible, this, mode);
		creator.addSeparator();

		for (IScore s : scores) {
			if (visible.contains(s) || !s.supports(mode))
				continue;
			creator.addContextMenuItem(new GenericContextMenuItem("Add " + s.getAbbreviation() + " " + s.getLabel(),
					new AddScoreColumnEvent(s).to(this)));
		}
	}

	@ListenTo(sendToMe = true)
	void onAddColumn(AddScoreColumnEvent event) {
		query.addSelection(Lists.newArrayList(Iterables.transform(event.getScores(),
				Scores.get().registerScores)));
	}

	@ListenTo(sendToMe = true)
	void onRemoveColumn(RemoveScoreColumnEvent event) {
		IScore score = event.getScore();
		query.sortBy(score, ESorting.NONE);
		query.removeSelection(score);
		if (event.isRemove()) {
			Scores.get().remove(score);
		}
	}

	public void moveColumn(QueryColumn column, int id) {
		ATableColumn after = (ATableColumn) get(id + 1);
		if (after == column || !(after instanceof QueryColumn))
			return;
		query.moveSelection(column.getScore(), ((QueryColumn) after).getScore());
	}

	@ListenTo(sendToMe = true)
	void onRename(RenameScoreColumnEvent event) {
		final ILabelHolder l = event.getColumn();
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				String r = RenameNameDialog.show(view.getParentComposite().getShell(), "Rename '" + l.getLabel()
						+ "' to", l.getLabel());
				if (r != null) {
					l.setLabel(r);
					view.setDisplayListDirty();
				}
			}
		});
	}

	@ListenTo(sendToMe = true)
	void onToggleNaNFilter(ToggleNaNFilterScoreColumnEvent event) {
		IScore score = event.getScore();
		for (IScoreFilter s : query.getFilter()) {
			if (!(s instanceof CompareScoreFilter))
				continue;
			CompareScoreFilter cs = ((CompareScoreFilter) s);
			if (cs.getReference() == score && cs.getOp() == ECompareOperator.IS_NOT_NA) {
				// remove the filter
				query.removeFilter(s);
				return;
			}
		}
		// wasn't there add the filter
		query.addFilter(new CompareScoreFilter(score, ECompareOperator.IS_NOT_NA, 0.5f));
	}

	@ListenTo(sendToMe = true)
	void onChangeCombinedScore(ChangeCombinedScoreOperatorEvent event) {
		CombinedScore score = event.getScore();
		CombinedScore new_ = new CombinedScore(score.getLabel(), event.getOp(), score.getTransformedChildren());
		query.replaceSelection(score, new_);
	}

	@ListenTo(sendToMe = true)
	void onCreateScore(final CreateScoreEvent event) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				IScoreFactory f = ScoreFactories.get(event.getScore());
				f.createCreateDialog(new Shell(), ScoreQueryUI.this).open();
			}
		});
	}

	private Set<IScore> getVisibleColumns() {
		Set<IScore> visible = new HashSet<>();
		for (QueryColumn c : this.queryColumns)
			visible.add(c.getScore());
		return visible;
	}

	protected void onAddToStratomex(int row) {
		stratomex.addToStratomex(data.get(row), getVisibleColumns(data.get(row)), query.getMode());
		updateAddToStratomexState();
	}

	protected void onEditFilter() {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				new ScoreFilterDialog(new Shell(), getVisibleColumns(), ScoreQueryUI.this).open();
			}
		});
	}


	public void selectNext() {
		setSelected(selectedRow + 1);
	}

	public void selectPrevious() {
		setSelected(selectedRow - 1);
	}
}