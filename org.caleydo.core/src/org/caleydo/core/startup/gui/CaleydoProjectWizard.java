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
package org.caleydo.core.startup.gui;

import java.io.IOException;

import org.caleydo.core.gui.preferences.PreferenceConstants;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.startup.ApplicationMode;
import org.caleydo.core.startup.GeneticGUIStartupProcedure;
import org.caleydo.core.startup.SerializationStartupProcedure;
import org.caleydo.core.startup.StartupProcessor;
import org.caleydo.core.startup.gui.ChooseProjectTypePage.EProjectLoadType;
import org.eclipse.jface.preference.PreferenceStore;
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

	/**
	 * Constructor.
	 */
	public CaleydoProjectWizard(final Shell parentShell) {

		this.setWindowTitle("Caleydo - Choose Data Source");

		Monitor primary = parentShell.getDisplay().getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = parentShell.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		parentShell.setLocation(x, y);
		parentShell.setActive();
	}

	@Override
	public void addPages() {
		addPage(new ChooseProjectTypePage());
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
		if (page.isPageComplete()) {
			PreferenceStore prefStore = GeneralManager.get().getPreferenceStore();

			// ProjectMode previousProjectMode =
			// ProjectMode.valueOf(prefStore.getString(PreferenceConstants.LAST_CHOSEN_PROJECT_MODE));

			prefStore.setValue(PreferenceConstants.LAST_CHOSEN_ORGANISM, page.getOrganism()
					.name());

			ProjectMode projectMode = page.getProjectMode();
			if (projectMode == ProjectMode.LOAD_PROJECT) {

				SerializationStartupProcedure startupProcedure = (SerializationStartupProcedure) StartupProcessor
						.get().createStartupProcedure(ApplicationMode.SERIALIZATION);
				EProjectLoadType projectLoadType = page.getProjectLoadType();

				prefStore.setValue(PreferenceConstants.LAST_CHOSEN_PROJECT_LOAD_TYPE,
						projectLoadType.name());

				if (projectLoadType.equals(EProjectLoadType.RECENT)) {
					startupProcedure.setLoadRecentProject(true);
				}
				else {
					startupProcedure.setLoadRecentProject(false);
					String projectFileName = page.getProjectFileName();
					startupProcedure.setProjectLocation(page.getProjectFileName());
					prefStore.setValue(PreferenceConstants.LAST_MANUALLY_CHOSEN_PROJECT,
							projectFileName);

				}
			}
			else if (projectMode == ProjectMode.SAMPLE_PROJECT) {
				SerializationStartupProcedure startupProcedure = (SerializationStartupProcedure) StartupProcessor
						.get().createStartupProcedure(ApplicationMode.SERIALIZATION);
				startupProcedure.loadSampleProject(true);

			}
			else if (projectMode == ProjectMode.GENE_EXPRESSION_SAMPLE_DATA) {

				GeneticGUIStartupProcedure startupProcedure = (GeneticGUIStartupProcedure) StartupProcessor
						.get().createStartupProcedure(ApplicationMode.GUI);
				startupProcedure.setLoadSampleData(true);
			}
			else if (projectMode == ProjectMode.GENE_EXPRESSION_NEW_DATA) {

				StartupProcessor.get().createStartupProcedure(ApplicationMode.GUI);

				GeneralManager.get().getBasicInfo().setOrganism(page.getOrganism());
			}
			else if (projectMode == ProjectMode.UNSPECIFIED_NEW_DATA) {
				StartupProcessor.get().createStartupProcedure(ApplicationMode.GENERIC);

			}
			else {
				throw new IllegalStateException("Not implemented!");
			}

			prefStore.setValue(PreferenceConstants.LAST_CHOSEN_PROJECT_MODE,
					projectMode.name());

			try {
				prefStore.save();
			}
			catch (IOException e) {
				throw new IllegalStateException("Unable to save preference file.");
			}

			return true;
		}

		return false;
	}

	@Override
	public boolean performCancel() {

		// Application.bDoExit = true;
		return true;
	}
}