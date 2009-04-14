package org.caleydo.rcp.views.opengl;

import java.util.ArrayList;

import org.caleydo.core.command.ECommandType;
import org.caleydo.rcp.Application;
import org.caleydo.rcp.EApplicationMode;
import org.caleydo.rcp.action.toolbar.view.storagebased.ActivateGroupHandling;
import org.caleydo.rcp.action.toolbar.view.storagebased.ClearSelectionsAction;
import org.caleydo.rcp.action.toolbar.view.storagebased.MergeClasses;
import org.caleydo.rcp.action.toolbar.view.storagebased.StartClusteringAction;
import org.eclipse.jface.action.IAction;
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

		if (Application.applicationMode == EApplicationMode.PATHWAY_VIEWER) {
			MessageBox alert = new MessageBox(new Shell(), SWT.OK);
			alert.setMessage("Cannot create heat map in pathway viewer mode!");
			alert.open();

			dispose();
			return;
		}

		createGLCanvas();
		createGLEventListener(ECommandType.CREATE_GL_TEXTURE_HEAT_MAP_3D, glCanvas.getID(), true);
	}

}