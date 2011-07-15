package org.caleydo.view.template;

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
public class SerializedTemplateView extends ASerializedView {

	/**
	 * Default constructor with default initialization
	 */
	public SerializedTemplateView() {
	}

	public SerializedTemplateView(String dataDomainType) {
		super(dataDomainType);
	}

	@Override
	public String getViewType() {
		return GLTemplate.VIEW_TYPE;
	}
}
