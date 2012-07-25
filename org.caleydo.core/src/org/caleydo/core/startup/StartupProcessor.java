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
package org.caleydo.core.startup;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.PreferenceManager;
import org.caleydo.core.net.IGroupwareManager;
import org.caleydo.core.startup.gui.CaleydoProjectWizard;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * Startup processor handles the creation of the startup procedure and its
 * execution.
 * 
 * @author Marc Streit
 */
public class StartupProcessor {

	private ApplicationWorkbenchAdvisor applicationWorkbenchAdvisor;

	private static StartupProcessor startupProcessor;

	private static AStartupProcedure startupProcedure;

	private Display display;

	private StartupProcessor() {
	}

	public static StartupProcessor get() {
		if (startupProcessor == null) {
			startupProcessor = new StartupProcessor();
		}
		return startupProcessor;
	}

	public void initStartupProcudure(Map<String, Object> applicationArguments) {

		String[] runConfigParameters = (String[]) applicationArguments
				.get("application.args");

		if (runConfigParameters != null) {

			for (int parameterCount = 0; parameterCount < runConfigParameters.length; parameterCount++) {
				String parameter = runConfigParameters[parameterCount];
				if (parameterCount == 0) {
					boolean isFile = checkFileName(parameter);
					if (isFile) {
						startupProcedure = new SerializationStartupProcedure();
						((SerializationStartupProcedure) startupProcedure)
								.setProjectLocation(parameter);
					}
				}

				if (parameter.equalsIgnoreCase("-help")
						|| parameter.equalsIgnoreCase("--help")
						|| parameter.equalsIgnoreCase("-h")) {
					System.out.println("This is Caleydo Version "
							+ GeneralManager.VERSION + "\n\n");

					System.out.println("Usage: caleydo [project_filename.cal]");
					System.out
							.println("The following command-line options are available:\n");
					System.out
							.println("-h \t --help \t Print help on command line options");
					System.out.println();
					shutdown();
				} else

				{
					String message = "Unknown Command Line Argument: " + parameter;
					Logger.log(new Status(Status.WARNING, this.toString(), message));
					System.out.println(message);
				}

				// TODO we need to re-think the view command line argument

				// if (parameter.equalsIgnoreCase("-view")
				// || parameter.equalsIgnoreCase("--view")
				// || parameter.equalsIgnoreCase("-v")) {
				//
				// } else if (parameter.contains(":")) {
				// // Parse initial start views
				// int delimiterPos = parameter.indexOf(":");
				// String view = "org.caleydo.view."
				// + parameter.substring(delimiterPos + 1, parameter.length());
				// String dataDomain = "org.caleydo.datadomain."
				// + parameter.substring(0, delimiterPos);
				// appInitData.addStartView(view, dataDomain);
				// }
			}

			changeWorkspaceLocation();
			GeneralManager.get().getSWTGUIManager();

			if (startupProcedure == null) {
				Shell shell = new Shell();
				WizardDialog projectWizardDialog = new WizardDialog(shell,
						new CaleydoProjectWizard(shell));

				if (projectWizardDialog.open() == Window.CANCEL) {
					shutdown();
				}
			}

			startupProcedure.initPreWorkbenchOpen();
			initRCPWorkbench();
		}
	}

	/** Check whether a string corresponds to a caleydo project file */
	private boolean checkFileName(String candiateString) {
		String[] substrings = candiateString.split(".");
		if (substrings.length > 0) {
			if (substrings[substrings.length - 1].equals("cal"))
				return true;
		}
		return false;
	}

	/**
	 * Changing the workspace location in order to be able to store and restore
	 * the workbench state (also in combination with serialized projects).
	 */
	private void changeWorkspaceLocation() {

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

	/**
	 * Static method for initializing the Caleydo core. Called when initializing
	 * the workbench because XML startup the progress bar is needed
	 */
	public void initCore() {

		startupProcedure.init();
		startupProcedure.execute();

		// ColorMappingManager.get().initiFromPreferenceStore(ColorMappingType.GENE_EXPRESSION);
	}

	private void initRCPWorkbench() {

		try {
			display = PlatformUI.createDisplay();
			applicationWorkbenchAdvisor = new ApplicationWorkbenchAdvisor();
			PlatformUI.createAndRunWorkbench(display, applicationWorkbenchAdvisor);
		} finally {
			shutdown();
		}
	}

	public AStartupProcedure createStartupProcedure(ApplicationMode appMode) {
		switch (appMode) {
		case GUI:
			startupProcedure = new GeneticGUIStartupProcedure();
			break;
		case SERIALIZATION:
			startupProcedure = new SerializationStartupProcedure();
			break;
		case GENERIC:
			startupProcedure = new GenericGUIStartupProcedure();
		}

		return startupProcedure;
	}

	public Display getDisplay() {
		return display;
	}

	public void shutdown() {

		// Save preferences before shutdown
		GeneralManager generalManager = GeneralManager.get();
		try {
			Logger.log(new Status(IStatus.WARNING, this.toString(),
					"Save Caleydo preferences..."));
			generalManager.getPreferenceStore().save();
		} catch (IOException ioException) {
			throw new IllegalStateException("Unable to save preference file at: "
					+ PreferenceManager.getPreferencePath(), ioException);
		}

		IGroupwareManager groupwareManager = generalManager.getGroupwareManager();
		if (groupwareManager != null) {
			groupwareManager.stop();
			generalManager.setGroupwareManager(null);
		}

		generalManager.getViewManager().stopAnimator();

		Logger.log(new Status(IStatus.INFO, this.toString(), "Bye bye!"));
		// display.dispose();

		System.exit(0);
	}

	public AStartupProcedure getStartupProcedure() {
		return startupProcedure;
	}
}
