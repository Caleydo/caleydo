package org.geneview.core.application.core;

//import org.geneview.core.manager.IGeneralManager;
//import org.geneview.core.manager.IViewManager;
import org.geneview.core.manager.ILoggerManager;
import org.geneview.core.manager.ILoggerManager.LoggerType;
import org.geneview.core.manager.ISWTGUIManager;
import org.geneview.core.manager.ISingelton;
import org.geneview.core.manager.IXmlParserManager;
import org.geneview.core.manager.parser.XmlParserManager;
import org.geneview.core.manager.singleton.IGeneralManagerSingleton;
import org.geneview.core.manager.singleton.OneForAllManager;
//import org.geneview.core.util.system.CerberusInputStream;

//import org.geneview.core.parser.handler.IXmlParserHandler;
//import org.geneview.core.parser.handler.command.CommandSaxHandler;
//import org.geneview.core.parser.handler.command.CommandSaxHandler;


import org.studierstube.net.protocol.muddleware.ClientByteStreamHandler;
import org.studierstube.net.protocol.muddleware.IMessage;
import org.studierstube.net.protocol.muddleware.IOperation;
import org.studierstube.net.protocol.muddleware.Message;
import org.studierstube.net.protocol.muddleware.Operation;
import org.studierstube.net.protocol.muddleware.OperationEnum;


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
	
	private boolean bIsRunning = false;
	
	/**
	 * Switch for loading XML file from Muddelware server.
	 * Default is FALSE and indicate, that the local XML file is used for bootstrapping.
	 * TRUE will get the XML file from the Muddleware server.
	 * 
	 * TODO: soft code Muddleware server and Muddleware server XPath.
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
	 * @see import org.geneview.core.manager.IGeneralManager
	 */
	protected final IGeneralManagerSingleton refOneForAllManager;
	
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
		logger.logMsg("===========================", LoggerType.STATUS);
		logger.logMsg("... Start Cerberus Core ...", LoggerType.STATUS);
		logger.logMsg("===========================", LoggerType.STATUS);
		logger.logMsg(" ", LoggerType.STATUS);
		
		//refViewManager = (IViewManager) refSingelton.getViewGLCanvasManager();
		
		refSWTGUIManager = refSingelton.getSWTGUIManager();		
	
		
		/**
		 * create the parser manager..
		 */
		refXmlParserManager = new XmlParserManager(refOneForAllManager);
		
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
	protected final boolean runUsingMuddleWare( final String sXPath ) {
		
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
			System.out.println("CerberusBootloader Can not connect to Muddleware server.");
			return false;
		}
		
		Operation operationSend = new Operation( OperationEnum.OP_ELEMENT_EXISTS );
		operationSend.setXPath( sXPath );
		
		IMessage sendMsg = new Message();
		sendMsg.addOperation( operationSend );
		
		IMessage receiveMsg = connection.sendReceiveMessage( sendMsg );
		
		if (( receiveMsg == null )||( receiveMsg.getNumOperations() < 1 )) {
			System.out.println("CerberusBootloader XPath does not exist, Muddleware server has no data on canvas settings.");
			connection.disconnect();
			return false;
		}
		
		if ( receiveMsg.getOperation( 0 ).getNodeString().equalsIgnoreCase( "true" ) ) {
			
			operationSend.setOperation( OperationEnum.OP_GET_ELEMENT );
			
			/* get configuration .. */
			sendMsg.setOperation( operationSend );
			receiveMsg = connection.sendReceiveMessage( sendMsg );
			connection.disconnect();
						
			IOperation op = receiveMsg.getOperation( 0 );					
			
			refXmlParserManager.parseXmlString( op.getXPath(), op.getNodeString() );
			
			System.out.println("CerberusBootloader PARSE using Muddleware done.");
			
		} else {
			System.out.println("CerberusBootloader Muddleware server has no data on canvas settings.");
			connection.disconnect();
			return false;
		}
		
		
		return true;
	}
	

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
		prototype.stop();
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
	 * @param fileName the sFileName to set
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
	public final boolean getBootstrappingViaMuddleware() {
		return this.bEnableBootstrapViaMuddleware;
	}

	
	public final IGeneralManagerSingleton getGeneralManager() {
	
		return refOneForAllManager;
	}

	
	/**
	 * @return the bEnableBootstrapViaMuddleware
	 */
	public final boolean isBEnableBootstrapViaMuddleware() {
	
		return bEnableBootstrapViaMuddleware;
	}

	
	/**
	 * @param enableBootstrapViaMuddleware the bEnableBootstrapViaMuddleware to set
	 */
	public final void setBootstrappingViaMuddleware(
			boolean enableBootstrapViaMuddleware) {
	
		bEnableBootstrapViaMuddleware = enableBootstrapViaMuddleware;
	}
	
	
	/**
	 * Start GeneView core.
	 * 
	 * @see CerberusBootloader#isRunning()
	 * @see CerberusBootloader#stop()
	 */
	public synchronized void run() {
		
		if ( this.refOneForAllManager == null ) {
			System.err.println( "FATAL ERROR!  " + 
					this.getClass().getSimpleName() + 
					".run() can not be executed, because no GeneralManager has bee created!");
			return;
		}
		
		/* make sure a logger was fetched.. */
		if ( logger == null ) {
			logger = refOneForAllManager.getSingelton().getLoggerManager();
		}
		
		/* use muddleware? */
		if ( getBootstrappingViaMuddleware() )
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
			refXmlParserManager.parseXmlFileByName( getXmlFileName() );		
		}

		logger.logMsg("  config loaded, start GUI ... ", LoggerType.STATUS);		
		refSWTGUIManager.runApplication();
		logger.logMsg("  config loaded ... [DONE]", LoggerType.STATUS);
		
		bIsRunning = true;
	}
	
	/**
	 * Stop the GeneView core and clean up all managers.
	 * 
	 * @see CerberusBootloader#run()
	 * @see CerberusBootloader#isRunning()
	 */
	public synchronized void stop() {
		if ( bIsRunning ) {
		
			if ( refOneForAllManager!= null ) {
				logger.logMsg("GeneView core   clean up...", LoggerType.STATUS);	
				refOneForAllManager.destroyOnExit();
				
				logger.logMsg("GeneView core   clean up... [done]\n", LoggerType.STATUS);		
				logger.logMsg("... Stop GeneView Core ...", LoggerType.STATUS);
				
				bIsRunning = false;
			}
		} else {
			logger.logMsg("GeneView Core was not running and can not be stopped!", LoggerType.ERROR_ONLY);
		}
	}
	
	/**
	 * Test if GeneView core is running.
	 * 
	 * @see CerberusBootloader#run()
	 * @see CerberusBootloader#stop()
	 * 
	 * @return TRUE if GeneView core is running
	 */
	public final synchronized boolean isRunning() {
		return bIsRunning;
	}
}
