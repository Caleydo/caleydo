package org.caleydo.view.matchmaker;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;

/**
 * Serialized form of the matchmaker view.
 * 
 * @author Marc Streit
 */
@XmlRootElement
@XmlType
public class SerializedMatchmakerView extends ASerializedView {

	public SerializedMatchmakerView() {
	}

	public SerializedMatchmakerView(String dataDomainType) {
		super(dataDomainType);
	}

	@Override
	public ViewFrustum getViewFrustum() {
		return null;
	}

	@Override
	public String getViewType() {
		return GLMatchmaker.VIEW_ID;
	}

}
