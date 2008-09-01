package org.caleydo.rcp.command.handler.view;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.view.opengl.CmdCreateGLEventListener;
import org.caleydo.core.command.view.opengl.CmdGlObjectPathway3D;
import org.caleydo.core.data.view.camera.EProjectionMode;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.pathway.GLPathway;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

public class OpenHeatMapHandler
	extends AbstractHandler
	implements IHandler
{

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		try
		{
			HandlerUtil.getActiveWorkbenchWindow(event)
				.getActivePage().showView("org.caleydo.rcp.views.GLHeatMapView");
		}
		catch (PartInitException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

}
