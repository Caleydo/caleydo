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

import org.caleydo.core.view.opengl.canvas.IGLKeyListener;
import org.caleydo.vis.rank.model.RankTableModel;
import org.caleydo.vis.rank.model.StackedRankColumnModel;

import com.google.common.collect.Iterables;

/**
 * @author Samuel Gratzl
 *
 */
public class RankTableKeyListener implements IGLKeyListener {
	private static final char TOGGLE_ALIGN_ALL = 't';

	private final RankTableModel table;

	public RankTableKeyListener(RankTableModel table) {
		super();
		this.table = table;
	}

	@Override
	public void keyPressed(IKeyEvent e) {
		if (e.isKey(ESpecialKey.DOWN))
			table.selectNextRow();
		else if (e.isKey(ESpecialKey.UP))
			table.selectPreviousRow();
		else if (e.isControlDown() && (e.isKey(TOGGLE_ALIGN_ALL))) {
			// short cut for align all
			for (StackedRankColumnModel stacked : Iterables.filter(table.getColumns(), StackedRankColumnModel.class)) {
				stacked.switchToNextAlignment();
			}
		}
	}

	@Override
	public void keyReleased(IKeyEvent e) {

	}
}
