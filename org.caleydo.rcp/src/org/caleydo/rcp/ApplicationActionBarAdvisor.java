package org.caleydo.rcp;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

public class ApplicationActionBarAdvisor
	extends ActionBarAdvisor {

	public static IStatusLineManager statusLineManager;

	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
		statusLineManager = configurer.getStatusLineManager();
	}
}
