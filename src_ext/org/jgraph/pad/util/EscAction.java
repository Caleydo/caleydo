/*
 * Copyright (C) 2001-2004 Gaudenz Alder
 *
 * 6/01/2006: I, Raphpael Valyi, changed back the header of this file to LGPL
 * because nobody changed the file significantly since the last
 * 3.0 version of GPGraphpad that was LGPL. By significantly, I mean: 
 *  - less than 3 instructions changes could honnestly have been done from an old fork,
 *  - license or copyright changes in the header don't count
 *  - automaticaly updating imports don't count,
 *  - updating systematically 2 instructions to a library specification update don't count.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package org.jgraph.pad.util;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;

/**The action analysis the source from the action event.
 * If it is a JInternalFrame or a Window, then
 * the action will call the dispose method. 
 */
public class EscAction extends AbstractAction {

	JButton button = null;

	/** Creates a new instance
	 * 
	 */
	public EscAction() {
		super();
	}

	/** Creates a new instance for the esc button
	 * 
	 */
	public EscAction(JButton button) {
		super();
		this.button = button;
	}

	/**If the button is set, then
	 * the method will call the do click
	 * method at the button.
	 * 
	 * If the event source 
	 * is a JInternalFrame or a Window, then
	 * the action will call the dispose method. 
	 * 
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 * @see javax.swing.JButton#doClick()
	 */
	public void actionPerformed(ActionEvent e) {
		System.out.println(e.getSource().getClass());

		// is the esc button set?
		if (button != null){
			button.doClick();
			return;
		}
			 

		// is it an internal frame?
		try {
			JInternalFrame jif =
				(JInternalFrame) SwingUtilities.getAncestorOfClass(
					JInternalFrame.class,
					(Component) e.getSource());
			if (jif != null) {
				jif.dispose();
				return;
			}
		} catch (Exception ex) {
			System.err.println(ex);
		}

		// is it an window?
		try {
			Window w =
				(Window) SwingUtilities.getAncestorOfClass(
					Window.class,
					(Component) e.getSource());
			if (w != null) {
				w.dispose();
				return;
			}
		} catch (Exception ex) {
			System.err.println(ex);
		}

	}
}
