package org.caleydo.rcp;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.serialize.ApplicationInitData;
import org.caleydo.rcp.startup.StartupProcessor;
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

	public static boolean bDeleteRestoredWorkbenchState = false;

	/** initialization data received from a Caleydo-server-application during startup */
	public static ApplicationInitData initData = null;

	// TODO: server address for plex-client mode, should be obtained from deskotheque instead from command
	// line param
	// private String serverAddress = null;

	@SuppressWarnings("unchecked")
	@Override
	public Object start(IApplicationContext context) throws Exception {

		GeneralManager.get().getPreferenceStore();
		GeneralManager.get().getViewGLCanvasManager().init();

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
		
		// if (bDeleteRestoredWorkbenchState) {
		// removeStoredWorkbenchState();
		// }

		return IApplication.EXIT_OK;
	}

	@Override
	public void stop() {
		final IWorkbench workbench = PlatformUI.getWorkbench();

		if (workbench == null)
			return;

		final Display display = workbench.getDisplay();
		display.syncExec(new Runnable() {
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
	// SetUtils.saveSetFile(loadDataParameters, initData.getSetFileContent());
	// if (initData.getGeneClusterTree() != null) {
	// SetUtils.saveGeneTreeFile(loadDataParameters, initData.getGeneClusterTree());
	// }
	// if (initData.getGeneClusterTree() != null) {
	// SetUtils.saveExperimentsTreeFile(loadDataParameters, initData.getGeneClusterTree());
	// }
	// // TODO remove temporary files (after storage creation or on shutdown)
	// // FIXME: restore plex client - this is to set specific to work from now on
	// //
	// // SetUtils.createStorages(loadDataParameters);
	// // ISet set = SetUtils.createData(dataDomain);
	// //
	// // HashMap<ContentVAType, ContentVirtualArray> contentVAMap = initData.getContentVAMap();
	// // for (Entry<ContentVAType, ContentVirtualArray> entry : contentVAMap.entrySet()) {
	// // ((ADataDomain) dataDomain).setContentVirtualArray(entry.getKey(), entry.getValue());
	// // }
	// //
	// // HashMap<StorageVAType, StorageVirtualArray> storageVAMap = initData.getStorageVAMap();
	// // for (Entry<StorageVAType, StorageVirtualArray> entry : storageVAMap.entrySet()) {
	// // ((ADataDomain) dataDomain).setStorageVirtualArray(entry.getKey(), entry.getValue());
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

	// private void removeStoredWorkbenchState() {
	//
	// IPath path = WorkbenchPlugin.getDefault().getDataLocation();
	// if (path == null) {
	// return;
	// }
	//
	// deleteDir(path.toFile());
	// }

	// // Deletes all files and subdirectories under dir.
	// // Returns true if all deletions were successful.
	// // If a deletion fails, the method stops attempting to delete and returns
	// // false.
	// public static boolean deleteDir(File dir) {
	// if (dir.isDirectory()) {
	// String[] children = dir.list();
	// for (String element : children) {
	// boolean success = deleteDir(new File(dir, element));
	// if (!success)
	// return false;
	// }
	// }
	//
	// // The directory is now empty so delete it
	// return dir.delete();
	// }
}