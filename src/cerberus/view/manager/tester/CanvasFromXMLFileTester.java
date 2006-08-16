/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.view.manager.tester;

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
import cerberus.manager.singelton.OneForAllManager;
import cerberus.view.manager.jogl.swing.CanvasSwingJoglManager;
import cerberus.xml.parser.jogl.SwingJoglJFrameSaxHandler;




import cerberus.command.CommandType;
import cerberus.command.window.CmdWindowNewIFrameHistogram2D;
import cerberus.command.window.CmdWindowNewIFrameHeatmap2D;

//import cerberus.data.manager.BaseManagerType;
import cerberus.data.collection.ISet;
import cerberus.data.collection.parser.CollectionFlatStorageSaxParserHandler;
import cerberus.data.collection.parser.CollectionSelectionSaxParserHandler;
import cerberus.data.collection.parser.CollectionSetSaxParserHandler;
import cerberus.net.dwt.swing.collection.DSwingSelectionCanvas;
import cerberus.net.dwt.swing.collection.DSwingStorageCanvas;
import cerberus.net.dwt.swing.canvas.DSwingHistogramCanvas;
import cerberus.net.dwt.swing.canvas.DSwingHeatMap2DCanvas;
import cerberus.xml.parser.ASaxParserHandler;
import cerberus.net.dwt.swing.parser.DSwingHistogramCanvasHandler;
import cerberus.net.dwt.swing.menu.DMenuBootStraper;
import cerberus.net.dwt.swing.mdi.DDesktopPane;
import cerberus.net.dwt.swing.mdi.DInternalFrame;



//import cerberus.data.xml.MementoNetEventXML;
import cerberus.data.xml.MementoCallbackXML;

import org.studierstube.net.protocol.muddleware.ClientByteStreamHandler;
import org.studierstube.net.protocol.muddleware.Message;
import org.studierstube.net.protocol.muddleware.Operation;


/**
 * Creates the hole application state from a XML file.
 * 
 * @author Michael Kalkusch
 *
 */
public class CanvasFromXMLFileTester extends JFrame {
//implements MementoCallbackXML {


	private boolean bUSeMuddlewareXMLserver = false;
	
	private ClientByteStreamHandler connection;
	
	protected CanvasSwingJoglManager canvasManager;
	
	protected SwingJoglJFrameSaxHandler saxHandler;
	
	
	/**
	 * 
	 */
	public CanvasFromXMLFileTester() {
		super("XML BootStrapper");
		
		OneForAllManager regGeneralManager = new OneForAllManager(null);
		regGeneralManager.initAll();
		
		canvasManager = (CanvasSwingJoglManager) 
			regGeneralManager.getSingelton().getViewCanvasManager();		
		
		canvasManager.run_Animator();
		
		saxHandler = canvasManager.getSAXHandler();
		
		//saxHandler = new SwingJoglJFrameSaxHandler( canvasManager );		
		
		
		setSize( 150, 150 );
		//XmlBootStrapper.setLocation( 2000, 30);
		setLocation( 50, 50);
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		setVisible( true );
		
		connection = new ClientByteStreamHandler( null );
		
	}

	
//	public void callbackForParser( final String tag_causes_callback,
//			final ISaxParserHandler refSaxHandler) {
//		
//	}
	
	public boolean parseOnce( InputSource inStream, 
			SwingJoglJFrameSaxHandler handler) {
		
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
	
	public boolean bootstrapFromXMLusingFileName( final String filename ) {
		
		parseOnce( openInputStreamFromFile(filename), saxHandler);
		
		System.out.println("PARSE done.");
	
		/**
		 * SELECTION is done...
		 */
		
		this.repaint();
		
		return true;
	}
	
	public boolean bootstrapFromXMLusingMuddleWare( final String filename ) {
		
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
		
		this.repaint();
		
		return true;
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
		
		CanvasFromXMLFileTester xmlBootStrapper =
			new CanvasFromXMLFileTester();
		
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
		
		xmlBootStrapper.repaint();
		
	}
	
//	public void callbackForParser( final ManagerObjectType type,
//			final String tag_causes_callback,
//			final ISaxParserHandler refSaxHandler) {
//			
//	}

}
