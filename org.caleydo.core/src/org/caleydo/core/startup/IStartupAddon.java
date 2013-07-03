/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.startup;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

/**
 * an addon either for the overall caleydo wizard or for handling command line arguments
 * 
 * @author Samuel Gratzl
 * 
 */
public interface IStartupAddon {

	/**
	 * @return true if no wizard is needed and the {@link IStartupProcedure} is already configured
	 */
	boolean init();

	/**
	 * creates an optional tab item composite for the startup wizard
	 * 
	 * @param parent
	 * @param page
	 * @return
	 */
	Composite create(Composite parent, final WizardPage page);

	/**
	 * validates this page
	 * 
	 * @return
	 */
	boolean validate();

	/**
	 * creates the startup procedure that will load the configured data
	 * 
	 * @return
	 */
	IStartupProcedure create();
}
