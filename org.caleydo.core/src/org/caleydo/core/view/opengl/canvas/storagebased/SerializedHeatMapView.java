package org.caleydo.core.view.opengl.canvas.storagebased;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.EProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;

/**
 * Serialized form of a heatmap-view. 
 * @author Werner Puff
 */
@XmlRootElement
@XmlType
public class SerializedHeatMapView 
	extends ASerializedView {
	
	public static final String GUI_ID = "org.caleydo.rcp.views.opengl.GLHeatMapView";

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

	@Override
	public String getViewGUIID() {
		return GUI_ID;
	}
}
