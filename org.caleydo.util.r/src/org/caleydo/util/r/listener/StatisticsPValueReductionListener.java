package org.caleydo.util.r.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.data.StatisticsPValueReductionEvent;
import org.caleydo.util.r.RStatisticsPerformer;

public class StatisticsPValueReductionListener extends
		AEventListener<RStatisticsPerformer> {

	@Override
	public void handleEvent(AEvent event) {
		StatisticsPValueReductionEvent pValueReductionEvent = null;
		if (event instanceof StatisticsPValueReductionEvent) {
			pValueReductionEvent = (StatisticsPValueReductionEvent) event;
			handler.oneSidedTTest(pValueReductionEvent.getTables());
		}
	}
}
