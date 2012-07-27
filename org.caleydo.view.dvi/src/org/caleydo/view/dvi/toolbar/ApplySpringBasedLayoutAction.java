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
package org.caleydo.view.dvi.toolbar;

import org.caleydo.core.gui.toolbar.IToolBarItem;
import org.caleydo.core.gui.toolbar.action.AToolBarAction;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.view.dvi.event.ApplySpecificGraphLayoutEvent;
import org.caleydo.view.dvi.layout.ForceDirectedGraphLayout;

public class ApplySpringBasedLayoutAction extends AToolBarAction implements IToolBarItem {

	public static final String TEXT = "Apply Spring-Based Layout";
//	public static final String ICON = "resources/icons/view/radial/radial_color_mapping.png";

	public ApplySpringBasedLayoutAction() {
		setText(TEXT);
		setToolTipText(TEXT);
//		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(
//				PlatformUI.getWorkbench().getDisplay(), ICON)));
		setChecked(false);
	}

	@Override
	public void run() {
		super.run();
		ApplySpecificGraphLayoutEvent event = new ApplySpecificGraphLayoutEvent();
		event.setGraphLayoutClass(ForceDirectedGraphLayout.class);
		event.setSender(this);
		GeneralManager.get().getEventPublisher().triggerEvent(event);
		setChecked(false);
		
		
	};

}
