package org.caleydo.core.startup;

import java.util.List;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.io.gui.ImportDataDialog;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.system.FileOperations;
import org.eclipse.jface.window.Window;

/**
 * Startup procedure for project wizard.
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public class GenericGUIStartupProcedure
	extends AStartupProcedure {

	public GenericGUIStartupProcedure() {
		// Delete old workbench state
		FileOperations.deleteDirectory(GeneralManager.CALEYDO_HOME_PATH + ".metadata");
	}

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

		ImportDataDialog dialog =
			new ImportDataDialog(StartupProcessor.get().getDisplay().getActiveShell(), dataDomain);

		if (Window.CANCEL == dialog.open())
			StartupProcessor.get().shutdown();
	}

	@Override
	public void addDefaultStartViews() {

		List<Pair<String, String>> startViewWithDataDomain =
			appInitData.getAppArgumentStartViewWithDataDomain();

		// Do not add any default views if at least one view is specified as application argument
		if (!startViewWithDataDomain.isEmpty())
			return;

		startViewWithDataDomain.add(new Pair<String, String>("org.caleydo.view.browser", dataDomain
			.getDataDomainType()));

		startViewWithDataDomain.add(new Pair<String, String>("org.caleydo.view.parcoords", dataDomain
			.getDataDomainType()));

	}
}
