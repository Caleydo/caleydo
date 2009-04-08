package org.caleydo.rcp.views.opengl;

import java.util.ArrayList;

import org.caleydo.core.command.ECommandType;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Composite;

public class GLHistogramView
	extends AGLViewPart {
	public static final String ID = "org.caleydo.rcp.views.GLHistogramView";

	/**
	 * Constructor.
	 */
	public GLHistogramView() {
		super();
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		createGLCanvas();
		createGLEventListener(ECommandType.CREATE_GL_HISTOGRAM, glCanvas.getID(), true);
	}

	public static void createToolBarItems(int iViewID) {
		alToolbar = new ArrayList<IAction>();
	}
}