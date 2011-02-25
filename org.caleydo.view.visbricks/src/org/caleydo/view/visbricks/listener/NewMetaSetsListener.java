package org.caleydo.view.visbricks.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.data.NewMetaSetsEvent;
import org.caleydo.view.visbricks.GLVisBricks;

/**
 * Listener for {@link NewMetaSetsEvent}.
 * 
 * @author Alexander Lex
 */
public class NewMetaSetsListener extends AEventListener<GLVisBricks> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof NewMetaSetsEvent) {
			handler.metaSetsUpdated();
		}
	}
}
