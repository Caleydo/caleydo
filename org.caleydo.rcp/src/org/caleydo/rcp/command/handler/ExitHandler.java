package org.caleydo.rcp.command.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.handlers.HandlerUtil;

public class ExitHandler
	extends AbstractHandler
	implements IHandler
{

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		HandlerUtil.getActiveWorkbenchWindow(event).close();

		return null;
	}

}
