/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.internal;

import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.handlers.HandlerUtil;

public class ExitHandler extends AbstractHandler implements IHandler {
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		HandlerUtil.getActiveWorkbenchWindow(event).close();
		Logger.log(new Status(IStatus.INFO, this.toString(), "Bye bye!"));
		return null;
	}
}
