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
package org.caleydo.view.subgraph.ranking;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.basic.IScrollBar;
import org.caleydo.core.view.opengl.layout2.basic.ScrollBarCompatibility;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.caleydo.view.subgraph.EEmbeddingID;
import org.caleydo.view.subgraph.GLSubGraph;
import org.caleydo.view.subgraph.GLWindow;
import org.caleydo.vis.rank.config.IRankTableUIConfig;
import org.caleydo.vis.rank.config.RankTableConfigBase;
import org.caleydo.vis.rank.data.FloatInferrers;
import org.caleydo.vis.rank.layout.RowHeightLayouts;
import org.caleydo.vis.rank.model.ARankColumnModel;
import org.caleydo.vis.rank.model.FloatRankColumnModel;
import org.caleydo.vis.rank.model.IRow;
import org.caleydo.vis.rank.model.RankTableModel;
import org.caleydo.vis.rank.model.StringRankColumnModel;
import org.caleydo.vis.rank.model.mapping.PiecewiseMapping;
import org.caleydo.vis.rank.ui.RenderStyle;
import org.caleydo.vis.rank.ui.TableUI;

/**
 * @author Samuel Gratzl
 *
 */
public class RankingElement extends GLElementContainer {
	private final RankTableModel table;
	private final GLSubGraph view;
	private IPathwayFilter filter = PathwayFilters.NONE;
	// private IPathwayRanking ranking = PathwayRankings.SIZE;
	private ARankColumnModel currentRankColumnModel;
	private StringRankColumnModel textColumn;
	private GLWindow window;
	private final PropertyChangeListener onSelectRow = new PropertyChangeListener() {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			rowSelected((PathwayRow) evt.getNewValue());
		}
	};

	private final PropertyChangeListener onModifyColumn = new PropertyChangeListener() {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (window == null || window.getSize().x() < 2)
				return;
			if (table.getColumns().size() > 1) {
				if (window.getSize().x() != 200)
					window.setSize(200, Float.NaN);
			} else {
				if (window.getSize().y() != 150)
					window.setSize(150, Float.NaN);
				setFilter(PathwayFilters.NONE);
			}
		}
	};

	public int getNumTableColumns() {
		return table.getColumns().size();
	}

	public RankingElement(final GLSubGraph view) {
		this.view = view;
		this.table = new RankTableModel(new RankTableConfigBase());
		table.addPropertyChangeListener(RankTableModel.PROP_SELECTED_ROW, onSelectRow);
		table.addPropertyChangeListener(RankTableModel.PROP_COLUMNS, onModifyColumn);

		initTable(table);

		setLayout(GLLayouts.flowVertical(0));
		IRankTableUIConfig config = new IRankTableUIConfig() {
			@Override
			public boolean isMoveAble() {
				return false;
			}

			@Override
			public boolean isInteractive() {
				return true;
			}

			@Override
			public boolean canChangeWeights() {
				return false;
			}

			@Override
			public IScrollBar createScrollBar(boolean horizontal) {
				return new ScrollBarCompatibility(horizontal, view.getDndController());
			}

			@Override
			public void renderIsOrderByGlyph(GLGraphics g, float w, float h, boolean orderByIt) {
				// no highlight
			}

			@Override
			public boolean isShowColumnPool() {
				return false;
			}

			@Override
			public EButtonBarPositionMode getButtonBarPosition() {
				return EButtonBarPositionMode.OVER_LABEL;
			}

			@Override
			public void renderRowBackground(GLGraphics g, float x, float y, float w, float h, boolean even, IRow row,
					IRow selected) {
				renderRowBackgroundImpl(g, x, y, w, h, even, row, selected);
			}

			@Override
			public boolean canEditValues() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isSmallHeaderByDefault() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void renderHeaderBackground(GLGraphics g, float w, float h, float labelHeight, ARankColumnModel model) {
				// TODO Auto-generated method stub

			}
		};

		TableUI tableUI = new TableUI(table, config, RowHeightLayouts.UNIFORM);
		this.add(tableUI);
		tableUI.getBody().addOnRowPick(new IPickingListener() {
			@Override
			public void pick(Pick pick) {
				onRowPick(pick);
			}
		});
	}

	protected void renderRowBackgroundImpl(GLGraphics g, float x, float y, float w, float h, boolean even, IRow row,
			IRow selected) {
		PathwayRow pathwayRow = (PathwayRow) row;

		if (view.hasPathway(pathwayRow.getPathway())) {
			g.color(RenderStyle.COLOR_SELECTED_ROW);
			g.incZ();
			g.fillRect(x, y, w, h);
			g.color(RenderStyle.COLOR_SELECTED_BORDER);
			g.drawLine(x, y, x + w, y);
			g.drawLine(x, y + h, x + w, y + h);
			g.decZ();
		} else if (!even) {
			g.color(RenderStyle.COLOR_BACKGROUND_EVEN);
			g.fillRect(x, y, w, h);
		}

	}

	@Override
	protected void takeDown() {
		table.reset();
		super.takeDown();
	}

	/**
	 * @param pick
	 */
	protected void onRowPick(Pick pick) {
		int rank = pick.getObjectID();
		PathwayRow row = (PathwayRow) table.getMyRanker(null).get(rank);
		// view.createTooltip(new DefaultLabelProvider(row.toString()));
		// System.out.println(row + " " + pick.getPickingMode());
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		if (w < 10)
			return;
		super.renderImpl(g, w, h);
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		if (w < 10)
			return;
		super.renderPickImpl(g, w, h);
	}

	/**
	 * @param newValue
	 */
	protected void rowSelected(PathwayRow newValue) {

		if (newValue == null)
			return;

		if (!view.hasPathway(newValue.getPathway()))
			view.addPathway(newValue.getPathway(), EEmbeddingID.PATHWAY_LEVEL1);

		table.setSelectedRow(null);
	}

	private void applyFilter() {
		List<IRow> data = table.getData();
		BitSet dataMask = new BitSet(data.size());
		int i = 0;
		for (IRow row : data) {
			// select all rows
			dataMask.set(i++, filter.showPathway(((PathwayRow) row).getPathway()));
		}
		table.setDataMask(dataMask);
	}

	/**
	 * @param eventListeners2
	 * @param table2
	 */
	private void initTable(RankTableModel table) {
		// add columns
		textColumn = new StringRankColumnModel(GLRenderers.drawText("Pathway", VAlign.CENTER),
				StringRankColumnModel.DEFAULT, Color.GRAY, new Color(.95f, .95f, .95f),
				StringRankColumnModel.FilterStrategy.SUBSTRING);
		textColumn.setWidth(140);
		table.add(textColumn);
		// table.addColumn(new StringRankColumnModel(GLRenderers.drawText("Pathway Type", VAlign.CENTER),
		// new Function<IRow, String>() {
		// @Override
		// public String apply(IRow in) {
		// PathwayRow r = (PathwayRow) in;
		// return r.getPathway().getType().getName();
		// }
		// }));

		// IFloatFunction<IRow> pathwaySize = new AFloatFunction<IRow>() {
		// @Override
		// public float applyPrimitive(IRow in) {
		// PathwayRow r = (PathwayRow) in;
		// return r.getPathway().vertexSet().size();
		// }
		// };
		// currentRankColumnModel = createDefaultFloatRankColumnModel(ranking);
		// table.add(currentRankColumnModel);
		// add data
		List<PathwayRow> data = new ArrayList<>();
		for (PathwayGraph g : PathwayManager.get().getAllItems()) {
			data.add(new PathwayRow(g));
		}
		Collections.sort(data);

		table.addData(data);
		applyFilter();
	}

	private FloatRankColumnModel createDefaultFloatRankColumnModel(IPathwayRanking ranking) {
		FloatRankColumnModel column = new FloatRankColumnModel(ranking.getRankingFunction(), GLRenderers.drawText(
				ranking.getRankingCriterion(), VAlign.CENTER), org.caleydo.core.util.color.Color.GRAY,
				org.caleydo.core.util.color.Color.LIGHT_GRAY, new PiecewiseMapping(0,
				Float.NaN), FloatInferrers.MEAN);
		column.setWidth(50);
		return column;
	}

	/**
	 * @param filter
	 *            setter, see {@link filter}
	 */
	public void setFilter(IPathwayFilter filter) {
		this.filter = filter;
		if (textColumn.isFiltered())
			textColumn.setFilter(null, false);
		applyFilter();
	}

	/**
	 * @param ranking
	 *            setter, see {@link ranking}
	 */
	public void setRanking(IPathwayRanking ranking) {
		// this.ranking = ranking;
		FloatRankColumnModel newRankModel = createDefaultFloatRankColumnModel(ranking);
		if (table.getColumns().size() > 1) {
			table.replace(currentRankColumnModel, newRankModel);
		} else {
			table.add(newRankModel);
		}
		newRankModel.orderByMe();
		currentRankColumnModel = newRankModel;
	}

	/**
	 * @param window
	 *            setter, see {@link window}
	 */
	public void setWindow(GLWindow window) {
		this.window = window;
	}
}
