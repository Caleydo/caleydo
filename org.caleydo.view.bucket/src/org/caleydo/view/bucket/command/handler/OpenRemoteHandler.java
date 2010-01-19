package org.caleydo.view.bucket.command.handler;

import org.caleydo.view.bucket.GLRemoteRendering;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

public class OpenRemoteHandler extends AbstractHandler implements IHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			HandlerUtil.getActiveWorkbenchWindow(event).getActivePage()
					.showView(GLRemoteRendering.VIEW_ID);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		return null;
	}

}
