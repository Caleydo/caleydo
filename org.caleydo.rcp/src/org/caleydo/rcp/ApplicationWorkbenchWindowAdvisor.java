package org.caleydo.rcp;

import org.caleydo.core.manager.general.GeneralManager;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchWindowAdvisor
	extends WorkbenchWindowAdvisor
{

	/**
	 * Constructor.
	 */
	public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer)
	{
		super(configurer);
	}

	@Override
	public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer)
	{

		return new ApplicationActionBarAdvisor(configurer);
	}

	@Override
	public void preWindowOpen()
	{
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		// configurer.setInitialSize(new Point(400, 300));
		configurer.setShowCoolBar(false);
		configurer.setShowStatusLine(false);
		// configurer.setShowProgressIndicator(true);
		configurer.setTitle("Caleydo");
	}

	@Override
	public void postWindowCreate()
	{
		super.postWindowCreate();

		getWindowConfigurer().getWindow().getShell().setMaximized(true);

		// Set status line in caleydo core
		GeneralManager.get().getSWTGUIManager().setExternalRCPStatusLine(
				getWindowConfigurer().getActionBarConfigurer().getStatusLineManager(),
				getWindowConfigurer().getWindow().getShell().getDisplay());

		if (Application.bIsWebstart && !Application.bDoExit)
		{
			Application.startCaleydoCore();
		}
	}
}
