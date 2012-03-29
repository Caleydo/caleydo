package org.caleydo.view.datawindows;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;

/**
 * Serialized form of a hyperbolic-view.
 * 
 * @author Marc Streit
 */
@XmlRootElement
@XmlType
public class SerializedHyperbolicView extends ASerializedView {

	/**
	 * Default constructor with default initialization
	 */
	public SerializedHyperbolicView() {
	}

	@Override
	public ViewFrustum getViewFrustum() {
		ViewFrustum viewFrustum = new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC, 0,
				8, 0, 8, -20, 20);
		return viewFrustum;
	}

	@Override
	public String getViewType() {
		return GLHyperbolic.VIEW_TYPE;
	}

	@Override
	public String getViewClassType() {
		return GLHyperbolic.class.getName();
	}
}
