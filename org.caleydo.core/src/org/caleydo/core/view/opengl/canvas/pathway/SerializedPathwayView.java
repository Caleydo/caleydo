package org.caleydo.core.view.opengl.canvas.pathway;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.EProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;

/**
 * Serialized form of a pathway-view. 
 * @author Werner Puff
 */
@XmlRootElement
@XmlType
public class SerializedPathwayView 
	extends ASerializedView {
	
	/** id of the pathway in caleydo's pathway library, -1 for unknown pathway */
	private int pathwayID;

	/**
	 * Default constructor with default initialization
	 */
	public SerializedPathwayView() {
		pathwayID = -1;
	}

	@Override
	public ECommandType getCreationCommandType() {
		return ECommandType.CREATE_GL_PATHWAY_3D;
	}

	/**
	 * Gets the pathwayId of this SerializedPathwayView
	 * @return pathwayId
	 */
	public int getPathwayID() {
		return pathwayID;
	}

	/**
	 * Sets the pathwayId of this SerlializedPathwayView
	 * @param pathwayId a valid pathwayId as in Caleydo's pathway library or -1 for an unknow or unitialized pathway
	 */
	public void setPathwayID(int pathwayId) {
		this.pathwayID = pathwayId;
	}

	@Override
	public ViewFrustum getViewFrustum() {
		ViewFrustum viewFrustum = new ViewFrustum(EProjectionMode.ORTHOGRAPHIC, -4, 4, -4, 4, -20, 20);
		return viewFrustum;
	}

}
