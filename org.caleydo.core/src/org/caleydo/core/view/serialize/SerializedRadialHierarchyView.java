package org.caleydo.core.view.serialize;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.view.opengl.camera.ViewFrustum;

/**
 * Serialized form of the radial hierarchy view.
 * 
 * @author Christian Partl
 */
public class SerializedRadialHierarchyView
	extends ASerializedView {

	private int iMaxDisplayedHierarchyDepth;

	@Override
	public ECommandType getCreationCommandType() {
		return ECommandType.CREATE_GL_RADIAL_HIERARCHY;
	}

	@Override
	public ViewFrustum getViewFrustum() {
		return null;
	}

	public int getMaxDisplayedHierarchyDepth() {
		return iMaxDisplayedHierarchyDepth;
	}

	public void setMaxDisplayedHierarchyDepth(int iMaxDisplayedHierarchyDepth) {
		this.iMaxDisplayedHierarchyDepth = iMaxDisplayedHierarchyDepth;
	}

}
