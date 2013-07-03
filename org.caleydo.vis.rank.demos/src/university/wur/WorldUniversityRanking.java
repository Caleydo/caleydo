/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLSandBox;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.vis.rank.model.ARankColumnModel;
import org.caleydo.vis.rank.model.ARow;
import org.caleydo.vis.rank.model.CategoricalRankColumnModel;
import org.caleydo.vis.rank.model.IRow;
import org.caleydo.vis.rank.model.OrderColumn;
import org.caleydo.vis.rank.model.RankRankColumnModel;
import org.caleydo.vis.rank.model.RankTableModel;
import org.caleydo.vis.rank.model.StackedRankColumnModel;
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

		List<UniversityRow> rows = new ArrayList<>(data.size());
		for (Map.Entry<String, WorldUniversityYear[]> entry : data.entrySet()) {
			rows.add(new UniversityRow(entry.getKey(), entry.getValue(), countries.get(entry.getKey())));
		}
		table.addData(rows);
		data = null;

		table.add(new RankRankColumnModel());
		final ARankColumnModel label = new StringRankColumnModel(GLRenderers.drawText("School Name", VAlign.CENTER),
				StringRankColumnModel.DEFAULT).setWidth(300);
		table.add(label);

		CategoricalRankColumnModel<String> cat = CategoricalRankColumnModel
				.createSimple(GLRenderers.drawText("Country",
				VAlign.CENTER), new ReflectionData<>(UniversityRow.class.getDeclaredField("country"), String.class),
						countries.values());
		table.add(cat);

		int rankColWidth = 40;

		// Arrays.asList("wur2010.txt", "wur2011.txt", "wur2012.txt");
		WorldUniversityYear.addYear(table, "2012", new YearGetter(0), false, false).orderByMe();

		WorldUniversityYear.addSpecialYear(table, new YearGetter(0));

		addYear(label, rankColWidth, table, "2011", new YearGetter(1)).setCompressed(true);
		addYear(label, rankColWidth, table, "2010", new YearGetter(2)).setCompressed(true);
		addYear(label, rankColWidth, table, "2009", new YearGetter(3)).setCollapsed(true);
		addYear(label, rankColWidth, table, "2008", new YearGetter(4)).setCollapsed(true);
		addYear(label, rankColWidth, table, "2007", new YearGetter(5)).setCollapsed(true);
	}

	private static StackedRankColumnModel addYear(ARankColumnModel label, int rankColWidth, RankTableModel table,
			String title, YearGetter year) {
		table.add(new OrderColumn());
		table.add(new RankRankColumnModel().setWidth(rankColWidth));
		table.add(label.clone().setCollapsed(true));
		StackedRankColumnModel model = WorldUniversityYear.addYear(table, title, year, false, false);
		model.orderByMe();
		return model;
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
