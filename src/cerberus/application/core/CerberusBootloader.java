/**
 * 
 */
package cerberus.application.core;


import java.io.StringReader;

import org.xml.sax.InputSource;

import cerberus.manager.IGeneralManager;
import cerberus.manager.singelton.OneForAllManager;
import cerberus.xml.parser.handler.IXmlParserHandler;
import cerberus.xml.parser.handler.command.CommandSaxHandler;
import cerberus.xml.parser.manager.XmlParserManager;
import cerberus.xml.parser.manager.IXmlParserManager;

import org.studierstube.net.protocol.muddleware.ClientByteStreamHandler;
import org.studierstube.net.protocol.muddleware.Message;
import org.studierstube.net.protocol.muddleware.Operation;


/**
 * Basic Cerberus Bootloader, starts application either 
 * from local XML-file or fram Muddleware-Server.
 * 
 * Requires package: org.studierstube.net.protocol.muddleware.*
 * 
 * @author kalkusch
 *
 */
public class CerberusBootloader
{

	private ClientByteStreamHandler connection;
		
	protected final IGeneralManager refGeneralManager;
	
	protected IXmlParserManager refParserManager;
		
	
	public static void main(String[] args) 
	{
		CerberusBootloader prototype = new CerberusBootloader();
		prototype.run( "data/XML/bootstrap/cerberus_bootstrap_sample.xml" );
		
		prototype.runUsingMuddleWare( "/cerberus/workspace" );
	}
	
	/**
	 * 
	 */
	public CerberusBootloader()
	{
		System.out.println("...Cerberus Bootloader...");
		
		refGeneralManager = new OneForAllManager( null );
		
		((OneForAllManager) refGeneralManager).initAll();
		
		
		refParserManager = new XmlParserManager( refGeneralManager, false );
		
		/**
		 * Register all required IXmlParserHandler to the XmlParserManager...
		 */
		IXmlParserHandler cmdHandler = 
			new CommandSaxHandler( refGeneralManager,
					refParserManager );
				
		refParserManager.registerSaxHandler( cmdHandler );
	
	}
	
	/**
	 * Start application by parsing a XMl file.
	 * 
	 * @param filename XML bootstrap file
	 */
	public void run( final String filename  ) {
		
		refParserManager.parseXmlFileByName( filename );
		
		
//		/*
//		 * Second possibility:
//		 */
//		
//		//import org.xml.sax.InputSource;
//		//import cerberus.util.system.CerberusInputStream;
//		
//		InputSource inSource = 
//			CerberusInputStream.openInputStreamFromFile( filename );
//					
//		refParserManager.parseXmlFileByInputStream( inSource );
	}

	/**
	 * Connect to Muddelware server and get XML-configuration 
	 * from the Muddleware-Server.
	 * 
	 * Not tested yet!
	 * 
	 */
	public boolean runUsingMuddleWare( final String sXPath ) {
		
		System.out.print("   bootloader read data from muddleware server.. ");
		
		/**
		 * initialize connection...
		 * connection is null if runUsingMuddleWare() called fo the first time.
		 */
		if ( connection == null ) {
			connection = new ClientByteStreamHandler( null );
		}
		
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
			InputSource inStream = new InputSource(new StringReader(configutaion));				
			
			refParserManager.parseXmlFileByInputStream( inStream );
			
			System.out.println("PARSE using Muddleware done.");
			
		} else {
			System.out.println("Muddleware server has no data on canvas settings.");
			connection.disconnect();
			return false;
		}
		
		
		return true;
	}
}
