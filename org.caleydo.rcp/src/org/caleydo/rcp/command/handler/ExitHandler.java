package org.caleydo.rcp.command.handler;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.rcp.Activator;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.handlers.HandlerUtil;

public class ExitHandler
	extends AbstractHandler
	implements IHandler {
	public Object execute(ExecutionEvent event) throws ExecutionException {
		HandlerUtil.getActiveWorkbenchWindow(event).close();
		GeneralManager.get().getLogger().log(new Status(Status.INFO, Activator.PLUGIN_ID, "Bye bye!"));
		return null;
	}
}
