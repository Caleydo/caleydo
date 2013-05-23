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
package org.caleydo.view.tourguide.internal.view.col;

import java.awt.Color;

import org.caleydo.core.data.collection.column.container.CategoryProperty;
import org.caleydo.core.data.collection.table.CategoricalTable;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.util.color.Colors;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.view.tourguide.internal.model.CategoricalPerspectiveRow;
import org.caleydo.vis.rank.data.AFloatFunction;
import org.caleydo.vis.rank.data.FloatInferrers;
import org.caleydo.vis.rank.data.IFloatFunction;
import org.caleydo.vis.rank.model.FloatRankColumnModel;
import org.caleydo.vis.rank.model.IRow;
import org.caleydo.vis.rank.model.mapping.PiecewiseMapping;

/**
 * @author Samuel Gratzl
 *
 */
public class CategoricalPercentageRankColumnModel extends FloatRankColumnModel {
	private final IDataDomain dataDomain;

	public CategoricalPercentageRankColumnModel(IFloatFunction<IRow> data, String label, Color color, Color bgColor,
			IDataDomain dataDomain, int max) {
		super(data, GLRenderers.drawText("% " + label), color, bgColor, new PiecewiseMapping(0, max), FloatInferrers
				.fix(Float.NaN));
		this.dataDomain = dataDomain;
		setFilter(false, false, true);
	}

	/**
	 * @return the dataDomain, see {@link #dataDomain}
	 */
	public IDataDomain getDataDomain() {
		return dataDomain;
	}

	public static CategoricalPercentageRankColumnModel create(final Object category, final CategoricalTable<?> table) {
		final CategoryProperty<?> property = table.getCategoryDescriptions().getCategoryProperty(category);
		String catName = property.getCategoryName();
		IFloatFunction<IRow> data = new AFloatFunction<IRow>() {
			@Override
			public float applyPrimitive(IRow in) {
				if (!(in instanceof CategoricalPerspectiveRow))
					return Float.NaN;
				CategoricalPerspectiveRow r = (CategoricalPerspectiveRow) in;
				if (r.getDataDomain() != table.getDataDomain())
					return Float.NaN;
				int have = table.getNumberOfMatches(category, r.getCategoryIDType(), r.getDimensionID());
				return have;
			}
		};
		Color col = Colors.of(property.getColor());
		Color bgColor = col.brighter().brighter();
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