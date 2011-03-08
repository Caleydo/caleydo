package org.caleydo.view.heatmap.heatmap;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.ECameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;

/**
 * Serialized form of a heatmap-view.
 * 
 * @author Werner Puff
 */
@XmlRootElement
@XmlType
public class SerializedHeatMapView extends ASerializedView {

	/**
	 * Default constructor with default initialization
	 */
	public SerializedHeatMapView() {
		
	}

	public SerializedHeatMapView(String dataDomainType) {
		super(dataDomainType);
	}

	@Override
	public ViewFrustum getViewFrustum() {
		ViewFrustum viewFrustum = new ViewFrustum(ECameraProjectionMode.ORTHOGRAPHIC, 0, 8, 0,
				8, -20, 20);
		return viewFrustum;
	}

	@Override
	public String getViewType() {
		return GLHeatMap.VIEW_ID;
	}
	
	
	@Override
	public String getViewClassType() {
		return GLHeatMap.class.getName();
	}
}
