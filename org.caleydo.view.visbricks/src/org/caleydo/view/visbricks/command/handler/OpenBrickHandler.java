package org.caleydo.view.visbricks.command.handler;

import org.caleydo.view.visbricks.brick.GLBrick;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

public class OpenBrickHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			HandlerUtil.getActiveWorkbenchWindow(event).getActivePage()
					.showView(GLBrick.VIEW_ID);
		} catch (PartInitException e) {
			e.printStackTrace();
		}

		return null;
	}
}
