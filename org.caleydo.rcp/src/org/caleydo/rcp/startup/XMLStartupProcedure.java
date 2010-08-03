package org.caleydo.rcp.startup;

import org.caleydo.core.manager.general.GeneralManager;

public class XMLStartupProcedure
	extends AStartupProcedure {

	private String fileName;

	public void setXMLFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public void init() {
		GeneralManager.get().getXmlParserManager().parseXmlFileByName(fileName);
	}

	@Override
	public void addDefaultStartViews() {
		// TODO Auto-generated method stub
		
	}
}
