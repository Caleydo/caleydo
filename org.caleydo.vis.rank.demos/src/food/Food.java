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
package food;

import static demo.RankTableDemo.toFloat;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLSandBox;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.vis.rank.data.AFloatFunction;
import org.caleydo.vis.rank.data.FloatInferrers;
import org.caleydo.vis.rank.data.IFloatSetterFunction;
import org.caleydo.vis.rank.model.ARow;
import org.caleydo.vis.rank.model.CategoricalRankColumnModel;
import org.caleydo.vis.rank.model.FloatRankColumnModel;
import org.caleydo.vis.rank.model.IRow;
import org.caleydo.vis.rank.model.RankRankColumnModel;
import org.caleydo.vis.rank.model.RankTableModel;
import org.caleydo.vis.rank.model.StringRankColumnModel;
import org.caleydo.vis.rank.model.mapping.PiecewiseMapping;

import demo.RankTableDemo;
import demo.RankTableDemo.IModelBuilder;
import demo.ReflectionData;

/**
 * @author Samuel Gratzl
 *
 */
public class Food implements IModelBuilder {
	private static final List<String> headers = Arrays.asList("Water (g/100 g)", "Food energy (kcal/100 g)",
			"Protein (g/100 g)", "Total lipid (fat)(g/100 g)", "Ash (g/100 g)",
			"Carbohydrate, by difference (g/100 g)", "Total dietary fiber (g/100 g)", "Total sugars (g/100 g)",
			"Calcium (mg/100 g)", "Iron (mg/100 g)", "Magnesium (mg/100 g)", "Phosphorus (mg/100 g)",
			"Potassium (mg/100 g)", "Sodium (mg/100 g)", "Zinc (mg/100 g)", "Copper (mg/100 g)",
			"Manganese (mg/100 g)", "Selenium (µg/100 g)", "Vitamin C (mg/100 g)", "Thiamin (mg/100 g)",
			"Riboflavin (mg/100 g)", "Niacin (mg/100 g)", "Pantothenic acid (mg/100 g)", "Vitamin B6 (mg/100 g)",
			"Folate, total (µg/100 g)", "Folic acid (µg/100 g)", "Food folate (µg/100 g)",
			"Folate (µg dietary folate equivalents/100 g)", "Choline, total (mg/100 g)", "Vitamin B12 (µg/100 g)",
			"Vitamin A (IU/100 g)", "Vitamin A (µg retinol activity equivalents/100g)", "Retinol (µg/100 g)",
			"Alpha-carotene (µg/100 g)", "Beta-carotene (µg/100 g)", "Beta-cryptoxanthin (µg/100 g)",
			"Lycopene (µg/100 g)", "Lutein+zeazanthin (µg/100 g)", "Vitamin E (alpha-tocopherol) (mg/100 g)",
			"Vitamin D (µg/100 g)", "Vitamin D (IU/100 g)", "Vitamin K (phylloquinone) (µg/100 g)",
			"Saturated fatty acid (g/100 g)", "Monounsaturated fatty acids (g/100 g)",
			"Polyunsaturated fatty acids (g/100 g)", "Cholesterol (mg/100 g)");

	private static final List<Pair<Color, Color>> colors = Arrays.asList(colors("#FC9272", "#FEE0D2"),
			colors("#9ECAE1", "#DEEBF7"), colors("#A1D99B", "#E5F5E0"), colors("#C994C7", "#E7E1EF"),
			colors("#FDBB84", "#FEE8C8"), colors("#DFC27D", "#F6E8C3"));

	private static final List<String> selection = Arrays.asList("Food energy (kcal/100 g)",/**/
			"Total lipid (fat)(g/100 g)",/**/
			"Saturated fatty acid (g/100 g)",/**/
			"Cholesterol (mg/100 g)",/**/
			"Sodium (mg/100 g)",/**/
			"Carbohydrate, by difference (g/100 g)",/**/
			"Total dietary fiber (g/100 g)", "Total sugars (g/100 g)",/**/
			"Protein (g/100 g)", /**/
			"Vitamin A (IU/100 g)", "Vitamin C (mg/100 g)", "Calcium (mg/100 g)", "Iron (mg/100 g)");

	private static final int[] selectionColors = { 0,/**/
	0, /**/
	1, /**/
	2, /**/
	5, /**/
	3, /**/
	4, 4,/**/
	0,/**/
	5, 5, 5, 5 };

	private static final List<String> pool = Arrays.asList(/**/
	"Magnesium (mg/100 g)", "Phosphorus (mg/100 g)",/**/
			"Monounsaturated fatty acids (g/100 g)", "Polyunsaturated fatty acids (g/100 g)");
	private static final int[] poolColors = { 5, 5, 1, 1 };

	@Override
	public void apply(RankTableModel table) throws Exception {
		List<FoodRow> rows = new ArrayList<>();
		Map<Integer, String> foodGroups = readData(rows);
		table.addData(rows);

		table.add(new RankRankColumnModel());
		// table.add(new CategoricalRankColumnModel<String>(GLRenderers.drawText("Short Description", VAlign.CENTER),
		// new Function<IRow, Set<String>>() {
		// @Override
		// public Set<String> apply(IRow in) {
		// return ((FoodRow)in).Shrt_Desc;
		// }
		// }, metaData));
		table.add(new StringRankColumnModel(GLRenderers.drawText("Description", VAlign.CENTER),
				StringRankColumnModel.DEFAULT));
		table.add(new CategoricalRankColumnModel<Integer>(GLRenderers.drawText("Food Group", VAlign.CENTER),
				new ReflectionData<Integer>(field("group"), Integer.class), foodGroups));


		for (int i = 0; i < selection.size(); ++i) {
			int j = headers.indexOf(selection.get(i));
			table.add(ucol(j, headers.get(j), selectionColors[i]));
		}

		for (int i = 0; i < pool.size(); ++i) {
			int j = headers.indexOf(pool.get(i));
			FloatRankColumnModel c = ucol(j, headers.get(j), poolColors[i]);
			table.add(c);
			c.hide();
		}

	}

	private static Pair<Color, Color> colors(String c1, String c2) {
		return Pair.make(Color.decode(c1), Color.decode(c2));
	}

	protected static Map<Integer, String> readData(List<FoodRow> rows) throws IOException {
		Map<Integer, String> foodGroups = readFoodGroups();

		try (BufferedReader r = new BufferedReader(new InputStreamReader(
				Food.class.getResourceAsStream("FOOD_DES.txt"), Charset.forName("UTF-8")))) {
			String line;
			while ((line = r.readLine()) != null) {
				String[] l = line.split("\t");
				FoodRow row = new FoodRow();
				// row.rank = Integer.parseInt(l[0]);
				row.NDB_No = Integer.parseInt(l[0]);
				row.group = Integer.parseInt(l[1]);
				row.description = l[2];
				rows.add(row);
			}
		}

		try (BufferedReader r = new BufferedReader(new InputStreamReader(Food.class.getResourceAsStream("ABBREV.csv"),
				Charset.forName("UTF-8")))) {
			String line;
			r.readLine(); // skip label
			for(int i = 0; (line = r.readLine()) != null; ++i) {
				String[] l = line.split("\t");
				FoodRow row = rows.get(i);
				row.NDB_No = Integer.parseInt(l[0]);
				row.data = new float[l.length - 2 - 5];
				for (int i1 = 0; i1 < row.data.length; ++i1) {
					row.data[i1] = toFloat(l, i1 + 2);
				}
			}
		}
		return foodGroups;
	}

	public static void dump() throws IOException {
		List<FoodRow> rows = new ArrayList<>();
		Map<Integer, String> foodGroups = readData(rows);

		final char SEP = '\t';
		try (PrintWriter w = new PrintWriter(new File("food_summary.csv"), "UTF-8")) {
			w.append("Description").append(SEP).append("Food Group");

			for (int i = 0; i < selection.size(); ++i) {
				w.append(SEP).append(selection.get(i));
			}
			for (int i = 0; i < pool.size(); ++i) {
				w.append(SEP).append(pool.get(i));
			}
			w.println();

			for (FoodRow row : rows) {
				w.append(row.description).append(SEP).append(foodGroups.get(row.group));

				for (int i = 0; i < selection.size(); ++i) {
					int j = headers.indexOf(selection.get(i));
					w.append(SEP).append(row.data.length <= j ? "" : toString(row.data[j]));
				}
				for (int i = 0; i < pool.size(); ++i) {
					int j = headers.indexOf(pool.get(i));
					w.append(SEP).append(row.data.length <= j ? "" : toString(row.data[j]));
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

	/**
	 * @param string
	 * @return
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 */
	private static Field field(String name) throws NoSuchFieldException, SecurityException {
		return FoodRow.class.getDeclaredField(name);
	}

	/**
	 * @return
	 * @throws IOException
	 * @throws NumberFormatException
	 */
	private static Map<Integer, String> readFoodGroups() throws NumberFormatException, IOException {
		Map<Integer, String> result = new HashMap<>();
		try (BufferedReader r = new BufferedReader(new InputStreamReader(Food.class.getResourceAsStream("FD_GROUP.txt"),
				Charset.forName("UTF-8")))) {
			String line;
			while ((line = r.readLine()) != null) {
				String[] l = line.split("\t");
				Integer id = Integer.parseInt(l[0]);
				result.put(id, l[1]);
			}
		}
		return result;
	}

	private FloatRankColumnModel ucol(int col, String label, int colorIndex) {
		Pair<Color, Color> pair = colors.get(colorIndex);
		FloatRankColumnModel f = new FloatRankColumnModel(new ValueGetter(col), GLRenderers.drawText(label,
				VAlign.CENTER), pair.getFirst(), pair.getSecond(), new PiecewiseMapping(0, Float.NaN),
				FloatInferrers.fix(0));
		f.setWidth(75);
		return f;
	}

	static class ValueGetter extends AFloatFunction<IRow> implements IFloatSetterFunction<IRow> {
		private final int index;

		public ValueGetter(int column) {
			this.index = column;
		}

		@Override
		public float applyPrimitive(IRow in) {
			FoodRow r = (FoodRow) in;
			if (r.data.length <= index)
				return 0;
			return r.data[index];
		}

		@Override
		public void set(IRow in, float value) {
			FoodRow r = (FoodRow) in;
			if (r.data.length <= index)
				return;
			r.data[index] = value;
		}

	}

	static class FoodRow extends ARow {
		public int NDB_No;
		public int group;
		public String description;
		public float[] data;

		@Override
		public String toString() {
			return description;
		}
	}

	public static void main(String[] args) {
		// dump();
		GLSandBox.main(args, RankTableDemo.class, "Food Nutrition", new Food());
	}
}
