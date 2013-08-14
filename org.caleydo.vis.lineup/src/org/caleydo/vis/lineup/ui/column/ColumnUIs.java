/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.ui.column;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.vis.lineup.config.IRankTableUIConfig;
import org.caleydo.vis.lineup.model.ACompositeRankColumnModel;
import org.caleydo.vis.lineup.model.ARankColumnModel;
import org.caleydo.vis.lineup.model.GroupRankColumnModel;
import org.caleydo.vis.lineup.model.MaxRankColumnModel;
import org.caleydo.vis.lineup.model.NestedRankColumnModel;
import org.caleydo.vis.lineup.model.OrderColumn;
import org.caleydo.vis.lineup.model.RankRankColumnModel;
import org.caleydo.vis.lineup.model.ScriptedRankColumnModel;
import org.caleydo.vis.lineup.model.StackedRankColumnModel;

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
		if (model instanceof MaxRankColumnModel || model instanceof ScriptedRankColumnModel)
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
