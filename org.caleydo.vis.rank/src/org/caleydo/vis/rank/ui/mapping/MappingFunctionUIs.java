/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.rank.ui.mapping;

import java.util.Map;

import org.caleydo.core.util.base.ICallback;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.function.IFloatList;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.vis.rank.config.IRankTableUIConfig;
import org.caleydo.vis.rank.model.CategoricalRankRankColumnModel.CategoryInfo;
import org.caleydo.vis.rank.model.mapping.BaseCategoricalMappingFunction;
import org.caleydo.vis.rank.model.mapping.ICategoricalMappingFunction;
import org.caleydo.vis.rank.model.mapping.IMappingFunction;
import org.caleydo.vis.rank.model.mapping.PiecewiseMapping;

/**
 * @author Samuel Gratzl
 *
 */
public class MappingFunctionUIs {
	public static GLElement create(IMappingFunction model, IFloatList data, Color color, Color bgColor,
			ICallback<? super IMappingFunction> callback, IRankTableUIConfig config) {
		MappingFunctionUI m = new MappingFunctionUI(model, data, color, bgColor, callback, config);
		if (model instanceof PiecewiseMapping) {
			m.addMode(new PiecewiseMappingParallelUI((PiecewiseMapping) model, true));
			m.addMode(new PiecewiseMappingCrossUI((PiecewiseMapping) model, true));

			// m.addMode(new PiecewiseMappingCrossUI((PiecewiseMapping) model, false));
			// m.addMode(new PiecewiseMappingParallelUI((PiecewiseMapping) model, false));
		} else {
			m.addMode(new MappingParallelUI<IMappingFunction>(model, true));
			m.addMode(new MappingCrossUI<IMappingFunction>(model, true));
			// m.addMode(new MappingCrossUI<IMappingFunction>(model, false));
			// m.addMode(new MappingParallelUI<IMappingFunction>(model, false));
		}
		return m;
	}

	public static <T> GLElement create(ICategoricalMappingFunction<T> model, Map<T, Integer> data,
			Map<T, CategoryInfo> metaData, Color color, Color bgColor,
			ICallback<? super ICategoricalMappingFunction<?>> callback, IRankTableUIConfig config) {
		if (model instanceof BaseCategoricalMappingFunction)
			return new BaseCategoricalMappingFunctionUI<T>((BaseCategoricalMappingFunction<T>) model, data, metaData,
					color, bgColor, callback, config);
		return null;
	}
}
