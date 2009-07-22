package org.caleydo.rcp.view.opengl;

import org.caleydo.core.command.ECommandType;
import org.caleydo.rcp.Application;
import org.caleydo.rcp.EApplicationMode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class GLHierarchicalHeatMapView
	extends AGLViewPart {
	public static final String ID = "org.caleydo.rcp.views.opengl.GLHierarchicalHeatMapView";

	/**
	 * Constructor.
	 */
	public GLHierarchicalHeatMapView() {
		super();
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		if (Application.applicationMode == EApplicationMode.GENE_EXPRESSION_PATHWAY_VIEWER) {
			MessageBox alert = new MessageBox(new Shell(), SWT.OK);
			alert.setMessage("Cannot create heat map in pathway viewer mode!");
			alert.open();

			dispose();
			return;
		}

		createGLCanvas();
		createGLEventListener(ECommandType.CREATE_GL_TEXTURE_HEAT_MAP_3D, glCanvas.getID(), true);

		glEventListener.setViewGUIID(ID);
	}

}