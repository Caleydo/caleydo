package org.caleydo.rcp.command.handler.view;

import org.caleydo.core.view.opengl.canvas.glyph.gridview.GLGlyph;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

public class OpenGlyphViewHandler
	extends AbstractHandler
	implements IHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().showView(GLGlyph.VIEW_ID);
		}
		catch (PartInitException e) {
			e.printStackTrace();
		}

		return null;
	}

}
