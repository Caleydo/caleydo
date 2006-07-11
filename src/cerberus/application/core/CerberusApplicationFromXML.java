/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.application.core;

import java.io.IOException;
import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.StringReader;

import javax.swing.JFrame;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import cerberus.manager.CommandManager;
import cerberus.manager.GeneralManager;
import cerberus.manager.singelton.OneForAllManager;
import cerberus.view.manager.jogl.swing.CanvasSwingJoglManager;
import cerberus.xml.parser.CerberusDefaultSaxHandler;
import cerberus.xml.parser.jogl.SwingJoglJFrameSaxHandler;
import cerberus.xml.parser.swing.SwingJMenuSaxHandler;
import cerberus.xml.parser.command.CommandSaxHandler;



import prometheus.command.CommandType;
import prometheus.command.window.CmdWindowNewIFrameHistogram2D;
import prometheus.command.window.CmdWindowNewIFrameHeatmap2D;

//import prometheus.data.manager.BaseManagerType;
import prometheus.data.collection.Set;
import prometheus.data.collection.parser.CollectionFlatStorageParseSaxHandler;
import prometheus.data.collection.parser.CollectionSelectionParseSaxHandler;
import prometheus.data.collection.parser.CollectionSetParseSaxHandler;
import prometheus.net.dwt.swing.collection.DSwingSelectionCanvas;
import prometheus.net.dwt.swing.collection.DSwingStorageCanvas;
import prometheus.net.dwt.swing.DSwingHistogramCanvas;
import prometheus.net.dwt.swing.DSwingHeatMap2DCanvas;
import prometheus.net.dwt.swing.parser.DParseBaseSaxHandler;
import prometheus.net.dwt.swing.parser.DSwingHistogramCanvasHandler;
import prometheus.net.dwt.swing.menu.DMenuBootStraper;
import prometheus.net.dwt.swing.mdi.DDesktopPane;
import prometheus.net.dwt.swing.mdi.DInternalFrame;



//import prometheus.data.xml.MementoNetEventXML;
import prometheus.data.xml.MementoCallbackXML;

import org.studierstube.net.protocol.muddleware.ClientByteStreamHandler;
import org.studierstube.net.protocol.muddleware.Message;
import org.studierstube.net.protocol.muddleware.Operation;


/**
 * Creates the hole application state from a XML file.
 * 
 * @author Michael Kalkusch
 *
 */
public class CerberusApplicationFromXML {
//implements MementoCallbackXML {


	private boolean bUSeMuddlewareXMLserver = false;
	
	private ClientByteStreamHandler connection;
	
	protected CanvasSwingJoglManager canvasManager;
	
	protected SwingJoglJFrameSaxHandler saxHandler;
	
	protected final GeneralManager refGeneralManager;

	private CommandSaxHandler saxCmdHandler;
	
	/**
	 * 
	 */
	public CerberusApplicationFromXML() {
		
		refGeneralManager =	new OneForAllManager( null );
		
		System.out.println("...Cerberus bootloader...");
		
		canvasManager = new CanvasSwingJoglManager( refGeneralManager );		
		canvasManager.run_Animator();
		
		saxHandler = canvasManager.getSAXHandler();
		
		saxCmdHandler = new CommandSaxHandler( refGeneralManager );
		
		//saxHandler = new SwingJoglJFrameSaxHandler( canvasManager );		

		
		connection = new ClientByteStreamHandler( null );
		
	}

	
//	public void callbackForParser( final String tag_causes_callback,
//			final DParseSaxHandler refSaxHandler) {
//		
//	}
	
	public boolean parseOnce( InputSource inStream, 
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
			
			System.out.println("PARS DONE\n  INFO:" +
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
	
	/**
	 * Bootstrapping of GeneralManager and menus inside the views. 
	 * Not parsing personal settings.
	 * 
	 * @return TRUE on successful bootstrapping of frames, views and menus inside the views
	 */
	protected boolean initializedGeneralManager() {
		
		return true;
	}
	
	public boolean bootstrapFromXMLusingFileName( final String filename ) {
		
		System.out.print("   bootloader read data from file.. ");
		
		parseOnce( openInputStreamFromFile(filename), saxHandler);
		
		parseOnce( openInputStreamFromFile(filename), 
				new SwingJMenuSaxHandler( refGeneralManager, canvasManager ) );
		
		parseOnce( openInputStreamFromFile(filename), saxCmdHandler);
		
		System.out.println(" views initialized!");
		
		System.out.print("create menu from XML file..");
		
		System.out.println("PARSE done.");
	
		/**
		 * SELECTION is done...
		 */
						
		return initializedGeneralManager();
	}
	
	public boolean bootstrapFromXMLusingMuddleWare( final String filename ) {
		
		System.out.print("   bootloader read data from muddleware server.. ");
		
		String sXPath = "/cerberus/workspace";
		
		connection.setServerNameAndPort( "localhost", 20000 );
		if ( connection.connect() ) {
			System.out.println("Can not connect to Muddleware server.");
			return false;
		}
		
		Operation operationSend = new Operation( Operation.OP_ELEMENT_EXISTS );
		operationSend.setXPath( sXPath );
		
		Message sendMsg = new Message();
		sendMsg.addOperation( operationSend );
		
		Message receiveMsg = connection.sendReceiveMessage( sendMsg );
		
		if (( receiveMsg == null )||( receiveMsg.getNumOperations() < 1 )) {
			System.out.println("XPath does not exist, Muddleware server has no data on canvas settings.");
			connection.disconnect();
			return false;
		}
		
		if ( receiveMsg.getOperation( 0 ).getNodeString().equalsIgnoreCase( "true" ) ) {
			
			operationSend.setOperation( Operation.OP_GET_ELEMENT );
			
			/* get configuration .. */
			sendMsg.setOperation( operationSend );
			receiveMsg = connection.sendReceiveMessage( sendMsg );
			connection.disconnect();
						
			String configutaion = receiveMsg.getOperation( 0 ).getNodeString();						
			InputSource in = new InputSource(new StringReader(configutaion));				
			
			parseOnce( in, saxHandler);
			
			System.out.println("PARSE done.");
			
		} else {
			System.out.println("Muddleware server has no data on canvas settings.");
			connection.disconnect();
			return false;
		}
		
	
	
		/**
		 * SELECTION is done...
		 */
		
		
		return initializedGeneralManager();
	}
	
	/**
	 * Opens a file and returns an input stream to that file.
	 * 
	 * @param sXMLFileName name abd path of the file
	 * @return input stream of the file, or null
	 */
	protected InputSource openInputStreamFromFile( final String sXMLFileName ) {
		
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
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		CerberusApplicationFromXML xmlBootStrapper =
			new CerberusApplicationFromXML();
		
		String sFileXML = "..\\data\\XML\\bootstrap\\cerberus_bootstrap_sample.xml";
		
		/**
		 * use arguments...
		 */
		if ( args.length > 0 ) {
			sFileXML = args[0];
		}
		
//		XmlBootStrapper.setSize( 800, 500 );
//		//XmlBootStrapper.setLocation( 2000, 30);
//		XmlBootStrapper.setLocation( 300, 30);
//		XmlBootStrapper.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
//		XmlBootStrapper.setVisible( true );
		
		try {
			
			if ( xmlBootStrapper.bUSeMuddlewareXMLserver ) {
				xmlBootStrapper.bootstrapFromXMLusingMuddleWare( "" );
			} else {
				xmlBootStrapper.bootstrapFromXMLusingFileName( sFileXML );
			}
			
		}
		catch ( Exception e) {
			System.out.println("ERROR: " + e.toString());
		}
		
		
	}
	
//	public void callbackForParser( final BaseManagerType type,
//			final String tag_causes_callback,
//			final DParseSaxHandler refSaxHandler) {
//			
//	}

}
