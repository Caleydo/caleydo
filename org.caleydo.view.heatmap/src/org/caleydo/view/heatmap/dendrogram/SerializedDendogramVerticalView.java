package org.caleydo.view.heatmap.dendrogram;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.serialize.ASerializedView;

/**
 * Serialized form of a dengrogram view.
 * 
 * @author Werner Puff
 */
@XmlRootElement
@XmlType
public class SerializedDendogramVerticalView extends ASerializedView {

	/**
	 * Default constructor with default initialization
	 */
	public SerializedDendogramVerticalView() {
	}

	public SerializedDendogramVerticalView(String dataDomainType) {
		super(dataDomainType);
	}

	@Override
	public String getViewType() {
		return GLDendrogram.VIEW_ID + ".vertical";
	}

}
