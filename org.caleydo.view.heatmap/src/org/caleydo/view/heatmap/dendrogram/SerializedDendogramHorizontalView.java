package org.caleydo.view.heatmap.dendrogram;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.serialize.ASerializedTopLevelDataView;

/**
 * Serialized form of a dendrogram view.
 * 
 * @author Werner Puff
 */
@XmlRootElement
@XmlType
public class SerializedDendogramHorizontalView extends ASerializedTopLevelDataView {
	
	/**
	 * Default constructor with default initialization
	 */
	public SerializedDendogramHorizontalView() {
	}

	public SerializedDendogramHorizontalView(String dataDomainType) {
		super(dataDomainType);
	}

	@Override
	public String getViewType() {
		return GLDendrogram.VIEW_TYPE + ".horizontal";
	}

}
