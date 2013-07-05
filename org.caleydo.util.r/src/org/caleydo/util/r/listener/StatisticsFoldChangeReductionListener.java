/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.util.r.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.data.StatisticsFoldChangeReductionEvent;
import org.caleydo.util.r.RStatisticsPerformer;

public class StatisticsFoldChangeReductionListener extends
		AEventListener<RStatisticsPerformer> {

	public StatisticsFoldChangeReductionListener(RStatisticsPerformer handler) {
		setHandler(handler);
	}
	@Override
	public void handleEvent(AEvent event) {
		StatisticsFoldChangeReductionEvent foldChangeReductionEvent = null;
		if (event instanceof StatisticsFoldChangeReductionEvent) {
			foldChangeReductionEvent = (StatisticsFoldChangeReductionEvent) event;
			handler.foldChange(foldChangeReductionEvent.getTablePerspective1(),
					foldChangeReductionEvent.getTablePerspective2(),
					foldChangeReductionEvent.isBetweenRecords());
		}
	}
}
