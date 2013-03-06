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
package org.caleydo.vis.rank.ui.mapping;

import java.awt.Color;
import java.util.Map;

import org.caleydo.core.io.gui.dataimport.widget.ICallback;
import org.caleydo.core.util.function.IFloatList;
import org.caleydo.core.view.opengl.layout2.GLElement;
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
			ICallback<? super IMappingFunction> callback) {
		MappingFunctionUI m = new MappingFunctionUI(model, data, color, bgColor, callback);
		if (model instanceof PiecewiseMapping) {
			m.addMode(new PiecewiseMappingCrossUI((PiecewiseMapping) model, true));
			m.addMode(new PiecewiseMappingParallelUI((PiecewiseMapping) model, true));
			m.addMode(new PiecewiseMappingCrossUI((PiecewiseMapping) model, false));
			m.addMode(new PiecewiseMappingParallelUI((PiecewiseMapping) model, false));
		} else {
			m.addMode(new MappingCrossUI<IMappingFunction>(model, true));
			m.addMode(new MappingParallelUI<IMappingFunction>(model, true));
			m.addMode(new MappingCrossUI<IMappingFunction>(model, false));
			m.addMode(new MappingParallelUI<IMappingFunction>(model, false));
		}
		return m;
	}

	public static <T> GLElement create(ICategoricalMappingFunction<T> model, Map<T, Integer> data,
			Map<T, CategoryInfo> metaData, Color bgColor, ICallback<? super ICategoricalMappingFunction<?>> callback) {
		if (model instanceof BaseCategoricalMappingFunction)
			return new BaseCategoricalMappingFunctionUI<T>((BaseCategoricalMappingFunction<T>) model, data, metaData,
					bgColor, callback);
		return null;
	}
}
