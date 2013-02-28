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
import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLSandBox;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.view.tourguide.v3.config.RankTableConfigBase;
import org.caleydo.view.tourguide.v3.layout.RowHeightLayouts;
import org.caleydo.view.tourguide.v3.model.ARow;
import org.caleydo.view.tourguide.v3.model.FloatRankColumnModel;
import org.caleydo.view.tourguide.v3.model.PiecewiseLinearMapping;
import org.caleydo.view.tourguide.v3.model.RankRankColumnModel;
import org.caleydo.view.tourguide.v3.model.RankTableModel;
import org.caleydo.view.tourguide.v3.model.StackedRankColumnModel;
import org.caleydo.view.tourguide.v3.model.StringRankColumnModel;
import org.caleydo.view.tourguide.v3.ui.ColumnPoolUI;
import org.caleydo.view.tourguide.v3.ui.TableBodyUI;
import org.caleydo.view.tourguide.v3.ui.TableHeaderUI;

/**
 * @author Samuel Gratzl
 *
 */
public class University extends GLElementContainer {

	private final RankTableModel table;

	public University(RankTableModel table) {
		this.table = table;
		setLayout(GLLayouts.flowVertical(0));

		this.add(new TableHeaderUI(table));
		this.add(new TableBodyUI(table, RowHeightLayouts.LINEAR));

		this.add(new ColumnPoolUI(table));
	}

	public static void main(String[] args) throws NumberFormatException, NoSuchFieldException, IOException {

		RankTableModel table = new RankTableModel(new RankTableConfigBase() {
			@Override
			public boolean isInteractive() {
				return true;
			}
		});
		table.addColumn(new RankRankColumnModel());
		table.addColumn(new StringRankColumnModel(GLRenderers.drawText("University", VAlign.CENTER),
				StringRankColumnModel.DFEAULT));
		table.addColumn(new StringRankColumnModel(GLRenderers.drawText("Country", VAlign.CENTER), new ReflectionData(
				field("country"))));

		final StackedRankColumnModel stacked = new StackedRankColumnModel();
		table.addColumn(stacked);

		table.addColumn(new StringRankColumnModel(GLRenderers.drawText("Year Founded", VAlign.CENTER),
				new ReflectionData(field("yearFounded"))));
		table.addColumn(new FloatRankColumnModel(new ReflectionFloatData(field("overallScore")), GLRenderers.drawText("Overall Score", VAlign.CENTER), Color
				.decode("#ffb380"), Color.decode("#ffe6d5"), new PiecewiseLinearMapping(0, 100)));

		table.addColumnTo(stacked, column("Teaching", "teaching", 30));
		table.addColumnTo(stacked, column("Research", "research", 30));
		table.addColumnTo(stacked, column("Citations", "citations", 30));
		table.addColumnTo(stacked, column("Income From Industry", "incomeFromIndustry", 7.5f));
		table.addColumnTo(stacked, column("International Mix", "internationalMix", 2.5f));

		List<UniversityRow> rows = new ArrayList<>();
		try (BufferedReader r = new BufferedReader(new InputStreamReader(
				University.class.getResourceAsStream("top100under50.txt")))) {
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
		table.addData(rows);
		GLSandBox.main(args, new University(table), new GLPadding(5));
	}

	public static float toFloat(String[] l, int i) {
		String v = l[i];
		if (v.equalsIgnoreCase("–"))
			return Float.NaN;
		return Float.parseFloat(v);
	}

	public static FloatRankColumnModel column(String name, String field, float weight) throws NumberFormatException,
			NoSuchFieldException {
		FloatRankColumnModel f = new FloatRankColumnModel(new ReflectionFloatData(field(field)),
				GLRenderers.drawText(name,
 VAlign.CENTER), Color.decode("#5fd3bc"), Color.decode("#d5fff6"), new PiecewiseLinearMapping(0,
				100));
		f.setWeight(weight * 5);
		return f;
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
}
