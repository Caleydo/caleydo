package org.caleydo.view.info;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;

/**
 * Serialized <INSERT VIEW NAME> view.
 * 
 * @author Alexander Lex
 */
@XmlRootElement
@XmlType
public class SerializedInfoView extends ASerializedView {

	/**
	 * Default constructor with default initialization
	 */
	public SerializedInfoView() {
	}

	public SerializedInfoView(String dataDomainType) {
		super(dataDomainType);
	}

	@Override
	public ViewFrustum getViewFrustum() {
		ViewFrustum viewFrustum = new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC, 0, 8, 0,
				8, -20, 20);
		return viewFrustum;
	}

	@Override
	public String getViewType() {
		return RcpInfoAreaView.VIEW_ID;
	}
}
