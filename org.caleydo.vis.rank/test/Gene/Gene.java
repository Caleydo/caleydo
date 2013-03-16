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
package Gene;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.vis.rank.data.AFloatFunction;
import org.caleydo.vis.rank.data.FloatInferrers;
import org.caleydo.vis.rank.model.ARow;
import org.caleydo.vis.rank.model.FloatRankColumnModel;
import org.caleydo.vis.rank.model.IRow;
import org.caleydo.vis.rank.model.RankRankColumnModel;
import org.caleydo.vis.rank.model.StackedRankColumnModel;
import org.caleydo.vis.rank.model.StringRankColumnModel;
import org.caleydo.vis.rank.model.mapping.PiecewiseMapping;
import org.eclipse.swt.widgets.Shell;

import demo.ARankTableDemo;

/**
 * @author Samuel Gratzl
 *
 */
public class Gene extends ARankTableDemo {

	/**
	 *
	 */
	public Gene(Shell parentShell) {
		super(parentShell, "mutsig pancan12");
	}

	enum TumorType {
		LUSC_TP, READ_TP, GBM_TP, BLCA_TP, UCEC_TP, COAD_TP, OV_TP, LAML_TB, HNSC, LUAD, BRCA, KIRC
	}


	static class TumorTypeRow {
		public static final int NUM_COLUMNS = 7;
		public static final int COL_RANK = 0;
		public static final int COL_nflank = 1;
		public static final int COL_nsil = 2;
		public static final int COL_nnon = 3;
		public static final int COL_nnull = 4;
		public static final int COL_p = 5;
		public static final int COL_q = 6;

		private float rank;
		private float nflank;
		private float nsil;
		private float nnon;
		private float nnull;
		private float p;
		private float q;

		public TumorTypeRow(String[] l, int offset) {
			rank = toFloat(l, offset++);
			nflank = toFloat(l, offset++);
			nsil = toFloat(l, offset++);
			nnon = toFloat(l, offset++);
			nnull = toFloat(l, offset++);
			p = toFloat(l, offset++);
			q = toFloat(l, offset++);
		}

		public float get(int index) {
			switch (index) {
			case COL_RANK:
				return rank;
			case COL_nflank:
				return nflank;
			case COL_nsil:
				return nsil;
			case COL_nnon:
				return nnon;
			case COL_nnull:
				return nnull;
			case COL_p:
				return p;
			case COL_q:
				return q;
			}
			return 0;
		}
	}

	@Override
	protected void createModel() throws IOException, NoSuchFieldException {
		List<GeneRow> rows = readData();
		table.addData(rows);
		RankRankColumnModel rankRankColumnModel = new RankRankColumnModel();
		table.add(rankRankColumnModel);
		table.add(new StringRankColumnModel(GLRenderers.drawText("Gene", VAlign.CENTER), StringRankColumnModel.DEFAULT));


		StackedRankColumnModel m = createPValue(TumorTypeRow.COL_p, "p", Color.decode("#DFC27D"),
				Color.decode("#F6E8C3"));
		m.setFilter(0.3f, 1.0f);
		createPValue(TumorTypeRow.COL_q, "q", Color.decode("#DFC27D"), Color.decode("#F6E8C3"));
		createUnBound(TumorTypeRow.COL_nflank, "nflank", Color.decode("#9ECAE1"), Color.decode("#DEEBF7"));
		createUnBound(TumorTypeRow.COL_nsil, "nsil", Color.decode("#A1D99B"), Color.decode("#E5F5E0"));
		createUnBound(TumorTypeRow.COL_nnon, "nnon", Color.decode("#C994C7"), Color.decode("#E7E1EF"));
		createUnBound(TumorTypeRow.COL_nnull, "nnull", Color.decode("#FDBB84"), Color.decode("#FEE8C8"));
	}

	private StackedRankColumnModel createPValue(final int column, final String label, Color color, Color bgColor) {
		StackedRankColumnModel stacked = new StackedRankColumnModel();
		stacked.setTitle(label);
		table.add(stacked);
		for (TumorType type : TumorType.values()) {
			stacked.add(new FloatRankColumnModel(new ValueGetter(type, column), GLRenderers.drawText(type.name() + "_"
					+ label, VAlign.CENTER), color, bgColor, pValueMapping(), FloatInferrers.MEDIAN));
		}
		stacked.setWidth(250);
		return stacked;
	}

	private StackedRankColumnModel createUnBound(final int column, final String label, Color color, Color bgColor) {
		StackedRankColumnModel stacked = new StackedRankColumnModel();
		stacked.setTitle(label);
		table.add(stacked);
		for (TumorType type : TumorType.values()) {
			stacked.add(new FloatRankColumnModel(new ValueGetter(type, column), GLRenderers.drawText(type.name() + "_"
					+ label, VAlign.CENTER), color, bgColor, new PiecewiseMapping(0, Float.NaN), FloatInferrers.MEDIAN));
		}
		stacked.setWidth(150);
		return stacked;
	}

	private PiecewiseMapping pValueMapping() {
		PiecewiseMapping p = new PiecewiseMapping(0, 1);
		p.clear();
		p.put(0, 1);
		p.put(1, 0);
		return p;
	}

	private static List<GeneRow> readData() throws IOException {
		List<GeneRow> rows = new ArrayList<>();
		try (BufferedReader r = new BufferedReader(new InputStreamReader(
				Gene.class.getResourceAsStream("mutsig_pancan12_all.txt"), Charset.forName("UTF-8")))) {
			String line;
			r.readLine(); // skip label
			while ((line = r.readLine()) != null) {
				String[] l = line.split("\t");
				GeneRow row = new GeneRow();
				// row.rank = Integer.parseInt(l[0]);
				row.gene = l[0];
				int offset = 1;
				row.tumorTypes = new TumorTypeRow[TumorType.values().length];
				for (int i = 0; i < row.tumorTypes.length; ++i) {
					row.tumorTypes[i] = new TumorTypeRow(l, offset);
					offset += TumorTypeRow.NUM_COLUMNS;
				}
				rows.add(row);
			}
		}
		return rows;
	}

	static class ValueGetter extends AFloatFunction<IRow> {
		private final int index;
		private final int subindex;

		public ValueGetter(TumorType type, int column) {
			this.index = type.ordinal();
			this.subindex = column;
		}

		@Override
		public float applyPrimitive(IRow in) {
			GeneRow r = (GeneRow) in;
			return r.tumorTypes[index].get(subindex);
		}

	}
	static class GeneRow extends ARow {
		public int rank;
		public String gene;
		public TumorTypeRow[] tumorTypes;

		@Override
		public String toString() {
			return gene;
		}
	}

	public static void main(String[] args) {
		main(args, Gene.class);
	}
}
