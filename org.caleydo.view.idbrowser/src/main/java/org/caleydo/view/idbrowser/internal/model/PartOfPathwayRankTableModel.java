/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.idbrowser.internal.model;

import java.util.Collections;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.caleydo.core.util.base.Labels;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.caleydo.vis.lineup.model.IRow;
import org.caleydo.vis.lineup.model.MultiCategoricalRankColumnModel;
import org.caleydo.vis.lineup.ui.IColumnRenderInfo;
import org.caleydo.vis.lineup.ui.detail.ValueElement;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.jogamp.common.util.IntObjectHashMap;

/**
 * for a gene compute in how many pathways the gene is part of
 *
 * handle it as a special kind of a {@link MultiCategoricalRankColumnModel}
 *
 * @author Samuel Gratzl
 *
 */
public class PartOfPathwayRankTableModel extends MultiCategoricalRankColumnModel<PathwayGraph> {
	/**
	 *
	 */
	public PartOfPathwayRankTableModel() {
		super(GLRenderers.drawText("Pathways", VAlign.CENTER), new Function<IRow, Set<PathwayGraph>>() {
			private final IntObjectHashMap cache = new IntObjectHashMap();

			@SuppressWarnings("unchecked")
			@Override
			public Set<PathwayGraph> apply(IRow input) {
				PrimaryIDRow r = (PrimaryIDRow) input;
				if (cache.containsKey(r.getIndex()))
					return (Set<PathwayGraph>) cache.get(r.getIndex());
				Set<PathwayGraph> p = getImpl(r);
				cache.put(r.getIndex(), p);
				return p;
			}
		}, Labels.TO_LABEL, createMetaData(), "<None>");
	}

	/**
	 * @param input
	 * @return
	 */
	protected static Set<PathwayGraph> getImpl(PrimaryIDRow r) {
		Object p = r.getPrimary();
		if (p instanceof Integer)
			return PathwayManager.get().getPathwayGraphsByGeneID(r.getPrimaryIDType(), (Integer) p);
		return Collections.emptySet();
	}

	/**
	 * @return
	 */
	private static Set<PathwayGraph> createMetaData() {
		return ImmutableSet.copyOf(PathwayManager.get().getAllItems());
	}

	public PartOfPathwayRankTableModel(PartOfPathwayRankTableModel clone) {
		super(clone);
	}

	@Override
	public PartOfPathwayRankTableModel clone() {
		return new PartOfPathwayRankTableModel(this);
	}

	@Override
	public ValueElement createValue() {
		return new MyValueElement();
	}

	class MyValueElement extends ValueElement {
		public MyValueElement() {
			setVisibility(EVisibility.VISIBLE);
		}

		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			if (h < 5)
				return;
			Set<PathwayGraph> v = getCatValue(getRow());
			int size = v == null ? 0 : v.size();
			float hi = Math.min(h, 18);
			// render just the number of matches
			if (!(((IColumnRenderInfo) getParent()).isCollapsed())) {
				g.drawText("" + size, 1, 1 + (h - hi) * 0.5f, w - 2, hi - 5);
			}
		}

		@Override
		public String getTooltip() {
			Set<PathwayGraph> value = getCatValue(getRow());
			if (value == null || value.isEmpty())
				return "<None";
			return StringUtils.join(Iterators.transform(value.iterator(), Labels.TO_LABEL), '\n');
		}
	}
}
