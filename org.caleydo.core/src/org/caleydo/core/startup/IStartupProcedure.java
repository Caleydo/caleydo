/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.startup;

import org.eclipse.ui.application.IWorkbenchWindowConfigurer;

/**
 *
 * @author Samuel Gratzl
 *
 */
public interface IStartupProcedure extends Runnable {

	/**
	 * Initialization stuff that has to be done before the workbench opens
	 * (e.g., copying the workbench data from a serialized project).
	 */
	boolean preWorkbenchOpen();

	/**
	 * Initialization stuff that has to be done after the workbench opened (e.g., making a specific view activate)
	 **/
	void postWorkbenchOpen(IWorkbenchWindowConfigurer configurer);
}
