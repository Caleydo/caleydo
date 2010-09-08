package org.caleydo.util.r.creator;

import java.util.ArrayList;

import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.rcp.view.creator.ARCPViewCreator;
import org.caleydo.rcp.view.rcp.CaleydoRCPViewPart;
import org.caleydo.util.r.view.RcpStatisticsView;
import org.caleydo.util.r.view.SerializedStatisticsView;
import org.caleydo.util.r.view.StatisticsView;

public class ViewCreator extends ARCPViewCreator {

	public ViewCreator() {
		super(StatisticsView.VIEW_ID);
	}

	@Override
	public CaleydoRCPViewPart createView(int parentContainerID, String label) {
		return new RcpStatisticsView();
	}

	@Override
	public ASerializedView createSerializedView() {

		return new SerializedStatisticsView();
	}
	
	@Override
	protected void registerDataDomains() {
		ArrayList<String> dataDomainTypes = new ArrayList<String>();
		dataDomainTypes.add("org.caleydo.datadomain.genetic");
		dataDomainTypes.add("org.caleydo.datadomain.generic");
		dataDomainTypes.add("org.caleydo.datadomain.clinical");

		DataDomainManager.get().getAssociationManager()
				.registerDatadomainTypeViewTypeAssociation(dataDomainTypes, StatisticsView.VIEW_ID);
	}
}
