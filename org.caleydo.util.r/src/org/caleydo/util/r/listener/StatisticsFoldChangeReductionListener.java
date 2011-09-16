package org.caleydo.util.r.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.data.StatisticsFoldChangeReductionEvent;
import org.caleydo.util.r.RStatisticsPerformer;

public class StatisticsFoldChangeReductionListener extends
		AEventListener<RStatisticsPerformer> {

	@Override
	public void handleEvent(AEvent event) {
		StatisticsFoldChangeReductionEvent foldChangeReductionEvent = null;
		if (event instanceof StatisticsFoldChangeReductionEvent) {
			foldChangeReductionEvent = (StatisticsFoldChangeReductionEvent) event;
			handler.foldChange(foldChangeReductionEvent.getTable1(), foldChangeReductionEvent.getTable2());
		}
	}
}
