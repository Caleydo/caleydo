package org.caleydo.view.pathway;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.view.base.rcp.ARcpGLViewPart;

public class RcpGLPathwayView extends ARcpGLViewPart {

	public static final String ID = SerializedPathwayView.GUI_ID;

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
		return ID;
	}

}