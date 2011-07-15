package org.caleydo.core.startup;

import java.util.List;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.data.CmdDataCreateDataDomain;
import org.caleydo.core.io.gui.LoadDataDialog;
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

		CmdDataCreateDataDomain cmd = new CmdDataCreateDataDomain(ECommandType.CREATE_DATA_DOMAIN);
		cmd.setAttributes("org.caleydo.datadomain.generic");
		cmd.doCommand();
		dataDomain = cmd.getCreatedObject();

		super.init(appInitData);
	}

	@Override
	public void execute() {
		super.execute();

		new LoadDataDialog(StartupProcessor.get().getDisplay().getActiveShell(), dataDomain);
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
