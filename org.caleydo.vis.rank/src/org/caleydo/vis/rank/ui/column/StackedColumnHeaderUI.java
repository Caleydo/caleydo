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

import static org.caleydo.vis.rank.ui.RenderStyle.COLUMN_SPACE;
import static org.caleydo.vis.rank.ui.RenderStyle.HIST_HEIGHT;
import static org.caleydo.vis.rank.ui.RenderStyle.LABEL_HEIGHT;

import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Locale;

import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IMouseLayer.IDragInfo;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.vis.rank.model.ARankColumnModel;
import org.caleydo.vis.rank.model.StackedRankColumnModel;
import org.caleydo.vis.rank.ui.GLPropertyChangeListeners;
import org.caleydo.vis.rank.ui.SeparatorUI;
import org.caleydo.vis.rank.ui.StackedSeparatorUI;
/**
 * @author Samuel Gratzl
 *
 */
public class StackedColumnHeaderUI extends ACompositeTableColumnHeaderUI<StackedRankColumnModel> {
	public final AlignmentDragInfo align = new AlignmentDragInfo();

	private final PropertyChangeListener alignmentChanged = GLPropertyChangeListeners.relayoutOnEvent(this);

	public StackedColumnHeaderUI(StackedRankColumnModel model, boolean interactive) {
		super(model, interactive);
		this.add(0, new StackedSummaryHeaderUI(model, interactive));
		model.addPropertyChangeListener(StackedRankColumnModel.PROP_ALIGNMENT, alignmentChanged);
	}

	@Override
	protected SeparatorUI createSeparator(int index) {
		return new StackedSeparatorUI(this, index);
	}

	@Override
	protected GLElement wrapImpl(ARankColumnModel model) {
		return ColumnUIs.createHeader(model, this.interactive, false);
	}

	@Override
	protected void takeDown() {
		model.removePropertyChangeListener(StackedRankColumnModel.PROP_ALIGNMENT, alignmentChanged);
		super.takeDown();
	}

	@Override
	public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {
		IGLLayoutElement summary = children.get(0);
		summary.setBounds(0, 0, w, HIST_HEIGHT + 4);

		super.doLayout(children, w, h);

		// update the alignment infos
		if (interactive) {
			List<? extends IGLLayoutElement> separators = children.subList(numColumns + 2, children.size());
			final IGLLayoutElement sep0 = children.get(numColumns + 1);
			((StackedSeparatorUI) sep0.asElement()).setAlignment(this.model.getAlignment());
			for (IGLLayoutElement sep : separators) {
				((StackedSeparatorUI) sep.asElement()).setAlignment(this.model.getAlignment());
			}
		}
	}

	@Override
	protected float getTopPadding() {
		return HIST_HEIGHT + LABEL_HEIGHT;
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		g.color(model.getBgColor()).fillRect(0, HIST_HEIGHT, w, h - HIST_HEIGHT);
		if (!model.isCollapsed()) {
			// render the distributions
			float[] distributions = model.getDistributions();
			float yi = HIST_HEIGHT + 7;
			float hi = LABEL_HEIGHT - 6;
			float x = COLUMN_SPACE + 2;
			g.color(Color.GRAY).drawLine(x, yi - 4, w - 2, yi - 4);
			for (int i = 0; i < numColumns; ++i) {
				float wi = model.get(i).getPreferredWidth() + COLUMN_SPACE;
				// g.drawLine(x, yi, x, yi + hi + 2);
				g.drawText(String.format(Locale.ENGLISH, "%.2f%%", distributions[i] * 100), x, yi, wi, hi - 4,
						VAlign.CENTER);
				// g.drawLine(x, yi + hi, x + wi, yi + hi);
				x += wi;
			}
			// g.drawLine(x, yi, x, yi + hi + 2);
		}
		super.renderImpl(g, w, h);
	}

	public static class AlignmentDragInfo implements IDragInfo {

	}

	public void setAlignment(int index) {
		model.setAlignment(index);
	}
}

