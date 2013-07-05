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
package nasatxl;

import static demo.RankTableDemo.toFloat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLSandBox;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.vis.rank.data.FloatInferrers;
import org.caleydo.vis.rank.model.ARankColumnModel;
import org.caleydo.vis.rank.model.ARow;
import org.caleydo.vis.rank.model.CategoricalRankColumnModel;
import org.caleydo.vis.rank.model.FloatRankColumnModel;
import org.caleydo.vis.rank.model.RankRankColumnModel;
import org.caleydo.vis.rank.model.RankTableModel;
import org.caleydo.vis.rank.model.mapping.PiecewiseMapping;

import demo.RankTableDemo;
import demo.RankTableDemo.IModelBuilder;
import demo.ReflectionData;
import demo.ReflectionFloatData;

/**
 * @author Samuel Gratzl
 *
 */
public class NASATxlResults implements IModelBuilder {

	@Override
	public void apply(RankTableModel table) throws Exception {
		List<NASATxlTest> rows = readData();
		table.addData(rows);

		Map<Integer, String> taskMetaData = new LinkedHashMap<>();
		for (int i = 1; i <= 13; ++i)
			taskMetaData.put(i, String.format("Task %d", i));

		table.add(new CategoricalRankColumnModel<Integer>(GLRenderers.drawText("Task", VAlign.CENTER),
				new ReflectionData<>(field("task"), Integer.class), taskMetaData));

		ReflectionFloatData data = new ReflectionFloatData(field("time"));
		FloatRankColumnModel f = new FloatRankColumnModel(data, GLRenderers.drawText("Execution Time (s)",
				VAlign.CENTER), new Color("#FFD92F"), new Color("#FFFFCC"), new PiecewiseMapping(0, Float.NaN),
				FloatInferrers.MEDIAN);
		f.setWidth(150);
		table.add(f);

		table.add(col(
				"mental_demand",
				"Mental Demand\nHow much mental and perceptual activity was required? Was the task easy or demanding, simple or complex?",
				"#FC9272", "#FEE0D2"));
		table.add(col(
				"physical_demand",
				"Physical Demand\nHow much physical activity was required? Was the task easy or demanding, slack or strenuous?",
				"#9ECAE1", "#DEEBF7"));
		table.add(col(
				"temporal_demand",
				"Temporal Demand\nHow much time pressure did you feel due to the pace at which the tasks or task elements occurred? Was the pace slow or rapid?",
				"#A1D99B", "#E5F5E0"));
		table.add(col(
				"performance",
				"Overall Performance\nHow successful were you in performing the task? How satisfied were you with your performance?",
				"#C994C7", "#E7E1EF"));
		table.add(col(
				"effort",
				"Effort\nHow hard did you have to work (mentally and physically) to accomplish your level of performance?",
				"#FDBB84", "#FEE8C8"));
		table.add(col(
				"frustration",
				"Frustration Level\nHow irritated, stresses, and annoyed versus content, relaxed, and complacent did you feel during the task?",
				"#DFC27D", "#F6E8C3"));
	}

	@Override
	public Iterable<? extends ARankColumnModel> createAutoSnapshotColumns(RankTableModel table, ARankColumnModel model) {
		Collection<ARankColumnModel> ms = new ArrayList<>(2);
		ms.add(new RankRankColumnModel());
		ARankColumnModel desc = find(table, "Task");
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

	protected static List<NASATxlTest> readData() throws IOException {
		List<NASATxlTest> rows = new ArrayList<>();
		try (BufferedReader r = new BufferedReader(new InputStreamReader(
				NASATxlResults.class.getResourceAsStream("nasaresult.csv"), Charset.forName("UTF-8")))) {
			String line;
			r.readLine();
			while ((line = r.readLine()) != null) {
				String[] l = line.split("\t");
				NASATxlTest row = new NASATxlTest();
				// row.rank = Integer.parseInt(l[0]);
				row.task = Integer.parseInt(l[0]);
				row.time = toFloat(l, 2);
				row.mental_demand = toFloat(l, 3);
				row.physical_demand = toFloat(l, 4);
				row.temporal_demand = toFloat(l, 5);
				row.performance = toFloat(l, 6);
				row.effort = toFloat(l, 7);
				row.frustration = toFloat(l, 8);
				rows.add(row);
			}
		}
		return rows;
	}

	private static Field field(String name) throws NoSuchFieldException, SecurityException {
		return NASATxlTest.class.getDeclaredField(name);
	}

	private FloatRankColumnModel col(String field, String label, String color, String bgColor)
			throws NoSuchFieldException, SecurityException {
		ReflectionFloatData data = new ReflectionFloatData(field(field));
		FloatRankColumnModel f = new FloatRankColumnModel(data, GLRenderers.drawText(label, VAlign.CENTER),
 new Color(
				color), new Color(bgColor), mapping(), FloatInferrers.MEDIAN);
		f.setWidth(150);
		return f;
	}

	protected PiecewiseMapping mapping() {
		PiecewiseMapping m = new PiecewiseMapping(0, 100);
		return m;
	}

	static class NASATxlTest extends ARow {
		public int task;
		public float mental_demand;
		public float physical_demand;
		public float temporal_demand;
		public float performance;
		public float effort;
		public float frustration;
		public float time;

		@Override
		public String toString() {
			return "Task " + task;
		}
	}

	public static void main(String[] args) {
		// dump();
		GLSandBox.main(args, RankTableDemo.class, "UserStudy NASATxlResults", new NASATxlResults());
	}
}
