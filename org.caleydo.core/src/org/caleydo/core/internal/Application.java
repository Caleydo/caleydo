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
package org.caleydo.core.internal;

import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.TreeSet;

import org.caleydo.core.internal.gui.CaleydoProjectWizard;
import org.caleydo.core.internal.startup.StartupAddons;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.startup.IStartupAddon;
import org.caleydo.core.startup.IStartupProcedure;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.kohsuke.args4j.ClassParser;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

/**
 * This class controls all aspects of the application's execution
 */
public class Application implements IApplication {


	@Override
	public Object start(IApplicationContext context) throws Exception {
		final Logger log = Logger.create(Application.class);
		try {
			log.info("Starting Caleydo");

			dumpEnvironment(log);

			changeWorkspaceLocation();

			GeneralManager.get(); // stupid but needed for initialization

			Map<String, IStartupAddon> startups = StartupAddons.findAll();

			if (!parseArgs(startups.values(), (String[]) context.getArguments().get("application.args"))) {
				return EXIT_OK; // invalid args
			}

			Display display = PlatformUI.createDisplay();

			// create a select the startup pro
			IStartupProcedure startup = selectStartupProcedure(startups, display);
			if (startup == null)
				return EXIT_OK; // unstartable
			startups = null; // cleanup

			startup.preWorkbenchOpen();

			ApplicationWorkbenchAdvisor advisor = new ApplicationWorkbenchAdvisor(startup);

			// cleanup
			startup = null;

			int returnCode = PlatformUI.createAndRunWorkbench(display, advisor);

			log.info("Bye bye!");

			if (returnCode == PlatformUI.RETURN_RESTART) {
				return EXIT_RESTART;
			}
			return EXIT_OK;

		} catch (Exception e) {
			log.error("Caught exception, crashing.", e);
			throw e;
		}
	}

	/**
	 * @param startups
	 * @param display
	 * @return
	 */
	private IStartupProcedure selectStartupProcedure(Map<String, IStartupAddon> startups, Display display) {
		for (IStartupAddon startup : startups.values()) {
			if (startup.init())
				return startup.create();
		}
		// not yet configured choose one
		Shell shell = new Shell(display);
		shell.moveAbove(null);
		CaleydoProjectWizard wizardImpl = new CaleydoProjectWizard(shell, startups);
		WizardDialog wizard = new WizardDialog(shell, wizardImpl);
		shell.forceActive();
		if (wizard.open() == Window.CANCEL) {
			return null;
		}
		return wizardImpl.getResult();
	}

	/**
	 * @param values
	 * @param strings
	 */
	private static boolean parseArgs(Collection<IStartupAddon> startups, String[] args) {
		CmdLineParser parser = new CmdLineParser(null);
		ClassParser beanParser = new ClassParser();
		// scan all addons for command line args
		for (IStartupAddon startup : startups)
			beanParser.parse(startup, parser);

		try {
			parser.parseArgument(args);
			return true;
		} catch (CmdLineException e) {
			// if there's a problem in the command line,
			// you'll get this exception. this will report
			// an error message.
			System.err.println(e.getMessage());
			System.err.println("Caleydo [options...] arguments...");
			// print the list of available options
			parser.printUsage(System.err);
			System.err.println();
			return false;
		}
	}

	/**
	 * @param log
	 *
	 */
	private static void dumpEnvironment(Logger log) {
		StringBuilder b = new StringBuilder();
		Map<String, String> env = System.getenv();
		for (String key : new TreeSet<>(env.keySet())) {
			b.append(key).append('=').append(env.get(key)).append('\n');
		}
		log.debug("Environment Variables:\n%s", b);

		b = new StringBuilder();
		Properties props = System.getProperties();
		for (String key : new TreeSet<>(props.stringPropertyNames())) {
			b.append(key).append('=').append(props.getProperty(key)).append('\n');
		}
		log.debug("System Properties:\n%s", b);
	}

	/**
	 * Changing the workspace location in order to be able to store and restore the workbench state (also in combination
	 * with serialized projects).
	 */
	private static void changeWorkspaceLocation() {

		final Location instanceLoc = Platform.getInstanceLocation();

		String workspacePath = "file://" + GeneralManager.CALEYDO_HOME_PATH;
		try {
			URL workspaceURL = new URL(workspacePath);
			instanceLoc.set(workspaceURL, false);
		} catch (Exception e) {
			// throw new IllegalStateException
			System.err.println("Cannot set workspace location at " + workspacePath);
		}
	}

	@Override
	public void stop() {
		final IWorkbench workbench = PlatformUI.getWorkbench();

		if (workbench == null)
			return;

		final Display display = workbench.getDisplay();
		display.syncExec(new Runnable() {
			@Override
			public void run() {
				if (!display.isDisposed()) {
					workbench.close();
				}
			}
		});
	}
}