package org.caleydo.rcp.command.handler;

import org.caleydo.core.manager.GeneralManager;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;

public class StopTrackingHandler
	extends AbstractHandler
	implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		GeneralManager.get().getTrackDataProvider().stopTracking();

		return null;
	}
}
