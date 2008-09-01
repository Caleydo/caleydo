package org.caleydo.rcp.command.handler.view;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

public class OpenParCoordsHandler
	extends AbstractHandler
	implements IHandler
{

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		try
		{
			HandlerUtil.getActiveWorkbenchWindow(event)
				.getActivePage().showView("org.caleydo.rcp.views.GLParCoordsView");
		}
		catch (PartInitException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

}
