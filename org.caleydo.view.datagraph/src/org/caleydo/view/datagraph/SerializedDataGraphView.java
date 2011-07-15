package org.caleydo.view.datagraph;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.serialize.ASerializedView;

/**
 * Serialized VisBricks view.
 * 
 * @author Alexander Lex
 */
@XmlRootElement
@XmlType
public class SerializedDataGraphView extends ASerializedView {

	/**
	 * Default constructor with default initialization
	 */
	public SerializedDataGraphView() {
	}

	public SerializedDataGraphView(String dataDomainType) {
		super(dataDomainType);
	}

	@Override
	public String getViewType() {
		return GLDataGraph.VIEW_TYPE;
	}

	@Override
	public String getViewClassType() {
		return GLDataGraph.class.getName();
	}
}
