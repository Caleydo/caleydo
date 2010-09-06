package org.caleydo.view.datameta.creator;

import java.util.ArrayList;

import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.rcp.view.creator.ARCPViewCreator;
import org.caleydo.rcp.view.rcp.CaleydoRCPViewPart;
import org.caleydo.view.datameta.RcpDataMetaView;
import org.caleydo.view.datameta.SerializedDataMetaView;

public class ViewCreator extends ARCPViewCreator {

	public ViewCreator() {
		super(RcpDataMetaView.VIEW_ID);
	}

	@Override
	public CaleydoRCPViewPart createView(int parentContainerID, String label) {
		return new RcpDataMetaView();
	}

	@Override
	public ASerializedView createSerializedView() {
		return new SerializedDataMetaView();
	}

	@Override
	protected void registerDataDomains() {
		ArrayList<String> dataDomainTypes = new ArrayList<String>();

		dataDomainTypes.add("org.caleydo.datadomain.genetic");
		//dataDomainTypes.add("org.caleydo.datadomain.generic");

		DataDomainManager.get().getAssociationManager()
				.registerDatadomainTypeViewTypeAssociation(dataDomainTypes, RcpDataMetaView.VIEW_ID);
	}
}
