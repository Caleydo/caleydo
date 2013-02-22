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
package org.caleydo.core.view.opengl.canvas.internal.awt;

import java.awt.Frame;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.media.opengl.GLCapabilitiesImmutable;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;

import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.core.view.contextmenu.item.SeparatorMenuItem;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.canvas.internal.IGLCanvasFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Samuel Gratzl
 *
 */
public class AWTGLCanvasFactory implements IGLCanvasFactory {

	@Override
	public AWTGLCanvas create(GLCapabilitiesImmutable caps, Composite parent) {
		GLCanvas canvas = new GLCanvas(caps);
		Composite composite = new Composite(parent, SWT.EMBEDDED);
		Frame frameGL = SWT_AWT.new_Frame(composite);
		frameGL.add(canvas);
		return new AWTGLCanvas(canvas, composite);
	}

	@Override
	public void showPopupMenu(final IGLCanvas canvas, final Iterable<? extends AContextMenuItem> items) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JPopupMenu menu = new JPopupMenu();
				for (AContextMenuItem item : items)
					create(menu, item);

				AWTGLCanvas glcanvas = (AWTGLCanvas) canvas;
				GLCanvas canvas = glcanvas.getCanvas();
				Point location = MouseInfo.getPointerInfo().getLocation();
				int cursorLocationX = location.x - canvas.getLocationOnScreen().x;
				int cursorLocationY = location.y - canvas.getLocationOnScreen().y;
				menu.show(canvas.getParent(), cursorLocationX, cursorLocationY);
			}
		});

	}

	private void create(JComponent parent, final AContextMenuItem item) {
		if (item instanceof SeparatorMenuItem) {
			((JPopupMenu) parent).addSeparator();
			return;
		}

		List<AContextMenuItem> subItems = item.getSubMenuItems();
		if (!subItems.isEmpty()) {
			final JMenu submenu = new JMenu();
			for (AContextMenuItem subMenuItem : subItems) {
				create(submenu, subMenuItem);
			}
			submenu.setText(item.getLabel());
			parent.add(submenu);
		} else {
			JMenuItem menuItem;
			switch (item.getType()) {
			case CHECK:
				menuItem = new JCheckBoxMenuItem(item.getLabel(), item.isState());
				break;
			case RADIO:
				menuItem = new JRadioButtonMenuItem(item.getLabel(), item.isState());
				break;
			default:
				menuItem = new JMenuItem(item.getLabel());
				break;
			}
			menuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					item.triggerEvent();
				}
			});
			parent.add(menuItem);
		}
	}
}
