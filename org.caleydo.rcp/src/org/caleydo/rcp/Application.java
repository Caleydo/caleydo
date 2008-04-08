package org.caleydo.rcp;

import java.util.Map;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.caleydo.core.application.core.CaleydoBootloader;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.rcp.dialog.file.OpenXmlConfigFileDialog;

/**
 * This class controls all aspects of the application's execution
 */
public class Application 
implements IApplication {

	public static final String debugMsgPrefix = "RCP: ";
	
	// FIXME: should not be static!
	public static IGeneralManager refGeneralManager;	
	
	public static CaleydoBootloader caleydo_core;
	
	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
	 */
	@SuppressWarnings("unchecked")
	public Object start(IApplicationContext context) throws Exception {

		System.out.println("Caleydo_RCP: bootstrapping ...");
		
		String sCaleydoXMLfile = "";		
		Map <String,Object> map = (Map <String,Object>) context.getArguments();
				
		if ( map.size() > 0) 
		{
			String [] info = (String[]) map.get("application.args");
			
			if ( info != null ) 
			{
				if ( info.length > 0) 
				{					
					sCaleydoXMLfile = info[0];
					System.out.println(debugMsgPrefix +"XML config file:" +sCaleydoXMLfile );
					
					if ( info.length > 1 ) {
						System.err.println(debugMsgPrefix + "can not handle more than on argument! ignor other argumets.");
					}
				}
			}
		}
			
		startCaleydoCore(sCaleydoXMLfile);
		
		Display display = PlatformUI.createDisplay();
		
		try {
			int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
			if (returnCode == PlatformUI.RETURN_RESTART)
				return IApplication.EXIT_RESTART;
			else
				return IApplication.EXIT_OK;
		} finally {
			disposeCaleydoCore();
			display.dispose();
			System.out.println(debugMsgPrefix + getClass().getSimpleName() + ".start() ==> display.dispose() ... [done]");
		}
				
	}

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	public void stop() {
		System.out.println(debugMsgPrefix + getClass().getSimpleName() + ".stop() ...");
	
		final IWorkbench workbench = PlatformUI.getWorkbench();
		
		if (workbench == null)
			return;
		final Display display = workbench.getDisplay();
		display.syncExec(new Runnable() {
			public void run() {
				if (!display.isDisposed())
					workbench.close();
			}
		});
	}

	protected void startCaleydoCore( final String xmlFileName ) {
		
		caleydo_core = new CaleydoBootloader();
		
		// If no file is provided as command line argument a XML file open dialog is opened
		if  (xmlFileName=="") 
		{
			Display display = PlatformUI.createDisplay();
		    Shell shell = new Shell(display);
		    shell.setText("Open project file");

			OpenXmlConfigFileDialog openDialog = new OpenXmlConfigFileDialog(shell);
			openDialog.open();
			
			if (caleydo_core.getXmlFileName().isEmpty())
				return;
			
			shell.dispose();
			
			Application.refGeneralManager = caleydo_core.getGeneralManager();
			caleydo_core.run_SWT();
			
			return;
		}
		// Load as command line argument provided XML config file name.
		else
		{
			caleydo_core.setXmlFileName(xmlFileName); 
			Application.refGeneralManager = caleydo_core.getGeneralManager();
			caleydo_core.run_SWT();			
		}
	}
	
	protected void disposeCaleydoCore() {
		
		System.out.println(debugMsgPrefix + getClass().getSimpleName() + ".disposeCaleydoCore() shutdown ...");
		
		if ( caleydo_core != null ) 
		{
			if ( caleydo_core.isRunning() ) 
			{
				caleydo_core.stop();
				caleydo_core = null;
			}
			else 
			{
				System.err.println(debugMsgPrefix + getClass().getSimpleName() + ".disposeCaleydoCore() core was already stopped!");
			}
		}
	}
		
}
