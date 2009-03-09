package org.caleydo.rcp.wizard.firststart;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.system.CmdFetchPathwayData;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.util.preferences.PreferenceConstants;
import org.caleydo.rcp.Application;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
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

	/**
	 * Constructor.
	 */
	public FetchPathwayDataPage() {
		super(PAGE_NAME, PAGE_NAME, null);

		this.setImageDescriptor(ImageDescriptor.createFromURL(this.getClass().getClassLoader().getResource(
			"resources/wizard/wizard.png")));

		thisPage = this;

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

		final Button buttonStartFetch = new Button(composite, SWT.NONE);
		buttonStartFetch.setText("Start pathway download");
		buttonStartFetch.setAlignment(SWT.CENTER);

		Label lblKeggPathwayCacher = new Label(progressBarGroup, SWT.NULL);
		lblKeggPathwayCacher.setText("KEGG Pathway Data Download Status:");
		lblKeggPathwayCacher.setAlignment(SWT.RIGHT);
		lblKeggPathwayCacher.setBounds(10, 10, 80, 20);

		final ProgressBar progressBarKeggPathwayCacher = new ProgressBar(progressBarGroup, SWT.SMOOTH);
		progressBarKeggPathwayCacher.setBounds(10, 10, 200, 32);

		Label lblKeggImagePathwayCacher = new Label(progressBarGroup, SWT.NULL);
		lblKeggImagePathwayCacher.setText("KEGG Image Download Status:");
		lblKeggImagePathwayCacher.setAlignment(SWT.RIGHT);
		lblKeggImagePathwayCacher.setBounds(10, 10, 80, 20);

		final ProgressBar progressBarKeggImagePathwayCacher = new ProgressBar(progressBarGroup, SWT.SMOOTH);
		progressBarKeggImagePathwayCacher.setBounds(10, 10, 200, 32);

		Label lblBioCartaPathwayCacher = new Label(progressBarGroup, SWT.NULL);
		lblBioCartaPathwayCacher.setText("BioCarta Data and Image Download Status:");
		lblBioCartaPathwayCacher.setAlignment(SWT.RIGHT);
		lblBioCartaPathwayCacher.setBounds(10, 10, 80, 20);

		final ProgressBar progressBarBioCartaPathwayCacher = new ProgressBar(progressBarGroup, SWT.SMOOTH);
		progressBarBioCartaPathwayCacher.setBounds(10, 10, 200, 32);

		buttonStartFetch.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CmdFetchPathwayData cmdPathwayFetch =
					(CmdFetchPathwayData) GeneralManager.get().getCommandManager().createCommandByType(
						ECommandType.FETCH_PATHWAY_DATA);

				cmdPathwayFetch.setAttributes(composite.getDisplay(), progressBarKeggPathwayCacher,
					progressBarKeggImagePathwayCacher, progressBarBioCartaPathwayCacher, parentPage);

				PreferenceStore prefStore = Application.caleydoCore.getGeneralManager().getPreferenceStore();

				if (prefStore.getBoolean(PreferenceConstants.USE_PROXY)) {
					String sProxyServer = prefStore.getString(PreferenceConstants.PROXY_SERVER);
					String sProxyPort = prefStore.getString(PreferenceConstants.PROXY_PORT);
					cmdPathwayFetch.setProxySettings(sProxyServer, Integer.parseInt(sProxyPort));
				}

				cmdPathwayFetch.doCommand();

				buttonStartFetch.setEnabled(false);
			}
		});

		final Label lblNote = new Label(composite, SWT.NONE);
		lblNote.setText("Note: Depending on your internet connection, this process can take several minutes.");

		return composite;
	}
}
