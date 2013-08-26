/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.ui;

import org.caleydo.core.view.opengl.canvas.GLMouseAdapter;
import org.caleydo.core.view.opengl.canvas.IGLKeyListener;

/**
 * @author Samuel Gratzl
 *
 */
public class RankTableUIMouseKeyListener extends GLMouseAdapter implements IGLKeyListener {
	private final TableBodyUI body;

	public RankTableUIMouseKeyListener(TableBodyUI body) {
		this.body = body;
	}

	@Override
	public void keyPressed(IKeyEvent e) {
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

	@Override
	public void keyReleased(IKeyEvent e) {

	}

	@Override
	public void mouseWheelMoved(IMouseEvent e) {
		if (e.getWheelRotation() == 0)
			return;
		body.scroll(-e.getWheelRotation());
	}
}
