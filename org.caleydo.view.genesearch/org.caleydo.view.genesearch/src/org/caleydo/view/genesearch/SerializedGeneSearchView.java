package org.caleydo.view.genesearch;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.EProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;

/**
 * Serialized <INSERT VIEW NAME> view.
 * 
 * @author <INSERT_YOUR_NAME>
 */
@XmlRootElement
@XmlType
public class SerializedGeneSearchView extends ASerializedView {

	/**
	 * Default constructor with default initialization
	 */
	public SerializedGeneSearchView() {
	}

	public SerializedGeneSearchView(String dataDomainType) {
		super(dataDomainType);
	}

	@Override
	public ViewFrustum getViewFrustum() {
return null;
	}

	@Override
	public String getViewType() {
		return RcpGeneSearchView.VIEW_ID;
	}
}
