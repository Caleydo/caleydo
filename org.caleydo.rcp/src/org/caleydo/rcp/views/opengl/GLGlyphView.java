package org.caleydo.rcp.views.opengl;

import java.util.ArrayList;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.util.preferences.PreferenceConstants;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.GLGlyph;
import org.caleydo.rcp.action.view.glyph.ClearSelectionsAction;
import org.caleydo.rcp.action.view.glyph.EnterViewNameAction;
import org.caleydo.rcp.action.view.glyph.OpenDataExportAction;
import org.caleydo.rcp.action.view.glyph.RemoveUnselectedFromViewAction;
import org.caleydo.rcp.util.glyph.GlyphBar;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.swt.widgets.Composite;

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
		
		createGLCanvas();
		createGLEventListener(ECommandType.CREATE_GL_GLYPH, glCanvas.getID(), true);
	}

	public static void createToolBarItems(int iViewID)
	{
		GLGlyph glyphview = (GLGlyph) GeneralManager.get().getViewGLCanvasManager()
				.getGLEventListener(iViewID);

		alToolbar = new ArrayList<IAction>();
		alToolbarContributions = new ArrayList<IContributionItem>();

		alToolbar.add(new ClearSelectionsAction(iViewID));
		alToolbar.add(new RemoveUnselectedFromViewAction(iViewID));

		alToolbar.add(new EnterViewNameAction(iViewID));

		alToolbar.add(new OpenDataExportAction(iViewID));

		glyphbar = new GlyphBar("Glyph ToolBar");
		glyphbar.setViewID(iViewID);

		alToolbarContributions.add(glyphbar);

		// only if standalone or explicitly requested
		if (glyphview.isRenderedRemote()
				&& GeneralManager.get().getPreferenceStore().getBoolean(
						PreferenceConstants.PC_LIMIT_REMOTE_TO_CONTEXT))
			return;

	}
}
