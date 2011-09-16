package org.caleydo.view.visbricks.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.data.NewSubDataTablesEvent;
import org.caleydo.view.visbricks.GLVisBricks;

/**
 * Listener for {@link NewSubDataTablesEvent}.
 * 
 * @author Alexander Lex
 */
public class NewSubDataTablesListener extends AEventListener<GLVisBricks> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof NewSubDataTablesEvent) {
			handler.subDataTablesUpdated();
		}
	}
}
