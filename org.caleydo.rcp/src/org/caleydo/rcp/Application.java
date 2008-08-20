package org.caleydo.rcp;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;

import org.caleydo.core.application.core.CaleydoBootloader;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;
import org.caleydo.rcp.progress.PathwayLoadingProgressIndicatorAction;
import org.caleydo.rcp.wizard.firststart.FirstStartWizard;
import org.caleydo.rcp.wizard.project.CaleydoProjectWizard;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
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

	/**
	 * Getter method for the Caleydo general manager. Use this reference to get
	 * access to all specialized managers.
	 */
	public static IGeneralManager generalManager;

	public static ApplicationWorkbenchAdvisor applicationWorkbenchAdvisor;
	
	public static boolean bIsWebstart = false;
	
	public static String sCaleydoXMLfile = "";
	
	public static boolean bDoExit = false;

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
		
		// Create Caleydo core
		caleydoCore = new CaleydoBootloader(bIsWebstart);
		generalManager = caleydoCore.getGeneralManager();
		
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
					generalManager.getLogger().log(Level.INFO, "Save Caleydo preferences...");
					generalManager.getPreferenceStore().setValue("firstStart", false);
					generalManager.getPreferenceStore().save();
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
			shell.setText("Open project file");
			Monitor primary = display.getPrimaryMonitor ();
			Rectangle bounds = primary.getBounds ();
			Rectangle rect = shell.getBounds ();
			int x = bounds.x + (bounds.width - rect.width) / 2;
			int y = bounds.y + (bounds.height - rect.height) / 2;
			shell.setLocation (x, y);
			
			WizardDialog projectWizardDialog = new WizardDialog(shell,
					new CaleydoProjectWizard());
			
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
