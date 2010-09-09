package org.caleydo.view.treemap;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.serialize.ASerializedView;

/**
 * Serialized form of a hierarchical treemap view.
 * 
 * @author Marc Streit
 */
@XmlRootElement
@XmlType
public class SerializedHierarchicalTreeMapView extends ASerializedView {

	/**
	 * Default constructor with default initialization
	 */
	public SerializedHierarchicalTreeMapView() {
	}

	public SerializedHierarchicalTreeMapView(String dataDomainType) {
		super(dataDomainType);
	}
	
	@Override
	public String getViewType() {
		return GLHierarchicalTreeMap.VIEW_ID;
	}
}
