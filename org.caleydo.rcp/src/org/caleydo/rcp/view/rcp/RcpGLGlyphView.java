package org.caleydo.rcp.view.rcp;

import org.caleydo.core.serialize.ASerializedView;
import org.eclipse.swt.widgets.Composite;

public class RcpGLGlyphView
	extends ARcpGLViewPart {

	public static int viewCount = 0;

	/**
	 * Constructor.
	 */
	public RcpGLGlyphView() {
		super();
		viewCount++;
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		createGLCanvas();
		createGLView(initSerializedView, glCanvas.getID());
	}

	@Override
	public ASerializedView createDefaultSerializedView() {
		SerializedGlyphView serializedView = new SerializedGlyphView("org.caleydo.datadomain.clinical");
		return serializedView;
	}

	@Override
	public String getViewGUIID() {
		return GLGlyph.VIEW_ID;
	}
}
