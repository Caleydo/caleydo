package org.caleydo.rcp.command.handler;

import org.caleydo.core.manager.general.GeneralManager;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;

public class StartTrackingHandler
	extends AbstractHandler
	implements IHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {

		GeneralManager.get().getTrackDataProvider().startTracking();

		return null;
	}
}
