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
import org.caleydo.core.data.selection.VirtualArray;
import org.caleydo.core.manager.IUseCase;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.specialized.genetic.GeneticUseCase;
import org.caleydo.core.manager.usecase.AUseCase;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.serialize.ApplicationInitData;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.mapping.color.EColorMappingType;
import org.caleydo.core.util.preferences.PreferenceConstants;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.SerializedGlyphView;
import org.caleydo.core.view.opengl.canvas.histogram.SerializedHistogramView;
import org.caleydo.core.view.opengl.canvas.hyperbolic.SerializedHyperbolicView;
import org.caleydo.core.view.opengl.canvas.radial.SerializedRadialHierarchyView;
import org.caleydo.core.view.opengl.canvas.remote.SerializedRemoteRenderingView;
import org.caleydo.core.view.opengl.canvas.storagebased.EVAType;
import org.caleydo.core.view.opengl.canvas.storagebased.SerializedDendogramHorizontalView;
import org.caleydo.core.view.opengl.canvas.storagebased.SerializedDendogramVerticalView;
import org.caleydo.core.view.opengl.canvas.storagebased.SerializedHeatMapView;
import org.caleydo.core.view.opengl.canvas.storagebased.SerializedParallelCoordinatesView;
import org.caleydo.core.view.swt.browser.SerializedHTMLBrowserView;
import org.caleydo.core.view.swt.tabular.SerializedTabularDataView;
import org.caleydo.rcp.core.bridge.RCPBridge;
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
import org.eclipse.jface.preference.IPreferenceStore;
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

	private static String BOOTSTRAP_FILE_GENE_EXPRESSION_MODE =
		"data/bootstrap/shared/webstart/bootstrap_webstart_gene_expression.xml";

	private static String BOOTSTRAP_FILE_SAMPLE_DATA_MODE =
		"data/bootstrap/shared/sample/bootstrap_gene_expression_sample.xml";
	
	@SuppressWarnings("unused")
	private static String BOOTSTRAP_FILE_PATHWAY_VIEWER_MODE =
		"data/bootstrap/shared/webstart/bootstrap_webstart_pathway_viewer.xml";

	private static String REAL_DATA_SAMPLE_FILE =
		"data/genome/microarray/sample/HCC_sample_dataset_4630_24_cluster.csv";

	public static CaleydoBootloader caleydoCore;

	public static ApplicationWorkbenchAdvisor applicationWorkbenchAdvisor;

	public static boolean bIsWebstart = false;
	public static boolean bDoExit = false;
	public static boolean isStartedFromXML = false;

	// The command line arguments overrules the preference store
	public static boolean bLoadPathwayData = true;
	public static boolean bOverrulePrefStoreLoadPathwayData = false;
	public static boolean bIsWindowsOS = false;
	public static boolean bIsInterentConnectionOK = false;
	public static boolean bDeleteRestoredWorkbenchState = false;

	/** startup information of the application */
	public static EApplicationMode applicationMode = EApplicationMode.GENE_EXPRESSION_NEW_DATA;

	public static String sCaleydoXMLfile = "";

	public static List<ASerializedView> startViews;

	/** initialization data received from a caleydo-server-application during startup */ 
	public static ApplicationInitData initData = null;
	
	public RCPBridge rcpGuiBridge;

	@Override
	@SuppressWarnings("unchecked")
	public Object start(IApplicationContext context) throws Exception {
		// System.out.println("Start Caleydo...");
		// System.out.println("OS Name:" +System.getProperty("os.name"));

		if (System.getProperty("os.name").contains("Win")) {
			bIsWindowsOS = true;
		}

		startViews = new ArrayList<ASerializedView>();

		Map<String, Object> map = (Map<String, Object>) context.getArguments();
		parseApplicationArguments(map);

		rcpGuiBridge = new RCPBridge();

		// Create Caleydo core
		caleydoCore = new CaleydoBootloader(rcpGuiBridge);

		Display display = PlatformUI.createDisplay();
		Shell shell = new Shell(display);

		// Check if Caleydo will be started the first time and no internet connection is detected
		if (caleydoCore.getGeneralManager().getPreferenceStore().getBoolean(PreferenceConstants.FIRST_START)
			&& !isInternetConnectionOK()) {
			WizardDialog internetConfigurationWizard =
				new WizardDialog(shell, new InternetConfigurationWizard());
			internetConfigurationWizard.open();
		}

		// If no file is provided as command line argument a XML file open
		// dialog is opened
		if (sCaleydoXMLfile.equals("")) {

			WizardDialog projectWizardDialog = new WizardDialog(shell, new CaleydoProjectWizard(shell));

			if (Window.CANCEL == projectWizardDialog.open()) {
				shutDown();
			}

			if (sCaleydoXMLfile.equals("")) {
				IUseCase useCase = GeneralManager.get().getUseCase();
				switch (applicationMode) {
//					case GENE_EXPRESSION_PATHWAY_VIEWER:
//						sCaleydoXMLfile = BOOTSTRAP_FILE_PATHWAY_VIEWER_MODE;
//						break;
					case GENE_EXPRESSION_SAMPLE_DATA_RANDOM:
						sCaleydoXMLfile = BOOTSTRAP_FILE_SAMPLE_DATA_MODE;
						useCase.setBootsTrapFileName(sCaleydoXMLfile);
						break;
					case GENE_EXPRESSION_SAMPLE_DATA_REAL:
					case GENE_EXPRESSION_NEW_DATA:
						sCaleydoXMLfile = BOOTSTRAP_FILE_GENE_EXPRESSION_MODE;
						useCase.setBootsTrapFileName(sCaleydoXMLfile);
						break;
					case UNSPECIFIED_NEW_DATA:
						// not necessary to load any mapping or XML files
						sCaleydoXMLfile = "";
						useCase.setBootsTrapFileName(sCaleydoXMLfile);
						break;
					case LOAD_PROJECT:
					case COLLABORATION_CLIENT:
						sCaleydoXMLfile = GeneralManager.get().getUseCase().getBootsTrapFileName();
						break;
						
					default:
						throw new IllegalStateException("Unknown application mode " + applicationMode);
				}
			}
		} else {
			// Assuming that if an external XML file is provided, the genetic use case applies
			IUseCase useCase = new GeneticUseCase();
			useCase.setBootsTrapFileName(sCaleydoXMLfile);
			GeneralManager.get().setUseCase(useCase);
			isStartedFromXML = true;
		}

		if (!caleydoCore.getGeneralManager().getPreferenceStore().getBoolean(
			PreferenceConstants.PATHWAY_DATA_OK)
			&& bLoadPathwayData)
		// && !caleydoCore.getGeneralManager().getPreferenceStore().getBoolean(
		// PreferenceConstants.FIRST_START))
		{
			WizardDialog firstStartWizard = new WizardDialog(shell, new FetchPathwayWizard());
			firstStartWizard.open();
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
			} else {
				return IApplication.EXIT_OK;
			}
		} finally {
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
					bLoadPathwayData = false;
					bOverrulePrefStoreLoadPathwayData = true;
				}
				else if (element.equals("load_pathways")) {
					bLoadPathwayData = true;
					bOverrulePrefStoreLoadPathwayData = true;
				}
				else if (element.equals(EStartViewType.PARALLEL_COORDINATES.getCommandLineArgument())) {
					startViews.add(new SerializedParallelCoordinatesView());
				}
				else if (element.equals(EStartViewType.HEATMAP.getCommandLineArgument())) {
					startViews.add(new SerializedHeatMapView());
				}
				else if (element.equals(EStartViewType.GLYPHVIEW.getCommandLineArgument())) {
					startViews.add(new SerializedGlyphView());
				}
				else if (element.equals(EStartViewType.BROWSER.getCommandLineArgument())) {
					startViews.add(new SerializedHTMLBrowserView());
				}
				else if (element.equals(EStartViewType.REMOTE.getCommandLineArgument())) {
					startViews.add(new SerializedRemoteRenderingView());
				}
				else if (element.equals(EStartViewType.TABULAR.getCommandLineArgument())) {
					startViews.add(new SerializedTabularDataView());
				}
				else if (element.equals(EStartViewType.RADIAL_HIERARCHY.getCommandLineArgument())) {
					startViews.add(new SerializedRadialHierarchyView());
				}
				else if (element.equals(EStartViewType.HYPERBOLIC.getCommandLineArgument())) {
					startViews.add(new SerializedHyperbolicView());
				}
				else if (element.equals(EStartViewType.HISTOGRAM.getCommandLineArgument())) {
					startViews.add(new SerializedHistogramView());
				}
				else if (element.equals(EStartViewType.DENDROGRAM_HORIZONTAL.getCommandLineArgument())) {
					startViews.add(new SerializedDendogramHorizontalView());
				}
				else if (element.equals(EStartViewType.DENDROGRAM_VERTICAL.getCommandLineArgument())) {
					startViews.add(new SerializedDendogramVerticalView());
				}
				else {
					sCaleydoXMLfile = element;
				}
			}
		}
	}
	
	private void shutDown() {
		// Save preferences before shutdown
		try {
			GeneralManager.get().getLogger().log(
				new Status(Status.WARNING, Activator.PLUGIN_ID, "Save Caleydo preferences..."));
			GeneralManager.get().getPreferenceStore().save();
		}
		catch (IOException e) {
			throw new IllegalStateException("Unable to save preference file.");
		}

		GeneralManager.get().getViewGLCanvasManager().stopAnimator();

		GeneralManager.get().getLogger().log(new Status(Status.INFO, Activator.PLUGIN_ID, "Bye bye!"));
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

		caleydoCore.setXmlFileName(sCaleydoXMLfile);
		caleydoCore.start();

		Shell shell = new Shell();

		if (applicationMode == EApplicationMode.COLLABORATION_CLIENT) {
			AUseCase useCase = (AUseCase) initData.getUseCase();
			SetUtils.saveSetFile(useCase.getLoadDataParameters(), initData.getSetFileContent());
			GeneralManager.get().setUseCase(useCase);

			LoadDataParameters loadDataParameters = useCase.getLoadDataParameters();
			SetUtils.createStorages(loadDataParameters);
			SetUtils.createData(useCase);
			
			HashMap<EVAType, VirtualArray> virtualArrayMap = initData.getVirtualArrayMap();
			for (Entry<EVAType, VirtualArray> entry : virtualArrayMap.entrySet()) {
				useCase.setVirtualArray(entry.getKey(), entry.getValue());
			}
			Application.initData = null;
			// TODO remove temporary set file on shutdown
		} else if (applicationMode == EApplicationMode.LOAD_PROJECT) {
			System.out.println("Load Project");
			AUseCase useCase = (AUseCase) initData.getUseCase();
			GeneralManager.get().setUseCase(useCase);

			LoadDataParameters loadDataParameters = useCase.getLoadDataParameters();
			SetUtils.createStorages(loadDataParameters);
			SetUtils.createData(useCase);

			HashMap<EVAType, VirtualArray> virtualArrayMap = initData.getVirtualArrayMap();
			for (Entry<EVAType, VirtualArray> entry : virtualArrayMap.entrySet()) {
				useCase.setVirtualArray(entry.getKey(), entry.getValue());
			}
			Application.initData = null;
		} else if (applicationMode == EApplicationMode.GENE_EXPRESSION_SAMPLE_DATA_REAL) {

			WizardDialog dataImportWizard =
				new WizardDialog(shell, new DataImportWizard(shell, REAL_DATA_SAMPLE_FILE));

			if (Window.CANCEL == dataImportWizard.open()) {
				bDoExit = true;
			}
		} else if ((applicationMode == EApplicationMode.GENE_EXPRESSION_NEW_DATA || applicationMode == EApplicationMode.UNSPECIFIED_NEW_DATA)
			&& (sCaleydoXMLfile.equals(BOOTSTRAP_FILE_GENE_EXPRESSION_MODE) || sCaleydoXMLfile.equals(""))) {

			WizardDialog dataImportWizard = new WizardDialog(shell, new DataImportWizard(shell));

			if (Window.CANCEL == dataImportWizard.open()) {
				bDoExit = true;
			}
		}

		// TODO - this initializes the VA after the data is written correctly in the set - probably not the
		// nicest place to do this. 
		// This is only necessary if started from xml. Otherwise this is done in FileLoadDataAction
		if (isStartedFromXML)
			GeneralManager.get().getUseCase().updateSetInViews();

		initializeColorMapping();

		// if (GeneralManager.get().isStandalone()) {
		// // Start OpenGL rendering
		// GeneralManager.get().getViewGLCanvasManager().startAnimator();
		// GeneralManager.get().getSWTGUIManager().runApplication();
		// }
	}

	public static void initializeColorMapping() {
		// The next two lines are a hack FIXME which need to be replaces once we
		// can call initializeDefaultPreferences() in PreferenceIntializer
		// ourselves
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.getInt("");

		ColorMappingManager.get().initiFromPreferenceStore(EColorMappingType.GENE_EXPRESSION);
	}

	public static void openRCPViews(IFolderLayout layout) {

		// Create RCP view manager
		RCPViewManager.get();

		// Create view list dynamically when not specified via the command line
		IUseCase usecase = GeneralManager.get().getUseCase();
		if (startViews.isEmpty()) {
			addDefaultStartViews(usecase);
		} else {
			if (usecase instanceof GeneticUseCase && ((GeneticUseCase) usecase).isPathwayViewerMode()) {
				applyPathwayViewerViewFilter();
			}
		}

		for (ASerializedView startView : startViews) {
			layout.addView(startView.getViewGUIID());
		}
	}

	/**
	 * Adds the default start views. Used when no start views are defined with command line arguments.
	 * @param useCase {@link IUseCase} to determine the correct default start views.
	 */
	private static void addDefaultStartViews(IUseCase useCase) {
		startViews.add(new SerializedHTMLBrowserView());
		
		if (useCase instanceof GeneticUseCase && !((GeneticUseCase) useCase).isPathwayViewerMode()) {
			// alStartViews.add(EStartViewType.TABULAR);
			startViews.add(new SerializedParallelCoordinatesView());
			startViews.add(new SerializedHeatMapView());
		}
		
		// Only show bucket when pathway data is loaded
		if (bLoadPathwayData) {
			startViews.add(new SerializedRemoteRenderingView());
		}
	}
	
	/**
	 * Filter all views except remote and browser in case of pathway viewer mode
	 */
	private static void applyPathwayViewerViewFilter() { 
		ArrayList<ASerializedView> newStartViews = new ArrayList<ASerializedView>();
		for (ASerializedView view : startViews) {
			if (view instanceof SerializedRemoteRenderingView || view instanceof SerializedHTMLBrowserView) {
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
