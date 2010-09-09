package org.caleydo.view.bookmark;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;

/**
 * Serialized form of a bookmark view.
 * 
 * @author Marc Streit
 */
@XmlRootElement
@XmlType
public class SerializedBookmarkView extends ASerializedView {

	/**
	 * Default constructor with default initialization
	 */
	public SerializedBookmarkView() {
	}

	public SerializedBookmarkView(String dataDomainType) {
		super(dataDomainType);
	}

	@Override
	public String getViewType() {
		return GLBookmarkView.VIEW_ID;
	}
}
