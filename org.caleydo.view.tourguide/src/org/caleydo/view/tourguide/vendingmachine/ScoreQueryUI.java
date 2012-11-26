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

import static org.caleydo.core.view.opengl.layout.ElementLayouts.createButton;
import static org.caleydo.core.view.opengl.layout.ElementLayouts.createColor;
import static org.caleydo.core.view.opengl.layout.ElementLayouts.createLabel;
import static org.caleydo.core.view.opengl.layout.ElementLayouts.createXSpacer;
import static org.caleydo.core.view.opengl.layout.ElementLayouts.createYSeparator;
import static org.caleydo.core.view.opengl.layout.ElementLayouts.createYSpacer;
import static org.caleydo.core.view.opengl.layout.ElementLayouts.wrap;
import static org.caleydo.core.view.opengl.layout.util.Renderers.createPickingRenderer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.util.base.ConstantLabelProvider;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.Colors;
import org.caleydo.core.util.color.IColor;
import org.caleydo.core.util.format.Formatter;
import org.caleydo.core.view.contextmenu.ContextMenuCreator;
import org.caleydo.core.view.contextmenu.GenericContextMenuItem;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.ColorRenderer;
import org.caleydo.core.view.opengl.layout.util.TextureRenderer;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.util.button.Button;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.view.tourguide.data.ESorting;
import org.caleydo.view.tourguide.data.ScoreQuery;
import org.caleydo.view.tourguide.data.Scores;
import org.caleydo.view.tourguide.data.ScoringElement;
import org.caleydo.view.tourguide.data.score.AGroupScore;
import org.caleydo.view.tourguide.data.score.AStratificationScore;
import org.caleydo.view.tourguide.data.score.EScoreType;
import org.caleydo.view.tourguide.data.score.IScore;
import org.caleydo.view.tourguide.data.score.ProductScore;
import org.caleydo.view.tourguide.event.AddScoreColumnEvent;
import org.caleydo.view.tourguide.event.RemoveScoreColumnEvent;
import org.caleydo.view.tourguide.renderer.ScoreBarRenderer;

import com.google.common.base.Function;


/**
 * @author Samuel Gratzl
 *
 */
public class ScoreQueryUI extends Column {
	private static final String SORT_COLUMN = "SORT_COLUMN";
	private static final String SELECT_ROW = "SELECT_ROW";
	private static final String SELECT_ROW_COLUMN = "SELECT_ROW_COLUMN";
	private static final String ADD_TO_STRATOMEX = "ADD_TO_STATOMEX";
	private static final String ADD_COLUMN = "ADD_COLUMN";

	private static final IColor SELECTED_COLOR = Colors.YELLOW;

	private static final int COL0_RANK_WIDTH = 20;

	private static final int COLX_SCORE_WIDTH = 75;
	private static final int COL2_ADD_COLUMN_X_WIDTH = 16;

	private static final int ROW_HEIGHT = 18;

	private final List<SortableColumnHeader> columns = new ArrayList<>();
	private Row headerRow;

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

	private final ISelectionListener selectionListener;
	private final AGLView view;
	private final Function<ScoringElement, Void> addToStratomexCallback;

	public ScoreQueryUI(AGLView view, ISelectionListener listener, Function<ScoringElement, Void> addToStratomex) {
		this.view = view;
		this.selectionListener = listener;
		this.addToStratomexCallback = addToStratomex;
		init();

		view.addTypePickingListener(new APickingListener() {
			@Override
			public void clicked(Pick pick) {
				onSortBy(columns.get(pick.getObjectID()));
			}

			@Override
			public void rightClicked(Pick pick) {
				onShowColumnMenu(columns.get(pick.getObjectID()));
			}
		}, SORT_COLUMN);
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
				setSelected(pick.getObjectID(), -1);
			}

			@Override
			public void rightClicked(Pick pick) {
				onAddToStratomex(pick.getObjectID());
			}
		}, SELECT_ROW);
		view.addTypePickingTooltipListener("click to highlight, right-click to add to Stratomex", SELECT_ROW);
		view.addTypePickingListener(new APickingListener() {
			@Override
			public void clicked(Pick pick) {
				int id = pick.getObjectID();
				setSelected(id >> 8, id & 0xFF);
			}
		}, SELECT_ROW_COLUMN);
		view.addTypePickingListener(new APickingListener() {
			@Override
			public void clicked(Pick pick) {
				onAddToStratomex(pick.getObjectID());
			}
		}, ADD_TO_STRATOMEX);
		view.addTypePickingTooltipListener("Add this row to StratomeX", ADD_TO_STRATOMEX);

	}


	private void init() {
		this.setBottomUp(false);
		setGrabX(true);
		setGrabY(true);
		this.headerRow = new Row();
		this.add(createYSpacer(5));
		this.add(headerRow);
		this.add(createYSeparator(9));
	}

	public void setQuery(ScoreQuery query) {
		if (this.query != null) {
			this.query.removePropertyChangeListener(ScoreQuery.PROP_SELECTION, selectionChanged);
			this.query.removePropertyChangeListener(ScoreQuery.PROP_ORDER_BY, orderByChanged);
		}
		this.query = query;
		this.query.addPropertyChangeListener(ScoreQuery.PROP_SELECTION, selectionChanged);
		this.query.addPropertyChangeListener(ScoreQuery.PROP_ORDER_BY, orderByChanged);
		// initial
		createColumns(query);
	}

	private void createColumns(ScoreQuery query) {
		this.headerRow.clear();
		this.columns.clear();
		this.headerRow.setPixelSizeY(ROW_HEIGHT);
		this.headerRow.add(createXSpacer(3));
		this.headerRow.add(createXSpacer(COL0_RANK_WIDTH));
		this.headerRow.add(createXSpacer(16));
		this.headerRow.add(ReferenceElements.create(Colors.TRANSPARENT, new ConstantLabelProvider("Stratification"),
				new ConstantLabelProvider("Group"), this.view));
		this.headerRow.add(createXSpacer(3));
		int i = 0;
		for (IScore column : query.getSelection()) {
			SortableColumnHeader col = new SortableColumnHeader(column, i++, query.getSorting(column));
			this.headerRow.add(col).add(createXSpacer(3));
			this.columns.add(col);
		}
		headerRow.add(createButton(view, new Button(ADD_COLUMN, 1, EIconTextures.GROUPER_COLLAPSE_PLUS)));
		invalidate();
	}

	public ScoreQuery getQuery() {
		return query;
	}

	protected void onSelectionChanged(PropertyChangeEvent evt) {
		createColumns(this.query);
		if (this.data != null)
			setData(data);
	}

	protected void onOrderByChanged(PropertyChangeEvent evt) {
		for (SortableColumnHeader col : columns) {
			ESorting s = query.getSorting(col.getScoreID());
			if (s != null)
				col.setSort(s);
		}
	}

	protected void onAddColumn() {
		Collection<IScore> scores = Scores.get().getScoreIDs();
		if (scores.isEmpty())
			return;
		ContextMenuCreator creator = view.getContextMenuCreator();
		if (scores.size() >= 2)
			creator.addContextMenuItem(new GenericContextMenuItem("Create Combined Score",
					new AddScoreColumnEvent(this)));

		Set<IScore> visible = new HashSet<>();
		for (SortableColumnHeader c : this.columns)
			visible.add(c.getScoreID());

		for (IScore s : scores) {
			if (visible.contains(s))
				continue;
			creator.addContextMenuItem(new GenericContextMenuItem("Add " + s.getLabel(), new AddScoreColumnEvent(s,
					this)));
		}
	}

	protected void onShowColumnMenu(SortableColumnHeader sortableColumnHeader) {
		ContextMenuCreator creator = view.getContextMenuCreator();
		creator.addContextMenuItem(new GenericContextMenuItem("Remove", new RemoveScoreColumnEvent(sortableColumnHeader
				.getScoreID(), this)));
	}

	protected void onAddToStratomex(int row) {
		if (this.selectedRow == row)
			setSelected(-1, -1);
		addToStratomexCallback.apply(data.get(row));
	}

	public Collection<IScore> getColumns() {

		Collection<IScore> cols = new ArrayList<>(columns.size());
		for (SortableColumnHeader col : columns)
			cols.add(col.getScoreID());
		return cols;
	}

	public List<ScoringElement> getData() {
		return Collections.unmodifiableList(data);
	}

	public ScoringElement getSelected() {
		if (selectedRow < 0)
			return null;
		return data.get(selectedRow);
	}

	public void setData(List<ScoringElement> data) {
		this.data = data;
		this.clear();
		this.add(createYSpacer(5));
		this.add(headerRow);
		this.add(createYSeparator(9));
		final int length = data.size();
		for (int i = 0; i < length; ++i)
			add(createRow(this.view, data.get(i), i)).add(createYSpacer(5));
		invalidate();
	}

	private void invalidate() {
		if (layoutManager != null) {
			layoutManager.updateLayout();
			updateSubLayout();
		}
	}

	private ElementLayout createRow(AGLView view, ScoringElement elem, int i) {
		Row tr = new Row();
		tr.setPixelSizeY(ROW_HEIGHT);
		tr.add(createLabel(view, query.isSorted() ? String.format("%d.", i + 1) : "", COL0_RANK_WIDTH));
		tr.add(createXSpacer(3));
		tr.add(createButton(view, new Button(ADD_TO_STRATOMEX, i, EIconTextures.GROUPER_COLLAPSE_PLUS)));
		tr.add(createXSpacer(3));
		ElementLayout source = ReferenceElements.create(elem.getStratification(), elem.getGroup(), this.view);
		source.addBackgroundRenderer(createPickingRenderer(SELECT_ROW, i, this.view));
		tr.add(source);
		tr.add(createXSpacer(3));
		int j = 0;
		for (SortableColumnHeader header : columns) {
			int id = i << 8 + j++;
			tr.add(createScoreValue(view, elem, header, id)).add(createXSpacer(3));
		}
		tr.add(createXSpacer(COL2_ADD_COLUMN_X_WIDTH));
		tr.addBackgroundRenderer(new ColorRenderer(Colors.TRANSPARENT.getRGBA()));
		return tr;
	}

	private ElementLayout createScoreValue(AGLView view, ScoringElement elem, SortableColumnHeader header, int id) {
		Row row = new Row();
		row.setGrabY(true);
		row.setXDynamic(true);
		IScore underlyingScore = header.getScoreID();
		if (underlyingScore instanceof ProductScore)
			underlyingScore = elem.getSelected((ProductScore) underlyingScore);
		TablePerspective strat = resolveStratification(underlyingScore);
		Group group = resolveGroup(underlyingScore);

		switch (header.type) {
		case STRATIFICATION_SCORE:
			if (strat != null) {
				row.add(createColor(strat.getDataDomain().getColor(), ReferenceElements.DATADOMAIN_TYPE_WIDTH));
				row.add(createXSpacer(3));
				row.add(createLabel(view, strat.getRecordPerspective(), ReferenceElements.STRATIFACTION_WIDTH));
			} else {
				row.add(createXSpacer(ReferenceElements.DATADOMAIN_TYPE_WIDTH + 3
						+ ReferenceElements.STRATIFACTION_WIDTH));
			}
			row.add(createXSpacer(3));
			break;
		case GROUP_SCORE:
			if (strat != null) {
				row.add(createColor(strat.getDataDomain().getColor(), ReferenceElements.DATADOMAIN_TYPE_WIDTH));
				row.add(createXSpacer(3));
				row.add(createLabel(view, strat.getRecordPerspective(), ReferenceElements.STRATIFACTION_WIDTH));
			} else {
				row.add(createXSpacer(ReferenceElements.DATADOMAIN_TYPE_WIDTH + 3
						+ ReferenceElements.STRATIFACTION_WIDTH));
			}
			if (group != null) {
				row.add(createXSpacer(3));
				row.add(createLabel(view, group, ReferenceElements.GROUP_WIDTH));
			} else {
				row.add(createXSpacer(3 + ReferenceElements.GROUP_WIDTH));
			}
			row.add(createXSpacer(3));
			break;
		default:
			row.add(createXSpacer(20));
			break;
		}
		float value = header.getScoreID().getScore(elem);
		if (!Float.isNaN(value)) {
			ElementLayout valueEL = createLabel(view, Formatter.formatNumber(value), COLX_SCORE_WIDTH);
			valueEL.setGrabY(true);
			valueEL.addBackgroundRenderer(new ScoreBarRenderer(value, strat != null ? strat.getDataDomain().getColor()
					: new Color(0, 0, 1, 0.25f)));
			row.add(valueEL);
		} else {
			row.add(createXSpacer(COLX_SCORE_WIDTH));
		}
		row.addBackgroundRenderer(createPickingRenderer(SELECT_ROW_COLUMN, id, this.view));
		return row;
	}

	private static TablePerspective resolveStratification(IScore score) {
		if (score instanceof AStratificationScore)
			return ((AStratificationScore) score).getReference();
		if (score instanceof AGroupScore)
			return ((AGroupScore) score).getStratification();
		return null;
	}

	private static Group resolveGroup(IScore score) {
		if (score instanceof AGroupScore)
			return ((AGroupScore) score).getGroup();
		return null;
	}

	public static int getOptimalWidth(EScoreType type) {
		switch (type) {
		case GROUP_SCORE:
			return ReferenceElements.DATADOMAIN_TYPE_WIDTH + 3 + ReferenceElements.STRATIFACTION_WIDTH + 3
					+ ReferenceElements.GROUP_WIDTH;
		case STRATIFICATION_SCORE:
			return ReferenceElements.DATADOMAIN_TYPE_WIDTH + 3 + ReferenceElements.STRATIFACTION_WIDTH;
		default:
			return 20;
		}
	}

	public void setSelected(int row, int col) {
		ScoringElement old = null;
		if (selectedRow != -1) {
			old = data.get(selectedRow);
			setBackgroundColor(selectedRow, Colors.TRANSPARENT);
		}
		selectedRow = row;
		ScoringElement new_ = null;
		if (selectedRow != -1) {
			new_ = data.get(selectedRow);
			setBackgroundColor(row, SELECTED_COLOR);
		}
		selectionListener.onSelectionChanged(old, new_, getSelectScoreID(new_, col));
	}

	private IScore getSelectScoreID(ScoringElement row, int col) {
		if (row == null || col < 0)
			return null;
		IScore s = columns.get(col).getScoreID();
		if (s instanceof ProductScore)
			s = row.getSelected((ProductScore) s);
		return s;
	}

	private void setBackgroundColor(int i, IColor color) {
		ElementLayout r = getTableRow(i);
		ColorRenderer c = (ColorRenderer) r.getBackgroundRenderer().get(0);
		c.setColor(color.getRGBA());
	}

	private ElementLayout getTableRow(int i) {
		return get(3 + (i) * 2); // 1 border 2 for header *2 for spacing
	}

	protected void onSortBy(SortableColumnHeader columnHeader) {
		if (query == null)
			return;
		query.sortBy(columnHeader.getScoreID(), columnHeader.nextSorting());
	}


	private class SortableColumnHeader extends Row {
		private ESorting sort = ESorting.NONE;
		private final IScore scoreID;
		private final EScoreType type;

		public SortableColumnHeader(final IScore scoreID, int i, ESorting sorting) {
			this.scoreID = scoreID;
			this.sort = sorting;
			this.type = scoreID.getScoreType();
			setBottomUp(false);
			setPixelSizeX(getOptimalWidth(type) + 3 + COLX_SCORE_WIDTH);
			ElementLayout label = createLabel(view, scoreID, -1);
			label.setGrabY(true);
			add(label);
			add(wrap(new TextureRenderer(sort.getFileName(), view.getTextureManager()), 16));
			addBackgroundRenderer(createPickingRenderer(SORT_COLUMN, i, view));
		}

		public IScore getScoreID() {
			return scoreID;
		}

		public void setSort(ESorting sort) {
			if (this.sort == sort)
				return;
			this.sort = sort;
			get(1).setRenderer(new TextureRenderer(this.sort.getFileName(), view.getTextureManager()));
		}

		public ESorting nextSorting() {
			setSort(this.sort.next());
			return this.sort;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((scoreID == null) ? 0 : scoreID.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			SortableColumnHeader other = (SortableColumnHeader) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (scoreID == null) {
				if (other.scoreID != null)
					return false;
			} else if (!scoreID.equals(other.scoreID))
				return false;
			return true;
		}

		private ScoreQueryUI getOuterType() {
			return ScoreQueryUI.this;
		}

	}

	public interface ISelectionListener {
		public void onSelectionChanged(ScoringElement old_, ScoringElement new_, IScore new_column);
	}
}
