package org.caleydo.rcp.command.handler;

import java.util.Collection;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.rcp.dialog.file.ExportDialog;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.swt.widgets.Shell;

public class ExportHandler
	extends AbstractHandler
	implements IHandler
{
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		ExportDialog dialog = new ExportDialog(new Shell());
		dialog.open();
		
//		Collection<ISet> colSets = GeneralManager.get().getSetManager().getAllItems();
//
//		for (ISet set : colSets)
//		{
//			set.export();
//		}
		return null;
	}
}
