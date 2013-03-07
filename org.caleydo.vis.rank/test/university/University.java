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
package university;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.vis.rank.data.FloatInferrers;
import org.caleydo.vis.rank.model.ARow;
import org.caleydo.vis.rank.model.CategoricalRankColumnModel;
import org.caleydo.vis.rank.model.FloatRankColumnModel;
import org.caleydo.vis.rank.model.RankRankColumnModel;
import org.caleydo.vis.rank.model.StackedRankColumnModel;
import org.caleydo.vis.rank.model.StringRankColumnModel;
import org.caleydo.vis.rank.model.mapping.PiecewiseMapping;

import demo.ARankTableDemo;
import demo.ReflectionData;
import demo.ReflectionFloatData;

/**
 * @author Samuel Gratzl
 *
 */
public class University extends ARankTableDemo {

	/**
	 *
	 */
	public University() {
		super("top 100 universties under 50 years");
	}
	@Override
	protected void createModel() throws IOException, NoSuchFieldException {
		List<UniversityRow> rows = readData();
		table.addData(rows);
		Map<String, String> metaData = readCountriesCategories();
		table.addColumn(new RankRankColumnModel());
		table.addColumn(new StringRankColumnModel(GLRenderers.drawText("University", VAlign.CENTER),
				StringRankColumnModel.DFEAULT));
		// as categorical
		table.addColumn(new CategoricalRankColumnModel<String>(GLRenderers.drawText("Country",
				VAlign.CENTER), new ReflectionData(field("country")), metaData));
		// as string
		// table.addColumn(eventListeners.register(new StringRankColumnModel(GLRenderers.drawText("Country",
		// VAlign.CENTER),
		// new ReflectionData(field("country")))));


		table.addColumn(new StringRankColumnModel(GLRenderers.drawText("Year Founded", VAlign.CENTER),
				new ReflectionData(field("yearFounded"))));

		final StackedRankColumnModel stacked = new StackedRankColumnModel();
		table.addColumn(stacked);
		table.addColumnTo(
				stacked,
				new FloatRankColumnModel(new ReflectionFloatData(field("teaching")), GLRenderers.drawText("Teaching",
						VAlign.CENTER), Color.decode("#8DD3C7"), Color.decode("#EEEEEE"), new PiecewiseMapping(0,
						100), FloatInferrers.MEDIAN).setWeight((float) 30 * 5));
		table.addColumnTo(
				stacked,
				new FloatRankColumnModel(new ReflectionFloatData(field("research")), GLRenderers.drawText("Research",
						VAlign.CENTER), Color.decode("#FFFFB3"), Color.decode("#EEEEEE"), new PiecewiseMapping(0,
						100), FloatInferrers.MEDIAN).setWeight((float) 30 * 5));
		table.addColumnTo(
				stacked,
				new FloatRankColumnModel(new ReflectionFloatData(field("citations")), GLRenderers.drawText("Citations",
						VAlign.CENTER), Color.decode("#BEBADA"), Color.decode("#EEEEEE"), new PiecewiseMapping(0,
						100), FloatInferrers.MEDIAN).setWeight((float) 30 * 5));
		table.addColumnTo(
				stacked,
				new FloatRankColumnModel(new ReflectionFloatData(field("incomeFromIndustry")), GLRenderers.drawText(
						"Income From Industry", VAlign.CENTER), Color.decode("#FB8072"), Color.decode("#EEEEEE"),
						new PiecewiseMapping(0, 100), FloatInferrers.MEDIAN).setWeight(7.5f * 5));
		table.addColumnTo(
				stacked,
				new FloatRankColumnModel(new ReflectionFloatData(field("internationalMix")), GLRenderers.drawText(
						"International Mix", VAlign.CENTER), Color.decode("#80B1D3"), Color.decode("#EEEEEE"),
						new PiecewiseMapping(0, 100), FloatInferrers.MEDIAN).setWeight(2.5f * 5));

		table.addColumn(new FloatRankColumnModel(new ReflectionFloatData(field("overallScore")), GLRenderers.drawText(
				"Overall Score", VAlign.CENTER), Color.decode("#ffb380"), Color.decode("#ffe6d5"),
				new PiecewiseMapping(0, 100), FloatInferrers.MEDIAN));
	}

	private static Map<String, String> readCountriesCategories() throws IOException {
		Map<String, String> metaData = new HashMap<>();
		try (BufferedReader r = new BufferedReader(new InputStreamReader(
				University.class.getResourceAsStream("countries.txt"), Charset.forName("UTF-8")))) {
			String line;
			r.readLine();
			while ((line = r.readLine()) != null) {
				String[] l = line.split("\t");
				metaData.put(l[0], l[0]);
			}
		}
		return metaData;
	}

	private static List<UniversityRow> readData() throws IOException {
		List<UniversityRow> rows = new ArrayList<>();
		try (BufferedReader r = new BufferedReader(new InputStreamReader(
				University.class.getResourceAsStream("top100under50.txt"), Charset.forName("UTF-8")))) {
			String line;
			r.readLine();
			while ((line = r.readLine()) != null) {
				String[] l = line.split("\t");
				UniversityRow row = new UniversityRow();
				row.rank = Integer.parseInt(l[0]);
				row.institution = l[2];
				row.country = l[3];
				row.yearFounded = Integer.parseInt(l[4]);
				row.teaching = toFloat(l, 5);
				row.research = toFloat(l, 6);
				row.citations = toFloat(l, 7);
				row.incomeFromIndustry = toFloat(l, 8);
				row.internationalMix = toFloat(l, 9);
				row.overallScore = toFloat(l, 10);
				rows.add(row);
			}
		}
		return rows;
	}

	public static Field field(String f) throws NoSuchFieldException {
		return UniversityRow.class.getDeclaredField(f);
	}

	static class UniversityRow extends ARow {
		public int rank;
		public String institution;
		public String country;
		public int yearFounded;
		public float teaching;
		public float research;
		public float citations;
		public float incomeFromIndustry;
		public float internationalMix;
		public float overallScore;

		@Override
		public String toString() {
			return institution;
		}
	}

	public static void main(String[] args) {
		new University().run();
	}
}
