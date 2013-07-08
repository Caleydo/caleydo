/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.internal;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PlatformUI;

/**
 * simple restart command to restart the whole application
 * @author Samuel Gratzl
 *
 */
public class RestartCommand extends AbstractHandler {
	private static final String PROP_EXIT_CODE = "eclipse.exitcode"; //$NON-NLS-1$

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		System.setProperty(PROP_EXIT_CODE, Integer.toString(24));
		PlatformUI.getWorkbench().restart();
		return null;
	}
}
