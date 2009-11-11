package org.caleydo.rcp;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import org.caleydo.core.application.core.CaleydoBootloader;
import org.caleydo.core.data.collection.set.LoadDataParameters;
import org.caleydo.core.data.collection.set.SetUtils;
import org.caleydo.core.data.selection.VirtualArray;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IUseCase;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.specialized.genetic.EOrganism;
import org.caleydo.core.manager.specialized.genetic.GeneticUseCase;
import org.caleydo.core.manager.specialized.genetic.pathway.EPathwayDatabaseType;
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
import org.caleydo.core.view.opengl.canvas.remote.SerializedRemoteRenderingView;
import org.caleydo.core.view.opengl.canvas.storagebased.EVAType;
import org.caleydo.core.view.opengl.canvas.storagebased.SerializedHierarchicalHeatMapView;
import org.caleydo.core.view.opengl.canvas.storagebased.SerializedParallelCoordinatesView;
import org.caleydo.core.view.swt.browser.SerializedHTMLBrowserView;
import org.caleydo.rcp.core.bridge.RCPBridge;
import org.caleydo.rcp.progress.PathwayLoadingProgressIndicatorAction;
import org.caleydo.rcp.view.RCPViewManager;
import org.caleydo.rcp.wizard.firststart.FetchPathwayWizard;
import org.caleydo.rcp.wizard.firststart.InternetConfigurationWizard;
import org.caleydo.rcp.wizard.firststart.ProxyConfigurationPage;
import org.caleydo.rcp.wizard.project.CaleydoProjectWizard;
import org.caleydo.rcp.wizard.project.DataImportWizard;
import org.eclipse.core.runtime.IPath;
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

/**
 * This class controls all aspects of the application's execution
 */
@SuppressWarnings("restriction")
public class Application
	implements IApplication {

	public static final boolean RELEASE_MODE = true;
	
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

	@SuppressWarnings("unused")
	private static String BOOTSTRAP_FILE_PATHWAY_VIEWER_MODE =
		"data/bootstrap/shared/webstart/bootstrap_webstart_pathway_viewer.xml";

	private static String REAL_DATA_SAMPLE_FILE =
		"data/genome/microarray/sample/HCC_sample_dataset_4630_24_cluster.csv";

	public static CaleydoBootloader caleydoCoreBootloader;

	public static ApplicationWorkbenchAdvisor applicationWorkbenchAdvisor;

	public static boolean bIsWebstart = false;
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
	public static List<Class<? extends ASerializedView>> startViews;

	/** list of initialized {@link ASerializedView} instances */
	public static List<ASerializedView> initializedStartViews;

	/** initialization data received from a caleydo-server-application during startup */
	public static ApplicationInitData initData = null;

	public RCPBridge rcpGuiBridge;

	private PreferenceStore prefStore;

	@Override
	@SuppressWarnings("unchecked")
	public Object start(IApplicationContext context) throws Exception {
		// System.out.println("Start Caleydo...");
		// System.out.println("OS Name:" +System.getProperty("os.name"));

		if (System.getProperty("os.name").contains("Win")) {
			bIsWindowsOS = true;
		}

		startViews = new ArrayList<Class<? extends ASerializedView>>();

		Map<String, Object> map = (Map<String, Object>) context.getArguments();
		parseApplicationArguments(map);

		rcpGuiBridge = new RCPBridge();

		// Create Caleydo core
		caleydoCoreBootloader = new CaleydoBootloader(rcpGuiBridge);

		prefStore = GeneralManager.get().getPreferenceStore();

		Display display = PlatformUI.createDisplay();
		Shell shell = new Shell(display);

		// Check if Caleydo will be started the first time and no Internet connection is detected
		if (prefStore.getBoolean(PreferenceConstants.FIRST_START) && !isInternetConnectionOK()) {
			WizardDialog internetConfigurationWizard =
				new WizardDialog(shell, new InternetConfigurationWizard());
			internetConfigurationWizard.open();
		}

		// If no file is provided as command line argument a wizard page is opened to determine the xml file
		if (sCaleydoXMLfile.equals("")) {

			if (Application.applicationMode == EApplicationMode.PLEX_CLIENT) {
				Application.initData = GroupwareUtils.startPlexClient();
				GeneralManager.get().addUseCase(Application.initData.getUseCase());
			}
			else {
				WizardDialog projectWizardDialog = new WizardDialog(shell, new CaleydoProjectWizard(shell));
				if (Window.CANCEL == projectWizardDialog.open()) {
					shutDown();
				}
			}

			if (sCaleydoXMLfile.equals("")) {
				IUseCase useCase = GeneralManager.get().getUseCase(EDataDomain.GENETIC_DATA);
				switch (applicationMode) {
					// case GENE_EXPRESSION_PATHWAY_VIEWER:
					// sCaleydoXMLfile = BOOTSTRAP_FILE_PATHWAY_VIEWER_MODE;
					// break;
					case GENE_EXPRESSION_SAMPLE_DATA:
					case GENE_EXPRESSION_NEW_DATA:
						sCaleydoXMLfile = BOOTSTRAP_FILE_GENE_EXPRESSION_MODE;
						useCase.setBootstrapFileName(sCaleydoXMLfile);
						break;
					case UNSPECIFIED_NEW_DATA:
						// not necessary to load any mapping or XML files
						sCaleydoXMLfile = "";
						useCase.setBootstrapFileName(sCaleydoXMLfile);
						break;
					case LOAD_PROJECT:
					case COLLABORATION_CLIENT:
					case PLEX_CLIENT:
						sCaleydoXMLfile = useCase.getBootstrapFileName();
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

		triggerPathwayFetching();

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
				shutDown();
			}
		}
	}

	private void parseApplicationArguments(Map<String, Object> map) {
		String[] sArParam = (String[]) map.get("application.args");

		if (sArParam != null) {
			for (String element : sArParam) {
				if (element.equals("webstart")) {
					bIsWebstart = true;
				}
				else if (element.equals("no_pathways")) {
					// bLoadPathwayData = false;
					bOverrulePrefStoreLoadPathwayData = true;
				}
				else if (element.equals("load_pathways")) {
					// bLoadPathwayData = true;
					bOverrulePrefStoreLoadPathwayData = true;
				}
				else if (element.equals("plexclient")) {
					if (sCaleydoXMLfile != null && !sCaleydoXMLfile.isEmpty()) {
						throw new IllegalArgumentException(
							"It is not allowed to specify a bootstrap-file in plex-client mode.");
					}
					Application.applicationMode = EApplicationMode.PLEX_CLIENT;
				}
				else {
					EStartViewType viewType = null;
					try {
						viewType = EStartViewType.valueOf(element);
						startViews.add(viewType.getSerializedViewClass());
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

	private void shutDown() {
		// Save preferences before shutdown
		IGeneralManager generalManager = GeneralManager.get();
		try {
			generalManager.getLogger().log(
				new Status(Status.WARNING, Activator.PLUGIN_ID, "Save Caleydo preferences..."));
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

		generalManager.getLogger().log(new Status(Status.INFO, Activator.PLUGIN_ID, "Bye bye!"));
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
			AUseCase useCase = (AUseCase) initData.getUseCase();
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
		else if (applicationMode == EApplicationMode.LOAD_PROJECT) {
			System.out.println("Load Project");
			AUseCase useCase = (AUseCase) initData.getUseCase();
			GeneralManager.get().addUseCase(useCase);

			if (useCase instanceof GeneticUseCase)
				triggerPathwayLoading();

			LoadDataParameters loadDataParameters = useCase.getLoadDataParameters();
			SetUtils.createStorages(loadDataParameters);
			SetUtils.createData(useCase);

			HashMap<EVAType, VirtualArray> virtualArrayMap = initData.getVirtualArrayMap();
			for (Entry<EVAType, VirtualArray> entry : virtualArrayMap.entrySet()) {
				useCase.setVirtualArray(entry.getKey(), entry.getValue());
			}
			Application.initData = null;
		}
		else if (applicationMode == EApplicationMode.GENE_EXPRESSION_SAMPLE_DATA) {

			triggerPathwayLoading();

			WizardDialog dataImportWizard =
				new WizardDialog(shell, new DataImportWizard(shell, REAL_DATA_SAMPLE_FILE));

			if (Window.CANCEL == dataImportWizard.open()) {
				bDoExit = true;
			}
		}
		else if ((applicationMode == EApplicationMode.GENE_EXPRESSION_NEW_DATA || applicationMode == EApplicationMode.UNSPECIFIED_NEW_DATA)
			&& (sCaleydoXMLfile.equals(BOOTSTRAP_FILE_GENE_EXPRESSION_MODE) || sCaleydoXMLfile.equals(""))) {

			if (applicationMode.getDataDomain() == EDataDomain.GENETIC_DATA)
				triggerPathwayLoading();

			WizardDialog dataImportWizard = new WizardDialog(shell, new DataImportWizard(shell));

			if (Window.CANCEL == dataImportWizard.open()) {
				bDoExit = true;
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

		// if (GeneralManager.get().isStandalone()) {
		// // Start OpenGL rendering
		// GeneralManager.get().getViewGLCanvasManager().startAnimator();
		// GeneralManager.get().getSWTGUIManager().runApplication();
		// }
	}

	public static void initializeColorMapping() {

		ColorMappingManager.get().initiFromPreferenceStore(EColorMappingType.GENE_EXPRESSION);
	}

	/**
	 * parses throw the list of start-views to initialize them by creating default serialized representations
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

		initializedStartViews = new ArrayList<ASerializedView>();
		for (Class<? extends ASerializedView> viewType : startViews) {
			try {
				initializedStartViews.add(viewType.newInstance());
			}
			catch (IllegalAccessException ex) {
				ex.printStackTrace();
				// nothing we can do when the views no-arg constructor not public
			}
			catch (InstantiationException ex) {
				throw new RuntimeException("Error while instantiating a view of type '" + viewType + "'", ex);
			}
		}
		startViews = null;
	}

	public static void openRCPViews(IFolderLayout layout) {

		// Create RCP view manager
		RCPViewManager.get();

		if (Application.LAZY_VIEW_LOADING) {
			for (ASerializedView startView : initializedStartViews) {
				layout.addView(startView.getViewGUIID());
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
		startViews.add(SerializedHTMLBrowserView.class);

		IUseCase useCase = GeneralManager.get().getUseCase(dataDomain);
		if ((dataDomain == EDataDomain.GENETIC_DATA && !((GeneticUseCase) useCase).isPathwayViewerMode())
			|| useCase instanceof UnspecifiedUseCase) {
			// alStartViews.add(EStartViewType.TABULAR);
			startViews.add(SerializedParallelCoordinatesView.class);
			startViews.add(SerializedHierarchicalHeatMapView.class);
		}

		// Only show bucket when pathway data is loaded
		if (GeneralManager.get().getPathwayManager().size() > 0) {
			startViews.add(SerializedRemoteRenderingView.class);
		}
	}

	/**
	 * Filter all views except remote and browser in case of pathway viewer mode
	 */
	private static void applyPathwayViewerViewFilter() {
		ArrayList<Class<? extends ASerializedView>> newStartViews =
			new ArrayList<Class<? extends ASerializedView>>();
		for (Class<? extends ASerializedView> view : startViews) {
			if (view.equals(SerializedRemoteRenderingView.class)
				|| view.equals(SerializedHTMLBrowserView.class)) {
				newStartViews.add(view);
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
		// Only load pathways in genetic use case mode
		if (GeneralManager.get().getUseCase(EDataDomain.GENETIC_DATA) != null) {
			// Trigger pathway loading
			new PathwayLoadingProgressIndicatorAction().run(null);
		}
	}

	private void triggerPathwayFetching() {

		// Only fetch pathways if in genetic use case mode
		if (!prefStore.getString(PreferenceConstants.LAST_CHOSEN_USE_CASE_MODE).equals(
			EDataDomain.GENETIC_DATA.name()))
			return;

		String sPathwayDataSources =
			prefStore.getString(PreferenceConstants.LAST_CHOSEN_PATHWAY_DATA_SOURCES);
		StringTokenizer tokenizer = new StringTokenizer(sPathwayDataSources, ";");
		String sLoadedPathwaySources = prefStore.getString(PreferenceConstants.PATHWAY_DATA_OK);
		ArrayList<EPathwayDatabaseType> alFetchPathwaySources = new ArrayList<EPathwayDatabaseType>();
		EOrganism eOrganism =
			EOrganism.valueOf(prefStore.getString(PreferenceConstants.LAST_CHOSEN_ORGANISM));

		// Look if organism and pathway source combination has been already fetched
		while (tokenizer.hasMoreTokens()) {
			EPathwayDatabaseType sTmpPathwayDataSource = EPathwayDatabaseType.valueOf(tokenizer.nextToken());
			if (!sLoadedPathwaySources.contains(eOrganism.name() + "+" + sTmpPathwayDataSource))
				alFetchPathwaySources.add(sTmpPathwayDataSource);
		}

		if (!alFetchPathwaySources.isEmpty()) {
			WizardDialog firstStartWizard =
				new WizardDialog(Display.getCurrent().getActiveShell(), new FetchPathwayWizard(
					alFetchPathwaySources));
			firstStartWizard.open();
		}
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
