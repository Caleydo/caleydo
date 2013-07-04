/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.startup;

import org.caleydo.core.manager.GeneralManager;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.internal.splash.EclipseSplashHandler;

/**
 * Caleydo splash screen with integrated progress bar.
 *
 * @author Marc Streit
 */
@SuppressWarnings("restriction")
public class InteractiveSplashHandler extends EclipseSplashHandler {

	@Override
	public void init(Shell splash) {
		super.init(splash);

		GeneralManager.get().setSplashProgressMonitor(this.getBundleProgressMonitor());
	}
}