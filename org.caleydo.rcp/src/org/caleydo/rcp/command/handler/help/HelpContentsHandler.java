package org.caleydo.rcp.command.handler.help;

import org.caleydo.core.manager.event.view.browser.ChangeURLEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

public class HelpContentsHandler
	extends AbstractHandler
	implements IHandler {

	private final static String URL_HELP_CONTENTS = "http://www.caleydo.org/help/help.html";

	public Object execute(ExecutionEvent event) throws ExecutionException {

		try {
			HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().showView("org.caleydo.view.browser");
		}
		catch (PartInitException e) {
			e.printStackTrace();
		}

		ChangeURLEvent changeURLEvent = new ChangeURLEvent();
		changeURLEvent.setSender(this);
		changeURLEvent.setUrl(URL_HELP_CONTENTS);
		GeneralManager.get().getEventPublisher().triggerEvent(changeURLEvent);

		return null;
	}
}
