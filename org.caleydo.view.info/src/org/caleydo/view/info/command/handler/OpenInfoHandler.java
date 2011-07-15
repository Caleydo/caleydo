package org.caleydo.view.info.command.handler;

import org.caleydo.view.info.RcpInfoAreaView;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

public class OpenInfoHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			HandlerUtil.getActiveWorkbenchWindow(event).getActivePage()
					.showView(RcpInfoAreaView.VIEW_TYPE);
		} catch (PartInitException e) {
			e.printStackTrace();
		}

		return null;
	}
}
