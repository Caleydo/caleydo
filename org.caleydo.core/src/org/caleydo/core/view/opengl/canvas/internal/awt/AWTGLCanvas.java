/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.canvas.internal.awt;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.util.List;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;

import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.core.view.contextmenu.item.SeparatorMenuItem;
import org.caleydo.core.view.opengl.canvas.AGLCanvas;
import org.caleydo.core.view.opengl.canvas.IGLFocusListener;
import org.caleydo.core.view.opengl.canvas.IGLKeyListener;
import org.caleydo.core.view.opengl.canvas.IGLMouseListener;
import org.caleydo.core.view.opengl.picking.IPickingLabelProvider;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Samuel Gratzl
 *
 */
final class AWTGLCanvas extends AGLCanvas {
	private final GLCanvas canvas;
	private final Composite composite;

	AWTGLCanvas(GLCanvas canvas, Composite composite) {
		super(canvas);
		this.canvas = canvas;
		this.composite = composite;
	}

	/**
	 * @return the canvas
	 */
	GLCanvas getCanvas() {
		return canvas;
	}

	@Override
	public IPickingListener createTooltip(ILabeled label) {
		return new ToolTipPickingListener(label);
	}

	@Override
	public IPickingListener createTooltip(String label) {
		return new ToolTipPickingListener(label);
	}

	@Override
	public IPickingListener createTooltip(IPickingLabelProvider label) {
		return new ToolTipPickingListener(label);
	}


	@Override
	public void addMouseListener(IGLMouseListener listener) {
		AWTMouseAdapter adapter = new AWTMouseAdapter(listener, this);
		canvas.addMouseListener(adapter);
		canvas.addMouseMotionListener(adapter);
		canvas.addMouseWheelListener(adapter);
	}


	@Override
	public void removeMouseListener(IGLMouseListener listener) {
		for (MouseListener l : canvas.getMouseListeners()) {
			if (l instanceof AWTMouseAdapter && ((AWTMouseAdapter) l).getListener() == listener) {
				canvas.removeMouseListener(l);
				canvas.removeMouseMotionListener((AWTMouseAdapter) l);
				canvas.removeMouseWheelListener((AWTMouseAdapter) l);
				break;
			}
		}
	}

	@Override
	public void addFocusListener(IGLFocusListener listener) {
		canvas.addFocusListener(new AWTFocusAdapter(listener));
	}


	@Override
	public void removeFocusListener(IGLFocusListener listener) {
		for (FocusListener l : canvas.getFocusListeners()) {
			if (l instanceof AWTFocusAdapter && ((AWTFocusAdapter) l).getListener() == listener) {
				canvas.removeFocusListener(l);
				break;
			}
		}
	}


	@Override
	public void requestFocus() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				canvas.requestFocus();
			}
		});
	}


	@Override
	public void addKeyListener(IGLKeyListener listener) {
		canvas.addKeyListener(new AWTKeyAdapter(listener));
	}

	@Override
	public void removeKeyListener(IGLKeyListener listener) {
		for (KeyListener l : canvas.getKeyListeners()) {
			if (l instanceof AWTKeyAdapter && ((AWTKeyAdapter) l).getListener() == listener) {
				canvas.removeKeyListener(l);
				break;
			}
		}
	}

	@Override
	public void addGLEventListener(GLEventListener listener) {
		canvas.addGLEventListener(listener);
	}


	@Override
	public void removeGLEventListener(GLEventListener listener) {
		canvas.removeGLEventListener(listener);
	}

	@Override
	public GLAutoDrawable asGLAutoDrawAble() {
		return canvas;
	}

	@Override
	public Composite asComposite() {
		return composite;
	}

	@Override
	public void showPopupMenu(final Iterable<? extends AContextMenuItem> items) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JPopupMenu menu = new JPopupMenu();
				for (AContextMenuItem item : items)
					create(menu, item);

				GLCanvas canvas = getCanvas();
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

	@Override
	public String toString() {
		return "AWTGLCanvas of " + canvas.getName();
	}

}
