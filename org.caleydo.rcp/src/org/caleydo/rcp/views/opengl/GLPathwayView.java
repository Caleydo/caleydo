package org.caleydo.rcp.views.opengl;

import java.util.ArrayList;

import org.caleydo.rcp.action.view.pathway.GeneMappingAction;
import org.caleydo.rcp.action.view.pathway.NeighborhoodAction;
import org.caleydo.rcp.action.view.pathway.TextureAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;

public class GLPathwayView
	extends AGLViewPart
{
	public static final String ID = "org.caleydo.rcp.views.GLPathwayView";

	/**
	 * Constructor.
	 */
	public GLPathwayView()
	{
		super();
	}

	public static void createToolBarItems(int iViewID)
	{
		alToolbar = new ArrayList<IAction>();

		IAction textureAction = new TextureAction(iViewID);
		alToolbar.add(textureAction);
		IAction neighborhoodAction = new NeighborhoodAction(iViewID);
		alToolbar.add(neighborhoodAction);
		IAction geneMappingAction = new GeneMappingAction(iViewID);
		alToolbar.add(geneMappingAction);
	}

	@Override
	protected final void fillToolBar()
	{
		if (alToolbar == null)
		{
			createToolBarItems(iGLEventListenerID);
		}

		IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
		fillToolBar(toolBarManager);
	}
}