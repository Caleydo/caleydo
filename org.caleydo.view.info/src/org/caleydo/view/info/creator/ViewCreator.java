package org.caleydo.view.info.creator;

import java.util.ArrayList;

import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.rcp.view.creator.ARCPViewCreator;
import org.caleydo.rcp.view.rcp.CaleydoRCPViewPart;
import org.caleydo.view.info.RcpInfoAreaView;
import org.caleydo.view.info.SerializedInfoView;
import org.caleydo.view.info.toolbar.InfoToolBarContent;

public class ViewCreator extends ARCPViewCreator {

	public ViewCreator() {
		super(RcpInfoAreaView.VIEW_ID);
	}
	@Override
	public ASerializedView createSerializedView() {

		return new SerializedInfoView();
	}

	@Override
	public Object createToolBarContent() {
		return new InfoToolBarContent();
	}

	@Override
	public CaleydoRCPViewPart createView(int parentContainerID, String label) {
		return new RcpInfoAreaView();
	}

	@Override
	protected void registerDataDomains() {
		ArrayList<String> dataDomainTypes = new ArrayList<String>();

		dataDomainTypes.add("org.caleydo.datadomain.genetic");
		dataDomainTypes.add("org.caleydo.datadomain.generic");

		DataDomainManager.getInstance().getAssociationManager()
				.registerDatadomainTypeViewTypeAssociation(dataDomainTypes, RcpInfoAreaView.VIEW_ID);
	}
}
