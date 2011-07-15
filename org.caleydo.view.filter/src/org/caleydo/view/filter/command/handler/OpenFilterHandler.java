package org.caleydo.view.filter.command.handler;

import org.caleydo.view.filter.RcpFilterView;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

public class OpenFilterHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			HandlerUtil.getActiveWorkbenchWindow(event).getActivePage()
					.showView(RcpFilterView.VIEW_TYPE);
		} catch (PartInitException e) {
			e.printStackTrace();
		}

		return null;
	}
}
