package org.caleydo.rcp.views.opengl;

import org.caleydo.core.command.ECommandType;
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
		createGLEventListener(ECommandType.CREATE_GL_GLYPH, glCanvas.getID(), true);
	}
}
