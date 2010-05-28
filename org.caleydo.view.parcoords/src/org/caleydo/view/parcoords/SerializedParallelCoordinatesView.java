package org.caleydo.view.parcoords;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.EProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;

/**
 * Serialized form of a parallel-coordinates-view.
 * 
 * @author Werner Puff
 */
@XmlRootElement
@XmlType
public class SerializedParallelCoordinatesView extends ASerializedView {

	/**
	 * Default constructor with default initialization
	 */
	public SerializedParallelCoordinatesView() {
	}

	public SerializedParallelCoordinatesView(String dataDomainType) {
		super(dataDomainType);
	}

	@Override
	public ViewFrustum getViewFrustum() {
		ViewFrustum viewFrustum = new ViewFrustum(EProjectionMode.ORTHOGRAPHIC,
				0, 8, 0, 8, -20, 20);
		return viewFrustum;
	}

	@Override
	public String getViewType() {
		return GLParallelCoordinates.VIEW_ID;
	}
}
