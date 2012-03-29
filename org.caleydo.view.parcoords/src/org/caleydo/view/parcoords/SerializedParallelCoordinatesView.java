package org.caleydo.view.parcoords;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.caleydo.core.serialize.ASerializedTopLevelDataView;

/**
 * Serialized form of a parallel-coordinates-view.
 * 
 * @author Werner Puff
 */
@XmlRootElement
@XmlType
public class SerializedParallelCoordinatesView extends ASerializedTopLevelDataView {

	/**
	 * Default constructor with default initialization
	 */
	public SerializedParallelCoordinatesView() {
	}

	public SerializedParallelCoordinatesView(String dataDomainType) {
		super(dataDomainType);
	}

	@Override
	public String getViewType() {
		return GLParallelCoordinates.VIEW_TYPE;
	}

	@Override
	public String getViewClassType() {
		return GLParallelCoordinates.class.getName();
	}
}
