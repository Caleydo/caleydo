package org.caleydo.view.treemap;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.serialize.ASerializedView;

/**
 * Serialized form of a treemap view.
 * 
 * @author Marc Streit
 */
@XmlRootElement
@XmlType
public class SerializedTreeMapView extends ASerializedView {

	/**
	 * Default constructor with default initialization
	 */
	public SerializedTreeMapView() {
	}

	public SerializedTreeMapView(String dataDomainType) {
		super(dataDomainType);
	}

	@Override
	public String getViewType() {
		return GLTreeMap.VIEW_ID;
	}
}
