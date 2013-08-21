/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.vis.lineup.model.RankTableModel;

/**
 * @author Samuel Gratzl
 *
 */
public class SimpleRankTableStatsElement extends PickableGLElement implements PropertyChangeListener {
	private final RankTableModel table;

	public SimpleRankTableStatsElement(RankTableModel table) {
		this.table = table;
		setSize(180, 12);
	}

	@Override
	protected void init(IGLElementContext context) {
		super.init(context);
		table.addPropertyChangeListener(RankTableModel.PROP_FILTER_INVALID, this);
	}

	@Override
	protected void takeDown() {
		table.removePropertyChangeListener(RankTableModel.PROP_FILTER_INVALID, this);
		super.takeDown();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		repaint();
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		String text = getTooltip();
		float hi = Math.min(12, h);
		g.drawText(text, 5, (h - hi) * 0.5f, w - 10, hi);
	}

	@Override
	public String getTooltip() {
		StringBuilder b = new StringBuilder();
		int total = table.getDataMask() != null ? table.getDataMask().cardinality() : table.getDataSize();
		int filtered = table.getMyRanker(null).getFilter().cardinality();
		b.append(filtered).append(" visible of ").append(total)
				.append(String.format(" (%.2f%%)", (filtered * 100.f) / total));
		return b.toString();
	}
}
