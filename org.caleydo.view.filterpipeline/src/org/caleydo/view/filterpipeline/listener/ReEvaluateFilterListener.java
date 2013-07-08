/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
/**
 *
 */
package org.caleydo.view.filterpipeline.listener;

import org.caleydo.core.data.filter.event.ReEvaluateFilterListEvent;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.view.filterpipeline.GLFilterPipeline;
import org.caleydo.view.filterpipeline.listener.SetFilterTypeEvent.FilterType;

/**
 * @author Thomas Geymayer
 *
 */
public class ReEvaluateFilterListener extends AEventListener<GLFilterPipeline> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof ReEvaluateFilterListEvent)
			handler.handleReEvaluateFilter(FilterType.RECORD);
		// FIXME bad hack - doesn't check for real type

	}

}
