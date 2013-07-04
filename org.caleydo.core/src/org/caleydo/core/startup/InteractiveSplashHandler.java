/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.startup;

import org.caleydo.core.manager.GeneralManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.splash.BasicSplashHandler;

/**
 * Caleydo splash screen with integrated progress bar.
 *
 * @author Marc Streit
 */
public class InteractiveSplashHandler extends BasicSplashHandler {

	@Override
	public void init(Shell splash) {
		super.init(splash);

		setProgressRect(new Rectangle(20, 200, splash.getSize().x-40, 25));
		setMessageRect(new Rectangle(20, 230,splash.getSize().x-40, 25));
		setForeground(new RGB(255, 255, 255));

		Label idLabel = new Label(getContent(), SWT.LEFT);
		idLabel.setForeground(getForeground());
		idLabel.setBounds(new Rectangle(splash.getSize().x-80, 185, 80, 18));
		idLabel.setText("Version " + GeneralManager.VERSION);


		// public the progress monitor
		GeneralManager.get().setSplashProgressMonitor(this.getBundleProgressMonitor());
	}
}