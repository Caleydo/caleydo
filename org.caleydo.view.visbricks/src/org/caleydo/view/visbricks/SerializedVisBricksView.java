package org.caleydo.view.visbricks;

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
public class SerializedVisBricksView extends ASerializedView {

	/**
	 * Default constructor with default initialization
	 */
	public SerializedVisBricksView() {
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
