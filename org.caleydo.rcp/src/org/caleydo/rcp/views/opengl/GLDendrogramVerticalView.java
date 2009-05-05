package org.caleydo.rcp.views.opengl;

import java.util.ArrayList;

import org.caleydo.core.command.ECommandType;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Composite;

public class GLDendrogramVerticalView
	extends AGLViewPart {
	public static final String ID = "org.caleydo.rcp.views.opengl.GLDendrogramVerticalView";

	/**
	 * Constructor.
	 */
	public GLDendrogramVerticalView() {
		super();
	}

	@Override
	public void createPartControl(Composite parent) {

		super.createPartControl(parent);
		createGLCanvas();
		createGLEventListener(ECommandType.CREATE_GL_DENDROGRAM_VERTICAL, glCanvas.getID(), true);
	}

	public static void createToolBarItems(int iViewID) {
		alToolbar = new ArrayList<IAction>();
	}
}