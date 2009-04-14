package org.caleydo.rcp.views.opengl;

import java.util.ArrayList;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.util.preferences.PreferenceConstants;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.GLGlyph;
import org.caleydo.rcp.action.toolbar.view.glyph.ChangeSelectionBrushAction;
import org.caleydo.rcp.action.toolbar.view.glyph.ChangeViewModeAction;
import org.caleydo.rcp.action.toolbar.view.glyph.ChangeViewModeSecondaryAction;
import org.caleydo.rcp.action.toolbar.view.glyph.ClearSelectionsAction;
import org.caleydo.rcp.action.toolbar.view.glyph.EnterViewNameAction;
import org.caleydo.rcp.action.toolbar.view.glyph.OpenDataExportAction;
import org.caleydo.rcp.action.toolbar.view.glyph.OpenNewWindowAction;
import org.caleydo.rcp.action.toolbar.view.glyph.RemoveUnselectedFromViewAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.swt.widgets.Composite;

public class GLGlyphView
	extends AGLViewPart {
	public static final String ID = "org.caleydo.rcp.views.opengl.GLGlyphView";

	public static int viewCount = 0;

	/**
	 * Constructor.
	 */
	public GLGlyphView() {
		super();
		viewCount++;
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		createGLCanvas();
		createGLEventListener(ECommandType.CREATE_GL_GLYPH, glCanvas.getID(), true);
	}
}
