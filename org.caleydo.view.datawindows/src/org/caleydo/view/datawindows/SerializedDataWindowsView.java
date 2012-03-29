package org.caleydo.view.datawindows;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.caleydo.core.serialize.ASerializedView;

/**
 * Serialized form of a data windows view.
 * 
 * @author Marc Streit
 */
@XmlRootElement
@XmlType
public class SerializedDataWindowsView extends ASerializedView {

	/**
	 * Default constructor with default initialization
	 */
	public SerializedDataWindowsView() {
	}

	@Override
	public String getViewType() {
		return GLDataWindows.VIEW_TYPE;
	}
}
