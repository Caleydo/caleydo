package org.caleydo.rcp.views;

import java.awt.Frame;
import java.util.ArrayList;

import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
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
	protected int iViewID;

	protected static ArrayList<IAction> alToolbar; 
	
	/**
	 * Constructor.
	 */
	public AGLViewPart()
	{
		super();
	}

	public void setGLCanvas(final GLCaleydoCanvas glCanvas)
	{
		this.glCanvas = glCanvas;
	}

	public void setViewId(final int iViewID)
	{
		this.iViewID = iViewID;
	}

	@Override
	public void createPartControl(Composite parent)
	{
		swtComposite = new Composite(parent, SWT.EMBEDDED);
		
		fillToolBar();
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
}
