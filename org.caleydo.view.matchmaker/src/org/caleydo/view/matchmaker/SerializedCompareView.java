package org.caleydo.view.matchmaker;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;

/**
 * Serialized form of the compare view.
 * 
 * @author Marc Streit
 */
@XmlRootElement
@XmlType
public class SerializedCompareView extends ASerializedView {

	public SerializedCompareView() {
	}

	public SerializedCompareView(String dataDomainType) {
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
