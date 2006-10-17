/*
 * @(#)ViewFit.java	1.2 02.02.2003
 *
 * Copyright (C) 2001-2004 Gaudenz Alder
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.jgraph.pad.coreframework.actions;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.geom.Rectangle2D;

public class ViewFit extends AbstractActionRadioButton {

	public static final String NONE = "None";

	public static final String WINDOW = "Window";

	public static final String PAGE_WIDTH = "PageWidth";

	public static final String PAGE = "Page";

	public ViewFit() {
		super();
		lastActionCommand = NONE;
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if (null != e) {
			lastActionCommand = e.getActionCommand();
		}

		if (NONE.equals(lastActionCommand)) {
			getCurrentDocument().setResizeAction(null);
			getCurrentGraph().setScale(1);
		} else if (WINDOW.equals(lastActionCommand)) {
			Rectangle2D graphBounds = getCurrentGraph().getCellBounds(
					getCurrentGraph().getRoots());
			if (graphBounds == null)
				return;
			Dimension panelBounds = getCurrentDocument().getScrollPane()
					.getViewport().getExtentSize();
			if (panelBounds == null)
				return;
			double x_h = panelBounds.getHeight() / graphBounds.getHeight();
			double x_w = panelBounds.getWidth() / graphBounds.getWidth();
			double scale = x_h * 0.9;
			if (x_w < x_h)
				scale = x_w * 0.9;
			getCurrentDocument().setResizeAction(null);
			getCurrentDocument().getGraph().setScale(scale);
			getCurrentDocument().getGraph().scrollRectToVisible(
					new Rectangle((int) (graphBounds.getX() * scale),
							(int) (graphBounds.getY() * scale),
							(int) (graphBounds.getWidth() * scale),
							(int) (graphBounds.getHeight() * scale)));
		} else if (PAGE.equals(lastActionCommand)) {
			Dimension p = getCurrentGraph().getMinimumSize();
			if (p != null && (p.getWidth() != 0 || p.getHeight() != 0)) {
				Dimension s = getCurrentDocument().getScrollPane()
						.getViewport().getExtentSize();
				double scale = 1;
				if (s.getWidth() / p.getWidth() < s.getHeight() / p.getHeight())
					scale = s.getWidth() / p.getWidth();
				else
					scale = s.getHeight() / p.getHeight();
				scale = Math.max(Math.min(scale, 16), .01);
				getCurrentGraph().setScale(scale);
				getCurrentDocument().setResizeAction(this);
				getCurrentDocument().getDocComponent().repaint();
			}
		} else if (PAGE_WIDTH.equals(lastActionCommand)) {
			Dimension p = getCurrentGraph().getMinimumSize();
			if (p != null && (p.getWidth() != 0 || p.getHeight() != 0)) {
				Dimension s = getCurrentDocument().getScrollPane()
						.getViewport().getExtentSize();
				s.width = s.width - 20;
				double scale = s.getWidth() / p.getWidth();
				scale = Math.max(Math.min(scale, 16), .01);
				getCurrentGraph().setScale(scale);
				getCurrentDocument().setResizeAction(this);
			}
		}

		update();
	}

	/**
	 * @see org.jgraph.pad.actions.AbstractActionRadioButton#getPossibleActionCommands()
	 */
	public String[] getPossibleActionCommands() {
		return new String[] { NONE, WINDOW, PAGE_WIDTH, PAGE };
	}

}
