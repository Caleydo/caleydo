package cerberus.application.prototype;

import org.xml.sax.InputSource;

import cerberus.util.system.CerberusInputStream;

import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager;
import cerberus.manager.ISWTGUIManager;
import cerberus.manager.IViewManager;
import cerberus.manager.singelton.OneForAllManager;
import cerberus.manager.type.ManagerObjectType;

import cerberus.xml.parser.manager.XmlParserManager;
//import cerberus.xml.parser.handler.command.CommandSaxHandler;

public class CerberusPrototype
{
	private String sFileName;
	
	private OneForAllManager refOneForAllManager;
	
	protected IGeneralManager refGeneralManager;
	protected ISWTGUIManager refSWTGUIManager;
	protected IViewManager refViewManager;	
	protected XmlParserManager refXmlParserManager;
	//protected IEventPublisher refEventPublisher;
	
	public static void main(String[] args) 
	{
		CerberusPrototype prototype = new CerberusPrototype();
		
		if ( args.length > 0 ) 
		{
			prototype.setXmlFileName( args[0] ); 	
		}
		
		prototype.run();
	}
	
	public CerberusPrototype()
	{
		/**
		 * In order to use SWT call setStateSWT( true ) to enabel SWT support!
		 */
		refOneForAllManager = new OneForAllManager(null);
		refOneForAllManager.setStateSWT( true );
		refOneForAllManager.initAll();
		
		refGeneralManager = refOneForAllManager.getGeneralManager();

		refXmlParserManager = new XmlParserManager( refGeneralManager, false );
		
		refGeneralManager.getSingelton().setXmlParserManager(refXmlParserManager);
				
		/**
		 * Register additional SaxParserHandler here:
		 * <br>
		 * sample code:<br>
		 * <br>
		 * AnySaxHandler myNewHandler = <br>		if ( args.length > 0 ) 
		{
			prototype.setXmlFileName( args[0] ); 	
		}
		 *   new CommandSaxHandler( generalManager, refXmlParserManager );<br>
		 * <br>  
		 * refXmlParserManager.registerAndInitSaxHandler( myNewHandler ); <br>
		 * <br>
		 */

		// Default file name
		setXmlFileName( "data/XML/bootstrap/cerberus_bootstrap_sample_demo.xml" );

		refViewManager = (IViewManager) refGeneralManager.getSingelton().getViewGLCanvasManager();
		
		refSWTGUIManager = (ISWTGUIManager) refGeneralManager.getSingelton().getSWTGUIManager();		

	}

	public void run()
	{		
		parseBootstrapDataFromXML();
		
		refSWTGUIManager.runApplication();
		
		refOneForAllManager.destroyOnExit();
	}
	
	public final void setXmlFileName( String sFileName ) {
		this.sFileName = sFileName;
	}
	
	public final String getXmlFileName() {
		return this.sFileName;
	}
	
	protected void parseBootstrapDataFromXML()
	{
		ILoggerManager logger = 
			refGeneralManager.getSingelton().getLoggerManager();
		
		InputSource inSource = 
			CerberusInputStream.openInputStreamFromFile( sFileName, 
					logger );
				
		CerberusInputStream.parseOnce( inSource , 
				sFileName,
				refXmlParserManager,
				logger );
				
	}
}
