/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.stratomex.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.view.listener.AddTablePerspectivesEvent;
import org.caleydo.view.stratomex.GLStratomex;

/**
 * Listener for the event {@link AddGroupsToStratomexEvent}.
 *
 * @author Christian Partl
 * @auhtor Alexander Lex
 *
 */
public class AddGroupsToStratomexListener extends AEventListener<GLStratomex> {

	@Override
	public void handleEvent(AEvent event) {

		if (event instanceof AddTablePerspectivesEvent) {
			AddTablePerspectivesEvent addTablePerspectivesEvent = (AddTablePerspectivesEvent) event;
			if (addTablePerspectivesEvent.getReceiver() == handler) {
				handler.addTablePerspectives(addTablePerspectivesEvent.getTablePerspectives(), null, null);
			}
		}
	}


}
