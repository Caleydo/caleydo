package org.caleydo.rcp;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.caleydo.core.application.core.CaleydoBootloader;
import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.data.CmdDataCreateDataDomain;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.set.LoadDataParameters;
import org.caleydo.core.data.collection.set.SetUtils;
import org.caleydo.core.data.selection.ContentVAType;
import org.caleydo.core.data.selection.ContentVirtualArray;
import org.caleydo.core.data.selection.StorageVAType;
import org.caleydo.core.data.selection.StorageVirtualArray;
import org.caleydo.core.manager.IDataDomain;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.ISetBasedDataDomain;
import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.net.GroupwareUtils;
import org.caleydo.core.net.IGroupwareManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.serialize.ApplicationInitData;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.mapping.color.EColorMappingType;
import org.caleydo.core.util.preferences.PreferenceConstants;
import org.caleydo.rcp.core.bridge.RCPBridge;
import org.caleydo.rcp.view.RCPViewManager;
import org.caleydo.rcp.wizard.firststart.InternetConfigurationWizard;
import org.caleydo.rcp.wizard.firststart.ProxyConfigurationPage;
import org.caleydo.rcp.wizard.project.CaleydoProjectWizard;
import org.caleydo.rcp.wizard.project.DataImportWizard;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.osgi.framework.BundleException;

/**
 * This class controls all aspects of the application's execution
 */
@SuppressWarnings("restriction")
public class Application
	implements IApplication {

	/**
	 * determines if initialize-views should be loaded lazy (=<code>true</code>)or immediate (=
	 * <code>false</code)
	 */
	public static final boolean LAZY_VIEW_LOADING = false;

	private static String BOOTSTRAP_FILE_GENE_EXPRESSION_MODE =
		"data/bootstrap/shared/webstart/bootstrap_webstart_gene_expression.xml";

	private static String REAL_DATA_SAMPLE_FILE =
		"data/genome/microarray/sample/HCC_sample_dataset_4630_24_cluster.csv";

	public static CaleydoBootloader caleydoCoreBootloader;

	public static ApplicationWorkbenchAdvisor applicationWorkbenchAdvisor;

	public static boolean bDoExit = false;
	public static boolean isStartedFromXML = false;

	// The command line arguments overrules the preference store
	public static boolean triggerEarlyPathwayLoading = false;
	public static boolean bIsWindowsOS = false;
	public static boolean bIsInterentConnectionOK = false;
	public static boolean bDeleteRestoredWorkbenchState = false;

	/** startup information of the application */
	public static EApplicationMode applicationMode = EApplicationMode.GENE_EXPRESSION_NEW_DATA;

	public static String xmlInputFile = "";

	/**
	 * list of serialized-view class to create during startup, the first string is the view, the second the
	 * datadomain
	 */
	public static List<Pair<String, String>> startViewWithDataDomain;

	/** list of initialized view instances */
	public static List<String> initializedStartViews;

	/** initialization data received from a Caleydo-server-application during startup */
	public static ApplicationInitData initData = null;

	public RCPBridge rcpGuiBridge;

	private PreferenceStore prefStore;

	// TODO: server address for plex-client mode, should be obtained from deskotheque instead from command
	// line param
	private String serverAddress = null;

	@Override
	@SuppressWarnings("unchecked")
	public Object start(IApplicationContext context) throws Exception {
		// System.out.println("Start Caleydo...");
		// System.out.println("OS Name:" +System.getProperty("os.name"));

		if (System.getProperty("os.name").contains("Win")) {
			bIsWindowsOS = true;
		}

		startViewWithDataDomain = new ArrayList<Pair<String, String>>();

		Map<String, Object> map = context.getArguments();

		rcpGuiBridge = new RCPBridge();

		// Create Caleydo core
		caleydoCoreBootloader = new CaleydoBootloader(rcpGuiBridge);

		parseApplicationArguments(map);

		prefStore = GeneralManager.get().getPreferenceStore();

		Display display = PlatformUI.createDisplay();
		Shell shell = new Shell(display);

		GeneralManager.get().getViewGLCanvasManager().init();

		// Check if Caleydo will be started the first time and no Internet connection is detected
		if (prefStore.getBoolean(PreferenceConstants.FIRST_START) && !isInternetConnectionOK()) {
			WizardDialog internetConfigurationWizard =
				new WizardDialog(shell, new InternetConfigurationWizard());
			internetConfigurationWizard.open();
		}

//		if (triggerEarlyPathwayLoading) {
//			 CmdDataCreateDataDomain cmd = new CmdDataCreateDataDomain(ECommandType.CREATE_DATA_DOMAIN);
//			 cmd.setAttributes("org.caleydo.datadomain.pathway");
//			 cmd.doCommand();
//		}

		// If no file is provided as command line argument a wizard page is opened to determine the xml file
		if (xmlInputFile.equals("")) {

			if (Application.applicationMode == EApplicationMode.PLEX_CLIENT) {
				Application.initData = GroupwareUtils.startPlexClient(serverAddress);
			}
			else {
				WizardDialog projectWizardDialog = new WizardDialog(shell, new CaleydoProjectWizard(shell));
				projectWizardDialog.open();
				if (bDoExit) {
					shutdown();
					System.exit(0);
				}
			}

			switch (applicationMode) {
				case GENE_EXPRESSION_SAMPLE_DATA:
				case GENE_EXPRESSION_NEW_DATA:
					xmlInputFile = BOOTSTRAP_FILE_GENE_EXPRESSION_MODE;
					DataDomainManager.getInstance().getDataDomain("org.caleydo.datadomain.genetic")
						.setBootstrapFileName(xmlInputFile);
					break;
				case UNSPECIFIED_NEW_DATA:
				case NO_DATA:
					// not necessary to load any mapping or XML files
					xmlInputFile = "";
					break;
				case SAMPLE_PROJECT:
				case LOAD_PROJECT:
				case COLLABORATION_CLIENT:
				case PLEX_CLIENT:
					// TODO - make sure this is not needed
					// sCaleydoXMLfile = GeneralManager.get().getMasterUseCase().getBootstrapFileName();

					break;

				default:
					throw new IllegalStateException("Unknown application mode " + applicationMode);
			}
		}
		else {
			isStartedFromXML = true;
		}

		if (bDeleteRestoredWorkbenchState) {
			removeStoredWorkbenchState();
		}

		try {
			applicationWorkbenchAdvisor = new ApplicationWorkbenchAdvisor();

			int returnCode = PlatformUI.createAndRunWorkbench(display, applicationWorkbenchAdvisor);

			GeneralManager.get().getPreferenceStore().setValue("firstStart", false);

			if (returnCode == PlatformUI.RETURN_RESTART) {
				return IApplication.EXIT_RESTART;
			}
			else {
				return IApplication.EXIT_OK;
			}
		}
		finally {
			if (!bDoExit) {
				shutdown();
			}
		}
	}

	private void parseApplicationArguments(Map<String, Object> map) {
		String[] runConfigParameters = (String[]) map.get("application.args");

		if (runConfigParameters != null) {
			for (String element : runConfigParameters) {

				if (element.equals("load_pathways")) {
					// Load pathway domain and therefore trigger pathway loading
					triggerEarlyPathwayLoading = true;
				}
				else if (element.startsWith("plexclient")) {
					if (xmlInputFile != null && !xmlInputFile.isEmpty()) {
						throw new IllegalArgumentException(
							"It is not allowed to specify a bootstrap-file in plex-client mode.");
					}
					Application.applicationMode = EApplicationMode.PLEX_CLIENT;
					if (element.startsWith("plexclient:")) {
						serverAddress = element.substring("plexclient:".length());
					}
					else {
						serverAddress = "127.0.0.1";
					}
				}
				else if (element.contains(".xml")) {
					// command line parameter was not a related to a view type, so it must be the
					// bootstrap file
					if (Application.applicationMode == EApplicationMode.PLEX_CLIENT) {
						throw new IllegalArgumentException(
							"It is not allowed to specify a bootstrap-file in plex-client mode.");
					}
					xmlInputFile = element;
				}
				else if (element.contains(":")) {
					// Parse initial start views
					int delimiterPos = element.indexOf(":");
					String view = "org.caleydo.view." + element.substring(delimiterPos + 1, element.length());
					String dataDomain = "org.caleydo.datadomain." + element.substring(0, delimiterPos);
					startViewWithDataDomain.add(new Pair<String, String>(view, dataDomain));
				}
			}
		}
	}

	private static void shutdown() {
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

	public static void startCaleydoCore() {

		caleydoCoreBootloader.setXmlFileName(xmlInputFile);
		caleydoCoreBootloader.start();

		GeneralManager.get().getGUIBridge().init();

		Shell shell = new Shell();

		if (applicationMode == EApplicationMode.COLLABORATION_CLIENT
			|| applicationMode == EApplicationMode.PLEX_CLIENT) {
			IDataDomain dataDomain = initData.getDataDomain();
			LoadDataParameters loadDataParameters = dataDomain.getLoadDataParameters();
			SetUtils.saveSetFile(loadDataParameters, initData.getSetFileContent());
			if (initData.getGeneClusterTree() != null) {
				SetUtils.saveGeneTreeFile(loadDataParameters, initData.getGeneClusterTree());
			}
			if (initData.getGeneClusterTree() != null) {
				SetUtils.saveExperimentsTreeFile(loadDataParameters, initData.getGeneClusterTree());
			}
			// TODO remove temporary files (after storage creation or on shutdown)
			// FIXME: restore plex client - this is to set specific to work from now on
			//
			// SetUtils.createStorages(loadDataParameters);
			// ISet set = SetUtils.createData(dataDomain);
			//
			// HashMap<ContentVAType, ContentVirtualArray> contentVAMap = initData.getContentVAMap();
			// for (Entry<ContentVAType, ContentVirtualArray> entry : contentVAMap.entrySet()) {
			// ((ADataDomain) dataDomain).setContentVirtualArray(entry.getKey(), entry.getValue());
			// }
			//
			// HashMap<StorageVAType, StorageVirtualArray> storageVAMap = initData.getStorageVAMap();
			// for (Entry<StorageVAType, StorageVirtualArray> entry : storageVAMap.entrySet()) {
			// ((ADataDomain) dataDomain).setStorageVirtualArray(entry.getKey(), entry.getValue());
			// }

			// we need the VAs to be available before the tree is initialized
			// SetUtils.loadTrees(loadDataParameters, set);

			Application.initData = null;
		}
		else if (applicationMode == EApplicationMode.LOAD_PROJECT
			|| applicationMode == EApplicationMode.SAMPLE_PROJECT) {

			IDataDomain dataDomain = initData.getDataDomain();
			DataDomainManager.getInstance().register(dataDomain);

			if (!(dataDomain instanceof ISetBasedDataDomain))
				throw new IllegalStateException(
					"loading data is not supported for non-set-based data domains. Implement it!");

			ISetBasedDataDomain setBasedDataDomain = (ISetBasedDataDomain) dataDomain;

			LoadDataParameters loadDataParameters = dataDomain.getLoadDataParameters();
			SetUtils.createStorages(loadDataParameters);
			ISet set = SetUtils.createData(setBasedDataDomain);

			HashMap<ContentVAType, ContentVirtualArray> contentVAMap = initData.getContentVAMap();
			for (Entry<ContentVAType, ContentVirtualArray> entry : contentVAMap.entrySet()) {
				setBasedDataDomain.setContentVirtualArray(entry.getKey(), entry.getValue());
			}

			HashMap<StorageVAType, StorageVirtualArray> storageVAMap = initData.getStorageVAMap();
			for (Entry<StorageVAType, StorageVirtualArray> entry : storageVAMap.entrySet()) {
				setBasedDataDomain.setStorageVirtualArray(entry.getKey(), entry.getValue());
			}
			// we need the VAs to be available before the tree is initialized
			SetUtils.loadTrees(loadDataParameters, set);

			Application.initData = null;
		}
		else if (applicationMode == EApplicationMode.GENE_EXPRESSION_SAMPLE_DATA) {

			WizardDialog dataImportWizard =
				new WizardDialog(shell, new DataImportWizard(shell, REAL_DATA_SAMPLE_FILE));

			if (Window.CANCEL == dataImportWizard.open()) {
				shutdown();
				System.exit(0);
			}

			((ISetBasedDataDomain) DataDomainManager.getInstance().getDataDomain(
				"org.caleydo.datadomain.genetic")).updateSetInViews();

			startViewWithDataDomain.add(new Pair<String, String>("org.caleydo.view.heatmap.hierarchical",
				"org.caleydo.datadomain.genetic"));
			startViewWithDataDomain.add(new Pair<String, String>("org.caleydo.view.parcoords",
				"org.caleydo.datadomain.genetic"));
			startViewWithDataDomain.add(new Pair<String, String>("org.caleydo.view.bucket",
				"org.caleydo.datadomain.genetic"));
			startViewWithDataDomain.add(new Pair<String, String>("org.caleydo.view.histogram",
				"org.caleydo.datadomain.genetic"));

		}
		else if ((applicationMode == EApplicationMode.GENE_EXPRESSION_NEW_DATA || applicationMode == EApplicationMode.UNSPECIFIED_NEW_DATA)
			&& (xmlInputFile.equals(BOOTSTRAP_FILE_GENE_EXPRESSION_MODE) || xmlInputFile.equals(""))) {

			WizardDialog dataImportWizard = new WizardDialog(shell, new DataImportWizard(shell));

			if (Window.CANCEL == dataImportWizard.open()) {
				shutdown();
				System.exit(0);
			}
		}

		// TODO - this initializes the VA after the data is written correctly in the set - probably not the
		// nicest place to do this.
		// This is only necessary if started from xml. Otherwise this is done in FileLoadDataAction
		if (isStartedFromXML) {
			for(IDataDomain dataDomain : DataDomainManager.getInstance().getDataDomains())
			{
				if(dataDomain instanceof ISetBasedDataDomain)
					((ISetBasedDataDomain) dataDomain).updateSetInViews();
			}
		}

		initializeColorMapping();
		if (initializedStartViews == null) {
			initializeStartViews();
		}
	}

	public static void initializeColorMapping() {

		ColorMappingManager.get().initiFromPreferenceStore(EColorMappingType.GENE_EXPRESSION);
	}

	/**
	 * Parses throw the list of start-views to initialize them by creating default serialized representations
	 * of them.
	 */
	public static void initializeStartViews() {
		// Create view list dynamically when not specified via the command line

		if (startViewWithDataDomain.isEmpty()) {
			addDefaultStartViews();
		}

		initializedStartViews = new ArrayList<String>();
		for (Pair<String, String> viewWithDataDomain : startViewWithDataDomain) {

			// Force plugins of start views to load
			try {
				String viewType = viewWithDataDomain.getFirst();

				if (!viewType.contains("unspecified")) {
					if (viewType.contains("hierarchical"))
						viewType = viewType.replace(".hierarchical", "");

					Platform.getBundle(viewType).start();
				}
			}
			catch (NullPointerException ex) {
				System.out.println("Cannot load view plugin " + viewWithDataDomain.getFirst());
				ex.printStackTrace();
			}
			catch (BundleException e) {
				// TODO Write message that plugin is not available
				e.printStackTrace();
			}

			ASerializedView view =
				GeneralManager.get().getViewGLCanvasManager().getViewCreator(viewWithDataDomain.getFirst())
					.createSerializedView();

			view.setDataDomainType(viewWithDataDomain.getSecond());
			initializedStartViews.add(viewWithDataDomain.getFirst());
		}
	}

	public static void openRCPViews(IFolderLayout layout) {

		// Create RCP view manager
		RCPViewManager.get();

		if (Application.LAZY_VIEW_LOADING) {
			for (String startViewID : initializedStartViews) {
				layout.addView(startViewID);
			}
		}
	}

	/**
	 * Adds the default start views. Used when no start views are defined with command line arguments.
	 * 
	 * @param useCase
	 *            {@link IDataDomain} to determine the correct default start views.
	 */
	private static void addDefaultStartViews() {

		startViewWithDataDomain.add(new Pair<String, String>("org.caleydo.view.browser",
			"org.caleydo.datadomain.genetic"));

		// Only show bucket when pathway data is loaded
//		if (GeneralManager.get().getPathwayManager().size() > 0) {
//			startViewWithDataDomain.add(new Pair<String, String>("org.caleydo.view.bucket",
//				"org.caleydo.datadomain.genetic"));
//		}
	}

	public static boolean isInternetConnectionOK() {

		// Check internet connection
		try {
			InetAddress.getByName(ProxyConfigurationPage.TEST_URL);
		}
		catch (Exception e) {
			Application.bIsInterentConnectionOK = false;
			return false;
		}

		bIsInterentConnectionOK = true;
		return true;
	}

	private void removeStoredWorkbenchState() {

		IPath path = WorkbenchPlugin.getDefault().getDataLocation();
		if (path == null) {
			return;
		}

		deleteDir(path.toFile());
	}

	// Deletes all files and subdirectories under dir.
	// Returns true if all deletions were successful.
	// If a deletion fails, the method stops attempting to delete and returns
	// false.
	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (String element : children) {
				boolean success = deleteDir(new File(dir, element));
				if (!success)
					return false;
			}
		}

		// The directory is now empty so delete it
		return dir.delete();
	}
}