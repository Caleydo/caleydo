package org.caleydo.view.visbricks;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.serialize.ASerializedTopLevelDataView;

/**
 * Serialized VisBricks view.
 * 
 * @author Alexander Lex
 */
@XmlRootElement
@XmlType
public class SerializedVisBricksView extends ASerializedTopLevelDataView {

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
		return GLVisBricks.VIEW_TYPE;
	}

	@Override
	public String getViewClassType() {
		return GLVisBricks.class.getName();
	}
}
