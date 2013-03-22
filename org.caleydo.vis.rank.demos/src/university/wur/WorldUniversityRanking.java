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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLSandBox;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.vis.rank.model.ARow;
import org.caleydo.vis.rank.model.IRow;
import org.caleydo.vis.rank.model.OrderColumn;
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
public class WorldUniversityRanking implements IModelBuilder {
	@Override
	public void apply(RankTableModel table) throws Exception {
		// qsrank schoolname qsstars overall academic employer faculty international internationalstudents citations
		// arts engineering life natural social

		Map<String, WorldUniversityYear[]> data = WorldUniversityYear.readData(2012, 2011, 2010, 2009, 2008, 2007);
		List<UniversityRow> rows = new ArrayList<>(data.size());
		for (Map.Entry<String, WorldUniversityYear[]> entry : data.entrySet()) {
			rows.add(new UniversityRow(entry.getKey(), entry.getValue()));
		}
		table.addData(rows);
		data = null;

		table.add(new RankRankColumnModel());
		table.add(new StringRankColumnModel(GLRenderers.drawText("School Name", VAlign.CENTER),
				StringRankColumnModel.DEFAULT));

		// Arrays.asList("wur2010.txt", "wur2011.txt", "wur2012.txt");
		WorldUniversityYear.addYear(table, "2012", new YearGetter(0), true).orderByMe();
		table.add(new OrderColumn());
		table.add(new RankRankColumnModel());
		WorldUniversityYear.addYear(table, "2011", new YearGetter(1), false);
		table.add(new OrderColumn());
		table.add(new RankRankColumnModel());
		WorldUniversityYear.addYear(table, "2010", new YearGetter(2), false);
		table.add(new OrderColumn());
		table.add(new RankRankColumnModel());
		WorldUniversityYear.addYear(table, "2009", new YearGetter(3), false);
		table.add(new OrderColumn());
		table.add(new RankRankColumnModel());
		WorldUniversityYear.addYear(table, "2008", new YearGetter(4), false).setCompressed(true);
		table.add(new OrderColumn());
		table.add(new RankRankColumnModel());
		WorldUniversityYear.addYear(table, "2007", new YearGetter(5), false).setCompressed(true);
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

		public WorldUniversityYear[] years;

		/**
		 * @param school
		 * @param size
		 */
		public UniversityRow(String school, WorldUniversityYear[] years) {
			this.schoolname = school;
			this.years = years;
		}

		@Override
		public String toString() {
			return schoolname;
		}
	}

	public static void main(String[] args) {
		GLSandBox.main(args, RankTableDemo.class, "world university ranking 2012,2011 and 2010",
				new WorldUniversityRanking());
	}
}
