package org.caleydo.view.histogram;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.serialize.ASerializedTopLevelDataView;

/**
 * Serialized form of the remote-rendering view (bucket).
 * 
 * @author Werner Puff
 */
@XmlRootElement
@XmlType
public class SerializedHistogramView extends ASerializedTopLevelDataView {

	public SerializedHistogramView() {
	}

	public SerializedHistogramView(String dataDomainID) {
		super(dataDomainID);
	}

	@Override
	public String getViewType() {
		return GLHistogram.VIEW_TYPE;
	}

}
