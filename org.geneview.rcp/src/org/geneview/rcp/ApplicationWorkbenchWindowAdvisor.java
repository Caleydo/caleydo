package org.geneview.rcp;

//import java.net.MalformedURLException;
//import java.net.URL;

//import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
//import org.eclipse.swt.widgets.Shell;
//import org.eclipse.ui.PartInitException;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
//import org.eclipse.ui.browser.IWebBrowser;
//import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

    public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        super(configurer);
    }

    public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
        return new ApplicationActionBarAdvisor(configurer);
    }
    
    public void preWindowOpen() {
        IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
        configurer.setInitialSize(new Point(400, 300));
        configurer.setShowCoolBar(false);
        configurer.setShowStatusLine(false);    
        
//      try {
//    	IWorkbenchBrowserSupport browserSupport = configurer.getWorkbenchConfigurer().getWorkbench().getBrowserSupport();
//    	System.out.println("Browser support: " +browserSupport.isInternalWebBrowserAvailable());
//    	
//    	IWebBrowser browser = browserSupport.createBrowser(
//    			IWorkbenchBrowserSupport.AS_EDITOR | IWorkbenchBrowserSupport.PERSISTENT, "browser100", "browser", "GeneView web browser");
//    	browser.openURL(new URL("http://www.google.at"));
//
//    } catch (PartInitException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	} catch (MalformedURLException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
    } 
    
//    /*
//     * (non-Javadoc)
//     * @see org.eclipse.ui.application.WorkbenchWindowAdvisor#createWindowContents(org.eclipse.swt.widgets.Shell)
//     */
//    public void createWindowContents(Shell shell) { 
//    	
//    	super.createWindowContents(shell); 
//        shell.setMaximized(true);
//    }
}
