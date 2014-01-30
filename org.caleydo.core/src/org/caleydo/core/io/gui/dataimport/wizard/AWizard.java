/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.io.gui.dataimport.wizard;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.wizard.Wizard;

/**
 * @author Thomas Geymayer
 *
 */
public abstract class AWizard<Derived extends AWizard<Derived>> extends Wizard {

	protected Set<AImportDataPage<Derived>> visitedPages = new HashSet<>();

	public void addVisitedPage(AImportDataPage<Derived> page) {
		visitedPages.add(page);
	}
}
