package org.caleydo.core.command.system;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.caleydo.core.application.helper.cacher.biocarta.BioCartaPathwayCacher;
import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.base.ACmdExternalAttributes;
import org.caleydo.core.manager.specialized.genetic.EOrganism;
import org.caleydo.core.manager.specialized.genetic.pathway.EPathwayDatabaseType;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.preferences.PreferenceConstants;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;

/**
 * Command triggers helper tools that fetch data from pathway databases.
 * 
 * @author Marc Streit
 */
public class CmdFetchPathwayData
	extends ACmdExternalAttributes {

	private Display display = null;

	private DialogPage parentPage = null;

	private boolean isBioCartaCacherFinished = false;

	private BioCartaPathwayCacher bioCartaPathwayCacher;

	private EOrganism eOrganism;

	/**
	 * Constructor.
	 */
	public CmdFetchPathwayData(final ECommandType cmdType) {
		super(cmdType);
	}

	@Override
	public void doCommand() {
		// clearOldPathwayData();

		bioCartaPathwayCacher.start();
		commandManager.runDoCommand(this);
	}

	@Override
	public void undoCommand() {
		commandManager.runUndoCommand(this);
	}

	@Override
	public void setParameterHandler(final IParameterHandler parameterHandler) {
		super.setParameterHandler(parameterHandler);
	}

	public void setAttributes(final Display display, final ProgressBar progressBarBioCartaPathwayCacher,
		final DialogPage parentPage, final EOrganism eOrganism,
		final ArrayList<EPathwayDatabaseType> alPathwayDataSources) {

		this.display = display;
		this.parentPage = parentPage;
		this.eOrganism = eOrganism;

		if (alPathwayDataSources.contains(EPathwayDatabaseType.BIOCARTA)) {
			bioCartaPathwayCacher =
				new BioCartaPathwayCacher(display, progressBarBioCartaPathwayCacher, this, eOrganism);
		}
	}

	public void setProxySettings(String sProxyServer, int iProxyPort) {

		if (bioCartaPathwayCacher != null)
			bioCartaPathwayCacher.setProxySettings(sProxyServer, iProxyPort);
	}

	public void setFinishedBioCartaCacher() {
		isBioCartaCacherFinished = true;
		notifyWizard();

		// Append pathway organism+source combination
		generalManager.getPreferenceStore().setValue(
			PreferenceConstants.PATHWAY_DATA_OK,
			generalManager.getPreferenceStore().getString(PreferenceConstants.PATHWAY_DATA_OK)
				+ eOrganism.name() + "+" + EPathwayDatabaseType.BIOCARTA.name() + ";");

		try {
			generalManager.getPreferenceStore().save();
		}
		catch (IOException e1) {
			throw new IllegalStateException("Unable to save preference file.");
		}
	}

	public void notifyWizard() {
		if (parentPage == null || ((WizardPage) parentPage).getWizard() == null
			|| ((WizardPage) parentPage).getWizard().getContainer() == null)
			return;

		if (isBioCartaCacherFinished || bioCartaPathwayCacher == null) {

			display.asyncExec(new Runnable() {
				public void run() {
					if (parentPage instanceof WizardPage) {
						((WizardPage) parentPage).setPageComplete(true);
						// ((WizardPage) parentPage).getWizard().performFinish();
						((WizardPage) parentPage).getWizard().getContainer().getShell().close();
					}
					// else if (parentPage instanceof PreferencePage) {
					// ((PreferencePage) parentPage).setValid(true);
					// }

					generalManager.getPreferenceStore().setValue(PreferenceConstants.LAST_PATHWAY_UPDATE,
						getDateTime());

					try {
						generalManager.getPreferenceStore().save();
					}
					catch (IOException e1) {
						throw new IllegalStateException("Unable to save preference file.");
					}
				}
			});
		}
	}

	private String getDateTime() {
		DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}
}
