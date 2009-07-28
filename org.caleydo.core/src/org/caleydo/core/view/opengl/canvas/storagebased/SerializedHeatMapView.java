package org.caleydo.core.view.opengl.canvas.storagebased;

import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.view.opengl.camera.EProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.serialize.ASerializedView;

/**
 * Serialized form of a heatmap-view. 
 * @author Werner Puff
 */
@XmlType
public class SerializedHeatMapView 
	extends ASerializedView {
	
	/**
	 * Default constructor with default initialization
	 */
	public SerializedHeatMapView() {

	}

	@Override
	public ECommandType getCreationCommandType() {
		return ECommandType.CREATE_GL_HEAT_MAP_3D;
	}

	@Override
	public ViewFrustum getViewFrustum() {
		ViewFrustum viewFrustum = new ViewFrustum(EProjectionMode.ORTHOGRAPHIC, 0, 8, 0, 8, -20, 20);
		return viewFrustum;
	}

}
