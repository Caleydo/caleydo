package org.caleydo.rcp.command.handler;

import java.util.Collection;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.manager.general.GeneralManager;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;

public class ExportHandler
	extends AbstractHandler
	implements IHandler
{
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		Collection<ISet> colSets =  GeneralManager.get().getSetManager().getAllItems();
		
		for(ISet set : colSets)
		{
			set.export();
		}
		return null;
	}
}
