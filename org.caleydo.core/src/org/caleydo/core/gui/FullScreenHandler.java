package org.caleydo.core.gui;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

public class FullScreenHandler
	extends AbstractHandler
	implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// Toggle full screen status
		Shell shell = HandlerUtil.getActiveShell(event).getShell();
		shell.setFullScreen(!shell.getFullScreen());
		return null;
	}
}
