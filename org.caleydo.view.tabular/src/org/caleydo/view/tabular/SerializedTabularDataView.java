package org.caleydo.view.tabular;

import org.caleydo.core.serialize.ASerializedView;

/**
 * Serialized form of a tabular-browser view.
 * 
 * @author Werner Puff
 */
public class SerializedTabularDataView extends ASerializedView {

	public SerializedTabularDataView() {
	}

	public SerializedTabularDataView(String dataDomainType) {
		super(dataDomainType);
	}

	@Override
	public String getViewType() {
		return TabularDataView.VIEW_ID;
	}

}
