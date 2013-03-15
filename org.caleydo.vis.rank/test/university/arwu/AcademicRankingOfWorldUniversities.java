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

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.vis.rank.data.AFloatFunction;
import org.caleydo.vis.rank.data.FloatInferrers;
import org.caleydo.vis.rank.model.ARow;
import org.caleydo.vis.rank.model.FloatRankColumnModel;
import org.caleydo.vis.rank.model.IRow;
import org.caleydo.vis.rank.model.OrderColumn;
import org.caleydo.vis.rank.model.RankRankColumnModel;
import org.caleydo.vis.rank.model.RankTableModel;
import org.caleydo.vis.rank.model.StackedRankColumnModel;
import org.caleydo.vis.rank.model.StringRankColumnModel;
import org.caleydo.vis.rank.model.mapping.PiecewiseMapping;
import org.eclipse.swt.widgets.Shell;

import demo.ARankTableDemo;

/**
 * @author Samuel Gratzl
 *
 */
public class AcademicRankingOfWorldUniversities extends ARankTableDemo {

	/**
	 *
	 */
	public AcademicRankingOfWorldUniversities(Shell parentShell) {
		super(parentShell, "academic ranking of world universities 2012,2011 and 2010");
	}

	@Override
	protected void createModel() throws IOException, NoSuchFieldException {
		// ranking institution country national total alumini award hici nands pub pcb

		List<UniversityRow> rows = readData();
		table.addData(rows);

		table.add(new RankRankColumnModel());
		table.add(new StringRankColumnModel(GLRenderers.drawText("Institution", VAlign.CENTER),
				StringRankColumnModel.DEFAULT));

		// Arrays.asList("argu2010.txt", "argu2011.txt", "argu2012.txt");
		addYear(table, "2012", 2);
		table.add(new OrderColumn());
		table.add(new RankRankColumnModel());
		addYear(table, "2011", 1);
		table.add(new OrderColumn());
		table.add(new RankRankColumnModel());
		addYear(table, "2010", 0);
	}

	/**
	 * @param table
	 */
	private void addYear(RankTableModel table, String title, int year) {
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

		stacked.add(new FloatRankColumnModel(new ValueGetter(year, AcademicUniversityYear.COL_alumini), GLRenderers
				.drawText("Quality of Education\nAlumni of an institution winning Nobel Prizes and Fields Medals",
						VAlign.CENTER), Color.decode("#FC9272"), Color.decode("#FEE0D2"),
				percentage(), FloatInferrers.MEDIAN));
		stacked.add(new FloatRankColumnModel(new ValueGetter(year, AcademicUniversityYear.COL_award), GLRenderers
				.drawText("Quality of Faculty\nStaff of an institution winning Nobel Prizes and Fields Medals",
						VAlign.CENTER), Color.decode("#9ECAE1"), Color.decode("#DEEBF7"),
				percentage(), FloatInferrers.MEDIAN));
		stacked.add(new FloatRankColumnModel(new ValueGetter(year, AcademicUniversityYear.COL_hici), GLRenderers
.drawText("Quality of Faculty\nHighly cited researchers in 21 broad subject categories",
						VAlign.CENTER), Color
				.decode("#A1D99B"), Color.decode("#E5F5E0"),
				percentage(), FloatInferrers.MEDIAN));
		stacked.add(new FloatRankColumnModel(new ValueGetter(year, AcademicUniversityYear.COL_nands), GLRenderers
				.drawText("Research Output\nPapers published in Nature and Science", VAlign.CENTER), Color
				.decode("#C994C7"), Color.decode("#E7E1EF"),
				percentage(), FloatInferrers.MEDIAN));
		stacked.add(new FloatRankColumnModel(
				new ValueGetter(year, AcademicUniversityYear.COL_pub),
				GLRenderers
						.drawText(
								"Research Output\nPapers indexed in Science Citation Index-expanded and Social Science Citation Index",
								VAlign.CENTER), Color.decode("#FDBB84"), Color
				.decode("#FEE8C8"), percentage(), FloatInferrers.MEDIAN));
		stacked.add(new FloatRankColumnModel(new ValueGetter(year, AcademicUniversityYear.COL_pcb), GLRenderers
				.drawText("Per Capita Performance\nPer capita academic performance of an institution", VAlign.CENTER),
				Color.decode("#DFC27D"), Color
						.decode("#F6E8C3"), percentage(), FloatInferrers.MEDIAN));

		stacked.setDistributions(new float[] { 10, 20, 20, 20, 20, 10 });
		stacked.setWidth(300);
	}

	protected PiecewiseMapping percentage() {
		return new PiecewiseMapping(0, 100);
	}

	private static List<UniversityRow> readData() throws IOException {
		List<String> years = Arrays.asList("argu2010.txt", "argu2011.txt", "argu2012.txt");

		Map<String, UniversityRow> data = new LinkedHashMap<>();

		for (int i = 0; i < years.size(); ++i) {
			try (BufferedReader r = new BufferedReader(new InputStreamReader(AcademicRankingOfWorldUniversities.class.getResourceAsStream(years
					.get(i)), Charset.forName("UTF-8")))) {
				String line;
				r.readLine(); // header
				while ((line = r.readLine()) != null) {
					String[] l = line.split("\t");
					String school = l[1];
					String country = l[2];

					AcademicUniversityYear universityYear = new AcademicUniversityYear(l);
					if (!data.containsKey(school)) {
						data.put(school, new UniversityRow(school, country, years.size()));
					}
					data.get(school).years[i] = universityYear;
				}
			}
		}
		List<UniversityRow> rows = new ArrayList<>(data.values());
		return rows;
	}

	static class AcademicUniversityYear {
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
	}

	static class ValueGetter extends AFloatFunction<IRow> {
		private final int index;
		private final int subindex;

		public ValueGetter(int year, int column) {
			this.index = year;
			this.subindex = column;
		}

		@Override
		public float applyPrimitive(IRow in) {
			UniversityRow r = (UniversityRow) in;
			AcademicUniversityYear year = r.years[index];
			if (year == null)
				return Float.NaN;
			return year.get(subindex);
		}
	}

	static class UniversityRow extends ARow {
		public final String schoolname;
		public final String country;

		public AcademicUniversityYear[] years;

		public UniversityRow(String school, String country, int size) {
			this.schoolname = school;
			this.country = country;
			this.years = new AcademicUniversityYear[size];
		}

		/**
		 * @return the country, see {@link #country}
		 */
		public String getCountry() {
			return country;
		}

		@Override
		public String toString() {
			return schoolname;
		}
	}

	public static void main(String[] args) {
		main(args, AcademicRankingOfWorldUniversities.class);
	}
}
