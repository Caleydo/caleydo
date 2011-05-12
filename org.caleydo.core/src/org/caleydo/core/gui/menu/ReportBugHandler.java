package org.caleydo.core.gui.menu;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.event.view.browser.ChangeURLEvent;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

public class ReportBugHandler
	extends AbstractHandler
	implements IHandler {

	private final static String URL_REPORT_BUG = "https://trac.icg.tugraz.at/projects/org.caleydo/newticket";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		try {
			HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().showView("org.caleydo.view.browser");
		}
		catch (PartInitException e) {
			e.printStackTrace();
		}

		ChangeURLEvent changeURLEvent = new ChangeURLEvent();
		changeURLEvent.setSender(this);
		changeURLEvent.setUrl(URL_REPORT_BUG);
		GeneralManager.get().getEventPublisher().triggerEvent(changeURLEvent);

		return null;
	}
}
