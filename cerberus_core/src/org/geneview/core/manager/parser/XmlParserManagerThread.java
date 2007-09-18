/**
 * 
 */
package org.geneview.core.manager.parser;

import org.geneview.core.manager.IGeneralManager;


/**
 * @author java
 *
 */
public class XmlParserManagerThread extends XmlParserManager
		implements Runnable {

	private String sXmlFileName;
	
	/**
	 * @param generalManager
	 * @param bUseCascadingHandler
	 */
	public XmlParserManagerThread(IGeneralManager generalManager) {

		super(generalManager);
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {

		this.parseXmlFileByName( sXmlFileName );

	}

	
	/**
	 * @return Returns the sXmlFileName.
	 */
	public synchronized final String getXmlFileName() {
	
		return sXmlFileName;
	}

	
	/**
	 * @param xmlFileName The sXmlFileName to set.
	 */
	public synchronized final void setXmlFileName(String xmlFileName) {
	
		sXmlFileName = xmlFileName;
	}

}
