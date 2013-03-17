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
package university.arwu;

import static demo.RankTableDemo.toFloat;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;

import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.vis.rank.data.AFloatFunction;
import org.caleydo.vis.rank.data.FloatInferrers;
import org.caleydo.vis.rank.model.FloatRankColumnModel;
import org.caleydo.vis.rank.model.IRow;
import org.caleydo.vis.rank.model.RankTableModel;
import org.caleydo.vis.rank.model.StackedRankColumnModel;
import org.caleydo.vis.rank.model.mapping.PiecewiseMapping;

import com.google.common.base.Function;

/**
 * @author Samuel Gratzl
 *
 */
public class AcademicUniversityYear {
	// ranking institution country national total alumini award hici nands pub pcb

	public static final int COL_ranking = 0;
	public static final int COL_national = 1;
	public static final int COL_total = 2;
	public static final int COL_alumini = 3;
	public static final int COL_award = 4;
	public static final int COL_hici = 5;
	public static final int COL_nands = 6;
	public static final int COL_pub = 7;
	public static final int COL_pcb = 8;

	private final float ranking;
	private final float national;
	private final float total;
	private final float alumini;
	private final float award;
	private final float hici;
	private final float nands;
	private final float pub;
	private final float pcb;

	public AcademicUniversityYear(String[] l) {
		ranking = toFloat(l, 0);
		national = toFloat(l, 3);
		total = toFloat(l, 4);
		alumini = toFloat(l, 5);
		award = toFloat(l, 6);
		hici = toFloat(l, 7);
		nands = toFloat(l, 8);
		pub = toFloat(l, 9);
		pcb = toFloat(l, 10);
	}

	public float get(int index) {
		switch (index) {
		case COL_ranking:
			return ranking;
		case COL_alumini:
			return alumini;
		case COL_award:
			return award;
		case COL_hici:
			return hici;
		case COL_nands:
			return nands;
		case COL_national:
			return national;
		case COL_pcb:
			return pcb;
		case COL_pub:
			return pub;
		case COL_total:
			return total;
		}
		return 0;
	}

	/**
	 * @param table
	 */
	public static void addYear(RankTableModel table, String title, final Function<IRow, AcademicUniversityYear> map) {
		final StackedRankColumnModel stacked = new StackedRankColumnModel();
		stacked.setTitle(title);
		table.add(stacked);
		// Criteria Indicator Code Weight
		// Quality of Education Alumni of an institution winning Nobel Prizes and Fields Medals Alumni 10%
		// Quality of Faculty Staff of an institution winning Nobel Prizes and Fields Medals Award 20%
		// Highly cited researchers in 21 broad subject categories HiCi 20%
		// Research Output Papers published in Nature and Science* N&S 20%
		// Papers indexed in Science Citation Index-expanded and Social Science Citation Index PUB 20%
		// Per Capita Performance Per capita academic performance of an institution PCP 10%

		stacked.add(col(map, COL_alumini,
				"Quality of Education\nAlumni of an institution winning Nobel Prizes and Fields Medals", "#FC9272",
				"#FEE0D2"));
		stacked.add(col(map, COL_award,
				"Quality of Faculty\nStaff of an institution winning Nobel Prizes and Fields Medals", "#9ECAE1",
				"#DEEBF7"));
		stacked.add(col(map, COL_hici, "Quality of Faculty\nHighly cited researchers in 21 broad subject categories",
				"#A1D99B", "#E5F5E0"));
		stacked.add(col(map, COL_nands, "Research Output\nPapers published in Nature and Science", "#C994C7", "#E7E1EF"));
		stacked.add(col(map, COL_pub,
				"Research Output\nPapers indexed in Science Citation Index-expanded and Social Science Citation Index",
				"#FDBB84", "#FEE8C8"));
		stacked.add(col(map, COL_pcb, "Per Capita Performance\nPer capita academic performance of an institution",
				"#DFC27D", "#F6E8C3"));

		stacked.setDistributions(new float[] { 10, 20, 20, 20, 20, 10 });
		stacked.setWidth(300);
	}

	private static FloatRankColumnModel col(Function<IRow, AcademicUniversityYear> year, int col, String text,
			String color, String bgColor) {
		return new FloatRankColumnModel(new ValueGetter(year, col), GLRenderers.drawText(text, VAlign.CENTER),
				Color.decode(color), Color.decode(bgColor), percentage(), FloatInferrers.MEDIAN);
	}

	protected static PiecewiseMapping percentage() {
		return new PiecewiseMapping(0, 100);
	}

	public static Map<String, Pair<String, AcademicUniversityYear[]>> readData(int... years) throws IOException {
		Map<String, Pair<String, AcademicUniversityYear[]>> data = new LinkedHashMap<>();
		for (int i = 0; i < years.length; ++i) {
			String year = String.format("argu%4d.txt", years[i]);
			try (BufferedReader r = new BufferedReader(new InputStreamReader(
					AcademicUniversityYear.class.getResourceAsStream(year), Charset.forName("UTF-8")))) {
				String line;
				r.readLine(); // header
				while ((line = r.readLine()) != null) {
					String[] l = line.split("\t");
					String school = l[1];
					String country = l[2];

					AcademicUniversityYear universityYear = new AcademicUniversityYear(l);
					if (!data.containsKey(school)) {
						Pair<String, AcademicUniversityYear[]> s = Pair.make(country,
								new AcademicUniversityYear[years.length]);
						data.put(school, s);
					}
					data.get(school).getSecond()[i] = universityYear;
				}
			}
		}
		return data;
	}

	static class ValueGetter extends AFloatFunction<IRow> {
		private final int subindex;
		private final Function<IRow, AcademicUniversityYear> year;

		public ValueGetter(Function<IRow, AcademicUniversityYear> year, int column) {
			this.year = year;
			this.subindex = column;
		}

		@Override
		public float applyPrimitive(IRow in) {
			AcademicUniversityYear y = year.apply(in);
			if (y == null)
				return Float.NaN;
			return y.get(subindex);
		}
	}
}
