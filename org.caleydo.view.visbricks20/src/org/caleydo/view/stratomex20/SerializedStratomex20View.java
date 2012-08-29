package org.caleydo.view.stratomex20;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.caleydo.core.serialize.ASerializedMultiTablePerspectiveBasedView;

/**
 * Serialized <INSERT VIEW NAME> view.
 * 
 * @author <INSERT_YOUR_NAME>
 */
@XmlRootElement
@XmlType
public class SerializedStratomex20View extends ASerializedMultiTablePerspectiveBasedView {

	/**
	 * Default constructor with default initialization
	 */
	public SerializedStratomex20View() {
	}

	@Override
	public String getViewType() {
		return GLStratomex20.VIEW_TYPE;
	}
}
