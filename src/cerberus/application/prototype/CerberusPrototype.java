package cerberus.application.prototype;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import cerberus.manager.GeneralManager;
import cerberus.manager.gui.SWTGUIManagerSimple;
import cerberus.manager.singelton.OneForAllManager;
import cerberus.manager.view.ViewManagerSimple;
import cerberus.command.system.CmdSystemLoadFileViaImporter;
import cerberus.data.loader.MicroArrayLoader;
import cerberus.manager.type.ManagerObjectType;
import cerberus.xml.parser.CerberusDefaultSaxHandler;
import cerberus.xml.parser.command.CommandSaxHandler;
import cerberus.xml.parser.kgml.KgmlSaxHandler;



public class CerberusPrototype 
{
	public static void main(String[] args) 
	{
		String sRawDataFileName = "data/MicroarrayData/gpr_format/tests/test_10_values.gpr";
		
		/**
		 * In order to use SWT call setStateSWT( true ) to enabel SWT support!
		 */
		OneForAllManager oneForAllManager = new OneForAllManager(null);
		oneForAllManager.setStateSWT( true );
		oneForAllManager.initAll();
		
		GeneralManager generalManager = oneForAllManager.getGeneralManager();
		
		CommandSaxHandler saxCmdHandler = 
			new CommandSaxHandler( generalManager );
		
		String filename = "data/XML/bootstrap/cerberus_bootstrap_sample.xml";
					
		parseOnce( openInputStreamFromFile(filename), saxCmdHandler);
		
		//loading the raw data
//		MicroArrayLoader microArrayLoader = 
//			new MicroArrayLoader(oneForAllManager.getGeneralManager(), sRawDataFileName);
//		microArrayLoader.loadData();
		
		//load the pathway data
//		KgmlSaxHandler kgmlParser = new KgmlSaxHandler();
//		SAXParserFactory factory = SAXParserFactory.newInstance();
//		try
//		{
//			// Parse the input
//			SAXParser saxParser = factory.newSAXParser();
//			saxParser.parse(new File("data/XML/pathways/map00271.xml"),
//					kgmlParser);
//
//		} catch (Throwable t)
//		{
//			t.printStackTrace();
//		}
		
//		MicroArrayLoader microArrayLoader = new MicroArrayLoader(generalManager);
//		microArrayLoader.setTargetSet(oneForAllManager.getSingelton().getSetManager().getItemSet(25101));
//		microArrayLoader.setFileName(sRawDataFileName);
//		microArrayLoader.setTokenPattern("FLOAT;FLOAT;SKIP;STRING;STRING;ABORT");
//		microArrayLoader.loadData();

		CmdSystemLoadFileViaImporter commandFileImporter = 
			new CmdSystemLoadFileViaImporter(
				generalManager, sRawDataFileName, "SKIP;INT;SKIP;STRING;ABORT", 15101 );
		commandFileImporter.doCommand();
		
		SWTGUIManagerSimple swtGuiManager = (SWTGUIManagerSimple) generalManager.getManagerByBaseType(ManagerObjectType.GUI_SWT);
		
		ViewManagerSimple viewManager = (ViewManagerSimple) generalManager.getManagerByBaseType(ManagerObjectType.VIEW);
		//viewManager.createView(ManagerObjectType.VIEW_PATHWAY);
		//viewManager.createView(ManagerObjectType.VIEW_TEST_TABLE);
		viewManager.createView(ManagerObjectType.VIEW_SWT_DATA_EXPLORER);
		//viewManager.createView(ManagerObjectType.VIEW_SWT_SET_TABLE);
		//viewManager.createView(ManagerObjectType.VIEW_SWT_STORAGE_TABLE);
		//viewManager.createView(ManagerObjectType.VIEW_SWT_GEARS);
		//viewManager.createView(ManagerObjectType.VIEW_SWT_HEATMAP2D);
		
		swtGuiManager.runApplication();
	}
	
	/**
	 * Opens a file and returns an input stream to that file.
	 * 
	 * @param sXMLFileName name abd path of the file
	 * @return input stream of the file, or null
	 */
	protected static InputSource openInputStreamFromFile( final String sXMLFileName ) {
		
		try {
			File inputFile = new File( sXMLFileName );		
			FileReader inReader = new FileReader( inputFile );
			
			InputSource inStream = new InputSource( inReader );
			
			return inStream;
		}
		catch ( FileNotFoundException fnfe) {
			System.out.println("File not found " + fnfe.toString() );
		}
		return null;
	}
	
	public static boolean parseOnce( InputSource inStream, 
			CerberusDefaultSaxHandler handler) {
		
		try {
			XMLReader reader = XMLReaderFactory.createXMLReader();			
			reader.setContentHandler( handler );
			
			try {						
				reader.parse( inStream );
			} 
			catch ( IOException e) {
				System.out.println(" EX " + e.toString() );
			}
			
			/**
			 * Use data from parser to restore state...
			 */ 
			
			System.out.println("PARSE DONE\n  INFO:" +
					handler.getErrorMessage() );
			
			/**
			 * Restore state...
			 */
		
			inStream.getCharacterStream().close();
		}
		catch (SAXException se) {
			System.out.println("ERROR SE " + se.toString() );
		} // end try-catch SAXException
		catch (IOException ioe) {
			System.out.println("ERROR IO " + ioe.toString() );
		} // end try-catch SAXException, IOException
		
		
		return true;
	}
}
