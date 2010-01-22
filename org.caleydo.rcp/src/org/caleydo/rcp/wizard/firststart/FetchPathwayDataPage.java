package org.caleydo.rcp.wizard.firststart;

import java.util.ArrayList;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.system.CmdFetchPathwayData;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.specialized.genetic.EOrganism;
import org.caleydo.core.manager.specialized.genetic.pathway.EPathwayDatabaseType;
import org.caleydo.core.util.preferences.PreferenceConstants;
import org.caleydo.rcp.Application;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;

/**
 * Wizard for fetching pathway data from public databases.
 * 
 * @author Marc Streit
 */
public final class FetchPathwayDataPage
	extends WizardPage {
	public static final String PAGE_NAME = "Fetch Pathway Data";

	public final WizardPage thisPage;
	private ArrayList<EPathwayDatabaseType> alFetchPathwaySources;

	/**
	 * Constructor.
	 */
	public FetchPathwayDataPage(ArrayList<EPathwayDatabaseType> alFetchPathwaySources) {
		super(PAGE_NAME, PAGE_NAME, null);

		this.setImageDescriptor(ImageDescriptor.createFromURL(this.getClass().getClassLoader().getResource(
			"resources/wizard/wizard.png")));

		thisPage = this;
		this.alFetchPathwaySources = alFetchPathwaySources;

		setPageComplete(false);
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		RowLayout layout = new RowLayout(SWT.VERTICAL);
		layout.wrap = true;
		layout.fill = true;
		layout.justify = true;
		layout.center = true;
		layout.spacing = 15;
		composite.setLayout(layout);
		createContent(composite, this);

		setControl(composite);
	}

	public Composite createContent(final Composite composite, final DialogPage parentPage) {
		Group progressBarGroup = new Group(composite, SWT.NONE);
		progressBarGroup.setLayout(new GridLayout(2, false));
		progressBarGroup.setText("Fetch Progress");

		ProgressBar progressBarBioCartaPathwayCacher = null;
		if (alFetchPathwaySources.contains(EPathwayDatabaseType.BIOCARTA)) {
			Label lblBioCartaPathwayCacher = new Label(progressBarGroup, SWT.NULL);
			lblBioCartaPathwayCacher.setText("BioCarta Data and Image Download Status:");
			lblBioCartaPathwayCacher.setAlignment(SWT.RIGHT);
			lblBioCartaPathwayCacher.setBounds(10, 10, 80, 20);

			progressBarBioCartaPathwayCacher = new ProgressBar(progressBarGroup, SWT.SMOOTH);
			progressBarBioCartaPathwayCacher.setBounds(10, 10, 200, 32);
		}

		CmdFetchPathwayData cmdPathwayFetch =
			(CmdFetchPathwayData) GeneralManager.get().getCommandManager().createCommandByType(
				ECommandType.FETCH_PATHWAY_DATA);

		PreferenceStore prefStore =
			Application.caleydoCoreBootloader.getGeneralManager().getPreferenceStore();

		EOrganism eOrganism =
			EOrganism.valueOf(prefStore.getString(PreferenceConstants.LAST_CHOSEN_ORGANISM));

		cmdPathwayFetch.setAttributes(composite.getDisplay(), progressBarBioCartaPathwayCacher, parentPage,
			eOrganism, alFetchPathwaySources);

		if (prefStore.getBoolean(PreferenceConstants.USE_PROXY)) {
			String sProxyServer = prefStore.getString(PreferenceConstants.PROXY_SERVER);
			String sProxyPort = prefStore.getString(PreferenceConstants.PROXY_PORT);
			cmdPathwayFetch.setProxySettings(sProxyServer, Integer.parseInt(sProxyPort));
		}

		cmdPathwayFetch.doCommand();

		final Label lblNote = new Label(composite, SWT.NONE);
		lblNote
			.setText("Note: Depending on your internet connection, this process can take several minutes.");

		return composite;
	}
}
