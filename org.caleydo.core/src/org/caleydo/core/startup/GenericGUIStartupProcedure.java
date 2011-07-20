package org.caleydo.core.startup;

import java.util.List;

import org.caleydo.core.io.gui.ImportDataDialog;
import org.caleydo.core.manager.datadomain.ATableBasedDataDomain;
import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.util.collection.Pair;

/**
 * Startup procedure for project wizard.
 * 
 * @author Alexander Lex
 */
public class GenericGUIStartupProcedure
	extends AStartupProcedure {

	@Override
	public void init(ApplicationInitData appInitData) {

		this.dataDomain =
			(ATableBasedDataDomain) DataDomainManager.get()
				.createDataDomain("org.caleydo.datadomain.generic");

		super.init(appInitData);
	}

	@Override
	public void execute() {
		super.execute();

		new ImportDataDialog(StartupProcessor.get().getDisplay().getActiveShell(), dataDomain);
	}

	@Override
	public void addDefaultStartViews() {

		List<Pair<String, String>> startViewWithDataDomain =
			appInitData.getAppArgumentStartViewWithDataDomain();

		// Do not add any default views if at least one view is specified as application argument
		if (!startViewWithDataDomain.isEmpty())
			return;

		startViewWithDataDomain.add(new Pair<String, String>("org.caleydo.view.browser",
			dataDomain.getDataDomainType()));

		startViewWithDataDomain.add(new Pair<String, String>("org.caleydo.view.parcoords",
			dataDomain.getDataDomainType()));

	}
}
