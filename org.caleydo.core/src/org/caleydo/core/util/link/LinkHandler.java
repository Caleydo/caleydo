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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.Status;

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
	 * @param link
	 *            the link to be opened
	 */
	public static void openLink(final String link) {
		Thread execThread = new Thread(new Runnable() {
			public void run() {

				String osName = System.getProperty("os.name");
				try {
					if (osName.startsWith("Mac OS")) {
						Class<?> fileMgr = Class.forName("com.apple.eio.FileManager");
						Method openURL = fileMgr.getDeclaredMethod("openURL",
								new Class[] { String.class });
						openURL.invoke(null, new Object[] { link });
					} else if (osName.startsWith("Windows")) {
						exec("rundll32", "url.dll", "FileProtocolHandler", link);
					} else {
						System.out.println("vorher");

						boolean success = exec(new String[] { "xdg-open", link });
						// Assume Unix or Linux
						// we first try xdg-open

						System.out.println("nacher");
						// if that fails we try using which to find a browser
						if (!success) {

							String[] browsers = { "chrome", "firefox", "opera",
									"konqueror", "epiphany", "mozilla", "netscape" };
							String browser = null;
							for (int count = 0; count < browsers.length
									&& browser == null; count++) {
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
					Logger.log(new Status(Status.ERROR, "LinkHandler",
							"Caught exception while handling a link: \n" + link,
							exception));
				}
			}
		});
		execThread.setName("LinkHandlerExec");
		execThread.start();
	}

	/**
	 * Savely executes a process according to {@link Runtime#getRuntime()
	 * #exec(String[])}
	 */
	public static boolean exec(String... args) throws IOException, InterruptedException {

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
			Logger.log(new Status(Status.ERROR, "LinkHandler", "Process didn't complete",
					e));
			return false;
		}
	}
}
