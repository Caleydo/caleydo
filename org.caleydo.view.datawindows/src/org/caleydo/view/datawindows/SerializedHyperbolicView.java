package org.caleydo.view.datawindows;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.EProjectionMode;
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

	public SerializedHyperbolicView(String dataDomainType) {
		super(dataDomainType);
	}

	@Override
	public ViewFrustum getViewFrustum() {
		ViewFrustum viewFrustum = new ViewFrustum(EProjectionMode.ORTHOGRAPHIC, 0, 8, 0,
				8, -20, 20);
		return viewFrustum;
	}

	@Override
	public String getViewType() {
		return GLHyperbolic.VIEW_ID;
	}
}
