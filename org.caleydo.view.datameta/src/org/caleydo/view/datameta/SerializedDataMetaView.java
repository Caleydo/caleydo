package org.caleydo.view.datameta;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.serialize.ASerializedView;

/**
 * Serialized data meta view.
 * 
 * @author Marc Streit
 */
@XmlRootElement
@XmlType
public class SerializedDataMetaView extends ASerializedView {

	/**
	 * Default constructor with default initialization
	 */
	public SerializedDataMetaView() {
	}

	public SerializedDataMetaView(String dataDomainType) {
		super(dataDomainType);
	}

	@Override
	public String getViewType() {
		return RcpDataMetaView.VIEW_TYPE;
	}
}
