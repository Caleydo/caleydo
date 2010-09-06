package org.caleydo.view.genesearch.creator;

import java.util.ArrayList;

import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.rcp.view.creator.ARCPViewCreator;
import org.caleydo.rcp.view.rcp.CaleydoRCPViewPart;
import org.caleydo.view.genesearch.RcpGeneSearchView;
import org.caleydo.view.genesearch.SerializedGeneSearchView;
import org.caleydo.view.genesearch.toolbar.GeneSearchToolBarContent;

public class ViewCreator extends ARCPViewCreator {

	public ViewCreator() {
		super(RcpGeneSearchView.VIEW_ID);
	}

	@Override
	public ASerializedView createSerializedView() {

		return new SerializedGeneSearchView();
	}

	@Override
	public Object createToolBarContent() {
		return new GeneSearchToolBarContent();
	}

	@Override
	public CaleydoRCPViewPart createView(int parentContainerID, String label) {
		return new RcpGeneSearchView();
	}

	@Override
	protected void registerDataDomains() {
		ArrayList<String> dataDomainTypes = new ArrayList<String>();

		dataDomainTypes.add("org.caleydo.datadomain.genetic");

		DataDomainManager
				.get()
				.getAssociationManager()
				.registerDatadomainTypeViewTypeAssociation(dataDomainTypes,
						RcpGeneSearchView.VIEW_ID);
	}
}
