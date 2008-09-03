package org.caleydo.rcp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;

import org.caleydo.core.application.core.CaleydoBootloader;
import org.caleydo.core.manager.event.mediator.EMediatorType;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;
import org.caleydo.rcp.core.bridge.RCPBridge;
import org.caleydo.rcp.progress.PathwayLoadingProgressIndicatorAction;
import org.caleydo.rcp.util.info.InfoArea;
import org.caleydo.rcp.views.GLRemoteRenderingView;
import org.caleydo.rcp.wizard.firststart.FirstStartWizard;
import org.caleydo.rcp.wizard.project.CaleydoProjectWizard;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * This class controls all aspects of the application's execution
 */
public class Application
	implements IApplication
{
	private static String BOOTSTRAP_FILE_GENE_EXPRESSION_MODE 
		= "data/bootstrap/shared/webstart/bootstrap_webstart_gene_expression.xml";

	private static String BOOTSTRAP_FILE_PATHWAY_VIEWER_MODE 
		= "data/bootstrap/shared/webstart/bootstrap_webstart_pathway_viewer.xml";

	public static CaleydoBootloader caleydoCore;

	public static ApplicationWorkbenchAdvisor applicationWorkbenchAdvisor;
	
	public static boolean bIsWebstart = false;
	public static boolean bDoExit = false;
	public static boolean bPathwayViewerMode = false;
	
	public static String sCaleydoXMLfile = "";
	
	public static ArrayList<EStartViewsMode> alStartViews;
	
	public RCPBridge rcpGuiBridge;

	@Override
	@SuppressWarnings("unchecked")
	public Object start(IApplicationContext context) throws Exception
	{
		System.out.println("Caleydo RCP: bootstrapping ...");

		alStartViews = new ArrayList<EStartViewsMode>();
		
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
					else if (sArParam[iParamIndex].equals(EStartViewsMode.PARALLEL_COORDINATES.getCommandLineArgument()))
					{
						alStartViews.add(EStartViewsMode.PARALLEL_COORDINATES);
					}
					else if (sArParam[iParamIndex].equals(EStartViewsMode.HEATMAP.getCommandLineArgument()))
					{
						alStartViews.add(EStartViewsMode.HEATMAP);						
					}
					else if (sArParam[iParamIndex].equals(EStartViewsMode.BROWSER.getCommandLineArgument()))
					{
						alStartViews.add(EStartViewsMode.BROWSER);						
					}		
					else if (sArParam[iParamIndex].equals(EStartViewsMode.REMOTE.getCommandLineArgument()))
					{
						alStartViews.add(EStartViewsMode.REMOTE);						
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
		if (caleydoCore.getGeneralManager().getPreferenceStore().getBoolean("firstStart"))
		{
			WizardDialog firstStartWizard = new WizardDialog(display.getActiveShell(), 
					new FirstStartWizard());
			firstStartWizard.open();
		}
		
		if (bIsWebstart && !bDoExit)
		{
			startCaleydoCore();
		}
		
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
					GeneralManager.get().getLogger().log(Level.INFO, "Save Caleydo preferences...");
					GeneralManager.get().getPreferenceStore().setValue("firstStart", false);
					GeneralManager.get().getPreferenceStore().save();
				}
				catch (IOException e)
				{
					throw new CaleydoRuntimeException("Unable to save preference file.", 
							CaleydoRuntimeExceptionType.DATAHANDLING);
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
			
			if(WizardDialog.CANCEL == projectWizardDialog.open())
			{
				bDoExit = true;
			}
			
			shell.dispose();
			
			if (bPathwayViewerMode)	
				sCaleydoXMLfile = BOOTSTRAP_FILE_PATHWAY_VIEWER_MODE;
			else
				sCaleydoXMLfile = BOOTSTRAP_FILE_GENE_EXPRESSION_MODE;
			
			caleydoCore.setXmlFileName(sCaleydoXMLfile);
			caleydoCore.start();
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
		
		openViewsInRCP();
		
		// Register the info area to all mediator from type SELECTION
//		GeneralManager.get().getEventPublisher().registerReceiverToMediatorGroup(
//				EMediatorType.SELECTION_MEDIATOR, this);
		
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		
		// Register the info area to all mediator from type SELECTION
		GeneralManager.get().getEventPublisher().registerReceiverToMediatorGroup(
				EMediatorType.SELECTION_MEDIATOR, InfoArea.getInfoArea());
		
		// Start OpenGL rendering
		GeneralManager.get().getViewGLCanvasManager().startAnimator();
	}
	
	private static void openViewsInRCP() 
	{
		// Open Views in RCP
		try
		{
			ArrayList<EStartViewsMode> alStartViews = Application.alStartViews;
			if (alStartViews.contains(EStartViewsMode.REMOTE))
			{
				PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().showView(GLRemoteRenderingView.ID);
				alStartViews.remove(EStartViewsMode.REMOTE);
			}
			
			for (EStartViewsMode startViewsMode : alStartViews)
			{
					PlatformUI.getWorkbench().getActiveWorkbenchWindow()
						.getActivePage().showView(startViewsMode.getRCPViewID());
			}		
		}
		catch (PartInitException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();			
		}	
	}
}
