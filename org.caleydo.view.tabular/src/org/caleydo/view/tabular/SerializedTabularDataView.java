package org.caleydo.view.tabular;

import org.caleydo.core.serialize.ASerializedTopLevelDataView;

/**
 * Serialized form of a tabular-browser view.
 * 
 * @author Werner Puff
 */
public class SerializedTabularDataView extends ASerializedTopLevelDataView {

	public SerializedTabularDataView() {
	}

	public SerializedTabularDataView(String dataDomainType) {
		super(dataDomainType);
	}

	@Override
	public String getViewType() {
		return TabularDataView.VIEW_TYPE;
	}

}
