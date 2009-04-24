package org.caleydo.core.view.swt.browser;

import java.util.Set;

import org.caleydo.core.manager.general.GeneralManager;

public class URLGenerator {
	public String createURL(EBrowserQueryType eBrowserQueryType, int iDavidID) {
		String sURL = eBrowserQueryType.getBrowserQueryStringPrefix();

		if (!eBrowserQueryType.getMappingType().isMultiMap()) {
			sURL +=
				GeneralManager.get().getIDMappingManager()
					.getID(eBrowserQueryType.getMappingType(), iDavidID);
		}
		else {
			// TODO: only the first is handled in the case of multiple
			// how should we handle this here?
			sURL +=
				(String) ((Set<Object>) GeneralManager.get().getIDMappingManager().getMultiID(
					eBrowserQueryType.getMappingType(), iDavidID)).toArray()[0];
		}

		return sURL;
	}
}
