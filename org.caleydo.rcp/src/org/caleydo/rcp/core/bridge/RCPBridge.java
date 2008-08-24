package org.caleydo.rcp.core.bridge;

import org.caleydo.core.bridge.gui.IGUIBridge;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.remote.GLRemoteRendering;
import org.caleydo.rcp.command.handler.ExitHandler;
import org.caleydo.rcp.views.GLRemoteRenderingView;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.handlers.HandlerUtil;


public class RCPBridge
implements IGUIBridge
{
	@Override
	public void closeApplication()
	{
		try
		{
			new ExitHandler().execute(null);
		}
		catch (ExecutionException e)
		{
			throw new CaleydoRuntimeException("Cannot execute exit command.", 
					CaleydoRuntimeExceptionType.GUI_RCP);
		}
	}
	
	@Override
	public void setActiveGLSubView(AGLEventListener parentGLEventListener,
			AGLEventListener subGLEventListener)
	{		
		if (parentGLEventListener instanceof GLRemoteRendering)
		{
			GLRemoteRenderingView remoteRenderingView = (GLRemoteRenderingView) 
				PlatformUI.getWorkbench().getWorkbenchWindows()[0].getActivePage().getActivePart();
			
			final IToolBarManager toolBarManager = remoteRenderingView.getViewSite().getActionBars().getToolBarManager();
			toolBarManager.add(ActionFactory.QUIT.create(PlatformUI.getWorkbench().getWorkbenchWindows()[0]));
			
			remoteRenderingView.getSWTComposite().getDisplay().asyncExec(new Runnable()
			{
				public void run()
				{
					toolBarManager.update(true);
				}
			});
		}
	}
}
