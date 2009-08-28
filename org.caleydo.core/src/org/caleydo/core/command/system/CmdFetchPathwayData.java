package org.caleydo.core.command.system;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.caleydo.core.application.helper.cacher.biocarta.BioCartaPathwayCacher;
import org.caleydo.core.application.helper.cacher.kegg.KeggPathwayCacher;
import org.caleydo.core.application.helper.cacher.kegg.KeggPathwayImageCacher;
import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.base.ACmdExternalAttributes;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.specialized.genetic.EOrganism;
import org.caleydo.core.manager.specialized.genetic.pathway.EPathwayDatabaseType;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.preferences.PreferenceConstants;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.preference.PreferencePage;
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

	private boolean isKeggCacherFinished = false;
	private boolean isKeggImageCacherFinished = false;
	private boolean isBioCartaCacherFinished = false;

	private BioCartaPathwayCacher bioCartaPathwayCacher;
	private KeggPathwayCacher keggPathwayCacher;
	private KeggPathwayImageCacher keggPathwayImageCacher;

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

		if (keggPathwayCacher != null)
			keggPathwayCacher.start();
		else
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

	public void setAttributes(final Display display, final ProgressBar progressBarKeggPathwayCacher,
		final ProgressBar progressBarKeggPathwayImageCacher,
		final ProgressBar progressBarBioCartaPathwayCacher, final DialogPage parentPage,
		final EOrganism eOrganism, final ArrayList<EPathwayDatabaseType> alPathwayDataSources) {

		this.display = display;
		this.parentPage = parentPage;
		this.eOrganism = eOrganism;

		if (alPathwayDataSources.contains(EPathwayDatabaseType.BIOCARTA)) {
			bioCartaPathwayCacher =
				new BioCartaPathwayCacher(display, progressBarBioCartaPathwayCacher, this, eOrganism);
		}

		if (alPathwayDataSources.contains(EPathwayDatabaseType.KEGG)) {
			keggPathwayCacher = new KeggPathwayCacher(display, progressBarKeggPathwayCacher, this, eOrganism);
			keggPathwayImageCacher =
				new KeggPathwayImageCacher(display, progressBarKeggPathwayImageCacher, this, eOrganism);
		}
	}

	public void setProxySettings(String sProxyServer, int iProxyPort) {

		if (bioCartaPathwayCacher != null)
			bioCartaPathwayCacher.setProxySettings(sProxyServer, iProxyPort);

		if (keggPathwayCacher != null) {
			keggPathwayCacher.setProxySettings(sProxyServer, iProxyPort);
			keggPathwayImageCacher.setProxySettings(sProxyServer, iProxyPort);
		}
	}

	public void setFinishedKeggCacher() {
		isKeggCacherFinished = true;
		notifyWizard();
		keggPathwayImageCacher.start();
		// setFinishedKeggImageCacher();
	}

	public void setFinishedKeggImageCacher() {
		isKeggImageCacherFinished = true;
		notifyWizard();

		if (bioCartaPathwayCacher != null)
			bioCartaPathwayCacher.start();

		// Append pathway organism+source combination
		generalManager.getPreferenceStore().setValue(
			PreferenceConstants.PATHWAY_DATA_OK,
			generalManager.getPreferenceStore().getString(PreferenceConstants.PATHWAY_DATA_OK)
				+ eOrganism.name() + "+" + EPathwayDatabaseType.KEGG.name() + ";");
		
		try {
			generalManager.getPreferenceStore().save();
		}
		catch (IOException e1) {
			throw new IllegalStateException("Unable to save preference file.");
		}
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

		if ((isKeggCacherFinished || keggPathwayCacher == null)
			&& (isKeggImageCacherFinished || keggPathwayCacher == null)
			&& (isBioCartaCacherFinished || bioCartaPathwayCacher == null)) {

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

	// private void createBackup()
	// {
	// new File(IGeneralManager.CALEYDO_HOME_PATH + "kegg").renameTo(
	// new File(IGeneralManager.CALEYDO_HOME_PATH + "backup_kegg"));
	// new File(IGeneralManager.CALEYDO_HOME_PATH +
	// "cgap.nci.nih.gov").renameTo(
	// new File(IGeneralManager.CALEYDO_HOME_PATH + "backup_cgap.nci.nih.gov"));
	// new File(IGeneralManager.CALEYDO_HOME_PATH + "www.genome.jp").renameTo(
	// new File(IGeneralManager.CALEYDO_HOME_PATH + "backup_www.genome.jp"));
	//		
	// new File(IGeneralManager.CALEYDO_HOME_PATH +
	// "pathway_list_KEGG.txt").renameTo(
	// new File(IGeneralManager.CALEYDO_HOME_PATH +
	// "backup_pathway_list_KEGG.txt"));
	// new File(IGeneralManager.CALEYDO_HOME_PATH +
	// "pathway_list_BIOCARTA.txt").renameTo(
	// new File(IGeneralManager.CALEYDO_HOME_PATH +
	// "backup_pathway_list_BIOCARTA.txt"));
	// }
	//	
	// private void restoreBackup()
	// {
	//		
	// }

	/**
	 * @TODO: Call before updating pathway source
	 */
	private void clearOldPathwayData() {
		deleteDir(new File(IGeneralManager.CALEYDO_HOME_PATH + "kegg"));
		deleteDir(new File(IGeneralManager.CALEYDO_HOME_PATH + "cgap.nci.nih.gov"));
		deleteDir(new File(IGeneralManager.CALEYDO_HOME_PATH + "www.genome.jp"));

		deleteDir(new File(IGeneralManager.CALEYDO_HOME_PATH + "pathway_list_KEGG.txt"));
		deleteDir(new File(IGeneralManager.CALEYDO_HOME_PATH + "pathway_list_BIOCARTA.txt"));
	}

	// Deletes all files and subdirectories under dir.
	// Returns true if all deletions were successful.
	// If a deletion fails, the method stops attempting to delete and returns
	// false.
	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (String element : children) {
				boolean success = deleteDir(new File(dir, element));
				if (!success)
					return false;
			}
		}

		// The directory is now empty so delete it
		return dir.delete();
	}

	private String getDateTime() {
		DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}
}
