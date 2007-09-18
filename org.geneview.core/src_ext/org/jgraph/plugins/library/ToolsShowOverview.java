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
package org.jgraph.plugins.library;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.MissingResourceException;

import javax.swing.JDialog;
import javax.swing.JPanel;

import org.jgraph.pad.coreframework.GPAbstractActionDefault;
import org.jgraph.pad.resources.Translator;

public class ToolsShowOverview extends GPAbstractActionDefault {

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		JDialog overviewDlg;
		try {
			String title = Translator.getString("OverviewFrameTitle");
			overviewDlg = new JDialog(graphpad.getFrame(), title, false);
		} catch (MissingResourceException mre) {
			overviewDlg = new JDialog(graphpad.getFrame(), "Overview", false);
		}

		Container fContentPane = overviewDlg.getContentPane();

		fContentPane.setLayout(new BorderLayout());

		GPOverviewPanel gpOverviewPanel = new GPOverviewPanel(getGraphpad(),
				getCurrentDocument().getGraph(), getCurrentDocument());
		JPanel overviewPanel = GPOverviewPanel.createOverviewPanel(graphpad,
				graphpad.getCurrentGraph(), getCurrentDocument(),
				gpOverviewPanel);
		fContentPane.add(overviewPanel);
		overviewDlg.setSize(new Dimension(180, 180));
		overviewDlg.setLocationRelativeTo(graphpad.getFrame());
		overviewPanel.revalidate();
		// getCurrentDocument().setOverviewDialog(overviewDlg);

		overviewDlg.setVisible(true);
	}

}
