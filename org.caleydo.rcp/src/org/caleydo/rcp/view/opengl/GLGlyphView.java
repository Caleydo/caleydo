package org.caleydo.rcp.view.opengl;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.serialize.SerializedGlyphView;
import org.eclipse.swt.widgets.Composite;

public class GLGlyphView
	extends AGLViewPart {
	public static final String ID = "org.caleydo.rcp.views.opengl.GLGlyphView";

	public static int viewCount = 0;

	/**
	 * Constructor.
	 */
	public GLGlyphView() {
		super();
		viewCount++;
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		createGLCanvas();
		createGLEventListener(initSerializedView, glCanvas.getID());
	}

	@Override
	public ASerializedView createDefaultSerializedView() {
		SerializedGlyphView serializedView = new SerializedGlyphView();
		serializedView.setViewGUIID(getViewGUIID());
		return serializedView;
	}

	@Override
	public String getViewGUIID() {
		return ID;
	}

}
