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
package org.caleydo.vis.rank.ui.column;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.vis.rank.config.IRankTableUIConfig;
import org.caleydo.vis.rank.model.ACompositeRankColumnModel;
import org.caleydo.vis.rank.model.ARankColumnModel;
import org.caleydo.vis.rank.model.GroupRankColumnModel;
import org.caleydo.vis.rank.model.MaxRankColumnModel;
import org.caleydo.vis.rank.model.NestedRankColumnModel;
import org.caleydo.vis.rank.model.OrderColumn;
import org.caleydo.vis.rank.model.RankRankColumnModel;
import org.caleydo.vis.rank.model.ScriptedRankColumnModel;
import org.caleydo.vis.rank.model.StackedRankColumnModel;

/**
 * @author Samuel Gratzl
 *
 */
public class ColumnUIs {
	public static GLElement createHeader(ARankColumnModel model, IRankTableUIConfig config, boolean allowComplex) {
		if (allowComplex && model instanceof StackedRankColumnModel)
			return new StackedColumnHeaderUI((StackedRankColumnModel) model, config);
		if (allowComplex && (model instanceof NestedRankColumnModel || (model instanceof GroupRankColumnModel)))
			return new SimpleColumnHeaderUI((ACompositeRankColumnModel) model, config);
		if (model instanceof OrderColumn)
			return new OrderColumnHeaderUI(model, config);
		if (allowComplex && (model instanceof MaxRankColumnModel || model instanceof ScriptedRankColumnModel))
			return new VerticalColumnHeaderUI((ACompositeRankColumnModel) model, config);
		return new ColumnHeaderUI(model, config);
	}

	public static ITableColumnUI createBody(ARankColumnModel model, boolean allowComplex) {
		if (allowComplex && model instanceof StackedRankColumnModel)
			return new StackedColumnUI((StackedRankColumnModel) model);
		if (allowComplex && (model instanceof NestedRankColumnModel || (model instanceof GroupRankColumnModel)))
			return new SimpleColumnUI((ACompositeRankColumnModel) model);
		if (model instanceof RankRankColumnModel)
			return new RankColumnUI(model);
		return new ColumnUI(model);
	}

}
