package org.caleydo.core.io.gui;

import org.caleydo.core.gui.toolbar.action.SaveProjectAction;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;

public class SaveProjectHandler
	extends AbstractHandler
	implements IHandler {
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		new SaveProjectAction().run();

		return null;
	}
}
