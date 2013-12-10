/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.idbrowser.internal.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.data.collection.column.container.CategoricalClassDescription;
import org.caleydo.core.data.collection.column.container.CategoryProperty;
import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.id.IDType;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.vis.lineup.model.IRow;
import org.caleydo.vis.lineup.model.MultiCategoricalRankColumnModel;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;

/**
 * @author Samuel Gratzl
 *
 */
public class SingleCategorical extends MultiCategoricalRankColumnModel<String> {
	/**
	 *
	 */
	public SingleCategorical(final TablePerspective t) {
		super(GLRenderers.drawText(t.getLabel(), VAlign.CENTER), new Function<IRow, Set<String>>() {
			private final Table data = t.getDataDomain().getTable();
			private final Integer dimId = t.getDimensionPerspective().getVirtualArray().get(0);
			private final IDType idType = t.getDataDomain().getRecordIDType();
			@Override
			public Set<String> apply(IRow input) {
				assert input instanceof IIDRow;
				Set<Object> r = ((IIDRow) input).get(idType);
				if (r == null || r.isEmpty())
					return Collections.emptySet();
				Set<String> s = new TreeSet<>();
				for (Object ri : r) {
					if (!(ri instanceof Integer))
						continue;
					ri = data.getRaw(dimId, (Integer) ri);
					s.add(ri.toString());
				}
				return s;
			}
		}, toMetaData(t), t.getDataDomain().getColor(), t.getDataDomain().getColor().brighter().brighter(), "");
	}

	/**
	 * @param t
	 * @return
	 */
	private static Map<String, String> toMetaData(TablePerspective t) {
		Integer dimId = t.getDimensionPerspective().getVirtualArray().get(0);
		List<CategoryProperty<?>> categories = resolveCategories(dimId, t.getDataDomain(), EDimension.DIMENSION);
		Map<String, String> catMeta = new HashMap<>();
		for (CategoryProperty<?> p : categories) {
			catMeta.put(p.getCategory().toString(), p.getCategoryName());
		}
		return catMeta;

	}

	@SuppressWarnings("unchecked")
	static List<CategoryProperty<?>> resolveCategories(Integer singleID, ATableBasedDataDomain dataDomain,
			EDimension dim) {
		final Table table = dataDomain.getTable();

		Object spec = table.getDataClassSpecificDescription(dim.select(singleID.intValue(), 0),
				dim.select(0, singleID.intValue()));
		if (spec instanceof CategoricalClassDescription<?>) {
			List<?> tmp = ((CategoricalClassDescription<?>) spec).getCategoryProperties();
			return ImmutableList.copyOf((List<CategoryProperty<?>>) tmp);
		}
		return Collections.emptyList();
	}
}