package org.caleydo.view.heatmap.heatmap;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.serialize.ASerializedTopLevelDataView;
import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;

/**
 * Serialized form of a heatmap view.
 * 
 * @author Werner Puff
 */
@XmlRootElement
@XmlType
public class SerializedHeatMapView extends ASerializedTopLevelDataView {

	/**
	 * Default constructor with default initialization
	 */
	public SerializedHeatMapView() {

	}

	public SerializedHeatMapView(String dataDomainID) {
		super(dataDomainID);
	}

	@Override
	public ViewFrustum getViewFrustum() {
		ViewFrustum viewFrustum = new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC, 0,
				8, 0, 8, -20, 20);
		return viewFrustum;
	}

	@Override
	public String getViewType() {
		return GLHeatMap.VIEW_TYPE;
	}

	@Override
	public String getViewClassType() {
		return GLHeatMap.class.getName();
	}
}
