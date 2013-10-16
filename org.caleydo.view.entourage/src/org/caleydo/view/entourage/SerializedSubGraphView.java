package org.caleydo.view.entourage;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.serialize.ASerializedMultiTablePerspectiveBasedView;

/**
 * Serialized {@link GLEntourage} view.
 * 
 * @author Christian Partl
 * 
 */
@XmlRootElement
@XmlType
public class SerializedSubGraphView extends
		ASerializedMultiTablePerspectiveBasedView {

	public SerializedSubGraphView() {

	}

	@Override
	public String getViewType() {
		return GLEntourage.VIEW_TYPE;
	}

}
