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
package org.caleydo.view.treemap.actions;

import org.caleydo.core.event.view.treemap.ToggleLabelEvent;
import org.caleydo.core.gui.toolbar.IToolBarItem;
import org.caleydo.core.gui.toolbar.action.AToolBarAction;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.data.loader.ResourceLoader;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

/**
 * Action for switching labels on/off.
 * 
 * @author Michael Lafer
 * 
 */

public class ToggleLabelAction extends AToolBarAction implements IToolBarItem {

	public static final String TEXT = "Toggle Labels";
	public static final String ICON = "resources/icons/view/hyperbolic/tree_switch_lin.png";

	public ToggleLabelAction() {
		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(PlatformUI.getWorkbench().getDisplay(), ICON)));
		setChecked(true);
	}

	@Override
	public void run() {
		super.run();
		// System.out.println("label: "+isChecked());
		ToggleLabelEvent event = new ToggleLabelEvent();
		event.setDrawLabel(isChecked());
		GeneralManager.get().getEventPublisher().triggerEvent(event);
	};

}
