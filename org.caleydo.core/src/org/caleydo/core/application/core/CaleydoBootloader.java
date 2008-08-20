package org.caleydo.core.application.core;

import java.io.IOException;
import java.util.logging.Level;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.ISWTGUIManager;
import org.caleydo.core.manager.IXmlParserManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;

/**
 * Basic Caleydo Bootloader.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
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
	 * Reference to XML parser. The parser does the bootstrapping of the Caleydo
	 * application using an XML input stream.
	 */
	protected IXmlParserManager xmlParserManager;

	/**
	 * Constructor.
	 */
	public CaleydoBootloader(boolean bIsStandalone)
	{
		generalManager = GeneralManager.get();
		generalManager.init(bIsStandalone);

		generalManager.getLogger().log(Level.CONFIG, "===========================");
		generalManager.getLogger().log(Level.CONFIG, "... Start Caleydo Core ...");
		generalManager.getLogger().log(Level.CONFIG, "===========================");

		swtGUIManager = generalManager.getSWTGUIManager();
		xmlParserManager = generalManager.getXmlParserManager();
	}

	/**
	 * Used by RCP to get access to the general manager.
	 */
	public IGeneralManager getGeneralManager()
	{
		return generalManager;
	}

	/**
	 * Set local XML file name.
	 * 
	 * @param fileName the sFileName to set
	 */
	public final void setXmlFileName(String sFileName)
	{
		this.sFileName = sFileName;
	}

	/**
	 * Get local XML file name.
	 * 
	 * @return XML file name or XPath
	 */
	public final String getXmlFileName()
	{
		return this.sFileName;
	}

	/**
	 * Test if Caleydo core is running.
	 * 
	 * @see CaleydoBootloader#start()
	 * @see CaleydoBootloader#stop()
	 * @return TRUE if Caleydo core is running
	 */
	public final synchronized boolean isRunning()
	{
		return bIsRunning;
	}

	/**
	 * Start Caleydo core. 
	 * 
	 */
	public synchronized void start()
	{
		try
		{
			parseXmlConfigFile(getXmlFileName());
			generalManager.getLogger().log(Level.INFO, "  config loaded, start GUI ... ");
		}
		catch (CaleydoRuntimeException gre)
		{
			generalManager.getLogger().log(Level.SEVERE,
					" loading GUI config failed! " + gre.toString());
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
			generalManager.getLogger()
					.log(Level.SEVERE, "run_SWT() failed. " + gre.toString());
			bIsRunning = false;
		}
		catch (Exception e)
		{
			generalManager.getLogger().log(Level.SEVERE,
					"run_SWT() caused system error. " + e.toString());
			bIsRunning = false;
		}
	}

	public synchronized boolean parseXmlConfigFile(final String sFileName)
	{
		try
		{
			xmlParserManager.parseXmlFileByName(sFileName);

			// generalManager.getCommandManager().readSerializedObjects(
			// "data/serialize_test.out");

			bIsRunning = true;
			return true;
		}
		catch (CaleydoRuntimeException gre)
		{
			generalManager.getLogger().log(Level.SEVERE,
					"run_parseXmlConfigFile(" + sFileName + ") failed. " + gre.toString());
			return false;
		}
		catch (Exception e)
		{
			generalManager.getLogger().log(
					Level.SEVERE,
					"run_parseXmlConfigFile(" + sFileName + ") caused system error. "
							+ e.toString());
			return false;
		}
	}

	/**
	 * Stop the Caleydo core and clean up all managers.
	 * 
	 * @see CaleydoBootloader#start()
	 * @see CaleydoBootloader#isRunning()
	 */
	public synchronized void stop()
	{		
		// generalManager.getCommandManager().writeSerializedObjects(
		// "data/serialize_test.out");

		if (bIsRunning)
		{
			if (generalManager != null)
			{
				generalManager.getLogger().log(Level.INFO, "Bye bye!");

				bIsRunning = false;
			}
		}
	}

	/**
	 * Run the stand alone Caleydo core application ..
	 */
	public static void main(String[] args)
	{

		CaleydoBootloader caleydo = new CaleydoBootloader(true);

		if (args.length > 0)
		{
			caleydo.setXmlFileName(args[0]);
		}

		caleydo.start();
		caleydo.stop();

		System.exit(0);

		return;
	}
}
