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
package org.caleydo.view.tourguide.v3.ui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLSandBox;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.view.tourguide.v3.config.RankTableConfigBase;
import org.caleydo.view.tourguide.v3.data.AFloatFunction;
import org.caleydo.view.tourguide.v3.layout.RowHeightLayouts;
import org.caleydo.view.tourguide.v3.model.ARow;
import org.caleydo.view.tourguide.v3.model.FloatRankColumnModel;
import org.caleydo.view.tourguide.v3.model.IRow;
import org.caleydo.view.tourguide.v3.model.PiecewiseLinearMapping;
import org.caleydo.view.tourguide.v3.model.RankRankColumnModel;
import org.caleydo.view.tourguide.v3.model.RankTableModel;
import org.caleydo.view.tourguide.v3.model.StackedRankColumnModel;
import org.caleydo.view.tourguide.v3.model.StringRankColumnModel;

/**
 * @author Samuel Gratzl
 *
 */
public class RankedVis extends GLElementContainer {
	private final RankTableModel table;


	public RankedVis(RankTableModel table) {
		this.table = table;
		setLayout(GLLayouts.flowVertical(0));

		this.add(new TableHeaderUI(table));
		this.add(new TableBodyUI(table, RowHeightLayouts.LINEAR));

		this.add(new ColumnPoolUI(table));
	}

	public static void main(String[] args) {
		RankTableModel table = new RankTableModel(new RankTableConfigBase() {
			@Override
			public boolean isInteractive() {
				return true;
			}
		});
		table.addColumn(new RankRankColumnModel());
		table.addColumn(new StringRankColumnModel(GLRenderers.drawText("Label", VAlign.CENTER),
				StringRankColumnModel.DFEAULT));

		final StackedRankColumnModel stacked = new StackedRankColumnModel();
		table.addColumn(stacked);

		table.addColumnTo(
				stacked,
				new FloatRankColumnModel(new SimpleAcc(1), GLRenderers.drawText("Float", VAlign.CENTER), Color
						.decode("#ffb380"), Color.decode("#ffe6d5"), new PiecewiseLinearMapping(0, 1)));
		table.addColumnTo(stacked,
				new FloatRankColumnModel(new SimpleAcc(2), GLRenderers.drawText("Float2", VAlign.CENTER), Color
						.decode("#80ffb3"), Color.decode("#e3f4d7"), new PiecewiseLinearMapping(0, 1)));

		table.addColumn(new FloatRankColumnModel(new SimpleAcc(3), GLRenderers.drawText("Float3", VAlign.CENTER), Color
				.decode("#5fd3bc"), Color.decode("#d5fff6"), new PiecewiseLinearMapping(0, 1)));
		table.addColumn(new FloatRankColumnModel(new SimpleAcc(4), GLRenderers.drawText("Float4", VAlign.CENTER), Color
				.decode("#ffb380"), Color.decode("#ffe6d5"), new PiecewiseLinearMapping(0, 1)));


		Random r = new Random(200);
		List<IRow> rows = new ArrayList<>(100);
		for (int i = 0; i < 100; ++i)
			rows.add(new SimpleRow("Item " + Integer.toString(i, Character.MAX_RADIX), r.nextFloat(), r.nextFloat(), r
					.nextFloat(), r.nextFloat()));
		table.addData(rows);
		GLSandBox.main(args, new RankedVis(table), new GLPadding(5));
	}

	private static class SimpleAcc extends AFloatFunction<IRow> {
		private final int what;

		public SimpleAcc(int what) {
			this.what = what;
		}

		@Override
		public float applyPrimitive(IRow in) {
			SimpleRow r = (SimpleRow) in;
			switch (what) {
			case 1:
				return r.value;
			case 2:
				return r.value2;
			case 3:
				return r.value3;
			case 4:
				return r.value4;
			}
			return 0;
		}
	}

	static class SimpleRow extends ARow {
		private final String name;
		private final float value;
		private final float value2;
		private final float value3;
		private final float value4;

		public SimpleRow(String name, float value, float value2, float value3, float value4) {
			this.name = name;
			this.value = value;
			this.value2 = value2;
			this.value3 = value3;
			this.value4 = value4;
		}

		@Override
		public String toString() {
			return name;
		}
	}
}
