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

import static university.wur.WorldUniversityYear.COL_QSSTARS;
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

import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLSandBox;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.vis.rank.model.ARow;
import org.caleydo.vis.rank.model.CategoricalRankColumnModel;
import org.caleydo.vis.rank.model.IRow;
import org.caleydo.vis.rank.model.RankRankColumnModel;
import org.caleydo.vis.rank.model.RankTableModel;
import org.caleydo.vis.rank.model.StringRankColumnModel;

import com.google.common.base.Function;

import demo.RankTableDemo;
import demo.RankTableDemo.IModelBuilder;

/**
 * @author Samuel Gratzl
 *
 */
public class WorldUniversityRanking2012 implements IModelBuilder {
	@Override
	public void apply(RankTableModel table) throws Exception {
		Map<String, String> countries = WorldUniversityYear.readCountries();

		Map<String, WorldUniversityYear[]> data = WorldUniversityYear.readData(2012);
		countries.keySet().retainAll(data.keySet());

		List<UniversityRow> rows = new ArrayList<>(data.size());
		for (Map.Entry<String, WorldUniversityYear[]> entry : data.entrySet()) {
			rows.add(new UniversityRow(entry.getKey(), entry.getValue(), countries.get(entry.getKey())));
		}
		table.addData(rows);
		data = null;

		RankRankColumnModel rank = new RankRankColumnModel();
		rank.setWidth(40);
		table.add(rank);
		StringRankColumnModel label = new StringRankColumnModel(GLRenderers.drawText("School Name", VAlign.CENTER),
				StringRankColumnModel.DEFAULT);
		label.setWidth(200);
		table.add(label);

		CategoricalRankColumnModel<String> cat = new CategoricalRankColumnModel<String>(GLRenderers.drawText(
				"School Name", VAlign.CENTER), StringRankColumnModel.DEFAULT, countries);
		table.add(cat);

		WorldUniversityYear.addYear(table, "World University Ranking", new YearGetter(0), true).orderByMe();
	}

	public static void dump() throws IOException {
		Map<String, String> countries = WorldUniversityYear.readCountries();

		Map<String, WorldUniversityYear[]> data = WorldUniversityYear.readData(2012);
		countries.keySet().retainAll(data.keySet());
		final char SEP = '\t';
		try (PrintWriter w = new PrintWriter(new File("wur2012_summary.csv"), "UTF-8")) {
			w.append("School name").append(SEP).append("Country");

			w.append(SEP).append("Academic reputation");
			w.append(SEP).append("Employer reputation");
			w.append(SEP).append("Faculty/student ratio");
			w.append(SEP).append("Citations per faculty");
			w.append(SEP).append("International faculty ratio");
			w.append(SEP).append("International student ratio");
			w.append(SEP).append("QS Stars");
			w.println();

			for (Map.Entry<String, WorldUniversityYear[]> entry : data.entrySet()) {
				w.append(entry.getKey()).append(SEP).append(Objects.toString(countries.get(entry.getKey()), ""));

				for (WorldUniversityYear y : entry.getValue()) {
					w.append(SEP).append(toString(y.get(COL_academic)));
					w.append(SEP).append(toString(y.get(COL_employer)));
					w.append(SEP).append(toString(y.get(COL_faculty)));
					w.append(SEP).append(toString(y.get(COL_citations)));
					w.append(SEP).append(toString(y.get(COL_academic)));
					w.append(SEP).append(toString(y.get(COL_international)));
					w.append(SEP).append(toString(y.get(COL_QSSTARS)));
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
				new WorldUniversityRanking2012());
	}
}
