/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
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
