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
package org.caleydo.view.entourage.ranking;

import gleem.linalg.Vec2f;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.canvas.GLMouseAdapter;
import org.caleydo.core.view.opengl.canvas.GLThreadListenerWrapper;
import org.caleydo.core.view.opengl.canvas.IGLMouseListener;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.animation.AnimatedGLElementContainer;
import org.caleydo.core.view.opengl.layout2.basic.IScrollBar;
import org.caleydo.core.view.opengl.layout2.basic.ScrollBarCompatibility;
import org.caleydo.core.view.opengl.layout2.geom.Rect;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.manager.EPathwayDatabaseType;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.caleydo.view.entourage.EEmbeddingID;
import org.caleydo.view.entourage.GLEntourage;
import org.caleydo.view.entourage.GLWindow;
import org.caleydo.vis.lineup.config.IRankTableUIConfig;
import org.caleydo.vis.lineup.config.RankTableConfigBase;
import org.caleydo.vis.lineup.config.RankTableUIConfigBase;
import org.caleydo.vis.lineup.data.DoubleInferrers;
import org.caleydo.vis.lineup.layout.RowHeightLayouts;
import org.caleydo.vis.lineup.model.ARankColumnModel;
import org.caleydo.vis.lineup.model.CategoricalRankColumnModel;
import org.caleydo.vis.lineup.model.DoubleRankColumnModel;
import org.caleydo.vis.lineup.model.IRow;
import org.caleydo.vis.lineup.model.RankTableModel;
import org.caleydo.vis.lineup.model.StringRankColumnModel;
import org.caleydo.vis.lineup.model.mapping.PiecewiseMapping;
import org.caleydo.vis.lineup.model.mixin.ICollapseableColumnMixin;
import org.caleydo.vis.lineup.ui.RenderStyle;
import org.caleydo.vis.lineup.ui.TableUI;

import com.google.common.base.Function;

/**
 * @author Samuel Gratzl
 *
 */
public class RankingElement extends GLElementContainer {
	private final static int PATHWAY_NAME_COLUMN_WIDTH = 140;
	private final static int PATHWAY_DATABASE_COLUMN_WIDTH = 70;
	private final static int RANK_COLUMN_WIDTH = 50;

	private final RankTableModel table;
	private final GLEntourage view;
	private IPathwayFilter filter = PathwayFilters.NONE;
	// private IPathwayRanking ranking = PathwayRankings.SIZE;
	private ARankColumnModel currentRankColumnModel;
	private StringRankColumnModel pathwayNameColumn;
	private CategoricalRankColumnModel<?> pathwayDataBaseColumn;
	private GLWindow window;
	private TableUI tableUI;
	private IGLMouseListener mouseListener;

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
			((AnimatedGLElementContainer) window.getParent()).resizeChild(window, getRequiredWidth(), Float.NaN);
			if (!hasScoreColumn())
				setFilter(PathwayFilters.NONE);
		}
	};

	private final PropertyChangeListener onCollapseColumn = new PropertyChangeListener() {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			((AnimatedGLElementContainer) window.getParent()).resizeChild(window, getRequiredWidth(), Float.NaN);
			// window.setSize(getRequiredWidth(), Float.NaN);
		}
	};

	public float getRequiredWidth() {
		float width = pathwayNameColumn.getWidth() + pathwayDataBaseColumn.getWidth()
				+ (hasScoreColumn() ? currentRankColumnModel.getWidth() : 0) + 10;
		// + (pathwayDataBaseColumn.isCollapsed() ? COLLAPSED_COLUMN_WIDTH : PATHWAY_DATABASE_COLUMN_WIDTH)
		// + ( (currentRankColumnModel.isCollapsed() ? COLLAPSED_COLUMN_WIDTH
		// : RANK_COLUMN_WIDTH) : 0);
		return width;
	}

	public boolean hasScoreColumn() {
		return table.getColumns().size() > 2;
	}

	public RankingElement(final GLEntourage view) {
		this.view = view;
		mouseListener = GLThreadListenerWrapper.wrap(new GLMouseAdapter() {

			@Override
			public void mouseWheelMoved(IMouseEvent mouseEvent) {
				Vec2f location = getAbsoluteLocation();
				Vec2f size = getSize();
				Vec2f wheelPosition = mouseEvent.getPoint();
				if (wheelPosition.x() >= location.x() && wheelPosition.x() <= location.x() + size.x()
						&& wheelPosition.y() >= location.y() && wheelPosition.y() <= location.y() + size.y()) {
					tableUI.getBody().scroll(-mouseEvent.getWheelRotation());
				}

			}

		});
		view.getParentGLCanvas().addMouseListener(mouseListener);
		view.getEventListenerManager().register(mouseListener);

		this.table = new RankTableModel(new RankTableConfigBase());
		table.addPropertyChangeListener(RankTableModel.PROP_SELECTED_ROW, onSelectRow);
		table.addPropertyChangeListener(RankTableModel.PROP_COLUMNS, onModifyColumn);

		initTable(table);

		setLayout(GLLayouts.flowVertical(0));
		IRankTableUIConfig config = new RankTableUIConfigBase(true, false, false) {
			@Override
			public IScrollBar createScrollBar(boolean horizontal) {
				return new ScrollBarCompatibility(horizontal, view.getDndController());
			}

			@Override
			public void renderIsOrderByGlyph(GLGraphics g, float w, float h, boolean orderByIt) {
				// no highlight
			}

			@Override
			public EButtonBarPositionMode getButtonBarPosition() {
				return EButtonBarPositionMode.OVER_LABEL;
			}

			@Override
			public void renderRowBackground(GLGraphics g, Rect rect, boolean even, IRow row,
					IRow selected) {
				renderRowBackgroundImpl(g, rect.x(), rect.y(), rect.width(), rect.height(), even, row, selected);
			}

			@Override
			public boolean canEditValues() {
				return false;
			}

			@Override
			public boolean isSmallHeaderByDefault() {
				return false;
			}
		};

		tableUI = new TableUI(table, config, RowHeightLayouts.UNIFORM);
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
		pathwayNameColumn = new StringRankColumnModel(GLRenderers.drawText("Pathway", VAlign.CENTER),
				StringRankColumnModel.DEFAULT, Color.GRAY, new Color(.95f, .95f, .95f),
				StringRankColumnModel.FilterStrategy.SUBSTRING);
		pathwayNameColumn.setWidth(PATHWAY_NAME_COLUMN_WIDTH);
		pathwayNameColumn.addPropertyChangeListener(ICollapseableColumnMixin.PROP_COLLAPSED, onCollapseColumn);
		table.add(pathwayNameColumn);

		Collection<String> dbtypes = new ArrayList<>(2);
		for (EPathwayDatabaseType type : EPathwayDatabaseType.values()) {
			dbtypes.add(type.getName());
		}
		pathwayDataBaseColumn = CategoricalRankColumnModel.createSimple(
				GLRenderers.drawText("Pathway Type", VAlign.CENTER), new Function<IRow, String>() {
					@Override
					public String apply(IRow in) {
						PathwayRow r = (PathwayRow) in;
						return r.getPathway().getType().getName();
					}
				}, dbtypes);
		pathwayDataBaseColumn.setWidth(PATHWAY_DATABASE_COLUMN_WIDTH);
		pathwayDataBaseColumn.setCollapsed(true);
		pathwayDataBaseColumn.addPropertyChangeListener(ICollapseableColumnMixin.PROP_COLLAPSED, onCollapseColumn);
		table.add(pathwayDataBaseColumn);

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

	private DoubleRankColumnModel createDefaultFloatRankColumnModel(IPathwayRanking ranking) {
		DoubleRankColumnModel column = new DoubleRankColumnModel(ranking.getRankingFunction(), GLRenderers.drawText(
				ranking.getRankingCriterion(), VAlign.CENTER), Color.GRAY, Color.LIGHT_GRAY, new PiecewiseMapping(0,
				Float.NaN), DoubleInferrers.MEAN);
		column.setWidth(50);
		return column;
	}

	/**
	 * @param filter
	 *            setter, see {@link filter}
	 */
	public void setFilter(IPathwayFilter filter) {
		this.filter = filter;
		if (pathwayNameColumn.isFiltered())
			pathwayNameColumn.setFilter(null, false, false);
		applyFilter();
	}

	/**
	 * @param ranking
	 *            setter, see {@link ranking}
	 */
	public void setRanking(IPathwayRanking ranking) {
		// this.ranking = ranking;
		ARankColumnModel prevRankModel = currentRankColumnModel;
		currentRankColumnModel = createDefaultFloatRankColumnModel(ranking);
		currentRankColumnModel.addPropertyChangeListener(ICollapseableColumnMixin.PROP_COLLAPSED, onCollapseColumn);
		currentRankColumnModel.setWidth(RANK_COLUMN_WIDTH);
		if (table.getColumns().size() > 2) {
			table.replace(prevRankModel, currentRankColumnModel);
		} else {
			table.add(currentRankColumnModel);
		}
		((DoubleRankColumnModel) currentRankColumnModel).orderByMe();
	}

	/**
	 * @param window
	 *            setter, see {@link window}
	 */
	public void setWindow(GLWindow window) {
		this.window = window;
	}
}
