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
import org.caleydo.rcp.action.view.glyph.ClearSelectionsAction;
import org.caleydo.rcp.action.view.glyph.RemoveUnselectedFromViewAction;
import org.caleydo.rcp.util.glyph.GlyphBar;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.jface.action.IContributionItem;

public class GLGlyphView
	extends AGLViewPart
{
	public static final String ID = "org.caleydo.rcp.views.GLGlyphView";
	public static GlyphBar glyphbar;

	public static int viewCount = 0;

	/**
	 * Constructor.
	 */
	public GLGlyphView()
	{
		super();
		viewCount++;
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
		alToolbarContributions = new ArrayList<IContributionItem>();

		// all pc views
		// IAction changeViewModeToRectangleAction = new
		// ChangeViewModeToRectangleAction(iViewID);
		// alToolbar.add(changeViewModeToRectangleAction);
		// IAction changeViewModeToCircleAction = new
		// ChangeViewModeToCircleAction(iViewID);
		// alToolbar.add(changeViewModeToCircleAction);
		// IAction changeViewModeToRandomAction = new
		// ChangeViewModeToRandomAction(iViewID);
		// alToolbar.add(changeViewModeToRandomAction);
		// IAction changeViewModeToScatterplotAction = new
		// ChangeViewModeToScatterplotAction(
		// iViewID);
		// alToolbar.add(changeViewModeToScatterplotAction);
		alToolbar.add(new ClearSelectionsAction(iViewID));
		alToolbar.add(new RemoveUnselectedFromViewAction(iViewID));

		glyphbar = new GlyphBar("Glyph ToolBar");
		glyphbar.setViewID(iViewID);

		alToolbarContributions.add(glyphbar);

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

		alToolbar = null;
		alToolbarContributions = null;
	}

}
