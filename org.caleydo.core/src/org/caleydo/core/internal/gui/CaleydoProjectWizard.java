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
package org.caleydo.core.internal.gui;

import java.util.Map;

import org.caleydo.core.internal.MyPreferences;
import org.caleydo.core.startup.IStartupAddon;
import org.caleydo.core.startup.IStartupProcedure;
import org.caleydo.core.util.collection.Pair;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

/**
 * Wizard that appears after Caleydo startup.
 *
 * @author Marc Streit
 * @author Werner Puff
 * @author Alexander Lex
 */
public class CaleydoProjectWizard
	extends Wizard {

	private IStartupProcedure result;
	private final Map<String, IStartupAddon> addons;
	/**
	 * Constructor.
	 */
	public CaleydoProjectWizard(final Shell parentShell, Map<String, IStartupAddon> addons) {

		this.setWindowTitle("Caleydo - Choose Data Source");

		Monitor primary = parentShell.getDisplay().getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = parentShell.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		parentShell.setLocation(x, y);
		parentShell.setActive();

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
		if (page.isPageComplete() && addon.getSecond().validate()) {
			MyPreferences.setLastChosenProjectMode(addon.getFirst());
			MyPreferences.flush();

			setResult(addon.getSecond().create());
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