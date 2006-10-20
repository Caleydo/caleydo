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
		OneForAllManager oneForAllManager = new OneForAllManager(null);
		oneForAllManager.setStateSWT( true );
		oneForAllManager.initAll();
		
		refGeneralManager = oneForAllManager.getGeneralManager();
		
		refSWTGUIManager = (ISWTGUIManager) refGeneralManager.getManagerByBaseType(ManagerObjectType.GUI_SWT);
		
		refViewManager = (IViewManager) refGeneralManager.getManagerByBaseType(ManagerObjectType.VIEW);
		
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
	}

	public void run()
	{		
		parseBootstrapDataFromXML();
		
		refSWTGUIManager.runApplication();
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
