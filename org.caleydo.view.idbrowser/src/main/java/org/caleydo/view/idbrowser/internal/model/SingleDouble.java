/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.idbrowser.internal.model;

import java.util.Set;

import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.id.IDType;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.vis.lineup.data.ADoubleFunction;
import org.caleydo.vis.lineup.data.DoubleInferrers;
import org.caleydo.vis.lineup.model.DoubleRankColumnModel;
import org.caleydo.vis.lineup.model.IRow;
import org.caleydo.vis.lineup.model.mapping.PiecewiseMapping;

/**
 * @author Samuel Gratzl
 *
 */
public class SingleDouble extends DoubleRankColumnModel {
	/**
	 *
	 */
	public SingleDouble(final TablePerspective t) {
		super(new ADoubleFunction<IRow>() {
			private final Table data = t.getDataDomain().getTable();
			private final Integer dimId = t.getDimensionPerspective().getVirtualArray().get(0);
			private final IDType idType = t.getDataDomain().getRecordIDType();
			@Override
			public double applyPrimitive(IRow input) {
				assert input instanceof IIDRow;
				Set<Object> r = ((IIDRow) input).get(idType);
				if (r == null || r.isEmpty())
					return Double.NaN;
				Object ri = r.iterator().next();
				if (!(ri instanceof Integer))
					return Double.NaN;
				Integer id = (Integer) ri;
				ri = data.getRaw(dimId, id);
				if (ri instanceof Number)
					return ((Number) ri).doubleValue();
				return Double.NaN;
			}
		}, GLRenderers.drawText(t.getLabel(), VAlign.CENTER)
		, t.getDataDomain().getColor(), t.getDataDomain().getColor().brighter().brighter(),  new PiecewiseMapping(Float.NaN, Float.NaN), DoubleInferrers
.fix(Double.NaN));
	}
}