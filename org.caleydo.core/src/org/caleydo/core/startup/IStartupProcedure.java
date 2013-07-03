/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.startup;

import com.google.common.base.Function;

/**
 *
 * @author Samuel Gratzl
 *
 */
public interface IStartupProcedure {

	/**
	 * Initialization stuff that has to be done before the workbench opens
	 * (e.g., copying the workbench data from a serialized project).
	 */
	boolean preWorkbenchOpen();

	/**
	 * the actual work of this startup procedure
	 *
	 * @param setTitle
	 *            callback for setting the window title
	 * @return whether the procedure was successful
	 */
	boolean run(Function<String, Void> setTitle);

	/**
	 * Initialization stuff that has to be done after the workbench opened (e.g., making a specific view activate)
	 **/
	void postWorkbenchOpen();
}
