package org.caleydo.util.r.view;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;

/**
 * Serialized form of a statistics view.
 * 
 * @author Marc Streit
 */
public class SerializedStatisticsView extends ASerializedView {

	public SerializedStatisticsView() {
	}

	public SerializedStatisticsView(String dataDomainType) {
		super(dataDomainType);
	}

	@Override
	public ViewFrustum getViewFrustum() {
		return null;
	}

	@Override
	public String getViewType() {
		return StatisticsView.VIEW_ID;
	}

}
