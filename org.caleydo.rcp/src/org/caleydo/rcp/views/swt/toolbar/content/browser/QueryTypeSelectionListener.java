package org.caleydo.rcp.views.swt.toolbar.content.browser;

import org.caleydo.core.view.swt.browser.EBrowserQueryType;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;

public class QueryTypeSelectionListener
	extends SelectionAdapter {

	BrowserToolBarMediator browserToolBarMediator;

	public QueryTypeSelectionListener(BrowserToolBarMediator mediator) {
		browserToolBarMediator = mediator;
	}
	
	@Override
	public void widgetSelected(SelectionEvent e) {
		browserToolBarMediator.changeQueryType(EBrowserQueryType.valueOf(((Button) e.widget).getText()));
	}

}
