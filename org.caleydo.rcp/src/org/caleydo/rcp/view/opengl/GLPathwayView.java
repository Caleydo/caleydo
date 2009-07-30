package org.caleydo.rcp.view.opengl;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.canvas.storagebased.SerializedPathwayView;


public class GLPathwayView
	extends AGLViewPart {
	public static final String ID = "org.caleydo.rcp.views.opengl.GLPathwayView";

	/**
	 * Constructor.
	 */
	public GLPathwayView() {
		super();
	}

	@Override
	public ASerializedView createDefaultSerializedView() {
		SerializedPathwayView serializedView = new SerializedPathwayView();

		serializedView.setViewGUIID(getViewGUIID());

		return serializedView;
	}

	@Override
	public String getViewGUIID() {
		return ID;
	}

}