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
package org.caleydo.core.gui.preferences;

import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

import org.caleydo.core.manager.GeneralManager;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class ProxyPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public static final String PAGE_NAME = "Proxy Configuration Page";

	public static final String TEST_URL = "www.google.com";

	private Text txtProxyServer;
	private Text txtProxyPort;
	private boolean useProxy;

	private Label connectionOKLabel;

	public ProxyPreferencePage() {
		super(GRID);
		setPreferenceStore(GeneralManager.get().getPreferenceStore());
		setDescription("Proxy settings");

		useProxy = getPreferenceStore().getBoolean(PreferenceConstants.USE_PROXY);
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common GUI blocks needed to manipulate
	 * various types of preferences. Each field editor knows how to save and restore itself.
	 */
	@Override
	public void createFieldEditors() {

		Composite composite = new Composite(getFieldEditorParent(), SWT.NULL);
		composite.setLayout(new GridLayout(1, false));

		connectionOKLabel = new Label(composite, SWT.CENTER | SWT.BORDER);
		connectionOKLabel.setText("OK");
		connectionOKLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GREEN));

		updateInternetStatusLabel();

		createProxySettingsContent(composite);

		Button checkConnectionButton = new Button(composite, SWT.PUSH);
		checkConnectionButton.setText("Test internet connection");
		checkConnectionButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				updateInternetStatusLabel();

				applySettings();
			}
		});

		composite.pack();
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

		final Button btnUseProxy = new Button(groupProxySettings, SWT.RADIO);
		btnUseProxy.setBounds(10, 35, 75, 15);
		btnUseProxy.setText("Use proxy");
		btnUseProxy.setLayoutData(data);

		final Label lblProxyServer = new Label(groupProxySettings, SWT.NONE);
		lblProxyServer.setText("Proxy Server:");
		txtProxyServer = new Text(groupProxySettings, SWT.BORDER);
		data = new GridData();
		data.widthHint = 200;
		txtProxyServer.setLayoutData(data);

		final Label lblProxyPort = new Label(groupProxySettings, SWT.NONE);
		lblProxyPort.setText("Proxy Port:");

		txtProxyPort = new Text(groupProxySettings, SWT.BORDER);

		txtProxyPort.addListener(SWT.Verify, new Listener() {
			@Override
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

		if (useProxy) {
			btnUseProxy.setSelection(true);
			txtProxyServer.setText(getPreferenceStore().getString(PreferenceConstants.PROXY_SERVER));
			txtProxyPort.setText(getPreferenceStore().getString(PreferenceConstants.PROXY_PORT));
		}
		else {
			btnNoProxy.setSelection(true);
		}

		lblProxyServer.setEnabled(useProxy);
		lblProxyPort.setEnabled(useProxy);
		txtProxyServer.setEnabled(useProxy);
		txtProxyPort.setEnabled(useProxy);

		SelectionListener selectionListener = new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (e.widget == btnNoProxy) {
					useProxy = false;
				}
				else {
					useProxy = true;
				}

				lblProxyServer.setEnabled(useProxy);
				lblProxyPort.setEnabled(useProxy);
				txtProxyServer.setEnabled(useProxy);
				txtProxyPort.setEnabled(useProxy);
			}
		};

		btnNoProxy.addSelectionListener(selectionListener);
		btnUseProxy.addSelectionListener(selectionListener);
	}

	private boolean isInternetConnectionOK() {
		// Check internet connection
		try {
			if (useProxy) {
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
			// setPageComplete(false);
			return false;
		}

		// setPageComplete(true);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
	}

	private void applySettings() {
		IPreferenceStore prefStore = getPreferenceStore();
		prefStore.setValue(PreferenceConstants.USE_PROXY, useProxy);

		if (useProxy) {
			prefStore.setValue(PreferenceConstants.PROXY_SERVER, txtProxyServer.getText());
			prefStore.setValue(PreferenceConstants.PROXY_PORT, txtProxyPort.getText());

			System.setProperty("network.proxy_host", prefStore.getString(PreferenceConstants.PROXY_SERVER));
			System.setProperty("network.proxy_port", prefStore.getString(PreferenceConstants.PROXY_PORT));
		}
		else {
			System.setProperty("network.proxy_host", "");
			System.setProperty("network.proxy_port", "");
		}
	}

	@Override
	public boolean performOk() {
		applySettings();
		return super.performOk();
	}
}