package org.caleydo.core.view.opengl.canvas.storagebased;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;

/**
 * Serialized form of the {@link GLHierarchicalHeatMap} view.   
 * @author Werner Puff
 */
@XmlRootElement
@XmlType
public class SerializedHierarchicalHeatMapView 
	extends ASerializedView {
	
	@Override
	public ECommandType getCreationCommandType() {
		return ECommandType.CREATE_GL_TEXTURE_HEAT_MAP_3D;
	}

	@Override
	public ViewFrustum getViewFrustum() {
		return null;
	}

}
