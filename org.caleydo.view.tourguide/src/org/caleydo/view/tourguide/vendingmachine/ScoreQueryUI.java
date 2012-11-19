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
import static org.caleydo.core.view.opengl.layout.ElementLayouts.createLabel;
import static org.caleydo.core.view.opengl.layout.ElementLayouts.createXSeparator;
import static org.caleydo.core.view.opengl.layout.ElementLayouts.createXSpacer;
import static org.caleydo.core.view.opengl.layout.ElementLayouts.createYSeparator;
import static org.caleydo.core.view.opengl.layout.ElementLayouts.wrap;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.caleydo.core.util.color.Colors;
import org.caleydo.core.util.color.IColor;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.ColorRenderer;
import org.caleydo.core.view.opengl.layout.util.LabelRenderer;
import org.caleydo.core.view.opengl.layout.util.TextureRenderer;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.util.button.Button;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.view.tourguide.data.ESorting;
import org.caleydo.view.tourguide.data.ScoreQuery;
import org.caleydo.view.tourguide.data.ScoringElement;
import org.caleydo.view.tourguide.data.score.IScore;
import org.caleydo.view.tourguide.data.score.ProductScore;
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

	private static final IColor SELECTED_COLOR = Colors.YELLOW;

	private static final int COL0_ADD_TO_STRATOMEX_WIDTH = 16;
	private static final int COL1_LABEL_WIDTH = 220;
	private static final int COLX_SCORE_WIDTH = 100;

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
		}, SORT_COLUMN);
		view.addTypePickingListener(new APickingListener() {
			@Override
			public void clicked(Pick pick) {
				setSelected(pick.getObjectID(), -1);
			}
		}, SELECT_ROW);
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
				addToStratomex(pick.getObjectID());
			}
		}, ADD_TO_STRATOMEX);
	}

	private void init() {
		this.setBottomUp(false);
		setGrabX(true);
		setGrabY(true);
		this.headerRow = new Row();
		this.add(createYSeparator(5));
		this.add(headerRow);
		this.add(createYSeparator(5));
	}

	public void setQuery(ScoreQuery query) {
		if (this.query != null)
			this.query.removePropertyChangeListener(ScoreQuery.PROP_SELECTION, selectionChanged);
		this.query = query;
		this.query.addPropertyChangeListener(ScoreQuery.PROP_SELECTION, selectionChanged);
		// initial
		createColumns(query);
	}

	private void createColumns(ScoreQuery query) {
		this.headerRow.clear();
		this.columns.clear();
		this.headerRow.setPixelSizeY(ROW_HEIGHT);
		this.headerRow.add(createXSeparator(3));
		this.headerRow.add(createXSpacer(COL0_ADD_TO_STRATOMEX_WIDTH));
		this.headerRow.add(createXSeparator(3));
		this.headerRow.add(createLabel(view, "Identifier", COL1_LABEL_WIDTH));
		this.headerRow.add(createXSeparator(3));
		int i = 0;
		for (IScore column : query.getSelection()) {
			SortableColumnHeader col = new SortableColumnHeader(column, i++, query.getSorting(column));
			this.headerRow.add(col).add(createXSeparator(3));
			this.columns.add(col);
		}
		invalidate();
	}

	protected void onSelectionChanged(PropertyChangeEvent evt) {
		createColumns(this.query);
	}

	public ScoreQuery getQuery() {
		return query;
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

	public void setData(List<ScoringElement> data, AGLView view) {
		this.data = data;
		this.clear();
		this.add(createYSeparator(5));
		this.add(headerRow);
		this.add(createYSeparator(5));
		final int length = data.size();
		for (int i = 0; i < length; ++i)
			add(createRow(view, data.get(i), i)).add(createYSeparator(5));
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
		tr.add(createXSeparator(3));
		tr.add(createButton(view, new Button(ADD_TO_STRATOMEX, i,
				EIconTextures.GROUPER_COLLAPSE_PLUS)));
		tr.add(createXSeparator(3));
		ElementLayout label = wrap(new LabelRenderer(view, elem, SELECT_ROW, i), COL1_LABEL_WIDTH);
		label.addBackgroundRenderer(new ColorRenderer(elem.getDataDomain().getColor().getRGBA()));
		tr.add(label);
		tr.add(createXSeparator(3));
		int j = 0;
		for (SortableColumnHeader header : columns) {
			int id = i << 8 + j++;
			tr.add(createScoreValue(view, elem, header, id)).add(createXSeparator(3));
		}
		tr.addBackgroundRenderer(new ColorRenderer(Colors.TRANSPARENT.getRGBA()));
		return tr;
	}

	private ElementLayout createScoreValue(AGLView view, ScoringElement elem, SortableColumnHeader header, int id) {
		float value = header.getScoreID().getScore(elem);
		String label = header.getScoreID().getRepr(elem);
		ElementLayout l = wrap(new LabelRenderer(view, label).addPickingID(SELECT_ROW_COLUMN, id), -1);
		l.addBackgroundRenderer(new ScoreBarRenderer(value, elem.getDataDomain().getColor()));
		return l;
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

	protected void addToStratomex(int row) {
		addToStratomexCallback.apply(data.get(row));
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

		public SortableColumnHeader(final IScore scoreID, int i, ESorting sorting) {
			this.scoreID = scoreID;
			this.sort = sorting;
			setGrabX(true);
			setBottomUp(false);
			ElementLayout label = wrap(new LabelRenderer(view, scoreID, SORT_COLUMN, i), -1);
			label.setGrabY(true);
			add(label);
			add(wrap(new TextureRenderer(sort.getFileName(), view.getTextureManager()), 16));
		}

		public IScore getScoreID() {
			return scoreID;
		}

		public ESorting nextSorting() {
			this.sort = this.sort.next();
			get(1).setRenderer(new TextureRenderer(this.sort.getFileName(), view.getTextureManager()));
			return sort;
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
