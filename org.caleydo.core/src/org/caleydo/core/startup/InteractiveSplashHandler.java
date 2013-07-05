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

		setProgressRect(new Rectangle(20, 200, splash.getSize().x-40, 25));
		setMessageRect(new Rectangle(20, 230,splash.getSize().x-40, 25));
		setForeground(new RGB(255, 255, 255));

		Label idLabel = new Label(getContent(), SWT.LEFT);
		FontData fd = new FontData("Arial", 9, SWT.NORMAL);

		final Font newFont = new Font(splash.getDisplay(), fd);
		idLabel.setFont(newFont);

		getContent().setFont(newFont);


		idLabel.setForeground(getForeground());
		idLabel.setBounds(new Rectangle(20, 180, 120, 18));
		idLabel.setText("Version " + GeneralManager.VERSION);


		// publish the progress monitor
		IProgressMonitor monitor = this.getBundleProgressMonitor();
		monitor.beginTask("Loading Caleydo...", 100);

		GeneralManager.get().setSplashProgressMonitor(monitor);

		Application.get().runStartup();
	}

}