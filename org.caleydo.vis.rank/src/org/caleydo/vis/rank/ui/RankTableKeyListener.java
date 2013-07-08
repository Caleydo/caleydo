/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
