package org.caleydo.view.filterpipeline;

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
public class SerializedFilterPipelineView extends ASerializedView {

	/**
	 * Default constructor with default initialization
	 */
	public SerializedFilterPipelineView() {
	}

	public SerializedFilterPipelineView(String dataDomainType) {
		super(dataDomainType);
	}

	@Override
	public String getViewType() {
		return GLFilterPipeline.VIEW_ID;
	}
}
