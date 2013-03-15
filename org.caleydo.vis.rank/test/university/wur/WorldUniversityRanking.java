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
package university.wur;

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
public class WorldUniversityRanking extends ARankTableDemo {

	/**
	 *
	 */
	public WorldUniversityRanking(Shell parentShell) {
		super(parentShell, "world university ranking 2012,2011 and 2010");
	}

	@Override
	protected void createModel() throws IOException, NoSuchFieldException {
		// qsrank schoolname qsstars overall academic employer faculty international internationalstudents citations
		// arts engineering life natural social

		List<UniversityRow> rows = readData();
		table.addData(rows);

		table.add(new RankRankColumnModel());
		table.add(new StringRankColumnModel(GLRenderers.drawText("School Name", VAlign.CENTER),
				StringRankColumnModel.DEFAULT));

		// Arrays.asList("wur2010.txt", "wur2011.txt", "wur2012.txt");
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
		// table.add(new StarsRankColumnModel(table, ))

		final StackedRankColumnModel stacked = new StackedRankColumnModel();
		stacked.setTitle(title);
		table.add(stacked);
		// * Academic reputation (40%)
		// * Employer reputation (10%)
		// * Faculty/student ratio (20%)
		// * Citations per faculty (20%)
		// * International faculty ratio (5%)
		// * International student ratio (5%)
		stacked.add(new FloatRankColumnModel(new ValueGetter(year, UniversityYear.COL_academic), GLRenderers.drawText(
				"Academic reputation", VAlign.CENTER), Color.decode("#FC9272"), Color.decode("#FEE0D2"),
				percentage(), FloatInferrers.MEDIAN));
		stacked.add(new FloatRankColumnModel(new ValueGetter(year, UniversityYear.COL_employer), GLRenderers.drawText(
				"Employer reputation", VAlign.CENTER), Color.decode("#9ECAE1"), Color.decode("#DEEBF7"),
				percentage(), FloatInferrers.MEDIAN));
		stacked.add(new FloatRankColumnModel(new ValueGetter(year, UniversityYear.COL_faculty), GLRenderers.drawText(
				" Faculty/student ratio", VAlign.CENTER), Color.decode("#A1D99B"), Color.decode("#E5F5E0"),
				percentage(), FloatInferrers.MEDIAN));
		stacked.add(new FloatRankColumnModel(new ValueGetter(year, UniversityYear.COL_citations), GLRenderers.drawText(
				"Citations per faculty", VAlign.CENTER), Color.decode("#C994C7"), Color.decode("#E7E1EF"),
				percentage(), FloatInferrers.MEDIAN));
		stacked.add(new FloatRankColumnModel(new ValueGetter(year, UniversityYear.COL_international), GLRenderers
				.drawText("International faculty ratio", VAlign.CENTER), Color.decode("#FDBB84"), Color
				.decode("#FEE8C8"), percentage(), FloatInferrers.MEDIAN));
		stacked.add(new FloatRankColumnModel(new ValueGetter(year, UniversityYear.COL_internationalstudents),
				GLRenderers.drawText("International student ratio", VAlign.CENTER), Color.decode("#DFC27D"), Color
						.decode("#F6E8C3"), percentage(), FloatInferrers.MEDIAN));

		stacked.setDistributions(new float[] { 40, 10, 20, 20, 5, 5 });
		stacked.setWidth(300);
	}

	protected PiecewiseMapping percentage() {
		return new PiecewiseMapping(0, 100);
	}

	private static List<UniversityRow> readData() throws IOException {
		List<String> years = Arrays.asList("wur2010.txt", "wur2011.txt", "wur2012.txt");

		Map<String, UniversityRow> data = new LinkedHashMap<>();

		for (int i = 0; i < years.size(); ++i) {
			try (BufferedReader r = new BufferedReader(new InputStreamReader(WorldUniversityRanking.class.getResourceAsStream(years
					.get(i)), Charset.forName("UTF-8")))) {
				String line;
				r.readLine(); // header
				while ((line = r.readLine()) != null) {
					String[] l = line.split("\t");
					String school = l[1];

					UniversityYear universityYear = new UniversityYear(l);
					if (!data.containsKey(school)) {
						data.put(school, new UniversityRow(school, years.size()));
					}
					data.get(school).years[i] = universityYear;
				}
			}
		}
		List<UniversityRow> rows = new ArrayList<>(data.values());
		return rows;
	}

	static class UniversityYear {
		public static final int COL_QSSTARS = 0;
		public static final int COL_overall = 1;
		public static final int COL_academic = 2;
		public static final int COL_employer = 3;
		public static final int COL_faculty = 4;
		public static final int COL_international = 5;
		public static final int COL_internationalstudents = 6;
		public static final int COL_citations = 7;
		public static final int COL_arts = 8;
		public static final int COL_engineering = 9;
		public static final int COL_life = 10;
		public static final int COL_natural = 11;
		public static final int COL_social = 12;

		private final float qsstars;
		private final float overall;
		private final float academic;
		private final float employer;
		private final float faculty;
		private final float international;
		private final float internationalstudents;
		private final float citations;
		private final float arts;
		private final float engineering;
		private final float life;
		private final float natural;
		private final float social;

		public UniversityYear(String[] l) {
			qsstars = toFloat(l, 2);
			overall = toFloat(l, 3);
			academic = toFloat(l, 4);
			employer = toFloat(l, 5);
			faculty = toFloat(l, 6);
			international = toFloat(l, 7);
			internationalstudents = toFloat(l, 8);
			citations = toFloat(l, 9);
			arts = toFloat(l, 10);
			engineering = toFloat(l, 11);
			life = toFloat(l, 12);
			natural = toFloat(l, 13);
			social = toFloat(l, 14);
		}

		public float get(int index) {
			switch (index) {
			case COL_academic:
				return academic;
			case COL_arts:
				return arts;
			case COL_citations:
				return citations;
			case COL_employer:
				return employer;
			case COL_engineering:
				return engineering;
			case COL_faculty:
				return faculty;
			case COL_international:
				return international;
			case COL_internationalstudents:
				return internationalstudents;
			case COL_life:
				return life;
			case COL_natural:
				return natural;
			case COL_overall:
				return overall;
			case COL_QSSTARS:
				return qsstars;
			case COL_social:
				return social;
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
			UniversityYear year = r.years[index];
			if (year == null)
				return Float.NaN;
			return year.get(subindex);
		}
	}

	static class UniversityRow extends ARow {
		public String schoolname;

		public UniversityYear[] years;

		/**
		 * @param school
		 * @param size
		 */
		public UniversityRow(String school, int size) {
			this.schoolname = school;
			this.years = new UniversityYear[size];
		}

		@Override
		public String toString() {
			return schoolname;
		}
	}

	public static void main(String[] args) {
		main(args, WorldUniversityRanking.class);
	}
}
