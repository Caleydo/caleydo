/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.internal.gui;

import java.util.Map;

import org.caleydo.core.internal.MyPreferences;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.startup.IStartupAddon;
import org.caleydo.core.startup.IStartupProcedure;
import org.caleydo.core.util.collection.Pair;
import org.eclipse.jface.wizard.Wizard;

/**
 * Wizard that appears after Caleydo startup.
 *
 * @author Marc Streit
 * @author Werner Puff
 * @author Alexander Lex
 */
public class CaleydoProjectWizard extends Wizard {

	private IStartupProcedure result;
	private final Map<String, IStartupAddon> addons;

	/**
	 * Constructor.
	 */
	public CaleydoProjectWizard(Map<String, IStartupAddon> addons) {

		this.setWindowTitle("Caleydo " + GeneralManager.VERSION + " - Project Wizard");
		this.addons = addons;
	}

	@Override
	public void addPages() {
		addPage(new ChooseProjectTypePage(addons));
	}

	private ChooseProjectTypePage getChosenProjectTypePage() {
		return (ChooseProjectTypePage) getPage(ChooseProjectTypePage.PAGE_NAME);
	}

	@Override
	public boolean canFinish() {
		return (getChosenProjectTypePage().isPageComplete());
	}

	@Override
	public boolean performFinish() {
		ChooseProjectTypePage page = getChosenProjectTypePage();
		Pair<String, IStartupAddon> addon = page.getSelectedAddon();
		if (page.isPageComplete()) {
			MyPreferences.setLastChosenProjectMode(addon.getFirst());
			MyPreferences.flush();
			IStartupProcedure startupProcedure = addon.getSecond().create();
			if (startupProcedure == null)
				return false;
			setResult(startupProcedure);
			return true;
		}

		return false;
	}

	/**
	 * @param result
	 *            setter, see {@link result}
	 */
	public void setResult(IStartupProcedure result) {
		this.result = result;
	}

	/**
	 * @return the result, see {@link #result}
	 */
	public IStartupProcedure getResult() {
		return result;
	}

	@Override
	public boolean performCancel() {
		return true;
	}
}
