/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.enroute.command.handler;

import org.caleydo.view.enroute.GLEnRoutePathway;
import org.caleydo.view.pathway.GLPathway;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

public class OpenEnRouteHandler
	extends AbstractHandler
	implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			IWorkbenchWindow activeWindows = HandlerUtil.getActiveWorkbenchWindow(event);

			activeWindows.getActivePage().showView(GLPathway.VIEW_TYPE);
			activeWindows.getActivePage().showView(GLEnRoutePathway.VIEW_TYPE);

		}
		catch (PartInitException e) {
			e.printStackTrace();
		}

		return null;
	}
}
