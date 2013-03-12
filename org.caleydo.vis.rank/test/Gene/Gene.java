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
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.vis.rank.data.FloatInferrers;
import org.caleydo.vis.rank.model.ARow;
import org.caleydo.vis.rank.model.FloatRankColumnModel;
import org.caleydo.vis.rank.model.RankRankColumnModel;
import org.caleydo.vis.rank.model.StackedRankColumnModel;
import org.caleydo.vis.rank.model.StringRankColumnModel;
import org.caleydo.vis.rank.model.mapping.PiecewiseMapping;

import demo.ARankTableDemo;
import demo.ReflectionFloatData;

/**
 * @author Samuel Gratzl
 *
 */
public class Gene extends ARankTableDemo {

	/**
	 *
	 */
	public Gene() {
		super("mutsig pancan12");
	}

	@Override
	protected void createModel() throws IOException, NoSuchFieldException {
		List<GeneRow> rows = readData();
		table.addData(rows);
		RankRankColumnModel rankRankColumnModel = new RankRankColumnModel();
		rankRankColumnModel.setWeight(35);
		table.add(rankRankColumnModel);
		table.add(new StringRankColumnModel(GLRenderers.drawText("Gene", VAlign.CENTER), StringRankColumnModel.DEFAULT));

		StackedRankColumnModel stacked = new StackedRankColumnModel();
		table.add(stacked);
		stacked.add(new FloatRankColumnModel(new ReflectionFloatData(field("LUSC_TP_p")), GLRenderers.drawText(
				"LUSC_TP_p", VAlign.CENTER), Color.decode("#DFC27D"), Color.decode("#F6E8C3"), new PiecewiseMapping(0,
				1), FloatInferrers.MEDIAN));
		stacked.add(new FloatRankColumnModel(new ReflectionFloatData(field("READ_TP_p")), GLRenderers.drawText(
				"READ_TP_p", VAlign.CENTER), Color.decode("#DFC27D"), Color.decode("#F6E8C3"), new PiecewiseMapping(0,
				1), FloatInferrers.MEDIAN));
		stacked.add(new FloatRankColumnModel(new ReflectionFloatData(field("GBM_TP_p")), GLRenderers.drawText(
				"GBM_TP_p", VAlign.CENTER), Color.decode("#DFC27D"), Color.decode("#F6E8C3"),
				new PiecewiseMapping(0, 1), FloatInferrers.MEDIAN));
		stacked.add(new FloatRankColumnModel(new ReflectionFloatData(field("BLCA_TP_p")), GLRenderers.drawText(
				"BLCA_TP_p", VAlign.CENTER), Color.decode("#DFC27D"), Color.decode("#F6E8C3"), new PiecewiseMapping(0,
				1), FloatInferrers.MEDIAN));

		stacked = new StackedRankColumnModel();
		table.add(stacked);
		stacked.add(new FloatRankColumnModel(new ReflectionFloatData(field("LUSC_TP_q")), GLRenderers.drawText(
				"LUSC_TP_q", VAlign.CENTER), Color.decode("#DFC27D"), Color.decode("#F6E8C3"), new PiecewiseMapping(0,
				1), FloatInferrers.MEDIAN));
		stacked.add(new FloatRankColumnModel(new ReflectionFloatData(field("READ_TP_q")), GLRenderers.drawText(
				"READ_TP_q", VAlign.CENTER), Color.decode("#DFC27D"), Color.decode("#F6E8C3"), new PiecewiseMapping(0,
				1), FloatInferrers.MEDIAN));
		stacked.add(new FloatRankColumnModel(new ReflectionFloatData(field("GBM_TP_q")), GLRenderers.drawText(
				"GBM_TP_q", VAlign.CENTER), Color.decode("#DFC27D"), Color.decode("#F6E8C3"), new PiecewiseMapping(0,
				1), FloatInferrers.MEDIAN));
		stacked.add(new FloatRankColumnModel(new ReflectionFloatData(field("BLCA_TP_q")), GLRenderers.drawText(
				"BLCA_TP_q", VAlign.CENTER), Color.decode("#DFC27D"), Color.decode("#F6E8C3"), new PiecewiseMapping(0,
				1), FloatInferrers.MEDIAN));

		// stacked = new StackedRankColumnModel();
		// table.add(stacked);
		// stacked.add(new FloatRankColumnModel(new ReflectionFloatData(field("LUSC_TP_rank")), GLRenderers.drawText(
		// "LUSC_TP_rank", VAlign.CENTER), Color.decode("#FC9272"), Color.decode("#FEE0D2"), new PiecewiseMapping(
		// 0, Float.NaN), FloatInferrers.MEDIAN).setWeight((float) 20 * 5));
		// stacked.add(new FloatRankColumnModel(new ReflectionFloatData(field("READ_TP_rank")), GLRenderers.drawText(
		// "READ_TP_rank", VAlign.CENTER), Color.decode("#FC9272"), Color.decode("#FEE0D2"), new PiecewiseMapping(
		// 0, Float.NaN), FloatInferrers.MEDIAN).setWeight((float) 20 * 5));
		// stacked.add(new FloatRankColumnModel(new ReflectionFloatData(field("GBM_TP_rank")), GLRenderers.drawText(
		// "GBM_TP_rank", VAlign.CENTER), Color.decode("#FC9272"), Color.decode("#FEE0D2"), new PiecewiseMapping(
		// 0, Float.NaN), FloatInferrers.MEDIAN).setWeight((float) 20 * 5));
		// stacked.add(new FloatRankColumnModel(new ReflectionFloatData(field("BLCA_TP_rank")), GLRenderers.drawText(
		// "BLCA_TP_rank", VAlign.CENTER), Color.decode("#FC9272"), Color.decode("#FEE0D2"), new PiecewiseMapping(
		// 0, Float.NaN), FloatInferrers.MEDIAN).setWeight((float) 20 * 5));

		stacked = new StackedRankColumnModel();
		table.add(stacked);
		stacked.add(new FloatRankColumnModel(new ReflectionFloatData(field("LUSC_TP_nflank")), GLRenderers.drawText(
				"LUSC_TP_nflank", VAlign.CENTER), Color.decode("#9ECAE1"), Color.decode("#DEEBF7"),
				new PiecewiseMapping(0, Float.NaN), FloatInferrers.MEDIAN).setWeight((float) 20 * 5));
		stacked.add(new FloatRankColumnModel(new ReflectionFloatData(field("READ_TP_nflank")), GLRenderers.drawText(
				"READ_TP_nflank", VAlign.CENTER), Color.decode("#9ECAE1"), Color.decode("#DEEBF7"),
				new PiecewiseMapping(0, Float.NaN), FloatInferrers.MEDIAN).setWeight((float) 20 * 5));
		stacked.add(new FloatRankColumnModel(new ReflectionFloatData(field("GBM_TP_nflank")), GLRenderers.drawText(
				"GBM_TP_nflank", VAlign.CENTER), Color.decode("#9ECAE1"), Color.decode("#DEEBF7"),
				new PiecewiseMapping(0, Float.NaN), FloatInferrers.MEDIAN).setWeight((float) 20 * 5));
		stacked.add(new FloatRankColumnModel(new ReflectionFloatData(field("BLCA_TP_nflank")), GLRenderers.drawText(
				"BLCA_TP_nflank", VAlign.CENTER), Color.decode("#9ECAE1"), Color.decode("#DEEBF7"),
				new PiecewiseMapping(0, Float.NaN), FloatInferrers.MEDIAN).setWeight((float) 20 * 5));

		stacked = new StackedRankColumnModel();
		table.add(stacked);
		stacked.add(new FloatRankColumnModel(new ReflectionFloatData(field("LUSC_TP_nsil")), GLRenderers.drawText(
				"LUSC_TP_nsil", VAlign.CENTER), Color.decode("#A1D99B"), Color.decode("#E5F5E0"), new PiecewiseMapping(
				0, Float.NaN), FloatInferrers.MEDIAN).setWeight((float) 20 * 5));
		stacked.add(new FloatRankColumnModel(new ReflectionFloatData(field("READ_TP_nsil")), GLRenderers.drawText(
				"READ_TP_nsil", VAlign.CENTER), Color.decode("#A1D99B"), Color.decode("#E5F5E0"), new PiecewiseMapping(
				0, Float.NaN), FloatInferrers.MEDIAN).setWeight((float) 20 * 5));
		stacked.add(new FloatRankColumnModel(new ReflectionFloatData(field("GBM_TP_nsil")), GLRenderers.drawText(
				"GBM_TP_nsil", VAlign.CENTER), Color.decode("#A1D99B"), Color.decode("#E5F5E0"), new PiecewiseMapping(
				0, Float.NaN), FloatInferrers.MEDIAN).setWeight((float) 20 * 5));
		stacked.add(new FloatRankColumnModel(new ReflectionFloatData(field("BLCA_TP_nsil")), GLRenderers.drawText(
				"BLCA_TP_nsil", VAlign.CENTER), Color.decode("#A1D99B"), Color.decode("#E5F5E0"), new PiecewiseMapping(
				0, Float.NaN), FloatInferrers.MEDIAN).setWeight((float) 20 * 5));

		stacked = new StackedRankColumnModel();
		table.add(stacked);
		stacked.add(new FloatRankColumnModel(new ReflectionFloatData(field("LUSC_TP_nnon")), GLRenderers.drawText(
				"LUSC_TP_nnon", VAlign.CENTER), Color.decode("#C994C7"), Color.decode("#E7E1EF"), new PiecewiseMapping(
				0, Float.NaN), FloatInferrers.MEDIAN).setWeight(20f * 5));
		stacked.add(new FloatRankColumnModel(new ReflectionFloatData(field("READ_TP_nnon")), GLRenderers.drawText(
				"READ_TP_nnon", VAlign.CENTER), Color.decode("#C994C7"), Color.decode("#E7E1EF"), new PiecewiseMapping(
				0, Float.NaN), FloatInferrers.MEDIAN).setWeight(20f * 5));
		stacked.add(new FloatRankColumnModel(new ReflectionFloatData(field("GBM_TP_nnon")), GLRenderers.drawText(
				"GBM_TP_nnon", VAlign.CENTER), Color.decode("#C994C7"), Color.decode("#E7E1EF"), new PiecewiseMapping(
				0, Float.NaN), FloatInferrers.MEDIAN).setWeight(20f * 5));
		stacked.add(new FloatRankColumnModel(new ReflectionFloatData(field("BLCA_TP_nnon")), GLRenderers.drawText(
				"BLCA_TP_nnon", VAlign.CENTER), Color.decode("#C994C7"), Color.decode("#E7E1EF"), new PiecewiseMapping(
				0, Float.NaN), FloatInferrers.MEDIAN).setWeight(20f * 5));

		stacked = new StackedRankColumnModel();
		table.add(stacked);
		stacked.add(new FloatRankColumnModel(new ReflectionFloatData(field("LUSC_TP_nnull")), GLRenderers.drawText(
				"LUSC_TP_nnull", VAlign.CENTER), Color.decode("#FDBB84"), Color.decode("#FEE8C8"),
				new PiecewiseMapping(0, Float.NaN), FloatInferrers.MEDIAN).setWeight(20f * 5));
		stacked.add(new FloatRankColumnModel(new ReflectionFloatData(field("READ_TP_nnull")), GLRenderers.drawText(
				"READ_TP_nnull", VAlign.CENTER), Color.decode("#FDBB84"), Color.decode("#FEE8C8"),
				new PiecewiseMapping(0, Float.NaN), FloatInferrers.MEDIAN).setWeight(20f * 5));
		stacked.add(new FloatRankColumnModel(new ReflectionFloatData(field("GBM_TP_nnull")), GLRenderers.drawText(
				"GBM_TP_nnull", VAlign.CENTER), Color.decode("#FDBB84"), Color.decode("#FEE8C8"), new PiecewiseMapping(
				0, Float.NaN), FloatInferrers.MEDIAN).setWeight(20f * 5));
		stacked.add(new FloatRankColumnModel(new ReflectionFloatData(field("BLCA_TP_nnull")), GLRenderers.drawText(
				"BLCA_TP_nnull", VAlign.CENTER), Color.decode("#FDBB84"), Color.decode("#FEE8C8"),
				new PiecewiseMapping(0, Float.NaN), FloatInferrers.MEDIAN).setWeight(20f * 5));
	}

	private static List<GeneRow> readData() throws IOException {
		List<GeneRow> rows = new ArrayList<>();
		try (BufferedReader r = new BufferedReader(new InputStreamReader(
				Gene.class.getResourceAsStream("mutsig_pancan12_all.txt"), Charset.forName("UTF-8")))) {
			String line;
			r.readLine();
			while ((line = r.readLine()) != null) {
				String[] l = line.split("\t");
				GeneRow row = new GeneRow();
				// row.rank = Integer.parseInt(l[0]);
				row.gene = l[0];
				// row.LUSC_TP_rank = Integer.parseInt(l[1]);
				row.LUSC_TP_nflank = toFloat(l, 2);
				row.LUSC_TP_nsil = toFloat(l, 3);
				row.LUSC_TP_nnon = toFloat(l, 4);
				row.LUSC_TP_nnull = toFloat(l, 5);
				row.LUSC_TP_p = toFloat(l, 6);
				row.LUSC_TP_q = toFloat(l, 7);

				// row.READ_TP_rank = Integer.parseInt(l[8]);
				row.READ_TP_nflank = toFloat(l, 9);
				row.READ_TP_nsil = toFloat(l, 10);
				row.READ_TP_nnon = toFloat(l, 11);
				row.READ_TP_nnull = toFloat(l, 12);
				row.READ_TP_p = toFloat(l, 13);
				row.READ_TP_q = toFloat(l, 14);

				// row.GBM_TP_rank = Integer.parseInt(l[15]);
				row.GBM_TP_nflank = toFloat(l, 16);
				row.GBM_TP_nsil = toFloat(l, 17);
				row.GBM_TP_nnon = toFloat(l, 18);
				row.GBM_TP_nnull = toFloat(l, 19);
				row.GBM_TP_p = toFloat(l, 20);
				row.GBM_TP_q = toFloat(l, 21);

				// row.BLCA_TP_rank = Integer.parseInt(l[22]);
				row.BLCA_TP_nflank = toFloat(l, 23);
				row.BLCA_TP_nsil = toFloat(l, 24);
				row.BLCA_TP_nnon = toFloat(l, 25);
				row.BLCA_TP_nnull = toFloat(l, 26);
				row.BLCA_TP_p = toFloat(l, 27);
				row.BLCA_TP_q = toFloat(l, 2);

				rows.add(row);
			}
		}
		return rows;
	}

	public static Field field(String f) throws NoSuchFieldException {
		return GeneRow.class.getDeclaredField(f);
	}

	static class GeneRow extends ARow {
		public int rank;
		public String gene;
		public float LUSC_TP_rank;
		public float LUSC_TP_nflank;
		public float LUSC_TP_nsil;
		public float LUSC_TP_nnon;
		public float LUSC_TP_nnull;
		public float LUSC_TP_p;
		public float LUSC_TP_q;

		public float READ_TP_rank;
		public float READ_TP_nflank;
		public float READ_TP_nsil;
		public float READ_TP_nnon;
		public float READ_TP_nnull;
		public float READ_TP_p;
		public float READ_TP_q;

		public float GBM_TP_rank;
		public float GBM_TP_nflank;
		public float GBM_TP_nsil;
		public float GBM_TP_nnon;
		public float GBM_TP_nnull;
		public float GBM_TP_p;
		public float GBM_TP_q;

		public float BLCA_TP_rank;
		public float BLCA_TP_nflank;
		public float BLCA_TP_nsil;
		public float BLCA_TP_nnon;
		public float BLCA_TP_nnull;
		public float BLCA_TP_p;
		public float BLCA_TP_q;

		@Override
		public String toString() {
			return gene;
		}
	}

	public static void main(String[] args) {
		new Gene().run();
	}
}
