package cerberus.application.prototype;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import cerberus.view.gui.IView;
import cerberus.view.gui.swt.heatmap.jogl.SwtJogHistogram2DViewRep;

import cerberus.manager.IGeneralManager;
import cerberus.manager.gui.SWTGUIManager;
import cerberus.manager.singelton.OneForAllManager;
import cerberus.manager.view.ViewManagerSimple;
import cerberus.manager.type.ManagerObjectType;
import cerberus.xml.parser.ACerberusDefaultSaxHandler;
import cerberus.xml.parser.command.CommandSaxHandler;
import cerberus.xml.parser.kgml.KgmlSaxHandler;

public class CerberusPrototype
{
	protected IGeneralManager refGeneralManager;
	protected SWTGUIManager refSWTGUIManager;
	protected ViewManagerSimple refViewManager;
	
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
		
		refViewManager = (ViewManagerSimple) refGeneralManager.getManagerByBaseType(ManagerObjectType.VIEW);		
	}

	public void run()
	{
		parseInputFromXML();
		
		createViews();
		
		refSWTGUIManager.runApplication();
	}
	
	protected void parseInputFromXML()
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
		
		// Read application data
		CommandSaxHandler saxCmdHandler = 
			new CommandSaxHandler(refGeneralManager);
		String filename = "data/XML/bootstrap/cerberus_bootstrap_sample.xml";
		parseOnce( openInputStreamFromFile(filename), saxCmdHandler);
		
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
	
	protected void createViews()
	{
//		refViewManager.createView(ManagerObjectType.VIEW_TEST_TABLE);
//		refViewManager.createView(ManagerObjectType.VIEW_SWT_DATA_EXPLORER);
//		refViewManager.createView(ManagerObjectType.VIEW_SWT_GEARS);
//		refViewManager.createView(ManagerObjectType.VIEW_PATHWAY);
//		refViewManager.createView(ManagerObjectType.VIEW_SWT_HISTOGRAM2D);
		
//		IView newView = refViewManager.createView(ManagerObjectType.VIEW_SWT_HEATMAP2D);	
//		
//		SwtJogHistogram2DViewRep newCastView = (SwtJogHistogram2DViewRep) newView;
//		
//		Vector <String> vecAttributes = new Vector <String> ();		
//		vecAttributes.addElement( "35201" );
//		
//		newCastView.setAttributes( vecAttributes );
//		
//		newView.initView();
////		newView.retrieveNewGUIContainer();
//		newView.drawView();
	}

	/**
	 * Opens a file and returns an input stream to that file.
	 * 
	 * @param sXMLFileName
	 *            name abd path of the file
	 * @return input stream of the file, or null
	 */
	protected InputSource openInputStreamFromFile(final String sXMLFileName)
	{
		try
		{
			File inputFile = new File(sXMLFileName);
			FileReader inReader = new FileReader(inputFile);

			InputSource inStream = new InputSource(inReader);

			return inStream;
		} catch (FileNotFoundException fnfe)
		{
			System.out.println("File not found " + fnfe.toString());
		}
		return null;
	}

	public boolean parseOnce(InputSource inStream,
			ACerberusDefaultSaxHandler handler)
	{
		try
		{
			XMLReader reader = XMLReaderFactory.createXMLReader();
			reader.setContentHandler(handler);

			try
			{
				reader.parse(inStream);
			} catch (IOException e)
			{
				System.out.println(" EX " + e.toString());
			}

			/**
			 * Use data from parser to restore state...
			 */

			System.out.println("PARSE DONE\n  INFO:"
					+ handler.getErrorMessage());

			/**
			 * Restore state...
			 */

			inStream.getCharacterStream().close();
		} catch (SAXException se)
		{
			System.out.println("ERROR SE " + se.toString());
		} // end try-catch SAXException
		catch (IOException ioe)
		{
			System.out.println("ERROR IO " + ioe.toString());
		} // end try-catch SAXException, IOException

		return true;
	}
}
