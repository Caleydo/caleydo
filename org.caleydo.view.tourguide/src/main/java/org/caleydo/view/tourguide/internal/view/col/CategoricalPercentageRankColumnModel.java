/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.view.col;

import org.caleydo.core.data.collection.column.container.CategoryProperty;
import org.caleydo.core.data.collection.table.CategoricalTable;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.view.tourguide.internal.model.CategoricalPerspectiveRow;
import org.caleydo.vis.lineup.data.ADoubleFunction;
import org.caleydo.vis.lineup.data.DoubleInferrers;
import org.caleydo.vis.lineup.data.IDoubleFunction;
import org.caleydo.vis.lineup.model.DoubleRankColumnModel;
import org.caleydo.vis.lineup.model.IRow;
import org.caleydo.vis.lineup.model.mapping.PiecewiseMapping;

/**
 * @author Samuel Gratzl
 *
 */
public class CategoricalPercentageRankColumnModel extends DoubleRankColumnModel {
	private final IDataDomain dataDomain;

	public CategoricalPercentageRankColumnModel(IDoubleFunction<IRow> data, String label, Color color, Color bgColor,
			IDataDomain dataDomain, int max) {
		super(data, GLRenderers.drawText("% " + label), color, bgColor, new PiecewiseMapping(0, max), DoubleInferrers
				.fix(Double.NaN));
		this.dataDomain = dataDomain;
		setFilter(true, false, true, false);
	}

	/**
	 * @return the dataDomain, see {@link #dataDomain}
	 */
	public IDataDomain getDataDomain() {
		return dataDomain;
	}

	public static CategoricalPercentageRankColumnModel create(final Object category, final CategoricalTable<?> table,
			boolean isConsideredForCalculation) {
		final CategoryProperty<?> property = table.getCategoryDescriptions().getCategoryProperty(category);
		String catName = property.getCategoryName();
		IDoubleFunction<IRow> data = new ADoubleFunction<IRow>() {
			@Override
			public double applyPrimitive(IRow in) {
				if (!(in instanceof CategoricalPerspectiveRow))
					return Double.NaN;
				CategoricalPerspectiveRow r = (CategoricalPerspectiveRow) in;
				if (r.getDataDomain() != table.getDataDomain())
					return Double.NaN;
				int have = table.getNumberOfMatches(category, r.getCategoryIDType(), r.getDimensionID());
				return have;
			}
		};
		Color col = property.getColor();
		Color bgColor = col.brighter();
		if (!isConsideredForCalculation) {
			catName += "*";
			bgColor = Color.NEUTRAL_GREY.brighter();
			col = Color.NEUTRAL_GREY;
		}
		return new CategoricalPercentageRankColumnModel(data, catName, col, bgColor, table.getDataDomain(),
				table.depth());

	}

	public CategoricalPercentageRankColumnModel(CategoricalPercentageRankColumnModel copy) {
		super(copy);
		this.dataDomain = copy.dataDomain;
	}

	@Override
	public CategoricalPercentageRankColumnModel clone() {
		return new CategoricalPercentageRankColumnModel(this);
	}
}
