package org.caleydo.rcp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

import org.caleydo.core.application.core.CaleydoBootloader;
import org.caleydo.core.manager.event.mediator.EMediatorType;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.preferences.PreferenceConstants;
import org.caleydo.rcp.core.bridge.RCPBridge;
import org.caleydo.rcp.progress.PathwayLoadingProgressIndicatorAction;
import org.caleydo.rcp.util.info.InfoArea;
import org.caleydo.rcp.views.GLRemoteRenderingView;
import org.caleydo.rcp.wizard.firststart.FirstStartWizard;
import org.caleydo.rcp.wizard.project.CaleydoProjectWizard;
import org.caleydo.rcp.wizard.project.DataImportWizard;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * This class controls all aspects of the application's execution
 */
public class Application
	implements IApplication
{
	private static String BOOTSTRAP_FILE_GENE_EXPRESSION_MODE = "data/bootstrap/shared/webstart/bootstrap_webstart_gene_expression.xml";
	private static String BOOTSTRAP_FILE_SAMPLE_DATA_MODE = "data/bootstrap/shared/sample/bootstrap_gene_expression_sample.xml";
	private static String BOOTSTRAP_FILE_PATHWAY_VIEWER_MODE = "data/bootstrap/shared/webstart/bootstrap_webstart_pathway_viewer.xml";

	public static CaleydoBootloader caleydoCore;

	public static ApplicationWorkbenchAdvisor applicationWorkbenchAdvisor;

	public static boolean bIsWebstart = false;
	public static boolean bDoExit = false;
	public static EApplicationMode applicationMode = EApplicationMode.STANDARD;

	public static String sCaleydoXMLfile = "";

	public static ArrayList<EStartViewType> alStartViews;

	public RCPBridge rcpGuiBridge;

	@Override
	@SuppressWarnings("unchecked")
	public Object start(IApplicationContext context) throws Exception
	{
		System.out.println("Caleydo RCP: bootstrapping ...");
		
		alStartViews = new ArrayList<EStartViewType>();

		Map<String, Object> map = (Map<String, Object>) context.getArguments();

		if (map.size() > 0)
		{
			String[] sArParam = (String[]) map.get("application.args");

			if (sArParam != null)
			{
				for (int iParamIndex = 0; iParamIndex < sArParam.length; iParamIndex++)
				{
					if (sArParam[iParamIndex].equals("webstart"))
					{
						bIsWebstart = true;
					}
					else if (sArParam[iParamIndex].equals(EStartViewType.PARALLEL_COORDINATES
							.getCommandLineArgument()))
					{
						alStartViews.add(EStartViewType.PARALLEL_COORDINATES);
					}
					else if (sArParam[iParamIndex].equals(EStartViewType.HEATMAP
							.getCommandLineArgument()))
					{
						alStartViews.add(EStartViewType.HEATMAP);
					}
					else if (sArParam[iParamIndex].equals(EStartViewType.BROWSER
							.getCommandLineArgument()))
					{
						alStartViews.add(EStartViewType.BROWSER);
					}
					else if (sArParam[iParamIndex].equals(EStartViewType.REMOTE
							.getCommandLineArgument()))
					{
						alStartViews.add(EStartViewType.REMOTE);
					}
					else
					{
						sCaleydoXMLfile = sArParam[iParamIndex];
					}
				}
			}
		}

		rcpGuiBridge = new RCPBridge();

		// Create Caleydo core
		caleydoCore = new CaleydoBootloader(bIsWebstart, rcpGuiBridge);

		Display display = PlatformUI.createDisplay();

		// Check if Caleydo will be started the first time
		if (!caleydoCore.getGeneralManager().getPreferenceStore().getBoolean(
				PreferenceConstants.PATHWAY_DATA_OK))
		{
			WizardDialog firstStartWizard = new WizardDialog(display.getActiveShell(),
					new FirstStartWizard());
			firstStartWizard.open();
		}

//		if (bIsWebstart && !bDoExit)
//		{
//			startCaleydoCore();
//		}

		try
		{
			applicationWorkbenchAdvisor = new ApplicationWorkbenchAdvisor();

			int returnCode = PlatformUI.createAndRunWorkbench(display,
					applicationWorkbenchAdvisor);

			if (returnCode == PlatformUI.RETURN_RESTART)
			{
				return IApplication.EXIT_RESTART;
			}
			else
				return IApplication.EXIT_OK;
		}
		finally
		{
			if (!bDoExit)
			{
				// Save preferences before shutdown
				try
				{
					GeneralManager.get().getLogger().log(Level.INFO,
							"Save Caleydo preferences...");
					GeneralManager.get().getPreferenceStore().setValue("firstStart", false);
					GeneralManager.get().getPreferenceStore().save();
				}
				catch (IOException e)
				{
					throw new IllegalStateException("Unable to save preference file.");
				}
			}

			GeneralManager.get().getLogger().log(Level.INFO, "Bye bye!");
			display.dispose();
		}
	}

	@Override
	public void stop()
	{
		final IWorkbench workbench = PlatformUI.getWorkbench();

		if (workbench == null)
			return;

		final Display display = workbench.getDisplay();
		display.syncExec(new Runnable()
		{
			public void run()
			{
				if (!display.isDisposed())
				{
					workbench.close();
				}
			}
		});
	}

	public static void startCaleydoCore()
	{
		// If no file is provided as command line argument a XML file open
		// dialog is opened
		if (sCaleydoXMLfile.equals(""))
		{
			Display display = PlatformUI.createDisplay();
			Shell shell = new Shell(display);

			WizardDialog projectWizardDialog = new WizardDialog(shell,
					new CaleydoProjectWizard(shell));

			if (WizardDialog.CANCEL == projectWizardDialog.open())
			{
				bDoExit = true;
				return;
			}

			switch (applicationMode)
			{
				case PATHWAY_VIEWER:
					sCaleydoXMLfile = BOOTSTRAP_FILE_PATHWAY_VIEWER_MODE;
					break;
				case SAMPLE_DATA:
					sCaleydoXMLfile = BOOTSTRAP_FILE_SAMPLE_DATA_MODE;
					break;
				default:
					sCaleydoXMLfile = BOOTSTRAP_FILE_GENE_EXPRESSION_MODE;

			}
			caleydoCore.setXmlFileName(sCaleydoXMLfile);
			caleydoCore.start();

			if(applicationMode == EApplicationMode.STANDARD)
			{
				WizardDialog dataImportWizard = new WizardDialog(shell,
						new DataImportWizard(shell));

				if (WizardDialog.CANCEL == dataImportWizard.open())
				{
					bDoExit = true;
				}
			}
			
			shell.dispose();
		}
		else
		{
			caleydoCore.setXmlFileName(sCaleydoXMLfile);
			caleydoCore.start();
		}

		if (!bDoExit)
		{
			// Trigger pathway loading
			new PathwayLoadingProgressIndicatorAction().run(null);
		}

		initializeColorMapping();
		// initializeViewSettings();

		openViewsInRCP();

		// Register the info area to all mediator from type SELECTION
		GeneralManager.get().getEventPublisher().registerReceiverToMediatorGroup(
				EMediatorType.SELECTION_MEDIATOR, InfoArea.getInfoArea());
		
		if (GeneralManager.get().isStandalone())
		{
			// Start OpenGL rendering
			GeneralManager.get().getViewGLCanvasManager().startAnimator();

			GeneralManager.get().getSWTGUIManager().runApplication();
		}
	}

	public static void initializeColorMapping()
	{
		// The next two lines are a hack FIXME which need to be replaces once we
		// can call initializeDefaultPreferences() in PreferenceIntializer
		// ourselves
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.getInt("");

		ColorMappingManager.get().initiFromPreferenceStore();
//		store = GeneralManager.get().getPreferenceStore();
//		int iNumberOfMarkerPoints = store
//				.getInt(PreferenceConstants.NUMBER_OF_COLOR_MARKER_POINTS);
//
//		ArrayList<ColorMarkerPoint> alMarkerPoints = new ArrayList<ColorMarkerPoint>();
//		for (int iCount = 1; iCount <= iNumberOfMarkerPoints; iCount++)
//		{
//			float colorMarkerValue = store
//					.getFloat(PreferenceConstants.COLOR_MARKER_POINT_VALUE + iCount);
//			String color = store.getString(PreferenceConstants.COLOR_MARKER_POINT_COLOR
//					+ iCount);
//			
//			alMarkerPoints.add(new ColorMarkerPoint(colorMarkerValue, ConversionTools.getColorFromString(color)));
//		}
//
//		// TODO not generic
//		ColorMappingManager.get().initColorMapping(EColorMappingType.GENE_EXPRESSION,
//				alMarkerPoints);
//
//		for (AGLEventListener view : GeneralManager.get().getViewGLCanvasManager()
//				.getAllGLEventListeners())
//		{
//			view.setDisplayListDirty();
//		}

	}

	private static void openViewsInRCP()
	{
		if (applicationMode == EApplicationMode.PATHWAY_VIEWER)
		{
			// Filter all views except remote and browser in case of pathway
			// viewer mode
			Iterator<EStartViewType> iterStartViewsType = alStartViews.iterator();
			EStartViewType type;
			while (iterStartViewsType.hasNext())
			{
				type = iterStartViewsType.next();
				if (type != EStartViewType.REMOTE && type != EStartViewType.BROWSER)
				{
					iterStartViewsType.remove();
				}
			}
		}

		// Open Views in RCP
		try
		{
			if (alStartViews.contains(EStartViewType.REMOTE))
			{
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(
						GLRemoteRenderingView.ID);
				alStartViews.remove(EStartViewType.REMOTE);
			}

			for (EStartViewType startViewsMode : alStartViews)
			{
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(
						startViewsMode.getRCPViewID());
			}
		}
		catch (PartInitException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
