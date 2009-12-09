package test;

import com.sun.star.beans.PropertyValue;
import com.sun.star.bridge.XUnoUrlResolver;
import com.sun.star.frame.FrameSearchFlag;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.frame.XModel;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.table.XCell;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import com.sun.star.uno.XInterface;

import daemon.OfficeCalcApplication;


public class TestOffice {
	public static void main(String argv[]) throws Exception {
		OfficeCalcApplication oca = new OfficeCalcApplication();
		oca.init();
	}
	
	public void testOffice() throws Exception {
		  XComponentContext xLocalContext =
		      com.sun.star.comp.helper.Bootstrap.createInitialComponentContext(null);
		 
		  // initial serviceManager
		  XMultiComponentFactory xLocalServiceManager = xLocalContext.getServiceManager();
		 
		  // create a URL resolver
		  Object urlResolver = xLocalServiceManager.createInstanceWithContext(
		      "com.sun.star.bridge.UnoUrlResolver", xLocalContext);
		 
		  // query for the XUnoUrlResolver interface
		  XUnoUrlResolver xUrlResolver = (XUnoUrlResolver) UnoRuntime.queryInterface(XUnoUrlResolver.class, urlResolver);
		 
		  // Import the object
		  Object rInitialObject = xUrlResolver.resolve( 
		      "uno:socket,host=localhost,port=2002;urp;StarOffice.ServiceManager");
		 
		  XMultiServiceFactory msf = (XMultiServiceFactory) UnoRuntime.queryInterface(XMultiServiceFactory.class, rInitialObject);
		  XInterface desktop = (XInterface) msf.createInstance("com.sun.star.frame.Desktop");
		  
		  XComponentLoader cl = (XComponentLoader) UnoRuntime.queryInterface(XComponentLoader.class, desktop);
		  XComponent component = cl.loadComponentFromURL(
				  "file:///home/werner/dev/test/hw/button.ods", // "private:factory/scalc", 
				  "_default", 
				  FrameSearchFlag.GLOBAL,
				  new PropertyValue[] { } );
		  
		  // XComponentContext
		if (null != component) {
			System.out.println("component object successfully retrieved");
		} else {
			System.out.println("given component-object name unknown at server side");
		}
		
		XModel xDocModel =  (XModel) UnoRuntime.queryInterface(XModel.class, component);
		XCell cell = (XCell) UnoRuntime.queryInterface(XCell.class, xDocModel.getCurrentSelection());
		System.out.println("cell: " + cell.getFormula());

	}

}

