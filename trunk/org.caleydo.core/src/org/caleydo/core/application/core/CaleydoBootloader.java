package org.caleydo.core.application.core;

import org.caleydo.core.bridge.gui.IGUIBridge;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.ISWTGUIManager;
import org.caleydo.core.manager.IXmlParserManager;
import org.caleydo.core.manager.general.GeneralManager;

/**
 * Basic Caleydo Bootloader.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class CaleydoBootloader {
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
	 * Reference to XML parser. The parser does the bootstrapping of the Caleydo application using an XML
	 * input stream.
	 */
	protected IXmlParserManager xmlParserManager;

	/**
	 * Constructor.
	 */
	public CaleydoBootloader(IGUIBridge externalGUIBridge) {
		generalManager = GeneralManager.get();
		generalManager.init(externalGUIBridge);
		init();
	}

	private void init() {
		swtGUIManager = generalManager.getSWTGUIManager();
		xmlParserManager = generalManager.getXmlParserManager();
	}

	/**
	 * Used by RCP to get access to the general manager.
	 */
	public IGeneralManager getGeneralManager() {
		return generalManager;
	}

	/**
	 * Set local XML file name.
	 * 
	 * @param fileName
	 *            the sFileName to set
	 */
	public final void setXmlFileName(String sFileName) {
		this.sFileName = sFileName;
	}

	/**
	 * Start Caleydo core.
	 */
	public void start() {

		// Do nothing if no XML input file is specified
		if (sFileName == null || sFileName == "")
			return;

		xmlParserManager.parseXmlFileByName(sFileName);
	}
}
