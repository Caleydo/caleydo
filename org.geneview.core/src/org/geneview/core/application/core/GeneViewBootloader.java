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
import org.geneview.core.util.exception.GeneViewRuntimeException;
//import org.geneview.core.util.system.GeneViewInputStream;

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
 * Basic GeneView Bootloader, starts application either 
 * from local XML-file or fram Muddleware-Server.
 * 
 * Requires package: org.studierstube.net.protocol.muddleware.*
 * 
 * @author Michael Kalkusch
 *
 */
public class GeneViewBootloader
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
	 * The parser does the bootstrapping of the GeneView application 
	 * using an XML input stream.
	 */
	protected IXmlParserManager refXmlParserManager;
		
	/**
	 * Reference to Logger
	 */
	protected ILoggerManager logger;
	
	/**
	 * 
	 */
	public GeneViewBootloader()
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
		logger.logMsg("... Start GeneView Core ...", LoggerType.STATUS);
		logger.logMsg("===========================", LoggerType.STATUS);
		logger.logMsg(" ", LoggerType.STATUS);
		
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
		setXmlFileName( "data/bootstrap/bootstrap_sample_demo.xml" );
	}

	
	/**
	 * Connect to Muddelware server and get XML-configuration 
	 * from the Muddleware-Server.
	 * 
	 * Not tested yet!
	 * 
	 */
	protected final boolean runUsingMuddleWare( final String sXPath ) {
		
		logger.logMsg("   bootloader read data from muddleware server.. ",
				LoggerType.STATUS);
		
		/**
		 * initialize connection...
		 * connection is null if runUsingMuddleWare() called for the first time.
		 */
		if ( connection == null ) {
			connection = new ClientByteStreamHandler( null );
		}
		
		connection.setServerNameAndPort( "localhost", 20000 );
		
		if ( connection.connect() ) {
			logger.logMsg("GeneViewBootloader can not connect to Muddleware server.",
					LoggerType.MINOR_ERROR_XML);
			return false;
		}
		
		Operation operationSend = new Operation( OperationEnum.OP_ELEMENT_EXISTS );
		operationSend.setXPath( sXPath );
		
		IMessage sendMsg = new Message();
		sendMsg.addOperation( operationSend );
		
		IMessage receiveMsg = connection.sendReceiveMessage( sendMsg );
		
		if (( receiveMsg == null )||( receiveMsg.getNumOperations() < 1 )) {
			logger.logMsg("GeneViewBootloader XPath does not exist, Muddleware server has no data on canvas settings.",
					LoggerType.MINOR_ERROR_XML);
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
			
			System.out.println("GeneViewBootloader PARSE using Muddleware done.");
			
		} else {
			logger.logMsg("GeneViewBootloader Muddleware server has no data on canvas settings.",
					LoggerType.MINOR_ERROR_XML);
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
	 * @see org.geneview.core.application.core.GeneViewBootloader#setBootstrapViaMuddleware(boolean)
	 * @see org.geneview.core.application.core.GeneViewBootloader#getBootstrapViaMuddleware()
	 * 
	 * @see org.geneview.core.application.core.GeneViewBootloader#getXmlFileName()
	 * 
	 * @param fileName the sFileName to set
	 */
	public final void setXmlFileName( String sFileName ) {
		this.sFileName = sFileName;
	}
	
	/**
	 * Get local XML file name if config is read from local file or XPath if config is read from Muddleware server.
	 *  
	 * @see org.geneview.core.application.core.GeneViewBootloader#setBootstrapViaMuddleware(boolean)
	 * @see org.geneview.core.application.core.GeneViewBootloader#getBootstrapViaMuddleware()
	 * 
	 * @see org.geneview.core.application.core.GeneViewBootloader#setXmlFileName(String)
	 * 
	 * @return XML file name or XPath
	 */
	public final String getXmlFileName() {
		return this.sFileName;
	}
	
	/**
	 * 
	 * @see org.geneview.core.application.core.GeneViewBootloader#setXmlFileName(String)
	 * @see org.geneview.core.application.core.GeneViewBootloader#getXmlFileName()
	 * 
	 * @param bEnableBootstrapViaMuddleware TRUE for loading config via Muddleware or FALSE for loading config from local file.
	 */
	public final void setBootstrapViaMuddleware( boolean bEnableBootstrapViaMuddleware ) {
		this.bEnableBootstrapViaMuddleware = bEnableBootstrapViaMuddleware;
	}	
	
	/**
	 * Test if GeneView core is running.
	 * 
	 * @see GeneViewBootloader#run_SWT()
	 * @see GeneViewBootloader#stop()
	 * 
	 * @return TRUE if GeneView core is running
	 */
	public final synchronized boolean isRunning() {
		return bIsRunning;
	}
	
	public final IGeneralManagerSingleton getGeneralManager() {	
		return refOneForAllManager;
	}

	/**
	 * 
	 * @see org.geneview.core.application.core.GeneViewBootloader#setXmlFileName(String)
	 * @see org.geneview.core.application.core.GeneViewBootloader#getXmlFileName()
	 * 
	 * @return TRUE shows that config will be loaded via Muddleware or FALSE indicates that config is loaded from local file.
	 */
	public final boolean isBootstrapViaMuddlewareEnabled() {
	
		return bEnableBootstrapViaMuddleware;
	}

	
	/**
	 * Start GeneView core.
	 * Calls run_parseXmlConfigFile(String) with getXmlFileName() and starts SWT. 
	 * 
	 * @see GeneViewBootloader#getXmlFileName()
	 * @see GeneViewBootloader#run_parseXmlConfigFile(String)
	 * @see GeneViewBootloader#isRunning()
	 * @see GeneViewBootloader#stop()
	 */
	public synchronized void run_SWT() {
		
		run_parseXmlConfigFile( getXmlFileName() );		
		logger.logMsg("  config loaded, start GUI ... ", LoggerType.STATUS);
		
		try 
		{
			refSWTGUIManager.runApplication(); 			
			logger.logMsg("  config loaded ... [DONE]", LoggerType.STATUS);
		}
		catch (GeneViewRuntimeException gre)
		{
			logger.logMsg("run_SWT() failed. " +
					gre.toString() , LoggerType.MINOR_ERROR_XML);
			bIsRunning = false;
		}
		catch (Exception e) {
			logger.logMsg("run_SWT() cased system error. " +
					e.toString() , LoggerType.ERROR);
			bIsRunning = false;
		}	
	}
	
	/**
	 * does not Start SWT; intended for RCP and for parsing XML files after calling run_SWT() and starting the SWT canvas.
	 * 
	 * @see GeneViewBootloader#run_SWT()
	 * 
	 * @param fileName
	 * @return
	 */
	public synchronized boolean run_parseXmlConfigFile( final String fileName) {
		
		if ( this.refOneForAllManager == null ) {
			System.err.println( "FATAL ERROR!  " + 
					getClass().getSimpleName() + 
					".run_parseXmlConfigFile() can not be executed, because no GeneralManager has bee created!");
			return false;
		}
		
		try {
			parseXmlConfigFileLocalOrRemote(fileName);
			bIsRunning = true;
			return true;
		}
		catch (GeneViewRuntimeException gre)
		{
			logger.logMsg("run_parseXmlConfigFile(" + fileName + ") failed. " +
					gre.toString() , LoggerType.MINOR_ERROR_XML);
			return false;
		}
		catch (Exception e) {
			logger.logMsg("run_parseXmlConfigFile(" + fileName + ") cased system error. " +
					e.toString() , LoggerType.ERROR);
			return false;
		}			
	}
	
	protected void parseXmlConfigFileLocalOrRemote( final String fileName) {
		/* use muddleware? */
		if ( bEnableBootstrapViaMuddleware )
		{
			/**
			 * Load configuration from Muddleware server.
			 */
			logger.logMsg("  load config via Muddleware server ...", LoggerType.STATUS);			
			runUsingMuddleWare( "/geneview/workspace" );
		}
		else
		{
			/**
			 * Load configuration from local XML file.
			 */
			logger.logMsg("  load config via local XML file ... ", LoggerType.STATUS);			
			refXmlParserManager.parseXmlFileByName( getXmlFileName() );		
		}
	}
	
	/**
	 * Stop the GeneView core and clean up all managers.
	 * 
	 * @see GeneViewBootloader#run_SWT()
	 * @see GeneViewBootloader#isRunning()
	 */
	public synchronized void stop() {
		if ( bIsRunning ) 
		{		
			if ( refOneForAllManager!= null ) 
			{
				logger.logMsg("GeneView core   clean up...", LoggerType.STATUS);	
				refOneForAllManager.destroyOnExit();
				
				logger.logMsg("GeneView core   clean up... [done]\n", LoggerType.STATUS);		
				logger.logMsg("... Stop GeneView Core ...", LoggerType.STATUS);
				
				bIsRunning = false;
			}
		} 
		else 
		{
			if ( logger != null ) 
			{
				logger.logMsg("GeneView core was not running and can not be stopped!", LoggerType.ERROR);
			}
			else 
			{
				System.err.println("GeneView core was not running and can not be stopped!");
			}
		}
	}
	
	
	/**
	 * Run the GeneView core application ..
	 */
	public static void main(String[] args) 
	{
		GeneViewBootloader prototype = new GeneViewBootloader();
		
		if ( args.length > 0 ) 
		{
			prototype.setXmlFileName( args[0] ); 	
		}
		
		prototype.run_SWT();
		prototype.stop();
	}

}
