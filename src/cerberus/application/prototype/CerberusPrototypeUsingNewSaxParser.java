package cerberus.application.prototype;

import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.IOException;
//import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;

import cerberus.util.system.CerberusInputStream;
import cerberus.view.gui.IView;
import cerberus.view.gui.swt.heatmap.jogl.SwtJogHistogram2DViewRep;

import cerberus.manager.IGeneralManager;
import cerberus.manager.gui.SWTGUIManager;
import cerberus.manager.singelton.OneForAllManager;
import cerberus.manager.view.ViewManagerSimple;
import cerberus.manager.type.ManagerObjectType;
//import cerberus.xml.parser.ACerberusDefaultSaxHandler;
//import cerberus.xml.parser.command.CommandSaxHandler;
import cerberus.xml.parser.kgml.KgmlSaxHandler;

import cerberus.xml.parser.manager.XmlParserManager;
import cerberus.xml.parser.handler.command.CommandSaxHandler;

public class CerberusPrototypeUsingNewSaxParser
{
	protected IGeneralManager refGeneralManager;
	protected SWTGUIManager refSWTGUIManager;
	protected ViewManagerSimple refViewManager;	
	protected XmlParserManager refXmlParserManager;
	
	public static void main(String[] args) 
	{
		CerberusPrototypeUsingNewSaxParser prototype = new CerberusPrototypeUsingNewSaxParser();	
		prototype.run();
	}
	
	public CerberusPrototypeUsingNewSaxParser()
	{
		/**
		 * In order to use SWT call setStateSWT( true ) to enabel SWT support!
		 */
		OneForAllManager oneForAllManager = new OneForAllManager(null);
		oneForAllManager.setStateSWT( true );
		oneForAllManager.initAll();
		
		refGeneralManager = oneForAllManager.getGeneralManager();
		
		refSWTGUIManager = (SWTGUIManager) refGeneralManager.getManagerByBaseType(ManagerObjectType.GUI_SWT);
		
		refViewManager = (ViewManagerSimple) refGeneralManager.getManagerByBaseType(ManagerObjectType.VIEW);
		
		refXmlParserManager = new XmlParserManager( refGeneralManager, false );
		
		CommandSaxHandler cmdHandler = 
			new CommandSaxHandler( refGeneralManager,
					refXmlParserManager );
		
		refXmlParserManager.registerSaxHandler( cmdHandler, true );
		
	}

	public void run()
	{		
		parsePathwayDataFromXML();
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
	
	protected void parsePathwayDataFromXML()
	{
		// Read pathway data
	  	KgmlSaxHandler kgmlParser = new KgmlSaxHandler();

        SAXParserFactory factory = SAXParserFactory.newInstance();
        try 
        {
            // Parse the input
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse( 
            		new File("data/XML/pathways/map00271.xml"), kgmlParser);

        } catch (Throwable t) {
            t.printStackTrace();
        }
		
		
//		 MicroArrayLoader microArrayLoader = new MicroArrayLoader(generalManager);
//		 microArrayLoader.setTargetSet(oneForAllManager.getSingelton().getSetManager().getItemSet(25101));
//		 microArrayLoader.setFileName(sRawDataFileName);
//		 microArrayLoader.setTokenPattern("FLOAT;FLOAT;SKIP;STRING;STRING;ABORT");
//		 microArrayLoader.loadData();
		
//		 CmdSystemLoadFileViaImporter commandFileImporter =
//		 new CmdSystemLoadFileViaImporter(
//		 generalManager, sRawDataFileName, "SKIP;INT;SKIP;STRING;ABORT", 15101 );
//		 commandFileImporter.doCommand();

	}
	
}
