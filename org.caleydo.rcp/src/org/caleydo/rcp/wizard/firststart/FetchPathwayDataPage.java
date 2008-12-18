package org.caleydo.rcp.wizard.firststart;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.system.CmdFetchPathwayData;
import org.caleydo.core.manager.general.GeneralManager;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;

/**
 * Wizard for fetching pathway data from public databases.
 * 
 * @author Marc Streit
 */
public final class FetchPathwayDataPage
	extends WizardPage
{
	public static final String PAGE_NAME = "Fetch Pathway Data";

	public final WizardPage thisPage;

	private Text txtProxyServer;
	private Text txtProxyPort;
	private boolean bProxyEnable;

	/**
	 * Constructor.
	 */
	public FetchPathwayDataPage()
	{
		super(PAGE_NAME, PAGE_NAME, null);

		this.setImageDescriptor(ImageDescriptor.createFromURL(this.getClass().getClassLoader()
				.getResource("resources/wizard/wizard.png")));

		thisPage = this;

		setPageComplete(false);
	}

	public void createControl(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NULL);
		RowLayout layout = new RowLayout(SWT.VERTICAL);
		layout.wrap = true;
		layout.fill = true;
		layout.justify = true;
		layout.center = true;
		layout.spacing = 15;
		composite.setLayout(layout);
		createContent(composite, this);
		createProxySettingsContent(composite);

		setControl(composite);
	}

	public Composite createContent(final Composite composite, final DialogPage parentPage)
	{
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

		final ProgressBar progressBarKeggPathwayCacher = new ProgressBar(progressBarGroup,
				SWT.SMOOTH);
		progressBarKeggPathwayCacher.setBounds(10, 10, 200, 32);

		Label lblKeggImagePathwayCacher = new Label(progressBarGroup, SWT.NULL);
		lblKeggImagePathwayCacher.setText("KEGG Image Download Status:");
		lblKeggImagePathwayCacher.setAlignment(SWT.RIGHT);
		lblKeggImagePathwayCacher.setBounds(10, 10, 80, 20);

		final ProgressBar progressBarKeggImagePathwayCacher = new ProgressBar(
				progressBarGroup, SWT.SMOOTH);
		progressBarKeggImagePathwayCacher.setBounds(10, 10, 200, 32);

		Label lblBioCartaPathwayCacher = new Label(progressBarGroup, SWT.NULL);
		lblBioCartaPathwayCacher.setText("BioCarta Data and Image Download Status:");
		lblBioCartaPathwayCacher.setAlignment(SWT.RIGHT);
		lblBioCartaPathwayCacher.setBounds(10, 10, 80, 20);

		final ProgressBar progressBarBioCartaPathwayCacher = new ProgressBar(progressBarGroup,
				SWT.SMOOTH);
		progressBarBioCartaPathwayCacher.setBounds(10, 10, 200, 32);

		buttonStartFetch.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				CmdFetchPathwayData cmdPathwayFetch = (CmdFetchPathwayData) GeneralManager
						.get().getCommandManager().createCommandByType(
								ECommandType.FETCH_PATHWAY_DATA);

				cmdPathwayFetch.setAttributes(composite.getDisplay(),
						progressBarKeggPathwayCacher, progressBarKeggImagePathwayCacher,
						progressBarBioCartaPathwayCacher, parentPage);

				if (bProxyEnable)
				{
					cmdPathwayFetch.setProxySettings(txtProxyServer.getText(), 
							Integer.valueOf(txtProxyPort.getText()));
				}

				cmdPathwayFetch.doCommand();

				buttonStartFetch.setEnabled(false);
			}
		});

		final Label lblNote = new Label(composite, SWT.NONE);
		lblNote
				.setText("Note: Depending on your internet connection, this process can take several minutes.");

		return composite;
	}

	private void createProxySettingsContent(Composite parent)
	{
		Group groupProxySettings = new Group(parent, SWT.SHADOW_ETCHED_IN);
		// groupProxySettings.setSize(10, 10, 100, 70);
		groupProxySettings.setText("Proxy Settings");
		groupProxySettings.setLayout(new GridLayout(2, false));

		GridData data = new GridData();
		data.horizontalSpan = 2;

		final Button btnNoProxy = new Button(groupProxySettings, SWT.RADIO);
		btnNoProxy.setBounds(10, 20, 75, 15);
		btnNoProxy.setText("No proxy");
		btnNoProxy.setLayoutData(data);
		btnNoProxy.setSelection(true);
		btnNoProxy.setEnabled(true);

		final Button btnUseProxy = new Button(groupProxySettings, SWT.RADIO);
		btnUseProxy.setBounds(10, 35, 75, 15);
		btnUseProxy.setText("Use proxy");
		btnUseProxy.setLayoutData(data);

		final Label lblProxyServer = new Label(groupProxySettings, SWT.NONE);
		lblProxyServer.setText("Proxy Server:");
		lblProxyServer.setEnabled(false);
		txtProxyServer = new Text(groupProxySettings, SWT.BORDER);
		txtProxyServer.setEnabled(false);
		data = new GridData();
		data.widthHint = 200;
		txtProxyServer.setLayoutData(data);

		final Label lblProxyPort = new Label(groupProxySettings, SWT.NONE);
		lblProxyPort.setText("Proxy Port:");
		lblProxyPort.setEnabled(false);
		txtProxyPort = new Text(groupProxySettings, SWT.BORDER);
		txtProxyPort.setEnabled(false);
		txtProxyPort.addListener(SWT.Verify, new Listener()
		{
			public void handleEvent(Event e)
			{
				String string = e.text;
				char[] chars = new char[string.length()];
				string.getChars(0, chars.length, chars, 0);
				for (int i = 0; i < chars.length; i++)
				{
					if (!('0' <= chars[i] && chars[i] <= '9'))
					{
						e.doit = false;
						return;
					}
				}
			}
		});

		SelectionListener selectionListener = new SelectionAdapter()
		{

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if (e.widget == btnNoProxy)
					bProxyEnable = false;
				else
					bProxyEnable = true;

				lblProxyServer.setEnabled(bProxyEnable);
				lblProxyPort.setEnabled(bProxyEnable);
				txtProxyServer.setEnabled(bProxyEnable);
				txtProxyPort.setEnabled(bProxyEnable);
			}
		};

		btnNoProxy.addSelectionListener(selectionListener);
		btnUseProxy.addSelectionListener(selectionListener);
	}
}
