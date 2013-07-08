/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package demo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.caleydo.core.util.color.Color;
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
