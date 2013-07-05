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
package university.mup;

import org.caleydo.core.util.color.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLSandBox;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.vis.rank.data.FloatInferrers;
import org.caleydo.vis.rank.model.ARankColumnModel;
import org.caleydo.vis.rank.model.ARow;
import org.caleydo.vis.rank.model.FloatRankColumnModel;
import org.caleydo.vis.rank.model.IRow;
import org.caleydo.vis.rank.model.OrderColumn;
import org.caleydo.vis.rank.model.RankRankColumnModel;
import org.caleydo.vis.rank.model.RankTableModel;
import org.caleydo.vis.rank.model.StringRankColumnModel;
import org.caleydo.vis.rank.model.mapping.PiecewiseMapping;

import university.mup.MeasuringUniversityPerformanceData.Entry;

import com.google.common.base.Function;

import demo.RankTableDemo;
import demo.RankTableDemo.IModelBuilder;
import demo.ReflectionFloatData;

/**
 * @author Samuel Gratzl
 *
 */
public class MeasuringUniversityPerformance implements IModelBuilder {
	@Override
	public void apply(RankTableModel table) throws Exception {
		Map<String, MeasuringUniversityPerformanceData> data = MeasuringUniversityPerformanceData.readData(2009,2008,2007,2006,2005);
		List<UniversityRow> rows = new ArrayList<>(data.size());
		for (Map.Entry<String, MeasuringUniversityPerformanceData> entry : data.entrySet()) {
			rows.add(new UniversityRow(entry.getKey(), entry.getValue()));
		}
		table.addData(rows);
		data = null;

		table.add(new RankRankColumnModel());
		table.add(new StringRankColumnModel(GLRenderers.drawText("School Name", VAlign.CENTER),
				StringRankColumnModel.DEFAULT));


		// Arrays.asList("wur2010.txt", "wur2011.txt", "wur2012.txt");
		// 2009,2008,2007,2006,2005
		MeasuringUniversityPerformanceData.addYear(table, "2009", new YearGetter(0));
		table.add(new OrderColumn());
		table.add(new RankRankColumnModel());
		MeasuringUniversityPerformanceData.addYear(table, "2008", new YearGetter(1));
		table.add(new OrderColumn());
		table.add(new RankRankColumnModel());
		MeasuringUniversityPerformanceData.addYear(table, "2007", new YearGetter(2));
		table.add(new OrderColumn());
		table.add(new RankRankColumnModel());
		MeasuringUniversityPerformanceData.addYear(table, "2006", new YearGetter(3));
		table.add(new OrderColumn());
		table.add(new RankRankColumnModel());
		MeasuringUniversityPerformanceData.addYear(table, "2005", new YearGetter(4));

		table.add(new FloatRankColumnModel(new ReflectionFloatData(UniversityRow.class.getDeclaredField("enrollment")),
				GLRenderers.drawText("Enrollment Fall 2007", VAlign.CENTER), Color.LIGHT_GRAY, Color.LIGHT_GRAY
						.brighter(), new PiecewiseMapping(0, Float.NaN), FloatInferrers.MEDIAN));
	}

	@Override
	public Iterable<? extends ARankColumnModel> createAutoSnapshotColumns(RankTableModel table, ARankColumnModel model) {
		Collection<ARankColumnModel> ms = new ArrayList<>(2);
		ms.add(new RankRankColumnModel());
		ARankColumnModel desc = find(table, "School Name");
		if (desc != null)
			ms.add(desc.clone().setCollapsed(true));
		return ms;
	}

	private static ARankColumnModel find(RankTableModel table, String name) {
		for (ARankColumnModel model : table.getColumns()) {
			if (model.getTitle().equals(name))
				return model;
		}
		return null;
	}

	static class YearGetter implements Function<IRow, Entry[]> {
		private final int year;

		public YearGetter(int year) {
			this.year = year;
		}

		@Override
		public Entry[] apply(IRow in) {
			UniversityRow r = (UniversityRow) in;
			return r.years[year];
		}
	}

	static class UniversityRow extends ARow {
		public String schoolname;
		public int enrollment;

		public Entry[][] years;

		/**
		 * @param school
		 * @param size
		 */
		public UniversityRow(String school, MeasuringUniversityPerformanceData data) {
			this.schoolname = school;
			this.years = data.getYearEntries();
			this.enrollment = data.getTotalStudentEnrollmentFall2007();
		}

		@Override
		public String toString() {
			return schoolname;
		}
	}

	public static void main(String[] args) {
		GLSandBox.main(args, RankTableDemo.class, "Measuring University Performance 2009,2008,2007,2006,2005", new MeasuringUniversityPerformance());
	}
}
