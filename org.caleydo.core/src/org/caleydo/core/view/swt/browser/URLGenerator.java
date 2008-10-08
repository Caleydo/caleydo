package org.caleydo.core.view.swt.browser;

import org.caleydo.core.manager.general.GeneralManager;

public class URLGenerator
{
	public String createURL(EBrowserQueryType eBrowserQueryType, int iDavidID)
	{
		String sURL = eBrowserQueryType.getBrowserQueryStringPrefix();
		
		sURL += GeneralManager.get().getIDMappingManager()
			.getID(eBrowserQueryType.getMappingType(), iDavidID);
		
		return sURL;
	}
}
