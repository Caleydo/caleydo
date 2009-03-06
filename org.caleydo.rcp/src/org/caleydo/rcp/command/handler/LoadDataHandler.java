package org.caleydo.rcp.command.handler;

import org.caleydo.rcp.dialog.file.LoadDataDialog;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.swt.widgets.Shell;

public class LoadDataHandler
	extends AbstractHandler
	implements IHandler
{
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		Shell shell = new Shell();
//		shell.setMaximized(true);
		LoadDataDialog dialog = new LoadDataDialog(shell);
		dialog.open();
		
		return null;
	}
}
