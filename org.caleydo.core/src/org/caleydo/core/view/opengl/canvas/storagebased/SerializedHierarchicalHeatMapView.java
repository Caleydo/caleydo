package org.caleydo.core.view.opengl.canvas.storagebased;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.serialize.ASerializedView;

/**
 * Serialized form of the {@link GLHierarchicalHeatMap} view.   
 * @author Werner Puff
 */
public class SerializedHierarchicalHeatMapView 
	extends ASerializedView {
	
	@Override
	public ECommandType getCreationCommandType() {
		return ECommandType.CMD_ID;
	}

	@Override
	public ViewFrustum getViewFrustum() {
		return null;
	}

}
