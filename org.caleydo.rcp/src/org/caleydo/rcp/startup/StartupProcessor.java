package org.caleydo.rcp.startup;

import java.io.IOException;
import java.util.Map;

import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.net.IGroupwareManager;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.mapping.color.EColorMappingType;
import org.caleydo.rcp.Activator;
import org.caleydo.rcp.ApplicationWorkbenchAdvisor;
import org.caleydo.rcp.core.bridge.RCPBridge;
import org.caleydo.rcp.splashHandlers.InteractiveSplashHandler;
import org.caleydo.rcp.wizard.project.CaleydoProjectWizard;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * Startup processor handles the creation of the startup procedure and its execution.
 * 
 * @author Marc Streit
 */
public class StartupProcessor {

	private ApplicationWorkbenchAdvisor applicationWorkbenchAdvisor;

	private static StartupProcessor startupProcessor;

	private static AStartupProcedure startupProcedure;

	private Display display;

	private ApplicationInitData appInitData = new ApplicationInitData();

	private StartupProcessor() {
	}

	public static StartupProcessor get() {
		if (startupProcessor == null) {
			startupProcessor = new StartupProcessor();
		}
		return startupProcessor;
	}

	public void initStartupProcudure(Map<String, Object> applicationArguments) {

		String[] runConfigParameters = (String[]) applicationArguments.get("application.args");

		if (runConfigParameters != null) {
			for (String element : runConfigParameters) {

				if (element.equals("load_pathways")) {
					appInitData.setLoadPathways(true);
				}
				// else if (element.startsWith("plexclient")) {
				// if (xmlInputFile != null && !xmlInputFile.isEmpty()) {
				// throw new IllegalArgumentException(
				// "It is not allowed to specify a bootstrap-file in plex-client mode.");
				// }
				// Application.applicationMode = ApplicationMode.PLEX_CLIENT;
				// if (element.startsWith("plexclient:")) {
				// serverAddress = element.substring("plexclient:".length());
				// }
				// else {
				// serverAddress = "127.0.0.1";
				// }
				// }
				// else

				// Application.initData = GroupwareUtils.startPlexClient(serverAddress);

				if (element.contains(".xml")) {
					startupProcedure = new XMLStartupProcedure();
					((XMLStartupProcedure) startupProcedure).setXMLFileName(element);
				}
				else if (element.contains(":")) {
					// Parse initial start views
					int delimiterPos = element.indexOf(":");
					String view = "org.caleydo.view." + element.substring(delimiterPos + 1, element.length());
					String dataDomain = "org.caleydo.datadomain." + element.substring(0, delimiterPos);
					appInitData.addStartView(view, dataDomain);
				}
			}

			display = PlatformUI.createDisplay();

			initRCPWorkbench();
		}
	}

	/**
	 * Static method for initializing the Caleydo core. Called when initializing the workbench because XML
	 * startup the progress bar is needed
	 */
	public void initCore() {

		GeneralManager.get().getSWTGUIManager();

		GeneralManager.get().init(new RCPBridge());
		GeneralManager.get().getGUIBridge().init();

		if (startupProcedure == null) {
			Shell shell = display.getActiveShell();
			WizardDialog projectWizardDialog = new WizardDialog(shell, new CaleydoProjectWizard(shell));

			if (projectWizardDialog.open() == Window.CANCEL) {
				shutdown();
			}
		}

		startupProcedure.init(appInitData);
		startupProcedure.execute();

		ColorMappingManager.get().initiFromPreferenceStore(EColorMappingType.GENE_EXPRESSION);
	}

	private void initRCPWorkbench() {

		try {
			applicationWorkbenchAdvisor = new ApplicationWorkbenchAdvisor();

			PlatformUI.createAndRunWorkbench(display, applicationWorkbenchAdvisor);

			GeneralManager.get().getPreferenceStore().setValue("firstStart", false);
		}
		finally {
			shutdown();
		}
	}

	public AStartupProcedure createStartupProcedure(ApplicationMode appMode) {
		switch (appMode) {
			case GUI:
				startupProcedure = new GUIStartupProcedure();
				break;
			case SERIALIZATION:
				startupProcedure = new SerializationStartupProcedure();
				break;
			case XML:
				startupProcedure = new XMLStartupProcedure();
				break;
		}

		return startupProcedure;
	}

	public Display getDisplay() {
		return display;
	}

	public void shutdown() {

		// Save preferences before shutdown
		IGeneralManager generalManager = GeneralManager.get();
		try {
			generalManager.getLogger().log(
				new Status(IStatus.WARNING, Activator.PLUGIN_ID, "Save Caleydo preferences..."));
			generalManager.getPreferenceStore().save();
		}
		catch (IOException e) {
			throw new IllegalStateException("Unable to save preference file.");
		}

		IGroupwareManager groupwareManager = generalManager.getGroupwareManager();
		if (groupwareManager != null) {
			groupwareManager.stop();
			generalManager.setGroupwareManager(null);
		}

		generalManager.getViewGLCanvasManager().stopAnimator();

		generalManager.getLogger().log(new Status(IStatus.INFO, Activator.PLUGIN_ID, "Bye bye!"));
		// display.dispose();

		System.exit(0);
	}

	public ApplicationInitData getAppInitData() {
		return appInitData;
	}

	public AStartupProcedure getStartupProcedure() {
		return startupProcedure;
	}
}
