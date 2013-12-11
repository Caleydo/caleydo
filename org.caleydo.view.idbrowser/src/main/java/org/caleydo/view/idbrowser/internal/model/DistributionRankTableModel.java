/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.idbrowser.internal.model;

import java.util.Set;

import org.caleydo.core.data.collection.CategoricalHistogram;
import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.data.collection.table.CategoricalTable;
import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataSupportDefinitions;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.vis.lineup.model.ARankColumnModel;
import org.caleydo.vis.lineup.model.IRow;
import org.caleydo.vis.lineup.ui.detail.ValueElement;

/**
 * a {@link ARankColumnModel} for a categorical datadomain, which shows a horizontal mosiac plot
 * 
 * @author Samuel Gratzl
 * 
 */
public class DistributionRankTableModel extends ADataDomainRankTableModel {
	public DistributionRankTableModel(ATableBasedDataDomain d, EDimension dim) {
		super(d, dim);
		assert DataSupportDefinitions.categoricalTables.apply(d);
	}

	public DistributionRankTableModel(TablePerspective t, EDimension dim) {
		super(t, dim);
	}

	/**
	 * @param distributionRankTableModel
	 */
	public DistributionRankTableModel(DistributionRankTableModel clone) {
		super(clone);
	}

	@Override
	public ARankColumnModel clone() {
		return new DistributionRankTableModel(this);
	}

	@Override
	public String getValue(IRow row) {
		CategoricalHistogram h = getHist(row);
		if (h == null)
			return "";
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < h.size(); ++i) {
			b.append(h.getName(i)).append(":\t").append(h.get(i)).append('\n');
		}
		b.setLength(b.length() - 1); // remove last \n
		return b.toString();
	}

	CategoricalHistogram getHist(IRow row) {
		if (cache.containsKey(row.getIndex()))
			return (CategoricalHistogram) cache.get(row.getIndex());
		CategoricalHistogram c = computeHist((IIDRow) row);
		cache.put(row.getIndex(), c);
		return c;
	}

	/**
	 * @param row
	 * @return
	 */
	private CategoricalHistogram computeHist(IIDRow row) {
		Set<Object> ids = row.get(getIDType());
		if (ids == null || ids.isEmpty())
			return null;
		final Table table = d.getTable();
		CategoricalHistogram h = new CategoricalHistogram(((CategoricalTable<?>) table).getCategoryDescriptions());
		for (Object id : ids) {
			if (!(id instanceof Integer))
				continue;
			Integer id_i = (Integer) id;
			if (dim.isHorizontal()) {
				for (Integer other : others) {
					h.add(table.getRaw(id_i, other), other);
				}
			} else {
				for (Integer other : others) {
					h.add(table.getRaw(other, id_i), other);
				}
			}
		}
		return h;
	}


	@Override
	public ValueElement createValue() {
		return new MyValueElement();
	}

	private class MyValueElement extends ValueElement {

		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			if (h < 1)
				return;
			CategoricalHistogram hist = getHist(getRow());
			if (hist == null)
				return;

			int sum = 0;
			for (int i = 0; i < hist.size(); ++i)
				sum += hist.get(i);
			if (sum == 0)
				return;
			float factor = w / sum;

			float xi = 0;
			for (int i = 0; i < hist.size(); ++i) {
				float wi = hist.get(i) * factor;
				if (wi <= 0)
					continue;
				g.color(hist.getColor(i));
				g.fillRect(xi, 1, wi, h - 2);
				xi += wi;
			}
			if (getRenderInfo().getBarOutlineColor() != null) {
				// outline
				g.color(getRenderInfo().getBarOutlineColor()).drawRect(0, 1, w, h - 2);
			}
		}
	}

}
