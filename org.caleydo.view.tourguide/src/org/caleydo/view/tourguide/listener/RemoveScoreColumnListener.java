package org.caleydo.view.tourguide.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.view.tourguide.event.RemoveScoreColumnEvent;
import org.caleydo.view.tourguide.vendingmachine.VendingMachine;

public class RemoveScoreColumnListener extends AEventListener<VendingMachine> {

	public RemoveScoreColumnListener(VendingMachine m) {
		setHandler(m);
	}
	@Override
	public void handleEvent(AEvent event) {
		if (!(event instanceof RemoveScoreColumnEvent))
			return;
		if (handler.getScoreQueryUI() != event.getSender())
			return;
		RemoveScoreColumnEvent e = (RemoveScoreColumnEvent) event;
		handler.onRemoveColumn(e.getScore());
	}
}