package org.caleydo.view.scatterplot.command.handler;

import org.caleydo.view.scatterplot.GLScatterPlot;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

public class OpenScatterplotHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			HandlerUtil.getActiveWorkbenchWindow(event).getActivePage()
					.showView(GLScatterPlot.VIEW_ID);
		} catch (PartInitException e) {
			e.printStackTrace();
		}

		return null;
	}

}
