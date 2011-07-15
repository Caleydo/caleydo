package org.caleydo.view.histogram;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.serialize.ASerializedView;

/**
 * Serialized form of the remote-rendering view (bucket).
 * 
 * @author Werner Puff
 */
@XmlRootElement
@XmlType
public class SerializedHistogramView extends ASerializedView {

	public SerializedHistogramView() {
	}

	public SerializedHistogramView(String dataDomainType) {
		super(dataDomainType);
	}

	@Override
	public String getViewType() {
		return GLHistogram.VIEW_TYPE;
	}

}
