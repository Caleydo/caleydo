package org.caleydo.util.r.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.data.StatisticsResultFinishedEvent;
import org.caleydo.util.r.view.StatisticsView;

public class StatisticsResultFinishedEventListener extends AEventListener<StatisticsView> {

	@Override
	public void handleEvent(AEvent event) {
		StatisticsResultFinishedEvent statisticsFinishedResultsEvent = null;
		if (event instanceof StatisticsResultFinishedEvent) {
			statisticsFinishedResultsEvent = (StatisticsResultFinishedEvent) event;
			handler.resultFinished(statisticsFinishedResultsEvent.getSets());
		}
	}
}
