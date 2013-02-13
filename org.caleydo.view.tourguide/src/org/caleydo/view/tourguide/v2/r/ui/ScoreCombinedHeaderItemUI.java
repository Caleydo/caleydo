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
package org.caleydo.view.tourguide.v2.r.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.view.tourguide.v2.r.model.ScoreColumn;
import org.caleydo.view.tourguide.v2.r.model.ScoreTable;

/**
 * @author Samuel Gratzl
 *
 */
public class ScoreCombinedHeaderItemUI extends GLElement {
	private final ScoreTable table;
	private int selectedRow = -1;

	public ScoreCombinedHeaderItemUI(ScoreTable table) {
		this.table = table;
		this.table.addPropertyChangeListener(ScoreColumn.PROP_SELECTED_ROW, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				onSelectRow((int) evt.getNewValue());
			}
		});
		this.selectedRow = table.getSelectedRow();
	}

	/**
	 * @param newValue
	 */
	protected void onSelectRow(int selectedRow) {
		if (this.selectedRow == selectedRow)
			return;
		this.selectedRow = selectedRow;
		repaint();
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		super.renderImpl(g, w, h);
		// background
		g.color(table.getCombinedBackgroundColor()).fillRect(0, 0, w, h);
		// hist
		int bins = Math.round(w);
		int selectedBin = selectedRow < 0 ? -1 : table.getCombinedHistBin(bins, selectedRow);
		RenderUtils.renderHist(g, table.getCombinedHist(bins), w, h, selectedBin, table.getCombinedColor(),
 table
				.getCombinedColor().darker());
	}


}