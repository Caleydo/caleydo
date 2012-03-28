package org.caleydo.view.linearizedpathway;

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
public class SerializedLinearizedPathwayView extends ASerializedView {

	/**
	 * Default constructor with default initialization
	 */
	public SerializedLinearizedPathwayView() {
	}

	@Override
	public String getViewType() {
		return GLLinearizedPathway.VIEW_TYPE;
	}
	
	@Override
	public String getViewClassType() {
		return GLLinearizedPathway.class.getName();
	}
}
