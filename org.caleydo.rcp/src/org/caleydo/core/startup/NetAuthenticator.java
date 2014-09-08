package org.caleydo.core.startup;

import java.net.Authenticator;
import java.net.InetAddress;
import java.net.PasswordAuthentication;

import org.caleydo.core.internal.gui.UserValidationDialog;

public class NetAuthenticator extends Authenticator {

	/*
	 * @see Authenticator#getPasswordAuthentication()
	 */
	@Override
	protected PasswordAuthentication getPasswordAuthentication() {
		// String protocol = getRequestingProtocol();
		InetAddress address = getRequestingSite(); // can be null;
		// int port = getRequestingPort();
		String prompt = getRequestingPrompt(); // realm or message, not documented that can be null
		// String scheme = getRequestingScheme(); // not documented that can be null

		 // get the host name from the address since #getRequestingHost
		 // is not available in the foundation 1.0 class libraries
		String hostString = null;
		if (address != null) {
			hostString = address.getHostName();
		}
		if (hostString == null) {
			hostString = ""; //$NON-NLS-1$
		}
		String promptString = prompt;
		if (prompt == null) {
			promptString = ""; //$NON-NLS-1$
		}

		UserValidationDialog.Authentication auth = UserValidationDialog.getAuthentication(
				hostString, promptString);
		if (auth != null)
			return new PasswordAuthentication(auth.getUser(), auth
					.getPassword().toCharArray());
		else
			return null;
	}
}