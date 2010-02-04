package org.caleydo.view.tabular;

import org.caleydo.core.manager.usecase.EDataDomain;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;

/**
 * Serialized form of a tabular-browser view.
 * 
 * @author Werner Puff
 */
public class SerializedTabularDataView
	extends ASerializedView {

	public SerializedTabularDataView() {
	}

	public SerializedTabularDataView(EDataDomain dataDomain) {
		super(dataDomain);
	}

	@Override
	public ViewFrustum getViewFrustum() {
		return null;
	}

	@Override
	public String getViewType() {
		return TabularDataView.VIEW_ID;
	}

}
