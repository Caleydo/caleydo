package org.caleydo.core.startup;

import org.caleydo.core.manager.GeneralManager;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

/**
 * This class controls all aspects of the application's execution
 */
public class Application
	implements IApplication {

	/** initialization data received from a Caleydo-server-application during startup */
	// public static DataInitializationData initData = null;

	// TODO: server address for plex-client mode, should be obtained from deskotheque instead from command
	// line param
	// private String serverAddress = null;

	@SuppressWarnings("unchecked")
	@Override
	public Object start(IApplicationContext context) throws Exception {

		GeneralManager.get().getPreferenceStore();

		StartupProcessor.get().initStartupProcudure(context.getArguments());

		// System.out.println("Start Caleydo...");
		// System.out.println("OS Name:" +System.getProperty("os.name"));

		// if (System.getProperty("os.name").contains("Win")) {
		// bIsWindowsOS = true;
		// }

		// // Check if Caleydo will be started the first time and no Internet connection is detected
		// if (prefStore.getBoolean(PreferenceConstants.FIRST_START) && !isInternetConnectionOK()) {
		// WizardDialog internetConfigurationWizard =
		// new WizardDialog(shell, new InternetConfigurationWizard());
		// internetConfigurationWizard.open();
		// }

		// if (Application.applicationMode == ApplicationMode.PLEX_CLIENT) {
		// Application.initData = GroupwareUtils.startPlexClient(serverAddress);

		return IApplication.EXIT_OK;
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

	// if (applicationMode == ApplicationMode.COLLABORATION_CLIENT
	// || applicationMode == ApplicationMode.PLEX_CLIENT) {
	// IDataDomain dataDomain = initData.getDataDomain();
	// LoadDataParameters loadDataParameters = dataDomain.getLoadDataParameters();
	// SetUtils.saveSetFile(loadDataParameters, initData.getTableFileContent());
	// if (initData.getGeneClusterTree() != null) {
	// SetUtils.saveGeneTreeFile(loadDataParameters, initData.getGeneClusterTree());
	// }
	// if (initData.getGeneClusterTree() != null) {
	// SetUtils.saveExperimentsTreeFile(loadDataParameters, initData.getGeneClusterTree());
	// }
	// // TODO remove temporary files (after dimension creation or on shutdown)
	// // FIXME: restore plex client - this is to set specific to work from now on
	// //
	// // SetUtils.createDimensions(loadDataParameters);
	// // DataTable set = SetUtils.createData(dataDomain);
	// //
	// // HashMap<RecordVAType, ContentVirtualArray> recordVAMap = initData.getRecordVAMap();
	// // for (Entry<RecordVAType, ContentVirtualArray> entry : recordVAMap.entrySet()) {
	// // ((ADataDomain) dataDomain).setContentVirtualArray(entry.getKey(), entry.getValue());
	// // }
	// //
	// // HashMap<DimensionVAType, DimensionVirtualArray> dimensionVAMap = initData.getDimensionVAMap();
	// // for (Entry<DimensionVAType, DimensionVirtualArray> entry : dimensionVAMap.entrySet()) {
	// // ((ADataDomain) dataDomain).setDimensionVirtualArray(entry.getKey(), entry.getValue());
	// // }
	//
	// // we need the VAs to be available before the tree is initialized
	// // SetUtils.loadTrees(loadDataParameters, set);
	//
	// Application.initData = null;
	// }

	// public static boolean isInternetConnectionOK() {
	//
	// // Check internet connection
	// try {
	// InetAddress.getByName(ProxyConfigurationPage.TEST_URL);
	// }
	// catch (Exception e) {
	// Application.bIsInterentConnectionOK = false;
	// return false;
	// }
	//
	// bIsInterentConnectionOK = true;
	// return true;
	// }
}