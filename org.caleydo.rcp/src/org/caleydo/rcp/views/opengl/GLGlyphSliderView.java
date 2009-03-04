package org.caleydo.rcp.views.opengl;

import java.util.ArrayList;

import org.caleydo.core.command.ECommandType;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Composite;

public class GLGlyphSliderView
	extends AGLViewPart
{
	public static final String ID = "org.caleydo.rcp.views.opengl.GLGlyphSliderView";

	/**
	 * Constructor.
	 */
	public GLGlyphSliderView()
	{
		super();
	}

	@Override
	public void createPartControl(Composite parent)
	{
		super.createPartControl(parent);

		createGLCanvas();
		createGLEventListener(ECommandType.CREATE_GL_GLYPH_SLIDER, glCanvas.getID(), true);
	}

	public static void createToolBarItems(int iViewID)
	{
		alToolbar = new ArrayList<IAction>();
		return;

	}
}