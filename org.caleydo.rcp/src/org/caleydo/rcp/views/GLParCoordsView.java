package org.caleydo.rcp.views;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;

public class GLParCoordsView
	extends AGLViewPart
{

	public static final String ID = "org.caleydo.rcp.views.GLParCoordsView";

	protected int iGLCanvasDirectorId;

	/**
	 * Constructor.
	 */
	public GLParCoordsView()
	{

		super();
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent)
	{

		super.createPartControlSWT(parent);

		contributeToActionBars();
	}

	protected void contributeToActionBars()
	{
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	protected void fillLocalPullDown(IMenuManager manager)
	{

		manager.add(new Separator());
	}

	protected void fillLocalToolBar(IToolBarManager manager)
	{

	}
}