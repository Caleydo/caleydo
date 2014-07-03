/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.IListenerOwner;
import org.caleydo.core.view.IMultiTablePerspectiveBasedView;

/**
 * Listener for {@link RemoveTablePerspectiveEvent}s.
 *
 * @author Alexander Lex
 *
 */
public class RemoveTablePerspectiveListener<T extends IMultiTablePerspectiveBasedView & IListenerOwner> extends
		AEventListener<T> {
	/**
	 *
	 */
	public RemoveTablePerspectiveListener() {
	}

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof RemoveTablePerspectiveEvent) {
			RemoveTablePerspectiveEvent rEvent = (RemoveTablePerspectiveEvent) event;
			if (rEvent.getReceiver() == null || rEvent.getReceiver() == handler) {
				handler.removeTablePerspective(rEvent.getTablePerspective());
			}
		}
	}

}
