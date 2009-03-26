package org.caleydo.core.view.ser;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.view.opengl.camera.EProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;

/**
 * Serialized form of a parallel-coordinates-view. 
 * @author Werner Puff
 */
public class SerializedParallelCoordinatesView 
	implements ISerializedView {
	
	/**
	 * Default constructor with default initialization
	 */
	public SerializedParallelCoordinatesView() {

	}

	@Override
	public ECommandType getCreationCommandType() {
		return ECommandType.CREATE_GL_PARALLEL_COORDINATES_GENE_EXPRESSION;
	}

	@Override
	public ViewFrustum getViewFrustum() {
		ViewFrustum viewFrustum = new ViewFrustum(EProjectionMode.ORTHOGRAPHIC, 0, 8, 0, 8, -20, 20);
		return viewFrustum;
	}

}
