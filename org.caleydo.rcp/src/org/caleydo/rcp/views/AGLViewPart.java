package org.caleydo.rcp.views;

import java.awt.Frame;
import java.util.ArrayList;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.view.opengl.CmdCreateGLEventListener;
import org.caleydo.core.command.view.rcp.CmdViewCreateRcpGLCanvas;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.view.camera.EProjectionMode;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IEventPublisher.MediatorType;
import org.caleydo.core.manager.event.mediator.MediatorUpdateType;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.remote.AGLConnectionLineRenderer;
import org.caleydo.core.view.opengl.canvas.remote.GLRemoteRendering;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * Shared object for all Caleydo RCP OpenGL views.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public abstract class AGLViewPart
	extends ViewPart
{
	protected Frame frameGL;
	protected Composite swtComposite;
	protected GLCaleydoCanvas glCanvas;
	protected int iGLEventListenerID;

	protected static ArrayList<IAction> alToolbar; 
	
	/**
	 * Constructor.
	 */
	public AGLViewPart()
	{
		super();
	}

	/**
	 * This class creates the GL content (canvas + event listener)
	 * for a RCP view. 
	 * 
	 * @param glViewType
	 */
	protected void createStandaloneGLParts(ECommandType glViewType)
	{
		IGeneralManager generalManager = GeneralManager.get();
		
		CmdViewCreateRcpGLCanvas cmdCanvas = (CmdViewCreateRcpGLCanvas) generalManager
			.getCommandManager().createCommandByType(ECommandType.CREATE_VIEW_RCP_GLCANVAS);
		cmdCanvas.doCommand();
		
		ArrayList<Integer> iAlSets = new ArrayList<Integer>();
		for (ISet set : generalManager.getSetManager().getAllItems())
		{
			iAlSets.add(set.getID());
		}
		
		CmdCreateGLEventListener cmdView = (CmdCreateGLEventListener) generalManager
			.getCommandManager().createCommandByType(glViewType);
		cmdView.setAttributes(EProjectionMode.ORTHOGRAPHIC, 0, 8, 0, 8, -20, 20, 
				iAlSets, cmdCanvas.getCreatedObject().getID());
		cmdView.doCommand();
		
		int iViewID = cmdView.getCreatedObject().getID();
		
		GLCaleydoCanvas glCanvas = cmdCanvas.getCreatedObject();
		setGLData(glCanvas, iViewID);
		createPartControlGL();

		// Add created view to bucket mediator
		// FIXME: this approach is not general - think about better one
		ArrayList<Integer> iAlMediatorIDs = new ArrayList<Integer>();	
		iAlMediatorIDs.add(iViewID);	
		for (AGLEventListener glEventListener : generalManager.getViewGLCanvasManager().getAllGLEventListeners())
		{
			if (glEventListener instanceof GLRemoteRendering)
			{	
				generalManager.getEventPublisher().addSendersAndReceiversToMediator(
						generalManager.getEventPublisher().getItem(((GLRemoteRendering)glEventListener).getMediatorID()),
							iAlMediatorIDs, iAlMediatorIDs, MediatorType.SELECTION_MEDIATOR,
							MediatorUpdateType.MEDIATOR_DEFAULT);
				
				return;
			}
		}
	}
	
	@Override
	public void createPartControl(Composite parent)
	{
		swtComposite = new Composite(parent, SWT.EMBEDDED);		
		fillToolBar();
	}

	
	public void setGLData(final GLCaleydoCanvas glCanvas,
			final int iGLEventListenerID)
	{
		this.glCanvas = glCanvas;
		this.iGLEventListenerID = iGLEventListenerID;
	}

	public void createPartControlGL()
	{
		if (frameGL == null)
		{
			frameGL = SWT_AWT.new_Frame(swtComposite);
		}

		frameGL.add(glCanvas);
	}
	
	@Override
	public void setFocus()
	{
//		final IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
//		toolBarManager.add(ActionFactory.QUIT.create(this.getViewSite().getWorkbenchWindow()));
//		toolBarManager.update(true);
	}
	
	public Composite getSWTComposite()
	{
		return swtComposite;
	}
	
	protected abstract void fillToolBar();

	/**
	 * Method fills the toolbar in a given toolbar manager.
	 * Used in case of remote rendering.
	 * 
	 * @param toolBarManager
	 */
	public static void fillToolBar(final IToolBarManager toolBarManager)
	{
		for (IAction toolBarAction : alToolbar)
		{
			toolBarManager.add(toolBarAction);			
		}
	}
	
	@Override
	public void dispose()
	{
		super.dispose();

		GeneralManager.get().getViewGLCanvasManager().unregisterGLCanvas(glCanvas.getID());
		GeneralManager.get().getViewGLCanvasManager().unregisterGLEventListener(iGLEventListenerID);
	}
}
