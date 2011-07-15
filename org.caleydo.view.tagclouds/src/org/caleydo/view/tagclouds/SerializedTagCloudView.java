package org.caleydo.view.tagclouds;

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
public class SerializedTagCloudView extends ASerializedView {

	/**
	 * Default constructor with default initialization
	 */
	public SerializedTagCloudView() {
	}

	public SerializedTagCloudView(String dataDomainType) {
		super(dataDomainType);
	}

	@Override
	public String getViewType() {
		return GLTagCloud.VIEW_TYPE;
	}
}
