package org.caleydo.core.serialize;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * This class is a serialized form reduced to hold only the view-id. It should only be used until all views
 * have their own serialized form class.
 * 
 * @author Werner Puff
 */
@XmlRootElement
@XmlType
public class SerializedDummyView
	extends ASerializedTopLevelDataView {

	public SerializedDummyView() {
	}

	public SerializedDummyView(String dataDomainType) {
		super(dataDomainType);
	}

	@Override
	public String getViewType() {
		return null;
	}

}
