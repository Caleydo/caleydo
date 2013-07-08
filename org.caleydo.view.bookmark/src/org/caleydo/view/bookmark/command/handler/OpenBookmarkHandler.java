/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.bookmark.command.handler;

import org.caleydo.view.bookmark.GLBookmarkView;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

public class OpenBookmarkHandler extends AbstractHandler implements IHandler {

	/**
	 * Counter variable for determination of the secondary view ID. Needed for
	 * multiple instances of the same view type.
	 */
	private static int SECONDARY_ID = 0;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			HandlerUtil
					.getActiveWorkbenchWindow(event)
					.getActivePage()
					.showView(GLBookmarkView.VIEW_TYPE, Integer.toString(SECONDARY_ID++),
							IWorkbenchPage.VIEW_ACTIVATE);

		} catch (PartInitException e) {
			e.printStackTrace();
		}

		return null;
	}
}
