package org.caleydo.core.view.serialize;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.view.opengl.camera.EProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;

/**
 * Serialized form of a heatmap-view. 
 * @author Werner Puff
 */
public class SerializedGlyphView 
	extends ASerializedView {
	
	/**
	 * Default constructor with default initialization
	 */
	public SerializedGlyphView() {

	}

	@Override
	public ECommandType getCreationCommandType() {
		return ECommandType.CREATE_GL_GLYPH;
	}

	@Override
	public ViewFrustum getViewFrustum() {
		ViewFrustum viewFrustum = new ViewFrustum(EProjectionMode.ORTHOGRAPHIC, 0, 2, 0, 2, -20, 20);
		return viewFrustum;
	}

}
