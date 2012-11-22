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

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;

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

		// Load project if provided via webstart system property
		setProjectLocationFromSystemProperty();

		handleProgramArguments(applicationArguments);

		changeWorkspaceLocation();
		GeneralManager.get().getSWTGUIManager();


		if (startupProcedure == null) {
			Shell shell = new Shell();
			WizardDialog projectWizardDialog = new WizardDialog(shell,
					new CaleydoProjectWizard(shell));

			if (projectWizardDialog.open() == Window.CANCEL) {
				shutdown();
				return;
			}
		}

		startupProcedure.initPreWorkbenchOpen();
		initRCPWorkbench();
	}

	private void handleProgramArguments(Map<String, Object> applicationArguments) {

		String[] runConfigParameters = (String[]) applicationArguments
				.get("application.args");

		String argumentString = "[";
		for (String argument : runConfigParameters) {
			argumentString += argument + " ";
		}
		argumentString += "]";
		Logger.log(new Status(Status.INFO, this.toString(), "Application arguments: "
				+ argumentString));

		JSAP jsap = new JSAP();
		try {
			UnflaggedOption project = new UnflaggedOption("project")
					.setStringParser(JSAP.STRING_PARSER).setDefault("")
					.setRequired(false).setGreedy(false);
			jsap.registerParameter(project);

			FlaggedOption projectFlagged = new FlaggedOption("projectFlagged")
					.setStringParser(JSAP.STRING_PARSER).setDefault(JSAP.NO_DEFAULT)
					.setRequired(false).setShortFlag('p').setLongFlag(JSAP.NO_LONGFLAG);
			projectFlagged.setHelp("Load a caleydo project file [project_filename.cal]");
			jsap.registerParameter(projectFlagged);

			JSAPResult config = jsap.parse(runConfigParameters);

			// check whether the command line was valid, and if it wasn't,
			// display usage information and exit.
			if (!config.success()) {
				Logger.log(new Status(Status.ERROR, this.toString(),
						"Failed to parse program line arguments with JSAP: "
								+ argumentString));

				handleJSAPError(jsap);
				return;
			}

			String projectFileName = config.getString("project");

			if (projectFileName.isEmpty())
				projectFileName = config.getString("projectFlagged");

			boolean isProjectFile = false;
			if (projectFileName != null) {
				isProjectFile = checkFileName(projectFileName);
				if (isProjectFile) {
					startupProcedure = new SerializationStartupProcedure();
					((SerializationStartupProcedure) startupProcedure)
							.setProjectLocation(projectFileName);
				}
			}

		} catch (JSAPException e) {
			e.printStackTrace();

			Logger.log(new Status(Status.ERROR, this.toString(),
					"Error during parsing of program arguments", e));
			handleJSAPError(jsap);
		}
	}

	private void handleJSAPError(JSAP jsap) {
		System.err.println("Error during parsing of program arguments. Closing program.");
		System.err.println("Usage: Caleydo");
		System.err.println(jsap.getUsage());
		System.err.println();
	}

	/**
	 * Sets the caleydo project when specified as a system property via webstart
	 */
	private void setProjectLocationFromSystemProperty() {
		String projectLocation = System.getProperty("caleydo.project.location");
		if (projectLocation != null) {
			boolean isFile = checkFileName(projectLocation);
			if (isFile) {
				startupProcedure = new SerializationStartupProcedure();
				((SerializationStartupProcedure) startupProcedure)
						.setProjectLocation(projectLocation);
			}
		}
	}

	/** Check whether a string corresponds to a caleydo project file */
	private boolean checkFileName(String candiateString) {
		if (candiateString.endsWith(".cal"))
			return true;

		System.err.println("The specified project " + candiateString
				+ " is not a *.cal file");
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

		generalManager.getViewManager().stopAnimator();

		Logger.log(new Status(IStatus.INFO, this.toString(), "Bye bye!"));
	}

	public AStartupProcedure getStartupProcedure() {
		return startupProcedure;
	}
}
