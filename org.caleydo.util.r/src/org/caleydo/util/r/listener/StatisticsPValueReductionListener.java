/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.util.r.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.data.StatisticsPValueReductionEvent;
import org.caleydo.util.r.RStatisticsPerformer;

public class StatisticsPValueReductionListener extends
		AEventListener<RStatisticsPerformer> {

	public StatisticsPValueReductionListener(RStatisticsPerformer handler) {
		setHandler(handler);
	}

	@Override
	public void handleEvent(AEvent event) {
		StatisticsPValueReductionEvent pValueReductionEvent = null;
		if (event instanceof StatisticsPValueReductionEvent) {
			pValueReductionEvent = (StatisticsPValueReductionEvent) event;
			handler.oneSidedTTest(pValueReductionEvent.getTablePerspectives());
		}
	}
}
