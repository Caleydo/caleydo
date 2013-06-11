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
package org.caleydo.vis.rank.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.vis.rank.model.RankTableModel;

/**
 * @author Samuel Gratzl
 *
 */
public class SimpleRankTableStatsElement extends PickableGLElement implements PropertyChangeListener {
	private final RankTableModel table;

	public SimpleRankTableStatsElement(RankTableModel table) {
		this.table = table;
		setSize(150, 12);
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
				.append(String.format(" (%.2f%%)", ((float) filtered) / total));
		return b.toString();
	}
}
