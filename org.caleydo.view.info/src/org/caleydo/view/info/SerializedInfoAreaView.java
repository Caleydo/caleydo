package org.caleydo.view.info;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.serialize.ASerializedTopLevelDataView;

/**
 * Serialized form of a parallel-coordinates-view.
 * 
 * @author Werner Puff
 * @deprecated This class should extend ASerializedView!
 */
@Deprecated
@XmlRootElement
@XmlType
public class SerializedInfoAreaView extends ASerializedTopLevelDataView {

	/**
	 * Default constructor with default initialization
	 */
	public SerializedInfoAreaView() {
	}

	public SerializedInfoAreaView(String dataDomainType) {
		super(dataDomainType);
	}

	@Override
	public String getViewType() {
		return RcpInfoAreaView.VIEW_TYPE;
	}

	@Override
	public String getViewClassType() {
		return RcpInfoAreaView.class.getName();
	}
}
