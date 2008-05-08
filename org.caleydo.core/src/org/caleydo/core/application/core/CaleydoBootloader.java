package org.caleydo.core.application.core;

import java.util.logging.Level;

import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.ISWTGUIManager;
import org.caleydo.core.manager.IXmlParserManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.studierstube.net.protocol.muddleware.ClientByteStreamHandler;
import org.studierstube.net.protocol.muddleware.IMessage;
import org.studierstube.net.protocol.muddleware.IOperation;
import org.studierstube.net.protocol.muddleware.Message;
import org.studierstube.net.protocol.muddleware.Operation;
import org.studierstube.net.protocol.muddleware.OperationEnum;

/**
 * Basic Caleydo Bootloader, starts application either 
 * from local XML-file or fram Muddleware-Server.
 * 
 * Requires package: org.studierstube.net.protocol.muddleware.*
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class CaleydoBootloader
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
	 * Reference to general manager which is a singleton.
	 * 
	 * @see import org.caleydo.core.manager.IGeneralManager
	 */
	protected final IGeneralManager generalManager;
	
	/**
	 * This reference is used for starting the GUI
	 */
	protected ISWTGUIManager refSWTGUIManager;
	
	/**
	 * Reference to XML parser. 
	 * The parser does the bootstrapping of the Caleydo application 
	 * using an XML input stream.
	 */
	protected IXmlParserManager refXmlParserManager;
	
	/**
	 * Constructor.
	 */
	public CaleydoBootloader()
	{
		/**
		 * In order to use SWT call setStateSWT( true ) to enabel SWT support!
		 */
		generalManager = new GeneralManager();

		generalManager.getLogger().log(Level.CONFIG, "===========================");
		generalManager.getLogger().log(Level.CONFIG, "... Start Caleydo Core ...");
		generalManager.getLogger().log(Level.CONFIG, "===========================");

		refSWTGUIManager = generalManager.getSWTGUIManager();
		refXmlParserManager = generalManager.getXmlParserManager();
		
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

	public IGeneralManager getGeneralManager() 
	{
		return generalManager;
	}
	
	/**
	 * Connect to Muddelware server and get XML-configuration 
	 * from the Muddleware-Server.
	 * 
	 * Not tested yet!
	 * 
	 */
	protected final boolean runUsingMuddleWare( final String sXPath ) {
		
		generalManager.getLogger().log(Level.CONFIG, "bootloader read data from muddleware server.. ");
		
		/**
		 * initialize connection...
		 * connection is null if runUsingMuddleWare() called for the first time.
		 */
		if ( connection == null ) {
			connection = new ClientByteStreamHandler( null );
		}
		
		connection.setServerNameAndPort( "localhost", 20000 );
		
		if ( connection.connect() ) {
			generalManager.getLogger().log(Level.WARNING, 
					"CaleydoBootloader can not connect to Muddleware server.");
			return false;
		}
		
		Operation operationSend = new Operation( OperationEnum.OP_ELEMENT_EXISTS );
		operationSend.setXPath( sXPath );
		
		IMessage sendMsg = new Message();
		sendMsg.addOperation( operationSend );
		
		IMessage receiveMsg = connection.sendReceiveMessage( sendMsg );
		
		if (( receiveMsg == null )||( receiveMsg.getNumOperations() < 1 )) {
			generalManager.getLogger().log(Level.WARNING, 
					"CaleydoBootloader XPath does not exist, Muddleware server has no data on canvas settings.");
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
			
			generalManager.getLogger().log(Level.CONFIG, "CaleydoBootloader PARSE using Muddleware done.");
			
		} else {
			generalManager.getLogger().log(Level.CONFIG, "CaleydoBootloader Muddleware server has no data on canvas settings.");
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
	 * @see org.caleydo.core.application.core.CaleydoBootloader#setBootstrapViaMuddleware(boolean)
	 * @see org.caleydo.core.application.core.CaleydoBootloader#getBootstrapViaMuddleware()
	 * 
	 * @see org.caleydo.core.application.core.CaleydoBootloader#getXmlFileName()
	 * 
	 * @param fileName the sFileName to set
	 */
	public final void setXmlFileName( String sFileName ) {
		this.sFileName = sFileName;
	}
	
	/**
	 * Get local XML file name if config is read from local file or XPath if config is read from Muddleware server.
	 *  
	 * @see org.caleydo.core.application.core.CaleydoBootloader#setBootstrapViaMuddleware(boolean)
	 * @see org.caleydo.core.application.core.CaleydoBootloader#getBootstrapViaMuddleware()
	 * 
	 * @see org.caleydo.core.application.core.CaleydoBootloader#setXmlFileName(String)
	 * 
	 * @return XML file name or XPath
	 */
	public final String getXmlFileName() {
		return this.sFileName;
	}
	
	/**
	 * 
	 * @see org.caleydo.core.application.core.CaleydoBootloader#setXmlFileName(String)
	 * @see org.caleydo.core.application.core.CaleydoBootloader#getXmlFileName()
	 * 
	 * @param bEnableBootstrapViaMuddleware TRUE for loading config via Muddleware or FALSE for loading config from local file.
	 */
	public final void setBootstrapViaMuddleware( boolean bEnableBootstrapViaMuddleware ) {
		this.bEnableBootstrapViaMuddleware = bEnableBootstrapViaMuddleware;
	}	
	
	/**
	 * Test if Caleydo core is running.
	 * 
	 * @see CaleydoBootloader#run_SWT()
	 * @see CaleydoBootloader#stop()
	 * 
	 * @return TRUE if Caleydo core is running
	 */
	public final synchronized boolean isRunning() {
		return bIsRunning;
	}
	
	/**
	 * 
	 * @see org.caleydo.core.application.core.CaleydoBootloader#setXmlFileName(String)
	 * @see org.caleydo.core.application.core.CaleydoBootloader#getXmlFileName()
	 * 
	 * @return TRUE shows that config will be loaded via Muddleware or FALSE indicates that config is loaded from local file.
	 */
	public final boolean isBootstrapViaMuddlewareEnabled() {
	
		return bEnableBootstrapViaMuddleware;
	}

	
	/**
	 * Start Caleydo core.
	 * Calls run_parseXmlConfigFile(String) with getXmlFileName() and starts SWT. 
	 * 
	 * @see CaleydoBootloader#getXmlFileName()
	 * @see CaleydoBootloader#run_parseXmlConfigFile(String)
	 * @see CaleydoBootloader#isRunning()
	 * @see CaleydoBootloader#stop()
	 */
	public synchronized void run_SWT() {
		
		try 
		{
			run_parseXmlConfigFile( getXmlFileName() );		
			generalManager.getLogger().log(Level.CONFIG, "  config loaded, start GUI ... ");
		}
		catch (CaleydoRuntimeException gre)
		{
			generalManager.getLogger().log(Level.CONFIG, " loading GUI config failed! " +gre.toString());
			bIsRunning = false;
			return;
		}
		
		try 
		{
			refSWTGUIManager.runApplication(); 			
			generalManager.getLogger().log(Level.CONFIG, "  config loaded ... [DONE]");
		}
		catch (CaleydoRuntimeException gre)
		{
			generalManager.getLogger().log(Level.CONFIG, "run_SWT() failed. " +gre.toString());
			bIsRunning = false;
		}
		catch (Exception e) {
			generalManager.getLogger().log(Level.SEVERE, "run_SWT() caused system error. " +e.toString());
			bIsRunning = false;
		}	
	}
	
	/**
	 * does not Start SWT; intended for RCP and for parsing XML files after calling run_SWT() and starting the SWT canvas.
	 * 
	 * @see CaleydoBootloader#run_SWT()
	 * 
	 * @param fileName
	 * @return
	 */
	public synchronized boolean run_parseXmlConfigFile( final String fileName) {
		
		if (generalManager == null ) {
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
		catch (CaleydoRuntimeException gre)
		{
			generalManager.getLogger().log(Level.SEVERE, "run_parseXmlConfigFile(" + fileName + ") failed. " +
					gre.toString());
			return false;
		}
		catch (Exception e) {
			generalManager.getLogger().log(Level.SEVERE, "run_parseXmlConfigFile(" + fileName + ") caused system error. " +
					e.toString());
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
			generalManager.getLogger().log(Level.CONFIG, "load config via Muddleware server ...");			
			runUsingMuddleWare( "/caleydo/workspace" );
		}
		else
		{
			/**
			 * Load configuration from local XML file.
			 */
			generalManager.getLogger().log(Level.CONFIG, "load config via local XML file ... ");			
			refXmlParserManager.parseXmlFileByName( getXmlFileName() );		
		}
		
	}
	
	/**
	 * Stop the Caleydo core and clean up all managers.
	 * 
	 * @see CaleydoBootloader#run_SWT()
	 * @see CaleydoBootloader#isRunning()
	 */
	public synchronized void stop() {
		if ( bIsRunning ) 
		{		
			if ( generalManager!= null ) 
			{
				generalManager.getLogger().log(Level.INFO, "Caleydo core clean up...");	
				
				generalManager.destroyOnExit();
				
				generalManager.getLogger().log(Level.INFO, "[done]\n");		
				generalManager.getLogger().log(Level.INFO, "Bye bye!");
				
				bIsRunning = false;
			}
		} 
		else 
		{
			if ( generalManager != null ) 
			{
				generalManager.getLogger().log(Level.SEVERE, "Caleydo core was not running and can not be stopped!");
			}
			else 
			{
				System.err.println("Caleydo core was not running and can not be stopped!");
			}
		}
	}
	
	
	/**
	 * Run the Caleydo core application ..
	 */
	public static void main(String[] args) 
	{
		CaleydoBootloader prototype = new CaleydoBootloader();
		
		if ( args.length > 0 ) 
		{
			prototype.setXmlFileName( args[0] ); 	
		}
		
		prototype.run_SWT();
		prototype.stop();
		
		System.exit(0);

		return;
	}

}
