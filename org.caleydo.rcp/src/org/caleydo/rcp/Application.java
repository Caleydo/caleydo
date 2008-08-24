package org.caleydo.rcp;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;

import org.caleydo.core.application.core.CaleydoBootloader;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;
import org.caleydo.rcp.core.bridge.RCPBridge;
import org.caleydo.rcp.progress.PathwayLoadingProgressIndicatorAction;
import org.caleydo.rcp.wizard.firststart.FirstStartWizard;
import org.caleydo.rcp.wizard.project.CaleydoProjectWizard;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

/**
 * This class controls all aspects of the application's execution
 */
public class Application
	implements IApplication
{
	public static CaleydoBootloader caleydoCore;

	public static ApplicationWorkbenchAdvisor applicationWorkbenchAdvisor;
	
	public static boolean bIsWebstart = false;
	
	public static String sCaleydoXMLfile = "";
	
	public static boolean bDoExit = false;
	
	public RCPBridge rcpGuiBridge;

	@Override
	@SuppressWarnings("unchecked")
	public Object start(IApplicationContext context) throws Exception
	{
		System.out.println("Caleydo RCP: bootstrapping ...");

		Map<String, Object> map = (Map<String, Object>) context.getArguments();

		if (map.size() > 0)
		{
			String[] sArParam = (String[]) map.get("application.args");

			if (sArParam != null)
			{
				for (int iParamIndex = 0; iParamIndex < sArParam.length; iParamIndex++)
				{
					if (sArParam[iParamIndex].equals("webstart"))
						bIsWebstart = true;
					else 
						sCaleydoXMLfile = sArParam[iParamIndex];		
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
			
			disposeCaleydoCore();
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
			sCaleydoXMLfile = "data/bootstrap/shared/webstart/bootstrap_webstart_test.xml";
			caleydoCore.setXmlFileName(sCaleydoXMLfile);
			caleydoCore.start();
			
			Display display = PlatformUI.createDisplay();
			Shell shell = new Shell(display);
			
			WizardDialog projectWizardDialog = new WizardDialog(shell,
					new CaleydoProjectWizard(shell));
			
			if(WizardDialog.CANCEL == projectWizardDialog.open())
			{
				bDoExit = true;
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
	}

	protected void disposeCaleydoCore()
	{
		if (caleydoCore != null)
		{
			if (caleydoCore.isRunning())
			{
				caleydoCore.stop();
				caleydoCore = null;
			}
		}
	}
}
