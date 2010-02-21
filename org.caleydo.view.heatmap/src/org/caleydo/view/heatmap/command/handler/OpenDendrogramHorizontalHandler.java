package org.caleydo.view.heatmap.command.handler;

import org.caleydo.view.heatmap.GLDendrogram;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

public class OpenDendrogramHorizontalHandler extends AbstractHandler implements
		IHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			HandlerUtil.getActiveWorkbenchWindow(event).getActivePage()
					.showView(GLDendrogram.VIEW_ID + ".horizontal");
		} catch (PartInitException e) {
			e.printStackTrace();
		}

		return null;
	}

}
