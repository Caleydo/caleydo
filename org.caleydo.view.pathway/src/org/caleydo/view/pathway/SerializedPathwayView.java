package org.caleydo.view.pathway;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.caleydo.core.serialize.ASerializedTopLevelDataView;
import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;

/**
 * Serialized form of a pathway-view.
 * 
 * @author Werner Puff
 */
@XmlRootElement
@XmlType
public class SerializedPathwayView extends ASerializedTopLevelDataView {

	/** id of the pathway in caleydo's pathway library, -1 for unknown pathway */
	private int pathwayID;

	/**
	 * Default constructor with default initialization
	 */
	public SerializedPathwayView() {
	}

	public SerializedPathwayView(String dataDomainType) {
		super(dataDomainType);
		pathwayID = -1;
	}

	/**
	 * Gets the pathwayId of this SerializedPathwayView
	 * 
	 * @return pathwayId
	 */
	public int getPathwayID() {
		return pathwayID;
	}

	/**
	 * Sets the pathwayId of this SerlializedPathwayView
	 * 
	 * @param pathwayId
	 *            a valid pathwayId as in Caleydo's pathway library or -1 for an
	 *            unknown or uninitialized pathway
	 */
	public void setPathwayID(int pathwayId) {
		this.pathwayID = pathwayId;
	}

	@Override
	public ViewFrustum getViewFrustum() {
		ViewFrustum viewFrustum = new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC, -4,
				4, -4, 4, -20, 20);
		return viewFrustum;
	}

	@Override
	public String getViewType() {
		return GLPathway.VIEW_TYPE;
	}

	@Override
	public String getViewClassType() {
		return GLPathway.class.getName();
	}
}
