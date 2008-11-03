package org.caleydo.rcp.views;

import java.util.ArrayList;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.util.preferences.PreferenceConstants;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.GLGlyph;
import org.caleydo.rcp.action.view.glyph.ChangeViewModeToCircleAction;
import org.caleydo.rcp.action.view.glyph.ChangeViewModeToRandomAction;
import org.caleydo.rcp.action.view.glyph.ChangeViewModeToRectangleAction;
import org.caleydo.rcp.action.view.glyph.ChangeViewModeToScatterplotAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;

public class GLGlyphView
	extends AGLViewPart
{
	public static final String ID = "org.caleydo.rcp.views.GLGlyphView";

	/**
	 * Constructor.
	 */
	public GLGlyphView()
	{
		super();
	}

	@Override
	public void createPartControl(Composite parent)
	{
		super.createPartControl(parent);
	}

	public static void createToolBarItems(int iViewID)
	{
		GLGlyph glyphview = (GLGlyph) GeneralManager.get().getViewGLCanvasManager()
				.getGLEventListener(iViewID);

		alToolbar = new ArrayList<IAction>();

		// all pc views
		IAction changeViewModeToRectangleAction = new ChangeViewModeToRectangleAction(iViewID);
		alToolbar.add(changeViewModeToRectangleAction);
		IAction changeViewModeToCircleAction = new ChangeViewModeToCircleAction(iViewID);
		alToolbar.add(changeViewModeToCircleAction);
		IAction changeViewModeToRandomAction = new ChangeViewModeToRandomAction(iViewID);
		alToolbar.add(changeViewModeToRandomAction);
		IAction changeViewModeToScatterplotAction = new ChangeViewModeToScatterplotAction(
				iViewID);
		alToolbar.add(changeViewModeToScatterplotAction);

		// only if standalone or explicitly requested
		if (glyphview.isRenderedRemote()
				&& GeneralManager.get().getPreferenceStore().getBoolean(
						PreferenceConstants.PC_LIMIT_REMOTE_TO_CONTEXT))
			return;

	}

	@Override
	protected final void fillToolBar()
	{

		createGLCanvas();
		createGLEventListener(ECommandType.CREATE_GL_GLYPH, glCanvas.getID(), true);

		createToolBarItems(iGLEventListenerID);

		IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
		fillToolBar(toolBarManager);
	}
}