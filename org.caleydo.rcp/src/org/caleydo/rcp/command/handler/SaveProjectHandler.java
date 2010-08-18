package org.caleydo.rcp.command.handler;

import org.caleydo.rcp.action.toolbar.general.SaveProjectAction;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;

public class SaveProjectHandler
	extends AbstractHandler
	implements IHandler {
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		new SaveProjectAction().run();

		return null;
	}
}
