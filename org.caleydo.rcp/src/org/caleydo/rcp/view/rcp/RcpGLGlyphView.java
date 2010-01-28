package org.caleydo.rcp.view.rcp;

import org.caleydo.core.manager.usecase.EDataDomain;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.GLGlyph;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.SerializedGlyphView;
import org.eclipse.swt.widgets.Composite;

public class RcpGLGlyphView extends ARcpGLViewPart {

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
		SerializedGlyphView serializedView = new SerializedGlyphView(
				EDataDomain.CLINICAL_DATA);
		return serializedView;
	}

	@Override
	public String getViewGUIID() {
		return GLGlyph.VIEW_ID;
	}
}
