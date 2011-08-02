package org.caleydo.view.visbricks.brick;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.serialize.ASerializedView;

/**
 * Serialized VisBricks view.
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
@XmlRootElement
@XmlType
public class SerializedBrickView extends ASerializedView {

	/**
	 * Default constructor with default initialization
	 */
	public SerializedBrickView() {
	}

	@Override
	public String getViewType() {
		return GLBrick.VIEW_TYPE;
	}

	@Override
	public String getViewClassType() {
		return GLBrick.class.getName();
	}
}
