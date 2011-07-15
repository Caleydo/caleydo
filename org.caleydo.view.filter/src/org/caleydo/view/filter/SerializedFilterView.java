package org.caleydo.view.filter;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.serialize.ASerializedView;

/**
 * Serialized <INSERT VIEW NAME> view.
 * 
 * @author <INSERT_YOUR_NAME>
 */
@XmlRootElement
@XmlType
public class SerializedFilterView extends ASerializedView {

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
