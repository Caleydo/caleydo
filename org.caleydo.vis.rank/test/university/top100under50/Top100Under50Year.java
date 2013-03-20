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

import static demo.RankTableDemo.toFloat;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;

import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.vis.rank.data.AFloatFunction;
import org.caleydo.vis.rank.data.FloatInferrers;
import org.caleydo.vis.rank.model.FloatRankColumnModel;
import org.caleydo.vis.rank.model.IRow;
import org.caleydo.vis.rank.model.RankTableModel;
import org.caleydo.vis.rank.model.StackedRankColumnModel;
import org.caleydo.vis.rank.model.mapping.PiecewiseMapping;

import com.google.common.base.Function;

/**
 * @author Samuel Gratzl
 *
 */
public class Top100Under50Year {
	public static final int COL_ranking = 0;
	public static final int COL_teaching = 1;
	public static final int COL_research = 2;
	public static final int COL_citations = 3;
	public static final int COL_incomeFromIndustry = 4;
	public static final int COL_internationalMix = 5;
	public static final int COL_overall = 6;

	private final float ranking;
	private final float teaching;
	private final float research;
	private final float citations;
	private final float incomeFromIndustry;
	private final float internationalMix;
	private final float overall;

	// 100 Under 50 rank World University Rankings 2011-2012 position Institution Lat, long Country Year founded
	// Teaching Research Citations Income from Industry International mix Overall score


	public Top100Under50Year(String[] l) {
		ranking = toFloat(l, 0);
		teaching = toFloat(l, 6);
		research = toFloat(l, 7);
		citations = toFloat(l, 8);
		incomeFromIndustry = toFloat(l, 9);
		internationalMix = toFloat(l, 10);
		overall = toFloat(l, 11);
	}

	public float get(int index) {
		switch (index) {
		case COL_ranking:
			return ranking;
		case COL_citations:
			return citations;
		case COL_incomeFromIndustry:
			return incomeFromIndustry;
		case COL_internationalMix:
			return internationalMix;
		case COL_overall:
			return overall;
		case COL_research:
			return research;
		case COL_teaching:
			return teaching;
		}
		return 0;
	}

	/**
	 * @param table
	 */
	public static StackedRankColumnModel addYear(RankTableModel table, String title,
			final Function<IRow, Top100Under50Year> map) {
		final StackedRankColumnModel stacked = new StackedRankColumnModel();
		stacked.setTitle(title);
		table.add(stacked);
		// Research: volume, income and reputation (30 per cent)
		// Citations: research influence (30 per cent)
		// Teaching: the learning environment (30 per cent)
		// International outlook: people and research (7.5 per cent)
		// Industry income: innovation (2.5 per cent).
		stacked.add(col(map, COL_research, "Research", "#FC9272", "#FEE0D2"));
		stacked.add(col(map, COL_citations, "Citations", "#9ECAE1", "#DEEBF7"));
		stacked.add(col(map, COL_teaching, "Teaching", "#A1D99B", "#E5F5E0"));
		stacked.add(col(map, COL_internationalMix, "International outlook", "#C994C7", "#E7E1EF"));
		stacked.add(col(map, COL_incomeFromIndustry, "Industry income", "#FDBB84", "#FEE8C8"));

		stacked.setDistributions(new float[] { 30, 30, 30, 7.5f, 2.5f });
		stacked.setWidth(300);

		return stacked;
	}

	public static void addOverallYear(RankTableModel table, String title, Function<IRow, Top100Under50Year> map) {
		table.add(col(map, COL_overall, title, "#DFC27D", "#F6E8C3"));
	}

	private static FloatRankColumnModel col(Function<IRow, Top100Under50Year> year, int col, String text,
			String color, String bgColor) {
		return new FloatRankColumnModel(new ValueGetter(year, col), GLRenderers.drawText(text, VAlign.CENTER),
				Color.decode(color), Color.decode(bgColor), percentage(), FloatInferrers.MEDIAN);
	}

	protected static PiecewiseMapping percentage() {
		return new PiecewiseMapping(0, 100);
	}

	public static Map<String, Row> readData(int... years) throws IOException {
		Map<String, Row> data = new LinkedHashMap<>();
		for (int i = 0; i < years.length; ++i) {
			String year = String.format("THE100Under50rankings%4d.txt", years[i]);
			// 100 Under 50 rank World University Rankings 2011-2012 position Institution Lat, long Country Year founded
			// Teaching Research Citations Income from Industry International mix Overall score

			try (BufferedReader r = new BufferedReader(new InputStreamReader(
					Top100Under50Year.class.getResourceAsStream(year), Charset.forName("UTF-8")))) {
				String line;
				r.readLine(); // header
				while ((line = r.readLine()) != null) {
					String[] l = line.split("\t");
					String school = l[2];
					String location = l[3];
					String country = l[4];
					int yearFounded = Integer.parseInt(l[5]);

					Top100Under50Year universityYear = new Top100Under50Year(l);
					if (!data.containsKey(school)) {
						Row row = new Row(country, location, yearFounded, new Top100Under50Year[years.length]);
						data.put(school, row);
					}
					data.get(school).years[i] = universityYear;
				}
			}
		}
		return data;
	}

	static class ValueGetter extends AFloatFunction<IRow> {
		private final int subindex;
		private final Function<IRow, Top100Under50Year> year;

		public ValueGetter(Function<IRow, Top100Under50Year> year, int column) {
			this.year = year;
			this.subindex = column;
		}

		@Override
		public float applyPrimitive(IRow in) {
			Top100Under50Year y = year.apply(in);
			if (y == null)
				return Float.NaN;
			return y.get(subindex);
		}
	}

	public static class Row {

		public String country;
		public String location;
		public int yearFounded;

		public Top100Under50Year[] years;

		public Row(String country, String location, int yearFounded2, Top100Under50Year[] top100Under50Years) {
			this.country = country;
			this.location = location;
			this.yearFounded = yearFounded2;
			this.years = top100Under50Years;
		}

	}
}
