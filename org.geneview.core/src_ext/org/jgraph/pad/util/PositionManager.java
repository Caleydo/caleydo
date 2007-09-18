/*
 * @(#)PositionManager.java 1.0 05.08.2003
 *
 * Copyright (C) 2003 sven_luzar
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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JSplitPane;

public class PositionManager {

	/**
	 * Key suffix for the Registry to save and load the frame width for the
	 * component
	 */
	protected static final String FRAME_WIDTH = ".FrameWidth";

	/**
	 * Key suffix for the Registry to save and load the frame height for the
	 * component
	 */
	protected static final String FRAME_HEIGHT = ".FrameHeight";

	/**
	 * Key suffix for the Registry to save and load the frame state for the
	 * component
	 */
	protected static final String FRAME_STATE = ".FrameState";

	/**
	 * Key suffix for the Registry to save and load the frame x position for the
	 * component
	 */
	protected static final String FRAME_X = ".FrameX";

	/**
	 * Key suffix for the Registry to save and load the frame y position for the
	 * component
	 */
	protected static final String FRAME_Y = ".FrameY";

	/**
	 * Key suffix for the Registry to save and load the dividerlocation for the
	 * JSplitPane
	 * 
	 */
	protected static final String DIVIDER_LOCATION = ".DividerLocation";

	/**
	 * vector with all registered containers
	 * 
	 */
	protected static ArrayList containers = new ArrayList();

	/**
	 * we only need one adapter for all components
	 * 
	 */
	protected static PosComponentListener posComponentListener = new PosComponentListener();

	/**
	 * We only need one listener for all split panes. The listener reacts at
	 * each movement and stores the new position.
	 * 
	 */
	protected static PosPropertyChangeListener posPropertyChangeListener = new PosPropertyChangeListener();

	/**
	 * Adds a component to the control of the position manager.
	 * 
	 * <b>Note:</b> Register the Container before opening a window! For
	 * example:
	 * 
	 * <pre>
	 * JFrame f = new JFrame();
	 * PositionManager.addContainer(f);
	 * f.setVisible(true);
	 * </pre>
	 * 
	 * Adds the needed listeners at the component. Before the addition the
	 * method reads the old position values and sets them to the component.
	 * 
	 * If the method can't find old values then the method will try to get the
	 * screen size and will set 2/3 % from the screen size to the frame. If
	 * there is no availabe screen size the method uses the 400 x 600
	 * dimensions.
	 * 
	 * Currently we a support for the following components
	 * <ul>
	 * <li>{@link java.awt.Window}</li>
	 * <li>{@link javax.swing.JInternalFrame}</li>
	 * <li>{@link javax.swing.JSplitPane}</li>
	 * </ul>
	 * 
	 * @param comp
	 *            the concerning container
	 * 
	 */
	public static void addComponent(Component comp) {
		if (comp == null)
			return;

		if (comp instanceof JSplitPane) {
			JSplitPane jsp = (JSplitPane) comp;
			// set the old positions to the internal frame
			updateComponent(comp);

			// register the listener to store
			// the positions for the future
			jsp.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY,
					posPropertyChangeListener);

		} else

		// do not set the position for all components
		// use only windows
		if (comp instanceof JInternalFrame || comp instanceof Window) {
			// set the old positions to the internal frame
			updateComponent(comp);

			// register the listener to store
			// the positions for the future
			comp.addComponentListener(posComponentListener);

		} else
			throw new IllegalArgumentException(
					"We have no position storage implementation for "
							+ comp.getClass());

	}

	/**
	 * The method reads the old position values and sets them to the component.
	 * 
	 * If the method can't find old values then the method will try to get the
	 * screen size and will set 2/3 % from the screen size to the frame. If
	 * there is no availabe screen size the method uses the 400 x 600
	 * dimensions.
	 * 
	 * Currently we a support for the following components
	 * <ul>
	 * <li>{@link java.awt.Window}</li>
	 * <li>{@link javax.swing.JInternalFrame}</li>
	 * <li>{@link javax.swing.JSplitPane}</li>
	 * </ul>
	 * 
	 * @param comp
	 *            the concerning container
	 */
	public static void updateComponent(Component comp) {
		if (comp == null)
			return;

		if (comp instanceof JSplitPane) {
			JSplitPane jsp = (JSplitPane) comp;
			// set the old positions to the internal frame
			int pos = PositionManager.getIntPos(jsp,
					PositionManager.DIVIDER_LOCATION, -1);

			if (pos == -1)
				jsp.setDividerLocation(100);
			else
				jsp.setDividerLocation(pos);

		} else

		// do not set the position for all components
		// use only windows
		if (comp instanceof JInternalFrame || comp instanceof Window) {
			// set the old positions to the internal frame
			int x = PositionManager
					.getIntPos(comp, PositionManager.FRAME_X, -1);
			int y = PositionManager
					.getIntPos(comp, PositionManager.FRAME_Y, -1);
			int width = PositionManager.getIntPos(comp,
					PositionManager.FRAME_WIDTH, -1);
			int height = PositionManager.getIntPos(comp,
					PositionManager.FRAME_HEIGHT, -1);
			if (x == -1 || y == -1 || width == -1 || height == -1) {

				Dimension d;
				if (comp instanceof JInternalFrame) {
					JInternalFrame jif = (JInternalFrame) comp;
					Container cont = jif.getParent();
					if (cont == null
							|| (cont.getSize().width == 0 && cont.getSize().height == 0)) {
						// use the dimension if no more information is available
						d = new Dimension(600, 400);
					} else {
						// will use the desktop pane size
						// if available
						d = cont.getSize();
					}
				} else {
					// use the scree size by default
					d = comp.getToolkit().getScreenSize();
				}

				int h = d.height;
				int w = d.width;
				height = (int) (h * 0.66);
				width = (int) (w * 0.66);
				x = (int) ((double) (h - height) / 2);
				y = (int) ((double) (w - width) / 2);
			}

			Rectangle r = new Rectangle(x, y, width, height);
			comp.setBounds(r);

			if (comp instanceof JComponent) {
				((JComponent) comp).setPreferredSize(new Dimension(width,
						height));
				((JComponent) comp).setSize(new Dimension(width, height));
			}

		} else
			throw new IllegalArgumentException(
					"We have no position storage implementation for "
							+ comp.getClass());

	}

	/**
	 * Removes a component from the control of the position manager. The method
	 * removes the unused listeners from the component.
	 * 
	 */
	public static void removeComponent(Component comp) {
		if (comp == null)
			return;

		if (comp instanceof JSplitPane) {
			JSplitPane jsp = (JSplitPane) comp;
			jsp.removePropertyChangeListener(
					JSplitPane.DIVIDER_LOCATION_PROPERTY,
					posPropertyChangeListener);
		} else
		// do not set the position for all components
		// use only windows
		if (comp instanceof JInternalFrame || comp instanceof Window) {
			comp.removeComponentListener(posComponentListener);
		} else
			throw new IllegalArgumentException(
					"We have no position storage implementation for "
							+ comp.getClass());
	}

	/**
	 * Sets a value for the component.
	 * 
	 * You can use the extension to differ several values for the component.
	 * 
	 * If you use the same extension at the
	 * {@link #getIntPos(Component, String, int)} method, you will get back the
	 * value.
	 * 
	 * @param comp
	 *            the component
	 */
	public static void setIntPos(Component comp, String extension, int value) {
		//TODO use JNLP preferences!
		//Preferences p = getPreferences();
		//p.putInt(getKey(comp) + extension, value);
	}

	/**
	 * Gets the key for the component. The key is addition from the full
	 * qualified class name and the name property. If the name property is null,
	 * you only will get the full qualified class name.
	 * 
	 * If you have got two frames with the same class but with different window
	 * positions, then you should use different names and everything will work
	 * fine.
	 * 
	 * @param comp
	 */
	public static String getKey(Component comp) {
		/*
		 * 
		 * a full quallifier produces too log keys! we only have 80 chars
		 * 
		 * 
		 * Stack names = new Stack(); do { String name = comp.getName(); if
		 * (name != null) names.push(name); String className =
		 * comp.getClass().getName();
		 * 
		 * if (className != null); names.push(className);
		 * 
		 * comp = comp.getParent(); } while (comp != null);
		 * 
		 * String key = null; while (!names.empty()) { if (key != null) key =
		 * key + "_" + names.pop(); else key = "" + names.pop(); }
		 */
		String key = comp.getClass().getName();
		if (comp.getName() != null)
			key = key + "." + comp.getName();

		return key;
	}

	/**
	 * Returns an int value from the preferences.
	 * 
	 * You can use the extension to store differ values for on component
	 * 
	 * @param comp
	 *            the component
	 * @param extension
	 *            a value to save different values for the component
	 * @param defaultValue
	 *            the default value
	 * @return the value, if available or the default value
	 * 
	 * @see #setIntPos(Component, String, int)
	 */
	public static int getIntPos(Component comp, String extension,
			int defaultValue) {
		return -1;//TODO use JNLP Preferences!
		//Preferences p = getPreferences();
		//return p.getInt(getKey(comp) + extension, defaultValue);
	}
}

/**
 * This property change listener stores some special properties from component
 * objects at the user preferences.
 * 
 * @author sven_luzar
 * @version 1.0
 * 
 */
class PosPropertyChangeListener implements PropertyChangeListener {

	/**
	 * Stores the property value for some special properties.
	 * 
	 * The method considers the property
	 * <ul>
	 * <li>{@link JSplitPane#DIVIDER_LOCATION_PROPERTY}</li>
	 * </ul>
	 * 
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == null)
			return;

		if (evt.getPropertyName() == JSplitPane.DIVIDER_LOCATION_PROPERTY
				&& ((Integer) evt.getOldValue()).intValue() != -1) {

			if (evt.getSource() instanceof JSplitPane) {
				try {
					JSplitPane jsp = (JSplitPane) evt.getSource();
					PositionManager.setIntPos(jsp,
							PositionManager.DIVIDER_LOCATION, jsp
									.getDividerLocation());
				} catch (Exception ex) {
					System.err.println(ex);
				}
			}
		}
	}
}

/**
 * The component listener registers each movement from a component and stores
 * the bounds from this comonent in the user preferences.
 * 
 * @author sven_luzar
 * @version 1.0
 * 
 */

class PosComponentListener implements ComponentListener {

	/**
	 * calls {@link #storeComponentPosition(ComponentEvent)}
	 * 
	 */
	public void componentHidden(ComponentEvent e) {
		storeComponentPosition(e);

	}

	/**
	 * calls {@link #storeComponentPosition(ComponentEvent)}
	 * 
	 */
	public void componentMoved(ComponentEvent e) {
		storeComponentPosition(e);

	}

	/**
	 * calls {@link #storeComponentPosition(ComponentEvent)}
	 * 
	 */
	public void componentResized(ComponentEvent e) {
		storeComponentPosition(e);

	}

	/**
	 * calls {@link #storeComponentPosition(ComponentEvent)}
	 * 
	 */
	public void componentShown(ComponentEvent e) {
		storeComponentPosition(e);

	}

	/**
	 * Stores the component bounds in the user preferences.
	 * 
	 * @param e
	 * @see PositionManager#setIntPos(Component, String, int)
	 * 
	 */
	private void storeComponentPosition(ComponentEvent e) {

		Component c = e.getComponent();
		try {
			// get the bounds
			Rectangle b = c.getBounds();

			// store the bounds
			PositionManager.setIntPos(c, PositionManager.FRAME_WIDTH, b.width);
			PositionManager
					.setIntPos(c, PositionManager.FRAME_HEIGHT, b.height);
			PositionManager.setIntPos(c, PositionManager.FRAME_X, b.x);
			PositionManager.setIntPos(c, PositionManager.FRAME_Y, b.y);

			// will store the state if it is a frame
			if (c instanceof Frame) {
				Frame f = (Frame) c;
				PositionManager.setIntPos(f, PositionManager.FRAME_STATE, f
						.getState());
			}
		} catch (Exception ex) {
			System.err.println(ex);
		}
	}
}
