package demo;
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


import org.caleydo.core.util.color.Color;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.caleydo.core.view.opengl.layout2.GLSandBox;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.vis.rank.model.ARankColumnModel;
import org.caleydo.vis.rank.model.ARow;
import org.caleydo.vis.rank.model.CategoricalRankRankColumnModel;
import org.caleydo.vis.rank.model.CategoricalRankRankColumnModel.CategoryInfo;
import org.caleydo.vis.rank.model.IRow;
import org.caleydo.vis.rank.model.RankRankColumnModel;
import org.caleydo.vis.rank.model.RankTableModel;
import org.caleydo.vis.rank.model.mapping.BaseCategoricalMappingFunction;

import com.google.common.base.Function;

import demo.RankTableDemo.IModelBuilder;

/**
 * @author Samuel Gratzl
 *
 */
public class SimpleCategoryScoreTest implements IModelBuilder {
	@Override
	public void apply(RankTableModel table) throws Exception {
		table.add(new RankRankColumnModel());

		Map<String, CategoryInfo> metaData = new HashMap<>();
		metaData.put("Cat 1", new CategoryInfo("Category 1", Color.RED));
		metaData.put("Cat 2", new CategoryInfo("Category 2", Color.BLUE));
		metaData.put("Cat 3", new CategoryInfo("Category 3", Color.GREEN));
		metaData.put("Cat 4", new CategoryInfo("Category 4", Color.YELLOW));

		table.add(new CategoricalRankRankColumnModel<String>(GLRenderers.drawText("Category"),
				new Function<IRow, String>() {
					@Override
					public String apply(IRow in) {
						return ((SimpleRow) in).value;
					}
				}, metaData,
 new BaseCategoricalMappingFunction<String>(metaData
						.keySet())));

		List<String> categories = new ArrayList<>(metaData.keySet());
		Random r = new Random(200);
		List<IRow> rows = new ArrayList<>(20);
		for (int i = 0; i < 20; ++i)
			rows.add(new SimpleRow(categories.get(r.nextInt(categories.size()))));
		table.addData(rows);
	}

	@Override
	public Iterable<? extends ARankColumnModel> createAutoSnapshotColumns(RankTableModel table, ARankColumnModel model) {
		return Collections.singleton(new RankRankColumnModel());
	}

	public static Field field(String f) throws NoSuchFieldException {
		return SimpleRow.class.getDeclaredField(f);
	}

	static class SimpleRow extends ARow {
		private final String value;

		public SimpleRow(String value) {
			this.value = value;
		}
	}

	public static void main(String[] args) {
		GLSandBox.main(args, RankTableDemo.class, "SimpelCategoryTest", new SimpleCategoryScoreTest());
	}
}
