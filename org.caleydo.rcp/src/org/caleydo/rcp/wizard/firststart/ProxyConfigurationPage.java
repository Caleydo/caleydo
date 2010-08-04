package org.caleydo.rcp.wizard.firststart;

import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * Wizard page for configuring and checking internet connection.
 * 
 * @author Marc Streit
 */
public final class ProxyConfigurationPage
	extends WizardPage {
	public static final String PAGE_NAME = "Proxy Configuration Page";

	public static final String TEST_URL = "www.google.com";

	public final WizardPage thisPage;

	private Text txtProxyServer;
	private Text txtProxyPort;
	private boolean bUseProxy;

	private Label connectionOKLabel;

	/**
	 * Constructor.
	 */
	public ProxyConfigurationPage() {
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
		createContent(composite);

		setControl(composite);
	}

	public Composite createContent(final Composite composite) {
		connectionOKLabel = new Label(composite, SWT.CENTER | SWT.BORDER);
		connectionOKLabel.setText("OK");
		connectionOKLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GREEN));

		updateInternetStatusLabel();

		createProxySettingsContent(composite);

		Button checkConnectionButton = new Button(composite, SWT.PUSH);
		checkConnectionButton.setText("Test");
		checkConnectionButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
//				updateInternetStatusLabel();
//
//				PreferenceStore prefStore =
//					Application.caleydoCoreBootloader.getGeneralManager().getPreferenceStore();
//				prefStore.setValue(PreferenceConstants.USE_PROXY, bUseProxy);
//
//				if (bUseProxy) {
//					prefStore.setValue(PreferenceConstants.PROXY_SERVER, txtProxyServer.getText());
//					prefStore.setValue(PreferenceConstants.PROXY_PORT, txtProxyPort.getText());
//
//					System.setProperty("network.proxy_host", prefStore
//						.getString(PreferenceConstants.PROXY_SERVER));
//					System.setProperty("network.proxy_port", prefStore
//						.getString(PreferenceConstants.PROXY_PORT));
//				}
			}
		});

		return composite;
	}

	private void updateInternetStatusLabel() {
		int color;
		String sText = "";

		if (isInternetConnectionOK()) {
			color = SWT.COLOR_GREEN;
			sText = "Internet Connection OK";
		}
		else {
			color = SWT.COLOR_RED;
			sText = "No internet connection found";
		}

		connectionOKLabel.setBackground(Display.getCurrent().getSystemColor(color));
		connectionOKLabel.setText(sText);
	}

	private void createProxySettingsContent(Composite parent) {
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
		txtProxyPort.addListener(SWT.Verify, new Listener() {
			public void handleEvent(Event e) {
				String string = e.text;
				char[] chars = new char[string.length()];
				string.getChars(0, chars.length, chars, 0);
				for (int i = 0; i < chars.length; i++) {
					if (!('0' <= chars[i] && chars[i] <= '9')) {
						e.doit = false;
						return;
					}
				}
			}
		});

		SelectionListener selectionListener = new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (e.widget == btnNoProxy) {
					bUseProxy = false;
				}
				else {
					bUseProxy = true;
				}

				lblProxyServer.setEnabled(bUseProxy);
				lblProxyPort.setEnabled(bUseProxy);
				txtProxyServer.setEnabled(bUseProxy);
				txtProxyPort.setEnabled(bUseProxy);
			}
		};

		btnNoProxy.addSelectionListener(selectionListener);
		btnUseProxy.addSelectionListener(selectionListener);
	}

	private boolean isInternetConnectionOK() {
		// Check internet connection
		try {
			if (bUseProxy) {
				InetAddress proxyAddr;
				proxyAddr = InetAddress.getByName(txtProxyServer.getText());
				InetSocketAddress iSockAddr;
				iSockAddr = new InetSocketAddress(proxyAddr, Integer.parseInt(txtProxyPort.getText()));
				Proxy proxy = new Proxy(Proxy.Type.HTTP, iSockAddr);

				URL url = new URL("http://" + TEST_URL);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection(proxy);
				conn.connect();
				conn.disconnect();
			}
			else {
				InetAddress.getByName(TEST_URL);
			}
		}
		catch (Exception e) {
//			Application.bIsInterentConnectionOK = false;
			setPageComplete(false);
			return false;
		}

//		Application.bIsInterentConnectionOK = true;
		setPageComplete(true);
		return true;
	}
}
