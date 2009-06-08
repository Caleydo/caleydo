package org.caleydo.rcp.view.opengl;

import org.caleydo.core.command.ECommandType;
import org.eclipse.swt.widgets.Composite;

public class ClinicalGLParCoordsView
	extends AGLViewPart {
	public static final String ID = "org.caleydo.rcp.views.opengl.ClinicalGLParCoordsView";

	/**
	 * Constructor.
	 */
	public ClinicalGLParCoordsView() {
		super();
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		createGLCanvas();
		createGLEventListener(ECommandType.CREATE_GL_PARALLEL_COORDINATES, glCanvas.getID(), true);
	}

}