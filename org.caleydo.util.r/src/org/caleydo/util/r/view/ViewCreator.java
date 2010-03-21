package org.caleydo.util.r.view;

import org.caleydo.core.manager.view.creator.ASWTViewCreator;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.AView;

public class ViewCreator extends ASWTViewCreator {

	public ViewCreator(String viewType) {
		super(viewType);
	}

	@Override
	public AView createView(int parentContainerID, String label) {

		return new StatisticsView(parentContainerID, label);
	}

	@Override
	public ASerializedView createSerializedView() {

		return new SerializedStatisticsView();
	}
}
