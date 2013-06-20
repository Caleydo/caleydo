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
package org.caleydo.core.util.system;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * Events that signals browser-views to load a new URL.
 *
 * @author Marc Streit
 */
public class BrowserUtils {
	private static final Logger log = Logger.create(BrowserUtils.class);

	public static void openURL(String url) {
		try {
			openURL(new URL(url));
		} catch (MalformedURLException e) {
			log.error("can't open URL: " + url, e);
		}
	}

	public static final SelectionListener LINK_LISTENER = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			openURL(e.text);
		}
	};

	public static void openURL(URL url) {
		String osName = System.getProperty("os.name");

		if (!(osName.startsWith("Mac OS") || osName.startsWith("Windows"))) {
			// The system independent stuff doesn't properly work on linux
			openURLCompatibility(url);
			return;
		}

		if (PlatformUI.isWorkbenchRunning()) {
			try {
				PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(url);
			} catch (PartInitException e) {
				log.error("can't open URL: " + url, e);
			}
		} else if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Action.BROWSE)) {
			try {
				Desktop.getDesktop().browse(url.toURI());
			} catch (IOException | URISyntaxException e) {
				log.error("can't open URL: " + url, e);
			}
		} else {
			openURLCompatibility(url);
		}
	}

	private static void openURLCompatibility(URL url) {
		final String link = url.toExternalForm();
		Thread execThread = new Thread(new Runnable() {
			@Override
			public void run() {

				String osName = System.getProperty("os.name");
				try {
					if (osName.startsWith("Mac OS")) {
						Class<?> fileMgr = Class.forName("com.apple.eio.FileManager");
						Method openURL = fileMgr.getDeclaredMethod("openURL", new Class[] { String.class });
						openURL.invoke(null, new Object[] { link });
					} else if (osName.startsWith("Windows")) {
						exec("rundll32", "url.dll", "FileProtocolHandler", link);
					} else {
						// Assume Unix or Linux
						// we first try xdg-open
						boolean success = exec(new String[] { "xdg-open", link });
						// if that fails we try using which to find a browser
						if (!success) {
							String[] browsers = { "chrome", "firefox", "opera", "konqueror", "epiphany", "mozilla",
									"netscape" };
							String browser = null;
							for (int count = 0; count < browsers.length && browser == null; count++) {
								success = exec("which", browsers[count]);
								if (success) {
									browser = browsers[count];
								}
							}
							if (browser == null)
								throw new Exception("Could not find web browser");
							else {
								exec(new String[] { browser, link });
							}
						}
					}
				} catch (Exception exception) {
					Logger.log(new Status(IStatus.ERROR, "LinkHandler", "Caught exception while handling a link: \n"
							+ link, exception));
				}
			}
		});
		execThread.setName("LinkHandlerExec");
		execThread.start();
	}

	/**
	 * Savely executes a process according to {@link Runtime#getRuntime() #exec(String[])}
	 */
	private static boolean exec(String... args) throws IOException, InterruptedException {

		ProcessBuilder pb = new ProcessBuilder(args);
		args[args.length - 1] += "<NUL";

		pb.redirectErrorStream(true);
		Process p = pb.start();
		InputStreamReader isr = new InputStreamReader(p.getInputStream());
		BufferedReader input = new BufferedReader(isr);
		String line;
		while ((line = input.readLine()) != null) {
			System.out.println(line);
		}
		p.waitFor();
		input.close();
		isr.close();

		try {

			if (p.exitValue() == 0)
				return true;
			else
				return false;
		} catch (IllegalThreadStateException e) {
			Logger.log(new Status(IStatus.ERROR, "LinkHandler", "Process didn't complete", e));
			return false;
		}
	}
}
