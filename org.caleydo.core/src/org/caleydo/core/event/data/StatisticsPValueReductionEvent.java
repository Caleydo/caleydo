package org.caleydo.core.event.data;

import java.util.ArrayList;

import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.event.AEvent;

public class StatisticsPValueReductionEvent
	extends AEvent {

	private ArrayList<DataContainer> dataContainers;

	public StatisticsPValueReductionEvent(ArrayList<DataContainer> dataContainers) {
		this.dataContainers = dataContainers;
	}

	public void setDataContainers(ArrayList<DataContainer> dataContainers) {
		this.dataContainers = dataContainers;
	}

	public ArrayList<DataContainer> getDataContainers() {
		return dataContainers;
	}

	@Override
	public boolean checkIntegrity() {
		if (dataContainers == null || dataContainers.size() == 0)
			return false;

		return true;
	}
}
