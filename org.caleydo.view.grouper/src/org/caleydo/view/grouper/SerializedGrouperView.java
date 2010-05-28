package org.caleydo.view.grouper;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;

/**
 * Serialized form of the remote-rendering view (bucket).
 * 
 * @author Werner Puff
 * @author Alexander Lex
 */
@XmlRootElement
@XmlType
public class SerializedGrouperView extends ASerializedView {

	public SerializedGrouperView() {
	}

	public SerializedGrouperView(String dataDomainType) {
		super(dataDomainType);
	}

	@Override
	public ViewFrustum getViewFrustum() {
		return null;
	}

	@Override
	public String getViewType() {
		return GLGrouper.VIEW_ID;
	}

}
