package org.caleydo.view.genesearch;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.serialize.ASerializedTopLevelDataView;

/**
 * Serialized gene search view.
 * 
 * @author Marc Streit
 */
@XmlRootElement
@XmlType
public class SerializedGeneSearchView extends ASerializedTopLevelDataView {

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
