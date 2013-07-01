/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.filterpipeline.command.handler;

import org.caleydo.view.filterpipeline.GLFilterPipeline;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

public class OpenFilterPipelineHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {

			HandlerUtil.getActiveWorkbenchWindow(event).getActivePage()
					.showView(GLFilterPipeline.VIEW_TYPE);

		} catch (PartInitException e) {
			e.printStackTrace();
		}

		return null;
	}
}
