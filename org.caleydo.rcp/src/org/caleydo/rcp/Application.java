package org.caleydo.rcp;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.caleydo.core.application.core.CaleydoBootloader;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.specialized.genetic.GeneticUseCase;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.mapping.color.EColorMappingType;
import org.caleydo.core.util.preferences.PreferenceConstants;
import org.caleydo.rcp.core.bridge.RCPBridge;
import org.caleydo.rcp.progress.PathwayLoadingProgressIndicatorAction;
import org.caleydo.rcp.view.opengl.GLHistogramView;
import org.caleydo.rcp.wizard.firststart.FetchPathwayWizard;
import org.caleydo.rcp.wizard.firststart.InternetConfigurationWizard;
import org.caleydo.rcp.wizard.firststart.ProxyConfigurationPage;
import org.caleydo.rcp.wizard.project.CaleydoProjectWizard;
import org.caleydo.rcp.wizard.project.DataImportWizard;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Constants;

/**
 * This class controls all aspects of the application's execution
 */
public class Application
	implements IApplication {
	private static String BOOTSTRAP_FILE_GENE_EXPRESSION_MODE =
		"data/bootstrap/shared/webstart/bootstrap_webstart_gene_expression.xml";
	private static String BOOTSTRAP_FILE_SAMPLE_DATA_MODE =
		"data/bootstrap/shared/sample/bootstrap_gene_expression_sample.xml";
	private static String BOOTSTRAP_FILE_PATHWAY_VIEWER_MODE =
		"data/bootstrap/shared/webstart/bootstrap_webstart_pathway_viewer.xml";

	private static String REAL_DATA_SAMPLE_FILE =
		"data/genome/microarray/sample/HCC_sample_dataset_4630_24_cluster.csv";

	public static CaleydoBootloader caleydoCore;

	public static ApplicationWorkbenchAdvisor applicationWorkbenchAdvisor;

	public static boolean bIsWebstart = false;
	public static boolean bDoExit = false;

	// The command line arguments overrules the preference store
	public static boolean bLoadPathwayData = true;
	public static boolean bOverrulePrefStoreLoadPathwayData = false;
	public static boolean bIsWindowsOS = false;
	public static boolean bIsInterentConnectionOK = false;

	public static EApplicationMode applicationMode = EApplicationMode.STANDARD;

	public static String sCaleydoXMLfile = "";

	public static ArrayList<EStartViewType> alStartViews;

	public RCPBridge rcpGuiBridge;

	@Override
	@SuppressWarnings("unchecked")
	public Object start(IApplicationContext context) throws Exception {
		// System.out.println("Start Caleydo...");
		// System.out.println("OS Name:" +System.getProperty("os.name"));

		if (System.getProperty("os.name").contains("Win")) {
			bIsWindowsOS = true;
		}

		alStartViews = new ArrayList<EStartViewType>();

		Map<String, Object> map = (Map<String, Object>) context.getArguments();

		if (map.size() > 0) {
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
						alStartViews.add(EStartViewType.PARALLEL_COORDINATES);
					}
					else if (element.equals(EStartViewType.HEATMAP.getCommandLineArgument())) {
						alStartViews.add(EStartViewType.HEATMAP);
					}
					else if (element.equals(EStartViewType.GLYPHVIEW.getCommandLineArgument())) {
						alStartViews.add(EStartViewType.GLYPHVIEW);
					}
					else if (element.equals(EStartViewType.BROWSER.getCommandLineArgument())) {
						alStartViews.add(EStartViewType.BROWSER);
					}
					else if (element.equals(EStartViewType.REMOTE.getCommandLineArgument())) {
						alStartViews.add(EStartViewType.REMOTE);
					}
					else if (element.equals(EStartViewType.TABULAR.getCommandLineArgument())) {
						alStartViews.add(EStartViewType.TABULAR);
					}
					else if (element.equals(EStartViewType.RADIAL_HIERARCHY.getCommandLineArgument())) {
						alStartViews.add(EStartViewType.RADIAL_HIERARCHY);
					}
					else if (element.equals(EStartViewType.HYPERBOLIC.getCommandLineArgument())) {
						alStartViews.add(EStartViewType.HYPERBOLIC);
					}
					else if (element.equals(EStartViewType.HISTOGRAM.getCommandLineArgument())) {
						alStartViews.add(EStartViewType.HISTOGRAM);
					}
					else if (element.equals(EStartViewType.DENDROGRAM_HORIZONTAL.getCommandLineArgument())) {
						alStartViews.add(EStartViewType.DENDROGRAM_HORIZONTAL);
					}
					else if (element.equals(EStartViewType.DENDROGRAM_VERTICAL.getCommandLineArgument())) {
						alStartViews.add(EStartViewType.DENDROGRAM_VERTICAL);
					}
					else {
						sCaleydoXMLfile = element;
					}
				}
			}
		}

		rcpGuiBridge = new RCPBridge();

		// Create Caleydo core
		caleydoCore =
			new CaleydoBootloader(bIsWebstart, rcpGuiBridge);

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
			// shell.setActive();
			// shell.setFocus();

			WizardDialog projectWizardDialog = new WizardDialog(shell, new CaleydoProjectWizard(shell));

			if (Window.CANCEL == projectWizardDialog.open()) {
				shutDown();
			}

			if (sCaleydoXMLfile.equals("")) {
				switch (applicationMode) {
					case PATHWAY_VIEWER:
						sCaleydoXMLfile = BOOTSTRAP_FILE_PATHWAY_VIEWER_MODE;
						break;
					case SAMPLE_DATA_RANDOM:
						sCaleydoXMLfile = BOOTSTRAP_FILE_SAMPLE_DATA_MODE;
						break;
					case SAMPLE_DATA_REAL:
					case STANDARD:
						sCaleydoXMLfile = BOOTSTRAP_FILE_GENE_EXPRESSION_MODE;
						break;
					default:
						// do nothing
				}
			}
		}
		else {
			// Assuming that if an external XML file is provided, the genetic use case applies
			GeneralManager.get().setUseCase(new GeneticUseCase());
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

		try {
			applicationWorkbenchAdvisor = new ApplicationWorkbenchAdvisor();

			int returnCode = PlatformUI.createAndRunWorkbench(display, applicationWorkbenchAdvisor);

			GeneralManager.get().getPreferenceStore().setValue("firstStart", false);

			if (returnCode == PlatformUI.RETURN_RESTART)
				return IApplication.EXIT_RESTART;
			else
				return IApplication.EXIT_OK;
		}
		finally {
			if (!bDoExit) {
				shutDown();
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

		if (applicationMode == EApplicationMode.SAMPLE_DATA_REAL) {

			WizardDialog dataImportWizard =
				new WizardDialog(shell, new DataImportWizard(shell, REAL_DATA_SAMPLE_FILE));

			if (Window.CANCEL == dataImportWizard.open()) {
				bDoExit = true;
			}
		}
		else if (applicationMode == EApplicationMode.STANDARD
			&& (sCaleydoXMLfile.equals(BOOTSTRAP_FILE_GENE_EXPRESSION_MODE) || sCaleydoXMLfile.equals(""))) {

			WizardDialog dataImportWizard = new WizardDialog(shell, new DataImportWizard(shell));

			if (Window.CANCEL == dataImportWizard.open()) {
				bDoExit = true;
			}
		}

		initializeColorMapping();

		openRCPViews();

		if (!bDoExit && bLoadPathwayData) {
			// Trigger pathway loading
			new PathwayLoadingProgressIndicatorAction().run(null);

			// ((ToolBarView)PlatformUI.getWorkbench().getActiveWorkbenchWindow()
			// .getActivePage().getViewReferences()[0].getView(false)).addPathwayLoadingProgress();
		}

		if (GeneralManager.get().isStandalone()) {
			// Start OpenGL rendering
			GeneralManager.get().getViewGLCanvasManager().startAnimator();
			GeneralManager.get().getSWTGUIManager().runApplication();
		}
	}

	public static void initializeColorMapping() {
		// The next two lines are a hack FIXME which need to be replaces once we
		// can call initializeDefaultPreferences() in PreferenceIntializer
		// ourselves
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.getInt("");

		ColorMappingManager.get().initiFromPreferenceStore(EColorMappingType.GENE_EXPRESSION);
	}

	private static void openRCPViews() {
		// Create view list dynamically when not specified via the command line
		if (alStartViews.isEmpty()) {

			alStartViews.add(EStartViewType.BROWSER);

			if (applicationMode != EApplicationMode.PATHWAY_VIEWER) {
				// alStartViews.add(EStartViewType.TABULAR);
				alStartViews.add(EStartViewType.PARALLEL_COORDINATES);
				alStartViews.add(EStartViewType.HEATMAP);
			}

			// Only show bucket when pathway data is loaded
			if (bLoadPathwayData) {
				alStartViews.add(EStartViewType.REMOTE);
			}
		}
		else {
			if (applicationMode == EApplicationMode.PATHWAY_VIEWER) {
				// Filter all views except remote and browser in case of pathway
				// viewer mode
				Iterator<EStartViewType> iterStartViewsType = alStartViews.iterator();
				EStartViewType type;
				while (iterStartViewsType.hasNext()) {
					type = iterStartViewsType.next();
					if (type != EStartViewType.REMOTE && type != EStartViewType.BROWSER) {
						iterStartViewsType.remove();
					}
				}
			}
		}

		// Open Views in RCP
		try {
			// Open toolbar view
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(GLHistogramView.ID);

			// if (alStartViews.contains(EStartViewType.REMOTE))
			// {
			// PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(
			// GLRemoteRenderingView.ID);
			//
			// alStartViews.remove(EStartViewType.REMOTE);
			// }

			for (EStartViewType startViewsMode : alStartViews) {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(
					startViewsMode.getRCPViewID());
			}
		}
		catch (PartInitException e) {
			e.printStackTrace();
		}
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
}
