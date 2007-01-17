package cerberus.application.core;

import org.xml.sax.InputSource;

import java.io.StringReader;

//import cerberus.manager.IGeneralManager;
//import cerberus.manager.IViewManager;
import cerberus.manager.ILoggerManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.manager.ISWTGUIManager;
import cerberus.manager.ISingelton;
import cerberus.manager.IXmlParserManager;
import cerberus.manager.parser.XmlParserManager;
import cerberus.manager.singelton.OneForAllManager;
import cerberus.manager.singelton.IGeneralManagerSingelton;
//import cerberus.util.system.CerberusInputStream;

//import cerberus.xml.parser.handler.IXmlParserHandler;
//import cerberus.xml.parser.handler.command.CommandSaxHandler;
//import cerberus.xml.parser.handler.command.CommandSaxHandler;


import org.studierstube.net.protocol.muddleware.ClientByteStreamHandler;
import org.studierstube.net.protocol.muddleware.Message;
import org.studierstube.net.protocol.muddleware.Operation;


/**
 * Basic Cerberus Bootloader, starts application either 
 * from local XML-file or fram Muddleware-Server.
 * 
 * Requires package: org.studierstube.net.protocol.muddleware.*
 * 
 * @author Michael Kalkusch
 *
 */
public class CerberusBootloader
{
	
	/**
	 * Switch for loading XML file from Muddelware server.
	 * Default is FALSE and indicate, that the local XML file is used for bootstrapping.
	 * TRUE wil get teh XML file from the Muddleware server.
	 * 
	 * TODO: softcode Muddleware server and Muddleware server XPath.
	 */
	private boolean bEnableBootstrapViaMuddleware = false;
	
	/**
	 * File name of the local XML file
	 */
	private String sFileName;
	
	private ClientByteStreamHandler connection;

	/**
	 * Reference to Singelton. Is also the reference to the IGeneralManager.
	 * 
	 * @see import cerberus.manager.IGeneralManager
	 */
	protected IGeneralManagerSingelton refOneForAllManager;
	
	//protected final IGeneralManager refGeneralManager;
	
	/**
	 * This reference is used for starting the GUI
	 */
	protected ISWTGUIManager refSWTGUIManager;
	
	/**
	 * Reference to XML parser. 
	 * The parser does the bootstrapping of the cerberus application 
	 * using an XML input stream.
	 */
	protected IXmlParserManager refXmlParserManager;
	
	
	/**
	 * Reference to Logger
	 */
	protected ILoggerManager logger;
	
	//for debugging only:
	//protected IViewManager refViewManager;	
	
	//for debugging only:
	//protected IEventPublisher refEventPublisher;
	
	
	/**
	 * Run the Cerberus core application ..
	 */
	public static void main(String[] args) 
	{
		CerberusBootloader prototype = new CerberusBootloader();
		
		if ( args.length > 0 ) 
		{
			prototype.setXmlFileName( args[0] ); 	
		}
		
		prototype.run();
	}
	
	public CerberusBootloader()
	{
		/**
		 * In order to use SWT call setStateSWT( true ) to enabel SWT support!
		 */
		refOneForAllManager = new OneForAllManager(null);
		refOneForAllManager.setStateSWT( true );
		refOneForAllManager.initAll();

		final ISingelton refSingelton = refOneForAllManager.getSingelton();
		
		logger = refSingelton.getLoggerManager();		
		logger.logMsg("... Start Cerberus Core ...", LoggerType.STATUS);
		
		//refViewManager = (IViewManager) refSingelton.getViewGLCanvasManager();
		
		refSWTGUIManager = refSingelton.getSWTGUIManager();		
	
		
		/**
		 * create the parser manager..
		 */
		refXmlParserManager = new XmlParserManager( refOneForAllManager, false );
		
		refSingelton.setXmlParserManager(refXmlParserManager);
		
		/**
		 * Register additional SaxParserHandler here:
		 * <br>
		 * sample code:<br>
		 * <br>
		 * AnySaxHandler myNewHandler = <br>
		 *   new CommandSaxHandler( generalManager, refXmlParserManager );<br>
		 * <br>  
		 * refXmlParserManager.registerAndInitSaxHandler( myNewHandler ); <br>
		 * <br>
		 */
		
		
		/** 
		 * Default file name
		 */
		setXmlFileName( "data/XML/bootstrap/cerberus_bootstrap_sample_demo.xml" );

	}

	
	/**
	 * Connect to Muddelware server and get XML-configuration 
	 * from the Muddleware-Server.
	 * 
	 * Not tested yet!
	 * 
	 */
	protected boolean runUsingMuddleWare( final String sXPath ) {
		
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
						
			String configuration = receiveMsg.getOperation( 0 ).getNodeString();						
			InputSource inStream = new InputSource(new StringReader(configuration));				
			
			refXmlParserManager.parseXmlFileByInputStream( inStream, configuration );
			
			System.out.println("PARSE using Muddleware done.");
			
		} else {
			System.out.println("Muddleware server has no data on canvas settings.");
			connection.disconnect();
			return false;
		}
		
		
		return true;
	}
	

	/**
	 * Set local XML file name.
	 * Is case the XML file is received from the Muddleware server
	 * This is the XPath used to query the Muddleware server.
	 * 
	 * @see cerberus.application.core.CerberusBootloader#setBootstrapViaMuddleware(boolean)
	 * @see cerberus.application.core.CerberusBootloader#getBootstrapViaMuddleware()
	 * 
	 * @see cerberus.application.core.CerberusBootloader#getXmlFileName()
	 * 
	 * @param sFileName 
	 */
	public final void setXmlFileName( String sFileName ) {
		this.sFileName = sFileName;
	}
	
	/**
	 * Get local XML file name if config is read from local file or XPath if config is read from Muddleware server.
	 *  
	 * @see cerberus.application.core.CerberusBootloader#setBootstrapViaMuddleware(boolean)
	 * @see cerberus.application.core.CerberusBootloader#getBootstrapViaMuddleware()
	 * 
	 * @see cerberus.application.core.CerberusBootloader#setXmlFileName(String)
	 * 
	 * @return XML file name or XPath
	 */
	public final String getXmlFileName() {
		return this.sFileName;
	}
	
	/**
	 * 
	 * @see cerberus.application.core.CerberusBootloader#setXmlFileName(String)
	 * @see cerberus.application.core.CerberusBootloader#getXmlFileName()
	 * 
	 * @param bEnableBootstrapViaMuddleware TRUE for loading config via Muddleware or FALSE for loading config from local file.
	 */
	public final void setBootstrapViaMuddleware( boolean bEnableBootstrapViaMuddleware ) {
		this.bEnableBootstrapViaMuddleware = bEnableBootstrapViaMuddleware;
	}
	
	/**
	 * 
	 * @see cerberus.application.core.CerberusBootloader#setXmlFileName(String)
	 * @see cerberus.application.core.CerberusBootloader#getXmlFileName()
	 * 
	 * @return TRUE is config will be loaded via Muddleware or FALSE is config is loaded from local file.
	 */
	public final boolean getBootstrapViaMuddleware() {
		return this.bEnableBootstrapViaMuddleware;
	}
	
	
	
	/**
	 * Start application by parsing a XMl file. 
	 * XML file is eihter local or received from a Muddleware server.
	 * 
	 * @param filename XML bootstrap file
	 */
	public void run() {
		
				
		if ( bEnableBootstrapViaMuddleware )
		{
			/**
			 * Load configuration from Muddleware server.
			 */
			logger.logMsg("  load config via Muddleware server ...", LoggerType.STATUS);			
			runUsingMuddleWare( "/cerberus/workspace" );
		}
		else
		{
			/**
			 * Load configuration from local XML file.
			 */
			logger.logMsg("  load config via local XML file ... ", LoggerType.STATUS);			
			refXmlParserManager.parseXmlFileByName( sFileName );		
		}

		logger.logMsg("  config loaded, start GUI ... ", LoggerType.STATUS);		
		refSWTGUIManager.runApplication();
		
		logger.logMsg("Cerberus.core   clean up... ", LoggerType.STATUS);		
		refOneForAllManager.destroyOnExit();
		
		logger.logMsg("Cerberus.core   clean up... [done]\n", LoggerType.STATUS);		
		logger.logMsg("... Stop Cerberus Core ...", LoggerType.STATUS);
	}
	
}
