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

import static org.caleydo.core.view.opengl.layout.ElementLayouts.createXSeparator;
import static org.caleydo.core.view.opengl.layout.ElementLayouts.createXSpacer;
import static org.caleydo.view.tourguide.renderstyle.TourGuideRenderStyle.SELECTED_COLOR;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.caleydo.core.util.base.DefaultLabelProvider;
import org.caleydo.core.util.color.Colors;
import org.caleydo.core.view.contextmenu.AContextMenuItem.EContextMenuType;
import org.caleydo.core.view.contextmenu.ContextMenuCreator;
import org.caleydo.core.view.contextmenu.GenericContextMenuItem;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.PickingRenderer;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.tourguide.data.ESorting;
import org.caleydo.view.tourguide.data.ScoreQuery;
import org.caleydo.view.tourguide.data.Scores;
import org.caleydo.view.tourguide.data.ScoringElement;
import org.caleydo.view.tourguide.data.filter.CompareScoreFilter;
import org.caleydo.view.tourguide.data.filter.ECompareOperator;
import org.caleydo.view.tourguide.data.filter.IScoreFilter;
import org.caleydo.view.tourguide.data.score.CollapseScore;
import org.caleydo.view.tourguide.data.score.IScore;
import org.caleydo.view.tourguide.data.score.ScoreRegistry;
import org.caleydo.view.tourguide.event.AddScoreColumnEvent;
import org.caleydo.view.tourguide.event.RemoveScoreColumnEvent;
import org.caleydo.view.tourguide.event.RenameScoreColumnEvent;
import org.caleydo.view.tourguide.event.ToggleNaNFilterScoreColumnEvent;
import org.caleydo.view.tourguide.util.LabelComparator;
import org.caleydo.view.tourguide.vendingmachine.col.ATableColumn;
import org.caleydo.view.tourguide.vendingmachine.col.AddQueryColumn;
import org.caleydo.view.tourguide.vendingmachine.col.AddToStratomexColumn;
import org.caleydo.view.tourguide.vendingmachine.col.MatchColumn;
import org.caleydo.view.tourguide.vendingmachine.col.QueryColumn;
import org.caleydo.view.tourguide.vendingmachine.col.RankColumn;
import org.caleydo.view.tourguide.vendingmachine.ui.ScoreFilterDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

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

	public ScoreQueryUI(AGLView view, StratomexAdapter stratomex) {
		this.view = view;
		this.stratomex = stratomex;
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
		this.columns.add(new AddToStratomexColumn(view, stratomex));
		this.columns.add(new MatchColumn(view));
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
		this.add(colSpace);
		this.add(columns.get(2));
		for (QueryColumn col : this.queryColumns) {
			final ElementLayout s = createXSeparator(5);
			s.setGrabY(true);
			this.add(s).add(col);
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
				List<LayoutRenderer> renderers = l.getBackgroundRenderer();
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
				List<LayoutRenderer> renderers = l.getBackgroundRenderer();
				if (renderers.isEmpty() || !(renderers.get(0) instanceof PickingRenderer))
					continue;
				((PickingRenderer) renderers.get(0)).setColor(SELECTED_COLOR);
			}
			layoutManager.setRenderingDirty();
		}
		invalidate();
		stratomex.updatePreview(old, new_, getVisibleColumns(new_));
	}

	public ScoreQuery getQuery() {
		return query;
	}

	public List<ScoringElement> getData() {
		return Collections.unmodifiableList(data);
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
			public void rightClicked(Pick pick) {
				onShowColumnMenu(queryColumns.get(pick.getObjectID()));
			}
		}, QueryColumn.SORT_COLUMN);
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
		}, EDIT_FILTER);
		view.addTypePickingTooltipListener("Edit Score Filters", EDIT_FILTER);
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
			creator.addContextMenuItem(new GenericContextMenuItem("Rename", new RenameScoreColumnEvent(
					(DefaultLabelProvider) column.getScore(), this)));
			creator.addSeparator();
		}
		creator.addContextMenuItem(new GenericContextMenuItem("Remove", new RemoveScoreColumnEvent(column.getScore(),
				false, this)));
		creator.addContextMenuItem(new GenericContextMenuItem("Remove And Forget", new RemoveScoreColumnEvent(column
				.getScore(), true, this)));
		creator.addSeparator();

		{
			boolean hasNanFilter = hasNaNFilter(column.getScore());
			creator.addContextMenuItem(new GenericContextMenuItem("Enable NaN Filter", EContextMenuType.CHECK,
					new ToggleNaNFilterScoreColumnEvent(column.getScore(), this)).setState(hasNanFilter));
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
		List<IScore> scores = new ArrayList<>(Scores.get().getScoreIDs());
		Collections.sort(scores, new LabelComparator());
		final Set<IScore> visible = getVisibleColumns();

		ContextMenuCreator creator = view.getContextMenuCreator();
		ScoreRegistry.addCreateScoreItems(creator, visible, this);
		creator.addSeparator();
		if (scores.size() >= 2 || this.queryColumns.size() >= 2) {
			ScoreRegistry.addCreateCombinedItems(creator, visible, this);
		}
		creator.addSeparator();

		ScoreRegistry.addCreateMetricItems(creator, visible, this);
		creator.addSeparator();

		for (IScore s : scores) {
			if (visible.contains(s))
				continue;
			creator.addContextMenuItem(new GenericContextMenuItem("Add " + s.getLabel(), new AddScoreColumnEvent(s,
					this)));
		}
	}

	private Set<IScore> getVisibleColumns() {
		Set<IScore> visible = new HashSet<>();
		for (QueryColumn c : this.queryColumns)
			visible.add(c.getScore());
		return visible;
	}

	protected void onAddToStratomex(int row) {
		stratomex.addToStratomex(data.get(row));
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

interface ISelectionListener {
	public void onSelectionChanged(ScoringElement old_, ScoringElement new_, IScore selectedColumn,
			Collection<IScore> visibleColumns);
}
