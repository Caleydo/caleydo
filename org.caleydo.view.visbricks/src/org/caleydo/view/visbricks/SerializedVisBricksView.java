package org.caleydo.view.visbricks;

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
public class SerializedVisBricksView extends ASerializedView {

	/**
	 * Default constructor with default initialization
	 */
	public SerializedVisBricksView() {
	}

	public SerializedVisBricksView(String dataDomainType) {
		super(dataDomainType);
	}

	@Override
	public String getViewType() {
		return GLVisBricks.VIEW_ID;
	}
}
