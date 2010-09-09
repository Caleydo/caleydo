package org.caleydo.view.selectionbrowser;

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
public class SerializedSelectionBrowserView extends ASerializedView {

	/**
	 * Default constructor with default initialization
	 */
	public SerializedSelectionBrowserView() {
	}

	public SerializedSelectionBrowserView(String dataDomainType) {
		super(dataDomainType);
	}

	@Override
	public String getViewType() {
		return RcpSelectionBrowserView.VIEW_ID;
	}
}
