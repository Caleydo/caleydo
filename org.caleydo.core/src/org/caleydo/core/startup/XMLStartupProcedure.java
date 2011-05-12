package org.caleydo.core.startup;

import org.caleydo.core.manager.GeneralManager;

/**
 * Startup procedure for XML bootstrapping.
 * 
 * @author Marc Streit
 */
public class XMLStartupProcedure
	extends AStartupProcedure {

	private String fileName;

	public void setXMLFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public void init(ApplicationInitData appInitData) {

		super.init(appInitData);

		GeneralManager.get().getXmlParserManager().parseXmlFileByName(fileName);
	}

	@Override
	public void addDefaultStartViews() {
		// TODO Auto-generated method stub

	}
}
