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

import static university.wur.WorldUniversityYear.COL_academic;
import static university.wur.WorldUniversityYear.COL_citations;
import static university.wur.WorldUniversityYear.COL_employer;
import static university.wur.WorldUniversityYear.COL_faculty;
import static university.wur.WorldUniversityYear.COL_international;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLSandBox;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.vis.rank.model.ARow;
import org.caleydo.vis.rank.model.CategoricalRankColumnModel;
import org.caleydo.vis.rank.model.IRow;
import org.caleydo.vis.rank.model.OrderColumn;
import org.caleydo.vis.rank.model.RankRankColumnModel;
import org.caleydo.vis.rank.model.RankTableModel;
import org.caleydo.vis.rank.model.StringRankColumnModel;

import com.google.common.base.Function;

import demo.RankTableDemo;
import demo.RankTableDemo.IModelBuilder;
import demo.ReflectionData;

/**
 * @author Samuel Gratzl
 *
 */
public class WorldUniversityRanking implements IModelBuilder {
	@Override
	public void apply(RankTableModel table) throws Exception {
		// qsrank schoolname qsstars overall academic employer faculty international internationalstudents citations
		// arts engineering life natural social

		Map<String, String> countries = WorldUniversityYear.readCountries();


		Map<String, WorldUniversityYear[]> data = WorldUniversityYear.readData(2012, 2011, 2010, 2009, 2008, 2007);
		countries.keySet().retainAll(data.keySet());

		Map<String, String> countryMetaData = new TreeMap<>();
		List<UniversityRow> rows = new ArrayList<>(data.size());
		for (Map.Entry<String, WorldUniversityYear[]> entry : data.entrySet()) {
			String c = countries.get(entry.getKey());
			if (c != null)
				countryMetaData.put(c, c);
			rows.add(new UniversityRow(entry.getKey(), entry.getValue(), countries.get(entry.getKey())));
		}
		table.addData(rows);
		data = null;

		table.add(new RankRankColumnModel());
		table.add(new StringRankColumnModel(GLRenderers.drawText("School Name", VAlign.CENTER),
				StringRankColumnModel.DEFAULT).setWidth(300));

		CategoricalRankColumnModel<String> cat = new CategoricalRankColumnModel<String>(GLRenderers.drawText("Country",
				VAlign.CENTER), new ReflectionData<>(UniversityRow.class.getDeclaredField("country"), String.class),
				countryMetaData);
		table.add(cat);

		int rankColWidth = 40;

		// Arrays.asList("wur2010.txt", "wur2011.txt", "wur2012.txt");
		WorldUniversityYear.addYear(table, "2012", new YearGetter(0), false, false).orderByMe();
		table.add(new OrderColumn());
		table.add(new RankRankColumnModel().setWidth(rankColWidth));
		WorldUniversityYear.addYear(table, "2011", new YearGetter(1), false, false).setCompressed(true);
		table.add(new OrderColumn());
		table.add(new RankRankColumnModel().setWidth(rankColWidth));
		WorldUniversityYear.addYear(table, "2010", new YearGetter(2), false, false).setCompressed(true);
		table.add(new OrderColumn());
		table.add(new RankRankColumnModel().setWidth(rankColWidth));
		WorldUniversityYear.addYear(table, "2009", new YearGetter(3), false, false).setCollapsed(true);
		table.add(new OrderColumn());
		table.add(new RankRankColumnModel().setWidth(rankColWidth));
		WorldUniversityYear.addYear(table, "2008", new YearGetter(4), false, false).setCollapsed(true);
		table.add(new OrderColumn());
		table.add(new RankRankColumnModel().setWidth(rankColWidth));
		WorldUniversityYear.addYear(table, "2007", new YearGetter(5), false, false).setCollapsed(true);
	}

	public static void dump() throws IOException {
		Map<String, String> countries = WorldUniversityYear.readCountries();

		int[] years = { 2012, 2011, 2010, 2009, 2008, 2007 };
		Map<String, WorldUniversityYear[]> data = WorldUniversityYear.readData(years);
		countries.keySet().retainAll(data.keySet());
		final char SEP = '\t';
		try (PrintWriter w = new PrintWriter(new File("wur_summary.csv"), "UTF-8")) {
			w.append("School name").append(SEP).append("Country");

			for (int year : years) {
				w.append(SEP).append(year + " ").append("Academic reputation");
				w.append(SEP).append(year + " ").append("Employer reputation");
				w.append(SEP).append(year + " ").append("Faculty/student ratio");
				w.append(SEP).append(year + " ").append("Citations per faculty");
				w.append(SEP).append(year + " ").append("International faculty ratio");
				w.append(SEP).append(year + " ").append("International student ratio");
			}
			w.println();

			for (Map.Entry<String, WorldUniversityYear[]> entry : data.entrySet()) {
				w.append(entry.getKey()).append(SEP).append(Objects.toString(countries.get(entry.getKey()), ""));

				for (WorldUniversityYear y : entry.getValue()) {
					w.append(SEP).append(y == null ? "" : toString(y.get(COL_academic)));
					w.append(SEP).append(y == null ? "" : toString(y.get(COL_employer)));
					w.append(SEP).append(y == null ? "" : toString(y.get(COL_faculty)));
					w.append(SEP).append(y == null ? "" : toString(y.get(COL_citations)));
					w.append(SEP).append(y == null ? "" : toString(y.get(COL_academic)));
					w.append(SEP).append(y == null ? "" : toString(y.get(COL_international)));
				}
				w.println();
			}
		}
	}

	private static CharSequence toString(float f) {
		if (Float.isNaN(f))
			return "";
		return Float.toString(f);
	}

	static class YearGetter implements Function<IRow, WorldUniversityYear> {
		private final int year;

		public YearGetter(int year) {
			this.year = year;
		}

		@Override
		public WorldUniversityYear apply(IRow in) {
			UniversityRow r = (UniversityRow) in;
			return r.years[year];
		}
	}

	static class UniversityRow extends ARow {
		public String schoolname;
		private String country;

		public WorldUniversityYear[] years;

		/**
		 * @param school
		 * @param country
		 * @param size
		 */
		public UniversityRow(String school, WorldUniversityYear[] years, String country) {
			this.schoolname = school;
			this.years = years;
			this.country = country;
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
		// dump();
		GLSandBox.main(args, RankTableDemo.class, "world university ranking 2012,2011 and 2010",
				new WorldUniversityRanking());
	}
}
