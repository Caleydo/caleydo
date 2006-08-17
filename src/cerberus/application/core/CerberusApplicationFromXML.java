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

//import javax.swing.JFrame;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

//import cerberus.manager.CommandManager;
import cerberus.manager.IGeneralManager;
import cerberus.manager.singelton.OneForAllManager;
import cerberus.util.system.CerberusInputStream;
import cerberus.view.manager.jogl.swing.CanvasSwingJoglManager;
import cerberus.xml.parser.ACerberusDefaultSaxHandler;
import cerberus.xml.parser.jogl.SwingJoglJFrameSaxHandler;
import cerberus.xml.parser.swing.SwingJMenuSaxHandler;
import cerberus.xml.parser.command.CommandSaxHandler;



//import cerberus.command.CommandType;
//import cerberus.command.window.CmdWindowNewIFrameHistogram2D;
//import cerberus.command.window.CmdWindowNewIFrameHeatmap2D;
//
////import cerberus.data.manager.BaseManagerType;
//import cerberus.data.collection.Set;
//import cerberus.data.collection.parser.CollectionFlatStorageParseSaxHandler;
//import cerberus.data.collection.parser.CollectionSelectionParseSaxHandler;
//import cerberus.data.collection.parser.CollectionSetParseSaxHandler;
//import cerberus.net.dwt.swing.collection.DSwingSelectionCanvas;
//import cerberus.net.dwt.swing.collection.DSwingStorageCanvas;
//import cerberus.net.dwt.swing.canvas.DSwingHistogramCanvas;
//import cerberus.net.dwt.swing.canvas.DSwingHeatMap2DCanvas;
//import cerberus.xml.parser.DParseBaseSaxHandler;
//import cerberus.net.dwt.swing.parser.DSwingHistogramCanvasHandler;
//import cerberus.net.dwt.swing.menu.DMenuBootStraper;
//import cerberus.net.dwt.swing.mdi.DDesktopPane;
//import cerberus.net.dwt.swing.mdi.DInternalFrame;


//import cerberus.data.xml.MementoCallbackXML;

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
//implements IMementoCallbackXML {


	private boolean bUSeMuddlewareXMLserver = false;
	
	private ClientByteStreamHandler connection;
	
	protected CanvasSwingJoglManager canvasManager;
	
	protected SwingJoglJFrameSaxHandler saxHandler;
	
	protected final IGeneralManager refGeneralManager;

	private CommandSaxHandler saxCmdHandler;
	
	/**
	 * 
	 */
	public CerberusApplicationFromXML() {
		
		refGeneralManager =	new OneForAllManager( null );				
		((OneForAllManager) refGeneralManager).initAll();
		
		System.out.println("...Cerberus bootloader...");
		
		canvasManager = new CanvasSwingJoglManager( refGeneralManager );		
		canvasManager.run_Animator();
		
		saxHandler = canvasManager.getSAXHandler();
		
		saxCmdHandler = new CommandSaxHandler( refGeneralManager );
		
		//saxHandler = new SwingJoglJFrameSaxHandler( canvasManager );		
		
		connection = new ClientByteStreamHandler( null );
		
	}

	
//	public void callbackForParser( final String tag_causes_callback,
//			final ISaxParserHandler refSaxHandler) {
//		
//	}
	
	
	/**
	 * Bootstrapping of IGeneralManager and menus inside the views. 
	 * Not parsing personal settings.
	 * 
	 * @return TRUE on successful bootstrapping of frames, views and menus inside the views
	 */
	protected boolean initializedGeneralManager() {
		
		return true;
	}
	
	public boolean bootstrapFromXMLusingFileName( final String filename ) {
		
		System.out.print("   bootloader read data from file.. ");
		
		CerberusInputStream.parseOnce( 
				CerberusInputStream.openInputStreamFromFile(filename),
				saxHandler);
		
		CerberusInputStream.parseOnce( 
				CerberusInputStream.openInputStreamFromFile(filename),
				new SwingJMenuSaxHandler( refGeneralManager, canvasManager ) );
		
		CerberusInputStream.parseOnce( 
				CerberusInputStream.openInputStreamFromFile(filename),
				saxCmdHandler);
		
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
			
			CerberusInputStream.parseOnce( in, saxHandler);
			
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
	 * @param args
	 */
	public static void main(String[] args) {
		
		CerberusApplicationFromXML xmlBootStrapper =
			new CerberusApplicationFromXML();
		
		String sFileXML = "data/XML/bootstrap/cerberus_bootstrap_sample.xml";
		
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
	
//	public void callbackForParser( final ManagerObjectType type,
//			final String tag_causes_callback,
//			final ISaxParserHandler refSaxHandler) {
//			
//	}

}
