package org.caleydo.rcp.views;

import java.awt.Frame;
import java.util.ArrayList;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.view.opengl.CmdCreateGLEventListener;
import org.caleydo.core.command.view.rcp.CmdViewCreateRcpGLCanvas;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.event.mediator.EMediatorType;
import org.caleydo.core.manager.event.mediator.IMediatorReceiver;
import org.caleydo.core.manager.event.mediator.IMediatorSender;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.util.preferences.PreferenceConstants;
import org.caleydo.core.view.opengl.camera.EProjectionMode;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
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
	protected static ArrayList<IContributionItem> alToolbarContributions;

	/**
	 * Constructor.
	 */
	public AGLViewPart()
	{
		super();
	}

	protected void createGLCanvas()
	{
		CmdViewCreateRcpGLCanvas cmdCanvas = (CmdViewCreateRcpGLCanvas) GeneralManager.get()
				.getCommandManager()
				.createCommandByType(ECommandType.CREATE_VIEW_RCP_GLCANVAS);
		cmdCanvas.setAttributes(-1, false, false, false);
		cmdCanvas.doCommand();

		glCanvas = cmdCanvas.getCreatedObject();
		glCanvas.setParentComposite(swtComposite);
	}

	/**
	 * This class creates the GL event listener contained in a RCP view for a
	 * RCP view.
	 * 
	 * @param glViewType
	 *            The type of view. See {@link ECommandType}
	 * @param iParentCanvasID
	 *            the id of canvas where you want to render
	 * @param bRegisterToOverallMediator
	 *            true if you want this to listen and send to main mediator
	 */
	protected int createGLEventListener(ECommandType glViewType, int iParentCanvasID,
			boolean bRegisterToOverallMediator)
	{
		IGeneralManager generalManager = GeneralManager.get();

		ArrayList<Integer> iAlSets = new ArrayList<Integer>();
		for (ISet set : generalManager.getSetManager().getAllItems())
		{
			iAlSets.add(set.getID());
		}

		CmdCreateGLEventListener cmdView = (CmdCreateGLEventListener) generalManager
				.getCommandManager().createCommandByType(glViewType);

		if (glViewType == ECommandType.CREATE_GL_BUCKET_3D)
		{
			cmdView.setAttributes(EProjectionMode.PERSPECTIVE, -1.5f, 1.5f, -1.5f, 1.5f, 2.9f,
					100, iAlSets, iParentCanvasID, 0, 0, -8, 0, 0, 0, 0);
		}
		else if (glViewType == ECommandType.CREATE_GL_GLYPH)
		{
			cmdView.setAttributes(EProjectionMode.PERSPECTIVE, -1f, 1f, -1f, 1f, 2.9f, 100,
					iAlSets, iParentCanvasID, 0, 0, -8, 0, 0, 0, 0);
		}
		else
		{
			cmdView.setAttributes(EProjectionMode.ORTHOGRAPHIC, 0, 8, 0, 8, -20, 20, iAlSets,
					iParentCanvasID);
		}
		cmdView.doCommand();

		AGLEventListener glView = cmdView.getCreatedObject();
		int iViewID = glView.getID();

		setGLData(glCanvas, iViewID);
		createPartControlGL();

//		generalManager.getEventPublisher().addSender(
//				EMediatorType.SELECTION_MEDIATOR, (IMediatorSender)glView);
//		generalManager.getEventPublisher().addReceiver(
//				EMediatorType.SELECTION_MEDIATOR, (IMediatorReceiver)glView);

		return iViewID;
	}


	@Override
	public void createPartControl(Composite parent)
	{
		swtComposite = new Composite(parent, SWT.EMBEDDED);
		fillToolBar();
	}

	public void setGLData(final GLCaleydoCanvas glCanvas, final int iGLEventListenerID)
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
		// final IToolBarManager toolBarManager =
		// getViewSite().getActionBars().getToolBarManager();
		// toolBarManager.add(ActionFactory.QUIT.create(this.getViewSite().getWorkbenchWindow()));
		// toolBarManager.update(true);
	}

	public Composite getSWTComposite()
	{
		return swtComposite;
	}

	protected abstract void fillToolBar();

	/**
	 * Method fills the toolbar in a given toolbar manager. Used in case of
	 * remote rendering.
	 * 
	 * @param toolBarManager
	 */
	public static void fillToolBar(final IToolBarManager toolBarManager)
	{
		// Add ControlContribution items
		if (!GeneralManager.get().getPreferenceStore().getBoolean(
				PreferenceConstants.XP_CLASSIC_STYLE_MODE))
		{
			if (alToolbarContributions != null)
				for (IContributionItem item : alToolbarContributions)
					toolBarManager.add(item);

			alToolbarContributions = null;
		}

		// add action items
		for (IAction toolBarAction : alToolbar)
		{
			toolBarManager.add(toolBarAction);
		}
		alToolbar = null;

	}

	@Override
	public void dispose()
	{
		super.dispose();

		GeneralManager.get().getViewGLCanvasManager().unregisterGLCanvas(glCanvas.getID());
		GeneralManager.get().getViewGLCanvasManager().unregisterGLEventListener(
				iGLEventListenerID);

		// TODO: unregister from all event listeners!!
	}
}
