package org.caleydo.view.tabular.creator;

import org.caleydo.core.manager.view.creator.ASWTViewCreator;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.AView;
import org.caleydo.view.tabular.SerializedTabularDataView;
import org.caleydo.view.tabular.TabularDataView;

public class ViewCreator extends ASWTViewCreator {

	public ViewCreator(String viewType) {
		super(viewType);
	}

	@Override
	public AView createView(int parentContainerID, String label) {

		return new TabularDataView(parentContainerID, label);
	}

	@Override
	public ASerializedView createSerializedView() {

		return new SerializedTabularDataView();
	}
}
