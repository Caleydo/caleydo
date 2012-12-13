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
import static org.caleydo.core.view.opengl.layout.ElementLayouts.createXSpacer;
import static org.caleydo.core.view.opengl.layout.ElementLayouts.createYSeparator;
import static org.caleydo.core.view.opengl.layout.ElementLayouts.createYSpacer;
import static org.caleydo.core.view.opengl.layout.ElementLayouts.wrap;
import static org.caleydo.view.tourguide.renderstyle.TourGuideRenderStyle.COL0_RANK_WIDTH;
import static org.caleydo.view.tourguide.renderstyle.TourGuideRenderStyle.COL2_ADD_COLUMN_X_WIDTH;
import static org.caleydo.view.tourguide.renderstyle.TourGuideRenderStyle.COL_SPACING;
import static org.caleydo.view.tourguide.renderstyle.TourGuideRenderStyle.DATADOMAIN_TYPE_WIDTH;
import static org.caleydo.view.tourguide.renderstyle.TourGuideRenderStyle.GROUP_WIDTH;
import static org.caleydo.view.tourguide.renderstyle.TourGuideRenderStyle.LABEL_PADDING;
import static org.caleydo.view.tourguide.renderstyle.TourGuideRenderStyle.ROW_HEIGHT;
import static org.caleydo.view.tourguide.renderstyle.TourGuideRenderStyle.ROW_SPACING;
import static org.caleydo.view.tourguide.renderstyle.TourGuideRenderStyle.SELECTED_COLOR;
import static org.caleydo.view.tourguide.renderstyle.TourGuideRenderStyle.STRATIFACTION_WIDTH;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.util.base.ConstantLabelProvider;
import org.caleydo.core.util.base.ILabelProvider;
import org.caleydo.core.util.color.Colors;
import org.caleydo.core.util.color.IColor;
import org.caleydo.core.view.contextmenu.ContextMenuCreator;
import org.caleydo.core.view.contextmenu.GenericContextMenuItem;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Padding;
import org.caleydo.core.view.opengl.layout.Padding.EMode;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.ColorRenderer;
import org.caleydo.core.view.opengl.layout.util.PickingRenderer;
import org.caleydo.core.view.opengl.layout.util.Renderers;
import org.caleydo.core.view.opengl.layout.util.TextureRenderer;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.util.button.Button;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.view.tourguide.data.ESorting;
import org.caleydo.view.tourguide.data.ScoreQuery;
import org.caleydo.view.tourguide.data.Scores;
import org.caleydo.view.tourguide.data.ScoringElement;
import org.caleydo.view.tourguide.data.score.CollapseScore;
import org.caleydo.view.tourguide.data.score.IScore;
import org.caleydo.view.tourguide.data.score.SizeMetric;
import org.caleydo.view.tourguide.event.AddScoreColumnEvent;
import org.caleydo.view.tourguide.event.CreateScoreColumnEvent;
import org.caleydo.view.tourguide.event.RemoveScoreColumnEvent;
import org.caleydo.view.tourguide.renderer.AdvancedTextureRenderer;
import org.caleydo.view.tourguide.renderstyle.TourGuideRenderStyle;
import org.caleydo.view.tourguide.util.LabelComparator;
import org.caleydo.view.tourguide.vendingmachine.ui.ScoreFilterDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Samuel Gratzl
 *
 */
public class ScoreQueryUI extends Column {
	private static final String SELECT_ROW = "SELECT_ROW";
	private static final String ADD_TO_STRATOMEX = "ADD_TO_STATOMEX";
	private static final String ADD_COLUMN = "ADD_COLUMN";
	private static final String EDIT_FILTER = "EDIT_FILTER";

	private final List<AScoreColumn> columns = new ArrayList<>();
	private Row headerRow;

	private ElementLayout colSpacing = createXSpacer(COL_SPACING);
	private ElementLayout rowSpacing = createYSpacer(ROW_SPACING);

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

	private final AGLView view;
	private boolean running;
	private final StratomexAdapter stratomex;

	public ScoreQueryUI(AGLView view, StratomexAdapter stratomex) {
		this.view = view;
		this.stratomex = stratomex;
		init();
		initListeners(view);

	}

	private void init() {
		this.setBottomUp(false);
		setGrabX(true);
		setGrabY(true);
		this.headerRow = new Row();
		initTableHeader();
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

	private void initTableHeader() {
		this.add(rowSpacing);
		this.add(headerRow);
		this.add(createYSeparator(9));
	}

	private void createColumns(ScoreQuery query) {
		this.headerRow.clear();
		this.columns.clear();
		this.headerRow.setPixelSizeY(ROW_HEIGHT);
		this.headerRow.add(colSpacing);
		this.headerRow.add(createEditFilter());
		this.headerRow.add(createXSpacer(16));
		this.headerRow.add(createReference(Colors.TRANSPARENT, new ConstantLabelProvider("Stratification"),
				new ConstantLabelProvider("Group")));
		this.headerRow.add(colSpacing);
		int i = 0;
		for (IScore column : query.getSelection()) {
			AScoreColumn col = ScoreColumns.create(column, i++, query.getSorting(column), view);
			this.headerRow.add(col).add(colSpacing);
			this.columns.add(col);
		}
		headerRow.add(createButton(view, new Button(ADD_COLUMN, 1, EIconTextures.GROUPER_COLLAPSE_PLUS)));
		invalidate();
	}

	private ElementLayout createEditFilter() {
		Row row = new Row();
		row.setPixelSizeX(COL0_RANK_WIDTH);
		// row.setLeftToRight(false);
		row.setGrabY(true);
		ElementLayout b = wrap(new TextureRenderer(TourGuideRenderStyle.ICON_TABLE_FILTER, view.getTextureManager()),
				16);
		b.setGrabY(true);
		b.addBackgroundRenderer(new PickingRenderer(EDIT_FILTER, 1, view));
		row.append(b);
		return row;
	}

	public void setData(List<ScoringElement> data) {
		setSelected(-1, -1);
		this.data = data;
		this.clear();
		initTableHeader();
		final int length = data.size();
		for (int i = 0; i < length; ++i)
			add(createRow(this.view, data.get(i), i)).add(rowSpacing);
		invalidate();
	}

	private ElementLayout createLabel(String label, int width) {
		return createLabel(new ConstantLabelProvider(label), width);
	}

	private ElementLayout createLabel(ILabelProvider label, int width) {
		return wrap(Renderers.createLabel(label, view.getTextRenderer()).padding(LABEL_PADDING).build(), width);
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
		tr.add(createLabel(query.isSorted() ? String.format("%d.", i + 1) : "", COL0_RANK_WIDTH));
		tr.add(colSpacing);
		// button only available if not already part of stratomex
		if (this.stratomex.contains(elem.getStratification())) {
			tr.add(createXSpacer(16));
		} else {
			tr.add(createButton(view, new Button(ADD_TO_STRATOMEX, i, EIconTextures.GROUPER_COLLAPSE_PLUS)));
		}
		tr.add(colSpacing);
		ElementLayout source = createReference(elem.getStratification(), elem.getGroup());
		source.addBackgroundRenderer(new PickingRenderer(SELECT_ROW, i, this.view));
		tr.add(source);
		tr.add(colSpacing);
		int j = 0;
		for (AScoreColumn header : columns) {
			int id = i << 8 + j++;
			tr.add(header.createValue(elem, id)).add(colSpacing);
		}
		tr.add(createXSpacer(COL2_ADD_COLUMN_X_WIDTH)); // for plus button
		return tr;
	}


	public ElementLayout createReference(TablePerspective stratification, Group group) {
		return createReference(stratification.getDataDomain().getColor(), stratification.getRecordPerspective(), group);
	}

	public ElementLayout createReference(IColor color, ILabelProvider stratification, ILabelProvider group) {
		Row elem = new Row();
		elem.setGrabY(true);
		elem.setXDynamic(true);
		elem.add(createColor(color, DATADOMAIN_TYPE_WIDTH));
		elem.add(colSpacing);
		if (group != null) {
			elem.add(createLabel(stratification, STRATIFACTION_WIDTH));
			elem.add(colSpacing);
			elem.add(createLabel(group, GROUP_WIDTH));
		} else {
			elem.add(createLabel(stratification, STRATIFACTION_WIDTH + GROUP_WIDTH));
		}
		return elem;
	}

	public void setSelected(int row, int col) {
		ScoringElement old = null;
		if (selectedRow != -1) {
			old = data.get(selectedRow);
			getTableRow(selectedRow).clearBackgroundRenderers();
		}
		selectedRow = row;
		ScoringElement new_ = null;
		if (selectedRow != -1) {
			new_ = data.get(selectedRow);
			getTableRow(selectedRow).addBackgroundRenderer(new ColorRenderer(SELECTED_COLOR.getRGBA()));
			getTableRow(selectedRow).updateSubLayout();
		}
		stratomex.updatePreview(old, new_, getSelectScoreID(new_, col), getVisibleColumns(new_));
	}


	private ElementLayout getTableRow(int i) {
		return get(3 + (i) * 2); // 1 border 2 for header *2 for spacing
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

	public void setRunning(boolean running) {
		if (this.running == running)
			return;
		this.running = running;
		if (!this.running)
			this.clearForegroundRenderers();
		else {
			Padding p = new Padding(EMode.PROPORTIONAL, .3f);
			this.addForeGroundRenderer(new AdvancedTextureRenderer(EIconTextures.LOADING_CIRCLE.getFileName(), view
					.getTextureManager(), p).setZ(-.02f));
		}
		invalidate();
	}

	/**
	 * @return the running, see {@link #running}
	 */
	public boolean isRunning() {
		return running;
	}

	private IScore getSelectScoreID(ScoringElement row, int col) {
		if (row == null || col < 0)
			return null;
		IScore s = columns.get(col).getScore();
		if (s instanceof CollapseScore)
			s = row.getSelected((CollapseScore) s);
		return s;
	}

	private Collection<IScore> getVisibleColumns(ScoringElement row) {
		if (row == null)
			return null;
		Collection<IScore> r = new ArrayList<>();
		for (AScoreColumn column : this.columns) {
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
				onSortBy(columns.get(pick.getObjectID()));
			}

			@Override
			public void rightClicked(Pick pick) {
				onShowColumnMenu(columns.get(pick.getObjectID()));
			}
		}, AScoreColumn.SORT_COLUMN);
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
		}, SELECT_ROW);
		view.addTypePickingTooltipListener("click to highlight, right-click to add to Stratomex", SELECT_ROW);
		view.addTypePickingListener(new APickingListener() {
			@Override
			public void clicked(Pick pick) {
				int id = pick.getObjectID();
				setSelected(id >> 8, id & 0xFF);
			}
		}, AScoreColumn.SELECT_ROW_COLUMN);
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

	protected void onSortBy(AScoreColumn columnHeader) {
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
		for (AScoreColumn col : columns) {
			ESorting s = query.getSorting(col.getScore());
			if (s != null)
				col.setSort(s);
		}
	}

	protected void onShowColumnMenu(AScoreColumn column) {
		ContextMenuCreator creator = view.getContextMenuCreator();
		creator.addContextMenuItem(new GenericContextMenuItem("Remove", new RemoveScoreColumnEvent(column.getScore(),
				false, this)));
		creator.addContextMenuItem(new GenericContextMenuItem("Remove And Forget", new RemoveScoreColumnEvent(column
				.getScore(), true, this)));
		creator.addSeparator();
		// creator.addContextMenuItem(new GenericContextMenuItem("Edit Filter", new ))
	}

	protected void onAddColumn() {
		List<IScore> scores = new ArrayList<>(Scores.get().getScoreIDs());
		Collections.sort(scores, new LabelComparator());

		ContextMenuCreator creator = view.getContextMenuCreator();
		creator.addContextMenuItem(new GenericContextMenuItem("Create Jaccard Index Score", new CreateScoreColumnEvent(
				CreateScoreColumnEvent.Type.JACCARD, this)));
		creator.addContextMenuItem(new GenericContextMenuItem("Create Adjusted Rand Score", new CreateScoreColumnEvent(
				CreateScoreColumnEvent.Type.ADJUSTED_RAND, this)));
		if (scores.size() >= 2) {
			creator.addContextMenuItem(new GenericContextMenuItem("Create Combined Score", new CreateScoreColumnEvent(
					CreateScoreColumnEvent.Type.COMBINED, this)));
			creator.addContextMenuItem(new GenericContextMenuItem("Create Collapsed Score", new CreateScoreColumnEvent(
					CreateScoreColumnEvent.Type.COLLAPSED, this)));
		}
		creator.addSeparator();

		Set<IScore> visible = getVisibleColumns();

		for (IScore simple : Arrays.asList(new SizeMetric())) {
			if (visible.contains(simple))
				continue;
			creator.addContextMenuItem(new GenericContextMenuItem("Add " + simple.getLabel() + " Metric",
					new AddScoreColumnEvent(simple, this)));
		}
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
		for (AScoreColumn c : this.columns)
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



}

interface ISelectionListener {
	public void onSelectionChanged(ScoringElement old_, ScoringElement new_, IScore selectedColumn,
			Collection<IScore> visibleColumns);
}
