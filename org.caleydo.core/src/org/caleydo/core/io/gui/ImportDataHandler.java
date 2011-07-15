package org.caleydo.core.io.gui;

import java.util.Collection;

import org.caleydo.core.gui.dialog.ChooseDataDomainDialog;
import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.datadomain.IDataDomain;
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
		
		Collection<IDataDomain> possibleDataDomains = DataDomainManager.get().getDataDomains();
		IDataDomain chosenDataDomain = null;
		
		if (possibleDataDomains.size() == 1)
			chosenDataDomain = (IDataDomain)possibleDataDomains.toArray()[0];
		else {
			ChooseDataDomainDialog chooseDataDomainDialog = new ChooseDataDomainDialog(new Shell());
			chooseDataDomainDialog.setPossibleDataDomains(possibleDataDomains);
			chosenDataDomain = chooseDataDomainDialog.open();			
		}

		new ImportDataDialog(new Shell(), chosenDataDomain).open();
		return null;
	}
}
