/*
 * @(#)ToolsShowOverview.java	1.2 01.02.2003
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
package org.jgraph.plugins.gpgraph;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.JFrame;

import org.jgraph.pad.coreframework.GPAbstractActionDefault;
import org.jgraph.pad.resources.Translator;

public class ToolsShowExplorer extends GPAbstractActionDefault {

	protected transient JFrame explorerDialog;

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if (explorerDialog == null) {
			String title = Translator.getString("ExplorerFrameTitle");
			explorerDialog = new JFrame(title);
			explorerDialog.getContentPane().setLayout(new BorderLayout());
			explorerDialog.getContentPane().add(
				new GPExplorer(graphpad),
				BorderLayout.CENTER);
			explorerDialog.pack();
			explorerDialog.setSize(new Dimension(Math.max(320, explorerDialog.getWidth()),
												Math.max(240, explorerDialog.getHeight())));
		}
		explorerDialog.setVisible(true);
	}

}
