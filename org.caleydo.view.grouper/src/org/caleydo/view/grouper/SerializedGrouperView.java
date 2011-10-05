package org.caleydo.view.grouper;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.serialize.ASerializedTopLevelDataView;

/**
 * Serialized form of the remote-rendering view (bucket).
 * 
 * @author Werner Puff
 * @author Alexander Lex
 * @author Marc Streit
 */
@XmlRootElement
@XmlType
public class SerializedGrouperView extends ASerializedTopLevelDataView {

	public SerializedGrouperView() {
	}

	public SerializedGrouperView(String dataDomainType) {
		super(dataDomainType);
	}

	@Override
	public String getViewType() {
		return GLGrouper.VIEW_TYPE;
	}

}
