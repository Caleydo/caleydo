package cerberus.application.prototype;

import org.xml.sax.InputSource;

import cerberus.util.system.CerberusInputStream;


import cerberus.manager.IEventPublisher;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ISWTGUIManager;
import cerberus.manager.IViewManager;
import cerberus.manager.singelton.OneForAllManager;
import cerberus.manager.type.ManagerObjectType;

import cerberus.xml.parser.manager.XmlParserManager;
import cerberus.xml.parser.handler.command.CommandSaxHandler;

public class CerberusPrototype
{
	protected IGeneralManager refGeneralManager;
	protected ISWTGUIManager refSWTGUIManager;
	protected IViewManager refViewManager;	
	protected XmlParserManager refXmlParserManager;
	//protected IEventPublisher refEventPublisher;
	
	public static void main(String[] args) 
	{
		CerberusPrototype prototype = new CerberusPrototype();	
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
		
		//refEventPublisher = (IEventPublisher) refGeneralManager.getManagerByBaseType(ManagerObjectType.EVENT_PUBLISHER);
		
		CommandSaxHandler cmdHandler = 
			new CommandSaxHandler( refGeneralManager,
					refXmlParserManager );
		
		refXmlParserManager.registerSaxHandler( cmdHandler );
		
	}

	public void run()
	{		
		parseBootstrapDataFromXML();
		
		refSWTGUIManager.runApplication();
	}
	
	protected void parseBootstrapDataFromXML()
	{
		String filename = "data/XML/bootstrap/cerberus_bootstrap_sample_documented.xml";
		
		InputSource inSource = 
			CerberusInputStream.openInputStreamFromFile( filename );
					
		CerberusInputStream.parseOnce( inSource , refXmlParserManager );
	}
}
