package org.caleydo.view.pathway;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.view.base.rcp.ARcpGLViewPart;

public class RcpGLPathwayView extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpGLPathwayView() {
		super();
	}

	@Override
	public ASerializedView createDefaultSerializedView() {
		SerializedPathwayView serializedView = new SerializedPathwayView(
				dataDomain);
		return serializedView;
	}

	@Override
	public String getViewGUIID() {
		return GLPathway.VIEW_ID;
	}

}