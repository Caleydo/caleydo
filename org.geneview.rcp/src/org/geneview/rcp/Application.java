package org.geneview.rcp;

import java.util.Map;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.geneview.core.application.core.GeneViewBootloader;
import org.geneview.core.manager.IGeneralManager;

/**
 * This class controls all aspects of the application's execution
 */
public class Application implements IApplication {

	public static final String debugMsgPrefix = "RCP: ";
	
	// FIXME: should not be static!
	public static IGeneralManager refGeneralManager;	
	
	public static GeneViewBootloader geneview_core;
	
	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
	 */
	@SuppressWarnings("unchecked")
	public Object start(IApplicationContext context) throws Exception {

		System.out.println("GeneView_RCP: bootstrapping ...");
		
		String sGeneviewXMLfile = "";		
		Map <String,Object> map = (Map <String,Object>) context.getArguments();
				
		if ( map.size() > 0) 
		{
			String [] info = (String[]) map.get("application.args");
			
			if ( info != null ) 
			{
				if ( info.length > 0) 
				{					
					sGeneviewXMLfile = info[0];
					System.out.println(debugMsgPrefix +"XML config file:" +sGeneviewXMLfile );
					
					if ( info.length > 1 ) {
						System.err.println(debugMsgPrefix + "can not handle more than on argument! ignor other argumets.");
					}
				}
			}
		}
			
		startGeneViewCore(sGeneviewXMLfile);
		
		Display display = PlatformUI.createDisplay();
		
		try {
			int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
			if (returnCode == PlatformUI.RETURN_RESTART)
				return IApplication.EXIT_RESTART;
			else
				return IApplication.EXIT_OK;
		} finally {
			disposeGeneViewCore();
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

	protected void startGeneViewCore( final String xmlFileName ) {
		
		geneview_core = new GeneViewBootloader();
			
		if  (xmlFileName=="") 
		{
			geneview_core.setXmlFileName(
				"data/bootstrap/rcp/lbi/bootstrap_sample_LBI.xml"); 	
		}
		else
		{
			geneview_core.setXmlFileName(xmlFileName); 
		}

		Application.refGeneralManager = geneview_core.getGeneralManager();

		geneview_core.run_SWT();
	}
	
	protected void disposeGeneViewCore() {
		
		System.out.println(debugMsgPrefix + getClass().getSimpleName() + ".disposeGeneViewCore() shutdown ...");
		
		if ( geneview_core != null ) 
		{
			if ( geneview_core.isRunning() ) 
			{
				geneview_core.stop();
				geneview_core = null;
			}
			else 
			{
				System.err.println(debugMsgPrefix + getClass().getSimpleName() + ".disposeGeneViewCore() core was already stopped!");
			}
		}
	}
		
}
