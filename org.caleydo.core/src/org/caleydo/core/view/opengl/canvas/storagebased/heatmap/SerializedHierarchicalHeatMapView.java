package org.caleydo.core.view.opengl.canvas.storagebased.heatmap;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.manager.usecase.EDataDomain;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.EProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;

/**
 * Serialized form of the {@link GLHierarchicalHeatMap} view.
 * 
 * @author Werner Puff
 */
@XmlRootElement
@XmlType
public class SerializedHierarchicalHeatMapView
	extends ASerializedView {

	public SerializedHierarchicalHeatMapView() {
	}

	public SerializedHierarchicalHeatMapView(EDataDomain dataDomain) {
		super(dataDomain);
	}

	public static final String GUI_ID = "org.caleydo.rcp.views.opengl.GLHierarchicalHeatMapView";

	@Override
	public ECommandType getCreationCommandType() {
		return ECommandType.CREATE_GL_TEXTURE_HEAT_MAP_3D;
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
