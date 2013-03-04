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

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;

import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.caleydo.view.subgraph.GLSubGraph;
import org.caleydo.vis.rank.config.RankTableConfigBase;
import org.caleydo.vis.rank.data.AFloatFunction;
import org.caleydo.vis.rank.data.FloatInferrers;
import org.caleydo.vis.rank.data.IFloatFunction;
import org.caleydo.vis.rank.layout.RowHeightLayouts;
import org.caleydo.vis.rank.model.FloatRankColumnModel;
import org.caleydo.vis.rank.model.IRow;
import org.caleydo.vis.rank.model.RankTableModel;
import org.caleydo.vis.rank.model.StringRankColumnModel;
import org.caleydo.vis.rank.model.mapping.PiecewiseLinearMapping;
import org.caleydo.vis.rank.ui.TableBodyUI;
import org.caleydo.vis.rank.ui.TableHeaderUI;

/**
 * @author Samuel Gratzl
 *
 */
public class RankingElement extends GLElementContainer {
	private final RankTableModel table;
	private final GLSubGraph view;
	private final PropertyChangeListener onSelectRow = new PropertyChangeListener() {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			rowSelected((PathwayRow) evt.getNewValue());
		}
	};

	public RankingElement(GLSubGraph view) {
		this.view = view;
		this.table = new RankTableModel(new RankTableConfigBase() {
			@Override
			public boolean isInteractive() {
				return false;
			}
		});
		table.addPropertyChangeListener(RankTableModel.PROP_SELECTED_ROW, onSelectRow);
		initTable(table);
		setLayout(GLLayouts.flowVertical(0));
		this.add(new TableHeaderUI(table));
		TableBodyUI body = new TableBodyUI(table, RowHeightLayouts.LINEAR);
		body.addOnRowPick(new IPickingListener() {
			@Override
			public void pick(Pick pick) {
				onRowPick(pick);
			}
		});
		this.add(body);
	}

	/**
	 * @param pick
	 */
	protected void onRowPick(Pick pick) {
		int rank = pick.getObjectID();
		PathwayRow row = (PathwayRow) table.getCurrent(rank);
		System.out.println(row + " " + pick.getPickingMode());
	}

	/**
	 * @param newValue
	 */
	protected void rowSelected(PathwayRow newValue) {
		view.addPathway(newValue.getPathway());
	}

	/**
	 * @param table2
	 */
	private static void initTable(RankTableModel table) {
		// add columns
		table.addColumn(new StringRankColumnModel(GLRenderers.drawText("Pathway", VAlign.CENTER),
				StringRankColumnModel.DFEAULT));
		// table.addColumn(new StringRankColumnModel(GLRenderers.drawText("Pathway Type", VAlign.CENTER),
		// new Function<IRow, String>() {
		// @Override
		// public String apply(IRow in) {
		// PathwayRow r = (PathwayRow) in;
		// return r.getPathway().getType().getName();
		// }
		// }));

		IFloatFunction<IRow> pathwaySize = new AFloatFunction<IRow>() {
			@Override
			public float applyPrimitive(IRow in) {
				PathwayRow r = (PathwayRow) in;
				return r.getPathway().vertexSet().size();
			}
		};
		table.addColumn(new FloatRankColumnModel(pathwaySize, GLRenderers.drawText("Score", VAlign.CENTER), Color.BLUE,
				Color.LIGHT_GRAY, new PiecewiseLinearMapping(0, Float.NaN), FloatInferrers.MEAN));

		// add data
		Collection<PathwayRow> data = new ArrayList<>();
		for (PathwayGraph g : PathwayManager.get().getAllItems()) {
			data.add(new PathwayRow(g));
		}

		table.addData(data);

	}
}
