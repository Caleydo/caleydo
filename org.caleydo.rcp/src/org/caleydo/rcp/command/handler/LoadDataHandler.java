package org.caleydo.rcp.command.handler;

import org.caleydo.rcp.dialog.file.FileLoadDataDialog;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.handlers.HandlerUtil;

public class LoadDataHandler
	extends AbstractHandler
	implements IHandler
{

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		FileLoadDataDialog loadDataFileDialog = new FileLoadDataDialog(
				HandlerUtil.getActiveShell(event));

		loadDataFileDialog.open();
		
		return null;
	}

}
