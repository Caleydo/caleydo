package org.caleydo.rcp.command.handler.view;

import org.caleydo.rcp.view.opengl.RcpGLRadialHierarchyView;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

public class OpenRadialHierarchyHandler
	extends AbstractHandler
	implements IHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			IViewPart vp = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().showView(RcpGLRadialHierarchyView.ID);
			System.out.println("vp="+vp);
		}
		catch (PartInitException e) {
			e.printStackTrace();
		}

		return null;
	}

}
