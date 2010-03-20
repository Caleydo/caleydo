package org.caleydo.util.r.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.data.StatisticsFoldChangeReductionEvent;
import org.caleydo.util.r.RStatisticsPerformer;

public class StatisticsFoldChangeReductionListener extends
		AEventListener<RStatisticsPerformer> {

	@Override
	public void handleEvent(AEvent event) {
		StatisticsFoldChangeReductionEvent foldChangeReductionEvent = null;
		if (event instanceof StatisticsFoldChangeReductionEvent) {
			foldChangeReductionEvent = (StatisticsFoldChangeReductionEvent) event;
			handler.foldChange(foldChangeReductionEvent.getSet1(), foldChangeReductionEvent.getSet2());
		}
	}
}
