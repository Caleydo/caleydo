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
import org.caleydo.core.data.collection.set.LoadDataParameters;
import org.caleydo.core.data.collection.set.SetUtils;
import org.caleydo.core.data.selection.EVAType;
import org.caleydo.core.data.selection.VirtualArray;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IUseCase;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.specialized.genetic.GeneticUseCase;
import org.caleydo.core.manager.usecase.AUseCase;
import org.caleydo.core.manager.usecase.EDataDomain;
import org.caleydo.core.manager.usecase.UnspecifiedUseCase;
import org.caleydo.core.net.GroupwareUtils;
import org.caleydo.core.net.IGroupwareManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.serialize.ApplicationInitData;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.mapping.color.EColorMappingType;
import org.caleydo.core.util.preferences.PreferenceConstants;
import org.caleydo.rcp.core.bridge.RCPBridge;
import org.caleydo.rcp.progress.PathwayLoadingProgressIndicatorAction;
import org.caleydo.rcp.view.EStartViewType;
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

	/** Temporary solution to store data domain based on the gui dialog */
	public static EDataDomain dataDomain;

	private static String BOOTSTRAP_FILE_GENE_EXPRESSION_MODE =
		"data/bootstrap/shared/webstart/bootstrap_webstart_gene_expression.xml";

	// private static String BOOTSTRAP_FILE_SAMPLE_DATA_MODE =
	// "data/bootstrap/shared/sample/bootstrap_gene_expression_sample.xml";

	// @SuppressWarnings("unused")
	// private static String BOOTSTRAP_FILE_PATHWAY_VIEWER_MODE =
	// "data/bootstrap/shared/webstart/bootstrap_webstart_pathway_viewer.xml";

	private static String REAL_DATA_SAMPLE_FILE =
		"data/genome/microarray/sample/HCC_sample_dataset_4630_24_cluster.csv";

	public static CaleydoBootloader caleydoCoreBootloader;

	public static ApplicationWorkbenchAdvisor applicationWorkbenchAdvisor;

	public static boolean bDoExit = false;
	public static boolean isStartedFromXML = false;

	// The command line arguments overrules the preference store
	// public static boolean bLoadPathwayDataKeggHomoSapiens = true;
	// public static boolean bLoadPathwayDataKeggMusMusculus = true;
	// public static boolean bLoadPathwayDataBiocartaHomoSapiens = true;
	// public static boolean bLoadPathwayDataBiocartaMusMusculus = true;
	public static boolean bOverrulePrefStoreLoadPathwayData = false;
	public static boolean bIsWindowsOS = false;
	public static boolean bIsInterentConnectionOK = false;
	public static boolean bDeleteRestoredWorkbenchState = false;

	/** startup information of the application */
	public static EApplicationMode applicationMode = EApplicationMode.GENE_EXPRESSION_NEW_DATA;

	public static String sCaleydoXMLfile = "";

	/** list of serialized-view class to create during startup */
	public static List<String> startViews;

	/** list of initialized view instances */
	public static List<String> initializedStartViews;

	/** initialization data received from a caleydo-server-application during startup */
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

		startViews = new ArrayList<String>();

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

		// If no file is provided as command line argument a wizard page is opened to determine the xml file
		if (sCaleydoXMLfile.equals("")) {

			if (Application.applicationMode == EApplicationMode.PLEX_CLIENT) {
				Application.initData = GroupwareUtils.startPlexClient(serverAddress);
				GeneralManager.get().addUseCase(Application.initData.getUseCase());
			}
			else {
				WizardDialog projectWizardDialog = new WizardDialog(shell, new CaleydoProjectWizard(shell));
				projectWizardDialog.open();
				if (bDoExit) {
					shutdown();
					System.exit(0);
				}
			}

			if (sCaleydoXMLfile.equals("")) {

				switch (applicationMode) {
					// case GENE_EXPRESSION_PATHWAY_VIEWER:
					// sCaleydoXMLfile = BOOTSTRAP_FILE_PATHWAY_VIEWER_MODE;
					// break;
					case GENE_EXPRESSION_SAMPLE_DATA:
					case GENE_EXPRESSION_NEW_DATA:
						sCaleydoXMLfile = BOOTSTRAP_FILE_GENE_EXPRESSION_MODE;
						GeneralManager.get().getUseCase(EDataDomain.GENETIC_DATA).setBootstrapFileName(
							sCaleydoXMLfile);
						break;
					case UNSPECIFIED_NEW_DATA:
						// not necessary to load any mapping or XML files
						sCaleydoXMLfile = "";
						break;
					case SAMPLE_PROJECT:
					case LOAD_PROJECT:
					case COLLABORATION_CLIENT:
					case PLEX_CLIENT:
						sCaleydoXMLfile =
							GeneralManager.get().getUseCase(EDataDomain.GENETIC_DATA).getBootstrapFileName();
						break;

					default:
						throw new IllegalStateException("Unknown application mode " + applicationMode);
				}
			}
		}
		else {
			// Assuming that if an external XML file is provided, the genetic use case applies
			// IUseCase useCase = new GeneticUseCase();
			// useCase.setBootsTrapFileName(sCaleydoXMLfile);
			// GeneralManager.get().addUseCase(useCase);
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
		String[] sArParam = (String[]) map.get("application.args");

		if (sArParam != null) {
			for (String element : sArParam) {
				if (element.equals("no_pathways")) {
					// bLoadPathwayData = false;
					bOverrulePrefStoreLoadPathwayData = true;
				}
				else if (element.equals("load_pathways")) {
					// bLoadPathwayData = true;
					bOverrulePrefStoreLoadPathwayData = true;
				}
				else if (element.startsWith("plexclient")) {
					if (sCaleydoXMLfile != null && !sCaleydoXMLfile.isEmpty()) {
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
				else {
					EStartViewType viewType = null;
					try {
						viewType = EStartViewType.valueOf(element);
						startViews.add(viewType.getViewID());
					}
					catch (IllegalArgumentException ex) {
						// command line parameter was not a related to a view type, so it must be the
						// bootstrap file
						if (Application.applicationMode == EApplicationMode.PLEX_CLIENT) {
							throw new IllegalArgumentException(
								"It is not allowed to specify a bootstrap-file in plex-client mode.");
						}
						sCaleydoXMLfile = element;
					}

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

		caleydoCoreBootloader.setXmlFileName(sCaleydoXMLfile);
		caleydoCoreBootloader.start();

		Shell shell = new Shell();

		if (applicationMode == EApplicationMode.COLLABORATION_CLIENT
			|| applicationMode == EApplicationMode.PLEX_CLIENT) {
			AUseCase useCase = initData.getUseCase();
			LoadDataParameters loadDataParameters = useCase.getLoadDataParameters();
			SetUtils.saveSetFile(loadDataParameters, initData.getSetFileContent());
			if (initData.getGeneClusterTree() != null) {
				SetUtils.saveGeneTreeFile(loadDataParameters, initData.getGeneClusterTree());
			}
			if (initData.getGeneClusterTree() != null) {
				SetUtils.saveExperimentsTreeFile(loadDataParameters, initData.getGeneClusterTree());
			}
			// TODO remove temporary files (after storage creation or on shutdown)

			GeneralManager.get().addUseCase(useCase);

			if (useCase instanceof GeneticUseCase)
				triggerPathwayLoading();

			SetUtils.createStorages(loadDataParameters);
			SetUtils.createData(useCase);

			HashMap<EVAType, VirtualArray> virtualArrayMap = initData.getVirtualArrayMap();
			for (Entry<EVAType, VirtualArray> entry : virtualArrayMap.entrySet()) {
				useCase.setVirtualArray(entry.getKey(), entry.getValue());
			}
			Application.initData = null;
		}
		else if (applicationMode == EApplicationMode.LOAD_PROJECT || applicationMode == EApplicationMode.SAMPLE_PROJECT) {

			AUseCase useCase = initData.getUseCase();
			GeneralManager.get().addUseCase(useCase);

			LoadDataParameters loadDataParameters = useCase.getLoadDataParameters();
			SetUtils.createStorages(loadDataParameters);
			SetUtils.createData(useCase);

			HashMap<EVAType, VirtualArray> virtualArrayMap = initData.getVirtualArrayMap();
			for (Entry<EVAType, VirtualArray> entry : virtualArrayMap.entrySet()) {
				useCase.setVirtualArray(entry.getKey(), entry.getValue());
			}
			
			if (useCase instanceof GeneticUseCase)
				triggerPathwayLoading();
			
			Application.initData = null;
		}
		else if (applicationMode == EApplicationMode.GENE_EXPRESSION_SAMPLE_DATA) {

			triggerPathwayLoading();

			WizardDialog dataImportWizard =
				new WizardDialog(shell, new DataImportWizard(shell, REAL_DATA_SAMPLE_FILE));

			if (Window.CANCEL == dataImportWizard.open()) {
				shutdown();
				System.exit(0);
			}
		}
		else if ((applicationMode == EApplicationMode.GENE_EXPRESSION_NEW_DATA || applicationMode == EApplicationMode.UNSPECIFIED_NEW_DATA)
			&& (sCaleydoXMLfile.equals(BOOTSTRAP_FILE_GENE_EXPRESSION_MODE) || sCaleydoXMLfile.equals(""))) {

			// if (applicationMode.getDataDomain() == EDataDomain.GENETIC_DATA)
			// triggerPathwayLoading();

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
			for (IUseCase useCase : GeneralManager.get().getAllUseCases()) {
				useCase.updateSetInViews();
			}
		}

		initializeColorMapping();
		if (initializedStartViews == null) {
			initializeDefaultStartViews(applicationMode.getDataDomain());
		}
	}

	public static void initializeColorMapping() {

		ColorMappingManager.get().initiFromPreferenceStore(EColorMappingType.GENE_EXPRESSION);
	}

	/**
	 * Parses throw the list of start-views to initialize them by creating default serialized representations
	 * of them.
	 */
	public static void initializeDefaultStartViews(EDataDomain dataDomain) {
		// Create view list dynamically when not specified via the command line

		if (startViews.isEmpty()) {
			addDefaultStartViews(dataDomain);
		}
		else {

			IUseCase useCase = GeneralManager.get().getUseCase(dataDomain);
			if (dataDomain == EDataDomain.GENETIC_DATA && ((GeneticUseCase) useCase).isPathwayViewerMode()) {
				applyPathwayViewerViewFilter();
			}
		}

		initializedStartViews = new ArrayList<String>();
		for (String viewID : startViews) {

			// Force plugins of start views to load
			try {
				if (viewID.contains("hierarchical"))
					Platform.getBundle(viewID.replace(".hierarchical", "")).start();
				else
					Platform.getBundle(viewID).start();
			}
			catch (NullPointerException ex) {
				System.out.println(viewID);
				ex.printStackTrace();
			}
			catch (BundleException e) {
				// TODO Write message that plugin is not available
				e.printStackTrace();
			}

			ASerializedView view =
				GeneralManager.get().getViewGLCanvasManager().getViewCreator(viewID).createSerializedView();
			view.setDataDomain(dataDomain);
			initializedStartViews.add(viewID);
		}
		startViews = null;
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
	 *            {@link IUseCase} to determine the correct default start views.
	 */
	private static void addDefaultStartViews(EDataDomain dataDomain) {
		startViews.add(EStartViewType.browser.getViewID());

		IUseCase useCase = GeneralManager.get().getUseCase(dataDomain);

		if ((useCase instanceof GeneticUseCase && !((GeneticUseCase) useCase).isPathwayViewerMode())
			|| useCase instanceof UnspecifiedUseCase) {
			// alStartViews.add(EStartViewType.TABULAR);
			startViews.add(EStartViewType.parcoords.getViewID());
			startViews.add(EStartViewType.heatmap.getViewID());
		}

		// Only show bucket when pathway data is loaded
		if (GeneralManager.get().getPathwayManager().size() > 0) {
			startViews.add(EStartViewType.bucket.getViewID());
		}
	}

	/**
	 * Filter all views except remote and browser in case of pathway viewer mode
	 */
	private static void applyPathwayViewerViewFilter() {
		ArrayList<String> newStartViews = new ArrayList<String>();
		for (String viewID : startViews) {
			if (viewID.equals(EStartViewType.bucket.getViewID()) || viewID.equals("org.caleydo.view.browser")) {
				newStartViews.add(viewID);
			}
		}
		startViews = newStartViews;
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

	private static void triggerPathwayLoading() {

			// Trigger pathway loading
			new PathwayLoadingProgressIndicatorAction().run(null);
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
