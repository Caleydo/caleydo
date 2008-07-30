package org.caleydo.core.application.core;

import java.util.logging.Level;

import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.ISWTGUIManager;
import org.caleydo.core.manager.IXmlParserManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.util.exception.CaleydoRuntimeException;

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
	 * File name of the local XML file
	 */
	private String sFileName;
	
	/**
	 * Reference to general manager which is a singleton.
	 * 
	 * @see import org.caleydo.core.manager.IGeneralManager
	 */
	protected final IGeneralManager generalManager;
	
	/**
	 * This reference is used for starting the GUI
	 */
	protected ISWTGUIManager swtGUIManager;
	
	/**
	 * Reference to XML parser. 
	 * The parser does the bootstrapping of the Caleydo application 
	 * using an XML input stream.
	 */
	protected IXmlParserManager xmlParserManager;
	
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

		swtGUIManager = generalManager.getSWTGUIManager();
		xmlParserManager = generalManager.getXmlParserManager();
		
		/**
		 * Register additional SaxParserHandler here:
		 * <br>
		 * sample code:<br>
		 * <br>
		 * AnySaxHandler myNewHandler = <br>
		 *   new CommandSaxHandler( generalManager, xmlParserManager );<br>
		 * <br>  
		 * xmlParserManager.registerAndInitSaxHandler( myNewHandler ); <br>
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
	 * Test if Caleydo core is running.
	 * 
	 * @see CaleydoBootloader#runGUI()
	 * @see CaleydoBootloader#stop()
	 * 
	 * @return TRUE if Caleydo core is running
	 */
	public final synchronized boolean isRunning() {
		return bIsRunning;
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
	public synchronized void runGUI() {
		
		try 
		{
			run_parseXmlConfigFile( getXmlFileName() );		
			generalManager.getLogger().log(Level.INFO, "  config loaded, start GUI ... ");
		}
		catch (CaleydoRuntimeException gre)
		{
			generalManager.getLogger().log(Level.SEVERE, " loading GUI config failed! " +gre.toString());
			bIsRunning = false;
			return;
		}
		
		try 
		{
			swtGUIManager.runApplication(); 			
			generalManager.getLogger().log(Level.INFO, "  config loaded ... [DONE]");
		}
		catch (CaleydoRuntimeException gre)
		{
			generalManager.getLogger().log(Level.SEVERE, "run_SWT() failed. " +gre.toString());
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
	 * @see CaleydoBootloader#runGUI()
	 * 
	 * @param fileName
	 * @return
	 */
	public synchronized boolean run_parseXmlConfigFile( final String fileName) {
		
		if (generalManager == null ) 
		{
			System.err.println( "FATAL ERROR!  " + 
					getClass().getSimpleName() + 
					".run_parseXmlConfigFile() can not be executed, because no GeneralManager has bee created!");
			return false;
		}
		
		try {
			parseXmlConfigFileLocalOrRemote(fileName);
			
//			generalManager.getCommandManager().readSerializedObjects("data/serialize_test.out");
			
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

		/**
		 * Load configuration from local XML file.
		 */
		generalManager.getLogger().log(Level.CONFIG, "load config via local XML file ... ");			
		xmlParserManager.parseXmlFileByName( getXmlFileName() );		
	}
	
	/**
	 * Stop the Caleydo core and clean up all managers.
	 * 
	 * @see CaleydoBootloader#runGUI()
	 * @see CaleydoBootloader#isRunning()
	 */
	public synchronized void stop() 
	{		
//		generalManager.getCommandManager().writeSerializedObjects("data/serialize_test.out");

		if ( bIsRunning ) 
		{		
			if ( generalManager!= null ) 
			{
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
		CaleydoBootloader caleydo = new CaleydoBootloader();
		
		if ( args.length > 0 ) 
		{
			caleydo.setXmlFileName( args[0] ); 	
		}
		
		caleydo.runGUI();
		caleydo.stop();
		
		System.exit(0);

		return;
	}

}
