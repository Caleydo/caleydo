package org.caleydo.rcp.startup;

import java.util.List;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.data.CmdDataCreateDataDomain;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.specialized.Organism;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.preferences.PreferenceConstants;
import org.caleydo.rcp.wizard.project.DataImportWizard;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * Startup procedure for project wizard.
 * 
 * @author Alexander Lex
 */
public class GenericGUIStartupProcedure
	extends AStartupProcedure {





	@Override
	public void init(ApplicationInitData appInitData) {
			
		// FIXME this needs to be done after the wizard is closed, and dynamically
		CmdDataCreateDataDomain cmd = new CmdDataCreateDataDomain(ECommandType.CREATE_DATA_DOMAIN);
		cmd.setAttributes("org.caleydo.datadomain.generic");
		cmd.doCommand();

		super.init(appInitData);
	}

	@Override
	public void execute() {
		super.execute();

		Shell shell = StartupProcessor.get().getDisplay().getActiveShell();

		WizardDialog dataImportWizard;

	
	
			dataImportWizard = new WizardDialog(shell, new DataImportWizard(shell));

			if (Window.CANCEL == dataImportWizard.open()) {
				StartupProcessor.get().shutdown();
			}
		
	}



	@Override
	public void addDefaultStartViews() {

		List<Pair<String, String>> startViewWithDataDomain =
			appInitData.getAppArgumentStartViewWithDataDomain();

		// Do not add any default views if at least one view is specified as application argument
		if (!startViewWithDataDomain.isEmpty())
			return;

		startViewWithDataDomain.add(new Pair<String, String>("org.caleydo.view.browser",
			"org.caleydo.datadomain.generic"));
		
		startViewWithDataDomain.add(new Pair<String, String>("org.caleydo.view.parcoords",
			"org.caleydo.datadomain.generic"));

	
	}
}
