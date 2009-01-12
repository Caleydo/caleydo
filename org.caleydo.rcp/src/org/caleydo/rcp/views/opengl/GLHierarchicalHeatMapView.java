package org.caleydo.rcp.views.opengl;

import java.util.ArrayList;

import org.caleydo.core.command.ECommandType;
import org.caleydo.rcp.Application;
import org.caleydo.rcp.EApplicationMode;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class GLHierarchicalHeatMapView
	extends AGLViewPart
{
	public static final String ID = "org.caleydo.rcp.views.GLHierarchicalHeatMapView";

	/**
	 * Constructor.
	 */
	public GLHierarchicalHeatMapView()
	{
		super();
	}

	@Override
	public void createPartControl(Composite parent)
	{
		super.createPartControl(parent);
		
		if (Application.applicationMode == EApplicationMode.PATHWAY_VIEWER)
		{
			MessageBox alert = new MessageBox(new Shell(), SWT.OK);
			alert.setMessage("Cannot create heat map in pathway viewer mode!");
			alert.open();

			dispose();
			return;
		}
		
		createGLCanvas();
		createGLEventListener(ECommandType.CREATE_GL_TEXTURE_HEAT_MAP_3D, glCanvas.getID(), true);
	}

	public static void createToolBarItems(int iViewID)
	{
//		GLHierarchicalHeatMap hierarchicalHeatMap = (GLHierarchicalHeatMap) GeneralManager
//				.get().getViewGLCanvasManager().getGLEventListener(iViewID);

		alToolbar = new ArrayList<IAction>();

		// TODO: insert icons + action
		// IAction switchFocus = new InFocusAction(iViewID);
		// alToolbar.add(switchFocus);

		// IAction switchOrientation = new ChangeOrientationAction(iViewID);
		// alToolbar.add(switchOrientation);
	}
}