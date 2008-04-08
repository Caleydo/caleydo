/*
 * @(#)AbstractActionCheckBox.java	1.2 01.02.2003
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

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;

import org.jgraph.pad.coreframework.GPBarFactory;

public abstract class AbstractActionCheckBox extends AbstractActionToggle {

	/**
	 * @see org.jgraph.pad.actions.GPAbstractActionDefault#getMenuComponent(String)
	 */
	protected Component getMenuComponent(String actionCommand) {
		JCheckBoxMenuItem button = new JCheckBoxMenuItem(this);
		abstractButtons.add(button);
		GPBarFactory.fillMenuButton(button, getName(), actionCommand);
		String presentationText = getPresentationText(actionCommand);
		if (presentationText != null)
			button.setText(presentationText);

		return button;
	}

	/**
	 * @see org.jgraph.pad.actions.GPAbstractActionDefault#getToolComponent(String)
	 */
	public Component getToolComponent(String actionCommand) {
		JCheckBox button = new JCheckBox(this);
		abstractButtons.add(button);
		GPBarFactory.fillToolbarButton(button, getName(), actionCommand);
		return button;
	}
}
