package org.caleydo.view.radial.command.handler;

import org.caleydo.view.radial.GLRadialHierarchy;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

public class OpenRadialHierarchyHandler extends AbstractHandler implements
		IHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			IViewPart vp = HandlerUtil.getActiveWorkbenchWindow(event)
					.getActivePage().showView(GLRadialHierarchy.VIEW_ID);
			System.out.println("vp=" + vp);
		} catch (PartInitException e) {
			e.printStackTrace();
		}

		return null;
	}

}
