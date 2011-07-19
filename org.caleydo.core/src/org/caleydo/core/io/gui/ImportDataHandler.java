package org.caleydo.core.io.gui;

import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.swt.widgets.Shell;

public class ImportDataHandler
	extends AbstractHandler
	implements IHandler {
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		new ImportDataDialog(new Shell(), DataDomainManager.get().getDataDomainByType("org.caleydo.datadomain.genetic")).open();
		return null;
	}
}
