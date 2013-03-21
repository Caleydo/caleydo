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
package university.top100under50;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLSandBox;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.vis.rank.data.FloatInferrers;
import org.caleydo.vis.rank.model.ARow;
import org.caleydo.vis.rank.model.CategoricalRankColumnModel;
import org.caleydo.vis.rank.model.IRow;
import org.caleydo.vis.rank.model.IntegerRankColumnModel;
import org.caleydo.vis.rank.model.OrderColumn;
import org.caleydo.vis.rank.model.RankRankColumnModel;
import org.caleydo.vis.rank.model.RankTableModel;
import org.caleydo.vis.rank.model.StringRankColumnModel;

import university.top100under50.Top100Under50Year.Row;

import com.google.common.base.Function;

import demo.RankTableDemo;
import demo.RankTableDemo.IModelBuilder;
import demo.ReflectionData;

/**
 * @author Samuel Gratzl
 *
 */
public class Top100Under50 implements IModelBuilder {
	@Override
	public void apply(RankTableModel table) throws Exception {
		// qsrank schoolname qsstars overall academic employer faculty international internationalstudents citations
		// arts engineering life natural social

		Map<String, String> metaData = new HashMap<>();
		Map<String, Row> data = Top100Under50Year.readData(2012);
		List<UniversityRow> rows = new ArrayList<>(data.size());
		for (Map.Entry<String, Row> entry : data.entrySet()) {
			rows.add(new UniversityRow(entry.getKey(), entry.getValue()));
			metaData.put(entry.getValue().country, entry.getValue().country);
		}
		table.addData(rows);
		data = null;

		table.add(new RankRankColumnModel());
		table.add(new StringRankColumnModel(GLRenderers.drawText("School Name", VAlign.CENTER),
				StringRankColumnModel.DEFAULT));

		table.add(new CategoricalRankColumnModel<String>(GLRenderers.drawText("Country", VAlign.CENTER),
				new ReflectionData<String>(field("country"), String.class), metaData));

		table.add(new IntegerRankColumnModel(GLRenderers.drawText("Year Founded", VAlign.CENTER),
				new ReflectionData<Integer>(field("yearFounded"), Integer.class), Color.GRAY, new Color(.95f, .95f,
						.95f), null));

		table.add(new StringRankColumnModel(GLRenderers.drawText("Location", VAlign.CENTER),
				new ReflectionData<String>(field("location"), String.class)));

		Top100Under50Year.addYear(table, "2012", new YearGetter(0), FloatInferrers.MEDIAN).orderByMe();

		table.add(new OrderColumn());
		table.add(new RankRankColumnModel());
		Top100Under50Year.addOverallYear(table, "Overall Score 2012", new YearGetter(0));

	}

	public static Field field(String f) throws NoSuchFieldException {
		return UniversityRow.class.getDeclaredField(f);
	}

	static class YearGetter implements Function<IRow, Top100Under50Year> {
		private final int year;

		public YearGetter(int year) {
			this.year = year;
		}

		@Override
		public Top100Under50Year apply(IRow in) {
			UniversityRow r = (UniversityRow) in;
			return r.years[year];
		}
	}

	static class UniversityRow extends ARow {
		public String schoolname;
		public String country;
		public String location;
		public int yearFounded;

		public Top100Under50Year[] years;

		/**
		 * @param school
		 * @param size
		 */
		public UniversityRow(String school, Row row) {
			this.schoolname = school;
			this.years = row.years;
			this.country = row.country;
			this.yearFounded = row.yearFounded;
			this.location = row.location;
		}

		@Override
		public String toString() {
			return schoolname;
		}
	}

	public static void main(String[] args) {
		GLSandBox.main(args, RankTableDemo.class, "Top 100 under 50 2012",
				new Top100Under50());
	}
}
