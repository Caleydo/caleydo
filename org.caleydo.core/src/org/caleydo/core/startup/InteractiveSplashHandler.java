/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.startup;

import org.caleydo.core.internal.Application;
import org.caleydo.core.manager.GeneralManager;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
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

		setProgressRect(new Rectangle(55, 351, 537, 20));
		setMessageRect(new Rectangle(55, 328, 537, 20));
		setForeground(new RGB(255, 255, 255));

		Label idLabel = new Label(getContent(), SWT.RIGHT);
		idLabel.setFont(new Font(splash.getDisplay(), new FontData("Arial", 10, SWT.NORMAL)));

		idLabel.setForeground(getForeground());
		idLabel.setBounds(new Rectangle(55, 378, 537, 20));
		idLabel.setText("Caleydo Version " + GeneralManager.VERSION);

		getContent().setFont(new Font(splash.getDisplay(), new FontData("Arial", 12, SWT.NORMAL)));

		// publish the progress monitor
		IProgressMonitor monitor = this.getBundleProgressMonitor();
		monitor.beginTask("Loading Caleydo...", 100);

		GeneralManager.get().setSplashProgressMonitor(monitor);

		Application.get().runStartup();
	}

}