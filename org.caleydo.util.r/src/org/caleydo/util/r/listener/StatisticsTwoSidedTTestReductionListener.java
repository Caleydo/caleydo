/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.util.r.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.data.StatisticsTwoSidedTTestReductionEvent;
import org.caleydo.util.r.RStatisticsPerformer;

public class StatisticsTwoSidedTTestReductionListener extends
		AEventListener<RStatisticsPerformer> {

	public StatisticsTwoSidedTTestReductionListener(RStatisticsPerformer handler) {
		setHandler(handler);
	}

	@Override
	public void handleEvent(AEvent event) {
		StatisticsTwoSidedTTestReductionEvent pValueReductionEvent = null;
		if (event instanceof StatisticsTwoSidedTTestReductionEvent) {
			pValueReductionEvent = (StatisticsTwoSidedTTestReductionEvent) event;
			handler.twoSidedTTest(pValueReductionEvent.getTablePerspectives());
		}
	}
}
