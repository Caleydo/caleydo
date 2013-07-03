/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.filter.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.view.filter.RcpFilterView;

/**
 * Listener reacting on filter updates.
 * 
 * @author Marc Streit
 */
public class FilterUpdateListener extends AEventListener<RcpFilterView> {

	@Override
	public void handleEvent(AEvent event) {
		handler.handleFilterUpdatedEvent();
	}
}
