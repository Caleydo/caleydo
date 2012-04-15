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
package org.caleydo.core.util.link;

import java.lang.reflect.Method;

/**
 * URL related utilities.
 * 
 * @author Alexander Lex
 * 
 */
public class LinkHandler {

	/**
	 * Opens a link in the user's preferred browser in a platform-independent
	 * way.
	 * 
	 * @param link the link to be opened
	 */
	public static void openLink(String link) {
		String osName = System.getProperty("os.name");
		try {
			if (osName.startsWith("Mac OS")) {
				Class<?> fileMgr = Class.forName("com.apple.eio.FileManager");
				Method openURL = fileMgr.getDeclaredMethod("openURL",
						new Class[] { String.class });
				openURL.invoke(null, new Object[] { link });
			} else if (osName.startsWith("Windows")) {
				Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + link);
			} else {
				// Assume Unix or Linux
				// we first try xdg-open
				Process process = Runtime.getRuntime().exec(
						new String[] { "xdg-open", link });
				// if that fails we try using which to find a browser
				if (process.exitValue() != 0) {

					String[] browsers = { "chrome", "firefox", "opera", "konqueror",
							"epiphany", "mozilla", "netscape" };
					String browser = null;
					for (int count = 0; count < browsers.length && browser == null; count++)
						if (Runtime.getRuntime()
								.exec(new String[] { "which", browsers[count] })
								.waitFor() == 0) {
							browser = browsers[count];
						}
					if (browser == null)
						throw new Exception("Could not find web browser");
					else {
						Runtime.getRuntime().exec(new String[] { browser, link });
					}
				}
			}
		} catch (Exception exception) {
		}
	}

}
