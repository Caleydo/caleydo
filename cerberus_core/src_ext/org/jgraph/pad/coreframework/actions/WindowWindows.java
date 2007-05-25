/*
 * @(#)WindowWindows.java	1.2 09.02.2003
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

import java.awt.event.ActionEvent;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.beans.PropertyVetoException;

import javax.swing.JInternalFrame;
import javax.swing.JMenu;

import org.jgraph.pad.coreframework.GPDocFrame;

public class WindowWindows
	extends AbstractActionList implements ContainerListener {

  /**
	 * The menu.
	 */
  protected JMenu menu;
  
	/**
	 * Constructs an instance.
	 * 
	 * @param graphpad
	 */
	public WindowWindows() {
		super();
		getMenuBarComponent();
		graphpad.addDesktopContainerListener(this);
	}
	
	/**
	 * @see org.jgraph.pad.actions.AbstractActionList#getItems()
	 */
	protected Object[] getItems() {
		return new Object[] {};
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		GPDocFrame iframe = (GPDocFrame) getSelectedItem(e);
		try {
		  iframe.setIcon(false);
		  iframe.setSelected(true);
		} catch(PropertyVetoException ex) {
		}
		iframe.toFront();
	}

	/**
	 * @see org.jgraph.pad.actions.AbstractActionList#getMenuBarComponent()
	 */
	protected JMenu getMenuBarComponent() {
	  if (menu == null) {
	    menu = super.getMenuBarComponent();
	  }
		return menu;
	}

	/**
	 * @see org.jgraph.pad.actions.GPAbstractActionDefault#getPresentationText(java.lang.String)
	 */
	public String getPresentationText(String actionCommand) {
		return actionCommand;
	}

	/**
	 * @see org.jgraph.pad.actions.AbstractActionList#getItemPresentationText(java.lang.Object)
	 */
	public String getItemPresentationText(Object itemValue) {
		return null;
	}

  /**
   * @see java.awt.event.ContainerListener#componentAdded(java.awt.event.ContainerEvent)
   */
  public void componentAdded(ContainerEvent e) {
    updateMenuItems();
  }
  
  /**
   * @see java.awt.event.ContainerListener#componentRemoved(java.awt.event.ContainerEvent)
   */
  public void componentRemoved(ContainerEvent e) {
    updateMenuItems();
  }
  
  /**
   * Update the menu items.
   */
  protected void updateMenuItems() {
	  menu.removeAll();
		JInternalFrame[] iframes = graphpad.getAllFrames();
		for (int i = 0; i < iframes.length; i++) {
			GPDocFrame iframe = (GPDocFrame) iframes[i];
			menu.add(getMenuComponent(iframe.getDocument().getFrameTitle(), iframe));
		}
	  if (menu.getMenuComponentCount() > 0) {
	    setEnabled(true);
	  } else {
	    setEnabled(false);
	  }
	  menu.invalidate();
  }
}
