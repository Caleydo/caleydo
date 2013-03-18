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
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLSandBox;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.vis.rank.data.AFloatFunction;
import org.caleydo.vis.rank.data.FloatInferrers;
import org.caleydo.vis.rank.model.ARow;
import org.caleydo.vis.rank.model.FloatRankColumnModel;
import org.caleydo.vis.rank.model.IRow;
import org.caleydo.vis.rank.model.RankRankColumnModel;
import org.caleydo.vis.rank.model.RankTableModel;
import org.caleydo.vis.rank.model.StringRankColumnModel;
import org.caleydo.vis.rank.model.mapping.PiecewiseMapping;

import demo.RankTableDemo;
import demo.RankTableDemo.IModelBuilder;

/**
 * @author Samuel Gratzl
 *
 */
public class Food implements IModelBuilder {
	@Override
	public void apply(RankTableModel table) throws Exception {
		List<FoodRow> rows1 = new ArrayList<>();
		String[] header;
		Map<String,String> metaData = new HashMap<>();
		try (BufferedReader r = new BufferedReader(new InputStreamReader(Food.class.getResourceAsStream("ABBREV.csv"),
				Charset.forName("UTF-8")))) {
			String line;
			header = r.readLine().split("\t"); // skip label
			while ((line = r.readLine()) != null) {
				String[] l = line.split("\t");
				FoodRow row = new FoodRow();
				// row.rank = Integer.parseInt(l[0]);
				row.NDB_No = Integer.parseInt(l[0]);
				row.Shrt_Desc = new LinkedHashSet<>(Arrays.asList((l[1].split(","))));
				for(String s : row.Shrt_Desc)
					metaData.put(s,s);
				row.data = new float[header.length - 2 - 5];
				for (int i1 = 0; i1 < row.data.length; ++i1) {
					row.data[i1] = toFloat(l, i1 + 2);
				}
				rows1.add(row);
			}
		}
		System.out.println(metaData.size());
		table.addData(rows1);

		Color color = Color.decode("#DFC27D");
		Color bgColor = Color.decode("#F6E8C3");

		table.add(new RankRankColumnModel());
		// table.add(new CategoricalRankColumnModel<String>(GLRenderers.drawText("Short Description", VAlign.CENTER),
		// new Function<IRow, Set<String>>() {
		// @Override
		// public Set<String> apply(IRow in) {
		// return ((FoodRow)in).Shrt_Desc;
		// }
		// }, metaData));
		table.add(new StringRankColumnModel(GLRenderers.drawText("Short Description", VAlign.CENTER),
				StringRankColumnModel.DEFAULT));
		for (int i = 0; i < rows1.get(0).data.length; ++i) {
			table.add(ucol(i, header[i + 2], color, bgColor));
		}
		// NDB_No Shrt_Desc Water_(g) Energ_Kcal Protein_(g) Lipid_Tot_(g) Ash_(g) Carbohydrt_(g) Fiber_TD_(g)
		// Sugar_Tot_(g)
		// Calcium_(mg) Iron_(mg) Magnesium_(mg) Phosphorus_(mg) Potassium_(mg) Sodium_(mg) Zinc_(mg) Copper_(mg)
		// Manganese_(mg) Selenium_(µg) Vit_C_(mg) Thiamin_(mg) Riboflavin_(mg) Niacin_(mg) Panto_Acid_mg) Vit_B6_(mg)
		// Folate_Tot_(µg) Folic_Acid_(µg) Food_Folate_(µg) Folate_DFE_(µg) Choline_Tot_ (mg) Vit_B12_(µg) Vit_A_IU
		// Vit_A_RAE Retinol_(µg) Alpha_Carot_(µg) Beta_Carot_(µg) Beta_Crypt_(µg) Lycopene_(µg) Lut+Zea_ (µg)
		// Vit_E_(mg)
		// Vit_D_(µg) Vit_D_(IU) Vit_K_(µg) FA_Sat_(g) FA_Mono_(g) FA_Poly_(g) Cholestrl_(mg) GmWt_1 GmWt_Desc1 GmWt_2
		// GmWt_Desc2 Refuse_Pct
	}

	private FloatRankColumnModel ucol(int col, String label, Color color, Color bgColor) {
		return new FloatRankColumnModel(new ValueGetter(col), GLRenderers.drawText(label,
 VAlign.CENTER), color,
				bgColor, new PiecewiseMapping(0, Float.NaN), FloatInferrers.fix(0));
	}

	static class ValueGetter extends AFloatFunction<IRow> {
		private final int index;

		public ValueGetter(int column) {
			this.index = column;
		}

		@Override
		public float applyPrimitive(IRow in) {
			FoodRow r = (FoodRow) in;
			return r.data[index];
		}

	}

	static class FoodRow extends ARow {
		public int NDB_No;
		public Set<String> Shrt_Desc;
		public float[] data;

		@Override
		public String toString() {
			StringBuilder b = new StringBuilder();
			for (String s : Shrt_Desc)
				b.append(s).append(", ");
			if (!Shrt_Desc.isEmpty())
				b.setLength(b.length() - 2);
			return b.toString();
		}
	}

	public static void main(String[] args) {
		GLSandBox.main(args, RankTableDemo.class, "Food Nutrition", new Food());
	}
}
