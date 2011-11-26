package org.caleydo.view.kaplanmeier;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.serialize.ASerializedTopLevelDataView;

/**
 * Serialized Kaplan Meier view.
 * 
 * @author Marc Streit
 */
@XmlRootElement
@XmlType
public class SerializedKaplanMeierView extends ASerializedTopLevelDataView {

	/**
	 * Default constructor with default initialization
	 */
	public SerializedKaplanMeierView() {
	}

	@Override
	public String getViewType() {
		return GLKaplanMeier.VIEW_TYPE;
	}
}
