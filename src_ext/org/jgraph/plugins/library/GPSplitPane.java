/*
 * @(#)GPSplitPane.java 1.0 06.08.2003
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
package org.jgraph.plugins.library;

import java.awt.Component;

import javax.swing.JSplitPane;

import org.jgraph.pad.resources.LocaleChangeAdapter;
import org.jgraph.pad.util.PositionManager;


/**
 * One Layer between the JSplitPane 
 * and our implementation. 
 * Currently we add a load and store 
 * management for the divider position. 
 */
public class GPSplitPane extends JSplitPane {

	/** Calls the super constructor
	 *  and adds the instance to the position manager
	 * 
	 */
	public GPSplitPane() {
		super();
		PositionManager.addComponent(this);
	}

	/** Calls the super constructor
	 *  and adds the instance to the position manager
	 * 
	 * @param newOrientation
	 */
	public GPSplitPane(int newOrientation) {
		super(newOrientation);
		PositionManager.addComponent(this);
	}

	/** Calls the super constructor
	 *  and adds the instance to the position manager
	 * 
	 * @param newOrientation
	 * @param newContinuousLayout
	 */
	public GPSplitPane(int newOrientation, boolean newContinuousLayout) {
		super(newOrientation, newContinuousLayout);
		PositionManager.addComponent(this);
	}

	/** Calls the super constructor
	 *  and adds the instance to the position manager
	 * 
	 * @param newOrientation
	 * @param newLeftComponent
	 * @param newRightComponent
	 */
	public GPSplitPane(
		int newOrientation,
		Component newLeftComponent,
		Component newRightComponent) {
		super(newOrientation, newLeftComponent, newRightComponent);
		PositionManager.addComponent(this);
	}

	/** Calls the super constructor
	 *  and adds the instance to the position manager
	 * 
	 * @param newOrientation
	 * @param newContinuousLayout
	 * @param newLeftComponent
	 * @param newRightComponent
	 */
	public GPSplitPane(
		int newOrientation,
		boolean newContinuousLayout,
		Component newLeftComponent,
		Component newRightComponent) {
		super(
			newOrientation,
			newContinuousLayout,
			newLeftComponent,
			newRightComponent);
			PositionManager.addComponent(this);
	}

	/** Removes the Split Pane from the 
	 *  position manager and calls
	 *  the super implementation. 
	 *  
	 * @see java.lang.Object#finalize()
	 */
	protected void finalize() throws Throwable {
		PositionManager.removeComponent(this);
		super.finalize();
	}

	/** Calls the super implementation
	 *  and makes an update for the
	 *  component by using the locale
	 *  change adapter and the 
	 *  position manager.
	 *  
	 *  @param name the new name
	 *  @see PositionManager#updateComponent(Component)
	 *  @see LocaleChangeAdapter#updateComponent(Component)
	 *  @see java.awt.Component#setName(java.lang.String)
	 * 
	 */
	public void setName(String name) {
		super.setName(name);
		PositionManager.updateComponent(this);
		LocaleChangeAdapter.updateComponent(this);
	}
}
