package cerberus.application.prototype;

import org.xml.sax.InputSource;

import cerberus.util.system.CerberusInputStream;


import cerberus.manager.IGeneralManager;
import cerberus.manager.gui.SWTGUIManager;
import cerberus.manager.singelton.OneForAllManager;
import cerberus.manager.view.ViewManager;
import cerberus.manager.type.ManagerObjectType;

import cerberus.xml.parser.manager.XmlParserManager;
import cerberus.xml.parser.handler.command.CommandSaxHandler;

public class CerberusPrototype
{
	protected IGeneralManager refGeneralManager;
	protected SWTGUIManager refSWTGUIManager;
	protected ViewManager refViewManager;	
	protected XmlParserManager refXmlParserManager;
	
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
		
		refSWTGUIManager = (SWTGUIManager) refGeneralManager.getManagerByBaseType(ManagerObjectType.GUI_SWT);
		
		refViewManager = (ViewManager) refGeneralManager.getManagerByBaseType(ManagerObjectType.VIEW);
		
		refXmlParserManager = new XmlParserManager( refGeneralManager, false );
		
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
		String filename = "data/XML/bootstrap/cerberus_bootstrap_sample.xml";
		
		InputSource inSource = 
			CerberusInputStream.openInputStreamFromFile( filename );
					
		CerberusInputStream.parseOnce( inSource , refXmlParserManager );
	}
}
