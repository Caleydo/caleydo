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
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.caleydo.core.view.opengl.canvas.IGLKeyListener;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLSandBox;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.vis.rank.config.RankTableConfigBase;
import org.caleydo.vis.rank.layout.RowHeightLayouts;
import org.caleydo.vis.rank.model.ARow;
import org.caleydo.vis.rank.model.CategoricalRankColumnModel;
import org.caleydo.vis.rank.model.CategoricalRankColumnModel.CategoryInfo;
import org.caleydo.vis.rank.model.FloatRankColumnModel;
import org.caleydo.vis.rank.model.PiecewiseLinearMapping;
import org.caleydo.vis.rank.model.RankRankColumnModel;
import org.caleydo.vis.rank.model.RankTableModel;
import org.caleydo.vis.rank.model.StackedRankColumnModel;
import org.caleydo.vis.rank.model.StringRankColumnModel;
import org.caleydo.vis.rank.ui.ColumnPoolUI;
import org.caleydo.vis.rank.ui.TableBodyUI;
import org.caleydo.vis.rank.ui.TableHeaderUI;

/**
 * @author Samuel Gratzl
 *
 */
public class University extends GLSandBox {

	private final RankTableModel table;

	public University() throws NoSuchFieldException, NumberFormatException, IOException {
		super(new GLElementContainer(GLLayouts.flowVertical(0)), new GLPadding(5), new Dimension(800, 600));
		this.table = new RankTableModel(new RankTableConfigBase() {
			@Override
			public boolean isInteractive() {
				return true;
			}
		});
		createModel();
		canvas.addKeyListener(new IGLKeyListener() {
			@Override
			public void keyPressed(IKeyEvent e) {
				if (e.isKey(ESpecialKey.DOWN))
					table.selectNextRow();
				else if (e.isKey(ESpecialKey.UP))
					table.selectPreviousRow();
			}

			@Override
			public void keyReleased(IKeyEvent e) {

			}
		});
		createUI();

	}

	private void createModel() throws IOException, NoSuchFieldException {
		List<UniversityRow> rows = readData();
		table.addData(rows);
		Map<String, CategoryInfo> metaData = readCountriesCategories();

		table.addColumn(eventListeners.register(new RankRankColumnModel()));
		table.addColumn(eventListeners.register(new StringRankColumnModel(GLRenderers.drawText("University", VAlign.CENTER),
				StringRankColumnModel.DFEAULT)));
		// as categorical
		table.addColumn(eventListeners.register(new CategoricalRankColumnModel<String>(GLRenderers.drawText("Country",
				VAlign.CENTER), new ReflectionData(field("country")), metaData)));
		// as string
		// table.addColumn(eventListeners.register(new StringRankColumnModel(GLRenderers.drawText("Country",
		// VAlign.CENTER),
		// new ReflectionData(field("country")))));


		table.addColumn(eventListeners.register(new StringRankColumnModel(GLRenderers.drawText("Year Founded", VAlign.CENTER),
				new ReflectionData(field("yearFounded")))));

		final StackedRankColumnModel stacked = new StackedRankColumnModel();
		table.addColumn(stacked);
		table.addColumnTo(
				stacked,
				new FloatRankColumnModel(new ReflectionFloatData(field("teaching")), GLRenderers.drawText("Teaching",
						VAlign.CENTER), Color.decode("#8DD3C7"), Color.decode("#EEEEEE"), new PiecewiseLinearMapping(0,
						100)).setWeight((float) 30 * 5));
		table.addColumnTo(
				stacked,
				new FloatRankColumnModel(new ReflectionFloatData(field("research")), GLRenderers.drawText("Research",
						VAlign.CENTER), Color.decode("#FFFFB3"), Color.decode("#EEEEEE"), new PiecewiseLinearMapping(0,
						100)).setWeight((float) 30 * 5));
		table.addColumnTo(
				stacked,
				new FloatRankColumnModel(new ReflectionFloatData(field("citations")), GLRenderers.drawText("Citations",
						VAlign.CENTER), Color.decode("#BEBADA"), Color.decode("#EEEEEE"), new PiecewiseLinearMapping(0,
						100)).setWeight((float) 30 * 5));
		table.addColumnTo(
				stacked,
				new FloatRankColumnModel(new ReflectionFloatData(field("incomeFromIndustry")), GLRenderers.drawText(
						"Income From Industry", VAlign.CENTER), Color.decode("#FB8072"), Color.decode("#EEEEEE"),
						new PiecewiseLinearMapping(0, 100)).setWeight(7.5f * 5));
		table.addColumnTo(
				stacked,
				new FloatRankColumnModel(new ReflectionFloatData(field("internationalMix")), GLRenderers.drawText(
						"International Mix", VAlign.CENTER), Color.decode("#80B1D3"), Color.decode("#EEEEEE"),
						new PiecewiseLinearMapping(0, 100)).setWeight(2.5f * 5));

		table.addColumn(new FloatRankColumnModel(new ReflectionFloatData(field("overallScore")), GLRenderers.drawText(
				"Overall Score", VAlign.CENTER), Color.decode("#ffb380"), Color.decode("#ffe6d5"),
				new PiecewiseLinearMapping(0, 100)));
	}

	private static Map<String, CategoryInfo> readCountriesCategories() throws IOException {
		Map<String, CategoryInfo> metaData = new HashMap<>();
		try (BufferedReader r = new BufferedReader(new InputStreamReader(
				University.class.getResourceAsStream("countries.txt"), Charset.forName("UTF-8")))) {
			String line;
			r.readLine();
			while ((line = r.readLine()) != null) {
				String[] l = line.split("\t");
				CategoryInfo c = new CategoryInfo(l[0], Color.decode(l[1]));
				metaData.put(c.getLabel(), c);
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

	private void createUI() {
		// visual part
		GLElementContainer root = (GLElementContainer) getRoot();
		root.add(new TableHeaderUI(table));
		root.add(new TableBodyUI(table, RowHeightLayouts.LINEAR));

		root.add(new ColumnPoolUI(table));
	}

	public static float toFloat(String[] l, int i) {
		String v = l[i];
		if (v.equalsIgnoreCase("-"))
			return Float.NaN;
		return Float.parseFloat(v);
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

	public static void main(String[] args) throws NumberFormatException, NoSuchFieldException, IOException {
		new University().run();
	}
}
