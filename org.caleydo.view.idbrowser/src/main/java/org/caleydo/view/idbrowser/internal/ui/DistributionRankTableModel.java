/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.idbrowser.internal.ui;

import java.util.Set;

import org.caleydo.core.data.collection.CategoricalHistogram;
import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.data.collection.table.CategoricalTable;
import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataSupportDefinitions;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.view.idbrowser.internal.model.PrimaryIDRow;
import org.caleydo.vis.lineup.model.ARankColumnModel;
import org.caleydo.vis.lineup.model.IRow;
import org.caleydo.vis.lineup.ui.detail.ValueElement;

import com.jogamp.common.util.IntObjectHashMap;

/**
 * @author Samuel Gratzl
 *
 */
public class DistributionRankTableModel extends ARankColumnModel {
	private final ATableBasedDataDomain d;
	private final EDimension dim;

	private IntObjectHashMap cache = new IntObjectHashMap();

	public DistributionRankTableModel(ATableBasedDataDomain d, EDimension dim) {
		super(Color.GRAY, new Color(0.95f, .95f, .95f));
		this.d = d;
		assert DataSupportDefinitions.categoricalTables.apply(d);
		setHeaderRenderer(GLRenderers.drawText(d.getLabel(), VAlign.CENTER));
		this.dim = dim;
	}

	/**
	 * @param distributionRankTableModel
	 */
	public DistributionRankTableModel(DistributionRankTableModel clone) {
		super(clone);
		this.d = clone.d;
		this.dim = clone.dim;
		setHeaderRenderer(GLRenderers.drawText(d.getLabel(), VAlign.CENTER));
	}

	@Override
	public ARankColumnModel clone() {
		return new DistributionRankTableModel(this);
	}

	@Override
	public String getValue(IRow row) {
		// TODO Auto-generated method stub
		return null;
	}

	public IDType getIDType() {
		return dim.select(d.getDimensionIDType(), d.getRecordIDType());
	}

	CategoricalHistogram getHist(IRow row) {
		if (cache.containsKey(row.getIndex()))
			return (CategoricalHistogram) cache.get(row.getIndex());
		CategoricalHistogram c = computeHist((PrimaryIDRow) row);
		cache.put(row.getIndex(), c);
		return c;
	}

	/**
	 * @param row
	 * @return
	 */
	private CategoricalHistogram computeHist(PrimaryIDRow row) {
		Set<Object> ids = row.get(getIDType());
		if (ids == null || ids.isEmpty())
			return null;
		final Table table = d.getTable();
		CategoricalHistogram h = new CategoricalHistogram(((CategoricalTable<?>) table).getCategoryDescriptions());
		VirtualArray others = dim.opposite()
				.select(table.getDefaultDimensionPerspective(false), table.getDefaultRecordPerspective(false))
				.getVirtualArray();
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
	public GLElement createSummary(boolean interactive) {
		return new GLElement();
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
