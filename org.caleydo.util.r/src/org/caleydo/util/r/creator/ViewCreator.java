package org.caleydo.util.r.creator;

import java.util.ArrayList;

import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.view.creator.ASWTViewCreator;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.AView;
import org.caleydo.util.r.view.SerializedStatisticsView;
import org.caleydo.util.r.view.StatisticsView;

public class ViewCreator extends ASWTViewCreator {

	public ViewCreator() {
		super(StatisticsView.VIEW_ID);
	}

	@Override
	public AView createView(int parentContainerID) {

		return new StatisticsView(parentContainerID);
	}

	@Override
	public ASerializedView createSerializedView() {

		return new SerializedStatisticsView();
	}
	
	@Override
	protected void registerDataDomains() {
		ArrayList<String> dataDomainTypes = new ArrayList<String>();
		dataDomainTypes.add("org.caleydo.datadomain.genetic");

		DataDomainManager.getInstance().getAssociationManager()
				.registerDatadomainTypeViewTypeAssociation(dataDomainTypes, StatisticsView.VIEW_ID);
	}

}
