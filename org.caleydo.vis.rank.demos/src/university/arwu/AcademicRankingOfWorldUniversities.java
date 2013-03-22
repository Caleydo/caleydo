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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLSandBox;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.vis.rank.data.FloatInferrers;
import org.caleydo.vis.rank.model.ARow;
import org.caleydo.vis.rank.model.FloatRankColumnModel;
import org.caleydo.vis.rank.model.IRow;
import org.caleydo.vis.rank.model.OrderColumn;
import org.caleydo.vis.rank.model.RankRankColumnModel;
import org.caleydo.vis.rank.model.RankTableModel;
import org.caleydo.vis.rank.model.StringRankColumnModel;
import org.caleydo.vis.rank.model.mapping.PiecewiseMapping;

import university.endowments.EndowmentsYear;

import com.google.common.base.Function;

import demo.RankTableDemo;
import demo.RankTableDemo.IModelBuilder;
import demo.ReflectionFloatData;

/**
 * @author Samuel Gratzl
 *
 */
public class AcademicRankingOfWorldUniversities implements IModelBuilder {
	@Override
	public void apply(RankTableModel table) throws Exception {
		// ranking institution country national total alumini award hici nands pub pcb

		Map<String, Pair<String, AcademicUniversityYear[]>> data = AcademicUniversityYear.readData(2010, 2011, 2012);
		Map<String, int[]> endowments = EndowmentsYear.readData();
		List<UniversityRow> rows = new ArrayList<>(data.size());
		for(Map.Entry<String, Pair<String, AcademicUniversityYear[]>> entry : data.entrySet()) {
			UniversityRow r = new UniversityRow(entry.getKey(), entry.getValue().getFirst(), entry.getValue().getSecond());
			if (endowments.containsKey(entry.getKey())) {
				r.endowments2011 = endowments.get(entry.getKey())[0];
				r.endowments2012 = endowments.get(entry.getKey())[1];
			}
			rows.add(r);
		}
		table.addData(rows);
		data = null;

		table.add(new RankRankColumnModel());
		table.add(new StringRankColumnModel(GLRenderers.drawText("Institution", VAlign.CENTER),
				StringRankColumnModel.DEFAULT));


		// Arrays.asList("argu2010.txt", "argu2011.txt", "argu2012.txt");
		table.orderBy(AcademicUniversityYear.addYear(table, "2012", new YearGetter(2)));

		table.add(new FloatRankColumnModel(new ReflectionFloatData(UniversityRow.class.getDeclaredField("endowments2012")),GLRenderers.drawText("Endowment 2012", VAlign.CENTER),
				Color.LIGHT_GRAY, new Color(0.95f, 0.95f, .95f), new PiecewiseMapping(0, Float.NaN),
				FloatInferrers.MEAN));
		table.add(new OrderColumn());
		table.add(new RankRankColumnModel());
		AcademicUniversityYear.addYear(table, "2011", new YearGetter(1));
		table.add(new FloatRankColumnModel(new ReflectionFloatData(UniversityRow.class
				.getDeclaredField("endowments2011")), GLRenderers.drawText("Endowment 2011", VAlign.CENTER),
				Color.LIGHT_GRAY, new Color(0.95f, 0.95f, .95f), new PiecewiseMapping(0, Float.NaN),
				FloatInferrers.MEAN));
		table.add(new OrderColumn());
		table.add(new RankRankColumnModel());
		AcademicUniversityYear.addYear(table, "2010", new YearGetter(0));
	}

	static class YearGetter implements Function<IRow, AcademicUniversityYear> {
		private final int year;

		public YearGetter(int year) {
			this.year = year;
		}

		@Override
		public AcademicUniversityYear apply(IRow in) {
			UniversityRow r = (UniversityRow) in;
			return r.years[year];
		}
	}

	static class UniversityRow extends ARow {
		public int endowments2011;
		public int endowments2012;
		public final String schoolname;
		public final String country;
		public AcademicUniversityYear[] years;

		public UniversityRow(String school, String country, AcademicUniversityYear[] years) {
			this.schoolname = school;
			this.country = country;
			this.years = years;
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
		GLSandBox.main(args, RankTableDemo.class, "academic ranking of world universities 2012,2011 and 2010", new AcademicRankingOfWorldUniversities());
	}
}
