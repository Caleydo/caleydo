/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package university.top100under50;

import org.caleydo.core.util.color.Color;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLSandBox;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.vis.rank.data.FloatInferrers;
import org.caleydo.vis.rank.model.ARankColumnModel;
import org.caleydo.vis.rank.model.ARow;
import org.caleydo.vis.rank.model.CategoricalRankColumnModel;
import org.caleydo.vis.rank.model.IRow;
import org.caleydo.vis.rank.model.IntegerRankColumnModel;
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
				StringRankColumnModel.DEFAULT).setWidth(200));

		table.add(new CategoricalRankColumnModel<String>(GLRenderers.drawText("Country", VAlign.CENTER),
				new ReflectionData<String>(field("country"), String.class), metaData));

		table.add(new IntegerRankColumnModel(GLRenderers.drawText("Year Founded", VAlign.CENTER),
				new ReflectionData<Integer>(field("yearFounded"), Integer.class), Color.GRAY, new Color(.95f, .95f,
						.95f), null));

		table.add(new StringRankColumnModel(GLRenderers.drawText("Location", VAlign.CENTER),
				new ReflectionData<String>(field("location"), String.class)));

		Top100Under50Year.addYear(table, "2012", new YearGetter(0), FloatInferrers.MEDIAN);

		// table.add(new OrderColumn());
		// table.add(new RankRankColumnModel());
		// Top100Under50Year.addOverallYear(table, "Overall Score 2012", new YearGetter(0));

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
