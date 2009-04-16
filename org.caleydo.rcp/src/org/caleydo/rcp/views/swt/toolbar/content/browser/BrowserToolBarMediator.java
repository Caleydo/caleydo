package org.caleydo.rcp.views.swt.toolbar.content.browser;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.swt.browser.EBrowserQueryType;
import org.eclipse.swt.widgets.Button;
import org.caleydo.core.manager.event.view.browser.ChangeQueryTypeEvent;

public class BrowserToolBarMediator {

	/** connection to event-system */
	IEventPublisher eventPublisher;

	List<Button> queryTypeButtons;
	
	public BrowserToolBarMediator() {
		eventPublisher = GeneralManager.get().getEventPublisher();
		queryTypeButtons = new ArrayList<Button>();
	}

	public void changeQueryType(EBrowserQueryType method) {
		for (Button button : queryTypeButtons) {
			if (!button.getText().equals(method.toString())) {
				button.setSelection(false);
			}
		}
		ChangeQueryTypeEvent event = new ChangeQueryTypeEvent();
		event.setQueryType(method);
		eventPublisher.triggerEvent(event);
	}
	
	public void addQueryTypeButton(Button queryTypeButton) {
		queryTypeButtons.add(queryTypeButton);
	}
}
