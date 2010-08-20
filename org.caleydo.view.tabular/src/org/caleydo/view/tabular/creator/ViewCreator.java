package org.caleydo.view.tabular.creator;

import java.util.ArrayList;

import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.view.creator.ASWTViewCreator;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.AView;
import org.caleydo.view.tabular.SerializedTabularDataView;
import org.caleydo.view.tabular.TabularDataView;

public class ViewCreator extends ASWTViewCreator {

	public ViewCreator() {
		super(TabularDataView.VIEW_ID);
	}

	@Override
	public AView createView(int parentContainerID, String label) {

		return new TabularDataView(parentContainerID, label);
	}

	@Override
	public ASerializedView createSerializedView() {

		return new SerializedTabularDataView();
	}

	@Override
	protected void registerDataDomains() {
		ArrayList<String> dataDomainTypes = new ArrayList<String>();
		dataDomainTypes.add("org.caleydo.datadomain.genetic");
		dataDomainTypes.add("org.caleydo.datadomain.generic");
		dataDomainTypes.add("org.caleydo.datadomain.clinical");

		DataDomainManager
				.getInstance()
				.getAssociationManager()
				.registerDatadomainTypeViewTypeAssociation(dataDomainTypes,
						TabularDataView.VIEW_ID);
	}
}
