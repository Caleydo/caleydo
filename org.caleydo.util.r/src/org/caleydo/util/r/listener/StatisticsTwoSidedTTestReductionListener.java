package org.caleydo.util.r.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.data.StatisticsTwoSidedTTestReductionEvent;
import org.caleydo.util.r.RStatisticsPerformer;

public class StatisticsTwoSidedTTestReductionListener extends
		AEventListener<RStatisticsPerformer> {

	@Override
	public void handleEvent(AEvent event) {
		StatisticsTwoSidedTTestReductionEvent pValueReductionEvent = null;
		if (event instanceof StatisticsTwoSidedTTestReductionEvent) {
			pValueReductionEvent = (StatisticsTwoSidedTTestReductionEvent) event;
			handler.twoSidedTTest(pValueReductionEvent.getDataTables());
		}
	}
}
