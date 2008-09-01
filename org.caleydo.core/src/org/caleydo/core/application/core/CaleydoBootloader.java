package org.caleydo.core.application.core;

import java.util.logging.Level;
import org.caleydo.core.bridge.gui.IGUIBridge;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.ISWTGUIManager;
import org.caleydo.core.manager.IXmlParserManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.util.exception.CaleydoRuntimeException;

/**
 * Basic Caleydo Bootloader.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class CaleydoBootloader
{
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

		generalManager.getLogger().log(Level.INFO, "Start Caleydo Core");

		swtGUIManager = generalManager.getSWTGUIManager();
		xmlParserManager = generalManager.getXmlParserManager();

		init();
	}

	/**
	 * Constructor.
	 */
	public CaleydoBootloader(boolean bIsStandalone, IGUIBridge externalGUIBridge)
	{
		generalManager = GeneralManager.get();
		generalManager.init(bIsStandalone, externalGUIBridge);

		init();
	}

	private void init()
	{
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
	 * Start Caleydo core.
	 * 
	 */
	public synchronized void start()
	{
		try
		{
			parseXmlConfigFile(getXmlFileName());

			if (generalManager.isStandalone())
			{
				swtGUIManager.runApplication();
			}
			
			// Start OpenGL rendering
			generalManager.getViewGLCanvasManager().startAnimator();
		}
		catch (CaleydoRuntimeException gre)
		{

			generalManager.getLogger()
					.log(Level.SEVERE, "Problems during startup. " + gre.toString());
			
			generalManager.getGUIBridge().closeApplication();
		}
	}

	public void parseXmlConfigFile(final String sFileName)
	{
		xmlParserManager.parseXmlFileByName(sFileName);

		// generalManager.getCommandManager().readSerializedObjects(
		// "data/serialize_test.out");

		// generalManager.getCommandManager().writeSerializedObjects(
		// "data/serialize_test.out");

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

		System.exit(0);

		return;
	}
}
