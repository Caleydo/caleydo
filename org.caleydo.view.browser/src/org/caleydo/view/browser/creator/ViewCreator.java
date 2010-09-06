package org.caleydo.view.browser.creator;

import java.util.ArrayList;

import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.view.creator.ASWTViewCreator;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.AView;
import org.caleydo.view.browser.GenomeHTMLBrowser;
import org.caleydo.view.browser.SerializedHTMLBrowserView;

public class ViewCreator extends ASWTViewCreator {

	public ViewCreator() {
		super(GenomeHTMLBrowser.VIEW_ID);
	}

	@Override
	public AView createView(int parentContainerID) {

		return new GenomeHTMLBrowser(parentContainerID);
	}

	@Override
	public ASerializedView createSerializedView() {

		return new SerializedHTMLBrowserView();
	}

	@Override
	protected void registerDataDomains() {
		ArrayList<String> dataDomainTypes = new ArrayList<String>();
		dataDomainTypes.add("org.caleydo.datadomain.genetic");

		DataDomainManager
				.get()
				.getAssociationManager()
				.registerDatadomainTypeViewTypeAssociation(dataDomainTypes,
						GenomeHTMLBrowser.VIEW_ID);
	}
}
