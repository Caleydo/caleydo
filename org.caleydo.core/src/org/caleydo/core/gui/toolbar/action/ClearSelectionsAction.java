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
package org.caleydo.core.gui.toolbar.action;

import org.caleydo.core.data.selection.ESelectionCommandType;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.event.view.ClearSelectionsEvent;
import org.caleydo.core.event.view.SelectionCommandEvent;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.data.loader.ResourceLoader;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class ClearSelectionsAction extends AToolBarAction {

	public static final String TEXT = "Clear all selections";
	public static final String ICON = "resources/icons/view/tablebased/clear_selections.png";

	public ClearSelectionsAction() {
		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(
				PlatformUI.getWorkbench().getDisplay(), ICON)));
	}

	@Override
	public void run() {
		super.run();

		ClearSelectionsEvent event = new ClearSelectionsEvent();
		event.setSender(this);
		GeneralManager.get().getEventPublisher().triggerEvent(event);

		// Was needed for matchmaker that created the selection types
		// dynamically
		// RemoveManagedSelectionTypesEvent resetSelectionTypesEvent = new
		// RemoveManagedSelectionTypesEvent();
		// resetSelectionTypesEvent.setSender(this);
		// GeneralManager.get().getEventPublisher().triggerEvent(resetSelectionTypesEvent);

		SelectionCommand command = new SelectionCommand(ESelectionCommandType.CLEAR_ALL);
		SelectionCommandEvent commandEvent = new SelectionCommandEvent();
		commandEvent.setSelectionCommand(command);
		GeneralManager.get().getEventPublisher().triggerEvent(commandEvent);
		
	};
}
