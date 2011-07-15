package org.caleydo.view.genesearch;

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
public class SerializedGeneSearchView extends ASerializedView {

	/**
	 * Default constructor with default initialization
	 */
	public SerializedGeneSearchView() {
	}

	public SerializedGeneSearchView(String dataDomainType) {
		super(dataDomainType);
	}

	@Override
	public String getViewType() {
		return RcpGeneSearchView.VIEW_TYPE;
	}
}
