package org.caleydo.rcp.view.opengl;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.SerializedGlyphView;
import org.eclipse.swt.widgets.Composite;

public class RcpGLGlyphView
	extends ARcpGLViewPart {

	public static final String ID = SerializedGlyphView.GUI_ID;

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
		createGLEventListener(initSerializedView, glCanvas.getID());
	}

	@Override
	public ASerializedView createDefaultSerializedView() {
		SerializedGlyphView serializedView = new SerializedGlyphView();
		return serializedView;
	}

	@Override
	public String getViewGUIID() {
		return ID;
	}

}
