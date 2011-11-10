package org.caleydo.view.filter;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.serialize.ASerializedTopLevelDataView;

/**
 * Serialized filter view view.
 * 
 * @author Marc Streit
 */
@XmlRootElement
@XmlType
public class SerializedFilterView extends ASerializedTopLevelDataView {

	/**
	 * Default constructor with default initialization
	 */
	public SerializedFilterView() {
	}

	public SerializedFilterView(String dataDomainType) {
		super(dataDomainType);
	}

	@Override
	public String getViewType() {
		return RcpFilterView.VIEW_TYPE;
	}
}
