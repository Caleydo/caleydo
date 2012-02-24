package org.caleydo.view.visbricks20;

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
public class SerializedVisBricks20View extends ASerializedView {

	/**
	 * Default constructor with default initialization
	 */
	public SerializedVisBricks20View() {
	}

	@Override
	public String getViewType() {
		return GLVisBricks20.VIEW_TYPE;
	}
}
