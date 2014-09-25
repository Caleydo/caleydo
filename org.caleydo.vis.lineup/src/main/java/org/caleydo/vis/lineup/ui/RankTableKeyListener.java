/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.ui;

import org.caleydo.core.view.opengl.canvas.IGLKeyListener;
import org.caleydo.vis.lineup.model.RankTableModel;
import org.caleydo.vis.lineup.model.StackedRankColumnModel;

import com.google.common.collect.Iterables;

/**
 * @author Samuel Gratzl
 *
 */
public class RankTableKeyListener implements IGLKeyListener {
	private static final char TOGGLE_ALIGN_ALL = 't';

	private final RankTableModel table;
	private final TableBodyUI body;

	public RankTableKeyListener(RankTableModel table, TableBodyUI body) {
		this.table = table;
		this.body = body;
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
		} else if (body != null) {
			if (e.isKey(ESpecialKey.PAGE_UP)) {
				body.scroll(-15);
			} else if (e.isKey(ESpecialKey.PAGE_DOWN)) {
				body.scroll(15);
			} else if (e.isKey(ESpecialKey.HOME)) {
				body.scrollFirst();
			} else if (e.isKey(ESpecialKey.END)) {
				body.scrollLast();
			}
		}
	}

	@Override
	public void keyReleased(IKeyEvent e) {

	}
}
